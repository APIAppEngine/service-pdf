package apiserver.services.pdf.gateways.jobs;

import apiserver.core.connectors.coldfusion.jobs.CFPdfJob;

/**
 * Created by mnimer on 5/4/14.
 */
public class ThumbnailPdfResult extends CFPdfJob
{

    private Integer maxBreadth;
    private Integer maxLength;
    private Integer maxScale;
    private String imagePrefix;
    private String resolution;
    private Integer scale;
    private Boolean transparent;
    private Boolean hiRes;
    private Boolean compressTiffs;
    private String format;


    public void setFormat(String format)
    {
        this.format = format;
    }


    public void setImagePrefix(String imagePrefix)
    {
        this.imagePrefix = imagePrefix;
    }


    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }


    public void setScale(Integer scale)
    {
        this.scale = scale;
    }


    public void setTransparent(Boolean transparent)
    {
        this.transparent = transparent;
    }


    public void setHiRes(Boolean hiRes)
    {
        this.hiRes = hiRes;
    }


    public void setCompressTiffs(Boolean compressTiffs)
    {
        this.compressTiffs = compressTiffs;
    }


    public void setMaxScale(Integer maxScale)
    {
        this.maxScale = maxScale;
    }


    public void setMaxLength(Integer maxLength)
    {
        this.maxLength = maxLength;
    }


    public void setMaxBreadth(Integer maxBreadth)
    {
        this.maxBreadth = maxBreadth;
    }


    public Integer getMaxBreadth()
    {
        return maxBreadth;
    }

}
