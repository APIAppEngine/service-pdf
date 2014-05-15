package pdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mnimer on 4/13/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ImportResource("flows/extractPdfImage-flow.xml")
public class ExtractPdfImageTest
{


    @Test
    public void extractImage()
    {
        Assert.fail("Not implemented yet");
    }

}
