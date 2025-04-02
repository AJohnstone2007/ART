package uk.ac.rhul.cs.csle.art.util.stacks;

import uk.ac.rhul.cs.csle.art.util.derivations.SPPFSymbolNode;

public class GSSEdge {
  public final GSSNode dst;
  public final SPPFSymbolNode sppfnode;

  public GSSEdge(GSSNode dst, SPPFSymbolNode sppfNode) {
    this.sppfnode = sppfNode;
    this.dst = dst;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dst == null) ? 0 : dst.hashCode());
    result = prime * result + ((sppfnode == null) ? 0 : sppfnode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GSSEdge other = (GSSEdge) obj;
    if (dst == null) {
      if (other.dst != null) return false;
    } else if (!dst.equals(other.dst)) return false;
    if (sppfnode == null) {
      if (other.sppfnode != null) return false;
    } else if (!sppfnode.equals(other.sppfnode)) return false;
    return true;
  }
}
