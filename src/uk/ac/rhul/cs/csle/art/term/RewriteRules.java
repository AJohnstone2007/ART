package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.util.Util;

public class RewriteRules {
  private final ITerms iTerms;
  public final Map<Integer, Map<Integer, List<Integer>>> trRules = new LinkedHashMap<>();
  public int defaultStartRelation = 0;

  public RewriteRules(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  public void buildTRRule(int term) {
    // System.out.println("Processing trRule: " + iTerms.toString(term));
    int relation = iTerms.subterm(term, 1, 1, 1);
    int constructorIndex = iTerms.termSymbolStringIndex((iTerms.subterm(term, 1, 1, 0, 0)));
    // System.out.println("Building TR rule " + iTerms.toString(term) + "\nwith relation " + iTerms.toString(relation) + "\nand constructor "
    // + iTerms.getString(constructorIndex));
    if (trRules.get(relation) == null) trRules.put(relation, new HashMap<>());
    Map<Integer, List<Integer>> map = trRules.get(relation);
    if (map.get(constructorIndex) == null) map.put(constructorIndex, new LinkedList<>());
    map.get(constructorIndex).add(term);
    if (defaultStartRelation == 0) defaultStartRelation = relation;
    Util.trace(6, 0, "*** Added rewrite rule " + iTerms.toString(term) + "\n");
  }
}
