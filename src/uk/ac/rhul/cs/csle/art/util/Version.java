package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 964;}
  public static String timeStamp() {return "2026-07-06 20:29:34";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
