/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 26 September 2000
*
* gtb_nfa.c - Nondeterministic Finite Automaton functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <time.h>
#include "graph.h"
#include "hist.h"
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
#include "gtb_sr.h"
#include "gtb_gp.h"

//#define NFA_TRACE

static void * headers;           /* Symbol table */
static nfa *this_nfa;
static set_ terminal_set = SET_NULL;
static set_ nonterminal_set = SET_NULL;
static unsigned header_number;
static unsigned checked_headers;
static unsigned nodes;
static unsigned normal_edges;
static unsigned nonterminal_epsilon_edges;
static unsigned back_edges;
static unsigned reduction_edges;


int nfa_compare_header(void * left, void * right)
{
  if ((*(header **) left)->nonterminal_symbol->symbol_number > (*(header **) right)->nonterminal_symbol->symbol_number)
    return 1;
  else if ((*(header **) left)->nonterminal_symbol->symbol_number < (*(header **) right)->nonterminal_symbol->symbol_number)
    return -1;

  unsigned char_index;
  unsigned minimum_length = (*(header**) left)->follow_set->length < (*(header **) right)->follow_set->length ? (*(header**) left)->follow_set->length : (*(header **) right)->follow_set->length;

  for (char_index = 0; char_index < minimum_length; char_index++)
    if ((*(header **) left)->follow_set->elements[char_index] > (*(header **) right)->follow_set->elements[char_index])
      return 1;
    else if ((*(header **) left)->follow_set->elements[char_index] < (*(header **) right)->follow_set->elements[char_index])
      return -1;

  if ((*(header **) left)->follow_set->length > minimum_length)
  {
    for (; char_index < (*(header **) left)->follow_set->length; char_index++)
      if ((*(header **) left)->follow_set->elements[char_index] != 0)
        return 1;
  }
  else if ((*(header **) right)->follow_set->length > minimum_length)
  {
    for (; char_index < (*(header **) right)->follow_set->length; char_index++)
      if ((*(header **) right)->follow_set->elements[char_index] != 0)
        return -1;
  }

  return 0;
}

unsigned nfa_hash_header(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = (*(header **)data)->nonterminal_symbol->symbol_number;
  unsigned count = (*(header **)data)->follow_set->length;
  unsigned char *string = (*(header **)data)->follow_set->elements;

  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    {
      if (*string != 0)
        hashnumber = * string + hash_prime * hashnumber;

      string++;
    }

  return hashnumber;
}

void nfa_print_header(const void * symbol)
{
  if (symbol == NULL)
    text_printf("Null symbol");
  else
  {
    text_printf("%i, %i, {", (*(header **) symbol)->header_number, (*(header **) symbol)->nonterminal_symbol->symbol_number);
    set_print_set((*(header **) symbol)->follow_set, NULL, 0);
    text_printf("}");
  }
}

int nfa_compare_states(nfa_state *left, nfa_state *right)
{
  if (left->header_number > right->header_number)
    return -1;
  else if (left->header_number < right->header_number)
    return 1;
  else /* header_number's equal */
  {
    if (left->slot_number > right->slot_number)
      return -1;
    else if (left->slot_number < right->slot_number)
      return 1;
    else
      return 0;
  }
}

histogram_node *header_histogram = NULL;

static void nfa_write_histogram(histogram_node* this_histogram)
{
  text_printf("NFA has %lu headers distributed as follows\n", hist_sum_buckets(this_histogram));

  for (histogram_node *running_hist_node = this_histogram;
       running_hist_node != NULL;
       running_hist_node = running_hist_node->next)
    if (running_hist_node->value != 0)
      text_printf("%6u %s\n", running_hist_node->value, gram_find_symbol_by_number(this_nfa->grammar, running_hist_node->bucket)->id);

  text_printf("\n");
}

static void add_called_from(header *callee, header *caller_header, gram_node* caller_slot)
{
  called_from_list *temp = (called_from_list*) mem_calloc(1, sizeof(called_from_list));
  temp->header = caller_header;
  temp->grammar_slot = caller_slot;
  temp->next = callee->called_from;
  callee->called_from = temp;
}

header *nfa_construct_nfa_rec(gram_symbols_data *this_symbol, set_ *follow_set)
{
    // Print out some user confidence displays
    if (++checked_headers % 100000 == 0)
      printf("Checked %u headers, created %u headers\n", checked_headers, header_number);
    if (checked_headers % 1000000 == 0)
      nfa_write_histogram(header_histogram);


  header this_header_key = {this_symbol, follow_set};
  header_symbol this_header_key_symbol;
  this_header_key_symbol.header = &this_header_key;

  header_symbol *this_header_symbol = NULL;

  if (!this_nfa->unrolled) // An optimisation here
    this_header_symbol = (header_symbol*) symbol_lookup_key(headers, &this_header_key_symbol, NULL);

#if defined(NFA_TRACE)
    printf("Searched for header with symbol %s and set {",
                                                      this_header_key.nonterminal_symbol->id);
    gram_print_set_of_symbols(this_nfa->grammar, this_header_key.follow_set);
    printf("}: %sfound\n", this_header_symbol == NULL ? "not " : "");
#endif

  if (this_header_symbol == NULL)  // Make a new header
  {
    unsigned instance_number = 0;
    this_header_symbol = (header_symbol*) mem_calloc(1, sizeof(header_symbol));
    this_header_symbol->header = (header*) mem_calloc(1, sizeof (header));

    this_header_symbol->header->nonterminal_symbol = this_symbol;
    this_header_symbol->header->follow_set = (set_*) mem_calloc(1, sizeof(set_));
    set_unite_set(this_header_symbol->header->follow_set, follow_set);

    this_header_symbol = (header_symbol*) symbol_insert_key(headers, this_header_symbol, sizeof(header_symbol), sizeof(header_symbol));
    this_header_symbol->header->header_number = header_number++;

#if defined(NFA_TRACE)
    printf("Made header %u with symbol %s and set {", this_header_symbol->header->header_number,
                                                      this_header_symbol->header->nonterminal_symbol->id);
    gram_print_set_of_symbols(this_nfa->grammar, this_header_symbol->header->follow_set);
    printf("}\n");
#endif

    hist_update(header_histogram, this_header_symbol->header->nonterminal_symbol->symbol_number);  // Update the histogram statistics

    // If we have some RHS nonterminals, make a 'back' edge block (only really back for Knuth style, of course...
    if (this_symbol->instance_count != 0)
      this_header_symbol->header->back_edges = (header **) mem_calloc(this_symbol->instance_count + 1, sizeof(header*));

    nodes++;

    for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);       /* Iterate over productions */
         this_production_edge != NULL;
         this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
    {
      header_symbol *back_header_symbol;


      if (this_nfa->unrolled)
      {
        if (this_symbol->instance_count != 0)
          this_header_symbol->header->back_edges[instance_number] = this_header_symbol->header; /* flag to tell unrolled headers which production is in progress */

        unsigned this_production_instance_count = 0;

        // Walk this production counting RHS instances in case we need to jump ahead later
        for (gram_edge *this_element_edge = this_production_edge;
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          if (this_element_edge->symbol_table_entry->rule_tree != NULL)
            this_production_instance_count++;

        // If there are some instances, then this is a recursion candidate
        if (this_production_instance_count > 0)
        {
          back_header_symbol = this_header_symbol;

#if defined(NFA_TRACE)
          printf("At header %u (%s) instance number %u, item ",
                 this_header_symbol->header->header_number,
                 this_header_symbol->header->nonterminal_symbol->id,
                 instance_number);
          gram_print_slot((gram_node*) graph_destination(this_production_edge), 0);
          printf("\n");
#endif

          // Now walk down this hash chanin looking for an earlier instance of this nonterminal
          while ((back_header_symbol = (header_symbol*) symbol_next_symbol(headers, back_header_symbol)) != NULL)
          {
#if defined(NFA_TRACE)
            printf("Check back header %u:, %u, %u\n",
                   back_header_symbol->header->header_number,
                   back_header_symbol->header->back_edges[instance_number] == NULL ? 0 : back_header_symbol->header->back_edges[instance_number]->header_number,
                   back_header_symbol->header->back_edges[instance_number + this_production_instance_count] == NULL ? 0 : back_header_symbol->header->back_edges[instance_number + this_production_instance_count]->header_number);
#endif
            if (back_header_symbol->header->back_edges[instance_number] != NULL &&
                back_header_symbol->header->back_edges[instance_number + this_production_instance_count] == NULL)
              break;
          }

          if (back_header_symbol != NULL) // Point back and skip to th efirst instance of the next production
          {
#if defined(NFA_TRACE)
            printf("Found\n");
#endif
            this_header_symbol->header->back_edges[instance_number] = back_header_symbol->header;
            add_called_from(back_header_symbol->header, this_header_symbol->header, (gram_node*) graph_destination(this_production_edge));
            instance_number += this_production_instance_count;
            continue;
          }
#if defined(NFA_TRACE)
          else
            printf("Not found\n");  // So continue to Knuth style expansion
#endif
        }
      }

      int element_count = 1;
      for (gram_edge *this_element_edge = this_production_edge;
           this_element_edge != NULL;
           this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)), element_count++)
     {
        nodes++; normal_edges++;

        if (this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_NONTERMINAL)
        {
          set_ temp_set = SET_NULL;

          if (this_nfa->lookahead == 0)            /* LR(0) */
            set_assign_set(&temp_set, follow_set);
          else if (this_nfa->lookahead == -1)      /* SLR(1) */
            set_assign_set (&temp_set, &this_element_edge->symbol_table_entry->follow);
          else if (this_nfa->lookahead == 1)       /* LR(1) */
          {
            set_assign_set(&temp_set, &this_symbol->immediate_instance_follow[instance_number]);
            if (set_includes_element(&temp_set, GRAM_EPSILON_SYMBOL_NUMBER))
            {
              set_unite_set(&temp_set, follow_set);
              set_difference_element(&temp_set, GRAM_EPSILON_SYMBOL_NUMBER);
            }
          }

          // October 2003 innovation: if left recursive then just point to parent
          if (this_nfa->not_left && element_count == 2 && this_element_edge->symbol_table_entry->symbol_number == this_symbol->symbol_number)  // First proper symbol in the chain
          {
              this_header_symbol->header->back_edges[instance_number] = this_header_symbol->header;
              add_called_from(this_header_symbol->header, this_header_symbol->header, (gram_node*) graph_destination(this_element_edge));
          }
          else
          {
            if (!this_nfa->nonterminal_lookahead_sets)  // unwrap nonterminal set to terminals
            {
              gram_unwrap_nonterminal_follow_set(this_nfa->grammar, &temp_set);
              set_difference_set(&temp_set, &nonterminal_set);
              set_difference_element(&temp_set, GRAM_EPSILON_SYMBOL_NUMBER);
            }

            if (this_nfa->singleton_lookahead_sets)
            {
              this_header_symbol->header->singleton_back_edges[instance_number] = (header**) mem_calloc(set_cardinality(&temp_set) + 1, sizeof(header*));  // Make the fanout block

              unsigned *follow_set_elements = set_array(&temp_set);

              int follow_set_element_count;
              unsigned *this_follow_set_element;

              for (follow_set_element_count = 0, this_follow_set_element = follow_set_elements;
                   *this_follow_set_element != SET_END;
                   this_follow_set_element++, follow_set_element_count++)
              {
                set_assign_element(&temp_set, *this_follow_set_element);
                this_header_symbol->header->singleton_back_edges[instance_number][follow_set_element_count] = nfa_construct_nfa_rec(this_element_edge->symbol_table_entry, &temp_set);
                nonterminal_epsilon_edges++;
                add_called_from(this_header_symbol->header->singleton_back_edges[instance_number][follow_set_element_count], this_header_symbol->header, (gram_node*) graph_destination(this_element_edge));
              }
            }
            else
            {
              this_header_symbol->header->back_edges[instance_number] = nfa_construct_nfa_rec(this_element_edge->symbol_table_entry, &temp_set);
              nonterminal_epsilon_edges++;
              add_called_from(this_header_symbol->header->back_edges[instance_number], this_header_symbol->header, (gram_node*) graph_destination(this_element_edge));
            }
          }
          instance_number++;
          set_free(&temp_set);
        }
      }
    }
  }

  header *ret_header = this_header_symbol->header;

  if (this_nfa->unrolled)
    symbol_unlink_symbol(this_header_symbol);

  return ret_header;
}


/* Lookahead: <0 =>lhs;  0 => none; >0 => rhs.
   So, LR(1) corresponds to 1; LR(0) to 0 and SLR(1) to -1
*/
nfa *nfa_construct_nfa(grammar *this_gram, int unrolled, int not_left, int lookahead, int nullable_reductions, int nonterminal_lookahead_sets, int singleton_lookahead_sets)
{
  if (lookahead > 1 || lookahead < -1)
    text_message(TEXT_FATAL, "'nfa' paramater 3: only lookahead values of -1, 0 and 1 are supported\n");

  gram_tidy(this_gram, 1, 1);

  headers = symbol_new_table("header", 779563, 31, nfa_compare_header, nfa_hash_header, nfa_print_header);
  header_number = this_gram->first_header;
  checked_headers = normal_edges = nonterminal_epsilon_edges = back_edges = reduction_edges = nodes = 0;

  hist_init(&header_histogram);

  this_nfa = (nfa*) mem_calloc(1, sizeof(nfa));
  this_nfa->grammar = this_gram;
  this_nfa->headers = headers;
  this_nfa->unrolled = unrolled;
  this_nfa->not_left = not_left;
  this_nfa->lookahead = lookahead;
  this_nfa->nullable_reductions = nullable_reductions;
  this_nfa->nonterminal_lookahead_sets = nonterminal_lookahead_sets;
  this_nfa->singleton_lookahead_sets = singleton_lookahead_sets;

  set_assign_list(& terminal_set, GRAM_EOS_SYMBOL_NUMBER, SET_END);     // Initialise terminal set to end-of-string
  set_assign_list(& nonterminal_set, SET_END);     // Initialise nonterminal set to empty

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
       )
  {
    if (this_symbol->name_space == SCRIPT_NS_TERMINAL)
      set_unite_element(& terminal_set, this_symbol->symbol_number);
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
      set_unite_element(& nonterminal_set, this_symbol->symbol_number);
  }

  set_ follow_set = SET_NULL;
  if (this_nfa->lookahead == 0)
    set_assign_set(&follow_set, &terminal_set);
  else
    set_assign_element(& follow_set, GRAM_EOS_SYMBOL_NUMBER);

  this_nfa->start_header = nfa_construct_nfa_rec(this_gram->start_rule, &follow_set);

  this_nfa->header_count = header_number - this_gram->first_header;

  this_gram->first_dfa_state = header_number;

  this_nfa->header_index = (header**) mem_calloc(this_nfa->header_count + 1, sizeof (header*));

  for (header_symbol* this_header_symbol = (header_symbol *) symbol_next_symbol_in_scope(symbol_get_scope(headers));
       this_header_symbol != NULL;
       this_header_symbol = (header_symbol*) symbol_next_symbol_in_scope(this_header_symbol))
    this_nfa->header_index[this_header_symbol->header->header_number - this_gram->first_header] = this_header_symbol->header;

  if (script_gtb_verbose())
    nfa_write_histogram(header_histogram);

  hist_free(&header_histogram);

  if (script_gtb_verbose())
  {
    text_printf("Constructed %srolled NFA with %s lookahead; %s reductions; %s,%s lookahead sets:\n",
                this_nfa->unrolled ? "un" : "",
                this_nfa->lookahead == 0 ? "no" : this_nfa->lookahead > 0 ? "rhs (lr)" : "lhs (slr)",
                this_nfa->nullable_reductions? "nullable" : "normal",
                this_nfa->nonterminal_lookahead_sets ? "nonterminal" : "terminal",
                this_nfa->singleton_lookahead_sets ? "singleton" : "full");

    text_printf("  Checked %u headers\n", checked_headers);
    text_printf("  %u nodes of which %u headers;\n"
                "  %u edges of which %u normal edges (black),\n"
                "                    %u nonterminal epsilon edges (red),\n"
                "                    %u back edges (green),\n"
                "                    %u reduction edges (blue).\n",
                nodes, header_number - this_gram->first_header,
                normal_edges + nonterminal_epsilon_edges + back_edges + reduction_edges,
                normal_edges, nonterminal_epsilon_edges, back_edges, reduction_edges);
  }

  return this_nfa;
}

static set_ nfa_write_visited = SET_NULL;
static unsigned nfa_headers;
static unsigned nfa_nodes;
static unsigned nfa_normal_edges;
static unsigned nfa_nonterminal_epsilon_edges;
static unsigned nfa_back_edges;
static unsigned nfa_reduction_edges;

void nfa_write_rec(nfa *this_nfa, header *this_header, unsigned level)
{
  if (set_includes_element(&nfa_write_visited, this_header->header_number))
    return;
  else
    set_unite_element(&nfa_write_visited, this_header->header_number);

  unsigned instance_count = 0;
  gram_symbols_data *this_symbol = this_header->nonterminal_symbol;

  if (!this_nfa->unrolled)
    level = 0;

  if (this_symbol->rule_tree != NULL)
  {
    text_printf("node:{title:\"%u.%u\" horizontal_order:%u level:%u shape:box label:\"%u.%u: %s",
                graph_atom_number(this_symbol->rule_tree), this_header->header_number, this_header->header_number,
                2*level++, graph_atom_number(this_symbol->rule_tree), this_header->header_number, this_symbol->id);
    if (this_nfa->lookahead > 0 || this_nfa->lookahead < -1)
    {
      text_printf(" {");
      gram_print_set_of_symbols(this_nfa->grammar, this_header->follow_set);
      text_printf("}");
    }
    text_printf(" \"}\n");
    nfa_headers++; nfa_nodes++;

    unsigned production_number = 1;

    for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
         this_production_edge != NULL;
         this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), production_number++)
    {
      unsigned this_production_instance_count = 0;
      unsigned element_level = level;
      unsigned element_count = 0;
      for (gram_edge *this_element_edge = this_production_edge;
           this_element_edge != NULL;
           this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        if (this_element_edge->symbol_table_entry->rule_tree != NULL)
          this_production_instance_count++;

/* Debug: expand this to handle singleton lookahead set unrolled NFA's */
      if (this_production_instance_count != 0 && this_nfa->unrolled &&
          this_header->header_number > this_header->back_edges[instance_count]->header_number)
      { /* Unrolled NFA back edge */
        text_printf("backedge:{label:\"#\" color:green class:4 sourcename:\"%u.%u\" targetname:\"%u.%u\"}\n",
                    graph_atom_number(this_header->nonterminal_symbol->rule_tree),
                    this_header->header_number,
                    graph_atom_number(graph_destination(this_production_edge)),
                    this_header->back_edges[instance_count]->header_number);

        instance_count += this_production_instance_count;
        nfa_back_edges++;
      }
      else
      for (gram_edge *this_element_edge = this_production_edge;
           this_element_edge != NULL;
           element_count++, this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
      {

        text_printf("node:{title:\"%u.%u\" level:%u shape:box horizontal_order: %u label: \"%u.%u: ",
                    graph_atom_number(graph_destination(this_element_edge)), this_header->header_number,
                    2*element_level++, this_header->header_number * 100 + graph_atom_number(graph_destination(this_element_edge)),
                    graph_atom_number(graph_destination(this_element_edge)), this_header->header_number
                   );
        gram_print_slot((gram_node*) graph_destination(this_element_edge),1);
        text_printf(" \"");

        if (set_includes_element(&this_nfa->grammar->reductions, graph_atom_number(graph_destination(this_element_edge))) ||
            (this_nfa->nullable_reductions && set_includes_element(&this_nfa->grammar->right_nullable_reductions, graph_atom_number(graph_destination(this_element_edge))))
           )
          if (this_header->called_from == NULL)  //accept state
            text_printf(" bordercolor:cyan borderwidth:3");
          else
            text_printf(" bordercolor:blue borderwidth:3");

        text_printf("}\n");
        nfa_nodes++;

        text_printf("edge:{priority:1000 label:\"");
        gram_print_symbol_id(this_element_edge->symbol_table_entry);

        text_printf("\"  class:%u sourcename:\"%u.%u\" targetname:\"%u.%u\"}\n",
                    this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_NONTERMINAL ? 1 : 2,
                    graph_atom_number(graph_source(this_element_edge)), this_header->header_number,
                    graph_atom_number(graph_destination(this_element_edge)), this_header->header_number);

        nfa_normal_edges++;

        if (set_includes_element(&this_nfa->grammar->reductions, graph_atom_number(graph_destination(this_element_edge))) ||
            (this_nfa->nullable_reductions && set_includes_element(&this_nfa->grammar->right_nullable_reductions, graph_atom_number(graph_destination(this_element_edge))))
           )
        {
          // iterate over called from list adding blue arrows
          for (called_from_list *this_caller = this_header->called_from;
               this_caller != NULL;
               this_caller = this_caller->next)
          {
            if (this_caller->header->header_number <= this_header->header_number) //'red' or down arrows
            {
              if (this_nfa->unrolled)
              {
                text_printf("backedge:{color:blue label:\"R(%s,%u,%u)\" class:5 sourcename:\"%u.%u\" targetname:\"%u.%u\"}\n",
                            this_header->nonterminal_symbol->id, production_number, element_count,
                            graph_atom_number(graph_destination(this_element_edge)), this_header->header_number,
                            graph_atom_number(this_caller->grammar_slot), this_caller->header->header_number);
                nfa_reduction_edges++;
              }
            }
            else  // 'green' or back arrows
              if (this_caller->grammar_slot == (gram_node*) graph_destination(this_production_edge))
              { // Find down arrow for this station
                called_from_list *this_back_caller;

                for (this_back_caller = this_caller->header->called_from;
                     this_back_caller != NULL && this_caller->header->header_number <= this_back_caller->header->header_number;
                     this_back_caller = this_caller->next)
                  ;

                if (this_nfa->unrolled)
                {
                  text_printf("edge:{color:blue label:\"R(%s,%u,%u)\" class:5 sourcename:\"%u.%u\" targetname:\"%u.%u\"}\n",
                              this_header->nonterminal_symbol->id, production_number, element_count,
                              graph_atom_number(graph_destination(this_element_edge)), this_header->header_number,
                              graph_atom_number(this_back_caller->grammar_slot), this_back_caller->header->header_number);
                  nfa_reduction_edges++;
                }
            }
          }
        }

        if (this_element_edge->symbol_table_entry->rule_tree != NULL)
        {
          if (this_nfa->singleton_lookahead_sets)
          {
            for (header** this_header_pointer_array_element = this_header->singleton_back_edges[instance_count];
                 *this_header_pointer_array_element != NULL;
                 this_header_pointer_array_element++)
              text_printf("edge:{color:%s label:\"#\" class:3 sourcename:\"%u.%u\" targetname:\"%u.%u\"}\n",
                          this_header->header_number == this_header->back_edges[instance_count]->header_number ? "magenta" : "red",
                          graph_atom_number(graph_source(this_element_edge)), this_header->header_number,
                          graph_atom_number((*this_header_pointer_array_element)->nonterminal_symbol->rule_tree),
                          (*this_header_pointer_array_element)->header_number);
          }
          else
            text_printf("edge:{color:%s label:\"#\" class:3 sourcename:\"%u.%u\" targetname:\"%u.%u\"}\n",
                        this_header->header_number == this_header->back_edges[instance_count]->header_number ? "magenta" : "red",
                        graph_atom_number(graph_source(this_element_edge)), this_header->header_number,
                        graph_atom_number(this_header->back_edges[instance_count]->nonterminal_symbol->rule_tree), this_header->back_edges[instance_count]->header_number);

          if (this_nfa->singleton_lookahead_sets)
            for (header** this_header_pointer_array_element = this_header->singleton_back_edges[instance_count];
                 *this_header_pointer_array_element != NULL;
                 this_header_pointer_array_element++)
              nfa_write_rec(this_nfa, *this_header_pointer_array_element, level + gram_max_rule_length(this_header->nonterminal_symbol));
          else
            nfa_write_rec(this_nfa, this_header->back_edges[instance_count], level + gram_max_rule_length(this_header->nonterminal_symbol));

          nfa_nonterminal_epsilon_edges++;
          instance_count++;
        }
      }
    }
  }
}

void nfa_render(FILE *output_file, nfa *this_nfa)
{
  FILE * old_text_stream = text_stream();
  set_clear(&nfa_write_visited);

  nfa_headers = 0;
  nfa_nodes = 0;
  nfa_normal_edges = 0;
  nfa_nonterminal_epsilon_edges = 0;
  nfa_back_edges = 0;
  nfa_reduction_edges = 0;

  text_redirect(output_file);
  text_printf("graph:{\n"
              "orientation:top_to_bottom\n"
              "edge.arrowsize:7\n"
              "edge.thickness:1\n"
              "display_edge_labels:yes\n"
              "dirty_edge_labels:yes\n"
              "arrowmode:free\n"
              "node.borderwidth:1\n"
              "priority_phase:yes\n"
              "straight_phase:yes\n");

  nfa_write_rec(this_nfa, this_nfa->start_header, 0);

  text_printf("}\n");
  text_redirect(old_text_stream);

  if (script_gtb_verbose())
  {
    text_printf("Rendered %srolled NFA with %s lookahead; %s reductions; %s,%s lookahead sets:\n",
                this_nfa->unrolled ? "un" : "",
                this_nfa->lookahead == 0 ? "no" : this_nfa->lookahead > 0 ? "rhs (lr)" : "lhs (slr)",
                this_nfa->nullable_reductions? "nullable" : "normal",
                this_nfa->nonterminal_lookahead_sets ? "nonterminal" : "terminal",
                this_nfa->singleton_lookahead_sets ? "singleton" : "full");
    text_printf("  %u nodes of which %u headers;\n"
                "  %u edges of which %u normal edges (black),\n"
                "                    %u nonterminal epsilon edges (red),\n"
                "                    %u back edges (green),\n"
                "                    %u reduction edges (blue).\n",
                nfa_nodes, nfa_headers,
                nfa_normal_edges + nfa_nonterminal_epsilon_edges + nfa_back_edges + nfa_reduction_edges,
                nfa_normal_edges, nfa_nonterminal_epsilon_edges, nfa_back_edges, nfa_reduction_edges);
  }
}

void nfa_write(FILE *output_file, nfa *this_nfa)
{
  FILE * old_text_stream = text_stream();

  text_redirect(output_file);
  text_message(TEXT_WARNING, "write[] on an NFA produces no output: did you mean render[]?\n");
  text_redirect(old_text_stream);
}

void nfa_print_nfa_state(nfa *this_nfa, unsigned header_number, unsigned slot_number, int vcg)
{
  gram_print_slot_by_number(this_nfa->grammar, slot_number, vcg);
  if (header_number != 0 && this_nfa->lookahead != 0)
  {
    text_printf(" {");
    gram_print_set_of_symbols(this_nfa->grammar, this_nfa->header_index[header_number - this_nfa->grammar->first_header]->follow_set);
    text_printf("}");
  }
}


