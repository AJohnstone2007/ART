package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

public class ARTGrammarInstanceNot extends ARTGrammarInstance {

  public ARTGrammarInstanceNot(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Not";
  }
}
