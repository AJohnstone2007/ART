/****************************************************************************
*
* Header file generated by RDP on Dec 29 1994 at 17:39:44 from rdp.bnf
*
****************************************************************************/
#include "scan.h"

/* Token enumeration */
enum
{
TT_DUMMY = P_TOP, T_11 /* " */, T_12 /* ' */, T_13 /* ( */, 
T_14 /* (* */, T_15 /* ) */, T_16 /* * */, T_17 /* . */, T_18 /* : */, 
T_19 /* :: */, T_20 /* ::= */, T_21 /* < */, T_22 /* > */, T_23 /* @ */, 
T_CASE_INSENSITIVE, T_COMMENT, T_COMMENT_LINE, T_COMMENT_LINE_VISIBLE, 
T_COMMENT_NEST, T_COMMENT_NEST_VISIBLE, T_COMMENT_VISIBLE, T_HASH_PRIME, 
T_HASH_SIZE, T_INCLUDE, T_INTERPRETER, T_MAX_ERRORS, T_MAX_WARNINGS, 
T_OPTION, T_OUTPUT_FILE, T_PASSES, T_POST_PARSE, T_PRE_PARSE, 
T_SET_SIZE, T_SHOW_SKIPS, T_STRING, T_STRING_ESC, T_SUFFIX, 
T_TAB_WIDTH, T_TEXT_SIZE, T_TITLE, T_USES, T_51 /* [ */, T_52 /* [* */, 
T_53 /* ] */, T_54 /* { */, T_55 /* | */, T_56 /* } */,
TT_TOP
};

/* Parser start production */
void unit(void);

/* End of rdp.h */
