package uk.ac.rhul.cs.csle.art.cfg;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStacks;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;
import uk.ac.rhul.cs.csle.art.util.tasks.AbstractTasks;

public abstract class AbstractParser {
  public boolean inLanguage;
  public String input;
  public CFGRules cfgRules;
  public AbstractLexer lexer;
  protected int tokenIndex; // Current input index
  protected AbstractTasks tasks;
  public AbstractStacks stacks;
  public AbstractDerivations derivations;

  public abstract void parse(String input, CFGRules cfgRules, AbstractLexer lexer);

  public void statistics(Statistics currentStatistics) {
    tasks.statistics(currentStatistics);
    stacks.statistics(currentStatistics);
    derivations.statistics(currentStatistics);
  }
}
