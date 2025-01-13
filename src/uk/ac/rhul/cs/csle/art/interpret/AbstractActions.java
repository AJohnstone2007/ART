package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActions {

  public abstract String name();

  public abstract void action(int nodeNumber, Object[] aBlocks);

  public abstract Object[] createAtributeBlocks(Object parent, int ei);

  protected static String lexeme() {
    return "666";
  }
}
