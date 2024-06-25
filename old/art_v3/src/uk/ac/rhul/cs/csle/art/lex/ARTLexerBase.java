package uk.ac.rhul.cs.csle.art.lex;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLParserBase;
import uk.ac.rhul.cs.csle.art.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalBuiltin;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalCaseInsensitive;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalCaseSensitive;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalCharacter;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementTerminalFromNonterminal;
import uk.ac.rhul.cs.csle.art.util.ARTBitSet;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerFile;

public class ARTLexerBase {

  /******************************************************************************
   *
   * 1. Data
   *
   ******************************************************************************/
  private ARTGrammar grammar; // The grammar containing the lexical definitions
  protected ARTParserBase artParser;

  public int artTokenMax; // The value of the epsilon token which comes after the last token in the symbol enumeration

  public String artInputString; // The input as a String
  public char[] artInput; // The input as an array of characters zero terminated for compatability with C-style strings
  public int artInputLength; // length of input in characters
  public int artInputIndex; // Index into character string input used by lexers
  public int artLexemeLeftIndex; // During longest match lexing, remember start of lexeme
  public int artLexemeRightIndex; // During longest match lexing, remember start of lexeme
  int touchedTWESetCardinality;
  private int startOffset; // The index of the first nonwhitespace terminal in the string

  private String[] labelStrings;

  public ARTTWEPairSet tweSet[];
  private int overDegree[];
  private boolean touched[];

  // The priority and longer relations represented as boolean vectors
  private ARTBitSet[] higher;
  private ARTBitSet[] longer;

  // Data for instumentation of TWE set behaviour
  private Set<ArrayList<Integer>> lexicalisations = new HashSet<>();
  private int[] segmentLexicalisationCardinalities;
  private int[] segmentLexicalisationLengths;
  private BigInteger[] segmentLexicalisationProductBars;

  // Control flags for the TWEset lexer phases
  private boolean lexExtents;
  private boolean lexSegments;
  private boolean lexDead;
  private boolean longestWithin;
  private boolean longestAcross;
  private boolean priority;
  private boolean lexRecursive;
  public boolean lexWSSuffix = true;
  public boolean printTWESet;

  private final static Set<String> validBuiltins = new HashSet<>(Arrays.asList("CHAR_BQ", "COMMENT_BLOCK_C", "COMMENT_LINE_C", "COMMENT_NEST_ART", "ID", "ID_A",
      "ID_AN", "ID_SOS", "INTEGER", "REAL", "STRING_BRACE", "STRING_BRACE_NEST", "STRING_DOLLAR", "STRING_DQ", "STRING_PLAIN_SQ", "STRING_SQ", "WHITESPACE"));

  /******************************************************************************
   *
   * 2. Constructors and initialisation functions
   *
   ******************************************************************************/
  public ARTLexerBase() { // Dummy constrictor to satisfy ARTLexer
  }

  public ARTLexerBase(ARTGrammar grammar) {
    this.grammar = grammar;
    this.higher = grammar.getHigher();
    this.longer = grammar.getLonger();
    this.lexExtents = grammar.getOptionBlock().lexExtents;
    this.lexSegments = grammar.getOptionBlock().lexSegments;
    this.lexDead = grammar.getOptionBlock().lexDead;
    this.longestWithin = grammar.getOptionBlock().lexLongestWithin;
    this.longestAcross = grammar.getOptionBlock().lexLongestAcross;
    this.priority = grammar.getOptionBlock().lexPriority;
    this.lexRecursive = grammar.getOptionBlock().lexRecursive;
    this.lexWSSuffix = grammar.getOptionBlock().lexWSSuffix;
    this.printTWESet = grammar.getOptionBlock().lexPrintTWESet;
  }

  // This constructor is used by the SPPF traverser
  public ARTLexerBase(ARTBitSet[] higher, ARTBitSet[] longer, String inputString, int eos, String[] labelStrings, boolean longestWithin, boolean longestAcross,
      boolean priority) {
    this.labelStrings = labelStrings;

    artLoadInputArray(inputString);

    // Build empty TWE set from the left
    tweSet = new ARTTWEPairSet[inputString.length() + 1]; // Rightmost position contains $ (zero) and then we need an empty position for successor
    touched = new boolean[inputString.length() + 1];
    tweSetUpdateExactMakeLeftSet(eos, inputString.length(), inputString.length()); // Add terminating EOS
    this.grammar = null;
    this.higher = higher;
    this.longer = longer;
    this.lexExtents = true;
    this.lexSegments = true;
    this.lexDead = false;
    this.longestWithin = longestWithin;
    this.longestAcross = longestAcross;
    this.priority = priority;
    this.lexRecursive = false;
    this.lexWSSuffix = true;
    this.printTWESet = true;
  }

  public void artSetParser(ARTParserBase artGeneratedParser) {
    this.artParser = artGeneratedParser;
  }

  public void artLoadInputArray(String inputString) {
    // Build artInput as char[]
    this.artInput = new char[inputString.length() + 2]; // Rightmost position contains $ (zero) and then we need an empty position for successor
    for (int i = 0; i < inputString.length(); i++)
      artInput[i] = inputString.charAt(i);

    artInputLength = artInput.length - 1;
  }

  /******************************************************************************
   *
   * 3. Overload hooks and support routines for generated lexers which extend this class
   *
   ******************************************************************************/
  public int artLongestLength; // Used during longest match processing to remember the longest lexeme seen so far
  public int artLongestToken; // Used during longest match processing to remember a token which has the longest lexeme seen so far

  protected void artLexicalisePreparseWhitespaceInstances() throws ARTException {
    throw new ARTException("artLexicalisePreparseWhitespaceInstances in base class called - must be overridden");
  };

  protected void artLexicaliseBuiltinInstances() throws ARTException {
    throw new ARTException("artLexicaliseBuiltinInstances in base class called - must be overridden");
  };

  public int artLexicalisePreparseWhitespace() throws ARTException {
    // System.out.printf("Preparse whitespace entered at index %d", artInputIndex);
    // if (artInputIndex < artInputLength - 5) System.out.printf(" - \"%c%c%c%c%c...", artInput[artInputIndex + 0], artInput[artInputIndex + 1],
    // artInput[artInputIndex + 2], artInput[artInputIndex + 3], artInput[artInputIndex + 4]);
    // System.out.println();
    int startOrg = artInputIndex;
    int start;
    do {
      if (artPeekCh() == '\0') break;
      start = artInputIndex;

      artLexicalisePreparseWhitespaceInstances();

    } while (artInputIndex != start);

    // System.out.printf("Preparse whitespace matched \"%s\"\n", artInputString.substring(startOrg, artInputIndex));
    return -1;
  }

  protected void artLexicaliseTest(int token) {
    if (artInputIndex > artLexemeRightIndex) {
      artLexemeRightIndex = artInputIndex;
      artLongestToken = token;
    }
    artInputIndex = artLexemeLeftIndex; // reset input pointer
  }

  protected void artUpdateLongestLength(int length, int tokenIndex) {
    if (length > artLongestLength) {
      artLongestLength = length;
      artLongestToken = tokenIndex;
    }
  }

  public void artMatchLongestRaw() throws ARTException {
    artLexemeRightIndex = artLexemeLeftIndex = artInputIndex;

    if (artPeekCh() == '\0') {
      artLexemeRightIndex = artInputLength;
      artLongestToken = ARTGLLParserBase.ARTL_EOS;
      return;
    }

    // In this loop, artCharacterStringInputIndex does not advance becase we are using regionMatches
    for (int tokenIndex = artParser.artFirstTerminalLabel; tokenIndex < ARTGLLParserBase.ARTL_EPSILON; tokenIndex++) {
      if (!artInputString.regionMatches(artParser.artTerminalCaseInsensitive[tokenIndex], artInputIndex, artParser.artLabelStrings[tokenIndex], 0,
          artParser.artLabelStrings[tokenIndex].length()))
        continue;
      artLexemeRightIndex = artInputIndex + artParser.artLabelStrings[tokenIndex].length();
      artLongestToken = tokenIndex;
    }

    artLexicaliseBuiltinInstances();
    if (artLexemeLeftIndex == artLexemeRightIndex) {
      throw new ARTException(
          ARTText.echo("Lexical error: unexpected character '" + artInput[artInputIndex] + "' (character code " + (int) artInput[artInputIndex] + ")",
              artInputIndex, artInputString));
    }

    // Now we have found a lexeme, so jump the input index forward
    // System.out.println("Matched token " + artLongestToken + ": " + artParser.artLabelStrings[artLongestToken] + " with lexeme "
    // + artCharacterStringInput.substring(artLexemeLeftIndex, artLexemeRightIndex));
    artInputIndex = artLexemeRightIndex;
  }

  public int artLexicalTrim(int startIndex) throws ARTException {
    artInputIndex = startIndex;
    artLexicalisePreparseWhitespace();
    return artInputIndex;
  }

  /******************************************************************************
   *
   * 4. The new TWE set lexer
   *
   ******************************************************************************/
  // This wraps an instance of the lexer in a function that will create an array based version of the resulting TWE set
  // First dimension is the index into the input
  // Second dimension is the token
  // Third dimension is the sequence of right extents
  public int[][][] lexicaliseToIndexedTWESet(String inputString) throws ARTException {
    lexicaliseToLinkedTWESet(inputString);
    int[][][] ret = new int[tweSet.length][][];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = new int[grammar.getEpsilon().getElementNumber() + 1][];
      for (int token = 0; token < ret[i].length; token++) {
        int tokenCount = 0;
        for (ARTTWEPair tweElement : tweSet[i].map.keySet())
          if (!tweElement.suppressed && tweElement.token == token) tokenCount++;
        if (tokenCount > 0) {
          ret[i][token] = new int[tokenCount];
          tokenCount = 0;
          for (ARTTWEPair tweElement : tweSet[i].map.keySet())
            if (!tweElement.suppressed && tweElement.token == token) ret[i][token][tokenCount++] = tweElement.rightExtent;
        }
      }
    }

    if (printTWESet) printIndexedTWESet(ret);
    return ret;
  }

  public void lexicaliseToLinkedTWESet(String inputString) throws ARTException {

    artLoadInputArray(inputString);

    // Build empty TWE set from the left
    tweSet = new ARTTWEPairSet[inputString.length() + 2]; // Rightmost position contains $ (zero) and then we need an empty position for successor
    touched = new boolean[tweSet.length]; // updated by traverser whenever it lands somewhere

    artInputIndex = 0;

    while (true) {
      int startIndex = artInputIndex;
      for (ARTGrammarElementTerminal t : grammar.getWhitespaces()) {
        artInputIndex = startIndex;
        if (t.getId().equals("COMMENT_NEST_ART")) {
          artBuiltin_COMMENT_NEST_ART();
          if (artInputIndex > startIndex) break;
        } else if (t.getId().equals("COMMENT_LINE_C")) {
          artBuiltin_COMMENT_LINE_C();
          if (artInputIndex > startIndex) break;
        } else if (t.getId().equals("COMMENT_BLOCK_C")) {
          artBuiltin_COMMENT_BLOCK_C();
          if (artInputIndex > startIndex) break;
        } else if (t.getId().equals("WHITESPACE")) {
          artBuiltin_WHITESPACE();
          if (artInputIndex > startIndex) break;
        }
      }
      if (artInputIndex <= startIndex) break;
    }

    if (artInputIndex > 0) {
      startOffset = artInputIndex;
    } else
      startOffset = 0;

    tweSet[startOffset] = new ARTTWEPairSet();

    for (int i = startOffset; i < tweSet.length; i++)
      if (tweSet[i] != null) runRecognisers(i);

    // Normalise the TWE set by putting an empty set into each null position
    for (int i = 0; i < tweSet.length; i++)
      if (tweSet[i] == null) tweSet[i] = new ARTTWEPairSet();

    tweSetUpdateExactMakeRightSet(grammar.getEoS().getElementNumber(), artInput.length - 2, artInput.length - 1); // Add terminating EOS
    runSuppressors();
  }

  void runRecognisers(int i) throws ARTException {
    artLexemeLeftIndex = i;
    if (!lexWSSuffix) for (ARTGrammarElementTerminal w : grammar.getWhitespaces()) {
      artInputIndex = i;
      // System.out.println("Testing at index " + i + " whitespace terminal " + w);
      if (w.getId().equals("COMMENT_NEST_ART")) {
        artBuiltin_COMMENT_NEST_ART();
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(w.getElementNumber(), i, artInputIndex);
      } else if (w.getId().equals("COMMENT_LINE_C")) {
        artBuiltin_COMMENT_LINE_C();
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(w.getElementNumber(), i, artInputIndex);
      } else if (w.getId().equals("COMMENT_BLOCK_C")) {
        artBuiltin_COMMENT_BLOCK_C();
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(w.getElementNumber(), i, artInputIndex);
      } else if (w.getId().equals("WHITESPACE")) {
        artBuiltin_WHITESPACE();
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(w.getElementNumber(), i, artInputIndex);
      }
    }
    if (artInputIndex > i) return; // We found a whitespaceterminal

    for (ARTGrammarElementTerminal t : grammar.getTerminals()) {
      // System.out.println("Testing at index " + i + " terminal " + t);
      artInputIndex = i;

      if (t instanceof ARTGrammarElementTerminalCaseSensitive) {
        artBuiltin_SINGLETON_CASE_SENSITIVE(t.getId());
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
      }

      else if (t instanceof ARTGrammarElementTerminalCaseInsensitive) {
        artBuiltin_SINGLETON_CASE_INSENSITIVE(t.getId());
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
      }

      else if (t instanceof ARTGrammarElementTerminalCharacter) {
        artBuiltin_SINGLETON_CASE_SENSITIVE(t.getId());
        if (testMatch()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
      }

      // These are here for backwards compatibility and will be removed whenbuiltins are taken out
      else if (t instanceof ARTGrammarElementTerminalBuiltin) {
        if (t.getId().equals("STRING_SQ")) {
          artBuiltin_STRING_SQ();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("STRING_DQ")) {
          artBuiltin_STRING_DQ();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("ID")) {
          artBuiltin_ID();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("INTEGER")) {
          artBuiltin_INTEGER();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("REAL")) {
          artBuiltin_REAL();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex, '.');
        } else if (t.getId().equals("COMMENT_NEST_ART")) {
          artBuiltin_COMMENT_NEST_ART();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_LINE_C")) {
          artBuiltin_COMMENT_LINE_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_BLOCK_C")) {
          artBuiltin_COMMENT_BLOCK_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("WHITESPACE")) {
          artBuiltin_WHITESPACE();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        }
      } else if (t instanceof ARTGrammarElementTerminalFromNonterminal) {
        if (t.getId().equals("STRING_SQ")) {
          artBuiltin_STRING_SQ();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("STRING_DQ")) {
          artBuiltin_STRING_DQ();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("ID")) {
          artBuiltin_ID();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("INTEGER")) {
          artBuiltin_INTEGER();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("REAL")) {
          artBuiltin_REAL();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_NEST_ART")) {
          artBuiltin_COMMENT_NEST_ART();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_LINE_C")) {
          artBuiltin_COMMENT_LINE_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_BLOCK_C")) {
          artBuiltin_COMMENT_BLOCK_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("WHITESPACE")) {
          artBuiltin_WHITESPACE();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        }
      }
    }
  }

  private boolean testMatch() {
    return artInputIndex > artLexemeLeftIndex;
  }

  private boolean testMatchAndOptionallyConsumeWS() throws ARTException {
    if (artInputIndex <= artLexemeLeftIndex) return false;
    if (lexWSSuffix) { // consume until all fail
      while (true) {
        int startIndex = artInputIndex;
        for (ARTGrammarElementTerminal t : grammar.getWhitespaces()) {
          artInputIndex = startIndex;
          if (t.getId().equals("COMMENT_NEST_ART")) {
            artBuiltin_COMMENT_NEST_ART();
            if (artInputIndex > startIndex) break;
          } else if (t.getId().equals("COMMENT_LINE_C")) {
            artBuiltin_COMMENT_LINE_C();
            if (artInputIndex > startIndex) break;
          } else if (t.getId().equals("COMMENT_BLOCK_C")) {
            artBuiltin_COMMENT_BLOCK_C();
            if (artInputIndex > startIndex) break;
          } else if (t.getId().equals("WHITESPACE")) {
            artBuiltin_WHITESPACE();
            if (artInputIndex > startIndex) break;
          }
        }
        if (artInputIndex <= startIndex) break;
      }
    }
    return true;
  }

  public void runSuppressors() {
    // Remove dead ends
    if (lexDead) {
      System.out.println("Suppressing dead branches");
      for (int i = tweSet.length - 3; i >= 0; i--)

        for (ARTTWEPair e : tweSet[i].map.keySet())
          if (isVacuous(tweSet[e.rightExtent])) e.suppressed = true;
    }

    // Apply longest match within tokens
    if (longestWithin) {
      System.out.println("Longest match within tokens");
      for (int i = tweSet.length - 3; i >= 0; i--)

        if (tweSet[i].map.size() > 0) for (ARTTWEPair e : tweSet[i].map.keySet())
          for (ARTTWEPair f : tweSet[i].map.keySet()) {
            if (longer[e.token] != null && longer[e.token].get(f.token) && (e.rightExtent > f.rightExtent)) {
              System.out.println("longestWithin suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against " + tokenToString(e.token)
                  + ", " + i + ", " + e.rightExtent);
              f.suppressed = true;
            }
            // else {
            // System.out.println("longestWithin NOT suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against "
            // + tokenToString(e.token) + ", " + i + ", " + e.rightExtent);
            // System.out.println("Bang!");
            // }
          }
    }
    // Apply longest match across tokens
    if (longestAcross) {
      System.out.println("Longest match across tokens");
      for (int i = tweSet.length - 3; i >= 0; i--)

        if (tweSet[i].map.size() > 0) for (ARTTWEPair e : tweSet[i].map.keySet())
          for (ARTTWEPair f : tweSet[i].map.keySet())
            if ((e.rightExtent > f.rightExtent)) {
              f.suppressed = true;
              System.out.println("longestAcross suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against " + tokenToString(e.token)
                  + ", " + i + ", " + e.rightExtent);
            }
    }
    // Apply priorities
    if (priority) {
      System.out.println("Priority");
      // System.out.println("Priority relation is: ");
      // for (int i = 0; i < higher.length; i++) {
      // System.out.println(tokenToString(i) + ">");
      // if (higher[i] == null) {
      // System.out.println(" null");
      // continue;
      // }
      // for (int j = 0; j < higher[i].length(); j++)
      // if (higher[i].get(j)) System.out.println(tokenToString(j));
      // }

      for (int i = tweSet.length - 3; i >= 0; i--) {
        if (tweSet[i].map.size() > 0) for (ARTTWEPair e : tweSet[i].map.keySet())
          for (ARTTWEPair f : tweSet[i].map.keySet()) {
            if (e.rightExtent == f.rightExtent && higher[e.token] != null && higher[e.token].get(f.token)) {
              System.out.println("priority suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against " + tokenToString(e.token)
                  + ", " + i + ", " + e.rightExtent);
              f.suppressed = true;
            }
            // else
            // System.out.println("priority NOT suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against " + tokenToString(e.token)
            // + ", " + i + ", " + e.rightExtent);

          }
      }
    }
  }

  /******************************************************************************
   *
   * 4B. Old lexers
   *
   ******************************************************************************/
  // This lexer was moved over from ARTParserBase and is used by CNPIndexed and CNPGenerated variants
  public static int[] lexicaliseToArrayOfint(String input, int startIndex, String[] symbolStrings, int epsilon) {
    int eoS = 0;
    ArrayList<Integer> ret = new ArrayList<>();

    int stringStart = 0, longest, retIndex = 0;

    for (int i = startIndex; i > 0; i--)
      ret.add(retIndex++, eoS); // Dummy EoS at element zero which is not used for Earley

    int longestTerminal = 0;

    while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
      stringStart++;

    while (stringStart < input.length()) {
      longest = 0;
      for (int t = 1; t < epsilon; t++)
        if (input.regionMatches(stringStart, symbolStrings[t], 0, symbolStrings[t].length()) && symbolStrings[t].length() > longest) {
          longest = symbolStrings[t].length();
          longestTerminal = t;
        }
      ret.add(retIndex, longestTerminal);
      // System.out.println("After lexing:" + ret + " with retIndex = " + retIndex);
      if (longest == 0) return null; // lexicalisation error
      stringStart += longest;
      // !! We don't have kinds in the slots at the moment
      // if (!(ret.get(retIndex) instanceof ARTGrammarElementTerminalCharacter))
      while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
        stringStart++;
      retIndex++;
    }
    // set a_{n+1} = $
    ret.add(retIndex, eoS);
    // System.out.println("Completed lexing:" + ret);
    int[] retArray = new int[ret.size()];

    for (int i = 0; i < ret.size(); i++)
      retArray[i] = ret.get(i);

    return retArray;
  }

  // This is a quick and dirty lexicaliser which does not support ART's special lexical features - first token at element [1]
  // Used by CNPLinkedAPI
  public ArrayList<ARTGrammarElementTerminal> lexicaliseToArrayListOfTerminals(String input, int startIndex) {
    ArrayList<ARTGrammarElementTerminal> ret = new ArrayList<>();

    int stringStart = 0, longest, retIndex = 0;

    for (int i = startIndex; i > 0; i--)
      ret.add(retIndex++, grammar.getEoS()); // Dummy EoS at element zero which is not used for Earley

    ARTGrammarElementTerminal longestTerminal = null;

    while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
      stringStart++;

    while (stringStart < input.length()) {
      longest = 0;
      for (ARTGrammarElementTerminal t : grammar.getTerminals())
        if (input.regionMatches(stringStart, t.getId(), 0, t.getId().length()) && t.getId().length() > longest) {
          longest = t.getId().length();
          longestTerminal = t;
        }
      ret.add(retIndex, longestTerminal);
      // System.out.println("After lexing:" + ret + " with retIndex = " + retIndex);
      if (longest == 0) return null; // lexicalisation error
      stringStart += longest;
      if (!(ret.get(retIndex) instanceof ARTGrammarElementTerminalCharacter))
        while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
        stringStart++;
      retIndex++;
    }
    // set a_{n+1} = $
    ret.add(retIndex, grammar.getEoS());
    // System.out.println("Completed lexing:" + ret);
    return ret;
  }

  /******************************************************************************
   *
   * 4. TWE set maintenance and debug
   *
   ******************************************************************************/
  public boolean tweSetContains(int token, int left, int right) {
    if (tweSet[left] == null) return false;

    ARTTWEPair p = new ARTTWEPair(token, right);

    return tweSet[left].map.get(p) != null;
  }

  public void tweSetUpdateExactMakeLeftSet(int token, int left, int right) {
    // System.out.println("Update exact");
    if (tweSet[left] == null) tweSet[left] = new ARTTWEPairSet();
    ARTTWEPair p = new ARTTWEPair(token, right);
    tweSet[left].map.put(p, p);
  }

  public void tweSetUpdateExactMakeRightSet(int token, int left, int right) {
    // System.out.println("Update exact");
    ARTTWEPair p = new ARTTWEPair(token, right);
    tweSet[left].map.put(p, p);
    if (tweSet[right] == null) tweSet[right] = new ARTTWEPairSet();
  }

  private void tweSetUpdateWithPrefixes(int token, int left, int right) {
    // System.out.println("Update with prefixes");
    /* Modification 21-June-2019: do not add prefixes in the token has been declared as longest match */
    if (longestAcross || longestWithin) if (longer[token] != null && longer[token].get(token)) {
      tweSetUpdateExactMakeRightSet(token, left, right);
      return;
    }

    do {
      tweSetUpdateExactMakeRightSet(token, left, right--);
    } while (left < right);
  }

  private void tweSetUpdateWithPrefixes(int token, int left, int right, char stopChar) {
    // System.out.println("Update with prefixes");
    /* Modification 21-June-2019: do not add prefixes in the token has been declared as longest match */
    if (longestAcross || longestWithin) if (longer[token] != null && longer[token].get(token)) {
      tweSetUpdateExactMakeRightSet(token, left, right);
      return;
    }

    do {
      tweSetUpdateExactMakeRightSet(token, left, right--);
    } while (left < right && artInput[right] != stopChar);
  }

  public boolean isSuppressed(int token, int left, int right) {
    ARTTWEPair p = new ARTTWEPair(token, right);
    return tweSet[left].map.get(p).suppressed;
  }

  public boolean isIn(int token, int left, int right) {
    ARTTWEPair p = new ARTTWEPair(token, right);
    return tweSet[left].map.get(p) != null;
  }

  private boolean isVacuous(ARTTWEPairSet artTWEPairSet) {
    for (ARTTWEPair e : artTWEPairSet.map.keySet())
      if (e.rightExtent >= 0) return false;
    return true;
  }

  /******************************************************************************
   *
   * 5. Lexer data computations
   *
   ******************************************************************************/
  public void computeLexerData() {
    BigInteger segmentLexicalisationCount = null;
    BigInteger segmentLexicalisationLength = BigInteger.ZERO;

    if (lexSegments) {
      segmentLexicalisationCount = runSegments();

      // Compute lexicalisationsPrime
      for (int i = 0; i < tweSet.length; i++)
        if (segmentLexicalisationCardinalities[i] != 0) {
          segmentLexicalisationProductBars[i] = BigInteger.ONE;
          for (int j = 0; j < tweSet.length; j++)
            if (i != j && segmentLexicalisationCardinalities[j] != 0)
              segmentLexicalisationProductBars[i] = segmentLexicalisationProductBars[i].multiply(new BigInteger("" + segmentLexicalisationCardinalities[j]));
        }

      // Compute lexicalisation length
      for (int i = 0; i < tweSet.length; i++)
        if (segmentLexicalisationCardinalities[i] != 0) segmentLexicalisationLength = segmentLexicalisationLength
            .add(new BigInteger("" + segmentLexicalisationLengths[i]).multiply(segmentLexicalisationProductBars[i]));
    }

    if (printTWESet) printTWESet();

    // Now collect lexicalisations iteratively
    BigInteger iterativeLexicalisationCounts[] = new BigInteger[tweSet.length];
    BigInteger iterativeLexicalisationLengths[] = new BigInteger[tweSet.length];

    iterativeLexicalisationCounts[tweSet.length - 2] = BigInteger.ONE;
    for (int i = tweSet.length - 3; i >= 0; i--) {
      iterativeLexicalisationCounts[i] = BigInteger.ZERO;
      for (ARTTWEPair t : tweSet[i].map.keySet())
        if (!t.suppressed) iterativeLexicalisationCounts[i] = iterativeLexicalisationCounts[i].add(iterativeLexicalisationCounts[t.rightExtent]);
    }

    for (int i = 0; i < iterativeLexicalisationLengths.length; i++)
      iterativeLexicalisationLengths[i] = BigInteger.ZERO;

    for (int i = tweSet.length - 3; i >= 0; i--) {
      for (ARTTWEPair t : tweSet[i].map.keySet())
        if (!t.suppressed) iterativeLexicalisationLengths[i] = iterativeLexicalisationLengths[i]
            .add(iterativeLexicalisationLengths[t.rightExtent].add(iterativeLexicalisationCounts[t.rightExtent]));
    }

    System.out.println("Found (iterate) " + iterativeLexicalisationCounts[0] + " token-with-extent lexicalisation"
        + (iterativeLexicalisationCounts[0].equals(BigInteger.ONE) ? "" : "s") + " which is approximately "
        + iterativeLexicalisationCounts[0].toString().charAt(0) + " x 10^" + (iterativeLexicalisationCounts[0].toString().length() - 1));

    System.out.println("Found (iterate) sum over token-with-extent lexicalisation lengths " + iterativeLexicalisationLengths[0] + " which is approximately "
        + iterativeLexicalisationLengths[0].toString().charAt(0) + " x 10^" + (iterativeLexicalisationLengths[0].toString().length() - 1));

    if (iterativeLexicalisationCounts[0].equals(BigInteger.ZERO)) // Eeek: something went wrong
      for (int firstNonEmptyIndex = 0; firstNonEmptyIndex < tweSet.length; firstNonEmptyIndex++)
      if (!iterativeLexicalisationCounts[firstNonEmptyIndex].equals(BigInteger.ZERO)) {
        System.out.println("First nonempty lexicalisation index " + firstNonEmptyIndex);
        break;
      }

    if (lexSegments) System.out.println("Found (segment) " + segmentLexicalisationCount + (lexExtents ? " token-with-extent" : " token-only")
        + " lexicalisation" + (segmentLexicalisationCount.equals(BigInteger.ONE) ? "" : "s") + " which is approximately "
        + segmentLexicalisationCount.toString().charAt(0) + " x 10^" + (segmentLexicalisationCount.toString().length() - 1));

    if (lexSegments) System.out
        .println("Found (segment) sum over" + (lexExtents ? " token-with-extent" : " token-only") + " lexicalisation lengths " + segmentLexicalisationLength
            + " which is approximately " + segmentLexicalisationLength.toString().charAt(0) + " x 10^" + (segmentLexicalisationLength.toString().length() - 1));

    // Now collect the lexicalisations by recursively traversing the full set
    if (lexRecursive) {
      lex = new ArrayList<>(2 * tweSet.length);

      traverseTWESetCollectLexicalisationsRec(0, tweSet.length - 2, lexicalisations);
      System.out.println("Found (recurse) " + lexicalisations.size() + (lexExtents ? " token-with-extent" : " token-only") + " lexicalisation"
          + (lexicalisations.size() == 1 ? "" : "s"));
      if (lexicalisations.size() < 50) for (ArrayList<Integer> l : lexicalisations) {
        if (lexExtents)
          for (int k = 0; k < l.size(); k += 2)
            System.out.print(tokenToString(l.get(k)) + "." + l.get(k + 1) + " ");
        else
          for (int k = 0; k < l.size(); k += 1)
            System.out.print(tokenToString(l.get(k)) + " ");

        System.out.println();

        touchedTWESetCardinality = 0;
        if (lexSegments || lexRecursive) // touched array will have been updated: compute touched cardinality
          for (int i = 0; i < tweSet.length; i++)
          if (touched[i]) for (ARTTWEPair p : tweSet[i].map.keySet())
            if (!p.suppressed) touchedTWESetCardinality++;
      }
    }
  }

  private BigInteger runSegments() {
    // Compute overdegree
    overDegree = new int[tweSet.length];
    for (int i = 0; i < tweSet.length; i++)
      for (ARTTWEPair t : tweSet[i].map.keySet())
        for (int k = i + 1; k < t.rightExtent; k++)
          overDegree[k]++;

    // Compute dominator points
    segmentLexicalisationCardinalities = new int[tweSet.length];
    segmentLexicalisationLengths = new int[tweSet.length];
    segmentLexicalisationProductBars = new BigInteger[tweSet.length];

    for (int i = 0; i < tweSet.length - 1; i++)
      if (tweSet[i].map.keySet() != null && overDegree[i] == 0) {
        segmentLexicalisationCardinalities[i] = 1;
      }

    // Compute lexical segment sets
    BigInteger segmentLexicalisationCount = BigInteger.ONE;

    for (int i = 0; i < tweSet.length - 2; i++) {
      if (segmentLexicalisationCardinalities[i] == 0) continue;
      int j = i + 1;
      while (j < tweSet.length - 2 && segmentLexicalisationCardinalities[j] == 0)
        j++;
      lexicalisations = new HashSet<>();
      // System.out.print("Computing lexicalisations for segment " + i + "..." + j + ": ");
      // for (int k = i; k < j; k++)
      // System.out.print(ARTText.toLiteralString(Character.toString(artInput[k])));
      traverseTWESetCollectLexicalisationsRec(i, j, lexicalisations);
      segmentLexicalisationCardinalities[i] = lexicalisations.size();
      segmentLexicalisationLengths[i] = 0;

      for (ArrayList<Integer> l : lexicalisations)
        segmentLexicalisationLengths[i] += l.size() / (lexExtents ? 2 : 1);

      // System.out.println("found " + segmentLexicalisationCardinalities[i] + " lexicalisation" + (segmentLexicalisationCardinalities[i] == 1 ? "" : "s")
      // + " with total length " + segmentLexicalisationLengths[i]);

      lexicalisations.clear();
      segmentLexicalisationCount = segmentLexicalisationCount.multiply(BigInteger.valueOf(segmentLexicalisationCardinalities[i]));
    }
    return segmentLexicalisationCount;
  }

  /* recusive travesal of TWESet and lexicalisation builder */
  ArrayList<Integer> lex = new ArrayList<>();

  private void traverseTWESetCollectLexicalisationsRec(int i, int j, Set<ArrayList<Integer>> lexicalisations) {
    // System.out.println("traverse: " + i + " - " + j);
    touched[i] = true;
    for (ARTTWEPair e : tweSet[i].map.keySet()) {
      if (i == j) {
        if (!lexicalisations.contains(lex)) lexicalisations.add(new ArrayList<Integer>(lex));
      } else {
        lex.add(e.token);
        if (lexExtents) lex.add(e.rightExtent);
        if (!e.suppressed) traverseTWESetCollectLexicalisationsRec(e.rightExtent, j, lexicalisations); // Ignore supressed tokens
        lex.remove(lex.size() - 1); // Pop
        if (lexExtents) lex.remove(lex.size() - 1);
      }
    }
  }

  /******************************************************************************
   *
   * 6. Output functions
   *
   ******************************************************************************/
  String tokenToString(int token) {
    if (grammar != null) return grammar.getElement(token).toString();

    if (labelStrings != null) return labelStrings[token];

    return Integer.toString(token);
  }

  public void printTWESet() {
    // Now output the TWE set
    int cardinality = 0;
    int cardinalityChoice = 0;

    for (int i = 0; i < tweSet.length; i++) {
      if (tweSet[i] == null) continue;
      cardinality += tweSet[i].map.size();

      System.out.print(i + ": " + ARTText.toLiteralString(Character.toString(artInput[i])) + " { ");
      for (ARTTWEPair e : tweSet[i].map.keySet()) {
        System.out.print(tokenToString(e.token) + (e.suppressed ? "!" : ".") + e.rightExtent + " ");
        if (!e.suppressed) cardinalityChoice++;
      }
      System.out.print("}");
      if (lexSegments && overDegree != null) {
        System.out.print("^" + overDegree[i]);
        if (segmentLexicalisationCardinalities[i] != 0) System.out.print(" Segment lexicalisation count = " + segmentLexicalisationCardinalities[i]
            + ", sum of lengths = " + segmentLexicalisationLengths[i] + ", lexBar = " + segmentLexicalisationProductBars[i]);
      }
      System.out.println();
    }
    System.out.println("TWE set cardinality = " + cardinality);
    System.out.println("TWE set cardinality after choices = " + cardinalityChoice);
    if (lexSegments || lexRecursive) System.out.println("TWE set cardinality after choices for columns touched by lexicaliser = " + cardinalityChoice);
  }

  public void printIndexedTWESet(int[][][] indexedTWESet) {
    // Now output the TWE set
    int cardinality = 0;
    for (int i = 0; i < indexedTWESet.length; i++) {
      System.out.print(i + ": " + ARTText.toLiteralString(Character.toString(artInput[i])) + " { ");
      for (int t = 0; t < indexedTWESet[i].length; t++) {
        if (indexedTWESet[i][t] != null) {
          cardinality += indexedTWESet[i][t].length;

          for (int e : indexedTWESet[i][t])
            System.out.print(tokenToString(t) + "." + e + " ");
        }
      }
      System.out.print("}\n");
    }
    System.out.println("Indexed TWE set cardinality = " + cardinality);

  }

  /******************************************************************************
   *
   * 7. Builtin recognisers and their support functions
   *
   ******************************************************************************/
  int tokIndex;
  ARTText tokText;

  static public boolean isValidBuiltin(String name) {
    return validBuiltins.contains(name);
  }

  protected char artPeekCh() {
    if (artInputIndex >= artInputLength)
      return '\0';
    else
      return artInput[artInputIndex];
  }

  protected char artPeekChToLower() {
    if (artInputIndex >= artInputLength)
      return '\0';
    else
      return Character.toLowerCase(artInput[artInputIndex]);
  }

  protected char artPeekOneCh() {
    if (artInputIndex + 1 >= artInputLength)
      return '\0';
    else
      return artInput[artInputIndex + 1];
  }

  protected char artGetCh() {
    // System.out
    // .println("artGetCh() at index " + artInputIndex + " character " + (int) artInput.charAt(artInputIndex));
    if (artInputIndex >= artInputLength)
      return '\0';
    else
      return artInput[artInputIndex++];
  }

  void printInputSlice(int leftExtent, int rightExtent) {
    for (int i = leftExtent; i < rightExtent; i++)
      tokText.print(Character.toString(artInput[i]));
  }

  void printInputSliceStripped(int leftExtent, int rightExtent) {
    for (int i = leftExtent; i < rightExtent; i++)
      if (Character.isWhitespace(artInput[i]))
        tokText.print(Character.toString(artInput[i]));
      else
        tokText.print(" ");
  }

  void printTokenisedFirstTWESetElement() {
    for (ARTTWEPair e : tweSet[tokIndex].map.keySet()) {
      if (!e.suppressed) {
        ARTGrammarElement tokenElement = grammar.getElement(e.token);
        if (tokenElement instanceof ARTGrammarElementTerminalBuiltin && !((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("WHITESPACE"))
          tokText.print(((ARTGrammarElementTerminal) tokenElement).getId());
        else
          printInputSlice(tokIndex, e.rightExtent);

        tokIndex = e.rightExtent;
        return;
      }
    }
    System.out.println("Error when outputting input.tok - no unsuppressed token found at input position " + tokIndex);
    tokIndex = artInputLength + 1;
  }

  void printStrippedFirstTWESetElement() {
    for (ARTTWEPair e : tweSet[tokIndex].map.keySet()) {
      if (!e.suppressed) {
        ARTGrammarElement tokenElement = grammar.getElement(e.token);
        if (tokenElement instanceof ARTGrammarElementTerminalBuiltin && (((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("WHITESPACE")
            || ((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("COMMENT_BLOCK_C")
            || ((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("COMMENT_LINE_C")))
          printInputSliceStripped(tokIndex, e.rightExtent);
        else
          printInputSlice(tokIndex, e.rightExtent);

        tokIndex = e.rightExtent;
        return;
      }
    }
    System.out.println("Error when outputting input.tok - no unsuppressed token found at input position " + tokIndex);
    tokIndex = artInputLength + 1;
  }

  public void outputTokenFile(String baseName) throws ARTException {
    // Now output the TWE set as .tok file
    tokText = new ARTText(new ARTTextHandlerFile(baseName + ".tok"));

    tokIndex = 0;
    while (tokIndex < artInputLength - 1)
      printTokenisedFirstTWESetElement();
    tokText.close();
  }

  public void outputCommentStrippedFile(String baseName) throws ARTException {
    tokText = new ARTText(new ARTTextHandlerFile(baseName + ".noComm"));

    tokIndex = 0;
    while (tokIndex < artInputLength - 1)
      printStrippedFirstTWESetElement();
    tokText.close();
  }

  private boolean artIsAlpha(char i) {
    return Character.isLetter(i);
  }

  private boolean artIsDigit(char i) {
    return Character.isDigit(i);
  }

  private boolean artIsHexDigit(char i) {
    return artIsDigit(i) || (i >= 'a' && i <= 'f') || (i >= 'A' && i <= 'F');
  }

  private boolean artIsAlphaOrDigit(char i) {
    return artIsAlpha(i) || artIsDigit(i);
  }

  private boolean artIsSimpleSpace(char i) {
    return i == ' ' || i == '\t' || i == '\n' || i == '\r';
  }

  protected void artBuiltin_SINGLETON_CASE_SENSITIVE(String string) {
    // System.out.print("At index " + artInputIndex + " testing for singleton case sensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != artPeekCh()) {
        artInputIndex = artLexemeLeftIndex;
        // System.out.println(" reject");
        return;
      } else
        artGetCh();

    // System.out.println(" accept");
  }

  protected void artBuiltin_SINGLETON_CASE_INSENSITIVE(String string) {
    // System.out.print("At index " + artInputIndex + " testing for singleton case insensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != artPeekChToLower()) {
        artInputIndex = artLexemeLeftIndex;
        // System.out.println(" reject");
        return;
      } else
        artGetCh();

    // System.out.println(" accept");
  }

  protected void artBuiltin_ID() {
    if (!artIsAlpha(artPeekCh())) return;
    while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_')
      artGetCh();
  }

  protected void artBuiltin_ID_AN() {
    if (!artIsAlpha(artPeekCh())) return;
    while (artIsAlphaOrDigit(artPeekCh()))
      artGetCh();
  }

  // An identifier that only accepts alphaetic characters - no under score or digit
  protected void artBuiltin_ID_A() {
    while (artIsAlpha(artPeekCh()))
      artGetCh();
  }

  protected void artBuiltin_ID_SOS() {
    if (!(artIsAlpha(artPeekCh()) || artPeekCh() == '_')) return;
    while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_' || artPeekCh() == '-')
      artGetCh();

    while (artPeekCh() == '\'')
      artGetCh();
  }

  protected void artBuiltin_INTEGER() {
    if (!artIsDigit(artPeekCh())) // Integers must contain at least one leading digit
      return;

    /* Check for hexadecimal introducer */
    boolean hex = (artPeekCh() == '0' && (artPeekOneCh() == 'x' || artPeekOneCh() == 'X'));

    if (hex) {
      artGetCh();
      artGetCh(); // Skip over hex introducer
      if (!artIsHexDigit(artPeekCh())) {
        artInputIndex = artLexemeLeftIndex;
        return;
      }
      while (artIsHexDigit(artGetCh()))
        ;
    } else
      while (artIsDigit(artPeekCh()))
        artGetCh();
  }

  protected void artBuiltin_REAL() {
    if (!artIsDigit(artPeekCh())) // Reals must contain at least one leading digit
      return;

    while (artIsDigit(artPeekCh()))
      artGetCh();

    // System.out.println("Testing for real at " + artCharacterStringInputIndex + ": current characters are " + artPeekCh() + " and " + artPeekOneCh());
    if (!(artPeekCh() == '.' && artIsDigit(artPeekOneCh()))) {
      artInputIndex = artLexemeLeftIndex;
      return;
    }

    artGetCh(); // skip .

    while (artIsDigit(artPeekCh()))
      artGetCh();

    if (artPeekCh() == 'e' || artPeekCh() == 'E') {
      artGetCh();

      while (artIsDigit(artPeekCh()))
        artGetCh();
    }
  }

  private void artSkipEscapeSequence() {
    artGetCh(); // Step over \element
  }

  protected void artBuiltin_CHAR_SQ() {
    if (artPeekCh() != '\'') return;
    artGetCh(); // Skip delimiter
    if (artGetCh() == '\\') artSkipEscapeSequence();
    if (artPeekCh() != '\'') {
      artInputIndex = artLexemeLeftIndex;
      return;
    } // Abort and return
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_CHAR_BQ() {
    if (artPeekCh() != '`') return;
    artGetCh(); // Skip delimiter
    if (artGetCh() == '\\') artSkipEscapeSequence();
  }

  protected void artBuiltin_STRING_SQ() throws ARTException {
    if (artPeekCh() != '\'') return;
    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated ' ... ' string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '\'');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_PLAIN_SQ() throws ARTException {
    if (artPeekCh() != '\'') return;
    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated ' ... ' string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      artGetCh();
    } while (artPeekCh() != '\'');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_DQ() throws ARTException {
    if (artPeekCh() != '"') return;
    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated \" ... \" string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '"');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_BQ() throws ARTException {
    if (artPeekCh() != '`') return;
    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated ` ... ` string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '`');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_DOLLAR() throws ARTException {
    if (artPeekCh() != '$') return;
    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated $ ... $ string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '$');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_BRACE() throws ARTException {
    if (artPeekCh() != '{') return;
    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated { ... } string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '}');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_BRACE_NEST() throws ARTException {
    if (artPeekCh() != '{') return;
    int nestLevel = 0;
    do {
      if (artPeekCh() == '{') nestLevel++;
      if (artPeekCh() == '}') nestLevel--;
      if (artPeekCh() == '\0') throw new ARTException(ARTText.echo("Unterminated nestable { ... } string at position " + artLexicalTrim(artLexemeLeftIndex),
          artLexicalTrim(artLexemeLeftIndex), artInputString));

      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void artBuiltin_STRING_BB() throws ARTException {
    if (!((artPeekCh() == '[') && (artPeekOneCh() == '['))) return;
    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated [[ ... ]] string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (!((artPeekCh() == ']') && (artPeekOneCh() == ']')));
    artGetCh(); // Skip delimiter
    artGetCh(); // Skip delimiter
  }

  public void artBuiltin_WHITESPACE() {
    while (artIsSimpleSpace(artPeekCh()))
      artGetCh();
  }

  public void artBuiltin_COMMENT_NEST_ART() throws ARTException {
    if (!(artPeekCh() == '(' && artPeekOneCh() == '*')) return;
    int nestingLevel = 0;

    do {
      if (artPeekCh() == '\0') throw new ARTException(ARTText.echo("Unterminated nestable (* ... *) comment at position " + artLexicalTrim(artLexemeLeftIndex),
          artLexicalTrim(artLexemeLeftIndex), artInputString));

      if (artPeekCh() == '(' && artPeekOneCh() == '*') {
        artGetCh();
        artGetCh();
        nestingLevel++;
      } else if (artPeekCh() == '*' && artPeekOneCh() == ')') {
        artGetCh();
        artGetCh();
        nestingLevel--;
      } else
        artGetCh();
    } while (nestingLevel > 0);
  }

  protected void artBuiltin_COMMENT_BLOCK_C() throws ARTException {
    if (!((artPeekCh() == '/') && (artPeekOneCh() == '*'))) return;

    do {
      if (artPeekCh() == '\0') throw new ARTException(
          ARTText.echo("Unterminated /* ... */ comment at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString));
      artGetCh();
    } while (!(artPeekCh() == '*' && artPeekOneCh() == '/'));
    artGetCh();
    artGetCh();
  }

  public void artBuiltin_COMMENT_LINE_C() {
    if (!((artPeekCh() == '/') && (artPeekOneCh() == '/'))) return;

    while (artPeekCh() != '\n' && artPeekCh() != '\0') // Quietly accept an input file with no \n at the end.
      artGetCh();
  }

}
