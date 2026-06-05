package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 957;}
  public static String timeStamp() {return "2026-06-05 12:53:02";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
