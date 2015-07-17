package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;

/**
 * Created by mnimer on 4/17/14.
 */
public class DDXPdfResult extends CFPdfJob
{
    private String ddx;


    public String getDdx()
    {
        return ddx;
    }


    public void setDdx(String ddx)
    {
        this.ddx = ddx;
    }


}
