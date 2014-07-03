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

import apiserver.PdfMicroServiceApplication;
import apiserver.PdfTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: mikenimer
 * Date: 9/16/13
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfMicroServiceApplication.class)
@IntegrationTest("server.port=0")
public class ConvertHtmlToPdfRestTest
{

    @Autowired public WebApplicationContext context;

    @Value("${local.server.port}")
    public int port;

    @Value("${defaultReplyTimeout}")
    public Integer defaultTimeout;

    String rootUrl;

    @Before
    public void setup(){
        rootUrl = "http://localhost:" +port;
    }


    @Test
    public void testHtmlToPdfRest() throws Exception {

        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(get(rootUrl + "/pdf/convert/html")
                                .param("html", "<font size=\"24\">Hello</font> <i>World</i>")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/pdf"))
                .andReturn();

        System.out.println("bytes[] = " +result.getResponse().getContentLength());
        Assert.assertTrue(result.getResponse().getContentLength() > 10000); // the bytes are always different so we'll check general size
        PdfTestBase.saveFileToLocalDisk("test-htmlToPdf.pdf", result.getResponse().getContentAsByteArray());
    }

    @Test
    public void testHtmlWithHeaderFooterToPdfRest() throws Exception {

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/pdf/convert/html")
                                .param("html", "This is the <font size=\"24\">BODY</font>")
                                .param("headerHtml", "This is the HEADER")
                                .param("footerHtml", "This is the FOOTER")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/pdf"))
                .andReturn();

        System.out.println("bytes[] = " +result.getResponse().getContentLength());
        Assert.assertTrue(result.getResponse().getContentLength() > 10000); // the bytes are always different so we'll check general size
        PdfTestBase.saveFileToLocalDisk("test-htmlWithHeaderFooterToPdf.pdf", result.getResponse().getContentAsByteArray());
    }

}
