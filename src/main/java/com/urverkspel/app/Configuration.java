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

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

public class Configuration {

  private String name;
  private String inputFileName;
  private String outputFileName;

  public static String subReportIndent = "    ";

  private ArrayList<Action> actions = new ArrayList<Action>();

  public Configuration(Path jsonConfigFile) throws Exception {

    // Read the config file
    String configJson = Files.readString(jsonConfigFile);
    JSONObject configObject = new JSONObject(configJson);

    // Get the config contents
    name = GetStringIfKeyExists("configName", configObject);
    inputFileName = GetStringIfKeyExists("inputFileName", configObject);
    outputFileName = GetStringIfKeyExists("outputFileName", configObject);

    // Check for actions
    if (configObject.has("actions")) {
      JSONArray actionsArray = configObject.getJSONArray("actions");

      for (int i = 0; i < actionsArray.length(); i++) {
        Object potentialAction = actionsArray.get(i);

        handlePotentialAction(potentialAction);
      }
    }
  }

  private void handlePotentialAction(Object potentialActionConfig) {

    if (potentialActionConfig instanceof JSONObject && ((JSONObject) potentialActionConfig).has("action")) {

      // Convert the action config object to a JSONObject & read its action name
      JSONObject actionConfig = (JSONObject) potentialActionConfig;
      String actionName = actionConfig.getString("action");

      
      // Create the action based on the action name
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
      } else if (actionName.equals("insertFrom")) {
        newAction = new ActionPageInsertFrom();
      } else {
        return;
      }

      newAction.Load(actionConfig);
      actions.add(newAction);
    }
  }

  public void ApplyActionsTo(PDDocument document) throws Exception {
    for (Action action : actions) {
      try {
        System.out.print(" * " + action.GetName() + " ...");
        action.ApplyTo(document);
        System.out.println(colorize(" done", Attribute.GREEN_TEXT()));
      } catch (Exception ex) {
        System.out.println(colorize(" error", Attribute.BRIGHT_RED_TEXT()));
        System.out.println(subReportIndent + ex.getMessage() + "\n");
      }
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

  public Path GetOutputFile() {
    return Paths.get(outputFileName);
  }

  public String getName() {
    return name;
  }

  public static void writeSubReport(String message) {
    System.out.println(subReportIndent + message);
  }

  public static abstract class Action {

    public abstract void Load(JSONObject configFragment);

    public abstract void ApplyTo(PDDocument document) throws Exception;

    public abstract String GetName();
  }

}
