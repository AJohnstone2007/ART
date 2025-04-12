package uk.ac.rhul.cs.csle.art.util.derivations;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class SPPFSymbolNode extends AbstractSPPFNode {
  public int number = 0; // to be set after parsing by SPPF.numbeNodes()
  public final CFGNode grammarNode;
  public final int leftExtent;
  public final int rightExtent;
  public final Set<SPPFPackedNode> packNodes = new HashSet<>();

  public SPPFSymbolNode(CFGNode grammarNode, int leftExtent, int rightExtent) {
    super();
    this.grammarNode = grammarNode;
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
  }

  public boolean isSymbol() {
    return packNodes.size() == 0 /* terminal or epsilon */ || (grammarNode.element.kind == CFGKind.N && grammarNode.seq == null /* LHS */);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((grammarNode == null) ? 0 : grammarNode.hashCode());
    result = prime * result + leftExtent;
    result = prime * result + rightExtent;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SPPFSymbolNode other = (SPPFSymbolNode) obj;
    if (grammarNode == null) {
      if (other.grammarNode != null) return false;
    } else if (!grammarNode.equals(other.grammarNode)) return false;
    if (leftExtent != other.leftExtent) return false;
    if (rightExtent != other.rightExtent) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (grammarNode == null)
      sb.append("NULL");
    else
      sb.append(number);
    sb.append(": ");
    if (isSymbol())
      sb.append(grammarNode.toString());
    else
      sb.append(grammarNode.toStringAsProduction());
    sb.append(", ");
    sb.append(leftExtent);
    sb.append(", ");
    sb.append(rightExtent);

    return sb.toString();
  }

  @Override
  public int getLeftExtent() {
    return leftExtent;
  }

  @Override
  public int getRightExtent() {
    return rightExtent;
  }
}
