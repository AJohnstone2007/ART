
package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.AbstractRelation;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOrdered;

/************************************************************************************************
 *
 * This class represents Context Free Grammar rule sets
 *
 * Rules are constructed by the set of action*() functions which are called form the script intepreter
 *
 * Each action function sets the flag normalised to false
 *
 * The normalise() function computes a large set of staticly visible grammar properties which are used by some parsers and then sets normalised to true
 *
 * Any usage of a CFGRules object should be preceded by a call to normalise()
 *
 ***********************************************************************************************/
public final class CFGRules implements DisplayInterface { // class made final to avoid this-escape warning messages
  /************************************************************************************************
   *
   * Fields hard-set by the constructor - all private with limited access
   *
   ***********************************************************************************************/
  private static int nextFreeCFGRulesNumber = 1; // Global count of instances
  public final int cfgRulesNumber; // This instance's number
  public final CFGRulesKind cfgRulesKind; // one of SCRIPT, USER, LEXER, PARSER her emust be one SCRIPT. There may be mant USER. Only USER can have subgrammars
  private boolean normalised = false; // reset at initialisation; set by normalise(); reset by anything that touches the data stuctures
  private int nextUniqueLabelNumber = 0;
  private CFGRules cfgRulesLexer; // Subgrammar for lexing - terminals and rules below paraterminals
  private CFGRules cfgRulesParser; // Subgrammar for parsing - rules above paraterminals

  public String nextUniqueLabel() { // used by scriptinterpreter to give unqieue number to each bracket-like construct
    return Integer.toString(++nextUniqueLabelNumber);
  }

  public CFGRules getCfgRulesLexer() {
    return cfgRulesLexer;
  }

  public CFGRules getCfgRulesParser() {
    return cfgRulesParser;
  }

  /************************************************************************************************
   *
   * Fields manipulated by the script interpreter that force normalisation and must be cloned
   *
   ***********************************************************************************************/
  private final Set<Character> characterSet = new HashSet<>();
  private final Map<CFGElement, CFGElement> elements = new TreeMap<>(); // We use a map for elements because we need there to be one canonical instance
  private final Map<String, Set<Integer>> signatures = new TreeMap<>(); // A set of signatures for each constructor keyed on constructor name
  private final Set<CFGElement> paraterminals = new TreeSet<>();
  private final Set<CFGElement> declaredAsTokens = new TreeSet<>();
  private final Set<CFGElement> whitespaces = new TreeSet<>();
  private boolean seenWhitespaceDirective = false;
  private String filePrelude = null;
  private String classPrelude = null;
  private CFGElement startNonterminal = null;

  private void addCharacters(String string) {
    for (var c : string.toCharArray())
      addCharacter(c);
  }

  public void addCharacter(char c) {
    normalised = false;
    characterSet.add(c);
  }

  public CFGElement[] elementsAsArray() {
    return elements.keySet().toArray(new CFGElement[0]);
  }

  public CFGElement getCannonicalElement(CFGElement e) {
    return elements.get(e);
  }

  public void addSignature(int term) {
    normalised = false;
    String constructor = ScriptInterpreter.iTerms.termSymbolString(term);

    if (signatures.get(constructor) == null) signatures.put(constructor, new HashSet<>());

    signatures.get(constructor).add(term);
  }

  public void addParaterminal(CFGElement e) {
    normalised = false;
    paraterminals.add(e);
  }

  public boolean isParaterminal(CFGElement e) {
    return paraterminals.contains(e);
  }

  public void addToken(CFGElement e) {
    normalised = false;
    declaredAsTokens.add(e);
  }

  public void clearWhitespaces() {
    normalised = false;
    whitespaces.clear();
    seenWhitespaceDirective = true;
  }

  public void addWhitespaces(CFGElement e) {
    normalised = false;
    whitespaces.add(e);
    seenWhitespaceDirective = true;
  }

  public CFGElement[] whitespacesAsArray() {
    return whitespaces.toArray(new CFGElement[0]);
  }

  public String getFilePrelude() {
    return filePrelude;
  }

  public void setFilePrelude(String filePrelude) {
    normalised = false;
    this.filePrelude = filePrelude;
  }

  public String getClassPrelude() {
    return classPrelude;
  }

  public void setClassPrelude(String classPrelude) {
    normalised = false;
    this.classPrelude = classPrelude;
  }

  public CFGElement getStartNonterminal() {
    return startNonterminal;
  }

  public void setStartNonterminal(CFGElement startNonterminal) {
    normalised = false;
    this.startNonterminal = startNonterminal;
  }

  /************************************************************************************************
   *
   * Static final fields that are constant across all instances and thus do not need to be cloned
   *
   ***********************************************************************************************/
  private final static Set<CFGElementKind> terminalKinds = Set.of(CFGElementKind.TERM_BI, CFGElementKind.TERM_CH, CFGElementKind.TERM_CS,
      CFGElementKind.TERM_CI, CFGElementKind.TERM_CH_SET, CFGElementKind.TERM_CH_ANTI_SET);
  private final static Set<CFGElementKind> bracketKinds = Set.of(CFGElementKind.PAR, CFGElementKind.OPT, CFGElementKind.KLN, CFGElementKind.POS);
  // private final static Set<CFGElementKind> selfFirst = Set.of(CFGElementKind.TRM_BI, CFGElementKind.TRM_CH, CFGElementKind.EOS, CFGElementKind.TRM_CS,
  // CFGElementKind.TRM_CI, CFGElementKind.EPSILON); // Not used
  private final static Set<CFGElementKind> scaffoldingKinds = Set.of(CFGElementKind.SOS, CFGElementKind.EOS, CFGElementKind.EPSILON, CFGElementKind.ALT,
      CFGElementKind.END, CFGElementKind.PAR, CFGElementKind.OPT, CFGElementKind.POS, CFGElementKind.KLN);
  private final static Set<CFGElementKind> doNotCloneKinds = Set.of(CFGElementKind.ALT, CFGElementKind.END, CFGElementKind.PAR, CFGElementKind.OPT,
      CFGElementKind.POS, CFGElementKind.KLN);

  // These integer literals are used by gllHashPool instead of the enumeration elements
  public static final int SOS = 0;
  public static final int EOS = 1;
  public static final int EPSILON = 2;
  public static final int TRM_CS = 3;
  public static final int TRM_CI = 4;
  public static final int TRM_BI = 5;
  public static final int TRM_CH = 6;
  public static final int TRM_CH_UIB = 7;
  public static final int TRM_CH_UOB = 8;
  public static final int TRM_CH_SET = 9;
  public static final int TRM_CH_ANTI_SET = 10;
  public static final int NONTERMINAL = 11;
  public static final int ALT = 12;
  public static final int END = 13;
  public static final int PAR = 14;
  public static final int OPT = 15;
  public static final int POS = 16;
  public static final int KLN = 17;

  /************************************************************************************************
   *
   * Fields that are computed by !try or normalise() and thus do not need to be cloned
   *
   * TODO: Currently public, but need to be made private with access functions (not just getters!)
   *
   ***********************************************************************************************/
  // fields relating to the enumeration
  private int nextFreeEnumerationElement;
  public int terminalCount; // The number of unique terminal elements in this grammar
  public final CFGElement epsilonElement;
  private final CFGElement startOfStringElement;
  public final CFGElement endOfStringElement;
  public final CFGNode endOfStringNode;
  public final Map<Integer, CFGNode> numberToRulesNodeMap = new TreeMap<>();
  public final Map<CFGElement, CFGNode> elementToRulesNodeMap = new TreeMap<>(); // Map from nonterminals to list of productions represented by their LHS node
  public Map<CFGNode, CFGElement> lhsOf = new HashMap<>();

  // fields recording used and defined nonterminal
  public Set<CFGElement> defined = new TreeSet<>();
  public Set<CFGElement> used = new TreeSet<>();

  // reachability relations
  public final RelationOrdered<CFGElement, CFGElement> reachable = new RelationOrdered<>();
  public final RelationOrdered<CFGElement, CFGElement> reachableNonterminals = new RelationOrdered<>();
  public final RelationOrdered<CFGElement, CFGElement> reachablePara = new RelationOrdered<>();
  public final RelationOrdered<CFGElement, CFGElement> reachableNonterminalsPara = new RelationOrdered<>();
  public final Set<CFGElement> parserNonterminals = new TreeSet<>(); // Nonterminals reachable from start symbol, stopping at (and not including) paraterminals
  public Set<CFGElement> reachableParaterminals; // Paraterminals reachable from start symbol
  public final Set<CFGElement> lexerNonterminals = new TreeSet<>(); // Nonterminals reachable from every reachable paraterminal, including paraterminals

  // first and follow relations
  public final RelationOrdered<CFGElement, CFGElement> first = new RelationOrdered<>();
  public final RelationOrdered<CFGElement, CFGElement> follow = new RelationOrdered<>();
  public final RelationOrdered<CFGNode, CFGElement> instanceFirst = new RelationOrdered<>();
  public final RelationOrdered<CFGNode, CFGElement> instanceFollow = new RelationOrdered<>();
  public final RelationOrdered<CFGNode, CFGElement> instanceGuard = new RelationOrdered<>(); // static version of Elizabeth's testSelect

  // cycle detection
  public final Set<CFGElement> cyclicNonterminals = new HashSet<>(); // { X ::= \alpha X \beta} | \alpha,\beta =>* \epsilon }
  public final Set<CFGNode> cyclicSlots = new HashSet<>(); // { X ::= \alpha X \beta} | \alpha,\beta =>* \epsilon }
  public AbstractRelation<CFGElement, CFGElement> derivesExactly;
  public AbstractRelation<CFGElement, CFGElement> derivesExactlyTransitiveClosure;

  // accepting sets
  public final Set<CFGNode> acceptingSlots = new HashSet<>(); // Set of slots which are END nodes of the start production
  public final Set<Integer> acceptingNodeNumbers = new TreeSet<>(); // Set of node number for the slots on accepting slots

  /************************************************************************************************
   *
   * Constructors - one ab initio, one cloning
   *
   ***********************************************************************************************/
  public CFGRules(CFGRulesKind cfgRulesKind) { // Ab initio constructor for an empty rules set
    cfgRulesNumber = nextFreeCFGRulesNumber++;
    // Util.debug("Primary CFGRules constructor " + cfgRulesNumber + " " + cfgRulesKind);
    this.cfgRulesKind = cfgRulesKind;
    // Util.debug("Constructing cfgRulesNumber " + cfgRulesNumber + " " + cfgRulesKind);
    epsilonElement = findElement(CFGElementKind.EPSILON, "#"); // We are not using an initialiser block because elements must be initialised first
    endOfStringElement = findElement(CFGElementKind.EOS, "$");
    startOfStringElement = findElement(CFGElementKind.SOS, "$$");

    endOfStringNode = new CFGNode(this, CFGElementKind.EOS, "$", 0, GIFTKind.NONE, null, null);
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode
  }

  /*
   * Cloning constructor
   *
   * This is a copy constuctor which makes a fresh set of elements and optionally modifies the rules as they are copied
   *
   * It is important that the terminal and paraterminal numberings match the source element numberings because this constructor is used to create the lexer and
   * parser sub-grammars which need to be synchronised.
   *
   * TODO: this needs to be analysed and tests - there are dubious aspects to this
   *
   */
  private final Map<String, Integer> terminalNumbers = new TreeMap<>();

  public CFGRules(CFGRules src, CFGRulesKind cfgRulesKind, boolean character, boolean createParaterminals, boolean bnfLeft, boolean bnfRight) {
    terminalNumbers.clear();
    cfgRulesNumber = nextFreeCFGRulesNumber++;
    this.cfgRulesKind = cfgRulesKind; // Do not preserve the original kind
    // Util.debug("Copy CFGRules constructor " + cfgRulesNumber + " " + cfgRulesKind);
    // Util.debug("Copy constructing cfgRulesNumber " + cfgRulesNumber + " " + cfgRulesKind + " with parentnumber " + src.cfgRulesNumber);
    epsilonElement = findElement(CFGElementKind.EPSILON, "#"); // We are not using an initialiser block because elements must be initialised first
    endOfStringElement = findElement(CFGElementKind.EOS, "$");
    startOfStringElement = findElement(CFGElementKind.SOS, "$$");

    endOfStringNode = new CFGNode(this, CFGElementKind.EOS, "$", 0, GIFTKind.NONE, null, null);
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode

    for (var e : src.elements.keySet()) // Make new elements and collect TRM_CS elements
      if (character && e.cfgKind == CFGElementKind.TERM_CS)
        terminalNumbers.put(e.str, 0);
      else if (!doNotCloneKinds.contains(e.cfgKind)) findElement(e.cfgKind, e.str);

    if (character) {
      int terminalNumber = 0;

      if (src.whitespaces.size() > 0) { // Create WS nonterminal
        actionLHS("ART_" + terminalNumber++);
        actionALT();
        actionSEQ(CFGElementKind.KLN, nextUniqueLabel(), ScriptInterpreter.iTerms.termEmpty);
        for (var w : src.whitespaces) {
          actionALT();
          actionSEQ(w.cfgKind, w.str, ScriptInterpreter.iTerms.termEmpty);
          actionEND("");
        }
        actionEND("");
      }

      for (var s : terminalNumbers.keySet()) { // Now add each nonterminal to map and optionally create rule
        if (createParaterminals) {
          actionLHS("ART_" + terminalNumber);
          actionALT();
          trm_CS_to_TERM_CH(s, src.whitespaces.size() > 0);

          actionEND("");
        }
        terminalNumbers.put(s, terminalNumber++);
      }
      Util.debug("Terminal numbers: " + terminalNumbers);
    }

    cloneSetElements(paraterminals, src.paraterminals);
    cloneSetElements(declaredAsTokens, src.declaredAsTokens);
    seenWhitespaceDirective = src.seenWhitespaceDirective;
    cloneSetElements(whitespaces, src.whitespaces);
    if (src.startOfStringElement != null && src.getStartNonterminal() != null)
      setStartNonterminal(findElement(src.getStartNonterminal().cfgKind, src.getStartNonterminal().str));
    setFilePrelude(src.getFilePrelude()); // Strings are immutable so we can just assign reference
    setClassPrelude(src.getClassPrelude()); // Strings are immutable so we can just assign reference
    // Now build the rules
    for (var n : src.elementToRulesNodeMap.keySet())
      if (n.cfgKind == CFGElementKind.NONTERMINAL) {
        // Util.debug("CFGRules copy constructor mode " + cfgRulesKind + ": adding rule for " + n);
        actionLHS(n.str);
        cloneGrammarRulesRec(src, src.elementToRulesNodeMap.get(n).alt, character, createParaterminals, bnfLeft, bnfRight);
      }
    // TODO: no rules are ever queued so what is going on?
    dequeueRules(src, cfgRulesKind, character, createParaterminals, bnfLeft, bnfRight);
    if (character) { // We've handled whitespace explictly
      whitespaces.clear();
      seenWhitespaceDirective = true;
    }
  }

  private void trm_CS_to_TERM_CH(String s, boolean addWS) {
    for (int i = 0; i < s.length(); i++)
      actionSEQ(CFGElementKind.TERM_CH, s.substring(i, i + 1), ScriptInterpreter.iTerms.termEmpty);
    if (addWS) actionSEQ(CFGElementKind.NONTERMINAL, "ART_0", ScriptInterpreter.iTerms.termEmpty);
  }

  private void cloneSetElements(Set<CFGElement> dstSet, Set<CFGElement> srcSet) {
    for (var e : srcSet)
      dstSet.add(findElement(e.cfgKind, e.str));
  }

  private void cloneGrammarRulesRec(CFGRules src, CFGNode cfgNode, boolean character, boolean createParaterminals, boolean closureLeft, boolean closureRight) {
    if (cfgNode == null) return;
    // Util.debug("cloneGrammarsRulesRec at cfgNode " + cfgNode.toStringDot());
    switch (cfgNode.cfgElement.cfgKind) {
    case ALT:
      actionALT();
      cloneGrammarRulesRec(src, cfgNode.seq, character, createParaterminals, closureLeft, closureRight);
      cloneGrammarRulesRec(src, cfgNode.alt, character, createParaterminals, closureLeft, closureRight);
      return;
    case END:
      actionEND(cfgNode.cfgElement.str);
      // Util.debug("" + cfgNode.toStringAsProduction());
      // Util.debug("END");
      return;
    case PAR, OPT, KLN, POS:
      actionSEQ(cfgNode.cfgElement.cfgKind, nextUniqueLabel()/* cfgNode.cfgElement.str */, cfgNode.actionAsTerm);
      cloneGrammarRulesRec(src, cfgNode.alt, character, createParaterminals, closureLeft, closureRight);
      cloneGrammarRulesRec(src, cfgNode.seq, character, createParaterminals, closureLeft, closureRight);
      break;
    case TERM_CS:
      if (character) {
        // Util.debug("Expanding to characters: " + cfgNode);
        if (createParaterminals)
          actionSEQ(CFGElementKind.NONTERMINAL, "ART_" + terminalNumbers.get(cfgNode.cfgElement.str), cfgNode.actionAsTerm);
        else
          trm_CS_to_TERM_CH(cfgNode.cfgElement.str, src.whitespaces.size() > 0);
      } else // no expansion required; let's just carry on
        actionSEQ(cfgNode.cfgElement.cfgKind, cfgNode.cfgElement.str, cfgNode.actionAsTerm);
      cloneGrammarRulesRec(src, cfgNode.seq, character, createParaterminals, closureLeft, closureRight);
      break;
    default:
      actionSEQ(cfgNode.cfgElement.cfgKind, cfgNode.cfgElement.str, cfgNode.actionAsTerm);
      cloneGrammarRulesRec(src, cfgNode.seq, character, createParaterminals, closureLeft, closureRight);
      break;
    }
  }

  private record QueueRuleElement(int number, CFGNode cfgNode) {
  }

  Set<QueueRuleElement> queuedRules = new HashSet<>();
  LinkedList<QueueRuleElement> listRules = new LinkedList<>();

  private void queueRule(int i, CFGNode cfgNode) {
    var candidate = new QueueRuleElement(i, cfgNode);
    if (!queuedRules.contains(candidate)) {
      queuedRules.add(candidate);
      listRules.add(candidate);
    }
  }

  private void dequeueRules(CFGRules src, CFGRulesKind cfgRulesKindP, boolean character, boolean createParaterminals, boolean closureLeft,
      boolean closureRight) {
    QueueRuleElement ruleElement;
    while ((ruleElement = listRules.poll()) != null) {
      actionLHS("ART_" + ruleElement.number);
      actionALT();
      cloneGrammarRulesRec(src, ruleElement.cfgNode, character, false, closureLeft, closureRight);
    }
  }

  /************************************************************************************************
   *
   * Utility routines
   *
   ***********************************************************************************************/
  public boolean isEmpty() {
    return elementToRulesNodeMap.keySet().isEmpty();
  }

  public static boolean isLHS(CFGNode gn) {
    if (gn == null) return false;
    return gn.cfgElement != null && gn.cfgElement.cfgKind == CFGElementKind.NONTERMINAL && gn.seq == null;
  }

  /************************************************************************************************
   *
   * Action routines called from the script term traverser
   *
   *************************************************************************************************/
  public CFGNode workingNode;
  public GIFTKind workingFold = GIFTKind.NONE;
  // public int workingAction = 0;

  CFGNode mostRecentLHS;

  public CFGElement findElement(CFGElementKind kind, String s) {
    CFGElement candidate = new CFGElement(kind, s);
    if (elements.get(candidate) == null) elements.put(candidate, candidate);
    var ret = elements.get(candidate);

    switch (kind) { // Create individual character terminals from the contents of the set
    case TERM_CH_SET, TERM_CH_ANTI_SET:
      for (var c : ret.set)
        findElement(CFGElementKind.TERM_CH, "" + c);
      break;
    default:
      ; // skip everything else
    }
    return ret;
  }

  LinkedList<CFGNode> stack = new LinkedList<>();

  public void actionLHS(int t) {
    actionLHS(ScriptInterpreter.childSymbolString(t));
    if (ScriptInterpreter.iTerms.termArity(t) == 2) // Is there an attribute declaration?
      for (int i = 0; i < ScriptInterpreter.iTerms.termArity(ScriptInterpreter.iTerms.subterm(t, 1)); i++) {
        String typeParameter = "";
        if (ScriptInterpreter.iTerms.termArity(ScriptInterpreter.iTerms.subterm(t, 1, i)) == 3)
          typeParameter = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(t, 1, i, 2));
        mostRecentLHS.cfgElement.attributes.put(ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(t, 1, i, 0)),
            ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(t, 1, i, 1)) + typeParameter);
      }
  }

  public void actionLHS(String id) {
    CFGElement element = findElement(CFGElementKind.NONTERMINAL, id);
    defined.add(element);
    if (getStartNonterminal() == null) setStartNonterminal(element);
    workingNode = elementToRulesNodeMap.get(element);
    if (workingNode == null) elementToRulesNodeMap.put(element, actionSEQ(CFGElementKind.NONTERMINAL, id, 0));
    mostRecentLHS = elementToRulesNodeMap.get(element);
    normalised = false;

  }

  public void actionALT() {
    stack.push(workingNode);
    while (workingNode.alt != null) // Maintain specification order by adding new alternate at the end
      workingNode = workingNode.alt;
    workingNode = new CFGNode(this, CFGElementKind.ALT, nextUniqueLabel(), 0, workingFold, null, workingNode);
    workingFold = GIFTKind.NONE;
    normalised = false;
  }

  public CFGNode actionSEQ(CFGElementKind kind, String str, Integer actionAsTerm) {
    // Util.debug("Update working node with kind " + kind + " string " + str + " and slot term " + slotTerm);
    workingNode = new CFGNode(this, kind, str, actionAsTerm, workingFold, workingNode, null);
    if (kind != CFGElementKind.END) used.add(workingNode.cfgElement);
    workingFold = GIFTKind.NONE;
    normalised = false;
    return workingNode;
  }

  public void actionAttribute(String name, String type) {
  }

  public void actionEND(String actions) {
    actionSEQ(CFGElementKind.END, "", 0);
    workingNode = stack.pop();
    normalised = false;
  }

  /************************************************************************************************
   *
   * Normalisation - Compute staticaly visible grammar properties
   *
   ***********************************************************************************************/
  public void normalise() {
    if (normalised) return; // Nothing to do
    // Util.debug("Normalising cfgRules object with number " + cfgRulesNumber + " " + cfgRulesKind);
    if (!seenWhitespaceDirective) {
      Util.info("Adding default whitespace handling");
      whitespaces.add(findElement(CFGElementKind.TERM_BI, "SIMPLE_WHITESPACE"));
    }

    // Add implicit aspects of the rules
    normaliseAddSingletonNodes();
    normaliseAddCharacters();

    // Look for dubious literals which contain whitespace and issue warnings not errors - there may be situations where that is valid
    if (cfgRulesKind == CFGRulesKind.USER) for (var e : elements.keySet()) // Elements only - no EBNF implications
      if (e.cfgKind == CFGElementKind.TERM_CI || e.cfgKind == CFGElementKind.TERM_CS)
        if (Util.containsWhitespace(e.str)) Util.warning("Context free terminal contains whitespace: " + e);

    // Element and node numbering
    nextFreeEnumerationElement = 0;
    normaliseNumberElementsAndNodes(); // EBNF equipped
    normaliseSetEndNodeLinks(); // EBNF equipped

    // Set lhsOf
    for (CFGElement ge : elements.keySet())
      normaliseComputeLHSOfRec(ge, elementToRulesNodeMap.get(ge)); // EBNF equipped

    normaliseSetPositionalAttributes(); // EBNF equipped
    if (cfgRulesKind == CFGRulesKind.USER) normaliseComputeNullableSlots(); // EBNF equipped
    normaliseSetAcceptingSlotsAndNodes(); // EBNF not needed - we only look at the top level productions

    // First and follow sets
    // if (startNonterminal != null) follow.add(startNonterminal, endOfStringElement);
    // Util.debug("Warning - only computing first and follow for USER grammars");
    // if (cfgRulesKind == CFGRulesKind.USER) computeFirstFollowGuardSets();

    normaliseComputeReachabilities(); // ENF equipped
    normaliseComputeAndCheckParaterminalSets(); // Reachable element manipulation - no EBNF implications
    derivesExactly = new Relation<>();
    normaliseComputeCyclicSlots(); // TODO: Not understood
    normaliseCollectAndCheckAttributes(); // TODO: BNF only?
    if (cfgRulesKind == CFGRulesKind.USER) {
      normaliseConstructSubgrammars();
      normaliseSubgrammarConsistencyCheck();
    }
    normalised = true;
  }

  /************************************************************************************************
   *
   * Instance ordering and linking
   *
   ***********************************************************************************************/
  private void normaliseNumberElementsAndNodes() {
    for (CFGElement s : elements.keySet()) {
      s.number = nextFreeEnumerationElement++;
      if (terminalKinds.contains(s.cfgKind)) terminalCount = s.number;
      // Util.info("Enumerating grammar element " + s.ei + ": " + s.str);
    }
    terminalCount++;

    numberToRulesNodeMap.put(endOfStringNode.num = nextFreeEnumerationElement++, endOfStringNode);
    for (CFGElement n : elementToRulesNodeMap.keySet())
      numberElementsAndNodesRec(elementToRulesNodeMap.get(n));
  }

  private void numberElementsAndNodesRec(CFGNode node) {
    if (node != null) {
      numberToRulesNodeMap.put(node.num = nextFreeEnumerationElement++, node);
      if (node.cfgElement.cfgKind != CFGElementKind.END) {
        numberElementsAndNodesRec(node.seq);
        numberElementsAndNodesRec(node.alt);
      }
    }
  }

  private void normaliseSetEndNodeLinks() {
    for (CFGElement n : elementToRulesNodeMap.keySet()) {
      CFGNode lhs = elementToRulesNodeMap.get(n);
      for (CFGNode production = lhs.alt; production != null; production = production.alt)
        setEndNodeLinksRec(lhs, production);
    }
  }

  private void setEndNodeLinksRec(CFGNode parentNode, CFGNode altNode) {
    CFGNode gn;
    for (gn = altNode.seq; gn.cfgElement.cfgKind != CFGElementKind.END; gn = gn.seq) {
      // Util.info("processEndNodes at " + gn.ni + " " + gn);
      if (gn.alt != null) for (CFGNode alternate = gn.alt; alternate != null; alternate = alternate.alt)
        setEndNodeLinksRec(gn, alternate); // Recurse into brackets
    }
    // Util.info("processEndNodes processing " + gn.ni + " " + gn);
    gn.alt = altNode; // We are now at the end of a production or of a bracketed alternate
    gn.seq = parentNode;
    if (parentNode == elementToRulesNodeMap.get(getStartNonterminal())) acceptingNodeNumbers.add(gn.num);
    // Util.info("processEndNodes updated alt and seq to " + gn.alt.ni + " " + gn.seq.ni);
  }

  private void normaliseComputeLHSOfRec(CFGElement lhs, CFGNode cfgNode) {
    if (cfgNode == null) return;
    lhsOf.put(cfgNode, lhs);
    if (cfgNode.cfgElement.cfgKind == CFGElementKind.END) return;
    normaliseComputeLHSOfRec(lhs, cfgNode.seq);
    normaliseComputeLHSOfRec(lhs, cfgNode.alt);
  }

  private void normaliseSetPositionalAttributes() {
    for (CFGElement ge : elements.keySet())
      if (ge.cfgKind == CFGElementKind.NONTERMINAL && elementToRulesNodeMap.get(ge) != null) computePositionalAttributesRec(elementToRulesNodeMap.get(ge));
  }

  private void computePositionalAttributesRec(CFGNode parent) {
    for (CFGNode gn = parent.alt; gn != null; gn = gn.alt) { // walk all productions
      CFGNode penultimateSlot = gn.seq;
      CFGNode gs = gn.seq;
      gs.initial = true;
      gs.setNullablePrefix();
      if (gs.seq.cfgElement.cfgKind != CFGElementKind.END) gs.second = true;
      while (gs.cfgElement.cfgKind != CFGElementKind.END) {
        penultimateSlot = gs;
        if (gs.alt != null) computePositionalAttributesRec(gs);
        gs = gs.seq;
      }
      penultimateSlot.penultimate = true;
      gs.ultimate = true;
      gs.setNullableSuffix();
    }
  }

  boolean nullableChanged;

  private void normaliseComputeNullableSlots() {
    nullableChanged = true;
    int pass = 1;
    while (nullableChanged) {
      Util.debug("normaliseComputeNullable pass " + pass++);
      nullableChanged = false;
      for (CFGElement ge : elements.keySet())
        if (ge.cfgKind == CFGElementKind.NONTERMINAL && elementToRulesNodeMap.get(ge) != null) {
          computeNullableSlotsAndElementsRec(ge, elementToRulesNodeMap.get(ge).alt);
        }
    }
  }

  private void computeNullableSlotsAndElementsRec(CFGElement lhs, CFGNode headAlt) {
    for (CFGNode a = headAlt; a != null; a = a.alt) { // walk all productions
      if (a.getNullable()) nullableChanged = headAlt.setNullable();
      if (a.getNullable() && lhs != null) nullableChanged = lhs.setNullable();
      CFGNode s = a.seq;

      while (true) {
        switch (s.cfgElement.cfgKind) {
        case OPT, KLN:
          nullableChanged |= s.setNullable(); // Note flow through
        case PAR, POS:
          computeNullableSlotsAndElementsRec(null, s); // Recurse into EBNF subtrees
          break;
        default:
          ;
        }

        if (s.initial) nullableChanged |= s.setNullablePrefix(); // First slot is nullable prefix by definition
        if (s.ultimate) nullableChanged |= s.setNullableSuffix(); // Final slot is nullable sufffix by definition

        if (s.cfgElement.cfgKind == CFGElementKind.END) {
          if (s.getNullablePrefix()) nullableChanged |= a.setNullable(); // The nullable prefix reached the end, so this whole sequence is nullable
          break; // No more to do in this sequence
        }

        if (s.cfgElement.getNullable()) nullableChanged |= s.setNullable(); // This slot is itself nullable

        if (s.getNullable() && s.getNullablePrefix()) nullableChanged |= s.seq.setNullablePrefix(); // Propogate nullable prefix
        if (s.getNullable() && s.seq.getNullableSuffix()) nullableChanged |= s.setNullableSuffix(); // Propogate nullable suffix
        s = s.seq;
      }
    }
  }

  private void normaliseSetAcceptingSlotsAndNodes() {
    for (var p = elementToRulesNodeMap.get(getStartNonterminal()).alt; p != null; p = p.alt) { // Walk all productions
      CFGNode s;
      for (s = p.seq; s.cfgElement.cfgKind != CFGElementKind.END; s = s.seq) // Walk top level sequence
        ;
      acceptingSlots.add(s);
      acceptingNodeNumbers.add(s.num);
    }
  }

  /************************************************************************************************
   *
   * Adding implicit aspects of a rule set
   *
   ***********************************************************************************************/
  private void normaliseAddSingletonNodes() {
    // Add singleton grammar nodes for terminals, # and epsilon. These are used by the SPPF.
    for (CFGElement e : elements.keySet())
      if (e.cfgKind == CFGElementKind.TERM_BI || e.cfgKind == CFGElementKind.TERM_CS || e.cfgKind == CFGElementKind.TERM_CI
          || e.cfgKind == CFGElementKind.TERM_CH || e.cfgKind == CFGElementKind.TERM_CH_SET || e.cfgKind == CFGElementKind.TERM_CH_ANTI_SET
          || e.cfgKind == CFGElementKind.EPSILON)
        elementToRulesNodeMap.put(e, new CFGNode(this, e.cfgKind, e.str, 0, GIFTKind.NONE, null, null));
  }

  private void normaliseAddCharacters() {
    // Load characters
    // String printableASCII = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"; // Not used?
    // String defaultCharacterSet = "\r\n\t£" + printableASCII; // Not used?
    for (CFGElement e : elements.keySet())
      switch (e.cfgKind) {
      case TERM_BI:
        switch (e.str) {
        case "ID":
          addCharacters("_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
          break;
        case "ART_ID":
          addCharacters("$_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
          break;
        case "AP_INTEGER", "INTEGER":
          addCharacters("0123456789");
          break;
        case "SIGNED_INTEGER":
          addCharacters("0123456789+-");
          break;
        case "AP_REAL", "REAL":
          addCharacters("0123456789.Ee");
          break;
        case "SIGNED_REAL":
          addCharacters("0123456789.Ee+-");
          break;
        case "SIMPLE_WHITESPACE":
          addCharacters("\r\n\t ");
          break;
        case "CHAR_BQ":
          addCharacters("`\\");
          break;
        case "COMMENT_LINE_C":
          addCharacters("/");
          break;
        case "COMMENT_BLOCK_C":
          addCharacters("/*");
          break;
        case "COMMENT_NEST_ART":
          addCharacters("(*)");
          break;
        case "STRING_BRACE", "STRING_BRACE_NEST":
          addCharacters("{}\\");
          break;
        case "STRING_BRACKET", "STRING_BRACKET_NEST":
          addCharacters("{}\\");
          break;
        case "STRING_ANGLE", "STRING_ANGLE_NEST":
          addCharacters("<>\\");
          break;
        case "STRING_SHRIEK_SHRIEK":
          addCharacters("!\\");
          break;
        case "STRING_DOLLAR":
          addCharacters("$\\");
          break;
        case "STRING_SQ":
          addCharacters("'\\");
          break;
        case "STRING_DQ":
          addCharacters("\"\\");
          break;
        case "SML_CHAR":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_INT":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_REAL":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_STRID":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_STRING":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_SYMID":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_TYVAR":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_WORD":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        case "SML_COMMENT":
          Util.warning("Found SML builtin " + e.str + " whilst extending character set - ignoring");
          break;
        default:
          Util.fatal("Character set update found unexpected built in " + e);
          break;
        }
        break;
      case TERM_CS, TERM_CI, TERM_CH, TERM_CH_SET, TERM_CH_ANTI_SET:
        addCharacters(e.str);
        break;
      default:
        ;// skip everything else
      }
  }

  // TODO: this looks like BNF only
  private void normaliseCollectAndCheckAttributes() {
    for (CFGElement e : elements.keySet()) {
      Map<String, Integer> rhsNonterminalsInProduction = new HashMap<>();
      if (e.cfgKind == CFGElementKind.NONTERMINAL && elementToRulesNodeMap.get(e) != null) { // Only look at nonterminals
        String lhs = e.str;
        for (CFGNode gn = elementToRulesNodeMap.get(e).alt; gn != null; gn = gn.alt) { // iterate over the productions
          rhsNonterminalsInProduction.clear();
          for (CFGNode gs = gn.seq; gs.cfgElement.cfgKind != CFGElementKind.END; gs = gs.seq) {
            // Util.info("Collecting RHS nonterminals at " + gs + " " + ScriptInterpreter.iTerms.toRawString(gs.actionAsTerm));
            if (gs.cfgElement.cfgKind == CFGElementKind.NONTERMINAL) {
              if (rhsNonterminalsInProduction.get(gs.cfgElement.str) == null)
                rhsNonterminalsInProduction.put(gs.cfgElement.str, 1);
              else
                rhsNonterminalsInProduction.put(gs.cfgElement.str, rhsNonterminalsInProduction.get(gs.cfgElement.str) + 1);
              gs.instanceNumber = rhsNonterminalsInProduction.get(gs.cfgElement.str);
            }
          }
          // Now extend rhsNonterminalsAcrossAllProductions by the instances we have found for this production
          for (var a : rhsNonterminalsInProduction.keySet()) {
            if (e.rhsNonterminalsAcrossAllProductions.get(a) == null) e.rhsNonterminalsAcrossAllProductions.put(a, 0);
            if (rhsNonterminalsInProduction.get(a) > e.rhsNonterminalsAcrossAllProductions.get(a))
              e.rhsNonterminalsAcrossAllProductions.put(a, rhsNonterminalsInProduction.get(a));
          }
        }

        // Util.info("LHS: " + lhs + " with valid nonterminals=instances: " + rhsNonterminalsInProduction);
        // Now check each action to see if it is trying to access a RHS nonterminal which is not instances in this LHS
        for (CFGNode gn = elementToRulesNodeMap.get(e).alt; gn != null; gn = gn.alt) {
          for (CFGNode gs = gn.seq; gs.cfgElement.cfgKind != CFGElementKind.END; gs = gs.seq) {
            for (int i = 0; i < ScriptInterpreter.iTerms.termArity(gs.actionAsTerm); i++) {
              int annotationRoot = ScriptInterpreter.iTerms.subterm(gs.actionAsTerm, i);
              // Util.info("Processing slot annotation " + ScriptTermInterpreter.iTerms.toString(annotationRoot));
              switch (ScriptInterpreter.iTerms.termSymbolString(annotationRoot)) {
              case "cfgEquation", "cfgAssignment":
                checkTermActionsRec(annotationRoot, lhs, e.rhsNonterminalsAcrossAllProductions);
                break;

              case "cfgNative":
                checkNativeActions(ScriptInterpreter.iTerms.toString(annotationRoot), lhs, e.rhsNonterminalsAcrossAllProductions, true);
                break;
              case "cfgInsert":
                break;
              }
            }
            // Util.debug("Collecting attributes at " + gs + " " + ScriptTermInterpreter.iTerms.toRawString(gs.slotTerm));
          }
        }
      }
    }
  }

  /************************************************************************************************
   *
   * Cycle detection
   *
   ***********************************************************************************************/
  // TODO: dubious code - look like BNF only
  private void normaliseComputeCyclicSlots() {
    for (CFGElement lhs : elements.keySet())
      if (lhs.cfgKind == CFGElementKind.NONTERMINAL) {
        CFGNode topNode = elementToRulesNodeMap.get(lhs);
        if (topNode == null) continue;
        // Util.info("Compute nullable suffix visiting top node " + topNode.num + ":" + topNode);
        for (CFGNode altNode = topNode.alt; altNode != null; altNode = altNode.alt) {
          CFGNode seqNode = altNode;
          // boolean seenOnlyNullable = true; // Not used?
          while (true) {
            // Flow control
            seqNode = seqNode.seq;
            // Util.info("Visiting seq node " + seqNode.num + ":" + seqNode + " with seenOnlyNullable " + seenOnlyNullable);

            if (cyclicNonterminals.contains(seqNode.cfgElement) && derivesExactlyTransitiveClosure.get(seqNode.cfgElement).contains(lhs))
              cyclicSlots.add(seqNode.seq);
            if (seqNode.cfgElement.cfgKind == CFGElementKind.END) break;
          }
        }
      }
  }

  /*
   * Compute reachabilities
   *
   * reachable is all elements reachable from all nonterminals irrespective of paraterminals reachableNonterminals is all nonterminals reachable from all
   * nonterminals irrespective of paraterminals
   *
   * reachablePara is all elements reachable from all nonterminals with recursion stopping at paraterminals reachableNonterminalsPara is all nonterminals
   * reachable from all nonterminals with recursion stopping at paraterminals
   *
   *
   */

  // TODO: // Something odd here - all nonterminals are scanned but recursive routine dives into them all - shouldn't we just test from start symbol to avoid
  // unreacheachables?
  private void normaliseComputeReachabilities() {
    for (var lhs : elements.keySet())
      if (lhs.cfgKind == CFGElementKind.NONTERMINAL && elementToRulesNodeMap.get(lhs) != null) { // avoid nonterminals that have no rules in this grammar
        reachable.add(lhs); // Make empty relation codomains
        reachableNonterminals.add(lhs);
        reachablePara.add(lhs);
        reachableNonterminalsPara.add(lhs);
        addDirectlyReachable(lhs, elementToRulesNodeMap.get(lhs), reachable, reachableNonterminals, false);
        addDirectlyReachable(lhs, elementToRulesNodeMap.get(lhs), reachablePara, reachableNonterminalsPara, true);
      }
    reachable.transitiveClosure();
    reachableNonterminals.transitiveClosure();
    reachablePara.transitiveClosure();
    reachableNonterminalsPara.transitiveClosure();
  }

  private void addDirectlyReachable(CFGElement lhs, CFGNode topNode, AbstractRelation<CFGElement, CFGElement> allRel,
      AbstractRelation<CFGElement, CFGElement> nonterminalRel, boolean stopOnParaterminal) {
    // if (cfgRulesKind == CFGRulesKind.USER) Util.debug("computeReachabilityRec with LHS " + lhs + " and topNode " + topNode);
    if (topNode == null) return;
    if (allRel.get(lhs).contains(topNode.cfgElement)) return; // Don't cycle
    if (stopOnParaterminal && paraterminals.contains(lhs)) return;

    for (var altNode = topNode.alt; altNode != null; altNode = altNode.alt)
      for (var seqNode = altNode.seq; seqNode.cfgElement.cfgKind != CFGElementKind.END; seqNode = seqNode.seq) {
        if (bracketKinds.contains(seqNode.cfgElement.cfgKind))
          addDirectlyReachable(lhs, seqNode, allRel, nonterminalRel, stopOnParaterminal);
        else {
          addElements(allRel, lhs, seqNode);
          if (seqNode.cfgElement.cfgKind == CFGElementKind.NONTERMINAL) {
            addElements(nonterminalRel, lhs, seqNode);
            addDirectlyReachable(lhs, elementToRulesNodeMap.get(seqNode.cfgElement), allRel, nonterminalRel, stopOnParaterminal);
          }
        }
      }
  }

  // convenience method to bind all of the elements represented by a node: either just the element, or unpack the set of CH_SET and CH_ANTI_SET
  private void addElements(AbstractRelation<CFGElement, CFGElement> relation, CFGElement key, CFGNode node) {
    // if (cfgRulesKind == CFGRulesKind.USER) Util.debug("Binding " + node.cfgElement + " to key " + key + " in " + relation);
    relation.add(key, node.cfgElement);
    if (node.cfgElement.cfgKind == CFGElementKind.TERM_CH_SET || node.cfgElement.cfgKind == CFGElementKind.TERM_CH_ANTI_SET) for (var c : node.cfgElement.set)
      relation.add(key, findElement(CFGElementKind.TERM_CH, c.toString()));
  }

  /************************************************************************************************
   *
   * Compute and check subgrammar sets
   *
   * parserNonterminals are nonterminals reachable from start symbol, stopping at (and not including) paraterminals
   *
   * reachableParaterminals are paraterminals reachable from start symbol
   *
   * lexerNonterminals are nonterminals reachable from every reachable paraterminal, including paraterminals
   *
   ***********************************************************************************************/
  private void normaliseComputeAndCheckParaterminalSets() {
    if (getStartNonterminal() == null) return; // empty grammar
    parserNonterminals.addAll(reachableNonterminalsPara.get(getStartNonterminal()));
    parserNonterminals.add(getStartNonterminal());
    reachableParaterminals = new TreeSet<>(parserNonterminals);

    parserNonterminals.removeAll(paraterminals);
    reachableParaterminals.retainAll(paraterminals);

    for (var n : reachableParaterminals)
      lexerNonterminals.addAll(reachableNonterminals.get(n)); // Note we want all reachable nonterminals including paraterminals

    if (cfgRulesKind == CFGRulesKind.USER) {
      TreeSet<CFGElement> tmp = new TreeSet<>(elements.keySet());
      tmp.removeAll(reachable.get(getStartNonterminal()));
      tmp.remove(getStartNonterminal());
      tmp.removeAll(whitespaces);
      for (var e : elements.keySet())
        if (scaffoldingKinds.contains(e.cfgKind)) tmp.remove(e);
      if (!tmp.isEmpty()) {
        Util.warning("unused CF rule element" + (tmp.size() == 1 ? "" : "s") + ": " + tmp);
      }

      tmp = new TreeSet<>(paraterminals);
      tmp.removeAll(reachable.get(getStartNonterminal()));
      if (!tmp.isEmpty()) {
        Util.warning("unused paraterminal" + (tmp.size() == 1 ? "" : "s") + ": " + tmp);
      }

      tmp = new TreeSet<>(parserNonterminals);
      tmp.retainAll(lexerNonterminals);
      if (!tmp.isEmpty()) {
        Util.warning("nonterminal" + (tmp.size() == 1 ? "" : "s") + " used by both the parser and the lexer: " + tmp);
      }

      tmp = new TreeSet<>(paraterminals);
      tmp.retainAll(lexerNonterminals);
      if (!tmp.isEmpty()) {
        Util.warning("paraterminal" + (tmp.size() == 1 ? "" : "s") + " used by paraterminals: " + tmp);
      }

      tmp = new TreeSet<>(parserNonterminals);
      tmp.addAll(lexerNonterminals);
      tmp.removeAll(defined);
      if (!tmp.isEmpty()) {
        Util.fatal("undefined nonterminal" + (tmp.size() == 1 ? "" : "s") + ": " + tmp);
      }
    }

  }

  private void normaliseConstructSubgrammars() {
    // Construct lexer and parser subgrammars ONLY if we are a USER grammar
    // Note, by the time we get here, we are fully normalised so the subgrammars can use our reachability information
    cfgRulesLexer = new CFGRules(this, CFGRulesKind.LEXER, false, false, false, false);
    cfgRulesLexer.normalise(); // recurse only once for the lexer grammar
    cfgRulesParser = new CFGRules(this, CFGRulesKind.PARSER, false, false, false, false);
    cfgRulesParser.normalise(); // recurse only once for the parser grammar
  }

  private void normaliseSubgrammarConsistencyCheck() {
    for (var n : elementToRulesNodeMap.keySet())
      if (terminalKinds.contains(n.cfgKind)) {
        var mainElement = elementToRulesNodeMap.get(n);
        var lexerElement = getCfgRulesLexer().elementToRulesNodeMap.get(n);
        var parserElement = getCfgRulesParser().elementToRulesNodeMap.get(n);

        if (lexerElement == null)
          Util.error("Main element missing in lexer grammar: " + mainElement);
        else {
          if (lexerElement == mainElement) Util.error("Main and lexer cfgRules share element " + mainElement);
          if (lexerElement.num != mainElement.num)
            Util.error("Main and lexer element has number mismatch " + mainElement + ": " + mainElement.num + " -> " + lexerElement.num);
        }

        if (parserElement == null)
          Util.error("Main and parser cfgRules share element " + mainElement);
        else {
          if (parserElement == mainElement) Util.error("Main and parser cfgRules share element " + mainElement);
          if (parserElement.num != mainElement.num)
            Util.error("Main and parser element has number mismatch " + mainElement + ": " + mainElement.num + " -> " + parserElement.num);
        }
      }
  }

  /************************************************************************************************
   *
   * Attribute and action consistency checking
   *
   ***********************************************************************************************/
  private void checkTermActionsRec(int annotationRoot, String lhs, Map<String, Integer> rhsNonterminals) {
    if (ScriptInterpreter.iTerms.termSymbolString(annotationRoot).equals("cfgAttribute")) {
      String nonterminalID = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(annotationRoot, 0));
      String attributeID = ScriptInterpreter.iTerms.termSymbolString(ScriptInterpreter.iTerms.subterm(annotationRoot, 1));
      validateAttribute(nonterminalID, attributeID, lhs, rhsNonterminals, false); // int is default attribute type - overriden by <> declaration on rhs
    } else
      for (int i = 0; i < ScriptInterpreter.iTerms.termArity(annotationRoot); i++)
        checkTermActionsRec(ScriptInterpreter.iTerms.subterm(annotationRoot, i), lhs, rhsNonterminals);
  }

  private void validateAttribute(String nonterminalID, String attributeID, String lhs, Map<String, Integer> rhsNonterminals, boolean isNative) {
    // Util.info("Validating attribute " + nonterminalID + "," + attributeID);

    if (nonterminalID.equals(lhs)) { // Case 1: attribute ID is LHS, even though it might end in a digit
      addAttribute(nonterminalID, attributeID);
      return;
    }

    Character subscriptCharacter = nonterminalID.charAt(nonterminalID.length() - 1);
    int subscript = Character.isDigit(subscriptCharacter) ? (subscriptCharacter - '0') : -1;

    if (subscript > -1) { // Only consider attribute names with a trailing digit
      String nonterminalIDbare = nonterminalID.substring(0, nonterminalID.length() - 1); // strip subscript digit
      if (subscript == 0 && nonterminalIDbare.equals(lhs)) { // Case 2: subscripted LHS0
        addAttribute(nonterminalIDbare, attributeID);
        return;
      }
      if (rhsNonterminals.containsKey(nonterminalIDbare) && subscript <= rhsNonterminals.get(nonterminalIDbare)) { // Case 3: subscripted RHS instance
        addAttribute(nonterminalIDbare, attributeID);
        return;
      }
    }

    // Exception cases for Java to reduce spurious messages
    Set<String> javaAllowedClasses = Set.of("System", "Math", "Integer", "Double");
    if (javaAllowedClasses.contains(nonterminalID)) return; // Silent return

    Set<String> javaAllowedMethods = Set.of("put", "get");
    if (javaAllowedMethods.contains(attributeID)) return; // Silent return

    if (isNative)
      ; // Util.warning("ignoring native action attribute-like element " + nonterminalID + "." + attributeID + " in production for nonterminal " + lhs);
    else
      Util.fatal("invalid attribute " + nonterminalID + "." + attributeID + " in production for nonterminal " + lhs);
  }

  private void addAttribute(String nonterminalID, String attributeID) {
    if (findElement(CFGElementKind.NONTERMINAL, nonterminalID).attributes.get(attributeID) == null)
      findElement(CFGElementKind.NONTERMINAL, nonterminalID).attributes.put(attributeID, "int");
  }

  private void checkNativeActions(String action, String lhs, Map<String, Integer> rhsNonterminals, boolean warning) {
    String attributeRegex = "\\w+\\.\\w+";
    Pattern pattern = Pattern.compile(attributeRegex);
    Matcher matcher = pattern.matcher(action);
    while (matcher.find()) {
      String[] parts = matcher.group().split("\\.");
      validateAttribute(parts[0], parts[1], lhs, rhsNonterminals, warning);
    }
  }

  /************************************************************************************************
   *
   * Support for parsers that use array based grammar representation - called from GLLHashPool
   *
   ***********************************************************************************************/
  public int[] makeKindsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (CFGElement gs : elements.keySet())
      ret[gs.number] = gs.cfgKind.ordinal();
    for (int ni : numberToRulesNodeMap.keySet())
      ret[ni] = numberToRulesNodeMap.get(ni).cfgElement.cfgKind.ordinal();
    return ret;
  }

  public int[][] makeAltsArray() {
    int ret[][] = new int[nextFreeEnumerationElement][];
    for (int ni : numberToRulesNodeMap.keySet()) {
      CFGNode gn = numberToRulesNodeMap.get(ni);
      int altCount = 0;
      for (CFGNode alt = gn.alt; alt != null; alt = alt.alt)
        altCount++;
      ret[ni] = new int[altCount + 1];
      altCount = 0;
      for (CFGNode alt = gn.alt; alt != null; alt = alt.alt)
        ret[ni][altCount++] = alt.num;
      ret[ni][altCount] = 0;
    }
    return ret;
  }

  public int[] makeSeqsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (int ni : numberToRulesNodeMap.keySet()) {
      CFGNode sn = numberToRulesNodeMap.get(ni).seq;
      ret[ni] = sn == null ? 0 : sn.num;
    }
    return ret;
  }

  public int[] makeCallTargetsArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : numberToRulesNodeMap.keySet()) {
      CFGNode lhs = elementToRulesNodeMap.get(numberToRulesNodeMap.get(ni).cfgElement);
      ret[ni] = (lhs == null ? 0 : lhs.num);
    }
    return ret;
  }

  public int[] makeElementOfArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : numberToRulesNodeMap.keySet()) {
      CFGElement el = numberToRulesNodeMap.get(ni).cfgElement;
      ret[ni] = (el == null ? 0 : el.number);
    }
    return ret;
  }

  public boolean[][] makeFirstOfArray() { // FirstOf is slot, token -> boolean
    boolean[][] ret = new boolean[nextFreeEnumerationElement][];
    for (int i = 0; i < ret.length; i++)
      ret[i] = new boolean[nextFreeEnumerationElement];
    for (var e : first.getDomain())
      for (var f : first.get(e))
        ret[e.number][f.number] = true;
    return null;
  }

  public boolean[][] makeFollowOfArray() { // FollowOf is slot, token -> boolean
    boolean[][] ret = new boolean[nextFreeEnumerationElement][];
    for (int i = 0; i < ret.length; i++)
      ret[i] = new boolean[nextFreeEnumerationElement];
    for (var e : follow.getDomain())
      for (var f : follow.get(e))
        ret[e.number][f.number] = true;
    return ret;
  }

  /************************************************************************************************
   *
   * !print and !show action routines overriding defaults in DisplayInterface
   *
   ***********************************************************************************************/
  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indent, int depthLimit) {
    normalise();
    outputStream.print("(* " + (isEmpty() ? "Empty " : "") + cfgRulesKind + " Context Free Grammar rules *)\n");

    if (!characterSet.isEmpty()) {
      outputStream.print("!characterSet {");
      for (var c : characterSet)
        outputStream.print(Util.escapeString(c.toString()));
      outputStream.println("}");
    }

    if (!declaredAsTokens.isEmpty()) {
      outputStream.print("!token ");
      printElements(outputStream, declaredAsTokens);
      outputStream.println();
    }

    if (!paraterminals.isEmpty()) {
      outputStream.print("!paraterminal ");
      printElements(outputStream, paraterminals);
      outputStream.println();
    }

    if (whitespaces.isEmpty())
      outputStream.print("!clear whitespace ");
    else {
      outputStream.print("!whitespace ");
      printElements(outputStream, whitespaces);
    }
    outputStream.println();

    outputStream.println("!start " + getStartNonterminal());

    for (CFGElement n : elementToRulesNodeMap.keySet()) {
      boolean firstElement = true;
      for (CFGNode production = elementToRulesNodeMap.get(n).alt; production != null; production = production.alt) {
        if (firstElement) {
          outputStream.print(production.toStringAsProduction(" ::=\n ", null) + "\n");
          firstElement = false;
        } else
          outputStream.print("|" + production.toStringAsRHS(null) + "\n");
      }
    }

    if (full) {
      outputStream.print("Elements:\n");
      for (CFGElement s : elements.keySet()) {
        outputStream.print(" (" + s.toStringDetailed() + ") " + s);

        if (s.getNullable()) outputStream.print(" nullable");
        if (whitespaces.contains(s)) outputStream.print(" whitespace");
        if (cyclicNonterminals.contains(s)) outputStream.print(" cyclic");
        if (paraterminals.contains(s)) outputStream.print(" paraterminal");
        if (s.attributes != null && !s.attributes.isEmpty()) outputStream.print(" attributes: " + s.attributes);
        if (first.get(s) != null) {
          outputStream.print(" first = {");
          printElements(outputStream, first.get(s));
          outputStream.print("} follow = {");
          printElements(outputStream, follow.get(s));
          outputStream.print("}");
        }
        outputStream.print("\n");
      }
      outputStream.print("Nodes:\n");
      for (int i : numberToRulesNodeMap.keySet()) {
        CFGNode gn = numberToRulesNodeMap.get(i);
        outputStream.print(" " + i + ": " + gn.toStringAsProduction());
        if (full) {
          outputStream.print(" LHS: " + lhsOf.get(gn));
          if (gn.getNullable()) outputStream.print(" nullable");
          if (gn.getNullablePrefix()) outputStream.print(" nullablePrefix");
          if (gn.getNullableSuffix()) outputStream.print(" nullableSuffix");
          if (gn.initial) outputStream.print(" initial");
          if (gn.second) outputStream.print(" second");
          if (gn.penultimate) outputStream.print(" penultimate");
          if (gn.ultimate) outputStream.print(" final");

          if (!instanceFirst.get(gn).isEmpty()) {
            outputStream.print(" instfirst = {");
            printElements(outputStream, instanceFirst.get(gn));
            outputStream.print("} instfollow = {");
            printElements(outputStream, instanceFollow.get(gn));
            outputStream.print("}");
          }
          String action = gn.toStringActions();
          if (!action.isEmpty()) outputStream.print(" Action: " + action);
        }
        outputStream.print("\n");
      }

      outputStream.print("Accepting slot" + (acceptingSlots.size() == 1 ? "" : "s") + ":\n");
      for (var gn : acceptingSlots)
        outputStream.print(" " + gn.toStringAsProduction() + "\n");

      outputStream.print("Accepting node number" + (acceptingSlots.size() == 1 ? "" : "s") + ":");
      for (var gn : acceptingNodeNumbers)
        outputStream.print(" " + gn);
      outputStream.println();

      printSet(outputStream, declaredAsTokens, "Declared as tokens");
      printSet(outputStream, paraterminals, "Paraterminals");

      printSet(outputStream, defined, "Defined nonterminals");
      printSet(outputStream, used, "Used elements");

      // outputStream.print("Reachable all elements:\n" + reachable);
      // outputStream.print("Reachable nonterminals:\n" + reachableNonterminals);
      // outputStream.print("Reachable all elements stop at paraterminals:\n" + reachablePara);
      // outputStream.print("Reachable nonterminal stop at paraterminals:\n" + reachableNonterminalsPara);
      //
      // printSet(outputStream, cyclicNonterminals, "Cyclic nonterminals");
      //
      //
      // outputStream.print("derivesExactly(R):");
      // outputStream.println();
      // if (derivesExactly != null) for (var n : derivesExactly.getDomain())
      // if (cyclicNonterminals.contains(n)) {
      // outputStream.print(n + ": {");
      // for (var nf : derivesExactly.get(n))
      // outputStream.print(" " + nf);
      // outputStream.println(" }");
      // }
      //
      // outputStream.println("derivesExactlyTransitiveClosure (R+):");
      // if (derivesExactlyTransitiveClosure != null) for (var n : derivesExactlyTransitiveClosure.getDomain())
      // if (cyclicNonterminals.contains(n)) {
      // outputStream.print(n + ": {");
      // for (var nf : derivesExactlyTransitiveClosure.get(n))
      // outputStream.print(" " + nf);
      // outputStream.println(" }");
      // }
      //
      // outputStream.print("cocyclic nonterminals:\n");
      // if (derivesExactlyTransitiveClosure != null) for (var n : derivesExactlyTransitiveClosure.getDomain())
      // if (cyclicNonterminals.contains(n)) {
      // outputStream.print(n + ": {");
      // for (var nf : derivesExactlyTransitiveClosure.get(n))
      // if (derivesExactlyTransitiveClosure.get(nf).contains(n)) outputStream.print(" " + nf);
      // outputStream.println(" }");
      // }
      //
      // outputStream.print("cyclic slots:\n");
      // if (cyclicSlots != null) for (var n : cyclicSlots) {
      // outputStream.print(n.toStringAsProduction());
      // outputStream.println();
      // }
    }
  }

  private void printSet(PrintStream outputStream, Set<CFGElement> elementsP, String title) {
    outputStream.print(title + ": {");
    printElements(outputStream, elementsP);
    outputStream.println("}");
  }

  private void printElements(PrintStream outputStream, Set<CFGElement> elementsP) {
    if (elementsP == null) return;
    boolean firstElement = true;
    for (CFGElement e : elementsP) {
      if (firstElement)
        firstElement = false;
      else
        outputStream.print(", ");
      outputStream.print(e.toString());
    }
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indent, int depthLimit) {
    normalise();
    outputStream.print("digraph \"Reference grammar\"\n" + "{\n" + "graph[ordering=out ranksep=0.1 rankdir=\"LR\"]\n"
        + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04 color=gray]\n"
        + "edge[fontname=Helvetica fontsize=9 arrowsize = 0.3 color=gray]\n\n");
    for (CFGElement n : elementToRulesNodeMap.keySet())
      toStringDotRec(outputStream, elementToRulesNodeMap.get(n), full);
    outputStream.print("}\n");
  }

  private void toStringDotRec(PrintStream outputStream, CFGNode gn, boolean full) {
    outputStream.print("\"" + gn.num + "\"[label=\"" + gn.toStringDot());
    if (full) {
      outputStream.print("\nLHS: " + lhsOf.get(gn));
      if (gn.getNullable()) outputStream.print(" nullable");
      if (gn.getNullablePrefix()) outputStream.print(" nullablePrefix");
      if (gn.getNullableSuffix()) outputStream.print(" nullableSuffix");
      // if (initialSlots.contains(gn)) outputStream.print(" initial");
      // if (secondSlots.contains(gn)) outputStream.print(" second");
      // if (penultimateSlots.contains(gn)) outputStream.print(" penultimate");
      // if (finalSlots.contains(gn)) outputStream.print(" final");
      //
      // if (!instanceFirst.get(gn).isEmpty()) {
      // outputStream.print(" instfirst = {");
      // printElements(outputStream, instanceFirst.get(gn));
      // outputStream.print("} instfollow = {");
      // printElements(outputStream, instanceFollow.get(gn));
      // outputStream.print("}");
      // }
      String action = gn.toStringActions();
      if (!action.isEmpty()) outputStream.print(" Action: " + action);

    }
    outputStream.print("\"]\n");
    if (gn.cfgElement.cfgKind == CFGElementKind.ALT) {
      toStringDotSeqArrow(outputStream, gn, full);
      toStringDotAltArrow(outputStream, gn, full);
    } else if (gn.cfgElement.cfgKind != CFGElementKind.END) {
      toStringDotAltArrow(outputStream, gn, full);
      toStringDotSeqArrow(outputStream, gn, full);
    }
  }

  private void toStringDotAltArrow(PrintStream outputStream, CFGNode cs, boolean full) {
    if (cs.alt == null) return;
    outputStream.print(
        "\"" + cs.num + "\"->\"" + cs.alt.num + "\"" + "{rank = same; \"" + cs.num + "\"" + ";\"" + cs.alt.num + "\"" + ";" + "}" + "[label=\" a\"" + "]\n");
    if (!isLHS(cs.alt)) toStringDotRec(outputStream, cs.alt, full);
  }

  private void toStringDotSeqArrow(PrintStream outputStream, CFGNode cs, boolean full) {
    if (cs.seq == null) return;
    outputStream.print("\"" + cs.num + "\"->\"" + cs.seq.num + "\"\n");
    toStringDotRec(outputStream, cs.seq, full);
  }

  /************************************************************************************************
   *
   * Sentential form and sentence generator called from ScriptInterpreter - could be a separate class
   *
   ***********************************************************************************************/
  public void generate(int max, boolean sentencesOnly) {
    LinkedList<LinkedList<CFGElement>> sententialForms = new LinkedList<>();
    LinkedList<CFGElement> sententialForm = new LinkedList<>();
    sententialForm.add(getStartNonterminal());
    sententialForms.addLast(sententialForm);
    int leftmostNonterminalIndex;
    int count = 1;

    while (count <= max && sententialForms.size() > 0) {
      sententialForm = sententialForms.removeFirst();

      for (leftmostNonterminalIndex = 0; leftmostNonterminalIndex < sententialForm.size(); leftmostNonterminalIndex++)
        if (sententialForm.get(leftmostNonterminalIndex).cfgKind == CFGElementKind.NONTERMINAL) break;

      if (leftmostNonterminalIndex == sententialForm.size()) { // This is a sentence
        printSententialForm(count++, sententialForm);
        continue;
      }

      if (!sentencesOnly) printSententialForm(count++, sententialForm);

      CFGElement expansionNonterminal = sententialForm.get(leftmostNonterminalIndex);

      for (var altNode = elementToRulesNodeMap.get(expansionNonterminal).alt; altNode != null; altNode = altNode.alt) {
        LinkedList<CFGElement> newSententialForm = new LinkedList<>();

        for (int i = 0; i < leftmostNonterminalIndex; i++)
          newSententialForm.addLast(sententialForm.get(i));

        for (var seqNode = altNode.seq; seqNode.cfgElement.cfgKind != CFGElementKind.END; seqNode = seqNode.seq)
          switch (seqNode.cfgElement.cfgKind) {
          case TERM_CS, TERM_CI, TERM_CH, TERM_BI, NONTERMINAL:
            newSententialForm.add(seqNode.cfgElement);
            break;
          case EPSILON:
            break; // do nothing
          default:
            Util.error("illegal kind of grammar element encountered during generation: " + seqNode.cfgElement.cfgKind);
            break;
          }

        for (int i = leftmostNonterminalIndex + 1; i < sententialForm.size(); i++)
          newSententialForm.addLast(sententialForm.get(i));

        sententialForms.addLast(newSententialForm);
      }
    }
  }

  private void printSententialForm(int i, LinkedList<CFGElement> sententialForm) {

    System.out.print((isSentence(sententialForm) ? "\"" : " ") + i);
    for (var e : sententialForm)
      if (e.cfgKind == CFGElementKind.NONTERMINAL)
        System.out.print(" _" + e.str);
      else
        System.out.print(" " + e.str);
    System.out.println();
  }

  private boolean isSentence(LinkedList<CFGElement> sententialForm) {
    for (var e : sententialForm)
      if (e.cfgKind == CFGElementKind.NONTERMINAL) return false;
    return true;
  }

  boolean changed = true;

  Set<CFGElementKind> newParentElements = Set.of(CFGElementKind.PAR, CFGElementKind.OPT, CFGElementKind.POS, CFGElementKind.KLN);

  private void computeFirstFollowGuardSets() {
    follow.add(getStartNonterminal(), endOfStringElement);
    while (changed) {
      changed = false;
      for (var e : elements.keySet()) // Iterate over all grammar elements
        switch (e.cfgKind) {
        case EPSILON:
          changed |= first.add(e, epsilonElement);
          break;
        case TERM_CS, TERM_CI, TERM_BI, TERM_CH:
          changed |= first.add(e, e);
          break;
        case NONTERMINAL:
          computeFirstFollowSetsRec(elementToRulesNodeMap.get(e), elementToRulesNodeMap.get(e).alt);
          break;
        default:
          ; // skip everything else
        }
    }
  }

  private void computeFirstFollowSetsRec(CFGNode parentNode, CFGNode traverserNode) {
    // Traversal calls below this line
    if (traverserNode.cfgElement.cfgKind == CFGElementKind.END) // Reached the end of a sequence
      return;
    computeFirstFollowSetsRec(parentNode, traverserNode.seq); // continue across sequence
    if (traverserNode.alt != null) //
      computeFirstFollowSetsRec(newParentElements.contains(traverserNode.cfgElement.cfgKind) ? traverserNode : parentNode, traverserNode.alt);
    // Util.debug("computeFirstFollowSetsRec with parent " + parentNode + " and traverser node " + traverserNode);
    switch (traverserNode.cfgElement.cfgKind) {
    case TERM_CS, TERM_CI, TERM_BI, TERM_CH, EPSILON:
      if (instanceFirst.get(traverserNode).isEmpty()) changed |= instanceFirst.addAll(traverserNode, first.get(traverserNode.cfgElement));
      break;
    case NONTERMINAL:
      changed |= instanceFirst.addAll(traverserNode, first.get(traverserNode.cfgElement));
      var firstOfNext = instanceFirst.get(traverserNode.seq);
      if (firstOfNext.contains(epsilonElement)) {
        for (var f : firstOfNext)
          if (!f.equals(epsilonElement)) changed |= instanceFollow.add(traverserNode, f);
      }
      changed |= instanceFollow.addAll(traverserNode, firstOfNext);
      changed |= follow.addAll(traverserNode.cfgElement, instanceFollow.get(traverserNode)); // Union instance follow of this node into follow of payload
      break;
    case ALT:
      changed |= instanceFirst.addAll(traverserNode, instanceFirst.get(traverserNode.seq));
      // If this ALT is at top level, then its parent will be a nonterminal in which case union first of this alt into the LHS first set
      if (parentNode.cfgElement.cfgKind == CFGElementKind.NONTERMINAL) changed |= first.addAll(parentNode.cfgElement, instanceFirst.get(traverserNode));
      break;
    case END:
      changed |= instanceFirst.add(traverserNode, epsilonElement);
      break;
    case PAR:
      changed |= instanceFirst.addAll(traverserNode, instanceFirst.get(traverserNode.alt));
      break;
    case OPT:
      changed |= instanceFirst.addAll(traverserNode, instanceFirst.get(traverserNode.alt));
      changed |= instanceFirst.add(traverserNode, epsilonElement);
      break;
    case POS:
      changed |= instanceFirst.addAll(traverserNode, instanceFirst.get(traverserNode.alt));
      break;
    case KLN:
      changed |= instanceFirst.addAll(traverserNode, instanceFirst.get(traverserNode.alt));
      changed |= instanceFirst.add(traverserNode, epsilonElement);
      break;

    default:
      Util.fatal("Unexpected cfgKind encountered in computeFirstFollowGuardSetsRec(CFGNode): " + traverserNode.cfgElement.cfgKind);
    }
  }
}
