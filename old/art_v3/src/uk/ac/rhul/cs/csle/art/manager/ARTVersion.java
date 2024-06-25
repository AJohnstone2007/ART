package uk.ac.rhul.cs.csle.art.manager;
public class ARTVersion {
  public static int major() {return 3;}
  public static int minor() {return 0;}
  public static int build() {return 300;}
  public static String status() {return "AMBER";}
  public static String version() { return major()+"."+minor()+"."+build()+"."+status(); };
}
