package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

public class ARTGrammarInstanceAction extends ARTGrammarInstance {

  public ARTGrammarInstanceAction(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Action";
  }

}
