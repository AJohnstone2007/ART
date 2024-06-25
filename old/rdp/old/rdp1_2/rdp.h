/****************************************************************************
*
* RDP release 1.20 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 6 Nov 1994
*
* rdp.h - hand written rdp front end
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef RDP_H
#define RDP_H

#include "scan.h"
enum                            /* EBNF tokens */
{
  T_LBRACE = P_TOP, T_RBRACE, T_LPAR, T_RPAR, T_LBRACK, T_RBRACK,
  T_LANGLE, T_RANGLE, T_ALT, T_IS, T_PERIOD, T_COMMA, T_COLON, T_DCOLON,
  T_STAR, T_STRING, T_STRING_ESC, T_COMMENT_LINE_VISIBLE, T_COMMENT_LINE, T_COMMENT, T_COMMENT_NEST,
  T_COMMENT_VISIBLE, T_COMMENT_NEST_VISIBLE,
  T_SQUOTE, T_OSTRING, T_DQUOTE, T_OCOMMENT,
  /* directives */
  T_OPTION, T_TITLE, T_SUFFIX, T_CASE_INSENSITIVE, T_SHOW_SKIPS,
  T_SET_SIZE, T_OUTPUT_FILE, T_INCLUDE, T_PASSES,
  T_HASH_SIZE, T_HASH_PRIME, T_TEXT_SIZE, T_USES, T_MAX_ERRORS, T_MAX_WARNINGS,
  T_TAB_WIDTH, T_TEXT_MODE, T_PRE_PROCESS, T_POST_PROCESS,
  T_TOP
};

#endif

/* End of rdp.h */
