package uk.ac.rhul.cs.csle.art.term;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class TRRules implements DisplayInterface {
  private final ITerms iTerms;
  private final Map<Integer, Map<Integer, List<Integer>>> trScriptRules = new LinkedHashMap<>(); // Original rules loaded by user
  public Map<Integer, Map<Integer, List<Integer>>> trRules = new LinkedHashMap<>(); // Rewritten rules produced by normalise()
  Map<Integer, Map<Integer, Integer>> configurationMap = new HashMap<>(); // Map from relation to map of config name to config type
  public int defaultStartRelation = 0; // loaded by !start or from first rule seen

  private final Map<Integer, Set<Integer>> rewriteTerminals = new HashMap<>();
  private int startRelation;
  private Map<Integer, Integer> termToEnumElementMap;
  private Map<Integer, Integer> enumElementToTermMap;
  private Map<Integer, Integer> termRewriteConstructorDefinitions; // The number of times a constructor appears as the root of a term
  private Map<Integer, Integer> termRewriteConstructorUsages; // The number of times a constructor appears
  private Set<Integer> functionsInUse; // The set of functions in use

  private Map<Integer, Map<Integer, Integer>> variableNamesByRule; // Map from term to the variable aliases used in that term
  Map<Integer, Map<Integer, Integer>> reverseVariableNamesByRule; // Map from term to the variable aliases used in that term
  Map<Integer, Integer> variableMap;

  private boolean normalised = false;

  public TRRules(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  public void addTerminal(int relation, int terminal) {
    if (rewriteTerminals.get(relation) == null) rewriteTerminals.put(relation, new HashSet<>());
    rewriteTerminals.get(relation).add(terminal);
  }

  public void modifyConfiguration(int term) {
    if (!iTerms.hasSymbol(term, "configuration")) Util.fatal("Unexpected term passed to TRRules.modifyConfiguration " + iTerms.toString(term));
    int relation = iTerms.subterm(term, 0, 0);
    int configurationElements = iTerms.subterm(term, 1);
    if (configurationMap.get(relation) == null) configurationMap.put(relation, new LinkedHashMap<>());
    var relationConfigurationMap = configurationMap.get(relation);
    for (int i = 0; i < iTerms.termArity(configurationElements); i++)
      relationConfigurationMap.put(iTerms.subterm(configurationElements, i, 0), iTerms.subterm(configurationElements, i, 1));
    // Util.info("Updated configuration map to: ");
    // for (var r : configurationMap.keySet()) {
    // System.out.print(iTerms.toRawString(r));
    // Map<Integer, Integer> relationConfigurationElements = configurationMap.get(r);
    // for (var e : relationConfigurationElements.keySet())
    // System.out.print(" " + iTerms.toRawString(e) + ":" + iTerms.toRawString(relationConfigurationElements.get(e)));
    // Util.info();
    // }
  }

  /*
   * Note this is unfinished: the outline infrastructure is in place, and we can call the useType variant, but we are not currently loading the map from the
   * term
   *
   */
  public int unelideConfiguration(int term, int relation, boolean useType) {
    // Util.info("!!!Term before unelision is " + iTerms.toRawString(term));
    if (iTerms.hasSymbol(term, "trTuple")) {
      // Util.info("Already tupled; returning");
      return term;
    }
    if (configurationMap.get(relation) == null) {
      // Util.warning("Uneliding against relation " + iTerms.toRawString(relation) + " but no corresponding !configuration; skipping");
      return term;
    }
    // Util.info("Uneliding against relation " + iTerms.toRawString(relation) + " " + iTerms.toRawString(term));
    int theta = iTerms.subterm(term);
    Map<Integer, Integer> relationConfigurationElements = new LinkedHashMap<>(configurationMap.get(relation));

    if (!useType) { // useType is called after parsing to append all of the types from the configuration to the raw parse term
      // Walk the map, setting the bound value to the key
      for (var e : relationConfigurationElements.keySet())
        relationConfigurationElements.put(e, e);

      // Now walk the terms semantic entities, loading the map with each we find
      // !TODO
      for (int i = 1; i < iTerms.termArity(term); i++)
        // Util.info("found entity: " + iTerms.toRawString(iTerms.subterm(term, i)))
        ;
    }
    // Now reconstitute the term, extracting field names from the map
    LinkedList<Integer> list = new LinkedList<>();
    list.add(theta);
    for (var e : relationConfigurationElements.keySet())
      list.add(relationConfigurationElements.get(e));

    int ret = iTerms.findTerm("trTuple", list);
    // Util.info("Unelided term: " + iTerms.toRawString(ret));

    // Util.info("!!!Term after unelision is " + iTerms.toRawString(ret));
    return ret;
  }

  public void buildTRRule(int term) {
    // Util.debug("Processing trRule: " + iTerms.toString(term));
    int relation = iTerms.subterm(term, 1, 1, 1);
    int constructorIndex = iTerms.termSymbolStringIndex((iTerms.subterm(term, 1, 1, 0, 0)));
    // Util.debug("Building TR rule " + iTerms.toString(term) + "\nwith relation " + iTerms.toString(relation) + "\nand constructor "
    // + iTerms.getString(constructorIndex));
    if (trScriptRules.get(relation) == null) trScriptRules.put(relation, new HashMap<>());
    Map<Integer, List<Integer>> map = trScriptRules.get(relation);
    if (map.get(constructorIndex) == null) map.put(constructorIndex, new LinkedList<>());
    map.get(constructorIndex).add(term);
    // Util.debug("Added rewrite rule " + iTerms.toRawString(term));
    if (defaultStartRelation == 0) {
      defaultStartRelation = relation;
      // Util.debug("Set start relation to " + iTerms.toRawString(relation));
    }
    normalised = false;
  }

  public String rulesToString(Map<Integer, Map<Integer, List<Integer>>> trRules) {
    StringBuilder sb = new StringBuilder();
    for (int rel : trRules.keySet())
      for (int c : trRules.get(rel).keySet())
        for (int r : trRules.get(rel).get(c)) {
          sb.append(iTerms.plainTextTraverser.toString(r));
          sb.append("\n");
        }
    return sb.toString();
  }

  String bindingsToString(int[] bindings, Map<Integer, Integer> variableMap) {
    StringBuilder sb = new StringBuilder();
    boolean seen = false;
    sb.append("{ ");
    for (int i = 0; i < bindings.length; i++) {
      if (bindings[i] > 0) {
        if (seen) sb.append(", ");
        sb.append(iTerms.toString(iTerms.findTerm("_" + i), variableMap) + "=" + iTerms.toString(bindings[i], variableMap));
        seen = true;
      }
    }
    sb.append(" }");
    return sb.toString();
  }

  boolean isTerminatingConfiguration(int term, int relation) {
    int thetaRoot = thetaFromConfiguration(term);
    Set<Integer> terminals = rewriteTerminals.get(relation);
    return iTerms.isSpecialTerm(thetaRoot) || (terminals != null && terminals.contains(iTerms.termSymbolStringIndex(thetaRoot)));
  }

  public void normalise() {
    // Util.info("normalising");
    if (normalised) {
      // Util.info("nothing to do ");
      return;
    }
    trRules = new HashMap<>(trScriptRules);
    startRelation = 0;
    termToEnumElementMap = new HashMap<>();
    enumElementToTermMap = new HashMap<>();
    termRewriteConstructorDefinitions = new HashMap<>(); // The number of times a constructor appears as the root of a term
    termRewriteConstructorUsages = new HashMap<>(); // The number of times a constructor appears
    functionsInUse = new HashSet<>(); // The set of functions in use
    variableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term
    reverseVariableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term
    Map<Integer, Integer> constructorCount = new HashMap<>(); // The number of defined rules for each constructor Map<Integer, Integer>

    // Stage one - collect information
    termRewriteConstructorDefinitions.put(iTerms.findString("_"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("_*"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("->"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("=>"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("~>"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("true"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("false"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trLabel"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trTransition"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trMatch"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trPremises"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("tr"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trTopTuple"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trTuple"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trRule"), 1);

    termRewriteConstructorDefinitions.put(iTerms.findString("trRelation"), 1);

    // Util.info("IndexToTerm:" + ((ITermsLowLevelAPI) iTerms).getIndexToTerm());
    for (Integer scanRelationIndex : trRules.keySet()) { // Step through the relations
      // Util.info("Scanning rules for relation " + iTerms.plainTextTraverser.toString(scanRelationIndex));

      // Note: rule root is a symbol not a term

      for (Integer ruleRoot : trRules.get(scanRelationIndex).keySet()) { // Step through constructor symbol strings
        // Util.info("Processing constructor " + iTerms.getString(ruleRoot));
        // Collect the map of rules for this relation
        for (Integer ruleIndex : trRules.get(scanRelationIndex).get(ruleRoot)) {// Step through the list of rules
          if (termRewriteConstructorDefinitions.get(ruleRoot) == null)
            termRewriteConstructorDefinitions.put(ruleRoot, 1);
          else
            termRewriteConstructorDefinitions.put(ruleRoot, termRewriteConstructorDefinitions.get(ruleRoot) + 1);

          // Util.info("Checking for invalid function calls on " + iTerms.toString(ruleIndex));
          reportInvalidFunctionCallsRec(ruleIndex, iTerms.subterm(ruleIndex, 1, 1, 0));

          Map<Integer, Integer> variableStringIndexToVariableNumberMap = new HashMap<>();
          Map<Integer, Integer> variableNumberMapToVariableStringIndex = new HashMap<>();

          Set<Integer> numericVariablesInUse = new HashSet<>();
          nextFreeVariableNumber = 2;

          collectVariablesAndConstructorsRec(ruleIndex, variableStringIndexToVariableNumberMap, constructorCount, functionsInUse, numericVariablesInUse,
              ruleIndex);

          if (numericVariablesInUse.size() > 0 && variableStringIndexToVariableNumberMap.size() > 0)
            Util.info("*** Error - mix of numeric and alphanumeric variables in " + iTerms.plainTextTraverser.toString(ruleIndex));
          for (int v : numericVariablesInUse)
            if (!iTerms.isVariableSymbol(v)) Util.info(
                "*** Error - variable outside available range of _1 to _" + ITerms.variableCount + " in " + iTerms.plainTextTraverser.toString(ruleIndex));
          if (variableStringIndexToVariableNumberMap.size() > ITerms.variableCount)
            Util.info("*** Error - more than " + ITerms.variableCount + " variables used in " + iTerms.plainTextTraverser.toString(ruleIndex));

          for (int v : variableStringIndexToVariableNumberMap.keySet())
            variableNumberMapToVariableStringIndex.put(variableStringIndexToVariableNumberMap.get(v), v);
          variableNamesByRule.put(ruleIndex, variableStringIndexToVariableNumberMap);
          reverseVariableNamesByRule.put(ruleIndex, variableNumberMapToVariableStringIndex);
        }
      }

      for (int c : constructorCount.keySet())
        if (termRewriteConstructorDefinitions.get(c) == null) {

          String label = iTerms.getString(c);

          if (label.charAt(0) == '"') continue;

          boolean isNumber = true;
          int i = 0;
          if (label.charAt(i) == '-') i++;
          for (; i < label.length(); i++) {
            char ch = label.charAt(i);
            if (!Character.isDigit(ch) && ch != '.') isNumber = false;
          }

          if (isNumber) continue;

          var terminals = rewriteTerminals.get(scanRelationIndex);
          if (terminals != null && terminals.contains(c)) continue;
          Util.warning("in relation " + iTerms.plainTextTraverser.toString(scanRelationIndex) + " constructor " + label + " has no rule definitions");
        }
    }
    // Stage two - generate rewritten rules to use only only numeric variables to normalise the configurations
    for (int normaliseRelationIndex : trRules.keySet()) { // Step through the relations
      for (Integer thetaRoot : trRules.get(normaliseRelationIndex).keySet()) { // Collect the map of rules for this relation
        List<Integer> newRuleList = new LinkedList<>();
        for (Integer ruleIndex : trRules.get(normaliseRelationIndex).get(thetaRoot)) {// Step through the list of rules
          // Util.info("Normalising rule: " +iTerms.plainTextTraverser.toString(ruleIndex, null));
          int rewrittenRule = normaliseRuleRec(ruleIndex, variableNamesByRule.get(ruleIndex));
          // Util.info("Rewritten to: " +iTerms.plainTextTraverser.toString(rewrittenRule, null));
          newRuleList.add(rewrittenRule);
          // Add in map entries for the rewritten term, which will be the same as for the original rule!
          variableNamesByRule.put(rewrittenRule, variableNamesByRule.get(ruleIndex));
          reverseVariableNamesByRule.put(rewrittenRule, reverseVariableNamesByRule.get(ruleIndex));
        }
        trRules.get(normaliseRelationIndex).put(thetaRoot, newRuleList);
      }
    }
    normalised = true;
  }

  /* Variable and function mapping ****************************************************************************/
  private int unlabeledRuleNumber = 1;

  private int normaliseRuleRec(Integer ruleIndex, Map<Integer, Integer> variableNameMap) {
    // Util.info("normaliseRuleRec at " + iTerms.toString(ruleIndex));
    int arity = iTerms.termArity(ruleIndex);
    int ruleStringIndex = iTerms.termSymbolStringIndex(ruleIndex);
    // Special case processing for unlabelled rules - generate a label ofthe form Rx
    if (arity == 0 && iTerms.hasSymbol(ruleIndex, "trLabel")) {
      // Util.info("Generating new label R" + unlabeledRuleNumber);
      int[] newChildren = new int[1];
      newChildren[0] = iTerms.findTerm("R" + unlabeledRuleNumber++);
      return iTerms.findTerm(ruleStringIndex, newChildren);
    }

    int[] newChildren = new int[arity];

    if (variableNameMap.get(ruleStringIndex) != null) {
      // Util.info(" rewriting " + iTerms.getString(ruleStringIndex) + " to " + iTerms.getString(variableNameMap.get(ruleStringIndex)));
      ruleStringIndex = variableNameMap.get(ruleStringIndex);
    }

    for (int i = 0; i < arity; i++)
      newChildren[i] = normaliseRuleRec(iTerms.subterm(ruleIndex, i), variableNameMap);

    return iTerms.findTerm(ruleStringIndex, newChildren);
  }

  private int nextFreeVariableNumber = 1;

  private void collectVariablesAndConstructorsRec(int parentRewriteTermIndex, Map<Integer, Integer> variableStringIndexToVariableNumberMap,
      Map<Integer, Integer> constructorCount, Set<Integer> functionsInUse, Set<Integer> numericVariablesInUse, Integer termIndex) {
    // Util.info("collectVariablesAndConstructorsRec() at " +iTerms.plainTextTraverser.toString(termIndex, null));

    int termSymbolStringIndex = iTerms.termSymbolStringIndex(termIndex);
    if (iTerms.hasSymbol(termIndex, "trLabel")) return; // Do not go down into labels
    String termSymbolString = iTerms.termSymbolString(termIndex);

    if (termSymbolString.length() > 1 && termSymbolString.charAt(0) == '_' && termSymbolString.charAt(1) != '_') { // Variable
      if (iTerms.termArity(termIndex) > 0)
        Util.info("*** Error: non-leaf variable " + termSymbolString + " in " + iTerms.plainTextTraverser.toString(parentRewriteTermIndex));
      boolean isNumeric = true;
      for (int i = 1; i < termSymbolString.length(); i++)
        if (termSymbolString.charAt(i) < '0' || termSymbolString.charAt(i) > '9') isNumeric = false;
      if (isNumeric) {
        // Util.info("Updating numericVariablesInUse with " + termSymbolString);
        numericVariablesInUse.add(termSymbolStringIndex);
      } else if (variableStringIndexToVariableNumberMap.get(termSymbolStringIndex) == null) {
        // Util.info("Updating variableNumbers with " + termSymbolString + " mapped to " + nextFreeVariableNumber);
        // variableNumbers.put(nextFreeVariableNumber++, termStringIndex);
        variableStringIndexToVariableNumberMap.put(termSymbolStringIndex, nextFreeVariableNumber++);
      }
    } else if (termSymbolString.length() > 1 && termSymbolString.charAt(0) == '_' && termSymbolString.charAt(1) == '_') { // Function
      // Util.info("Updating functionsInUse with " + termSymbolString);
      functionsInUse.add(termSymbolStringIndex);
    } else { // Normal constructor
      if (constructorCount.get(termSymbolStringIndex) == null) constructorCount.put(termSymbolStringIndex, 0);
      // Util.info("Updating constructor counts for " + iTerms.getString(termStringIndex));
      constructorCount.put(termSymbolStringIndex, constructorCount.get(termSymbolStringIndex) + 1);
    }

    for (int i = 0; i < iTerms.termArity(termIndex); i++)
      collectVariablesAndConstructorsRec(parentRewriteTermIndex, variableStringIndexToVariableNumberMap, constructorCount, functionsInUse,
          numericVariablesInUse, iTerms.subterm(termIndex, i));
  }

  private void reportInvalidFunctionCallsRec(int parentRewriteTermIndex, int termIndex) {
    String termSymbolString = iTerms.termSymbolString(termIndex);
    int termStringIndex = iTerms.termSymbolStringIndex(termIndex);
    if (termSymbolString.length() > 0 && termSymbolString.charAt(0) != '_') {
      if (termRewriteConstructorUsages.get(termStringIndex) == null)
        termRewriteConstructorUsages.put(termStringIndex, 1);
      else
        termRewriteConstructorUsages.put(termStringIndex, termRewriteConstructorUsages.get(termStringIndex) + 1);
    }

    for (int i = 0; i < iTerms.termArity(termIndex); i++)
      reportInvalidFunctionCallsRec(parentRewriteTermIndex, iTerms.subterm(termIndex, i));
  }
  /* End of variable and function mapping ****************************************************************************/

  int thetaFromConfiguration(int term) {
    return (iTerms.hasSymbol(term, "trTopTuple") || iTerms.hasSymbol(term, "trTuple")) ? iTerms.subterm(term, 0) : term;
  }

  int thetaLHSFromConfiguration(int term) {
    return iTerms.subterm(thetaFromConfiguration(term), 0);
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
