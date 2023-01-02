package com.urverkspel.app;

import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.json.JSONObject;

public class ActionLayerRemoval extends Configuration.Action {

  List<String> layersToRemove;

  @Override
  public void Load(JSONObject configFragment) {
    layersToRemove = Arrays.asList(Configuration.GetStringArrayIfKeyExists("layerNames", configFragment));

  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    if (layersToRemove.size() == 0)
      return;

    ProgressBar progressBar = new ProgressBar(40, 0, document.getNumberOfPages() - 1, 5);
    System.out.println();

    // Remove contents from all pages
    PDPageTree pageTree = document.getPages();

    for (int i = 0; i < pageTree.getCount(); i++) {
      progressBar.SetValue(i);
      PDPage page = pageTree.get(i);
      PDFLayers.removeLayerContentFromPage(page, layersToRemove, document);
      progressBar.Print(true, true);
    }

    // Remove the groups themselves
    PDFLayers.removeLayersFromDocumentCatalog(document.getDocumentCatalog(),
        layersToRemove);
  }

  @Override
  public String GetName() {
    return "Removing layers [" + String.join(",", layersToRemove) + "]";
  }

}
