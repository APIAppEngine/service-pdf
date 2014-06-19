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
import apiserver.core.connectors.coldfusion.services.BinaryJob;
import apiserver.core.connectors.coldfusion.services.ObjectJob;
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.PdfGetInfoJob;
import apiserver.services.pdf.gateways.jobs.PdfSetInfoJob;
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
    @RequestMapping(value = "/info/get", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<Object> getPdfInfo(
            @ApiParam(name = "file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception {

        PdfGetInfoJob job = new PdfGetInfoJob();
        job.setFile(new Document(file));
        if (password != null) {
            job.setPassword(password);
        }

        Future<Map> future = getInfoGateway.pdfGetInfo(job);
        ObjectJob payload = (ObjectJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        return ResponseEntityHelper.processObject(payload.getResult());
    }


    /**
     * Get information about document
     *
     * @param documentId
     * @param password   Password to open pdf
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Get information about a cached PDF document ")
    @RequestMapping(value = "/{documentId}/info/get", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> getCachedPdfInfo(
            @ApiParam(name = "documentId", required = true)
                @RequestPart("documentId") String documentId,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception {

        PdfGetInfoJob job = new PdfGetInfoJob();
        job.setDocumentId(documentId);
        if (password != null) {
            job.setPassword(password);
        }

        Future<Map> future = getInfoGateway.pdfGetInfo(job);
        ObjectJob payload = (ObjectJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        return ResponseEntityHelper.processObject(payload.getResult());
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
    @RequestMapping(value = "/info/set", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> setPdfInfo(
            @ApiParam(name = "file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name = "info", required = true, value = "Map variable for relevant information. You can specify the Author, Subject, Title, and Keywords for the PDF output file.")
                @RequestPart("info") Map info,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception {

        PdfSetInfoJob job = new PdfSetInfoJob();
        job.setFile(new Document(file));
        if (info != null) {
            job.setInfo(info);
        }
        if (password != null) {
            job.setPassword(password);
        }

        Future<Map> future = setInfoGateway.pdfSetInfo(job);
        BinaryJob payload = (BinaryJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


    /**
     * Set information about cached document
     *
     * @param documentId PDF to update
     * @param info       Map of key/values to set
     * @param password   Password to open pdf
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Set information about a cached PDF document ")
    @RequestMapping(value = "/{documentId}/info/set", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<byte[]> setCachedPdfInfo(
            @ApiParam(name = "documentId", required = true)
                @RequestPart("documentId") String documentId,
            @ApiParam(name = "info", required = true, value = "Map variable for relevant information. You can specify the Author, Subject, Title, and Keywords for the PDF output file.")
                @RequestPart("info") Map info,
            @ApiParam(name = "password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception {

        PdfSetInfoJob job = new PdfSetInfoJob();
        job.setDocumentId(documentId);
        if (info != null) {
            job.setInfo(info);
        }
        if (password != null) {
            job.setPassword(password);
        }

        Future<Map> future = setInfoGateway.pdfSetInfo(job);
        BinaryJob payload = (BinaryJob) future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

}
