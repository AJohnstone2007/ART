package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

import java.util.HashMap;
import java.util.Map;

public class CFGElement implements Comparable<Object> {

  public int ei;
  public final CFGKind kind;
  public final String str;

  public final Map<String, String> attributes = new HashMap<>();
  public final Map<String, Integer> rhsNonterminalsAcrossAllProductions = new HashMap<>();

  public CFGElement(CFGKind kind, String s) {
    super();
    this.kind = kind;
    this.str = s;
  }

  public String toStringDetailed() {
    return ei + ": " + kind + " " + str;
  }

  @Override
  public String toString() {
    String ret;
    switch (kind) {
    case EOS:
      return "$";
    case T:
      return "'" + str + "'";
    case C:
      return "`" + str;
    case B:
      return "&" + str;
    case EPS:
      return "#";
    case N:
      return str;
    case ALT:
      return "|";
    case END:
      return "END";
    case DO:
      return ")";
    case OPT:
      return ")?";
    case POS:
      return ")+";
    case KLN:
      return ")*";
    default:
      return "???";
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((kind == null) ? 0 : kind.hashCode());
    result = prime * result + ((str == null) ? 0 : str.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CFGElement other = (CFGElement) obj;
    if (kind != other.kind) return false;
    if (str == null) {
      if (other.str != null) return false;
    } else if (!str.equals(other.str)) return false;
    return true;
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) return 1;
    CFGElement other = (CFGElement) o;
    if (kind.ordinal() > other.kind.ordinal())
      return 1;
    else if (kind.ordinal() < other.kind.ordinal())
      return -1;
    else
      return str.compareTo(other.str);
  }
}
