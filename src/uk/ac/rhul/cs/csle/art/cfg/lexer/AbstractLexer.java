package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.io.PrintStream;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public abstract class AbstractLexer implements DisplayInterface {
  protected CFGRules cfgRules;
  public String inputString = ""; // Original input string
  public TWESetElement[][] tweSlices;
  protected int inputIndex, lexemeStart, lexemeEnd, whitespacePrefix;
  protected char[] inputAsCharArray;

  // public static boolean useWhitespacePrefix = true; // dirty tricks department: this is a static variable so after making the script term we can switch to th
  // lexing regime for any !try directive
  // Internal research fields
  public Integer deleteTokenCount = 0;
  public Integer swapTokenCount = 0;

  protected void lexicalError(String msg, int inputIndex) {
    Util.error(Util.echo(msg, inputIndex, inputString));
  }

  public int cardinality() {
    if (tweSlices == null) return 0;
    int ret = 0;
    for (var i : tweSlices)
      ret += i.length;
    return ret;
  }

  public abstract boolean lex(String input, CFGRules cfgRules, ChooseRules chooseRules);

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
      Util.warning("TWE set is empty");
      outputStream.println("Empty TWE set");
      return;
    }
    outputStream.println("TWE Set");

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
  public String lexeme(CFGNode grammarNode, int leftExtent) {

    if (tweSlices[leftExtent] == null) Util.fatal("internal error - SPPF has terminal with left extent referencing null TWE   slice " + leftExtent);
    for (var te : tweSlices[leftExtent])
      if (te.cfgElement.cfgKind == grammarNode.cfgElement.cfgKind) return lexeme(te);

    Util.fatal("internal error - SPPF has terminal which is not present in TWE slice " + leftExtent);
    return "??? Unmatched lexeme kind";
  }

  public String lexeme(TWESetElement element) {
    String full = inputString.substring(element.leftExtent, element.lexemeEnd);

    if (element.cfgElement.cfgKind == CFGKind.TRM_BI) {
      switch (element.cfgElement.str) {
      case "STRING_DQ", "STRING_SQ", "STRING_PLAIN_SQ", "STRING_DOLLAR", "STRING_BRACE":
        return full.substring(1, full.length() - 1);
      case "CHAR_BQ":
        return full.substring(1);
      }
    }
    return full;
  }

  public void choose() {
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
        if (rightKind == CFGKind.SOS) continue; // do not suppress the start of string token

        for (int leftTWEIndex = 0; leftTWEIndex < tweSlices[sliceIndex].length; leftTWEIndex++) {
          if (leftTWEIndex == rightTWEIndex) continue; // Do not self-compare
          var leftTWE = tweSlices[sliceIndex][leftTWEIndex];
          var leftElement = leftTWE.cfgElement;
          var leftKind = leftElement.cfgKind;

          Util.debug("AbstractLexer.choose() at slice " + sliceIndex + " comparing " + leftTWE + " to " + rightTWE);

          // All set - now check the relations
          if ((ScriptInterpreter.currentChooseRules.testLonger(leftElement, rightElement) && leftTWE.rightExtent > rightTWE.rightExtent)
              || (ScriptInterpreter.currentChooseRules.testShorter(leftElement, rightElement) && leftTWE.rightExtent < rightTWE.rightExtent)
              || (ScriptInterpreter.currentChooseRules.testHigher(leftElement, rightElement) && leftTWE.rightExtent == rightTWE.rightExtent)) {
            rightTWE.suppressed = true;

            Util.debug("Suppressed " + rightTWE);
          }
        }
      }
    }
  }

  public void chooseDefault() {
    Util.debug("Running default lexical chooser");
    if (tweSlices[0] == null) {
      Util.error("lexer.chooseDefault() - empty tweSet");
      return;
    }

    if (!isDeterministic())
      Util.warning("TWE set has multiple lexicalisations; picking one using default strategy of longest match with builtins having low priority");

    for (int i = 0; i < tweSlices.length; i++) {
      var slice = tweSlices[i];
      if (slice != null) for (int ee = 0; ee < tweSlices[i].length; ee++)
        for (int ff = 0; ff < tweSlices[i].length; ff++) {
          if (ee == ff) continue; // Do not self-compare
          var f = tweSlices[i][ff];
          if (f.cfgElement.cfgKind == CFGKind.SOS) continue; // do not suppress the start of string token

          var e = tweSlices[i][ee];
          if (e.rightExtent > f.rightExtent)
            f.suppressed = true;
          else if (e.rightExtent == f.rightExtent && f.cfgElement.cfgKind == CFGKind.TRM_BI) {
            // Util.debug("TWE pair " + e + " and " + f + " - suppressing " + f);
            f.suppressed = true;
          }
        }
    }
  }

  public void chooseEAS() {
    // Util.debug("Running EAS lexical chooser");
    if (tweSlices[0] == null) {
      Util.error("lexer.chooseEAS() - empty tweSet");
      return;
    }

    if (!isDeterministic())
      Util.warning("TWE set has multiple lexicalisations; picking one using EAS strategy of case insensitive terminals having high priority");

    for (int i = 0; i < tweSlices.length; i++) {
      var slice = tweSlices[i];
      if (slice != null) for (int ee = 0; ee < tweSlices[i].length; ee++)
        for (int ff = 0; ff < tweSlices[i].length; ff++) {
          if (ee == ff) continue; // Do not self-compare
          var f = tweSlices[i][ff];
          if (f.cfgElement.cfgKind == CFGKind.SOS) continue; // do not suppress the start of string token

          var e = tweSlices[i][ee];
          if (e.cfgElement.cfgKind == CFGKind.TRM_CS && f.cfgElement.cfgKind != CFGKind.TRM_CS) f.suppressed = true;
        }
    }
  }

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
   * unsuppressed elements into it.
   */
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

  /******************************************************************************
   *
   * Individual recognisers and their support functions below this line
   *
   ******************************************************************************/
  protected char peekCh() {
    if (inputIndex >= inputString.length())
      return '\0';
    else
      return inputAsCharArray[inputIndex];
  }

  protected char peekChToLower() {
    if (inputIndex >= inputString.length())
      return '\0';
    else
      return Character.toLowerCase(inputAsCharArray[inputIndex]);
  }

  protected char peekOneCh() {
    if (inputIndex + 1 >= inputString.length())
      return '\0';
    else
      return inputAsCharArray[inputIndex + 1];
  }

  protected char peekCh(int offset) {
    if (inputIndex + offset >= inputString.length())
      return '\0';
    else
      return inputAsCharArray[inputIndex + offset];
  }

  protected char getCh() {
    if (inputIndex >= inputString.length())
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
      if (peekCh() == '\0') {
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
      if (peekCh() == '\n') {
        lexicalError("Newline character in ' ... ' string", lexemeStart);
        return;
      }
      if (peekCh() == '\0') {
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
      if (peekCh() == '\0') {
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

  protected void match_STRING_BRACKET_NEST() {
    if (peekCh() != '[') return;
    int nestLevel = 0;
    do {
      if (peekCh() == '[') nestLevel++;
      if (peekCh() == ']') nestLevel--;
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable [ ... ] string", lexemeStart);
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BRACKET() {
    if (peekCh() != '[') return;
    do {
      if (peekCh() == '\0') {
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
      if (peekCh() == '\0') {
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
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable { ... } string", lexemeStart);
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BB() {
    if (!((peekCh() == '[') && (peekOneCh() == '['))) return;
    do {
      if (peekCh() == '\0') {
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