package uk.ac.rhul.cs.csle.art.cfg.cfgRules;

public enum CFGRulesKind {
  SCRIPT, USER, LEXER, PARSER;

  @Override
  public String toString() {
    switch (this) {
    case SCRIPT:
      return "Script";
    case USER:
      return "User";
    case LEXER:
      return "Lexer";
    case PARSER:
      return "Parser";

    default:
      return null;
    }
  }
}
