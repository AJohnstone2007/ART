package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;

public class eSOSInterpreter extends AbstractInterpreter {
  private AbstractParser parser;

  public eSOSInterpreter() {
    System.out.println("Interpreter set to eSOS");
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
  }
}
