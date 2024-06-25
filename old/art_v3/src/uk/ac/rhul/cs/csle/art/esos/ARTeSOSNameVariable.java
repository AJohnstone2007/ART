package uk.ac.rhul.cs.csle.art.esos;

import uk.ac.rhul.cs.csle.art.value.ARTValueTermVariable;

public class ARTeSOSNameVariable extends ARTeSOSName implements ARTValueTermVariable {

  public ARTeSOSNameVariable(String payload) {
    super(payload);
  }

  public ARTeSOSNameVariable(ARTeSOSName payload) {
    super(payload);
  }
}