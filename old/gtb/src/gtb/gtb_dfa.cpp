/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 26 March 2003
*
* gtb_dfa.c - dfa construction functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#include <time.h>
#include <stdlib.h>
#include "graph.h"
#include "hist.h"
#include "memalloc.h"
#include "set.h"
#include "textio.h"
#include "util.h"
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

//#define DFA_TRACE
//#define REPORT_CLOSURES
//#define DFA_DUMP
//#define TBL_LOOKUP_TRACE

// Global variable by-passing what would be paramaters
static dfa *ret_dfa;

static unsigned *dfa_labels;
static unsigned *dfa_labels_top;
static unsigned *next_free_label_location;
static unsigned *dfa_tables;
static unsigned *dfa_tables_top;
static unsigned *next_free_table_location;
static unsigned *this_label_body;
static unsigned *current_input_label;
static unsigned first_slot;
static unsigned first_level_2_slot;
static unsigned first_level_3plus_slot;
static unsigned first_nonterminal;
static unsigned first_header;
static set_ *this_table_reductions;
static unsigned *slot_successor_symbol_index;
static unsigned *slot_successor_instance_number_index;
static unsigned *slot_successor_slot_number_index;
static nfa* current_nfa;
static set_ out_edges = SET_NULL;
static set_ visited = SET_NULL;
static unsigned **header_epsilon_closures;
static set_ *this_state_reductions;

//Horrible belt and braces check to make sure there's always a zero after a label!
#define DFA_LOAD_LABEL_ELEMENT(element) {next_free_label_location < dfa_labels_top ? *next_free_label_location++ = element : text_message(TEXT_FATAL, "ran out of dfa label space: try a larger value for paramater 3\n");}
//#define DFA_LOAD_LABEL_ELEMENT(element) {text_printf("DFA label: loaded %i at offset %u\n", element, next_free_label_location-dfa_labels); (next_free_label_location < dfa_labels_top ? *next_free_label_location++ = element : text_message(TEXT_FATAL, "dfa() ran out of label space: try a larger value for paramater 3\n")); *next_free_label_location = 0}

#define DFA_UNLOAD_LABEL_ELEMENT(element) {element = *current_input_label++; }
//#define DFA_UNLOAD_LABEL_ELEMENT(element) {element = *current_input_label++; text_printf("DFA: unloaded %i\n", element);}

#define DFA_LOAD_TABLE_ELEMENT(element) {if (table_buffer_size != 0) {next_free_table_location < dfa_tables_top ? *next_free_table_location++ = element : text_message(TEXT_FATAL, "ran out of dfa table space: try a larger value for paramater 2\n");}}
//#define DFA_LOAD_TABLE_ELEMENT(element) {text_printf("DFA table: loaded %i at offset %u\n", element, next_free_table_location-dfa_tables); next_free_table_location < dfa_tables_top ? *next_free_table_location++ = element : text_message(TEXT_FATAL, "dfa() ran out of table space: try a larger value for paramater 2\n");}

void calculate_header_epsilon_closure(header *this_header)
{
  if (set_includes_element(&visited, this_header->header_number))
    return;

  set_unite_element(&visited, this_header->header_number);

  unsigned *old_closure;

  if ((old_closure = header_epsilon_closures[this_header->header_number - current_nfa->grammar->first_header]) != NULL)
    while (*old_closure != SET_END)
      set_unite_element(&visited, *old_closure++);
  else
    if (this_header->nonterminal_symbol->level_1_nonterminal_instances != NULL)
      for (unsigned *this_level_1_nonterminal_instance = this_header->nonterminal_symbol->level_1_nonterminal_instances;
           *this_level_1_nonterminal_instance != SET_END;
           this_level_1_nonterminal_instance++)
        if (current_nfa->singleton_lookahead_sets)
          for (header** this_header_pointer_array_element = this_header->singleton_back_edges[*this_level_1_nonterminal_instance];
               *this_header_pointer_array_element != NULL;
               this_header_pointer_array_element++)
          calculate_header_epsilon_closure(*this_header_pointer_array_element);
        else
          calculate_header_epsilon_closure( this_header->back_edges[*this_level_1_nonterminal_instance]);
}

void collect_global_epsilon_closure_sub(header *target_header)
{
  set_unite_element(&visited, target_header->header_number);

#if defined(DFA_TRACE)
  text_printf("Successor header number is %u\n", target_header->header_number);
#endif

  unsigned *this_epsilon_closure_element = header_epsilon_closures[target_header->header_number - first_header];

  if (this_epsilon_closure_element != NULL)
    while (*this_epsilon_closure_element != SET_END)
    {
#if defined(DFA_TRACE)
      text_printf("Successor closure header number is %u\n", *this_epsilon_closure_element);
#endif
      set_unite_element(&visited, *this_epsilon_closure_element++);
    }
}

void collect_global_epsilon_closure(unsigned this_slot, unsigned this_header)
{
  unsigned this_successor_symbol = slot_successor_symbol_index[this_slot - first_slot];

#if defined(DFA_TRACE)
  text_printf("NFA state %u.%u has successor symbol %u (%s) and successor instance number %u\n", this_header, this_slot, this_successor_symbol, gram_find_symbol_by_number(current_nfa->grammar, slot_successor_symbol_index[this_slot - first_slot])->id, slot_successor_instance_number_index[this_slot - first_slot]);
#endif

  set_unite_element(&out_edges, this_successor_symbol);

  if (this_successor_symbol >= first_nonterminal)
  {
    if (current_nfa->singleton_lookahead_sets)
      for (header** this_header_pointer_array_element = current_nfa->header_index[this_header-first_header]->singleton_back_edges[slot_successor_instance_number_index[this_slot - first_slot]];
           *this_header_pointer_array_element != NULL;
           this_header_pointer_array_element++)
      collect_global_epsilon_closure_sub(*this_header_pointer_array_element);
    else
      collect_global_epsilon_closure_sub(current_nfa->header_index[this_header-first_header]->back_edges[slot_successor_instance_number_index[this_slot - first_slot]]);
  }
}

int compare_slot_header_pair(const void *left, const void *right)
{
  unsigned left_slot = *(unsigned *) left;
  unsigned left_header = *((unsigned *) left + 1);
  unsigned right_slot = *(unsigned *) right;
  unsigned right_header = *((unsigned *) right + 1);

  if (left_slot > right_slot)
    return 1;
  else if (left_slot < right_slot)
    return -1;
  else
  if (left_header > right_header)
    return 1;
  else if (left_header < right_header)
    return -1;
  else
    return 0;
}

void output_successor(unsigned this_symbol, unsigned this_slot, unsigned this_header)
{
  if (slot_successor_symbol_index[this_slot - first_slot] == this_symbol)
  {
    DFA_LOAD_LABEL_ELEMENT(slot_successor_slot_number_index[this_slot - first_slot]);
    DFA_LOAD_LABEL_ELEMENT(this_header);
  }
}


/* Find label in a well formed label buffer.

The label buffer must contain contiguous labels. No label after the
first one may be on symbol 0

The label to be found must have a symbol (zero will do) immediately
following.

We return the address of first location in the found symbol, or the found symbol itself if
not found
*/

int compare_labels(unsigned *left, unsigned *right)
{
#if defined(DFA_TRACE)
  text_printf("Comparing offset %u and offset %u\n", left - dfa_labels, right - dfa_labels);
#endif

  // First check for immediate equality: this will happen a lot in our application
  if (left == right)
  {
#if defined(DFA_TRACE)
    text_printf("Immediately identical\n");
#endif
    return 0;
  }

  // Now check opening symbol directly: again a useful optimisation but also important for later stage
  if (*left < *right)
  {
#if defined(DFA_TRACE)
    text_printf("Symbol less\n");
#endif
    return -1;
  }
  else if (*left > *right)
  {
#if defined(DFA_TRACE)
    text_printf("Symbol greater\n");
#endif
    return 1;
  }
  else
  {
    left++; right++;  // Now that we've etablished equality on the initial symbol. we need merely scan up to a symbol

    while (*left == *right && *left >= first_slot)
    {
#if defined(DFA_TRACE)
      text_printf("Testing offset %u and offset %u\n", left - dfa_labels, right - dfa_labels);
#endif
      left++; right++;
    }

    if (*left < first_slot && *right < first_slot)
    {
#if defined(DFA_TRACE)
      text_printf("Equal");
#endif
      return 0;
    }
    else
    {
#if defined(DFA_TRACE)
      text_printf("Not equal");
#endif
      return (*left < *right) ? -1 : 1;
    }
  }
}


unsigned *label_find_linear(unsigned *labels_start, unsigned* labels_one_past_end, unsigned *key)
{
  unsigned *test_element = labels_start;
  while (1)
  {
#if defined(DFA_TRACE)
    text_printf("Start at test_element offset %u, key offset %u\n", test_element-labels_start, key - labels_start);
#endif

    if (test_element >= labels_one_past_end)    // reached end of buffer: return key
    {
#if defined(DFA_TRACE)
      text_printf("Buffer exhausted: key not found\n");
#endif
      return key;
    }

    if (compare_labels(key, test_element) == 0)
      return test_element;
    else
      do
      {
#if defined(DFA_TRACE)
        text_printf("Skipping test_element offset %u\n", test_element - dfa_labels);
#endif
        test_element++;
      }
      while  (*test_element >= first_slot);
  }
}

unsigned **hash_table;
unsigned hash_buckets;
unsigned hash_prime;

unsigned hash_label(unsigned hash_seed, unsigned *label)
{
  hash_seed = *label++ + hash_prime * hash_seed; // Hash in the initial symbol

  while (*label >= first_slot)        // run up the label until we reach the followingsymbol
    hash_seed = * label++ + hash_prime * hash_seed;

  return hash_seed;
}

unsigned *label_find_hash(unsigned *labels_start, unsigned* labels_one_past_end, unsigned *key)
{
  unsigned hash_index = hash_label(0, key);
  unsigned *hash_value = hash_table[hash_index %hash_buckets];


  while (hash_value != NULL && compare_labels(key, hash_value) != 0)
    hash_value = hash_table[++hash_index % hash_buckets];

  if (hash_value == NULL)
  {
    hash_table[hash_index % hash_buckets] = key;
    return key;
  }
  else
    return hash_value;
}

static void print_stats(unsigned *header_usage, unsigned *nonterminal_usage)
{
  text_printf("\nHeader usage\n");
  for (unsigned this_header_number = 0; this_header_number < current_nfa->header_count; this_header_number++)
    if (header_usage[this_header_number] > 0)
    {
      text_printf("%6u %s {", header_usage[this_header_number], current_nfa->header_index[this_header_number]->nonterminal_symbol->id);
      gram_print_set_of_symbols(current_nfa->grammar, current_nfa->header_index[this_header_number]->follow_set);
      text_printf("}\n");
    }

  text_printf("\nNonterminal usage\n");
  for (unsigned this_symbol_number = first_nonterminal; this_symbol_number < first_slot; this_symbol_number++)
    text_printf("%6u %s\n", nonterminal_usage[this_symbol_number], gram_find_symbol_by_number(current_nfa->grammar, this_symbol_number)->id);
}

static void add_to_reductions(unsigned slot, set_ *follow_set, grammar *this_gram) //if this_gram is NUL, don't unwrap
{
  unsigned *follow_elements;

  if (this_gram != NULL)  //unwrap
  {
    set_ unwrapped_follow_set = SET_NULL;

    set_assign_set(&unwrapped_follow_set, follow_set);
    gram_unwrap_nonterminal_follow_set(this_gram, &unwrapped_follow_set);

    set_difference_set(&unwrapped_follow_set, &this_gram->nonterminals);
    follow_elements = set_array(&unwrapped_follow_set);
    set_free(&unwrapped_follow_set);
  }
  else
    follow_elements = set_array(follow_set);

  for (unsigned *this_follow_element = follow_elements; *this_follow_element != SET_END; this_follow_element++)
    set_unite_element(&this_state_reductions[*this_follow_element], slot);

  mem_free(follow_elements);
}

dfa *dfa_construct_dfa(nfa *this_nfa, unsigned table_buffer_size, unsigned label_buffer_size, unsigned hash_buckets_value, unsigned hash_prime_value)
{
  first_slot = this_nfa->grammar->first_level_0_slot;
  first_nonterminal = this_nfa->grammar->first_nonterminal;
  first_header = this_nfa->grammar->first_header;
  first_level_2_slot = this_nfa->grammar->first_level_2_slot;
  first_level_3plus_slot = this_nfa->grammar->first_level_3plus_slot;
  if (this_nfa->nullable_reductions)
    this_table_reductions = &this_nfa->grammar->right_nullable_reductions;
  else
    this_table_reductions = &this_nfa->grammar->reductions;
  unsigned this_input_label_symbol;
  unsigned this_slot;
  unsigned this_header;
  current_nfa = this_nfa;
  unsigned processed_state_count = 0;
  unsigned checked_states = 0;
  unsigned *reduction_set_array;

  if (table_buffer_size == UINT_MAX)
    table_buffer_size = 10000000;

  if (label_buffer_size == 0)
    label_buffer_size = 10000000;

  if (hash_buckets_value == 0)
    hash_buckets = 999983;
  else
    hash_buckets = hash_buckets_value;

  if (hash_prime_value == 0)
    hash_prime  = 999979;
  else
    hash_prime = hash_prime_value;

  if (script_gtb_verbose())
    text_printf("\nNFA has %lu headers\n", this_nfa->header_count);

  // Initialisation step 1: create the DFA
  ret_dfa =(dfa *) mem_calloc(1, sizeof(dfa));
  ret_dfa->nfa = this_nfa;
  ret_dfa->grammar = this_nfa->grammar;

  // Initialisation step 2: create the labels array the table array and initialise associated pointers
  dfa_labels = next_free_label_location = current_input_label = (unsigned*) mem_calloc(label_buffer_size, sizeof(unsigned));
  dfa_labels_top = dfa_labels + label_buffer_size;
  if (script_gtb_verbose())
    text_printf("Allocated %lu words for dfa labels\n", dfa_labels_top - dfa_labels);

  if (table_buffer_size == 0)
  {
    dfa_tables = NULL;
    if (script_gtb_verbose())
      text_printf("DFA table generation suppressed\n");
  }
  else
  {
    dfa_tables = next_free_table_location = (unsigned*) mem_calloc(table_buffer_size, sizeof(unsigned));
    dfa_tables_top = dfa_tables + table_buffer_size;
    if (script_gtb_verbose())
      text_printf("Allocated %lu words for dfa tables\n", dfa_tables_top - dfa_tables);

    //Now make reduction table
    reduction_set_array = set_array(this_table_reductions);
    ret_dfa->reduction_count = set_cardinality(this_table_reductions);
    ret_dfa->reduction_table = (reduction*) mem_calloc(ret_dfa->reduction_count, sizeof(reduction));
    ret_dfa->reductions_index = (unsigned*) mem_calloc(ret_dfa->reduction_count, sizeof(unsigned));

    for (unsigned reduction_index = 0, *this_reduction_set_element = reduction_set_array; *this_reduction_set_element != SET_END; this_reduction_set_element++, reduction_index++)
    {
      gram_node *this_slot = (gram_node*) graph_node_index(this_nfa->grammar->rules)[*this_reduction_set_element];

      // count backwards to symbol
      while (graph_next_in_edge(this_slot) != NULL)
      {
        this_slot = (gram_node*) graph_source(graph_next_in_edge(this_slot));
        ret_dfa->reduction_table[reduction_index].length++;
      }

      ret_dfa->reduction_table[reduction_index].length--;
      ret_dfa->reduction_table[reduction_index].symbol_number = this_slot->symbol_table_entry->symbol_number;

      if (this_nfa->nullable_reductions)
        ret_dfa->reduction_table[reduction_index].is_accepting = set_includes_element(&this_nfa->grammar->start_rule_right_nullable_reductions, *this_reduction_set_element);
      else
        ret_dfa->reduction_table[reduction_index].is_accepting = set_includes_element(&this_nfa->grammar->start_rule_reductions, *this_reduction_set_element);

      ret_dfa->reductions_index[reduction_index] = *this_reduction_set_element;
    }

#if defined(DFA_TRACE)
    for (unsigned reduction_index = 0, *this_reduction_set_element = reduction_set_array; *this_reduction_set_element != SET_END; this_reduction_set_element++, reduction_index++)
      text_printf("%u (%u) %u |%u|\n", reduction_index, *this_reduction_set_element, ret_dfa->reduction_table[reduction_index].symbol_number, ret_dfa->reduction_table[reduction_index].length);
#endif
  }

  //Initialisation step 3: create slot indices
  unsigned *header_usage = (unsigned *) mem_calloc(this_nfa->header_count, sizeof(unsigned));
  unsigned *nonterminal_usage = (unsigned *) mem_calloc(first_slot, sizeof(unsigned));

  slot_successor_symbol_index = (unsigned*) mem_calloc(first_header - first_slot, sizeof(unsigned));
  slot_successor_instance_number_index = (unsigned*) mem_calloc(first_header - first_slot, sizeof(unsigned));
  slot_successor_slot_number_index = (unsigned*) mem_calloc(first_header - first_slot, sizeof(unsigned));

  for (unsigned this_slot = 0; this_slot < first_header - first_slot; this_slot++)
  {
    gram_node* this_node = (gram_node*) graph_node_index(this_nfa->grammar->rules)[this_slot + first_slot];
    gram_edge* this_edge = (gram_edge*) graph_next_out_edge(this_node);

    if (this_edge != NULL)
    {
      slot_successor_symbol_index[this_slot] = this_edge->symbol_table_entry->symbol_number;
      slot_successor_instance_number_index[this_slot] = this_edge->instance_number;
      slot_successor_slot_number_index[this_slot] = graph_atom_number(graph_destination(this_edge));
    }
  }

  #if defined(DFA_TRACE)
  for (int this_slot = 0; this_slot < first_header - first_slot; this_slot++)
    text_printf("Slot %u has successor symbol %u, successor instance %u, successor slot %u\n", this_slot + first_slot, slot_successor_symbol_index[this_slot], slot_successor_instance_number_index[this_slot], slot_successor_slot_number_index[this_slot]);
  #endif

  //Initialisation step 4: create and compute header epsilon-closures
  header_epsilon_closures = (unsigned**) mem_calloc(this_nfa->header_count, sizeof(unsigned*));

  set_unite_element(&visited, this_nfa->header_count);  // resize set to maximum needed
  set_unite_element(&out_edges, first_slot);  // resize set to maximum needed

  for (unsigned this_header_number = 0; this_header_number< this_nfa->header_count; this_header_number++)
  {
    set_clear(&visited);
    calculate_header_epsilon_closure(this_nfa->header_index[this_header_number]);

    set_difference_element(&visited, this_nfa->header_index[this_header_number]->header_number);
    if (set_cardinality(&visited) != 0)
      header_epsilon_closures[this_header_number] = set_array(&visited);
  }

  //Initislisation step 5: report header epsilon-closures
  unsigned non_empty_sets = 0;
  unsigned sum_of_cardinalities = 0;

  for (unsigned this_header_number = 0; this_header_number< this_nfa->header_count; this_header_number++)
  {
    if (header_epsilon_closures[this_header_number] == NULL)
#if defined(REPORT_CLOSURES)
      text_printf("Header number %u has empty epsilon header closure\n", this_header_number + first_header)
#endif
      ;
    else
    {
      non_empty_sets++;

#if defined(REPORT_CLOSURES)
      text_printf("Header number %u has epsilon header closure {", this_header_number + first_header);
#endif

      for (unsigned *this_element = header_epsilon_closures[this_header_number]; *this_element != SET_END; this_element++)
      {
#if defined(REPORT_CLOSURES)
        text_printf(" %u", *this_element);
#endif
        sum_of_cardinalities++;
      }

#if defined(REPORT_CLOSURES)
      text_printf(" }\n");
#endif
    }
  }

    if (script_gtb_verbose())
      text_printf("There are %u non-empty header_epsilon_closures with a total of %u elements requiring %u bytes\n", non_empty_sets, sum_of_cardinalities, (this_nfa->header_count * sizeof(unsigned*)) +  (sum_of_cardinalities * sizeof(unsigned)));

  //Initialisation step 6: make hash table
  hash_table = (unsigned**) mem_calloc(hash_buckets, sizeof(unsigned*));
  if (script_gtb_verbose())
    text_printf("Allocated %lu entries for DFA hash table requiring %lu bytes\n", hash_buckets, hash_buckets * sizeof(unsigned*));

  //Initialisation step 6: make hash table
  this_state_reductions = (set_ *) mem_calloc(this_nfa->grammar->first_nonterminal, sizeof(set_));

  //Initialisation step 8: add S' ::= .S to the pending list
  DFA_LOAD_LABEL_ELEMENT(GRAM_ILLEGAL_SYMBOL_NUMBER);
  DFA_LOAD_LABEL_ELEMENT(graph_atom_number(graph_destination(graph_next_out_edge(this_nfa->start_header->nonterminal_symbol->rule_tree))));
  DFA_LOAD_LABEL_ELEMENT(this_nfa->start_header->header_number);
  ret_dfa->state_count++;

  // Worklist
  while (current_input_label != next_free_label_location)
  {
#if defined(DFA_TRACE)
    text_printf("current_input_label offset: %u  next_free_location offset: %u\n", current_input_label - dfa_labels, next_free_label_location - dfa_labels);
#endif

    // Worklist step 1: clear visited and out_edges sets
    set_clear(&visited);
    set_clear(&out_edges);
    for (unsigned symbol_number = GRAM_EPSILON_SYMBOL_NUMBER + 1; symbol_number < this_nfa->grammar->first_nonterminal; symbol_number++)
      set_clear(&(this_state_reductions[symbol_number]));

    // Worklist step 2: fetch first entry (which will be the symbol)
    DFA_UNLOAD_LABEL_ELEMENT(this_input_label_symbol);                                             // get the symbol for this state
    DFA_LOAD_TABLE_ELEMENT(this_input_label_symbol);                                                  //Output the symbol which marks the beginning of this state to tables array
    this_label_body = current_input_label;

    // Worklist step 3: walk the label elements building the global visited (epsilon_closure) set: adds immediate successor labels to edge_labels as a side effect
    set_clear(&visited);
    current_input_label = this_label_body;
    while (*current_input_label >= first_header)                                       // while this is a header
    {
      DFA_UNLOAD_LABEL_ELEMENT(this_header);
      header *actual_header = this_nfa->header_index[this_header - first_header];                                       // get the header
      unsigned *this_move_to_slot = actual_header->nonterminal_symbol->move_tos[this_input_label_symbol];

      if (this_move_to_slot != NULL)
        while (*this_move_to_slot != SET_END)
        {
          add_to_reductions(*this_move_to_slot, actual_header->follow_set, this_nfa->nonterminal_lookahead_sets ? this_nfa->grammar : NULL);
          collect_global_epsilon_closure(*this_move_to_slot++, this_header);
        }
    }

    while (*current_input_label > first_slot && *current_input_label < first_header)   // while this is a slot
    {
      DFA_UNLOAD_LABEL_ELEMENT(this_slot);                                             // get the slot
      DFA_UNLOAD_LABEL_ELEMENT(this_header);                                           // get the header
      header *actual_header = this_nfa->header_index[this_header - first_header];                                       // get the header
      add_to_reductions(this_slot, actual_header->follow_set, this_nfa->nonterminal_lookahead_sets ? this_nfa->grammar : NULL);

      collect_global_epsilon_closure(this_slot, this_header);
    }

    unsigned *global_epsilon_closure = set_array(&visited);

    //Worklist step 4: walk the global epsilon_closure adding out edge labels from the move_tos in the global closure
    for (unsigned *this_global_epsilon_closure_element = global_epsilon_closure; *this_global_epsilon_closure_element != SET_END; this_global_epsilon_closure_element++)
    {
      header *actual_header = this_nfa->header_index[*this_global_epsilon_closure_element - first_header];                                       // get the header

      unsigned **move_tos = actual_header->nonterminal_symbol->move_tos;

      //Side effect: add leaders to reduction set
      if (move_tos[GRAM_EPSILON_SYMBOL_NUMBER] != NULL)
        for (unsigned *this_move_to = move_tos[GRAM_EPSILON_SYMBOL_NUMBER]; *this_move_to != SET_END; this_move_to++)
          add_to_reductions(*this_move_to, actual_header->follow_set, this_nfa->nonterminal_lookahead_sets ? this_nfa->grammar : NULL);

      for (unsigned this_symbol = GRAM_EPSILON_SYMBOL_NUMBER + 1; this_symbol < first_slot; this_symbol++)
        if (move_tos[this_symbol] != NULL)
          set_unite_element(&out_edges, this_symbol);
    }

    //Worklist step 4a: write out reductions
    for (unsigned symbol_number = GRAM_EPSILON_SYMBOL_NUMBER + 1; symbol_number < this_nfa->grammar->first_nonterminal; symbol_number++)
    {
      set_intersect_set(&this_state_reductions[symbol_number], this_table_reductions);

      if (set_cardinality(&this_state_reductions[symbol_number]) != 0)
      {
        unsigned *reductions_array = set_array(&this_state_reductions[symbol_number]);

        for (unsigned *this_reduction_number = reductions_array; *this_reduction_number != SET_END; this_reduction_number++)
          DFA_LOAD_TABLE_ELEMENT(*this_reduction_number);

        mem_free(reductions_array);

        DFA_LOAD_TABLE_ELEMENT(symbol_number);
      }
    }


    set_difference_element(&out_edges, GRAM_ILLEGAL_SYMBOL_NUMBER);  // Special case: NFA states with no successor show succ label 0
    unsigned *out_edge_labels = set_array(&out_edges);

    // Worklist step 5: iterate over the out-edge labels, constructing output states
    for (unsigned *this_out_edge_label = out_edge_labels; *this_out_edge_label != SET_END; this_out_edge_label++)
    {
#if defined(REPORT_CLOSURES)
      text_printf("Out edge labelled %u (%s)\n", *this_out_edge_label, gram_find_symbol_by_number(this_nfa->grammar, *this_out_edge_label)->id);
#endif

     unsigned *current_output_label = next_free_label_location;

     DFA_LOAD_LABEL_ELEMENT(*this_out_edge_label);        //Output the symbol which marks the beginning of this state to labels array

     // Walk global epsilon closure, outputting those headers that have a non-empty move_to on the current out edge label symbol
     for (unsigned *this_global_epsilon_closure_element = global_epsilon_closure; *this_global_epsilon_closure_element != SET_END; this_global_epsilon_closure_element++)
        if (this_nfa->header_index[*this_global_epsilon_closure_element - first_header]->nonterminal_symbol->move_tos[*this_out_edge_label] != NULL)
          DFA_LOAD_LABEL_ELEMENT(*this_global_epsilon_closure_element);

      //Worklist step 6: walk the input label looking for successor slots on this symbol
      unsigned *first_slot_header_pair = next_free_label_location;                             // remember start of this block for subsequent sorting

      current_input_label = this_label_body;
      while (*current_input_label >= first_header)                                       // while this is a header
      {
        DFA_UNLOAD_LABEL_ELEMENT(this_header);                                           // get the header
        unsigned *this_move_to_slot = this_nfa->header_index[this_header - first_header]->nonterminal_symbol->move_tos[this_input_label_symbol];

        if (this_move_to_slot != NULL)
          while (*this_move_to_slot != SET_END)
            output_successor(*this_out_edge_label, *this_move_to_slot++, this_header);
      }

      while (*current_input_label >= first_slot && *current_input_label < first_header)   // while this is a slot
      {
        DFA_UNLOAD_LABEL_ELEMENT(this_slot);                                             // get the slot
        DFA_UNLOAD_LABEL_ELEMENT(this_header);                                           // get the header
        output_successor(*this_out_edge_label, this_slot, this_header);
      }

      //Worklist step 6: sort slot/header pairs into ascending order to produce a canonical form for searching
      if ((next_free_label_location - first_slot_header_pair) > 2)
        qsort(first_slot_header_pair, (next_free_label_location - first_slot_header_pair) / 2, 2 * sizeof(unsigned), compare_slot_header_pair);

      //Worklist step 7: search all previous states to see if this newly created state is already in the labels buffer
      if (ret_dfa->state_count >= hash_buckets)
        text_message(TEXT_FATAL, "DFA builder ran out of hash buckets\n");

      unsigned *found_label = label_find_hash(dfa_labels, next_free_label_location, current_output_label);

      if (found_label == current_output_label)
      {
#if defined(DFA_TRACE)
        text_printf("No match\n");
#endif

        ret_dfa->state_count++;
        //update header_usage
        for (unsigned *this_label_element = current_output_label + 1; this_label_element < next_free_label_location && *this_label_element >= first_header; this_label_element++)
          if (*this_label_element >= first_header)
          {
            header_usage[*this_label_element - first_header]++;
            nonterminal_usage[current_nfa->header_index[*this_label_element - first_header]->nonterminal_symbol->symbol_number]++;
          }
      }
      else
      {
#if defined(DFA_TRACE)
        text_printf("Match\n");
#endif
        memset(current_output_label, 0, (next_free_label_location - current_output_label) * sizeof(unsigned));  // We have a match: overwrite this label
        next_free_label_location = current_output_label;
      }

      DFA_LOAD_TABLE_ELEMENT((unsigned) found_label);  // Put out edge into table

      if (script_gtb_verbose() && ++checked_states % 1000 == 0)
        text_printf("Checked %u states and created %u states of which %u processed (convergence %lf); %u bytes of memory consumed, %u bytes left, %u hash slots left\n", checked_states, ret_dfa->state_count, processed_state_count, (double) processed_state_count / (double) ret_dfa->state_count, next_free_label_location - dfa_labels, label_buffer_size - (next_free_label_location - dfa_labels), hash_buckets - ret_dfa->state_count);

#if 0
      if (checked_states % 1000000 == 0)
        print_stats(header_usage, nonterminal_usage);
#endif
    }

    mem_free(global_epsilon_closure);
    mem_free(out_edge_labels);

#if defined(DFA_TRACE)
    text_printf("Finished state label\n--------------\n");
#endif
    processed_state_count++;
  }

  if (script_gtb_verbose())
    print_stats(header_usage, nonterminal_usage);

  // Wrap up step 1: free hash table */
  mem_free(hash_table);

  // Wrap up step 1: free visited set */
  set_free(&visited);
  set_free(&out_edges);

  // Wrap up step 2: resize labels and tables arrays
  ret_dfa->labels = dfa_labels; //(unsigned*) mem_realloc(dfa_labels, next_free_label_location - dfa_labels);
  ret_dfa->tables = dfa_tables; //(unsigned*) mem_realloc(dfa_tables, next_free_table_location - dfa_tables);

  // Wrap up step 3: make labels and tables indices
  if (dfa_tables != NULL)
  {
    ret_dfa->labels_index = (unsigned**) mem_calloc(ret_dfa->state_count, sizeof (unsigned*));
    for (unsigned state_number = 0, *this_label_element_pointer = dfa_labels; state_number < ret_dfa->state_count; state_number++)
    {
      ret_dfa->labels_index[state_number] = this_label_element_pointer;

#if defined(DFA_TRACE)
      text_printf("label_index[%i] = %p\n", state_number, ret_dfa->labels_index[state_number]);
#endif

      do
      {
        this_label_element_pointer++;
      }
      while (*this_label_element_pointer >=  ret_dfa->grammar->first_level_0_slot);

    }

    ret_dfa->tables_index = (unsigned**) mem_calloc(ret_dfa->state_count, sizeof (unsigned*));
    for (unsigned state_number = 0, *this_table_element_pointer = dfa_tables; state_number < ret_dfa->state_count; state_number++)
    {
      ret_dfa->tables_index[state_number] = this_table_element_pointer;

#if defined(DFA_TRACE)
      text_printf("table_index[%i] = %p\n", state_number, ret_dfa->tables_index[state_number]);
#endif

      this_table_element_pointer++;  //Skip symbol

      while (*this_table_element_pointer >=  this_nfa->grammar->first_level_0_slot && *this_table_element_pointer <=  this_nfa->grammar->first_header)
      {
        while (*this_table_element_pointer >=  this_nfa->grammar->first_level_0_slot && *this_table_element_pointer <=  this_nfa->grammar->first_header)
          this_table_element_pointer++;  //Skip reduction

        this_table_element_pointer++;  //Skip lookahead symbol
      }

      //Now the shifts
      while (*this_table_element_pointer >=  this_nfa->grammar->first_level_0_slot)
        this_table_element_pointer++;

    }
  }

  //Wrap up step 4: rework table pointers to use table state numbers rather than pointers into labels, and to use header table indices for reductions
  #if defined(DFA_TRACE)
        text_printf("Table dump\n");
  #endif
  if (dfa_tables != NULL)
  {
    for (unsigned state_number = 0, *this_label_element_pointer = dfa_tables; state_number < ret_dfa->state_count; state_number++)
    {
  #if defined(DFA_TRACE)
        text_printf("state %i, symbol %u\n", state_number, *this_label_element_pointer);
  #endif

      this_label_element_pointer++;  // Skip symbol

      while (*this_label_element_pointer >=  ret_dfa->grammar->first_level_0_slot && *this_label_element_pointer <=  ret_dfa->grammar->first_header)
      {
        while (*this_label_element_pointer >=  ret_dfa->grammar->first_level_0_slot && *this_label_element_pointer <=  ret_dfa->grammar->first_header)
        {
#if defined(DFA_TRACE)
          text_printf("R%u ", *this_label_element_pointer);
#endif
          *this_label_element_pointer = utl_iterative_bsearch_unsigned((unsigned*) ret_dfa->reductions_index, 0, ret_dfa->reduction_count, *this_label_element_pointer) + ret_dfa->grammar->first_level_0_slot;

          if ((int) (*this_label_element_pointer) < 0)
            text_message(TEXT_FATAL, "internal error: DFA reduction index not found\n");

          this_label_element_pointer++;
        }

#if defined(DFA_TRACE)
        text_printf("{%u}\n", *this_label_element_pointer);
#endif
        this_label_element_pointer++;
      }

      while (*this_label_element_pointer >=  ret_dfa->grammar->first_level_0_slot) //While not a symbol
      {
        int offset = utl_iterative_bsearch_unsigned((unsigned*) ret_dfa->labels_index, 0, ret_dfa->state_count, (unsigned) *this_label_element_pointer);

  #if defined(DFA_TRACE)
        text_printf("out-edge %p (%i)", *this_label_element_pointer, offset);
  #endif

        if (offset < 0)
          text_message(TEXT_FATAL, "internal error: DFA label index not found\n");

        *this_label_element_pointer = offset + this_nfa->grammar->first_dfa_state;

        this_label_element_pointer++;
      }
    }
  }

  if (script_gtb_verbose())
  {
    text_printf("\nChecked %u states and created %u states of which %u processed (convergence %lf); %u bytes of memory consumed, %u bytes left, %u hash slots left\n", checked_states, ret_dfa->state_count, processed_state_count, (double) processed_state_count / (double) ret_dfa->state_count, next_free_label_location - dfa_labels, label_buffer_size - (next_free_label_location - dfa_labels), hash_buckets - ret_dfa->state_count);
    text_printf("Built %srolled DFA with %s lookahead, %s reductions: %u states\n\n",
                ret_dfa->nfa->unrolled ? "un" : "",
                ret_dfa->nfa->lookahead == 0 ? "no" : ret_dfa->nfa->lookahead > 0 ? "rhs (lr)" : "lhs (slr)",
                ret_dfa->nfa->nullable_reductions? "nullable" : "normal", ret_dfa->state_count);
  }

#if defined(DFA_DUMP)
  text_printf("\nDFA labels index\n");
  for (int i = 0; i < ret_dfa->state_count; i++)
    text_printf("%i (%i) %p\n", i, i + ret_dfa->grammar->first_dfa_state, ret_dfa->labels_index[i]);

  text_printf("\nDFA labels\n");
  for (int i = 0; i < ret_dfa->state_count; i++)
  {
    text_printf("%p (%i) %u\n", ret_dfa->labels_index[i], i + ret_dfa->grammar->first_dfa_state, *ret_dfa->labels_index[i]);
    for (unsigned *label_element_pointer = ret_dfa->labels_index[i] + 1; *label_element_pointer >= ret_dfa->grammar->first_level_0_slot; label_element_pointer++)
      text_printf("%p %u\n", label_element_pointer, *label_element_pointer);

  }

  text_printf("\nDFA tables index\n");
  for (int i = 0; i < ret_dfa->state_count; i++)
    text_printf("%i (%i) %p\n", i, i + ret_dfa->grammar->first_dfa_state, ret_dfa->tables_index[i]);

  text_printf("\nDFA table\n");
  for (int i = 0; i < ret_dfa->state_count; i++)
  {
    text_printf("%p (%i) %u\n", ret_dfa->tables_index[i], i + ret_dfa->grammar->first_dfa_state, *ret_dfa->tables_index[i]);
    for (unsigned *table_element_pointer = ret_dfa->tables_index[i] + 1; *table_element_pointer >= ret_dfa->grammar->first_level_0_slot; table_element_pointer++)
      text_printf("%p %u\n", table_element_pointer, *table_element_pointer);

  }
#endif

  return ret_dfa;
}

void dfa_write_label_element(dfa* this_dfa, unsigned this_slot_number, unsigned this_header_number)
{
  text_printf("\n%u.%u: ", this_slot_number, this_header_number);
  nfa_print_nfa_state(this_dfa->nfa, this_header_number, this_slot_number, 1);
}

void dfa_write(FILE *output_file, dfa *this_dfa)
{
  FILE * old_text_stream = text_stream();

  text_redirect(output_file);

  if (output_file == NULL)
    text_message(TEXT_FATAL, "unable to open dfa_write output file for write\n");

  text_redirect(output_file);

  text_printf("LR symbol table\n");

  for (gram_symbols_data *this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_dfa->grammar->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    text_printf("%u %s\n", this_symbol->symbol_number, this_symbol->id);

  text_printf("\nLR state table");

  for (unsigned state_number = 0; state_number < this_dfa->state_count; state_number++)
  {
    unsigned *this_table_element_pointer = this_dfa->tables_index[state_number];

    text_printf("\n%u %u: ", state_number + this_dfa->grammar->first_dfa_state, *this_table_element_pointer);

    this_table_element_pointer++;                           // skip symbol element in tables

    //Now get the reductions out of the table
    while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot && *this_table_element_pointer <=  this_dfa->grammar->first_header)
    {
      text_printf("\n");

      while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot && *this_table_element_pointer <=  this_dfa->grammar->first_header)
      {
        text_printf(" R[%u]", *this_table_element_pointer - this_dfa->grammar->first_level_0_slot);
        this_table_element_pointer++;
      }

      text_printf(" {%u}", *this_table_element_pointer);
      this_table_element_pointer++;
    }

    //Now the edges
    while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot)
    {
      text_printf("\n S%u", *this_table_element_pointer);
      this_table_element_pointer++;
    }
  }

  text_printf("\n\nLR reduction table\n");
  for (unsigned reduction_number = 0; reduction_number < this_dfa->reduction_count; reduction_number++)
  {
    text_printf("R[%u] R%u |%u|->%u ",
                reduction_number,
                this_dfa->reductions_index[reduction_number],
                this_dfa->reduction_table[reduction_number].length,
                this_dfa->reduction_table[reduction_number].symbol_number
               );
    gram_print_slot_by_number(this_dfa->grammar, this_dfa->reductions_index[reduction_number], 0);

    if (this_dfa->reduction_table[reduction_number].is_accepting)
      text_printf("Accepting\n");
    else
      text_printf("\n");
  }

  text_redirect(old_text_stream);
}

void dfa_render(FILE *output_file, dfa *this_dfa)
{
  FILE * old_text_stream = text_stream();

  unsigned dfa_terminal_edges = 0;
  unsigned dfa_nonterminal_edges = 0;
  unsigned dfa_reduction_items = 0;
  unsigned dfa_reductions = 0;

  text_redirect(output_file);
  text_printf("graph:{\n"
              "orientation:top_to_bottom\n"
              "edge.arrowsize:7\n"
              "edge.thickness:1\n"
              "display_edge_labels:yes\n"
              "arrowmode:free\n"
              "node.borderwidth:1\n");

  for (unsigned state_number = 0; state_number < this_dfa->state_count; state_number++)
  {
    bool is_reducing = false;
    bool is_accepting = false;

    unsigned *this_label_element_pointer = this_dfa->labels_index[state_number];
    unsigned *this_table_element_pointer = this_dfa->tables_index[state_number];

    text_printf("node:{title:\"%u\" label:\"%u:", state_number + this_dfa->grammar->first_dfa_state, state_number + this_dfa->grammar->first_dfa_state);

    unsigned this_symbol = *this_label_element_pointer++;   // skip symbol element in labels
    if (this_table_element_pointer != NULL)
      this_table_element_pointer++;                           // skip symbol element in tables

    while (*this_label_element_pointer >= this_dfa->grammar->first_header)
    {
      unsigned *this_move_to_slot = this_dfa->nfa->header_index[*this_label_element_pointer- first_header]->nonterminal_symbol->move_tos[this_symbol];

      while (*this_move_to_slot != SET_END)
        dfa_write_label_element(this_dfa, *this_move_to_slot++, *this_label_element_pointer);

      this_label_element_pointer++;
    }

    while (*this_label_element_pointer >= this_dfa->grammar->first_level_0_slot && *this_label_element_pointer < this_dfa->grammar->first_header)
    {
      if (GRAM_IS_HEADER(this_dfa->grammar, *(this_label_element_pointer+1)))
      {
        dfa_write_label_element(this_dfa, *this_label_element_pointer, *(this_label_element_pointer+1));
        this_label_element_pointer += 2;
      }
      else
        dfa_write_label_element(this_dfa, *this_label_element_pointer++, 0);
    }

    //Now get the reductions out of the table
    if (this_table_element_pointer != NULL)
      while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot && *this_table_element_pointer <  this_dfa->grammar->first_header)
      {
        text_printf("\n");

        while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot && *this_table_element_pointer <  this_dfa->grammar->first_header)
        {
          // Look up to see if this reduction is accepting

          is_accepting = this_dfa->reduction_table[*this_table_element_pointer - this_dfa->grammar->first_level_0_slot].is_accepting;

          dfa_reduction_items++;
          text_printf(" \f01R%u\f31 ", this_dfa->reductions_index[*this_table_element_pointer - this_dfa->grammar->first_level_0_slot]);
          is_reducing = true;
          this_table_element_pointer++;
        }

        text_printf("{%s}", gram_find_symbol_by_number(this_dfa->grammar, *this_table_element_pointer)->id);
        this_table_element_pointer++;
      }

    text_printf("\"");

    if (is_accepting)
      text_printf(" bordercolor:cyan borderwidth:3");
    else if (is_reducing)
      text_printf(" bordercolor:blue borderwidth:3");

    text_printf("}\n");


    //Now the edges
    if (this_table_element_pointer != NULL)
    {
      while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot)
      {
        int out_symbol = *(this_dfa->tables_index[(*this_table_element_pointer) - this_dfa->grammar->first_dfa_state]);
        text_printf("edge:{sourcename:\"%i\" targetname:\"%i\" label:\"", state_number + this_dfa->grammar->first_dfa_state, *this_table_element_pointer);
        gram_symbols_data *this_print_symbol = gram_find_symbol_by_number(this_dfa->grammar, out_symbol);
        gram_print_symbol_id(this_print_symbol);
        this_table_element_pointer++;
        text_printf("\"}\n");
        if (this_print_symbol->name_space == SCRIPT_NS_TERMINAL)
          dfa_terminal_edges++;
        else
          dfa_nonterminal_edges++;
      }
    }
  }

  text_printf("}\n");
  text_redirect(old_text_stream);

  if (script_gtb_verbose())
  {
    text_printf("Rendered %srolled DFA %swith %s lookahead, %s reductions: ",
                this_dfa->nfa->unrolled ? "un" : "",
                this_dfa->merged ? "(merged) " : "",
                this_dfa->nfa->lookahead == 0 ? "no" : this_dfa->nfa->lookahead > 0 ? "rhs (lr)" : "lhs (slr)",
                this_dfa->nfa->nullable_reductions? "nullable" : "normal");
    text_printf("%u state%s, %u terminal edge%s, %u nonterminal edge%s, %u reduction item%s and %u individual reduction%s\n",
                ret_dfa->state_count, ret_dfa->state_count == 1 ? "" : "s",
                dfa_terminal_edges, dfa_terminal_edges == 1 ? "" : "s",
                dfa_nonterminal_edges, dfa_nonterminal_edges == 1 ? "" : "s",
                dfa_reduction_items, dfa_reduction_items == 1 ? "" : "s",
                dfa_reductions, dfa_reductions == 1 ? "" : "s");
  }
}

// ********** Action access functions below here
// Count the actions, and return the shift if there is one
static int dfa_count_actions(dfa *this_dfa, unsigned state, unsigned symbol, int *shift_action)
{
  int action_count = 0;

  #if defined(TBL_LOOKUP_TRACE)
   text_printf("Counting actions for state %u symbol %u\n", state, symbol);
  #endif

  unsigned *this_table_element_pointer = this_dfa->tables_index[state - this_dfa->grammar->first_dfa_state];

  #if defined(TBL_LOOKUP_TRACE)
    text_printf("State %u has in-edge symbol %u\n", state, *this_table_element_pointer);
  #endif

  this_table_element_pointer++;                           // skip symbol element in tables

  //Now check the reductions
  while (GRAM_IS_SLOT(this_dfa->grammar, *this_table_element_pointer))
  {
    unsigned reduction_count = 0;

    while (GRAM_IS_SLOT(this_dfa->grammar, *this_table_element_pointer))
    {
  #if defined(TBL_LOOKUP_TRACE)
    text_printf("Scanned reduction R%u \n", *this_table_element_pointer);
  #endif
      this_table_element_pointer++;
      reduction_count++;
    }

  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Lookahead symbol is {%u}\n", *this_table_element_pointer);
  #endif

    if (symbol == *this_table_element_pointer)
    {
       action_count+=reduction_count;

  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Action_count updated to %i\n", action_count);
  #endif
    }
    this_table_element_pointer++;
  }

  //Now the edges
  while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot)
  {
#if defined(TBL_LOOKUP_TRACE)
     text_printf("Scanned shift %u on lookahead symbol %u\n",
                 *this_table_element_pointer,
                 *(this_dfa->tables_index[*this_table_element_pointer - this_dfa->grammar->first_dfa_state]));
#endif

    if (symbol == *(this_dfa->tables_index[*this_table_element_pointer - this_dfa->grammar->first_dfa_state]))
    {
      action_count++;
      *shift_action = *this_table_element_pointer;

// Could optimise here by making immediate return

    #if defined(TBL_LOOKUP_TRACE)
      text_printf("Incremented action_count to %i\n", action_count);
    #endif
    }

    this_table_element_pointer++;
  }

  return action_count;
}

int *dfa_retrieve_all_actions(dfa *this_dfa, unsigned state, unsigned symbol)
{
  #if defined(TBL_LOOKUP_TRACE)
   text_printf("Lookup all actions for state %u symbol %u\n", state, symbol);
  #endif

  int shift_action = 0;
  int action_count = dfa_count_actions(this_dfa, state, symbol, &shift_action);

  #if defined(TBL_LOOKUP_TRACE)
   text_printf("Action count for state %u symbol %u is %i\n", state, symbol, action_count);
  #endif

  int *return_actions = (int*) mem_calloc(action_count+1, sizeof(int));
  int *this_return_action = return_actions;
  unsigned *this_table_element_pointer = this_dfa->tables_index[state - this_dfa->grammar->first_dfa_state];

  if (shift_action != 0)
    *this_return_action++ = shift_action;

  #if defined(TBL_LOOKUP_TRACE)
    text_printf("Return actions updated with shift action%i\n", shift_action);
  #endif

  #if defined(TBL_LOOKUP_TRACE)
    text_printf("State %u has in-edge symbol %u\n", state, *this_table_element_pointer);
  #endif

  this_table_element_pointer++;                           // skip symbol element in tables

  //Now check the reductions
  while (*this_table_element_pointer >=  this_dfa->grammar->first_level_0_slot && *this_table_element_pointer <= this_dfa->grammar->first_header)
  {
    unsigned* first_reduction_pointer = this_table_element_pointer;

    while (*this_table_element_pointer >= this_dfa->grammar->first_level_0_slot && *this_table_element_pointer <= this_dfa->grammar->first_header)
    {
  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Scanned reduction R%u \n", *this_table_element_pointer);
  #endif
      this_table_element_pointer++;
    }

  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Lookahead symbol is {%u}\n", *this_table_element_pointer);
  #endif

    if (symbol == *this_table_element_pointer)
    {
       memcpy(this_return_action, first_reduction_pointer, sizeof(unsigned) * (this_table_element_pointer - first_reduction_pointer));

  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Return actions updated with %i reductions\n",this_table_element_pointer - first_reduction_pointer);
  #endif

      this_return_action++;
    }

    this_table_element_pointer++;
  }

  return return_actions;
}

int dfa_retrieve_first_action(dfa *this_dfa, unsigned state, unsigned symbol)
{
  int return_action = 0;   // Error

  #if defined(TBL_LOOKUP_TRACE)
   text_printf("Lookup one action for state %u symbol %u\n", state, symbol);
  #endif

  unsigned *this_table_element_pointer = this_dfa->tables_index[state - this_dfa->grammar->first_dfa_state];

  #if defined(TBL_LOOKUP_TRACE)
   text_printf("State %u has in-edge symbol %u\n", state, *this_table_element_pointer);
  #endif

  this_table_element_pointer++;                           // skip symbol element in tables

  //Now check the reductions
  while (GRAM_IS_SLOT(this_dfa->grammar, *this_table_element_pointer))
  {
    unsigned first_reduction_of_this_block = *this_table_element_pointer;

    while (GRAM_IS_SLOT(this_dfa->grammar, *this_table_element_pointer))
    {
  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Scanned reduction R%u \n", *this_table_element_pointer);
  #endif
      this_table_element_pointer++;
    }

  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Lookahead symbol is {%u}\n", *this_table_element_pointer);
  #endif

    if (symbol == *this_table_element_pointer && return_action == 0)
    {
      return_action = first_reduction_of_this_block;
  #if defined(TBL_LOOKUP_TRACE)
       text_printf("Return action updated with R[%u]\n", return_action);
  #endif
    }

    this_table_element_pointer++;
  }

  //Now the edges
  while (GRAM_IS_DFA_STATE(this_dfa->grammar, *this_table_element_pointer))
  {
#if defined(TBL_LOOKUP_TRACE)
     text_printf("Scanned shift %u on lookahead symbol %u\n",
                 *this_table_element_pointer,
                 *(this_dfa->tables_index[*this_table_element_pointer - this_dfa->grammar->first_dfa_state]));
#endif

    if (symbol == *(this_dfa->tables_index[*this_table_element_pointer - this_dfa->grammar->first_dfa_state]))
    {
      return_action = *this_table_element_pointer;

    #if defined(TBL_LOOKUP_TRACE)
      text_printf("Return action updated with S%u\n", return_action);
    #endif
    }

    this_table_element_pointer++;
  }

  return return_action;
}

struct lookahead_set_element
{
  unsigned item_number;
  set_ lookahead;
};

struct merged_state_element
{
  set_ id;
  unsigned state_number;

};

char grammar_element_kind(grammar *this_gram, unsigned element)
{
  if (GRAM_IS_TERMINAL(this_gram, element))
    return 'T';

  if (GRAM_IS_NONTERMINAL(this_gram, element))
    return 'N';

  if (GRAM_IS_SLOT(this_gram, element))
    return 'S';

  if (GRAM_IS_HEADER(this_gram, element))
    return 'H';

  if (GRAM_IS_DFA_STATE(this_gram, element))
    return 'D';

  return '?';
}

dfa* dfa_la_merge(dfa* this_dfa)
{
  dfa* ret_dfa;
  set_ current_state = SET_NULL;
  merged_state_element *this_symbol;

  if (this_dfa->labels_index == NULL)
    text_message(TEXT_FATAL, "attempt to perform la_merge on DFA with stripped labels\n");

  void* state_table = symbol_new_table("la_merge_state_table", 999983, 999979, symbol_compare_set, symbol_hash_set, symbol_print_set);

  //Make a new state index
  unsigned *new_state_index = (unsigned*) mem_malloc(this_dfa->state_count * sizeof(unsigned));
  unsigned *old_state_index = (unsigned*) mem_malloc(this_dfa->state_count * sizeof(unsigned));
  unsigned new_state_number = 0;

  //Iterate over the states in this_dfa, locating them in state table and lookaheads
  for (unsigned state_number = 0; state_number < this_dfa->state_count; state_number++)
  {
    //Walk the state label, building up the set current_state
    unsigned *label_pointer = this_dfa->labels_index[state_number];
    unsigned *table_pointer = this_dfa->tables_index[state_number];

#if defined(LA_MERGE_TRACE)
    text_printf("\nLabel for state %u: ", state_number + this_dfa->grammar->first_dfa_state);
#endif
    do
    {
#if defined(LA_MERGE_TRACE)
      text_printf("%c%u ", grammar_element_kind(this_dfa->grammar, *label_pointer), *label_pointer);
#endif
      label_pointer++;
    }
    while (!GRAM_IS_SYMBOL(this_dfa->grammar, *label_pointer));
#if defined(LA_MERGE_TRACE)
    text_printf("\n");

    text_printf("Table for state %u: ", state_number + this_dfa->grammar->first_dfa_state);
#endif
    do
    {
#if defined(LA_MERGE_TRACE)
      text_printf("%c%u ", grammar_element_kind(this_dfa->grammar, *table_pointer), *table_pointer);
#endif
      table_pointer++;
    }
    while (!GRAM_IS_SYMBOL(this_dfa->grammar, *table_pointer));
#if defined(LA_MERGE_TRACE)
    text_printf("\n");
#endif

    set_clear(&current_state);
    label_pointer = this_dfa->labels_index[state_number];

    unsigned this_symbol_number = *label_pointer++;   // skip symbol element in labels

    while (*label_pointer >= this_dfa->grammar->first_header)
    {
      unsigned *this_move_to_slot = this_dfa->nfa->header_index[*label_pointer- first_header]->nonterminal_symbol->move_tos[this_symbol_number];

      while (*this_move_to_slot != SET_END)
        set_unite_element(&current_state, *this_move_to_slot++);

      label_pointer++;
    }

    while (*label_pointer >= this_dfa->grammar->first_level_0_slot && *label_pointer < this_dfa->grammar->first_header)
    {
      set_unite_element(&current_state, *label_pointer);
      label_pointer += 2;
    }

#if defined(LA_MERGE_TRACE)
    text_printf("Slot set for state %u: {", state_number + this_dfa->grammar->first_dfa_state);
    set_print_set(&current_state, NULL, 0);
    text_printf("}\n");
#endif

    if ((this_symbol = (merged_state_element*) symbol_lookup_key(state_table, &current_state, NULL)) != NULL)
    {
#if defined(LA_MERGE_TRACE)
      text_printf("Old\n");
#endif
    }
    else
    {
      set_ temp = SET_NULL;
      set_assign_set_normalise(&temp, &current_state);

      this_symbol = (merged_state_element*) symbol_insert_key(state_table, &temp, sizeof (set_), sizeof (merged_state_element));
      this_symbol->state_number = new_state_number++;
#if defined(LA_MERGE_TRACE)
      text_printf("New\n");
#endif
    }

    new_state_index[state_number] = this_symbol->state_number;
    old_state_index[this_symbol->state_number] = state_number;
#if defined(LA_MERGE_TRACE)
    text_printf("Old state %u; new state %u\n", this_dfa->grammar->first_dfa_state + state_number, this_dfa->grammar->first_dfa_state + new_state_index[state_number]);
#endif
  }

// Now build the return dfa
// Hack: set sizes to some large number: should be computed exactly
  unsigned merged_states = 0;
  unsigned merged_total_cardinality = 0;

  for (this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(symbol_get_scope(state_table));
       this_symbol != NULL;
       this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(this_symbol))
  {
    merged_states++;
    merged_total_cardinality += set_cardinality(&this_symbol->id);
  }

  if (script_gtb_verbose())
    text_printf("la_merge: input DFA has %u states, merged DFA has %u states, merged label cardinality %u\n", this_dfa->state_count, merged_states, merged_total_cardinality);

  unsigned label_buffer_size = merged_states + merged_total_cardinality + 1;
  unsigned table_buffer_size = 100000;

  ret_dfa =(dfa *) mem_calloc(1, sizeof(dfa));
  ret_dfa->nfa = this_dfa->nfa;
  ret_dfa->grammar = this_dfa->grammar;
  ret_dfa->state_count = merged_states;
  ret_dfa->labels_index = (unsigned**) mem_calloc(ret_dfa->state_count, sizeof (unsigned*));
  ret_dfa->tables_index = (unsigned**) mem_calloc(ret_dfa->state_count, sizeof (unsigned*));

  // Create the labels array the table array and initialise associated pointers
  ret_dfa->labels = next_free_label_location = (unsigned*) mem_calloc(label_buffer_size, sizeof(unsigned));
  dfa_labels_top = ret_dfa->labels + label_buffer_size;
  if (script_gtb_verbose())
    text_printf("Allocated %lu words for dfa labels\n", dfa_labels_top - ret_dfa->labels);

  for (this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(symbol_get_scope(state_table));
       this_symbol != NULL;
       this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(this_symbol))
  {
    ret_dfa->labels_index[this_symbol->state_number] = next_free_label_location;
    DFA_LOAD_LABEL_ELEMENT(*(this_dfa->labels_index[old_state_index[this_symbol->state_number]]));

    unsigned *label_elements = set_array(&this_symbol->id);

    for (unsigned *this_label_element = label_elements; *this_label_element != SET_END; this_label_element++)
      DFA_LOAD_LABEL_ELEMENT(*this_label_element);
  }

  // and now do the action tables
  dfa_tables = ret_dfa->tables = next_free_table_location = (unsigned*) mem_calloc(table_buffer_size, sizeof(unsigned));
  dfa_tables_top = ret_dfa->tables + table_buffer_size;
  if (script_gtb_verbose())
    text_printf("Allocated %u words for dfa tables\n", dfa_tables_top - ret_dfa->tables);

  set_ *reductions_for_lookahead = (set_*) mem_calloc(this_dfa->grammar->first_nonterminal, sizeof(set_));
  set_ reduction_set = SET_NULL;

  for (this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(symbol_get_scope(state_table));
       this_symbol != NULL;
       this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(this_symbol))
  {
    //Construct merged reduction sets for all old states that have this state as their merged state number
    for (int count = 0; count < this_dfa->grammar->first_nonterminal; count++)
      set_clear(&reductions_for_lookahead[count]);

    for (int old_state = 0; old_state < this_dfa->state_count; old_state++)
    {
      if (new_state_index[old_state] == this_symbol->state_number)  // Does this old state map to the current merged state
      {
        unsigned *reduction_pointer = this_dfa->tables_index[old_state] + 1;      // Skip the symbol entry for the old table state

        while (*reduction_pointer >= ret_dfa->grammar->first_level_0_slot && *reduction_pointer < ret_dfa->grammar->first_dfa_state)
        {
          set_clear(&reduction_set);

          while (*reduction_pointer >= ret_dfa->grammar->first_level_0_slot && *reduction_pointer < ret_dfa->grammar->first_dfa_state)
            set_unite_element(&reduction_set, *reduction_pointer++);

          set_unite_set(&reductions_for_lookahead[*reduction_pointer++], &reduction_set);
        }
      }
    }

    ret_dfa->tables_index[this_symbol->state_number] = next_free_table_location;

    unsigned *shift_pointer = this_dfa->tables_index[old_state_index[this_symbol->state_number]];

    *next_free_table_location++ = *shift_pointer++; //Copy the old symbol value

    //Skip the reductions
    while (*shift_pointer >= ret_dfa->grammar->first_level_0_slot && *shift_pointer < ret_dfa->grammar->first_dfa_state)
    {
      while (*shift_pointer >= ret_dfa->grammar->first_level_0_slot && *shift_pointer < ret_dfa->grammar->first_dfa_state)
        shift_pointer++;

      shift_pointer++;
    }

    //Write out the merged reductions
    for (int symbol = 0; symbol < this_dfa->grammar->first_nonterminal; symbol++)
      if (set_cardinality(&reductions_for_lookahead[symbol]) > 0)
      {
        unsigned *reduction_elements = set_array(&reductions_for_lookahead[symbol]);
        for (unsigned *this_reduction_element = reduction_elements; *this_reduction_element != SET_END; this_reduction_element++)
          DFA_LOAD_TABLE_ELEMENT(*this_reduction_element);

        DFA_LOAD_TABLE_ELEMENT(symbol);
        mem_free(reduction_elements);
      }

    //Copy the shifts out of the old table
    while (GRAM_IS_DFA_STATE(this_dfa->grammar, *shift_pointer))
    {
      DFA_LOAD_TABLE_ELEMENT(new_state_index[*shift_pointer - this_dfa->grammar->first_dfa_state] + this_dfa->grammar->first_dfa_state);
      shift_pointer++;
    }
  }

  //Now make reduction table
  ret_dfa->reduction_count = this_dfa->reduction_count;
  ret_dfa->reduction_table = (reduction*) mem_calloc(ret_dfa->reduction_count, sizeof(reduction));
  memcpy(ret_dfa->reduction_table, this_dfa->reduction_table, ret_dfa->reduction_count * sizeof(reduction));
  ret_dfa->reductions_index = (unsigned*) mem_calloc(ret_dfa->reduction_count, sizeof(unsigned));
  memcpy(ret_dfa->reductions_index, this_dfa->reductions_index, ret_dfa->reduction_count * sizeof(unsigned));

  //Release all memory back to heap
  for (this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(symbol_get_scope(state_table));
       this_symbol != NULL;
       this_symbol = (merged_state_element*) symbol_next_symbol_in_scope(this_symbol))
    set_free(&this_symbol->id);

  symbol_free_table(state_table);

  mem_free(new_state_index);
  mem_free(old_state_index);

  ret_dfa->merged = 1;

  return ret_dfa;
}

void dfa_dfa_statistics(dfa* this_dfa)
{
  text_printf("DFA statistics\n");

  unsigned conflict_count = 0;
  unsigned *reduction_count = (unsigned*) mem_malloc(this_dfa->grammar->first_level_0_slot * sizeof(unsigned));
  unsigned *shift_count = (unsigned*) mem_malloc(this_dfa->grammar->first_level_0_slot * sizeof(unsigned));

  for (unsigned state = 0; state < this_dfa->state_count; state++)
  {
    unsigned *action_pointer = this_dfa->tables_index[state];
    memset(shift_count, 0, this_dfa->grammar->first_level_0_slot * sizeof(unsigned));
    memset(reduction_count, 0, this_dfa->grammar->first_level_0_slot * sizeof(unsigned));

    action_pointer++;                                          // Skip symbol node

    while (*action_pointer >= this_dfa->grammar->first_level_0_slot && *action_pointer < this_dfa->grammar->first_dfa_state)
    {
      unsigned this_reduction_count = 0;

      while (*action_pointer >= this_dfa->grammar->first_level_0_slot && *action_pointer < this_dfa->grammar->first_dfa_state)
      {
        this_reduction_count++;
        action_pointer++;
      }

      reduction_count[*action_pointer] += this_reduction_count;

      action_pointer++;
    }

    while (GRAM_IS_DFA_STATE(this_dfa->grammar, *action_pointer))
    {
      unsigned* this_symbol = this_dfa->tables_index[*action_pointer - this_dfa->grammar->first_dfa_state];

      shift_count[*this_symbol]++;

      action_pointer++;
    }

    for (int symbol = 0; symbol < this_dfa->grammar->first_level_0_slot; symbol++)
      if ((shift_count[symbol] + reduction_count[symbol]) > 1)
      {
        conflict_count++;
        if (script_gtb_verbose())
          text_printf("State %u on symbol %u has %u shift%s and %u reduce%s\n",
                      state + this_dfa->grammar->first_dfa_state,
                      symbol,
                      shift_count[symbol], shift_count[symbol] != 1 ? "s" : "",
                      reduction_count[symbol], reduction_count[symbol] != 1 ? "s" : ""
                      );
      }
  }

  mem_free(shift_count);
  mem_free(reduction_count);

  text_printf("%srolled DFA %swith %s lookahead, %s reductions\n",
              this_dfa->nfa->unrolled ? "un" : "",
              this_dfa->merged ? "(merged) " : "",
              this_dfa->nfa->lookahead == 0 ? "no" : this_dfa->nfa->lookahead > 0 ? "rhs (lr)" : "lhs (slr)",
              this_dfa->nfa->nullable_reductions? "nullable" : "normal");
  text_printf("%u state%s, %u terminal%s, %u nonterminal%s, %u conflicted cell%s\n",
              this_dfa->state_count, this_dfa->state_count == 1 ? "" : "s",
              this_dfa->grammar->first_nonterminal - this_dfa->grammar->first_terminal,
              this_dfa->grammar->first_nonterminal - this_dfa->grammar->first_terminal == 1 ? "" : "s",
              this_dfa->grammar->first_level_0_slot - this_dfa->grammar->first_nonterminal,
              this_dfa->grammar->first_level_0_slot - this_dfa->grammar->first_nonterminal == 1 ? "" : "s",
              conflict_count, conflict_count == 1 ? "" : "s"
              );

}

