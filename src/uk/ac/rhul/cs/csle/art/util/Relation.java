package uk.ac.rhul.cs.csle.art.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Relation<T1, T2> {
  private final Map<T1, Set<T2>> map;
  private final HashSet<T2> empty = new HashSet<>();

  public Relation() {
    super();
    map = new HashMap<>();
  }

  public Relation(Relation<T1, T2> relation) { // copy constructor
    this();
    for (T1 t1 : relation.getDomain())
      map.put(t1, new HashSet<>(relation.get(t1)));
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
    if (map.get(src) == null) map.put(src, new HashSet<>());
  }

  public boolean add(T1 src, T2 dst) {
    if (map.get(src) == null) map.put(src, new HashSet<>());
    return map.get(src).add(dst);
  }

  public boolean addAll(T1 src, Set<T2> dsts) {
    if (map.get(src) == null) map.put(src, new HashSet<>());
    return map.get(src).addAll(dsts);
  }

  public void remove(T1 src, T2 dst) {
    if (map.get(src) == null) return;
    map.get(src).remove(dst);
  }

  public void clear() {
    map.clear();
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
            // System.out.println("Transitive closure with base " + t1 + " intermediate " + t2 + " and target " + next + "\nAdding edge from " + t1 + " to " +
            // next
            // + (changed ? " CHANGE" : " SAME"));
          }
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Relation: \n");
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

}
