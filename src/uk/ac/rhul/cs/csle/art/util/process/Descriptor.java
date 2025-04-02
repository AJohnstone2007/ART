package uk.ac.rhul.cs.csle.art.util.process;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSNode;

public class Descriptor {
  public CFGNode grammarNode;
  public int inputIndex;
  public GSSNode stackNode;
  public SPPFSymbolNode derivationNode;

  public Descriptor(CFGNode grammarNode, int inputIndex, GSSNode stackNode, SPPFSymbolNode derivationNode) {
    super();
    this.grammarNode = grammarNode;
    this.inputIndex = inputIndex;
    this.stackNode = stackNode;
    this.derivationNode = derivationNode;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    sb.append(inputIndex);
    sb.append(", ");
    sb.append(grammarNode.toStringAsProduction());
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
    result = prime * result + inputIndex;
    result = prime * result + ((grammarNode == null) ? 0 : grammarNode.hashCode());
    result = prime * result + ((derivationNode == null) ? 0 : derivationNode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Descriptor other = (Descriptor) obj;
    if (stackNode == null) {
      if (other.stackNode != null) return false;
    } else if (!stackNode.equals(other.stackNode)) return false;
    if (inputIndex != other.inputIndex) return false;
    if (grammarNode == null) {
      if (other.grammarNode != null) return false;
    } else if (!grammarNode.equals(other.grammarNode)) return false;
    if (derivationNode == null) {
      if (other.derivationNode != null) return false;
    } else if (!derivationNode.equals(other.derivationNode)) return false;
    return true;
  }
}
