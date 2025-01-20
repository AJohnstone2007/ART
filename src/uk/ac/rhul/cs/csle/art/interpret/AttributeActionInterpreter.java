package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
  private AbstractParser parser;
  private int tokenIndex;

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
    tokenIndex = 0;
    int derivationInterpeterTerm = parser.derivationAsInterpeterTerm();
    System.out.println("Interpeter term: " + parser.cfgRules.iTerms.toString(derivationInterpeterTerm));
    interpret(artActions.init(this, derivationInterpeterTerm));
  }

  @Override
  public void interpret(AbstractActionsNonterminal attributes) {
    CFGNode altNode = parser.cfgRules.numberToNodeMap.get(Integer.parseInt(parser.cfgRules.iTerms.termSymbolString(attributes.term)));
    var children = parser.cfgRules.iTerms.termChildren(attributes.term);
    int childNumber = 0;

    for (var node = altNode.seq; node.elm.kind != CFGKind.END; node = node.seq) // Skip alt node
      attributes.initAttributes(node.num, children[childNumber++]);

    for (var node = altNode; node.elm.kind != CFGKind.END; node = node.seq) {
      switch (node.elm.kind) {
      case N:
        if (!node.delayed) interpret(attributes.getAttributes(node.num));
        break;
      case T, TI, C, B:
        tokenIndex++;
        break;
      }
      attributes.action(node.num);
    }
  }

  @Override
  public String lexeme() {
    return parser.lexeme(tokenIndex - 1);
  }
}
