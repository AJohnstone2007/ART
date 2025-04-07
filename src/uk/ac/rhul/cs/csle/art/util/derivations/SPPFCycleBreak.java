package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOverNaturals;

public class SPPFCycleBreak {
  private final SPPF sppf;
  private final Deque<AbstractSPPFNode> visitedDeque = new ArrayDeque<>(); // Only usedby sppfCycleRec to keep a list of visited nodes during descent
  private Set<AbstractSPPFNode> xNodesBeforeBreaking; // All cyclic nodes - only used by SPPF diagnostics
  private Set<SPPFSymbolNode> xS; // Set of cYclic symbol or intermediate nodes; a subset of the X in Elizabeth's note
  private Set<SPPFPackedNode> xP; // Set of cYclic packed nodes; X = Xs U Xp
  private Set<SPPFPackedNode> cbD = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  private final Set<SPPFPackedNode> cbDPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note
  private final Relation<AbstractSPPFNode, AbstractSPPFNode> sppfReachableSlow = new Relation<>();
  private RelationOverNaturals sppfReachable;
  private final BitSet sppfCyclic = new BitSet();
  private BitSet sppfRootReachable;
  private boolean cycleBreakTrace;
  private boolean cycleBreakCounts;
  private boolean cycleBreakStatistics;

  SPPFCycleBreak(SPPF sppf) {
    this.sppf = sppf;
  }

  private void sppfComputeCoreReachability(Set<CFGNode> cyclicCFGSlots) {

    if (sppf == null || sppf.rootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping reachability analysis");
      return;
    }

    // System.out.println("Cyclic slots: " + cyclicCFGSlots);

    sppfReachable.clear();
    // System.out.println("After clearing sppfReachable, contents are:\n" + sppfReachable);
    for (var n : sppf.nodes.keySet())
      for (var p : n.packNodes) {
        if (cbD.contains(p)) {
          // System.out.println("Skipping Deleted packed node " + p);
          continue;
        }

        if (cyclicCFGSlots != null && !cyclicCFGSlots.contains(p.grammarNode)) continue;

        // System.out.println("Adding cyclicSPPFNode " + n);
        sppfReachable.add(n.number, p.number);
        if (p.leftChild != null) sppfReachable.add(p.number, p.leftChild.number);
        sppfReachable.add(p.number, p.rightChild.number);

      }
    // System.out.println("After initialising sppfReachable, contents are:\n" + sppfReachable);
    sppfReachable.transitiveClosure();
    // System.out.println("After transitive closure of sppfReachable, contents are:\n" + sppfReachable);

    sppfRootReachable = sppfReachable.getCodomain(sppf.rootNode.number);
    // System.out.println("Root reachable set: " + sppfRootReachable);
    sppfCyclic.clear();
    for (int n = 0; n < sppfReachable.domainSize(); n++)
      if (sppfReachable.get(n, n)) sppfCyclic.set(n);
  }

  public void sppfPrintCyclicSPPFNodesFromReachability() {
    sppfComputeCoreReachability(sppf.cfgRules.cyclicSlots);
    System.out.println(sppfCyclic.isEmpty() ? "There are no cyclic nodes" : "Cyclic nodes are");
    for (int i = 0; i < sppfCyclic.length(); i++)
      if (sppfCyclic.get(i)) System.out.print("  " + i);
    System.out.println();
  }

  public void loadXPartitionsFromReachability() {
    // In this implementation, X is represented by its packed node and symbol node partitions xP and xS
    xP = new HashSet<>();
    xS = new HashSet<>();
    for (var n : sppf.nodes.keySet())
      if (sppfCyclic.get(n.number)) {
        xS.add(n);
        for (var p : n.packNodes)
          if (sppfCyclic.get(p.number)) {
            // xNodesBeforeBreaking.add(p);
            xP.add(p);
          }
      }
  }

  public void loadXPartitionsFromCFGRulesRec(SPPFSymbolNode n) {
    if (sppf.visited.get(n.number)) return;
    sppf.visited.set(n.number);
    // System.out.println("loadXPartitionsFromCFGRulesRec() entered " + n);
    for (var p : n.packNodes) {
      if (sppf.cfgRules.cyclicSlots.contains(p.grammarNode)) {
        xS.add(n);
        // System.out.println("Added cyclic symbol node " + n);
        xP.add(p);
        // System.out.println("Added cyclic packed node " + p);
      }

      if (p.leftChild != null) loadXPartitionsFromCFGRulesRec(p.leftChild);
      loadXPartitionsFromCFGRulesRec(p.rightChild);
    }
  }

  void trace(String msg) {
    if (cycleBreakTrace) System.out.println(msg);
  }

  private int cycleBreakPass;

  boolean sppfCycleBreak() {
    trace("SPPF cycle break pass " + cycleBreakPass++ + " with xS:" + xS + " xP:" + xP + " D:" + cbD + " D':" + cbDPrime);
    for (var v : new HashSet<>(xS)) {
      trace("Checking symbol node " + v + " with child predicate " + noPackedChildInXp(v, xP));
      if (noPackedChildInXp(v, xP)) {
        xS.remove(v);
        if (cycleBreakTrace) System.out.println("Removed from xS: " + v);
        return true;
      }
    }

    for (var v : new HashSet<>(xP)) {
      trace("Checking packed node " + v + " with child predicate " + noSymbolChildInXs(v, xS) + " and sibling predicate " + somePackedSiblingNotInXp(v, xP));

      if (noSymbolChildInXs(v, xS) || somePackedSiblingNotInXp(v, xP)) {
        xP.remove(v);
        if (cycleBreakTrace) System.out.println("Removed from xP: " + v);
        if (!noSymbolChildInXs(v, xS) && somePackedSiblingNotInXp(v, xP)) cbD.add(v);
        return true;
      }
    }
    return false;
  }

  boolean sppfCycleBreakOriginal() {
    trace("SPPF cycle break pass " + cycleBreakPass++ + " with xS:" + xS + " xP:" + xP + " D:" + cbD + " D':" + cbDPrime);
    for (var v : new HashSet<>(xS)) {
      trace("Checking symbol node " + v + " with child predicate " + noPackedChildInXp(v, xP));
      if (noPackedChildInXp(v, xP)) {
        xS.remove(v);
        if (cycleBreakTrace) System.out.println("Removed from xS: " + v);
        return true; // Unnecessary? Experiment 1
        // break; // Experiment 2: Break out of loop instead of return
        // Experiment 3: remember v in loop and then remove directly from xS outside of the loop
      }
    }

    for (var v : new HashSet<>(xP)) {
      trace("Checking packed node " + v + " with child predicate " + noSymbolChildInXs(v, xS) + " and sibling predicate " + somePackedSiblingNotInXp(v, xP));

      if (noSymbolChildInXs(v, xS) || somePackedSiblingNotInXp(v, xP)) {
        xP.remove(v);
        if (cycleBreakTrace) System.out.println("Removed from xP: " + v);
        // if (noSymbolChildInX(v, xS) && somePackedSiblingNotInX(v, xP)) cbDPrime.add(v); // Not needed for base algorithm
        if (!noSymbolChildInXs(v, xS) && somePackedSiblingNotInXp(v, xP)) cbD.add(v);
        return true;
      }
    }
    return false;
  }

  private void cbUpdate(SPPFPackedNode v, Set<SPPFPackedNode> Dset) {
    Dset.add(v);
    xP.remove(v);
    if (cycleBreakTrace) System.out.println("Removed from xP: " + v);
  }

  private void cbUpdate(SPPFSymbolNode v) {
    xS.remove(v);
    if (cycleBreakTrace) System.out.println("Removed from xS: " + v);
  }

  // children(v) ∩ Xi = ∅ (symbol node child predicate)
  public boolean noPackedChildInXp(SPPFSymbolNode sppfN, Set<SPPFPackedNode> xP) {
    for (var pn : sppfN.packNodes)
      if (xP.contains(pn)) return false;
    return true;
  }

  // children(v) ∩ Xi = ∅ (packed node child predicate)
  public boolean noSymbolChildInXs(SPPFPackedNode sppfPN, Set<SPPFSymbolNode> xS) {
    if (sppfPN.leftChild != null && xS.contains(sppfPN.leftChild)) return false;
    if (xS.contains(sppfPN.rightChild)) return false;
    return true;
  }

  // sibling(v) ̸⊆ Xi (packed node sibling predicate)
  public boolean somePackedSiblingNotInXp(SPPFPackedNode sppfPN, Set<SPPFPackedNode> xP) {
    for (var p : sppfPN.parent.packNodes)
      if (p != sppfPN && !xP.contains(p)) return true;
    return false;
  }

  private long cycleBreakStartTime, cycleBreakEndTime;
  private int countSymbol = 0;
  private int countInter = 0;
  private int countPacked = 0;
  private int countPara = 0;
  private int countTerm = 0;
  private int countEps = 0;
  private boolean countRemove = false;
  Set<AbstractSPPFNode> countReachable;

  public void sppfBreakCycles(boolean trace, boolean counts, boolean statistics) {
    if (sppf == null || sppf.rootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping cycle breaking");
      return;
    }
    this.cycleBreakTrace = cycleBreakTrace;
    this.cycleBreakCounts = counts;
    this.cycleBreakStatistics = statistics;
    xP = new HashSet<>();
    xS = new HashSet<>();
    cbD = new HashSet<>();
    countReachable = new HashSet<>();
    sppf.visited.clear();
    loadXPartitionsFromCFGRulesRec(sppf.rootNode);

    if (statistics) {
      System.out.print(sppf.inputString.length() + "," + getClass().getSimpleName() + "," + "accept" + ",");
      countSymbol = countInter = countPacked = countPara = countTerm = countEps = 0;
      sppf.visited.clear();
      countRemove = false;
      sppfBreakCyclesCountsRec(sppf.rootNode);
      System.out.print(countSymbol + "," + countInter + "," + countPacked + "," + countPara + "," + countTerm + "," + countEps + "," + xS.size() + ","
          + xP.size() + "," + cbD.size());
    }

    if (counts) {
      System.out.println("Core:\tSymbol\tInter\tPacked\tPara\tTerm\tEps\t|Xs|\t|Xp|\t|D|");
      countSymbol = countInter = countPacked = countPara = countTerm = countEps = 0;
      sppf.visited.clear();
      countRemove = false;
      sppfBreakCyclesCountsRec(sppf.rootNode);
      System.out.println("Before\t" + countSymbol + "\t" + countInter + "\t" + countPacked + "\t" + countPara + "\t" + countTerm + "\t" + countEps + "\t"
          + xS.size() + "\t" + xP.size() + "\t" + cbD.size());
    }

    cycleBreakStartTime = System.nanoTime();
    if (xS.size() + xP.size() < 10000)
      newCycleBreak();
    else
      System.out.println("Crowbarred");// Crowbar away large examples
    cycleBreakEndTime = System.nanoTime();

    if (statistics) {
      countSymbol = countInter = countPacked = countPara = countTerm = countEps = 0;
      sppf.visited.clear();
      countRemove = true;
      sppfBreakCyclesCountsRec(sppf.rootNode);
      System.out.println("," + countSymbol + "," + countInter + "," + countPacked + "," + countPara + "," + countTerm + "," + countEps + "," + xS.size() + ","
          + xP.size() + "," + cbD.size() + "," + Util.timeAsMilliseconds(cycleBreakStartTime, cycleBreakEndTime));
    }

    if (counts) {
      countSymbol = countInter = countPacked = countPara = countTerm = countEps = 0;
      sppf.visited.clear();
      countRemove = true;
      sppfBreakCyclesCountsRec(sppf.rootNode);
      System.out.println("After\t" + countSymbol + "\t" + countInter + "\t" + countPacked + "\t" + countPara + "\t" + countTerm + "\t" + countEps + "\t"
          + xS.size() + "\t" + xP.size() + "\t" + cbD.size());
      System.out.println("Cycle break time " + Util.timeAsMilliseconds(cycleBreakStartTime, cycleBreakEndTime) + "ms");
      System.out.println("Nodes made unreachable by cycle breaking");
      if (countReachable.size() == 0)
        System.out.println("--None--");
      else
        for (var n : countReachable) {
          if (n instanceof SPPFSymbolNode) {
            SPPFSymbolNode nn = (SPPFSymbolNode) n;

            if (nn.li != nn.ri) System.out.print("*");
          }
          System.out.println(n);
        }
    }

  }

  private void updateCountReachable(AbstractSPPFNode n) {
    if (countRemove)
      countReachable.remove(n);
    else
      countReachable.add(n);
  }

  private void sppfBreakCyclesCountsRec(SPPFSymbolNode sppfn) {
    // System.out.println("\nEntered sppfBreakCyclesCountsRec() at node " + node);
    if (sppf.visited.get(sppfn.number)) return;
    sppf.visited.set(sppfn.number);
    updateCountReachable(sppfn);

    if (sppfn.gn.element.kind == CFGKind.EPS)
      countEps++;
    else if (sppfn.packNodes.size() == 0)
      countTerm++;
    else if (sppf.cfgRules.paraterminalElements.contains(sppfn.gn.element)) {
      countPara++;
      return;
    } else if (sppfn.isSymbol())
      countSymbol++;
    else
      countInter++;

    for (SPPFPackedNode p : sppfn.packNodes) { // Recurse through packed nodes
      if (cbD.contains(p)) continue;
      updateCountReachable(p);
      countPacked++;
      if (p.leftChild != null) sppfBreakCyclesCountsRec(p.leftChild);
      if (p.rightChild != null) sppfBreakCyclesCountsRec(p.rightChild);
    }
  }

  private void newCycleBreak() {
    boolean changedXpI, changedXpO = true;
    Stack<SPPFSymbolNode> y1 = new Stack<>();
    Stack<SPPFPackedNode> y2 = new Stack<>();
    SPPFPackedNode v = null;

    while (changedXpO) {
      changedXpO = false;
      for (var vp : xP) {
        if (somePackedSiblingNotInXp(vp, xP)) {
          v = vp;
          cbD.add(vp);
          changedXpO = true;
          break;
        }
      }

      if (changedXpO) {
        xP.remove(v);
        changedXpI = true;
        while (changedXpI) {
          changedXpI = false;
          for (var vs : xS)
            if (noPackedChildInXp(vs, xP)) y1.push(vs);
          while (!y1.empty())
            xS.remove(y1.pop());

          for (var vip : xP) {
            if (noSymbolChildInXs(vip, xS)) {
              y2.push(vip);
              changedXpI = true;
            }
          }

          while (!y2.empty())
            xP.remove(y2.pop());
        }
      }
    }
  }

  class Configuration {
    final Set<SPPFPackedNode> xP1;
    final Set<SPPFSymbolNode> xS1;
    final Set<SPPFPackedNode> d;
    final Set<SPPFPackedNode> dPrime;

    public Configuration(Set<SPPFPackedNode> xP, Set<SPPFSymbolNode> xS, Set<SPPFPackedNode> d, Set<SPPFPackedNode> dPrime) {
      super();
      this.xP1 = xP;
      this.xS1 = xS;
      this.d = d;
      this.dPrime = dPrime;
    }

    public Configuration(Configuration c) {
      xP1 = new HashSet<>(c.xP1);
      xS1 = new HashSet<>(c.xS1);
      d = new HashSet<>(c.d);
      dPrime = new HashSet<>(c.dPrime);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((d == null) ? 0 : d.hashCode());
      result = prime * result + ((dPrime == null) ? 0 : dPrime.hashCode());
      result = prime * result + ((xP1 == null) ? 0 : xP1.hashCode());
      result = prime * result + ((xS1 == null) ? 0 : xS1.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Configuration other = (Configuration) obj;
      if (d == null) {
        if (other.d != null) return false;
      } else if (!d.equals(other.d)) return false;
      if (dPrime == null) {
        if (other.dPrime != null) return false;
      } else if (!dPrime.equals(other.dPrime)) return false;
      if (xP1 == null) {
        if (other.xP1 != null) return false;
      } else if (!xP1.equals(other.xP1)) return false;
      if (xS1 == null) {
        if (other.xS1 != null) return false;
      } else if (!xS1.equals(other.xS1)) return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("< xP:");
      builder.append(xP1);
      // builder.append("\n");
      builder.append(" xS:");
      builder.append(xS1);
      // builder.append("\n");
      builder.append(" d:");
      builder.append(d);
      // builder.append("\n");
      builder.append(" dP':");
      builder.append(dPrime);
      builder.append(">");
      return builder.toString();
    }
  }

  Deque<Configuration> q = new LinkedList<>();
  Set<Configuration> queued = new HashSet<>();
  Relation<Configuration, Configuration> breakRelation = new Relation<>();
  // PrintStream dotOut = null;

  boolean breakCyclesRelationTrace = false;

  public void sppfBreakCyclesRelation() {
    Configuration c, cp;
    sppfComputeCoreReachability(null); // computes sppfCyclic (set of cyclic SPPF nodes)
    loadXPartitionsFromReachability(); // Load X from computed cyclic nodes as partitions xP and xS

    Configuration c_0 = new Configuration(xP, xS, new HashSet<SPPFPackedNode>(), new HashSet<SPPFPackedNode>());
    q.add(c_0); // enqueue start element
    System.out.println("C_0: " + c_0);

    while (q.size() != 0) {
      c = q.removeFirst(); // deqeue c
      if (breakCyclesRelationTrace) System.out.println("** Processing: " + c);

      for (var v : c.xS1) {
        if (breakCyclesRelationTrace) System.out.println("Checking symbol node: " + v);
        if (noPackedChildInXp(v, c.xP1)) {
          zcbUpdate(c, v, false, false);
        }
      }

      for (var v : c.xP1) {
        if (breakCyclesRelationTrace) System.out.println("Checking packed node: " + v);
        if (!noSymbolChildInXs(v, c.xS1) && somePackedSiblingNotInXp(v, c.xP1)) {
          zcbUpdate(c, v, true, false);
        } else if (somePackedSiblingNotInXp(v, c.xP1)) {
          zcbUpdate(c, v, false, true);
        }
      }
    }

    System.out.println("Relation has " + breakRelation.getDomain().size() + " nodes with terminals");
    for (var de : breakRelation.getDomain()) {
      // System.out.println(de + "->" + r.get(de));
      if (breakRelation.get(de).size() == 0) System.out.println(de);
    }
    System.out.println("Relation is:\n" + breakRelation);

    // System.out.println("Queued");
    // for (var v : queued)
    // System.out.println(v);

    // Output relation as .dot file
    try {
      PrintStream dotOut = new PrintStream(new File("sppfCycleBreakRelation.dot"));
      dotOut.println("digraph \"SPPF\" {\n" + "rankdir=\"LR\" "
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
      for (var de : breakRelation.getDomain()) {
        for (var cde : breakRelation.get(de))
          dotOut.println("\"" + de + "\"->\"" + cde + "\"");
        dotOut.println();
      }
      dotOut.println("}");
      dotOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to sppfCycleBreakRelation.dot");
    }

  }

  private void zcbUpdate(Configuration c, AbstractSPPFNode v, Boolean D, Boolean DPrime) {
    var cp = new Configuration(c);
    if (v instanceof SPPFPackedNode)
      cp.xP1.remove(v);
    else
      cp.xS1.remove(v);
    if (D) cp.d.add((SPPFPackedNode) v);
    if (DPrime) cp.dPrime.add((SPPFPackedNode) v);
    newState(c, cp);

  }

  public void newState(Configuration c, Configuration cp) {
    if (c.equals(cp)) {
      System.out.println("\n!Bang!");
    }
    breakRelation.add(c, cp);
    breakRelation.add(cp);
    if (breakCyclesRelationTrace) System.out.println("Added (" + c + ", " + cp + ")");
    if (!queued.contains(cp)) {
      q.add(cp);
      queued.add(cp);
      if (breakCyclesRelationTrace) System.out.println("Queued" + cp);
    }
  }

  /** lexicalisation checks *********************************************************************/
  Map<Integer, Set<SPPFSymbolNode>> paraterminalInstances = new TreeMap<>();
  int paraterminalCount;
  int terminalCount;

  public void sppfPrintParaterminals() {
    if (sppf == null || sppf.rootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping paraterminal instance printing");
      return;
    }
    System.out.println("Paraterminals");
    sppf.visited.clear();
    paraterminalCount = terminalCount = 0;
    sppfCollectParaterminalsRec(sppf.rootNode);
    System.out.println("!!! Excluding epsilon leaves, there are " + paraterminalCount + " paraterminal instances and " + terminalCount
        + " terminal instances reachable from the SPPF root");
    System.out.println("!!! During paraterminal collection " + sppf.visited.cardinality() + " SPPFnodes were visited");
    int rightmostIndex = 0;
    boolean overlapping = false;
    for (var i : paraterminalInstances.keySet()) {
      for (var s : paraterminalInstances.get(i))
        System.out.println(s.li + "," + s.ri + "  " + s.gn.element);
    }
  }

  private void sppfCollectParaterminalsRec(SPPFSymbolNode sppfn) {
    // System.out.println("Collect paraterminals at " + sppfn);
    if (sppf.visited.get(sppfn.number)) return;
    sppf.visited.set(sppfn.number);

    if (sppfn.isSymbol() && sppf.cfgRules.paraterminalElements.contains(sppfn.gn.element)) {
      paraterminalInstanceAdd(sppfn);
      paraterminalCount++;
      return;
    }
    if (sppfn.packNodes.size() == 0) {
      paraterminalInstanceAdd(sppfn);
      if (sppfn.gn.element.kind != CFGKind.EPS) terminalCount++;
    }
    for (var p : sppfn.packNodes) {
      if (p.leftChild != null) sppfCollectParaterminalsRec(p.leftChild);
      sppfCollectParaterminalsRec(p.rightChild);
    }
  }

  private void paraterminalInstanceAdd(SPPFSymbolNode sppfn) {
    if (paraterminalInstances.get(sppfn.li) == null) paraterminalInstances.put(sppfn.li, new HashSet<SPPFSymbolNode>());
    paraterminalInstances.get(sppfn.li).add(sppfn);
  }

  SPPFSymbolNode[] parasentence;
  Set<List<SPPFSymbolNode>> parasentences;

  public void sppfPrintParasentences() {
    if (sppf == null || sppf.rootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping parasentence printing");
      return;
    }
    System.out.println("Parasentences");
    sppf.visited.clear();
    parasentence = new SPPFSymbolNode[100 * sppf.tokens.length + 1];
    psCall = 0;
    parasentences = new HashSet<>();
    // sppfCollectParasentencesRec(sppf.rootNode, 0);
    // sppfCollectParasentencesIter(sppf.rootNode, 0);
    // System.out.println(parasentences);
    for (var s : parasentences) {
      for (var n : s)
        System.out.print(n.li + "," + n.ri + ":" + n.gn.element.str + "  ");
      System.out.println();
    }
  }

  /*
   * Iterative explorationofderivations
   *
   * If packedNode.size == 1 then just descend
   *
   */
  private int psCall;

  private int sppfCollectParasentencesRec(SPPFSymbolNode node, int parasentenceIndex) {
    int thisCall = ++psCall;
    System.out.println(thisCall + " collect parasentences at " + node + " with parasentenceIndex " + parasentenceIndex);
    int entrySentenceIndex = parasentenceIndex;
    if (sppf.visited.get(node.number)) {
      System.out.println("Already called; abort");
      return parasentenceIndex;
    }
    sppf.visited.set(node.number);

    if (node.packNodes.isEmpty() || (node.isSymbol() && sppf.cfgRules.paraterminalElements.contains(node.gn.element))) {
      if (!(node.packNodes.isEmpty() && node.gn.element.kind == CFGKind.EPS)) {
        System.out.println("Extending with " + node.gn.element.str);
        parasentence[parasentenceIndex++] = node;
        if (node.ri == sppf.tokens.length - 1) addParasentence(parasentenceIndex);
      }
    } else
      for (var p : node.packNodes) {
        if (cbD.contains(p)) {
          System.out.println("Skipping cyclic node " + p.number);
          continue;
        }
        parasentenceIndex = entrySentenceIndex;
        if (p.leftChild != null) {
          System.out.println("Calling left child of " + p + " under " + node + " with parasentenceIndex " + parasentenceIndex);
          parasentenceIndex = sppfCollectParasentencesRec(p.leftChild, parasentenceIndex);
        }
        System.out.println("Calling right child of " + p + " under " + node + " with parasentenceIndex " + parasentenceIndex);
        parasentenceIndex = sppfCollectParasentencesRec(p.rightChild, parasentenceIndex);
      }
    sppf.visited.clear(node.number); // We are enumerating all traversals!
    System.out.println("Return from call " + thisCall);
    return parasentenceIndex;
  }

  private void addParasentence(int length) {
    System.out.println("Adding sentence of length " + length + parasentence);
    List<SPPFSymbolNode> parasentenceList = new LinkedList<>();
    for (int i = 0; i < length; i++)
      parasentenceList.add(parasentence[i]);
    parasentences.add(parasentenceList);
  }

}
