package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
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
    parser.constructOracle();
    oracleIndex = 0;
    // System.out.println("Parser supplies oracle: ");
    // for (var i : parser.oracle)
    // System.out.println(i);
    //

    interpretRec(artActions.createAtributeBlocks(parser.cfgRules.startNonterminal.ei));
  }

  private void interpretRec(Object[] attributeBlocks) {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(parser.oracle[oracleIndex++]).seq;
    while (true) {
      System.out.println("Calling action " + node.num);
      artActions.action(node.num, attributeBlocks);
      switch (node.elm.kind) {
      case N:
        CFGNode lhs = parser.cfgRules.elementToNodeMap.get(node.elm);
        System.out.println("Creating attribute block for node " + node.num + " with element " + node.elm + " which has lhs node " + lhs.num);
        interpretRec(artActions.createAtributeBlocks(lhs.elm.ei));
        break;
      case END:
        return;
      }
      node = node.seq;
    }
  }
}
