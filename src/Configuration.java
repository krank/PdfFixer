import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.jar.JarEntry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONArray;
import org.json.JSONObject;

public class Configuration {

  private String name;
  private String inputFileName;
  private String outputFileName;

  private ArrayList<Action> actions = new ArrayList<>();

  public Configuration(Path jsonConfigFile) throws IOException {

    String configJson = Files.readString(jsonConfigFile);

    JSONObject configObject = new JSONObject(configJson);

    name = configObject.has("configName") ? configObject.get("configName").toString() : "";
    inputFileName = configObject.has("inputFileName") ? configObject.get("inputFileName").toString() : "";
    outputFileName = configObject.has("outputFileName") ? configObject.get("outputFileName").toString() : "";

    // Actions

    if (configObject.has("actions")) {
      JSONArray actionsArray = configObject.getJSONArray("actions");

      for (Object potentialAction : actionsArray) {
        if (potentialAction instanceof JSONObject && ((JSONObject) potentialAction).has("action")) {

          JSONObject actionConfig = (JSONObject) potentialAction;
          String actionName = actionConfig.getString("action");

          Action newAction = null;

          if (actionName.equals("setMetadata")) {
            newAction = new ActionMetadata();
          } else {
            break;
          }

          newAction.Load(actionConfig);
          actions.add(newAction);
        }
      }
    }
  }

  public void ApplyActionsTo(PDDocument document) {
    for (Action action : actions) {
      action.ApplyTo(document);
    }
  }

  public Boolean sanityCheck() {

    // Check input file
    if (Files.notExists(GetInputFile())) {
      System.out.println("Input file not found");
      return false;
    }

    // Check output file
    try {
      Paths.get(outputFileName);
    } catch (InvalidPathException ex) {
      System.out.println("Output file not valid");
      return false;
    }

    return true;
  }

  public static String GetStringIfKeyExists(String key, JSONObject object) {
    return object.has(key) ? object.get(key).toString() : "";
  }

  public Path GetInputFile() {
    return Paths.get(inputFileName);
  }

  public String GetOutputFileName() {
    return outputFileName;
  }

  public static abstract class Action {

    public abstract void Load(JSONObject configFragment);

    public abstract void ApplyTo(PDDocument document);
  }

}
