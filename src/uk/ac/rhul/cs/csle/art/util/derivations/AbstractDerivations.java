package uk.ac.rhul.cs.csle.art.util.derivations;

import uk.ac.rhul.cs.csle.art.util.OutputInterface;
import uk.ac.rhul.cs.csle.art.util.Util;

public abstract class AbstractDerivations implements OutputInterface {
  public void chooseLongestMatch() {
    Util.notImplemented("chooseLongestMatch()", getClass());
  }

  public int derivationAsTerm() {
    Util.notImplemented("derivationAsTerm()", getClass());
    return 0;
  }

  public int derivationAsInterpeterTerm() {
    Util.notImplemented("derivationAsInterpreterTerm()", getClass());
    return 0;
  }

  public boolean ambiguityCheck() {
    Util.notImplemented("ambiguityCheck()", getClass());
    return false;
  }

  public void breakCycles(boolean trace, boolean counts, boolean statistics) {
    Util.notImplemented("breakCycles()", getClass());
  }

  public void breakCyclesRelation() {
    Util.notImplemented("breakCyclesRelation()", getClass());
  }

  public void printParaterminals() {
    Util.notImplemented("printParaterminals()", getClass());
  }

  public void printParasentences() {
    Util.notImplemented("printParasentences()", getClass());
  }

  public void printCyclicSPPFNodesFromReachability() {
    // TODO Auto-generated method stub

  }
}
