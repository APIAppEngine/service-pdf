package apiserver.services.pdf;

import apiserver.PdfTestBase;
import com.jayway.jsonpath.internal.JsonReader;
import net.minidev.json.JSONArray;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 4/13/14.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfMicroServiceApplication.class)
@IntegrationTest("server.port=0")
public class ExtractPdfImageTest
{
    @Autowired
    public WebApplicationContext context;

    @Value("${server.port}")
    public int port;

    @Value("${defaultReplyTimeout}")
    public Integer defaultTimeout;

    String rootUrl;
    InputStream pdfFile;

    @Before
    public void setup(){
        rootUrl = "http://localhost:" +port;

        pdfFile = this.getClass().getClassLoader().getResourceAsStream("testdoc-loremipsumWithPopulatedFormAndImages.pdf");
    }

    @Test
    public void extractImages() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "testdoc-loremipsumWithPopulatedFormAndImages.pdf", "application/pdf", pdfFile);

        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(fileUpload(rootUrl + "/pdf/extract/image")
                                .file(file)
                )
                .andExpect(status().is(200))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JSONArray jsonNode = (JSONArray)new JsonReader().parse(json).json();
        Assert.assertEquals(2, jsonNode.size());

        for (Object o : jsonNode) {
            String imgUrl = rootUrl + o;
            MvcResult img = MockMvcBuilders.webAppContextSetup(context).build()
                    .perform(get(imgUrl))
                    .andExpect(status().is(200))
                    .andReturn();
            PdfTestBase.saveFileToLocalDisk("test-image-" +o.hashCode() +".jpg", img.getResponse().getContentAsByteArray());
        }
    }
}
