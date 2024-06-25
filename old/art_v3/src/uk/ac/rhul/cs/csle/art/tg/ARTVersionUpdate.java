package uk.ac.rhul.cs.csle.art.tg;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.manager.ARTVersion;

public class ARTVersionUpdate {

  public static void main(String[] args) throws FileNotFoundException {
    String status = "AMBER";
    int newBuild = ARTVersion.build() + 1;

    if (args.length != 0) status = args[0];
    System.out.printf("Updating from %s: new build %d, new status %s%n", ARTVersion.version(), newBuild, status);
    PrintWriter pw = new PrintWriter("ARTVersion.java.new");

    pw.printf(
        "package uk.ac.rhul.cs.csle.art;%n" + "public class ARTVersion {%n" + "  public static int major() {return %d;}%n"
            + "  public static int minor() {return %d;}%n" + "  public static int build() {return %d;}%n" + "  public static String status() {return \"%s\";}%n"
            + "  public static String version() { return major()+\".\"+minor()+\".\"+build()+\".\"+status(); };%n" + "}%n",
        ARTVersion.major(), ARTVersion.minor(), newBuild, status);
    pw.close();

    pw = new PrintWriter("artversion.h.new");
    pw.printf("#define ARTMAJOR %d%n" + "#define ARTMINOR %d%n" + "#define ARTBUILD %d%n" + "#define ARTSTATUS \"%s\"%n", +ARTVersion.major(),
        ARTVersion.minor(), newBuild, status);
    pw.close();

    pw = new PrintWriter("manifest.local.new");
    pw.printf("Specification-Vendor: Center for Software Language Engineering, RHUL%n" + "Specification-Title: ART%n" + "Specification-Version: 3.0%n"
        + "Implementation-Vendor: Center for Software Language Engineering, RHUL%n" + "Implementation-Title: ART%n" + "Implementation-Version: %d.%d.%d.%s%n"
        + "Main-Class: uk.ac.rhul.cs.csle.art.ART%n", ARTVersion.major(), ARTVersion.minor(), newBuild, status);
    pw.close();
  }
}
