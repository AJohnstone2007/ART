package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 69;}
  public static String timeStamp() {return "2024-06-26 09:45:31";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
