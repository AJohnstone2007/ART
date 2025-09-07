
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
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public final class CFGRules implements DisplayInterface { // final to avoid this-escape
  // Fields set by the script interpreter that must be cloned
  private boolean clean = false; // set True by normalise; reset by anything that touches the data stuctures
  public static int nextFreeCFGRulesNumber = 1;
  private int nextUniqueLabelNumber = 0;
  private final int nextGeneratedNonterminalNumber = 1;

  public String nextUniqueLabel() {
    return Integer.toString(++nextUniqueLabelNumber);
  }

  // TODO: These need setters that set clean to false
  public final int cfgRulesNumber;
  public final CFGRulesKind cfgRulesKind;
  public Set<Character> characters = new HashSet<>();;
  public final Map<CFGElement, CFGElement> elements = new TreeMap<>(); // We use a map for elements because we need there to be one canonical instance
  public final Set<CFGElement> paraterminals = new TreeSet<>();
  public final Set<CFGElement> declaredAsTokens = new TreeSet<>();
  public boolean seenWhitespaceDirective = false;
  public final Set<CFGElement> whitespaces = new TreeSet<>();
  public String filePrelude = null;
  public String classPrelude = null;
  public CFGElement startNonterminal = null;

  // Static fields that are constant across all instances and thus do not need to be cloned
  private final static Set<CFGKind> terminalKinds = Set.of(CFGKind.TRM_BI, CFGKind.TRM_CH, CFGKind.TRM_CS, CFGKind.TRM_CI, CFGKind.TRM_CH_SET,
      CFGKind.TRM_CH_ANTI_SET);
  private final static Set<CFGKind> bracketKinds = Set.of(CFGKind.PAR, CFGKind.OPT, CFGKind.KLN, CFGKind.POS);
  private final static Set<CFGKind> selfFirst = Set.of(CFGKind.TRM_BI, CFGKind.TRM_CH, CFGKind.EOS, CFGKind.TRM_CS, CFGKind.TRM_CI, CFGKind.EPSILON);
  private final static Set<CFGKind> scaffoldingKinds = Set.of(CFGKind.SOS, CFGKind.EOS, CFGKind.EPSILON, CFGKind.ALT, CFGKind.END, CFGKind.PAR, CFGKind.OPT,
      CFGKind.POS, CFGKind.KLN);
  private final static Set<CFGKind> doNotCloneKinds = Set.of(CFGKind.ALT, CFGKind.END, CFGKind.PAR, CFGKind.OPT, CFGKind.POS, CFGKind.KLN);

  // Fields that are computed by !try or normalise() and thus do not need to be cloned
  private int nextFreeEnumerationElement;
  public final CFGElement epsilonElement;
  public final CFGElement startOfStringElement;
  public final CFGElement endOfStringElement;
  public final CFGNode endOfStringNode;
  public final Map<Integer, CFGNode> numberToRulesNodeMap = new TreeMap<>();
  public final Map<CFGElement, CFGNode> elementToRulesNodeMap = new TreeMap<>(); // Map from nonterminals to list of productions represented by their LHS node
  public int lexSize;
  public Set<CFGElement> defined = new TreeSet<>();
  public Set<CFGElement> used = new TreeSet<>();

  public final AbstractRelation<CFGElement, CFGElement> reachable = new RelationOrdered<>();
  public final AbstractRelation<CFGElement, CFGElement> reachableNonterminals = new RelationOrdered<>();
  public final AbstractRelation<CFGElement, CFGElement> reachablePara = new RelationOrdered<>();
  public final AbstractRelation<CFGElement, CFGElement> reachableNonterminalsPara = new RelationOrdered<>();
  public final Set<CFGElement> parserNonterminals = new TreeSet<>(); // Nonterminals reachable from start symbol, stopping at (and not including) paraterminals
  public Set<CFGElement> reachableParaterminals; // Paraterminals reachable from start symbol
  public final Set<CFGElement> lexerNonterminals = new TreeSet<>(); // Nonterminals reachable from every reachable paraterminal, including paraterminals

  public final AbstractRelation<CFGElement, CFGElement> first = new Relation<>();
  public final AbstractRelation<CFGElement, CFGElement> follow = new Relation<>();

  public final AbstractRelation<CFGNode, CFGElement> instanceFirst = new Relation<>(); // definition?
  public final AbstractRelation<CFGNode, CFGElement> instanceFollow = new Relation<>(); // definition?

  public final Set<CFGNode> initialSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \alpha = \epsilon }
  public final Set<CFGNode> secondSlots = new HashSet<>(); // { X ::= \alpha Y . \beta} | \alpha = \epsilon, Y \ne \epsilon }
  public final Set<CFGNode> penultimateSlots = new HashSet<>(); // { X ::= \alpha . Y \beta} | \beta = \epsilon, Y \ne \epsilon }
  public final Set<CFGNode> finalSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \beta = \epsilon }

  public final Set<CFGNode> nullablePrefixSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \alpha =>* \epsilon }
  public final Set<CFGNode> nullableSuffixSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \beta =>* \epsilon }
  public final Set<CFGElement> cyclicNonterminals = new HashSet<>(); // { X ::= \alpha X \beta} | \alpha,\beta =>* \epsilon }
  public final Set<CFGNode> cyclicSlots = new HashSet<>(); // { X ::= \alpha X \beta} | \alpha,\beta =>* \epsilon }
  public AbstractRelation<CFGElement, CFGElement> derivesExactly;
  public AbstractRelation<CFGElement, CFGElement> derivesExactlyTransitiveClosure;

  public final Set<CFGNode> acceptingSlots = new HashSet<>(); // Set of slots which are END nodes of the start production
  public final Set<Integer> acceptingNodeNumbers = new TreeSet<>(); // Set of node number for the slots on accepting slots

  public CFGRules cfgRulesLexer; // Subgrammar for lexing - terminals and rules below paraterminals
  public CFGRules cfgRulesParser; // Subgrammar for parsing - rules above paraterminals

  public CFGRules(CFGRulesKind cfgRulesKind) { // Ab initio constructor for an empty rules set
    cfgRulesNumber = nextFreeCFGRulesNumber++;
    this.cfgRulesKind = cfgRulesKind;
    // Util.debug("Constructing cfgRulesNumber " + cfgRulesNumber + " " + cfgRulesKind);
    epsilonElement = findElement(CFGKind.EPSILON, "#"); // We are not using an initialiser block because elements must be initialised first
    endOfStringElement = findElement(CFGKind.EOS, "$");
    startOfStringElement = findElement(CFGKind.SOS, "$$");

    endOfStringNode = new CFGNode(this, CFGKind.EOS, "$", 0, GIFTKind.NONE, null, null);
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode
  }

  /*
   * This is a copy constuctor which makes a fresh set of elements and optionally modifies the rules as they are copied It is important that the terminal and
   * paraterminal numberings match the source element numberings because this constructor is used to create the lexer and parser sub-grammars which need to be
   * in sync
   */
  private final Map<String, Integer> terminalNumbers = new TreeMap<>();

  public CFGRules(CFGRules src, CFGRulesKind cfgRulesKind, boolean character, boolean createParaterminals, boolean bnfLeft, boolean bnfRight) {
    if (src.cfgRulesKind == CFGRulesKind.USER) src.normalise();
    cfgRulesNumber = nextFreeCFGRulesNumber++;
    this.cfgRulesKind = cfgRulesKind; // Do not preserve the original kind
    // Util.debug("Copy constructing cfgRulesNumber " + cfgRulesNumber + " " + cfgRulesKind + " with parentnumber " + src.cfgRulesNumber);
    epsilonElement = findElement(CFGKind.EPSILON, "#"); // We are not using an initialiser block because elements must be initialised first
    endOfStringElement = findElement(CFGKind.EOS, "$");
    startOfStringElement = findElement(CFGKind.SOS, "$$");

    endOfStringNode = new CFGNode(this, CFGKind.EOS, "$", 0, GIFTKind.NONE, null, null);
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode

    for (var e : src.elements.keySet()) // Make new elements and collect TRM_CS elements
      if (character && e.cfgKind == CFGKind.TRM_CS)
        terminalNumbers.put(e.str, 0);
      else if (!doNotCloneKinds.contains(e.cfgKind)) findElement(e.cfgKind, e.str);

    if (character) {
      int terminalNumber = 0;

      if (src.whitespaces.size() > 0) { // Create WS nonterminal
        actionLHS("ART_" + terminalNumber++);
        actionALT();
        actionSEQ(CFGKind.KLN, nextUniqueLabel(), ScriptInterpreter.iTerms.termEmpty);
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
          trm_CS_to_TRM_CH(s, src.whitespaces.size() > 0);

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
    if (src.startOfStringElement != null && src.startNonterminal != null)
      startNonterminal = findElement(src.startNonterminal.cfgKind, src.startNonterminal.str);
    filePrelude = src.filePrelude; // Strings are immutable so we can just assign reference
    classPrelude = src.classPrelude; // Strings are immutable so we can just assign reference
    // Now build the rules
    for (var n : src.elementToRulesNodeMap.keySet())
      if (n.cfgKind == CFGKind.NONTERMINAL) {
        // Util.debug("CFGRules copy constructor mode " + cfgRulesKind + ": adding rule for " + n);
        actionLHS(n.str);
        cloneGrammarRulesRec(src, src.elementToRulesNodeMap.get(n).alt, character, createParaterminals, bnfLeft, bnfRight);
      }
    dequeueRules(src, cfgRulesKind, character, createParaterminals, bnfLeft, bnfRight);
    if (character) { // We've handled whitespace explictly
      whitespaces.clear();
      seenWhitespaceDirective = true;
    }
  }

  private void trm_CS_to_TRM_CH(String s, boolean addWS) {
    for (int i = 0; i < s.length(); i++)
      actionSEQ(CFGKind.TRM_CH, s.substring(i, i + 1), ScriptInterpreter.iTerms.termEmpty);
    if (addWS) actionSEQ(CFGKind.NONTERMINAL, "ART_0", ScriptInterpreter.iTerms.termEmpty);
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
    case TRM_CS:
      if (character) {
        // Util.debug("Expanding to characters: " + cfgNode);
        if (createParaterminals)
          actionSEQ(CFGKind.NONTERMINAL, "ART_" + terminalNumbers.get(cfgNode.cfgElement.str), cfgNode.actionAsTerm);
        else
          trm_CS_to_TRM_CH(cfgNode.cfgElement.str, src.whitespaces.size() > 0);
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

  private void dequeueRules(CFGRules src, CFGRulesKind cfgRulesKind, boolean character, boolean createParaterminals, boolean closureLeft,
      boolean closureRight) {
    QueueRuleElement ruleElement;
    while ((ruleElement = listRules.poll()) != null) {
      actionLHS("ART_" + ruleElement.number);
      actionALT();
      cloneGrammarRulesRec(src, ruleElement.cfgNode, character, false, closureLeft, closureRight);
    }
  }

  public void normalise() { // Compute the fields that are not directly set by the script interpeter
    if (clean) return;
    // Util.debug("Normalising cfgRulesNumber " + nextFreeCFGRulesNumber + " " + cfgRulesKind);
    if (!seenWhitespaceDirective) whitespaces.add(findElement(CFGKind.TRM_BI, "SIMPLE_WHITESPACE"));

    derivesExactly = new Relation<CFGElement, CFGElement>();

    // Add singleton grammar nodes for terminals, # and epsilon. These are used by the SPPF.
    for (CFGElement e : elements.keySet())
      if (e.cfgKind == CFGKind.TRM_BI || e.cfgKind == CFGKind.TRM_CS || e.cfgKind == CFGKind.TRM_CI || e.cfgKind == CFGKind.TRM_CH
          || e.cfgKind == CFGKind.TRM_CH_SET || e.cfgKind == CFGKind.TRM_CH_ANTI_SET || e.cfgKind == CFGKind.EPSILON)
        elementToRulesNodeMap.put(e, new CFGNode(this, e.cfgKind, e.str, 0, GIFTKind.NONE, null, null));

    // Element and node numbering
    nextFreeEnumerationElement = 0;
    numberElementsAndNodes();
    setEndNodeLinks();

    // Load characters
    String printableASCII = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    String defaultCharacterSet = "\r\n\t£" + printableASCII;
    for (CFGElement e : elements.keySet())
      switch (e.cfgKind) {
      case TRM_BI:
        switch (e.str) {
        case "ID":
          addCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
          break;
        case "INTEGER":
          addCharacters("0123456789");
          break;
        case "REAL":
          addCharacters("0123456789.");
          break;
        case "SIMPLE_WHITESPACE":
          addCharacters("\r\n\t ");
          break;
        }
        break;
      case TRM_CS, TRM_CI, TRM_CH, TRM_CH_SET, TRM_CH_ANTI_SET:
        addCharacters(e.str);
        break;
      }

    // Set positional attributes and accepting slots, and seed nullablePrefixSlots and nullableSuffixSlots
    for (CFGElement ge : elements.keySet())
      if (ge.cfgKind == CFGKind.NONTERMINAL && elementToRulesNodeMap.get(ge) != null)
        for (CFGNode gn = elementToRulesNodeMap.get(ge).alt; gn != null; gn = gn.alt) {
          CFGNode gs = gn.seq;
          initialSlots.add(gs);
          nullablePrefixSlots.add(gs);
          if (gs.seq.cfgElement.cfgKind != CFGKind.END) secondSlots.add(gs.seq);
          while (gs.seq.cfgElement.cfgKind != CFGKind.END)
            gs = gs.seq;
          penultimateSlots.add(gs);
          finalSlots.add(gs.seq);
          nullableSuffixSlots.add(gs.seq);
          if (ge == startNonterminal) {
            acceptingSlots.add(gs.seq);
            acceptingNodeNumbers.add(gs.seq.num);
          }
        }

    // Look for dubious literals
    if (cfgRulesKind == CFGRulesKind.USER) for (var e : elements.keySet())
      if (e.cfgKind == CFGKind.TRM_CI || e.cfgKind == CFGKind.TRM_CS)
        if (Util.containsWhitespace(e.str)) Util.warning("Context free terminal contains whitespace: " + e);

    // Compute reachability relation
    computeReachabilities();

    // Check kindof nonterminals
    computeAndCheckNonterminalSets();

    // First and follow sets
    if (startNonterminal != null) follow.add(startNonterminal, endOfStringElement);
    for (CFGElement ge : elements.keySet())
      if (selfFirst.contains(ge.cfgKind)) first.add(ge, ge);
    computeFirstSetsAndNullablePrefixes();
    computeNullableSuffixesAndCyclic();
    computeFollowSets();
    computeCyclicSlots();//

    // Collect attributes
    for (CFGElement e : elements.keySet()) {
      Map<String, Integer> rhsNonterminalsInProduction = new HashMap<>();
      if (e.cfgKind == CFGKind.NONTERMINAL && elementToRulesNodeMap.get(e) != null) { // Only look at nonterminals
        String lhs = e.str;
        for (CFGNode gn = elementToRulesNodeMap.get(e).alt; gn != null; gn = gn.alt) { // iterate over the productions
          rhsNonterminalsInProduction.clear();
          for (CFGNode gs = gn.seq; gs.cfgElement.cfgKind != CFGKind.END; gs = gs.seq) {
            // Util.info("Collecting RHS nonterminals at " + gs + " " + ScriptTermInterpreter.iTerms.toRawString(gs.slotTerm));
            if (gs.cfgElement.cfgKind == CFGKind.NONTERMINAL) {
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
          for (CFGNode gs = gn.seq; gs.cfgElement.cfgKind != CFGKind.END; gs = gs.seq) {
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

    clean = true;

    // Construct lexer and parser subgrammars ONLY if we are a USER grammar
    // Note, by the time we get here, we are fully normalised so the subgrammars can use our reachability information
    if (cfgRulesKind == CFGRulesKind.USER) {
      cfgRulesLexer = new CFGRules(this, CFGRulesKind.LEXER, false, false, false, false);
      cfgRulesLexer.normalise(); // recurse only once for the lexer grammar
      cfgRulesParser = new CFGRules(this, CFGRulesKind.PARSER, false, false, false, false);
      cfgRulesParser.normalise(); // recurse only once for the parser grammar
      subGrammarConsistencyCheck();
    }
  }

  private void addCharacters(String string) {
    for (var c : string.toCharArray())
      characters.add(c);
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
  private void computeReachabilities() {
    for (var lhs : elements.keySet())
      if (lhs.cfgKind == CFGKind.NONTERMINAL && elementToRulesNodeMap.get(lhs) != null) { // avoid nonterminals that have no rules in this grammar
        reachable.add(lhs); // Make empty relation codomains
        reachableNonterminals.add(lhs);
        reachablePara.add(lhs);
        reachableNonterminalsPara.add(lhs);
        computeReachabilityRec(lhs, elementToRulesNodeMap.get(lhs), reachable, reachableNonterminals, false);
        computeReachabilityRec(lhs, elementToRulesNodeMap.get(lhs), reachablePara, reachableNonterminalsPara, true);
      }
    reachable.transitiveClosure();
    reachableNonterminals.transitiveClosure();
    reachablePara.transitiveClosure();
    reachableNonterminalsPara.transitiveClosure();
  }

  private void computeReachabilityRec(CFGElement lhs, CFGNode topNode, AbstractRelation<CFGElement, CFGElement> allRel,
      AbstractRelation<CFGElement, CFGElement> nonterminalRel, boolean stopOnParaterminal) {
    // if (cfgRulesKind == CFGRulesKind.USER) Util.debug("computeReachabilityRec with LHS " + lhs + " and topNode " + topNode);
    if (topNode == null) return;
    if (allRel.get(lhs).contains(topNode.cfgElement)) return; // Don't cycle
    if (stopOnParaterminal && paraterminals.contains(lhs)) return;

    for (var altNode = topNode.alt; altNode != null; altNode = altNode.alt)
      for (var seqNode = altNode.seq; seqNode.cfgElement.cfgKind != CFGKind.END; seqNode = seqNode.seq) {
        if (bracketKinds.contains(seqNode.cfgElement.cfgKind))
          computeReachabilityRec(lhs, seqNode, allRel, nonterminalRel, stopOnParaterminal);
        else {
          addElements(allRel, lhs, seqNode);
          if (seqNode.cfgElement.cfgKind == CFGKind.NONTERMINAL) {
            addElements(nonterminalRel, lhs, seqNode);
            computeReachabilityRec(lhs, elementToRulesNodeMap.get(seqNode.cfgElement), allRel, nonterminalRel, stopOnParaterminal);
          }
        }
      }
  }

  // convenience method to bind all of the elements represented by a node: either just the element, or unpack the set of CH_SET and CH_ANTI_SET
  private void addElements(AbstractRelation<CFGElement, CFGElement> relation, CFGElement key, CFGNode node) {
    // if (cfgRulesKind == CFGRulesKind.USER) Util.debug("Binding " + node.cfgElement + " to key " + key + " in " + relation);
    relation.add(key, node.cfgElement);
    if (node.cfgElement.cfgKind == CFGKind.TRM_CH_SET || node.cfgElement.cfgKind == CFGKind.TRM_CH_ANTI_SET) for (var c : node.cfgElement.set)
      relation.add(key, findElement(CFGKind.TRM_CH, c.toString()));
  }

  /*
   * Having built reachbility sets, compute nonterminals kind sets and ensure they form a partition of the nonterminal elements
   *
   * parserNonterminals are nonterminals reachable from start symbol, stopping at (and not including) paraterminals
   *
   * reachableParaterminals are paraterminals reachable from start symbol
   *
   * lexerNonterminals are nonterminals reachable from every reachable paraterminal, including paraterminals
   */

  private void computeAndCheckNonterminalSets() {
    if (startNonterminal == null) return; // empty grammar
    parserNonterminals.addAll(reachableNonterminalsPara.get(startNonterminal));
    parserNonterminals.add(startNonterminal);
    reachableParaterminals = new TreeSet(parserNonterminals);

    parserNonterminals.removeAll(paraterminals);
    reachableParaterminals.retainAll(paraterminals);

    for (var n : reachableParaterminals)
      lexerNonterminals.addAll(reachableNonterminals.get(n)); // Note we want all reachable nonterminals including paraterminals

    if (cfgRulesKind == CFGRulesKind.USER) {
      TreeSet<CFGElement> tmp = new TreeSet<>(elements.keySet());
      tmp.removeAll(reachable.get(startNonterminal));
      tmp.remove(startNonterminal);
      tmp.removeAll(whitespaces);
      for (var e : elements.keySet())
        if (scaffoldingKinds.contains(e.cfgKind)) tmp.remove(e);
      if (!tmp.isEmpty()) {
        Util.warning("unused CFG element" + (tmp.size() == 1 ? "" : "s") + ": " + tmp);
      }

      tmp = new TreeSet<>(paraterminals);
      tmp.removeAll(reachable.get(startNonterminal));
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

  private void subGrammarConsistencyCheck() {
    for (var n : elementToRulesNodeMap.keySet())
      if (terminalKinds.contains(n)) {
        var mainElement = elementToRulesNodeMap.get(n);
        var lexerElement = cfgRulesLexer.elementToRulesNodeMap.get(n);
        var parserElement = cfgRulesParser.elementToRulesNodeMap.get(n);

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

  Set<CFGElement> removeEpsilon(Set<CFGElement> set) {
    Set<CFGElement> tmp = new HashSet<>(set);
    tmp.remove(epsilonElement);
    return tmp;
  }

  private void computeFirstSetsAndNullablePrefixes() {
    // Closure loop
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CFGElement lhs : elements.keySet())
        if (lhs.cfgKind == CFGKind.NONTERMINAL) {
          CFGNode topNode = elementToRulesNodeMap.get(lhs);
          if (topNode == null) continue;
          // Util.info("Visiting top node " + topNode.num + ":" + topNode);
          for (CFGNode altNode = topNode.alt; altNode != null; altNode = altNode.alt) {
            // Util.info("Visiting alt node " + altNode.num + ":" + altNode);
            CFGNode seqNode = altNode;
            boolean seenOnlyNullable = true;
            while (true) {
              // Flow control
              seqNode = seqNode.seq;
              // Util.info("Visiting seq node " + seqNode.num + ":" + seqNode + " with seenOnlyNullable " + seenOnlyNullable);

              // 1. Capture nullable prefixes
              if (seenOnlyNullable) {
                // Util.info("Adding nullable prefixslot " + seqNode.toStringAsProduction());
                changed |= nullablePrefixSlots.add(seqNode);
              }

              if (seqNode.cfgElement.cfgKind == CFGKind.END) break;

              // 2. Update instance first with the element itself for nonterminals only
              if (seqNode.cfgElement.cfgKind == CFGKind.NONTERMINAL) changed |= instanceFirst.add(seqNode, seqNode.cfgElement);

              // 3. Fold in the first set of this node's element after suppressing epsilon
              changed |= instanceFirst.addAll(seqNode, removeEpsilon(first.get(seqNode.cfgElement))); // Update instance first with element first

              // 4. If epsilon is in this node's element's first set, fold the first set of our successor in
              if (first.get(seqNode.cfgElement).contains(epsilonElement))
                changed |= instanceFirst.addAll(seqNode, instanceFirst.get(seqNode.seq));
              else // otherwise reset seenOnlyNullable
                seenOnlyNullable = false;
            }
            if (seenOnlyNullable) {
              // Util.info("All elements in sequence are nullable; adding epsilon to first of " + lhs);
              changed |= first.add(lhs, epsilonElement); // If the whole sequence is made up of nullable elements, thenadd epsilon to the left hand side
            }
            // Now fold the instance first set of the first slot in this sequence into the LHS
            changed |= first.addAll(lhs, instanceFirst.get(altNode.seq));
          }
        }
    }
  }

  private void computeNullableSuffixesAndCyclic() {
    for (CFGElement lhs : elements.keySet())
      if (lhs.cfgKind == CFGKind.NONTERMINAL) {
        CFGNode topNode = elementToRulesNodeMap.get(lhs);
        if (topNode == null) continue;
        // Util.info("Compute nullable suffix visiting top node " + topNode.num + ":" + topNode);
        for (CFGNode altNode = topNode.alt; altNode != null; altNode = altNode.alt) {
          // Util.info("Compute nullable suffix visiting alt node " + altNode.num + ":" + altNode);
          computeNullableSuffixAndDerivesExactlyRec(lhs, altNode.seq);
        }
      }
    derivesExactlyTransitiveClosure = new Relation<>(derivesExactly);
    derivesExactlyTransitiveClosure.transitiveClosure();
    for (var n : derivesExactlyTransitiveClosure.getDomain())
      if (derivesExactlyTransitiveClosure.get(n).contains(n)) cyclicNonterminals.add(n);
  }

  private void computeCyclicSlots() {
    for (CFGElement lhs : elements.keySet())
      if (lhs.cfgKind == CFGKind.NONTERMINAL) {
        CFGNode topNode = elementToRulesNodeMap.get(lhs);
        if (topNode == null) continue;
        // Util.info("Compute nullable suffix visiting top node " + topNode.num + ":" + topNode);
        for (CFGNode altNode = topNode.alt; altNode != null; altNode = altNode.alt) {
          CFGNode seqNode = altNode;
          boolean seenOnlyNullable = true;
          while (true) {
            // Flow control
            seqNode = seqNode.seq;
            // Util.info("Visiting seq node " + seqNode.num + ":" + seqNode + " with seenOnlyNullable " + seenOnlyNullable);

            if (cyclicNonterminals.contains(seqNode.cfgElement) && derivesExactlyTransitiveClosure.get(seqNode.cfgElement).contains(lhs))
              cyclicSlots.add(seqNode.seq);
            if (seqNode.cfgElement.cfgKind == CFGKind.END) break;
          }
        }
      }
  }

  private boolean computeNullableSuffixAndDerivesExactlyRec(CFGElement lhs, CFGNode seqNode) {
    boolean nullableSuffix;
    // Util.info("Compute nullable suffix REC visiting seq node " + seqNode.num + ":" + seqNode + " under lhs " + lhs);

    if (seqNode.cfgElement.cfgKind == CFGKind.END)
      nullableSuffix = true;
    else
      nullableSuffix = computeNullableSuffixAndDerivesExactlyRec(lhs, seqNode.seq) && first.get(seqNode.cfgElement).contains(epsilonElement);

    if (nullableSuffix) nullableSuffixSlots.add(seqNode);

    if (seqNode.cfgElement.cfgKind == CFGKind.NONTERMINAL && nullablePrefixSlots.contains(seqNode) && nullableSuffixSlots.contains(seqNode.seq))
      derivesExactly.add(lhs, seqNode.cfgElement);

    return nullableSuffix;
  }

  private void computeFollowSets() {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CFGElement lhs : elements.keySet())
        if (lhs.cfgKind == CFGKind.NONTERMINAL) {
          CFGNode topNode = elementToRulesNodeMap.get(lhs);
          if (topNode == null) continue;
          // Util.info("Visiting top node " + topNode.num + ":" + topNode);
          for (CFGNode altNode = topNode.alt; altNode != null; altNode = altNode.alt) {
            // Util.info("Visiting alt node " + altNode.num + ":" + altNode);
            CFGNode seqNode = altNode.seq;
            while (true) {
              changed |= instanceFollow.addAll(seqNode, removeEpsilon(instanceFirst.get(seqNode.seq)));
              if (seqNode.cfgElement.cfgKind == CFGKind.NONTERMINAL)
                changed |= follow.addAll(seqNode.cfgElement, removeEpsilon(instanceFirst.get(seqNode.seq)));
              if (nullableSuffixSlots.contains(seqNode.seq)) changed |= follow.addAll(seqNode.cfgElement, follow.get(lhs));
              if (seqNode.cfgElement.cfgKind == CFGKind.END) break;
              seqNode = seqNode.seq;
            }
          }
        }
    }
  }

  public boolean isEmpty() {
    return elementToRulesNodeMap.keySet().isEmpty();
  }

  private class InstancePair {
    String nonterminalID;
    String attributeID;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attributeID == null) ? 0 : attributeID.hashCode());
      result = prime * result + ((nonterminalID == null) ? 0 : nonterminalID.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      InstancePair other = (InstancePair) obj;
      if (attributeID == null) {
        if (other.attributeID != null) return false;
      } else if (!attributeID.equals(other.attributeID)) return false;
      if (nonterminalID == null) {
        if (other.nonterminalID != null) return false;
      } else if (!nonterminalID.equals(other.nonterminalID)) return false;
      return true;
    }

  }

  private void addAttribute(String nonterminalID, String attributeID) {
    if (findElement(CFGKind.NONTERMINAL, nonterminalID).attributes.get(attributeID) == null)
      findElement(CFGKind.NONTERMINAL, nonterminalID).attributes.put(attributeID, "int");
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

  private void numberElementsAndNodes() {
    for (CFGElement s : elements.keySet()) {
      s.number = nextFreeEnumerationElement++;
      if (terminalKinds.contains(s.cfgKind)) lexSize = s.number;
      // Util.info("Enumerating grammar element " + s.ei + ": " + s.str);
    }
    lexSize++;

    numberToRulesNodeMap.put(endOfStringNode.num = nextFreeEnumerationElement++, endOfStringNode);
    for (CFGElement n : elementToRulesNodeMap.keySet())
      numberElementsAndNodesRec(elementToRulesNodeMap.get(n));
  }

  private void numberElementsAndNodesRec(CFGNode node) {
    if (node != null) {
      numberToRulesNodeMap.put(node.num = nextFreeEnumerationElement++, node);
      if (node.cfgElement.cfgKind != CFGKind.END) {
        numberElementsAndNodesRec(node.seq);
        numberElementsAndNodesRec(node.alt);
      }
    }
  }

  private void setEndNodeLinks() {
    for (CFGElement n : elementToRulesNodeMap.keySet()) {
      CFGNode lhs = elementToRulesNodeMap.get(n);
      for (CFGNode production = lhs.alt; production != null; production = production.alt)
        setEndNodeLinksRec(lhs, production);
    }
  }

  private void setEndNodeLinksRec(CFGNode parentNode, CFGNode altNode) {
    CFGNode gn;
    for (gn = altNode.seq; gn.cfgElement.cfgKind != CFGKind.END; gn = gn.seq) {
      // Util.info("processEndNodes at " + gn.ni + " " + gn);
      if (gn.alt != null) for (CFGNode alternate = gn.alt; alternate != null; alternate = alternate.alt)
        setEndNodeLinksRec(gn, alternate); // Recurse into brackets
    }
    // Util.info("processEndNodes processing " + gn.ni + " " + gn);
    gn.alt = altNode; // We are now at the end of a production or of a bracketed alternate
    gn.seq = parentNode;
    if (parentNode == elementToRulesNodeMap.get(startNonterminal)) acceptingNodeNumbers.add(gn.num);
    // Util.info("processEndNodes updated alt and seq to " + gn.alt.ni + " " + gn.seq.ni);
  }

  // Atttribute-action functions from ReferenceGrammarParser.art below this line
  public CFGNode workingNode;
  public GIFTKind workingFold = GIFTKind.NONE;
  // public int workingAction = 0;

  CFGNode mostRecentLHS;

  /* Action routines called from the script term traverser */
  public CFGElement findElement(CFGKind kind, String s) {
    if (s == null) {
      Util.debug("bang");
    }
    CFGElement candidate = new CFGElement(kind, s);
    if (elements.get(candidate) == null) elements.put(candidate, candidate);
    var ret = elements.get(candidate);

    switch (kind) { // Create individual character terminals from the contents of the set
    case TRM_CH_SET, TRM_CH_ANTI_SET:
      for (var c : ret.set)
        findElement(CFGKind.TRM_CH, "" + c);
      break;
    }
    return ret;
  }

  LinkedList<CFGNode> stack = new LinkedList<>();

  public void actionLHS(String id) {
    CFGElement element = findElement(CFGKind.NONTERMINAL, id);
    defined.add(element);
    if (startNonterminal == null) startNonterminal = element;
    workingNode = elementToRulesNodeMap.get(element);
    if (workingNode == null) elementToRulesNodeMap.put(element, actionSEQ(CFGKind.NONTERMINAL, id, 0));
    mostRecentLHS = elementToRulesNodeMap.get(element);
    clean = false;
  }

  public void actionALT() {
    stack.push(workingNode);
    while (workingNode.alt != null) // Maintain specification order by adding new alternate at the end
      workingNode = workingNode.alt;
    workingNode = new CFGNode(this, CFGKind.ALT, nextUniqueLabel(), 0, workingFold, null, workingNode);
    workingFold = GIFTKind.NONE;
    clean = false;
  }

  public CFGNode actionSEQ(CFGKind kind, String str, Integer actionAsTerm) {
    // Util.debug("Update working node with kind " + kind + " string " + str + " and slot term " + slotTerm);
    workingNode = new CFGNode(this, kind, str, actionAsTerm, workingFold, workingNode, null);
    if (kind != CFGKind.END) used.add(workingNode.cfgElement);
    workingFold = GIFTKind.NONE;
    clean = false;
    return workingNode;
  }

  public void actionAttribute(String name, String type) {
    mostRecentLHS.cfgElement.attributes.put(name, type);
    clean = false;
  }

  public void actionEND(String actions) {
    actionSEQ(CFGKind.END, "", 0);
    workingNode = stack.pop();
    clean = false;
  }

  /** Support for table driven parsers ***************************************/

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

  /** Static methods *********************************************************/

  public static boolean isLHS(CFGNode gn) {
    if (gn == null) return false;
    return gn.cfgElement != null && gn.cfgElement.cfgKind == CFGKind.NONTERMINAL && gn.seq == null;
  }

  public void generate(int max, boolean sentencesOnly) {
    LinkedList<LinkedList<CFGElement>> sententialForms = new LinkedList<>();
    LinkedList<CFGElement> sententialForm = new LinkedList<>();
    sententialForm.add(startNonterminal);
    sententialForms.addLast(sententialForm);
    int leftmostNonterminalIndex;
    int count = 1;

    while (count <= max && sententialForms.size() > 0) {
      sententialForm = sententialForms.removeFirst();

      for (leftmostNonterminalIndex = 0; leftmostNonterminalIndex < sententialForm.size(); leftmostNonterminalIndex++)
        if (sententialForm.get(leftmostNonterminalIndex).cfgKind == CFGKind.NONTERMINAL) break;

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

        for (var seqNode = altNode.seq; seqNode.cfgElement.cfgKind != CFGKind.END; seqNode = seqNode.seq)
          switch (seqNode.cfgElement.cfgKind) {
          case TRM_CS, TRM_CI, TRM_CH, TRM_BI, NONTERMINAL:
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
      if (e.cfgKind == CFGKind.NONTERMINAL)
        System.out.print(" _" + e.str);
      else
        System.out.print(" " + e.str);
    System.out.println();
  }

  private boolean isSentence(LinkedList<CFGElement> sententialForm) {
    for (var e : sententialForm)
      if (e.cfgKind == CFGKind.NONTERMINAL) return false;
    return true;
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    outputStream.print("(* " + (isEmpty() ? "Empty " : "") + cfgRulesKind + " Context Free Grammar rules *)\n");

    if (!characters.isEmpty()) outputStream.println("!characters " + characters);

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

    outputStream.println("!start " + startNonterminal);
    for (CFGElement n : elementToRulesNodeMap.keySet()) {
      boolean first = true;
      for (CFGNode production = elementToRulesNodeMap.get(n).alt; production != null; production = production.alt) {
        if (first) {
          outputStream.print(production.toStringAsProduction(" ::=\n ", null) + "\n");
          first = false;
        } else
          outputStream.print("|" + production.toStringAsRHS(null) + "\n");
      }
    }

    if (full) {
      outputStream.print("Elements:\n");
      for (CFGElement s : elements.keySet()) {
        outputStream.print(" (" + s.toStringDetailed() + ") " + s);
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
          if (initialSlots.contains(gn)) outputStream.print(" initial");
          if (secondSlots.contains(gn)) outputStream.print(" second");
          if (penultimateSlots.contains(gn)) outputStream.print(" penultimate");
          if (finalSlots.contains(gn)) outputStream.print(" final");
          if (nullableSuffixSlots.contains(gn)) outputStream.print(" nullableSuffix");
          if (nullablePrefixSlots.contains(gn)) outputStream.print(" nullablePrefix");

          if (!instanceFirst.get(gn).isEmpty()) {
            outputStream.print(" instfirst = {");
            printElements(outputStream, instanceFirst.get(gn));
            outputStream.print("} instfollow = {");
            printElements(outputStream, instanceFollow.get(gn));
            outputStream.print("}");
          }
        }
        outputStream.print(" Action: " + gn.toStringActions());
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

      outputStream.print("Reachable all elements:\n" + reachable);
      outputStream.print("Reachable nonterminals:\n" + reachableNonterminals);
      outputStream.print("Reachable all elements stop at paraterminals:\n" + reachablePara);
      outputStream.print("Reachable nonterminal stop at paraterminals:\n" + reachableNonterminalsPara);

      printSet(outputStream, cyclicNonterminals, "Cyclic nonterminals");

      printSet(outputStream, defined, "Defined nonterminals");
      printSet(outputStream, used, "Used elements");

      outputStream.print("derivesExactly(R):");
      outputStream.println();
      if (derivesExactly != null) for (var n : derivesExactly.getDomain())
        if (cyclicNonterminals.contains(n)) {
          outputStream.print(n + ": {");
          for (var nf : derivesExactly.get(n))
            outputStream.print(" " + nf);
          outputStream.println(" }");
        }

      outputStream.println("derivesExactlyTrasitiveClosure (R+):");
      if (derivesExactlyTransitiveClosure != null) for (var n : derivesExactlyTransitiveClosure.getDomain())
        if (cyclicNonterminals.contains(n)) {
          outputStream.print(n + ": {");
          for (var nf : derivesExactlyTransitiveClosure.get(n))
            outputStream.print(" " + nf);
          outputStream.println(" }");
        }

      outputStream.print("cocyclic nonterminals:\n");
      if (derivesExactlyTransitiveClosure != null) for (var n : derivesExactlyTransitiveClosure.getDomain())
        if (cyclicNonterminals.contains(n)) {
          outputStream.print(n + ": {");
          for (var nf : derivesExactlyTransitiveClosure.get(n))
            if (derivesExactlyTransitiveClosure.get(nf).contains(n)) outputStream.print(" " + nf);
          outputStream.println(" }");
        }

      outputStream.print("cyclic slots:\n");
      if (cyclicSlots != null) for (var n : cyclicSlots) {
        outputStream.print(n.toStringAsProduction());
        outputStream.println();
      }
    }
  }

  private void printSet(PrintStream outputStream, Set<CFGElement> elements, String title) {
    outputStream.print(title + ": {");
    printElements(outputStream, elements);
    outputStream.println("}");
  }

  private void printElements(PrintStream outputStream, Set<CFGElement> elements) {
    if (elements == null) return;
    boolean first = true;
    for (CFGElement e : elements) {
      if (first)
        first = false;
      else
        outputStream.print(", ");
      outputStream.print(e.toString());
    }
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    outputStream.print("digraph \"Reference grammar\"\n" + "{\n" + "graph[ordering=out ranksep=0.1]\n"
        + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04 color=gray]\n"
        + "edge[fontname=Helvetica fontsize=9 arrowsize = 0.3 color=gray]\n\n");
    for (CFGElement n : elementToRulesNodeMap.keySet())
      toStringDotRec(outputStream, elementToRulesNodeMap.get(n));
    outputStream.print("}\n");
  }

  private void toStringDotRec(PrintStream outputStream, CFGNode cs) {
    outputStream.print("\"" + cs.num + "\"[label=\"" + cs.toStringDot() + "\"]\n");
    if (cs.cfgElement.cfgKind == CFGKind.ALT) {
      toStringDotSeqArrow(outputStream, cs);
      toStringDotAltArrow(outputStream, cs);
    } else if (cs.cfgElement.cfgKind != CFGKind.END) {
      toStringDotAltArrow(outputStream, cs);
      toStringDotSeqArrow(outputStream, cs);
    }
  }

  private void toStringDotAltArrow(PrintStream outputStream, CFGNode cs) {
    if (cs.alt == null) return;
    outputStream.print(
        "\"" + cs.num + "\"->\"" + cs.alt.num + "\"" + "{rank = same; \"" + cs.num + "\"" + ";\"" + cs.alt.num + "\"" + ";" + "}" + "[label=\" a\"" + "]\n");
    if (!isLHS(cs.alt)) toStringDotRec(outputStream, cs.alt);
  }

  private void toStringDotSeqArrow(PrintStream outputStream, CFGNode cs) {
    if (cs.seq == null) return;
    outputStream.print("\"" + cs.num + "\"->\"" + cs.seq.num + "\"\n");
    toStringDotRec(outputStream, cs.seq);
  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
    // TODO Auto-generated method stub
  }

}
