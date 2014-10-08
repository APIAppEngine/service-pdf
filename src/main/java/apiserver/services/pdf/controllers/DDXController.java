package apiserver.services.pdf.controllers;

import apiserver.MimeType;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.services.BinaryResult;
import apiserver.services.cache.model.Document;
import apiserver.services.pdf.gateways.PdfGateway;
import apiserver.services.pdf.gateways.jobs.DDXPdfResult;
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
 * Created by mnimer on 4/16/14.
 */
@Controller
@RestController
@Api(value = "/pdf", description = "[PDF]")
@RequestMapping("/pdf")
public class DDXController
{

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;

    @Qualifier("processDDXGateway")
    @Autowired public PdfGateway gateway;


    /**
     * Apply a DDX file to a PDF for advanced manipulation
     *
     * The supported DDX Elements are:
     * About,Author,Background,Center,DatePattern,DDX,DocumentInformation,DocumentText,Footer,Header,InitialViewProfile,Keyword,Keywords,Left,MasterPassword,Metadata,NoBookmarks,OpenPassword,PageLabel,Password,PasswordAccessProfile,PasswordEncryptionProfile,PDF (except: certification and mergeLayers ),PDFGroup,Permissions,Right,StyledText,StyleProfile,Subject,TableOfContents,TableOfContentsEntryPattern,TableOfContentsPagePattern,Title,Watermark
     *
     * The excluded DDX Elements are:
     * ArtBox,AttachmentAppearance,Bookmarks,BlankPage,BleedBox,Comments,Description,FileAttachments,FilenameEncoding,LinkAlias,Links,NoBackgrounds,NoComments,NoFileAttchments,NoFooters,NoForms,NoHeaders,NoLinks,NoPageLabels,NoThumbnails,NoWatermarks,NoXFA,PageMargins,PageSize,PageRotation,PageOverlay,PageUnderlay,PDFsFromBookmarks,Transform,TrimBox
     *
     * @param file
     * @param DDX
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Use DDX instructions to manipulate PDF documents")
    @RequestMapping(value = "/modify/ddx", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> processDDX(
            @ApiParam(name="file", required = true)
                @RequestPart("file") MultipartFile file,
            @ApiParam(name="ddx", required = true, value = "XML string with DDX instructions")
                @RequestParam("ddx") String DDX
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        DDXPdfResult job = new DDXPdfResult();
        job.setFile(new Document(file));
        job.setDdx(DDX);


        Future<Map> future = gateway.processDDX(job);
        BinaryResult payload = (BinaryResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getResult();
        String contentType = MimeType.pdf.contentType;
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }



    /**
     * apply a DDX file to a cached PDF for advanced manipulation
     * @param documentId
     * @param DDX
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Use a DDX file for advanced manipulation")
    @RequestMapping(value = "/modify/{documentId}/ddx", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> processCachedPdfDDX(
            @ApiParam(name="documentId", required = true) @RequestPart("documentId") String documentId,
            @ApiParam(name="ddx", required = true) @RequestParam("ddx") String DDX
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        DDXPdfResult job = new DDXPdfResult();
        job.setDocumentId(documentId);
        job.setDdx(DDX);


        Future<Map> future = gateway.processDDX(job);
        DDXPdfResult payload = (DDXPdfResult)future.get(defaultTimeout, TimeUnit.MILLISECONDS);


        byte[] fileBytes = payload.getResult();
        String contentType = MimeType.pdf.contentType;
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }

}
