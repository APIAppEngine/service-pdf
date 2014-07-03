package apiserver.services.pdf;

import apiserver.services.pdf.gateways.PdfConversionGateway;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * User: mikenimer
 * TODO write more tests around the cfdocument options.
 */
@Ignore
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
/**

        try
        {
            URL url = this.getClass().getClassLoader().getResource(name);
            File file = new File(url.getFile());

            Document2PdfJob args = new Document2PdfJob();
            args.setFile(new Document(file) );

            // set a few random arguments, to spot check it's working.
            args.setFontEmbed(true);
            args.setOrientation(CFDocumentJob.Orientation.LANDSCAPE);
            args.setPermissions(new String[]{CFDocumentJob.Permission.AllowPrinting.name()});

            String[] permissions = new String[]{
                    CFDocumentJob.Permission.AllowCopy.name(),
                    CFDocumentJob.Permission.AllowPrinting.name(),
                    CFDocumentJob.Permission.AllowScreenReaders.name()
            };
            args.setPermissions(permissions);

            Future<Map> resultFuture = pdfUrlGateway.convertDocumentToPdf(args);
            Object result = resultFuture.get( defaultTimeout, TimeUnit.MILLISECONDS );

            Assert.assertTrue(result != null);
            Assert.assertTrue(((Document2PdfJob) result).getResult().length > 0);
        }
        catch (Exception ex){
            ex.printStackTrace();
            Assert.fail(ex.getMessage());

        }
 **/
    }


}
