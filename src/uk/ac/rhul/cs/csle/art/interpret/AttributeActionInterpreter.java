package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class AttributeActionInterpreter extends AbstractInterpreter {
  private AbstractActions artActions = new ARTDefaultActions();
  private String lexeme;

  public AttributeActionInterpreter() {
    Util.trace(3, "Interpreter set to Attribute-Action");
    artActions = new ARTDefaultActions(); // Set default
    try { // try to connect to user actions
      artActions = (AbstractActions) getClass().getClassLoader().loadClass("ARTGeneratedActions").getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Silently absorb exceptions - uncomment the line below for debugging
      Util.info(e.getMessage() + "\nUnable to dynamically load user ARTActions - using default empty actions");
    }
    Util.trace(3, "Attached to " + artActions.getClass().getSimpleName() + " " + artActions.name());
  }

  private int intFromTermSymbol(int term) {
    return Integer.parseInt(ScriptInterpreter.iTerms.termSymbolString(term));
  }

  @Override
  public void interpret(AbstractParser parser) {
    this.parser = parser;
    int interpreterTerm = parser.derivations.derivationAsInterpeterTerm();
    // ScriptInterpreter.iTerms.toDot(interpreterTerm, "interpreterTerm.dot");

    var root = artActions.init(this, interpreterTerm);
    interpret(root);
  }

  @Override
  public void interpret(AbstractAttributeBlock attributes) {
    if (attributes == null) Util.fatal("Invalid ARTGeneratedActions - debug, regenerate and recompile");
    CFGNode altNode = parser.lexicalisations.cfgRules.numberToRulesNodeMap.get(intFromTermSymbol(attributes.term));
    var children = ScriptInterpreter.iTerms.termChildren(attributes.term);
    // Util.info("Interpreting production " + altNode.toStringAsProduction() + " with " + children.length + "children");
    int childNumber = 0;

    for (var node = altNode.seq; node.cfgElement.cfgKind != CFGElementKind.END; node = node.seq) // Skip alt node
      attributes.initRHSAttributeBlock(node.num, children[childNumber++]);

    childNumber = -1;
    for (var node = altNode; node.cfgElement.cfgKind != CFGElementKind.END; node = node.seq) {
      // Util.info("node number " + node.num + " childNumber = " + childNumber);
      switch (node.cfgElement.cfgKind) {
      case NONTERMINAL:
        if (!node.delayed) interpret(attributes.getAttributes(node.num));
        break;
      case TRM_CS, TRM_CI, TRM_CH, TRM_BI:
        String[] labelElements = ScriptInterpreter.iTerms.termSymbolString(children[childNumber]).split(",");

        lexeme = parser.lexicalisations.lexeme(node.cfgElement, Integer.parseInt(labelElements[1]), Integer.parseInt(labelElements[2]));
        break;
      }
      childNumber++;
      // Util.info("Calling action at " + node.toStringAsProduction());
      attributes.action(node.num);
    }
  }

  @Override
  public String lexeme() {
    return lexeme;
  }
}
