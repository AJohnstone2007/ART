package uk.ac.rhul.cs.csle.art.util;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public interface DisplayInterface {
  public default void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indent, int depthLimit) {
    Util.fatal("print output not defined for " + getClass().getName());
  };

  public default void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indent, int depthLimit) {
    Util.fatal("show output not defined for " + getClass().getName());
  };

  public default void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indent, int depthLimit) {
    Util.fatal("statistics output not defined for " + getClass().getName());
  };

  default void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indent) {
    Util.fatal("print short output not defined for " + getClass().getName());
  };
}
