package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexemeKind;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractSPPFNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFPackedNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;
import uk.ac.rhul.cs.csle.art.util.process.Descriptor;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOverNaturals;
import uk.ac.rhul.cs.csle.art.util.stacks.GSS2Dot;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSEdge;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSNode;

public class GLLBaseLine extends AbstractParser {
  private final BitSet visitedSPPFNodes = new BitSet(), suppressedSPPFNode = new BitSet();

  /* Temporary disambiguation before choosers are implemented ****************/
  @Override
  public void chooseLongestMatch() {
    visitedSPPFNodes.clear();
    chooseLongestMatchRec(sppfRootNode);
  }

  private void chooseLongestMatchRec(SPPFSymbolNode sn) {
    if (visitedSPPFNodes.get(sn.number)) return;
    visitedSPPFNodes.set(sn.number);

    int rightMostPivot = -1;
    SPPFPackedNode candidate = null;
    if (sn.packNodes.size() > 1) {
      Util.warning("Ambiguity detected at SPPF node " + sn.number + ": " + sn.gn.toStringAsProduction() + " involving ");
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

  /* Parser ******************************************************************/
  public void parse(AbstractLexer lexer) {
    this.lexer = lexer;
    descS = new HashSet<>();
    descQ = new LinkedList<>();
    sppf = new HashMap<>();
    gss = new HashMap<>();
    gssRoot = new GSSNode(cfgRules.endOfStringNode, 0);
    gss.put(gssRoot, gssRoot);
    tokenIndex = 0;
    sn = gssRoot;
    dn = null;
    for (CFGNode p = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt; p != null; p = p.alt)
      queueDesc(p.seq, tokenIndex, sn, dn);
    inLanguage = false;

    nextDescriptor: while (dequeueDesc())
      while (true) {
        switch (gn.element.kind) {
        case B, T, TI, C:
          if (lexer.tokens[tokenIndex] == gn.element.number) {
            // System.out.println("Matched " + lexer.tokens[i]);
            du(1);
            tokenIndex++;
            gn = gn.seq;
            break;
          } else
            continue nextDescriptor;
        case N:
          call(gn);
          continue nextDescriptor;
        case EPS:
          du(0);
          gn = gn.seq;
          break;
        case END:
          ret();
          continue nextDescriptor;
        case DO:
          gn = gn.alt;
          break;
        case ALT:
          for (CFGNode tmp = gn; tmp != null; tmp = tmp.alt)
            queueDesc(tmp.seq, tokenIndex, sn, dn);
          continue nextDescriptor;
        case EOS:
          break;
        case KLN:
          break;
        case OPT:
          break;
        case POS:
          break;
        default:
          break;
        }
      }
    if (!inLanguage) {
      System.out.println(Util.echo("GLLBL " + "syntax error", lexer.leftIndices[sppfWidestIndex()], lexer.inputString));
      // System.out.println("Widest index: " + sppfWidestIndex());
    } else
      Util.trace(3, 0, "Accept\n");

    loadCounts();
    numberSPPFNodes();
  }

  int sppfCardinality;

  private void numberSPPFNodes() {
    sppfCardinality = 1;
    for (var n : sppf.keySet())
      n.number = sppfCardinality++;
    for (var n : sppf.keySet())
      for (var p : n.packNodes) {
        p.parent = n;
        p.number = sppfCardinality++;
      }

    sppfReachable = new RelationOverNaturals(sppfCardinality, sppfCardinality);
  }

  /* Thread handling *********************************************************/
  Set<Descriptor> descS;
  Deque<Descriptor> descQ;
  CFGNode gn;
  GSSNode sn;
  SPPFSymbolNode dn;

  void queueDesc(CFGNode gn, int i, GSSNode gssN, SPPFSymbolNode sppfN) {
    Descriptor tmp = new Descriptor(gn, i, gssN, sppfN);
    if (descS.add(tmp)) descQ.addFirst(tmp);
  }

  boolean dequeueDesc() {
    Descriptor tmp = descQ.poll();
    if (tmp == null) return false;
    gn = tmp.grammarNode;
    tokenIndex = tmp.inputIndex;
    sn = tmp.stackNode;
    dn = tmp.derivationNode;
    return true;
  }

  /* Deque handling **********************************************************/
  Map<GSSNode, GSSNode> gss;
  GSSNode gssRoot;

  GSSNode gssFind(CFGNode gn, int i) {
    GSSNode gssN = new GSSNode(gn, i);
    if (gss.get(gssN) == null) gss.put(gssN, gssN);
    return gss.get(gssN);
  }

  void call(CFGNode gn) {
    GSSNode gssN = gssFind(gn.seq, tokenIndex);
    GSSEdge gssE = new GSSEdge(sn, dn);
    if (!gssN.edges.contains(gssE)) {
      gssN.edges.add(gssE);
      for (SPPFSymbolNode rc : gssN.pops)
        queueDesc(gn.seq, rc.ri, sn, sppfUpdate(gn.seq, dn, rc));
    }
    for (CFGNode p = cfgRules.elementToNodeMap.get(gn.element).alt; p != null; p = p.alt)
      queueDesc(p.seq, tokenIndex, gssN, null);
  }

  void ret() {
    if (sn.equals(gssRoot)) { // Deque base
      if (cfgRules.acceptingNodeNumbers.contains(gn.num) && (tokenIndex == lexer.tokens.length - 1)) {
        sppfRootNode = sppf.get(new SPPFSymbolNode(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), 0, lexer.tokens.length - 1));
        inLanguage = true;
      }
      return; // End of parse
    }
    sn.pops.add(dn);
    for (GSSEdge e : sn.edges)
      queueDesc(sn.grammarNode, tokenIndex, e.dst, sppfUpdate(sn.grammarNode, e.sppfnode, dn));
  }

  /* Derivation handling *****************************************************/
  Map<SPPFSymbolNode, SPPFSymbolNode> sppf;
  SPPFSymbolNode sppfRootNode;

  SPPFSymbolNode sppfFind(CFGNode dn, int li, int ri) {
    // System.out.print("sppfFind with dn " + dn.toStringAsProduction() + " with extents " + li + "," + ri);

    SPPFSymbolNode tmp = new SPPFSymbolNode(dn, li, ri);
    if (!sppf.containsKey(tmp)) {
      sppf.put(tmp, tmp);
      // System.out.print(" added " + tmp);
    }
    // System.out.println(" resulting sppf\n" + sppf.keySet());
    return sppf.get(tmp);
  }

  SPPFSymbolNode sppfUpdate(CFGNode gn, SPPFSymbolNode ln, SPPFSymbolNode rn) {
    SPPFSymbolNode ret = sppfFind(gn.element.kind == CFGKind.END ? gn.seq : gn, ln == null ? rn.li : ln.li, rn.ri);
    // System.out.println(
    // "Updating SPPF node with gn " + gn.toStringAsProduction() + " and extents " + (ln == null ? rn.li : ln.li) + "," + rn.ri + " retrieves node " + ret);
    ret.packNodes.add(new SPPFPackedNode(gn, ln == null ? rn.li : ln.ri, ln, rn));
    return ret;
  }

  void du(int width) {
    // dn = sppfUpdate(gn.seq, dn, sppfFind(gn, i, i + width)); // SLE paper version
    var gnp = cfgRules.elementToNodeMap.get(gn.element);
    dn = sppfUpdate(gn.seq, dn, sppfFind(gnp, tokenIndex, tokenIndex + width)); // singleton element version to reduce SPPF size and correct for grammars with
                                                                                // cycles
  }

  private int sppfWidestIndex() {
    int ret = 0;
    for (SPPFSymbolNode s : sppf.keySet())
      if (ret < s.ri) ret = s.ri;
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
    if (sppfRootNode == null) return 0;
    visitedSPPFNodes.clear();
    derivationSeenCycle = false;
    LinkedList<Integer> carrier = new LinkedList<>();
    derivationAsTermRec(sppfRootNode, carrier, firstAvailablePackNode(sppfRootNode).grammarNode.seq); // Root packed node must have a grammar node that is the
                                                                                                      // end of a
    // start production
    loadDerivationCounts(derivationNodeCount, derivationAmbiguityNodeCount);
    return carrier.getFirst();
  }

  private String derivationAsTermRec(SPPFSymbolNode sppfn, LinkedList<Integer> childrenFromParent, CFGNode gn) {
    // System.out.println("\nEntered derivationAsTermRec() at node " + sppfn + " instance " + gn);
    if (visitedSPPFNodes.get(sppfn.number)) {
      if (!derivationSeenCycle) Util.error(System.err, "derivationAsTermRec() found cycle in derivation");
      derivationSeenCycle = true;
      return "__CYCLE";
    }

    visitedSPPFNodes.set(sppfn.number);
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
      constructor = firstAvailableSPPFPN == null ? "" + -sppfn.ri : "" + firstAvailableSPPFPN.grammarNode.alt.num;
      else
      constructor = (gn.element.kind == CFGKind.B) ? lexer.lexemeOfBuiltin(LexemeKind.valueOf(gn.element.str), lexer.leftIndices[sppfn.li]) : gn.element.str;

    if (children != childrenFromParent) {
      childrenFromParent.add(cfgRules.iTerms.findTerm(constructor, children));
      derivationNodeCount++;
    }

    visitedSPPFNodes.clear(sppfn.number);
    return (gn.giftKind == GIFTKind.OVER) ? constructor : null;
  }

  private void collectChildNodesRec(SPPFPackedNode sppfpn, LinkedList<SPPFSymbolNode> childNodes) {
    // System.out.println("CollectChildNodesRec() at pack node " + sppfpn);
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

    if (candidate == null) System.out.println("No unsuppressed pack nodes found at SPPF node " + sppfn);

    if (ambiguous) {
      System.out.println("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
      for (SPPFPackedNode p : sppfn.packNodes)
        if (!p.suppressed) System.out.println("  " + p);
    }
    return candidate;
  }

  private boolean ambiguousSPPF;

  public boolean ambiguityCheck() {
    ambiguousSPPF = false;
    if (sppfRootNode != null) ambiguityCheckRec(sppfRootNode);
    return ambiguousSPPF;
  }

  public void ambiguityCheckRec(SPPFSymbolNode sppfn) {
    // if (sppfn.gn.elm.kind != CFGKind.N) return;
    int activePackedNodes = 0;
    for (SPPFPackedNode p : sppfn.packNodes)
      if (!p.suppressed) {
        activePackedNodes++;
        if (p.leftChild != null) ambiguityCheckRec(p.leftChild);
        ambiguityCheckRec(p.rightChild);
      }

    // if (activePackedNodes == 0) System.out.println("No unsuppressed pack nodes found at SPPF node " + sppfn);

    if (activePackedNodes > 1) {
      ambiguousSPPF = true;
      System.out.println("Ambiguous SPPF node " + sppfn.toString() + " involving slots: ");
      for (SPPFPackedNode p : sppfn.packNodes)
        if (!p.suppressed) System.out.println("  " + p);
    }
  }

  private void loadCounts() {
    loadTWECounts(lexer.tokens.length, lexer.tokens.length - 1, 1);

    int gssEdgeCount = 0, popCount = 0;
    for (GSSNode g : gss.keySet()) {
      gssEdgeCount += g.edges.size();
      popCount += g.pops.size();
    }
    loadGSSCounts(descS.size(), gss.keySet().size(), gssEdgeCount, popCount);

    int sppfEpsilonNodeCount = 0, sppfTerminalNodeCount = 0, sppfNonterminalNodeCount = 0, sppfIntermediateNodeCount = 0, sppfPackNodeCount = 0,
        sppfAmbiguityCount = 0, sppfEdgeCount = 0;
    for (SPPFSymbolNode s : sppf.keySet()) {
      switch (s.gn.element.kind) {
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

    loadSPPFCounts(sppfEpsilonNodeCount, sppfTerminalNodeCount, sppfNonterminalNodeCount, sppfIntermediateNodeCount, sppf.keySet().size(), sppfPackNodeCount,
        sppfAmbiguityCount, sppfEdgeCount);
    // loadPoolAllocated(-1);
    // loadHashCounts(-20, -21, -22, -23, -24, -25, -26);
  }

  /* Set element classes **********************************************************************/

  /* GSS and SPPF rendering *******************************************************************/
  @Override
  public void gss2Dot() {
    new GSS2Dot(gss, "gss.dot");
  }

  @Override
  public void sppfPrint() {
    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping SPPF printing");
      return;
    }
    for (var n : sppf.keySet()) {
      System.out.println(n);
      for (var pn : n.packNodes)
        System.out.println(pn);
    }
  }

  // class SPPF2Dot {
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

  @Override
  public void sppf2Dot() {

    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping SPPF visualisation");
      return;
    }
    sppfComputeCoreReachability(null);

    SPPF2Dot(sppf, sppfRootNode, "sppf_full.dot", true, true, false); // full SPPF
    SPPF2Dot(sppf, sppfRootNode, "sppf_core.dot", false, true, true); // core SPPF - only nodes reachable from (S,0,n)
  }

  @Override
  public void derivation2Dot() {
    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping derivation visualisation");
      return;
    }
    derivation2Dot(sppf, sppfRootNode, "derivation.dot", false, false); // full SPPF
  }

  public void derivation2Dot(Map<SPPFSymbolNode, SPPFSymbolNode> sppf, SPPFSymbolNode rootNode, String filename, boolean showNodeNumbers, boolean showIndices) {

    try {
      dotOut = new PrintStream(new File(filename));
      dotOut.println("digraph \"Derivation\" {\n"
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
      visitedSPPFNodes.clear();
      derivationToDotRec(rootNode, 0, showNodeNumbers, showIndices);
      dotOut.println("}");
      dotOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to " + filename);
    }
  }

  int nextFreeNodeNumber = 0;

  private void derivationToDotRec(SPPFSymbolNode sppfn, int parent, boolean showNodeNumbers, boolean showIndices) {
    if (visitedSPPFNodes.get(sppfn.number)) return;
    visitedSPPFNodes.set(sppfn.number);

    int nodeNumber = parent;

    if (sppfn.isSymbol()) {
      nodeNumber = ++nextFreeNodeNumber;
      dotOut.println("\"" + nodeNumber + "\"" + symbolNodeStyle + " [label=\"" + (showNodeNumbers ? "" + nodeNumber : "") + " " + sppfn.gn.toString() + " "
          + (showIndices ? sppfn.li + ", " + sppfn.ri : "") + "\"]");

      if (parent != 0) dotOut.println("\"" + parent + "\"" + "->" + "\"" + nodeNumber + "\"");
    }

    // Recurse to an unsuppressed packed node
    for (SPPFPackedNode p : sppfn.packNodes) {
      if (!p.suppressed) {
        if (p.leftChild != null) derivationToDotRec(p.leftChild, nodeNumber, showNodeNumbers, showIndices);
        derivationToDotRec(p.rightChild, nodeNumber, showNodeNumbers, showIndices);
        break;
      }
    }
    visitedSPPFNodes.clear(sppfn.number);
  }

  public void SPPF2Dot(Map<SPPFSymbolNode, SPPFSymbolNode> sppf, SPPFSymbolNode rootNode, String filename, boolean full, boolean showIndicies,
      boolean showIntermediates) {
    this.showIndicies = showIndicies;
    this.showIntermediates = showIntermediates;

    try {
      dotOut = new PrintStream(new File(filename));
      dotOut.println("digraph \"SPPF\" {\n"
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
      if (full)
        for (SPPFSymbolNode n : sppf.keySet())
          sppfSubtreeToDot(n);
      else {
        visitedSPPFNodes.clear();
        coreSPPFToDotRec(rootNode);
      }
      dotOut.println("}");
      dotOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to " + filename);
    }
  }

  private void coreSPPFToDotRec(SPPFSymbolNode sppfn) {
    if (visitedSPPFNodes.get(sppfn.number)) return;
    visitedSPPFNodes.set(sppfn.number);

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
    if (sppfCyclic.get(sppfn.number)) dotOut.println(cycleStyle);
    if (!sppfRootReachable.get(sppfn.number)) dotOut.println(unreachableSymbolNodeStyle);
    if (sppfn == sppfRootNode) dotOut.println(rootNodeStyle);

    for (SPPFPackedNode p : sppfn.packNodes) {
      boolean isCyclicP = sppfCyclic.get(p.number);

      dotOut.println("\"" + p.number + "\"" + packNodeStyle + " [label=\"" + p.number + ": " + p.grammarNode.toStringAsProduction() + " , " + p.pivot + "\"]");
      if (isCyclicP) dotOut.println(cycleStyle);
      if (!sppfRootReachable.get(p.number)) dotOut.println(unreachablePackNodeStyle);

      if (cbD.contains(p) && cbDPrime.contains(p))
        dotOut.println(deletedAndDeletedPrimePackNodeStyle);
      else if (cbD.contains(p))
        dotOut.println(deletedPackNodeStyle);
      else if (cbDPrime.contains(p)) dotOut.println(deletedPrimePackNodeStyle);
      dotOut.println("\"" + sppfn.number + "\"" + "->" + "\"" + p.number + "\"");

      if (isCyclicP)
        dotOut.println(cycleStyle);
      else if (isAmbiguous) dotOut.println(ambiguousStyle);

      if (p.leftChild != null) {
        dotOut.println("\"" + p.number + "\"" + "->" + "\"" + p.leftChild.number + "\"");
        if (isCyclicP && sppfCyclic.get(p.leftChild.number)) dotOut.println(cycleStyle);
      }

      dotOut.println("\"" + p.number + "\"" + "->" + "\"" + p.rightChild.number + "\"");
      if (isCyclicP && sppfCyclic.get(p.rightChild.number)) dotOut.println(cycleStyle);
    }
  }
  // }
  /* SPPF cycle breaking **********************************************************************/

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

  private void sppfComputeCoreReachability(Set<CFGNode> cyclicCFGSlots) {

    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping reachability analysis");
      return;
    }

    // System.out.println("Cyclic slots: " + cyclicCFGSlots);

    sppfReachable.clear();
    // System.out.println("After clearing sppfReachable, contents are:\n" + sppfReachable);
    for (var n : sppf.keySet())
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

    sppfRootReachable = sppfReachable.getCodomain(sppfRootNode.number);
    // System.out.println("Root reachable set: " + sppfRootReachable);
    sppfCyclic.clear();
    for (int n = 0; n < sppfReachable.domainSize(); n++)
      if (sppfReachable.get(n, n)) sppfCyclic.set(n);
  }

  @Override
  public void sppfPrintCyclicSPPFNodesFromReachability() {
    sppfComputeCoreReachability(cfgRules.cyclicSlots);
    System.out.println(sppfCyclic.isEmpty() ? "There are no cyclic nodes" : "Cyclic nodes are");
    for (int i = 0; i < sppfCyclic.length(); i++)
      if (sppfCyclic.get(i)) System.out.print("  " + i);
    System.out.println();
  }

  public void loadXPartitionsFromReachability() {
    // In this implementation, X is represented by its packed node and symbol node partitions xP and xS
    xP = new HashSet<>();
    xS = new HashSet<>();
    for (var n : sppf.keySet())
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
    if (visitedSPPFNodes.get(n.number)) return;
    visitedSPPFNodes.set(n.number);
    // System.out.println("loadXPartitionsFromCFGRulesRec() entered " + n);
    for (var p : n.packNodes) {
      if (cfgRules.cyclicSlots.contains(p.grammarNode)) {
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

  @Override
  public void sppfBreakCycles(boolean trace, boolean counts, boolean statistics) {
    if (sppf == null || sppfRootNode == null) {
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
    visitedSPPFNodes.clear();
    loadXPartitionsFromCFGRulesRec(sppfRootNode);

    if (statistics) {
      System.out.print(lexer.inputString.length() + "," + getClass().getSimpleName() + "," + (inLanguage ? "accept" : "reject") + ",");
      countSymbol = countInter = countPacked = countPara = countTerm = countEps = 0;
      visitedSPPFNodes.clear();
      countRemove = false;
      sppfBreakCyclesCountsRec(sppfRootNode);
      System.out.print(countSymbol + "," + countInter + "," + countPacked + "," + countPara + "," + countTerm + "," + countEps + "," + xS.size() + ","
          + xP.size() + "," + cbD.size());
    }

    if (counts) {
      System.out.println("Core:\tSymbol\tInter\tPacked\tPara\tTerm\tEps\t|Xs|\t|Xp|\t|D|");
      countSymbol = countInter = countPacked = countPara = countTerm = countEps = 0;
      visitedSPPFNodes.clear();
      countRemove = false;
      sppfBreakCyclesCountsRec(sppfRootNode);
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
      visitedSPPFNodes.clear();
      countRemove = true;
      sppfBreakCyclesCountsRec(sppfRootNode);
      System.out.println("," + countSymbol + "," + countInter + "," + countPacked + "," + countPara + "," + countTerm + "," + countEps + "," + xS.size() + ","
          + xP.size() + "," + cbD.size() + "," + Util.timeAsMilliseconds(cycleBreakStartTime, cycleBreakEndTime));
    }

    if (counts) {
      countSymbol = countInter = countPacked = countPara = countTerm = countEps = 0;
      visitedSPPFNodes.clear();
      countRemove = true;
      sppfBreakCyclesCountsRec(sppfRootNode);
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
    if (visitedSPPFNodes.get(sppfn.number)) return;
    visitedSPPFNodes.set(sppfn.number);
    updateCountReachable(sppfn);

    if (sppfn.gn.element.kind == CFGKind.EPS)
      countEps++;
    else if (sppfn.packNodes.size() == 0)
      countTerm++;
    else if (cfgRules.paraterminalElements.contains(sppfn.gn.element)) {
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

  @Override
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
      dotOut = new PrintStream(new File("sppfCycleBreakRelation.dot"));
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

  @Override
  public void sppfPrintParaterminals() {
    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping paraterminal instance printing");
      return;
    }
    System.out.println("Paraterminals");
    visitedSPPFNodes.clear();
    paraterminalCount = terminalCount = 0;
    sppfCollectParaterminalsRec(sppfRootNode);
    System.out.println("!!! Excluding epsilon leaves, there are " + paraterminalCount + " paraterminal instances and " + terminalCount
        + " terminal instances reachable from the SPPF root");
    System.out.println("!!! During paraterminal collection " + visitedSPPFNodes.cardinality() + " SPPFnodes were visited");
    int rightmostIndex = 0;
    boolean overlapping = false;
    for (var i : paraterminalInstances.keySet()) {
      for (var s : paraterminalInstances.get(i))
        System.out.println(s.li + "," + s.ri + "  " + s.gn.element);
    }
  }

  private void sppfCollectParaterminalsRec(SPPFSymbolNode sppfn) {
    // System.out.println("Collect paraterminals at " + sppfn);
    if (visitedSPPFNodes.get(sppfn.number)) return;
    visitedSPPFNodes.set(sppfn.number);

    if (sppfn.isSymbol() && cfgRules.paraterminalElements.contains(sppfn.gn.element)) {
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

  @Override
  public void sppfPrintParasentences() {
    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping parasentence printing");
      return;
    }
    System.out.println("Parasentences");
    visitedSPPFNodes.clear();
    parasentence = new SPPFSymbolNode[100 * lexer.tokens.length + 1];
    psCall = 0;
    parasentences = new HashSet<>();
    // sppfCollectParasentencesRec(sppfRootNode, 0);
    sppfCollectParasentencesIter(sppfRootNode, 0);
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
  private void sppfCollectParasentencesIter(SPPFSymbolNode sppfRootNode2, int i) {
  }

  private int psCall;

  private int sppfCollectParasentencesRec(SPPFSymbolNode node, int parasentenceIndex) {
    int thisCall = ++psCall;
    System.out.println(thisCall + " collect parasentences at " + node + " with parasentenceIndex " + parasentenceIndex);
    int entrySentenceIndex = parasentenceIndex;
    if (visitedSPPFNodes.get(node.number)) {
      System.out.println("Already called; abort");
      return parasentenceIndex;
    }
    visitedSPPFNodes.set(node.number);

    if (node.packNodes.isEmpty() || (node.isSymbol() && cfgRules.paraterminalElements.contains(node.gn.element))) {
      if (!(node.packNodes.isEmpty() && node.gn.element.kind == CFGKind.EPS)) {
        System.out.println("Extending with " + node.gn.element.str);
        parasentence[parasentenceIndex++] = node;
        if (node.ri == lexer.tokens.length - 1) addParasentence(parasentenceIndex);
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
    visitedSPPFNodes.clear(node.number); // We are enumerating all traversals!
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