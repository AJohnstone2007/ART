package uk.ac.rhul.cs.csle.art.old.v4.cfg.referenceFamily.script;

import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;

/*******************************************************************************
 *
 * ReferenceGrammarLexer.java
 *
 *******************************************************************************/
public class ReferenceGrammarLexer extends ARTLexerV3 {
  @Override
  public void artLexicaliseBuiltinInstances() {
    artBuiltin_ID();
    artLexicaliseTest(ReferenceGrammarParser.ARTTB_ID);
    artBuiltin_INTEGER();
    artLexicaliseTest(ReferenceGrammarParser.ARTTB_INTEGER);
    artBuiltin_REAL();
    artLexicaliseTest(ReferenceGrammarParser.ARTTB_REAL);
    artBuiltin_STRING_DQ();
    artLexicaliseTest(ReferenceGrammarParser.ARTTB_STRING_DQ);
    artBuiltin_STRING_PLAIN_SQ();
    artLexicaliseTest(ReferenceGrammarParser.ARTTB_STRING_PLAIN_SQ);
  }

  @Override
  public void artLexicalisePreparseWhitespaceInstances() {
    artBuiltin_COMMENT_LINE_C();
    artBuiltin_COMMENT_NEST_ART();
    artBuiltin_SIMPLE_WHITESPACE();
  }

}
