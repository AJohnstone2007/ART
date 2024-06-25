package uk.ac.rhul.cs.csle.art.cava;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerConsole;

public class CavaFX extends Application {
  ARTCavaParser parser = new ARTCavaParser(new ARTCavaLexer());
  static ARTText text = new ARTText(new ARTTextHandlerConsole());

  public class ParserAsRunnable implements Runnable {

    @Override
    public void run() {
      try {
        parser.artEvaluator();
      } catch (ARTException e) {
        ARTText.printFatal("sementics - " + e.getMessage());
      }
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    try {
      parser.artParse(ARTText.readFile("test.cava"));
    } catch (ARTException e) {
      ARTText.printFatal("syntax - " + e.getMessage());
    }

    parser.artDisambiguateRightmost();
    parser.artDerivationSelectFirst();
    (new Thread(new ParserAsRunnable())).start();
  }
}
