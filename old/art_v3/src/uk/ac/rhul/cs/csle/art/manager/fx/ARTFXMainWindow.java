package uk.ac.rhul.cs.csle.art.manager.fx;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uk.ac.rhul.cs.csle.art.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTree;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;

// This is a first attempt at a text editor control window for ART - it may develop into the main window of the visual version
public class ARTFXMainWindow {

  ARTManager manager;

  public ARTFXMainWindow(ARTManager artManager, ARTGrammar currentGrammar, String grammarFilename, ARTTree artRDT, String inputFilename, double originX,
      double originY, double sizeX, double sizeY) throws ARTException {

    this.manager = artManager;
    ARTFXMainWindow us = this; // This is a little ugly; in the close event handler, this means the event, not the window...
    Rectangle2D primaryScreenBounds;
    Scene scene;
    Stage stage;

    // 3 Construct UI elements
    ARTFXTreeNavigator instanceTree = new ARTFXTreeNavigator(currentGrammar.getInstanceTree());
    ScrollPane instanceTreeScrollPane = new ScrollPane(instanceTree.getCanvas());
    instanceTreeScrollPane.setPannable(true);

    VBox buttonBox = new VBox();
    buttonBox.setMinWidth(100);
    Button parseButton = new Button("Parse");
    parseButton.setOnAction(event -> {
      System.out.println("Parse");
      artManager.getModules().clear();
      artManager.getGrammars().clear();
      artManager.clearDefaultMainModule();
      Platform.runLater(() -> instanceTreeScrollPane.setContent(new Canvas()));

    });
    parseButton.setPadding(new Insets(10));
    parseButton.setPrefWidth(100);

    Button saveAllButton = new Button("Save all");
    saveAllButton.setOnAction(event -> {
      System.out.println("Save all");
    });
    saveAllButton.setPadding(new Insets(10));
    saveAllButton.setPrefWidth(100);

    Button exitButton = new Button("Exit");
    exitButton.setOnAction(event -> {
      Platform.exit();
      System.exit(0);
    });
    exitButton.setPadding(new Insets(10));
    exitButton.setPrefWidth(100);

    buttonBox.getChildren().addAll(parseButton, saveAllButton, exitButton);

    TextArea grammarArea = new TextArea(ARTText.readFile(grammarFilename));
    grammarArea.setPrefColumnCount(80);
    grammarArea.setPrefRowCount(5);
    TextArea inputArea = new TextArea(ARTText.readFile(inputFilename));
    inputArea.setPrefColumnCount(80);
    inputArea.setPrefRowCount(5);

    ScrollPane grammarScrollPane = new ScrollPane(grammarArea);
    grammarScrollPane.setFitToHeight(true);
    // grammarScrollPane.setFitToWidth(true);
    ScrollPane inputScrollPane = new ScrollPane(inputArea);
    inputScrollPane.setFitToHeight(true);
    // inputScrollPane.setFitToWidth(true);

    HBox controlBox = new HBox();
    controlBox.getChildren().addAll(buttonBox, grammarScrollPane, inputScrollPane);

    VBox topBox = new VBox();

    topBox.getChildren().addAll(controlBox, instanceTreeScrollPane);

    primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    stage = new Stage();
    stage.setTitle(
        "ART: grammar " + grammarFilename == null ? "(new file)" : grammarFilename + "; input " + inputFilename == null ? "(new file)" : inputFilename);
    stage.setX(primaryScreenBounds.getWidth() * originX);
    stage.setY(primaryScreenBounds.getHeight() * originY);
    stage.setWidth(primaryScreenBounds.getWidth() * sizeX);
    stage.setHeight(primaryScreenBounds.getHeight() * sizeY);
    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent t) {
        Platform.exit();
        System.exit(0);
      }
    });

    // 5 Enable
    scene = new Scene(topBox);
    stage.setScene(scene);
    stage.show();

    // Build tree viewer
    new ARTFXTreeNavigator3D(currentGrammar.getInstanceTree(), false);

  }

}
