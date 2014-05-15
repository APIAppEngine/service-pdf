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
import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.services.ObjectJob;
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

import javax.ws.rs.Produces;
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


    @ApiOperation(value = "TODO")
    @Produces("application/pdf")
    @RequestMapping(value = "/extract/text", method = RequestMethod.POST)
    public ResponseEntity<Object> extractTextFromPdf(
            @ApiParam(name = "file", required = true) @RequestPart("file") MultipartFile file
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        ExtractTextJob job = new ExtractTextJob();
        job.setFile(new Document(file));


        Future<Map> future = textGateway.extractText(job);
        ObjectJob payload = (ObjectJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        Object result = payload.getResult();
        String contentType = MimeType.pdf.contentType;
        return ResponseEntityHelper.processObject(result);
    }


    @ApiOperation(value = "TODO")
    @Produces("application/pdf")
    @RequestMapping(value = "/extract/image", method = RequestMethod.POST)
    public ResponseEntity<Object> extractImageFromPdf(
            @ApiParam(name = "file", required = true) @RequestPart("file") MultipartFile file
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        ExtractImageJob job = new ExtractImageJob();
        job.setFile(new Document(file));


        Future<Map> future = imageGateway.extractImage(job);
        ObjectJob payload = (ObjectJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        Object result = payload.getResult();
        String contentType = MimeType.pdf.contentType;
        return ResponseEntityHelper.processObject(result);
    }


}
