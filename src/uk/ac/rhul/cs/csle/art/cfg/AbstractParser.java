package uk.ac.rhul.cs.csle.art.cfg;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStacks;
import uk.ac.rhul.cs.csle.art.util.tasks.AbstractTasks;

public abstract class AbstractParser {
  public boolean inLanguage;
  // public String input;
  public AbstractLexicalisations lexicalisations;
  public AbstractTasks tasks;
  public AbstractStacks stacks;
  public AbstractDerivations derivations;
  protected int inputIndex; // Current input index

  public abstract void parse(AbstractLexicalisations lexicalisations);

  public void outcomeReport() {
    if (!lexicalisations.valid())
      Util.trace(1, name() + "lexical reject");
    else if (inLanguage)
      Util.trace(1, name() + " accept");
    else {
      if (derivations == null)
        Util.trace(1, name() + " reject");
      else {
        int widestIndex = derivations.widestIndex();
        while (widestIndex > 0 && lexicalisations.getSlice(widestIndex) == null)
          widestIndex--; // Step backwards to occupied slice
        Util.error(Util.echo(name() + " syntax error ", lexicalisations.getSlice(widestIndex)[0].leftExtent, lexicalisations.inputString));
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
