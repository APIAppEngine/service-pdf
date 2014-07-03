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
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.OptimizePdfResult;
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
public class OptimizeController
{
    @Qualifier("optimizePdfApiGateway")
    @Autowired public PdfGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     *
     * @param file
     * @param algo bilinear,bicubic,nearest_neighbour - algorithm for image downsampling
     * @param pages page numbers associated with the objects in the PDF document or "*"
     * @param vscale Vertical scale of the image to be modified. Valid values are vscale>0
     * @param hscale Horizontal scale of the image to be modified. Valid values are hscale<1
     * @param noattachments Discard all attachments
     * @param nobookmarks Discard all bookmarks
     * @param nocomments Discard all comments
     * @param nofonts Discard all fonts
     * @param nojavascripts Discard all JavaScript actions
     * @param nolinks Discard external cross-references
     * @param nometadata Discard document information and metadata
     * @param nothumbnails Discard embedded page thumbnails
     * @param password PDF document password
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Reduce the quality of a PDF document")
    @RequestMapping(value = "/optimize", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> optimizePdf(
            @ApiParam(name="file", required = true)
                @RequestPart("file") MultipartFile file,
            //required options
            @ApiParam(name="algo", required = true, allowableValues = "bilinear,bicubic,nearest_neighbour", value = "Specifies the algorithm for image downsampling. The values are bilinear, bicubic, and nearest_neighbour")
                @RequestPart("algo") String algo,
            @ApiParam(name="pages", required = true, defaultValue = "*", value = "Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.")
                @RequestPart(value = "pages", required = true) String pages,
            //optional options
            @ApiParam(name="vscale", required = false, value = "Vertical scale of the image to be modified. Valid values are vscale>0")
                @RequestPart(value = "vscale", required = false) Integer vscale,
            @ApiParam(name="hscale", required = false, value="Horizontal scale of the image to be modified. Valid values are hscale<1")
                @RequestPart(value = "hscale", required = false) Integer hscale,
            @ApiParam(name="noattachments", required = false, allowableValues = "yes,no", value = "Discard all attachments")
                @RequestPart(value = "noattachments", required = false) Boolean noattachments,
            @ApiParam(name="nobookmarks", required = false, allowableValues = "yes,no", value = "Discard all bookmarks")
                @RequestPart(value = "nobookmarks", required = false) Boolean nobookmarks,
            @ApiParam(name="nocomments", required = false, allowableValues = "yes,no", value = "Discard all comments")
                @RequestPart(value = "nocomments", required = false) Boolean nocomments,
            @ApiParam(name="nofonts", required = false, allowableValues = "yes,no", value = "Discard all fonts")
                @RequestPart(value = "nofonts", required = false) Boolean nofonts,
            @ApiParam(name="nojavascripts", required = false, allowableValues = "yes,no", value = "Discard all JavaScript actions")
                @RequestPart(value = "nojavascripts", required = false) Boolean nojavascripts,
            @ApiParam(name="nolinks", required = false, allowableValues = "yes,no", value = "Discard external cross-references")
                @RequestPart(value = "nolinks", required = false) Boolean nolinks,
            @ApiParam(name="nometadata", required = false, allowableValues = "yes,no", value = "Discard document information and metadata")
                @RequestPart(value = "nometadata", required = false) Boolean nometadata,
            @ApiParam(name="nothumbnails", required = false, allowableValues = "yes,no", value = "Discard embedded page thumbnails")
                @RequestPart(value = "nothumbnails", required = false) Boolean nothumbnails,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        OptimizePdfResult job = new OptimizePdfResult();
        //file
        job.setFile(new Document(file));

        job.setAlgo(algo);
        job.setPages(pages);
        if( vscale != null ) job.setVScale(new Double(vscale));
        if( hscale != null ) job.setHScale(new Double(hscale));
        if( noattachments != null ) job.setNoAttachments(noattachments);
        if( nobookmarks != null ) job.setNoBookmarks(nobookmarks);
        if( nocomments != null ) job.setNoComments(nocomments);
        if( nofonts != null ) job.setNoFonts(nofonts);
        if( nojavascripts != null ) job.setNoJavaScripts(nojavascripts);
        if( nolinks != null ) job.setNoLinks(nolinks);
        if( nometadata != null ) job.setNoMetadata(nometadata);
        if( nothumbnails != null ) job.setNoThumbnails(nothumbnails);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.optimizePdf(job);
        PopulatePdfFormResult payload = (PopulatePdfFormResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }




    @ApiOperation(value = "TODO")
    @RequestMapping(value = "/{documentId}/optimize", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<byte[]> optimizeCachedPdf(
            @ApiParam(name="documentId", required = true) @RequestPart("documentId") String documentId,
           //required options
            @ApiParam(name="algo", required = true, allowableValues = "bilinear,bicubic,nearest_neighbour", value = "Specifies the algorithm for image downsampling. The values are bilinear, bicubic, and nearest_neighbour")
            @RequestPart("algo") String algo,
            @ApiParam(name="pages", required = true, defaultValue = "*", value = "Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.")
            @RequestPart(value = "pages", required = true) String pages,
            //optional options
            @ApiParam(name="vscale", required = false, value = "Vertical scale of the image to be modified. Valid values are vscale>0")
            @RequestPart(value = "vscale", required = false) Integer vscale,
            @ApiParam(name="hscale", required = false, value="Horizontal scale of the image to be modified. Valid values are hscale<1")
            @RequestPart(value = "hscale", required = false) Integer hscale,
            @ApiParam(name="noattachments", required = false, allowableValues = "yes,no", value = "Discard all attachments")
            @RequestPart(value = "noattachments", required = false) Boolean noattachments,
            @ApiParam(name="nobookmarks", required = false, allowableValues = "yes,no", value = "Discard all bookmarks")
            @RequestPart(value = "nobookmarks", required = false) Boolean nobookmarks,
            @ApiParam(name="nocomments", required = false, allowableValues = "yes,no", value = "Discard all comments")
            @RequestPart(value = "nocomments", required = false) Boolean nocomments,
            @ApiParam(name="nofonts", required = false, allowableValues = "yes,no", value = "Discard all fonts")
            @RequestPart(value = "nofonts", required = false) Boolean nofonts,
            @ApiParam(name="nojavascripts", required = false, allowableValues = "yes,no", value = "Discard all JavaScript actions")
            @RequestPart(value = "nojavascripts", required = false) Boolean nojavascripts,
            @ApiParam(name="nolinks", required = false, allowableValues = "yes,no", value = "Discard external cross-references")
            @RequestPart(value = "nolinks", required = false) Boolean nolinks,
            @ApiParam(name="nometadata", required = false, allowableValues = "yes,no", value = "Discard document information and metadata")
            @RequestPart(value = "nometadata", required = false) Boolean nometadata,
            @ApiParam(name="nothumbnails", required = false, allowableValues = "yes,no", value = "Discard embedded page thumbnails")
            @RequestPart(value = "nothumbnails", required = false) Boolean nothumbnails,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
            @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        OptimizePdfResult job = new OptimizePdfResult();
        //file
        job.setDocumentId(documentId);

        job.setAlgo(algo);
        job.setPages(pages);
        if( vscale != null ) job.setVScale(new Double(vscale));
        if( hscale != null ) job.setHScale(new Double(hscale));
        if( noattachments != null ) job.setNoAttachments(noattachments);
        if( nobookmarks != null ) job.setNoBookmarks(nobookmarks);
        if( nocomments != null ) job.setNoComments(nocomments);
        if( nofonts != null ) job.setNoFonts(nofonts);
        if( nojavascripts != null ) job.setNoJavaScripts(nojavascripts);
        if( nolinks != null ) job.setNoLinks(nolinks);
        if( nometadata != null ) job.setNoMetadata(nometadata);
        if( nothumbnails != null ) job.setNoThumbnails(nothumbnails);
        if( password != null ) job.setPassword(password);

        Future<Map> future = gateway.optimizePdf(job);
        PopulatePdfFormResult payload = (PopulatePdfFormResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getResult();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

}
