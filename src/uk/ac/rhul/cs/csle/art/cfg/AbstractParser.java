package uk.ac.rhul.cs.csle.art.cfg;

import java.io.PrintStream;

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
  public AbstractTasks tasks;
  public AbstractStacks stacks;
  public AbstractLexer lexer;
  protected int inputIndex; // Current input index
  public AbstractDerivations derivations;
  public boolean modeRecogniser;
  public boolean modeHashPool;
  public boolean modeTaskLIFO;
  public boolean modeTaskFIFO;
  public boolean modeProductionLookeahead;
  public boolean modePopLookeahead;
  public boolean modeDerivationTrim;

  public abstract void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules);

  public void outcomeReport() {
    if (lexer.tweSlices == null) return; // A lexical error will already have been reported

    if (inLanguage)
      Util.trace(1, name() + " accept");
    else {
      if (derivations == null)
        Util.trace(1, name() + " reject");
      else {
        int widestIndex = derivations.widestIndex();
        Util.error(Util.echo(name() + " syntax error ", lexer.tweSlices[widestIndex][0].leftExtent, lexer.inputString));
      }
    }
  }

  protected String name() {
    return getClass().getSimpleName();
  }

  public void printCardinalities(PrintStream outputStream) {
    outputStream.println("!print cardinalities not implmented for parser " + name());
  }
}
