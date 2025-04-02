package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.util.Util;

class SPPF2Dot {
  final String rootNodeStyle = "[style=filled fillcolor=lightgreen]";
  final String packNodeStyle = "[style=rounded]";
  final String ambiguousStyle = "[color=orange]";
  final String cycleStyle = "[color=red]";
  final String intermediateNodeStyle = "[style=filled fillcolor=grey92]";
  final String symbolNodeStyle = "";
  final String unreachableSymbolNodeStyle = "[style=filled fillcolor=lightyellow]";
  final String unreachablePackNodeStyle = "[style=\"filled,rounded\" fillcolor=lightyellow]";
  final String deletedPackNodeStyle = "[style=\"filled,rounded\" fillcolor=cornflowerblue]";
  final String deletedPrimePackNodeStyle = "[style=\"filled,rounded\" fillcolor=pink]";
  final String deletedAndDeletedPrimePackNodeStyle = "[style=\"filled,rounded\" fillcolor=mediumpurple]";
  PrintStream dotOut = null;
  private boolean showIndicies;
  private boolean showIntermediates;
  private final SPPF sppf;

  public SPPF2Dot(SPPF sppf) {
    this.sppf = sppf;
    if (sppf == null || sppf.rootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping SPPF visualisation");
      return;
    }
    sppf.computeCoreReachability(null);

    sppf2Dot("sppf_full.dot", true, true, false); // full SPPF
    sppf2Dot("sppf_core.dot", false, true, true); // core SPPF - only nodes reachable from (S,0,n)
  }

  // public void derivation2Dot() {
  // if (sppf == null || sppfRootNode == null) {
  // Util.warning("Current parser does not have a current SPPF - skipping derivation visualisation");
  // return;
  // }
  // derivation2Dot(sppf, sppfRootNode, "derivation.dot", false, false); // full SPPF
  // }
  //
  // public void derivation2Dot(Map<SPPFN, SPPFN> sppf, SPPFN rootNode, String filename, boolean showNodeNumbers, boolean showIndices) {
  //
  // try {
  // dotOut = new PrintStream(new File(filename));
  // dotOut.println("digraph \"Derivation\" {\n"
  // + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3
  // color=gray]");
  // sppf.visited.clear();
  // derivationToDotRec(rootNode, 0, showNodeNumbers, showIndices);
  // dotOut.println("}");
  // dotOut.close();
  // } catch (FileNotFoundException e) {
  // System.out.println("Unable to write SPPF visualisation to " + filename);
  // }
  // }
  //
  // int nextFreeNodeNumber = 0;
  //
  // private void derivationToDotRec(SPPFN sppfn, int parent, boolean showNodeNumbers, boolean showIndices) {
  // if (sppf.visited.get(sppfn.number)) return;
  // sppf.visited.set(sppfn.number);
  //
  // int nodeNumber = parent;
  //
  // if (sppfn.isSymbol()) {
  // nodeNumber = ++nextFreeNodeNumber;
  // dotOut.println("\"" + nodeNumber + "\"" + symbolNodeStyle + " [label=\"" + (showNodeNumbers ? "" + nodeNumber : "") + " " + sppfn.gn.toString() + " "
  // + (showIndices ? sppfn.li + ", " + sppfn.ri : "") + "\"]");
  //
  // if (parent != 0) dotOut.println("\"" + parent + "\"" + "->" + "\"" + nodeNumber + "\"");
  // }
  //
  // // Recurse to an unsuppressed packed node
  // for (SPPFPN p : sppfn.packNS) {
  // if (!p.suppressed) {
  // if (p.leftChild != null) derivationToDotRec(p.leftChild, nodeNumber, showNodeNumbers, showIndices);
  // derivationToDotRec(p.rightChild, nodeNumber, showNodeNumbers, showIndices);
  // break;
  // }
  // }
  // sppf.visited.clear(sppfn.number);
  // }

  public void sppf2Dot(String filename, boolean full, boolean showIndicies, boolean showIntermediates) {
    this.showIndicies = showIndicies;
    this.showIntermediates = showIntermediates;

    try {
      dotOut = new PrintStream(new File(filename));
      dotOut.println("digraph \"SPPF\" {\n"
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
      if (full)
        for (SPPFSymbolNode n : sppf.nodes.keySet())
          sppfSubtreeToDot(n);
      else {
        sppf.visited.clear();
        coreSPPFToDotRec(sppf.rootNode);
      }
      dotOut.println("}");
      dotOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to " + filename);
    }
  }

  private void coreSPPFToDotRec(SPPFSymbolNode sppfn) {
    if (sppf.visited.get(sppfn.number)) return;
    sppf.visited.set(sppfn.number);

    sppfSubtreeToDot(sppfn);

    for (SPPFPackedNode p : sppfn.packNodes) { // Recurse through packed nodes
      if (p.leftChild != null) coreSPPFToDotRec(p.leftChild);
      if (p.rightChild != null) coreSPPFToDotRec(p.rightChild);
    }
  }

  private void sppfSubtreeToDot(SPPFSymbolNode sppfn) {
    boolean isAmbiguous = sppfn.packNodes.size() > 1;
    if (sppfn.isSymbol())
      dotOut.println(
          "\"" + sppfn.number + "\"" + symbolNodeStyle + " [label=\"" + sppfn.number + " " + sppfn.gn.toString() + " " + sppfn.li + ", " + sppfn.ri + "\"]");
    else
      dotOut.println("\"" + sppfn.number + "\"" + intermediateNodeStyle + " [label=\"" + sppfn.number + " " + sppfn.gn.toStringAsProduction() + " " + sppfn.li
          + ", " + sppfn.ri + "\"]");

    if (isAmbiguous) dotOut.println(ambiguousStyle);
    if (sppf.cyclic.get(sppfn.number)) dotOut.println(cycleStyle);
    if (!sppf.rootReachable.get(sppfn.number)) dotOut.println(unreachableSymbolNodeStyle);
    if (sppfn == sppf.rootNode) dotOut.println(rootNodeStyle);

    for (SPPFPackedNode p : sppfn.packNodes) {
      boolean isCyclicP = sppf.cyclic.get(p.number);

      dotOut.println("\"" + p.number + "\"" + packNodeStyle + " [label=\"" + p.number + ": " + p.grammarNode.toStringAsProduction() + " , " + p.pivot + "\"]");
      if (isCyclicP) dotOut.println(cycleStyle);
      if (!sppf.rootReachable.get(p.number)) dotOut.println(unreachablePackNodeStyle);

      if (sppf.cbD.contains(p) && sppf.cbDPrime.contains(p))
        dotOut.println(deletedAndDeletedPrimePackNodeStyle);
      else if (sppf.cbD.contains(p))
        dotOut.println(deletedPackNodeStyle);
      else if (sppf.cbDPrime.contains(p)) dotOut.println(deletedPrimePackNodeStyle);
      dotOut.println("\"" + sppfn.number + "\"" + "->" + "\"" + p.number + "\"");

      if (isCyclicP)
        dotOut.println(cycleStyle);
      else if (isAmbiguous) dotOut.println(ambiguousStyle);

      if (p.leftChild != null) {
        dotOut.println("\"" + p.number + "\"" + "->" + "\"" + p.leftChild.number + "\"");
        if (isCyclicP && sppf.cyclic.get(p.leftChild.number)) dotOut.println(cycleStyle);
      }

      dotOut.println("\"" + p.number + "\"" + "->" + "\"" + p.rightChild.number + "\"");
      if (isCyclicP && sppf.cyclic.get(p.rightChild.number)) dotOut.println(cycleStyle);
    }
  }
}
