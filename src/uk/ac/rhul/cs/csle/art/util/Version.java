package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 725;}
  public static String timeStamp() {return "2025-08-15 23:22:37";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
