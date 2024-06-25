package uk.ac.rhul.cs.csle.art.value;

public class ARTValueBoolean extends ARTValue {
  boolean payload = false;

  @Override
  public Boolean getPayload() {
    return payload;
  }

  public ARTValueBoolean(boolean l) {
    this.payload = l;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (payload ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueBoolean)) return false;
    ARTValueBoolean other = (ARTValueBoolean) obj;
    if (payload != other.payload) return false;
    return true;
  }

  @Override
  public ARTValue lnot() {
    return new ARTValueBoolean(!payload);
  }

  @Override
  public ARTValue land(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload && r.castToBoolean().payload);
  }

  @Override
  public ARTValue lor(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload || r.castToBoolean().payload);
  }

  @Override
  public ARTValue lxor(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload ^ r.castToBoolean().payload);
  }

  @Override
  public ARTValueBoolean castToBoolean() {
    return this;
  }

  @Override
  public ARTValueCharacter castToCharacter() {
    return new ARTValueCharacter(payload ? 't' : 'f');
  }

  @Override
  public ARTValueInteger32 castToInteger32() {
    return new ARTValueInteger32(payload ? 1 : 0);
  }

  @Override
  public ARTValueReal64 castToReal64() {
    return payload ? new ARTValueReal64(1.0) : new ARTValueReal64(0.0);
  }

  @Override
  public ARTValueString castToString() {
    return payload ? new ARTValueString("true") : new ARTValueString("false");
  }

}
