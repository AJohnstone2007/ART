/*******************************************************************************
*
* GTB release 2.00 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 14 October 2003
*
* trie.h - unigned trie handler
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef TRIE_H
#define TRIE_H

#include <stddef.h>
#include <stdio.h>
#include "gtb_gram.h"

struct trie_node
{
  int is_accepting:1;
};

struct trie_edge
{
  int is_reduction:1;
  unsigned element;
};

extern grammar *trie_render_gram;

extern int long trie_state_count;
extern int long trie_nonterminal_edge_count;
extern int long trie_terminal_edge_count;
extern int long trie_tilded_state_count;
extern int long trie_nontilded_state_count;

void *trie_create(void);
void trie_restart(void *graph);

void trie_vcg_print_graph(const void * graph);
void trie_vcg_print_node(const void * node);
void trie_vcg_print_edge(const void * edge);

trie_node * trie_insert_unsigned(trie_node* root, unsigned element, int is_tilded, int is_nonterminal);

void trie_test(void);

#endif

/* End of trie.h */
