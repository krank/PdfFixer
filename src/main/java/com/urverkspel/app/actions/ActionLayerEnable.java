package com.urverkspel.app.actions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

import com.urverkspel.app.*;

public class ActionLayerEnable extends Configuration.Action {

  String layerName;
  boolean enabled;

  public ActionLayerEnable(Configuration config, JSONObject configFragment) {
    super(config, configFragment);

    layerName = Configuration.getStringIfKeyExists("layerName", configFragment);
    enabled = Configuration.getBooleanIfKeyExists("enabled", configFragment);

  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    PDFLayers.setLayerEnabled(document, layerName, enabled);
  }

  @Override
  public String GetName() {
    if (enabled) {
      return "Enable layer '" + layerName + "'";
    } else {
      return "Disable layer '" + layerName + "'";
    }
  }
  
}
