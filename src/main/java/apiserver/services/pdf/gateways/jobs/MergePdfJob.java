package apiserver.services.pdf.gateways.jobs;

import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;
import apiserver.core.connectors.coldfusion.services.BinaryJob;

/**
 * Created by mnimer on 4/13/14.
 */
public class MergePdfJob extends CFPdfJob implements BinaryJob
{

    private String documentId;
    private Document file1;
    private Document file2;
    private byte[] pdfBytes;


    public String getDocumentId()
    {
        return documentId;
    }


    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }


    public Document getFile1()
    {
        return file1;
    }


    public void setFile1(Document file)
    {
        this.file1 = file;
    }


    public Document getFile2()
    {
        return file2;
    }


    public void setFile2(Document file2)
    {
        this.file2 = file2;
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
