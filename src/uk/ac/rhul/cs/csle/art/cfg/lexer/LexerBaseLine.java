package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class LexerBaseLine extends AbstractLexer {

  public ArrayList<Set<TWESetElement>> tweSet;
  private int inputIndex, inputLength, lexemeEnd;

  @Override
  public String lexeme(int l) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void printLexicalisations(boolean raw) {
    System.out.println("TWE set for input string " + inputString);
    for (int i = 0; i < tweSet.size(); i++) {
      var e = tweSet.get(i);
      if (e != null) for (var s : e)
        System.out.println(i + ": " + s + " lexeme " + inputString.substring(i, s.lexemeEnd));
    }
  }

  @Override
  public String lexemeOfBuiltin(TokenKind kind, int startIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void statistics(Statistics currentstatistics) {
    // TODO Auto-generated method stub

  }

  @Override
  public int[] getTokens() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int[] getLeftIndices() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void lex(String userString, CFGRules cfgRules) {
    inputString = userString + "\0";
    inputAsCharArray = inputString.toCharArray();
    inputIndex = 0;
    inputLength = inputString.length();
    tweSet = new ArrayList<>();
    for (int i = 0; i < inputLength; i++)
      tweSet.add(null);
    tweSet.set(0, new HashSet<>());

    for (int i = 0; i < inputString.length(); i++)
      if (tweSet.get(i) != null) constructTWESlice(i, cfgRules);

    var lastElement = tweSet.get(inputLength - 1);

    if (lastElement != null) lastElement.add(new TWESetElement(cfgRules.endOfStringElement, inputLength, inputLength));
  }

  public void constructTWESlice(int lexemeStart, CFGRules cfgRules) {
    for (var e : cfgRules.elements.keySet())
      if (e.isToken) {
        inputIndex = lexemeStart;
        tryTokenMatch(e);
        if (inputIndex != lexemeStart) {// Matched?
          lexemeEnd = inputIndex;
          int wsStart;
          if (!e.suppressWhitespace) do {
            wsStart = inputIndex;
            for (var w : cfgRules.elements.keySet())
              if (w.isWhitespace) tryTokenMatch(w);
          } while (inputIndex != wsStart); // No more whitespace found

          tweSet.get(lexemeStart).add(new TWESetElement(e, lexemeEnd, inputIndex));
          if (tweSet.get(inputIndex) == null) tweSet.set(inputIndex, new HashSet<>()); // Mark for downstream processing
        }
      }
  }

  private void tryTokenMatch(CFGElement e) {
    // System.out.println("tryTokenMatch(" + e + ") at inputIndex " + lexerInputIndex);

    switch (e.cfgKind) {
    default:
      Util.fatal("tryTokenMatch() on cfgElement with unexpected cfgKind " + e);
      break;
    case T:
      match_SINGLETON_CASE_INSENSITIVE(e.str);
      break;
    case TI:
      match_SINGLETON_CASE_INSENSITIVE(e.str);
    case C:
      match_CHARACTER(e.str);
      break;
    case N:
      Util.fatal("tryTokenMatch() in class " + this.getClass().getSimpleName() + " does not support paraterminals");
    case B:
      switch (e.str) {
      case "ID":
        match_ID();
        break;
      case "INTEGER":
        match_INTEGER();
        break;
      case "SIGNED_INTEGER":
        match_SIGNED_INTEGER();
        break;
      case "AP_INTEGER":
        match_AP_INTEGER();
        break;
      case "REAL":
        match_REAL();
        break;
      case "AP_REAL":
        match_AP_REAL();
        break;
      case "SIGNED_REAL":
        match_SIGNED_REAL();
        break;
      case "CHAR_BQ":
        match_CHAR_BQ();
        break;
      case "STRING_DQ":
        match_STRING_DQ();
        break;
      case "STRING_SQ":
        match_STRING_SQ();
        break;
      case "STRING_PLAIN_SQ":
        match_STRING_PLAIN_SQ();
        break;
      case "STRING_BRACE_NEST":
        match_STRING_BRACE_NEST();
        break;
      case "STRING_BRACKET_NEST":
        match_STRING_BRACKET_NEST();
        break;
      case "STRING_DOLLAR":
        match_STRING_DOLLAR();
        break;
      case "STRING_SHRIEK_SHRIEK":
        match_STRING_SHRIEK_SHREIK();
        break;
      case "SIMPLE_WHITESPACE":
        match_SIMPLE_WHITESPACE();
        break;
      case "COMMENT_BLOCK_C":
        match_COMMENT_BLOCK_C();
        break;
      case "COMMENT_LINE_C":
        match_COMMENT_LINE_C();
        break;
      case "COMMENT_NEST_ART":
        match_COMMENT_NEST_ART();
        break;

      case "SML_COMMENT":
        match_SML_COMMENT();
        break;
      case "SML_D":
        match_SML_D();
        break;
      case "SML_INT":
        match_SML_INT();
        break;
      case "SML_WORD":
        match_SML_WORD();
        break;
      case "SML_REAL":
        match_SML_REAL();
        break;
      case "SML_CHAR":
        match_SML_CHAR();
        break;
      case "SML_STRING":
        match_SML_STRING();
        break;
      case "SML_VID":
        match_SML_VID();
        break;
      case "SML_TYVAR":
        match_SML_TYVAR();
        break;
      case "SML_TYCON":
        match_SML_TYCON();
        break;
      case "SML_LAB":
        match_SML_LAB();
        break;
      case "SML_STRID":
        match_SML_STRID();
        break;
      case "SML_SYMID":
        match_SML_SYMID();
        break;

      default:
        Util.fatal("Unknown builtin in lexer " + e);
        break;
      }

    }
  }

  /******************************************************************************
   *
   * Individual recognisers and their support functions below this line
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
    if (string.charAt(0) != peekCh())
      return;
    else
      getCh();
  }

  protected void match_SINGLETON_CASE_SENSITIVE(String string) {
    int lexemeStart = inputIndex;
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekCh()) {
        inputIndex = lexemeStart;
        // Util.info(" reject");
        return;
      } else
        getCh();
  }

  protected void match_SINGLETON_CASE_INSENSITIVE(String string) {
    int lexemeStart = inputIndex;
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekChToLower()) {
        inputIndex = lexemeStart;
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

    int lexemeStart = inputIndex;

    if (peekCh() == '~') getCh();

    /* Check for hexadecimal introducer */
    boolean hex = peekCh() == '0' && peekOneCh() == 'x';

    if (hex) {
      getCh();
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = lexemeStart;
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

    int lexemeStart = inputIndex;

    getCh(); // Skip leading 0w
    getCh();

    /* Check for hexadecimal introducer */
    boolean hex = peekCh() == 'x';

    if (hex) {
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = lexemeStart;
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

    int lexemeStart = inputIndex;
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
        inputIndex = lexemeStart;
        return;
      }

      if (peekCh() == '~') getCh();

      invalid = !isDigit(peekCh());

      while (isDigit(peekCh()))
        getCh();
    }

    // One or other or both of the optional parts must be present for this to be a float
    if (invalid) {
      inputIndex = lexemeStart;
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

    int lexemeStart = inputIndex;

    /* Check for hexadecimal introducer */
    boolean hex = (peekCh() == '0' && (peekOneCh() == 'x' || peekOneCh() == 'X'));

    if (hex) {
      getCh();
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = lexemeStart;
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
    int lexemeStart = inputIndex;
    if (!isDigit(peekCh())) // Reals must contain at least one leading digit
      return;

    while (isDigit(peekCh()))
      getCh();

    // Util.info("Testing for real at " + artCharacterStringInputIndex + ": current characters are " + peekCh() + " and " + peekOneCh());
    if (!(peekCh() == '.' && isDigit(peekOneCh()))) {
      inputIndex = lexemeStart;
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
    int lexemeStart = inputIndex;
    getCh(); // Skip delimiter
    if (getCh() == '\\') artSkipEscapeSequence();
    if (peekCh() != '\'') {
      inputIndex = lexemeStart;
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

  protected void match_STRING_SHRIEK_SHREIK() {
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
}
