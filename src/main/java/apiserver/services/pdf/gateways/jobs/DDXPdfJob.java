package apiserver.services.pdf.gateways.jobs;

import apiserver.services.cache.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;
import apiserver.core.connectors.coldfusion.services.BinaryJob;

/**
 * Created by mnimer on 4/17/14.
 */
public class DDXPdfJob extends CFPdfJob implements BinaryJob
{

    private String documentId;
    private String ddx;
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


    public String getDdx()
    {
        return ddx;
    }


    public void setDdx(String ddx)
    {
        this.ddx = ddx;
    }


    public Document getFile()
    {
        return file;
    }


    public void setFile(Document file)
    {
        this.file = file;
    }


    public byte[] getPdfBytes()
    {
        return pdfBytes;
    }


    public void setPdfBytes(byte[] pdfBytes)
    {
        this.pdfBytes = pdfBytes;
    }
}
