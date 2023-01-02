package com.urverkspel.app;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
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

    if (targetPageNum < 0) {
      throw new Exception("Target page number is not set");
    }

    // Merge the source file into the target file (1)

    // Remove unwanted pages from target file (3)

    // Move remaining pages to targetPage location (2)



    // Path firstFile = Path.of("kungen.pdf");
    // Path secondFile = Path.of("Omslag_separerade.pdf");
    // PDDocument firstDocument = Loader.loadPDF(firstFile.toFile());
    // PDDocument secondDocument = Loader.loadPDF(secondFile.toFile());

    // PDFMergerUtility merger = new PDFMergerUtility();

    // merger.appendDocument(firstDocument, secondDocument);
    
    // PDPage p = PDFPages.getPageFromDocument(firstDocument, firstDocument.getNumberOfPages() - 2);
    
    // firstDocument.removePage(p);

    // PDFPages.insertPageAt(firstDocument, 0, p);
    
    // firstDocument.save("test.pdf");
    // firstDocument.close();
    // secondDocument.close();

    // Read page(s) from the source file
    // PDDocument sourceDocument = Loader.loadPDF(sourceFile.toFile());
    // if (sourcePageNum >= 0) {




      // Configuration.writeSubReport("bajs");

      // // Single page
      // sourcePageNum = PDFPages.getBoundedPageNumber(sourceDocument, sourcePageNum);

      // PDPage sourcePage = PDFPages.getPageFromDocument(sourceDocument, sourcePageNum);

      // document.importPage(sourcePage);

      // document.addPage(newPage);
      // pageDictionary.setItem("Parent", document.getDocumentCatalog().getCOSObject());

      // PDFPages.insertPageAt(document, 7, newPage);

      // PDFMergerUtility merger = new PDFMergerUtility();
      

    // }

    // if (sourcePageNumFirst >= 0 && sourcePageNumLast >= 0) {

    //   // Range of pages
    //   sourcePageNumFirst = PDFPages.getBoundedPageNumber(sourceDocument, sourcePageNumFirst);
    //   sourcePageNumLast = PDFPages.getBoundedPageNumber(sourceDocument, sourcePageNumLast);

    //   for (int i = sourcePageNumLast; i >= sourcePageNumFirst; i--) {
    //     PDPage sourcePage = PDFPages.getPageFromDocument(sourceDocument, i);
    //     PDFPages.InsertPageAt(document, targetPageNum, sourcePage);
    //   }

    // }
  }

  @Override
  public String GetName() {
    return "Inserting page " + (sourcePageNum + 1) + " from " + sourceFile.getFileName() + " to page "
        + targetPageNum;
  }

}
