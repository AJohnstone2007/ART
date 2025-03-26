package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.sppf.SPPF;
import uk.ac.rhul.cs.csle.art.util.sppf.SPPFN;

class SPPFSentenceAnalyser {
  final SPPF sppf;
  Map<Integer, Set<SPPFN>> paraterminalInstances = new TreeMap<>();
  int paraterminalCount;
  int terminalCount;

  SPPFSentenceAnalyser(SPPF sppf) {
    this.sppf = sppf;
  }

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
        System.out.println(s.li + "," + s.ri + "  " + s.gn.elm);
    }
  }

  private void sppfCollectParaterminalsRec(SPPFN sppfn) {
    // System.out.println("Collect paraterminals at " + sppfn);
    if (sppf.visited.get(sppfn.number)) return;
    sppf.visited.set(sppfn.number);

    if (sppfn.isSymbol() && sppf.cfgRules.paraterminalElements.contains(sppfn.gn.elm)) {
      paraterminalInstanceAdd(sppfn);
      paraterminalCount++;
      return;
    }
    if (sppfn.packNS.size() == 0) {
      paraterminalInstanceAdd(sppfn);
      if (sppfn.gn.elm.kind != CFGKind.EPS) terminalCount++;
    }
    for (var p : sppfn.packNS) {
      if (p.leftChild != null) sppfCollectParaterminalsRec(p.leftChild);
      sppfCollectParaterminalsRec(p.rightChild);
    }
  }

  private void paraterminalInstanceAdd(SPPFN sppfn) {
    if (paraterminalInstances.get(sppfn.li) == null) paraterminalInstances.put(sppfn.li, new HashSet<SPPFN>());
    paraterminalInstances.get(sppfn.li).add(sppfn);
  }

  SPPFN[] parasentence;
  Set<List<SPPFN>> parasentences;

  public void sppfPrintParasentences() {
    if (sppf == null || sppf.rootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping parasentence printing");
      return;
    }
    System.out.println("Parasentences");
    sppf.visited.clear();
    parasentence = new SPPFN[100 * sppf.tokens.length + 1];
    psCall = 0;
    parasentences = new HashSet<>();
    // sppfCollectParasentencesRec(sppf.rootNode, 0);
    sppfCollectParasentencesIter(sppf.rootNode, 0);
    // System.out.println(parasentences);
    for (var s : parasentences) {
      for (var n : s)
        System.out.print(n.li + "," + n.ri + ":" + n.gn.elm.str + "  ");
      System.out.println();
    }
  }

  private void sppfCollectParasentencesIter(SPPFN rootNode, int i) {
    // TODO Auto-generated method stub

  }

  /*
   * Iterative explorationofderivations
   *
   * If packedNode.size == 1 then just descend
   *
   */
  private int psCall;

  private int sppfCollectParasentencesRec(SPPFN node, int parasentenceIndex) {
    int thisCall = ++psCall;
    System.out.println(thisCall + " collect parasentences at " + node + " with parasentenceIndex " + parasentenceIndex);
    int entrySentenceIndex = parasentenceIndex;
    if (sppf.visited.get(node.number)) {
      System.out.println("Already called; abort");
      return parasentenceIndex;
    }
    sppf.visited.set(node.number);

    if (node.packNS.isEmpty() || (node.isSymbol() && sppf.cfgRules.paraterminalElements.contains(node.gn.elm))) {
      if (!(node.packNS.isEmpty() && node.gn.elm.kind == CFGKind.EPS)) {
        System.out.println("Extending with " + node.gn.elm.str);
        parasentence[parasentenceIndex++] = node;
        if (node.ri == sppf.tokens.length - 1) addParasentence(parasentenceIndex);
      }
    } else
      for (var p : node.packNS) {
        if (sppf.cbD.contains(p)) {
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
    List<SPPFN> parasentenceList = new LinkedList<>();
    for (int i = 0; i < length; i++)
      parasentenceList.add(parasentence[i]);
    parasentences.add(parasentenceList);
  }
}