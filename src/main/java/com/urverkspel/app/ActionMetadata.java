package com.urverkspel.app;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.json.JSONObject;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.impl.XMPMetaParser;
import com.adobe.internal.xmp.impl.XMPSerializerRDF;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.options.SerializeOptions;

// https://javadoc.io/static/com.adobe.xmp/xmpcore/6.0.6/overview-summary.html
// http://useof.org/java-open-source/org.apache.pdfbox.pdmodel.common.PDMetadata

public class ActionMetadata extends Configuration.Action {

  String title;
  String author;
  String description;
  String[] keywords;

  static XMPSerializerRDF serializer = new XMPSerializerRDF();

  @Override
  public void Load(JSONObject configFragment) {

    title = Configuration.GetStringIfKeyExists("title", configFragment);
    author = Configuration.GetStringIfKeyExists("author", configFragment);
    description = Configuration.GetStringIfKeyExists("subject", configFragment);

    keywords = Configuration.GetStringArrayIfKeyExists("keywords", configFragment);
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    applyClassic(document);

    // Get metadata from object & parse it to Adobe xmp's XMPMeta
    PDMetadata metadata = document.getDocumentCatalog().getMetadata();
    InputStream xmpInputStream = metadata.exportXMPMetadata();

    byte[] xmpBytes = xmpInputStream.readAllBytes();
    XMPMeta meta = XMPMetaParser.parse(xmpBytes, null);

    // Do the changes
    setXmpArrayProperty(meta, "creator", author);
    setXmpArrayProperty(meta, "title", title);
    setXmpArrayProperty(meta, "description", description);
    setXmpArrayProperty(meta, "subject", keywords);

    // Serialize the XMPMeta & convert it back to an OutputStream so PDFBox'
    // metadata can ingest it
    OutputStream xmpOutputStream = new ByteArrayOutputStream();

    serializer.serialize(meta, xmpOutputStream, new SerializeOptions());
    xmpBytes = xmpOutputStream.toString().getBytes();

    // System.out.println(xmpOutputStream.toString());

    metadata.importXMPMetadata(xmpBytes);
    document.getDocumentCatalog().setMetadata(metadata);
  }

  private void setXmpArrayProperty(XMPMeta meta, String propName, String value) throws Exception {

    if (value.isBlank())
      return;

    meta.setArrayItem(XMPConst.NS_DC, propName, 1, value);
  }

  private void setXmpArrayProperty(XMPMeta meta, String propName, String[] value) throws Exception {

    // Completely clear/remove the old array
    if (meta.doesPropertyExist(XMPConst.NS_DC, propName)) {
      meta.deleteProperty(XMPConst.NS_DC, propName);
    }

    PropertyOptions options = new PropertyOptions();
    options.setArray(true);

    // Append the new items
    for (String part : value) {
      meta.appendArrayItem(XMPConst.NS_DC, propName, options, part, null);
    }
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