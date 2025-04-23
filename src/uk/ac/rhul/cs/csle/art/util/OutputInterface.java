package uk.ac.rhul.cs.csle.art.util;

import java.io.PrintStream;

public interface OutputInterface {
  default void printHeader(PrintStream ps) {
    Util.notImplemented("printHead()", getClass());
  }

  default void print(PrintStream ps) {
    Util.notImplemented("print()", getClass());
  }

  public default void printDot(PrintStream ps) {
    Util.notImplemented("printDot()", getClass());
  }

  public default void show() {
    Util.notImplemented("show()", getClass());
  }
}
