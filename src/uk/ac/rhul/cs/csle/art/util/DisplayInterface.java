package uk.ac.rhul.cs.csle.art.util;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.term.TermTraverserText;

public interface DisplayInterface {
  public default void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indent, int depthLimit) {
    Util.fatal("print output not defined for " + getClass().getName());
  }

  public default void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indent, int depthLimit) {
    Util.fatal("show output not defined for " + getClass().getName());
  }
}
