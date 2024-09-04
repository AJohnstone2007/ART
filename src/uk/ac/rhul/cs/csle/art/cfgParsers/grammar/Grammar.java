
package uk.ac.rhul.cs.csle.art.cfgParsers.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.ART;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Relation;
import uk.ac.rhul.cs.csle.art.util.Util;

public class Grammar {
  public final ITerms iTerms;

  // Constants
  private final Set<GrammarKind> lexKinds = Set.of(GrammarKind.B, GrammarKind.C, GrammarKind.T, GrammarKind.TI);
  private final Set<GrammarKind> selfFirst = Set.of(GrammarKind.B, GrammarKind.C, GrammarKind.EOS, GrammarKind.T, GrammarKind.TI, GrammarKind.EPS);
  private int nextFreeEnumerationElement;
  public final GrammarElement epsilonElement;
  public final GrammarElement endOfStringElement;
  public final GrammarNode endOfStringNode;

  public String name = "";
  public GrammarElement startNonterminal;

  public final Map<GrammarElement, GrammarElement> elements = new TreeMap<>();
  public final Map<Integer, GrammarElement> elementsByNumber = new TreeMap<>();
  public final Map<Integer, GrammarNode> nodesByNumber = new TreeMap<>();
  public final Map<GrammarElement, GrammarNode> rules = new TreeMap<>(); // Map from nonterminals to list of productions represented by their LHS node

  public int lexSize;
  public final Set<LKind> whitespaces = new HashSet<>(); // This should be a set of elements, with the builtins added to elements
  public LKind[] lexicalKindsArray;
  public String[] lexicalStringsArray;
  public LKind[] whitespacesArray;

  // Grammar analysis data
  public final Relation<GrammarElement, GrammarElement> reach1 = new Relation<>(); // { (X,Y) | X ::= \alpha Y \beta }
  public final Relation<GrammarElement, GrammarElement> singleton1 = new Relation<>(); // { (X,Y) | X ::= \alpha Y \beta, \alpha, \beta \derives \epsilon }

  public final Relation<GrammarElement, GrammarElement> reach = new Relation<>(); // reach1*
  public final Relation<GrammarElement, GrammarElement> first = new Relation<>(); // first1*
  public final Relation<GrammarElement, GrammarElement> follow = new Relation<>(); // definition?
  public final Relation<GrammarElement, GrammarElement> last = new Relation<>(); // last1*
  public final Relation<GrammarElement, GrammarElement> singleton = new Relation<>(); // singleton1*

  public final Relation<GrammarNode, GrammarElement> instanceFirst = new Relation<>(); // definition?
  public final Relation<GrammarNode, GrammarElement> instanceFollow = new Relation<>(); // definition?

  public final Set<GrammarNode> initialSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \alpha = \epsilon }
  public final Set<GrammarNode> penultimateSlots = new HashSet<>(); // { X ::= \alpha . Y \beta} | \beta = \epsilon } (note: Y is not epsilon)
  public final Set<GrammarNode> finalSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \beta = \epsilon }

  public final Set<GrammarNode> nullablePrefixSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \alpha =>* \epsilon }
  public final Set<GrammarNode> nullableSuffixSlots = new HashSet<>(); // { X ::= \alpha . \beta} | \beta =>* \epsilon }
  public final Set<GrammarElement> cyclicNonterminals = new HashSet<>(); // { X ::= \alpha X \beta} | \alpha,\beta =>* \epsilon }

  public final Set<GrammarNode> acceptingSlots = new HashSet<>(); // Set of slots which are END nodes of the start production
  public final Set<Integer> acceptingNodeNumbers = new TreeSet<>(); // Set of node number for the slots on accepting slots

  public Grammar(String name, ITerms iTerms) {
    this.name = name;
    this.iTerms = iTerms;
    epsilonElement = findElement(GrammarKind.EPS, "#");
    endOfStringElement = findElement(GrammarKind.EOS, "$");

    endOfStringNode = new GrammarNode(this, GrammarKind.EOS, "$", 0, GIFTKind.NONE, null, null);
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode

    whitespaces.add(LKind.SIMPLE_WHITESPACE); // default whitespace if non declared
  }

  private boolean changed;

  public void normalise() {
    // Element and node numbering
    nextFreeEnumerationElement = 0;
    numberElementsAndNodes();
    setEndNodeLinks();

    // Report nonterminals with no rules
    Set<GrammarElement> tmp = new HashSet<>();
    for (GrammarElement e : elements.keySet())
      if (e.kind == GrammarKind.N) {
        if (rules.get(e) == null) tmp.add(e);
      }

    if (tmp.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("Nonterminal" + (tmp.size() == 1 ? " " : "s ") + "used but not defined: ");
      for (GrammarElement n : tmp)
        sb.append(n.str + " ");
      Util.fatal(sb.toString());
    }

    // Compute lexical data
    lexicalKindsArray = new LKind[lexSize];
    lexicalStringsArray = new String[lexSize];

    int token = 0;
    lexicalStringsArray[0] = "EOS";
    for (GrammarElement e1 : elements.keySet()) {
      // System.out.println("Computing lexical arrays for " + e);
      switch (e1.kind) {
      case B:
        try {
          lexicalKindsArray[token] = LKind.valueOf(e1.str);
        } catch (IllegalArgumentException ex) {
          Util.fatal("Unknown builtin &" + e1.str);
        }
        lexicalStringsArray[token] = e1.str;
        break;
      case C:
        lexicalKindsArray[token] = LKind.CHARACTER;
        lexicalStringsArray[token] = e1.str;
        break;
      case T:
        lexicalKindsArray[token] = LKind.SINGLETON_CASE_SENSITIVE;
        lexicalStringsArray[token] = e1.str;
        break;
      case TI:
        lexicalKindsArray[token] = LKind.SINGLETON_CASE_INSENSITIVE;
        lexicalStringsArray[token] = e1.str;
        break;
      default:
        break;
      }
      token++;
    }

    whitespacesArray = whitespaces.toArray(new LKind[0]);

    // Set positional attributes and accepting slots, and seed nullablePrefixSlots and nullableSuffixSlots
    for (GrammarElement ge : elements.keySet())
      if (ge.kind == GrammarKind.N) for (GrammarNode gn = rules.get(ge).alt; gn != null; gn = gn.alt) {
        GrammarNode gs = gn.seq;
        initialSlots.add(gs);
        nullablePrefixSlots.add(gs);
        while (gs.seq.elm.kind != GrammarKind.END)
          gs = gs.seq;
        penultimateSlots.add(gs);
        finalSlots.add(gs.seq);
        nullableSuffixSlots.add(gs.seq);
        if (ge == startNonterminal) {
          acceptingSlots.add(gs.seq);
          acceptingNodeNumbers.add(gs.seq.num);
        }
      }

    // Seed first sets
    for (GrammarElement ge : elements.keySet())
      if (selfFirst.contains(ge.kind)) first.add(ge, ge);

    // Seed follow sets
    if (startNonterminal != null) follow.get(startNonterminal).add(endOfStringElement);

    // Closure loop over first and follow set computation
    changed = true;
    while (changed) {
      changed = false;
      for (GrammarElement ge : elements.keySet())
        if (ge.kind == GrammarKind.N) firstAndFollowSetsAlt(rules.get(ge), rules.get(ge).alt);
    }
  }

  private void numberElementsAndNodes() {
    for (GrammarElement s : elements.keySet()) {
      s.ei = nextFreeEnumerationElement++;
      if (lexKinds.contains(s.kind)) lexSize = s.ei;
      // System.out.println("Enumerating grammar element " + s.ei + ": " + s.str);
    }
    lexSize++;

    nodesByNumber.put(endOfStringNode.num = nextFreeEnumerationElement++, endOfStringNode);
    for (GrammarElement n : rules.keySet())
      numberElementsAndNodesRec(rules.get(n));
  }

  private void numberElementsAndNodesRec(GrammarNode node) {
    if (node != null) {
      nodesByNumber.put(node.num = nextFreeEnumerationElement++, node);
      if (node.elm.kind != GrammarKind.END) {
        numberElementsAndNodesRec(node.seq);
        numberElementsAndNodesRec(node.alt);
      }
    }
  }

  private void setEndNodeLinks() {
    for (GrammarElement n : rules.keySet()) {
      GrammarNode lhs = rules.get(n);
      for (GrammarNode production = lhs.alt; production != null; production = production.alt)
        setEndNodeLinksRec(lhs, production);
    }
  }

  private void setEndNodeLinksRec(GrammarNode parentNode, GrammarNode altNode) {
    GrammarNode gn;
    for (gn = altNode.seq; gn.elm.kind != GrammarKind.END; gn = gn.seq) {
      // System.out.println("processEndNodes at " + gn.ni + " " + gn);
      if (gn.alt != null) for (GrammarNode alternate = gn.alt; alternate != null; alternate = alternate.alt)
        setEndNodeLinksRec(gn, alternate); // Recurse into brackets
    }
    // System.out.println("processEndNodes processing " + gn.ni + " " + gn);
    gn.alt = altNode; // We are now at the end of a production or of a bracketed alternate
    gn.seq = parentNode;
    if (parentNode == rules.get(startNonterminal)) acceptingNodeNumbers.add(gn.num);
    // System.out.println("processEndNodes updated alt and seq to " + gn.alt.ni + " " + gn.seq.ni);
  }

  Set<GrammarElement> removeEpsilon(Set<GrammarElement> set) {
    Set<GrammarElement> tmp = new HashSet<>(set);
    tmp.remove(epsilonElement);
    return tmp;
  }

  void firstAndFollowSetsAlt(GrammarNode bracketNode, GrammarNode gn) {
    if (ART.tracing) System.out.println("firstAndFollowSetsAlt at " + gn.num);
    if (gn.alt != null) firstAndFollowSetsAlt(bracketNode, gn.alt); // recurse over ALT nodes
    firstAndFollowSetsRec(gn, gn.seq); // recurse over sequence

    // changed |= instanceFirst.addAll(gn, removeEpsilon(instanceFirst.get(gn.seq)));
    // changed |= instanceFirst.addAll(bracketNode, instanceFirst.get(gn));
    // changed |= first.addAll(bracketNode.elm, instanceFirst.get(bracketNode));
    //
    // // For closure nodes, fold first into follow
    // if (bracketNode.elm.kind == GrammarKind.POS || bracketNode.elm.kind == GrammarKind.KLN)
    // changed |= instanceFollow.addAll(bracketNode, removeEpsilon(instanceFirst.get(bracketNode)));
    //
    // if (bracketNode.elm.kind == GrammarKind.OPT || bracketNode.elm.kind == GrammarKind.KLN) changed |= instanceFirst.add(bracketNode, epsilonElement);
  }

  // Note: initial and final already computed; nullablePrefix and nullableSuffix also updated accordingly
  private void firstAndFollowSetsRec(GrammarNode bracketNode, GrammarNode gn) { // Returns seen only nullable - could be replaced by
    if (ART.tracing) System.out.println("firstAndFollowSetsRec at " + gn.num);

    // if (leftNullable) nullablePrefixSlots.add(gn);

    if (gn.elm.kind == GrammarKind.END) return;
    firstAndFollowSetsRec(bracketNode, gn.seq);

    // 5. Fold into instanceFirst our element's FIRST
    // if (gn.elm.kind == GrammarKind.EPS)
    // changed |= instanceFirst.addAll(gn, first.get(gn.elm));
    // else
    // changed |= instanceFirst.addAll(gn, removeEpsilon(first.get(gn.elm)));

    // 6. If our element first contains epsilon, fold in our successor's first as well
    // if (first.get(gn.elm).contains(epsilonElement)) {
    // changed |= instanceFirst.get(gn).addAll(instanceFirst.get(gn.seq));
    // if (instanceFirst.get(gn.seq).contains(epsilonElement)) changed |= nullableSuffixSlots.add(gn);
    // }

    // 7. Now update nonterminal elements first set with this instanceFirst
    // if (gn.elm.kind == GrammarKind.N) changed |= first.addAll(gn.elm, instanceFirst.get(gn));

    // 8. If we are an initial slot, update our root instanceFirst set with our instanceFirstSet
    // if (initialSlots.contains(gn)) changed |= instanceFirst.get(bracketNode).addAll(instanceFirst.get(gn));

    // 9. If we have both a nullable suffix and a nullable prefix, and this element is the bracketNode's element (LHS) then we are cyclic
    // if (nullablePrefixSlots.contains(gn) && nullableSuffixSlots.contains(gn) && gn.elm.equals(bracketNode.elm)) cyclicNonterminals.add(gn.elm);

    // 8. Update follow sets with first set of successor, and update instance element follow set
    // changed |= instanceFollow.get(gn).addAll(removeEpsilon(instanceFirst.get(gn.seq)));
    // if (nullableSuffixSlots.contains(gn.seq)) changed |= instanceFollow.get(gn).addAll(instanceFollow.get(rootNode));
    // changed |= follow.get(gn.elm).addAll(instanceFollow.get(gn));
  }

  // Data access for lexers
  public LKind[] lexicalKindsArray() {
    return lexicalKindsArray;
  }

  public String[] lexicalStringsArray() {
    return lexicalStringsArray;
  }

  public LKind[] whitespacesArray() {
    return whitespacesArray;
  }

  // Atttribute-action functions from ReferenceGrammarParser.art below this line
  public GrammarNode workingNode;
  public GIFTKind workingFold = GIFTKind.NONE;
  public int workingAction = 0;

  GrammarNode mostRecentLHS;

  /* Action routines called from the script term traverser */
  public GrammarElement findElement(GrammarKind kind, String s) {
    GrammarElement candidate = new GrammarElement(kind, s);
    if (elements.get(candidate) == null) elements.put(candidate, candidate);
    return elements.get(candidate);
  }

  LinkedList<GrammarNode> stack = new LinkedList<>();

  public void lhsAction(String id) {
    GrammarElement element = findElement(GrammarKind.N, id);
    if (startNonterminal == null) startNonterminal = element;
    workingNode = rules.get(element);
    if (workingNode == null) rules.put(element, updateWorkingNode(GrammarKind.N, id));
    mostRecentLHS = rules.get(element);
  }

  public void altAction() {
    stack.push(workingNode);
    while (workingNode.alt != null) // Maintain specification order by adding new alternate at the end
      workingNode = workingNode.alt;
    workingNode = new GrammarNode(this, GrammarKind.ALT, null, workingAction, workingFold, null, workingNode);
    workingAction = 0;
    workingFold = GIFTKind.NONE;
  }

  public void endAction(String actions) {
    updateWorkingNode(GrammarKind.END, "");
    workingNode = stack.pop();
  }

  public GrammarNode updateWorkingNode(GrammarKind kind, String str) {
    workingNode = new GrammarNode(this, kind, str, workingAction, workingFold, workingNode, null);
    workingAction = 0;
    workingFold = GIFTKind.NONE;
    return workingNode;
  }

  /* Visualisation rendering */
  public String toStringDot() {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph \"Reference grammar\"\n" + "{\n" + "graph[ordering=out ranksep=0.1]\n"
        + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04 color=gray]\n"
        + "edge[fontname=Helvetica fontsize=9 arrowsize = 0.3 color=gray]\n\n");
    for (GrammarElement n : rules.keySet())
      toStringDotRec(sb, rules.get(n));
    sb.append("}\n");
    return sb.toString();
  }

  private void toStringDotRec(StringBuilder sb, GrammarNode cs) {
    sb.append("\"" + cs.num + "\"[label=\"" + cs.toStringDot() + "\"]\n");
    if (cs.elm.kind == GrammarKind.ALT) {
      seqArrow(sb, cs);
      altArrow(sb, cs);
    } else if (cs.elm.kind != GrammarKind.END) {
      altArrow(sb, cs);
      seqArrow(sb, cs);
    }
  }

  private void altArrow(StringBuilder sb, GrammarNode cs) {
    if (cs.alt == null) return;
    sb.append(
        "\"" + cs.num + "\"->\"" + cs.alt.num + "\"" + "{rank = same; \"" + cs.num + "\"" + ";\"" + cs.alt.num + "\"" + ";" + "}" + "[label=\" a\"" + "]\n");
    if (!isLHS(cs.alt)) toStringDotRec(sb, cs.alt);
  }

  private void seqArrow(StringBuilder sb, GrammarNode cs) {
    if (cs.seq == null) return;
    sb.append("\"" + cs.num + "\"->\"" + cs.seq.num + "\"\n");
    toStringDotRec(sb, cs.seq);
  }

  /* Text rendering */

  public String toStringProperties() {
    return toStringBody(true);
  }

  @Override
  public String toString() {
    return toStringBody(false);
  }

  public String toStringBody(boolean showProperties) {
    StringBuilder sb = new StringBuilder();
    if (startNonterminal != null)
      sb.append("Grammar " + name + " with start nonterminal " + startNonterminal.str + "\n");
    else
      sb.append("Grammar " + name + " with empty start nonterminal\n");

    if (isEmpty()) return "Grammar has no rules";

    sb.append("Grammar rules:\n");
    for (GrammarElement n : rules.keySet()) {
      boolean first = true;
      for (GrammarNode production = rules.get(n).alt; production != null; production = production.alt) {
        if (first) {
          sb.append(" " + production.toStringAsProduction(" ::=\n ", null) + "\n");
          first = false;
        } else {
          sb.append(" | " + production.toStringAsRHS(null) + "\n");
        }
      }
    }

    sb.append("Grammar elements:\n");
    for (GrammarElement s : elements.keySet()) {
      sb.append(" (" + s.toStringDetailed() + ") " + s);
      if (showProperties && first.get(s) != null) {
        sb.append(" first = {");
        appendElements(sb, first.get(s));
        sb.append("} follow = {");
        appendElements(sb, follow.get(s));
        sb.append("}");
      }
      sb.append("\n");
    }
    sb.append("Grammar nodes:\n");
    for (int i : nodesByNumber.keySet()) {
      GrammarNode gn = nodesByNumber.get(i);
      sb.append(" " + i + ": " + gn.toStringAsProduction());
      if (showProperties) {
        if (initialSlots.contains(gn)) sb.append(" initial");
        if (penultimateSlots.contains(gn)) sb.append(" penultimate");
        if (finalSlots.contains(gn)) sb.append(" final");
        if (nullableSuffixSlots.contains(gn)) sb.append(" nullableSuffix");
        if (nullablePrefixSlots.contains(gn)) sb.append(" nullablePrefix");

        sb.append(" instfirst = {");
        appendElements(sb, instanceFirst.get(gn));
        sb.append("} instfollow = {");
        appendElements(sb, instanceFollow.get(gn));
        sb.append("}");
      }
      sb.append("\n");
    }

    sb.append("Accepting slot" + (acceptingSlots.size() == 1 ? "" : "s") + ":\n");
    for (var gn : acceptingSlots)
      sb.append(" " + gn.toStringAsProduction() + "\n");

    sb.append("Accepting node number" + (acceptingSlots.size() == 1 ? "" : "s") + ":");
    for (var gn : acceptingNodeNumbers)
      sb.append(" " + gn);

    sb.append("\nWhitespaces: " + whitespaces);

    return sb.toString();
  }

  public boolean isEmpty() {
    return rules.keySet().isEmpty();
  }

  private void appendElements(StringBuilder sb, Set<GrammarElement> elements) {
    if (elements == null) return;
    boolean first = true;
    for (GrammarElement e : elements) {
      if (first)
        first = false;
      else
        sb.append(",");
      sb.append(e.toString());
    }
  }

  public void show(String filename) {
    PrintStream ps;
    try {
      ps = new PrintStream(new File(filename));
      ps.println(toStringDot());
      ps.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to open visualisation file " + filename);
    }
  }

  /** Support for table driven parsers ***************************************/

  public int[] makeKindsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (GrammarElement gs : elements.keySet())
      ret[gs.ei] = gs.kind.ordinal();
    for (int ni : nodesByNumber.keySet())
      ret[ni] = nodesByNumber.get(ni).elm.kind.ordinal();
    return ret;
  }

  public int[][] makeAltsArray() {
    int ret[][] = new int[nextFreeEnumerationElement][];
    for (int ni : nodesByNumber.keySet()) {
      GrammarNode gn = nodesByNumber.get(ni);
      int altCount = 0;
      for (GrammarNode alt = gn.alt; alt != null; alt = alt.alt)
        altCount++;
      ret[ni] = new int[altCount + 1];
      altCount = 0;
      for (GrammarNode alt = gn.alt; alt != null; alt = alt.alt)
        ret[ni][altCount++] = alt.num;
      ret[ni][altCount] = 0;
    }
    return ret;
  }

  public int[] makeSeqsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GrammarNode sn = nodesByNumber.get(ni).seq;
      ret[ni] = sn == null ? 0 : sn.num;
    }
    return ret;
  }

  public int[] makeCallTargetsArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GrammarNode lhs = rules.get(nodesByNumber.get(ni).elm);
      ret[ni] = (lhs == null ? 0 : lhs.num);
    }
    return ret;
  }

  public int[] makeElementOfArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GrammarElement el = nodesByNumber.get(ni).elm;
      ret[ni] = (el == null ? 0 : el.ei);
    }
    return ret;
  }

  /** Static methods *********************************************************/

  public static boolean isLHS(GrammarNode gn) {
    return gn.elm != null && gn.elm.kind == GrammarKind.N && gn.seq == null;
  }
}
