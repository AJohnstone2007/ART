package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 759;}
  public static String timeStamp() {return "2025-09-01 20:40:59";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
