package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 1010;}
  public static String timeStamp() {return "2026-10-03 19:31:19";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
