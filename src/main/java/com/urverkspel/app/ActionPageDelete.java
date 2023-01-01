package com.urverkspel.app;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

public class ActionPageDelete extends Configuration.Action {

  int pageTarget = 0;

  @Override
  public void Load(JSONObject configFragment) {
    pageTarget = Configuration.GetIntegerIfKeyExists("pageNumber", configFragment) - 1;
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    PDFPages.deletePage(document, pageTarget);
  }

  @Override
  public String GetName() {
    return "Removing page " + (pageTarget + 1) + ".";
  }

}
