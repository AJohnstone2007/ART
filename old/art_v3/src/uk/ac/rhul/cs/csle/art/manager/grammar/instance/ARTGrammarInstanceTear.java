package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElement;

public class ARTGrammarInstanceTear extends ARTGrammarInstance {

  public ARTGrammarInstanceTear(int key, ARTGrammarElement payload) {
    super(key, payload);
  }

  @Override
  public String toString() {
    return key + " Tear";
  }

}
