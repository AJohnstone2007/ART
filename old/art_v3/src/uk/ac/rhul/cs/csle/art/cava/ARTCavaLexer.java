package uk.ac.rhul.cs.csle.art.cava;

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
* ARTCavaLexer.java
*
*******************************************************************************/
public class ARTCavaLexer extends ARTLexerBase {
public void artLexicaliseBuiltinInstances() throws ARTException {
  artBuiltin_ID();
  artLexicaliseTest(ARTCavaParser.ARTTB_ID);
  artBuiltin_INTEGER();
  artLexicaliseTest(ARTCavaParser.ARTTB_INTEGER);
  artBuiltin_REAL();
  artLexicaliseTest(ARTCavaParser.ARTTB_REAL);
  artBuiltin_STRING_DQ();
  artLexicaliseTest(ARTCavaParser.ARTTB_STRING_DQ);
  artBuiltin_STRING_SQ();
  artLexicaliseTest(ARTCavaParser.ARTTB_STRING_SQ);
}

public void artLexicalisePreparseWhitespaceInstances() throws ARTException {
  artBuiltin_COMMENT_BLOCK_C();
  artBuiltin_COMMENT_LINE_C();
  artBuiltin_WHITESPACE();
}

};
