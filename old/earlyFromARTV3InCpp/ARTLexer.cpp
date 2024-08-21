#include<stdlib.h>
#include<stdio.h>
#include<ctype.h>
#include<string.h>
#define String const char*
#define public
#define private
#define static
#define final
#define boolean bool
#define null NULL
#define throws
#define FileNotFoundException

  private boolean isSubstringAt(const char* full, int start, const char* sub) {
    int subStart = 0;

    while (full[start] != 0 && sub[subStart] != 0 && full[start] == sub[subStart]) {
      start++;
      subStart++;
    }

    return sub[subStart] == 0;
  }

  private String charsToString(const char* c) {
    String ret = "";
    for (int i = 0; c[i] != 0; i++)
      ret += c[i];
    return ret;
  }

    int inputLengthFromLexer;
    int* dynamicLexicaliseLongestMatch(String input, int startIndex) {
    // End of language customisation

    int inputStringLength = strlen(input);
    int* ret = null;
    int stringStart, retIndex;

    for (int pass = 0; pass < 2; pass++) {
      retIndex = stringStart = 0;
      for (int i = startIndex; i > 0; i--) {
        if (ret != null) ret[retIndex] = eoS; // Dummy EoS at element zero which is not used for Earley
        retIndex++;
      }

      int longestTerminal, longestTerminalLength;

      while (stringStart < inputStringLength && isspace(input[stringStart]))
        stringStart++;

      while (stringStart < inputStringLength) {
        longestTerminal = longestTerminalLength = 0;
        for (int t = eoS + 1; t < epsilon; t++) {

          if (isSubstringAt(input, stringStart, symbolStrings[t])) {
            if (strlen(symbolStrings[t]) > longestTerminalLength) {
              longestTerminal = t;
              longestTerminalLength = strlen(symbolStrings[t]);
            }
          }
        }
        if (ret != null) ret[retIndex] = longestTerminal;
        if (longestTerminal == 0) return null; // lexicalisation error
        stringStart += strlen(symbolStrings[longestTerminal]);
        // Just do whitespace for all terminals in this version... if (!(ret[retIndex]
        // instanceof ARTGrammarElementTerminalCharacter))
        while (stringStart < inputStringLength && isspace(input[stringStart]))
          stringStart++;
        retIndex++;
      }
      // set a_{n+1} = $
      if (ret != null) ret[retIndex] = eoS;
      retIndex++;

      if (ret == null) ret = new int[retIndex];
    }
    inputLengthFromLexer = retIndex;
    return ret;
  }

