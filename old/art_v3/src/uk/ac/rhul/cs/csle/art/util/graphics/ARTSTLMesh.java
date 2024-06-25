package uk.ac.rhul.cs.csle.art.util.graphics;

import javafx.scene.shape.TriangleMesh;
import uk.ac.rhul.cs.csle.art.util.ARTException;

public class ARTSTLMesh extends TriangleMesh {

  public ARTSTLMesh(ARTSTLParser stlParser) throws ARTException {
    this(stlParser, false, 0, 0, 0);
  }

  public ARTSTLMesh(ARTSTLParser stlParser, Boolean flipZ) throws ARTException {
    this(stlParser, flipZ, 0, 0, 0);
  }

  public ARTSTLMesh(ARTSTLParser stlParser, float offsetX, float offsetY, float offsetZ) throws ARTException {
    this(stlParser, false, offsetX, offsetY, offsetZ);
  }

  public ARTSTLMesh(ARTSTLParser stlParser, Boolean flipZ, float offsetX, float offsetY, float offsetZ) throws ARTException {
    super();

    int facetCount = stlParser.getFacetCount();

    float points[] = new float[facetCount * 9];
    float texCoords[] = { 0f, 0f };
    System.out.println("Facet count " + facetCount);
    int faces[] = new int[facetCount * 6];

    ARTCoord normal = new ARTCoord(), vertex1 = new ARTCoord(), vertex2 = new ARTCoord(), vertex3 = new ARTCoord();

    for (int facetNumber = 0; facetNumber < facetCount; facetNumber++) {
      stlParser.readFacet(normal, vertex1, vertex2, vertex3);
      points[facetNumber * 9 + 0] = vertex1.getX() + offsetX;
      points[facetNumber * 9 + 1] = vertex1.getY() + offsetY;
      points[facetNumber * 9 + 2] = vertex1.getZ() + offsetZ;

      points[facetNumber * 9 + 3] = vertex2.getX() + offsetX;
      points[facetNumber * 9 + 4] = vertex2.getY() + offsetY;
      points[facetNumber * 9 + 5] = vertex2.getZ() + offsetZ;

      points[facetNumber * 9 + 6] = vertex3.getX() + offsetX;
      points[facetNumber * 9 + 7] = vertex3.getY() + offsetY;
      points[facetNumber * 9 + 8] = vertex3.getZ() + offsetZ;

      if (flipZ) {
        points[facetNumber * 9 + 2] = -points[facetNumber * 9 + 2];
        points[facetNumber * 9 + 5] = -points[facetNumber * 9 + 5];
        points[facetNumber * 9 + 8] = -points[facetNumber * 9 + 8];
      }

      faces[facetNumber * 6 + 0] = facetNumber * 3 + 0;
      faces[facetNumber * 6 + 2] = facetNumber * 3 + 1;
      faces[facetNumber * 6 + 4] = facetNumber * 3 + 2;
    }

    getPoints().setAll(points);
    getTexCoords().setAll(texCoords);
    getFaces().setAll(faces);
  }

}
