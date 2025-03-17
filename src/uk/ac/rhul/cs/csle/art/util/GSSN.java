package uk.ac.rhul.cs.csle.art.util;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class GSSN {
  public final CFGNode gn;
  final int i;
  public final Set<GSSE> edges = new HashSet<>();
  public final Set<SPPFN> pops = new HashSet<>();

  public GSSN(CFGNode gn, int i) {
    super();
    this.gn = gn;
    this.i = i;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GSSN other = (GSSN) obj;
    if (i != other.i) return false;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "(" + gn.toStringAsProduction() + ", " + i + ")";
  }

}
