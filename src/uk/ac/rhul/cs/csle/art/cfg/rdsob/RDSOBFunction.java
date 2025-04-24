package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBFunction extends AbstractParser {

  public boolean match(CFGNode gn, int tokenIndex) {
    return lexer.firstLexicalisation.get(tokenIndex).element == gn.element;
  }

  boolean rdsobFunction(CFGNode lhs) {
    if (dn.next == null) dn.next = new DerivationSingletonNode(cfgRules.endOfStringNode, null);
    dn = dn.next;
    DerivationSingletonNode dnAtEntry = dn;

    int i_entry = tokenIndex;
    altLoop: for (CFGNode tmp = lhs.alt; tmp != null; tmp = tmp.alt) {
      tokenIndex = i_entry;
      dn = dnAtEntry;
      dn.gn = tmp;
      CFGNode gn = tmp.seq;
      while (true) {
        switch (gn.element.cfgKind) {
        case B, C, T, TI:
          if (lexer.firstLexicalisation.get(tokenIndex++).element == gn.element) {
            gn = gn.seq;
            break;
          } else
            continue altLoop;
        case N:
          if (rdsobFunction(cfgRules.elementToNodeMap.get(gn.element))) {
            gn = gn.seq;
            break;
          } else
            continue altLoop;
        case EPS:
          gn = gn.seq;
          break;
        case END:
          return true;
        case ALT, DO, EOS, KLN, OPT, POS:
          Util.fatal("internal error - unexpected grammar node in rdsobFunction: " + gn);
        }
      }
    }
    return false;
  }

  @Override
  public void parse(String input, CFGRules cfgRules, AbstractLexer lexer) {
    inLanguage = false;
    this.input = input;
    this.cfgRules = cfgRules;
    this.lexer = lexer;
    tokenIndex = 0;
    dnRoot = dn = new DerivationSingletonNode(cfgRules.endOfStringNode, null);
    inLanguage = rdsobFunction(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal)) && lexer.firstLexicalisation.get(tokenIndex).element.number == 0;
    if (!inLanguage) Util.echo("Syntax error at location " + tokenIndex, Util.lineNumber(tokenIndex, lexer.inputString), lexer.inputString);
  }

  protected DerivationSingletonNode dnRoot, dn;

  public class DerivationSingletonNode {
    public CFGNode gn;
    public DerivationSingletonNode next;

    public DerivationSingletonNode(CFGNode gn, DerivationSingletonNode next) {
      super();
      this.gn = gn;
      this.next = next;
    }

    @Override
    public String toString() {
      return gn.toString();
    }
  }
}
