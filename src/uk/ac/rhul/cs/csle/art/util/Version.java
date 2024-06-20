package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 61;}
  public static String timeStamp() {return "2024-06-20 22:22:53";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
