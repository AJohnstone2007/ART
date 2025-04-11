package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class TermTraverser {
  protected final ITerms iTerms;
  protected final Map<Integer, Consumer<Integer>> opsPreorder;
  protected final Map<Integer, Consumer<Integer>> opsInorder;
  protected final Map<Integer, Consumer<Integer>> opsPostorder;
  protected final Set<Integer> breakSet;

  public TermTraverser(ITerms iTerms) {
    this.iTerms = iTerms;
    opsPreorder = new HashMap<>();
    opsInorder = new HashMap<>();
    opsPostorder = new HashMap<>();
    breakSet = new HashSet<>();
  }

  public void addBreak(Integer... term) {
    for (Integer t : term)
      breakSet.add(t);
  }

  public void addBreak(String... termRootSymbol) {
    for (String s : termRootSymbol)
      addBreak(iTerms.findString(s));
  }

  public void addAction(String symbol, Consumer<Integer> preorder, Consumer<Integer> inorder, Consumer<Integer> postorder) {
    addAction(iTerms.findString(symbol), preorder, inorder, postorder);
  }

  public void addActionBreak(String symbol, Consumer<Integer> preorder, Consumer<Integer> inorder, Consumer<Integer> postorder) {
    addAction(iTerms.findString(symbol), preorder, inorder, postorder);
    addBreak(symbol);
  }

  //@formatter:off
  public void addAction(Integer symbolIndex, Consumer<Integer> preorder, Consumer<Integer> inorder, Consumer<Integer> postorder) {
    opsPreorder.put(symbolIndex, preorder == null ? (Integer t) -> {} : preorder);
    opsInorder.put(symbolIndex, inorder == null ? (Integer t) -> {} : inorder);
    opsPostorder.put(symbolIndex, postorder == null ? (Integer t) -> {} : postorder);
  }

  public void addEmptyAction(String... symbolString) { // force no operation for all actions on a particular key
    for (String s : symbolString) {
//      Util.info("Setting empty action for " + s);
      addAction(s,
          (Integer t) -> {},
          (Integer t) -> {},
          (Integer t) -> {});
    }
  }
 //@formatter:on

  public void perform(Map<Integer, Consumer<Integer>> map, int termIndex) { // Perform an action: use default (keyed on -1) if there is no action in the table
    Consumer<Integer> action;
    if (termIndex == 0)
      action = map.get(-1); // if we are passed a null term, then get default action: trick for __m
    else
      action = map.get(iTerms.termSymbolStringIndex(termIndex));
    // if (action == null) {
    // Util.info("no action for " + iTerms.getTermSymbolString(termIndex));
    // } else {
    // Util.info("found action for " + iTerms.getTermSymbolString(termIndex));
    // }
    if (action == null) action = map.get(-1); // get default action
    if (action != null) action.accept(termIndex);
  }

  public void traverse(int termIndex) {
    // Util.info("traverse() at term " + iTerms.toRawString(termIndex));
    perform(opsPreorder, termIndex);
    int[] children = iTerms.termChildren(termIndex);
    int length = children.length;
    int lengthLessOne = length - 1;
    if (!breakSet.contains(iTerms.termSymbolStringIndex(termIndex))) for (int i = 0; i < length; i++) {
      traverse(children[i]);
      if (i < lengthLessOne) perform(opsInorder, termIndex);
    }
    perform(opsPostorder, termIndex);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TermTraverser " + "[opsPreorder=");
    for (int i : opsPreorder.keySet())
      builder.append(iTerms.getString(i) + "\n");
    builder.append("\nopsInorder=");
    builder.append(opsInorder.keySet());
    builder.append("\nopsPostorder=");
    builder.append(opsPostorder.keySet());
    builder.append("\nbreakSet=");
    builder.append(breakSet);
    builder.append("]");
    return builder.toString();
  }

}
