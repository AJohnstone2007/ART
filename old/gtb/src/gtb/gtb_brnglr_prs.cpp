/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_brnglr_prs.cpp - an RNGLR parser, as per the TOPLAS paper
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <string.h>
#include <time.h>
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
#include "gtb_brnglr_prs.h"
#include "stats.h"

char** blocks;
int blockSizeExponent; // Blocks are 2^blockSizeExponent so as to allow rapid separation of block and offset
int blockSize; // The block size computed by the constructor
int blockSizeMask; // The computed mask for this block size
int blockCount; // The maximumsize of this pool
int highWaterBlock = 0; // The currently extending block
int highWaterOffset = 1; // The first free location in the currently extending block: start at 1 because 0 means null

void poolInit(int bSE, int bC) {
  blockSizeExponent = bSE;
  blockCount = bC;
  blockSize = 1 << blockSizeExponent;
  blockSizeMask = blockSize - 1;
  blocks = (char**) mem_calloc(sizeof(char*), blockCount);
  blocks[0] = (char*) mem_calloc(sizeof(char), blockSize);
}

void*  pool_malloc(int count) { // C version returning pointer
  if (blockSize - highWaterOffset < count) {// allocate new block
    if (++highWaterBlock >= blockCount) { printf("Pool overflow"); exit(1);} // Note: we could resize the array here easily
    blocks[highWaterBlock] = (char*) mem_calloc(sizeof(char), blockSize);
    highWaterOffset = 0;
  }

  void* ret = blocks[highWaterBlock] + highWaterOffset;
  highWaterOffset += count;
  return ret;
}

void*  pool_calloc(int size, int count) { // C version returning pointer
  return pool_malloc(size*count);
}

void poolFree(void *p) {}

//#define BRNGLR_TRACE
#define COMPACT_EXTENDED_STATES
//#define edge_table

//#define BRNGLR_POOL 1
#if defined(BRNGLR_POOL)
#define algorithm "BRNLGR (GTB with Pool)"
#define mm_init() (poolInit(21, 2048))
#define mm_malloc(n) (pool_malloc(n))
#define mm_calloc(n,m) (pool_calloc(n,m))
#define mm_free(p) (poolFree(p))
#else
#define algorithm "BRNLGR (GTB with standard memory allocator)"
#define mm_init()
#define mm_malloc(n) mem_malloc(n)
#define mm_calloc(n,m) mem_calloc(n,m)
#define mm_free(p) mem_free(p)
#endif

/******************************************************************************/
typedef struct ssg_edge_table_element_struct
{
  int valid;
  set_ elements;
} ssg_edge_table_element;

typedef struct sppf_pack_edge_block_element
{
  int valid;
  void *pack_edge;
}sppf_pack_edge_block;

typedef struct sppf_pack_vector_element
{
  // Must have same layout as SPPF node to ensure that the union in sppf_node_table_element works properly!
  unsigned xdash; //Like x in an SPPF node: will always hold $ (GRAM_EOS_SYMBOL_NUMBER) in a pack vector
  sppf_node *parent;
  sppf_pack_edge_block pack_edges[2];  //At least two: but in practice we shall over-allocate to allow variable arrays of size n
} sppf_pack_vector;

typedef struct sppf_node_table_element_struct
{
  int valid;
  union
  {
    sppf_node *node;
    sppf_pack_vector *vector;
  };
} sppf_node_table_element;

struct reduction_stack_element
{
  ssg_node *v;                         /* v in paper */
  reduction *rt;                       /* indirectly, X and |alpha| in paper. f is X is this implementation! */
  unsigned m;                          /* The BRNGLR index */
  sppf_node *y;                        /* y in the paper */
};

struct shift_stack_element
{
  ssg_node *v;                         /* v in paper */
  int k;                               /* k =label[w] in paper */
};

typedef struct bnglr_context_struct
{
  unsigned *a;                         // Input string
  unsigned n;                          // Length of input string
  unsigned i;                          // Current input symbol index (and frontier number)
  unsigned state_count;                // State count from DFA
  unsigned extended_state_count;       // Total number of extended states
  unsigned max_reduction_length;       // Longest reduction in the grammar
  unsigned nonterminal_count;          // Number of nonterminals in the grammar
  unsigned first_dfa_state;            // First dfa state in grammar enumeration
  unsigned first_nonterminal;          // First nonterminal state in grammar enumeration
  reduction *q;                        // Current reduction
  unsigned q_reduction_number;         // Offset into the reduction table for q
  unsigned q_slot_number;              // Rules slot number for q
  unsigned X;                          // Left hand symbol of current reduction
  unsigned m;                          // Remaining length of reduction path to be traced
  unsigned mod_alpha;                  // Prefix length
  ssg_node *v;                         // First node down this reduction's path
  ssg_node *u;                         // Second (final) node in this reduction's path
  sppf_node *y;
  sppf_node *z;                        // New SPPF node being added to SPPF
  sppf_node *u_f;                      // The epsilon node for add children
  sppf_node *delta_left;               // The first element in delta = (delta_left, delta_right)
  sppf_node *delta_right;              // The first element in delta = (delta_left, delta_right) Will be null for singleton list
  int j;                               // Leftmost index for z and its first child
  int j_prime;                         // Leftmost index for z's second child (or i+1 if there isn't one)
  ssg_node **current_frontier;         // The current frontier base
  ssg_node **next_frontier;            // The next frontier base as built by shifter
  reduction_stack_element *reduction_stack, *reduction_stack_pointer;   //Set R
  shift_stack_element *shift_stack, *shift_stack_prime, *shift_stack_pointer, *shift_stack_prime_pointer;       //Sets Q, Q'

  ssg_edge_table_element *ssg_edge_table;                 //Out edges from current SSG frontier: present if number is current validation value
  unsigned ssg_edge_table_state_count; //Total number of states + extended states
  int sppf_node_table_size;            //Number of cells in the node table
  sppf_node_table_element *sppf_node_table;     //The node table for this parser
  sppf_node_table_element *node_table_element;  //Last node table lookup element found
  sppf_node *parent;                   //This holds the 'actual' SPPF node: sometimes it is just a copy of the node
                                       //field from node_table_element, otherwise it is a second indirection in the case of a pack vector

  int sppf_node_table_base;            //The base value for node table 'current'
  int sppf_node_table_base_increment;  //The value to increment base by at each level
  int sppf_node_table_pack_value;      //The special pack value used to indicate that reduction numbers must be found in pack nodes

  unsigned *N_alpha_equivalence_classes;        //Equivalence class numbers
  unsigned N_alpha_equivalence_class_count;     //Th enumber of equivalence classes

  /* Instrumentation variables */
  unsigned gss_state_node_count;
  unsigned gss_extended_node_count;
  unsigned gss_edge_count;

  unsigned sppf_epsilon_node_count;
  unsigned sppf_terminal_node_count;
  unsigned sppf_nonterminal_node_count;
  unsigned sppf_intermediate_node_count;
  unsigned sppf_pack_node_count;
  unsigned sppf_edge_count;
  clock_t start_time;
  clock_t finish_time;

  unsigned family_checks;
}
brnglr_context;

/******************************************************************************/
 unsigned **extended_state_nonterminal_vector;

#if defined(COMPACT_EXTENDED_STATES)
 unsigned
extended_state(brnglr_context *context)
{
  return context->state_count + extended_state_nonterminal_vector[context->X - context->first_nonterminal][context->m - 1 - 2];
}
#else
 int
extended_state(brnglr_context *context)
{
  return context->state_count + ((context->X - context->first_nonterminal) * context->max_reduction_length) + context->m;
}
#endif

/******************************************************************************/

 void
N_alpha_equivalence_classes_create (dfa * this_dfa, brnglr_context * context)
{
  unsigned first_slot = this_dfa->grammar->first_level_0_slot;
  unsigned slot_count = this_dfa->grammar->first_header - this_dfa->grammar->first_level_0_slot;
  context->N_alpha_equivalence_classes = (unsigned *) mm_calloc (slot_count, sizeof (unsigned));

  for (int i = 0; i < slot_count; i++)
    context->N_alpha_equivalence_classes[i] = UINT_MAX;

  context->N_alpha_equivalence_class_count = 0;

  graph_create_node_index (this_dfa->grammar->rules);

  for (int i = 0; i < slot_count; i++)
  {
    unsigned prefix_length;
	gram_symbols_data *lhs_symbol;

    if (context->N_alpha_equivalence_classes[i] == UINT_MAX &&  // Not yet processed
        (lhs_symbol = gram_slot_lhs_symbol (this_dfa->grammar, first_slot + i)) != GRAM_ILLEGAL_SYMBOL_NUMBER &&        // Not a station  (could test these two more efficiently by checking enumeration)
        (prefix_length = gram_slot_prefix_length (this_dfa->grammar, first_slot + i)) > 0 &&    // Not a leader
        gram_slot_suffix_length (this_dfa->grammar, first_slot + i) > 1)
    {
      context->N_alpha_equivalence_classes[i] = context->N_alpha_equivalence_class_count++;

      for (int j = i + 1; j < slot_count; j++)
        if (context->N_alpha_equivalence_classes[j] == UINT_MAX &&      // Not yet processed
            gram_slot_lhs_symbol (this_dfa->grammar, first_slot + j) == lhs_symbol &&   // Same LHS
            gram_slot_prefix_length (this_dfa->grammar, first_slot + j) == prefix_length &&     // Same prefix length
            gram_slot_suffix_length (this_dfa->grammar, first_slot + j) > 1)
        {
          gram_edge *i_edge =
            (gram_edge *) graph_next_in_edge (graph_node_index (this_dfa->grammar->rules)[first_slot + i]);
          gram_edge *j_edge =
            (gram_edge *) graph_next_in_edge (graph_node_index (this_dfa->grammar->rules)[first_slot + j]);

          for (int length = prefix_length; length > 0; length--)
          {
            if (i_edge->symbol_table_entry != j_edge->symbol_table_entry)
              goto no_match;

            i_edge = (gram_edge *) graph_next_in_edge (graph_source (i_edge));
            j_edge = (gram_edge *) graph_next_in_edge (graph_source (j_edge));
          }
          context->N_alpha_equivalence_classes[j] = context->N_alpha_equivalence_class_count - 1;

        no_match:
          ;
        }
    }
  }

#if defined(GTB_ROB_COMPATIBLE)
  printf("\a!!! equivalence classes disabled in this version of BRNGLR\n");

  context->N_alpha_equivalence_class_count = 0;

  for (int i = 0; i < slot_count; i++)
    if (context->N_alpha_equivalence_classes[i] > 0)
      context->N_alpha_equivalence_classes[i] = context->N_alpha_equivalence_class_count++;
#endif

#if defined(BRNGLR_TRACE)
  for (int i = 0; i < slot_count; i++)
  {
    text_printf ("%3i %3i ", i, context->N_alpha_equivalence_classes[i]);
    gram_print_slot_by_number (this_dfa->grammar, first_slot + i, 0);
    text_printf ("\n");
  }
  text_printf
    ("Total of %i nonterminals and %i N_alpha equivalence class%s\n",
     this_dfa->grammar->first_level_0_slot -
     this_dfa->grammar->first_nonterminal,
     context->N_alpha_equivalence_class_count, context->N_alpha_equivalence_class_count == 1 ? "" : "es");
#endif
}

/******************************************************************************/
 void ssg_edge_table_create(derivation * this_derivation, brnglr_context * context)
{
  context->ssg_edge_table_state_count = this_derivation->dfa->state_count + context->extended_state_count;

  int table_size = (context->n + 1) * context->ssg_edge_table_state_count;
  if (script_gtb_verbose())
     text_printf("Grammar has %i nonterminals and maximum reduction length %i\n",
              this_derivation->dfa->grammar->first_level_0_slot - this_derivation->dfa->grammar->first_nonterminal + 1, context->max_reduction_length);
  if (script_gtb_verbose())
    text_printf("BRNGLR automaton has %i states; %i extended states; total %i states\n", this_derivation->dfa->state_count, context->extended_state_count, context->ssg_edge_table_state_count);
  if (table_size < 0)
    text_message(TEXT_FATAL, "table size overflows memory address range\n");
  if (script_gtb_verbose())
    text_printf("Allocating %i bytes (%.3f Mbytes) for edge table\n", table_size * sizeof(int), ((double)(table_size*sizeof(int)) /(1024.0*1024.0)));
  context->ssg_edge_table = (ssg_edge_table_element*) mem_calloc(table_size, sizeof(ssg_edge_table_element));

}

 void ssg_edge_table_set(brnglr_context *context, ssg_node *v, ssg_node *w)
{
#if defined(edge_table)
  text_printf("ssg_edge_table_set: w->value = %i, w->level = %i, v->value = %i, v->level = %i, context->n = %i, context->i = %i\n",
                                   w->value,      w->level,      v->value,      v->level,      context->n,      context->i);
#endif
  ssg_edge_table_element* this_element = &context->ssg_edge_table[(w->value - context->first_dfa_state) * (context->n + 1) + v->level];

  if (this_element->valid != context->sppf_node_table_pack_value)
  {
    set_clear(&this_element->elements);
#if defined(edge_table)
    text_printf("ssg_edge_table_set: clearing set vector\n");
#endif
    this_element->valid = context->sppf_node_table_pack_value;
  }
  set_unite_element(&this_element->elements, v->value - context->first_dfa_state);
}

 int ssg_edge_table_get(brnglr_context *context, ssg_node *v, ssg_node *w)
{
#if defined(edge_table)
  text_printf("ssg_edge_table_get: w->value = %i, w->level = %i, v->value = %i, v->level = %i, context->n = %i, context->i = %i\n",
                                   w->value,      w->level,      v->value,      v->level,      context->n,      context->i);
#endif
  ssg_edge_table_element* this_element = &context->ssg_edge_table[(w->value - context->first_dfa_state) * (context->n + 1) + v->level];

  if (this_element->valid != context->sppf_node_table_pack_value)
  {
#if defined(edge_table)
    text_printf("ssg_edge_table_get: element not valid\n");
#endif
    return 0;
  }
  else
  {
    int ret_value = set_includes_element(&this_element->elements, v->value - context->first_dfa_state);

#if defined(edge_table)
    text_printf("ssg_edge_table_get: return %i\n", ret_value);
#endif
    return ret_value;
  }
}

/******************************************************************************/

/* The SPPF_node_table has three roles: (i) to provide fast access to the SPPF node for nonterminal P at level i;
                                        (ii) to remember the applied reduction for unpacked nodes;
                                        (iii) to mark an SPPF node as having packed children.

   Each element of the node table contains an integer i and a pointer to an SPPF node. We also have a base node table value which is
   initialised to zero, and incremented by 1+|reductions| at the start of each level.

   A node_table_element is valid if its i-value is >= sppf_node_table_base. The applied reduction is i_value - sppf_node_table_base.
   Already packed nodes are represented by an applied reduction value of 1+|reductions|.
*/

 void
sppf_node_table_create (derivation * this_derivation, brnglr_context * context)
{
  context->sppf_node_table_size = (context->n + 2) *
                             (context-> N_alpha_equivalence_class_count + this_derivation->dfa->grammar->first_level_0_slot -
                                                                          this_derivation->dfa->grammar->first_nonterminal);
  context->sppf_node_table =
    (sppf_node_table_element *) mem_calloc (context->sppf_node_table_size, sizeof (sppf_node_table_element));
  context->sppf_node_table_base = 0;
  context->sppf_node_table_base_increment = this_derivation->dfa->reduction_count + 1;  /* We need an illegal value to mark already-packed values, hence the + 1 */
  context->sppf_node_table_pack_value = context->sppf_node_table_base_increment - 1;

  for (int i = 0; i < context->sppf_node_table_size; i++)
    (context->sppf_node_table+i)->valid = INT_MIN;

#if defined(BRNGLR_TRACE)
  text_printf
    ("Allocated %i bytes to sppf_node_table based at %p: %i lexemes, %i nonterminals and %i equivalence classes; %i bytes per table record\n",
     context->sppf_node_table_size * sizeof (sppf_node_table_element),
     context->sppf_node_table,
     context->n + 1,
     this_derivation->dfa->grammar->first_level_0_slot - this_derivation->dfa->grammar->first_nonterminal,
     context->N_alpha_equivalence_class_count,
     sizeof (sppf_node_table_element));
  text_printf ("SPPF node table base increment value is %i\n", context->sppf_node_table_base_increment);

#endif
}

/* Load context->sppf_node_table_element with a valid node. No other changes made to sppf_node_table */
 void
sppf_node_table_find_N (derivation * this_derivation, brnglr_context * context)
{
#if defined(BRNGLR_TRACE)
  text_printf ("Accessing SPPF (for N-set) node table for X = %i and j = %i... ", context->X, context->j);
#endif

  context->node_table_element = &(context->sppf_node_table[context->j + ((context->X - this_derivation->dfa->nfa->grammar->first_nonterminal) * context->n)]);
  if (context->node_table_element->node != NULL)
  {
    if (context->node_table_element->node->x == GRAM_EOS_SYMBOL_NUMBER)    // Is this a pack vector?
    {
      context->parent = context->node_table_element->vector->parent;       // yes, then doubly indirect
  #if defined(BRNGLR_TRACE)
      text_printf ("node table element contains pack vector, parent is %p\n", context->parent);
  #endif
    }
    else
    {
      context->parent = context->node_table_element->node;                 // no, node pointer is is the element
  #if defined(BRNGLR_TRACE)
      text_printf ("node table element does not contain pack vector, parent is %p\n", context->parent);
  #endif
    }
  }

#if defined(BRNGLR_TRACE)
  text_printf
    ("found node table element %p with level * (|reductions| + 1) + reduction of %i, sppf node based at %p\n",
     context->node_table_element,
     context->node_table_element->valid, context->node_table_element->node);
#endif

  if (context->node_table_element->valid < context->sppf_node_table_base)    //need to create a new SPPF node
  {
    context->node_table_element->valid = context->sppf_node_table_base + context->q_reduction_number;

    if (context->node_table_element->node == NULL)                                  // Never used before
      context->parent = context->node_table_element->node = (sppf_node *) graph_insert_node (sizeof (sppf_node), this_derivation->sppf);
    else
    {
      if (context->node_table_element->node->x != GRAM_EOS_SYMBOL_NUMBER)   //Used before, but never packed
        context->parent = context->node_table_element->node = (sppf_node *) graph_insert_node (sizeof (sppf_node), this_derivation->sppf);
      else                                                               //Packed!
        context->parent = context->node_table_element->vector->parent = (sppf_node *) graph_insert_node (sizeof (sppf_node), this_derivation->sppf);
    }

    if (context->X >= this_derivation->dfa->nfa->grammar->first_nonterminal)
      context->sppf_nonterminal_node_count++;
    else
      context->sppf_terminal_node_count++;

    context->parent->x = context->X;
    context->parent->j = context->j;
#if defined(BRNGLR_TRACE)
  text_printf("created new SPPF node <%i> based at %p\n", graph_atom_number(context->parent), context->parent);
#endif
  }
#if defined(BRNGLR_TRACE)
  else
    text_printf("loaded context->sppf_node_table_element with %p\n", context->node_table_element);
#endif
}

 void
sppf_node_table_find_I (derivation * this_derivation, brnglr_context * context)
{
  unsigned q_slot_number = context->q_slot_number;
  gram_node *this_slot = (gram_node*) graph_node_index(this_derivation->dfa->nfa->grammar->rules)[q_slot_number];
  unsigned count_back_length = context->q->length - context->m + 2;

  for (int i = 0; i < count_back_length; i++)
    this_slot = (gram_node*) graph_source(graph_next_in_edge(this_slot));

  int this_slot_number = graph_atom_number(this_slot) - this_derivation->dfa->nfa->grammar->first_level_0_slot;

#if defined(BRNGLR_TRACE)
  text_printf ("Accessing SPPF (for I-set) node table for reduction slot %i and m=%i yielding slot %i (equivalence class %i) and j = %i... ",
               context->q_slot_number,
               context->m,
               this_slot_number,
               context->N_alpha_equivalence_classes[this_slot_number],
               context->j);
#endif

  int sppf_node_table_row_number = context->N_alpha_equivalence_classes[this_slot_number] + context->nonterminal_count;
  context->node_table_element = &(context->sppf_node_table[context->j + (sppf_node_table_row_number * context->n)]);

  if (context->node_table_element->node != NULL)
  {
    if (context->node_table_element->node->x == GRAM_EOS_SYMBOL_NUMBER)    // Is this a pack vector?
    {
      context->parent = context->node_table_element->vector->parent;       // yes, then doubly indirect
  #if defined(BRNGLR_TRACE)
      text_printf ("node table element contains pack vector, parent is %p\n", context->parent);
  #endif
    }
    else
    {
      context->parent = context->node_table_element->node;                 // no, node pointer is is the element
  #if defined(BRNGLR_TRACE)
      text_printf ("node table element does not contain pack vector, parent is %p\n", context->parent);
  #endif
    }
  }

#if defined(BRNGLR_TRACE)
  text_printf
    ("found node table element %p with level * (|reductions| + 1) + reduction of %i, sppf node based at %p\n",
     context->node_table_element,
     context->node_table_element->valid, context->node_table_element->node);
#endif

// Switch off I-cache for compatibility with Rob's implementation
#if defined(GTB_ROB_COMPATIBLE)
  if(true)
#else
  if (context->node_table_element->valid < context->sppf_node_table_base)    //need to create a new SPPF node
#endif
  {
    context->node_table_element->valid = context->sppf_node_table_base + sppf_node_table_row_number;

    if (context->node_table_element->node == NULL)                                  // Never used before
      context->parent = context->node_table_element->node = (sppf_node *) graph_insert_node (sizeof (sppf_node), this_derivation->sppf);
    else
    {
      if (context->node_table_element->node->x != GRAM_EOS_SYMBOL_NUMBER)   //Used before, but never packed
        context->parent = context->node_table_element->node = (sppf_node *) graph_insert_node (sizeof (sppf_node), this_derivation->sppf);
      else                                                               //Packed!
        context->parent = context->node_table_element->vector->parent = (sppf_node *) graph_insert_node (sizeof (sppf_node), this_derivation->sppf);
    }

    context->sppf_intermediate_node_count++;

    context->parent->x = context->N_alpha_equivalence_classes[this_slot_number] + this_derivation->dfa->nfa->grammar->first_level_0_slot;
    context->parent->j = context->j;
#if defined(BRNGLR_TRACE)
  text_printf("created new SPPF node <%i> based at %p\n", graph_atom_number(context->parent), context->parent);
#endif
  }
#if defined(BRNGLR_TRACE)
  else
    text_printf("loaded context->sppf_node_table_element with  %p\n", context->node_table_element);
#endif
}

/******************************************************************************/

/* In the paper, we have COMPLETE_REDUCTION(u, X, i) */
/* Here u, X and i are taken directly from the context block */
/* COMPLETE_REDUCTION is called twice, once with parameters (v,X,i) and once with (u,X,i) */
 void
complete_reduction (derivation * this_derivation, brnglr_context * context)
{
  ssg_node *w;

  unsigned k = context->u->value;
  /* get the shift (the 'goto') out of the table pl is a member of T(k,X) */
  int pl = dfa_retrieve_first_action (this_derivation->dfa, k, context->X);
#if defined(BRNGLR_TRACE)
  text_printf ("goto state %i: ", pl);
#endif

  context->j = context->u->level;

  if (context->v == context->u)
    context->j_prime = context->i;                         // Special case: path of only one element
  else
    context->j_prime = context->v->level;                  // j value for (possibly non-existent) right child

  /* If there is no node z in N labelled (X,j) {
     create an SPPF node z labelled (X,j) */
  sppf_node_table_find_N(this_derivation, context);
  context->z = context->parent;

  /* If there is a w in U_{i} with label l */
  if ((w = context->current_frontier[pl - context->first_dfa_state]) != NULL)
  {
#if defined(BRNGLR_TRACE)
    text_printf ("(old)\n");
#endif

    if (!ssg_edge_table_get(context, context->u, w))
    {
      /* create an edge (w, u) labelled z (and also with symbol X!) */
      ssg_edge *new_ssg_edge = (ssg_edge *) graph_insert_edge (sizeof (ssg_edge), context->u, w);       /* create an edge from w to u */
      ssg_edge_table_set(context, context->u, w);      /* validate this entry */
      context->gss_edge_count++;
      new_ssg_edge->symbol = context->X;
      new_ssg_edge->sppf_node = context->z;


      int *these_goto_parse_actions = dfa_retrieve_all_actions (this_derivation->dfa, pl, context->a[context->i + 1]);  /* Get T(l, a_i+1) */
      int *this_goto_parse_action = these_goto_parse_actions;

      if (GRAM_IS_DFA_STATE (this_derivation->dfa->nfa->grammar, *this_goto_parse_action))      /* If there is a shift ph in T(l, a+1) (it will be the first action) */
        this_goto_parse_action++;                          /* then skip the shift */

      /* Now we stack the non-zero length non-accepting reductions from w using u as the start of the path */
      for (; *this_goto_parse_action != 0; this_goto_parse_action++)    /* forall reductions in T(goto_state, a_i+1) */
      {
        reduction *rt =
          &this_derivation->dfa->
          reduction_table[*this_goto_parse_action - this_derivation->dfa->nfa->grammar->first_level_0_slot];

        /* Pick out and stack the non-zero length, non-accepting reductions */
        if (rt->length != 0 && !rt->is_accepting)          /* Skip accepting reductions */
        {
          context->reduction_stack_pointer->v = context->u;
          context->reduction_stack_pointer->rt = rt;
          context->reduction_stack_pointer->m = rt->length;
          context->reduction_stack_pointer->y = context->z;

#if defined(BRNGLR_TRACE)
          text_printf ("On new goto state (zero length creating reduction): Add to R: ");
          sr_print_action (this_derivation->dfa, w->value, context->a[context->i + 1], *this_goto_parse_action);
          text_printf ("\n");
#endif
          context->reduction_stack_pointer++;              /* Bump R pointer */
        }
      }
      mm_free (these_goto_parse_actions);
    }
  }
  else
  {
#if defined(BRNGLR_TRACE)
    text_printf ("(new at level %i)\n", context->i);
#endif
    /* Create a node w on current_frontier (in U_i) labelled l and an edge (w, u) labelled z */
    w = context->current_frontier[pl - context->first_dfa_state] =
      (ssg_node *) graph_insert_node (sizeof (ssg_node), this_derivation->ssg);
    context->gss_state_node_count++;
    w->value = pl;
    w->level = context->i;

    ssg_edge *new_ssg_edge = (ssg_edge *) graph_insert_edge (sizeof (ssg_edge), context->u, w);
    ssg_edge_table_set(context, context->u, w);      /* validate this entry */
    context->gss_edge_count++;
    new_ssg_edge->symbol = context->X;
    new_ssg_edge->sppf_node = context->z;

    int *these_goto_parse_actions = dfa_retrieve_all_actions (this_derivation->dfa, pl, context->a[context->i + 1]);    /* Get T(l, a_i+1) */
    int *this_goto_parse_action = these_goto_parse_actions;

    if (GRAM_IS_DFA_STATE (this_derivation->dfa->nfa->grammar, *this_goto_parse_action))        /* If there is a shift ph in T(l, a+1) */
    {
      context->shift_stack_pointer->v = w;
      context->shift_stack_pointer->k = *this_goto_parse_action;
#if defined(BRNGLR_TRACE)
      text_printf ("On new goto state: Add to Q: ");
      sr_print_action (this_derivation->dfa, w->value, context->a[context->i + 1], context->shift_stack_pointer->k);
      text_printf ("\n");
#endif

      context->shift_stack_pointer++;                      /* bump Q pointer */

      this_goto_parse_action++;                            /* advance action pointer past the shift action */
    }

    /* Now we stack the non-accepting reductions from w */
    for (; *this_goto_parse_action != 0; this_goto_parse_action++)      /* forall reductions in T(goto_state, a_i+1) */
    {
      reduction *rt =
        &this_derivation->dfa->reduction_table[*this_goto_parse_action -
                                               this_derivation->dfa->nfa->grammar->first_level_0_slot];

      /* Pick out the non-accepting reductions */
      if (!rt->is_accepting)                               /* skip accepting reductions */
        if (rt->length == 0)                               /* |t| = 0 */
        {
          context->reduction_stack_pointer->v = w;         /* w */
          context->reduction_stack_pointer->rt = rt;
          context->reduction_stack_pointer->m = 0;
          context->reduction_stack_pointer->y = NULL;      /* epsilon in paper */

#if defined(BRNGLR_TRACE)
          text_printf ("On new goto state (zero length creating reduction): Add to R: ");
          sr_print_action (this_derivation->dfa, w->value, context->a[context->i + 1], *this_goto_parse_action);
          text_printf ("\n");
#endif

          context->reduction_stack_pointer++;              /* Bump R pointer */
        }
        else                                               /* |t| != 0 */
        {
          context->reduction_stack_pointer->v = context->u;
          context->reduction_stack_pointer->rt = rt;
          context->reduction_stack_pointer->m = rt->length;
          context->reduction_stack_pointer->y = context->z;

#if defined(BRNGLR_TRACE)
          text_printf ("On new goto state (non-zero length creating reduction): Add to R: ");
          sr_print_action (this_derivation->dfa, w->value, context->a[context->i + 1], *this_goto_parse_action);
          text_printf ("\n");
#endif

          context->reduction_stack_pointer++;              /* Bump R pointer */
        }
    }
    mm_free (these_goto_parse_actions);
  }
}

/******************************************************************************/

 int
brnglr_family_element_is_context (brnglr_context * context, sppf_node * parent)
{
  void *second_edge = graph_next_out_edge (graph_next_out_edge (parent));

  if (second_edge == NULL)
    return context->j_prime == context->i;

  if(((sppf_node *) graph_destination (second_edge))->j == INT_MIN)
    return context->j_prime == context->i;

  return context->j_prime == ((sppf_node *) graph_destination (second_edge))->j;
}

 void
add_children_brnglr (derivation * this_derivation, brnglr_context * context)
{
#if defined(BRNGLR_TRACE)
  text_printf ("ADD_CHILDREN on SPPF atom <%i> labelled %i %i which has children ",
               graph_atom_number (context->z), context->z->x, context->z->j);

  for (void *temp = graph_next_out_edge(context->z); temp != NULL; temp = graph_next_out_edge(temp))
  {
    sppf_node *node = (sppf_node*) graph_destination(temp);
    text_printf("<%i> (%i %i)  ",graph_atom_number (node), node->x, node->j);
  }

  text_printf("\n");
#endif

  /* We are passed delta_left, delta_right and f in the context block, so
     we do not need to create a separate upsilon. We'll examine context->f as
     we go to decide if upsilon is (delta_left, delta_right) or
     (delta_left, delta_right, f)
   */

  void *first_out_edge;

  /* If z has no children */
  if ((first_out_edge = graph_next_out_edge (context->z)) == NULL)      /* y has no children */
  {
#if defined(BRNGLR_TRACE)
    text_printf
      ("Adding to childless SPPF node <%i> labelled (%i %i) reduction %i\n",
       graph_atom_number (context->z), context->z->x, context->z->j, context->q_reduction_number);

#endif

    /* First do the epsilon node: we're doing head insertion don't forget! */
    if (context->u_f != NULL)
    {
#if defined(BRNGLR_TRACE)
      text_printf ("linking to epsilon node\n");
#endif
      graph_insert_edge (0, context->u_f, context->z);
      context->sppf_edge_count++;
    }
    else
    {
#if defined(BRNGLR_TRACE)
      text_printf ("no epsilon node to link\n");
#endif
    }

    /* Now do the rest of upsilon */
    if (context->delta_right != NULL)
    {
      graph_insert_edge (0, context->delta_right, context->z);
      context->sppf_edge_count++;
    }

    graph_insert_edge (0, context->delta_left, context->z);
    context->sppf_edge_count++;

    /* and finally put the reduction node number into the SPPF table */
    context->node_table_element->valid = context->sppf_node_table_base + context->q_reduction_number;

#if defined(GTB_ROB_COMPATIBLE)
    /* and also the first reduction field! */
    context->z->first_reduction_number = context->q_reduction_number;
#endif
  }
  else
  {
    /* Assert: z has been looked up, and sppf_node_table_element is valid */

#if defined(GTB_ROB_COMPATIBLE)
    if (((sppf_node*) graph_destination(graph_next_out_edge(context->z)))->x != 0)
#else
    if (context->node_table_element->valid != context->sppf_node_table_pack_value)  /* not a pack node: ignore data structure on search */
#endif

    {
      /* Check for family membership of this non packed node */
#if defined(BRNGLR_TRACE)
      text_printf
        ("Checking unpacked SPPF node <%i> labelled (%i %i) and reduction %i for family membership\n",
         graph_atom_number (context->z), context->z->x, context->z->j, context->q_reduction_number);
#endif

#if defined(GTB_ROB_COMPATIBLE)
      if ((context->z->first_reduction_number == context->q_reduction_number) &&
          brnglr_family_element_is_context (context, context->z))
#else
      if ((context->node_table_element->valid % context->sppf_node_table_base_increment == context->q_reduction_number) &&
          brnglr_family_element_is_context (context, context->z))
#endif
        return;

#if defined(BRNGLR_TRACE)
      text_printf ("Reduction not found in unpacked family: adding packed node to convert unpacked family to packed\n");
#endif
      /* First create a pack vector if we need one */
      if (context->node_table_element->node == context->parent)  /* Do we need to create a pack vector?  */
      {
        //Weird allocation: the pack vector has an array of fixed size [1] built in: we're overallocating to give an array of size [n+1]
        //An analysis of the algotithm may show that we only need 0..n-1 = [n] elements, but we're playing safe here
        context->node_table_element->vector = (sppf_pack_vector*) mm_calloc(1, sizeof(sppf_pack_vector) + ((context->n) * sizeof(sppf_pack_edge_block)));

        // Now set the pack vector valid fields to INT_MIN
        for (unsigned i = 0; i <= context->n; i++)
          context->node_table_element->vector->pack_edges[i].valid = INT_MIN;

        //Now load the pack vector with the parent
        context->node_table_element->vector->xdash = GRAM_EOS_SYMBOL_NUMBER;
        context->node_table_element->vector->parent = context->parent;
      }

      /* insert a pack node */
      sppf_node *p = (sppf_node *) graph_insert_node (sizeof (sppf_node),
                                                      this_derivation->sppf);
      context->sppf_pack_node_count++;

      // Put the reduction number in the pack node
      p->j = context->node_table_element->valid % context->sppf_node_table_base_increment;

      //... and negate it because this is the first one
      p->j = -p->j;

      context->node_table_element->valid = context->sppf_node_table_pack_value;

      //Now calculate local for the unpacked j_prime by getting it from the second child of z, or using context->i if there isn't one
      sppf_node * second_child = NULL;
      void * second_edge = NULL;
      void * first_edge = graph_next_out_edge(context->z);

      if (first_edge != NULL)
        second_edge = graph_next_out_edge(first_edge);

      if (second_edge != NULL)
        second_child = (sppf_node*) graph_destination(second_edge);

      if (second_child != NULL && second_child->j == INT_MIN) /* epsilon node, means that there is no right child */
        second_child = NULL;

      unsigned local_j_prime = second_child == NULL ? context->i : second_child->j;
      do
      {
        graph_insert_edge_after_final (0, graph_destination (first_out_edge), p);
        context->sppf_edge_count++;
        graph_delete_edge (first_out_edge);
        context->sppf_edge_count--;
      }
      while ((first_out_edge = graph_next_out_edge (context->z)) != NULL);


      context->node_table_element->vector->pack_edges[local_j_prime].pack_edge = graph_insert_edge (0, p, context->z);
      context->node_table_element->vector->pack_edges[local_j_prime].valid = context->i;
      context->sppf_edge_count++;
    }
    else /* we've got a previously packed node so use the vector to avoid a factor n search time */
    {
      /* Check for family membership of all the pack nodes under this node node */
#if defined(BRNGLR_TRACE)
      text_printf
        ("Checking packed SPPF node <%i> labelled (%i %i) and reduction %i for family membership\n",
         graph_atom_number (context->z), context->z->x, context->z->j, context->q_reduction_number);
#endif

      if (context->node_table_element->vector->pack_edges[context->j_prime].valid == context->i)  /* is vector entry valid */
      {
//      void *this_pack_edge = first_out_edge;
        void *this_pack_edge = context->node_table_element->vector->pack_edges[context->j_prime].pack_edge;

        do
        {
          sppf_node *p = (sppf_node *) graph_destination (this_pack_edge);

          int j = p->j;

          if (j < 0)
            j = -j;

          if (j != context->q_reduction_number)
            goto bump;

          if (!brnglr_family_element_is_context (context, p))
            goto bump;

  #if defined(BRNGLR_TRACE)
          text_printf ("Reduction found in packed family: starting next reduction\n");
  #endif
          return;

          bump:
            this_pack_edge = graph_next_out_edge (this_pack_edge);
       }
//       while (this_pack_edge != NULL);
       while (this_pack_edge != NULL && ((sppf_node *) graph_destination(this_pack_edge))->j > 0);
     }

#if defined(BRNGLR_TRACE)
      text_printf ("Reduction not found in packed family: adding new packed node\n");
#endif
    }

/* By now, either the children were pack nodes, or we have made them so */

    /* Create the pack node for this reduction, mark with its reduction number and link to z */
    sppf_node *p_prime = (sppf_node *) graph_insert_node (sizeof (sppf_node),
                                                          this_derivation->sppf);
    context->sppf_pack_node_count++;
    p_prime->j = context->q_reduction_number;

    // Now the edge. If we have a validated packed_edge_vector entry for this jprime, then
    // we should just hook the edge on in sequence after the validated one. If not, then hook the
    // edge in at the front and validate the entry
    if (context->node_table_element->vector->pack_edges[context->j_prime].valid == context->i)
      graph_insert_edge_after_out_edge (0, p_prime, context->node_table_element->vector->pack_edges[context->j_prime].pack_edge);
    else  // we're new
    {
      p_prime->j = -p_prime->j;
      context->node_table_element->vector->pack_edges[context->j_prime].pack_edge = graph_insert_edge (0, p_prime, context->z);
      context->node_table_element->vector->pack_edges[context->j_prime].valid = context->i;
    }

    context->sppf_edge_count++;

/* Note that we're doing head insertion, so we go in reverse order */
    /* if epsilon-sppf is non-null, insert it */
    if (context->u_f != NULL)
    {
      graph_insert_edge (0, context->u_f, p_prime);
      context->sppf_edge_count++;
    }

    if (context->delta_right != NULL)
    {
      graph_insert_edge (0, context->delta_right, p_prime);
      context->sppf_edge_count++;
    }

    /* Now link left which was y_m in the RNGLR algorithm */
    graph_insert_edge (0, context->delta_left, p_prime);
    context->sppf_edge_count++;
  }
}

/******************************************************************************/
/* Main BRGNGLR parser function */

derivation *
sr_brnglr_parse (dfa * this_dfa, char *string, int reduction_stack_size)
{
  brnglr_context *context;
  derivation *this_derivation;         /* derivation structure to hold output of parse */

  /*step zero - added in 2024 to support new experimental framework with optional pool based memory management */
  mm_init();
  resetStats();
  loadAlgorithm(algorithm);

  /* Initialisation step 1: sign on */

  if (script_gtb_verbose()) {
    text_printf ("\n");
    text_message (TEXT_INFO, "BRNGLR parse: \'%s\'\n", string);
  }

  /* Initialisation step 2: create derivation structure and SSG structure */

  context = (brnglr_context *) mm_calloc (1, sizeof (brnglr_context));
  this_derivation = (derivation *) mm_calloc (1, sizeof (derivation));
  this_derivation->dfa = this_dfa;
  this_derivation->ssg = graph_insert_graph ("SSG");
  this_derivation->sppf = graph_insert_graph ("SPPF");
  sppf_create_epsilon_sppf (this_derivation);
  context->sppf_epsilon_node_count = sppf_epsilon_node_count;
  context->sppf_edge_count += sppf_epsilon_edge_count;
  if (script_gtb_verbose()) {
    FILE *epsilon_sppf_vcg_file = fopen ("esppf.vcg", "w");
    text_redirect (epsilon_sppf_vcg_file);
    sppf_set_render_derivation (this_derivation);
    graph_vcg (this_derivation->sppf, sppf_vcg_epsilon_sppf_print_index, sppf_vcg_print_node, sppf_vcg_print_edge);
    fclose (epsilon_sppf_vcg_file);
    text_redirect (stdout);
  }

  /* Initialisation step 3a: create the extended state looup table*/

  context->state_count = this_dfa->state_count;
  context->first_nonterminal = this_dfa->grammar->first_nonterminal;
  context->nonterminal_count = this_dfa->grammar->first_level_0_slot - context->first_nonterminal;

  extended_state_nonterminal_vector = (unsigned**) mm_calloc(this_derivation->dfa->nfa->grammar->first_level_0_slot - context->first_nonterminal, sizeof(unsigned*));

  context->extended_state_count = 0;

  for (unsigned nonterminal = context->first_nonterminal;
       nonterminal < this_derivation->dfa->nfa->grammar->first_level_0_slot;
       nonterminal++)
  {
    unsigned longest_reduction = 0;

    for (int i = 0; i < this_dfa->reduction_count; i++)
      if (this_dfa->reduction_table[i].symbol_number == nonterminal)
        if (this_dfa->reduction_table[i].length > longest_reduction)
          longest_reduction = this_dfa->reduction_table[i].length;

//        text_printf("Nonterminal %i: longest production has length %i\n", nonterminal, longest_reduction);

    if (longest_reduction > 2)
    {
      extended_state_nonterminal_vector[nonterminal - context->first_nonterminal] = (unsigned*) mm_calloc(longest_reduction - 2 + 1, sizeof(unsigned));

      for (int step_number = 0; step_number < longest_reduction - 2; step_number++) {
         extended_state_nonterminal_vector[nonterminal - context->first_nonterminal][step_number] = context->extended_state_count++;
      }
    }
  }

#if defined(BRNGLR_TRACE)
  //  text_printf ("Total of %i extended states\n", current_extended_state_number);
#endif

  context->max_reduction_length = 0;

  for (int i = 0; i < this_dfa->reduction_count; i++)
    if (this_dfa->reduction_table[i].length > context->max_reduction_length)
      context->max_reduction_length = this_dfa->reduction_table[i].length;

#if defined(BRNGLR_TRACE)
  text_printf ("Maximum reduction length is %i\n", context->max_reduction_length);
#endif

#if !defined(COMPACT_EXTENDED_STATES)
  context->extended_state_count = extended_state_count(this_derivation, context);
#endif

  context->nonterminal_count = this_dfa->grammar->first_level_0_slot - this_dfa->grammar->first_nonterminal;

  /* Initialisation step 3b: create the frontiers. In BRNGLR we must leave enough space for the pseudo states too! */
  context->current_frontier = (ssg_node **) mm_calloc (sizeof (ssg_node *), this_dfa->state_count + context->extended_state_count);
  context->next_frontier =    (ssg_node **) mm_calloc (sizeof (ssg_node *), this_dfa->state_count + context->extended_state_count);
  context->first_dfa_state = this_dfa->nfa->grammar->first_dfa_state;

  /* Initialisation step 4: create and initialise reduction (R) and shift (Q) sets as stacks */
  context->reduction_stack =
    (reduction_stack_element *) mm_calloc (reduction_stack_size ==
                                            0 ? 4096 : reduction_stack_size, sizeof (reduction_stack_element));
  context->shift_stack = (shift_stack_element *) mm_calloc (this_dfa->state_count, sizeof (shift_stack_element));
  context->shift_stack_prime = (shift_stack_element *) mm_calloc (this_dfa->state_count, sizeof (shift_stack_element));

  loadSetupTime();

  /* Initialisation step 5: initialise lexer and load input symbol array */
  context->n = 0;                           /* Our n is actually the n+1 from the paper since we include terminating $ */
  lex_init (string, this_dfa->nfa->grammar);

  int lexeme;
  do
  {
    lexeme = lex_lex ();
    context->n++;
  }
  while (lexeme != GRAM_EOS_SYMBOL_NUMBER && lexeme != GRAM_ILLEGAL_SYMBOL_NUMBER);

  context->n--;                                                     /* Paper does not include terminating $: that i sat position n+1 */

  if (script_gtb_verbose())
    text_message (TEXT_INFO,
                "BRNGLR parse: input length %i character%s, %i lexeme%s\n",
                strlen (string), strlen (string) == 1 ? "" : "s", context->n, context->n == 1 ? "" : "s");

  context->a = (unsigned *) mm_malloc ((context->n + 2) * sizeof (unsigned));   /* we don't use a[0], and we need an a[d+1] for $, hence length is d+2 */

  lex_init (string, this_dfa->nfa->grammar);

  for (int i = 1; i <= context->n + 1; i++)
    context->a[i] = lex_lex ();


  /* Initialisation step 6: create {N,alpha} equivalence classes */
  N_alpha_equivalence_classes_create (this_dfa, context);

  /* Initialisation step 7: create sppf_node_table and ssg_edge table*/
  ssg_edge_table_create(this_derivation, context);
  sppf_node_table_create (this_derivation, context);       //picks up N_alpha_equivalence_class_count from a global

  /* Initialisation step 8: start the clock */
  context->start_time = clock();
  loadInputStringLength(strlen(string));
  loadInputTokenLength(context->n);
  loadLexTime();

  /* End of initialisation */
/******************************************************************************/

  /* ** Main algorithm, as per pages 30-22 of the AI paper ** */
  /* Test against 1 here, not zero, because our d is one plus the d in the paper */
  if ((context->i = context->n) == 0)                                              /* On empty string, see if acc is a member of T(0,$) by checking all reductions and setting this_derivation->accept */
  {
#if defined(BRNGLR_TRACE)
    text_printf ("Empty string\n");
#endif
    int *these_parse_actions = dfa_retrieve_all_actions (this_dfa, this_dfa->nfa->grammar->first_dfa_state, GRAM_EOS_SYMBOL_NUMBER);    /* Get T(0, $) */
    int *this_parse_action = these_parse_actions;

    if (GRAM_IS_DFA_STATE (this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then skip it */
      this_parse_action++;

    for (; *this_parse_action != 0; this_parse_action++)   /* Walk the rest of the actions in T(0, $) looking for accepts */
      if (this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)
        this_derivation->accept = 1;

    if (this_derivation->accept)
    {
      graph_set_root (this_derivation->sppf,
                      this_derivation->epsilon_sppf_index[this_derivation->dfa->grammar->start_rule->symbol_number]);
      if (script_gtb_verbose())
        sppf_statistics (this_derivation, "Before SPPF pruning");
      graph_retain_set_of_nodes (this_derivation->sppf, graph_reachable_nodes (this_derivation->sppf, graph_root (this_derivation->sppf)));
      if (script_gtb_verbose())
        sppf_statistics (this_derivation, "After SPPF pruning");
    }
  }
  else                                                     /* Non-empty string */
  {
    ssg_node *v0 = (ssg_node *) graph_insert_node (sizeof (ssg_node), this_derivation->ssg);    /* Create a node v_0... */
    context->gss_state_node_count++;
    v0->value = this_dfa->nfa->grammar->first_dfa_state;   /* labelled with the start state of the DFA - not necessarilly 0 in our implementation */
    v0->level = 0;
    context->current_frontier[0] = v0;                     /* U_0 = { v_0 } */
    context->reduction_stack_pointer = context->reduction_stack;        /* R = { } */
    context->shift_stack_pointer = context->shift_stack;   /* S = { } */
    context->a[context->n + 1] = GRAM_EOS_SYMBOL_NUMBER;            /* a_n+1 = $ (actually this has already been done by the lexer... */
    /* NB our current frontier and next frontier arrays represent U_k and U_k+1 at any point: we can't initislise the U_i's to empty because they don't exist */

    int *these_parse_actions = dfa_retrieve_all_actions (this_dfa, this_dfa->nfa->grammar->first_dfa_state, context->a[1]);     /* Get T(0, a_1) */
    int *this_parse_action = these_parse_actions;       /* If there is a shift, it will be first */

    if (GRAM_IS_DFA_STATE (this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then push (label(v0), k) into Q set */
    {
      context->shift_stack_pointer->v = v0;
      context->shift_stack_pointer->k = *this_parse_action;
#if defined(BRNGLR_TRACE)
      text_printf ("Add to Q: ");
      sr_print_action (this_dfa, context->shift_stack_pointer->v->value, context->a[1], *this_parse_action);
#endif
      context->shift_stack_pointer++;                      /* bump Q pointer */

      this_parse_action++;                                 /* advance action pointer past the shift action */
    }

    for (; *this_parse_action != 0; this_parse_action++)   /* forall reductions in T(0, a_1) */
    {
      if (!this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)
      {
        context->reduction_stack_pointer->v = v0;
        context->reduction_stack_pointer->rt =
          &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
        context->reduction_stack_pointer->y = NULL;        /* y is epsilon in the paper means 'no node' */
        context->reduction_stack_pointer->m = context->reduction_stack_pointer->rt->length;     /* This is the 'parent' reduction */

#if defined(BRNGLR_TRACE)
        text_printf ("Load v_0 actions: Add to R: ");
        sr_print_action (this_dfa, context->reduction_stack_pointer->v->value, context->a[1], *this_parse_action);
#endif

        context->reduction_stack_pointer++;                /* Bump R pointer */
      }
    }

    mm_free (these_parse_actions);                        /* Reclaim memory for T(0, a_1) */

/******************************************************************************/

    /* Main loop: for i = 0 to d while U_i != { } */
    int thereis_a_shifted_node = true; /* true if we created a node during SHIFTER: use to test U_i != { } */
    for (context->i = 0; context->i <= context->n && thereis_a_shifted_node; context->i++)
    {
#if defined(BRNGLR_TRACE)
      text_printf ("\nStarting frontier %i: a[i+1] is %i\n", context->i, context->a[context->i + 1]);
#endif
      /* Clear next_frontier */
      /* Switch frontiers and clear next_frontier: we do this at the beginning so that at the end of the runm current_frontier contains U_d */
      ssg_node **frontier_exchanger = context->current_frontier;
      context->current_frontier = context->next_frontier;
      context->next_frontier = frontier_exchanger;
      memset (context->next_frontier, 0, sizeof (ssg_node *) * (this_dfa->state_count + context->extended_state_count));     // SOMETHING WRONG HERE!!!

      context->sppf_node_table_base += context->sppf_node_table_base_increment; /* Effectively, clear \cal N and \cal I */
      context->sppf_node_table_pack_value += context->sppf_node_table_base_increment;   /* Set the pack 'constant' to the next value */

/******************************************************************************/

      /* REDUCER */

      while (context->reduction_stack_pointer != context->reduction_stack)
      {
        context->reduction_stack_pointer--;

        context->v = context->reduction_stack_pointer->v;
        context->q = context->reduction_stack_pointer->rt;

        /* Pointer into reduction table */ ;
        context->q_reduction_number = context->reduction_stack_pointer->rt - this_dfa->reduction_table; /* in */ ;

        context->q_slot_number = this_dfa->reductions_index[context->q_reduction_number];
        context->m = context->reduction_stack_pointer->m;
        context->X = context->q->symbol_number;     // This is where the pool implemenrarion breaks
        context->mod_alpha = context->q->length;
        context->y = context->reduction_stack_pointer->y;


#if defined(BRNGLR_TRACE)
        text_printf
          ("Reducer: State (%i,L%i), input symbol %i, action R[%i]\n",
           context->v->value, context->v->level, context->a[context->i + 1], context->q - this_dfa->reduction_table);
#endif

        if (context->m == context->mod_alpha)
          context->u_f = this_derivation->epsilon_sppf_index[context->q_slot_number];
        else
          context->u_f = NULL;

        switch (context->m)
        {
          case 0:
          {
#if defined(BRNGLR_TRACE)
            text_printf ("Case m = 0 at level %i\n", context->i);
#endif
            unsigned k = context->v->value;
            /* get the shift (the 'goto') out of the table pl is a member of T(k,X) */
            int pl = dfa_retrieve_first_action (this_dfa, k, context->X);
#if defined(BRNGLR_TRACE)
            text_printf ("goto state %i ", pl);
#endif

            ssg_node *w;

            if ((w = context->current_frontier[pl - context->first_dfa_state]) != NULL)       /* If there is a w in U_{i} with label l */
            {
#if defined(BRNGLR_TRACE)
              text_printf ("(old)\n");
#endif
              if (!ssg_edge_table_get(context, context->v, w))
              {
                ssg_edge *new_ssg_edge = (ssg_edge *) graph_insert_edge (sizeof (ssg_edge), context->v, w);
                ssg_edge_table_set(context, context->v, w);      /* validate this entry */
                context->gss_edge_count++;
                new_ssg_edge->symbol = context->X;
                new_ssg_edge->sppf_node = this_derivation->epsilon_sppf_index[context->X];
              }
            }
            else
            {
#if defined(BRNGLR_TRACE)
              text_printf ("(new)\n");
#endif
              /* Create a node w on current_frontier (in U_i) labelled l and an edge (w, v) */
              w = context->current_frontier[pl - context->first_dfa_state] =
                (ssg_node *) graph_insert_node (sizeof (ssg_node), this_derivation->ssg);

              context->gss_state_node_count++;
              w->value = pl;
              w->level = context->i;

              ssg_edge *new_ssg_edge = (ssg_edge *) graph_insert_edge (sizeof (ssg_edge), context->v, w);
              ssg_edge_table_set(context, context->v, w);      /* validate this entry */
              context->gss_edge_count++;
              new_ssg_edge->symbol = context->q->symbol_number;
              new_ssg_edge->sppf_node = this_derivation->epsilon_sppf_index[context->X];

              int *these_goto_parse_actions = dfa_retrieve_all_actions (this_dfa, pl, context->a[context->i + 1]);      /* Get T(l,a_i  */
              int *this_goto_parse_action = these_goto_parse_actions;

              if (GRAM_IS_DFA_STATE (this_dfa->nfa->grammar, *this_goto_parse_action))  /* If there is a shift ph in T(l, a+1) */
              {
                context->shift_stack_pointer->v = context->current_frontier[pl - context->first_dfa_state];
                context->shift_stack_pointer->k = *this_goto_parse_action;
#if defined(BRNGLR_TRACE)
                text_printf ("On new goto state: Add to Q: ");
                sr_print_action (this_dfa,
                                 context->shift_stack_pointer->v->value,
                                 context->a[context->i + 1], context->shift_stack_pointer->k);
                text_printf ("\n");
#endif

                context->shift_stack_pointer++;            /* bump Q pointer */

                this_goto_parse_action++;                  /* advance action pointer past the shift action */
              }

              /* Now we stack the zero length non-accepting reductions from w using w as the start of the path */
              for (; *this_goto_parse_action != 0; this_goto_parse_action++)    /* forall reductions in T(goto_state, a_i+1) */
              {
                /* Pick out and stact the zero length, non-accepting reductions */
                if (this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length == 0 && !this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)       /* Skip accepting reductions */
                {
                  reduction *rt = &this_dfa->reduction_table[*this_goto_parse_action - this_dfa->nfa->grammar->first_level_0_slot];

                  context->reduction_stack_pointer->v = w;       /* w */
                  context->reduction_stack_pointer->rt = rt;
                  context->reduction_stack_pointer->m = 0;
                  context->reduction_stack_pointer->y = NULL;   /* epsilon in the paper */

#if defined(BRNGLR_TRACE)
                  text_printf ("On new goto state (zero length creating reduction): Add to R: ");

                  sr_print_action (this_dfa, w->value, context->a[context->i + 1], *this_parse_action);
                  text_printf ("\n");
#endif

                  context->reduction_stack_pointer++;      /* Bump R pointer */
                }
              }
            }
            break;
          }

          case 1:
          {
#if defined(BRNGLR_TRACE)
            text_printf ("Case m = 1 at level %i\n", context->i);
#endif
            /* COMPLETE_REDUCTION(v, X, i): X and i already set up in context block */
            context->u = context->v;
            complete_reduction (this_derivation, context);
            context->z = context->parent;

            /* ADD_CHILDREN(z, (y), f): z and f already set up in context block */
            context->delta_left = context->y;
            context->delta_right = NULL;
            add_children_brnglr (this_derivation, context);
            break;
          }


          case 2:
          {
#if defined(BRNGLR_TRACE)
            text_printf ("Case m = 2 at level %i\n", context->i);
#endif

            for (ssg_edge * this_out_edge = (ssg_edge *) graph_next_out_edge (context->v);
                 this_out_edge != NULL;
                 this_out_edge = (ssg_edge *) graph_next_out_edge (this_out_edge))
            {
#if defined(GTB_EDGE_VISITS)
              this_out_edge->visits++;
#endif
              /* COMPLETE_REDUCTION(u, X, i): u is the current child, X and i already set up in context block */
              context->u = (ssg_node*) graph_destination(this_out_edge);
              complete_reduction (this_derivation, context);

              /* ADD_CHILDREN(z, (x, y), f): a and f already set up in context block */
              context->delta_left = this_out_edge->sppf_node;
              context->delta_right = context->y;
              add_children_brnglr (this_derivation, context);
            }
            break;
          }

          default:                                        /* m > 2, so do intermediates */
#if defined(BRNGLR_TRACE)
            text_printf ("Case m >= 3 (m is %i) at level %i\n", context->m, context->i);
#endif
            ssg_node *w;

            if ((w = context->current_frontier[extended_state(context)]) == NULL)       /* If there is no w in U_{i} with label X,m */
            {
#if defined(BRNGLR_TRACE)
              text_printf ("(new)\n");
#endif
              /* Create a node w on current_frontier (in U_i) labelled l and an edge (w, u) */
              w = context->current_frontier[extended_state(context)] =
                (ssg_node *) graph_insert_node (sizeof (ssg_node), this_derivation->ssg);
              context->gss_extended_node_count++;
              w->value = extended_state(context) + this_derivation->dfa->nfa->grammar->first_dfa_state;     /* What's this: should be labelled X_m */
              w->level = context->i;
            }

            for (ssg_edge * this_out_edge =
                 (ssg_edge *) graph_next_out_edge (context->v);
                 this_out_edge != NULL; this_out_edge = (ssg_edge *) graph_next_out_edge (this_out_edge))
            {
#if defined(GTB_EDGE_VISITS)
              this_out_edge->visits++;
#endif
              context->u = (ssg_node*) graph_destination(this_out_edge);
              context->j = context->u->level;
              if (context->v == context->u)                            //May not be needed because, perhaps, u can never equal v
                context->j_prime = context->i;                         // Special case: path of only one element
              else
                context->j_prime = context->v->level;                  // j value for (possibly non-existent) right child

              sppf_node *x = this_out_edge->sppf_node;

              if (!ssg_edge_table_get(context, context->u, w))
              {
                /* Create edge (w, u) labelled z */
                ssg_edge *new_ssg_edge = (ssg_edge *) graph_insert_edge (sizeof (ssg_edge), context->u, w);
                ssg_edge_table_set(context, context->u, w);      /* validate this entry */
                context->gss_edge_count++;
                new_ssg_edge->symbol = 0;

                /* If there is no node z in I labelled (X, alpha_prime, j) then make one */
                sppf_node_table_find_I(this_derivation, context);
                context->z = context->parent;

                new_ssg_edge->sppf_node = context->z;

                /* Add (u, q, m - 1, z) to R */
                context->reduction_stack_pointer->v = context->u;
                context->reduction_stack_pointer->rt = context->q;
                context->reduction_stack_pointer->m = context->m - 1;
                context->reduction_stack_pointer->y = context->z;

#if defined(BRNGLR_TRACE)
                text_printf ("On new intermediate node: Add to R: ");
                text_printf ("\n");
#endif

                context->reduction_stack_pointer++;      /* Bump R pointer */
              }
              else
              {
#if defined(GTB_ROB_COMPATIBLE)
                context->z = this_edge->sppf_node;
#else
                /* If there is no node z in I labelled (X, alpha_prime, j) then make one */
                sppf_node_table_find_I(this_derivation, context);
                context->z = context->parent;
#endif
              }

              /* ADD_CHILDREN(z, (x, y), f): f already set up in context block */
              context->delta_left = x;
              context->delta_right = context->y;
              add_children_brnglr (this_derivation, context);
            }

            break;
        }
      }

/******************************************************************************/

      /* SHIFTER */

      thereis_a_shifted_node = false;
      if (context->i != context->n)
      {
        context->shift_stack_prime_pointer = context->shift_stack_prime;        /* Q' = { ] */

        context->z = (sppf_node *) graph_insert_node (sizeof (sppf_node),
                                                        this_derivation->sppf);
        context->sppf_terminal_node_count++;
        context->z->x = context->a[context->i + 1];
        context->z->j = context->i;

        while (context->shift_stack_pointer != context->shift_stack)    /* While Q != { } */
        {
          /* The shifter handles two cases: that in which the node to shift to already exists, and that in which it doesn't
             In both cases we need to access the action in T(k, a_{i+2}) so we factor the table access out and perform it here for both cases */
          context->shift_stack_pointer--;
#if defined(BRNGLR_TRACE)
          text_printf
            ("Shifter processing current frontier state %i, next frontier state %i, on symbol %i\n",
             context->shift_stack_pointer->v->value, context->shift_stack_pointer->k, context->a[context->i + 1]);
#endif

          these_parse_actions = dfa_retrieve_all_actions (this_dfa, context->shift_stack_pointer->k, context->a[context->i + 2]);       /* Get T(k, a_i+2) */
          this_parse_action = these_parse_actions;

          if (context->next_frontier[context->shift_stack_pointer->k - context->first_dfa_state] != NULL)       /* If there is a w in U_{i+1} with label k */
          {
            ssg_edge *new_ssg_edge = (ssg_edge *) graph_insert_edge (sizeof (ssg_edge),
                                                                     context->shift_stack_pointer->v,
                                                                     context->
                                                                     next_frontier
                                                                     [context->
                                                                      shift_stack_pointer->
                                                                      k - context->first_dfa_state]);
            context->gss_edge_count++;
            new_ssg_edge->symbol = context->a[context->i + 1];
            new_ssg_edge->sppf_node = context->z;

            if (GRAM_IS_DFA_STATE (this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then skip it */
              this_parse_action++;

            for (; *this_parse_action != 0; this_parse_action++)        /* forall reductions in T(k, a_i+2) */
            {
              if (this_dfa->
                  reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length != 0)
              {
#if defined(BRNGLR_TRACE)
                text_printf ("Shifter old node: Add to R: ");
                sr_print_action (this_dfa,
                                 context->shift_stack_pointer->k, context->a[context->i + 2], *this_parse_action);
#endif
                context->reduction_stack_pointer->v = context->shift_stack_pointer->v;  /* We store the target of the first edge on the search path */
                context->reduction_stack_pointer->rt =
                  &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                context->reduction_stack_pointer->y = context->z;
                context->reduction_stack_pointer->m = context->reduction_stack_pointer->rt->length;     /* This is the 'parent' reduction */

                context->reduction_stack_pointer++;        /* Bump R pointer */
              }
            }
          }
          else
          {
            context->next_frontier[context->shift_stack_pointer->k -
                                   context->first_dfa_state] =
              (ssg_node *) graph_insert_node (sizeof (ssg_node), this_derivation->ssg);
            context->gss_state_node_count++;
            context->next_frontier[context->shift_stack_pointer->k -
                                   context->first_dfa_state]->value = context->shift_stack_pointer->k;
            context->next_frontier[context->shift_stack_pointer->k - context->first_dfa_state]->level = context->i + 1;
            thereis_a_shifted_node = true;

            ssg_edge *new_ssg_edge = (ssg_edge *) graph_insert_edge (sizeof (ssg_edge),
                                                                     context->shift_stack_pointer->v,
                                                                     context->
                                                                     next_frontier
                                                                     [context->
                                                                      shift_stack_pointer->
                                                                      k - context->first_dfa_state]);
            context->gss_edge_count++;
            new_ssg_edge->symbol = context->a[context->i + 1];
            new_ssg_edge->sppf_node = context->z;

            if (GRAM_IS_DFA_STATE (this_dfa->nfa->grammar, *this_parse_action)) /* If this first action is a shift, then add it into shift_stack_prime */
            {
              context->shift_stack_prime_pointer->v =
                context->next_frontier[context->shift_stack_pointer->k - context->first_dfa_state];
              context->shift_stack_prime_pointer->k = *this_parse_action;
#if defined(BRNGLR_TRACE)
              text_printf ("Add to Q': ");
              sr_print_action (this_dfa,
                               context->
                               shift_stack_prime_pointer->v->value, context->a[context->i + 2], *this_parse_action);
#endif
              context->shift_stack_prime_pointer++;

              this_parse_action++;                         /* and skip the shift action */
            }

            for (; *this_parse_action != 0; this_parse_action++)        /* forall reductions in T(k, a_i+2) */
            {
              if (this_dfa->
                  reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].length != 0)
              {
                context->reduction_stack_pointer->v = context->shift_stack_pointer->v;  /* We store the target of the first edge on the search path */
                context->reduction_stack_pointer->rt =
                  &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                context->reduction_stack_pointer->m = context->reduction_stack_pointer->rt->length;     /* This is the 'parent' reduction */
                context->reduction_stack_pointer->y = context->z;

#if defined(BRNGLR_TRACE)
                text_printf ("Shifter new node: Add to R: ");
                sr_print_action (this_dfa,
                                 context->
                                 reduction_stack_pointer->v->value, context->a[context->i + 2], *this_parse_action);
#endif
              }
              else
              {
                // !!! This load seems to be putting in zeroes, leading to subsequent reducer crash in pool implementation
                context->reduction_stack_pointer->v = context->next_frontier[context->shift_stack_pointer->k - context->first_dfa_state];       /* We store the start node on the search path for zero length reductions */
                context->reduction_stack_pointer->rt =
                  &this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot];
                context->reduction_stack_pointer->m = context->reduction_stack_pointer->rt->length;     /* This is the 'parent' reduction */
                context->reduction_stack_pointer->y = NULL;

#if defined(BRNGLR_TRACE)
                text_printf ("Shifter new node: Add to R: ");
                sr_print_action (this_dfa,
                                 context->
                                 reduction_stack_pointer->v->value, context->a[context->i + 2], *this_parse_action);
#endif
              }

              context->reduction_stack_pointer++;          /* Bump R pointer */
            }
          }
          mm_free (these_parse_actions);                  /* Reclaim memory for T(k, a_i+2) */
        }

        /* copy Q' to Q by exchanging pointers */
        shift_stack_element *shift_stack_exchanger = context->shift_stack;      /* rotate stack bases through exchange */
        context->shift_stack = context->shift_stack_prime;
        context->shift_stack_prime = shift_stack_exchanger;

        context->shift_stack_pointer = context->shift_stack_prime_pointer;      /* set Q-stack-pointer to be Q'-stack-pointer... */
      }
    }
  }

  context->finish_time = clock();

/******************************************************************************/
  /* Test for acceptance: scan all states on frontier and their reductions */
  /* If we find an accepting reduction then walk all it's in-edges to locate the source node for the SSG */
  for (int h = 0; h < this_dfa->state_count; h++)
  {
    if (context->current_frontier[h] != NULL)     /* If state h is on current frontier */
    {
      int *these_parse_actions = dfa_retrieve_all_actions (this_dfa, h + context->first_dfa_state, GRAM_EOS_SYMBOL_NUMBER);    /* Get T(i, $) */

#if 0
      text_printf("Acceptance check: state %u is occupied\n", h + context->first_dfa_state);
#endif

      for (int *this_parse_action = these_parse_actions; *this_parse_action != 0; this_parse_action++)
        if (GRAM_IS_SLOT (this_dfa->nfa->grammar, *this_parse_action))
        {
          if (this_dfa->reduction_table[*this_parse_action - this_dfa->nfa->grammar->first_level_0_slot].is_accepting)
          {
#if 0
            text_printf("Acceptance check at level %u: in state %u found accepting reduction number %u with lookahead symbol %u\n",
            context->i,
            h + context->first_dfa_state,
            *this_parse_action - this_dfa->nfa->grammar->first_level_0_slot,
            context->a[context->i]
            );
#endif
            if (context->a[context->i] == GRAM_EOS_SYMBOL_NUMBER)
            {
              this_derivation->accept = 1;

              /* Walk all the in edges of this state to find the start reduction */
              for (ssg_edge * this_edge =
                   (ssg_edge *) graph_next_out_edge (context->current_frontier[h]);
                   this_edge != NULL; this_edge = (ssg_edge *) graph_next_out_edge (this_edge))
              {
                ssg_node *dest_node = (ssg_node *) graph_destination (this_edge);

                /* If this edge points at the DFA start state... */
                if (dest_node->level == 0)
                {
                  graph_set_root (this_derivation->sppf, this_edge->sppf_node);
                }
              }

#if 0
              if (graph_root (this_derivation->sppf) == NULL)
                text_message (TEXT_ERROR, "attempt to prune SPPF that has no recognised root node\n");
              else
              {
                if (script_gtb_verbose())
                  sppf_statistics (this_derivation, "Before SPPF pruning");
                set_ *reachable = graph_reachable_nodes (this_derivation->sppf, graph_root (this_derivation->sppf));
                graph_retain_set_of_nodes (this_derivation->sppf, reachable);
                set_free(reachable);

                  if (script_gtb_verbose()) sppf_statistics (this_derivation, "After SPPF pruning");
              }
#else
     //           text_message(TEXT_INFO, "pruning disabled in this version\n");
#endif
              break;                                         /* No need to check the rest of the states on the final frontier */
            }
          }
        }
      mm_free (these_parse_actions);
    }
  }

/******************************************************************************/

  loadParseTime();
  loadAccept(this_derivation->accept);

  if (script_gtb_verbose())
    if (this_derivation->accept)
      text_message (TEXT_INFO, "BRNGLR parse: accept\n");
    else
      text_message (TEXT_INFO, "BRNGLR parse: reject after consuming %i out of %i lexemes\n", context->i - 1, context->n);

  double run_time = 0;

  if (script_gtb_verbose() && run_time > 0.001)
  {
    text_message(TEXT_INFO, "Run time: %fs\n", run_time);

  }

  if (script_gtb_verbose())
    text_message(TEXT_INFO, "SSG: %u DFA state nodes, %u extended state nodes, %u edges\n",
               context->gss_state_node_count,
               context->gss_extended_node_count,
               context->gss_edge_count);

  sr_ssg_statistics(stdout, this_derivation->ssg);

  if (script_gtb_verbose())
    text_message(TEXT_INFO, "SPPF as constructed: %u epsilon nodes, %u terminal nodes, %u nonterminal nodes, %u intermediate nodes, %u pack nodes, %u edges\n",
               context->sppf_epsilon_node_count,
               context->sppf_terminal_node_count,
               context->sppf_nonterminal_node_count,
               context->sppf_intermediate_node_count,
               context->sppf_pack_node_count,
               context->sppf_edge_count);


  if (script_gtb_verbose()) {
    FILE *sppf_vcg_file = fopen ("sppf.vcg", "w");
    text_redirect (sppf_vcg_file);
    sppf_set_render_derivation (this_derivation);
    if (context->sppf_edge_count < 3000)
      graph_vcg (this_derivation->sppf, NULL, sppf_vcg_print_node, sppf_vcg_print_edge);
    fclose (sppf_vcg_file);
    text_redirect (stdout);
  }

  /* Cleanup: free memory */
  mm_free (context->current_frontier);
  mm_free (context->next_frontier);
  mm_free (context->shift_stack);
  mm_free (context->shift_stack_prime);
  mm_free (context->a);
  mm_free (context->sppf_node_table);
  mm_free (context->N_alpha_equivalence_classes);
  mm_free (context);

  artLog();

  return this_derivation;
}
