package uk.ac.rhul.cs.csle.art.manager.parser;

import uk.ac.rhul.cs.csle.art.alg.gll.support.*;
import uk.ac.rhul.cs.csle.art.lex.*;
import uk.ac.rhul.cs.csle.art.manager.*;
import uk.ac.rhul.cs.csle.art.manager.grammar.*;
import uk.ac.rhul.cs.csle.art.manager.mode.*;
import uk.ac.rhul.cs.csle.art.util.*;
import uk.ac.rhul.cs.csle.art.util.text.*;
import uk.ac.rhul.cs.csle.art.value.*;
/*******************************************************************************
*
* ARTLexer.java
*
*******************************************************************************/
public class ARTLexer extends ARTLexerBase {
public void artLexicaliseBuiltinInstances() throws ARTException {
  artBuiltin_CHAR_BQ();
  artLexicaliseTest(ARTParser.ARTTB_CHAR_BQ);
  artBuiltin_ID();
  artLexicaliseTest(ARTParser.ARTTB_ID);
  artBuiltin_INTEGER();
  artLexicaliseTest(ARTParser.ARTTB_INTEGER);
  artBuiltin_REAL();
  artLexicaliseTest(ARTParser.ARTTB_REAL);
  artBuiltin_STRING_BRACE_NEST();
  artLexicaliseTest(ARTParser.ARTTB_STRING_BRACE_NEST);
  artBuiltin_STRING_DOLLAR();
  artLexicaliseTest(ARTParser.ARTTB_STRING_DOLLAR);
  artBuiltin_STRING_DQ();
  artLexicaliseTest(ARTParser.ARTTB_STRING_DQ);
  artBuiltin_STRING_PLAIN_SQ();
  artLexicaliseTest(ARTParser.ARTTB_STRING_PLAIN_SQ);
}

public void artLexicalisePreparseWhitespaceInstances() throws ARTException {
  artBuiltin_COMMENT_LINE_C();
  artBuiltin_COMMENT_NEST_ART();
  artBuiltin_WHITESPACE();
}

};
