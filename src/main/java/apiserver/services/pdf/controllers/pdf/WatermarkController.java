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

import apiserver.MimeType;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
@Api(value = "/api/pdf", description = "[PDF]")
@RequestMapping("/api/pdf")
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
    @RequestMapping(value = "/modify/watermark", method = RequestMethod.POST)
    public ResponseEntity<byte[]> addWatermarkToPdf(
            @ApiParam(name="file", required = true)
                @RequestParam("file") MultipartFile file,
            @ApiParam(name="image", required = true)
                @RequestParam("image") MultipartFile image,
            @ApiParam(name="foreground", required = false, allowableValues = "yes, no", value = "Placement of the watermark on the page:")
                @RequestParam(value="foreground", required = false) Boolean foreground,
            @ApiParam(name="opacity", required = false, value = "Opacity of the watermark. Valid values are integers in the range 0 (transparent) through 10 (opaque).")
                @RequestParam(value = "opacity", required = false) Double opacity,
            @ApiParam(name="position", required = false, value = "Position on the page where the watermark is placed. The position represents the top-left corner of the watermark. Specify the xand y coordinates; for example “50,30”.")
                @RequestParam(value="position", required = false) String position,
            @ApiParam(name="showOnPrint", required = false, value = "Specify whether to print the watermark with the PDF document:")
                @RequestParam(value="showOnPrint", required = false, defaultValue = "true") Boolean showOnPrint

    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeAddWatermarkJob(file, null, image, foreground, opacity, position, showOnPrint);
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
    @RequestMapping(value = "/modify/watermark/remove", method = RequestMethod.POST)
    public ResponseEntity<byte[]> removeWatermarkFromPdf(
            @ApiParam(name="file", required = true)
                @RequestParam("file") MultipartFile file,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer")
                @RequestParam(value = "pages", required = false) String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeRemoveWatermarkJob(file, null, pages, password);
    }






    private ResponseEntity<byte[]> executeAddWatermarkJob(
            MultipartFile file
            , String documentId
            , MultipartFile image
            , Boolean foreground
            , Double opacity
            , String position
            , Boolean showOnPrint
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfJob job = new CFPdfJob();
        job.setAction("addwatermark");
        //file
        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }

        if( image != null ){
            Document doc = new Document(image);
            doc.setContentType(MimeType.getMimeType(image.getOriginalFilename()));
            doc.setFileName(image.getOriginalFilename());
            job.setImage(doc);
        }

        if( foreground != null ) job.setForeground(foreground);
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( position != null ) job.setPosition(position);
        if( opacity != null ) job.setOpacity(opacity);

        Future<Map> future = gateway.addWatermarkToPdf(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }




    private ResponseEntity<byte[]> executeRemoveWatermarkJob(
            MultipartFile file
            , String documentId
            , String pages
            , String password
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfJob job = new CFPdfJob();
        job.setAction("removeWatermark");
        //file
        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }

        if( pages != null ) job.setPages(pages);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.removeWatermarkFromPdf(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }
}
