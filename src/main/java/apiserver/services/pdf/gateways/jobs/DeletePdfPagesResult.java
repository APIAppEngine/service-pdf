package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;
import apiserver.core.connectors.coldfusion.services.BinaryResult;
import apiserver.model.Document;

/**
 * Created by mnimer on 4/7/14.
 */
public class DeletePdfPagesResult extends CFPdfJob implements BinaryResult
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
