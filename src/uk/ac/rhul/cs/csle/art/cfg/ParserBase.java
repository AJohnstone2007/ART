package uk.ac.rhul.cs.csle.art.cfg;

import uk.ac.rhul.cs.csle.art.cfg.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarNode;
import uk.ac.rhul.cs.csle.art.cfg.grammar.LKind;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeGrammarKind;

public abstract class ParserBase {
  public int traceLevel = 0;
  public Grammar grammar;
  public String inputString = "";
  public String inputStringName = "";
  public int[] input;
  public int[] positions;
  protected int i;
  public int rightmostParseIndex;
  public boolean suppressEcho = false;

  public ARTModeGrammarKind artGrammarKind = ARTModeGrammarKind.UNKNOWN;
  public boolean inadmissable; // set when, say, a BNF parser is called with an EBNF grammar
  public boolean inLanguage;
  private long startTime;
  private long setupTime;
  private long lexTime;
  private long lexChooseTime;
  private long parseTime;
  private long parseChooseTime;
  private long termGenerateTime;
  private long semanticsTime;
  private long artStartMemory;
  private long artEndMemory;
  private long artEndPoolAllocated;

  public void loadSetupTime() {
    setupTime = System.nanoTime();
  }

  public void loadLexTime() {
    lexTime = System.nanoTime();
  }

  public void loadLexChooseTime() {
    lexChooseTime = System.nanoTime();
  }

  public void loadParseTime() {
    parseTime = System.nanoTime();
  }

  public void loadParseChooseTime() {
    parseChooseTime = System.nanoTime();
  }

  public void loadTermGenerateTime() {
    termGenerateTime = System.nanoTime();
  }

  public void loadSemanticsTime() {
    semanticsTime = System.nanoTime();
  }

  public void loadStartMemory() {
    artStartMemory = memoryUsed();
  }

  public void loadEndMemory() {
    artEndMemory = memoryUsed();
  }

  public void loadEndPoolAllocated(long m) {
    artEndPoolAllocated = m;
  }

  private void normaliseTimes() {
    if (setupTime < startTime) setupTime = startTime;
    if (lexTime < setupTime) lexTime = setupTime;
    if (lexChooseTime < lexTime) lexChooseTime = lexTime;
    if (parseTime < lexChooseTime) parseTime = lexChooseTime;
    if (parseChooseTime < parseTime) parseChooseTime = parseTime;
    if (termGenerateTime < parseChooseTime) termGenerateTime = parseChooseTime;
    if (semanticsTime < termGenerateTime) semanticsTime = termGenerateTime;
  }

  private String timeAsMilliseconds(long startTime, long stopTime) {
    return String.format("%.3f", (stopTime - startTime) * 1E-6);
  }

  private String artGetTimes() {
    return timeAsMilliseconds(startTime, setupTime) + "," + timeAsMilliseconds(setupTime, lexTime) + "," + timeAsMilliseconds(lexTime, lexChooseTime) + ","
        + timeAsMilliseconds(lexChooseTime, parseTime) + "," + timeAsMilliseconds(parseTime, parseChooseTime) + ","
        + timeAsMilliseconds(parseChooseTime, termGenerateTime) + "," + timeAsMilliseconds(termGenerateTime, semanticsTime);
  }

  private String getSPPFCounts() {
    return "GSS SN,GSS EN,GGS E,SPPF Eps,SPPF T,SPPF NT,SPPF Inter,SPPF PN,SPPF Edge,";
  }

  private String getPoolCounts() {
    return "Pool,H0,H1,H2,H3,H4,H5,H6+";
  }

  public long memoryUsed() {
    System.gc();
    System.gc();
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  public void resetStats() {
    startTime = System.nanoTime();
    setupTime = lexTime = lexChooseTime = parseTime = parseChooseTime = termGenerateTime = semanticsTime = artStartMemory = artEndMemory = artEndPoolAllocated = 0;
  }

  public String statisticsToString() {
    normaliseTimes();
    return inputString.length() + "," + getClass().getSimpleName() + "," + (inLanguage ? "accept" : "reject") + ",OK," + artGetTimes() + "," + input.length
        + "," + (input.length - 1) + ",1," + getSPPFCounts() + getPoolCounts();
  }

  protected String lexemeOfBuiltin(LKind kind, int inputIndex) {
    switch (kind) {
    case ID: {
      int right = inputIndex;
      while (right < inputString.length()
          && (Character.isAlphabetic(inputString.charAt(right)) || Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;

      return inputString.substring(inputIndex, right);
    }
    case CHARACTER:
      return inputString.substring(inputIndex + 1, inputIndex + 2);
    case CHAR_BQ:
      return inputString.substring(inputIndex + 1, inputIndex + 2);
    case COMMENT_BLOCK_C:
      break;
    case COMMENT_LINE_C:
      break;
    case COMMENT_NEST_ART:
      break;
    case INTEGER: {
      int right = inputIndex;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(inputIndex, right);
    }
    case REAL: {
      int right = inputIndex;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(inputIndex, right);
    }
    case SIGNED_INTEGER: {
      int right = inputIndex;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(inputIndex, right);
    }
    case SIGNED_REAL: {
      int right = inputIndex;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(inputIndex, right);
    }
    case SIMPLE_WHITESPACE:
      break;
    case SINGLETON_CASE_INSENSITIVE:
      break;
    case SINGLETON_CASE_SENSITIVE:
      break;
    case STRING_PLAIN_SQ: {
      int right = inputIndex + 1;
      while (inputString.charAt(right) != '\'')
        right++;
      return inputString.substring(inputIndex + 1, right);
    }
    case STRING_DQ: {
      int right = inputIndex + 1;
      while (inputString.charAt(right) != '\"')
        right++;
      return inputString.substring(inputIndex + 1, right);
    }
    case STRING_BRACE_NEST:
      break;
    case STRING_BRACKET_NEST:
      break;
    case STRING_DOLLAR:
      break;
    case STRING_SQ: {
      int right = inputIndex + 1;
      while (inputString.charAt(right) != '\'')
        right++;
      return inputString.substring(inputIndex + 1, right);
    }
    }
    return "???";
  }

  protected boolean match(GrammarNode gn) {
    return input[i] == gn.elm.ei;
  }

  public void parse() {
    System.out.println("parse() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void show() {
    System.out.println("show() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void chooseLongestMatch() {
    System.out.println("chooseLongestMatch() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public int derivationAsTerm() {
    System.out.println("derivationAsTerm() not implemented for parser class " + this.getClass().getSimpleName());
    return 0;
  }

  protected GrammarNode getLHS(GrammarNode gn) {
    return grammar.rules.get(gn.elm);
  }

  protected void trace(int level, String msg) {
    if (level <= traceLevel) System.out.println(msg);
  }
}
