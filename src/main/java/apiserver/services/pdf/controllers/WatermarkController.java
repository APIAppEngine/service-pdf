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
import apiserver.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.WatermarkPdfResult;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * User: mnimer
 * Date: 9/15/12
 */
@Controller
@RestController
@Api(value = "/pdf", description = "[PDF]")
@RequestMapping("/pdf")
public class WatermarkController
{
    @Qualifier("watermarkPdfGateway")
    @Autowired
    public PdfGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * Add Watermark
     * @param file
     * @param image
     * @param foreground
     * @param showOnPrint
     * @param position
     * @param opacity
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Add Watermark")
    @RequestMapping(value = "/modify/watermark", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> addWatermarkToPdf(
            @ApiParam(name="file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name="image", required = true)
                @RequestPart("image") MultipartFile image,
            @ApiParam(name="foreground", required = false, allowableValues = "yes, no", value = "Placement of the watermark on the page:")
                @RequestParam(value="foreground") Boolean foreground,
            @ApiParam(name="showOnPrint", required = false, value = "Specify whether to print the watermark with the PDF document:")
                @RequestParam(value="showOnPrint") Boolean showOnPrint,
            @ApiParam(name="position", required = false, value = "Position on the page where the watermark is placed. The position represents the top-left corner of the watermark. Specify the xand y coordinates; for example “50,30”.")
                @RequestParam(value="position") String position,
            @ApiParam(name="opacity", required = false, value = "Opacity of the watermark. Valid values are integers in the range 0 (transparent) through 10 (opaque).")
                @RequestPart(value = "opacity", required = false) Double opacity

    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        WatermarkPdfResult job = new WatermarkPdfResult();
        //file
        job.setFile(new Document(file));
        if( image != null ){
            Document doc = new Document(image);
            doc.setContentType(MimeType.getMimeType(image.getOriginalFilename()));
            doc.setFileName(image.getOriginalFilename());
        }

        if( foreground != null ) job.setForeground(foreground);
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( position != null ) job.setPosition(position);
        if( opacity != null ) job.setOpacity(opacity);

        Future<Map> future = gateway.addWatermarkToPdf(job);
        BinaryResult payload = (BinaryResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

    /**
     * Add Watermark to cached pdf
     * @param documentId
     * @param image
     * @param foreground
     * @param showOnPrint
     * @param position
     * @param opacity
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Add Watermark to cached pdf")
    @RequestMapping(value = "/modify/{documentId}/watermark", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> addWatermarkToCachedPdf(
            @ApiParam(name="documentId", required = true)
                @RequestPart("documentId") String documentId,
            @ApiParam(name="image", required = true)
                @RequestPart("image") MultipartFile image,
            @ApiParam(name="foreground", required = false, allowableValues = "yes, no", value = "Placement of the watermark on the page:")
                @RequestParam(value="foreground") Boolean foreground,
            @ApiParam(name="showOnPrint", required = false, value = "Specify whether to print the watermark with the PDF document:")
                @RequestParam(value="showOnPrint") Boolean showOnPrint,
            @ApiParam(name="position", required = false, value = "Position on the page where the watermark is placed. The position represents the top-left corner of the watermark. Specify the xand y coordinates; for example “50,30”.")
                @RequestParam(value="position") String position,
            @ApiParam(name="opacity", required = false, value = "Opacity of the watermark. Valid values are integers in the range 0 (transparent) through 10 (opaque).")
                @RequestPart(value = "opacity", required = false) Double opacity
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        WatermarkPdfResult job = new WatermarkPdfResult();
        //file
        job.setDocumentId(documentId);
        if( image != null ){
            Document doc = new Document(image);
            doc.setContentType(MimeType.getMimeType(image.getOriginalFilename()));
            doc.setFileName(image.getOriginalFilename());
        }

        if( foreground != null ) job.setForeground(foreground);
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( position != null ) job.setPosition(position);
        if( opacity != null ) job.setOpacity(opacity);

        Future<Map> future = gateway.addWatermarkToPdf(job);
        BinaryResult payload = (BinaryResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


    /**
     * Remove watermark
     * @param file
     * @param pages
     * @param password
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Remove Watermark")
    @RequestMapping(value = "/modify/watermark", method = RequestMethod.DELETE, produces = "application/pdf")
    public ResponseEntity<byte[]> removeWatermarkFromPdf(
            @ApiParam(name="file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer")
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        WatermarkPdfResult job = new WatermarkPdfResult();
        //file
        job.setFile(new Document(file));
        if( pages != null ) job.setPages(pages);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.removeWatermarkFromPdf(job);
        BinaryResult payload = (BinaryResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


    /**
     * Remove watermark
     * @param documentId
     * @param pages
     * @param password
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Remove Watermark")
    @RequestMapping(value = "/modify/{documentId}/watermark", method = RequestMethod.DELETE, produces = "application/pdf")
    public ResponseEntity<byte[]> removeWatermarkFromCachedPdf(
            @ApiParam(name="documentId", required = true)
                @RequestPart("documentId") String documentId,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer")
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        WatermarkPdfResult job = new WatermarkPdfResult();
        //file
        job.setDocumentId( documentId );
        if( pages != null ) job.setPages(pages);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.removeWatermarkFromPdf(job);
        BinaryResult payload = (BinaryResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


}
