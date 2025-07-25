package uk.ac.rhul.cs.csle.art.util.stacks;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public abstract class AbstractStackNode {
  public abstract CFGNode getGrammarNode();

  public abstract Set<PopSetElement> getPops();

  public abstract Set<GSSEdge> getEdges();

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);

}
