/*******************************************************************************
*
* Header file generated by RDP on Dec 19 2015 07:01:02 from cpp_lex.bnf
*
*******************************************************************************/
#ifndef CPP_LEX_H
#define CPP_LEX_H

#include "arg.h"
#include "graph.h"
#include "hist.h"
#include "memalloc.h"
#include "scan.h"
#include "set.h"
#include "symbol.h"
#include "textio.h"


/* Maximum number of passes */
#define RDP_PASSES 1

/* Time and date stamp */
#define RDP_STAMP "Generated on Dec 19 2015 07:01:02 and compiled on " __DATE__ " at " __TIME__ 

/* Token enumeration */
enum
{
RDP_TT_BOTTOM = SCAN_P_TOP,
RDP_T_33 /* ! */ = SCAN_P_TOP,RDP_T_3361 /* != */,RDP_T_34 /* " */,RDP_T_37 /* % */,RDP_T_3761 /* %= */,RDP_T_38 /* & */,RDP_T_3838 /* && */,RDP_T_3861 /* &= */,
RDP_T_39 /* ' */,RDP_T_40 /* ( */,RDP_T_41 /* ) */,RDP_T_42 /* * */,RDP_T_4261 /* *= */,RDP_T_43 /* + */,RDP_T_4343 /* ++ */,RDP_T_4361 /* += */,
RDP_T_44 /* , */,RDP_T_45 /* - */,RDP_T_4545 /* -- */,RDP_T_4561 /* -= */,RDP_T_4562 /* -> */,RDP_T_46 /* . */,RDP_T_4646 /* .. */,RDP_T_464646 /* ... */,
RDP_T_47 /* / */,RDP_T_4742 /* / * */,RDP_T_4747 /* // */,RDP_T_4761 /* /= */,RDP_T_58 /* : */,RDP_T_5858 /* :: */,RDP_T_59 /* ; */,RDP_T_60 /* < */,
RDP_T_6060 /* << */,RDP_T_606061 /* <<= */,RDP_T_6061 /* <= */,RDP_T_61 /* = */,RDP_T_6161 /* == */,RDP_T_62 /* > */,RDP_T_6261 /* >= */,RDP_T_6262 /* >> */,
RDP_T_626261 /* >>= */,RDP_T_63 /* ? */,RDP_T_91 /* [ */,RDP_T_93 /* ] */,RDP_T_94 /* ^ */,RDP_T_9461 /* ^= */,RDP_T_alignas,RDP_T_alignof,
RDP_T_and,RDP_T_and_eq,RDP_T_asm,RDP_T_auto,RDP_T_bitand,RDP_T_bitor,RDP_T_bool,RDP_T_break,
RDP_T_case,RDP_T_catch,RDP_T_char,RDP_T_char16_t,RDP_T_char32_t,RDP_T_class,RDP_T_compl,RDP_T_concept,
RDP_T_const,RDP_T_const_cast,RDP_T_constexpr,RDP_T_continue,RDP_T_decltype,RDP_T_default,RDP_T_delete,RDP_T_do,
RDP_T_double,RDP_T_dynamic_cast,RDP_T_else,RDP_T_enum,RDP_T_explicit,RDP_T_export,RDP_T_extern,RDP_T_false,
RDP_T_float,RDP_T_for,RDP_T_friend,RDP_T_goto,RDP_T_if,RDP_T_inline,RDP_T_int,RDP_T_long,
RDP_T_mutable,RDP_T_namespace,RDP_T_new,RDP_T_noexcept,RDP_T_not,RDP_T_not_eq,RDP_T_nullptr,RDP_T_operator,
RDP_T_or,RDP_T_or_eq,RDP_T_private,RDP_T_protected,RDP_T_public,RDP_T_register,RDP_T_reinterpret_cast,RDP_T_requires,
RDP_T_return,RDP_T_short,RDP_T_signed,RDP_T_sizeof,RDP_T_static,RDP_T_static_assert,RDP_T_static_cast,RDP_T_struct,
RDP_T_switch,RDP_T_template,RDP_T_this,RDP_T_thread_local,RDP_T_throw,RDP_T_true,RDP_T_try,RDP_T_typedef,
RDP_T_typeid,RDP_T_typename,RDP_T_union,RDP_T_unsigned,RDP_T_using,RDP_T_virtual,RDP_T_void,RDP_T_volatile,
RDP_T_wchar_t,RDP_T_while,RDP_T_xor,RDP_T_xor_eq,RDP_T_123 /* { */,RDP_T_124 /* | */,RDP_T_12461 /* |= */,RDP_T_124124 /* || */,
RDP_T_125 /* } */,RDP_T_126 /* ~ */,
RDP_TT_TOP
};

/* Tree data types */

typedef struct rdp_tree_node_data_struct
{
  SCAN_DATA
  
} rdp_tree_node_data;
typedef struct rdp_tree_edge_data_struct
{
  int rdp_edge_kind;
  
} rdp_tree_edge_data;

/* Symbol table support */
/* Parser start production */
void lexerGrammar(void);



#endif

/* End of cpp_lex.h */