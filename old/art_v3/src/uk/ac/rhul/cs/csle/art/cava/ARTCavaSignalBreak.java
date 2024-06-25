package uk.ac.rhul.cs.csle.art.cava;

import uk.ac.rhul.cs.csle.art.util.ARTException;

public class ARTCavaSignalBreak extends ARTException {

  public ARTCavaSignalBreak(String message) {
    super(message);
  }

  public ARTCavaSignalBreak() {
    this("");
  }
}
