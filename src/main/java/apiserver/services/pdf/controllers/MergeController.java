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
import apiserver.core.connectors.coldfusion.services.BinaryResult;
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.MergePdfResult;
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
public class MergeController
{
    @Qualifier("mergePdfFormApiGateway")
    @Autowired
    public PdfGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * Merge two pdf files into one.
     * @param file1
     * @param file2
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Merge two PDF documents into an output PDF file.")
    @RequestMapping(value = "/merge", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> mergePdfDocuments(
            @ApiParam(name="files", required = true, value = "multiple pdf files to merge into one.")
                @RequestPart("files") MultipartFile[] files,
            @ApiParam(name="order", required = true, allowableValues = "name,time", value="How to order the files when merging, required only when package is specified as true")
                @RequestPart("order") String order,
            @ApiParam(name="package", required = true, allowableValues = "yes,no", value="create PDF packages if set to true.")
                @RequestPart("package") Boolean packagePdf,
            @ApiParam(name="ascending", required = true, allowableValues = "yes,no", value="Order in which the PDF files are sorted:")
                @RequestPart("ascending") Boolean ascending,
            @ApiParam(name="keepBookmark", required = true, allowableValues = "yes,no", value="Specifies whether bookmarks from the source PDF documents are retained in the merged document")
                @RequestPart("keepBookmark") Boolean keepBookmark,
            @ApiParam(name="pages", required = true, value="Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.")
                @RequestPart("pages") String pages,
            @ApiParam(name="password", required = true, value="Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart("password") String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        MergePdfResult job = new MergePdfResult();

        int idx = 0;
        Document[] documents = new Document[files.length];
        for (MultipartFile file : files) {
            Document doc = new Document(file);
            doc.setFileName(file.getOriginalFilename());
            doc.setContentType(MimeType.getMimeType(file.getOriginalFilename()));
            documents[idx++] = doc;
        }

        if( order != null ) job.setOrder(order);
        if( packagePdf != null ) job.setPackagePdf(packagePdf);
        if( ascending != null ) job.setAscending(ascending);
        if( keepBookmark != null ) job.setKeepBookmark(keepBookmark);
        if( pages != null ) job.setPages(pages);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.mergePdf(job);
        BinaryResult payload = (BinaryResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


}
