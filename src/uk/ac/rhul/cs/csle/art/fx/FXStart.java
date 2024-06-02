package uk.ac.rhul.cs.csle.art.fx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.ART;
import uk.ac.rhul.cs.csle.art.script.ARTScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;

public class FXStart extends Application {

  @Override
  public void start(Stage primaryStage) {
    if (ART.tryFilename == null) {
      new ARTScriptTermInterpreter(new ITermsLowLevelAPI()).interpret(ART.specificationString);
      Platform.exit();
      System.exit(0);
    }

    String specContents = "";
    try {
      specContents = Files.readString(Paths.get((ART.specificationString)));
    } catch (IOException e) {
      /* Silently absorb a missing file */ }

    String tryContents = "";
    try {
      tryContents = Files.readString(Paths.get((ART.tryFilename)));
    } catch (IOException e) {
      /* Silently absorb a missing file */ }

    final int w10Vff = 7; // This is a fudge factor for Windows 10 where invisible framing still takes 7 pixels horizontally and 5 vertically

    Rectangle2D screen = Screen.getPrimary().getBounds();
    double windowWidth = screen.getWidth() / 3;

    var graphicsWindow = new GraphicsWindow(new Stage(), 2 * windowWidth - w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(), "ART visualiser",
        new MenuBuilderARTGraphics(), 500.0);

    var tryEditor = new EditorWithConsoleWindow(new Stage(), windowWidth - w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(),
        "Source program: " + ART.tryFilename, new MenuBuilderARTText(), tryContents);

    var specEditor = new EditorWithConsoleWindow(new Stage(), -w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(),
        "ART specification: " + ART.specificationString, new MenuBuilderARTText(), specContents);
  }
}
