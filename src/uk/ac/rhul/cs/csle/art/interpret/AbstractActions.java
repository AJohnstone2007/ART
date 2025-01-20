package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActions {
  protected AbstractInterpreter interpreter; // call back

  public abstract String name();

  public abstract AbstractActionsNonterminal init(AbstractInterpreter interpreter, int term);

  protected String lexeme() { // convenience wrapper method
    return interpreter.lexeme();
  }

  protected String lexemeCore() { // convenience wrapper method
    return interpreter.lexemeCore();
  }

  public void interpret(AbstractActionsNonterminal instance) { // convenience wrapper method
    interpreter.interpret(instance);
  }

}
