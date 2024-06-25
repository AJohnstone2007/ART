package uk.ac.rhul.cs.csle.art.manager.fx;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTree;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTreeVertex;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTXform;

public class ARTFXTreeNavigator3D {
  private final Group root = new Group();
  private final ARTXform world = new ARTXform();
  Stage stage = new Stage();
  private final PerspectiveCamera camera = new PerspectiveCamera(true);
  private final ARTXform cameraFormRotator = new ARTXform();
  private final ARTXform cameraFormPanner = new ARTXform();
  private final ARTXform cameraFormInvertY = new ARTXform();
  private final double cameraDistance = 1000;

  private double mousePosX;
  private double mousePosY;
  private double mouseOldX;
  private double mouseOldY;
  private double mouseDeltaX;
  private double mouseDeltaY;

  private final PhongMaterial redMaterial = new PhongMaterial();
  private final PhongMaterial greenMaterial = new PhongMaterial();
  private final PhongMaterial blueMaterial = new PhongMaterial();
  private final PhongMaterial yellowMaterial = new PhongMaterial();

  // Application specific members
  private final ARTTree tree;
  private final boolean isLeftToRight;
  private final double xSeparation = 16; // horizontal separation between nodes
  private final double xSeparationDiv2 = xSeparation / 2; // vertical spacing (not separation) of baselines
  private final double xSeparationDiv4 = xSeparation / 4; // vertical spacing (not separation) of baselines
  private final double ySeparation = 16; // vertical separation between nodes
  private final double ySeparationDiv2 = ySeparation / 2; // vertical spacing (not separation) of baselines
  private final double ySeparationDiv4 = ySeparation / 4; // vertical spacing (not separation) of baselines
  private final double frameThickness = 1;
  private final double frameBorder = 2;
  private final double frameBorderMul2 = 2 * frameBorder;
  private final double framePortXOffset = 10;
  private final double framePortYOffset = 10;
  private final double frameTextBaseLineOffset = frameBorder + 12;
  private final PhongMaterial frameMaterial;

  // Extents data structures for sizing: these map logical x and y coordinates into physical anchor points in 2D space
  private int xMax = 0, yMax = 0; // These will be set to the x,y coordinates of the bounding logical integer rectangle
  private final HashMap<Integer, Double> xExtents = new HashMap<Integer, Double>();
  private final HashMap<Integer, Double> yExtents = new HashMap<Integer, Double>();

  public ARTFXTreeNavigator3D(ARTTree tree, boolean isLeftToRight) {
    // Application specific constructor actions
    this.isLeftToRight = isLeftToRight;
    this.tree = tree;
    frameMaterial = isLeftToRight ? redMaterial : yellowMaterial;
    final Group sGroup = new Group();
    renderTree(sGroup);
    world.getChildren().add(sGroup);
    // End of application specific constructor actions

    // Make some materials
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    yellowMaterial.setDiffuseColor(Color.GOLD);
    yellowMaterial.setSpecularColor(Color.YELLOW);

    root.getChildren().add(world);

    buildCamera();
    Scene scene = new Scene(root, 900, 600, true, SceneAntialiasing.BALANCED);
    scene.setFill(Color.GREY);
    handleMouse(scene, world);
    scene.setCamera(camera);
    stage.setScene(scene);
    stage.show();
  }

  private void buildCamera() {
    root.getChildren().add(cameraFormRotator);
    cameraFormRotator.getChildren().add(cameraFormPanner);
    cameraFormPanner.getChildren().add(cameraFormInvertY);
    cameraFormInvertY.getChildren().add(camera);
    cameraFormInvertY.setRotateZ(/* 180.0 */0); // Y axis inversion is not used in this application so that text is 'naturally' the right way up

    camera.setNearClip(0.1);
    camera.setFarClip(10000.0);
    camera.setTranslateZ(-cameraDistance);
    cameraFormRotator.ry.setAngle(00.0);
    cameraFormRotator.rx.setAngle(0);
  }

  private void handleMouse(Scene scene, final Node root) {
    scene.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseOldX = me.getSceneX();
        mouseOldY = me.getSceneY();
      }
    });
    scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        if (me.isPrimaryButtonDown()) {
          cameraFormRotator.ry.setAngle(cameraFormRotator.ry.getAngle() + mouseDeltaX);
          cameraFormRotator.rx.setAngle(cameraFormRotator.rx.getAngle() - mouseDeltaY);
        } else if (me.isMiddleButtonDown()) {
          double z = camera.getTranslateZ();
          double newZ = z + mouseDeltaX;
          camera.setTranslateZ(newZ);
        } else if (me.isSecondaryButtonDown()) {
          cameraFormPanner.t.setX(cameraFormPanner.t.getX() - mouseDeltaX);
          cameraFormPanner.t.setY(cameraFormPanner.t.getY() - mouseDeltaY);
        }
      }
    });
  }

  private void visitTreeComputeExtents(ARTTreeVertex vertex, int x, int y) {
    // System.err.println("Compute extents at coordinates (" + x + ", " + y + ") visiting node " + vertex);
    if (x > xMax) xMax = x;
    if (y > yMax) yMax = y;
    Bounds bounds = new Text(vertex.toString()).getBoundsInLocal();

    double width = bounds.getWidth() + frameBorderMul2;
    double height = bounds.getHeight();
    if (xExtents.get(x) == null || width > xExtents.get(x)) xExtents.put(x, width);
    if (yExtents.get(y) == null || height > yExtents.get(y)) yExtents.put(y, height);

    if (vertex.getChild() != null) visitTreeComputeExtents(vertex.getChild(), isLeftToRight ? x + 1 : x, isLeftToRight ? y : y + 1);
    if (vertex.getSibling() != null) visitTreeComputeExtents(vertex.getSibling(), isLeftToRight ? x : xMax + 1, isLeftToRight ? yMax + 1 : y);
  }

  // Construct the graphics for a single node comprising a canvas with the label, a frame and in and our ports
  private void renderNode(Group sGroup, ARTTreeVertex vertex, int x, int y, boolean isNotLeaf, boolean isFirst, boolean isNotRoot) {
    // System.err.println("Render node at (" + x + ", " + y + ") visiting node " + vertex);
    // compute some coordinates
    double xx = xExtents.get(x);
    double yy = yExtents.get(y);
    double frameHeight = yExtents.get(y + 1) - yy - ySeparation;
    double frameWidth = xExtents.get(x + 1) - xx - xSeparation;
    double frameHeightHalf = frameHeight / 2;
    double frameWidthHalf = frameWidth / 2;

    // Make a canvas containing the label
    Canvas canvas = new Canvas(frameWidth, frameHeight);
    canvas.getGraphicsContext2D().fillText(vertex.toString(), frameBorder, frameTextBaseLineOffset);

    // Make the enclosing frame
    final Box top = new Box(frameWidth, frameThickness, frameThickness);
    top.setMaterial(frameMaterial);
    top.setTranslateX(frameWidth / 2);
    final Box left = new Box(frameThickness, frameHeight, frameThickness);
    left.setMaterial(frameMaterial);
    left.setTranslateY(frameHeightHalf);
    final Box right = new Box(frameThickness, frameHeight, frameThickness);
    right.setMaterial(frameMaterial);
    right.setTranslateX(frameWidth);
    right.setTranslateY(frameHeightHalf);
    final Box bottom = new Box(frameWidth, frameThickness, frameThickness);
    bottom.setMaterial(frameMaterial);
    bottom.setTranslateX(frameWidthHalf);
    bottom.setTranslateY(frameHeight);

    final Box inBar, outBar;
    if (isNotRoot) {
      if (isLeftToRight) {
        inBar = new Box(xSeparationDiv2, frameThickness, frameThickness);
        inBar.setTranslateY(framePortYOffset);
        inBar.setTranslateX(-xSeparationDiv4);
      } else {
        inBar = new Box(frameThickness, ySeparationDiv2, frameThickness);
        inBar.setTranslateX(framePortXOffset);
        inBar.setTranslateY(-ySeparationDiv4);
      }
      inBar.setMaterial(frameMaterial);
    } else
      inBar = null;

    if (isNotLeaf) {
      if (isLeftToRight) {
        outBar = new Box(xSeparationDiv2, frameThickness, frameThickness);
        outBar.setTranslateY(framePortYOffset);
        outBar.setTranslateX(frameWidth + xSeparationDiv4);
      } else {
        outBar = new Box(frameThickness, ySeparationDiv2, frameThickness);
        outBar.setTranslateX(framePortXOffset);
        outBar.setTranslateY(frameHeight + ySeparationDiv4);
      }
      outBar.setMaterial(frameMaterial);
    } else
      outBar = null;

    // Make the enclosing group and place it
    Group nodeGroup = new Group();
    nodeGroup.setTranslateX(xx);
    nodeGroup.setTranslateY(yy);
    nodeGroup.getChildren().addAll(canvas, top, left, right, bottom);
    if (isNotLeaf) nodeGroup.getChildren().add(outBar);
    if (isNotRoot) nodeGroup.getChildren().add(inBar);

    sGroup.getChildren().add(nodeGroup);
  }

  private void visitTreeRender(Group sGroup, ARTTreeVertex vertex, int parentCoordinate, int x, int y, boolean isFirst) {
    if (x > xMax) xMax = x;
    if (y > yMax) yMax = y;
    // Scan chidren's extents
    int childCount = 0;
    for (ARTTreeVertex tmp = vertex.getChild(); tmp != null; tmp = tmp.getSibling())
      childCount++;

    boolean isNotLeaf = childCount != 0;
    if (childCount == 0) // zero and one child are indistinguishable
      childCount = 1;

    renderNode(sGroup, vertex, x, y, isNotLeaf, isFirst, parentCoordinate >= 0);

    if (vertex.getChild() != null)
      visitTreeRender(sGroup, vertex.getChild(), isLeftToRight ? y : x, isLeftToRight ? x + 1 : x, isLeftToRight ? y : y + 1, true);
    if (vertex.getSibling() != null)
      visitTreeRender(sGroup, vertex.getSibling(), parentCoordinate, isLeftToRight ? x : xMax + 1, isLeftToRight ? yMax + 1 : y, false);
    else {// at last child, so connect all across to parent
      final Box acrossBar;
      if (parentCoordinate != x && x != 0) {
        if (isLeftToRight) {
          double length = yExtents.get(y) - yExtents.get(parentCoordinate);
          acrossBar = new Box(frameThickness, length, frameThickness);
          acrossBar.setTranslateY(yExtents.get(parentCoordinate) + framePortYOffset + length / 2);
          acrossBar.setTranslateX(xExtents.get(x) - xSeparationDiv2);
        } else {
          double length = xExtents.get(x) - xExtents.get(parentCoordinate);
          acrossBar = new Box(length, frameThickness, frameThickness);
          acrossBar.setTranslateX(xExtents.get(parentCoordinate) + framePortXOffset + length / 2);
          acrossBar.setTranslateY(yExtents.get(y) - ySeparationDiv2);
        }
        acrossBar.setMaterial(frameMaterial);
        sGroup.getChildren().add(acrossBar);
      }
    }
  }

  private void renderTree(Group sGroup) {
    // 1 Compute extents
    xMax = yMax = 0;
    visitTreeComputeExtents(tree.getRoot(), 0, 0);

    // 2 Convert individual extents to cumulative coordinates
    double offset = 0;
    for (int i = 0; i <= xMax; i++) {
      Double tmp = xExtents.get(i);
      xExtents.put(i, offset);
      offset += tmp + xSeparation;
    }
    xExtents.put(xMax + 1, offset);

    offset = 0;
    for (int i = 0; i <= yMax; i++) {
      Double tmp = yExtents.get(i);
      yExtents.put(i, offset);
      offset += tmp + ySeparation;
    }
    yExtents.put(yMax + 1, offset);

    // System.err.println("xExtents = " + xExtents);
    // System.err.println("yExtents = " + yExtents);
    // 4 Build the 3D tree
    xMax = yMax = 0;
    visitTreeRender(sGroup, tree.getRoot(), -1, 0, 0, false);

    // 5 Offset the tree so that it is centred
    sGroup.setTranslateX(-xExtents.get(xMax) / 2);
    sGroup.setTranslateY(-yExtents.get(yMax) / 2);
  }
}
