package uk.ac.rhul.cs.csle.art.old.v4.term;

public interface PluginInterface {
  String name();

  Value plugin(Value... args) throws ValueException;
}
