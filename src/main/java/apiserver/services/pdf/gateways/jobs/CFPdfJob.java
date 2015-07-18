package apiserver.services.pdf.gateways.jobs;


import apiserver.ApiServerConstants;
import apiserver.core.model.IDocument;
import apiserver.jobs.GetDocumentJob;
import apiserver.jobs.IProxyJob;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mnimer on 4/13/14.
 */
public class CFPdfJob extends GetDocumentJob implements IProxyJob, Serializable
{

    private static final String ADDQUADS = "addQuads";
    private static final String ASCENDING = "acending";
    private static final String ALGO = "algo";
    private static final Object ALIGN = "align";
    private static final Object COMPRESSTIFFS = "compresstiffs";
    private static final String HSCAPE = "hscape";
    private static final String NOATTACHMENTS = "noAttachments";
    private static final String NOCOMMENTS = "noComments";
    private static final String NOFONTS = "noFonts";
    private static final String NOJAVASCRIPTS = "noJavascripts";
    private static final String NOLINKS = "noLinks";
    private static final String NOMETADATA = "noMetadata";
    private static final String NOTHUMBNAILS = "noThumbnails";
    private static final String PAGES = "pages";
    private static final String PASSWORD = "password";
    private static final String VSCAPE = "vscape";
    private static final Object BOTTOMMARGIN = "bottomMargin";
    private static final Object DDX = "ddx";
    private static final Object FOREGROUND = "foreground";
    private static final Object IMAGE = "image";
    private static final Object INFO = "info";
    private static final Object ISBASE64 = "isBase64";
    private static final Object KEEPBOOKMARK = "keepBookmark";
    private static final Object LEFTMARGIN = "leftMargin";
    private static final Object MAXSCALE = "maxscale";
    private static final Object MAXLENGTH = "maxlength";
    private static final Object MAXBREADTH = "maxbreadth";
    private static final Object RIGHTMARGIN = "rightMargin";
    private static final Object OPACITY = "opacity";
    private static final Object SHOWONPRINT = "showOnPrint";
    private static final Object TEXT = "text";
    private static final Object NUMBERFORMAT = "numberFormat";
    private static final Object ALLOWASSEMBLY = "allowAssembly";
    private static final Object ALLOWCOPY = "allowCopy";
    private static final Object ALLOWDEGRADEDPRINTING = "allowDegradedPrinting";
    private static final Object ALLOWFILLIN = "allowFillIn";
    private static final Object ALLOWMODIFYANNOTATIONS = "allowModifyAnnotations";
    private static final Object ALLOWPRINTING = "allowPrinting";
    private static final Object ALLOWSCREENREADERS = "allowScreenReaders";
    private static final Object ALLOWSECURE = "allowSecure";
    private static final Object ENCRYPT = "encrypt";
    private static final Object NEWUSERPASSWORD = "newUserPassword";
    private static final Object NEWOWNERPASSWORD = "newOwnerPassword";
    private static final Object ORDER = "order";
    private static final Object PACKAGEPDF = "package";
    private static final Object POSITION = "position";
    private static final Object RESOLUTION = "resolution";
    private static final Object ROTATION = "rotation";
    private static final Object SCALE = "scale";
    private static final Object TRANSPARENT = "transparent";
    private static final Object HIRES = "hires";
    private static final Object HONOURSPACES = "honourSpaces";
    private static final Object USESTRUCTURE = "useStructure";
    private static final Object TYPE = "type";
    private static final Object FORMAT = "format";
    private static final Object IMAGEPREFIX = "imagePrefix";


    /**
     * Supported encryptio
     */
    public enum Encryption
    {
        RC4_40,
        RC4_128,
        RC4_128M,
        AES_128,
        NONE
    }


    private String action;

    // Map of options to pass through, will be set with an AttributeCollection argument.
    private Map options = new HashMap();

    // store the http response returned from CF (good or bad)
    private ResponseEntity httpResponse;


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


    /**
     * @param algo
     */

    public void setAlgo(String algo)
    {
        this.getOptions().put(ALGO, algo);
    }


    public void setAllowAssembly(Boolean allowAssembly)
    {
        this.getOptions().put(ALLOWASSEMBLY, allowAssembly);
    }


    public void setAllowCopy(Boolean allowCopy)
    {
        this.getOptions().put(ALLOWCOPY, allowCopy);
    }


    public void setAllowDegradedPrinting(Boolean allowDegradedPrinting)
    {
        this.getOptions().put(ALLOWDEGRADEDPRINTING, allowDegradedPrinting);
    }


    public void setAllowFillIn(Boolean allowFillIn)
    {
        this.getOptions().put(ALLOWFILLIN, allowFillIn);
    }


    public void setAllowModifyAnnotations(Boolean allowModifyAnnotations)
    {
        this.getOptions().put(ALLOWMODIFYANNOTATIONS, allowModifyAnnotations);
    }


    public void setAllowPrinting(Boolean allowPrinting)
    {
        this.getOptions().put(ALLOWPRINTING, allowPrinting);
    }


    public void setAllowScreenReaders(Boolean allowScreenReaders)
    {
        this.getOptions().put(ALLOWSCREENREADERS, allowScreenReaders);
    }


    public void setAllowSecure(Boolean allowSecure)
    {
        this.getOptions().put(ALLOWSECURE, allowSecure);
    }


    public void setAlign(String align)
    {
        this.getOptions().put(ALIGN, align);
    }


    public void setBottomMargin(Integer bottomMargin)
    {
        this.getOptions().put(BOTTOMMARGIN, bottomMargin);
    }


    public void setDdx(String ddx)
    {
        this.getOptions().put(DDX, ddx);
    }


    public void setHScale(Double hScale)
    {
        this.getOptions().put(HSCAPE, hScale);
    }


    public void setEncrypt(String encrypt)
    {
        this.getOptions().put(ENCRYPT, encrypt);
    }


    public void setImage(IDocument image)
    {
        this.getOptions().put(IMAGE, image);
    }


    public void setInfo(Map info)
    {
        this.getOptions().put(INFO, info);
    }


    public void setIsBase64(Boolean isBase64)
    {
        this.getOptions().put(ISBASE64, isBase64);
    }


    public void setLeftMargin(Integer leftMargin)
    {
        this.getOptions().put(LEFTMARGIN, leftMargin);
    }


    public void setNewUserPassword(String newUserPassword)
    {
        this.getOptions().put(NEWUSERPASSWORD, newUserPassword);
    }


    public void setNewOwnerPassword(String newOwnerPassword)
    {
        this.getOptions().put(NEWOWNERPASSWORD, newOwnerPassword);
    }


    public void setNoAttachments(Boolean noAttachments)
    {
        this.getOptions().put(NOATTACHMENTS, noAttachments);
    }


    public void setNoBookmarks(Boolean noBookmarks)
    {
        this.getOptions().put(NOATTACHMENTS, noBookmarks);
    }


    public void setNoComments(Boolean noComments)
    {
        this.getOptions().put(NOCOMMENTS, noComments);
    }


    public void setNoFonts(Boolean noFonts)
    {
        this.getOptions().put(NOFONTS, noFonts);
    }


    public void setNoJavaScripts(Boolean noJavaScripts)
    {
        this.getOptions().put(NOJAVASCRIPTS, noJavaScripts);
    }


    public void setNoLinks(Boolean noLinks)
    {
        this.getOptions().put(NOLINKS, noLinks);
    }


    public void setNoMetadata(Boolean noMetadata)
    {
        this.getOptions().put(NOMETADATA, noMetadata);
    }


    public void setNoThumbnails(Boolean noThumbnails)
    {
        this.getOptions().put(NOTHUMBNAILS, noThumbnails);
    }


    /**
     * @param numberFormat Allowed values: lowercaseroman, numeric, uppercaseroman
     */
    public void setNumberFormat(String numberFormat)
    {
        this.getOptions().put(NUMBERFORMAT, numberFormat);
    }


    public void setOpacity(Double opacity)
    {
        this.getOptions().put(OPACITY, opacity);
    }


    public void setPages(String pages)
    {
        this.getOptions().put(PAGES, pages);
    }


    public void setPassword(String password)
    {
        this.getOptions().put(PASSWORD, password);
    }


    public void setPosition(String position)
    {
        this.getOptions().put(POSITION, position);
    }


    public void setRightMargin(Integer rightMargin)
    {
        this.getOptions().put(RIGHTMARGIN, rightMargin);
    }


    public void setRotation(Integer rotation)
    {
        this.getOptions().put(ROTATION, rotation);
    }


    public void setResolution(String resolution)
    {
        this.getOptions().put(RESOLUTION, resolution);
    }


    public void setScale(Integer scale)
    {
        this.getOptions().put(SCALE, scale);
    }


    public void setShowOnPrint(Boolean showOnPrint)
    {
        this.getOptions().put(SHOWONPRINT, showOnPrint);
    }


    public void setText(String text)
    {
        this.getOptions().put(TEXT, text);
    }


    public void setVScale(Double vScale)
    {
        this.getOptions().put(VSCAPE, vScale);
    }


    public void setKeepBookmark(Boolean keepBookmark) {
        this.getOptions().put(KEEPBOOKMARK, keepBookmark);
    }


    public void setAscending(Boolean ascending) {
        this.getOptions().put(ASCENDING, ascending);
    }


    public void setPackagePdf(Boolean packagePdf) {
        this.getOptions().put(PACKAGEPDF, packagePdf);
    }


    public void setOrder(String order) {
        this.getOptions().put(ORDER, order);
    }


    public void setAddQuads(String addQuads) {
        this.getOptions().put(ADDQUADS, addQuads);
    }


    public void setHonourSpaces(Boolean honourSpaces) {
        this.getOptions().put(HONOURSPACES, honourSpaces);
    }


    public void setUseStructure(Boolean useStructure) {
        this.getOptions().put(USESTRUCTURE, useStructure);
    }


    public void setType(String type) {
        this.getOptions().put(TYPE, type);
    }


    public void setFormat(String format) {
        this.getOptions().put(FORMAT, format);
    }


    public void setImagePrefix(String imagePrefix) {
        this.getOptions().put(IMAGEPREFIX, imagePrefix);
    }

    public void setTransparent(Boolean transparent)
    {
        this.getOptions().put(TRANSPARENT, transparent);
    }

    public void setHiRes(Boolean hires)
    {
        this.getOptions().put(HIRES, hires);
    }

    public void setCompressTiffs(Boolean compressTiffs)
    {
        this.getOptions().put(COMPRESSTIFFS, compressTiffs);
    }


    public void setMaxScale(Integer maxScale)
    {
        this.getOptions().put(MAXSCALE, maxScale);
    }

    public void setMaxLength(Integer maxLength)
    {
        this.getOptions().put(MAXLENGTH, maxLength);
    }

    public void setMaxBreadth(Integer maxBreadth)
    {
        this.getOptions().put(MAXBREADTH, maxBreadth);
    }


    public void setForeground(Boolean foreground)
    {
        this.getOptions().put(FOREGROUND, foreground);
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