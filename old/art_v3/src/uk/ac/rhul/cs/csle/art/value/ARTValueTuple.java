package uk.ac.rhul.cs.csle.art.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ARTValueTuple extends ARTValueCollection {

  List<ARTValue> payload = new ArrayList<ARTValue>();

  public List<ARTValue> getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    String ret = "< ";
    for (ARTValue v : payload)
      ret += v.toString() + " ";
    return ret + ">";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = mapString("< ", map);
    for (ARTValue v : payload)
      ret += v.toLatexString(map) + " ";
    return ret + mapString(">", map);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueTuple)) return false;
    ARTValueTuple other = (ARTValueTuple) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public Iterator<ARTValue> iterator() {
    return payload.iterator();
  }
}
