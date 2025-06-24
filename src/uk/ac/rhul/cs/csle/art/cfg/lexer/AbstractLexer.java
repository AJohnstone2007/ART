package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public abstract class AbstractLexer {
  public CFGRules cfgRules;
  public String inputString = ""; // Original input string
  public TWESetElement[][] tweSlices;

  // Internal research fields
  public Integer deleteTokenCount = 0;
  public Integer swapTokenCount = 0;

  protected void lexicalError(String msg, int inputIndex) {
    Util.error(Util.echo(msg, inputIndex, inputString));
  }

  public abstract void lex(String input, CFGRules cfgRules, ChooseRules chooseRules);

  public abstract void statistics(Statistics currentstatistics);

  public void printTWESet(PrintStream outputStream, TermTraverserText outputTraverser) {
    if (tweSlices == null) {
      Util.warning("Empty TWE set");
      return;
    }
    for (int i = 0; i < tweSlices.length; i++) {
      if (tweSlices[i] == null) continue;
      outputStream.print("Index " + i + ":");
      for (int j = 0; j < tweSlices[i].length; j++)
        outputStream.print("  " + tweSlices[i][j]);
      outputStream.println();
    }
  }

  public String lexeme(int index) { // Used by deteministic parsers which only access the first element in a slice
    return lexeme(index, 0);
  }

  public String lexeme(int index, int offset) {
    return lexeme(tweSlices[index][offset]);
  }

  public String lexeme(TWESetElement element) {
    String full = inputString.substring(element.lexemeStart, element.lexemeEnd);

    if (element.cfgElement.cfgKind == CFGKind.B) {
      switch (element.cfgElement.str) {
      case "STRING_DQ", "STRING_SQ", "STRING_PLAIN_SQ", "STRING_DOLLAR":
        return full.substring(1, full.length() - 1);
      case "CHAR":
        return full.substring(1);
      }
    }
    return full;
  }

  public void chooseDefault() {
    if (tweSlices[0] == null) {
      Util.error("Empty tweSet");
      return;
    }
    for (int i = 0; i < tweSlices.length; i++) {
      var slice = tweSlices[i];
      if (slice != null) for (int ei = 0; ei < slice.length; ei++)
        for (int fi = 0; fi < slice.length; fi++) {
          var e = slice[ei];
          var f = slice[fi];
          if ((e.rightExtent > f.rightExtent) || (e.rightExtent == f.rightExtent && e.cfgElement.cfgKind != CFGKind.B && f.cfgElement.cfgKind == CFGKind.B))
            f.suppressed = true;
        }
    }
  }

  public void suppressDeadPaths() {
    // // Util.debug("TWE dead path suppression");
    // int inDegree[] = new int[slices.size() + 1];
    // int outDegree[] = new int[slices.size() + 1];
    // for (int i = 0; i < slices.size(); i++)
    // if (slices.get(i) != null) for (var e : slices.get(i))
    // if (!e.suppressed) {
    // outDegree[i]++;
    // inDegree[e.rightExtent]++;
    // }
    //
    // for (int i = slices.size() - 3; i >= 0; i--)
    // if (outDegree[i] != 0) {
    // for (var e : slices.get(i))
    // if (!e.suppressed && outDegree[e.rightExtent] == 0) {
    // e.suppressed = true;
    // outDegree[i]--;
    // inDegree[e.rightExtent]--;
    // }
    // }
    //
    // for (int i = 1; i < slices.size(); i++) {
    // if (outDegree[i] != 0) {
    // for (var e : slices.get(i))
    // if (!e.suppressed && inDegree[i] == 0) {
    // e.suppressed = true;
    // outDegree[i]--;
    // inDegree[e.rightExtent]--;
    // }
    // }
    // }
  }

  public void removeSuppressedTWE() {
    for (int i = 0; i < tweSlices.length; i++)
      if (tweSlices[i] != null && tweSlices[i].length > 1) {
        var oldSlice = tweSlices[i];
        int suppressedCount = 0;
        for (int ei = 0; ei < oldSlice.length; ei++)
          if (oldSlice[ei].suppressed) suppressedCount++;
        if (suppressedCount > 0) {
          tweSlices[i] = new TWESetElement[oldSlice.length - suppressedCount];

          int newSliceIndex = 0;
          for (int ei = 0; ei < oldSlice.length; ei++)
            if (!oldSlice[ei].suppressed) tweSlices[i][newSliceIndex++] = oldSlice[ei];
        }
      }
  }
}