package com.urverkspel.app.actions;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.json.JSONObject;

import com.urverkspel.app.*;

public class ActionLayerRemove extends Configuration.Action {

  List<String> layersToRemove;
  
  public ActionLayerRemove(Configuration config, JSONObject configFragment) {
    super(config, configFragment);

    layersToRemove = Configuration.getStringArrayIfKeyExists("layerNames", configFragment);
  }


  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    if (layersToRemove.size() == 0)
      return;

    ProgressBar progressBar = new ProgressBar(40, 0, document.getNumberOfPages() - 1, Configuration.subReportIndent);
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
