package uk.ac.rhul.cs.csle.art.util.tasks;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.OutputInterface;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public abstract class AbstractTasks implements OutputInterface {

  public abstract void queue(int tokenIndex, CFGNode cfgNode, AbstractStackNode stackNode, AbstractDerivationNode derivationNode);

  public abstract void statistics(Statistics statistics);

  public abstract DescriptorGLL next();
}
