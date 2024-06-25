package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

public class ARTGrammarInstanceAlt extends ARTGrammarInstance {

  public ARTGrammarInstanceAlt(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Alt";
  }
}
