/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* rdp_aux.h - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef RDP_AUX_H
#define RDP_AUX_H

#include "arg.h"
#include "symbol.h"
#include "set.h"
#include "graph.h"

#define RDP_RESERVED_WORDS                                       \
/* ANSI C reserved words */ \
"auto", "break", "case", "char", "const", "continue", "default", \
"do", "double", "else", "enum", "extern", "float", "for", \
"goto", "if", "int", "long", "register", "return", "short", \
"signed", "sizeof", "static", "struct", "switch", "typedef", \
"union", "unsigned", "void", "volatile", "while", \
/* C++ reserved words */ \
"operator", \
/* ANSI C library names (a selection) */ \
"printf"

#define RDP_DATA                                                            \
char * id; \
int token; \
unsigned token_value;         /* token value for tokens */ \
unsigned extended_value;      /* extended value for tokens */ \
kind_type kind; \
char * return_type;           /* return_type name */ \
unsigned return_type_stars;   /* number of indirections in return type */ \
char * token_string;          /* pointer to token value as a string */ \
char * token_enum;            /* pointer to token value as enum element */ \
char * extended_enum;         /* pointer to extended value as enum element */ \
int promote_default;          /* default promotion operator */ \
int promote;                  /* promotion operator for inline calls */ \
int delimiter_promote;        /* promotion operator for iterator delimiters */ \
int \
comment_only: \
1,                            /* flag to suppress unused production warning if production contains only comments */ \
contains_null: \
1,                            /* for quick first calculation */ \
parameterised: \
1,                            /* production has inherited attributes */ \
code_successor: \
1,                            /* mark items that follow a code item */ \
code_terminator: \
1,                            /* mark last code item in sequence */ \
code_only: \
1,                            /* primary production with code only */ \
been_defined: \
1,                            /* has appeared on LHS of ::= */ \
in_use: \
1,                            /* production being checked flag */ \
ll1_violation: \
1,                            /* ll(1) violation detected */ \
first_done: \
1,                            /* first() completed on this production */ \
follow_done: \
1;                            /* follow() completed on this production */ \
set_ first;                   /* set of first symbols */ \
unsigned call_count;          /* how many times production is called */ \
unsigned first_cardinality;   /* number of elements in first set */ \
set_ follow;                  /* set of follow symbols */ \
unsigned follow_cardinality;  /* number of elements in follow set */ \
unsigned code_pass;           /* active parser pass for code element */ \
unsigned long lo;             /* minimum iteration count */ \
unsigned long hi;             /* maximum iteration count */ \
rdp_param_list * params,      /* list of parameter names (and types) */ \
* actuals;                    /* list of actuals filled in by item_ret */ \
struct rdp_list_node * list;  /* list of alternatives or items */ \
struct rdp_data_node * supplementary_token;  /* spare token pointer */ \
char * close;                 /* extended keyword close string */

enum scan_extended_class_type
{
  E_SIMPLE,                   /* i.e. not extended! */ E_STRING, E_STRING_ESC, 
  E_COMMENT, E_COMMENT_NEST, E_COMMENT_LINE, 
  E_COMMENT_VISIBLE, E_COMMENT_NEST_VISIBLE, E_COMMENT_LINE_VISIBLE
}; 

enum rdp_promote_op{PROMOTE_DONT, PROMOTE_DEFAULT, PROMOTE, PROMOTE_AND_COPY, PROMOTE_ABOVE}; 

enum rdp_iter_op{ITER_FLAT, ITER_LEFT, ITER_RIGHT}; 

enum rdp_param_type{PARAM_ID, PARAM_STRING, PARAM_REAL, PARAM_INTEGER}; 

typedef struct rdp_param_node
{
  char * id; 
  long int n; 
  double r; 
  char * type; 
  unsigned stars; 
  enum rdp_param_type flavour; 
  struct rdp_param_node * next; 
}rdp_param_list; 

typedef struct rdp_string_list_node
{
  char * str1, 
  * str2; 
  struct rdp_string_list_node * next; 
}rdp_string_list; 

typedef struct rdp_arg_list_node
{
  enum arg_kind_type kind; 
  char * var; 
  char * key; 
  char * desc; 
  struct rdp_arg_list_node * next; 
}rdp_arg_list; 

typedef struct rdp_table_list_node
{
  char * name; 
  unsigned size; 
  unsigned prime; 
  char * compare; 
  char * hash; 
  char * print; 
  char * data_fields; 
  struct rdp_table_list_node * next; 
}rdp_table_list; 

typedef struct rdp_index_table_list_node
{
  char * name; 
  unsigned size; 
  char * compare; 
  char * print; 
  char * data_fields; 
  struct rdp_index_table_list_node * next; 
}rdp_index_table_list; 

typedef enum                  /* Production classifications */
{
  K_EXTENDED, 
  K_INTEGER, K_REAL, K_STRING, K_CODE, K_TOKEN, 
  K_PRIMARY, K_SEQUENCE, K_LIST
}kind_type; 

typedef enum
{
  RDP_OLD, RDP_NEW, RDP_ANY
}symbol_type; 


extern rdp_string_list
* rdp_dir_include;            /* strings from INCLUDE directives */

extern rdp_arg_list
* rdp_dir_args;               /* data from ARG_* directives */

extern rdp_table_list
* rdp_dir_symbol_table; 

extern rdp_index_table_list
* rdp_dir_index_table; 

extern void * rdp_base;       /* symbol table for the parser */

typedef struct rdp_list_node
{
  char * return_name; 
  struct rdp_data_node * production; 
  rdp_param_list * actuals;   /* list of actuals used by production call */
  struct rdp_list_node * next; 
  int promote;                /* promotion operator for this node */
  int promote_epsilon;        /* promotion operator for epsilons generated by this node */
  char * default_action;      /* action to be executed of lo=0 and body not taken */ \
}rdp_list; 

extern char
* rdp_primary_id;             /* identifier for parent production */

extern void * rdp_base;       /* pointer to production chain scope */

extern set_ rdp_production_set;  /* set of production kinds */

extern int
rdp_comment_only,             /* Flag to track productions that contain only comments */
rdp_rule_count,               /* Number of rules declared * 2 */
rdp_force,                    /* flag to force output file writing */
rdp_expanded,                 /* flag to force expanded BNF printing */
rdp_error_production_name,    /* flag to force writing of production name into error messages */
rdp_parser_only,              /* omit semantic actions flag */
rdp_trace,                    /* add trace messages flag */
rdp_verbose;                  /* verbose mode flag */

extern unsigned rdp_component;  /* sub-production component number */
extern unsigned rdp_token_count;  /* number of tokens + extendeds */

extern struct rdp_data_node * rdp_start_prod;

extern char
* rdp_dir_title,              /* string from TITLE directive */
* rdp_dir_suffix,             /* string from SUFFIX directive */
* rdp_dir_pre_parse,          /* string from PRE_PARSE directive */
* rdp_dir_post_parse,         /* string from POST_PARSE directive */
* rdp_dir_global,             /* string from GLOBAL directive */
* rdp_dir_output_file,        /* string from OUTPUT_FILE directive */
* rdp_dir_tree_edge_fields,   /* fild names for tree edge from TREE directive */
* rdp_dir_tree_node_fields;   /* fild names for tree node from TREE directive */

extern unsigned
rdp_dir_derivation_tree,      /* DERIVATION_TREE flag */
rdp_dir_tree,                 /* TREE flag */
rdp_dir_epsilon_tree,         /* EPSILON_TRTEE tree flag */
rdp_dir_annotated_epsilon_tree,  /* ANNOTATED_EPSILON_TRTEE tree flag */
rdp_dir_case_insensitive,     /* CASE_INSENSITIVE flag */
rdp_dir_show_skips,           /* SHOW_SKIPS flag */
rdp_dir_newline_visible,      /* NEWLINE_VISIBLE flag */
rdp_dir_multiple_source_files,/* MULTIPLE_SOURCE_FILES flag */
rdp_dir_passes,               /* PASSES directive */
rdp_dir_hash_size,            /* HASH_SIZE directive */
rdp_dir_hash_prime,           /* HASH_PRIME directive */
rdp_dir_retain_comments,      /* RETAIN_COMMENTS directive */
rdp_dir_max_errors,           /* MAX_ERRORS directive */
rdp_dir_max_warnings;         /* MAX_WARNINGS directive */

extern unsigned long rdp_dir_text_size;  /* TEXT_SIZE directive */
extern unsigned long rdp_dir_tab_width;  /* TAB_WIDTH directive */

/* globally visible functions */

void rdp_add_arg(enum arg_kind_type kind, char * key, char * var, char * desc); 
struct rdp_data_node * rdp_find(char * id, kind_type kind, symbol_type symbol); 
struct rdp_data_node * rdp_find_extended(char * open, char * close, int token); 
void rdp_post_parse(char * outputfilename, int force); 
void rdp_pre_parse(void); 
void * rdp_process_token(char * name); 

#endif

/* End of rdp_aux.h */
