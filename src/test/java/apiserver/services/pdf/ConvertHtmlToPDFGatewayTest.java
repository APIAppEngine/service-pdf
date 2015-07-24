package apiserver.services.pdf;

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

import apiserver.PdfTestBase;
import apiserver.jobs.IProxyJob;
import apiserver.services.pdf.gateways.PdfConversionGateway;
import apiserver.services.pdf.gateways.jobs.CFDocumentJob;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: mikenimer
 * Date: 9/16/13
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfMicroServiceApplication.class)
public class ConvertHtmlToPdfGatewayTest
{
    @Autowired
    private WebApplicationContext context;

    @Qualifier("convertHtmlToPdfChannelApiGateway")
    @Autowired public PdfConversionGateway pdfHtmlGateway;


    @Value("#{applicationProperties.defaultReplyTimeout}")
    private Integer defaultTimeout;



    @Test
    public void convertHtmlToPdfGateway()
    {
        try
        {
            CFDocumentJob args = new CFDocumentJob();
            args.setHtml("<b>Hello World</b>");
            args.setFontEmbed(true);
            args.setMarginBottom(2);
            args.setMarginTop(2);
            args.setMarginLeft(2);
            args.setMarginRight(2);

            String[] permissions = new String[]{
                    CFDocumentJob.Permission.AllowCopy.name(),
                    CFDocumentJob.Permission.AllowPrinting.name(),
                    CFDocumentJob.Permission.AllowScreenReaders.name()
            };
            args.setPermissions(permissions);

            Future<Map> resultFuture = pdfHtmlGateway.cfdocumentConvertHtmlToPdf(args);
            IProxyJob result = (IProxyJob)resultFuture.get( defaultTimeout, TimeUnit.MILLISECONDS );

            Assert.assertTrue(result != null);
            Assert.assertTrue(result.getHttpResponse() != null);
            Assert.assertTrue(result.getHttpResponse().getBody() != null);
            //Assert.assertTrue(((IColdFusionJob)result).getHttpResponse().getBody().getResult().length > 10000);
            PdfTestBase.saveFileToLocalDisk("test-htmlToPdf2.pdf", (byte[]) result.getHttpResponse().getBody());
        }
        catch (Exception ex){
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }


}