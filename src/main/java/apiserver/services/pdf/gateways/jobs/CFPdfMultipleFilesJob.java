package apiserver.services.pdf.gateways.jobs;


import apiserver.ApiServerConstants;
import apiserver.model.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mnimer on 4/13/14.
 */
public class CFPdfMultipleFilesJob extends CFPdfJob
{
    private Document[] documents;


    public Document[] getDocuments()
    {
        return documents;
    }


    public void setDocuments(Document[] documents)
    {
        this.documents = documents;
    }


    @Override public Map getArguments()
    {
        Map args = new HashMap();
        args.put(ApiServerConstants.FILES, getDocuments() );
        args.put(ApiServerConstants.ACTION, this.getAction() );
        args.put(ApiServerConstants.OPTIONS, getOptions());
        return args;
    }
}