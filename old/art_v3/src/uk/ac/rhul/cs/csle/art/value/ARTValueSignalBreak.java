package uk.ac.rhul.cs.csle.art.value;

public class ARTValueSignalBreak extends ARTValueSignal {
  @Override
  public ARTValue v() {
    return new ARTValueUndefined();
  }

}
