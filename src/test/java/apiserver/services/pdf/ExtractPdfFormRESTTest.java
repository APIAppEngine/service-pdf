package apiserver.services.pdf;


import apiserver.PdfMicroServiceApplication;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
public class ExtractPdfFormRESTTest
{

    @Autowired
    public WebApplicationContext context;

    @Value("${local.server.port}")
    public int port;

    @Value("${defaultReplyTimeout}")
    public Integer defaultTimeout;

    String rootUrl;
    String xfdf;
    InputStream pdfFile;

    @Before
    public void setup() throws IOException {

        rootUrl = "http://localhost:" +port;
        pdfFile = this.getClass().getClassLoader().getResourceAsStream("testdoc-loremipsumWithPopulatedFormAndImages.pdf");


        InputStream is = this.getClass().getClassLoader().getResourceAsStream("testdoc-loremipsumWithPopulatedFormAndImages_data.xfdf");
        xfdf = IOUtils.toString(is);

    }

    @Test
    public void testExtractPdfForm() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "testdoc-loremipsumWithPopulatedFormAndImages.pdf", "application/pdf", pdfFile);


        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(fileUpload(rootUrl + "/pdf/form/extract")
                                .file(file)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("FirstName").value("Mike"))
                .andReturn();

    }


    @Ignore
    @Test
    public void testExtractCachedPdfForm() throws Exception {
       throw new RuntimeException("Not implemented");
    }

}
