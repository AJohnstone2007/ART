package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;

public class RDSOBFunction extends AbstractParser {

  public boolean match(CFGNode gn, int tokenIndex) {
    return lexicalisations.getSlice(tokenIndex)[0].cfgElement == gn.cfgElement;
  }

  boolean rdsobFunction(CFGNode lhs) {
    if (dn.next == null) dn.next = new DerivationSingletonNode(lexicalisations.cfgRules.endOfStringNode, null);
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
          if (lexicalisations.getSlice(inputIndex)[0].cfgElement == gn.cfgElement) {
            Util.trace(8, "Match " + gn);
            inputIndex++;
            gn = gn.seq;
            break;
          } else
            Util.trace(8, "Mismatch " + gn);
          continue altLoop;
        case NONTERMINAL:
          if (rdsobFunction(lexicalisations.cfgRules.elementToRulesNodeMap.get(gn.cfgElement))) {
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
  public void parse(AbstractLexicalisations lexicalisations) {
    this.lexicalisations = lexicalisations;

    inputIndex = 0;
    dnRoot = dn = new DerivationSingletonNode(lexicalisations.cfgRules.endOfStringNode, null);
    inLanguage = rdsobFunction(lexicalisations.cfgRules.elementToRulesNodeMap.get(lexicalisations.cfgRules.startNonterminal))
        && lexicalisations.getSlice(inputIndex)[0].cfgElement.number == 0;
    if (!inLanguage) Util.echo("Syntax error at location " + inputIndex, Util.lineNumber(inputIndex, lexicalisations.inputString), lexicalisations.inputString);
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
