package uk.ac.rhul.cs.csle.art.util.stacks;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.OutputInterface;
import uk.ac.rhul.cs.csle.art.util.Util;

public abstract class AbstractStacks implements OutputInterface {
  public GSSNode find(CFGNode grammarNode, int i) {
    Util.notImplemented("find()", getClass());
    return null;
  }
}
