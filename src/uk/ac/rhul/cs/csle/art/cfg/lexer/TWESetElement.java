package uk.ac.rhul.cs.csle.art.cfg.lexer;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;

public class TWESetElement {
  final CFGElement element;
  final int lexemeEnd; // first non-whitespace character
  final int rightExtent;
  boolean suppressed;

  public TWESetElement(CFGElement element, int lexemeStart, int rightExtent) {
    super();
    this.element = element;
    this.lexemeEnd = lexemeStart;
    this.rightExtent = rightExtent;
    suppressed = false;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (suppressed) builder.append("-");
    builder.append(element);
    builder.append(", ");
    builder.append(lexemeEnd);
    builder.append(", ");
    builder.append(rightExtent);
    return builder.toString();
  }

}
