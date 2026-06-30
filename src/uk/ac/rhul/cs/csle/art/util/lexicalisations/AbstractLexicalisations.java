package uk.ac.rhul.cs.csle.art.util.lexicalisations;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.TWESetElement;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;

public abstract class AbstractLexicalisations implements DisplayInterface {
  public String inputString;
  public CFGRules cfgRules;
  public Integer deleteTokenCount; // Undocumented feature
  public Integer swapTokenCount; // Undocumented feature

  public abstract TWESetElement[] getSlice(int n);

  public abstract void putSlice(int n, TWESetElement[] slice);

  public abstract boolean valid();

  public abstract long cardinality();

  public abstract boolean isDeterministic();

  public abstract String lexeme(CFGElement cfgElement, int leftExtent, int rightExtent);

  public abstract String lexeme(TWESetElement element);

  public abstract void choose(ChooseRules chooseRules);

  public abstract void chooseDefault();

  public abstract void suppressDeadPaths();

  public abstract int removeSuppressedTWE();
}
