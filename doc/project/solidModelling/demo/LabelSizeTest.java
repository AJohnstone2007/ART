import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LabelSizeTest extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("A window");
    Label label = new Label("A label");
    Scene scene = new Scene(label);

    primaryStage.setWidth(400);
    //    primaryStage.setHeight(200);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
