package com.urverkspel.app;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

/* PLANS:
 * x TODO: Modular design
 * x TODO: Read config from xml/json
 * x TODO: Remove all items from specified layer, from all pages
 * x TODO: Set metadata
 * x TODO: Insert blank pages, at specific locations (first/last)
 * - TODO: JSON schema
 * - TODO: Delete specific pages
 * - TODO: OCProperties order (.indb?), replace names
 * - TODO: Insert specific pages from one PDF into another, at specified locations (including first/last)
 * - TODO: Extract page (range) to specified file
 * - TODO: LOWPRIO Dryruns & reports
 * - TODO: LOWPRIO Merge layers
 * - TODO: LOWPRIO Simple UI for making configs
 * 
 * - BUG: Adobe uses XMP "description" for Subject, and "Subject" for keywords
 */

// TODO: Check whatever fuckery InDesign does with its nestings - multiple titles?

public class App {
  public static void main(String[] args) throws Exception {

    String configFilename = "config.json";

    if (args.length > 0) {
      configFilename = args[0];
    }

    // Load config
    Path configFile = Paths.get(configFilename);

    if (!configFile.toFile().exists()) {
      System.out.println("Config file '" + configFile + "' does not exist. Exiting");
      return;
    }


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

    System.out.println("Saving file to [" + configuration.GetOutputFileName() + "]");
    document.save(configuration.GetOutputFileName());
    document.close();
  }

}
