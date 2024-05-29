package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 44;}
  public static String timeStamp() {return "2024-05-29 23:00:18";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
