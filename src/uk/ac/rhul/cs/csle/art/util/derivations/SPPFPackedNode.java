package uk.ac.rhul.cs.csle.art.util.derivations;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class SPPFPackedNode extends AbstractSPPFNode {
  // Note Nov 2024 added parent to support SPPF cycle detection - not needed in all variants
  public int number = 0; // Numbers allocated after parse
  public SPPFSymbolNode parent = null; // Parents allocated after parse
  public final CFGNode grammarNode;
  public final int pivot;
  public final SPPFSymbolNode leftChild;
  public final SPPFSymbolNode rightChild;
  public boolean suppressed = false;

  public SPPFPackedNode(CFGNode grammarNode, int pivot, SPPFSymbolNode leftChild, SPPFSymbolNode rightChild) {
    super();
    this.grammarNode = grammarNode;
    this.pivot = pivot;
    this.leftChild = leftChild;
    this.rightChild = rightChild;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((grammarNode == null) ? 0 : grammarNode.hashCode());
    result = prime * result + number;
    result = prime * result + pivot;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SPPFPackedNode other = (SPPFPackedNode) obj;
    if (grammarNode == null) {
      if (other.grammarNode != null) return false;
    } else if (!grammarNode.equals(other.grammarNode)) return false;
    if (number != other.number) return false;
    if (pivot != other.pivot) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(number);
    sb.append(": ");
    sb.append(grammarNode.toStringAsProduction());
    sb.append(", " + pivot);
    sb.append(" under ");
    sb.append(parent);
    return sb.toString();
  }
}
