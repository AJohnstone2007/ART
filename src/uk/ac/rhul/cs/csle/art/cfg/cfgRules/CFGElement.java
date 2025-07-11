package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.util.Util;

public class CFGElement implements Comparable<Object> {

  public int number;
  public boolean isToken;
  public boolean isWhitespace;
  public boolean suppressWhitespace;
  public final CFGKind cfgKind;
  public final String str;

  public final Map<String, String> attributes = new HashMap<>();
  public final Map<String, Integer> rhsNonterminalsAcrossAllProductions = new HashMap<>();

  public CFGElement(CFGKind kind, String s) {
    super();
    this.cfgKind = kind;
    this.str = s;
    switch (this.cfgKind) {
    case C:
      isToken = true;
      suppressWhitespace = true;
      break;
    case T, TI, B:
      isToken = true;
      break;
    }
  }

  public String toStringDetailed() {
    return number + ": " + cfgKind + " " + Util.escapeString(str);
  }

  @Override
  public String toString() {
    switch (cfgKind) {
    case SOS:
      return "$$";
    case EOS:
      return "$";
    case T:
      return "'" + Util.escapeString(str) + "'";
    case TI:
      return "\"" + Util.escapeString(str) + "\"";
    case C:
      return "`" + Util.escapeString(str);
    case B:
      return "&" + Util.escapeString(str);
    case EPS:
      return "#";
    case N:
      return Util.escapeString(str);
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
