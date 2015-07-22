package apiserver.services.pdf.controllers.pdf;

/*******************************************************************************
 Copyright (c) 2013 Mike Nimer.

 This file is part of ApiServer Project.

 The ApiServer Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 The ApiServer Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the ApiServer Project.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import apiserver.jobs.IProxyJob;
import apiserver.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.CFPdfJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: mnimer
 * Date: 9/15/12
 */
@Controller
@RestController
@Api(value = "/api/pdf", description = "[PDF]")
@RequestMapping("/api/pdf")
public class ExtractImageController
{
    @Qualifier("extractPdfTextApiGateway")
    @Autowired
    public PdfGateway textGateway;

    @Qualifier("extractPdfImageApiGateway")
    @Autowired
    public PdfGateway imageGateway;

    private
    @Value("#{applicationProperties.defaultReplyTimeout}")
    Integer defaultTimeout;


    /**
     * Extract images in pdf
     * @param file
     * @param format
     * @param imagePrefix
     * @param pages
     * @param password
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws IOException
     * @throws Exception
     */
    @ApiOperation(value = "Extract images in pdf")
    @RequestMapping(value = "/extract/image", method = RequestMethod.POST)
    public ResponseEntity<Object> extractImageFromPdf(
            @ApiParam(name = "file", required = true)
                @RequestParam(value="file", required = true) MultipartFile file,
            @ApiParam(name = "format", required = false, value = "png|tiff|jpg - Format in which the images should be extracted")
                @RequestParam(value = "format", required = false, defaultValue = "jpg") String format,
            @ApiParam(name = "imagePrefix", required = false, value = "the string that you want to prefix with the image name")
                @RequestParam(value = "imagePrefix", required = false, defaultValue = "pdf-") String imagePrefix,
            @ApiParam(name = "pages", required = false, defaultValue = "*", value = "page numbers from where the text needs to be extracted from the PDF document")
                @RequestParam(value = "pages", required = false, defaultValue = "*") String pages,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password
        ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeJob(file, format, imagePrefix, pages, password);
    }


    /**
     *
     * @param file
     * @param format
     * @param imagePrefix
     * @param pages
     * @param password
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    private ResponseEntity<Object> executeJob(
            MultipartFile file
            , String format
            , String imagePrefix
            , String pages
            , String password
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfJob job = new CFPdfJob();
        job.setAction("extractimage");
        job.setDocument(new Document(file));
        job.setPages(pages==null?"*":pages);
        job.setFormat(format==null?"jpg":format);
        job.setImagePrefix(imagePrefix==null?"*":imagePrefix);
        if(password!=null) job.setPassword(password);


        Future<Map> future = imageGateway.extractImage(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }


}
