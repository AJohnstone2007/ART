package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 740;}
  public static String timeStamp() {return "2025-08-19 22:48:43";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
