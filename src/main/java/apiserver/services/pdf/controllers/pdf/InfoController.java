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

import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.services.IObjectResult;
import apiserver.jobs.IProxyJob;
import apiserver.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.CFPdfJob;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
public class InfoController
{
    @Qualifier("pdfGetInfoApiGateway")
    @Autowired public PdfGateway getInfoGateway;

    @Qualifier("pdfSetInfoApiGateway")
    @Autowired public PdfGateway setInfoGateway;

    private
    @Value("#{applicationProperties.defaultReplyTimeout}")
    Integer defaultTimeout;


    /**
     * Get information about document
     *
     * @param file     PDF to pull info from
     * @param password Password to open pdf
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Get information about a PDF document ")
    @RequestMapping(value = "/info/get", method = RequestMethod.POST)
    public ResponseEntity<Object> getPdfInfo(
            @ApiParam(name = "file", required = true)
                @RequestParam("file") MultipartFile file,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception {

        return executeGetInfoJob(file, null, password);
    }



    /**
     * Set information about document
     *
     * @param file     PDF to update
     * @param info     Map of key/values to set
     * @param password Password to open pdf
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Set information about a PDF document ")
    @RequestMapping(value = "/info/set", method = RequestMethod.POST)
    public ResponseEntity<byte[]> setPdfInfo(
            @ApiParam(name = "file", required = true)
                @RequestParam("file") MultipartFile file,
            @ApiParam(name = "info", required = true, value = "Json String/Map variable for relevant information. You can specify the Author, Subject, Title, and Keywords for the PDF output file.")
                @RequestParam("info") String info,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception {

        return executeSetInfoJob(file, null, info, password);
    }






    private ResponseEntity<Object> executeGetInfoJob(
            MultipartFile file
            , String documentId
            , String password
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfJob job = new CFPdfJob();
        job.setAction("getinfo");
        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }
        if (password != null) {
            job.setPassword(password);
        }

        Future<Map> future = getInfoGateway.pdfGetInfo(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }






    private ResponseEntity<byte[]> executeSetInfoJob(
            MultipartFile file
            , String documentId
            , String infoMap
            , String password
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfJob job = new CFPdfJob();
        job.setAction("setinfo");

        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }

        if (infoMap != null) {
            //deserialize the json infoMAP
            Map info = new ObjectMapper().readValue(infoMap, HashMap.class);
            job.setInfo(info);
        }

        if (password != null) {
            job.setPassword(password);
        }

        Future<Map> future = setInfoGateway.pdfSetInfo(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }
}


