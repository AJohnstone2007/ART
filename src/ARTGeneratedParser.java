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
* ARTGeneratedParser.java
*
*******************************************************************************/
@SuppressWarnings("fallthrough") public class ARTGeneratedParser extends ARTGLLParserHashPool {
private static boolean[] ARTSet1;
private static boolean[] ARTSet2;
private static boolean[] ARTSet3;
private static boolean[] ARTSet4;
private static boolean[] ARTSet5;
private static boolean[] ARTSet6;
private static boolean[] ARTSet7;

/* Start of artLabel enumeration */
public static final int ARTX_EOS = 0;
public static final int ARTTB_SIMPLE_WHITESPACE = 1;
public static final int ARTTS_x = 2;
public static final int ARTTS_y = 3;
public static final int ARTTS_z = 4;
public static final int ARTX_EPSILON = 5;
public static final int ARTL_ART_S = 6;
public static final int ARTL_ART_Y = 7;
public static final int ARTL_ART_S_1 = 8;
public static final int ARTL_ART_S_2 = 9;
public static final int ARTL_ART_S_3 = 10;
public static final int ARTL_ART_S_4 = 11;
public static final int ARTL_ART_S_5 = 12;
public static final int ARTL_ART_S_6 = 13;
public static final int ARTL_ART_S_7 = 14;
public static final int ARTL_ART_S_8 = 15;
public static final int ARTL_ART_S_9 = 16;
public static final int ARTL_ART_S_10 = 17;
public static final int ARTL_ART_Y_11 = 18;
public static final int ARTL_ART_Y_12 = 19;
public static final int ARTL_ART_Y_13 = 20;
public static final int ARTL_ART_Y_14 = 21;
public static final int ARTX_DESPATCH = 22;
public static final int ARTX_DUMMY = 23;
public static final int ARTX_LABEL_EXTENT = 24;
/* End of artLabel enumeration */

/* Start of artName enumeration */
public static final int ARTNAME_NONE = 0;
public static final int ARTNAME_EXTENT = 1;
/* End of artName enumeration */
public void ARTPF_ART_S() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal S production descriptor loads*/
    case ARTL_ART_S: 
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_S_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal S: match production*/
    case ARTL_ART_S_2: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_x, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_S_4, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_S_6, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_Y; return; }
    case ARTL_ART_S_6: 
      /* Nonterminal template end */
      if (!ARTSet5[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_z, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_S_8, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet5[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_z, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_S_10, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_Y() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal Y production descriptor loads*/
    case ARTL_ART_Y: 
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_Y_12, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal Y: match production*/
    case ARTL_ART_Y_12: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_y, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_Y_14, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet5[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void artParseBody(int artStartLabel) {
  artLoadSetupTime();
  artSpecificationName = "testV3.art";
  artStartSymbolLabel = artStartLabel;
  artIsInLanguage = false;
  artTokenExtent = 8;
  artLexicaliseForV3GLL(artInputString, null);
  artLoadLexTime();
  artDummySPPFNode = artFindSPPFInitial(ARTL_DUMMY, 0, 0);
  artCurrentSPPFNode = artDummySPPFNode;
  artRootGSSNode = artFindGSS(ARTL_EOS, 0, 0, 0);
  artCurrentGSSNode = artRootGSSNode;
  artCurrentRestartLabel = artStartSymbolLabel;
  artCurrentInputPairIndex = 0;
  artCurrentInputPairReference = 0;
  while (true)
    switch (artlhsL[artCurrentRestartLabel]) {
      case ARTL_ART_S: 
        ARTPF_ART_S();
        break;
      case ARTL_ART_Y: 
        ARTPF_ART_Y();
        break;
      case ARTX_DESPATCH: 
        if (artNoDescriptors()) { 
          artCheckAcceptance();
          artLoadParseTime();
          artLoadEndMemory();
          return;
         }
        artUnloadDescriptor();
    }
}

public void ARTSet1initialise() {
  ARTSet1 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet1, 0, artSetExtent, false);
}

public void ARTSet3initialise() {
  ARTSet3 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
  ARTSet3[ARTX_EOS] = true;
}

public void ARTSet2initialise() {
  ARTSet2 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
  ARTSet2[ARTTS_x] = true;
}

public void ARTSet4initialise() {
  ARTSet4 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
  ARTSet4[ARTTS_y] = true;
  ARTSet4[ARTL_ART_Y] = true;
}

public void ARTSet7initialise() {
  ARTSet7 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
  ARTSet7[ARTTS_y] = true;
}

public void ARTSet5initialise() {
  ARTSet5 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
  ARTSet5[ARTTS_z] = true;
}

public void ARTSet6initialise() {
  ARTSet6 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
}

public void artSetInitialise() {
  ARTSet1initialise();
  ARTSet3initialise();
  ARTSet2initialise();
  ARTSet4initialise();
  ARTSet7initialise();
  ARTSet5initialise();
  ARTSet6initialise();
}

public void artTableInitialiser_ART_S() {
  artLabelInternalStrings[ARTL_ART_S] = "S";
  artLabelStrings[ARTL_ART_S] = "S";
  artKindOfs[ARTL_ART_S] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_S_2] = "S ::= . 'x'  Y 'z'  'z'  ";
  artLabelStrings[ARTL_ART_S_2] = "";
  artlhsL[ARTL_ART_S_2] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_2] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_S_3] = "S ::= 'x'  Y 'z'  'z'  ";
  artLabelStrings[ARTL_ART_S_3] = "";
  artlhsL[ARTL_ART_S_3] = ARTL_ART_S;
  artLabelInternalStrings[ARTL_ART_S_4] = "S ::= 'x'  . Y 'z'  'z'  ";
  artLabelStrings[ARTL_ART_S_4] = "";
  artlhsL[ARTL_ART_S_4] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_4] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_S_4] = true;
  artLabelInternalStrings[ARTL_ART_S_6] = "S ::= 'x'  Y . 'z'  'z'  ";
  artLabelStrings[ARTL_ART_S_6] = "";
  artlhsL[ARTL_ART_S_6] = ARTL_ART_S;
  artSlotInstanceOfs[ARTL_ART_S_6] = ARTL_ART_Y;
  artKindOfs[ARTL_ART_S_6] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_S_6] = true;
  artLabelInternalStrings[ARTL_ART_S_7] = "S ::= 'x'  Y 'z'  'z'  ";
  artLabelStrings[ARTL_ART_S_7] = "";
  artlhsL[ARTL_ART_S_7] = ARTL_ART_S;
  artPopD[ARTL_ART_S_7] = true;
  artLabelInternalStrings[ARTL_ART_S_8] = "S ::= 'x'  Y 'z'  . 'z'  ";
  artLabelStrings[ARTL_ART_S_8] = "";
  artlhsL[ARTL_ART_S_8] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_8] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_S_8] = true;
  artLabelInternalStrings[ARTL_ART_S_9] = "S ::= 'x'  Y 'z'  'z'  ";
  artLabelStrings[ARTL_ART_S_9] = "";
  artlhsL[ARTL_ART_S_9] = ARTL_ART_S;
  artPopD[ARTL_ART_S_9] = true;
  artLabelInternalStrings[ARTL_ART_S_10] = "S ::= 'x'  Y 'z'  'z'  .";
  artLabelStrings[ARTL_ART_S_10] = "";
  artlhsL[ARTL_ART_S_10] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_10] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_S_10] = true;
  arteoR_pL[ARTL_ART_S_10] = true;
  artPopD[ARTL_ART_S_10] = true;
}

public void artTableInitialiser_ART_Y() {
  artLabelInternalStrings[ARTL_ART_Y] = "Y";
  artLabelStrings[ARTL_ART_Y] = "Y";
  artKindOfs[ARTL_ART_Y] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_Y_12] = "Y ::= . 'y'  ";
  artLabelStrings[ARTL_ART_Y_12] = "";
  artlhsL[ARTL_ART_Y_12] = ARTL_ART_Y;
  artKindOfs[ARTL_ART_Y_12] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_Y_12] = true;
  artLabelInternalStrings[ARTL_ART_Y_13] = "Y ::= 'y'  ";
  artLabelStrings[ARTL_ART_Y_13] = "";
  artlhsL[ARTL_ART_Y_13] = ARTL_ART_Y;
  artPopD[ARTL_ART_Y_13] = true;
  artLabelInternalStrings[ARTL_ART_Y_14] = "Y ::= 'y'  .";
  artLabelStrings[ARTL_ART_Y_14] = "";
  artlhsL[ARTL_ART_Y_14] = ARTL_ART_Y;
  artKindOfs[ARTL_ART_Y_14] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_Y_14] = true;
  arteoR_pL[ARTL_ART_Y_14] = true;
  artPopD[ARTL_ART_Y_14] = true;
}

public void artTableInitialise() {
  artLabelInternalStrings = new String[ARTX_LABEL_EXTENT + 1];
  artLabelStrings = new String[ARTX_LABEL_EXTENT + 1];
  artLabelInternalStrings[ARTL_EOS] = "ART$";
  artLabelStrings[ARTL_EOS] = " EOS $";
  artLabelInternalStrings[ARTX_DESPATCH] = "ARTX_DESPATCH";
  artLabelStrings[ARTX_DESPATCH] = " DESPATCH";
  artLabelInternalStrings[ARTL_DUMMY] = "ARTL_DUMMY";
  artLabelStrings[ARTL_DUMMY] = " DUMMY";
  artLabelInternalStrings[ARTX_LABEL_EXTENT] = "!!ILLEGAL!!";
  artLabelStrings[ARTX_LABEL_EXTENT] = " ILLEGAL";
  artLabelStrings[ARTL_EPSILON] = "#";
  artLabelInternalStrings[ARTL_EPSILON] = "#";

  artTerminalRequiresWhiteSpace = new boolean[ARTL_EPSILON];
  artInitialiseBooleanArray(artTerminalRequiresWhiteSpace, 0, ARTL_EPSILON, false);

  artTerminalCaseInsensitive = new boolean[ARTL_EPSILON];
  artInitialiseBooleanArray(artTerminalCaseInsensitive, 0, ARTL_EPSILON, false);

  artlhsL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artlhsL, 0, ARTX_LABEL_EXTENT);
  artlhsL[ARTX_DESPATCH] = ARTX_DESPATCH;

  artKindOfs = new int[ARTX_LABEL_EXTENT + 1];
  artKindOfs[ARTL_EOS] = ARTK_EOS;
  artKindOfs[ARTL_EPSILON] = ARTK_EPSILON;

  artHigher = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artLonger = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artShorter = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artPreSlots = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artPreSlots, 0, ARTX_LABEL_EXTENT);

  artPostSlots = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artPostSlots, 0, ARTX_LABEL_EXTENT);

  artInstanceOfs = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artInstanceOfs, 0, ARTX_LABEL_EXTENT);

  artSlotInstanceOfs = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artSlotInstanceOfs, 0, ARTX_LABEL_EXTENT);

  artUserNameOfs = new int[ARTX_LABEL_EXTENT + 1];

  artGathers = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artGathers, 0, ARTX_LABEL_EXTENT);

  artFolds = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artFolds, 0, ARTX_LABEL_EXTENT, 0);

  artpL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artpL, 0, ARTX_LABEL_EXTENT);

  artaL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artaL, 0, ARTX_LABEL_EXTENT);

  artcolonL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artcolonL, 0, ARTX_LABEL_EXTENT);

  arteoOPL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoOPL, 0, ARTX_LABEL_EXTENT, false);

  artfiRL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artfiRL, 0, ARTX_LABEL_EXTENT, false);

  artfiPCL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artfiPCL, 0, ARTX_LABEL_EXTENT, false);

  arteoRL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoRL, 0, ARTX_LABEL_EXTENT, false);

  arteoR_pL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoR_pL, 0, ARTX_LABEL_EXTENT, false);

  artPopD = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artPopD, 0, ARTX_LABEL_EXTENT, false);

  artLabelStrings[ARTTB_SIMPLE_WHITESPACE] = "SIMPLE_WHITESPACE";
  artLabelInternalStrings[ARTTB_SIMPLE_WHITESPACE] = "&SIMPLE_WHITESPACE";
  artKindOfs[ARTTB_SIMPLE_WHITESPACE] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_SIMPLE_WHITESPACE] = true;
  artLabelStrings[ARTTS_x] = "x";
  artLabelInternalStrings[ARTTS_x] = "'x'";
  artKindOfs[ARTTS_x] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_x] = true;
  artLabelStrings[ARTTS_y] = "y";
  artLabelInternalStrings[ARTTS_y] = "'y'";
  artKindOfs[ARTTS_y] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_y] = true;
  artLabelStrings[ARTTS_z] = "z";
  artLabelInternalStrings[ARTTS_z] = "'z'";
  artKindOfs[ARTTS_z] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_z] = true;
  artTableInitialiser_ART_S();
  artTableInitialiser_ART_Y();
}

public ARTGeneratedParser(ARTLexerV3 artLexer) {
  this(null, artLexer);
}

public ARTGeneratedParser(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
  super(artGrammar, artLexer);
  artParserKind = "GLL Gen";
  artFirstTerminalLabel = ARTTS_x;
  artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
  artSetExtent = 8;
  ARTL_EOS = ARTX_EOS;
  ARTL_EPSILON = ARTX_EPSILON;
  ARTL_DUMMY = ARTX_DUMMY;
  artGrammarKind = ARTModeGrammarKind.BNF;
  artDefaultStartSymbolLabel = ARTL_ART_S;
  artBuildDirectives = "ARTDirectives [inputs=[], inputFilenames=[], directives={suppressPopGuard=false, tweLexicalisations=false, algorithmMode=gllGeneratorPool, tweLongest=false, tweSegments=false, sppfShortest=false, termWrite=false, tweCounts=false, clusteredGSS=false, twePrint=false, rewriteDisable=false, tweAmbiguityClasses=false, sppfAmbiguityAnalysis=false, rewriteConfiguration=false, outputDirectory=., inputCounts=false, twePriority=false, treeShow=false, tweRecursive=false, rewritePostorder=false, rewriteContractum=true, parseCounts=false, predictivePops=false, suppressProductionGuard=false, sppfDead=false, twePrintFull=false, input=0, tweExtents=false, suppressSemantics=false, despatchMode=fragment, treePrintLevel=3, sppfShowFull=false, treePrint=false, sppfChooseCounts=false, log=1, tweDump=false, sppfCycleDetect=false, sppfCountSentences=false, parserName=ARTGeneratedParser, rewriteResume=true, inputPrint=false, lexerName=ARTGeneratedLexer, trace=false, tweTokenWrite=false, tweDead=false, tweShortest=false, rewritePure=true, tweSelectOne=false, smlCycleBreak=false, termPrint=false, suppressTestRepeat=false, rewritePreorder=false, sppfAmbiguityAnalysisFull=false, tweFromSPPF=false, actionSuppress=false, tweLexicalisationsQuick=false, sppfPriority=false, sppfShow=false, rewriteOneStep=false, namespace=, sppfSelectOne=false, FIFODescriptors=false, sppfOrderedLongest=false, verbosity=0, sppfLongest=false, gssShow=false}]";
  artFIFODescriptors = false;
  artSetInitialise();
  artTableInitialise();
}

public void ARTRD_S(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*S ::= 'x'  Y . 'z'  'z'  */
    case ARTL_ART_S_6: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_Y(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*S ::= 'x'  Y 'z'  . 'z'  */
    case ARTL_ART_S_8: 
      ARTRD_S(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*S ::= 'x'  Y 'z'  'z'  .*/
    case ARTL_ART_S_10: 
            ARTRD_S(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_Y(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*Y ::= 'y'  .*/
    case ARTL_ART_Y_14: 
            ARTRD_Y(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void artEvaluate(ARTGLLRDTHandle artElement, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable)  {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ART_S: ARTRD_S(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_Y: ARTRD_Y(artElement.element, artParent, artWriteable); break;
  }
}

};
