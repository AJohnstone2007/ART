import uk.ac.rhul.cs.csle.art.term.AbstractPlugin;

// Rename this class to ARTValuePlugin if you want to develop plugins within the Eclipse workspace; but change it back again before distribution
public class ARTValuePluginNoFX extends AbstractPlugin {

  @Override
  public String description() {
    return "Adrian's example plugin";
  }

  @Override
  public Object plugin(Object... args) {
    switch ((String) args[0]) {
    case "init":
      System.out.println("Plugin initialised");
      return __done;

    case "show":
      for (var i : args)
        System.out.println(i);
      return __done;

    case "invert":
      return -(Integer) args[1];

    default:
      return __bottom;
    }
  }
}