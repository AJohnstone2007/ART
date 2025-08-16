package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 726;}
  public static String timeStamp() {return "2025-08-16 14:21:55";}
  public static String version() { return major()+"_"+minor()+"_"+build() + " " + timeStamp(); };
}
