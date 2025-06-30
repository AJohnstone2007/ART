package uk.ac.rhul.cs.csle.art.cfg;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStacks;
import uk.ac.rhul.cs.csle.art.util.tasks.AbstractTasks;

public abstract class AbstractParser {
  public boolean inLanguage;
  public String input;
  public CFGRules cfgRules;
  public AbstractLexer lexer;
  protected int inputIndex; // Current input index
  protected AbstractTasks tasks;
  public AbstractStacks stacks;
  public AbstractDerivations derivations;

  public abstract void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules);

  public void outcomeReport() {
    if (lexer.tweSlices == null) return; // A lexical error will already have been reported

    if (inLanguage) {
      Util.trace(1, "Parser accept");
      if (derivations == null) Util.error("current parser does not produce API level derivations");
    } else if (derivations == null)
      Util.error("Syntax error");
    else
      Util.error(Util.echo("Syntax error", lexer.tweSlices[derivations.widestIndex()][0].lexemeStart, lexer.inputString));
  }
}
