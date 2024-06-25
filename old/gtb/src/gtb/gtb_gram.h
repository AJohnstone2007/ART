/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 17 September 2000
*
* gtb_gram.h - grammar functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_GRAM_H
#define GTB_GRAM_H

#include"set.h"
#include"gtb.h"

#define GRAM_AUGMENTED_SUFFIX "!augmented"
#define GRAM_INDIRECT_SUFFIX "!indirect"
#define GRAM_TILDE_SUFFIX "!tilde"

#define GRAM_ILLEGAL_SYMBOL_NUMBER 0
#define GRAM_ILLEGAL_SYMBOL_STRING "!Illegal"
#define GRAM_EPSILON_SYMBOL_NUMBER 1
#define GRAM_EPSILON_SYMBOL_STRING "#"
#define GRAM_EOS_SYMBOL_NUMBER 2
#define GRAM_EOS_SYMBOL_STRING "$"

#define GRAM_IS_TERMINAL(gram, item) (item >= gram->first_terminal && item < gram->first_nonterminal)
#define GRAM_IS_NONTERMINAL(gram, item) (item >= gram->first_nonterminal && item < gram->first_level_0_slot)
#define GRAM_IS_SYMBOL(gram, item) (item < gram->first_level_0_slot)
#define GRAM_IS_SLOT(gram, item) (item >= gram->first_level_0_slot && item < gram->first_header)
#define GRAM_IS_HEADER(gram, item) (item >= gram->first_header && item < gram->first_dfa_state)
#define GRAM_IS_DFA_STATE(gram, item) (item >= gram->first_dfa_state )

struct gram_action
{
  char *action;
  struct gram_action *next;
};

struct gram_node
{
  union
  {
   struct gram_symbols_data *symbol_table_entry;     // root node of grammar rule
   struct gram_action *actions;                             // action list for slots
  };
};

struct gram_edge
{
  unsigned instance_number;
  int tilde:1;                                               // tilde found before nonterminal attribute for AH style automata
  struct gram_symbols_data *symbol_table_entry;
  script_symbols_data *script_symbol_table_entry;
};

struct gram_symbols_data
{
  int name_space;                          // SPECIAL, TERMINAL, NONTERMINAL
  char *id;
  unsigned symbol_number;
  struct gram_node *rule_tree;             // pointer into rules tree
  set_ first;                              // first set for this symbol
  set_ follow;                             // follow set for this symbol
  unsigned production_count;               // number of productions, and thus size of selector set array
  set_* selectors;                         // LL(1) selector sets
  unsigned **move_tos;                     // move() sets represented as set_arrays
  unsigned instance_count;                 // the number of  RHS instances for this symbol
  set_ *immediate_instance_follow;         // immediate instance follow sets for these instances
  unsigned *level_1_nonterminal_instances; // set of nonterminal instances in leaders
  int is_tilded:1;
  int ll1_nondetermined:1;                 // union of ll1_follow_nondetermined and ll1_selector_nondetermined
  int ll1_follow_nondetermined:1;          // intersection of first and follow nonempty
  int ll1_selector_nondetermined:1;        // interesection of at least two selectors nonempty
};

struct grammar
{
  void *rules;                           // graph folding forest of rules trees
  struct gram_symbols_data *start_rule;  // pointer to symbol table entry for start rule
  unsigned first_terminal;               // number of first nonterminal (usually EPSILON_SYMBOL_NUMBER + 1)
  unsigned first_nonterminal;            // number of first nonterminal (first_terminal + |{terminals}|
  unsigned first_level_0_slot;           // number of first level 0 (root, or station) slot (first_nonterminal + |{nonterminals}|
  unsigned first_level_1_slot;           // number of first level 1 (leader) slot
  unsigned first_level_2_slot;           // number of first level 2 slot
  unsigned first_level_3plus_slot;       // number of first slot at level 3 or above
  unsigned first_header;                 // 1 + number of last slot in grammar
  unsigned first_dfa_state;
  void *symbol_table;
  gram_symbols_data **symbol_index;
  unsigned *prefix_equivalent_slot_index;
  unsigned maximum_rule_length;          // length of the longest rule
  int tilde_enabled:1;
  set_ terminals;
  set_ nonterminals;
  set_ reductions;
  set_ right_nullable_reductions;
  set_ start_rule_reductions;
  set_ start_rule_right_nullable_reductions;
  set_ reachable;                        // Symbols that are reachable from the start symbol
};

grammar *gram_construct_gram(rdp_tree_node_data * root, rdp_tree_node_data * start_rule, bool tilde_enabled);
void gram_augment_gram(grammar * this_gram, int force_augmentation);
void gram_tilde(grammar *this_gram, int terminalise);
grammar *gram_copy_gram(grammar *this_gram);
grammar *gram_remove_multiple_direct_recursion(grammar *this_gram);
grammar *gram_remove_unreachable_rules(grammar *this_gram);

gram_node *gram_insert_node(gram_node * parent, gram_symbols_data *label, unsigned instance_number, int tilde);

gram_symbols_data *gram_find_symbol_by_id(grammar *this_gram, char *id, int name_space);
gram_symbols_data *gram_find_symbol_by_number(grammar *this_gram, unsigned symbol_number);

unsigned gram_max_rule_length(gram_symbols_data *this_symbol);

void gram_print_rules(grammar * this_gram);
void gram_print_set_of_symbols(grammar *this_gram, set_ *this_set);
void gram_print_set_of_slots(grammar *this_gram, set_ *this_set);
int gram_print_symbol_id(gram_symbols_data *this_symbol);
int gram_print_slot(gram_node * this_slot, int vcg);
int gram_print_slot_by_number(grammar *this_gram, unsigned slot_number, int vcg);
unsigned *gram_slot_suffix_by_number(grammar *this_gram, unsigned slot_number);
int gram_print_slot_suffix(grammar *this_gram, unsigned * suffix);
void gram_print_symbols(grammar *this_gram, unsigned first_symbol);

char *gram_strip_suffix(char * string, char*suffix);
void gram_tidy(grammar* this_gram, int augment, int recalculate_reachability);
void gram_tokeniser(FILE *output_file, grammar *this_gram);
void gram_unwrap_nonterminal_follow_set(grammar *this_gram, set_ *this_set);
void gram_write(FILE *output_file, grammar *this_gram);
void gram_render(FILE *output_file, grammar *this_gram);

unsigned gram_slot_prefix_length(grammar* this_gram, unsigned slot);
unsigned gram_slot_suffix_length(grammar* this_gram, unsigned slot);
gram_symbols_data *gram_slot_lhs_symbol(grammar* this_gram, unsigned slot);
unsigned gram_previous_slot(grammar* this_gram, unsigned slot);
unsigned gram_next_slot(grammar* this_gram, unsigned slot);

grammar *gram_ebnf2bnf(grammar *this_gram);
grammar *gram_cnf(grammar *this_gram);
grammar *gram_gnf(grammar *this_gram);
#endif

