package com.urverkspel.app.actions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

import com.urverkspel.app.*;

public class ActionPageDelete extends Configuration.Action {

  int pageTarget = 0;

  public ActionPageDelete(Configuration config, JSONObject configFragment) {
    super(config, configFragment);
    
    pageTarget = Configuration.getIntIfKeyExists("pageNumber", configFragment) - 1;
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    PDFPages.deletePage(document, pageTarget);
  }

  @Override
  public String GetName() {
    return "Removing page " + (pageTarget + 1);
  }

}
