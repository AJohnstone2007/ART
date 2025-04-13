package uk.ac.rhul.cs.csle.art.util.derivations;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.OutputInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

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
    Util.notImplemented("printCyclicSPPFNodesFromReachability()", getClass());
  }

  public abstract AbstractDerivationNode find(CFGNode cfgNode, int tokenIndex, int i);

  public abstract AbstractDerivationNode extend(CFGNode gn, AbstractDerivationNode leftNode, AbstractDerivationNode rightNode);

  public abstract int widestIndex();

  public abstract void setRoot(CFGNode cfgNode, int i);

  public abstract void numberNodes();

  public abstract void statistics(Statistics currentStatistics);
}
