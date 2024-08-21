/*****************************************************"**************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_sppf.cpp - sppf related functions used by gtb_rnglr and gtb_brnglr
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <string.h>
#include <stdlib.h>
#include "graph.h"
#include "memalloc.h"
#include "hist.h"
#include "textio.h"
#include "gtb.h"
#include "gtb_lex.h"
#include "gtb_gram.h"
#include "gtb_nfa.h"
#include "gtb_dfa.h"
#include "gtb_gdg.h"
#include "gtb_sr.h"
#include "gtb_scr.h"
#include "gtb_sppf.h"
#include "gtb_rnglr_rec.h"

static derivation *vcg_sppf_render_derivation = NULL;
unsigned sppf_epsilon_node_count;
unsigned sppf_epsilon_edge_count;

void sppf_set_render_derivation(derivation *derivation)
{
  vcg_sppf_render_derivation = derivation;
}

void  sppf_vcg_print_edge(const void *edge)
{
  if(((sppf_node*) graph_destination(edge))->j == (unsigned) INT_MIN)
    text_printf("color: red");
}

void sppf_vcg_print_node(const void *node)
{
  sppf_node *this_node = (sppf_node*) node;

#if 0
  if (this_node->x > 0)
    if (this_node->x >= vcg_sppf_render_derivation->dfa->nfa->grammar->first_level_0_slot)
    {
      text_printf("label:\"[%i]", this_node->x - vcg_sppf_render_derivation->dfa->nfa->grammar->first_level_0_slot);

      gram_print_symbol_id(gram_find_symbol_by_number(vcg_sppf_render_derivation->dfa->nfa->grammar, this_node->x - vcg_sppf_render_derivation->dfa->nfa->grammar->first_level_0_slot));

      text_printf("%i\"", this_node->j);

    }
    else
    {
      text_printf("label:\"");
      gram_print_symbol_id(gram_find_symbol_by_number(vcg_sppf_render_derivation->dfa->nfa->grammar, this_node->x));
      text_printf(" %i\"",this_node->j);
    }
#else
  {
    if (this_node->j == (unsigned) INT_MIN)    /* esppf node */
    {
      text_printf("bordercolor:red ");

      if (this_node->x == 0)
        text_printf("shrink:1 shape:circle label:\"0\"");
      else if (this_node->x == (unsigned) INT_MIN)
        text_printf("label:\"      \"");
      else
      {
        text_printf("label:\"");
        gram_print_symbol_id(gram_find_symbol_by_number(vcg_sppf_render_derivation->dfa->nfa->grammar, this_node->x));
        text_printf("\"");
      }
    }
    else
    {
      if (this_node->x != 0)
      {
        if (this_node->x >= vcg_sppf_render_derivation->dfa->nfa->grammar->first_level_0_slot)
          text_printf("label:\"[%i] %i\"",
                      this_node->x - vcg_sppf_render_derivation->dfa->nfa->grammar->first_level_0_slot,
                      this_node->j
                      );
        else
        {
          text_printf("label:\"");
          gram_print_symbol_id(gram_find_symbol_by_number(vcg_sppf_render_derivation->dfa->nfa->grammar, this_node->x));
          text_printf(" %i\"",this_node->j);
        }
      }
      else
        text_printf("shrink:1 shape:circle label:\"%i\"", this_node->j);
    }
  }
  #endif
}

void sppf_statistics(derivation* this_derivation, char *message)
{
  unsigned terminal_node_count = 0;
  unsigned nonterminal_node_count = 0;
  unsigned intermediate_node_count = 0;
  unsigned epsilon_node_count = 0;
  unsigned pack_node_count = 0;
  unsigned epsilon_to_epsilon_edge_count = 0;
  unsigned normal_to_epsilon_edge_count = 0;
  unsigned normal_to_normal_edge_count = 0;
  unsigned bad_edge_count = 0;

  for (sppf_node * this_node = (sppf_node*) graph_next_node(this_derivation->sppf); (sppf_node*) this_node != NULL; this_node = (sppf_node*) graph_next_node(this_node))
  {
    if (this_node->j == (unsigned) INT_MIN)
      epsilon_node_count++;
    else if (this_node->x == 0)
      pack_node_count++;
    else if (this_node->x < this_derivation->dfa->nfa->grammar->first_nonterminal)
      terminal_node_count++;
    else if (this_node->x < this_derivation->dfa->nfa->grammar->first_level_0_slot)
      nonterminal_node_count++;
    else
      intermediate_node_count++;

    for (void *this_edge = graph_next_out_edge(this_node); this_edge != NULL; this_edge = graph_next_out_edge(this_edge))
      if ( ((sppf_node*) graph_source(this_edge))->j == (unsigned) INT_MIN && ((sppf_node*) graph_destination(this_edge))->j == (unsigned) INT_MIN )
        epsilon_to_epsilon_edge_count++;
      else if ( ((sppf_node*) graph_source(this_edge))->j != (unsigned) INT_MIN && ((sppf_node*) graph_destination(this_edge))->j == (unsigned) INT_MIN )
	        normal_to_epsilon_edge_count++;
      else if ( ((sppf_node*) graph_source(this_edge))->j != (unsigned) INT_MIN && ((sppf_node*) graph_destination(this_edge))->j != (unsigned) INT_MIN )
        normal_to_normal_edge_count++;
      else
        bad_edge_count++;
  }

  if (bad_edge_count != 0)
    text_printf("\a%i bad edges!\n", bad_edge_count);

  text_printf("%s: %u epsilon nodes; %u terminal nodes; %u nonterminal nodes; %u intermediate nodes %u pack nodes; %u internal epsilon edges, %u external epsilon edges, %u normal edges\n",
              message,
              epsilon_node_count,
              terminal_node_count,
              nonterminal_node_count,
              intermediate_node_count,
              pack_node_count,
              epsilon_to_epsilon_edge_count,
              normal_to_epsilon_edge_count,
              normal_to_normal_edge_count);
}

void sppf_vcg_epsilon_sppf_print_index(const void *graph)
{
  for (int i = 0; i < vcg_sppf_render_derivation->dfa->nfa->grammar->first_dfa_state; i++)
  {
    if (vcg_sppf_render_derivation->epsilon_sppf_index[i] != NULL)
    {
      text_printf("node:{title:\"i%i\" bordercolor:blue", i);
      text_printf("label:\"");

      if (i == 0)
        text_printf("#");
      else if (GRAM_IS_SLOT(vcg_sppf_render_derivation->dfa->nfa->grammar, i))
        gram_print_slot_by_number(vcg_sppf_render_derivation->dfa->nfa->grammar, i, 1);
      else
        gram_print_symbol_id(vcg_sppf_render_derivation->dfa->nfa->grammar->symbol_index[i]);

      text_printf("\"}\n");
      text_printf("edge:{class:2 sourcename:\"i%i\" targetname:\"%i\" color:blue}\n", i, graph_atom_number(vcg_sppf_render_derivation->epsilon_sppf_index[i]));
    }
  }
}

static sppf_node * epsilon_sppf_add_slot_suffix(derivation *this_derivation, unsigned *this_slot, void *epsilon_sppf_table, int suppress_ultimate_penultimate, sppf_node* base_node)
{
#if defined(RNGLR_EPSILON_TRACE)
  text_printf("epsilon_sppf_add_slot_suffix on slot %i\n", *this_slot);
#endif

  /* Step one: test to see if we're at the end of a production, and abort if necessary */
  if (suppress_ultimate_penultimate)
  {
    gram_edge *out_edge = (gram_edge*) graph_next_out_edge(graph_node_index(this_derivation->dfa->nfa->grammar->rules)[*this_slot]);
    gram_edge *out_out_edge = NULL;
    if (out_edge != NULL)
      out_out_edge = (gram_edge*) graph_next_out_edge(graph_destination(out_edge));

    /* Set penultimate index entry to the nonterminal entry for the symbol after the dot */
    if (out_edge != NULL && out_out_edge == NULL)
      this_derivation->epsilon_sppf_index[*this_slot] = this_derivation->epsilon_sppf_index[out_edge->symbol_table_entry->symbol_number];

    if (out_edge == NULL || out_out_edge == NULL)
      return NULL;
  }

  /* Step two: if the table is in use, then look up the suffix */
  unsigned *suffix = gram_slot_suffix_by_number(this_derivation->dfa->nfa->grammar, *this_slot);

  epsilon_sppf_table_data *


  this_symbol;

  if (epsilon_sppf_table != NULL)
  {

    this_symbol = (epsilon_sppf_table_data*) symbol_find(epsilon_sppf_table, &suffix, sizeof(unsigned*), sizeof(epsilon_sppf_table_data), NULL, SYMBOL_ANY);

    if (this_symbol->sppf_node != NULL)  /* We have an old symbol: update and return */
    {
      mem_free(suffix);  /* don't need this one */
      return (this_derivation->epsilon_sppf_index[*this_slot] = this_symbol->sppf_node);
    }
  }

  /* Step three: if we get here, we need to make a new tree */
  sppf_node *this_sppf_node;
  if (base_node == NULL)
  {
    this_sppf_node = (sppf_node*) graph_insert_node(sizeof(sppf_node), this_derivation->sppf);
    sppf_epsilon_node_count++;
  }
  else
    this_sppf_node = base_node;

  /* Set 'empty' nodes to x is INT_MIN so that rederer can distinguish them from SPPF pack nodes */
  if (epsilon_sppf_table != NULL)
    this_sppf_node->x = (unsigned) INT_MIN;

  this_sppf_node->j = (unsigned) INT_MIN;

  if (*suffix == 0)
  {
    graph_insert_edge(0, this_derivation->epsilon_sppf_index[0], this_sppf_node);    /* epsilon suffix */
    sppf_epsilon_edge_count++;
  }
  else
    for (int count = 1; count <= *suffix; count++)
    {
      graph_insert_edge(0, this_derivation->epsilon_sppf_index[suffix[count]], this_sppf_node);
      sppf_epsilon_edge_count++;
    }

  this_derivation->epsilon_sppf_index[*this_slot] = this_sppf_node;

  /* Step four: put the subtree into the new symbol table entry if there was one */
  if (epsilon_sppf_table != NULL)
    this_symbol->sppf_node = this_sppf_node;

  return this_sppf_node;
}

void sppf_create_epsilon_sppf(derivation* this_derivation)
{
  sppf_epsilon_node_count = 0;
  sppf_epsilon_edge_count = 0;

  this_derivation->epsilon_sppf_index = (sppf_node**) mem_calloc(this_derivation->dfa->nfa->grammar->first_dfa_state, sizeof(sppf_node*));

  void *epsilon_sppf_table = epsilon_sppf_table = symbol_new_table("Epsilon SPPF entries", 101, 31,
                                                                  symbol_compare_pointer_to_counted_unsigned,
                                                                  symbol_hash_pointer_to_counted_unsigned,
                                                                  symbol_print_pointer_to_counted_unsigned);

  this_derivation->epsilon_sppf_index[0] = (sppf_node*) graph_insert_node(sizeof(sppf_node), this_derivation->sppf);
  sppf_epsilon_node_count++;
  this_derivation->epsilon_sppf_index[0]->x = GRAM_EPSILON_SYMBOL_NUMBER;
  this_derivation->epsilon_sppf_index[0]->j = (unsigned) INT_MIN;

  unsigned *nullable_reduction_numbers = set_array(&this_derivation->dfa->nfa->grammar->right_nullable_reductions);

  /* First create epsilon-sppf nodes for nonterminals by looking for leader reductions (i.e. accepting level 1 slots */
  for (unsigned *this_nullable_reduction_number = nullable_reduction_numbers;
       *this_nullable_reduction_number != SET_END;
       this_nullable_reduction_number++)
  {
    if (*this_nullable_reduction_number < this_derivation->dfa->nfa->grammar->first_level_2_slot)   /* make a node for the RHS */
    {
      int this_nonterminal_number = ((gram_node*) graph_source(graph_next_in_edge((gram_node*) graph_node_index(this_derivation->dfa->nfa->grammar->rules)[*this_nullable_reduction_number])))->symbol_table_entry->symbol_number;

      if (this_derivation->epsilon_sppf_index[this_nonterminal_number] == NULL)
      {
        this_derivation->epsilon_sppf_index[this_nonterminal_number] = (sppf_node*) graph_insert_node(sizeof(sppf_node), this_derivation->sppf);
        sppf_epsilon_node_count++;
        this_derivation->epsilon_sppf_index[this_nonterminal_number]->x = this_nonterminal_number;
        this_derivation->epsilon_sppf_index[this_nonterminal_number]->j = (unsigned) INT_MIN;
      }
    }
  }

  /* Now create symbol table entries */
  for (unsigned *this_nullable_reduction_number = nullable_reduction_numbers;
       *this_nullable_reduction_number != SET_END;
       this_nullable_reduction_number++)
  {
    if (*this_nullable_reduction_number < this_derivation->dfa->nfa->grammar->first_level_2_slot)
    {
      gram_node *lhs_node = (gram_node*) graph_source(graph_next_in_edge(graph_node_index(this_derivation->dfa->nfa->grammar->rules)[*this_nullable_reduction_number]));

      /* Now test to see if we have exactly one nullable production */
      int nullable_productions = 0;

      for (gram_edge* this_edge = (gram_edge*) graph_next_out_edge(lhs_node); this_edge != NULL; this_edge = (gram_edge*) graph_next_out_edge(this_edge))
        if (set_includes_element(&this_derivation->dfa->nfa->grammar->right_nullable_reductions, graph_atom_number(graph_destination(this_edge))))
          nullable_productions++;

      if (script_gtb_verbose())
        text_printf("Nonterminal %s has %i nullable productions\n", lhs_node->symbol_table_entry->id, nullable_productions);

      // I'm a leader, so find my LHS and then add a suffix for each RHS
      if (nullable_productions == 1)
        epsilon_sppf_add_slot_suffix(this_derivation, this_nullable_reduction_number, NULL, 0, this_derivation->epsilon_sppf_index[lhs_node->symbol_table_entry->symbol_number]);
      else
      {
        graph_insert_edge(0,
                          epsilon_sppf_add_slot_suffix(this_derivation, this_nullable_reduction_number, NULL, 0, NULL),
                          this_derivation->epsilon_sppf_index[lhs_node->symbol_table_entry->symbol_number]);
        sppf_epsilon_edge_count++;
      }
    }
    else  /* required nullable parts */
      epsilon_sppf_add_slot_suffix(this_derivation, this_nullable_reduction_number, epsilon_sppf_table, 1, NULL);
  }

  mem_free(nullable_reduction_numbers);
}

