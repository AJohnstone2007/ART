package uk.ac.rhul.cs.csle.art.fx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.fx.sandbox.AleroJavaSandbox;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;

public class Alero extends Application {

  /**********************************************************************************************************
   *
   * Initialisaton, window creation and access
   *
   **********************************************************************************************************/
  private static Stage textStage;
  private static Stage graphicsStage;
  private static EditorWithConsoleWindow tw;
  private static GraphicsWindow gw;
  public static ITerms iTerms = new ITerms();
  public boolean recording = true;
  public static AleroMesh currentMesh = null;
  private static File workingDirectory = new File(System.getProperty("user.home"));

  @Override
  public void start(Stage primaryStage) {
    /*
     * Argument processing
     */
    var unamedArgs = getParameters().getUnnamed();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm a");

    String fileContents = "/* Alero new file: " + LocalDateTime.now().format(format) + " */";
    String filename = unamedArgs.size() == 0 ? null : unamedArgs.get(0);
    if (filename != null) fileContents = readFile(filename);

    final int windowsKulge = 7; // This is a fudge factor for Windows 10 and 11 where invisible framing still takes 7 pixels horizontally and 5 vertically

    Rectangle2D screen = Screen.getPrimary().getBounds();
    graphicsStage = new Stage();
    gw = new GraphicsWindow(graphicsStage, 800 - 3 * windowsKulge, 0, screen.getWidth() - 800 + 4 * windowsKulge, screen.getHeight(), "Alero graphics",
        new MenuBuilderAlero(), 500.0);
    textStage = primaryStage;
    tw = new EditorWithConsoleWindow(textStage, -windowsKulge, 0, 800, screen.getHeight(), "Alero text", new MenuBuilderAlero(), fileContents);
    // Comment next line out for production!
    // new JavaSandbox();
  }

  public String readFile(String filename) throws AleroException {
    String inputString = "";
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(filename));
      inputString = Charset.forName("ISO-8859-1").newDecoder().decode(ByteBuffer.wrap(encoded)).toString();
    } catch (FileNotFoundException ex) {
      throw new AleroException("Unable to open input file " + filename + "\n");
    } catch (CharacterCodingException e) {
      throw new AleroException("Encoding error in input file " + filename + "\n");
    } catch (IOException e) {
      throw new AleroException("I/O error whilst attempting to read input file " + filename + "\n");
    }
    return inputString;
  }

  public static void console(String str) {
    tw.printConsole(str);
  }

  public static void consoleln(String str) {
    tw.printConsole(str);
    tw.printConsole("\n");
  }

  public static void code(String str) {
    tw.insertCode(str);
  }

  public static void codeln(String str) {
    tw.insertCode(str);
    tw.insertCode("\n");
  }

  public static void addPart(Node node) {
    gw.world.getChildren().add(node);
  }

  /**********************************************************************************************************
   *
   * Scaling factors for common modelling regimes
   *
   **********************************************************************************************************/
  public static double scaleFactor1 = 1; // Full size
  public static double scaleFactor10mm = 30.6; // Greenly Gauge 1 - 10mm to the foot
  public static double scaleFactor32 = 32; // 1:32 Gauge 1
  public static double scaleFactor7mm = 43.5; // UK O or zero gauge 7mm to the foot
  public static double scaleFactor4mm = 76.2; // UK OO and HO 4mm to the foot
  public static double scaleFactor2mm = 152; // 2mm to the foot
  public static double scaleFactorBritishNGauge = 148; // UK N gauge

  /**********************************************************************************************************
   *
   * Utility functions for managing imperial to metric conversion with scaling
   *
   **********************************************************************************************************/
  public static final double mmPerInch = 25.4;

  public static boolean snapping = false;
  public static double snapGrid = 10.0;

  public static double snap(double x) {
    return snapping ? Math.round(x * snapGrid) / snapGrid : x;
  }

  public static double fi(double feet, double inches) {
    return snap((feet * 12 + inches) * mmPerInch / scaleFactor);
  }

  public static double i(double inches) {
    return snap(inches * mmPerInch / scaleFactor);
  }

  public static double mm(double millimetres) {
    return snap(millimetres / scaleFactor);
  }

  /**********************************************************************************************************
   *
   * Utility function for managing scaling directly from drawings or photographs
   *
   **********************************************************************************************************/
  public static double dmm(double millimetres) {
    return mm(millimetres * scaleFactor);
  }

  /**********************************************************************************************************
   *
   * Materials
   *
   **********************************************************************************************************/
  public static final PhongMaterial materialRed = new PhongMaterial(Color.RED);
  public static final PhongMaterial blueMaterial = new PhongMaterial(Color.BLUE);
  public static final PhongMaterial greenMaterial = new PhongMaterial(Color.GREEN);
  public static final PhongMaterial steelMaterial = new PhongMaterial(Color.SILVER);
  public static final PhongMaterial plywoodMaterial = new PhongMaterial(Color.WHEAT);
  public static final PhongMaterial sleeperMaterial = new PhongMaterial(Color.SADDLEBROWN);

  /**********************************************************************************************************
   *
   * Defaults
   *
   **********************************************************************************************************/
  public static double scaleFactor = scaleFactor32; // Default scale factor
  public static double gauge = fi(4, 8.5); // Default to standard gauge

  /**********************************************************************************************************
   *
   * Menu despatcher
   *
   * @throws Exception
   *
   **********************************************************************************************************/
  public static void menuAction(String s) throws Exception {
    FileChooser fileChooser;
    File file;
    switch (s) {
    case "_Run":
      consoleln("Run");
      try {
        gw.meshGroup.getChildren().clear();
        // consoleln("Returned value " + adlInterpreter.adlInterpret(tw.codeArea.getText()).javaValue());
        // consoleln("Top level symbols " + adlInterpreter.topLevelSymbols);
      } catch (RuntimeException e) {
        consoleln("Abend");
        consoleln(e.getMessage());
      }
      break;
    case "E_xit":
      exit();
      break;
    case "_Import":
      fileChooser = new FileChooser();
      fileChooser.setTitle("Import mesh file");
      fileChooser.setInitialDirectory(workingDirectory);
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Mesh Files (*.stl)", "*.stl"), new ExtensionFilter("All Files", "*.*"));
      file = fileChooser.showOpenDialog(textStage);
      if (file != null) {
        workingDirectory = file.getParentFile();
        consoleln("import(\"" + file.getPath() + "\")\n");
        codeln("import(\"" + file.getPath() + "\")\n");
        try {
          currentMesh = new AleroMesh(file);
          addMesh(currentMesh);
        } catch (Exception e) {
          console("I/O error during STL import");
        }
      }
      break;
    case "_Export":
      fileChooser = new FileChooser();
      fileChooser.setTitle("Export mesh file");
      fileChooser.setInitialDirectory(workingDirectory);
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Mesh Files (*.stl)", "*.stl"), new ExtensionFilter("All Files", "*.*"));
      file = fileChooser.showOpenDialog(textStage);
      if (file != null) {
        workingDirectory = file.getParentFile();
        try {
          currentMesh.toBinaryFile(file.getPath());
        } catch (IOException e) {
          console("Export: file write error");
        }
      }
      break;
    case "_Normalise":
      fileChooser = new FileChooser();
      fileChooser.setTitle("Normalise mesh file");
      fileChooser.setInitialDirectory(workingDirectory);
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Mesh Files (*.stl)", "*.stl"), new ExtensionFilter("All Files", "*.*"));
      file = fileChooser.showOpenDialog(textStage);
      if (file != null) {
        workingDirectory = file.getParentFile();
        consoleln("normalise(\"" + file.getPath() + "\")\n");
        codeln("normalise(\"" + file.getPath() + "\")\n");
        try {
          currentMesh = new AleroMesh(file);
          addMesh(currentMesh);
        } catch (Exception e) {
          console("Normalise: file read or format error");
        }
        currentMesh.computeBoundingBox();
        tw.printConsole("stl: bounding box (" + currentMesh.minX + ", " + currentMesh.minY + ", " + currentMesh.minZ + "), (" + currentMesh.maxX + ", "
            + currentMesh.maxY + ", " + currentMesh.maxZ + ")\n");
        double pointDensity = (currentMesh.getPoints().size()) / currentMesh.volume();
        tw.printConsole("stl: volume " + currentMesh.volume() + " contains " + currentMesh.getPoints().size() + " points at density " + pointDensity + "\n");
        if (pointDensity > 100) {
          tw.printConsole("stl: auto automatic rescaling from inches to mm");
          currentMesh.scale(25.4f, 25.4f, 25.4f);
        }
        currentMesh.centreBoundingBox();
        currentMesh.computeBoundingBox();
        tw.printConsole("stl: bounding box after rescale and centring (" + currentMesh.minX + ", " + currentMesh.minY + ", " + currentMesh.minZ + "), ("
            + currentMesh.maxX + ", " + currentMesh.maxY + ", " + currentMesh.maxZ + ")\n");

        try {
          currentMesh.toBinaryFile(file.getPath() + ".nor");
        } catch (IOException e) {
          console("Normalise: file write error");
        }
      }
      break;
    case "_JavaSandbox":
      try {
        new AleroJavaSandbox();
      } catch (AleroException e) {
        e.printStackTrace();
      }
      break;

    case "_Home":
      gw.cameraHome();
      break;
    case "_Right":
      gw.cameraXNegative();
      break;
    case "_Left":
      gw.cameraXPositive();
      break;
    case "_Back":
      gw.cameraYNegative();
      break;
    case "_Front":
      gw.cameraYPositive();
      break;
    case "_Above":
      gw.cameraZNegative();
      break;
    case "_Under":
      gw.cameraZPositive();
      break;
    case "Zoom In":
      gw.cameraZoomIn();
      break;
    case "Zoom Out":
      gw.cameraZoomOut();
      break;
    case "Rotate Up":
      gw.cameraRotateXNeg();
      break;
    case "Rotate Down":
      gw.cameraRotateXPos();
      break;
    case "Rotate Left":
      gw.cameraRotateYPos();
      break;
    case "Rotate Right":
      gw.cameraRotateYNeg();
      break;
    case "Pan Up":
      gw.cameraPanYPos();
      break;
    case "Pan Down":
      gw.cameraPanYNeg();
      break;
    case "Pan Left":
      gw.cameraPanXNeg();
      break;
    case "Pan Right":
      gw.cameraPanXPos();
      break;
    case "Parallel camera":
      gw.cameraParallel();
      break;
    case "Perspective camera":
      gw.cameraPerspective();
      break;

    default:
      tw.printConsole("Action " + s + " not yet implemented\n");
    }

  }

  public static void addMesh(TriangleMesh mesh) {
    addShape3D(new MeshView(mesh));
  }

  public static void addShape3D(Shape3D meshView) {
    PhongMaterial material = new PhongMaterial(Color.BLANCHEDALMOND);
    meshView.setMaterial(material);

    // meshView.setCullFace(CullFace.NONE); // So we can see the backs of the triangles in case the winding order is incorrect - will render in black
    meshView.setCullFace(CullFace.NONE);
    meshView.setDrawMode(DrawMode.FILL);
    addNode(meshView);
  }

  public static void addNode(Node node) {
    gw.meshGroup.getChildren().add(node);
  }

  public static void exit() {
    Platform.exit();
  }
}
