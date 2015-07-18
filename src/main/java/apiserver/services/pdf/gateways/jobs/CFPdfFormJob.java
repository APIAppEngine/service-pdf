package apiserver.services.pdf.gateways.jobs;

import apiserver.jobs.GetDocumentJob;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mnimer on 4/13/14.
 */
public class CFPDFFormJob extends GetDocumentJob implements Serializable
{
    private static final String OVERWRITEDATA = "overwriteDate";
    private static final String PASSWORD = "password";


    // Map of options to pass through, will be set with an AttributeCollection argument.
    private Map options = new HashMap();

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
}
