import uk.ac.rhul.cs.csle.art.alg.gll.support.*;
import uk.ac.rhul.cs.csle.art.lex.*;
import uk.ac.rhul.cs.csle.art.manager.*;
import uk.ac.rhul.cs.csle.art.manager.grammar.*;
import uk.ac.rhul.cs.csle.art.manager.mode.*;
import uk.ac.rhul.cs.csle.art.util.*;
/*******************************************************************************
*
* ARTGeneratedParser.java
*
*******************************************************************************/
public class ARTGeneratedParser extends ARTGLLParserHashPool {
  private static boolean[] ARTSet1;
  private static boolean[] ARTSet2;
  private static boolean[] ARTSet3;
  private static boolean[] ARTSet4;
  private static boolean[] ARTSet5;
  private static boolean[] ARTSet6;
  private static boolean[] ARTSet7;
  private static boolean[] ARTSet8;
  private static boolean[] ARTSet9;
  private static boolean[] ARTSet10;
  private static boolean[] ARTSet11;

  /* Start of artLabel enumeration */
  public static final int ARTX_EOS = 0;
  public static final int ARTTB_ID = 1;
  public static final int ARTTC_b = 2;
  public static final int ARTTS_a = 3;
  public static final int ARTX_EPSILON = 4;
  public static final int ARTTN_ART_TFN_ART__DOLLARB_PERCENT_DOLLAR = 5;
  public static final int ARTTB_WHITESPACE = 6;
  public static final int ARTL_ART__DOLLARB_PERCENT_DOLLAR = 7;
  public static final int ARTL_ART_S = 8;
  public static final int ARTL_ART__DOLLARB_PERCENT_DOLLAR_14 = 9;
  public static final int ARTL_ART__DOLLARB_PERCENT_DOLLAR_16 = 10;
  public static final int ARTL_ART__DOLLARB_PERCENT_DOLLAR_15 = 11;
  public static final int ARTL_ART__DOLLARB_PERCENT_DOLLAR_18 = 12;
  public static final int ARTL_ART__DOLLARB_PERCENT_DOLLAR_20 = 13;
  public static final int ARTL_ART_S_2 = 14;
  public static final int ARTL_ART_S_6 = 15;
  public static final int ARTL_ART_S_11 = 16;
  public static final int ARTL_ART_S_12 = 17;
  public static final int ARTL_ART_S_3 = 18;
  public static final int ARTL_ART_S_4 = 19;
  public static final int ARTL_ART_S_10 = 20;
  public static final int ARTX_DESPATCH = 21;
  public static final int ARTX_DUMMY = 22;
  public static final int ARTX_LABEL_EXTENT = 23;
  /* End of artLabel enumeration */

  /* Start of artName enumeration */
  public static final int ARTNAME_NONE = 0;
  public static final int ARTNAME_EXTENT = 1;
  /* End of artName enumeration */
  public void ARTPF_ART__DOLLARB_PERCENT_DOLLAR() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal $B%$ production descriptor loads*/
      case ARTL_ART__DOLLARB_PERCENT_DOLLAR: 
        if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ART__DOLLARB_PERCENT_DOLLAR_14, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ART__DOLLARB_PERCENT_DOLLAR_18, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal $B%$: match production*/
      case ARTL_ART__DOLLARB_PERCENT_DOLLAR_14: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTC_b, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_ART__DOLLARB_PERCENT_DOLLAR_16, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal $B%$: match production*/
      case ARTL_ART__DOLLARB_PERCENT_DOLLAR_18: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_ART__DOLLARB_PERCENT_DOLLAR_20, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ART_S() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal S production descriptor loads*/
      case ARTL_ART_S: 
        if (ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ART_S_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
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
        if (!ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ART_S_6, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ART__DOLLARB_PERCENT_DOLLAR; return; }
      case ARTL_ART_S_6: 
        /* Nonterminal template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Optional, non-nullable body template start */
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {         artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artTemporarySPPFNode = artFindSPPF(ARTL_ART_S_12, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        artFindDescriptor(ARTL_ART_S_12, artCurrentGSSNode, artCurrentInputPairIndex, artTemporarySPPFNode);
 }
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_ART_S_11, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
      case ARTL_ART_S_12: 
        /* Optional, non-nullable body template end */
        /* Cat/unary template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void artParseBody(String artInputString, int artStartLabel) throws ARTException {
    artSetupCompleteTime = artReadClock();
    artStartSymbolLabel = artStartLabel;
    artIsInLanguage = false;
    artLexicaliseUsingLongestMatch(artInputString);
    artLexCompleteTime = artReadClock();
    artDummySPPFNode = artFindSPPFInitial(ARTL_DUMMY, 0, 0);
    artCurrentSPPFNode = artDummySPPFNode;
    artRootGSSNode = artFindGSS(ARTL_EOS, 0, 0, 0);
    artCurrentGSSNode = artRootGSSNode;
    artCurrentRestartLabel = artStartSymbolLabel;
    artCurrentInputPairIndex = 0;
    artCurrentInputPairReference = 0;
    while (true)
      switch (artlhsL[artCurrentRestartLabel]) {
        case ARTL_ART__DOLLARB_PERCENT_DOLLAR: 
          ARTPF_ART__DOLLARB_PERCENT_DOLLAR();
          break;
        case ARTL_ART_S: 
          ARTPF_ART_S();
          break;
        case ARTX_DESPATCH: 
          if (artNoDescriptors()) { 
            artCheckAcceptance();
            artParseCompleteTime = artReadClock();
            return;
           }
          artUnloadDescriptor();
      }
  }

  public void ARTSet1initialise() {
    ARTSet1 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet1, 0, artSetExtent, false);
  }

  public void ARTSet8initialise() {
    ARTSet8 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
    ARTSet8[ARTTB_ID] = true;
    ARTSet8[ARTTC_b] = true;
  }

  public void ARTSet7initialise() {
    ARTSet7 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
    ARTSet7[ARTX_EOS] = true;
  }

  public void ARTSet10initialise() {
    ARTSet10 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet10, 0, artSetExtent, false);
    ARTSet10[ARTTB_ID] = true;
  }

  public void ARTSet2initialise() {
    ARTSet2 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
    ARTSet2[ARTTC_b] = true;
  }

  public void ARTSet11initialise() {
    ARTSet11 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet11, 0, artSetExtent, false);
    ARTSet11[ARTTB_ID] = true;
  }

  public void ARTSet5initialise() {
    ARTSet5 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
  }

  public void ARTSet9initialise() {
    ARTSet9 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
    ARTSet9[ARTX_EOS] = true;
    ARTSet9[ARTTB_ID] = true;
    ARTSet9[ARTTC_b] = true;
  }

  public void ARTSet3initialise() {
    ARTSet3 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
    ARTSet3[ARTX_EOS] = true;
    ARTSet3[ARTTB_ID] = true;
  }

  public void ARTSet6initialise() {
    ARTSet6 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
    ARTSet6[ARTTS_a] = true;
  }

  public void ARTSet4initialise() {
    ARTSet4 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
    ARTSet4[ARTTC_b] = true;
  }

  public void artSetInitialise() {
    ARTSet1initialise();
    ARTSet8initialise();
    ARTSet7initialise();
    ARTSet10initialise();
    ARTSet2initialise();
    ARTSet11initialise();
    ARTSet5initialise();
    ARTSet9initialise();
    ARTSet3initialise();
    ARTSet6initialise();
    ARTSet4initialise();
  }

  public void artTableInitialiser_ART__DOLLARB_PERCENT_DOLLAR() {
    artLabelInternalStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR] = "$B%$";
    artLabelStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR] = "$B%$";
    artKindOfs[ARTL_ART__DOLLARB_PERCENT_DOLLAR] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_14] = "$B%$ ::= . `b ";
    artLabelStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_14] = "";
    artlhsL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_14] = ARTL_ART__DOLLARB_PERCENT_DOLLAR;
    artKindOfs[ARTL_ART__DOLLARB_PERCENT_DOLLAR_14] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ART__DOLLARB_PERCENT_DOLLAR_14] = true;
    artLabelInternalStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_15] = "$B%$ ::= `b ";
    artLabelStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_15] = "";
    artlhsL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_15] = ARTL_ART__DOLLARB_PERCENT_DOLLAR;
    artPopD[ARTL_ART__DOLLARB_PERCENT_DOLLAR_15] = true;
    artLabelInternalStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_16] = "$B%$ ::= `b .";
    artLabelStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_16] = "";
    artlhsL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_16] = ARTL_ART__DOLLARB_PERCENT_DOLLAR;
    artKindOfs[ARTL_ART__DOLLARB_PERCENT_DOLLAR_16] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_16] = true;
    arteoR_pL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_16] = true;
    artPopD[ARTL_ART__DOLLARB_PERCENT_DOLLAR_16] = true;
    artLabelInternalStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_18] = "$B%$ ::= . # ";
    artLabelStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_18] = "";
    artlhsL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_18] = ARTL_ART__DOLLARB_PERCENT_DOLLAR;
    artKindOfs[ARTL_ART__DOLLARB_PERCENT_DOLLAR_18] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ART__DOLLARB_PERCENT_DOLLAR_18] = true;
    artLabelInternalStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_20] = "$B%$ ::= # .";
    artLabelStrings[ARTL_ART__DOLLARB_PERCENT_DOLLAR_20] = "";
    artlhsL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_20] = ARTL_ART__DOLLARB_PERCENT_DOLLAR;
    artKindOfs[ARTL_ART__DOLLARB_PERCENT_DOLLAR_20] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_20] = true;
    arteoR_pL[ARTL_ART__DOLLARB_PERCENT_DOLLAR_20] = true;
    artPopD[ARTL_ART__DOLLARB_PERCENT_DOLLAR_20] = true;
  }

  public void artTableInitialiser_ART_S() {
    artLabelInternalStrings[ARTL_ART_S] = "S";
    artLabelStrings[ARTL_ART_S] = "S";
    artKindOfs[ARTL_ART_S] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_S_2] = "S ::= . 'a' $B%$ ( &ID )? ";
    artLabelStrings[ARTL_ART_S_2] = "";
    artlhsL[ARTL_ART_S_2] = ARTL_ART_S;
    artKindOfs[ARTL_ART_S_2] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_S_3] = "S ::= 'a' $B%$ ( &ID )? ";
    artLabelStrings[ARTL_ART_S_3] = "";
    artlhsL[ARTL_ART_S_3] = ARTL_ART_S;
    artLabelInternalStrings[ARTL_ART_S_4] = "S ::= 'a' . $B%$ ( &ID )? ";
    artLabelStrings[ARTL_ART_S_4] = "";
    artlhsL[ARTL_ART_S_4] = ARTL_ART_S;
    artKindOfs[ARTL_ART_S_4] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_S_4] = true;
    artLabelInternalStrings[ARTL_ART_S_6] = "S ::= 'a' $B%$ . ( &ID )? ";
    artLabelStrings[ARTL_ART_S_6] = "";
    artlhsL[ARTL_ART_S_6] = ARTL_ART_S;
    artSlotInstanceOfs[ARTL_ART_S_6] = ARTL_ART__DOLLARB_PERCENT_DOLLAR;
    artKindOfs[ARTL_ART_S_6] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_S_10] = "S ::= 'a' $B%$ ( &ID )? ";
    artLabelStrings[ARTL_ART_S_10] = "";
    artlhsL[ARTL_ART_S_10] = ARTL_ART_S;
    artPopD[ARTL_ART_S_10] = true;
    artLabelInternalStrings[ARTL_ART_S_11] = "S ::= 'a' $B%$ ( &ID .)? ";
    artLabelStrings[ARTL_ART_S_11] = "";
    artlhsL[ARTL_ART_S_11] = ARTL_ART_S;
    artKindOfs[ARTL_ART_S_11] = ARTK_INTERMEDIATE;
    artpL[ARTL_ART_S_11] = ARTL_ART_S_12;
    arteoOPL[ARTL_ART_S_11] = true;
    arteoR_pL[ARTL_ART_S_11] = true;
    artPopD[ARTL_ART_S_11] = true;
    artLabelInternalStrings[ARTL_ART_S_12] = "S ::= 'a' $B%$ ( &ID )? .";
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

    artTerminalsFromNonterminals = new boolean[ARTX_LABEL_EXTENT];
    artTerminalsFromNonterminals[ARTTN_ART_TFN_ART__DOLLARB_PERCENT_DOLLAR] = true;

    artNonterminalsDeclaredAsTerminals = new boolean[ARTX_LABEL_EXTENT];
    artNonterminalsDeclaredAsTerminals[ARTL_ART__DOLLARB_PERCENT_DOLLAR] = true;

    artLonger = new ARTBitSet[ARTX_LABEL_EXTENT + 1];
    for (int i = 0; i < ARTX_LABEL_EXTENT + 1; i++) artLonger[i] = new ARTBitSet();
    artHigher = new ARTBitSet[ARTX_LABEL_EXTENT + 1];
    for (int i = 0; i < ARTX_LABEL_EXTENT + 1; i++) artHigher[i] = new ARTBitSet();

    artAnnotations = new String[ARTX_LABEL_EXTENT];
    artInitialiseStringArray(artAnnotations, 0, ARTX_LABEL_EXTENT, null);

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

    artLabelStrings[ARTTN_ART_TFN_ART__DOLLARB_PERCENT_DOLLAR] = "ART_TFN_ART_$B%$";
    artLabelInternalStrings[ARTTN_ART_TFN_ART__DOLLARB_PERCENT_DOLLAR] = "ART_TFN_ART_$B%$";
    artLabelStrings[ARTTB_ID] = "ID";
    artLabelInternalStrings[ARTTB_ID] = "&ID";
    artKindOfs[ARTTB_ID] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_ID] = true;
    artLabelStrings[ARTTC_b] = "b";
    artLabelInternalStrings[ARTTC_b] = "`b";
    artKindOfs[ARTTC_b] = ARTK_CHARACTER_TERMINAL;
    artLabelStrings[ARTTS_a] = "a";
    artLabelInternalStrings[ARTTS_a] = "'a'";
    artKindOfs[ARTTS_a] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_a] = true;
    artTableInitialiser_ART__DOLLARB_PERCENT_DOLLAR();
    artTableInitialiser_ART_S();
  }

  public ARTGeneratedParser(ARTLexerBase artLexerBase) {
    this(null, artLexerBase);
  }

  public ARTGeneratedParser(ARTGrammar artGrammar, ARTLexerBase artLexerBase) {
    super(artGrammar, artLexerBase);
    artFirstTerminalLabel = ARTTC_b;
    artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
    artSetExtent = ARTX_EPSILON + 1;
    ARTL_EOS = ARTX_EOS;
    ARTL_EPSILON = ARTX_EPSILON;
    ARTL_DUMMY = ARTX_DUMMY;
    artGrammarKind = ARTModeGrammarKind.EBNF;
    artDefaultStartSymbolLabel = ARTL_ART_S;
    artBuildOptions = "ARTOptionBlock [verbosityLevel=0, statistics=false, trace=false, inputFilenames=[], phaseModule=true, phaseLex=true, phasePreChoose=true, phaseParse=true, phasePostChoose=true, phaseDerivationSelect=true, phaseGIFT=true, phaseAG=true, phaseTR=true, phaseSOS=true, showTWE=false, showBSR=false, showSPPFFull=false, showSPPFCore=false, showDT=false, showGIFT=false, showAG=false, showTR=false, showSOS=false, ebnfClosureLeft=false, ebnfClosureRight=false, ebnfMultiplyOut=false, ebnfLeftFactor=false, ebnfBracketToNonterminal=false, lexWSSuffix=false, lexExtents=false, lexSegments=false, lexRecursive=false, lexPrintTWESet=false, lexDFA=false, lexCFRecognise=false, lexCFParse=true, lexDead=false, lexLongestWithin=false, lexLongestAcross=false, lexPriority=false, postLongestWithin=false, postLongestAcross=false, postPriority=false, outputDirectory=., parserName=ARTGeneratedParser, lexerName=ARTGeneratedLexer, namespace=null, despatchMode=fragment, supportMode=HashPool, targetLanguageMode=Java, predictivePops=false, FIFODescriptors=false, suppressPopGuard=false, suppressProductionGuard=false, suppressTestRepeat=false, suppressSemantics=false, clusteredGSS=false, mGLL=false, algorithmMode=gllGeneratorPool, treeLevel=3]";
    artFIFODescriptors = false;
    artSetInitialise();
    artTableInitialise();
  }

  private ARTGLLRDT artRDT;
  private int artNextFreeNodeNumber = 1;
  public void ARTRD__DOLLARB_PERCENT_DOLLAR(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
  ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
      /*$B%$ ::= `b .*/
      case ARTL_ART__DOLLARB_PERCENT_DOLLAR_16: 
                ARTRD__DOLLARB_PERCENT_DOLLAR(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
        artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
                break;
      /*$B%$ ::= # .*/
      case ARTL_ART__DOLLARB_PERCENT_DOLLAR_20: 
                ARTRD__DOLLARB_PERCENT_DOLLAR(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
        artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
                break;
        default:
          throw new ARTException("ARTRD__DOLLARB_PERCENT_DOLLAR: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_S(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*S ::= 'a' $B%$ . ( &ID )? */
    case ARTL_ART_S_6: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD__DOLLARB_PERCENT_DOLLAR(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*S ::= 'a' $B%$ ( &ID .)? */
    case ARTL_ART_S_11: 
            ARTRD_S(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*S ::= 'a' $B%$ ( &ID )? .*/
    case ARTL_ART_S_12: 
            ARTRD_S(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
            break;
        default:
          throw new ARTException("ARTRD_S: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void artEvaluate(ARTGLLRDTHandle artElement, Object artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable) throws ARTException {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ART__DOLLARB_PERCENT_DOLLAR: ARTRD__DOLLARB_PERCENT_DOLLAR(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_S: ARTRD_S(artElement.element, artParent, artWriteable); break;
  }
}

public ARTGLLRDT artEvaluator() throws ARTException {
  artText.println("artEvaluator not supported for EBNF grammars");
  return null;
}
};
