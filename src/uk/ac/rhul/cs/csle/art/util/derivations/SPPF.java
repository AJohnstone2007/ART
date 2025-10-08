package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.PrintStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.GIFTKind;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOverNaturals;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class SPPF extends AbstractDerivations {
  public SPPFSymbolNode root;

  public final Map<SPPFSymbolNode, SPPFSymbolNode> nodes = new HashMap<>();
  public final BitSet visited = new BitSet();

  public final BitSet cyclic = new BitSet();
  public RelationOverNaturals reachable;
  public BitSet rootReachable = new BitSet();
  private final BitSet suppressedSPPFNode = new BitSet();
  public final Set<SPPFPackedNode> cbD = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  public final Set<SPPFPackedNode> cbDPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note

  public SPPF(AbstractLexicalisations lexicalisations) {
    this.lexicalisations = lexicalisations;
  }

  @Override
  public AbstractDerivationNode find(CFGNode dn, int leftExtent, int rightExtent) {
    // Util.debug("sppf.find with dn [" + dn.num + "] " + dn.toStringAsProduction() + " with extents " + leftExtent + "," + rightExtent);

    AbstractDerivationNode tmp = new SPPFSymbolNode(dn, leftExtent, rightExtent);
    if (!nodes.containsKey(tmp)) {
      nodes.put((SPPFSymbolNode) tmp, (SPPFSymbolNode) tmp);
      // Util.debug(" added " + tmp);
    }
    // Util.debug(" resulting sppf\n" + nodes.keySet());
    return nodes.get(tmp);
  }

  @Override
  public AbstractDerivationNode extend(CFGNode gn, AbstractDerivationNode leftNode, AbstractDerivationNode rightNode) {
    // Util.debug("sppf.extend grammar node " + gn.toStringAsProduction() + " with left node " + leftNode + " and right node " + rightNode);

    if (gn == null) return rightNode;

    if (ScriptInterpreter.currentModes.contains("compactderivations") && lexicalisations.cfgRules.secondSlots.contains(gn)) {
      // Util.debug("sppf.extend grammar node is second node - returning rightNode");
      return rightNode;
    }

    SPPFSymbolNode ret = (SPPFSymbolNode) find(gn.cfgElement.cfgKind == CFGElementKind.END ? gn.seq : gn,
        leftNode == null ? rightNode.getLeftExtent() : leftNode.getLeftExtent(), rightNode.getRightExtent());

    // Util.debug("Extending SPPF node with gn " + gn.toStringAsProduction() + " and extents "
    // + (leftNode == null ? rightNode.getLeftExtent() : leftNode.getLeftExtent()) + "," + rightNode.getRightExtent() + " retrieves node " + ret);

    ret.packNodes.add(new SPPFPackedNode(gn, leftNode == null ? rightNode.getLeftExtent() : leftNode.getRightExtent(), (SPPFSymbolNode) leftNode,
        (SPPFSymbolNode) rightNode));

    return ret;
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
    int ret = 1;
    for (SPPFSymbolNode s : nodes.keySet())
      if (ret < s.rightExtent) ret = s.rightExtent;
    // Util.debug("Returning widest derivation " + ret);
    return ret;
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
    try {
      derivationAsTermRec(root, carrier, firstAvailablePackNode(root).grammarNode.seq); // Root grammar node is the end of a start production
    } catch (TermException e) {
      return 0;
    }
    return carrier.getFirst();
  }

  private String derivationAsTermRec(SPPFSymbolNode sppfn, LinkedList<Integer> childrenFromParent, CFGNode gn) throws TermException {
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
      constructor = (gn.cfgElement.cfgKind == CFGElementKind.TRM_BI) ? lexicalisations.lexeme(sppfn.grammarNode, sppfn.leftExtent) : gn.cfgElement.str;

    // Util.debug("At SPPF node " + sppfn + " and grammar node " + gn + " make new term with constructor: " + constructor);
    if (children != childrenFromParent) {
      childrenFromParent.add(ScriptInterpreter.iTerms.findTerm(constructor, children));
      derivationNodeCount++;
    }

    visited.clear(sppfn.number);
    return (gn.giftKind == GIFTKind.OVER) ? constructor : null;
  }

  private void collectChildNodesRec(SPPFPackedNode sppfpn, LinkedList<SPPFSymbolNode> childNodes) throws TermException {
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

  private SPPFPackedNode firstAvailablePackNode(SPPFSymbolNode sppfn) throws TermException {
    SPPFPackedNode candidate = null;
    boolean ambiguous = false;
    for (SPPFPackedNode p : sppfn.packNodes)
      if (!p.suppressed) if (candidate == null)
        candidate = p;
      else
        ambiguous = true;

    if (candidate == null) {
      Util.error("Ambiguous SPPF node has all children suppressed - please review choosers: ");
      for (SPPFPackedNode p : sppfn.packNodes)
        Util.info(" " + p);
      throw new TermException();
    }

    // if (ambiguous) {
    // Util.info("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
    // for (SPPFPackedNode p : sppfn.packNodes)
    // if (!p.suppressed) Util.info(" " + p);
    // }
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
      Util.warning("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
      for (SPPFPackedNode p : sppfn.packNodes)
        if (!p.suppressed) Util.info(" " + p);
    }
  }

  @Override
  public void setRoot(CFGNode cfgNode, int n) {
    root = nodes.get(new SPPFSymbolNode(cfgNode, 0, n));
  }

  public void loadStatistics(Statistics currentStatistics) {
    int sppfEpsilonNodeCount = 0, sppfTerminalNodeCount = 0, sppfNonterminalNodeCount = 0, sppfIntermediateNodeCount = 0, sppfPackNodeCount = 0,
        sppfAmbiguityCount = 0, sppfEdgeCount = 0;
    for (SPPFSymbolNode s : nodes.keySet()) {
      switch (s.grammarNode.cfgElement.cfgKind) {
      // Dodgy - how do we test the flavour of an SPPF node?
      case TRM_CS, TRM_CI, TRM_CH, TRM_BI:
        sppfTerminalNodeCount++;
        break;
      case EPSILON:
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

    ScriptInterpreter.currentStatistics.put("sppfEpsilonNodeCount", sppfEpsilonNodeCount);
    ScriptInterpreter.currentStatistics.put("sppfTerminalNodeCount", sppfTerminalNodeCount);
    ScriptInterpreter.currentStatistics.put("sppfNonterminalNodeCount", sppfNonterminalNodeCount);
    ScriptInterpreter.currentStatistics.put("sppfIntermediateNodeCount", sppfIntermediateNodeCount);
    ScriptInterpreter.currentStatistics.put("sppfSymbolPlusIntermediateNodeCount", nodes.keySet().size());
    ScriptInterpreter.currentStatistics.put("sppfPackNodeCount", sppfPackNodeCount);
    ScriptInterpreter.currentStatistics.put("sppfAmbiguityCount", sppfAmbiguityCount);
    ScriptInterpreter.currentStatistics.put("sppfEdgeCount", sppfEdgeCount);
    // loadPoolAllocated(-1);
    // loadHashCounts(-20, -21, -22, -23, -24, -25, -26);
  }

  Set<CFGNode> ambiguousSlots;

  @Override
  public void choose(ChooseRules chooseRules) {
    // Util.debug("Running choosers");
    visited.clear();
    ambiguousSlots = new HashSet<>();
    if (root == null) {
      Util.warning("SPPF contains no derivations: skipping choosers");
      return;
    }
    chooseRec(root);

    if (!ambiguousSlots.isEmpty()) {
      Util.warning("After choose rule application, ambiguities remain involving the following slots. Use !print ambiguities for full details");
      for (var s : ambiguousSlots)
        Util.info(s.toStringAsProduction());
    }
  }

  private void chooseRec(SPPFSymbolNode sn) {
    // Util.debug("chooseRec() at " + sn);
    if (visited.get(sn.number)) return;
    visited.set(sn.number);

    // 1. Choose
    if (sn.packNodes.size() > 1) {
      for (SPPFPackedNode leftPackedNode : sn.packNodes)
        for (SPPFPackedNode rightPackedNode : sn.packNodes) {
          if (leftPackedNode == rightPackedNode) continue; // Do not self test
          var leftNode = leftPackedNode.grammarNode;
          var rightNode = rightPackedNode.grammarNode;
          if ((ScriptInterpreter.currentChooseRules.testLonger(leftNode, rightNode) && leftPackedNode.pivot > rightPackedNode.pivot)
              || (ScriptInterpreter.currentChooseRules.testShorter(leftNode, rightNode) && leftPackedNode.pivot < rightPackedNode.pivot)
              || (ScriptInterpreter.currentChooseRules.testHigher(leftNode, rightNode))) {
            rightPackedNode.suppressed = true;
            Util.warning("Suppressed  " + rightPackedNode);
          }
        }

      // 2. Flag any remaining ambiguities
      int livePN = 0;
      for (var pn : sn.packNodes)
        if (!pn.suppressed) livePN++;

      if (livePN == 0)
        Util.error("No unsuppressed packed nodes under " + sn);
      else if (livePN > 1) for (var pn : sn.packNodes)
        if (!pn.suppressed) ambiguousSlots.add(pn.grammarNode);
    }

    // 3. Recurse
    for (var p : sn.packNodes) {
      if (p.suppressed) continue;
      if (p.leftChild != null) chooseRec(p.leftChild);
      if (p.rightChild != null) chooseRec(p.rightChild);
    }

  }

  @Override
  public void breakCycles(boolean trace, boolean counts, boolean statistics) {
    new SPPFCycleBreak(this).sppfCycleBreak();
  }

  @Override
  public void breakCyclesRelation() {
    new SPPFCycleBreak(this).sppfBreakCyclesRelation();
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    for (var n : nodes.keySet()) {
      outputStream.println(n);
      for (var pn : n.packNodes) {
        outputStream.print(pn.suppressed ? "*" : " ");
        outputStream.println(pn);
      }
    }
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    new SPPF2Dot(this, outputStream, indexed, full, true);
  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
    // TODO Auto-generated method stub

  }

  @Override
  public long bsrCardinality() {
    long ret = 0;
    for (var n : nodes.keySet())
      for (var pn : n.packNodes)
        ret++;
    return ret;
  }

  @Override
  public long symbolCardinality() {
    long ret = 0;
    for (var n : nodes.keySet())
      ret++;
    return ret;
  }
}
