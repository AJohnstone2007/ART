package uk.ac.rhul.cs.csle.art.util.sppf;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class SPPFN extends SPPFNode {
  public int number = 0; // to be set after parsing
  public final CFGNode gn;
  public final int li;
  public final int ri;
  public final Set<SPPFPN> packNS = new HashSet<>();

  public SPPFN(CFGNode gn, int li, int ri) {
    super();
    this.gn = gn;
    this.li = li;
    this.ri = ri;
  }

  public boolean isSymbol() {
    return packNS.size() == 0 /* terminal or epsilon */ || (gn.elm.kind == CFGKind.N && gn.seq == null /* LHS */);
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
    SPPFN other = (SPPFN) obj;
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
    if (gn == null)
      sb.append("NULL");
    else
      sb.append(number);
    sb.append(": ");
    if (isSymbol())
      sb.append(gn.toString());
    else
      sb.append(gn.toStringAsProduction());
    sb.append(", ");
    sb.append(li);
    sb.append(", ");
    sb.append(ri);

    return sb.toString();
  }
}
