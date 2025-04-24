package uk.ac.rhul.cs.csle.art.util.derivations;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.util.Util;

class SPPFSentenceAnalyser {
  final SPPF sppf;
  Map<Integer, Set<SPPFSymbolNode>> paraterminalInstances = new TreeMap<>();
  int paraterminalCount;
  int terminalCount;

  SPPFSentenceAnalyser(SPPF sppf) {
    this.sppf = sppf;
  }

  public void sppfPrintParaterminals() {
    if (sppf == null || sppf.root == null) {
      Util.warning("Current parser does not have a current SPPF - skipping paraterminal instance printing");
      return;
    }
    Util.info("Paraterminals");
    sppf.visited.clear();
    paraterminalCount = terminalCount = 0;
    sppfCollectParaterminalsRec(sppf.root);
    Util.info("!!! Excluding epsilon leaves, there are " + paraterminalCount + " paraterminal instances and " + terminalCount
        + " terminal instances reachable from the SPPF root");
    Util.info("!!! During paraterminal collection " + sppf.visited.cardinality() + " SPPFnodes were visited");
    int rightmostIndex = 0;
    boolean overlapping = false;
    for (var i : paraterminalInstances.keySet()) {
      for (var s : paraterminalInstances.get(i))
        Util.info(s.leftExtent + "," + s.rightExtent + "  " + s.grammarNode.element);
    }
  }

  private void sppfCollectParaterminalsRec(SPPFSymbolNode sppfn) {
    // Util.info("Collect paraterminals at " + sppfn);
    if (sppf.visited.get(sppfn.number)) return;
    sppf.visited.set(sppfn.number);

    if (sppfn.isSymbol() && sppf.parser.cfgRules.paraterminalElements.contains(sppfn.grammarNode.element)) {
      paraterminalInstanceAdd(sppfn);
      paraterminalCount++;
      return;
    }
    if (sppfn.packNodes.size() == 0) {
      paraterminalInstanceAdd(sppfn);
      if (sppfn.grammarNode.element.cfgKind != CFGKind.EPS) terminalCount++;
    }
    for (var p : sppfn.packNodes) {
      if (p.leftChild != null) sppfCollectParaterminalsRec(p.leftChild);
      sppfCollectParaterminalsRec(p.rightChild);
    }
  }

  private void paraterminalInstanceAdd(SPPFSymbolNode sppfn) {
    if (paraterminalInstances.get(sppfn.leftExtent) == null) paraterminalInstances.put(sppfn.leftExtent, new HashSet<SPPFSymbolNode>());
    paraterminalInstances.get(sppfn.leftExtent).add(sppfn);
  }

  SPPFSymbolNode[] parasentence;
  Set<List<SPPFSymbolNode>> parasentences;

  public void sppfPrintParasentences() {
    if (sppf == null || sppf.root == null) {
      Util.warning("Current parser does not have a current SPPF - skipping parasentence printing");
      return;
    }
    Util.info("Parasentences");
    sppf.visited.clear();
    parasentence = new SPPFSymbolNode[100 * sppf.parser.lexer.firstLexicalisation.size() + 1];
    psCall = 0;
    parasentences = new HashSet<>();
    // sppfCollectParasentencesRec(sppf.root, 0);
    sppfCollectParasentencesIter(sppf.root, 0);
    // Util.info(parasentences);
    for (var s : parasentences) {
      for (var n : s)
        System.out.print(n.leftExtent + "," + n.rightExtent + ":" + n.grammarNode.element.str + "  ");
      System.out.println();
    }
  }

  private void sppfCollectParasentencesIter(SPPFSymbolNode rootNode, int i) {
    // TODO Auto-generated method stub

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
    Util.info(thisCall + " collect parasentences at " + node + " with parasentenceIndex " + parasentenceIndex);
    int entrySentenceIndex = parasentenceIndex;
    if (sppf.visited.get(node.number)) {
      Util.info("Already called; abort");
      return parasentenceIndex;
    }
    sppf.visited.set(node.number);

    if (node.packNodes.isEmpty() || (node.isSymbol() && sppf.parser.cfgRules.paraterminalElements.contains(node.grammarNode.element))) {
      if (!(node.packNodes.isEmpty() && node.grammarNode.element.cfgKind == CFGKind.EPS)) {
        Util.info("Extending with " + node.grammarNode.element.str);
        parasentence[parasentenceIndex++] = node;
        if (node.rightExtent == sppf.parser.lexer.firstLexicalisation.size() - 1) addParasentence(parasentenceIndex);
      }
    } else
      for (var p : node.packNodes) {
        if (sppf.cbD.contains(p)) {
          Util.info("Skipping cyclic node " + p.number);
          continue;
        }
        parasentenceIndex = entrySentenceIndex;
        if (p.leftChild != null) {
          Util.info("Calling left child of " + p + " under " + node + " with parasentenceIndex " + parasentenceIndex);
          parasentenceIndex = sppfCollectParasentencesRec(p.leftChild, parasentenceIndex);
        }
        Util.info("Calling right child of " + p + " under " + node + " with parasentenceIndex " + parasentenceIndex);
        parasentenceIndex = sppfCollectParasentencesRec(p.rightChild, parasentenceIndex);
      }
    sppf.visited.clear(node.number); // We are enumerating all traversals!
    Util.info("Return from call " + thisCall);
    return parasentenceIndex;
  }

  private void addParasentence(int length) {
    Util.info("Adding sentence of length " + length + parasentence);
    List<SPPFSymbolNode> parasentenceList = new LinkedList<>();
    for (int i = 0; i < length; i++)
      parasentenceList.add(parasentence[i]);
    parasentences.add(parasentenceList);
  }
}