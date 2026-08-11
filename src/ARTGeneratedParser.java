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
private static boolean[] ARTSet9;

/* Start of artLabel enumeration */
public static final int ARTX_EOS = 0;
public static final int ARTTC__HT = 1;
public static final int ARTTC__LF = 2;
public static final int ARTTC__CR = 3;
public static final int ARTTC__SPACE = 4;
public static final int ARTTC_p = 5;
public static final int ARTTC_q = 6;
public static final int ARTTC_r = 7;
public static final int ARTTC_s = 8;
public static final int ARTX_EPSILON = 9;
public static final int ARTL_ART_ARTLexerStart = 10;
public static final int ARTL_ART_S = 11;
public static final int ARTL_ART_id = 12;
public static final int ARTL_ART_id1 = 13;
public static final int ARTL_ART_id2 = 14;
public static final int ARTL_ART_letter = 15;
public static final int ARTL_ART_S_1 = 16;
public static final int ARTL_ART_S_2 = 17;
public static final int ARTL_ART_S_3 = 18;
public static final int ARTL_ART_S_4 = 19;
public static final int ARTL_ART_S_5 = 20;
public static final int ARTL_ART_S_6 = 21;
public static final int ARTL_ART_S_7 = 22;
public static final int ARTL_ART_S_8 = 23;
public static final int ARTL_ART_S_9 = 24;
public static final int ARTL_ART_S_10 = 25;
public static final int ARTL_ART_id_11 = 26;
public static final int ARTL_ART_id_12 = 27;
public static final int ARTL_ART_id_13 = 28;
public static final int ARTL_ART_id_14 = 29;
public static final int ARTL_ART_id_15 = 30;
public static final int ARTL_ART_id_16 = 31;
public static final int ARTL_ART_id_17 = 32;
public static final int ARTL_ART_id_18 = 33;
public static final int ARTX_DESPATCH = 34;
public static final int ARTX_DUMMY = 35;
public static final int ARTX_LABEL_EXTENT = 36;
/* End of artLabel enumeration */

/* Start of artName enumeration */
public static final int ARTNAME_NONE = 0;
public static final int ARTNAME_EXTENT = 1;
/* End of artName enumeration */
public void ARTPF_ART_ARTLexerStart() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal ARTLexerStart production descriptor loads*/
    case ARTL_ART_ARTLexerStart: 
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
  }
}

public void ARTPF_ART_S() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal S production descriptor loads*/
    case ARTL_ART_S: 
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_S_2, artCurrentGSSNode, artCurrentInputPairReference, artDummySPPFNode);
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_S_6, artCurrentGSSNode, artCurrentInputPairReference, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal S: match production*/
    case ARTL_ART_S_2: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSSMGLL(ARTL_ART_S_4, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_id; return; }
    case ARTL_ART_S_4: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopMGLL(artCurrentGSSNode, artCurrentInputPairReference, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal S: match production*/
    case ARTL_ART_S_6: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSSMGLL(ARTL_ART_S_8, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_id; return; }
    case ARTL_ART_S_8: 
      /* Nonterminal template end */
      if (!ARTSet5[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSSMGLL(ARTL_ART_S_10, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_S; return; }
    case ARTL_ART_S_10: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopMGLL(artCurrentGSSNode, artCurrentInputPairReference, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_id() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal id production descriptor loads*/
    case ARTL_ART_id: 
      if (ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_id_12, artCurrentGSSNode, artCurrentInputPairReference, artDummySPPFNode);
      if (ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_id_16, artCurrentGSSNode, artCurrentInputPairReference, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal id: match production*/
    case ARTL_ART_id_12: 
      /* Cat/unary template start */
      /* Terminal template start */
      for (int artI = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference+1]][artInputPairBuffer[artCurrentInputPairReference]]; artInputSuccessorBuffer[artI] != -1; artI++) 
        if (ARTSet7[artInputPairBuffer[artInputSuccessorBuffer[artI]]]) { 
          artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTL_ART_id1, artCurrentInputPairIndex, artInputPairBuffer[artInputSuccessorBuffer[artI]+1]);
          artTemporarySPPFNode = artFindSPPF(ARTL_ART_id_14, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
          artFindDescriptor(ARTL_ART_id_14, artCurrentGSSNode, artInputSuccessorBuffer[artI], artTemporarySPPFNode);
         }

        { artCurrentRestartLabel = ARTX_DESPATCH; return; }

    case ARTL_ART_id_14: 
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopMGLL(artCurrentGSSNode, artCurrentInputPairReference, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal id: match production*/
    case ARTL_ART_id_16: 
      /* Cat/unary template start */
      /* Terminal template start */
      for (int artI = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference+1]][artInputPairBuffer[artCurrentInputPairReference]]; artInputSuccessorBuffer[artI] != -1; artI++) 
        if (ARTSet7[artInputPairBuffer[artInputSuccessorBuffer[artI]]]) { 
          artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTL_ART_id2, artCurrentInputPairIndex, artInputPairBuffer[artInputSuccessorBuffer[artI]+1]);
          artTemporarySPPFNode = artFindSPPF(ARTL_ART_id_18, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
          artFindDescriptor(ARTL_ART_id_18, artCurrentGSSNode, artInputSuccessorBuffer[artI], artTemporarySPPFNode);
         }

        { artCurrentRestartLabel = ARTX_DESPATCH; return; }

    case ARTL_ART_id_18: 
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopMGLL(artCurrentGSSNode, artCurrentInputPairReference, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_id1() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal id1 production descriptor loads*/
    case ARTL_ART_id1: 
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
  }
}

public void ARTPF_ART_id2() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal id2 production descriptor loads*/
    case ARTL_ART_id2: 
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
  }
}

public void ARTPF_ART_letter() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal letter production descriptor loads*/
    case ARTL_ART_letter: 
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
  }
}

public void artParseBody(int artStartLabel) {
  artLoadSetupTime();
  artSpecificationName = "artChooseParseTWE.art";
  artStartSymbolLabel = artStartLabel;
  artIsInLanguage = false;
  artTokenExtent = 16;
  artLexBuildTriplesFromFile("ARTTWE.twe");
  artLexBuildSuccessorSets();
  artLoadLexTime();
  artDummySPPFNode = artFindSPPFInitial(ARTL_DUMMY, 0, 0);
  artCurrentSPPFNode = artDummySPPFNode;
  artRootGSSNode = artFindGSS(ARTL_EOS, 0, 0, 0);
  artCurrentGSSNode = artRootGSSNode;
  artLoadDescriptorInitialMGLL();
  artCurrentRestartLabel = ARTX_DESPATCH;
  artCurrentInputPairIndex = 0;
  artCurrentInputPairReference = 0;
  while (true)
    switch (artlhsL[artCurrentRestartLabel]) {
      case ARTL_ART_ARTLexerStart: 
        ARTPF_ART_ARTLexerStart();
        break;
      case ARTL_ART_S: 
        ARTPF_ART_S();
        break;
      case ARTL_ART_id: 
        ARTPF_ART_id();
        break;
   // Skipping paraterminal id1
   // Skipping paraterminal id2
      case ARTL_ART_letter: 
        ARTPF_ART_letter();
        break;
      case ARTX_DESPATCH: 
        if (artNoDescriptors()) { 
          artCheckAcceptance();
          artLoadParseTime();
          artLoadEndMemory();
          return;
         }
        artUnloadDescriptorMGLL();
    }
}

public void ARTSet1initialise() {
  ARTSet1 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet1, 0, artSetExtent, false);
}

public void ARTSet9initialise() {
  ARTSet9 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
  ARTSet9[ARTL_ART_id2] = true;
}

public void ARTSet5initialise() {
  ARTSet5 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
  ARTSet5[ARTL_ART_S] = true;
  ARTSet5[ARTL_ART_id] = true;
  ARTSet5[ARTL_ART_id1] = true;
  ARTSet5[ARTL_ART_id2] = true;
}

public void ARTSet6initialise() {
  ARTSet6 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
  ARTSet6[ARTL_ART_id1] = true;
  ARTSet6[ARTL_ART_id2] = true;
}

public void ARTSet8initialise() {
  ARTSet8 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
  ARTSet8[ARTL_ART_id1] = true;
}

public void ARTSet3initialise() {
  ARTSet3 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
  ARTSet3[ARTX_EOS] = true;
}

public void ARTSet2initialise() {
  ARTSet2 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
  ARTSet2[ARTL_ART_id] = true;
  ARTSet2[ARTL_ART_id1] = true;
  ARTSet2[ARTL_ART_id2] = true;
}

public void ARTSet7initialise() {
  ARTSet7 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
  ARTSet7[ARTX_EOS] = true;
  ARTSet7[ARTL_ART_S] = true;
  ARTSet7[ARTL_ART_id] = true;
  ARTSet7[ARTL_ART_id1] = true;
  ARTSet7[ARTL_ART_id2] = true;
}

public void ARTSet4initialise() {
  ARTSet4 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
}

public void artSetInitialise() {
  ARTSet1initialise();
  ARTSet9initialise();
  ARTSet5initialise();
  ARTSet6initialise();
  ARTSet8initialise();
  ARTSet3initialise();
  ARTSet2initialise();
  ARTSet7initialise();
  ARTSet4initialise();
}

public void artTableInitialiser_ART_ARTLexerStart() {
  artLabelInternalStrings[ARTL_ART_ARTLexerStart] = "ARTLexerStart";
  artLabelStrings[ARTL_ART_ARTLexerStart] = "ARTLexerStart";
  artKindOfs[ARTL_ART_ARTLexerStart] = ARTK_NONTERMINAL;
}

public void artTableInitialiser_ART_S() {
  artLabelInternalStrings[ARTL_ART_S] = "S";
  artLabelStrings[ARTL_ART_S] = "S";
  artKindOfs[ARTL_ART_S] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_S_2] = "S ::= . id ";
  artLabelStrings[ARTL_ART_S_2] = "";
  artlhsL[ARTL_ART_S_2] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_2] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_S_4] = "S ::= id .";
  artLabelStrings[ARTL_ART_S_4] = "";
  artlhsL[ARTL_ART_S_4] = ARTL_ART_S;
  artSlotInstanceOfs[ARTL_ART_S_4] = ARTL_ART_id;
  artKindOfs[ARTL_ART_S_4] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_S_4] = true;
  arteoR_pL[ARTL_ART_S_4] = true;
  artPopD[ARTL_ART_S_4] = true;
  artLabelInternalStrings[ARTL_ART_S_6] = "S ::= . id S ";
  artLabelStrings[ARTL_ART_S_6] = "";
  artlhsL[ARTL_ART_S_6] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_6] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_S_8] = "S ::= id . S ";
  artLabelStrings[ARTL_ART_S_8] = "";
  artlhsL[ARTL_ART_S_8] = ARTL_ART_S;
  artSlotInstanceOfs[ARTL_ART_S_8] = ARTL_ART_id;
  artKindOfs[ARTL_ART_S_8] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_S_8] = true;
  artLabelInternalStrings[ARTL_ART_S_10] = "S ::= id S .";
  artLabelStrings[ARTL_ART_S_10] = "";
  artlhsL[ARTL_ART_S_10] = ARTL_ART_S;
  artSlotInstanceOfs[ARTL_ART_S_10] = ARTL_ART_S;
  artKindOfs[ARTL_ART_S_10] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_S_10] = true;
  arteoR_pL[ARTL_ART_S_10] = true;
  artPopD[ARTL_ART_S_10] = true;
}

public void artTableInitialiser_ART_id() {
  artLabelInternalStrings[ARTL_ART_id] = "id";
  artLabelStrings[ARTL_ART_id] = "id";
  artKindOfs[ARTL_ART_id] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_id_12] = "id ::= . id1 ";
  artLabelStrings[ARTL_ART_id_12] = "";
  artlhsL[ARTL_ART_id_12] = ARTL_ART_id;
  artKindOfs[ARTL_ART_id_12] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_id_13] = "id ::= id1 ";
  artLabelStrings[ARTL_ART_id_13] = "";
  artlhsL[ARTL_ART_id_13] = ARTL_ART_id;
  artLabelInternalStrings[ARTL_ART_id_14] = "id ::= id1 .";
  artLabelStrings[ARTL_ART_id_14] = "";
  artlhsL[ARTL_ART_id_14] = ARTL_ART_id;
  artSlotInstanceOfs[ARTL_ART_id_14] = ARTL_ART_id1;
  artKindOfs[ARTL_ART_id_14] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_id_14] = true;
  arteoR_pL[ARTL_ART_id_14] = true;
  artPopD[ARTL_ART_id_14] = true;
  artLabelInternalStrings[ARTL_ART_id_16] = "id ::= . id2 ";
  artLabelStrings[ARTL_ART_id_16] = "";
  artlhsL[ARTL_ART_id_16] = ARTL_ART_id;
  artKindOfs[ARTL_ART_id_16] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_id_17] = "id ::= id2 ";
  artLabelStrings[ARTL_ART_id_17] = "";
  artlhsL[ARTL_ART_id_17] = ARTL_ART_id;
  artLabelInternalStrings[ARTL_ART_id_18] = "id ::= id2 .";
  artLabelStrings[ARTL_ART_id_18] = "";
  artlhsL[ARTL_ART_id_18] = ARTL_ART_id;
  artSlotInstanceOfs[ARTL_ART_id_18] = ARTL_ART_id2;
  artKindOfs[ARTL_ART_id_18] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_id_18] = true;
  arteoR_pL[ARTL_ART_id_18] = true;
  artPopD[ARTL_ART_id_18] = true;
}

public void artTableInitialiser_ART_id1() {
  artLabelInternalStrings[ARTL_ART_id1] = "id1";
  artLabelStrings[ARTL_ART_id1] = "id1";
  artKindOfs[ARTL_ART_id1] = ARTK_NONTERMINAL;
}

public void artTableInitialiser_ART_id2() {
  artLabelInternalStrings[ARTL_ART_id2] = "id2";
  artLabelStrings[ARTL_ART_id2] = "id2";
  artKindOfs[ARTL_ART_id2] = ARTK_NONTERMINAL;
}

public void artTableInitialiser_ART_letter() {
  artLabelInternalStrings[ARTL_ART_letter] = "letter";
  artLabelStrings[ARTL_ART_letter] = "letter";
  artKindOfs[ARTL_ART_letter] = ARTK_NONTERMINAL;
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
  if (artLonger[ARTL_ART_id1] == null) artLonger[ARTL_ART_id1] = new ARTBitSet(ARTX_LABEL_EXTENT + 1);
  artLonger[ARTL_ART_id1].set(ARTL_ART_id1);

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

  artLabelStrings[ARTTC__HT] = "\t";
  artLabelInternalStrings[ARTTC__HT] = "`\\t";
  artKindOfs[ARTTC__HT] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC__LF] = "\n";
  artLabelInternalStrings[ARTTC__LF] = "`\\n";
  artKindOfs[ARTTC__LF] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC__CR] = "\r";
  artLabelInternalStrings[ARTTC__CR] = "`\\r";
  artKindOfs[ARTTC__CR] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC__SPACE] = " ";
  artLabelInternalStrings[ARTTC__SPACE] = "` ";
  artKindOfs[ARTTC__SPACE] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC_p] = "p";
  artLabelInternalStrings[ARTTC_p] = "`p";
  artKindOfs[ARTTC_p] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC_q] = "q";
  artLabelInternalStrings[ARTTC_q] = "`q";
  artKindOfs[ARTTC_q] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC_r] = "r";
  artLabelInternalStrings[ARTTC_r] = "`r";
  artKindOfs[ARTTC_r] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC_s] = "s";
  artLabelInternalStrings[ARTTC_s] = "`s";
  artKindOfs[ARTTC_s] = ARTK_CHARACTER_TERMINAL;
  artTableInitialiser_ART_ARTLexerStart();
  artTableInitialiser_ART_S();
  artTableInitialiser_ART_id();
  artTableInitialiser_ART_id1();
  artTableInitialiser_ART_id2();
  artTableInitialiser_ART_letter();
}

public ARTGeneratedParser(ARTLexerV3 artLexer) {
  this(null, artLexer);
}

public ARTGeneratedParser(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
  super(artGrammar, artLexer);
  artParserKind = "MGLL Gen";
  artFirstTerminalLabel = ARTTC__HT;
  artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
  artSetExtent = 16;
  ARTL_EOS = ARTX_EOS;
  ARTL_EPSILON = ARTX_EPSILON;
  ARTL_DUMMY = ARTX_DUMMY;
  artGrammarKind = ARTModeGrammarKind.BNF;
  artDefaultStartSymbolLabel = ARTL_ART_S;
  artBuildDirectives = "ARTDirectives [inputs=[], inputFilenames=[], directives={suppressPopGuard=false, tweLexicalisations=false, algorithmMode=mgllGeneratorPool, tweLongest=false, tweSegments=false, sppfShortest=false, termWrite=false, tweCounts=false, clusteredGSS=false, twePrint=false, rewriteDisable=false, tweAmbiguityClasses=false, sppfAmbiguityAnalysis=false, rewriteConfiguration=false, outputDirectory=., inputCounts=false, twePriority=false, treeShow=false, tweRecursive=false, rewritePostorder=false, rewriteContractum=true, parseCounts=false, predictivePops=false, suppressProductionGuard=false, sppfDead=false, twePrintFull=false, input=0, tweExtents=false, suppressSemantics=false, despatchMode=fragment, treePrintLevel=3, sppfShowFull=false, treePrint=false, sppfChooseCounts=false, log=1, tweDump=false, sppfCycleDetect=false, sppfCountSentences=false, parserName=ARTGeneratedParser, rewriteResume=true, inputPrint=false, lexerName=ARTGeneratedLexer, trace=false, tweTokenWrite=false, tweDead=false, tweShortest=false, rewritePure=true, tweSelectOne=false, smlCycleBreak=false, termPrint=false, suppressTestRepeat=false, rewritePreorder=false, sppfAmbiguityAnalysisFull=false, tweFromSPPF=false, actionSuppress=false, tweLexicalisationsQuick=false, sppfPriority=false, sppfShow=false, rewriteOneStep=false, namespace=, sppfSelectOne=false, FIFODescriptors=false, sppfOrderedLongest=false, verbosity=0, sppfLongest=false, gssShow=false}]";
  artFIFODescriptors = false;
  artSetInitialise();
  artTableInitialise();
}

public void ARTRD_ARTLexerStart(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        default: ; }}}
}

public void ARTRD_S(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*S ::= id .*/
    case ARTL_ART_S_4: 
            ARTRD_S(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_id(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*S ::= id S .*/
    case ARTL_ART_S_10: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_id(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_S(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_id(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*id ::= id1 .*/
    case ARTL_ART_id_14: 
            ARTRD_id(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_id1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*id ::= id2 .*/
    case ARTL_ART_id_18: 
            ARTRD_id(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_id2(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_id1(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        default: ; }}}
}

public void ARTRD_id2(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        default: ; }}}
}

public void ARTRD_letter(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        default: ; }}}
}

public void artEvaluate(ARTGLLRDTHandle artElement, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable)  {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ART_ARTLexerStart: ARTRD_ARTLexerStart(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_S: ARTRD_S(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_id: ARTRD_id(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_id1: ARTRD_id1(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_id2: ARTRD_id2(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_letter: ARTRD_letter(artElement.element, artParent, artWriteable); break;
  }
}

};
