package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

public enum CFGKind {
  SOS, // Start of String ($$)
  EOS, // End of String ($)
  EPSILON, // Epsilon
  TRM_CS, // Termial case sensitive
  TRM_CI, // Terminal case insenitive
  TRM_BI, // Terminal built in
  TRM_CH, // Terminal character
  TRM_CH_SET, // Terminal character set: match anything in the contents
  TRM_CH_ANTI_SET, // Terminal character anti set: match anything but the contents
  NONTERMINAL, // Nonterminal
  ALT, // Alternate
  END, // End of rule
  PAR, // Parenthesised sub-expression (FBNF)
  OPT, // Optional sub-expression (EBNF)
  POS, // Positive closure over sub-expression (EBNF)
  KLN // Kleeene closure over sub-expression (EBNF)
}
