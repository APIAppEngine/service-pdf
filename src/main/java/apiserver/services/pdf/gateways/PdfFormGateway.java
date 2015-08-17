package apiserver.services.pdf.gateways;

import apiserver.services.pdf.gateways.jobs.CFPdfFormJob;
import org.springframework.messaging.handler.annotation.Header;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by mnimer on 4/13/14.
 */
public interface PdfFormGateway
{
    Future<Map> extractPdfForm(CFPdfFormJob args, @Header("format") String format);
    Future<Map> populatePdfForm(CFPdfFormJob args);
}
