package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.services.BinaryResult;
import apiserver.services.cache.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;

/**
 * Created by mnimer on 4/14/14.
 */
public class OptimizePdfResult extends CFPdfJob implements BinaryResult
{

    private String documentId;
    private Document file;
    private byte[] pdfBytes;


    public String getDocumentId()
    {
        return documentId;
    }


    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }


    public Document getFile()
    {
        return file;
    }


    public void setFile(Document file)
    {
        this.file = file;
    }


    public byte[] getResult()
    {
        return pdfBytes;
    }


    public void setResult(byte[] pdfBytes)
    {
        this.pdfBytes = pdfBytes;
    }
}
