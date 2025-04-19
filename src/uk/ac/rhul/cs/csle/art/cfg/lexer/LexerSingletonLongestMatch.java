package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class LexerSingletonLongestMatch extends AbstractLexer {
  @Override
  public void lex(String inputString, CFGRules cfgRules) {
    this.inputString = inputString + "\0";
    this.cfgRules = cfgRules;

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

        for (TokenKind w : cfgRules.whitespaces)
          processBuiltin(w, null);
        if (lexerInputIndex == wsStart) break;
      }

      if (lexerInputIndex == inputLength) break;

      leftIndex = lexerInputIndex;

      // Util.info("Running recognisers at index " + inputIndex);
      for (int token = 1; token < cfgRules.lexicalKindsArray.length; token++) {
        lexerInputIndex = leftIndex;
        processBuiltin(cfgRules.lexicalKindsArray[token], cfgRules.lexicalStringsArray[token]);
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

  @Override
  public void printLexicalisations(boolean raw) {
    // Util.info("String: " + inputString);
    // var lexemeKinds = LexemeKind.values();
    // for (int i = 0; i < lexemeKinds.length; i++)
    // Util.info(lexemeKinds[i]);
    // int index = 0;
    if (raw)
      for (int i = 0; i < tokens.length; i++)
        Util.info(cfgRules.lexicalKindsArray()[tokenList.get(i)] == TokenKind.SINGLETON_CASE_SENSITIVE ? cfgRules.lexicalStringsArray()[tokens[i]]
            : cfgRules.lexicalKindsArray()[tokenList.get(i)].toString());
    else
      for (int i = 0; i < tokens.length; i++)
        Util.info(i + ":" + leftIndices[i] + "," + rightIndices[i] + " " + cfgRules.lexicalKindsArray[tokenList.get(i)] + " "
            + cfgRules.lexicalStringsArray[tokens[i]]);
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