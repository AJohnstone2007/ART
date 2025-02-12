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
import java.util.TreeMap;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexemeKind;
import uk.ac.rhul.cs.csle.art.script.TraversalKind;
import uk.ac.rhul.cs.csle.art.util.Relation;
import uk.ac.rhul.cs.csle.art.util.RelationOverNaturals;
import uk.ac.rhul.cs.csle.art.util.Util;

public class GLLBaseLine extends AbstractParser {
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
          if (tokens[i] == gn.elm.ei) {
            // System.out.println("Matched " + tokens[i]);
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
    if (!inLanguage) System.out.print(Util.echo("GLLBL " + "syntax error", leftIndices[sppfWidestIndex()], inputString));
    loadCounts();
    numberSPPFNodes();
  }

  int sppfCardinality;

  private void numberSPPFNodes() {
    sppfCardinality = 1;
    for (var n : sppf.keySet())
      n.number = sppfCardinality++;
    for (var n : sppf.keySet())
      for (var p : n.packNS) {
        p.parent = n;
        p.number = sppfCardinality++;
      }

    sppfReachable = new RelationOverNaturals(sppfCardinality, sppfCardinality);
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

  /* Deque handling **********************************************************/
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
    if (sn.equals(gssRoot)) { // Deque base
      if (cfgRules.acceptingNodeNumbers.contains(gn.num) && (i == tokens.length - 1)) {
        sppfRootNode = sppf.get(new SPPFN(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), 0, tokens.length - 1));
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

  boolean derivationForInterpreter = false;

  @Override
  public int derivationAsInterpeterTerm() {
    derivationForInterpreter = true;
    int ret = derivationAsTerm();
    derivationForInterpreter = false;
    return ret;
  }

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

  boolean derivationSeenCycle = false;

  private String derivationAsTermRec(SPPFN sppfn, LinkedList<Integer> childrenFromParent, CFGNode gn) {
    // System.out.println("\nEntered derivationAsTermRec() at node " + sppfn + " instance " + gn);
    if (visitedSPPFNodes.get(sppfn.number)) {
      if (!derivationSeenCycle) Util.error("derivationAsTermRec() found cycle in derivation");
      derivationSeenCycle = true;
      return "__CYCLE";
    }

    visitedSPPFNodes.set(sppfn.number);
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
      constructor = (gn.elm.kind == CFGKind.B) ? lexemeOfBuiltin(LexemeKind.valueOf(gn.elm.str), leftIndices[sppfn.li]) : gn.elm.str;

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

  private boolean ambiguousSPPF;

  public boolean ambiguityCheck() {
    ambiguousSPPF = false;
    ambiguityCheckRec(this.sppfRootNode);
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

  private boolean isSymbol(SPPFN sppfn) {
    return sppfn.packNS.size() == 0 /* terminal or epsilon */ || (sppfn.gn.elm.kind == CFGKind.N && sppfn.gn.seq == null /* LHS */);
  }

  private void loadCounts() {
    loadTWECounts(tokens.length, tokens.length - 1, 1);

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
      if (gn == null)
        sb.append("NULL");
      else
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
    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping SPPF printing");
      return;
    }
    for (var n : sppf.keySet()) {
      System.out.println(n);
      for (var pn : n.packNS)
        System.out.println(pn);
    }
  }

  @Override
  public void sppf2Dot() {
    sppfComputeReachability();

    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping SPPF visualisation");
      return;
    }
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
      sppfOut.println(
          "\"" + sppfn.number + "\"" + symbolNodeStyle + " [label=\"" + sppfn.number + " " + sppfn.gn.toString() + " " + sppfn.li + ", " + sppfn.ri + "\"]");
    else
      sppfOut.println("\"" + sppfn.number + "\"" + intermediateNodeStyle + " [label=\"" + sppfn.number + " " + sppfn.gn.toStringAsProduction() + " " + sppfn.li
          + ", " + sppfn.ri + "\"]");

    if (isAmbiguous) sppfOut.println(ambiguousStyle);
    if (sppfCyclic.get(sppfn.number)) sppfOut.println(cycleStyle);
    if (!sppfRootReachable.get(sppfn.number)) sppfOut.println(unreachableSymbolNodeStyle);
    if (sppfn == sppfRootNode) sppfOut.println(rootNodeStyle);

    for (SPPFPN p : sppfn.packNS) {
      boolean isCyclicP = sppfCyclic.get(p.number);

      sppfOut.println("\"" + p.number + "\"" + packNodeStyle + " [label=\"" + p.number + ": " + p.gn.toStringAsProduction() + " , " + p.pivot + "\"]");
      if (isCyclicP) sppfOut.println(cycleStyle);
      if (!sppfRootReachable.get(p.number)) sppfOut.println(unreachablePackNodeStyle);

      if (cbD.contains(p) && cbDPrime.contains(p))
        sppfOut.println(deletedAndDeletedPrimePackNodeStyle);
      else if (cbD.contains(p))
        sppfOut.println(deletedPackNodeStyle);
      else if (cbDPrime.contains(p)) sppfOut.println(deletedPrimePackNodeStyle);
      sppfOut.println("\"" + sppfn.number + "\"" + "->" + "\"" + p.number + "\"");

      if (isCyclicP)
        sppfOut.println(cycleStyle);
      else if (isAmbiguous) sppfOut.println(ambiguousStyle);

      if (p.leftChild != null) {
        sppfOut.println("\"" + p.number + "\"" + "->" + "\"" + p.leftChild.number + "\"");
        if (isCyclicP && sppfCyclic.get(p.leftChild.number)) sppfOut.println(cycleStyle);
      }

      sppfOut.println("\"" + p.number + "\"" + "->" + "\"" + p.rightChild.number + "\"");
      if (isCyclicP && sppfCyclic.get(p.rightChild.number)) sppfOut.println(cycleStyle);
    }
  }

  /* SPPF cycle breaking **********************************************************************/

  private final Deque<SPPFNode> visitedDeque = new ArrayDeque<>(); // Only usedby sppfCycleRec to keep a list of visited nodes during descent
  private Set<SPPFNode> xNodesBeforeBreaking; // All cyclic nodes - only used by SPPF diagnostics
  private Set<SPPFN> yS; // Set of cYclic symbol or intermediate nodes; a subset of the X in Elizabeth's note
  private Set<SPPFPN> yP; // Set of cYclic packed nodes; Y = yS UP
  private Set<SPPFPN> cbD = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  private final Set<SPPFPN> cbDPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note
  private final Relation<SPPFNode, SPPFNode> sppfReachableSlow = new Relation<>();
  private RelationOverNaturals sppfReachable;
  private final BitSet sppfCyclic = new BitSet();
  private BitSet sppfRootReachable;
  private boolean cycleBreakTrace;
  private TraversalKind cycleBreakTraversalKind;
  private boolean cycleBreakLone;
  private boolean cycleBreakSibling;

  private void sppfComputeReachabilitySlow() {
    sppfReachableSlow.clear();
    for (var n : sppf.keySet())
      for (var p : n.packNS) {
        if (cbD.contains(p)) {
          // System.out.println("Skipping Deleted packed node " + p);
          continue;
        }
        sppfReachableSlow.add(n, p);
        if (p.leftChild != null) sppfReachableSlow.add(p, p.leftChild);
        sppfReachableSlow.add(p, p.rightChild);
      }
    // System.out.println("After initialising sppfReachable, cardinality is " + sppfReachableSlow.getDomain().size());
    sppfReachableSlow.transitiveClosure();
    // System.out.println("After transitive closure of sppfReachable, contents are:\n" + sppfReachableSlow);

    sppfRootReachable = new BitSet();
    for (var r : sppfReachableSlow.get(sppfRootNode))
      if (r instanceof SPPFN)
        sppfRootReachable.set(((SPPFN) r).number);
      else
        sppfRootReachable.set(((SPPFPN) r).number);
    // System.out.println("Root reachable set: " + sppfRootReachable);
    sppfCyclic.clear();
    for (var n : sppfReachableSlow.getDomain())
      if (sppfReachableSlow.get(n).contains(n)) if (n instanceof SPPFN)
        sppfCyclic.set(((SPPFN) n).number);
      else
        sppfCyclic.set(((SPPFPN) n).number);

    // System.out.println("Cyclic node set: " + sppfCyclic);
  }

  private void sppfComputeReachability() {
    // Count SPPF nodes
    // Allocate SPPF node count bitsets of size SPPF node count
    // Load adjacencies
    // Run Warshall's what-did-you-expect algorithm

    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping reachability analysis");
      return;
    }

    sppfReachable.clear();
    // System.out.println("After clearing sppfReachable, contents are:\n" + sppfReachable);
    for (var n : sppf.keySet())
      for (var p : n.packNS) {
        if (cbD.contains(p)) {
          // System.out.println("Skipping Deleted packed node " + p);
          continue;
        }

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

    // System.out.println("Cyclic node set (fast): " + sppfCyclic);
  }

  @Override
  public void sppfPrintCyclicNodes() {
    sppfComputeReachability();
    printCyclic();
  }

  private void printCyclic() {
    System.out.println(sppfCyclic.isEmpty() ? "There are no cyclic nodes" : "Cyclic nodes are");
    for (int i = 0; i < sppfCyclic.length(); i++)
      if (sppfCyclic.get(i)) System.out.println("  " + i);
  }

  public void loadXPartitions() {
    // In this implementation, X is represented by its packed node and symbol node partitions xP and xS
    yP = new HashSet<>();
    yS = new HashSet<>();
    for (var n : sppf.keySet())
      if (sppfCyclic.get(n.number)) {
        yS.add(n);
        for (var p : n.packNS)
          if (sppfCyclic.get(p.number)) {
            // xNodesBeforeBreaking.add(p);
            yP.add(p);
          }
      }
  }

  void trace(String msg) {
    if (cycleBreakTrace) System.out.println(msg);
  }

  // child predicate true - remove from X
  // sibling predicate true - remove from X
  //
  // child predicate true and sibling predicate true - add to D'
  // child predicate false and sibling predicate true - add to D

  private int cycleBreakPass;

  boolean sppfCycleBreak() {
    trace("SPPF cycle break pass " + cycleBreakPass++ + " with xS:" + yS + " xP:" + yP + " D:" + cbD + " D':" + cbDPrime);
    for (var v : new HashSet<>(yS)) {
      trace("Checking symbol node " + v + " with child predicate " + noChildInX(v, yP));
      if (noChildInX(v, yP)) {
        yS.remove(v);
        if (cycleBreakTrace) System.out.println("Removed from xS: " + v);
        return true;
      }
    }

    for (var v : new HashSet<>(yP)) {
      trace("Checking packed node " + v + " with child predicate " + noChildInX(v, yS) + " and sibling predicate " + someSiblingNotInX(v, yP));

      if (noChildInX(v, yS) || someSiblingNotInX(v, yP)) {
        yP.remove(v);
        if (cycleBreakTrace) System.out.println("Removed from xP: " + v);
        if (noChildInX(v, yS) && someSiblingNotInX(v, yP)) cbDPrime.add(v);
        if (!noChildInX(v, yS) && someSiblingNotInX(v, yP)) cbD.add(v);
        return true;
      }
    }
    return false;
  }

  private void cbUpdate(SPPFPN v, Set<SPPFPN> Dset) {
    Dset.add(v);
    yP.remove(v);
    if (cycleBreakTrace) System.out.println("Removed from xP: " + v);
  }

  private void cbUpdate(SPPFN v) {
    yS.remove(v);
    if (cycleBreakTrace) System.out.println("Removed from xS: " + v);
  }

  // children(v) ∩ Xi = ∅ (symbol node child predicate)
  public boolean noChildInX(SPPFN sppfN, Set<SPPFPN> yP) {
    for (var pn : sppfN.packNS)
      if (yP.contains(pn)) return false;
    return true;
  }

  // sibling(v) ̸⊆ Xi (packed node sibling predicate)
  public boolean someSiblingNotInX(SPPFPN sppfPN, Set<SPPFPN> yP) {
    for (var p : sppfPN.parent.packNS)
      if (p != sppfPN && !yP.contains(p)) return true;
    return false;
  }

  // children(v) ∩ Xi = ∅ (packed node child predicate)
  public boolean noChildInX(SPPFPN sppfPN, Set<SPPFN> yS) {
    if (sppfPN.leftChild != null && yS.contains(sppfPN.leftChild)) return false;
    if (yS.contains(sppfPN.rightChild)) return false;
    return true;
  }

  @Override
  public void sppfBreakCycles(boolean cycleBreakTrace, TraversalKind cycleBreakTraversalKind, boolean cycleBreakLone, boolean cycleBreakSibling) {
    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping cycle breaking");
      return;
    }
    this.cycleBreakTrace = cycleBreakTrace;
    this.cycleBreakTraversalKind = cycleBreakTraversalKind;
    this.cycleBreakLone = cycleBreakLone;
    this.cycleBreakSibling = cycleBreakSibling;

    cbD = new HashSet<>();

    System.out.println("Cycle breaking SPPF with " + sppfCardinality + " nodes - finding cyclic nodes");

    var reachabilityStartTime = System.nanoTime();

    // Load all cyclic nodes to X (in detail to the xS and xP partitions)
    sppfComputeReachability();

    var cycleBreakStartTime = System.nanoTime();
    System.out.println(
        "!!! Cycle detect time in milliseconds (note - all SPPF reachability so slow): " + timeAsMilliseconds(reachabilityStartTime, cycleBreakStartTime));
    loadXPartitions();

    if (cycleBreakTrace) {
      System.out.println("Before cycle breaking, |Xs| = " + yS.size() + " |Xp| = " + yP.size());

      System.out.print("Xs is ");
      if (yS.size() == 0)
        System.out.print("empty");
      else
        for (var d : yS)
          System.out.print("\n  " + d);

      System.out.print("\nXp is ");
      if (yP.size() == 0)
        System.out.print("empty");
      else
        for (var d : yP)
          System.out.print("\n  " + d);

      System.out.println();
    }

    cycleBreakPass = 1;
    while (sppfCycleBreak())
      ;

    var cycleBreakEndTime = System.nanoTime();
    System.out.println("!!! Cycle break time in milliseconds: " + timeAsMilliseconds(cycleBreakStartTime, cycleBreakEndTime));

    if (cycleBreakTrace) {
      System.out.println("After cycle breaking, |Xs|=" + yS.size() + " |Xp|=" + yP.size() + " |D|=" + cbD.size() + " |D'|=" + cbDPrime.size());

      System.out.print("Xs is ");
      if (yS.size() == 0)
        System.out.print("empty");
      else
        for (var d : yS)
          System.out.print("\n  " + d);

      System.out.print("\nXp is ");
      if (yP.size() == 0)
        System.out.print("empty");
      else
        for (var d : yP)
          System.out.print("\n  " + d);

      System.out.print("\nD is ");
      if (cbD.size() == 0)
        System.out.print("empty");
      else
        for (var d : cbD)
          System.out.print("\n  " + d);

      System.out.print("\nD' is ");
      if (cbDPrime.size() == 0)
        System.out.print("empty");
      else
        for (var d : cbDPrime)
          System.out.print("\n  " + d);

      System.out.println("\nRecomputing cyclic nodes");
      sppfComputeReachability();
      printCyclic();
    }
  }

  class Configuration {
    final Set<SPPFPN> xP;
    final Set<SPPFN> xS;
    final Set<SPPFPN> d;
    final Set<SPPFPN> dPrime;

    public Configuration(Set<SPPFPN> xP, Set<SPPFN> xS, Set<SPPFPN> d, Set<SPPFPN> dPrime) {
      super();
      this.xP = xP;
      this.xS = xS;
      this.d = d;
      this.dPrime = dPrime;
    }

    public Configuration(Configuration c) {
      xP = new HashSet<>(c.xP);
      xS = new HashSet<>(c.xS);
      d = new HashSet<>(c.d);
      dPrime = new HashSet<>(c.dPrime);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((d == null) ? 0 : d.hashCode());
      result = prime * result + ((dPrime == null) ? 0 : dPrime.hashCode());
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
      if (dPrime == null) {
        if (other.dPrime != null) return false;
      } else if (!dPrime.equals(other.dPrime)) return false;
      if (xP == null) {
        if (other.xP != null) return false;
      } else if (!xP.equals(other.xP)) return false;
      if (xS == null) {
        if (other.xS != null) return false;
      } else if (!xS.equals(other.xS)) return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("< xP:");
      builder.append(xP);
      // builder.append("\n");
      builder.append(" xS:");
      builder.append(xS);
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

  boolean breakCyclesRelationTrace = false;

  @Override
  public void sppfBreakCyclesRelation() {
    Configuration c, cp;
    sppfComputeReachability(); // computes sppfCyclic (set of cyclic SPPF nodes)
    loadXPartitions(); // Load X from computed cyclic nodes as partitions xP and xS

    Configuration c_0 = new Configuration(yP, yS, new HashSet<SPPFPN>(), new HashSet<SPPFPN>());
    q.add(c_0); // enqueue start element
    System.out.println("C_0: " + c_0);

    while (q.size() != 0) {
      c = q.removeFirst(); // deqeue c
      if (breakCyclesRelationTrace) System.out.println("** Processing: " + c);

      for (var v : c.xS) {
        if (breakCyclesRelationTrace) System.out.println("Checking symbol node: " + v);
        if (noChildInX(v, c.xP)) {
          zcbUpdate(c, v, false, false);
        }
      }

      for (var v : c.xP) {
        if (breakCyclesRelationTrace) System.out.println("Checking packed node: " + v);
        if (!noChildInX(v, c.xS) && someSiblingNotInX(v, c.xP)) {
          zcbUpdate(c, v, true, false);
        } else if (someSiblingNotInX(v, c.xP)) {
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
      sppfOut = new PrintStream(new File("sppfCycleBreakRelation.dot"));
      sppfOut.println("digraph \"SPPF\" {\n" + "rankdir=\"LR\" "
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.3 color=gray]");
      for (var de : breakRelation.getDomain()) {
        for (var cde : breakRelation.get(de))
          sppfOut.println("\"" + de + "\"->\"" + cde + "\"");
        sppfOut.println();
      }
      sppfOut.println("}");
      sppfOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to sppfCycleBreakRelation.dot");
    }

  }

  private void zcbUpdate(Configuration c, SPPFNode v, Boolean D, Boolean DPrime) {
    var cp = new Configuration(c);
    if (v instanceof SPPFPN)
      cp.xP.remove(v);
    else
      cp.xS.remove(v);
    if (D) cp.d.add((SPPFPN) v);
    if (DPrime) cp.dPrime.add((SPPFPN) v);
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
  Map<Integer, Set<SPPFN>> paraterminalInstances = new TreeMap<>();
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
        System.out.println(s.li + "," + s.ri + "  " + s.gn.elm);
    }
  }

  private void sppfCollectParaterminalsRec(SPPFN sppfn) {
    // System.out.println("Collect paraterminals at " + sppfn);
    if (visitedSPPFNodes.get(sppfn.number)) return;
    visitedSPPFNodes.set(sppfn.number);

    if (isSymbol(sppfn) && cfgRules.paraterminalElements.contains(sppfn.gn.elm)) {
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

  @Override
  public void sppfPrintParasentences() {
    if (sppf == null || sppfRootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping parasentence printing");
      return;
    }
    System.out.println("Parasentences");
    visitedSPPFNodes.clear();
    parasentence = new SPPFN[100 * tokens.length + 1];
    psCall = 0;
    parasentences = new HashSet<>();
    // sppfCollectParasentencesRec(sppfRootNode, 0);
    sppfCollectParasentencesIter(sppfRootNode, 0);
    // System.out.println(parasentences);
    for (var s : parasentences) {
      for (var n : s)
        System.out.print(n.li + "," + n.ri + ":" + n.gn.elm.str + "  ");
      System.out.println();
    }
  }

  /*
   * Iterative explorationofderivations
   *
   * If packedNode.size == 1 then just descend
   *
   */
  private void sppfCollectParasentencesIter(SPPFN sppfRootNode2, int i) {
  }

  private int psCall;

  private int sppfCollectParasentencesRec(SPPFN node, int parasentenceIndex) {
    int thisCall = ++psCall;
    System.out.println(thisCall + " collect parasentences at " + node + " with parasentenceIndex " + parasentenceIndex);
    int entrySentenceIndex = parasentenceIndex;
    if (visitedSPPFNodes.get(node.number)) {
      System.out.println("Already called; abort");
      return parasentenceIndex;
    }
    visitedSPPFNodes.set(node.number);

    if (node.packNS.isEmpty() || (isSymbol(node) && cfgRules.paraterminalElements.contains(node.gn.elm))) {
      if (!(node.packNS.isEmpty() && node.gn.elm.kind == CFGKind.EPS)) {
        System.out.println("Extending with " + node.gn.elm.str);
        parasentence[parasentenceIndex++] = node;
        if (node.ri == tokens.length - 1) addParasentence(parasentenceIndex);
      }
    } else
      for (var p : node.packNS) {
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
    List<SPPFN> parasentenceList = new LinkedList<>();
    for (int i = 0; i < length; i++)
      parasentenceList.add(parasentence[i]);
    parasentences.add(parasentenceList);
  }
}