/*****************************************************************************
*
* GTB version 2.3 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 26 September 2002
*
* gtb_scr.h - script processor
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_SCR_H
#define GTB_SCR_H

#define GTB_VERSION "GTB V2.41"
#include <stdio.h>
#include "gtb.h"

typedef enum script_namespace_enum
{
  SCRIPT_NS_SPECIAL,
  SCRIPT_NS_TERMINAL,
  SCRIPT_NS_TERMINAL_ELEMENT,
  SCRIPT_NS_NONTERMINAL,
  SCRIPT_NS_SCRIPT
} script_namespace;

typedef enum script_primitive_type_enum
{
  SCRIPT_P_C_ACTION,
  SCRIPT_P_EMPTY,
  SCRIPT_P_VOID,
  SCRIPT_P_BOOLEAN,
  SCRIPT_P_NATURAL,
  SCRIPT_P_INTEGER,
  SCRIPT_P_REAL,
  SCRIPT_P_CHAR,
  SCRIPT_P_STRING,
  SCRIPT_P_FILE,
  SCRIPT_P_COMPOUND,
  SCRIPT_P_BUILT_IN_FUNCTION,
  SCRIPT_P_BUILT_IN_ENUM,
  SCRIPT_P_NONTERMINAL,
  SCRIPT_P_PRODUCTION,
  SCRIPT_P_TERMINAL,
  SCRIPT_P_TERMINAL_ELEMENT,
  SCRIPT_P_EPSILON,
  SCRIPT_P_GRAMMAR,
  SCRIPT_P_GDG,
  SCRIPT_P_NFA,
  SCRIPT_P_DFA,
  SCRIPT_P_AH_TABLE,
  SCRIPT_P_DERIVATION,
  SCRIPT_P_GENERATED_PARSER,
  SCRIPT_P_CYK_TABLE,
  SCRIPT_P_CYCLE_BREAK_SETS,
  SCRIPT_P_AH_TRIE,
  SCRIPT_P_RIA,
  SCRIPT_P_RCA
  } script_primitive_type;

typedef enum script_built_in_enum
{
  SCRIPT_BUILT_IN_illegal,

  SCRIPT_BUILT_IN_write_text,
  SCRIPT_BUILT_IN_write_raw,
  SCRIPT_BUILT_IN_read_text,
  SCRIPT_BUILT_IN_read_raw,
  SCRIPT_BUILT_IN_window,

  SCRIPT_BUILT_IN_self_loops,
  SCRIPT_BUILT_IN_no_self_loops,

  SCRIPT_BUILT_IN_sentences,
  SCRIPT_BUILT_IN_sentential_forms,
  SCRIPT_BUILT_IN_left,
  SCRIPT_BUILT_IN_right,
  SCRIPT_BUILT_IN_random,

  SCRIPT_BUILT_IN_ll,
  SCRIPT_BUILT_IN_slr,
  SCRIPT_BUILT_IN_lalr,
  SCRIPT_BUILT_IN_lr,
  SCRIPT_BUILT_IN_rnglr,
  SCRIPT_BUILT_IN_ri,

  SCRIPT_BUILT_IN_rolled,
  SCRIPT_BUILT_IN_unrolled,
  SCRIPT_BUILT_IN_unrolled_not_left,

  SCRIPT_BUILT_IN_earley_lookahead,
  SCRIPT_BUILT_IN_earley_no_lookahead,

  SCRIPT_BUILT_IN_normal_reductions,
  SCRIPT_BUILT_IN_nullable_reductions,
  SCRIPT_BUILT_IN_terminal_lookahead_sets,
  SCRIPT_BUILT_IN_nonterminal_lookahead_sets,
  SCRIPT_BUILT_IN_full_lookahead_sets,
  SCRIPT_BUILT_IN_singleton_lookahead_sets,

  SCRIPT_BUILT_IN_terminal,
  SCRIPT_BUILT_IN_terminal_all,
  SCRIPT_BUILT_IN_nonterminal,

  SCRIPT_BUILT_IN_tilde_enabled,
  SCRIPT_BUILT_IN_tilde_disabled,

  SCRIPT_BUILT_IN_retain_break_sets,
  SCRIPT_BUILT_IN_prune_break_sets_by_element,
  SCRIPT_BUILT_IN_prune_break_sets_by_set,
  SCRIPT_BUILT_IN_prune_break_sets_by_table,

  SCRIPT_BUILT_IN_delete_break_sets

} script_built_in_enum;

struct script_value
{
  struct script_value *next;
  script_primitive_type type;
  union /*value_union*/ {
   int v_BOOLEAN:1;
   unsigned v_NATURAL;
   int v_INTEGER;
   float v_REAL;
   char v_CHAR;
   char * v_STRING;
   void * v_POINTER;
   struct grammar *v_GRAMMAR;
   struct nfa *v_NFA;
   struct dfa *v_DFA;
   struct ah_table *v_AH_TABLE;
   FILE * v_FILE;
   struct gdg *v_GDG;
   struct derivation_struct *v_DERIVATION;
   struct cyk_table *v_CYK_TABLE;
   void * v_COMPOUND;
   struct script_value (*v_BUILT_IN_FUNCTION) (void *root, int side_effects);
  };
};

/* Symbol table data type for script engine */
struct script_symbols_data
{
  char *id;                         /* identifier */
  int read_only:1;                  /* may not be overwritten */
  script_value *value;
};

extern char* c_value;

void script_check_for_errors(void);                    /* Fatal message if errors have been seen */
void script_close_scope(void);                         /* Close the current scope region in the script symbol table */
void script_initialise(char *sourcefilename);          /* Main post-parse function */
void script_open_scope(void);                          /* Open a new scope region in the script symbol table */
void script_name_scope(char *scope_name);              /* Open a new scope region in the script symbol table */
void script_process(void *rdt);                        /* Main post-parse function */
script_value script_read_symbol_value(rdp_tree_node_data *root, int side_effects);
char *script_strdup(char *str);                        /* Make a copy of a string like Unix strdup */
script_symbols_data *script_symbol_find(char *id);     /* Find symbol in script table */
script_symbols_data *script_symbol_new(char *id);      /* Insert new symbol in script table: must not previously exist */
int script_gtb_verbose(void);
#endif

