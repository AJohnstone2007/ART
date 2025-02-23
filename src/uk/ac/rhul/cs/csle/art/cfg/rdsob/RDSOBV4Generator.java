package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBV4Generator {
  ITerms iTerms;
  PrintWriter text = null;

  public RDSOBV4Generator(CFGRules cfgRules) {
    String filename = "ARTGeneratedRDSOB";
    iTerms = ScriptTermInterpreter.iTerms;
    String timeStamp = Util.timestamp();
    System.out.println("Writing new " + filename + ": " + timeStamp);
    try {
      text = new PrintWriter(new File(filename + ".java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file " + filename);
    }

    text.println("import java.io.FileNotFoundException;\n\nclass ARTGeneratedRDSOB extends uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBV4Base {\n");

    // Write parse functions
    for (var e : cfgRules.elements.keySet())
      if (e.kind == CFGKind.N) {
        text.printf("boolean parse_%s() {\n  int rc = cc, ro = co;\n", e.str);
        int pCount = 0;
        boolean seenEpsilon = false;

        for (var alt = cfgRules.elementToNodeMap.get(e).alt; alt != null; alt = alt.alt) {
          pCount++;
          text.printf("\n  /* Nonterminal %s, alternate %d */\n  cc = rc; co = ro; oracleSet(%d);", e.str, pCount, pCount);

          int braceCount = 0;
          for (var i = alt.seq; i.elm.kind != CFGKind.END; i = i.seq) {
            switch (i.elm.kind) {
            case N:
              text.printf("\n  if (parse_%s()) {", i.elm.str);
              braceCount++;
              break;
            case T:
              text.printf("\n  if (match(\"%s\")) {", i.elm.str);
              braceCount++;
              break;
            case EPS:
              text.printf("\n  /* epsilon */ ");
              seenEpsilon = true;
              break;
            default:
              Util.fatal("Unexpected CFGKind in RDSOBV4Generator parse function " + i.elm.kind);
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
        text.printf(" class Attribute_%s {", e.str);
        boolean first = true;
        for (var a : e.attributes.keySet()) // attributes
          text.print(" " + e.attributes.get(a) + " " + a + ";");

        text.printf(" }\n\n");

        text.printf(" void semantics_%s(Attribute_%s %s) {\n switch(oracle[co++]) {", e.str, e.str, e.str);

        int pCount = 0;
        for (var alt = cfgRules.elementToNodeMap.get(e).alt; alt != null; alt = alt.alt) {
          pCount++;
          text.printf("\n case %d: {", pCount);

          for (var i = alt.seq; i.elm.kind != CFGKind.END; i = i.seq)
            if (i.elm.kind == CFGKind.N) text.printf("\n Attribute_%s %s%d = new Attribute_%s();", i.elm.str, i.elm.str, 1/* i.instanceNumber */, i.elm.str);

          CFGNode seq = alt;
          if (seq.slotTerm != 0) text.print("\n" + seq.toStringActions()); // Print initial action
          do {
            seq = seq.seq;
            switch (seq.elm.kind) {
            case N:
              text.printf("\n // Instance %s", seq.instanceNumber);

              text.printf("\n semantics_%s(%s%d);", seq.elm.str, seq.elm.str, seq.instanceNumber);
              break;
            case T:
              text.printf("\n match(\"%s\");", seq.elm.str);
              break;
            case EPS:
              text.printf("\n /* epsilon */");
              break;
            case END: // nothing to do: just here to ensure that the final action is printed
              break;
            default:
              Util.fatal("Unexpected CFGKind in RDSOBV4Generator semantics function " + seq.elm.kind);
            }
            if (seq.slotTerm != 0) text.print("\n" + seq.toStringActions()); // Print any actions in this node
          } while (seq.elm.kind != CFGKind.END);
          text.print("\n break; }\n");
        }
        text.printf("\n }\n }\n\n");
      }

    // // Write tree functions
    // for (ARTGrammarElementNonterminal n : artGrammar.getNonterminals()) {
    // text.printf(" TreeNode tree_%s() {\n TreeNode leftNode = null, rightNode = null;\n" + " switch(oracle[co++]) {", n.toString());
    // int pCount = 0;
    // for (ARTGrammarInstanceCat p : n.getProductions()) {
    // pCount++;
    // text.printf("\n case %d:", pCount);
    //
    // boolean first = true;
    // for (ARTGrammarInstance i = p.getChild(); i != null; i = i.getSibling()) {
    // if (i instanceof ARTGrammarInstanceSlot) continue;
    //
    // text.print("\n ");
    //
    // if (first) {
    // text.print("leftNode = ");
    // first = false;
    // }
    //
    // if (i instanceof ARTGrammarInstanceNonterminal)
    // text.printf("rightNode = new TreeNode(\"%s\", tree_%s(), rightNode, TreeKind.NONTERMINAL, GIFTKind.%s);", i.getPayload(), i.getPayload(),
    // foldKind(i));
    // else if (i instanceof ARTGrammarInstanceTerminal)
    // text.printf("rightNode = new TreeNode(\"%s\", null, rightNode, TreeKind.TERMINAL, GIFTKind.%s); match(\"%s\");",
    // text.toLiteralString(((ARTGrammarElementTerminal) i.getPayload()).getId().toString()), foldKind(i),
    // text.toLiteralString(((ARTGrammarElementTerminal) i.getPayload()).getId().toString()));
    // else if (i instanceof ARTGrammarInstanceEpsilon)
    // text.printf("rightNode = new TreeNode(\"#\", null, rightNode, TreeKind.EPSILON, GIFTKind.%s);", foldKind(i));
    // else
    // text.print("!!! Unknown Instance tree node !!!");
    // }
    // text.print("\n break;\n");
    //
    // }
    // text.printf("\n }\n return leftNode;\n" + "}\n\n");
    // }

    //@formatter:off
    text.printf("void parse(String filename) throws FileNotFoundException {\n" +
        "  input = readInput(filename);\n" +
        "\n" +
        "  System.out.printf(\"Input: '%%s'\\n\", input);\n" +
        "  cc = co = 0; builtIn_WHITESPACE();\n" +
        "  if (!(parse_%s() && input.charAt(cc) == '\\0')) {System.out.print(\"Rejected\\n\"); return; }\n" +
        "\n" +
        "  System.out.print(\"Accepted\\n\");\n" +
        "  System.out.print(\"Oracle:\"); for (int i = 0; i < co; i++) System.out.printf(\" %%d\", oracle[i]); System.out.printf(\"\\n\");\n" +
        "  System.out.print(\"\\nSemantics phase\\n\"); cc = 0; co = 0; builtIn_WHITESPACE(); Attribute_%s %s = new Attribute_%s(); semantics_%s(%s);\n" +
//        "  System.out.print(\"\\nTree construction phase\\n\"); cc = 0; co = 0; builtIn_WHITESPACE();\n" +
//        "  TreeNode dt = new TreeNode(\"%s\", tree_%s(), null, TreeKind.NONTERMINAL, GIFTKind.NONE);\n" +
//        "  dt.dot(\"dt.dot\");" +
//        "  System.out.print(\"\\nDerivation term\\n\"); dt.printTerm(0);\n" +
//        "  System.out.print(\"\\n\\nDerivation tree\\n\"); dt.printTree(0);\n" +
//        "  TreeNode cloneRoot = dt.clone(null, null);\n" +
//        "    cloneRoot.dot(\"clone.dot\");\n" +
//        "\n" +
//        "    // System.out.print(\"\\nCloned derivation tree\\n\");\n" +
//        "    // cloneRoot.printTree(0);\n" +
//        "    TreeNode rdtEpsilon = dt.evaluateTIF(null, null, true);\n" +
//        "    rdtEpsilon.dot(\"rdtEpsilon.dot\");\n" +
//        "\n" +
//        "    //System.out.print(\"\\nRDTEpsilon fold tree\\n\");\n" +
//        "    //rdtEpsilon.printTree(0);\n" +
//        "    rdtEpsilon.foldunderEpsilon();\n" +
//        "    rdtEpsilon.dot(\"rdtEpsilonFold.dot\");\n" +
//        "\n" +
//        "    //System.out.print(\"\\nAnnotated RDTEpsilon tree\\n\");\n" +
//        "    //rdtEpsilon.printTree(0);\n" +
//        "    rdt = rdtEpsilon.evaluateTIF(null, null, true);\n" +
//        "    rdt.dot(\"rdt.dot\");\n" +
//        "\n" +
//        "    System.out.print(\"\\nRewritten Derivation term\\n\"); rdt.printTerm(0);\n" +
//        "    System.out.print(\"\\n\\nRewritten Derivation Tree\\n\");\n" +
//        "    rdt.printTree(0);\n" +
//        "    postParse(rdt);\n" +
//        "\n" +
//        "" +
        "}\n" +
        "\n" +
        "public static void main(String[] args) throws FileNotFoundException{\n" +
        "    if (args.length < 1) {\n" +
        "      System.err.println(\"No input file name supplied\");\n" +
        "      System.exit(1);\n" +
        "    } else\n"+
        "      new ARTGeneratedRDSOB().parse(args[0]);\n" +
        "  }\n", cfgRules.startNonterminal.str,cfgRules.startNonterminal.str,cfgRules.startNonterminal.str,cfgRules.startNonterminal.str,cfgRules.startNonterminal.str,cfgRules.startNonterminal.str,cfgRules.startNonterminal.str,cfgRules.startNonterminal.str);
    text.print("}\n");
    //@formatter:on

    text.close();
  }
}