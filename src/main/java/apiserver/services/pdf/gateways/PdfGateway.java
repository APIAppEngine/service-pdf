package apiserver.services.pdf.gateways;


import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;
import apiserver.services.pdf.gateways.jobs.AddFooterPdfJob;
import apiserver.services.pdf.gateways.jobs.AddHeaderPdfJob;
import apiserver.services.pdf.gateways.jobs.DDXPdfResult;
import apiserver.services.pdf.gateways.jobs.DeletePdfPagesResult;
import apiserver.services.pdf.gateways.jobs.ExtractImageResult;
import apiserver.services.pdf.gateways.jobs.ExtractTextResult;
import apiserver.services.pdf.gateways.jobs.MergePdfResult;
import apiserver.services.pdf.gateways.jobs.OptimizePdfResult;
import apiserver.services.pdf.gateways.jobs.PdfGetInfoResult;
import apiserver.services.pdf.gateways.jobs.PdfSetInfoResult;
import apiserver.services.pdf.gateways.jobs.ThumbnailPdfResult;
import apiserver.services.pdf.gateways.jobs.WatermarkPdfResult;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by mnimer on 4/13/14.
 */
public interface PdfGateway
{
    Future<Map> mergePdf(MergePdfResult args);

    Future<Map> optimizePdf(OptimizePdfResult args);

    Future<Map> processDDX(DDXPdfResult args);

    Future<Map> extractText(ExtractTextResult args);

    Future<Map> extractImage(ExtractImageResult args);

    Future<Map> addFooterToPdf(CFPdfJob args);

    Future<Map> addHeaderToPdf(CFPdfJob args);

    Future<Map> removeHeaderFooter(CFPdfJob args);

    Future<Map> pdfGetInfo(PdfGetInfoResult args);

    Future<Map> pdfSetInfo(PdfSetInfoResult args);

    Future<Map> deletePages(DeletePdfPagesResult args);

    Future<Map> protectPdf(CFPdfJob args);

    Future<Map> thumbnailGenerator(ThumbnailPdfResult job);

    Future<Map> transformPdf(CFPdfJob job);

    Future<Map> addWatermarkToPdf(WatermarkPdfResult job);

    Future<Map> removeWatermarkFromPdf(WatermarkPdfResult job);
}
