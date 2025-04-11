package uk.ac.rhul.cs.csle.art.cfg.rdsob;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class AbstractRDSOB {
  protected String input;
  protected int inputIndex;
  protected int oracleIndex;
  protected int lexemeL;
  protected int lexemeR;
  protected int oracle[];

  protected AbstractRDSOB() {
    oracle = new int[1000];
    inputIndex = oracleIndex = 0;
  }

  protected String lexeme() {
    return input.substring(lexemeL, lexemeR);
  }

  protected String readInput(String filename) throws FileNotFoundException {
    return new Scanner(new File(filename)).useDelimiter("\\Z").next() + "\0";
  }

  protected void oracleSet(int i) {
    if (oracleIndex == oracle.length) oracle = Arrays.copyOf(oracle, oracle.length + oracle.length / 2);
    oracle[oracleIndex++] = i;
  }

  protected boolean match(String s) {
    if (input.regionMatches(inputIndex, s, 0, s.length())) {
      inputIndex += s.length();
      builtIn_WHITESPACE();
      return true;
    }
    return false;
  }

  // Built in recognisers below this line
  protected boolean builtIn_ID() {
    if (!Character.isJavaIdentifierStart(input.charAt(inputIndex))) return false;
    lexemeL = inputIndex++;
    while (Character.isJavaIdentifierPart(input.charAt(inputIndex)))
      inputIndex++;
    lexemeR = inputIndex;
    builtIn_WHITESPACE();
    return true;
  }

  protected boolean builtIn_WHITESPACE() {
    while (Character.isWhitespace(input.charAt(inputIndex)))
      inputIndex++;
    return true;
  }

  protected boolean isxdigit(char c) {
    if (Character.isDigit(c)) return true;
    if (c >= 'a' && c <= 'f') return true;
    if (c >= 'A' && c <= 'F') return true;
    return false;
  }

  protected boolean builtIn_INTEGER() {
    if (!Character.isDigit(input.charAt(inputIndex))) return false;
    lexemeL = inputIndex;

    boolean hex = (input.charAt(inputIndex) == '0' && (input.charAt(inputIndex + 1) == 'x' || input.charAt(inputIndex + 1) == 'X'));
    if (hex) inputIndex += 2; // Skip over hex introducer

    while (hex ? isxdigit(input.charAt(inputIndex)) : Character.isDigit(input.charAt(inputIndex)))
      inputIndex++;
    lexemeR = inputIndex;
    builtIn_WHITESPACE();
    return true;
  }

  protected boolean builtIn_REAL() {
    if (!Character.isDigit(input.charAt(inputIndex))) return false;
    lexemeL = inputIndex;
    while (Character.isDigit(input.charAt(inputIndex)))
      inputIndex++;
    if (input.charAt(inputIndex) != '.') return true;
    inputIndex++; // skip over the .
    while (Character.isDigit(input.charAt(inputIndex)))
      inputIndex++;
    if (input.charAt(inputIndex) == 'e' || input.charAt(inputIndex) == 'E') {
      inputIndex++;
      while (Character.isDigit(input.charAt(inputIndex)))
        inputIndex++;
    }
    lexemeR = inputIndex;
    builtIn_WHITESPACE();
    return true;
  }

  protected boolean builtIn_CHAR_SQ() {
    if (input.charAt(inputIndex) != '\'') return false;
    inputIndex++; // skip over the '
    lexemeL = inputIndex;
    if (input.charAt(inputIndex) == '\\') inputIndex++;
    inputIndex++;
    if (input.charAt(inputIndex) != '\'') return false;
    lexemeR = inputIndex;
    inputIndex++; // skip past final delimiter
    builtIn_WHITESPACE();
    return true;
  }

  protected boolean builtIn_STRING_SQ() {
    if (input.charAt(inputIndex) != '\'') return false;
    lexemeL = inputIndex + 1;
    do {
      if (input.charAt(inputIndex) == '\\') inputIndex++;
      inputIndex++;
    } while (input.charAt(inputIndex) != '\'');
    lexemeR = inputIndex;
    inputIndex++; // skip past final delimiter
    builtIn_WHITESPACE();
    return true;
  }

  protected boolean builtIn_STRING_DQ() {
    if (input.charAt(inputIndex) != '"') return false;
    lexemeL = inputIndex + 1;
    do {
      if (input.charAt(inputIndex) == '\\') inputIndex++;
      inputIndex++;
    } while (input.charAt(inputIndex) != '"');
    lexemeR = inputIndex;
    inputIndex++; // skip past final delimiter
    builtIn_WHITESPACE();
    return true;
  }

  protected boolean builtIn_ACTION() {
    if (!(input.charAt(inputIndex) == '!' && input.charAt(inputIndex + 1) == '!')) return false;
    inputIndex += 2;
    lexemeL = inputIndex;
    while (true) {
      if (input.charAt(inputIndex) == 0) break;
      if (input.charAt(inputIndex) == '!' && input.charAt(inputIndex) == '!') {
        inputIndex += 2;
        break;
      }
      inputIndex++;
    }
    lexemeR = inputIndex - 2;
    builtIn_WHITESPACE();
    return true;
  }
}
