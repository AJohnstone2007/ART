package uk.ac.rhul.cs.csle.art.lex;

class ARTTWEPair {
  int token, rightExtent;
  public boolean suppressed = false;

  ARTTWEPair(int token, int rightExtent) {
    this.token = token;
    this.rightExtent = rightExtent;
  }

  @Override
  public String toString() {
    return token + (suppressed ? "!" : ".") + rightExtent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + rightExtent;
    result = prime * result + token;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTTWEPair)) return false;
    ARTTWEPair other = (ARTTWEPair) obj;
    if (rightExtent != other.rightExtent) return false;
    if (token != other.token) return false;
    return true;
  }
}