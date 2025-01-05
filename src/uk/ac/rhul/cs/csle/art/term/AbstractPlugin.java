package uk.ac.rhul.cs.csle.art.term;

public abstract class AbstractPlugin {
  abstract public String name();

  abstract public Object plugin(Object... args);
}
