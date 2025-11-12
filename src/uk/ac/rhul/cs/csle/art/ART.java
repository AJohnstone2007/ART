package uk.ac.rhul.cs.csle.art;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import javafx.application.Application;
import uk.ac.rhul.cs.csle.art.fx.FXStart;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;
import uk.ac.rhul.cs.csle.art.old.v4.core.ARTV4;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.test.AJDebug;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.Version;

public class ART {
  public static String clargs[];
  public static boolean tracing = false;

  // @formatter:off
  public static void main(String[] args) {
    clargs = args;
    if (args.length > 0) switch (args[0].toLowerCase()) {            // Test for initial special mode argument
    case "incver":    incVersion(); return;                                        // Undocumented internal mode
    case "ajdebug":   new AJDebug(args); return;                                   // Undocumented internal mode
    case "v3":        new ARTV3(Arrays.copyOfRange(args, 1, args.length)); return; // Undocumented internal mode
    case "v4":        new ARTV4(Arrays.copyOfRange(args, 1, args.length)); return; // Undocumented internal mode
    case "ide", "fx": Application.launch(FXStart.class); return;
    default:          new ScriptInterpreter(artScriptString(args)); return; // Run batch mode without fx in this context
    }
    // No args
    Util.info("ART " + Version.version() + "\nUsage:");
    Util.info("  ide <specfilename> <tryfilename> - open IDE windows on ART specification in file <specfilename> and try input in file <tryfilename>");
    Util.info("  fx <spec> - interpret <spec> directly under JavaFX");
    Util.info("  <spec> - interpret <spec> directly without JavaFX");
  }
  // @formatter:on

  public static String artScriptString(String[] args) { // Construct an ART script string, processing embedded filenames accordingly
    StringBuilder scriptStringBuilder = new StringBuilder();
    for (int argp = 0; argp < args.length; argp++) {
      if (args[argp].endsWith(".art"))
        try {
          // Util.info("Appending contents of ART script file " + args[argp]);
          scriptStringBuilder.append(Files.readString(Paths.get((args[argp]))));
        } catch (IOException e) {
          Util.fatal("Unable to read script file " + args[argp] + "\n" + e);
        }
      else if (!args[argp].startsWith("\"") && !args[argp].startsWith("'") && args[argp].contains(".")) {
        StringBuilder tryStringBuilder = new StringBuilder();
        while (argp < args.length && !args[argp].startsWith("\"") && !args[argp].startsWith("'") && args[argp].contains("."))
          try {
            // Util.info("Appending contents of !try string file " + args[argp]);
            tryStringBuilder.append(Files.readString(Paths.get((args[argp]))));
            argp++; // add after append in case an exception gets thrown - we want to report the inaccessible file
          } catch (IOException e) {
            Util.fatal("Unable to read !try string file " + args[argp] + "\n");
          }
        scriptStringBuilder.append("!try \"" + Util.escapeString(tryStringBuilder.toString()) + "\"");
      }

      else
        scriptStringBuilder.append(args[argp]);
      scriptStringBuilder.append("\n");
    }

    // Util.info("Script string builder returns\n" + scriptStringBuilder);
    return scriptStringBuilder.toString();

  }

  /* This is used by GitHub push scripts to increment the version number: accessed via the undocumented mode 'incver' */
  private static void incVersion() {
    try {
      int newBuild = Version.build() + 1;
      String timeStamp = LocalDate.now() + " " + LocalTime.now();
      timeStamp = timeStamp.substring(0, timeStamp.indexOf('.'));

      Util.info("Updating from " + Version.version() + ": new build " + newBuild + " at " + timeStamp);
      PrintWriter pw;
      pw = new PrintWriter("Version.java.new");
      pw.printf(
          "package uk.ac.rhul.cs.csle.art.util;%n" + "public class Version {%n" + "  public static int major() {return %d;}%n"
              + "  public static int minor() {return %d;}%n" + "  public static int build() {return %d;}%n"
              + "  public static String timeStamp() {return \"%s\";}%n"
              + "  public static String version() { return major()+\"_\"+minor()+\"_\"+build() + \" \" + timeStamp(); };%n" + "}%n",
          Version.major(), Version.minor(), newBuild, timeStamp);
      pw.close();

      pw = new PrintWriter("manifest.local.new");
      pw.printf("Specification-Vendor: Center for Software Language Engineering, RHUL%n" + "Specification-Title: ART%n" + "Specification-Version: 5%n"
          + "Implementation-Vendor: Center for Software Language Engineering, RHUL%n" + "Implementation-Title: ART%n" + "Implementation-Version: %d_%d_%d%n"
          + "Implementation-Build-Date: %s%n" + "Main-Class: uk.ac.rhul.cs.csle.art.ART%n", Version.major(), Version.minor(), newBuild, timeStamp);
      pw.close();
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open Version source file");
    }
  }
}
