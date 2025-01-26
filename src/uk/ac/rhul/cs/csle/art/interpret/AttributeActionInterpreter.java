package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
  private AbstractParser parser;
  private int previousToken;

  public AttributeActionInterpreter() {
    System.out.println("Interpreter set to Attribute-Action");
    artActions = new ARTDefaultActions(); // Set default
    try { // try to connect to user actions
      artActions = (AbstractActions) getClass().getClassLoader().loadClass("ARTGeneratedActions").getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Silently absorb exceptions - uncomment the line below for debugging
      // System.out.println(e.getMessage() + "\nUnable to dynamically load user ARTActions - using default empty actions");
    }
    System.out.println("Attached to " + artActions.getClass().getSimpleName() + " " + artActions.name());
  }

  private int intFromTermSymbol(int term) {
    return Integer.parseInt(parser.cfgRules.iTerms.termSymbolString(term));
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
    var root = artActions.init(this, parser.derivationAsInterpeterTerm());
    interpret(root);
  }

  @Override
  public void interpret(AbstractAttributeBlock attributes) {
    CFGNode altNode = parser.cfgRules.numberToNodeMap.get(intFromTermSymbol(attributes.term));
    var children = parser.cfgRules.iTerms.termChildren(attributes.term);
    int childNumber = 0;

    for (var node = altNode.seq; node.elm.kind != CFGKind.END; node = node.seq) // Skip alt node
      attributes.initRHSAttributeBlock(node.num, children[childNumber++]);

    childNumber = -1;
    for (var node = altNode; node.elm.kind != CFGKind.END; node = node.seq) {
      // System.out.println("node number " + node.num + " childNumber = " + childNumber + " previous token = " + previousToken);
      switch (node.elm.kind) {
      case N:
        if (!node.delayed) interpret(attributes.getAttributes(node.num));
        break;
      case T, TI, C, B:
        previousToken = -intFromTermSymbol(children[childNumber]) - 1; // Change this to get token index from the derivation tree
        break;
      }
      childNumber++;
      attributes.action(node.num);
    }
  }

  @Override
  public String lexeme() {
    return parser.lexeme(previousToken);
  }
}
