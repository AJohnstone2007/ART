package uk.ac.rhul.cs.csle.art.util.stacks;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;
import uk.ac.rhul.cs.csle.art.util.tasks.AbstractTasks;

public class GSSGLL extends AbstractStacks {
  public Map<GSSNode, GSSNode> nodes = new HashMap<>();
  public GSSNode root;

  public GSSGLL(CFGRules cfgRules) {
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
      for (PopSetElement rc : gssNode.pops) // Contingent pops
        tasks.queue(rc.inputIndex, gn.seq, stackNode, derivations.extend(gn.seq, derivationNode, rc.derivationNode));
    }
    return gssNode;
  }

  @Override
  public void pop(AbstractDerivations derivations, AbstractTasks tasks, int tokenIndex, AbstractStackNode stackNode, AbstractDerivationNode derivationNode) {
    stackNode.getPops().add(new PopSetElement(tokenIndex, derivationNode));
    for (GSSEdge e : stackNode.getEdges())
      tasks.queue(tokenIndex, stackNode.getGrammarNode(), e.dst, derivations.extend(stackNode.getGrammarNode(), e.derivationNode, derivationNode));
  }

  public void loadStatistics(Statistics statistics) {
    int gssEdgeCount = 0, popCount = 0;
    for (GSSNode g : nodes.keySet()) {
      gssEdgeCount += g.edges.size();
      popCount += g.pops.size();
    }
    statistics.put("gssNodeCount", nodes.keySet().size());
    statistics.put("gssEdgeCount", gssEdgeCount);
    statistics.put("popCount", popCount);
  }

  @Override
  public void toDot() {
    new GSS2Dot(nodes, "gssV3.dot");
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    // TODO Auto-generated method stub

  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    // TODO Auto-generated method stub

  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
    // TODO Auto-generated method stub

  }

}
