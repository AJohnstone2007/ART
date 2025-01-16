package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.util.Util;

public abstract class AbstractInterpreter {
  public void interpret(AbstractParser parser) {
    Util.error("interpret() not implemented for interpreter class " + this.getClass().getSimpleName());
  }

  protected String lexeme() {
    Util.error("lexeme() not implemented for interpreter class " + this.getClass().getSimpleName());
    return "";
  }

}
