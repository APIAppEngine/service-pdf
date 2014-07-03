package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.services.BinaryResult;
import apiserver.services.cache.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;

/**
 * Created by mnimer on 5/4/14.
 */
public class WatermarkPdfResult extends CFPdfJob implements BinaryResult
{

    private String documentId;
    private Document file;
    private byte[] pdfBytes;
    private Boolean foreground;


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


    public void setForeground(Boolean foreground)
    {
        this.foreground = foreground;
    }


    public Boolean getForeground()
    {
        return foreground;
    }
}
