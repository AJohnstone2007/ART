package uk.ac.rhul.cs.csle.art.util.graph;

import java.io.PrintWriter;

public class ARTAbstractEdge {

  protected ARTGraphVertex parent;
  protected ARTGraphVertex child;

  public ARTAbstractEdge(ARTGraphVertex parent, ARTGraphVertex child) {
    this.parent = parent;
    this.child = child;
  }

  public void remove() {
    parent.removeOutEdge(this);
    child.removeInEdge(this);
  }

  public ARTGraphVertex getParent() {
    return parent;
  }

  public ARTGraphVertex getChild() {
    return child;
  }

  public void printDot(ARTAbstractGraph graph, PrintWriter printWriter) {
    printWriter.print("\n\"" + parent + "\"->\"" + child + "\";");
  }

}