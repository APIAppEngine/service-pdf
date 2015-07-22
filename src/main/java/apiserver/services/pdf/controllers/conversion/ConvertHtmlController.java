package apiserver.services.pdf.controllers.conversion;

/*******************************************************************************
 Copyright (c) 2013 Mike Nimer.

 This file is part of ApiServer Project.

 The ApiServer Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 The ApiServer Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the ApiServer Project.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import apiserver.jobs.IProxyJob;
import apiserver.services.pdf.gateways.PdfConversionGateway;
import apiserver.services.pdf.gateways.jobs.CFDocumentJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: mnimer
 * Date: 9/15/12
 */
@Controller
@RestController
@Api(value = "/api/pdf", description = "[PDF]")
@RequestMapping("/api/pdf")
public class ConvertHtmlController
{
    @Qualifier("convertHtmlToPdfApiGateway")
    @Autowired
    public PdfConversionGateway gateway;

    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * Convert an HTML string into a PDF document.
     * @param html
     * @param headerHtml
     * @param footerHtml
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "Convert an HTML string into a PDF document.")
    @RequestMapping(value = "/convert/html", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/pdf")
    public ResponseEntity<byte[]> html2pdf(
            @ApiParam(name="html", required = true)
                @RequestParam(value = "html") String html,
            @ApiParam(name="headerHtml", required = false)
                @RequestParam(value = "headerHtml", required = false) String headerHtml,
            @ApiParam(name="footerHtml", required = false)
                @RequestParam(value = "footerHtml", required = false) String footerHtml,
            // Optional arguments
            @ApiParam(name="authPassword", required = false)
                @RequestParam(value = "authPassword", required = false) String authPassword,
            @ApiParam(name="authUser", required = false)
                @RequestParam(value = "authUser", required = false) String authUser,
            @ApiParam(name="backgroundVisible", required = false, defaultValue = "false")
                @RequestParam(value = "backgroundVisible", required = false, defaultValue = "false") Boolean backgroundVisible,
            @ApiParam(name="encryption", required = false, defaultValue = "none", allowableValues = "128-bit,40-bit,none")
                @RequestParam(value = "encryption", required = false) String encryption,
            @ApiParam(name="bookmark", required = false, defaultValue = "false")
                @RequestParam(value = "bookmark", required = false, defaultValue = "false") Boolean bookmark,
            @ApiParam(name="fontEmbed", required = false, defaultValue = "true", allowableValues = "true,false")
                @RequestParam(value = "fontEmbed", required = false) Boolean fontEmbed,
            @ApiParam(name="formFields", required = false, defaultValue = "true", allowableValues = "true,false")
                @RequestParam(value = "formFields", required = false) Boolean formFields,
            @ApiParam(name="formstype", required = false, defaultValue = "true", allowableValues = "FDF,PDF,HTML,XML")
                @RequestParam(value = "formstype", required = false) Boolean formstype,
            @ApiParam(name="marginBottom", required = false, defaultValue = "0")
                @RequestParam(value = "marginBottom", defaultValue = "0") Integer marginBottom,
            @ApiParam(name="marginTop", required = false, defaultValue = "0")
                @RequestParam(value = "marginTop", defaultValue = "0") Integer marginTop,
            @ApiParam(name="marginLeft", required = false, defaultValue = "0")
                @RequestParam(value = "marginLeft", defaultValue = "0") Integer marginLeft,
            @ApiParam(name="marginRight", required = false, defaultValue = "0")
                @RequestParam(value = "marginRight", defaultValue = "0") Integer marginRight,
            @ApiParam(name="orientation", required = false, defaultValue = "portrait", allowableValues = "portrait,landscape")
                @RequestParam(value = "orientation", required = false) String orientation,
            @ApiParam(name="ownerPassword", required = false)
                @RequestParam(value = "openpassword", required = false) String openpassword,
            @ApiParam(name="openpassword", required = false)
                @RequestParam(value = "ownerPassword", required = false) String ownerPassword,
            @ApiParam(name="pageHeight", required = false)
                @RequestParam(value = "pageHeight", required = false) Integer pageHeight,
            @ApiParam(name="pageWidth", required = false)
                @RequestParam(value = "pageWidth", required = false) Integer pageWidth,
            @ApiParam(name="pageType", required = false, defaultValue = "letter", allowableValues = "legal,letter,a4,a5,b4,b5,b4-jis,b5-jis,custom")
                @RequestParam(value = "pageType", required = false) String pageType,
            @ApiParam(name="pdfa", required = false)
                @RequestParam(value = "pdfa", required = false) Boolean pdfa,
            @ApiParam(name="scale", required = false)
                @RequestParam(value = "scale", required = false) Integer scale,
            @ApiParam(name="unit", required = false)
                @RequestParam(value = "unit", required = false) String unit,
            @ApiParam(name="permissionpasswrd", required = false)
                @RequestParam(value = "permissionpasswrd", required = false) String permissionpasswrd,
            @ApiParam(name="userPassword", required = false)
                @RequestParam(value = "userPassword", required = false) String userPassword,
            @ApiParam(name="tagged", required = false)
                @RequestParam(value = "tagged", required = false) Boolean tagged,
            // Permisions[] items
            @ApiParam(name="allowPrinting", required = false, defaultValue = "true")
                @RequestParam(value = "allowPrinting", defaultValue = "true") Boolean allowPrinting,
            @ApiParam(name="allowModifyContents", required = false, defaultValue = "true")
                @RequestParam(value = "allowModifyContents", defaultValue = "true") Boolean allowModifyContents,
            @ApiParam(name="allowCopy", required = false, defaultValue = "true")
                @RequestParam(value = "allowCopy", defaultValue = "true") Boolean allowCopy,
            @ApiParam(name="allowModifyAnnotations", required = false, defaultValue = "true")
                @RequestParam(value = "allowModifyAnnotations", defaultValue = "true") Boolean allowModifyAnnotations,
            @ApiParam(name="allowFillIn", required = false, defaultValue = "true")
                @RequestParam(value = "allowFillIn", defaultValue = "true") Boolean allowFillIn,
            @ApiParam(name="allowScreenReaders", required = false, defaultValue = "true")
                @RequestParam(value = "allowScreenReaders", defaultValue = "true") Boolean allowScreenReaders,
            @ApiParam(name="allowAssembly", required = false, defaultValue = "false")
                @RequestParam(value = "allowAssembly", defaultValue = "true") Boolean allowAssembly,
            @ApiParam(name="allowDegradedPrinting", required = false, defaultValue = "true")
                @RequestParam(value = "allowDegradedPrinting", defaultValue = "true") Boolean allowDegradedPrinting

    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        CFDocumentJob args = new CFDocumentJob();
        args.setHtml(html);
        args.setHeaderHtml(headerHtml);
        args.setFooterHtml(footerHtml);

        //Optional Arguments
        if( authPassword != null) args.setAuthPassword(authPassword);
        if( authUser != null) args.setAuthUser(authUser);
        if( bookmark != null) args.setBookmark(bookmark);
        if( formFields != null) args.setFormFields(formFields);
        if( pdfa != null) args.setPdfa(pdfa);
        if( permissionpasswrd != null) args.setPermissionPasswrd(permissionpasswrd);
        if( tagged != null) args.setTagged(tagged);

        if( backgroundVisible != null) args.setBackgroundVisible(backgroundVisible);
        //if( encryption != null ) args.setEncryption(encryption);
        if( fontEmbed != null) args.setFontEmbed(fontEmbed);
        if( marginBottom != null) args.setMarginBottom(marginBottom);
        if( marginTop != null) args.setMarginTop(marginTop);
        if( marginLeft != null) args.setMarginLeft(marginLeft);
        if( marginRight != null) args.setMarginRight(marginRight);
        //if( orientation != null) args.setOrientation(orientation);
        if( ownerPassword != null) args.setOwnerPassword(ownerPassword);
        if( pageHeight != null) args.setPageHeight(pageHeight);
        if( pageWidth != null) args.setPageWidth(pageWidth);
        //if( pageType != null) args.setPageType(pageType);
        if( scale != null) args.setScale(scale);
        //if( unit != null) args.setUnit(unit);
        if( userPassword != null) args.setUserPassword(userPassword);

        List<String> permissionsArray = new ArrayList();
        if( allowAssembly!= null && allowAssembly ) permissionsArray.add(CFDocumentJob.Permission.AllowAssembly.name());
        if( allowCopy!= null && allowCopy ) permissionsArray.add(CFDocumentJob.Permission.AllowCopy.name());
        if( allowDegradedPrinting!= null && allowDegradedPrinting ) permissionsArray.add(CFDocumentJob.Permission.AllowDegradedPrinting.name());
        if( allowFillIn!= null && allowFillIn  ) permissionsArray.add(CFDocumentJob.Permission.AllowFillIn.name());
        if( allowModifyAnnotations!= null && allowModifyAnnotations ) permissionsArray.add(CFDocumentJob.Permission.AllowModifyAnnotations.name());
        if( allowModifyContents!= null && allowModifyContents ) permissionsArray.add(CFDocumentJob.Permission.AllowModifyContents.name());
        if( allowScreenReaders!= null && allowScreenReaders ) permissionsArray.add(CFDocumentJob.Permission.AllowScreenReaders.name());
        if( allowPrinting!= null && allowPrinting ) permissionsArray.add(CFDocumentJob.Permission.AllowPrinting.name());
        if( permissionsArray!= null && permissionsArray.size() > 0 ) {
            args.setPermissions(StringUtils.toStringArray(permissionsArray));
        }

        Future<Map> future = gateway.convertHtmlToPdf(args);
        IProxyJob payload = (IProxyJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }


}
