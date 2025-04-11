package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBOracleGenerator {
  ITerms iTerms;
  PrintWriter text = null;

  private final Set<String> printedInits = new HashSet<>();

  private void printAllInitsRec(CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.element.kind == CFGKind.END) return;

    if (cfgNode.element.kind == CFGKind.N && cfgNode.element.attributes.keySet().size() > 0) {
      String name = cfgNode.element.str + cfgNode.instanceNumber;
      if (!printedInits.contains(name)) text.println("  Attributes_" + cfgNode.element.str + " " + name + " = new Attributes_" + cfgNode.element.str + "(); ");
      printedInits.add(name);
    }
    printAllInitsRec(cfgNode.seq);
    printAllInitsRec(cfgNode.alt);
  }

  private void printActions(CFGNode node) {
    String actions = node.toStringActions();
    if (!actions.equals("")) text.println("   " + node.toStringActions()); // Print initial action
  }

  public RDSOBOracleGenerator(CFGRules cfgRules) {
    if (cfgRules.startNonterminal == null) Util.fatal("RDSOBOracle found no CFG rules");
    String filename = "ARTGeneratedRDSOBOracle";
    iTerms = ScriptTermInterpreter.iTerms;
    String timeStamp = Util.timestamp();
    Util.info("Writing new " + filename + ": " + timeStamp);
    try {
      text = new PrintWriter(new File(filename + ".java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file " + filename);
    }

    text.println("import java.io.FileNotFoundException;import java.util.LinkedList;\n\nclass " + filename
        + " extends uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBAbstract {\n");

    // Write parse functions
    for (var e : cfgRules.elements.keySet())
      if (e.kind == CFGKind.N) {
        text.printf("boolean parse_%s() {\n  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;\n", e.str);
        int pCount = 0;
        boolean seenEpsilon = false;

        for (var alt = cfgRules.elementToNodeMap.get(e).alt; alt != null; alt = alt.alt) {
          pCount++;
          text.printf("\n  /* Nonterminal %s, alternate %d */\n  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(%d);", e.str, pCount, pCount);

          int braceCount = 0;
          for (var seq = alt.seq; seq.element.kind != CFGKind.END; seq = seq.seq) {
            switch (seq.element.kind) {
            case N:
              text.printf("\n  if (parse_%s()) {", seq.element.str);
              braceCount++;
              break;
            case T:
              text.printf("\n  if (match(\"%s\")) {", seq.element.str);
              braceCount++;
              break;
            case B:
              text.printf("\n  if (builtIn_%s()) {", seq.element.str);
              braceCount++;
              break;
            case EPS:
              text.printf("\n  /* epsilon */ ");
              seenEpsilon = true;
              break;
            default:
              Util.fatal("Unexpected CFGKind in RDSOBV4Generator parse function " + seq.element.kind);
            }
          }
          text.printf(" return true; ");

          for (int bc = 0; bc < braceCount; bc++)
            text.print("}");
          text.println();

        }
        if (!seenEpsilon) text.printf("\n  return false;\n");
        text.printf("}\n\n");

      }
    // Write semantics functions
    for (var e : cfgRules.elements.keySet())
      if (e.kind == CFGKind.N) {
        if (e.attributes.keySet().size() > 0) {
          text.printf("class Attributes_%s {", e.str);
          boolean first = true;
          for (var a : e.attributes.keySet()) // attributes
            text.print(" " + e.attributes.get(a) + " " + a + ";");
          text.println(" }\n");
        }

        text.println("void semantics_" + e.str + "(" + (e.attributes.keySet().size() > 0 ? "Attributes_" + e.str + " " + e.str : "") + ") {");
        // Create attribute blocks for this rule
        CFGNode lhsRoot = cfgRules.elementToNodeMap.get(e).alt;
        printedInits.clear();
        printAllInitsRec(lhsRoot);
        text.print("  switch(oracle[oracleIndex++]) {");
        int pCount = 0;
        for (var alt = lhsRoot; alt != null; alt = alt.alt) {
          pCount++;
          text.println("\n    case " + pCount + ":");

          CFGNode seq = alt;
          printActions(seq); // print initial actions which are held on the alt node
          do {
            seq = seq.seq;
            switch (seq.element.kind) {
            case N:
              if (seq.element.attributes.keySet().size() > 0)
                text.printf("    semantics_%s(%s%d);\n", seq.element.str, seq.element.str, seq.instanceNumber);
              else
                text.printf("    semantics_%s();\n", seq.element.str);
              break;
            case T:
              text.printf("    match(\"%s\");\n", seq.element.str);
              break;
            case B:
              text.printf("    builtIn_%s();\n", seq.element.str);
              break;
            case EPS:
              text.printf("    /* epsilon */\n");
              break;
            case END: // nothing to do: just here to ensure that the final action is printed
              break;
            default:
              Util.fatal("Unexpected CFGKind in RDSOBV4Generator semantics function " + seq.element.kind);
            }
            printActions(seq);
          } while (seq.element.kind != CFGKind.END);
          text.println("    break;");
        }
        text.printf("  }\n }\n\n");
      }

    // // Write term functions
    // for (var e : cfgRules.elements.keySet()) {
    // if (e.kind == CFGKind.N) {
    // text.println("String term_" + e.str + "(LinkedList<String> children) {\n String newRoot = null;");
    // // Create attribute blocks for this rule
    // CFGNode lhsRoot = cfgRules.elementToNodeMap.get(e).alt;
    // printedInits.clear();
    // printAllInitsRec(lhsRoot);
    // text.print(" switch(oracle[oracleIndex++]) {");
    // int pCount = 0;
    // for (var alt = lhsRoot; alt != null; alt = alt.alt) {
    // pCount++;
    // text.println("\n case " + pCount + ":");
    //
    // CFGNode seq = alt;
    // do {
    // seq = seq.seq;
    // switch (seq.elm.kind) {
    // case N:
    // text.printf(" term_%s(new LinkedList<>());\n", seq.elm.str, seq.elm.str, seq.instanceNumber);
    // break;
    // case T:
    // text.printf(" match(\"%s\");\n", seq.elm.str);
    // break;
    // case B:
    // text.printf(" builtIn_%s();\n", seq.elm.str);
    // break;
    // case EPS:
    // text.printf(" /* epsilon */\n");
    // break;
    // case END: // nothing to do: just here to ensure that the final action is printed
    // break;
    // default:
    // Util.fatal("Unexpected CFGKind in RDSOBV4Generator semantics function " + seq.elm.kind);
    // }
    // // switch (seq.elm)
    // } while (seq.elm.kind != CFGKind.END);
    // text.println(" break;");
    // }
    // text.printf(" }\n return newRoot;\n }\n\n");
    // }
    // }

    //@formatter:off
    text.println("void parse(String filename) throws FileNotFoundException {\n" +
        "  input = readInput(filename);\n\n" +
        "  System.out.println(\"Input: \" + input);" +
        "  inputIndex = oracleIndex = 0; builtIn_WHITESPACE();\n" +
        "  if (!(parse_" + cfgRules.startNonterminal.str + "() && input.charAt(inputIndex) == '\\0')) { System.out.print(\"Rejected\\n\"); return; }\n" +
        "\n" +
        "  System.out.print(\"Accepted\\n\");\n" +
        "  System.out.print(\"Oracle:\"); for (int i = 0; i < oracleIndex; i++) System.out.println(\" \" + oracle[i]); System.out.println();\n" +
        "  System.out.print(\"Semantics:\\n\"); inputIndex = oracleIndex = 0; builtIn_WHITESPACE(); semantics_" +
           cfgRules.startNonterminal.str+"("+(cfgRules.startNonterminal.attributes.keySet().size() > 0 ? "new Attributes_" + cfgRules.startNonterminal.str + "()":"")+");\n"+
//        "  System.out.print(\"\\nTerm:\\n\"); inputIndex = oracleIndex = 0; builtIn_WHITESPACE(); term_" + cfgRules.startNonterminal.str+"(new LinkedList<>());\n"+
        "}\n\n" +
        "public static void main(String[] args) throws FileNotFoundException{\n" +
        "    if (args.length < 1) System.out.println(\"Usage: java " + filename + " <input file name>\");\n"
        + "    else  new ARTGeneratedRDSOBOracle().parse(args[0]);\n"
        + "\n  }\n}");
    //@formatter:on

    text.close();
  }

}