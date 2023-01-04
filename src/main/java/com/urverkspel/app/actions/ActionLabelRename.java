package com.urverkspel.app.actions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

import com.urverkspel.app.*;

public class ActionLabelRename extends Configuration.Action {
  
  String oldLabel;
  String newLabel;
  int maxTimes = -1;

  public ActionLabelRename(Configuration config, JSONObject configFragment) {
    super(config, configFragment);

    oldLabel = Configuration.getStringIfKeyExists("from", configFragment);
    newLabel = Configuration.getStringIfKeyExists("to", configFragment);
    maxTimes = Configuration.getIntIfKeyExists("maxTimes", configFragment);

  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    PDFLayers.renameLabel(document, oldLabel, newLabel, maxTimes);
  }

  @Override
  public String GetName() {
    return "Renaming label(s) named '" + oldLabel + "'' to '" + newLabel + "'";
  }

}
