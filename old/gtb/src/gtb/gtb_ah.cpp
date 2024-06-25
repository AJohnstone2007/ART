/*******************************************************************************
*
* GTB release 2.5 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 25 September 2002
*
* gtb_gram.c - grammar functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
//#define AH_AUTOMATON_TRACE
//#define AH_BIN_FILE_TRACE
#define AH_VCG_LIMIT 1000
//#define AH_RECOGNISER_TRACE
//#define LEFT_CONTEXT_GRAMMAR_TRACE 1

#include <time.h>
#include "set.h"
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
#include "gtb_lex.h"
#include "gtb_sr.h"
#include "gtb_gp.h"
#include "gtb_ah.h"
#include "trie.h"
#include "stdlib.h"

#define LC_SUFFIX "!left_context"
#define TERMINAL_SUFFIX "!terminal"


void ah_table_write(FILE *output_file, ah_table *this_table)
{
  fprintf(output_file, "AH table write not implemented: see dump_table() in gtb_ah.cpp\n");
}

static histogram_node *pushes;
static histogram_node *pops;
static histogram_node *contingent_pops;
static histogram_node *shifts;
static histogram_node *reductions;
static histogram_node* push_histogram;

static int old_state_count;
static int old_nontilded_state_count;
static int old_tilded_state_count;
static int old_terminal_edge_count;
static int old_nonterminal_edge_count;
static int old_reduction_edge_count;

int ah_collect_tildes_and_start(set_ *tilde_set, grammar *this_gram, int suppress_warnings)
{
  int ret_value = 0;

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->rule_tree != NULL)
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
        for (gram_edge *this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          if (this_element_edge->tilde)
          {
            // Only collect the nonterminal versions!
            char *stripped_id = gram_strip_suffix(this_element_edge->symbol_table_entry->id, GRAM_TILDE_SUFFIX);

            gram_symbols_data *this_tilded_nonterminal_symbol = gram_find_symbol_by_id(this_gram, stripped_id, SCRIPT_NS_NONTERMINAL);

            if (set_includes_element(&this_gram->reachable, this_tilded_nonterminal_symbol->symbol_number))
              set_unite_element(tilde_set, this_tilded_nonterminal_symbol->symbol_number);
            else
              if (!suppress_warnings)
                text_printf("Warning: AH trie not built for unreachable tilded nonterminal %s\n", this_tilded_nonterminal_symbol->id);

            mem_free(stripped_id);
          }

  if (set_includes_element(tilde_set, this_gram->start_rule->symbol_number))
    ret_value = 1;

  set_unite_element(tilde_set, this_gram->start_rule->symbol_number);

  return ret_value;
}

grammar *ahz_left_context_grammar(grammar *input_gram)
{
  grammar *this_gram = (grammar *) mem_calloc(1, sizeof(grammar));
  gram_symbols_data * epsilon_symbol;

  this_gram->symbol_table = symbol_new_table("gram symbols", 101, 31, symbol_compare_integer_string, symbol_hash_integer_string, symbol_print_integer_string);
  gram_find_symbol_by_id(this_gram, GRAM_ILLEGAL_SYMBOL_STRING, SCRIPT_NS_SPECIAL)->symbol_number = GRAM_ILLEGAL_SYMBOL_NUMBER;
  (epsilon_symbol = gram_find_symbol_by_id(this_gram, GRAM_EPSILON_SYMBOL_STRING, SCRIPT_NS_SPECIAL))->symbol_number = GRAM_EPSILON_SYMBOL_NUMBER;
  gram_find_symbol_by_id(this_gram, GRAM_EOS_SYMBOL_STRING, SCRIPT_NS_SPECIAL)->symbol_number = GRAM_EOS_SYMBOL_NUMBER;

  set_unite_element(&(gram_find_symbol_by_id(this_gram, GRAM_EPSILON_SYMBOL_STRING, SCRIPT_NS_SPECIAL)->first), GRAM_EPSILON_SYMBOL_NUMBER);
  this_gram->rules = graph_insert_graph("");

  //Create nonterminals for all the tilded nonterminals in the original grammar
  set_ tilded_nonterminals_set = SET_NULL;
  ah_collect_tildes_and_start(&tilded_nonterminals_set, input_gram, 1);
  unsigned *tilded_nonterminals = set_array(&tilded_nonterminals_set);

  for (unsigned *this_tilded_nonterminal = tilded_nonterminals; *this_tilded_nonterminal != SET_END; this_tilded_nonterminal++)
  {
    gram_symbols_data *this_trie_symbol = gram_find_symbol_by_number(input_gram, *this_tilded_nonterminal);

    char *this_lc_symbol_id = (char*) mem_calloc(1, strlen(this_trie_symbol->id) + strlen(LC_SUFFIX) + 1);
    strcat(this_lc_symbol_id, this_trie_symbol->id);
    strcat(this_lc_symbol_id, LC_SUFFIX);
    gram_find_symbol_by_id(this_gram, this_lc_symbol_id, SCRIPT_NS_NONTERMINAL);
  }

  for (gram_symbols_data *this_input_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(input_gram->symbol_table));
       this_input_symbol != NULL;
       this_input_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_input_symbol))
  {
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
    text_printf("Left context grammar: input symbol %s\n", this_input_symbol->id);
#endif
    if (this_input_symbol->rule_tree != NULL && set_cardinality(&this_input_symbol->follow) != 0)
    {
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
    text_printf("Left context grammar: scanning %s\n", this_input_symbol->id);
#endif

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_input_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        // Check for cycle in left context grammar
        if (graph_next_out_edge(graph_destination(this_production_edge)) == NULL)
        {
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
    text_printf("Left context grammar: found epsilon rule\n", this_input_symbol->id);
#endif
          continue;  // This is an epsilon rule
        }

        int element_counter = 0;

        for (gram_edge *this_element_edge = this_production_edge;   // hunt forwards
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)), element_counter++)
          if (this_element_edge->symbol_table_entry->rule_tree != NULL)
          {
            if (element_counter == 1 && this_input_symbol == this_element_edge->symbol_table_entry)
    #if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
                text_printf("Left context grammar: suppressing left context cycle on %s\n", this_input_symbol->id)
    #endif
              ;
            else
            {
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
              text_printf("Left context grammar: found instance of %s\n", this_element_edge->symbol_table_entry->id);
#endif
              // Make new symbols
              char *new_rhs_id = (char*) mem_calloc(1, strlen(this_element_edge->symbol_table_entry->id) + strlen(LC_SUFFIX) + 1);
              new_rhs_id = strcat(new_rhs_id, this_element_edge->symbol_table_entry->id);
              new_rhs_id = strcat(new_rhs_id, LC_SUFFIX);
              gram_symbols_data* this_rhs_symbol = gram_find_symbol_by_id(this_gram, new_rhs_id, SCRIPT_NS_NONTERMINAL);
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
              text_printf("Left context grammar: rhs symbol %s\n", new_rhs_id);
#endif
              mem_free(new_rhs_id);

              if (this_rhs_symbol->rule_tree == NULL)
              {
                this_rhs_symbol->rule_tree = (gram_node *) graph_insert_node(sizeof(gram_node), this_gram->rules);
                this_rhs_symbol->rule_tree->symbol_table_entry = this_rhs_symbol;
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
              text_printf("Left context grammar: new symbol\n");
#endif
              }

              char *new_lhs_id = (char*) mem_calloc(1, strlen(this_input_symbol->id) + strlen(LC_SUFFIX) + 1);
              new_lhs_id = strcat(new_lhs_id, this_input_symbol->id);
              new_lhs_id = strcat(new_lhs_id, LC_SUFFIX);
              gram_symbols_data* this_lhs_symbol = gram_find_symbol_by_id(this_gram, new_lhs_id, SCRIPT_NS_NONTERMINAL);
              this_gram->start_rule = this_rhs_symbol;
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
              text_printf("Left context grammar: lhs symbol %s\n", new_lhs_id);
#endif
              mem_free(new_lhs_id);

              //Now build the rules tree
              gram_node *parent = gram_insert_node(this_rhs_symbol->rule_tree, epsilon_symbol, 0, 0);
              parent = gram_insert_node(parent, this_lhs_symbol, 0, 0);


              for (gram_edge *this_prefix_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));   // hunt forwards
                   this_prefix_element_edge != this_element_edge;
                   this_prefix_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_prefix_element_edge)))
              {
#if defined(LEFT_CONTEXT_GRAMMAR_TRACE)
      text_printf("Left context grammar: prefix %s\n", this_prefix_element_edge->symbol_table_entry->id);
#endif

                if (this_prefix_element_edge->symbol_table_entry->rule_tree != NULL)
                {
                  char *new_terminal_id;

                  new_terminal_id = (char*) mem_calloc(1, strlen(this_prefix_element_edge->symbol_table_entry->id) + strlen(TERMINAL_SUFFIX) + 1);
                  new_terminal_id = strcat(new_terminal_id, this_prefix_element_edge->symbol_table_entry->id);
                  new_terminal_id = strcat(new_terminal_id, TERMINAL_SUFFIX);

                  parent = gram_insert_node(parent, gram_find_symbol_by_id(this_gram, new_terminal_id, SCRIPT_NS_TERMINAL), 0, 0);
                  mem_free(new_terminal_id);

                }
                else
                  parent = gram_insert_node(parent, gram_find_symbol_by_id(this_gram, this_prefix_element_edge->symbol_table_entry->id, SCRIPT_NS_TERMINAL), 0, 0);

              }
            }
          }
      }
    }
  }
  gram_tidy(this_gram, 0, 1);

  return this_gram;
}

static int vcg_suppressed = 0;

struct ah_generate_stack {gram_symbols_data *symbol; int production; int level; gram_edge* continuation;} *ah_stack, *ah_stack_p;
static trie_node *ah_trie_root;
static grammar *ah_left_context_grammar;
static grammar *ah_tilded_grammar;
static unsigned long ah_reduction_edge_count;

set_ ah_stack_set = SET_NULL;

void process_gamma(trie_node *running_root, gram_symbols_data* this_symbol, grammar *tilded_grammar)
{
  {
#if defined (AH_AUTOMATON_TRACE)
    text_printf("Folding in rules from tilded grammar\n");
#endif
    // Now fold in the right hand sides of the tilded grammar
    trie_node *gamma_root = running_root;
    char *primary_rule_id = gram_strip_suffix(this_symbol->id, LC_SUFFIX);
    gram_symbols_data *this_primary_symbol = gram_find_symbol_by_id(tilded_grammar, primary_rule_id, SCRIPT_NS_NONTERMINAL);

#if defined (AH_AUTOMATON_TRACE)
    text_printf("Trie rule start for primary %s\n", this_primary_symbol->id);
#endif
    //Now walk the productions appending symbols to the trie
    for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_primary_symbol->rule_tree);
         this_production_edge != NULL;
         this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
    {
      int production_length = 0;

      running_root = gamma_root;

#if defined (AH_AUTOMATON_TRACE)
      text_printf("Trie production start\n");
#endif
      unsigned reduction_item_number = graph_atom_number(graph_destination(this_production_edge));
      for (gram_edge *this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));
           this_element_edge != NULL;
           this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
      {
        running_root = trie_insert_unsigned(running_root, this_element_edge->symbol_table_entry->symbol_number, this_element_edge->symbol_table_entry->is_tilded, this_element_edge->symbol_table_entry->rule_tree == NULL);

#if defined (AH_AUTOMATON_TRACE)
      text_printf("!!!1 Added %s to trie: root node now %i\n", this_element_edge->symbol_table_entry->id, graph_atom_number(running_root));
      text_printf("%lu state%s, %lu normal state%s, %lu tilded state%s, %lu terminal_edge%s, %lu nonterminal edge%s, %lu tilded edge%s and %lu reduction edge%s\n",
                  trie_state_count - old_state_count, trie_state_count - old_state_count == 1 ? "" : "s",
                  trie_nontilded_state_count - old_nontilded_state_count, trie_nontilded_state_count - old_nontilded_state_count  == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count - old_tilded_state_count == 1 ? "" : "s",
                  trie_terminal_edge_count - old_terminal_edge_count, trie_terminal_edge_count -old_terminal_edge_count == 1 ? "" : "s",
                  trie_nonterminal_edge_count - old_nonterminal_edge_count , trie_nonterminal_edge_count - old_nonterminal_edge_count == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count -old_tilded_state_count == 1 ? "" : "s",
                  ah_reduction_edge_count - old_reduction_edge_count, ah_reduction_edge_count -old_reduction_edge_count == 1 ? "" : "s");
#endif

        reduction_item_number = graph_atom_number(graph_destination(this_element_edge));
        production_length++;
      }
#if defined (AH_AUTOMATON_TRACE)
      text_printf("Production length is %u\n", production_length);
#endif

      //Now add reduction edge
      trie_node *reduction_target = running_root;

      while (production_length-- != 0)
      {
        trie_edge* primary_in_edge = (trie_edge*) graph_next_in_edge(reduction_target);

        if (script_gtb_verbose())
          printf("**Profile: state %i <- %s\n", graph_atom_number(reduction_target), this_primary_symbol->id);

//        set_unite_element(&state_covers, this_primary_symbol->id);   // Add cover symbol to cover set

        while (primary_in_edge->is_reduction)
          primary_in_edge = (trie_edge*) graph_next_in_edge(primary_in_edge);

        reduction_target = (trie_node*) graph_source(primary_in_edge);

      }
      reduction_target = trie_insert_unsigned(reduction_target, this_primary_symbol->symbol_number, 0, 1);
#if defined (AH_AUTOMATON_TRACE)
      text_printf("!!!2 Added reduction_target for %s to trie: root node now %i\n", this_primary_symbol->id, graph_atom_number(reduction_target));
      text_printf("%lu state%s, %lu normal state%s, %lu tilded state%s, %lu terminal_edge%s, %lu nonterminal edge%s, %lu tilded edge%s and %lu reduction edge%s\n",
                  trie_state_count - old_state_count, trie_state_count - old_state_count == 1 ? "" : "s",
                  trie_nontilded_state_count - old_nontilded_state_count, trie_nontilded_state_count - old_nontilded_state_count  == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count - old_tilded_state_count == 1 ? "" : "s",
                  trie_terminal_edge_count - old_terminal_edge_count, trie_terminal_edge_count -old_terminal_edge_count == 1 ? "" : "s",
                  trie_nonterminal_edge_count - old_nonterminal_edge_count , trie_nonterminal_edge_count - old_nonterminal_edge_count == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count -old_tilded_state_count == 1 ? "" : "s",
                  ah_reduction_edge_count - old_reduction_edge_count, ah_reduction_edge_count -old_reduction_edge_count == 1 ? "" : "s");
#endif

      // Now walk our out edges to see if we already have this reduction
      int reduction_already_done = 0;

      for (trie_edge *this_trie_edge = (trie_edge*) graph_next_out_edge(running_root); this_trie_edge != NULL; this_trie_edge = (trie_edge*) graph_next_out_edge(this_trie_edge))
        if (this_trie_edge->is_reduction && graph_destination(this_trie_edge) == reduction_target)
        {
          reduction_already_done = 1;
          break;
        }

      if (!reduction_already_done)
      {
#if defined (AH_AUTOMATON_TRACE)
      text_printf("Adding reduction from node %i to node %i\n", graph_atom_number(running_root), graph_atom_number(reduction_target));
#endif
        trie_edge *reduction_edge = (trie_edge*) graph_insert_edge(sizeof(trie_edge), reduction_target, running_root);
        reduction_edge->is_reduction = 1;
        ah_reduction_edge_count++;
        reduction_edge->element = reduction_item_number;
      }
    }
  }
}

int stack_size;

// return 1 if a string is added to the trie
int ah_generate(gram_symbols_data *symbol, int production, int level)
{
#if defined (AH_AUTOMATON_TRACE)
  text_printf("\n**ah_gen: start of level %i call on %s production %i, stack pointer %i\n", level, symbol->id, production, ah_stack_p - ah_stack);
#endif

  if (set_includes_element(&ah_stack_set, symbol->symbol_number))
  {
#if defined (AH_AUTOMATON_TRACE)
  text_printf("\n**ah_gen: symbol %s is already stacked: returning 0 without changing trie\n", symbol->id);
#endif
    return 0;
  }

  // Step 0: load the explicit stack with our paramaters
  ah_stack_p++;
  if ((ah_stack_p - ah_stack) > stack_size)
  {
    text_redirect(stdout);

    text_printf("AH stack overflow");


    while(--ah_stack_p > ah_stack)
    {
      text_printf("%i %s\n", ah_stack_p - ah_stack, ah_stack_p->symbol->id);
    }

    text_message(TEXT_FATAL, "Exiting after stack overflow");
  }
  ah_stack_p->symbol = symbol;
  ah_stack_p->production = production;
  ah_stack_p->level = level;
  set_unite_element(&ah_stack_set, symbol->symbol_number);

  gram_edge* this_production_edge;

  // Step 1: see if we have a production with the required number
  for (this_production_edge = (gram_edge*) graph_next_out_edge(symbol->rule_tree); production-- > 1; this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
    ;

  if (this_production_edge == NULL)
  {
#if defined (AH_AUTOMATON_TRACE)
    text_printf("\n**ah_gen: no such production - returning 0 without touching trie\n");
#endif
    set_difference_element(&ah_stack_set, symbol->symbol_number);
    ah_stack_p--; return 0;
  }

  // Step 2: see if this is an epsilon production, in which case add the continuations to the trie and return 1
  if (graph_next_out_edge(graph_destination(this_production_edge)) == NULL)
  {
#if defined (AH_AUTOMATON_TRACE)
    text_printf("\n**ah_gen: epsilon production - adding continuations to the trie and returning 1\n");
#endif

    // Work our way back through the stack adding continuations to the trie
    trie_node *running_root = ah_trie_root;
    ah_generate_stack *temp_p = ah_stack_p - 1;  //Skip the current stack fram which must be empty

    while (temp_p > ah_stack)
    {
#if defined (AH_AUTOMATON_TRACE)
      text_printf("\n**ah_gen: temp_p = %i; symbol %s, production %i, level %i\n", temp_p - ah_stack, temp_p->symbol->id, temp_p->production, temp_p->level);
#endif

      int continuation_production_number = temp_p->production;
      gram_edge *this_continuation_production_edge;

      for (this_continuation_production_edge = (gram_edge*) graph_next_out_edge(temp_p->symbol->rule_tree);
           continuation_production_number-- > 1;
           this_continuation_production_edge = (gram_edge*) graph_next_out_edge(this_continuation_production_edge))
        ;

      gram_edge* first_element = (gram_edge*) graph_next_out_edge(graph_destination(this_continuation_production_edge));

      for (gram_edge* this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(first_element));
           this_element_edge != NULL;
           this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
      {
        if (this_element_edge->symbol_table_entry->symbol_number >= ah_left_context_grammar->first_nonterminal)  // Is nonterminal (which can't happen!)
          text_message(TEXT_FATAL, "non-first symbol '%s' in left context grammar production for %s is a nonterminal\n", this_element_edge->symbol_table_entry->id, temp_p->symbol);

        char *primary_symbol_id = gram_strip_suffix(this_element_edge->symbol_table_entry->id, TERMINAL_SUFFIX);
        gram_symbols_data *primary_symbol = gram_find_symbol_by_id(ah_tilded_grammar, primary_symbol_id, strcmp(this_element_edge->symbol_table_entry->id, primary_symbol_id) == 0 ? SCRIPT_NS_TERMINAL : SCRIPT_NS_NONTERMINAL);
        running_root = trie_insert_unsigned(running_root, primary_symbol->symbol_number, primary_symbol->is_tilded, primary_symbol->rule_tree == NULL);
  #if defined (AH_AUTOMATON_TRACE)
        text_printf("\n!!!3 Added '%s' to trie: running root is %i\n", this_element_edge->symbol_table_entry->id, graph_atom_number(running_root));
        text_printf("%lu state%s, %lu normal state%s, %lu tilded state%s, %lu terminal_edge%s, %lu nonterminal edge%s, %lu tilded edge%s and %lu reduction edge%s\n",
                    trie_state_count - old_state_count, trie_state_count - old_state_count == 1 ? "" : "s",
                    trie_nontilded_state_count - old_nontilded_state_count, trie_nontilded_state_count - old_nontilded_state_count  == 1 ? "" : "s",
                    trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count - old_tilded_state_count == 1 ? "" : "s",
                    trie_terminal_edge_count - old_terminal_edge_count, trie_terminal_edge_count -old_terminal_edge_count == 1 ? "" : "s",
                    trie_nonterminal_edge_count - old_nonterminal_edge_count , trie_nonterminal_edge_count - old_nonterminal_edge_count == 1 ? "" : "s",
                    trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count -old_tilded_state_count == 1 ? "" : "s",
                    ah_reduction_edge_count - old_reduction_edge_count, ah_reduction_edge_count -old_reduction_edge_count == 1 ? "" : "s");
  #endif
        mem_free(primary_symbol_id);
      }
      temp_p--;
    }

    process_gamma(running_root, ah_stack[1].symbol, ah_tilded_grammar);
    set_difference_element(&ah_stack_set, symbol->symbol_number);
    ah_stack_p--; return 1;
  }

  // Step 3: handle a full production starting with a terminal (which can't happen!)
  gram_edge *this_first_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));

  if (this_first_element_edge->symbol_table_entry->symbol_number < ah_left_context_grammar->first_nonterminal)  // Is terminal
    text_message(TEXT_FATAL, "first symbol '%s' in left context grammar production for %s is a terminal\n", this_first_element_edge->symbol_table_entry->id, symbol->id);

  // Step 4: handle a full production starting with a nonterminal
#if defined (AH_AUTOMATON_TRACE)
  text_printf("\n**ah_gen: starting non-epsilon production\n");
#endif

  while (ah_generate(this_first_element_edge->symbol_table_entry, ++production, level + 1))
    ;

  set_difference_element(&ah_stack_set, symbol->symbol_number);
  ah_stack_p--; return 1;
}

void write_bin_file(FILE *bin_file, int value)
{
  fprintf(bin_file, "%i ", value);
}

void write_bin_file_newline(FILE *bin_file)
{
  fprintf(bin_file, "\n");
}

static int ext_to_int_state(int state, ah_table *this_parse_table)
{
  if (state < 0)
    return - state + AH_ACTION_POP_AND_ACCEPT - 1;
  else
    return state + this_parse_table->tilded_state_count - 1;
}

static int int_to_ext_state(int *state, ah_table *this_parse_table)
{
  if (state == (int*) -1)
    return -1;

  for (int ext_state = 0; ;ext_state++)
  {
    if (this_parse_table->state_vector[ext_state] == state)
    {
      if (ext_state >= this_parse_table->tilded_state_count)
        return ext_state - this_parse_table->tilded_state_count + 1;
      else
       return - ext_state + AH_ACTION_POP_AND_ACCEPT - 1;
    }
  }
}

void dump_parse_table(ah_table *ret_parse_table)
{
  //Now output the action table
  text_printf("Parse table with start at %i\n", int_to_ext_state(ret_parse_table->start, ret_parse_table));

  for (int i = 0; i < ret_parse_table->tilded_state_count + ret_parse_table->nontilded_state_count; i++)
  {
    text_printf("%i (%i)", int_to_ext_state(ret_parse_table->state_vector[i], ret_parse_table), ret_parse_table->state_vector[i]);

    int *action = ret_parse_table->state_vector[i];

    while (1)
    {
      if (*action <= AH_ACTION_ERROR && *action >= AH_ACTION_POP_AND_ACCEPT)
      {
        switch (*action)
        {
          case AH_ACTION_ERROR: break;
          case AH_ACTION_ACCEPT: text_printf(" A"); break;
          case AH_ACTION_POP: text_printf(" O"); break;
          case AH_ACTION_POP_AND_ACCEPT: text_printf(" O A"); break;
        }
        break;
      }
      else // What kind of action are we?
        if (*action < (int) ret_parse_table->grammar->first_nonterminal)  // Shift
        {
           text_printf(" S%i %i", *action, int_to_ext_state((int*) *(action+1), ret_parse_table));
        }
        else if (*action < (int) ret_parse_table->grammar->first_header)  // Reduce
        {
          text_printf(" R%i %i", *action, int_to_ext_state((int*) *(action+1), ret_parse_table));
        }
        else                                                        // Push
          text_printf(" P%i %i", int_to_ext_state((int*) *(action), ret_parse_table), int_to_ext_state((int*) *(action+1), ret_parse_table));

      action+=2;
    }

    text_printf("\n");
  }
}

ah_table *ah_ah_table(grammar *this_gram)
{
  if (!this_gram->tilde_enabled)
    text_message(TEXT_FATAL, "ah_table called on non-tilde enabled grammar (change grammar[x] call to grammar[x tilde_enabled])");

  unsigned action_count;
  unsigned total_action_count = 0;
  grammar *left_context_grammar = ahz_left_context_grammar(this_gram);
#if defined (AH_AUTOMATON_TRACE)
  text_printf("Left context grammar dump\n");
  gram_write(stdout, left_context_grammar, 0);
  text_printf("End of left context grammar dump\n");
#endif

  ah_table *ret_parse_table = (ah_table*) mem_calloc(1, sizeof(ah_table));

  ret_parse_table->grammar = this_gram;

  ah_left_context_grammar = left_context_grammar;
  ah_tilded_grammar = this_gram;

  int trie_accepting_state;

  void *trie;

  ah_reduction_edge_count = 0;
  set_ tilded_nonterminals_set = SET_NULL;
  int start_is_tilded = ah_collect_tildes_and_start(&tilded_nonterminals_set, this_gram, 0);
  unsigned *tilded_nonterminals = set_array(&tilded_nonterminals_set);
  int *trie_start_states = (int*) mem_calloc(set_cardinality(&tilded_nonterminals_set) + 1, sizeof(unsigned));
  int *trie_negative_start_states = (int*) mem_calloc(set_cardinality(&tilded_nonterminals_set) + 1, sizeof(unsigned));

  //Set up the stack
  stack_size = (this_gram->first_level_0_slot - this_gram->first_nonterminal + 1);
  ah_stack = (ah_generate_stack*) mem_calloc(stack_size, sizeof(struct ah_generate_stack));
  ah_stack_p = ah_stack;

  FILE *vcg_file;

  if (!vcg_suppressed)
  {
    vcg_file = fopen("trie.vcg", "w");

    fprintf(vcg_file,
                "graph:{\n"
                "orientation:top_to_bottom\n"
                "edge.arrowsize:7\n"
                "edge.thickness:1\n"
                "display_edge_labels:yes\n"
                "arrowmode:free\n"
                "node.borderwidth:1\n"
                "orientation:left_to_right\n");
  }

  FILE *table_file = fopen("ah_trie.tbl", "w");

  FILE *bin_file = fopen("ah_trie.bin", "w");

#if defined (AH_AUTOMATON_TRACE)
  table_file = stdout;
#endif
  text_redirect(table_file);

  text_printf("\nSymbols\n");
  gram_print_symbols(this_gram, 0);

  text_printf("\nStart symbol\n");
  text_printf("%4u %s\n", this_gram->start_rule->symbol_number, this_gram->start_rule->id);

  text_printf("\nRules\n");
  gram_print_rules(this_gram);

  text_printf("\nReduction table\n");
  unsigned *reductions = set_array(&this_gram->reductions);

  for (unsigned *this_reduction = reductions; *this_reduction != SET_END; this_reduction++)
  {
    text_printf("R%-4u |%u|->%-4u ",
                *this_reduction,
                gram_slot_prefix_length(this_gram, *this_reduction),
                gram_slot_lhs_symbol(this_gram, *this_reduction)->symbol_number);

    gram_print_slot_by_number(this_gram, *this_reduction, 0);
    text_printf("\n");
  }

  text_printf("\nParse table");

  int first = 1;

  //Start of trie construction
  for (unsigned *this_tilded_nonterminal = tilded_nonterminals; *this_tilded_nonterminal != SET_END; this_tilded_nonterminal++)
  {
    gram_symbols_data *this_trie_symbol = gram_find_symbol_by_number(this_gram, *this_tilded_nonterminal);

    char *this_lc_symbol_id = (char*) mem_calloc(1, strlen(this_trie_symbol->id) + strlen(LC_SUFFIX) + 1);
    strcat(this_lc_symbol_id, this_trie_symbol->id);
    strcat(this_lc_symbol_id, LC_SUFFIX);
    left_context_grammar->start_rule = gram_find_symbol_by_id(left_context_grammar, this_lc_symbol_id, SCRIPT_NS_NONTERMINAL);

    gram_node *moveable_epsilon_node = gram_insert_node(left_context_grammar->start_rule->rule_tree, gram_find_symbol_by_id(this_gram, GRAM_EPSILON_SYMBOL_STRING, SCRIPT_NS_SPECIAL), 0, 0);

    if (script_gtb_verbose())
    {
      text_redirect(stdout);
      text_printf("Outputing trie for %s ...\n", this_trie_symbol->id);
      text_redirect(table_file);
    }

    old_state_count = trie_state_count;  //we've already put a new root in!
    old_nontilded_state_count = trie_nontilded_state_count;  //we've already put a new root in!
    old_tilded_state_count = trie_tilded_state_count;
    old_terminal_edge_count = trie_terminal_edge_count;
    old_nonterminal_edge_count = trie_nonterminal_edge_count;
    old_reduction_edge_count = ah_reduction_edge_count;

    if (first)
    {
      first = 0;
      trie = trie_create();
    }
    else
    {
#if defined (AH_AUTOMATON_TRACE)
    text_printf("Restarting trie\n");
#endif

      trie_restart(trie);
    }

    ah_trie_root = (trie_node*) graph_root(trie);

    trie_start_states[this_tilded_nonterminal - tilded_nonterminals] = graph_atom_number(graph_root(trie));
    trie_start_states[this_tilded_nonterminal - tilded_nonterminals + 1] = INT_MAX;

    trie_negative_start_states[this_tilded_nonterminal - tilded_nonterminals] = -trie_tilded_state_count + AH_ACTION_POP_AND_ACCEPT;
    trie_negative_start_states[this_tilded_nonterminal - tilded_nonterminals + 1] = INT_MIN;

    trie_node *accepting_state = trie_insert_unsigned((trie_node*) graph_root(trie), *this_tilded_nonterminal, 0, 1);
#if defined (AH_AUTOMATON_TRACE)
      text_printf("!!!4 Added accepting symbol %u to trie: accepting state %i\n", *this_tilded_nonterminal, graph_atom_number(accepting_state));
      text_printf("%lu state%s, %lu normal state%s, %lu tilded state%s, %lu terminal_edge%s, %lu nonterminal edge%s, %lu tilded edge%s and %lu reduction edge%s\n",
                  trie_state_count - old_state_count, trie_state_count - old_state_count == 1 ? "" : "s",
                  trie_nontilded_state_count - old_nontilded_state_count, trie_nontilded_state_count - old_nontilded_state_count  == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count - old_tilded_state_count == 1 ? "" : "s",
                  trie_terminal_edge_count - old_terminal_edge_count, trie_terminal_edge_count -old_terminal_edge_count == 1 ? "" : "s",
                  trie_nonterminal_edge_count - old_nonterminal_edge_count , trie_nonterminal_edge_count - old_nonterminal_edge_count == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count -old_tilded_state_count == 1 ? "" : "s",
                  ah_reduction_edge_count - old_reduction_edge_count, ah_reduction_edge_count -old_reduction_edge_count == 1 ? "" : "s");
#endif
    accepting_state->is_accepting = 1;
    if (*this_tilded_nonterminal == this_gram->start_rule->symbol_number)
      trie_accepting_state = graph_atom_number(accepting_state);

    //Now generate over all symbols
    for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(left_context_grammar->symbol_table));
         this_symbol != NULL;
         this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol))
    {
  #if defined (AH_AUTOMATON_TRACE)
      text_printf("Generating language for left context symbol %s\n", this_symbol->id);
  #endif
      if (this_symbol->rule_tree != NULL)
      {
        int production = 1;
        while (ah_generate(this_symbol, production++, 1))
          ;
      }
    }

// Output this section of parse table
    for (trie_node *this_trie_node = (trie_node*) graph_next_node(trie); this_trie_node != NULL; this_trie_node = (trie_node*) graph_next_node(this_trie_node))
    {
       if (!vcg_suppressed)
       {
         fprintf(vcg_file, "node:{title:\"%i\" label:\"%i%s\" shape:%s%s}\n", graph_atom_number(this_trie_node), graph_atom_number(this_trie_node), this_trie_node->is_accepting ? " pop" : "", this_trie_node->is_accepting ? "ellipse" : "circle", this_trie_node->is_accepting ? " bordercolor:blue" : "" );
         if (graph_atom_number(this_trie_node) > AH_VCG_LIMIT)
           vcg_suppressed = 1;
       }

      text_printf("\n%6i ", graph_atom_number(this_trie_node));

      // The binary file has the syntax ( out_degree action action action... 0 ) where out_degree is negative for tilded edges
      // Count the outedges
      action_count = 0;

      // Phase one: output text and vcg files
      for (trie_edge* this_trie_edge = (trie_edge*) graph_next_out_edge(this_trie_node); this_trie_edge != NULL; this_trie_edge = (trie_edge*) graph_next_out_edge(this_trie_edge))
      {
        if (!vcg_suppressed)
        {
          if (this_trie_edge->is_reduction)
            fprintf(vcg_file, "edge:{label:\"R%u\" class:2 color:blue sourcename:\"%i\" targetname:\"%i\"}\n", this_trie_edge->element,
                        graph_atom_number(graph_source(this_trie_edge)),
                        graph_atom_number(graph_destination(this_trie_edge)));
          else
            fprintf(vcg_file, "edge:{label:\"%s\" sourcename:\"%i\" targetname:\"%i\"}\n", gram_find_symbol_by_number(this_gram, this_trie_edge->element)->id,
                        graph_atom_number(graph_source(this_trie_edge)),
                        graph_atom_number(graph_destination(this_trie_edge)));
        }

        if (this_trie_edge->is_reduction)
        {
          text_printf("R%u %i ", this_trie_edge->element, graph_atom_number(graph_destination(this_trie_edge)));
          action_count+=2;
        }
        else
        {
          // Find un-tilded equivalent
          char *tilded_id = gram_find_symbol_by_number(this_gram, this_trie_edge->element)->id;
          char *untilded_id = gram_strip_suffix(tilded_id, GRAM_TILDE_SUFFIX);
          gram_symbols_data *this_untilded_symbol = gram_find_symbol_by_id(this_gram, untilded_id, SCRIPT_NS_NONTERMINAL);

          if (strcmp(untilded_id, tilded_id) != 0)
          {
            text_printf("P%i %i ", this_untilded_symbol->symbol_number, graph_atom_number(graph_destination(this_trie_edge)));

            if (graph_atom_number(graph_destination(this_trie_edge)) >= 0)
              fprintf(stdout, "Push has positive operand\n");

            action_count+=2;
          }
          else if (this_trie_edge->element < this_gram->first_nonterminal)
          {
            text_printf("S%u %i ", this_trie_edge->element, graph_atom_number(graph_destination(this_trie_edge)));

            if (graph_atom_number(graph_destination(this_trie_edge)) <= 0)
              fprintf(stdout, "Shift has negative operand\n");

            action_count+=2;
          }

          mem_free(untilded_id);
        }
        }

        action_count++;
        if (this_trie_node->is_accepting)
        {

          if (graph_atom_number(this_trie_node) == trie_accepting_state)
          {
            if (start_is_tilded)
              text_printf("O A ");
            else
              text_printf("A ");
          }
          else
            text_printf("O ");
        }

      write_bin_file(bin_file, graph_atom_number(this_trie_node));
      write_bin_file(bin_file, action_count);

      // Do the edges first
      for (trie_edge* this_trie_edge = (trie_edge*) graph_next_out_edge(this_trie_node); this_trie_edge != NULL; this_trie_edge = (trie_edge*) graph_next_out_edge(this_trie_edge))
      {
        if (this_trie_edge->is_reduction)
        {
          write_bin_file(bin_file, this_trie_edge->element);
          write_bin_file(bin_file, graph_atom_number(graph_destination(this_trie_edge)));
        }
        else
        {
          // Find un-tilded equivalent
          char *tilded_id = gram_find_symbol_by_number(this_gram, this_trie_edge->element)->id;
          char *untilded_id = gram_strip_suffix(tilded_id, GRAM_TILDE_SUFFIX);
          gram_symbols_data *this_untilded_symbol = gram_find_symbol_by_id(this_gram, untilded_id, SCRIPT_NS_NONTERMINAL);

          if (strcmp(untilded_id, tilded_id) != 0)
          {
            write_bin_file(bin_file, this_untilded_symbol->symbol_number);
            write_bin_file(bin_file, graph_atom_number(graph_destination(this_trie_edge)));
          }
          else if (this_trie_edge->element < this_gram->first_nonterminal)
          {
            write_bin_file(bin_file, this_trie_edge->element);
            write_bin_file(bin_file, graph_atom_number(graph_destination(this_trie_edge)));
          }

          mem_free(untilded_id);
        }
      }

      // and now do the nonadic action
      if (this_trie_node->is_accepting)
      {
        if (graph_atom_number(this_trie_node) == trie_accepting_state)
        {
          if (start_is_tilded)
            write_bin_file(bin_file, AH_ACTION_POP_AND_ACCEPT);
          else
            write_bin_file(bin_file, AH_ACTION_ACCEPT);
        }
        else
          write_bin_file(bin_file, AH_ACTION_POP);
      }
      else
        write_bin_file(bin_file, AH_ACTION_ERROR);

      total_action_count += action_count;
      write_bin_file_newline(bin_file);
    }

    if (*(this_tilded_nonterminal + 1) != SET_END)
    {
      graph_delete_node(moveable_epsilon_node);
    }
    if (script_gtb_verbose())
    {
      text_redirect(stdout);
      text_printf("%lu state%s, %lu normal state%s, %lu tilded state%s, %lu terminal_edge%s, %lu nonterminal edge%s, %lu tilded edge%s and %lu reduction edge%s\n",
                  trie_state_count - old_state_count, trie_state_count - old_state_count == 1 ? "" : "s",
                  trie_nontilded_state_count - old_nontilded_state_count, trie_nontilded_state_count - old_nontilded_state_count  == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count - old_tilded_state_count == 1 ? "" : "s",
                  trie_terminal_edge_count - old_terminal_edge_count, trie_terminal_edge_count -old_terminal_edge_count == 1 ? "" : "s",
                  trie_nonterminal_edge_count - old_nonterminal_edge_count , trie_nonterminal_edge_count - old_nonterminal_edge_count == 1 ? "" : "s",
                  trie_tilded_state_count - old_tilded_state_count, trie_tilded_state_count -old_tilded_state_count == 1 ? "" : "s",
                  ah_reduction_edge_count - old_reduction_edge_count, ah_reduction_edge_count -old_reduction_edge_count == 1 ? "" : "s");
      text_redirect(table_file);
    }
  }

  // Output trie start table
  text_printf("\n\nTrie start table");
  unsigned *this_tilded_nonterminal;
  int *this_start_state;
  int *this_negated_start_state;

  for (this_tilded_nonterminal = tilded_nonterminals, this_start_state = trie_start_states, this_negated_start_state = trie_negative_start_states;
       *this_tilded_nonterminal != SET_END;
       this_tilded_nonterminal++, this_start_state++, this_negated_start_state++)
    text_printf("\n%u %u %i", *this_tilded_nonterminal, *this_start_state, *this_negated_start_state);

  if (!vcg_suppressed)
    fprintf(vcg_file, "}\n");

  text_redirect(stdout);

  ret_parse_table->tilded_state_count = trie_tilded_state_count - 1;
  ret_parse_table->nontilded_state_count = trie_nontilded_state_count - 1;

  if (script_gtb_verbose())
  {
    text_printf("\nAH grammar has %u special symbols, %u terminals, %u nonterminals and %u slots\n",
                this_gram->first_terminal,
                this_gram->first_nonterminal - this_gram->first_terminal,
                this_gram->first_level_0_slot - this_gram->first_nonterminal,
                this_gram->first_header - this_gram->first_nonterminal);

    text_printf("AH Automaton has %lu state%s, %lu normal state%s, %lu tilded state%s, %lu terminal_edge%s, %lu nonterminal edge%s, %lu tilded edge%s and %lu reduction edge%s\n\n",
                  trie_state_count, trie_state_count== 1 ? "" : "s",
                  trie_nontilded_state_count - 1, trie_nontilded_state_count -1 == 1 ? "" : "s",
                  trie_tilded_state_count - 1, trie_tilded_state_count - 1== 1 ? "" : "s",
                  trie_terminal_edge_count, trie_terminal_edge_count == 1 ? "" : "s",
                  trie_nonterminal_edge_count, trie_nonterminal_edge_count == 1 ? "" : "s",
                  trie_tilded_state_count - 1, trie_tilded_state_count -1 == 1 ? "" : "s",
                  ah_reduction_edge_count, ah_reduction_edge_count == 1 ? "" : "s");
   }
/*  text_printf("\nAH automaton has %lu states, %lu normal state%s, %lu tilded state%s and %lu reduction edge%s\n",
              trie_state_count,
              ret_parse_table->nontilded_state_count, ret_parse_table->nontilded_state_count == 1 ? "" : "s",
              ret_parse_table->tilded_state_count, ret_parse_table->tilded_state_count == 1 ? "" : "s",
              ah_reduction_edge_count, ah_reduction_edge_count == 1 ? "" : "s");
*/

  //Time to read the table back in
  int **state_vector = (int**) mem_calloc(ret_parse_table->nontilded_state_count + ret_parse_table->tilded_state_count, sizeof(int*));

  ret_parse_table->state_vector = state_vector;

  ret_parse_table->actions = (int*) mem_calloc(total_action_count, sizeof(int));


  #if defined(AH_BIN_FILE_TRACE)
  text_printf("Action base address: %u; first header: %u\n", (unsigned) ret_parse_table->actions, ret_parse_table->grammar->first_header);
  #endif

  if ((unsigned) ret_parse_table->actions < ret_parse_table->grammar->first_header)
    text_message(TEXT_FATAL, "AH parse table base address is less than this grammar's first header\n");

  fclose(bin_file);

  // Reopen file for read
  bin_file = fopen("ah_trie.bin", "r");
  int *next_free_location = ret_parse_table->actions;

  while (!feof(bin_file))
  {
    int state;
    if (fscanf(bin_file, "%i ", &state) == 0)  // Get the state number
      break;

    state = ext_to_int_state(state, ret_parse_table);

    fscanf(bin_file, "%i ", &action_count); // Get the action count

    //Load state vector
    state_vector[state] = next_free_location;

    for (unsigned i = 0; i< action_count; i++)
      fscanf(bin_file, "%i ", next_free_location++);
  }

// Run through the table, replacing state numbers with action start addresses
  for (int i = 0; i < ret_parse_table->tilded_state_count + ret_parse_table->nontilded_state_count; i++)
  {
    int *action = state_vector[i];

    while (1)
    {
      if (*action <= AH_ACTION_ERROR && *action >= AH_ACTION_POP_AND_ACCEPT)
        break;
      else // What kind of action are we?
        if (*action < (int) ret_parse_table->grammar->first_nonterminal)  // Shift
          *(action + 1) = (int) state_vector[ext_to_int_state(*(action + 1), ret_parse_table)];
        else if (*action < (int) ret_parse_table->grammar->first_level_0_slot)  // Push
        {
          for (this_tilded_nonterminal = tilded_nonterminals, this_start_state = trie_start_states; *this_tilded_nonterminal != *action; this_tilded_nonterminal++, this_start_state++)
            ;
          int index = ext_to_int_state(*this_start_state, ret_parse_table);
          *action = (int) state_vector[index];
          *(action + 1) = (int) state_vector[ext_to_int_state(*(action + 1), ret_parse_table)];
        }
        else                                                        // Reduce
        {
          int z = ext_to_int_state(*(action + 1), ret_parse_table);
          *(action + 1) = (int) state_vector[z];
        }

      action+=2;
    }
  }

  //Finally, find start state
  for (this_tilded_nonterminal = tilded_nonterminals, this_start_state = trie_start_states; *this_tilded_nonterminal != this_gram->start_rule->symbol_number; this_tilded_nonterminal++, this_start_state++)
    ;

  ret_parse_table->start = state_vector[ext_to_int_state(*this_start_state, ret_parse_table)];
  ret_parse_table->tilded_nonterminals = tilded_nonterminals;
  ret_parse_table->trie_start_states = trie_start_states;
  ret_parse_table->trie_negative_start_states = trie_negative_start_states;

#if defined(AH_BIN_FILE_TRACE)
  dump_parse_table(ret_parse_table);
#endif

  return ret_parse_table;
}

// Datatypes for the recogniser
struct ah_call_graph_node {int *state; int level; struct ah_call_graph_edge *next_edge;};
struct ah_call_graph_edge {struct ah_call_graph_node *destination; struct ah_call_graph_edge *next_edge;};

struct ah_process {int *state; ah_call_graph_node *cgn;};

gram_symbols_data *ah_trie_symbol_of_state(int *address_state, ah_table *this_parse_table)
{
  unsigned *this_tilded_nonterminal;
  int *this_trie_start_state;
  int state_number = int_to_ext_state(address_state, this_parse_table);

  if (state_number < 0)
    for (this_tilded_nonterminal = this_parse_table->tilded_nonterminals, this_trie_start_state = this_parse_table->trie_negative_start_states;
         *this_trie_start_state >= state_number;
         this_tilded_nonterminal++, this_trie_start_state++)
      ;
  else
    for (this_tilded_nonterminal = this_parse_table->tilded_nonterminals, this_trie_start_state = this_parse_table->trie_start_states;
         *this_trie_start_state <= state_number;
         this_tilded_nonterminal++, this_trie_start_state++)
     ;

  this_tilded_nonterminal--;

  return gram_find_symbol_by_number(this_parse_table->grammar, *this_tilded_nonterminal);
}

void ah_show_call_graph(void *call_graph, ah_table *this_parse_table)
{
  for (ah_call_graph_node *this_node = (ah_call_graph_node *) symbol_next_symbol_in_scope(symbol_get_scope(call_graph));
       this_node != NULL;
       this_node = (ah_call_graph_node *) symbol_next_symbol_in_scope(this_node))
  {
    text_printf("CG node (%i, %i) points to", int_to_ext_state(this_node->state, this_parse_table), this_node->level);

    for (ah_call_graph_edge *this_edge = this_node->next_edge;
         this_edge != NULL;
         this_edge = this_edge->next_edge)
      text_printf(" (%i, %i)", int_to_ext_state(this_edge->destination->state, this_parse_table), this_edge->destination->level);

    text_printf(" .\n");
  }

}

void ah_show_process_set(void *process_set, ah_table *this_parse_table)
{
  for (ah_process *this_process = (ah_process *) symbol_next_symbol_in_scope(symbol_get_scope(process_set));
       this_process != NULL;
       this_process = (ah_process *) symbol_next_symbol_in_scope(this_process))
    text_printf("Process (%i, (%i, %i))\n", int_to_ext_state(this_process->state, this_parse_table), int_to_ext_state(this_process->cgn->state, this_parse_table), this_process->cgn->level);

}

void ah_show_popped_states_set(void *popped_set, ah_table *this_parse_table)
{
  for (int *this_state = (int *) symbol_next_symbol_in_scope(symbol_get_scope(popped_set));
       this_state != NULL;
       this_state = (int *) symbol_next_symbol_in_scope(this_state))
    text_printf("State %i\n", int_to_ext_state((int*) *this_state, this_parse_table));
}


void do_shift(int *action, ah_table *this_parse_table, int last_lexeme, ah_process temp_process, ah_call_graph_node *cg_node,
              void *next_frontier)
{
#if defined(AH_RECOGNISER_TRACE)
  text_printf("Shift %i %i", *action, int_to_ext_state((int*) *(action+1), this_parse_table));
#endif
  hist_update (shifts, *action);

  if (*action != last_lexeme)
  {
#if defined(AH_RECOGNISER_TRACE)
  text_printf(" (symbol does not match current lexeme: no action performed)\n");
#endif
  }
  else
  {
#if defined(AH_RECOGNISER_TRACE)
  text_printf(" (symbol matches current lexeme: updating sets)\n");
#endif
    // Shift x, y on process (s, c) yields process (y, c) in next frontier
    temp_process.state = (int*) *(action + 1);
    temp_process.cgn = cg_node;
    symbol_find(next_frontier, &temp_process, sizeof(ah_process), sizeof(ah_process), NULL, SYMBOL_ANY);
  }
}

void do_reduce(int last_lexeme, int *action, ah_table *this_parse_table, ah_process temp_process, ah_call_graph_node *cg_node,
               void *current_frontier, void *pending)
{
#if defined(AH_RECOGNISER_TRACE)

    text_printf("Reduce %i %i\n", *action, int_to_ext_state((int*) *(action+1), this_parse_table));
#endif

  gram_symbols_data *slot_symbol = gram_slot_lhs_symbol(this_parse_table->grammar, (*action)); //Get symbol from reduction table!

  set_ temp_follow_set = SET_NULL;

  set_assign_set(&temp_follow_set, &slot_symbol->follow);
  gram_unwrap_nonterminal_follow_set(this_parse_table->grammar, &temp_follow_set);

  #if defined(AH_RECOGNISER_TRACE)
  text_printf("Follow(%s) = {", slot_symbol->id);
  set_print_set(&temp_follow_set, NULL, 0);
  text_printf("}\n");
  #endif

  if (set_includes_element(&temp_follow_set, last_lexeme))
  {
  #if defined(AH_RECOGNISER_TRACE)
    text_printf("Current symbol is in follow(%s) so executing reduce\n", slot_symbol->id);
  #endif

    // Reduce x, y on process (s, c) yields process (y, c) in pending and current frontier
    temp_process.state = (int*) *(action + 1);
    temp_process.cgn = cg_node;
    if (symbol_lookup_key(current_frontier, &temp_process, NULL) == NULL)
    {
      symbol_insert_key(current_frontier, &temp_process, sizeof(ah_process), sizeof(ah_process));
      symbol_insert_key(pending, &temp_process, sizeof(ah_process), sizeof(ah_process));
      hist_update(reductions, slot_symbol->symbol_number);
    }
  }
  else
  {
  #if defined(AH_RECOGNISER_TRACE)
  text_printf("Current symbol not in follow(%s) so suppressing reduce\n", slot_symbol->id);
  #endif
  }

  set_free(&temp_follow_set);
}

void do_pop(int *this_pending_state, ah_table *this_parse_table, int last_lexeme, ah_call_graph_node *cg_node,
            int current_level, void *popped_states, void *current_frontier, void *pending, int &pop_actions)
{
#if defined(AH_RECOGNISER_TRACE)
text_printf("Pop\n");
text_printf("This_pending_state = %i\n", int_to_ext_state(this_pending_state, this_parse_table));
text_printf("this_parse_table = %p\n", this_parse_table);
text_printf("last_lexeme = %i\n", last_lexeme);
text_printf("current_level = %i\n", current_level);
text_printf("cg_node->level = %i\n", cg_node->level);
text_printf("current_frontier %p\n", current_frontier);
#endif

ah_process temp_process;

set_ temp_set = SET_NULL;

set_assign_set(&temp_set, &ah_trie_symbol_of_state(this_pending_state, this_parse_table)->follow);
gram_unwrap_nonterminal_follow_set(this_parse_table->grammar, &temp_set);

#if defined(AH_RECOGNISER_TRACE)
text_printf("Follow(%s) = {", ah_trie_symbol_of_state(this_pending_state, this_parse_table)->id);
set_print_set(&temp_set, NULL, 0);
text_printf("}\n");
#endif
  // pOp on process (s, c) yields processes {(label(c), x) for all x pointed
  // to by c} in pending and current frontier
  if (set_includes_element(&temp_set, last_lexeme))
  {
#if defined(AH_RECOGNISER_TRACE)
    text_printf("Current lexeme %i (%s) is in follow set for trie %s from state %i: executing pop\n",
                last_lexeme,
                gram_find_symbol_by_number(this_parse_table->grammar, last_lexeme)->id,
                ah_trie_symbol_of_state(this_pending_state, this_parse_table)->id,
                int_to_ext_state(this_pending_state, this_parse_table)
               );
#endif
    temp_process.state = cg_node->state;

    // Put this node into the popped set
    if (cg_node->level == current_level)
      symbol_find(popped_states, &(cg_node->state), sizeof(int), sizeof(int), NULL, SYMBOL_ANY);

    for (ah_call_graph_edge *this_edge = cg_node->next_edge; this_edge != NULL; this_edge = this_edge->next_edge)
    {
      temp_process.cgn = this_edge->destination;

      hist_update(pops, ah_trie_symbol_of_state(this_pending_state, this_parse_table)->symbol_number);
      pop_actions++;
      if (symbol_lookup_key(current_frontier, &temp_process, NULL) == NULL)
      {
        symbol_insert_key(current_frontier, &temp_process, sizeof(ah_process), sizeof(ah_process));
        symbol_insert_key(pending, &temp_process, sizeof(ah_process), sizeof(ah_process));
      }
#if defined(AH_RECOGNISER_TRACE)
      text_printf("Created process (%i, (%i, %i))\n", int_to_ext_state(temp_process.state, this_parse_table), int_to_ext_state(temp_process.cgn->state, this_parse_table), temp_process.cgn->level);
#endif
    }
  }
  else
  {
#if defined(AH_RECOGNISER_TRACE)
    text_printf("Current lexeme %i (%s) is not in follow set for trie %s from state %i: suppressing pop on lookahead\n",
                last_lexeme,
                gram_find_symbol_by_number(this_parse_table->grammar, last_lexeme)->id,
                ah_trie_symbol_of_state(this_pending_state, this_parse_table)->id,
                int_to_ext_state(this_pending_state, this_parse_table)
               );
#endif
  }

#if defined(AH_RECOGNISER_TRACE)
  text_printf("End of pop\n");
#endif
  set_free(&temp_set);
}

void do_contingent_pop(int *this_pending_state, ah_table *this_parse_table, int last_lexeme, ah_call_graph_node *cg_node,
            int current_level, void *popped_states, void *current_frontier, void *pending, int &pop_actions)
{
#if defined(AH_RECOGNISER_TRACE)
text_printf("Pop\n");
text_printf("This_pending_state = %i\n", int_to_ext_state(this_pending_state, this_parse_table));
text_printf("this_parse_table = %p\n", this_parse_table);
text_printf("last_lexeme = %i\n", last_lexeme);
text_printf("current_level = %i\n", current_level);
text_printf("cg_node->level = %i\n", cg_node->level);
text_printf("current_frontier %p\n", current_frontier);
#endif

ah_process temp_process;

    temp_process.state = this_pending_state;

    // Put this node into the popped set
    if (cg_node->level == current_level)
      symbol_find(popped_states, &(cg_node->state), sizeof(int), sizeof(int), NULL, SYMBOL_ANY);

      temp_process.cgn = cg_node;

      hist_update(contingent_pops, ah_trie_symbol_of_state(this_pending_state, this_parse_table)->symbol_number);
      pop_actions++;
      if (symbol_lookup_key(current_frontier, &temp_process, NULL) == NULL)
      {
        symbol_insert_key(current_frontier, &temp_process, sizeof(ah_process), sizeof(ah_process));
        symbol_insert_key(pending, &temp_process, sizeof(ah_process), sizeof(ah_process));
      }
#if defined(AH_RECOGNISER_TRACE)
      text_printf("Created process (%i, (%i, %i))\n", int_to_ext_state(temp_process.state, this_parse_table), int_to_ext_state(temp_process.cgn->state, this_parse_table), temp_process.cgn->level);
#endif

#if defined(AH_RECOGNISER_TRACE)
  text_printf("End of pop\n");
#endif
}

void do_push(int* this_pending_state, void *call_graph, int *action, ah_table *this_parse_table, int last_lexeme, ah_process temp_process, ah_call_graph_node *cg_node,
             int current_level, void *popped_states, void *current_frontier, void *pending, int &pop_actions)
  {
  ah_call_graph_node temp_cg_node;

  #if defined(AH_RECOGNISER_TRACE)
  text_printf("Push %i %i (%i)\n", int_to_ext_state((int*) *action, this_parse_table), int_to_ext_state((int*) *(action+1), this_parse_table));
  #endif
  // Push x, y on process (s, c) yields process (x, z) in pending and current frontier where
  // process z is a cgn labeled y whose child is c

  //Get L = symbol(trie(x))) and only do the action if c_sym in FIRST(L) or L nullable and c_sym in FOLLOW(L)
  set_ temp_first_set = SET_NULL;

  gram_symbols_data *trie_start_symbol = ah_trie_symbol_of_state((int*) *(action), this_parse_table);

  set_assign_set(&temp_first_set, &trie_start_symbol->first);

  set_ temp_follow_set = SET_NULL;

  set_assign_set(&temp_follow_set, &trie_start_symbol->follow);
  gram_unwrap_nonterminal_follow_set(this_parse_table->grammar, &temp_follow_set);

  #if defined(AH_RECOGNISER_TRACE)
  text_printf("First(%s) = {", trie_start_symbol->id);
  set_print_set(&temp_first_set, NULL, 0);
  text_printf("}\n");

  text_printf("Follow(%s) = {", trie_start_symbol->id);
  set_print_set(&temp_follow_set, NULL, 0);
  text_printf("}\n");
  #endif

  if (set_includes_element(&temp_first_set, last_lexeme)  || (set_includes_element(&temp_first_set, GRAM_EPSILON_SYMBOL_NUMBER) && set_includes_element(&temp_follow_set, last_lexeme)))
  {
  #if defined(AH_RECOGNISER_TRACE)
    text_printf("Current symbol is in first(%s follow(%s)) so executing push\n", trie_start_symbol->id, trie_start_symbol->id);
  #endif

    temp_cg_node.state = (int*) *(action+1);
    temp_cg_node.level = current_level;
    ah_call_graph_node *this_node;

    if ((this_node = (ah_call_graph_node*) symbol_lookup_key(call_graph, &temp_cg_node, NULL)) == NULL)
    {
      this_node = (ah_call_graph_node*) symbol_find(call_graph, &temp_cg_node, 2*sizeof(int), sizeof(ah_call_graph_node), NULL, SYMBOL_ANY);

      // And create the new processes
      temp_process.state = (int*) *action;
      temp_process.cgn = this_node;
      if (symbol_lookup_key(current_frontier, &temp_process, NULL) == NULL)
      {
        symbol_insert_key(current_frontier, &temp_process, sizeof(ah_process), sizeof(ah_process));
        symbol_insert_key(pending, &temp_process, sizeof(ah_process), sizeof(ah_process));
      }
    }
    else
    {
  #if defined(AH_RECOGNISER_TRACE)
      text_printf("Call graph node (%i, %i) already in graph\n", int_to_ext_state(this_node->state, this_parse_table), this_node->level);
  #endif
    }

    // Now scan edges to see if we are already connected to this_process->cgn
    ah_call_graph_edge *this_cg_edge;

    for (this_cg_edge = this_node->next_edge; this_cg_edge != NULL; this_cg_edge = this_cg_edge->next_edge)
      if (this_cg_edge->destination == cg_node)
        break;

    if (this_cg_edge == NULL)
    {
      ah_call_graph_edge *this_edge = (ah_call_graph_edge*) mem_calloc(1, sizeof(ah_call_graph_edge));

      this_edge->destination = cg_node;
      this_edge->next_edge = this_node->next_edge;
      this_node->next_edge = this_edge;

  #if defined(AH_RECOGNISER_TRACE)
      text_printf("Created call graph edge (%i, %i) -> (%i, %i)\n", int_to_ext_state(this_node->state, this_parse_table), this_node->level, int_to_ext_state(cg_node->state, this_parse_table), cg_node->level);
  #endif
      // Check to see whether the source node has had a pop done to it !CONTINGENT POPS!
      if (symbol_lookup_key(popped_states, &(this_node->state), NULL) != NULL)
      {
        temp_process.state = this_node->state;
        temp_process.cgn = cg_node;

  #if defined(AH_RECOGNISER_TRACE)
        text_printf("Added edge from node which has already been popped: executing pop to create process (%i, (%i, %i))\n",
                    int_to_ext_state(temp_process.state, this_parse_table), int_to_ext_state(temp_process.cgn->state, this_parse_table), temp_process.cgn->level);
  #endif

        hist_update(pops, trie_start_symbol->symbol_number);

        do_contingent_pop(temp_process.state, this_parse_table, last_lexeme, temp_process.cgn,
               current_level, popped_states, current_frontier, pending, pop_actions);
      }
     }
    else
    {
  #if defined(AH_RECOGNISER_TRACE)
      text_printf("Call graph edge (%i, %i) -> (%i, %i) already in graph\n", int_to_ext_state(this_node->state, this_parse_table), this_node->level, int_to_ext_state(cg_node->state, this_parse_table), cg_node->level);
  #endif
    }

    if (script_gtb_verbose())
      text_printf("**Profile: state %i push on symbol %s\n",
                  int_to_ext_state((int*) *(action+1), this_parse_table), trie_start_symbol->id);
    hist_update(push_histogram, -int_to_ext_state((int*) *(action+1), this_parse_table));
  }
  else
  {
  #if defined(AH_RECOGNISER_TRACE)
    text_printf("Current symbol not in first(%s follow(%s)) so suppressing push\n", trie_start_symbol->id, trie_start_symbol->id);
  #endif
  }

  set_free(&temp_first_set);
  set_free(&temp_first_set);
}

int ah_ah_recognise(ah_table* this_table, char *string)
{
  int pop_actions = 0;
  int max_cardinality = 0;
  int sum_over_cardinalities = 0;
  int accepted;
  int current_level = 0;

  unsigned process_count = 0;
  
  clock_t start_time;
  clock_t finish_time;

  hist_init(&pushes);
  hist_init(&pops);
  hist_init(&contingent_pops);
  hist_init(&shifts);
  hist_init(&reductions);
  hist_init(&push_histogram);

  //Step 1: lex the string to get its length
  lex_init(string, this_table->grammar);

  int string_length = 0;
  int last_lexeme;

  for (last_lexeme = lex_lex(); last_lexeme > GRAM_EOS_SYMBOL_NUMBER; last_lexeme = lex_lex())
    string_length++;

  if (last_lexeme != GRAM_EOS_SYMBOL_NUMBER)
    text_message(TEXT_FATAL, "\nri_recognise: string contains illegal symbol\n");

  text_printf("\n");
  text_message(TEXT_INFO, "RIGLR recognise: \'%s\'\n", string);

  //Step 2: Create sets (as symbol tables)
  void *call_graph = symbol_new_table("call_graph", 4217, 4219, symbol_compare_integer_pair, symbol_hash_integer_pair, symbol_print_integer_pair);
  void *popped_states = symbol_new_table("popped_states", 4217, 4219, symbol_compare_int, symbol_hash_int, symbol_print_int);

  void *current_frontier = symbol_new_table("current_frontier", 4217, 4219, symbol_compare_integer_pair, symbol_hash_integer_pair, symbol_print_integer_pair);
  void *next_frontier = symbol_new_table("next_frontier", 4217, 4219, symbol_compare_integer_pair, symbol_hash_integer_pair, symbol_print_integer_pair);
  void *pending = symbol_new_table("pending", 4217, 4219, symbol_compare_integer_pair, symbol_hash_integer_pair, symbol_print_integer_pair);
  void *temp_set;


  //Step 3: create call graph root
  ah_call_graph_node temp_cg_node = {(int*) -1, current_level};
  ah_call_graph_node *root = (ah_call_graph_node*) symbol_insert_key(call_graph, &temp_cg_node, 2*sizeof(int), sizeof(ah_call_graph_node));

  //Step 4: add (0,root) to current frontier
  ah_process temp_process = {this_table->start, root};
  symbol_insert_key(current_frontier, &temp_process, sizeof(ah_process), sizeof(ah_process));

  //Step 5: restart lexer
  lex_init(string, this_table->grammar);
  last_lexeme = GRAM_ILLEGAL_SYMBOL_NUMBER;

  start_time = clock();

  //Step 6: main lexer loop
  while (1)
  {
    //Step 6.0: finished if we have an empty current frontier
    if (symbol_next_symbol_in_scope(symbol_get_scope(current_frontier)) == NULL)
      break;

    //Step 6.1: get the next lexeme
    last_lexeme = lex_lex();

    //Step 6.2: copy the contents of the current_frontier to pending
    for (ah_process *this_copy_process = (ah_process*) symbol_next_symbol_in_scope(symbol_get_scope(current_frontier));
         this_copy_process != NULL;
         this_copy_process = (ah_process *) symbol_next_symbol_in_scope(this_copy_process))
      symbol_insert_key(pending, this_copy_process, sizeof(ah_process), sizeof(ah_process));


#if defined(AH_RECOGNISER_TRACE)
    text_printf("\n\nCurrent level: %i lexed: %i\n", current_level, last_lexeme, 0, 0);

    text_printf("\nAfter lex - call graph:\n");
    ah_show_call_graph(call_graph, this_table);

    text_printf("\nAfter lex - current frontier:\n");
    ah_show_process_set(current_frontier, this_table);
#endif

    // Step 6.2a update accept
    accepted = 0;

    //Step 6.3: pending loop
    while (1)
    {
      // Step 6.3.1: find first pending symbol
      ah_process *this_process = (ah_process*) symbol_next_symbol_in_scope(symbol_get_scope(pending));

      // Step 6.3.2: exit main loop if there's nothing to do
      if (this_process == NULL)
        break;

#if defined(AH_RECOGNISER_TRACE)
    text_printf("\n ** Processing (%i, (%i, %i)) -", int_to_ext_state(this_process->state, this_table), int_to_ext_state(this_process->cgn->state, this_table), this_process->cgn->level);
    text_printf(" state %i is in trie for %s\n", int_to_ext_state(this_process->state, this_table), ah_trie_symbol_of_state(this_process->state, this_table)->id);
#endif

      // Step 6.3.3: remember state and cgn and then dispose symbol
      int *action = this_process->state;
      int *this_pending_state = this_process->state;
      ah_call_graph_node *cg_node = this_process->cgn;
      symbol_free_symbol(this_process);

      // Step 6.3.3.1: action loop
      while (1)
      {
        if (*action <= AH_ACTION_ERROR && *action >= AH_ACTION_POP_AND_ACCEPT)
        {
          switch (*action)
          {
            case AH_ACTION_ERROR: break;
            case AH_ACTION_ACCEPT:
              if (cg_node == root)
              {  accepted = 1;
#if defined(AH_RECOGNISER_TRACE)
                text_printf("Accept at level %i\n", current_level);
#endif
              }
              break;
            case AH_ACTION_POP_AND_ACCEPT:
              if (cg_node == root)
              {
                accepted = 1;
#if defined(AH_RECOGNISER_TRACE)
                text_printf("Accept at level %i\n", current_level);
#endif
              }

              // fall through to straight pop
            case AH_ACTION_POP:
              do_pop(this_pending_state, this_table, last_lexeme, cg_node,
                     current_level, popped_states, current_frontier, pending, pop_actions);
              break;
          }
          break;
        }
        else // What kind of action are we?
        {
          if (*action < (int) this_table->grammar->first_nonterminal)  // Shift
            do_shift(action, this_table, last_lexeme, temp_process, cg_node, next_frontier);
          else if (*action < (int) this_table->grammar->first_header)  // Reduce
            do_reduce(last_lexeme, action, this_table, temp_process, cg_node, current_frontier, pending);
          else                                                         // Push
            do_push(this_pending_state, call_graph, action, this_table, last_lexeme, temp_process, cg_node,
                    current_level, popped_states, current_frontier, pending, pop_actions);
        }

        action+=2;
#if defined(AH_RECOGNISER_TRACE)
        text_printf("\nAfter action - call graph:\n");
        ah_show_call_graph(call_graph, this_table);

        text_printf("\nAfter action - popped states:\n");
        ah_show_popped_states_set(popped_states, this_table);

        text_printf("\nAfter action - current frontier:\n");
        ah_show_process_set(current_frontier, this_table);

        text_printf("\nAfter action - next frontier:\n");
        ah_show_process_set(next_frontier, this_table);

        text_printf("\nAfter action - pending:\n");
        ah_show_process_set(pending, this_table);
#endif
      }
    }

    // Step 6.4: Report cardinality of current frontier
    int cardinality = 0;
    for (ah_process *this_process = (ah_process *) symbol_next_symbol_in_scope(symbol_get_scope(current_frontier));
         this_process != NULL;
         this_process = (ah_process *) symbol_next_symbol_in_scope(this_process))
      cardinality++;

    if(script_gtb_verbose())
      text_printf("|U_%i| = %i\n", current_level, cardinality);

    if (cardinality > max_cardinality)
      max_cardinality = cardinality;

    sum_over_cardinalities += cardinality;

    // Step 6.5: swap current and next frontiers
    temp_set = current_frontier;
    current_frontier = next_frontier;
    next_frontier = temp_set;

    // Step 6.6: empty next frontier and popped call graph nodes
    symbol_free_scope(symbol_get_scope(next_frontier));
    symbol_free_scope(symbol_get_scope(popped_states));

    current_level++;

#if defined(AH_RECOGNISER_TRACE)
        text_printf("\nAfter moving to next frontier - call graph:\n");
        ah_show_call_graph(call_graph, this_table);

        text_printf("\nAfter moving to next frontier - current frontier:\n");
        ah_show_process_set(current_frontier, this_table);

        text_printf("\nAfter moving to next frontier - next frontier:\n");
        ah_show_popped_states_set(popped_states, this_table);

        text_printf("\nAfter moving to next frontier - pending:\n");
        ah_show_process_set(pending, this_table);
#endif
  }

  //Step 7: Check for acceptance
#if defined(AH_RECOGNISER_TRACE)
  text_printf("After exit from lexer loop, current_level = %i, last_lexeme = %i (%s), accepted = %i\n",
              current_level,
              last_lexeme,
              gram_find_symbol_by_number(this_table->grammar, last_lexeme)->id,
              accepted);
#endif
  if (last_lexeme != GRAM_EOS_SYMBOL_NUMBER)
    accepted = 0;

  finish_time = clock();

  //Step 8: Delete symbol tables
  symbol_free_table(popped_states);
  symbol_free_table(current_frontier);
  symbol_free_table(next_frontier);
  symbol_free_table(pending);

  //Step 9: Output some statistics
  int nodes = 0;
  int edges = 0;

  double run_time = (double) (finish_time - start_time) / (double) CLK_TCK;

  text_printf("RIGLR recogniser ran in %lf CPU s\n", run_time);

  for (ah_call_graph_node *this_node = (ah_call_graph_node *) symbol_next_symbol_in_scope(symbol_get_scope(call_graph));
       this_node != NULL;
       this_node = (ah_call_graph_node *) symbol_next_symbol_in_scope(this_node))
  {
    nodes++;

    for (ah_call_graph_edge *this_edge = this_node->next_edge;
         this_edge != NULL;
         this_edge = this_edge->next_edge)
      edges++;
  }

  if (accepted)
    text_message(TEXT_INFO, "RIGLR recognise: accept\n");
  else
    text_message(TEXT_INFO, "RIGLR recognise: reject\n");

  text_printf("Call graph has %i node%s and %i edge%s\n", nodes, nodes == 1 ? "" : "s", edges, edges == 1 ? "" : "s");
  if(script_gtb_verbose())
  {
    text_printf("Cardinality of largest U_i: %i\n", max_cardinality);
    text_printf("Sum over the cardinalities of all U_i's: %i\n", sum_over_cardinalities);
    text_printf("Total pop/edge actions: %i\n", pop_actions);
    text_printf("Symbol\t\tshifts\treduces\tpushes\tpops\tcontingent pops\n");
    for (int symbol_number = 2; symbol_number < this_table->grammar->first_level_0_slot; symbol_number++)
      text_printf("%i\t%s\t%i\t%i\t%i\t%i\t%i\n", symbol_number,
      gram_find_symbol_by_number(this_table->grammar, symbol_number)->id,
      hist_bucket_value(shifts, symbol_number),
      hist_bucket_value(reductions, symbol_number),
      hist_bucket_value(pushes, symbol_number),
      hist_bucket_value(pops, symbol_number),
      hist_bucket_value(contingent_pops, symbol_number));

    text_printf("\nHistogram of pushes by absolute label of return state\n");
    hist_print(push_histogram);
  //  dump_parse_table(this_table);
  }

  return accepted;
}

