package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class LexerTWE extends AbstractLexer {

  class tweSetElement {
    CFGElement element;
    int wsPrefix;
    int rightExtent;
  }

  public int[][] tweSet;
  private final boolean[] hasLeftExtent = new boolean[this.inputString.length()];

  @Override
  public void lex(String userString, CFGRules cfgRules) {
    inputString = userString + "\0";
    hasLeftExtent[0] = true;
    Set<Integer> allTokenNumbers = new HashSet<>();
    for (int i = 0; i < cfgRules.lexicalKindsArray.length; i++)
      allTokenNumbers.add(i);

    for (int i = 0; i < inputString.length(); i++)
      if (hasLeftExtent[i]) lexSlice(i, allTokenNumbers);
  }

  public void lexSlice(int i, Set<Integer> validTokens) {
    Set<tweSetElement> slice = new HashSet<>();

    for (var t : validTokens)
      // System.out.print("Checking builtin " + b + "/" + s + " at inputIndex " + inputIndex);

      switch (cfgRules.lexicalKindsArray[t]) {
      case CHARACTER:
        match_CHARACTER(cfgRules.lexicalStringsArray[t]);
        break;
      case SINGLETON_CASE_INSENSITIVE:
        match_SINGLETON_CASE_INSENSITIVE(cfgRules.lexicalStringsArray[t]);
        break;
      case SINGLETON_CASE_SENSITIVE:
        match_SINGLETON_CASE_SENSITIVE(cfgRules.lexicalStringsArray[t]);
        break;
      case ID:
        match_ID();
        break;
      case INTEGER:
        match_INTEGER();
        break;
      case SIGNED_INTEGER:
        match_SIGNED_INTEGER();
        break;
      case AP_INTEGER:
        match_AP_INTEGER();
        break;
      case REAL:
        match_REAL();
        break;
      case AP_REAL:
        match_AP_REAL();
        break;
      case SIGNED_REAL:
        match_SIGNED_REAL();
        break;
      case CHAR_BQ:
        match_CHAR_BQ();
        break;
      case STRING_DQ:
        match_STRING_DQ();
        break;
      case STRING_SQ:
        match_STRING_SQ();
        break;
      case STRING_PLAIN_SQ:
        match_STRING_PLAIN_SQ();
        break;
      case STRING_BRACE_NEST:
        match_STRING_BRACE_NEST();
        break;
      case STRING_BRACKET_NEST:
        match_STRING_BRACKET_NEST();
        break;
      case STRING_DOLLAR:
        match_STRING_DOLLAR();
        break;
      case STRING_SHRIEK_SHRIEK:
        match_STRING_SHRIEK_SHREIK();
        break;
      case SIMPLE_WHITESPACE:
        match_SIMPLE_WHITESPACE();
        break;
      case COMMENT_BLOCK_C:
        match_COMMENT_BLOCK_C();
        break;
      case COMMENT_LINE_C:
        match_COMMENT_LINE_C();
        break;
      case COMMENT_NEST_ART:
        match_COMMENT_NEST_ART();
        break;

      case SML_COMMENT:
        match_SML_COMMENT();
        break;
      case SML_D:
        match_SML_D();
        break;
      case SML_INT:
        match_SML_INT();
        break;
      case SML_WORD:
        match_SML_WORD();
        break;
      case SML_REAL:
        match_SML_REAL();
        break;
      case SML_CHAR:
        match_SML_CHAR();
        break;
      case SML_STRING:
        match_SML_STRING();
        break;
      case SML_VID:
        match_SML_VID();
        break;
      case SML_TYVAR:
        match_SML_TYVAR();
        break;
      case SML_TYCON:
        match_SML_TYCON();
        break;
      case SML_LAB:
        match_SML_LAB();
        break;
      case SML_STRID:
        match_SML_STRID();
        break;
      case SML_SYMID:
        match_SML_SYMID();
        break;

      default:
        Util.fatal("Unknown builtin " + cfgRules.lexicalKindsArray[t]);
        break;
      }
    // Util.info(" return " + inputIndex);
  }

  @Override
  public void printLexicalisations(boolean raw) {
    // TODO Auto-generated method stub

  }

  @Override
  public void statistics(Statistics statistics) {
    // TODO Auto-generated method stub

  }

}
