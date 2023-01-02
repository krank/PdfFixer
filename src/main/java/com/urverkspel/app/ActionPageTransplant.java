package com.urverkspel.app;

import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

public class ActionPageTransplant extends Configuration.Action {

  Path sourceFile = null;
  int sourcePageNum = 0;
  int targetPageNum = 0;

  @Override
  public void Load(JSONObject configFragment) {
    String sourceFileName = Configuration.GetStringIfKeyExists("sourceFile", configFragment);
    sourceFile = Path.of(sourceFileName);

    sourcePageNum = configFragment.getInt("sourcePageNum");
    targetPageNum = configFragment.getInt("targetPageNum");

  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public String GetName() {
    return "Transplanting page " + sourcePageNum + " from " + sourceFile.getFileName() + " to page "
        + targetPageNum;
  }

}
