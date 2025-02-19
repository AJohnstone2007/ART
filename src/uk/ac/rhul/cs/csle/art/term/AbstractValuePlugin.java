package uk.ac.rhul.cs.csle.art.term;

public abstract class AbstractValuePlugin {
  protected static __done __done = new __done();
  protected static __empty __empty = new __empty();
  protected static __bottom __bottom = new __bottom();

  abstract public String description();

  abstract public Object plugin(Object... args);
}
