package uk.ac.rhul.cs.csle.art.util.graph;

public class ARTGraphEdge extends ARTAbstractEdge {
  Object payload;

  public ARTGraphEdge(ARTGraphVertex parent, ARTGraphVertex child, Object payload) {
    super(parent, child);
    this.parent = parent;
    this.child = child;
    this.payload = payload;

    parent.addOutEdge(this);
    child.addInEdge(this);
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }
}
