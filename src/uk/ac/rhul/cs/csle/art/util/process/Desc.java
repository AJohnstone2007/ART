package uk.ac.rhul.cs.csle.art.util.process;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.gss.GSSN;
import uk.ac.rhul.cs.csle.art.util.sppf.SPPFN;

public class Desc {
  public CFGNode gn;
  public int i;
  public GSSN sn;
  public SPPFN dn;

  public Desc(CFGNode gn, int index, GSSN sn, SPPFN dn) {
    super();
    this.gn = gn;
    this.i = index;
    this.sn = sn;
    this.dn = dn;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    sb.append(i);
    sb.append(", ");
    sb.append(gn.toStringAsProduction());
    sb.append(", ");
    sb.append(sn);
    sb.append(", ");
    sb.append(dn);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((sn == null) ? 0 : sn.hashCode());
    result = prime * result + i;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    result = prime * result + ((dn == null) ? 0 : dn.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Desc other = (Desc) obj;
    if (sn == null) {
      if (other.sn != null) return false;
    } else if (!sn.equals(other.sn)) return false;
    if (i != other.i) return false;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    if (dn == null) {
      if (other.dn != null) return false;
    } else if (!dn.equals(other.dn)) return false;
    return true;
  }
}
