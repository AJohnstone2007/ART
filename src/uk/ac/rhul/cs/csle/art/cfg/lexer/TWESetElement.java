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
