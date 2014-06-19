package apiserver.services.pdf.gateways.jobs;

import apiserver.services.cache.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;
import apiserver.core.connectors.coldfusion.services.BinaryJob;

/**
 * Created by mnimer on 4/13/14.
 */
public class MergePdfJob extends CFPdfJob implements BinaryJob
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


    public byte[] getPdfBytes()
    {
        return pdfBytes;
    }


    public void setPdfBytes(byte[] pdfBytes)
    {
        this.pdfBytes = pdfBytes;
    }
}
