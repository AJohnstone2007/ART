import uk.ac.rhul.cs.csle.art.old.v4.core.ARTUncheckedException;
import java.io.FileNotFoundException;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.*;
import uk.ac.rhul.cs.csle.art.old.v3.lex.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.*;
import uk.ac.rhul.cs.csle.art.old.v4.util.text.*;
import uk.ac.rhul.cs.csle.art.term.*;
import uk.ac.rhul.cs.csle.art.old.v4.util.bitset.ARTBitSet;
/*******************************************************************************
*
* ARTGeneratedLexer.java
*
*******************************************************************************/
@SuppressWarnings("fallthrough") public class ARTGeneratedLexer extends ARTLexerV3 {
public void artLexicaliseBuiltinInstances() {
  artBuiltin_SIMPLE_WHITESPACE();
  artLexicaliseTest(ARTGeneratedParser.ARTTB_SIMPLE_WHITESPACE);
}

public void artLexicalisePreparseWhitespaceInstances() {
  artBuiltin_SIMPLE_WHITESPACE();
}

};
