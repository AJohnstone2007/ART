package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.Map;

/* This extension to TermTraverser adds text specific functions that allow a traverser to build a String rendering of a term, taking into account aliases
*/

public class TermTraverserText extends TermTraverser {
  private final Map<Integer, Integer> globalAliases;
  private Map<Integer, Integer> localAliases;
  public final StringBuilder sb;
  private int depthLimit;
  private boolean indent;

  public TermTraverserText(ITerms iTerms) {
    super(iTerms);
    globalAliases = new HashMap<>();
    sb = new StringBuilder();
    depthLimit = -1;
    indent = false;
  }

  //@formatter:off
  public void addAction(String symbol, String preorder, String inorder, String postorder) {
    addAction(symbol,
        (preorder == null ? null : (Integer t) -> sb.append(preorder)),
        (inorder == null ? null : (Integer t) -> sb.append(inorder)),
        (postorder == null ? null : (Integer t) -> sb.append(postorder)));
  }
  //@formatter:on

  public void addActionBreak(String symbol, String preorder, String inorder, String postorder) {
    addBreak(symbol);
    addAction(symbol, preorder, inorder, postorder);
  }

  public void addGlobalAlias(Integer key, Integer value) {
    globalAliases.put(key, value);
  }

  public void addGlobalAlias(String key, String value) {
    addGlobalAlias(iTerms.findString(key), iTerms.findString(value));
  }

  public int childSymbolIndex(int root, int childNumber) {
    return iTerms.termChildren(root)[childNumber];
  }

  public String childSymbolString(int root, int childNumber) {
    return iTerms.termSymbolString(childSymbolIndex(root, childNumber));
  }

  // public String childStrippedSymbolString(int root, int childNumber) {
  // String str = childSymbolString(root, childNumber);
  // return str.substring(1, str.length() - 1);
  // }

  public void appendAlias(int stringIndex) {
    appendAlias("", stringIndex, "");
  }

  public void appendAlias(String prefix, int stringIndex, String postfix) {
    Integer candidate = aliasLookup(stringIndex);

    // If we haven't found an alias yet, then just use original stringIndex; append corresponding string
    sb.append(prefix + iTerms.getString(aliasLookup(stringIndex)) + postfix);
  }

  public Integer aliasLookup(int stringIndex) {
    Integer candidate = null;

    // First try local aliases
    if (localAliases != null) candidate = localAliases.get(stringIndex);

    // If we haven't found a string yet, try the global aliases
    if (candidate == null && globalAliases != null) candidate = globalAliases.get(stringIndex);

    // If still not found, return original
    return candidate == null ? stringIndex : candidate;
  }

  public void append(String string) {
    sb.append(string);
  }

  public String getString() {
    return sb.toString();
  }

  public void traverse(int termIndex, int depth) {
    // System.out.println("Traverser at " + termIndex + " " + iTerms.getTermSymbolString(termIndex) + " with string index " +
    // iTerms.getTermSymbolIndex(termIndex)
    // + " and string " + sb);
    if (indent) {
      sb.append("\n");
      for (int i = 0; i < depth; i++)
        sb.append("   ");
    }
    perform(opsPreorder, termIndex);
    if (depthLimit >= 0 && depth >= depthLimit)
      sb.append("..");
    else {
      int[] children = iTerms.termChildren(termIndex);
      int length = children.length;
      int lengthLessOne = length - 1;
      if (!breakSet.contains(iTerms.termSymbolStringIndex(termIndex))) for (int i = 0; i < length; i++) {
        traverse(children[i], depth + 1);
        if (i < lengthLessOne) perform(opsInorder, termIndex);
      }
    }
    perform(opsPostorder, termIndex);
  }

  public String toString(Integer term) {
    return toString(term, false, -1, null);
  }

  public String toString(Integer term, Map<Integer, Integer> localAliases) {
    return toString(term, false, -1, localAliases);
  }

  public String toString(Integer term, Boolean indent, Integer depthLimit, Map<Integer, Integer> localAliases) {
    this.localAliases = localAliases;
    this.indent = indent;
    this.depthLimit = depthLimit;
    sb.setLength(0); // clear string builder
    traverse(term, 0);
    this.indent = false;
    this.depthLimit = -1;
    this.localAliases = null;
    return sb.toString();
  }

}
