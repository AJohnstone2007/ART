package uk.ac.rhul.cs.csle.art.interpret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RIGInterpreter_OLD extends AbstractInterpreter {
  ITerms it;
  // Rules are held as a map from relation to map from constructor symbol string index to ArrayList of rule terms
  Map<Integer /* relation term */, Map<Integer /* symbol string index */, ArrayList<Integer> /* rule term list */>> rules;
  // We remember all rewrites to speed things up
  // The memo table is a map from relation to map from term to set of terms
  Map<Integer /* relation term */, Map<Integer /* start term */, Set<Integer> /* set of rewritten terms */>> memo;
  int startRelation;
  Set<Integer> terminalSymbols;
  private int rewriteAttemptCounter;
  private int matchRelationTerm;
  private int artTupleSymbolIndex;

  public RIGInterpreter_OLD() {
    Util.trace(3, "Interpreter set to RiM (Rewriting in General)");
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
    it = ScriptInterpreter.iTerms; // Local reference to reduce typing
    startRelation = 0;
    rules = new HashMap<>();
    memo = new HashMap<>();
    matchRelationTerm = it.findTerm("|>");
    artTupleSymbolIndex = it.findString("artTuple");
    terminalSymbols = new HashSet<>();
    terminalSymbols.add(it.findString("int32"));
    terminalSymbols.add(it.findString("real64"));
    terminalSymbols.add(it.findString("string"));
    Util.info("RiG interpreting\n" + it.toString(ScriptInterpreter.currentTryTerm));
    interpretRec(ScriptInterpreter.currentTryTerm);
    // printRules();
  }

  private void interpretRec(int term) {
    switch (it.termSymbolString(term)) {
    case "artRule":
      Util.info("Found rule: " + it.toString(term));
      addRule(term);
      break;
    case "!try": // perform rewrites
      Util.info("Found !try: " + it.toString(term));
      stepper(it.subterm(term, 0), startRelation);
      break;
    }

    int[] children = it.termChildren(term);
    for (int c = 0; c < children.length; c++) // recurse, left to right
      interpretRec(children[c]);
  }

  private void addRule(int term) {
    Integer relation = it.subterm(term, 2, 1);
    if (startRelation == 0) startRelation = relation;
    Integer lhsConclusionRootSymbolStringIndex = it.termSymbolStringIndex(it.subterm(term, 2, 0));
    Util.info("Adding new rule " + it.toString(relation) + "/" + it.getString(lhsConclusionRootSymbolStringIndex) + ": " + it.toString(term));

    if (rules.get(relation) == null) rules.put(relation, new HashMap<Integer, ArrayList<Integer>>());
    Map<Integer, ArrayList<Integer>> constructorMap = rules.get(relation);
    if (constructorMap.get(lhsConclusionRootSymbolStringIndex) == null) constructorMap.put(lhsConclusionRootSymbolStringIndex, new ArrayList<Integer>());
    ArrayList<Integer> activeRuleList = constructorMap.get(lhsConclusionRootSymbolStringIndex);
    activeRuleList.add(term);
  }

  private void printRules() {
    Util.info("-- Rules");
    for (var relation : rules.keySet())
      for (var rootSymbol : rules.get(relation).keySet())
        for (var rule : rules.get(relation).get(rootSymbol))
          Util.info("Rule: " + it.toString(relation) + "/" + it.getString(rootSymbol) + ": " + it.toString(rule));
  }

  private int stepper(int term, int relation) {
    int newTerm, oldTerm = term;
    int rewriteStepCounter = rewriteAttemptCounter = 1;
    while (true) {
      Util.trace(3, 0, "Step " + rewriteStepCounter++ + " attempting to rewrite " + it.toString(oldTerm));
      newTerm = evaluate(oldTerm, relation, 1);
      if (newTerm == 0 || newTerm == oldTerm /* nothing changed */) break;
      rewriteStepCounter++;
      oldTerm = newTerm;
    }
    Util.trace(2, 0,
        (isTerminatingConfiguration(oldTerm, relation) ? "Normal termination on " : "Stuck on ") + it.toString(newTerm) + " after " + rewriteStepCounter
            + " step" + (rewriteStepCounter == 1 ? "" : "s") + " and " + rewriteAttemptCounter + " rewrite attempt" + (rewriteAttemptCounter == 1 ? "" : "s"));
    return newTerm;

  }

  private boolean isTerminatingConfiguration(int term, int relation) {
    int thetaRoot = term;
    int thetaRootSymbolIndex = it.termSymbolStringIndex(thetaRoot);
    if (thetaRootSymbolIndex == artTupleSymbolIndex) { // Step down to first element of tuple
      thetaRoot = it.subterm(thetaRoot, 0);
      thetaRootSymbolIndex = it.termSymbolStringIndex(thetaRoot);
    }
    return terminalSymbols.contains(thetaRootSymbolIndex);
  }

  int evaluate(int term, int relation, int level) {
    Util.info("evaluate() " + it.toString(term) + " under " + it.toString(relation) + " at level " + level);
    Map<Integer, Integer> rho, rhoP;
    int rootSymbolIndex = it.termSymbolStringIndex(term);
    if (terminalSymbols.contains(rootSymbolIndex)) return term;
    if (relation == matchRelationTerm) return term;
    var compatibleRelation = rules.get(relation);
    if (compatibleRelation == null) Util.error("No rules found for relation " + it.toString(relation));
    var compatibleRules = rules.get(relation).get(rootSymbolIndex);
    if (compatibleRules == null) {
      Util.warning("No rules found for constructor " + it.getString(rootSymbolIndex) + " in relation " + it.toString(relation));
      return term;
    }

    nextRule: for (var rule : compatibleRules) {
      Util.trace(3, "Rule " + it.toString(rule));
      int label = it.subterm(rule, 0);
      int conclusionLHS = it.subterm(rule, 2, 0);
      int conclusionRHS = it.subterm(rule, 2, 2);
      Util.debug("Evaluate: trying rule -" + it.toString(label));
      boolean thetaMatch = match(rho = new HashMap<Integer, Integer>(), term, conclusionLHS);
      Util.debug("Theta match " + thetaMatch + " with bindings " + rho);
      if (!thetaMatch) continue nextRule;
      int[] premises = it.termChildren(it.subterm(rule, 1));
      for (int i = 0; i < premises.length; i++) {
        int premiseLHS = it.subterm(premises[i], 0);
        int premiseRelation = it.subterm(premises[i], 1);
        int premiseRHS = it.subterm(premises[i], 2);
        if (!match(rho, evaluate(substitute(premiseLHS, rho), premiseRelation, 1), substitute(premiseRHS, rho))) continue nextRule;
      }
      return substitute(conclusionRHS, rho);
    }
    return term;
  }

  boolean match(Map<Integer, Integer> bindings, int closedTerm, int pattern) {
    Util.debug("match() closed term " + it.toString(closedTerm) + " pattern " + it.toString(pattern));
    // Postorder match, so recurse first

    int[] closedChildren = it.termChildren(closedTerm);
    int[] patternChildren = it.termChildren(pattern);

    // Now just try non-star variables
    if (closedChildren.length != patternChildren.length) return false;
    for (int i = 0; i < closedChildren.length; i++)
      if (!match(bindings, closedChildren[i], patternChildren[i])) return false;

    int closedSymbolIndex = it.termSymbolStringIndex(closedTerm);
    String closedSymbolString = it.getString(closedSymbolIndex);
    char closedSymbolPrefix = closedSymbolString.charAt(0);
    if (closedSymbolPrefix == '_' || closedSymbolPrefix == '$') Util.fatal("match() - closed term has variable or function" + it.toString(closedTerm));

    int patternSymbolIndex = it.termSymbolStringIndex(pattern);
    String patternSymbolString = it.getString(patternSymbolIndex);
    char patternSymbolPrefix = patternSymbolString.charAt(0);
    if (patternSymbolPrefix == '$') Util.fatal("match() - pattern has function" + it.toString(pattern));

    if (patternSymbolPrefix == '_') { // Process variable
      Integer bound;
      if ((bound = bindings.get(patternSymbolIndex)) != null) {
        Util.warning("match() - pattern is nonlinear on " + patternSymbolString);
        return bound == closedTerm;
      }
      bindings.put(patternSymbolIndex, closedTerm);
      Util.debug("match() return true with bindings " + bindings);
      return true;
    }

    // if we arrive here, then all children have matched and variables have been processed
    return closedSymbolIndex == patternSymbolIndex;
  }

  int substitute(int pattern, Map<Integer, Integer> substitution) {
    int patternSymbolIndex = it.termSymbolStringIndex(pattern);
    String patternSymbolString = it.getString(patternSymbolIndex);

    int[] children = it.termChildren(pattern);

    if (patternSymbolString.charAt(0) == '_') {
      Integer replacement = substitution.get(patternSymbolIndex);
      if (replacement != null) return replacement;
    }

    for (int i = 0; i < children.length; i++)
      children[i] = substitute(children[i], substitution);

    return it.findTerm(it.termSymbolStringIndex(pattern), children);
  }
}
