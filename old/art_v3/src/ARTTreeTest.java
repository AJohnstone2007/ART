import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.value.ARTValueTerm;

public class ARTTreeTest {
  static ARTValueTerm root = null;

  public static void main(String[] args) {
    String string = null;
    try {
      string = new Scanner(new File(args[0])).useDelimiter("\\Z").next() + "\0";

      // String string = "A(B,`C,|(X,Y), E(Z))";

      root = ARTValueTerm.makeTerms(string);

      for (ARTValueTerm tmp = root; tmp != null; tmp = tmp.getSibling())
        System.out.println("Constructed term: " + tmp);

      System.out.println("\nPreorder traversal");
      traverseSimplePreorder(root);

      System.out.println("\nPostorder traversal");
      traverseSimplePostorder(root);

      System.out.println("\nInorder traversal");
      traverseSimpleInorder(root);

      String target = "X";
      System.out.println("\nPreorder traversal with search for target " + target);
      traverseSearch(root, "X");

      System.out.println("\nPreorder traversal over every embedded tree");
      traverseAllEmbeddedTrees(root);
    } catch (ARTException e) {
      System.out.println("Syntax error: " + e.getMessage() + "\n" + string);
    } catch (FileNotFoundException e) {
      System.out.println("Fatal file error: " + e.getMessage());
    }
  }

  static void traverseSimplePreorder(ARTValueTerm t) {
    System.out.println("traverseSimplePreorder: " + t.getPayload());
    for (ARTValueTerm c = t.getChild(); c != null; c = c.getSibling())
      traverseSimplePreorder(c);
  }

  static void traverseSimplePostorder(ARTValueTerm t) {
    for (ARTValueTerm c = t.getChild(); c != null; c = c.getSibling())
      traverseSimplePostorder(c);
    System.out.println("traverseSimplePostorder: " + t.getPayload());
  }

  static void traverseSimpleInorder(ARTValueTerm t) {
    if (t.getChild() == null) // We are a leaf so just print our label and return
      System.out.println("traverseSimpleInorder: " + t.getPayload());
    else
      for (ARTValueTerm c = t.getChild(); c != null; c = c.getSibling()) {
        traverseSimpleInorder(c);
        if (c.getSibling() != null) System.out.println("traverseSimpleInorder: " + t.getPayload());
      }
  }

  static boolean traverseSearch(ARTValueTerm t, String target) {
    System.out.println("traverseSearch: " + t.getPayload());
    if (target.equals(t.getPayload().getPayload())) {
      System.out.println("Found");
      return true;
    }

    for (ARTValueTerm c = t.getChild(); c != null; c = c.getSibling())
      if (traverseSearch(c, target)) return true;

    return false;
  }

  static void traveseEmbeddedTreesSetup(Map<ARTValueTerm, ARTValueTerm> choiceMap, ARTValueTerm t) {
    if (t.getPayload().getPayload().equals("|")) choiceMap.put(t, t.getChild());

    for (ARTValueTerm c = t.getChild(); c != null; c = c.getSibling())
      traveseEmbeddedTreesSetup(choiceMap, c);
  }

  private static boolean traveseEmbeddedTrees(Map<ARTValueTerm, ARTValueTerm> choiceMap, ARTValueTerm t, boolean completed, StringWriter sw) {
    ARTValueTerm c;

    if ((c = choiceMap.get(t)) == null) { // t is not in the choice map, so this a conventional sequncing nod
      System.out.println("traverseEmbedded: " + t.getPayload());
      if (t.getChild() == null) // leaf
        sw.write(t.getPayload() + "");

      for (c = t.getChild(); c != null; c = c.getSibling())
        completed = traveseEmbeddedTrees(choiceMap, c, completed, sw); // propogate the completed value across the tree
      return completed; // and pass the completed value of the rightmost tree back up
    } else { // t is in the choice map
      if (traveseEmbeddedTrees(choiceMap, c, completed, sw)) { // return value dictates whether we update the child
        if (c.getSibling() == null) { // we have processed the last child, so reset to fist child and return true
          choiceMap.put(t, t.getChild());
          return true;
        } else { // we have not processed the last child, so step to the next one and return false
          choiceMap.put(t, c.getSibling());
          return false;
        }
      } else
        return false;
    }
  }

  private static boolean traveseEmbeddedTreesAndPrintYield(Map<ARTValueTerm, ARTValueTerm> choiceMap, ARTValueTerm t, Set<String> uniqueFrontiers) {
    StringWriter sw = new StringWriter();
    boolean ret = traveseEmbeddedTrees(choiceMap, root, true, sw);
    System.out.println("Yield: " + sw);
    uniqueFrontiers.add(sw.toString());
    return ret;
  }

  private static void traverseAllEmbeddedTrees(ARTValueTerm root) {
    Map<ARTValueTerm, ARTValueTerm> choiceMap = new HashMap<>();
    traveseEmbeddedTreesSetup(choiceMap, root);

    int treeCount = 1;
    System.out.println("Tree " + treeCount);
    Set<String> uniqueFrontiers = new HashSet<>();

    while (!traveseEmbeddedTreesAndPrintYield(choiceMap, root, uniqueFrontiers))
      System.out.println("\nTree " + ++treeCount);

    System.out.println("\nUnique frontiers in embedded trees");
    for (String s : uniqueFrontiers)
      System.out.println(s);
  }

}
