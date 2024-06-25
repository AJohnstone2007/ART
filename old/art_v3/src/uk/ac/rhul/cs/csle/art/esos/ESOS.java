package uk.ac.rhul.cs.csle.art.esos;

/******************************************************************************
 * ESOS.java
 *
 * This is a test harness for the eSOS tool
 *
 * (c) Adrian Johnstone 2017
 *****************************************************************************/
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDT;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextLevel;

public class ESOS {

  static ARTText text;

  public static void main(String[] args) throws ARTException {
    if (args.length == 0) {
      System.err.printf("Usage - java ESOS <inputfilename>%n");
      System.exit(0);
    }

    String inputFilename = args[0];

    text = new ARTText(new ARTTextHandlerConsole());
    // Uncomment the next line and comment out the following one to see application specific text handling in action
    // GLLParser parser = new GLLParser(text);

    String inputString = "";

    try {
      byte[] encoded = Files.readAllBytes(Paths.get(inputFilename));
      inputString = Charset.forName("ISO-8859-1").newDecoder().decode(ByteBuffer.wrap(encoded)).toString();
    } catch (FileNotFoundException ex) {
      text.printf(ARTTextLevel.FATAL, "unable to open input file %s%n", args[0]);
    } catch (CharacterCodingException e) {
      text.printf(ARTTextLevel.FATAL, "encoding error in input file %s%n", args[0]);
    } catch (IOException e) {
      text.printf(ARTTextLevel.FATAL, "I/O error whilst attempting to read input file %s%n", args[0]);
    }
    ;

    ARTeSOSParser parser = new ARTeSOSParser(new ARTeSOSLexer());

    // text.printf(TextLevel.TRACE, "Input: %s%n", inputString);

    try {
      parser.artParse(inputString);

      if (!parser.artIsInLanguage) throw new ARTException(inputFilename + " has syntax errors: terminating");

      // parser.disambiguateRightmost();
      parser.artDerivationSelectFirst();
      // parser.artRenderSPPF("gss.dot", parser.artRenderKindGSS);
      // parser.artRenderSPPF("sppf.dot", parser.artRenderKindSPPFFull);
      // parser.artRenderSPPF("sppffull.dot", parser.artRenderKindSPPFFull);

      ARTGLLRDT tree = parser.artEvaluator();
      if (tree != null) {
        // tree.printDot("rdt.dot");
      } else {
        System.out.println("artEvaluator returns null tree\n");
      }

      parser.eSOSSpecification.interpret();

      System.out.println(parser.artGetTimes());
    } catch (ARTException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
}
