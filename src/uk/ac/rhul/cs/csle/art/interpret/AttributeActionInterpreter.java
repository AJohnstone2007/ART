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
    int interpreterTerm = parser.derivationAsInterpeterTerm();
    System.out.println("InterpeterTerm: " + parser.cfgRules.iTerms.toString(interpreterTerm));
    parser.constructOracle();
    tokenIndex = oracleIndex = 0;
    // interpretUsingOracleRec(0, artActions.init(this, 0), 0);
    interpretUsingDerivationTermRec(0, interpreterTerm, artActions.init(this, interpreterTerm));
  }

  void indent(int level) {
    for (int i = 0; i < level; i++)
      System.out.print(" ");
  }

  @Override
  public void interpretUsingDerivationTermRec(int level, int term, AbstractActionsNonterminal attributes) {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(Integer.parseInt(parser.cfgRules.iTerms.termSymbolString(term)));
    var children = parser.cfgRules.iTerms.termChildren(term);
    int childNumber = -1;
    indent(level);
    System.out.println("Entered tree-based interpreter at cfgNode " + node.num);
    while (node.elm.kind != CFGKind.END) {
      indent(level);
      System.out.println("About to interpret node " + node.elm + " at child " + childNumber);
      switch (node.elm.kind) {
      case N:
        indent(level);
        if (node.delayed) {
          indent(level);
          attributes.call(node.num, children[childNumber]);
          System.out.println("Skipping delated nonterminal");
        } else {
          indent(level);
          System.out.println("Calling nonterminal");
          interpretUsingDerivationTermRec(level + 1, children[childNumber], attributes.call(node.num, children[childNumber]));
        }
        break;
      case T, TI, C, B:
        tokenIndex++;
      }
      attributes.action(node.num);
      childNumber++;
      node = node.seq;
    }
    indent(level);
    System.out.println("Leaving tree-based interpreter at cfgNode " + node.num);
  }

  private void interpretUsingOracleRec(int level, AbstractActionsNonterminal attributes, int term) {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(parser.oracle[oracleIndex++]);
    // indent(level);
    // System.out.println("Entered oracle-based interpreter with index at " + (oracleIndex - 1));
    while (node.elm.kind != CFGKind.END) {
      switch (node.elm.kind) {
      case N:
        // indent(level);
        // System.out.println("About to interpret nonterminal " + node.elm);
        interpretUsingOracleRec(level + 1, attributes.call(node.num, term), term);
        break;
      case T, TI, C, B:
        tokenIndex++;
      }
      attributes.action(node.num);
      node = node.seq;
    }
    // indent(level);
    // System.out.println("Leaving oracle-based interpreter with index at " + (oracleIndex - 1));

  }

  @Override
  public String lexeme() {
    return parser.lexeme(tokenIndex - 1);
  }
}
