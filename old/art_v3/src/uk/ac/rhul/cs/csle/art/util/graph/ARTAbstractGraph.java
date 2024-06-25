package uk.ac.rhul.cs.csle.art.util.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.util.ARTException;

public abstract class ARTAbstractGraph {
  protected Object label;

  public ARTAbstractGraph(Object label) {
    this.label = label;
  }

  public Object getLabel() {
    return label;
  }

  public void setLabel(Object label) {
    this.label = label;
  }

  public void printDot(String filename) throws ARTException, FileNotFoundException {
    printDot(new File(filename));
  }

  public void printDot(File file) throws ARTException, FileNotFoundException {
    printDot(new PrintWriter(file));
  }

  protected abstract void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter);

  public void printDot(PrintWriter printWriter) {
    printWriter.println("digraph \"" + label + "\"");
    printWriter.println("{");
    printWriter.println("graph[ordering=out]");
    printWriter.println("node[fontname=Helvetica fontsize=9 shape=box style=rounded height = 0 width = 0 margin= 0.04]");
    printWriter.println("edge[arrowsize = 0.3]");
    printDotBody(this, printWriter);
    printWriter.print("\n}");
    printWriter.close();
  }

}
