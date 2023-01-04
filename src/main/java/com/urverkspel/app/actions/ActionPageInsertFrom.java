package com.urverkspel.app.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.json.JSONObject;

import com.urverkspel.app.*;

public class ActionPageInsertFrom extends Configuration.Action {

  Path sourceFile = null;
  int sourcePageNum = -1;
  int targetPageNum = -1;
  RangeCollection sourcePageRanges = null;

  public ActionPageInsertFrom(Configuration config, JSONObject configFragment) {
    super(config, configFragment);

    String sourceFileName = Configuration.getStringIfKeyExists("sourceFile", configFragment);
  
    sourceFile = config.getConfigRelativePath(sourceFileName);
  
    sourcePageNum = Configuration.getIntIfKeyExists("sourcePage", configFragment) - 1;
    targetPageNum = Configuration.getIntIfKeyExists("beforePage", configFragment) - 1;
  
    String sourcePagesString = Configuration.getStringIfKeyExists("sourcePageRange", configFragment);
  
    if (sourcePagesString != null && !sourcePagesString.isEmpty()) {
      sourcePageRanges = new RangeCollection(sourcePagesString);
    }
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    // Make sure source file exists and is not the same as the target file
    if (Files.notExists(sourceFile)) {
      throw new Exception("Source file " + sourceFile.getFileName() + " does not exist");
    }

    PDDocument sourceDocument = Loader.loadPDF(sourceFile.toFile());

    if (sourceDocument == document) {
      throw new Exception("Source file " + sourceFile.getFileName() + " is the same as the target file");
    }

    // Remove unwanted pages from source file
    if (sourcePageNum >= 0 || sourcePageRanges != null) {

      // System.out.println("hey");

      // Add pages to remove to a list so we don't mess up the loop
      List<PDPage> pagesToRemove = new ArrayList<>();
      for (int i = 0; i < sourceDocument.getNumberOfPages(); i++) {

        // Remember: i is 0-based, but page number ranges are 1-based
        if (i == sourcePageNum) {
          continue;
        }
        if (sourcePageRanges != null && sourcePageRanges.contains(i + 1)) {
          continue;
        }

        PDPage page = sourceDocument.getPages().get(i);

        pagesToRemove.add(page);
      }

      // Remove pages from source file
      for (PDPage page : pagesToRemove) {
        sourceDocument.removePage(page);
      }
    }

    // Merge the source file into the target file
    PDFMergerUtility merger = new PDFMergerUtility();
    merger.appendDocument(document, sourceDocument);

    int numberOfPages = sourceDocument.getNumberOfPages();
    sourceDocument.close();

    List<PDPage> sourcePages = new ArrayList<>();

    // Create a list of all pages we just got from the source file
    for (int i = 0; i < numberOfPages; i++) {
      int pNum = document.getNumberOfPages() - numberOfPages + i;
      sourcePages.add(PDFPages.getPageFromDocument(document, pNum));
    }

    // Move pages to targetPage location in document
    PDFPages.insertPagesAt(document, targetPageNum, sourcePages, true);

  }

  @Override
  public String GetName() {
    if (sourcePageRanges != null) {
      return "Inserting pages " + sourcePageRanges.toString() + " from '" + sourceFile.getFileName() + "'' at page "
          + (targetPageNum + 1);
    } else if (sourcePageNum >= 0) {
      return "Inserting page " + (sourcePageNum + 1) + " from '" + sourceFile.getFileName() + "'' at page "
          + (targetPageNum + 1);
    } else {
      return "Inserting pages from " + sourceFile.getFileName() + " to page " + targetPageNum;
    }
  }

}
