package uk.ac.rhul.cs.csle.art.util.lexicalisations;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.TWESetElement;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public abstract class AbstractLexicalisations implements DisplayInterface {
  public String inputString;
  public CFGRules cfgRules;
  public Integer deleteTokenCount; // Undocumented feature
  public Integer swapTokenCount; // Undocumented feature

  public abstract TWESetElement[] getSlice(int n);

  public abstract void putSlice(int n, TWESetElement[] slice);

  public abstract boolean valid();

  public abstract int cardinality();

  public abstract boolean isDeterministic();

  @Override
  public abstract void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented);

  @Override
  public abstract void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented);

  @Override
  public abstract void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented);

  public abstract String lexeme(CFGNode grammarNode, int leftExtent);

  public abstract String lexeme(TWESetElement element);

  public abstract void choose(ChooseRules chooseRules);

  public abstract void chooseDefault();

  public abstract void suppressDeadPaths();

  public abstract void removeSuppressedTWE();
}
