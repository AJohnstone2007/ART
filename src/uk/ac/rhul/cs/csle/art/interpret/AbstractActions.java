package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActions {
  protected AbstractInterpreter interpreter; // call back

  public abstract String name();

  public abstract AbstractAttributeBlock init(AbstractInterpreter interpreter, int term);

  protected String lexeme() { // convenience wrapper method
    return interpreter.lexeme();
  }

  protected String lexemeCore() { // convenience wrapper method
    return interpreter.lexemeCore();
  }

  protected void interpret(AbstractAttributeBlock instance) { // convenience wrapper method
    interpreter.interpret(instance);
  }

  protected Object plugin(Object... args) {
    return interpreter.parser.cfgRules.iTerms.plugin.plugin(args);
  }

}
