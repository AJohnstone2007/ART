package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 950;}
  public static String timeStamp() {return "2026-05-28 11:42:56";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
