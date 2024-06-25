package uk.ac.rhul.cs.csle.art.util.graphics;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.ARTException;

public abstract class ARTSTLParser {
  Set<ARTCoord> pointSet = new HashSet<ARTCoord>();
  Set<ARTFace> faceSet = new HashSet<ARTFace>();

  public abstract void readFacet(ARTCoord normal, ARTCoord vertex1, ARTCoord vertex2, ARTCoord vertex3) throws ARTException;

  public int getFaceCount() {
    return faceSet.size();
  }

  public abstract int getFacetCount();
}
