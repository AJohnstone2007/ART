package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class CFGNode {
  public static String caseSensitiveTerminalStrop = "'";
  public static String caseInsensitiveTerminalStrop = "\"";
  public static String characterTerminalStrop = "`";
  public static String builtinTerminalStrop = "&";

  public final CFGElement cfgElement; // Grammar element
  public int num;
  public CFGNode seq; // sequence link
  public CFGNode alt; // alternate link
  public final GIFTKind giftKind;
  public boolean delayed;
  public int actionAsTerm; // Holds the slot term as parent to slot decorations
  public int instanceNumber = -1;

  private static int nextUniqueNumericLabel = 1;

  public CFGNode(CFGRules grammar, CFGKind kind, String str, int actionAsTerm, GIFTKind giftKind, CFGNode previous, CFGNode parent) {
    super();
    if (str == null) str = "" + nextUniqueNumericLabel++; // EBNF and ALT nodes have null string - uniquify
    this.cfgElement = grammar.findElement(kind, str);
    this.actionAsTerm = actionAsTerm;
    this.giftKind = giftKind;
    if (previous != null) previous.seq = this;
    if (parent != null) parent.alt = this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + num;
    result = prime * result + ((cfgElement == null) ? 0 : cfgElement.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CFGNode other = (CFGNode) obj;
    if (num != other.num) return false;
    if (cfgElement == null) {
      if (other.cfgElement != null) return false;
    } else if (!cfgElement.equals(other.cfgElement)) return false;
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
    switch (cfgElement.cfgKind) {
    case EOS, ALT, PAR, KLN, OPT, POS:
      ret += cfgElement.cfgKind;
      break;
    case TRM_CS, TRM_CH, TRM_BI, NONTERMINAL, EPSILON:
      ret += cfgElement.cfgKind + "\n" + cfgElement.str + giftToString();
      break;
    case END:
      ret += "END " + "\n(" + seq.num + "," + alt.num + ")";
      break;
    default:
      ret = "???";
      break;
    }
    if (actionAsTerm != 0) ret += Util.escapeString(ScriptTermInterpreter.iTerms.toString(actionAsTerm), false);
    return ret;
  }

  @Override
  public String toString() {
    return /* num + " " + */ cfgElement + giftToString() + delayedToString();
  }

  public String toStringAsProduction() { // convenience method for common use case
    return toStringAsProduction(" ::=", " .");
  }

  public String toStringAsProduction(String rewritesDenotation, String slotDenotation) { // Print a node in the context of its production
    // Util.info("toStringAsProduction called on " + num + ": " + this);
    StringBuilder sb = new StringBuilder();

    if (CFGRules.isLHS(this) || (seq == null && alt == null))
      sb.append(cfgElement);
    else if (seq.cfgElement.cfgKind == CFGKind.EOS)
      sb.append(seq);
    else {
      CFGNode tmp;
      for (tmp = this; !(tmp.cfgElement.cfgKind == CFGKind.END && CFGRules.isLHS(tmp.seq)); tmp = tmp.seq) {// Locate the end of this production
        // Util.info("toStringAsProduction at " + tmp + " with next-in-sequence element " + tmp.seq.elm);
      }
      sb.append(tmp.seq.cfgElement + rewritesDenotation); // Render LHS

      toStringAsSequenceRec(sb, tmp.alt, slotDenotation, this); // Render RHS
    }
    return sb.toString();
  }

  private void toStringAsSequenceRec(StringBuilder sb, CFGNode alt, String slotDenotation, CFGNode targetNode) {
    // Util.info("toStringAsSequenceRec called on " + this.instanceNumber + ":" + this);
    if (alt.cfgElement.cfgKind != CFGKind.ALT) Util.fatal("toStringAsSequenceRec() called on node " + alt.num + " which is not not an ALT node");
    for (CFGNode tmpSeq = alt.seq;; tmpSeq = tmpSeq.seq) { // run down this sequence
      if (tmpSeq == targetNode) sb.append(slotDenotation);
      if (tmpSeq.cfgElement.cfgKind != CFGKind.END && tmpSeq.alt != null) { // If this element has an alt, then recursively process it first
        sb.append(" (");
        for (CFGNode tmpAlt = tmpSeq.alt; tmpAlt != null; tmpAlt = tmpAlt.alt) {
          toStringAsSequenceRec(sb, tmpAlt, slotDenotation, targetNode);
          if (tmpAlt.alt != null) sb.append(" |"); // Closing parethesis supplied by next level up
        }
      }
      if (tmpSeq.cfgElement.cfgKind == CFGKind.END) return;
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
    toStringActionsRec(sb, actionAsTerm);
    return sb.toString();
  }

  private void toStringActionsRec(StringBuilder sb, int actionAsTerm) {
    if (actionAsTerm == 0) return;
    if (ScriptTermInterpreter.iTerms.hasSymbol(actionAsTerm, "cfgNative")) {
      sb.append(ScriptTermInterpreter.iTerms.toString(ScriptTermInterpreter.iTerms.subterm(actionAsTerm, 0)));
      return;
    }
    for (int i = 0; i < ScriptTermInterpreter.iTerms.termArity(actionAsTerm); i++)
      toStringActionsRec(sb, ScriptTermInterpreter.iTerms.termChildren(actionAsTerm)[i]);
  }

}
