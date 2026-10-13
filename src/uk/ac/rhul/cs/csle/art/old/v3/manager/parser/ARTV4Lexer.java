package uk.ac.rhul.cs.csle.art.old.v3.manager.parser;

import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;

/*******************************************************************************
 *
 * ARTV4Lexer.java
 *
 *******************************************************************************/
public class ARTV4Lexer extends ARTLexerV3 {
  @Override
  public void artLexicaliseBuiltinInstances() {
    artBuiltin_CHAR_BQ();
    artLexicaliseTest(ARTV4Parser.ARTTB_CHAR_BQ);
    artBuiltin_ID();
    artLexicaliseTest(ARTV4Parser.ARTTB_ID);
    artBuiltin_SIGNED_INTEGER();
    artLexicaliseTest(ARTV4Parser.ARTTB_SIGNED_INTEGER);
    artBuiltin_SIGNED_REAL();
    artLexicaliseTest(ARTV4Parser.ARTTB_SIGNED_REAL);
    artBuiltin_STRING_BRACE_NEST();
    artLexicaliseTest(ARTV4Parser.ARTTB_STRING_BRACE_NEST);
    artBuiltin_STRING_BRACKET_NEST();
    artLexicaliseTest(ARTV4Parser.ARTTB_STRING_BRACKET_NEST);
    artBuiltin_STRING_DOLLAR();
    artLexicaliseTest(ARTV4Parser.ARTTB_STRING_DOLLAR);
    artBuiltin_STRING_DQ();
    artLexicaliseTest(ARTV4Parser.ARTTB_STRING_DQ);
    artBuiltin_STRING_PLAIN_SQ();
    artLexicaliseTest(ARTV4Parser.ARTTB_STRING_PLAIN_SQ);
    artBuiltin_STRING_SQ();
    artLexicaliseTest(ARTV4Parser.ARTTB_STRING_SQ);
  }

  @Override
  public void artLexicalisePreparseWhitespaceInstances() {
    artBuiltin_COMMENT_LINE_C();
    artBuiltin_COMMENT_NEST_ART();
    artBuiltin_SIMPLE_WHITESPACE();
  }

}
