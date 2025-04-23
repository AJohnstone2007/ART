package uk.ac.rhul.cs.csle.art.cfg.lexer;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;

public class TWESetElement {
  final CFGElement element;
  final int leftExtent;
  final int lexemeEnd; // first non-whitespace character
  final int rightExtent;
  boolean suppressed;

  public TWESetElement(CFGElement element, int leftExtent, int lexemeEnd, int rightExtent) {
    super();
    this.element = element;
    this.leftExtent = leftExtent;
    this.lexemeEnd = lexemeEnd;
    this.rightExtent = rightExtent;
    suppressed = false;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (suppressed) builder.append("*");
    builder.append(element);
    builder.append(", ");
    builder.append(leftExtent);
    builder.append(", ");
    builder.append(lexemeEnd);
    builder.append(", ");
    builder.append(rightExtent);
    return builder.toString();
  }

}
