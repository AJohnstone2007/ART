import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLAttributeBlock;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLParserHashPool;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTHandle;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTPayload;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeGrammarKind;
import uk.ac.rhul.cs.csle.art.old.v4.util.bitset.ARTBitSet;

/*******************************************************************************
 *
 * ARTGeneratedParser.java
 *
 *******************************************************************************/
@SuppressWarnings("fallthrough")
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
  private static boolean[] ARTSet12;
  private static boolean[] ARTSet13;
  private static boolean[] ARTSet14;
  private static boolean[] ARTSet15;
  private static boolean[] ARTSet16;
  private static boolean[] ARTSet17;
  private static boolean[] ARTSet18;
  private static boolean[] ARTSet19;
  private static boolean[] ARTSet20;
  private static boolean[] ARTSet21;
  private static boolean[] ARTSet22;
  private static boolean[] ARTSet23;
  private static boolean[] ARTSet24;
  private static boolean[] ARTSet25;
  private static boolean[] ARTSet26;
  private static boolean[] ARTSet27;
  private static boolean[] ARTSet28;
  private static boolean[] ARTSet29;
  private static boolean[] ARTSet30;
  private static boolean[] ARTSet31;
  private static boolean[] ARTSet32;
  private static boolean[] ARTSet33;
  private static boolean[] ARTSet34;
  private static boolean[] ARTSet35;
  private static boolean[] ARTSet36;
  private static boolean[] ARTSet37;
  private static boolean[] ARTSet38;

  /* Start of artLabel enumeration */
  public static final int ARTX_EOS = 0;
  public static final int ARTTB_INTEGER = 1;
  public static final int ARTTB_SIMPLE_WHITESPACE = 2;
  public static final int ARTTB_STRING_DQ = 3;
  public static final int ARTTS__SHREIK_EQUAL = 4;
  public static final int ARTTS__PERCENT = 5;
  public static final int ARTTS__LPAR = 6;
  public static final int ARTTS__RPAR = 7;
  public static final int ARTTS__STAR = 8;
  public static final int ARTTS__STAR_STAR = 9;
  public static final int ARTTS__PLUS = 10;
  public static final int ARTTS__COMMA = 11;
  public static final int ARTTS__MINUS = 12;
  public static final int ARTTS__SLASH = 13;
  public static final int ARTTS__SEMICOLON = 14;
  public static final int ARTTS__LT = 15;
  public static final int ARTTS__LT_EQUAL = 16;
  public static final int ARTTS__EQUAL_EQUAL = 17;
  public static final int ARTTS__GT = 18;
  public static final int ARTTS__GT_EQUAL = 19;
  public static final int ARTTS_print = 20;
  public static final int ARTX_EPSILON = 21;
  public static final int ARTL_ART_INTEGER = 22;
  public static final int ARTL_ART_STRING_DQ = 23;
  public static final int ARTL_ART_e0 = 24;
  public static final int ARTL_ART_e1 = 25;
  public static final int ARTL_ART_e2 = 26;
  public static final int ARTL_ART_e3 = 27;
  public static final int ARTL_ART_e4 = 28;
  public static final int ARTL_ART_e5 = 29;
  public static final int ARTL_ART_printElements = 30;
  public static final int ARTL_ART_statement = 31;
  public static final int ARTL_ART_INTEGER_181 = 32;
  public static final int ARTL_ART_INTEGER_182 = 33;
  public static final int ARTL_ART_INTEGER_183 = 34;
  public static final int ARTL_ART_INTEGER_184 = 35;
  public static final int ARTL_ART_STRING_DQ_37 = 36;
  public static final int ARTL_ART_STRING_DQ_38 = 37;
  public static final int ARTL_ART_STRING_DQ_39 = 38;
  public static final int ARTL_ART_STRING_DQ_40 = 39;
  public static final int ARTL_ART_e0_41 = 40;
  public static final int ARTL_ART_e0_42 = 41;
  public static final int ARTL_ART_e0_43 = 42;
  public static final int ARTL_ART_e0_44 = 43;
  public static final int ARTL_ART_e0_45 = 44;
  public static final int ARTL_ART_e0_46 = 45;
  public static final int ARTL_ART_e0_47 = 46;
  public static final int ARTL_ART_e0_48 = 47;
  public static final int ARTL_ART_e0_49 = 48;
  public static final int ARTL_ART_e0_50 = 49;
  public static final int ARTL_ART_e0_51 = 50;
  public static final int ARTL_ART_e0_52 = 51;
  public static final int ARTL_ART_e0_53 = 52;
  public static final int ARTL_ART_e0_54 = 53;
  public static final int ARTL_ART_e0_55 = 54;
  public static final int ARTL_ART_e0_56 = 55;
  public static final int ARTL_ART_e0_57 = 56;
  public static final int ARTL_ART_e0_58 = 57;
  public static final int ARTL_ART_e0_59 = 58;
  public static final int ARTL_ART_e0_60 = 59;
  public static final int ARTL_ART_e0_61 = 60;
  public static final int ARTL_ART_e0_62 = 61;
  public static final int ARTL_ART_e0_63 = 62;
  public static final int ARTL_ART_e0_64 = 63;
  public static final int ARTL_ART_e0_65 = 64;
  public static final int ARTL_ART_e0_66 = 65;
  public static final int ARTL_ART_e0_67 = 66;
  public static final int ARTL_ART_e0_68 = 67;
  public static final int ARTL_ART_e0_69 = 68;
  public static final int ARTL_ART_e0_70 = 69;
  public static final int ARTL_ART_e0_71 = 70;
  public static final int ARTL_ART_e0_72 = 71;
  public static final int ARTL_ART_e0_73 = 72;
  public static final int ARTL_ART_e0_74 = 73;
  public static final int ARTL_ART_e0_75 = 74;
  public static final int ARTL_ART_e0_76 = 75;
  public static final int ARTL_ART_e0_77 = 76;
  public static final int ARTL_ART_e0_78 = 77;
  public static final int ARTL_ART_e0_79 = 78;
  public static final int ARTL_ART_e0_80 = 79;
  public static final int ARTL_ART_e0_81 = 80;
  public static final int ARTL_ART_e0_82 = 81;
  public static final int ARTL_ART_e0_83 = 82;
  public static final int ARTL_ART_e0_84 = 83;
  public static final int ARTL_ART_e0_85 = 84;
  public static final int ARTL_ART_e0_86 = 85;
  public static final int ARTL_ART_e0_87 = 86;
  public static final int ARTL_ART_e0_88 = 87;
  public static final int ARTL_ART_e0_89 = 88;
  public static final int ARTL_ART_e0_90 = 89;
  public static final int ARTL_ART_e0_91 = 90;
  public static final int ARTL_ART_e0_92 = 91;
  public static final int ARTL_ART_e1_93 = 92;
  public static final int ARTL_ART_e1_94 = 93;
  public static final int ARTL_ART_e1_95 = 94;
  public static final int ARTL_ART_e1_96 = 95;
  public static final int ARTL_ART_e1_97 = 96;
  public static final int ARTL_ART_e1_98 = 97;
  public static final int ARTL_ART_e1_99 = 98;
  public static final int ARTL_ART_e1_100 = 99;
  public static final int ARTL_ART_e1_101 = 100;
  public static final int ARTL_ART_e1_102 = 101;
  public static final int ARTL_ART_e1_103 = 102;
  public static final int ARTL_ART_e1_104 = 103;
  public static final int ARTL_ART_e1_105 = 104;
  public static final int ARTL_ART_e1_106 = 105;
  public static final int ARTL_ART_e1_107 = 106;
  public static final int ARTL_ART_e1_108 = 107;
  public static final int ARTL_ART_e1_109 = 108;
  public static final int ARTL_ART_e1_110 = 109;
  public static final int ARTL_ART_e1_111 = 110;
  public static final int ARTL_ART_e1_112 = 111;
  public static final int ARTL_ART_e2_113 = 112;
  public static final int ARTL_ART_e2_114 = 113;
  public static final int ARTL_ART_e2_115 = 114;
  public static final int ARTL_ART_e2_116 = 115;
  public static final int ARTL_ART_e2_117 = 116;
  public static final int ARTL_ART_e2_118 = 117;
  public static final int ARTL_ART_e2_119 = 118;
  public static final int ARTL_ART_e2_120 = 119;
  public static final int ARTL_ART_e2_121 = 120;
  public static final int ARTL_ART_e2_122 = 121;
  public static final int ARTL_ART_e2_123 = 122;
  public static final int ARTL_ART_e2_124 = 123;
  public static final int ARTL_ART_e2_125 = 124;
  public static final int ARTL_ART_e2_126 = 125;
  public static final int ARTL_ART_e2_127 = 126;
  public static final int ARTL_ART_e2_128 = 127;
  public static final int ARTL_ART_e2_129 = 128;
  public static final int ARTL_ART_e2_130 = 129;
  public static final int ARTL_ART_e2_131 = 130;
  public static final int ARTL_ART_e2_132 = 131;
  public static final int ARTL_ART_e2_133 = 132;
  public static final int ARTL_ART_e2_134 = 133;
  public static final int ARTL_ART_e2_135 = 134;
  public static final int ARTL_ART_e2_136 = 135;
  public static final int ARTL_ART_e2_137 = 136;
  public static final int ARTL_ART_e2_138 = 137;
  public static final int ARTL_ART_e2_139 = 138;
  public static final int ARTL_ART_e2_140 = 139;
  public static final int ARTL_ART_e3_141 = 140;
  public static final int ARTL_ART_e3_142 = 141;
  public static final int ARTL_ART_e3_143 = 142;
  public static final int ARTL_ART_e3_144 = 143;
  public static final int ARTL_ART_e3_145 = 144;
  public static final int ARTL_ART_e3_146 = 145;
  public static final int ARTL_ART_e3_147 = 146;
  public static final int ARTL_ART_e3_148 = 147;
  public static final int ARTL_ART_e3_149 = 148;
  public static final int ARTL_ART_e3_150 = 149;
  public static final int ARTL_ART_e3_151 = 150;
  public static final int ARTL_ART_e3_152 = 151;
  public static final int ARTL_ART_e3_153 = 152;
  public static final int ARTL_ART_e3_154 = 153;
  public static final int ARTL_ART_e3_155 = 154;
  public static final int ARTL_ART_e3_156 = 155;
  public static final int ARTL_ART_e4_157 = 156;
  public static final int ARTL_ART_e4_158 = 157;
  public static final int ARTL_ART_e4_159 = 158;
  public static final int ARTL_ART_e4_160 = 159;
  public static final int ARTL_ART_e4_161 = 160;
  public static final int ARTL_ART_e4_162 = 161;
  public static final int ARTL_ART_e4_163 = 162;
  public static final int ARTL_ART_e4_164 = 163;
  public static final int ARTL_ART_e4_165 = 164;
  public static final int ARTL_ART_e4_166 = 165;
  public static final int ARTL_ART_e4_167 = 166;
  public static final int ARTL_ART_e4_168 = 167;
  public static final int ARTL_ART_e5_169 = 168;
  public static final int ARTL_ART_e5_170 = 169;
  public static final int ARTL_ART_e5_171 = 170;
  public static final int ARTL_ART_e5_172 = 171;
  public static final int ARTL_ART_e5_173 = 172;
  public static final int ARTL_ART_e5_174 = 173;
  public static final int ARTL_ART_e5_175 = 174;
  public static final int ARTL_ART_e5_176 = 175;
  public static final int ARTL_ART_e5_177 = 176;
  public static final int ARTL_ART_e5_178 = 177;
  public static final int ARTL_ART_e5_179 = 178;
  public static final int ARTL_ART_e5_180 = 179;
  public static final int ARTL_ART_printElements_13 = 180;
  public static final int ARTL_ART_printElements_14 = 181;
  public static final int ARTL_ART_printElements_15 = 182;
  public static final int ARTL_ART_printElements_16 = 183;
  public static final int ARTL_ART_printElements_17 = 184;
  public static final int ARTL_ART_printElements_18 = 185;
  public static final int ARTL_ART_printElements_19 = 186;
  public static final int ARTL_ART_printElements_20 = 187;
  public static final int ARTL_ART_printElements_21 = 188;
  public static final int ARTL_ART_printElements_22 = 189;
  public static final int ARTL_ART_printElements_23 = 190;
  public static final int ARTL_ART_printElements_24 = 191;
  public static final int ARTL_ART_printElements_25 = 192;
  public static final int ARTL_ART_printElements_26 = 193;
  public static final int ARTL_ART_printElements_27 = 194;
  public static final int ARTL_ART_printElements_28 = 195;
  public static final int ARTL_ART_printElements_29 = 196;
  public static final int ARTL_ART_printElements_30 = 197;
  public static final int ARTL_ART_printElements_31 = 198;
  public static final int ARTL_ART_printElements_32 = 199;
  public static final int ARTL_ART_printElements_33 = 200;
  public static final int ARTL_ART_printElements_34 = 201;
  public static final int ARTL_ART_printElements_35 = 202;
  public static final int ARTL_ART_printElements_36 = 203;
  public static final int ARTL_ART_statement_1 = 204;
  public static final int ARTL_ART_statement_2 = 205;
  public static final int ARTL_ART_statement_3 = 206;
  public static final int ARTL_ART_statement_4 = 207;
  public static final int ARTL_ART_statement_5 = 208;
  public static final int ARTL_ART_statement_6 = 209;
  public static final int ARTL_ART_statement_7 = 210;
  public static final int ARTL_ART_statement_8 = 211;
  public static final int ARTL_ART_statement_9 = 212;
  public static final int ARTL_ART_statement_10 = 213;
  public static final int ARTL_ART_statement_11 = 214;
  public static final int ARTL_ART_statement_12 = 215;
  public static final int ARTX_DESPATCH = 216;
  public static final int ARTX_DUMMY = 217;
  public static final int ARTX_LABEL_EXTENT = 218;
  /* End of artLabel enumeration */

  /* Start of artName enumeration */
  public static final int ARTNAME_NONE = 0;
  public static final int ARTNAME_EXTENT = 1;

  /* End of artName enumeration */
  public void ARTPF_ART_INTEGER() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal INTEGER production descriptor loads */
    case ARTL_ART_INTEGER:
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_INTEGER_182, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal INTEGER: match production */
    case ARTL_ART_INTEGER_182:
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_INTEGER, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_INTEGER_184, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_STRING_DQ() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal STRING_DQ production descriptor loads */
    case ARTL_ART_STRING_DQ:
      if (ARTSet5[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_STRING_DQ_38, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal STRING_DQ: match production */
    case ARTL_ART_STRING_DQ_38:
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_DQ, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_STRING_DQ_40, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_e0() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal e0 production descriptor loads */
    case ARTL_ART_e0:
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e0_42, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e0_46, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e0_54, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e0_62, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e0_70, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e0_78, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e0_86, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal e0: match production */
    case ARTL_ART_e0_42:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_44, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_44:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e0: match production */
    case ARTL_ART_e0_46:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_48, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_48:
      /* Nonterminal template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__GT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e0_50, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_52, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_52:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e0: match production */
    case ARTL_ART_e0_54:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_56, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_56:
      /* Nonterminal template end */
      if (!ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e0_58, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_60, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_60:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e0: match production */
    case ARTL_ART_e0_62:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_64, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_64:
      /* Nonterminal template end */
      if (!ARTSet10[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__GT_EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e0_66, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_68, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_68:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e0: match production */
    case ARTL_ART_e0_70:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_72, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_72:
      /* Nonterminal template end */
      if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LT_EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e0_74, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_76, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_76:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e0: match production */
    case ARTL_ART_e0_78:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_80, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_80:
      /* Nonterminal template end */
      if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__EQUAL_EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e0_82, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_84, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_84:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e0: match production */
    case ARTL_ART_e0_86:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_88, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_88:
      /* Nonterminal template end */
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__SHREIK_EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e0_90, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e0_92, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e0_92:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_e1() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal e1 production descriptor loads */
    case ARTL_ART_e1:
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e1_94, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e1_98, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e1_106, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal e1: match production */
    case ARTL_ART_e1_94:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e1_96, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e2;
      return;
    }
    case ARTL_ART_e1_96:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e1: match production */
    case ARTL_ART_e1_98:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e1_100, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e1_100:
      /* Nonterminal template end */
      if (!ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__PLUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e1_102, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet15[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e1_104, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e2;
      return;
    }
    case ARTL_ART_e1_104:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e1: match production */
    case ARTL_ART_e1_106:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e1_108, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e1_108:
      /* Nonterminal template end */
      if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e1_110, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet15[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e1_112, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e2;
      return;
    }
    case ARTL_ART_e1_112:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_e2() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal e2 production descriptor loads */
    case ARTL_ART_e2:
      if (ARTSet19[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e2_114, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e2_118, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e2_126, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e2_134, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal e2: match production */
    case ARTL_ART_e2_114:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e2_116, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e3;
      return;
    }
    case ARTL_ART_e2_116:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e2: match production */
    case ARTL_ART_e2_118:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e2_120, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e2;
      return;
    }
    case ARTL_ART_e2_120:
      /* Nonterminal template end */
      if (!ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__STAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e2_122, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e2_124, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e3;
      return;
    }
    case ARTL_ART_e2_124:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e2: match production */
    case ARTL_ART_e2_126:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e2_128, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e2;
      return;
    }
    case ARTL_ART_e2_128:
      /* Nonterminal template end */
      if (!ARTSet21[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__SLASH, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e2_130, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e2_132, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e3;
      return;
    }
    case ARTL_ART_e2_132:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e2: match production */
    case ARTL_ART_e2_134:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e2_136, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e2;
      return;
    }
    case ARTL_ART_e2_136:
      /* Nonterminal template end */
      if (!ARTSet22[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__PERCENT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e2_138, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e2_140, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e3;
      return;
    }
    case ARTL_ART_e2_140:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_e3() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal e3 production descriptor loads */
    case ARTL_ART_e3:
      if (ARTSet24[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e3_142, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet16[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e3_146, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e3_152, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal e3: match production */
    case ARTL_ART_e3_142:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e3_144, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e4;
      return;
    }
    case ARTL_ART_e3_144:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e3: match production */
    case ARTL_ART_e3_146:
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__PLUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e3_148, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e3_150, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e3;
      return;
    }
    case ARTL_ART_e3_150:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e3: match production */
    case ARTL_ART_e3_152:
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e3_154, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e3_156, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e3;
      return;
    }
    case ARTL_ART_e3_156:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_e4() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal e4 production descriptor loads */
    case ARTL_ART_e4:
      if (ARTSet25[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e4_158, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet25[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e4_162, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal e4: match production */
    case ARTL_ART_e4_158:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e4_160, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e5;
      return;
    }
    case ARTL_ART_e4_160:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e4: match production */
    case ARTL_ART_e4_162:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e4_164, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e5;
      return;
    }
    case ARTL_ART_e4_164:
      /* Nonterminal template end */
      if (!ARTSet26[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__STAR_STAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e4_166, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet24[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e4_168, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e4;
      return;
    }
    case ARTL_ART_e4_168:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_e5() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal e5 production descriptor loads */
    case ARTL_ART_e5:
      if (ARTSet28[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e5_170, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet29[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_e5_174, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal e5: match production */
    case ARTL_ART_e5_170:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e5_172, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_INTEGER;
      return;
    }
    case ARTL_ART_e5_172:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal e5: match production */
    case ARTL_ART_e5_174:
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e5_176, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_e5_178, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e1;
      return;
    }
    case ARTL_ART_e5_178:
      /* Nonterminal template end */
      if (!ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_e5_180, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_printElements() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal printElements production descriptor loads */
    case ARTL_ART_printElements:
      if (ARTSet32[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_printElements_14, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet32[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_printElements_18, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet35[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_printElements_26, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet35[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_printElements_30, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal printElements: match production */
    case ARTL_ART_printElements_14:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_printElements_16, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_STRING_DQ;
      return;
    }
    case ARTL_ART_printElements_16:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal printElements: match production */
    case ARTL_ART_printElements_18:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_printElements_20, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_STRING_DQ;
      return;
    }
    case ARTL_ART_printElements_20:
      /* Nonterminal template end */
      if (!ARTSet33[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_printElements_22, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_printElements_24, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_printElements;
      return;
    }
    case ARTL_ART_printElements_24:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal printElements: match production */
    case ARTL_ART_printElements_26:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_printElements_28, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e0;
      return;
    }
    case ARTL_ART_printElements_28:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    /* Nonterminal printElements: match production */
    case ARTL_ART_printElements_30:
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_printElements_32, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_e0;
      return;
    }
    case ARTL_ART_printElements_32:
      /* Nonterminal template end */
      if (!ARTSet33[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_printElements_34, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_printElements_36, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_printElements;
      return;
    }
    case ARTL_ART_printElements_36:
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  public void ARTPF_ART_statement() {
    switch (artCurrentRestartLabel) {
    /* Nonterminal statement production descriptor loads */
    case ARTL_ART_statement:
      if (ARTSet36[artInputPairBuffer[artCurrentInputPairReference]])
        artFindDescriptor(ARTL_ART_statement_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH;
      return;
    }
    /* Nonterminal statement: match production */
    case ARTL_ART_statement_2:
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_print, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_statement_4, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet29[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_statement_6, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_statement_8, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTL_ART_printElements;
      return;
    }
    case ARTL_ART_statement_8:
      /* Nonterminal template end */
      if (!ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_statement_10, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet38[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference
          + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__SEMICOLON, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_statement_12, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet37[artInputPairBuffer[artCurrentInputPairReference]]) {
        artCurrentRestartLabel = ARTX_DESPATCH;
        return;
      }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode); {
      artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */;
      return;
    }
    }
  }

  @Override
  public void artParseBody(int artStartLabel) {
    artSpecificationName = "";
    artStartSymbolLabel = artStartLabel;
    artIsInLanguage = false;
    artTokenExtent = 32;
    artLexicaliseForV3GLL(artInputString, null);
    artDummySPPFNode = artFindSPPFInitial(ARTL_DUMMY, 0, 0);
    artCurrentSPPFNode = artDummySPPFNode;
    artRootGSSNode = artFindGSS(ARTL_EOS, 0, 0, 0);
    artCurrentGSSNode = artRootGSSNode;
    artCurrentRestartLabel = artStartSymbolLabel;
    artCurrentInputPairIndex = 0;
    artCurrentInputPairReference = 0;
    while (true)
      switch (artlhsL[artCurrentRestartLabel]) {
      case ARTL_ART_INTEGER:
        ARTPF_ART_INTEGER();
        break;
      case ARTL_ART_STRING_DQ:
        ARTPF_ART_STRING_DQ();
        break;
      case ARTL_ART_e0:
        ARTPF_ART_e0();
        break;
      case ARTL_ART_e1:
        ARTPF_ART_e1();
        break;
      case ARTL_ART_e2:
        ARTPF_ART_e2();
        break;
      case ARTL_ART_e3:
        ARTPF_ART_e3();
        break;
      case ARTL_ART_e4:
        ARTPF_ART_e4();
        break;
      case ARTL_ART_e5:
        ARTPF_ART_e5();
        break;
      case ARTL_ART_printElements:
        ARTPF_ART_printElements();
        break;
      case ARTL_ART_statement:
        ARTPF_ART_statement();
        break;
      case ARTX_DESPATCH:
        if (artNoDescriptors()) {
          artCheckAcceptance();
          return;
        }
        artUnloadDescriptor();
      }
  }

  public void ARTSet1initialise() {
    ARTSet1 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet1, 0, artSetExtent, false);
  }

  public void ARTSet19initialise() {
    ARTSet19 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet19, 0, artSetExtent, false);
    ARTSet19[ARTTB_INTEGER] = true;
    ARTSet19[ARTTS__LPAR] = true;
    ARTSet19[ARTTS__PLUS] = true;
    ARTSet19[ARTTS__MINUS] = true;
    ARTSet19[ARTL_ART_INTEGER] = true;
    ARTSet19[ARTL_ART_e3] = true;
    ARTSet19[ARTL_ART_e4] = true;
    ARTSet19[ARTL_ART_e5] = true;
  }

  public void ARTSet37initialise() {
    ARTSet37 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet37, 0, artSetExtent, false);
    ARTSet37[ARTX_EOS] = true;
  }

  public void ARTSet28initialise() {
    ARTSet28 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet28, 0, artSetExtent, false);
    ARTSet28[ARTTB_INTEGER] = true;
    ARTSet28[ARTL_ART_INTEGER] = true;
  }

  public void ARTSet3initialise() {
    ARTSet3 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
    ARTSet3[ARTTS__SHREIK_EQUAL] = true;
    ARTSet3[ARTTS__PERCENT] = true;
    ARTSet3[ARTTS__RPAR] = true;
    ARTSet3[ARTTS__STAR] = true;
    ARTSet3[ARTTS__STAR_STAR] = true;
    ARTSet3[ARTTS__PLUS] = true;
    ARTSet3[ARTTS__COMMA] = true;
    ARTSet3[ARTTS__MINUS] = true;
    ARTSet3[ARTTS__SLASH] = true;
    ARTSet3[ARTTS__LT] = true;
    ARTSet3[ARTTS__LT_EQUAL] = true;
    ARTSet3[ARTTS__EQUAL_EQUAL] = true;
    ARTSet3[ARTTS__GT] = true;
    ARTSet3[ARTTS__GT_EQUAL] = true;
  }

  public void ARTSet23initialise() {
    ARTSet23 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet23, 0, artSetExtent, false);
    ARTSet23[ARTTB_INTEGER] = true;
    ARTSet23[ARTTS__LPAR] = true;
    ARTSet23[ARTTS__PLUS] = true;
    ARTSet23[ARTTS__MINUS] = true;
    ARTSet23[ARTL_ART_INTEGER] = true;
    ARTSet23[ARTL_ART_e4] = true;
    ARTSet23[ARTL_ART_e5] = true;
  }

  public void ARTSet32initialise() {
    ARTSet32 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet32, 0, artSetExtent, false);
    ARTSet32[ARTTB_STRING_DQ] = true;
    ARTSet32[ARTL_ART_STRING_DQ] = true;
  }

  public void ARTSet6initialise() {
    ARTSet6 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
    ARTSet6[ARTTS__RPAR] = true;
    ARTSet6[ARTTS__COMMA] = true;
  }

  public void ARTSet18initialise() {
    ARTSet18 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet18, 0, artSetExtent, false);
    ARTSet18[ARTTS__SHREIK_EQUAL] = true;
    ARTSet18[ARTTS__PERCENT] = true;
    ARTSet18[ARTTS__RPAR] = true;
    ARTSet18[ARTTS__STAR] = true;
    ARTSet18[ARTTS__PLUS] = true;
    ARTSet18[ARTTS__COMMA] = true;
    ARTSet18[ARTTS__MINUS] = true;
    ARTSet18[ARTTS__SLASH] = true;
    ARTSet18[ARTTS__LT] = true;
    ARTSet18[ARTTS__LT_EQUAL] = true;
    ARTSet18[ARTTS__EQUAL_EQUAL] = true;
    ARTSet18[ARTTS__GT] = true;
    ARTSet18[ARTTS__GT_EQUAL] = true;
  }

  public void ARTSet35initialise() {
    ARTSet35 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet35, 0, artSetExtent, false);
    ARTSet35[ARTTB_INTEGER] = true;
    ARTSet35[ARTTS__LPAR] = true;
    ARTSet35[ARTTS__PLUS] = true;
    ARTSet35[ARTTS__MINUS] = true;
    ARTSet35[ARTL_ART_INTEGER] = true;
    ARTSet35[ARTL_ART_e0] = true;
    ARTSet35[ARTL_ART_e1] = true;
    ARTSet35[ARTL_ART_e2] = true;
    ARTSet35[ARTL_ART_e3] = true;
    ARTSet35[ARTL_ART_e4] = true;
    ARTSet35[ARTL_ART_e5] = true;
  }

  public void ARTSet31initialise() {
    ARTSet31 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet31, 0, artSetExtent, false);
    ARTSet31[ARTTB_INTEGER] = true;
    ARTSet31[ARTTB_STRING_DQ] = true;
    ARTSet31[ARTTS__LPAR] = true;
    ARTSet31[ARTTS__PLUS] = true;
    ARTSet31[ARTTS__MINUS] = true;
    ARTSet31[ARTL_ART_INTEGER] = true;
    ARTSet31[ARTL_ART_STRING_DQ] = true;
    ARTSet31[ARTL_ART_e0] = true;
    ARTSet31[ARTL_ART_e1] = true;
    ARTSet31[ARTL_ART_e2] = true;
    ARTSet31[ARTL_ART_e3] = true;
    ARTSet31[ARTL_ART_e4] = true;
    ARTSet31[ARTL_ART_e5] = true;
  }

  public void ARTSet14initialise() {
    ARTSet14 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet14, 0, artSetExtent, false);
    ARTSet14[ARTTS__SHREIK_EQUAL] = true;
    ARTSet14[ARTTS__RPAR] = true;
    ARTSet14[ARTTS__PLUS] = true;
    ARTSet14[ARTTS__COMMA] = true;
    ARTSet14[ARTTS__MINUS] = true;
    ARTSet14[ARTTS__LT] = true;
    ARTSet14[ARTTS__LT_EQUAL] = true;
    ARTSet14[ARTTS__EQUAL_EQUAL] = true;
    ARTSet14[ARTTS__GT] = true;
    ARTSet14[ARTTS__GT_EQUAL] = true;
  }

  public void ARTSet12initialise() {
    ARTSet12 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet12, 0, artSetExtent, false);
    ARTSet12[ARTTS__EQUAL_EQUAL] = true;
  }

  public void ARTSet2initialise() {
    ARTSet2 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
    ARTSet2[ARTTB_INTEGER] = true;
  }

  public void ARTSet34initialise() {
    ARTSet34 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet34, 0, artSetExtent, false);
    ARTSet34[ARTTB_INTEGER] = true;
    ARTSet34[ARTTB_STRING_DQ] = true;
    ARTSet34[ARTTS__LPAR] = true;
    ARTSet34[ARTTS__PLUS] = true;
    ARTSet34[ARTTS__MINUS] = true;
    ARTSet34[ARTL_ART_INTEGER] = true;
    ARTSet34[ARTL_ART_STRING_DQ] = true;
    ARTSet34[ARTL_ART_e0] = true;
    ARTSet34[ARTL_ART_e1] = true;
    ARTSet34[ARTL_ART_e2] = true;
    ARTSet34[ARTL_ART_e3] = true;
    ARTSet34[ARTL_ART_e4] = true;
    ARTSet34[ARTL_ART_e5] = true;
    ARTSet34[ARTL_ART_printElements] = true;
  }

  public void ARTSet22initialise() {
    ARTSet22 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet22, 0, artSetExtent, false);
    ARTSet22[ARTTS__PERCENT] = true;
  }

  public void ARTSet25initialise() {
    ARTSet25 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet25, 0, artSetExtent, false);
    ARTSet25[ARTTB_INTEGER] = true;
    ARTSet25[ARTTS__LPAR] = true;
    ARTSet25[ARTL_ART_INTEGER] = true;
    ARTSet25[ARTL_ART_e5] = true;
  }

  public void ARTSet29initialise() {
    ARTSet29 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet29, 0, artSetExtent, false);
    ARTSet29[ARTTS__LPAR] = true;
  }

  public void ARTSet30initialise() {
    ARTSet30 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet30, 0, artSetExtent, false);
    ARTSet30[ARTTS__RPAR] = true;
  }

  public void ARTSet20initialise() {
    ARTSet20 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet20, 0, artSetExtent, false);
    ARTSet20[ARTTS__STAR] = true;
  }

  public void ARTSet16initialise() {
    ARTSet16 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet16, 0, artSetExtent, false);
    ARTSet16[ARTTS__PLUS] = true;
  }

  public void ARTSet15initialise() {
    ARTSet15 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet15, 0, artSetExtent, false);
    ARTSet15[ARTTB_INTEGER] = true;
    ARTSet15[ARTTS__LPAR] = true;
    ARTSet15[ARTTS__PLUS] = true;
    ARTSet15[ARTTS__MINUS] = true;
    ARTSet15[ARTL_ART_INTEGER] = true;
    ARTSet15[ARTL_ART_e2] = true;
    ARTSet15[ARTL_ART_e3] = true;
    ARTSet15[ARTL_ART_e4] = true;
    ARTSet15[ARTL_ART_e5] = true;
  }

  public void ARTSet33initialise() {
    ARTSet33 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet33, 0, artSetExtent, false);
    ARTSet33[ARTTS__COMMA] = true;
  }

  public void ARTSet5initialise() {
    ARTSet5 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
    ARTSet5[ARTTB_STRING_DQ] = true;
  }

  public void ARTSet17initialise() {
    ARTSet17 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet17, 0, artSetExtent, false);
    ARTSet17[ARTTS__MINUS] = true;
  }

  public void ARTSet27initialise() {
    ARTSet27 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet27, 0, artSetExtent, false);
    ARTSet27[ARTTB_INTEGER] = true;
    ARTSet27[ARTTS__LPAR] = true;
    ARTSet27[ARTL_ART_INTEGER] = true;
  }

  public void ARTSet21initialise() {
    ARTSet21 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet21, 0, artSetExtent, false);
    ARTSet21[ARTTS__SLASH] = true;
  }

  public void ARTSet7initialise() {
    ARTSet7 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
    ARTSet7[ARTTB_INTEGER] = true;
    ARTSet7[ARTTS__LPAR] = true;
    ARTSet7[ARTTS__PLUS] = true;
    ARTSet7[ARTTS__MINUS] = true;
    ARTSet7[ARTL_ART_INTEGER] = true;
    ARTSet7[ARTL_ART_e1] = true;
    ARTSet7[ARTL_ART_e2] = true;
    ARTSet7[ARTL_ART_e3] = true;
    ARTSet7[ARTL_ART_e4] = true;
    ARTSet7[ARTL_ART_e5] = true;
  }

  public void ARTSet36initialise() {
    ARTSet36 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet36, 0, artSetExtent, false);
    ARTSet36[ARTTS_print] = true;
  }

  public void ARTSet38initialise() {
    ARTSet38 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet38, 0, artSetExtent, false);
    ARTSet38[ARTTS__SEMICOLON] = true;
  }

  public void ARTSet9initialise() {
    ARTSet9 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
    ARTSet9[ARTTS__LT] = true;
  }

  public void ARTSet13initialise() {
    ARTSet13 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet13, 0, artSetExtent, false);
    ARTSet13[ARTTS__SHREIK_EQUAL] = true;
  }

  public void ARTSet24initialise() {
    ARTSet24 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet24, 0, artSetExtent, false);
    ARTSet24[ARTTB_INTEGER] = true;
    ARTSet24[ARTTS__LPAR] = true;
    ARTSet24[ARTL_ART_INTEGER] = true;
    ARTSet24[ARTL_ART_e4] = true;
    ARTSet24[ARTL_ART_e5] = true;
  }

  public void ARTSet4initialise() {
    ARTSet4 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
  }

  public void ARTSet8initialise() {
    ARTSet8 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
    ARTSet8[ARTTS__GT] = true;
  }

  public void ARTSet10initialise() {
    ARTSet10 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet10, 0, artSetExtent, false);
    ARTSet10[ARTTS__GT_EQUAL] = true;
  }

  public void ARTSet26initialise() {
    ARTSet26 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet26, 0, artSetExtent, false);
    ARTSet26[ARTTS__STAR_STAR] = true;
  }

  public void ARTSet11initialise() {
    ARTSet11 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet11, 0, artSetExtent, false);
    ARTSet11[ARTTS__LT_EQUAL] = true;
  }

  public void artSetInitialise() {
    ARTSet1initialise();
    ARTSet19initialise();
    ARTSet37initialise();
    ARTSet28initialise();
    ARTSet3initialise();
    ARTSet23initialise();
    ARTSet32initialise();
    ARTSet6initialise();
    ARTSet18initialise();
    ARTSet35initialise();
    ARTSet31initialise();
    ARTSet14initialise();
    ARTSet12initialise();
    ARTSet2initialise();
    ARTSet34initialise();
    ARTSet22initialise();
    ARTSet25initialise();
    ARTSet29initialise();
    ARTSet30initialise();
    ARTSet20initialise();
    ARTSet16initialise();
    ARTSet15initialise();
    ARTSet33initialise();
    ARTSet5initialise();
    ARTSet17initialise();
    ARTSet27initialise();
    ARTSet21initialise();
    ARTSet7initialise();
    ARTSet36initialise();
    ARTSet38initialise();
    ARTSet9initialise();
    ARTSet13initialise();
    ARTSet24initialise();
    ARTSet4initialise();
    ARTSet8initialise();
    ARTSet10initialise();
    ARTSet26initialise();
    ARTSet11initialise();
  }

  public void artTableInitialiser_ART_INTEGER() {
    artLabelInternalStrings[ARTL_ART_INTEGER] = "INTEGER";
    artLabelStrings[ARTL_ART_INTEGER] = "INTEGER";
    artKindOfs[ARTL_ART_INTEGER] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_INTEGER_182] = "INTEGER ::= . &INTEGER  ";
    artLabelStrings[ARTL_ART_INTEGER_182] = "";
    artlhsL[ARTL_ART_INTEGER_182] = ARTL_ART_INTEGER;
    artKindOfs[ARTL_ART_INTEGER_182] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ART_INTEGER_182] = true;
    artLabelInternalStrings[ARTL_ART_INTEGER_183] = "INTEGER ::= &INTEGER  ";
    artLabelStrings[ARTL_ART_INTEGER_183] = "";
    artlhsL[ARTL_ART_INTEGER_183] = ARTL_ART_INTEGER;
    artPopD[ARTL_ART_INTEGER_183] = true;
    artLabelInternalStrings[ARTL_ART_INTEGER_184] = "INTEGER ::= &INTEGER  .";
    artLabelStrings[ARTL_ART_INTEGER_184] = "";
    artlhsL[ARTL_ART_INTEGER_184] = ARTL_ART_INTEGER;
    artKindOfs[ARTL_ART_INTEGER_184] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_INTEGER_184] = true;
    arteoR_pL[ARTL_ART_INTEGER_184] = true;
    artPopD[ARTL_ART_INTEGER_184] = true;
  }

  public void artTableInitialiser_ART_STRING_DQ() {
    artLabelInternalStrings[ARTL_ART_STRING_DQ] = "STRING_DQ";
    artLabelStrings[ARTL_ART_STRING_DQ] = "STRING_DQ";
    artKindOfs[ARTL_ART_STRING_DQ] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_STRING_DQ_38] = "STRING_DQ ::= . &STRING_DQ  ";
    artLabelStrings[ARTL_ART_STRING_DQ_38] = "";
    artlhsL[ARTL_ART_STRING_DQ_38] = ARTL_ART_STRING_DQ;
    artKindOfs[ARTL_ART_STRING_DQ_38] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ART_STRING_DQ_38] = true;
    artLabelInternalStrings[ARTL_ART_STRING_DQ_39] = "STRING_DQ ::= &STRING_DQ  ";
    artLabelStrings[ARTL_ART_STRING_DQ_39] = "";
    artlhsL[ARTL_ART_STRING_DQ_39] = ARTL_ART_STRING_DQ;
    artPopD[ARTL_ART_STRING_DQ_39] = true;
    artLabelInternalStrings[ARTL_ART_STRING_DQ_40] = "STRING_DQ ::= &STRING_DQ  .";
    artLabelStrings[ARTL_ART_STRING_DQ_40] = "";
    artlhsL[ARTL_ART_STRING_DQ_40] = ARTL_ART_STRING_DQ;
    artKindOfs[ARTL_ART_STRING_DQ_40] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_STRING_DQ_40] = true;
    arteoR_pL[ARTL_ART_STRING_DQ_40] = true;
    artPopD[ARTL_ART_STRING_DQ_40] = true;
  }

  public void artTableInitialiser_ART_e0() {
    artLabelInternalStrings[ARTL_ART_e0] = "e0";
    artLabelStrings[ARTL_ART_e0] = "e0";
    artKindOfs[ARTL_ART_e0] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_e0_42] = "e0 ::= . e1 ";
    artLabelStrings[ARTL_ART_e0_42] = "";
    artlhsL[ARTL_ART_e0_42] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_42] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_44] = "e0 ::= e1 .";
    artLabelStrings[ARTL_ART_e0_44] = "";
    artlhsL[ARTL_ART_e0_44] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_44] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_44] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e0_44] = true;
    arteoR_pL[ARTL_ART_e0_44] = true;
    artPopD[ARTL_ART_e0_44] = true;
    artLabelInternalStrings[ARTL_ART_e0_46] = "e0 ::= . e1 '>'  e1 ";
    artLabelStrings[ARTL_ART_e0_46] = "";
    artlhsL[ARTL_ART_e0_46] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_46] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_48] = "e0 ::= e1 . '>'  e1 ";
    artLabelStrings[ARTL_ART_e0_48] = "";
    artlhsL[ARTL_ART_e0_48] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_48] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_48] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e0_48] = true;
    artLabelInternalStrings[ARTL_ART_e0_49] = "e0 ::= e1 '>'  e1 ";
    artLabelStrings[ARTL_ART_e0_49] = "";
    artlhsL[ARTL_ART_e0_49] = ARTL_ART_e0;
    artLabelInternalStrings[ARTL_ART_e0_50] = "e0 ::= e1 '>'  . e1 ";
    artLabelStrings[ARTL_ART_e0_50] = "";
    artlhsL[ARTL_ART_e0_50] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_50] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_52] = "e0 ::= e1 '>'  e1 .";
    artLabelStrings[ARTL_ART_e0_52] = "";
    artlhsL[ARTL_ART_e0_52] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_52] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_52] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e0_52] = true;
    arteoR_pL[ARTL_ART_e0_52] = true;
    artPopD[ARTL_ART_e0_52] = true;
    artLabelInternalStrings[ARTL_ART_e0_54] = "e0 ::= . e1 '<'  e1 ";
    artLabelStrings[ARTL_ART_e0_54] = "";
    artlhsL[ARTL_ART_e0_54] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_54] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_56] = "e0 ::= e1 . '<'  e1 ";
    artLabelStrings[ARTL_ART_e0_56] = "";
    artlhsL[ARTL_ART_e0_56] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_56] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_56] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e0_56] = true;
    artLabelInternalStrings[ARTL_ART_e0_57] = "e0 ::= e1 '<'  e1 ";
    artLabelStrings[ARTL_ART_e0_57] = "";
    artlhsL[ARTL_ART_e0_57] = ARTL_ART_e0;
    artLabelInternalStrings[ARTL_ART_e0_58] = "e0 ::= e1 '<'  . e1 ";
    artLabelStrings[ARTL_ART_e0_58] = "";
    artlhsL[ARTL_ART_e0_58] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_58] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_60] = "e0 ::= e1 '<'  e1 .";
    artLabelStrings[ARTL_ART_e0_60] = "";
    artlhsL[ARTL_ART_e0_60] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_60] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_60] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e0_60] = true;
    arteoR_pL[ARTL_ART_e0_60] = true;
    artPopD[ARTL_ART_e0_60] = true;
    artLabelInternalStrings[ARTL_ART_e0_62] = "e0 ::= . e1 '>='  e1 ";
    artLabelStrings[ARTL_ART_e0_62] = "";
    artlhsL[ARTL_ART_e0_62] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_62] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_64] = "e0 ::= e1 . '>='  e1 ";
    artLabelStrings[ARTL_ART_e0_64] = "";
    artlhsL[ARTL_ART_e0_64] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_64] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_64] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e0_64] = true;
    artLabelInternalStrings[ARTL_ART_e0_65] = "e0 ::= e1 '>='  e1 ";
    artLabelStrings[ARTL_ART_e0_65] = "";
    artlhsL[ARTL_ART_e0_65] = ARTL_ART_e0;
    artLabelInternalStrings[ARTL_ART_e0_66] = "e0 ::= e1 '>='  . e1 ";
    artLabelStrings[ARTL_ART_e0_66] = "";
    artlhsL[ARTL_ART_e0_66] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_66] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_68] = "e0 ::= e1 '>='  e1 .";
    artLabelStrings[ARTL_ART_e0_68] = "";
    artlhsL[ARTL_ART_e0_68] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_68] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_68] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e0_68] = true;
    arteoR_pL[ARTL_ART_e0_68] = true;
    artPopD[ARTL_ART_e0_68] = true;
    artLabelInternalStrings[ARTL_ART_e0_70] = "e0 ::= . e1 '<='  e1 ";
    artLabelStrings[ARTL_ART_e0_70] = "";
    artlhsL[ARTL_ART_e0_70] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_70] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_72] = "e0 ::= e1 . '<='  e1 ";
    artLabelStrings[ARTL_ART_e0_72] = "";
    artlhsL[ARTL_ART_e0_72] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_72] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_72] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e0_72] = true;
    artLabelInternalStrings[ARTL_ART_e0_73] = "e0 ::= e1 '<='  e1 ";
    artLabelStrings[ARTL_ART_e0_73] = "";
    artlhsL[ARTL_ART_e0_73] = ARTL_ART_e0;
    artLabelInternalStrings[ARTL_ART_e0_74] = "e0 ::= e1 '<='  . e1 ";
    artLabelStrings[ARTL_ART_e0_74] = "";
    artlhsL[ARTL_ART_e0_74] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_74] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_76] = "e0 ::= e1 '<='  e1 .";
    artLabelStrings[ARTL_ART_e0_76] = "";
    artlhsL[ARTL_ART_e0_76] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_76] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_76] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e0_76] = true;
    arteoR_pL[ARTL_ART_e0_76] = true;
    artPopD[ARTL_ART_e0_76] = true;
    artLabelInternalStrings[ARTL_ART_e0_78] = "e0 ::= . e1 '=='  e1 ";
    artLabelStrings[ARTL_ART_e0_78] = "";
    artlhsL[ARTL_ART_e0_78] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_78] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_80] = "e0 ::= e1 . '=='  e1 ";
    artLabelStrings[ARTL_ART_e0_80] = "";
    artlhsL[ARTL_ART_e0_80] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_80] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_80] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e0_80] = true;
    artLabelInternalStrings[ARTL_ART_e0_81] = "e0 ::= e1 '=='  e1 ";
    artLabelStrings[ARTL_ART_e0_81] = "";
    artlhsL[ARTL_ART_e0_81] = ARTL_ART_e0;
    artLabelInternalStrings[ARTL_ART_e0_82] = "e0 ::= e1 '=='  . e1 ";
    artLabelStrings[ARTL_ART_e0_82] = "";
    artlhsL[ARTL_ART_e0_82] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_82] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_84] = "e0 ::= e1 '=='  e1 .";
    artLabelStrings[ARTL_ART_e0_84] = "";
    artlhsL[ARTL_ART_e0_84] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_84] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_84] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e0_84] = true;
    arteoR_pL[ARTL_ART_e0_84] = true;
    artPopD[ARTL_ART_e0_84] = true;
    artLabelInternalStrings[ARTL_ART_e0_86] = "e0 ::= . e1 '!='  e1 ";
    artLabelStrings[ARTL_ART_e0_86] = "";
    artlhsL[ARTL_ART_e0_86] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_86] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_88] = "e0 ::= e1 . '!='  e1 ";
    artLabelStrings[ARTL_ART_e0_88] = "";
    artlhsL[ARTL_ART_e0_88] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_88] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_88] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e0_88] = true;
    artLabelInternalStrings[ARTL_ART_e0_89] = "e0 ::= e1 '!='  e1 ";
    artLabelStrings[ARTL_ART_e0_89] = "";
    artlhsL[ARTL_ART_e0_89] = ARTL_ART_e0;
    artLabelInternalStrings[ARTL_ART_e0_90] = "e0 ::= e1 '!='  . e1 ";
    artLabelStrings[ARTL_ART_e0_90] = "";
    artlhsL[ARTL_ART_e0_90] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_e0_90] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e0_92] = "e0 ::= e1 '!='  e1 .";
    artLabelStrings[ARTL_ART_e0_92] = "";
    artlhsL[ARTL_ART_e0_92] = ARTL_ART_e0;
    artSlotInstanceOfs[ARTL_ART_e0_92] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e0_92] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e0_92] = true;
    arteoR_pL[ARTL_ART_e0_92] = true;
    artPopD[ARTL_ART_e0_92] = true;
  }

  public void artTableInitialiser_ART_e1() {
    artLabelInternalStrings[ARTL_ART_e1] = "e1";
    artLabelStrings[ARTL_ART_e1] = "e1";
    artKindOfs[ARTL_ART_e1] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_e1_94] = "e1 ::= . e2 ";
    artLabelStrings[ARTL_ART_e1_94] = "";
    artlhsL[ARTL_ART_e1_94] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e1_94] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e1_96] = "e1 ::= e2 .";
    artLabelStrings[ARTL_ART_e1_96] = "";
    artlhsL[ARTL_ART_e1_96] = ARTL_ART_e1;
    artSlotInstanceOfs[ARTL_ART_e1_96] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e1_96] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e1_96] = true;
    arteoR_pL[ARTL_ART_e1_96] = true;
    artPopD[ARTL_ART_e1_96] = true;
    artLabelInternalStrings[ARTL_ART_e1_98] = "e1 ::= . e1 '+'  e2 ";
    artLabelStrings[ARTL_ART_e1_98] = "";
    artlhsL[ARTL_ART_e1_98] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e1_98] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e1_100] = "e1 ::= e1 . '+'  e2 ";
    artLabelStrings[ARTL_ART_e1_100] = "";
    artlhsL[ARTL_ART_e1_100] = ARTL_ART_e1;
    artSlotInstanceOfs[ARTL_ART_e1_100] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e1_100] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e1_100] = true;
    artLabelInternalStrings[ARTL_ART_e1_101] = "e1 ::= e1 '+'  e2 ";
    artLabelStrings[ARTL_ART_e1_101] = "";
    artlhsL[ARTL_ART_e1_101] = ARTL_ART_e1;
    artLabelInternalStrings[ARTL_ART_e1_102] = "e1 ::= e1 '+'  . e2 ";
    artLabelStrings[ARTL_ART_e1_102] = "";
    artlhsL[ARTL_ART_e1_102] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e1_102] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e1_104] = "e1 ::= e1 '+'  e2 .";
    artLabelStrings[ARTL_ART_e1_104] = "";
    artlhsL[ARTL_ART_e1_104] = ARTL_ART_e1;
    artSlotInstanceOfs[ARTL_ART_e1_104] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e1_104] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e1_104] = true;
    arteoR_pL[ARTL_ART_e1_104] = true;
    artPopD[ARTL_ART_e1_104] = true;
    artLabelInternalStrings[ARTL_ART_e1_106] = "e1 ::= . e1 '-'  e2 ";
    artLabelStrings[ARTL_ART_e1_106] = "";
    artlhsL[ARTL_ART_e1_106] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e1_106] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e1_108] = "e1 ::= e1 . '-'  e2 ";
    artLabelStrings[ARTL_ART_e1_108] = "";
    artlhsL[ARTL_ART_e1_108] = ARTL_ART_e1;
    artSlotInstanceOfs[ARTL_ART_e1_108] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e1_108] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e1_108] = true;
    artLabelInternalStrings[ARTL_ART_e1_109] = "e1 ::= e1 '-'  e2 ";
    artLabelStrings[ARTL_ART_e1_109] = "";
    artlhsL[ARTL_ART_e1_109] = ARTL_ART_e1;
    artLabelInternalStrings[ARTL_ART_e1_110] = "e1 ::= e1 '-'  . e2 ";
    artLabelStrings[ARTL_ART_e1_110] = "";
    artlhsL[ARTL_ART_e1_110] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e1_110] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e1_112] = "e1 ::= e1 '-'  e2 .";
    artLabelStrings[ARTL_ART_e1_112] = "";
    artlhsL[ARTL_ART_e1_112] = ARTL_ART_e1;
    artSlotInstanceOfs[ARTL_ART_e1_112] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e1_112] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e1_112] = true;
    arteoR_pL[ARTL_ART_e1_112] = true;
    artPopD[ARTL_ART_e1_112] = true;
  }

  public void artTableInitialiser_ART_e2() {
    artLabelInternalStrings[ARTL_ART_e2] = "e2";
    artLabelStrings[ARTL_ART_e2] = "e2";
    artKindOfs[ARTL_ART_e2] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_e2_114] = "e2 ::= . e3 ";
    artLabelStrings[ARTL_ART_e2_114] = "";
    artlhsL[ARTL_ART_e2_114] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_114] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e2_116] = "e2 ::= e3 .";
    artLabelStrings[ARTL_ART_e2_116] = "";
    artlhsL[ARTL_ART_e2_116] = ARTL_ART_e2;
    artSlotInstanceOfs[ARTL_ART_e2_116] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e2_116] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e2_116] = true;
    arteoR_pL[ARTL_ART_e2_116] = true;
    artPopD[ARTL_ART_e2_116] = true;
    artLabelInternalStrings[ARTL_ART_e2_118] = "e2 ::= . e2 '*'  e3 ";
    artLabelStrings[ARTL_ART_e2_118] = "";
    artlhsL[ARTL_ART_e2_118] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_118] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e2_120] = "e2 ::= e2 . '*'  e3 ";
    artLabelStrings[ARTL_ART_e2_120] = "";
    artlhsL[ARTL_ART_e2_120] = ARTL_ART_e2;
    artSlotInstanceOfs[ARTL_ART_e2_120] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_120] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e2_120] = true;
    artLabelInternalStrings[ARTL_ART_e2_121] = "e2 ::= e2 '*'  e3 ";
    artLabelStrings[ARTL_ART_e2_121] = "";
    artlhsL[ARTL_ART_e2_121] = ARTL_ART_e2;
    artLabelInternalStrings[ARTL_ART_e2_122] = "e2 ::= e2 '*'  . e3 ";
    artLabelStrings[ARTL_ART_e2_122] = "";
    artlhsL[ARTL_ART_e2_122] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_122] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e2_124] = "e2 ::= e2 '*'  e3 .";
    artLabelStrings[ARTL_ART_e2_124] = "";
    artlhsL[ARTL_ART_e2_124] = ARTL_ART_e2;
    artSlotInstanceOfs[ARTL_ART_e2_124] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e2_124] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e2_124] = true;
    arteoR_pL[ARTL_ART_e2_124] = true;
    artPopD[ARTL_ART_e2_124] = true;
    artLabelInternalStrings[ARTL_ART_e2_126] = "e2 ::= . e2 '/'  e3 ";
    artLabelStrings[ARTL_ART_e2_126] = "";
    artlhsL[ARTL_ART_e2_126] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_126] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e2_128] = "e2 ::= e2 . '/'  e3 ";
    artLabelStrings[ARTL_ART_e2_128] = "";
    artlhsL[ARTL_ART_e2_128] = ARTL_ART_e2;
    artSlotInstanceOfs[ARTL_ART_e2_128] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_128] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e2_128] = true;
    artLabelInternalStrings[ARTL_ART_e2_129] = "e2 ::= e2 '/'  e3 ";
    artLabelStrings[ARTL_ART_e2_129] = "";
    artlhsL[ARTL_ART_e2_129] = ARTL_ART_e2;
    artLabelInternalStrings[ARTL_ART_e2_130] = "e2 ::= e2 '/'  . e3 ";
    artLabelStrings[ARTL_ART_e2_130] = "";
    artlhsL[ARTL_ART_e2_130] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_130] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e2_132] = "e2 ::= e2 '/'  e3 .";
    artLabelStrings[ARTL_ART_e2_132] = "";
    artlhsL[ARTL_ART_e2_132] = ARTL_ART_e2;
    artSlotInstanceOfs[ARTL_ART_e2_132] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e2_132] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e2_132] = true;
    arteoR_pL[ARTL_ART_e2_132] = true;
    artPopD[ARTL_ART_e2_132] = true;
    artLabelInternalStrings[ARTL_ART_e2_134] = "e2 ::= . e2 '%'  e3 ";
    artLabelStrings[ARTL_ART_e2_134] = "";
    artlhsL[ARTL_ART_e2_134] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_134] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e2_136] = "e2 ::= e2 . '%'  e3 ";
    artLabelStrings[ARTL_ART_e2_136] = "";
    artlhsL[ARTL_ART_e2_136] = ARTL_ART_e2;
    artSlotInstanceOfs[ARTL_ART_e2_136] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_136] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e2_136] = true;
    artLabelInternalStrings[ARTL_ART_e2_137] = "e2 ::= e2 '%'  e3 ";
    artLabelStrings[ARTL_ART_e2_137] = "";
    artlhsL[ARTL_ART_e2_137] = ARTL_ART_e2;
    artLabelInternalStrings[ARTL_ART_e2_138] = "e2 ::= e2 '%'  . e3 ";
    artLabelStrings[ARTL_ART_e2_138] = "";
    artlhsL[ARTL_ART_e2_138] = ARTL_ART_e2;
    artKindOfs[ARTL_ART_e2_138] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e2_140] = "e2 ::= e2 '%'  e3 .";
    artLabelStrings[ARTL_ART_e2_140] = "";
    artlhsL[ARTL_ART_e2_140] = ARTL_ART_e2;
    artSlotInstanceOfs[ARTL_ART_e2_140] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e2_140] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e2_140] = true;
    arteoR_pL[ARTL_ART_e2_140] = true;
    artPopD[ARTL_ART_e2_140] = true;
  }

  public void artTableInitialiser_ART_e3() {
    artLabelInternalStrings[ARTL_ART_e3] = "e3";
    artLabelStrings[ARTL_ART_e3] = "e3";
    artKindOfs[ARTL_ART_e3] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_e3_142] = "e3 ::= . e4 ";
    artLabelStrings[ARTL_ART_e3_142] = "";
    artlhsL[ARTL_ART_e3_142] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e3_142] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e3_144] = "e3 ::= e4 .";
    artLabelStrings[ARTL_ART_e3_144] = "";
    artlhsL[ARTL_ART_e3_144] = ARTL_ART_e3;
    artSlotInstanceOfs[ARTL_ART_e3_144] = ARTL_ART_e4;
    artKindOfs[ARTL_ART_e3_144] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e3_144] = true;
    arteoR_pL[ARTL_ART_e3_144] = true;
    artPopD[ARTL_ART_e3_144] = true;
    artLabelInternalStrings[ARTL_ART_e3_146] = "e3 ::= . '+'  e3 ";
    artLabelStrings[ARTL_ART_e3_146] = "";
    artlhsL[ARTL_ART_e3_146] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e3_146] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e3_147] = "e3 ::= '+'  e3 ";
    artLabelStrings[ARTL_ART_e3_147] = "";
    artlhsL[ARTL_ART_e3_147] = ARTL_ART_e3;
    artLabelInternalStrings[ARTL_ART_e3_148] = "e3 ::= '+'  . e3 ";
    artLabelStrings[ARTL_ART_e3_148] = "";
    artlhsL[ARTL_ART_e3_148] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e3_148] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e3_148] = true;
    artLabelInternalStrings[ARTL_ART_e3_150] = "e3 ::= '+'  e3 .";
    artLabelStrings[ARTL_ART_e3_150] = "";
    artlhsL[ARTL_ART_e3_150] = ARTL_ART_e3;
    artSlotInstanceOfs[ARTL_ART_e3_150] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e3_150] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e3_150] = true;
    arteoR_pL[ARTL_ART_e3_150] = true;
    artPopD[ARTL_ART_e3_150] = true;
    artLabelInternalStrings[ARTL_ART_e3_152] = "e3 ::= . '-'  e3 ";
    artLabelStrings[ARTL_ART_e3_152] = "";
    artlhsL[ARTL_ART_e3_152] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e3_152] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e3_153] = "e3 ::= '-'  e3 ";
    artLabelStrings[ARTL_ART_e3_153] = "";
    artlhsL[ARTL_ART_e3_153] = ARTL_ART_e3;
    artLabelInternalStrings[ARTL_ART_e3_154] = "e3 ::= '-'  . e3 ";
    artLabelStrings[ARTL_ART_e3_154] = "";
    artlhsL[ARTL_ART_e3_154] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e3_154] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e3_154] = true;
    artLabelInternalStrings[ARTL_ART_e3_156] = "e3 ::= '-'  e3 .";
    artLabelStrings[ARTL_ART_e3_156] = "";
    artlhsL[ARTL_ART_e3_156] = ARTL_ART_e3;
    artSlotInstanceOfs[ARTL_ART_e3_156] = ARTL_ART_e3;
    artKindOfs[ARTL_ART_e3_156] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e3_156] = true;
    arteoR_pL[ARTL_ART_e3_156] = true;
    artPopD[ARTL_ART_e3_156] = true;
  }

  public void artTableInitialiser_ART_e4() {
    artLabelInternalStrings[ARTL_ART_e4] = "e4";
    artLabelStrings[ARTL_ART_e4] = "e4";
    artKindOfs[ARTL_ART_e4] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_e4_158] = "e4 ::= . e5 ";
    artLabelStrings[ARTL_ART_e4_158] = "";
    artlhsL[ARTL_ART_e4_158] = ARTL_ART_e4;
    artKindOfs[ARTL_ART_e4_158] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e4_160] = "e4 ::= e5 .";
    artLabelStrings[ARTL_ART_e4_160] = "";
    artlhsL[ARTL_ART_e4_160] = ARTL_ART_e4;
    artSlotInstanceOfs[ARTL_ART_e4_160] = ARTL_ART_e5;
    artKindOfs[ARTL_ART_e4_160] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e4_160] = true;
    arteoR_pL[ARTL_ART_e4_160] = true;
    artPopD[ARTL_ART_e4_160] = true;
    artLabelInternalStrings[ARTL_ART_e4_162] = "e4 ::= . e5 '**'  e4 ";
    artLabelStrings[ARTL_ART_e4_162] = "";
    artlhsL[ARTL_ART_e4_162] = ARTL_ART_e4;
    artKindOfs[ARTL_ART_e4_162] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e4_164] = "e4 ::= e5 . '**'  e4 ";
    artLabelStrings[ARTL_ART_e4_164] = "";
    artlhsL[ARTL_ART_e4_164] = ARTL_ART_e4;
    artSlotInstanceOfs[ARTL_ART_e4_164] = ARTL_ART_e5;
    artKindOfs[ARTL_ART_e4_164] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e4_164] = true;
    artLabelInternalStrings[ARTL_ART_e4_165] = "e4 ::= e5 '**'  e4 ";
    artLabelStrings[ARTL_ART_e4_165] = "";
    artlhsL[ARTL_ART_e4_165] = ARTL_ART_e4;
    artLabelInternalStrings[ARTL_ART_e4_166] = "e4 ::= e5 '**'  . e4 ";
    artLabelStrings[ARTL_ART_e4_166] = "";
    artlhsL[ARTL_ART_e4_166] = ARTL_ART_e4;
    artKindOfs[ARTL_ART_e4_166] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e4_168] = "e4 ::= e5 '**'  e4 .";
    artLabelStrings[ARTL_ART_e4_168] = "";
    artlhsL[ARTL_ART_e4_168] = ARTL_ART_e4;
    artSlotInstanceOfs[ARTL_ART_e4_168] = ARTL_ART_e4;
    artKindOfs[ARTL_ART_e4_168] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e4_168] = true;
    arteoR_pL[ARTL_ART_e4_168] = true;
    artPopD[ARTL_ART_e4_168] = true;
  }

  public void artTableInitialiser_ART_e5() {
    artLabelInternalStrings[ARTL_ART_e5] = "e5";
    artLabelStrings[ARTL_ART_e5] = "e5";
    artKindOfs[ARTL_ART_e5] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_e5_170] = "e5 ::= . INTEGER ";
    artLabelStrings[ARTL_ART_e5_170] = "";
    artlhsL[ARTL_ART_e5_170] = ARTL_ART_e5;
    artKindOfs[ARTL_ART_e5_170] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e5_172] = "e5 ::= INTEGER .";
    artLabelStrings[ARTL_ART_e5_172] = "";
    artlhsL[ARTL_ART_e5_172] = ARTL_ART_e5;
    artSlotInstanceOfs[ARTL_ART_e5_172] = ARTL_ART_INTEGER;
    artKindOfs[ARTL_ART_e5_172] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e5_172] = true;
    arteoR_pL[ARTL_ART_e5_172] = true;
    artPopD[ARTL_ART_e5_172] = true;
    artLabelInternalStrings[ARTL_ART_e5_174] = "e5 ::= . '('  e1 ')'  ";
    artLabelStrings[ARTL_ART_e5_174] = "";
    artlhsL[ARTL_ART_e5_174] = ARTL_ART_e5;
    artKindOfs[ARTL_ART_e5_174] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_e5_175] = "e5 ::= '('  e1 ')'  ";
    artLabelStrings[ARTL_ART_e5_175] = "";
    artlhsL[ARTL_ART_e5_175] = ARTL_ART_e5;
    artLabelInternalStrings[ARTL_ART_e5_176] = "e5 ::= '('  . e1 ')'  ";
    artLabelStrings[ARTL_ART_e5_176] = "";
    artlhsL[ARTL_ART_e5_176] = ARTL_ART_e5;
    artKindOfs[ARTL_ART_e5_176] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_e5_176] = true;
    artLabelInternalStrings[ARTL_ART_e5_178] = "e5 ::= '('  e1 . ')'  ";
    artLabelStrings[ARTL_ART_e5_178] = "";
    artlhsL[ARTL_ART_e5_178] = ARTL_ART_e5;
    artSlotInstanceOfs[ARTL_ART_e5_178] = ARTL_ART_e1;
    artKindOfs[ARTL_ART_e5_178] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ART_e5_178] = true;
    artLabelInternalStrings[ARTL_ART_e5_179] = "e5 ::= '('  e1 ')'  ";
    artLabelStrings[ARTL_ART_e5_179] = "";
    artlhsL[ARTL_ART_e5_179] = ARTL_ART_e5;
    artPopD[ARTL_ART_e5_179] = true;
    artLabelInternalStrings[ARTL_ART_e5_180] = "e5 ::= '('  e1 ')'  .";
    artLabelStrings[ARTL_ART_e5_180] = "";
    artlhsL[ARTL_ART_e5_180] = ARTL_ART_e5;
    artKindOfs[ARTL_ART_e5_180] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_e5_180] = true;
    arteoR_pL[ARTL_ART_e5_180] = true;
    artPopD[ARTL_ART_e5_180] = true;
  }

  public void artTableInitialiser_ART_printElements() {
    artLabelInternalStrings[ARTL_ART_printElements] = "printElements";
    artLabelStrings[ARTL_ART_printElements] = "printElements";
    artKindOfs[ARTL_ART_printElements] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_printElements_14] = "printElements ::= . STRING_DQ ";
    artLabelStrings[ARTL_ART_printElements_14] = "";
    artlhsL[ARTL_ART_printElements_14] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_14] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_printElements_16] = "printElements ::= STRING_DQ .";
    artLabelStrings[ARTL_ART_printElements_16] = "";
    artlhsL[ARTL_ART_printElements_16] = ARTL_ART_printElements;
    artSlotInstanceOfs[ARTL_ART_printElements_16] = ARTL_ART_STRING_DQ;
    artKindOfs[ARTL_ART_printElements_16] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_printElements_16] = true;
    arteoR_pL[ARTL_ART_printElements_16] = true;
    artPopD[ARTL_ART_printElements_16] = true;
    artLabelInternalStrings[ARTL_ART_printElements_18] = "printElements ::= . STRING_DQ ','  printElements ";
    artLabelStrings[ARTL_ART_printElements_18] = "";
    artlhsL[ARTL_ART_printElements_18] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_18] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_printElements_20] = "printElements ::= STRING_DQ . ','  printElements ";
    artLabelStrings[ARTL_ART_printElements_20] = "";
    artlhsL[ARTL_ART_printElements_20] = ARTL_ART_printElements;
    artSlotInstanceOfs[ARTL_ART_printElements_20] = ARTL_ART_STRING_DQ;
    artKindOfs[ARTL_ART_printElements_20] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_printElements_20] = true;
    artLabelInternalStrings[ARTL_ART_printElements_21] = "printElements ::= STRING_DQ ','  printElements ";
    artLabelStrings[ARTL_ART_printElements_21] = "";
    artlhsL[ARTL_ART_printElements_21] = ARTL_ART_printElements;
    artLabelInternalStrings[ARTL_ART_printElements_22] = "printElements ::= STRING_DQ ','  . printElements ";
    artLabelStrings[ARTL_ART_printElements_22] = "";
    artlhsL[ARTL_ART_printElements_22] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_22] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_printElements_24] = "printElements ::= STRING_DQ ','  printElements .";
    artLabelStrings[ARTL_ART_printElements_24] = "";
    artlhsL[ARTL_ART_printElements_24] = ARTL_ART_printElements;
    artSlotInstanceOfs[ARTL_ART_printElements_24] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_24] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_printElements_24] = true;
    arteoR_pL[ARTL_ART_printElements_24] = true;
    artPopD[ARTL_ART_printElements_24] = true;
    artLabelInternalStrings[ARTL_ART_printElements_26] = "printElements ::= . e0 ";
    artLabelStrings[ARTL_ART_printElements_26] = "";
    artlhsL[ARTL_ART_printElements_26] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_26] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_printElements_28] = "printElements ::= e0 .";
    artLabelStrings[ARTL_ART_printElements_28] = "";
    artlhsL[ARTL_ART_printElements_28] = ARTL_ART_printElements;
    artSlotInstanceOfs[ARTL_ART_printElements_28] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_printElements_28] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_printElements_28] = true;
    arteoR_pL[ARTL_ART_printElements_28] = true;
    artPopD[ARTL_ART_printElements_28] = true;
    artLabelInternalStrings[ARTL_ART_printElements_30] = "printElements ::= . e0 ','  printElements ";
    artLabelStrings[ARTL_ART_printElements_30] = "";
    artlhsL[ARTL_ART_printElements_30] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_30] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_printElements_32] = "printElements ::= e0 . ','  printElements ";
    artLabelStrings[ARTL_ART_printElements_32] = "";
    artlhsL[ARTL_ART_printElements_32] = ARTL_ART_printElements;
    artSlotInstanceOfs[ARTL_ART_printElements_32] = ARTL_ART_e0;
    artKindOfs[ARTL_ART_printElements_32] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_printElements_32] = true;
    artLabelInternalStrings[ARTL_ART_printElements_33] = "printElements ::= e0 ','  printElements ";
    artLabelStrings[ARTL_ART_printElements_33] = "";
    artlhsL[ARTL_ART_printElements_33] = ARTL_ART_printElements;
    artLabelInternalStrings[ARTL_ART_printElements_34] = "printElements ::= e0 ','  . printElements ";
    artLabelStrings[ARTL_ART_printElements_34] = "";
    artlhsL[ARTL_ART_printElements_34] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_34] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_printElements_36] = "printElements ::= e0 ','  printElements .";
    artLabelStrings[ARTL_ART_printElements_36] = "";
    artlhsL[ARTL_ART_printElements_36] = ARTL_ART_printElements;
    artSlotInstanceOfs[ARTL_ART_printElements_36] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_printElements_36] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_printElements_36] = true;
    arteoR_pL[ARTL_ART_printElements_36] = true;
    artPopD[ARTL_ART_printElements_36] = true;
  }

  public void artTableInitialiser_ART_statement() {
    artLabelInternalStrings[ARTL_ART_statement] = "statement";
    artLabelStrings[ARTL_ART_statement] = "statement";
    artKindOfs[ARTL_ART_statement] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_ART_statement_2] = "statement ::= . 'print'  '('  printElements ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_2] = "";
    artlhsL[ARTL_ART_statement_2] = ARTL_ART_statement;
    artKindOfs[ARTL_ART_statement_2] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_statement_3] = "statement ::= 'print'  '('  printElements ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_3] = "";
    artlhsL[ARTL_ART_statement_3] = ARTL_ART_statement;
    artLabelInternalStrings[ARTL_ART_statement_4] = "statement ::= 'print'  . '('  printElements ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_4] = "";
    artlhsL[ARTL_ART_statement_4] = ARTL_ART_statement;
    artKindOfs[ARTL_ART_statement_4] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_ART_statement_4] = true;
    artLabelInternalStrings[ARTL_ART_statement_5] = "statement ::= 'print'  '('  printElements ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_5] = "";
    artlhsL[ARTL_ART_statement_5] = ARTL_ART_statement;
    artLabelInternalStrings[ARTL_ART_statement_6] = "statement ::= 'print'  '('  . printElements ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_6] = "";
    artlhsL[ARTL_ART_statement_6] = ARTL_ART_statement;
    artKindOfs[ARTL_ART_statement_6] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_ART_statement_8] = "statement ::= 'print'  '('  printElements . ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_8] = "";
    artlhsL[ARTL_ART_statement_8] = ARTL_ART_statement;
    artSlotInstanceOfs[ARTL_ART_statement_8] = ARTL_ART_printElements;
    artKindOfs[ARTL_ART_statement_8] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ART_statement_8] = true;
    artLabelInternalStrings[ARTL_ART_statement_9] = "statement ::= 'print'  '('  printElements ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_9] = "";
    artlhsL[ARTL_ART_statement_9] = ARTL_ART_statement;
    artPopD[ARTL_ART_statement_9] = true;
    artLabelInternalStrings[ARTL_ART_statement_10] = "statement ::= 'print'  '('  printElements ')'  . ';'  ";
    artLabelStrings[ARTL_ART_statement_10] = "";
    artlhsL[ARTL_ART_statement_10] = ARTL_ART_statement;
    artKindOfs[ARTL_ART_statement_10] = ARTK_INTERMEDIATE;
    artPopD[ARTL_ART_statement_10] = true;
    artLabelInternalStrings[ARTL_ART_statement_11] = "statement ::= 'print'  '('  printElements ')'  ';'  ";
    artLabelStrings[ARTL_ART_statement_11] = "";
    artlhsL[ARTL_ART_statement_11] = ARTL_ART_statement;
    artPopD[ARTL_ART_statement_11] = true;
    artLabelInternalStrings[ARTL_ART_statement_12] = "statement ::= 'print'  '('  printElements ')'  ';'  .";
    artLabelStrings[ARTL_ART_statement_12] = "";
    artlhsL[ARTL_ART_statement_12] = ARTL_ART_statement;
    artKindOfs[ARTL_ART_statement_12] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_ART_statement_12] = true;
    arteoR_pL[ARTL_ART_statement_12] = true;
    artPopD[ARTL_ART_statement_12] = true;
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

    artLabelStrings[ARTTB_INTEGER] = "INTEGER";
    artLabelInternalStrings[ARTTB_INTEGER] = "&INTEGER";
    artKindOfs[ARTTB_INTEGER] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_INTEGER] = true;
    artLabelStrings[ARTTB_SIMPLE_WHITESPACE] = "SIMPLE_WHITESPACE";
    artLabelInternalStrings[ARTTB_SIMPLE_WHITESPACE] = "&SIMPLE_WHITESPACE";
    artKindOfs[ARTTB_SIMPLE_WHITESPACE] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_SIMPLE_WHITESPACE] = true;
    artLabelStrings[ARTTB_STRING_DQ] = "STRING_DQ";
    artLabelInternalStrings[ARTTB_STRING_DQ] = "&STRING_DQ";
    artKindOfs[ARTTB_STRING_DQ] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_STRING_DQ] = true;
    artLabelStrings[ARTTS__SHREIK_EQUAL] = "!=";
    artLabelInternalStrings[ARTTS__SHREIK_EQUAL] = "'!='";
    artKindOfs[ARTTS__SHREIK_EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__SHREIK_EQUAL] = true;
    artLabelStrings[ARTTS__PERCENT] = "%";
    artLabelInternalStrings[ARTTS__PERCENT] = "'%'";
    artKindOfs[ARTTS__PERCENT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__PERCENT] = true;
    artLabelStrings[ARTTS__LPAR] = "(";
    artLabelInternalStrings[ARTTS__LPAR] = "'('";
    artKindOfs[ARTTS__LPAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__LPAR] = true;
    artLabelStrings[ARTTS__RPAR] = ")";
    artLabelInternalStrings[ARTTS__RPAR] = "')'";
    artKindOfs[ARTTS__RPAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__RPAR] = true;
    artLabelStrings[ARTTS__STAR] = "*";
    artLabelInternalStrings[ARTTS__STAR] = "'*'";
    artKindOfs[ARTTS__STAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__STAR] = true;
    artLabelStrings[ARTTS__STAR_STAR] = "**";
    artLabelInternalStrings[ARTTS__STAR_STAR] = "'**'";
    artKindOfs[ARTTS__STAR_STAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__STAR_STAR] = true;
    artLabelStrings[ARTTS__PLUS] = "+";
    artLabelInternalStrings[ARTTS__PLUS] = "'+'";
    artKindOfs[ARTTS__PLUS] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__PLUS] = true;
    artLabelStrings[ARTTS__COMMA] = ",";
    artLabelInternalStrings[ARTTS__COMMA] = "','";
    artKindOfs[ARTTS__COMMA] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__COMMA] = true;
    artLabelStrings[ARTTS__MINUS] = "-";
    artLabelInternalStrings[ARTTS__MINUS] = "'-'";
    artKindOfs[ARTTS__MINUS] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__MINUS] = true;
    artLabelStrings[ARTTS__SLASH] = "/";
    artLabelInternalStrings[ARTTS__SLASH] = "'/'";
    artKindOfs[ARTTS__SLASH] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__SLASH] = true;
    artLabelStrings[ARTTS__SEMICOLON] = ";";
    artLabelInternalStrings[ARTTS__SEMICOLON] = "';'";
    artKindOfs[ARTTS__SEMICOLON] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__SEMICOLON] = true;
    artLabelStrings[ARTTS__LT] = "<";
    artLabelInternalStrings[ARTTS__LT] = "'<'";
    artKindOfs[ARTTS__LT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__LT] = true;
    artLabelStrings[ARTTS__LT_EQUAL] = "<=";
    artLabelInternalStrings[ARTTS__LT_EQUAL] = "'<='";
    artKindOfs[ARTTS__LT_EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__LT_EQUAL] = true;
    artLabelStrings[ARTTS__EQUAL_EQUAL] = "==";
    artLabelInternalStrings[ARTTS__EQUAL_EQUAL] = "'=='";
    artKindOfs[ARTTS__EQUAL_EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__EQUAL_EQUAL] = true;
    artLabelStrings[ARTTS__GT] = ">";
    artLabelInternalStrings[ARTTS__GT] = "'>'";
    artKindOfs[ARTTS__GT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__GT] = true;
    artLabelStrings[ARTTS__GT_EQUAL] = ">=";
    artLabelInternalStrings[ARTTS__GT_EQUAL] = "'>='";
    artKindOfs[ARTTS__GT_EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__GT_EQUAL] = true;
    artLabelStrings[ARTTS_print] = "print";
    artLabelInternalStrings[ARTTS_print] = "'print'";
    artKindOfs[ARTTS_print] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_print] = true;
    artTableInitialiser_ART_INTEGER();
    artTableInitialiser_ART_STRING_DQ();
    artTableInitialiser_ART_e0();
    artTableInitialiser_ART_e1();
    artTableInitialiser_ART_e2();
    artTableInitialiser_ART_e3();
    artTableInitialiser_ART_e4();
    artTableInitialiser_ART_e5();
    artTableInitialiser_ART_printElements();
    artTableInitialiser_ART_statement();
  }

  public ARTGeneratedParser(ARTLexerV3 artLexer) {
    this(null, artLexer);
  }

  public ARTGeneratedParser(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
    super(artGrammar, artLexer);
    artParserKind = "GLL Gen";
    artFirstTerminalLabel = ARTTS__SHREIK_EQUAL;
    artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
    artSetExtent = 32;
    ARTL_EOS = ARTX_EOS;
    ARTL_EPSILON = ARTX_EPSILON;
    ARTL_DUMMY = ARTX_DUMMY;
    artGrammarKind = ARTModeGrammarKind.BNF;
    artDefaultStartSymbolLabel = ARTL_ART_statement;
    artBuildDirectives = "ARTDirectives [inputs=[], inputFilenames=[], directives={suppressPopGuard=false, tweLexicalisations=false, algorithmMode=gllGeneratorPool, tweLongest=false, tweSegments=false, sppfShortest=false, termWrite=false, tweCounts=false, clusteredGSS=false, twePrint=false, rewriteDisable=false, tweAmbiguityClasses=false, sppfAmbiguityAnalysis=false, rewriteConfiguration=false, outputDirectory=., inputCounts=false, twePriority=false, treeShow=false, tweRecursive=false, rewritePostorder=false, rewriteContractum=true, parseCounts=false, predictivePops=false, suppressProductionGuard=false, sppfDead=false, twePrintFull=false, input=0, tweExtents=false, suppressSemantics=false, despatchMode=fragment, treePrintLevel=3, sppfShowFull=false, treePrint=false, sppfChooseCounts=false, log=1, tweDump=false, sppfCycleDetect=false, sppfCountSentences=false, parserName=ARTGeneratedParser, rewriteResume=true, inputPrint=false, lexerName=ARTGeneratedLexer, trace=false, tweTokenWrite=false, tweDead=false, tweShortest=false, rewritePure=true, tweSelectOne=false, smlCycleBreak=false, termPrint=false, suppressTestRepeat=false, rewritePreorder=false, sppfAmbiguityAnalysisFull=false, tweFromSPPF=false, actionSuppress=false, tweLexicalisationsQuick=false, sppfPriority=false, sppfShow=false, rewriteOneStep=false, namespace=, sppfSelectOne=false, FIFODescriptors=false, sppfOrderedLongest=false, verbosity=0, sppfLongest=false, gssShow=false}]";
    artFIFODescriptors = false;
    artSetInitialise();
    artTableInitialise();
  }

  public void ARTRD_INTEGER(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* INTEGER ::= &INTEGER . */
        case ARTL_ART_INTEGER_184:
          ARTRD_INTEGER(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_STRING_DQ(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* STRING_DQ ::= &STRING_DQ . */
        case ARTL_ART_STRING_DQ_40:
          ARTRD_STRING_DQ(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_e0(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* e0 ::= e1 . */
        case ARTL_ART_e0_44:
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e0 ::= e1 '>' . e1 */
        case ARTL_ART_e0_50:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e0 ::= e1 '>' e1 . */
        case ARTL_ART_e0_52:
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e0 ::= e1 '<' . e1 */
        case ARTL_ART_e0_58:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e0 ::= e1 '<' e1 . */
        case ARTL_ART_e0_60:
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e0 ::= e1 '>=' . e1 */
        case ARTL_ART_e0_66:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e0 ::= e1 '>=' e1 . */
        case ARTL_ART_e0_68:
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e0 ::= e1 '<=' . e1 */
        case ARTL_ART_e0_74:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e0 ::= e1 '<=' e1 . */
        case ARTL_ART_e0_76:
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e0 ::= e1 '==' . e1 */
        case ARTL_ART_e0_82:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e0 ::= e1 '==' e1 . */
        case ARTL_ART_e0_84:
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e0 ::= e1 '!=' . e1 */
        case ARTL_ART_e0_90:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e0 ::= e1 '!=' e1 . */
        case ARTL_ART_e0_92:
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_e1(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* e1 ::= e2 . */
        case ARTL_ART_e1_96:
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e2(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e1 ::= e1 '+' . e2 */
        case ARTL_ART_e1_102:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e1 ::= e1 '+' e2 . */
        case ARTL_ART_e1_104:
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e2(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e1 ::= e1 '-' . e2 */
        case ARTL_ART_e1_110:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e1 ::= e1 '-' e2 . */
        case ARTL_ART_e1_112:
          ARTRD_e1(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e2(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_e2(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* e2 ::= e3 . */
        case ARTL_ART_e2_116:
          ARTRD_e2(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e3(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e2 ::= e2 '*' . e3 */
        case ARTL_ART_e2_122:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e2(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e2 ::= e2 '*' e3 . */
        case ARTL_ART_e2_124:
          ARTRD_e2(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e3(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e2 ::= e2 '/' . e3 */
        case ARTL_ART_e2_130:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e2(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e2 ::= e2 '/' e3 . */
        case ARTL_ART_e2_132:
          ARTRD_e2(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e3(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e2 ::= e2 '%' . e3 */
        case ARTL_ART_e2_138:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e2(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e2 ::= e2 '%' e3 . */
        case ARTL_ART_e2_140:
          ARTRD_e2(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e3(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_e3(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* e3 ::= e4 . */
        case ARTL_ART_e3_144:
          ARTRD_e3(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e4(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e3 ::= '+' e3 . */
        case ARTL_ART_e3_150:
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e3(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e3 ::= '-' e3 . */
        case ARTL_ART_e3_156:
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e3(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_e4(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* e4 ::= e5 . */
        case ARTL_ART_e4_160:
          ARTRD_e4(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e5(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e4 ::= e5 '**' . e4 */
        case ARTL_ART_e4_166:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e5(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* e4 ::= e5 '**' e4 . */
        case ARTL_ART_e4_168:
          ARTRD_e4(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e4(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_e5(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* e5 ::= INTEGER . */
        case ARTL_ART_e5_172:
          ARTRD_e5(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_INTEGER(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e5 ::= '(' e1 . ')' */
        case ARTL_ART_e5_178:
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e1(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* e5 ::= '(' e1 ')' . */
        case ARTL_ART_e5_180:
          ARTRD_e5(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_printElements(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* printElements ::= STRING_DQ . */
        case ARTL_ART_printElements_16:
          ARTRD_printElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_STRING_DQ(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* printElements ::= STRING_DQ ',' . printElements */
        case ARTL_ART_printElements_22:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_STRING_DQ(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* printElements ::= STRING_DQ ',' printElements . */
        case ARTL_ART_printElements_24:
          ARTRD_printElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_printElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* printElements ::= e0 . */
        case ARTL_ART_printElements_28:
          ARTRD_printElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_e0(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* printElements ::= e0 ',' . printElements */
        case ARTL_ART_printElements_34:
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          ARTRD_e0(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* printElements ::= e0 ',' printElements . */
        case ARTL_ART_printElements_36:
          ARTRD_printElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_printElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        default:
          ;
        }
      }
    }
  }

  public void ARTRD_statement(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) {
    ARTGLLRDTVertex artNewParent;
    boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
        /* statement ::= 'print' '(' . printElements ')' ';' */
        case ARTL_ART_statement_6:
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* statement ::= 'print' '(' printElements . ')' ';' */
        case ARTL_ART_statement_8:
          ARTRD_statement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artNewWriteable = true;
          artNewParent = artParent.addChild(artNextFreeNodeNumber++,
              new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
                  artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          ARTRD_printElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
          break;
        /* statement ::= 'print' '(' printElements ')' . ';' */
        case ARTL_ART_statement_10:
          ARTRD_statement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        /* statement ::= 'print' '(' printElements ')' ';' . */
        case ARTL_ART_statement_12:
          ARTRD_statement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
          artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)),
              artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
          break;
        default:
          ;
        }
      }
    }
  }

  @Override
  public void artEvaluate(ARTGLLRDTHandle artElement, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable) {
    switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ART_INTEGER:
      ARTRD_INTEGER(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_STRING_DQ:
      ARTRD_STRING_DQ(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_e0:
      ARTRD_e0(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_e1:
      ARTRD_e1(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_e2:
      ARTRD_e2(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_e3:
      ARTRD_e3(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_e4:
      ARTRD_e4(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_e5:
      ARTRD_e5(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_printElements:
      ARTRD_printElements(artElement.element, artParent, artWriteable);
      break;
    case ARTL_ART_statement:
      ARTRD_statement(artElement.element, artParent, artWriteable);
      break;
    }
  }

};
