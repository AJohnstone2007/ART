package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.PrintStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.relation.RelationOverNaturals;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class SPPFDummyForRecognisers extends AbstractDerivations {
  public final AbstractParser parser;
  private final boolean firstSuppressParent;
  public SPPFSymbolNode root;
  public String inputString; // Original input string

  public final Map<SPPFSymbolNode, SPPFSymbolNode> nodes = new HashMap<>();
  public final BitSet visited = new BitSet();

  public final BitSet cyclic = new BitSet();
  public RelationOverNaturals reachable;
  public BitSet rootReachable = new BitSet();
  private final BitSet suppressedSPPFNode = new BitSet();
  public final Set<SPPFPackedNode> cbD = new HashSet<>(); // Set of deleted cyclic nodes: D in Elizabeth's note
  public final Set<SPPFPackedNode> cbDPrime = new HashSet<>(); // Set of deleted cyclic nodes: D' in Elizabeth's note

  public SPPFDummyForRecognisers(AbstractParser parser) {
    this(parser, false);
  }

  public SPPFDummyForRecognisers(AbstractParser parser, boolean firstSuppressParent) {
    super();
    this.parser = parser;
    this.firstSuppressParent = firstSuppressParent;
    root = new SPPFSymbolNode(parser.cfgRules.endOfStringNode, 0, 0); // Create a dummy root node and use that for returns
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

  /* Temporary disambiguation before choosers are implemented ****************/
  @Override
  public void chooseLongestMatch() {
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

}
