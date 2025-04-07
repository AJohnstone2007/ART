package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFPackedNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;
import uk.ac.rhul.cs.csle.art.util.process.Descriptor;
import uk.ac.rhul.cs.csle.art.util.stacks.GSS;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSEdge;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSNode;

public class GLLBaseLineRefactored extends AbstractParser {
  public SPPF sppf;
  public GSS gss;

  /* Parser ******************************************************************/
  @Override
  public void parse(AbstractLexer lexer) {
    this.lexer = lexer;
    descriptorSeen = new HashSet<>();
    descriptorQueue = new LinkedList<>();
    sppf = new SPPF(cfgRules);
    gss = new GSS(cfgRules);
    tokenIndex = 0;
    stackNode = gss.root;
    derivationNode = null;
    for (CFGNode production = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt; production != null; production = production.alt)
      queueDescriptor(production.seq, tokenIndex, stackNode, derivationNode);
    inLanguage = false;

    nextDescriptor: while (dequeueDescriptor())
      while (true) {
        switch (grammarNode.element.kind) {
        case EPS:
          derivationUpdate(0);
          grammarNode = grammarNode.seq;
          break;
        case B, T, TI, C:
          if (lexer.tokens[tokenIndex] == grammarNode.element.number) {
            // System.out.println("Matched " + lexer.tokens[i]);
            derivationUpdate(1);
            tokenIndex++;
            grammarNode = grammarNode.seq;
            break;
          } else
            continue nextDescriptor;
        case N:
          call(grammarNode);
          continue nextDescriptor;
        case END:
          ret();
          continue nextDescriptor;
        case ALT:
          for (CFGNode production = grammarNode; production != null; production = production.alt)
            queueDescriptor(production.seq, tokenIndex, stackNode, derivationNode);
          continue nextDescriptor;
        default:
          break;
        }
      }
    if (inLanguage)
      Util.trace(3, 0, "Accept\n");
    else
      Util.trace(0, 0, Util.echo("GLLBL " + "syntax error before position " + sppf.widestIndex(), lexer.leftIndices[sppf.widestIndex()], lexer.inputString));

    loadCounts();
    sppf.numberNodes();
  }

  /* Thread handling *********************************************************/
  Set<Descriptor> descriptorSeen;
  Deque<Descriptor> descriptorQueue;
  CFGNode grammarNode;
  GSSNode stackNode;
  SPPFSymbolNode derivationNode;

  void queueDescriptor(CFGNode grammarNode, int i, GSSNode stackNode, SPPFSymbolNode drivationNode) {
    Descriptor tmp = new Descriptor(grammarNode, i, stackNode, drivationNode);
    if (descriptorSeen.add(tmp)) descriptorQueue.addFirst(tmp);
  }

  boolean dequeueDescriptor() {
    Descriptor tmp = descriptorQueue.poll();
    if (tmp == null) return false;
    grammarNode = tmp.grammarNode;
    tokenIndex = tmp.inputIndex;
    stackNode = tmp.stackNode;
    derivationNode = tmp.derivationNode;
    return true;
  }

  /* Stack handling **********************************************************/
  void call(CFGNode grammarNode) {
    GSSNode gssNode = gss.find(grammarNode.seq, tokenIndex);
    GSSEdge gssEdge = new GSSEdge(stackNode, derivationNode);
    if (!gssNode.edges.contains(gssEdge)) {
      gssNode.edges.add(gssEdge);
      for (SPPFSymbolNode rc : gssNode.pops)
        queueDescriptor(grammarNode.seq, rc.ri, stackNode, sppfUpdate(grammarNode.seq, derivationNode, rc));
    }
    for (CFGNode production = cfgRules.elementToNodeMap.get(grammarNode.element).alt; production != null; production = production.alt)
      queueDescriptor(production.seq, tokenIndex, gssNode, null);
  }

  void ret() {
    if (stackNode.equals(gss.root)) { // Deque base
      if (cfgRules.acceptingNodeNumbers.contains(grammarNode.num) && (tokenIndex == lexer.tokens.length - 1)) {
        sppf.rootNode = sppf.nodes.get(new SPPFSymbolNode(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), 0, lexer.tokens.length - 1));
        inLanguage = true;
      }
      return; // End of parse
    }
    stackNode.pops.add(derivationNode);
    for (GSSEdge e : stackNode.edges)
      queueDescriptor(stackNode.grammarNode, tokenIndex, e.dst, sppfUpdate(stackNode.grammarNode, e.sppfnode, derivationNode));
  }

  /* Derivation handling *****************************************************/
  SPPFSymbolNode sppfFind(CFGNode dn, int li, int ri) {
    // System.out.print("sppfFind with dn " + dn.toStringAsProduction() + " with extents " + li + "," + ri);

    SPPFSymbolNode tmp = new SPPFSymbolNode(dn, li, ri);
    if (!sppf.nodes.containsKey(tmp)) {
      sppf.nodes.put(tmp, tmp);
      // System.out.print(" added " + tmp);
    }
    // System.out.println(" resulting sppf\n" + sppf.nodes.keySet());
    return sppf.nodes.get(tmp);
  }

  SPPFSymbolNode sppfUpdate(CFGNode grammarNode, SPPFSymbolNode leftNode, SPPFSymbolNode rightNode) {
    SPPFSymbolNode ret = sppfFind(grammarNode.element.kind == CFGKind.END ? grammarNode.seq : grammarNode, leftNode == null ? rightNode.li : leftNode.li,
        rightNode.ri);
    // System.out.println(
    // "Updating SPPF node with gn " + gn.toStringAsProduction() + " and extents " + (ln == null ? rn.li : ln.li) + "," + rn.ri + " retrieves node " + ret);
    ret.packNodes.add(new SPPFPackedNode(grammarNode, leftNode == null ? rightNode.li : leftNode.ri, leftNode, rightNode));
    return ret;
  }

  void derivationUpdate(int width) {
    // dn = sppfUpdate(gn.seq, dn, sppfFind(gn, i, i + width)); // SLE paper version
    var gnp = cfgRules.elementToNodeMap.get(grammarNode.element);
    derivationNode = sppfUpdate(grammarNode.seq, derivationNode, sppfFind(gnp, tokenIndex, tokenIndex + width)); // singleton element version to reduce SPPF
                                                                                                                 // size and correct for
    // grammars with cycles
  }

  private void loadCounts() {
    ScriptTermInterpreter.currentStatistics.put("tweNodeCount", (long) lexer.tokens.length);
    ScriptTermInterpreter.currentStatistics.put("tweEdgeCount", lexer.tokens.length - 1);
    ScriptTermInterpreter.currentStatistics.put("tweLexCount", 1);

    int gssEdgeCount = 0, popCount = 0;
    for (GSSNode g : gss.nodes.keySet()) {
      gssEdgeCount += g.edges.size();
      popCount += g.pops.size();
    }
    ScriptTermInterpreter.currentStatistics.put("descriptorCount", descriptorSeen.size());
    ScriptTermInterpreter.currentStatistics.put("gssNodeCount", gss.nodes.keySet().size());
    ScriptTermInterpreter.currentStatistics.put("gssEdgeCount", gssEdgeCount);
    ScriptTermInterpreter.currentStatistics.put("popCount", popCount);

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

    ScriptTermInterpreter.currentStatistics.put("sppfEpsilonNodeCount", sppfEpsilonNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfTerminalNodeCount", sppfTerminalNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfNonterminalNodeCount", sppfNonterminalNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfIntermediateNodeCount", sppfIntermediateNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfSymbolPlusIntermediateNodeCount", sppf.nodes.keySet().size());
    ScriptTermInterpreter.currentStatistics.put("sppfPackNodeCount", sppfPackNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfAmbiguityCount", sppfAmbiguityCount);
    ScriptTermInterpreter.currentStatistics.put("sppfEdgeCount", sppfEdgeCount);
  }
}