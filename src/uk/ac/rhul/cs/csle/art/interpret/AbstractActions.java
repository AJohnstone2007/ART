package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActions {
  protected AbstractInterpreter interpreter; // call back

  public abstract AbstractActionsNonterminal init(AbstractInterpreter interpreter, int term);

  public abstract String name();

  protected String lexeme() {
    return interpreter.lexeme();
  }

  protected String lexemeCore() {
    return interpreter.lexemeCore();
  }

  public void interpret(AbstractActionsNonterminal instance) {
    interpreter.interpretUsingDerivationTermRec(instance.term, instance);
  }

}
