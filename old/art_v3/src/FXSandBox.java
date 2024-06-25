import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class FXSandBox extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    PhongMaterial material = makeMaterial();

    // First, we make a group whose sole function is to rotate the world 180 degrees around the axis, so that the Y axis goes up the screen
    Group flipRoot = new Group();
    flipRoot.setRotationAxis(Rotate.X_AXIS);
    flipRoot.setRotate(180);
    Group root = new Group();
    flipRoot.getChildren().add(root);
    Scene scene = new Scene(flipRoot, 1000, 1000, true);
    // the root Group is now the 'real' root under which all other nodes will live

    // Set up a grey world with (x, y, z) axes coloured (red, green, blue)
    scene.setFill(Color.GREY);
    makeAxes(root, -10, 100);
    primaryStage.setScene(scene);

    // For 3D scenes in Java FX, use a perspective camera withe fixed eye paramater set true
    // Note there is no camera control in this application, so we expect to see the red and green axes with a blue dot at the origin denoting the z axis
    PerspectiveCamera camera = new PerspectiveCamera(true);
    camera.setNearClip(0.1);
    camera.setFarClip(1000.0);
    camera.setTranslateZ(-500); // By default the camera looks Z positive so zoom out by moving Z negative
    scene.setCamera(camera);

    // A test mesh
    TriangleMesh mesh = makePyramid(75, 100);
    MeshView meshView = new MeshView(mesh);
    meshView.setCullFace(CullFace.NONE); // So we can see the backs of the triangles in case the winding order is incorrect - will render in black
    meshView.setMaterial(material);
    setColour(mesh, 0, 1);
    setColour(mesh, 1, 2);
    setColour(mesh, 2, 3);
    setColour(mesh, 3, 4);
    setColour(mesh, 4, 5);
    setColour(mesh, 5, 6);

    root.getChildren().add(meshView);

    primaryStage.show(); // Make everything visible
  }

  // Set specified face to one of the eight colours
  private void setColour(TriangleMesh mesh, int faceNumber, int colourNumber) {
    mesh.getFaces().set(faceNumber * 6 + 1, colourNumber);
    mesh.getFaces().set(faceNumber * 6 + 3, colourNumber);
    mesh.getFaces().set(faceNumber * 6 + 5, colourNumber);
  }

  private PhongMaterial makeMaterial() {
    PhongMaterial phongMaterial = new PhongMaterial();
    WritableImage textureImage = new WritableImage(8, 1);
    PixelWriter textureWriter = textureImage.getPixelWriter();
    textureWriter.setColor(0, 0, Color.BLACK);
    textureWriter.setColor(1, 0, Color.RED);
    textureWriter.setColor(2, 0, Color.GREEN);
    textureWriter.setColor(3, 0, Color.YELLOW);
    textureWriter.setColor(4, 0, Color.BLUE);
    textureWriter.setColor(5, 0, Color.MAGENTA);
    textureWriter.setColor(6, 0, Color.CYAN);
    textureWriter.setColor(7, 0, Color.WHITE);
    phongMaterial.setDiffuseMap(textureImage);
    return phongMaterial;
  }

  // Create an array of eight texture coordinates that map to the centres of the eight pixels in the textureImage
  private void loadTextureCoordinates(TriangleMesh mesh) {
    float boundary = 1f / 8f;
    mesh.getTexCoords().addAll(.5f * boundary, .5f, 1.5f * boundary, .5f, 2.5f * boundary, .5f, 3.5f * boundary, .5f, 4.5f * boundary, .5f, 5.5f * boundary,
        .5f, 6.5f * boundary, .5f, 7.5f * boundary, .5f);
  }

  private void makeAxes(Group group, double negativeExtent, double positiveExtent) {
    double fullExtent = -negativeExtent + positiveExtent;

    final PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    final PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    final PhongMaterial blueMaterial = new PhongMaterial();
    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    // Initial position of axis will be centred on the middle of the bar: we need to offset by
    double offset = fullExtent / 2 + negativeExtent;
    final Box xAxis = new Box(fullExtent, 1, 1);
    xAxis.setTranslateX(offset);
    xAxis.setMaterial(redMaterial);
    final Box yAxis = new Box(1, fullExtent, 1);
    yAxis.setTranslateY(offset);
    yAxis.setMaterial(greenMaterial);
    final Box zAxis = new Box(1, 1, fullExtent);
    zAxis.setTranslateZ(offset);
    zAxis.setMaterial(blueMaterial);

    group.getChildren().addAll(xAxis, yAxis, zAxis);
  }

  // Experimental meshes below here
  TriangleMesh makeRightAngledTriangle(float xExtent, float yExtent) {
    TriangleMesh triangleMesh = new TriangleMesh();
    triangleMesh.getPoints().addAll(0f, 0f, 0f, /**/ xExtent, 0f, 0f, /**/ 0f, yExtent, 0f); // (0,0,0), (x,0,0), (0,y,0)
    loadTextureCoordinates(triangleMesh);
    triangleMesh.getFaces().addAll(0, 0, 1, 0, 2, 0);

    return triangleMesh;
  }

  TriangleMesh makeSquare(float extent) {
    TriangleMesh triangleMesh = new TriangleMesh();
    triangleMesh.getPoints().addAll(0f, 0f, 0f, /**/ extent, 0f, 0f, /**/ 0f, extent, 0f, /**/ extent, extent, 0f); // (0,0,0), (x,0,0), (0,y,0)
    loadTextureCoordinates(triangleMesh);
    triangleMesh.getFaces().addAll(0, 0, 1, 0, 2, 0, /**/ 1, 0, 3, 0, 2, 0);

    return triangleMesh;
  }

  TriangleMesh makeRightAngledTetrahedron(float xExtent, float yExtent, float zExtent) {
    TriangleMesh triangleMesh = new TriangleMesh();
    triangleMesh.getPoints().addAll(0f, 0f, 0f, /**/ xExtent, 0f, 0f, /**/ 0f, yExtent, 0f, /**/ 0f, 0f, zExtent); // (0,0,0), (x,0,0), (0,y,0), (0,0,z)
    loadTextureCoordinates(triangleMesh);
    triangleMesh.getFaces().addAll(0, 0, 1, 0, 2, 0, /**/ 0, 0, 1, 0, 3, 0, /**/ 0, 0, 2, 0, 3, 0, /**/ 1, 0, 2, 0, 3, 0);

    return triangleMesh;
  }

  TriangleMesh makePyramid(float height, float side) {
    TriangleMesh triangleMesh = new TriangleMesh();

    triangleMesh.getPoints().addAll(0f, 0f, 0f, /**/ 0f, height, -side / 2, /**/ -side / 2, height, 0f, /**/ side / 2, height, 0f, /**/ 0f, height, side / 2);
    loadTextureCoordinates(triangleMesh);
    triangleMesh.getFaces().addAll(0, 0, 2, 0, 1, 0, /**/ 0, 0, 1, 0, 3, 0, /**/ 0, 0, 3, 0, 4, 0, /**/ 0, 0, 4, 0, 2, 0, /**/ 4, 0, 1, 0, 2, 0, /**/
        4, 0, 3, /**/ 0, 1, 0);

    return triangleMesh;
  }
}
