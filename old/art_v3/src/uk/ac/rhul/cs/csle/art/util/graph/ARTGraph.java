package uk.ac.rhul.cs.csle.art.util.graph;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ARTGraph extends ARTAbstractGraph {
  public ARTGraph(Object label) {
    super(label);
  }

  protected final Map<Object, ARTGraphVertex> keyMap = new HashMap<Object, ARTGraphVertex>();

  public ARTGraphVertex addVertex(Object key, Object label) {
    ARTGraphVertex vertex = new ARTGraphVertex(key, label);
    keyMap.put(key, vertex);
    return vertex;
  }

  public ARTGraphVertex lookupVertex(Object key) {
    return keyMap.get(key);
  }

  public ARTGraphVertex findVertex(Object key, Object label) {
    ARTGraphVertex vertex = keyMap.get(key);
    if (vertex == null) vertex = addVertex(key, label);
    return vertex;
  }

  public ARTAbstractEdge addEdge(ARTGraphVertex parent, ARTGraphVertex child, Object label) {
    return new ARTGraphEdge(parent, child, label);
  }

  public ARTAbstractEdge addEdge(ARTGraphVertex parent, ARTGraphVertex child) {
    return addEdge(parent, child, null);
  }

  public ArrayList<ARTGraphVertex> findRoots() {
    ArrayList<ARTGraphVertex> roots = new ArrayList<ARTGraphVertex>();

    for (ARTGraphVertex v : keyMap.values())
      if (v.getInEdges().isEmpty()) roots.add((v));
    return roots;
  }

  @Override
  public String toString() {
    String ret = "Graph " + label;

    for (ARTGraphVertex v : keyMap.values()) {
      ret += "\n" + v.getPayload();

      for (ARTAbstractEdge e : v.getOutEdges())
        ret += "-> " + e.getChild().getPayload();
    }
    return ret;
  }

  public Map<Object, ARTGraphVertex> getKeyMap() {
    return keyMap;
  }

  @Override
  public void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter) {
    for (ARTGraphVertex vertex : keyMap.values()) {
      vertex.printDot(graph, printWriter);
      for (ARTGraphEdge edge : vertex.getOutEdges())
        edge.printDot(graph, printWriter);
    }
  }

}
