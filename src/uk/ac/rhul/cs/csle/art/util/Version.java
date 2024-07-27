package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 73;}
  public static String timeStamp() {return "2024-07-27 22:08:27";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
