package uk.ac.rhul.cs.csle.art.util.sppf;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class SPPFPN extends SPPFNode {
  // Note Nov 2024 added parent to support SPPF cycle detection
  public int number = 0; // Numbers allocated after parse
  public SPPFN parent = null; // Parents allocated after parse
  public final CFGNode gn;
  public final int pivot;
  public final SPPFN leftChild;
  public final SPPFN rightChild;
  public boolean suppressed = false;

  public SPPFPN(CFGNode gn, int pivot, SPPFN leftChild, SPPFN rightChild) {
    super();
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
    result = prime * result + number;
    result = prime * result + pivot;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SPPFPN other = (SPPFPN) obj;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    if (number != other.number) return false;
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
