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
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.ParserBase;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexemeKind;
import uk.ac.rhul.cs.csle.art.script.TraversalKind;
import uk.ac.rhul.cs.csle.art.util.Relation;
import uk.ac.rhul.cs.csle.art.util.Util;

public class GLLBaseLine extends ParserBase {
  private final BitSet visitedSPPFNodes = new BitSet(), suppressedSPPFNode = new BitSet();

  /* Temporary disambiguation before choosers are implemented ****************/
  @Override
  public void chooseLongestMatch() {
    visitedSPPFNodes.clear();
    chooseLongestMatchRec(sppfRootNode);
  }

  private void chooseLongestMatchRec(SPPFN sn) {
    if (visitedSPPFNodes.get(sn.number)) return;
    visitedSPPFNodes.set(sn.number);

    int rightMostPivot = -1;
    SPPFPN candidate = null;
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

  /* Parser ******************************************************************/
  @Override
  public void parse() {
    descS = new HashSet<>();
    descQ = new LinkedList<>();
    sppf = new HashMap<>();
    gss = new HashMap<>();
    gssRoot = new GSSN(cfgRules.endOfStringNode, 0);
    gss.put(gssRoot, gssRoot);
    i = 0;
    sn = gssRoot;
    dn = null;
    for (CFGNode p = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt; p != null; p = p.alt)
      queueDesc(p.seq, i, sn, dn);
    inLanguage = false;

    nextDescriptor: while (dequeueDesc())
      while (true) {
        switch (gn.elm.kind) {
        case B, T, TI, C:
          if (input[i] == gn.elm.ei) {
            // System.out.println("Matched " + input[i]);
            du(1);
            i++;
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
            queueDesc(tmp.seq, i, sn, dn);
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
    if (!inLanguage) System.out.print(Util.echo("GLLBL " + "syntax error", positions[sppfWidestIndex()], inputString));
    loadCounts();
    numberSPPFNodes();
  }

  private void numberSPPFNodes() {
    int i = 1;
    for (var n : sppf.keySet())
      n.number = i++;
    for (var n : sppf.keySet())
      for (var p : n.packNS) {
        p.parent = n;
        p.number = i++;
      }
  }

  /* Thread handling *********************************************************/
  Set<Desc> descS;
  Deque<Desc> descQ;
  CFGNode gn;
  GSSN sn;
  SPPFN dn;

  void queueDesc(CFGNode gn, int i, GSSN gssN, SPPFN sppfN) {
    Desc tmp = new Desc(gn, i, gssN, sppfN);
    if (descS.add(tmp)) descQ.addFirst(tmp);
  }

  boolean dequeueDesc() {
    Desc tmp = descQ.poll();
    if (tmp == null) return false;
    gn = tmp.gn;
    i = tmp.i;
    sn = tmp.sn;
    dn = tmp.dn;
    return true;
  }

  /* Stack handling **********************************************************/
  Map<GSSN, GSSN> gss;
  GSSN gssRoot;

  GSSN gssFind(CFGNode gn, int i) {
    GSSN gssN = new GSSN(gn, i);
    if (gss.get(gssN) == null) gss.put(gssN, gssN);
    return gss.get(gssN);
  }

  void call(CFGNode gn) {
    GSSN gssN = gssFind(gn.seq, i);
    GSSE gssE = new GSSE(sn, dn);
    if (!gssN.edges.contains(gssE)) {
      gssN.edges.add(gssE);
      for (SPPFN rc : gssN.pops)
        queueDesc(gn.seq, rc.ri, sn, sppfUpdate(gn.seq, dn, rc));
    }
    for (CFGNode p = cfgRules.elementToNodeMap.get(gn.elm).alt; p != null; p = p.alt)
      queueDesc(p.seq, i, gssN, null);
  }

  void ret() {
    if (sn.equals(gssRoot)) { // Stack base
      if (cfgRules.acceptingNodeNumbers.contains(gn.num) && (i == input.length - 1)) {
        sppfRootNode = sppf.get(new SPPFN(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), 0, input.length - 1));
        inLanguage = true;
      }
      return; // End of parse
    }
    sn.pops.add(dn);
    for (GSSE e : sn.edges)
      queueDesc(sn.gn, i, e.dst, sppfUpdate(sn.gn, e.sppfnode, dn));
  }

  /* Derivation handling *****************************************************/
  Map<SPPFN, SPPFN> sppf;
  SPPFN sppfRootNode;

  SPPFN sppfFind(CFGNode dn, int li, int ri) {
    // System.out.print("sppfFind with dn " + dn.toStringAsProduction() + " with extents " + li + "," + ri);

    SPPFN tmp = new SPPFN(dn, li, ri);
    if (!sppf.containsKey(tmp)) {
      sppf.put(tmp, tmp);
      // System.out.print(" added " + tmp);
    }
    // System.out.println(" resulting sppf\n" + sppf.keySet());
    return sppf.get(tmp);
  }

  SPPFN sppfUpdate(CFGNode gn, SPPFN ln, SPPFN rn) {
    SPPFN ret = sppfFind(gn.elm.kind == CFGKind.END ? gn.seq : gn, ln == null ? rn.li : ln.li, rn.ri);
    // System.out.println(
    // "Updating SPPF node with gn " + gn.toStringAsProduction() + " and extents " + (ln == null ? rn.li : ln.li) + "," + rn.ri + " retrieves node " + ret);
    ret.packNS.add(new SPPFPN(gn, ln == null ? rn.li : ln.ri, ln, rn));
    return ret;
  }

  void du(int width) {
    // dn = sppfUpdate(gn.seq, dn, sppfFind(gn, i, i + width)); // SLE paper version
    var gnp = cfgRules.elementToNodeMap.get(gn.elm);
    dn = sppfUpdate(gn.seq, dn, sppfFind(gnp, i, i + width)); // singleton element version to reduce SPPF size and correct for grammars with cycles
  }

  private int sppfWidestIndex() {
    int ret = 0;
    for (SPPFN s : sppf.keySet())
      if (ret < s.ri) ret = s.ri;
    return ret;
  }

  /* Term generation **************************************************************************/
  /* This version handles promotion operators, but does not create ambiguity nodes */
  long derivationNodeCount = 0, derivationAmbiguityNodeCount = 0;

  @Override
  public int derivationAsTerm() {
    if (sppfRootNode == null) return 0;
    visitedSPPFNodes.clear();
    LinkedList<Integer> carrier = new LinkedList<>();
    derivationAsTermRec(sppfRootNode, carrier, firstAvailablePackNode(sppfRootNode).gn.seq); // Root packed node must have a grammar node that is the end of a
                                                                                             // start production
    loadDerivationCounts(derivationNodeCount, derivationAmbiguityNodeCount);
    return carrier.getFirst();
  }

  private String derivationAsTermRec(SPPFN sppfn, LinkedList<Integer> childrenFromParent, CFGNode gn) {
    // System.out.println("\nEntered derivationAsTermRec() at node " + sppfn + " instance " + gn);
    if (visitedSPPFNodes.get(sppfn.number)) {
      Util.warning("derivationAsTermRec() found cycle in derivation");
      return "Cycle in derivation";
    }

    visitedSPPFNodes.set(sppfn.number);
    LinkedList<Integer> children = (gn.giftKind == GIFTKind.OVER || gn.giftKind == GIFTKind.UNDER) ? childrenFromParent : new LinkedList<>();
    String constructor = null;

    if (sppfn.packNS.size() != 0) { // Non leaf symbol nodes
      SPPFPN sppfpn = firstAvailablePackNode(sppfn);
      CFGNode childgn = sppfpn.gn.alt.seq;
      LinkedList<SPPFN> childSymbolNodes = new LinkedList<>();
      collectChildNodesRec(sppfpn, childSymbolNodes);
      for (SPPFN s : childSymbolNodes) {
        String newConstructor = derivationAsTermRec(s, children, childgn);
        if (newConstructor != null) constructor = newConstructor; // Update on every ^^ child so that the last one wins
        childgn = childgn.seq; // Step to next child grammar node
      }
    }

    if (constructor == null) // If there were no OVERs, then set the constructor to be our symbol
      constructor = (gn.elm.kind == CFGKind.B) ? lexemeOfBuiltin(LexemeKind.valueOf(gn.elm.str), positions[sppfn.li]) : gn.elm.str;

    if (children != childrenFromParent) {
      childrenFromParent.add(cfgRules.iTerms.findTerm(constructor, children));
      derivationNodeCount++;
    }

    visitedSPPFNodes.clear(sppfn.number);
    return (gn.giftKind == GIFTKind.OVER) ? constructor : null;
  }

  private void collectChildNodesRec(SPPFPN sppfpn, LinkedList<SPPFN> childNodes) {
    // System.out.println("CollectChildNodesRec() at pack node " + sppfpn);
    SPPFN leftChild = sppfpn.leftChild, rightChild = sppfpn.rightChild;
    if (leftChild != null) {
      if (isSymbol(leftChild)) // found a symbol
        childNodes.add(leftChild);
      else
        collectChildNodesRec(firstAvailablePackNode(leftChild), childNodes);
    }

    if (isSymbol(rightChild)) // found a symbol
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

  private boolean isSymbol(SPPFN sppfn) {
    return sppfn.packNS.size() == 0 /* terminal or epsilon */ || (sppfn.gn.elm.kind == CFGKind.N && sppfn.gn.seq == null /* LHS */);
  }

  private void loadCounts() {
    loadTWECounts(input.length, input.length - 1, 1);

    int gssEdgeCount = 0, popCount = 0;
    for (GSSN g : gss.keySet()) {
      gssEdgeCount += g.edges.size();
      popCount += g.pops.size();
    }
    loadGSSCounts(descS.size(), gss.keySet().size(), gssEdgeCount, popCount);

    int sppfEpsilonNodeCount = 0, sppfTerminalNodeCount = 0, sppfNonterminalNodeCount = 0, sppfIntermediateNodeCount = 0, sppfPackNodeCount = 0,
        sppfAmbiguityCount = 0, sppfEdgeCount = 0;
    for (SPPFN s : sppf.keySet()) {
      switch (s.gn.elm.kind) {
      // Dodgy - how do we test the flavour of an SPPF node?
      case T, TI, C, B:
        sppfTerminalNodeCount++;
        break;
      case EPS:
        sppfEpsilonNodeCount++;
        break;
      }
      sppfPackNodeCount += s.packNS.size();
      if (s.packNS.size() > 1) sppfAmbiguityCount++;
      for (SPPFPN p : s.packNS) {
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
  class Desc {
    public CFGNode gn;
    public int i;
    public GSSN sn;
    public SPPFN dn;

    public Desc(CFGNode gn, int index, GSSN sn, SPPFN dn) {
      super();
      this.gn = gn;
      this.i = index;
      this.sn = sn;
      this.dn = dn;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      sb.append(i);
      sb.append(", ");
      sb.append(gn.toStringAsProduction());
      sb.append(", ");
      sb.append(sn);
      sb.append(", ");
      sb.append(dn);
      sb.append(")");
      return sb.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((sn == null) ? 0 : sn.hashCode());
      result = prime * result + i;
      result = prime * result + ((gn == null) ? 0 : gn.hashCode());
      result = prime * result + ((dn == null) ? 0 : dn.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Desc other = (Desc) obj;
      if (sn == null) {
        if (other.sn != null) return false;
      } else if (!sn.equals(other.sn)) return false;
      if (i != other.i) return false;
      if (gn == null) {
        if (other.gn != null) return false;
      } else if (!gn.equals(other.gn)) return false;
      if (dn == null) {
        if (other.dn != null) return false;
      } else if (!dn.equals(other.dn)) return false;
      return true;
    }
  }

  class GSSN {
    public final CFGNode gn;
    final int i;
    public final Set<GSSE> edges = new HashSet<>();
    public final Set<SPPFN> pops = new HashSet<>();

    public GSSN(CFGNode gn, int i) {
      super();
      this.gn = gn;
      this.i = i;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + i;
      result = prime * result + ((gn == null) ? 0 : gn.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      GSSN other = (GSSN) obj;
      if (i != other.i) return false;
      if (gn == null) {
        if (other.gn != null) return false;
      } else if (!gn.equals(other.gn)) return false;
      return true;
    }

    @Override
    public String toString() {
      return "(" + gn.toStringAsProduction() + ", " + i + ")";
    }

  }

  class GSSE {
    public final GSSN dst;
    public final SPPFN sppfnode;

    public GSSE(GSSN dst, SPPFN sppfNode) {
      this.sppfnode = sppfNode;
      this.dst = dst;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dst == null) ? 0 : dst.hashCode());
      result = prime * result + ((sppfnode == null) ? 0 : sppfnode.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      GSSE other = (GSSE) obj;
      if (dst == null) {
        if (other.dst != null) return false;
      } else if (!dst.equals(other.dst)) return false;
      if (sppfnode == null) {
        if (other.sppfnode != null) return false;
      } else if (!sppfnode.equals(other.sppfnode)) return false;
      return true;
    }
  }

  class SPPFNode { // Carrier to unify SPPFN and SPPPPN
  };

  class SPPFN extends SPPFNode {
    public int number = 0; // to be set after parsing
    public final CFGNode gn;
    public final int li;
    public final int ri;
    public final Set<SPPFPN> packNS = new HashSet<>();

    public SPPFN(CFGNode gn, int li, int ri) {
      super();
      this.gn = gn;
      this.li = li;
      this.ri = ri;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((gn == null) ? 0 : gn.hashCode());
      result = prime * result + li;
      result = prime * result + ri;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      SPPFN other = (SPPFN) obj;
      if (gn == null) {
        if (other.gn != null) return false;
      } else if (!gn.equals(other.gn)) return false;
      if (li != other.li) return false;
      if (ri != other.ri) return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      // if (gn == null)
      // sb.append("NULL");
      // else
      sb.append(number);
      sb.append(": ");
      if (isSymbol(this))
        sb.append(gn.toString());
      else
        sb.append(gn.toStringAsProduction());
      sb.append(", ");
      sb.append(li);
      sb.append(", ");
      sb.append(ri);

      return sb.toString();
    }
  }

  // Note Nov 2024 added parent to support SPPF cycle detection - not needed for actual cycle detection
  class SPPFPN extends SPPFNode {
    public int number = 0; // Numbers allocated after parse
    public SPPFN parent = null; // Parents allocated after parse
    public final CFGNode gn;
    public final int pivot;
    public final SPPFN leftChild;
    public final SPPFN rightChild;
    public boolean suppressed = false;

    public SPPFPN(CFGNode gn, int pivot, SPPFN leftChild, SPPFN rightChild) {
      super();
      this.gn = gn;
      this.pivot = pivot;
      this.leftChild = leftChild;
      this.rightChild = rightChild;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((gn == null) ? 0 : gn.hashCode());
      result = prime * result + number;
      result = prime * result + pivot;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      SPPFPN other = (SPPFPN) obj;
      if (gn == null) {
        if (other.gn != null) return false;
      } else if (!gn.equals(other.gn)) return false;
      if (number != other.number) return false;
      if (pivot != other.pivot) return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(number);
      sb.append(": ");
      sb.append(gn.toStringAsProduction());
      sb.append(", " + pivot);
      sb.append(" under ");
      sb.append(parent);
      return sb.toString();
    }
  }

  /* GSS and SPPF rendering *******************************************************************/
  @Override
  public void gss2Dot() {
    new GSS2Dot(gss, "gss.dot");
  }

  class GSS2Dot {
    public GSS2Dot(Map<GSSN, GSSN> gss, String filename) {
      if (gss == null) return;
      PrintStream gssOut;
      try {
        gssOut = new PrintStream(new File(filename));
        gssOut.println("digraph \"GSS\" {\n" + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04  color=gray]\n"
            + "graph[ordering=out ranksep=0.1]\n" + "edge[arrowsize = 0.3  color=gray]");

        if (gss != null) for (GSSN s : gss.keySet()) {
          gssOut.println("\"" + s + "\" [label=\"" + s.gn.toStringAsProduction() + "\n" + s.i + "\"]");
          for (GSSE c : s.edges) // iterate over children
            gssOut.println("\"" + s + "\"->\"" + c.dst + "\"");
        }
        gssOut.println("}");
        gssOut.close();
      } catch (FileNotFoundException e) {
        System.out.println("Unable to write GSS visualisation to " + filename);
      }
    }
  }

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
  PrintStream sppfOut = null;
  private boolean showIndicies;
  private boolean showIntermediates;

  @Override
  public void sppfPrint() {
    if (sppf == null || sppfRootNode == null) Util.fatal("Current parser does not have a current SPPF that can be printed");
    for (var n : sppf.keySet()) {
      System.out.println(n);
      for (var pn : n.packNS)
        System.out.println(pn);
    }
  }

  @Override
  public void sppf2Dot() {
    sppfComputeReachability();

    if (sppf == null || sppfRootNode == null) Util.fatal("Current parser does not have a current SPPF that can be visualised as a .dot file");
    SPPF2Dot(sppf, sppfRootNode, "sppf_full.dot", true, true, true); // full SPPF
    SPPF2Dot(sppf, sppfRootNode, "sppf_core.dot", false, true, true); // core SPPF - only nodes reachable from (S,0,n)
  }

  public void SPPF2Dot(Map<SPPFN, SPPFN> sppf, SPPFN rootNode, String filename, boolean full, boolean showIndicies, boolean showIntermediates) {
    this.showIndicies = showIndicies;
    this.showIntermediates = showIntermediates;

    try {
      sppfOut = new PrintStream(new File(filename));
      sppfOut.println("digraph \"SPPF\" {\n"
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
      if (full)
        for (SPPFN n : sppf.keySet())
          sppfSubtreeToDot(n);
      else {
        visitedSPPFNodes.clear();
        coreSPPFToDotRec(rootNode);
      }
      sppfOut.println("}");
      sppfOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to " + filename);
    }
  }

  private void coreSPPFToDotRec(SPPFN sppfn) {
    if (visitedSPPFNodes.get(sppfn.number)) return;
    visitedSPPFNodes.set(sppfn.number);

    sppfSubtreeToDot(sppfn);

    for (SPPFPN p : sppfn.packNS) { // Recurse through packed nodes
      if (p.leftChild != null) coreSPPFToDotRec(p.leftChild);
      if (p.rightChild != null) coreSPPFToDotRec(p.rightChild);
    }
  }

  private void sppfSubtreeToDot(SPPFN sppfn) {
    boolean isAmbiguous = sppfn.packNS.size() > 1;
    if (isSymbol(sppfn))
      sppfOut.println("\"" + sppfn + "\"" + symbolNodeStyle);
    else
      sppfOut.println("\"" + sppfn + "\"" + intermediateNodeStyle);

    if (isAmbiguous) sppfOut.println(ambiguousStyle);
    if (sppfCyclic.contains(sppfn)) sppfOut.println(cycleStyle);
    if (!sppfRootReachable.contains(sppfn)) sppfOut.println(unreachableSymbolNodeStyle);
    if (sppfn == sppfRootNode) sppfOut.println(rootNodeStyle);

    for (SPPFPN p : sppfn.packNS) {
      boolean isCyclicP = sppfCyclic.contains(p);

      sppfOut.println("\"" + p + "\"" + packNodeStyle + " [label = \"" + p.number + ": " + p.gn.toStringAsProduction() + " , " + p.pivot + "\" ]");
      if (isCyclicP) sppfOut.println(cycleStyle);
      if (!sppfRootReachable.contains(p)) sppfOut.println(unreachablePackNodeStyle);

      if (cycleBreakDeleted.contains(p) && cycleBreakDeletedPrime.contains(p))
        sppfOut.println(deletedAndDeletedPrimePackNodeStyle);
      else if (cycleBreakDeleted.contains(p))
        sppfOut.println(deletedPackNodeStyle);
      else if (cycleBreakDeletedPrime.contains(p)) sppfOut.println(deletedPrimePackNodeStyle);
      sppfOut.println("\"" + sppfn + "\"" + "->" + "\"" + p + "\"");

      if (isCyclicP)
        sppfOut.println(cycleStyle);
      else if (isAmbiguous) sppfOut.println(ambiguousStyle);

      if (p.leftChild != null) {
        sppfOut.println("\"" + p + "\"" + "->" + "\"" + p.leftChild + "\"");
        if (isCyclicP && sppfCyclic.contains(p.leftChild)) sppfOut.println(cycleStyle);
      }

      sppfOut.println("\"" + p + "\"" + "->" + "\"" + p.rightChild + "\"");
      if (isCyclicP && sppfCyclic.contains(p.rightChild)) sppfOut.println(cycleStyle);
    }
  }

  /* SPPF cycle breaking **********************************************************************/

  private final Deque<SPPFNode> visitedStack = new ArrayDeque<>(); // Only usedby sppfCycleRec to keep a list of visited nodes during descent
  private Set<SPPFNode> xNodesBeforeBreaking; // All cyclic nodes - only used by SPPF diagnostics
  private Set<SPPFN> xS; // Set of cyclic symbol or intermediate nodes; a subset of the X in Elizabeth's note
  private Set<SPPFPN> xP; // Set of cyclic packed nodes; X = xS U xP
  private Set<SPPFNode> cycleBreakDeleted = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  private final Set<SPPFNode> cycleBreakDeletedPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note
  private final Relation<SPPFNode, SPPFNode> sppfReachable = new Relation<>();
  private final Set<SPPFNode> sppfCyclic = new HashSet<>();
  private Set<SPPFNode> sppfRootReachable;
  private boolean cycleBreakTrace;
  private TraversalKind cycleBreakTraversalKind;
  private boolean cycleBreakLone;
  private boolean cycleBreakSibling;

  // Enumerate every derivation and check for cycles - may be veerrry slow
  // private Set<Set<SPPFNode>> sppfCycles; // Used by sppPrintCyclesRec to uniquify the detected cycles
  //
  // private void sppfPrintCycleRec(SPPFN sppfn) {
  // if (visitedSPPFNodes.get(sppfn.number)) {
  // sppfCycles.add(new HashSet<>(visitedStack));
  // return;
  // }
  // visitedSPPFNodes.set(sppfn.number);
  // visitedStack.push(sppfn);
  //
  // for (var pn : sppfn.packNS) {
  // if (cycleBreakDeleted.contains(pn)) continue; // Skip packed nodes marked for deletion by cycle removal
  // visitedStack.push(pn);
  // if (pn.leftChild != null) sppfPrintCycleRec(pn.leftChild);
  // sppfPrintCycleRec(pn.rightChild);
  // visitedStack.pop();
  // }
  // visitedSPPFNodes.clear(sppfn.number);
  // visitedStack.pop();
  // }
  //
  // @Override
  // public void sppfPrintCycles() {
  // if (sppfRootNode == null) Util.fatal("Current parser does not have a current SPPF that can be cycle checked");
  // visitedSPPFNodes.clear();
  // sppfCycles = new HashSet<>();
  // sppfPrintCycleRec(sppfRootNode);
  // if (sppfCycles.size() == 0)
  // System.out.println("No SPPF cycles found");
  // else
  // for (var sc : sppfCycles) {
  // System.out.print("SPPF cycle involving ");
  // for (var sce : sc)
  // if (sce instanceof SPPFN) System.out.print(sce + " ");
  // System.out.println();
  // }
  // }
  //
  private void sppfComputeReachability() {
    sppfReachable.clear();
    for (var n : sppf.keySet())
      for (var p : n.packNS) {
        if (cycleBreakDeleted.contains(p) /* || cycleBreakDeletedPrime.contains(p) */) continue;
        sppfReachable.add(n, p);
        if (p.leftChild != null) sppfReachable.add(p, p.leftChild);
        sppfReachable.add(p, p.rightChild);
      }
    // System.out.println("After initialising sppfReachable, contents are:\n" + sppfReachable);
    sppfReachable.transitiveClosure();
    // System.out.println("After transitive closure of sppfReachable, contents are:\n" + sppfReachable);

    sppfRootReachable = sppfReachable.get(sppfRootNode);
    // System.out.println("Root reachable set: " + sppfRootReachable);
    sppfCyclic.clear();
    for (var n : sppfReachable.getDomain())
      if (sppfReachable.get(n).contains(n)) sppfCyclic.add(n);
  }

  @Override
  public void sppfPrintCyclicNodes() {
    sppfComputeReachability();
    System.out.println(sppfCyclic.isEmpty() ? "There are no cyclic nodes" : "Cyclic nodes are");
    for (var d : sppfCyclic)
      System.out.println("  " + d);
  }

  public void loadXPartitions() {
    // In this implementation, X is represented by its packed node and symbol node partitions xP and xS
    xP = new HashSet<>();
    xS = new HashSet<>();
    for (var n : sppf.keySet())
      if (sppfCyclic.contains(n)) {
        xS.add(n);
        for (var p : n.packNS)
          if (sppfCyclic.contains(p)) {
            xNodesBeforeBreaking.add(p);
            xP.add(p);
          }
      }
  }

  private boolean changed;
  private int cycleBreakPass;

  void sppfCycleBreakAlgorithm1() {
    for (var pp : new HashSet<>(xP)) {
      // Look to see if this node has any 'keep' packed children
      SPPFN n = pp.parent;
      boolean hasNotInXp = false;
      if (n != null) for (var p : n.packNS)
        if (!xP.contains(p)) {
          hasNotInXp = true;
          break;
        }
      // If any of the packed children were 'keep' then mark one non-keep node as D
      if (hasNotInXp) {
        if (cycleBreakTrace) System.out.println("Node " + n + " has 'keep' children which are not in Xp");
        for (var p : n.packNS) {
          // System.out.println("Scanning for deletion: " + p + " in xP " + xP.contains(p));
          if (xP.contains(p)) {
            changed = true;
            cycleBreakDeleted.add(p);
            xP.remove(p);
            if (cycleBreakTrace) System.out.println("Deleted pack node " + p);

            if (cycleBreakTrace) System.out.println("Start of v loop");
            boolean changedOnV = true;
            while (changedOnV) {
              changedOnV = false;

              for (var pn : new HashSet<>(xP))
                if (!xS.contains(pn.rightChild) && (pn.leftChild != null && !xS.contains(pn.leftChild))) {
                  changedOnV = true;
                  xP.remove(pn);
                  if (cycleBreakTrace) System.out.println("Removed from Xp: " + pn);
                }

              for (var nn : new HashSet<>(xS)) {
                boolean allNotInX = true;
                for (var pn : nn.packNS)
                  if (xP.contains(pn)) allNotInX = false; // break here
                if (allNotInX) {
                  changedOnV = true;
                  xS.remove(nn);
                  if (cycleBreakTrace) System.out.println("Removed from Xs: " + nn);
                }
              }
            }
          }
          // NB if we only want to remove one 'u' for each 'w' then insert a break here; probably pointless
          // because our deterministic w loop will come back here on the next pass anyway
        }
      }
    }
  }

  // This is an implementation of 'CycleBreak1'
  void sppfCycleBreakAlgorithm2() {
    for (var nn : new HashSet<>(xS)) { // Process all symbol nodes in X

      if (cycleBreakTrace) System.out.println("Checking symbol node " + nn + " with child predicate " + childPredicateSymbol(nn));
      if (childPredicateSymbol(nn)) {
        xS.remove(nn);
        if (cycleBreakTrace) System.out.println("Removed from xS: " + nn);
        changed = true;
        return;
      }
    }
    for (var pp : new HashSet<>(xP)) { // Process all packed nodes in X
      if (cycleBreakTrace) System.out
          .println("Checking packed node " + pp + " with child predicate " + childPredicatePacked(pp) + " and sibling predicate " + siblingPredicatePacked(pp));
      if (childPredicatePacked(pp) && siblingPredicatePacked(pp)) System.out.println("Both predicates triggered for packed node " + pp);

      if (!childPredicatePacked(pp)) {
        xP.remove(pp);
        cycleBreakDeleted.add(pp);
        if (cycleBreakTrace) System.out.println("Removed from xP and added to D: " + pp);
        changed = true;
        return;
      } else if (siblingPredicatePacked(pp)) { // Note else is not required when 'single' returns are being used
        xP.remove(pp);
        cycleBreakDeletedPrime.add(pp);
        if (cycleBreakTrace) System.out.println("Removed from xP and added to D': " + pp);
        changed = true;
        return;
      }
    }
  }

  // children(v) ∩ Xi = ∅ (symbol node child predicate)
  public boolean childPredicateSymbol(SPPFN sppfN) {
    for (var pn : sppfN.packNS)
      if (xP.contains(pn)) return false;
    return true;
  }

  // sibling(v) ̸⊆ Xi (packed node sibling predicate)
  public boolean siblingPredicatePacked(SPPFPN sppfPN) {
    SPPFN parent = sppfPN.parent;
    for (var p : parent.packNS)
      if (p != sppfPN && !xP.contains(p)) return true;
    return false;
  }

  // children(v) ∩ Xi = ∅ (packed node child predicate)
  public boolean childPredicatePacked(SPPFPN sppfPN) {
    if (sppfPN.leftChild != null && xS.contains(sppfPN.leftChild)) return false;
    if (xS.contains(sppfPN.rightChild)) return false;
    return true;
  }

  @Override
  public void sppfBreakCycles(boolean cycleBreakTrace, TraversalKind cycleBreakTraversalKind, boolean cycleBreakLone, boolean cycleBreakSibling) {
    this.cycleBreakTrace = cycleBreakTrace;
    this.cycleBreakTraversalKind = cycleBreakTraversalKind;
    this.cycleBreakLone = cycleBreakLone;
    this.cycleBreakSibling = cycleBreakSibling;

    xNodesBeforeBreaking = new HashSet<>(); // Only needed to show broken cycles (magenta lines in sppf*.dot) - comment out otherwise
    cycleBreakDeleted = new HashSet<>();

    // Load all cyclic nodes to X (in detail to the xS and xP partitions)
    sppfComputeReachability();
    // if (cycleBreakTrace) {
    // System.out.println("Self reachable (cyclic) nodes are");
    // for (var d : sppfCyclic)
    // System.out.println(" " + d);
    // }

    loadXPartitions();

    if (cycleBreakTrace) {
      System.out.println("Before cycle breaking, |Xs| = " + xS.size() + " |Xp| = " + xP.size());

      System.out.println("Xs is ");
      for (var d : xS)
        System.out.println("  " + d);

      System.out.println("Xp is ");
      for (var d : xP)
        System.out.println("  " + d);
    }

    changed = true;
    cycleBreakPass = 1;
    while (changed) {
      if (cycleBreakTrace) System.out.println("SPPF cycle break pass " + cycleBreakPass++);
      changed = false;
      sppfCycleBreakAlgorithm2();
    }

    if (cycleBreakTrace) {
      System.out.println(
          "After cycle breaking, |Xs|=" + xS.size() + " |Xp|=" + xP.size() + " |D|=" + cycleBreakDeleted.size() + " |D'|=" + cycleBreakDeletedPrime.size());

      System.out.println("D is ");
      for (var d : cycleBreakDeleted)
        System.out.println("  " + d);

      System.out.println("D' is ");
      for (var d : cycleBreakDeletedPrime)
        System.out.println("  " + d);
    }
    sppfComputeReachability(); // Recompute reachability to see if any cycles are left
  }

  class Configuration {
    Set<SPPFPN> xP;
    Set<SPPFN> xS;
    Set<SPPFPN> d;

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      for (var v : xP)
        sb.append(v.number + " ");

      sb.append("}{");
      for (var v : xS)
        sb.append(v.number + " ");

      sb.append("}{");
      for (var v : d)
        sb.append(v.number + " ");

      sb.append("}");
      return sb.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((d == null) ? 0 : d.hashCode());
      result = prime * result + ((xP == null) ? 0 : xP.hashCode());
      result = prime * result + ((xS == null) ? 0 : xS.hashCode());
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
      if (xP == null) {
        if (other.xP != null) return false;
      } else if (!xP.equals(other.xP)) return false;
      if (xS == null) {
        if (other.xS != null) return false;
      } else if (!xS.equals(other.xS)) return false;
      return true;
    }

    public Configuration(Set<SPPFPN> xP, Set<SPPFN> xS, Set<SPPFPN> d) {
      super();
      this.xP = xP;
      this.xS = xS;
      this.d = d;
    }

    public Configuration(Configuration c) {
      xP = new HashSet<>(c.xP);
      xS = new HashSet<>(c.xS);
      d = new HashSet<>(c.d);
    }

  }

  Deque<Configuration> q = new LinkedList<>();
  Set<Configuration> queued = new HashSet<>();
  Relation<Configuration, Configuration> r = new Relation<>();

  boolean breakCyclesRelationTrace = false;

  @Override
  public void sppfBreakCyclesRelation() {
    Configuration c, cp;
    sppfComputeReachability(); // computes sppfCyclic (set of cyclic SPPF nodes)
    loadXPartitions(); // Load X from computed cyclic nodes as partitions xP and xS

    Configuration c_0 = new Configuration(xP, xS, new HashSet<SPPFPN>());
    q.add(c_0); // enqueue start element
    System.out.println("C_0: " + c_0);

    while (q.size() != 0) {
      c = q.removeFirst(); // deqeue c
      if (breakCyclesRelationTrace) System.out.println("** Processing: " + c);

      for (var p : c.xP) { // Process packed nodes
        if (breakCyclesRelationTrace) System.out.println("Checking packed node: " + p);
        if (hasKeptSibling(p, c.xP)) { // u loop
          if (breakCyclesRelationTrace) System.out.println("Packed node has kept sibling");
          cp = new Configuration(c);
          cp.xP.remove(p);
          cp.d.add(p);
          newState(c, cp);
        }

        if (allChildrenKept(p, c.xS)) { // w loop
          if (breakCyclesRelationTrace) System.out.println("Packed node has all kept children");
          cp = new Configuration(c);
          cp.xP.remove(p);
          newState(c, cp);
        }
      }

      for (var n : c.xS) {
        if (breakCyclesRelationTrace) System.out.println("Checking symbol node: " + n);

        if (allChildrenKept(n, c.xP)) { // w loop
          if (breakCyclesRelationTrace) System.out.println("Symbol node has all kept children");
          cp = new Configuration(c);
          cp.xS.remove(n);
          newState(c, cp);
        }
      }
    }

    System.out.println("Relation has " + r.getDomain().size() + " nodes with terminals");
    for (var de : r.getDomain()) {
      // System.out.println(de + "->" + r.get(de));
      if (r.get(de).size() == 0) System.out.println(de);
    }

    // System.out.println("Queued");
    // for (var v : queued)
    // System.out.println(v);

    // Output relation as .dot file
    try {
      sppfOut = new PrintStream(new File("sppfCycleBreakRelation.dot"));
      sppfOut.println("digraph \"SPPF\" {\n" + "rankdir=\"LR\" "
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
      for (var de : r.getDomain()) {
        for (var cde : r.get(de))
          sppfOut.println("\"" + de + "\"->\"" + cde + "\"");
        sppfOut.println();
      }
      sppfOut.println("}");
      sppfOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to sppfCycleBreakRelation.dot");
    }

  }

  public void newState(Configuration c, Configuration cp) {
    r.add(c, cp);
    r.add(cp);
    if (breakCyclesRelationTrace) System.out.println("Added (" + c + ", " + cp + ")");
    if (!queued.contains(cp)) {
      q.add(cp);
      queued.add(cp);
      if (breakCyclesRelationTrace) System.out.println("Queued" + cp);
    }
  }

  private boolean allChildrenKept(SPPFN n, Set<SPPFPN> xP) {
    for (var p : n.packNS)
      if (xP.contains(p)) return false;
    return true;
  }

  private boolean allChildrenKept(SPPFPN p, Set<SPPFN> xS) {
    if (p.leftChild != null && xS.contains(p.leftChild)) return false;
    if (xS.contains(p.leftChild)) return false;
    return true;
  }

  private boolean hasKeptSibling(SPPFPN p, Set<SPPFPN> xP) {
    for (var ps : p.parent.packNS)
      if (!xP.contains(ps)) return true;
    return false;
  }
}
