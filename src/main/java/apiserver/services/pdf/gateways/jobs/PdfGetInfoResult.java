package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;
import apiserver.core.connectors.coldfusion.services.ObjectResult;
import apiserver.services.cache.model.Document;

/**
 * Created by mnimer on 4/17/14.
 */
public class PdfGetInfoResult extends CFPdfJob implements ObjectResult
{

    private String documentId;
    private Document file;
    private Object result;


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


    public Object getResult()
    {
        return result;
    }


    public void setResult(Object results)
    {
        this.result = result;
    }
}
