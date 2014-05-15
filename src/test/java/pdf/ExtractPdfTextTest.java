package unitTests.v1_0.pdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mnimer on 4/13/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ImportResource("flows/extractPdfText-flow.xml")
public class ExtractPdfTextTest
{

    @Test
    public void extractText()
    {
        Assert.fail("Not implemented yet");
    }

}
