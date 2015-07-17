package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;

/**
 * Created by mnimer on 5/4/14.
 */
public class WatermarkPdfResult extends CFPdfJob
{
    private Boolean foreground;



    public void setForeground(Boolean foreground)
    {
        this.foreground = foreground;
    }


    public Boolean getForeground()
    {
        return foreground;
    }


}
