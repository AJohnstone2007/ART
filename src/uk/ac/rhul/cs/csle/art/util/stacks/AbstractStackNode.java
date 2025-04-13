package uk.ac.rhul.cs.csle.art.util.stacks;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;

public abstract class AbstractStackNode {
  public abstract CFGNode getGrammarNode();

  public abstract Set<AbstractDerivationNode> getPops();

  public abstract Set<GSSEdge> getEdges();
}
