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
private static boolean[] ARTSet10;
private static boolean[] ARTSet11;
private static boolean[] ARTSet12;
private static boolean[] ARTSet13;
private static boolean[] ARTSet14;
private static boolean[] ARTSet15;
private static boolean[] ARTSet16;
private static boolean[] ARTSet17;
private static boolean[] ARTSet18;
private static boolean[] ARTSet19;

/* Start of artLabel enumeration */
public static final int ARTX_EOS = 0;
public static final int ARTTC__HT = 1;
public static final int ARTTC__LF = 2;
public static final int ARTTC__CR = 3;
public static final int ARTTC__SPACE = 4;
public static final int ARTTC_x = 5;
public static final int ARTTC_y = 6;
public static final int ARTTC_z = 7;
public static final int ARTX_EPSILON = 8;
public static final int ARTL_ART_ARTLexerStart = 9;
public static final int ARTL_ART_ART_CSP_x = 10;
public static final int ARTL_ART_ART_CSP_y = 11;
public static final int ARTL_ART_ART_CSP_z = 12;
public static final int ARTL_ART_S = 13;
public static final int ARTL_ART_Y = 14;
public static final int ARTL_ART_ARTLexerStart_1 = 15;
public static final int ARTL_ART_ARTLexerStart_2 = 16;
public static final int ARTL_ART_ARTLexerStart_3 = 17;
public static final int ARTC_ART_ARTLexerStart_3 = 18;
public static final int ARTL_ART_ARTLexerStart_4 = 19;
public static final int ARTA_ART_ARTLexerStart_4 = 20;
public static final int ARTL_ART_ARTLexerStart_5 = 21;
public static final int ARTL_ART_ARTLexerStart_6 = 22;
public static final int ARTL_ART_ARTLexerStart_7 = 23;
public static final int ARTL_ART_ARTLexerStart_8 = 24;
public static final int ARTL_ART_ARTLexerStart_9 = 25;
public static final int ARTL_ART_ARTLexerStart_10 = 26;
public static final int ARTL_ART_ARTLexerStart_11 = 27;
public static final int ARTL_ART_ARTLexerStart_12 = 28;
public static final int ARTL_ART_ARTLexerStart_13 = 29;
public static final int ARTL_ART_ARTLexerStart_14 = 30;
public static final int ARTL_ART_ARTLexerStart_15 = 31;
public static final int ARTL_ART_ARTLexerStart_16 = 32;
public static final int ARTL_ART_ARTLexerStart_17 = 33;
public static final int ARTL_ART_ART_CSP_x_18 = 34;
public static final int ARTL_ART_ART_CSP_x_19 = 35;
public static final int ARTL_ART_ART_CSP_x_20 = 36;
public static final int ARTL_ART_ART_CSP_x_21 = 37;
public static final int ARTL_ART_ART_CSP_x_22 = 38;
public static final int ARTC_ART_ART_CSP_x_22 = 39;
public static final int ARTL_ART_ART_CSP_x_23 = 40;
public static final int ARTA_ART_ART_CSP_x_23 = 41;
public static final int ARTL_ART_ART_CSP_x_24 = 42;
public static final int ARTL_ART_ART_CSP_x_25 = 43;
public static final int ARTL_ART_ART_CSP_x_26 = 44;
public static final int ARTL_ART_ART_CSP_x_27 = 45;
public static final int ARTL_ART_ART_CSP_x_28 = 46;
public static final int ARTL_ART_ART_CSP_x_29 = 47;
public static final int ARTL_ART_ART_CSP_x_30 = 48;
public static final int ARTL_ART_ART_CSP_x_31 = 49;
public static final int ARTL_ART_ART_CSP_x_32 = 50;
public static final int ARTL_ART_ART_CSP_x_33 = 51;
public static final int ARTL_ART_ART_CSP_x_34 = 52;
public static final int ARTL_ART_ART_CSP_x_35 = 53;
public static final int ARTL_ART_ART_CSP_x_36 = 54;
public static final int ARTL_ART_ART_CSP_x_37 = 55;
public static final int ARTL_ART_ART_CSP_x_38 = 56;
public static final int ARTL_ART_ART_CSP_x_39 = 57;
public static final int ARTL_ART_ART_CSP_x_40 = 58;
public static final int ARTL_ART_ART_CSP_y_41 = 59;
public static final int ARTL_ART_ART_CSP_y_42 = 60;
public static final int ARTL_ART_ART_CSP_y_43 = 61;
public static final int ARTL_ART_ART_CSP_y_44 = 62;
public static final int ARTL_ART_ART_CSP_y_45 = 63;
public static final int ARTC_ART_ART_CSP_y_45 = 64;
public static final int ARTL_ART_ART_CSP_y_46 = 65;
public static final int ARTA_ART_ART_CSP_y_46 = 66;
public static final int ARTL_ART_ART_CSP_y_47 = 67;
public static final int ARTL_ART_ART_CSP_y_48 = 68;
public static final int ARTL_ART_ART_CSP_y_49 = 69;
public static final int ARTL_ART_ART_CSP_y_50 = 70;
public static final int ARTL_ART_ART_CSP_y_51 = 71;
public static final int ARTL_ART_ART_CSP_y_52 = 72;
public static final int ARTL_ART_ART_CSP_y_53 = 73;
public static final int ARTL_ART_ART_CSP_y_54 = 74;
public static final int ARTL_ART_ART_CSP_y_55 = 75;
public static final int ARTL_ART_ART_CSP_y_56 = 76;
public static final int ARTL_ART_ART_CSP_y_57 = 77;
public static final int ARTL_ART_ART_CSP_y_58 = 78;
public static final int ARTL_ART_ART_CSP_y_59 = 79;
public static final int ARTL_ART_ART_CSP_y_60 = 80;
public static final int ARTL_ART_ART_CSP_y_61 = 81;
public static final int ARTL_ART_ART_CSP_y_62 = 82;
public static final int ARTL_ART_ART_CSP_y_63 = 83;
public static final int ARTL_ART_ART_CSP_z_64 = 84;
public static final int ARTL_ART_ART_CSP_z_65 = 85;
public static final int ARTL_ART_ART_CSP_z_66 = 86;
public static final int ARTL_ART_ART_CSP_z_67 = 87;
public static final int ARTL_ART_ART_CSP_z_68 = 88;
public static final int ARTC_ART_ART_CSP_z_68 = 89;
public static final int ARTL_ART_ART_CSP_z_69 = 90;
public static final int ARTA_ART_ART_CSP_z_69 = 91;
public static final int ARTL_ART_ART_CSP_z_70 = 92;
public static final int ARTL_ART_ART_CSP_z_71 = 93;
public static final int ARTL_ART_ART_CSP_z_72 = 94;
public static final int ARTL_ART_ART_CSP_z_73 = 95;
public static final int ARTL_ART_ART_CSP_z_74 = 96;
public static final int ARTL_ART_ART_CSP_z_75 = 97;
public static final int ARTL_ART_ART_CSP_z_76 = 98;
public static final int ARTL_ART_ART_CSP_z_77 = 99;
public static final int ARTL_ART_ART_CSP_z_78 = 100;
public static final int ARTL_ART_ART_CSP_z_79 = 101;
public static final int ARTL_ART_ART_CSP_z_80 = 102;
public static final int ARTL_ART_ART_CSP_z_81 = 103;
public static final int ARTL_ART_ART_CSP_z_82 = 104;
public static final int ARTL_ART_ART_CSP_z_83 = 105;
public static final int ARTL_ART_ART_CSP_z_84 = 106;
public static final int ARTL_ART_ART_CSP_z_85 = 107;
public static final int ARTL_ART_ART_CSP_z_86 = 108;
public static final int ARTX_DESPATCH = 109;
public static final int ARTX_DUMMY = 110;
public static final int ARTX_LABEL_EXTENT = 111;
/* End of artLabel enumeration */

/* Start of artName enumeration */
public static final int ARTNAME_NONE = 0;
public static final int ARTNAME_EXTENT = 1;
/* End of artName enumeration */

public void ARTPF_ART_ARTLexerStart() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal ARTLexerStart production descriptor loads*/
    case ARTL_ART_ARTLexerStart: 
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_ARTLexerStart_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal ARTLexerStart: match production*/
    case ARTL_ART_ARTLexerStart_2: 
      /* Cat/unary template start */
      /* Kleene closure, non-nullable body template start */
      artCurrentGSSNode = artFindGSSRecogniser(ARTL_ART_ARTLexerStart_17, artCurrentGSSNode, artCurrentInputPairIndex);
      if (ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) {       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
 }
      /*L*/
    case ARTC_ART_ARTLexerStart_3: 
      if (!ARTSet5[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template start */
      if (ARTSet6[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ARTLexerStart_6, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ARTLexerStart_10, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet8[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ARTLexerStart_14, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
    case ARTL_ART_ARTLexerStart_6: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSSRecogniser(ARTL_ART_ARTLexerStart_8, artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTL_ART_ART_CSP_x; return; }
    case ARTL_ART_ARTLexerStart_8: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ARTLexerStart_4; return; }
    case ARTL_ART_ARTLexerStart_10: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSSRecogniser(ARTL_ART_ARTLexerStart_12, artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTL_ART_ART_CSP_y; return; }
    case ARTL_ART_ARTLexerStart_12: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ARTLexerStart_4; return; }
    case ARTL_ART_ARTLexerStart_14: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSSRecogniser(ARTL_ART_ARTLexerStart_16, artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTL_ART_ART_CSP_z; return; }
    case ARTL_ART_ARTLexerStart_16: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ARTLexerStart_4; return; }
    case ARTA_ART_ARTLexerStart_4: 
      if (artTestRepeat(ARTL_ART_ARTLexerStart_4, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode)) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template end */
      if (ARTSet3[artInputPairBuffer[artCurrentInputPairReference]])       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTC_ART_ARTLexerStart_3; return; }
    case ARTL_ART_ARTLexerStart_17: 
      /* Kleene closure, non-nullable body template end */
      /* Cat/unary template end */
      tweSet.ARTLexerV3Wrapup(artCurrentInputPairIndex);
            artIsInLanguage |= artCurrentInputPairIndex == artLexer.artInputLength - artWhitespaceEOSPrefixLength;
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_ART_CSP_x() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal ART_CSP_x production descriptor loads*/
    case ARTL_ART_ART_CSP_x: 
      if (ARTSet10[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_ART_CSP_x_19, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal ART_CSP_x: match production*/
    case ARTL_ART_ART_CSP_x_19: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Kleene closure, non-nullable body template start */
      artCurrentGSSNode = artFindGSSRecogniser(ARTL_ART_ART_CSP_x_40, artCurrentGSSNode, artCurrentInputPairIndex);
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) {       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
 }
      /*L*/
    case ARTC_ART_ART_CSP_x_22: 
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template start */
      if (ARTSet14[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_x_25, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_x_29, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet16[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_x_33, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_x_37, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
    case ARTL_ART_ART_CSP_x_25: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_x_23; return; }
    case ARTL_ART_ART_CSP_x_29: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_x_23; return; }
    case ARTL_ART_ART_CSP_x_33: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_x_23; return; }
    case ARTL_ART_ART_CSP_x_37: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_x_23; return; }
    case ARTA_ART_ART_CSP_x_23: 
      if (artTestRepeat(ARTL_ART_ART_CSP_x_23, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode)) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template end */
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]])       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTC_ART_ART_CSP_x_22; return; }
    case ARTL_ART_ART_CSP_x_40: 
      /* Kleene closure, non-nullable body template end */
      /* Cat/unary template end */
      tweSet.tweSetUpdateExactMakeLeftSet(ARTL_ART_ART_CSP_x, artGSSNodeLevel(artCurrentGSSNode), artCurrentInputPairIndex);
      if (!ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_ART_CSP_y() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal ART_CSP_y production descriptor loads*/
    case ARTL_ART_ART_CSP_y: 
      if (ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_ART_CSP_y_42, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal ART_CSP_y: match production*/
    case ARTL_ART_ART_CSP_y_42: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Kleene closure, non-nullable body template start */
      artCurrentGSSNode = artFindGSSRecogniser(ARTL_ART_ART_CSP_y_63, artCurrentGSSNode, artCurrentInputPairIndex);
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) {       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
 }
      /*L*/
    case ARTC_ART_ART_CSP_y_45: 
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template start */
      if (ARTSet14[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_y_48, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_y_52, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet16[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_y_56, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_y_60, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
    case ARTL_ART_ART_CSP_y_48: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_y_46; return; }
    case ARTL_ART_ART_CSP_y_52: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_y_46; return; }
    case ARTL_ART_ART_CSP_y_56: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_y_46; return; }
    case ARTL_ART_ART_CSP_y_60: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_y_46; return; }
    case ARTA_ART_ART_CSP_y_46: 
      if (artTestRepeat(ARTL_ART_ART_CSP_y_46, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode)) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template end */
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]])       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTC_ART_ART_CSP_y_45; return; }
    case ARTL_ART_ART_CSP_y_63: 
      /* Kleene closure, non-nullable body template end */
      /* Cat/unary template end */
      tweSet.tweSetUpdateExactMakeLeftSet(ARTL_ART_ART_CSP_y, artGSSNodeLevel(artCurrentGSSNode), artCurrentInputPairIndex);
      if (!ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_ART_CSP_z() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal ART_CSP_z production descriptor loads*/
    case ARTL_ART_ART_CSP_z: 
      if (ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_ART_CSP_z_65, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal ART_CSP_z: match production*/
    case ARTL_ART_ART_CSP_z_65: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Kleene closure, non-nullable body template start */
      artCurrentGSSNode = artFindGSSRecogniser(ARTL_ART_ART_CSP_z_86, artCurrentGSSNode, artCurrentInputPairIndex);
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) {       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
 }
      /*L*/
    case ARTC_ART_ART_CSP_z_68: 
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template start */
      if (ARTSet14[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_z_71, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_z_75, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet16[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_z_79, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]])       artFindDescriptor(ARTL_ART_ART_CSP_z_83, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
    case ARTL_ART_ART_CSP_z_71: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_z_69; return; }
    case ARTL_ART_ART_CSP_z_75: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_z_69; return; }
    case ARTL_ART_ART_CSP_z_79: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_z_69; return; }
    case ARTL_ART_ART_CSP_z_83: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      /* Terminal template end */
      /* Cat/unary template end */
      { artCurrentRestartLabel = ARTA_ART_ART_CSP_z_69; return; }
    case ARTA_ART_ART_CSP_z_69: 
      if (artTestRepeat(ARTL_ART_ART_CSP_z_69, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode)) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Alternate template end */
      if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]])       artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTC_ART_ART_CSP_z_68; return; }
    case ARTL_ART_ART_CSP_z_86: 
      /* Kleene closure, non-nullable body template end */
      /* Cat/unary template end */
      tweSet.tweSetUpdateExactMakeLeftSet(ARTL_ART_ART_CSP_z, artGSSNodeLevel(artCurrentGSSNode), artCurrentInputPairIndex);
      if (!ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPopRecogniser(artCurrentGSSNode, artCurrentInputPairIndex);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_S() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal S production descriptor loads*/
    case ARTL_ART_S: 
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
  }
}

public void ARTPF_ART_Y() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal Y production descriptor loads*/
    case ARTL_ART_Y: 
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
  }
}

public void artParseBody(int artStartLabel) {
  tweSet = new ARTLexerV3(artHigher, artLonger, artInputString, artInputString.length(), ARTL_EOS, artLabelInternalStrings, artDirectives, 666);

  tweSet.tweSetUpdateExactMakeLeftSet(0, artInputString.length(), artInputString.length() + 1); // Add terminating EOS  artLoadSetupTime();
  artSpecificationName = "artChoose.art";
  artStartSymbolLabel = artStartLabel;
  artIsInLanguage = false;
  artTokenExtent = 15;
  artLexicaliseForV3GLL(artInputString, null);
  artLoadLexTime();
  artRootGSSNode = artFindGSSRecogniser(ARTL_EOS, 0, 0);
  artCurrentGSSNode = artRootGSSNode;
  artCurrentRestartLabel = artStartSymbolLabel;
  artCurrentInputPairIndex = 0;
  artCurrentInputPairReference = 0;
  while (true)
    switch (artlhsL[artCurrentRestartLabel]) {
      case ARTL_ART_ARTLexerStart: 
        ARTPF_ART_ARTLexerStart();
        break;
      case ARTL_ART_ART_CSP_x: 
        ARTPF_ART_ART_CSP_x();
        break;
      case ARTL_ART_ART_CSP_y: 
        ARTPF_ART_ART_CSP_y();
        break;
      case ARTL_ART_ART_CSP_z: 
        ARTPF_ART_ART_CSP_z();
        break;
      case ARTL_ART_S: 
        ARTPF_ART_S();
        break;
      case ARTL_ART_Y: 
        ARTPF_ART_Y();
        break;
      case ARTX_DESPATCH: 
        if (artNoDescriptors()) { 
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

public void ARTSet16initialise() {
  ARTSet16 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet16, 0, artSetExtent, false);
  ARTSet16[ARTTC__HT] = true;
}

public void ARTSet14initialise() {
  ARTSet14 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet14, 0, artSetExtent, false);
  ARTSet14[ARTTC__LF] = true;
}

public void ARTSet12initialise() {
  ARTSet12 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet12, 0, artSetExtent, false);
  ARTSet12[ARTX_EOS] = true;
  ARTSet12[ARTTC__HT] = true;
  ARTSet12[ARTTC__LF] = true;
  ARTSet12[ARTTC__CR] = true;
  ARTSet12[ARTTC__SPACE] = true;
  ARTSet12[ARTTC_x] = true;
  ARTSet12[ARTTC_y] = true;
  ARTSet12[ARTTC_z] = true;
  ARTSet12[ARTL_ART_ART_CSP_x] = true;
  ARTSet12[ARTL_ART_ART_CSP_y] = true;
  ARTSet12[ARTL_ART_ART_CSP_z] = true;
}

public void ARTSet15initialise() {
  ARTSet15 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet15, 0, artSetExtent, false);
  ARTSet15[ARTTC__CR] = true;
}

public void ARTSet2initialise() {
  ARTSet2 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
  ARTSet2[ARTTC_x] = true;
  ARTSet2[ARTTC_y] = true;
  ARTSet2[ARTTC_z] = true;
  ARTSet2[ARTL_ART_ART_CSP_x] = true;
  ARTSet2[ARTL_ART_ART_CSP_y] = true;
  ARTSet2[ARTL_ART_ART_CSP_z] = true;
}

public void ARTSet11initialise() {
  ARTSet11 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet11, 0, artSetExtent, false);
  ARTSet11[ARTTC__HT] = true;
  ARTSet11[ARTTC__LF] = true;
  ARTSet11[ARTTC__CR] = true;
  ARTSet11[ARTTC__SPACE] = true;
}

public void ARTSet5initialise() {
  ARTSet5 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
  ARTSet5[ARTTC_x] = true;
  ARTSet5[ARTTC_y] = true;
  ARTSet5[ARTTC_z] = true;
  ARTSet5[ARTL_ART_ART_CSP_x] = true;
  ARTSet5[ARTL_ART_ART_CSP_y] = true;
  ARTSet5[ARTL_ART_ART_CSP_z] = true;
}

public void ARTSet6initialise() {
  ARTSet6 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
  ARTSet6[ARTTC_x] = true;
  ARTSet6[ARTL_ART_ART_CSP_x] = true;
}

public void ARTSet7initialise() {
  ARTSet7 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
  ARTSet7[ARTTC_y] = true;
  ARTSet7[ARTL_ART_ART_CSP_y] = true;
}

public void ARTSet8initialise() {
  ARTSet8 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
  ARTSet8[ARTTC_z] = true;
  ARTSet8[ARTL_ART_ART_CSP_z] = true;
}

public void ARTSet13initialise() {
  ARTSet13 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet13, 0, artSetExtent, false);
  ARTSet13[ARTTC__HT] = true;
  ARTSet13[ARTTC__LF] = true;
  ARTSet13[ARTTC__CR] = true;
  ARTSet13[ARTTC__SPACE] = true;
}

public void ARTSet4initialise() {
  ARTSet4 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
  ARTSet4[ARTX_EOS] = true;
  ARTSet4[ARTTC_x] = true;
  ARTSet4[ARTTC_y] = true;
  ARTSet4[ARTTC_z] = true;
  ARTSet4[ARTL_ART_ART_CSP_x] = true;
  ARTSet4[ARTL_ART_ART_CSP_y] = true;
  ARTSet4[ARTL_ART_ART_CSP_z] = true;
}

public void ARTSet10initialise() {
  ARTSet10 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet10, 0, artSetExtent, false);
  ARTSet10[ARTTC_x] = true;
}

public void ARTSet18initialise() {
  ARTSet18 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet18, 0, artSetExtent, false);
  ARTSet18[ARTTC_y] = true;
}

public void ARTSet19initialise() {
  ARTSet19 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet19, 0, artSetExtent, false);
  ARTSet19[ARTTC_z] = true;
}

public void ARTSet9initialise() {
  ARTSet9 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
}

public void ARTSet17initialise() {
  ARTSet17 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet17, 0, artSetExtent, false);
  ARTSet17[ARTTC__SPACE] = true;
}

public void artSetInitialise() {
  ARTSet1initialise();
  ARTSet3initialise();
  ARTSet16initialise();
  ARTSet14initialise();
  ARTSet12initialise();
  ARTSet15initialise();
  ARTSet2initialise();
  ARTSet11initialise();
  ARTSet5initialise();
  ARTSet6initialise();
  ARTSet7initialise();
  ARTSet8initialise();
  ARTSet13initialise();
  ARTSet4initialise();
  ARTSet10initialise();
  ARTSet18initialise();
  ARTSet19initialise();
  ARTSet9initialise();
  ARTSet17initialise();
}

public void artTableInitialiser_ART_ARTLexerStart() {
  artLabelInternalStrings[ARTL_ART_ARTLexerStart] = "ARTLexerStart";
  artLabelStrings[ARTL_ART_ARTLexerStart] = "ARTLexerStart";
  artKindOfs[ARTL_ART_ARTLexerStart] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_2] = "ARTLexerStart ::= . ( ART_CSP_x | ART_CSP_y | ART_CSP_z )* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_2] = "";
  artlhsL[ARTL_ART_ARTLexerStart_2] = ARTL_ART_ARTLexerStart;
  artKindOfs[ARTL_ART_ARTLexerStart_2] = ARTK_INTERMEDIATE;
  artlhsL[ARTC_ART_ARTLexerStart_3] = ARTL_ART_ARTLexerStart;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_4] = "ARTLexerStart ::= ( ART_CSP_x | ART_CSP_y | ART_CSP_z )* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_4] = "";
  artlhsL[ARTA_ART_ARTLexerStart_4] = ARTL_ART_ARTLexerStart;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_6] = "ARTLexerStart ::= ( . ART_CSP_x | ART_CSP_y | ART_CSP_z )* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_6] = "";
  artlhsL[ARTL_ART_ARTLexerStart_6] = ARTL_ART_ARTLexerStart;
  artKindOfs[ARTL_ART_ARTLexerStart_6] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_8] = "ARTLexerStart ::= ( ART_CSP_x .| ART_CSP_y | ART_CSP_z )* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_8] = "";
  artlhsL[ARTL_ART_ARTLexerStart_8] = ARTL_ART_ARTLexerStart;
  artSlotInstanceOfs[ARTL_ART_ARTLexerStart_8] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ARTLexerStart_8] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ARTLexerStart_8] = ARTL_ART_ARTLexerStart_16;
  artaL[ARTL_ART_ARTLexerStart_8] = ARTL_ART_ARTLexerStart_16;
  artPopD[ARTL_ART_ARTLexerStart_8] = true;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_10] = "ARTLexerStart ::= ( ART_CSP_x | . ART_CSP_y | ART_CSP_z )* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_10] = "";
  artlhsL[ARTL_ART_ARTLexerStart_10] = ARTL_ART_ARTLexerStart;
  artKindOfs[ARTL_ART_ARTLexerStart_10] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_12] = "ARTLexerStart ::= ( ART_CSP_x | ART_CSP_y .| ART_CSP_z )* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_12] = "";
  artlhsL[ARTL_ART_ARTLexerStart_12] = ARTL_ART_ARTLexerStart;
  artSlotInstanceOfs[ARTL_ART_ARTLexerStart_12] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ARTLexerStart_12] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ARTLexerStart_12] = ARTL_ART_ARTLexerStart_16;
  artaL[ARTL_ART_ARTLexerStart_12] = ARTL_ART_ARTLexerStart_16;
  artPopD[ARTL_ART_ARTLexerStart_12] = true;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_14] = "ARTLexerStart ::= ( ART_CSP_x | ART_CSP_y | . ART_CSP_z )* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_14] = "";
  artlhsL[ARTL_ART_ARTLexerStart_14] = ARTL_ART_ARTLexerStart;
  artKindOfs[ARTL_ART_ARTLexerStart_14] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_16] = "ARTLexerStart ::= ( ART_CSP_x | ART_CSP_y | ART_CSP_z .)* ";
  artLabelStrings[ARTL_ART_ARTLexerStart_16] = "";
  artlhsL[ARTL_ART_ARTLexerStart_16] = ARTL_ART_ARTLexerStart;
  artSlotInstanceOfs[ARTL_ART_ARTLexerStart_16] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ARTLexerStart_16] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ARTLexerStart_16] = true;
  artLabelInternalStrings[ARTL_ART_ARTLexerStart_17] = "ARTLexerStart ::= ( ART_CSP_x | ART_CSP_y | ART_CSP_z )* .";
  artLabelStrings[ARTL_ART_ARTLexerStart_17] = "";
  artlhsL[ARTL_ART_ARTLexerStart_17] = ARTL_ART_ARTLexerStart;
  artKindOfs[ARTL_ART_ARTLexerStart_17] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_ARTLexerStart_17] = true;
  arteoR_pL[ARTL_ART_ARTLexerStart_17] = true;
  artPopD[ARTL_ART_ARTLexerStart_17] = true;
}

public void artTableInitialiser_ART_ART_CSP_x() {
  artLabelInternalStrings[ARTL_ART_ART_CSP_x] = "ART_CSP_x";
  artLabelStrings[ARTL_ART_ART_CSP_x] = "x";
  artKindOfs[ARTL_ART_ART_CSP_x] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_19] = "ART_CSP_x ::= . `x  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_19] = "";
  artlhsL[ARTL_ART_ART_CSP_x_19] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_19] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_20] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_20] = "";
  artlhsL[ARTL_ART_ART_CSP_x_20] = ARTL_ART_ART_CSP_x;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_21] = "ART_CSP_x ::= `x  . ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_21] = "";
  artlhsL[ARTL_ART_ART_CSP_x_21] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_21] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_ART_CSP_x_21] = true;
  artlhsL[ARTC_ART_ART_CSP_x_22] = ARTL_ART_ART_CSP_x;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_23] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_23] = "";
  artlhsL[ARTA_ART_ART_CSP_x_23] = ARTL_ART_ART_CSP_x;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_25] = "ART_CSP_x ::= `x  ( . `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_25] = "";
  artlhsL[ARTL_ART_ART_CSP_x_25] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_25] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_x_25] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_26] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_26] = "";
  artlhsL[ARTL_ART_ART_CSP_x_26] = ARTL_ART_ART_CSP_x;
  artPopD[ARTL_ART_ART_CSP_x_26] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_27] = "ART_CSP_x ::= `x  ( `\\n  .| `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_27] = "";
  artlhsL[ARTL_ART_ART_CSP_x_27] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_27] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_x_27] = ARTL_ART_ART_CSP_x_39;
  artaL[ARTL_ART_ART_CSP_x_27] = ARTL_ART_ART_CSP_x_39;
  artPopD[ARTL_ART_ART_CSP_x_27] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_29] = "ART_CSP_x ::= `x  ( `\\n  | . `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_29] = "";
  artlhsL[ARTL_ART_ART_CSP_x_29] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_29] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_x_29] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_30] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_30] = "";
  artlhsL[ARTL_ART_ART_CSP_x_30] = ARTL_ART_ART_CSP_x;
  artPopD[ARTL_ART_ART_CSP_x_30] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_31] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  .| `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_31] = "";
  artlhsL[ARTL_ART_ART_CSP_x_31] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_31] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_x_31] = ARTL_ART_ART_CSP_x_39;
  artaL[ARTL_ART_ART_CSP_x_31] = ARTL_ART_ART_CSP_x_39;
  artPopD[ARTL_ART_ART_CSP_x_31] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_33] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | . `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_33] = "";
  artlhsL[ARTL_ART_ART_CSP_x_33] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_33] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_x_33] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_34] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_34] = "";
  artlhsL[ARTL_ART_ART_CSP_x_34] = ARTL_ART_ART_CSP_x;
  artPopD[ARTL_ART_ART_CSP_x_34] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_35] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  .| `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_35] = "";
  artlhsL[ARTL_ART_ART_CSP_x_35] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_35] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_x_35] = ARTL_ART_ART_CSP_x_39;
  artaL[ARTL_ART_ART_CSP_x_35] = ARTL_ART_ART_CSP_x_39;
  artPopD[ARTL_ART_ART_CSP_x_35] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_37] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | . `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_37] = "";
  artlhsL[ARTL_ART_ART_CSP_x_37] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_37] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_x_37] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_38] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_38] = "";
  artlhsL[ARTL_ART_ART_CSP_x_38] = ARTL_ART_ART_CSP_x;
  artPopD[ARTL_ART_ART_CSP_x_38] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_39] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   .)* ";
  artLabelStrings[ARTL_ART_ART_CSP_x_39] = "";
  artlhsL[ARTL_ART_ART_CSP_x_39] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_39] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_x_39] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_x_40] = "ART_CSP_x ::= `x  ( `\\n  | `\\r  | `\\t  | `   )* .";
  artLabelStrings[ARTL_ART_ART_CSP_x_40] = "";
  artlhsL[ARTL_ART_ART_CSP_x_40] = ARTL_ART_ART_CSP_x;
  artKindOfs[ARTL_ART_ART_CSP_x_40] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_ART_CSP_x_40] = true;
  arteoR_pL[ARTL_ART_ART_CSP_x_40] = true;
  artPopD[ARTL_ART_ART_CSP_x_40] = true;
}

public void artTableInitialiser_ART_ART_CSP_y() {
  artLabelInternalStrings[ARTL_ART_ART_CSP_y] = "ART_CSP_y";
  artLabelStrings[ARTL_ART_ART_CSP_y] = "y";
  artKindOfs[ARTL_ART_ART_CSP_y] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_42] = "ART_CSP_y ::= . `y  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_42] = "";
  artlhsL[ARTL_ART_ART_CSP_y_42] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_42] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_43] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_43] = "";
  artlhsL[ARTL_ART_ART_CSP_y_43] = ARTL_ART_ART_CSP_y;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_44] = "ART_CSP_y ::= `y  . ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_44] = "";
  artlhsL[ARTL_ART_ART_CSP_y_44] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_44] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_ART_CSP_y_44] = true;
  artlhsL[ARTC_ART_ART_CSP_y_45] = ARTL_ART_ART_CSP_y;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_46] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_46] = "";
  artlhsL[ARTA_ART_ART_CSP_y_46] = ARTL_ART_ART_CSP_y;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_48] = "ART_CSP_y ::= `y  ( . `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_48] = "";
  artlhsL[ARTL_ART_ART_CSP_y_48] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_48] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_y_48] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_49] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_49] = "";
  artlhsL[ARTL_ART_ART_CSP_y_49] = ARTL_ART_ART_CSP_y;
  artPopD[ARTL_ART_ART_CSP_y_49] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_50] = "ART_CSP_y ::= `y  ( `\\n  .| `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_50] = "";
  artlhsL[ARTL_ART_ART_CSP_y_50] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_50] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_y_50] = ARTL_ART_ART_CSP_y_62;
  artaL[ARTL_ART_ART_CSP_y_50] = ARTL_ART_ART_CSP_y_62;
  artPopD[ARTL_ART_ART_CSP_y_50] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_52] = "ART_CSP_y ::= `y  ( `\\n  | . `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_52] = "";
  artlhsL[ARTL_ART_ART_CSP_y_52] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_52] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_y_52] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_53] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_53] = "";
  artlhsL[ARTL_ART_ART_CSP_y_53] = ARTL_ART_ART_CSP_y;
  artPopD[ARTL_ART_ART_CSP_y_53] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_54] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  .| `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_54] = "";
  artlhsL[ARTL_ART_ART_CSP_y_54] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_54] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_y_54] = ARTL_ART_ART_CSP_y_62;
  artaL[ARTL_ART_ART_CSP_y_54] = ARTL_ART_ART_CSP_y_62;
  artPopD[ARTL_ART_ART_CSP_y_54] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_56] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | . `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_56] = "";
  artlhsL[ARTL_ART_ART_CSP_y_56] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_56] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_y_56] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_57] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_57] = "";
  artlhsL[ARTL_ART_ART_CSP_y_57] = ARTL_ART_ART_CSP_y;
  artPopD[ARTL_ART_ART_CSP_y_57] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_58] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  .| `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_58] = "";
  artlhsL[ARTL_ART_ART_CSP_y_58] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_58] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_y_58] = ARTL_ART_ART_CSP_y_62;
  artaL[ARTL_ART_ART_CSP_y_58] = ARTL_ART_ART_CSP_y_62;
  artPopD[ARTL_ART_ART_CSP_y_58] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_60] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | . `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_60] = "";
  artlhsL[ARTL_ART_ART_CSP_y_60] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_60] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_y_60] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_61] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_61] = "";
  artlhsL[ARTL_ART_ART_CSP_y_61] = ARTL_ART_ART_CSP_y;
  artPopD[ARTL_ART_ART_CSP_y_61] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_62] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   .)* ";
  artLabelStrings[ARTL_ART_ART_CSP_y_62] = "";
  artlhsL[ARTL_ART_ART_CSP_y_62] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_62] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_y_62] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_y_63] = "ART_CSP_y ::= `y  ( `\\n  | `\\r  | `\\t  | `   )* .";
  artLabelStrings[ARTL_ART_ART_CSP_y_63] = "";
  artlhsL[ARTL_ART_ART_CSP_y_63] = ARTL_ART_ART_CSP_y;
  artKindOfs[ARTL_ART_ART_CSP_y_63] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_ART_CSP_y_63] = true;
  arteoR_pL[ARTL_ART_ART_CSP_y_63] = true;
  artPopD[ARTL_ART_ART_CSP_y_63] = true;
}

public void artTableInitialiser_ART_ART_CSP_z() {
  artLabelInternalStrings[ARTL_ART_ART_CSP_z] = "ART_CSP_z";
  artLabelStrings[ARTL_ART_ART_CSP_z] = "z";
  artKindOfs[ARTL_ART_ART_CSP_z] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_65] = "ART_CSP_z ::= . `z  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_65] = "";
  artlhsL[ARTL_ART_ART_CSP_z_65] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_65] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_66] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_66] = "";
  artlhsL[ARTL_ART_ART_CSP_z_66] = ARTL_ART_ART_CSP_z;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_67] = "ART_CSP_z ::= `z  . ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_67] = "";
  artlhsL[ARTL_ART_ART_CSP_z_67] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_67] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_ART_CSP_z_67] = true;
  artlhsL[ARTC_ART_ART_CSP_z_68] = ARTL_ART_ART_CSP_z;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_69] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_69] = "";
  artlhsL[ARTA_ART_ART_CSP_z_69] = ARTL_ART_ART_CSP_z;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_71] = "ART_CSP_z ::= `z  ( . `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_71] = "";
  artlhsL[ARTL_ART_ART_CSP_z_71] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_71] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_z_71] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_72] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_72] = "";
  artlhsL[ARTL_ART_ART_CSP_z_72] = ARTL_ART_ART_CSP_z;
  artPopD[ARTL_ART_ART_CSP_z_72] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_73] = "ART_CSP_z ::= `z  ( `\\n  .| `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_73] = "";
  artlhsL[ARTL_ART_ART_CSP_z_73] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_73] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_z_73] = ARTL_ART_ART_CSP_z_85;
  artaL[ARTL_ART_ART_CSP_z_73] = ARTL_ART_ART_CSP_z_85;
  artPopD[ARTL_ART_ART_CSP_z_73] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_75] = "ART_CSP_z ::= `z  ( `\\n  | . `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_75] = "";
  artlhsL[ARTL_ART_ART_CSP_z_75] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_75] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_z_75] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_76] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_76] = "";
  artlhsL[ARTL_ART_ART_CSP_z_76] = ARTL_ART_ART_CSP_z;
  artPopD[ARTL_ART_ART_CSP_z_76] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_77] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  .| `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_77] = "";
  artlhsL[ARTL_ART_ART_CSP_z_77] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_77] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_z_77] = ARTL_ART_ART_CSP_z_85;
  artaL[ARTL_ART_ART_CSP_z_77] = ARTL_ART_ART_CSP_z_85;
  artPopD[ARTL_ART_ART_CSP_z_77] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_79] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | . `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_79] = "";
  artlhsL[ARTL_ART_ART_CSP_z_79] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_79] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_z_79] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_80] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_80] = "";
  artlhsL[ARTL_ART_ART_CSP_z_80] = ARTL_ART_ART_CSP_z;
  artPopD[ARTL_ART_ART_CSP_z_80] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_81] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  .| `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_81] = "";
  artlhsL[ARTL_ART_ART_CSP_z_81] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_81] = ARTK_INTERMEDIATE;
  artpL[ARTL_ART_ART_CSP_z_81] = ARTL_ART_ART_CSP_z_85;
  artaL[ARTL_ART_ART_CSP_z_81] = ARTL_ART_ART_CSP_z_85;
  artPopD[ARTL_ART_ART_CSP_z_81] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_83] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | . `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_83] = "";
  artlhsL[ARTL_ART_ART_CSP_z_83] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_83] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_z_83] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_84] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   )* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_84] = "";
  artlhsL[ARTL_ART_ART_CSP_z_84] = ARTL_ART_ART_CSP_z;
  artPopD[ARTL_ART_ART_CSP_z_84] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_85] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   .)* ";
  artLabelStrings[ARTL_ART_ART_CSP_z_85] = "";
  artlhsL[ARTL_ART_ART_CSP_z_85] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_85] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ART_CSP_z_85] = true;
  artLabelInternalStrings[ARTL_ART_ART_CSP_z_86] = "ART_CSP_z ::= `z  ( `\\n  | `\\r  | `\\t  | `   )* .";
  artLabelStrings[ARTL_ART_ART_CSP_z_86] = "";
  artlhsL[ARTL_ART_ART_CSP_z_86] = ARTL_ART_ART_CSP_z;
  artKindOfs[ARTL_ART_ART_CSP_z_86] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_ART_CSP_z_86] = true;
  arteoR_pL[ARTL_ART_ART_CSP_z_86] = true;
  artPopD[ARTL_ART_ART_CSP_z_86] = true;
}

public void artTableInitialiser_ART_S() {
  artLabelInternalStrings[ARTL_ART_S] = "S";
  artLabelStrings[ARTL_ART_S] = "S";
  artKindOfs[ARTL_ART_S] = ARTK_NONTERMINAL;
}

public void artTableInitialiser_ART_Y() {
  artLabelInternalStrings[ARTL_ART_Y] = "Y";
  artLabelStrings[ARTL_ART_Y] = "Y";
  artKindOfs[ARTL_ART_Y] = ARTK_NONTERMINAL;
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
  artLabelStrings[ARTTC_x] = "x";
  artLabelInternalStrings[ARTTC_x] = "`x";
  artKindOfs[ARTTC_x] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC_y] = "y";
  artLabelInternalStrings[ARTTC_y] = "`y";
  artKindOfs[ARTTC_y] = ARTK_CHARACTER_TERMINAL;
  artLabelStrings[ARTTC_z] = "z";
  artLabelInternalStrings[ARTTC_z] = "`z";
  artKindOfs[ARTTC_z] = ARTK_CHARACTER_TERMINAL;
  artTableInitialiser_ART_ARTLexerStart();
  artTableInitialiser_ART_ART_CSP_x();
  artTableInitialiser_ART_ART_CSP_y();
  artTableInitialiser_ART_ART_CSP_z();
  artTableInitialiser_ART_S();
  artTableInitialiser_ART_Y();
}

public ARTGeneratedParser(ARTLexerV3 artLexer) {
  this(null, artLexer);
}

public ARTGeneratedParser(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
  super(artGrammar, artLexer);
  artParserKind = "GLLTWE Gen";
  artFirstTerminalLabel = ARTTC__HT;
  artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
  artSetExtent = 15;
  ARTL_EOS = ARTX_EOS;
  ARTL_EPSILON = ARTX_EPSILON;
  ARTL_DUMMY = ARTX_DUMMY;
  artGrammarKind = ARTModeGrammarKind.EBNF;
  artDefaultStartSymbolLabel = ARTL_ART_ARTLexerStart;
  artBuildDirectives = "ARTDirectives [inputs=[], inputFilenames=[], directives={suppressPopGuard=false, tweLexicalisations=false, algorithmMode=gllTWEGeneratorPool, tweLongest=false, tweSegments=false, sppfShortest=false, termWrite=false, tweCounts=false, clusteredGSS=false, twePrint=false, rewriteDisable=false, tweAmbiguityClasses=false, sppfAmbiguityAnalysis=false, rewriteConfiguration=false, outputDirectory=., inputCounts=false, twePriority=false, treeShow=false, tweRecursive=false, rewritePostorder=false, rewriteContractum=true, parseCounts=false, predictivePops=false, suppressProductionGuard=false, sppfDead=false, twePrintFull=false, input=0, tweExtents=false, suppressSemantics=false, despatchMode=fragment, treePrintLevel=3, sppfShowFull=false, treePrint=false, sppfChooseCounts=false, log=1, tweDump=false, sppfCycleDetect=false, sppfCountSentences=false, parserName=ARTGeneratedParser, rewriteResume=true, inputPrint=false, lexerName=ARTGeneratedLexer, trace=false, tweTokenWrite=false, tweDead=false, tweShortest=false, rewritePure=true, tweSelectOne=false, smlCycleBreak=false, termPrint=false, suppressTestRepeat=false, rewritePreorder=false, sppfAmbiguityAnalysisFull=false, tweFromSPPF=false, actionSuppress=false, tweLexicalisationsQuick=false, sppfPriority=false, sppfShow=false, rewriteOneStep=false, namespace=, sppfSelectOne=false, FIFODescriptors=false, sppfOrderedLongest=false, verbosity=0, sppfLongest=false, gssShow=false}]";
  artFIFODescriptors = false;
  artSetInitialise();
  artTableInitialise();
}

};
