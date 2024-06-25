package uk.ac.rhul.cs.csle.art.value;

import java.util.Map;

public abstract class ARTValueCollection extends ARTValue implements Iterable<ARTValue> {
  @Override
  public String toString() {
    String ret = "[ ";
    // for (ARTValue v : getPayload())
    // ret += v + " ";
    return ret + "]";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = "[ ";
    // for (ARTValue v : payload)
    // ret += v.toLatexString(map) + " ";
    return ret + "]";
  }

}
