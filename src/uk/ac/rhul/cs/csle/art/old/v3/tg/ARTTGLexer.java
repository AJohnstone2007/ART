package uk.ac.rhul.cs.csle.art.old.v3.tg;

import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;

/*******************************************************************************
 *
 * ARTTGLexer.java
 *
 *******************************************************************************/
public class ARTTGLexer extends ARTLexerV3 {
  @Override
  public void artLexicaliseBuiltinInstances() {
    artBuiltin_ID();
    artLexicaliseTest(ARTTGParser.ARTTB_ID);
    artBuiltin_STRING_BRACE();
    artLexicaliseTest(ARTTGParser.ARTTB_STRING_BRACE);
    artBuiltin_STRING_DOLLAR();
    artLexicaliseTest(ARTTGParser.ARTTB_STRING_DOLLAR);
  }

  @Override
  public void artLexicalisePreparseWhitespaceInstances() {
    artBuiltin_COMMENT_NEST_ART();
    artBuiltin_SIMPLE_WHITESPACE();
  }

}
