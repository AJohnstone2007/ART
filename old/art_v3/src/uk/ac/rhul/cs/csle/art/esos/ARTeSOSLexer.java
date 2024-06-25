package uk.ac.rhul.cs.csle.art.esos;

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
* ARTeSOSLexer.java
*
*******************************************************************************/
public class ARTeSOSLexer extends ARTLexerBase {
public void artLexicaliseBuiltinInstances() throws ARTException {
  artBuiltin_ID_SOS();
  artLexicaliseTest(ARTeSOSParser.ARTTB_ID_SOS);
  artBuiltin_INTEGER();
  artLexicaliseTest(ARTeSOSParser.ARTTB_INTEGER);
  artBuiltin_REAL();
  artLexicaliseTest(ARTeSOSParser.ARTTB_REAL);
  artBuiltin_STRING_DQ();
  artLexicaliseTest(ARTeSOSParser.ARTTB_STRING_DQ);
  artBuiltin_STRING_SQ();
  artLexicaliseTest(ARTeSOSParser.ARTTB_STRING_SQ);
}

public void artLexicalisePreparseWhitespaceInstances() throws ARTException {
  artBuiltin_COMMENT_LINE_C();
  artBuiltin_COMMENT_NEST_ART();
  artBuiltin_WHITESPACE();
}

};
