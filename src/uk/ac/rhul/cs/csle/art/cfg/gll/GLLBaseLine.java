package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSS;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSEdge;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSNode;
import uk.ac.rhul.cs.csle.art.util.tasks.DescriptorGLL;

public class GLLBaseLine extends AbstractParser {
  Deque<DescriptorGLL> descriptorQueue;
  CFGNode grammarNode;
  GSSNode stackNode;
  SPPFSymbolNode derivationNode;

  @Override
  public void parse(AbstractLexer lexer) {
    this.lexer = lexer;
    derivations = sppf = new SPPF(this);
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
      Util.error(Util.echo("GLLBL " + "syntax error", lexer.leftIndices[sppf.widestIndex()], lexer.inputString));
  }

  /* Thread handling *********************************************************/
  void queueDesc(CFGNode gn, int i, GSSNode gssN, SPPFSymbolNode sppfN) {
    DescriptorGLL tmp = new DescriptorGLL(gn, i, gssN, sppfN);
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
      for (SPPFSymbolNode rc : gssNode.pops)
        queueDesc(gn.seq, rc.rightExtent, stackNode, sppf.update(gn.seq, derivationNode, rc));
    }
    for (CFGNode p = cfgRules.elementToNodeMap.get(gn.element).alt; p != null; p = p.alt)
      queueDesc(p.seq, tokenIndex, gssNode, null);
  }

  void ret() {
    if (stackNode.equals(gss.root)) { // Deque base
      if (cfgRules.acceptingNodeNumbers.contains(grammarNode.num) && (tokenIndex == lexer.tokens.length - 1)) {
        sppf.root = sppf.nodes.get(new SPPFSymbolNode(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), 0, lexer.tokens.length - 1));
        inLanguage = true;
      }
      return; // End of parse
    }
    stackNode.pops.add(derivationNode);
    for (GSSEdge e : stackNode.edges)
      queueDesc(stackNode.grammarNode, tokenIndex, e.dst, sppf.update(stackNode.grammarNode, e.sppfnode, derivationNode));
  }

  /* Derivation handling *****************************************************/
  void derivationUpdate() {
    derivationNode = sppf.update(grammarNode.seq, derivationNode, sppf.find(cfgRules.elementToNodeMap.get(grammarNode.element), tokenIndex, tokenIndex + 1));
  }

  void derivationUpdateEpsilon() {
    derivationNode = sppf.update(grammarNode.seq, derivationNode, sppf.find(cfgRules.elementToNodeMap.get(grammarNode.element), tokenIndex, tokenIndex));
  }
}