package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.relation.Relation;

public class TWESet {
  private final String inputString;
  private final ArrayList<Set<TWESetElement>> slices = new ArrayList<>();

  public TWESet(String inputString) {
    this.inputString = inputString;
    slices.add(new HashSet<>());
    for (int i = 1; i < inputString.length(); i++)
      slices.add(null);
  }

  public void set(int i, Set<TWESetElement> slice) {
    slices.set(i, slice);
  }

  public Set<TWESetElement> get(int i) {
    return slices.get(i);
  }

  public void chooseDefault() {
    if (slices.get(0).isEmpty()) {
      Util.error("Empty tweSet");
      return;
    }
    for (var slice : slices)
      if (slice != null) for (var e : slice)
        for (var f : slice)
          if ((e.rightExtent > f.rightExtent) || (e.rightExtent == f.rightExtent && e.cfgElement.cfgKind != CFGKind.TRM_BI && f.cfgElement.cfgKind == CFGKind.TRM_BI))
            f.suppressed = true;
    suppressDeadPaths();
  }

  public void choose(Relation<CFGElement, CFGElement> longer, Relation<CFGElement, CFGElement> shorter, Relation<CFGElement, CFGElement> higher) {
    for (var slice : slices)
      if (slice != null) for (var e : slice)
        for (var f : slice)
          if ((e.rightExtent > f.rightExtent) && longer.isRelated(e.cfgElement, f.cfgElement)
              || (e.rightExtent < f.rightExtent) && shorter.isRelated(e.cfgElement, f.cfgElement)
              || (e.rightExtent == f.rightExtent) && higher.isRelated(e.cfgElement, f.cfgElement))
            f.suppressed = true;
  }

  public void suppressDeadPaths() {
    // Util.debug("TWE dead path suppression");
    int inDegree[] = new int[slices.size() + 1];
    int outDegree[] = new int[slices.size() + 1];
    for (int i = 0; i < slices.size(); i++)
      if (slices.get(i) != null) for (var e : slices.get(i))
        if (!e.suppressed) {
          outDegree[i]++;
          inDegree[e.rightExtent]++;
        }

    for (int i = slices.size() - 3; i >= 0; i--)
      if (outDegree[i] != 0) {
        for (var e : slices.get(i))
          if (!e.suppressed && outDegree[e.rightExtent] == 0) {
            e.suppressed = true;
            outDegree[i]--;
            inDegree[e.rightExtent]--;
          }
      }

    for (int i = 1; i < slices.size(); i++) {
      if (outDegree[i] != 0) {
        for (var e : slices.get(i))
          if (!e.suppressed && inDegree[i] == 0) {
            e.suppressed = true;
            outDegree[i]--;
            inDegree[e.rightExtent]--;
          }
      }
    }
  }

  public ArrayList<TWESetElement> firstLexicalisation() {
    ArrayList<TWESetElement> ret = new ArrayList<>();
    for (int i = 0; i < slices.size();)
      for (var e : slices.get(i))
        if (!e.suppressed) {
          ret.add(e);
          i = e.rightExtent;
          break;
        }
    // Util.debug("First lexicalisation: " + firstLexicalisation);
    return ret;
  }
}
