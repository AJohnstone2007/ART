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
    Util.fatal(Util.echo(msg, inputIndex, inputString));
  }

  public abstract void lex(String input, CFGRules cfgRules, ChooseRules chooseRules);

  public abstract void statistics(Statistics currentstatistics);

  public void printTWESet(PrintStream outputStream, TermTraverserText outputTraverser) {
    for (int i = 0; i < tweSlices.length; i++) {
      outputStream.print("Index " + i + ":");
      for (int j = 0; j < tweSlices[i].length; j++)
        outputStream.print("  " + tweSlices[i][j]);
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
}