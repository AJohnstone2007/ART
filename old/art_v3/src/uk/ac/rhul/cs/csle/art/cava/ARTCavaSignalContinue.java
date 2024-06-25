package uk.ac.rhul.cs.csle.art.cava;

import uk.ac.rhul.cs.csle.art.util.ARTException;

public class ARTCavaSignalContinue extends ARTException {

  public ARTCavaSignalContinue(String message) {
    super(message);
  }

  public ARTCavaSignalContinue() {
    this("");
  }
}
