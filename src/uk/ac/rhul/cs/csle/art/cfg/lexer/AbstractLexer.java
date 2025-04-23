package uk.ac.rhul.cs.csle.art.cfg.lexer;

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

  public abstract void lex(String input, CFGRules cfgRules2);

  public abstract TWESet getTWESet();

  public abstract String lexeme(int l);

  public abstract void printLexicalisations(boolean raw);

  // This is only needed by SPPF.java and can probably be reworked as lexeme()
  public abstract String lexemeOfBuiltin(TokenKind kind, int startIndex);

  public abstract void statistics(Statistics currentstatistics);

  public abstract int[] getTokens();

  // This is only needed for error echoing and SPPF - change to tokenIndexToStringIndex - in TWE set the indices are the string indices so notneeded
  public abstract int[] getLeftIndices();

  protected void lexicalError(String msg) {
    Util.fatal(Util.echo(msg, lexerInputIndex, inputString));
  }
}
