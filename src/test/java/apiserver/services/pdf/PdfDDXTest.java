package apiserver.services.pdf;

import apiserver.PdfTestBase;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 4/13/14.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfMicroServiceApplication.class)
@IntegrationTest("server.port=0")
public class PdfDDXTest
{
    @Autowired
    public WebApplicationContext context;

    @Value("${local.server.port}")
    public int port;

    @Value("${defaultReplyTimeout}")
    public Integer defaultTimeout;

    String rootUrl;
    InputStream pdfFile;
    String ddxFile;

    @Before
    public void setup() throws IOException {
        rootUrl = "http://localhost:" +port;

        pdfFile = this.getClass().getClassLoader().getResourceAsStream("testdoc-annualreportfromword.pdf");
        ddxFile = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("testdoc-simpleddxfooter.xml"));
    }

    @Ignore
    @Test
    /* todo: Need to test on machine with CF 11 PDFGenerator service */
    public void addFooterToPdfWithDDX() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "testdoc-annualreportfromword.pdf", "application/pdf", pdfFile);

        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(fileUpload(rootUrl + "/pdf/modify/ddx")
                                .file(file)
                                .param("ddx", ddxFile)
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/pdf"))
                .andReturn();

        Assert.assertTrue(result.getResponse().getContentLength() > 100000); // the bytes are always different so we'll check general size
        PdfTestBase.saveFileToLocalDisk("test-addFooterWithDDX.pdf", result.getResponse().getContentAsByteArray());
    }
}
