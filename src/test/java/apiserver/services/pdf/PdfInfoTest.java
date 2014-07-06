package apiserver.services.pdf;

import apiserver.PdfMicroServiceApplication;
import apiserver.PdfTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 4/13/14.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfMicroServiceApplication.class)
@IntegrationTest("server.port=0")
public class PdfInfoTest
{
    @Autowired
    public WebApplicationContext context;

    @Value("${local.server.port}")
    public int port;

    @Value("${defaultReplyTimeout}")
    public Integer defaultTimeout;

    String rootUrl;
    InputStream pdfFile;

    @Before
    public void setup(){
        rootUrl = "http://localhost:" +port;

        pdfFile = this.getClass().getClassLoader().getResourceAsStream("testdoc-annualreportfromword.pdf");
    }

    @Test
    public void getPdfInfo() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "testdoc-annualreportfromword.pdf", "application/pdf", pdfFile);

        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(fileUpload(rootUrl + "/pdf/info/get")
                                .file(file)
                )
                .andExpect(status().is(200))
                .andReturn();

        Assert.assertTrue(result.getResponse().getContentLength() > 100000); // the bytes are always different so we'll check general size
    }

    @Test
    public void setPdfInfo() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "testdoc-annualreportfromword.pdf", "application/pdf", pdfFile);

        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(fileUpload(rootUrl + "/pdf/info/set")
                                .file(file)
                )
                .andExpect(status().is(200))
                .andReturn();

        Assert.assertTrue(result.getResponse().getContentLength() > 100000); // the bytes are always different so we'll check general size
        PdfTestBase.saveFileToLocalDisk("test-addHeader.pdf", result.getResponse().getContentAsByteArray());
    }
}
