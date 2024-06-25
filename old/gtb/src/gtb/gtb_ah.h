/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 27 April 2004
*
* gtb_ah.h - Aycock and Horspool functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_AH_H
#define GTB_AH_H

#define AH_ACTION_ERROR 0
#define AH_ACTION_ACCEPT -1
#define AH_ACTION_POP -2
#define AH_ACTION_POP_AND_ACCEPT -3

struct ah_table
{
  void *symbol_table;
  struct grammar *grammar;
  int tilded_state_count;
  int nontilded_state_count;
  int *actions;
  int *start;
  int **state_vector;
  unsigned *tilded_nonterminals;
  int *trie_start_states;
  int *trie_negative_start_states;
};

grammar *ahz_left_context_grammar(grammar *this_gram);
ah_table *ah_ah_table(grammar *this_gram);
int ah_ah_recognise(ah_table* this_table, char *string);
void ah_table_write(FILE *output_file, ah_table *this_table);
#endif

