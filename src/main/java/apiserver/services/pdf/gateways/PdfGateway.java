package apiserver.services.pdf.gateways;


import apiserver.services.pdf.gateways.jobs.*;

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

    Future<Map> addFooterToPdf(AddFooterPdfResult args);

    Future<Map> addHeaderToPdf(AddHeaderPdfResult args);

    Future<Map> removeHeaderFooter(RemoveHeaderFooterResult args);

    Future<Map> pdfGetInfo(PdfGetInfoResult args);

    Future<Map> pdfSetInfo(PdfSetInfoResult args);

    Future<Map> deletePages(DeletePdfPagesResult args);

    Future<Map> protectPdf(SecurePdfResult args);

    Future<Map> thumbnailGenerator(ThumbnailPdfResult job);

    Future<Map> transformPdf(SecurePdfResult job);

    Future<Map> addWatermarkToPdf(WatermarkPdfResult job);

    Future<Map> removeWatermarkFromPdf(WatermarkPdfResult job);
}
