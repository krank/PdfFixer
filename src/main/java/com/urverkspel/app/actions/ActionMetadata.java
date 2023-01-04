package com.urverkspel.app.actions;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.json.JSONObject;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPMeta;

import com.urverkspel.app.*;

// https://javadoc.io/static/com.adobe.xmp/xmpcore/6.0.6/overview-summary.html
// http://useof.org/java-open-source/org.apache.pdfbox.pdmodel.common.PDMetadata

public class ActionMetadata extends Configuration.Action {

  String title;
  String author;
  String description;
  List<String> keywords;
  
  public ActionMetadata(Configuration config, JSONObject configFragment) {
    super(config, configFragment);

    title = Configuration.getStringIfKeyExists("title", configFragment);
    author = Configuration.getStringIfKeyExists("author", configFragment);
    description = Configuration.getStringIfKeyExists("description", configFragment);
  
    keywords = Configuration.getStringArrayIfKeyExists("keywords", configFragment);
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    applyClassic(document);

    // Load meta
    XMPMeta meta = PDFMetadata.getMetaFromDocument(document);

    // Do the changes
    PDFMetadata.setXmpArrayProperty(meta, "creator", List.of(author));
    PDFMetadata.setXmpArrayProperty(meta, "title", List.of(title));
    PDFMetadata.setXmpArrayProperty(meta, "description", List.of(description));
    PDFMetadata.setXmpArrayProperty(meta, "subject", keywords);
    
    // meta.setArrayItem(XMPConst.NS_PDF, "Keywords", 1, "");
    meta.setProperty(XMPConst.NS_PDF, "Keywords", String.join(",", keywords));

    // Save meta
    PDFMetadata.SaveMetaToDocument(document, meta);
  }

  private void applyClassic(PDDocument document) {
    PDDocumentInformation information = document.getDocumentInformation();

    if (author != "")
      information.setAuthor(author);
    if (title != "")
      information.setTitle(title);
    if (description != "")
      information.setSubject(description);
    if (keywords.size() > 0)
      information.setKeywords(String.join(",", keywords));

    document.setDocumentInformation(information);
  }

  @Override
  public String GetName() {
    return "Edit metadata";
  }

}