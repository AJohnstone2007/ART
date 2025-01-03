package uk.ac.rhul.cs.csle.art.interpret;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ActionsGenerator {

  private final ITerms iTerms;

  public ActionsGenerator(CFGRules cfgRules, String filePrelude, String classPrelude) {
    iTerms = ScriptTermInterpreter.iTerms;
    System.out.println("Generating ARTActions.java");
    PrintWriter text = null;
    try {
      text = new PrintWriter(new File("ARTActions.java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file ARTActions.java");
    }
    text.println("import uk.ac.rhul.cs.csle.art.interpret.Actions;");
    if (filePrelude != null) text.println(filePrelude);
    text.println("public class ARTActions extends Actions {");
    if (classPrelude != null) text.println(classPrelude);
    for (var e : cfgRules.elements.keySet())
      if (e.kind == CFGKind.N) {
        text.print("  public static class A_" + e.str + " {");
        for (var a : e.attributes.keySet())
          text.print(" " + e.attributes.get(a) + " " + a + ";");
        text.println(" }");

        printAllActionsRec(text, e, cfgRules.elementToNodeMap.get(e));

        printTermActionsRec(text, cfgRules.elementToNodeMap.get(e));
      }

    text.println("  public void action(int nodeNumber, Object[] aBlocks) {\n    switch(nodeNumber) {");

    for (var e : cfgRules.elements.keySet())
      if (e.kind == CFGKind.N) {
        printAllActionsDespatchRec(text, cfgRules.elementToNodeMap.get(e), e);
      }

    text.println("      default: break;\n    }\n  }\n}");
    text.close();
  }

  private void printAllActionsRec(PrintWriter text, CFGElement lhs, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    if (cfgNode.slotTerm != 0 && iTerms.termArity(cfgNode.slotTerm) != 0) {
      text.print("  public static void action" + cfgNode.num + "(");

      text.print("A_" + lhs + " " + lhs);

      for (var n : lhs.rhsNonterminals.keySet())
        for (int index = 0; index < lhs.rhsNonterminals.get(n); index++) {
          text.print(", A_" + n + " " + n + (index + 1));
        }
      text.print("){");
      printSlotTermRec(text, cfgNode.slotTerm);
      text.println("}");
    }
    printAllActionsRec(text, lhs, cfgNode.seq);
    printAllActionsRec(text, lhs, cfgNode.alt);
  }

  private void printSlotTermRec(PrintWriter text, int slotTerm) {
    // System.out.println(iTerms.termSymbolString(slotTerm));

    if (iTerms.hasSymbol(slotTerm, "cfgNative")) {
      text.print(iTerms.toString(iTerms.subterm(slotTerm, 0)));
      return;
    }
    for (int i = 0; i < iTerms.termArity(slotTerm); i++)
      printSlotTermRec(text, iTerms.termChildren(slotTerm)[i]);
  }

  private void printTermActionsRec(PrintWriter text, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    // printSlotTerm(text, cfgNode.slotTerm);
    printTermActionsRec(text, cfgNode.seq);
    printTermActionsRec(text, cfgNode.alt);
  }

  private void printAllActionsDespatchRec(PrintWriter text, CFGNode cfgNode, CFGElement lhs) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    if (cfgNode.slotTerm != 0 && iTerms.termArity(cfgNode.slotTerm) != 0) {
      text.print("      case " + cfgNode.num + ": action" + cfgNode.num + "(");
      text.print("(A_" + lhs + ") aBlocks[0]");

      int blockCount = 1;
      for (var n : lhs.rhsNonterminals.keySet())
        for (int index = 0; index < lhs.rhsNonterminals.get(n); index++) {
          text.print(", (A_" + n + ") aBlocks[" + (blockCount++) + "]");
        }
      text.println("); break;");
    }
    printAllActionsDespatchRec(text, cfgNode.seq, lhs);
    printAllActionsDespatchRec(text, cfgNode.alt, lhs);
  }
}
