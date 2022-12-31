package com.urverkspel.app;

import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.apache.xmpbox.xml.DomXmpParser;
import org.json.JSONObject;

public class ActionMetadata extends Configuration.Action {

  String author;
  String creator;
  String producer;

  String title;
  String subject;
  String keywords;

  @Override
  public void Load(JSONObject configFragment) {

    author = Configuration.GetStringIfKeyExists("author", configFragment);
    creator = Configuration.GetStringIfKeyExists("creator", configFragment);
    producer = Configuration.GetStringIfKeyExists("producer", configFragment);
    title = Configuration.GetStringIfKeyExists("title", configFragment);
    subject = Configuration.GetStringIfKeyExists("subject", configFragment);
    keywords = Configuration.GetStringIfKeyExists("keywords", configFragment);

  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    System.out.println("Editing metadata...");

    // http://useof.org/java-open-source/org.apache.pdfbox.pdmodel.common.PDMetadata

    PDDocumentCatalog catalog = document.getDocumentCatalog();

    PDMetadata metadata = catalog.getMetadata();
    // XMPMetadata xmp = XMPMetadata.createXMPMetadata();

    InputStream xmpStream = metadata.exportXMPMetadata();

    byte[] xmpBytes = new byte[xmpStream.available()];
    xmpBytes = xmpStream.readAllBytes();
    String xmpString = new String(xmpBytes);
    // XmpSerializer serializer = new XmpSerializer();

    // DomXmpParser parser = new DomXmpParser();
    

    // XMPMetadata xmp = parser.parse(xmpBytes);

    // for (XMPSchema schema : xmp.getAllSchemas()) {
    //   System.out.println(schema);
    // }

    // PDMetadata metadata = document.getDocumentCatalog().getMetadata();

    // metadata.

    applyClassic(document);
  }

  private void applyClassic(PDDocument document) {
    PDDocumentInformation information = document.getDocumentInformation();

    if (author != "")
      information.setAuthor(author);
    if (creator != "")
      information.setCreator(creator);
    if (producer != "")
      information.setProducer(producer);
    if (title != "")
      information.setTitle(title);
    if (subject != "")
      information.setSubject(subject);
    if (keywords != "")
      information.setKeywords(keywords);

    document.setDocumentInformation(information);
  }

}