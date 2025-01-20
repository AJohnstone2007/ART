package uk.ac.rhul.cs.csle.art.interpret;

import uk.ac.rhul.cs.csle.art.util.Util;

public class eSOSInterpreter extends AbstractInterpreter {

  @Override
  public void interpretUsingDerivationTermRec(int term, AbstractActionsNonterminal nonterminalActions) {
    Util.fatal("eSOSIntepreter does not support attributeAction interpeters");
  }

}
