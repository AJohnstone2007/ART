package uk.ac.rhul.cs.csle.art.cava;

import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.value.ARTValue;

public class ARTCavaSignalYield extends ARTException {
  private ARTValue value;

  public ARTCavaSignalYield(String message) {
    super(message);
  }

  public ARTCavaSignalYield() {
    this("");
  }

  public ARTCavaSignalYield(ARTValue value) {
    this("");
    this.value = value;
  }

  public ARTValue getValue() {
    return value;
  }
}
