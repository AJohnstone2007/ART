package uk.ac.rhul.cs.csle.art.cfg;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexemeKind;
import uk.ac.rhul.cs.csle.art.script.TraversalKind;
import uk.ac.rhul.cs.csle.art.util.Util;

public abstract class AbstractParser {
  public int traceLevel = 0;
  public CFGRules cfgRules;
  public String inputString = ""; // Original input string
  public String inputStringName = "";
  public int[] input; // Input as array of tokens
  public int[] positions; // Mapping from start of token's lexeme to index into the string
  protected int i; // Current input index
  public int[] oracle;

  public boolean inLanguage;

  protected String lexemeOfBuiltin(LexemeKind kind, int inputIndex) {
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
    case SIGNED_INTEGER: {
      int right = inputIndex;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(inputIndex, right);
    }
    case AP_INTEGER: {
      int right = inputIndex + 1; // skip £
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(inputIndex + 1, right);
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
    case AP_REAL: {
      int right = inputIndex + 1;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(inputIndex + 1, right);
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
    case STRING_BRACE_NEST: {
      int level = 1, right = inputIndex + 1;
      while (level != 0) {
        if (inputString.charAt(right) == '{')
          level++;
        else if (inputString.charAt(right) == '}') level--;
        right++;
      }
      return inputString.substring(inputIndex + 1, right);
    }
    case STRING_BRACKET_NEST: {
      int level = 1, right = inputIndex + 1;
      while (level != 0) {
        if (inputString.charAt(right) == '<')
          level++;
        else if (inputString.charAt(right) == '>') level--;
        right++;
      }
      return inputString.substring(inputIndex + 1, right);
    }
    case STRING_DOLLAR: {
      int right = inputIndex + 1;
      while (inputString.charAt(right) != '$')
        right++;
      return inputString.substring(inputIndex + 1, right);
    }
    case STRING_SHRIEK_SHRIEK: {
      int right = inputIndex + 2;
      while (!(inputString.charAt(right) == '!' && inputString.charAt(right + 1) == '!'))
        right++;
      return inputString.substring(inputIndex + 2, right);
    }
    case STRING_SQ: {
      int right = inputIndex + 1;
      while (inputString.charAt(right) != '\'')
        right++;
      return inputString.substring(inputIndex + 1, right);
    }
    }
    return "unprocessed lexeme";
  }

  protected boolean match(CFGNode gn) {
    return input[i] == gn.elm.ei;
  }

  public void parse() {
    System.out.println("parse() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void chooseLongestMatch() {
    System.out.println("chooseLongestMatch() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public int derivationAsTerm() {
    System.out.println("derivationAsTerm() not implemented for parser class " + this.getClass().getSimpleName());
    return 0;
  }

  public void gss2Dot() {
    System.out.println("gss2Dot() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void gssPrint() {
    System.out.println("gssPrint() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void sppf2Dot() {
    System.out.println("sppf2Dot() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void sppfPrint() {
    System.out.println("sppfPrint() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void sppfPrintCyclicNodes() {
    System.out.println("sppfCyclicNodes() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void sppfBreakCycles(boolean trace, TraversalKind traversalKind, boolean lone, boolean sibling) {
    System.out.println("sppfBreakCycles() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void sppfBreakCyclesRelation() {
    System.out.println("sppfBreakCyclesRelation() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void sppfPrintSentences() {
    System.out.println("sppfPrintSentences() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void constructOracle() {
    System.out.println("interpretAttributeAction() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public CFGNode getLHS(CFGNode gn) {
    return cfgRules.elementToNodeMap.get(gn.elm);
  }

  protected void trace(int level, String msg) {
    if (level <= traceLevel) System.out.println(msg);
  }

  /* Statistics support for RunExp below this line ***************************/
  private long startTime, setupTime, lexTime, lexChooseTime, parseTime, parseChooseTime, termGenerateTime, semanticsTime;
  private long tweNodeCount, tweEdgeCount, tweLexCount;
  private long descriptorCount;
  private long gssNodeCount, gssEdgeCount, popCount;
  private long sppfEpsilonNodeCount, sppfTerminalNodeCount, sppfNonterminalNodeCount, sppfIntermediateNodeCount, sppfSymbolPlusIntermediateNodeCount,
      sppfPackNodeCount, sppfAmbiguityCount, sppfEdgeCount, sppfCyclicSCCCount;
  private long derivationNodeCount, derivationAmbiguityNodeCount;
  private long startMemory, endMemory;
  private long poolAllocated, h0, h1, h2, h3, h4, h5, h6more;

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

  public void loadTWECounts(long tweNodeCount, long tweEdgeCount, long tweLexCount) {
    this.tweNodeCount = tweNodeCount;
    this.tweEdgeCount = tweEdgeCount;
    this.tweLexCount = tweLexCount;
  }

  public void loadGSSCounts(long descriptorCount, long gssNodeCount, long gssEdgeCount, long popCount) {
    this.descriptorCount = descriptorCount;
    this.gssNodeCount = gssNodeCount;
    this.gssEdgeCount = gssEdgeCount;
    this.popCount = popCount;
  }

  public void loadSPPFCounts(long sppfEpsilonNodeCount, long sppfTerminalNodeCount, long sppfNonterminalNodeCount, long sppfIntermediateNodeCount,
      long sppfSymbolPlusIntermediateNodeCount, long sppfPackNodeCount, long sppfAmbiguityCount, long sppfEdgeCount) {
    this.sppfEpsilonNodeCount = sppfEpsilonNodeCount;
    this.sppfTerminalNodeCount = sppfTerminalNodeCount;
    this.sppfNonterminalNodeCount = sppfNonterminalNodeCount;
    this.sppfIntermediateNodeCount = sppfIntermediateNodeCount;
    this.sppfSymbolPlusIntermediateNodeCount = sppfSymbolPlusIntermediateNodeCount;
    this.sppfPackNodeCount = sppfPackNodeCount;
    this.sppfAmbiguityCount = sppfAmbiguityCount;
    this.sppfEdgeCount = sppfEdgeCount;
  }

  public void loadSPPFCyclicSCCCount(long sppfCyclicSCCCount) {
    this.sppfCyclicSCCCount = sppfCyclicSCCCount;
  }

  public void loadDerivationCounts(long derivationNodeCount, long derivationAmbiguityNodeCount) {
    this.derivationNodeCount = derivationNodeCount;
    this.derivationAmbiguityNodeCount = derivationAmbiguityNodeCount;
  }

  public void loadStartMemory() {
    // startMemory = memoryUsed();
  }

  public void loadEndMemory() {
    // endMemory = memoryUsed();
  }

  private long memoryUsed() {
    System.gc();
    System.gc();
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  public void loadPoolAllocated(long m) {
    poolAllocated = m;
  }

  public void loadHashCounts(long h0, long h1, long h2, long h3, long h4, long h5, long h6more) {
    this.h0 = h0;
    this.h1 = h1;
    this.h2 = h2;
    this.h3 = h3;
    this.h4 = h4;
    this.h5 = h5;
    this.h6more = h6more;
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

  private String getTWECounts() {
    return tweNodeCount + "," + tweEdgeCount + "," + tweLexCount;
  }

  private String getGSSCounts() {
    return descriptorCount + "," + gssNodeCount + "," + gssEdgeCount + "," + popCount;
  }

  private String getSPPFCounts() {
    return sppfEpsilonNodeCount + "," + sppfTerminalNodeCount + "," + sppfNonterminalNodeCount + "," + sppfIntermediateNodeCount + ","
        + sppfSymbolPlusIntermediateNodeCount + "," + sppfPackNodeCount + "," + sppfAmbiguityCount + "," + sppfEdgeCount + "," + sppfCyclicSCCCount;
  }

  private String getDerivationCounts() {
    return derivationNodeCount + "," + derivationAmbiguityNodeCount;
  }

  private String getPoolCounts() {
    return poolAllocated + "," + h0 + "," + h1 + "," + h2 + "," + h3 + "," + h4 + "," + h5 + "," + h6more;
  }

  public void resetStatistics() {
    startTime = System.nanoTime();
    setupTime = lexTime = lexChooseTime = parseTime = parseChooseTime = termGenerateTime = semanticsTime = 0;
    tweNodeCount = tweEdgeCount = tweLexCount = -1;
    descriptorCount = gssNodeCount = gssEdgeCount = popCount = -1;
    sppfEpsilonNodeCount = sppfTerminalNodeCount = sppfNonterminalNodeCount = sppfIntermediateNodeCount = sppfSymbolPlusIntermediateNodeCount = sppfPackNodeCount = sppfAmbiguityCount = sppfEdgeCount = sppfCyclicSCCCount = -1;
    startMemory = endMemory = poolAllocated = h0 = h1 = h2 = h3 = h4 = h5 = h6more = -1;
    derivationNodeCount = derivationAmbiguityNodeCount = -1;
  }

  public String statisticsToString() {
    normaliseTimes();
    return inputString.length() + "," + getClass().getSimpleName() + "," + (inLanguage ? "accept" : "reject") + ",OK," + artGetTimes() + "," + getTWECounts()
        + "," + getGSSCounts() + "," + getSPPFCounts() + "," + getDerivationCounts() + "," + (endMemory - startMemory) + "," + getPoolCounts();
  }

  /* Lexers **********************************************************************/
  protected LexemeKind[] kinds;
  protected String[] strings;
  protected LexemeKind[] whitespaces;

  protected char[] inputAsCharArray;
  protected int inputIndex, inputLength, leftIndex, longestMatchToken, longestMatchRightIndex, firstBuiltin;

  public void lex(String inputString, LexemeKind[] kinds, String[] strings, LexemeKind[] whitespaces) {
    System.out.println("lex() not implemented for parser class " + this.getClass().getSimpleName());
  }

  protected void processBuiltin(LexemeKind b, String s) {
    switch (b) {
    case CHARACTER:
      match_CHARACTER(s);
      break;
    case SINGLETON_CASE_INSENSITIVE:
      match_SINGLETON_CASE_INSENSITIVE(s);
      break;
    case SINGLETON_CASE_SENSITIVE:
      match_SINGLETON_CASE_SENSITIVE(s);
      break;
    case ID:
      match_ID();
      break;
    case INTEGER:
      match_INTEGER();
      break;
    case SIGNED_INTEGER:
      match_SIGNED_INTEGER();
      break;
    case AP_INTEGER:
      match_AP_INTEGER();
      break;
    case REAL:
      match_REAL();
      break;
    case AP_REAL:
      match_AP_REAL();
      break;
    case SIGNED_REAL:
      match_SIGNED_REAL();
      break;
    case CHAR_BQ:
      match_CHAR_BQ();
      break;
    case STRING_DQ:
      match_STRING_DQ();
      break;
    case STRING_SQ:
      match_STRING_SQ();
      break;
    case STRING_PLAIN_SQ:
      match_STRING_PLAIN_SQ();
      break;
    case STRING_BRACE_NEST:
      match_STRING_BRACE_NEST();
      break;
    case STRING_BRACKET_NEST:
      match_STRING_BRACKET_NEST();
      break;
    case STRING_DOLLAR:
      match_STRING_DOLLAR();
      break;
    case STRING_SHRIEK_SHRIEK:
      match_STRING_SHRIEK_SHREIK();
      break;
    case SIMPLE_WHITESPACE:
      match_SIMPLE_WHITESPACE();
      break;
    case COMMENT_BLOCK_C:
      match_COMMENT_BLOCK_C();
      break;
    case COMMENT_LINE_C:
      match_COMMENT_LINE_C();
      break;
    case COMMENT_NEST_ART:
      match_COMMENT_NEST_ART();
      break;
    default:
      Util.fatal("Unknown builtin " + b);
      break;
    }
  }

  protected void lexicalError(String msg) {
    Util.fatal(Util.echo(msg, inputIndex, inputString));
  }

  /******************************************************************************
   *
   * Builtin recognisers and their support functions below this line
   *
   ******************************************************************************/
  protected char peekCh() {
    if (inputIndex >= inputLength)
      return '\0';
    else
      return inputAsCharArray[inputIndex];
  }

  protected char peekChToLower() {
    if (inputIndex >= inputLength)
      return '\0';
    else
      return Character.toLowerCase(inputAsCharArray[inputIndex]);
  }

  protected char peekOneCh() {
    if (inputIndex + 1 >= inputLength)
      return '\0';
    else
      return inputAsCharArray[inputIndex + 1];
  }

  protected char peekCh(int offset) {
    if (inputIndex + offset >= inputLength)
      return '\0';
    else
      return inputAsCharArray[inputIndex + offset];
  }

  protected char getCh() {
    // System.out.println("getCh() at index " + inputIndex + " character " + (int) input[inputIndex]);
    if (inputIndex >= inputLength)
      return '\0';
    else
      return inputAsCharArray[inputIndex++];
  }

  protected void seekCh(int offset) {
    inputIndex = offset;
    getCh();
  }

  public static boolean isAlpha(char c) {
    return Character.isLetter(c);
  }

  public static boolean isDigit(char c) {
    return Character.isDigit(c);
  }

  public static boolean isHexDigit(char c) {
    return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
  }

  public static boolean isAlphaOrDigit(char c) {
    return isAlpha(c) || isDigit(c);
  }

  public static boolean isSimpleSpace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

  protected void match_CHARACTER(String string) {
    // System.out.print("At index " + inputIndex + " testing for character " + string + " against " + artPeekCh() + "...");
    if (string.charAt(0) != peekCh()) {
      inputIndex = leftIndex;
      // System.out.println(" reject");
      return;
    } else
      getCh();

    // System.out.println(" accept");
  }

  protected void match_SINGLETON_CASE_SENSITIVE(String string) {
    // System.out.print("At index " + inputIndex + " testing for singleton case sensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekCh()) {
        inputIndex = leftIndex;
        // System.out.println(" reject");
        return;
      } else
        getCh();

    // System.out.println(" accept");
  }

  protected void match_SINGLETON_CASE_INSENSITIVE(String string) {
    // System.out.print("At index " + inputIndex + " testing for singleton case insensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekChToLower()) {
        inputIndex = leftIndex;
        // System.out.println(" reject");
        return;
      } else
        getCh();

    // System.out.println(" accept");
  }

  protected void match_SML_COMMENT() {
    if (!(peekCh() == '(' && peekOneCh() == '*')) return;
    int nestingLevel = 0;

    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable SML comment");
        return;
      }

      if (peekCh() == '(' && peekOneCh() == '*') {
        getCh();
        getCh();
        nestingLevel++;
      } else if (peekCh() == '*' && peekOneCh() == ')') {
        getCh();
        getCh();
        nestingLevel--;
      } else
        getCh();
    } while (nestingLevel > 0);
  }

  protected void match_SML_D() {
    if (isDigit(peekCh())) getCh();
  }

  protected void match_SML_INT() {

    if (!(isDigit(peekCh()) || (peekCh() == '~' && isDigit(peekOneCh())))) return;

    if (peekCh() == '~') getCh();

    /* Check for hexadecimal introducer */
    boolean hex = peekCh() == '0' && peekOneCh() == 'x';

    if (hex) {
      getCh();
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = leftIndex;
        return;
      }
      while (isHexDigit(peekCh()))
        getCh();
    } else
      while (isDigit(peekCh()))
        getCh();
  }

  protected void match_SML_WORD() {
    if (!(peekCh() == '0' && peekOneCh() == 'w')) return;

    getCh(); // Skip leading 0w
    getCh();

    /* Check for hexadecimal introducer */
    boolean hex = peekCh() == 'x';

    if (hex) {
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = leftIndex;
        return;
      }
      while (isHexDigit(peekCh()))
        getCh();
    } else
      while (isDigit(peekCh()))
        getCh();
  }

  protected void match_SML_REAL() {
    if (!(isDigit(peekCh()) || (peekCh() == '~' && isDigit(peekOneCh())))) return;

    boolean invalid = true;

    if (peekCh() == '~') getCh();

    while (isDigit(peekCh()))
      getCh();

    if (peekCh() == '.') {
      getCh(); // skip .

      invalid = !isDigit(peekCh());

      while (isDigit(peekCh()))
        getCh();
    }

    if (peekCh() == 'e' || peekCh() == 'E') {

      getCh(); // skip e | E

      if (!(isDigit(peekCh()) || (peekCh() == '~' && isDigit(peekOneCh())))) {
        inputIndex = leftIndex;
        return;
      }

      if (peekCh() == '~') getCh();

      invalid = !isDigit(peekCh());

      while (isDigit(peekCh()))
        getCh();
    }

    // One or other or both of the optional parts must be present for this to be a float
    if (invalid) {
      inputIndex = leftIndex;
      return;
    }
  }

  protected void match_SML_CHAR() {
    if (!(peekCh() == '#' && peekOneCh() == '"')) return;
    getCh(); // Skip #
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated SML character");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '"');
    getCh(); // Skip delimiter
  }

  protected void match_SML_STRING() {
    if (peekCh() != '"') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated SML string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '"');
    getCh(); // Skip delimiter
  }

  Set<Character> SML_symbolIDElements = Set.of('!', '%', '&', '$', '#', '+', '-', '/', ':', '<', '=', '>', '?', '@', '\\', '~', '`', '^', '|', '*');
  Set<Character> SML_LabelInitialDigitElements = Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9');

  protected void match_SML_VID() {
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    } else
      while (SML_symbolIDElements.contains(peekCh()))
        getCh();
  }

  protected void match_SML_TYVAR() {
    if (peekCh() == '\'' && (isAlpha(peekOneCh()) || peekOneCh() == '_' || peekOneCh() == '\'')) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    }
  }

  protected void match_SML_TYCON() {
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    } else
      while (SML_symbolIDElements.contains(peekCh()))
        getCh();
  }

  protected void match_SML_LAB() { // labels
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    } else if (SML_LabelInitialDigitElements.contains(peekCh())) {
      while (isDigit(peekCh()))
        getCh();
    } else
      while (SML_symbolIDElements.contains(peekCh()))
        getCh();

  }

  protected void match_SML_STRID() { // alphanumeric identifiers for structure IDs
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '`')
        getCh();
    }
  }

  protected void match_SML_SYMID() {
    while (SML_symbolIDElements.contains(peekCh()))
      getCh();
  }

  protected void match_ID() {
    if (!(isAlpha(peekCh()) || peekCh() == '_')) return;
    while (isAlphaOrDigit(peekCh()) || peekCh() == '_')
      getCh();
  }

  protected void match_ID_AN() {
    if (!isAlpha(peekCh())) return;
    while (isAlphaOrDigit(peekCh()))
      getCh();
  }

  // An identifier that only accepts alphaetic characters - no under score or digit
  protected void match_ID_A() {
    while (isAlpha(peekCh()))
      getCh();
  }

  protected void match_ID_SOS() {
    if (!(isAlpha(peekCh()) || peekCh() == '_')) return;
    while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '-')
      getCh();

    while (peekCh() == '\'')
      getCh();
  }

  protected void match_INTEGER() {
    if (!isDigit(peekCh())) // Integers must contain at least one leading digit
      return;

    /* Check for hexadecimal introducer */
    boolean hex = (peekCh() == '0' && (peekOneCh() == 'x' || peekOneCh() == 'X'));

    if (hex) {
      getCh();
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = leftIndex;
        return;
      }
      while (isHexDigit(getCh()))
        ;
    } else
      while (isDigit(peekCh()))
        getCh();
  }

  protected void match_SIGNED_INTEGER() {
    if (peekCh() == '-' && isDigit(peekOneCh())) // Integers must contain at least one leading digit
      getCh();
    match_INTEGER();
  }

  protected void match_AP_INTEGER() {
    if (peekCh() != '\u00A3') return; // check for £
    getCh();
    match_SIGNED_INTEGER();
  }

  protected void match_REAL() {
    if (!isDigit(peekCh())) // Reals must contain at least one leading digit
      return;

    while (isDigit(peekCh()))
      getCh();

    // System.out.println("Testing for real at " + artCharacterStringInputIndex + ": current characters are " + artPeekCh() + " and " + artPeekOneCh());
    if (!(peekCh() == '.' && isDigit(peekOneCh()))) {
      inputIndex = leftIndex;
      return;
    }

    getCh(); // skip .

    while (isDigit(peekCh()))
      getCh();

    if (peekCh() == 'e' || peekCh() == 'E') {
      getCh();

      while (isDigit(peekCh()))
        getCh();
    }
  }

  protected void match_SIGNED_REAL() {
    if (peekCh() == '-' && isDigit(peekOneCh())) // Integers must contain at least one leading digit
      getCh();
    match_REAL();
  }

  protected void match_AP_REAL() {
    if (peekCh() != '\u00A3') return; // check for £
    getCh();
    match_SIGNED_REAL();
  }

  private void artSkipEscapeSequence() {
    getCh(); // Step over \element
  }

  protected void match_CHAR_SQ() {
    if (peekCh() != '\'') return;
    getCh(); // Skip delimiter
    if (getCh() == '\\') artSkipEscapeSequence();
    if (peekCh() != '\'') {
      inputIndex = leftIndex;
      return;
    } // Abort and return
    getCh(); // Skip delimiter
  }

  protected void match_CHAR_BQ() {
    if (peekCh() != '`') return;
    getCh(); // Skip delimiter
    if (getCh() == '\\') artSkipEscapeSequence();
  }

  protected void match_STRING_SQ() {
    if (peekCh() != '\'') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated ' ... ' string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '\'');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_PLAIN_SQ() {
    if (peekCh() != '\'') return;
    do {
      if (peekCh() == '\n') {
        lexicalError("Newline character in ' ... ' string");
        return;
      }
      if (peekCh() == '\0') {
        lexicalError("Unterminated ' ... ' string");
        return;
      }
      getCh();
    } while (peekCh() != '\'');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_DQ() {
    if (peekCh() != '"') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated \" ... \" string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '"');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BQ() {
    if (peekCh() != '`') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated ` ... ` string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '`');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_DOLLAR() {
    if (peekCh() != '$') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated $ ... $ string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '$');
    getCh(); // Skip delimiter
  }

  private void match_STRING_SHRIEK_SHREIK() {
    if (!(peekCh() == '!' && peekOneCh() == '!')) return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated !! ... !! string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (!(peekCh() == '!' && peekOneCh() == '!'));
    getCh();
    getCh();// Skip delimiter
  }

  protected void match_STRING_BRACKET_NEST() {
    if (peekCh() != '[') return;
    int nestLevel = 0;
    do {
      if (peekCh() == '[') nestLevel++;
      if (peekCh() == ']') nestLevel--;
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable [ ... ] string");
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BRACKET() {
    if (peekCh() != '[') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated [ ... ] string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != ']');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BRACE() {
    if (peekCh() != '{') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated { ... } string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '}');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BRACE_NEST() {
    if (peekCh() != '{') return;
    int nestLevel = 0;
    do {
      if (peekCh() == '{') nestLevel++;
      if (peekCh() == '}') nestLevel--;
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable { ... } string");
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BB() {
    if (!((peekCh() == '[') && (peekOneCh() == '['))) return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated [[ ... ]] string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (!((peekCh() == ']') && (peekOneCh() == ']')));
    getCh(); // Skip delimiter
    getCh(); // Skip delimiter
  }

  protected void match_SIMPLE_WHITESPACE() {
    while (isSimpleSpace(peekCh()))
      getCh();
  }

  protected void match_COMMENT_NEST_ART() {
    if (!(peekCh() == '(' && peekOneCh() == '*')) return;
    int nestingLevel = 0;

    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable (* ... *) comment");
        return;
      }

      if (peekCh() == '(' && peekOneCh() == '*') {
        getCh();
        getCh();
        nestingLevel++;
      } else if (peekCh() == '*' && peekOneCh() == ')') {
        getCh();
        getCh();
        nestingLevel--;
      } else
        getCh();
    } while (nestingLevel > 0);
  }

  protected void match_COMMENT_BLOCK_C() {
    if (!((peekCh() == '/') && (peekOneCh() == '*'))) return;

    do {
      if (peekCh() == '\0') {
        System.out.println("Unterminated /* ... */ comment");
        return;
      }
      getCh();
    } while (!(peekCh() == '*' && peekOneCh() == '/'));
    getCh();
    getCh();
  }

  protected void match_COMMENT_LINE_C() {
    if (!((peekCh() == '/') && (peekOneCh() == '/'))) return;

    while (peekCh() != '\n' && peekCh() != '\0') // Quietly accept an input file with no \n at the end.
      getCh();
  }

}
