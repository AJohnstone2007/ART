package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

public class ARTGrammarInstanceCat extends ARTGrammarInstance {

  public ARTGrammarInstanceCat(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Cat";
  }
}
