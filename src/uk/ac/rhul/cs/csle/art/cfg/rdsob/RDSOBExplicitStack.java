package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBExplicitStack extends AbstractParser {
  class SNode {
    CFGNode returnNode;
    int i_entry;
    DerivationSingletonNode dnAtEntry;
    SNode next;

    public SNode(CFGNode retNode, int i, SNode next, DerivationSingletonNode dn) {
      this.returnNode = retNode;
      this.i_entry = i;
      this.next = next;
      if (dn.next == null) dn.next = new DerivationSingletonNode(gn, null);
      dn = dn.next;
      this.dnAtEntry = dn;
    }
  }

  SNode sn;
  CFGNode gn;

  boolean rdsobExplicitStack() {
    while (true)
      switch (gn.elm.kind) {
      case B, C, T, TI:
        if (match(gn)) {
          i++;
          gn = gn.seq;
        } else if (backtrack()) return false;
        break;
      case N:
        call(gn);
        break;
      case EPS:
        gn = gn.seq;
        break;
      case END:
        // dn_update(gn.alt);
        gn = retrn();
        if (sn == null) return true;
        break;
      case ALT, DO, EOS, KLN, OPT, POS:
        Util.fatal("internal error - unexpected grammar node in rdsobExplicitStack");
      }
  }

  void call(CFGNode caller) {
    sn = new SNode(caller.seq, i, sn, dn);
    gn = getLHS(gn).alt.seq;
  }

  CFGNode retrn() {
    CFGNode tmp = sn.returnNode;
    sn = sn.next;
    return tmp;
  }

  boolean backtrack() { // return true if no backtrack target found
    while (true) {
      while (gn.elm.kind != CFGKind.END)
        gn = gn.seq;
      if (gn.alt.alt == null) {
        gn = retrn();
        if (sn == null) return true;
      } else {
        i = sn.i_entry;
        dn = sn.dnAtEntry;
        gn = gn.alt.alt.seq;
        dn.gn = gn;
        break;
      }
    }
    return false;
  }

  @Override
  public void parse() {
    gn = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt.seq;
    i = 0;
    dnRoot = dn = new DerivationSingletonNode(cfgRules.endOfStringNode, null);
    sn = new SNode(cfgRules.endOfStringNode, 0, null, dn);
    inLanguage = rdsobExplicitStack() && input[i] == 0;
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
