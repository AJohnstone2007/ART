package uk.ac.rhul.cs.csle.art.cava;

import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerConsole;

public class Cava {

  static ARTText text = new ARTText(new ARTTextHandlerConsole());

  public static void main(String[] args) throws ARTException {
    ARTCavaParser parser = new ARTCavaParser(new ARTCavaLexer());

    try {
      parser.artParse(ARTText.readFile("test.cava"));
    } catch (ARTException e) {
      ARTText.printFatal("syntax - " + e.getMessage());
    }

    parser.artDisambiguateRightmost();
    parser.artDerivationSelectFirst();
    try {
      parser.artEvaluator();
    } catch (ARTException e) {
      ARTText.printFatal("sementics - " + e.getMessage());
    }
  }
}
