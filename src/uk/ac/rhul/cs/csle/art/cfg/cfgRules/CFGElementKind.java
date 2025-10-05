package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

/*
 *  !!! Alert - if you modify (add delete or reorder elements) this enumeration you must:
 *
 *  1. Run the mainline CFGRules.main() to generate a matching set of integer constants
 *
 *  2. Cut and paste the output into all parser sources that use the table driven CFGRuks
 *
 *  Current list of affected parsers: GLLHashPool MGLLHashPool
 *
 */

public enum CFGElementKind {
  SOS, // Start of String ($$) 0
  EOS, // End of String ($) 1
  EPSILON, // Epsilon 2
  TRM_CS, // Terminal case sensitive 3
  TRM_CI, // Terminal case insensitive 4
  TRM_BI, // Terminal built in 5
  TRM_CH, // Terminal character 6
  TRM_CH_UIB, // Terminal unkown lexeme but in band character 7
  TRM_CH_UOB, // Terminal unknown lexeme and out of band character 8
  TRM_CH_SET, // Terminal character set: match anything in the contents 9
  TRM_CH_ANTI_SET, // Terminal character anti set: match anything but the contents 10
  NONTERMINAL, // Nonterminal 11
  ALT, // Alternate 12
  END, // End of rule 13
  PAR, // Parenthesised sub-expression (FBNF) 14
  OPT, // Optional sub-expression (EBNF) 15
  POS, // Positive closure over sub-expression (EBNF) 16
  KLN // Kleeene closure over sub-expression (EBNF) 17
}
