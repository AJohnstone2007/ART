package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActions {
  public abstract AbstractActionsNonterminal init();

  public abstract String name();

  protected static String lexeme() {
    return "666";
  }
}
