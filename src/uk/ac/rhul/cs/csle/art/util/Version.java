package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 507;}
  public static String timeStamp() {return "2025-02-28 18:35:18";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
