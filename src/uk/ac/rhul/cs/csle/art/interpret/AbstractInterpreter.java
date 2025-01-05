package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;

public abstract class AbstractInterpreter {
  public void interpret(AbstractParser parser) {
    System.out.println("interpret() not implemented for interpreter class " + this.getClass().getSimpleName());
  }

}
