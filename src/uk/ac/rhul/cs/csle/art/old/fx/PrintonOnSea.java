package uk.ac.rhul.cs.csle.art.old.fx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;

public class PrintonOnSea {
  double runningLineGap = fi(6, 0);
  double yardLineGap = fi(10, 0);

  int facets = 180;
  double outlineWidth = 10;

  double boardX = 1200;
  double boardY = 600;
  double boardZ = 36;

  double sleeperX = i(10);
  double sleeperY = fi(9, 0);
  double sleeperZ = i(5);
  // These are the sleeper spacing for a 30 foot panel on the LBSCR
  double[] sleepersLBSCR30 = { fi(1.625, 0) + 0 * fi(2, 6.25), fi(1.625, 0) + 1 * fi(2, 6.25), fi(1.625, 0) + 2 * fi(2, 6.25), fi(1.625, 0) + 3 * fi(2, 6.25),
      fi(1.625, 0) + 4 * fi(2, 6.25), fi(1.625, 0) + 5 * fi(2, 6.25), fi(1.625, 0) + 6 * fi(2, 6.25), fi(1.625, 0) + 7 * fi(2, 6.25),
      fi(1.625, 0) + 8 * fi(2, 6.25), fi(1.625, 0) + 9 * fi(2, 6.25), fi(1.625, 0) + 10 * fi(2, 6.25), fi(1.625, 0) + 11 * fi(2, 6.25) };

  double mainLineY = -(boardY / 2 - 100);
  double rulingRadius = fi(10, 0); // 10 feet like Peco points
  double rulingSeparation = fi(11, 0); // 11 real feet cente separation
  double rulingTurnoutAngle = curveAngleY(rulingRadius, rulingSeparation / 2);
  double rulingTurnoutX = curveX(rulingRadius, rulingTurnoutAngle);
  double rulingTurnoutY = curveY(rulingRadius, rulingTurnoutAngle);

  // Track dimensions
  double railZ = i(6);
  double railY = i(2.5);
  double railBaseZ = i(1);

  // Rolling stock dimensions
  double coachExtension = 150;

  double wagonX = 200;
  double wagonY = 78;
  double coachX = 650;
  double coachY = 90;
  double locoX = 400;

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

  double curveX(double r, double theta) { // The x-displacement for a curve of radius r which subtends angle theta
    return r * Math.sin(Math.abs(theta));
  };

  double curveY(double r, double theta) { // The y-displacement for a curve of radius r which subtends angle theta
    return r - (r * Math.cos(Math.abs(theta)));
  };

  double curveAngleX(double radius, double Y) {
    return Math.acos(radius);
  }

  double curveAngleY(double radius, double Y) {
    return Math.acos((radius - Y) / radius);
  }

  Node board(double x, double y) {
    Box ret = new Box(x, y, boardZ);
    ret.setMaterial(plywoodMaterial);
    ret.setTranslateZ(-boardZ);
    // add(ret);
    return ret;
  }

  Node sleeper(double originX, double originY) {
    Box ret = new Box(sleeperX, sleeperY, sleeperZ);
    ret.setTranslateX(originX - sleeperX / 2);
    ret.setTranslateY(originY);
    ret.setTranslateZ(sleeperZ / 2);
    ret.setMaterial(sleeperMaterial);
    return ret;
  }

  Node straight(double length, double originX, double originY, double orientationAngle, double... sleeperPosition) {
    Group ret = new Group();
    Box centreLine = new Box(length, 2, 4);
    centreLine.setMaterial(greenMaterial);
    Box leftBar = new Box(8, 10, 2);
    leftBar.setMaterial(redMaterial);
    leftBar.setTranslateX(-length / 2 + 4);
    Box rightBar = new Box(8, 10, 2);
    rightBar.setMaterial(blueMaterial);
    rightBar.setTranslateX(length / 2 - 4);

    Box rail1 = new Box(length, railY, railZ);
    rail1.setTranslateY(gauge / 2 + railY / 2);
    rail1.setTranslateZ(sleeperZ + railZ + railBaseZ);
    rail1.setMaterial(steelMaterial);

    Box rail2 = new Box(length, railY, railZ);
    rail2.setTranslateY(-gauge / 2 - railY / 2);
    rail2.setTranslateZ(sleeperZ + railZ + railBaseZ);
    rail2.setMaterial(steelMaterial);

    ret.getChildren().addAll(centreLine, leftBar, rightBar, rail1, rail2);

    for (double x : sleeperPosition)
      ret.getChildren().add(sleeper(-length / 2 + x, 0));

    ret.setRotate(orientationAngle);

    ret.setTranslateX(length / 2 + originX);
    ret.setTranslateY(originY);

    // add(ret);

    return ret;
  }

  Node straightLBSCR(double originX, double originY, double orientationAngle) {
    return straight(fi(30, 0), originX, originY, orientationAngle, sleepersLBSCR30);
  }

  public void createWorld() {
    board(boardX, boardY).setTranslateX(boardX * -1.5);
    board(boardX, boardY).setTranslateX(boardX * -0.5);
    board(boardX, boardY).setTranslateX(boardX * 0.5);
    board(boardX, boardY).setTranslateX(boardX * 1.5);
    for (int i = -6; i < 6; i++)
      straightLBSCR(i * fi(30, 0), 0, 0);

    for (int i = -2; i < 2; i++)
      straightLBSCR(i * fi(30, 0), -gauge - runningLineGap, 0);

  }

  public static void add(AleroMesh stlMesh) {
    stlMesh.centreBoundingBox();
    System.out.println("Add mesh " + stlMesh.toStringFull());
    PhongMaterial material = new PhongMaterial(stlMesh.colour);

    MeshView meshView = new MeshView(stlMesh);

    meshView.setMaterial(material);

    // meshView.setCullFace(CullFace.NONE); // So we can see the backs of the triangles in case the winding order is incorrect - will render in black
    meshView.setCullFace(CullFace.NONE);
    meshView.setDrawMode(DrawMode.FILL);
    // gw.meshGroup.getChildren().add(meshView);
  }
}
