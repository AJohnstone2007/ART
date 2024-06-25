package uk.ac.rhul.cs.csle.art.value;

public class ARTValueSignalReturn extends ARTValueSignal {
  ARTValue v;

  public ARTValueSignalReturn() {
    this.v = new ARTValueUndefined();
  }

  public ARTValueSignalReturn(ARTValue v) {
    this.v = v;
  }

  @Override
  public ARTValue v() {
    return v;
  }
}
