package com.urverkspel.app.actions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

import com.urverkspel.app.*;

public class ActionBlankPageInsert extends Configuration.Action {

  int pageTarget = 0;
  
  public ActionBlankPageInsert(Configuration config, JSONObject configFragment) {
    super(config, configFragment);
    pageTarget = Configuration.getIntIfKeyExists("beforePage", configFragment) - 1;
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    PDFPages.insertBlankPageBefore(document, pageTarget);

  }

  @Override
  public String GetName() {
    return "Inserting new blank page before page" + (pageTarget + 1);
  }

}
