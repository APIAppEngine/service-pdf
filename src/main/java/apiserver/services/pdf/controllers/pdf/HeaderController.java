package apiserver.services.pdf.controllers.pdf;

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
 * Created by mnimer on 4/16/14.
 */
@Controller
@RestController
@Api(value = "/api/v1/pdf", description = "[PDF]")
@RequestMapping("/api/v1/pdf")
public class HeaderController
{
    @Qualifier("addPdfHeaderApiGateway")
    @Autowired
    public PdfGateway headerGateway;

    private  @Value("${defaultReplyTimeout}")  Integer defaultTimeout;


    /**
     *
     * @param file
     * @param text Text to include in header
     * @param pages Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.
     * @param align Aligns the header and footer in PDF. left,right,center
     * @param bottomMargin
     * @param leftMargin
     * @param rightMargin
     * @param numberFormat  Specify the numbering format for PDF pages in the footer. lowercaseroman, numeric, uppercaseroman
     * @param opacity   Opacity of the watermark. Valid values are integers in the range 0 (transparent) through 10 (opaque).
     * @param showOnPrint
     * @param password  Owner or user password of the source PDF document, if the document is password-protected.
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception

     */
    @ApiOperation(value = "Add Header to PDF pages")
    @RequestMapping(value = "/modify/header", method = RequestMethod.POST)
    public ResponseEntity<byte[]> addHeader(
            @ApiParam(name="file", required = true) 
                @RequestParam(value = "file") MultipartFile file,
            @ApiParam(name="align", required = false, allowableValues = "left,right,center")
                @RequestParam(value = "align", required = false) String align,
            @ApiParam(name="bottomMargin", required = false, value = "value of the header bottom marign")
                @RequestParam(value = "bottomMargin", required = false) Integer bottomMargin,
            @ApiParam(name="leftMargin", required = false, value = "value of the header left marign")
                @RequestParam(value = "leftMargin", required = false) Integer leftMargin,
            @ApiParam(name="numberFormat", required = false, allowableValues = "lowercaseroman, numeric, uppercaseroman", value = "used with either _PAGENUMBER or _LASTPAGENUMBER")
                @RequestParam(value = "numberFormat", required = false) String numberFormat,
            @ApiParam(name="opacity", required = false, value = "header opacity")
                @RequestParam(value = "opacity", required = false) Double opacity,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer")
                @RequestParam(value = "pages", required = false) String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password,
            @ApiParam(name="rightMargin", required = false, value = "value of the header right margin")
                @RequestParam(value = "rightMargin", required = false) Integer rightMargin,
            @ApiParam(name="showOnPrint", required = false, defaultValue = "false", value = "Specify whether to print the watermark with the PDF document")
                @RequestParam(value = "showOnPrint", required = false) Boolean showOnPrint,
            @ApiParam(name="topMargin", required = false, value = "value of the header top marign")
                @RequestParam(value = "topMargin", required = false) Integer topMargin,
            @ApiParam(name="text", required = true, value = "_PAGELABEL: add current page label|_LASTPAGELABEL: add last page label|PAGENUMBER: add current page number|_LASTPAGENUMBER: add last page number|Text for the header. You can also add a normal text string.")
                @RequestParam(value = "text", required = false) String text
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeAddHeaderJob(file, null, align, bottomMargin, leftMargin, numberFormat, opacity, pages, password, rightMargin, showOnPrint, text);
    }






    private ResponseEntity executeAddHeaderJob(
            MultipartFile file
            , String documentId
            , String align
            , Integer bottomMargin
            , Integer leftMargin
            , String numberFormat
            , Double opacity
            , String pages
            , String password
            , Integer rightMargin
            , Boolean showOnPrint
            , String text
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        //todo validate ALIGN, OPACITY (0-10)


        CFPdfJob job = new CFPdfJob();
        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }
        job.setAction("addheader");
        job.setPages(pages==null?"*":pages);
        if( align != null ) job.setAlign(align);
        if( bottomMargin != null ) job.setBottomMargin(bottomMargin);
        if( leftMargin != null ) job.setLeftMargin(leftMargin);
        if( numberFormat != null ) job.setNumberFormat(numberFormat);
        if( opacity != null ) job.setOpacity(opacity);
        if( password != null ) job.setPassword(password);
        if( rightMargin != null ) job.setRightMargin(rightMargin);
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( text != null ) job.setText(text);


        Future<Map> future = headerGateway.addHeaderToPdf(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }





}
