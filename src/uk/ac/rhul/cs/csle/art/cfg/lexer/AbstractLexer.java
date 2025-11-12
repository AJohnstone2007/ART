package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;

public abstract class AbstractLexer {
  public AbstractLexicalisations lexicalisations;

  protected int inputIndex, lexemeStart, lexemeEnd, whitespacePrefix;
  protected char[] inputAsCharArray;
  public Map<CFGElement, CFGElement> uobs, uibs;

  public abstract AbstractLexicalisations lex(String userString, CFGRules cfgRules);

  protected void lexicalError(String msg, int inputIndex) {
    Util.error(Util.echo(msg, inputIndex, lexicalisations.inputString));
  }

  /******************************************************************************
   *
   * Individual recognisers and their support functions below this line
   *
   ******************************************************************************/
  protected char peekCh() {
    if (inputIndex >= lexicalisations.inputString.length())
      return '\0';
    else
      return inputAsCharArray[inputIndex];
  }

  protected char peekChToLower() {
    if (inputIndex >= lexicalisations.inputString.length())
      return '\0';
    else
      return Character.toLowerCase(inputAsCharArray[inputIndex]);
  }

  protected char peekOneCh() {
    if (inputIndex + 1 >= lexicalisations.inputString.length())
      return '\0';
    else
      return inputAsCharArray[inputIndex + 1];
  }

  protected char peekCh(int offset) {
    if (inputIndex + offset >= lexicalisations.inputString.length())
      return '\0';
    else
      return inputAsCharArray[inputIndex + offset];
  }

  protected char getCh() {
    if (inputIndex >= lexicalisations.inputString.length())
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

  protected void match_CHARACTER_SET(Set<Character> set) {
    if (!set.contains(peekCh()))
      return;
    else
      getCh();
  }

  protected void match_SINGLETON_CASE_SENSITIVE(String string) {
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekCh()) {
        inputIndex = lexemeStart;
        // Util.info(" reject");
        return;
      } else
        getCh();
  }

  protected void match_SINGLETON_CASE_INSENSITIVE(String string) {
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
        lexicalError("Unterminated nestable SML comment", lexemeStart);
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
        lexicalError("Unterminated SML character", lexemeStart);
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
        lexicalError("Unterminated SML string", lexemeStart);
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
    char control = getCh(); // Step over element
    // Util.debug("skipEscapeSquence at " + inputIndex + ": " + peekCh());
    if (control == 'u') {
      getCh();
      getCh();
      getCh();
      getCh();
    } else if (control == 'v') {
      getCh();
      getCh();
      getCh();
      getCh();
      getCh();
      getCh();
    }
  }

  protected void match_CHAR_SQ() {
    if (peekCh() != '\'') return;
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
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated ' ... ' string", lexemeStart);
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '\'');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_PLAIN_SQ() {
    if (peekCh() != '\'') return;
    do {
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated ' ... ' string", lexemeStart);
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
        lexicalError("Unterminated \" ... \" string", lexemeStart);
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
        lexicalError("Unterminated ` ... ` string", lexemeStart);
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '`');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_DOLLAR() {
    if (peekCh() != '$') return;
    do {
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated $ ... $ string", lexemeStart);
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
        lexicalError("Unterminated !! ... !! string", lexemeStart);
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (!(peekCh() == '!' && peekOneCh() == '!'));
    getCh();
    getCh();// Skip delimiter
  }

  protected void match_STRING_ANGLE_NEST() {
    if (peekCh() != '<') return;
    int nestLevel = 0;
    do {
      if (peekCh() == '<') nestLevel++;
      if (peekCh() == '>') nestLevel--;
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated nestable < ... > string", lexemeStart);
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BRACKET_NEST() {
    if (peekCh() != '[') return;
    int nestLevel = 0;
    do {
      if (peekCh() == '[') nestLevel++;
      if (peekCh() == ']') nestLevel--;
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated nestable [ ... ] string", lexemeStart);
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BRACKET() {
    if (peekCh() != '[') return;
    do {
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated [ ... ] string", lexemeStart);
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != ']');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BRACE() {
    if (peekCh() != '{') return;
    do {
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated { ... } string", lexemeStart);
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
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated nestable { ... } string", lexemeStart);
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BB() {
    if (!((peekCh() == '[') && (peekOneCh() == '['))) return;
    do {
      if (peekCh() == '\0' || peekCh() == '\n') {
        lexicalError("Unterminated [[ ... ]] string", lexemeStart);
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
        lexicalError("Unterminated nestable (* ... *) comment", lexemeStart);
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
        lexicalError("Unterminated /* ... */ comment", lexemeStart);
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