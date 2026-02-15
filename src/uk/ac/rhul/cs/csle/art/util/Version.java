package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 911;}
  public static String timeStamp() {return "2026-02-15 03:16:15";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
