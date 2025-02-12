package uk.ac.rhul.cs.csle.art.term;

public class ARTDefaultPlugin extends AbstractPlugin {

  @Override
  public String description() {
    return "System default plugin";
  }

  @Override
  public Object plugin(Object... args) {
    System.out.println("Plugin " + description() + " called with " + args.length + " argument" + (args.length == 1 ? "" : "s"));
    for (Object a : args) {
      System.out.println(a.getClass() + " " + a);
    }
    return __done;
  }

}
