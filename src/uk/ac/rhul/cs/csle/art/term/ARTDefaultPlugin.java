package uk.ac.rhul.cs.csle.art.term;

public class ARTDefaultPlugin extends AbstractPlugin {

  @Override
  public String description() {
    return "report __user call arguments";
  }

  @Override
  public Object plugin(Object... args) {
    System.out.println(description() + " called with " + args.length + " argument" + (args.length == 1 ? "" : "s"));
    for (Object a : args) {
      System.out.println(a == null ? "null" : a.getClass() + " " + a);
    }
    return args.length == 0 ? null : args[0];
  }

}
