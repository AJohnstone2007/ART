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
import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLParserBase;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTXform;

public class ARTFXSPPFNavigator3D {
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
  private final ARTGLLParserBase parser;
  private final boolean isLeftToRight;
  private final double xSeparation = 16; // horizontal separation between nodes
  private final double xSeparationDiv2 = xSeparation / 2; // vertical spacing (not separation) of baselines
  private final double xSeparationDiv4 = xSeparation / 4; // vertical spacing (not separation) of baselines
  private final double ySeparation = 16; // vertical separation between nodes
  private final double ySeparationDiv2 = ySeparation / 2; // vertical spacing (not separation) of baselines
  private final double ySeparationDiv4 = ySeparation / 4; // vertical spacing (not separation) of baselines
  private final double zSeparation = 300;
  private final double zSeparationDiv2 = zSeparation / 2;
  private final double frameThickness = 1;
  private final double frameBorder = 2;
  private final double frameBorderMul2 = 2 * frameBorder;
  private final double framePortXOffset = 6;
  private final double framePortYOffset = 6;
  private final double frameTextBaseLineOffset = frameBorder + 12;
  private final PhongMaterial frameMaterial;

  // Extents data structures for sizing: these map logical x and y coordinates into physical anchor points in 2D space
  private int xMax, yMax;
  private final HashMap<Integer, Double> xExtents = new HashMap<Integer, Double>();
  private final HashMap<Integer, Double> yExtents = new HashMap<Integer, Double>();

  public ARTFXSPPFNavigator3D(ARTGLLParserBase parser2, boolean isLeftToRight) {
    // Application specific constructor actions
    this.isLeftToRight = isLeftToRight;
    this.parser = parser2;
    frameMaterial = isLeftToRight ? redMaterial : yellowMaterial;
    final Group sGroup = new Group();
    renderSPPF(sGroup);
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

        if (me.isShiftDown() && (me.isSecondaryButtonDown() || me.isPrimaryButtonDown())) {
          double z = camera.getTranslateZ();
          double newZ = z + mouseDeltaX;
          camera.setTranslateZ(newZ);
        } else if (me.isPrimaryButtonDown()) {
          cameraFormRotator.ry.setAngle(cameraFormRotator.ry.getAngle() + mouseDeltaX);
          cameraFormRotator.rx.setAngle(cameraFormRotator.rx.getAngle() - mouseDeltaY);
        } else if (me.isSecondaryButtonDown()) {
          cameraFormPanner.t.setX(cameraFormPanner.t.getX() - mouseDeltaX);
          cameraFormPanner.t.setY(cameraFormPanner.t.getY() - mouseDeltaY);
        }
      }
    });
  }

  // Recursively visit a tree of SPPF nodes, updating the column widths using maps xExtents and yExtents
  private int visitTreeComputeExtents(int sppfNode, int x, int y) {
    int ret = x;
    // System.err.println("Compute extents at coordinates (" + x + ", " + y + ") visiting SPPF node " + sppfNode + " labelled "
    // + parser.artGetLabelString(parser.artSPPFNodeLabel(sppfNode)));
    Bounds bounds = new Text(parser.artLabelStrings[parser.artSPPFNodeLabel(sppfNode)]).getBoundsInLocal();
    double width = bounds.getWidth() + frameBorderMul2;
    double height = bounds.getHeight();

    if (xExtents.get(x) == null || width > xExtents.get(x)) xExtents.put(x, width);
    if (yExtents.get(y) == null || height > yExtents.get(y)) yExtents.put(y, height);

    // Visit each pack node's children recursively
    for (int p = parser.artSPPFNodePackedNodeList(sppfNode); p != 0; p = parser.artSPPFPackedNodePackedNodeList(p)) {
      if (parser.artSPPFPackedNodeLeftChild(p) != 0) {
        int leftMost = visitTreeComputeExtents(parser.artSPPFPackedNodeLeftChild(p), x, y + 1);
        ret = visitTreeComputeExtents(parser.artSPPFPackedNodeRightChild(p), leftMost + 1, y + 1);
      } else
        ret = visitTreeComputeExtents(parser.artSPPFPackedNodeRightChild(p), x, y + 1);
    }
    return ret;
  }

  // Construct the graphics for a single node comprising a canvas with the label, a frame and in and our ports
  private void renderNode(Group sGroup, int sppfNode, int x, int y, int z, int parentZ, boolean isNotLeaf, boolean isFirst, boolean isNotRoot,
      boolean isAmbiguityRoot) {
    // System.err.println("Render node at (" + x + ", " + y + ", " + z + ") visiting node " + sppfNode);
    if (!isNotLeaf) y = yMax;
    // compute some coordinates
    double xx = xExtents.get(x);
    double yy = yExtents.get(y);
    double frameHeight = yExtents.get(y + 1) - yy - ySeparation;
    double frameWidth = xExtents.get(x + 1) - xx - xSeparation;
    double frameHeightHalf = frameHeight / 2;
    double frameWidthHalf = frameWidth / 2;

    // Make a canvas containing the label
    Canvas canvas = new Canvas(frameWidth, frameHeight);
    canvas.getGraphicsContext2D().fillText(parser.artLabelStrings[parser.artSPPFNodeLabel(sppfNode)], frameBorder, frameTextBaseLineOffset);

    // Make the enclosing frame
    final Box top = new Box(frameWidth + frameThickness, frameThickness, frameThickness);
    top.setMaterial(frameMaterial);
    top.setTranslateX(frameWidth / 2);
    final Box left = new Box(frameThickness, frameHeight, frameThickness);
    left.setMaterial(frameMaterial);
    left.setTranslateY(frameHeightHalf);
    final Box right = new Box(frameThickness, frameHeight, frameThickness);
    right.setMaterial(frameMaterial);
    right.setTranslateX(frameWidth);
    right.setTranslateY(frameHeightHalf);
    final Box bottom = new Box(frameWidth + frameThickness, frameThickness, frameThickness);
    bottom.setMaterial(frameMaterial);
    bottom.setTranslateX(frameWidthHalf);
    bottom.setTranslateY(frameHeight);

    Box inBar = null, outBar = null;
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
    }

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
    }
    Box zBox = null;
    if (isAmbiguityRoot) {
      double zLength = (renderZ - parentZ) * zSeparation;
      zBox = new Box(frameThickness, frameThickness, zLength);
      zBox.setMaterial(redMaterial);
      zBox.setTranslateX(framePortXOffset);
      zBox.setTranslateY(-ySeparationDiv2);
      zBox.setTranslateZ(-(zLength / 2));
    }
    // Make the enclosing group and place it
    Group nodeGroup = new Group();
    nodeGroup.setTranslateX(xx);
    nodeGroup.setTranslateY(yy);
    nodeGroup.setTranslateZ(z * zSeparation);
    nodeGroup.getChildren().addAll(canvas, top, left, right, bottom);
    if (isNotLeaf) nodeGroup.getChildren().add(outBar);
    if (isNotRoot) nodeGroup.getChildren().add(inBar);
    if (isAmbiguityRoot) nodeGroup.getChildren().add(zBox);

    sGroup.getChildren().add(nodeGroup);
  }

  int renderZ = 0;

  // Return the rightmost coordinate of the subtree rendered by this call
  private int visitSPPFRender(Group sGroup, int sppfNode, int parentCoordinate, int x, int y, int parentZ, boolean isFirst, boolean isAmbiguityRoot) {
    int ret = x;
    int initialZ = renderZ;

    // renderNode(sGroup, sppfNode, x, y, renderZ, parentZ, parser.artSPPFNodePackedNodeList(sppfNode) != 0, isFirst, parentCoordinate >= 0, isAmbiguityRoot);

    // Visit each pack node's children recursively
    int pivot = x;
    for (int p = parser.artSPPFNodePackedNodeList(sppfNode); p != 0; p = parser.artSPPFPackedNodePackedNodeList(p)) {
      if (parser.artSPPFPackedNodeLeftChild(p) != 0) {
        pivot = visitSPPFRender(sGroup, parser.artSPPFPackedNodeLeftChild(p), x, x, y + 1, parentZ, true, renderZ != initialZ);
        ret = visitSPPFRender(sGroup, parser.artSPPFPackedNodeRightChild(p), x, pivot + 1, y + 1, parentZ, true, false);
      } else
        ret = visitSPPFRender(sGroup, parser.artSPPFPackedNodeRightChild(p), x, x, y + 1, parentZ, true, renderZ != initialZ);

      if (parser.artSPPFPackedNodePackedNodeList(p) != 0) renderZ++;
    }

    renderNode(sGroup, sppfNode, x, y, renderZ, parentZ, parser.artSPPFNodePackedNodeList(sppfNode) != 0, isFirst, parentCoordinate >= 0, isAmbiguityRoot);

    final Box acrossBar;
    if (parentCoordinate != x && x != 0) {
      if (isLeftToRight) {
        double length = yExtents.get(x);
        acrossBar = new Box(frameThickness, length + frameThickness, frameThickness);
        acrossBar.setTranslateY(yExtents.get(parentCoordinate) + framePortYOffset + length / 2);
        acrossBar.setTranslateX(xExtents.get(x) - xSeparationDiv2);
      } else {
        double length = xExtents.get(x) - xExtents.get(parentCoordinate);
        acrossBar = new Box(length + frameThickness, frameThickness, frameThickness);
        acrossBar.setTranslateX(xExtents.get(parentCoordinate) + framePortXOffset + length / 2);
        acrossBar.setTranslateY(yExtents.get(y) - ySeparationDiv2);
        acrossBar.setTranslateZ(initialZ * zSeparation);
      }
      acrossBar.setMaterial(frameMaterial);
      sGroup.getChildren().add(acrossBar);
    }
    return ret;
  }

  private void renderSPPF(Group sGroup) {
    // 1 Compute individual column and row extents
    visitTreeComputeExtents(parser.artSPPFRoot(), 0, 0);

    // 2a compute xMax and yMax
    xMax = yMax = 0;
    for (int x : xExtents.keySet())
      if (x > xMax) xMax = x;
    for (int y : yExtents.keySet())
      if (y > yMax) yMax = y;

    // 2b Convert individual extents to absolute cumulative coordinates
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
    visitSPPFRender(sGroup, parser.artSPPFRoot(), -1, 0, 0, 0, false, false);

    // 5 Offset the tree so that it is centred
    sGroup.setTranslateX(-xExtents.get(xMax) / 2);
    sGroup.setTranslateY(-yExtents.get(yMax) / 2);
  }
}
