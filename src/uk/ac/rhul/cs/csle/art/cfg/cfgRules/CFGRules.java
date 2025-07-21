
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

import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public final class CFGRules implements DisplayInterface { // final to avoid this-escape
  public final ITerms iTerms;

  // Constants
  private final Set<CFGKind> lexKinds = Set.of(CFGKind.TRM_BI, CFGKind.TRM_CH, CFGKind.TRM_CS, CFGKind.TRM_CI);
  private final Set<CFGKind> selfFirst = Set.of(CFGKind.TRM_BI, CFGKind.TRM_CH, CFGKind.EOS, CFGKind.TRM_CS, CFGKind.TRM_CI, CFGKind.EPS);
  private int nextFreeEnumerationElement;
  public final CFGElement epsilonElement;
  public final CFGElement startOfStringElement;
  public final CFGElement endOfStringElement;
  public final CFGNode endOfStringNode;

  public String name = "";
  public CFGElement startNonterminal;

  public final Map<CFGElement, CFGElement> elements = new TreeMap<>();

  public final Map<Integer, CFGNode> numberToNodeMap = new TreeMap<>();
  public final Map<CFGElement, CFGNode> elementToNodeMap = new TreeMap<>(); // Map from nonterminals to list of productions represented by their LHS node

  public int lexSize;

  public Set<String> paraterminalNames = new HashSet<>();
  public Set<CFGElement> paraterminalElements = new HashSet<>();

  // Grammar analysis data
  public final Relation<CFGElement, CFGElement> first = new Relation<>();
  public final Relation<CFGElement, CFGElement> follow = new Relation<>();

  public final Relation<CFGElement, CFGElement> reach1 = new Relation<>(); // { (X,Y) | X ::= \alpha Y \beta }
  public final Relation<CFGElement, CFGElement> leftNullableReach1 = new Relation<>(); // { (X,Y) | X ::= \alpha Y \beta }
  public final Relation<CFGElement, CFGElement> rightNullableReach1 = new Relation<>(); // { (X,Y) | X ::= \alpha Y \beta }

  public final Relation<CFGElement, CFGElement> reach = new Relation<>(); // reach1*
  public final Relation<CFGElement, CFGElement> leftNullableReach = new Relation<>(); // leftNullableReach*
  public final Relation<CFGElement, CFGElement> rightNullableReach = new Relation<>(); // rightNullableREach*

  public final Relation<CFGNode, CFGElement> instanceFirst = new Relation<>(); // definition?
  public final Relation<CFGNode, CFGElement> instanceFollow = new Relation<>(); // definition?

  public final Set<CFGNode> initialSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \alpha = \epsilon }
  public final Set<CFGNode> secondSlots = new HashSet<>(); // { X ::= \alpha Y . \beta} | \alpha = \epsilon, Y \ne \epsilon }
  public final Set<CFGNode> penultimateSlots = new HashSet<>(); // { X ::= \alpha . Y \beta} | \beta = \epsilon, Y \ne \epsilon }
  public final Set<CFGNode> finalSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \beta = \epsilon }

  public final Set<CFGNode> nullablePrefixSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \alpha =>* \epsilon }
  public final Set<CFGNode> nullableSuffixSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \beta =>* \epsilon }
  public final Set<CFGElement> cyclicNonterminals = new HashSet<>(); // { X ::= \alpha X \beta} | \alpha,\beta =>* \epsilon }
  public final Set<CFGNode> cyclicSlots = new HashSet<>(); // { X ::= \alpha X \beta} | \alpha,\beta =>* \epsilon }
  public Relation<CFGElement, CFGElement> derivesExactly;
  public Relation<CFGElement, CFGElement> derivesExactlyTransitiveClosure;

  public final Set<CFGNode> acceptingSlots = new HashSet<>(); // Set of slots which are END nodes of the start production
  public final Set<Integer> acceptingNodeNumbers = new TreeSet<>(); // Set of node number for the slots on accepting slots

  public String filePrelude = null;
  public String classPrelude = null;

  public boolean seenWhitespaceDirective = false;

  public CFGRules(String name, ITerms iTerms) {
    this.name = name;
    this.iTerms = iTerms;
    epsilonElement = findElement(CFGKind.EPS, "#");
    endOfStringElement = findElement(CFGKind.EOS, "$");
    startOfStringElement = findElement(CFGKind.SOS, "$$");

    endOfStringNode = new CFGNode(this, CFGKind.EOS, "$", 0, GIFTKind.NONE, null, null);
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode
  }

  // TODO: Whatis this for?
  public CFGRules(CFGRules that, boolean binarise) {
    this.name = that.name;
    this.iTerms = that.iTerms;
    epsilonElement = findElement(CFGKind.EPS, "#");
    endOfStringElement = findElement(CFGKind.EOS, "$");
    startOfStringElement = findElement(CFGKind.SOS, "$$");

    endOfStringNode = new CFGNode(this, CFGKind.EOS, "$", 0, GIFTKind.NONE, null, null);
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode

    for (var r : that.elementToNodeMap.keySet()) {
      lhsAction(r.str);
      // TODO: recursive traversal of rules required because of EBNF
    }
  }

  public void normalise() {
    if (!seenWhitespaceDirective) findElement(CFGKind.TRM_BI, "SIMPLE_WHITESPACE").isWhitespace = true;

    derivesExactly = new Relation<>();
    // Add singleton grammar nodes for terminals, # and epsilon. These are used by the SPPF.
    for (CFGElement e : elements.keySet())
      if (e.cfgKind == CFGKind.TRM_BI || e.cfgKind == CFGKind.TRM_CS || e.cfgKind == CFGKind.TRM_CI || e.cfgKind == CFGKind.TRM_CH || e.cfgKind == CFGKind.EPS)
        elementToNodeMap.put(e, new CFGNode(this, e.cfgKind, e.str, 0, GIFTKind.NONE, null, null));

    // Element and node numbering
    nextFreeEnumerationElement = 0;
    numberElementsAndNodes();
    setEndNodeLinks();

    // Report nonterminals with no rules, and create paraterminal element set
    Set<CFGElement> tmp = new HashSet<>();
    for (CFGElement e : elements.keySet())
      if (e.cfgKind == CFGKind.NON) {
        if (elementToNodeMap.get(e) == null) tmp.add(e);

        if (paraterminalNames.contains(e.str)) paraterminalElements.add(e);
      }

    if (tmp.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("Nonterminal" + (tmp.size() == 1 ? " " : "s ") + "used but not defined: ");
      for (CFGElement n : tmp)
        sb.append(n.str + " ");
      Util.fatal(sb.toString());
    }

    // Set positional attributes and accepting slots, and seed nullablePrefixSlots and nullableSuffixSlots
    for (CFGElement ge : elements.keySet())
      if (ge.cfgKind == CFGKind.NON) for (CFGNode gn = elementToNodeMap.get(ge).alt; gn != null; gn = gn.alt) {
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

    // Seed first sets
    for (CFGElement ge : elements.keySet())
      if (selfFirst.contains(ge.cfgKind)) first.add(ge, ge);

    // Seed follow sets
    if (startNonterminal != null) follow.add(startNonterminal, endOfStringElement);

    // Util.info("Initial first sets: " + first);
    // Util.info("Initial instance first sets: " + instanceFirst);
    // Util.info("Initial follow sets: " + follow);
    // Util.info("Initial instance follow sets: " + instanceFollow);
    computeFirstSetsAndNullablePrefixes();
    computeNullableSuffixesAndCyclic();
    computeFollowSets();
    computeCyclicSlots();//
    // Util.info("Final first sets: " + first);
    // Util.info("Final instance first sets: " + instanceFirst);
    // Util.info("Final follow sets: " + follow);
    // Util.info("Final instance follow sets: " + instanceFollow);

    // Collect attributes
    // Util.info("*** Collecting attributes");
    for (CFGElement e : elements.keySet()) {
      // Util.info("*** Collecting attributes for element " + e.toStringDetailed());
      Map<String, Integer> rhsNonterminalsInProduction = new HashMap<>();
      if (e.cfgKind == CFGKind.NON) { // Only look at nonterminals
        String lhs = e.str;
        for (CFGNode gn = elementToNodeMap.get(e).alt; gn != null; gn = gn.alt) { // iterate over the productions
          rhsNonterminalsInProduction.clear();
          for (CFGNode gs = gn.seq; gs.cfgElement.cfgKind != CFGKind.END; gs = gs.seq) {
            // Util.info("Collecting RHS nonterminals at " + gs + " " + iTerms.toRawString(gs.slotTerm));
            if (gs.cfgElement.cfgKind == CFGKind.NON) {
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
        for (CFGNode gn = elementToNodeMap.get(e).alt; gn != null; gn = gn.alt) {
          for (CFGNode gs = gn.seq; gs.cfgElement.cfgKind != CFGKind.END; gs = gs.seq) {
            for (int i = 0; i < iTerms.termArity(gs.slotTerm); i++) {
              int annotationRoot = iTerms.subterm(gs.slotTerm, i);
              // Util.info("Processing slot annotation " + iTerms.toString(annotationRoot));
              switch (iTerms.termSymbolString(annotationRoot)) {
              case "cfgEquation", "cfgAssignment":
                checkTermActionsRec(annotationRoot, lhs, e.rhsNonterminalsAcrossAllProductions);
                break;

              case "cfgNative":
                checkNativeActions(iTerms.toString(annotationRoot), lhs, e.rhsNonterminalsAcrossAllProductions, true);
                break;
              case "cfgInsert":
                break;
              }
            }
            // Util.info("Collecting attributes at " + gs + " " + iTerms.toRawString(gs.slotTerm));
          }
        }
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
        if (lhs.cfgKind == CFGKind.NON) {
          CFGNode topNode = elementToNodeMap.get(lhs);
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
              if (seqNode.cfgElement.cfgKind == CFGKind.NON) changed |= instanceFirst.add(seqNode, seqNode.cfgElement);

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
      if (lhs.cfgKind == CFGKind.NON) {
        CFGNode topNode = elementToNodeMap.get(lhs);
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
      if (lhs.cfgKind == CFGKind.NON) {
        CFGNode topNode = elementToNodeMap.get(lhs);
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

    if (seqNode.cfgElement.cfgKind == CFGKind.NON && nullablePrefixSlots.contains(seqNode) && nullableSuffixSlots.contains(seqNode.seq))
      derivesExactly.add(lhs, seqNode.cfgElement);

    return nullableSuffix;
  }

  private void computeFollowSets() {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CFGElement lhs : elements.keySet())
        if (lhs.cfgKind == CFGKind.NON) {
          CFGNode topNode = elementToNodeMap.get(lhs);
          // Util.info("Visiting top node " + topNode.num + ":" + topNode);
          for (CFGNode altNode = topNode.alt; altNode != null; altNode = altNode.alt) {
            // Util.info("Visiting alt node " + altNode.num + ":" + altNode);
            CFGNode seqNode = altNode.seq;
            while (true) {
              changed |= instanceFollow.addAll(seqNode, removeEpsilon(instanceFirst.get(seqNode.seq)));
              if (seqNode.cfgElement.cfgKind == CFGKind.NON) changed |= follow.addAll(seqNode.cfgElement, removeEpsilon(instanceFirst.get(seqNode.seq)));
              if (nullableSuffixSlots.contains(seqNode.seq)) changed |= follow.addAll(seqNode.cfgElement, follow.get(lhs));
              if (seqNode.cfgElement.cfgKind == CFGKind.END) break;
              seqNode = seqNode.seq;
            }
          }
        }
    }
  }

  public boolean isEmpty() {
    return elementToNodeMap.keySet().isEmpty();
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
    if (findElement(CFGKind.NON, nonterminalID).attributes.get(attributeID) == null) findElement(CFGKind.NON, nonterminalID).attributes.put(attributeID, "int");
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
    if (iTerms.termSymbolString(annotationRoot).equals("cfgAttribute")) {
      String nonterminalID = iTerms.termSymbolString(iTerms.subterm(annotationRoot, 0));
      String attributeID = iTerms.termSymbolString(iTerms.subterm(annotationRoot, 1));
      validateAttribute(nonterminalID, attributeID, lhs, rhsNonterminals, false); // int is default attribute type - overriden by <> declaration on rhs
    } else
      for (int i = 0; i < iTerms.termArity(annotationRoot); i++)
        checkTermActionsRec(iTerms.subterm(annotationRoot, i), lhs, rhsNonterminals);
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
      if (lexKinds.contains(s.cfgKind)) lexSize = s.number;
      // Util.info("Enumerating grammar element " + s.ei + ": " + s.str);
    }
    lexSize++;

    numberToNodeMap.put(endOfStringNode.num = nextFreeEnumerationElement++, endOfStringNode);
    for (CFGElement n : elementToNodeMap.keySet())
      numberElementsAndNodesRec(elementToNodeMap.get(n));
  }

  private void numberElementsAndNodesRec(CFGNode node) {
    if (node != null) {
      numberToNodeMap.put(node.num = nextFreeEnumerationElement++, node);
      if (node.cfgElement.cfgKind != CFGKind.END) {
        numberElementsAndNodesRec(node.seq);
        numberElementsAndNodesRec(node.alt);
      }
    }
  }

  private void setEndNodeLinks() {
    for (CFGElement n : elementToNodeMap.keySet()) {
      CFGNode lhs = elementToNodeMap.get(n);
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
    if (parentNode == elementToNodeMap.get(startNonterminal)) acceptingNodeNumbers.add(gn.num);
    // Util.info("processEndNodes updated alt and seq to " + gn.alt.ni + " " + gn.seq.ni);
  }

  // Atttribute-action functions from ReferenceGrammarParser.art below this line
  public CFGNode workingNode;
  public GIFTKind workingFold = GIFTKind.NONE;
  // public int workingAction = 0;

  CFGNode mostRecentLHS;

  /* Action routines called from the script term traverser */
  public CFGElement findElement(CFGKind kind, String s) {
    CFGElement candidate = new CFGElement(kind, s);
    if (elements.get(candidate) == null) elements.put(candidate, candidate);
    return elements.get(candidate);
  }

  public CFGElement findWhitespaceElement(CFGKind kind, String s) {
    var ret = findElement(kind, s);
    ret.isWhitespace = true;
    seenWhitespaceDirective = true;
    return ret;
  }

  LinkedList<CFGNode> stack = new LinkedList<>();

  public void lhsAction(String id) {
    CFGElement element = findElement(CFGKind.NON, id);
    if (startNonterminal == null) startNonterminal = element;
    workingNode = elementToNodeMap.get(element);
    if (workingNode == null) elementToNodeMap.put(element, updateWorkingNode(CFGKind.NON, id, 0));
    mostRecentLHS = elementToNodeMap.get(element);
  }

  public void altAction() {
    stack.push(workingNode);
    while (workingNode.alt != null) // Maintain specification order by adding new alternate at the end
      workingNode = workingNode.alt;
    workingNode = new CFGNode(this, CFGKind.ALT, null, 0, workingFold, null, workingNode);
    workingFold = GIFTKind.NONE;
  }

  public void endAction(String actions) {
    updateWorkingNode(CFGKind.END, "", 0);
    workingNode = stack.pop();
  }

  public CFGNode updateWorkingNode(CFGKind kind, String str, Integer slotTerm) {
    // Util.debug("Update working node with kind " + kind + " string " + str + " and slot term " + slotTerm);
    if (kind == CFGKind.TRM_CH_ANTI_SET) {
      System.out.println("!Bang !!");
    }

    workingNode = new CFGNode(this, kind, str, slotTerm, workingFold, workingNode, null);
    workingFold = GIFTKind.NONE;
    return workingNode;
  }

  public void attributeAction(String name, String type) {
    mostRecentLHS.cfgElement.attributes.put(name, type);
  }

  /** Support for table driven parsers ***************************************/

  public int[] makeKindsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (CFGElement gs : elements.keySet())
      ret[gs.number] = gs.cfgKind.ordinal();
    for (int ni : numberToNodeMap.keySet())
      ret[ni] = numberToNodeMap.get(ni).cfgElement.cfgKind.ordinal();
    return ret;
  }

  public int[][] makeAltsArray() {
    int ret[][] = new int[nextFreeEnumerationElement][];
    for (int ni : numberToNodeMap.keySet()) {
      CFGNode gn = numberToNodeMap.get(ni);
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
    for (int ni : numberToNodeMap.keySet()) {
      CFGNode sn = numberToNodeMap.get(ni).seq;
      ret[ni] = sn == null ? 0 : sn.num;
    }
    return ret;
  }

  public int[] makeCallTargetsArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : numberToNodeMap.keySet()) {
      CFGNode lhs = elementToNodeMap.get(numberToNodeMap.get(ni).cfgElement);
      ret[ni] = (lhs == null ? 0 : lhs.num);
    }
    return ret;
  }

  public int[] makeElementOfArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : numberToNodeMap.keySet()) {
      CFGElement el = numberToNodeMap.get(ni).cfgElement;
      ret[ni] = (el == null ? 0 : el.number);
    }
    return ret;
  }

  /** Static methods *********************************************************/

  public static boolean isLHS(CFGNode gn) {
    if (gn == null) return false;
    return gn.cfgElement != null && gn.cfgElement.cfgKind == CFGKind.NON && gn.seq == null;
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
        if (sententialForm.get(leftmostNonterminalIndex).cfgKind == CFGKind.NON) break;

      if (leftmostNonterminalIndex == sententialForm.size()) { // This is a sentence
        printSententialForm(count++, sententialForm);
        continue;
      }

      if (!sentencesOnly) printSententialForm(count++, sententialForm);

      CFGElement expansionNonterminal = sententialForm.get(leftmostNonterminalIndex);

      for (var altNode = elementToNodeMap.get(expansionNonterminal).alt; altNode != null; altNode = altNode.alt) {
        LinkedList<CFGElement> newSententialForm = new LinkedList<>();

        for (int i = 0; i < leftmostNonterminalIndex; i++)
          newSententialForm.addLast(sententialForm.get(i));

        for (var seqNode = altNode.seq; seqNode.cfgElement.cfgKind != CFGKind.END; seqNode = seqNode.seq)
          switch (seqNode.cfgElement.cfgKind) {
          case TRM_CS, TRM_CI, TRM_CH, TRM_BI, NON:
            newSententialForm.add(seqNode.cfgElement);
            break;
          case EPS:
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
      if (e.cfgKind == CFGKind.NON)
        System.out.print(" _" + e.str);
      else
        System.out.print(" " + e.str);
    System.out.println();
  }

  private boolean isSentence(LinkedList<CFGElement> sententialForm) {
    for (var e : sententialForm)
      if (e.cfgKind == CFGKind.NON) return false;
    return true;
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    outputStream.print(
        (isEmpty() ? "Empty" : "") + "Context Free Grammar rules with start symbol " + (startNonterminal == null ? "<empty>" : startNonterminal.str) + "\n");

    for (CFGElement n : elementToNodeMap.keySet()) {
      boolean first = true;
      for (CFGNode production = elementToNodeMap.get(n).alt; production != null; production = production.alt) {
        if (first) {
          outputStream.print(" " + production.toStringAsProduction(" ::=\n ", null) + "\n");
          first = false;
        } else {
          outputStream.print(" | " + production.toStringAsRHS(null) + "\n");
        }
      }
    }

    if (full) {
      outputStream.print("Elements:\n");
      for (CFGElement s : elements.keySet()) {
        outputStream.print(" (" + s.toStringDetailed() + ") " + s);
        if (s.isWhitespace) outputStream.print(" whitespace");
        if (cyclicNonterminals.contains(s)) outputStream.print(" cyclic");
        if (paraterminalElements.contains(s)) outputStream.print(" paraterminal");
        if (s.attributes != null && !s.attributes.isEmpty()) outputStream.print(" attributes: " + s.attributes);
        if (full && first.get(s) != null) {
          outputStream.print(" first = {");
          printElements(outputStream, first.get(s));
          outputStream.print("} follow = {");
          printElements(outputStream, follow.get(s));
          outputStream.print("}");
        }
        outputStream.print("\n");
      }
      outputStream.print("Nodes:\n");
      for (int i : numberToNodeMap.keySet()) {
        CFGNode gn = numberToNodeMap.get(i);
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
        if (gn.slotTerm != 0) outputStream.print(" Action: " + gn.toStringActions());
        outputStream.print("\n");
      }

      outputStream.print("Accepting slot" + (acceptingSlots.size() == 1 ? "" : "s") + ":\n");
      for (var gn : acceptingSlots)
        outputStream.print(" " + gn.toStringAsProduction() + "\n");

      outputStream.print("Accepting node number" + (acceptingSlots.size() == 1 ? "" : "s") + ":");
      for (var gn : acceptingNodeNumbers)
        outputStream.print(" " + gn);
      outputStream.println();

      outputStream.print("Paraterminals: " + paraterminalNames);
      outputStream.println();

      outputStream.print("Cyclic nonterminals: " + cyclicNonterminals);
      outputStream.println();

      outputStream.print("derivesOnly(R):");
      outputStream.println();
      for (var n : derivesExactly.getDomain())
        if (cyclicNonterminals.contains(n)) {
          outputStream.print(n + ": {");
          for (var nf : derivesExactly.get(n))
            outputStream.print(" " + nf);
          outputStream.println(" }");
        }

      outputStream.println("derivesOnlyClosed (R+):");
      for (var n : derivesExactlyTransitiveClosure.getDomain())
        if (cyclicNonterminals.contains(n)) {
          outputStream.print(n + ": {");
          for (var nf : derivesExactlyTransitiveClosure.get(n))
            outputStream.print(" " + nf);
          outputStream.println(" }");
        }

      outputStream.print("cocyclic nonterminals:\n");
      for (var n : derivesExactlyTransitiveClosure.getDomain())
        if (cyclicNonterminals.contains(n)) {
          outputStream.print(n + ": {");
          for (var nf : derivesExactlyTransitiveClosure.get(n))
            if (derivesExactlyTransitiveClosure.get(nf).contains(n)) outputStream.print(" " + nf);
          outputStream.println(" }");
        }

      outputStream.print("cyclic slots:\n");
      for (var n : cyclicSlots) {
        outputStream.print(n.toStringAsProduction());
        outputStream.println();
      }
    }
  }

  private void printElements(PrintStream outputStream, Set<CFGElement> elements) {
    if (elements == null) return;
    boolean first = true;
    for (CFGElement e : elements) {
      if (first)
        first = false;
      else
        outputStream.print(",");
      outputStream.print(e.toString());
    }
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    outputStream.print("digraph \"Reference grammar\"\n" + "{\n" + "graph[ordering=out ranksep=0.1]\n"
        + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04 color=gray]\n"
        + "edge[fontname=Helvetica fontsize=9 arrowsize = 0.3 color=gray]\n\n");
    for (CFGElement n : elementToNodeMap.keySet())
      toStringDotRec(outputStream, elementToNodeMap.get(n));
    outputStream.print("}\n");
  }

  private void toStringDotRec(PrintStream outputStream, CFGNode cs) {
    outputStream.print("\"" + cs.num + "\"[label=\"" + cs.toStringDot() + "\"]\n");
    if (cs.cfgElement.cfgKind == CFGKind.ALT) {
      seqArrow(outputStream, cs);
      altArrow(outputStream, cs);
    } else if (cs.cfgElement.cfgKind != CFGKind.END) {
      altArrow(outputStream, cs);
      seqArrow(outputStream, cs);
    }
  }

  private void altArrow(PrintStream outputStream, CFGNode cs) {
    if (cs.alt == null) return;
    outputStream.print(
        "\"" + cs.num + "\"->\"" + cs.alt.num + "\"" + "{rank = same; \"" + cs.num + "\"" + ";\"" + cs.alt.num + "\"" + ";" + "}" + "[label=\" a\"" + "]\n");
    if (!isLHS(cs.alt)) toStringDotRec(outputStream, cs.alt);
  }

  private void seqArrow(PrintStream outputStream, CFGNode cs) {
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
