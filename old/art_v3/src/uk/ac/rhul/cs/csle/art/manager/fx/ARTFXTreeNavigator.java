package uk.ac.rhul.cs.csle.art.manager.fx;

import java.util.HashMap;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTree;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTreeVertex;

class ARTFXTreeNavigator {
  final Canvas canvas;
  final double xSeparation = 10; // horizontal separation between nodes
  final double ySeparation = 20; // vertical separation between nodes
  final double ySeparationHalf = ySeparation / 2; // vertical spacing (not separation) of baselines
  final double frame = 2; // spavce to leave between a label and the node border
  final double frame2 = 2 * frame; // a convenience constant in case our Java compiler is not very good at common subexpression elimination
  final double radius = 5; // The curve radius for nodes with rounded corners
  final double textBaseLine = new Text("X").getBoundsInLocal().getHeight() - 4;

  int xMax, yMax;
  HashMap<Integer, Double> xExtents = new HashMap<Integer, Double>();
  HashMap<Integer, Double> yExtents = new HashMap<Integer, Double>();
  private final ARTTree tree;

  private double vertexWidth(ARTTreeVertex vertex) {
    return new Text(vertex.toString()).getBoundsInLocal().getWidth() + frame2;
  }

  private void paintNode(ARTTreeVertex vertex, GraphicsContext gc, int x, int y, boolean isNotLeaf, boolean isFirst) {
    double xx = xExtents.get(x);
    double yy = yExtents.get(y);
    double width = xExtents.get(x + 1) - xx - xSeparation + frame2;
    double height = yExtents.get(y + 1) - yy - ySeparation + frame2;

    gc.strokeRoundRect(xx, yy, width, height, radius, radius);
    gc.fillText(vertex.toString(), frame + xx, yy + textBaseLine); // 5 is a fudge factor
    gc.strokeLine(xx + (isFirst ? 5 : 10), yy, xx + 5, yy - ySeparationHalf + 1);
    if (isNotLeaf) gc.strokeLine(xx + 5, yy + height, xx + 5, yy + height + ySeparationHalf);
    // System.out.printf("painted vertex %d labelled '%s' at x=%d, y=%d of width %f\n", vertex.getKey(), vertex.toString(), x, y, vertexWidth(vertex));
  }

  private void visitTreeComputeExtents(ARTTreeVertex vertex, int x, int y) {
    // System.out.printf("vistTreeExtents at vertex %d, x=%d, y=%d\n", vertex.getKey(), x, y);
    if (x > xMax) xMax = x;
    if (y > yMax) yMax = y;
    Bounds bounds = new Text(vertex.toString()).getBoundsInLocal();

    double width = bounds.getWidth();
    double height = bounds.getHeight();
    if (xExtents.get(x) == null || width > xExtents.get(x)) xExtents.put(x, width);
    if (yExtents.get(y) == null || height > yExtents.get(y)) yExtents.put(y, height);
    if (vertex.getChild() != null) visitTreeComputeExtents(vertex.getChild(), x, y + 1);
    if (vertex.getSibling() != null) visitTreeComputeExtents(vertex.getSibling(), xMax + 1, y);
  }

  private void visitTreePaint(ARTTreeVertex vertex, GraphicsContext gc, int xParent, int x, int y, boolean isFirst) {
    // System.out.printf("vistTreePaint at vertex %d, xparent=%d, x=%d, y=%d\n", vertex.getKey(), xParent, x, y);
    if (x > xMax) xMax = x;
    if (y > yMax) yMax = y;
    // Scan chidren's extents
    int childCount = 0;
    for (ARTTreeVertex tmp = vertex.getChild(); tmp != null; tmp = tmp.getSibling())
      childCount++;

    boolean isNotLeaf = childCount != 0;
    if (childCount == 0) // zero and one child are indistinguishable
      childCount = 1;

    paintNode(vertex, gc, x, y, isNotLeaf, isFirst);

    if (vertex.getChild() != null) visitTreePaint(vertex.getChild(), gc, x, x, y + 1, true);
    if (vertex.getSibling() != null)
      visitTreePaint(vertex.getSibling(), gc, xParent, xMax + 1, y, false);
    else // at last child, so connect all across to parent
    if (xParent != x && x != 0)
      gc.strokeLine(xExtents.get(xParent) + 5, yExtents.get(y) - ySeparationHalf, xExtents.get(x) + 5, yExtents.get(y) - ySeparationHalf);
  }

  ARTFXTreeNavigator(ARTTree tree) {
    this.tree = tree;

    // 1 Compute extents
    xMax = yMax = 0;
    visitTreeComputeExtents(tree.getRoot(), 0, 0);

    // 2 Convert individual extents to cumulative coordinates
    double offset = 0;
    for (int i = 0; i <= xMax; i++) {
      Double tmp = xExtents.get(i);
      xExtents.put(i, offset);
      offset += tmp + xSeparation;
    }
    xExtents.put(xMax + 1, offset);
    // System.out.printf("xExtents[%d] = %f\n", xMax + 1, offset);

    offset = 0;
    for (int i = 0; i <= yMax; i++) {
      Double tmp = yExtents.get(i);
      yExtents.put(i, offset);
      offset += tmp + ySeparation;
    }
    yExtents.put(yMax + 1, offset);
    // System.out.printf("yExtents[%d] = %f\n", yMax + 1, offset);

    // 3 Construct UI elements
    canvas = new Canvas(xExtents.get(xMax + 1), yExtents.get(yMax + 1));
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setStroke(Color.GRAY);

    ScrollPane scrollPane = new ScrollPane(canvas);
    scrollPane.setPannable(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setFitToWidth(true);

    // 4 Paint canvas
    xMax = yMax = 0;
    visitTreePaint(tree.getRoot(), gc, -1, 0, 0, false);
  }

  public Canvas getCanvas() {
    return canvas;
  }
}