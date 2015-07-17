package apiserver.services.pdf.gateways.jobs;


import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class to manage all of the additional properties that can be used with CFDOCUMENT
 * Created by mnimer
 */
public abstract class CFDocumentJob implements Serializable
{
    public enum Encryption
    {
        BIT128("128-bit"),
        BIT40("40-bit"),
        NONE("none");


        public String encryptionLevel;


        Encryption(String _encryptionLevel)
        {
            this.encryptionLevel = _encryptionLevel;
        }
    }

    public enum Orientation
    {
        PORTRAIT,
        LANDSCAPE;
    }

    public enum PageType
    {
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

    public enum Permission
    {
        AllowPrinting,
        AllowModifyContents,
        AllowCopy,
        AllowModifyAnnotations,
        AllowFillIn,
        AllowScreenReaders,
        AllowAssembly,
        AllowDegradedPrinting
    }

    public enum Unit
    {
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
    private static final String UNIT = "unit";
    private static final String USERPASSWORD = "userPassword";


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


    /**
     * Specifies whether the background prints when the user prints the document:
     *
     * @param backgroundVisible
     */
    public void setBackgroundVisible(boolean backgroundVisible)
    {
        this.options.put(BACKGROUNDVISIBLE, backgroundVisible);
    }


    /**
     * Specifies whether the output is encrypted:
     *
     * @param encryption
     */
    public void setEncryption(Encryption encryption)
    {
        this.options.put(ENCRYPTION, encryption);
    }


    /**
     * Specifies if the fonts should be embedded in the output: Yes, No, Selective (embed all fonts except Java fonts and core fonts.)
     *
     * @param fontEmbed
     */
    public void setFontEmbed(Boolean fontEmbed)
    {
        this.options.put(FONTEMBED, fontEmbed);
    }


    /**
     * Bottom margin in inches (default) or centimeters. To specify the bottom margin in centimeters, include the unit=cm attribute.
     *
     * @param marginBottom
     */
    public void setMarginBottom(int marginBottom)
    {
        this.options.put(MARGINBOTTOM, marginBottom);
    }


    /**
     * Top margin in inches (default) or centimeters. To specify the top margin in centimeters, include the unit=cm attribute.
     *
     * @param marginTop
     */
    public void setMarginTop(int marginTop)
    {
        this.options.put(MARGINTOP, marginTop);
    }


    /**
     * Left margin in inches (default) or centimeters. To specify the left margin in centimeters, include the unit=cm attribute.
     *
     * @param marginLeft
     */
    public void setMarginLeft(int marginLeft)
    {
        this.options.put(MARGINLEFT, marginLeft);
    }


    /**
     * Right margin in inches (default) or centimeters. To specify the right margin in centimeters, include the unit=cm attribute.
     *
     * @param marginRight
     */
    public void setMarginRight(int marginRight)
    {
        this.options.put(MARGINRIGHT, marginRight);
    }


    /**
     * Page orientation: portrait,landscape
     *
     * @param orientation
     */
    public void setOrientation(Orientation orientation)
    {
        this.options.put(ORIENTATION, orientation);
    }


    /**
     * Specifies the owner password.
     *
     * @param ownerPassword
     */
    public void setOwnerPassword(String ownerPassword)
    {
        this.options.put(OWNERPASSWORD, ownerPassword);
    }


    /**
     * Page height in inches (default) or centimeters. This attribute is only valid if pagetype=custom. To specify page height in centimeters, include the unit=cm attribute.
     *
     * @param pageHeight
     */
    public void setPageHeight(int pageHeight)
    {
        this.options.put(PAGEHEIGHT, pageHeight);
    }


    /**
     * Page width in inches (default) or centimeters. This attribute is only valid if pageType=custom. To specify page width in centimeters, include the unit=cm attribute.
     *
     * @param pageWidth
     */
    public void setPageWidth(int pageWidth)
    {
        this.options.put(PAGEWIDTH, pageWidth);
    }


    /**
     * Page type for the generated pdf
     *
     * @param pageType
     */
    public void setPageType(PageType pageType)
    {
        this.options.put(PAGETYPE, pageType);
    }


    /**
     * Sets one or more of the permissions enums:
     *
     * @param permissions
     */
    public void setPermissions(String[] permissions)
    {
        this.options.put(PERMISSION, Arrays.asList(permissions));
    }


    /**
     * Scale factor as a percentage. Use this option to reduce the size of the HTML output so that it fits on that paper. Specify a number less than 100.
     *
     * @param scale
     */
    public void setScale(Integer scale)
    {
        this.options.put(SCALE, scale);
    }


    /**
     * Default unit for the pageHeight, pageWidth, and margin attributes:
     *
     * @param unit
     */
    public void setUnit(Unit unit)
    {
        this.options.put(UNIT, unit);
    }


    /**
     * Specifies the user password.
     *
     * @param userPassword
     */
    public void setUserPassword(String userPassword)
    {
        this.options.put(USERPASSWORD, userPassword);
    }
}
