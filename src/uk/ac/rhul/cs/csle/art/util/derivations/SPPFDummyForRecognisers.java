package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class SPPFDummyForRecognisers extends AbstractDerivations {
  public SPPFSymbolNode root;

  public SPPFDummyForRecognisers(AbstractLexicalisations lexicalisations) {
    super();
    this.lexicalisations = lexicalisations;
    root = new SPPFSymbolNode(lexicalisations.cfgRules.endOfStringNode, 0, 0); // Create a dummy root node and use that for returns
  }

  @Override
  public AbstractDerivationNode find(CFGNode dn, int leftExtent, int rightExtent) {
    return root;
  }

  @Override
  public AbstractDerivationNode extend(CFGNode gn, AbstractDerivationNode leftNode, AbstractDerivationNode rightNode) {
    return root;
  }

  @Override
  public void numberNodes() {
  }

  @Override
  public int widestIndex() {
    return 0;
  }

  /* Term generation **************************************************************************/
  /* This version handles promotion operators, but does not create ambiguity nodes */

  long derivationNodeCount = 0, derivationAmbiguityNodeCount = 0;

  boolean derivationForInterpreter = false;

  @Override
  public int derivationAsInterpeterTerm() {
    return 0;
  }

  boolean derivationSeenCycle;

  @Override
  public int derivationAsTerm() {
    return 0;
  }

  @Override
  public boolean ambiguityCheck() {
    return false;
  }

  @Override
  public void setRoot(CFGNode cfgNode, int n) {
  }

  public void loadStatistics(Statistics currentStatistics) {
  }

  @Override
  public void choose(ChooseRules chooseRules) {
    // TODO Auto-generated method stub - no choosers at present
  }

  @Override
  public void breakCycles(boolean trace, boolean counts, boolean statistics) {
  }

  @Override
  public void breakCyclesRelation() {
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
  }

  @Override
  public long bsrCardinality() {
    return -1;
  }

  @Override
  public long symbolCardinality() {
    return -1;
  }
}
