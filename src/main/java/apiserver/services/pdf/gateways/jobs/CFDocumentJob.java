package apiserver.services.pdf.gateways.jobs;

import apiserver.jobs.IProxyJob;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class to manage all of the additional properties that can be used with CFDOCUMENT
 * Created by mnimer
 */
public class CFDocumentJob implements IProxyJob, Serializable
{
    public enum Encryption{
        BIT128("128-bit"),
        BIT40("40-bit"),
        NONE("none");


        public String encryptionLevel;

        Encryption(String _encryptionLevel)
        {
            this.encryptionLevel = _encryptionLevel;
        }
    }

    public enum Orientation {
        PORTRAIT,
        LANDSCAPE;
    }

    public enum PageType {
        LEGAL("legal"),
        LETTER("letter"),
        A4("a4"),
        A5("a5"),
        B4("b4"),
        B5("b5"),
        B4JIS("B4-jis"),
        B5JIS("b5-jis"),
        CUSTOM("custom");

        public String pageType;
        PageType(String _pageType)
        {
            this.pageType = _pageType;
        }
    }

    public enum Permission {
        AllowPrinting,
        AllowModifyContents,
        AllowCopy,
        AllowModifyAnnotations,
        AllowFillIn,
        AllowScreenReaders,
        AllowAssembly,
        AllowDegradedPrinting
    }

    public enum Unit {
        IN, CM
    }


    private static final String BACKGROUNDVISIBLE = "backgroundVisible";
    private static final String ENCRYPTION = "encryption";
    private static final String FONTEMBED = "fontEmbed";
    private static final String MARGINBOTTOM = "marginBottom";
    private static final String MARGINTOP = "marginTop";
    private static final String MARGINLEFT = "marginLeft";
    private static final String MARGINRIGHT = "marginRight";
    private static final String ORIENTATION = "orientation";
    private static final String OWNERPASSWORD = "ownerPassword";
    private static final String PAGEHEIGHT = "pageHeight";
    private static final String PAGEWIDTH = "pageWidth";
    private static final String PAGETYPE = "pageType";
    private static final String PERMISSION = "permission";
    private static final String SCALE = "scale";
    private static final String SRC = "src";
    private static final String UNIT = "unit";
    private static final String USERPASSWORD = "userPassword";
    private static final String HTML = "html";
    private static final String HTMLSECTIONS = "sections";
    private static final String HEADERHTML = "headerhtml";
    private static final String FOOTERHTML = "footerhtml";
    private static final String AUTHPASSWORD = "authpassword";
    private static final String AUTHUSER = "authuser";
    private static final String BOOKMARK = "bookmark";
    private static final String FORMFIELDS = "formfields";
    private static final String PDFA = "pdfa";
    private static final String PERMISSIONPASSWORD = "permissionpassword";
    private static final String TAGGED = "tagged";


    private ResponseEntity responseEntity;

    // Map of options to pass through, will be set with an AttributeCollection argument.
    private Map options = new HashMap();


    public void setUrl(String url)
    {
        this.options.put(SRC, url);
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
     * Specifies whether the background prints when the user prints the document:
     * @param backgroundVisible
     */
    public void setBackgroundVisible(boolean backgroundVisible)
    {
        this.options.put(BACKGROUNDVISIBLE, backgroundVisible);
    }


    /**
     * Password sent to the target URL for Basic Authentication. Combined with username to form a base64 encoded string that is passed in the Authenticate header. Does not provide support for Integrated Windows, NTLM, or Kerebos authentication.
     * @param authPassword
     */
    public void setAuthPassword(String authPassword)
    {
        this.options.put(AUTHPASSWORD, authPassword);
    }


    /**
     * User name sent to the target URL for Basic Authentication. Combined with password to form a base64 encoded string that is passed in the Authenticate header. Does not provide support for Integrated Windows, NTLM, or Kerebos authentication.
     */
    public void setAuthUser(String authUser)
    {
        this.options.put(AUTHUSER, authUser);
    }

    /**
     * Specifies whether bookmarks are created in the document:
     *  yes: creates bookmarks.
     *  no: does not create bookmarks.
     */
    public void setBookmark(Boolean bookmark)
    {
        this.options.put(BOOKMARK, bookmark);
    }

    /**
     * This attribute is available only if you have integrated OpenOffice with ColdFusion.A Boolean value that specifies if form fields are exported as widgets or only their fixed print representation is exported.
     */
    public void setFormFields(Boolean formFields)
    {
        this.options.put(FORMFIELDS, formFields);
    }

    /**
     * A Boolean value that specifies if you need to create a PDF of type PDF/A-1 (ISO 19005-1:2005)
     */
    public void setPdfa(Boolean pdfa)
    {
        this.options.put(PDFA, pdfa);
    }


    /**
     * Password required to access restricted permissions. The restricted permissions are specified using the permissions attribute.
     */
    public void setPermissionPasswrd(String password)
    {
        this.options.put(PERMISSIONPASSWORD, password);
    }


    /**
     * A Boolean value that determines if the PDF is created using the Tagged PDF tag
     */
    public void setTagged(Boolean tagged)
    {
        this.options.put(TAGGED, tagged);
    }

    /**
     * Specifies whether the output is encrypted:
     * @param encryption
     */
    public void setEncryption(Encryption encryption)
    {
        this.options.put(ENCRYPTION, encryption);
    }


    /**
     * Specifies if the fonts should be embedded in the output: Yes, No, Selective (embed all fonts except Java fonts and core fonts.)
     * @param fontEmbed
     */
    public void setFontEmbed(Boolean fontEmbed)
    {
        this.options.put(FONTEMBED, fontEmbed);
    }


    /**
     * Bottom margin in inches (default) or centimeters. To specify the bottom margin in centimeters, include the unit=cm attribute.
     * @param marginBottom
     */
    public void setMarginBottom(int marginBottom)
    {
        this.options.put(MARGINBOTTOM, marginBottom);
    }


    /**
     * Top margin in inches (default) or centimeters. To specify the top margin in centimeters, include the unit=cm attribute.
     * @param marginTop
     */
    public void setMarginTop(int marginTop)
    {
        this.options.put(MARGINTOP, marginTop);
    }


    /**
     * Left margin in inches (default) or centimeters. To specify the left margin in centimeters, include the unit=cm attribute.
     * @param marginLeft
     */
    public void setMarginLeft(int marginLeft)
    {
        this.options.put(MARGINLEFT, marginLeft);
    }


    /**
     * Right margin in inches (default) or centimeters. To specify the right margin in centimeters, include the unit=cm attribute.
     * @param marginRight
     */
    public void setMarginRight(int marginRight)
    {
        this.options.put(MARGINRIGHT, marginRight);
    }


    /**
     * Page orientation: portrait,landscape
     * @param orientation
     */
    public void setOrientation(Orientation orientation)
    {
        this.options.put(ORIENTATION, orientation);
    }


    /**
     * Specifies the owner password.
     * @param ownerPassword
     */
    public void setOwnerPassword(String ownerPassword)
    {
        this.options.put(OWNERPASSWORD, ownerPassword);
    }


    /**
     * Page height in inches (default) or centimeters. This attribute is only valid if pagetype=custom. To specify page height in centimeters, include the unit=cm attribute.
     * @param pageHeight
     */
    public void setPageHeight(int pageHeight)
    {
        this.options.put(PAGEHEIGHT, pageHeight);
    }


    /**
     * Page width in inches (default) or centimeters. This attribute is only valid if pageType=custom. To specify page width in centimeters, include the unit=cm attribute.
     * @param pageWidth
     */
    public void setPageWidth(int pageWidth)
    {
        this.options.put(PAGEWIDTH, pageWidth);
    }


    /**
     * Page type for the generated pdf
     * @param pageType
     */
    public void setPageType(PageType pageType)
    {
        this.options.put(PAGETYPE, pageType);
    }


    /**
     * Sets one or more of the permissions enums:
     * @param permissions
     */
    public void setPermissions(String[] permissions)
    {
        this.options.put(PERMISSION, Arrays.asList(permissions) );
    }


    /**
     * Scale factor as a percentage. Use this option to reduce the size of the HTML output so that it fits on that paper. Specify a number less than 100.
     * @param scale
     */
    public void setScale(Integer scale)
    {
        this.options.put(SCALE, scale);
    }


    /**
     * Default unit for the pageHeight, pageWidth, and margin attributes:
     * @param unit
     */
    public void setUnit(Unit unit)
    {
        this.options.put(UNIT, unit);
    }


    /**
     * Specifies the user password.
     * @param userPassword
     */
    public void setUserPassword(String userPassword)
    {
        this.options.put(USERPASSWORD, userPassword);
    }


    /**
     * Html to convert to pdf
     * @param html
     */
    public void setHtml(String html)
    {
        this.options.put(HTML, html);
    }

    /**
     * Html Sections to convert to pdf
     * @param sections
     */
    public void setHtmlSections(Map sections)
    {
        this.options.put(HTMLSECTIONS, sections);
    }

    /**
     * HTML to use for the header of all pages in the PDF
     * @param headerHtml
     */
    public void setHeaderHtml(String headerHtml) {
        this.options.put(HEADERHTML, headerHtml);
    }


    /**
     * HTML to use for the footer of all pages in the PDF
     * @param footerHtml
     */
    public void setFooterHtml(String footerHtml) {
        this.options.put(FOOTERHTML, footerHtml);
    }



    @Override public Map getArguments()
    {
        Map args = new HashMap();
        args.put("options", this.getOptions());
        return args;
    }


    @Override public ResponseEntity getHttpResponse()
    {
        return this.responseEntity;
    }


    @Override public void setHttpResponse(ResponseEntity response_)
    {
        this.responseEntity = response_;
    }
}
