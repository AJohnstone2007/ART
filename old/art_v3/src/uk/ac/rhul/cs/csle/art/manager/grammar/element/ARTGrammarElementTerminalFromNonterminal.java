package uk.ac.rhul.cs.csle.art.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGrammarElementTerminalFromNonterminal extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalFromNonterminal(String id) {
    super(id);
    // System.out.println("Created terminal from nonterminal " + toEnumerationString(null));
  }

  @Override
  public String toString() {
    return id;
  }

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(id);
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return "ARTTN_" + toEnumerationString();
    else
      return "ART" + prefix + "_" + toEnumerationString();
  }

}
