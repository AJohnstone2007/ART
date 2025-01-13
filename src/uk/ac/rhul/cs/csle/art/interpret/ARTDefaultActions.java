package uk.ac.rhul.cs.csle.art.interpret;

public class ARTDefaultActions extends AbstractActions {

  @Override
  public void action(int nodeNumber, Object[] aBlocks) {
  }

  @Override
  public String name() {
    return "empty actions";
  }

  @Override
  public Object[] createAtributeBlocks(Object parent, int ei) {
    return null;
  }

}
