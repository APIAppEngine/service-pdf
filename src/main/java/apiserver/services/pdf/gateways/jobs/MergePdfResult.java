package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.services.BinaryResult;
import apiserver.services.cache.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;

/**
 * Created by mnimer on 4/13/14.
 */
public class MergePdfResult extends CFPdfJob implements BinaryResult
{

    private String documentId;
    private Document[] files;

    private byte[] pdfBytes;


    public String getDocumentId()
    {
        return documentId;
    }


    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }


    public Document[] getFiles() {
        return files;
    }


    public void setFiles(Document[] files) {
        this.files = files;
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
