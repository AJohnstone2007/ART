package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActionsNonterminal {
  public int term;

  public abstract void initAttributes(int nodeNumber, int term);

  public abstract AbstractActionsNonterminal getAttributes(int nodeNumber);

  public abstract void action(int nodeNumber);
}
