/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* rdp_aux.h - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef RDP_AUX_H
#define RDP_AUX_H

#include "symbol.h"
#include "set.h"

#define RDP__RESERVED_WORDS                                      \
/* ANSI C reserved words */                                      \
"auto", "break", "case", "char", "const", "continue", "default", \
"do", "double", "else", "enum", "extern", "float", "for",        \
"goto", "if", "int", "long", "register", "return", "short",      \
"signed", "sizeof", "static", "struct", "switch", "typedef",     \
"union", "unsigned", "void", "volatile", "while",                \
/* C++ reserved words */                                         \
								 \
/* ANSI C library names (a selection) */                         \
"printf"

#define RDP__DATA                                                           \
  char *id;                                                                 \
  int token;                                                                \
 kind_type kind;                                                            \
  char *return_type;            /* return_type name */                      \
  unsigned return_type_stars;   /* number of indirections in return type */ \
  char *token_string;           /* pointer to token value as a string */    \
  char *token_enum;             /* pointer to token value as enum element */\
 int contains_null:                                                         \
   1,                           /* for quick first calculation */           \
  parameterised:                                                            \
   1,                           /* production has inherited attributes */   \
  code_successor:                                                           \
   1,                           /* mark items that follow a code item */    \
  code_terminator:                                                          \
   1,                           /* mark last code item in sequence */       \
  code_only:                                                                \
   1,                           /* primary production with code only */     \
  been_defined:                                                             \
   1,                           /* has appeared on LHS of ::= */            \
  in_use:                                                                   \
   1,                           /* production being checked flag */         \
  first_done:                                                               \
   1,                           /* first() completed on this production */  \
  follow_done:                                                              \
   1;                           /* follow() completed on this production */ \
  set__ first;                  /* set of first symbols */                  \
  unsigned call_count;          /* how many times production is called */   \
  unsigned first_cardinality;   /* number of elements in first set */       \
  set__ follow;                 /* set of follow symbols */                 \
  unsigned follow_cardinality;  /* number of elements in follow set */      \
  unsigned token_value;         /* token value for tokens */                \
  unsigned code_pass;           /* active parser pass for code element */   \
  rdp__param_list *params,      /* list of parameter names (and types) */   \
  *actuals;                     /* list of actuals filled in by item_ret */ \
  struct rdp__list_node *list;  /* list of alternatives or items */         \
  struct rdp__data_node *supplementary_token;/* spare token pointer */      \
  int extended_class;           /* extended keyword class number */         \
  char *close;                  /* extended keyword close string */

enum scan__extended_class_type
{
  E_SIMPLE, /* i.e. not extended! */ E_STRING, E_STRING_ESC,
  E_COMMENT, E_COMMENT_NEST, E_COMMENT_LINE,
  E_COMMENT_VISIBLE, E_COMMENT_NEST_VISIBLE, E_COMMENT_LINE_VISIBLE
};

typedef struct rdp__param_node
{
  char *id,
  *type;
  struct rdp__param_node *next;
} rdp__param_list;

typedef struct rdp__string_list_node
{
  char *str1,
  *str2;
  struct rdp__string_list_node *next;
} rdp__string_list;

typedef struct rdp__table_list_node
{
  char *name;
  unsigned size;
  unsigned prime;
  char* compare;
  char* hash;
  char* print;
  char* data_fields;
  struct rdp__table_list_node *next;
} rdp__table_list;

typedef enum                 /* Production classifications */
{
  K__EXTENDED,
  K__INTEGER, K__REAL, K__STRING, K__CODE, K__TOKEN,
  K__PRIMARY, K__SEQUENCE, K__DO_FIRST, K__LIST, K__CONDITIONAL,
  K__ITERATION
} kind_type;

typedef enum
{
  RDP__OLD, RDP__NEW, RDP__ANY
} symbol_type;


extern rdp__string_list
*rdp__dir_help,                 /* strings from HELP directives */
*rdp__dir_include;              /* strings from INCLUDE directives */

extern rdp__table_list
*rdp__dir_symbol_table;

extern void *rdp__base;                         /* symbol table for the parser */

typedef struct rdp__list_node
{
  char *return_name;
  struct rdp__data_node *production;
  rdp__param_list *actuals;	/* list of actuals used by production call */
  struct rdp__list_node *next;
} rdp__list;

extern char
 rdp__force,                    /* flag to force output file writing */
 rdp__expanded,                 /* flag to force expanded BNF printing */
 rdp__error_production_name,    /* flag to force writing of production name into error messages */
 rdp__verbose,                  /* verbose mode flag */
 rdp__parser_only,              /* omit semantic actions flag */
*rdp__primary_id;               /* identifier for parent production */

extern void *rdp__base;                     /* pointer to production chain scope */

extern set__ rdp__production_set; /* set of production kinds */

extern unsigned rdp__component; /* sub-production component number */
extern unsigned rdp__token_count; /* number of tokens + extendeds */

extern struct rdp__data_node * rdp__start_prod;

extern char
*rdp__dir_title,                /* string from TITLE directive */
*rdp__dir_suffix,               /* string from SUFFIX directive */
*rdp__dir_pre_parse,            /* string from PRE_PARSE directive */
*rdp__dir_post_parse,           /* string from POST_PARSE directive */
*rdp__dir_output_file;          /* string from OUTPUT_FILE directive */

extern unsigned
 rdp__dir_interpreter,          /* INTERPRETER flag */
 rdp__dir_case_insensitive,     /* CASE_INSENSITIVE flag */
 rdp__dir_show_skips,           /* SHOW_SKIPS flag */
 rdp__dir_newline_visible,      /* NEWLINE_VISIBLE flag */
 rdp__dir_passes,               /* PASSES directive */
 rdp__dir_hash_size,            /* HASH_SIZE directive */
 rdp__dir_hash_prime,           /* HASH_PRIME directive */
 rdp__dir_tab_width,            /* TAB_WIDTH directive */
 rdp__dir_max_errors,           /* MAX_ERRORS directive */
 rdp__dir_max_warnings;         /* MAX_WARNINGS directive */

extern unsigned long rdp__dir_text_size;            /* TEXT_SIZE directive */

/* globally visible functions */

/* these belong in rdp__gram! */
void rdp__check_eoln(char *id);
void rdp__check_prod_name_valid(char *id);
void rdp__check_comment_token_valid(char *id);
void rdp__check_string_token_valid(char *id);
void rdp__check_token_valid(char *id);

struct rdp__data_node *rdp__find(char *id, kind_type kind, symbol_type symbol);
struct rdp__data_node *rdp__find_extended(char *open, char *close, int token);
void rdp__post_parse(char *outputfilename, char force);
void rdp__pre_parse(void);

#endif

/* End of rdp_aux.h */
