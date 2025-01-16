package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;

public class LexerSingletonLongestMatch extends AbstractParser {
  private ArrayList<Integer> tokenList;
  private ArrayList<Integer> leftIndexList;
  private ArrayList<Integer> rightIndexList;
  public int[] tokens;
  public int[] leftIndices;
  public int[] rightIndices;

  @Override
  public void lex(String inputString, LexemeKind[] kinds, String[] strings, LexemeKind[] whitespaces) {
    this.inputString = inputString + "\0";
    this.kinds = kinds;
    this.strings = strings;
    this.whitespaces = whitespaces;

    inputAsCharArray = inputString.toCharArray();
    inputIndex = 0;
    inputLength = inputString.length();

    tokenList = new ArrayList<>();
    leftIndexList = new ArrayList<>();
    rightIndexList = new ArrayList<>();
    tokens = leftIndices = null;

    longestMatchRightIndex = 0;
    longestMatchToken = 0;
    // System.out.println("Input: " + inputString);

    while (inputIndex < inputAsCharArray.length) {
      // Absorb a run of whitespace tokens
      while (true) {
        int wsStart = inputIndex;

        for (LexemeKind w : whitespaces)
          processBuiltin(w, null);
        if (inputIndex == wsStart) break;
      }

      if (inputIndex == inputLength) break;

      leftIndex = inputIndex;

      for (int token = 1; token < kinds.length; token++) {
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
      inputIndex = longestMatchRightIndex;
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
  }

  private void checkLongestMatch(int token) {
    if (inputIndex > longestMatchRightIndex) {
      longestMatchRightIndex = inputIndex;
      longestMatchToken = token;
      inputIndex = leftIndex;
    }
  }

  public void report() {
    System.out.println("String: " + inputString);
    System.out.print("Token names: ");
    int index = 0;
    for (int i : tokenList)
      System.out.print((index++) + ":" + strings[i] + " ");
    System.out.println();

    System.out.print("Tokens: ");
    for (int i : tokenList)
      System.out.print(i + " ");
    System.out.println();

    System.out.print("Left indices: ");
    for (int i : leftIndices)
      System.out.print(i + " ");
    System.out.println();

    System.out.print("Right indices:");
    for (int i : rightIndices)
      System.out.print(" " + i);
    System.out.println();

    System.out.print("Bare lexemes:");
    for (int i = 0; i < tokens.length; i++)
      System.out.print(" '" + inputString.substring(leftIndices[i], rightIndices[i]) + "'");
    System.out.println();
  }
}