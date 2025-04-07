package uk.ac.rhul.cs.csle.art.util.derivations;

import uk.ac.rhul.cs.csle.art.util.OutputInterface;

public class AbstractDerivations implements OutputInterface {
  public void chooseLongestMatch() {
    System.out.println("chooseLongestMatch() not implemented in class " + this.getClass().getSimpleName());
  }

  public int derivationAsTerm() {
    System.out.println("derivationAsTerm() not implemented in class " + this.getClass().getSimpleName());
    return 0;
  }

  public int derivationAsInterpeterTerm() {
    System.out.println("derivationAsInterpreterTerm() not implemented in class " + this.getClass().getSimpleName());
    return 0;
  }

  public boolean ambiguityCheck() {
    System.out.println("ambiguityCheck() not implemented in class " + this.getClass().getSimpleName());
    return false;
  }

  public void breakCycles(boolean trace, boolean counts, boolean statistics) {
    // TODO Auto-generated method stub

  }

  public void breakCyclesRelation() {
    // TODO Auto-generated method stub

  }

  public void printParaterminals() {
    // TODO Auto-generated method stub

  }

  public void printParasentences() {
    // TODO Auto-generated method stub

  }

  public void print() {
    // TODO Auto-generated method stub

  }

  public void printCyclicSPPFNodesFromReachability() {
    // TODO Auto-generated method stub

  }

  public void show() {
    // TODO Auto-generated method stub

  }
}
