package apiserver.services.pdf.controllers;

import apiserver.MimeType;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.services.BinaryJob;
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.AddFooterPdfJob;
import apiserver.services.pdf.gateways.jobs.AddHeaderPdfJob;
import apiserver.services.pdf.gateways.jobs.RemoveHeaderFooterJob;
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
 * Created by mnimer on 4/16/14.
 */
@Controller
@RestController
@Api(value = "/pdf", description = "[PDF]")
@RequestMapping("/pdf")
public class HeaderFooterController
{
    @Qualifier("addPdfHeaderApiGateway")
    @Autowired
    public PdfGateway headerGateway;

    @Qualifier("addPdfFooterApiGateway")
    @Autowired
    public PdfGateway footerGateway;

    @Qualifier("pdfRemoveHeaderFooterApiGateway")
    @Autowired
    public PdfGateway headerFooterGateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


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
    @RequestMapping(value = "/modify/header", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> addHeader(
            @ApiParam(name="file", required = true) 
                @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name="text", required = false, value = "_PAGELABEL: add current page label|_LASTPAGELABEL: add last page label|PAGENUMBER: add current page number|_LASTPAGENUMBER: add last page number|Text for the header. You can also add a normal text string.") 
                @RequestPart(value = "text", required = false) String text,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer") 
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="align", required = false, allowableValues = "left,right,center") 
                @RequestPart(value = "align", required = false) String align,
            @ApiParam(name="bottomMargin", required = false, value = "value of the header bottom marign") 
                @RequestPart(value = "bottomMargin", required = false) Integer bottomMargin,
            @ApiParam(name="topMargin", required = false, value = "value of the header top marign") 
                @RequestPart(value = "topMargin", required = false) Integer topMargin,
            @ApiParam(name="leftMargin", required = false, value = "value of the header left marign") 
                @RequestPart(value = "leftMargin", required = false) Integer leftMargin,
            @ApiParam(name="rightMargin", required = false, value = "value of the header right margin") 
                @RequestPart(value = "rightMargin", required = false) Integer rightMargin,
            @ApiParam(name="numberFormat", required = false, allowableValues = "lowercaseroman, numeric, uppercaseroman", value = "used with either _PAGENUMBER or _LASTPAGENUMBER") 
                @RequestPart(value = "numberFormat", required = false) String numberFormat,
            @ApiParam(name="opacity", required = false, value = "header opacity") 
                @RequestPart(value = "opacity", required = false) Integer opacity,
            @ApiParam(name="showOnPrint", required = false, defaultValue = "false", value = "Specify whether to print the watermark with the PDF document") 
                @RequestPart(value = "showOnPrint", required = false) Boolean showOnPrint,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.") 
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        AddHeaderPdfJob job = new AddHeaderPdfJob();
        job.setFile(new Document(file));


        if( text != null ) job.setText(text);
        if( pages != null ) job.setPages(pages);
        if( align != null ) job.setAlign(align);
        if( bottomMargin != null ) job.setBottomMargin(bottomMargin);
        if( leftMargin != null ) job.setLeftMargin(leftMargin);
        if( rightMargin != null ) job.setRightMargin(rightMargin);
        if( numberFormat != null ) job.setNumberFormat(numberFormat);
        if( opacity != null ) job.setOpacity(opacity.doubleValue());
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( password != null ) job.setPassword(password);


        Future<Map> future = headerGateway.addHeaderToPdf(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

    /**
     * Add Footer to PDF pages
     * @param documentId    ID of cached document
     * @param text Text to include in footer
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
    @ApiOperation(value = "Add Header to cached PDF pages")
    @RequestMapping(value = "/modify/{documentId}/header", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> addHeaderToCachedPdf(
            @ApiParam(name="documentId", required = true) 
                @RequestPart("documentId") String documentId,
            @ApiParam(name="text", required = false, value = "_PAGELABEL: add current page label|_LASTPAGELABEL: add last page label|PAGENUMBER: add current page number|_LASTPAGENUMBER: add last page number|Text for the header. You can also add a normal text string.") 
                @RequestPart(value = "text", required = false) String text,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer") 
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="align", required = false, allowableValues = "left,right,center") 
                @RequestPart(value = "align", required = false) String align,
            @ApiParam(name="bottomMargin", required = false, value = "value of the header bottom marign") 
                @RequestPart(value = "bottomMargin", required = false) Integer bottomMargin,
            @ApiParam(name="topMargin", required = false, value = "value of the header top marign") 
                @RequestPart(value = "topMargin", required = false) Integer topMargin,
            @ApiParam(name="leftMargin", required = false, value = "value of the header left marign") 
                @RequestPart(value = "leftMargin", required = false) Integer leftMargin,
            @ApiParam(name="rightMargin", required = false, value = "value of the header right margin") 
                @RequestPart(value = "rightMargin", required = false) Integer rightMargin,
            @ApiParam(name="numberFormat", required = false, allowableValues = "lowercaseroman, numeric, uppercaseroman", value = "used with either _PAGENUMBER or _LASTPAGENUMBER") 
                @RequestPart(value = "numberFormat", required = false) String numberFormat,
            @ApiParam(name="opacity", required = false, value = "header opacity") 
                @RequestPart(value = "opacity", required = false) Integer opacity,
            @ApiParam(name="showOnPrint", required = false, defaultValue = "false", value = "Specify whether to print the watermark with the PDF document") 
                @RequestPart(value = "showOnPrint", required = false) Boolean showOnPrint,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.") 
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        AddHeaderPdfJob job = new AddHeaderPdfJob();
        job.setDocumentId(documentId);

        if( text != null ) job.setText(text);
        if( pages != null ) job.setPages(pages);
        if( align != null ) job.setAlign(align);
        if( bottomMargin != null ) job.setBottomMargin(bottomMargin);
        if( leftMargin != null ) job.setLeftMargin(leftMargin);
        if( rightMargin != null ) job.setRightMargin(rightMargin);
        if( numberFormat != null ) job.setNumberFormat(numberFormat);
        if( opacity != null ) job.setOpacity(opacity.doubleValue());
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( password != null ) job.setPassword(password);


        Future<Map> future = headerGateway.addHeaderToPdf(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }







    /**
     *
     * @param file
     * @param text Text to include in footer
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
    @ApiOperation(value = "Add Footer to PDF pages")
    @RequestMapping(value = "/modify/footer", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> addFooter(
            @ApiParam(name="file", required = true) 
                @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name="text", required = false, value = "_PAGELABEL: add current page label|_LASTPAGELABEL: add last page label|PAGENUMBER: add current page number|_LASTPAGENUMBER: add last page number|Text for the header. You can also add a normal text string.") 
                @RequestPart(value = "text", required = false) String text,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer") 
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="align", required = false, allowableValues = "left,right,center") 
                @RequestPart(value = "align", required = false) String align,
            @ApiParam(name="bottomMargin", required = false, value = "value of the header bottom marign") 
                @RequestPart(value = "bottomMargin", required = false) Integer bottomMargin,
            @ApiParam(name="topMargin", required = false, value = "value of the header top marign") 
                @RequestPart(value = "topMargin", required = false) Integer topMargin,
            @ApiParam(name="leftMargin", required = false, value = "value of the header left marign") 
                @RequestPart(value = "leftMargin", required = false) Integer leftMargin,
            @ApiParam(name="rightMargin", required = false, value = "value of the header right margin") 
                @RequestPart(value = "rightMargin", required = false) Integer rightMargin,
            @ApiParam(name="numberFormat", required = false, allowableValues = "lowercaseroman, numeric, uppercaseroman", value = "used with either _PAGENUMBER or _LASTPAGENUMBER") 
                @RequestPart(value = "numberFormat", required = false) String numberFormat,
            @ApiParam(name="opacity", required = false, value = "header opacity") 
                @RequestPart(value = "opacity", required = false) Integer opacity,
            @ApiParam(name="showOnPrint", required = false, defaultValue = "false", value = "Specify whether to print the watermark with the PDF document") 
                @RequestPart(value = "showOnPrint", required = false) Boolean showOnPrint,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.") 
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        AddFooterPdfJob job = new AddFooterPdfJob();
        job.setFile(new Document(file));

        if( text != null ) job.setText(text);
        if( pages != null ) job.setPages(pages);
        if( align != null ) job.setAlign(align);
        if( bottomMargin != null ) job.setBottomMargin(bottomMargin);
        if( leftMargin != null ) job.setLeftMargin(leftMargin);
        if( rightMargin != null ) job.setRightMargin(rightMargin);
        if( numberFormat != null ) job.setNumberFormat(numberFormat);
        if( opacity != null ) job.setOpacity(opacity.doubleValue());
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( password != null ) job.setPassword(password);


        Future<Map> future = footerGateway.addFooterToPdf(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }




    /**
     * Add Footer to PDF pages
     * @param documentId    ID of cached document
     * @param text Text to include in footer
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
    @ApiOperation(value = "Add Footer to cached PDF pages")
    @RequestMapping(value = "/modify/{documentId}/footer", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> addFooterToCachedPdf(
            @ApiParam(name="documentId", required = true) 
                @RequestPart("documentId") String documentId,
            @ApiParam(name="text", required = false, value = "_PAGELABEL: add current page label|_LASTPAGELABEL: add last page label|PAGENUMBER: add current page number|_LASTPAGENUMBER: add last page number \\text for the header. You can also add a normal text string.") 
                @RequestPart(value = "text", required = false) String text,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer") 
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="align", required = false, allowableValues = "left,right,center") 
                @RequestPart(value = "align", required = false) String align,
            @ApiParam(name="bottomMargin", required = false, value = "value of the header bottom marign") 
                @RequestPart(value = "bottomMargin", required = false) Integer bottomMargin,
            @ApiParam(name="topMargin", required = false, value = "value of the header top marign") 
                @RequestPart(value = "topMargin", required = false) Integer topMargin,
            @ApiParam(name="leftMargin", required = false, value = "value of the header left marign") 
                @RequestPart(value = "leftMargin", required = false) Integer leftMargin,
            @ApiParam(name="rightMargin", required = false, value = "value of the header right margin") 
                @RequestPart(value = "rightMargin", required = false) Integer rightMargin,
            @ApiParam(name="numberFormat", required = false, allowableValues = "lowercaseroman, numeric, uppercaseroman", value = "used with either _PAGENUMBER or _LASTPAGENUMBER") 
                @RequestPart(value = "numberFormat", required = false) String numberFormat,
            @ApiParam(name="opacity", required = false, value = "header opacity") 
                @RequestPart(value = "opacity", required = false) Integer opacity,
            @ApiParam(name="showOnPrint", required = false, defaultValue = "false", value = "Specify whether to print the watermark with the PDF document") 
                @RequestPart(value = "showOnPrint", required = false) Boolean showOnPrint,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.") 
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        AddFooterPdfJob job = new AddFooterPdfJob();
        job.setDocumentId(documentId);


        if( text != null ) job.setText(text);
        if( pages != null ) job.setPages(pages);
        if( align != null ) job.setAlign(align);
        if( bottomMargin != null ) job.setBottomMargin(bottomMargin);
        if( leftMargin != null ) job.setLeftMargin(leftMargin);
        if( rightMargin != null ) job.setRightMargin(rightMargin);
        if( numberFormat != null ) job.setNumberFormat(numberFormat);
        if( opacity != null ) job.setOpacity(opacity.doubleValue());
        if( showOnPrint != null ) job.setShowOnPrint(showOnPrint);
        if( password != null ) job.setPassword(password);


        Future<Map> future = footerGateway.addFooterToPdf(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }






    /**
     * Remove Header & Footer from pdf pages
     * @param file    Pdf document
     * @param pages Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.
     * @param password  Owner or user password of the source PDF document, if the document is password-protected.
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = " Remove Header & Footer from pdf pages")
    @RequestMapping(value = "/modify/headerFooter", method = RequestMethod.DELETE, produces = "application/pdf")
    public ResponseEntity<byte[]> removeHeaderFooterFromPdf(
            @ApiParam(name="file", required = true)
                @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer")
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        RemoveHeaderFooterJob job = new RemoveHeaderFooterJob();
        job.setFile(new Document(file));
        job.setPages(pages);
        if( password != null ) job.setPassword(password);


        Future<Map> future = headerFooterGateway.removeHeaderFooter(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }





    /**
     * Remove Header & Footer from pdf pages
     * @param documentId    ID of cached document
     * @param pages Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.
     * @param password  Owner or user password of the source PDF document, if the document is password-protected.
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Remove Header & Footer from cached pdf document")
    @RequestMapping(value = "/modify/{documentId}/headerFooter", method = RequestMethod.DELETE, produces = "application/pdf")
    public ResponseEntity<byte[]> removeHeaderFooterFromCachedPdf(
            @ApiParam(name="documentId", required = true)
                @RequestPart("documentId") String documentId,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer")
                @RequestPart(value = "pages", required = false) String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestPart(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        RemoveHeaderFooterJob job = new RemoveHeaderFooterJob();
        job.setDocumentId(documentId);
        job.setPages(pages);
        if( password != null ) job.setPassword(password);


        Future<Map> future = headerFooterGateway.removeHeaderFooter(job);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

}
