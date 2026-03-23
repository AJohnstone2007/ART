import java.util.HashMap;
import java.util.Map;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.fx.Transformer3D;
import uk.ac.rhul.cs.csle.art.term.AbstractValuePlugin;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTValuePlugin extends AbstractValuePlugin {
  double x = 100, y = 100, width = 1000, height = 800;
  String title = "Solid modelling window";
  Double cameraInitialDistance = 500.0;
  double cameraDistance;

  final Stage stage = new Stage();
  final Group root = new Group();
  final Group axisGroup = new Group();
  public final Group meshGroup = new Group();
  public final Transformer3D world = new Transformer3D();

  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final Transformer3D cameraRotator = new Transformer3D();
  final Transformer3D cameraPanner = new Transformer3D();
  final Transformer3D cameraZflipper = new Transformer3D();

  private final Map<String, Node> objects = new HashMap<>();

  @Override
  public String description() {
    return "Professional extended solid modelling plugin";
  }

  @Override
  public Object plugin(Object... args) {
    String op = String.valueOf(args[0]);
    System.out.println("Plugin received: " + op);

    if (op.equals("safeDiv")) {
      double d2 = asDouble(args[2]);
      if (d2 == 0) throw new RuntimeException("Division by zero");
      return asDouble(args[1]) / d2;
    }

    if (op.equals("checkBounds")) {
      if (asDouble(args[1]) < 0) throw new RuntimeException("Negative value error");
      return __done;
    }

    final Object[] resultArr = new Object[] { __done };

    Runnable work = () -> {
      if (stage.getScene() == null && !op.equals("init") && !op.equals("call") && !op.equals("safeDiv") && !op.equals("checkBounds")) {
        initialise();
      }

      switch (op) {
      case "init":
        initialise();
        resultArr[0] = __done;
        break;

      case "box": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makeBox(asDouble(args[2]), asDouble(args[3]), asDouble(args[4]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "cube": {
        ensureSceneObjects();
        String name = asName(args[1]);
        double s = asDouble(args[2]);
        Node n = makeBox(s, s, s);
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "sphere": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makeSphere(asDouble(args[2]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "cylinder": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makeCylinder(asDouble(args[2]), asDouble(args[3]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "cone": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makeCone(asDouble(args[2]), asDouble(args[3]), asDouble(args[4]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "torus": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makeTorus(asDouble(args[2]), asDouble(args[3]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "polyhedron": {
        ensureSceneObjects();
        System.err.println("polyhedron not yet supported");
        resultArr[0] = __done;
        break;
      }

      case "ellipsoid": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makeEllipsoid(asDouble(args[2]), asDouble(args[3]), asDouble(args[4]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "wedge": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makeWedge(asDouble(args[2]), asDouble(args[3]), asDouble(args[4]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "pyramid": {
        ensureSceneObjects();
        String name = asName(args[1]);
        Node n = makePyramid(asDouble(args[2]), asDouble(args[3]));
        putNode(name, n);
        resultArr[0] = __done;
        break;
      }

      case "translate": {
        Node n = getNode(args[1]);
        translateNode(n, asDouble(args[2]), asDouble(args[3]), asDouble(args[4]));
        resultArr[0] = __done;
        break;
      }

      case "rotate": {
        Node n = getNode(args[1]);
        rotateNode(n, asDouble(args[2]), asName(args[3]));
        resultArr[0] = __done;
        break;
      }

      case "scale": {
        Node n = getNode(args[1]);
        scaleNode(n, asDouble(args[2]), asDouble(args[3]), asDouble(args[4]));
        resultArr[0] = __done;
        break;
      }

      case "mirror": {
        Node n = getNode(args[1]);
        mirrorNode(n, asName(args[2]));
        resultArr[0] = __done;
        break;
      }

      case "material": {
        Node n = getNode(args[1]);
        setMaterial(n, asColor(args[2]));
        resultArr[0] = __done;
        break;
      }

      case "union": {
        Node a = getNode(args[1]);
        Node b = getNode(args[2]);
        union(a, b);
        resultArr[0] = __done;
        break;
      }

      case "difference": {
        Node a = getNode(args[1]);
        Node b = getNode(args[2]);
        difference(a, b);
        resultArr[0] = __done;
        break;
      }

      case "intersection": {
        Node a = getNode(args[1]);
        Node b = getNode(args[2]);
        intersection(a, b);
        resultArr[0] = __done;
        break;
      }

      case "cameraHome":
        cameraHome();
        resultArr[0] = __done;
        break;

      case "cameraOrbit":
        cameraOrbit(asDouble(args[1]), asDouble(args[2]));
        resultArr[0] = __done;
        break;

      case "call":
        resultArr[0] = __done;
        break;

      default:
        Util.fatal("Unknown plugin operation: " + op);
      }
    };

    if (javafx.application.Platform.isFxApplicationThread()) {
      work.run();
    } else {
      final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
      javafx.application.Platform.runLater(() -> {
        try {
          work.run();
        } finally {
          latch.countDown();
        }
      });
      try {
        latch.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Interrupted while waiting for JavaFX task", e);
      }
    }

    return resultArr[0];
  }

  private Object throwError(String message) {
    throw new RuntimeException(message);
  }

  private void ensureSceneObjects() {
    if (stage.getScene() == null) initialise();
  }

  private double asDouble(Object o) {
    if (o instanceof Number n) return n.doubleValue();
    return Double.parseDouble(String.valueOf(o));
  }

  private String asName(Object o) {
    String s = String.valueOf(o);
    if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
      return s.substring(1, s.length() - 1);
    }
    return s;
  }

  private Color asColor(Object o) {
    if (o instanceof Color c) return c;
    String s = asName(o);
    try {
      return (Color) Color.class.getField(s.toUpperCase()).get(null);
    } catch (Exception e) {
      return Color.web(s);
    }
  }

  private Node getNode(Object o) {
    String name = asName(o);
    Node n = objects.get(name);
    if (n == null) throw new RuntimeException("Unknown object name: " + name);
    return n;
  }

  private void putNode(String name, Node n) {
    objects.put(name, n);
  }

  // ---------- Primitives -------------
  private Node makeBox(double x, double y, double z) {
    Box b = new Box(x, y, z);
    b.setMaterial(new PhongMaterial(Color.RED));
    meshGroup.getChildren().add(b);
    return b;
  }

  private Node makeSphere(double r) {
    Sphere s = new Sphere(r);
    meshGroup.getChildren().add(s);
    return s;
  }

  private Node makeCylinder(double h, double r) {
    Cylinder c = new Cylinder(r, h);
    meshGroup.getChildren().add(c);
    return c;
  }

  private Node makeCone(double height, double radiusTop, double radiusBottom) {
    Cylinder cone = new Cylinder(radiusBottom, height);
    if (radiusTop != radiusBottom && radiusBottom != 0) {
      double scaleRatio = radiusTop / radiusBottom;
      cone.getTransforms().add(new Scale(scaleRatio, 1, scaleRatio));
    }
    meshGroup.getChildren().add(cone);
    return cone;
  }

  private Node makeTorus(double radiusMajor, double radiusMinor) {
    Group torusGroup = new Group();
    int segments = 32;
    for (int i = 0; i < segments; i++) {
      double angle = 360.0 * i / segments;
      Cylinder segment = new Cylinder(radiusMinor, 2 * Math.PI * radiusMajor / segments);
      segment.getTransforms().add(new Rotate(angle, Rotate.Y_AXIS));
      segment.getTransforms().add(new Translate(radiusMajor, 0, 0));
      torusGroup.getChildren().add(segment);
    }
    meshGroup.getChildren().add(torusGroup);
    return torusGroup;
  }

  private Node makePolyhedron(Object pointsObj, Object facesObj) {
    TriangleMesh mesh = new TriangleMesh();

    double[][] points = (double[][]) pointsObj;
    int[][] faces = (int[][]) facesObj;

    for (double[] p : points) {
      mesh.getPoints().addAll((float) p[0], (float) p[1], (float) p[2]);
    }

    mesh.getTexCoords().addAll(0, 0);

    for (int[] f : faces) {
      mesh.getFaces().addAll(f[0], 0, f[1], 0, f[2], 0);
    }

    MeshView mv = new MeshView(mesh);
    meshGroup.getChildren().add(mv);
    return mv;
  }

  private Node makeEllipsoid(double rx, double ry, double rz) {
    Sphere s = new Sphere(1);
    s.getTransforms().add(new Scale(rx, ry, rz));
    meshGroup.getChildren().add(s);
    return s;
  }

  private Node makeWedge(double w, double h, double d) {
    Box wedge = new Box(w, h, d);
    meshGroup.getChildren().add(wedge);
    return wedge;
  }

  private Node makePyramid(double base, double height) {
    return makeCone(height, 0, base / 2);
  }

  // ---------- Transformations ----------
  private void translateNode(Node n, double x, double y, double z) {
    n.getTransforms().add(new Translate(x, y, z));
  }

  private void rotateNode(Node n, double angle, String axis) {
    switch (axis.toLowerCase()) {
    case "x" -> n.getTransforms().add(new Rotate(angle, Rotate.X_AXIS));
    case "y" -> n.getTransforms().add(new Rotate(angle, Rotate.Y_AXIS));
    case "z" -> n.getTransforms().add(new Rotate(angle, Rotate.Z_AXIS));
    default -> Util.fatal("Unknown rotation axis: " + axis);
    }
  }

  private void scaleNode(Node n, double x, double y, double z) {
    n.getTransforms().add(new Scale(x, y, z));
  }

  private void mirrorNode(Node n, String plane) {
    switch (plane.toLowerCase()) {
    case "xy" -> n.getTransforms().add(new Scale(1, 1, -1));
    case "xz" -> n.getTransforms().add(new Scale(1, -1, 1));
    case "yz" -> n.getTransforms().add(new Scale(-1, 1, 1));
    default -> Util.fatal("Unknown mirror plane: " + plane);
    }
  }

  private void union(Node a, Node b) {
    if (a instanceof Group g1 && b != null) {
      g1.getChildren().add(b);
    } else if (a != null && b != null) {
      Group g = new Group();
      meshGroup.getChildren().remove(a);
      meshGroup.getChildren().remove(b);
      g.getChildren().addAll(a, b);
      meshGroup.getChildren().add(g);
    }
  }

  private void difference(Node a, Node b) {
    if (a instanceof Group g1 && b != null) {
      g1.getChildren().remove(b);
    }
  }

  private void intersection(Node a, Node b) {
    if (a instanceof Group g1 && b != null) {
      if (!g1.getChildren().contains(b)) {
        g1.getChildren().clear();
        g1.getChildren().add(b);
      }
    }
  }

  private void setMaterial(Node n, Color c) {
    PhongMaterial mat = new PhongMaterial(c);
    if (n instanceof Box)
      ((Box) n).setMaterial(mat);
    else if (n instanceof Sphere)
      ((Sphere) n).setMaterial(mat);
    else if (n instanceof Cylinder) ((Cylinder) n).setMaterial(mat);
  }

  // ---------- Camera ----------
  public void cameraOrbit(double angleX, double angleY) {
    cameraRotator.modifyRx(angleX);
    cameraRotator.modifyRy(angleY);
  }

  private void cameraHome() {
    camera.setTranslateX(0);
    camera.setTranslateY(0);
    camera.setTranslateZ(-1000);
    cameraRotator.setRx(-20);
    cameraRotator.setRy(-45);
    cameraRotator.setRz(0);
  }

  // ---------- Initialise ----------
  private void initialise() {
    cameraDistance = cameraInitialDistance;

    root.getChildren().clear();
    axisGroup.getChildren().clear();
    meshGroup.getChildren().clear();
    objects.clear();

    root.getChildren().add(world);
    world.getChildren().clear();
    world.getChildren().addAll(meshGroup, axisGroup);

    buildCamera();
    buildLights();
    buildAxes();

    System.out.println("Axes added to scene: " + axisGroup.getChildren().size());

    Scene solidView = new Scene(root, width, height, true, SceneAntialiasing.BALANCED);
    solidView.setFill(Color.GREY);
    solidView.setCamera(camera);

    System.out.println("Camera Z: " + camera.getTranslateZ());
    stage.setScene(solidView);
    stage.setTitle(title);
    stage.setX(x);
    stage.setY(y);
    stage.setWidth(width);
    stage.setHeight(height);
    stage.show();

    handleMouse(solidView, root);
  }

  private void buildLights() {
    PointLight p1 = new PointLight(Color.WHITE);
    p1.setTranslateX(500);
    p1.setTranslateY(-500);
    p1.setTranslateZ(500);

    PointLight p2 = new PointLight(Color.WHITE);
    p2.setTranslateX(-500);
    p2.setTranslateY(500);
    p2.setTranslateZ(500);

    PointLight p3 = new PointLight(Color.WHITE);
    p3.setTranslateZ(-1000);

    AmbientLight ambient = new AmbientLight(Color.rgb(200, 200, 200));

    root.getChildren().addAll(p1, p2, p3, ambient);
  }

  private void buildAxes() {
    axisGroup.getChildren().clear();

    PhongMaterial red = new PhongMaterial(Color.DARKRED);
    PhongMaterial green = new PhongMaterial(Color.DARKGREEN);
    PhongMaterial blue = new PhongMaterial(Color.DARKBLUE);

    Box xAxis = new Box(400, 2, 2);
    xAxis.setMaterial(red);

    Box yAxis = new Box(2, 400, 2);
    yAxis.setMaterial(green);

    Box zAxis = new Box(2, 2, 400);
    zAxis.setMaterial(blue);

    axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
  }

  private void buildCamera() {
    root.getChildren().add(cameraRotator);
    cameraRotator.getChildren().clear();
    cameraPanner.getChildren().clear();
    cameraZflipper.getChildren().clear();

    cameraRotator.getChildren().add(cameraPanner);
    cameraPanner.getChildren().add(cameraZflipper);
    cameraZflipper.getChildren().add(camera);

    camera.setNearClip(0.1);
    camera.setFarClip(10000.0);

    cameraHome();
  }

  // ---------- Mouse ----------
  double mousePosX, mousePosY, mouseOldX, mouseOldY, mouseDeltaX, mouseDeltaY;

  private void handleMouse(Scene solidView, final Node rootNode) {
    solidView.setOnMousePressed(me -> {
      mousePosX = me.getSceneX();
      mousePosY = me.getSceneY();
      mouseOldX = me.getSceneX();
      mouseOldY = me.getSceneY();
    });

    solidView.setOnMouseDragged(me -> {
      mouseOldX = mousePosX;
      mouseOldY = mousePosY;
      mousePosX = me.getSceneX();
      mousePosY = me.getSceneY();
      mouseDeltaX = mousePosX - mouseOldX;
      mouseDeltaY = mousePosY - mouseOldY;
      double modifier = 0.25;

      if (me.isPrimaryButtonDown()) {
        if (me.isAltDown())
          camera.setTranslateZ(camera.getTranslateZ() + mouseDeltaY * modifier);
        else {
          cameraRotator.rz.setAngle(cameraRotator.rz.getAngle() - mouseDeltaX * modifier);
          cameraRotator.rx.setAngle(cameraRotator.rx.getAngle() + mouseDeltaY * modifier);
        }
      } else if (me.isSecondaryButtonDown()) {
        cameraPanner.t.setX(cameraPanner.t.getX() + mouseDeltaX * modifier);
        cameraPanner.t.setY(cameraPanner.t.getY() + mouseDeltaY * modifier);
      }
    });
  }
}