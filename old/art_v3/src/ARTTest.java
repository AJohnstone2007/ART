
/******************************************************************************
 * ARTTest.java
 *
 * This is a test harness for ART generated Java parsers
 *
 * (c) Adrian Johnstone 2013-17
 *****************************************************************************/
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDT;
import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerConsole;

public class ARTTest {
  static ARTText text = new ARTText(new ARTTextHandlerConsole());

  static String getStringArgument(String arg, String argumentName) {
    return arg.substring(argumentName.length() + 2, arg.length() - 1);
  }

  public static void main(String[] args) throws ARTException, FileNotFoundException {
    String inputFilename = null;
    ARTGeneratedParser parser = new ARTGeneratedParser(new ARTGeneratedLexer());

    boolean phaseAG = false, showAll = false, postUseTerminals = false, postLongestWithin = false, postLongestAcross = false, postPriority = false;

    if (args.length == 0) ARTText.printFatal("Usage - java ARTTest argument1 argument2...\n");
    for (String arg : args)
      if (arg.charAt(0) == '+') {
        if (arg.startsWith("+statistics"))
          parser.ARTSTATISTICS = true;
        else if (arg.startsWith("+postUseTerminals"))
          postUseTerminals = true;
        else if (arg.startsWith("+postLongestWithin"))
          postLongestWithin = true;
        else if (arg.startsWith("+postLongestAcross"))
          postLongestAcross = true;
        else if (arg.startsWith("+postPriority"))
          postPriority = true;
        else if (arg.startsWith("+trace"))
          parser.ARTTRACE = true;
        else if (arg.startsWith("+showAll"))
          showAll = true;
        else if (arg.startsWith("+phaseAG"))
          phaseAG = true;
        else if (arg.startsWith("+inputFile:"))
          inputFilename = getStringArgument(arg, "inputFile:");
        else if (arg.startsWith("+inputFileIfExists:")) {
          if (Files.isRegularFile(Paths.get(getStringArgument(arg, "inputFileIfExists:"))) && inputFilename == null)
            inputFilename = getStringArgument(arg, "inputFileIfExists:");
        } else if (arg.startsWith("+gll") || arg.startsWith("+gllGeneratorPool") || arg.startsWith("+gllClusteredGeneratorPool") || arg.startsWith("+mgll")
            || arg.startsWith("+mgllGeneratorPool") || arg.startsWith("+cnpGeneratorPool") || arg.startsWith("+FIFODescriptors")
            || arg.startsWith("+predictivePops"))
          ; // absorb unused options quietly
        else
          ARTText.printFatal("Unknown command line argument " + arg + "\n");

      } else
        inputFilename = arg;

    if (inputFilename == null) ARTText.printFatal("No input file specified");

    try {
      parser.artParse(ARTText.readFile(inputFilename));
    } catch (ARTException e) {
      text.println(e.getMessage());
    }

    if (parser.artInadmissable) {
      text.println("** Grammar inadmissable");
      parser.artLog(inputFilename, false);
      return;
    }
    if (parser.artIsInLanguage)
      text.println("** Accept");
    else {
      text.println("** Reject");
    }

    if (parser.tweSet != null) parser.tweSet.printTWESet();

    if (parser.ARTSTATISTICS) {
      parser.artcomputeStatistics();
      text.println("Generated parser statistics\n");
      parser.artLog(inputFilename, true);
      parser.artLog(inputFilename, false);
    }

    // ARTLexerTWESet tweSet = parser.artDisambiguateWithinSPPF(postUseTerminals, postLongestWithin, postLongestAcross, postPriority);
    // parser.artDisambiguateRightmost();
    parser.artDerivationSelectFirst();

    if (showAll) {
      parser.artRenderSPPF("dtCore.dot", parser.artRenderKindDerivation);
      parser.artRenderSPPF("dtFull.dot", parser.artRenderKindDerivationFull);
      parser.artRenderSPPF("gss.dot", parser.artRenderKindGSS);
      // parser.artRenderSPPF("sppfCore.dot", parser.artRenderKindSPPFFull, tweSet);
      parser.artRenderSPPF("sppfFull.dot", parser.artRenderKindSPPFFull);
    }

    if (phaseAG) {
      ARTGLLRDT tree = parser.artEvaluator();
      if (tree != null) {
        tree.printDot("rdtEvaluator_GLL.dot");

        printTreeAsTerm(tree);
        text.print("\n");
        visitTree(tree);
      }
    }
  }

  static void visitTree(ARTGLLRDT tree) {
    visitTree(tree, tree.getRoot(), 0);
  }

  static void visitTree(ARTGLLRDT tree, ARTGLLRDTVertex vertex, int level) {
    if (vertex == null) return;
    // Preorder printout
    for (int i = 0; i < level; i++)
      text.print("  ");
    text.printf("%d: %s%n", vertex.getKey(), vertex.getPayload().toString(tree));
    visitTree(tree, vertex.getChild(), level + 1);
    visitTree(tree, vertex.getSibling(), level);
  }

  static void printTreeAsTerm(ARTGLLRDT tree) {
    printTreeAsTerm(tree, tree.getRoot());
  }

  static void printTreeAsTerm(ARTGLLRDT tree, ARTGLLRDTVertex vertex) {
    if (vertex == null) return;
    // Preorder printout
    text.print(vertex.getPayload().toString(tree));
    if (vertex.getChild() != null) {
      text.print("(");
      printTreeAsTerm(tree, vertex.getChild());
      text.print(")");
    }
    if (vertex.getSibling() != null) {
      text.print(", ");
      printTreeAsTerm(tree, vertex.getSibling());
    }
  }
}
