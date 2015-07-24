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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
public class ExtractTextController
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
     * Extract all the words in the PDF.
     * @param file
     * @param addquads
     * @param honourspaces
     * @param pages
     * @param password
     * @param type
     * @param useStructure
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws IOException
     * @throws Exception
     */
    @ApiOperation(value = "Extract all the words in the PDF.")
    @RequestMapping(value = "/extract/text", method = RequestMethod.POST)
    public ResponseEntity<Object> extractTextFromPdf(
            @ApiParam(name = "file", required = true)
                @RequestParam("file") MultipartFile file,
            @ApiParam(name = "addquads", required = false, defaultValue = "false", value = "add the position or quadrants for the text in the PDF")
                @RequestParam(value = "addquads", required = false) String addquads,
            @ApiParam(name = "honourspaces", required = false, defaultValue = "false", value = "Set this option to 'true', for improved readability and spacing.")
                @RequestParam(value = "honourspaces", required = false) Boolean honourspaces,
            @ApiParam(name = "pages", required = false, defaultValue = "*", value = "page numbers from where the text needs to be extracted from the PDF document")
                @RequestParam(value = "pages", required = false) String pages,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password,
            @ApiParam(name = "type", required = false, defaultValue = "xml", value = "string or xml format in which the text needs to be extracted")
                @RequestParam(value = "type", required = false, defaultValue = "xml") String type,
            @ApiParam(name = "usestructure", required = false, value = "Lets you extract content based on the PDF structure. For better readability of the extracted text, use this attribute together with the attribute honourspaces.")
                @RequestParam(value = "usestructure", required = false) Boolean useStructure
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeJob(file, addquads, honourspaces, pages, password, type, useStructure);
    }


    /**
     *
     * @param file
     * @param addquads
     * @param honourspaces
     * @param pages
     * @param password
     * @param type
     * @param useStructure
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    private ResponseEntity<Object> executeJob(
            MultipartFile file
            , String addquads
            , Boolean honourspaces
            , String pages
            , String password
            , String type
            , Boolean useStructure
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        //todo validate FILE, type

        CFPdfJob job = new CFPdfJob();
        job.setAction("extracttext");
        job.setDocument(new Document(file));
        job.setPages(pages==null?"*":pages);
        job.setType(type==null?"xml":type);
        if(addquads!=null) job.setAddQuads(addquads);
        if(honourspaces!=null) job.setHonourSpaces(honourspaces);
        if(password!=null) job.setPassword(password);
        if(useStructure!=null) job.setUseStructure(useStructure);


        Future future = textGateway.extractText(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }



}
