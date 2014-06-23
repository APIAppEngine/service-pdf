package apiserver.services.pdf.gateways;


import apiserver.services.pdf.gateways.jobs.*;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by mnimer on 4/13/14.
 */
public interface PdfGateway
{
    Future<Map> mergePdf(MergePdfJob args);

    Future<Map> optimizePdf(OptimizePdfJob args);

    Future<Map> processDDX(DDXPdfJob args);

    Future<Map> extractText(ExtractTextJob args);

    Future<Map> extractImage(ExtractImageJob args);

    Future<Map> addFooterToPdf(AddFooterPdfJob args);

    Future<Map> addHeaderToPdf(AddHeaderPdfJob args);

    Future<Map> removeHeaderFooter(RemoveHeaderFooterJob args);

    Future<Map> pdfGetInfo(PdfGetInfoJob args);

    Future<Map> pdfSetInfo(PdfSetInfoJob args);

    Future<Map> deletePages(DeletePdfPagesJob args);

    Future<Map> protectPdf(SecurePdfJob args);

    Future<Map> thumbnailGenerator(ThumbnailPdfJob job);

    Future<Map> transformPdf(SecurePdfJob job);

    Future<Map> addWatermarkToPdf(WatermarkPdfJob job);

    Future<Map> removeWatermarkFromPdf(WatermarkPdfJob job);
}
