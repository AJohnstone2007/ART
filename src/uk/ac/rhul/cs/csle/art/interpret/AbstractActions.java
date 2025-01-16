package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActions {
  protected AbstractInterpreter interpreter; // call back

  public abstract AbstractActionsNonterminal init(AbstractInterpreter interpreter);

  public abstract String name();

  protected String lexeme() {
    return interpreter.lexeme();
  }
}
