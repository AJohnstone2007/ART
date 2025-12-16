package uk.ac.rhul.cs.csle.art.old.v3.manager.parser;

import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;

/*******************************************************************************
 *
 * ARTGeneratedLexer.java
 *
 *******************************************************************************/
@SuppressWarnings("fallthrough")
public class ARTV3Lexer extends ARTLexerV3 {
  @Override
  public void artLexicaliseBuiltinInstances() {
    artBuiltin_CHAR_BQ();
    artLexicaliseTest(ARTV3Parser.ARTTB_CHAR_BQ);
    artBuiltin_ID();
    artLexicaliseTest(ARTV3Parser.ARTTB_ID);
    artBuiltin_INTEGER();
    artLexicaliseTest(ARTV3Parser.ARTTB_INTEGER);
    artBuiltin_REAL();
    artLexicaliseTest(ARTV3Parser.ARTTB_REAL);
    artBuiltin_STRING_BRACE_NEST();
    artLexicaliseTest(ARTV3Parser.ARTTB_STRING_BRACE_NEST);
    artBuiltin_STRING_DOLLAR();
    artLexicaliseTest(ARTV3Parser.ARTTB_STRING_DOLLAR);
    artBuiltin_STRING_DQ();
    artLexicaliseTest(ARTV3Parser.ARTTB_STRING_DQ);
    artBuiltin_STRING_PLAIN_SQ();
    artLexicaliseTest(ARTV3Parser.ARTTB_STRING_PLAIN_SQ);
  }

  @Override
  public void artLexicalisePreparseWhitespaceInstances() {
    artBuiltin_COMMENT_LINE_C();
    artBuiltin_COMMENT_NEST_ART();
    artBuiltin_SIMPLE_WHITESPACE();
  }

};
