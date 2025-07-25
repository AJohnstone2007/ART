package uk.ac.rhul.cs.csle.art.util.stacks;

import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;

public class PopSetElement {
  SPPFSymbolNode derivationNode;
  int inputIndex;

  public PopSetElement(SPPFSymbolNode derivationNode, int inputIndex) {
    super();
    this.derivationNode = derivationNode;
    this.inputIndex = inputIndex;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((derivationNode == null) ? 0 : derivationNode.hashCode());
    result = prime * result + inputIndex;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PopSetElement other = (PopSetElement) obj;
    if (derivationNode == null) {
      if (other.derivationNode != null) return false;
    } else if (!derivationNode.equals(other.derivationNode)) return false;
    if (inputIndex != other.inputIndex) return false;
    return true;
  }

}
