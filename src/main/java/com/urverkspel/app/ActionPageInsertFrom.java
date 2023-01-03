package com.urverkspel.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.json.JSONObject;

public class ActionPageInsertFrom extends Configuration.Action {

  Path sourceFile = null;
  int sourcePageNum = -1;
  int sourcePageNumFirst = -1;
  int sourcePageNumLast = -1;
  int targetPageNum = -1;

  @Override
  public void Load(JSONObject configFragment) {

    String sourceFileName = Configuration.GetStringIfKeyExists("sourceFile", configFragment);
    sourceFile = Path.of(sourceFileName);

    sourcePageNum = Configuration.GetIntegerIfKeyExists("sourcePage", configFragment) - 1;
    sourcePageNumFirst = Configuration.GetIntegerIfKeyExists("sourcePageFirst", configFragment) - 1;
    sourcePageNumLast = Configuration.GetIntegerIfKeyExists("sourcePageLast", configFragment) - 1;
    targetPageNum = Configuration.GetIntegerIfKeyExists("beforePage", configFragment) - 1;
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    // Make sure source file exists and is not the same as the target file
    if (Files.notExists(sourceFile)) {
      throw new Exception("Source file " + sourceFile.getFileName() + " does not exist");
    }

    PDDocument sourceDocument = Loader.loadPDF(sourceFile.toFile());

    List<PDPage> sourcePages = new ArrayList<>();

    int numberOfPages = sourceDocument.getNumberOfPages();

    // TODO: Remove unwanted pages from source file

    // Merge the source file into the target file
    PDFMergerUtility merger = new PDFMergerUtility();
    merger.appendDocument(document, sourceDocument);
    sourceDocument.close();

    // Create a list of all pages we just got from the source file
    for (int i = 0; i < numberOfPages; i++) {
      int pNum = document.getNumberOfPages() - numberOfPages + i;
      System.out.println("Adding page " + pNum);
      sourcePages.add(PDFPages.getPageFromDocument(document, pNum));
    }

    // Move pages to targetPage location

    PDFPages.insertPagesAt(sourceDocument, targetPageNum, sourcePages, true);

  }

  @Override
  public String GetName() {
    return "Inserting page " + (sourcePageNum + 1) + " from " + sourceFile.getFileName() + " to page "
        + targetPageNum;
  }

}
