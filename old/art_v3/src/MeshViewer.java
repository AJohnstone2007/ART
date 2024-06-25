import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTSTLMesh;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTSTLParser;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTXform;

public class MeshViewer {
  MeshViewer(ARTSTLParser stlParser, float offsetX, float offsetY, float offsetZ) throws ARTException {
    Stage stage = new Stage();
    buildScene();
    buildCamera();
    buildAxes();

    PhongMaterial material = new PhongMaterial(Color.OLDLACE);

    final Group sGroup = new Group();

    MeshView meshView = new MeshView(new ARTSTLMesh(stlParser, offsetX, offsetY, offsetZ));

    meshView.setMaterial(material);
    sGroup.getChildren().add(meshView);
    world.getChildren().addAll(sGroup);

    Scene scene = new Scene(root, 900, 600, true);
    scene.setFill(Color.GREY);
    handleMouse(scene, world);

    scene.setCamera(camera);
    stage.setScene(scene);
    stage.show();
  }

  final Group root = new Group();
  final Group axisGroup = new Group();
  final ARTXform world = new ARTXform();
  final ARTXform moleculeGroup = new ARTXform();
  boolean timelinePlaying = false;
  double ONE_FRAME = 1.0 / 24.0;
  double DELTA_MULTIPLIER = 200.0;
  double CONTROL_MULTIPLIER = 0.1;
  double SHIFT_MULTIPLIER = 0.1;
  double ALT_MULTIPLIER = 0.5;

  private void buildScene() {
    root.getChildren().add(world);
  }

  private void buildAxes() {
    final PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    final PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    final PhongMaterial blueMaterial = new PhongMaterial();
    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    final Box xAxis = new Box(240.0, 1, 1);
    final Box yAxis = new Box(1, 240.0, 1);
    final Box zAxis = new Box(1, 1, 240.0);

    xAxis.setMaterial(redMaterial);
    yAxis.setMaterial(greenMaterial);
    zAxis.setMaterial(blueMaterial);

    axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
    world.getChildren().addAll(axisGroup);
  }

  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final ARTXform cameraForm1 = new ARTXform();
  final ARTXform cameraForm2 = new ARTXform();
  final ARTXform cameraForm3 = new ARTXform();
  final double cameraDistance = 450;

  private void buildCamera() {
    root.getChildren().add(cameraForm1);
    cameraForm1.getChildren().add(cameraForm2);
    cameraForm2.getChildren().add(cameraForm3);
    cameraForm3.getChildren().add(camera);
    cameraForm3.setRotateZ(180.0);

    camera.setNearClip(0.1);
    camera.setFarClip(10000.0);
    camera.setTranslateZ(-cameraDistance);
    cameraForm1.ry.setAngle(00.0);
    cameraForm1.rx.setAngle(0);
  }

  double mousePosX;
  double mousePosY;
  double mouseOldX;
  double mouseOldY;
  double mouseDeltaX;
  double mouseDeltaY;

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

        double modifier = 1.0;
        double modifierFactor = 0.25;

        if (me.isControlDown()) {
          modifier = 0.1;
        }
        if (me.isShiftDown()) {
          modifier = 10.0;
        }
        if (me.isPrimaryButtonDown()) {
          if (me.isAltDown()) {
            double z = camera.getTranslateZ();
            double newZ = z + mouseDeltaY * modifierFactor * modifier;
            camera.setTranslateZ(newZ);
          } else {
            cameraForm1.ry.setAngle(cameraForm1.ry.getAngle() - mouseDeltaX * modifierFactor * modifier); // +
            cameraForm1.rx.setAngle(cameraForm1.rx.getAngle() + mouseDeltaY * modifierFactor * modifier); // -
          }
        } else if (me.isSecondaryButtonDown()) {
          cameraForm2.t.setX(cameraForm2.t.getX() + mouseDeltaX * modifierFactor * modifier); // -
          cameraForm2.t.setY(cameraForm2.t.getY() + mouseDeltaY * modifierFactor * modifier); // -
        }
      }
    });
  }
}
