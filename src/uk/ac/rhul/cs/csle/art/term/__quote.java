package uk.ac.rhul.cs.csle.art.term;

public class __quote {
  int term;

  public __quote(int term) {
    super();
    this.term = term;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + term;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    __quote other = (__quote) obj;
    if (term != other.term) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Quote:" + term;
  }

}
