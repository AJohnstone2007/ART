/*****************************************************************************
*
* gramconv version 2.11 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 21 August 2006
*
* gc_aux.c - auxilliary functions for the grammar converter
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GC_AUX_H
#define GC_AUX_H

#include "gramconv.h"

typedef struct p_block {char *id; char *type; struct p_block *next;} p_block;

enum gc_kind {EK_NONE, EK_ROOT, EK_SEQUENCE, EK_OR, EK_PERMUTE, EK_EPSILON,
              EK_TERMINAL, EK_NONTERMINAL, EK_LHS_NONTERMINAL, EK_ITER,
              EK_INVERT, EK_DIFF, EK_RANGE, EK_ELLIPSIS, EK_ACTION, EK_CHAR_SET};
enum gc_promotion {EP_NONE, EP_UNDER, EP_OVER};

extern int art_output;
extern int bison_output;
extern int gtb_output;
extern int iso_output;
extern int sdf_output;
extern int yacc_output;
extern int pure_grammar;
extern int terminalise;
extern int left_recursion_removal;
extern int curly_parenthesis;
extern unsigned long halt_level;
extern int vcg_output;

extern int parser_output;
extern char *start_nonterminal;
extern char *whitespace_nonterminal;

extern int default_substitute;
extern int default_multiply;
extern int default_head_tail_expansion;
extern int default_left_recursive;
extern int default_right_recursive;
extern lhs_count;

void gc_post_parse(void *rdp_tree, int argc, char *argv[]);
int string_has_opt_suffix(char *name);
int lc_execute(char *lc_string);
#endif

