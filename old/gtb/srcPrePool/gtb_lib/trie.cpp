/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 14 December 2003
*
* trie.h - unigned trie handler
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "set.h"
#include "graph.h"
#include "textio.h"
#include "memalloc.h"
#include "graph.h"
#include "memalloc.h"
#include "textio.h"
#include "gtb.h"
#include "gtb_scr.h"
#include "gtb_gram.h"
#include "gtb_gdg.h"
#include "gtb_gen.h"
#include "gtb_pred.h"
#include "gtb_nfa.h"
#include "gtb_dfa.h"
#include "gtb_lex.h"
#include "gtb_sr.h"
#include "gtb_gp.h"
#include "gtb_ah.h"
#include "trie.h"

//#define TRIE_TRACE

int long trie_state_count;
int long trie_terminal_edge_count;
int long trie_nonterminal_edge_count;

int long trie_tilded_state_count;
int long trie_nontilded_state_count;

void *trie_create(void)
{
  trie_state_count = 0;
  trie_terminal_edge_count = 0;
  trie_nonterminal_edge_count = 0;

  trie_tilded_state_count = 1;
  trie_nontilded_state_count = 1;

  void * ret_trie = graph_insert_graph("trie");

  graph_set_root(ret_trie, graph_insert_node(sizeof(trie_node), ret_trie));
  graph_set_atom_number(graph_root(ret_trie), trie_nontilded_state_count++);

  trie_state_count++;
#if defined (TRIE_TRACE)
  text_printf("!!Created trie: root node %i\n", graph_atom_number(graph_root(ret_trie)));
#endif
  return ret_trie;
}

void trie_restart(void *graph)
{
  graph_clear_graph(graph);

  graph_set_root(graph, graph_insert_node(sizeof(trie_node), graph));
  graph_set_atom_number(graph_root(graph), trie_nontilded_state_count++);
  trie_state_count++;
#if defined(TRIE_TRACE)
  text_printf("!!Restarted trie: root node %i\n", graph_atom_number(graph_root(graph)));
#endif

}

trie_node * trie_insert_unsigned(trie_node* root, unsigned element, int is_tilded, int is_nonterminal)
{
  trie_edge *child_edge;

#if defined(TRIE_TRACE)
  text_printf("\n!!Trying edge %i from node %i %s%s...", element, graph_atom_number(root), is_tilded?" is tilded":" not tilded", is_nonterminal?" nonterminal":" terminal");
#endif

  for (child_edge = (trie_edge*) graph_next_out_edge(root);
       child_edge != NULL && child_edge->element != element;
       child_edge = (trie_edge*) graph_next_out_edge(child_edge))
    ;

  if (child_edge == NULL) //Not found
  {

    trie_node *this_node = (trie_node*) graph_insert_node(sizeof(trie_node), root);

    trie_state_count++;

    ((trie_edge*) graph_insert_edge(sizeof(trie_edge), this_node, root))->element = element;

    if (is_tilded)
    {
      graph_set_atom_number(this_node, -trie_tilded_state_count + AH_ACTION_POP_AND_ACCEPT);
      trie_tilded_state_count++;
    }
    else
    {
      graph_set_atom_number(this_node, trie_nontilded_state_count++);

      if (is_nonterminal)
        trie_nonterminal_edge_count++;
      else
        trie_terminal_edge_count++;
    }

    root = this_node;
#if defined(TRIE_TRACE)
  text_printf("not found: new root %i\n", graph_atom_number(root));
#endif
  }
  else
{
  root = (trie_node*) graph_destination(child_edge);
#if defined(TRIE_TRACE)
  text_printf("found: new root %i\n", graph_atom_number(root));
#endif
}
  return root;
}

void trie_vcg_print_graph(const void * graph)
{
  text_printf("orientation:left_to_right");
}

void trie_vcg_print_node(const void * node)
{
  trie_node *this_node = (trie_node*) node;

  text_printf("label:\"%u%s\" shape:%s%s", graph_atom_number(node), this_node->is_accepting ? " pop" : "", this_node->is_accepting ? "ellipse" : "circle", this_node->is_accepting ? " bordercolor:blue" : "" );
}

grammar *trie_render_gram;

void trie_vcg_print_edge(const void * edge)
{
  trie_edge *this_edge = (trie_edge*) edge;

  if (this_edge->is_reduction)
    text_printf("label:\"R%u\" color:blue", this_edge->element);
  else
    text_printf("label:\"%s\"", gram_find_symbol_by_number(trie_render_gram, this_edge->element)->id);
}

#if 0
void trie_test(void)
{
  void *trie = trie_create();

  unsigned first[6] = {1,2,3,4,5,0};

  void* restart = trie_insert_string_of_unsigned(graph_root(trie),first);
  unsigned second[6] = {1,2,3,6,7,0};
  trie_insert_string_of_unsigned(restart,second);
  trie_insert_string_of_unsigned(restart,first);

  FILE *vcg_file = fopen("trie.vcg", "w");
  text_redirect(vcg_file);
  graph_vcg(trie, trie_vcg_print_graph, trie_vcg_print_node, trie_vcg_print_edge);
  text_redirect(stdout);
}
#endif

/* End of trie.cpp */
