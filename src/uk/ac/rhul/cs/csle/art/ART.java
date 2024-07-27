package uk.ac.rhul.cs.csle.art;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import javafx.application.Application;
import uk.ac.rhul.cs.csle.art.fx.FXStart;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;
import uk.ac.rhul.cs.csle.art.old.v4.core.ARTV4;
import uk.ac.rhul.cs.csle.art.script.ARTScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.test.AJDebug;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.Version;

public class ART {
  public static String specificationString = null;
  public static String tryFilename = null;
  public static boolean tracing = false;

  // @formatter:off
  public static void main(String[] args) {
    if (args.length > 0) switch (args[0]) { // Test for initial special mode argument
    case "incVersion": incVersion(); return;              // Undocumented internal mode
    case "ajdebug": new AJDebug(args); return;             // Undocumented internal mode
    case "v3": new ARTV3(Arrays.copyOfRange(args, 1, args.length)); return; // Undocumented internal mode
    case "v4": new ARTV4(Arrays.copyOfRange(args, 1, args.length)); return; // Undocumented internal mode
    case "noFX": new ARTScriptTermInterpreter(new ITermsLowLevelAPI()).interpret(Util.scriptString(args)); return; // Run batch mode without fx in this context
    case "noIDE": specificationString = Util.scriptString(args); Application.launch(FXStart.class, args); return; // Run batch mode in a JavaFX application
    case "version": System.out.println("ART Version " + Version.version()); return; // Run batch mode in a JavaFX application
    }
    specificationString = args.length < 1 ? "" : args[0];
    tryFilename = args.length < 2 ? "" : args[1];
    Application.launch(FXStart.class, args);  // We arrive here only if none of the special modes is activated; hence use the IDE
  }
  // @formatter:on

  private static void incVersion() {
    try {
      int newBuild = Version.build() + 1;
      String timeStamp = LocalDate.now() + " " + LocalTime.now();
      timeStamp = timeStamp.substring(0, timeStamp.indexOf('.'));

      System.out.printf("Updating from %s: new build %d%n", Version.version(), newBuild, timeStamp);
      PrintWriter pw;
      pw = new PrintWriter("Version.java.new");
      pw.printf(
          "package uk.ac.rhul.cs.csle.art.util;%n" + "public class Version {%n" + "  public static int major() {return %d;}%n"
              + "  public static int minor() {return %d;}%n" + "  public static int build() {return %d;}%n"
              + "  public static String timeStamp() {return \"%s\";}%n"
              + "  public static String version() { return major()+\".\"+minor()+\".\"+build() + \" \" + timeStamp(); };%n" + "}%n",
          Version.major(), Version.minor(), newBuild, timeStamp);
      pw.close();

      pw = new PrintWriter("manifest.local.new");
      pw.printf("Specification-Vendor: Center for Software Language Engineering, RHUL%n" + "Specification-Title: ART%n" + "Specification-Version: 5%n"
          + "Implementation-Vendor: Center for Software Language Engineering, RHUL%n" + "Implementation-Title: ART%n" + "Implementation-Version: %d.%d.%d%n"
          + "Implementation-Build-Date: %s%n" + "Main-Class: uk.ac.rhul.cs.csle.art.ART%n", Version.major(), Version.minor(), newBuild, timeStamp);
      pw.close();
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open Version source file");
    }
  }
}
