package pdf;

import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.connectors.coldfusion.jobs.CFDocumentJob;
import apiserver.services.pdf.gateways.PdfConversionGateway;
import apiserver.services.pdf.gateways.jobs.Document2PdfJob;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: mikenimer
 * TODO write more tests around the cfdocument options.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ImportResource("flows/documentToPdf-flow.xml")
public class ConvertDocumentToPdfTest
{

    @Qualifier("convertDocumentToPdfApiGateway")
    @Autowired
    public PdfConversionGateway pdfUrlGateway;


    private @Value("#{applicationProperties.defaultReplyTimeout}") Integer defaultTimeout;



    @Test
    public void convertPptToPdf()
    {
        convertEmbeddedDocument("slides.ppt");
    }

    @Test
    public void convertPptxToPdf()
    {
        convertEmbeddedDocument("slides.pptx");
    }


    @Test
    public void convertDocToPdf()
    {
        convertEmbeddedDocument("wordDoc.doc");
    }


    @Ignore //Crashes open office
    @Test
    public void convertDocxToPdf()
    {
        convertEmbeddedDocument("wordDoc.docx");
    }

    @Test
    public void convertEPubToPdf()
    {
        convertEmbeddedDocument("wordDoc.epub");
    }


    @Test
    public void convertExcelToPdf()
    {
        convertEmbeddedDocument("spreadsheet.xls");
    }

    @Test
    public void convertExcelXToPdf()
    {
        convertEmbeddedDocument("spreadsheet.xlsx");
    }


    private void convertEmbeddedDocument(String name)
    {
        try
        {
            URL url = this.getClass().getClassLoader().getResource(name);
            File file = new File(url.getFile());

            Document2PdfJob args = new Document2PdfJob();
            args.setFile(new Document(file) );

            // set a few random arguments, to spot check it's working.
            args.setFontEmbed(true);
            args.setOrientation(CFDocumentJob.Orientation.LANDSCAPE);
            args.setPermissions(new CFDocumentJob.Permission[]{CFDocumentJob.Permission.AllowPrinting});

            CFDocumentJob.Permission[] permissions = new CFDocumentJob.Permission[]{
                    CFDocumentJob.Permission.AllowCopy,
                    CFDocumentJob.Permission.AllowPrinting,
                    CFDocumentJob.Permission.AllowScreenReaders
            };
            args.setPermissions(permissions);

            Future<Map> resultFuture = pdfUrlGateway.convertDocumentToPdf(args);
            Object result = resultFuture.get( defaultTimeout, TimeUnit.MILLISECONDS );

            Assert.assertTrue(result != null);
            Assert.assertTrue(((Document2PdfJob) result).getPdfBytes().length > 0);
        }
        catch (Exception ex){
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

}
