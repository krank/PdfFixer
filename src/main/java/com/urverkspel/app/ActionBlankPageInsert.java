package com.urverkspel.app;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

public class ActionBlankPageInsert extends Configuration.Action {

  int pageTarget = 0;
  
  public ActionBlankPageInsert(Configuration config) {
    super(config);
  }

  @Override
  public void Load(JSONObject configFragment) {
    pageTarget = Configuration.GetIntIfKeyExists("beforePage", configFragment) - 1;
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    PDFPages.insertBlankPageBefore(document, pageTarget);

  }

  @Override
  public String GetName() {
    return "Inserting new blank page before p." + (pageTarget + 1);
  }

}
