package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.Util;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
  private int previousToken;

  public AttributeActionInterpreter() {
    // Util.info("Interpreter set to Attribute-Action");
    artActions = new ARTDefaultActions(); // Set default
    try { // try to connect to user actions
      artActions = (AbstractActions) getClass().getClassLoader().loadClass("ARTGeneratedActions").getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Silently absorb exceptions - uncomment the line below for debugging
      // Util.info(e.getMessage() + "\nUnable to dynamically load user ARTActions - using default empty actions");
    }
    Util.info("Attached to " + artActions.getClass().getSimpleName() + " " + artActions.name());
  }

  private int intFromTermSymbol(int term) {
    return Integer.parseInt(parser.cfgRules.iTerms.termSymbolString(term));
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
    int interpreterTerm = parser.derivations.derivationAsInterpeterTerm();
    // ScriptTermInterpreter.iTerms.toDot(interpreterTerm, "interpreterTerm.dot");

    var root = artActions.init(this, interpreterTerm);
    interpret(root);
  }

  @Override
  public void interpret(AbstractAttributeBlock attributes) {
    if (attributes == null) Util.fatal("Invalid ARTGeneratedActions - debug, regenerate and recompile");
    CFGNode altNode = parser.cfgRules.numberToRulesNodeMap.get(intFromTermSymbol(attributes.term));
    var children = parser.cfgRules.iTerms.termChildren(attributes.term);
    int childNumber = 0;

    for (var node = altNode.seq; node.cfgElement.cfgKind != CFGKind.END; node = node.seq) // Skip alt node
      attributes.initRHSAttributeBlock(node.num, children[childNumber++]);

    childNumber = -1;
    for (var node = altNode; node.cfgElement.cfgKind != CFGKind.END; node = node.seq) {
      // Util.info("node number " + node.num + " childNumber = " + childNumber + " previous token = " + previousToken);
      switch (node.cfgElement.cfgKind) {
      case NONTERMINAL:
        if (!node.delayed) interpret(attributes.getAttributes(node.num));
        break;
      case TRM_CS, TRM_CI, TRM_CH, TRM_BI:
        previousToken = -intFromTermSymbol(children[childNumber]) - 1; // Change this to get token index from the derivation tree
        break;
      }
      childNumber++;
      // Util.info("Calling action " + node.toStringAsProduction());
      attributes.action(node.num);
    }
  }

  @Override
  public String lexeme() {
    return "Unimplemented lexeme()"; // parser.lexer.lexeme(previousToken);
  }
}
