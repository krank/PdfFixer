package com.urverkspel.app;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.json.JSONObject;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPMeta;

// https://javadoc.io/static/com.adobe.xmp/xmpcore/6.0.6/overview-summary.html
// http://useof.org/java-open-source/org.apache.pdfbox.pdmodel.common.PDMetadata

public class ActionMetadata extends Configuration.Action {

  String title;
  String author;
  String description;
  String[] keywords;
  
  public ActionMetadata(Configuration config) {
    super(config);
  }

  @Override
  public void Load(JSONObject configFragment) {

    title = Configuration.GetStringIfKeyExists("title", configFragment);
    author = Configuration.GetStringIfKeyExists("author", configFragment);
    description = Configuration.GetStringIfKeyExists("description", configFragment);

    keywords = Configuration.GetStringArrayIfKeyExists("keywords", configFragment);
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    applyClassic(document);

    // Load meta
    XMPMeta meta = PDFMetadata.getMetaFromDocument(document);

    // Do the changes
    PDFMetadata.setXmpArrayProperty(meta, "creator", author);
    PDFMetadata.setXmpArrayProperty(meta, "title", title);
    PDFMetadata.setXmpArrayProperty(meta, "description", description);
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
    if (keywords.length > 0)
      information.setKeywords(String.join(",", keywords));

    document.setDocumentInformation(information);
  }

  @Override
  public String GetName() {
    return "Edit metadata";
  }

}