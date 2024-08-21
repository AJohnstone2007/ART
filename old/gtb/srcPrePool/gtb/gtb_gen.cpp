/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 21 Sep 2000
*
* gtb_gen.c - generator functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#include <stdlib.h>
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

int gen_string_nonterminal_count(grammar * this_gram, unsigned *string)
{
  int nonterminal_count = 0;

  while (*string != 0)
    if (gram_find_symbol_by_number(this_gram, *string++)->name_space == SCRIPT_NS_NONTERMINAL)
      nonterminal_count++;

  return nonterminal_count;
}

static void print_sentential_form(grammar *this_gram, unsigned *string, unsigned line_length, int stripped)
{
  unsigned output_length = 0;

  if (*string == 0)
  {
    if (stripped)
      text_printf("(Empty string)");
    else
      text_printf("%s", GRAM_EPSILON_SYMBOL_STRING);
  }
  else
    while (*string != 0)
    {
      gram_symbols_data *this_symbol = gram_find_symbol_by_number(this_gram, *string++);

      if (!stripped && this_symbol->rule_tree == NULL)
        output_length += text_printf("'%s' ", this_symbol->id);
      else
        output_length += text_printf("%s ", this_symbol->id);

      if (output_length > line_length - 8 && &string != 0)
      {
        text_printf("\n        ");
        output_length = 0;
      }
    }

  text_printf("\n");
}

//#define GEN_TRACE 1
// return a symbol table full of sentential forms from the generator
void* gen_generate(grammar * this_gram, gram_symbols_data *start_symbol, unsigned long limit, unsigned order, int show_all_sentential_forms, int quiet)
{
  typedef struct string_queue_struct
  {
    unsigned *string;
    struct string_queue_struct *next;
  } string_queue_block;
  string_queue_block *string_queue_destination = (string_queue_block *) mem_calloc(1, sizeof(string_queue_block));
  string_queue_block *string_queue_tail = string_queue_destination;
  string_queue_block *string_queue_temp;
  unsigned long counter = 1;
  char *order_string;

  switch (order)
  {
    case SCRIPT_BUILT_IN_left: order_string = "leftmost"; break;
    case SCRIPT_BUILT_IN_right: order_string = "rightmost"; break;
    case SCRIPT_BUILT_IN_random: order_string = "random"; break;
    default: text_message(TEXT_FATAL, "call to gtb_generate() with unrecognised order parameter value\n");
  }

  void *string_table = symbol_new_table("string table", 36587, 36583, symbol_compare_string_of_unsigned, symbol_hash_string_of_unsigned, symbol_print_string_of_unsigned);

  if (!quiet)
    text_printf("\nGenerated %s using %s derivation\n\n", show_all_sentential_forms ? "sentential forms" : "sentences", order_string);

  /* create first string queue item for the start symbol */
  string_queue_destination->string = (unsigned *) mem_calloc(2, sizeof(unsigned));
  *(string_queue_destination->string) = start_symbol->symbol_number;
  symbol_insert_key(string_table, &string_queue_destination->string, sizeof(unsigned*), sizeof(gen_string_symbol));

  do
  {
    string_queue_temp = string_queue_destination;

    /* (Optionally) print the sentential form */
    if (!quiet && (gen_string_nonterminal_count(this_gram, string_queue_temp->string)==0 || show_all_sentential_forms))
    {
      printf("%6lu: ", counter++);
      print_sentential_form(this_gram, string_queue_temp->string, 0, !show_all_sentential_forms);
    }

    /* Expand nonterminal and add new strings to queue */
    unsigned nonterminal_count = gen_string_nonterminal_count(this_gram, string_queue_temp->string);
    if (nonterminal_count > 0)
    {
      unsigned *temp = string_queue_temp->string;
      size_t prefix_length = 0,
       suffix_length = 0;
      unsigned expansion_symbol;
      unsigned expansion_symbol_number;

      switch (order)
      {
        case SCRIPT_BUILT_IN_left: expansion_symbol_number = 1; break;
        case SCRIPT_BUILT_IN_right: expansion_symbol_number = nonterminal_count; break;
        case SCRIPT_BUILT_IN_random: expansion_symbol_number = (rand() % nonterminal_count) + 1; break;
      }

      /* Find length of prefix string up to (but not including) nth nonterminal */
      while (expansion_symbol_number > 0)
      {
        prefix_length++;
        if (gram_find_symbol_by_number(this_gram, *temp++)->name_space == SCRIPT_NS_NONTERMINAL)
          expansion_symbol_number--;
      }

      prefix_length--;

      expansion_symbol = string_queue_temp->string[prefix_length];

      /* Find length of suffix from nth nonterminal to end */
      while (*temp++ != 0)
        suffix_length++;

      /* scan over all productions in the rule for the expansion symbol */
      for (void * this_production_edge = graph_next_out_edge(gram_find_symbol_by_number(this_gram, expansion_symbol)->rule_tree);
           this_production_edge != NULL;
           this_production_edge = graph_next_out_edge(this_production_edge)
        )
      {
        unsigned element_count = 0;
        size_t count;
        unsigned *source,
        *dest;

        /* Count the number of elements in this production */
        for (gram_edge *this_element_edge = (gram_edge *) graph_next_out_edge(graph_destination(this_production_edge));
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
          if (this_element_edge->symbol_table_entry->name_space != SCRIPT_NS_SPECIAL)
            element_count++;

        /* Allocate memory for the new string */
        unsigned* string = (unsigned *) mem_calloc(prefix_length + element_count + suffix_length + 1, sizeof(unsigned));

        /* Copy the prefix */
        for (count = 0, dest = string, source = string_queue_temp->string;
            count < prefix_length;
            count++)
          *dest++ = *source++;

        /* Copy the expansion symbol production's elements */
        for (gram_edge *this_element_edge = (gram_edge *) graph_next_out_edge(graph_destination(this_production_edge));
            this_element_edge != NULL;
            this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        {
          if (this_element_edge->symbol_table_entry->name_space != SCRIPT_NS_SPECIAL)
            *dest++ = this_element_edge->symbol_table_entry->symbol_number;
        }

        /* Copy the sufffix */
        for (count = 0, source++;
            count < suffix_length;
            count++)
          *dest++ = *source++;

        // Look up the string on the table and put into the queue if we haven't seen it before
        #if defined(GEN_TRACE)
        text_printf("Constructed sentential form: ");
        print_sentential_form(this_gram, string, 0, 0);
        #endif

        if (symbol_lookup_key(string_table, &string, NULL) == NULL)
        {
          symbol_insert_key(string_table, &string, sizeof(unsigned*), sizeof(gen_string_symbol));

          #if defined(GEN_TRACE)
          text_printf("New\n");
          #endif
          /* Make a new queue element at the tail end, and advance the tail pointer */
          string_queue_tail->next = (string_queue_block *) mem_calloc(1, sizeof(string_queue_block));
          string_queue_tail = string_queue_tail->next;
          string_queue_tail->string = string;
        }
        else
        {
          #if defined(GEN_TRACE)
          text_printf("Old\n");
          #endif
          mem_free(string);
        }
      }
    }

    /* Advance the destination pointer and free the memory from the expanded string */
    string_queue_destination = string_queue_destination->next;
    mem_free(string_queue_temp);
  }
  while (string_queue_destination != NULL && (limit == 0 || counter <= limit));

  return string_table;
}

