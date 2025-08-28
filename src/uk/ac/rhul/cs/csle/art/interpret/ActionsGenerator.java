package uk.ac.rhul.cs.csle.art.interpret;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ActionsGenerator {

  public ActionsGenerator(CFGRules cfgRules, String filePrelude, String classPrelude) {
    String filename = "ARTGeneratedActions";
    String timeStamp = Util.timestamp();
    Util.info("Writing new " + filename + ": " + timeStamp);
    PrintWriter text = null;
    try {
      text = new PrintWriter(new File(filename + ".java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file " + filename);
    }
    text.println("import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;");
    text.println("import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;");
    text.println("import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;");

    text.println("import uk.ac.rhul.cs.csle.art.util.Util;");
    if (filePrelude != null) text.println(filePrelude);
    text.println("public class " + filename + " extends AbstractActions {");
    if (classPrelude != null) text.println(classPrelude);
    text.println("  public String name() { return \"" + timeStamp + "\"; }");

    for (var e : cfgRules.elements.keySet())
      if (e.cfgKind == CFGKind.NONTERMINAL) {
        text.println("\n  public class ART_C_" + e.str + " extends AbstractAttributeBlock {");
        text.print("    ART_C_" + e.str + " " + e.str + " = this;"); // LHS

        for (var a : e.attributes.keySet()) // attributes
          text.print(" " + e.attributes.get(a) + " " + a + ";");

        for (var r : e.rhsNonterminalsAcrossAllProductions.keySet()) // RHS instances
          for (int n = 0; n < e.rhsNonterminalsAcrossAllProductions.get(r); n++)
            text.print(" ART_C_" + r + " " + r + (n + 1) + ";");

        // INIT
        text.println("\n\n    public void initRHSAttributeBlock(int nodeNumber, int term) {");
        text.println("      switch(nodeNumber){");
        printAllInitsRec(text, cfgRules.elementToRulesNodeMap.get(e).alt);
        text.println("      }\n    }");

        // GET
        text.println("\n    public AbstractAttributeBlock getAttributes(int nodeNumber) {\n      switch(nodeNumber){");
        printAllGetsRec(text, cfgRules.elementToRulesNodeMap.get(e).alt);
        text.println(
            "      default: Util.fatal(\"getAttributes: unknown node \" + nodeNumber + \". Probable out-of-date ARTGeneratedActions - regenerate and recompile\"); return null;\n      }\n    }");

        // ACTION
        text.println("\n    public void action(int nodeNumber) {\n      switch(nodeNumber){");
        printAllActionsRec(text, e, cfgRules.elementToRulesNodeMap.get(e));
        printTermActionsRec(text, cfgRules.elementToRulesNodeMap.get(e));
        text.println("      }\n    }\n  }");
      }

    text.println(
        "\n  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {\n    this.interpreter = interpreter;\n    var ret = new ART_C_"
            + cfgRules.startNonterminal.str + "();\n    ret.term = term;\n    return ret;\n  }");
    text.println("}");
    text.close();
  }

  private void printAllInitsRec(PrintWriter text, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.cfgElement.cfgKind == CFGKind.END) return;
    if (cfgNode.cfgElement.cfgKind == CFGKind.NONTERMINAL) text.println("      case " + cfgNode.num + ": " + cfgNode.cfgElement.str + cfgNode.instanceNumber
        + " = new ART_C_" + cfgNode.cfgElement.str + "(); " + cfgNode.cfgElement.str + cfgNode.instanceNumber + ".term = term; break;");
    printAllInitsRec(text, cfgNode.seq);
    printAllInitsRec(text, cfgNode.alt);
  }

  private void printAllGetsRec(PrintWriter text, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.cfgElement.cfgKind == CFGKind.END) return;
    if (cfgNode.cfgElement.cfgKind == CFGKind.NONTERMINAL)
      text.println("      case " + cfgNode.num + ": return " + cfgNode.cfgElement.str + cfgNode.instanceNumber + ";");
    printAllGetsRec(text, cfgNode.seq);
    printAllGetsRec(text, cfgNode.alt);
  }

  private void printAllActionsRec(PrintWriter text, CFGElement lhs, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.cfgElement.cfgKind == CFGKind.END) return;
    if (cfgNode.actionAsTerm != 0 && ScriptInterpreter.iTerms.termArity(cfgNode.actionAsTerm) != 0) {
      text.print("      case " + cfgNode.num + ":");
      printSlotTermRec(text, cfgNode.actionAsTerm);
      text.println(" break;");
    }
    printAllActionsRec(text, lhs, cfgNode.seq);
    printAllActionsRec(text, lhs, cfgNode.alt);
  }

  private void printSlotTermRec(PrintWriter text, int slotTerm) {
    // Util.info(ScriptTermInterpreter.iTerms.termSymbolString(slotTerm));

    if (ScriptInterpreter.iTerms.hasSymbol(slotTerm, "cfgNative")) {
      text.print(ScriptInterpreter.iTerms.toString(ScriptInterpreter.iTerms.subterm(slotTerm, 0)));
      return;
    }
    for (int i = 0; i < ScriptInterpreter.iTerms.termArity(slotTerm); i++)
      printSlotTermRec(text, ScriptInterpreter.iTerms.termChildren(slotTerm)[i]);
  }

  private void printTermActionsRec(PrintWriter text, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.cfgElement.cfgKind == CFGKind.END) return;
    // printSlotTerm(text, cfgNode.slotTerm);
    printTermActionsRec(text, cfgNode.seq);
    printTermActionsRec(text, cfgNode.alt);
  }
}
