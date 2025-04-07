package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;

public class AbstractLexer {
  public String inputString = ""; // Original input string
  public String inputStringName = "";
  public int[] tokens; // Input as array of tokens
  public int[] oracle;
  public int[] leftIndices;
  public int[] rightIndices;
  protected ArrayList<Integer> tokenList;
  protected ArrayList<Integer> leftIndexList;
  protected ArrayList<Integer> rightIndexList;
  public Integer deleteTokenCount = 0;
  public Integer swapTokenCount = 0;
  /* Lexers **********************************************************************/
  protected LexemeKind[] kinds;
  protected String[] strings;
  protected LexemeKind[] whitespaces;

  protected char[] inputAsCharArray;
  protected int lexerInputIndex, inputLength, leftIndex, longestMatchToken, longestMatchRightIndex, firstBuiltin;

  public String lexeme(int l) {
    return inputString.substring(leftIndices[l], rightIndices[l]);
  }

  // !! Static version for use with new March 2025 parsers
  public static String lexemeOfBuiltin(LexemeKind kind, String inputString, int startIndex) {
    switch (kind) {
    case ID: {
      int right = startIndex;
      while (right < inputString.length()
          && (Character.isAlphabetic(inputString.charAt(right)) || Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;

      return inputString.substring(startIndex, right);
    }
    case CHARACTER:
      return inputString.substring(startIndex + 1, startIndex + 2);
    case CHAR_BQ:
      return inputString.substring(startIndex + 1, startIndex + 2);
    case COMMENT_BLOCK_C:
      break;
    case COMMENT_LINE_C:
      break;
    case COMMENT_NEST_ART:
      break;
    case INTEGER: {
      int right = startIndex;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(startIndex, right);
    }
    case SIGNED_INTEGER: {
      int right = startIndex;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(startIndex, right);
    }
    case AP_INTEGER: {
      int right = startIndex + 1; // skip £
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case REAL: {
      int right = startIndex;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(startIndex, right);
    }
    case SIGNED_REAL: {
      int right = startIndex;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(startIndex, right);
    }
    case AP_REAL: {
      int right = startIndex + 1;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case SIMPLE_WHITESPACE:
      break;
    case SINGLETON_CASE_INSENSITIVE:
      break;
    case SINGLETON_CASE_SENSITIVE:
      break;
    case STRING_PLAIN_SQ: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '\'')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_DQ: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '\"')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_BRACE_NEST: {
      int level = 1, right = startIndex + 1;
      while (level != 0) {
        if (inputString.charAt(right) == '{')
          level++;
        else if (inputString.charAt(right) == '}') level--;
        right++;
      }
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_BRACKET_NEST: {
      int level = 1, right = startIndex + 1;
      while (level != 0) {
        if (inputString.charAt(right) == '<')
          level++;
        else if (inputString.charAt(right) == '>') level--;
        right++;
      }
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_DOLLAR: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '$')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_SHRIEK_SHRIEK: {
      int right = startIndex + 2;
      while (!(inputString.charAt(right) == '!' && inputString.charAt(right + 1) == '!'))
        right++;
      return inputString.substring(startIndex + 2, right);
    }
    case STRING_SQ: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '\'')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    }
    return "unprocessed lexeme";
  }

  public String lexemeOfBuiltin(LexemeKind kind, int startIndex) {
    switch (kind) {
    case ID: {
      int right = startIndex;
      while (right < inputString.length()
          && (Character.isAlphabetic(inputString.charAt(right)) || Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;

      return inputString.substring(startIndex, right);
    }
    case CHARACTER:
      return inputString.substring(startIndex + 1, startIndex + 2);
    case CHAR_BQ:
      return inputString.substring(startIndex + 1, startIndex + 2);
    case COMMENT_BLOCK_C:
      break;
    case COMMENT_LINE_C:
      break;
    case COMMENT_NEST_ART:
      break;
    case INTEGER: {
      int right = startIndex;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(startIndex, right);
    }
    case SIGNED_INTEGER: {
      int right = startIndex;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(startIndex, right);
    }
    case AP_INTEGER: {
      int right = startIndex + 1; // skip £
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case REAL: {
      int right = startIndex;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(startIndex, right);
    }
    case SIGNED_REAL: {
      int right = startIndex;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(startIndex, right);
    }
    case AP_REAL: {
      int right = startIndex + 1;
      if (inputString.charAt(right) == '-') right++;
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      right++; // skip decimal point
      while (right < inputString.length() && Character.isDigit(inputString.charAt(right)))
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case SIMPLE_WHITESPACE:
      break;
    case SINGLETON_CASE_INSENSITIVE:
      break;
    case SINGLETON_CASE_SENSITIVE:
      break;
    case STRING_PLAIN_SQ: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '\'')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_DQ: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '\"')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_BRACE_NEST: {
      int level = 1, right = startIndex + 1;
      while (level != 0) {
        if (inputString.charAt(right) == '{')
          level++;
        else if (inputString.charAt(right) == '}') level--;
        right++;
      }
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_BRACKET_NEST: {
      int level = 1, right = startIndex + 1;
      while (level != 0) {
        if (inputString.charAt(right) == '<')
          level++;
        else if (inputString.charAt(right) == '>') level--;
        right++;
      }
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_DOLLAR: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '$')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    case STRING_SHRIEK_SHRIEK: {
      int right = startIndex + 2;
      while (!(inputString.charAt(right) == '!' && inputString.charAt(right + 1) == '!'))
        right++;
      return inputString.substring(startIndex + 2, right);
    }
    case STRING_SQ: {
      int right = startIndex + 1;
      while (inputString.charAt(right) != '\'')
        right++;
      return inputString.substring(startIndex + 1, right);
    }
    }
    return "unprocessed lexeme";
  }

  public boolean match(CFGNode gn, int tokenIndex) {
    return tokens[tokenIndex] == gn.element.number;
  }

}
