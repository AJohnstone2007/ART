package uk.ac.rhul.cs.csle.art.fx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.ART;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class FXStart extends Application {

  @Override
  public void start(Stage primaryStage) {

    if (ART.clargs[0].equals("fx")) {
      var graphicsWindow = new GraphicsWindow(new Stage(), 700, 0, 800, 400, "ART visualiser", new MenuBuilderARTGraphics(), 500.0);
      new ScriptTermInterpreter(ART.scriptString(Arrays.copyOfRange(ART.clargs, 1, ART.clargs.length)));
      // Platform.exit();
      // System.exit(0);
    }

    else if (ART.clargs[0].equals("ide")) {
      String specFilename = ART.clargs.length > 1 ? ART.clargs[1] : "", specContents = "";
      try {
        specContents = Files.readString(Paths.get(specFilename));
      } catch (IOException e) {
        /* Silently absorb a missing file */ }

      String tryFilename = ART.clargs.length > 2 ? ART.clargs[2] : "", tryContents = "";
      try {
        tryContents = Files.readString(Paths.get((tryFilename)));
      } catch (IOException e) {
        /* Silently absorb a missing file */ }

      final int w10Vff = 7; // This is a fudge factor for Windows 10 where invisible framing still takes 7 pixels horizontally and 5 vertically

      Rectangle2D screen = Screen.getPrimary().getBounds();
      double windowWidth = screen.getWidth() / 3;

      var graphicsWindow = new GraphicsWindow(new Stage(), 2 * windowWidth - w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(), "ART visualiser",
          new MenuBuilderARTGraphics(), 500.0);

      var tryEditor = new EditorWithConsoleWindow(new Stage(), windowWidth - w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(),
          "Source program: " + tryFilename, new MenuBuilderARTText(), tryContents);

      var specEditor = new EditorWithConsoleWindow(new Stage(), -w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(), "ART specification: " + specFilename,
          new MenuBuilderARTText(), specContents);
    } else
      Util.fatal("Unexpected argument to FXStart " + ART.clargs[0]);
  }
}