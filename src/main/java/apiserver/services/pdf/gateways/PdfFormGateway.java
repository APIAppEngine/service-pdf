package apiserver.services.pdf.gateways;

import apiserver.services.pdf.gateways.jobs.ExtractPdfFormResult;
import apiserver.services.pdf.gateways.jobs.PopulatePdfFormResult;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by mnimer on 4/13/14.
 */
public interface PdfFormGateway
{
    Future<Map> extractPdfForm(ExtractPdfFormResult args);
    Future<Map> populatePdfForm(PopulatePdfFormResult args);
}
