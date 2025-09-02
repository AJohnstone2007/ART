package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.util.Util;

class SPPF2Dot {
  private final String rootNodeStyle = "[style=filled fillcolor=lightgreen]";
  private final String packNodeStyle = "[style=rounded]";
  private final String ambiguousStyle = "[color=orange]";
  private final String cycleStyle = "[color=red]";
  private final String intermediateNodeStyle = "[style=filled fillcolor=grey92]";
  private final String symbolNodeStyle = "";
  private final String unreachableSymbolNodeStyle = "[style=filled fillcolor=lightyellow]";
  private final String unreachablePackNodeStyle = "[style=\"filled,rounded\" fillcolor=lightyellow]";
  private final String deletedPackNodeStyle = "[style=\"filled,rounded\" fillcolor=cornflowerblue]";
  private final String deletedPrimePackNodeStyle = "[style=\"filled,rounded\" fillcolor=pink]";
  private final String deletedAndDeletedPrimePackNodeStyle = "[style=\"filled,rounded\" fillcolor=mediumpurple]";
  private final String suppressedPackNodeStyle = "[style=\"filled,rounded\" fillcolor=pink]";
  private PrintStream outputStream = null;
  private boolean indexed;
  private boolean showIntermediates;
  private final SPPF sppf;

  public SPPF2Dot(SPPF sppf, PrintStream outputStream, boolean indexed, boolean full, boolean showIntermediates) {
    this.sppf = sppf;
    this.outputStream = outputStream;
    this.indexed = indexed;
    this.showIntermediates = showIntermediates;

    outputStream.println("digraph \"SPPF\" {\n"
        + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
    if (full)
      for (SPPFSymbolNode n : sppf.nodes.keySet())
        sppfSubtreeToDot(n);
    else {
      if (sppf.root == null) {
        Util.error("SPPF for rejected string can only be visualused in full");
        return;
      }
      sppf.visited.clear();
      coreSPPFToDotRec(sppf.root);
    }
    outputStream.println("}");
    outputStream.close();
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
  // Util.info("Unable to write SPPF visualisation to " + filename);
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
      outputStream.println("\"" + sppfn.number + "\"" + symbolNodeStyle + " [label=\"" + sppfn.number + " " + sppfn.grammarNode.toString() + " "
          + sppfn.leftExtent + ", " + sppfn.rightExtent + "\"]");
    else
      outputStream.println("\"" + sppfn.number + "\"" + intermediateNodeStyle + " [label=\"" + sppfn.number + " " + sppfn.grammarNode.toStringAsProduction()
          + " " + sppfn.leftExtent + ", " + sppfn.rightExtent + "\"]");

    if (isAmbiguous) outputStream.println(ambiguousStyle);
    if (sppf.cyclic.get(sppfn.number)) outputStream.println(cycleStyle);
    if (!sppf.rootReachable.get(sppfn.number)) outputStream.println(unreachableSymbolNodeStyle);
    if (sppfn == sppf.root) outputStream.println(rootNodeStyle);

    for (SPPFPackedNode p : sppfn.packNodes) {
      boolean isCyclicP = sppf.cyclic.get(p.number);

      outputStream.println("\"" + p.number + "\"" + packNodeStyle + " [label=\"" + (p.suppressed ? "*" : "") + p.number + ": "
          + p.grammarNode.toStringAsProduction() + " , " + p.pivot + "\"]");
      if (isCyclicP) outputStream.println(cycleStyle);
      if (!sppf.rootReachable.get(p.number)) outputStream.println(unreachablePackNodeStyle);
      if (p.suppressed) outputStream.println(suppressedPackNodeStyle);

      if (sppf.cbD.contains(p) && sppf.cbDPrime.contains(p))
        outputStream.println(deletedAndDeletedPrimePackNodeStyle);
      else if (sppf.cbD.contains(p))
        outputStream.println(deletedPackNodeStyle);
      else if (sppf.cbDPrime.contains(p)) outputStream.println(deletedPrimePackNodeStyle);
      outputStream.println("\"" + sppfn.number + "\"" + "->" + "\"" + p.number + "\"");

      if (isCyclicP)
        outputStream.println(cycleStyle);
      else if (isAmbiguous) outputStream.println(ambiguousStyle);

      if (p.leftChild != null) {
        outputStream.println("\"" + p.number + "\"" + "->" + "\"" + p.leftChild.number + "\"");
        if (isCyclicP && sppf.cyclic.get(p.leftChild.number)) outputStream.println(cycleStyle);
      }

      outputStream.println("\"" + p.number + "\"" + "->" + "\"" + p.rightChild.number + "\"");
      if (isCyclicP && sppf.cyclic.get(p.rightChild.number)) outputStream.println(cycleStyle);
    }
  }
}
