package com.urverkspel.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdfparser.PDFStreamParser;
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
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;

public class PDFLayers {

  public static void printLayerNames(PDDocument document) {
    PDOptionalContentProperties props = document.getDocumentCatalog().getOCProperties();

    String[] names = props.getGroupNames();
    for (String name : names) {
      System.out.println(name);
    }
  }

  public static void renameLabel(PDDocument document, String oldName, String newName, int maxTimes) {

    // Find the D
    COSDictionary d = (COSDictionary) document.getDocumentCatalog().getOCProperties().getCOSObject().getItem("D");

    // Find the Order object & extract the array
    COSObject orderCOS = (COSObject) d.getItem("Order");
    COSArray orderArray = (COSArray) orderCOS.getObject();

    // Iterate through the sub-arrays
    for (int i = 0; i < orderArray.size(); i++) {

      COSArray orderSubArray = null;

      // Get the sub-array
      COSBase orderSubCOS = orderArray.get(i);
      if (orderSubCOS instanceof COSArray) {
        orderSubArray = (COSArray) orderSubCOS;
      } else if (orderSubCOS instanceof COSObject) {
        COSObject intermediaryObject = (COSObject) orderSubCOS.getCOSObject();
        orderSubArray = (COSArray) intermediaryObject.getObject();
      } else {
        continue;
      }

      // Iterate over the array and find the string we want to replace
      for (int j = 0; j < orderSubArray.size(); j++) {
        COSBase orderedItem = orderSubArray.get(j);
        if (orderedItem instanceof COSString) {
          COSString labelString = (COSString) orderedItem;
          if (labelString.getString().equals(oldName) || oldName.isBlank()) {
            orderSubArray.set(j, new COSString(newName));
            maxTimes--;
            break;
          }
        }
      }

      if (maxTimes == 0) {
        break;
      }
    }

    COSObject orderSubCOS = (COSObject) orderArray.get(0);
    COSArray orderSubArray = (COSArray) orderSubCOS.getObject();

    // Iterate over the array and find the string we want to replace
    for (int i = 0; i < orderSubArray.size(); i++) {
      COSBase orderedItem = orderSubArray.get(i);
      if (orderedItem instanceof COSString) {
        COSString labelString = (COSString) orderedItem;
        if (labelString.getString().equals(oldName) || oldName.isBlank()) {
          orderSubArray.set(i, new COSString(newName));
          break;
        }
      }
    }
  }

  public static void removeLayersFromDocumentCatalog(PDDocumentCatalog catalog, List<String> layersToRemove) {

    PDOptionalContentProperties props = catalog.getOCProperties();

    // Remove the OCGs
    COSDictionary ocgsDict = (COSDictionary) props.getCOSObject();
    COSArray ocgs = (COSArray) ocgsDict.getItem(COSName.OCGS);

    List<Integer> toRemove = new ArrayList<Integer>();

    for (int i = 0; i < ocgs.size(); i++) {

      COSObject o = (COSObject) ocgs.get(i);
      COSDictionary dict = (COSDictionary) o.getObject();

      if (layersToRemove.contains(dict.getString(COSName.NAME))) {
        toRemove.add(i);
      }
    }

    for (int i = toRemove.size() - 1; i >= 0; i--) {
      ocgs.remove(toRemove.get(i));
    }

    ocgsDict.setItem(COSName.OCGS, ocgs);
  }

  public static void removeLayerContentFromPage(PDPage page, List<String> layersToRemove, PDDocument document)
      throws IOException {

    PDResources resources = page.getResources();

    // Parse the page's tokens
    PDFStreamParser parser = new PDFStreamParser(page);
    List<Object> tokens = parser.parse();

    // Make new token list
    ArrayList<Object> newTokens = new ArrayList<Object>();

    String currentLayer = null;
    int mcLevel = 0;
    ArrayList<Object> line = new ArrayList<Object>();

    // Go through all tokens
    for (int i = 0; i < tokens.size(); i++) {

      // Get token & add to line
      Object objectToken = tokens.get(i);
      line.add(objectToken);

      // If token is operator, treat line as finished & act on it
      if (objectToken instanceof Operator) {
        Operator operator = (Operator) objectToken;

        // Do different things depending on type of operator

        if (operator.getName().equals(OperatorName.BEGIN_MARKED_CONTENT_SEQ)
            || operator.getName().equals(OperatorName.BEGIN_MARKED_CONTENT)) {

          // Check if this is the beginning of an OCG
          String OCGname = isOCGBeginning(line, resources);

          if (OCGname != null) {
            currentLayer = OCGname;
          } else {
            mcLevel++;
          }

        } else if (operator.getName().equals(OperatorName.END_MARKED_CONTENT)) {
          if (mcLevel > 0) {
            mcLevel--;
          }
          if (mcLevel == 0 && currentLayer != null) {

            if (layersToRemove.contains(currentLayer)) {
              line.clear(); // EMC tokens that end a hidden layer should not be included
            }

            currentLayer = null;
          }
        }

        // Maybe add tokens
        if (!layersToRemove.contains(currentLayer)
            || PDFTokens.gfxContextOperators.contains(operator.getName())) {

          for (Object token : line) {
            newTokens.add(token);
          }
        }

        // System.out.println(PDFTokens.makeLineString(line));

        line.clear();
      }
    }

    // replace page's contents
    PDStream newStream = PDFTokens.makeStreamFromTokenList(newTokens, document);
    page.setContents(newStream);

  }

  private static String isOCGBeginning(List<Object> tokens, PDResources pageResources) {

    // If there's no room, return
    if (tokens.size() < 3)
      return null;

    // COSNAME 1
    Object objectToken = tokens.get(0);

    // If token isn't a COSName, it's not the beginning of an OCG
    if (!(objectToken instanceof COSName) || (COSName) objectToken != COSName.OC)
      return null;

    // COSNAME 2

    // If next in line isn't a COSname, return
    objectToken = tokens.get(1);
    if (!(objectToken instanceof COSName))
      return null;

    // If the second COSname isn't associated with properties, return
    PDPropertyList pList = pageResources.getProperties((COSName) objectToken);
    if (pList == null || !(pList instanceof PDOptionalContentGroup))
      return null;

    // BDC

    // If next in line isn't a BDC, return
    objectToken = tokens.get(2);

    if (!(objectToken instanceof Operator)
        || !((Operator) objectToken).getName().equals(OperatorName.BEGIN_MARKED_CONTENT_SEQ))
      return null;

    return ((PDOptionalContentGroup) pList).getName();
  }

}