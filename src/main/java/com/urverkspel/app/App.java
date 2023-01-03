package com.urverkspel.app;

import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

/* PLANS:
 * x Modular design
 * x Read config from xml/json
 * x Remove all items from specified layer, from all pages
 * x Set metadata
 * x Insert blank pages, at specific locations (first/last)
 * x JSON schema
 * x Delete specific pages
 * x Rename layer labels
 * x Make exe
 * x Insert specific pages from one PDF into another, at specified locations (including first/last)
 * x Use config file's working directory as base for relative paths
 * TODO: Fix bug w/ multiple layer order groups
 * TODO: Rename layers

 * TODO: LOWPRIO Extract page/s (range) to specified file
 * TODO: LOWPRIO reorder layers
 * TODO: LOWPRIO move layer to below another label
 * TODO: LOWPRIO create new label
 * TODO: LOWPRIO delete label
 * TODO: LOWPRIO moving/reordering pages
 * TODO: LOWPRIO Dryruns & reports
 * TODO: LOWPRIO Simple UI for making configs
 * 
 * - BUG: Adobe uses XMP "description" for Subject, and "Subject" for keywords
 */

public class App {

  public static void main(String[] args) throws Exception {

    System.out.println("--- PDF Fixer ---");

    // Decide on config file to load
    String configFilename = "config.json";

    if (args.length > 0) {
      configFilename = args[0];
    }

    // Load config file
    Configuration configuration = null;
    try {
      Path configFile = Path.of(configFilename);
      configuration = new Configuration(configFile);
    } catch (Exception ex) {
      System.out.println("Failed to load config file: " + ex.getMessage());
      return;
    }

    System.out.println("Following instructions from '" + configuration.getName() + "' [" + configFilename + "]");

    // Load input document
    Path pdfFile = configuration.GetInputFile();

    System.out.println("\nReading [" + pdfFile.toString() + "]");
    PDDocument document = Loader.loadPDF(pdfFile.toFile());

    // Apply actions to document
    System.out.println("\nApplying actions:");
    configuration.ApplyActionsTo(document);

    // Save resulting file
    Path outputFile = configuration.GetOutputFile();
    System.out.println("\nSaving file to [" + outputFile + "] ... \n");
    document.save(outputFile.toFile());
    System.out.println(colorize("Done!", Attribute.GREEN_TEXT()));

    document.close();
  }
}
