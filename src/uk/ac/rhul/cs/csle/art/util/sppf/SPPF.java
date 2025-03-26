package uk.ac.rhul.cs.csle.art.util.sppf;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexemeKind;
import uk.ac.rhul.cs.csle.art.util.DerivationRep;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOverNaturals;

public class SPPF extends DerivationRep {
  public final CFGRules cfgRules;
  public SPPFN rootNode;
  public String inputString; // Original input string
  public int[] tokens; // Input as array of tokens
  public int[] leftIndices;

  public SPPF(CFGRules cfgRules) {
    super();
    this.cfgRules = cfgRules;
    this.rootNode = rootNode;
  }

  public void loadLexicalisation(String inputString, int[] tokens, int[] leftIndices) {
    this.inputString = inputString;
    this.tokens = tokens;
    this.leftIndices = leftIndices;
  }

  public final Map<SPPFN, SPPFN> nodes = new HashMap<>();
  public final BitSet visited = new BitSet();

  public final BitSet cyclic = new BitSet();
  public RelationOverNaturals reachable;
  public BitSet rootReachable = new BitSet();
  private final BitSet suppressedSPPFNode = new BitSet();
  public final Set<SPPFPN> cbD = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  public final Set<SPPFPN> cbDPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note

  public void computeCoreReachability(Object object) {
    // TODO Auto-generated method stub

  }

  public void numberSPPFNodes() {
    int nextFreeNodeNumber = 1;
    for (var n : nodes.keySet())
      n.number = nextFreeNodeNumber++;
    for (var n : nodes.keySet())
      for (var p : n.packNS) {
        p.parent = n;
        p.number = nextFreeNodeNumber++;
      }

    reachable = new RelationOverNaturals(nextFreeNodeNumber, nextFreeNodeNumber);
  }

  /* Temporary disambiguation before choosers are implemented ****************/
  public void chooseLongestMatch() {
    visited.clear();
    chooseLongestMatchRec(rootNode);
  }

  private void chooseLongestMatchRec(SPPFN sn) {
    if (visited.get(sn.number)) return;
    visited.set(sn.number);

    int rightMostPivot = -1;
    SPPFPN candidate = null;
    if (sn.packNS.size() > 1) {
      Util.warning("Ambiguity detected at SPPF node " + sn.number + ": " + sn.gn.toStringAsProduction() + " involving ");
      for (var p : sn.packNS)
        Util.info("   " + p.toString());
    }
    for (SPPFPN p : sn.packNS) {
      if (p.pivot > rightMostPivot) {
        rightMostPivot = p.pivot;
        candidate = p;
      }
      if (p.leftChild != null) chooseLongestMatchRec(p.leftChild);
      if (p.rightChild != null) chooseLongestMatchRec(p.rightChild);
    }

    for (SPPFPN p : sn.packNS)
      if (p != candidate) p.suppressed = true;
  }

  /* Term generation **************************************************************************/
  /* This version handles promotion operators, but does not create ambiguity nodes */

  long derivationNodeCount = 0, derivationAmbiguityNodeCount = 0;

  boolean derivationForInterpreter = false;

  public int derivationAsInterpeterTerm() {
    derivationForInterpreter = true;
    int ret = derivationAsTerm();
    derivationForInterpreter = false;
    return ret;
  }

  boolean derivationSeenCycle;

  public int derivationAsTerm() {
    if (rootNode == null) return 0;
    visited.clear();
    derivationSeenCycle = false;
    LinkedList<Integer> carrier = new LinkedList<>();
    derivationAsTermRec(rootNode, carrier, firstAvailablePackNode(rootNode).gn.seq); // Root grammar node is the end of a start production
    return carrier.getFirst();
  }

  private String derivationAsTermRec(SPPFN sppfn, LinkedList<Integer> childrenFromParent, CFGNode gn) {
    // System.out.println("\nEntered derivationAsTermRec() at node " + sppfn + " instance " + gn);
    if (visited.get(sppfn.number)) {
      if (!derivationSeenCycle) Util.error(System.err, "derivationAsTermRec() found cycle in derivation");
      derivationSeenCycle = true;
      return "__CYCLE";
    }

    visited.set(sppfn.number);
    LinkedList<Integer> children = (gn.giftKind == GIFTKind.OVER || gn.giftKind == GIFTKind.UNDER) ? childrenFromParent : new LinkedList<>();
    String constructor = null;

    SPPFPN firstAvailableSPPFPN = null;
    if (sppfn.packNS.size() != 0) { // Non leaf symbol nodes
      firstAvailableSPPFPN = firstAvailablePackNode(sppfn);
      CFGNode childgn = firstAvailableSPPFPN.gn.alt.seq;
      LinkedList<SPPFN> childSymbolNodes = new LinkedList<>();
      collectChildNodesRec(firstAvailableSPPFPN, childSymbolNodes);
      for (SPPFN s : childSymbolNodes) {
        String newConstructor = derivationAsTermRec(s, children, childgn);
        if (newConstructor != null) constructor = newConstructor; // Update on every ^^ child so that the last one wins
        childgn = childgn.seq; // Step to next child grammar node
      }
    }

    if (constructor == null) // If there were no OVERs, then set the constructor to be our symbol
      if (derivationForInterpreter)
      constructor = firstAvailableSPPFPN == null ? "" + -sppfn.ri : "" + firstAvailableSPPFPN.gn.alt.num;
      else
      constructor = (gn.elm.kind == CFGKind.B) ? AbstractParser.lexemeOfBuiltin(LexemeKind.valueOf(gn.elm.str), inputString, leftIndices[sppfn.li])
          : gn.elm.str;

    if (children != childrenFromParent) {
      childrenFromParent.add(cfgRules.iTerms.findTerm(constructor, children));
      derivationNodeCount++;
    }

    visited.clear(sppfn.number);
    return (gn.giftKind == GIFTKind.OVER) ? constructor : null;
  }

  private void collectChildNodesRec(SPPFPN sppfpn, LinkedList<SPPFN> childNodes) {
    // System.out.println("CollectChildNodesRec() at pack node " + sppfpn);
    SPPFN leftChild = sppfpn.leftChild, rightChild = sppfpn.rightChild;
    if (leftChild != null) {
      if (leftChild.isSymbol()) // found a symbol
        childNodes.add(leftChild);
      else
        collectChildNodesRec(firstAvailablePackNode(leftChild), childNodes);
    }

    if (rightChild.isSymbol()) // found a symbol
      childNodes.add(rightChild);
    else
      collectChildNodesRec(firstAvailablePackNode(rightChild), childNodes);
  }

  private SPPFPN firstAvailablePackNode(SPPFN sppfn) {
    SPPFPN candidate = null;
    boolean ambiguous = false;
    for (SPPFPN p : sppfn.packNS)
      if (!p.suppressed) if (candidate == null)
        candidate = p;
      else
        ambiguous = true;

    if (candidate == null) System.out.println("No unsuppressed pack nodes found at SPPF node " + sppfn);

    if (ambiguous) {
      System.out.println("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
      for (SPPFPN p : sppfn.packNS)
        if (!p.suppressed) System.out.println("  " + p);
    }
    return candidate;
  }

  private boolean ambiguousSPPF;

  public boolean ambiguityCheck() {
    ambiguousSPPF = false;
    if (rootNode != null) ambiguityCheckRec(rootNode);
    return ambiguousSPPF;
  }

  public void ambiguityCheckRec(SPPFN sppfn) {
    // if (sppfn.gn.elm.kind != CFGKind.N) return;
    int activePackedNodes = 0;
    for (SPPFPN p : sppfn.packNS)
      if (!p.suppressed) {
        activePackedNodes++;
        if (p.leftChild != null) ambiguityCheckRec(p.leftChild);
        ambiguityCheckRec(p.rightChild);
      }

    // if (activePackedNodes == 0) System.out.println("No unsuppressed pack nodes found at SPPF node " + sppfn);

    if (activePackedNodes > 1) {
      ambiguousSPPF = true;
      System.out.println("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
      for (SPPFPN p : sppfn.packNS)
        if (!p.suppressed) System.out.println("  " + p);
    }
  }
}
