package uk.ac.rhul.cs.csle.art.cfg;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStacks;
import uk.ac.rhul.cs.csle.art.util.stacks.GSS;
import uk.ac.rhul.cs.csle.art.util.tasks.AbstractTasks;
import uk.ac.rhul.cs.csle.art.util.tasks.DescriptorGLL;

public abstract class AbstractParser {
  public AbstractLexer lexer;
  protected int tokenIndex; // Current input index
  public AbstractTasks tasks = null;
  public AbstractDerivations derivations = null;
  public AbstractStacks stacks = null;
  public CFGRules cfgRules;
  public boolean inLanguage;
  public GSS gss;
  public SPPF sppf;
  public Set<DescriptorGLL> descriptors;

  public void parse(AbstractLexer lexer) {
    Util.notImplemented("parse(lexer)", getClass());
  }

  public CFGNode getLHS(CFGNode gn) {
    return cfgRules.elementToNodeMap.get(gn.element);
  }

}
