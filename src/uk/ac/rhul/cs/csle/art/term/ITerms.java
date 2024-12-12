package uk.ac.rhul.cs.csle.art.term;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ITerms {
  /* Term class - to be replaced by HashPool style implementation *******************************************/
  // @formatter:off
  class ITerm { // Replace at some point with a HashPool implementation
    private final int symbolIndex;
    private final int[] children;

    public ITerm(int symbolIndex, int[] children) { this.symbolIndex = symbolIndex; this.children = children; }

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
  public ARTPluginInterface plugin = new ARTPlugin();
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

    // 5. Connect to a plugin - either the default built in to art.jar or one in the use class path ifone can be found
    plugin = new ARTPlugin();
    // Try and find a plugin for __user() calls
    Class<?> pluginClass;
    try {
      pluginClass = getClass().getClassLoader().loadClass("ValueUserPlugin");
      plugin = (ARTPluginInterface) pluginClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Silently absorb exception - when the default is used
    }
    if (plugin.name() != null) System.out.println("Attached to plugin: " + plugin.name());
  }

  /* Raw term rendering *************************************************************************************/
  public String toString(int term) {
    if (term == 0) return "null term";
    return rawTextTraverser.toString(term);
  }

  public String toString(Integer term, Boolean indent, Integer depthLimit, Map<Integer, Integer> localAliases) {
    if (term == 0) return "null term";
    return rawTextTraverser.toString(term, indent, depthLimit, localAliases);
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
        if (!metaCharacters.contains(c)) throw new RewriteException("iTerms.unescapeMeta found escaped non-meta character");
      }
      sb.append(c);
      i++;
    }
    return sb.toString();
  }

  /* Parse a term written in a human-comfortable string form with abbreviations for some constants **********/

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
    if (symbolNameString.length() > 0 && symbolNameString.charAt(0) == '_') {
      if (symbolNameString.length() > 1 && symbolNameString.charAt(1) == '_') {// two underscores so must be intrinsic function or type
        if (!isSpecialSymbol(symbolNameStringIndex)) parseSyntaxError("unknown evaluatable function: " + symbolNameString);
      } else {
        if (!isVariableSymbol(symbolNameStringIndex) && !isSequenceVariableSymbol(symbolNameStringIndex)) parseSyntaxError("unknown variable");
      }
    }

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

    if (isSequenceVariableTerm(openTermIndex)) throw new RewriteException("in matchZeroSV() right hand side must not contain sequence variables");

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
  // Evaluate an operation over constants: silently return term if we don't recognise an operation
  public int evaluateTerm(int term, int[] children) {
    if (children.length == 0 || !termSymbolString(term).startsWith("__")) return term; // Nothing to do

    int termSymbolStringIndex = termSymbolStringIndex(term);
    int firstChildSymbolStringIndex = termSymbolStringIndex(children[0]);

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
        }
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
        }
      case __sizeStringIndex:
      }
    case 2:
      switch (termSymbolStringIndex) {
      case __eqStringIndex:
        return javaBooleanToTerm(children[0] == children[1]);
      case __neStringIndex:
        return javaBooleanToTerm(children[0] != children[1]);
      // case __compareStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__compare(r).toString());
      // case __eqStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__eq(r).toString());
      // case __neStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__ne(r).toString());
      // case __gtStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__gt(r).toString());
      // case __ltStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__lt(r).toString());
      // case __geStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__ge(r).toString());
      // case __leStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__le(r).toString());
      // case __andStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__and(r).toString());
      // case __orStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__or(r).toString());
      // case __xorStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__xor(r).toString());
      // case __lshStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__lsh(r).toString());
      // case __rshStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__rsh(r).toString());
      // case __ashStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__ash(r).toString());
      // case __addStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__add(r).toString());
      // case __subStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__sub(r).toString());
      // case __mulStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__mul(r).toString());
      // case __divStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__div(r).toString());
      // case __modStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__mod(r).toString());
      // case __expStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__exp(r).toString());
      // case __catStringIndex:
      // return findTerm(l.__cat(r).toString());
      // case __putStringIndex:
      // return findTerm(l.__put(r).toString());
      // case __getStringIndex:
      // return findTerm(l.__get(r).toString());
      // case __containsStringIndex:
      // return findTerm(l.__contains(r).toString());
      // case __removeStringIndex:
      // return findTerm(l.__remove(r).toString());
      // case __unionStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__union(r).toString());
      // case __intersectionStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__intersection(r).toString());
      // case __differenceStringIndex:
      // if (mixCheck(l, r, term)) return bottomTermIndex;
      // return findTerm(l.__difference(r).toString());
      // // Now the cast despatcher
      // case __castStringIndex: {
      // int targetType = getTermSymbolStringIndex(children[1]);
      // if (targetType == __quoteStringIndex) targetType = getTermSymbolStringIndex(this.getTermChildren(children[1])[0]);
      // switch (targetType) {
      // case __boolStringIndex:
      // return findTerm(l.__cast__bool().toString());
      // case __charStringIndex:
      // return findTerm(l.__cast__char().toString());
      // case __intAPStringIndex:
      // return findTerm(l.__cast__intAP().toString());
      // case __int32StringIndex:
      // return findTerm(l.__cast__int32().toString());
      // case __realAPStringIndex:
      // return findTerm(l.__cast__realAP().toString());
      // case __real64StringIndex:
      // return findTerm(l.__cast__real64().toString());
      // case __stringStringIndex:
      // return findTerm(l.__cast__string().toString());
      // case __setStringIndex:
      // return findTerm(l.__cast__set().toString());
      // default:
      // throw new RewriteException("illegal __cast target type " + tt.toString(children[1]));
      // }
      // }
      }
    case 3:
      // switch (termSymbolStringIndex) {
      // case __putStringIndex:
      // return findTerm(l.__put(r, rr).toString());
      // }
    }
    return term; // Nothing to do
  }

  public boolean hasSymbol(Integer term, String string) {
    // System.out.println("Checking term " + toString(term) + " against symbol " + string);
    return this.termSymbolStringIndex(term) == this.findString(string);
  }

  public void mustHaveSymbol(int term, String symbol) {
    if (!hasSymbol(term, symbol)) throw new ValueException("Term " + toString(term) + "failed type check type against" + symbol);
  }

  public void mustHaveSymbol(int term, String symbol, int arity) {
    mustHaveSymbol(term, symbol);
    if (termArity(term) != arity) throw new ValueException("Term " + toString(term) + "failed type check type against" + symbol + " with arity " + arity);
  }

  /* Conversions between Java values and terms ************************************************************************************/
  public Boolean termToJavaBoolean(int term) {
    mustHaveSymbol(term, "__boolean", 1);
    switch (termSymbolString(termChildren(term)[0])) {
    case "true":
      return true;
    case "false":
      return false;
    }
    throw new ValueException("Term " + toString(term) + " is not a valid __bool");
  }

  public int javaBooleanToTerm(Boolean value) {
    return value ? termBooleanTrue : termBooleanFalse;
  }

  public Character termToJavaCharacter(int term) {
    mustHaveSymbol(term, "__char", 1);
    return termSymbolString(termChildren(term)[0]).charAt(0);
  }

  public int javaCharacterToTerm(Character value) {
    throw new ValueException("javaCharacterToTerm not yet implemented");
  }

  public Integer termToJavaInteger(int term) {
    mustHaveSymbol(term, "__int32", 1);
    return Integer.parseInt(termSymbolString(termChildren(term)[0]));
  }

  public int javaIntegerToTerm(Integer value) {
    throw new ValueException("javaIntegerToTerm not yet implemented");
  }

  public BigInteger termToJavaBigInteger(int term) {
    mustHaveSymbol(term, "__intAP", 1);
    return new BigInteger(termSymbolString(termChildren(term)[0]));
  }

  public int javaBigIntegerToTerm(BigInteger value) {
    throw new ValueException("javaBigIntegerToTerm not yet implemented");
  }

  public Double termToJavaDouble(int term) {
    mustHaveSymbol(term, "__real64", 1);
    return Double.parseDouble(termSymbolString(termChildren(term)[0]));
  }

  public int javaDoubleToTerm(Double value) {
    throw new ValueException("javaDoubleToTerm not yet implemented");
  }

  public BigDecimal termToJavaBigDecimal(int term) {
    mustHaveSymbol(term, "__realAP", 1);
    return new BigDecimal(termSymbolString(termChildren(term)[0]));
  }

  public int javaBigDecimalToTerm(BigDecimal value) {
    throw new ValueException("javaBigDecimalToTerm not yet implemented");
  }

  public String termToJavaString(int term) {
    mustHaveSymbol(term, "__string", 1);
    return termSymbolString(termChildren(term)[0]);
  }

  public int javaStringToTerm(String value) {
    throw new ValueException("javaStringToTerm not yet implemented");
  }

  public LinkedList termToJavaLinkedList(int term) {
    mustHaveSymbol(term, "__list", 2);
    throw new ValueException("termToJavaLinkedList not yet implemented");
  }

  public int javaListToTerm(List value) {
    throw new ValueException("javaListToTerm not yet implemented");
  }

  public LinkedList termToJavaHashMap(int term) {
    mustHaveSymbol(term, "__set", 2);
    throw new ValueException("termToJavaHashMap not yet implemented");
  }

  public int javaMapToTerm(Map value) {
    throw new ValueException("javaMapToTerm not yet implemented");
  }

  /* String index constant management **********************************************************************/
  // Whenever types or operations are added to or removed form the value system, run this mainline and replace the constants and loadStrings()
  public static void main(String[] args) {
    String[] s = { "__map", "__bottom", "__done", "__empty", "__quote", "__blob", "__keyval", "__adtProd", "__adtSum", "__proc", "__bool", "__char", "__intAP",
        "__int32", "__realAP", "__real64", "__string", "__list", "__set", "__eq", "__ne", "__gt", "__lt", "__ge", "__le", "__compare", "__not", "__and", "__or",
        "__xor", "__lsh", "__rsh", "__ash", "__neg", "__add", "__sub", "__mul", "__div", "__mod", "__exp", "__size", "__cat", "__slice", "__get", "__put",
        "__contains", "__remove", "__extract", "__union", "__intersection", "__difference", "__cast", "__termArity", "__termRoot", "__plugin" };
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

  public final int __mapStringIndex = 33, __bottomStringIndex = 34, __doneStringIndex = 35, __emptyStringIndex = 36, __quoteStringIndex = 37,
      __blobStringIndex = 38, __keyvalStringIndex = 39, __adtProdStringIndex = 40, __adtSumStringIndex = 41, __procStringIndex = 42, __boolStringIndex = 43,
      __charStringIndex = 44, __intAPStringIndex = 45, __int32StringIndex = 46, __realAPStringIndex = 47, __real64StringIndex = 48, __stringStringIndex = 49,
      __listStringIndex = 50, __setStringIndex = 51, __eqStringIndex = 52, __neStringIndex = 53, __gtStringIndex = 54, __ltStringIndex = 55,
      __geStringIndex = 56, __leStringIndex = 57, __compareStringIndex = 58, __notStringIndex = 59, __andStringIndex = 60, __orStringIndex = 61,
      __xorStringIndex = 62, __lshStringIndex = 63, __rshStringIndex = 64, __ashStringIndex = 65, __negStringIndex = 66, __addStringIndex = 67,
      __subStringIndex = 68, __mulStringIndex = 69, __divStringIndex = 70, __modStringIndex = 71, __expStringIndex = 72, __sizeStringIndex = 73,
      __catStringIndex = 74, __sliceStringIndex = 75, __getStringIndex = 76, __putStringIndex = 77, __containsStringIndex = 78, __removeStringIndex = 79,
      __extractStringIndex = 80, __unionStringIndex = 81, __intersectionStringIndex = 82, __differenceStringIndex = 83, __castStringIndex = 84,
      __termArityStringIndex = 85, __termRootStringIndex = 86, __pluginStringIndex = 87;

  void loadStrings() {
    loadString("__map", 33);
    loadString("__bottom", 34);
    loadString("__done", 35);
    loadString("__empty", 36);
    loadString("__quote", 37);
    loadString("__blob", 38);
    loadString("__keyval", 39);
    loadString("__adtProd", 40);
    loadString("__adtSum", 41);
    loadString("__proc", 42);
    loadString("__bool", 43);
    loadString("__char", 44);
    loadString("__intAP", 45);
    loadString("__int32", 46);
    loadString("__realAP", 47);
    loadString("__real64", 48);
    loadString("__string", 49);
    loadString("__list", 50);
    loadString("__set", 51);
    loadString("__eq", 52);
    loadString("__ne", 53);
    loadString("__gt", 54);
    loadString("__lt", 55);
    loadString("__ge", 56);
    loadString("__le", 57);
    loadString("__compare", 58);
    loadString("__not", 59);
    loadString("__and", 60);
    loadString("__or", 61);
    loadString("__xor", 62);
    loadString("__lsh", 63);
    loadString("__rsh", 64);
    loadString("__ash", 65);
    loadString("__neg", 66);
    loadString("__add", 67);
    loadString("__sub", 68);
    loadString("__mul", 69);
    loadString("__div", 70);
    loadString("__mod", 71);
    loadString("__exp", 72);
    loadString("__size", 73);
    loadString("__cat", 74);
    loadString("__slice", 75);
    loadString("__get", 76);
    loadString("__put", 77);
    loadString("__contains", 78);
    loadString("__remove", 79);
    loadString("__extract", 80);
    loadString("__union", 81);
    loadString("__intersection", 82);
    loadString("__difference", 83);
    loadString("__cast", 84);
    loadString("__termArity", 85);
    loadString("__termRoot", 86);
    loadString("__plugin", 87);
  }
}
