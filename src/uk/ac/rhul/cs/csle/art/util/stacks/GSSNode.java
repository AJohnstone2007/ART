package uk.ac.rhul.cs.csle.art.util.stacks;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class GSSNode extends AbstractStackNode {
  public final CFGNode cfgNode;
  final int inputIndex;
  public final Set<GSSEdge> edges = new HashSet<>();
  public final Set<PopSetElement> pops = new HashSet<>();

  @Override
  public CFGNode getGrammarNode() {
    return cfgNode;
  }

  @Override
  public Set<PopSetElement> getPops() {
    return pops;
  }

  @Override
  public Set<GSSEdge> getEdges() {
    return edges;
  }

  public GSSNode(CFGNode grammarNode, int inputIndex) {
    super();
    this.cfgNode = grammarNode;
    this.inputIndex = inputIndex;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cfgNode == null) ? 0 : cfgNode.hashCode());
    result = prime * result + inputIndex;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GSSNode other = (GSSNode) obj;
    if (cfgNode == null) {
      if (other.cfgNode != null) return false;
    } else if (!cfgNode.equals(other.cfgNode)) return false;
    if (inputIndex != other.inputIndex) return false;
    return true;
  }

  @Override
  public String toString() {
    return "(" + cfgNode.toStringAsProduction() + ", " + inputIndex + ")";
  }
}
