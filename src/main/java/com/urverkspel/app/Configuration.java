package com.urverkspel.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Configuration {

  private String name;
  private String inputFileName;
  private String outputFileName;

  private ArrayList<Action> actions = new ArrayList<Action>();

  public Configuration(Path jsonConfigFile) throws IOException {

    String configJson = Files.readString(jsonConfigFile);

    JSONObject configObject = new JSONObject(configJson);

    name = GetStringIfKeyExists("configName", configObject);
    inputFileName = GetStringIfKeyExists("inputFileName", configObject);
    outputFileName = GetStringIfKeyExists("outputFileName", configObject);

    // Actions

    if (configObject.has("actions")) {

      JSONArray actionsArray = configObject.getJSONArray("actions");

      for (int i = 0; i < actionsArray.length(); i++) {
        Object potentialAction = actionsArray.get(i);

        if (potentialAction instanceof JSONObject && ((JSONObject) potentialAction).has("action")) {

          JSONObject actionConfig = (JSONObject) potentialAction;
          String actionName = actionConfig.getString("action");

          Action newAction = null;

          if (actionName.equals("setMetadata")) {
            newAction = new ActionMetadata();
          } else if (actionName.equals("removeLayers")) {
            newAction = new ActionLayerRemoval();
          } else if (actionName.equals("addBlankPage")) {
            newAction = new ActionBlankPageInsert();
          } else if (actionName.equals("deletePage")) {
            newAction = new ActionPageDelete();
          } else if (actionName.equals("renameLayerLabel")) {
            newAction = new ActionLabelRename();
          } else {
            continue;
          }

          newAction.Load(actionConfig);
          actions.add(newAction);

        }
      }
    }

  }

  public void ApplyActionsTo(PDDocument document) throws Exception {
    for (Action action : actions) {
      System.out.print(" * " + action.GetName() + " ...");
      action.ApplyTo(document);
      System.out.println(" done.");
    }
  }

  public Boolean sanityCheck() {
    Path outputFile = null;
    Path inputFile = null;

    // Check input & output file validity
    try {
      outputFile = Paths.get(outputFileName);
      inputFile = Paths.get(inputFileName);
    } catch (InvalidPathException ex) {
      if (outputFile == null)
        System.out.println("Output file not valid");
      if (inputFile == null)
        System.out.println("input file not valid");
      return false;
    }

    // Check input file exists & is readable
    if (Files.notExists(GetInputFile())) {
      System.out.println("Input file not found");
      return false;
    }

    if (!Files.isReadable(inputFile)) {
      System.out.println("Input file not readable");
      return false;
    }

    if (Files.exists(outputFile) && !Files.isWritable(outputFile)) {
      System.out.println("Output file not writable");
      return false;
    }

    // Check that input and output files are different
    if (outputFile.toAbsolutePath().equals(inputFile.toAbsolutePath())) {
      System.out.println("Output file & input file cannot be the same");
      return false;
    }

    System.out.println("Config is valid.");
    return true;
  }

  public static String GetStringIfKeyExists(String key, JSONObject object) {
    try {
      return object.getString(key);
    } catch (JSONException e) {
      return "";
    }
  }

  public static Integer GetIntegerIfKeyExists(String key, JSONObject object) {
    try {
      return object.getInt(key);
    } catch (JSONException e) {
      return -1;
    }
  }

  public static String[] GetStringArrayIfKeyExists(String key, JSONObject object) {

    JSONArray items;

    try {
      items = object.getJSONArray(key);
    } catch (JSONException e) {
      return new String[0];
    }

    String[] stringArray = new String[items.length()];

    for (int i = 0; i < stringArray.length; i++) {
      stringArray[i] = items.getString(i);
    }

    return stringArray;
  }

  public Path GetInputFile() {
    return Paths.get(inputFileName);
  }

  public String GetOutputFileName() {
    return outputFileName;
  }

  public String getName() {
    return name;
  }

  public static abstract class Action {

    public abstract void Load(JSONObject configFragment);

    public abstract void ApplyTo(PDDocument document) throws Exception;

    public abstract String GetName();
  }

}
