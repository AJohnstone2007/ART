import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.util.Util;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2025-01-09 17:21:51"; }
  public static class A_S { }
  public static void action24(A_S S, A_X X1){ System.out.println("Found an a"); }
  public static void action26(A_S S, A_X X1){ System.out.println("Found an @"); }
  public static class A_X { int x; }
  public static void action31(A_X X, A_X X1){ System.out.println("Found an x"); }
  public void action(int nodeNumber, Object[] aBlocks) {
    switch(nodeNumber) {
      case 24: action24((A_S) aBlocks[0], (A_X) aBlocks[1]); break;
      case 26: action26((A_S) aBlocks[0], (A_X) aBlocks[1]); break;
      case 31: action31((A_X) aBlocks[0], (A_X) aBlocks[1]); break;
      default: break;
    }
  }
  public Object[] createAtributeBlocks(int ei) {
    switch (ei) {
      case 6: return new Object[] { new A_S(), new A_X() };
      case 7: return new Object[] { new A_X(), new A_X() };
    }
    Util.fatal("internal error - attempt to create attribute block for unknown nonterminal element " + ei);
    return null;
  }
}
