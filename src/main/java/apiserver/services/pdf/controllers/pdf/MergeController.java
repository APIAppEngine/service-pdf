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

import apiserver.MimeType;
import apiserver.jobs.IProxyJob;
import apiserver.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.CFPdfJob;
import apiserver.services.pdf.gateways.jobs.CFPdfMultipleFilesJob;
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
@Api(value = "/api/v1/pdf", description = "[PDF]")
@RequestMapping("/api/v1/pdf")
public class MergeController
{
    @Qualifier("mergePdfApiGateway")
    @Autowired
    public PdfGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * Merge two pdf files into one.
     * @param files
     * @param keepBookmark
     * @param password
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws IOException
     * @throws Exception
     */
    @ApiOperation(value = "Merge two PDF documents into an output PDF file.")
    @RequestMapping(value = "/merge", method = RequestMethod.POST)
    public ResponseEntity<byte[]> mergePdfDocuments(
            @ApiParam(name="file1", required = true, value = "multiple pdf files to merge into one.")
                @RequestParam(value = "file", required = true) MultipartFile file1,
            @ApiParam(name="file2", required = true, value = "multiple pdf files to merge into one.")
                @RequestParam(value = "file", required = true) MultipartFile file2,
            @ApiParam(name="keepBookmark", required = false, allowableValues = "yes,no", value="Specifies whether bookmarks from the source PDF documents are retained in the merged document")
                @RequestParam(value = "keepBookmark", required = false) Boolean keepBookmark,
            @ApiParam(name="password", required = false, value="Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeJob(file1, file2, keepBookmark, password);
    }


    private ResponseEntity<byte[]> executeJob(
            MultipartFile file1
            , MultipartFile file2
            , Boolean keepBookmark
            , String password
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfMultipleFilesJob job = new CFPdfMultipleFilesJob();
        job.setAction("merge");

        int idx = 0;
        Document[] documents = new Document[2];

        Document doc = new Document(file1);
        doc.setFileName(file1.getOriginalFilename());
        doc.setContentType(MimeType.getMimeType(file1.getOriginalFilename()));
        documents[idx++] = doc;

        Document doc2 = new Document(file2);
        doc2.setFileName(file2.getOriginalFilename());
        doc2.setContentType(MimeType.getMimeType(file2.getOriginalFilename()));
        documents[idx++] = doc2;

        job.setDocuments(documents);

        if( keepBookmark != null ) job.setKeepBookmark(keepBookmark);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.mergePdf(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }


}
