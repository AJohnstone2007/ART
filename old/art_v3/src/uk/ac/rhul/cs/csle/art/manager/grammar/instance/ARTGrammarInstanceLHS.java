package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGrammarInstanceLHS extends ARTGrammarInstance {

  public ARTGrammarInstanceLHS(int key, ARTGrammarElement payload) {
    super(key, payload);
    isLHS = true;
    lhsL = this;
  }

  @Override
  public String toString() {
    return ARTText.toIdentifier(key + " LHS " + payload);
  }
}
