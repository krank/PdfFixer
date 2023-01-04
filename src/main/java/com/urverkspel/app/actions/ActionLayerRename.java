package com.urverkspel.app.actions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

import com.urverkspel.app.*;

public class ActionLayerRename extends Configuration.Action {

  String oldName;
  String newName;
  int maxTimes = -1;

  public ActionLayerRename(Configuration config, JSONObject configFragment) {
    super(config, configFragment);

    oldName = Configuration.getStringIfKeyExists("from", configFragment);
    newName = Configuration.getStringIfKeyExists("to", configFragment);
    maxTimes = Configuration.getIntIfKeyExists("maxTimes", configFragment);

  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    PDFLayers.renameLayer(document, oldName, newName, maxTimes);
  }

  @Override
  public String GetName() {
    return "Renaming layer(s) named '" + oldName + "'' to '" + newName + "'";
  }
  
}
