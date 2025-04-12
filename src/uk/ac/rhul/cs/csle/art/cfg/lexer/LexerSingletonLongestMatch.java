package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class LexerSingletonLongestMatch extends AbstractLexer {
  public void lex(String inputString, LexemeKind[] kinds, String[] strings, LexemeKind[] whitespaces) {
    this.inputString = inputString + "\0";
    this.kinds = kinds;
    this.strings = strings;
    this.whitespaces = whitespaces;

    // Util.info("Starting lexing with kinds/string arrays:");
    // for (int i = 0; i < kinds.length; i++)
    // Util.info(kinds[i] + "/" + strings[i]);

    inputAsCharArray = inputString.toCharArray();
    lexerInputIndex = 0;
    inputLength = inputString.length();

    tokenList = new ArrayList<>();
    leftIndexList = new ArrayList<>();
    rightIndexList = new ArrayList<>();
    tokens = leftIndices = null;

    longestMatchRightIndex = 0;
    longestMatchToken = 0;
    // Util.info("Input: " + inputString);

    while (lexerInputIndex < inputAsCharArray.length) {
      // Absorb a run of whitespace tokens
      // Util.info("Absorbing WS at index " + inputIndex);
      while (true) {
        int wsStart = lexerInputIndex;

        for (LexemeKind w : whitespaces)
          processBuiltin(w, null);
        if (lexerInputIndex == wsStart) break;
      }

      if (lexerInputIndex == inputLength) break;

      leftIndex = lexerInputIndex;

      // Util.info("Running recognisers at index " + inputIndex);
      for (int token = 1; token < kinds.length; token++) {
        lexerInputIndex = leftIndex;
        processBuiltin(kinds[token], strings[token]);
        checkLongestMatch(token);
      }

      if (longestMatchRightIndex == leftIndex) { // We matched nothing, which is an error
        lexicalError("Unrecognised lexeme");
        tokenList = null;
        return;
      }

      tokenList.add(longestMatchToken);
      leftIndexList.add(leftIndex);
      rightIndexList.add(longestMatchRightIndex);
      lexerInputIndex = longestMatchRightIndex;
    }
    if (deleteTokenCount > 0) {
      Util.info("Deleting " + deleteTokenCount + " tokens - original size " + tokenList.size() + "\n");
      int leftIndex = tokenList.size() / 2 - deleteTokenCount / 2;
      tokenList.subList(leftIndex, leftIndex + deleteTokenCount).clear();
      Util.info("Deleted " + deleteTokenCount + " tokens - final size " + tokenList.size() + "\n");
    }
    tokenList.add(0); // Terminating EOS
    leftIndexList.add(inputString.length());
    rightIndexList.add(inputString.length() + 1);
    tokens = new int[tokenList.size()];
    leftIndices = new int[tokenList.size()];
    rightIndices = new int[tokenList.size()];
    for (int i = 0; i < tokens.length; i++) {
      tokens[i] = tokenList.get(i);
      leftIndices[i] = leftIndexList.get(i);
      rightIndices[i] = rightIndexList.get(i);
    }
    if (swapTokenCount > 0) {
      Util.trace(2, 0, "Swapping " + swapTokenCount + " tokens\n");
      int leftIndex = tokenList.size() / 2 - swapTokenCount / 2;
      int rightIndex = tokenList.size() / 2 + swapTokenCount / 2;
      while (leftIndex < rightIndex) {
        int tmp = tokens[leftIndex];
        tokens[leftIndex] = tokens[rightIndex];
        tokens[rightIndex] = tmp;

        tmp = leftIndices[leftIndex];
        leftIndices[leftIndex] = leftIndices[rightIndex];
        leftIndices[rightIndex] = tmp;

        tmp = rightIndices[leftIndex];
        rightIndices[leftIndex] = rightIndices[rightIndex];
        rightIndices[rightIndex] = tmp;

        leftIndex++;
        rightIndex--;
      }
    }
  }

  private void checkLongestMatch(int token) {
    if (lexerInputIndex > longestMatchRightIndex) {
      longestMatchRightIndex = lexerInputIndex;
      longestMatchToken = token;
      lexerInputIndex = leftIndex;
    }
  }

  public void printLexicalisations(boolean raw) {
    // Util.info("String: " + inputString);
    // var lexemeKinds = LexemeKind.values();
    // for (int i = 0; i < lexemeKinds.length; i++)
    // Util.info(lexemeKinds[i]);
    // int index = 0;
    if (raw)
      for (int i = 0; i < tokens.length; i++)
        Util.info(kinds[tokenList.get(i)] == LexemeKind.SINGLETON_CASE_SENSITIVE ? strings[tokens[i]] : kinds[tokenList.get(i)].toString());
    else
      for (int i = 0; i < tokens.length; i++)
        Util.info(i + ":" + leftIndices[i] + "," + rightIndices[i] + " " + kinds[tokenList.get(i)] + " " + strings[tokens[i]]);
  }

  protected void processBuiltin(LexemeKind b, String s) {
    // System.out.print("Checking builtin " + b + "/" + s + " at inputIndex " + inputIndex);
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

    case SML_COMMENT:
      match_SML_COMMENT();
      break;
    case SML_D:
      match_SML_D();
      break;
    case SML_INT:
      match_SML_INT();
      break;
    case SML_WORD:
      match_SML_WORD();
      break;
    case SML_REAL:
      match_SML_REAL();
      break;
    case SML_CHAR:
      match_SML_CHAR();
      break;
    case SML_STRING:
      match_SML_STRING();
      break;
    case SML_VID:
      match_SML_VID();
      break;
    case SML_TYVAR:
      match_SML_TYVAR();
      break;
    case SML_TYCON:
      match_SML_TYCON();
      break;
    case SML_LAB:
      match_SML_LAB();
      break;
    case SML_STRID:
      match_SML_STRID();
      break;
    case SML_SYMID:
      match_SML_SYMID();
      break;

    default:
      Util.fatal("Unknown builtin " + b);
      break;
    }
    // Util.info(" return " + inputIndex);
  }

  protected void lexicalError(String msg) {
    Util.fatal(Util.echo(msg, lexerInputIndex, inputString));
  }

  /******************************************************************************
   *
   * Builtin recognisers and their support functions below this line
   *
   ******************************************************************************/
  protected char peekCh() {
    if (lexerInputIndex >= inputLength)
      return '\0';
    else
      return inputAsCharArray[lexerInputIndex];
  }

  protected char peekChToLower() {
    if (lexerInputIndex >= inputLength)
      return '\0';
    else
      return Character.toLowerCase(inputAsCharArray[lexerInputIndex]);
  }

  protected char peekOneCh() {
    if (lexerInputIndex + 1 >= inputLength)
      return '\0';
    else
      return inputAsCharArray[lexerInputIndex + 1];
  }

  protected char peekCh(int offset) {
    if (lexerInputIndex + offset >= inputLength)
      return '\0';
    else
      return inputAsCharArray[lexerInputIndex + offset];
  }

  protected char getCh() {
    if (lexerInputIndex >= inputLength)
      return '\0';
    else
      return inputAsCharArray[lexerInputIndex++];
  }

  protected void seekCh(int offset) {
    lexerInputIndex = offset;
    getCh();
  }

  public static boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  public static boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
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
    if (string.charAt(0) != peekCh()) {
      lexerInputIndex = leftIndex;
      return;
    } else
      getCh();
  }

  protected void match_SINGLETON_CASE_SENSITIVE(String string) {
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekCh()) {
        lexerInputIndex = leftIndex;
        // Util.info(" reject");
        return;
      } else
        getCh();
  }

  protected void match_SINGLETON_CASE_INSENSITIVE(String string) {
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekChToLower()) {
        lexerInputIndex = leftIndex;
        return;
      } else
        getCh();
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
        lexerInputIndex = leftIndex;
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
        lexerInputIndex = leftIndex;
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
        lexerInputIndex = leftIndex;
        return;
      }

      if (peekCh() == '~') getCh();

      invalid = !isDigit(peekCh());

      while (isDigit(peekCh()))
        getCh();
    }

    // One or other or both of the optional parts must be present for this to be a float
    if (invalid) {
      lexerInputIndex = leftIndex;
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
    int lexemeStart = lexerInputIndex;
    if (peekCh() == '\'') {
      // if (isAlpha(peekOneCh()) || peekOneCh() == '_' || peekOneCh() == '\'') {
      getCh();
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
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
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
        lexerInputIndex = leftIndex;
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

    // Util.info("Testing for real at " + artCharacterStringInputIndex + ": current characters are " + peekCh() + " and " + peekOneCh());
    if (!(peekCh() == '.' && isDigit(peekOneCh()))) {
      lexerInputIndex = leftIndex;
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
      lexerInputIndex = leftIndex;
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
        lexicalError("Unterminated /* ... */ comment");
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

  @Override
  public void statistics(Statistics statistics) {
    {
      statistics.put("tweNodeCount", (long) tokens.length);
      statistics.put("tweEdgeCount", tokens.length - 1);
      statistics.put("tweLexCount", 1);
    }
  }

}