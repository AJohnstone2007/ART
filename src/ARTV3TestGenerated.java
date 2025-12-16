import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.old.v4.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.v4.util.text.ARTText;

/******************************************************************************
 * ARTV3TestGenerated.java
 *
 * This is a test harness for V3 ART generated Java parsers
 *
 * (c) Adrian Johnstone 2013-22
 *****************************************************************************/

public class ARTV3TestGenerated {
  public static void main(String[] args) throws FileNotFoundException {
    String inputFilename = null;
    ARTGeneratedParser parser = new ARTGeneratedParser(new ARTGeneratedLexer());

    if (args.length == 0) {
      System.out.println("No arguments supplied\n");
      System.exit(1);
    }

    for (String arg : args)
      if (arg.charAt(0) != '!')
        inputFilename = arg;
      else
        parser.artDirectives.set(arg.substring(1, arg.length()), true);

    if (inputFilename == null) throw new ARTUncheckedException("No input file specified");
    String input = "";

    try {
      input = ARTText.readFile(inputFilename);
      parser.artParse(input);
      System.out.println(parser.artStats());
    } catch (ARTUncheckedException e) {
      System.out.println("Fatal error: " + e.getMessage());
    }
  }
}
