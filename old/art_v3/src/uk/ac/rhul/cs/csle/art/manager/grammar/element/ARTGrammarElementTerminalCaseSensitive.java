package uk.ac.rhul.cs.csle.art.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGrammarElementTerminalCaseSensitive extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalCaseSensitive(String id) {
    super(id);
  }

  @Override
  public String toString() {
    return "'" + id + "'";
  }

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(id);
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return "ARTTS_" + toEnumerationString();
    else
      return "ART" + prefix + "_" + toEnumerationString();
  }
}
