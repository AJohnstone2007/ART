package uk.ac.rhul.cs.csle.art.manager.grammar.instance;

public class ARTGrammarInstanceSlot extends ARTGrammarInstance {

  public ARTGrammarInstanceSlot(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return /* key.toString() + " " + */ toGrammarString(".");
  }

}
