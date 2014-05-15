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

import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.services.BinaryJob;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.ThumbnailPdfJob;
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
public class ThumbnailController
{
    @Qualifier("thumbnailPdfGateway")
    @Autowired
    public PdfGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;



    /**
     * Generate thumbnails from the pages of a PDF
     * @param file
     * @param format    png,jpeg,tiff
     * @param imagePrefix   string used as a prefix in the output filename
     * @param resolution low, high
     * @param scale percentage between 1 and 100
     * @param transparent
     * @param hires You can set this attribute to true to extract high-resolution images from the page. If a document contains high-resolution images and you want to retain the resolution of the images, then this attribute is useful
     * @param compressTiffs
     * @param maxScale  maximum scale of the thumbnail
     * @param maxLength maximum length of the thumbnail
     * @param maxBreadth    maximum width of the thumbnail
     * @param pages
     * @param password
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Generate thumbnails from the pages of a PDF")
    @Produces("application/pdf")
    @RequestMapping(value = "/thumbnail", method = RequestMethod.POST)
    public ResponseEntity<byte[]> thumbnailPdf(
            @ApiParam(name="file", required = true) @RequestPart("file") MultipartFile file,
            @ApiParam(name="format", required = false, allowableValues = "png,jpeg,tiff") @RequestPart("format") String format,
            @ApiParam(name="imagePrefix", required = false, allowableValues = "png,jpeg,tiff") @RequestPart("imagePrefix") String imagePrefix,
            @ApiParam(name="resolution", required = false, allowableValues = "low,high") @RequestPart("resolution") String resolution,
            @ApiParam(name="scale", required = false) @RequestPart("scale") Integer scale,
            @ApiParam(name="transparent", required = false) @RequestPart("transparent") Boolean transparent,
            @ApiParam(name="hires", required = false) @RequestPart("hires") Boolean hires,
            @ApiParam(name="compressTiffs", required = false) @RequestPart("compressTiffs") Boolean compressTiffs,
            @ApiParam(name="maxScale", required = false) @RequestPart("maxscale") Integer maxScale,
            @ApiParam(name="maxLength", required = false) @RequestPart("maxlength") Integer maxLength,
            @ApiParam(name="maxBreadth", required = false) @RequestPart("maxBreadth") Integer maxBreadth,
            @ApiParam(name="pages", required = false) @RequestPart("pages") String pages,
            @ApiParam(name="password", required = false) @RequestPart("password") String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        ThumbnailPdfJob job = new ThumbnailPdfJob();
        //file
        job.setFile(new Document(file));

        if( format != null ) job.setFormat(format);
        if( imagePrefix != null ) job.setImagePrefix(imagePrefix);
        if( resolution != null ) job.setResolution(resolution);
        if( scale != null ) job.setScale(scale);
        if( transparent != null ) job.setTransparent(transparent);
        if( hires != null ) job.setHiRes(hires);
        if( compressTiffs != null ) job.setCompressTiffs(compressTiffs);
        if( maxScale != null ) job.setMaxScale(maxScale);
        if( maxLength != null ) job.setMaxLength(maxLength);
        if( maxBreadth != null ) job.setMaxBreadth(maxBreadth);
        if( pages != null ) job.setPages(pages);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.thumbnailGenerator(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }



    /**
     * Generate thumbnails from the pages of a PDF
     * @param documentId
     * @param format    png,jpeg,tiff
     * @param imagePrefix   string used as a prefix in the output filename
     * @param resolution low, high
     * @param scale percentage between 1 and 100
     * @param transparent
     * @param hires You can set this attribute to true to extract high-resolution images from the page. If a document contains high-resolution images and you want to retain the resolution of the images, then this attribute is useful
     * @param compressTiffs
     * @param maxScale  maximum scale of the thumbnail
     * @param maxLength maximum length of the thumbnail
     * @param maxBreadth    maximum width of the thumbnail
     * @param pages
     * @param password
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Generate thumbnails from the pages of a PDF")
    @Produces("application/pdf")
    @RequestMapping(value = "/thumbnail", method = RequestMethod.POST)
    public ResponseEntity<byte[]> thumbnailCachedPdf(
            @ApiParam(name="documentId", required = true) @RequestPart("documentId") String documentId,
            @ApiParam(name="format", required = false, allowableValues = "png,jpeg,tiff") @RequestPart("format") String format,
            @ApiParam(name="imagePrefix", required = false, allowableValues = "png,jpeg,tiff") @RequestPart("imagePrefix") String imagePrefix,
            @ApiParam(name="resolution", required = false, allowableValues = "low,high") @RequestPart("resolution") String resolution,
            @ApiParam(name="scale", required = false) @RequestPart("scale") Integer scale,
            @ApiParam(name="transparent", required = false) @RequestPart("transparent") Boolean transparent,
            @ApiParam(name="hires", required = false) @RequestPart("hires") Boolean hires,
            @ApiParam(name="compressTiffs", required = false) @RequestPart("compressTiffs") Boolean compressTiffs,
            @ApiParam(name="maxScale", required = false) @RequestPart("maxscale") Integer maxScale,
            @ApiParam(name="maxLength", required = false) @RequestPart("maxlength") Integer maxLength,
            @ApiParam(name="maxBreadth", required = false) @RequestPart("maxBreadth") Integer maxBreadth,
            @ApiParam(name="pages", required = false) @RequestPart("pages") String pages,
            @ApiParam(name="password", required = false) @RequestPart("password") String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        ThumbnailPdfJob job = new ThumbnailPdfJob();
        //file
        job.setDocumentId(documentId);

        if( format != null ) job.setFormat(format);
        if( imagePrefix != null ) job.setImagePrefix(imagePrefix);
        if( resolution != null ) job.setResolution(resolution);
        if( scale != null ) job.setScale(scale);
        if( transparent != null ) job.setTransparent(transparent);
        if( hires != null ) job.setHiRes(hires);
        if( compressTiffs != null ) job.setCompressTiffs(compressTiffs);
        if( maxScale != null ) job.setMaxScale(maxScale);
        if( maxLength != null ) job.setMaxLength(maxLength);
        if( maxBreadth != null ) job.setMaxBreadth(maxBreadth);
        if( pages != null ) job.setPages(pages);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.thumbnailGenerator(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

}
