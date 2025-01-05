import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2025-01-05 16:47:03"; }
  public static class A_S { }
  public static class A_X { }
  public static void action0(A_X X){ System.out.println("Found an x"); }
  public void action(int nodeNumber, Object[] aBlocks) {
    switch(nodeNumber) {
      case 0: action0((A_X) aBlocks[0]); break;
      default: break;
    }
  }
}
