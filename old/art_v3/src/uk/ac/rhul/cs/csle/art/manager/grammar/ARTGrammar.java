package uk.ac.rhul.cs.csle.art.manager.grammar;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.manager.ARTOptionBlock;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementAttribute;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementEoS;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementModuleNonterminal;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalBuiltin;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalCaseInsensitive;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalCaseSensitive;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalCharacter;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalFromNonterminal;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceAction;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceActionValue;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceAlt;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceAnnotation;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceDiff;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceDoFirst;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceIter;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceKleeneClosure;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceLHS;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceNot;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceOptional;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstancePositiveClosure;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceRoot;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceTear;
import uk.ac.rhul.cs.csle.art.manager.grammar.instance.ARTGrammarInstanceTerminal;
import uk.ac.rhul.cs.csle.art.manager.mode.ARTModeGrammarKind;
import uk.ac.rhul.cs.csle.art.manager.module.ARTModule;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_ID;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_action;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_builtinTerminal;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_caseInsensitiveTerminal;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_caseSensitiveTerminal;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_characterTerminal;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_nonterminal;
import uk.ac.rhul.cs.csle.art.util.ARTBitSet;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTree;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.value.ARTValueString;
import uk.ac.rhul.cs.csle.art.value.ARTValueTerm;

public class ARTGrammar {
  private final ARTManager artManager;
  private final String id;
  private final ARTOptionBlock optionBlock;
  private boolean isEBNF;
  private boolean isFBNF;
  private final ARTGrammarElementNonterminal unaugmentedStartNonterminal;
  private ARTGrammarElementNonterminal defaultStartNonterminal;
  private final Set<ARTGrammarElementNonterminal> nonterminals = new TreeSet<>();
  private final Set<ARTGrammarElementNonterminal> nonterminalsDeclaredAsTerminals = new HashSet<>();
  private final Map<ARTName, ARTGrammarElementNonterminal> nonterminalNameMap = new HashMap<>();
  private final Set<ARTGrammarElementTerminal> terminals = new TreeSet<ARTGrammarElementTerminal>();
  private final Set<ARTGrammarElementTerminal> whitespaces = new TreeSet<ARTGrammarElementTerminal>();
  private final Map<String, ARTGrammarElementTerminalCharacter> terminalCharacterNameMap = new HashMap<>();
  private final Map<String, ARTGrammarElementTerminalCaseSensitive> terminalCaseSensitiveNameMap = new HashMap<>();
  private final Map<String, ARTGrammarElementTerminalCaseInsensitive> terminalCaseInsensitiveNameMap = new HashMap<>();
  private final Map<String, ARTGrammarElementTerminalBuiltin> terminalBuiltinNameMap = new HashMap<>();
  private final Map<Integer, ARTGrammarElement> elementNumberMap = new HashMap<>();
  private final Map<String, ARTGrammarElementTerminalFromNonterminal> terminalFromNonterminalNameMap = new HashMap<>();
  private final ARTBitSet[] longer;
  private final ARTBitSet[] higher;

  private final boolean isEOSFollow = false;
  private boolean isDirty = true;
  private int firstTerminalElementNumber;
  private int lastTerminalElementNumber;
  private int firstNonterminalElementNumber;
  private int lastNonterminalElementNumber;
  // Todo: at the moment slots are not enumerated - we'll just set it to lastNonterminalElementNumber
  private int lastSlotNumber;

  private final Set<String> preludeStrings = new HashSet<String>();
  private final Set<String> supportStrings = new HashSet<String>();

  private final ARTGrammarElementEoS eoS = new ARTGrammarElementEoS();
  private final ARTGrammarElementEpsilon epsilon = new ARTGrammarElementEpsilon();

  private ARTTree instanceTree;

  private Set<ARTGrammarElementAttribute> supportAttributes = new HashSet<ARTGrammarElementAttribute>();

  private ARTGrammarInstance pendingnL;

  private int nextFreeSetNumber;
  private int nextInstanceNumber;
  private int nextTerminalInstanceNumber;

  Map<LinkedList<ARTGrammarElement>, ARTGrammarInstanceSlot> prefixStringMap = new HashMap<>();
  private final Map<ARTGrammarInstanceSlot, ARTGrammarInstanceSlot> prefixSlotMap = new HashMap<>();

  private final Set<Set<ARTGrammarElement>> usedSets = new HashSet<>();
  private final HashMap<Set<ARTGrammarElement>, Integer> mergedSets = new HashMap<>();
  private int instanceCount = 1;

  public ARTGrammar(ARTManager artManager, String id, ARTModule artModule, boolean augment, ARTOptionBlock artOptionBlock)
      throws ARTException, FileNotFoundException {
    this(artManager, id, artModule, artModule.getDefaultStart(), augment, null, artOptionBlock);
  }

  public ARTGrammar(ARTManager artManager, String id, ARTModule artModule, ARTOptionBlock artOptionBlock) throws ARTException, FileNotFoundException {
    this(artManager, id, artModule, artModule.getDefaultStart(), false, null, artOptionBlock);
  }

  public ARTGrammar(ARTManager artManager, String id, ARTModule artModule, ARTGrammarElementModuleNonterminal start, boolean augment,
      Map<ARTGrammarElement, Set<ARTGrammarElement>> relation, ARTOptionBlock artOptionBlock) throws ARTException, FileNotFoundException {
    this.id = id;
    this.artManager = artManager;
    this.optionBlock = artOptionBlock;

    ARTGrammarElementNonterminal nonterminal;
    ARTGrammarElementModuleNonterminal moduleStart = artManager.getDefaultMainModule().getDefaultStart();
    if (moduleStart == null)
      throw new ARTException("Module " + artManager.getDefaultMainModule().getId() + " has no start symbol so cannot construct a grammar");
    preludeStrings.addAll(artModule.getPreludeStrings());
    supportStrings.addAll(artModule.getSupportStrings());

    // Iterate over module nonterminals, loading them into the grammar tables so as to preserve specification ordering
    for (ARTGrammarElementModuleNonterminal n : artModule.getNonterminalList()) {// Use nonterminalsList to retain specification ordering
      nonterminal = findNonterminal(n.getModule(), n.getId());
      nonterminal.getAttributes().addAll(n.getAttributes());
    }

    // Set up default start symol
    defaultStartNonterminal = findNonterminal(moduleStart);

    // Optionally augment the grammar
    unaugmentedStartNonterminal = defaultStartNonterminal;
    if (augment) {
      nonterminal = findNonterminal(artModule, "ART_AUGMENTED");
      ARTGrammarInstanceLHS lhs = new ARTGrammarInstanceLHS(instanceCount++, nonterminal);
      ARTGrammarInstanceCat cat = new ARTGrammarInstanceCat(instanceCount++);
      ARTGrammarInstanceSlot first = (ARTGrammarInstanceSlot) cat.addChild(new ARTGrammarInstanceSlot(instanceCount++));
      ARTGrammarInstanceNonterminal second = (ARTGrammarInstanceNonterminal) cat
          .addChild(new ARTGrammarInstanceNonterminal(instanceCount++, defaultStartNonterminal));
      ARTGrammarInstanceSlot third = (ARTGrammarInstanceSlot) cat.addChild(new ARTGrammarInstanceSlot(instanceCount++));

      lhs.lhsL = cat.lhsL = first.lhsL = second.lhsL = third.lhsL = lhs;
      cat.productionL = first.productionL = second.productionL = third.productionL = cat;

      first.prefixLength = 0;
      second.prefixLength = third.prefixLength = 1;

      nonterminal.getProductions().add(cat);

      defaultStartNonterminal = nonterminal;
    }

    // Load module productions into this grammar
    for (ARTGrammarElementModuleNonterminal n : artModule.getNonterminalList()) {
      // System.out.printf("ARTGrammar constructor processing module %s found nonterminal %s%n", artModule.getId(), moduleNonterminal);
      nonterminal = findNonterminal(n);

      for (ARTGLLRDTVertex vertex : n.getProductions()) {
        // System.out.printf("**** **** Adding production for nonterminal %s\n", nonterminal);
        addProduction(nonterminal, artModule, vertex);
      }
    }

    if (nonterminals.isEmpty()) throw new ARTException("Induced grammar contains no nonterminals");

    // Create new terminals from nonterminals that are declared as terminals
    for (ARTValueTerm t : artModule.getDeclaredTerminals()) {
      getNonterminalsDeclaredAsTerminals().add(findNonterminal(artModule, t.getChild().getPayload().toString()));
      findTerminalFromNonterminal(terminalFromNonterminalName(artModule.getId(), t.getChild().getPayload().toString()));
    }
    // Now set up whitespace terminals
    // TODO: grammar generation - check whitespace terminals are not reachable from the start symbol

    // If no whitespace declaration at all, then assert the default behaviour into this grammar
    if (!artModule.seenWhitespaceDeclaration && !terminals.contains(new ARTGrammarElementTerminalBuiltin("WHITESPACE")))
      getWhitespaces().add(new ARTGrammarElementTerminalBuiltin("WHITESPACE"));

    for (ARTValueTerm w : artModule.getWhitespaceTerminals()) {
      if (w.getPayload().toString().equals("builtinTerminal"))
        getWhitespaces().add(new ARTGrammarElementTerminalBuiltin(w.getChild().getPayload().toString()));
      else if (w.getPayload().toString().equals("nonterminal"))
        getWhitespaces()
            .add(new ARTGrammarElementTerminalFromNonterminal(terminalFromNonterminalName(artModule.getId(), w.getChild().getPayload().toString())));
      else
        throw new ARTException("Unexpected term in whitespace list: " + w);
    }

    clean();

    // Build choice maps
    longer = new ARTBitSet[getLastSlotNumber()];
    higher = new ARTBitSet[getLastSlotNumber()];
    for (ARTValueTerm t : artModule.getLonger().keySet()) {
      // System.out.println("Processing " + t + " >> " + artModule.getLonger().get(t));
      extendChoiceRelation(longer, artModule, t, artModule.getLonger().get(t));
    }

    Map<ARTValueTerm, Set<ARTValueTerm>> x = artModule.getHigher();
    for (ARTValueTerm t : artModule.getHigher().keySet()) {
      // System.out.println("Processing " + t + " > " + artModule.getHigher().get(t));
      extendChoiceRelation(higher, artModule, t, artModule.getHigher().get(t));
    }
    // System.out.println(this);
  }

  public boolean addProduction(ARTGrammarElementNonterminal nonterminal, ARTModule artModule, ARTGLLRDTVertex vertex) throws ARTException {
    boolean ret = nonterminal.getProductions()
        .add((ARTGrammarInstanceCat) buildGrammarProductionFromRDT(nonterminal, artModule, new ARTGrammarInstanceRoot(0), vertex, false));
    if (ret) isDirty = true;
    return ret;
  }

  private void artComputeFfCELocalRec(ARTGrammarInstance instance) throws ARTException {
    if (instance instanceof ARTGrammarInstanceOptional) {
      instance.getSibling().isFfCE = true;
      artComputeFfCELocalRec(instance.getChild());
    } else if (instance instanceof ARTGrammarInstanceDoFirst)
      artComputeFfCELocalRec(instance.getChild());

    else if (instance instanceof ARTGrammarInstanceAlt)
      for (ARTGrammarInstance i = instance.getChild(); i != null; i = i.getSibling())
        artComputeFfCELocalRec(i);

    else if (instance instanceof ARTGrammarInstanceCat)
      artComputeFfCELocalRec(instance.getChild().getSibling());

    else if (instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceKleeneClosure
        || instance instanceof ARTGrammarInstanceTerminal || instance instanceof ARTGrammarInstanceNonterminal || instance instanceof ARTGrammarInstanceEpsilon)
      instance.getSibling().isFfCE = true;
    else
      throw new ARTException("artComputerFfCELocalRec: U=unexpected node " + instance);
  }

  private void artComputeFfCERec(ARTGrammarInstance instance) throws ARTException {
    // artManager.getText().print("artComputeFfCERec() visiting node instance of " + instance.getClass() + " labelled " + instance + "\n");

    if (instance instanceof ARTGrammarInstancePositiveClosure) {
      // artManager.getText().print("Detected positive closure\n");
      artComputeFfCELocalRec(instance.getChild());
    }

    for (ARTGrammarInstance i = instance.getChild(); i != null; i = i.getSibling())
      artComputeFfCERec(i);
  }

  private void artSetInstanceNumberRec(ARTGrammarElementNonterminal nonterminal, ARTGrammarInstance instance) {
    if (instance.getPayload() == nonterminal) instance.instanceNumberWithinProduction = nextInstanceNumber++;
    if (instance instanceof ARTGrammarInstanceTerminal && instance.instanceNumberWithinProduction == 0)
      instance.instanceNumberWithinProduction = nextTerminalInstanceNumber++;
    for (ARTGrammarInstance child = instance.getChild(); child != null; child = child.getSibling())
      artSetInstanceNumberRec(nonterminal, child);
  }

  // the belowClosure flag is set by instances of * + and ? and is used to control
  private ARTGrammarInstance buildGrammarProductionFromRDT(ARTGrammarElementNonterminal lhs, ARTModule artModule, ARTGrammarInstance parent,
      ARTGLLRDTVertex vertex, boolean belowClosure) throws ARTException {
    // System.out.printf("buildGrammarProduction() at %s with parent %s and belowClosure %s\n", vertex, parent, belowClosure);
    ARTGrammarInstance newParent = parent;
    boolean newBelowClosure = false;
    boolean recurse = true;

    isDirty = true;
    if (vertexLabel(vertex, "(")) {
      if (belowClosure) {
        newBelowClosure = true; // propogate to solitary elements that may need slots adding
        newParent = parent; // Do not instantiate ( below *, + or ?
      } else
        newParent = parent.addChild(new ARTGrammarInstanceDoFirst(instanceCount++));

      /*
       * Hazard - the concrete ART syntax allows user to write, say, a* instead of (a)*. We use processAbbreviation to instantiate the right tree in those cases
       */
    } else if (vertexLabel(vertex, "?")) {
      newBelowClosure = true;
      newParent = parent.addChild(new ARTGrammarInstanceOptional(instanceCount++));
      if (processAbbreviation(artModule, newParent, vertex)) return newParent; // Abbreviations only allowed on BNF instances, so all done
    } else if (vertexLabel(vertex, "*")) {
      newBelowClosure = true;
      newParent = parent.addChild(new ARTGrammarInstanceKleeneClosure(instanceCount++));
      if (processAbbreviation(artModule, newParent, vertex)) return newParent; // Abbreviations only allowed on BNF instances, so all done
    } else if (vertexLabel(vertex, "+")) {
      newBelowClosure = true;
      newParent = parent.addChild(new ARTGrammarInstancePositiveClosure(instanceCount++));
      if (processAbbreviation(artModule, newParent, vertex)) return newParent; // Abbreviations only allowed on BNF instances, so all done
    } else if (vertexLabel(vertex, "ARTV3.not"))
      newParent = parent.addChild(new ARTGrammarInstanceNot(instanceCount++));
    else if (vertexLabel(vertex, "ARTV3.iter"))
      newParent = parent.addChild(new ARTGrammarInstanceIter(instanceCount++));
    else if (vertexLabel(vertex, "ARTV3.diff"))
      newParent = parent.addChild(new ARTGrammarInstanceDiff(instanceCount++));
    else if (vertexLabel(vertex, "ARTV3.alt"))
      newParent = parent.addChild(new ARTGrammarInstanceAlt(instanceCount++));
    else if (vertexLabel(vertex, "ARTV3.cat"))
      newParent = parent.addChild(new ARTGrammarInstanceCat(instanceCount++));
    else if (vertexLabel(vertex, "ARTV3.slot")) {
      newParent = parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
    } else if (vertexLabel(vertex, "ARTV3.action")) {
      recurse = false;
      newParent = parent.addChild(new ARTGrammarInstanceAction(instanceCount++));
      newParent = newParent.addChild(new ARTGrammarInstanceActionValue(instanceCount++, ((ARTAT_ARTV3_action) vertex.getPayload().getAttributes()).v));
    } else { // non recursing labels
      recurse = false;
      if (belowClosure) {
        parent = parent.addChild(new ARTGrammarInstanceCat(instanceCount++));
        parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
      } else { // annotatable nodes
        if (vertexLabel(vertex, "ARTV3.nonterminal"))
          newParent = parent.addChild(new ARTGrammarInstanceNonterminal(instanceCount++,
              findNonterminal(artModule, ((ARTAT_ARTV3_nonterminal) vertex.getPayload().getAttributes()).v)));
        else if (vertexLabel(vertex, "ARTV3.characterTerminal"))
          newParent = parent.addChild(
              new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCharacter(((ARTAT_ARTV3_characterTerminal) vertex.getPayload().getAttributes()).v)));
        else if (vertexLabel(vertex, "ARTV3.caseSensitiveTerminal")) {
          String pattern = ((ARTAT_ARTV3_caseSensitiveTerminal) vertex.getPayload().getAttributes()).v;
          if (pattern.replaceFirst("\\s", "").length() != pattern.length())
            throw new ARTException("In production for nonterminal " + lhs.getId() + ", case sensitive terminal has embedded whitespace: '" + pattern + "'");
          newParent = parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCaseSensitive(pattern)));
        } else if (vertexLabel(vertex, "ARTV3.caseInsensitiveTerminal")) {
          String pattern = ((ARTAT_ARTV3_caseInsensitiveTerminal) vertex.getPayload().getAttributes()).v;
          if (pattern.replaceAll("\\s", "").length() != pattern.length())
            throw new ARTException("In production for nonterminal " + lhs.getId() + ", case insensitive terminal has embedded whitespace: \"" + pattern + "\"");
          newParent = parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCaseInsensitive(pattern)));
        } else if (vertexLabel(vertex, "ARTV3.builtinTerminal"))
          newParent = parent.addChild(
              new ARTGrammarInstanceTerminal(instanceCount++, findTerminalBuiltin(((ARTAT_ARTV3_builtinTerminal) vertex.getPayload().getAttributes()).v)));
        else if (vertexLabel(vertex, "ARTV3.epsilon"))
          newParent = parent.addChild(new ARTGrammarInstanceEpsilon(instanceCount++, epsilon));
        else
          throw new ARTException("Unknown node label " + artManager.getParser().artLabelStrings[vertex.getPayload().getLabel()]
              + " encountered in RDT during grammar tree construction");

        processAnnotations(lhs, newParent, vertex);
      }
      if (belowClosure) parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));

    }

    if (recurse) for (ARTGLLRDTVertex child = vertex.getChild(); child != null; child = child.getSibling())
      buildGrammarProductionFromRDT(lhs, artModule, newParent, child, newBelowClosure);

    return newParent;
  }

  /* buildInstanceTree() stitches together the production trees reachable from the start symbol with new LHS nodes and a new root node */
  private void buildInstanceTree(ARTGrammarElementNonterminal start) throws ARTException, FileNotFoundException {
    boolean changed = true;
    // 0 Instantiate left hand side nodes and connect to production nodes
    int lhsInstanceCount = 0;
    instanceTree = new ARTTree("Grammar" + id);
    ARTGrammarInstanceRoot root = new ARTGrammarInstanceRoot(--lhsInstanceCount);
    instanceTree.setRoot(root);
    for (ARTGrammarElementNonterminal nonterminal : nonterminals) {
      nonterminal.lhsInstance = new ARTGrammarInstanceLHS(--lhsInstanceCount, nonterminal);
      nonterminal.lhsInstance.isLHS = true;
      nonterminal.lhsInstance.setPayload(nonterminal);
      root.addChild(nonterminal.lhsInstance);
      for (ARTGrammarInstance productionRoot : nonterminal.getProductions())
        nonterminal.lhsInstance.addChild(productionRoot);
    }

    // Set the instance numbers
    for (ARTGrammarElementNonterminal nonterminal : nonterminals)
      for (ARTGrammarInstance production : nonterminal.getProductions())
        for (ARTGrammarElementNonterminal innerNonterminal : nonterminals) { // This is very inefficient
          nextInstanceNumber = nextTerminalInstanceNumber = 1;
          artSetInstanceNumberRec(innerNonterminal, production);
        }

    if (getOptionBlock().verbosityLevel > 0) instanceTree.printDot("GrammarInstanceTreeBeforeSetComputations.dot");

    // 1 Adding EoS to nonterminal follow sets
    if (isEOSFollow) {
      for (ARTGrammarElementNonterminal n : nonterminals)
        n.getFollow().add(eoS);
    } else
      start.getFollow().add(eoS);

    // 2 first and follow set computations
    while (changed) {
      changed = false;
      changed |= (computeSetsRec((ARTGrammarInstance) instanceTree.getRoot(), 0, null, null));
    }

    // 3 Main attribute computation
    changed = true;
    // int pass = 0;
    while (changed) {
      // pass++;
      // artManager.text.print("Main attribute computation pass\n");
      changed = false;
      pendingnL = null;
      changed |= (computeAttributesRec((ARTGrammarInstance) instanceTree.getRoot(), null, null, null, 0));
      changed |= (computePlAttributeRec((ARTGrammarInstance) instanceTree.getRoot()));
    }

    // 4 fiR and predictive pops
    // artManager.text.print("Computing fiR and predictive pops\n");
    computefiRRec((ARTGrammarInstance) instanceTree.getRoot());
    artComputeFfCERec((ARTGrammarInstance) instanceTree.getRoot());
    computePredictivePopRec((ARTGrammarInstance) instanceTree.getRoot());

    // 5 isFBNF and isEBNF
    isFBNF = true;
    isEBNF = false;
    // artManager.text.print("Computing isFBNF and is EBNF\n");
    for (ARTGrammarElementNonterminal nonterminal : nonterminals)
      for (ARTGrammarInstance production : nonterminal.getProductions()) {
        /*
         * computeIsEFBNFRec treats instances of epsilon as inidcating EBNF because they are assummed to be embedded.
         *
         * We therefore DON'T call it on simple BNF epsilon productions
         *
         */
        // System.out.println("compute EBNF at production " + production.toGrammarString());
        // System.out.println("First non-slot is " + production.getChild().getSibling());
        // System.out.println("Second non-slot is " + production.getChild().getSibling().getSibling().getSibling());
        if (production.getChild().getSibling() instanceof ARTGrammarInstanceEpsilon) {
          if (production.getChild().getSibling().getSibling().getSibling() != null) computeIsEFBNFRec(production.getChild());
          // System.out.println("Calling computeIsEFBNFRec");
        } else
          computeIsEFBNFRec(production.getChild());

      }
    // System.out.println("isEBNF: " + isEBNF + " isFBNF: " + isFBNF);

    // 6 Compute merged sets
    nextFreeSetNumber = 1;

    computeMergedSets(root);

    // System.out.println(mergedSets);

    // 7 Compute prefix lengths and map for BSRset algorithms
    computeSlotPrefixMap();

    if (getOptionBlock().verbosityLevel > 0) instanceTree.printDot("GrammarInstanceTreeAfterSetComputations.dot");

    isDirty = false;
  }

  public void clean() throws ARTException, FileNotFoundException {
    boolean error = false;
    if (isDirty) {
      for (ARTGrammarElementNonterminal n : nonterminals)
        if (n.getProductions().isEmpty()) {
          error = true;
          throw new ARTException("Nonterminal " + n.getId() + " has no productions");
        }

      if (error) throw new ARTException("Grammar has nondefined nonterminals");
      numberSymbols();
      buildInstanceTree(defaultStartNonterminal);
    }
  }

  private boolean computeAttributesRec(ARTGrammarInstance instance, ARTGrammarInstance parentInstance, ARTGrammarInstanceLHS LHSInstance,
      ARTGrammarInstanceCat productionInstance, int level) {
    if (instance == null) return false;
    // System.out.printf("artComputeAttributes entering node %d at level %d with LHSInstance %d and productionInstance %d\n", instance.getKey(), level,
    // LHSInstance == null ? 0 : LHSInstance.getKey(), productionInstance == null ? 0 : productionInstance.getKey());

    boolean changed = false;

    if (instance instanceof ARTGrammarInstanceLHS) LHSInstance = (ARTGrammarInstanceLHS) instance;
    if (level == 2) productionInstance = (ARTGrammarInstanceCat) instance;

    if (!(instance instanceof ARTGrammarInstanceSlot)) // Do not recurse below POS nodes
      for (ARTGrammarInstance tmp = instance.getChild(); tmp != null; tmp = tmp.getSibling())
        changed |= (computeAttributesRec(tmp, instance, LHSInstance, productionInstance, level + 1));
    else
      instance.isSlotParentLabel = instance.getChild() != null;

    // System.out.printf("artComputeAttributes after recursion from node %d at level %d with LHSInstance %d and productionInstance %d\n", instance.getKey(),
    // level,
    // LHSInstance == null ? 0 : LHSInstance.getKey(), productionInstance == null ? 0 : productionInstance.getKey());

    // Initialisations
    if (instance.getLhsL() == null && LHSInstance != null) {
      changed |= (instance.getLhsL() != LHSInstance);
      instance.lhsL = LHSInstance;
    }

    changed |= (instance.getProductionL() != productionInstance);
    instance.productionL = productionInstance;

    if (instance.niL == null) {
      changed |= (instance.niL != instance);
      instance.niL = instance;
    }
    if (instance.nL == null) {
      changed |= (instance.nL != instance);
      instance.nL = instance;
    } // Some elision here - the paper doesn't define nL for the last node in a
    // nonterminal's productions: let's make it L
    if (instance.aL == null) {
      changed |= (instance.aL != instance);
      instance.aL = instance;
    }

    if (instance.pL == null) {
      changed |= (instance.pL != instance);
      // DEBUG - uncommenting this line breaks termination - why?
      // instance.pL = instance;
    }
    if (instance.lrL == null) {
      changed |= (instance.lrL != instance);
      instance.lrL = instance;
    }
    if (instance.erL == null) {
      changed |= (instance.erL != instance);
      instance.erL = instance;
    }

    // Boolean attribute calculations
    // EoR
    changed |= (instance.isEoR != (instance instanceof ARTGrammarInstanceSlot && level == 3 && instance.getSibling() == null));
    instance.isEoR = (instance instanceof ARTGrammarInstanceSlot && level == 3 && instance.getSibling() == null);

    // EoOP
    if (instance instanceof ARTGrammarInstanceOptional || instance instanceof ARTGrammarInstanceDoFirst) {
      changed |= (rightmostElementRec(instance).isEoOP != true);
      rightmostElementRec(instance).isEoOP = true;
    }

    if (instance.aL.isEoOP) {
      changed |= (instance.isEoOP != true);
      instance.isEoOP = true;
    }

    // EoD
    if (instance instanceof ARTGrammarInstanceDoFirst) {
      changed |= (rightmostElementRec(instance).isEoD != true);
      rightmostElementRec(instance).isEoD = true;
    }

    // EoO
    if (instance instanceof ARTGrammarInstanceOptional) {
      changed |= (rightmostElementRec(instance).isEoO != true);
      rightmostElementRec(instance).isEoO = true;
    }

    // EoP
    if (instance instanceof ARTGrammarInstancePositiveClosure) {
      changed |= (rightmostElementRec(instance).isEoP != true);
      rightmostElementRec(instance).isEoP = true;
    }

    // EoK
    if (instance instanceof ARTGrammarInstanceKleeneClosure) {
      changed |= (rightmostElementRec(instance).isEoK != true);
      rightmostElementRec(instance).isEoK = true;
    }

    // fiR now processed in final pass - see below

    // niL
    if (instance instanceof ARTGrammarInstanceNonterminal) {
      changed |= (instance.getSibling().niL != instance);
      instance.getSibling().niL = instance;
    }

    // nL
    if (instance instanceof ARTGrammarInstanceSlot && pendingnL != null) {
      if (pendingnL.isEoR) {
        changed |= (pendingnL.nL != pendingnL);
        pendingnL.nL = pendingnL;
      } else {
        changed |= (pendingnL.nL != instance);
        pendingnL.nL = instance;
      }
      pendingnL = null;
    }

    if (instance instanceof ARTGrammarInstanceSlot) pendingnL = instance;

    // aL
    if (instance instanceof ARTGrammarInstanceAlt) {
      ARTGrammarInstance E_r_n = rightmostElementRec(instance);
      for (ARTGrammarInstance tmp = instance.getChild(); tmp != null; tmp = tmp.getSibling()) {
        ARTGrammarInstance endNode = rightmostElementRec(tmp);

        changed |= (endNode.aL != E_r_n);
        endNode.aL = E_r_n;
        // if (endNode != E_r_n) // In this update we have extended isEoA to be true for the end of all alternate including the one without a | following
        changed |= (endNode.isEoA != true);
        endNode.isEoA = true;
      }
    }

    // isEof

    // isPopD
    if (instance instanceof ARTGrammarInstanceSlot && instance.getSibling() == null) {
      changed |= (instance.isPopD != true);
      instance.isPopD = true;
    }

    if (instance.getSibling() != null && instance.getSibling().isPopD
        && (instance instanceof ARTGrammarInstanceSlot || instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceTerminal)) {
      changed |= (instance.isPopD != true);
      instance.isPopD = true;
    }

    // isPosSelector;
    if (instance instanceof ARTGrammarInstanceSlot) if (parentInstance.getChild() != instance && instance.getSibling() != null) {
      changed |= (instance.isSlotSelector != true);
      instance.isSlotSelector = true;
    }

    // lrL
    if (instance instanceof ARTGrammarInstanceLHS || (instance instanceof ARTGrammarInstanceAlt && level == 1))
      for (ARTGrammarInstance childNode = instance.getChild(); childNode != null; childNode = childNode.getSibling()) {
        changed |= (childNode.lrL != leftmostElementRec(childNode));
        childNode.lrL = leftmostElementRec(childNode);
      }
    else if (instance instanceof ARTGrammarInstanceSlot) if (instance.getSibling() != null) {
      changed |= (instance.getSibling().lrL != instance);
      instance.getSibling().lrL = instance;
    } else if (instance instanceof ARTGrammarInstanceCat || instance instanceof ARTGrammarInstanceAlt) {
      changed |= (instance.lrL != leftmostElementRec(instance));
      instance.lrL = leftmostElementRec(instance);
    }

    // erL
    if (instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceNonterminal || instance instanceof ARTGrammarInstanceTerminal
        || instance instanceof ARTGrammarInstanceDoFirst || instance instanceof ARTGrammarInstanceOptional
        || instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceKleeneClosure) {
      changed |= (instance.erL != instance.getSibling());
      instance.erL = instance.getSibling();
    }
    if (instance instanceof ARTGrammarInstanceCat || instance instanceof ARTGrammarInstanceAlt) {
      changed |= (instance.erL != rightmostElementRec(instance));
      instance.erL = rightmostElementRec(instance);
    }

    // System.out.printf("Leaving node %d with changed %s\n", instance.getKey(), changed ? "true" : "false");

    return changed;
  }

  private void computefiRRec(ARTGrammarInstance node) throws ARTException {
    // System.out.printf("artComputefiRRec() visiting node %d %s\n", node.getKey(), node.getClass());

    if (node instanceof ARTGrammarInstanceRoot)
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computefiRRec(tmp);
    else if (node instanceof ARTGrammarInstanceLHS)
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computefiRRec(tmp);
    else if (node instanceof ARTGrammarInstanceDoFirst)
      computefiRRec(node.getChild());
    else if (node instanceof ARTGrammarInstancePositiveClosure || node instanceof ARTGrammarInstanceKleeneClosure || node instanceof ARTGrammarInstanceOptional)
      ;
    else if (node instanceof ARTGrammarInstanceAlt)
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computefiRRec(tmp);

    else if (node instanceof ARTGrammarInstanceCat && node.getChild().getSibling().getSibling().getSibling() == null) // test for unary
    {
      ARTGrammarInstance child2 = node.getChild().getSibling();
      if (!(child2 instanceof ARTGrammarInstanceNonterminal || child2 instanceof ARTGrammarInstanceTerminal)) computefiRRec(child2);
    }

    else if (node instanceof ARTGrammarInstanceCat)
      computefiRRec(node.getChild().getSibling());

    else if (node instanceof ARTGrammarInstanceNonterminal) {
      // Mods to account for Liz's refinement of fiR
      boolean twins = false;

      if (node.getSibling().getSibling() != null) {
        ARTGrammarInstance successorGrammarNode = node.getSibling().getSibling();
        if (successorGrammarNode instanceof ARTGrammarInstanceNonterminal && successorGrammarNode.getPayload() == node.getPayload()) twins = true;
      }
      // End of refinement

      if (!(twins && node.first.contains(epsilon))) node.getSibling().isFiR = true;
    }

    else if (node instanceof ARTGrammarInstanceTerminal)
      node.getSibling().isFiR = true;

    else if (node instanceof ARTGrammarInstanceEpsilon || node instanceof ARTGrammarInstanceSlot || node instanceof ARTGrammarInstanceTear
        || node instanceof ARTGrammarInstanceAnnotation)
      ;

    else
      throw new ARTException("Unexpected node " + node.getKey() + "encountered during artComputefiRRec()\n");

  }

  private void computeIsEFBNFRec(ARTGrammarInstance instance) {
    // System.out.println("computeIsEFBNFRec at " + instance);
    if (instance == null) return;

    if (instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceKleeneClosure
        || instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceOptional || instance instanceof ARTGrammarInstanceIter
        || (instance instanceof ARTGrammarInstanceDoFirst) && !(instance.isPredictivePop || instance.isPostPredictivePop)) {
      // System.out.println("Resetting isFBNF");
      isFBNF = false;
    }

    if (instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceKleeneClosure
        || instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceOptional
        || instance instanceof ARTGrammarInstanceDoFirst || instance instanceof ARTGrammarInstanceIter) {
      // System.out.println("Setting isEBNF");
      isEBNF = true;
    }

    computeIsEFBNFRec(instance.getChild());
    computeIsEFBNFRec(instance.getSibling());
  }

  private void computeMergedSets(ARTGrammarInstance instance) {
    mergeSet(instance.first);
    mergeSet(instance.follow);
    mergeSet(instance.getGuard());

    for (ARTGrammarInstance tmp = instance.getChild(); tmp != null; tmp = tmp.getSibling())
      computeMergedSets(tmp);
  }

  private boolean computePlAttributeRec(ARTGrammarInstance node) {
    if (node == null) return false;

    boolean changed = false;

    if (!(node instanceof ARTGrammarInstanceSlot)) // Do not recurse through POS nodes
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
      computePlAttributeRec(tmp);

    changed |= (node.pL != pL(node));
    node.pL = pL(node);
    // System.out.println(changed);
    return changed;
  }

  private void computePredictivePopRec(ARTGrammarInstance node) {
    node.isPostPredictivePop = true;
    if (node.getSibling() != null) node.getSibling().isPostPredictivePop = true;

    if (node instanceof ARTGrammarInstanceRoot || node instanceof ARTGrammarInstanceLHS || node instanceof ARTGrammarInstanceAlt
        || node instanceof ARTGrammarInstanceDoFirst) // Propogate
      // to all
      // children
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computePredictivePopRec(tmp);
    else if (node instanceof ARTGrammarInstanceCat) { // Popogate to final child only as long as it is a terminal, nonterminal or do-first bracket
      ARTGrammarInstance lastNonPosChildNode = null;

      for (ARTGrammarInstance childNode = node.getChild(); childNode != null; childNode = childNode.getSibling()) { // locate last atom instance
        if (!(childNode instanceof ARTGrammarInstanceSlot)) lastNonPosChildNode = childNode;
      }

      if (lastNonPosChildNode instanceof ARTGrammarInstanceDoFirst)
        computePredictivePopRec(lastNonPosChildNode);
      else
        lastNonPosChildNode.getSibling().isPredictivePop = true;
    }

  }

  private boolean computeSetsRec(ARTGrammarInstance node, int level, ARTGrammarElementNonterminal lhs, ARTGrammarInstance bracketNode) throws ARTException {
    boolean changed = false;

    if (node == null) return changed;

    // artManager.text.printf("artComputesetsRec() visiting node %d at level %d with lhs %s and bracketNode %s\n", node.getKey(), level, lhs, bracketNode);

    changed |= (computeSetsRec(node.getSibling(), level, lhs, bracketNode));

    ARTGrammarInstance newBracketNode = bracketNode;

    if (node instanceof ARTGrammarInstanceDoFirst || node instanceof ARTGrammarInstanceOptional || node instanceof ARTGrammarInstancePositiveClosure
        || node instanceof ARTGrammarInstanceKleeneClosure)
      newBracketNode = node;

    if (node instanceof ARTGrammarInstanceRoot) {
      ; // Nothing to do
    }

    else if (node instanceof ARTGrammarInstanceLHS) {
      changed |= node.follow.addAll(((ARTGrammarElementNonterminal) node.getPayload()).getFollow());
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        changed |= node.first.addAll(tmp.first);

      changed |= (((ARTGrammarElementNonterminal) node.getPayload()).getFirst()).addAll(node.first);
    }

    else if (node instanceof ARTGrammarInstanceAlt) {
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        changed |= node.first.addAll(tmp.first);
    }

    else if (node instanceof ARTGrammarInstanceCat) {
      // Walk the children of a cat node until we find a non-nullable symbol skipping slot nodes
      for (ARTGrammarInstance child = node.getChild().getSibling(); child != null; child = child.getSibling().getSibling()) {
        HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(child.first);
        if (child.getSibling().getSibling() != null) tmp.remove(epsilon);

        changed |= node.first.addAll(tmp);
        if (!child.first.contains(epsilon)) break;
      }
    }

    else if (node instanceof ARTGrammarInstanceSlot) {
      if (node.getSibling() == null) { // \beta is \epsilon
        if (newBracketNode == null)
          changed |= node.first.add(epsilon);
        else {
          changed |= node.first.addAll(newBracketNode.getSibling().first); // fold in follow for this bracket
          if (newBracketNode instanceof ARTGrammarInstanceKleeneClosure || newBracketNode instanceof ARTGrammarInstancePositiveClosure)
            changed |= node.first.addAll(newBracketNode.first);
        }
      } else { // \beta is not epsilon so there will be an X (a terminal or a nonterminal) following, then another pos slot
        HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.getSibling().first);
        tmp.remove(epsilon);

        changed |= node.first.addAll(tmp);

        if (node.getSibling().first.contains(epsilon)) changed |= node.first.addAll(node.getSibling().getSibling().first); // bring over first (alpha X . beta)
      }

      // Guard set computation for slots
      HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.first);
      if (tmp.contains(epsilon)) if (newBracketNode == null)
        tmp.addAll(lhs.getFollow());
      else {
        tmp.addAll(newBracketNode.getSibling().getGuard());
        // For loops, we need the first of the body as well
        if (newBracketNode instanceof ARTGrammarInstanceKleeneClosure || newBracketNode instanceof ARTGrammarInstancePositiveClosure)
          tmp.addAll(newBracketNode.getGuard());
      }

      tmp.remove(epsilon);
      changed |= node.getGuard().addAll(tmp);

      return changed; // Do not recurse into actions!
    }

    else if (node instanceof ARTGrammarInstanceNonterminal) {
      ARTGrammarElementNonterminal nonterminal = (ARTGrammarElementNonterminal) node.getPayload();

      changed |= node.first.addAll(nonterminal.getFirst());

      HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>();
      tmp.addAll(node.getSibling().first);
      tmp.remove(epsilon);

      changed |= node.follow.addAll(tmp);

      if (node.getSibling().first.contains(epsilon)) // are we at the end of a rule?
        changed |= node.follow.addAll(lhs.getFollow());

      changed |= nonterminal.getFollow().addAll(node.follow);
    }

    else if (node instanceof ARTGrammarInstanceTerminal)
      ; // Nothing to do: first set is computed in constructor for ARTInstanceTerminal

    else if (node instanceof ARTGrammarInstanceEpsilon)
      ; // Nothing to do: first set is computed in constructor for ARTInstanceEpsilon

    else if (node instanceof ARTGrammarInstanceDoFirst || node instanceof ARTGrammarInstanceOptional || node instanceof ARTGrammarInstancePositiveClosure
        || node instanceof ARTGrammarInstanceKleeneClosure) {
      changed |= node.first.addAll(node.getChild().first);
      if (node instanceof ARTGrammarInstanceOptional || node instanceof ARTGrammarInstanceKleeneClosure) changed |= node.first.add(epsilon);
      if (!(node instanceof ARTGrammarInstanceDoFirst)) {// Do not compute for ( for consistency with V2 although the template does not use them
        HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.getChild().first);
        if (tmp.contains(epsilon)) tmp.addAll(node.getSibling().getGuard());

        tmp.remove(epsilon);
        changed |= node.getChild().getGuard().addAll(tmp);

      }
    }

    else
      throw new ARTException("unsupported node type " + node.getClass().toString() + " during set evaluation\n");

    changed |= (computeSetsRec(node.getChild(), level + 1, level == 1 ? (ARTGrammarElementNonterminal) node.getPayload() : lhs,
        node.bracketInstance(newBracketNode)));
    return changed;
  }

  private void computeSlotPrefixMap() {
    // System.out.println("Computing prefix entries");
    // Build the prefix string map
    for (ARTGrammarElementNonterminal n : nonterminals) { // Iterate n over nonterminals
      for (ARTGrammarInstance p : n.getProductions()) { // Iterate p over the production roots of n
        int prefixLength = 0;
        LinkedList<ARTGrammarElement> prefixString = new LinkedList<>();
        // Modified 30 Oct 2017 to ensure that only proper prefixes are mapped
        for (ARTGrammarInstance e = p.getChild(); e != null; e = e.getSibling()) { // Iterate e over the sequence of elements
          // production root p
          if (e.getSibling() == null)
            e.prefixLength = prefixLength;
          else if (e instanceof ARTGrammarInstanceSlot) {
            e.prefixLength = prefixLength;
            // System.out.println("Building prefix entries for " + e + " prefix length " + e.prefixLength);
            if (prefixStringMap.get(prefixString) == null) prefixStringMap.put(new LinkedList<>(prefixString), (ARTGrammarInstanceSlot) e);
            getPrefixSlotMap().put((ARTGrammarInstanceSlot) e, prefixStringMap.get(prefixString));
            // System.out.println(prefixString + " mapped to " + prefixStringMap.get(prefixString));
          } else {
            e.prefixLength = ++prefixLength;
            if (!(e instanceof ARTGrammarInstanceEpsilon)) prefixString.add(e.getPayload());
          }
        }
      }
    }
    // System.out.println(prefixStringMap);
  }

  private Integer elementNumberFromTerm(ARTModule artModule, ARTValueTerm t) throws ARTException {

    int ret = elementNumberFromTermCore(artModule, t);
    // System.out.println("elementNumberFromTerm (" + artModule.getId() + ", " + t + ") returns " + ret);
    return ret;
  }

  private Integer elementNumberFromTermCore(ARTModule artModule, ARTValueTerm t) throws ARTException {
    if (((ARTValueString) t.getPayload()).toString().equals("nonterminal")) {
      ARTGrammarElement e = getNonterminalNameMap().get(new ARTName(artModule, termFirstChildLabel(t)));
      if (e == null) e = getTerminalFromNonterminalNameMap().get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }
    if (((ARTValueString) t.getPayload()).toString().equals("caseSensitiveTerminal")) {
      ARTGrammarElement e = terminalCaseSensitiveNameMap.get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }
    if (((ARTValueString) t.getPayload()).toString().equals("caseInsensitiveTerminal")) {
      ARTGrammarElementTerminalCaseInsensitive e = terminalCaseInsensitiveNameMap.get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }
    if (((ARTValueString) t.getPayload()).toString().equals("builtinTerminal")) {
      ARTGrammarElementTerminalBuiltin e = terminalBuiltinNameMap.get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }
    throw new ARTException("Unknown term constructor " + t.getPayload() + " in elementFromTerm()");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTGrammar other = (ARTGrammar) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    return true;
  }

  /*
   * There are a couple of complexities here
   *
   * Firstly, we need to unpack the 'allSingletons' constructor by iterating over the grammars terminals
   *
   * Secondly, we have have elements in the modules term based relation that refer to unused grammar elements, and we don't want to accidentally create new
   * gammar elements at this stage. We handle this by ignoring attempts to extend the relations by pairs containing zeroes
   *
   */
  private void extendChoiceRelation(ARTBitSet[] map, ARTModule artModule, ARTValueTerm tLHS, Set<ARTValueTerm> set) throws ARTException {
    if (tLHS.getPayload().toString().equals("allSingletons")) // expand to a call of RHS method in all case seinsitive termunals
      for (ARTGrammarElement lhs : terminals) {
        if (lhs instanceof ARTGrammarElementTerminalCaseSensitive) extendChoiceRelationRHS(map, artModule, lhs.getElementNumber(), set);
      }
    else
      extendChoiceRelationRHS(map, artModule, elementNumberFromTerm(artModule, tLHS), set);

  }

  private void extendChoiceRelationRHS(ARTBitSet[] map, ARTModule artModule, int lhs, Set<ARTValueTerm> set) throws ARTException {
    if (lhs == 0) return; // Ignore unused lhs elements
    for (ARTValueTerm rhs : set)
      if (rhs.getPayload().toString().equals("allSingletons"))
        for (ARTGrammarElement rhsElement : terminals) {
          if (rhsElement instanceof ARTGrammarElementTerminalCaseSensitive) extendChoiceRelationElement(map, lhs, rhsElement.getElementNumber());
        }
      else
        extendChoiceRelationElement(map, lhs, elementNumberFromTerm(artModule, rhs));
  }

  private void extendChoiceRelationElement(ARTBitSet[] map, int lhs, int rhs) {
    // System.out.println("Extending " + lhs + "-> " + rhs);
    if (lhs == 0) return;
    if (rhs == 0) return;
    if (map[lhs] == null) map[lhs] = new ARTBitSet();
    map[lhs].set(rhs);
  }

  public ARTGrammarElementNonterminal findNonterminal(ARTModule artModule, String id) {
    return findNonterminal(new ARTName(artModule, id));
  }

  public ARTGrammarElementNonterminal findNonterminal(ARTGrammarElementModuleNonterminal moduleNonterminal) {
    return findNonterminal(moduleNonterminal.getModule(), moduleNonterminal.getId());
  }

  public ARTGrammarElementNonterminal findNonterminal(ARTName key) {
    ARTGrammarElementNonterminal ret = getNonterminalNameMap().get(key);
    if (ret == null) {
      getNonterminalNameMap().put(key, ret = new ARTGrammarElementNonterminal(key)); // Note that we are making a new one so that this grammar is independent of
                                                                                     // the
      // module
      nonterminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalBuiltin findTerminalBuiltin(String key) throws ARTException {
    ARTGrammarElementTerminalBuiltin ret = terminalBuiltinNameMap.get(key);
    if (ret == null) {
      terminalBuiltinNameMap.put(key, ret = new ARTGrammarElementTerminalBuiltin(key)); // Note that we are making a new one so that this grammar is
                                                                                        // independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalCaseInsensitive findTerminalCaseInsensitive(String key) {
    ARTGrammarElementTerminalCaseInsensitive ret = terminalCaseInsensitiveNameMap.get(key);
    if (ret == null) {
      terminalCaseInsensitiveNameMap.put(key, ret = new ARTGrammarElementTerminalCaseInsensitive(key)); // Note that we are making a new one so that this
                                                                                                        // grammar is independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalCaseSensitive findTerminalCaseSensitive(String key) {
    ARTGrammarElementTerminalCaseSensitive ret = terminalCaseSensitiveNameMap.get(key);
    if (ret == null) {
      terminalCaseSensitiveNameMap.put(key, ret = new ARTGrammarElementTerminalCaseSensitive(key)); // Note that we are making a new one so that this grammar
                                                                                                    // is independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalCharacter findTerminalCharacter(String key) {
    ARTGrammarElementTerminalCharacter ret = terminalCharacterNameMap.get(key);
    if (ret == null) {
      terminalCharacterNameMap.put(key, ret = new ARTGrammarElementTerminalCharacter(key)); // Note that we are making a new one so that this grammar is
                                                                                            // independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public String terminalFromNonterminalName(String moduleName, String nonterminalName) {
    return "ART_TFN_" + moduleName + "_" + nonterminalName;
  }

  public ARTGrammarElementTerminalFromNonterminal findTerminalFromNonterminal(String name) {
    ARTGrammarElementTerminalFromNonterminal ret = getTerminalFromNonterminalNameMap().get(name);
    if (ret == null) {
      getTerminalFromNonterminalNameMap().put(name, ret = new ARTGrammarElementTerminalFromNonterminal(name));
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementNonterminal getDefaultStartNonterminal() {
    return defaultStartNonterminal;
  }

  public ARTGrammarElement getElement(int n) {
    ;
    return elementNumberMap.get(n);
  }

  public Map<Integer, ARTGrammarElement> getElementNumberMap() {
    return elementNumberMap;
  }

  public ARTGrammarElementEoS getEoS() {
    return eoS;
  }

  public ARTGrammarElementEpsilon getEpsilon() {
    return epsilon;
  }

  public int getFirstNonterminalElementNumber() {
    return firstNonterminalElementNumber;
  }

  public int getFirstTerminalElementNumber() {
    return firstTerminalElementNumber;
  }

  public ARTModeGrammarKind getGrammarKind() {
    return isEBNF && !isFBNF ? ARTModeGrammarKind.EBNF : isEBNF ? ARTModeGrammarKind.FBNF : ARTModeGrammarKind.BNF;
  }

  public ARTBitSet[] getHigher() {
    return higher;
  }

  public String getId() {
    return id;
  }

  public ARTTree getInstanceTree() {
    return instanceTree;
  }

  public int getLastNonterminalElementNumber() {
    return lastNonterminalElementNumber;
  }

  public int getLastTerminalElementNumber() {
    return lastTerminalElementNumber;
  }

  public ARTBitSet[] getLonger() {
    return longer;
  }

  public HashMap<Set<ARTGrammarElement>, Integer> getMergedSets() {
    return mergedSets;
  }

  public Set<ARTGrammarElementNonterminal> getNonterminals() {
    return nonterminals;
  }

  public Map<ARTGrammarInstanceSlot, ARTGrammarInstanceSlot> getPrefixSlotMap() {
    return prefixSlotMap;
  }

  public Map<LinkedList<ARTGrammarElement>, ARTGrammarInstanceSlot> getPrefixStringMap() {
    return prefixStringMap;
  }

  public Set<String> getPreludeStrings() {
    return preludeStrings;
  }

  public Set<ARTGrammarElementAttribute> getSupportAttributes() {
    return supportAttributes;
  }

  public Set<String> getSupportStrings() {
    return supportStrings;
  }

  public Set<ARTGrammarElementTerminal> getTerminals() {
    return terminals;
  }

  public ARTGrammarElementNonterminal getUnaugmentedStartNonterminal() {
    return unaugmentedStartNonterminal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public boolean isEBNF() {
    return isEBNF;
  }

  public boolean isEoS(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementEoS;
  }

  public boolean isEpsilon(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementEpsilon;
  }

  public boolean isFBNF() {
    return isFBNF;
  }

  public boolean isNonterminal(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementNonterminal;
  }

  public boolean isOutOfRange(int n) {
    return elementNumberMap.get(n) == null;
  }

  public boolean isTerminal(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementTerminal;
  }

  // leftmostElementRec is used by the generator, hence package private
  public ARTGrammarInstance leftmostElementRec(ARTGrammarInstance node) {
    ARTGrammarInstance ret;

    if (node.getChild() == null || node instanceof ARTGrammarInstanceSlot) // Only consider down to POS nodes!
      ret = node;
    else
      ret = leftmostElementRec(node.getChild());

    return ret;
  }

  private void mergeSet(Set<ARTGrammarElement> set) {
    if (set != null && !mergedSets.containsKey(set)) {
      // System.out.println("Merging set " + nextFreeSetNumber + " " + set);
      mergedSets.put(set, nextFreeSetNumber++);
    }
  }

  private void numberSymbols() {
    getElementNumberMap().clear();
    int nextFreeSymbol = 0;

    eoS.setElementNumber(nextFreeSymbol++);
    getElementNumberMap().put(eoS.getElementNumber(), eoS);
    firstTerminalElementNumber = nextFreeSymbol;
    for (ARTGrammarElementTerminal t : terminals) {
      t.setElementNumber(nextFreeSymbol++);
      getElementNumberMap().put(t.getElementNumber(), t);
    }
    lastTerminalElementNumber = nextFreeSymbol - 1;
    epsilon.setElementNumber(nextFreeSymbol++);
    getElementNumberMap().put(epsilon.getElementNumber(), epsilon);

    // Suprise! Whitespace terminals are numbered above epsilon since that is the cutoff for lexical pattern matching
    for (ARTGrammarElementTerminal w : getWhitespaces()) {
      w.setElementNumber(nextFreeSymbol++);
      getElementNumberMap().put(w.getElementNumber(), w);
    }

    firstNonterminalElementNumber = nextFreeSymbol;
    for (ARTGrammarElementNonterminal n : nonterminals) {
      n.setElementNumber(nextFreeSymbol++);
      getElementNumberMap().put(n.getElementNumber(), n);
    }
    lastNonterminalElementNumber = nextFreeSymbol - 1;

    // TODO: grammar generation - enumerate slots in grammar
    lastSlotNumber = lastNonterminalElementNumber;
    // System.out.println(" Renumbered symbols to : " + getElementNumberMap());
  }

  private ARTGrammarInstance pL(ARTGrammarInstance node) {
    // We only want the slots immediately before a | (see definition in paper), that is EoA (end of alternate) unless we are at the end of the last sequence in
    // a bracket
    if (node.isEoA && !(node.isEoD || node.isEoO || node.isEoP || node.isEoK)) if (node.aL == node)
      return node;
    else
      return pL(node.aL);
    if (node.isEoOP) return pL(node.nL);
    return node;
  }

  public void prettyPrint() {
    ARTText pp = new ARTText();

    boolean terminalDeclaration = false;
    for (ARTGrammarElementTerminal t : terminals)
      terminalDeclaration |= t instanceof ARTGrammarElementTerminalFromNonterminal;

    if (terminalDeclaration) {
      pp.print("terminal");

      for (ARTGrammarElementTerminal t : terminals)
        if (t instanceof ARTGrammarElementTerminalFromNonterminal) pp.print(" " + t.toString());
      pp.println("\n");
    }

    boolean hasChoosers = false;
    for (int i = 0; i < higher.length; i++) {
      hasChoosers |= higher[i] != null && higher[i].isEmpty();
      hasChoosers |= longer[i] != null && longer[i].isEmpty();
    }

    if (hasChoosers) {
      pp.println("choose");
      for (int lhs = 0; lhs < higher.length; lhs++)
        for (int rhs = 0; rhs < higher.length; rhs++)
          if (higher[lhs].get(rhs)) pp.println(getElement(lhs) + ">>" + getElement(rhs));

      for (int lhs = 0; lhs < longer.length; lhs++)
        for (int rhs = 0; rhs < longer.length; rhs++)
          if (longer[lhs].get(rhs)) pp.println(getElement(lhs) + ">" + getElement(rhs));

      pp.println();
    }

    pp.println("start " + defaultStartNonterminal);

    for (ARTGrammarElementNonterminal n : nonterminals)
      // for (ARTGrammarInstance p : n.getProductions())
      if (n.lhsInstance != null) pp.println("\n" + n + " ::= " + n.lhsInstance.toGrammarSlotStringRec(null, "", "", false));
  }

  private boolean processAbbreviation(ARTModule artModule, ARTGrammarInstance parent, ARTGLLRDTVertex abbreviationVertex) throws ARTException {
    // System.out.println("Processing abbreviation node " + abbreviationVertex + " with label "
    // + artManager.getParser().artGetLabelString(abbreviationVertex.getPayload().getLabel()));
    ARTGLLRDTVertex vertex = abbreviationVertex.getChild();

    if (vertexLabel(vertex, "(")) return false;

    parent = parent.addChild(new ARTGrammarInstanceCat(instanceCount++));
    parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
    if (vertexLabel(vertex, "ARTV3.nonterminal"))
      parent.addChild(
          new ARTGrammarInstanceNonterminal(instanceCount++, findNonterminal(artModule, ((ARTAT_ARTV3_nonterminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "ARTV3.characterTerminal"))
      parent.addChild(
          new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCharacter(((ARTAT_ARTV3_characterTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "ARTV3.caseSensitiveTerminal"))
      parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++,
          findTerminalCaseSensitive(((ARTAT_ARTV3_caseSensitiveTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "ARTV3.caseInsensitiveTerminal"))
      parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++,
          findTerminalCaseInsensitive(((ARTAT_ARTV3_caseInsensitiveTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "ARTV3.builtinTerminal"))
      parent.addChild(
          new ARTGrammarInstanceTerminal(instanceCount++, findTerminalBuiltin(((ARTAT_ARTV3_builtinTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "ARTV3.epsilon"))
      parent.addChild(new ARTGrammarInstanceEpsilon(instanceCount++, epsilon));
    else
      throw new ARTException("Found abbreviation over unknown tree instance labelled" + vertex.getPayload());
    parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
    return true;
  }

  private void processAnnotations(ARTGrammarElementNonterminal lhs, ARTGrammarInstance instance, ARTGLLRDTVertex vertex) throws ARTException {
    if (vertex.getChild() == null) return;
    // System.err.printf("Processing annotations for grammar %s and RDT node %d - %s\n", instance, vertex.getKey(), vertex.toString());
    if (!vertexLabel(vertex.getChild(), "ARTV3.annotations")) throw new ARTException("Expecting node labelled ARTV3.annotations in tree");
    for (ARTGLLRDTVertex annotation = vertex.getChild().getChild(); annotation != null; annotation = annotation.getSibling())
      if (vertexLabel(annotation, "ARTV3.name")) {
        instance.instanceName = ((ARTAT_ARTV3_ID) annotation.getChild().getPayload().attributes).v;
      } else if (vertexLabel(annotation, "ARTV3.delay")) {
        instance.isDelayed = true;
        if (!(instance.getPayload() instanceof ARTGrammarElementNonterminal)) throw new ARTException("Only nonterminal nodes may be delayed (" + vertex + ")");
        ((ARTGrammarElementNonterminal) instance.getPayload()).hasDelayedInstances = true;
        lhs.setContainsDelayedInstances(true);
      } else if (vertexLabel(annotation, "ARTV3.gather")) {
        if (instance.gatherName != null) throw new ARTException("Only one gather annotation allowed at node " + vertex);
        instance.gatherName = ((ARTAT_ARTV3_ID) annotation.getChild().getPayload().attributes).v;
      } else if (vertexLabel(annotation, "ARTV3.fold")) {
        ARTGLLRDTVertex fold = annotation.getChild();
        if (instance.fold != ARTFold.EMPTY)
          System.out.println("Only one fold annotation allowed at node " + vertex + " on RDT node " + instance + " under LHS " + lhs);
        if (vertexLabel(fold, "^_"))
          instance.fold = ARTFold.NONE;
        else if (vertexLabel(fold, "^"))
          instance.fold = ARTFold.UNDER;
        else if (vertexLabel(fold, "^^"))
          instance.fold = ARTFold.OVER;
        else if (vertexLabel(fold, "^^^"))
          instance.fold = ARTFold.TEAR;
        else
          throw new ARTException("Unknown fold annotation " + fold.getPayload() + " on RDT node " + instance + " under LHS " + lhs);
      }
  }

  private ARTGrammarInstance rightmostElementRec(ARTGrammarInstance node) {
    if (node == null) return null;
    // System.out.println("rightMostElementRec visiting null node!");
    // System.out.println("rightMostElementRec visiting node " + node.getKey());
    ARTGrammarInstance ret = null;

    if (node.getChild() == null || node instanceof ARTGrammarInstanceSlot) // Only consider down to POS nodes!
      ret = node;
    else
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        ret = rightmostElementRec(tmp);

    return ret;
  }

  public void setSupportAttributes(Set<ARTGrammarElementAttribute> supportAttributes) {
    this.supportAttributes = supportAttributes;
  }

  private String termFirstChildLabel(ARTValueTerm t) {
    return t.getChild().getPayload().toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("** Grammar ");
    builder.append("'" + id + "'\n");
    builder.append(optionBlock);
    builder.append("\nisEBNF=");
    builder.append(isEBNF);
    builder.append(", isFBNF=");
    builder.append(isFBNF);
    builder.append("\nunaugmentedStartNonterminal=");
    builder.append(unaugmentedStartNonterminal);
    builder.append(", defaultStartNonterminal=");
    builder.append(defaultStartNonterminal);
    builder.append("\nnonterminals=");
    builder.append(nonterminals);
    builder.append("\nterminals=");
    builder.append(terminals);
    builder.append("\nwhitespaces=");
    builder.append(getWhitespaces());
    builder.append("\nlonger=");
    builder.append(Arrays.toString(longer));
    builder.append("\nhigher=");
    builder.append(Arrays.toString(higher));
    return builder.toString();
  }

  private boolean vertexLabel(ARTGLLRDTVertex vertex, String string) {
    // System.out.println("Checking vertex " + vertex + " against " + string);
    String[] strings = artManager.getParser().artLabelStrings;
    int label = vertex.getPayload().getLabel();
    String lstring = strings[label];
    return lstring.equals(string);
  }

  public ARTOptionBlock getOptionBlock() {
    return optionBlock;
  }

  public Map<ARTName, ARTGrammarElementNonterminal> getNonterminalNameMap() {
    return nonterminalNameMap;
  }

  public Set<ARTGrammarElementTerminal> getWhitespaces() {
    return whitespaces;
  }

  public Map<String, ARTGrammarElementTerminalFromNonterminal> getTerminalFromNonterminalNameMap() {
    return terminalFromNonterminalNameMap;
  }

  public Set<ARTGrammarElementNonterminal> getNonterminalsDeclaredAsTerminals() {
    return nonterminalsDeclaredAsTerminals;
  }

  public int getLastSlotNumber() {
    return lastSlotNumber;
  }

  public ARTManager getARTManager() {
    return artManager;
  }
}
