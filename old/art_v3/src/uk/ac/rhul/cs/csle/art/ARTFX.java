package uk.ac.rhul.cs.csle.art;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.util.ARTException;

public class ARTFX extends Application {

  public static void main(final String[] arguments) throws FileNotFoundException, ARTException {
    Application.launch(arguments);
  }

  @Override
  public void start(final Stage stage) throws IOException, ARTException {
    new ARTManager().fxMode("test.art", "test.str");
  }
}
