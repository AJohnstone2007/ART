import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.term.AbstractPlugin;

// Rename this class to ARTValuePlugin if you want to develop plugins within the Eclipse workspace; but change it back again before distribution
public class ARTValuePluginFX extends AbstractPlugin {

  @Override
  public String description() {
    return "Adrian's example JavaFX plugin";
  }

  Group root = new Group();

  @Override
  public Object plugin(Object... args) {
    switch ((String) args[0]) {
    case "init":
      Stage stage = new Stage();
      stage.setScene(new Scene(root, 700, 300));
      stage.show();
      break;

    case "show":
      System.out.println("Showing: " + args[1] + args[2]);
      root.getChildren().add(new Label("Show: " + args[1] + args[2]));
      break;

    case "invert":
      return -(Integer) args[1];
    }

    return __done;
  }
}