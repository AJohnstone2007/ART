package uk.ac.rhul.cs.csle.art.util.relation;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class RelationOrdered<T1, T2> extends AbstractRelation<T1, T2> {
  public RelationOrdered() {
    super();
    map = new TreeMap<T1, Set<T2>>();
    empty = new TreeSet<T2>();
  }

  public RelationOrdered(AbstractRelation<T1, T2> relation) { // copy constructor
    this();
    for (T1 t1 : relation.getDomain())
      map.put(t1, new TreeSet<>(relation.get(t1)));
  }

  @Override
  protected Set<T2> makeSet() {
    return new TreeSet<T2>();
  }

  @Override
  protected Map<T1, Set<T2>> makeMap() {
    return new TreeMap<T1, Set<T2>>();
  }

}
