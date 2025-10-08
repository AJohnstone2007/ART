package uk.ac.rhul.cs.csle.art.util.stacks;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.tasks.AbstractTasks;

public abstract class AbstractStacks implements DisplayInterface {
  // public abstract GSSNode find(CFGNode grammarNode, int i);

  public abstract AbstractStackNode getRoot();

  public abstract AbstractStackNode push(AbstractDerivations derivations, AbstractTasks tasks, int tokenIndex, CFGNode gn, AbstractStackNode stackNode,
      AbstractDerivationNode derivationNode);

  public abstract void pop(AbstractDerivations derivations, AbstractTasks tasks, int tokenIndex, AbstractStackNode stackNode,
      AbstractDerivationNode derivationNode);

  public abstract void toDot();

  public long nodeCardinality() {
    return 0;
  }

  public long edgeCardinality() {
    return 0;
  }

  public long popCardinality() {
    return 0;
  }
}
