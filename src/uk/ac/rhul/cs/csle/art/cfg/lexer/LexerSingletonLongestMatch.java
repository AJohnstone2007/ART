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

    // System.out.println("Starting lexing with kinds/string arrays:");
    // for (int i = 0; i < kinds.length; i++)
    // System.out.println(kinds[i] + "/" + strings[i]);

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

  @Override
  public void printLexicalisations() {
    // System.out.println("String: " + inputString);
    // var lexemeKinds = LexemeKind.values();
    // for (int i = 0; i < lexemeKinds.length; i++)
    // System.out.println(lexemeKinds[i]);
    // int index = 0;
    for (int i = 0; i < tokens.length; i++)
      System.out.println(i + ":" + leftIndices[i] + "," + rightIndices[i] + " " + kinds[tokenList.get(i)] + " " + strings[tokens[i]]);
  }
}