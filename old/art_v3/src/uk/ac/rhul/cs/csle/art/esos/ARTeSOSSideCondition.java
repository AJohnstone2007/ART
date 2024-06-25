package uk.ac.rhul.cs.csle.art.esos;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.value.ARTValueException;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermUsingList;

public class ARTeSOSSideCondition extends ARTeSOSCondition {

  private final ARTValueTermUsingList lhs, rhs;

  public ARTValueTermUsingList getLhs() {
    return lhs;
  }

  public ARTValueTermUsingList getRhs() {
    return rhs;
  }

  public ARTeSOSSideCondition(ARTValueTermUsingList lhs, ARTValueTermUsingList rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return lhs + " |> " + rhs;
  }

  @Override
  public String toLatexString(Map<String, String> map) throws ARTValueException {
    return lhs.toLatexString(map) + " " + (map.containsKey("|>") ? map.get("|>") : "|>") + rhs.toLatexString(map);
  }
}
