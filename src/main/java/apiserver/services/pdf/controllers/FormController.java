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

import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.services.BinaryResult;
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfFormGateway;
import apiserver.services.pdf.gateways.jobs.ExtractPdfFormResult;
import apiserver.services.pdf.gateways.jobs.PopulatePdfFormResult;
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
public class FormController
{
    @Qualifier("populatePdfFormApiGateway")
    @Autowired
    public PdfFormGateway populateGateway;

    @Qualifier("extractPdfFormApiGateway")
    @Autowired
    public PdfFormGateway extractGateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * Extract the value of the form fields in a pdf
     * @param file
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Extract the value of the form fields in a pdf")
    @RequestMapping(value = "/form/extract", method = RequestMethod.POST)
    public ResponseEntity<Object> extractFormFields(
            @ApiParam(name="file", required = true) @RequestPart("file") MultipartFile file,
            @ApiParam(name="password", required = false) @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        ExtractPdfFormResult job = new ExtractPdfFormResult();
        job.setFile(new Document(file));
        if( password != null ) job.setPassword(password);

        Future<Map> future = extractGateway.extractPdfForm(job);
        ExtractPdfFormResult payload = (ExtractPdfFormResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        return ResponseEntityHelper.processObject(payload.getResult());
    }


    /**
     * Extract the value of the form fields in a pdf
     * @param documentId
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Extract the value of the form fields in a pdf")
    @RequestMapping(value = "/form/{documentId}/extract", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> extractCachedFormFields(
            @ApiParam(name="documentId", required = true) @RequestPart("documentId") String documentId,
            @ApiParam(name="password", required = false) @RequestPart("password") String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        ExtractPdfFormResult job = new ExtractPdfFormResult();
        job.setDocumentId(documentId);
        if( password != null ) job.setPassword(password);

        Future<Map> future = extractGateway.extractPdfForm(job);
        ExtractPdfFormResult payload = (ExtractPdfFormResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        return ResponseEntityHelper.processObject(payload);
    }


    /**
     * Populate the pdf form fields
     * @param file
     * @param XFDF or XFA XML String
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Populate the pdf form fields")
    @RequestMapping(value = "/form/populate", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> populateFormFields(
            @ApiParam(name="file", required = true) @RequestPart("file") MultipartFile file,
            @ApiParam(name="fields", required = true) @RequestPart("fields") String fields,
            @ApiParam(name="password", required = false) @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        PopulatePdfFormResult job = new PopulatePdfFormResult();
        job.setFile(new Document(file));
        job.setFields(fields);
        if( password != null ) job.setPassword(password);

        Future<Map> future = populateGateway.populatePdfForm(job);
        PopulatePdfFormResult payload = (PopulatePdfFormResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


    /**
     * Populate the pdf form fields
     * @param documentId
     * @param xfdf XML
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Populate the pdf form fields")
    @RequestMapping(value = "/form/{documentId}/populate", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> populateCachedFormFields(
            @ApiParam(name="documentId", required = true) @RequestPart("documentId") String documentId,
            @ApiParam(name="fields", required = true) @RequestPart("fields") String fields,
            @ApiParam(name="password", required = false) @RequestPart("password") String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        PopulatePdfFormResult job = new PopulatePdfFormResult();
        job.setDocumentId(documentId);
        job.setFields(fields);
        if( password != null ) job.setPassword(password);

        Future<Map> future = populateGateway.populatePdfForm(job);
        BinaryResult payload = (BinaryResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


}
