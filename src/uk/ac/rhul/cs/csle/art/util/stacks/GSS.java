package uk.ac.rhul.cs.csle.art.util.stacks;

import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;
import uk.ac.rhul.cs.csle.art.util.tasks.AbstractTasks;

public class GSS extends AbstractStacks {
  public Map<GSSNode, GSSNode> nodes = new HashMap<>();
  public GSSNode root;

  public GSS(CFGRules cfgRules) {
    root = new GSSNode(cfgRules.endOfStringNode, 0);
    nodes.put(root, root);
  }

  private GSSNode find(CFGNode grammarNode, int i) {
    GSSNode gssNode = new GSSNode(grammarNode, i);
    if (nodes.get(gssNode) == null) nodes.put(gssNode, gssNode);
    return nodes.get(gssNode);
  }

  @Override
  public AbstractStackNode getRoot() {
    return root;
  }

  @Override
  public AbstractStackNode push(AbstractDerivations derivations, AbstractTasks tasks, int tokenIndex, CFGNode gn, AbstractStackNode stackNode,
      AbstractDerivationNode derivationNode) {
    GSSNode gssNode = find(gn.seq, tokenIndex);
    GSSEdge gssEdge = new GSSEdge(stackNode, derivationNode);
    if (!gssNode.edges.contains(gssEdge)) {
      gssNode.edges.add(gssEdge);
      for (AbstractDerivationNode rc : gssNode.pops)
        tasks.queue(rc.getRightExtent(), gn.seq, stackNode, derivations.extend(gn.seq, derivationNode, rc));
    }
    return gssNode;
  }

  @Override
  public void pop(AbstractDerivations derivations, AbstractTasks tasks, int tokenIndex, AbstractStackNode stackNode, AbstractDerivationNode derivationNode) {
    stackNode.getPops().add(derivationNode);
    for (GSSEdge e : stackNode.getEdges())
      tasks.queue(tokenIndex, stackNode.getGrammarNode(), e.dst, derivations.extend(stackNode.getGrammarNode(), e.derivationNode, derivationNode));
  }

  @Override
  public void statistics(Statistics statistics) {
    int gssEdgeCount = 0, popCount = 0;
    for (GSSNode g : nodes.keySet()) {
      gssEdgeCount += g.edges.size();
      popCount += g.pops.size();
    }
    statistics.put("gssNodeCount", nodes.keySet().size());
    statistics.put("gssEdgeCount", gssEdgeCount);
    statistics.put("popCount", popCount);
  }

}
