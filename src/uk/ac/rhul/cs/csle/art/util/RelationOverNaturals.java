package uk.ac.rhul.cs.csle.art.util;

import java.util.BitSet;

public class RelationOverNaturals {

  BitSet[] relation;
  int initialCodomainSize;

  public RelationOverNaturals(int domainSize, int initialCodomainSize) {
    super();
    relation = new BitSet[domainSize];
    this.initialCodomainSize = initialCodomainSize;
  }

  public int domainSize() {
    return relation.length;
  }

  public boolean add(int src, int dst) {

    if (relation[src] == null) relation[src] = new BitSet(initialCodomainSize);
    BitSet codomain = relation[src];
    boolean ret = !codomain.get(dst);
    relation[src].set(dst);
    return ret;
  }

  public void remove(int src, int dst) {
    if (relation[src] == null) return;
    relation[src].clear(dst);
  }

  public void clear() {
    for (var b : relation)
      b.clear();
  }

  public void transitiveClosure() {
    @SuppressWarnings("unchecked")
    boolean changed = true;
    while (changed) {
      changed = false;
      for (BitSet i : relation)
        for (int j = 0; j < i.length(); j++) {
          BitSet kk = relation[j];
          if (kk != null) for (int k = 0; k < kk.length(); k++)
            if (kk.get(k) && !i.get(k)) {
              changed = true;
              i.set(k);
            }
        }
    }
  }

  public BitSet get(int number) {
    return relation[number];
  }

  public boolean get(int src, int dst) {
    return relation[src].get(dst);
  }

  // public String toDotString() {
  // StringBuilder sb = new StringBuilder();
  // sb.append("digraph \"GDG\" {\n"
  // + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.1
  // color=gray]");
  //
  // for (T1 t1 : getDomain())
  // for (T2 t2 : get(t1))
  // sb.append("\"" + t1 + "\" -> \"" + t2 + "\"\n");
  // sb.append("}");
  // return sb.toString();
  // }
}
