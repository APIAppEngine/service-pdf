package apiserver.services.pdf.controllers;

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
import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.services.ObjectJob;
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.ExtractImageJob;
import apiserver.services.pdf.gateways.jobs.ExtractTextJob;
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
import org.springframework.web.bind.annotation.RequestPart;
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
@Api(value = "/pdf", description = "[PDF]")
@RequestMapping("/pdf")
public class ExtractController
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


    @ApiOperation(value = "Extract all the words in the PDF.")
    @RequestMapping(value = "/extract/text", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<Object> extractTextFromPdf(
            @ApiParam(name = "file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name = "pages", required = false, defaultValue = "*", value = "page numbers from where the text needs to be extracted from the PDF document")
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name = "addquads", required = false, defaultValue = "false", value = "add the position or quadrants for the text in the PDF")
                @RequestPart(value = "addquads", required = false) String addquads,
            @ApiParam(name = "honourspaces", required = false, defaultValue = "false", value = "Set this option to 'true', for improved readability and spacing.")
                @RequestPart(value = "honourspaces", required = false) Boolean honourspaces,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password,
            @ApiParam(name = "type", required = false, defaultValue = "xml", value = "string or xml format in which the text needs to be extracted")
                @RequestPart(value = "type", required = false) String type,
            @ApiParam(name = "usestructure", required = false, value = "Lets you extract content based on the PDF structure. For better readability of the extracted text, use this attribute together with the attribute honourspaces.")
                @RequestPart(value = "usestructure", required = false) Boolean useStructure
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        ExtractTextJob job = new ExtractTextJob();
        job.setFile(new Document(file));

        job.setPages(pages==null?"*":pages);
        if(addquads!=null) job.setAddQuads(addquads);
        if(honourspaces!=null) job.setHonourSpaces(honourspaces);
        if(password!=null) job.setPassword(password);
        job.setType(type==null?"xml":type);
        job.setUseStructure(useStructure);


        Future<Map> future = textGateway.extractText(job);
        ObjectJob payload = (ObjectJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        Object result = payload.getResult();
        String contentType = MimeType.pdf.contentType;
        return ResponseEntityHelper.processObject(result);
    }


    @ApiOperation(value = "Extract images in pdf")
    @RequestMapping(value = "/extract/image", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<Object> extractImageFromPdf(
            @ApiParam(name = "file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name = "pages", required = false, defaultValue = "*", value = "page numbers from where the text needs to be extracted from the PDF document")
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password,
            @ApiParam(name = "format", required = false, value = "png|tiff|jpg - Format in which the images should be extracted")
                @RequestPart(value = "format", required = false) String format,
            @ApiParam(name = "imagePrefix", required = false, value = "the string that you want to prefix with the image name")
                @RequestPart(value = "imagePrefix", required = false) String imagePrefix

        ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        ExtractImageJob job = new ExtractImageJob();
        job.setFile(new Document(file));
        if(pages!=null) job.setPages(pages);
        if(password!=null) job.setPassword(password);
        job.setFormat(format==null?"jpg":format);
        job.setImagePrefix(imagePrefix==null?"*":imagePrefix);


        Future<Map> future = imageGateway.extractImage(job);
        ObjectJob payload = (ObjectJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        Object result = payload.getResult();
        String contentType = MimeType.pdf.contentType;
        return ResponseEntityHelper.processObject(result);
    }


}
