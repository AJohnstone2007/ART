package uk.ac.rhul.cs.csle.art.esos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLParserBase;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerFile;
import uk.ac.rhul.cs.csle.art.value.ARTValue;
import uk.ac.rhul.cs.csle.art.value.ARTValueException;
import uk.ac.rhul.cs.csle.art.value.ARTValueOp;
import uk.ac.rhul.cs.csle.art.value.ARTValueString;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermUsingList;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermVariable;

public class ARTeSOSSpecification {
  // Console trace levels: zero - silent, 1 - final configuration, 2 - steps, 3 - condition evaluation, 4 - static data structures, 5 - debug
  private static int traceLevel = 1;
  private int fSOSCounter; // Total number of calls to fSOS
  private static int fSOSCounterLimit = 0; // Set to nonzero integer to stop interpreter early

  final private ARTGLLParserBase parser;
  private static Integer nextLabelNumber = 1;
  public final static ARTeSOSName termKey = new ARTeSOSName("theta");
  final private static Map<String, String> latexMap = new HashMap<String, String>(); // Translations from text strings to LaTeX renditions
  private static int errorCount = 0;
  final private Map<String, ARTeSOSRelation> relations = new HashMap<>();
  final private Map<ARTeSOSName, Integer> constructorNames = new HashMap<>(); // Constructor names mapped to their arity
  final private Map<ARTeSOSName, Integer> functionNames = new HashMap<>(); // Function names mapped to their arity
  final private Set<ARTeSOSName> functionsInUse = new HashSet<>();
  final private List<ARTeSOSRule> rules = new ArrayList<ARTeSOSRule>();
  final private List<ARTeSOSTest> tests = new ArrayList<ARTeSOSTest>();
  static final private ARTeSOSConfiguration fullConfiguration = new ARTeSOSConfiguration();
  static final Map<ARTeSOSName, Integer> priorityNumbers = new TreeMap<>();

  ARTeSOSSpecification(ARTGLLParserBase parser) {
    this.parser = parser;
    addLaTeXName("theta", "\\theta");
    addLaTeXName("|>", "\\triangleright");
    addLaTeXName("true", "\\textsf{\\textit{true}}");
    addLaTeXName("false", "\\textsf{\\textit{false}}");
    // Automatically add operations from the ARTValue package
    for (ARTValueOp op : ARTValueOp.values()) {
      String opName = op.toString();

      if (opName.startsWith("is"))
        addFunctionName(opName, 1);
      else
        addFunctionName(opName + "Op", 2);
    }
    // Now do special functions
    addFunctionName("isTerminal", 2);
    addFunctionName("isNonterminal", 2);
  }

  @Override
  public String toString() {
    String ret = "(*\n\n";

    ret += "Configuration" + fullConfiguration + "\n\n";

    ret += "Relations\n";
    for (String r : new TreeSet<String>(relations.keySet()))
      ret += relations.get(r) + "\n";

    ret += "\nConstructors\n";
    for (ARTeSOSName c : new TreeSet<ARTeSOSName>(constructorNames.keySet()))
      ret += c.toString() + "\n";

    ret += "\nFunctions referenced\n";
    for (ARTeSOSName f : new TreeSet<ARTeSOSName>(functionsInUse))
      ret += f.toString() + "\n";
    ret += "*)\n";

    ret += "\n";

    for (ARTeSOSRule r : rules)
      ret += r.toString();

    ret += "\n";

    for (ARTeSOSTest t : tests)
      ret += t.toString();

    return ret;
  }

  private String toLatexString(Map<String, String> map) throws ARTValueException {
    String ret = "";

    ret += "{\\bf configuration} $" + fullConfiguration.toLatexString(map) + "$\n\n";

    for (String r : new TreeSet<String>(relations.keySet())) {
      ret += "{\\bf relation} $" + relations.get(r).toLatexString(map) + "\\quad" + relations.get(r).getConfiguration().toLatexStringKeys(map) + "$\\quad";
      for (ARTeSOSName k : relations.get(r).getThetaTerminalNames().keySet())
        ret += k.toLatexString(map);
      ret += "\n\n";
    }

    for (ARTeSOSName c : new TreeSet<ARTeSOSName>(constructorNames.keySet()))
      ret += "{\\bf constructor} $" + c.toLatexString(map) + "$\n\n";

    for (ARTeSOSName f : new TreeSet<ARTeSOSName>(functionsInUse))
      ret += "{\\bf function} $" + f.toLatexString(map) + "$\n\n";

    ret += "\\clearpage\n";

    for (ARTeSOSRule r : rules)
      ret += r.toLatexString(map);

    ret += "\n";

    // for (ARTeSOSTest t : tests)
    // ret += t.toLatexString(map);

    return ret;
  }

  public void printAsText(String filename) throws ARTException {
    ARTText textHandler = new ARTText(new ARTTextHandlerFile(filename));
    textHandler.print(this.toString());
    textHandler.close();
  }

  public void printAsLaTeX(String filename) throws ARTException {
    ARTText laTeXHandler = new ARTText(new ARTTextHandlerFile(filename));
    laTeXHandler.print(this.toLatexString(latexMap));
    laTeXHandler.close();
  }

  public static Map<String, String> getLatexmap() {
    return latexMap;
  }

  public static ARTeSOSConfiguration getFullconfiguration() {
    return fullConfiguration;
  }

  public static Object getNextLabelNumber() {
    return nextLabelNumber++;
  }

  public static void trace(int level, String string) {
    if (traceLevel >= level) System.out.println("  " + string);
  }

  public static void addLaTeXName(String string, String l) {
    latexMap.put(string, l.trim().replaceAll("_", "\\\\_") + " ");
  }

  public static int getErrorCount() {
    return errorCount;
  }

  public static void info(String msg) {
    System.out.println("\n" + msg);
  }

  public static void error(String msg) {
    errorCount++;
    System.out.println("\nError: " + msg);
  }

  public static void errorCheckAbort() {
    if (errorCount > 0) {
      System.out.println("\nError count: " + errorCount + "; terminating");
      System.exit(1);
    }
  }

  public void addConstructorName(ARTValueTermUsingList v) {
    if (!(v.getPayload() instanceof ARTeSOSName)) error("internal error: attempt to add a constructor name which is not a name");
    ARTeSOSName n = (ARTeSOSName) v.getPayload();
    constructorNames.put(n, v.getChildren().size());
    trace(5, "Adding constructor " + n.toString());
    addLaTeXName(n.toString(), "\\textsf{" + n.toString() + "}");
  }

  public void addFunctionName(String name, int arity) {
    ARTeSOSName n = new ARTeSOSName(name);
    functionNames.put(n, arity);
    trace(5, "Adding function " + n.toString());
    addLaTeXName(n.toString(), "\\texttt{" + n.toString() + "}");
  }

  public ARTeSOSRelation findRelation(String v) throws ARTException {
    ARTeSOSRelation ret = new ARTeSOSRelation(v);
    if (!relations.containsKey(v)) relations.put(v, ret);
    return relations.get(v);
  }

  public ARTeSOSRelation findRelation(String v, boolean s) throws ARTException {
    ARTeSOSRelation ret = findRelation(v);
    ret.setIsStarred(s);
    return ret;
  }

  public void addRule(ARTeSOSRule r) throws ARTValueException {
    trace(5, "Adding rule " + r);
    rules.add(r);
  }

  public void addTest(String name, int traceLevel, ARTeSOSRelation relation, ARTValueTermUsingList p) {
    ARTeSOSTest test = new ARTeSOSTest(name, traceLevel, relation, p, null);
    trace(5, "Adding test " + test);
    tests.add(test);
  }

  public ARTValueTermUsingList constructSpecialFunctionTerm(String rootLabel, String relation, ARTValueTermUsingList putativeTerminal) {
    ARTValueTermUsingList ret = new ARTValueTermUsingList(new ARTeSOSName(rootLabel));
    ret.addChild(new ARTValueTermUsingList(new ARTValueString(relation)));
    ret.addChild(putativeTerminal);
    return ret;
  }

  private void createPriorityMapEntries(ARTeSOSName name) {
    if (!workingRelationMap.containsKey(name)) workingRelationMap.put(name, new TreeSet<ARTeSOSName>());
    if (!priorityGT.containsKey(name)) priorityGT.put(name, new TreeSet<ARTeSOSName>());
    if (!priorityEQ.containsKey(name)) {
      priorityEQ.put(name, new TreeSet<ARTeSOSName>());
      priorityEQ.get(name).add(name);
    }
  }

  public void addPriorityGT(ARTeSOSName left, ARTeSOSName right) {
    createPriorityMapEntries(left);
    createPriorityMapEntries(right);
    priorityGT.get(left).add(right);
  }

  public void addPriorityEQ(ARTeSOSName left, ARTeSOSName right) {
    createPriorityMapEntries(left);
    createPriorityMapEntries(right);
    priorityEQ.get(left).add(right);
    priorityEQ.get(right).add(left);
  }

  public static Comparator<ARTeSOSRule> PriorityComparator = new Comparator<ARTeSOSRule>() {

    @Override
    public int compare(ARTeSOSRule left, ARTeSOSRule right) {
      return -Integer.compare(priorityNumbers.get(left.getPriority()), priorityNumbers.get(right.getPriority()));
    }
  };

  /***
   * Induction and static checker routines
   *
   * @throws ARTException
   ***/
  final Map<ARTeSOSName, Set<ARTeSOSName>> priorityGT = new TreeMap<>(); // Use tree maps to ensure sorted debugging output
  final Map<ARTeSOSName, Set<ARTeSOSName>> workingRelationMap = new TreeMap<>();
  final Map<ARTeSOSName, Set<ARTeSOSName>> priorityEQ = new TreeMap<>();

  void copyGTToWorkingRelation() {
    for (ARTeSOSName p : workingRelationMap.keySet()) {
      workingRelationMap.get(p).clear();
      workingRelationMap.get(p).addAll(priorityGT.get(p));
    }
  }

  void copyWorkingRelationToEQ() {
    for (ARTeSOSName p : priorityEQ.keySet()) {
      priorityEQ.get(p).clear();
      priorityEQ.get(p).addAll(workingRelationMap.get(p));
    }
  }

  void copyWorkingRelationToGT() {
    for (ARTeSOSName p : priorityGT.keySet()) {
      priorityGT.get(p).clear();
      priorityGT.get(p).addAll(workingRelationMap.get(p));
    }
  }

  // Iterate over all of the elements of name's equivalence class, setting their priorities to the hightest value seen so far. Note that there can be no cycles.
  private int setPriorityNumber(ARTeSOSName name, int level) {
    for (ARTeSOSName e : priorityEQ.get(name)) {
      if (priorityNumbers.get(e) == null || priorityNumbers.get(e) < level) {
        // System.out.println("Adjusting priority " + e + " from " + priorityNumbers.get(e) + " to " + level);
        priorityNumbers.put(e, level);
      }
    }
    return level;
  }

  // Recursively follow all paths from this name, allocatting priorities post-order
  private int allocatePriority(ARTeSOSName e) {
    int ret = -1; // -1 should never be returned, but the data flow analyser cannot prove that
    if (priorityGT.get(e).size() == 0) return setPriorityNumber(e, 1);
    for (ARTeSOSName f : priorityEQ.get(e))
      for (ARTeSOSName g : priorityGT.get(f))
        setPriorityNumber(f, ret = (allocatePriority(g)) + 1);

    return ret;
  }

  private void computePriorities() throws ARTException {
    boolean changed;
    // At entry, maps EQ, GT and GTprime have already all been populated: each priority name is bound to an empty set
    // System.out.println("After parsing:\n EQ: " + priorityEQ + "\n WR:" + workingRelationMap + "\n GT:" + priorityGT);

    // Transitively close EQ - reflexive closure has been applied by parser
    do {
      changed = false;
      for (ARTeSOSName p : priorityEQ.keySet())
        for (ARTeSOSName q : priorityEQ.get(p))
          for (ARTeSOSName r : priorityEQ.get(q))
            if (!workingRelationMap.get(p).contains(r)) {
              changed = true;
              workingRelationMap.get(p).add(r);
            }
      copyWorkingRelationToEQ();
    } while (changed);

    // System.out.println("After equality closure:\n EQ: " + priorityEQ + "\n WR:" + workingRelationMap + "\n GT:" + priorityGT);

    // Transitively close GT under EQ
    Set<ARTeSOSName> qSet = new HashSet<>();
    Set<ARTeSOSName> rSet = new HashSet<>();
    copyGTToWorkingRelation();
    do {
      changed = false;
      for (ARTeSOSName p : priorityGT.keySet()) { // Iterate over all priority names
        qSet.clear();
        rSet.clear();
        for (ARTeSOSName pp : priorityEQ.get(p)) {// Iterate over all names that are equal to p so as to catch otherwise unnmentioned = elements
          // System.out.println("Whilst accumulating qSet, pp is " + pp + " and prioirityGT(pp) is " + priorityGT.get(pp));
          qSet.addAll(priorityGT.get(pp)); // qSet accumulates all of the elements that element p can reach within GT in one hop
        }
        for (ARTeSOSName qq : qSet)
          rSet.addAll(priorityGT.get(qq)); // rSet accumulates all of the elements that element p can reach within GT in two hop

        // System.out.println("For element " + p + ", qSet is " + qSet + ", rSet is " + rSet);

        for (ARTeSOSName rr : rSet)
          if (!workingRelationMap.get(p).contains(rr)) {
            changed = true;
            workingRelationMap.get(p).add(rr);
          }
      }
      copyWorkingRelationToGT();
    } while (changed);

    // System.out.println("After GT closure:\n EQ: " + priorityEQ + "\n WR:" + workingRelationMap + "\n GT:" + priorityGT);

    // Test for cycles
    for (ARTeSOSName p : priorityGT.keySet())
      if (priorityGT.get(p).contains(p)) throw new ARTException("eSOS priority relation has a cycle on " + p);

    // Test for totality------------------
    Set<ARTeSOSName> topElements = new TreeSet<>(priorityGT.keySet());
    for (ARTeSOSName p : priorityGT.keySet())
      for (ARTeSOSName q : priorityGT.get(p))
        topElements.remove(q);

    // System.out.println("priorityGT topElements = " + topElements);

    // Check to see if all of the top elements are equal
    boolean allEqual = true;
    for (ARTeSOSName s : topElements)
      for (ARTeSOSName t : topElements)
        if (!priorityEQ.get(s).contains(t)) allEqual = false;

    if (topElements.size() > 1 && !allEqual) throw new ARTException("eSOS priority relation has multiple top elements: " + topElements);

    // Allocate priority numbers
    for (ARTeSOSName e : topElements)
      allocatePriority(e);

    // System.out.print("Priority numbers: " + priorityNumbers + "\n");
  }

  public void induceRules() throws ARTException {
    // traceLevel = 5; // debug only
    trace(5, "Inducing full rule set");
    // 0. Dump out the raw rules
    printAsText("eSOSRulesRaw.txt");
    printAsLaTeX("eSOSRulesRaw.tex");

    // 1. Traverse all relations and collect configuration as union over all relation configurations
    // Collect entities from the global relation, and then remove the global relation
    if (relations.containsKey("")) {
      Map<ARTValue, ARTValue> globalConfigurationMap = relations.get("").configuration.getPayload();

      for (ARTValue e : globalConfigurationMap.keySet())
        fullConfiguration.addEntity((ARTeSOSName) e, (ARTValueTermUsingList) globalConfigurationMap.get(e));

      relations.remove("");
    }

    // Now go through all the user-defined relations as well
    for (String r : relations.keySet()) {
      Map<ARTValue, ARTValue> configurationMap = relations.get(r).configuration.getPayload();
      for (ARTValue e : configurationMap.keySet())
        fullConfiguration.addEntity((ARTeSOSName) e, (ARTValueTermUsingList) configurationMap.get(e));
    }
    trace(5, "Full configuration is " + fullConfiguration);
    // traceLevel = 1; // debug only

    // 2. Lift expressions within transitions into side-conditions
    // Note that there can't be expressions in incoming term so perhaps there shouldn't be conditions in the conclusion's left hand side

    for (ARTeSOSRule r : rules) {
    }

    // 3. Propgate entities to elided configurations in rules

    // Make index map with an entry for each entity in the full configuration
    Map<ARTeSOSName, Integer> configurationIndices = new HashMap<>();

    // Scan over all of the rules
    for (ARTeSOSRule r : rules) {
      // Clear all of the indices (andcreate them the first time round)
      for (ARTValue e : fullConfiguration.getPayload().keySet())
        configurationIndices.put((ARTeSOSName) e, 1);

      // Set index map bindings to start above the highest index used in the rule
      setConfigurationIndexMapBindings(configurationIndices, r.getConclusion());
      for (ARTeSOSCondition c : r.getConditions())
        setConfigurationIndexMapBindings(configurationIndices, c);

      // Now go round the clock, inducing (filling in missing) entities and updating indices as we fo
      induceEntities(configurationIndices, r.getConclusion().getLhs(), false);
      for (ARTeSOSCondition c : r.getConditions())
        if (c instanceof ARTeSOSTransition) {
          induceEntities(configurationIndices, ((ARTeSOSTransition) c).getLhs(), false);
          induceEntities(configurationIndices, ((ARTeSOSTransition) c).getRhs(), true);
        }
      induceEntities(configurationIndices, r.getConclusion().getRhs(), false);
    }

    // 4. Check priority ordering and sort rules
    computePriorities();
    // rules.sort(PriorityComparator);

    // System.out.println("Prioritised rules\n" + rules);
    // 5. Build the rule map for each relation
    for (ARTeSOSRule r : rules) {
      Map<ARTeSOSName, LinkedList<ARTeSOSRule>> relationRuleMap = r.getConclusion().getRelation().getRuleMap();
      ARTeSOSName key = (ARTeSOSName) ((ARTValueTermUsingList) r.getConclusion().getLhs().valueKey(termKey)).getPayload();

      // Make sure we know about this constructor name and its arity
      addConstructorName((ARTValueTermUsingList) r.getConclusion().getLhs().valueKey(termKey));

      // Now build the list of rules for this relation/constructor pair
      LinkedList<ARTeSOSRule> ruleList;
      if ((ruleList = relationRuleMap.get(key)) == null) relationRuleMap.put(key, ruleList = new LinkedList<ARTeSOSRule>());
      ruleList.add(r);
    }

    // 4. Static check
    staticCheck();

    // 7. Dump out the cooked rules
    printAsText("eSOSRules.txt");
    printAsLaTeX("eSOSRules.tex");
  }

  private void induceEntities(Map<ARTeSOSName, Integer> configurationIndices, ARTeSOSConfiguration configuration, boolean bumpIndices) {
    for (ARTeSOSName e : configurationIndices.keySet())
      if (configuration.getPayload().get(e) == null) {
        configurationIndices.put(e, configurationIndices.get(e) + (bumpIndices ? 1 : 0));
        configuration.getPayload().put(e, new ARTValueTermUsingList(e.base(configurationIndices.get(e))));
      }
  }

  private void setConfigurationIndexMapBindings(Map<ARTeSOSName, Integer> configurationIndices, ARTeSOSConfiguration c) throws ARTException {
    for (ARTValue e : c.getPayload().keySet()) {
      ARTeSOSName entityName = (ARTeSOSName) e;
      ARTeSOSName base = entityName.base();
      if (configurationIndices.get(base) == null) throw new ARTException("entity " + base + " not found in configuration " + c);

      if (entityName.subscript >= configurationIndices.get(base)) configurationIndices.put(base, entityName.subscript + 1);
    }
  }

  private void setConfigurationIndexMapBindings(Map<ARTeSOSName, Integer> configurationIndices, ARTeSOSCondition condition) throws ARTException {
    if (condition instanceof ARTeSOSTransition) {
      setConfigurationIndexMapBindings(configurationIndices, ((ARTeSOSTransition) condition).getLhs());
      setConfigurationIndexMapBindings(configurationIndices, ((ARTeSOSTransition) condition).getRhs());
    }
  }

  private void staticCheckTerm(Map<ARTeSOSName, Integer> thetaTerminalNames, ARTValueTermUsingList term, Set<ARTValueTermVariable> definedMetaVariables,
      boolean isExpression) throws ARTException {
    if (term.getPayload() instanceof ARTeSOSName) {
      ARTeSOSName name = (ARTeSOSName) term.getPayload();
      if (isExpression && functionNames.get(name) != null) functionsInUse.add(name);
      if (!constructorNames.containsKey(name) && !functionNames.containsKey(name) && !thetaTerminalNames.containsKey(name)) {
        trace(5, "Updating variable node " + term);
        term.setPayload(new ARTeSOSNameVariable((ARTeSOSName) term.getPayload()));
        definedMetaVariables.add((ARTValueTermVariable) term.getPayload());
        // if (term.getChildren().size() != 0) error("Term label '" + term.getPayload() + "' does not appear in constructor, value or function contexts " +
        // term);
      }
    }
    for (ARTValueTermUsingList c : term.getChildren())
      staticCheckTerm(thetaTerminalNames, c, definedMetaVariables, isExpression);
  }

  private void staticCheckConfiguration(Map<ARTeSOSName, Integer> thetaTerminalNames, ARTeSOSConfiguration configuration,
      Set<ARTValueTermVariable> definedMetaVariables) throws ARTException {
    for (ARTValue key : configuration)
      staticCheckTerm(thetaTerminalNames, (ARTValueTermUsingList) configuration.valueKey(key), definedMetaVariables, false);
  }

  public void staticCheck() throws ARTException {
    HashMap<ARTeSOSName, Integer> emptyMap = new HashMap<>();
    // Now run over the rules checking that metavariables are bound before they are used and changing payloads
    for (ARTeSOSRule r : rules) {
      trace(5, "Static check of " + r);
      r.metavariables = new HashSet<>();
      staticCheckConfiguration(r.getConclusion().getRelation().getThetaTerminalNames(), r.getConclusion().getLhs(), r.metavariables);

      for (ARTeSOSCondition e : r.getConditions()) {
        if (e instanceof ARTeSOSSideCondition) {
          staticCheckTerm(emptyMap, ((ARTeSOSSideCondition) e).getLhs(), r.metavariables, true);
          staticCheckTerm(emptyMap, ((ARTeSOSSideCondition) e).getRhs(), r.metavariables, false);
        } else {
          staticCheckConfiguration(((ARTeSOSTransition) e).getRelation().getThetaTerminalNames(), ((ARTeSOSTransition) e).getLhs(), r.metavariables);
          staticCheckConfiguration(((ARTeSOSTransition) e).getRelation().getThetaTerminalNames(), ((ARTeSOSTransition) e).getRhs(), r.metavariables);
        }
      }
      staticCheckConfiguration(r.getConclusion().getRelation().getThetaTerminalNames(), r.getConclusion().getRhs(), r.metavariables);
    }

    // Disjointness checks
    for (ARTeSOSName s : constructorNames.keySet())
      if (functionNames.containsKey(s)) error(s + " is both a constructor name and a function name");

    for (ARTeSOSRelation r : relations.values())
      for (ARTeSOSName s : r.getThetaTerminalNames().keySet()) {
        if (functionNames.containsKey(s)) error(s + " is both a terminal in relation " + r + " and a function name");
        if (constructorNames.containsKey(s)) error(" in relation " + r + " constructor " + s + " has rules but is declaraed as terminal");
      }

    errorCheckAbort();
  }

  /*** Interpreter routines ***/

  private boolean interpretSideCondition(ARTeSOSSideCondition sideCondition, Map<ARTValueTermVariable, ARTValueTermUsingList> bindings) throws ARTException {
    ARTValueTermUsingList lhsClosed = sideCondition.getLhs().substitute(bindings);
    if (lhsClosed.getPayload() instanceof ARTeSOSName) { // Is this a function?
      List<ARTValueTermUsingList> children = lhsClosed.getChildren();
      String functionName = ((ARTeSOSName) lhsClosed.getPayload()).getV();
      ARTValue leftPayload = children.get(0).getPayload();
      ARTValue rightPayload = children.size() > 1 ? children.get(1).getPayload() : null;
      ARTValue suppPayload = children.size() > 2 ? children.get(2).getPayload() : null;
      trace(5, "Executing function " + functionName + " with arguments " + leftPayload + ", " + rightPayload + ", " + suppPayload);
      lhsClosed = new ARTValueTermUsingList(ARTValue.despatch((functionName.endsWith("Op") ? functionName.substring(0, functionName.length() - 2) : functionName),
          leftPayload, rightPayload, suppPayload));
    }
    boolean ret = lhsClosed.closedMatchPatternSingletonBindings(sideCondition.getRhs(), bindings);
    trace(5, "Side condition evaluates to " + ret + " with bindings " + bindings);
    return ret;
  }

  private ARTeSOSConfiguration fSOS(ARTeSOSRelation relation, ARTeSOSConfiguration configuration) throws ARTException {
    if (fSOSCounterLimit > 0 && fSOSCounter >= fSOSCounterLimit) throw new ARTException("Interpreter aborted after " + fSOSCounter + " calls to fSOS()");

    ARTeSOSConfiguration ret = null;
    int localfSOSCounter = fSOSCounter++;

    trace(3, localfSOSCounter + " fSOS(" + relation.getLexeme() + ", " + configuration + ")");
    Map<ARTValueTermVariable, ARTValueTermUsingList> bindings = new HashMap<>();
    ARTValueTermUsingList theta = ((ARTValueTermUsingList) configuration.valueKey(ARTeSOSSpecification.termKey));
    if (relation.isTerminalTheta(theta)) return configuration; // Reflexivity

    ARTValue thetaRootPayload = theta.getPayload();
    List<ARTeSOSRule> ruleList = relation.getRuleMap().get(thetaRootPayload);
    if (ruleList == null) throw new ARTException("In relation " + relation.lexeme + ", constructor " + thetaRootPayload + " has no rules");

    ruleLoop: for (ARTeSOSRule rule : ruleList) {
      bindings.clear();
      trace(5, "Attempting match against rule " + rule.getLabel());
      if (!configuration.match(rule.getConclusion().getLhs(), bindings)) continue ruleLoop; // fail on conclusion LHS
      trace(5, "Conclusion LHS successfully matched with bindings " + bindings);
      int conditionNumber = 0;
      for (ARTeSOSCondition condition : rule.getConditions()) {
        trace(3, localfSOSCounter + " [" + rule.getLabel() + "]." + ++conditionNumber);
        trace(5, "Testing condition " + condition);
        if (condition instanceof ARTeSOSSideCondition) {
          if (!interpretSideCondition((ARTeSOSSideCondition) condition, bindings)) {
            trace(3, localfSOSCounter + " [" + rule.getLabel() + "]." + conditionNumber + " side condition failed");
            continue ruleLoop;
          }
        } else if (condition instanceof ARTeSOSTransition) {
          ARTeSOSTransition transition = (ARTeSOSTransition) condition;
          ARTeSOSConfiguration testConfiguration = transition.getLhs().substitute(bindings);
          trace(5, "recursively calling fSOS on relation " + transition.getRelation().lexeme + " with new configuration " + testConfiguration);

          if ((ret = fSOS(transition.getRelation(), testConfiguration)) == null) {
            trace(3, localfSOSCounter + " [" + rule.getLabel() + "]." + conditionNumber + " transition condition failed");
            continue ruleLoop;
          }
          ret.match(transition.getRhs(), bindings);
        } else
          throw new ARTException("Condition " + condition + " is not an instance of ARTSOSSideCondition or ARTSOSTransition");
      }
      ret = rule.getConclusion().getRhs().substitute(bindings);
      trace(2, localfSOSCounter + " [" + rule.getLabel() + "] rewrites " + configuration + " to " + ret);

      return ret;
    }
    return null;
  }

  public void interpretTest(ARTeSOSTest test) throws ARTException {
    traceLevel = test.getTraceLevel();

    ARTeSOSConfiguration configuration = new ARTeSOSConfiguration(test.getRelation().getConfiguration());
    fSOSCounter = 1;
    int step = 1;
    try {
      configuration.updateFromTerm(test.getLhs());
      if (!test.getName().equals("")) trace(1, test.getName());
      trace(2, "-- Interpreter start --");
      while (true) {
        if (test.getRelation().isTerminalTheta((ARTValueTermUsingList) configuration.valueKey(termKey))) {
          fSOSCounter--;
          step--;
          trace(1, "Total of " + step + " step" + (step == 1 ? "" : "s") + " and " + fSOSCounter + " call" + (fSOSCounter == 1 ? "" : "s") + " to fSOS()");
          trace(1, "Normal termination in configuration " + configuration + "\n");
          break;
        } else
          trace(2, step++ + " " + configuration.toString());
        ARTeSOSConfiguration tmp;
        if ((tmp = fSOS(test.getRelation(), configuration)) == null) {
          trace(1, "Interpretation stuck on relation " + test.getRelation().lexeme + " in configuration " + configuration + "\n");
          break;
        } else
          configuration = tmp;

        trace(2, "-- Interpreter restart --");
      }
    } catch (ARTException e) {
      error(e.getMessage());
      errorCheckAbort();
    }
  }

  public void interpret() throws ARTException {
    for (ARTeSOSTest t : tests)
      interpretTest(t);
    parser.setArteSOSInterpretCompleteTime(parser.artReadClock());
  }
}
