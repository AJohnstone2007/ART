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
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexemeKind;
import uk.ac.rhul.cs.csle.art.util.Relation;
import uk.ac.rhul.cs.csle.art.util.Util;

public class GLLBaseLine extends ParserBase {
  private final BitSet visitedSPPFNodes = new BitSet(), suppressedSPPFNode = new BitSet(), deletedSPPFNode = new BitSet();

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
    gssRoot = new GSSN(grammar.endOfStringNode, 0);
    gss.put(gssRoot, gssRoot);
    i = 0;
    sn = gssRoot;
    dn = null;
    for (CFGNode p = grammar.rules.get(grammar.startNonterminal).alt; p != null; p = p.alt)
      queueDesc(p.seq, i, sn, dn);
    inLanguage = false;

    nextDescriptor: while (dequeueDesc())
      while (true) {
        switch (gn.elm.kind) {
        case B, T, TI, C:
          if (input[i] == gn.elm.ei) {
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
    for (CFGNode p = grammar.rules.get(gn.elm).alt; p != null; p = p.alt)
      queueDesc(p.seq, i, gssN, null);
  }

  void ret() {
    if (sn.equals(gssRoot)) { // Stack base
      if (grammar.acceptingNodeNumbers.contains(gn.num) && (i == input.length - 1)) {
        sppfRootNode = sppf.get(new SPPFN(grammar.rules.get(grammar.startNonterminal), 0, input.length - 1));
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
    SPPFN tmp = new SPPFN(dn, li, ri);
    if (!sppf.containsKey(tmp)) sppf.put(tmp, tmp);
    return sppf.get(tmp);
  }

  SPPFN sppfUpdate(CFGNode gn, SPPFN ln, SPPFN rn) {
    SPPFN ret = sppfFind(gn.elm.kind == CFGKind.END ? gn.seq : gn, ln == null ? rn.li : ln.li, rn.ri);
    ret.packNS.add(new SPPFPN(gn, ln == null ? rn.li : ln.ri, ln, rn));
    return ret;
  }

  void du(int width) {
    dn = sppfUpdate(gn.seq, dn, sppfFind(gn, i, i + width));
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
    if (visitedSPPFNodes.get(sppfn.number)) return "-!-"; // Util.fatal("derivationAsTermRec() found cycle in derivation");

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
      childrenFromParent.add(grammar.iTerms.findTerm(constructor, children));
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

  int nextFreeSPPFNodeNumber = 0;

  class SPPFNode { // Carrier to unify SPPFN and SPPPPN
  };

  class SPPFN extends SPPFNode {
    public final int number; // to allow a bitset to be used as visited set
    public final CFGNode gn;
    public final int li;
    public final int ri;
    public final Set<SPPFPN> packNS = new HashSet<>();

    public SPPFN(CFGNode gn, int li, int ri) {
      super();
      this.number = nextFreeSPPFNodeNumber++;
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
      sb.append("(");
      if (gn == null)
        sb.append("NULL");
      else
        sb.append(gn.toString());
      sb.append(", ");
      sb.append(li);
      sb.append(", ");
      sb.append(ri);
      sb.append(")");

      if (sppfReachable != null && sppfReachable.get(this).contains(this)) sb.append(" (cyclic)");
      return sb.toString();
    }
  }

  class SPPFPN extends SPPFNode {
    public final int number; // to allow a bitset to be used as visited set
    public final CFGNode gn;
    public final int pivot;
    public final SPPFN leftChild;
    public final SPPFN rightChild;
    public boolean suppressed = false;

    public SPPFPN(CFGNode gn, int pivot, SPPFN leftChild, SPPFN rightChild) {
      super();
      this.number = nextFreeSPPFNodeNumber++;
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
      if (pivot != other.pivot) return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append(gn.toStringAsProduction());
      sb.append(", " + pivot);
      sb.append("]");
      if (sppfReachable != null && sppfReachable.get(this).contains(this)) sb.append(" (cyclic)");
      if (suppressed) sb.append(" (suppressed)");
      return sb.toString();
    }
  }

  /* GSS and SPPF rendering *******************************************************************/
  private void sppfPrintRec(SPPFN sppfn) {
    if (visitedSPPFNodes.get(sppfn.number)) return;
    visitedSPPFNodes.set(sppfn.number);
    System.out.println(sppfn);
    for (var pn : sppfn.packNS)
      System.out.println(pn);

    for (var pn : sppfn.packNS) {
      if (pn.leftChild != null) sppfPrintRec(pn.leftChild);
      sppfPrintRec(pn.rightChild);
    }
  }

  @Override
  public void sppfPrint() {
    if (sppfRootNode == null) Util.fatal("Current parser does not have a current SPPF that can be printed");
    sppfComputeReachability();

    // Output SPPF
    visitedSPPFNodes.clear();
    sppfPrintRec(sppfRootNode);
  }

  @Override
  public void gss2Dot() {
    new GSS2Dot(gss, "gss.dot");
  }

  @Override
  public void sppf2Dot() {
    sppfComputeReachability();
    // System.out.println("SPPF reachability\n" + sppfReachable.toString());
    new SPPF2Dot(sppf, sppfRootNode, "sppf_full.dot", true, true, true); // full SPPF
    new SPPF2Dot(sppf, sppfRootNode, "sppf_core.dot", false, true, true); // core SPPF - only nodes reachable from (S,0,n)
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

  class SPPF2Dot {
    final String packNodeStyle = "[style=rounded]";
    final String ambiguousStyle = "[color=orange]";
    final String cycleStyle = "[color=red]";
    final String intermediateNodeStyle = "[style=filled fillcolor=grey92]";
    final String symbolNodeStyle = "";
    PrintStream sppfOut = null;
    private final Map<SPPFN, SPPFN> sppf;
    private final boolean showIndicies;
    private final boolean showIntermediates;

    private String renderSPPFPackNode(SPPFN parent, SPPFPN node) {
      return renderSPPFPackNodeLabel(parent, node) + "[label = \"" + node.gn.toStringAsProduction() + " " + node.pivot + "\" ]" + packNodeStyle;
    }

    private String renderSPPFPackNodeLabel(SPPFN parent, SPPFPN node) {
      return "\"" + parent.gn.toStringAsProduction() + "," + parent.li + "," + parent.ri + " " + node.gn.toStringAsProduction() + " " + node.pivot + "\"";
    }

    private String renderSPPFNode(SPPFN node) {
      // if (node == null) return "null";
      return renderSPPFNodeLabel(node) + (node.packNS.size() > 1 ? ambiguousStyle : "") + (isSymbol(node) ? symbolNodeStyle : intermediateNodeStyle);
    }

    private boolean isSymbol(SPPFN node) {
      return node.packNS.size() == 0 || CFGRules.isLHS(node.gn);
    }

    private String renderSPPFNodeLabel(SPPFN node) {
      String label;
      if (node == null)
        return "NULL node";
      else {
        if (node.gn == null)
          label = "NULL label";
        else if (node.packNS.size() == 0)
          label = node.gn.toString();
        else if (CFGRules.isLHS(node.gn)) {
          label = node.gn.elm.str;
        } else
          label = node.gn.toStringAsProduction();
        return "\"" + label + (showIndicies ? (" " + node.li + "," + node.ri) : "") + "\"";
      }
    }

    public SPPF2Dot(Map<SPPFN, SPPFN> sppf, SPPFN rootNode, String filename, boolean full, boolean showIndicies, boolean showIntermediates) {
      this.sppf = sppf;
      this.showIndicies = showIndicies;
      this.showIntermediates = showIntermediates;

      if (sppf == null) return;
      sppfComputeReachability();
      visitedSPPFNodes.clear();
      try {
        sppfOut = new PrintStream(new File(filename));
        sppfOut.println("digraph \"SPPF\" {\n"
            + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.1 color=gray]");
        if (sppf != null) if (full)
          fullSPPF(rootNode);
        else
          coreSPPFRec(rootNode);
        sppfOut.println("}");
        sppfOut.close();
      } catch (FileNotFoundException e) {
        System.out.println("Unable to write SPPF visualisation to " + filename);
      }
    }

    private void coreSPPFRec(SPPFN sppfn) {
      if (sppf == null || sppfn == null) return;

      if (visitedSPPFNodes.get(sppfn.number)) return;
      visitedSPPFNodes.set(sppfn.number);

      sppfOut.println(renderSPPFNode(sppfn));
      for (SPPFPN p : sppfn.packNS) {
        boolean isCyclicP = isCyclic(p);

        sppfOut.println(renderSPPFPackNode(sppfn, p));
        if (isCyclicP) sppfOut.println(cycleStyle);
        sppfOut.println(renderSPPFNodeLabel(sppfn) + "->" + renderSPPFPackNodeLabel(sppfn, p));
        if (isCyclicP)
          sppfOut.println(cycleStyle);
        else if (sppfn.packNS.size() > 1) sppfOut.println(ambiguousStyle);

        if (p.leftChild != null) {
          sppfOut.println(renderSPPFPackNodeLabel(sppfn, p) + "->" + renderSPPFNodeLabel(p.leftChild));
          if (isCyclicP) sppfOut.println(cycleStyle);
        }
        sppfOut.println(renderSPPFPackNodeLabel(sppfn, p) + "->" + renderSPPFNodeLabel(p.rightChild));
        if (isCyclicP) sppfOut.println(cycleStyle);

        if (p.leftChild != null) coreSPPFRec(p.leftChild);
        if (p.rightChild != null) coreSPPFRec(p.rightChild);
      }
    }

    private void fullSPPF(SPPFN rootnode) {
      if (sppf == null || rootnode == null) return;

      if (visitedSPPFNodes.get(rootnode.number)) return;
      visitedSPPFNodes.set(rootnode.number);

      for (SPPFN s : sppf.keySet()) {
        sppfOut.println(renderSPPFNode(s));
        for (SPPFPN p : s.packNS) {
          sppfOut.println(renderSPPFPackNode(s, p));
          sppfOut.println(renderSPPFNodeLabel(s) + "->" + renderSPPFPackNodeLabel(s, p) + (s.packNS.size() > 1 ? ambiguousStyle : ""));
          if (p.leftChild != null) sppfOut.println(renderSPPFPackNodeLabel(s, p) + "->" + renderSPPFNodeLabel(p.leftChild));
          sppfOut.println(renderSPPFPackNodeLabel(s, p) + "->" + renderSPPFNodeLabel(p.rightChild));
        }
      }
    }
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

  /* SPPF cycle breaking **********************************************************************/

  // Enumerate every derivation and check for cycles - may be veerrry slow
  Deque visitedStack = new ArrayDeque();

  private void sppfCycleRec(SPPFN sppfn) {
    if (visitedSPPFNodes.get(sppfn.number)) {
      System.out.print("SPPF cycle detected at node " + sppfn);
      for (var v : visitedStack)
        System.out.print(" " + v);
      System.out.println();
      return;
    }
    visitedSPPFNodes.set(sppfn.number);
    visitedStack.push(sppfn);
    // System.out.println("sc: " + sppfn);
    // for (var pn : sppfn.packNS)
    // System.out.println("sc: " + pn);

    for (var pn : sppfn.packNS) {
      visitedStack.push(pn);
      if (pn.leftChild != null) sppfCycleRec(pn.leftChild);
      sppfCycleRec(pn.rightChild);
      visitedStack.pop();
    }
    visitedSPPFNodes.clear(sppfn.number);
    visitedStack.pop();
  }

  @Override
  public void sppfPrintCycles() {
    if (sppfRootNode == null) Util.fatal("Current parser does not have a current SPPF that can be cycle checked");
    System.out.println("Cycle listing for SPPF");
    visitedSPPFNodes.clear();
    sppfCycleRec(sppfRootNode);
  }

  // Compute cyclic nodes by closure over immediate reachability
  Relation<SPPFNode, SPPFNode> sppfReachable;

  private boolean isCyclic(SPPFNode sppfn) {
    return sppfReachable.get(sppfn).contains(sppfn);
  }

  private void sppfComputeReachability() {
    sppfReachable = new Relation<>();
    for (var n : sppf.keySet())
      for (var p : n.packNS) {
        sppfReachable.add(n, p);
        if (p.leftChild != null) sppfReachable.add(p, p.leftChild);
        sppfReachable.add(p, p.rightChild);
      }
    sppfReachable.transitiveClosure();
  }

  Set<SPPFN> xS; // Set of cyclic symbol or intermediate nodes
  Set<SPPFPN> xP; // Set of cyclic packed nodes
  Set<SPPFNode> d; // Set of deleted cyclic nodes
  Set<SPPFNode> visitedW; // Control revisiting under packing and cycles for outer loop

  boolean changedOnW;

  void sppfCycleBreakTraverseWRec(SPPFN n) {
    if (visitedW.contains(n)) return;
    visitedW.add(n);

    boolean hasNotInXp = false;
    for (var p : n.packNS)
      if (!xP.contains(p)) {
        hasNotInXp = true;
        break;
      }

    if (hasNotInXp) for (var p : n.packNS)
      if (xP.contains(p)) {
        changedOnW = true;
        xP.remove(p);
        d.add(p);

        boolean changedOnV;
        do {
          changedOnV = false;
          for (var nn : xP)
            if (/* child check */ true) {
              changedOnV = true;
              d.remove(xP);
            }
          for (var nn : xS)
            if (/* child check */ true) {
              changedOnV = true;
              d.remove(xP);
            }
        } while (changedOnV);
      }

    // Now recurse through all pack nodes
    for (var pn : n.packNS) {
      if (pn.leftChild != null) sppfCycleBreakTraverseWRec(pn.leftChild);
      sppfCycleBreakTraverseWRec(pn.rightChild);
    }

    return ret;
  }

  void sppfCycleBreak() {
    xP = new HashSet<>();
    xS = new HashSet<>();
    d = new HashSet<>();

    for (var n : sppfReachable.getDomain())
      if (isCyclic(n)) {
        if (n instanceof SPPFN)
          xS.add((SPPFN) n);
        else
          xP.add((SPPFPN) n);
      }

    do {
      visitedW.clear();
    } while (sppfCycleBreakTraverseWRec(sppfRootNode));

    // Sanity check
    System.out.println("After cycle breaking, |Xs| = " + xS.size() + " |Xp| = " + xP.size());
  }
}
