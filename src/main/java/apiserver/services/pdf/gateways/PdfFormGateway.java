package apiserver.services.pdf.gateways;

import apiserver.services.pdf.gateways.jobs.ExtractPdfFormJob;
import apiserver.services.pdf.gateways.jobs.PopulatePdfFormJob;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by mnimer on 4/13/14.
 */
public interface PdfFormGateway
{
    Future<Map> extractPdfForm(ExtractPdfFormJob args);
    Future<Map> populatePdfForm(PopulatePdfFormJob args);
}
