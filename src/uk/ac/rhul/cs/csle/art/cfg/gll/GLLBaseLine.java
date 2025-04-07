package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFPackedNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;
import uk.ac.rhul.cs.csle.art.util.process.Descriptor;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSEdge;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSNode;

public class GLLBaseLine extends AbstractParser {
  private final BitSet visitedSPPFNodes = new BitSet(), suppressedSPPFNode = new BitSet();
  SPPF sppf;

  /* Parser ******************************************************************/
  @Override
  public void parse(AbstractLexer lexer) {
    this.lexer = lexer;
    derivations = sppf = new SPPF(cfgRules);
    descS = new HashSet<>();
    descQ = new LinkedList<>();
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
    sppf.numberNodes();
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
        sppfRootNode = sppf.nodes.get(new SPPFSymbolNode(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), 0, lexer.tokens.length - 1));
        inLanguage = true;
      }
      return; // End of parse
    }
    sn.pops.add(dn);
    for (GSSEdge e : sn.edges)
      queueDesc(sn.grammarNode, tokenIndex, e.dst, sppfUpdate(sn.grammarNode, e.sppfnode, dn));
  }

  /* Derivation handling *****************************************************/
  SPPFSymbolNode sppfRootNode;

  SPPFSymbolNode sppfFind(CFGNode dn, int li, int ri) {
    // System.out.print("sppfFind with dn " + dn.toStringAsProduction() + " with extents " + li + "," + ri);

    SPPFSymbolNode tmp = new SPPFSymbolNode(dn, li, ri);
    if (!sppf.nodes.containsKey(tmp)) {
      sppf.nodes.put(tmp, tmp);
      // System.out.print(" added " + tmp);
    }
    // System.out.println(" resulting sppf\n" + sppf.nodes,keySet());
    return sppf.nodes.get(tmp);
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
    for (SPPFSymbolNode s : sppf.nodes.keySet())
      if (ret < s.ri) ret = s.ri;
    return ret;
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
    for (SPPFSymbolNode s : sppf.nodes.keySet()) {
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

    loadSPPFCounts(sppfEpsilonNodeCount, sppfTerminalNodeCount, sppfNonterminalNodeCount, sppfIntermediateNodeCount, sppf.nodes.keySet().size(),
        sppfPackNodeCount, sppfAmbiguityCount, sppfEdgeCount);
    // loadPoolAllocated(-1);
    // loadHashCounts(-20, -21, -22, -23, -24, -25, -26);
  }

}