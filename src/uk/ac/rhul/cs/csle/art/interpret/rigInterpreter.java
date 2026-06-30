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

public class RIGInterpreter extends AbstractInterpreter {
  ITerms it;
  // Rules are held as a map Chi from relation to map from constructor symbol string index to ArrayList of rule terms
  Map<Integer /* relation term */, Map<Integer /* symbol string index */, ArrayList<Integer> /* rule term list */>> rules;

  int startRelation = 0; // Relation in first conclusion seen
  Map<Integer /* relation term */, Set<Integer> /* set of terminal pattern */> terminalPatterns;

  private int rewriteAttemptCounter;
  private int matchRelationTerm;
  private int artTupleSymbolIndex;

  public RIGInterpreter() {
    Util.trace(3, "Interpreter set to RiM (Rewriting in General)");
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
    it = ScriptInterpreter.iTerms; // Local reference to reduce typing
    startRelation = 0;
    rules = new HashMap<>();
    matchRelationTerm = it.findTerm("|>");
    artTupleSymbolIndex = it.findString("artTuple");
    terminalPatterns = new HashMap<>();
    addTerminalPattern(0, it.findTerm("int32(_)")); // We use term 0 to represent default terminals which are present in all relations
    addTerminalPattern(0, it.findTerm("real64(_)"));
    addTerminalPattern(0, it.findTerm("string(_)"));

    Util.info("RiG interpreting\n" + it.toString(ScriptInterpreter.currentTryTerm));
    interpretRec(ScriptInterpreter.currentTryTerm);
    // Debug printRules();
  }

  private void addTerminalPattern(int relation, int pattern) {
    if (terminalPatterns.get(relation) == null) terminalPatterns.put(relation, new HashSet<Integer>());
    terminalPatterns.get(relation).add(pattern);
  }

  // Helper routine to add a rule to the working set Chi
  private void addRule(int term) {
    Integer relation = it.subterm(term, 2, 1);
    if (startRelation == 0) startRelation = relation;
    Integer lhsConclusionRootSymbolStringIndex = it.termSymbolStringIndex(it.subterm(term, 2, 0));
    // Util.debug("Adding new rule " + it.toString(relation) + "/" + it.getString(lhsConclusionRootSymbolStringIndex) + ": " + it.toString(term));

    if (rules.get(relation) == null) rules.put(relation, new HashMap<Integer, ArrayList<Integer>>());
    Map<Integer, ArrayList<Integer>> constructorMap = rules.get(relation);
    if (constructorMap.get(lhsConclusionRootSymbolStringIndex) == null) constructorMap.put(lhsConclusionRootSymbolStringIndex, new ArrayList<Integer>());
    ArrayList<Integer> activeRuleList = constructorMap.get(lhsConclusionRootSymbolStringIndex);
    activeRuleList.add(term);
  }

  // Script interpreter which preorder traverses the derivation tree of the user script, add rules and interpeting directives
  private void interpretRec(int term) {
    switch (it.termSymbolString(term)) {
    case "artRule":
      // Util.debug("Found rule: " + it.toString(term));
      addRule(term);
      break;
    case "!try": // perform rewrites
      // Util.debug("Found !try: " + it.toString(term));
      stepper(it.subterm(term, 0), startRelation);
      break;
    }

    int[] children = it.termChildren(term);
    for (int c = 0; c < children.length; c++) // recurse, left to right
      interpretRec(children[c]);
  }

  private void printRules() {
    Util.info("-- Rules");
    for (var relation : rules.keySet())
      for (var rootSymbol : rules.get(relation).keySet())
        for (var rule : rules.get(relation).get(rootSymbol))
          Util.info("Rule: " + it.toString(relation) + "/" + it.getString(rootSymbol) + ": " + it.toString(rule));
  }

  private void stepper(int term, int relation) {
    int rewriteStepCounter = rewriteAttemptCounter = 1;
    HashSet<Integer> newTerms = new HashSet<Integer>(), oldTerms = new HashSet<Integer>(), tmp;
    oldTerms.add(term);

    while (true) {
      Util.trace(3, 0, "Step " + rewriteStepCounter++);
      HashSet<Integer> resultTerms = evaluateExtend(oldTerms, relation, 1);
      if (newTerms.size() == 0) break;
      rewriteStepCounter++;
      tmp = oldTerms;
      oldTerms = newTerms;
      newTerms = tmp;
      newTerms.clear();
    }
    Util.trace(2, 0,
        "Terminated after " + rewriteStepCounter + " steps and " + rewriteAttemptCounter + " rewrite attempt" + (rewriteAttemptCounter == 1 ? "" : "s"));
    for (var t : oldTerms) {
      Util.trace(4, 3, (isTerminatingConfiguration(t, relation) ? "Terminal " : "Stuck on ") + it.toString(t));
    }
  }

  private boolean isTerminatingConfiguration(int term, int relation) {
    int thetaRoot = term;
    int thetaRootSymbolIndex = it.termSymbolStringIndex(thetaRoot);
    if (thetaRootSymbolIndex == artTupleSymbolIndex) { // Step down to first element of tuple
      thetaRoot = it.subterm(thetaRoot, 0);
      thetaRootSymbolIndex = it.termSymbolStringIndex(thetaRoot);
    }

    Map<Integer, Integer> dummyBindings = new HashMap<>();
    Set<Integer> tp = terminalPatterns.get(0); // Check default terminal patterns frst
    for (int p : tp)
      if (match(dummyBindings, term, p) != null) return true;

    tp = terminalPatterns.get(relation); // Now check terminals for this relation
    for (var p : tp)
      if (match(dummyBindings, term, p) != null) return true;

    return false;
  }

  HashSet<Integer> evaluateExtend(HashSet<Integer> terms, int relation, int level) {
    // Util.info("evaluate() " + it.toString(term) + " under " + it.toString(relation) + " at level " + level);
    var ret = new HashSet<Integer>();
    for (var term : terms) {
      if (relation == matchRelationTerm) ret.add(term); // For matches, evaluation is degenerate
      if (isTerminatingConfiguration(term, relation)) ret.add(term); // For terminal terms, evaluation is degenerate

      int rootSymbolIndex = it.termSymbolStringIndex(term);

      var compatibleRelation = rules.get(relation);
      if (compatibleRelation == null) Util.error("No rules found for relation " + it.toString(relation));
      var compatibleRules = rules.get(relation).get(rootSymbolIndex);
      if (compatibleRules == null) {
        Util.warning("No rules found for constructor " + it.getString(rootSymbolIndex) + " in relation " + it.toString(relation));
        return ret;
      }

      nextRule: for (var rule : compatibleRules) {
        Util.trace(3, "Rule " + it.toString(rule));
        Map<Integer, Integer> rho = new HashMap<>(); // Accumulate bindings as we go around the clock

        int label = it.subterm(rule, 0);
        int conclusionLHS = it.subterm(rule, 2, 0);
        int conclusionRHS = it.subterm(rule, 2, 2);
        Util.debug("Evaluate: trying rule -" + it.toString(label));
        rho = matchNoStarExtend(rho, term, conclusionLHS);
        Util.debug("Bindings after Theta match " + rho);
        if (rho == null) continue nextRule;
        int[] premises = it.termChildren(it.subterm(rule, 1));
        for (int i = 0; i < premises.length; i++) {
          int premiseLHS = it.subterm(premises[i], 0);
          int premiseRelation = it.subterm(premises[i], 1);
          int premiseRHS = it.subterm(premises[i], 2);

          // Set<Integer> evalateLHS = evaluateExtend(substitute(premiseLHS, rho), premiseRelation, 1);
          // for (var e : evaluateExtend)
          // if (match(rho, e, substitute(premiseRHS, rho)) != null) continue nextRule;
        }
        ret.add(substitute(conclusionRHS, rho));
      }
    }
    return ret;
  }

  // This wrapper function allows us to switch between matching functions - uncomment the one you want
  // We pass in the currentBindings which allows on the fly substitution, and return the extended set of bindings
  // It is the choice of the match function whether to extend and return the currentBindings argument, or make a new return set
  Map<Integer, Integer> match(Map<Integer, Integer> bindings, int closedTerm, int pattern) {
    return matchNoStarExtend(bindings, closedTerm, pattern);
    // return matchOneStar(bindings, closedTerm, pattern);
    // return matchAnyStar(bindings, closedTerm, pattern);
  }

  // This version does not allow any sequence variables, and return an extended form of currentBindings
  // A failed match returns null, otherwise we return extended currentBindings
  Map<Integer, Integer> matchNoStarExtend(Map<Integer, Integer> bindings, int closedTerm, int pattern) {
    Map<Integer, Integer> ret = bindings;

    Util.debug("match() closed term " + it.toString(closedTerm) + " pattern " + it.toString(pattern));
    // Postorder match, so recurse first

    int[] closedChildren = it.termChildren(closedTerm);
    int[] patternChildren = it.termChildren(pattern);

    // Post order traversal of children
    if (closedChildren.length != patternChildren.length) return null; // If there are no sequence variables, then the child arrays must be equal length
    for (int i = 0; i < closedChildren.length; i++)
      if ((ret = match(bindings, closedChildren[i], patternChildren[i])) == null) return null;

    // Collect pattern label and test
    int closedSymbolIndex = it.termSymbolStringIndex(closedTerm);
    String closedSymbolString = it.getString(closedSymbolIndex);
    char closedSymbolPrefix = closedSymbolString.charAt(0);
    if (closedSymbolPrefix == '_' || closedSymbolPrefix == '$') Util.fatal("match() - closed term has variable or function" + it.toString(closedTerm));

    // Collect closedTerm label and test
    int patternSymbolIndex = it.termSymbolStringIndex(pattern);
    String patternSymbolString = it.getString(patternSymbolIndex);
    char patternSymbolPrefix = patternSymbolString.charAt(0);
    if (patternSymbolPrefix == '$') Util.fatal("match() - pattern has function" + it.toString(pattern));

    if (patternSymbolPrefix == '_') { // Process variable
      Integer bound;
      if ((bound = bindings.get(patternSymbolIndex)) != null) {
        Util.warning("match() - pattern is nonlinear on " + patternSymbolString);
        if (bound != closedTerm) return null;
      }
      ret.put(patternSymbolIndex, closedTerm);
      Util.debug("match() return true with bindings " + bindings);
      return ret;
    }

    // if we arrive here, then all children have matched and variables have been processed
    return closedSymbolIndex == patternSymbolIndex ? ret : null;
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
