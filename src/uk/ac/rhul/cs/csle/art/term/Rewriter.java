package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.Util;

public class Rewriter {
  private final ITerms iTerms;
  private TRRules tr;
  private int rewriteAttemptCounter;
  private int rewriteStepCounter;
  private final Map<Integer, Set<Integer>> cycleCheck = new HashMap<>();

  public Rewriter(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  private int rewriteAttempt(int term, int relationTerm, int level) { // return rewritten term and set rewriteAttemptOutcome

    if (relationTerm == 0) Util.fatal("ESOS rewrite attempted on null relation");

    Util.trace(4, level,
        "Rewrite attempt " + ++rewriteAttemptCounter + ": " + iTerms.toString(term, tr.variableMap) + " " + iTerms.toString(relationTerm, tr.variableMap));
    if (!cycleCheck.containsKey(relationTerm)) cycleCheck.put(relationTerm, new HashSet<Integer>());
    Set<Integer> cycleSet = cycleCheck.get(relationTerm);
    // if (cycleSet.contains(configuration)) throw new ARTExceptionFatal("cycle detected " +iTerms.toString(configuration)
    // +iTerms.toString(relationTerm));
    cycleSet.add(term);

    if (tr.isTerminatingConfiguration(term, relationTerm)) {
      Util.trace(4, level + 1, "Terminal " + iTerms.toString(term));
      return term;
    }

    int rootTheta = tr.thetaFromConfiguration(term);
    Map<Integer, List<Integer>> rulesForThisRelation = tr.trRules.get(relationTerm);

    List<Integer> ruleList = null;

    if (rulesForThisRelation != null) { // There may be no rules at all in this relation!
      ruleList = rulesForThisRelation.get(iTerms.termSymbolStringIndex(rootTheta));
      if (ruleList == null) ruleList = rulesForThisRelation.get(iTerms.findString("")); // Default rules - 1 Oct 24 What are these?
    }

    if (ruleList == null) {
      Util.error("No rules in relation " + iTerms.toString(relationTerm, tr.variableMap) + " for constructor " + iTerms.termSymbolString(rootTheta));
      return term;
    }

    nextRule: for (int ruleIndex : ruleList) {
      tr.variableMap = tr.reverseVariableNamesByRule.get(ruleIndex);
      // Util.info("Variable map: ");
      // for (int i :rw.variableMap.keySet())
      // Util.info(i + ":" + iTerms.getString(variableMap.get(i)));
      Util.trace(5, level, iTerms.toString(ruleIndex, false, -1, tr.variableMap)); // Announce the next rule we are going to try
      int lhs = iTerms.subterm(ruleIndex, 1, 1, 0);
      int premises = iTerms.subterm(ruleIndex, 1, 0);
      int premiseCount = iTerms.termArity(premises);
      int rhs = iTerms.subterm(ruleIndex, 1, 1, 2);
      int[] bindings = new int[ITerms.variableCount];

      int ruleLabel = tr.thetaFromConfiguration(ruleIndex);
      if (!iTerms.matchZeroSV(term, lhs, bindings)) {
        Util.trace(5, level, iTerms.toString(iTerms.subterm(ruleLabel, 0), tr.variableMap) + " Theta match failed: seek another rule");
        continue nextRule;
      }
      Util.trace(7, level,
          iTerms.toString(iTerms.subterm(ruleLabel, 0), tr.variableMap) + "bindings after Theta match " + tr.bindingsToString(bindings, tr.variableMap));

      // Now work through the premises
      for (int premiseNumber = 0; premiseNumber < premiseCount; premiseNumber++) {
        int premise = iTerms.subterm(premises, premiseNumber);
        Util.trace(6, level,
            iTerms.toString(iTerms.subterm(ruleLabel, 0), tr.variableMap) + "premise " + (premiseNumber + 1) + " " + iTerms.toString(premise, tr.variableMap));
        if (iTerms.hasSymbol(premise, "trMatch")) {// |> match expressions
          if (!iTerms.matchZeroSV(iTerms.substitute(bindings, iTerms.subterm(premise, 0), 0), iTerms.subterm(premise, 1), bindings)) {
            Util.trace(6, level,
                iTerms.toString(iTerms.subterm(ruleLabel, 0), tr.variableMap) + "premise " + (premiseNumber + 1) + " failed: seek another rule");
            continue nextRule;
          }
        } else { // transition
          if (iTerms.hasSymbol(premise, "trTransition")) {
            int rewriteTerm = iTerms.substitute(bindings, tr.thetaLHSFromConfiguration(premise), 0);
            int rewriteRelation = iTerms.subterm(premise, 1);
            int rewrittenTerm = rewriteAttempt(rewriteTerm, rewriteRelation, level + 1);
            if (rewrittenTerm < 0) {
              Util.trace(6, level,
                  iTerms.toString(iTerms.subterm(ruleLabel, 0), tr.variableMap) + "premise" + (premiseNumber + 1) + " failed: seek another rule");
              continue nextRule;
            }
            if (!iTerms.matchZeroSV(rewrittenTerm, iTerms.subterm(premise, 2), bindings)) continue nextRule;
          } else
            Util.fatal("ESOS - unknown premise kind " + iTerms.toString(premise, tr.variableMap));
        }
        Util.trace(7, level, iTerms.toString(iTerms.subterm(ruleLabel, 0), tr.variableMap) + "bindings after premise " + (premiseNumber + 1) + " "
            + tr.bindingsToString(bindings, tr.variableMap));
      }

      term = iTerms.substitute(bindings, rhs, 0);
      Util.trace(4, level, iTerms.toString(iTerms.subterm(ruleLabel, 0), tr.variableMap) + "rewrites to " + iTerms.toString(term, tr.variableMap));
      return term;
    }
    // If we get here, then no rules succeeded
    return term;
  }

  private int stepper(int inputTerm) {
    rewriteAttemptCounter = rewriteStepCounter = 0;

    int oldTerm = inputTerm;
    int relation = tr.defaultStartRelation;
    int newTerm;

    while (true) {
      for (int i : cycleCheck.keySet())
        cycleCheck.get(i).clear();
      Util.trace(3, 0, "Step " + ++rewriteStepCounter + " " + iTerms.toString(oldTerm, false, 4, null));
      newTerm = rewriteAttempt(oldTerm, relation, 1);
      if (tr.isTerminatingConfiguration(newTerm, relation) || newTerm == oldTerm /* nothing changed */) break;
      oldTerm = newTerm;
    }

    Util.trace(2, 0,
        (tr.isTerminatingConfiguration(newTerm, relation) ? "Normal termination on " : "Stuck on ") + iTerms.toString(newTerm, tr.variableMap) + " after "
            + rewriteStepCounter + " step" + (rewriteStepCounter == 1 ? "" : "s") + " and " + rewriteAttemptCounter + " rewrite attempt"
            + (rewriteAttemptCounter == 1 ? "" : "s"));
    return newTerm;
  }

  public int rewrite(int currentDerivationTerm, TRRules rewriteRules) {
    if (currentDerivationTerm == 0) {
      Util.warning("no curent derivation term - skipping rewrites");
      return currentDerivationTerm;
    }

    if (rewriteRules.defaultStartRelation == 0) {
      Util.warning("no rewrite rules defined - skipping rewrites");
      return currentDerivationTerm;
    }
    tr = rewriteRules;
    tr.normalise();

    return stepper(currentDerivationTerm);
  }
}
