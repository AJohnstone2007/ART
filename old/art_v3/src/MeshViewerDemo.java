import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTSTLASCIIParser;

public class MeshViewerDemo extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws ARTException {

    new MeshViewer(new ARTSTLASCIIParser("stl/02.stl"), 0, 0, 0);
    // new MeshViewer(new ARTSTLBinaryParser("stl/Series-80-upper.stl"), 0, 0, 0);
  }
}
