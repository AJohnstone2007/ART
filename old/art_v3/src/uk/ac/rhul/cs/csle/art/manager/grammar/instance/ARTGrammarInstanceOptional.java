package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

public class ARTGrammarInstanceOptional extends ARTGrammarInstance {

  public ARTGrammarInstanceOptional(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " ?";
  }
}
