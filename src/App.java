import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

/* PLANS:
 * - TODO: Read config from xml/json
 * - TODO: Simple UI for making configs
 * - TODO: Insert specific pages from one PDF into another, at specified locations (including first/last)
 * - TODO: Insert blank pages, at specific locations (first/last)
 * - TODO: Delete specific pages
 * x TODO: Remove all items from specified layer, from all pages
 * - TODO: Set metadata
 * - TODO: Modular design
 * - TODO: Dryruns & reports
 */

// TODO: Check whatever fuckery InDesign does with its nestings - multiple titles?

public class App {
  public static void main(String[] args) throws Exception {

    String inputFilename = "kungen.pdf";
    String outputFilename = "kungen_output.pdf";

    List<String> layersToRemove = new ArrayList<>(List.of("Grid", "Göm på PDF", "Guides and Grids"));
    
    File pdfFile = new File(inputFilename);

    PDDocument document = Loader.loadPDF(pdfFile);

    // Remove layers
    PDFLayers.removeLayersAndContentsFromDocument(document, layersToRemove);

    

    document.save(outputFilename);
    document.close();
  }
 
}
