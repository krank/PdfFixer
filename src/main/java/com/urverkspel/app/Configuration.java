package com.urverkspel.app;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

import com.urverkspel.app.actions.*;

public class Configuration {

  private String name;
  private String inputFileName;
  private String outputFileName;

  private Path configPath;

  public static String subReportIndent = "    ";

  private ArrayList<Action> actions = new ArrayList<Action>();

  public Configuration(Path jsonConfigFile) throws Exception {

    configPath = jsonConfigFile.toAbsolutePath().getParent();

    // Read the config file
    String configJson = Files.readString(jsonConfigFile);
    JSONObject configObject = new JSONObject(configJson);

    // Get the config contents
    name = getStringIfKeyExists("configName", configObject);
    inputFileName = getStringIfKeyExists("inputFileName", configObject);
    outputFileName = getStringIfKeyExists("outputFileName", configObject);

    // Check for actions
    if (configObject.has("actions")) {
      JSONArray actionsArray = configObject.getJSONArray("actions");

      for (int i = 0; i < actionsArray.length(); i++) {
        Object potentialAction = actionsArray.get(i);

        handlePotentialAction(potentialAction);
      }
    }

    sanityCheck();

  }

  private void handlePotentialAction(Object potentialActionConfig) {

    if (potentialActionConfig instanceof JSONObject && ((JSONObject) potentialActionConfig).has("action")) {

      // Convert the action config object to a JSONObject & read its action name
      JSONObject actionConfig = (JSONObject) potentialActionConfig;
      String actionName = actionConfig.getString("action");

      // Create the action based on the action name
      Action newAction = null;

      // Metadata
      if (actionName.equals("setMetadata")) {
        newAction = new ActionMetadata(this, actionConfig);

        // Layers
      } else if (actionName.equals("removeLayers")) {
        newAction = new ActionLayerRemove(this, actionConfig);

      } else if (actionName.equals("renameLayer")) {
        newAction = new ActionLayerRename(this, actionConfig);

      } else if (actionName.equals("renameLayerLabel")) {
        newAction = new ActionLabelRename(this, actionConfig);

      } else if (actionName.equals("enableLayer")) {
        newAction = new ActionLayerEnable(this, actionConfig);

        // Pages
      } else if (actionName.equals("addBlankPage")) {
        newAction = new ActionBlankPageInsert(this, actionConfig);

      } else if (actionName.equals("insertFrom")) {
        newAction = new ActionPageInsertFrom(this, actionConfig);

      } else if (actionName.equals("deletePage")) {
        newAction = new ActionPageDelete(this, actionConfig);

      } else {
        return;
      }

      actions.add(newAction);
    }
  }

  public void applyActionsTo(PDDocument document) throws Exception {
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

  public Boolean sanityCheck() throws Exception {
    Path outputFile = null;
    Path inputFile = null;

    // Check input & output file validity
    outputFile = Paths.get(configPath.toString(), outputFileName);
    inputFile = Paths.get(configPath.toString(), inputFileName);

    // Check input file exists & is readable
    if (Files.notExists(getInputFile())) {
      throw new NoSuchFileException("Input file not found");
    }

    if (!Files.isReadable(inputFile)) {
      throw new Exception("Input file not readable");
    }

    if (Files.exists(outputFile) && !Files.isWritable(outputFile)) {
      throw new AccessDeniedException("Output file not writable");
    }

    // Check that input and output files are different
    if (outputFile.toAbsolutePath().equals(inputFile.toAbsolutePath())) {
      throw new Exception("Output file & input file cannot be the same");
    }

    return true;
  }

  public static String getStringIfKeyExists(String key, JSONObject object) {
    try {
      return object.getString(key);
    } catch (JSONException e) {
      return "";
    }
  }

  public static Integer getIntIfKeyExists(String key, JSONObject object) {
    try {
      return object.getInt(key);
    } catch (JSONException e) {
      return -1;
    }
  }

  public static Boolean getBooleanIfKeyExists(String key, JSONObject object) {
    try {
      return object.getBoolean(key);
    } catch (JSONException e) {
      return false;
    }
  }

  public static List<String> getStringArrayIfKeyExists(String key, JSONObject object) {

    JSONArray items;

    try {
      items = object.getJSONArray(key);
    } catch (JSONException e) {
      return Collections.emptyList();
    }

    ArrayList<String> stringList = new ArrayList<String>();

    for (int i = 0; i < items.length(); i++) {
      stringList.add(items.getString(i));
    }

    return stringList;
  }

  public static void writeSubReport(String message) {
    System.out.println(subReportIndent + message);
  }

  public Path getConfigPath() {
    return configPath;
  }

  public Path getConfigRelativePath(String path) {
    return Paths.get(configPath.toString(), path).normalize();
  }

  public Path getInputFile() {
    return Paths.get(configPath.toString(), inputFileName);
  }

  public Path getOutputFile() {
    return Paths.get(configPath.toString(), outputFileName);
  }

  public String getName() {
    return name;
  }

  public static abstract class Action {

    protected Configuration config;

    public Action(Configuration config, JSONObject configFragment) {
      this.config = config;
    }

    public abstract void ApplyTo(PDDocument document) throws Exception;

    public abstract String GetName();
  }

}
