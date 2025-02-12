package uk.ac.rhul.cs.csle.art.term;

import java.util.Map;

public class __blob {
  private static int nextFreeNumber = 1;
  private static Map<Integer, __blob> cache;

  public int number;
  public Object payload;

  __blob(Object payload) {
    number = nextFreeNumber++;
    this.payload = payload;
    cache.put(number, this);
  }

  void free(int number) {
    cache.remove(number);
  }

  public static __blob get(int term) {
    return cache.get(term);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + number;
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    __blob other = (__blob) obj;
    if (number != other.number) return false;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Blob:");
    builder.append(number);
    builder.append("=");
    builder.append(payload);
    return builder.toString();
  }

}
