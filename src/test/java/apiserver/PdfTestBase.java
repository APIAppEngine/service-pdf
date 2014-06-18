package apiserver;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Created by mnimer on 5/18/14.
 */
@WebAppConfiguration
@ContextConfiguration(classes = PdfMicroServiceApplication.class)
public abstract class PdfTestBase
{
    public static ConfigurableApplicationContext context;

    @Autowired
    public WebApplicationContext wac;


    //@Value("${server.port}")
    public String port;

    //@Value("${defaultReplyTimeout}")
    public Integer defaultTimeout;

    public String rootUrl;


    @BeforeClass
    public static void start() throws Exception
    {
        context = SpringApplication.run(PdfMicroServiceApplication.class);
    }


    @AfterClass
    public static void stop()
    {
        if (context != null)
        {
            context.close();
        }
    }


    public void setup() throws URISyntaxException, IOException, InterruptedException, ExecutionException
    {
        // manually load autowired
        //properties
        port = context.getEnvironment().getProperty("server.port");
        defaultTimeout = new Integer(context.getEnvironment().getProperty("defaultReplyTimeout"));
        rootUrl = "http://localhost:" +port;
    }


    public void tearDown() throws InterruptedException, ExecutionException
    {
    }



    @Before
    public void cacheDocument() throws URISyntaxException, IOException, InterruptedException, ExecutionException
    {
        setup();

    }

    @After
    public void clearCache() throws InterruptedException, ExecutionException
    {
        tearDown();

    }


    public static void saveFileToLocalDisk(String fileName, byte[] fileBytes) {

        try {

            File file = new File("/Users/mnimer/Desktop/" + fileName);

            if (file.exists()) {
                file.delete();
            }

            FileUtils.writeByteArrayToFile(file, fileBytes);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
