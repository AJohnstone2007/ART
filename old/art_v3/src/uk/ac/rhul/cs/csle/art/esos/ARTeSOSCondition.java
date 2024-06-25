package uk.ac.rhul.cs.csle.art.esos;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.value.ARTValueException;

public abstract class ARTeSOSCondition {
  public abstract String toLatexString(Map<String, String> map) throws ARTValueException;

}
