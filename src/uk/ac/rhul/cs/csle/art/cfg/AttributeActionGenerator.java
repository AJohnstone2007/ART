package uk.ac.rhul.cs.csle.art.cfg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.Util;

public class AttributeActionGenerator {

  public AttributeActionGenerator(CFGRules cfgRules) {
    System.out.println("Generating Attribute Action system");
    PrintWriter text = null;
    try {
      text = new PrintWriter(new File("ARTAttributeAction.java"));
    } catch (FileNotFoundException e) {
      Util.fatal("Unable to open output file ARTAttributeAction.java");
    }

    text.println("public class ARTAttributeAction {");

    for (var e : cfgRules.elements.keySet())
      if (e.kind == CFGKind.N) {
        text.println("\n  public static class " + e.str + " {");
        for (var a : e.attributes)
          text.println("    int " + a + ";");
        text.println("  }");
      }

    text.println("}");

    text.close();
  }

}
