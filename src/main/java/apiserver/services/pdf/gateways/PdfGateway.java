package apiserver.services.pdf.gateways;


import apiserver.services.pdf.gateways.jobs.CFPdfJob;
import apiserver.services.pdf.gateways.jobs.CFPdfMultipleFilesJob;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by mnimer on 4/13/14.
 */
public interface PdfGateway
{


    Future<Map> addFooterToPdf(CFPdfJob args);
    Future<Map> addHeaderToPdf(CFPdfJob args);
    Future<Map> addWatermarkToPdf(CFPdfJob job);
    Future<Map> deletePages(CFPdfJob args);
    Future<Map> extractImage(CFPdfJob args);
    Future<Map> extractText(CFPdfJob args);
    Future<Map> mergePdf(CFPdfMultipleFilesJob args);
    Future<Map> optimizePdf(CFPdfJob args);
    Future<Map> pdfGetInfo(CFPdfJob args);
    Future<Map> pdfSetInfo(CFPdfJob args);
    Future<Map> processDDX(CFPdfJob args);
    Future<Map> protectPdf(CFPdfJob args);
    Future<Map> thumbnailGenerator(CFPdfJob job);
    Future<Map> transformPdf(CFPdfJob job);
    Future<Map> removeHeaderFooter(CFPdfJob args);
    Future<Map> removeWatermarkFromPdf(CFPdfJob job);

    //todo
    //Future<Map> signPdf(CFPdfJob job);

    //todo
    //Future<Map> unsignPdf(CFPdfJob job);

    //todo
    //Future<Map> validatesignature(CFPdfJob job);
    
}
