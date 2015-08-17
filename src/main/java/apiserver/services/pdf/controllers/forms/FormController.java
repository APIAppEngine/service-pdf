package apiserver.services.pdf.controllers.forms;

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
import apiserver.services.pdf.gateways.PdfFormGateway;
import apiserver.services.pdf.gateways.jobs.CFPdfFormJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
@Api(value = "/api/v1/pdf", description = "[PDF]")
@RequestMapping("/api/v1/pdf")
public class FormController
{
    @Qualifier("populatePdfFormApiGateway")
    @Autowired
    public PdfFormGateway populateGateway;

    @Qualifier("extractPdfFormApiGateway")
    @Autowired
    public PdfFormGateway extractGateway;

    private @Value("${defaultReplyTimeout}")  Integer defaultTimeout;


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
            @ApiParam(name="file", required = true)
                @RequestParam(value = "file", required = true) MultipartFile file,
            @ApiParam(name="format", required = false, allowableValues = "xml,json")
                @RequestParam(value = "format", required = false, defaultValue = "xml") String format,
            @ApiParam(name="password", required = false)
                @RequestParam(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        if( !format.toLowerCase().equals("xml") && !format.toLowerCase().equals("json") )
        {
            MultiValueMap _headers = new LinkedMultiValueMap();
            _headers.add("Content-Type", "text/plain");
            return new ResponseEntity("Invalid format. Allowable values are XML or JSON", HttpStatus.BAD_REQUEST);
        }


        CFPdfFormJob job = new CFPdfFormJob();
        job.setDocument(new Document(file));
        job.setFormat(format);
        if( password != null ) job.setPassword(password);

        Future<Map> future = extractGateway.extractPdfForm(job, format);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
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
            @ApiParam(name="file", required = true)
                @RequestPart(value = "file", required = true) MultipartFile file,
            @ApiParam(name="xmlData", required = true)
                @RequestPart(value = "xmlData", required = true) String xmlData,
            @ApiParam(name="password", required = false)
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        CFPdfFormJob job = new CFPdfFormJob();
        job.setDocument(new Document(file));
        job.setFields(xmlData);
        if( password != null ) job.setPassword(password);

        Future<Map> future = populateGateway.populatePdfForm(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }



}
