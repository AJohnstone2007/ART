package uk.ac.rhul.cs.csle.art.value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ARTValueRecord extends ARTValueCollection {

  protected Map<ARTValue, ARTValue> payload;

  public Map<ARTValue, ARTValue> getPayload() {
    return payload;
  }

  public ARTValueRecord() {
    payload = new LinkedHashMap<ARTValue, ARTValue>();
  }

  public ARTValueRecord(ARTValueRecord seed) {
    payload = new LinkedHashMap<ARTValue, ARTValue>(seed.payload);
  }

  @Override
  public String toString() {
    String ret = "< ";
    for (Iterator<ARTValue> iter = payload.keySet().iterator(); iter.hasNext();) {
      ARTValue k = iter.next();
      ret += k + "=" + payload.get(k);
      ret += iter.hasNext() ? ", " : "";
    }
    return ret + " >";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = mapString("<", map);
    for (Iterator<ARTValue> iter = payload.keySet().iterator(); iter.hasNext();) {
      ARTValue k = iter.next();
      ret += k.toLatexString(map) + mapString("=", map) + payload.get(k).toLatexString(map);
      ret += iter.hasNext() ? "," : "";
    }
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
    if (!(obj instanceof ARTValueRecord)) return false;
    ARTValueRecord other = (ARTValueRecord) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public Iterator<ARTValue> iterator() {
    return payload.keySet().iterator();
  }

  @Override
  public ARTValue cardinality() {
    return new ARTValueInteger32(payload.size());
  }

  @Override
  public ARTValue contains(ARTValue key) {
    return new ARTValueBoolean(payload.containsKey(key));
  }

  @Override
  public ARTValue insertKey(ARTValue key, ARTValue value) throws ARTValueException {
    if (payload.containsKey(key)) throw new ARTValueException("ARTValueRecord insertion of already existing key: " + key);
    payload.put(key, value);
    return this;
  }

  @Override
  public ARTValue update(ARTValue key, ARTValue value) throws ARTValueException {
    if (!payload.containsKey(key)) throw new ARTValueException("ARTValueRecord update of unknown key: " + key);
    payload.put(key, value);
    return this;
  }

  @Override
  public ARTValue valueKey(ARTValue key) throws ARTValueException {
    if (payload.containsKey(key))
      return payload.get(key);
    else
      throw new ARTValueException("ARTValueRecord access to unknown key " + key + " in record " + payload);
  }
}
