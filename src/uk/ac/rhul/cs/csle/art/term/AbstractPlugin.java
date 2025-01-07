package uk.ac.rhul.cs.csle.art.term;

public abstract class AbstractPlugin {
  abstract public String description();

  abstract public Object plugin(Object... args);
}
