package uk.ac.rhul.cs.csle.art.util.sppf;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOverNaturals;

public class SPPF {
  public SPPFN rootNode;
  public Map<SPPFN, SPPFN> nodes;
  public BitSet visited;

  public final BitSet cyclic = new BitSet();
  public BitSet rootReachable = new BitSet();
  public RelationOverNaturals reachable;
  public Set<SPPFPN> cbD = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  public final Set<SPPFPN> cbDPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note

  public void computeCoreReachability(Object object) {
    // TODO Auto-generated method stub

  }

  public void numberSPPFNodes() {
    int nextFreeNodeNumber = 1;
    for (var n : nodes.keySet())
      n.number = nextFreeNodeNumber++;
    for (var n : nodes.keySet())
      for (var p : n.packNS) {
        p.parent = n;
        p.number = nextFreeNodeNumber++;
      }

    reachable = new RelationOverNaturals(nextFreeNodeNumber, nextFreeNodeNumber);
  }

  /* Temporary disambiguation before choosers are implemented ****************/
  public void chooseLongestMatch() {
    visited.clear();
    chooseLongestMatchRec(rootNode);
  }

  private void chooseLongestMatchRec(SPPFN sn) {
    if (visited.get(sn.number)) return;
    visited.set(sn.number);

    int rightMostPivot = -1;
    SPPFPN candidate = null;
    if (sn.packNS.size() > 1) {
      Util.warning("Ambiguity detected at SPPF node " + sn.number + ": " + sn.gn.toStringAsProduction() + " involving ");
      for (var p : sn.packNS)
        Util.info("   " + p.toString());
    }
    for (SPPFPN p : sn.packNS) {
      if (p.pivot > rightMostPivot) {
        rightMostPivot = p.pivot;
        candidate = p;
      }
      if (p.leftChild != null) chooseLongestMatchRec(p.leftChild);
      if (p.rightChild != null) chooseLongestMatchRec(p.rightChild);
    }

    for (SPPFPN p : sn.packNS)
      if (p != candidate) p.suppressed = true;
  }

}
