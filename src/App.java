import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;

/* TODO
 * - Read config from xml/json
 * - Simple UI for making configs
 * - Insert specific pages from one PDF into another, at specified locations (including first/last)
 * - Insert blank pages, at specific locations (first/last)
 * - Delete specific pages
 * - Remove all items from specified layer, from all pages
 * - Set title of PDF (Maybe other metadata)
 * - Modular design
 */

public class App {
  public static void main(String[] args) throws Exception {

    // https://stackoverflow.com/questions/49576411/using-pdfbox-to-remove-optional-content-groups-that-are-not-enabled
    // https://stackoverflow.com/questions/45053923/how-to-delete-an-optional-content-group-alongwith-its-content-from-pdf-using-pdf

    String inputFilename = "test.pdf";
    String outputFilename = "test_output.pdf";

    List layersToRemove = new ArrayList<String>(List.of("Första lagret"));
    File pdfFile = new File(inputFilename);

    PDDocument document = Loader.loadPDF(pdfFile);

    PDDocumentCatalog catalog = document.getDocumentCatalog();
    PDOptionalContentProperties props = catalog.getOCProperties();

    // PDPage page = document.getPage(0);
    PDPage page = catalog.getPages().get(0);
    PDResources resources = page.getResources();

    PDFStreamParser parser = new PDFStreamParser(page);

    List tokens = parser.parse();

    System.out.println(tokens.size());

    ArrayList<Object> newTokens = new ArrayList<Object>();

    // BEGIN = CosName.OC
    // END = Operator EMC

    // Beginning match:
    // CosName.OC
    // CosName with PropertyList that is an OCG
    // BMC

    // End match:
    // EMC

    String currentLayer = null;

    for (int i = 0; i < tokens.size(); i++) {

      // Check for beginnings of OCGs
      String OCGroupName = GetOCGBeginningAt(tokens, i, resources);

      if (OCGroupName != null) {
        currentLayer = OCGroupName;
        System.out.println("Layer start: " + currentLayer);
      }

      // Only add old tokens to new list IF they're not part of a prohibited OCG
      Object objectToken = tokens.get(i);

      if (!layersToRemove.contains(currentLayer)) {
        newTokens.add(objectToken);
      }

      // If reached end, null the OCGroupName again
      if (objectToken instanceof Operator
          && ((Operator) objectToken).getName().equals(OperatorName.END_MARKED_CONTENT)) {
        System.out.println("Layer end: " + currentLayer);
        currentLayer = null;
      }

      // TODO: Remove OCGs when all items have been removed
      // REMEMBER: Check whatever fuckery InDesign does with its nestings

    }

    PDStream newStream = MakeStreamFromTokenList(newTokens, document);
    page.setContents(newStream);

    // Remove the OCGs
    COSDictionary ocgsDict = (COSDictionary) props.getCOSObject();
    COSArray ocgs = (COSArray) ocgsDict.getItem(COSName.OCGS);

    List<Integer> toRemove = new ArrayList<>();

    for (int i = 0; i < ocgs.size(); i++) {

      COSObject o = (COSObject) ocgs.get(i);
      COSDictionary dict = (COSDictionary) o.getObject();

      System.out.println(dict.getString(COSName.NAME));
      
      if (layersToRemove.contains(dict.getString(COSName.NAME))) {
        System.out.println(" REMOVE");
        toRemove.add(i);
      }
    }

    for (int i = 0; i < toRemove.size(); i++)
    {
      ocgs.remove(toRemove.get(i));
    }

    ocgsDict.setItem(COSName.OCGS, ocgs);

    // document.save(outputFilename);

    document.close();
    // System.out.println(newTokens.size());

  }

  private static PDStream MakeStreamFromTokenList(List<Object> tokens, PDDocument document) throws IOException {
    PDStream newStream = new PDStream(document);
    OutputStream outputStream = newStream.createOutputStream(COSName.FLATE_DECODE);
    ContentStreamWriter writer = new ContentStreamWriter(outputStream);
    writer.writeTokens(tokens);
    outputStream.close();

    return newStream;
  }

  private static String GetOCGBeginningAt(List tokens, int index, PDResources pageResources) {

    Object objectToken = tokens.get(index);

    // If there's no room, return
    if (tokens.size() < index + 2)
      return null;

    // COSNAME 1

    // If token isn't a COSName, it's not the beginning of an OCG
    if (!(objectToken instanceof COSName) || (COSName) objectToken != COSName.OC)
      return null;

    // COSNAME 2

    // If next in line isn't a COSname, return
    objectToken = tokens.get(index + 1);
    if (!(objectToken instanceof COSName))
      return null;

    // If the second COSname isn't associated with properties, return
    PDPropertyList pList = pageResources.getProperties((COSName) objectToken);
    if (pList == null || !(pList instanceof PDOptionalContentGroup))
      return null;

    // BMC

    // If next in line isn't a BDC, return
    objectToken = tokens.get(index + 2);

    if (!(objectToken instanceof Operator)
        || !((Operator) objectToken).getName().equals(OperatorName.BEGIN_MARKED_CONTENT_SEQ))
      return null;

    return ((PDOptionalContentGroup) pList).getName();
  }

}
