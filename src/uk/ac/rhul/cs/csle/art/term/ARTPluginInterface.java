package uk.ac.rhul.cs.csle.art.term;

public interface ARTPluginInterface {
  String name();

  Object plugin(Object... args) throws ValueException;
}
