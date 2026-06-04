package uk.ac.rhul.cs.csle.art.fx.sandbox;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import uk.ac.rhul.cs.csle.art.fx.Alero;
import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;
import uk.ac.rhul.cs.csle.art.term.mesh.CSG;
import uk.ac.rhul.cs.csle.art.term.mesh.Cuboid;
import uk.ac.rhul.cs.csle.art.term.mesh.Tetrahedron;

public class AleroJavaSandbox {
  public AleroJavaSandbox() throws Exception {
    csgTest();
    // new TriangleExperiments();
    // new PrintonOnSea().createWorld();
  }

  private void csgTest() {
    AleroMesh mesh1 = new Cuboid(20, 30, 60);
    AleroMesh mesh2 = new Tetrahedron(50, 50, 40);
    // AleroMesh mesh = new AleroMesh("stl/rch12TonWagonFullPaintTestJohnstone.stl");
    // showMesh(mesh1);
    // showMesh(mesh2);
    CSG csgA = new CSG(mesh1);
    CSG csgB = new CSG(mesh2);
    CSG csgC = csgB.difference(csgA);
    // // System.out.println("csgC: " + csgC);
    AleroMesh mesh3 = new AleroMesh(csgC);
    showMesh(mesh3);
  }

  void showMesh(Mesh mesh) {
    MeshView meshView = new MeshView(mesh);
    meshView.setDrawMode(DrawMode.FILL);
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseMap(new Image("file:GERTank.png"));
    meshView.setMaterial(material);
    meshView.setCullFace(CullFace.NONE);
    Alero.addPart(meshView);
  }
}
