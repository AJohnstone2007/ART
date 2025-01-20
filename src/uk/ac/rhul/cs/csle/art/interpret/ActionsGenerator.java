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
    String filename = "ARTGeneratedActions";
    iTerms = ScriptTermInterpreter.iTerms;
    String timeStamp = Util.timestamp();
    System.out.println("Writing new " + filename + ": " + timeStamp);
    PrintWriter text = null;
    try {
      text = new PrintWriter(new File(filename + ".java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file " + filename);
    }
    text.println("import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;");
    text.println("import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;");
    text.println("import uk.ac.rhul.cs.csle.art.interpret.AbstractActionsNonterminal;");

    text.println("import uk.ac.rhul.cs.csle.art.util.Util;");
    if (filePrelude != null) text.println(filePrelude);
    text.println("public class " + filename + " extends AbstractActions {");
    if (classPrelude != null) text.println(classPrelude);
    text.println("  public String name() { return \"" + timeStamp + "\"; }");

    for (var e : cfgRules.elements.keySet())
      if (e.kind == CFGKind.N) {
        text.println("\n  public class ART_C_" + e.str + " extends AbstractActionsNonterminal {");
        text.print("    int term; ART_C_" + e.str + " " + e.str + " = this;"); // LHS

        for (var a : e.attributes.keySet()) // attributes
          text.print(" " + e.attributes.get(a) + " " + a + ";");

        for (var r : e.rhsNonterminalsAcrossAllProductions.keySet()) // RHS instances
          for (int n = 0; n < e.rhsNonterminalsAcrossAllProductions.get(r); n++)
            text.print(" ART_C_" + r + " " + r + (n + 1) + ";");

        // INIT
        text.println("\n    public void initAttributes(int nodeNumber, int term) { this.term = term; ");
        text.println("      switch(nodeNumber){");
        printAllInitsRec(text, cfgRules.elementToNodeMap.get(e).alt);
        text.print("      }\n    }");

        // GET
        text.println("\n    public AbstractActionsNonterminal getAttributes(int nodeNumber) { switch(nodeNumber){");
        printAllGetsRec(text, cfgRules.elementToNodeMap.get(e).alt);
        text.println("      default: Util.fatal(\"getAttributes: unknown node \" + nodeNumber); return null;\n    }}");

        // ACTION
        text.println("\n    public void action(int nodeNumber) { switch(nodeNumber){");
        printAllActionsRec(text, e, cfgRules.elementToNodeMap.get(e));
        printTermActionsRec(text, cfgRules.elementToNodeMap.get(e));
        text.println("    }}\n  }");
      }

    text.println("public AbstractActionsNonterminal init(AbstractInterpreter interpreter, int term) { this.interpreter = interpreter; var ret = new ART_C_"
        + cfgRules.startNonterminal.str + "(); ret.activate(null); return ret; }");
    text.println("}");
    text.close();
  }

  private void printAllInitsRec(PrintWriter text, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    if (cfgNode.elm.kind == CFGKind.N) text.println("      case " + cfgNode.num + ": ");
    printAllInitsRec(text, cfgNode.seq);
    printAllInitsRec(text, cfgNode.alt);
  }

  private void printAllGetsRec(PrintWriter text, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    if (cfgNode.elm.kind == CFGKind.N) text.println("      case " + cfgNode.num + ": return " + cfgNode.elm.str + cfgNode.instanceNumber + ";");
    printAllGetsRec(text, cfgNode.seq);
    printAllGetsRec(text, cfgNode.alt);
  }

  private void printAllActionsRec(PrintWriter text, CFGElement lhs, CFGNode cfgNode) {
    if (cfgNode == null || cfgNode.elm.kind == CFGKind.END) return;
    if (cfgNode.slotTerm != 0 && iTerms.termArity(cfgNode.slotTerm) != 0) {
      text.print("      case " + cfgNode.num + ":");
      printSlotTermRec(text, cfgNode.slotTerm);
      text.println("break;");
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
}
