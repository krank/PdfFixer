package com.urverkspel.app;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PDFPages {

  public static int getBoundedPageNumber(PDDocument document, int pageNum) {
    int maxPageNum = document.getNumberOfPages();
    if (pageNum < 0) {
      return 0;
    } else if (pageNum >= maxPageNum) {
      return maxPageNum - 1;
    } else {
      return pageNum;
    }
  }

  public static void analyzePage(PDDocument document, int pageNum) throws Exception {

    PDPage page = document.getPage(pageNum);

    StringBuilder lineBuilder = new StringBuilder();
    StringBuilder pageBuilder = new StringBuilder();

    // PDResources resources = page.getResources();

    PDFStreamParser parser = new PDFStreamParser(page);
    List<Object> tokens = parser.parse();

    int operatorCount = 0;

    for (int i = 0; i < tokens.size(); i++) {
      Object currentToken = tokens.get(i);

      if (currentToken instanceof COSName) {
        lineBuilder.append("n" + ((COSName) currentToken).getName());

      } else if (currentToken instanceof Operator) {
        lineBuilder.insert(0, operatorCount + ": ");
        operatorCount++;
        lineBuilder.append(((Operator) currentToken).getName());

        // End of the line
        pageBuilder.append(lineBuilder + System.lineSeparator());
        lineBuilder = new StringBuilder();
      } else {
        lineBuilder.append(currentToken);
      }

      lineBuilder.append(" || ");

    }

    System.out.println(pageBuilder);

  }

  public static void deletePage(PDDocument document, int pageNum) {
    PDPage page = getPageFromDocument(document, pageNum);
    document.removePage(page);
  }

  public static void insertBlankPageBefore(PDDocument document, int pageNum) {

    // Adobe: Always previous page, unless first, then use previous first
    PDPage prototype = getPageFromDocument(document, pageNum - 1);

    // Create new page & set boxes
    PDPage newPage = new PDPage(prototype.getMediaBox());
    newPage.setArtBox(prototype.getArtBox());
    newPage.setBleedBox(prototype.getBleedBox());
    newPage.setCropBox(prototype.getCropBox());

    // Insert new page
    insertPageAt(document, pageNum, newPage, false);

  }

  public static void insertPageAt(PDDocument document, int pageNum, PDPage page, boolean removePageFromDocumentFirst) {

    insertPagesAt(document, pageNum, List.of(page), removePageFromDocumentFirst);

  }

  
  public static void insertPagesAt(PDDocument document, int pageNum, List<PDPage> pages, boolean removePagesFromDocumentFirst) {

    PDPage currentPageAtLocation = getPageFromDocument(document, pageNum);

    for (PDPage page : pages) {

      // If page is already at location, skip it
      if (page.equals(currentPageAtLocation)) {
        continue;
      }

      // In case page is already in document, remove it
      if (removePagesFromDocumentFirst)
      {
        document.removePage(page);
      }

      if (document.getNumberOfPages() == 0) { // If document is empty, add pages
        document.addPage(page);
        
      } else if (pageNum < document.getNumberOfPages()) { // If page number is in bounds, insert pages
        document.getPages().insertBefore(page, currentPageAtLocation);
        
      } else { // If page number is out of bounds, insert pages at end
        document.getPages().insertAfter(page, currentPageAtLocation);
      }
    }
  }

  public static PDPage getPageFromDocument(PDDocument document, int pageNum) {

    // If page number is out of bounds, use first or last page
    pageNum = getBoundedPageNumber(document, pageNum);

    return document.getPage(pageNum);
  }

  public static List<PDPage> getPagesFromDocument(String rangeString) {

    // STUB
    return null;
  }

}
