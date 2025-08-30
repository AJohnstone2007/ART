package uk.ac.rhul.cs.csle.art.util.relation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractRelation<T1, T2> {
  final protected Map<T1, Set<T2>> map = makeMap();
  final protected Set<T2> empty = makeSet();

  abstract protected Set<T2> makeSet();

  abstract protected Map<T1, Set<T2>> makeMap();

  public boolean isRelated(T1 t1, T2 t2) {
    return get(t1).contains(t2);
  }

  public Set<T1> getDomain() {
    return map.keySet();
  }

  public Set<T2> get(T1 t1) {
    Set<T2> tmp = map.get(t1);
    if (tmp == null)
      return empty;
    else
      return tmp;
  }

  public void add(T1 src) {
    if (map.get(src) == null) map.put(src, makeSet());
  }

  public boolean add(T1 src, T2 dst) {
    if (map.get(src) == null) map.put(src, makeSet());
    return map.get(src).add(dst);
  }

  public boolean addAll(T1 src, Set<T2> dsts) {
    if (map.get(src) == null) map.put(src, makeSet());
    return map.get(src).addAll(dsts);
  }

  public boolean addAllAll(Set<T1> srcSet, Set<T2> dsts) {
    boolean ret = false;
    for (var src : srcSet)
      ret |= addAll(src, dsts);

    return ret;
  }

  public void remove(T1 src, T2 dst) {
    if (map.get(src) == null) return;
    map.get(src).remove(dst);
  }

  public void clear() {
    map.clear();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (T1 t1 : getDomain()) {
      sb.append(t1 + " -> {");
      for (T2 t2 : get(t1))
        sb.append(" " + t2);
      sb.append(" }\n");
    }
    return sb.toString();
  }

  public String toStringDot() {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph \"GDG\" {\n"
        + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.1 color=gray]");

    for (T1 t1 : getDomain())
      for (T2 t2 : get(t1))
        sb.append("\"" + t1 + "\" -> \"" + t2 + "\"\n");
    sb.append("}");
    return sb.toString();
  }

  @SuppressWarnings("unchecked")
  public void transitiveClosure() {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (T1 t1 : new HashSet<>(getDomain())) { // We may add edges from t1 (horrible)
        for (T2 t2 : new HashSet<>(get(t1))) {
          Set<T2> nextEdges = get((T1) t2);
          if (nextEdges != null) for (T2 next : nextEdges) {
            changed |= add(t1, next);
            // Util.info("Transitive closure with base " + t1 + " intermediate " + t2 + " and target " + next + "\nAdding edge from " + t1 + " to " +
            // next
            // + (changed ? " CHANGE" : " SAME"));
          }
        }
      }
    }
  }
}
