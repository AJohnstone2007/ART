/*****************************************************"**************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_rnglr_prs.cpp - an RNGLR parser, as per the TOPLAS paper
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <string.h>
#include <time.h>
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
#include "gtb_rnglr_prs.h"

/* 8 July 2006: created by splitting off this code from gtb_sr.cpp */
/* 8 July 2006: added edge histogram code to check behaviour of Chi set traversal algorithm */
//#define RNGLR_EPSILON_TRACE
//#define RNGLR_RECOGNISER_TRACE
//#define CHI_HIST

#if defined(CHI_HIST)
static histogram_node *chi_edge_histogram = NULL;
static histogram_node *chi_node_histogram = NULL;
static histogram_node *chi_prime_edge_histogram = NULL;
static histogram_node *chi_prime_node_histogram = NULL;
#endif

static unsigned family_checks;
static unsigned family_checks_failed;

/* poor man's generator: use a stack to explore part of the SGG, returning an element of Chi on each iteration */
ssg_edge **reduction_search_stack, **reduction_search_stack_pointer, **reduction_search_stack_limit;

/* Global variable that holds the next member of Chi */
static ssg_node* reduction_search_target_node;

/* Initialise the search stack */
static void reduction_search_generate_init(dfa* this_dfa)
{
  reduction_search_stack = (ssg_edge**) mem_calloc(this_dfa->nfa->grammar->maximum_rule_length, sizeof (ssg_edge*) );
}

/* Return search stack space to heap */
static void reduction_search_generate_cleanup(void)
{
  mem_free(reduction_search_stack);
}

/* Find the first search target, stacking the edges as we go */
static void reduction_search_generate_start(ssg_node* this_node, int length)
{
  reduction_search_target_node = this_node;
  reduction_search_stack_pointer = reduction_search_stack;
  reduction_search_stack_limit = reduction_search_stack + length;

#if defined(RNGLR_RECOGNISER_TRACE)
  text_printf("Search generate start: node (%i,L%i) length %i\n", this_node->value, this_node->level, length);
#endif

#if defined(CHI_HIST)
  hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
#endif

  while (reduction_search_stack_pointer < reduction_search_stack_limit)
  {
    ssg_edge * this_edge = (ssg_edge*) graph_next_out_edge(reduction_search_target_node);

    this_edge->visits++;

#if defined(CHI_HIST)
    hist_update(chi_edge_histogram, graph_atom_number(this_edge));
#endif

    *reduction_search_stack_pointer = this_edge;

#if defined(RNGLR_RECOGNISER_TRACE)
    text_printf("Search generate start: stacked in-edge to state (%u,L%i)\n",
                ((ssg_node*) graph_destination(*reduction_search_stack_pointer))->value,
                ((ssg_node*) graph_destination(*reduction_search_stack_pointer))->level);
#endif
    reduction_search_stack_pointer++;

    reduction_search_target_node = (ssg_node*) graph_destination(this_edge);
#if defined(CHI_HIST)
    hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
#endif
  }
#if defined(RNGLR_RECOGNISER_TRACE)
  text_printf("Search generate start: target is (%i,L%i)\n", reduction_search_target_node->value, reduction_search_target_node->level);
  // Now dump the path
  for (ssg_edge **reduction_search_stack_temp = reduction_search_stack; reduction_search_stack_temp < reduction_search_stack_pointer; reduction_search_stack_temp++)
  {
    text_printf("Path symbol %i", (*reduction_search_stack_temp)->symbol);
//    gram_print_symbol_id_by_number(this_gram, (*reduction_search_stack_temp)->symbol);
    text_printf("\n");
  }
  text_printf("Path end\n");
#endif
}

static void reduction_search_generate_next_target(void)
{
#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next: stack base %p, stack pointer %p, offset %i\n",
                  reduction_search_stack, reduction_search_stack_pointer, reduction_search_stack_pointer - reduction_search_stack
                 );
#endif
  if (reduction_search_stack_pointer == reduction_search_stack)
    reduction_search_target_node = NULL;
  else
  {
    /* unstack until we find an edge with a sibling */
    ssg_edge *sibling_edge;

    while (reduction_search_stack_pointer > reduction_search_stack && (sibling_edge = (ssg_edge*) graph_next_out_edge(*--reduction_search_stack_pointer)) == NULL)
    {
      #if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next: unstacked in-edge %u to state (%u,L%i)\n",
                  graph_atom_number(*reduction_search_stack_pointer),
                  ((ssg_node*) graph_destination(*reduction_search_stack_pointer))->value,
                  ((ssg_node*) graph_destination(*reduction_search_stack_pointer))->level);
      #endif
    }

#if defined(RNGLR_RECOGNISER_TRACE)
    text_printf("Search generate next: after unstacking: stack base %p, stack pointer %p, offset %i, sibling edge %u to state (%u,L%i)\n",
                reduction_search_stack, reduction_search_stack_pointer, reduction_search_stack_pointer - reduction_search_stack,
                graph_atom_number(sibling_edge),
                reduction_search_target_node->value,
                reduction_search_target_node->level
               );
#endif
    if (sibling_edge == NULL)
      reduction_search_target_node = NULL;
    else
    {
      //Push new edge onto stack
      *reduction_search_stack_pointer = sibling_edge;
      reduction_search_stack_pointer++;

      reduction_search_target_node = (ssg_node*) graph_destination(sibling_edge);

#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next: stacking sibling edge %u to state (%u,L%i)\n",
                  graph_atom_number(sibling_edge),
                  reduction_search_target_node->value,
                  reduction_search_target_node->level);
#endif

      sibling_edge->visits++;
      #if defined(CHI_HIST)
      hist_update(chi_edge_histogram, graph_atom_number(sibling_edge));
      #endif


      #if defined(CHI_HIST)
      hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
      #endif

      /* Now continue down new branch */
      while (reduction_search_stack_pointer < reduction_search_stack_limit)
      {
        ssg_edge * this_edge = (ssg_edge*) graph_next_out_edge(reduction_search_target_node);

        this_edge->visits++;

        #if defined(CHI_HIST)
        hist_update(chi_edge_histogram, graph_atom_number(this_edge));
        #endif

        *reduction_search_stack_pointer = this_edge;

        #if defined(RNGLR_RECOGNISER_TRACE)
        text_printf("Search generate start: stacked in-edge to state (%u,L%i)\n",
                    ((ssg_node*) graph_destination(*reduction_search_stack_pointer))->value,
                    ((ssg_node*) graph_destination(*reduction_search_stack_pointer))->level);
        #endif
        reduction_search_stack_pointer++;

        reduction_search_target_node = (ssg_node*) graph_destination(this_edge);
        #if defined(CHI_HIST)
        hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
        #endif
      }
    }
  }

  #if defined(RNGLR_RECOGNISER_TRACE)
  if (reduction_search_target_node == NULL)
    text_printf("Search generate next: target is NULL\n");
  else
  {
    text_printf("Search generate next: target is %i\n", reduction_search_target_node->value);
    // Now dump the path
    for (ssg_edge **reduction_search_stack_temp = reduction_search_stack; reduction_search_stack_temp < reduction_search_stack_pointer; reduction_search_stack_temp++)
    {
      text_printf("Path symbol %i\n", (*reduction_search_stack_temp)->symbol);
    }
    text_printf("Path end\n");
  }
#endif
}

static void reduction_search_rec(ssg_node *node, int depth)
{
#if defined(CHI_HIST)
  hist_update(chi_prime_node_histogram, graph_atom_number(node));
#endif

  if (depth <= 0)
    return;

  for (ssg_edge *this_edge = (ssg_edge*) graph_next_out_edge(node); this_edge != NULL; this_edge = (ssg_edge*) graph_next_out_edge(this_edge))
  {
#if defined(CHI_HIST)
    hist_update(chi_prime_edge_histogram, graph_atom_number(this_edge));
#endif
    reduction_search_rec((ssg_node*) graph_destination(this_edge), depth - 1);
  }
}

typedef struct sppf_node_cache_element{
  int i;
  sppf_node* node;
} sppf_node_cache_element;

derivation *sr_rnglr_parse(dfa * this_dfa, char *string, int reduction_stack_size)
{
  struct reduction_stack_element{
    ssg_node *v;                     /* v in paper */
    reduction* reduction_table_entry;       /* indirectly, X and m in paper. f is X is this implementation! */
    sppf_node *y;                   /* y in the paper */
  } *reduction_stack, *reduction_stack_pointer;

  struct shift_stack_element {
    ssg_node *v;    /* v in paper */
    int k;          /* label[w] in paper */
  } *shift_stack, *shift_stack_prime, *shift_stack_pointer, *shift_stack_prime_pointer, *shift_stack_exchanger;

  clock_t start_time;
  clock_t finish_time;
  derivation *this_derivation;          /* derivation structure to hold output of parse */
  ssg_node **current_frontier;          /* U_k the current frontier */
  ssg_node **next_frontier;             /* U_k+1 the next frontier created via shoft actions */
  ssg_node **frontier_exchanger;        /* dummy frontier pointer used when swapping over frontiers */

  /* Initialisation step 1: sign on */
  text_printf("\n");
  text_message(TEXT_INFO, "RNGLR parse: \'%s\'\n", string);

  /* Initialisation step 2: create derivation structure and SSG structure */
  this_derivation = (derivation *) mem_calloc(1, sizeof(derivation));
  this_derivation->dfa = this_dfa;
  this_derivation->ssg = graph_insert_graph("SSG");
  this_derivation->sppf = graph_insert_graph("SPPF");
  sppf_create_epsilon_sppf(this_derivation);
  FILE *epsilon_sppf_vcg_file = fopen("esppf.vcg", "w");
  text_redirect(epsilon_sppf_vcg_file);
  sppf_set_render_derivation(this_derivation);
  graph_vcg(this_derivation->sppf, sppf_vcg_epsilon_sppf_print_index, sppf_vcg_print_node, sppf_vcg_print_edge);
  fclose(epsilon_sppf_vcg_file);
  family_checks = 0;
  family_checks_failed = 0;
  text_redirect(stdout);

  /* Initialisation step 3: create the frontiers */
  current_frontier = (ssg_node**) mem_calloc(sizeof(ssg_node*), this_dfa->state_count);
  next_frontier = (ssg_node**) mem_calloc(sizeof(ssg_node*), this_dfa->state_count);
  int first_dfa_state = this_dfa->nfa->grammar->first_dfa_state;

  /* Initialisation step 4: create and initialise reduction (R) and shift (Q) sets as stacks */
  reduction_stack = (reduction_stack_element*) mem_calloc(reduction_stack_size == 0 ? 4096 : reduction_stack_size, sizeof (reduction_stack_element));
  shift_stack = (shift_stack_element*) mem_calloc(this_dfa->state_count, sizeof(shift_stack_element));
  shift_stack_prime = (shift_stack_element*) mem_calloc(this_dfa->state_count, sizeof(shift_stack_element));
  reduction_search_generate_init(this_dfa);

  /* Initialisation step 5: initialise lexer and load input symbol array */
  int d = 0;
  lex_init(string, this_dfa->nfa->grammar);

  int lexeme;
  do
  {
    lexeme = lex_lex(); d++;
  }
  while (lexeme != GRAM_EOS_SYMBOL_NUMBER && lexeme != GRAM_ILLEGAL_SYMBOL_NUMBER);

  d--;  /* don't count terminating $ */

  text_message(TEXT_INFO, "RNGLR parse: input length %i character%s, %i lexeme%s\n", strlen(string), strlen(string) == 1 ? "" :"s", d, d == 1 ? "" : "s");

  int *a = (int*) mem_malloc((d+2) * sizeof(unsigned));  /* we don't use a[0], and we need an a[d+1] for $, hence length is d+2 */

  lex_init(string, this_dfa->nfa->grammar);

  int i;

  for (i = 1; i <= d + 1; i++)
    a[i] = lex_lex();

  /* Initialisation step 6: create SPPF_node_cache */

  /* The cache has three roles: (i) to provide fast access to the SPPF node for nonterminal P at level i;
                                (ii) to remember the applied reduction for unpacked nodes;
                                (iii) to mark an SPPF node as having packed children.

     Each element of the cache contains an integer i and a pointer to an SPPF node. We also have a base cache value which is
     initialised to zero, and incremented by 1+|reductions| at the start of each level.

     A cache element is valid if its i-value is >= cache_base. The applied reduction is i_value - cache_base. Already packed nodes
     are represented by an applied reduction value of 1+|reductions|.
  */

  sppf_node_cache_element *sppf_node_cache = (sppf_node_cache_element*) mem_calloc((d + 2) * (this_dfa->grammar->first_level_0_slot - this_dfa->grammar->first_nonterminal), sizeof(sppf_node_cache_element));
  sppf_node_cache_element *cache_element;
  unsigned sppf_cache_base = 0;
  unsigned sppf_cache_base_increment = this_derivation->dfa->reduction_count + 1;  /* We need an illegal value to mark already-packed values, hence the + 1 */

#if defined(RNGLR_RECOGNISER_TRACE)
    text_printf("Allocated %i bytes to sppf_node_cache: %i lexemes and %i nonterminals; %i bytes per cache record\n",
                 (d + 2) * (this_dfa->grammar->first_level_0_slot - this_dfa->grammar->first_nonterminal) * sizeof(sppf_node_cache_element),
                 d,
                 this_dfa->grammar->first_level_0_slot - this_dfa->grammar->first_nonterminal,
                 sizeof(sppf_node_cache_element));
    text_printf("Cache base increment value is %i\n", sppf_cache_base_increment);

#endif

  /* Initialisation step 7: start the clock */
  start_time = clock();

  /* Main algorithm, as per pages 20-21 of the TOPLAS paper */
  /* Test against 1 here, not zero, because our d is one plus the d in the paper */
  if (d == 0)  /* On empty string, see is acc is a member of T(0,$) by checking all reductions and setting this_derivation->accept */
  {
    text_printf("Empty string\n");
#if defined(RNGLR_RECOGNISER_TRACE)
    text_printf("Empty string\n");
#endif
    int *these_parse_actions = dfa_retrieve_all_actions(this_dfa, this_dfa->nfa->grammar->first_dfa_state, GRAM_EOS_SYMBOL_NUMBER);  /* Get T(0, $) */
    int *this_parse_action = these_parse_actions;

    if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then skip it */
      this_parse_action++;

    for (;*this_parse_action != 0; this_parse_action++)                /* Walk the rest of the actions in T(0, $) looking for accepts */
      if (this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)
        this_derivation->accept = 1;

    graph_set_root(this_derivation->sppf, this_derivation->epsilon_sppf_index[this_derivation->dfa->grammar->start_rule->symbol_number]);

    if (graph_root(this_derivation->sppf) == NULL)
      text_message(TEXT_ERROR, "attempt to prune SPPF that has no recognised root node\n");
    else
    {
      sppf_statistics(this_derivation, "Before SPPF pruning");
      graph_retain_set_of_nodes(this_derivation->sppf, graph_reachable_nodes(this_derivation->sppf, graph_root(this_derivation->sppf))
      );
      sppf_statistics(this_derivation, "After SPPF pruning");
    }
  }
  else /* Non-empty string */
  {
    ssg_node *v0 = (ssg_node*) graph_insert_node(sizeof(ssg_node), this_derivation->ssg);  /* Create a node v_0... */
    v0->value = this_dfa->nfa->grammar->first_dfa_state;         /* labelled with the start state of the DFA */
    v0->level = 0;
    current_frontier[0] = v0;                                    /* U_0 = { v_0 } */
    reduction_stack_pointer = reduction_stack;                   /* R = { } */
    shift_stack_pointer = shift_stack;                           /* S = { } */
    a[d+1] = GRAM_EOS_SYMBOL_NUMBER;
    /* NB our current frontier and next frontier arrays represent U_k and U_k+1 at any point: we can't initislise the U_i's to empty because they don't exist */

    int *these_parse_actions = dfa_retrieve_all_actions(this_dfa, this_dfa->nfa->grammar->first_dfa_state, a[1]);  /* Get T(0, a_1) */
    int *this_parse_action = these_parse_actions;                /* If there is a shift, it will be first */

    if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then push (label(v0), k) into Q set */
    {
      shift_stack_pointer->v = v0;
      shift_stack_pointer->k = *this_parse_action;
#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Add to Q: ");
      sr_print_action(this_dfa, shift_stack_pointer->current_frontier_node->value, a[1], *this_parse_action);
#endif
      shift_stack_pointer++;                                     /* bump Q pointer */

      this_parse_action++;                                       /* advance action pointer past the shift action */
    }

    for (; *this_parse_action != 0; this_parse_action++)         /* forall reductions in T(0, a_1) */
    {
      if (!this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)
      {
        reduction_stack_pointer->v = v0;
        reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
        reduction_stack_pointer->y = NULL;              /* y is epsilon in the paper means 'no node' */

  #if defined(RNGLR_RECOGNISER_TRACE)
        text_printf("Load v_0 actions: Add to R: ");
        sr_print_action(this_dfa, reduction_stack_pointer->gss_node->value, a[1], *this_parse_action);
  #endif

        reduction_stack_pointer++;                                 /* Bump R pointer */
      }
    }

    mem_free(these_parse_actions);                               /* Reclaim memory for T(0, a_1) */

    /* Main loop: for i = 0 to d while U_i != { } */
    int thereis_a_shifted_node = true;                           /* true if we created a node during SHIFTER: use to test U_i != { } */
    for (i = 0; i <= d && thereis_a_shifted_node; i++)
        {
#if defined(RNGLR_RECOGNISER_TRACE)
    text_printf("\nStarting frontier %i: a[i] is %i\n", i, a[i]);
#endif
      /* Clear next_frontier */
      /* Switch frontiers and clear next_frontier: we do this at the beginning so that at the end of the runm current_frontier contains U_d */
      frontier_exchanger = current_frontier;
      current_frontier = next_frontier;
      next_frontier = frontier_exchanger;
      memset(next_frontier, 0, sizeof(ssg_node*) * this_dfa->state_count);

      sppf_cache_base += sppf_cache_base_increment;

      /* REDUCER */
      while (reduction_stack_pointer != reduction_stack)
      {
        reduction_stack_pointer--;

        ssg_node *original_reduction_node = reduction_stack_pointer->v;
        reduction *original_reduction_table_entry = reduction_stack_pointer->reduction_table_entry;
        unsigned original_reduction_number = reduction_stack_pointer->reduction_table_entry - this_dfa->reduction_table;
        unsigned original_slot_number = this_dfa->reductions_index[original_reduction_number];

        sppf_node *original_sppf_node = reduction_stack_pointer->y;

#if defined(RNGLR_RECOGNISER_TRACE)
        text_printf("Reducer: State (%i,L%i), input symbol %i, action R[%i]\n",
                    original_reduction_node->value, original_reduction_node->level,
                    a[i+1],
                    original_reduction_table_entry - this_dfa->reduction_table);
#endif

#if defined(CHI_HIST)
        /* Initialise the Chi histograms */
        hist_init(&chi_edge_histogram);
        hist_init(&chi_node_histogram);
        hist_init(&chi_prime_edge_histogram);
        hist_init(&chi_prime_node_histogram);

        /* and now build the chi_prime histograms with a recursive walk */
        reduction_search_rec(original_reduction_node, original_reduction_table_entry->length - 1);
#endif

        reduction_search_generate_start(original_reduction_node, original_reduction_table_entry->length - 1);

        /* reduction_search_target_node now contains the first member of Chi, the set of nodes which can be reached along a
           path of length m - 1, or 0 if m = 0. Each time we call reduction_search_next_target() we get a new member of Chi
           in reduction_search_target_node until all have been visited; subsequent calls yield NULL

           In the paper, the current member of Chi is called u, here we call it reduction_search_target.

           The reduction search stack contains the edges on this path, from which y_{m-1},...y_1 may be found. y_m, of course, is reduction_stack_pointer->sppf_node
        */
        while (reduction_search_target_node != NULL)
        {
          sppf_node *z;

          if (original_reduction_table_entry->length  == 0)
          {
            z = this_derivation->epsilon_sppf_index[original_reduction_table_entry->symbol_number];   /* set z to be the root of the epsilon SPPF */
#if defined(RNGLR_RECOGNISER_TRACE)
            text_printf("z set to epsilon_sppf_index entry for symbol %i (%p)\n", original_reduction_table_entry->symbol_number, z);
#endif
          }
          else
          {
            int j = reduction_search_target_node->level;  /* j is c (as in U_c) in the paper */


#if defined(RNGLR_RECOGNISER_TRACE)
          text_printf("Accessing SPPF cache for symbol %i and substring start %i... ", original_reduction_table_entry->symbol_number, j);
#endif
            cache_element = &(sppf_node_cache[j + ((original_reduction_table_entry->symbol_number - this_dfa->grammar->first_nonterminal) * d)]);

#if defined(RNGLR_RECOGNISER_TRACE)
          text_printf("found level * (|reductions| + 1) + reduction of %i, sppf node based at %p\n", cache_element->i, cache_element->sppf_node);
#endif

            if (cache_element->i < sppf_cache_base)
            {
              cache_element->i = sppf_cache_base + original_reduction_number;
              cache_element->node = z = (sppf_node*) graph_insert_node(sizeof(sppf_node), this_derivation->sppf);
              z->x = original_reduction_table_entry->symbol_number;
              z->j = j;
            }
            else
              z = cache_element->node;
          }

          /* get the shift (the 'goto') out of the table pl is a member of T(k,X) */
          int goto_action = dfa_retrieve_first_action(this_dfa, reduction_search_target_node->value, original_reduction_table_entry->symbol_number);
#if defined(RNGLR_RECOGNISER_TRACE)
          text_printf("Goto state %i: ", goto_action);
#endif

          /* In the paper the goto state is called w, here it is called current_frontier[goto_action - first_dfa_state]. An odd name, which arises from the fact
             that a shift action is simply the state number of the state to be shifted to */

          /* The reducer handles two cases: that in which the node to goto already exists, and that in which it doesn't
             In both cases we need to access the action in T(l, a_{i+1}) so we factor the table access out and perform it here for both cases */
          int *these_goto_parse_actions = dfa_retrieve_all_actions(this_dfa, goto_action, a[i+1]);  /* Get T(goto_state, a_i+1) */
          int *this_goto_parse_action = these_goto_parse_actions;

          if (current_frontier[goto_action - first_dfa_state] != NULL)  /* If there is a w in U_{i} with label l */
          {
#if defined(RNGLR_RECOGNISER_TRACE)
            text_printf("old\n");
#endif
            /* Check out edges of w to see if we have one going to u: as soon as we find one we jump to the end of the reducer and pull the next u from Chi */
            for (ssg_edge* this_edge = (ssg_edge*) graph_next_out_edge(current_frontier[goto_action - first_dfa_state]); this_edge != NULL; this_edge = (ssg_edge*) graph_next_out_edge(this_edge))
              if ((ssg_node*) graph_destination(this_edge) == reduction_search_target_node)
                goto edge_already_exists;

            /* We didn't find an appropriate edge, so now we must add one and stack the appropriate actions for subsequent processing */
            ssg_edge *new_ssg_edge = (ssg_edge*) graph_insert_edge(sizeof(ssg_edge), reduction_search_target_node, current_frontier[goto_action - first_dfa_state]);    /* create an edge from w to u */
            new_ssg_edge->symbol = original_reduction_table_entry->symbol_number;
            new_ssg_edge->sppf_node = z;

            /* Now we must start stacking the reductions from w */
            if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_goto_parse_action))  /* Skip any shift which will be first in sequence of parse actions */
              this_goto_parse_action++;

            if (original_reduction_table_entry->length != 0)         /* If reduction-edge was created by a non-zero length reduction */
            {
              for (; *this_goto_parse_action != 0; this_goto_parse_action++)         /* forall reductions in T(goto_state, a_1) */
              {
                if (!this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)   /* skip accepting reductions */
                {
                  /* Add (u, B, t, z) to R */
                  if (this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length != 0)
                  {
#if defined(RNGLR_RECOGNISER_TRACE)
                    text_printf("On old goto state: Add to R: ");
                    sr_print_action(this_dfa, current_frontier[goto_action - first_dfa_state]->value, a[i+1], *this_goto_parse_action);
                    text_printf("\n");
#endif
                    reduction_stack_pointer->v = reduction_search_target_node;  /* u */
                    reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                    reduction_stack_pointer->y = z;

                    reduction_stack_pointer++;                                 /* Bump R pointer */
                  }
                }
              }
            }
          }
          else
          {
#if defined(RNGLR_RECOGNISER_TRACE)
            text_printf("new\n");
#endif
            /* Create a node w on current_frontier (in U_i) labelled l and an edge (w, u) */
            current_frontier[goto_action - first_dfa_state] = (ssg_node*) graph_insert_node(sizeof(ssg_node), this_derivation->ssg);
            current_frontier[goto_action - first_dfa_state]->value = goto_action;
            current_frontier[goto_action - first_dfa_state]->level = i;

            ssg_edge* new_ssg_edge = (ssg_edge*) graph_insert_edge(sizeof(ssg_edge), reduction_search_target_node, current_frontier[goto_action - first_dfa_state]);
            new_ssg_edge->symbol = original_reduction_table_entry->symbol_number;
            new_ssg_edge->sppf_node = z;

            if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_goto_parse_action))  /* If there is a shift ph in T(goto_state, a+1) */
            {
              shift_stack_pointer->v = current_frontier[goto_action - first_dfa_state];
              shift_stack_pointer->k = *this_goto_parse_action;
#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("On new goto state: Add to Q: ");
              sr_print_action(this_dfa, shift_stack_pointer->current_frontier_node->value, a[i+1], shift_stack_pointer->next_frontier_node_number);
              text_printf("\n");
#endif

              shift_stack_pointer++;                                     /* bump Q pointer */

              this_goto_parse_action++;                                       /* advance action pointer past the shift action */
            }

            /* Now we stack the zero length non-accepting reductions from w using w as the start of the path */
            for (; *this_goto_parse_action != 0; this_goto_parse_action++)         /* forall reductions in T(goto_state, a_i+1) */
            {
              /* Pick out and stack the zero length, non-accepting reductions */
              if (this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length == 0 && !this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)  /* Skip accepting reductions */
              {
                reduction_stack_pointer->v = current_frontier[goto_action - first_dfa_state];   /* w */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                reduction_stack_pointer->y = NULL;  /* epsilon in paper */

#if defined(RNGLR_RECOGNISER_TRACE)
                text_printf("On new goto state (zero length creating reduction): Add to R: ");
                sr_print_action(this_dfa, current_frontier[goto_action - first_dfa_state]->value, a[i+1], *this_goto_parse_action);
                text_printf("\n");
#endif
                reduction_stack_pointer++;                                 /* Bump R pointer */
              }
            }

            /* Now we do the non-zero length reductions IF w was created by a non-zero length reduction: we use u as the start point on the search path */
            if (original_reduction_table_entry->length != 0)
            {
              /* reset the action pointer to the first reduction in the block */
              this_goto_parse_action = these_goto_parse_actions;

              if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_goto_parse_action))  /* If there is a shift... */
                this_goto_parse_action++;                                              /* ...skip it */

              for (; *this_goto_parse_action != 0; this_goto_parse_action++)         /* forall reductions in T(goto_state, a_i+1) */
              {
                /* Pick out and stack the non-zero length, non-accepting reductions */
                if ((this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length != 0) && (!this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting))
                                {
#if defined(RNGLR_RECOGNISER_TRACE)
                  text_printf("On new goto state (nonzero length creating reduction): Add to R: ");
                  sr_print_action(this_dfa, current_frontier[goto_action - first_dfa_state]->value, a[i+1], *this_goto_parse_action);
                  text_printf("\n");
#endif
                  reduction_stack_pointer->v = reduction_search_target_node;       /* u */
                  reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                  reduction_stack_pointer->y = z;  /* epsilon in paper */

                  reduction_stack_pointer++;                                 /* Bump R pointer */
                }
              }
            }

          }

          edge_already_exists:

          /* ADD_CHILDREN */
          sppf_node *epsilon_sppf_node = this_derivation->epsilon_sppf_index[original_slot_number];

          if (original_reduction_table_entry->length  == 0)
          {
#if defined(RNGLR_RECOGNISER_TRACE)
             text_printf("Skipping ADD_CHILDREN on zero length reduction\n");
#endif
             goto next_reduction;
          }

#if defined(RNGLR_RECOGNISER_TRACE)
          text_printf("ADD_CHILDREN on SPPF atom %i labelled %i %i\n", graph_atom_number(z), z->x, z->j);
#endif

          void *first_out_edge;

          if ((first_out_edge  = graph_next_out_edge(z)) == NULL)      /* y (z) has no children */
          {
#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("Adding to childless SPPF node <%i> labelled (%i %i) reduction %i\n", graph_atom_number(z), z->x, z->j, original_reduction_number);
#endif

            if (epsilon_sppf_node != NULL)
            {
#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("linking to epsilon node\n");
#endif
              graph_insert_edge(0, epsilon_sppf_node, z);
             }
             else
             {
#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("no epsilon node to link\n");
#endif
             }

            graph_insert_edge(0, original_sppf_node, z);
            for (ssg_edge **lambda_element = reduction_search_stack; lambda_element < reduction_search_stack_pointer; lambda_element++)
              graph_insert_edge(0, (*lambda_element)->sppf_node, z);
          }
          else
          {
            if (cache_element->i != sppf_cache_base+sppf_cache_base_increment - 1)  /* not a pack node */
            {
              /* Check for family membership of this non packed node */
              void *this_out_edge = first_out_edge;

              family_checks++;

#if 1
#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("Checking unpacked SPPF node <%i> labelled (%i %i) and reduction %i for family membership\n", graph_atom_number(z), z->x, z->j, original_reduction_number);
#endif
              ssg_edge **lambda_element = reduction_search_stack_pointer - 1;

              if (cache_element->i % sppf_cache_base_increment != original_reduction_number)
                goto not_in_family_of_unpacked;

              while (lambda_element >= reduction_search_stack && this_out_edge != NULL)
              {
                if ((*lambda_element)->sppf_node != graph_destination(this_out_edge))
                  goto not_in_family_of_unpacked;

                lambda_element--;
                this_out_edge = graph_next_out_edge(this_out_edge);
              }

              if (this_out_edge == NULL || graph_destination(this_out_edge) != original_sppf_node)
                goto not_in_family_of_unpacked;

              goto next_reduction;       /* Success */

              not_in_family_of_unpacked:
#endif

#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("Reduction not found in unpacked family: adding packed node\n");
#endif
              /* insert a pack node */
              sppf_node *pack_node = (sppf_node*) graph_insert_node(sizeof(sppf_node), this_derivation->sppf);
              pack_node->j = cache_element->i % sppf_cache_base_increment;
              cache_element->i = sppf_cache_base + sppf_cache_base_increment - 1;

              do
              {
                graph_insert_edge_after_final(0, graph_destination(first_out_edge), pack_node);
                graph_delete_edge(first_out_edge);
              }
              while ((first_out_edge = graph_next_out_edge(z)) != NULL);

              graph_insert_edge(0, pack_node, z);
            }
            else
            {
              /* Check for family membership of this packed node */
              family_checks++;

#if defined(RNGLR_RECOGNISER_TRACE)
                text_printf("Checking packed SPPF node <%i> labelled (%i %i) and reduction %i for family membership\n", graph_atom_number(z), z->x, z->j, original_reduction_number);
#endif

              for (void *this_pack_edge = first_out_edge; this_pack_edge != NULL; this_pack_edge = graph_next_out_edge(this_pack_edge))
              {
                sppf_node *pack_node = (sppf_node*) graph_destination(this_pack_edge);

                if (pack_node->j  != original_reduction_number)
                  continue;

                void *this_out_edge = graph_next_out_edge(pack_node);

#if 1
#if defined(RNGLR_RECOGNISER_TRACE)
                text_printf("Checking pack node <%i> and reduction %i\n", graph_atom_number(pack_node), original_reduction_number);
#endif

                ssg_edge **lambda_element = reduction_search_stack_pointer - 1;

                while (lambda_element >= reduction_search_stack && this_out_edge != NULL)
                {

#if defined(RNGLR_RECOGNISER_TRACE)
                  text_printf("Checking child <%i> of pack node <%i> [%p] against lambda element [%p]\n",
                              graph_atom_number(graph_destination(this_out_edge)),
                              graph_atom_number(pack_node), original_reduction_number,
                              graph_destination(this_out_edge),
                              (*lambda_element)->sppf_node
                              );
#endif
                  if ((*lambda_element)->sppf_node != graph_destination(this_out_edge))
                    goto not_in_family_of_packed;

                  lambda_element--;
                  this_out_edge = graph_next_out_edge(this_out_edge);
                }

                if (this_out_edge == NULL || graph_destination(this_out_edge) != original_sppf_node)
                  goto not_in_family_of_packed;

#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("Reduction found in packed family: starting next reduction\n");
#endif
                goto next_reduction;

                not_in_family_of_packed:
                  ;
#endif
              }

#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("Reduction not found in packed family: adding new packed node\n");
#endif
            }

/* By now, either the children were pack nodes, or we have made them so */

            /* Create the pack node for this reduction, mark with its reduction number and link to z */
            family_checks_failed++;
            sppf_node *pack_node = (sppf_node*) graph_insert_node(sizeof(sppf_node), this_derivation->sppf);
            pack_node->j = original_reduction_number;
            graph_insert_edge(0, pack_node, z);

/* Note that we're doing head insertion, so we go in reverse order */
            /* if epsilon-sppf is non-null, insert it */
            if (epsilon_sppf_node != NULL)
              graph_insert_edge(0, epsilon_sppf_node, pack_node);

            /* Now link y_m (which will be y) */
            graph_insert_edge(0, original_sppf_node,pack_node);

            /* and now work backwards through the reduction search path lambda */
            for (ssg_edge **lambda_element = reduction_search_stack; lambda_element < reduction_search_stack_pointer; lambda_element++)
              graph_insert_edge(0, (*lambda_element)->sppf_node, pack_node);
          }

          next_reduction:
          reduction_search_generate_next_target();                  /* Pull the next u from Chi */
        }

#if defined(CHI_HIST)
        text_printf("Chi edges\n");
        hist_print(chi_edge_histogram);
        text_printf("Chi edges ");
        hist_report_differences(chi_edge_histogram, chi_prime_edge_histogram);
        text_printf("Chi nodes\n");
        hist_print(chi_node_histogram);
        text_printf("Chi nodes ");
        hist_report_differences(chi_node_histogram, chi_prime_node_histogram);

        hist_free(&chi_edge_histogram);
        hist_free(&chi_node_histogram);
        hist_free(&chi_prime_edge_histogram);
        hist_free(&chi_prime_node_histogram);
#endif

      }

      /* SHIFTER */
      thereis_a_shifted_node = false;
      if (i != d)
      {
        shift_stack_prime_pointer = shift_stack_prime;           /* Q' = { ] */

        sppf_node *z = (sppf_node*) graph_insert_node(sizeof(sppf_node), this_derivation->sppf);
        z->x = a[i+1];
        z->j = i;

        while (shift_stack_pointer != shift_stack)               /* While Q != { } */
        {
          /* The shifter handles two cases: that in which the node to shift to already exists, and that in which it doesn't
             In both cases we need to access the action in T(k, a_{i+2}) so we factor the table access out and perform it here for both cases */
          shift_stack_pointer--;
#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Shifter processing current frontier %i, next frontier %i, symbol %i\n",
                  shift_stack_pointer->current_frontier_node->value,
                  shift_stack_pointer->next_frontier_node_number,
                  a[i+1]);
#endif

          int *these_parse_actions = dfa_retrieve_all_actions(this_dfa, shift_stack_pointer->k, a[i+2]);  /* Get T(k, a_i+2) */
          int *this_parse_action = these_parse_actions;

          if (next_frontier[shift_stack_pointer->k - first_dfa_state] != NULL)  /* If there is a w in U_{i+1} with label k */
          {
            ssg_edge *new_ssg_edge = (ssg_edge*) graph_insert_edge(sizeof(ssg_edge), shift_stack_pointer->v, next_frontier[shift_stack_pointer->k  - first_dfa_state]);
            new_ssg_edge->symbol = a[i+1];
            new_ssg_edge->sppf_node = z;

            if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then skip it */
              this_parse_action++;

            for (; *this_parse_action != 0; this_parse_action++)   /* forall reductions in T(k, a_i+2) */
            {
              if (this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length != 0)
              {
#if defined(RNGLR_RECOGNISER_TRACE)
                text_printf("Shifter old node: Add to R: ");
                sr_print_action(this_dfa, shift_stack_pointer->next_frontier_node_number, a[i+2], *this_parse_action);
#endif
                reduction_stack_pointer->v = shift_stack_pointer->v;   /* We store the target of the first edge on the search path */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                reduction_stack_pointer->y = z;

                reduction_stack_pointer++;                                 /* Bump R pointer */
              }
            }
          }
          else
          {
            next_frontier[shift_stack_pointer->k - first_dfa_state] = (ssg_node*) graph_insert_node(sizeof(ssg_node), this_derivation->ssg);
            next_frontier[shift_stack_pointer->k - first_dfa_state]->value = shift_stack_pointer->k;
            next_frontier[shift_stack_pointer->k - first_dfa_state]->level = i + 1;
            thereis_a_shifted_node = true;

            ssg_edge *new_ssg_edge = (ssg_edge*) graph_insert_edge(sizeof(ssg_edge), shift_stack_pointer->v, next_frontier[shift_stack_pointer->k - first_dfa_state]);
            new_ssg_edge->symbol = a[i+1];
            new_ssg_edge->sppf_node = z;

            if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then add it into shift_stack_prime */
            {
              shift_stack_prime_pointer->v = next_frontier[shift_stack_pointer->k - first_dfa_state];
              shift_stack_prime_pointer->k = *this_parse_action;
#if defined(RNGLR_RECOGNISER_TRACE)
              text_printf("Add to Q': ");
              sr_print_action(this_dfa, shift_stack_prime_pointer->current_frontier_node->value, a[i+2], *this_parse_action);
#endif
              shift_stack_prime_pointer++;

              this_parse_action++;                                 /* and skip the shift action */
            }

            for (; *this_parse_action != 0; this_parse_action++)   /* forall reductions in T(k, a_i+2) */
            {
              if (this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length != 0)
              {
                reduction_stack_pointer->v = shift_stack_pointer->v;   /* We store the target of the first edge on the search path */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                reduction_stack_pointer->y = z;
#if defined(RNGLR_RECOGNISER_TRACE)
                text_printf("Shifter new node: Add to R: ");
                sr_print_action(this_dfa, reduction_stack_pointer->gss_node->value, a[i+2], *this_parse_action);
#endif
              }
              else
              {
                reduction_stack_pointer->v = next_frontier[shift_stack_pointer->k - first_dfa_state];   /* We store the start node on the search path for zero length reductions */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                reduction_stack_pointer->y = NULL;

#if defined(RNGLR_RECOGNISER_TRACE)
                text_printf("Shifter new node: Add to R: ");
                sr_print_action(this_dfa, reduction_stack_pointer->gss_node->value, a[i+2], *this_parse_action);
#endif
              }
              reduction_stack_pointer++;                                 /* Bump R pointer */
            }
          }
          mem_free(these_parse_actions);                           /* Reclaim memory for T(k, a_i+2) */
        }

        /* copy Q' to Q by exchanging pointers */
        shift_stack_exchanger = shift_stack;                       /* rotate stack bases through exchange */
        shift_stack = shift_stack_prime;
        shift_stack_prime = shift_stack_exchanger;

        shift_stack_pointer = shift_stack_prime_pointer;           /* set Q-stack-pointer to be Q'-stack-pointer... */
      }
    }
  }

  /* Stop the clock */
  finish_time = clock();

  /* Test for acceptance: scan all states on frontier and their reductions */
  for (int h = 0; h < this_dfa->state_count; h++)
  {
    if (current_frontier[h] != NULL)               /* If state i is on frontier */
    {
#if 0
      text_printf("Acceptance check: state %u is occupied\n", h + this_dfa->nfa->grammar->first_dfa_state);
#endif

      int *these_parse_actions = dfa_retrieve_all_actions(this_dfa, h + first_dfa_state, GRAM_EOS_SYMBOL_NUMBER);  /* Get T(h, $) */

      for (int *this_parse_action = these_parse_actions; *this_parse_action!= 0; this_parse_action++)
        if (GRAM_IS_SLOT(this_dfa->nfa->grammar, *this_parse_action))

          if (this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)
          {
#if 0
            text_printf("Acceptance check at level %u: in state %u found accepting reduction number %u with lookahead symbol %u\n",
            i,
            h + this_dfa->nfa->grammar->first_dfa_state,
            *this_parse_action - this_dfa->nfa->grammar->first_level_0_slot,
            a[i]
            );
#endif
            if (a[i] == GRAM_EOS_SYMBOL_NUMBER)
            {
              this_derivation->accept = 1;

              /* Walk all the in edges of this state to find the start reduction */
              for (ssg_edge* this_edge = (ssg_edge*) graph_next_out_edge(current_frontier[h]);
                   this_edge != NULL;
                   this_edge = (ssg_edge*) graph_next_out_edge(this_edge))
              {
                ssg_node* dest_node = (ssg_node*) graph_destination(this_edge);

                /* If this edge points at the DFA start state... */
                if (dest_node->level == 0)
                {
                  graph_set_root(this_derivation->sppf, this_edge->sppf_node);
                }
              }

              sr_ssg_statistics(stdout, this_derivation->ssg);
              if (graph_root(this_derivation->sppf) == NULL)
                text_message(TEXT_ERROR, "attempt to prune SPPF that has no recognised root node\n");
              else
              {
                sppf_statistics(this_derivation, "Before SPPF pruning");
                graph_retain_set_of_nodes(this_derivation->sppf, graph_reachable_nodes(this_derivation->sppf, graph_root(this_derivation->sppf)));
                sppf_statistics(this_derivation, "After SPPF pruning");
              }
              break; /* No need to check the rest of the states on the final frontier */
            }
          }

      mem_free(these_parse_actions);
    }
  }

  /* Cleanup: free memory */
  mem_free(current_frontier);
  mem_free(next_frontier);
  mem_free(reduction_stack);
  mem_free(shift_stack);
  mem_free(shift_stack_prime);
  mem_free(sppf_node_cache);
  reduction_search_generate_cleanup();
  mem_free(a);

  if (this_derivation->accept)
    text_message(TEXT_INFO, "RNGLR parse: accept\n");
  else
    text_message(TEXT_INFO, "RNGLR parse: reject after consuming %i out of %i lexemes\n", i - 1, d);

  text_printf("Total of %u family checks of which %u failed\n", family_checks, family_checks_failed);
  double run_time = (double) (finish_time - start_time) / (double) CLK_TCK;

  if (script_gtb_verbose() && run_time > 0.001)
  {
    text_message(TEXT_INFO, "Run time: %fs\n", run_time);

    text_message(TEXT_INFO, "Parse rate: %i lexemes at %.1f lexeme/s\n", d, (double) d / ((double) (finish_time - start_time) / (double) CLK_TCK));
  }

  text_printf("!!RNGLR, %f,%i\n", run_time, d);

  FILE *sppf_vcg_file = fopen("sppf.vcg", "w");
  text_redirect(sppf_vcg_file);
  sppf_set_render_derivation(this_derivation);
  graph_vcg(this_derivation->sppf, NULL, sppf_vcg_print_node, sppf_vcg_print_edge);
  fclose(sppf_vcg_file);
  text_redirect(stdout);

  return this_derivation;
}

