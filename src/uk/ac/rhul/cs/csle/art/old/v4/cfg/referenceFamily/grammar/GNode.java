package uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.grammar;

import uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.Reference;

public class GNode {
  public static String caseSensitiveTerminalStrop = "'";
  public static String caseInsensitiveTerminalStrop = "\"";
  public static String characterTerminalStrop = "`";
  public static String builtinTerminalStrop = "&";

  static Grammar grammar;
  public int num;
  public final GElement elm; // Grammar element
  public GNode seq; // sequence link
  public GNode alt; // alternate link
  public GIFTKind giftKind = GIFTKind.NONE;
  public int action; // Holds an action term used by attribute evaluators

  // public final Set<GNode> first = new HashSet<>();
  // public final Set<GNode> follow = new HashSet<>();
  // public final Set<GNode> select = new HashSet<>();
  //
  // public boolean derivationFindTweak;

  /*
   * compute as gn.prev != null && gn.prev.prev == null && gn.seq.kind != gnKind.END && (gn.prev.kind == gn.Kind.TERMINALLC || (gn.prev.kind ==
   * gn.Kind.NONTERMINAL && gn.prev.isNullable))
   */
  public GNode(GKind kind, String str, Grammar grammar) {
    GNode.grammar = grammar;
    elm = GNode.grammar.findElement(kind, "");
  }

  public GNode(GKind kind, String str, int action, GIFTKind fold, GNode previous, GNode parent) {
    super();
    this.elm = GNode.grammar.findElement(kind, str);
    this.action = action;
    this.giftKind = fold;
    if (previous != null) previous.seq = this;
    if (parent != null) parent.alt = this;

    // if (kind == Kind.EPS || kind == Kind.EOS || kind == Kind.END || kind == Kind.T || kind == Kind.B) first.add(this);
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
    GNode other = (GNode) obj;
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
      return "^^-";
    case UNDER:
      return "^";
    default:
      return "^???";
    }
  }

  public String toStringDot() {
    String ret = num + " ";
    if (action != 0) ret += action + " ";
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
    // System.out.println("toStringAsProduction called on " + ni + ":" + this);
    StringBuilder sb = new StringBuilder();

    if (Grammar.isLHS(this))
      sb.append(elm.str);
    else if (seq.elm.kind == GKind.EOS)
      sb.append(seq);
    else {
      GNode tmp;
      for (tmp = this; !(tmp.elm.kind == GKind.END && Grammar.isLHS(tmp.seq)); tmp = tmp.seq) // Locate the end of this production
        // System.out.println("toStringAsProduction at " + tmp + " with next-in-sequence element " + tmp.seq.el)
        ;

      // System.out.println("toStringAsProduction rendering LHS");
      sb.append(tmp.seq.elm.str + rewritesDenotation); // Render LHS
      toStringAsSequenceRec(sb, tmp.alt, slotDenotation, this); // Render RHS
    }
    return sb.toString();
  }

  private void toStringAsSequenceRec(StringBuilder sb, GNode alt, String slotDenotation, GNode targetNode) {
    // System.out.println("toStringAsSequenceRec called on " + this.instanceNumber + ":" + this);
    if (alt.elm.kind != GKind.ALT) Reference.fatal("toStringAsSequenceRe()c called on node " + alt.num + " which is not not an ALT node");
    for (GNode tmpSeq = alt.seq;; tmpSeq = tmpSeq.seq) { // run down this sequence
      if (tmpSeq == targetNode) sb.append(slotDenotation);
      if (tmpSeq.elm.kind != GKind.END && tmpSeq.alt != null) { // If this element has an alt, then recursively process it first
        sb.append(" (");
        for (GNode tmpAlt = tmpSeq.alt; tmpAlt != null; tmpAlt = tmpAlt.alt) {
          toStringAsSequenceRec(sb, tmpAlt, slotDenotation, targetNode);
          if (tmpAlt.alt != null) sb.append(" |"); // Closing parethesis supplied by next level up
        }
      }
      if (tmpSeq.elm.kind == GKind.END) return;
      sb.append(" " + tmpSeq);
    }
  }

  public String toStringAsRHS(String slotDenotation) {
    StringBuilder sb = new StringBuilder();
    toStringAsSequenceRec(sb, this, slotDenotation, this);
    return sb.toString();
  }
}
