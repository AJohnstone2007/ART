package uk.ac.rhul.cs.csle.art.value;

public class ARTValueSignalYield extends ARTValueSignal {

  ARTValue v;

  public ARTValueSignalYield() {
    this.v = new ARTValueUndefined();
  }

  public ARTValueSignalYield(ARTValue v) {
    this.v = v;
  }

  @Override
  public ARTValue v() {
    return v;
  }

}
