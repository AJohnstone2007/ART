package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.Parser;

public class AttributeActionInterpreter extends Interpreter {
  public AttributeActionInterpreter() {
    System.out.println("Interpreter set to Attribute-Action");
  }

  @Override
  public void interpret(Parser parser) {
    parser.interpretAttributeAction();
  }
}
