package uk.ac.rhul.cs.csle.art.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;

public class GSS2Dot {
  public GSS2Dot(Map<GSSN, GSSN> gss, String filename) {
    if (gss == null) return;
    PrintStream gssOut;
    try {
      gssOut = new PrintStream(new File(filename));
      gssOut.println("digraph \"GSS\" {\n" + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04  color=gray]\n"
          + "graph[ordering=out ranksep=0.1]\n" + "edge[arrowsize = 0.3  color=gray]");

      if (gss != null) for (GSSN s : gss.keySet()) {
        gssOut.println("\"" + s + "\" [label=\"" + s.gn.toStringAsProduction() + "\n" + s.i + "\"]");
        for (GSSE c : s.edges) // iterate over children
          gssOut.println("\"" + s + "\"->\"" + c.dst + "\"");
      }
      gssOut.println("}");
      gssOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write GSS visualisation to " + filename);
    }
  }
}
