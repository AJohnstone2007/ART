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
private static boolean[] ARTSet8;

/* Start of artLabel enumeration */
public static final int ARTX_EOS = 0;
public static final int ARTTB_SIMPLE_WHITESPACE = 1;
public static final int ARTTS_a = 2;
public static final int ARTTS_r = 3;
public static final int ARTTS_t = 4;
public static final int ARTX_EPSILON = 5;
public static final int ARTL_ART_AR = 6;
public static final int ARTL_ART_RT = 7;
public static final int ARTL_ART_S = 8;
public static final int ARTL_ART_AR_19 = 9;
public static final int ARTL_ART_AR_20 = 10;
public static final int ARTL_ART_AR_21 = 11;
public static final int ARTL_ART_AR_22 = 12;
public static final int ARTL_ART_AR_23 = 13;
public static final int ARTL_ART_AR_24 = 14;
public static final int ARTL_ART_RT_13 = 15;
public static final int ARTL_ART_RT_14 = 16;
public static final int ARTL_ART_RT_15 = 17;
public static final int ARTL_ART_RT_16 = 18;
public static final int ARTL_ART_RT_17 = 19;
public static final int ARTL_ART_RT_18 = 20;
public static final int ARTL_ART_S_1 = 21;
public static final int ARTL_ART_S_2 = 22;
public static final int ARTL_ART_S_3 = 23;
public static final int ARTL_ART_S_4 = 24;
public static final int ARTL_ART_S_5 = 25;
public static final int ARTL_ART_S_6 = 26;
public static final int ARTL_ART_S_7 = 27;
public static final int ARTL_ART_S_8 = 28;
public static final int ARTL_ART_S_9 = 29;
public static final int ARTL_ART_S_10 = 30;
public static final int ARTL_ART_S_11 = 31;
public static final int ARTL_ART_S_12 = 32;
public static final int ARTX_DESPATCH = 33;
public static final int ARTX_DUMMY = 34;
public static final int ARTX_LABEL_EXTENT = 35;
/* End of artLabel enumeration */

/* Start of artName enumeration */
public static final int ARTNAME_NONE = 0;
public static final int ARTNAME_EXTENT = 1;
/* End of artName enumeration */
public void ARTPF_ART_AR() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal AR production descriptor loads*/
    case ARTL_ART_AR: 
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_AR_20, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal AR: match production*/
    case ARTL_ART_AR_20: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_a, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_AR_22, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_r, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_AR_24, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_RT() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal RT production descriptor loads*/
    case ARTL_ART_RT: 
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_RT_14, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal RT: match production*/
    case ARTL_ART_RT_14: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_r, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_RT_16, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_t, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_RT_18, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_S() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal S production descriptor loads*/
    case ARTL_ART_S: 
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_S_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_S_8, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal S: match production*/
    case ARTL_ART_S_2: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_a, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_S_4, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_S_6, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_RT; return; }
    case ARTL_ART_S_6: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal S: match production*/
    case ARTL_ART_S_8: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_S_10, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_AR; return; }
    case ARTL_ART_S_10: 
      /* Nonterminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_t, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_S_12, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void artParseBody(int artStartLabel) {
  artLoadSetupTime();
  artSpecificationName = "testV3.art";
  artStartSymbolLabel = artStartLabel;
  artIsInLanguage = false;
  artTokenExtent = 9;
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
      case ARTL_ART_AR: 
        ARTPF_ART_AR();
        break;
      case ARTL_ART_RT: 
        ARTPF_ART_RT();
        break;
      case ARTL_ART_S: 
        ARTPF_ART_S();
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
  ARTSet3[ARTTS_t] = true;
}

public void ARTSet6initialise() {
  ARTSet6 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
  ARTSet6[ARTX_EOS] = true;
}

public void ARTSet7initialise() {
  ARTSet7 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
  ARTSet7[ARTTS_a] = true;
  ARTSet7[ARTL_ART_AR] = true;
}

public void ARTSet8initialise() {
  ARTSet8 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
  ARTSet8[ARTTS_r] = true;
  ARTSet8[ARTL_ART_RT] = true;
}

public void ARTSet5initialise() {
  ARTSet5 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
}

public void ARTSet2initialise() {
  ARTSet2 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
  ARTSet2[ARTTS_a] = true;
}

public void ARTSet4initialise() {
  ARTSet4 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
  ARTSet4[ARTTS_r] = true;
}

public void artSetInitialise() {
  ARTSet1initialise();
  ARTSet3initialise();
  ARTSet6initialise();
  ARTSet7initialise();
  ARTSet8initialise();
  ARTSet5initialise();
  ARTSet2initialise();
  ARTSet4initialise();
}

public void artTableInitialiser_ART_AR() {
  artLabelInternalStrings[ARTL_ART_AR] = "AR";
  artLabelStrings[ARTL_ART_AR] = "AR";
  artKindOfs[ARTL_ART_AR] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_AR_20] = "AR ::= . 'a'  'r'  ";
  artLabelStrings[ARTL_ART_AR_20] = "";
  artlhsL[ARTL_ART_AR_20] = ARTL_ART_AR;
  artKindOfs[ARTL_ART_AR_20] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_AR_20] = true;
  artLabelInternalStrings[ARTL_ART_AR_21] = "AR ::= 'a'  'r'  ";
  artLabelStrings[ARTL_ART_AR_21] = "";
  artlhsL[ARTL_ART_AR_21] = ARTL_ART_AR;
  artPopD[ARTL_ART_AR_21] = true;
  artLabelInternalStrings[ARTL_ART_AR_22] = "AR ::= 'a'  . 'r'  ";
  artLabelStrings[ARTL_ART_AR_22] = "";
  artlhsL[ARTL_ART_AR_22] = ARTL_ART_AR;
  artKindOfs[ARTL_ART_AR_22] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_AR_22] = true;
  artPopD[ARTL_ART_AR_22] = true;
  artLabelInternalStrings[ARTL_ART_AR_23] = "AR ::= 'a'  'r'  ";
  artLabelStrings[ARTL_ART_AR_23] = "";
  artlhsL[ARTL_ART_AR_23] = ARTL_ART_AR;
  artPopD[ARTL_ART_AR_23] = true;
  artLabelInternalStrings[ARTL_ART_AR_24] = "AR ::= 'a'  'r'  .";
  artLabelStrings[ARTL_ART_AR_24] = "";
  artlhsL[ARTL_ART_AR_24] = ARTL_ART_AR;
  artKindOfs[ARTL_ART_AR_24] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_AR_24] = true;
  arteoR_pL[ARTL_ART_AR_24] = true;
  artPopD[ARTL_ART_AR_24] = true;
}

public void artTableInitialiser_ART_RT() {
  artLabelInternalStrings[ARTL_ART_RT] = "RT";
  artLabelStrings[ARTL_ART_RT] = "RT";
  artKindOfs[ARTL_ART_RT] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_RT_14] = "RT ::= . 'r'  't'  ";
  artLabelStrings[ARTL_ART_RT_14] = "";
  artlhsL[ARTL_ART_RT_14] = ARTL_ART_RT;
  artKindOfs[ARTL_ART_RT_14] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_RT_14] = true;
  artLabelInternalStrings[ARTL_ART_RT_15] = "RT ::= 'r'  't'  ";
  artLabelStrings[ARTL_ART_RT_15] = "";
  artlhsL[ARTL_ART_RT_15] = ARTL_ART_RT;
  artPopD[ARTL_ART_RT_15] = true;
  artLabelInternalStrings[ARTL_ART_RT_16] = "RT ::= 'r'  . 't'  ";
  artLabelStrings[ARTL_ART_RT_16] = "";
  artlhsL[ARTL_ART_RT_16] = ARTL_ART_RT;
  artKindOfs[ARTL_ART_RT_16] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_RT_16] = true;
  artPopD[ARTL_ART_RT_16] = true;
  artLabelInternalStrings[ARTL_ART_RT_17] = "RT ::= 'r'  't'  ";
  artLabelStrings[ARTL_ART_RT_17] = "";
  artlhsL[ARTL_ART_RT_17] = ARTL_ART_RT;
  artPopD[ARTL_ART_RT_17] = true;
  artLabelInternalStrings[ARTL_ART_RT_18] = "RT ::= 'r'  't'  .";
  artLabelStrings[ARTL_ART_RT_18] = "";
  artlhsL[ARTL_ART_RT_18] = ARTL_ART_RT;
  artKindOfs[ARTL_ART_RT_18] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_RT_18] = true;
  arteoR_pL[ARTL_ART_RT_18] = true;
  artPopD[ARTL_ART_RT_18] = true;
}

public void artTableInitialiser_ART_S() {
  artLabelInternalStrings[ARTL_ART_S] = "S";
  artLabelStrings[ARTL_ART_S] = "S";
  artKindOfs[ARTL_ART_S] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_S_2] = "S ::= . 'a'  RT ";
  artLabelStrings[ARTL_ART_S_2] = "";
  artlhsL[ARTL_ART_S_2] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_2] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_S_3] = "S ::= 'a'  RT ";
  artLabelStrings[ARTL_ART_S_3] = "";
  artlhsL[ARTL_ART_S_3] = ARTL_ART_S;
  artLabelInternalStrings[ARTL_ART_S_4] = "S ::= 'a'  . RT ";
  artLabelStrings[ARTL_ART_S_4] = "";
  artlhsL[ARTL_ART_S_4] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_4] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_S_4] = true;
  artLabelInternalStrings[ARTL_ART_S_6] = "S ::= 'a'  RT .";
  artLabelStrings[ARTL_ART_S_6] = "";
  artlhsL[ARTL_ART_S_6] = ARTL_ART_S;
  artSlotInstanceOfs[ARTL_ART_S_6] = ARTL_ART_RT;
  artKindOfs[ARTL_ART_S_6] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_S_6] = true;
  arteoR_pL[ARTL_ART_S_6] = true;
  artPopD[ARTL_ART_S_6] = true;
  artLabelInternalStrings[ARTL_ART_S_8] = "S ::= . AR 't'  ";
  artLabelStrings[ARTL_ART_S_8] = "";
  artlhsL[ARTL_ART_S_8] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_8] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_S_10] = "S ::= AR . 't'  ";
  artLabelStrings[ARTL_ART_S_10] = "";
  artlhsL[ARTL_ART_S_10] = ARTL_ART_S;
  artSlotInstanceOfs[ARTL_ART_S_10] = ARTL_ART_AR;
  artKindOfs[ARTL_ART_S_10] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_S_10] = true;
  artPopD[ARTL_ART_S_10] = true;
  artLabelInternalStrings[ARTL_ART_S_11] = "S ::= AR 't'  ";
  artLabelStrings[ARTL_ART_S_11] = "";
  artlhsL[ARTL_ART_S_11] = ARTL_ART_S;
  artPopD[ARTL_ART_S_11] = true;
  artLabelInternalStrings[ARTL_ART_S_12] = "S ::= AR 't'  .";
  artLabelStrings[ARTL_ART_S_12] = "";
  artlhsL[ARTL_ART_S_12] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_12] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_S_12] = true;
  arteoR_pL[ARTL_ART_S_12] = true;
  artPopD[ARTL_ART_S_12] = true;
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
  artLabelStrings[ARTTS_a] = "a";
  artLabelInternalStrings[ARTTS_a] = "'a'";
  artKindOfs[ARTTS_a] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_a] = true;
  artLabelStrings[ARTTS_r] = "r";
  artLabelInternalStrings[ARTTS_r] = "'r'";
  artKindOfs[ARTTS_r] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_r] = true;
  artLabelStrings[ARTTS_t] = "t";
  artLabelInternalStrings[ARTTS_t] = "'t'";
  artKindOfs[ARTTS_t] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_t] = true;
  artTableInitialiser_ART_AR();
  artTableInitialiser_ART_RT();
  artTableInitialiser_ART_S();
}

public ARTGeneratedParser(ARTLexerV3 artLexer) {
  this(null, artLexer);
}

public ARTGeneratedParser(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
  super(artGrammar, artLexer);
  artParserKind = "GLL Gen";
  artFirstTerminalLabel = ARTTS_a;
  artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
  artSetExtent = 9;
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

public void ARTRD_AR(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*AR ::= 'a'  'r'  .*/
    case ARTL_ART_AR_24: 
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_RT(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*RT ::= 'r'  't'  .*/
    case ARTL_ART_RT_18: 
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_S(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*S ::= 'a'  RT .*/
    case ARTL_ART_S_6: 
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_RT(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*S ::= AR 't'  .*/
    case ARTL_ART_S_12: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_AR(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void artEvaluate(ARTGLLRDTHandle artElement, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable)  {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ART_AR: ARTRD_AR(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_RT: ARTRD_RT(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_S: ARTRD_S(artElement.element, artParent, artWriteable); break;
  }
}

};
