/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_rnglr_rec.cpp - an RNGLR recogniser, as per the TOPLAS paper
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <string.h>
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
#include "gtb_rnglr_rec.h"

//#define RNGLR_RECOGNISER_TRACE
//#define CHI_HIST

#if defined(CHI_HIST)
static histogram_node *chi_edge_histogram = NULL;
static histogram_node *chi_node_histogram = NULL;
static histogram_node *chi_prime_edge_histogram = NULL;
static histogram_node *chi_prime_node_histogram = NULL;
#endif

/* poor man's generator: use a stack to explore part of the SGG, returning an element of Chi on each iteration */
static struct reduction_search_stack_element{
  ssg_edge *edge;                     /* start node of side branch */
  int length;                         /* remaining length of search */
} *reduction_search_stack, *reduction_search_stack_pointer;

/* Global variable that holds the next member of Chi */
static ssg_node* reduction_search_target_node;

/* Initialise the search stack */
static void reduction_search_generate_init(dfa* this_dfa)
{
  reduction_search_stack = (reduction_search_stack_element*) mem_calloc(this_dfa->nfa->grammar->maximum_rule_length, sizeof (reduction_search_stack_element));
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

#if defined(RNGLR_RECOGNISER_TRACE)
  text_printf("Search generate start: node (%i,L%i) length %i\n", this_node->value, this_node->level, length);
#endif

#if defined(CHI_HIST)
  hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
#endif

  for (; length > 0; length--)
  {
    ssg_edge * this_edge = (ssg_edge*) graph_next_out_edge(reduction_search_target_node);
#if defined(CHI_HIST)
    hist_update(chi_edge_histogram, graph_atom_number(this_edge));
#endif

#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate start: traversing edge %u to state (%u,L%i)\n",
                  graph_atom_number(this_edge),
                  ((ssg_node*) graph_destination(this_edge))->value,
                  ((ssg_node*) graph_destination(this_edge))->level,
                  reduction_search_stack_pointer->length);
#endif

    if ((reduction_search_stack_pointer->edge = (ssg_edge*) graph_next_out_edge(this_edge)) != NULL)
    {
      reduction_search_stack_pointer->length = length - 1;
#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate start: stacked in-edge to state (%u,L%i), length %i\n",
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->value,
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->level,
                  reduction_search_stack_pointer->length);
#endif
      reduction_search_stack_pointer++;
    }

    reduction_search_target_node = (ssg_node*) graph_destination(this_edge);
#if defined(CHI_HIST)
    hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
#endif
  }
#if defined(RNGLR_RECOGNISER_TRACE)
  text_printf("Search generate start: target is (%i,L%i)\n", reduction_search_target_node->value, reduction_search_target_node->level);
#endif
}

static void reduction_search_generate_next_target(void)
{
#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next: stack base %p, stack pointer %p, offset %i\n",
                  reduction_search_stack, reduction_search_stack_pointer, reduction_search_stack_pointer - reduction_search_stack
                 );
#endif
  if (reduction_search_stack_pointer <= reduction_search_stack)
    reduction_search_target_node = NULL;
  else
  {
    /* unstack the entry */
    reduction_search_stack_pointer--;

    ssg_edge* unstacked_edge = reduction_search_stack_pointer->edge;
#if defined(CHI_HIST)
    hist_update(chi_edge_histogram, graph_atom_number(reduction_search_stack_pointer->edge));
#endif
    reduction_search_target_node = (ssg_node*) graph_destination(unstacked_edge);
#if defined(CHI_HIST)
    hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
#endif

    unsigned length = reduction_search_stack_pointer->length;

#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next: unstacked in-edge %u to state (%u,L%i), length %i\n",
                  graph_atom_number(reduction_search_stack_pointer->edge),
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->value,
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->level,
                  reduction_search_stack_pointer->length);
#endif

    /* Crow bar: stack any further edges that are siblings of the edge we just popped */
      if ((reduction_search_stack_pointer->edge = (ssg_edge*) graph_next_out_edge(unstacked_edge)) != NULL)
      {
        reduction_search_stack_pointer->length = length;
#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next crowbar: stacked in-edge to state (%u,L%i), length %i\n",
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->value,
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->level,
                  reduction_search_stack_pointer->length);
#endif
        reduction_search_stack_pointer++;
      }

    /* Now walk down the leading path, stacking siblings just as we did for the _start function */
    for (; length > 0; length--)
    {
      ssg_edge * this_edge = (ssg_edge*) graph_next_out_edge(reduction_search_target_node);
#if defined(CHI_HIST)
      hist_update(chi_edge_histogram, graph_atom_number(this_edge));
#endif

#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next: traversing edge %u to state (%u,L%i)\n",
                  graph_atom_number(this_edge),
                  ((ssg_node*) graph_destination(this_edge))->value,
                  ((ssg_node*) graph_destination(this_edge))->level,
                  reduction_search_stack_pointer->length);
#endif

      if ((reduction_search_stack_pointer->edge = (ssg_edge*) graph_next_out_edge(this_edge)) != NULL)
      {
        reduction_search_stack_pointer->length = length - 1;
#if defined(RNGLR_RECOGNISER_TRACE)
      text_printf("Search generate next: stacked in-edge to state (%u,L%i), length %i\n",
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->value,
                  ((ssg_node*) graph_destination(reduction_search_stack_pointer->edge))->level,
                  reduction_search_stack_pointer->length);
#endif
        reduction_search_stack_pointer++;
      }

      reduction_search_target_node = (ssg_node*) graph_destination(this_edge);
#if defined(CHI_HIST)
      hist_update(chi_node_histogram, graph_atom_number(reduction_search_target_node));
#endif
    }
  }
#if defined(RNGLR_RECOGNISER_TRACE)
  if (reduction_search_target_node == NULL)
    text_printf("Search generate next: target is NULL\n");
  else
    text_printf("Search generate next: target is %i\n", reduction_search_target_node->value);
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

derivation *sr_rnglr_recognise(dfa * this_dfa, char *string, int reduction_stack_size)
{
  struct reduction_stack_element{
    ssg_node *gss_node;                 /* u in paper */
    reduction* reduction_table_entry;   /* indirectly, X and m in paper */
  } *reduction_stack, *reduction_stack_pointer;

  struct shift_stack_element {
    ssg_node *current_frontier_node;    /* v in paper */
    int next_frontier_node_number;      /* label[w] in paper */
  } *shift_stack, *shift_stack_prime, *shift_stack_pointer, *shift_stack_prime_pointer, *shift_stack_exchanger;

  derivation *this_derivation;          /* derivation structure to hold output of parse */
  ssg_node **current_frontier;          /* U_k the current frontier */
  ssg_node **next_frontier;             /* U_k+1 the next frontier created via shoft actions */
  ssg_node **frontier_exchanger;        /* dummy frontier pointer used when swapping over frontiers */

  /* Initialisation step 1: sign on */
  text_printf("\n");
  text_message(TEXT_INFO, "RNGLR recognise: \'%s\'\n", string);

  /* Initialisation step 2: create derivation structure and SSG structure */
  this_derivation = (derivation *) mem_calloc(1, sizeof(derivation));
  this_derivation->dfa = this_dfa;
  this_derivation->ssg = graph_insert_graph("ssg trace");

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

  d--;    /* don't count the end-of-string symbol */

  text_message(TEXT_INFO, "RNGLR recognise: input length %i character%s, %i lexeme%s\n", strlen(string), strlen(string) == 1 ? "" :"s", d, d == 1 ? "" : "s");

  int *a = (int*) mem_malloc((d+2) * sizeof(unsigned));  /* we don't use a[0], and we need an a[d+1] for $, hence length is d+2 */

  lex_init(string, this_dfa->nfa->grammar);

  int i;
  for (i = 1; i <= d; i++)
    a[i] = lex_lex();


  /* Main algorithm, as per pages 20-21 of the TOPLAS paper */
  /* Test against 1 here, not zero, because our d is one plus the d in the paper */
  if (d == 0)  /* On empty string, see is acc is a member of T(0,$) by checking all reductions and setting this_derivation->accept */
  {
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
      shift_stack_pointer->current_frontier_node = v0;
      shift_stack_pointer->next_frontier_node_number = *this_parse_action;
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
        reduction_stack_pointer->gss_node = v0;
        reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];

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

      /* REDUCER */
      while (reduction_stack_pointer != reduction_stack)
      {
        reduction_stack_pointer--;

        ssg_node *original_reduction_node = reduction_stack_pointer->gss_node;
        reduction *original_reduction_table_entry = reduction_stack_pointer->reduction_table_entry;

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

           In the paper, the current member of Chi is called u, here we call it reduction_search_target
        */
        while (reduction_search_target_node != NULL)
        {
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
            ((ssg_edge*) graph_insert_edge(sizeof(ssg_edge), reduction_search_target_node, current_frontier[goto_action - first_dfa_state]))->symbol = original_reduction_table_entry->symbol_number;   /* create an edge from w to u */

            /* Now we must start stacking the reductions from w */
            if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_goto_parse_action))  /* Skip any shift which will be first in sequence of parse actions */
              this_goto_parse_action++;

            if (original_reduction_table_entry->length != 0)         /* If reduction-edge was created by a non-zero length reduction */
            {
              for (; *this_goto_parse_action != 0; this_goto_parse_action++)         /* forall reductions in T(goto_state, a_1) */
              {
                if (!this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)   /* skip accepting reductions */
                {
                  /* Add (u, B, t) to R */
                  if (this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length != 0)
                  {
#if defined(RNGLR_RECOGNISER_TRACE)
                    text_printf("On old goto state: Add to R: ");
                    sr_print_action(this_dfa, current_frontier[goto_action - first_dfa_state]->value, a[i+1], *this_goto_parse_action);
                    text_printf("\n");
#endif
                    reduction_stack_pointer->gss_node = reduction_search_target_node;  /* u */
                    reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot];

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

            ((ssg_edge*) graph_insert_edge(sizeof(ssg_edge), reduction_search_target_node, current_frontier[goto_action - first_dfa_state]))->symbol = original_reduction_table_entry->symbol_number;

            if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_goto_parse_action))  /* If there is a shift ph in T(goto_state, a+1) */
            {
              shift_stack_pointer->current_frontier_node = current_frontier[goto_action - first_dfa_state];
              shift_stack_pointer->next_frontier_node_number = *this_goto_parse_action;
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
                reduction_stack_pointer->gss_node = current_frontier[goto_action - first_dfa_state];   /* w */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot];

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
                  reduction_stack_pointer->gss_node = reduction_search_target_node;       /* u */
                  reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot];

                  reduction_stack_pointer++;                                 /* Bump R pointer */
                }
              }
            }

          }


          edge_already_exists:
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

          int *these_parse_actions = dfa_retrieve_all_actions(this_dfa, shift_stack_pointer->next_frontier_node_number, a[i+2]);  /* Get T(k, a_i+2) */
          int *this_parse_action = these_parse_actions;

          if (next_frontier[shift_stack_pointer->next_frontier_node_number - first_dfa_state] != NULL)  /* If there is a w in U_{i+1} with label k */
          {
            ((ssg_edge*) graph_insert_edge(sizeof(ssg_edge), shift_stack_pointer->current_frontier_node, next_frontier[shift_stack_pointer->next_frontier_node_number  - first_dfa_state]))->symbol = a[i+1];

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
                reduction_stack_pointer->gss_node = shift_stack_pointer->current_frontier_node;   /* We store the target of the first edge on the search path */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];

                reduction_stack_pointer++;                                 /* Bump R pointer */
              }
            }
          }
          else
          {
            next_frontier[shift_stack_pointer->next_frontier_node_number - first_dfa_state] = (ssg_node*) graph_insert_node(sizeof(ssg_node), this_derivation->ssg);
            next_frontier[shift_stack_pointer->next_frontier_node_number - first_dfa_state]->value = shift_stack_pointer->next_frontier_node_number;
            next_frontier[shift_stack_pointer->next_frontier_node_number - first_dfa_state]->level = i + 1;
            thereis_a_shifted_node = true;

            ((ssg_edge*) graph_insert_edge(sizeof(ssg_edge), shift_stack_pointer->current_frontier_node, next_frontier[shift_stack_pointer->next_frontier_node_number - first_dfa_state]))->symbol = a[i+1];

            if (GRAM_IS_DFA_STATE(this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then add it into shift_stack_prime */
            {
              shift_stack_prime_pointer->current_frontier_node = next_frontier[shift_stack_pointer->next_frontier_node_number - first_dfa_state];
              shift_stack_prime_pointer->next_frontier_node_number = *this_parse_action;
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
                reduction_stack_pointer->gss_node = shift_stack_pointer->current_frontier_node;   /* We store the target of the first edge on the search path */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
#if defined(RNGLR_RECOGNISER_TRACE)
                text_printf("Shifter new node: Add to R: ");
                sr_print_action(this_dfa, reduction_stack_pointer->gss_node->value, a[i+2], *this_parse_action);
#endif
              }
              else
              {
                reduction_stack_pointer->gss_node = next_frontier[shift_stack_pointer->next_frontier_node_number - first_dfa_state];   /* We store the start node on the search path for zero length reductions */
                reduction_stack_pointer->reduction_table_entry = &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];

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

  /* Test for acceptance: scan all states on frontier and their reductions */
  for (int h = 0; h < this_dfa->state_count; h++)
  {
    if (current_frontier[h] != NULL)               /* If state h is on frontier */
    {
#if 1
      text_printf("Acceptance check: state %u is occupied\n", h + this_dfa->nfa->grammar->first_dfa_state);
#endif
      int *these_parse_actions = dfa_retrieve_all_actions(this_dfa, h + first_dfa_state, GRAM_EOS_SYMBOL_NUMBER);  /* Get T(h, $) */

      for (int *this_parse_action = these_parse_actions; *this_parse_action!= 0; this_parse_action++)
        if (GRAM_IS_SLOT(this_dfa->nfa->grammar, *this_parse_action))
          if (this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)
          {
#if 1
            text_printf("Acceptance check at level %u: in state %u found accepting reduction number %u with lookahead symbol %u\n",
            i,
            h + this_dfa->nfa->grammar->first_dfa_state,
            *this_parse_action - this_dfa->nfa->grammar->first_level_0_slot,
            a[i]
            );
#endif
            if (a[i] == GRAM_EOS_SYMBOL_NUMBER)
              this_derivation->accept = 1;
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
  reduction_search_generate_cleanup();
  mem_free(a);

  if (this_derivation->accept)
    text_message(TEXT_INFO, "RNGLR recognise: accept\n");
  else
    text_message(TEXT_INFO, "RNGLR recognise: reject after consuming %i out of %i lexemes\n", i - 1, d);

  return this_derivation;
}



