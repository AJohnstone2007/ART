package uk.ac.rhul.cs.csle.art.cava;

import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.value.ARTValue;

public class ARTCavaSignalReturn extends ARTException {
  private ARTValue value;

  public ARTCavaSignalReturn(String message) {
    super(message);
  }

  public ARTCavaSignalReturn() {
    this("");
  }

  public ARTCavaSignalReturn(ARTValue value) {
    this("");
    this.value = value;
  }

  public ARTValue getValue() {
    return value;
  }
}
