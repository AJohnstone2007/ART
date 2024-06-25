package uk.ac.rhul.cs.csle.art.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.lex.ARTLexerBase;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGrammarElementTerminalBuiltin extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalBuiltin(String id) throws ARTException {
    super(id);
    if (!ARTLexerBase.isValidBuiltin(id)) throw new ARTException("unknown builtin &" + id);

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
