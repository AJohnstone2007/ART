package uk.ac.rhul.cs.csle.art.choose;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class ChooseRules implements DisplayInterface {
  private final Set<Integer> lexicalTerms;
  private final Set<Integer> derivationChooseTerms;
  private boolean dirty = true;

  private final Relation<CFGElement, CFGElement> lexicalHigher = new Relation<>();
  private final Relation<CFGElement, CFGElement> lexicalLonger = new Relation<>();
  private final Relation<CFGElement, CFGElement> lexicalShorter = new Relation<>();
  private final Relation<CFGNode, CFGNode> derivationHigher = new Relation<>();
  private final Relation<CFGNode, CFGNode> derivationLonger = new Relation<>();
  private final Relation<CFGNode, CFGNode> derivationShorter = new Relation<>();

  public ChooseRules(ChooseRules payload) { // Copy constructor
    super();
    lexicalTerms = new HashSet<>(payload.lexicalTerms);
    derivationChooseTerms = new HashSet<>(payload.derivationChooseTerms);
  }

  public ChooseRules() {
    super();
    lexicalTerms = new HashSet<>();
    derivationChooseTerms = new HashSet<>();
  }

  public void addDerivationChooseRule(int term) {
    ScriptInterpreter.seenChooseRule = true;
    derivationChooseTerms.add(term);
    dirty = true;
  }

  public void addLexicalChooseRule(int term) {
    ScriptInterpreter.seenChooseRule = true;
    lexicalTerms.add(term);
    dirty = true;
  }

  public void normalise() {
    if (!dirty) return;

    for (int term : derivationChooseTerms) {
      // Util.debug("Choose derivation rule : " + ScriptInterpreter.iTerms.toRawString(term));
      CFGNode lhs = locateCFGNode(ScriptInterpreter.iTerms.subterm(term, 0)), rhs = locateCFGNode(ScriptInterpreter.iTerms.subterm(term, 2));
      if (lhs == null || rhs == null) {
        Util.warning("No matching CFG rule found in derivation choose rule " + ScriptInterpreter.iTerms.toString(term));
        continue;
      }
      var op = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, 1));
      switch (op) {
      case "chooseHigher":
        derivationHigher.add(lhs, rhs);
        break;
      case "chooseLower":
        derivationHigher.add(rhs, lhs);
        break;
      case "chooseLonger":
        derivationLonger.add(lhs, rhs);
        break;
      case "chooseShorter":
        derivationShorter.add(lhs, rhs);
        break;
      default:
        Util.fatal("Unexpected chooserOp: " + op);
      }
    }

    for (int term : lexicalTerms) {
      // Util.debug("Choose lexical rule : " + ScriptInterpreter.iTerms.toRawString(term));
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

    checkCyclic();

    dirty = false;
  }

  private CFGNode locateCFGNode(int term) {
    int arity = ScriptInterpreter.iTerms.termArity(term);

    // Check for multiple dots
    int dotCount = 0;
    for (int i = 0; i < arity; i++)
      if (ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, i)).equals(".")) dotCount++;
    if (dotCount > 1) Util.fatal("Choose rule subterm has multiple dots " + ScriptInterpreter.iTerms.toString(term));

    CFGElement lhs = ScriptInterpreter.findCFGElement(ScriptInterpreter.iTerms.subterm(term, 0));
    CFGNode altNode = ScriptInterpreter.currentCFGRules.elementToRulesNodeMap.get(lhs);
    if (altNode == null) return null; // No lhs nonterminal

    for (altNode = altNode.alt; altNode != null; altNode = altNode.alt) { // Check each production
      // Util.debug("Testing choose rule term " + ScriptInterpreter.iTerms.toString(term) + " against production " + altNode.toStringAsProduction());

      int subTerm = 1;
      CFGNode ret = null;
      CFGNode seqNode = altNode.seq;

      while (true) {
        if (seqNode.cfgElement.cfgKind == CFGElementKind.END && ret == null) ret = seqNode;

        if (subTerm < arity && ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, subTerm)).equals(".")) {
          ret = seqNode;
          // Skip the dot
          subTerm++;
        }

        if (seqNode.cfgElement.cfgKind == CFGElementKind.END || subTerm >= arity) // We have exhausted either the production, or the term, or both
          break;

        // We have not exhausted either the term or the production, so test for match
        if (seqNode.cfgElement != ScriptInterpreter.findCFGElement(ScriptInterpreter.iTerms.subterm(term, subTerm))) break; // Mismatch

        seqNode = seqNode.seq;
        subTerm++;
      }

      if (seqNode.cfgElement.cfgKind == CFGElementKind.END && subTerm == arity) {
        // Util.debug("Choose term " + ScriptInterpreter.iTerms.toString(term) + " matched to " + ret.toStringAsProduction());
        return ret;
      }
    }
    if (altNode == null) Util.warning("No matching production found for derivation chooser " + ScriptInterpreter.iTerms.toString(term));
    return null;
  }

  void checkCyclic() {

    var tmpL = lexicalHigher.cyclic();
    if (tmpL.size() != 0) Util.warning("Lexical higher (>) choosers are cyclic on " + tmpL);
    tmpL = lexicalLonger.cyclic();
    if (tmpL.size() != 0) Util.warning("Lexical longer (>>) choosers are cyclic on " + tmpL);
    tmpL = lexicalShorter.cyclic();
    if (tmpL.size() != 0) Util.warning("Lexical shorter (<<) choosers are cyclic on " + tmpL);

    var tmpD = derivationHigher.cyclic();
    if (tmpD.size() != 0) Util.warning("Derivation higher (>) choosers are cyclic on ");
    for (var d : tmpD)
      Util.info(d.toStringAsProduction());

    tmpD = derivationLonger.cyclic();
    if (tmpD.size() != 0) Util.warning("Derivation longer (>>) choosers are cyclic on ");
    for (var d : tmpD)
      Util.info(d.toStringAsProduction());
    tmpD = derivationShorter.cyclic();
    if (tmpD.size() != 0) Util.warning("Derivation shorter (<<) choosers are cyclic on ");
    for (var d : tmpD)
      Util.info(d.toStringAsProduction());
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
      Set<CFGElementKind> collectKinds = new HashSet<>();
      String chooseSetName = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(firstChild, 0));
      switch (chooseSetName) {
      case "anyCharacterTerminal":
        collectKinds.add(CFGElementKind.TRM_CH);
        break;
      case "anyBuiltinTerminal":
        collectKinds.add(CFGElementKind.TRM_BI);
        break;
      case "anyCaseSensitiveTerminal":
        collectKinds.add(CFGElementKind.TRM_CS);
        break;
      case "anyCaseInsensitiveTerminal":
        collectKinds.add(CFGElementKind.TRM_CI);
        break;
      case "anyParaterminal":
        collectKinds.add(CFGElementKind.NONTERMINAL);
        break;
      case "anyTerminal":
        collectKinds.add(CFGElementKind.TRM_CH);
        collectKinds.add(CFGElementKind.TRM_BI);
        collectKinds.add(CFGElementKind.TRM_CS);
        collectKinds.add(CFGElementKind.TRM_CI);
        collectKinds.add(CFGElementKind.NONTERMINAL);
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
        outputStream.println(t1.toStringAsProduction() + " " + op + " " + (t2 == null ? t2 : t2.toStringAsProduction()));
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    normalise();
    printLexicalRelation(lexicalHigher, ">", outputStream, outputTraverser, indexed, full, indented);
    printDerivationRelation(derivationHigher, ">", outputStream, outputTraverser, indexed, full, indented);
    printLexicalRelation(lexicalLonger, ">>", outputStream, outputTraverser, indexed, full, indented);
    printDerivationRelation(derivationLonger, ">>", outputStream, outputTraverser, indexed, full, indented);
    printLexicalRelation(lexicalShorter, "<<", outputStream, outputTraverser, indexed, full, indented);
    printDerivationRelation(derivationShorter, "<<", outputStream, outputTraverser, indexed, full, indented);
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    normalise();
    // TODO Auto-generated method stub

  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
    // TODO Auto-generated method stub

  }
}
