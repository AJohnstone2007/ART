package uk.ac.rhul.cs.csle.art.interpret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Util;

public class rigInterpreter extends AbstractInterpreter {
  ITerms it;
  // Rules are held as a map from relation to map from constructor symbol string index to ArrayList of rule terms
  Map<Integer /* relation term */, Map<Integer /* symbol string index */, ArrayList<Integer> /* rule term list */>> rules;

  public rigInterpreter() {
    Util.trace(3, "Interpreter set to RiM (Rewriting in General)");
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
    it = ScriptInterpreter.iTerms; // Local reference to reduce typing

    Util.info("RiG interpreting\n" + it.toString(ScriptInterpreter.currentTryTerm));

    interpretRec(ScriptInterpreter.currentTryTerm);
  }

  private void interpretRec(int term) {
    rules = new HashMap<>();
    switch (it.termSymbolString(term)) {
    case "!try": // perform rewrites
      Util.info("Found !try: " + it.toString(term));
      break;
    case "artRule":
      Util.info("Found rule: " + it.toString(term));
      addRule(term);
      break;

    }
    int[] children = it.termChildren(term);
    for (int c = 0; c < children.length; c++) // recurse, left to right
      interpretRec(children[c]);
  }

  private void addRule(int term) {
    Integer relation = it.subterm(term, 1, 1, 1);
    Integer lhsConclusionRootSymbolStringIndex = it.termSymbolStringIndex(it.subterm(term, 1, 1, 0));

    Util.info("Adding new rule " + it.toString(relation) + "/" + it.getString(lhsConclusionRootSymbolStringIndex) + ": " + it.toString(term));
    if (rules.get(relation) == null) rules.put(relation, new HashMap<Integer, ArrayList<Integer>>());
    Map<Integer, ArrayList<Integer>> constructorMap = rules.get(relation);
    if (constructorMap.get(lhsConclusionRootSymbolStringIndex) == null) constructorMap.put(lhsConclusionRootSymbolStringIndex, new ArrayList<Integer>());
    ArrayList<Integer> activeRuleList = constructorMap.get(lhsConclusionRootSymbolStringIndex);
    activeRuleList.add(term);
  }

  Map<Integer, Integer> match(int l, int r) {
    return null;
  }

  int substitute(int term, Map<Integer, Integer> substition) {
    return 0;
  }

  Set<Integer> evaluate(int term, int relation) {
    return null;
  }
}
