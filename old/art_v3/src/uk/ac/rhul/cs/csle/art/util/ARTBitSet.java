package uk.ac.rhul.cs.csle.art.util;

import java.util.BitSet;

public class ARTBitSet extends BitSet {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public void setAll(int... elements) {
    for (int e : elements)
      set(e);
  }

}
