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
            @ApiParam(name="backgroundVisible", required = false, defaultValue = "true") @RequestParam(value = "backgroundVisible") Boolean backgroundVisible,
            @ApiParam(name="encryption", required = false, defaultValue = "none", allowableValues = "128-bit,40-bit,none") @RequestParam(value = "encryption") CFDocumentJob.Encryption encryption,
            @ApiParam(name="fontEmbed", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "fontEmbed") Boolean fontEmbed,
            @ApiParam(name="marginBottom", required = false, defaultValue = "0") @RequestParam(value = "marginBottom", defaultValue = "0") Integer marginBottom,
            @ApiParam(name="marginTop", required = false, defaultValue = "0") @RequestParam(value = "marginTop", defaultValue = "0") Integer marginTop,
            @ApiParam(name="marginLeft", required = false, defaultValue = "0") @RequestParam(value = "marginLeft", defaultValue = "0") Integer marginLeft,
            @ApiParam(name="marginRight", required = false, defaultValue = "0") @RequestParam(value = "marginRight", defaultValue = "0") Integer marginRight,
            @ApiParam(name="orientation", required = false, defaultValue = "portrait", allowableValues = "portrait,landscape") @RequestParam(value = "orientation", defaultValue = "0") CFDocumentJob.Orientation orientation,
            @ApiParam(name="ownerPassword", required = false) @RequestParam(value = "ownerPassword") String ownerPassword,
            @ApiParam(name="pageHeight", required = false) @RequestParam(value = "pageHeight") Integer pageHeight,
            @ApiParam(name="pageWidth", required = false) @RequestParam(value = "pageWidth") Integer pageWidth,
            @ApiParam(name="pageType", required = false, defaultValue = "letter", allowableValues = "legal,letter,a4,a5,b4,b5,b4-jis,b5-jis,custom") @RequestParam(value = "pageType", defaultValue = "letter") CFDocumentJob.PageType pageType,
            @ApiParam(name="scale", required = false) @RequestParam(value = "scale") Integer scale,
            @ApiParam(name="unit", required = false) @RequestParam(value = "unit") CFDocumentJob.Unit unit,
            @ApiParam(name="userPassword", required = false) @RequestParam(value = "userPassword") String userPassword,
            // Permisions[] items
            @ApiParam(name="allowPrinting", required = false, defaultValue = "false") @RequestParam(value = "allowPrinting", defaultValue = "false") Boolean allowPrinting,
            @ApiParam(name="allowModifyContents", required = false, defaultValue = "false") @RequestParam(value = "allowModifyContents", defaultValue = "false") Boolean allowModifyContents,
            @ApiParam(name="allowCopy", required = false, defaultValue = "false") @RequestParam(value = "allowCopy", defaultValue = "false") Boolean allowCopy,
            @ApiParam(name="allowModifyAnnotations", required = false, defaultValue = "false") @RequestParam(value = "allowModifyAnnotations", defaultValue = "false") Boolean allowModifyAnnotations,
            @ApiParam(name="allowFillIn", required = false, defaultValue = "false") @RequestParam(value = "allowFillIn", defaultValue = "false") Boolean allowFillIn,
            @ApiParam(name="allowScreenReaders", required = false, defaultValue = "false") @RequestParam(value = "allowScreenReaders", defaultValue = "false") Boolean allowScreenReaders,
            @ApiParam(name="allowAssembly", required = false, defaultValue = "false") @RequestParam(value = "allowAssembly", defaultValue = "false") Boolean allowAssembly,
            @ApiParam(name="allowDegradedPrinting", required = false, defaultValue = "false") @RequestParam(value = "allowDegradedPrinting", defaultValue = "false") Boolean allowDegradedPrinting

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

        List<CFDocumentJob.Permission> permissionsArray = new ArrayList();
        if( allowAssembly ) permissionsArray.add(CFDocumentJob.Permission.AllowAssembly);
        if( allowCopy ) permissionsArray.add(CFDocumentJob.Permission.AllowCopy);
        if( allowDegradedPrinting ) permissionsArray.add(CFDocumentJob.Permission.AllowDegradedPrinting);
        if( allowFillIn  ) permissionsArray.add(CFDocumentJob.Permission.AllowFillIn);
        if( allowModifyAnnotations ) permissionsArray.add(CFDocumentJob.Permission.AllowModifyAnnotations);
        if( allowModifyContents ) permissionsArray.add(CFDocumentJob.Permission.AllowModifyContents);
        if( allowScreenReaders ) permissionsArray.add(CFDocumentJob.Permission.AllowScreenReaders);
        if( allowPrinting ) permissionsArray.add(CFDocumentJob.Permission.AllowPrinting);
        args.setPermissions( (CFDocumentJob.Permission[])permissionsArray.toArray() );

        Future<Map> future = gateway.convertHtmlToPdf(args);
        BinaryJob payload = (BinaryJob)future.get(defaultTimeout, TimeUnit.MILLISECONDS);

        byte[] fileBytes = payload.getPdfBytes();
        String contentType = "application/pdf";
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(fileBytes, contentType, false);
        return result;
    }


}
