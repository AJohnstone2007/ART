package uk.ac.rhul.cs.csle.art.util.lexicalisations;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.TWESetElement;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class TWESet extends AbstractLexicalisations {
  public TWESet(String inputString, CFGRules cfgRules) {
    super();
    this.cfgRules = cfgRules;
    this.inputString = inputString;
    tweSlices = new TWESetElement[inputString.length()][];
  }

  public TWESetElement[][] tweSlices;

  @Override
  public TWESetElement[] getSlice(int n) {
    return tweSlices[n];
  }

  @Override
  public void putSlice(int n, TWESetElement[] slice) {
    tweSlices[n] = slice;
  }

  @Override
  public long cardinality() {
    long ret = 0;
    for (var i : tweSlices)
      if (i != null) ret += i.length;
    return ret;
  }

  @Override
  public boolean isDeterministic() {
    if (tweSlices == null) {
      Util.error("AbstractLexer.isDeterministic() called on null TWE set");
      return false;
    }

    for (var ts : tweSlices) {
      if (ts == null) continue;
      int unsuppressedElementCount = 0; // Could use boolean for speed but code looks tricksy so do this for perspicaciousness
      for (var te : ts) {
        if (!te.suppressed) unsuppressedElementCount++;
        if (unsuppressedElementCount > 1) {
          // Util.debug("nonDeterministic TWE slice: ");
          // for (var pe : ts)
          // System.out.println(pe);
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    if (tweSlices == null) {
      outputStream.println("Empty TWE set");
      return;
    }

    for (int i = 0; i < tweSlices.length; i++) {
      if (tweSlices[i] == null) continue;
      for (int j = 0; j < tweSlices[i].length; j++)
        outputStream.println("Index " + i + ":  " + tweSlices[i][j] + "  " + lexeme(tweSlices[i][j]));
    }
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    // TODO Auto-generated method stub

  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
    // TODO Auto-generated method stub
  }

  /* Search tweSlices[leftExtent] for a TWE set elementthat matchs grammarNode */
  @Override
  public String lexeme(CFGElement cfgElement, int leftExtent, int rightExtent) {

    if (tweSlices[leftExtent] == null) Util.fatal("internal error - lexeme(" + cfgElement + ", " + leftExtent + ", " + rightExtent + ") references null slice");
    for (var te : tweSlices[leftExtent])
      if (!te.suppressed && te.cfgElement.cfgKind == cfgElement.cfgKind && te.rightExtent == rightExtent) return lexeme(te);

    Util.fatal("internal error - lexeme(" + cfgElement + ", " + leftExtent + ", " + rightExtent + ") not found");
    return "??? Unmatched lexeme kind";
  }

  @Override
  public String lexeme(TWESetElement element) {
    String full = inputString.substring(element.leftExtent, element.lexemeEnd);

    if (element.cfgElement.cfgKind == CFGElementKind.TRM_BI) {
      switch (element.cfgElement.str) {
      case "STRING_SHRIEK_SHRIEK":
        return full.substring(2, full.length() - 2);
      case "STRING_DQ", "STRING_SQ", "STRING_PLAIN_SQ", "STRING_DOLLAR", "STRING_BRACE":
        return full.substring(1, full.length() - 1);
      case "CHAR_BQ", "AP_INTEGER", "AP_REAL":
        return full.substring(1);
      }
    }
    return full;
  }

  @Override
  public void choose(ChooseRules chooseRules) {
    if (tweSlices[0] == null) {
      Util.error("lexer.choose() - empty tweSet");
      return;
    }

    for (int sliceIndex = 0; sliceIndex < tweSlices.length; sliceIndex++) {
      var slice = tweSlices[sliceIndex];
      if (slice != null) for (int rightTWEIndex = 0; rightTWEIndex < tweSlices[sliceIndex].length; rightTWEIndex++) { // The right element is suppressable
        var rightTWE = tweSlices[sliceIndex][rightTWEIndex];
        if (rightTWE.suppressed) continue; // Already suppressed so nothing to do
        var rightElement = rightTWE.cfgElement;
        var rightKind = rightElement.cfgKind;
        if (rightKind == CFGElementKind.SOS) continue; // do not suppress the start of string token

        for (int leftTWEIndex = 0; leftTWEIndex < tweSlices[sliceIndex].length; leftTWEIndex++) {
          if (leftTWEIndex == rightTWEIndex) continue; // Do not self-compare
          var leftTWE = tweSlices[sliceIndex][leftTWEIndex];
          var leftElement = leftTWE.cfgElement;
          var leftKind = leftElement.cfgKind;

          // Util.debug("AbstractLexer.choose() at slice " + sliceIndex + " comparing " + leftTWE + " to " + rightTWE);

          // All set - now check the relations
          if ((chooseRules.testLonger(leftElement, rightElement) && leftTWE.rightExtent > rightTWE.rightExtent)
              || (chooseRules.testShorter(leftElement, rightElement) && leftTWE.rightExtent < rightTWE.rightExtent)
              || (chooseRules.testHigher(leftElement, rightElement) && leftTWE.rightExtent == rightTWE.rightExtent)) {
            rightTWE.suppressed = true;

            // Util.debug("Suppressed " + rightTWE);
          }
        }
      }
    }
  }

  @Override
  public void chooseDefault() {
    // Util.debug("Running default lexical chooser");
    if (tweSlices[0] == null) {
      Util.error("lexer.chooseDefault() - empty tweSet");
      return;
    }

    Util.warning("Running default lexical disambiguation");

    for (int i = 0; i < tweSlices.length; i++) {
      var slice = tweSlices[i];
      if (slice != null) for (int ee = 0; ee < tweSlices[i].length; ee++)
        for (int ff = 0; ff < tweSlices[i].length; ff++) {
          if (ee == ff) continue; // Do not self-compare
          var f = tweSlices[i][ff];
          if (f.cfgElement.cfgKind == CFGElementKind.SOS) continue; // do not suppress the start of string token

          var e = tweSlices[i][ee];
          if (e.rightExtent > f.rightExtent)
            f.suppressed = true;
          else if (e.rightExtent == f.rightExtent && f.cfgElement.cfgKind == CFGElementKind.TRM_BI) {
            // Util.debug("TWE pair " + e + " and " + f + " - suppressing " + f);
            f.suppressed = true;
          }
        }
    }
  }

  @Override
  public void suppressDeadPaths() {
    // // Util.debug("TWE dead path suppression");
    // int inDegree[] = new int[tweSlices.length + 1];
    // int outDegree[] = new int[tweSlices.length + 1];
    // for (int i = 0; i < tweSlices.length; i++)
    // if (tweSlices[i] != null) for (var e : tweSlices[i])
    // if (!e.suppressed) {
    // outDegree[i]++;
    // inDegree[e.rightExtent]++;
    // }
    //
    // for (int i = tweSlices.length - 3; i >= 0; i--)
    // if (outDegree[i] != 0) {
    // for (var e : tweSlices[i])
    // if (!e.suppressed && outDegree[e.rightExtent] == 0) {
    // e.suppressed = true;
    // outDegree[i]--;
    // inDegree[e.rightExtent]--;
    // }
    // }
    //
    // for (int i = 1; i < tweSlices.length; i++) {
    // if (outDegree[i] != 0) {
    // for (var e : tweSlices[i])
    // if (!e.suppressed && inDegree[i] == 0) {
    // e.suppressed = true;
    // outDegree[i]--;
    // inDegree[e.rightExtent]--;
    // }
    // }
    // }
  }

  /*
   * Work through the current TWEset, counting thnumber of suppressed elements in each slice. If this is >0, make a new replacement slice and copy the
   * unsuppressed elements into it. Return the cardinality of the TWE set.
   */
  @Override
  public int removeSuppressedTWE() {
    int newCardinality = 0;
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
        newCardinality += tweSlices[i].length;
      }
    return newCardinality;
  }

  @Override
  public boolean valid() {
    return tweSlices[inputString.length() - 1] != null; // If EOS slice is missing, then lexer gace up early
  }
}
