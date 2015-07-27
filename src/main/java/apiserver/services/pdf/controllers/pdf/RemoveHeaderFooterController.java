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
@Api(value = "/api/v1/pdf", description = "[PDF]")
@RequestMapping("/api/v1/pdf")
public class RemoveHeaderFooterController
{
    @Qualifier("pdfRemoveHeaderFooterApiGateway")
    @Autowired
    public PdfGateway headerFooterGateway;

    private  @Value("${defaultReplyTimeout}")  Integer defaultTimeout;





    /**
     * Remove Header & Footer from pdf pages
     * @param file    Pdf document
     * @param pages Page or pages in the source PDF document on which to perform the action. You can specify multiple pages and page ranges as follows: “1,6–9,56–89,100, 110–120”.
     * @param password  Owner or user password of the source PDF document, if the document is password-protected.
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws IOException
     * @throws Exception
     */
    @ApiOperation(value = " Remove Header & Footer from pdf pages")
    @RequestMapping(value = "/modify/headerfooter/remove", method = RequestMethod.POST)
    public ResponseEntity removeHeaderFooterFromPdf(
            @ApiParam(name="file", required = true)
                @RequestParam(value = "file") MultipartFile file,
            @ApiParam(name="pages", required = false, value = "page or pages to add the footer")
                @RequestParam(value = "pages", required = false, defaultValue = "*") String pages,
            @ApiParam(name="password", required = false, value = "Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeRemoveHeaderFooterJob(file, null, pages, password);
    }






    private ResponseEntity<byte[]> executeRemoveHeaderFooterJob(
            MultipartFile file
            , String documentId
            , String pages
            , String password
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfJob job = new CFPdfJob();
        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }
        job.setAction("removeheaderfooter");
        job.setPages(pages==null?"*":pages);
        if( password != null ) job.setPassword(password);


        Future<Map> future = headerFooterGateway.removeHeaderFooter(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }
}
