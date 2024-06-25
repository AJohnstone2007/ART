package uk.ac.rhul.cs.csle.art.tg;

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
* ARTTGLexer.java
*
*******************************************************************************/
public class ARTTGLexer extends ARTLexerBase {
public void artLexicaliseBuiltinInstances() throws ARTException {
  artBuiltin_ID();
  artLexicaliseTest(ARTTGParser.ARTTB_ID);
  artBuiltin_STRING_BRACE();
  artLexicaliseTest(ARTTGParser.ARTTB_STRING_BRACE);
  artBuiltin_STRING_DOLLAR();
  artLexicaliseTest(ARTTGParser.ARTTB_STRING_DOLLAR);
}

public void artLexicalisePreparseWhitespaceInstances() throws ARTException {
  artBuiltin_COMMENT_NEST_ART();
  artBuiltin_WHITESPACE();
}

};
