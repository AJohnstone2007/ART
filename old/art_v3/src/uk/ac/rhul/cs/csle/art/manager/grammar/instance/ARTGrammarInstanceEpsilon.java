package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElement;

public class ARTGrammarInstanceEpsilon extends ARTGrammarInstance {

  public ARTGrammarInstanceEpsilon(int key, ARTGrammarElement payload) {
    super(key, null);
    first.add(payload);
  }

  @Override
  public String toString() {
    return key + " #";
  }
}
