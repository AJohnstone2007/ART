package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;

public abstract class AbstractActions {
  protected AbstractInterpreter interpreter; // call back

  public abstract String name();

  public abstract AbstractAttributeBlock init(AbstractInterpreter interpreter, int term);

  protected String lexeme() { // convenience wrapper method
    return interpreter.lexeme();
  }

  // protected String lexemeCore() { // convenience wrapper method
  // return interpreter.lexemeCore();
  // }
  //
  // public String lexemeAsString() {
  // return Util.unescapeString(lexeme(), 1, 1); // strip a delimiter from each end
  // }

  protected void interpret(AbstractAttributeBlock instance) { // convenience wrapper method
    interpreter.interpret(instance);
  }

  protected Object plugin(Object... args) {
    return ScriptInterpreter.iTerms.plugin.plugin(args);
  }

}
