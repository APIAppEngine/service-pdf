package apiserver.services.pdf.gateways;


import apiserver.services.pdf.gateways.jobs.AddFooterPdfResult;
import apiserver.services.pdf.gateways.jobs.AddHeaderPdfResult;
import apiserver.services.pdf.gateways.jobs.DDXPdfResult;
import apiserver.services.pdf.gateways.jobs.DeletePdfPagesResult;
import apiserver.services.pdf.gateways.jobs.ExtractImageResult;
import apiserver.services.pdf.gateways.jobs.ExtractTextResult;
import apiserver.services.pdf.gateways.jobs.MergePdfResult;
import apiserver.services.pdf.gateways.jobs.OptimizePdfResult;
import apiserver.services.pdf.gateways.jobs.PdfGetInfoResult;
import apiserver.services.pdf.gateways.jobs.PdfSetInfoResult;
import apiserver.services.pdf.gateways.jobs.RemoveHeaderFooterResult;
import apiserver.services.pdf.gateways.jobs.SecurePdfResult;
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
