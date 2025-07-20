package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

public enum CFGKind {
  SOS, // Start of String ($$)
  EOS, // End of String ($)
  EPS, // Epsilon
  TRM_CS, // Termial case sensitive
  TRM_CI, // Terminal case insenitive
  TRM_BI, // Terminal built in
  TRM_CHR, // Terminal character
  TRM_CHAR_SET, // Terminal character set
  NONTRM, // Nonterminal
  ALT, // Alternate
  END, // End of rule
  DO_FIRST, // Parenthesised sub-expression (FBNF)
  OPT, // Optional sub-expression (EBNF)
  POS, // Positive closure over sub-expression (EBNF)
  KLN // Kleeene closure over sub-expression (EBNF)
}
