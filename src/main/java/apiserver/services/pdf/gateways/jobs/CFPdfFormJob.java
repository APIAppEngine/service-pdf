package apiserver.services.pdf.gateways.jobs;

import apiserver.ApiServerConstants;
import apiserver.jobs.GetDocumentJob;
import apiserver.jobs.IProxyJob;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mnimer on 4/13/14.
 */
public class CFPdfFormJob extends GetDocumentJob implements IProxyJob, Serializable
{
    private static final String OVERWRITEDATA = "overwriteDate";
    private static final String PASSWORD = "password";
    private static final String XMLDATA = "xmlData";
    private static final String FORMAT = "format";

    private ResponseEntity httpResponse;

    private String action;

    // Map of options to pass through, will be set with an AttributeCollection argument.
    private Map options = new HashMap();


    public String getAction()
    {
        return action;
    }


    public void setAction(String action)
    {
        this.action = action;
    }


    /**
     * Options
     */
    public Map getOptions()
    {
        return this.options;
    }

    public void setOptions(Map _options)
    {
        this.options = _options;
    }


    public void setOverwriteData(Boolean overwriteData)
    {
        getOptions().put(OVERWRITEDATA, overwriteData);
    }

    public void setPassword(String password)
    {
        this.getOptions().put(PASSWORD, password);
    }


    public void setFormat(String format)
    {
        getOptions().put(FORMAT, format.toLowerCase().trim());
    }


    public void setFields(String fields)
    {
        getOptions().put(XMLDATA, fields.trim());
    }




    public ResponseEntity getHttpResponse()
    {
        return httpResponse;
    }


    public void setHttpResponse(ResponseEntity httpResponse)
    {
        this.httpResponse = httpResponse;
    }


    @Override public Map getArguments()
    {
        Map args = new HashMap();
        args.put(ApiServerConstants.ACTION, this.getAction() );
        args.put(ApiServerConstants.FILE, getDocument() );
        args.put(ApiServerConstants.OPTIONS, getOptions());
        return args;
    }
}
