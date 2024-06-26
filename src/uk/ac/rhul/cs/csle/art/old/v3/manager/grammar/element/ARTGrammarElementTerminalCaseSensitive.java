package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.old.v4.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.v4.util.text.ARTText;

public class ARTGrammarElementTerminalCaseSensitive extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalCaseSensitive(String id) {
    super(id);
    if (id.length() == 0) throw new ARTUncheckedException("empty case sensitive terminal " + id + " is not allowed - the empty string is denoted by #");
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

  @Override
  public String toParaterminalString() {
    return "ART_CSP_" + ARTText.toIdentifier(toString().substring(1, toString().length() - 1));
  }
}
