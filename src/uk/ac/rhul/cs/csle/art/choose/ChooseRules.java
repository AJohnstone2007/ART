package uk.ac.rhul.cs.csle.art.choose;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class ChooseRules implements DisplayInterface {
  private final Relation<CFGElement, CFGElement> lexicalHigher;
  private final Relation<CFGElement, CFGElement> lexicalLonger;
  private final Relation<CFGElement, CFGElement> lexicalShorter;
  private final Relation<CFGNode, CFGNode> derivationHigher;
  private final Relation<CFGNode, CFGNode> derivationLonger;
  private final Relation<CFGNode, CFGNode> derivationShorter;

  public ChooseRules(ChooseRules payload) { // Copy constructor
    lexicalHigher = new Relation<>(payload.lexicalHigher);
    lexicalLonger = new Relation<>(payload.lexicalLonger);
    lexicalShorter = new Relation<>(payload.lexicalShorter);
    derivationHigher = new Relation<>(payload.derivationHigher);
    derivationLonger = new Relation<>(payload.derivationLonger);
    derivationShorter = new Relation<>(payload.derivationShorter);
  }

  public ChooseRules() {
    super();
    lexicalHigher = new Relation<>();
    lexicalLonger = new Relation<>();
    lexicalShorter = new Relation<>();
    derivationHigher = new Relation<>();
    derivationLonger = new Relation<>();
    derivationShorter = new Relation<>();
  }

  public void buildDerivationChooseRule(int term) {
    // Util.debug("Choose derivation rule : " + ScriptInterpreter.iTerms.toRawString(term));
  }

  public void buildLexicalChooseRule(int term) {
    // Util.debug("Choose lexical rule : " + ScriptInterpreter.iTerms.toRawString(term));
    ScriptInterpreter.seenChooseRule = true;
    Set<CFGElement> lhs = new HashSet<>(), rhs = new HashSet<>();
    buildLexicalChooseSet(lhs, ScriptInterpreter.iTerms.subterm(term, 0));
    buildLexicalChooseSet(rhs, ScriptInterpreter.iTerms.subterm(term, 2));
    var op = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, 1));
    switch (op) {
    case "chooseHigher":
      lexicalHigher.addAllAll(lhs, rhs);
      break;
    case "chooseLower":
      lexicalHigher.addAllAll(rhs, lhs);
      break;
    case "chooseLonger":
      lexicalLonger.addAllAll(lhs, rhs);
      break;
    case "chooseShorter":
      lexicalShorter.addAllAll(lhs, rhs);
      break;
    default:
      Util.fatal("Unexpected chooserOp: " + op);
    }
  }

  public boolean testHigher(CFGElement left, CFGElement right) {
    return lexicalHigher.get(left).contains(right);
  }

  public boolean testLonger(CFGElement left, CFGElement right) {
    return lexicalLonger.get(left).contains(right);
  }

  public boolean testShorter(CFGElement left, CFGElement right) {
    return lexicalShorter.get(left).contains(right);
  }

  public boolean testHigher(CFGNode left, CFGNode right) {
    return derivationHigher.get(left).contains(right);
  }

  public boolean testLonger(CFGNode left, CFGNode right) {
    return derivationLonger.get(left).contains(right);
  }

  public boolean testShorter(CFGNode left, CFGNode right) {
    return derivationShorter.get(left).contains(right);
  }

  void buildLexicalChooseSet(Set<CFGElement> set, int term) {
    int firstChild = ScriptInterpreter.iTerms.subterm(term, 0);
    if (ScriptInterpreter.iTerms.termSymbolString(firstChild).equals("chooseSet")) {
      Set<CFGKind> collectKinds = new HashSet<>();
      String chooseSetName = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(firstChild, 0));
      switch (chooseSetName) {
      case "anyCharacterTerminal":
        collectKinds.add(CFGKind.TRM_CH);
        break;
      case "anyBuiltinTerminal":
        collectKinds.add(CFGKind.TRM_BI);
        break;
      case "anyCaseSensitiveTerminal":
        collectKinds.add(CFGKind.TRM_CS);
        break;
      case "anyCaseInsensitiveTerminal":
        collectKinds.add(CFGKind.TRM_CI);
        break;
      case "anyParaterminal":
        collectKinds.add(CFGKind.NONTERMINAL);
        break;
      case "anyTerminal":
        collectKinds.add(CFGKind.TRM_CH);
        collectKinds.add(CFGKind.TRM_BI);
        collectKinds.add(CFGKind.TRM_CS);
        collectKinds.add(CFGKind.TRM_CI);
        collectKinds.add(CFGKind.NONTERMINAL);
        break;

      default:
        Util.fatal("Unexpected chooser set: " + chooseSetName);
      }
      for (var e : ScriptInterpreter.currentCFGRules.elements.keySet())
        if (collectKinds.contains(e.cfgKind)) set.add(e);
    } else
      set.add(ScriptInterpreter.findCFGElement(firstChild)); // First element is implicit add
    for (int i = 1; i < ScriptInterpreter.iTerms.termArity(term); i++) {
      CFGElement element = ScriptInterpreter.findCFGElement(ScriptInterpreter.iTerms.subterm(term, i, 0));
      if (ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, i)).equals("chooseAdd"))
        set.add(element);
      else
        set.remove(element);
    }
  }

  public void printLexicalRelation(Relation<CFGElement, CFGElement> relation, String op, PrintStream outputStream, TermTraverserText outputTraverser,
      boolean indexed, boolean full, boolean indented) {
    for (var t1 : relation.getDomain())
      for (var t2 : relation.get(t1))
        outputStream.println(t1 + " " + op + " " + t2);
  }

  public void printDerivationRelation(Relation<CFGNode, CFGNode> relation, String op, PrintStream outputStream, TermTraverserText outputTraverser,
      boolean indexed, boolean full, boolean indented) {
    for (var t1 : relation.getDomain())
      for (var t2 : relation.get(t1))
        outputStream.println(t1 + " " + op + " " + t2);
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    printLexicalRelation(lexicalHigher, ">", outputStream, outputTraverser, indexed, full, indented);
    printDerivationRelation(derivationHigher, ">", outputStream, outputTraverser, indexed, full, indented);
    printLexicalRelation(lexicalLonger, ">>", outputStream, outputTraverser, indexed, full, indented);
    printDerivationRelation(derivationLonger, ">>", outputStream, outputTraverser, indexed, full, indented);
    printLexicalRelation(lexicalShorter, "<<", outputStream, outputTraverser, indexed, full, indented);
    printDerivationRelation(derivationShorter, "<<", outputStream, outputTraverser, indexed, full, indented);
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
