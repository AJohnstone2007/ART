package uk.ac.rhul.cs.csle.art.esos;

import uk.ac.rhul.cs.csle.art.value.ARTValue;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermUsingList;

public class ARTeSOSEntitySingleton extends ARTValueTermUsingList implements ARTeSOSEntity {

  public ARTeSOSEntitySingleton(ARTValue payload) {
    super(payload);
  }
}
