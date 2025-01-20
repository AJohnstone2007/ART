package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
  private int tokenIndex;
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
    tokenIndex = 0;
    interpretUsingDerivationTermRec(interpreterTerm, artActions.init(this, interpreterTerm));
  }

  @Override
  public void interpretUsingDerivationTermRec(int term, AbstractActionsNonterminal attributes) {
    CFGNode node = parser.cfgRules.numberToNodeMap.get(Integer.parseInt(parser.cfgRules.iTerms.termSymbolString(term)));
    var children = parser.cfgRules.iTerms.termChildren(term);
    int childNumber = -1;
    System.out.println("Entered tree-based interpreter at cfgNode " + node.num);
    while (node.elm.kind != CFGKind.END) {
      System.out.println("About to interpret node " + node.elm + " at child " + childNumber);
      switch (node.elm.kind) {
      case N:
        if (node.delayed)
          attributes.call(node.num, children[childNumber]);
        else
          interpretUsingDerivationTermRec(children[childNumber], attributes.call(node.num, children[childNumber]));
        break;
      case T, TI, C, B:
        tokenIndex++;
      }
      attributes.action(node.num);
      childNumber++;
      node = node.seq;
    }
    System.out.println("Leaving tree-based interpreter at cfgNode " + node.num);
  }

  @Override
  public String lexeme() {
    return parser.lexeme(tokenIndex - 1);
  }
}
