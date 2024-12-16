package uk.ac.rhul.cs.csle.alero.hardCoded;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import uk.ac.rhul.cs.csle.alero.Alero;
import uk.ac.rhul.cs.csle.alero.AleroException;
import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;

/* This is a JavaFX demonstration of basic vector algrabra properties in 3D */

public class TriangleExperiments {

//  public TriangleExperiments() throws AleroException {
//
//    // Mesh mesh = new Tetrahedron(50, 50, 40);
//    // showMesh(mesh);
//    Point3D origin = new Point3D(0, 0, 0);
//    Point3D p1 = new Point3D(10, 10, 10);
//    Point3D p2 = new Point3D(50, 0, 50);
//    Point3D p3 = new Point3D(0, 50, 50);
//
//    showMesh(singleTriangle(p1, p2, p3), Color.OLDLACE);
//
//    drawLine(p1, p2, Color.CYAN);
//    drawLine(p2, p3, Color.MAGENTA);
//    drawLine(p3, p1, Color.YELLOW);
//
//    Point3D edge1Dir = p2.subtract(p1);
//    Point3D edge2Dir = p3.subtract(p2);
//    Point3D edge3Dir = p1.subtract(p3);
//
//    drawLine(origin, edge1Dir, Color.CYAN);
//    drawLine(origin, edge2Dir, Color.MAGENTA);
//    drawLine(origin, edge3Dir, Color.YELLOW);
//
//    Point3D normal = edge1Dir.crossProduct(edge2Dir);
//
//    drawLine(origin, normal, Color.ORANGE);
//  }
//
//  Mesh singleTriangle(Point3D p1, Point3D p2, Point3D p3) {
//    TriangleMesh ret = new TriangleMesh();
//    ret.getTexCoords().setAll(0, 0);
//    ret.getPoints().setAll((float) p1.getX(), (float) p1.getY(), (float) p1.getZ(), (float) p2.getX(), (float) p2.getY(), (float) p2.getZ(), (float) p3.getX(),
//        (float) p3.getY(), (float) p3.getZ());
//    ret.getFaces().setAll(0, 0, 1, 0, 2, 0);
//    return ret;
//  }
//
//  void drawLine(Point3D p1, Point3D p2, Color color) {
//    showMesh(singleTriangle(p1, p2, p1), color, true);
//  }
//
//  void showVector(float x, float y, float z) throws AleroException {
//    showMesh(new AleroMesh(new float[] { 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0 }, new float[] { x, y, z }), Color.MAGENTA);
//  }
//
//  void showMesh(Mesh mesh, Color color) {
//    showMesh(mesh, color, false);
//  }
//
//  void showMesh(Mesh mesh, Color color, boolean lineOnly) {
//    MeshView meshView = new MeshView(mesh);
//    meshView.setDrawMode(lineOnly ? DrawMode.LINE : DrawMode.FILL);
//    PhongMaterial material = new PhongMaterial();
//    material.setDiffuseColor(color);
//    meshView.setMaterial(material);
//    // meshView.setCullFace(CullFace.NONE);
//    Alero.add(meshView);
//  }
}
