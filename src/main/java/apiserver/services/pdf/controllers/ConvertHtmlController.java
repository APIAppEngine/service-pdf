package apiserver.services.pdf.controllers;

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

import apiserver.core.common.ResponseEntityHelper;
import apiserver.core.connectors.coldfusion.jobs.CFDocumentJob;
import apiserver.core.connectors.coldfusion.services.BinaryJob;
import apiserver.services.pdf.gateways.PdfConversionGateway;
import apiserver.services.pdf.gateways.jobs.Html2PdfJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.Type;
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
@Api(value = "/pdf", description = "[PDF]")
@RequestMapping("/pdf")
public class ConvertHtmlController
{
    @Qualifier("convertHtmlToPdfChannelApiGateway")
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
            @ApiParam(name="html", required = true) @RequestParam(value = "html") String html,
            @ApiParam(name="headerHtml", required = false) @RequestParam(value = "headerHtml", required = false) String headerHtml,
            @ApiParam(name="footerHtml", required = false) @RequestParam(value = "footerHtml", required = false) String footerHtml,
            // Optional arguments
            @ApiParam(name="backgroundVisible", required = false, defaultValue = "true") @RequestParam(value = "backgroundVisible", required = false) Boolean backgroundVisible,
            @ApiParam(name="encryption", required = false, defaultValue = "none", allowableValues = "128-bit,40-bit,none") @RequestParam(value = "encryption", required = false) CFDocumentJob.Encryption encryption,
            @ApiParam(name="fontEmbed", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "fontEmbed", required = false) Boolean fontEmbed,
            @ApiParam(name="marginBottom", required = false, defaultValue = "0") @RequestParam(value = "marginBottom", required = false) Integer marginBottom,
            @ApiParam(name="marginTop", required = false, defaultValue = "0") @RequestParam(value = "marginTop", required = false) Integer marginTop,
            @ApiParam(name="marginLeft", required = false, defaultValue = "0") @RequestParam(value = "marginLeft", required = false) Integer marginLeft,
            @ApiParam(name="marginRight", required = false, defaultValue = "0") @RequestParam(value = "marginRight", required = false) Integer marginRight,
            @ApiParam(name="orientation", required = false, defaultValue = "portrait", allowableValues = "portrait,landscape") @RequestParam(value = "orientation", required = false) CFDocumentJob.Orientation orientation,
            @ApiParam(name="ownerPassword", required = false) @RequestParam(value = "ownerPassword", required = false) String ownerPassword,
            @ApiParam(name="pageHeight", required = false) @RequestParam(value = "pageHeight", required = false) Integer pageHeight,
            @ApiParam(name="pageWidth", required = false) @RequestParam(value = "pageWidth", required = false) Integer pageWidth,
            @ApiParam(name="pageType", required = false, defaultValue = "letter", allowableValues = "legal,letter,a4,a5,b4,b5,b4-jis,b5-jis,custom") @RequestParam(value = "pageType", required = false) CFDocumentJob.PageType pageType,
            @ApiParam(name="scale", required = false) @RequestParam(value = "scale", required = false) Integer scale,
            @ApiParam(name="unit", required = false) @RequestParam(value = "unit", required = false) CFDocumentJob.Unit unit,
            @ApiParam(name="userPassword", required = false) @RequestParam(value = "userPassword", required = false) String userPassword,
            // Permisions[] items
            @ApiParam(name="allowPrinting", required = false, defaultValue = "false") @RequestParam(value = "allowPrinting", required = false) Boolean allowPrinting,
            @ApiParam(name="allowModifyContents", required = false, defaultValue = "false") @RequestParam(value = "allowModifyContents", required = false) Boolean allowModifyContents,
            @ApiParam(name="allowCopy", required = false, defaultValue = "false") @RequestParam(value = "allowCopy", required = false) Boolean allowCopy,
            @ApiParam(name="allowModifyAnnotations", required = false, defaultValue = "false") @RequestParam(value = "allowModifyAnnotations", required = false) Boolean allowModifyAnnotations,
            @ApiParam(name="allowFillIn", required = false, defaultValue = "false") @RequestParam(value = "allowFillIn", required = false) Boolean allowFillIn,
            @ApiParam(name="allowScreenReaders", required = false, defaultValue = "false") @RequestParam(value = "allowScreenReaders", required = false) Boolean allowScreenReaders,
            @ApiParam(name="allowAssembly", required = false, defaultValue = "false") @RequestParam(value = "allowAssembly", required = false) Boolean allowAssembly,
            @ApiParam(name="allowDegradedPrinting", required = false, defaultValue = "false") @RequestParam(value = "allowDegradedPrinting", required = false) Boolean allowDegradedPrinting

    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        Html2PdfJob args = new Html2PdfJob();
        args.setHtml(html);
        args.setHeaderHtml(headerHtml);
        args.setFooterHtml(footerHtml);

        //Optional Arguments
        if( backgroundVisible != null) args.setBackgroundVisible(backgroundVisible);
        if( encryption != null ) args.setEncryption(encryption);
        if( fontEmbed != null) args.setFontEmbed(fontEmbed);
        if( marginBottom != null) args.setMarginBottom(marginBottom);
        if( marginTop != null) args.setMarginTop(marginTop);
        if( marginLeft != null) args.setMarginLeft(marginLeft);
        if( marginRight != null) args.setMarginRight(marginRight);
        if( orientation != null) args.setOrientation(orientation);
        if( ownerPassword != null) args.setOwnerPassword(ownerPassword);
        if( pageHeight != null) args.setPageHeight(pageHeight);
        if( pageWidth != null) args.setPageWidth(pageWidth);
        if( pageType != null) args.setPageType(pageType);
        if( scale != null) args.setScale(scale);
        if( unit != null) args.setUnit(unit);
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
            args.setPermissions((String[]) permissionsArray.toArray());
        }

        Future<Map> future = gateway.convertHtmlToPdf(args);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


}
