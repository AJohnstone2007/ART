import uk.ac.rhul.cs.csle.art.term.AbstractValuePlugin;

// Rename this class to ARTValuePlugin if you want to develop plugins within the Eclipse workspace; but change it back again before distribution
public class ARTValuePlugin extends AbstractValuePlugin {

  @Override
  public String description() {
    return "Rotabot plugin";
  }

  @Override
  public Object plugin(Object... args) {
    switch ((String) args[0]) {
    case "init":
      System.out.println("Plugin initialised");
      return __done;

    case "home":
      System.out.println("Return to home position");
      return __done;

    case "go":
      System.out.println("Go " + args[1]);
      return __done;

    case "rotate":
      System.out.println("Rotate by " + args[1]);
      return __done;

    default:
      System.out.println("Unknown plugin opeation " + args[0]);
      return __bottom;
    }
  }
}