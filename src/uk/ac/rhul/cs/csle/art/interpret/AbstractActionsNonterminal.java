package uk.ac.rhul.cs.csle.art.interpret;

public abstract class AbstractActionsNonterminal {

  public abstract void action(int nodeNumber);

  public abstract AbstractActionsNonterminal call(int nodeNumber);
}
