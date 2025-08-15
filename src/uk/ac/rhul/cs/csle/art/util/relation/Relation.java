package uk.ac.rhul.cs.csle.art.util.relation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Relation<T1, T2> extends AbstractRelation<T1, T2> {
  public Relation() {
  }

  public Relation(AbstractRelation<T1, T2> relation) { // copy constructor
    for (T1 t1 : relation.getDomain())
      map.put(t1, new HashSet<>(relation.get(t1)));
  }

  @Override
  protected Set<T2> makeSet() {
    return new HashSet<T2>();
  }

  @Override
  protected Map<T1, Set<T2>> makeMap() {
    return new HashMap<T1, Set<T2>>();
  }
}
