import uk.ac.rhul.cs.csle.art.alg.gll.support.*;
import uk.ac.rhul.cs.csle.art.lex.*;
import uk.ac.rhul.cs.csle.art.manager.*;
import uk.ac.rhul.cs.csle.art.manager.grammar.*;
import uk.ac.rhul.cs.csle.art.manager.mode.*;
import uk.ac.rhul.cs.csle.art.util.*;
/*******************************************************************************
*
* ARTGeneratedLexer.java
*
*******************************************************************************/
public class ARTGeneratedLexer extends ARTLexerBase {
public void artLexicaliseBuiltinInstances() throws ARTException {
  artBuiltin_ID();
  artLexicaliseTest(ARTGeneratedParser.ARTTB_ID);
}

public void artLexicalisePreparseWhitespaceInstances() throws ARTException {
  artBuiltin_WHITESPACE();
}

};
