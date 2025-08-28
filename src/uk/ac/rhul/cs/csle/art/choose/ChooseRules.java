package uk.ac.rhul.cs.csle.art.choose;

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.module.ARTV3Module;
import uk.ac.rhul.cs.csle.art.old.v4.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class ChooseRules implements DisplayInterface {
  private final Relation<CFGNode, CFGNode> higher = new Relation<>();
  private final Relation<CFGNode, CFGNode> longer = new Relation<>();
  private final Relation<CFGNode, CFGNode> shorter = new Relation<>();
  private final Set<Integer> chooseTerms = new LinkedHashSet<>();

  public ChooseRules(ChooseRules payload) { // Copy constructor
  }

  public ChooseRules() {
    super();
  }

  public void buildChooseRule(int term) {
    Util.debug("Choose rule : " + ScriptInterpreter.iTerms.toString(term));
    chooseTerms.add(term);
  }

  public void normalise(CFGRules grammar) {
    // Util.info("Building chooser relation on " + chooseRules);
    // for (var i : chooseRules) {
    // Util.info("Term i:");
    //
    // }

    // Create an array of bindings that will be used to 'evaluate' chooser expressions using the value system
    int[] bindings = new int[9]; // Note binding zero is reserved for wildcard...
    // !!! Compress this code by makiong multiple passes
    final int anyCharacterTerminal = 1, anyBuiltinTerminal = 2, anyCaseSensitiveTerminal = 3, anyCaseInsensitiveTerminal = 4, anyParaterminal = 5,
        anyNonterminal = 6, anyLiteralTerminal = 7, anyTerminal = 8;

    // Build the pseudo-variables for this grammar
    String anyCharacterStr = "__set(", anyBuiltinStr = "__set(", anyCaseSensitiveStr = "__set(", anyCaseInsensitiveStr = "__set(", anyParaStr = "__set(",
        anyNonStr = "__set(", anyLiteralStr = "__set(", anyStr = "__set(";

    for (CFGElement e1 : grammar.elements.keySet()) {
      switch (e1.cfgKind) {
      case TRM_BI:
        anyBuiltinStr = extendStringList(anyBuiltinStr, "srBuiltinTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srBuiltinTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case TRM_CH:
        anyCharacterStr = extendStringList(anyCharacterStr, "srCharacterTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srCharacterTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case TRM_CS:
        anyCaseSensitiveStr = extendStringList(anyCaseSensitiveStr, "srCaseSensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyLiteralStr = extendStringList(anyLiteralStr, "srCaseSensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srCaseSensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case TRM_CI:
        anyCaseInsensitiveStr = extendStringList(anyCaseInsensitiveStr, "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyLiteralStr = extendStringList(anyLiteralStr, "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case NONTERMINAL:
        if (grammar.paraterminals.contains(e1))
          anyParaStr = extendStringList(anyParaStr, "srNonterminal(" + ITerms.escapeMeta(e1.str) + ")");
        else
          anyNonStr = extendStringList(anyNonStr, "srNonterminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srNonterminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      default:
        break;
      }
    }

    anyCharacterStr += ")";
    anyBuiltinStr += ")";
    anyCaseSensitiveStr += ")";
    anyCaseInsensitiveStr += ")";
    anyParaStr += ")";
    anyNonStr += ")";
    anyLiteralStr += ")";
    anyStr += ")";

    // Now make the terms, and assign to the bindings array
    bindings[anyCharacterTerminal] = ScriptInterpreter.iTerms.findTerm(anyCharacterStr);
    bindings[anyBuiltinTerminal] = ScriptInterpreter.iTerms.findTerm(anyBuiltinStr);
    bindings[anyCaseSensitiveTerminal] = ScriptInterpreter.iTerms.findTerm(anyCaseSensitiveStr);
    bindings[anyCaseInsensitiveTerminal] = ScriptInterpreter.iTerms.findTerm(anyCaseInsensitiveStr);
    bindings[anyParaterminal] = ScriptInterpreter.iTerms.findTerm(anyParaStr);
    bindings[anyNonterminal] = ScriptInterpreter.iTerms.findTerm(anyNonStr);
    bindings[anyLiteralTerminal] = ScriptInterpreter.iTerms.findTerm(anyLiteralStr);
    bindings[anyTerminal] = ScriptInterpreter.iTerms.findTerm(anyStr);

    // Debug: announce what we have just constructed
    // Util.info("Characters: " + ScriptTermInterpreter.iTerms.toString(bindings[1]));
    // Util.info("Builtins: " + ScriptTermInterpreter.iTerms.toString(bindings[2]));
    // Util.info("CaseSensitives: " + ScriptTermInterpreter.iTerms.toString(bindings[3]));
    // Util.info("CaseInsensitives: " + ScriptTermInterpreter.iTerms.toString(bindings[4]));
    // Util.info("Paras: " + ScriptTermInterpreter.iTerms.toString(bindings[5]));
    // Util.info("Nons: " + ScriptTermInterpreter.iTerms.toString(bindings[6]));
    // Util.info("Literals: " + ScriptTermInterpreter.iTerms.toString(bindings[7]));
    // Util.info("Any: " + ScriptTermInterpreter.iTerms.toString(bindings[8]));

    // for (String chooserSetID : ARTV3Module.getChoosers().keySet()) {
    // List<String> chooseExpressionList = ARTV3Module.getChoosers().get(chooserSetID);
    // ARTChooserSet chooserSet = findChooserSet(chooserSetID);
    //
    // for (String expression : chooseExpressionList) {
    // // Util.info("Evaluating chooser expression:" + expression);
    //
    // int root = ScriptTermInterpreter.iTerms.findTerm(expression);
    //
    // if (ScriptTermInterpreter.iTerms.getTermSymbolString(ScriptTermInterpreter.iTerms.getSubterm(root, 0)).equals("chooseSPPF")
    // || ScriptTermInterpreter.iTerms.getTermSymbolString(ScriptTermInterpreter.iTerms.getSubterm(root, 1)).equals("chooseSPPF"))
    // if (!(ScriptTermInterpreter.iTerms.getTermSymbolString(ScriptTermInterpreter.iTerms.getSubterm(root, 0)).equals("chooseSPPF")
    // && ScriptTermInterpreter.iTerms.getTermSymbolString(ScriptTermInterpreter.iTerms.getSubterm(root, 1)).equals("chooseSPPF"))) {
    // Util.info("SPPF choosers can only use productions: skipping");
    // continue;
    // }
    //
    // int evaluated;
    // evaluated = ScriptTermInterpreter.iTerms.substitute(bindings, root, 0);
    //
    // switch (ScriptTermInterpreter.iTerms.getTermSymbolString(evaluated)) {
    // case "chooseHigher":
    // updateChooser(ARTV3Module, chooserSet.higher, ScriptTermInterpreter.iTerms.getSubterm(evaluated, 0), ScriptTermInterpreter.iTerms.getSubterm(evaluated,
    // 1));
    // break;
    // case "chooseLonger":
    // updateChooser(ARTV3Module, chooserSet.longer, ScriptTermInterpreter.iTerms.getSubterm(evaluated, 0), ScriptTermInterpreter.iTerms.getSubterm(evaluated,
    // 1));
    // break;
    // case "chooseShorter":
    // updateChooser(ARTV3Module, chooserSet.shorter, ScriptTermInterpreter.iTerms.getSubterm(evaluated, 0), ScriptTermInterpreter.iTerms.getSubterm(evaluated,
    // 1));
    // break;
    // }
    // }
    // }
    //
    // // Util.info("Used nonterminals " + usedNonterminals);
    // // Util.info("Paraterminals " + paraterminals);
    //
    // for (ARTGrammarElementNonterminal n : nonterminals)
    // if (n.getProductions().isEmpty() && usedNonterminals.contains(n))
    // if (!paraterminals.contains(n)) Util.info("*** Warning - undefined nonterminal " + n);
  }

  private String extendStringList(String string, String extension) {
    if (!string.endsWith("(")) string += ',';
    string += extension;
    return string;
  }

  private void updateChooser(ARTV3Module ARTV3Module, ARTBitSet[] bits, int lhsTerm, int rhsTerm) {
    for (int l = 0; l < ScriptInterpreter.iTerms.termArity(lhsTerm); l++) {
      // Util.info("Updating chooser with: " + ScriptTermInterpreter.iTerms.toString(lhsTerm) + " relation " + ScriptTermInterpreter.iTerms.toString(rhsTerm));
      int lTerm = ScriptInterpreter.iTerms.subterm(lhsTerm, l);
      for (int r = 0; r < ScriptInterpreter.iTerms.termArity(rhsTerm); r++) {
        int rTerm = ScriptInterpreter.iTerms.subterm(rhsTerm, r);
        setChooserBit(ARTV3Module, bits, lTerm, rTerm);
      }
    }
  }

  private void setChooserBit(ARTV3Module ARTV3Module, ARTBitSet[] bits, int l, int r) {
    int lElement = convertTermToEnumerationElement(ARTV3Module, l), rElement = convertTermToEnumerationElement(ARTV3Module, r);

    if (lElement == -1 || rElement == -1) return;
    if (bits[lElement] == null) bits[lElement] = new ARTBitSet();
    bits[lElement].set(rElement);
  }

  private int convertTermToEnumerationElement(ARTV3Module module, int term) {
    String child = ITerms.unescapeMeta(ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(term, 0)));
    // Util.info("Converting: " + child);

    ARTGrammarElement element = null;

    // switch (ScriptTermInterpreter.iTerms.getTermSymbolString(term)) {
    // case "srNonterminal":
    // element = nonterminalNameMap.get(new ARTName(module, child));
    // break;
    // case "srCaseSensitiveTerminal":
    // element = terminalCaseSensitiveNameMap.get(child);
    // break;
    // case "srCaseInsensitiveTerminal":
    // element = terminalCaseInsensitiveNameMap.get(child);
    // break;
    // case "srBuiltinTerminal":
    // element = terminalBuiltinNameMap.get(child);
    // break;
    // case "srCharacterTerminal":
    // element = terminalCharacterNameMap.get(child);
    // break;
    //
    // default:
    // throw new ARTUncheckedException("Unrecognised chooser term " + ScriptTermInterpreter.iTerms.toString(term));
    // }

    // if (element == null) {
    // Util.info("Warning - chooser element " + child + " does not appear in any grammar rule");
    // return -1;
    // }
    //
    int ret = element.getElementNumber();

    // Util.info("Returning: " + ret);

    return ret;
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    // TODO Auto-generated method stub

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
