package uk.ac.rhul.cs.csle.art.old.v4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.old.v4.core.ARTV4;
import uk.ac.rhul.cs.csle.art.old.v4.core.Version;

public class ARTV4underV5 {
  // public static void main(String[] args) {
  // new ARTV4underV5(args);
  // }

  public ARTV4underV5(String[] args) {
    StringBuilder sb = new StringBuilder();
    if (args.length == 0) {
      System.err.println("ART " + Version.version() + ": no arguments supplied");
      System.exit(1);
    }

    int restOfLine = 0;
    if (args[0].charAt(0) != '!') {
      try {
        sb.append(Files.readString(Paths.get(args[0])));
      } catch (IOException e) {
        System.err.println("ART: unable to read file " + args[0]);
        System.exit(1);
      }
      restOfLine = 1;
      if (args.length > 1 && args[1].charAt(0) != '!') restOfLine = 2;
    }

    for (int i = restOfLine; i < args.length; i++) // Catenate the rest of the arguments
      sb.append(args[i] + " ");

    if (args.length > 1 && args[1].charAt(0) != '!') sb.append("!try \"" + args[1] + "\"\n");

    new ARTV4(sb.toString());
  }
}
