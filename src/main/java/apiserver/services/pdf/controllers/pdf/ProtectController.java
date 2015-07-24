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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
public class ProtectController
{
    @Qualifier("protectPdfApiGateway")
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
    @RequestMapping(value = "/protect", method = RequestMethod.POST)
    public ResponseEntity protectPdf(
            HttpServletRequest request,
            @ApiParam(name="file", required = true)
                @RequestParam(value = "file", required = true) MultipartFile file,
            @ApiParam(name="encrypt", required = false, allowableValues = "RC4_40,RC4_128,RC4_128M,AES_128,none", defaultValue = "RC4_128", value = "Encryption type for the PDF output file:")
                @RequestParam(value = "encrypt", required = false, defaultValue="RC4_40") String encrypt,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password,
            @ApiParam(name="newUserPassword", required = false, value="Password used to open PDF document.")
                @RequestParam(value = "newUserPassword", required = false) String newUserPassword,
            @ApiParam(name="newOwnerPassword", required = false, value = "Password used to set permissions on a PDF document.")
                @RequestParam(value = "newOwnerPassword", required = false) String newOwnerPassword,
            @ApiParam(name="allowAssembly", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowAssembly", required = false) Boolean allowAssembly,
            @ApiParam(name="allowCopy", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowCopy", required = false) Boolean allowCopy,
            @ApiParam(name="allowDegradedPrinting", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowDegradedPrinting", required = false) Boolean allowDegradedPrinting,
            @ApiParam(name="allowFillIn", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowFillIn", required = false) Boolean allowFillIn,
            @ApiParam(name="allowModifyAnnotations", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowModifyAnnotations", required = false) Boolean allowModifyAnnotations,
            @ApiParam(name="allowPrinting", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowPrinting", required = false) Boolean allowPrinting,
            @ApiParam(name="allowScreenReaders", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowScreenReaders", required = false) Boolean allowScreenReaders,
            @ApiParam(name="allowSecure", required = false, value = "permissions on the PDF document")
                @RequestParam(value = "allowSecure", required = false) Boolean allowSecure
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeJob(file, null, encrypt, password, newUserPassword, newOwnerPassword, allowAssembly, allowCopy, allowDegradedPrinting, allowFillIn, allowModifyAnnotations, allowPrinting, allowScreenReaders, allowSecure);
    }





    private ResponseEntity executeJob(
            MultipartFile file
            , String documentId
            , String encrypt
            , String password
            , String newUserPassword
            , String newOwnerPassword
            , Boolean allowAssembly
            , Boolean allowCopy
            , Boolean allowDegradedPrinting
            , Boolean allowFillIn
            , Boolean allowModifyAnnotations
            , Boolean allowPrinting
            , Boolean allowScreenReaders
            , Boolean allowSecure
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        if( newUserPassword == null && newOwnerPassword == null)
        {
            MultiValueMap _headers = new LinkedMultiValueMap();
            _headers.add("Content-Type", "text/plain");
            return new ResponseEntity("Missing newUserPassword or newOwnerPassword", HttpStatus.BAD_REQUEST);
        }

        if( encrypt != null ){
            try {
                CFPdfJob.Encryption encryption = CFPdfJob.Encryption.valueOf(encrypt.toUpperCase());
            }catch(IllegalArgumentException ex){
                String values = org.apache.commons.lang.StringUtils.join(CFPdfJob.Encryption.values(), ",");
                MultiValueMap _headers = new LinkedMultiValueMap();
                _headers.add("Content-Type", "text/plain");
                return new ResponseEntity("Invalid encryption value. Valid values are: " + values, _headers, HttpStatus.BAD_REQUEST);
            }
        }


        CFPdfJob job = new CFPdfJob();
        job.setAction("protect");

        //file
        if( file != null ) {
            job.setDocument(new Document(file));
        }else if( documentId != null ){
            job.setDocumentId(documentId);
        }
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

        Future<Map> future = gateway.protectPdf(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }

}
