package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class CFGNode {
  public static String caseSensitiveTerminalStrop = "'";
  public static String caseInsensitiveTerminalStrop = "\"";
  public static String characterTerminalStrop = "`";
  public static String builtinTerminalStrop = "&";

  public int num;
  public final CFGElement elm; // Grammar element
  public CFGNode seq; // sequence link
  public CFGNode alt; // alternate link
  public final GIFTKind giftKind;
  public boolean delayed;
  public int slotTerm; // Holds the slot term as parent to slot decorations
  public int instanceNumber = -1;

  private static int nextUniqueNumericLabel = 1;

  public CFGNode(CFGRules grammar, CFGKind kind, String str, int slotTerm, GIFTKind giftKind, CFGNode previous, CFGNode parent) {
    super();
    if (str == null) str = "" + nextUniqueNumericLabel++; // EBNF and ALT nodes have null string - uniquify
    this.elm = grammar.findElement(kind, str);
    this.slotTerm = slotTerm;
    this.giftKind = giftKind;
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

  private String giftToString() {
    switch (giftKind) {
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

  private String delayedToString() {
    return delayed ? "!<" : "";
  }

  public String toStringDot() {
    String ret = num + " ";
    switch (elm.kind) {
    case EOS, ALT, DO, KLN, OPT, POS:
      ret += elm.kind;
      break;
    case T, C, B, N, EPS:
      ret += elm.kind + "\n" + elm.str + giftToString();
      break;
    case END:
      ret += "END " + "\n(" + seq.num + "," + alt.num + ")";
      break;
    default:
      ret = "???";
      break;
    }
    if (slotTerm != 0) ret += Util.escapeString(ScriptTermInterpreter.iTerms.toString(slotTerm), false);
    return ret;
  }

  @Override
  public String toString() {
    return /* num + " " + */toString1();
  }

  public String toString1() {
    switch (elm.kind) {
    case EOS:
      return "EOS node";
    case T:
      return caseSensitiveTerminalStrop + elm.str + caseSensitiveTerminalStrop + giftToString();
    case C:
      return characterTerminalStrop + elm.str + giftToString();
    case B:
      return builtinTerminalStrop + elm.str + giftToString();
    case EPS:
      return "#" + giftToString();
    case N:
      return elm.str + giftToString() + delayedToString();
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

  public String toStringActions() {
    StringBuilder sb = new StringBuilder();
    toStringActionsRec(sb, slotTerm);
    return sb.toString();
  }

  private void toStringActionsRec(StringBuilder sb, int slotTerm) {
    if (slotTerm == 0) return;
    if (ScriptTermInterpreter.iTerms.hasSymbol(slotTerm, "cfgNative")) {
      sb.append(ScriptTermInterpreter.iTerms.toString(ScriptTermInterpreter.iTerms.subterm(slotTerm, 0)));
      return;
    }
    for (int i = 0; i < ScriptTermInterpreter.iTerms.termArity(slotTerm); i++)
      toStringActionsRec(sb, ScriptTermInterpreter.iTerms.termChildren(slotTerm)[i]);
  }

}
