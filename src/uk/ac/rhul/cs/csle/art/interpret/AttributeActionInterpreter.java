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
    CFGNode startNode = parser.cfgRules.numberToNodeMap.get(parser.oracle[0]);

    System.out.println("oracle[0] is " + startNode.num + " " + startNode.toStringAsProduction());
    interpretRec();
  }

  /*
   * Rules for creation of attribute blocks
   *
   * LHS block is aBlocks[0] and must be created by the caller
   *
   * RHS blocks are aBlocks[1], aBlocks[2],... and are created on entry, with elements being called off at each nonterminal
   *
   * Difficulty: nonterminals are not all in the same order, so we need to maintain separate counters for each instance label - meaningless: all productions are
   * equivalent
   */
  private void interpretRec() {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(parser.oracle[oracleIndex++]).seq;
    while (true) {
      // System.out.println("Calling action " + node.num);
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
