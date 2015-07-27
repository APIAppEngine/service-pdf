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
 * User: mnimer
 * Date: 9/15/12
 */
@Controller
@RestController
@Api(value = "/api/v1/pdf", description = "[PDF]")
@RequestMapping("/api/v1/pdf")
public class OptimizeController
{
    @Qualifier("optimizePdfApiGateway")
    @Autowired public PdfGateway gateway;

    private  @Value("${defaultReplyTimeout}")  Integer defaultTimeout;


    /**
     *
     * @param file
     * @param algo bilinear,bicubic,nearest_neighbour - algorithm for image downsampling
     * @param hscale Horizontal scale of the image to be modified. Valid values are hscale<1
     * @param noattachments Discard all attachments
     * @param nobookmarks Discard all bookmarks
     * @param nocomments Discard all comments
     * @param nofonts Discard all fonts
     * @param nojavascripts Discard all JavaScript actions
     * @param nolinks Discard external cross-references
     * @param nometadata Discard document information and metadata
     * @param nothumbnails Discard embedded page thumbnails
     * @param pages page numbers associated with the objects in the PDF document or "*"
     * @param password PDF document password
     * @param vscale Vertical scale of the image to be modified. Valid values are vscale>0
     * @return
     * @throws java.io.IOException
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws Exception
     */
    @ApiOperation(value = "Reduce the quality of a PDF document")
    @RequestMapping(value = "/optimize", method = RequestMethod.POST)
    public ResponseEntity optimizePdf(
            @ApiParam(name = "file", required = true)
                @RequestParam("file") MultipartFile file,
            @ApiParam(name="algo", required = true, allowableValues = "bilinear,bicubic,nearest_neighbour", value = "Specifies the algorithm for image downsampling. The values are bilinear, bicubic, and nearest_neighbour")
                @RequestParam(value = "algo", required = false) String algo,
            @ApiParam(name="hscale", required = false, value="Horizontal scale of the image to be modified. Valid values are hscale<1")
                @RequestParam(value = "hscale", required = false) Double hscale,
            @ApiParam(name="noattachments", required = false, allowableValues = "yes,no", value = "Discard all attachments")
                @RequestParam(value = "noattachments", required = false) Boolean noattachments,
            @ApiParam(name="nobookmarks", required = false, allowableValues = "yes,no", value = "Discard all bookmarks")
                @RequestParam(value = "nobookmarks", required = false) Boolean nobookmarks,
            @ApiParam(name="nocomments", required = false, allowableValues = "yes,no", value = "Discard all comments")
                @RequestParam(value = "nocomments", required = false) Boolean nocomments,
            @ApiParam(name="nofonts", required = false, allowableValues = "yes,no", value = "Discard all fonts")
                @RequestParam(value = "nofonts", required = false) Boolean nofonts,
            @ApiParam(name="nojavascripts", required = false, allowableValues = "yes,no", value = "Discard all JavaScript actions")
                @RequestParam(value = "nojavascripts", required = false) Boolean nojavascripts,
            @ApiParam(name="nolinks", required = false, allowableValues = "yes,no", value = "Discard external cross-references")
                @RequestParam(value = "nolinks", required = false) Boolean nolinks,
            @ApiParam(name="nometadata", required = false, allowableValues = "yes,no", value = "Discard document information and metadata")
                @RequestParam(value = "nometadata", required = false) Boolean nometadata,
            @ApiParam(name="nothumbnails", required = false, allowableValues = "yes,no", value = "Discard embedded page thumbnails")
                @RequestParam(value = "nothumbnails", required = false) Boolean nothumbnails,
            @ApiParam(name="pages", required = false, defaultValue = "*", value = "Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.")
                @RequestParam(value = "pages", required = false) String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password,
            @ApiParam(name="vscale", required = false, value = "Vertical scale of the image to be modified. Valid values are vscale>0")
                @RequestParam(value = "vscale", required = false) Double vscale

    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        return executeJob(file, null, algo, hscale, noattachments, nobookmarks, nocomments, nofonts, nojavascripts, nolinks, nometadata, nothumbnails, pages, password, vscale);
    }



    private ResponseEntity executeJob(
            MultipartFile file
            , String documentId
            , String algo
            , Double hscale
            , Boolean noattachments
            , Boolean nobookmarks
            , Boolean nocomments
            , Boolean nofonts
            , Boolean nojavascripts
            , Boolean nolinks
            , Boolean nometadata
            , Boolean nothumbnails
            , String pages
            , String password
            , Double vscale
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        //todo: validate algo

        CFPdfJob job = new CFPdfJob();
        job.setAction("optimize");

        //file
        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }


        job.setPages(pages==null?"*":pages);
        if( algo != null ) job.setAlgo(algo);
        if( hscale != null ) job.setHScale(hscale);
        if( noattachments != null ) job.setNoAttachments(noattachments);
        if( nobookmarks != null ) job.setNoBookmarks(nobookmarks);
        if( nocomments != null ) job.setNoComments(nocomments);
        if( nofonts != null ) job.setNoFonts(nofonts);
        if( nojavascripts != null ) job.setNoJavaScripts(nojavascripts);
        if( nolinks != null ) job.setNoLinks(nolinks);
        if( nometadata != null ) job.setNoMetadata(nometadata);
        if( nothumbnails != null ) job.setNoThumbnails(nothumbnails);
        if( password != null ) job.setPassword(password);
        if( vscale != null ) job.setVScale(vscale);

        Future<Map> future = gateway.optimizePdf(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }

}
