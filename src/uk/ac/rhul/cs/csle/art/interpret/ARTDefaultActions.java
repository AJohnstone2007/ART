package uk.ac.rhul.cs.csle.art.interpret;

public class ARTDefaultActions extends AbstractActions {
  @Override
  public String name() {
    return "empty actions";
  }

  @Override
  public AbstractActionsNonterminal init(AbstractInterpreter interpreter, int term) {
    return null;
  }
}
