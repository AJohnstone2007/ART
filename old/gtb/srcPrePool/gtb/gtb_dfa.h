/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 1 July 2001
*
* gtb_dfa.h - dfa construction functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_DFA_H
#define GTB_DFA_H
#include "gtb_nfa.h"
//#define ptrdiff_t long

struct reduction
{
  int length;
  unsigned symbol_number;
  int is_accepting:1;
};

struct dfa
{
  struct nfa *nfa;
  struct grammar *grammar;
  ptrdiff_t *labels;
  ptrdiff_t **labels_index;
  ptrdiff_t *tables;
  ptrdiff_t **tables_index;
  ptrdiff_t *reductions_index;
  struct reduction *reduction_table;
  unsigned reduction_count;

  int merged:1;
  unsigned state_count;
};

dfa *dfa_construct_dfa(nfa *this_nfa, unsigned table_buffer_size, unsigned label_buffer_size, unsigned hash_buckets_value, unsigned hash_prime_value);
void dfa_dfa_statistics(dfa* this_dfa);
dfa* dfa_la_merge(dfa* this_dfa);
int dfa_retrieve_first_action(dfa *this_dfa, unsigned state, unsigned symbol);
int *dfa_retrieve_all_actions(dfa *this_dfa, unsigned state, unsigned symbol);

void dfa_write(FILE *output_file, dfa *this_dfa);
void dfa_render(FILE *output_file, dfa *this_dfa);
#endif
