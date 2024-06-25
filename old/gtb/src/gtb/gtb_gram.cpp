/*******************************************************************************
*
* GTB release 2.5 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 25 September 2002
*
* gtb_gram.c - grammar functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
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
#include "gtb_sr.h"
#include "gtb_gp.h"
#include "trie.h"
#include "stdlib.h"

gram_symbols_data *gram_find_symbol_by_id(grammar *this_gram, char *id, int name_space)
{
  gram_symbols_data *this_symbol;
  struct key_struct {int name_space; char *id; } key;

  key.id = id;
  key.name_space = name_space;

  if ((this_symbol = (gram_symbols_data *) symbol_lookup_key(this_gram->symbol_table, &key, NULL)) == NULL)
  {
    this_symbol = (gram_symbols_data *) symbol_insert_key(this_gram->symbol_table, &key, sizeof(struct key_struct), sizeof(gram_symbols_data));
    this_symbol->id = script_strdup(id);
  }

  return this_symbol;
}

gram_symbols_data *gram_find_symbol_by_number(grammar *this_gram, unsigned symbol_number)
{
  if (symbol_number >= this_gram->first_level_0_slot)
  {
    text_message(TEXT_INFO, "internal error - attempt to find out of range symbol number %u\n", symbol_number);
    return NULL;
  }
  else
    return this_gram->symbol_index[symbol_number];
}

int gram_print_symbol_id(gram_symbols_data *this_symbol)
{
  if (this_symbol == NULL)
    return text_printf("NULL_ID");
  else
    return text_printf("%s%s%s", this_symbol->name_space == SCRIPT_NS_TERMINAL ? "'" : this_symbol->name_space == SCRIPT_NS_TERMINAL_ELEMENT ? "`" : "",
                                 this_symbol->id,
                                 this_symbol->name_space == SCRIPT_NS_TERMINAL ? "'" : ""
                       );
}

void gram_print_symbols(grammar *this_gram, unsigned first_symbol)
{
  unsigned symbol_count = this_gram->first_level_0_slot;
  unsigned this_symbol_number;
  int divider_printed = 0;

  for (this_symbol_number = first_symbol; this_symbol_number < symbol_count; this_symbol_number++)
  {
    gram_symbols_data *this_symbol = gram_find_symbol_by_number(this_gram, this_symbol_number);

    if (this_symbol != NULL)
    {
      if (this_symbol->rule_tree != NULL && !divider_printed)
      {
        text_printf("-------------\n");
        divider_printed = 1;
      }
      text_printf("%4u ", this_symbol_number);
      gram_print_symbol_id(this_symbol);
      text_printf("\n");
    }
  }
}

void gram_print_rules(grammar * this_gram)
{
  for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_rule_symbol != NULL;
       this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
  {
    if (this_rule_symbol->rule_tree != NULL)  /* Skip terminals, etc, which have no associated rule tree */
    {
      gram_print_symbol_id(this_rule_symbol);
      text_printf(" ::=");

      if (graph_next_out_edge(this_rule_symbol->rule_tree) == NULL)
        text_printf(" UNDEFINED\n");
      else
        for (gram_edge * this_production_edge = (gram_edge*) graph_next_out_edge(this_rule_symbol->rule_tree);
             this_production_edge != NULL;
             this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
        {

          if (graph_next_out_edge(graph_destination(this_production_edge)) == NULL)  //special case: empty element list
            text_printf(" #");
          else
            for (gram_edge *this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));
                 this_element_edge != NULL;
                 this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
            {
              gram_symbols_data *this_edge_symbol = this_element_edge->symbol_table_entry;
              text_printf(" ");
              if (this_element_edge->tilde)
                text_printf("~");
              gram_print_symbol_id(this_edge_symbol);
              if (this_edge_symbol->name_space == SCRIPT_NS_NONTERMINAL)
                text_printf("[%u]", this_element_edge->instance_number);
            }
          if (graph_next_out_edge(this_production_edge) == NULL)
            text_printf(" .\n");
          else
            text_printf(" |\n%*s", strlen(this_rule_symbol->id) + 5," ");
        }
    }
  }
}

static void gram_print_sets(grammar * this_gram)
{
  text_printf("\nterminals = {");
  set_print_set(&this_gram->terminals, NULL, 0);
  text_printf("} = {");
  gram_print_set_of_symbols(this_gram, &this_gram->terminals);
  text_printf("}\n");

  text_printf("\nnonterminals = {");
  set_print_set(&this_gram->nonterminals, NULL, 0);
  text_printf("} = {");
  gram_print_set_of_symbols(this_gram, &this_gram->nonterminals);
  text_printf("}\n");

  text_printf("\nreachable = {");
  set_print_set(&this_gram->reachable, NULL, 0);
  text_printf("} = {");
  gram_print_set_of_symbols(this_gram, &this_gram->reachable);
  text_printf("}\n");

  text_printf("\nreductions = {");
  set_print_set(&this_gram->reductions, NULL, 0);
  text_printf("} = {");
  gram_print_set_of_slots(this_gram, &this_gram->reductions);
  text_printf("}\n");

  text_printf("\nnullable_reductions = {");
  set_print_set(&this_gram->right_nullable_reductions, NULL, 0);
  text_printf("} = {");
  gram_print_set_of_slots(this_gram, &this_gram->right_nullable_reductions);
  text_printf("}\n");

  text_printf("\nstart rule reductions = {");
  set_print_set(&this_gram->start_rule_reductions, NULL, 0);
  text_printf("} = {");
  gram_print_set_of_slots(this_gram, &this_gram->start_rule_reductions);
  text_printf("}\n");

  text_printf("\nstart rule nullable_reductions = {");
  set_print_set(&this_gram->start_rule_right_nullable_reductions, NULL, 0);
  text_printf("} = {");
  gram_print_set_of_slots(this_gram, &this_gram->start_rule_right_nullable_reductions);
  text_printf("}\n");

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol!= NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_TERMINAL || this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      text_printf("\nfirst(");
      gram_print_symbol_id(this_symbol);
      text_printf(") = {");
      gram_print_set_of_symbols(this_gram, &this_symbol->first);
      text_printf("}\n");

      if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL && this_symbol->selectors != NULL)
      {
        int production_count = 0;

        for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);       /* Iterate over productions */
             this_production_edge != NULL;
             this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), production_count++)
        {
          text_printf("selector(");
          gram_print_symbol_id(this_symbol);
          text_printf(", %i) = {", production_count);
          gram_print_set_of_symbols(this_gram, &this_symbol->selectors[production_count]);
          text_printf("}\n");
        }
      }

      text_printf("wrapped_follow(");
      gram_print_symbol_id(this_symbol);
      text_printf(") = {");
      gram_print_set_of_symbols(this_gram, &this_symbol->follow);
      text_printf("}\n");

      text_printf("follow(");
      gram_print_symbol_id(this_symbol);
      text_printf(") = {");

      set_ working_set = SET_NULL;
      set_assign_set(&working_set, &this_symbol->follow);
      gram_unwrap_nonterminal_follow_set(this_gram, &working_set);
      set_difference_element(&working_set, GRAM_EPSILON_SYMBOL_NUMBER);
      gram_print_set_of_symbols(this_gram, &working_set);
      text_printf("}\n");

      if (this_symbol->ll1_nondetermined)
      {
        if (this_symbol->ll1_follow_nondetermined)
          text_printf("Non-LL(1): not follow determined\n");
        if (this_symbol->ll1_selector_nondetermined)
          text_printf("Non-LL(1): not selector determined\n");
      }

      if (this_symbol->immediate_instance_follow != NULL)
        for (unsigned instance_count = 0; instance_count < this_symbol->instance_count; instance_count++)
        {
          text_printf("rhs_follow(");
          gram_print_symbol_id(this_symbol);
          text_printf(", %u) = {", instance_count);
          gram_print_set_of_symbols(this_gram, &this_symbol->immediate_instance_follow[instance_count]);
          text_printf("}\n");
        }

      if (script_gtb_verbose())
      {
        if (this_symbol->move_tos != NULL)
          for (unsigned this_symbol_number = 0; this_symbol_number < this_gram->first_level_0_slot; this_symbol_number++)
            if (this_symbol->move_tos[this_symbol_number] != NULL)
            {
              text_printf("move(");
              gram_print_symbol_id(this_symbol);
              text_printf(", ");
              gram_print_symbol_id(gram_find_symbol_by_number(this_gram, this_symbol_number));
              text_printf(") = {");

              int first = 1;
              for (unsigned *this_element = this_symbol->move_tos[this_symbol_number]; *this_element != SET_END; this_element++)
              {
                if (first)
                {
                  first = 0;
                  text_printf(" ");
                }
                else
                  text_printf(", ");

                gram_print_slot_by_number(this_gram, *this_element, 0);
              }
              text_printf("}\n");
            }
      }

      if (script_gtb_verbose())
      {
        if (this_symbol->level_1_nonterminal_instances != NULL)
        {
          text_printf("level_1_nonterminal_instances(");
          gram_print_symbol_id(this_symbol);
          text_printf(") = {");

          int first = 1;
          for (unsigned *this_element = this_symbol->level_1_nonterminal_instances; *this_element != SET_END; this_element++)
          {
            if (first)
            {
              first = 0;
              text_printf(" ");
            }
            else
              text_printf(", ");

            text_printf("%u", *this_element);
          }
          text_printf(" }\n");
        }
      }
    }
}

unsigned gram_max_rule_length(gram_symbols_data *this_symbol)
{
  if (this_symbol->rule_tree == NULL)
    return 0;
  else
  {
    unsigned max_rule_length = 0;

    for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
         this_production_edge != NULL;
         this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
    {
      unsigned production_length = 0;

      for (gram_edge *this_element_edge = this_production_edge;
           this_element_edge != NULL;
           this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        if (++production_length > max_rule_length)
          max_rule_length = production_length;
    }

    return max_rule_length;
  }
}

gram_node *gram_insert_node(gram_node * parent, gram_symbols_data *label, unsigned instance_number, int tilde)
{
  gram_node *temp = (gram_node *) graph_insert_node(sizeof(gram_node), parent);

  gram_edge *this_edge = ((gram_edge*) graph_insert_edge_after_final(sizeof(gram_edge), temp, parent));

  this_edge->symbol_table_entry = label;
  this_edge->instance_number = instance_number;
  this_edge->tilde = tilde;

  return temp;
}

static void gram_number_symbols(grammar * this_gram)
{
  /* Running pointers to the current terminal and nonterminal */
  gram_symbols_data *this_symbol;

  /* Symbol counter: used to allocate a unique index to each terminal and nonterminal. Symbol zero is reserved as an illegal value so as to pick up uninitialised symbol variables and symbol 1
     corresponds to the epsilon string. Hence start allocating symbol numbers from one above EPSILON_SYMBOL */
  unsigned symbol_number = GRAM_EOS_SYMBOL_NUMBER + 1;

  this_gram->first_terminal = symbol_number;

  /* Alphabeticize the terminals and nonterminals */
  symbol_sort_scope(this_gram->symbol_table, symbol_get_scope(this_gram->symbol_table));

  /* Scan through terminals, allocating unique indices */
  for (this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_TERMINAL)
      this_symbol->symbol_number = symbol_number++;  /* allocate unique terminal index */

  this_gram->first_nonterminal = symbol_number;
  /* Scan through nonterminals, allocating unique indices */
  for (this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
      this_symbol->symbol_number = symbol_number++;  /* allocate unique terminal index */
  this_gram->first_level_0_slot = symbol_number;
}

static void gram_number_slots(grammar *this_gram)
{
  unsigned slot_number = this_gram->first_level_0_slot;

  // Zero all the atom numbers
  for (gram_node *this_gram_node = (gram_node*) graph_next_node(this_gram->rules); this_gram_node != NULL; this_gram_node = (gram_node*) graph_next_node(this_gram_node))
    graph_set_atom_number(this_gram_node, 0);

  for (gram_node *this_gram_node = (gram_node*) graph_next_node(this_gram->rules); this_gram_node != NULL; this_gram_node = (gram_node*) graph_next_node(this_gram_node))
    if (graph_next_in_edge(this_gram_node) == NULL)
      graph_set_atom_number(this_gram_node, slot_number++);

  this_gram->first_level_1_slot = slot_number;
  for (gram_node *this_gram_node = (gram_node*) graph_next_node(this_gram->rules); this_gram_node != NULL; this_gram_node = (gram_node*) graph_next_node(this_gram_node))
  {
    gram_edge *this_in_edge = (gram_edge*) graph_next_in_edge(this_gram_node) ;

    if (this_in_edge != NULL && this_in_edge->symbol_table_entry->symbol_number == GRAM_EPSILON_SYMBOL_NUMBER)
      graph_set_atom_number(this_gram_node, slot_number++);
  }

  this_gram->first_level_2_slot = slot_number;
  for (gram_node *this_gram_node = (gram_node*) graph_next_node(this_gram->rules); this_gram_node != NULL; this_gram_node = (gram_node*) graph_next_node(this_gram_node))
  {
    gram_edge *this_in_edge = (gram_edge*) graph_next_in_edge(this_gram_node) ;

    if (this_in_edge != NULL)
    {
      gram_edge *this_parent_in_edge = (gram_edge*) graph_next_in_edge(graph_source(this_in_edge)) ;

      if (this_parent_in_edge != NULL && this_parent_in_edge->symbol_table_entry->symbol_number == GRAM_EPSILON_SYMBOL_NUMBER)
        graph_set_atom_number(this_gram_node, slot_number++);
    }
  }

  this_gram->first_level_3plus_slot = slot_number;
  for (gram_node *this_gram_node = (gram_node*) graph_next_node(this_gram->rules); this_gram_node != NULL; this_gram_node = (gram_node*) graph_next_node(this_gram_node))
    if (graph_atom_number(this_gram_node) == 0)
      graph_set_atom_number(this_gram_node, slot_number++);

  this_gram->first_header = slot_number;
}

static void gram_fixup_undefined_nonterminals(grammar *this_gram)
{
  for (gram_symbols_data *this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL && this_symbol->rule_tree == NULL)
    {
      text_message(TEXT_WARNING, "nonterminal %s has no rule\n", this_symbol->id);

      this_symbol->rule_tree = (gram_node *) graph_insert_node(sizeof(gram_node), this_gram->rules);
    }
}

void gram_augment_gram(grammar * this_gram, int force_augmentation)
{
  int is_augmented = 1;

  /* Grammar is already augmented if the start rule is of the form S-#-o-N-o . */

  gram_node *start_rule = this_gram->start_rule->rule_tree;

  if (this_gram->start_rule->name_space != SCRIPT_NS_NONTERMINAL)
    text_message(TEXT_FATAL, "in augmentation, start symbol is not a nonterminal\n");

  gram_edge *production_edge = (gram_edge*) graph_next_out_edge(start_rule);

  if (graph_next_out_edge(production_edge) != NULL)
    is_augmented = 0;

  gram_edge *first_symbol_edge = (gram_edge*) graph_next_out_edge(graph_destination(production_edge));

  if (first_symbol_edge == NULL)  // Language contains just one epsilon rule
    is_augmented = 0;
  else
  {
    if (first_symbol_edge->symbol_table_entry->name_space != SCRIPT_NS_NONTERMINAL)
      is_augmented = 0;

    gram_edge *second_symbol_edge = (gram_edge*) graph_next_out_edge(graph_destination(first_symbol_edge));

    if (second_symbol_edge != NULL)
      is_augmented = 0;
  }

  if (force_augmentation || !is_augmented)
  {
    if (force_augmentation && is_augmented)
      text_message(TEXT_WARNING, "forcing augmentation of an already augmented grammar\n");

    gram_symbols_data* epsilon_symbol = epsilon_symbol = gram_find_symbol_by_id(this_gram, GRAM_EPSILON_SYMBOL_STRING, SCRIPT_NS_SPECIAL);

    char *new_start_id =(char*) mem_calloc(strlen(this_gram->start_rule->id) + strlen(GRAM_AUGMENTED_SUFFIX) + 1, sizeof(char));
    strcat(strcat(new_start_id, this_gram->start_rule->id), GRAM_AUGMENTED_SUFFIX);
    gram_symbols_data *new_start_symbol = gram_find_symbol_by_id(this_gram, new_start_id, SCRIPT_NS_NONTERMINAL);

    new_start_symbol->rule_tree = (gram_node *) graph_insert_node(sizeof(gram_node), this_gram->rules);

    new_start_symbol->rule_tree->symbol_table_entry = new_start_symbol;

    gram_node *new_production = gram_insert_node(new_start_symbol->rule_tree, epsilon_symbol, 0, 0);

    gram_insert_node(new_production, this_gram->start_rule, 0, 0);

    this_gram->start_rule = new_start_symbol;
  }

}

static void gram_create_symbol_index(grammar *this_gram)
{
  if (this_gram->symbol_index != NULL)
    mem_free(this_gram->symbol_index);

  this_gram->symbol_index = (gram_symbols_data **) mem_calloc(this_gram->first_level_0_slot, sizeof(gram_symbols_data*));

  for (gram_symbols_data *this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
  this_gram->symbol_index[this_symbol->symbol_number] = this_symbol;
}

static int unite_set_with_element(set_ * dst, int element)
{
  if (set_includes_element(dst, element))
    return 0;

  set_unite_element(dst, element);
  return 1;
}

static int unite_set_with_set(set_ * dst, set_ * src)
{
  if (set_includes_set(dst, src))
    return 0;

  set_unite_set(dst, src);
  return 1;
}

static void gram_calculate_reachability(gram_node * this_rule, set_ * reachable)
{
  if (this_rule -> symbol_table_entry == NULL)
    return;

  if (set_includes_element(reachable, this_rule->symbol_table_entry->symbol_number))
    return;

  set_unite_element(reachable, this_rule->symbol_table_entry->symbol_number);

  for (void * this_production_edge = graph_next_out_edge(this_rule);
       this_production_edge != NULL;
       this_production_edge = graph_next_out_edge(this_production_edge))
    if (graph_next_out_edge(graph_destination(this_production_edge)) == NULL)
      set_unite_element(reachable, GRAM_EPSILON_SYMBOL_NUMBER);
    else
      for (gram_edge* this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));
           this_element_edge != NULL;
           this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
      {
        if (this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_NONTERMINAL)
        {
          if (this_element_edge->symbol_table_entry->rule_tree != NULL)
            gram_calculate_reachability(this_element_edge->symbol_table_entry->rule_tree, reachable);
          else
            text_message(TEXT_ERROR, "rule %s references undefined symbol %s\n", this_rule->symbol_table_entry->id, this_element_edge->symbol_table_entry->id);
        }
        else if (this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_TERMINAL)
          set_unite_element(reachable, this_element_edge->symbol_table_entry->symbol_number);
        else
          text_message(TEXT_FATAL, "unexpected node kind in gram_calculate_reachability\n");
      }
}


static void gram_calculate_terminal_and_nonterminal_sets(grammar * this_gram)
{
  set_clear(&this_gram->terminals);
  set_clear(&this_gram->nonterminals);

  for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_rule_symbol != NULL;
       this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
  {
    if (GRAM_IS_TERMINAL(this_gram, this_rule_symbol->symbol_number))
      set_unite_element(&this_gram->terminals, this_rule_symbol->symbol_number);

    if (GRAM_IS_NONTERMINAL(this_gram, this_rule_symbol->symbol_number))
      set_unite_element(&this_gram->nonterminals, this_rule_symbol->symbol_number);
  }

}

static void gram_calculate_first_sets(grammar * this_gram)
{
  for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_rule_symbol != NULL;
       this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
    set_clear(&this_rule_symbol->first);

  int changed;

  do
  {
    changed = 0;

    /* Iterate over symbols */
    for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
         this_rule_symbol != NULL;
         this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
    {
      gram_node *this_rule = this_rule_symbol->rule_tree;

      if (this_rule != NULL)  /* Skip terminals, etc, which have no associated rule tree */
      {
        int production_count = 0;

        for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_rule);       /* Iterate over productions */
             this_production_edge != NULL;
             this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
          production_count++;

        if (production_count != 0)
          this_rule_symbol->selectors = (set_ *) mem_calloc(production_count, sizeof(set_));

        production_count = 0;

        for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_rule);       /* Iterate over productions */
             this_production_edge != NULL;
             this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), production_count++)
        {
          set_ running_first_set = SET_NULL;  /* Set used to build cumulative first set */
          int seen_only_nullable_elements = 1;  /* When adding elements to the production first set, we only add the elements up to the first non-epsilon element. This flag controls such additions */
          gram_edge *this_element_edge;
          set_assign_list(&running_first_set, SET_END); /* assign the empty set to running_first_set */

          /* Step forwards to last element edge in production */
          for (this_element_edge = this_production_edge;
               graph_next_out_edge(graph_destination(this_element_edge)) != NULL;
               this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
            ;

          /* step backwards over all the elements */
          for (;
               this_element_edge->symbol_table_entry->symbol_number != GRAM_EPSILON_SYMBOL_NUMBER;    /* Don't epsilon edge at start of catkin! */
               this_element_edge = (gram_edge*) graph_next_in_edge(graph_source(this_element_edge)))
          {
            /* If we're a terminal, then we are non-nullable and we must start a new running first set */
            switch (this_element_edge->symbol_table_entry->name_space)
            {
              case SCRIPT_NS_TERMINAL:
                seen_only_nullable_elements = 0;
                set_assign_element(&running_first_set, this_element_edge->symbol_table_entry->symbol_number);
                changed |= unite_set_with_element(&this_element_edge->symbol_table_entry->first, this_element_edge->symbol_table_entry->symbol_number);
                break;
              case SCRIPT_NS_NONTERMINAL:
                if (set_includes_element(&(this_element_edge->symbol_table_entry->first), GRAM_EPSILON_SYMBOL_NUMBER)) /* Nullable? */
                {
                  set_unite_set(&running_first_set, &(this_element_edge->symbol_table_entry->first));
                  set_difference_element(&running_first_set, GRAM_EPSILON_SYMBOL_NUMBER);
                }
                else
                {
                  seen_only_nullable_elements = 0;
                  set_assign_set(&running_first_set, &(this_element_edge->symbol_table_entry->first));
                }
                set_unite_element(&running_first_set, this_element_edge->symbol_table_entry->symbol_number);
                break;
            }
            if (seen_only_nullable_elements)
              changed |= unite_set_with_element(&(this_element_edge->symbol_table_entry->first), GRAM_EPSILON_SYMBOL_NUMBER);
          }

          if (this_rule_symbol->selectors != NULL)
            set_assign_set(&this_rule_symbol->selectors[production_count], &running_first_set);

          changed |= unite_set_with_set(&(this_rule_symbol->first), &running_first_set);

          if (seen_only_nullable_elements)  /* Got to end of production and all nullable => rule must match # */
          {
            changed |= unite_set_with_element(&(this_rule_symbol->first), GRAM_EPSILON_SYMBOL_NUMBER);
            if (this_rule_symbol->selectors != NULL)
              set_unite_element(&this_rule_symbol->selectors[production_count], GRAM_EPSILON_SYMBOL_NUMBER);
          }
        }
      }
    }
  }
  while (changed);
}

static void gram_calculate_follow_sets(grammar * this_gram)
{
  for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_rule_symbol != NULL;
       this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
    set_clear(&this_rule_symbol->follow);

  int changed;                  /* Flag to show if any follow sets changed */

  if (this_gram->start_rule != NULL)
    unite_set_with_element(&this_gram->start_rule->follow, GRAM_EOS_SYMBOL_NUMBER);  /* Add in end of string to foll(start) */

  do
  {
    changed = 0;

    /* Iterate over symbols */
    for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
         this_rule_symbol != NULL;
         this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
    {
      gram_node *this_rule = this_rule_symbol->rule_tree;

      if (this_rule != NULL && set_cardinality(&(this_rule_symbol->follow)) != 0 /* must be reachable */)
      {
        for (void *this_production_edge = graph_next_out_edge(this_rule);
             this_production_edge != NULL;
             this_production_edge = graph_next_out_edge(this_production_edge))
        {
          for (gram_edge *this_element_edge = (gram_edge *) graph_next_out_edge(graph_destination(this_production_edge));
               this_element_edge != NULL;
               this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          {
            int seen_only_nullable_elements = 1;

            for (gram_edge *this_next_element_edge = (gram_edge *) graph_next_out_edge(graph_destination(this_element_edge));
                 seen_only_nullable_elements && this_next_element_edge != NULL;
                 this_next_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_next_element_edge)))
            {
              changed |= unite_set_with_element(&this_element_edge->symbol_table_entry->follow, this_next_element_edge->symbol_table_entry->symbol_number);
              if (!set_includes_element(&this_next_element_edge->symbol_table_entry->first, GRAM_EPSILON_SYMBOL_NUMBER))
                seen_only_nullable_elements = 0;
            }

            if (seen_only_nullable_elements)
              changed |= unite_set_with_set(&(this_element_edge->symbol_table_entry->follow), &this_rule_symbol->follow);
          }
        }
      }
    }
  }
  while (changed);
}

static void gram_calculate_ll1(grammar *this_gram)
{
  set_ terminal_mask = SET_NULL;

  set_fill(&terminal_mask, this_gram->first_nonterminal - 1);

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol))
  {
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL && this_symbol->selectors != NULL)
    {
      if (set_includes_element(&this_symbol->first, GRAM_EPSILON_SYMBOL_NUMBER))
      {
        set_ working_set = SET_NULL;

        set_assign_set(&working_set, &this_symbol->follow);
        gram_unwrap_nonterminal_follow_set(this_gram, &working_set);
        set_intersect_set(&working_set, &this_symbol->first);
        set_intersect_set(&working_set, &terminal_mask);

        this_symbol->ll1_follow_nondetermined = set_cardinality(&working_set) != 0;

        set_free(&working_set);
      }

      set_ working_set = SET_NULL;

      int production_count = 0;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);       /* Iterate over productions */
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
        production_count++;

      for (int left = 0; left < production_count; left++)
        for (int right = left + 1; right < production_count; right++)
        {
          set_assign_set(&working_set, &this_symbol->selectors[left]);
          set_intersect_set(&working_set, &this_symbol->selectors[right]);
          set_intersect_set(&working_set, &terminal_mask);

          this_symbol->ll1_selector_nondetermined |= set_cardinality(&working_set) != 0;
        }

      set_free(&working_set);

      this_symbol->ll1_nondetermined = this_symbol->ll1_follow_nondetermined || this_symbol->ll1_selector_nondetermined;
    }
  }
  set_free(&terminal_mask);
}



static void gram_calculate_immediate_instance_follows(grammar *this_gram)
{
  // Calculate instance and production counts for each nonterminal
  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->rule_tree != NULL)
    {
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);       /* Iterate over productions */
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        for (gram_edge *this_element_edge = this_production_edge;
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          if (this_element_edge->symbol_table_entry->rule_tree != NULL)
            this_symbol->instance_count++;
      }

      if (this_symbol->instance_count != 0)
      {
        if (this_symbol->immediate_instance_follow != NULL)
          mem_free(this_symbol->immediate_instance_follow);

        this_symbol->immediate_instance_follow = (set_ *) mem_calloc(this_symbol->instance_count, sizeof(set_));
      }

    }

  // Calculate instance follow sets
  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->rule_tree != NULL)
    {
      unsigned instance_count = 0;
      unsigned production_count = 0;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);       /* Iterate over productions */
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), production_count++)
      {
        for (gram_edge *this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        {
          if (this_element_edge->symbol_table_entry->rule_tree != NULL)
          {
            int reached_end_of_production = 1;

            for (gram_edge *this_running_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge));
                 this_running_edge != NULL;
                 this_running_edge = (gram_edge *) graph_next_out_edge(graph_destination(this_running_edge)))
              {
                set_unite_element(&this_symbol->immediate_instance_follow[instance_count], this_running_edge->symbol_table_entry->symbol_number);
                if (!set_includes_element(&this_running_edge->symbol_table_entry->first, GRAM_EPSILON_SYMBOL_NUMBER))
                {
                  reached_end_of_production = 0;
                  break;
                }
              }

            // Take out any epsilons we accumulated along the way
            set_difference_element(&this_symbol->immediate_instance_follow[instance_count], GRAM_EPSILON_SYMBOL_NUMBER);

            // Now put it back in if we ran along the whole production
            if (reached_end_of_production)
              set_unite_element(&this_symbol->immediate_instance_follow[instance_count], GRAM_EPSILON_SYMBOL_NUMBER);

            instance_count++;
          }
        }
      }
    }
}

static void gram_calculate_move_tos(grammar * this_gram)
{
  set_ working_set = SET_NULL;

  set_unite_element(&working_set, this_gram->first_level_0_slot);   // force size of set to be as large as necessary

  for (gram_symbols_data *this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      this_symbol->move_tos = (unsigned**) mem_calloc(this_gram->first_level_0_slot ,sizeof(unsigned*));

      // Compute move_to(epsilon), i.e. the leaders
      set_clear(&working_set);
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
        set_unite_element(&working_set, graph_atom_number(graph_destination(this_production_edge)));

      if (set_cardinality(&working_set) != 0)
        this_symbol->move_tos[GRAM_EPSILON_SYMBOL_NUMBER] = set_array(&working_set);


      for (gram_symbols_data *this_move_on_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
           this_move_on_symbol != NULL;
           this_move_on_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_move_on_symbol))
      {
        set_clear(&working_set);

        for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
             this_production_edge != NULL;
             this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
        {
          gram_edge *this_forward_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));

          if (this_forward_edge != NULL && this_forward_edge->symbol_table_entry == this_move_on_symbol)
            set_unite_element(&working_set, graph_atom_number(graph_destination(this_forward_edge)));
        }

        if (set_cardinality(&working_set) != 0)
          this_symbol->move_tos[this_move_on_symbol->symbol_number] = set_array(&working_set);
      }
    }

  set_free(&working_set);
}

static void gram_calculate_level_1_nonterminal_instances(grammar * this_gram)
{
  set_ working_set = SET_NULL;

  for (gram_symbols_data *this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      set_clear(&working_set);

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
          gram_edge *this_forward_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));

          if (this_forward_edge != NULL && this_forward_edge->symbol_table_entry->symbol_number >= this_gram->first_nonterminal)
            set_unite_element(&working_set, this_forward_edge->instance_number);
      }

      if (set_cardinality(&working_set) != 0)
        this_symbol->level_1_nonterminal_instances = set_array(&working_set);
    }

  set_free(&working_set);
}

static void gram_calculate_reductions(grammar * this_gram)
{
  set_clear(&this_gram->reductions);
  set_clear(&this_gram->right_nullable_reductions);
  set_clear(&this_gram->start_rule_reductions);
  set_clear(&this_gram->start_rule_right_nullable_reductions);

  for (gram_symbols_data *this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        gram_edge *this_element_edge;

        for (this_element_edge = this_production_edge;   // hunt forwards
             graph_next_out_edge(graph_destination(this_element_edge)) != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          ;

        set_unite_element(&this_gram->reductions, graph_atom_number(graph_destination(this_element_edge)));

        if (this_symbol == this_gram->start_rule)
          set_unite_element(&this_gram->start_rule_reductions, graph_atom_number(graph_destination(this_element_edge)));


        for (int go_on = 1;   // hunt backwards
             go_on && this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_in_edge(graph_source(this_element_edge)))
        {
          set_unite_element(&this_gram->right_nullable_reductions, graph_atom_number(graph_destination(this_element_edge)));

          if (this_symbol == this_gram->start_rule)
            set_unite_element(&this_gram->start_rule_right_nullable_reductions, graph_atom_number(graph_destination(this_element_edge)));

          if (!set_includes_element(&this_element_edge->symbol_table_entry->first, GRAM_EPSILON_SYMBOL_NUMBER))
            go_on = 0;
        }
      }
}

static void gram_calculate_maximum_rule_length(grammar * this_gram)
{
  for (gram_symbols_data *this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        gram_edge *this_element_edge;
        unsigned rule_length = 0;

        for (this_element_edge = this_production_edge;   // hunt forwards
             graph_next_out_edge(graph_destination(this_element_edge)) != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          rule_length++;

        if (rule_length > this_gram->maximum_rule_length)
          this_gram->maximum_rule_length = rule_length;
      }
}

static void gram_calculate_prefix_equivalent_slot_index(grammar *this_gram)
{
  if (this_gram->prefix_equivalent_slot_index != NULL)
    mem_free(this_gram->prefix_equivalent_slot_index);

  this_gram->prefix_equivalent_slot_index = (unsigned*) mem_calloc(this_gram->first_dfa_state = this_gram->first_level_0_slot, sizeof(unsigned));
}

set_ nonterminal_visited = SET_NULL;

void gram_tidy(grammar* this_gram, int augment, int recalculate_reachability)
{
  set_unite_element(&(gram_find_symbol_by_id(this_gram, GRAM_EPSILON_SYMBOL_STRING, SCRIPT_NS_SPECIAL)->first), GRAM_EPSILON_SYMBOL_NUMBER);

  gram_fixup_undefined_nonterminals(this_gram);
  if (augment == 1)
    gram_augment_gram(this_gram, 0);   /* don't augment an already augmented grammar */
  else if (augment == 2)
    gram_augment_gram(this_gram, 1);   /* force augmentation */
  gram_number_symbols(this_gram);
  gram_create_symbol_index(this_gram);
  gram_number_slots(this_gram);
  graph_create_node_index(this_gram->rules);

  if (recalculate_reachability)
  {
    if (this_gram->start_rule == NULL)
      text_message(TEXT_FATAL, "attempt to perform reachability analysis on grammar with NULL start rule\n");
    else
    {
      set_clear(&this_gram->reachable);
      gram_calculate_reachability(this_gram->start_rule->rule_tree, &this_gram->reachable);
    }
  }
  gram_calculate_terminal_and_nonterminal_sets(this_gram);
  gram_calculate_first_sets(this_gram);
  gram_calculate_follow_sets(this_gram);
  gram_calculate_ll1(this_gram);
  gram_calculate_immediate_instance_follows(this_gram);
  gram_calculate_move_tos(this_gram);
  gram_calculate_level_1_nonterminal_instances(this_gram);
  gram_calculate_reductions(this_gram);
  gram_calculate_maximum_rule_length(this_gram);
  gram_calculate_prefix_equivalent_slot_index(this_gram);
}

grammar *gram_construct_gram(rdp_tree_node_data * root, rdp_tree_node_data * start_rule, bool tilde_enabled)
{
  gram_symbols_data * epsilon_symbol;
  grammar *this_gram = (grammar *) mem_calloc(1, sizeof(grammar));

  this_gram->tilde_enabled = tilde_enabled;
  this_gram->symbol_table = symbol_new_table("gram symbols", 101, 31, symbol_compare_integer_string, symbol_hash_integer_string, symbol_print_integer_string);
  gram_find_symbol_by_id(this_gram, GRAM_ILLEGAL_SYMBOL_STRING, SCRIPT_NS_SPECIAL)->symbol_number = GRAM_ILLEGAL_SYMBOL_NUMBER;
  (epsilon_symbol = gram_find_symbol_by_id(this_gram, GRAM_EPSILON_SYMBOL_STRING, SCRIPT_NS_SPECIAL))->symbol_number = GRAM_EPSILON_SYMBOL_NUMBER;
  gram_find_symbol_by_id(this_gram, GRAM_EOS_SYMBOL_STRING, SCRIPT_NS_SPECIAL)->symbol_number = GRAM_EOS_SYMBOL_NUMBER;

  this_gram->rules = graph_insert_graph("");

  gram_symbols_data *this_symbol_table_entry;

  /* traverse tree adding nonterminals and terminals to symbol table */
  for (rdp_tree_edge_data *this_rule_edge = (rdp_tree_edge_data *) graph_next_out_edge(root); this_rule_edge != NULL; this_rule_edge = (rdp_tree_edge_data *) graph_next_out_edge(this_rule_edge))
  {
    rdp_tree_node_data *this_rule = (rdp_tree_node_data *) graph_destination(this_rule_edge);

    this_symbol_table_entry = gram_find_symbol_by_id(this_gram, this_rule->id, SCRIPT_NS_NONTERMINAL);

    // Optionally make tilde-d terminal version of this name
    if (tilde_enabled)
    {
      char *new_terminal_id = (char*) mem_calloc(1, strlen(this_rule->id) + strlen(GRAM_TILDE_SUFFIX) + 1);
      new_terminal_id = strcat(new_terminal_id, this_rule->id);
      new_terminal_id = strcat(new_terminal_id, GRAM_TILDE_SUFFIX);

      gram_find_symbol_by_id(this_gram, new_terminal_id, SCRIPT_NS_TERMINAL)->is_tilded = 1;
      mem_free(new_terminal_id);
    }

    gram_node *this_new_rule = (gram_node *) graph_insert_node(sizeof(gram_node), this_gram->rules);

    this_new_rule->symbol_table_entry = this_symbol_table_entry;

    unsigned instance_number = 0;

    if (this_rule->symbol_table_entry == start_rule->symbol_table_entry)
      this_gram->start_rule = this_symbol_table_entry;

    this_symbol_table_entry->rule_tree = this_new_rule;

    for (void *this_production_edge = graph_next_out_edge(this_rule);
         this_production_edge != NULL;
         this_production_edge = graph_next_out_edge(this_production_edge))
    {
      rdp_tree_node_data *this_production = (rdp_tree_node_data *) graph_destination(this_production_edge);
      gram_node *parent = this_new_rule;

      parent = gram_insert_node(parent, epsilon_symbol, 0, 0);

      for (void *this_element_edge = graph_next_out_edge(this_production);
           this_element_edge != NULL;
           this_element_edge = graph_next_out_edge(this_element_edge))
      {
        rdp_tree_node_data *this_element = (rdp_tree_node_data *) graph_destination(this_element_edge);
        gram_action *this_action;

        switch (this_element->symbol_table_entry->value->type)
        {
        case SCRIPT_P_C_ACTION:
          this_action = (gram_action*) mem_calloc(1, sizeof (gram_action));
          this_action->action = script_strdup(this_element->id);
          this_action->next = parent->actions;
          parent->actions = this_action;
          continue;
        case SCRIPT_P_NONTERMINAL:
          this_symbol_table_entry = gram_find_symbol_by_id(this_gram, this_element->id, SCRIPT_NS_NONTERMINAL);
          instance_number++;
          break;
        case SCRIPT_P_TERMINAL:
          this_symbol_table_entry = gram_find_symbol_by_id(this_gram, this_element->id, SCRIPT_NS_TERMINAL);
          break;
        case SCRIPT_P_TERMINAL_ELEMENT:
          this_symbol_table_entry = gram_find_symbol_by_id(this_gram, this_element->id, SCRIPT_NS_TERMINAL_ELEMENT);
          break;
        case SCRIPT_P_EPSILON:
          this_symbol_table_entry = NULL;  /* skip */
          break;
        default:
          text_message(TEXT_FATAL, "internal programming error: unexpected node type found during construct_gram()\n");
        }

        if (this_symbol_table_entry != NULL)
          parent = gram_insert_node(parent, this_symbol_table_entry, instance_number - 1, this_element->tilde);
      }
    }
  }

  gram_tidy(this_gram, 0, 1);

  return this_gram;
}

unsigned gram_slot_prefix_length(grammar* this_gram, unsigned slot)
{
  unsigned length = 0;
  gram_edge *this_edge = (gram_edge*) graph_next_in_edge(graph_node_index(this_gram->rules)[slot]);

  while (this_edge->symbol_table_entry->symbol_number != GRAM_EPSILON_SYMBOL_NUMBER)
  {
    length++;
    this_edge = (gram_edge*) graph_next_in_edge(graph_source(this_edge));
  }

  return length;
}

unsigned gram_slot_suffix_length(grammar* this_gram, unsigned slot)
{
  unsigned length = 0;

  for(gram_edge *this_edge = (gram_edge*) graph_next_out_edge(graph_node_index(this_gram->rules)[slot]);
      this_edge != NULL;
      this_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_edge))
     )
    length++;

  return length;
}

gram_symbols_data *gram_slot_lhs_symbol(grammar* this_gram, unsigned slot)
{
  gram_edge *this_edge = (gram_edge*) graph_next_in_edge(graph_node_index(this_gram->rules)[slot]);

  if (this_edge == NULL)                   // If this is a station, return 0
    return GRAM_ILLEGAL_SYMBOL_NUMBER;

  while (this_edge->symbol_table_entry->symbol_number != GRAM_EPSILON_SYMBOL_NUMBER)
    this_edge = (gram_edge*) graph_next_in_edge(graph_source(this_edge));

  return ((gram_node *) graph_source(this_edge))->symbol_table_entry;
}

char *gram_strip_suffix(char * string, char*suffix)
{
  char * ret_string;

  if (strlen(string) < strlen(suffix) || strcmp(suffix, string + strlen(string) - strlen(suffix)) != 0)
    ret_string = script_strdup(string);
  else
  {
    ret_string = (char*) mem_calloc(1, strlen(string) - strlen(suffix) + 1);
    strncpy(ret_string, string, strlen(string) - strlen(suffix));
  }

  return(ret_string);
}

void gram_tilde(grammar *this_gram, int terminalise)
{
  if (!this_gram->tilde_enabled)
    text_message(TEXT_FATAL, "attempt to tilde a grammar that is not tilde eneabled\n");

  /* Pass one: set up tilde values on edges from source tildes */
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
        {
          if (this_element_edge->tilde)
          {
            char *stripped_id = gram_strip_suffix(this_element_edge->symbol_table_entry->id, GRAM_TILDE_SUFFIX);

            if (terminalise)
            {
              text_printf("Terminalising %s\n", this_element_edge->symbol_table_entry->id);

              char *suffix_id = (char*) mem_calloc(1, strlen(stripped_id) + strlen(GRAM_TILDE_SUFFIX) + 1);
              strcat(suffix_id, stripped_id);
              strcat(suffix_id, GRAM_TILDE_SUFFIX);
              this_element_edge->symbol_table_entry = gram_find_symbol_by_id(this_gram, suffix_id, SCRIPT_NS_TERMINAL);
              mem_free(suffix_id);
            }
            else
              this_element_edge->symbol_table_entry = gram_find_symbol_by_id(this_gram, stripped_id, SCRIPT_NS_NONTERMINAL);

            mem_free(stripped_id);
          }
        }
//  gram_tidy(this_gram, 0, 0);
}


grammar *gram_remove_unreachable_rules(grammar *this_gram)
{
  gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));

  while(this_symbol != NULL)
  {
    gram_symbols_data *next_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol);

    if (next_symbol != NULL && (next_symbol->name_space == SCRIPT_NS_NONTERMINAL || next_symbol->name_space == SCRIPT_NS_TERMINAL))
      if (!set_includes_element(&this_gram->reachable, next_symbol->symbol_number))
      {

        text_printf("Removing symbol %s\n", next_symbol->id);
#if 0
        mem_free(next_symbol->id);
        mem_free(next_symbol->move_tos);  //and what about their individual elemenrs?
        set_free(&next_symbol->first);
        set_free(&next_symbol->follow);
        mem_free(next_symbol->immediate_instance_follow);
#endif
        symbol_free_symbol(next_symbol);
      }

    this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol);
  }

  symbol_print_table(this_gram->symbol_table);

  gram_tidy(this_gram, 0, 1);

  return this_gram;
}

grammar *gram_remove_multiple_direct_recursion(grammar *this_gram)
{
  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      unsigned direct_recursion_count = 0;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))

        for (gram_edge *this_element_edge = this_production_edge;   // hunt forwards
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          if (this_element_edge->symbol_table_entry == this_symbol)
          {
            direct_recursion_count++;
            break;
          }

      if (direct_recursion_count >1)
      {
        text_printf("Symbol %s has %u directly recursive productions: ", this_symbol->id, direct_recursion_count);

        gram_symbols_data* epsilon_symbol = epsilon_symbol = gram_find_symbol_by_id(this_gram, GRAM_EPSILON_SYMBOL_STRING, SCRIPT_NS_SPECIAL);

        char *new_indirect_id =(char*) mem_calloc(strlen(this_symbol->id) + strlen(GRAM_INDIRECT_SUFFIX) + 1, sizeof(char));
        strcat(strcat(new_indirect_id, this_symbol->id), GRAM_INDIRECT_SUFFIX);
        gram_symbols_data *new_indirect_symbol = gram_find_symbol_by_id(this_gram, new_indirect_id, SCRIPT_NS_NONTERMINAL);

        new_indirect_symbol->rule_tree = (gram_node *) graph_insert_node(sizeof(gram_node), this_gram->rules);

        new_indirect_symbol->rule_tree->symbol_table_entry = new_indirect_symbol;

        gram_node *new_production = gram_insert_node(new_indirect_symbol->rule_tree, epsilon_symbol, 0, 0);

        gram_insert_node(new_production, this_symbol, 0, 0);

        text_printf("added symbol %s\n", new_indirect_symbol->id);

        for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
             this_production_edge != NULL;
             this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
          for (gram_edge *this_element_edge = this_production_edge;   // hunt forwards
               this_element_edge != NULL;
               this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
            if (this_element_edge->symbol_table_entry == this_symbol)
              this_element_edge->symbol_table_entry = new_indirect_symbol;
      }
    }

  gram_tidy(this_gram, 0, 1);

  return this_gram;
}

grammar *gram_copy_gram(grammar *this_gram)
{
  return this_gram;  /* KLUDGE: do it properly when we know what a gram looks like */
}

int gram_print_slot(gram_node * this_slot, int vcg)
{
  gram_node *this_rule = this_slot;
  gram_edge *this_element_edge = (gram_edge*) graph_next_in_edge(this_rule);

  if (this_element_edge == NULL) // station
  {
    text_printf("%s ", this_slot->symbol_table_entry->id);
    return 0;
  }
  while (graph_next_in_edge(this_rule) != NULL)
  {
    this_element_edge = (gram_edge*) graph_next_in_edge(this_rule);
    this_rule = (gram_node*) graph_source(this_element_edge);
  }

  gram_print_symbol_id(this_rule->symbol_table_entry);
  text_printf(" ::= ");

  if (graph_destination(this_element_edge) == this_slot)
    if (vcg)
      text_printf("\fb\f02.\f31\fn ");
    else
      text_printf(". ");

  this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge));

  if (this_element_edge == NULL)
      text_printf("#");
  else
    for (;
         this_element_edge != NULL;
         this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
    {
      gram_print_symbol_id(this_element_edge->symbol_table_entry);
      text_printf(" ");

      if ((gram_node*) graph_destination(this_element_edge) == this_slot)
        if (vcg)
          text_printf("\fb\f02.\f31\fn ");
        else
          text_printf(". ");
    }

  return 0;
}

void gram_print_action(gram_action *this_action)
{
  if (this_action != NULL)
  {
    gram_print_action(this_action->next);
    text_printf("\n");
    text_print_C_string(this_action->action);
  }
}

void gram_render(FILE *output_file, grammar *this_gram)
{
  FILE *old_text_stream = text_stream();

  text_redirect(output_file);

  text_printf("graph:{\norientation:top_to_bottom"
  "\nedge.arrowsize:7"
  "\nedge.thickness:1"
  "\ndisplay_edge_labels:yes"
  "\ndirty_edge_labels:yes"
  "\narrowmode:free"
  "\nnode.borderwidth:1\n");

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
       )
    if (this_symbol->rule_tree != NULL)
    {
      text_printf("node:{title:\"%u\" horizontal_order:%u shape:hexagon label:\"%u: %s \"}\n", graph_atom_number(this_symbol->rule_tree), graph_atom_number(this_symbol->rule_tree), graph_atom_number(this_symbol->rule_tree), this_symbol->id);
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        for (gram_edge *this_element_edge = this_production_edge;
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        {
          gram_node *this_node = (gram_node*) graph_destination(this_element_edge);

          text_printf("node:{title:\"%u\" horizontal_order:%u shape:box label:\"%u: ", graph_atom_number(this_node), graph_atom_number(this_node), graph_atom_number(this_node));
          gram_print_slot(this_node,1);

          gram_print_action(this_node->actions);

          text_printf("\"");

          if (set_includes_element(&this_gram->reductions, graph_atom_number(this_node)))
            text_printf(" bordercolor:blue borderwidth:3");
          else
            if (set_includes_element(&this_gram->right_nullable_reductions, graph_atom_number(this_node)))
              text_printf(" bordercolor:cyan borderwidth:3");

          text_printf("}\n");

          text_printf("edge:{label:\"%s", this_element_edge->tilde ? "~" : "");
          gram_print_symbol_id(this_element_edge->symbol_table_entry);

          if (this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_NONTERMINAL)
            text_printf(" [%u]", this_element_edge->instance_number);

          text_printf("\" sourcename:\"%u\" targetname:\"%u\"}\n",
                      graph_atom_number(graph_source(this_element_edge)),
                      graph_atom_number(this_node));
        }
      }
    }
  text_printf("}\n");

  text_redirect(old_text_stream);
}

void gram_write(FILE *output_file, grammar *this_gram)
{
  FILE *old_text_stream = text_stream();

  text_redirect(output_file);

  text_printf("\nGrammar report for start rule ");
  gram_print_symbol_id(this_gram->start_rule);

  if (script_gtb_verbose())
  {
    text_printf("\n\n %6u terminals\n %6u nonterminals\n %6u level 0 slots\n %6u level 1 slots\n %6u level 2 slots\n %6u level 3+ slots\n %6u slots in total\n",
                this_gram->first_nonterminal - this_gram->first_terminal,
                this_gram->first_level_0_slot - this_gram->first_nonterminal,
                this_gram->first_level_1_slot - this_gram->first_level_0_slot,
                this_gram->first_level_2_slot - this_gram->first_level_1_slot,
                this_gram->first_level_3plus_slot - this_gram->first_level_2_slot,
                this_gram->first_header - this_gram->first_level_3plus_slot,
                this_gram->first_header - this_gram->first_level_0_slot);

    text_printf("\nMaximum rule length %u\n", this_gram->maximum_rule_length);
  }

  text_printf("\nGrammar alphabet\n");
  gram_print_symbols(this_gram, 0);

  text_printf("\nGrammar rules\n");
  gram_print_rules(this_gram);

  if (script_gtb_verbose())
  {
    text_printf("\nGrammar sets\n");
    gram_print_sets(this_gram);
  }

  text_printf("\nEnd of grammar report for start rule ");
  gram_print_symbol_id(this_gram->start_rule);
  text_printf("\n");
  text_redirect(old_text_stream);
}

/* Horrible hack: set_callback needs to be able to retrieve the id string for a symbol,
   but the same symbol may be used in various contexts. This internal variable is used
   in conjunction ith gtb_set_render_gram and macro GTB_SET_PRINT_CALLBACK to
   specify the current context. Ghastly. */
grammar *gtb_render_gram;

static int gram_print_id_for_symbol_number_callback(const unsigned this_symbol_number)
{
  return gram_print_symbol_id(gram_find_symbol_by_number(gtb_render_gram, this_symbol_number));
}

void gram_print_set_of_symbols(grammar *this_gram, set_ *this_set)
{
  gtb_render_gram = this_gram;
  set_print_set_callback(this_set, gram_print_id_for_symbol_number_callback, 0);
}

static int gram_print_slot_for_slot_number_callback(const unsigned this_slot_number)
{
  return gram_print_slot(((gram_node**) graph_node_index(gtb_render_gram->rules))[this_slot_number], 0);
//  return text_printf("%u", this_slot_number);

}

void gram_print_set_of_slots(grammar *this_gram, set_ *this_set)
{
  gtb_render_gram = this_gram;
  set_print_set_callback(this_set, gram_print_slot_for_slot_number_callback, 0);
}

int gram_print_slot_by_number(grammar *this_gram, unsigned slot_number, int vcg)
{
  return gram_print_slot(((gram_node**) graph_node_index(this_gram->rules))[slot_number], vcg);
}

unsigned *gram_slot_suffix_by_number(grammar *this_gram, unsigned slot_number)
{
  gram_node *this_slot = ((gram_node**) graph_node_index(this_gram->rules))[slot_number];
  gram_edge *this_element_edge;
  unsigned count = 0;

  for (this_element_edge = (gram_edge*) graph_next_out_edge(this_slot);
       this_element_edge != NULL;
       this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
    count++;

  unsigned *suffix = (unsigned*) mem_malloc((count+1) * sizeof(unsigned));

  *suffix = count;

  count = 1;

  for (this_element_edge = (gram_edge*) graph_next_out_edge(this_slot);
       this_element_edge != NULL;
       this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
    suffix[count++] = this_element_edge->symbol_table_entry->symbol_number;

  return suffix;
}

unsigned gram_previous_slot(grammar* this_gram, unsigned slot_number)
{
  gram_node *this_slot = ((gram_node**) graph_node_index(this_gram->rules))[slot_number];

  return graph_atom_number(graph_source(graph_next_in_edge(this_slot)));
}

unsigned gram_next_slot(grammar* this_gram, unsigned slot_number)
{
  gram_node *this_slot = ((gram_node**) graph_node_index(this_gram->rules))[slot_number];

  return graph_atom_number(graph_destination(graph_next_out_edge(this_slot)));
}

int gram_print_slot_suffix(grammar *this_gram, unsigned * suffix)
{
  int output_count = 0;

  for (int count = 1; count <= *suffix; count++)
  {
    output_count += text_printf(" ");
    output_count += gram_print_symbol_id(gram_find_symbol_by_number(this_gram, suffix[count]));
  }
  return output_count;
}


void gram_tokeniser(FILE *output_file, grammar *this_gram)
{
  fprintf(output_file, "(* Tokeniser generated from GTB *)\n\n");

  fprintf(output_file, "start ::= {\n");

  int symbol_count = this_gram->first_level_0_slot;

  for (int this_symbol_number = GRAM_EOS_SYMBOL_NUMBER + 1; this_symbol_number < symbol_count; this_symbol_number++)
  {
    gram_symbols_data *this_symbol = gram_find_symbol_by_number(this_gram, this_symbol_number);

    if (this_symbol != NULL)
      if (this_symbol->rule_tree == NULL)
        fprintf(output_file, "            '%s' [* text_printf(\"%s \"); *] |\n", this_symbol->id, this_symbol->id);
  }

  fprintf(output_file, "            INTEGER\n          }.\n\ncomment ::= COMMENT('' '').\n");
}

/* Walk a follow set looking for nonterminals, folding in the first sets and removing the nonterminals */
void gram_unwrap_nonterminal_follow_set(grammar *this_gram, set_ *this_set)
{
  unsigned *set_elements = set_array(this_set);

  for (unsigned *this_set_element = set_elements; *this_set_element != SET_END; this_set_element++)
  {
    gram_symbols_data* this_symbol = gram_find_symbol_by_number(this_gram, *this_set_element);
    if (this_symbol->rule_tree != NULL)
      set_unite_set(this_set, &this_symbol->first);
  }
}

grammar *gram_ebnf2bnf(grammar *this_gram)
{
  text_message(TEXT_ERROR, "ebnf2bnf suppressed in version 2.5: use external tool\n");
}

grammar *gram_cnf(grammar *this_gram)
{
  text_message(TEXT_ERROR, "cnf suppressed in version 2.5: use external tool\n");
}

grammar *gram_gnf(grammar *this_gram)
{
  text_message(TEXT_ERROR, "gnf suppressed in version 2.5: use external tool\n");
}

