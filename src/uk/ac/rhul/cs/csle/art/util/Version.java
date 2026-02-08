package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 904;}
  public static String timeStamp() {return "2026-02-08 18:47:35";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
