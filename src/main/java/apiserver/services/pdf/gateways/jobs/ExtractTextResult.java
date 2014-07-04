package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.services.StringResult;
import apiserver.services.cache.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;

/**
 * Created by mnimer on 4/17/14.
 */
public class ExtractTextResult extends CFPdfJob implements StringResult
{

    private String documentId;
    private Document file;
    private String result;


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


    public String getResult()
    {
        return result;
    }


    public void setResult(String results)
    {
        this.result = results;
    }
}
