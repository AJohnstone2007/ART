package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
  private int oracleIndex, tokenIndex;
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
    System.out.println("Starting interpretation");
    this.parser = parser;
    parser.constructOracle();
    tokenIndex = oracleIndex = 0;
    interpretRec(0, artActions.init(this));
  }

  void indent(int level) {
    for (int i = 0; i < level; i++)
      System.out.print(" ");
  }

  private void interpretRec(int level, AbstractActionsNonterminal attributes) {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(parser.oracle[oracleIndex++]);
    // indent(level);
    // System.out.println("Entered interpreter with oracle index at " + (oracleIndex - 1));
    while (node.elm.kind != CFGKind.END) {
      switch (node.elm.kind) {
      case N:
        // indent(level);
        // System.out.println("About to interpret nonterminal " + node.elm);
        interpretRec(level + 1, attributes.call(node.num));
        break;
      case T, TI, C, B:
        tokenIndex++;
      }
      attributes.action(node.num);
      node = node.seq;
    }
    // indent(level);
    // System.out.println("Leaving interpreter with oracle index at " + (oracleIndex - 1));

  }

  @Override
  public String lexeme() {
    return parser.lexeme(tokenIndex - 1);
  }
}
