package uk.ac.rhul.cs.csle.art.util.derivations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.util.Util;

class SPPFDump {
  PrintStream dumpOut = null;
  private final SPPF sppf;

  public SPPFDump(SPPF sppf, String filename) {
    this.sppf = sppf;
    if (sppf.root == null) Util.warning("Missing SPPF root node - continuing with dump");

    try {
      dumpOut = new PrintStream(new File(filename));
      for (SPPFSymbolNode n : sppf.nodes.keySet()) {
        dumpOut.println(n.grammarNode.toStringAsProduction() + " " + n.leftExtent + "," + n.rightExtent);

        for (SPPFPackedNode p : n.packNodes)
          dumpOut.println(
              n.grammarNode.toStringAsProduction() + " " + n.leftExtent + "," + n.rightExtent + "  " + p.grammarNode.toStringAsProduction() + " " + p.pivot);
      }
    } catch (FileNotFoundException e) {
      Util.error("Unable to write SPPF dump to " + filename);
    }
  }
}
