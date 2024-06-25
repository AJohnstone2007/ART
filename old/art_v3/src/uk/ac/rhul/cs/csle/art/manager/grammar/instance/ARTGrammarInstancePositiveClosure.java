package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

public class ARTGrammarInstancePositiveClosure extends ARTGrammarInstance {

  public ARTGrammarInstancePositiveClosure(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " +";
  }
}
