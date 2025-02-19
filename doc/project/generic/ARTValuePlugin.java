import uk.ac.rhul.cs.csle.art.term.AbstractValuePlugin;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTValuePlugin extends AbstractValuePlugin {

  @Override
  public String description() {
    return "Adrian's generic example plugin";
  }

  @Override
  public Object plugin(Object... args) {
    switch ((String) args[0]) {
    case "init":
      System.out.println("Plugin initialised");
      return __done;

    case "type":
      for (var i : args)
        System.out.println(i);
      return __done; // Command returns __done

    case "invert":
      return -(Integer) args[1]; // Expression operator returns value

    default:
      Util.fatal("Unknown plugin operation: " + args[0]);
      return __bottom;
    }
  }
}