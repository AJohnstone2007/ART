package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.PrintStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.GIFTKind;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOverNaturals;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class SPPF extends AbstractDerivations {
  public final AbstractParser parser;
  public SPPFSymbolNode root;
  public String inputString; // Original input string

  public final Map<SPPFSymbolNode, SPPFSymbolNode> nodes = new HashMap<>();
  public final BitSet visited = new BitSet();

  public final BitSet cyclic = new BitSet();
  public RelationOverNaturals reachable;
  public BitSet rootReachable = new BitSet();
  private final BitSet suppressedSPPFNode = new BitSet();
  public final Set<SPPFPackedNode> cbD = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  public final Set<SPPFPackedNode> cbDPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note

  public SPPF(AbstractParser parser) {
    super();
    this.parser = parser;
  }

  @Override
  public AbstractDerivationNode find(CFGNode dn, int li, int ri) {
    // Util.debug("sppfFind with dn " + dn.toStringAsProduction() + " with extents " + li + "," + ri);

    AbstractDerivationNode tmp = new SPPFSymbolNode(dn, li, ri);
    if (!nodes.containsKey(tmp)) {
      nodes.put((SPPFSymbolNode) tmp, (SPPFSymbolNode) tmp);
      // Util.debug(" added " + tmp);
    }
    // Util.debug(" resulting sppf\n" + nodes.keySet());
    return nodes.get(tmp);
  }

  @Override
  public AbstractDerivationNode extend(CFGNode gn, AbstractDerivationNode leftNode, AbstractDerivationNode rightNode) {
    SPPFSymbolNode ret = (SPPFSymbolNode) find(gn.cfgElement.cfgKind == CFGKind.END ? gn.seq : gn,
        leftNode == null ? rightNode.getLeftExtent() : leftNode.getLeftExtent(), rightNode.getRightExtent());
    // Util.debug(
    // "Extending SPPF node with gn " + gn.toStringAsProduction() + " and extents " + (ln == null ? rn.li : ln.li) + "," + rn.ri + " retrieves node " + ret);
    ret.packNodes.add(new SPPFPackedNode(gn, leftNode == null ? rightNode.getLeftExtent() : leftNode.getRightExtent(), (SPPFSymbolNode) leftNode,
        (SPPFSymbolNode) rightNode));
    return ret;
  }

  @Override
  public void print(PrintStream ps) {
    if (root == null) {
      Util.warning("no SPPF root node - skipping printing");
      return;
    }
    for (var n : nodes.keySet()) {
      ps.println(n);
      for (var pn : n.packNodes)
        ps.println(pn);
    }
  }

  @Override
  public void numberNodes() {
    int nextFreeNodeNumber = 1;
    for (var n : nodes.keySet())
      n.number = nextFreeNodeNumber++;
    for (var n : nodes.keySet())
      for (var p : n.packNodes) {
        p.parent = n;
        p.number = nextFreeNodeNumber++;
      }

    reachable = new RelationOverNaturals(nextFreeNodeNumber, nextFreeNodeNumber);
  }

  @Override
  public int widestIndex() {
    int ret = 0;
    for (SPPFSymbolNode s : nodes.keySet())
      if (ret < s.rightExtent) ret = s.rightExtent;
    // Util.debug("Returning widest derivation " + ret);
    return ret;
  }

  /* Temporary disambiguation before choosers are implemented ****************/
  @Override
  public void chooseLongestMatch() {
    visited.clear();
    if (root == null) {
      Util.warning("SPPF contains no derivations: skipping choosers");
      return;
    }
    chooseLongestMatchRec(root);
  }

  private void chooseLongestMatchRec(SPPFSymbolNode sn) {
    if (visited.get(sn.number)) return;
    visited.set(sn.number);

    int rightMostPivot = -1;
    SPPFPackedNode candidate = null;
    if (sn.packNodes.size() > 1) {
      Util.warning("Ambiguity detected at SPPF node " + sn.number + ": " + sn.grammarNode.toStringAsProduction() + " involving ");
      for (var p : sn.packNodes)
        Util.info("   " + p.toString());
    }
    for (SPPFPackedNode p : sn.packNodes) {
      if (p.pivot > rightMostPivot) {
        rightMostPivot = p.pivot;
        candidate = p;
      }
      if (p.leftChild != null) chooseLongestMatchRec(p.leftChild);
      if (p.rightChild != null) chooseLongestMatchRec(p.rightChild);
    }

    for (SPPFPackedNode p : sn.packNodes)
      if (p != candidate) p.suppressed = true;
  }

  /* Term generation **************************************************************************/
  /* This version handles promotion operators, but does not create ambiguity nodes */

  long derivationNodeCount = 0, derivationAmbiguityNodeCount = 0;

  boolean derivationForInterpreter = false;

  @Override
  public int derivationAsInterpeterTerm() {
    derivationForInterpreter = true;
    int ret = derivationAsTerm();
    derivationForInterpreter = false;
    return ret;
  }

  boolean derivationSeenCycle;

  @Override
  public int derivationAsTerm() {
    if (root == null) return 0;
    visited.clear();
    derivationSeenCycle = false;
    LinkedList<Integer> carrier = new LinkedList<>();
    derivationAsTermRec(root, carrier, firstAvailablePackNode(root).grammarNode.seq); // Root grammar node is the end of a start production
    return carrier.getFirst();
  }

  private String derivationAsTermRec(SPPFSymbolNode sppfn, LinkedList<Integer> childrenFromParent, CFGNode gn) {
    // Util.debug("Entered derivationAsTermRec() at node (" + sppfn + ") instance " + gn.toStringAsProduction());
    if (visited.get(sppfn.number)) {
      if (!derivationSeenCycle) Util.error(System.err, "derivationAsTermRec() found cycle in derivation");
      derivationSeenCycle = true;
      return "__CYCLE";
    }

    visited.set(sppfn.number);
    LinkedList<Integer> children = (gn.giftKind == GIFTKind.OVER || gn.giftKind == GIFTKind.UNDER) ? childrenFromParent : new LinkedList<>();
    String constructor = null;

    SPPFPackedNode firstAvailableSPPFPN = null;
    if (sppfn.packNodes.size() != 0) { // Non leaf symbol nodes
      firstAvailableSPPFPN = firstAvailablePackNode(sppfn);
      CFGNode childgn = firstAvailableSPPFPN.grammarNode.alt.seq;
      LinkedList<SPPFSymbolNode> childSymbolNodes = new LinkedList<>();
      collectChildNodesRec(firstAvailableSPPFPN, childSymbolNodes);
      for (SPPFSymbolNode s : childSymbolNodes) {
        String newConstructor = derivationAsTermRec(s, children, childgn);
        if (newConstructor != null) constructor = newConstructor; // Update on every ^^ child so that the last one wins
        childgn = childgn.seq; // Step to next child grammar node
      }
    }

    if (constructor == null) // If there were no OVERs, then set the constructor to be our symbol
      if (derivationForInterpreter)
      constructor = firstAvailableSPPFPN == null ? "" + -sppfn.rightExtent : "" + firstAvailableSPPFPN.grammarNode.alt.num;
      else
      constructor = (gn.cfgElement.cfgKind == CFGKind.B) ? parser.lexer.lexeme(sppfn.leftExtent) : gn.cfgElement.str;

    if (children != childrenFromParent) {
      childrenFromParent.add(parser.cfgRules.iTerms.findTerm(constructor, children));
      derivationNodeCount++;
    }

    visited.clear(sppfn.number);
    return (gn.giftKind == GIFTKind.OVER) ? constructor : null;
  }

  private void collectChildNodesRec(SPPFPackedNode sppfpn, LinkedList<SPPFSymbolNode> childNodes) {
    // Util.info("CollectChildNodesRec() at pack node " + sppfpn);
    SPPFSymbolNode leftChild = sppfpn.leftChild, rightChild = sppfpn.rightChild;
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

  private SPPFPackedNode firstAvailablePackNode(SPPFSymbolNode sppfn) {
    SPPFPackedNode candidate = null;
    boolean ambiguous = false;
    for (SPPFPackedNode p : sppfn.packNodes)
      if (!p.suppressed) if (candidate == null)
        candidate = p;
      else
        ambiguous = true;

    if (candidate == null) Util.info("No unsuppressed pack nodes found at SPPF node " + sppfn);

    if (ambiguous) {
      Util.info("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
      for (SPPFPackedNode p : sppfn.packNodes)
        if (!p.suppressed) Util.info("  " + p);
    }
    return candidate;
  }

  private boolean ambiguousSPPF;

  @Override
  public boolean ambiguityCheck() {
    ambiguousSPPF = false;
    if (root != null) ambiguityCheckRec(root);
    return ambiguousSPPF;
  }

  private void ambiguityCheckRec(SPPFSymbolNode sppfn) {
    // if (sppfn.gn.elm.kind != CFGKind.N) return;
    int activePackedNodes = 0;
    for (SPPFPackedNode p : sppfn.packNodes)
      if (!p.suppressed) {
        activePackedNodes++;
        if (p.leftChild != null) ambiguityCheckRec(p.leftChild);
        ambiguityCheckRec(p.rightChild);
      }

    // if (activePackedNodes == 0) Util.info("No unsuppressed pack nodes found at SPPF node " + sppfn);

    if (activePackedNodes > 1) {
      ambiguousSPPF = true;
      Util.info("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
      for (SPPFPackedNode p : sppfn.packNodes)
        if (!p.suppressed) Util.info("  " + p);
    }
  }

  @Override
  public void setRoot(CFGNode cfgNode, int n) {
    root = nodes.get(new SPPFSymbolNode(cfgNode, 0, n));
  }

  @Override
  public void statistics(Statistics currentStatistics) {
    int sppfEpsilonNodeCount = 0, sppfTerminalNodeCount = 0, sppfNonterminalNodeCount = 0, sppfIntermediateNodeCount = 0, sppfPackNodeCount = 0,
        sppfAmbiguityCount = 0, sppfEdgeCount = 0;
    for (SPPFSymbolNode s : nodes.keySet()) {
      switch (s.grammarNode.cfgElement.cfgKind) {
      // Dodgy - how do we test the flavour of an SPPF node?
      case T, TI, C, B:
        sppfTerminalNodeCount++;
        break;
      case EPS:
        sppfEpsilonNodeCount++;
        break;
      }
      sppfPackNodeCount += s.packNodes.size();
      if (s.packNodes.size() > 1) sppfAmbiguityCount++;
      for (SPPFPackedNode p : s.packNodes) {
        sppfEdgeCount++; // inedge
        if (p.leftChild != null) sppfEdgeCount++;
        if (p.rightChild != null) sppfEdgeCount++;
      }
    }

    ScriptTermInterpreter.currentStatistics.put("sppfEpsilonNodeCount", sppfEpsilonNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfTerminalNodeCount", sppfTerminalNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfNonterminalNodeCount", sppfNonterminalNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfIntermediateNodeCount", sppfIntermediateNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfSymbolPlusIntermediateNodeCount", nodes.keySet().size());
    ScriptTermInterpreter.currentStatistics.put("sppfPackNodeCount", sppfPackNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfAmbiguityCount", sppfAmbiguityCount);
    ScriptTermInterpreter.currentStatistics.put("sppfEdgeCount", sppfEdgeCount);
    // loadPoolAllocated(-1);
    // loadHashCounts(-20, -21, -22, -23, -24, -25, -26);
  }

  @Override
  public void choose(ChooseRules chooseRules) {
    // TODO Auto-generated method stub

  }
}
