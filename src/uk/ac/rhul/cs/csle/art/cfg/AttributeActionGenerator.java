package uk.ac.rhul.cs.csle.art.cfg;

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

public class AttributeActionGenerator {

  private final ITerms iTerms;

  public AttributeActionGenerator(CFGRules cfgRules) {
    iTerms = ScriptTermInterpreter.iTerms;
    System.out.println("Generating Attribute Action system");
    PrintWriter text = null;
    try {
      text = new PrintWriter(new File("ARTAttributeAction.java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file ARTAttributeAction.java");
    }

    text.println("public class ARTAttributeAction {");

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
        printAllActionsDespatchRec(text, cfgRules.elementToNodeMap.get(e));
      }

    text.println("    }\n  }\n}");
    text.close();
  }

  private void printAllActionsDespatchRec(PrintWriter text, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    if (cfgNode.slotTerm != 0 && iTerms.termArity(cfgNode.slotTerm) != 0) text.println("      case " + cfgNode.num + ": action" + cfgNode.num + "(); break;");

    printAllActionsDespatchRec(text, cfgNode.seq);
    printAllActionsDespatchRec(text, cfgNode.alt);
  }

  private void printAllActionsRec(PrintWriter text, CFGElement lhs, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    if (cfgNode.slotTerm != 0 && iTerms.termArity(cfgNode.slotTerm) != 0) {
      text.print("  public static void action" + cfgNode.num + "(");
      boolean first = true;
      for (var n : lhs.rhsNonterminals.keySet())
        for (int index = 1; index < lhs.rhsNonterminals.get(n); index++) {
          if (first) {
            first = false;
          } else
            text.print(", ");
          text.print("A_" + n + " " + n + index);
        }
      text.print("){");
      printSlotTermRec(text, cfgNode.slotTerm);
      text.println("}");
    }
    printAllActionsRec(text, lhs, cfgNode.seq);
    printAllActionsRec(text, lhs, cfgNode.alt);
  }

  private void printSlotTermRec(PrintWriter text, int slotTerm) {
    System.out.println(iTerms.termSymbolString(slotTerm));

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
}
