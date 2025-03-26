package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.gss.GSS2Dot;
import uk.ac.rhul.cs.csle.art.util.gss.GSSE;
import uk.ac.rhul.cs.csle.art.util.gss.GSSN;
import uk.ac.rhul.cs.csle.art.util.process.Desc;
import uk.ac.rhul.cs.csle.art.util.sppf.SPPF;
import uk.ac.rhul.cs.csle.art.util.sppf.SPPFN;
import uk.ac.rhul.cs.csle.art.util.sppf.SPPFPN;

public class GLLBaseLineRefactored extends AbstractParser {
  public SPPF sppf = new SPPF(cfgRules);

  /* Parser ******************************************************************/
  @Override
  public void parse() {
    descS = new HashSet<>();
    descQ = new LinkedList<>();
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
    if (!inLanguage) {
      System.out.println(Util.echo("GLLBL " + "syntax error", leftIndices[sppfWidestIndex()], inputString));
      // System.out.println("Widest index: " + sppfWidestIndex());
    } else
      Util.trace(3, 0, "Accept\n");

    loadCounts();
    sppf.numberSPPFNodes();
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
        sppf.rootNode = sppf.nodes.get(new SPPFN(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), 0, tokens.length - 1));
        inLanguage = true;
      }
      return; // End of parse
    }
    sn.pops.add(dn);
    for (GSSE e : sn.edges)
      queueDesc(sn.gn, i, e.dst, sppfUpdate(sn.gn, e.sppfnode, dn));
  }

  /* Derivation handling *****************************************************/
  SPPFN sppfFind(CFGNode dn, int li, int ri) {
    // System.out.print("sppfFind with dn " + dn.toStringAsProduction() + " with extents " + li + "," + ri);

    SPPFN tmp = new SPPFN(dn, li, ri);
    if (!sppf.nodes.containsKey(tmp)) {
      sppf.nodes.put(tmp, tmp);
      // System.out.print(" added " + tmp);
    }
    // System.out.println(" resulting sppf\n" + sppf.nodes.keySet());
    return sppf.nodes.get(tmp);
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
    for (SPPFN s : sppf.nodes.keySet())
      if (ret < s.ri) ret = s.ri;
    return ret;
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
    for (SPPFN s : sppf.nodes.keySet()) {
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

    loadSPPFCounts(sppfEpsilonNodeCount, sppfTerminalNodeCount, sppfNonterminalNodeCount, sppfIntermediateNodeCount, sppf.nodes.keySet().size(),
        sppfPackNodeCount, sppfAmbiguityCount, sppfEdgeCount);
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
    if (sppf == null || sppf.rootNode == null) {
      Util.warning("Current parser does not have a current SPPF - skipping SPPF printing");
      return;
    }
    for (var n : sppf.nodes.keySet()) {
      System.out.println(n);
      for (var pn : n.packNS)
        System.out.println(pn);
    }
  }
}