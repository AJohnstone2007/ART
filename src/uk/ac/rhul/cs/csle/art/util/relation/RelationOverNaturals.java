package uk.ac.rhul.cs.csle.art.util.relation;

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
      if (b != null) b.clear();
  }

  // if a->b and b->c then set a->c
  public void transitiveClosure() {
    @SuppressWarnings("unchecked")
    boolean changed = true;
    while (changed) {
      changed = false;
      for (int a = 0; a < relation.length; a++)
        if (relation[a] != null) for (int b = 0; b < relation[a].length(); b++) {
          if (relation[a].get(b)) {
            if (relation[b] != null) {
              int oldCardinalityA = relation[a].cardinality();
              relation[a].or(relation[b]);
              changed |= relation[a].cardinality() != oldCardinalityA;
            }
          }
        }
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < relation.length; i++)
      if (relation[i] != null) {
        builder.append(i + "->");
        builder.append(relation[i]);
        builder.append("\n");
      }
    return builder.toString();
  }

  public BitSet getCodomain(int number) {
    return relation[number];
  }

  public boolean get(int src, int dst) {
    if (relation[src] == null) return false;
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
