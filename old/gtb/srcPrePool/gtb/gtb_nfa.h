/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 26 September 2000
*
* gtb_nfa.h - Finite State Automaton functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_NFA_H
#define GTB_NFA_H

#include "gtb_gram.h"

struct called_from_list
{
  struct header *header;
  struct gram_node *grammar_slot;
  struct called_from_list *next;
};

struct header
{
  struct gram_symbols_data *nonterminal_symbol;
  struct set_ *follow_set;
  unsigned header_number;
  struct called_from_list *called_from;
  union
  {
    struct header **back_edges;
    struct header ***singleton_back_edges;
  };
};

struct header_symbol
{
  struct header *header;
};

struct nfa
{
  void *headers;                      //Symbol table holding the header labels
  struct header **header_index;
  struct header *start_header;
  unsigned header_count;
  struct grammar *grammar;
  int lookahead;
  int nullable_reductions:1;
  int unrolled:1;
  int not_left:1;
  int nonterminal_lookahead_sets:1;
  int singleton_lookahead_sets:1;
};

struct nfa_state
{
  unsigned header_number;
  unsigned slot_number;
};

/* Lookahead: <0 =>lhs;  0 => none; >0 => rhs.
   So, LR(1) corresponds to 1; LR(0) to 0 and SLR(1) to -1
*/
int nfa_compare_states(nfa_state *left, nfa_state *right);
void nfa_print_nfa_state(nfa *this_nfa, unsigned header_number, unsigned slot_number, int vcg);
nfa *nfa_construct_nfa(grammar *this_gram, int unrolled, int not_left, int lookahead, int nullable_reductions, int nonterminal_lookahead_sets, int singleton_lookahead_sets);
void nfa_write(FILE *output_file, nfa *this_nfa);
void nfa_render(FILE *output_file, nfa *this_nfa);
#endif

