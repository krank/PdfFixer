package com.urverkspel.app;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

public class ActionLabelRename extends Configuration.Action {
  
  String oldLabel;
  String newLabel;
  int maxTimes = -1;

  public ActionLabelRename(Configuration config) {
    super(config);
  }
  
  @Override
  public void Load(JSONObject configFragment) {
    oldLabel = Configuration.GetStringIfKeyExists("from", configFragment);
    newLabel = Configuration.GetStringIfKeyExists("to", configFragment);
    maxTimes = Configuration.GetIntIfKeyExists("maxTimes", configFragment);
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    PDFLayers.renameLabel(document, oldLabel, newLabel, maxTimes);
  }

  @Override
  public String GetName() {
    return "Renaming label " + oldLabel + " to " + newLabel;
  }

}
