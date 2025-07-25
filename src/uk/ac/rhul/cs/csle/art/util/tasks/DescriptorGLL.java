package uk.ac.rhul.cs.csle.art.util.tasks;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;

public class DescriptorGLL {
  public final int tokenIndex;
  public final CFGNode cfgNode;
  public final AbstractStackNode stackNode;
  public final AbstractDerivationNode derivationNode;

  public DescriptorGLL(int tokenIndex, CFGNode grammarNode, AbstractStackNode stackNode, AbstractDerivationNode derivationNode) {
    super();
    this.tokenIndex = tokenIndex;
    this.cfgNode = grammarNode;
    this.stackNode = stackNode;
    this.derivationNode = derivationNode;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    sb.append(tokenIndex);
    sb.append(", ");
    sb.append(cfgNode.toStringAsProduction());
    sb.append(", ");
    sb.append(stackNode);
    sb.append(", ");
    sb.append(derivationNode);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((stackNode == null) ? 0 : stackNode.hashCode());
    result = prime * result + tokenIndex;
    result = prime * result + ((cfgNode == null) ? 0 : cfgNode.hashCode());
    result = prime * result + ((derivationNode == null) ? 0 : derivationNode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DescriptorGLL other = (DescriptorGLL) obj;
    if (stackNode == null) {
      if (other.stackNode != null) return false;
    } else if (!stackNode.equals(other.stackNode)) return false;
    if (tokenIndex != other.tokenIndex) return false;
    if (cfgNode == null) {
      if (other.cfgNode != null) return false;
    } else if (!cfgNode.equals(other.cfgNode)) return false;
    if (derivationNode == null) {
      if (other.derivationNode != null) return false;
    } else if (!derivationNode.equals(other.derivationNode)) return false;
    return true;
  }
}
