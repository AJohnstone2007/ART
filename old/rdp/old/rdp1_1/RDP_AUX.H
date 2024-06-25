/****************************************************************************
*
* RDP release 1.00 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Feb 1994
*
* rdp_aux.h - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef RDP_SUPP_H
#define RDP_SUPP_H

#include "symbol.h"
#include "set.h"

#define RDP_SET_SIZE 200

extern char
 rdp__force,                    /* flag to force output file writing */
 rdp__expanded,                 /* flag to force expanded BNF printing */
 rdp__verbose,                  /* verbose mode flag */
*rdp__primary_id;               /* identifier for parent production */

extern symbol__
* rdp__start_prod,              /* symbol for first production */
*rdp__base;                     /* pointer to production chain scope */

extern unsigned rdp__component; /* sub-production component number */

extern char
*rdp__dir_title,                /* string from TITLE directive */
*rdp__dir_suffix,               /* string from SUFFIX directive */
*rdp__dir_pre_process,          /* string from PRE_PROCESS directive */
*rdp__dir_post_process,         /* string from POST_PROCESS directive */
*rdp__dir_output_file;          /* string from OUTPUT_FILE directive */

extern unsigned rdp__dir_set_size,  /* number from SET_SIZE directive */
 rdp__dir_hex_dollar,           /* HEX DOLLAR flag */
 rdp__dir_text_mode,            /* TEXT_MODE flag */
 rdp__dir_case_insensitive,     /* CASE_INSENSITIVE flag */
 rdp__dir_show_skips,           /* SHOW_SKIPS flag */
 rdp__dir_newline_visible,      /* NEWLINE_VISIBLE flag */
 rdp__dir_passes,               /* PASSES directive */
 rdp__dir_hash_size,            /* HASH_SIZE directive */
 rdp__dir_hash_prime,           /* HASH_PRIME directive */
 rdp__dir_tab_width,            /* TAB_WIDTH directive */
 rdp__dir_text_size,            /* TEXT_SIZE directive */
 rdp__dir_max_errors,           /* MAX_ERRORS directive */
 rdp__dir_max_warnings;         /* MAX_WARNINGS directive */

struct rdp__string_list
{
  char *str1,
  *str2;
  struct rdp__string_list *next;
};

extern struct rdp__string_list
*rdp__dir_help,                 /* strings from HELP directives */
*rdp__dir_include;              /* strings from INCLUDE directives */

typedef struct rdp__list_node
{
  char *return_name;
  symbol__ *production;
  struct rdp__list_node *next;
} rdp__list;

struct rdp__data_node
{
  enum                          /* Production classifications */
  {
    K_NUMBER_PRIMITIVE, K_STRING_PRIMITIVE, K_CODE, K_TOKEN,
    K_PRIMARY, K_SEQUENCE, K_DO_FIRST, K_LIST, K_CONDITIONAL,
    K_ITERATION
  } kind;
 int contains_null:
   1,                           /* for quick first calculation */
  been_defined:
   1,                           /* has appeared on LHS of ::= */
  in_use:
   1,                           /* production being checked flag */
  first_done:
   1,                           /* first() completed on this production */
  follow_done:
   1;                           /* follow() completed on this production */
  set__ first;                  /* set of first symbols in this production */
  unsigned first_cardinality;   /* number of elements in first set */
  set__ follow;                 /* set of follow symbols in this production */
  unsigned follow_cardinality;  /* number of elements in follow set */
  unsigned token_value;         /* token value for tokens */
  struct rdp__list_node *list;  /* list of alternatives or items */
  symbol__ *supplementary_token;/* spare token pointer */
  unsigned count;               /* list length */
  char *return_type;            /* return_type name */
  unsigned return_type_stars;   /* number of levels of inrdp__direction in return type */
  int extended_class;           /* extended keyword class number */
  char *close;                  /* extended keyword close string */
};

/*macro for retrieving fields */
#define DATA(n,field)	(((struct rdp__data_node*) n->data)->field)

/* globally visible functions */

void rdp__check_eoln(char *id);
void rdp__check_prod_name_valid(char *id);
void rdp__check_comment_token_valid(char *id);
void rdp__check_string_token_valid(char *id);
void rdp__check_token_valid(char *id);
symbol__ *rdp__find(char *id, unsigned kind, int must_be_new);
int rdp__post_process(char *outputfilename, char force, symbol__ * base);
void rdp__pre_process(void);

#endif

/* End of rdp_aux.h */
