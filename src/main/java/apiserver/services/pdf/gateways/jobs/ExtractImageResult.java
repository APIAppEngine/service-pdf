package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;
import apiserver.core.connectors.coldfusion.services.CollectionResult;
import apiserver.services.cache.model.Document;

import java.util.Collection;

/**
 * Created by mnimer on 4/17/14.
 */
public class ExtractImageResult extends CFPdfJob implements CollectionResult
{

    private String documentId;
    private Document file;
    private Collection result;


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


    public Collection getResult()
    {
        return result;
    }


    public void setResult(Collection result)
    {
        this.result = result;
    }


}
