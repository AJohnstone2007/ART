package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 889;}
  public static String timeStamp() {return "2025-12-16 11:41:17";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
