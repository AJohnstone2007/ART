package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v4.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.v4.util.text.ARTText;

public class ARTGrammarElementTerminalBuiltin extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalBuiltin(String id) {
    super(id);
    if (!ARTLexerV3.isValidBuiltin(id)) {
      throw new ARTUncheckedException("unknown builtin &" + id);
    }

  }

  @Override
  public String toString() {
    return "&" + id;
  }

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(id);
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return "ARTTB_" + toEnumerationString();
    else
      return "ART" + prefix + "_" + toEnumerationString();
  }

}
