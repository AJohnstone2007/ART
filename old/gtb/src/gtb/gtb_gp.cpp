/*******  ************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 15 October 2003
*
* gtb_gp.cpp - generalised parser functions (not GLR: see gtb_sr.cpp for those)
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
//#define EARLEY_TRACE 1
#define NEW_PACK_STRUCTURE 1

#include <string.h>
#include <ctype.h>
#include <time.h>
#include "graph.h"
#include "memalloc.h"
#include "set.h"
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

clock_t start_time, finish_time;

unsigned *tokens;
unsigned token_count;

struct sppf_payload{unsigned slot; unsigned level; unsigned index; int is_pack_node:1;};
struct sppf_index_type{unsigned level; sppf_payload *sppf_node; unsigned *k; unsigned first_family_slot;} **sppf_index;

set_ *slot_firsts;
unsigned slot_count;
unsigned level;
grammar *sppf_render_grammar;
sppf_payload **sppf_level_nodes;
unsigned epsilon_node_symbol;
unsigned end_of_run_symbol;
unsigned first_token;
unsigned nonterminal_count;
struct earley_node{unsigned index; sppf_payload *sppf_node; earley_node *next;};
earley_node*** earley_sets;
unsigned slot_offset;
unsigned header_offset;
unsigned *header_slots;
unsigned *level_2_slots;
unsigned *terminal_slots;
unsigned *terminal_slot_lookahead;
unsigned *acceptance_slots;
unsigned *successor_slots;

struct leader_range{unsigned lo; unsigned hi;} *nonterminal_slots;

unsigned **nonterminal_instance_slots;
unsigned **nonterminal_instance_successor_slots;
unsigned **tail_slots;
unsigned **tail_successor_slots;

void *sppf;

static void print_earley_slot(unsigned slot, int vcg, int is_pack_node)
{
    if (is_pack_node)
      text_printf("#");
    else if (slot >= first_token && slot < first_token + sppf_render_grammar-> first_nonterminal)
      gram_print_symbol_id(gram_find_symbol_by_number(sppf_render_grammar, slot - first_token));
    else if (slot + slot_offset < sppf_render_grammar->first_level_0_slot)
      gram_print_symbol_id(gram_find_symbol_by_number(sppf_render_grammar, slot + slot_offset));
    else
      gram_print_slot_by_number(sppf_render_grammar, slot + slot_offset, vcg);
}

static void sppf_vcg_print_node(const void * node)
{
  sppf_payload* this_node = (sppf_payload*) node;

  if (this_node->slot == epsilon_node_symbol)
    text_printf("label:\"#, %u, %u\"", this_node->index, this_node->level);
  else if (this_node->is_pack_node)
    text_printf("shape:circle");
  else
  {
    text_printf("label:\"[%lu] ", graph_atom_number(this_node));

    text_printf(" %u ", this_node->slot);

    print_earley_slot(this_node->slot, 1, this_node->is_pack_node);

    text_printf(", %u, %u", this_node->index, this_node->level);

    text_printf("\"\n");
  }
}

static sppf_payload* sppf_add_node(void *sppf, unsigned slot, unsigned level, unsigned index, unsigned first_family_slot)
{
  sppf_payload *this_node = (sppf_payload*) graph_insert_node(sizeof(sppf_payload), sppf);

#if defined(EARLEY_TRACE)
  text_printf("** sppf_add_node [%lu], %u, %u, %u\n", graph_atom_number(this_node), slot, level, index);
#endif

  this_node->slot = slot;
  this_node->level = level;
  this_node->index = index;

  if (slot + nonterminal_count < slot_count + nonterminal_count)
  {
    sppf_index[slot + nonterminal_count][index].level = level;
    sppf_index[slot + nonterminal_count][index].sppf_node = this_node;
    sppf_index[slot + nonterminal_count][index].first_family_slot = first_family_slot;
  }

  return this_node;
}

static sppf_payload* sppf_add_epsilon_node(void *sppf, unsigned level)
{
  sppf_payload *this_node = (sppf_payload*) graph_insert_node(sizeof(sppf_payload), sppf);

#if defined(EARLEY_TRACE)
  text_printf("** sppf_add_epsilon_node [%lu], %u, %u, %u\n", graph_atom_number(this_node), level);
#endif

  this_node->slot = epsilon_node_symbol;
  this_node->level = level;
  this_node->index = level;

  return this_node;
}

static sppf_payload* sppf_add_pack_node(void *sppf, unsigned slot, unsigned level, unsigned index, unsigned k)
{
  sppf_payload *this_node = (sppf_payload*) graph_insert_node(sizeof(sppf_payload), sppf);

#if defined(EARLEY_TRACE)
  text_printf("** sppf_add_pack_node [%lu], %u, %u, %u, k\n", graph_atom_number(this_node), slot, level, index, k);
#endif

  this_node->slot = slot;
  this_node->level = level;
  this_node->index = index;
  this_node->is_pack_node = 1;

  if (sppf_index[slot + nonterminal_count][index].k == NULL)
    sppf_index[slot + nonterminal_count][index].k = (unsigned*) mem_calloc(token_count, sizeof(unsigned));

  sppf_index[slot + nonterminal_count][index].k[k] = level;

  return this_node;
}

static bool sppf_pack_node_exists(unsigned slot, unsigned level, unsigned index, unsigned k)
{
  if (sppf_index[slot + nonterminal_count][index].k == NULL)
    return false;

  return sppf_index[slot + nonterminal_count][index].k[k] == level;
}

/* This is pretty horrible: we just do a linear search of the graph to see if a particular node is already in there */
static sppf_payload* sppf_find_node(unsigned slot, unsigned check_level, unsigned index)
{
#if defined(EARLEY_TRACE)
  printf("** sppf_find_node slot %u, check_level %u, index %u, current_level %u\n", slot, check_level, index, level);
#endif

  if (sppf_index[slot + nonterminal_count][index].level == check_level)
    return sppf_index[slot + nonterminal_count][index].sppf_node;
  else
    return NULL;
}

static char *lex_ip;
static grammar* lex_grammar;

// Do a longest match lex from string lex_ip
// #define LEXER_TRACE 1
static unsigned lex(void)
{
  gram_symbols_data *this_symbol,
  *longest_symbol;
  int longest_length = 0;

  while (!isgraph(*lex_ip) && *lex_ip != 0)
    lex_ip++;

  if (*lex_ip == 0)
  {
#if defined(LEXER_TRACE)
    text_printf("Lex: EOS\n");
#endif
    return GRAM_EOS_SYMBOL_NUMBER;
  }

  for (this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(lex_grammar->symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_TERMINAL)
    {
      int this_length = strlen(this_symbol->id);

      if (strncmp(this_symbol->id, lex_ip, this_length) == 0 && this_length > longest_length)
      {
        longest_symbol = this_symbol;
        longest_length = this_length;
      }
    }

  if (longest_length > 0)
  {
#if defined(LEXER_TRACE)
    text_printf("Lex: %i \'%s\'\n", longest_symbol->symbol_number, longest_symbol->id);
#endif
    lex_ip += longest_length;
    return longest_symbol->symbol_number;
  }
  else
  {
    text_printf("Lex: no match starting at ");
    for (int i = 0; i < 10; i++)
      if (*lex_ip == 0) break; else text_printf("%c", *lex_ip++);
    return GRAM_ILLEGAL_SYMBOL_NUMBER;
  }
}

cyk_table* gp_cyk_recognise(grammar *this_grammar, char *string)
{
  cyk_table *this_cyk_table = (cyk_table*) mem_calloc(1, sizeof(derivation));
  unsigned token_count;

  lex_grammar = this_grammar;

  for (lex_ip = string; lex() != GRAM_EOS_SYMBOL_NUMBER; token_count++)
    ;

  text_printf("Input string contains %u tokens\n", token_count);

  return this_cyk_table;
}

/*********************************************************************************/

static sppf_payload *make_node(unsigned s, unsigned j, unsigned i, sppf_payload * w, sppf_payload *v) /* no Nu at present */
{
  unsigned label;
  bool is_tail_slot = tail_slots[s] != tail_successor_slots[s];

#if defined(EARLEY_TRACE)
  text_printf("make_node: ");
  print_earley_slot(s, 0);
  text_printf(", j=%u, i=%u, w=%p", j, i, w);
  if (w != NULL)
  {
    printf(" [");
    print_earley_slot(w->slot, 0);

    text_printf(", %u, %u", w->level, w->index);
    text_printf(" ]");
  }

  text_printf(", v=%p", v);
  if (v != NULL)
  {
    printf(" [");
    print_earley_slot(v->slot, 0);

    text_printf(", %u, %u", v->level, v->index);
    text_printf(" ]");
  }

  text_printf("\n");
#endif

  if (is_tail_slot)
    label = header_slots[s];
  else
    label = s;

  if (level_2_slots[s] && !is_tail_slot)
  {
#if defined(EARLEY_TRACE)
    text_printf("IS_LEADER_SUCCESSOR_SLOT AND NOT IS_TAIL_SLOT\n");
#endif
    return v;
  }
  else
  {
    sppf_payload *y = sppf_find_node(label, i, j);

#if defined(EARLEY_TRACE)
  if (y == NULL)
    text_printf("NEW\n");
  else
    text_printf("OLD\n");
#endif

    if (y == NULL)
    {
      y = sppf_add_node(sppf, label, i, j, s);

      /* Now go straight to reverse add, since y is new */
      if (w != NULL)
        graph_insert_edge(0, w, y);

      graph_insert_edge(0, v, y);

      return y;
    }

/* Now, we have an old y. Check whether it is packed or not */
    void* right_edge = graph_next_out_edge(y);
    sppf_payload* right_node = (sppf_payload*) graph_destination(right_edge); /* matches v */

    if (!right_node->is_pack_node)  /* not packed */
    {
      void* left_edge = graph_next_out_edge(right_edge);
      sppf_payload* left_node = left_edge == NULL ? NULL : (sppf_payload*) graph_destination(left_edge);  /* matches w */

      if (!(left_node == w && right_node == v))
      /* pack and add */
      {
        /* no need to search for this pack node ! */

        unsigned retrieved_pack_slot = sppf_index[y->slot + nonterminal_count][j].first_family_slot;

        sppf_payload* pack_node = sppf_add_pack_node(sppf, retrieved_pack_slot, i, j, v->index);

        if (left_node != NULL)
        {
          graph_delete_edge(left_edge);
          graph_insert_edge(0, left_node, pack_node);
        }

        graph_delete_edge(right_edge);
        graph_insert_edge(0, right_node, pack_node);
        graph_insert_edge(0, pack_node, y);

        /* no need to search for this pack node! */
        sppf_payload *new_pack_node = sppf_add_pack_node(sppf, s, i, j, v->index);
        graph_insert_edge(0, new_pack_node, y);

        if (w != NULL)
          graph_insert_edge(0, w, new_pack_node);

        graph_insert_edge(0, v, new_pack_node);
      }

      return y;
    }
    else /* packed */
    {
#if defined(NEW_PACK_STRUCTURE)
      if (sppf_pack_node_exists(s, y->level, y->index, v->index))
        return y;
#else
      for (void *pack_edge = right_edge; pack_edge != NULL; pack_edge = graph_next_out_edge(pack_edge))
      {
        sppf_payload *pack_node = (sppf_payload*) graph_destination(pack_edge);

        void *right_edge = graph_next_out_edge(pack_node);

        if ((sppf_payload*) graph_destination(right_edge) != v)
          continue;

        void *left_edge = graph_next_out_edge(right_edge);
        sppf_payload* left_node = left_edge == NULL ? NULL : (sppf_payload*) graph_destination(left_edge);  /* matches w */

        if (w == left_node)
          return y;
      }

#endif
      /* Not found, so add new packed family */
      sppf_payload *new_pack_node = sppf_add_pack_node(sppf, s, i, j, v->index);

      graph_insert_edge(0, new_pack_node, y);

      if (w != NULL)
        graph_insert_edge(0, w, new_pack_node);

      graph_insert_edge(0, v, new_pack_node);

      return y;
    }
  }
}

static bool earley_add(char *message, unsigned slot, unsigned level, unsigned index, sppf_payload *sppf_node)
{
#if defined(EARLEY_TRACE)
  text_printf("%s earley_add: ", message);
  print_earley_slot(slot, 0);
  text_printf(", level %u, index %u, SPPF node %p", level, index, sppf_node);
  if (sppf_node != NULL)
  {
    printf(" [");
    print_earley_slot(sppf_node->slot, 0);

    text_printf(", %u, %u", sppf_node->level, sppf_node->index);
    text_printf(" ]");
  }

  text_printf("\n");
#endif

  for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
    if (temp->index == index && temp->sppf_node == sppf_node)
      return false;

  earley_node *temp = (earley_node*) mem_malloc(sizeof(earley_node));

  temp->index = index;
  temp->sppf_node = sppf_node;
  temp->next = earley_sets[slot][level];
  earley_sets[slot][level] = temp;

  return true;
}

derivation* gp_earley_parse(grammar *this_grammar, char *string, int lookahead)
{
  sppf_render_grammar = this_grammar;
  sppf = graph_insert_graph("Earley SPPF");

  /* 0. Precompute slot indices */
  slot_offset = this_grammar->first_level_1_slot;
  header_offset = this_grammar->first_level_1_slot - this_grammar->first_level_0_slot;
  slot_count = this_grammar->first_header - slot_offset;
  unsigned nonterminal_offset = this_grammar->first_nonterminal;
  nonterminal_count = this_grammar->first_level_0_slot - nonterminal_offset;
  epsilon_node_symbol = this_grammar->first_header;
  end_of_run_symbol = epsilon_node_symbol + 1;
  first_token = end_of_run_symbol + 1;

#if defined(EARLEY_TRACE)
  text_printf("Grammar contains %u slots\n", slot_count);
#endif

  struct action{unsigned slot; unsigned index; sppf_payload *sppf_node;} *R = (action*) mem_malloc(100000*sizeof(action));
  unsigned R_pointer = 0;

  slot_firsts = (set_*) mem_calloc(slot_count, sizeof(set_));
  level_2_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  acceptance_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  header_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  successor_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  terminal_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  terminal_slot_lookahead = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  nonterminal_slots = (leader_range*) mem_calloc(slot_count, sizeof(leader_range));

  unsigned *H = (unsigned*) mem_calloc(nonterminal_count, sizeof(unsigned));
  for (unsigned temp = 0; temp < nonterminal_count; temp++)
    H[temp] = UINT_MAX;

  sppf_payload **H_sppf = (sppf_payload**) mem_calloc(nonterminal_count, sizeof(sppf_payload*));
  unsigned *nonterminal_instance_counts = (unsigned *) mem_calloc(nonterminal_count , sizeof(unsigned));

  nonterminal_instance_slots = (unsigned **) mem_calloc(nonterminal_count , sizeof(unsigned*));
  nonterminal_instance_successor_slots = (unsigned **) mem_calloc(nonterminal_count , sizeof(unsigned*));
  tail_slots = (unsigned**) mem_calloc(slot_count, sizeof(unsigned*));
  tail_successor_slots = (unsigned**) mem_calloc(slot_count, sizeof(unsigned*));

  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    header_slots[slot] = graph_atom_number(gram_slot_lhs_symbol(this_grammar, slot+slot_offset)->rule_tree) - slot_offset;


    gram_node *slot_node = (gram_node*) graph_node_index(this_grammar->rules)[slot + slot_offset];
    gram_edge *slot_edge = (gram_edge*) graph_next_out_edge(slot_node);

    level_2_slots[slot] = (slot + slot_offset >= this_grammar->first_level_2_slot) && (slot + slot_offset < this_grammar->first_level_3plus_slot);

    unsigned successor_slot = graph_atom_number(graph_destination(slot_edge)) - slot_offset;

    successor_slots[slot] = successor_slot;

    if (slot_edge != NULL)
    {
      if (slot_edge->symbol_table_entry->symbol_number < this_grammar->first_nonterminal)
      {
        terminal_slots[slot] = successor_slot;
        terminal_slot_lookahead[slot] = slot_edge->symbol_table_entry->symbol_number;
      }
      else
      {
        /* Update instance counts for next step */
        nonterminal_instance_counts[slot_edge->symbol_table_entry->symbol_number - nonterminal_offset]++;

        /* note reversal of order: rules tree built with head insertion, so children are in reverse order! */
        nonterminal_slots[slot].hi = graph_atom_number(graph_destination(graph_next_out_edge(slot_edge->symbol_table_entry->rule_tree))) - slot_offset + 1;
        nonterminal_slots[slot].lo = graph_atom_number(graph_destination(graph_final_out_edge(slot_edge->symbol_table_entry->rule_tree))) - slot_offset;
      }
    }
  }

  /* Initialise slot_first sets */
  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    gram_symbols_data *lhs_symbol = gram_slot_lhs_symbol(this_grammar, slot + slot_offset);
    gram_node *slot_node = (gram_node*) graph_node_index(this_grammar->rules)[slot + slot_offset];

    gram_edge *slot_edge = (gram_edge*) graph_next_out_edge(slot_node);

    if (slot_edge == NULL)  /* tail slot */
    {
      set_assign_set(&slot_firsts[slot], &lhs_symbol->follow);
      gram_unwrap_nonterminal_follow_set(this_grammar, &slot_firsts[slot]);
    }
    else
    {
      gram_symbols_data *slot_symbol = slot_edge->symbol_table_entry;
      unsigned instance_number = slot_edge->instance_number;

      if (slot_symbol->name_space == SCRIPT_NS_TERMINAL)
        set_assign_element(&slot_firsts[slot], slot_symbol->symbol_number);
      else
      {
        set_assign_set(&slot_firsts[slot], &slot_symbol->first);

        if (set_includes_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER))
        {
          set_ temp = SET_NULL;
          set_difference_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER);

          set_assign_set(&temp, &lhs_symbol->immediate_instance_follow[instance_number]);
          gram_unwrap_nonterminal_follow_set(this_grammar, &temp);
          set_unite_set(&slot_firsts[slot], &temp);
          set_free(&temp);
        }

        if (set_includes_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER))
        {
          set_ temp = SET_NULL;
          set_difference_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER);

          set_assign_set(&temp, &lhs_symbol->follow);
          gram_unwrap_nonterminal_follow_set(this_grammar, &temp);
          set_unite_set(&slot_firsts[slot], &temp);
          set_free(&temp);
        }
      }
    }
  }

  /* Allocate the successor arrays and clear counters */
  for (unsigned this_nonterminal = 0; this_nonterminal < nonterminal_count; this_nonterminal++)
  {
#if defined(EARLEY_TRACE)
    text_printf("Nonterminal %u has %u instances\n", this_nonterminal, nonterminal_instance_counts[this_nonterminal]);
#endif

    nonterminal_instance_slots[this_nonterminal] = (unsigned*) mem_calloc(nonterminal_instance_counts[this_nonterminal] + 1, sizeof (unsigned));
    *nonterminal_instance_slots[this_nonterminal] = end_of_run_symbol;
    nonterminal_instance_successor_slots[this_nonterminal] = (unsigned*) mem_calloc(nonterminal_instance_counts[this_nonterminal] + 1, sizeof (unsigned));
    nonterminal_instance_counts[this_nonterminal] = 0;
  }

  /* Load the successor arrays, and point the tail slots to them */
  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    gram_node *slot_node = (gram_node*) graph_node_index(this_grammar->rules)[slot + slot_offset];
    gram_edge *slot_edge = (gram_edge*) graph_next_out_edge(slot_node);
    unsigned slot_symbol;
    unsigned lhs_symbol = gram_slot_lhs_symbol(this_grammar, slot + slot_offset)->symbol_number - nonterminal_offset;

    if (slot_edge == NULL)
    {
      tail_successor_slots[slot] = nonterminal_instance_successor_slots[lhs_symbol];
      tail_slots[slot] = nonterminal_instance_slots[lhs_symbol];
      if (gram_slot_lhs_symbol(this_grammar, slot + slot_offset) == this_grammar->start_rule)
        acceptance_slots[slot] = 1;
    }
    else if ((slot_symbol = slot_edge->symbol_table_entry->symbol_number) >= this_grammar->first_nonterminal)
    {
      unsigned successor_slot = graph_atom_number(graph_destination(slot_edge)) - slot_offset;

      slot_symbol -= nonterminal_offset;

      nonterminal_instance_slots[slot_symbol][nonterminal_instance_counts[slot_symbol]] = slot;
      nonterminal_instance_successor_slots[slot_symbol][nonterminal_instance_counts[slot_symbol]++] = successor_slot;
      nonterminal_instance_slots[slot_symbol][nonterminal_instance_counts[slot_symbol]] = end_of_run_symbol;
    }
  }
  /* --- debug output follows --- */

#if defined(EARLEY_TRACE)
  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    text_printf("slot_first( ");
    gram_print_slot_by_number(this_grammar, slot + slot_offset, 0);
    text_printf(") = { ");
    gram_print_set_of_symbols(this_grammar, &slot_firsts[slot]);
    text_printf(" }\n");
  }

  for (unsigned this_nonterminal = 0; this_nonterminal < nonterminal_count; this_nonterminal++)
  {
    gram_print_symbol_id(gram_find_symbol_by_number(this_grammar, this_nonterminal + nonterminal_offset));
    printf(": %u", nonterminal_instance_counts[this_nonterminal]);

    for (unsigned this_instance_slot = 0; nonterminal_instance_slots[this_nonterminal][this_instance_slot] != end_of_run_symbol; this_instance_slot++)
      printf(" [%u->%u]",
             nonterminal_instance_slots[this_nonterminal][this_instance_slot] + slot_offset,
             nonterminal_instance_successor_slots[this_nonterminal][this_instance_slot] + slot_offset);
    printf("\n");
  }

  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    printf("%u H%u: (%u, %u)\n", slot + slot_offset, header_slots[slot] + slot_offset, nonterminal_slots[slot].lo + slot_offset, nonterminal_slots[slot].hi + slot_offset);
    gram_print_slot_by_number(this_grammar, slot + slot_offset, 0);

    if (level_2_slots[slot])
      printf("LEVEL 2 ");

    if (tail_slots[slot] != NULL)
    {
      unsigned *this_instance_slot = tail_slots[slot];
      unsigned *this_instance_successor_slot = tail_successor_slots[slot];

      for (; *this_instance_slot != end_of_run_symbol; this_instance_slot++, this_instance_successor_slot++)
        printf(" [%u->%u]", *this_instance_slot + slot_offset, *this_instance_successor_slot + slot_offset);
    }

    printf("\n");
  }

  for (unsigned slot = 0; slot < slot_count; slot++)
    printf("%u: %u '%u' %s\n", slot + slot_offset, terminal_slots[slot] + slot_offset, terminal_slot_lookahead[slot], acceptance_slots[slot]?"(accept)" : "");

  printf("****************");

  for (unsigned slot = 0; slot < slot_count; slot++)
    printf("%u -> %u\n", slot + slot_offset, successor_slots[slot] + slot_offset);
#endif

  /* 1. Tokenise the input */
  token_count = 0;

  lex_grammar = this_grammar;
  for (lex_ip = string; lex() != GRAM_EOS_SYMBOL_NUMBER; token_count++)
    ;
#if defined(EARLEY_TRACE)
  text_printf("Input string contains %u tokens\n", token_count);
#endif

  token_count++;

  sppf_level_nodes = (sppf_payload**) mem_calloc(token_count+1, sizeof(sppf_payload *));

  tokens = (unsigned*) mem_malloc((token_count)* sizeof(unsigned));
  unsigned *input_token = tokens;

  lex_ip = string;
  while((*input_token++ = lex()) != GRAM_EOS_SYMBOL_NUMBER)
    ;

  /* 2. Create datastructure to hold Earley sets
        We use essentially a two dimensional array of linked lists, index by slot, then frontier, then index.
  */
  earley_sets = (earley_node***) mem_calloc(slot_count, sizeof(earley_node**));

  for (int this_slot = 0; this_slot < slot_count; this_slot++)
    earley_sets[this_slot] = (earley_node**) mem_calloc(token_count, sizeof (earley_node*));

  derivation* this_derivation = (derivation*) mem_calloc(1, sizeof(derivation));

  sppf_index = (sppf_index_type**) mem_calloc(slot_count + nonterminal_count, sizeof(sppf_index_type*));

  for (unsigned temp = 0; temp < slot_count + nonterminal_count; temp++)
    sppf_index[temp] = (sppf_index_type*) mem_calloc(token_count, sizeof(sppf_index_type));

  start_time = clock();

  /* 3. Initialise E_0 using rule 1*/
  for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_grammar->start_rule->rule_tree);
       this_production_edge != NULL;
       this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
  {
    unsigned slot = graph_atom_number(graph_destination(this_production_edge)) - slot_offset;

    if (!lookahead || set_includes_element(&slot_firsts[slot], tokens[level]))
    {
      earley_add("Rule 1 (initialisation)", slot, 0, 0, NULL);
      R[R_pointer].slot = slot;
      R[R_pointer].index = 0;
      R[R_pointer].sppf_node = NULL;
      R_pointer++;
    }
  }

  /* 4. Iterate over all frontiers with worklist */
  for (level = 0; level < token_count; level++)
  {
#if defined(EARLEY_TRACE)
    text_printf("Processing level %i\n", level);
#endif

    sppf_level_nodes[level] = (sppf_payload*) graph_next_node(sppf);

    /* 4.1 Use rule 2 to initialise from E_i - 1 : update to use Q*/

    if (level != 0)  /* first level pre-initialised in step 3 */
    {
      sppf_payload *v = sppf_add_node(sppf, tokens[level - 1] + first_token, level, level - 1, 0);
      for (unsigned slot = 0; slot < slot_count; slot++)
      {
        unsigned successor_slot;
        if (((successor_slot = terminal_slots[slot]) != 0) && tokens[level - 1] == terminal_slot_lookahead[slot])
        if (!lookahead || set_includes_element(&slot_firsts[successor_slot], tokens[level]))
          for (earley_node *temp = earley_sets[slot][level-1]; temp != NULL; temp = temp->next)
          {
            /* Note, under rule 2 an element cannot be addded twice, so don't check for inclusion */
            sppf_payload *sppf_node = make_node(successor_slot, temp->index, level, temp->sppf_node, v);
            earley_add("Rule 2 (shift)", successor_slot, level, temp->index, sppf_node);

            R[R_pointer].slot = successor_slot;
            R[R_pointer].index = temp->index;
            R[R_pointer].sppf_node = sppf_node;
            R_pointer++;
          }
      }
    }

    while (R_pointer != 0)
    {
      R_pointer--;
      unsigned slot = R[R_pointer].slot;
      unsigned k = R[R_pointer].index;
      sppf_payload *w = R[R_pointer].sppf_node;

      unsigned lo_slot;

      if ((lo_slot = nonterminal_slots[slot].lo) != nonterminal_slots[slot].hi) /* 4.2.1 Rule 3 */
      {
        unsigned hi_slot = nonterminal_slots[slot].hi;

        for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
          for (unsigned leader_slot = lo_slot; leader_slot<hi_slot; leader_slot++)
            if (!lookahead || set_includes_element(&slot_firsts[leader_slot], tokens[level]))
            if (earley_add("Rule 3 (nonterminal slot)", leader_slot, level, level, NULL))
            {
              R[R_pointer].slot = leader_slot;
              R[R_pointer].index = level;
              R[R_pointer].sppf_node = NULL;
              R_pointer++;
            }

        unsigned header_slot = header_slots[lo_slot] + header_offset;
        unsigned successor_slot = successor_slots[slot];

        if (H[header_slot] == level)
        {
          sppf_payload *y = make_node(successor_slot, k, level, w, H_sppf[header_slot]);

          /* Test if beta begins with a nonterminal, or is empty */
          if (tail_slots[successor_slot] != NULL || (nonterminal_slots[successor_slot].lo != nonterminal_slots[successor_slot].hi))  /* Nonterminal */
          {
            if (!lookahead || set_includes_element(&slot_firsts[successor_slot], tokens[level]))

            if (earley_add("Rule 3 contingent", successor_slot, level, k, y))
            {
              R[R_pointer].slot = successor_slot;
              R[R_pointer].index = k;
              R[R_pointer].sppf_node = y;
              R_pointer++;
            }
          }
        }
      }
      else if (tail_slots[slot] != NULL) /* 4.2.2 Rule 4 */
      if (!lookahead || set_includes_element(&slot_firsts[slot], tokens[level]))
      {
        /* Look for occupied slots on level k that have this slot symbol after the dot */
        unsigned *this_instance_slot = tail_slots[slot];
        unsigned *this_instance_successor_slot = tail_successor_slots[slot];
        unsigned header_slot = header_slots[slot];

        if (w == NULL)
        {
          w = sppf_find_node(header_slot, level, level);

          if (w == NULL)
          {
            w = sppf_add_node(sppf, header_slot, level, level, slot);

            graph_insert_edge(0, sppf_add_epsilon_node(sppf, level), w);
          }
          else /* old w - check for epsilon family */
          {
            void* right_edge = graph_next_out_edge(w);

            if (right_edge == NULL)
              text_message(TEXT_FATAL, "Rule 4 old w has no children\n");

            sppf_payload* right_node = (sppf_payload*) graph_destination(right_edge);

            if (!right_node->is_pack_node)  /* not packed */
            {
              void* left_edge = graph_next_out_edge(right_edge);
              sppf_payload* left_node = left_edge == NULL ? NULL : (sppf_payload*) graph_destination(left_edge);

              if (right_node->slot != epsilon_node_symbol)
              /* pack and add */
              {
                unsigned retrieved_pack_slot = sppf_index[w->slot + nonterminal_count][level].first_family_slot;

                  if (right_node->index != level)
                    text_message(TEXT_FATAL, "Rule 4 old w right node->index not equal to level\n");

                sppf_payload* pack_node = sppf_add_pack_node(sppf, retrieved_pack_slot, level, level, right_node->index);

                if (left_node != NULL)
                {
                  graph_delete_edge(left_edge);
                  graph_insert_edge(0, left_node, pack_node);
                }

                graph_delete_edge(right_edge);
                graph_insert_edge(0, right_node, pack_node);

                graph_insert_edge(0, pack_node, w);

                sppf_payload *new_pack_node = sppf_add_pack_node(sppf, w->slot, level, level, 0);
                graph_insert_edge(0, new_pack_node, w);

                graph_insert_edge(0, sppf_add_epsilon_node(sppf, level), new_pack_node);
              }
              else /* packed */
              {
                bool found = false;

#if defined(NEW_PACK_STRUCTURE)
                found = sppf_pack_node_exists(w->slot, w->level, w->index, right_node->index);
#else
                for (void *pack_edge = right_edge; pack_edge != NULL; pack_edge = graph_next_out_edge(pack_edge))
                {
                  sppf_payload *pack_node = (sppf_payload*) graph_destination(pack_edge);

                  void *right_edge = graph_next_out_edge(pack_node);

                  if (((sppf_payload*) graph_destination(right_edge))->slot == epsilon_node_symbol)
                  {
                    found = true;
                    break;
                  }
                }
#endif

                /* Not found, so add new packed family */
                if (!found)
                {
                  sppf_payload *new_pack_node = sppf_add_pack_node(sppf, w->slot, level, level, 0);
                  graph_insert_edge(0, new_pack_node, w);

                  graph_insert_edge(0, sppf_add_epsilon_node(sppf, level), new_pack_node);
                }
              }
            }
          }
        }

        /* Now we have a clean w with an epsilon family. Phew. */
        if (k == level)
        {
          H[header_slot + header_offset] = level;
          H_sppf[header_slot + header_offset] = w;
        }

        for (; *this_instance_slot != end_of_run_symbol; this_instance_slot++, this_instance_successor_slot++)  /* scan over instance slots for this lhs */
          if (!lookahead || set_includes_element(&slot_firsts[*this_instance_successor_slot], tokens[level]))
          for (earley_node *temp = earley_sets[*this_instance_slot][k]; temp != NULL; temp = temp->next)  /* scan over (D::= mu B nu,h) on level k */
          {
            sppf_payload *y = make_node(*this_instance_successor_slot, temp->index, level, temp->sppf_node, w);

            if (earley_add("Rule 4 (tail slot)", *this_instance_successor_slot, level, temp->index, y))
            {
              R[R_pointer].slot = *this_instance_successor_slot;
              R[R_pointer].index = temp->index;
              R[R_pointer].sppf_node = y;
              R_pointer++;
            }
          }
      }
    }
  }

  finish_time = clock();

  /* 5. Debug: print out Earley sets */
#if 0
  for (unsigned level = 0; level < token_count; level++)
  {
    unsigned cardinality = 0;

    for (unsigned slot = 0; slot < slot_count; slot++)
      for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
        cardinality++;

    text_printf("\n|E_%u| = %u { ", level, cardinality);

    if (cardinality < 16)
      for (unsigned slot = 0; slot < slot_count; slot++)
      {
        for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
        {
          text_printf("\n(");
          gram_print_slot_by_number(this_grammar, slot + slot_offset, 0);
          text_printf(", %u)", temp->index);
        }
      }
    else
      text_printf(" ...");

    text_printf(" }\n");
  }
#endif

  /* 6. Test for acceptance */
  for (unsigned slot = 0; slot < slot_count; slot++)
    if (acceptance_slots[slot] && earley_sets[slot][token_count -1 ] != NULL)
      this_derivation->accept = 1;

  double run_time = 0;

 if (script_gtb_verbose()) {
  text_printf("%s\n", this_derivation->accept ? "Accept" : "Reject");

  /* 7. Statistics output */

  text_printf("Earley cubic parser ran in %lf CPU s\n", run_time);

  unsigned cardinality = 0;
  for (unsigned level = 0; level < token_count; level++)
    for (unsigned slot = 0; slot < slot_count; slot++)
      for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
        cardinality++;

  text_printf("Sum over Earley set cardinalities: %u\n", cardinality);

  unsigned nonterminal_nodes = 0;
  unsigned terminal_nodes = 0;
  unsigned intermediate_nodes = 0;
  unsigned pack_nodes = 0;
  unsigned epsilon_nodes = 0;

  for (sppf_payload *this_node = (sppf_payload*) graph_next_node(sppf); this_node != NULL; this_node = (sppf_payload*) graph_next_node(this_node))
  {
    unsigned slot = this_node->slot;

    if (this_node->is_pack_node)
      pack_nodes++;
    else if (slot == epsilon_node_symbol)
      epsilon_nodes++;
    else if (slot >= first_token && slot < first_token + sppf_render_grammar-> first_nonterminal)
      terminal_nodes++;
    else if (slot + slot_offset < sppf_render_grammar->first_level_1_slot)
      nonterminal_nodes++;
    else
      intermediate_nodes++;
  }

  text_printf("%u nonterminal nodes\n"
              "%u terminal nodes\n"
              "%u intermediate nodes\n"
              "%u pack nodes\n"
              "%u epsilon nodes\n",
              nonterminal_nodes, terminal_nodes, intermediate_nodes, pack_nodes, epsilon_nodes);

  if (graph_atom_number(graph_next_node(sppf)) < 1000)
  {
    text_redirect(fopen("sppf.vcg", "w"));
    graph_vcg(sppf, NULL, sppf_vcg_print_node, NULL);
    text_redirect(stdout);
  }
  else
    text_printf("VCG rendering of large SPPF suppressed\n");

//  for (sppf_payload * this_node = (sppf_payload*) graph_next_node(sppf); this_node != NULL; this_node = (sppf_payload*) graph_next_node(this_node))
//    text_printf("slot, %u, level, %u, index, %u\n", this_node->slot, this_node->level, this_node->index);

  /* Return results */
  }

  // Now output CSV record
  text_printf("%i,Earley,%s,%f,%f,%f,%f,%f,%f,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i\n",
               strlen(string),
               this_derivation->accept ? "Accept" : "Reject",
               0.0,
               0.0,
               run_time,
               0.0,
               0.0,
               0.0,
               token_count,
               0,
               0,
               0,
               0,
               0,
               0,
               0,
               0,
               0,
               0,
               0);

  return this_derivation;
}

/******************************************************************************/

derivation* gp_earley_recognise(grammar *this_grammar, char *string, int lookahead)
{
  /* 0. Precompute slot indices */
  slot_offset = this_grammar->first_level_1_slot;
  header_offset = this_grammar->first_level_1_slot - this_grammar->first_level_0_slot;
  slot_count = this_grammar->first_header - slot_offset;
  unsigned nonterminal_offset = this_grammar->first_nonterminal;
  nonterminal_count = this_grammar->first_level_0_slot - nonterminal_offset;
  epsilon_node_symbol = this_grammar->first_header;
  end_of_run_symbol = epsilon_node_symbol + 1;
  first_token = end_of_run_symbol + 1;

#if defined(EARLEY_TRACE)
  text_printf("Grammar contains %u slots\n", slot_count);
#endif

  struct action{unsigned slot; unsigned index;} *R = (action*) mem_malloc(100000*sizeof(action));
  unsigned R_pointer = 0;

  slot_firsts = (set_*) mem_calloc(slot_count, sizeof(set_));
  level_2_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  acceptance_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  header_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  successor_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  terminal_slots = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  terminal_slot_lookahead = (unsigned*) mem_calloc(slot_count, sizeof(unsigned));
  nonterminal_slots = (leader_range*) mem_calloc(slot_count, sizeof(leader_range));

  unsigned *H = (unsigned*) mem_calloc(nonterminal_count, sizeof(unsigned));
  for (unsigned temp = 0; temp < nonterminal_count; temp++)
    H[temp] = UINT_MAX;

  unsigned *nonterminal_instance_counts = (unsigned *) mem_calloc(nonterminal_count , sizeof(unsigned));

  nonterminal_instance_slots = (unsigned **) mem_calloc(nonterminal_count , sizeof(unsigned*));
  nonterminal_instance_successor_slots = (unsigned **) mem_calloc(nonterminal_count , sizeof(unsigned*));
  tail_slots = (unsigned**) mem_calloc(slot_count, sizeof(unsigned*));
  tail_successor_slots = (unsigned**) mem_calloc(slot_count, sizeof(unsigned*));

  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    header_slots[slot] = graph_atom_number(gram_slot_lhs_symbol(this_grammar, slot+slot_offset)->rule_tree) - slot_offset;


    gram_node *slot_node = (gram_node*) graph_node_index(this_grammar->rules)[slot + slot_offset];
    gram_edge *slot_edge = (gram_edge*) graph_next_out_edge(slot_node);

    level_2_slots[slot] = (slot + slot_offset >= this_grammar->first_level_2_slot) && (slot + slot_offset < this_grammar->first_level_3plus_slot);

    unsigned successor_slot = graph_atom_number(graph_destination(slot_edge)) - slot_offset;

    successor_slots[slot] = successor_slot;

    if (slot_edge != NULL)
    {
      if (slot_edge->symbol_table_entry->symbol_number < this_grammar->first_nonterminal)
      {
        terminal_slots[slot] = successor_slot;
        terminal_slot_lookahead[slot] = slot_edge->symbol_table_entry->symbol_number;
      }
      else
      {
        /* Update instance counts for next step */
        nonterminal_instance_counts[slot_edge->symbol_table_entry->symbol_number - nonterminal_offset]++;

        /* note reversal of order: rules tree built with head insertion, so children are in reverse order! */
        nonterminal_slots[slot].hi = graph_atom_number(graph_destination(graph_next_out_edge(slot_edge->symbol_table_entry->rule_tree))) - slot_offset + 1;
        nonterminal_slots[slot].lo = graph_atom_number(graph_destination(graph_final_out_edge(slot_edge->symbol_table_entry->rule_tree))) - slot_offset;
      }
    }
  }

  /* Initialise slot_first sets */
  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    gram_symbols_data *lhs_symbol = gram_slot_lhs_symbol(this_grammar, slot + slot_offset);
    gram_node *slot_node = (gram_node*) graph_node_index(this_grammar->rules)[slot + slot_offset];

    gram_edge *slot_edge = (gram_edge*) graph_next_out_edge(slot_node);

    if (slot_edge == NULL)  /* tail slot */
    {
      set_assign_set(&slot_firsts[slot], &lhs_symbol->follow);
      gram_unwrap_nonterminal_follow_set(this_grammar, &slot_firsts[slot]);
    }
    else
    {
      gram_symbols_data *slot_symbol = slot_edge->symbol_table_entry;
      unsigned instance_number = slot_edge->instance_number;

      if (slot_symbol->name_space == SCRIPT_NS_TERMINAL)
        set_assign_element(&slot_firsts[slot], slot_symbol->symbol_number);
      else
      {
        set_assign_set(&slot_firsts[slot], &slot_symbol->first);

        if (set_includes_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER))
        {
          set_ temp = SET_NULL;
          set_difference_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER);

          set_assign_set(&temp, &lhs_symbol->immediate_instance_follow[instance_number]);
          gram_unwrap_nonterminal_follow_set(this_grammar, &temp);
          set_unite_set(&slot_firsts[slot], &temp);
          set_free(&temp);
        }

        if (set_includes_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER))
        {
          set_ temp = SET_NULL;
          set_difference_element(&slot_firsts[slot], GRAM_EPSILON_SYMBOL_NUMBER);

          set_assign_set(&temp, &lhs_symbol->follow);
          gram_unwrap_nonterminal_follow_set(this_grammar, &temp);
          set_unite_set(&slot_firsts[slot], &temp);
          set_free(&temp);
        }
      }
    }
  }

  /* Allocate the successor arrays and clear counters */
  for (unsigned this_nonterminal = 0; this_nonterminal < nonterminal_count; this_nonterminal++)
  {
#if defined(EARLEY_TRACE)
    text_printf("Nonterminal %u has %u instances\n", this_nonterminal, nonterminal_instance_counts[this_nonterminal]);
#endif

    nonterminal_instance_slots[this_nonterminal] = (unsigned*) mem_calloc(nonterminal_instance_counts[this_nonterminal] + 1, sizeof (unsigned));
    *nonterminal_instance_slots[this_nonterminal] = end_of_run_symbol;
    nonterminal_instance_successor_slots[this_nonterminal] = (unsigned*) mem_calloc(nonterminal_instance_counts[this_nonterminal] + 1, sizeof (unsigned));
    nonterminal_instance_counts[this_nonterminal] = 0;
  }

  /* Load the successor arrays, and point the tail slots to them */
  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    gram_node *slot_node = (gram_node*) graph_node_index(this_grammar->rules)[slot + slot_offset];
    gram_edge *slot_edge = (gram_edge*) graph_next_out_edge(slot_node);
    unsigned slot_symbol;
    unsigned lhs_symbol = gram_slot_lhs_symbol(this_grammar, slot + slot_offset)->symbol_number - nonterminal_offset;

    if (slot_edge == NULL)
    {
      tail_successor_slots[slot] = nonterminal_instance_successor_slots[lhs_symbol];
      tail_slots[slot] = nonterminal_instance_slots[lhs_symbol];
      if (gram_slot_lhs_symbol(this_grammar, slot + slot_offset) == this_grammar->start_rule)
        acceptance_slots[slot] = 1;
    }
    else if ((slot_symbol = slot_edge->symbol_table_entry->symbol_number) >= this_grammar->first_nonterminal)
    {
      unsigned successor_slot = graph_atom_number(graph_destination(slot_edge)) - slot_offset;

      slot_symbol -= nonterminal_offset;

      nonterminal_instance_slots[slot_symbol][nonterminal_instance_counts[slot_symbol]] = slot;
      nonterminal_instance_successor_slots[slot_symbol][nonterminal_instance_counts[slot_symbol]++] = successor_slot;
      nonterminal_instance_slots[slot_symbol][nonterminal_instance_counts[slot_symbol]] = end_of_run_symbol;
    }
  }
  /* --- debug output follows --- */

#if defined(EARLEY_TRACE)
  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    text_printf("slot_first( ");
    gram_print_slot_by_number(this_grammar, slot + slot_offset, 0);
    text_printf(") = { ");
    gram_print_set_of_symbols(this_grammar, &slot_firsts[slot]);
    text_printf(" }\n");
  }

  for (unsigned this_nonterminal = 0; this_nonterminal < nonterminal_count; this_nonterminal++)
  {
    gram_print_symbol_id(gram_find_symbol_by_number(this_grammar, this_nonterminal + nonterminal_offset));
    printf(": %u", nonterminal_instance_counts[this_nonterminal]);

    for (unsigned this_instance_slot = 0; nonterminal_instance_slots[this_nonterminal][this_instance_slot] != end_of_run_symbol; this_instance_slot++)
      printf(" [%u->%u]",
             nonterminal_instance_slots[this_nonterminal][this_instance_slot] + slot_offset,
             nonterminal_instance_successor_slots[this_nonterminal][this_instance_slot] + slot_offset);
    printf("\n");
  }

  for (unsigned slot = 0; slot < slot_count; slot++)
  {
    printf("%u H%u: (%u, %u)\n", slot + slot_offset, header_slots[slot] + slot_offset, nonterminal_slots[slot].lo + slot_offset, nonterminal_slots[slot].hi + slot_offset);
    gram_print_slot_by_number(this_grammar, slot + slot_offset, 0);

    if (level_2_slots[slot])
      printf("LEVEL 2 ");

    if (tail_slots[slot] != NULL)
    {
      unsigned *this_instance_slot = tail_slots[slot];
      unsigned *this_instance_successor_slot = tail_successor_slots[slot];

      for (; *this_instance_slot != end_of_run_symbol; this_instance_slot++, this_instance_successor_slot++)
        printf(" [%u->%u]", *this_instance_slot + slot_offset, *this_instance_successor_slot + slot_offset);
    }

    printf("\n");
  }

  for (unsigned slot = 0; slot < slot_count; slot++)
    printf("%u: %u '%u' %s\n", slot + slot_offset, terminal_slots[slot] + slot_offset, terminal_slot_lookahead[slot], acceptance_slots[slot]?"(accept)" : "");

  printf("****************");

  for (unsigned slot = 0; slot < slot_count; slot++)
    printf("%u -> %u\n", slot + slot_offset, successor_slots[slot] + slot_offset);
#endif

  /* 1. Tokenise the input */
  token_count = 0;

  lex_grammar = this_grammar;
  for (lex_ip = string; lex() != GRAM_EOS_SYMBOL_NUMBER; token_count++)
    ;
#if defined(EARLEY_TRACE)
  text_printf("Input string contains %u tokens\n", token_count);
#endif

  token_count++;

  tokens = (unsigned*) mem_malloc((token_count)* sizeof(unsigned));
  unsigned *input_token = tokens;

  lex_ip = string;
  while((*input_token++ = lex()) != GRAM_EOS_SYMBOL_NUMBER)
    ;

  /* 2. Create datastructure to hold Earley sets
        We use essentially a two dimensional array of linked lists, index by slot, then frontier, then index.
  */
  earley_sets = (earley_node***) mem_calloc(slot_count, sizeof(earley_node**));

  for (int this_slot = 0; this_slot < slot_count; this_slot++)
    earley_sets[this_slot] = (earley_node**) mem_calloc(token_count, sizeof (earley_node*));

  derivation* this_derivation = (derivation*) mem_calloc(1, sizeof(derivation));

  start_time = clock();

  /* 3. Initialise E_0 using rule 1*/
  for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_grammar->start_rule->rule_tree);
       this_production_edge != NULL;
       this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
  {
    unsigned slot = graph_atom_number(graph_destination(this_production_edge)) - slot_offset;

    if (!lookahead || set_includes_element(&slot_firsts[slot], tokens[level]))
    {
      earley_add("Rule 1 (initialisation)", slot, 0, 0, NULL);
      R[R_pointer].slot = slot;
      R[R_pointer].index = 0;
      R_pointer++;
    }
  }

  /* 4. Iterate over all frontiers with worklist */
  for (level = 0; level < token_count; level++)
  {
#if defined(EARLEY_TRACE)
    text_printf("Processing level %i\n", level);
#endif

    /* 4.1 Use rule 2 to initialise from E_i - 1 : update to use Q*/

    if (level != 0)  /* first level pre-initialised in step 3 */
    {
      for (unsigned slot = 0; slot < slot_count; slot++)
      {
        unsigned successor_slot;
        if (((successor_slot = terminal_slots[slot]) != 0) && tokens[level - 1] == terminal_slot_lookahead[slot])
        if (!lookahead || set_includes_element(&slot_firsts[successor_slot], tokens[level]))
          for (earley_node *temp = earley_sets[slot][level-1]; temp != NULL; temp = temp->next)
          {
            /* Note, under rule 2 an element cannot be addded twice, so don't check for inclusion */
            earley_add("Rule 2 (shift)", successor_slot, level, temp->index, NULL);

            R[R_pointer].slot = successor_slot;
            R[R_pointer].index = temp->index;
            R_pointer++;
          }
      }
    }

    while (R_pointer != 0)
    {
      R_pointer--;
      unsigned slot = R[R_pointer].slot;
      unsigned k = R[R_pointer].index;

      unsigned lo_slot;

      if ((lo_slot = nonterminal_slots[slot].lo) != nonterminal_slots[slot].hi) /* 4.2.1 Rule 3 */
      {
        unsigned hi_slot = nonterminal_slots[slot].hi;

        for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
          for (unsigned leader_slot = lo_slot; leader_slot<hi_slot; leader_slot++)
            if (!lookahead || set_includes_element(&slot_firsts[leader_slot], tokens[level]))
            if (earley_add("Rule 3 (nonterminal slot)", leader_slot, level, level, NULL))
            {
              R[R_pointer].slot = leader_slot;
              R[R_pointer].index = level;
              R_pointer++;
            }

        unsigned header_slot = header_slots[lo_slot] + header_offset;
        unsigned successor_slot = successor_slots[slot];

        if (H[header_slot] == level)
        {
          /* Test if beta begins with a nonterminal, or is empty */
          if (tail_slots[successor_slot] != NULL || (nonterminal_slots[successor_slot].lo != nonterminal_slots[successor_slot].hi))  /* Nonterminal */
          {
            if (!lookahead || set_includes_element(&slot_firsts[successor_slot], tokens[level]))

            if (earley_add("Rule 3 contingent", successor_slot, level, k, NULL))
            {
              R[R_pointer].slot = successor_slot;
              R[R_pointer].index = k;
              R_pointer++;
            }
          }
        }
      }
      else if (tail_slots[slot] != NULL) /* 4.2.2 Rule 4 */
      if (!lookahead || set_includes_element(&slot_firsts[slot], tokens[level]))
      {
        /* Look for occupied slots on level k that have this slot symbol after the dot */
        unsigned *this_instance_slot = tail_slots[slot];
        unsigned *this_instance_successor_slot = tail_successor_slots[slot];
        unsigned header_slot = header_slots[slot];

        /* Now we have a clean w with an epsilon family. Phew. */
        if (k == level)
          H[header_slot + header_offset] = level;

        for (; *this_instance_slot != end_of_run_symbol; this_instance_slot++, this_instance_successor_slot++)  /* scan over instance slots for this lhs */
          if (!lookahead || set_includes_element(&slot_firsts[*this_instance_successor_slot], tokens[level]))
          for (earley_node *temp = earley_sets[*this_instance_slot][k]; temp != NULL; temp = temp->next)  /* scan over (D::= mu B nu,h) on level k */
            if (earley_add("Rule 4 (tail slot)", *this_instance_successor_slot, level, temp->index, NULL))
            {
              R[R_pointer].slot = *this_instance_successor_slot;
              R[R_pointer].index = temp->index;
              R_pointer++;
            }
      }
    }
  }

  finish_time = clock();

  /* 5. Debug: print out Earley sets */
#if 1
  for (unsigned level = 0; level < token_count; level++)
  {
    unsigned cardinality = 0;

    for (unsigned slot = 0; slot < slot_count; slot++)
      for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
        cardinality++;

    text_printf("\n|E_%u| = %u { ", level, cardinality);

    if (cardinality < 16)
      for (unsigned slot = 0; slot < slot_count; slot++)
      {
        for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
        {
          text_printf("\n(");
          gram_print_slot_by_number(this_grammar, slot + slot_offset, 0);
          text_printf(", %u)", temp->index);
        }
      }
    else
      text_printf(" ...");

    text_printf(" }\n");
  }
#endif

  /* 6. Test for acceptance */
  for (unsigned slot = 0; slot < slot_count; slot++)
    if (acceptance_slots[slot] && earley_sets[slot][token_count -1 ] != NULL)
      this_derivation->accept = 1;

  text_printf("%s\n", this_derivation->accept ? "Accept" : "Reject");

  /* 7. Statistics output */

  double run_time = 0;

  text_printf("Earley cubic parser ran in %lf CPU s\n", run_time);

  unsigned cardinality = 0;
  for (unsigned level = 0; level < token_count; level++)
    for (unsigned slot = 0; slot < slot_count; slot++)
      for (earley_node *temp = earley_sets[slot][level]; temp != NULL; temp = temp->next)
        cardinality++;

  text_printf("Sum over Earley set cardinalities: %u\n", cardinality);

  /* Return results */
  return this_derivation;
}
