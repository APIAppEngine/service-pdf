package pdf;

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

import apiserver.core.connectors.coldfusion.jobs.CFDocumentJob;
import apiserver.services.pdf.gateways.PdfConversionGateway;
import apiserver.services.pdf.gateways.jobs.Html2PdfJob;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: mikenimer
 * Date: 9/16/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ImportResource("flows/htmlToPdf-flow.xml")
public class ConvertHtmlToPDFTest
{

    @Qualifier("convertHtmlToPdfChannelApiGateway")
    @Autowired
    public PdfConversionGateway pdfHtmlGateway;


    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;



    @Test
    public void convertHtmlToPdf()
    {
        try
        {
            Html2PdfJob args = new Html2PdfJob();
            args.setHtml("<b>Hello World</b>");
            args.setFontEmbed(true);
            args.setMarginBottom(2);
            args.setMarginTop(2);
            args.setMarginLeft(2);
            args.setMarginRight(2);

            CFDocumentJob.Permission[] permissions = new CFDocumentJob.Permission[]{
                    CFDocumentJob.Permission.AllowCopy,
                    CFDocumentJob.Permission.AllowPrinting,
                    CFDocumentJob.Permission.AllowScreenReaders
            };
            args.setPermissions(permissions);

            Future<Map> resultFuture = pdfHtmlGateway.convertHtmlToPdf(args);
            Object result = resultFuture.get( defaultTimeout, TimeUnit.MILLISECONDS );

            Assert.assertTrue(result != null);
            Assert.assertEquals(38639, ((Html2PdfJob)result).getPdfBytes().length);
        }
        catch (Exception ex){
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void convertHtmlToPdf2()
    {
        try
        {
            Html2PdfJob args = new Html2PdfJob();
            args.setHtml("<b>Hello World</b>");
            args.setHeaderHtml("header");
            args.setFooterHtml("footer");

            Future<Map> resultFuture = pdfHtmlGateway.convertHtmlToPdf(args);
            Object result = resultFuture.get( defaultTimeout, TimeUnit.MILLISECONDS );

            Assert.assertTrue(result != null);
            Assert.assertEquals(40432, ((Html2PdfJob)result).getPdfBytes().length);
        }
        catch (Exception ex){
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }
}
