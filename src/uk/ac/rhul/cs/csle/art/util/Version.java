package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 651;}
  public static String timeStamp() {return "2025-06-21 03:04:38";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
