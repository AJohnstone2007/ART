package uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.osbrd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.grammar.GElement;
import uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.grammar.GKind;
import uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.grammar.GNode;
import uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.grammar.Grammar;

public class OSBRDGenerator {

  public OSBRDGenerator(Grammar grammar, String className) {
    PrintWriter text = null;
    try {
      text = new PrintWriter(new File(className + ".java"));
    } catch (FileNotFoundException e) {
      System.out.println("Unable to open output file " + className + ".java");
      System.exit(1);
    }

    System.out.println(grammar);
    grammar.visualise("grammar.dot");
    text.print(
        "import java.io.IOException;\nimport java.nio.file.Files;\nimport java.nio.file.Paths;\nimport uk.ac.rhul.cs.csle.art.v5.DNode;\nimport uk.ac.rhul.cs.csle.art.v5.grammar.Kind;\n"
            + "import uk.ac.rhul.cs.csle.art.v5.lexer.LexerLM;\n\n" + "class OSBRDG extends uk.ac.rhul.cs.csle.art.v5.osbrd.OSBRDParser {\n"
            + "String[] lexemes = {");

    boolean firstLexeme = true;
    for (GElement s : grammar.elements.keySet()) {
      if (s.kind != GKind.T) continue;
      if (firstLexeme)
        firstLexeme = false;
      else
        text.print(", ");
      text.print("\"" + s.str + "\"/*" + s.ei + "*/");
    }

    text.println("};\n");

    for (GElement s : grammar.rules.keySet()) {
      text.println("boolean p_" + s.str + "() {\n int eI = i; DNode eDN = dn;");
      boolean seenEpsilon = false, firstAlt = true;
      for (GNode alt = grammar.rules.get(s).alt; alt != null; alt = alt.alt) {
        int braceCount = 0;
        if (firstAlt)
          firstAlt = false;
        else
          text.print("\n i = eI; dn = eDN;");
        seqLoop: for (GNode seq = alt.seq;; seq = seq.seq) {
          switch (seq.elm.kind) {
          case T:
            text.print("\n if (input[i]==" + seq.elm.ei + "/*" + seq.elm.str + "*/) {i++;");
            braceCount++;
            break;
          case N:
            text.print("\n if (p_" + seq.elm.str + "()) {");
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
    System.out.println("OSBRD generated parser for grammar '" + grammar.grammarName + "' written to " + className + ".java");
  }
}