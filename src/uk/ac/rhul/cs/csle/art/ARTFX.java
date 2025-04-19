package uk.ac.rhul.cs.csle.art;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;

public class ARTFX extends Application {
  public static Stage primaryStage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    ARTFX.primaryStage = primaryStage;
    new ScriptTermInterpreter(ART.scriptString(getParameters().getRaw().toArray(new String[0])));
  }
}
