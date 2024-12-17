package uk.ac.rhul.cs.csle.art.term;

public class ARTPlugin implements ARTPluginInterface {

  @Override
  public String name() {
    return "Default ARTPlugin";
  }

  @Override
  public Object plugin(Object... args) {
    System.out.println(name() + " called with " + args.length + " argument" + (args.length == 1 ? "" : "s"));
    for (Object a : args) {
      System.out.println(a == null ? "null" : a.getClass() + " " + a);
    }
    return args.length == 0 ? null : args[0];
  }

}
