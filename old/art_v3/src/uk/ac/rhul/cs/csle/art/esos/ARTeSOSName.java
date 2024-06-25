package uk.ac.rhul.cs.csle.art.esos;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.value.ARTValue;
import uk.ac.rhul.cs.csle.art.value.ARTValueString;

public class ARTeSOSName extends ARTValueString {
  // name handling - we allow multiple primes and a single integer subscript prime = subscript = 0 means plain and prototype means created in transition
  // directive and needing to be initialised within the static check routine
  protected boolean leadingUnderscore = false;

  protected int subscript;
  protected int primeCount = 0;

  public ARTeSOSName(ARTeSOSName payload) {
    super(payload.getV());
    leadingUnderscore = payload.getLeadingUnderscore();
    subscript = payload.getSubscript();
    primeCount = payload.getPrimeCount();
  }

  ARTeSOSName(String payload) {
    super("");
    if (payload.charAt(0) == '_') leadingUnderscore = true;
    int start = leadingUnderscore ? 1 : 0;
    int finish = payload.length() - 1;
    while (payload.charAt(finish) == '\'') {
      primeCount++;
      finish--;
    }
    while (Character.isDigit(payload.charAt(finish)))
      finish--;
    this.payload = payload.substring(start, finish + 1);
    if (finish + 1 < payload.length() - primeCount) this.subscript = Integer.parseInt(payload.substring(finish + 1, payload.length() - primeCount));
  }

  ARTeSOSName base() {
    return new ARTeSOSName(payload);
  }

  public ARTValue base(int i) {
    ARTeSOSName ret = new ARTeSOSName(payload);
    ret.setSubscript(i);
    return ret;
  }

  public void setLeadingUnderscore(boolean leadingUnderscore) {
    this.leadingUnderscore = leadingUnderscore;
  }

  public void setSubscript(int subscript) {
    this.subscript = subscript;
  }

  public void setPrimeCount(int primeCount) {
    this.primeCount = primeCount;
  }

  public String getV() {
    return payload;
  }

  public boolean getLeadingUnderscore() {
    return leadingUnderscore;
  }

  public int getSubscript() {
    return subscript;
  }

  public int getPrimeCount() {
    return primeCount;
  }

  @Override
  public String toString() {
    String ret = leadingUnderscore ? "_" : "";
    ret += payload;
    if (subscript > 0) ret += subscript;
    for (int i = 0; i < primeCount; i++)
      ret += "'";
    return ret;
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = leadingUnderscore ? "\\_" : "";
    ret += mapString(payload, map);
    if (subscript > 0) ret += "_{" + subscript + "}";
    if (primeCount > 0) {
      ret += "^{";
      for (int i = 0; i < primeCount; i++)
        ret += "\\prime";
      ret += "}";
    }
    return ret;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (leadingUnderscore ? 1231 : 1237);
    result = prime * result + primeCount;
    result = prime * result + subscript;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (!(obj instanceof ARTeSOSName)) return false;
    ARTeSOSName other = (ARTeSOSName) obj;
    if (leadingUnderscore != other.leadingUnderscore) return false;
    if (primeCount != other.primeCount) return false;
    if (subscript != other.subscript) return false;
    return true;
  }

  @Override
  public int compareTo(Object that) {
    if (!(that instanceof ARTeSOSName)) return -1;
    return toString().compareTo(((ARTValueString) that).toString());
  }
}
