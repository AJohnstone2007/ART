package uk.ac.rhul.cs.csle.art.util.stacks;

import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;

public class GSSEdge {
  public final AbstractStackNode dst;
  public final AbstractDerivationNode derivationNode;

  public GSSEdge(AbstractStackNode stackNode, AbstractDerivationNode derivationNode) {
    this.derivationNode = derivationNode;
    this.dst = stackNode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dst == null) ? 0 : dst.hashCode());
    result = prime * result + ((derivationNode == null) ? 0 : derivationNode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GSSEdge other = (GSSEdge) obj;
    if (dst == null) {
      if (other.dst != null) return false;
    } else if (!dst.equals(other.dst)) return false;
    if (derivationNode == null) {
      if (other.derivationNode != null) return false;
    } else if (!derivationNode.equals(other.derivationNode)) return false;
    return true;
  }
}
