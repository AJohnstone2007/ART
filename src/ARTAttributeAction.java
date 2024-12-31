public class ARTAttributeAction {

  public static class A {
    public int yy;
    public int zz;
  }

  public static class S {
    public int y;
  }

  public void action22(S S, A A1, A A2) {
    S.y = A1.yy + A2.yy - A2.zz;
  }

  public void action23() {
  }

  public void action(int nodeNumber, Object[] aBlocks) {
    switch (nodeNumber) {
    case 22:
      action22((S) aBlocks[0], (A) aBlocks[1], (A) aBlocks[2]);
      break;
    }
  }
}
