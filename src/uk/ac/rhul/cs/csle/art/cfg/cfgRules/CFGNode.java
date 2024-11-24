package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Util;

public class CFGNode {
  public static String caseSensitiveTerminalStrop = "'";
  public static String caseInsensitiveTerminalStrop = "\"";
  public static String characterTerminalStrop = "`";
  public static String builtinTerminalStrop = "&";
  private static ITerms iTerms = null;

  public int num;
  public final CFGElement elm; // Grammar element
  public CFGNode seq; // sequence link
  public CFGNode alt; // alternate link
  public final GIFTKind giftKind;
  public int actionSeq; // Holds an action term used by attribute evaluators

  private static int nextUniqueNumericLabel = 1;

  public CFGNode(CFGRules grammar, CFGKind kind, String str, int actionSeq, GIFTKind giftKInd, CFGNode previous, CFGNode parent) {
    super();
    if (str == null) str = "" + nextUniqueNumericLabel++; // EBNF and ALT nodes have null string - uniquify
    this.elm = grammar.findElement(kind, str);
    this.actionSeq = actionSeq;
    this.giftKind = giftKInd;
    if (previous != null) previous.seq = this;
    if (parent != null) parent.alt = this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + num;
    result = prime * result + ((elm == null) ? 0 : elm.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CFGNode other = (CFGNode) obj;
    if (num != other.num) return false;
    if (elm == null) {
      if (other.elm != null) return false;
    } else if (!elm.equals(other.elm)) return false;
    return true;
  }

  private String giftToString(GIFTKind kind) {
    switch (kind) {
    case NONE:
      return "";
    case OVER:
      return "^^";
    case TEAR:
      return "^^^";
    case UNDER:
      return "^";
    default:
      return "^???";
    }
  }

  public String toStringDot() {
    String actionToString = actionSeq == 0 ? "" : ("\n" + iTerms.toString(actionSeq));
    String ret = num + " ";
    if (actionSeq != 0) ret += /* action + */ " ";
    switch (elm.kind) {
    case EOS, ALT, DO, KLN, OPT, POS:
      return ret + elm.kind;
    case T, C, B, N, EPS:
      return ret + elm.kind + "\n" + elm.str + giftToString(giftKind);
    case END:
      return ret + "END " + "\n(" + seq.num + "," + alt.num + ")";
    default:
      return "???";
    }
  }

  @Override
  public String toString() {
    switch (elm.kind) {
    case EOS:
      return "EOS node";
    case T:
      return caseSensitiveTerminalStrop + elm.str + caseSensitiveTerminalStrop + giftToString(giftKind);
    case C:
      return characterTerminalStrop + elm.str + giftToString(giftKind);
    case B:
      return builtinTerminalStrop + elm.str + giftToString(giftKind);
    case EPS:
      return "#" + giftToString(giftKind);
    case N:
      return elm.str + giftToString(giftKind);
    case ALT:
      return "|";
    case END:
      return "";
    case DO:
      return ")";
    case OPT:
      return ")?";
    case POS:
      return ")+";
    case KLN:
      return ")*";
    default:
      return "???";
    }
  }

  public String toStringAsProduction() { // convenience method for common use case
    return toStringAsProduction(" ::=", " .");
  }

  public String toStringAsProduction(String rewritesDenotation, String slotDenotation) { // Print a node in the context of its production
    // System.out.println("toStringAsProduction called on " + num + ": " + this);
    StringBuilder sb = new StringBuilder();

    if (CFGRules.isLHS(this) || (seq == null && alt == null))
      sb.append(elm.str);
    else if (seq.elm.kind == CFGKind.EOS)
      sb.append(seq);
    else {
      CFGNode tmp;
      for (tmp = this; !(tmp.elm.kind == CFGKind.END && CFGRules.isLHS(tmp.seq)); tmp = tmp.seq) {// Locate the end of this production
        // System.out.println("toStringAsProduction at " + tmp + " with next-in-sequence element " + tmp.seq.elm);
      }
      sb.append(tmp.seq.elm.str + rewritesDenotation); // Render LHS

      toStringAsSequenceRec(sb, tmp.alt, slotDenotation, this); // Render RHS
    }
    return sb.toString();
  }

  private void toStringAsSequenceRec(StringBuilder sb, CFGNode alt, String slotDenotation, CFGNode targetNode) {
    // System.out.println("toStringAsSequenceRec called on " + this.instanceNumber + ":" + this);
    if (alt.elm.kind != CFGKind.ALT) Util.fatal("toStringAsSequenceRec() called on node " + alt.num + " which is not not an ALT node");
    for (CFGNode tmpSeq = alt.seq;; tmpSeq = tmpSeq.seq) { // run down this sequence
      if (tmpSeq == targetNode) sb.append(slotDenotation);
      if (tmpSeq.elm.kind != CFGKind.END && tmpSeq.alt != null) { // If this element has an alt, then recursively process it first
        sb.append(" (");
        for (CFGNode tmpAlt = tmpSeq.alt; tmpAlt != null; tmpAlt = tmpAlt.alt) {
          toStringAsSequenceRec(sb, tmpAlt, slotDenotation, targetNode);
          if (tmpAlt.alt != null) sb.append(" |"); // Closing parethesis supplied by next level up
        }
      }
      if (tmpSeq.elm.kind == CFGKind.END) return;
      sb.append(" " + tmpSeq);
    }
  }

  public String toStringAsRHS(String slotDenotation) {
    StringBuilder sb = new StringBuilder();
    toStringAsSequenceRec(sb, this, slotDenotation, this);
    return sb.toString();
  }
}
