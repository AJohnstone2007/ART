package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 479;}
  public static String timeStamp() {return "2025-02-17 15:48:42";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
