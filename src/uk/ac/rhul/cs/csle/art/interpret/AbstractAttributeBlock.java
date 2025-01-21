package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractAttributeBlock {
  public int term;

  public abstract void initRHSAttributeBlock(int nodeNumber, int term);

  public abstract AbstractAttributeBlock getAttributes(int nodeNumber);

  public abstract void action(int nodeNumber);
}
