package uk.ac.rhul.cs.csle.art.util.stacks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.util.Util;

public class GSS2Dot {
  public GSS2Dot(Map<GSSNode, GSSNode> gss, String filename) {
    if (gss == null) return;
    PrintStream gssOut;
    try {
      gssOut = new PrintStream(new File(filename));
      gssOut.println("digraph \"GSS\" {\n" + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04  color=gray]\n"
          + "graph[ordering=out ranksep=0.1]\n" + "edge[arrowsize = 0.3  color=gray]");

      if (gss != null) for (GSSNode s : gss.keySet()) {
        gssOut.println("\"" + s + "\" [label=\"" + s.grammarNode.toStringAsProduction() + "\n" + s.inputIndex + "\"]");
        for (GSSEdge c : s.edges) // iterate over children
          gssOut.println("\"" + s + "\"->\"" + c.dst + "\"");
      }
      gssOut.println("}");
      gssOut.close();
    } catch (FileNotFoundException e) {
      Util.error("Unable to write GSS visualisation to " + filename);
    }
  }
}
