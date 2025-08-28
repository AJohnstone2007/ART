package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

/* This extension to TermTraverser adds text specific functions that allow a traverser to build a String rendering of a term, taking into account aliases
*/

public class TermTraverserText extends TermTraverser {
  private final Map<Integer, Integer> globalAliases;
  private Map<Integer, Integer> localAliases;
  public final StringBuilder sb;
  private int depthLimit;
  private boolean indent;

  public TermTraverserText(ITerms iTerms, String name) {
    super(name);
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
    addGlobalAlias(ScriptInterpreter.iTerms.findString(key), ScriptInterpreter.iTerms.findString(value));
  }

  public int childSymbolIndex(int root, int childNumber) {
    return ScriptInterpreter.iTerms.termChildren(root)[childNumber];
  }

  public String childSymbolString(int root, int childNumber) {
    return ScriptInterpreter.iTerms.termSymbolString(childSymbolIndex(root, childNumber));
  }

  // public String childStrippedSymbolString(int root, int childNumber) {
  // String str = childSymbolString(root, childNumber);
  // return str.substring(1, str.length() - 1);
  // }

  public void appendAlias(int stringIndex) {
    appendAlias("", stringIndex, "");
  }

  public void appendAlias(String prefix, int stringIndex, String postfix) {
    sb.append(prefix + Util.escapeString(ScriptInterpreter.iTerms.getString(aliasLookup(stringIndex)), false) + postfix);
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
    // Util.info("Text traverser at term " + termIndex + " labeled " + ScriptTermInterpreter.iTerms.termSymbolStringIndex(termIndex) + ":" +
    // ScriptTermInterpreter.iTerms.termSymbolString(termIndex) + "$"
    // + " and string " + sb);
    if (indent) {
      sb.append("\n");
      for (int i = 0; i < depth; i++)
        sb.append(" ");
    }
    perform(opsPreorder, termIndex);
    int[] children = ScriptInterpreter.iTerms.termChildren(termIndex);
    int length = children.length;
    if (!breakSet.contains(ScriptInterpreter.iTerms.termSymbolStringIndex(termIndex))) {
      for (int childNumber = 0; childNumber < length; childNumber++) {

        if (depthLimit >= 0 && depth >= depthLimit)
          sb.append("..");
        else
          traverse(children[childNumber], depth + 1);
        if (length > 1 && childNumber < length - 1) perform(opsInorder, termIndex);
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
