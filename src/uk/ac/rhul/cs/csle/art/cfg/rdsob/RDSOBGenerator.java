package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBGenerator {

  public RDSOBGenerator(CFGRules grammar, String className) {
    PrintWriter text = null;
    try {
      text = new PrintWriter(new File(className + ".java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file " + className + ".java");
    }

    Util.info(grammar.toString());
    text.print(
        "import java.io.IOException;\nimport java.nio.file.Files;\nimport java.nio.file.Paths;\nimport uk.ac.rhul.cs.csle.art.v5.DNode;\nimport uk.ac.rhul.cs.csle.art.v5.grammar.Kind;\n"
            + "import uk.ac.rhul.cs.csle.art.v5.lexer.LexerLM;\n\n" + "class RDSOB extends uk.ac.rhul.cs.csle.art.v5.rdsob.RDSOBParser {\n"
            + "String[] lexemes = {");

    boolean firstLexeme = true;
    for (CFGElement s : grammar.elements.keySet()) {
      if (s.cfgKind != CFGKind.TRM_CS) continue;
      if (firstLexeme)
        firstLexeme = false;
      else
        text.print(", ");
      text.print("\"" + s.str + "\"/*" + s.number + "*/");
    }

    text.println("};\n");

    for (CFGElement s : grammar.elementToNodeMap.keySet()) {
      text.println("boolean p_" + s.str + "() {\n int eI = i; DNode eDN = dn;");
      boolean seenEpsilon = false, firstAlt = true;
      for (CFGNode alt = grammar.elementToNodeMap.get(s).alt; alt != null; alt = alt.alt) {
        int braceCount = 0;
        if (firstAlt)
          firstAlt = false;
        else
          text.print("\n i = eI; dn = eDN;");
        seqLoop: for (CFGNode seq = alt.seq;; seq = seq.seq) {
          switch (seq.cfgElement.cfgKind) {
          case TRM_CS:
            text.print("\n if (input[i]==" + seq.cfgElement.number + "/*" + seq.cfgElement.str + "*/) {i++;");
            braceCount++;
            break;
          case NON:
            text.print("\n if (p_" + seq.cfgElement.str + "()) {");
            braceCount++;
            break;
          case EPS:
            text.print("\n /* epsilon */");
            seenEpsilon = true;
            break;
          case END:
            text.print(" du(" + alt.num + "); return true;");
            for (int i = 0; i < braceCount; i++)
              text.print("}");
            text.println();
            break seqLoop;
          case ALT, TRM_BI, TRM_CH, PAR, EOS, KLN, OPT, POS, TRM_CI:
            Util.fatal("internal error - unexpected grammar node in rdsobFunction");
          }
        }
      }

      if (!seenEpsilon) text.println(" return false;");
      text.printf("}\n\n");
    }

    text.print("public void parse(String string) {\n input = new LexerLM().lex(string, lexemes);\n accepted = p_" + grammar.startNonterminal.str
        + "() && input[i] == 0;\n }\n\n" + "public static void main(String[] args) throws IOException {\n" + " " + className + " parser = new " + className
        + "();\n parser.parse(Files.readString(Paths.get(args[0])));\n"
        + " System.out.println((parser.accepted ? \"Accept\" : \"Reject\")\n    + \" with derivation: \" + parser.dn);\n }\n}");

    text.close();
    Util.info("SOBRD generated parser for grammar '" + grammar.name + "' written to " + className + ".java");
  }
}