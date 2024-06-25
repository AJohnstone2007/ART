package uk.ac.rhul.cs.csle.art.util;
public class Version {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 64;}
  public static String timeStamp() {return "2024-06-25 21:39:12";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
