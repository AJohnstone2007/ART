package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class LexerBaseLine extends AbstractLexer {
  private boolean[] hasSlice;

  @Override
  public boolean lex(String userString, CFGRules cfgRules, ChooseRules chooseRules) {
    this.cfgRules = cfgRules;
    // Util.debug("Grammar" + cfgRules + "end of grammar");
    inputString = userString + "\0";
    inputAsCharArray = inputString.toCharArray();

    tweSlices = new TWESetElement[inputString.length() + 1][];
    hasSlice = new boolean[inputString.length() + 1];
    inputIndex = 0;
    uibs = new TreeMap<>();
    uobs = new TreeMap<>();

    whitespaceLongstMatch();

    whitespacePrefix = inputIndex;// Old style - tells us the single longest whitespace prefix
    hasSlice[0] = true;

    for (int i = 0; i < inputString.length(); i++)
      if (hasSlice[i]) tweSlices[i] = constructTWESlice(i);

    // if (!hasSlice[inputString.length() - 1]) { // Lexical reject
    // int rightmostActiveSlice;
    // for (rightmostActiveSlice = inputString.length() - 1; rightmostActiveSlice >= 0; rightmostActiveSlice--)
    // if (hasSlice[rightmostActiveSlice]) break;
    //
    // lexicalError("Unknown lexeme starting with character " + (int) inputAsCharArray[rightmostActiveSlice] + " - " + inputAsCharArray[rightmostActiveSlice],
    // rightmostActiveSlice);
    //
    // tweSlices = null;
    // return false;
    // }

    // Add EOS
    tweSlices[inputString.length() - 1] = new TWESetElement[1];
    tweSlices[inputString.length() - 1][0] = new TWESetElement(cfgRules.endOfStringElement, inputString.length() - 1, inputString.length() - 1,
        inputString.length());

    if (ScriptInterpreter.seenChooseRule)
      choose();
    else
      chooseDefault();

    Util.debug("Lexicalisations");
    print(System.out, ScriptInterpreter.iTerms.rawTextTraverser, false, false, false);

    return true;
  }

  public TWESetElement[] constructTWESlice(int index) {
    Util.debug("Lexer at " + inputIndex);

    Set<TWESetElement> ret = new HashSet<>();
    // if (index == 0) ret.add(new TWESetElement(cfgRules.startOfStringElement, 1, 1, inputIndex));

    int lexemeStart = index == 0 ? whitespacePrefix : index; // Index zero is special case because of leading whitespace
    for (var e : cfgRules.elements.keySet()) {
      switch (e.cfgKind) {
      case TRM_CS, TRM_CI, TRM_BI, TRM_CH:
        inputIndex = lexemeStart;
        tryTokenMatch(e);
        if (inputIndex != lexemeStart) {// Matched?
          // Util.debug("constructTWESlice() matched " + e + " with right extent " + inputIndex + " - " + inputString.substring(lexemeStart, inputIndex));
          lexemeEnd = inputIndex;
          if (e.cfgKind != CFGElementKind.TRM_CH) whitespaceLongstMatch(); // absorb trailing whitespace
          ret.add(new TWESetElement(e, lexemeStart, lexemeEnd, inputIndex));
          hasSlice[inputIndex] = true; // Mark for downstream processing
        }
      }
    }
    if (ret.isEmpty()) {
      var ch = peekCh();
      if (cfgRules.characterSet.contains(ch)) {
        CFGElement uib = findUIB(getCh());
        Util.debug("Added to UIB: " + uib);
        ret.add(new TWESetElement(uib, lexemeStart, lexemeEnd, inputIndex));
        hasSlice[inputIndex] = true; // Mark for downstream processing
      } else {
        CFGElement uob = findUOB(getCh());
        Util.debug("Added to UOB: " + uob);
        ret.add(new TWESetElement(uob, lexemeStart, lexemeEnd, inputIndex));
        hasSlice[inputIndex] = true; // Mark for downstream processing
      }
    }

    return ret.toArray(new TWESetElement[0]);
  }

  private CFGElement findUOB(Character ch) {
    CFGElement candidate = new CFGElement(CFGElementKind.TRM_CH_UOB, ch.toString());
    if (uobs.get(candidate) == null) uobs.put(candidate, candidate);
    return uobs.get(candidate);
  }

  private CFGElement findUIB(Character ch) {
    CFGElement candidate = new CFGElement(CFGElementKind.TRM_CH_UIB, ch.toString());
    if (uibs.get(candidate) == null) uibs.put(candidate, candidate);
    return uibs.get(candidate);
  }

  private void whitespaceLongstMatch() {
    int wsStart;
    do {
      wsStart = inputIndex;
      for (var w : cfgRules.whitespaces)
        tryTokenMatch(w);
    } while (inputIndex != wsStart); // No more whitespace found
  }

  private void tryTokenMatch(CFGElement e) {
    // Util.debug("tryTokenMatch(" + e.toStringDetailed() + ") at inputIndex " + inputIndex);
    lexemeStart = inputIndex;
    switch (e.cfgKind) {
    default:
      Util.fatal("tryTokenMatch() on cfgElement with unexpected cfgKind " + e);
      break;
    case TRM_CS:
      match_SINGLETON_CASE_SENSITIVE(e.str);
      break;
    case TRM_CI:
      match_SINGLETON_CASE_INSENSITIVE(e.str);
      break;
    case TRM_CH:
      match_CHARACTER(e.str);
      break;
    case TRM_CH_SET:
      match_CHARACTER_SET(e.set);
      break;
    case TRM_CH_ANTI_SET:
      match_CHARACTER_SET(e.set); // care:note that anti-sets look for characters in the set
      break;
    case NONTERMINAL:
      Util.fatal("tryTokenMatch() in class " + this.getClass().getSimpleName() + " can never support paraterminals; try using !lexer gllRecogniser instead");
      break;
    case TRM_BI:
      switch (e.str) {
      case "ID":
        match_ID();
        break;
      case "INTEGER":
        match_INTEGER();
        break;
      case "SIGNED_INTEGER":
        match_SIGNED_INTEGER();
        break;
      case "AP_INTEGER":
        match_AP_INTEGER();
        break;
      case "REAL":
        match_REAL();
        break;
      case "AP_REAL":
        match_AP_REAL();
        break;
      case "SIGNED_REAL":
        match_SIGNED_REAL();
        break;
      case "CHAR_BQ":
        match_CHAR_BQ();
        break;
      case "STRING_DQ":
        match_STRING_DQ();
        break;
      case "STRING_SQ":
        match_STRING_SQ();
        break;
      case "STRING_PLAIN_SQ":
        match_STRING_PLAIN_SQ();
        break;
      case "STRING_BRACE":
        match_STRING_BRACE();
        break;
      case "STRING_BRACE_NEST":
        match_STRING_BRACE_NEST();
        break;
      case "STRING_BRACKET_NEST":
        match_STRING_BRACKET_NEST();
        break;
      case "STRING_DOLLAR":
        match_STRING_DOLLAR();
        break;
      case "STRING_SHRIEK_SHRIEK":
        match_STRING_SHRIEK_SHREIK();
        break;
      case "SIMPLE_WHITESPACE":
        match_SIMPLE_WHITESPACE();
        break;
      case "COMMENT_BLOCK_C":
        match_COMMENT_BLOCK_C();
        break;
      case "COMMENT_LINE_C":
        match_COMMENT_LINE_C();
        break;
      case "COMMENT_NEST_ART":
        match_COMMENT_NEST_ART();
        break;

      case "SML_COMMENT":
        match_SML_COMMENT();
        break;
      case "SML_D":
        match_SML_D();
        break;
      case "SML_INT":
        match_SML_INT();
        break;
      case "SML_WORD":
        match_SML_WORD();
        break;
      case "SML_REAL":
        match_SML_REAL();
        break;
      case "SML_CHAR":
        match_SML_CHAR();
        break;
      case "SML_STRING":
        match_SML_STRING();
        break;
      case "SML_VID":
        match_SML_VID();
        break;
      case "SML_TYVAR":
        match_SML_TYVAR();
        break;
      case "SML_TYCON":
        match_SML_TYCON();
        break;
      case "SML_LAB":
        match_SML_LAB();
        break;
      case "SML_STRID":
        match_SML_STRID();
        break;
      case "SML_SYMID":
        match_SML_SYMID();
        break;

      default:
        Util.fatal("Unknown builtin in lexer " + e);
        break;
      }
    }
  }

}