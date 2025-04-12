package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFPackedNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSS;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSEdge;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSNode;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;
import uk.ac.rhul.cs.csle.art.util.tasks.DescriptorGLL;

public class GLLBaseLine extends AbstractParser {
  Deque<DescriptorGLL> descriptorQueue;
  CFGNode grammarNode;
  GSSNode stackNode;
  AbstractDerivationNode derivationNode;

  @Override
  public void parse(AbstractLexer lexer) {
    this.lexer = lexer;
    derivations = new SPPF(this);
    descriptors = new HashSet<>();
    descriptorQueue = new LinkedList<>();
    gss = new GSS(cfgRules);
    for (CFGNode p = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt; p != null; p = p.alt)
      queueDesc(p.seq, 0, gss.root, null);
    inLanguage = false;

    nextDescriptor: while (dequeueDesc())
      while (true) {
        switch (grammarNode.element.kind) {
        case ALT:
          for (CFGNode tmp = grammarNode; tmp != null; tmp = tmp.alt)
            queueDesc(tmp.seq, tokenIndex, stackNode, derivationNode);
          continue nextDescriptor;
        case B, T, TI, C:
          if (lexer.tokens[tokenIndex] == grammarNode.element.number) {
            Util.trace(8, 0, "Matched " + grammarNode.element);
            derivationUpdate();
            tokenIndex++;
            grammarNode = grammarNode.seq;
            break;
          } else
            continue nextDescriptor;
        case EPS:
          derivationUpdateEpsilon();
          grammarNode = grammarNode.seq;
          break;
        case N:
          call(grammarNode);
          continue nextDescriptor;
        case END:
          ret();
          continue nextDescriptor;
        default:
          break;
        }
      }
    if (inLanguage)
      Util.trace(1, "Parser accept");
    else
      Util.error(Util.echo("GLLBL " + "syntax error", lexer.leftIndices[derivations.widestIndex()], lexer.inputString));
  }

  /* Thread handling *********************************************************/
  void queueDesc(CFGNode gn, int i, GSSNode gssN, AbstractDerivationNode derivationNode) {
    DescriptorGLL tmp = new DescriptorGLL(gn, i, gssN, derivationNode);
    if (descriptors.add(tmp)) descriptorQueue.addFirst(tmp);
  }

  boolean dequeueDesc() {
    DescriptorGLL tmp = descriptorQueue.poll();
    if (tmp == null) return false;
    grammarNode = tmp.grammarNode;
    tokenIndex = tmp.tokenIndex;
    stackNode = tmp.stackNode;
    derivationNode = tmp.derivationNode;
    return true;
  }

  /* Stack handling **********************************************************/
  void call(CFGNode gn) {
    GSSNode gssNode = gss.find(gn.seq, tokenIndex);
    GSSEdge gssEdge = new GSSEdge(stackNode, derivationNode);
    if (!gssNode.edges.contains(gssEdge)) {
      gssNode.edges.add(gssEdge);
      for (AbstractDerivationNode rc : gssNode.pops)
        queueDesc(gn.seq, rc.getRightExtent(), stackNode, derivations.update(gn.seq, derivationNode, rc));
    }
    for (CFGNode p = cfgRules.elementToNodeMap.get(gn.element).alt; p != null; p = p.alt)
      queueDesc(p.seq, tokenIndex, gssNode, null);
  }

  void ret() {
    if (stackNode.equals(gss.root)) { // Deque base
      if (cfgRules.acceptingNodeNumbers.contains(grammarNode.num) && (tokenIndex == lexer.tokens.length - 1)) {
        derivations.setRoot(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), lexer.tokens.length - 1);
        inLanguage = true;
      }
      return; // End of parse
    }
    stackNode.pops.add(derivationNode);
    for (GSSEdge e : stackNode.edges)
      queueDesc(stackNode.grammarNode, tokenIndex, e.dst, derivations.update(stackNode.grammarNode, e.derivationNode, derivationNode));
  }

  /* Derivation handling *****************************************************/
  void derivationUpdate() {
    derivationNode = derivations.update(grammarNode.seq, derivationNode,
        derivations.find(cfgRules.elementToNodeMap.get(grammarNode.element), tokenIndex, tokenIndex + 1));
  }

  void derivationUpdateEpsilon() {
    derivationNode = derivations.update(grammarNode.seq, derivationNode,
        derivations.find(cfgRules.elementToNodeMap.get(grammarNode.element), tokenIndex, tokenIndex));
  }

  @Override
  public void statistics(Statistics currentstatistics) {
    int gssEdgeCount = 0, popCount = 0;
    for (GSSNode g : gss.nodes.keySet()) {
      gssEdgeCount += g.edges.size();
      popCount += g.pops.size();
    }
    ScriptTermInterpreter.currentStatistics.put("descriptorCount", descriptors.size());
    ScriptTermInterpreter.currentStatistics.put("gssNodeCount", gss.nodes.keySet().size());
    ScriptTermInterpreter.currentStatistics.put("gssEdgeCount", gssEdgeCount);
    ScriptTermInterpreter.currentStatistics.put("popCount", popCount);

    int sppfEpsilonNodeCount = 0, sppfTerminalNodeCount = 0, sppfNonterminalNodeCount = 0, sppfIntermediateNodeCount = 0, sppfPackNodeCount = 0,
        sppfAmbiguityCount = 0, sppfEdgeCount = 0;
    for (SPPFSymbolNode s : ((SPPF) derivations).nodes.keySet()) {
      switch (s.grammarNode.element.kind) {
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
    ScriptTermInterpreter.currentStatistics.put("sppfSymbolPlusIntermediateNodeCount", ((SPPF) derivations).nodes.keySet().size());
    ScriptTermInterpreter.currentStatistics.put("sppfPackNodeCount", sppfPackNodeCount);
    ScriptTermInterpreter.currentStatistics.put("sppfAmbiguityCount", sppfAmbiguityCount);
    ScriptTermInterpreter.currentStatistics.put("sppfEdgeCount", sppfEdgeCount);
    // loadPoolAllocated(-1);
    // loadHashCounts(-20, -21, -22, -23, -24, -25, -26);
  }
}