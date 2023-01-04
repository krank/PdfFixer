package com.urverkspel.app;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.impl.XMPMetaParser;
import com.adobe.internal.xmp.impl.XMPSerializerRDF;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.options.SerializeOptions;

public class PDFMetadata {

  static XMPSerializerRDF serializer = new XMPSerializerRDF();

  public static XMPMeta getMetaFromDocument(PDDocument document) throws Exception {

    // Get catalog
    PDMetadata metadata = document.getDocumentCatalog().getMetadata();

    // Get & read metadata as bytes
    InputStream xmpInputStream = metadata.exportXMPMetadata();
    byte[] xmpBytes = xmpInputStream.readAllBytes();

    // Parse & return metadata
    return XMPMetaParser.parse(xmpBytes, null);
  }

  public static void SaveMetaToDocument(PDDocument document, XMPMeta meta) throws Exception {
    
    // Serialize the XMPMeta & convert it back to an OutputStream so PDFBox'
    // metadata can ingest it
    OutputStream xmpOutputStream = new ByteArrayOutputStream();

    serializer.serialize(meta, xmpOutputStream, new SerializeOptions());
    byte[] xmpBytes = xmpOutputStream.toString().getBytes();

    PDMetadata metadata = document.getDocumentCatalog().getMetadata();
    metadata.importXMPMetadata(xmpBytes);
    document.getDocumentCatalog().setMetadata(metadata);
  }

  public static void setXmpArrayProperty(XMPMeta meta, String propName, List<String> value)
      throws Exception {

    // If the value is empty, don't do anything
    if (value.size() == 0)
      return;

    // Completely clear/remove the old array
    if (meta.doesPropertyExist(XMPConst.NS_DC, propName)) {
      meta.deleteProperty(XMPConst.NS_DC, propName);
    }

    // Set the array options; we want an array to be created if it doesn't exist (it shouldn't)
    PropertyOptions options = new PropertyOptions();
    options.setArray(true);

    // Append the new items
    for (String part : value) {
      meta.appendArrayItem(XMPConst.NS_DC, propName, options, part, null);
    }
  }

}
