package uk.ac.rhul.cs.csle.art.value;

public class ARTValueCharacter extends ARTValueInteger {
  char payload;

  @Override
  public Character getPayload() {
    return payload;
  }

  public ARTValueCharacter(char l) {
    this.payload = l;
  }

  public ARTValueCharacter(String v) throws ARTValueException {
    if (v.length() != 1) throw new ARTValueException("ARTValueCharacter constructor argument is length " + v.length());
    this.payload = v.charAt(0);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + payload;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueCharacter)) return false;
    ARTValueCharacter other = (ARTValueCharacter) obj;
    if (payload != other.payload) return false;
    return true;
  }

  @Override
  public ARTValue gt(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload > r.castToCharacter().payload);
  }

  @Override
  public ARTValue lt(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload < r.castToCharacter().payload);
  }

  @Override
  public ARTValue ge(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload >= r.castToCharacter().payload);
  }

  @Override
  public ARTValue le(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload <= r.castToCharacter().payload);
  }

  @Override
  public ARTValue eq(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload == r.castToCharacter().payload);
  }

  @Override
  public ARTValue ne(ARTValue r) throws ARTValueException {
    return new ARTValueBoolean(payload != r.castToCharacter().payload);
  }

  @Override
  public ARTValue add(ARTValue r) throws ARTValueException {
    return new ARTValueInteger32(payload + r.castToCharacter().payload);
  }

  @Override
  public ARTValue sub(ARTValue r) throws ARTValueException {
    return new ARTValueInteger32(payload - r.castToCharacter().payload);
  }

  @Override
  public ARTValue mul(ARTValue r) throws ARTValueException {
    return new ARTValueInteger32(payload * r.castToCharacter().payload);
  }

  @Override
  public ARTValue div(ARTValue r) throws ARTValueException {
    return new ARTValueInteger32(payload / r.castToCharacter().payload);
  }

  @Override
  public ARTValue mod(ARTValue r) throws ARTValueException {
    return new ARTValueInteger32(payload % r.castToCharacter().payload);
  }

  @Override
  public ARTValue exp(ARTValue r) throws ARTValueException {
    return new ARTValueInteger32((int) (Math.pow(payload, r.castToReal64().payload)));
  }

  @Override
  public ARTValue neg() {
    return new ARTValueInteger32(-payload);
  }

  @Override
  public ARTValueBoolean castToBoolean() {
    return new ARTValueBoolean(payload != 0);
  }

  @Override
  public ARTValueCharacter castToCharacter() {
    return this;
  }

  @Override
  public ARTValueInteger32 castToInteger32() {
    return new ARTValueInteger32(payload);
  }

  @Override
  public ARTValueReal64 castToReal64() {
    return new ARTValueReal64(payload);
  }

  @Override
  public ARTValueString castToString() {
    return new ARTValueString("" + payload);
  }

  @Override
  protected int getCoercionPriority() {
    return coercionPriorityCharacter;
  }

}
