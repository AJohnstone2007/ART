package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBFunction extends AbstractParser {

  public boolean match(CFGNode gn, int tokenIndex) {
    return lexer.tweSlices[tokenIndex][0].cfgElement == gn.cfgElement;
  }

  boolean rdsobFunction(CFGNode lhs) {
    if (dn.next == null) dn.next = new DerivationSingletonNode(cfgRules.endOfStringNode, null);
    dn = dn.next;
    DerivationSingletonNode dnAtEntry = dn;

    int i_entry = inputIndex;
    altLoop: for (CFGNode tmp = lhs.alt; tmp != null; tmp = tmp.alt) {
      inputIndex = i_entry;
      dn = dnAtEntry;
      dn.cfgNode = tmp;
      CFGNode gn = tmp.seq;
      while (true) {
        switch (gn.cfgElement.cfgKind) {
        case TRM_BI, TRM_CH, TRM_CS, TRM_CI:
          if (lexer.tweSlices[inputIndex][0].cfgElement == gn.cfgElement) {
            Util.trace(8, "Match " + gn);
            inputIndex++;
            gn = gn.seq;
            break;
          } else
            Util.trace(8, "Mismatch " + gn);
          continue altLoop;
        case NONTERMINAL:
          if (rdsobFunction(cfgRules.elementToNodeMap.get(gn.cfgElement))) {
            gn = gn.seq;
            break;
          } else
            continue altLoop;
        case EPSILON:
          gn = gn.seq;
          break;
        case END:
          return true;
        case ALT, PAR, EOS, KLN, OPT, POS:
          Util.fatal("internal error - unexpected grammar node in rdsobFunction: " + gn);
        }
      }
    }
    return false;
  }

  @Override
  public void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules) {
    inLanguage = false;
    this.input = input;
    this.cfgRules = cfgRules;
    this.lexer = lexer;

    if (!lexer.lex(input, cfgRules, chooseRules)) return;

    inputIndex = 0;
    dnRoot = dn = new DerivationSingletonNode(cfgRules.endOfStringNode, null);
    inLanguage = rdsobFunction(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal)) && lexer.tweSlices[inputIndex][0].cfgElement.number == 0;
    if (!inLanguage) Util.echo("Syntax error at location " + inputIndex, Util.lineNumber(inputIndex, lexer.inputString), lexer.inputString);
  }

  protected DerivationSingletonNode dnRoot, dn;

  public class DerivationSingletonNode {
    public CFGNode cfgNode;
    public DerivationSingletonNode next;

    public DerivationSingletonNode(CFGNode cfgNode, DerivationSingletonNode next) {
      super();
      this.cfgNode = cfgNode;
      this.next = next;
    }

    @Override
    public String toString() {
      return cfgNode.toString();
    }
  }
}
