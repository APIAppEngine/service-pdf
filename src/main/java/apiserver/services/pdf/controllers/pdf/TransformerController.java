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
 * Created by mnimer on 4/18/14.
 */
@Controller
@RestController
@Api(value = "/api/v1/pdf", description = "[PDF]")
@RequestMapping("/api/v1/pdf")
public class TransformerController
{
    @Qualifier("transformPdfGateway")
    @Autowired
    public PdfGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * Transform pages
     * @param file
     * @param password
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     * @throws Exception
     */
    @ApiOperation(value = "Transform pages in pdf")
    @RequestMapping(value = "/transform", method = RequestMethod.POST)
    public ResponseEntity<byte[]> transformPdf(
            @ApiParam(name = "file", required = true)
                @RequestParam("file") MultipartFile file,
            @ApiParam(name = "hScale", required = false, value="Horizontal scale of the image to be modified. Valid values are hscale<1")
                @RequestParam(value = "hScale", required = false, defaultValue = "1") Double hScale,
            @ApiParam(name = "password", required = false, value="Owner or user password of the source PDF document, if the document is password-protected.")
                @RequestParam(value = "password", required = false) String password,
            @ApiParam(name = "position", required = false, value="Position on the page where the watermark is placed. The position represents the top-left corner of the watermark. Specify the xand y coordinates; for example “50,30”.")
                @RequestParam(value = "position", required = false) String position,
            @ApiParam(name = "rotation", required = false, allowableValues = "0, 90, 180, 270", value = "Degree of rotation")
                @RequestParam(value = "rotation", required = false) Integer rotation,
            @ApiParam(name = "vScale", required = false, value = "Vertical scale of the image to be modified. Valid values are vscale>0")
                @RequestParam(value = "vScale", required = false, defaultValue = "1") Double vScale
    ) throws InterruptedException, ExecutionException, TimeoutException, IOException, Exception
    {
        return executeJob(file, null, password, hScale, vScale, position, rotation);
    }






    private ResponseEntity<byte[]> executeJob(
            MultipartFile file
            , String documentId
            , String password
            , Double hScale
            , Double vScale
            , String position
            , Integer rotation
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        CFPdfJob job = new CFPdfJob();
        job.setAction("transform");
        //file
        if( file != null ) {
            job.setDocument(new Document(file));
        }else{
            job.setDocumentId(documentId);
        }
        if( password != null ) job.setPassword(password);
        if( hScale != null ) job.setHScale(hScale);
        if( vScale != null ) job.setVScale(vScale);
        if( position != null ) job.setPosition(position);
        if( rotation != null ) job.setRotation(rotation);

        Future<Map> future = gateway.transformPdf(job);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }

}
