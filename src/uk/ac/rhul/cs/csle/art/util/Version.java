package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 987;}
  public static String timeStamp() {return "2026-08-24 19:58:07";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
