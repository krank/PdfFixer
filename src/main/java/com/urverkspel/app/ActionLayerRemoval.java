package com.urverkspel.app;

import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

public class ActionLayerRemoval extends Configuration.Action {

  String[] layersToRemove;

  @Override
  public void Load(JSONObject configFragment) {
    layersToRemove = Configuration.GetStringArrayIfKeyExists("layerNames", configFragment);
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    if (layersToRemove.length == 0)
      return;

    PDFLayers.removeLayersAndContentsFromDocument(document, Arrays.asList(layersToRemove));
  }

  @Override
  public String GetName() {
    return "Removing layers [" + String.join(",", layersToRemove) + "]";
  }

}
