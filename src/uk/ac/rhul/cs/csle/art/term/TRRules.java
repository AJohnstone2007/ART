package uk.ac.rhul.cs.csle.art.term;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class TRRules implements DisplayInterface {
  // Fields that need to be cloned
  private final Map<Integer, Map<Integer, List<Integer>>> trScriptRules = new LinkedHashMap<>(); // Original rules loaded by user
  private final Map<Integer, Map<Integer, Integer>> configurationMap = new HashMap<>(); // Map from relation to map of config name to config type
  public int defaultStartRelation = 0; // loaded by !start or from first rule seen

  // Working fields initialised by !try or normalise
  private int startRelation;
  Map<Integer, Map<Integer, List<Integer>>> trRules = new LinkedHashMap<>(); // Rewritten rules produced by normalise() and read by rewriter
  private final Map<Integer, Set<Integer>> rewriteTerminals = new HashMap<>();
  private Map<Integer, Integer> termToEnumElementMap;
  private Map<Integer, Integer> enumElementToTermMap;
  private Map<Integer, Integer> termRewriteConstructorDefinitions; // The number of times a constructor appears as the root of a term
  private Map<Integer, Integer> termRewriteConstructorUsages; // The number of times a constructor appears
  private Set<Integer> functionsInUse; // The set of functions in use

  private Map<Integer, Map<Integer, Integer>> variableNamesByRule; // Map from term to the variable aliases used in that term
  Map<Integer, Map<Integer, Integer>> reverseVariableNamesByRule; // Map from term to the variable aliases used in that term
  Map<Integer, Integer> variableMap;

  private boolean normalised = false;

  public TRRules(TRRules payload) { // Copy constructor
    // TODO: unfinished
  }

  public TRRules() {
    super();
  }

  public void addTerminal(int relation, int terminal) {
    // Util.debug("Adding terminal " + ScriptInterpreter.iTerms.toString(terminal) + " in relation " + ScriptInterpreter.iTerms.toString(relation));
    if (rewriteTerminals.get(relation) == null) rewriteTerminals.put(relation, new HashSet<>());
    rewriteTerminals.get(relation).add(terminal);
  }

  public void modifyConfiguration(int term) {
    if (!ScriptInterpreter.iTerms.hasSymbol(term, "!configuration"))
      Util.fatal("Unexpected term passed to TRRules.modifyConfiguration " + ScriptInterpreter.iTerms.toString(term));
    int relation = ScriptInterpreter.iTerms.subterm(term, 0, 0);
    int configurationElements = ScriptInterpreter.iTerms.subterm(term, 1);
    if (configurationMap.get(relation) == null) configurationMap.put(relation, new LinkedHashMap<>());
    var relationConfigurationMap = configurationMap.get(relation);
    for (int i = 0; i < ScriptInterpreter.iTerms.termArity(configurationElements); i++)
      relationConfigurationMap.put(ScriptInterpreter.iTerms.subterm(configurationElements, i, 0),
          ScriptInterpreter.iTerms.subterm(configurationElements, i, 1));
    // Util.info("Updated configuration map to: ");
    // for (var r : configurationMap.keySet()) {
    // System.out.print(ScriptTermInterpreter.iTerms.toRawString(r));
    // Map<Integer, Integer> relationConfigurationElements = configurationMap.get(r);
    // for (var e : relationConfigurationElements.keySet())
    // System.out.print(" " + ScriptTermInterpreter.iTerms.toRawString(e) + ":" +
    // ScriptTermInterpreter.iTerms.toRawString(relationConfigurationElements.get(e)));
    // Util.info();
    // }
  }

  /*
   * Note this is unfinished: the outline infrastructure is in place, and we can call the useType variant, but we are not currently loading the map from the
   * term
   *
   */
  public int unelideConfiguration(int term, int relation, boolean useType) {
    // Util.info("!!!Term before unelision is " + ScriptTermInterpreter.iTerms.toRawString(term));
    if (ScriptInterpreter.iTerms.hasSymbol(term, "trTuple")) {
      // Util.info("Already tupled; returning");
      return term;
    }
    if (configurationMap.get(relation) == null) {
      // Util.warning("Uneliding against relation " + ScriptTermInterpreter.iTerms.toRawString(relation) + " but no corresponding !configuration; skipping");
      return term;
    }
    // Util.info("Uneliding against relation " + ScriptTermInterpreter.iTerms.toRawString(relation) + " " + ScriptTermInterpreter.iTerms.toRawString(term));
    int theta = ScriptInterpreter.iTerms.subterm(term);
    Map<Integer, Integer> relationConfigurationElements = new LinkedHashMap<>(configurationMap.get(relation));

    if (!useType) { // useType is called after parsing to append all of the types from the configuration to the raw parse term
      // Walk the map, setting the bound value to the key
      for (var e : relationConfigurationElements.keySet())
        relationConfigurationElements.put(e, e);

      // Now walk the terms semantic entities, loading the map with each we find
      // !TODO
      for (int i = 1; i < ScriptInterpreter.iTerms.termArity(term); i++)
        // Util.info("found entity: " + ScriptTermInterpreter.iTerms.toRawString(ScriptTermInterpreter.iTerms.subterm(term, i)))
        ;
    }
    // Now reconstitute the term, extracting field names from the map
    LinkedList<Integer> list = new LinkedList<>();
    list.add(theta);
    for (var e : relationConfigurationElements.keySet())
      list.add(relationConfigurationElements.get(e));

    int ret = ScriptInterpreter.iTerms.findTerm("trTuple", list);
    // Util.info("Unelided term: " + ScriptTermInterpreter.iTerms.toRawString(ret));

    // Util.info("!!!Term after unelision is " + ScriptTermInterpreter.iTerms.toRawString(ret));
    return ret;
  }

  public void buildTRRule(int term) {
    // Util.debug("Processing trRule: " + ScriptTermInterpreter.iTerms.toString(term));
    int relation = ScriptInterpreter.iTerms.subterm(term, 1, 1, 1);
    int constructorIndex = ScriptInterpreter.iTerms.termSymbolStringIndex((ScriptInterpreter.iTerms.subterm(term, 1, 1, 0, 0)));
    // Util.debug("Building TR rule " + ScriptTermInterpreter.iTerms.toString(term) + "\nwith relation " + ScriptTermInterpreter.iTerms.toString(relation) +
    // "\nand constructor "
    // + ScriptTermInterpreter.iTerms.getString(constructorIndex));
    if (trScriptRules.get(relation) == null) trScriptRules.put(relation, new HashMap<>());
    Map<Integer, List<Integer>> map = trScriptRules.get(relation);
    if (map.get(constructorIndex) == null) map.put(constructorIndex, new LinkedList<>());
    map.get(constructorIndex).add(term);
    // Util.debug("Added rewrite rule " + ScriptTermInterpreter.iTerms.toRawString(term));
    if (defaultStartRelation == 0) {
      defaultStartRelation = relation;
      // Util.debug("Set start relation to " + ScriptTermInterpreter.iTerms.toRawString(relation));
    }
    normalised = false;
  }

  public String rulesToString(Map<Integer, Map<Integer, List<Integer>>> trRules) {
    StringBuilder sb = new StringBuilder();
    for (int rel : trRules.keySet())
      for (int c : trRules.get(rel).keySet())
        for (int r : trRules.get(rel).get(c)) {
          sb.append(ScriptInterpreter.iTerms.plainTextTraverser.toString(r));
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
        sb.append(ScriptInterpreter.iTerms.toString(ScriptInterpreter.iTerms.findTerm("_" + i), variableMap) + "="
            + ScriptInterpreter.iTerms.toString(bindings[i], variableMap));
        seen = true;
      }
    }
    sb.append(" }");
    return sb.toString();
  }

  boolean isTerminatingConfiguration(int term, int relation) {
    int thetaRoot = thetaFromConfiguration(term);
    Set<Integer> terminals = rewriteTerminals.get(relation);
    return ScriptInterpreter.iTerms.isSpecialTerm(thetaRoot)
        || (terminals != null && terminals.contains(ScriptInterpreter.iTerms.termSymbolStringIndex(thetaRoot)));
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
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("_"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("_*"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("->"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("=>"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("~>"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("true"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("false"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trLabel"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trTransition"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trMatch"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trPremises"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("tr"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trTopTuple"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trTuple"), 1);
    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trRule"), 1);

    termRewriteConstructorDefinitions.put(ScriptInterpreter.iTerms.findString("trRelation"), 1);

    // Util.info("IndexToTerm:" + ((ITermsLowLevelAPI) iTerms).getIndexToTerm());
    for (Integer scanRelationIndex : trRules.keySet()) { // Step through the relations
      // Util.info("Scanning rules for relation " + ScriptTermInterpreter.iTerms.plainTextTraverser.toString(scanRelationIndex));

      // Note: rule root is a symbol not a term

      for (Integer ruleRoot : trRules.get(scanRelationIndex).keySet()) { // Step through constructor symbol strings
        // Util.info("Processing constructor " + ScriptTermInterpreter.iTerms.getString(ruleRoot));
        // Collect the map of rules for this relation
        for (Integer ruleIndex : trRules.get(scanRelationIndex).get(ruleRoot)) {// Step through the list of rules
          if (termRewriteConstructorDefinitions.get(ruleRoot) == null)
            termRewriteConstructorDefinitions.put(ruleRoot, 1);
          else
            termRewriteConstructorDefinitions.put(ruleRoot, termRewriteConstructorDefinitions.get(ruleRoot) + 1);

          // Util.info("Checking for invalid function calls on " + ScriptTermInterpreter.iTerms.toString(ruleIndex));
          reportInvalidFunctionCallsRec(ruleIndex, ScriptInterpreter.iTerms.subterm(ruleIndex, 1, 1, 0));

          Map<Integer, Integer> variableStringIndexToVariableNumberMap = new HashMap<>();
          Map<Integer, Integer> variableNumberMapToVariableStringIndex = new HashMap<>();

          Set<Integer> numericVariablesInUse = new HashSet<>();
          nextFreeVariableNumber = 2;

          collectVariablesAndConstructorsRec(ruleIndex, variableStringIndexToVariableNumberMap, constructorCount, functionsInUse, numericVariablesInUse,
              ruleIndex);

          if (numericVariablesInUse.size() > 0 && variableStringIndexToVariableNumberMap.size() > 0)
            Util.info("*** Error - mix of numeric and alphanumeric variables in " + ScriptInterpreter.iTerms.plainTextTraverser.toString(ruleIndex));
          for (int v : numericVariablesInUse)
            if (!ScriptInterpreter.iTerms.isVariableSymbol(v)) Util.info("*** Error - variable outside available range of _1 to _" + ITerms.variableCount
                + " in " + ScriptInterpreter.iTerms.plainTextTraverser.toString(ruleIndex));
          if (variableStringIndexToVariableNumberMap.size() > ITerms.variableCount) Util
              .info("*** Error - more than " + ITerms.variableCount + " variables used in " + ScriptInterpreter.iTerms.plainTextTraverser.toString(ruleIndex));

          for (int v : variableStringIndexToVariableNumberMap.keySet())
            variableNumberMapToVariableStringIndex.put(variableStringIndexToVariableNumberMap.get(v), v);
          variableNamesByRule.put(ruleIndex, variableStringIndexToVariableNumberMap);
          reverseVariableNamesByRule.put(ruleIndex, variableNumberMapToVariableStringIndex);
        }
      }

      for (int c : constructorCount.keySet())
        if (termRewriteConstructorDefinitions.get(c) == null) {

          String label = ScriptInterpreter.iTerms.getString(c);

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
          Util.warning(
              "in relation " + ScriptInterpreter.iTerms.plainTextTraverser.toString(scanRelationIndex) + " constructor " + label + " has no rule definitions");
        }
    }
    // Stage two - generate rewritten rules to use only only numeric variables to normalise the configurations
    for (int normaliseRelationIndex : trRules.keySet()) { // Step through the relations
      for (Integer thetaRoot : trRules.get(normaliseRelationIndex).keySet()) { // Collect the map of rules for this relation
        List<Integer> newRuleList = new LinkedList<>();
        for (Integer ruleIndex : trRules.get(normaliseRelationIndex).get(thetaRoot)) {// Step through the list of rules
          // Util.info("Normalising rule: " +ScriptTermInterpreter.iTerms.plainTextTraverser.toString(ruleIndex, null));
          int rewrittenRule = normaliseRuleRec(ruleIndex, variableNamesByRule.get(ruleIndex));
          // Util.info("Rewritten to: " +ScriptTermInterpreter.iTerms.plainTextTraverser.toString(rewrittenRule, null));
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
    // Util.info("normaliseRuleRec at " + ScriptTermInterpreter.iTerms.toString(ruleIndex));
    int arity = ScriptInterpreter.iTerms.termArity(ruleIndex);
    int ruleStringIndex = ScriptInterpreter.iTerms.termSymbolStringIndex(ruleIndex);
    // Special case processing for unlabelled rules - generate a label ofthe form Rx
    if (arity == 0 && ScriptInterpreter.iTerms.hasSymbol(ruleIndex, "trLabel")) {
      // Util.info("Generating new label R" + unlabeledRuleNumber);
      int[] newChildren = new int[1];
      newChildren[0] = ScriptInterpreter.iTerms.findTerm("R" + unlabeledRuleNumber++);
      return ScriptInterpreter.iTerms.findTerm(ruleStringIndex, newChildren);
    }

    int[] newChildren = new int[arity];

    if (variableNameMap.get(ruleStringIndex) != null) {
      // Util.info(" rewriting " + ScriptTermInterpreter.iTerms.getString(ruleStringIndex) + " to " +
      // ScriptTermInterpreter.iTerms.getString(variableNameMap.get(ruleStringIndex)));
      ruleStringIndex = variableNameMap.get(ruleStringIndex);
    }

    for (int i = 0; i < arity; i++)
      newChildren[i] = normaliseRuleRec(ScriptInterpreter.iTerms.subterm(ruleIndex, i), variableNameMap);

    return ScriptInterpreter.iTerms.findTerm(ruleStringIndex, newChildren);
  }

  private int nextFreeVariableNumber = 1;

  private void collectVariablesAndConstructorsRec(int parentRewriteTermIndex, Map<Integer, Integer> variableStringIndexToVariableNumberMap,
      Map<Integer, Integer> constructorCount, Set<Integer> functionsInUse, Set<Integer> numericVariablesInUse, Integer termIndex) {
    // Util.info("collectVariablesAndConstructorsRec() at " +ScriptTermInterpreter.iTerms.plainTextTraverser.toString(termIndex, null));

    int termSymbolStringIndex = ScriptInterpreter.iTerms.termSymbolStringIndex(termIndex);
    if (ScriptInterpreter.iTerms.hasSymbol(termIndex, "trLabel")) return; // Do not go down into labels
    String termSymbolString = ScriptInterpreter.iTerms.termSymbolString(termIndex);

    if (termSymbolString.length() > 1 && termSymbolString.charAt(0) == '_' && termSymbolString.charAt(1) != '_') { // Variable
      if (ScriptInterpreter.iTerms.termArity(termIndex) > 0)
        Util.info("*** Error: non-leaf variable " + termSymbolString + " in " + ScriptInterpreter.iTerms.plainTextTraverser.toString(parentRewriteTermIndex));
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
      // Util.info("Updating constructor counts for " + ScriptTermInterpreter.iTerms.getString(termStringIndex));
      constructorCount.put(termSymbolStringIndex, constructorCount.get(termSymbolStringIndex) + 1);
    }

    for (int i = 0; i < ScriptInterpreter.iTerms.termArity(termIndex); i++)
      collectVariablesAndConstructorsRec(parentRewriteTermIndex, variableStringIndexToVariableNumberMap, constructorCount, functionsInUse,
          numericVariablesInUse, ScriptInterpreter.iTerms.subterm(termIndex, i));
  }

  private void reportInvalidFunctionCallsRec(int parentRewriteTermIndex, int termIndex) {
    String termSymbolString = ScriptInterpreter.iTerms.termSymbolString(termIndex);
    int termStringIndex = ScriptInterpreter.iTerms.termSymbolStringIndex(termIndex);
    if (termSymbolString.length() > 0 && termSymbolString.charAt(0) != '_') {
      if (termRewriteConstructorUsages.get(termStringIndex) == null)
        termRewriteConstructorUsages.put(termStringIndex, 1);
      else
        termRewriteConstructorUsages.put(termStringIndex, termRewriteConstructorUsages.get(termStringIndex) + 1);
    }

    for (int i = 0; i < ScriptInterpreter.iTerms.termArity(termIndex); i++)
      reportInvalidFunctionCallsRec(parentRewriteTermIndex, ScriptInterpreter.iTerms.subterm(termIndex, i));
  }
  /* End of variable and function mapping ****************************************************************************/

  int thetaFromConfiguration(int term) {
    return (ScriptInterpreter.iTerms.hasSymbol(term, "trTopTuple") || ScriptInterpreter.iTerms.hasSymbol(term, "trTuple"))
        ? ScriptInterpreter.iTerms.subterm(term, 0)
        : term;
  }

  int thetaLHSFromConfiguration(int term) {
    return ScriptInterpreter.iTerms.subterm(thetaFromConfiguration(term), 0);
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    normalise();
    // TODO Auto-generated method stub

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
