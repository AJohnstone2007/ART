package uk.ac.rhul.cs.csle.art.esos;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.value.ARTValueException;

public interface ARTeSOSEntity {
  public String toLatexString(Map<String, String> map) throws ARTValueException;

}
