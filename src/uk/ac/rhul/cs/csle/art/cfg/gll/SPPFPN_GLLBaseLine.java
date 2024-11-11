package uk.ac.rhul.cs.csle.art.cfg.gll;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

// Note Nov 2024 added parent to support SPPF cycle detection - not needed for actual cycle detection
class SPPFPN_GLLBaseLine extends SPPFNode_GLLBaseLine {
  public final int number; // to allow a bitset to be used as visited set
  public final SPPFN_GLLBaseLine parent;
  public final CFGNode gn;
  public final int pivot;
  public final SPPFN_GLLBaseLine leftChild;
  public final SPPFN_GLLBaseLine rightChild;
  public boolean suppressed = false;

  public SPPFPN_GLLBaseLine(SPPFN_GLLBaseLine parent, CFGNode gn, int pivot, SPPFN_GLLBaseLine leftChild, SPPFN_GLLBaseLine rightChild,
      int nextFreeSPPFNodeNumber) {
    super();
    this.number = nextFreeSPPFNodeNumber;
    this.parent = parent;
    this.gn = gn;
    this.pivot = pivot;
    this.leftChild = leftChild;
    this.rightChild = rightChild;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    result = prime * result + pivot;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SPPFPN_GLLBaseLine other = (SPPFPN_GLLBaseLine) obj;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    if (parent == null) {
      if (other.parent != null) return false;
    } else if (!parent.equals(other.parent)) return false;
    if (pivot != other.pivot) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(number);
    sb.append(": ");
    sb.append(gn.toStringAsProduction());
    sb.append(", " + pivot);
    sb.append(" under ");
    sb.append(parent);
    return sb.toString();
  }
}
