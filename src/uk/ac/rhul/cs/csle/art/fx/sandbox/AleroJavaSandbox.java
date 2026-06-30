package uk.ac.rhul.cs.csle.art.fx.sandbox;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import uk.ac.rhul.cs.csle.art.fx.Alero;
import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;
import uk.ac.rhul.cs.csle.art.term.mesh.csg.CSG;
import uk.ac.rhul.cs.csle.art.term.mesh.generators.Cuboid;
import uk.ac.rhul.cs.csle.art.term.mesh.generators.Pyramid;

public class AleroJavaSandbox {
  public AleroJavaSandbox() throws Exception {
    csgTest();
    // new TriangleExperiments();
    // new PrintonOnSea().createWorld();
  }

  private void csgTest() {
    AleroMesh mesh1 = new Pyramid(50, 50, 40).translate(-75, 0, 0), mesh2 = new Cuboid(20, 30, 40).translate(-75, 0, 0);
    CSG csgA, csgB;
    // showMesh(mesh1);
    // showMesh(mesh2);

    csgA = new CSG(mesh1.translate(0, 0, 0));
    csgB = new CSG(mesh2.translate(0, 0, 0));
    showMesh(new AleroMesh(csgA.union(csgB)));

    csgA = new CSG(mesh1.translate(75, 0, 0));
    csgB = new CSG(mesh2.translate(75, 0, 0));
    showMesh(new AleroMesh(csgA.difference(csgB)));

    csgA = new CSG(mesh1.translate(75, 0, 0));
    csgB = new CSG(mesh2.translate(75, 0, 0));
    showMesh(new AleroMesh(csgA.intersection(csgB)));
  }

  void showMesh(Mesh mesh) {
    MeshView meshView = new MeshView(mesh);
    meshView.setDrawMode(DrawMode.LINE);
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseMap(new Image("file:GERTank.png"));
    meshView.setMaterial(material);
    meshView.setCullFace(CullFace.NONE);
    Alero.addPart(meshView);
  }

  // private void csgTest() {
  // AleroMesh mesh1 = new Cuboid(20, 30, 40);
  // AleroMesh mesh2 = new Pyramid(50, 50, 40);
  // // AleroMesh mesh = new AleroMesh("stl/rch12TonWagonFullPaintTestJohnstone.stl");
  // // AleroMesh mesh2 = null;
  // // try {
  // // mesh2 = new AleroMesh("stl/gearHousing.stl");
  // // } catch (Exception e) {
  // // // TODO Auto-generated catch block
  // // e.printStackTrace();
  // // }
  // Util.debug("Loaded both meshes");
  // // showMesh(mesh1);
  // // showMesh(mesh2);
  // CSG csgA = new CSG(mesh1);
  // Util.debug("made csgA");
  // CSG csgB = new CSG(mesh2);
  // Util.debug("made csgB - about to compute difference");
  // CSG csgC = csgB.difference(csgA);
  // // // System.out.println("csgC: " + csgC);
  // Util.debug("Computed difference; about to make mesh from difference");
  // Alero.currentMesh = new AleroMesh(csgC);
  // Util.debug("Displaying difference mesh");
  // showMesh(Alero.currentMesh);
  // Util.debug("Display finished");
  // try {
  // Alero.currentMesh.toASCIIFile("ascii.stl");
  // Alero.currentMesh.toBinaryFile("binary.stl");
  // } catch (FileNotFoundException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // Util.debug("Dump complete");
  // }
  //

}
