package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGrammarInstanceNonterminal extends ARTGrammarInstance {

  public ARTGrammarInstanceNonterminal(int key, ARTGrammarElement payload) {
    super(key, payload);
  }

  @Override
  public String toString() {
    return ARTText.toIdentifier(key + " " + payload);
  }

  @Override
  public String instanceString() {
    return ARTText.toIdentifier(((ARTGrammarElementNonterminal) payload).getId() + instanceNumberWithinProduction);
  }
}
