package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
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
    interpretRec(artActions.createAtributeBlocks(null, parser.cfgRules.startNonterminal.ei), 0);
  }

  void indent(int level) {
    for (int i = 0; i < level; i++)
      System.out.print("  ");
  }

  private void interpretRec(Object[] attributeBlocks, int level) {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(parser.oracle[oracleIndex++]);
    indent(level);
    System.out.println("Entered interpreter with oracle index at " + (oracleIndex - 1) + " and atribute blocks ");
    System.out.println("Parent: " + attributeBlocks[0]);
    for (int i = 1; i < attributeBlocks.length; i++)
      System.out.println(attributeBlocks[i]);
    int nonterminalCount = 1; // this might not be good enough - we need to look up the particular instance
    while (node.elm.kind != CFGKind.END) {
      switch (node.elm.kind) {
      case N:
        indent(level);
        System.out.println("About to interpret nonterminal " + node.elm);
        interpretRec(attributeBlocks == null ? null
            : artActions.createAtributeBlocks(attributeBlocks[nonterminalCount++], parser.cfgRules.elementToNodeMap.get(node.elm).elm.ei), level + 1);
        break;
      }
      artActions.action(node.num, attributeBlocks);
      node = node.seq;
    }
    indent(level);
    System.out.println("Leaving interpreter with oracle index at " + (oracleIndex - 1));

  }
}
