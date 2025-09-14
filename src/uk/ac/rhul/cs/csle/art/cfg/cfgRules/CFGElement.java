package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.util.Util;

public class CFGElement implements Comparable<Object> {

  public int number;
  public final CFGElementKind cfgKind;
  public final String str;
  public final Set<Character> set;

  public final Map<String, String> attributes = new HashMap<>();
  public final Map<String, Integer> rhsNonterminalsAcrossAllProductions = new HashMap<>();

  public CFGElement(CFGElementKind kind, String s) {
    super();
    this.cfgKind = kind;
    switch (this.cfgKind) {
    case TRM_CH_SET, TRM_CH_ANTI_SET:
      set = new TreeSet<>();
      // Util.debug("Processing to set: " + s);
      for (var c : Util.unescapeString(s).toCharArray())
        set.add(c);
      this.str = set.toString(); // canonicalise string for character sets
      return;
    default:
      set = null;
      this.str = s;
      return;
    }
  }

  public String toStringDetailed() {
    return number + ": " + cfgKind + " " + Util.escapeString(str);
  }

  @Override
  public String toString() {
    switch (cfgKind) {
    case SOS, EOS, EPSILON, ALT, END, PAR, OPT, POS, KLN:
      return str;
    case TRM_CS:
      return "'" + Util.escapeString(str) + "'";
    case TRM_CI:
      return "\"" + Util.escapeString(str) + "\"";
    case TRM_CH:
      return "`" + Util.escapeString(str);
    case TRM_CH_SET:
      return "{" + setToString() + "}";
    case TRM_CH_ANTI_SET:
      return "~{" + setToString() + "}";
    case TRM_CH_UOB:
      return "!{}" + Util.escapeString(str);
    case TRM_BI:
      return "&" + Util.escapeString(str);
    case NONTERMINAL:
      return Util.escapeString(str);
    default:
      return "???";
    }
  }

  String setToString() {
    StringBuilder sb = new StringBuilder();
    for (var s : set)
      sb.append(Util.escapeString(s.toString()));
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cfgKind == null) ? 0 : cfgKind.hashCode());
    result = prime * result + ((str == null) ? 0 : str.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CFGElement other = (CFGElement) obj;
    if (cfgKind != other.cfgKind) return false;
    if (str == null) {
      if (other.str != null) return false;
    } else if (!str.equals(other.str)) return false;
    return true;
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) return 1;
    CFGElement other = (CFGElement) o;
    if (cfgKind.ordinal() > other.cfgKind.ordinal())
      return 1;
    else if (cfgKind.ordinal() < other.cfgKind.ordinal())
      return -1;
    else
      return str.compareTo(other.str);
  }
}
