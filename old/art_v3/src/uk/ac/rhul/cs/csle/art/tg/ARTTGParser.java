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
* ARTTGParser.java
*
*******************************************************************************/
import java.io.File; import java.io.FileNotFoundException; import java.io.PrintWriter;
public class ARTTGParser extends ARTGLLParserHashPool {
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
  private static boolean[] ARTSet20;

  /* Start of artLabel enumeration */
  public static final int ARTX_EOS = 0;
  public static final int ARTTB_ID = 1;
  public static final int ARTTB_STRING_BRACE = 2;
  public static final int ARTTB_STRING_DOLLAR = 3;
  public static final int ARTTS_accept = 4;
  public static final int ARTTS_reject = 5;
  public static final int ARTX_EPSILON = 6;
  public static final int ARTTN_ART_TFN_ARTTG_COMMENT_NEST_ART = 7;
  public static final int ARTTN_ART_TFN_ARTTG_WHITESPACE = 8;
  public static final int ARTTB_COMMENT_NEST_ART = 9;
  public static final int ARTTB_WHITESPACE = 10;
  public static final int ARTL_ARTTG_ID = 11;
  public static final int ARTL_ARTTG_acceptOpt = 12;
  public static final int ARTL_ARTTG_optionOpt = 13;
  public static final int ARTL_ARTTG_rejectOpt = 14;
  public static final int ARTL_ARTTG_stringBrace = 15;
  public static final int ARTL_ARTTG_stringDollar = 16;
  public static final int ARTL_ARTTG_strings = 17;
  public static final int ARTL_ARTTG_test = 18;
  public static final int ARTL_ARTTG_tests = 19;
  public static final int ARTL_ARTTG_text = 20;
  public static final int ARTL_ARTTG_ID_86 = 21;
  public static final int ARTL_ARTTG_ID_88 = 22;
  public static final int ARTL_ARTTG_ID_87 = 23;
  public static final int ARTL_ARTTG_acceptOpt_44 = 24;
  public static final int ARTL_ARTTG_acceptOpt_50 = 25;
  public static final int ARTL_ARTTG_acceptOpt_45 = 26;
  public static final int ARTL_ARTTG_acceptOpt_46 = 27;
  public static final int ARTL_ARTTG_acceptOpt_52 = 28;
  public static final int ARTL_ARTTG_acceptOpt_54 = 29;
  public static final int ARTL_ARTTG_optionOpt_34 = 30;
  public static final int ARTL_ARTTG_optionOpt_36 = 31;
  public static final int ARTL_ARTTG_optionOpt_40 = 32;
  public static final int ARTL_ARTTG_optionOpt_42 = 33;
  public static final int ARTL_ARTTG_rejectOpt_56 = 34;
  public static final int ARTL_ARTTG_rejectOpt_62 = 35;
  public static final int ARTL_ARTTG_rejectOpt_57 = 36;
  public static final int ARTL_ARTTG_rejectOpt_58 = 37;
  public static final int ARTL_ARTTG_rejectOpt_64 = 38;
  public static final int ARTL_ARTTG_rejectOpt_66 = 39;
  public static final int ARTL_ARTTG_stringBrace_98 = 40;
  public static final int ARTL_ARTTG_stringBrace_100 = 41;
  public static final int ARTL_ARTTG_stringBrace_99 = 42;
  public static final int ARTL_ARTTG_stringDollar_92 = 43;
  public static final int ARTL_ARTTG_stringDollar_94 = 44;
  public static final int ARTL_ARTTG_stringDollar_93 = 45;
  public static final int ARTL_ARTTG_strings_68 = 46;
  public static final int ARTL_ARTTG_strings_70 = 47;
  public static final int ARTL_ARTTG_strings_72 = 48;
  public static final int ARTL_ARTTG_strings_74 = 49;
  public static final int ARTL_ARTTG_strings_78 = 50;
  public static final int ARTL_ARTTG_strings_80 = 51;
  public static final int ARTL_ARTTG_strings_82 = 52;
  public static final int ARTL_ARTTG_test_18 = 53;
  public static final int ARTL_ARTTG_test_20 = 54;
  public static final int ARTL_ARTTG_test_22 = 55;
  public static final int ARTL_ARTTG_test_24 = 56;
  public static final int ARTL_ARTTG_test_28 = 57;
  public static final int ARTL_ARTTG_test_32 = 58;
  public static final int ARTL_ARTTG_tests_8 = 59;
  public static final int ARTL_ARTTG_tests_10 = 60;
  public static final int ARTL_ARTTG_tests_12 = 61;
  public static final int ARTL_ARTTG_tests_14 = 62;
  public static final int ARTL_ARTTG_tests_16 = 63;
  public static final int ARTL_ARTTG_text_2 = 64;
  public static final int ARTL_ARTTG_text_6 = 65;
  public static final int ARTX_DESPATCH = 66;
  public static final int ARTX_DUMMY = 67;
  public static final int ARTX_LABEL_EXTENT = 68;
  /* End of artLabel enumeration */

  /* Start of artName enumeration */
  public static final int ARTNAME_NONE = 0;
  public static final int ARTNAME_EXTENT = 1;
  /* End of artName enumeration */
  public void ARTPF_ARTTG_ID() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.ID production descriptor loads*/
      case ARTL_ARTTG_ID: 
        if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_ID_86, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.ID: match production*/
      case ARTL_ARTTG_ID_86: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_ID_88, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_acceptOpt() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.acceptOpt production descriptor loads*/
      case ARTL_ARTTG_acceptOpt: 
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_acceptOpt_44, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_acceptOpt_52, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.acceptOpt: match production*/
      case ARTL_ARTTG_acceptOpt_44: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_accept, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_acceptOpt_46, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_acceptOpt_50, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_strings; return; }
      case ARTL_ARTTG_acceptOpt_50: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal ARTTG.acceptOpt: match production*/
      case ARTL_ARTTG_acceptOpt_52: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_acceptOpt_54, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_optionOpt() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.optionOpt production descriptor loads*/
      case ARTL_ARTTG_optionOpt: 
        if (ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_optionOpt_34, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_optionOpt_40, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.optionOpt: match production*/
      case ARTL_ARTTG_optionOpt_34: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_optionOpt_36, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_stringBrace; return; }
      case ARTL_ARTTG_optionOpt_36: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal ARTTG.optionOpt: match production*/
      case ARTL_ARTTG_optionOpt_40: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_optionOpt_42, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_rejectOpt() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.rejectOpt production descriptor loads*/
      case ARTL_ARTTG_rejectOpt: 
        if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_rejectOpt_56, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_rejectOpt_64, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.rejectOpt: match production*/
      case ARTL_ARTTG_rejectOpt_56: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_reject, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_rejectOpt_58, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_rejectOpt_62, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_strings; return; }
      case ARTL_ARTTG_rejectOpt_62: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal ARTTG.rejectOpt: match production*/
      case ARTL_ARTTG_rejectOpt_64: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_rejectOpt_66, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_stringBrace() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.stringBrace production descriptor loads*/
      case ARTL_ARTTG_stringBrace: 
        if (ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_stringBrace_98, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.stringBrace: match production*/
      case ARTL_ARTTG_stringBrace_98: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_BRACE, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_stringBrace_100, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_stringDollar() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.stringDollar production descriptor loads*/
      case ARTL_ARTTG_stringDollar: 
        if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_stringDollar_92, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.stringDollar: match production*/
      case ARTL_ARTTG_stringDollar_92: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_DOLLAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_stringDollar_94, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_strings() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.strings production descriptor loads*/
      case ARTL_ARTTG_strings: 
        if (ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_strings_68, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_strings_72, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_strings_80, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.strings: match production*/
      case ARTL_ARTTG_strings_68: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_ARTTG_strings_70, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal ARTTG.strings: match production*/
      case ARTL_ARTTG_strings_72: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_strings_74, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_stringDollar; return; }
      case ARTL_ARTTG_strings_74: 
        /* Nonterminal template end */
        if (!ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_strings_78, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_strings; return; }
      case ARTL_ARTTG_strings_78: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal ARTTG.strings: match production*/
      case ARTL_ARTTG_strings_80: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_strings_82, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_stringDollar; return; }
      case ARTL_ARTTG_strings_82: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_test() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.test production descriptor loads*/
      case ARTL_ARTTG_test: 
        if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_test_18, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.test: match production*/
      case ARTL_ARTTG_test_18: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_test_20, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_ID; return; }
      case ARTL_ARTTG_test_20: 
        /* Nonterminal template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_test_22, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_optionOpt; return; }
      case ARTL_ARTTG_test_22: 
        /* Nonterminal template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_test_24, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_stringDollar; return; }
      case ARTL_ARTTG_test_24: 
        /* Nonterminal template end */
        if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_test_28, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_acceptOpt; return; }
      case ARTL_ARTTG_test_28: 
        /* Nonterminal template end */
        if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_test_32, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_rejectOpt; return; }
      case ARTL_ARTTG_test_32: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_tests() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.tests production descriptor loads*/
      case ARTL_ARTTG_tests: 
        if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_tests_8, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_tests_14, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.tests: match production*/
      case ARTL_ARTTG_tests_8: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_tests_10, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_test; return; }
      case ARTL_ARTTG_tests_10: 
        /* Nonterminal template end */
        if (!ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_tests_12, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_tests; return; }
      case ARTL_ARTTG_tests_12: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal ARTTG.tests: match production*/
      case ARTL_ARTTG_tests_14: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_tests_16, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_test; return; }
      case ARTL_ARTTG_tests_16: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_ARTTG_text() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal ARTTG.text production descriptor loads*/
      case ARTL_ARTTG_text: 
        if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_ARTTG_text_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal ARTTG.text: match production*/
      case ARTL_ARTTG_text_2: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_ARTTG_text_6, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_ARTTG_tests; return; }
      case ARTL_ARTTG_text_6: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
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
        case ARTL_ARTTG_ID: 
          ARTPF_ARTTG_ID();
          break;
        case ARTL_ARTTG_acceptOpt: 
          ARTPF_ARTTG_acceptOpt();
          break;
        case ARTL_ARTTG_optionOpt: 
          ARTPF_ARTTG_optionOpt();
          break;
        case ARTL_ARTTG_rejectOpt: 
          ARTPF_ARTTG_rejectOpt();
          break;
        case ARTL_ARTTG_stringBrace: 
          ARTPF_ARTTG_stringBrace();
          break;
        case ARTL_ARTTG_stringDollar: 
          ARTPF_ARTTG_stringDollar();
          break;
        case ARTL_ARTTG_strings: 
          ARTPF_ARTTG_strings();
          break;
        case ARTL_ARTTG_test: 
          ARTPF_ARTTG_test();
          break;
        case ARTL_ARTTG_tests: 
          ARTPF_ARTTG_tests();
          break;
        case ARTL_ARTTG_text: 
          ARTPF_ARTTG_text();
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

  public void ARTSet7initialise() {
    ARTSet7 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
    ARTSet7[ARTTS_accept] = true;
  }

  public void ARTSet20initialise() {
    ARTSet20 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet20, 0, artSetExtent, false);
    ARTSet20[ARTX_EOS] = true;
  }

  public void ARTSet12initialise() {
    ARTSet12 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet12, 0, artSetExtent, false);
    ARTSet12[ARTTB_STRING_BRACE] = true;
  }

  public void ARTSet3initialise() {
    ARTSet3 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
    ARTSet3[ARTTB_STRING_BRACE] = true;
    ARTSet3[ARTTB_STRING_DOLLAR] = true;
  }

  public void ARTSet19initialise() {
    ARTSet19 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet19, 0, artSetExtent, false);
    ARTSet19[ARTTS_accept] = true;
    ARTSet19[ARTTS_reject] = true;
  }

  public void ARTSet18initialise() {
    ARTSet18 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet18, 0, artSetExtent, false);
    ARTSet18[ARTX_EOS] = true;
    ARTSet18[ARTTB_ID] = true;
    ARTSet18[ARTTS_accept] = true;
    ARTSet18[ARTTS_reject] = true;
  }

  public void ARTSet17initialise() {
    ARTSet17 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet17, 0, artSetExtent, false);
    ARTSet17[ARTX_EOS] = true;
    ARTSet17[ARTTB_ID] = true;
    ARTSet17[ARTTB_STRING_DOLLAR] = true;
    ARTSet17[ARTTS_accept] = true;
    ARTSet17[ARTTS_reject] = true;
  }

  public void ARTSet15initialise() {
    ARTSet15 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet15, 0, artSetExtent, false);
    ARTSet15[ARTTS_reject] = true;
  }

  public void ARTSet11initialise() {
    ARTSet11 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet11, 0, artSetExtent, false);
    ARTSet11[ARTTB_STRING_DOLLAR] = true;
  }

  public void ARTSet2initialise() {
    ARTSet2 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
    ARTSet2[ARTTB_ID] = true;
  }

  public void ARTSet5initialise() {
    ARTSet5 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
    ARTSet5[ARTTS_accept] = true;
  }

  public void ARTSet8initialise() {
    ARTSet8 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
    ARTSet8[ARTTB_STRING_DOLLAR] = true;
  }

  public void ARTSet4initialise() {
    ARTSet4 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
  }

  public void ARTSet9initialise() {
    ARTSet9 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
    ARTSet9[ARTX_EOS] = true;
    ARTSet9[ARTTB_ID] = true;
    ARTSet9[ARTTB_STRING_DOLLAR] = true;
    ARTSet9[ARTTS_reject] = true;
  }

  public void ARTSet10initialise() {
    ARTSet10 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet10, 0, artSetExtent, false);
    ARTSet10[ARTTB_STRING_BRACE] = true;
  }

  public void ARTSet13initialise() {
    ARTSet13 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet13, 0, artSetExtent, false);
    ARTSet13[ARTTS_reject] = true;
  }

  public void ARTSet14initialise() {
    ARTSet14 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet14, 0, artSetExtent, false);
    ARTSet14[ARTX_EOS] = true;
    ARTSet14[ARTTB_ID] = true;
  }

  public void ARTSet6initialise() {
    ARTSet6 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
    ARTSet6[ARTX_EOS] = true;
    ARTSet6[ARTTB_ID] = true;
    ARTSet6[ARTTS_reject] = true;
  }

  public void ARTSet16initialise() {
    ARTSet16 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet16, 0, artSetExtent, false);
    ARTSet16[ARTX_EOS] = true;
    ARTSet16[ARTTB_ID] = true;
    ARTSet16[ARTTB_STRING_DOLLAR] = true;
  }

  public void artSetInitialise() {
    ARTSet1initialise();
    ARTSet7initialise();
    ARTSet20initialise();
    ARTSet12initialise();
    ARTSet3initialise();
    ARTSet19initialise();
    ARTSet18initialise();
    ARTSet17initialise();
    ARTSet15initialise();
    ARTSet11initialise();
    ARTSet2initialise();
    ARTSet5initialise();
    ARTSet8initialise();
    ARTSet4initialise();
    ARTSet9initialise();
    ARTSet10initialise();
    ARTSet13initialise();
    ARTSet14initialise();
    ARTSet6initialise();
    ARTSet16initialise();
  }

  public void artTableInitialiser_ARTTG_ID() {
    artLabelInternalStrings[ARTL_ARTTG_ID] = "ARTTG.ID";
    artLabelStrings[ARTL_ARTTG_ID] = "ARTTG.ID";
    artKindOfs[ARTL_ARTTG_ID] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_ID_86] = "ARTTG.ID ::= . &ID ";
    artLabelStrings[ARTL_ARTTG_ID_86] = "";
    artlhsL[ARTL_ARTTG_ID_86] = ARTL_ARTTG_ID;
    artKindOfs[ARTL_ARTTG_ID_86] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ARTTG_ID_86] = true;
    artLabelInternalStrings[ARTL_ARTTG_ID_87] = "ARTTG.ID ::= &ID ";
    artLabelStrings[ARTL_ARTTG_ID_87] = "";
    artlhsL[ARTL_ARTTG_ID_87] = ARTL_ARTTG_ID;
    artPopD[ARTL_ARTTG_ID_87] = true;
    artLabelInternalStrings[ARTL_ARTTG_ID_88] = "ARTTG.ID ::= &ID .";
    artLabelStrings[ARTL_ARTTG_ID_88] = "";
    artlhsL[ARTL_ARTTG_ID_88] = ARTL_ARTTG_ID;
    artKindOfs[ARTL_ARTTG_ID_88] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_ID_88] = true;
    arteoR_pL[ARTL_ARTTG_ID_88] = true;
    artPopD[ARTL_ARTTG_ID_88] = true;
  }

  public void artTableInitialiser_ARTTG_acceptOpt() {
    artLabelInternalStrings[ARTL_ARTTG_acceptOpt] = "ARTTG.acceptOpt";
    artLabelStrings[ARTL_ARTTG_acceptOpt] = "ARTTG.acceptOpt";
    artKindOfs[ARTL_ARTTG_acceptOpt] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_acceptOpt_44] = "ARTTG.acceptOpt ::= . 'accept' ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_acceptOpt_44] = "";
    artlhsL[ARTL_ARTTG_acceptOpt_44] = ARTL_ARTTG_acceptOpt;
    artKindOfs[ARTL_ARTTG_acceptOpt_44] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_acceptOpt_45] = "ARTTG.acceptOpt ::= 'accept' ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_acceptOpt_45] = "";
    artlhsL[ARTL_ARTTG_acceptOpt_45] = ARTL_ARTTG_acceptOpt;
    artLabelInternalStrings[ARTL_ARTTG_acceptOpt_46] = "ARTTG.acceptOpt ::= 'accept' . ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_acceptOpt_46] = "";
    artlhsL[ARTL_ARTTG_acceptOpt_46] = ARTL_ARTTG_acceptOpt;
    artKindOfs[ARTL_ARTTG_acceptOpt_46] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ARTTG_acceptOpt_46] = true;
    artLabelInternalStrings[ARTL_ARTTG_acceptOpt_50] = "ARTTG.acceptOpt ::= 'accept' ARTTG.strings .";
    artLabelStrings[ARTL_ARTTG_acceptOpt_50] = "";
    artlhsL[ARTL_ARTTG_acceptOpt_50] = ARTL_ARTTG_acceptOpt;
    artSlotInstanceOfs[ARTL_ARTTG_acceptOpt_50] = ARTL_ARTTG_strings;
    artKindOfs[ARTL_ARTTG_acceptOpt_50] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_acceptOpt_50] = true;
    arteoR_pL[ARTL_ARTTG_acceptOpt_50] = true;
    artPopD[ARTL_ARTTG_acceptOpt_50] = true;
    artLabelInternalStrings[ARTL_ARTTG_acceptOpt_52] = "ARTTG.acceptOpt ::= . # ";
    artLabelStrings[ARTL_ARTTG_acceptOpt_52] = "";
    artlhsL[ARTL_ARTTG_acceptOpt_52] = ARTL_ARTTG_acceptOpt;
    artKindOfs[ARTL_ARTTG_acceptOpt_52] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ARTTG_acceptOpt_52] = true;
    artLabelInternalStrings[ARTL_ARTTG_acceptOpt_54] = "ARTTG.acceptOpt ::= # .";
    artLabelStrings[ARTL_ARTTG_acceptOpt_54] = "";
    artlhsL[ARTL_ARTTG_acceptOpt_54] = ARTL_ARTTG_acceptOpt;
    artKindOfs[ARTL_ARTTG_acceptOpt_54] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_acceptOpt_54] = true;
    arteoR_pL[ARTL_ARTTG_acceptOpt_54] = true;
    artPopD[ARTL_ARTTG_acceptOpt_54] = true;
  }

  public void artTableInitialiser_ARTTG_optionOpt() {
    artLabelInternalStrings[ARTL_ARTTG_optionOpt] = "ARTTG.optionOpt";
    artLabelStrings[ARTL_ARTTG_optionOpt] = "ARTTG.optionOpt";
    artKindOfs[ARTL_ARTTG_optionOpt] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_optionOpt_34] = "ARTTG.optionOpt ::= . ARTTG.stringBrace ";
    artLabelStrings[ARTL_ARTTG_optionOpt_34] = "";
    artlhsL[ARTL_ARTTG_optionOpt_34] = ARTL_ARTTG_optionOpt;
    artKindOfs[ARTL_ARTTG_optionOpt_34] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_optionOpt_36] = "ARTTG.optionOpt ::= ARTTG.stringBrace .";
    artLabelStrings[ARTL_ARTTG_optionOpt_36] = "";
    artlhsL[ARTL_ARTTG_optionOpt_36] = ARTL_ARTTG_optionOpt;
    artSlotInstanceOfs[ARTL_ARTTG_optionOpt_36] = ARTL_ARTTG_stringBrace;
    artKindOfs[ARTL_ARTTG_optionOpt_36] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_optionOpt_36] = true;
    arteoR_pL[ARTL_ARTTG_optionOpt_36] = true;
    artPopD[ARTL_ARTTG_optionOpt_36] = true;
    artLabelInternalStrings[ARTL_ARTTG_optionOpt_40] = "ARTTG.optionOpt ::= . # ";
    artLabelStrings[ARTL_ARTTG_optionOpt_40] = "";
    artlhsL[ARTL_ARTTG_optionOpt_40] = ARTL_ARTTG_optionOpt;
    artKindOfs[ARTL_ARTTG_optionOpt_40] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ARTTG_optionOpt_40] = true;
    artLabelInternalStrings[ARTL_ARTTG_optionOpt_42] = "ARTTG.optionOpt ::= # .";
    artLabelStrings[ARTL_ARTTG_optionOpt_42] = "";
    artlhsL[ARTL_ARTTG_optionOpt_42] = ARTL_ARTTG_optionOpt;
    artKindOfs[ARTL_ARTTG_optionOpt_42] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_optionOpt_42] = true;
    arteoR_pL[ARTL_ARTTG_optionOpt_42] = true;
    artPopD[ARTL_ARTTG_optionOpt_42] = true;
  }

  public void artTableInitialiser_ARTTG_rejectOpt() {
    artLabelInternalStrings[ARTL_ARTTG_rejectOpt] = "ARTTG.rejectOpt";
    artLabelStrings[ARTL_ARTTG_rejectOpt] = "ARTTG.rejectOpt";
    artKindOfs[ARTL_ARTTG_rejectOpt] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_rejectOpt_56] = "ARTTG.rejectOpt ::= . 'reject' ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_rejectOpt_56] = "";
    artlhsL[ARTL_ARTTG_rejectOpt_56] = ARTL_ARTTG_rejectOpt;
    artKindOfs[ARTL_ARTTG_rejectOpt_56] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_rejectOpt_57] = "ARTTG.rejectOpt ::= 'reject' ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_rejectOpt_57] = "";
    artlhsL[ARTL_ARTTG_rejectOpt_57] = ARTL_ARTTG_rejectOpt;
    artLabelInternalStrings[ARTL_ARTTG_rejectOpt_58] = "ARTTG.rejectOpt ::= 'reject' . ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_rejectOpt_58] = "";
    artlhsL[ARTL_ARTTG_rejectOpt_58] = ARTL_ARTTG_rejectOpt;
    artKindOfs[ARTL_ARTTG_rejectOpt_58] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ARTTG_rejectOpt_58] = true;
    artLabelInternalStrings[ARTL_ARTTG_rejectOpt_62] = "ARTTG.rejectOpt ::= 'reject' ARTTG.strings .";
    artLabelStrings[ARTL_ARTTG_rejectOpt_62] = "";
    artlhsL[ARTL_ARTTG_rejectOpt_62] = ARTL_ARTTG_rejectOpt;
    artSlotInstanceOfs[ARTL_ARTTG_rejectOpt_62] = ARTL_ARTTG_strings;
    artKindOfs[ARTL_ARTTG_rejectOpt_62] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_rejectOpt_62] = true;
    arteoR_pL[ARTL_ARTTG_rejectOpt_62] = true;
    artPopD[ARTL_ARTTG_rejectOpt_62] = true;
    artLabelInternalStrings[ARTL_ARTTG_rejectOpt_64] = "ARTTG.rejectOpt ::= . # ";
    artLabelStrings[ARTL_ARTTG_rejectOpt_64] = "";
    artlhsL[ARTL_ARTTG_rejectOpt_64] = ARTL_ARTTG_rejectOpt;
    artKindOfs[ARTL_ARTTG_rejectOpt_64] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ARTTG_rejectOpt_64] = true;
    artLabelInternalStrings[ARTL_ARTTG_rejectOpt_66] = "ARTTG.rejectOpt ::= # .";
    artLabelStrings[ARTL_ARTTG_rejectOpt_66] = "";
    artlhsL[ARTL_ARTTG_rejectOpt_66] = ARTL_ARTTG_rejectOpt;
    artKindOfs[ARTL_ARTTG_rejectOpt_66] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_rejectOpt_66] = true;
    arteoR_pL[ARTL_ARTTG_rejectOpt_66] = true;
    artPopD[ARTL_ARTTG_rejectOpt_66] = true;
  }

  public void artTableInitialiser_ARTTG_stringBrace() {
    artLabelInternalStrings[ARTL_ARTTG_stringBrace] = "ARTTG.stringBrace";
    artLabelStrings[ARTL_ARTTG_stringBrace] = "ARTTG.stringBrace";
    artKindOfs[ARTL_ARTTG_stringBrace] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_stringBrace_98] = "ARTTG.stringBrace ::= . &STRING_BRACE ";
    artLabelStrings[ARTL_ARTTG_stringBrace_98] = "";
    artlhsL[ARTL_ARTTG_stringBrace_98] = ARTL_ARTTG_stringBrace;
    artKindOfs[ARTL_ARTTG_stringBrace_98] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ARTTG_stringBrace_98] = true;
    artLabelInternalStrings[ARTL_ARTTG_stringBrace_99] = "ARTTG.stringBrace ::= &STRING_BRACE ";
    artLabelStrings[ARTL_ARTTG_stringBrace_99] = "";
    artlhsL[ARTL_ARTTG_stringBrace_99] = ARTL_ARTTG_stringBrace;
    artPopD[ARTL_ARTTG_stringBrace_99] = true;
    artLabelInternalStrings[ARTL_ARTTG_stringBrace_100] = "ARTTG.stringBrace ::= &STRING_BRACE .";
    artLabelStrings[ARTL_ARTTG_stringBrace_100] = "";
    artlhsL[ARTL_ARTTG_stringBrace_100] = ARTL_ARTTG_stringBrace;
    artKindOfs[ARTL_ARTTG_stringBrace_100] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_stringBrace_100] = true;
    arteoR_pL[ARTL_ARTTG_stringBrace_100] = true;
    artPopD[ARTL_ARTTG_stringBrace_100] = true;
  }

  public void artTableInitialiser_ARTTG_stringDollar() {
    artLabelInternalStrings[ARTL_ARTTG_stringDollar] = "ARTTG.stringDollar";
    artLabelStrings[ARTL_ARTTG_stringDollar] = "ARTTG.stringDollar";
    artKindOfs[ARTL_ARTTG_stringDollar] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_stringDollar_92] = "ARTTG.stringDollar ::= . &STRING_DOLLAR ";
    artLabelStrings[ARTL_ARTTG_stringDollar_92] = "";
    artlhsL[ARTL_ARTTG_stringDollar_92] = ARTL_ARTTG_stringDollar;
    artKindOfs[ARTL_ARTTG_stringDollar_92] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ARTTG_stringDollar_92] = true;
    artLabelInternalStrings[ARTL_ARTTG_stringDollar_93] = "ARTTG.stringDollar ::= &STRING_DOLLAR ";
    artLabelStrings[ARTL_ARTTG_stringDollar_93] = "";
    artlhsL[ARTL_ARTTG_stringDollar_93] = ARTL_ARTTG_stringDollar;
    artPopD[ARTL_ARTTG_stringDollar_93] = true;
    artLabelInternalStrings[ARTL_ARTTG_stringDollar_94] = "ARTTG.stringDollar ::= &STRING_DOLLAR .";
    artLabelStrings[ARTL_ARTTG_stringDollar_94] = "";
    artlhsL[ARTL_ARTTG_stringDollar_94] = ARTL_ARTTG_stringDollar;
    artKindOfs[ARTL_ARTTG_stringDollar_94] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_stringDollar_94] = true;
    arteoR_pL[ARTL_ARTTG_stringDollar_94] = true;
    artPopD[ARTL_ARTTG_stringDollar_94] = true;
  }

  public void artTableInitialiser_ARTTG_strings() {
    artLabelInternalStrings[ARTL_ARTTG_strings] = "ARTTG.strings";
    artLabelStrings[ARTL_ARTTG_strings] = "ARTTG.strings";
    artKindOfs[ARTL_ARTTG_strings] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_strings_68] = "ARTTG.strings ::= . # ";
    artLabelStrings[ARTL_ARTTG_strings_68] = "";
    artlhsL[ARTL_ARTTG_strings_68] = ARTL_ARTTG_strings;
    artKindOfs[ARTL_ARTTG_strings_68] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ARTTG_strings_68] = true;
    artLabelInternalStrings[ARTL_ARTTG_strings_70] = "ARTTG.strings ::= # .";
    artLabelStrings[ARTL_ARTTG_strings_70] = "";
    artlhsL[ARTL_ARTTG_strings_70] = ARTL_ARTTG_strings;
    artKindOfs[ARTL_ARTTG_strings_70] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_strings_70] = true;
    arteoR_pL[ARTL_ARTTG_strings_70] = true;
    artPopD[ARTL_ARTTG_strings_70] = true;
    artLabelInternalStrings[ARTL_ARTTG_strings_72] = "ARTTG.strings ::= . ARTTG.stringDollar ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_strings_72] = "";
    artlhsL[ARTL_ARTTG_strings_72] = ARTL_ARTTG_strings;
    artKindOfs[ARTL_ARTTG_strings_72] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_strings_74] = "ARTTG.strings ::= ARTTG.stringDollar . ARTTG.strings ";
    artLabelStrings[ARTL_ARTTG_strings_74] = "";
    artlhsL[ARTL_ARTTG_strings_74] = ARTL_ARTTG_strings;
    artSlotInstanceOfs[ARTL_ARTTG_strings_74] = ARTL_ARTTG_stringDollar;
    artKindOfs[ARTL_ARTTG_strings_74] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ARTTG_strings_74] = true;
    artLabelInternalStrings[ARTL_ARTTG_strings_78] = "ARTTG.strings ::= ARTTG.stringDollar ARTTG.strings .";
    artLabelStrings[ARTL_ARTTG_strings_78] = "";
    artlhsL[ARTL_ARTTG_strings_78] = ARTL_ARTTG_strings;
    artSlotInstanceOfs[ARTL_ARTTG_strings_78] = ARTL_ARTTG_strings;
    artKindOfs[ARTL_ARTTG_strings_78] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_strings_78] = true;
    arteoR_pL[ARTL_ARTTG_strings_78] = true;
    artPopD[ARTL_ARTTG_strings_78] = true;
    artLabelInternalStrings[ARTL_ARTTG_strings_80] = "ARTTG.strings ::= . ARTTG.stringDollar ";
    artLabelStrings[ARTL_ARTTG_strings_80] = "";
    artlhsL[ARTL_ARTTG_strings_80] = ARTL_ARTTG_strings;
    artKindOfs[ARTL_ARTTG_strings_80] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_strings_82] = "ARTTG.strings ::= ARTTG.stringDollar .";
    artLabelStrings[ARTL_ARTTG_strings_82] = "";
    artlhsL[ARTL_ARTTG_strings_82] = ARTL_ARTTG_strings;
    artSlotInstanceOfs[ARTL_ARTTG_strings_82] = ARTL_ARTTG_stringDollar;
    artKindOfs[ARTL_ARTTG_strings_82] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_strings_82] = true;
    arteoR_pL[ARTL_ARTTG_strings_82] = true;
    artPopD[ARTL_ARTTG_strings_82] = true;
  }

  public void artTableInitialiser_ARTTG_test() {
    artLabelInternalStrings[ARTL_ARTTG_test] = "ARTTG.test";
    artLabelStrings[ARTL_ARTTG_test] = "ARTTG.test";
    artKindOfs[ARTL_ARTTG_test] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_test_18] = "ARTTG.test ::= . ARTTG.ID ARTTG.optionOpt ARTTG.stringDollar ARTTG.acceptOpt ARTTG.rejectOpt ";
    artLabelStrings[ARTL_ARTTG_test_18] = "";
    artlhsL[ARTL_ARTTG_test_18] = ARTL_ARTTG_test;
    artKindOfs[ARTL_ARTTG_test_18] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_test_20] = "ARTTG.test ::= ARTTG.ID . ARTTG.optionOpt ARTTG.stringDollar ARTTG.acceptOpt ARTTG.rejectOpt ";
    artLabelStrings[ARTL_ARTTG_test_20] = "";
    artlhsL[ARTL_ARTTG_test_20] = ARTL_ARTTG_test;
    artSlotInstanceOfs[ARTL_ARTTG_test_20] = ARTL_ARTTG_ID;
    artKindOfs[ARTL_ARTTG_test_20] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ARTTG_test_20] = true;
    artLabelInternalStrings[ARTL_ARTTG_test_22] = "ARTTG.test ::= ARTTG.ID ARTTG.optionOpt . ARTTG.stringDollar ARTTG.acceptOpt ARTTG.rejectOpt ";
    artLabelStrings[ARTL_ARTTG_test_22] = "";
    artlhsL[ARTL_ARTTG_test_22] = ARTL_ARTTG_test;
    artSlotInstanceOfs[ARTL_ARTTG_test_22] = ARTL_ARTTG_optionOpt;
    artKindOfs[ARTL_ARTTG_test_22] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_test_24] = "ARTTG.test ::= ARTTG.ID ARTTG.optionOpt ARTTG.stringDollar . ARTTG.acceptOpt ARTTG.rejectOpt ";
    artLabelStrings[ARTL_ARTTG_test_24] = "";
    artlhsL[ARTL_ARTTG_test_24] = ARTL_ARTTG_test;
    artSlotInstanceOfs[ARTL_ARTTG_test_24] = ARTL_ARTTG_stringDollar;
    artKindOfs[ARTL_ARTTG_test_24] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_test_28] = "ARTTG.test ::= ARTTG.ID ARTTG.optionOpt ARTTG.stringDollar ARTTG.acceptOpt . ARTTG.rejectOpt ";
    artLabelStrings[ARTL_ARTTG_test_28] = "";
    artlhsL[ARTL_ARTTG_test_28] = ARTL_ARTTG_test;
    artSlotInstanceOfs[ARTL_ARTTG_test_28] = ARTL_ARTTG_acceptOpt;
    artKindOfs[ARTL_ARTTG_test_28] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_test_32] = "ARTTG.test ::= ARTTG.ID ARTTG.optionOpt ARTTG.stringDollar ARTTG.acceptOpt ARTTG.rejectOpt .";
    artLabelStrings[ARTL_ARTTG_test_32] = "";
    artlhsL[ARTL_ARTTG_test_32] = ARTL_ARTTG_test;
    artSlotInstanceOfs[ARTL_ARTTG_test_32] = ARTL_ARTTG_rejectOpt;
    artKindOfs[ARTL_ARTTG_test_32] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_test_32] = true;
    arteoR_pL[ARTL_ARTTG_test_32] = true;
    artPopD[ARTL_ARTTG_test_32] = true;
  }

  public void artTableInitialiser_ARTTG_tests() {
    artLabelInternalStrings[ARTL_ARTTG_tests] = "ARTTG.tests";
    artLabelStrings[ARTL_ARTTG_tests] = "ARTTG.tests";
    artKindOfs[ARTL_ARTTG_tests] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_tests_8] = "ARTTG.tests ::= . ARTTG.test ARTTG.tests ";
    artLabelStrings[ARTL_ARTTG_tests_8] = "";
    artlhsL[ARTL_ARTTG_tests_8] = ARTL_ARTTG_tests;
    artKindOfs[ARTL_ARTTG_tests_8] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_tests_10] = "ARTTG.tests ::= ARTTG.test . ARTTG.tests ";
    artLabelStrings[ARTL_ARTTG_tests_10] = "";
    artlhsL[ARTL_ARTTG_tests_10] = ARTL_ARTTG_tests;
    artSlotInstanceOfs[ARTL_ARTTG_tests_10] = ARTL_ARTTG_test;
    artKindOfs[ARTL_ARTTG_tests_10] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ARTTG_tests_10] = true;
    artLabelInternalStrings[ARTL_ARTTG_tests_12] = "ARTTG.tests ::= ARTTG.test ARTTG.tests .";
    artLabelStrings[ARTL_ARTTG_tests_12] = "";
    artlhsL[ARTL_ARTTG_tests_12] = ARTL_ARTTG_tests;
    artSlotInstanceOfs[ARTL_ARTTG_tests_12] = ARTL_ARTTG_tests;
    artKindOfs[ARTL_ARTTG_tests_12] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_tests_12] = true;
    arteoR_pL[ARTL_ARTTG_tests_12] = true;
    artPopD[ARTL_ARTTG_tests_12] = true;
    artLabelInternalStrings[ARTL_ARTTG_tests_14] = "ARTTG.tests ::= . ARTTG.test ";
    artLabelStrings[ARTL_ARTTG_tests_14] = "";
    artlhsL[ARTL_ARTTG_tests_14] = ARTL_ARTTG_tests;
    artKindOfs[ARTL_ARTTG_tests_14] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_tests_16] = "ARTTG.tests ::= ARTTG.test .";
    artLabelStrings[ARTL_ARTTG_tests_16] = "";
    artlhsL[ARTL_ARTTG_tests_16] = ARTL_ARTTG_tests;
    artSlotInstanceOfs[ARTL_ARTTG_tests_16] = ARTL_ARTTG_test;
    artKindOfs[ARTL_ARTTG_tests_16] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_tests_16] = true;
    arteoR_pL[ARTL_ARTTG_tests_16] = true;
    artPopD[ARTL_ARTTG_tests_16] = true;
  }

  public void artTableInitialiser_ARTTG_text() {
    artLabelInternalStrings[ARTL_ARTTG_text] = "ARTTG.text";
    artLabelStrings[ARTL_ARTTG_text] = "ARTTG.text";
    artKindOfs[ARTL_ARTTG_text] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ARTTG_text_2] = "ARTTG.text ::= . ARTTG.tests ";
    artLabelStrings[ARTL_ARTTG_text_2] = "";
    artlhsL[ARTL_ARTTG_text_2] = ARTL_ARTTG_text;
    artKindOfs[ARTL_ARTTG_text_2] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ARTTG_text_6] = "ARTTG.text ::= ARTTG.tests .";
    artLabelStrings[ARTL_ARTTG_text_6] = "";
    artlhsL[ARTL_ARTTG_text_6] = ARTL_ARTTG_text;
    artSlotInstanceOfs[ARTL_ARTTG_text_6] = ARTL_ARTTG_tests;
    artKindOfs[ARTL_ARTTG_text_6] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ARTTG_text_6] = true;
    arteoR_pL[ARTL_ARTTG_text_6] = true;
    artPopD[ARTL_ARTTG_text_6] = true;
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

    artNonterminalsDeclaredAsTerminals = new boolean[ARTX_LABEL_EXTENT];

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

    artLabelStrings[ARTTB_ID] = "ID";
    artLabelInternalStrings[ARTTB_ID] = "&ID";
    artKindOfs[ARTTB_ID] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_ID] = true;
    artLabelStrings[ARTTB_STRING_BRACE] = "STRING_BRACE";
    artLabelInternalStrings[ARTTB_STRING_BRACE] = "&STRING_BRACE";
    artKindOfs[ARTTB_STRING_BRACE] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_STRING_BRACE] = true;
    artLabelStrings[ARTTB_STRING_DOLLAR] = "STRING_DOLLAR";
    artLabelInternalStrings[ARTTB_STRING_DOLLAR] = "&STRING_DOLLAR";
    artKindOfs[ARTTB_STRING_DOLLAR] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_STRING_DOLLAR] = true;
    artLabelStrings[ARTTS_accept] = "accept";
    artLabelInternalStrings[ARTTS_accept] = "'accept'";
    artKindOfs[ARTTS_accept] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_accept] = true;
    artLabelStrings[ARTTS_reject] = "reject";
    artLabelInternalStrings[ARTTS_reject] = "'reject'";
    artKindOfs[ARTTS_reject] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_reject] = true;
    artTableInitialiser_ARTTG_ID();
    artTableInitialiser_ARTTG_acceptOpt();
    artTableInitialiser_ARTTG_optionOpt();
    artTableInitialiser_ARTTG_rejectOpt();
    artTableInitialiser_ARTTG_stringBrace();
    artTableInitialiser_ARTTG_stringDollar();
    artTableInitialiser_ARTTG_strings();
    artTableInitialiser_ARTTG_test();
    artTableInitialiser_ARTTG_tests();
    artTableInitialiser_ARTTG_text();
  }

  public ARTTGParser(ARTLexerBase artLexerBase) {
    this(null, artLexerBase);
  }

  public ARTTGParser(ARTGrammar artGrammar, ARTLexerBase artLexerBase) {
    super(artGrammar, artLexerBase);
    artFirstTerminalLabel = ARTTS_accept;
    artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
    artSetExtent = ARTX_EPSILON + 1;
    ARTL_EOS = ARTX_EOS;
    ARTL_EPSILON = ARTX_EPSILON;
    ARTL_DUMMY = ARTX_DUMMY;
    artGrammarKind = ARTModeGrammarKind.BNF;
    artDefaultStartSymbolLabel = ARTL_ARTTG_text;
    artBuildOptions = "ARTOptionBlock [verbosityLevel=0, statistics=false, trace=false, inputFilenames=[], phaseModule=true, phaseLex=true, phasePreChoose=true, phaseParse=true, phasePostChoose=true, phaseDerivationSelect=true, phaseGIFT=true, phaseAG=true, phaseTR=true, phaseSOS=true, showTWE=false, showBSR=false, showSPPFFull=false, showSPPFCore=false, showDT=false, showGIFT=false, showAG=false, showTR=false, showSOS=false, ebnfClosureLeft=false, ebnfClosureRight=false, ebnfMultiplyOut=false, ebnfLeftFactor=false, ebnfBracketToNonterminal=false, lexWSSuffix=false, lexExtents=false, lexSegments=false, lexRecursive=false, lexPrintTWESet=false, lexDFA=false, lexCFRecognise=false, lexCFParse=true, lexDead=false, lexLongestWithin=false, lexLongestAcross=false, lexPriority=false, postLongestWithin=false, postLongestAcross=false, postPriority=false, outputDirectory=., parserName=ARTTGParser, lexerName=ARTTGLexer, namespace=uk.ac.rhul.cs.csle.art.tg, despatchMode=fragment, supportMode=HashPool, targetLanguageMode=Java, predictivePops=false, FIFODescriptors=false, suppressPopGuard=false, suppressProductionGuard=false, suppressTestRepeat=false, suppressSemantics=false, clusteredGSS=false, mGLL=false, algorithmMode=gllGeneratorPool, treeLevel=3]";
    artFIFODescriptors = false;
    artSetInitialise();
    artTableInitialise();
  }

  private ARTGLLRDT artRDT;
  private int artNextFreeNodeNumber = 1;
int stringCount; boolean v3;
  public static class ARTAT_ARTTG_ID extends ARTGLLAttributeBlock {
    public int rightExtent;
    public int leftExtent;
    public String lexeme;
    public String v;
    public String toString() {
      String ret = "";
    ret += " rightExtent=" + rightExtent;
    ret += " leftExtent=" + leftExtent;
    ret += " lexeme=" + lexeme;
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_ARTTG_acceptOpt extends ARTGLLAttributeBlock {
    public String base;
    public int count;
    ARTGLLRDTHandle strings1;
    public String toString() {
      String ret = "";
    ret += " base=" + base;
    ret += " count=" + count;
      return ret + " ";
}
  }

  public static class ARTAT_ARTTG_optionOpt extends ARTGLLAttributeBlock {
    public String v;
    ARTGLLRDTHandle stringBrace1;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_ARTTG_rejectOpt extends ARTGLLAttributeBlock {
    public String base;
    public int count;
    ARTGLLRDTHandle strings1;
    public String toString() {
      String ret = "";
    ret += " base=" + base;
    ret += " count=" + count;
      return ret + " ";
}
  }

  public static class ARTAT_ARTTG_stringBrace extends ARTGLLAttributeBlock {
    public int rightExtent;
    public int leftExtent;
    public String lexeme;
    public String v;
    public String toString() {
      String ret = "";
    ret += " rightExtent=" + rightExtent;
    ret += " leftExtent=" + leftExtent;
    ret += " lexeme=" + lexeme;
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_ARTTG_stringDollar extends ARTGLLAttributeBlock {
    public int rightExtent;
    public int leftExtent;
    public String lexeme;
    public String v;
    public String toString() {
      String ret = "";
    ret += " rightExtent=" + rightExtent;
    ret += " leftExtent=" + leftExtent;
    ret += " lexeme=" + lexeme;
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_ARTTG_strings extends ARTGLLAttributeBlock {
    public String fileName;
    ARTGLLRDTHandle stringDollar1;
    ARTGLLRDTHandle strings1;
    public String toString() {
      String ret = "";
    ret += " fileName=" + fileName;
      return ret + " ";
}
  }

  public static class ARTAT_ARTTG_text extends ARTGLLAttributeBlock {
    public boolean v3;
    public String toString() {
      String ret = "";
    ret += " v3=" + v3;
      return ret + " ";
}
  }

  public void ARTRD_ID(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_ID ID) throws ARTException {
  ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
      /*ARTTG.ID ::= &ID .*/
      case ARTL_ARTTG_ID_88: 
                ARTRD_ID(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID);
        artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
        ID.lexeme = artLexeme(ID.leftExtent, ID.rightExtent); ID.v = artLexemeAsID(ID.leftExtent, ID.rightExtent); 
        break;
        default:
          throw new ARTException("ARTRD_ID: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_acceptOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_acceptOpt acceptOpt, ARTAT_ARTTG_strings strings1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.acceptOpt ::= 'accept' ARTTG.strings .*/
    case ARTL_ARTTG_acceptOpt_50: 
      strings1 = new ARTAT_ARTTG_strings();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
       strings1.fileName = acceptOpt.base + ".acc"; 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), strings1));
      ARTRD_strings(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, strings1, null, null);
            break;
    /*ARTTG.acceptOpt ::= # .*/
    case ARTL_ARTTG_acceptOpt_54: 
      strings1 = new ARTAT_ARTTG_strings();
            ARTRD_acceptOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, acceptOpt, strings1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_acceptOpt: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_optionOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_optionOpt optionOpt, ARTAT_ARTTG_stringBrace stringBrace1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.optionOpt ::= ARTTG.stringBrace .*/
    case ARTL_ARTTG_optionOpt_36: 
      stringBrace1 = new ARTAT_ARTTG_stringBrace();
            ARTRD_optionOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, optionOpt, stringBrace1);
      stringBrace1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      stringBrace1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), stringBrace1));
      ARTRD_stringBrace(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, stringBrace1);
       optionOpt.v = stringBrace1.v; 
      break;
    /*ARTTG.optionOpt ::= # .*/
    case ARTL_ARTTG_optionOpt_42: 
      stringBrace1 = new ARTAT_ARTTG_stringBrace();
            ARTRD_optionOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, optionOpt, stringBrace1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_optionOpt: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_rejectOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_rejectOpt rejectOpt, ARTAT_ARTTG_strings strings1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.rejectOpt ::= 'reject' ARTTG.strings .*/
    case ARTL_ARTTG_rejectOpt_62: 
      strings1 = new ARTAT_ARTTG_strings();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
       strings1.fileName = rejectOpt.base + ".rej"; 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), strings1));
      ARTRD_strings(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, strings1, null, null);
            break;
    /*ARTTG.rejectOpt ::= # .*/
    case ARTL_ARTTG_rejectOpt_66: 
      strings1 = new ARTAT_ARTTG_strings();
            ARTRD_rejectOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, rejectOpt, strings1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_rejectOpt: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_stringBrace(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_stringBrace stringBrace) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.stringBrace ::= &STRING_BRACE .*/
    case ARTL_ARTTG_stringBrace_100: 
            ARTRD_stringBrace(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, stringBrace);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      stringBrace.lexeme = artLexeme(stringBrace.leftExtent, stringBrace.rightExtent); stringBrace.v = artLexemeAsString(stringBrace.leftExtent, stringBrace.rightExtent); 
      break;
        default:
          throw new ARTException("ARTRD_stringBrace: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_stringDollar(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_stringDollar stringDollar) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.stringDollar ::= &STRING_DOLLAR .*/
    case ARTL_ARTTG_stringDollar_94: 
            ARTRD_stringDollar(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, stringDollar);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      stringDollar.lexeme = artLexeme(stringDollar.leftExtent, stringDollar.rightExtent); stringDollar.v = artLexemeAsRawString(stringDollar.leftExtent, stringDollar.rightExtent); 
      break;
        default:
          throw new ARTException("ARTRD_stringDollar: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_strings(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_strings strings, ARTAT_ARTTG_stringDollar stringDollar1, ARTAT_ARTTG_strings strings1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.strings ::= # .*/
    case ARTL_ARTTG_strings_70: 
      stringDollar1 = new ARTAT_ARTTG_stringDollar();
      strings1 = new ARTAT_ARTTG_strings();
            ARTRD_strings(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, strings, stringDollar1, strings1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*ARTTG.strings ::= ARTTG.stringDollar ARTTG.strings .*/
    case ARTL_ARTTG_strings_78: 
      stringDollar1 = new ARTAT_ARTTG_stringDollar();
      strings1 = new ARTAT_ARTTG_strings();
            stringDollar1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      stringDollar1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), stringDollar1));
      ARTRD_stringDollar(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, stringDollar1);
       System.out.println("New string file '" + strings.fileName + "_" + stringCount /* + ": string '" + stringDollar1.v + "'" */); 
    try { PrintWriter out = new PrintWriter(new File(strings.fileName + "_" + stringCount++));
    out.printf("%s", stringDollar1.v);
    out.close(); }catch (FileNotFoundException e) { e.printStackTrace(); }
    strings1.fileName = strings.fileName;
  
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), strings1));
      ARTRD_strings(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, strings1, null, null);
            break;
    /*ARTTG.strings ::= ARTTG.stringDollar .*/
    case ARTL_ARTTG_strings_82: 
      stringDollar1 = new ARTAT_ARTTG_stringDollar();
      strings1 = new ARTAT_ARTTG_strings();
            ARTRD_strings(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, strings, stringDollar1, strings1);
      stringDollar1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      stringDollar1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), stringDollar1));
      ARTRD_stringDollar(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, stringDollar1);
       System.out.println("New string file '" + strings.fileName + "_" + stringCount /* + ": string '" + stringDollar1.v + "'"*/); 
    try { PrintWriter out = new PrintWriter(new File(strings.fileName + "_" + stringCount++));
    out.printf("%s", stringDollar1.v);
    out.close(); }catch (FileNotFoundException e) { e.printStackTrace(); }
  
      break;
        default:
          throw new ARTException("ARTRD_strings: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_test(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_ID ID1, ARTAT_ARTTG_acceptOpt acceptOpt1, ARTAT_ARTTG_optionOpt optionOpt1, ARTAT_ARTTG_rejectOpt rejectOpt1, ARTAT_ARTTG_stringDollar stringDollar1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.test ::= ARTTG.ID ARTTG.optionOpt . ARTTG.stringDollar ARTTG.acceptOpt ARTTG.rejectOpt */
    case ARTL_ARTTG_test_22: 
      ID1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID1));
      ARTRD_ID(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID1);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), optionOpt1));
      ARTRD_optionOpt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, optionOpt1, null);
            break;
    /*ARTTG.test ::= ARTTG.ID ARTTG.optionOpt ARTTG.stringDollar . ARTTG.acceptOpt ARTTG.rejectOpt */
    case ARTL_ARTTG_test_24: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID1, acceptOpt1, optionOpt1, rejectOpt1, stringDollar1);
      stringDollar1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      stringDollar1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), stringDollar1));
      ARTRD_stringDollar(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, stringDollar1);
       
  StringBuilder stringBuilder = new StringBuilder(stringDollar1.v + " ");
  StringBuilder startSymbol = new StringBuilder();

  int cc;
  for (cc = 0; cc < stringDollar1.v.length(); cc++) {
    if (stringDollar1.v.charAt(cc) == '_' || Character.isLetter(stringDollar1.v.charAt(cc)))
      break; 
    if (stringDollar1.v.charAt(cc) == '(' && stringDollar1.v.charAt(cc+1) == '*') 
      do 
        cc++;
      while(!(stringDollar1.v.charAt(cc-1) == '*' && stringDollar1.v.charAt(cc) == ')'));
   }

  for (; cc < stringDollar1.v.length(); cc++) {
    if (!(stringDollar1.v.charAt(cc) == '_' || Character.isLetter(stringDollar1.v.charAt(cc))))
      break; 
    startSymbol.append(stringDollar1.v.charAt(cc));
   }

  try { PrintWriter out = new PrintWriter(new File(ID1.v + ".art"));
  if (v3) {
    out.println("module M");
  } else {
    out.println("(* " + ID1.v + ".art *)");
    out.println("M()(" + startSymbol.toString() + ")");
  }

  if (v3) {
    for (int i = 1; i < stringBuilder.length(); i++)
      if (stringBuilder.charAt(i) == ';' && !(stringBuilder.charAt(i-1) == '\'' && stringBuilder.charAt(i+1) == '\'')){
        stringBuilder.replace(i, i+1, " "); 
      }
  }

  out.printf("%s", stringBuilder.toString());
  out.close(); }catch (FileNotFoundException e) { e.printStackTrace(); }
  System.out.println("New" + (v3 ? " v3" : " v2") + " test file '" + ID1.v + "' with options '" + optionOpt1.v /* + "': grammar '" + stringDollar1.v + "' mapped to '" + stringBuilder.toString() + "'"*/); 
  stringCount = 1;


 acceptOpt1.base = ID1.v; 
      break;
    /*ARTTG.test ::= ARTTG.ID ARTTG.optionOpt ARTTG.stringDollar ARTTG.acceptOpt . ARTTG.rejectOpt */
    case ARTL_ARTTG_test_28: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID1, acceptOpt1, optionOpt1, rejectOpt1, stringDollar1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), acceptOpt1));
      ARTRD_acceptOpt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, acceptOpt1, null);
       rejectOpt1.base = ID1.v; 
      break;
    /*ARTTG.test ::= ARTTG.ID ARTTG.optionOpt ARTTG.stringDollar ARTTG.acceptOpt ARTTG.rejectOpt .*/
    case ARTL_ARTTG_test_32: 
      ID1 = new ARTAT_ARTTG_ID();
      acceptOpt1 = new ARTAT_ARTTG_acceptOpt();
      optionOpt1 = new ARTAT_ARTTG_optionOpt();
      rejectOpt1 = new ARTAT_ARTTG_rejectOpt();
      stringDollar1 = new ARTAT_ARTTG_stringDollar();
            ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID1, acceptOpt1, optionOpt1, rejectOpt1, stringDollar1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), rejectOpt1));
      ARTRD_rejectOpt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, rejectOpt1, null);
            break;
        default:
          throw new ARTException("ARTRD_test: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_tests(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.tests ::= ARTTG.test ARTTG.tests .*/
    case ARTL_ARTTG_tests_12: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, null, null, null, null, null);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_tests(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*ARTTG.tests ::= ARTTG.test .*/
    case ARTL_ARTTG_tests_16: 
            ARTRD_tests(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_test(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null, null, null, null);
            break;
        default:
          throw new ARTException("ARTRD_tests: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_text(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ARTTG_text text) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ARTTG.text ::= ARTTG.tests .*/
    case ARTL_ARTTG_text_6: 
      v3 = text.v3;
      ARTRD_text(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, text);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_tests(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default:
          throw new ARTException("ARTRD_text: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void artEvaluate(ARTGLLRDTHandle artElement, Object artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable) throws ARTException {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ARTTG_ID:  ARTRD_ID(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_ID) artAttributes); break;
    case ARTL_ARTTG_acceptOpt:  ARTRD_acceptOpt(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_acceptOpt) artAttributes, null); break;
    case ARTL_ARTTG_optionOpt:  ARTRD_optionOpt(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_optionOpt) artAttributes, null); break;
    case ARTL_ARTTG_rejectOpt:  ARTRD_rejectOpt(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_rejectOpt) artAttributes, null); break;
    case ARTL_ARTTG_stringBrace:  ARTRD_stringBrace(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_stringBrace) artAttributes); break;
    case ARTL_ARTTG_stringDollar:  ARTRD_stringDollar(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_stringDollar) artAttributes); break;
    case ARTL_ARTTG_strings:  ARTRD_strings(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_strings) artAttributes, null, null); break;
    case ARTL_ARTTG_test: ARTRD_test(artElement.element, artParent, artWriteable, null, null, null, null, null); break;
    case ARTL_ARTTG_tests: ARTRD_tests(artElement.element, artParent, artWriteable); break;
    case ARTL_ARTTG_text:  ARTRD_text(artElement.element, artParent, artWriteable, (ARTAT_ARTTG_text) artAttributes); break;
  }
}

public ARTGLLRDT artEvaluator() throws ARTException {
  ARTAT_ARTTG_text text1 = new ARTAT_ARTTG_text();
  return  artEvaluator(text1);
}

public ARTGLLRDT artEvaluator(ARTAT_ARTTG_text text1) throws ARTException {
  artRDT = new ARTGLLRDT("RDT", artKindOfs, artLabelStrings, artAnnotations, artLexer.artInputString);
  ARTGLLRDTVertex artNewParent = new ARTGLLRDTVertex(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artRootSPPFNode),artSPPFNodeRightExtent(artRootSPPFNode),artSPPFNodeLabel(artRootSPPFNode), text1));
  artRDT.setRoot(artNewParent);
  boolean artNewWriteable = true;
  artEvaluate(new ARTGLLRDTHandle(artRootSPPFNode), text1, artNewParent, artNewWriteable);
  return artRDT;
}
};
