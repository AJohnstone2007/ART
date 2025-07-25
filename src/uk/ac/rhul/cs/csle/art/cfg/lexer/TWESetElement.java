package uk.ac.rhul.cs.csle.art.cfg.lexer;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;

public class TWESetElement {
  public final CFGElement cfgElement;
  public final int leftExtent;
  public final int lexemeEnd; // first non-whitespace character
  public final int rightExtent;
  public boolean suppressed;

  public TWESetElement(CFGElement element, int leftExtent, int lexemeEnd, int rightExtent) {
    super();
    this.cfgElement = element;
    this.leftExtent = leftExtent;
    this.lexemeEnd = lexemeEnd;
    this.rightExtent = rightExtent;
    suppressed = false;
    // Util.debug("Constructed TWESetElement " + this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cfgElement == null) ? 0 : cfgElement.hashCode());
    result = prime * result + leftExtent;
    result = prime * result + lexemeEnd;
    result = prime * result + rightExtent;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TWESetElement other = (TWESetElement) obj;
    if (cfgElement == null) {
      if (other.cfgElement != null) return false;
    } else if (!cfgElement.equals(other.cfgElement)) return false;
    if (leftExtent != other.leftExtent) return false;
    if (lexemeEnd != other.lexemeEnd) return false;
    if (rightExtent != other.rightExtent) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (suppressed) builder.append("*");
    builder.append(cfgElement.toStringDetailed());
    builder.append(", ");
    builder.append(leftExtent);
    builder.append(", ");
    builder.append(lexemeEnd);
    builder.append(", ");
    builder.append(rightExtent);
    return builder.toString();
  }
}
