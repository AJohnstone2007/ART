package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public abstract class AbstractLexer {
  public String inputString = ""; // Original input string
  protected char[] inputAsCharArray;
  protected int lexerInputIndex;
  public CFGRules cfgRules;
  public Integer deleteTokenCount = 0;
  public Integer swapTokenCount = 0;
  public TWESet tweSet = new TWESet("dummy");
  public ArrayList<TWESetElement> firstLexicalisation;

  public abstract void lex(String input, CFGRules cfgRules);

  public String lexeme(int i) {
    return lexeme(firstLexicalisation.get(i));
  }

  public String lexeme(TWESetElement element) {
    String full = inputString.substring(element.leftExtent, element.lexemeEnd);

    if (element.element.cfgKind == CFGKind.B) {
      switch (element.element.str) {
      case "STRING_DQ", "STRING_SQ", "STRING_PLAIN_SQ", "STRING_DOLLAR":
        return full.substring(1, full.length() - 1);
      case "CHAR":
        return full.substring(1);
      }
    }
    return full;
  }

  public abstract void loadFirstLexicalisation();

  public abstract void printLexicalisations(boolean raw);

  public abstract void statistics(Statistics currentstatistics);

  protected void lexicalError(String msg) {
    Util.fatal(Util.echo(msg, lexerInputIndex, inputString));
  }

  // // This is only needed by SPPF.java and can probably be reworked as lexeme()
  public abstract String lexemeOfBuiltin(TokenKind kind, int startIndex);
}