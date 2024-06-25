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
* ARTeSOSParser.java
*
*******************************************************************************/
public class ARTeSOSParser extends ARTGLLParserHashPool {
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
  private static boolean[] ARTSet39;
  private static boolean[] ARTSet40;
  private static boolean[] ARTSet41;
  private static boolean[] ARTSet42;
  private static boolean[] ARTSet43;
  private static boolean[] ARTSet44;
  private static boolean[] ARTSet45;
  private static boolean[] ARTSet46;
  private static boolean[] ARTSet47;
  private static boolean[] ARTSet48;
  private static boolean[] ARTSet49;
  private static boolean[] ARTSet50;
  private static boolean[] ARTSet51;
  private static boolean[] ARTSet52;
  private static boolean[] ARTSet53;
  private static boolean[] ARTSet54;
  private static boolean[] ARTSet55;
  private static boolean[] ARTSet56;
  private static boolean[] ARTSet57;

  /* Start of artLabel enumeration */
  public static final int ARTX_EOS = 0;
  public static final int ARTTB_ID_SOS = 1;
  public static final int ARTTB_INTEGER = 2;
  public static final int ARTTB_REAL = 3;
  public static final int ARTTB_STRING_DQ = 4;
  public static final int ARTTB_STRING_SQ = 5;
  public static final int ARTTS__LPAR = 6;
  public static final int ARTTS__RPAR = 7;
  public static final int ARTTS__PLUS = 8;
  public static final int ARTTS__COMMA = 9;
  public static final int ARTTS__MINUS = 10;
  public static final int ARTTS__MINUS_MINUS_MINUS = 11;
  public static final int ARTTS__MINUS_GT = 12;
  public static final int ARTTS__MINUS_GT_STAR = 13;
  public static final int ARTTS__MINUS_GT_GT = 14;
  public static final int ARTTS__COLON = 15;
  public static final int ARTTS__LT = 16;
  public static final int ARTTS__EQUAL = 17;
  public static final int ARTTS__EQUAL_GT = 18;
  public static final int ARTTS__EQUAL_GT_STAR = 19;
  public static final int ARTTS__GT = 20;
  public static final int ARTTS_false = 21;
  public static final int ARTTS_latex = 22;
  public static final int ARTTS_listIn = 23;
  public static final int ARTTS_listOut = 24;
  public static final int ARTTS_map = 25;
  public static final int ARTTS_mapFixed = 26;
  public static final int ARTTS_priority = 27;
  public static final int ARTTS_relation = 28;
  public static final int ARTTS_singleton = 29;
  public static final int ARTTS_test = 30;
  public static final int ARTTS_true = 31;
  public static final int ARTTS_untyped = 32;
  public static final int ARTTS__BAR_GT = 33;
  public static final int ARTX_EPSILON = 34;
  public static final int ARTTN_ART_TFN_eSOS_COMMENT_LINE_C = 35;
  public static final int ARTTN_ART_TFN_eSOS_COMMENT_NEST_ART = 36;
  public static final int ARTTN_ART_TFN_eSOS_WHITESPACE = 37;
  public static final int ARTTB_COMMENT_LINE_C = 38;
  public static final int ARTTB_COMMENT_NEST_ART = 39;
  public static final int ARTTB_WHITESPACE = 40;
  public static final int ARTL_eSOS_BOOLEAN = 41;
  public static final int ARTL_eSOS_ID_SOS = 42;
  public static final int ARTL_eSOS_INTEGER = 43;
  public static final int ARTL_eSOS_REAL = 44;
  public static final int ARTL_eSOS_RELATION = 45;
  public static final int ARTL_eSOS_STRING_DQ = 46;
  public static final int ARTL_eSOS_STRING_SQ = 47;
  public static final int ARTL_eSOS_conditions = 48;
  public static final int ARTL_eSOS_configuration = 49;
  public static final int ARTL_eSOS_element = 50;
  public static final int ARTL_eSOS_entityReferences = 51;
  public static final int ARTL_eSOS_labelOpt = 52;
  public static final int ARTL_eSOS_latexElement = 53;
  public static final int ARTL_eSOS_latexElements = 54;
  public static final int ARTL_eSOS_priorities = 55;
  public static final int ARTL_eSOS_priority = 56;
  public static final int ARTL_eSOS_priorityOpt = 57;
  public static final int ARTL_eSOS_relationElement = 58;
  public static final int ARTL_eSOS_relationElements = 59;
  public static final int ARTL_eSOS_relationSymbol = 60;
  public static final int ARTL_eSOS_rule = 61;
  public static final int ARTL_eSOS_sideCondition = 62;
  public static final int ARTL_eSOS_specification = 63;
  public static final int ARTL_eSOS_subterms = 64;
  public static final int ARTL_eSOS_term = 65;
  public static final int ARTL_eSOS_termElement = 66;
  public static final int ARTL_eSOS_test = 67;
  public static final int ARTL_eSOS_transition = 68;
  public static final int ARTL_eSOS_BOOLEAN_514 = 69;
  public static final int ARTL_eSOS_BOOLEAN_516 = 70;
  public static final int ARTL_eSOS_BOOLEAN_515 = 71;
  public static final int ARTL_eSOS_BOOLEAN_520 = 72;
  public static final int ARTL_eSOS_BOOLEAN_522 = 73;
  public static final int ARTL_eSOS_BOOLEAN_521 = 74;
  public static final int ARTL_eSOS_ID_SOS_508 = 75;
  public static final int ARTL_eSOS_ID_SOS_510 = 76;
  public static final int ARTL_eSOS_ID_SOS_509 = 77;
  public static final int ARTL_eSOS_INTEGER_526 = 78;
  public static final int ARTL_eSOS_INTEGER_528 = 79;
  public static final int ARTL_eSOS_INTEGER_527 = 80;
  public static final int ARTL_eSOS_REAL_532 = 81;
  public static final int ARTL_eSOS_REAL_534 = 82;
  public static final int ARTL_eSOS_REAL_533 = 83;
  public static final int ARTL_eSOS_RELATION_482 = 84;
  public static final int ARTL_eSOS_RELATION_484 = 85;
  public static final int ARTL_eSOS_STRING_DQ_538 = 86;
  public static final int ARTL_eSOS_STRING_DQ_540 = 87;
  public static final int ARTL_eSOS_STRING_DQ_539 = 88;
  public static final int ARTL_eSOS_STRING_SQ_544 = 89;
  public static final int ARTL_eSOS_STRING_SQ_546 = 90;
  public static final int ARTL_eSOS_STRING_SQ_545 = 91;
  public static final int ARTL_eSOS_conditions_262 = 92;
  public static final int ARTL_eSOS_conditions_264 = 93;
  public static final int ARTL_eSOS_conditions_266 = 94;
  public static final int ARTL_eSOS_conditions_268 = 95;
  public static final int ARTL_eSOS_conditions_272 = 96;
  public static final int ARTL_eSOS_conditions_274 = 97;
  public static final int ARTL_eSOS_conditions_276 = 98;
  public static final int ARTL_eSOS_conditions_280 = 99;
  public static final int ARTL_eSOS_configuration_296 = 100;
  public static final int ARTL_eSOS_configuration_298 = 101;
  public static final int ARTL_eSOS_configuration_302 = 102;
  public static final int ARTL_eSOS_element_14 = 103;
  public static final int ARTL_eSOS_element_18 = 104;
  public static final int ARTL_eSOS_element_20 = 105;
  public static final int ARTL_eSOS_element_15 = 106;
  public static final int ARTL_eSOS_element_16 = 107;
  public static final int ARTL_eSOS_element_22 = 108;
  public static final int ARTL_eSOS_element_26 = 109;
  public static final int ARTL_eSOS_element_30 = 110;
  public static final int ARTL_eSOS_element_23 = 111;
  public static final int ARTL_eSOS_element_24 = 112;
  public static final int ARTL_eSOS_element_32 = 113;
  public static final int ARTL_eSOS_element_38 = 114;
  public static final int ARTL_eSOS_element_33 = 115;
  public static final int ARTL_eSOS_element_34 = 116;
  public static final int ARTL_eSOS_element_40 = 117;
  public static final int ARTL_eSOS_element_44 = 118;
  public static final int ARTL_eSOS_element_46 = 119;
  public static final int ARTL_eSOS_element_41 = 120;
  public static final int ARTL_eSOS_element_42 = 121;
  public static final int ARTL_eSOS_element_48 = 122;
  public static final int ARTL_eSOS_element_50 = 123;
  public static final int ARTL_eSOS_element_52 = 124;
  public static final int ARTL_eSOS_element_56 = 125;
  public static final int ARTL_eSOS_element_53 = 126;
  public static final int ARTL_eSOS_element_54 = 127;
  public static final int ARTL_eSOS_entityReferences_304 = 128;
  public static final int ARTL_eSOS_entityReferences_306 = 129;
  public static final int ARTL_eSOS_entityReferences_308 = 130;
  public static final int ARTL_eSOS_entityReferences_312 = 131;
  public static final int ARTL_eSOS_entityReferences_316 = 132;
  public static final int ARTL_eSOS_entityReferences_309 = 133;
  public static final int ARTL_eSOS_entityReferences_310 = 134;
  public static final int ARTL_eSOS_entityReferences_318 = 135;
  public static final int ARTL_eSOS_entityReferences_322 = 136;
  public static final int ARTL_eSOS_entityReferences_324 = 137;
  public static final int ARTL_eSOS_entityReferences_326 = 138;
  public static final int ARTL_eSOS_entityReferences_330 = 139;
  public static final int ARTL_eSOS_entityReferences_319 = 140;
  public static final int ARTL_eSOS_entityReferences_320 = 141;
  public static final int ARTL_eSOS_entityReferences_323 = 142;
  public static final int ARTL_eSOS_labelOpt_240 = 143;
  public static final int ARTL_eSOS_labelOpt_242 = 144;
  public static final int ARTL_eSOS_labelOpt_246 = 145;
  public static final int ARTL_eSOS_labelOpt_250 = 146;
  public static final int ARTL_eSOS_labelOpt_247 = 147;
  public static final int ARTL_eSOS_labelOpt_248 = 148;
  public static final int ARTL_eSOS_labelOpt_254 = 149;
  public static final int ARTL_eSOS_labelOpt_258 = 150;
  public static final int ARTL_eSOS_labelOpt_255 = 151;
  public static final int ARTL_eSOS_labelOpt_256 = 152;
  public static final int ARTL_eSOS_latexElement_70 = 153;
  public static final int ARTL_eSOS_latexElement_72 = 154;
  public static final int ARTL_eSOS_latexElement_74 = 155;
  public static final int ARTL_eSOS_latexElement_78 = 156;
  public static final int ARTL_eSOS_latexElement_80 = 157;
  public static final int ARTL_eSOS_latexElement_82 = 158;
  public static final int ARTL_eSOS_latexElements_58 = 159;
  public static final int ARTL_eSOS_latexElements_62 = 160;
  public static final int ARTL_eSOS_latexElements_64 = 161;
  public static final int ARTL_eSOS_latexElements_59 = 162;
  public static final int ARTL_eSOS_latexElements_60 = 163;
  public static final int ARTL_eSOS_latexElements_66 = 164;
  public static final int ARTL_eSOS_latexElements_68 = 165;
  public static final int ARTL_eSOS_priorities_168 = 166;
  public static final int ARTL_eSOS_priorities_172 = 167;
  public static final int ARTL_eSOS_priorities_174 = 168;
  public static final int ARTL_eSOS_priorities_169 = 169;
  public static final int ARTL_eSOS_priorities_170 = 170;
  public static final int ARTL_eSOS_priorities_176 = 171;
  public static final int ARTL_eSOS_priorities_178 = 172;
  public static final int ARTL_eSOS_priority_180 = 173;
  public static final int ARTL_eSOS_priority_182 = 174;
  public static final int ARTL_eSOS_priority_184 = 175;
  public static final int ARTL_eSOS_priority_186 = 176;
  public static final int ARTL_eSOS_priority_183 = 177;
  public static final int ARTL_eSOS_priority_190 = 178;
  public static final int ARTL_eSOS_priority_192 = 179;
  public static final int ARTL_eSOS_priority_194 = 180;
  public static final int ARTL_eSOS_priority_196 = 181;
  public static final int ARTL_eSOS_priority_193 = 182;
  public static final int ARTL_eSOS_priority_200 = 183;
  public static final int ARTL_eSOS_priority_202 = 184;
  public static final int ARTL_eSOS_priority_204 = 185;
  public static final int ARTL_eSOS_priority_206 = 186;
  public static final int ARTL_eSOS_priority_203 = 187;
  public static final int ARTL_eSOS_priorityOpt_226 = 188;
  public static final int ARTL_eSOS_priorityOpt_228 = 189;
  public static final int ARTL_eSOS_priorityOpt_232 = 190;
  public static final int ARTL_eSOS_priorityOpt_236 = 191;
  public static final int ARTL_eSOS_priorityOpt_233 = 192;
  public static final int ARTL_eSOS_priorityOpt_234 = 193;
  public static final int ARTL_eSOS_relationElement_98 = 194;
  public static final int ARTL_eSOS_relationElement_100 = 195;
  public static final int ARTL_eSOS_relationElement_102 = 196;
  public static final int ARTL_eSOS_relationElement_104 = 197;
  public static final int ARTL_eSOS_relationElement_101 = 198;
  public static final int ARTL_eSOS_relationElement_103 = 199;
  public static final int ARTL_eSOS_relationElement_108 = 200;
  public static final int ARTL_eSOS_relationElement_110 = 201;
  public static final int ARTL_eSOS_relationElement_112 = 202;
  public static final int ARTL_eSOS_relationElement_114 = 203;
  public static final int ARTL_eSOS_relationElement_111 = 204;
  public static final int ARTL_eSOS_relationElement_113 = 205;
  public static final int ARTL_eSOS_relationElement_118 = 206;
  public static final int ARTL_eSOS_relationElement_120 = 207;
  public static final int ARTL_eSOS_relationElement_122 = 208;
  public static final int ARTL_eSOS_relationElement_124 = 209;
  public static final int ARTL_eSOS_relationElement_121 = 210;
  public static final int ARTL_eSOS_relationElement_123 = 211;
  public static final int ARTL_eSOS_relationElement_128 = 212;
  public static final int ARTL_eSOS_relationElement_130 = 213;
  public static final int ARTL_eSOS_relationElement_132 = 214;
  public static final int ARTL_eSOS_relationElement_134 = 215;
  public static final int ARTL_eSOS_relationElement_131 = 216;
  public static final int ARTL_eSOS_relationElement_133 = 217;
  public static final int ARTL_eSOS_relationElement_138 = 218;
  public static final int ARTL_eSOS_relationElement_140 = 219;
  public static final int ARTL_eSOS_relationElement_142 = 220;
  public static final int ARTL_eSOS_relationElement_144 = 221;
  public static final int ARTL_eSOS_relationElement_146 = 222;
  public static final int ARTL_eSOS_relationElement_141 = 223;
  public static final int ARTL_eSOS_relationElement_143 = 224;
  public static final int ARTL_eSOS_relationElement_150 = 225;
  public static final int ARTL_eSOS_relationElement_152 = 226;
  public static final int ARTL_eSOS_relationElement_154 = 227;
  public static final int ARTL_eSOS_relationElement_156 = 228;
  public static final int ARTL_eSOS_relationElement_158 = 229;
  public static final int ARTL_eSOS_relationElement_153 = 230;
  public static final int ARTL_eSOS_relationElement_155 = 231;
  public static final int ARTL_eSOS_relationElement_162 = 232;
  public static final int ARTL_eSOS_relationElement_164 = 233;
  public static final int ARTL_eSOS_relationElements_86 = 234;
  public static final int ARTL_eSOS_relationElements_90 = 235;
  public static final int ARTL_eSOS_relationElements_92 = 236;
  public static final int ARTL_eSOS_relationElements_87 = 237;
  public static final int ARTL_eSOS_relationElements_88 = 238;
  public static final int ARTL_eSOS_relationElements_94 = 239;
  public static final int ARTL_eSOS_relationElements_96 = 240;
  public static final int ARTL_eSOS_relationSymbol_488 = 241;
  public static final int ARTL_eSOS_relationSymbol_490 = 242;
  public static final int ARTL_eSOS_relationSymbol_489 = 243;
  public static final int ARTL_eSOS_relationSymbol_492 = 244;
  public static final int ARTL_eSOS_relationSymbol_494 = 245;
  public static final int ARTL_eSOS_relationSymbol_493 = 246;
  public static final int ARTL_eSOS_relationSymbol_496 = 247;
  public static final int ARTL_eSOS_relationSymbol_498 = 248;
  public static final int ARTL_eSOS_relationSymbol_497 = 249;
  public static final int ARTL_eSOS_relationSymbol_500 = 250;
  public static final int ARTL_eSOS_relationSymbol_502 = 251;
  public static final int ARTL_eSOS_relationSymbol_501 = 252;
  public static final int ARTL_eSOS_relationSymbol_504 = 253;
  public static final int ARTL_eSOS_relationSymbol_506 = 254;
  public static final int ARTL_eSOS_relationSymbol_505 = 255;
  public static final int ARTL_eSOS_rule_210 = 256;
  public static final int ARTL_eSOS_rule_214 = 257;
  public static final int ARTL_eSOS_rule_216 = 258;
  public static final int ARTL_eSOS_rule_218 = 259;
  public static final int ARTL_eSOS_rule_220 = 260;
  public static final int ARTL_eSOS_rule_222 = 261;
  public static final int ARTL_eSOS_rule_219 = 262;
  public static final int ARTL_eSOS_sideCondition_332 = 263;
  public static final int ARTL_eSOS_sideCondition_334 = 264;
  public static final int ARTL_eSOS_sideCondition_336 = 265;
  public static final int ARTL_eSOS_sideCondition_338 = 266;
  public static final int ARTL_eSOS_sideCondition_335 = 267;
  public static final int ARTL_eSOS_specification_2 = 268;
  public static final int ARTL_eSOS_specification_4 = 269;
  public static final int ARTL_eSOS_specification_6 = 270;
  public static final int ARTL_eSOS_specification_8 = 271;
  public static final int ARTL_eSOS_specification_10 = 272;
  public static final int ARTL_eSOS_subterms_360 = 273;
  public static final int ARTL_eSOS_subterms_362 = 274;
  public static final int ARTL_eSOS_subterms_364 = 275;
  public static final int ARTL_eSOS_subterms_366 = 276;
  public static final int ARTL_eSOS_subterms_370 = 277;
  public static final int ARTL_eSOS_subterms_372 = 278;
  public static final int ARTL_eSOS_subterms_376 = 279;
  public static final int ARTL_eSOS_subterms_380 = 280;
  public static final int ARTL_eSOS_subterms_375 = 281;
  public static final int ARTL_eSOS_term_342 = 282;
  public static final int ARTL_eSOS_term_344 = 283;
  public static final int ARTL_eSOS_term_348 = 284;
  public static final int ARTL_eSOS_term_350 = 285;
  public static final int ARTL_eSOS_term_354 = 286;
  public static final int ARTL_eSOS_term_356 = 287;
  public static final int ARTL_eSOS_term_358 = 288;
  public static final int ARTL_eSOS_term_353 = 289;
  public static final int ARTL_eSOS_term_357 = 290;
  public static final int ARTL_eSOS_termElement_382 = 291;
  public static final int ARTL_eSOS_termElement_384 = 292;
  public static final int ARTL_eSOS_termElement_388 = 293;
  public static final int ARTL_eSOS_termElement_390 = 294;
  public static final int ARTL_eSOS_termElement_394 = 295;
  public static final int ARTL_eSOS_termElement_396 = 296;
  public static final int ARTL_eSOS_termElement_400 = 297;
  public static final int ARTL_eSOS_termElement_402 = 298;
  public static final int ARTL_eSOS_termElement_406 = 299;
  public static final int ARTL_eSOS_termElement_408 = 300;
  public static final int ARTL_eSOS_termElement_412 = 301;
  public static final int ARTL_eSOS_termElement_414 = 302;
  public static final int ARTL_eSOS_test_418 = 303;
  public static final int ARTL_eSOS_test_420 = 304;
  public static final int ARTL_eSOS_test_422 = 305;
  public static final int ARTL_eSOS_test_426 = 306;
  public static final int ARTL_eSOS_test_421 = 307;
  public static final int ARTL_eSOS_test_430 = 308;
  public static final int ARTL_eSOS_test_432 = 309;
  public static final int ARTL_eSOS_test_434 = 310;
  public static final int ARTL_eSOS_test_436 = 311;
  public static final int ARTL_eSOS_test_438 = 312;
  public static final int ARTL_eSOS_test_442 = 313;
  public static final int ARTL_eSOS_test_433 = 314;
  public static final int ARTL_eSOS_test_437 = 315;
  public static final int ARTL_eSOS_test_446 = 316;
  public static final int ARTL_eSOS_test_448 = 317;
  public static final int ARTL_eSOS_test_450 = 318;
  public static final int ARTL_eSOS_test_452 = 319;
  public static final int ARTL_eSOS_test_454 = 320;
  public static final int ARTL_eSOS_test_456 = 321;
  public static final int ARTL_eSOS_test_458 = 322;
  public static final int ARTL_eSOS_test_462 = 323;
  public static final int ARTL_eSOS_test_449 = 324;
  public static final int ARTL_eSOS_test_453 = 325;
  public static final int ARTL_eSOS_test_457 = 326;
  public static final int ARTL_eSOS_test_466 = 327;
  public static final int ARTL_eSOS_test_468 = 328;
  public static final int ARTL_eSOS_test_470 = 329;
  public static final int ARTL_eSOS_test_472 = 330;
  public static final int ARTL_eSOS_test_474 = 331;
  public static final int ARTL_eSOS_test_478 = 332;
  public static final int ARTL_eSOS_test_469 = 333;
  public static final int ARTL_eSOS_test_473 = 334;
  public static final int ARTL_eSOS_transition_282 = 335;
  public static final int ARTL_eSOS_transition_286 = 336;
  public static final int ARTL_eSOS_transition_288 = 337;
  public static final int ARTL_eSOS_transition_292 = 338;
  public static final int ARTX_DESPATCH = 339;
  public static final int ARTX_DUMMY = 340;
  public static final int ARTX_LABEL_EXTENT = 341;
  /* End of artLabel enumeration */

  /* Start of artName enumeration */
  public static final int ARTNAME_NONE = 0;
  public static final int ARTNAME_EXTENT = 1;
  /* End of artName enumeration */
  public void ARTPF_eSOS_BOOLEAN() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.BOOLEAN production descriptor loads*/
      case ARTL_eSOS_BOOLEAN: 
        if (ARTSet4[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_BOOLEAN_514, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_BOOLEAN_520, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.BOOLEAN: match production*/
      case ARTL_eSOS_BOOLEAN_514: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_true, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_BOOLEAN_516, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.BOOLEAN: match production*/
      case ARTL_eSOS_BOOLEAN_520: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_false, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_BOOLEAN_522, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_ID_SOS() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.ID_SOS production descriptor loads*/
      case ARTL_eSOS_ID_SOS: 
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_ID_SOS_508, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.ID_SOS: match production*/
      case ARTL_eSOS_ID_SOS_508: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID_SOS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_ID_SOS_510, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_INTEGER() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.INTEGER production descriptor loads*/
      case ARTL_eSOS_INTEGER: 
        if (ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_INTEGER_526, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.INTEGER: match production*/
      case ARTL_eSOS_INTEGER_526: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_INTEGER, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_INTEGER_528, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_REAL() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.REAL production descriptor loads*/
      case ARTL_eSOS_REAL: 
        if (ARTSet10[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_REAL_532, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.REAL: match production*/
      case ARTL_eSOS_REAL_532: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_REAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_REAL_534, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_RELATION() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.RELATION production descriptor loads*/
      case ARTL_eSOS_RELATION: 
        if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_RELATION_482, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.RELATION: match production*/
      case ARTL_eSOS_RELATION_482: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_RELATION_484, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_relationSymbol; return; }
      case ARTL_eSOS_RELATION_484: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_STRING_DQ() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.STRING_DQ production descriptor loads*/
      case ARTL_eSOS_STRING_DQ: 
        if (ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_STRING_DQ_538, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.STRING_DQ: match production*/
      case ARTL_eSOS_STRING_DQ_538: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_DQ, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_STRING_DQ_540, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_STRING_SQ() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.STRING_SQ production descriptor loads*/
      case ARTL_eSOS_STRING_SQ: 
        if (ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_STRING_SQ_544, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.STRING_SQ: match production*/
      case ARTL_eSOS_STRING_SQ_544: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_SQ, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_STRING_SQ_546, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_conditions() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.conditions production descriptor loads*/
      case ARTL_eSOS_conditions: 
        if (ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_conditions_262, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_conditions_266, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_conditions_274, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.conditions: match production*/
      case ARTL_eSOS_conditions_262: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_conditions_264, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.conditions: match production*/
      case ARTL_eSOS_conditions_266: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_conditions_268, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_transition; return; }
      case ARTL_eSOS_conditions_268: 
        /* Nonterminal template end */
        if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_conditions_272, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_conditions; return; }
      case ARTL_eSOS_conditions_272: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.conditions: match production*/
      case ARTL_eSOS_conditions_274: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_conditions_276, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_sideCondition; return; }
      case ARTL_eSOS_conditions_276: 
        /* Nonterminal template end */
        if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_conditions_280, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_conditions; return; }
      case ARTL_eSOS_conditions_280: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_configuration() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.configuration production descriptor loads*/
      case ARTL_eSOS_configuration: 
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_configuration_296, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.configuration: match production*/
      case ARTL_eSOS_configuration_296: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_configuration_298, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_configuration_298: 
        /* Nonterminal template end */
        if (!ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_configuration_302, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_entityReferences; return; }
      case ARTL_eSOS_configuration_302: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_element() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.element production descriptor loads*/
      case ARTL_eSOS_element: 
        if (ARTSet24[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_element_14, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet26[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_element_22, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet26[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_element_32, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet27[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_element_40, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_element_48, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet29[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_element_52, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.element: match production*/
      case ARTL_eSOS_element_14: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_latex, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_element_16, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet25[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_18, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_latexElement; return; }
      case ARTL_eSOS_element_18: 
        /* Nonterminal template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_20, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_latexElements; return; }
      case ARTL_eSOS_element_20: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.element: match production*/
      case ARTL_eSOS_element_22: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_relation, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_element_24, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_26, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_RELATION; return; }
      case ARTL_eSOS_element_26: 
        /* Nonterminal template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_30, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_relationElements; return; }
      case ARTL_eSOS_element_30: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.element: match production*/
      case ARTL_eSOS_element_32: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_relation, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_element_34, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_38, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_relationElements; return; }
      case ARTL_eSOS_element_38: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.element: match production*/
      case ARTL_eSOS_element_40: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_priority, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_element_42, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_44, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_priority; return; }
      case ARTL_eSOS_element_44: 
        /* Nonterminal template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_46, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_priorities; return; }
      case ARTL_eSOS_element_46: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.element: match production*/
      case ARTL_eSOS_element_48: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_50, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_rule; return; }
      case ARTL_eSOS_element_50: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.element: match production*/
      case ARTL_eSOS_element_52: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_test, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_element_54, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_element_56, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_test; return; }
      case ARTL_eSOS_element_56: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_entityReferences() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.entityReferences production descriptor loads*/
      case ARTL_eSOS_entityReferences: 
        if (ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_entityReferences_304, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_entityReferences_308, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_entityReferences_318, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.entityReferences: match production*/
      case ARTL_eSOS_entityReferences_304: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_entityReferences_306, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.entityReferences: match production*/
      case ARTL_eSOS_entityReferences_308: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_entityReferences_310, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_entityReferences_312, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_entityReferences_312: 
        /* Nonterminal template end */
        if (!ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_entityReferences_316, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_entityReferences; return; }
      case ARTL_eSOS_entityReferences_316: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.entityReferences: match production*/
      case ARTL_eSOS_entityReferences_318: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_entityReferences_320, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_entityReferences_322, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_entityReferences_322: 
        /* Nonterminal template end */
        if (!ARTSet32[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_entityReferences_324, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_entityReferences_326, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_entityReferences_326: 
        /* Nonterminal template end */
        if (!ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_entityReferences_330, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_entityReferences; return; }
      case ARTL_eSOS_entityReferences_330: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_labelOpt() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.labelOpt production descriptor loads*/
      case ARTL_eSOS_labelOpt: 
        if (ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_labelOpt_240, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet35[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_labelOpt_246, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet35[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_labelOpt_254, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.labelOpt: match production*/
      case ARTL_eSOS_labelOpt_240: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_labelOpt_242, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.labelOpt: match production*/
      case ARTL_eSOS_labelOpt_246: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_labelOpt_248, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_labelOpt_250, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_labelOpt_250: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.labelOpt: match production*/
      case ARTL_eSOS_labelOpt_254: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_labelOpt_256, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_labelOpt_258, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_STRING_DQ; return; }
      case ARTL_eSOS_labelOpt_258: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_latexElement() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.latexElement production descriptor loads*/
      case ARTL_eSOS_latexElement: 
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_latexElement_70, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_latexElement_78, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.latexElement: match production*/
      case ARTL_eSOS_latexElement_70: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_latexElement_72, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_latexElement_72: 
        /* Nonterminal template end */
        if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_latexElement_74, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_STRING_DQ; return; }
      case ARTL_eSOS_latexElement_74: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.latexElement: match production*/
      case ARTL_eSOS_latexElement_78: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_latexElement_80, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_RELATION; return; }
      case ARTL_eSOS_latexElement_80: 
        /* Nonterminal template end */
        if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_latexElement_82, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_STRING_DQ; return; }
      case ARTL_eSOS_latexElement_82: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_latexElements() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.latexElements production descriptor loads*/
      case ARTL_eSOS_latexElements: 
        if (ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_latexElements_58, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_latexElements_66, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.latexElements: match production*/
      case ARTL_eSOS_latexElements_58: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_latexElements_60, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet25[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_latexElements_62, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_latexElement; return; }
      case ARTL_eSOS_latexElements_62: 
        /* Nonterminal template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_latexElements_64, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_latexElements; return; }
      case ARTL_eSOS_latexElements_64: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.latexElements: match production*/
      case ARTL_eSOS_latexElements_66: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_latexElements_68, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_priorities() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.priorities production descriptor loads*/
      case ARTL_eSOS_priorities: 
        if (ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_priorities_168, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_priorities_176, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.priorities: match production*/
      case ARTL_eSOS_priorities_168: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_priorities_170, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priorities_172, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_priority; return; }
      case ARTL_eSOS_priorities_172: 
        /* Nonterminal template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priorities_174, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_priorities; return; }
      case ARTL_eSOS_priorities_174: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.priorities: match production*/
      case ARTL_eSOS_priorities_176: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_priorities_178, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_priority() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.priority production descriptor loads*/
      case ARTL_eSOS_priority: 
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_priority_180, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_priority_190, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_priority_200, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.priority: match production*/
      case ARTL_eSOS_priority_180: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priority_182, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_priority_182: 
        /* Nonterminal template end */
        if (!ARTSet36[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__GT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_priority_184, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priority_186, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_priority_186: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.priority: match production*/
      case ARTL_eSOS_priority_190: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priority_192, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_priority_192: 
        /* Nonterminal template end */
        if (!ARTSet37[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_priority_194, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priority_196, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_priority_196: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.priority: match production*/
      case ARTL_eSOS_priority_200: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priority_202, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_priority_202: 
        /* Nonterminal template end */
        if (!ARTSet32[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_priority_204, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priority_206, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_priority_206: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_priorityOpt() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.priorityOpt production descriptor loads*/
      case ARTL_eSOS_priorityOpt: 
        if (ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_priorityOpt_226, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet39[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_priorityOpt_232, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.priorityOpt: match production*/
      case ARTL_eSOS_priorityOpt_226: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_priorityOpt_228, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.priorityOpt: match production*/
      case ARTL_eSOS_priorityOpt_232: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__PLUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_priorityOpt_234, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_priorityOpt_236, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_priorityOpt_236: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_relationElement() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.relationElement production descriptor loads*/
      case ARTL_eSOS_relationElement: 
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElement_98, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElement_108, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElement_118, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElement_128, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElement_138, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElement_150, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElement_162, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.relationElement: match production*/
      case ARTL_eSOS_relationElement_98: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_100, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_100: 
        /* Nonterminal template end */
        if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_102, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet41[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_map, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_104, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationElement: match production*/
      case ARTL_eSOS_relationElement_108: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_110, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_110: 
        /* Nonterminal template end */
        if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_112, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet42[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_mapFixed, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_114, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationElement: match production*/
      case ARTL_eSOS_relationElement_118: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_120, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_120: 
        /* Nonterminal template end */
        if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_122, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet43[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_listIn, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_124, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationElement: match production*/
      case ARTL_eSOS_relationElement_128: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_130, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_130: 
        /* Nonterminal template end */
        if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_132, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet44[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_listOut, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_134, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationElement: match production*/
      case ARTL_eSOS_relationElement_138: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_140, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_140: 
        /* Nonterminal template end */
        if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_142, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_singleton, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_144, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_146, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_146: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationElement: match production*/
      case ARTL_eSOS_relationElement_150: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_152, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_152: 
        /* Nonterminal template end */
        if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_154, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet46[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_untyped, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElement_156, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_158, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_relationElement_158: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationElement: match production*/
      case ARTL_eSOS_relationElement_162: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElement_164, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_relationElement_164: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_relationElements() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.relationElements production descriptor loads*/
      case ARTL_eSOS_relationElements: 
        if (ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElements_86, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationElements_94, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.relationElements: match production*/
      case ARTL_eSOS_relationElements_86: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElements_88, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElements_90, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_relationElement; return; }
      case ARTL_eSOS_relationElements_90: 
        /* Nonterminal template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_relationElements_92, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_relationElements; return; }
      case ARTL_eSOS_relationElements_92: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationElements: match production*/
      case ARTL_eSOS_relationElements_94: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationElements_96, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_relationSymbol() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.relationSymbol production descriptor loads*/
      case ARTL_eSOS_relationSymbol: 
        if (ARTSet47[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationSymbol_488, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet48[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationSymbol_492, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet49[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationSymbol_496, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet50[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationSymbol_500, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet51[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_relationSymbol_504, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.relationSymbol: match production*/
      case ARTL_eSOS_relationSymbol_488: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS_GT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationSymbol_490, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationSymbol: match production*/
      case ARTL_eSOS_relationSymbol_492: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS_GT_STAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationSymbol_494, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationSymbol: match production*/
      case ARTL_eSOS_relationSymbol_496: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__EQUAL_GT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationSymbol_498, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationSymbol: match production*/
      case ARTL_eSOS_relationSymbol_500: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__EQUAL_GT_STAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationSymbol_502, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.relationSymbol: match production*/
      case ARTL_eSOS_relationSymbol_504: 
        /* Cat/unary template start */
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS_GT_GT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_relationSymbol_506, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_rule() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.rule production descriptor loads*/
      case ARTL_eSOS_rule: 
        if (ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_rule_210, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.rule: match production*/
      case ARTL_eSOS_rule_210: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_rule_214, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_labelOpt; return; }
      case ARTL_eSOS_rule_214: 
        /* Nonterminal template end */
        if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_rule_216, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_priorityOpt; return; }
      case ARTL_eSOS_rule_216: 
        /* Nonterminal template end */
        if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_rule_218, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_conditions; return; }
      case ARTL_eSOS_rule_218: 
        /* Nonterminal template end */
        if (!ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__MINUS_MINUS_MINUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_rule_220, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_rule_222, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_transition; return; }
      case ARTL_eSOS_rule_222: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_sideCondition() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.sideCondition production descriptor loads*/
      case ARTL_eSOS_sideCondition: 
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_sideCondition_332, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.sideCondition: match production*/
      case ARTL_eSOS_sideCondition_332: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_sideCondition_334, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_sideCondition_334: 
        /* Nonterminal template end */
        if (!ARTSet52[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__BAR_GT, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_sideCondition_336, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_sideCondition_338, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_sideCondition_338: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_specification() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.specification production descriptor loads*/
      case ARTL_eSOS_specification: 
        if (ARTSet22[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_specification_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet22[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_specification_8, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.specification: match production*/
      case ARTL_eSOS_specification_2: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_specification_4, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_element; return; }
      case ARTL_eSOS_specification_4: 
        /* Nonterminal template end */
        if (!ARTSet22[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_specification_6, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_specification; return; }
      case ARTL_eSOS_specification_6: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet53[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.specification: match production*/
      case ARTL_eSOS_specification_8: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_specification_10, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_element; return; }
      case ARTL_eSOS_specification_10: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet53[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_subterms() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.subterms production descriptor loads*/
      case ARTL_eSOS_subterms: 
        if (ARTSet54[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_subterms_360, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_subterms_364, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_subterms_370, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.subterms: match production*/
      case ARTL_eSOS_subterms_360: 
        /* Cat/unary template start */
        /* Epsilon template start */
        artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_subterms_362, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Epsilon template end */
        /* Cat/unary template end */
        if (!ARTSet54[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.subterms: match production*/
      case ARTL_eSOS_subterms_364: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_subterms_366, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_subterms_366: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet54[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.subterms: match production*/
      case ARTL_eSOS_subterms_370: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_subterms_372, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_term; return; }
      case ARTL_eSOS_subterms_372: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_subterms_376, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet54[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_subterms_380, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_subterms; return; }
      case ARTL_eSOS_subterms_380: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet54[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_term() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.term production descriptor loads*/
      case ARTL_eSOS_term: 
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_term_342, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_term_348, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.term: match production*/
      case ARTL_eSOS_term_342: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_term_344, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_termElement; return; }
      case ARTL_eSOS_term_344: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.term: match production*/
      case ARTL_eSOS_term_348: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_term_350, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_term_350: 
        /* Nonterminal template end */
        if (!ARTSet55[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_term_354, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet56[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_term_356, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_subterms; return; }
      case ARTL_eSOS_term_356: 
        /* Nonterminal template end */
        if (!ARTSet57[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_term_358, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_termElement() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.termElement production descriptor loads*/
      case ARTL_eSOS_termElement: 
        if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_termElement_382, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_termElement_388, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet10[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_termElement_394, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_termElement_400, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_termElement_406, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_termElement_412, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.termElement: match production*/
      case ARTL_eSOS_termElement_382: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_termElement_384, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_BOOLEAN; return; }
      case ARTL_eSOS_termElement_384: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.termElement: match production*/
      case ARTL_eSOS_termElement_388: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_termElement_390, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_INTEGER; return; }
      case ARTL_eSOS_termElement_390: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.termElement: match production*/
      case ARTL_eSOS_termElement_394: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_termElement_396, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_REAL; return; }
      case ARTL_eSOS_termElement_396: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.termElement: match production*/
      case ARTL_eSOS_termElement_400: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_termElement_402, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_STRING_DQ; return; }
      case ARTL_eSOS_termElement_402: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.termElement: match production*/
      case ARTL_eSOS_termElement_406: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_termElement_408, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_STRING_SQ; return; }
      case ARTL_eSOS_termElement_408: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.termElement: match production*/
      case ARTL_eSOS_termElement_412: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_termElement_414, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_ID_SOS; return; }
      case ARTL_eSOS_termElement_414: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_test() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.test production descriptor loads*/
      case ARTL_eSOS_test: 
        if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_test_418, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_test_430, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_test_446, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        if (ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_test_466, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.test: match production*/
      case ARTL_eSOS_test_418: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_420, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_RELATION; return; }
      case ARTL_eSOS_test_420: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_422, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_426, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_subterms; return; }
      case ARTL_eSOS_test_426: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.test: match production*/
      case ARTL_eSOS_test_430: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_432, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_STRING_DQ; return; }
      case ARTL_eSOS_test_432: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_434, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_436, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_RELATION; return; }
      case ARTL_eSOS_test_436: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_438, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_442, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_subterms; return; }
      case ARTL_eSOS_test_442: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.test: match production*/
      case ARTL_eSOS_test_446: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_448, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_INTEGER; return; }
      case ARTL_eSOS_test_448: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_450, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_452, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_STRING_DQ; return; }
      case ARTL_eSOS_test_452: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_454, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_456, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_RELATION; return; }
      case ARTL_eSOS_test_456: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_458, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_462, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_subterms; return; }
      case ARTL_eSOS_test_462: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
        /* Nonterminal eSOS.test: match production*/
      case ARTL_eSOS_test_466: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_468, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_INTEGER; return; }
      case ARTL_eSOS_test_468: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_470, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_472, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_RELATION; return; }
      case ARTL_eSOS_test_472: 
        /* Nonterminal template end */
        if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Terminal template start */
        artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
        artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
        artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
        artCurrentSPPFNode = artFindSPPF(ARTL_eSOS_test_474, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
        /* Terminal template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_test_478, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_subterms; return; }
      case ARTL_eSOS_test_478: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
    }
  }

  public void ARTPF_eSOS_transition() {
    switch (artCurrentRestartLabel) {
        /* Nonterminal eSOS.transition production descriptor loads*/
      case ARTL_eSOS_transition: 
        if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
          artFindDescriptor(ARTL_eSOS_transition_282, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
        { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal eSOS.transition: match production*/
      case ARTL_eSOS_transition_282: 
        /* Cat/unary template start */
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_transition_286, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_configuration; return; }
      case ARTL_eSOS_transition_286: 
        /* Nonterminal template end */
        if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_transition_288, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_RELATION; return; }
      case ARTL_eSOS_transition_288: 
        /* Nonterminal template end */
        if (!ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
        /* Nonterminal template start */
        artCurrentGSSNode = artFindGSS(ARTL_eSOS_transition_292, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
        { artCurrentRestartLabel = ARTL_eSOS_configuration; return; }
      case ARTL_eSOS_transition_292: 
        /* Nonterminal template end */
        /* Cat/unary template end */
        if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
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
        case ARTL_eSOS_BOOLEAN: 
          ARTPF_eSOS_BOOLEAN();
          break;
        case ARTL_eSOS_ID_SOS: 
          ARTPF_eSOS_ID_SOS();
          break;
        case ARTL_eSOS_INTEGER: 
          ARTPF_eSOS_INTEGER();
          break;
        case ARTL_eSOS_REAL: 
          ARTPF_eSOS_REAL();
          break;
        case ARTL_eSOS_RELATION: 
          ARTPF_eSOS_RELATION();
          break;
        case ARTL_eSOS_STRING_DQ: 
          ARTPF_eSOS_STRING_DQ();
          break;
        case ARTL_eSOS_STRING_SQ: 
          ARTPF_eSOS_STRING_SQ();
          break;
        case ARTL_eSOS_conditions: 
          ARTPF_eSOS_conditions();
          break;
        case ARTL_eSOS_configuration: 
          ARTPF_eSOS_configuration();
          break;
        case ARTL_eSOS_element: 
          ARTPF_eSOS_element();
          break;
        case ARTL_eSOS_entityReferences: 
          ARTPF_eSOS_entityReferences();
          break;
        case ARTL_eSOS_labelOpt: 
          ARTPF_eSOS_labelOpt();
          break;
        case ARTL_eSOS_latexElement: 
          ARTPF_eSOS_latexElement();
          break;
        case ARTL_eSOS_latexElements: 
          ARTPF_eSOS_latexElements();
          break;
        case ARTL_eSOS_priorities: 
          ARTPF_eSOS_priorities();
          break;
        case ARTL_eSOS_priority: 
          ARTPF_eSOS_priority();
          break;
        case ARTL_eSOS_priorityOpt: 
          ARTPF_eSOS_priorityOpt();
          break;
        case ARTL_eSOS_relationElement: 
          ARTPF_eSOS_relationElement();
          break;
        case ARTL_eSOS_relationElements: 
          ARTPF_eSOS_relationElements();
          break;
        case ARTL_eSOS_relationSymbol: 
          ARTPF_eSOS_relationSymbol();
          break;
        case ARTL_eSOS_rule: 
          ARTPF_eSOS_rule();
          break;
        case ARTL_eSOS_sideCondition: 
          ARTPF_eSOS_sideCondition();
          break;
        case ARTL_eSOS_specification: 
          ARTPF_eSOS_specification();
          break;
        case ARTL_eSOS_subterms: 
          ARTPF_eSOS_subterms();
          break;
        case ARTL_eSOS_term: 
          ARTPF_eSOS_term();
          break;
        case ARTL_eSOS_termElement: 
          ARTPF_eSOS_termElement();
          break;
        case ARTL_eSOS_test: 
          ARTPF_eSOS_test();
          break;
        case ARTL_eSOS_transition: 
          ARTPF_eSOS_transition();
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

  public void ARTSet16initialise() {
    ARTSet16 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet16, 0, artSetExtent, false);
    ARTSet16[ARTTS__MINUS_MINUS_MINUS] = true;
  }

  public void ARTSet26initialise() {
    ARTSet26 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet26, 0, artSetExtent, false);
    ARTSet26[ARTTS_relation] = true;
  }

  public void ARTSet24initialise() {
    ARTSet24 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet24, 0, artSetExtent, false);
    ARTSet24[ARTTS_latex] = true;
  }

  public void ARTSet30initialise() {
    ARTSet30 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet30, 0, artSetExtent, false);
    ARTSet30[ARTTB_INTEGER] = true;
    ARTSet30[ARTTB_STRING_DQ] = true;
    ARTSet30[ARTTS__MINUS_GT] = true;
    ARTSet30[ARTTS__MINUS_GT_STAR] = true;
    ARTSet30[ARTTS__MINUS_GT_GT] = true;
    ARTSet30[ARTTS__EQUAL_GT] = true;
    ARTSet30[ARTTS__EQUAL_GT_STAR] = true;
  }

  public void ARTSet44initialise() {
    ARTSet44 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet44, 0, artSetExtent, false);
    ARTSet44[ARTTS_listOut] = true;
  }

  public void ARTSet48initialise() {
    ARTSet48 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet48, 0, artSetExtent, false);
    ARTSet48[ARTTS__MINUS_GT_STAR] = true;
  }

  public void ARTSet43initialise() {
    ARTSet43 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet43, 0, artSetExtent, false);
    ARTSet43[ARTTS_listIn] = true;
  }

  public void ARTSet23initialise() {
    ARTSet23 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet23, 0, artSetExtent, false);
    ARTSet23[ARTX_EOS] = true;
    ARTSet23[ARTTB_ID_SOS] = true;
    ARTSet23[ARTTB_INTEGER] = true;
    ARTSet23[ARTTB_REAL] = true;
    ARTSet23[ARTTB_STRING_DQ] = true;
    ARTSet23[ARTTB_STRING_SQ] = true;
    ARTSet23[ARTTS__PLUS] = true;
    ARTSet23[ARTTS__MINUS] = true;
    ARTSet23[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet23[ARTTS_false] = true;
    ARTSet23[ARTTS_latex] = true;
    ARTSet23[ARTTS_priority] = true;
    ARTSet23[ARTTS_relation] = true;
    ARTSet23[ARTTS_test] = true;
    ARTSet23[ARTTS_true] = true;
  }

  public void ARTSet56initialise() {
    ARTSet56 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet56, 0, artSetExtent, false);
    ARTSet56[ARTTB_ID_SOS] = true;
    ARTSet56[ARTTB_INTEGER] = true;
    ARTSet56[ARTTB_REAL] = true;
    ARTSet56[ARTTB_STRING_DQ] = true;
    ARTSet56[ARTTB_STRING_SQ] = true;
    ARTSet56[ARTTS__RPAR] = true;
    ARTSet56[ARTTS_false] = true;
    ARTSet56[ARTTS_true] = true;
  }

  public void ARTSet49initialise() {
    ARTSet49 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet49, 0, artSetExtent, false);
    ARTSet49[ARTTS__EQUAL_GT] = true;
  }

  public void ARTSet34initialise() {
    ARTSet34 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet34, 0, artSetExtent, false);
    ARTSet34[ARTTB_ID_SOS] = true;
    ARTSet34[ARTTB_INTEGER] = true;
    ARTSet34[ARTTB_REAL] = true;
    ARTSet34[ARTTB_STRING_DQ] = true;
    ARTSet34[ARTTB_STRING_SQ] = true;
    ARTSet34[ARTTS__PLUS] = true;
    ARTSet34[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet34[ARTTS_false] = true;
    ARTSet34[ARTTS_true] = true;
  }

  public void ARTSet55initialise() {
    ARTSet55 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet55, 0, artSetExtent, false);
    ARTSet55[ARTTS__LPAR] = true;
  }

  public void ARTSet50initialise() {
    ARTSet50 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet50, 0, artSetExtent, false);
    ARTSet50[ARTTS__EQUAL_GT_STAR] = true;
  }

  public void ARTSet57initialise() {
    ARTSet57 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet57, 0, artSetExtent, false);
    ARTSet57[ARTTS__RPAR] = true;
  }

  public void ARTSet3initialise() {
    ARTSet3 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
    ARTSet3[ARTX_EOS] = true;
    ARTSet3[ARTTB_ID_SOS] = true;
    ARTSet3[ARTTB_INTEGER] = true;
    ARTSet3[ARTTB_REAL] = true;
    ARTSet3[ARTTB_STRING_DQ] = true;
    ARTSet3[ARTTB_STRING_SQ] = true;
    ARTSet3[ARTTS__RPAR] = true;
    ARTSet3[ARTTS__PLUS] = true;
    ARTSet3[ARTTS__COMMA] = true;
    ARTSet3[ARTTS__MINUS] = true;
    ARTSet3[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet3[ARTTS__MINUS_GT] = true;
    ARTSet3[ARTTS__MINUS_GT_STAR] = true;
    ARTSet3[ARTTS__MINUS_GT_GT] = true;
    ARTSet3[ARTTS__EQUAL_GT] = true;
    ARTSet3[ARTTS__EQUAL_GT_STAR] = true;
    ARTSet3[ARTTS_false] = true;
    ARTSet3[ARTTS_latex] = true;
    ARTSet3[ARTTS_priority] = true;
    ARTSet3[ARTTS_relation] = true;
    ARTSet3[ARTTS_test] = true;
    ARTSet3[ARTTS_true] = true;
    ARTSet3[ARTTS__BAR_GT] = true;
  }

  public void ARTSet39initialise() {
    ARTSet39 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet39, 0, artSetExtent, false);
    ARTSet39[ARTTS__PLUS] = true;
  }

  public void ARTSet2initialise() {
    ARTSet2 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
    ARTSet2[ARTTS_false] = true;
    ARTSet2[ARTTS_true] = true;
  }

  public void ARTSet27initialise() {
    ARTSet27 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet27, 0, artSetExtent, false);
    ARTSet27[ARTTS_priority] = true;
  }

  public void ARTSet31initialise() {
    ARTSet31 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet31, 0, artSetExtent, false);
    ARTSet31[ARTTS__COMMA] = true;
  }

  public void ARTSet15initialise() {
    ARTSet15 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet15, 0, artSetExtent, false);
    ARTSet15[ARTTB_ID_SOS] = true;
    ARTSet15[ARTTB_INTEGER] = true;
    ARTSet15[ARTTB_REAL] = true;
    ARTSet15[ARTTB_STRING_DQ] = true;
    ARTSet15[ARTTB_STRING_SQ] = true;
    ARTSet15[ARTTS_false] = true;
    ARTSet15[ARTTS_true] = true;
  }

  public void ARTSet19initialise() {
    ARTSet19 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet19, 0, artSetExtent, false);
    ARTSet19[ARTX_EOS] = true;
    ARTSet19[ARTTB_ID_SOS] = true;
    ARTSet19[ARTTB_INTEGER] = true;
    ARTSet19[ARTTB_REAL] = true;
    ARTSet19[ARTTB_STRING_DQ] = true;
    ARTSet19[ARTTB_STRING_SQ] = true;
    ARTSet19[ARTTS__PLUS] = true;
    ARTSet19[ARTTS__MINUS] = true;
    ARTSet19[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet19[ARTTS__MINUS_GT] = true;
    ARTSet19[ARTTS__MINUS_GT_STAR] = true;
    ARTSet19[ARTTS__MINUS_GT_GT] = true;
    ARTSet19[ARTTS__EQUAL_GT] = true;
    ARTSet19[ARTTS__EQUAL_GT_STAR] = true;
    ARTSet19[ARTTS_false] = true;
    ARTSet19[ARTTS_latex] = true;
    ARTSet19[ARTTS_priority] = true;
    ARTSet19[ARTTS_relation] = true;
    ARTSet19[ARTTS_test] = true;
    ARTSet19[ARTTS_true] = true;
  }

  public void ARTSet35initialise() {
    ARTSet35 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet35, 0, artSetExtent, false);
    ARTSet35[ARTTS__MINUS] = true;
  }

  public void ARTSet51initialise() {
    ARTSet51 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet51, 0, artSetExtent, false);
    ARTSet51[ARTTS__MINUS_GT_GT] = true;
  }

  public void ARTSet47initialise() {
    ARTSet47 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet47, 0, artSetExtent, false);
    ARTSet47[ARTTS__MINUS_GT] = true;
  }

  public void ARTSet11initialise() {
    ARTSet11 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet11, 0, artSetExtent, false);
    ARTSet11[ARTTS__MINUS_GT] = true;
    ARTSet11[ARTTS__MINUS_GT_STAR] = true;
    ARTSet11[ARTTS__MINUS_GT_GT] = true;
    ARTSet11[ARTTS__EQUAL_GT] = true;
    ARTSet11[ARTTS__EQUAL_GT_STAR] = true;
  }

  public void ARTSet42initialise() {
    ARTSet42 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet42, 0, artSetExtent, false);
    ARTSet42[ARTTS_mapFixed] = true;
  }

  public void ARTSet54initialise() {
    ARTSet54 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet54, 0, artSetExtent, false);
    ARTSet54[ARTX_EOS] = true;
    ARTSet54[ARTTB_ID_SOS] = true;
    ARTSet54[ARTTB_INTEGER] = true;
    ARTSet54[ARTTB_REAL] = true;
    ARTSet54[ARTTB_STRING_DQ] = true;
    ARTSet54[ARTTB_STRING_SQ] = true;
    ARTSet54[ARTTS__RPAR] = true;
    ARTSet54[ARTTS__PLUS] = true;
    ARTSet54[ARTTS__MINUS] = true;
    ARTSet54[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet54[ARTTS_false] = true;
    ARTSet54[ARTTS_latex] = true;
    ARTSet54[ARTTS_priority] = true;
    ARTSet54[ARTTS_relation] = true;
    ARTSet54[ARTTS_test] = true;
    ARTSet54[ARTTS_true] = true;
  }

  public void ARTSet12initialise() {
    ARTSet12 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet12, 0, artSetExtent, false);
    ARTSet12[ARTX_EOS] = true;
    ARTSet12[ARTTB_ID_SOS] = true;
    ARTSet12[ARTTB_INTEGER] = true;
    ARTSet12[ARTTB_REAL] = true;
    ARTSet12[ARTTB_STRING_DQ] = true;
    ARTSet12[ARTTB_STRING_SQ] = true;
    ARTSet12[ARTTS__PLUS] = true;
    ARTSet12[ARTTS__COMMA] = true;
    ARTSet12[ARTTS__MINUS] = true;
    ARTSet12[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet12[ARTTS_false] = true;
    ARTSet12[ARTTS_latex] = true;
    ARTSet12[ARTTS_priority] = true;
    ARTSet12[ARTTS_relation] = true;
    ARTSet12[ARTTS_test] = true;
    ARTSet12[ARTTS_true] = true;
  }

  public void ARTSet40initialise() {
    ARTSet40 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet40, 0, artSetExtent, false);
    ARTSet40[ARTTS__COLON] = true;
  }

  public void ARTSet29initialise() {
    ARTSet29 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet29, 0, artSetExtent, false);
    ARTSet29[ARTTS_test] = true;
  }

  public void ARTSet37initialise() {
    ARTSet37 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet37, 0, artSetExtent, false);
    ARTSet37[ARTTS__LT] = true;
  }

  public void ARTSet32initialise() {
    ARTSet32 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet32, 0, artSetExtent, false);
    ARTSet32[ARTTS__EQUAL] = true;
  }

  public void ARTSet5initialise() {
    ARTSet5 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
  }

  public void ARTSet36initialise() {
    ARTSet36 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet36, 0, artSetExtent, false);
    ARTSet36[ARTTS__GT] = true;
  }

  public void ARTSet4initialise() {
    ARTSet4 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
    ARTSet4[ARTTS_true] = true;
  }

  public void ARTSet52initialise() {
    ARTSet52 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet52, 0, artSetExtent, false);
    ARTSet52[ARTTS__BAR_GT] = true;
  }

  public void ARTSet25initialise() {
    ARTSet25 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet25, 0, artSetExtent, false);
    ARTSet25[ARTTB_ID_SOS] = true;
    ARTSet25[ARTTS__MINUS_GT] = true;
    ARTSet25[ARTTS__MINUS_GT_STAR] = true;
    ARTSet25[ARTTS__MINUS_GT_GT] = true;
    ARTSet25[ARTTS__EQUAL_GT] = true;
    ARTSet25[ARTTS__EQUAL_GT_STAR] = true;
  }

  public void ARTSet20initialise() {
    ARTSet20 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet20, 0, artSetExtent, false);
    ARTSet20[ARTX_EOS] = true;
    ARTSet20[ARTTB_ID_SOS] = true;
    ARTSet20[ARTTB_INTEGER] = true;
    ARTSet20[ARTTB_REAL] = true;
    ARTSet20[ARTTB_STRING_DQ] = true;
    ARTSet20[ARTTB_STRING_SQ] = true;
    ARTSet20[ARTTS__PLUS] = true;
    ARTSet20[ARTTS__COMMA] = true;
    ARTSet20[ARTTS__MINUS] = true;
    ARTSet20[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet20[ARTTS__MINUS_GT] = true;
    ARTSet20[ARTTS__MINUS_GT_STAR] = true;
    ARTSet20[ARTTS__MINUS_GT_GT] = true;
    ARTSet20[ARTTS__EQUAL_GT] = true;
    ARTSet20[ARTTS__EQUAL_GT_STAR] = true;
    ARTSet20[ARTTS_false] = true;
    ARTSet20[ARTTS_latex] = true;
    ARTSet20[ARTTS_priority] = true;
    ARTSet20[ARTTS_relation] = true;
    ARTSet20[ARTTS_test] = true;
    ARTSet20[ARTTS_true] = true;
  }

  public void ARTSet53initialise() {
    ARTSet53 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet53, 0, artSetExtent, false);
    ARTSet53[ARTX_EOS] = true;
  }

  public void ARTSet28initialise() {
    ARTSet28 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet28, 0, artSetExtent, false);
    ARTSet28[ARTTB_ID_SOS] = true;
    ARTSet28[ARTTB_INTEGER] = true;
    ARTSet28[ARTTB_REAL] = true;
    ARTSet28[ARTTB_STRING_DQ] = true;
    ARTSet28[ARTTB_STRING_SQ] = true;
    ARTSet28[ARTTS__PLUS] = true;
    ARTSet28[ARTTS__MINUS] = true;
    ARTSet28[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet28[ARTTS_false] = true;
    ARTSet28[ARTTS_true] = true;
  }

  public void ARTSet6initialise() {
    ARTSet6 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
    ARTSet6[ARTTS_false] = true;
  }

  public void ARTSet8initialise() {
    ARTSet8 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
    ARTSet8[ARTX_EOS] = true;
    ARTSet8[ARTTB_ID_SOS] = true;
    ARTSet8[ARTTB_INTEGER] = true;
    ARTSet8[ARTTB_REAL] = true;
    ARTSet8[ARTTB_STRING_DQ] = true;
    ARTSet8[ARTTB_STRING_SQ] = true;
    ARTSet8[ARTTS__LPAR] = true;
    ARTSet8[ARTTS__RPAR] = true;
    ARTSet8[ARTTS__PLUS] = true;
    ARTSet8[ARTTS__COMMA] = true;
    ARTSet8[ARTTS__MINUS] = true;
    ARTSet8[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet8[ARTTS__MINUS_GT] = true;
    ARTSet8[ARTTS__MINUS_GT_STAR] = true;
    ARTSet8[ARTTS__MINUS_GT_GT] = true;
    ARTSet8[ARTTS__COLON] = true;
    ARTSet8[ARTTS__LT] = true;
    ARTSet8[ARTTS__EQUAL] = true;
    ARTSet8[ARTTS__EQUAL_GT] = true;
    ARTSet8[ARTTS__EQUAL_GT_STAR] = true;
    ARTSet8[ARTTS__GT] = true;
    ARTSet8[ARTTS_false] = true;
    ARTSet8[ARTTS_latex] = true;
    ARTSet8[ARTTS_priority] = true;
    ARTSet8[ARTTS_relation] = true;
    ARTSet8[ARTTS_test] = true;
    ARTSet8[ARTTS_true] = true;
    ARTSet8[ARTTS__BAR_GT] = true;
  }

  public void ARTSet41initialise() {
    ARTSet41 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet41, 0, artSetExtent, false);
    ARTSet41[ARTTS_map] = true;
  }

  public void ARTSet14initialise() {
    ARTSet14 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet14, 0, artSetExtent, false);
    ARTSet14[ARTTB_STRING_SQ] = true;
  }

  public void ARTSet22initialise() {
    ARTSet22 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet22, 0, artSetExtent, false);
    ARTSet22[ARTTB_ID_SOS] = true;
    ARTSet22[ARTTB_INTEGER] = true;
    ARTSet22[ARTTB_REAL] = true;
    ARTSet22[ARTTB_STRING_DQ] = true;
    ARTSet22[ARTTB_STRING_SQ] = true;
    ARTSet22[ARTTS__PLUS] = true;
    ARTSet22[ARTTS__MINUS] = true;
    ARTSet22[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet22[ARTTS_false] = true;
    ARTSet22[ARTTS_latex] = true;
    ARTSet22[ARTTS_priority] = true;
    ARTSet22[ARTTS_relation] = true;
    ARTSet22[ARTTS_test] = true;
    ARTSet22[ARTTS_true] = true;
  }

  public void ARTSet9initialise() {
    ARTSet9 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
    ARTSet9[ARTTB_INTEGER] = true;
  }

  public void ARTSet38initialise() {
    ARTSet38 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet38, 0, artSetExtent, false);
    ARTSet38[ARTTS__PLUS] = true;
  }

  public void ARTSet21initialise() {
    ARTSet21 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet21, 0, artSetExtent, false);
    ARTSet21[ARTTS__COMMA] = true;
  }

  public void ARTSet33initialise() {
    ARTSet33 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet33, 0, artSetExtent, false);
    ARTSet33[ARTTS__MINUS] = true;
  }

  public void ARTSet46initialise() {
    ARTSet46 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet46, 0, artSetExtent, false);
    ARTSet46[ARTTS_untyped] = true;
  }

  public void ARTSet13initialise() {
    ARTSet13 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet13, 0, artSetExtent, false);
    ARTSet13[ARTTB_STRING_DQ] = true;
  }

  public void ARTSet45initialise() {
    ARTSet45 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet45, 0, artSetExtent, false);
    ARTSet45[ARTTS_singleton] = true;
  }

  public void ARTSet7initialise() {
    ARTSet7 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
    ARTSet7[ARTTB_ID_SOS] = true;
  }

  public void ARTSet17initialise() {
    ARTSet17 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet17, 0, artSetExtent, false);
    ARTSet17[ARTTB_ID_SOS] = true;
    ARTSet17[ARTTB_INTEGER] = true;
    ARTSet17[ARTTB_REAL] = true;
    ARTSet17[ARTTB_STRING_DQ] = true;
    ARTSet17[ARTTB_STRING_SQ] = true;
    ARTSet17[ARTTS_false] = true;
    ARTSet17[ARTTS_true] = true;
  }

  public void ARTSet18initialise() {
    ARTSet18 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet18, 0, artSetExtent, false);
    ARTSet18[ARTTB_ID_SOS] = true;
    ARTSet18[ARTTB_INTEGER] = true;
    ARTSet18[ARTTB_REAL] = true;
    ARTSet18[ARTTB_STRING_DQ] = true;
    ARTSet18[ARTTB_STRING_SQ] = true;
    ARTSet18[ARTTS__MINUS_MINUS_MINUS] = true;
    ARTSet18[ARTTS_false] = true;
    ARTSet18[ARTTS_true] = true;
  }

  public void ARTSet10initialise() {
    ARTSet10 = new boolean[artSetExtent];
    artInitialiseBooleanArray(ARTSet10, 0, artSetExtent, false);
    ARTSet10[ARTTB_REAL] = true;
  }

  public void artSetInitialise() {
    ARTSet1initialise();
    ARTSet16initialise();
    ARTSet26initialise();
    ARTSet24initialise();
    ARTSet30initialise();
    ARTSet44initialise();
    ARTSet48initialise();
    ARTSet43initialise();
    ARTSet23initialise();
    ARTSet56initialise();
    ARTSet49initialise();
    ARTSet34initialise();
    ARTSet55initialise();
    ARTSet50initialise();
    ARTSet57initialise();
    ARTSet3initialise();
    ARTSet39initialise();
    ARTSet2initialise();
    ARTSet27initialise();
    ARTSet31initialise();
    ARTSet15initialise();
    ARTSet19initialise();
    ARTSet35initialise();
    ARTSet51initialise();
    ARTSet47initialise();
    ARTSet11initialise();
    ARTSet42initialise();
    ARTSet54initialise();
    ARTSet12initialise();
    ARTSet40initialise();
    ARTSet29initialise();
    ARTSet37initialise();
    ARTSet32initialise();
    ARTSet5initialise();
    ARTSet36initialise();
    ARTSet4initialise();
    ARTSet52initialise();
    ARTSet25initialise();
    ARTSet20initialise();
    ARTSet53initialise();
    ARTSet28initialise();
    ARTSet6initialise();
    ARTSet8initialise();
    ARTSet41initialise();
    ARTSet14initialise();
    ARTSet22initialise();
    ARTSet9initialise();
    ARTSet38initialise();
    ARTSet21initialise();
    ARTSet33initialise();
    ARTSet46initialise();
    ARTSet13initialise();
    ARTSet45initialise();
    ARTSet7initialise();
    ARTSet17initialise();
    ARTSet18initialise();
    ARTSet10initialise();
  }

  public void artTableInitialiser_eSOS_BOOLEAN() {
    artLabelInternalStrings[ARTL_eSOS_BOOLEAN] = "eSOS.BOOLEAN";
    artLabelStrings[ARTL_eSOS_BOOLEAN] = "eSOS.BOOLEAN";
    artKindOfs[ARTL_eSOS_BOOLEAN] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_BOOLEAN_514] = "eSOS.BOOLEAN ::= . 'true' ";
    artLabelStrings[ARTL_eSOS_BOOLEAN_514] = "";
    artlhsL[ARTL_eSOS_BOOLEAN_514] = ARTL_eSOS_BOOLEAN;
    artKindOfs[ARTL_eSOS_BOOLEAN_514] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_BOOLEAN_514] = true;
    artLabelInternalStrings[ARTL_eSOS_BOOLEAN_515] = "eSOS.BOOLEAN ::= 'true' ";
    artLabelStrings[ARTL_eSOS_BOOLEAN_515] = "";
    artlhsL[ARTL_eSOS_BOOLEAN_515] = ARTL_eSOS_BOOLEAN;
    artPopD[ARTL_eSOS_BOOLEAN_515] = true;
    artLabelInternalStrings[ARTL_eSOS_BOOLEAN_516] = "eSOS.BOOLEAN ::= 'true' .";
    artLabelStrings[ARTL_eSOS_BOOLEAN_516] = "";
    artlhsL[ARTL_eSOS_BOOLEAN_516] = ARTL_eSOS_BOOLEAN;
    artKindOfs[ARTL_eSOS_BOOLEAN_516] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_BOOLEAN_516] = true;
    arteoR_pL[ARTL_eSOS_BOOLEAN_516] = true;
    artPopD[ARTL_eSOS_BOOLEAN_516] = true;
    artLabelInternalStrings[ARTL_eSOS_BOOLEAN_520] = "eSOS.BOOLEAN ::= . 'false' ";
    artLabelStrings[ARTL_eSOS_BOOLEAN_520] = "";
    artlhsL[ARTL_eSOS_BOOLEAN_520] = ARTL_eSOS_BOOLEAN;
    artKindOfs[ARTL_eSOS_BOOLEAN_520] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_BOOLEAN_520] = true;
    artLabelInternalStrings[ARTL_eSOS_BOOLEAN_521] = "eSOS.BOOLEAN ::= 'false' ";
    artLabelStrings[ARTL_eSOS_BOOLEAN_521] = "";
    artlhsL[ARTL_eSOS_BOOLEAN_521] = ARTL_eSOS_BOOLEAN;
    artPopD[ARTL_eSOS_BOOLEAN_521] = true;
    artLabelInternalStrings[ARTL_eSOS_BOOLEAN_522] = "eSOS.BOOLEAN ::= 'false' .";
    artLabelStrings[ARTL_eSOS_BOOLEAN_522] = "";
    artlhsL[ARTL_eSOS_BOOLEAN_522] = ARTL_eSOS_BOOLEAN;
    artKindOfs[ARTL_eSOS_BOOLEAN_522] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_BOOLEAN_522] = true;
    arteoR_pL[ARTL_eSOS_BOOLEAN_522] = true;
    artPopD[ARTL_eSOS_BOOLEAN_522] = true;
  }

  public void artTableInitialiser_eSOS_ID_SOS() {
    artLabelInternalStrings[ARTL_eSOS_ID_SOS] = "eSOS.ID_SOS";
    artLabelStrings[ARTL_eSOS_ID_SOS] = "eSOS.ID_SOS";
    artKindOfs[ARTL_eSOS_ID_SOS] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_ID_SOS_508] = "eSOS.ID_SOS ::= . &ID_SOS ";
    artLabelStrings[ARTL_eSOS_ID_SOS_508] = "";
    artlhsL[ARTL_eSOS_ID_SOS_508] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_ID_SOS_508] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_ID_SOS_508] = true;
    artLabelInternalStrings[ARTL_eSOS_ID_SOS_509] = "eSOS.ID_SOS ::= &ID_SOS ";
    artLabelStrings[ARTL_eSOS_ID_SOS_509] = "";
    artlhsL[ARTL_eSOS_ID_SOS_509] = ARTL_eSOS_ID_SOS;
    artPopD[ARTL_eSOS_ID_SOS_509] = true;
    artLabelInternalStrings[ARTL_eSOS_ID_SOS_510] = "eSOS.ID_SOS ::= &ID_SOS .";
    artLabelStrings[ARTL_eSOS_ID_SOS_510] = "";
    artlhsL[ARTL_eSOS_ID_SOS_510] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_ID_SOS_510] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_ID_SOS_510] = true;
    arteoR_pL[ARTL_eSOS_ID_SOS_510] = true;
    artPopD[ARTL_eSOS_ID_SOS_510] = true;
  }

  public void artTableInitialiser_eSOS_INTEGER() {
    artLabelInternalStrings[ARTL_eSOS_INTEGER] = "eSOS.INTEGER";
    artLabelStrings[ARTL_eSOS_INTEGER] = "eSOS.INTEGER";
    artKindOfs[ARTL_eSOS_INTEGER] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_INTEGER_526] = "eSOS.INTEGER ::= . &INTEGER ";
    artLabelStrings[ARTL_eSOS_INTEGER_526] = "";
    artlhsL[ARTL_eSOS_INTEGER_526] = ARTL_eSOS_INTEGER;
    artKindOfs[ARTL_eSOS_INTEGER_526] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_INTEGER_526] = true;
    artLabelInternalStrings[ARTL_eSOS_INTEGER_527] = "eSOS.INTEGER ::= &INTEGER ";
    artLabelStrings[ARTL_eSOS_INTEGER_527] = "";
    artlhsL[ARTL_eSOS_INTEGER_527] = ARTL_eSOS_INTEGER;
    artPopD[ARTL_eSOS_INTEGER_527] = true;
    artLabelInternalStrings[ARTL_eSOS_INTEGER_528] = "eSOS.INTEGER ::= &INTEGER .";
    artLabelStrings[ARTL_eSOS_INTEGER_528] = "";
    artlhsL[ARTL_eSOS_INTEGER_528] = ARTL_eSOS_INTEGER;
    artKindOfs[ARTL_eSOS_INTEGER_528] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_INTEGER_528] = true;
    arteoR_pL[ARTL_eSOS_INTEGER_528] = true;
    artPopD[ARTL_eSOS_INTEGER_528] = true;
  }

  public void artTableInitialiser_eSOS_REAL() {
    artLabelInternalStrings[ARTL_eSOS_REAL] = "eSOS.REAL";
    artLabelStrings[ARTL_eSOS_REAL] = "eSOS.REAL";
    artKindOfs[ARTL_eSOS_REAL] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_REAL_532] = "eSOS.REAL ::= . &REAL ";
    artLabelStrings[ARTL_eSOS_REAL_532] = "";
    artlhsL[ARTL_eSOS_REAL_532] = ARTL_eSOS_REAL;
    artKindOfs[ARTL_eSOS_REAL_532] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_REAL_532] = true;
    artLabelInternalStrings[ARTL_eSOS_REAL_533] = "eSOS.REAL ::= &REAL ";
    artLabelStrings[ARTL_eSOS_REAL_533] = "";
    artlhsL[ARTL_eSOS_REAL_533] = ARTL_eSOS_REAL;
    artPopD[ARTL_eSOS_REAL_533] = true;
    artLabelInternalStrings[ARTL_eSOS_REAL_534] = "eSOS.REAL ::= &REAL .";
    artLabelStrings[ARTL_eSOS_REAL_534] = "";
    artlhsL[ARTL_eSOS_REAL_534] = ARTL_eSOS_REAL;
    artKindOfs[ARTL_eSOS_REAL_534] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_REAL_534] = true;
    arteoR_pL[ARTL_eSOS_REAL_534] = true;
    artPopD[ARTL_eSOS_REAL_534] = true;
  }

  public void artTableInitialiser_eSOS_RELATION() {
    artLabelInternalStrings[ARTL_eSOS_RELATION] = "eSOS.RELATION";
    artLabelStrings[ARTL_eSOS_RELATION] = "eSOS.RELATION";
    artKindOfs[ARTL_eSOS_RELATION] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_RELATION_482] = "eSOS.RELATION ::= . eSOS.relationSymbol ";
    artLabelStrings[ARTL_eSOS_RELATION_482] = "";
    artlhsL[ARTL_eSOS_RELATION_482] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_RELATION_482] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_RELATION_484] = "eSOS.RELATION ::= eSOS.relationSymbol .";
    artLabelStrings[ARTL_eSOS_RELATION_484] = "";
    artlhsL[ARTL_eSOS_RELATION_484] = ARTL_eSOS_RELATION;
    artSlotInstanceOfs[ARTL_eSOS_RELATION_484] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_RELATION_484] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_RELATION_484] = true;
    arteoR_pL[ARTL_eSOS_RELATION_484] = true;
    artPopD[ARTL_eSOS_RELATION_484] = true;
  }

  public void artTableInitialiser_eSOS_STRING_DQ() {
    artLabelInternalStrings[ARTL_eSOS_STRING_DQ] = "eSOS.STRING_DQ";
    artLabelStrings[ARTL_eSOS_STRING_DQ] = "eSOS.STRING_DQ";
    artKindOfs[ARTL_eSOS_STRING_DQ] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_STRING_DQ_538] = "eSOS.STRING_DQ ::= . &STRING_DQ ";
    artLabelStrings[ARTL_eSOS_STRING_DQ_538] = "";
    artlhsL[ARTL_eSOS_STRING_DQ_538] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_STRING_DQ_538] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_STRING_DQ_538] = true;
    artLabelInternalStrings[ARTL_eSOS_STRING_DQ_539] = "eSOS.STRING_DQ ::= &STRING_DQ ";
    artLabelStrings[ARTL_eSOS_STRING_DQ_539] = "";
    artlhsL[ARTL_eSOS_STRING_DQ_539] = ARTL_eSOS_STRING_DQ;
    artPopD[ARTL_eSOS_STRING_DQ_539] = true;
    artLabelInternalStrings[ARTL_eSOS_STRING_DQ_540] = "eSOS.STRING_DQ ::= &STRING_DQ .";
    artLabelStrings[ARTL_eSOS_STRING_DQ_540] = "";
    artlhsL[ARTL_eSOS_STRING_DQ_540] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_STRING_DQ_540] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_STRING_DQ_540] = true;
    arteoR_pL[ARTL_eSOS_STRING_DQ_540] = true;
    artPopD[ARTL_eSOS_STRING_DQ_540] = true;
  }

  public void artTableInitialiser_eSOS_STRING_SQ() {
    artLabelInternalStrings[ARTL_eSOS_STRING_SQ] = "eSOS.STRING_SQ";
    artLabelStrings[ARTL_eSOS_STRING_SQ] = "eSOS.STRING_SQ";
    artKindOfs[ARTL_eSOS_STRING_SQ] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_STRING_SQ_544] = "eSOS.STRING_SQ ::= . &STRING_SQ ";
    artLabelStrings[ARTL_eSOS_STRING_SQ_544] = "";
    artlhsL[ARTL_eSOS_STRING_SQ_544] = ARTL_eSOS_STRING_SQ;
    artKindOfs[ARTL_eSOS_STRING_SQ_544] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_STRING_SQ_544] = true;
    artLabelInternalStrings[ARTL_eSOS_STRING_SQ_545] = "eSOS.STRING_SQ ::= &STRING_SQ ";
    artLabelStrings[ARTL_eSOS_STRING_SQ_545] = "";
    artlhsL[ARTL_eSOS_STRING_SQ_545] = ARTL_eSOS_STRING_SQ;
    artPopD[ARTL_eSOS_STRING_SQ_545] = true;
    artLabelInternalStrings[ARTL_eSOS_STRING_SQ_546] = "eSOS.STRING_SQ ::= &STRING_SQ .";
    artLabelStrings[ARTL_eSOS_STRING_SQ_546] = "";
    artlhsL[ARTL_eSOS_STRING_SQ_546] = ARTL_eSOS_STRING_SQ;
    artKindOfs[ARTL_eSOS_STRING_SQ_546] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_STRING_SQ_546] = true;
    arteoR_pL[ARTL_eSOS_STRING_SQ_546] = true;
    artPopD[ARTL_eSOS_STRING_SQ_546] = true;
  }

  public void artTableInitialiser_eSOS_conditions() {
    artLabelInternalStrings[ARTL_eSOS_conditions] = "eSOS.conditions";
    artLabelStrings[ARTL_eSOS_conditions] = "eSOS.conditions";
    artKindOfs[ARTL_eSOS_conditions] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_conditions_262] = "eSOS.conditions ::= . # ";
    artLabelStrings[ARTL_eSOS_conditions_262] = "";
    artlhsL[ARTL_eSOS_conditions_262] = ARTL_eSOS_conditions;
    artKindOfs[ARTL_eSOS_conditions_262] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_conditions_262] = true;
    artLabelInternalStrings[ARTL_eSOS_conditions_264] = "eSOS.conditions ::= # .";
    artLabelStrings[ARTL_eSOS_conditions_264] = "";
    artlhsL[ARTL_eSOS_conditions_264] = ARTL_eSOS_conditions;
    artKindOfs[ARTL_eSOS_conditions_264] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_conditions_264] = true;
    arteoR_pL[ARTL_eSOS_conditions_264] = true;
    artPopD[ARTL_eSOS_conditions_264] = true;
    artLabelInternalStrings[ARTL_eSOS_conditions_266] = "eSOS.conditions ::= . eSOS.transition eSOS.conditions ";
    artLabelStrings[ARTL_eSOS_conditions_266] = "";
    artlhsL[ARTL_eSOS_conditions_266] = ARTL_eSOS_conditions;
    artKindOfs[ARTL_eSOS_conditions_266] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_conditions_268] = "eSOS.conditions ::= eSOS.transition . eSOS.conditions ";
    artLabelStrings[ARTL_eSOS_conditions_268] = "";
    artlhsL[ARTL_eSOS_conditions_268] = ARTL_eSOS_conditions;
    artSlotInstanceOfs[ARTL_eSOS_conditions_268] = ARTL_eSOS_transition;
    artKindOfs[ARTL_eSOS_conditions_268] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_conditions_268] = true;
    artLabelInternalStrings[ARTL_eSOS_conditions_272] = "eSOS.conditions ::= eSOS.transition eSOS.conditions .";
    artLabelStrings[ARTL_eSOS_conditions_272] = "";
    artlhsL[ARTL_eSOS_conditions_272] = ARTL_eSOS_conditions;
    artSlotInstanceOfs[ARTL_eSOS_conditions_272] = ARTL_eSOS_conditions;
    artKindOfs[ARTL_eSOS_conditions_272] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_conditions_272] = true;
    arteoR_pL[ARTL_eSOS_conditions_272] = true;
    artPopD[ARTL_eSOS_conditions_272] = true;
    artLabelInternalStrings[ARTL_eSOS_conditions_274] = "eSOS.conditions ::= . eSOS.sideCondition eSOS.conditions ";
    artLabelStrings[ARTL_eSOS_conditions_274] = "";
    artlhsL[ARTL_eSOS_conditions_274] = ARTL_eSOS_conditions;
    artKindOfs[ARTL_eSOS_conditions_274] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_conditions_276] = "eSOS.conditions ::= eSOS.sideCondition . eSOS.conditions ";
    artLabelStrings[ARTL_eSOS_conditions_276] = "";
    artlhsL[ARTL_eSOS_conditions_276] = ARTL_eSOS_conditions;
    artSlotInstanceOfs[ARTL_eSOS_conditions_276] = ARTL_eSOS_sideCondition;
    artKindOfs[ARTL_eSOS_conditions_276] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_conditions_276] = true;
    artLabelInternalStrings[ARTL_eSOS_conditions_280] = "eSOS.conditions ::= eSOS.sideCondition eSOS.conditions .";
    artLabelStrings[ARTL_eSOS_conditions_280] = "";
    artlhsL[ARTL_eSOS_conditions_280] = ARTL_eSOS_conditions;
    artSlotInstanceOfs[ARTL_eSOS_conditions_280] = ARTL_eSOS_conditions;
    artKindOfs[ARTL_eSOS_conditions_280] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_conditions_280] = true;
    arteoR_pL[ARTL_eSOS_conditions_280] = true;
    artPopD[ARTL_eSOS_conditions_280] = true;
  }

  public void artTableInitialiser_eSOS_configuration() {
    artLabelInternalStrings[ARTL_eSOS_configuration] = "eSOS.configuration";
    artLabelStrings[ARTL_eSOS_configuration] = "eSOS.configuration";
    artKindOfs[ARTL_eSOS_configuration] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_configuration_296] = "eSOS.configuration ::= . eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_configuration_296] = "";
    artlhsL[ARTL_eSOS_configuration_296] = ARTL_eSOS_configuration;
    artKindOfs[ARTL_eSOS_configuration_296] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_configuration_298] = "eSOS.configuration ::= eSOS.term . eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_configuration_298] = "";
    artlhsL[ARTL_eSOS_configuration_298] = ARTL_eSOS_configuration;
    artSlotInstanceOfs[ARTL_eSOS_configuration_298] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_configuration_298] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_configuration_298] = true;
    artLabelInternalStrings[ARTL_eSOS_configuration_302] = "eSOS.configuration ::= eSOS.term eSOS.entityReferences .";
    artLabelStrings[ARTL_eSOS_configuration_302] = "";
    artlhsL[ARTL_eSOS_configuration_302] = ARTL_eSOS_configuration;
    artSlotInstanceOfs[ARTL_eSOS_configuration_302] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_configuration_302] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_configuration_302] = true;
    arteoR_pL[ARTL_eSOS_configuration_302] = true;
    artPopD[ARTL_eSOS_configuration_302] = true;
  }

  public void artTableInitialiser_eSOS_element() {
    artLabelInternalStrings[ARTL_eSOS_element] = "eSOS.element";
    artLabelStrings[ARTL_eSOS_element] = "eSOS.element";
    artKindOfs[ARTL_eSOS_element] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_element_14] = "eSOS.element ::= . 'latex' eSOS.latexElement eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_element_14] = "";
    artlhsL[ARTL_eSOS_element_14] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_14] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_15] = "eSOS.element ::= 'latex' eSOS.latexElement eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_element_15] = "";
    artlhsL[ARTL_eSOS_element_15] = ARTL_eSOS_element;
    artLabelInternalStrings[ARTL_eSOS_element_16] = "eSOS.element ::= 'latex' . eSOS.latexElement eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_element_16] = "";
    artlhsL[ARTL_eSOS_element_16] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_16] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_element_16] = true;
    artLabelInternalStrings[ARTL_eSOS_element_18] = "eSOS.element ::= 'latex' eSOS.latexElement . eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_element_18] = "";
    artlhsL[ARTL_eSOS_element_18] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_18] = ARTL_eSOS_latexElement;
    artKindOfs[ARTL_eSOS_element_18] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_20] = "eSOS.element ::= 'latex' eSOS.latexElement eSOS.latexElements .";
    artLabelStrings[ARTL_eSOS_element_20] = "";
    artlhsL[ARTL_eSOS_element_20] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_20] = ARTL_eSOS_latexElements;
    artKindOfs[ARTL_eSOS_element_20] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_element_20] = true;
    arteoR_pL[ARTL_eSOS_element_20] = true;
    artPopD[ARTL_eSOS_element_20] = true;
    artLabelInternalStrings[ARTL_eSOS_element_22] = "eSOS.element ::= . 'relation' eSOS.RELATION eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_element_22] = "";
    artlhsL[ARTL_eSOS_element_22] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_22] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_23] = "eSOS.element ::= 'relation' eSOS.RELATION eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_element_23] = "";
    artlhsL[ARTL_eSOS_element_23] = ARTL_eSOS_element;
    artLabelInternalStrings[ARTL_eSOS_element_24] = "eSOS.element ::= 'relation' . eSOS.RELATION eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_element_24] = "";
    artlhsL[ARTL_eSOS_element_24] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_24] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_element_24] = true;
    artLabelInternalStrings[ARTL_eSOS_element_26] = "eSOS.element ::= 'relation' eSOS.RELATION . eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_element_26] = "";
    artlhsL[ARTL_eSOS_element_26] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_26] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_element_26] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_30] = "eSOS.element ::= 'relation' eSOS.RELATION eSOS.relationElements .";
    artLabelStrings[ARTL_eSOS_element_30] = "";
    artlhsL[ARTL_eSOS_element_30] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_30] = ARTL_eSOS_relationElements;
    artKindOfs[ARTL_eSOS_element_30] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_element_30] = true;
    arteoR_pL[ARTL_eSOS_element_30] = true;
    artPopD[ARTL_eSOS_element_30] = true;
    artLabelInternalStrings[ARTL_eSOS_element_32] = "eSOS.element ::= . 'relation' eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_element_32] = "";
    artlhsL[ARTL_eSOS_element_32] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_32] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_33] = "eSOS.element ::= 'relation' eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_element_33] = "";
    artlhsL[ARTL_eSOS_element_33] = ARTL_eSOS_element;
    artLabelInternalStrings[ARTL_eSOS_element_34] = "eSOS.element ::= 'relation' . eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_element_34] = "";
    artlhsL[ARTL_eSOS_element_34] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_34] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_element_34] = true;
    artLabelInternalStrings[ARTL_eSOS_element_38] = "eSOS.element ::= 'relation' eSOS.relationElements .";
    artLabelStrings[ARTL_eSOS_element_38] = "";
    artlhsL[ARTL_eSOS_element_38] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_38] = ARTL_eSOS_relationElements;
    artKindOfs[ARTL_eSOS_element_38] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_element_38] = true;
    arteoR_pL[ARTL_eSOS_element_38] = true;
    artPopD[ARTL_eSOS_element_38] = true;
    artLabelInternalStrings[ARTL_eSOS_element_40] = "eSOS.element ::= . 'priority' eSOS.priority eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_element_40] = "";
    artlhsL[ARTL_eSOS_element_40] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_40] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_41] = "eSOS.element ::= 'priority' eSOS.priority eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_element_41] = "";
    artlhsL[ARTL_eSOS_element_41] = ARTL_eSOS_element;
    artLabelInternalStrings[ARTL_eSOS_element_42] = "eSOS.element ::= 'priority' . eSOS.priority eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_element_42] = "";
    artlhsL[ARTL_eSOS_element_42] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_42] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_element_42] = true;
    artLabelInternalStrings[ARTL_eSOS_element_44] = "eSOS.element ::= 'priority' eSOS.priority . eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_element_44] = "";
    artlhsL[ARTL_eSOS_element_44] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_44] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_element_44] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_46] = "eSOS.element ::= 'priority' eSOS.priority eSOS.priorities .";
    artLabelStrings[ARTL_eSOS_element_46] = "";
    artlhsL[ARTL_eSOS_element_46] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_46] = ARTL_eSOS_priorities;
    artKindOfs[ARTL_eSOS_element_46] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_element_46] = true;
    arteoR_pL[ARTL_eSOS_element_46] = true;
    artPopD[ARTL_eSOS_element_46] = true;
    artLabelInternalStrings[ARTL_eSOS_element_48] = "eSOS.element ::= . eSOS.rule ";
    artLabelStrings[ARTL_eSOS_element_48] = "";
    artlhsL[ARTL_eSOS_element_48] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_48] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_50] = "eSOS.element ::= eSOS.rule .";
    artLabelStrings[ARTL_eSOS_element_50] = "";
    artlhsL[ARTL_eSOS_element_50] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_50] = ARTL_eSOS_rule;
    artKindOfs[ARTL_eSOS_element_50] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_element_50] = true;
    arteoR_pL[ARTL_eSOS_element_50] = true;
    artPopD[ARTL_eSOS_element_50] = true;
    artLabelInternalStrings[ARTL_eSOS_element_52] = "eSOS.element ::= . 'test' eSOS.test ";
    artLabelStrings[ARTL_eSOS_element_52] = "";
    artlhsL[ARTL_eSOS_element_52] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_52] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_element_53] = "eSOS.element ::= 'test' eSOS.test ";
    artLabelStrings[ARTL_eSOS_element_53] = "";
    artlhsL[ARTL_eSOS_element_53] = ARTL_eSOS_element;
    artLabelInternalStrings[ARTL_eSOS_element_54] = "eSOS.element ::= 'test' . eSOS.test ";
    artLabelStrings[ARTL_eSOS_element_54] = "";
    artlhsL[ARTL_eSOS_element_54] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_element_54] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_element_54] = true;
    artLabelInternalStrings[ARTL_eSOS_element_56] = "eSOS.element ::= 'test' eSOS.test .";
    artLabelStrings[ARTL_eSOS_element_56] = "";
    artlhsL[ARTL_eSOS_element_56] = ARTL_eSOS_element;
    artSlotInstanceOfs[ARTL_eSOS_element_56] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_element_56] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_element_56] = true;
    arteoR_pL[ARTL_eSOS_element_56] = true;
    artPopD[ARTL_eSOS_element_56] = true;
  }

  public void artTableInitialiser_eSOS_entityReferences() {
    artLabelInternalStrings[ARTL_eSOS_entityReferences] = "eSOS.entityReferences";
    artLabelStrings[ARTL_eSOS_entityReferences] = "eSOS.entityReferences";
    artKindOfs[ARTL_eSOS_entityReferences] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_304] = "eSOS.entityReferences ::= . # ";
    artLabelStrings[ARTL_eSOS_entityReferences_304] = "";
    artlhsL[ARTL_eSOS_entityReferences_304] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_304] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_entityReferences_304] = true;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_306] = "eSOS.entityReferences ::= # .";
    artLabelStrings[ARTL_eSOS_entityReferences_306] = "";
    artlhsL[ARTL_eSOS_entityReferences_306] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_306] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_entityReferences_306] = true;
    arteoR_pL[ARTL_eSOS_entityReferences_306] = true;
    artPopD[ARTL_eSOS_entityReferences_306] = true;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_308] = "eSOS.entityReferences ::= . ',' eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_308] = "";
    artlhsL[ARTL_eSOS_entityReferences_308] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_308] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_309] = "eSOS.entityReferences ::= ',' eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_309] = "";
    artlhsL[ARTL_eSOS_entityReferences_309] = ARTL_eSOS_entityReferences;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_310] = "eSOS.entityReferences ::= ',' . eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_310] = "";
    artlhsL[ARTL_eSOS_entityReferences_310] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_310] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_entityReferences_310] = true;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_312] = "eSOS.entityReferences ::= ',' eSOS.term . eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_312] = "";
    artlhsL[ARTL_eSOS_entityReferences_312] = ARTL_eSOS_entityReferences;
    artSlotInstanceOfs[ARTL_eSOS_entityReferences_312] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_entityReferences_312] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_316] = "eSOS.entityReferences ::= ',' eSOS.term eSOS.entityReferences .";
    artLabelStrings[ARTL_eSOS_entityReferences_316] = "";
    artlhsL[ARTL_eSOS_entityReferences_316] = ARTL_eSOS_entityReferences;
    artSlotInstanceOfs[ARTL_eSOS_entityReferences_316] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_316] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_entityReferences_316] = true;
    arteoR_pL[ARTL_eSOS_entityReferences_316] = true;
    artPopD[ARTL_eSOS_entityReferences_316] = true;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_318] = "eSOS.entityReferences ::= . ',' eSOS.ID_SOS '=' eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_318] = "";
    artlhsL[ARTL_eSOS_entityReferences_318] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_318] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_319] = "eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_319] = "";
    artlhsL[ARTL_eSOS_entityReferences_319] = ARTL_eSOS_entityReferences;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_320] = "eSOS.entityReferences ::= ',' . eSOS.ID_SOS '=' eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_320] = "";
    artlhsL[ARTL_eSOS_entityReferences_320] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_320] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_entityReferences_320] = true;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_322] = "eSOS.entityReferences ::= ',' eSOS.ID_SOS . '=' eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_322] = "";
    artlhsL[ARTL_eSOS_entityReferences_322] = ARTL_eSOS_entityReferences;
    artSlotInstanceOfs[ARTL_eSOS_entityReferences_322] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_entityReferences_322] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_323] = "eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_323] = "";
    artlhsL[ARTL_eSOS_entityReferences_323] = ARTL_eSOS_entityReferences;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_324] = "eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' . eSOS.term eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_324] = "";
    artlhsL[ARTL_eSOS_entityReferences_324] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_324] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_326] = "eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' eSOS.term . eSOS.entityReferences ";
    artLabelStrings[ARTL_eSOS_entityReferences_326] = "";
    artlhsL[ARTL_eSOS_entityReferences_326] = ARTL_eSOS_entityReferences;
    artSlotInstanceOfs[ARTL_eSOS_entityReferences_326] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_entityReferences_326] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_entityReferences_330] = "eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' eSOS.term eSOS.entityReferences .";
    artLabelStrings[ARTL_eSOS_entityReferences_330] = "";
    artlhsL[ARTL_eSOS_entityReferences_330] = ARTL_eSOS_entityReferences;
    artSlotInstanceOfs[ARTL_eSOS_entityReferences_330] = ARTL_eSOS_entityReferences;
    artKindOfs[ARTL_eSOS_entityReferences_330] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_entityReferences_330] = true;
    arteoR_pL[ARTL_eSOS_entityReferences_330] = true;
    artPopD[ARTL_eSOS_entityReferences_330] = true;
  }

  public void artTableInitialiser_eSOS_labelOpt() {
    artLabelInternalStrings[ARTL_eSOS_labelOpt] = "eSOS.labelOpt";
    artLabelStrings[ARTL_eSOS_labelOpt] = "eSOS.labelOpt";
    artKindOfs[ARTL_eSOS_labelOpt] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_240] = "eSOS.labelOpt ::= . # ";
    artLabelStrings[ARTL_eSOS_labelOpt_240] = "";
    artlhsL[ARTL_eSOS_labelOpt_240] = ARTL_eSOS_labelOpt;
    artKindOfs[ARTL_eSOS_labelOpt_240] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_labelOpt_240] = true;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_242] = "eSOS.labelOpt ::= # .";
    artLabelStrings[ARTL_eSOS_labelOpt_242] = "";
    artlhsL[ARTL_eSOS_labelOpt_242] = ARTL_eSOS_labelOpt;
    artKindOfs[ARTL_eSOS_labelOpt_242] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_labelOpt_242] = true;
    arteoR_pL[ARTL_eSOS_labelOpt_242] = true;
    artPopD[ARTL_eSOS_labelOpt_242] = true;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_246] = "eSOS.labelOpt ::= . '-' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_labelOpt_246] = "";
    artlhsL[ARTL_eSOS_labelOpt_246] = ARTL_eSOS_labelOpt;
    artKindOfs[ARTL_eSOS_labelOpt_246] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_247] = "eSOS.labelOpt ::= '-' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_labelOpt_247] = "";
    artlhsL[ARTL_eSOS_labelOpt_247] = ARTL_eSOS_labelOpt;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_248] = "eSOS.labelOpt ::= '-' . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_labelOpt_248] = "";
    artlhsL[ARTL_eSOS_labelOpt_248] = ARTL_eSOS_labelOpt;
    artKindOfs[ARTL_eSOS_labelOpt_248] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_labelOpt_248] = true;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_250] = "eSOS.labelOpt ::= '-' eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_labelOpt_250] = "";
    artlhsL[ARTL_eSOS_labelOpt_250] = ARTL_eSOS_labelOpt;
    artSlotInstanceOfs[ARTL_eSOS_labelOpt_250] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_labelOpt_250] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_labelOpt_250] = true;
    arteoR_pL[ARTL_eSOS_labelOpt_250] = true;
    artPopD[ARTL_eSOS_labelOpt_250] = true;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_254] = "eSOS.labelOpt ::= . '-' eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_labelOpt_254] = "";
    artlhsL[ARTL_eSOS_labelOpt_254] = ARTL_eSOS_labelOpt;
    artKindOfs[ARTL_eSOS_labelOpt_254] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_255] = "eSOS.labelOpt ::= '-' eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_labelOpt_255] = "";
    artlhsL[ARTL_eSOS_labelOpt_255] = ARTL_eSOS_labelOpt;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_256] = "eSOS.labelOpt ::= '-' . eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_labelOpt_256] = "";
    artlhsL[ARTL_eSOS_labelOpt_256] = ARTL_eSOS_labelOpt;
    artKindOfs[ARTL_eSOS_labelOpt_256] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_labelOpt_256] = true;
    artLabelInternalStrings[ARTL_eSOS_labelOpt_258] = "eSOS.labelOpt ::= '-' eSOS.STRING_DQ .";
    artLabelStrings[ARTL_eSOS_labelOpt_258] = "";
    artlhsL[ARTL_eSOS_labelOpt_258] = ARTL_eSOS_labelOpt;
    artSlotInstanceOfs[ARTL_eSOS_labelOpt_258] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_labelOpt_258] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_labelOpt_258] = true;
    arteoR_pL[ARTL_eSOS_labelOpt_258] = true;
    artPopD[ARTL_eSOS_labelOpt_258] = true;
  }

  public void artTableInitialiser_eSOS_latexElement() {
    artLabelInternalStrings[ARTL_eSOS_latexElement] = "eSOS.latexElement";
    artLabelStrings[ARTL_eSOS_latexElement] = "eSOS.latexElement";
    artKindOfs[ARTL_eSOS_latexElement] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_latexElement_70] = "eSOS.latexElement ::= . eSOS.ID_SOS eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_latexElement_70] = "";
    artlhsL[ARTL_eSOS_latexElement_70] = ARTL_eSOS_latexElement;
    artKindOfs[ARTL_eSOS_latexElement_70] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_latexElement_72] = "eSOS.latexElement ::= eSOS.ID_SOS . eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_latexElement_72] = "";
    artlhsL[ARTL_eSOS_latexElement_72] = ARTL_eSOS_latexElement;
    artSlotInstanceOfs[ARTL_eSOS_latexElement_72] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_latexElement_72] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_latexElement_72] = true;
    artLabelInternalStrings[ARTL_eSOS_latexElement_74] = "eSOS.latexElement ::= eSOS.ID_SOS eSOS.STRING_DQ .";
    artLabelStrings[ARTL_eSOS_latexElement_74] = "";
    artlhsL[ARTL_eSOS_latexElement_74] = ARTL_eSOS_latexElement;
    artSlotInstanceOfs[ARTL_eSOS_latexElement_74] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_latexElement_74] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_latexElement_74] = true;
    arteoR_pL[ARTL_eSOS_latexElement_74] = true;
    artPopD[ARTL_eSOS_latexElement_74] = true;
    artLabelInternalStrings[ARTL_eSOS_latexElement_78] = "eSOS.latexElement ::= . eSOS.RELATION eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_latexElement_78] = "";
    artlhsL[ARTL_eSOS_latexElement_78] = ARTL_eSOS_latexElement;
    artKindOfs[ARTL_eSOS_latexElement_78] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_latexElement_80] = "eSOS.latexElement ::= eSOS.RELATION . eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_latexElement_80] = "";
    artlhsL[ARTL_eSOS_latexElement_80] = ARTL_eSOS_latexElement;
    artSlotInstanceOfs[ARTL_eSOS_latexElement_80] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_latexElement_80] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_latexElement_80] = true;
    artLabelInternalStrings[ARTL_eSOS_latexElement_82] = "eSOS.latexElement ::= eSOS.RELATION eSOS.STRING_DQ .";
    artLabelStrings[ARTL_eSOS_latexElement_82] = "";
    artlhsL[ARTL_eSOS_latexElement_82] = ARTL_eSOS_latexElement;
    artSlotInstanceOfs[ARTL_eSOS_latexElement_82] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_latexElement_82] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_latexElement_82] = true;
    arteoR_pL[ARTL_eSOS_latexElement_82] = true;
    artPopD[ARTL_eSOS_latexElement_82] = true;
  }

  public void artTableInitialiser_eSOS_latexElements() {
    artLabelInternalStrings[ARTL_eSOS_latexElements] = "eSOS.latexElements";
    artLabelStrings[ARTL_eSOS_latexElements] = "eSOS.latexElements";
    artKindOfs[ARTL_eSOS_latexElements] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_latexElements_58] = "eSOS.latexElements ::= . ',' eSOS.latexElement eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_latexElements_58] = "";
    artlhsL[ARTL_eSOS_latexElements_58] = ARTL_eSOS_latexElements;
    artKindOfs[ARTL_eSOS_latexElements_58] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_latexElements_59] = "eSOS.latexElements ::= ',' eSOS.latexElement eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_latexElements_59] = "";
    artlhsL[ARTL_eSOS_latexElements_59] = ARTL_eSOS_latexElements;
    artLabelInternalStrings[ARTL_eSOS_latexElements_60] = "eSOS.latexElements ::= ',' . eSOS.latexElement eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_latexElements_60] = "";
    artlhsL[ARTL_eSOS_latexElements_60] = ARTL_eSOS_latexElements;
    artKindOfs[ARTL_eSOS_latexElements_60] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_latexElements_60] = true;
    artLabelInternalStrings[ARTL_eSOS_latexElements_62] = "eSOS.latexElements ::= ',' eSOS.latexElement . eSOS.latexElements ";
    artLabelStrings[ARTL_eSOS_latexElements_62] = "";
    artlhsL[ARTL_eSOS_latexElements_62] = ARTL_eSOS_latexElements;
    artSlotInstanceOfs[ARTL_eSOS_latexElements_62] = ARTL_eSOS_latexElement;
    artKindOfs[ARTL_eSOS_latexElements_62] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_latexElements_64] = "eSOS.latexElements ::= ',' eSOS.latexElement eSOS.latexElements .";
    artLabelStrings[ARTL_eSOS_latexElements_64] = "";
    artlhsL[ARTL_eSOS_latexElements_64] = ARTL_eSOS_latexElements;
    artSlotInstanceOfs[ARTL_eSOS_latexElements_64] = ARTL_eSOS_latexElements;
    artKindOfs[ARTL_eSOS_latexElements_64] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_latexElements_64] = true;
    arteoR_pL[ARTL_eSOS_latexElements_64] = true;
    artPopD[ARTL_eSOS_latexElements_64] = true;
    artLabelInternalStrings[ARTL_eSOS_latexElements_66] = "eSOS.latexElements ::= . # ";
    artLabelStrings[ARTL_eSOS_latexElements_66] = "";
    artlhsL[ARTL_eSOS_latexElements_66] = ARTL_eSOS_latexElements;
    artKindOfs[ARTL_eSOS_latexElements_66] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_latexElements_66] = true;
    artLabelInternalStrings[ARTL_eSOS_latexElements_68] = "eSOS.latexElements ::= # .";
    artLabelStrings[ARTL_eSOS_latexElements_68] = "";
    artlhsL[ARTL_eSOS_latexElements_68] = ARTL_eSOS_latexElements;
    artKindOfs[ARTL_eSOS_latexElements_68] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_latexElements_68] = true;
    arteoR_pL[ARTL_eSOS_latexElements_68] = true;
    artPopD[ARTL_eSOS_latexElements_68] = true;
  }

  public void artTableInitialiser_eSOS_priorities() {
    artLabelInternalStrings[ARTL_eSOS_priorities] = "eSOS.priorities";
    artLabelStrings[ARTL_eSOS_priorities] = "eSOS.priorities";
    artKindOfs[ARTL_eSOS_priorities] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_priorities_168] = "eSOS.priorities ::= . ',' eSOS.priority eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_priorities_168] = "";
    artlhsL[ARTL_eSOS_priorities_168] = ARTL_eSOS_priorities;
    artKindOfs[ARTL_eSOS_priorities_168] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priorities_169] = "eSOS.priorities ::= ',' eSOS.priority eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_priorities_169] = "";
    artlhsL[ARTL_eSOS_priorities_169] = ARTL_eSOS_priorities;
    artLabelInternalStrings[ARTL_eSOS_priorities_170] = "eSOS.priorities ::= ',' . eSOS.priority eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_priorities_170] = "";
    artlhsL[ARTL_eSOS_priorities_170] = ARTL_eSOS_priorities;
    artKindOfs[ARTL_eSOS_priorities_170] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_priorities_170] = true;
    artLabelInternalStrings[ARTL_eSOS_priorities_172] = "eSOS.priorities ::= ',' eSOS.priority . eSOS.priorities ";
    artLabelStrings[ARTL_eSOS_priorities_172] = "";
    artlhsL[ARTL_eSOS_priorities_172] = ARTL_eSOS_priorities;
    artSlotInstanceOfs[ARTL_eSOS_priorities_172] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_priorities_172] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priorities_174] = "eSOS.priorities ::= ',' eSOS.priority eSOS.priorities .";
    artLabelStrings[ARTL_eSOS_priorities_174] = "";
    artlhsL[ARTL_eSOS_priorities_174] = ARTL_eSOS_priorities;
    artSlotInstanceOfs[ARTL_eSOS_priorities_174] = ARTL_eSOS_priorities;
    artKindOfs[ARTL_eSOS_priorities_174] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_priorities_174] = true;
    arteoR_pL[ARTL_eSOS_priorities_174] = true;
    artPopD[ARTL_eSOS_priorities_174] = true;
    artLabelInternalStrings[ARTL_eSOS_priorities_176] = "eSOS.priorities ::= . # ";
    artLabelStrings[ARTL_eSOS_priorities_176] = "";
    artlhsL[ARTL_eSOS_priorities_176] = ARTL_eSOS_priorities;
    artKindOfs[ARTL_eSOS_priorities_176] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_priorities_176] = true;
    artLabelInternalStrings[ARTL_eSOS_priorities_178] = "eSOS.priorities ::= # .";
    artLabelStrings[ARTL_eSOS_priorities_178] = "";
    artlhsL[ARTL_eSOS_priorities_178] = ARTL_eSOS_priorities;
    artKindOfs[ARTL_eSOS_priorities_178] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_priorities_178] = true;
    arteoR_pL[ARTL_eSOS_priorities_178] = true;
    artPopD[ARTL_eSOS_priorities_178] = true;
  }

  public void artTableInitialiser_eSOS_priority() {
    artLabelInternalStrings[ARTL_eSOS_priority] = "eSOS.priority";
    artLabelStrings[ARTL_eSOS_priority] = "eSOS.priority";
    artKindOfs[ARTL_eSOS_priority] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_priority_180] = "eSOS.priority ::= . eSOS.ID_SOS '>' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_180] = "";
    artlhsL[ARTL_eSOS_priority_180] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_priority_180] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priority_182] = "eSOS.priority ::= eSOS.ID_SOS . '>' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_182] = "";
    artlhsL[ARTL_eSOS_priority_182] = ARTL_eSOS_priority;
    artSlotInstanceOfs[ARTL_eSOS_priority_182] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_priority_182] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_priority_182] = true;
    artLabelInternalStrings[ARTL_eSOS_priority_183] = "eSOS.priority ::= eSOS.ID_SOS '>' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_183] = "";
    artlhsL[ARTL_eSOS_priority_183] = ARTL_eSOS_priority;
    artLabelInternalStrings[ARTL_eSOS_priority_184] = "eSOS.priority ::= eSOS.ID_SOS '>' . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_184] = "";
    artlhsL[ARTL_eSOS_priority_184] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_priority_184] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priority_186] = "eSOS.priority ::= eSOS.ID_SOS '>' eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_priority_186] = "";
    artlhsL[ARTL_eSOS_priority_186] = ARTL_eSOS_priority;
    artSlotInstanceOfs[ARTL_eSOS_priority_186] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_priority_186] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_priority_186] = true;
    arteoR_pL[ARTL_eSOS_priority_186] = true;
    artPopD[ARTL_eSOS_priority_186] = true;
    artLabelInternalStrings[ARTL_eSOS_priority_190] = "eSOS.priority ::= . eSOS.ID_SOS '<' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_190] = "";
    artlhsL[ARTL_eSOS_priority_190] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_priority_190] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priority_192] = "eSOS.priority ::= eSOS.ID_SOS . '<' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_192] = "";
    artlhsL[ARTL_eSOS_priority_192] = ARTL_eSOS_priority;
    artSlotInstanceOfs[ARTL_eSOS_priority_192] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_priority_192] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_priority_192] = true;
    artLabelInternalStrings[ARTL_eSOS_priority_193] = "eSOS.priority ::= eSOS.ID_SOS '<' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_193] = "";
    artlhsL[ARTL_eSOS_priority_193] = ARTL_eSOS_priority;
    artLabelInternalStrings[ARTL_eSOS_priority_194] = "eSOS.priority ::= eSOS.ID_SOS '<' . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_194] = "";
    artlhsL[ARTL_eSOS_priority_194] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_priority_194] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priority_196] = "eSOS.priority ::= eSOS.ID_SOS '<' eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_priority_196] = "";
    artlhsL[ARTL_eSOS_priority_196] = ARTL_eSOS_priority;
    artSlotInstanceOfs[ARTL_eSOS_priority_196] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_priority_196] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_priority_196] = true;
    arteoR_pL[ARTL_eSOS_priority_196] = true;
    artPopD[ARTL_eSOS_priority_196] = true;
    artLabelInternalStrings[ARTL_eSOS_priority_200] = "eSOS.priority ::= . eSOS.ID_SOS '=' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_200] = "";
    artlhsL[ARTL_eSOS_priority_200] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_priority_200] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priority_202] = "eSOS.priority ::= eSOS.ID_SOS . '=' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_202] = "";
    artlhsL[ARTL_eSOS_priority_202] = ARTL_eSOS_priority;
    artSlotInstanceOfs[ARTL_eSOS_priority_202] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_priority_202] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_priority_202] = true;
    artLabelInternalStrings[ARTL_eSOS_priority_203] = "eSOS.priority ::= eSOS.ID_SOS '=' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_203] = "";
    artlhsL[ARTL_eSOS_priority_203] = ARTL_eSOS_priority;
    artLabelInternalStrings[ARTL_eSOS_priority_204] = "eSOS.priority ::= eSOS.ID_SOS '=' . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priority_204] = "";
    artlhsL[ARTL_eSOS_priority_204] = ARTL_eSOS_priority;
    artKindOfs[ARTL_eSOS_priority_204] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priority_206] = "eSOS.priority ::= eSOS.ID_SOS '=' eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_priority_206] = "";
    artlhsL[ARTL_eSOS_priority_206] = ARTL_eSOS_priority;
    artSlotInstanceOfs[ARTL_eSOS_priority_206] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_priority_206] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_priority_206] = true;
    arteoR_pL[ARTL_eSOS_priority_206] = true;
    artPopD[ARTL_eSOS_priority_206] = true;
  }

  public void artTableInitialiser_eSOS_priorityOpt() {
    artLabelInternalStrings[ARTL_eSOS_priorityOpt] = "eSOS.priorityOpt";
    artLabelStrings[ARTL_eSOS_priorityOpt] = "eSOS.priorityOpt";
    artKindOfs[ARTL_eSOS_priorityOpt] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_priorityOpt_226] = "eSOS.priorityOpt ::= . # ";
    artLabelStrings[ARTL_eSOS_priorityOpt_226] = "";
    artlhsL[ARTL_eSOS_priorityOpt_226] = ARTL_eSOS_priorityOpt;
    artKindOfs[ARTL_eSOS_priorityOpt_226] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_priorityOpt_226] = true;
    artLabelInternalStrings[ARTL_eSOS_priorityOpt_228] = "eSOS.priorityOpt ::= # .";
    artLabelStrings[ARTL_eSOS_priorityOpt_228] = "";
    artlhsL[ARTL_eSOS_priorityOpt_228] = ARTL_eSOS_priorityOpt;
    artKindOfs[ARTL_eSOS_priorityOpt_228] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_priorityOpt_228] = true;
    arteoR_pL[ARTL_eSOS_priorityOpt_228] = true;
    artPopD[ARTL_eSOS_priorityOpt_228] = true;
    artLabelInternalStrings[ARTL_eSOS_priorityOpt_232] = "eSOS.priorityOpt ::= . '+' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priorityOpt_232] = "";
    artlhsL[ARTL_eSOS_priorityOpt_232] = ARTL_eSOS_priorityOpt;
    artKindOfs[ARTL_eSOS_priorityOpt_232] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_priorityOpt_233] = "eSOS.priorityOpt ::= '+' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priorityOpt_233] = "";
    artlhsL[ARTL_eSOS_priorityOpt_233] = ARTL_eSOS_priorityOpt;
    artLabelInternalStrings[ARTL_eSOS_priorityOpt_234] = "eSOS.priorityOpt ::= '+' . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_priorityOpt_234] = "";
    artlhsL[ARTL_eSOS_priorityOpt_234] = ARTL_eSOS_priorityOpt;
    artKindOfs[ARTL_eSOS_priorityOpt_234] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_priorityOpt_234] = true;
    artLabelInternalStrings[ARTL_eSOS_priorityOpt_236] = "eSOS.priorityOpt ::= '+' eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_priorityOpt_236] = "";
    artlhsL[ARTL_eSOS_priorityOpt_236] = ARTL_eSOS_priorityOpt;
    artSlotInstanceOfs[ARTL_eSOS_priorityOpt_236] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_priorityOpt_236] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_priorityOpt_236] = true;
    arteoR_pL[ARTL_eSOS_priorityOpt_236] = true;
    artPopD[ARTL_eSOS_priorityOpt_236] = true;
  }

  public void artTableInitialiser_eSOS_relationElement() {
    artLabelInternalStrings[ARTL_eSOS_relationElement] = "eSOS.relationElement";
    artLabelStrings[ARTL_eSOS_relationElement] = "eSOS.relationElement";
    artKindOfs[ARTL_eSOS_relationElement] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_relationElement_98] = "eSOS.relationElement ::= . eSOS.ID_SOS ':' 'map' ";
    artLabelStrings[ARTL_eSOS_relationElement_98] = "";
    artlhsL[ARTL_eSOS_relationElement_98] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_98] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_100] = "eSOS.relationElement ::= eSOS.ID_SOS . ':' 'map' ";
    artLabelStrings[ARTL_eSOS_relationElement_100] = "";
    artlhsL[ARTL_eSOS_relationElement_100] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_100] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_100] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_relationElement_100] = true;
    artPopD[ARTL_eSOS_relationElement_100] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_101] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'map' ";
    artLabelStrings[ARTL_eSOS_relationElement_101] = "";
    artlhsL[ARTL_eSOS_relationElement_101] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_101] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_102] = "eSOS.relationElement ::= eSOS.ID_SOS ':' . 'map' ";
    artLabelStrings[ARTL_eSOS_relationElement_102] = "";
    artlhsL[ARTL_eSOS_relationElement_102] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_102] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationElement_102] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_103] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'map' ";
    artLabelStrings[ARTL_eSOS_relationElement_103] = "";
    artlhsL[ARTL_eSOS_relationElement_103] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_103] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_104] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'map' .";
    artLabelStrings[ARTL_eSOS_relationElement_104] = "";
    artlhsL[ARTL_eSOS_relationElement_104] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_104] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElement_104] = true;
    arteoR_pL[ARTL_eSOS_relationElement_104] = true;
    artPopD[ARTL_eSOS_relationElement_104] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_108] = "eSOS.relationElement ::= . eSOS.ID_SOS ':' 'mapFixed' ";
    artLabelStrings[ARTL_eSOS_relationElement_108] = "";
    artlhsL[ARTL_eSOS_relationElement_108] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_108] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_110] = "eSOS.relationElement ::= eSOS.ID_SOS . ':' 'mapFixed' ";
    artLabelStrings[ARTL_eSOS_relationElement_110] = "";
    artlhsL[ARTL_eSOS_relationElement_110] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_110] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_110] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_relationElement_110] = true;
    artPopD[ARTL_eSOS_relationElement_110] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_111] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'mapFixed' ";
    artLabelStrings[ARTL_eSOS_relationElement_111] = "";
    artlhsL[ARTL_eSOS_relationElement_111] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_111] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_112] = "eSOS.relationElement ::= eSOS.ID_SOS ':' . 'mapFixed' ";
    artLabelStrings[ARTL_eSOS_relationElement_112] = "";
    artlhsL[ARTL_eSOS_relationElement_112] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_112] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationElement_112] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_113] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'mapFixed' ";
    artLabelStrings[ARTL_eSOS_relationElement_113] = "";
    artlhsL[ARTL_eSOS_relationElement_113] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_113] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_114] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'mapFixed' .";
    artLabelStrings[ARTL_eSOS_relationElement_114] = "";
    artlhsL[ARTL_eSOS_relationElement_114] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_114] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElement_114] = true;
    arteoR_pL[ARTL_eSOS_relationElement_114] = true;
    artPopD[ARTL_eSOS_relationElement_114] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_118] = "eSOS.relationElement ::= . eSOS.ID_SOS ':' 'listIn' ";
    artLabelStrings[ARTL_eSOS_relationElement_118] = "";
    artlhsL[ARTL_eSOS_relationElement_118] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_118] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_120] = "eSOS.relationElement ::= eSOS.ID_SOS . ':' 'listIn' ";
    artLabelStrings[ARTL_eSOS_relationElement_120] = "";
    artlhsL[ARTL_eSOS_relationElement_120] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_120] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_120] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_relationElement_120] = true;
    artPopD[ARTL_eSOS_relationElement_120] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_121] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'listIn' ";
    artLabelStrings[ARTL_eSOS_relationElement_121] = "";
    artlhsL[ARTL_eSOS_relationElement_121] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_121] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_122] = "eSOS.relationElement ::= eSOS.ID_SOS ':' . 'listIn' ";
    artLabelStrings[ARTL_eSOS_relationElement_122] = "";
    artlhsL[ARTL_eSOS_relationElement_122] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_122] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationElement_122] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_123] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'listIn' ";
    artLabelStrings[ARTL_eSOS_relationElement_123] = "";
    artlhsL[ARTL_eSOS_relationElement_123] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_123] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_124] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'listIn' .";
    artLabelStrings[ARTL_eSOS_relationElement_124] = "";
    artlhsL[ARTL_eSOS_relationElement_124] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_124] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElement_124] = true;
    arteoR_pL[ARTL_eSOS_relationElement_124] = true;
    artPopD[ARTL_eSOS_relationElement_124] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_128] = "eSOS.relationElement ::= . eSOS.ID_SOS ':' 'listOut' ";
    artLabelStrings[ARTL_eSOS_relationElement_128] = "";
    artlhsL[ARTL_eSOS_relationElement_128] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_128] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_130] = "eSOS.relationElement ::= eSOS.ID_SOS . ':' 'listOut' ";
    artLabelStrings[ARTL_eSOS_relationElement_130] = "";
    artlhsL[ARTL_eSOS_relationElement_130] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_130] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_130] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_relationElement_130] = true;
    artPopD[ARTL_eSOS_relationElement_130] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_131] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'listOut' ";
    artLabelStrings[ARTL_eSOS_relationElement_131] = "";
    artlhsL[ARTL_eSOS_relationElement_131] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_131] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_132] = "eSOS.relationElement ::= eSOS.ID_SOS ':' . 'listOut' ";
    artLabelStrings[ARTL_eSOS_relationElement_132] = "";
    artlhsL[ARTL_eSOS_relationElement_132] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_132] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationElement_132] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_133] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'listOut' ";
    artLabelStrings[ARTL_eSOS_relationElement_133] = "";
    artlhsL[ARTL_eSOS_relationElement_133] = ARTL_eSOS_relationElement;
    artPopD[ARTL_eSOS_relationElement_133] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_134] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'listOut' .";
    artLabelStrings[ARTL_eSOS_relationElement_134] = "";
    artlhsL[ARTL_eSOS_relationElement_134] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_134] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElement_134] = true;
    arteoR_pL[ARTL_eSOS_relationElement_134] = true;
    artPopD[ARTL_eSOS_relationElement_134] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_138] = "eSOS.relationElement ::= . eSOS.ID_SOS ':' 'singleton' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_138] = "";
    artlhsL[ARTL_eSOS_relationElement_138] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_138] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_140] = "eSOS.relationElement ::= eSOS.ID_SOS . ':' 'singleton' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_140] = "";
    artlhsL[ARTL_eSOS_relationElement_140] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_140] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_140] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_relationElement_140] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_141] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'singleton' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_141] = "";
    artlhsL[ARTL_eSOS_relationElement_141] = ARTL_eSOS_relationElement;
    artLabelInternalStrings[ARTL_eSOS_relationElement_142] = "eSOS.relationElement ::= eSOS.ID_SOS ':' . 'singleton' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_142] = "";
    artlhsL[ARTL_eSOS_relationElement_142] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_142] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_143] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'singleton' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_143] = "";
    artlhsL[ARTL_eSOS_relationElement_143] = ARTL_eSOS_relationElement;
    artLabelInternalStrings[ARTL_eSOS_relationElement_144] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'singleton' . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_144] = "";
    artlhsL[ARTL_eSOS_relationElement_144] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_144] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_146] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'singleton' eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_relationElement_146] = "";
    artlhsL[ARTL_eSOS_relationElement_146] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_146] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_146] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElement_146] = true;
    arteoR_pL[ARTL_eSOS_relationElement_146] = true;
    artPopD[ARTL_eSOS_relationElement_146] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_150] = "eSOS.relationElement ::= . eSOS.ID_SOS ':' 'untyped' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_150] = "";
    artlhsL[ARTL_eSOS_relationElement_150] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_150] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_152] = "eSOS.relationElement ::= eSOS.ID_SOS . ':' 'untyped' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_152] = "";
    artlhsL[ARTL_eSOS_relationElement_152] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_152] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_152] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_relationElement_152] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_153] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'untyped' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_153] = "";
    artlhsL[ARTL_eSOS_relationElement_153] = ARTL_eSOS_relationElement;
    artLabelInternalStrings[ARTL_eSOS_relationElement_154] = "eSOS.relationElement ::= eSOS.ID_SOS ':' . 'untyped' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_154] = "";
    artlhsL[ARTL_eSOS_relationElement_154] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_154] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_155] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'untyped' eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_155] = "";
    artlhsL[ARTL_eSOS_relationElement_155] = ARTL_eSOS_relationElement;
    artLabelInternalStrings[ARTL_eSOS_relationElement_156] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'untyped' . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_relationElement_156] = "";
    artlhsL[ARTL_eSOS_relationElement_156] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_156] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_158] = "eSOS.relationElement ::= eSOS.ID_SOS ':' 'untyped' eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_relationElement_158] = "";
    artlhsL[ARTL_eSOS_relationElement_158] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_158] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_relationElement_158] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElement_158] = true;
    arteoR_pL[ARTL_eSOS_relationElement_158] = true;
    artPopD[ARTL_eSOS_relationElement_158] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElement_162] = "eSOS.relationElement ::= . eSOS.term ";
    artLabelStrings[ARTL_eSOS_relationElement_162] = "";
    artlhsL[ARTL_eSOS_relationElement_162] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElement_162] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElement_164] = "eSOS.relationElement ::= eSOS.term .";
    artLabelStrings[ARTL_eSOS_relationElement_164] = "";
    artlhsL[ARTL_eSOS_relationElement_164] = ARTL_eSOS_relationElement;
    artSlotInstanceOfs[ARTL_eSOS_relationElement_164] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_relationElement_164] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElement_164] = true;
    arteoR_pL[ARTL_eSOS_relationElement_164] = true;
    artPopD[ARTL_eSOS_relationElement_164] = true;
  }

  public void artTableInitialiser_eSOS_relationElements() {
    artLabelInternalStrings[ARTL_eSOS_relationElements] = "eSOS.relationElements";
    artLabelStrings[ARTL_eSOS_relationElements] = "eSOS.relationElements";
    artKindOfs[ARTL_eSOS_relationElements] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_relationElements_86] = "eSOS.relationElements ::= . ',' eSOS.relationElement eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_relationElements_86] = "";
    artlhsL[ARTL_eSOS_relationElements_86] = ARTL_eSOS_relationElements;
    artKindOfs[ARTL_eSOS_relationElements_86] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElements_87] = "eSOS.relationElements ::= ',' eSOS.relationElement eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_relationElements_87] = "";
    artlhsL[ARTL_eSOS_relationElements_87] = ARTL_eSOS_relationElements;
    artLabelInternalStrings[ARTL_eSOS_relationElements_88] = "eSOS.relationElements ::= ',' . eSOS.relationElement eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_relationElements_88] = "";
    artlhsL[ARTL_eSOS_relationElements_88] = ARTL_eSOS_relationElements;
    artKindOfs[ARTL_eSOS_relationElements_88] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_relationElements_88] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElements_90] = "eSOS.relationElements ::= ',' eSOS.relationElement . eSOS.relationElements ";
    artLabelStrings[ARTL_eSOS_relationElements_90] = "";
    artlhsL[ARTL_eSOS_relationElements_90] = ARTL_eSOS_relationElements;
    artSlotInstanceOfs[ARTL_eSOS_relationElements_90] = ARTL_eSOS_relationElement;
    artKindOfs[ARTL_eSOS_relationElements_90] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_relationElements_92] = "eSOS.relationElements ::= ',' eSOS.relationElement eSOS.relationElements .";
    artLabelStrings[ARTL_eSOS_relationElements_92] = "";
    artlhsL[ARTL_eSOS_relationElements_92] = ARTL_eSOS_relationElements;
    artSlotInstanceOfs[ARTL_eSOS_relationElements_92] = ARTL_eSOS_relationElements;
    artKindOfs[ARTL_eSOS_relationElements_92] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElements_92] = true;
    arteoR_pL[ARTL_eSOS_relationElements_92] = true;
    artPopD[ARTL_eSOS_relationElements_92] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElements_94] = "eSOS.relationElements ::= . # ";
    artLabelStrings[ARTL_eSOS_relationElements_94] = "";
    artlhsL[ARTL_eSOS_relationElements_94] = ARTL_eSOS_relationElements;
    artKindOfs[ARTL_eSOS_relationElements_94] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationElements_94] = true;
    artLabelInternalStrings[ARTL_eSOS_relationElements_96] = "eSOS.relationElements ::= # .";
    artLabelStrings[ARTL_eSOS_relationElements_96] = "";
    artlhsL[ARTL_eSOS_relationElements_96] = ARTL_eSOS_relationElements;
    artKindOfs[ARTL_eSOS_relationElements_96] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationElements_96] = true;
    arteoR_pL[ARTL_eSOS_relationElements_96] = true;
    artPopD[ARTL_eSOS_relationElements_96] = true;
  }

  public void artTableInitialiser_eSOS_relationSymbol() {
    artLabelInternalStrings[ARTL_eSOS_relationSymbol] = "eSOS.relationSymbol";
    artLabelStrings[ARTL_eSOS_relationSymbol] = "eSOS.relationSymbol";
    artKindOfs[ARTL_eSOS_relationSymbol] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_488] = "eSOS.relationSymbol ::= . '->' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_488] = "";
    artlhsL[ARTL_eSOS_relationSymbol_488] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_488] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationSymbol_488] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_489] = "eSOS.relationSymbol ::= '->' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_489] = "";
    artlhsL[ARTL_eSOS_relationSymbol_489] = ARTL_eSOS_relationSymbol;
    artPopD[ARTL_eSOS_relationSymbol_489] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_490] = "eSOS.relationSymbol ::= '->' .";
    artLabelStrings[ARTL_eSOS_relationSymbol_490] = "";
    artlhsL[ARTL_eSOS_relationSymbol_490] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_490] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationSymbol_490] = true;
    arteoR_pL[ARTL_eSOS_relationSymbol_490] = true;
    artPopD[ARTL_eSOS_relationSymbol_490] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_492] = "eSOS.relationSymbol ::= . '->*' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_492] = "";
    artlhsL[ARTL_eSOS_relationSymbol_492] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_492] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationSymbol_492] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_493] = "eSOS.relationSymbol ::= '->*' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_493] = "";
    artlhsL[ARTL_eSOS_relationSymbol_493] = ARTL_eSOS_relationSymbol;
    artPopD[ARTL_eSOS_relationSymbol_493] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_494] = "eSOS.relationSymbol ::= '->*' .";
    artLabelStrings[ARTL_eSOS_relationSymbol_494] = "";
    artlhsL[ARTL_eSOS_relationSymbol_494] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_494] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationSymbol_494] = true;
    arteoR_pL[ARTL_eSOS_relationSymbol_494] = true;
    artPopD[ARTL_eSOS_relationSymbol_494] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_496] = "eSOS.relationSymbol ::= . '=>' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_496] = "";
    artlhsL[ARTL_eSOS_relationSymbol_496] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_496] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationSymbol_496] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_497] = "eSOS.relationSymbol ::= '=>' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_497] = "";
    artlhsL[ARTL_eSOS_relationSymbol_497] = ARTL_eSOS_relationSymbol;
    artPopD[ARTL_eSOS_relationSymbol_497] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_498] = "eSOS.relationSymbol ::= '=>' .";
    artLabelStrings[ARTL_eSOS_relationSymbol_498] = "";
    artlhsL[ARTL_eSOS_relationSymbol_498] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_498] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationSymbol_498] = true;
    arteoR_pL[ARTL_eSOS_relationSymbol_498] = true;
    artPopD[ARTL_eSOS_relationSymbol_498] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_500] = "eSOS.relationSymbol ::= . '=>*' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_500] = "";
    artlhsL[ARTL_eSOS_relationSymbol_500] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_500] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationSymbol_500] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_501] = "eSOS.relationSymbol ::= '=>*' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_501] = "";
    artlhsL[ARTL_eSOS_relationSymbol_501] = ARTL_eSOS_relationSymbol;
    artPopD[ARTL_eSOS_relationSymbol_501] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_502] = "eSOS.relationSymbol ::= '=>*' .";
    artLabelStrings[ARTL_eSOS_relationSymbol_502] = "";
    artlhsL[ARTL_eSOS_relationSymbol_502] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_502] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationSymbol_502] = true;
    arteoR_pL[ARTL_eSOS_relationSymbol_502] = true;
    artPopD[ARTL_eSOS_relationSymbol_502] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_504] = "eSOS.relationSymbol ::= . '->>' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_504] = "";
    artlhsL[ARTL_eSOS_relationSymbol_504] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_504] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_relationSymbol_504] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_505] = "eSOS.relationSymbol ::= '->>' ";
    artLabelStrings[ARTL_eSOS_relationSymbol_505] = "";
    artlhsL[ARTL_eSOS_relationSymbol_505] = ARTL_eSOS_relationSymbol;
    artPopD[ARTL_eSOS_relationSymbol_505] = true;
    artLabelInternalStrings[ARTL_eSOS_relationSymbol_506] = "eSOS.relationSymbol ::= '->>' .";
    artLabelStrings[ARTL_eSOS_relationSymbol_506] = "";
    artlhsL[ARTL_eSOS_relationSymbol_506] = ARTL_eSOS_relationSymbol;
    artKindOfs[ARTL_eSOS_relationSymbol_506] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_relationSymbol_506] = true;
    arteoR_pL[ARTL_eSOS_relationSymbol_506] = true;
    artPopD[ARTL_eSOS_relationSymbol_506] = true;
  }

  public void artTableInitialiser_eSOS_rule() {
    artLabelInternalStrings[ARTL_eSOS_rule] = "eSOS.rule";
    artLabelStrings[ARTL_eSOS_rule] = "eSOS.rule";
    artKindOfs[ARTL_eSOS_rule] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_rule_210] = "eSOS.rule ::= . eSOS.labelOpt eSOS.priorityOpt eSOS.conditions '---' eSOS.transition ";
    artLabelStrings[ARTL_eSOS_rule_210] = "";
    artlhsL[ARTL_eSOS_rule_210] = ARTL_eSOS_rule;
    artKindOfs[ARTL_eSOS_rule_210] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_rule_214] = "eSOS.rule ::= eSOS.labelOpt . eSOS.priorityOpt eSOS.conditions '---' eSOS.transition ";
    artLabelStrings[ARTL_eSOS_rule_214] = "";
    artlhsL[ARTL_eSOS_rule_214] = ARTL_eSOS_rule;
    artSlotInstanceOfs[ARTL_eSOS_rule_214] = ARTL_eSOS_labelOpt;
    artKindOfs[ARTL_eSOS_rule_214] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_rule_214] = true;
    artLabelInternalStrings[ARTL_eSOS_rule_216] = "eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt . eSOS.conditions '---' eSOS.transition ";
    artLabelStrings[ARTL_eSOS_rule_216] = "";
    artlhsL[ARTL_eSOS_rule_216] = ARTL_eSOS_rule;
    artSlotInstanceOfs[ARTL_eSOS_rule_216] = ARTL_eSOS_priorityOpt;
    artKindOfs[ARTL_eSOS_rule_216] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_rule_218] = "eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt eSOS.conditions . '---' eSOS.transition ";
    artLabelStrings[ARTL_eSOS_rule_218] = "";
    artlhsL[ARTL_eSOS_rule_218] = ARTL_eSOS_rule;
    artSlotInstanceOfs[ARTL_eSOS_rule_218] = ARTL_eSOS_conditions;
    artKindOfs[ARTL_eSOS_rule_218] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_rule_219] = "eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt eSOS.conditions '---' eSOS.transition ";
    artLabelStrings[ARTL_eSOS_rule_219] = "";
    artlhsL[ARTL_eSOS_rule_219] = ARTL_eSOS_rule;
    artLabelInternalStrings[ARTL_eSOS_rule_220] = "eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt eSOS.conditions '---' . eSOS.transition ";
    artLabelStrings[ARTL_eSOS_rule_220] = "";
    artlhsL[ARTL_eSOS_rule_220] = ARTL_eSOS_rule;
    artKindOfs[ARTL_eSOS_rule_220] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_rule_222] = "eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt eSOS.conditions '---' eSOS.transition .";
    artLabelStrings[ARTL_eSOS_rule_222] = "";
    artlhsL[ARTL_eSOS_rule_222] = ARTL_eSOS_rule;
    artSlotInstanceOfs[ARTL_eSOS_rule_222] = ARTL_eSOS_transition;
    artKindOfs[ARTL_eSOS_rule_222] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_rule_222] = true;
    arteoR_pL[ARTL_eSOS_rule_222] = true;
    artPopD[ARTL_eSOS_rule_222] = true;
  }

  public void artTableInitialiser_eSOS_sideCondition() {
    artLabelInternalStrings[ARTL_eSOS_sideCondition] = "eSOS.sideCondition";
    artLabelStrings[ARTL_eSOS_sideCondition] = "eSOS.sideCondition";
    artKindOfs[ARTL_eSOS_sideCondition] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_sideCondition_332] = "eSOS.sideCondition ::= . eSOS.term '|>' eSOS.term ";
    artLabelStrings[ARTL_eSOS_sideCondition_332] = "";
    artlhsL[ARTL_eSOS_sideCondition_332] = ARTL_eSOS_sideCondition;
    artKindOfs[ARTL_eSOS_sideCondition_332] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_sideCondition_334] = "eSOS.sideCondition ::= eSOS.term . '|>' eSOS.term ";
    artLabelStrings[ARTL_eSOS_sideCondition_334] = "";
    artlhsL[ARTL_eSOS_sideCondition_334] = ARTL_eSOS_sideCondition;
    artSlotInstanceOfs[ARTL_eSOS_sideCondition_334] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_sideCondition_334] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_sideCondition_334] = true;
    artLabelInternalStrings[ARTL_eSOS_sideCondition_335] = "eSOS.sideCondition ::= eSOS.term '|>' eSOS.term ";
    artLabelStrings[ARTL_eSOS_sideCondition_335] = "";
    artlhsL[ARTL_eSOS_sideCondition_335] = ARTL_eSOS_sideCondition;
    artLabelInternalStrings[ARTL_eSOS_sideCondition_336] = "eSOS.sideCondition ::= eSOS.term '|>' . eSOS.term ";
    artLabelStrings[ARTL_eSOS_sideCondition_336] = "";
    artlhsL[ARTL_eSOS_sideCondition_336] = ARTL_eSOS_sideCondition;
    artKindOfs[ARTL_eSOS_sideCondition_336] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_sideCondition_338] = "eSOS.sideCondition ::= eSOS.term '|>' eSOS.term .";
    artLabelStrings[ARTL_eSOS_sideCondition_338] = "";
    artlhsL[ARTL_eSOS_sideCondition_338] = ARTL_eSOS_sideCondition;
    artSlotInstanceOfs[ARTL_eSOS_sideCondition_338] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_sideCondition_338] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_sideCondition_338] = true;
    arteoR_pL[ARTL_eSOS_sideCondition_338] = true;
    artPopD[ARTL_eSOS_sideCondition_338] = true;
  }

  public void artTableInitialiser_eSOS_specification() {
    artLabelInternalStrings[ARTL_eSOS_specification] = "eSOS.specification";
    artLabelStrings[ARTL_eSOS_specification] = "eSOS.specification";
    artKindOfs[ARTL_eSOS_specification] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_specification_2] = "eSOS.specification ::= . eSOS.element eSOS.specification ";
    artLabelStrings[ARTL_eSOS_specification_2] = "";
    artlhsL[ARTL_eSOS_specification_2] = ARTL_eSOS_specification;
    artKindOfs[ARTL_eSOS_specification_2] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_specification_4] = "eSOS.specification ::= eSOS.element . eSOS.specification ";
    artLabelStrings[ARTL_eSOS_specification_4] = "";
    artlhsL[ARTL_eSOS_specification_4] = ARTL_eSOS_specification;
    artSlotInstanceOfs[ARTL_eSOS_specification_4] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_specification_4] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_specification_4] = true;
    artLabelInternalStrings[ARTL_eSOS_specification_6] = "eSOS.specification ::= eSOS.element eSOS.specification .";
    artLabelStrings[ARTL_eSOS_specification_6] = "";
    artlhsL[ARTL_eSOS_specification_6] = ARTL_eSOS_specification;
    artSlotInstanceOfs[ARTL_eSOS_specification_6] = ARTL_eSOS_specification;
    artKindOfs[ARTL_eSOS_specification_6] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_specification_6] = true;
    arteoR_pL[ARTL_eSOS_specification_6] = true;
    artPopD[ARTL_eSOS_specification_6] = true;
    artLabelInternalStrings[ARTL_eSOS_specification_8] = "eSOS.specification ::= . eSOS.element ";
    artLabelStrings[ARTL_eSOS_specification_8] = "";
    artlhsL[ARTL_eSOS_specification_8] = ARTL_eSOS_specification;
    artKindOfs[ARTL_eSOS_specification_8] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_specification_10] = "eSOS.specification ::= eSOS.element .";
    artLabelStrings[ARTL_eSOS_specification_10] = "";
    artlhsL[ARTL_eSOS_specification_10] = ARTL_eSOS_specification;
    artSlotInstanceOfs[ARTL_eSOS_specification_10] = ARTL_eSOS_element;
    artKindOfs[ARTL_eSOS_specification_10] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_specification_10] = true;
    arteoR_pL[ARTL_eSOS_specification_10] = true;
    artPopD[ARTL_eSOS_specification_10] = true;
  }

  public void artTableInitialiser_eSOS_subterms() {
    artLabelInternalStrings[ARTL_eSOS_subterms] = "eSOS.subterms";
    artLabelStrings[ARTL_eSOS_subterms] = "eSOS.subterms";
    artKindOfs[ARTL_eSOS_subterms] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_subterms_360] = "eSOS.subterms ::= . # ";
    artLabelStrings[ARTL_eSOS_subterms_360] = "";
    artlhsL[ARTL_eSOS_subterms_360] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_subterms_360] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_subterms_360] = true;
    artLabelInternalStrings[ARTL_eSOS_subterms_362] = "eSOS.subterms ::= # .";
    artLabelStrings[ARTL_eSOS_subterms_362] = "";
    artlhsL[ARTL_eSOS_subterms_362] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_subterms_362] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_subterms_362] = true;
    arteoR_pL[ARTL_eSOS_subterms_362] = true;
    artPopD[ARTL_eSOS_subterms_362] = true;
    artLabelInternalStrings[ARTL_eSOS_subterms_364] = "eSOS.subterms ::= . eSOS.term ";
    artLabelStrings[ARTL_eSOS_subterms_364] = "";
    artlhsL[ARTL_eSOS_subterms_364] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_subterms_364] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_subterms_366] = "eSOS.subterms ::= eSOS.term .";
    artLabelStrings[ARTL_eSOS_subterms_366] = "";
    artlhsL[ARTL_eSOS_subterms_366] = ARTL_eSOS_subterms;
    artSlotInstanceOfs[ARTL_eSOS_subterms_366] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_subterms_366] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_subterms_366] = true;
    arteoR_pL[ARTL_eSOS_subterms_366] = true;
    artPopD[ARTL_eSOS_subterms_366] = true;
    artLabelInternalStrings[ARTL_eSOS_subterms_370] = "eSOS.subterms ::= . eSOS.term ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_subterms_370] = "";
    artlhsL[ARTL_eSOS_subterms_370] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_subterms_370] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_subterms_372] = "eSOS.subterms ::= eSOS.term . ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_subterms_372] = "";
    artlhsL[ARTL_eSOS_subterms_372] = ARTL_eSOS_subterms;
    artSlotInstanceOfs[ARTL_eSOS_subterms_372] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_subterms_372] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_subterms_372] = true;
    artLabelInternalStrings[ARTL_eSOS_subterms_375] = "eSOS.subterms ::= eSOS.term ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_subterms_375] = "";
    artlhsL[ARTL_eSOS_subterms_375] = ARTL_eSOS_subterms;
    artLabelInternalStrings[ARTL_eSOS_subterms_376] = "eSOS.subterms ::= eSOS.term ',' . eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_subterms_376] = "";
    artlhsL[ARTL_eSOS_subterms_376] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_subterms_376] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_subterms_380] = "eSOS.subterms ::= eSOS.term ',' eSOS.subterms .";
    artLabelStrings[ARTL_eSOS_subterms_380] = "";
    artlhsL[ARTL_eSOS_subterms_380] = ARTL_eSOS_subterms;
    artSlotInstanceOfs[ARTL_eSOS_subterms_380] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_subterms_380] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_subterms_380] = true;
    arteoR_pL[ARTL_eSOS_subterms_380] = true;
    artPopD[ARTL_eSOS_subterms_380] = true;
  }

  public void artTableInitialiser_eSOS_term() {
    artLabelInternalStrings[ARTL_eSOS_term] = "eSOS.term";
    artLabelStrings[ARTL_eSOS_term] = "eSOS.term";
    artKindOfs[ARTL_eSOS_term] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_term_342] = "eSOS.term ::= . eSOS.termElement ";
    artLabelStrings[ARTL_eSOS_term_342] = "";
    artlhsL[ARTL_eSOS_term_342] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_term_342] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_term_344] = "eSOS.term ::= eSOS.termElement .";
    artLabelStrings[ARTL_eSOS_term_344] = "";
    artlhsL[ARTL_eSOS_term_344] = ARTL_eSOS_term;
    artSlotInstanceOfs[ARTL_eSOS_term_344] = ARTL_eSOS_termElement;
    artKindOfs[ARTL_eSOS_term_344] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_term_344] = true;
    arteoR_pL[ARTL_eSOS_term_344] = true;
    artPopD[ARTL_eSOS_term_344] = true;
    artLabelInternalStrings[ARTL_eSOS_term_348] = "eSOS.term ::= . eSOS.ID_SOS '(' eSOS.subterms ')' ";
    artLabelStrings[ARTL_eSOS_term_348] = "";
    artlhsL[ARTL_eSOS_term_348] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_term_348] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_term_350] = "eSOS.term ::= eSOS.ID_SOS . '(' eSOS.subterms ')' ";
    artLabelStrings[ARTL_eSOS_term_350] = "";
    artlhsL[ARTL_eSOS_term_350] = ARTL_eSOS_term;
    artSlotInstanceOfs[ARTL_eSOS_term_350] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_term_350] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_term_350] = true;
    artLabelInternalStrings[ARTL_eSOS_term_353] = "eSOS.term ::= eSOS.ID_SOS '(' eSOS.subterms ')' ";
    artLabelStrings[ARTL_eSOS_term_353] = "";
    artlhsL[ARTL_eSOS_term_353] = ARTL_eSOS_term;
    artLabelInternalStrings[ARTL_eSOS_term_354] = "eSOS.term ::= eSOS.ID_SOS '(' . eSOS.subterms ')' ";
    artLabelStrings[ARTL_eSOS_term_354] = "";
    artlhsL[ARTL_eSOS_term_354] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_term_354] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_term_356] = "eSOS.term ::= eSOS.ID_SOS '(' eSOS.subterms . ')' ";
    artLabelStrings[ARTL_eSOS_term_356] = "";
    artlhsL[ARTL_eSOS_term_356] = ARTL_eSOS_term;
    artSlotInstanceOfs[ARTL_eSOS_term_356] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_term_356] = ARTK_INTERMEDIATE;
    artPopD[ARTL_eSOS_term_356] = true;
    artLabelInternalStrings[ARTL_eSOS_term_357] = "eSOS.term ::= eSOS.ID_SOS '(' eSOS.subterms ')' ";
    artLabelStrings[ARTL_eSOS_term_357] = "";
    artlhsL[ARTL_eSOS_term_357] = ARTL_eSOS_term;
    artPopD[ARTL_eSOS_term_357] = true;
    artLabelInternalStrings[ARTL_eSOS_term_358] = "eSOS.term ::= eSOS.ID_SOS '(' eSOS.subterms ')' .";
    artLabelStrings[ARTL_eSOS_term_358] = "";
    artlhsL[ARTL_eSOS_term_358] = ARTL_eSOS_term;
    artKindOfs[ARTL_eSOS_term_358] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_term_358] = true;
    arteoR_pL[ARTL_eSOS_term_358] = true;
    artPopD[ARTL_eSOS_term_358] = true;
  }

  public void artTableInitialiser_eSOS_termElement() {
    artLabelInternalStrings[ARTL_eSOS_termElement] = "eSOS.termElement";
    artLabelStrings[ARTL_eSOS_termElement] = "eSOS.termElement";
    artKindOfs[ARTL_eSOS_termElement] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_termElement_382] = "eSOS.termElement ::= . eSOS.BOOLEAN ";
    artLabelStrings[ARTL_eSOS_termElement_382] = "";
    artlhsL[ARTL_eSOS_termElement_382] = ARTL_eSOS_termElement;
    artKindOfs[ARTL_eSOS_termElement_382] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_termElement_384] = "eSOS.termElement ::= eSOS.BOOLEAN .";
    artLabelStrings[ARTL_eSOS_termElement_384] = "";
    artlhsL[ARTL_eSOS_termElement_384] = ARTL_eSOS_termElement;
    artSlotInstanceOfs[ARTL_eSOS_termElement_384] = ARTL_eSOS_BOOLEAN;
    artKindOfs[ARTL_eSOS_termElement_384] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_termElement_384] = true;
    arteoR_pL[ARTL_eSOS_termElement_384] = true;
    artPopD[ARTL_eSOS_termElement_384] = true;
    artLabelInternalStrings[ARTL_eSOS_termElement_388] = "eSOS.termElement ::= . eSOS.INTEGER ";
    artLabelStrings[ARTL_eSOS_termElement_388] = "";
    artlhsL[ARTL_eSOS_termElement_388] = ARTL_eSOS_termElement;
    artKindOfs[ARTL_eSOS_termElement_388] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_termElement_390] = "eSOS.termElement ::= eSOS.INTEGER .";
    artLabelStrings[ARTL_eSOS_termElement_390] = "";
    artlhsL[ARTL_eSOS_termElement_390] = ARTL_eSOS_termElement;
    artSlotInstanceOfs[ARTL_eSOS_termElement_390] = ARTL_eSOS_INTEGER;
    artKindOfs[ARTL_eSOS_termElement_390] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_termElement_390] = true;
    arteoR_pL[ARTL_eSOS_termElement_390] = true;
    artPopD[ARTL_eSOS_termElement_390] = true;
    artLabelInternalStrings[ARTL_eSOS_termElement_394] = "eSOS.termElement ::= . eSOS.REAL ";
    artLabelStrings[ARTL_eSOS_termElement_394] = "";
    artlhsL[ARTL_eSOS_termElement_394] = ARTL_eSOS_termElement;
    artKindOfs[ARTL_eSOS_termElement_394] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_termElement_396] = "eSOS.termElement ::= eSOS.REAL .";
    artLabelStrings[ARTL_eSOS_termElement_396] = "";
    artlhsL[ARTL_eSOS_termElement_396] = ARTL_eSOS_termElement;
    artSlotInstanceOfs[ARTL_eSOS_termElement_396] = ARTL_eSOS_REAL;
    artKindOfs[ARTL_eSOS_termElement_396] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_termElement_396] = true;
    arteoR_pL[ARTL_eSOS_termElement_396] = true;
    artPopD[ARTL_eSOS_termElement_396] = true;
    artLabelInternalStrings[ARTL_eSOS_termElement_400] = "eSOS.termElement ::= . eSOS.STRING_DQ ";
    artLabelStrings[ARTL_eSOS_termElement_400] = "";
    artlhsL[ARTL_eSOS_termElement_400] = ARTL_eSOS_termElement;
    artKindOfs[ARTL_eSOS_termElement_400] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_termElement_402] = "eSOS.termElement ::= eSOS.STRING_DQ .";
    artLabelStrings[ARTL_eSOS_termElement_402] = "";
    artlhsL[ARTL_eSOS_termElement_402] = ARTL_eSOS_termElement;
    artSlotInstanceOfs[ARTL_eSOS_termElement_402] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_termElement_402] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_termElement_402] = true;
    arteoR_pL[ARTL_eSOS_termElement_402] = true;
    artPopD[ARTL_eSOS_termElement_402] = true;
    artLabelInternalStrings[ARTL_eSOS_termElement_406] = "eSOS.termElement ::= . eSOS.STRING_SQ ";
    artLabelStrings[ARTL_eSOS_termElement_406] = "";
    artlhsL[ARTL_eSOS_termElement_406] = ARTL_eSOS_termElement;
    artKindOfs[ARTL_eSOS_termElement_406] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_termElement_408] = "eSOS.termElement ::= eSOS.STRING_SQ .";
    artLabelStrings[ARTL_eSOS_termElement_408] = "";
    artlhsL[ARTL_eSOS_termElement_408] = ARTL_eSOS_termElement;
    artSlotInstanceOfs[ARTL_eSOS_termElement_408] = ARTL_eSOS_STRING_SQ;
    artKindOfs[ARTL_eSOS_termElement_408] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_termElement_408] = true;
    arteoR_pL[ARTL_eSOS_termElement_408] = true;
    artPopD[ARTL_eSOS_termElement_408] = true;
    artLabelInternalStrings[ARTL_eSOS_termElement_412] = "eSOS.termElement ::= . eSOS.ID_SOS ";
    artLabelStrings[ARTL_eSOS_termElement_412] = "";
    artlhsL[ARTL_eSOS_termElement_412] = ARTL_eSOS_termElement;
    artKindOfs[ARTL_eSOS_termElement_412] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_termElement_414] = "eSOS.termElement ::= eSOS.ID_SOS .";
    artLabelStrings[ARTL_eSOS_termElement_414] = "";
    artlhsL[ARTL_eSOS_termElement_414] = ARTL_eSOS_termElement;
    artSlotInstanceOfs[ARTL_eSOS_termElement_414] = ARTL_eSOS_ID_SOS;
    artKindOfs[ARTL_eSOS_termElement_414] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_termElement_414] = true;
    arteoR_pL[ARTL_eSOS_termElement_414] = true;
    artPopD[ARTL_eSOS_termElement_414] = true;
  }

  public void artTableInitialiser_eSOS_test() {
    artLabelInternalStrings[ARTL_eSOS_test] = "eSOS.test";
    artLabelStrings[ARTL_eSOS_test] = "eSOS.test";
    artKindOfs[ARTL_eSOS_test] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_test_418] = "eSOS.test ::= . eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_418] = "";
    artlhsL[ARTL_eSOS_test_418] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_418] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_420] = "eSOS.test ::= eSOS.RELATION . ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_420] = "";
    artlhsL[ARTL_eSOS_test_420] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_420] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_test_420] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_test_420] = true;
    artLabelInternalStrings[ARTL_eSOS_test_421] = "eSOS.test ::= eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_421] = "";
    artlhsL[ARTL_eSOS_test_421] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_422] = "eSOS.test ::= eSOS.RELATION ',' . eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_422] = "";
    artlhsL[ARTL_eSOS_test_422] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_422] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_426] = "eSOS.test ::= eSOS.RELATION ',' eSOS.subterms .";
    artLabelStrings[ARTL_eSOS_test_426] = "";
    artlhsL[ARTL_eSOS_test_426] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_426] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_test_426] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_test_426] = true;
    arteoR_pL[ARTL_eSOS_test_426] = true;
    artPopD[ARTL_eSOS_test_426] = true;
    artLabelInternalStrings[ARTL_eSOS_test_430] = "eSOS.test ::= . eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_430] = "";
    artlhsL[ARTL_eSOS_test_430] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_430] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_432] = "eSOS.test ::= eSOS.STRING_DQ . ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_432] = "";
    artlhsL[ARTL_eSOS_test_432] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_432] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_test_432] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_test_432] = true;
    artLabelInternalStrings[ARTL_eSOS_test_433] = "eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_433] = "";
    artlhsL[ARTL_eSOS_test_433] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_434] = "eSOS.test ::= eSOS.STRING_DQ ',' . eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_434] = "";
    artlhsL[ARTL_eSOS_test_434] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_434] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_436] = "eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION . ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_436] = "";
    artlhsL[ARTL_eSOS_test_436] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_436] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_test_436] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_437] = "eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_437] = "";
    artlhsL[ARTL_eSOS_test_437] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_438] = "eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION ',' . eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_438] = "";
    artlhsL[ARTL_eSOS_test_438] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_438] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_442] = "eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms .";
    artLabelStrings[ARTL_eSOS_test_442] = "";
    artlhsL[ARTL_eSOS_test_442] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_442] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_test_442] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_test_442] = true;
    arteoR_pL[ARTL_eSOS_test_442] = true;
    artPopD[ARTL_eSOS_test_442] = true;
    artLabelInternalStrings[ARTL_eSOS_test_446] = "eSOS.test ::= . eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_446] = "";
    artlhsL[ARTL_eSOS_test_446] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_446] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_448] = "eSOS.test ::= eSOS.INTEGER . ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_448] = "";
    artlhsL[ARTL_eSOS_test_448] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_448] = ARTL_eSOS_INTEGER;
    artKindOfs[ARTL_eSOS_test_448] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_test_448] = true;
    artLabelInternalStrings[ARTL_eSOS_test_449] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_449] = "";
    artlhsL[ARTL_eSOS_test_449] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_450] = "eSOS.test ::= eSOS.INTEGER ',' . eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_450] = "";
    artlhsL[ARTL_eSOS_test_450] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_450] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_452] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ . ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_452] = "";
    artlhsL[ARTL_eSOS_test_452] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_452] = ARTL_eSOS_STRING_DQ;
    artKindOfs[ARTL_eSOS_test_452] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_453] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_453] = "";
    artlhsL[ARTL_eSOS_test_453] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_454] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' . eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_454] = "";
    artlhsL[ARTL_eSOS_test_454] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_454] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_456] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION . ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_456] = "";
    artlhsL[ARTL_eSOS_test_456] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_456] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_test_456] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_457] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_457] = "";
    artlhsL[ARTL_eSOS_test_457] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_458] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' . eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_458] = "";
    artlhsL[ARTL_eSOS_test_458] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_458] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_462] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms .";
    artLabelStrings[ARTL_eSOS_test_462] = "";
    artlhsL[ARTL_eSOS_test_462] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_462] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_test_462] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_test_462] = true;
    arteoR_pL[ARTL_eSOS_test_462] = true;
    artPopD[ARTL_eSOS_test_462] = true;
    artLabelInternalStrings[ARTL_eSOS_test_466] = "eSOS.test ::= . eSOS.INTEGER ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_466] = "";
    artlhsL[ARTL_eSOS_test_466] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_466] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_468] = "eSOS.test ::= eSOS.INTEGER . ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_468] = "";
    artlhsL[ARTL_eSOS_test_468] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_468] = ARTL_eSOS_INTEGER;
    artKindOfs[ARTL_eSOS_test_468] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_test_468] = true;
    artLabelInternalStrings[ARTL_eSOS_test_469] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_469] = "";
    artlhsL[ARTL_eSOS_test_469] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_470] = "eSOS.test ::= eSOS.INTEGER ',' . eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_470] = "";
    artlhsL[ARTL_eSOS_test_470] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_470] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_472] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION . ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_472] = "";
    artlhsL[ARTL_eSOS_test_472] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_472] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_test_472] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_473] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION ',' eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_473] = "";
    artlhsL[ARTL_eSOS_test_473] = ARTL_eSOS_test;
    artLabelInternalStrings[ARTL_eSOS_test_474] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION ',' . eSOS.subterms ";
    artLabelStrings[ARTL_eSOS_test_474] = "";
    artlhsL[ARTL_eSOS_test_474] = ARTL_eSOS_test;
    artKindOfs[ARTL_eSOS_test_474] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_test_478] = "eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION ',' eSOS.subterms .";
    artLabelStrings[ARTL_eSOS_test_478] = "";
    artlhsL[ARTL_eSOS_test_478] = ARTL_eSOS_test;
    artSlotInstanceOfs[ARTL_eSOS_test_478] = ARTL_eSOS_subterms;
    artKindOfs[ARTL_eSOS_test_478] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_test_478] = true;
    arteoR_pL[ARTL_eSOS_test_478] = true;
    artPopD[ARTL_eSOS_test_478] = true;
  }

  public void artTableInitialiser_eSOS_transition() {
    artLabelInternalStrings[ARTL_eSOS_transition] = "eSOS.transition";
    artLabelStrings[ARTL_eSOS_transition] = "eSOS.transition";
    artKindOfs[ARTL_eSOS_transition] = ARTK_NONTERMINAL;
    artLabelInternalStrings[ARTL_eSOS_transition_282] = "eSOS.transition ::= . eSOS.configuration eSOS.RELATION eSOS.configuration ";
    artLabelStrings[ARTL_eSOS_transition_282] = "";
    artlhsL[ARTL_eSOS_transition_282] = ARTL_eSOS_transition;
    artKindOfs[ARTL_eSOS_transition_282] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_transition_286] = "eSOS.transition ::= eSOS.configuration . eSOS.RELATION eSOS.configuration ";
    artLabelStrings[ARTL_eSOS_transition_286] = "";
    artlhsL[ARTL_eSOS_transition_286] = ARTL_eSOS_transition;
    artSlotInstanceOfs[ARTL_eSOS_transition_286] = ARTL_eSOS_configuration;
    artKindOfs[ARTL_eSOS_transition_286] = ARTK_INTERMEDIATE;
    artfiRL[ARTL_eSOS_transition_286] = true;
    artLabelInternalStrings[ARTL_eSOS_transition_288] = "eSOS.transition ::= eSOS.configuration eSOS.RELATION . eSOS.configuration ";
    artLabelStrings[ARTL_eSOS_transition_288] = "";
    artlhsL[ARTL_eSOS_transition_288] = ARTL_eSOS_transition;
    artSlotInstanceOfs[ARTL_eSOS_transition_288] = ARTL_eSOS_RELATION;
    artKindOfs[ARTL_eSOS_transition_288] = ARTK_INTERMEDIATE;
    artLabelInternalStrings[ARTL_eSOS_transition_292] = "eSOS.transition ::= eSOS.configuration eSOS.RELATION eSOS.configuration .";
    artLabelStrings[ARTL_eSOS_transition_292] = "";
    artlhsL[ARTL_eSOS_transition_292] = ARTL_eSOS_transition;
    artSlotInstanceOfs[ARTL_eSOS_transition_292] = ARTL_eSOS_configuration;
    artKindOfs[ARTL_eSOS_transition_292] = ARTK_INTERMEDIATE;
    arteoRL[ARTL_eSOS_transition_292] = true;
    arteoR_pL[ARTL_eSOS_transition_292] = true;
    artPopD[ARTL_eSOS_transition_292] = true;
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

    artLabelStrings[ARTTB_ID_SOS] = "ID_SOS";
    artLabelInternalStrings[ARTTB_ID_SOS] = "&ID_SOS";
    artKindOfs[ARTTB_ID_SOS] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_ID_SOS] = true;
    artLabelStrings[ARTTB_INTEGER] = "INTEGER";
    artLabelInternalStrings[ARTTB_INTEGER] = "&INTEGER";
    artKindOfs[ARTTB_INTEGER] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_INTEGER] = true;
    artLabelStrings[ARTTB_REAL] = "REAL";
    artLabelInternalStrings[ARTTB_REAL] = "&REAL";
    artKindOfs[ARTTB_REAL] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_REAL] = true;
    artLabelStrings[ARTTB_STRING_DQ] = "STRING_DQ";
    artLabelInternalStrings[ARTTB_STRING_DQ] = "&STRING_DQ";
    artKindOfs[ARTTB_STRING_DQ] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_STRING_DQ] = true;
    artLabelStrings[ARTTB_STRING_SQ] = "STRING_SQ";
    artLabelInternalStrings[ARTTB_STRING_SQ] = "&STRING_SQ";
    artKindOfs[ARTTB_STRING_SQ] = ARTK_BUILTIN_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTB_STRING_SQ] = true;
    artLabelStrings[ARTTS__LPAR] = "(";
    artLabelInternalStrings[ARTTS__LPAR] = "'('";
    artKindOfs[ARTTS__LPAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__LPAR] = true;
    artLabelStrings[ARTTS__RPAR] = ")";
    artLabelInternalStrings[ARTTS__RPAR] = "')'";
    artKindOfs[ARTTS__RPAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__RPAR] = true;
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
    artLabelStrings[ARTTS__MINUS_MINUS_MINUS] = "---";
    artLabelInternalStrings[ARTTS__MINUS_MINUS_MINUS] = "'---'";
    artKindOfs[ARTTS__MINUS_MINUS_MINUS] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__MINUS_MINUS_MINUS] = true;
    artLabelStrings[ARTTS__MINUS_GT] = "->";
    artLabelInternalStrings[ARTTS__MINUS_GT] = "'->'";
    artKindOfs[ARTTS__MINUS_GT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__MINUS_GT] = true;
    artLabelStrings[ARTTS__MINUS_GT_STAR] = "->*";
    artLabelInternalStrings[ARTTS__MINUS_GT_STAR] = "'->*'";
    artKindOfs[ARTTS__MINUS_GT_STAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__MINUS_GT_STAR] = true;
    artLabelStrings[ARTTS__MINUS_GT_GT] = "->>";
    artLabelInternalStrings[ARTTS__MINUS_GT_GT] = "'->>'";
    artKindOfs[ARTTS__MINUS_GT_GT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__MINUS_GT_GT] = true;
    artLabelStrings[ARTTS__COLON] = ":";
    artLabelInternalStrings[ARTTS__COLON] = "':'";
    artKindOfs[ARTTS__COLON] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__COLON] = true;
    artLabelStrings[ARTTS__LT] = "<";
    artLabelInternalStrings[ARTTS__LT] = "'<'";
    artKindOfs[ARTTS__LT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__LT] = true;
    artLabelStrings[ARTTS__EQUAL] = "=";
    artLabelInternalStrings[ARTTS__EQUAL] = "'='";
    artKindOfs[ARTTS__EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__EQUAL] = true;
    artLabelStrings[ARTTS__EQUAL_GT] = "=>";
    artLabelInternalStrings[ARTTS__EQUAL_GT] = "'=>'";
    artKindOfs[ARTTS__EQUAL_GT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__EQUAL_GT] = true;
    artLabelStrings[ARTTS__EQUAL_GT_STAR] = "=>*";
    artLabelInternalStrings[ARTTS__EQUAL_GT_STAR] = "'=>*'";
    artKindOfs[ARTTS__EQUAL_GT_STAR] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__EQUAL_GT_STAR] = true;
    artLabelStrings[ARTTS__GT] = ">";
    artLabelInternalStrings[ARTTS__GT] = "'>'";
    artKindOfs[ARTTS__GT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__GT] = true;
    artLabelStrings[ARTTS_false] = "false";
    artLabelInternalStrings[ARTTS_false] = "'false'";
    artKindOfs[ARTTS_false] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_false] = true;
    artLabelStrings[ARTTS_latex] = "latex";
    artLabelInternalStrings[ARTTS_latex] = "'latex'";
    artKindOfs[ARTTS_latex] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_latex] = true;
    artLabelStrings[ARTTS_listIn] = "listIn";
    artLabelInternalStrings[ARTTS_listIn] = "'listIn'";
    artKindOfs[ARTTS_listIn] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_listIn] = true;
    artLabelStrings[ARTTS_listOut] = "listOut";
    artLabelInternalStrings[ARTTS_listOut] = "'listOut'";
    artKindOfs[ARTTS_listOut] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_listOut] = true;
    artLabelStrings[ARTTS_map] = "map";
    artLabelInternalStrings[ARTTS_map] = "'map'";
    artKindOfs[ARTTS_map] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_map] = true;
    artLabelStrings[ARTTS_mapFixed] = "mapFixed";
    artLabelInternalStrings[ARTTS_mapFixed] = "'mapFixed'";
    artKindOfs[ARTTS_mapFixed] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_mapFixed] = true;
    artLabelStrings[ARTTS_priority] = "priority";
    artLabelInternalStrings[ARTTS_priority] = "'priority'";
    artKindOfs[ARTTS_priority] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_priority] = true;
    artLabelStrings[ARTTS_relation] = "relation";
    artLabelInternalStrings[ARTTS_relation] = "'relation'";
    artKindOfs[ARTTS_relation] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_relation] = true;
    artLabelStrings[ARTTS_singleton] = "singleton";
    artLabelInternalStrings[ARTTS_singleton] = "'singleton'";
    artKindOfs[ARTTS_singleton] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_singleton] = true;
    artLabelStrings[ARTTS_test] = "test";
    artLabelInternalStrings[ARTTS_test] = "'test'";
    artKindOfs[ARTTS_test] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_test] = true;
    artLabelStrings[ARTTS_true] = "true";
    artLabelInternalStrings[ARTTS_true] = "'true'";
    artKindOfs[ARTTS_true] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_true] = true;
    artLabelStrings[ARTTS_untyped] = "untyped";
    artLabelInternalStrings[ARTTS_untyped] = "'untyped'";
    artKindOfs[ARTTS_untyped] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS_untyped] = true;
    artLabelStrings[ARTTS__BAR_GT] = "|>";
    artLabelInternalStrings[ARTTS__BAR_GT] = "'|>'";
    artKindOfs[ARTTS__BAR_GT] = ARTK_CASE_SENSITIVE_TERMINAL;
    artTerminalRequiresWhiteSpace[ARTTS__BAR_GT] = true;
    artTableInitialiser_eSOS_BOOLEAN();
    artTableInitialiser_eSOS_ID_SOS();
    artTableInitialiser_eSOS_INTEGER();
    artTableInitialiser_eSOS_REAL();
    artTableInitialiser_eSOS_RELATION();
    artTableInitialiser_eSOS_STRING_DQ();
    artTableInitialiser_eSOS_STRING_SQ();
    artTableInitialiser_eSOS_conditions();
    artTableInitialiser_eSOS_configuration();
    artTableInitialiser_eSOS_element();
    artTableInitialiser_eSOS_entityReferences();
    artTableInitialiser_eSOS_labelOpt();
    artTableInitialiser_eSOS_latexElement();
    artTableInitialiser_eSOS_latexElements();
    artTableInitialiser_eSOS_priorities();
    artTableInitialiser_eSOS_priority();
    artTableInitialiser_eSOS_priorityOpt();
    artTableInitialiser_eSOS_relationElement();
    artTableInitialiser_eSOS_relationElements();
    artTableInitialiser_eSOS_relationSymbol();
    artTableInitialiser_eSOS_rule();
    artTableInitialiser_eSOS_sideCondition();
    artTableInitialiser_eSOS_specification();
    artTableInitialiser_eSOS_subterms();
    artTableInitialiser_eSOS_term();
    artTableInitialiser_eSOS_termElement();
    artTableInitialiser_eSOS_test();
    artTableInitialiser_eSOS_transition();
  }

  public ARTeSOSParser(ARTLexerBase artLexerBase) {
    this(null, artLexerBase);
  }

  public ARTeSOSParser(ARTGrammar artGrammar, ARTLexerBase artLexerBase) {
    super(artGrammar, artLexerBase);
    artFirstTerminalLabel = ARTTS__LPAR;
    artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
    artSetExtent = ARTX_EPSILON + 1;
    ARTL_EOS = ARTX_EOS;
    ARTL_EPSILON = ARTX_EPSILON;
    ARTL_DUMMY = ARTX_DUMMY;
    artGrammarKind = ARTModeGrammarKind.BNF;
    artDefaultStartSymbolLabel = ARTL_eSOS_specification;
    artBuildOptions = "ARTOptionBlock [verbosityLevel=0, statistics=false, trace=false, inputFilenames=[], phaseModule=true, phaseLex=true, phasePreChoose=true, phaseParse=true, phasePostChoose=true, phaseDerivationSelect=true, phaseGIFT=true, phaseAG=true, phaseTR=true, phaseSOS=true, showTWE=false, showBSR=false, showSPPFFull=false, showSPPFCore=false, showDT=false, showGIFT=false, showAG=false, showTR=false, showSOS=false, ebnfClosureLeft=false, ebnfClosureRight=false, ebnfMultiplyOut=false, ebnfLeftFactor=false, ebnfBracketToNonterminal=false, lexWSSuffix=false, lexExtents=false, lexSegments=false, lexRecursive=false, lexPrintTWESet=false, lexDFA=false, lexCFRecognise=false, lexCFParse=true, lexDead=false, lexLongestWithin=false, lexLongestAcross=false, lexPriority=false, postLongestWithin=false, postLongestAcross=false, postPriority=false, outputDirectory=., parserName=ARTeSOSParser, lexerName=ARTeSOSLexer, namespace=uk.ac.rhul.cs.csle.art.esos, despatchMode=fragment, supportMode=HashPool, targetLanguageMode=Java, predictivePops=false, FIFODescriptors=false, suppressPopGuard=false, suppressProductionGuard=false, suppressTestRepeat=false, suppressSemantics=false, clusteredGSS=false, mGLL=false, algorithmMode=gllGeneratorPool, treeLevel=3]";
    artFIFODescriptors = false;
    artSetInitialise();
    artTableInitialise();
  }

  private ARTGLLRDT artRDT;
  private int artNextFreeNodeNumber = 1;
 ARTeSOSSpecification eSOSSpecification = new ARTeSOSSpecification(this); 
          ARTeSOSRule workingRule; ARTeSOSRelation workingRelation; ARTeSOSConfiguration workingConfiguration; 
  public static class ARTAT_eSOS_BOOLEAN extends ARTGLLAttributeBlock {
    public boolean v;
    public String lexeme;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
    ret += " lexeme=" + lexeme;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_ID_SOS extends ARTGLLAttributeBlock {
    public ARTeSOSName v;
    public int rightExtent;
    public int leftExtent;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
    ret += " rightExtent=" + rightExtent;
    ret += " leftExtent=" + leftExtent;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_INTEGER extends ARTGLLAttributeBlock {
    public int v;
    public int rightExtent;
    public int leftExtent;
    public String lexeme;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
    ret += " rightExtent=" + rightExtent;
    ret += " leftExtent=" + leftExtent;
    ret += " lexeme=" + lexeme;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_REAL extends ARTGLLAttributeBlock {
    public int rightExtent;
    public int leftExtent;
    public String lexeme;
    public double v;
    public String toString() {
      String ret = "";
    ret += " rightExtent=" + rightExtent;
    ret += " leftExtent=" + leftExtent;
    ret += " lexeme=" + lexeme;
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_RELATION extends ARTGLLAttributeBlock {
    public int rightExtent;
    public int leftExtent;
    public String v;
    public String toString() {
      String ret = "";
    ret += " rightExtent=" + rightExtent;
    ret += " leftExtent=" + leftExtent;
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_STRING_DQ extends ARTGLLAttributeBlock {
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

  public static class ARTAT_eSOS_STRING_SQ extends ARTGLLAttributeBlock {
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

  public static class ARTAT_eSOS_sideCondition extends ARTGLLAttributeBlock {
    public ARTeSOSSideCondition v;
    ARTGLLRDTHandle term1;
    ARTGLLRDTHandle term2;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_subterms extends ARTGLLAttributeBlock {
    public ARTValueTermUsingList p;
    ARTGLLRDTHandle subterms1;
    ARTGLLRDTHandle term1;
    public String toString() {
      String ret = "";
    ret += " p=" + p;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_term extends ARTGLLAttributeBlock {
    public ARTValueTermUsingList v;
    ARTGLLRDTHandle ID_SOS1;
    ARTGLLRDTHandle subterms1;
    ARTGLLRDTHandle termElement1;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_termElement extends ARTGLLAttributeBlock {
    public ARTValueTermUsingList v;
    ARTGLLRDTHandle BOOLEAN1;
    ARTGLLRDTHandle ID_SOS1;
    ARTGLLRDTHandle INTEGER1;
    ARTGLLRDTHandle REAL1;
    ARTGLLRDTHandle STRING_DQ1;
    ARTGLLRDTHandle STRING_SQ1;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
      return ret + " ";
}
  }

  public static class ARTAT_eSOS_transition extends ARTGLLAttributeBlock {
    public ARTeSOSTransition v;
    ARTGLLRDTHandle RELATION1;
    public String toString() {
      String ret = "";
    ret += " v=" + v;
      return ret + " ";
}
  }

  public void ARTRD_BOOLEAN(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_BOOLEAN BOOLEAN) throws ARTException {
  ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
      /*eSOS.BOOLEAN ::= 'true' .*/
      case ARTL_eSOS_BOOLEAN_516: 
                ARTRD_BOOLEAN(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, BOOLEAN);
        artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
         BOOLEAN.lexeme = "true"; BOOLEAN.v = true; 
        break;
      /*eSOS.BOOLEAN ::= 'false' .*/
      case ARTL_eSOS_BOOLEAN_522: 
                ARTRD_BOOLEAN(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, BOOLEAN);
        artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
         BOOLEAN.lexeme = "false"; BOOLEAN.v = false; 
        break;
        default:
          throw new ARTException("ARTRD_BOOLEAN: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_ID_SOS(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_ID_SOS ID_SOS) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.ID_SOS ::= &ID_SOS .*/
    case ARTL_eSOS_ID_SOS_510: 
            ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ID_SOS.v = new ARTeSOSName(artLexemeAsID(ID_SOS.leftExtent, ID_SOS.rightExtent)); 
      break;
        default:
          throw new ARTException("ARTRD_ID_SOS: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_INTEGER(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_INTEGER INTEGER) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.INTEGER ::= &INTEGER .*/
    case ARTL_eSOS_INTEGER_528: 
            ARTRD_INTEGER(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      INTEGER.lexeme = artLexeme(INTEGER.leftExtent, INTEGER.rightExtent); INTEGER.v = artLexemeAsInteger(INTEGER.leftExtent, INTEGER.rightExtent); 
      break;
        default:
          throw new ARTException("ARTRD_INTEGER: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_REAL(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_REAL REAL) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.REAL ::= &REAL .*/
    case ARTL_eSOS_REAL_534: 
            ARTRD_REAL(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, REAL);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      REAL.lexeme = artLexeme(REAL.leftExtent, REAL.rightExtent); REAL.v = artLexemeAsReal(REAL.leftExtent, REAL.rightExtent); 
      break;
        default:
          throw new ARTException("ARTRD_REAL: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_RELATION(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_RELATION RELATION) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.RELATION ::= eSOS.relationSymbol .*/
    case ARTL_eSOS_RELATION_484: 
            ARTRD_RELATION(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, RELATION);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_relationSymbol(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
      RELATION.v = artLexemeAsID(RELATION.leftExtent, RELATION.rightExtent).trim(); 
      break;
        default:
          throw new ARTException("ARTRD_RELATION: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_STRING_DQ(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_STRING_DQ STRING_DQ) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.STRING_DQ ::= &STRING_DQ .*/
    case ARTL_eSOS_STRING_DQ_540: 
            ARTRD_STRING_DQ(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, STRING_DQ);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      STRING_DQ.lexeme = artLexeme(STRING_DQ.leftExtent, STRING_DQ.rightExtent); STRING_DQ.v = artLexemeAsString(STRING_DQ.leftExtent, STRING_DQ.rightExtent); 
      break;
        default:
          throw new ARTException("ARTRD_STRING_DQ: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_STRING_SQ(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_STRING_SQ STRING_SQ) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.STRING_SQ ::= &STRING_SQ .*/
    case ARTL_eSOS_STRING_SQ_546: 
            ARTRD_STRING_SQ(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, STRING_SQ);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      STRING_SQ.lexeme = artLexeme(STRING_SQ.leftExtent, STRING_SQ.rightExtent); STRING_SQ.v = artLexemeAsString(STRING_SQ.leftExtent, STRING_SQ.rightExtent); 
      break;
        default:
          throw new ARTException("ARTRD_STRING_SQ: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_conditions(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_sideCondition sideCondition1, ARTAT_eSOS_transition transition1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.conditions ::= # .*/
    case ARTL_eSOS_conditions_264: 
      sideCondition1 = new ARTAT_eSOS_sideCondition();
      transition1 = new ARTAT_eSOS_transition();
            ARTRD_conditions(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, sideCondition1, transition1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.conditions ::= eSOS.transition eSOS.conditions .*/
    case ARTL_eSOS_conditions_272: 
      sideCondition1 = new ARTAT_eSOS_sideCondition();
      transition1 = new ARTAT_eSOS_transition();
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), transition1));
      ARTRD_transition(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, transition1, null);
       workingRule.getConditions().add(transition1.v); 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_conditions(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
    /*eSOS.conditions ::= eSOS.sideCondition eSOS.conditions .*/
    case ARTL_eSOS_conditions_280: 
      sideCondition1 = new ARTAT_eSOS_sideCondition();
      transition1 = new ARTAT_eSOS_transition();
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), sideCondition1));
      ARTRD_sideCondition(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, sideCondition1, null, null);
       workingRule.getConditions().add(sideCondition1.v); 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_conditions(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
        default:
          throw new ARTException("ARTRD_conditions: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_configuration(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_term term1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.configuration ::= eSOS.term eSOS.entityReferences .*/
    case ARTL_eSOS_configuration_302: 
      term1 = new ARTAT_eSOS_term();
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), term1));
      ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, term1, null, null, null);
       workingConfiguration.addEntity(eSOSSpecification.termKey, term1.v); 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_entityReferences(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
        default:
          throw new ARTException("ARTRD_configuration: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_element(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_RELATION RELATION1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.element ::= 'latex' eSOS.latexElement . eSOS.latexElements */
    case ARTL_eSOS_element_18: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_latexElement(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null, null);
            break;
    /*eSOS.element ::= 'latex' eSOS.latexElement eSOS.latexElements .*/
    case ARTL_eSOS_element_20: 
      RELATION1 = new ARTAT_eSOS_RELATION();
            ARTRD_element(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, RELATION1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_latexElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.element ::= 'relation' eSOS.RELATION . eSOS.relationElements */
    case ARTL_eSOS_element_26: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            RELATION1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      RELATION1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), RELATION1));
      ARTRD_RELATION(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, RELATION1);
       workingRelation = eSOSSpecification.findRelation(RELATION1.v); 
      break;
    /*eSOS.element ::= 'relation' eSOS.RELATION eSOS.relationElements .*/
    case ARTL_eSOS_element_30: 
      RELATION1 = new ARTAT_eSOS_RELATION();
            ARTRD_element(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, RELATION1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_relationElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.element ::= 'relation' eSOS.relationElements .*/
    case ARTL_eSOS_element_38: 
      RELATION1 = new ARTAT_eSOS_RELATION();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
       workingRelation = eSOSSpecification.findRelation(""); 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_relationElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.element ::= 'priority' eSOS.priority . eSOS.priorities */
    case ARTL_eSOS_element_44: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_priority(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
    /*eSOS.element ::= 'priority' eSOS.priority eSOS.priorities .*/
    case ARTL_eSOS_element_46: 
      RELATION1 = new ARTAT_eSOS_RELATION();
            ARTRD_element(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, RELATION1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_priorities(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.element ::= eSOS.rule .*/
    case ARTL_eSOS_element_50: 
      RELATION1 = new ARTAT_eSOS_RELATION();
            ARTRD_element(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, RELATION1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_rule(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null);
            break;
    /*eSOS.element ::= 'test' eSOS.test .*/
    case ARTL_eSOS_element_56: 
      RELATION1 = new ARTAT_eSOS_RELATION();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_test(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null, null, null);
            break;
        default:
          throw new ARTException("ARTRD_element: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_entityReferences(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_ID_SOS ID_SOS1, ARTAT_eSOS_term term1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.entityReferences ::= # .*/
    case ARTL_eSOS_entityReferences_306: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_entityReferences(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.entityReferences ::= ',' eSOS.term . eSOS.entityReferences */
    case ARTL_eSOS_entityReferences_312: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), term1));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, term1, null, null, null);
       workingConfiguration.addEntity(term1.v); 
      break;
    /*eSOS.entityReferences ::= ',' eSOS.term eSOS.entityReferences .*/
    case ARTL_eSOS_entityReferences_316: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_entityReferences(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, term1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_entityReferences(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
    /*eSOS.entityReferences ::= ',' eSOS.ID_SOS . '=' eSOS.term eSOS.entityReferences */
    case ARTL_eSOS_entityReferences_322: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            break;
    /*eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' . eSOS.term eSOS.entityReferences */
    case ARTL_eSOS_entityReferences_324: 
      ARTRD_entityReferences(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' eSOS.term . eSOS.entityReferences */
    case ARTL_eSOS_entityReferences_326: 
      ARTRD_entityReferences(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, term1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), term1));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, term1, null, null, null);
       workingConfiguration.addEntity(ID_SOS1.v, term1.v); 
      break;
    /*eSOS.entityReferences ::= ',' eSOS.ID_SOS '=' eSOS.term eSOS.entityReferences .*/
    case ARTL_eSOS_entityReferences_330: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_entityReferences(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, term1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_entityReferences(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
        default:
          throw new ARTException("ARTRD_entityReferences: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_labelOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_ID_SOS ID_SOS1, ARTAT_eSOS_STRING_DQ STRING_DQ1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.labelOpt ::= # .*/
    case ARTL_eSOS_labelOpt_242: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
            ARTRD_labelOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, STRING_DQ1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       workingRule.setLabelNumeric(); 
      break;
    /*eSOS.labelOpt ::= '-' eSOS.ID_SOS .*/
    case ARTL_eSOS_labelOpt_250: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
       workingRule.setLabel(ID_SOS1.v.toString()); 
      break;
    /*eSOS.labelOpt ::= '-' eSOS.STRING_DQ .*/
    case ARTL_eSOS_labelOpt_258: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            STRING_DQ1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      STRING_DQ1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), STRING_DQ1));
      ARTRD_STRING_DQ(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, STRING_DQ1);
       workingRule.setLabel(STRING_DQ1.v.toString()); 
      break;
        default:
          throw new ARTException("ARTRD_labelOpt: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_latexElement(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_ID_SOS ID_SOS1, ARTAT_eSOS_RELATION RELATION1, ARTAT_eSOS_STRING_DQ STRING_DQ1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.latexElement ::= eSOS.ID_SOS eSOS.STRING_DQ .*/
    case ARTL_eSOS_latexElement_74: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      RELATION1 = new ARTAT_eSOS_RELATION();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
            ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            STRING_DQ1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      STRING_DQ1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), STRING_DQ1));
      ARTRD_STRING_DQ(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, STRING_DQ1);
       ARTeSOSSpecification.addLaTeXName(ID_SOS1.v.toString(), STRING_DQ1.v); 
      break;
    /*eSOS.latexElement ::= eSOS.RELATION eSOS.STRING_DQ .*/
    case ARTL_eSOS_latexElement_82: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      RELATION1 = new ARTAT_eSOS_RELATION();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
            RELATION1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      RELATION1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), RELATION1));
      ARTRD_RELATION(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, RELATION1);
            STRING_DQ1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      STRING_DQ1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), STRING_DQ1));
      ARTRD_STRING_DQ(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, STRING_DQ1);
       ARTeSOSSpecification.addLaTeXName(RELATION1.v, STRING_DQ1.v); 
      break;
        default:
          throw new ARTException("ARTRD_latexElement: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_latexElements(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.latexElements ::= ',' eSOS.latexElement . eSOS.latexElements */
    case ARTL_eSOS_latexElements_62: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_latexElement(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null, null);
            break;
    /*eSOS.latexElements ::= ',' eSOS.latexElement eSOS.latexElements .*/
    case ARTL_eSOS_latexElements_64: 
            ARTRD_latexElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_latexElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.latexElements ::= # .*/
    case ARTL_eSOS_latexElements_68: 
            ARTRD_latexElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_latexElements: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_priorities(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.priorities ::= ',' eSOS.priority . eSOS.priorities */
    case ARTL_eSOS_priorities_172: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_priority(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
    /*eSOS.priorities ::= ',' eSOS.priority eSOS.priorities .*/
    case ARTL_eSOS_priorities_174: 
            ARTRD_priorities(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_priorities(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.priorities ::= # .*/
    case ARTL_eSOS_priorities_178: 
            ARTRD_priorities(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_priorities: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_priority(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_ID_SOS ID_SOS1, ARTAT_eSOS_ID_SOS ID_SOS2) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.priority ::= eSOS.ID_SOS '>' . eSOS.ID_SOS */
    case ARTL_eSOS_priority_184: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.priority ::= eSOS.ID_SOS '>' eSOS.ID_SOS .*/
    case ARTL_eSOS_priority_186: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
            ARTRD_priority(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2);
      ID_SOS2.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS2.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS2));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS2);
       eSOSSpecification.addPriorityGT(ID_SOS1.v, ID_SOS2.v); 
      break;
    /*eSOS.priority ::= eSOS.ID_SOS '<' . eSOS.ID_SOS */
    case ARTL_eSOS_priority_194: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.priority ::= eSOS.ID_SOS '<' eSOS.ID_SOS .*/
    case ARTL_eSOS_priority_196: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
            ARTRD_priority(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2);
      ID_SOS2.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS2.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS2));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS2);
       eSOSSpecification.addPriorityGT(ID_SOS2.v, ID_SOS1.v); 
      break;
    /*eSOS.priority ::= eSOS.ID_SOS '=' . eSOS.ID_SOS */
    case ARTL_eSOS_priority_204: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.priority ::= eSOS.ID_SOS '=' eSOS.ID_SOS .*/
    case ARTL_eSOS_priority_206: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
            ARTRD_priority(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2);
      ID_SOS2.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS2.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS2));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS2);
       eSOSSpecification.addPriorityEQ(ID_SOS1.v, ID_SOS2.v); 
      break;
        default:
          throw new ARTException("ARTRD_priority: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_priorityOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_ID_SOS ID_SOS1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.priorityOpt ::= # .*/
    case ARTL_eSOS_priorityOpt_228: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
            ARTRD_priorityOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       workingRule.setPriority(new ARTeSOSName("default")); 
      break;
    /*eSOS.priorityOpt ::= '+' eSOS.ID_SOS .*/
    case ARTL_eSOS_priorityOpt_236: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
       workingRule.setPriority(ID_SOS1.v); 
      break;
        default:
          throw new ARTException("ARTRD_priorityOpt: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_relationElement(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_ID_SOS ID_SOS1, ARTAT_eSOS_ID_SOS ID_SOS2, ARTAT_eSOS_term term1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' . 'map' */
    case ARTL_eSOS_relationElement_102: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'map' .*/
    case ARTL_eSOS_relationElement_104: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       workingRelation.addEntity(ID_SOS1.v, new ARTValueTermUsingList(new ARTeSOSEntityMap())); 
      break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' . 'mapFixed' */
    case ARTL_eSOS_relationElement_112: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'mapFixed' .*/
    case ARTL_eSOS_relationElement_114: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       workingRelation.addEntity(ID_SOS1.v, new ARTValueTermUsingList(new ARTeSOSEntityMapFixed())); 
      break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' . 'listIn' */
    case ARTL_eSOS_relationElement_122: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'listIn' .*/
    case ARTL_eSOS_relationElement_124: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       workingRelation.addEntity(ID_SOS1.v, new ARTValueTermUsingList(new ARTeSOSEntityListIn())); 
      break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' . 'listOut' */
    case ARTL_eSOS_relationElement_132: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'listOut' .*/
    case ARTL_eSOS_relationElement_134: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       workingRelation.addEntity(ID_SOS1.v, new ARTValueTermUsingList(new ARTeSOSEntityListOut())); 
      break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' . 'singleton' eSOS.ID_SOS */
    case ARTL_eSOS_relationElement_142: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'singleton' . eSOS.ID_SOS */
    case ARTL_eSOS_relationElement_144: 
      ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'singleton' eSOS.ID_SOS .*/
    case ARTL_eSOS_relationElement_146: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      ID_SOS2.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS2.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS2));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS2);
       workingRelation.addEntity(ID_SOS1.v, new ARTValueTermUsingList(new ARTeSOSEntitySingleton(new ARTValueTermUsingList(ID_SOS1.v)))); 
      break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' . 'untyped' eSOS.ID_SOS */
    case ARTL_eSOS_relationElement_154: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'untyped' . eSOS.ID_SOS */
    case ARTL_eSOS_relationElement_156: 
      ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationElement ::= eSOS.ID_SOS ':' 'untyped' eSOS.ID_SOS .*/
    case ARTL_eSOS_relationElement_158: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      ID_SOS2.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS2.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS2));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS2);
       workingRelation.addEntity(ID_SOS1.v, new ARTValueTermUsingList(new ARTeSOSEntityUntyped(new ARTValueTermUsingList(ID_SOS1.v)))); 
      break;
    /*eSOS.relationElement ::= eSOS.term .*/
    case ARTL_eSOS_relationElement_164: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      ID_SOS2 = new ARTAT_eSOS_ID_SOS();
      term1 = new ARTAT_eSOS_term();
            ARTRD_relationElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID_SOS1, ID_SOS2, term1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), term1));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, term1, null, null, null);
       workingRelation.addThetaTerminal(term1.v); 
      break;
        default:
          throw new ARTException("ARTRD_relationElement: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_relationElements(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.relationElements ::= ',' eSOS.relationElement . eSOS.relationElements */
    case ARTL_eSOS_relationElements_90: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_relationElement(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null, null);
            break;
    /*eSOS.relationElements ::= ',' eSOS.relationElement eSOS.relationElements .*/
    case ARTL_eSOS_relationElements_92: 
            ARTRD_relationElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_relationElements(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.relationElements ::= # .*/
    case ARTL_eSOS_relationElements_96: 
            ARTRD_relationElements(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_relationElements: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_relationSymbol(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.relationSymbol ::= '->' .*/
    case ARTL_eSOS_relationSymbol_490: 
            ARTRD_relationSymbol(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationSymbol ::= '->*' .*/
    case ARTL_eSOS_relationSymbol_494: 
            ARTRD_relationSymbol(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationSymbol ::= '=>' .*/
    case ARTL_eSOS_relationSymbol_498: 
            ARTRD_relationSymbol(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationSymbol ::= '=>*' .*/
    case ARTL_eSOS_relationSymbol_502: 
            ARTRD_relationSymbol(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.relationSymbol ::= '->>' .*/
    case ARTL_eSOS_relationSymbol_506: 
            ARTRD_relationSymbol(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_relationSymbol: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_rule(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_transition transition1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt . eSOS.conditions '---' eSOS.transition */
    case ARTL_eSOS_rule_216: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_labelOpt(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_priorityOpt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null);
            break;
    /*eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt eSOS.conditions . '---' eSOS.transition */
    case ARTL_eSOS_rule_218: 
      ARTRD_rule(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, transition1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_conditions(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null);
            break;
    /*eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt eSOS.conditions '---' . eSOS.transition */
    case ARTL_eSOS_rule_220: 
      ARTRD_rule(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, transition1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.rule ::= eSOS.labelOpt eSOS.priorityOpt eSOS.conditions '---' eSOS.transition .*/
    case ARTL_eSOS_rule_222: 
      transition1 = new ARTAT_eSOS_transition();
       workingRule = new ARTeSOSRule(); 
      ARTRD_rule(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, transition1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), transition1));
      ARTRD_transition(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, transition1, null);
       workingRule.setConclusion(transition1.v); eSOSSpecification.addRule(workingRule); 
      break;
        default:
          throw new ARTException("ARTRD_rule: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_sideCondition(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_sideCondition sideCondition, ARTAT_eSOS_term term1, ARTAT_eSOS_term term2) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.sideCondition ::= eSOS.term '|>' . eSOS.term */
    case ARTL_eSOS_sideCondition_336: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), term1));
      ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, term1, null, null, null);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.sideCondition ::= eSOS.term '|>' eSOS.term .*/
    case ARTL_eSOS_sideCondition_338: 
      term1 = new ARTAT_eSOS_term();
      term2 = new ARTAT_eSOS_term();
            ARTRD_sideCondition(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, sideCondition, term1, term2);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), term2));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, term2, null, null, null);
       sideCondition.v = new ARTeSOSSideCondition(term1.v, term2.v); 
      break;
        default:
          throw new ARTException("ARTRD_sideCondition: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_specification(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.specification ::= eSOS.element eSOS.specification .*/
    case ARTL_eSOS_specification_6: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_element(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, null);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_specification(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*eSOS.specification ::= eSOS.element .*/
    case ARTL_eSOS_specification_10: 
            ARTRD_specification(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_element(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null);
       eSOSSpecification.induceRules(); 
      break;
        default:
          throw new ARTException("ARTRD_specification: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_subterms(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_subterms subterms, ARTAT_eSOS_subterms subterms1, ARTAT_eSOS_term term1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.subterms ::= # .*/
    case ARTL_eSOS_subterms_362: 
      subterms1 = new ARTAT_eSOS_subterms();
      term1 = new ARTAT_eSOS_term();
            ARTRD_subterms(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, subterms, subterms1, term1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.subterms ::= eSOS.term .*/
    case ARTL_eSOS_subterms_366: 
      subterms1 = new ARTAT_eSOS_subterms();
      term1 = new ARTAT_eSOS_term();
            ARTRD_subterms(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, subterms, subterms1, term1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), term1));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, term1, null, null, null);
       subterms.p.addChild(term1.v); 
      break;
    /*eSOS.subterms ::= eSOS.term ',' . eSOS.subterms */
    case ARTL_eSOS_subterms_376: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), term1));
      ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, term1, null, null, null);
       subterms.p.addChild(term1.v); 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      subterms1.p = subterms.p; 
      break;
    /*eSOS.subterms ::= eSOS.term ',' eSOS.subterms .*/
    case ARTL_eSOS_subterms_380: 
      subterms1 = new ARTAT_eSOS_subterms();
      term1 = new ARTAT_eSOS_term();
            ARTRD_subterms(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, subterms, subterms1, term1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), subterms1));
      ARTRD_subterms(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, subterms1, null, null);
            break;
        default:
          throw new ARTException("ARTRD_subterms: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_term(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_term term, ARTAT_eSOS_ID_SOS ID_SOS1, ARTAT_eSOS_subterms subterms1, ARTAT_eSOS_termElement termElement1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.term ::= eSOS.termElement .*/
    case ARTL_eSOS_term_344: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      subterms1 = new ARTAT_eSOS_subterms();
      termElement1 = new ARTAT_eSOS_termElement();
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, term, ID_SOS1, subterms1, termElement1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), termElement1));
      ARTRD_termElement(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, termElement1, null, null, null, null, null, null);
       term.v = termElement1.v; 
      break;
    /*eSOS.term ::= eSOS.ID_SOS '(' . eSOS.subterms ')' */
    case ARTL_eSOS_term_354: 
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
       term.v = new ARTValueTermUsingList(ID_SOS1.v); subterms1.p = term.v; 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.term ::= eSOS.ID_SOS '(' eSOS.subterms . ')' */
    case ARTL_eSOS_term_356: 
      ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, term, ID_SOS1, subterms1, termElement1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), subterms1));
      ARTRD_subterms(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, subterms1, null, null);
            break;
    /*eSOS.term ::= eSOS.ID_SOS '(' eSOS.subterms ')' .*/
    case ARTL_eSOS_term_358: 
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      subterms1 = new ARTAT_eSOS_subterms();
      termElement1 = new ARTAT_eSOS_termElement();
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, term, ID_SOS1, subterms1, termElement1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default:
          throw new ARTException("ARTRD_term: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_termElement(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_termElement termElement, ARTAT_eSOS_BOOLEAN BOOLEAN1, ARTAT_eSOS_ID_SOS ID_SOS1, ARTAT_eSOS_INTEGER INTEGER1, ARTAT_eSOS_REAL REAL1, ARTAT_eSOS_STRING_DQ STRING_DQ1, ARTAT_eSOS_STRING_SQ STRING_SQ1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.termElement ::= eSOS.BOOLEAN .*/
    case ARTL_eSOS_termElement_384: 
      BOOLEAN1 = new ARTAT_eSOS_BOOLEAN();
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      REAL1 = new ARTAT_eSOS_REAL();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      STRING_SQ1 = new ARTAT_eSOS_STRING_SQ();
            ARTRD_termElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, termElement, BOOLEAN1, ID_SOS1, INTEGER1, REAL1, STRING_DQ1, STRING_SQ1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), BOOLEAN1));
      ARTRD_BOOLEAN(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, BOOLEAN1);
       termElement.v = new ARTValueTermUsingList(new ARTValueBoolean(BOOLEAN1.v)); 
      break;
    /*eSOS.termElement ::= eSOS.INTEGER .*/
    case ARTL_eSOS_termElement_390: 
      BOOLEAN1 = new ARTAT_eSOS_BOOLEAN();
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      REAL1 = new ARTAT_eSOS_REAL();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      STRING_SQ1 = new ARTAT_eSOS_STRING_SQ();
            ARTRD_termElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, termElement, BOOLEAN1, ID_SOS1, INTEGER1, REAL1, STRING_DQ1, STRING_SQ1);
      INTEGER1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      INTEGER1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), INTEGER1));
      ARTRD_INTEGER(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, INTEGER1);
       termElement.v = new ARTValueTermUsingList(new ARTValueInteger32(INTEGER1.v)); 
      break;
    /*eSOS.termElement ::= eSOS.REAL .*/
    case ARTL_eSOS_termElement_396: 
      BOOLEAN1 = new ARTAT_eSOS_BOOLEAN();
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      REAL1 = new ARTAT_eSOS_REAL();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      STRING_SQ1 = new ARTAT_eSOS_STRING_SQ();
            ARTRD_termElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, termElement, BOOLEAN1, ID_SOS1, INTEGER1, REAL1, STRING_DQ1, STRING_SQ1);
      REAL1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      REAL1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), REAL1));
      ARTRD_REAL(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, REAL1);
       termElement.v = new ARTValueTermUsingList(new ARTValueReal64(REAL1.v)); 
      break;
    /*eSOS.termElement ::= eSOS.STRING_DQ .*/
    case ARTL_eSOS_termElement_402: 
      BOOLEAN1 = new ARTAT_eSOS_BOOLEAN();
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      REAL1 = new ARTAT_eSOS_REAL();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      STRING_SQ1 = new ARTAT_eSOS_STRING_SQ();
            ARTRD_termElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, termElement, BOOLEAN1, ID_SOS1, INTEGER1, REAL1, STRING_DQ1, STRING_SQ1);
      STRING_DQ1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      STRING_DQ1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), STRING_DQ1));
      ARTRD_STRING_DQ(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, STRING_DQ1);
       termElement.v = new ARTValueTermUsingList(new ARTValueString(STRING_DQ1.v)); 
      break;
    /*eSOS.termElement ::= eSOS.STRING_SQ .*/
    case ARTL_eSOS_termElement_408: 
      BOOLEAN1 = new ARTAT_eSOS_BOOLEAN();
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      REAL1 = new ARTAT_eSOS_REAL();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      STRING_SQ1 = new ARTAT_eSOS_STRING_SQ();
            ARTRD_termElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, termElement, BOOLEAN1, ID_SOS1, INTEGER1, REAL1, STRING_DQ1, STRING_SQ1);
      STRING_SQ1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      STRING_SQ1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), STRING_SQ1));
      ARTRD_STRING_SQ(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, STRING_SQ1);
       termElement.v = new ARTValueTermUsingList(new ARTValueCharacter(STRING_SQ1.v)); 
      break;
    /*eSOS.termElement ::= eSOS.ID_SOS .*/
    case ARTL_eSOS_termElement_414: 
      BOOLEAN1 = new ARTAT_eSOS_BOOLEAN();
      ID_SOS1 = new ARTAT_eSOS_ID_SOS();
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      REAL1 = new ARTAT_eSOS_REAL();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      STRING_SQ1 = new ARTAT_eSOS_STRING_SQ();
            ARTRD_termElement(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, termElement, BOOLEAN1, ID_SOS1, INTEGER1, REAL1, STRING_DQ1, STRING_SQ1);
      ID_SOS1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      ID_SOS1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), ID_SOS1));
      ARTRD_ID_SOS(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, ID_SOS1);
      termElement.v = new ARTValueTermUsingList(ID_SOS1.v); 
      break;
        default:
          throw new ARTException("ARTRD_termElement: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_test(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_INTEGER INTEGER1, ARTAT_eSOS_RELATION RELATION1, ARTAT_eSOS_STRING_DQ STRING_DQ1, ARTAT_eSOS_subterms subterms1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.test ::= eSOS.RELATION ',' . eSOS.subterms */
    case ARTL_eSOS_test_422: 
      RELATION1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      RELATION1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), RELATION1));
      ARTRD_RELATION(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, RELATION1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       subterms1.p = new ARTValueTermUsingList(new ARTValueInteger32(0)); 
      break;
    /*eSOS.test ::= eSOS.RELATION ',' eSOS.subterms .*/
    case ARTL_eSOS_test_426: 
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      RELATION1 = new ARTAT_eSOS_RELATION();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      subterms1 = new ARTAT_eSOS_subterms();
            ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), subterms1));
      ARTRD_subterms(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, subterms1, null, null);
       eSOSSpecification.addTest("", 1, eSOSSpecification.findRelation(RELATION1.v), subterms1.p); 
      break;
    /*eSOS.test ::= eSOS.STRING_DQ ',' . eSOS.RELATION ',' eSOS.subterms */
    case ARTL_eSOS_test_434: 
      STRING_DQ1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      STRING_DQ1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), STRING_DQ1));
      ARTRD_STRING_DQ(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, STRING_DQ1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION . ',' eSOS.subterms */
    case ARTL_eSOS_test_436: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      RELATION1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      RELATION1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), RELATION1));
      ARTRD_RELATION(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, RELATION1);
            break;
    /*eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION ',' . eSOS.subterms */
    case ARTL_eSOS_test_438: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       subterms1.p = new ARTValueTermUsingList(new ARTValueInteger32(0)); 
      break;
    /*eSOS.test ::= eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms .*/
    case ARTL_eSOS_test_442: 
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      RELATION1 = new ARTAT_eSOS_RELATION();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      subterms1 = new ARTAT_eSOS_subterms();
            ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), subterms1));
      ARTRD_subterms(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, subterms1, null, null);
       eSOSSpecification.addTest(STRING_DQ1.v, 1, eSOSSpecification.findRelation(RELATION1.v), subterms1.p); 
      break;
    /*eSOS.test ::= eSOS.INTEGER ',' . eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms */
    case ARTL_eSOS_test_450: 
      INTEGER1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      INTEGER1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), INTEGER1));
      ARTRD_INTEGER(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, INTEGER1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ . ',' eSOS.RELATION ',' eSOS.subterms */
    case ARTL_eSOS_test_452: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      STRING_DQ1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      STRING_DQ1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), STRING_DQ1));
      ARTRD_STRING_DQ(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, STRING_DQ1);
            break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' . eSOS.RELATION ',' eSOS.subterms */
    case ARTL_eSOS_test_454: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION . ',' eSOS.subterms */
    case ARTL_eSOS_test_456: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      RELATION1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      RELATION1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), RELATION1));
      ARTRD_RELATION(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, RELATION1);
            break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' . eSOS.subterms */
    case ARTL_eSOS_test_458: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       subterms1.p = new ARTValueTermUsingList(new ARTValueInteger32(0)); 
      break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.STRING_DQ ',' eSOS.RELATION ',' eSOS.subterms .*/
    case ARTL_eSOS_test_462: 
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      RELATION1 = new ARTAT_eSOS_RELATION();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      subterms1 = new ARTAT_eSOS_subterms();
            ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), subterms1));
      ARTRD_subterms(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, subterms1, null, null);
       eSOSSpecification.addTest(STRING_DQ1.v, INTEGER1.v, eSOSSpecification.findRelation(RELATION1.v), subterms1.p); 
      break;
    /*eSOS.test ::= eSOS.INTEGER ',' . eSOS.RELATION ',' eSOS.subterms */
    case ARTL_eSOS_test_470: 
      INTEGER1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      INTEGER1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), INTEGER1));
      ARTRD_INTEGER(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, INTEGER1);
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION . ',' eSOS.subterms */
    case ARTL_eSOS_test_472: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      RELATION1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      RELATION1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), RELATION1));
      ARTRD_RELATION(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, RELATION1);
            break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION ',' . eSOS.subterms */
    case ARTL_eSOS_test_474: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
       subterms1.p = new ARTValueTermUsingList(new ARTValueInteger32(0)); 
      break;
    /*eSOS.test ::= eSOS.INTEGER ',' eSOS.RELATION ',' eSOS.subterms .*/
    case ARTL_eSOS_test_478: 
      INTEGER1 = new ARTAT_eSOS_INTEGER();
      RELATION1 = new ARTAT_eSOS_RELATION();
      STRING_DQ1 = new ARTAT_eSOS_STRING_DQ();
      subterms1 = new ARTAT_eSOS_subterms();
            ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, INTEGER1, RELATION1, STRING_DQ1, subterms1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), subterms1));
      ARTRD_subterms(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, subterms1, null, null);
       eSOSSpecification.addTest("", INTEGER1.v, eSOSSpecification.findRelation(RELATION1.v), subterms1.p); 
      break;
        default:
          throw new ARTException("ARTRD_test: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void ARTRD_transition(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_eSOS_transition transition, ARTAT_eSOS_RELATION RELATION1) throws ARTException {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*eSOS.transition ::= eSOS.configuration eSOS.RELATION . eSOS.configuration */
    case ARTL_eSOS_transition_288: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_configuration(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, null);
            RELATION1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      RELATION1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), RELATION1));
      ARTRD_RELATION(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, RELATION1);
       workingConfiguration = transition.v.getRhs(); 
      break;
    /*eSOS.transition ::= eSOS.configuration eSOS.RELATION eSOS.configuration .*/
    case ARTL_eSOS_transition_292: 
      RELATION1 = new ARTAT_eSOS_RELATION();
       transition.v = new ARTeSOSTransition();
    workingConfiguration = transition.v.getLhs(); 
      ARTRD_transition(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, transition, RELATION1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_configuration(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null);
       transition.v.setRelation(eSOSSpecification.findRelation(RELATION1.v)); 
      break;
        default:
          throw new ARTException("ARTRD_transition: unhandled pack node " + artPackedNode + " with label " + artSPPFPackedNodeLabel(artPackedNode) + " - " + artLabelInternalStrings[artSPPFPackedNodeLabel(artPackedNode)]);
        }
      }
    }
}

public void artEvaluate(ARTGLLRDTHandle artElement, Object artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable) throws ARTException {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_eSOS_BOOLEAN:  ARTRD_BOOLEAN(artElement.element, artParent, artWriteable, (ARTAT_eSOS_BOOLEAN) artAttributes); break;
    case ARTL_eSOS_ID_SOS:  ARTRD_ID_SOS(artElement.element, artParent, artWriteable, (ARTAT_eSOS_ID_SOS) artAttributes); break;
    case ARTL_eSOS_INTEGER:  ARTRD_INTEGER(artElement.element, artParent, artWriteable, (ARTAT_eSOS_INTEGER) artAttributes); break;
    case ARTL_eSOS_REAL:  ARTRD_REAL(artElement.element, artParent, artWriteable, (ARTAT_eSOS_REAL) artAttributes); break;
    case ARTL_eSOS_RELATION:  ARTRD_RELATION(artElement.element, artParent, artWriteable, (ARTAT_eSOS_RELATION) artAttributes); break;
    case ARTL_eSOS_STRING_DQ:  ARTRD_STRING_DQ(artElement.element, artParent, artWriteable, (ARTAT_eSOS_STRING_DQ) artAttributes); break;
    case ARTL_eSOS_STRING_SQ:  ARTRD_STRING_SQ(artElement.element, artParent, artWriteable, (ARTAT_eSOS_STRING_SQ) artAttributes); break;
    case ARTL_eSOS_conditions: ARTRD_conditions(artElement.element, artParent, artWriteable, null, null); break;
    case ARTL_eSOS_configuration: ARTRD_configuration(artElement.element, artParent, artWriteable, null); break;
    case ARTL_eSOS_element: ARTRD_element(artElement.element, artParent, artWriteable, null); break;
    case ARTL_eSOS_entityReferences: ARTRD_entityReferences(artElement.element, artParent, artWriteable, null, null); break;
    case ARTL_eSOS_labelOpt: ARTRD_labelOpt(artElement.element, artParent, artWriteable, null, null); break;
    case ARTL_eSOS_latexElement: ARTRD_latexElement(artElement.element, artParent, artWriteable, null, null, null); break;
    case ARTL_eSOS_latexElements: ARTRD_latexElements(artElement.element, artParent, artWriteable); break;
    case ARTL_eSOS_priorities: ARTRD_priorities(artElement.element, artParent, artWriteable); break;
    case ARTL_eSOS_priority: ARTRD_priority(artElement.element, artParent, artWriteable, null, null); break;
    case ARTL_eSOS_priorityOpt: ARTRD_priorityOpt(artElement.element, artParent, artWriteable, null); break;
    case ARTL_eSOS_relationElement: ARTRD_relationElement(artElement.element, artParent, artWriteable, null, null, null); break;
    case ARTL_eSOS_relationElements: ARTRD_relationElements(artElement.element, artParent, artWriteable); break;
    case ARTL_eSOS_relationSymbol: ARTRD_relationSymbol(artElement.element, artParent, artWriteable); break;
    case ARTL_eSOS_rule: ARTRD_rule(artElement.element, artParent, artWriteable, null); break;
    case ARTL_eSOS_sideCondition:  ARTRD_sideCondition(artElement.element, artParent, artWriteable, (ARTAT_eSOS_sideCondition) artAttributes, null, null); break;
    case ARTL_eSOS_specification: ARTRD_specification(artElement.element, artParent, artWriteable); break;
    case ARTL_eSOS_subterms:  ARTRD_subterms(artElement.element, artParent, artWriteable, (ARTAT_eSOS_subterms) artAttributes, null, null); break;
    case ARTL_eSOS_term:  ARTRD_term(artElement.element, artParent, artWriteable, (ARTAT_eSOS_term) artAttributes, null, null, null); break;
    case ARTL_eSOS_termElement:  ARTRD_termElement(artElement.element, artParent, artWriteable, (ARTAT_eSOS_termElement) artAttributes, null, null, null, null, null, null); break;
    case ARTL_eSOS_test: ARTRD_test(artElement.element, artParent, artWriteable, null, null, null, null); break;
    case ARTL_eSOS_transition:  ARTRD_transition(artElement.element, artParent, artWriteable, (ARTAT_eSOS_transition) artAttributes, null); break;
  }
}

public ARTGLLRDT artEvaluator() throws ARTException {
  artRDT = new ARTGLLRDT("RDT", artKindOfs, artLabelStrings, artAnnotations, artLexer.artInputString);
  ARTGLLRDTVertex artNewParent = new ARTGLLRDTVertex(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artRootSPPFNode),artSPPFNodeRightExtent(artRootSPPFNode),artSPPFNodeLabel(artRootSPPFNode), null));
  artRDT.setRoot(artNewParent);
  boolean artNewWriteable = true;
  artEvaluate(new ARTGLLRDTHandle(artRootSPPFNode), null, artNewParent, artNewWriteable);
  artAttributeEvaluateCompleteTime = artReadClock();
  return artRDT;
}
};
