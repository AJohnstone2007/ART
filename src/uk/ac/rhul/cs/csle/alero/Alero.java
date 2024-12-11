package uk.ac.rhul.cs.csle.alero;

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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.alero.gui.GraphicsWindow;
import uk.ac.rhul.cs.csle.alero.gui.TextWindow;
import uk.ac.rhul.cs.csle.alero.hardCoded.JavaSandbox;
import uk.ac.rhul.cs.csle.alero.language.ADLInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;

public class Alero extends Application {
  /**********************************************************************************************************
   *
   * Initialisaton, window creation and access
   *
   **********************************************************************************************************/
  private static Stage textStage;
  private static Stage graphicsStage;
  public static TextWindow tw;
  private static GraphicsWindow gw;
  public static ITerms iTerms = new ITermsLowLevelAPI();
  private static ADLInterpreter adlInterpreter = new ADLInterpreter(iTerms);

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

    final int w10Vff = 7; // This is a fudge factor for Windows 10 where invisible framing still takes 7 pixels horizontally and 5 vertically

    Rectangle2D screen = Screen.getPrimary().getBounds();
    graphicsStage = new Stage();
    gw = new GraphicsWindow(graphicsStage, 800 - 3 * w10Vff, 0, screen.getWidth() - 800 + 4 * w10Vff, screen.getHeight(), "Alero graphics", 500.0);

    textStage = primaryStage;
    tw = new TextWindow(textStage, -w10Vff, 0, 800, screen.getHeight(), "Alero text", fileContents);

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

  public static void add(Node node) {
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
  public static final PhongMaterial redMaterial = new PhongMaterial(Color.RED);
  public static final PhongMaterial blueMaterial = new PhongMaterial(Color.BLUE);
  public static final PhongMaterial greenMaterial = new PhongMaterial(Color.GREEN);
  public static final PhongMaterial steelMaterial = new PhongMaterial(Color.SILVER);
  // public static final PhongMaterial plywoodMaterial = new PhongMaterial(Color.BURLYWOOD);
  public static final PhongMaterial plywoodMaterial = new PhongMaterial(Color.WHEAT);
  public static final PhongMaterial sleeperMaterial = new PhongMaterial(Color.SADDLEBROWN);

  /**********************************************************************************************************
   *
   * Defaults
   *
   **********************************************************************************************************/
  public static double scaleFactor = scaleFactor32; // Default scale factor
  public static double gauge = fi(4, 8.5); // Default to standard gauge
  public static String title = "Alero"; // Default window title

  private static void addMenuItem(Menu menu, String label) {
    addMenuItem(menu, label, null);
  }

  private static void addMenuItem(Menu menu, String label, String accelerator) {
    MenuItem ret = new MenuItem(label);
    menu.getItems().add(ret);
    ret.setOnAction(e -> Alero.menuAction(label));
    if (accelerator != null) ret.setAccelerator(KeyCombination.keyCombination(accelerator));
  }

  /**********************************************************************************************************
   *
   * Menu builder
   *
   **********************************************************************************************************/
  public static MenuBar buildMenuBar() {
    MenuBar menuBar = new MenuBar();
    Menu menu;

    menu = new Menu("_File");
    addMenuItem(menu, "_Run", "shortcut+R");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_New");
    addMenuItem(menu, "_Open");
    addMenuItem(menu, "_Close");
    addMenuItem(menu, "_Save", "shortcut+S");
    addMenuItem(menu, "Save _As");
    addMenuItem(menu, "Save A_ll");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Export");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "E_xit", "shortcut+Q");

    menuBar.getMenus().add(menu);

    menu = new Menu("_Edit");
    addMenuItem(menu, "_Find", "shortcut+F");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Copy", "shortcut+C");
    addMenuItem(menu, "Cu_t", "shortcut+X");
    addMenuItem(menu, "_Paste", "shortcut+V");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Co_mment", "shortcut+/");
    addMenuItem(menu, "_Reformat", "shortcut+T");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Preferences");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Insert");
    addMenuItem(menu, "_LOM");
    addMenuItem(menu, "B_all");
    addMenuItem(menu, "_Box");
    addMenuItem(menu, "_Cone");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Function");
    addMenuItem(menu, "_Text");
    addMenuItem(menu, "_Import");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_JavaSandbox");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Transform");
    addMenuItem(menu, "_Union");
    addMenuItem(menu, "_Difference");
    addMenuItem(menu, "_Intersection");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Translate");
    addMenuItem(menu, "_Rotate");
    addMenuItem(menu, "_Scale");
    addMenuItem(menu, "_Mirror");
    addMenuItem(menu, "_Affine");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Bloat");
    addMenuItem(menu, "_Hull");
    addMenuItem(menu, "_Fillet");
    addMenuItem(menu, "_Chamfer");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Material");
    menuBar.getMenus().add(menu);

    menu = new Menu("_View");
    addMenuItem(menu, "_Home", "shortcut+H");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Above");
    addMenuItem(menu, "_Under");
    addMenuItem(menu, "_Left");
    addMenuItem(menu, "_Right");
    addMenuItem(menu, "_Front", "shortcut+O");
    addMenuItem(menu, "_Back");
    addMenuItem(menu, "_Turntable");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Zoom In", "shortcut+=");
    addMenuItem(menu, "Zoom Out", "shortcut+-");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Pan Up", "shift+UP");
    addMenuItem(menu, "Pan Down", "shift+DOWN");
    addMenuItem(menu, "Pan Left", "shift+LEFT");
    addMenuItem(menu, "Pan Right", "shift+RIGHT");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Rotate Up", "shortcut+UP");
    addMenuItem(menu, "Rotate Down", "shortcut+DOWN");
    addMenuItem(menu, "Rotate Left", "shortcut+LEFT");
    addMenuItem(menu, "Rotate Right", "shortcut+RIGHT");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Parallel _view");
    addMenuItem(menu, "Perspective _view");
    addMenuItem(menu, "A_xes");
    addMenuItem(menu, "Fa_ces");
    addMenuItem(menu, "_Edges");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Analyse");
    addMenuItem(menu, "_Statistics");
    addMenuItem(menu, "_Validity");
    addMenuItem(menu, "_Repair");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Generate");
    addMenuItem(menu, "_Box");
    addMenuItem(menu, "_Roof");
    addMenuItem(menu, "_Wheel");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Help");
    addMenuItem(menu, "_Contents");
    addMenuItem(menu, "_About");
    menuBar.getMenus().add(menu);

    return menuBar;
  }

  /**********************************************************************************************************
   *
   * Menu despatcher
   *
   **********************************************************************************************************/
  public static void menuAction(String s) {
    switch (s) {
    case "_Run":
      consoleln("Run");
      try {
        gw.meshGroup.getChildren().clear();
        consoleln("Returned value " + adlInterpreter.adlInterpret(tw.codeArea.getText()).javaValue());
        consoleln("Top level symbols " + adlInterpreter.topLevelSymbols);
      } catch (RuntimeException e) {
        consoleln("Abend ");
        consoleln(e.getMessage());
      }
      break;
    case "E_xit":
      exit();
      break;
    case "_Import":
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open mesh file");
      // fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
      fileChooser.setInitialDirectory(new File("\\adrian\\eclipse\\art\\stlIn"));
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Mesh Files (*.stl)", "*.stl"), new ExtensionFilter("All Files", "*.*"));
      File file = fileChooser.showOpenDialog(textStage);
      if (file != null) {
        stl(file);
        tw.codeArea.appendText("import(\"" + file.getName() + "\")\n");
      }
      break;

    case "_JavaSandbox":
      // new PrintonOnSea().createWorld();
      try {
        new JavaSandbox();
      } catch (AleroException e) {
        // TODO Auto-generated catch block
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

  public static void stl(String filename) {
    stl(new File(filename));
  }

  public static void stl(File file) {
    try {
      if (file == null) return;
      AleroMesh stlMesh = new AleroMesh(file);
      stlMesh.computeBoundingBox();
      tw.printConsole("stl: loaded " + file.getName() + "\n");
      tw.printConsole("stl: bounding box (" + stlMesh.minX + ", " + stlMesh.minY + ", " + stlMesh.minZ + "), (" + stlMesh.maxX + ", " + stlMesh.maxY + ", "
          + stlMesh.maxZ + ")\n");
      double pointDensity = (stlMesh.getPoints().size()) / stlMesh.volume();
      tw.printConsole("stl: volume " + stlMesh.volume() + " contains " + stlMesh.getPoints().size() + " points at density " + pointDensity + "\n");
      if (pointDensity > 100) {
        tw.printConsole("stl: auto automatic rescaling from inches to mm");
        stlMesh.scale(25.4f, 25.4f, 25.4f);
      }
      stlMesh.centreBoundingBox();
      stlMesh.computeBoundingBox();
      tw.printConsole("stl: bounding box after rescale and centring (" + stlMesh.minX + ", " + stlMesh.minY + ", " + stlMesh.minZ + "), (" + stlMesh.maxX + ", "
          + stlMesh.maxY + ", " + stlMesh.maxZ + ")\n");
      addMesh(stlMesh);
    } catch (RuntimeException e) {
      consoleln(e.getMessage());
    }
  }

  public static void addMesh(AleroMesh stlMesh) {
    stlMesh.centreBoundingBox();
    System.out.println("Add mesh " + stlMesh.toStringFull());
    PhongMaterial material = new PhongMaterial(stlMesh.colour);

    MeshView meshView = new MeshView(stlMesh);

    meshView.setMaterial(material);

    // meshView.setCullFace(CullFace.NONE); // So we can see the backs of the triangles in case the winding order is incorrect - will render in black
    meshView.setCullFace(CullFace.NONE);
    meshView.setDrawMode(DrawMode.FILL);
    gw.meshGroup.getChildren().add(meshView);
  }

  public static void exit() {
    Platform.exit();
  }
}
