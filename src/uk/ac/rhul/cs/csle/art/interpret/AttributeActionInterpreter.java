package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
  private int[] oracle;
  private int oracleIndex;
  private AbstractParser parser;

  public AttributeActionInterpreter() {
    System.out.println("Interpreter set to Attribute-Action");
    artActions = new ARTDefaultActions(); // Set default
    try { // try to connect to user actions
      artActions = (AbstractActions) getClass().getClassLoader().loadClass("ARTGeneratedActions").getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Silently absorb exceptions - uncomment the line below for debugging
      // System.out.println(e.getMessage() + "\nUnable to dynamically load user ARTActions - using default empty actions");
    }
    System.out.println("Attached to " + artActions.getClass().getSimpleName() + ": " + artActions.name());
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
    oracle = parser.constructOracle();
    oracleIndex = 0;
    System.out.println("Parser supplies oracle: ");
    for (var i : oracle)
      System.out.println(i);

    interpretRec();
  }

  private void interpretRec() {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(oracle[oracleIndex++]).seq;
    while (true) {
      System.out.println("Calling action " + node.num);
      artActions.action(node.num, null);
      switch (node.elm.kind) {
      case N:
        interpretRec();
        break;
      case END:
        return;
      }
      node = node.seq;
    }
  }
}
