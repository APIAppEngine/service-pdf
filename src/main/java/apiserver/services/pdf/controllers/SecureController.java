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
import apiserver.exceptions.NotImplementedException;
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.SecurePdfJob;
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
public class SecureController
{
    @Qualifier("securePdfApiGateway")
    @Autowired
    public PdfGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * Secure a pdf
     * @param file
     * @param password  Owner or user password of the source PDF document, if the document is password-protected.
     * @param newUserPassword   Password used to open PDF document.
     * @param newOwnerPassword  Password used to set permissions on a PDF document
     * @param encrypt   RC4_40|RC4_128|RC4_128M|AES_128|none
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Secure a pdf with a password")
    @RequestMapping(value = "/protect", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> protectPdf(
            @ApiParam(name="file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart("password") String password,
            @ApiParam(name="newUserPassword", required = true, value="Password used to open PDF document.")
                @RequestPart("newUserPassword") String newUserPassword,
            @ApiParam(name="newOwnerPassword", required = true, value = "Password used to set permissions on a PDF document.")
                @RequestPart("newOwnerPassword") String newOwnerPassword,
            @ApiParam(name="encrypt", required = false, allowableValues = "RC4_40,RC4_128,RC4_128M,AES_128,none", value = "Encryption type for the PDF output file:")
                @RequestPart("encrypt") String encrypt,
            @ApiParam(name="allowAssembly", required = false, value = "permissions on the PDF document")
                @RequestPart("allowAssembly") Boolean allowAssembly,
            @ApiParam(name="allowCopy", required = false, value = "permissions on the PDF document")
                @RequestPart("allowCopy") Boolean allowCopy,
            @ApiParam(name="allowDegradedPrinting", required = false, value = "permissions on the PDF document")
                @RequestPart("allowDegradedPrinting") Boolean allowDegradedPrinting,
            @ApiParam(name="allowFillIn", required = false, value = "permissions on the PDF document")
                @RequestPart("allowFillIn") Boolean allowFillIn,
            @ApiParam(name="allowModifyAnnotations", required = false, value = "permissions on the PDF document")
                @RequestPart("allowModifyAnnotations") Boolean allowModifyAnnotations,
            @ApiParam(name="allowPrinting", required = false, value = "permissions on the PDF document")
                @RequestPart("allowPrinting") Boolean allowPrinting,
            @ApiParam(name="allowScreenReaders", required = false, value = "permissions on the PDF document")
                @RequestPart("allowScreenReaders") Boolean allowScreenReaders,
            @ApiParam(name="allowSecure", required = false, value = "permissions on the PDF document")
                @RequestPart("allowSecure") Boolean allowSecure
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        SecurePdfJob job = new SecurePdfJob();
        //file
        job.setFile(new Document(file));
        if( password != null ) job.setPassword(password);
        if( newUserPassword != null ) job.setNewUserPassword(newUserPassword);
        if( newOwnerPassword != null ) job.setNewOwnerPassword(newOwnerPassword);
        if( encrypt != null ) job.setEncrypt(encrypt);
        // permission
        if( allowAssembly != null ) job.setAllowAssembly(allowAssembly);
        if( allowCopy != null ) job.setAllowCopy(allowCopy);
        if( allowDegradedPrinting != null ) job.setAllowDegradedPrinting(allowDegradedPrinting);
        if( allowFillIn != null ) job.setAllowFillIn(allowFillIn);
        if( allowModifyAnnotations != null ) job.setAllowModifyAnnotations(allowModifyAnnotations);
        if( allowPrinting != null ) job.setAllowPrinting(allowPrinting);
        if( allowScreenReaders != null ) job.setAllowScreenReaders(allowScreenReaders);
        if( allowSecure != null ) job.setAllowSecure(allowSecure);

        Future<Map> future = gateway.securePdf(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

    /**
     * Secure a cached pdf with a password
     * @param documentId
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Secure a cached pdf with a password")
    @RequestMapping(value = "/{documentId}/protect", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<byte[]> protectPdf(
            @ApiParam(name="documentId", required = true)
                @RequestPart("documentId") String documentId,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart("password") String password,
            @ApiParam(name="newUserPassword", required = true, value="Password used to open PDF document.")
                @RequestPart("newUserPassword") String newUserPassword,
            @ApiParam(name="newOwnerPassword", required = true, value = "Password used to set permissions on a PDF document.")
                @RequestPart("newOwnerPassword") String newOwnerPassword,
            @ApiParam(name="encrypt", required = false, allowableValues = "RC4_40,RC4_128,RC4_128M,AES_128,none", value = "Encryption type for the PDF output file:")
                @RequestPart("encrypt") String encrypt,
            @ApiParam(name="allowAssembly", required = false, value = "permissions on the PDF document")
                @RequestPart("allowAssembly") Boolean allowAssembly,
            @ApiParam(name="allowCopy", required = false, value = "permissions on the PDF document")
                @RequestPart("allowCopy") Boolean allowCopy,
            @ApiParam(name="allowDegradedPrinting", required = false, value = "permissions on the PDF document")
                @RequestPart("allowDegradedPrinting") Boolean allowDegradedPrinting,
            @ApiParam(name="allowFillIn", required = false, value = "permissions on the PDF document")
                @RequestPart("allowFillIn") Boolean allowFillIn,
            @ApiParam(name="allowModifyAnnotations", required = false, value = "permissions on the PDF document")
                @RequestPart("allowModifyAnnotations") Boolean allowModifyAnnotations,
            @ApiParam(name="allowPrinting", required = false, value = "permissions on the PDF document")
                @RequestPart("allowPrinting") Boolean allowPrinting,
            @ApiParam(name="allowScreenReaders", required = false, value = "permissions on the PDF document")
                @RequestPart("allowScreenReaders") Boolean allowScreenReaders,
            @ApiParam(name="allowSecure", required = false, value = "permissions on the PDF document")
                @RequestPart("allowSecure") Boolean allowSecure
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        SecurePdfJob job = new SecurePdfJob();
        //file
        job.setDocumentId(documentId);
        if( password != null ) job.setPassword(password);
        if( newUserPassword != null ) job.setNewUserPassword(newUserPassword);
        if( newOwnerPassword != null ) job.setNewOwnerPassword(newOwnerPassword);
        if( encrypt != null ) job.setEncrypt(encrypt);
        // permission
        if( allowAssembly != null ) job.setAllowAssembly(allowAssembly);
        if( allowCopy != null ) job.setAllowCopy(allowCopy);
        if( allowDegradedPrinting != null ) job.setAllowDegradedPrinting(allowDegradedPrinting);
        if( allowFillIn != null ) job.setAllowFillIn(allowFillIn);
        if( allowModifyAnnotations != null ) job.setAllowModifyAnnotations(allowModifyAnnotations);
        if( allowPrinting != null ) job.setAllowPrinting(allowPrinting);
        if( allowScreenReaders != null ) job.setAllowScreenReaders(allowScreenReaders);
        if( allowSecure != null ) job.setAllowSecure(allowSecure);

        Future<Map> future = gateway.securePdf(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }



}
