package uk.ac.rhul.cs.csle.art.util.tasks;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;

public abstract class AbstractTasks implements DisplayInterface {

  public abstract void queue(int tokenIndex, CFGNode cfgNode, AbstractStackNode stackNode, AbstractDerivationNode derivationNode);

  public abstract ConfigurationGLL next();

  public long cardinality() {
    return 0;
  }
}
