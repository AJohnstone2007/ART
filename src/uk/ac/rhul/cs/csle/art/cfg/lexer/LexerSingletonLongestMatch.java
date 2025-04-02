package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.util.Util;

public class LexerSingletonLongestMatch extends AbstractParser {
  private ArrayList<Integer> tokenList;
  private ArrayList<Integer> leftIndexList;
  private ArrayList<Integer> rightIndexList;
  public int[] tokens;
  public int[] leftIndices;
  public int[] rightIndices;
  public Integer deleteTokenCount = 0;
  public Integer swapTokenCount = 0;

  @Override
  public void lex(String inputString, LexemeKind[] kinds, String[] strings, LexemeKind[] whitespaces) {
    this.inputString = inputString + "\0";
    this.kinds = kinds;
    this.strings = strings;
    this.whitespaces = whitespaces;

    // System.out.println("Starting lexing with kinds/string arrays:");
    // for (int i = 0; i < kinds.length; i++)
    // System.out.println(kinds[i] + "/" + strings[i]);

    inputAsCharArray = inputString.toCharArray();
    lexerInputIndex = 0;
    inputLength = inputString.length();

    tokenList = new ArrayList<>();
    leftIndexList = new ArrayList<>();
    rightIndexList = new ArrayList<>();
    tokens = leftIndices = null;

    longestMatchRightIndex = 0;
    longestMatchToken = 0;
    // System.out.println("Input: " + inputString);

    while (lexerInputIndex < inputAsCharArray.length) {
      // Absorb a run of whitespace tokens
      // System.out.println("Absorbing WS at index " + inputIndex);
      while (true) {
        int wsStart = lexerInputIndex;

        for (LexemeKind w : whitespaces)
          processBuiltin(w, null);
        if (lexerInputIndex == wsStart) break;
      }

      if (lexerInputIndex == inputLength) break;

      leftIndex = lexerInputIndex;

      // System.out.println("Running recognisers at index " + inputIndex);
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
      Util.trace(6, 0, "Deleting " + deleteTokenCount + " tokens - original size " + tokenList.size() + "\n");
      int leftIndex = tokenList.size() / 2 - deleteTokenCount / 2;
      tokenList.subList(leftIndex, leftIndex + deleteTokenCount).clear();
      Util.trace(6, 0, "Deleted " + deleteTokenCount + " tokens - final size " + tokenList.size() + "\n");
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

  @Override
  public void printLexicalisations(boolean raw) {
    // System.out.println("String: " + inputString);
    // var lexemeKinds = LexemeKind.values();
    // for (int i = 0; i < lexemeKinds.length; i++)
    // System.out.println(lexemeKinds[i]);
    // int index = 0;
    if (raw)
      for (int i = 0; i < tokens.length; i++)
        System.out.println(kinds[tokenList.get(i)] == LexemeKind.SINGLETON_CASE_SENSITIVE ? strings[tokens[i]] : kinds[tokenList.get(i)]);
    else
      for (int i = 0; i < tokens.length; i++)
        System.out.println(i + ":" + leftIndices[i] + "," + rightIndices[i] + " " + kinds[tokenList.get(i)] + " " + strings[tokens[i]]);
  }
}