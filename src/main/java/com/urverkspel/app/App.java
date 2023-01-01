package com.urverkspel.app;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

/* PLANS:
 * x TODO: Read config from xml/json
 * - TODO: Simple UI for making configs
 * - TODO: Insert specific pages from one PDF into another, at specified locations (including first/last)
 * - TODO: Insert blank pages, at specific locations (first/last)
 * - TODO: Delete specific pages
 * x TODO: Remove all items from specified layer, from all pages
 * x TODO: Set metadata
 * - TODO: Extract page (range) to specified file
 * x TODO: Modular design
 * - TODO: Dryruns & reports
 * 
 * - BUG: Adobe uses XMP "description" for Subject, and "Subject" for keywords
 */

// TODO: Check whatever fuckery InDesign does with its nestings - multiple titles?

public class App {
  public static void main(String[] args) throws Exception {

    String configFilename = "config.json";

    // Load config
    Path configFile = Paths.get(configFilename);
    Configuration configuration = new Configuration(configFile);

    if (!configuration.sanityCheck()) {
      System.out.println("Config is not valid. Exiting");
      return;
    }

    System.out.println("Following instructions from '" + configuration.getName() + "' [" + configFile + "]");

    // Load input document
    Path pdfFile = configuration.GetInputFile();

    System.out.println("Reading [" + pdfFile.toString() + "]");
    PDDocument document = Loader.loadPDF(pdfFile.toFile());

    System.out.println("Applying actions:");
    configuration.ApplyActionsTo(document);

    // List<String> layersToRemove = new ArrayList<>(List.of("Grid", "Göm på PDF",
    // "Guides and Grids"));
    // File pdfFile = new File(inputFilename);
    // PDDocument document = Loader.loadPDF(pdfFile);

    // // Remove layers
    // PDFLayers.removeLayersAndContentsFromDocument(document, layersToRemove);

    System.out.println("Saving file...");
    document.save(configuration.GetOutputFileName());
    document.close();
  }

}
