package apiserver.services.pdf;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 7/2/14.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfMicroServiceApplication.class)
@IntegrationTest("server.port=0")
public class ProtectPdfRESTTest
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
    public void testProtectPdf() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "testdoc-annualreportfromword.pdf", "application/pdf", pdfFile);

        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(fileUpload(rootUrl + "/pdf/protect")
                                .file(file)
                                .param("newUserPassword", "admin")
                                .param("encrypt", "RC4_128")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/pdf"))
                .andReturn();

        Assert.assertTrue(result.getResponse().getContentLength() > 100000); // the bytes are always different so we'll check general size
        PdfTestBase.saveFileToLocalDisk("test-protectpdf.pdf", result.getResponse().getContentAsByteArray());
    }
}
