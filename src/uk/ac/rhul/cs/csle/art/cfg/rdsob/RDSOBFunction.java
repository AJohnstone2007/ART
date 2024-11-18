package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.ParserBase;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBFunction extends ParserBase {

  boolean rdsobFunction(CFGNode lhs) {
    if (dn.next == null) dn.next = new DerivationSingletonNode(cfgRules.endOfStringNode, null);
    dn = dn.next;
    DerivationSingletonNode dnAtEntry = dn;

    int i_entry = i;
    altLoop: for (CFGNode tmp = lhs.alt; tmp != null; tmp = tmp.alt) {
      i = i_entry;
      dn = dnAtEntry;
      dn.gn = tmp;
      CFGNode gn = tmp.seq;
      while (true) {
        switch (gn.elm.kind) {
        case B, C, T, TI:
          if (match(gn)) {
            i++;
            gn = gn.seq;
            break;
          } else
            continue altLoop;
        case N:
          if (rdsobFunction(getLHS(gn))) {
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
  public void parse() {
    i = 0;
    dnRoot = dn = new DerivationSingletonNode(cfgRules.endOfStringNode, null);
    inLanguage = rdsobFunction(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal)) && input[i] == 0;
    if (!inLanguage) Util.echo("Syntax error at location " + i, Util.lineNumber(i, inputString), inputString);
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
