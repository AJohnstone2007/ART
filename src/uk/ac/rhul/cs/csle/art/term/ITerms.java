package uk.ac.rhul.cs.csle.art.term;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.Util;

public class ITerms {
  private class ValueException extends RuntimeException {
    private static final long serialVersionUID = -8086174221866434606L;

    public ValueException(String message) {
      super(message);
    }
  }

  /* Term class - to be replaced by HashPool style implementation *******************************************/
  // @formatter:off
  private class ITerm { // Replace at some point with a HashPool implementation
    private final int symbolIndex;
    private final int[] children;

    public ITerm(int symbolIndex, int[] children) {
      this.symbolIndex = symbolIndex;
      if (children == null) children= new int[0];
      this.children = children;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(children);
      result = prime * result + symbolIndex;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ITerm other = (ITerm) obj;
      if (!Arrays.equals(children, other.children)) return false;
      if (symbolIndex != other.symbolIndex) return false;
      return true;
    }

    public int getSymbolIndex() {return symbolIndex; }
    public String getSymbolString() { return indexToString.get(symbolIndex); }
    public int[] getChildren() { return children; }
    public int getArity() { return children.length; }
    @Override public String toString() { return (symbolIndex + "(" + Arrays.toString(children) + ")"); }
  }
  // @formatter:on

  public static int variableCount = 20;
  public static int sequenceVariableCount = 10;
  public AbstractPlugin plugin;
  private final TermTraverserText rawTextTraverser = new TermTraverserText(this); // This is intended for debugging - show the raw term
  public TermTraverserText plainTextTraverser; // Plain text pretty printer - loaded by user
  public TermTraverserText latexTraverser; // LaTeX pretty printer - loaded by user

  private final Map<String, Integer> stringToIndex = new HashMap<>(); // Each unique string is mapped onto the naturals
  private final Map<Integer, String> indexToString = new HashMap<>(); // Reverse map for recovering the strings
  private Integer stringNextFreeIndex = 1; // defensive programming: index zero is never used and can be used as fail value
  private final Map<ITerm, Integer> termToIndex = new HashMap<>(); // Each unique immutable term is mapped onto the naturals
  private final Map<Integer, ITerm> indexToTerm = new HashMap<>(); // Reverse map for recovering the terms
  private Integer termNextFreeIndex = 1; // defensive programming: index zero is never used

  private final int firstVariableIndex;
  private final int firstSequenceVariableIndex;
  private final int firstSpecialSymbolIndex;
  private final int firstNormalSymbolIndex;

  public final int termBooleanTrue;
  public final int termBooleanFalse;
  public final int termBottom;
  public final int termDone;
  public final int termEmpty;
  public final int termStringEmpty;
  public final int termArrayEmpty;
  public final int termListEmpty;
  public final int termSetEmpty;
  public final int termMapEmpty;

  public final Bottom bottom = new Bottom();
  public final Done done = new Done();
  public final Empty empty = new Empty();

  public ITerms() {
    // 1. Load text traverser default action which appends the escaped version of the constructor
    rawTextTraverser.addAction(-1, (Integer t) -> rawTextTraverser.append(escapeMeta(termSymbolString(t)) + (termArity(t) == 0 ? "" : "(")),
        (Integer t) -> rawTextTraverser.append(", "), (Integer t) -> rawTextTraverser.append(termArity(t) == 0 ? "" : ")"));

    // 2. Create string map entries for variables
    firstVariableIndex = stringNextFreeIndex;
    findString("_");
    for (int v = 1; v <= variableCount; v++)
      findString("_" + v);
    firstSequenceVariableIndex = stringNextFreeIndex;
    findString("_*");
    for (int v = 1; v <= sequenceVariableCount; v++)
      findString("_" + v + "*");

    // 3. Create string map entries for special symbols
    firstSpecialSymbolIndex = stringNextFreeIndex;
    loadStrings();

    // 4. Make useful constant terms
    firstNormalSymbolIndex = stringNextFreeIndex;
    termBooleanTrue = findTerm("__bool(true)");
    termBooleanFalse = findTerm("__bool(false)");
    termBottom = findTerm("__bottom");
    termDone = findTerm("__done");
    termEmpty = findTerm("__empty");
    termStringEmpty = findTerm("__string");
    termArrayEmpty = findTerm("__array(__int32(0))");
    termListEmpty = findTerm("__list");
    termSetEmpty = findTerm("__set");
    termMapEmpty = findTerm("__map");

    // 5. Connect to a plugin - either the default built in to art.jar or one in the use class path if one can be found
    plugin = new ARTDefaultPlugin();
    // Try and find a plugin for __user() calls
    try {
      plugin = (AbstractPlugin) getClass().getClassLoader().loadClass("ARTValuePlugin").getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Silently absorb exception - when the default is used
    }
    System.out.println("Attached to " + plugin.getClass().getSimpleName() + ": " + plugin.description());
  }

  /* Raw term rendering *************************************************************************************/
  public String toRawString(int term) {
    if (term == 0) return "null term";
    return rawTextTraverser.toString(term);
  }

  public String toString(int term) {
    if (term == 0) return "null term";
    return plainTextTraverser.toString(term);
  }

  public String toString(Integer term, Map<Integer, Integer> localAliases) {
    if (term == 0) return "null term";
    return plainTextTraverser.toString(term, false, -1, localAliases);
  }

  public String toString(Integer term, Boolean indent, Integer depthLimit, Map<Integer, Integer> localAliases) {
    if (term == 0) return "null term";
    return plainTextTraverser.toString(term, indent, depthLimit, localAliases);
  }

  /* Symbol categories **************************************************************************************/
  public boolean isVariableSymbol(int symbolIndex) {
    return symbolIndex >= firstVariableIndex && symbolIndex < firstSequenceVariableIndex;
  }

  public boolean isVariableTerm(int term) {
    return isVariableSymbol(indexToTerm.get(term).getSymbolIndex());
  }

  public boolean isSequenceVariableSymbol(int symbolIndex) {
    return symbolIndex >= firstSequenceVariableIndex && symbolIndex < firstSpecialSymbolIndex;
  }

  public boolean isSequenceVariableTerm(int term) {
    return isSequenceVariableSymbol(indexToTerm.get(term).getSymbolIndex());
  }

  public boolean isSpecialSymbol(int symbolIndex) {
    return symbolIndex >= firstSpecialSymbolIndex && symbolIndex < firstNormalSymbolIndex;
  }

  public boolean isSpecialTerm(int term) {
    return isSpecialSymbol(indexToTerm.get(term).getSymbolIndex());
  }

  public boolean isNormalSymbol(int symbolIndex) {
    return symbolIndex >= firstNormalSymbolIndex;
  }

  public boolean isNormalTerm(int term) {
    int symbolIndex = indexToTerm.get(term).getSymbolIndex();
    return symbolIndex >= firstNormalSymbolIndex;
  }

  /* Meta character handling for string version of a term **************************************************/
  static Set<Character> metaCharacters = Set.of('(', ')', ',', '_', '*', ':', '\\', '\"', ' ');

  public static String escapeMeta(String string) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      if (metaCharacters.contains(c)) sb.append('\\');
      sb.append(c);
    }
    return sb.toString();
  }

  public static String unescapeMeta(String string) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    while (i < string.length()) {
      char c = string.charAt(i);

      if (c == '\\') {
        i++;
        c = string.charAt(i);
        if (!metaCharacters.contains(c)) Util.fatal("iTerms.unescapeMeta found escaped non-meta character");
      }
      sb.append(c);
      i++;
    }
    return sb.toString();
  }

  /* Parse a term written in a human-comfortable string form with some abbreviations for some constants **********/

  /*
   * parseTerm ::= name ( `( WS parseSubterms `) WS )?
   *
   * parseSubterms ::= term ( `, WS parseTerm )*
   *
   * The lexical structure is a litle unusual in that a name can be any string of characters that does not cause an LL(1) nondeterminism, and escape characters
   * are allowed in names. Escape sequences (backslash-X) where X is n, t, r or u are special and have their Java meanings. All other escape sequences yield \
   * c. Hence a name can have an embedded * in it, for instance, written \*
   *
   * name ::= nondigit char* WS
   *
   * The grammar is arranged so that each method has a single integer synthesized attribute, hence we can use a return type of int For character sequences, the
   * returned value is the key into the string pool. For terms, the returned value is the key into the term pool
   */
  private String parseInput;
  private int parseCurrentInputIndex;
  private char parseCurrentCharacter;

  private void getc() {
    parseCurrentCharacter = parseInput.charAt(parseCurrentInputIndex++);
  }

  /* Support for echoing the current line */
  private int lineNumber() {
    int index = parseCurrentInputIndex;
    if (parseInput == null || index < 0) return 0;

    if (index >= parseInput.length()) index = parseInput.length() - 1;

    int lineCount = 1;

    for (int tmp = 0; tmp < index; tmp++)
      if (parseInput.charAt(tmp) == '\n') lineCount++;

    return lineCount;

  }

  private int columnNumber() { // Return x coordinate: note that the first column is column zero!
    int index = parseCurrentInputIndex;
    int columnCount = 0;

    if (parseInput == null || index < 0) return 0;

    if (index >= parseInput.length()) index = parseInput.length() - 1;

    if (index == 0) return 0;

    do {
      index--;
      columnCount++;
    } while (index > 0 && parseInput.charAt(index) != '\n');

    if (index != 0) columnCount--; // If we did not terminate on start of buffer, then we must have terminated on \n so step forward 1

    return columnCount;
  }

  private static void appendLine(StringBuilder sb, int lineNumber, String buffer) {
    if (lineNumber < 1) return;
    int length = buffer.length();
    int tmp = lineNumber;
    int i;
    for (i = 0; tmp > 1 && i < length; i++)
      if (buffer.charAt(i) == '\n') tmp--;
    if (i >= length) return;

    sb.append(String.format("%5d: ", lineNumber));
    for (; i < length && buffer.charAt(i) != '\n'; i++)
      sb.append(buffer.charAt(i));
    sb.append("\n");
  }

  private String echo(String message) {
    StringBuilder sb = new StringBuilder();
    sb.append(lineNumber() + "," + columnNumber() + " " + message + "\n");
    if (parseInput == null || parseCurrentInputIndex < 0) return sb.toString();

    appendLine(sb, lineNumber() - 1, parseInput);
    appendLine(sb, lineNumber(), parseInput);

    sb.append("-------"); // Print pointer line
    int echoColumn = columnNumber();
    for (int tmp = 0; tmp < echoColumn; tmp++)
      sb.append("-");
    sb.append("^\n");
    appendLine(sb, lineNumber() + 1, parseInput);
    sb.append("\n");

    return sb.toString();
  }

  private void parseSyntaxError(String s) {
    System.out.println("V5 " + echo(s));
    // System.out.println("** " + s);
    // System.out.println(input);
    // for (int i = 0; i < cp - 1; i++)
    // System.out.print("-");
    // System.out.println("^");
    System.exit(1);
  }

  private int parseTerm() {
    int symbolNameStringIndex = parseSymbolName();
    // Semantic checks on symbol name
    String symbolNameString = getString(symbolNameStringIndex);
    // if (symbolNameString.length() > 0 && symbolNameString.charAt(0) == '_') {
    // if (symbolNameString.length() > 1 && symbolNameString.charAt(1) == '_') {// two underscores so must be intrinsic function or type
    // if (!isSpecialSymbol(symbolNameStringIndex)) parseSyntaxError("unknown evaluatable function: " + symbolNameString);
    // } else {
    // if (!isVariableSymbol(symbolNameStringIndex) && !isSequenceVariableSymbol(symbolNameStringIndex)) parseSyntaxError("unknown variable");
    // }
    // }

    List<Integer> subterms;
    if (parseCurrentCharacter == '(') {
      getc();
      parseWhiteSpace();
      if (parseCurrentCharacter == ')') {
        getc();
        parseWhiteSpace();
        subterms = new LinkedList<>();
      } else {
        subterms = parseSubterms();
        if (parseCurrentCharacter != ')') parseSyntaxError("Syntax error whilst parsing term: expected ')' or ',' but found '" + parseCurrentCharacter + "'");
        getc();
        parseWhiteSpace();
      }
    } else
      subterms = new LinkedList<>();

    int[] children = new int[subterms.size()];
    for (int i = 0; i < subterms.size(); i++)
      children[i] = subterms.get(i);

    return findTerm(symbolNameStringIndex, children); // Variable has string = 0
  }

  private List<Integer> parseSubterms() {
    List<Integer> ret = new LinkedList<>();
    ret.add(parseTerm());
    while (parseCurrentCharacter == ',') {
      getc();
      parseWhiteSpace();
      ret.add(parseTerm());
    }
    return ret;
  }

  private int parseSymbolName() {
    String name = new String();
    if (Character.isWhitespace(parseCurrentCharacter) || parseCurrentCharacter == '(' || parseCurrentCharacter == ')' || parseCurrentCharacter == ','
        || parseCurrentCharacter == ':' || parseCurrentCharacter == (char) 0) {
      parseSyntaxError("Empty name");
    }
    /* 31 March 2021 - added literal strings as names */
    /* 3 January 2022 - dropped quotes from parsed payload */
    /* 27 May 2022 - retain backslashes */

    // 1. String processing
    if (parseCurrentCharacter == '"') {
      do {
        name += parseCurrentCharacter;
        if (parseCurrentCharacter == '\\') {
          getc();
          name += parseCurrentCharacter;
        }
        getc();
      } while (parseCurrentCharacter != '"');
      name += parseCurrentCharacter;
      getc();
    } else
      // 2. nonstring processing
      while (!Character.isWhitespace(parseCurrentCharacter) && parseCurrentCharacter != '(' && parseCurrentCharacter != ')' && parseCurrentCharacter != ','
          && parseCurrentCharacter != ':' && parseCurrentCharacter != (char) 0) {
        if (parseCurrentCharacter == '\\') {
          // name += cc;
          getc();
        }
        name += parseCurrentCharacter;
        getc();
      }

    parseWhiteSpace();
    return findString(name);
  }

  private void parseWhiteSpace() {
    while (Character.isWhitespace(parseCurrentCharacter) && parseCurrentCharacter != (char) 0)
      getc();
  }

  /* Finders *************************************************************************************************************************/
  public int findTerm(String term) {
    parseInput = (term + "\0");
    parseCurrentInputIndex = 0;
    getc();
    int ret = parseTerm();
    if (parseCurrentInputIndex != parseInput.length()) {
      parseSyntaxError("Unexpected characters after term");
      return 0;
    }
    return ret;
  }

  public int findTerm(int symbolStringIndex, int... children) {
    Integer ret;
    ITerm term = new ITerm(symbolStringIndex, children);

    if ((ret = termToIndex.get(term)) == null) {
      termToIndex.put(term, termNextFreeIndex);
      indexToTerm.put(termNextFreeIndex, term);
      ret = termNextFreeIndex++;
    }
    return ret;
  }

  public int findTerm(String string, int... children) {
    return findTerm(findString(string), children);
  }

  public int findTerm(int symbolStringIndex, LinkedList<Integer> children) {
    int nc = 0, newChildren[] = new int[children.size()];
    for (int i : children)
      newChildren[nc++] = i;
    return findTerm(symbolStringIndex, newChildren);
  }

  public int findTerm(String string, LinkedList<Integer> children) {
    return findTerm(findString(string), children);
  }

  public int findString(String string) {
    Integer ret;
    if ((ret = stringToIndex.get(string)) == null) {
      stringToIndex.put(string, stringNextFreeIndex);
      indexToString.put(stringNextFreeIndex, new String(string)); // Don't assume the old string is immutable...
      ret = stringNextFreeIndex++;
    }
    return ret;
  }

  public String getString(int stringIndex) {
    return indexToString.get(stringIndex);
  }

  public int termArity(int term) {
    return indexToTerm.get(term).getArity();
  }

  public String termSymbolString(int term) {
    return indexToTerm.get(term).getSymbolString();
  }

  public int termSymbolStringIndex(int term) {
    return indexToTerm.get(term).getSymbolIndex();
  }

  public int[] termChildren(int term) {
    return indexToTerm.get(term).getChildren();
  }

  public int termVariableNumber(int term) {
    return termSymbolStringIndex(term) - firstVariableIndex;
  }

  public int subterm(int term, int... path) {
    int ret = term;
    for (int i = 0; i < path.length; i++)
      ret = termChildren(ret)[path[i]];
    return ret;
  }

  /* Pattern matching and substitution **********************************************************************/
  public boolean matchOneSV(int closedTermIndex, int openTermIndex, int[] bindings) { // This matcher allows one sequence variable per
                                                                                      // sequence of siblings
    return false;
  }

  public boolean matchZeroSV(int closedTermIndex, int openTermIndex, int[] bindings) { // This matcher does not allow sequence
    // System.out
    // .println("matchZeroSV() " + closedTermIndex + ":" + toString(closedTermIndex) + " against open term " + openTermIndex + ":" + toString(openTermIndex));

    if (isSequenceVariableTerm(openTermIndex)) Util.fatal("in matchZeroSV() right hand side must not contain sequence variables");

    if (isVariableTerm(openTermIndex)) {
      int variableNumber = termVariableNumber(openTermIndex);
      if (variableNumber == 0) {
        // System.out.println("matchZeroSV() matches wildcard and returns true with no update to bindings");
        return true;
      }

      bindings[variableNumber] = closedTermIndex; // Variable zero means match anything but don't bind
      // System.out.println("matchZeroSV() binds to variable and returns true");
      return true;
    }

    if (!(termSymbolStringIndex(closedTermIndex) == termSymbolStringIndex(openTermIndex) && termArity(closedTermIndex) == termArity(openTermIndex)))
      return false;
    for (int i = 0; i < termArity(openTermIndex); i++)
      if (!matchZeroSV(termChildren(closedTermIndex)[i], termChildren(openTermIndex)[i], bindings)) return false;

    // System.out.println("matchZeroSV() matched children and root, and returns true");

    return true;
  }

  public int substitute(int[] bindings, int openTermIndex, int level) {
    int ret;

    // Postorder substitution so substitute children first

    // System.out.println(level + " Substitute " + toString(openTermIndex) + " with bindings {" + toStringBindings(bindings) + "}");
    // System.out.println(level + " Open term is " + toString(openTermIndex));
    int arity = termArity(openTermIndex);
    // System.out.println(level + " Arity is " + arity);

    int[] children = new int[arity];
    int newArity = 0;
    for (int i = 0; i < arity; i++) {
      children[i] = substitute(bindings, termChildren(openTermIndex)[i], level + 1);
      if (isSequenceVariableTerm(children[i]))
        newArity += termArity(children[i]);
      else
        newArity++;
    }

    // System.out.println(level + " After substitution, open term is " + toString(openTermIndex));

    // if (newArity != arity) {// There were sequence variable bindings, so we must promote the children of the sequences
    // int[] newChildren = new int[newArity];
    // int nextNewChild = 0;
    //
    // for (int i = 0; i < arity; i++) { // If not a sequence child, copy else copy children
    // if (getTermSymbolIndex(children[i]) == 0) // Are we substituting a sequence variable?
    // for (int j = 0; j < getTermArity(children[i]); j++)
    // newChildren[nextNewChild++] = getTermChildren(children[i])[j];
    // else
    // newChildren[nextNewChild++] = children[i];
    // }
    // children = newChildren;
    // }
    //
    if (isVariableTerm(openTermIndex) || isSequenceVariableTerm(openTermIndex)) {
      int termVariableNumber = termVariableNumber(openTermIndex);
      int boundValue = bindings[termVariableNumber];
      // Speculative comment out for listings examples
      // if (boundValue == 0) throw new RewriteException("attempt to substitute unbound variable " + tt.toString(openTermIndex));
      ret = boundValue;
      // Now reduce substituted values by evaluation
      if (isSpecialTerm(boundValue))
        ret = evaluateTerm(boundValue, this.termChildren(boundValue));
      else
        ret = boundValue;
    } else if (isSpecialTerm(openTermIndex))
      ret = evaluateTerm(openTermIndex, children);
    else
      ret = findTerm(termSymbolStringIndex(openTermIndex), children);

    // System.out.println("Substitute " + toString(openTermIndex) + " with bindings " + toStringBindings(bindings) + " returns " + toString(ret));
    return ret;
  }

  public String toStringBindings(int[] bindings) {
    String ret = "";
    for (int i = 0; i < bindings.length; i++) {
      if (bindings[i] != 0) ret += " _" + i + "=" + rawTextTraverser.toString(bindings[i]);
    }
    return ret;
  }

  // Take a closed term and a list of k (match, substitute) terms represented as an integer array of length 2*k
  // Return a list of rewritten terms of length j, where j is the number of successful matches
  int[] bindings = new int[variableCount + sequenceVariableCount]; // Enough space for all variables

  // public int[] rewrite(int closedTerm, int... matchSubstituteTerms) {
  // if (!(matchSubstituteTerms.length > 0 && matchSubstituteTerms.length % 2 != 0))
  // throw new RewriteException("internal - matchSubstituteTerms must be of length 2n, n>0");
  //
  // int[] rewrites = new int[matchSubstituteTerms.length / 2];
  // int matches = 0;
  // for (int i = 0; i < matchSubstituteTerms.length; i += 2)
  // if (matchZeroSV(closedTerm, matchSubstituteTerms[i], bindings)) {
  // rewrites[matches++] = substitute(bindings, closedTerm, 0);
  // Arrays.fill(bindings, 0);
  // }
  // return Arrays.copyOf(rewrites, matches);
  // }

  /* Value system evaluation ********************************************************************************/
  public int evaluateTerm(int term, int[] children) {
    String rootSymbolString = termSymbolString(term);
    if (children.length == 0 || !rootSymbolString.startsWith("__")) return term; // Nothing to do

    // System.out.println("Evaluating " + toString(term));

    int termSymbolStringIndex = termSymbolStringIndex(term);

    // Skip types
    switch (termSymbolStringIndex) {
    case __aStringIndex, __lStringIndex, __sStringIndex, __mStringIndex:
      Util.fatal("term may only appear in appropriate collection context");
      break;

    case __bottomStringIndex, __doneStringIndex, __emptyStringIndex, __quoteStringIndex, __blobStringIndex, __adtprodStringIndex, __adtsumStringIndex, __procStringIndex,

        __boolStringIndex, __charStringIndex, __intAPStringIndex, __int32StringIndex, __realAPStringIndex, __real64StringIndex, __stringStringIndex,

        __arrayStringIndex, __listStringIndex, __setStringIndex, __mapStringIndex:
      return term;
    }

    if (termSymbolStringIndex == __pluginStringIndex) {
      Object values[] = new Object[children.length];
      for (int i = 0; i < children.length; i++)
        values[i] = termToJavaObject(children[i]);

      return javaObjectToTerm(plugin.plugin(values));
    }

    int firstChildSymbolStringIndex = children.length < 1 ? 0 : termSymbolStringIndex(children[0]);
    int secondChildSymbolStringIndex = children.length < 2 ? 0 : termSymbolStringIndex(children[1]);

    switch (children.length) {
    case 1:
      switch (termSymbolStringIndex) {
      case __notStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __boolStringIndex:
          return javaBooleanToTerm(!termToJavaBoolean(children[0]));
        case __int32StringIndex:
          return javaIntegerToTerm(~termToJavaInteger(children[0]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).not());
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __negStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          return javaIntegerToTerm(-termToJavaInteger(children[0]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).negate());
        case __real64StringIndex:
          return javaDoubleToTerm(-termToJavaDouble(children[0]));
        case __realAPStringIndex:
          return javaBigDecimalToTerm(termToJavaBigDecimal(children[0]).negate());
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __cardStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __stringStringIndex:
          return javaIntegerToTerm(termToJavaString(children[0]).length());
        case __arrayStringIndex:
          return javaIntegerToTerm(termToJavaArrayList(children[0]).size());
        case __listStringIndex:
          return javaIntegerToTerm(termToJavaLinkedList(children[0]).size());
        case __setStringIndex:
          return javaIntegerToTerm(termToJavaLinkedHashSet(children[0]).size());
        case __mapStringIndex:
          return javaIntegerToTerm(termToJavaLinkedHashMap(children[0]).size());
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __termArityStringIndex:
        return javaIntegerToTerm(termArity(children[0]));

      case __termRootStringIndex:
        return findTerm(termSymbolStringIndex(term));

      default:
        Util.fatal("Unrecognised operation " + toRawString(term));
        break;
      }
      break;

    case 2:
      switch (termSymbolStringIndex) {
      case __eqStringIndex:
        return javaBooleanToTerm(children[0] == children[1]);

      case __neStringIndex:
        return javaBooleanToTerm(children[0] != children[1]);

      case __gtStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __charStringIndex:
          return javaBooleanToTerm(termToJavaCharacter(children[0]) > termToJavaCharacter(children[1]));
        case __int32StringIndex:
          return javaBooleanToTerm(termToJavaInteger(children[0]) > termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBooleanToTerm(termToJavaBigInteger(children[0]).compareTo(termToJavaBigInteger(children[1])) > 0);
        case __real64StringIndex:
          return javaBooleanToTerm(termToJavaDouble(children[0]) > termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBooleanToTerm(termToJavaBigDecimal(children[0]).compareTo(termToJavaBigDecimal(children[1])) > 0);
        case __stringStringIndex:
          return javaBooleanToTerm(termToJavaString(children[0]).compareTo(termToJavaString(children[1])) > 0);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __ltStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __charStringIndex:
          return javaBooleanToTerm(termToJavaCharacter(children[0]) < termToJavaCharacter(children[1]));
        case __int32StringIndex:
          return javaBooleanToTerm(termToJavaInteger(children[0]) < termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBooleanToTerm(termToJavaBigInteger(children[0]).compareTo(termToJavaBigInteger(children[1])) < 0);
        case __real64StringIndex:
          return javaBooleanToTerm(termToJavaDouble(children[0]) < termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBooleanToTerm(termToJavaBigDecimal(children[0]).compareTo(termToJavaBigDecimal(children[1])) < 0);
        case __stringStringIndex:
          return javaBooleanToTerm(termToJavaString(children[0]).compareTo(termToJavaString(children[1])) < 0);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __geStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __charStringIndex:
          return javaBooleanToTerm(termToJavaCharacter(children[0]) >= termToJavaCharacter(children[1]));
        case __int32StringIndex:
          return javaBooleanToTerm(termToJavaInteger(children[0]) >= termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBooleanToTerm(termToJavaBigInteger(children[0]).compareTo(termToJavaBigInteger(children[1])) >= 0);
        case __real64StringIndex:
          return javaBooleanToTerm(termToJavaDouble(children[0]) >= termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBooleanToTerm(termToJavaBigDecimal(children[0]).compareTo(termToJavaBigDecimal(children[1])) >= 0);
        case __stringStringIndex:
          return javaBooleanToTerm(termToJavaString(children[0]).compareTo(termToJavaString(children[1])) >= 0);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __leStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __charStringIndex:
          return javaBooleanToTerm(termToJavaCharacter(children[0]) <= termToJavaCharacter(children[1]));
        case __int32StringIndex:
          return javaBooleanToTerm(termToJavaInteger(children[0]) <= termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBooleanToTerm(termToJavaBigInteger(children[0]).compareTo(termToJavaBigInteger(children[1])) <= 0);
        case __real64StringIndex:
          return javaBooleanToTerm(termToJavaDouble(children[0]) <= termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBooleanToTerm(termToJavaBigDecimal(children[0]).compareTo(termToJavaBigDecimal(children[1])) <= 0);
        case __stringStringIndex:
          return javaBooleanToTerm(termToJavaString(children[0]).compareTo(termToJavaString(children[1])) <= 0);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __compStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __charStringIndex:
          return javaIntegerToTerm(Integer.signum(termToJavaCharacter(children[0]).compareTo(termToJavaCharacter(children[1]))));
        case __int32StringIndex:
          return javaIntegerToTerm(Integer.signum(termToJavaInteger(children[0]).compareTo(termToJavaInteger(children[1]))));
        case __intAPStringIndex:
          return javaIntegerToTerm(Integer.signum(termToJavaBigInteger(children[0]).compareTo(termToJavaBigInteger(children[1]))));
        case __real64StringIndex:
          return javaIntegerToTerm(Integer.signum(termToJavaDouble(children[0]).compareTo(termToJavaDouble(children[1]))));
        case __realAPStringIndex:
          return javaIntegerToTerm(Integer.signum(termToJavaBigDecimal(children[0]).compareTo(termToJavaBigDecimal(children[1]))));
        case __stringStringIndex:
          return javaIntegerToTerm(Integer.signum(termToJavaString(children[0]).compareTo(termToJavaString(children[1]))));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __andStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __boolStringIndex:
          return javaBooleanToTerm(termToJavaBoolean(children[0]) & termToJavaBoolean(children[1]));
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) & termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).and(termToJavaBigInteger(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __orStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __boolStringIndex:
          return javaBooleanToTerm(termToJavaBoolean(children[0]) | termToJavaBoolean(children[1]));
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) | termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).or(termToJavaBigInteger(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __xorStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __boolStringIndex:
          return javaBooleanToTerm(termToJavaBoolean(children[0]) ^ termToJavaBoolean(children[1]));
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) ^ termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).xor(termToJavaBigInteger(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __shiftStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          int right = termToJavaInteger(children[1]);
          return javaIntegerToTerm(right > 0 ? termToJavaInteger(children[0]) >>> right : termToJavaInteger(children[0]) << -right);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __sshiftStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          int right = termToJavaInteger(children[1]);
          return javaIntegerToTerm(right > 0 ? termToJavaInteger(children[0]) >> right : termToJavaInteger(children[0]) << -right);
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).shiftRight(termToJavaBigInteger(children[1]).intValueExact()));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __rotStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          return javaIntegerToTerm(Integer.rotateRight(termToJavaInteger(children[0]), termToJavaInteger(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __addStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) + termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).add(termToJavaBigInteger(children[1])));
        case __real64StringIndex:
          return javaDoubleToTerm(termToJavaDouble(children[0]) + termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBigDecimalToTerm(termToJavaBigDecimal(children[0]).add(termToJavaBigDecimal(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __subStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) - termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).subtract(termToJavaBigInteger(children[1])));
        case __real64StringIndex:
          return javaDoubleToTerm(termToJavaDouble(children[0]) - termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBigDecimalToTerm(termToJavaBigDecimal(children[0]).subtract(termToJavaBigDecimal(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __mulStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) * termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).multiply(termToJavaBigInteger(children[1])));
        case __real64StringIndex:
          return javaDoubleToTerm(termToJavaDouble(children[0]) * termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBigDecimalToTerm(termToJavaBigDecimal(children[0]).multiply(termToJavaBigDecimal(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __divStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) / termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).divide(termToJavaBigInteger(children[1])));
        case __real64StringIndex:
          return javaDoubleToTerm(termToJavaDouble(children[0]) / termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBigDecimalToTerm(termToJavaBigDecimal(children[0]).divide(termToJavaBigDecimal(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __modStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          return javaIntegerToTerm(termToJavaInteger(children[0]) % termToJavaInteger(children[1]));
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).remainder(termToJavaBigInteger(children[1])));
        case __real64StringIndex:
          return javaDoubleToTerm(termToJavaDouble(children[0]) % termToJavaDouble(children[1]));
        case __realAPStringIndex:
          return javaBigDecimalToTerm(termToJavaBigDecimal(children[0]).remainder(termToJavaBigDecimal(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __expStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __int32StringIndex:
          int left = termToJavaInteger(children[0]), right = termToJavaInteger(children[1]);
          for (int i = 1; i < right; i++)
            left *= left;
          return javaIntegerToTerm(left);
        case __intAPStringIndex:
          return javaBigIntegerToTerm(termToJavaBigInteger(children[0]).pow(termToJavaBigInteger(children[1]).intValueExact()));
        case __real64StringIndex:
          return javaDoubleToTerm(Math.pow(termToJavaDouble(children[0]), termToJavaDouble(children[1])));
        case __realAPStringIndex:
          return javaBigDecimalToTerm(termToJavaBigDecimal(children[0]).pow(termToJavaBigDecimal(children[1]).intValueExact())); // Note integer powers only:
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __catStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __stringStringIndex:
          return javaStringToTerm(termToJavaString(children[0]) + termToJavaString(children[1]));
        case __listStringIndex:
          var ret = termToJavaLinkedList(children[0]);
          ret.addAll(termToJavaLinkedList(children[1]));
          return javaListToTerm(ret);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __putStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __arrayStringIndex:
          Util.fatal("__put on __array must have arity 3: __put(__array,  _int32, X)");
          return termBottom;
        case __listStringIndex:
          Util.fatal("__put on __list must have arity 3: __put(__list,  _int32, X)");
          return termBottom;
        case __setStringIndex:
          Set<Object> set = termToJavaLinkedHashSet(children[0]);
          set.add(termToJavaObject(children[1]));
          return javaSetToTerm(set);
        case __mapStringIndex:
          Util.fatal("__put on __map must have arity 3: __put(__map,  _Key, X)");
          return termBottom;
        }
        break;

      case __getStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __stringStringIndex:
          return javaCharacterToTerm(termToJavaString(children[0]).charAt(termToJavaInteger(children[1])));
        case __arrayStringIndex:
          ArrayList<Object> arrayList = termToJavaArrayList(children[0]);
          int indexAL = termToJavaInteger(children[1]);
          return javaObjectToTerm(arrayList.get(indexAL));
        case __listStringIndex:
          LinkedList<Object> list = termToJavaLinkedList(children[0]);
          int indexL = termToJavaInteger(children[1]);
          if (indexL >= list.size()) return termEmpty;
          return javaObjectToTerm(list.get(indexL));
        case __setStringIndex:
          return termToJavaLinkedHashSet(children[0]).contains(termToJavaObject(children[1])) ? children[1] : termEmpty;
        case __mapStringIndex:
          Map<Object, Object> map = termToJavaLinkedHashMap(children[0]);
          Object bound = map.get(termToJavaObject(children[1]));
          return bound == null ? termEmpty : javaObjectToTerm(bound);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __removeStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __listStringIndex:
          List<Object> list = termToJavaLinkedList(children[0]);
          list.remove((int) termToJavaInteger(children[1])); // Nasty: remove by position if int; remove by value if Integer
          return javaListToTerm(list);
        case __setStringIndex:
          Set<Object> set = termToJavaLinkedHashSet(children[0]);
          set.remove(termToJavaObject(children[1]));
          return javaSetToTerm(set);
        case __mapStringIndex:
          Map<Object, Object> map = termToJavaLinkedHashMap(children[0]);
          map.remove(termToJavaObject(children[1]));
          return javaMapToTerm(map);
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __prefixStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __stringStringIndex:
          return javaStringToTerm(termToJavaString(children[0]).substring(0, termToJavaInteger(children[1])));
        case __arrayStringIndex:
          return javaArrayToTerm(new ArrayList<Object>(termToJavaArrayList(children[0]).subList(0, termToJavaInteger(children[1])))); // Cannot cast, so make
                                                                                                                                      // new,sigh
        case __listStringIndex:
          return javaListToTerm(termToJavaLinkedList(children[0]).subList(0, termToJavaInteger(children[1])));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;
      case __suffixStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __stringStringIndex:
          return javaStringToTerm(termToJavaString(children[0]).substring(termToJavaInteger(children[1])));
        case __arrayStringIndex:
          ArrayList<Object> arrayList = termToJavaArrayList(children[0]);
          return javaArrayToTerm(new ArrayList<Object>(arrayList.subList(termToJavaInteger(children[1]), arrayList.size())));
        case __listStringIndex:
          List<Object> list = termToJavaLinkedList(children[0]);
          return javaListToTerm(list.subList(termToJavaInteger(children[1]), list.size()));
        default:
          Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
          break;
        }
        break;

      case __uniteStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __setStringIndex:
          Set<Object> set = termToJavaLinkedHashSet(children[0]);
          set.addAll(termToJavaLinkedHashSet(children[1]));
          return javaSetToTerm(set);
        }
        break;

      case __intersectStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __setStringIndex:
          Set<Object> set = termToJavaLinkedHashSet(children[0]);
          set.retainAll(termToJavaLinkedHashSet(children[1]));
          return javaSetToTerm(set);
        }
        break;

      case __diffStringIndex:
        switch (firstChildSymbolStringIndex) {
        case __setStringIndex:
          Set<Object> set = termToJavaLinkedHashSet(children[0]);
          set.removeAll(termToJavaLinkedHashSet(children[1]));
          return javaSetToTerm(set);
        }
        break;

      case __castStringIndex:
        switch (firstChildSymbolStringIndex) {

        case __int32StringIndex:
          switch (secondChildSymbolStringIndex) {
          case __int32StringIndex:
            return children[0]; // No change
          case __intAPStringIndex:
            return javaBigIntegerToTerm(new BigInteger(termToJavaInteger(children[0]).toString()));
          case __real64StringIndex:
            return javaDoubleToTerm((double) termToJavaInteger(children[0]));
          case __realAPStringIndex:
            return javaBigDecimalToTerm(new BigDecimal(termToJavaInteger(children[0]).toString()));
          }

        default:
          Util.fatal("Cannot cast " + getString(firstChildSymbolStringIndex) + " to " + getString(secondChildSymbolStringIndex));
          return termBottom;
        }

      default:
        Util.fatal("Unrecognised operation " + toRawString(term));
        break;
      }
      break;

    case 3:
    case __putStringIndex:
      switch (firstChildSymbolStringIndex) {
      case __arrayStringIndex:
        ArrayList<Object> arrayList = termToJavaArrayList(children[0]);
        arrayList.set(termToJavaInteger(children[1]), termToJavaObject(children[2]));
        return javaArrayToTerm(arrayList);
      case __listStringIndex:
        List<Object> list = termToJavaLinkedList(children[0]);
        list.add(termToJavaInteger(children[1]), termToJavaObject(children[2]));
        return javaListToTerm(list);
      case __setStringIndex:
        Util.fatal("__put on __set must have arity 2: __put(__set, X)");
        return termBottom;
      case __mapStringIndex:
        Map<Object, Object> map = termToJavaLinkedHashMap(children[0]);
        map.put(termToJavaObject(children[1]), termToJavaObject(children[2]));
        return javaMapToTerm(map);

      default:
        Util.fatal("Operation " + getString(termSymbolStringIndex) + " not applicable to type " + getString(firstChildSymbolStringIndex));
      }
      break;

    default:
      Util.fatal("Unrecognised operation " + toRawString(term));
      return termBottom;
    }
    Util.fatal("Internal evaluator flowcontrol error " + toRawString(term));
    return termBottom;
  }

  public boolean hasSymbol(Integer term, String string) {
    // System.out.println("Checking term " + toString(term) + " against symbol " + string);
    return termSymbolStringIndex(term) == this.findString(string);
  }

  public void mustHaveSymbol(int term, String symbol) {
    if (!hasSymbol(term, symbol)) Util.fatal("Term " + toString(term) + "failed type check type against" + symbol);
  }

  public void mustHaveSymbol(int term, String symbol, int arity) {
    mustHaveSymbol(term, symbol);
    if (termArity(term) != arity) Util.fatal("Term " + toString(term) + "failed type check type against" + symbol + " with arity " + arity);
  }

  /* Conversions between Java values and terms ************************************************************************************/
  public Boolean termToJavaBoolean(int term) {
    mustHaveSymbol(term, "__bool", 1);
    switch (termSymbolString(termChildren(term)[0])) {
    case "true":
      return true;
    case "false":
      return false;
    }
    Util.fatal("Term " + toString(term) + " is not a valid __bool");
    return false;
  }

  public int javaBooleanToTerm(Boolean value) {
    return value ? termBooleanTrue : termBooleanFalse;
  }

  public Character termToJavaCharacter(int term) {
    mustHaveSymbol(term, "__char", 1);
    return termSymbolString(termChildren(term)[0]).charAt(0);
  }

  public int javaCharacterToTerm(Character value) {
    return findTerm(__charStringIndex, findTerm(value.toString()));
  }

  public Integer termToJavaInteger(int term) {
    mustHaveSymbol(term, "__int32", 1);
    return Integer.parseInt(termSymbolString(termChildren(term)[0]));
  }

  public int javaIntegerToTerm(Integer value) {
    return findTerm(__int32StringIndex, findTerm(value.toString()));
  }

  public BigInteger termToJavaBigInteger(int term) {
    mustHaveSymbol(term, "__intAP", 1);
    return new BigInteger(termSymbolString(termChildren(term)[0]));
  }

  public int javaBigIntegerToTerm(BigInteger value) {
    return findTerm(__intAPStringIndex, findTerm(value.toString()));
  }

  public Double termToJavaDouble(int term) {
    mustHaveSymbol(term, "__real64", 1);
    return Double.parseDouble(termSymbolString(termChildren(term)[0]));
  }

  public int javaDoubleToTerm(Double value) {
    return findTerm(__real64StringIndex, findTerm(value.toString()));
  }

  public BigDecimal termToJavaBigDecimal(int term) {
    mustHaveSymbol(term, "__realAP", 1);
    return new BigDecimal(termSymbolString(termChildren(term)[0]));
  }

  public int javaBigDecimalToTerm(BigDecimal value) {
    return findTerm(__realAPStringIndex, findTerm(value.toString()));
  }

  // Strings - watch outfor special case of __string with no children denoting the empty string
  public String termToJavaString(int term) {
    mustHaveSymbol(term, "__string", 1);
    if (termArity(term) == 0) return "";
    return termSymbolString(termChildren(term)[0]);
  }

  public int javaStringToTerm(String value) {
    if (value.length() == 0) return termStringEmpty;
    return findTerm(__stringStringIndex, findTerm(value.toString()));
  }

  // Arrays
  public ArrayList<Object> termToJavaArrayList(int term) {
    mustHaveSymbol(term, "__array");

    ArrayList<Object> ret = new ArrayList<>();
    int size = termToJavaInteger(termChildren(term)[0]);
    if (size > 0) { // Load elements
      term = termChildren(term)[1];
      while (termArity(term) == 2) {
        int[] children = termChildren(term);
        ret.add(termToJavaObject(children[0]));
        term = children[1];
      }
      ret.add(termToJavaObject(termChildren(term)[0])); // Add in the last element
    }
    if (size != ret.size()) Util.fatal("Array size does not match element count");
    return ret;
  }

  public int javaArrayToTerm(ArrayList<Object> value) {
    Iterator<Object> i = value.iterator();
    if (i.hasNext()) return findTerm("__array", javaIntegerToTerm(value.size()), javaElementsToTermRec("__a", i));
    return termArrayEmpty;
  }

  // List
  public LinkedList<Object> termToJavaLinkedList(int term) {
    mustHaveSymbol(term, "__list");
    LinkedList<Object> ret = new LinkedList<>();
    if (termArity(term) == 0) return ret; // Empty
    term = termChildren(term)[0];
    while (termArity(term) == 2) {
      int[] children = termChildren(term);
      ret.add(termToJavaObject(children[0]));
      term = children[1];
    }
    ret.add(termToJavaObject(termChildren(term)[0])); // Add in the last element
    return ret;
  }

  public int javaListToTerm(List<?> value) {
    Iterator<?> i = value.iterator();
    if (i.hasNext()) return findTerm("__list", javaElementsToTermRec("__l", i));
    return termListEmpty;
  }

  // Set
  public LinkedHashSet<Object> termToJavaLinkedHashSet(int term) { // Use LinkedHashSet to mimic linked list 'pure' rules
    mustHaveSymbol(term, "__set");
    LinkedHashSet<Object> ret = new LinkedHashSet<>();
    if (termArity(term) == 0) return ret; // Empty
    term = termChildren(term)[0];
    while (termArity(term) == 2) {
      int[] children = termChildren(term);
      ret.add(termToJavaObject(children[0]));
      term = children[1];
    }
    ret.add(termToJavaObject(termChildren(term)[0])); // Add in the last element
    return ret;
  }

  public int javaSetToTerm(Set<?> value) {
    Iterator<?> i = value.iterator();
    if (i.hasNext()) return findTerm("__set", javaElementsToTermRec("__s", i));
    return termSetEmpty;
  }

  private int javaElementsToTermRec(String constructor, Iterator<?> i) {
    var e = i.next();
    if (i.hasNext()) {
      return findTerm(constructor, javaObjectToTerm(e), javaElementsToTermRec(constructor, i));
    }
    return findTerm(constructor, javaObjectToTerm(e));
  }

  // Map
  public LinkedHashMap<Object, Object> termToJavaLinkedHashMap(int term) { // Use LinkedHashMap to mimic linked list 'pure' rules
    mustHaveSymbol(term, "__map");
    LinkedHashMap<Object, Object> ret = new LinkedHashMap<>();
    if (termArity(term) == 0) return ret; // Empty
    term = termChildren(term)[0];
    while (termArity(term) == 3) {
      int[] children = termChildren(term);
      ret.put(termToJavaObject(children[0]), termToJavaObject(children[1]));
      term = children[2];
    }
    ret.put(termToJavaObject(termChildren(term)[0]), termToJavaObject(termChildren(term)[1])); // Add in the last element
    return ret;
  }

  public int javaMapToTerm(Map<?, ?> value) {
    Iterator<?> i = value.keySet().iterator();
    if (i.hasNext()) return findTerm("__map", javaMapToTermRec(value, i));
    return termMapEmpty;
  }

  private int javaMapToTermRec(Map<?, ?> value, Iterator<?> i) {
    var k = i.next();
    if (i.hasNext()) {
      return findTerm("__m", javaObjectToTerm(k), javaObjectToTerm(value.get(k)), javaMapToTermRec(value, i));
    }
    return findTerm("__m", javaObjectToTerm(k), javaObjectToTerm(value.get(k)));
  }

  // Blob
  public Blob termToJavaBlob(int term) {
    return Blob.get(term);
  }

  public int javaBlobToTerm(Blob value) {
    return findTerm("__blob", javaIntegerToTerm(value.number));
  }

  public Object termToJavaObject(int term) {
    switch (termSymbolStringIndex(term)) {
    case __bottomStringIndex:
      return bottom;
    case __doneStringIndex:
      return done;
    case __emptyStringIndex:
      return empty;
    case __quoteStringIndex:
      return new Quote(subTerm(term, 0));
    case __blobStringIndex:
      return termToJavaBlob(term);
    case __boolStringIndex:
      return termToJavaBoolean(term);
    case __charStringIndex:
      return termToJavaCharacter(term);
    case __intAPStringIndex:
      return termToJavaBigInteger(term);
    case __int32StringIndex:
      return termToJavaInteger(term);
    case __realAPStringIndex:
      return termToJavaBigDecimal(term);
    case __real64StringIndex:
      return termToJavaDouble(term);
    case __stringStringIndex:
      return termToJavaString(term);
    case __listStringIndex:
      return termToJavaLinkedList(term);
    case __setStringIndex:
      return termToJavaLinkedHashSet(term);
    case __mapStringIndex:
      return termToJavaLinkedHashMap(term);
    default:
      return new Quote(term); // For anything else, autoquote it
    }
  }

  private int subTerm(int term, int i) {
    // TODO Auto-generated method stub
    return 0;
  }

  // Note that in later versions of Java this can be replaced by pattern matching on class
  // This version is acceptable to Java 17 (Sep 21) which is the student default for AY 2024-45
  public int javaObjectToTerm(Object value) {
    if (value == null) Util.fatal("Cannot convert Java null to a term - try Botton, Done or Empty");
    if (value instanceof Quote) return ((Quote) value).term;
    if (value instanceof Bottom) return termBottom;
    if (value instanceof Done) return termDone;
    if (value instanceof Empty) return termEmpty;
    if (value instanceof Blob) return javaBlobToTerm((Blob) value);
    if (value instanceof Boolean) return javaBooleanToTerm((Boolean) value);
    if (value instanceof Character) return javaCharacterToTerm((Character) value);
    if (value instanceof Integer) return javaIntegerToTerm((Integer) value);
    if (value instanceof BigInteger) return javaBigIntegerToTerm((BigInteger) value);
    if (value instanceof Double) return javaDoubleToTerm((Double) value);
    if (value instanceof BigDecimal) return javaBigDecimalToTerm((BigDecimal) value);
    if (value instanceof String) return javaStringToTerm((String) value);
    if (value instanceof List<?>) return javaListToTerm((List<?>) value);
    if (value instanceof Set<?>) return javaSetToTerm((Set<?>) value);
    if (value instanceof Map<?, ?>) return javaMapToTerm((Map<?, ?>) value);
    Util.fatal("Java Class has no ART Value partner type: " + value.getClass());
    return termBottom; // Illegal object class with no term equivalent
  }

  /* String index constant management **********************************************************************/
  // Whenever types or operations are added to or removed form the value system, run this mainline and replace the constants and loadStrings()
  public static void main(String[] args) {
    // @formatter:off
    String[] s = {
"__bottom",
"__done",
"__empty",
"__quote",
"__blob",
"__adtprod",
"__adtsum",
"__proc",
"__bool",
"__char",
"__intAP",
"__int32",
"__realAP",
"__real64",
"__string",
"__array", "__a",
"__list", "__l",
"__set", "__s",
"__map", "__m",

"__eq", "__ne", "__gt", "__lt", "__ge", "__le", "__comp",
"__not", "__and", "__or", "__xor",
"__shift", "__sshift", "__rot",
"__neg", "__add", "__sub", "__mul", "__div", "__mod", "__exp",

"__card",  "__put", "__get", "__remove", "__cat", "__prefix", "__suffix",
"__unite", "__intersect", "__diff",

"__cast", "__termArity", "__termRoot", "__plugin"
};
    // @formatter:on

    int symbolValue = variableCount + sequenceVariableCount + 1 + 2;
    System.out.print("public final int " + s[0] + "StringIndex = " + (symbolValue++));
    for (int i = 1; i < s.length; i++)
      System.out.print(", " + s[i] + "StringIndex = " + (symbolValue++));
    System.out.println(";");

    symbolValue = variableCount + sequenceVariableCount + 1 + 2;
    System.out.println("void loadStrings() {");
    for (String sym : s)
      System.out.println("loadString(\"" + sym + "\"," + symbolValue++ + ");");
    System.out.println("}");
  }

  private void loadString(String string, int index) {
    if (findString(string) != index) System.out.println("String index mismatch for " + string);
  }

  public final int __bottomStringIndex = 33, __doneStringIndex = 34, __emptyStringIndex = 35, __quoteStringIndex = 36, __blobStringIndex = 37,
      __adtprodStringIndex = 38, __adtsumStringIndex = 39, __procStringIndex = 40, __boolStringIndex = 41, __charStringIndex = 42, __intAPStringIndex = 43,
      __int32StringIndex = 44, __realAPStringIndex = 45, __real64StringIndex = 46, __stringStringIndex = 47, __arrayStringIndex = 48, __aStringIndex = 49,
      __listStringIndex = 50, __lStringIndex = 51, __setStringIndex = 52, __sStringIndex = 53, __mapStringIndex = 54, __mStringIndex = 55, __eqStringIndex = 56,
      __neStringIndex = 57, __gtStringIndex = 58, __ltStringIndex = 59, __geStringIndex = 60, __leStringIndex = 61, __compStringIndex = 62,
      __notStringIndex = 63, __andStringIndex = 64, __orStringIndex = 65, __xorStringIndex = 66, __shiftStringIndex = 67, __sshiftStringIndex = 68,
      __rotStringIndex = 69, __negStringIndex = 70, __addStringIndex = 71, __subStringIndex = 72, __mulStringIndex = 73, __divStringIndex = 74,
      __modStringIndex = 75, __expStringIndex = 76, __cardStringIndex = 77, __putStringIndex = 78, __getStringIndex = 79, __removeStringIndex = 80,
      __catStringIndex = 81, __prefixStringIndex = 82, __suffixStringIndex = 83, __uniteStringIndex = 84, __intersectStringIndex = 85, __diffStringIndex = 86,
      __castStringIndex = 87, __termArityStringIndex = 88, __termRootStringIndex = 89, __pluginStringIndex = 90;

  void loadStrings() {
    loadString("__bottom", 33);
    loadString("__done", 34);
    loadString("__empty", 35);
    loadString("__quote", 36);
    loadString("__blob", 37);
    loadString("__adtprod", 38);
    loadString("__adtsum", 39);
    loadString("__proc", 40);
    loadString("__bool", 41);
    loadString("__char", 42);
    loadString("__intAP", 43);
    loadString("__int32", 44);
    loadString("__realAP", 45);
    loadString("__real64", 46);
    loadString("__string", 47);
    loadString("__array", 48);
    loadString("__a", 49);
    loadString("__list", 50);
    loadString("__l", 51);
    loadString("__set", 52);
    loadString("__s", 53);
    loadString("__map", 54);
    loadString("__m", 55);
    loadString("__eq", 56);
    loadString("__ne", 57);
    loadString("__gt", 58);
    loadString("__lt", 59);
    loadString("__ge", 60);
    loadString("__le", 61);
    loadString("__comp", 62);
    loadString("__not", 63);
    loadString("__and", 64);
    loadString("__or", 65);
    loadString("__xor", 66);
    loadString("__shift", 67);
    loadString("__sshift", 68);
    loadString("__rot", 69);
    loadString("__neg", 70);
    loadString("__add", 71);
    loadString("__sub", 72);
    loadString("__mul", 73);
    loadString("__div", 74);
    loadString("__mod", 75);
    loadString("__exp", 76);
    loadString("__card", 77);
    loadString("__put", 78);
    loadString("__get", 79);
    loadString("__remove", 80);
    loadString("__cat", 81);
    loadString("__prefix", 82);
    loadString("__suffix", 83);
    loadString("__unite", 84);
    loadString("__intersect", 85);
    loadString("__diff", 86);
    loadString("__cast", 87);
    loadString("__termArity", 88);
    loadString("__termRoot", 89);
    loadString("__plugin", 90);
  }
}
