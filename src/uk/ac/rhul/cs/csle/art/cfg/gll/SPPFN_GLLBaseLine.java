package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

class SPPFN_GLLBaseLine extends SPPFNode_GLLBaseLine {
  public final int number; // to allow a bitset to be used as visited set
  public final CFGNode gn;
  public final int li;
  public final int ri;
  public final Set<SPPFPN_GLLBaseLine> packNS = new HashSet<>();

  public SPPFN_GLLBaseLine(CFGNode gn, int li, int ri, int nextFreeSPPFNodeNumber) {
    super();
    this.number = nextFreeSPPFNodeNumber;
    this.gn = gn;
    this.li = li;
    this.ri = ri;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    result = prime * result + li;
    result = prime * result + ri;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SPPFN_GLLBaseLine other = (SPPFN_GLLBaseLine) obj;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    if (li != other.li) return false;
    if (ri != other.ri) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // if (gn == null)
    // sb.append("NULL");
    // else
    sb.append(number);
    sb.append(": ");
    sb.append(gn.toString());
    sb.append(", ");
    sb.append(li);
    sb.append(", ");
    sb.append(ri);

    return sb.toString();
  }
}
