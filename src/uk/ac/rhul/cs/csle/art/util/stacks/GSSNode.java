package uk.ac.rhul.cs.csle.art.util.stacks;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;

public class GSSNode {
  public final CFGNode grammarNode;
  final int inputIndex;
  public final Set<GSSEdge> edges = new HashSet<>();
  public final Set<SPPFSymbolNode> pops = new HashSet<>();

  public GSSNode(CFGNode grammarNode, int inputIndex) {
    super();
    this.grammarNode = grammarNode;
    this.inputIndex = inputIndex;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + inputIndex;
    result = prime * result + ((grammarNode == null) ? 0 : grammarNode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GSSNode other = (GSSNode) obj;
    if (inputIndex != other.inputIndex) return false;
    if (grammarNode == null) {
      if (other.grammarNode != null) return false;
    } else if (!grammarNode.equals(other.grammarNode)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "(" + grammarNode.toStringAsProduction() + ", " + inputIndex + ")";
  }
}
