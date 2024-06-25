import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDT;
import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTGenerateMeshExtrudedPolygon;
import uk.ac.rhul.cs.csle.art.util.graphics.ARTSceneViewer;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerConsole;

public class ARTFXTest extends Application {

  static ARTText text = new ARTText(new ARTTextHandlerConsole());

  static void visitTree(ARTGLLRDT tree) {
    visitTree(tree, tree.getRoot(), 0);
  }

  static void visitTree(ARTGLLRDT tree, ARTGLLRDTVertex vertex, int level) {
    if (vertex == null) return;
    // Preorder printout
    for (int i = 0; i < level; i++)
      text.print("  ");
    text.printf("%d: %s%n", vertex.getKey(), vertex.getPayload().toString(tree));
    visitTree(tree, vertex.getChild(), level + 1);
    visitTree(tree, vertex.getSibling(), level);
  }

  // private void exerciseSPPFRenderer() throws ARTException {
  // ARTGLLParserBase parser = new ARTGLLParserHashPool(new ARTGeneratedLexer());
  //
  // try {
  // parser.artParse(ARTText.readFile("test.str"));
  // } catch (ARTException e) {
  // ARTText.printFatal(e.getMessage());
  // }
  // ;
  //
  // if (parser.artIsInLanguage)
  // text.println("Accept");
  // else {
  // text.println("Reject");
  // }
  // parser.artDisambiguateRightmost();
  // parser.artDerivationSelectFirst();
  // // parser.artRenderSPPF("dt.dot", parser.artRenderKindDerivation);
  // // parser.artRenderSPPF("dtfull.dot", parser.artRenderKindDerivationFull);
  // // parser.artRenderSPPF("gss.dot", parser.artRenderKindGSS);
  // // parser.artRenderSPPF("sppf.dot", parser.artRenderKindSPPFFull);
  // // parser.artRenderSPPF("sppffull.dot", parser.artRenderKindSPPFFull);
  //
  // ARTGLLRDT tree = parser.artEvaluator();
  //
  // // new ARTFXTreeNavigator3D(tree, false);
  // // new ARTFXTreeNavigator3D(tree, true);
  // new ARTFXSPPFNavigator3D(parser, false);
  //
  // if (tree != null) {
  // // tree.printDot("rdt.dot");
  // // visitTree(tree);
  // } else {
  // text.println("artEvaluator returns null tree\n");
  // }
  //
  // // text.println("Case sensitive terminal names: " + parser.artCaseSensitiveTerminalNames());
  // // text.println("Case insensitive terminal names: " + parser.artCaseInsensitiveTerminalNames());
  // // text.println("Builtin terminal names: " + parser.artBuiltinTerminalNames());
  // // text.println("Character terminal names: " + parser.artCharacterTerminalNames());
  // // text.println("Nonerminal names: " + parser.artNonterminalNames());
  //
  // }

  @Override
  public void start(Stage primaryStage) throws Exception {
    //
    // String[] args = (String[]) getParameters().getRaw().toArray();
    //
    // if (args.length == 0) ARTText.printFatal("Usage - java ARTTest <inputfilename>%n");

    // exerciseSPPFRenderer();

    exerciseMeshGenerators(primaryStage);
  }

  private void exerciseMeshGenerators(Stage primaryStage) throws FileNotFoundException {
    ARTSceneViewer viewer = new ARTSceneViewer("Mesh Generator Test");
    viewer.addAxes(-110, 200);

    // ARTGenerateMeshExtrudedPolygon mesh = new ARTGenerateMeshExtrudedPolygon(10, 5, 0, 0, 20, 10, 9, (float) 1.0);
    ARTGenerateMeshExtrudedPolygon mesh = new ARTGenerateMeshExtrudedPolygon(10, 5, 5, 3, 20, 10, 9, (float) 1.0);
    // ARTGenerateMeshExtrudedPolygon mesh = new ARTGenerateMeshExtrudedPolygon(100, 100, 0, 0, 200, 4, 4, (float) 1.0);
    // ARTGenerateMeshPyramid mesh = new ARTGenerateMeshPyramid(100, 100);
    mesh.writeASCIISTL("output.stl", "test");
    mesh.setColour("brass");
    MeshView meshView = new MeshView(mesh);
    // meshView.setCullFace(CullFace.NONE); // So we can see the backs of the triangles in case the winding order is incorrect - will render in black
    meshView.setMaterial(mesh.getMaterial());

    viewer.getContentGroup().getChildren().add(meshView);
  }
}
