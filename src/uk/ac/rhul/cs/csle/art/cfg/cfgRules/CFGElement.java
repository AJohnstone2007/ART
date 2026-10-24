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
  private boolean nullable; // This element can derive epsilon
  public final Map<String, String> attributes = new HashMap<>();
  public final Map<String, Integer> rhsNonterminalsAcrossAllProductions = new HashMap<>();
  public final Set<CFGElement> first = new TreeSet<>();

  boolean setNullable() {
    if (nullable) return false; // already set
    nullable = true; // We have changed it
    return true;
  }

  boolean getNullable() {
    return nullable;
  }

  public CFGElement(CFGElementKind kind, String s) {
    super();
    this.cfgKind = kind;
    switch (this.cfgKind) {
    case TERM_CH_SET, TERM_CH_ANTI_SET:
      nullable = false;
      set = new TreeSet<>();
      // Util.debug("Processing to set: " + s);
      for (var c : Util.unescapeString(s).toCharArray())
        set.add(c);
      this.str = set.toString(); // canonicalise string for character sets
      return;
    case EPSILON:
      nullable = true;
      set = null;
      this.str = s;
      return;
    default:
      nullable = false;
      set = null;
      this.str = s;
      return;
    }
  }

  public String toStringDetailed() {
    return number + ": " + cfgKind + " " + Util.escapeString(str);
  }

  @Override
  public String toString() { // standard for use in visualisations
    switch (cfgKind) {
    case EPSILON:
      return "#";
    case EOS:
      return "$";
    case SOS, ALT, END, PAR, OPT, POS, KLN:
      return cfgKind.toString();
    case TERM_CS:
      return "'" + Util.escapeString(str) + "'";
    case TERM_CI:
      return "\"" + Util.escapeString(str) + "\"";
    case TERM_CH:
      return "`" + Util.escapeString(str);
    case TERM_CH_SET:
      return "{" + setToString() + "}";
    case TERM_CH_ANTI_SET:
      return "~{" + setToString() + "}";
    case TERM_CH_UOB:
      return "!{}" + Util.escapeString(str);
    case TERM_BI:
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

  public String toStringAsProductionElement() { // nonstandard for use when printing productions from cfgRules - a ( is inserted and we are used postorder
    switch (cfgKind) {
    case EPSILON:
      return "#";
    case EOS:
      return "$";
    case SOS, ALT, END:
      return cfgKind.toString();
    case PAR:
      return (")");
    case OPT:
      return (")?");
    case POS:
      return (")+");
    case KLN:
      return (")*");
    case TERM_CS:
      return "'" + Util.escapeString(str) + "'";
    case TERM_CI:
      return "\"" + Util.escapeString(str) + "\"";
    case TERM_CH:
      return "`" + Util.escapeString(str);
    case TERM_CH_SET:
      return "{" + setToString() + "}";
    case TERM_CH_ANTI_SET:
      return "~{" + setToString() + "}";
    case TERM_CH_UOB:
      return "!{}" + Util.escapeString(str);
    case TERM_BI:
      return "&" + Util.escapeString(str);
    case NONTERMINAL:
      return Util.escapeString(str);
    default:
      return "???";
    }
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
