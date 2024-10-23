package uk.ac.rhul.cs.csle.art.choose;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.module.ARTV3Module;
import uk.ac.rhul.cs.csle.art.old.v4.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Relation;

public class ChooseRules {
  private final ITerms iTerms;
  private final Relation higher = new Relation();
  private final Relation longer = new Relation();
  private final Relation shorter = new Relation();

  public ChooseRules(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  public void normalise(CFGRules grammar, Set<Integer> chooseRules) {
    // System.out.println("Building chooser relation on " + chooseRules);
    // for (var i : chooseRules) {
    // System.out.println("Term i:");
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
      switch (e1.kind) {
      case B:
        anyBuiltinStr = extendStringList(anyBuiltinStr, "srBuiltinTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srBuiltinTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case C:
        anyCharacterStr = extendStringList(anyCharacterStr, "srCharacterTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srCharacterTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case T:
        anyCaseSensitiveStr = extendStringList(anyCaseSensitiveStr, "srCaseSensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyLiteralStr = extendStringList(anyLiteralStr, "srCaseSensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srCaseSensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case TI:
        anyCaseInsensitiveStr = extendStringList(anyCaseInsensitiveStr, "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyLiteralStr = extendStringList(anyLiteralStr, "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        anyStr = extendStringList(anyStr, "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(e1.str) + ")");
        break;
      case N:
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
    bindings[anyCharacterTerminal] = iTerms.findTerm(anyCharacterStr);
    bindings[anyBuiltinTerminal] = iTerms.findTerm(anyBuiltinStr);
    bindings[anyCaseSensitiveTerminal] = iTerms.findTerm(anyCaseSensitiveStr);
    bindings[anyCaseInsensitiveTerminal] = iTerms.findTerm(anyCaseInsensitiveStr);
    bindings[anyParaterminal] = iTerms.findTerm(anyParaStr);
    bindings[anyNonterminal] = iTerms.findTerm(anyNonStr);
    bindings[anyLiteralTerminal] = iTerms.findTerm(anyLiteralStr);
    bindings[anyTerminal] = iTerms.findTerm(anyStr);

    // Debug: announce what we have just constructed
    // System.out.println("Characters: " + iTerms.toString(bindings[1]));
    // System.out.println("Builtins: " + iTerms.toString(bindings[2]));
    // System.out.println("CaseSensitives: " + iTerms.toString(bindings[3]));
    // System.out.println("CaseInsensitives: " + iTerms.toString(bindings[4]));
    // System.out.println("Paras: " + iTerms.toString(bindings[5]));
    // System.out.println("Nons: " + iTerms.toString(bindings[6]));
    // System.out.println("Literals: " + iTerms.toString(bindings[7]));
    // System.out.println("Any: " + iTerms.toString(bindings[8]));

    // for (String chooserSetID : ARTV3Module.getChoosers().keySet()) {
    // List<String> chooseExpressionList = ARTV3Module.getChoosers().get(chooserSetID);
    // ARTChooserSet chooserSet = findChooserSet(chooserSetID);
    //
    // for (String expression : chooseExpressionList) {
    // // System.out.println("Evaluating chooser expression:" + expression);
    //
    // int root = iTerms.findTerm(expression);
    //
    // if (iTerms.getTermSymbolString(iTerms.getSubterm(root, 0)).equals("chooseSPPF")
    // || iTerms.getTermSymbolString(iTerms.getSubterm(root, 1)).equals("chooseSPPF"))
    // if (!(iTerms.getTermSymbolString(iTerms.getSubterm(root, 0)).equals("chooseSPPF")
    // && iTerms.getTermSymbolString(iTerms.getSubterm(root, 1)).equals("chooseSPPF"))) {
    // System.out.println("SPPF choosers can only use productions: skipping");
    // continue;
    // }
    //
    // int evaluated;
    // evaluated = iTerms.substitute(bindings, root, 0);
    //
    // switch (iTerms.getTermSymbolString(evaluated)) {
    // case "chooseHigher":
    // updateChooser(ARTV3Module, chooserSet.higher, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
    // break;
    // case "chooseLonger":
    // updateChooser(ARTV3Module, chooserSet.longer, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
    // break;
    // case "chooseShorter":
    // updateChooser(ARTV3Module, chooserSet.shorter, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
    // break;
    // }
    // }
    // }
    //
    // // System.out.println("Used nonterminals " + usedNonterminals);
    // // System.out.println("Paraterminals " + paraterminals);
    //
    // for (ARTGrammarElementNonterminal n : nonterminals)
    // if (n.getProductions().isEmpty() && usedNonterminals.contains(n))
    // if (!paraterminals.contains(n)) System.out.println("*** Warning - undefined nonterminal " + n);
  }

  private String extendStringList(String string, String extension) {
    if (!string.endsWith("(")) string += ',';
    string += extension;
    return string;
  }

  private void updateChooser(ARTV3Module ARTV3Module, ARTBitSet[] bits, int lhsTerm, int rhsTerm) {
    for (int l = 0; l < iTerms.getTermArity(lhsTerm); l++) {
      // System.out.println("Updating chooser with: " + iTerms.toString(lhsTerm) + " relation " + iTerms.toString(rhsTerm));
      int lTerm = iTerms.getSubterm(lhsTerm, l);
      for (int r = 0; r < iTerms.getTermArity(rhsTerm); r++) {
        int rTerm = iTerms.getSubterm(rhsTerm, r);
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
    String child = ITerms.unescapeMeta(iTerms.getTermSymbolString(iTerms.getSubterm(term, 0)));
    // System.out.println("Converting: " + child);

    ARTGrammarElement element = null;

    // switch (iTerms.getTermSymbolString(term)) {
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
    // throw new ARTUncheckedException("Unrecognised chooser term " + iTerms.toString(term));
    // }

    // if (element == null) {
    // System.out.println("Warning - chooser element " + child + " does not appear in any grammar rule");
    // return -1;
    // }
    //
    int ret = element.getElementNumber();

    // System.out.println("Returning: " + ret);

    return ret;
  }

}
