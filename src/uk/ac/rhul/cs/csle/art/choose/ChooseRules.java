package uk.ac.rhul.cs.csle.art.choose;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class ChooseRules implements DisplayInterface {
  private final Relation<CFGElement, CFGElement> higher;
  private final Relation<CFGElement, CFGElement> longer;
  private final Relation<CFGElement, CFGElement> shorter;

  public ChooseRules(ChooseRules payload) { // Copy constructor
    higher = new Relation<>(payload.higher);
    longer = new Relation<>(payload.longer);
    shorter = new Relation<>(payload.shorter);
  }

  public ChooseRules() {
    super();
    higher = new Relation<>();
    longer = new Relation<>();
    shorter = new Relation<>();
  }

  public void buildChooseRule(int term) {
    Util.debug("Choose rule : " + ScriptInterpreter.iTerms.toRawString(term));
    Set<CFGElement> lhs = new HashSet<>(), rhs = new HashSet<>();
    buildChooseSet(lhs, ScriptInterpreter.iTerms.subterm(term, 0));
    buildChooseSet(rhs, ScriptInterpreter.iTerms.subterm(term, 2));
    var op = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, 1));
    switch (op) {
    case "chooseHigher":
      higher.addAllAll(lhs, rhs);
      break;
    case "chooseLower":
      higher.addAllAll(rhs, lhs);
      break;
    case "chooseLonger":
      longer.addAllAll(lhs, rhs);
      break;
    case "chooseShorter":
      shorter.addAllAll(lhs, rhs);
      break;
    default:
      Util.fatal("Unexpected chooserOp: " + op);
    }
  }

  void buildChooseSet(Set<CFGElement> set, int term) {
    set.add(ScriptInterpreter.findCFGElement(ScriptInterpreter.iTerms.subterm(term, 0))); // First element is implicit add
    for (int i = 1; i < ScriptInterpreter.iTerms.termArity(term); i++) {
      CFGElement element = ScriptInterpreter.findCFGElement(ScriptInterpreter.iTerms.subterm(term, i, 0));
      if (ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, i)).equals("chooseAdd"))
        set.add(element);
      else
        set.remove(element);
    }
  }

  public void printRelation(Relation<CFGElement, CFGElement> relation, String op, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed,
      boolean full, boolean indented) {
    for (var t1 : relation.getDomain())
      for (var t2 : relation.get(t1))
        outputStream.println(t1 + " " + op + " " + t2);
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    printRelation(higher, ">", outputStream, outputTraverser, indexed, full, indented);
    printRelation(longer, ">>", outputStream, outputTraverser, indexed, full, indented);
    printRelation(shorter, "<<", outputStream, outputTraverser, indexed, full, indented);
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    // TODO Auto-generated method stub

  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
    // TODO Auto-generated method stub

  }
}
