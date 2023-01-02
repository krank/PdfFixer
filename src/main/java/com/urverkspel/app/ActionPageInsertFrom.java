package com.urverkspel.app;

import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.json.JSONObject;

public class ActionPageInsertFrom extends Configuration.Action {

  Path sourceFile = null;
  int sourcePageNum = 0;
  int targetPageNum = 0;

  @Override
  public void Load(JSONObject configFragment) {
    
    String sourceFileName = Configuration.GetStringIfKeyExists("sourceFile", configFragment);
    sourceFile = Path.of(sourceFileName);

    sourcePageNum = Configuration.GetIntegerIfKeyExists(sourceFileName, configFragment) - 1;
    targetPageNum = Configuration.GetIntegerIfKeyExists("targetPage", configFragment) - 1;
  }

  @Override
  public void ApplyTo(PDDocument document) throws Exception {

    
    // Make sure source file exists and is not the same as the target file
    if (!sourceFile.toFile().exists()) {
      throw new Exception("Source file " + sourceFile.getFileName() + " does not exist");
    }

    // Read page(s) from the source file

    // Insert the source pages into the target file
  }

  @Override
  public String GetName() {
    return "Transplanting page " + sourcePageNum + " from " + sourceFile.getFileName() + " to page "
        + targetPageNum;
  }

}
