package unitTests.v1_0.pdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mnimer on 4/13/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ImportResource("flows/addFooterToPdf-flow.xml")
public class PdfFooterTest
{


    @Test
    public void addFooterImageToPdf()
    {
        Assert.fail("Not implemented yet");
    }

    public void addFooterTextToPdf()
    {
        Assert.fail("Not implemented yet");
    }
}
