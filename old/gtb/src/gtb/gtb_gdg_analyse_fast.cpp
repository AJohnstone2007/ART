/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 27 September 2005
*
* gtb_analyse_fast.cpp - high speed loop breaking for RIGLR terminalisation
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdlib.h>
#include <math.h>
#include "graph.h"
#include "hist.h"
#include "gtb.h"
#include "gtb_scr.h"
#include "gtb_gram.h"
#include "gtb_gdg.h"
#include "gtb_gdg.h"

static unsigned set_fixed_bv_size = 0;
static unsigned hash_table_size = 0;
static unsigned break_table_size = 0;
static unsigned *break_next_free_location;
static unsigned *break_table_top;
static unsigned **hash_table;
static unsigned *break_table;
static unsigned *cardinality_table;

static unsigned *gdg_break_set;
static unsigned gdg_break_set_cardinality;
static unsigned gdg_break_set_cardinality_limit;
static bool gdg_cardinality_limit_triggered;
static unsigned *gdg_diff_sets;
static unsigned *gdg_diff_set;
static int leaf_count;
static int break_index;
static int calls;
struct fast_cycle_data {unsigned *bv; unsigned *arr; unsigned cardinality;};
static fast_cycle_data *fast_cycles;
static bool prune_break_sets_by_set;
static bool prune_break_sets_by_element;

/* lookup table of bit masks for bits 0 to 31 */
static const unsigned set_fixed_bv_mask[32] = {0x00000001, 0x00000002, 0x00000004, 0x00000008,
                                               0x00000010, 0x00000020, 0x00000040, 0x00000080,
                                               0x00000100, 0x00000200, 0x00000400, 0x00000800,
                                               0x00001000, 0x00002000, 0x00004000, 0x00008000,
                                               0x00010000, 0x00020000, 0x00040000, 0x00080000,
                                               0x00100000, 0x00200000, 0x00400000, 0x00800000,
                                               0x01000000, 0x02000000, 0x04000000, 0x08000000,
                                               0x10000000, 0x20000000, 0x40000000, 0x80000000};


                                           //    0      1      2      3      4      5      6      7      8      9      A      B      C      D      E      F   lo
static const bool is_singleton_byte[256] = {
                                             false,  true,  true, false,  true, false, false, false,  true, false, false, false, false, false, false, false,  // 0x
                                              true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 1x
                                              true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 2x
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 3x
                                              true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 4x
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 5x
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 6x
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 7x
                                              true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 8x
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // 9x
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // Ax
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // Bx
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // Cx
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // Dx
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,  // Ex
                                             false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false   // Fx
                                           };

static void set_fixed_bv_extent(unsigned size)
{
  set_fixed_bv_size = size;
}

static unsigned *set_fixed_bv_allocate(void)
{
  return (unsigned*) mem_calloc(set_fixed_bv_size, sizeof(unsigned));
}

static unsigned set_fixed_bv_hash(unsigned *src)
{
  unsigned hash_number = 0;

  for (int count = 0; count < set_fixed_bv_size; count++)
    hash_number = hash_number + *src++;

  return hash_number % hash_table_size;
}

static bool set_fixed_bv_equal_set(unsigned *dst, unsigned *src)
{
  for (int count = 0; count < set_fixed_bv_size; count++)
    if (*dst++ != *src++)
      return false;

  return true;
}

static void set_fixed_bv_copy(unsigned *dst, unsigned *src)
{
  for (int count = 0; count < set_fixed_bv_size; count++)
    *dst++ = *src++;
}

static bool set_fixed_bv_is_subset(unsigned *superset, unsigned *subset)
{
  for (int count = 0; count < set_fixed_bv_size; count++)
    if ((*subset++ & ~(*superset++)) != 0)
      return false;

  return true;
}

static void set_fixed_bv_unite_element(unsigned *dst, unsigned element)
{
   *(dst + (element >> 5)) |= set_fixed_bv_mask[element & 0x1F];
}

static void set_fixed_bv_difference_element(unsigned *dst, unsigned element)
{
   *(dst + (element >> 5)) &= ~set_fixed_bv_mask[element & 0x1F];
}

static void set_fixed_bv_print(unsigned *dst)
{
  int element_number = 0;
  bool first = true;

  for (int count = 0; count < set_fixed_bv_size; count++)
  {
    unsigned elements = dst[count];

    for (int bit_count = 0; bit_count < 32; bit_count++)
    {
      if ((elements & 1) != 0)
      {
        if (!first)
          text_printf(", ");
        else
          first = false;

        text_printf("%i", element_number);
      }

      element_number++;
      elements = elements >> 1;
    }
  }
}

bool set_fixed_bv_has_common_elements_set(unsigned *dst, unsigned *src)
{
  for (int count = 0; count < set_fixed_bv_size; count++)
    if (((*(dst+count)) & (*(src+count))) != 0)
      return true;

  return false;
}

void gdg_break_cycles_rec(unsigned index)
{
  if ((++calls & 0xffffff) == 0)
    text_printf("\r%u calls, %u break sets", calls, break_index);

#if 0
  text_printf("Recurse to level %i: gdg_break_set is {", index);
  set_fixed_bv_print(gdg_break_set);
  text_printf("}\n and new set is {");
  if (fast_cycles[index].bv == NULL)
    text_printf("NULL");
  else
    set_fixed_bv_print(fast_cycles[index].bv);
  text_printf("}\n");
#endif

  if ((gdg_break_set_cardinality_limit != 0) && (gdg_break_set_cardinality_limit < gdg_break_set_cardinality))
  {
    gdg_cardinality_limit_triggered = true;
    return;
  }

  fast_cycle_data *cycle = &fast_cycles[index];

  if (cycle->bv == NULL) //we're at the end of the list of cycles so add to break table
  {
    leaf_count++;

#if 0
    text_printf("Looking up break set {");
    set_fixed_bv_print(gdg_break_set);
    text_printf("} - ");
#endif

    // Look up the current break set in the hash table
    unsigned hash_bucket = set_fixed_bv_hash(gdg_break_set);
    int found;
    while (1)
    {
      if (hash_table[hash_bucket] == 0)
      {
        found = false;
        break;
      }
      else
      {
        if (set_fixed_bv_equal_set(hash_table[hash_bucket], gdg_break_set))
        {
          found = true;
          break;
        }
        else
          hash_bucket = (hash_bucket + 1) % hash_table_size;
      }
    }

#if 0
    text_printf("%s\n", found ? "found" : "not found");
#endif

    // If not found, then add it
    if (!found)
    {
      set_fixed_bv_copy(break_next_free_location, gdg_break_set);
      //Hash bucket is still pointing at the place we'd like to insert this set
      hash_table[hash_bucket] = break_next_free_location;

      if ((break_next_free_location += set_fixed_bv_size) >= break_table_top)
        text_message(TEXT_FATAL, "ran out of space for break sets\n");

      cardinality_table[break_index] = gdg_break_set_cardinality;

#if 0
      text_printf("Added as bucket %u and break set index %i\n", hash_bucket, break_index);
#endif

      if (prune_break_sets_by_set)
      {
        unsigned *inner_cardinality = cardinality_table;
        unsigned *inner_set = break_table;
        for (int inner_count = 0; inner_count < break_index; inner_count++, inner_cardinality++, inner_set += set_fixed_bv_size)
        {
          if (*inner_cardinality == 0)
            continue;

          if (gdg_break_set_cardinality > *inner_cardinality)
          {
            if (set_fixed_bv_is_subset(gdg_break_set, inner_set))
            {
              cardinality_table[break_index] = 0;
              break;
            }
          }
          else
          {
            if (set_fixed_bv_is_subset(inner_set, gdg_break_set))
            {
              *inner_cardinality = 0;
            }
          }
        }
      }

      break_index++;
    }
  }
  else // We're not at the end of the list, so modify break_set and recurse
  {
    if (set_fixed_bv_has_common_elements_set(gdg_break_set, cycle->bv))  // Nothing to do: we're looking for mininal break sets
      gdg_break_cycles_rec(index+1);
    else
    {
      // Construct new_break set for this cycle
//#define PRUNE_BY_ELEMENT_TRACE
      if (prune_break_sets_by_element)
      {
        // Find break sets that differ from gdg_break_set by exactly one bit, and add those elements to the diff set
        unsigned set_fixed_bv_size_char = set_fixed_bv_size * sizeof(unsigned);
        unsigned char *break_set_char = (unsigned char*) break_table;                                    //Character pointer to break set
        unsigned *break_set_cardinality = cardinality_table;

#if defined(PRUNE_BY_ELEMENT_TRACE)
        text_printf("Prune by element for level %i: at entry diff set is {", index);
        set_fixed_bv_print(gdg_diff_set);
        text_printf("} copying diff set from %p->%p (level %i->%i)\n",
                    gdg_diff_set, gdg_diff_set + set_fixed_bv_size,
                    gdg_diff_set - gdg_diff_sets, gdg_diff_set + set_fixed_bv_size- gdg_diff_sets);
#endif

        memcpy(gdg_diff_set + set_fixed_bv_size, gdg_diff_set, set_fixed_bv_size * sizeof(unsigned));   //Copy diff set to placeholder for next call
        gdg_diff_set += set_fixed_bv_size;                                                              // and set gdg_diff_set pointer to next level's slot

        for (int count = 0; count < break_index; count++, break_set_cardinality++, break_set_char += set_fixed_bv_size_char)      // Scan over all break sets
        {
          if (*break_set_cardinality == 0)                   // Skip suppressed sets
            continue;

          // Now scan this break set and gdg_break set to find that single element that differs
          unsigned diff_element = 0;
          unsigned diff_index = 0;
          unsigned char *gdg_break_set_char = (unsigned char*) gdg_break_set;
          unsigned char *break_set_test_char = break_set_char;

#if defined(PRUNE_BY_ELEMENT_TRACE)
        text_printf("Checking against break set %i {", count);
        set_fixed_bv_print((unsigned*) break_set_char);
        text_printf("}\n");
#endif

          for (int count = 0; count < set_fixed_bv_size_char; count++, gdg_break_set_char++, break_set_test_char++)
          {
            unsigned char this_element = *break_set_test_char & ((unsigned char) ~(*gdg_break_set_char));


#if defined(PRUNE_BY_ELEMENT_TRACE)
        text_printf("Diff element %u, diff_index %u, testing element %i: %u against %u to give %u: ", diff_element, diff_index, count, *break_set_test_char, *gdg_break_set_char, this_element);
#endif

            if (this_element != 0)
            {
              if (is_singleton_byte[this_element])
              {
#if defined(PRUNE_BY_ELEMENT_TRACE)
                text_printf("result is singelton - ");
#endif
                if (diff_element == 0)
                {
#if defined(PRUNE_BY_ELEMENT_TRACE)
                  text_printf("setting diff index and element\n");
#endif
                  diff_index = count;
                  diff_element = this_element;
                }
                else
                {
                  // Reset the diff element back again because two singletons were found
#if defined(PRUNE_BY_ELEMENT_TRACE)
                  text_printf("resetting diff index and element on second singleton\n");
#endif
                  diff_element = 0;
                  break;
                }
              }
              else
              {
#if defined(PRUNE_BY_ELEMENT_TRACE)
                text_printf("result is multiple\n");
#endif
                break;
              }
            }
#if defined(PRUNE_BY_ELEMENT_TRACE)
          else
            text_printf("result is zero\n");

#endif


          }

          if (diff_element != 0)   // Did we find a single differing element?
          {
            *(gdg_diff_set + diff_index) |= diff_element;   //Unite it into diff
            *break_set_cardinality = 0;
#if defined(PRUNE_BY_ELEMENT_TRACE)
            text_printf("Found break set with single differing element: suppressed break set and updated diff_set to {");
            set_fixed_bv_print(gdg_diff_set);
            text_printf("}\n");
#endif
          }
        }

#if defined(PRUNE_BY_ELEMENT_TRACE)
            text_printf("At end of break set comparison, diff set is {");
            set_fixed_bv_print(gdg_diff_set);
            text_printf("}\n");
#endif

        //By now we have collected all of the singeltons for this cycle (this call)
        // so step through the cycle elements calling those that aren't in the diff set
        gdg_break_set_cardinality++;
        for (unsigned *this_cycle_element = cycle->arr;
             *this_cycle_element != SET_END;
             this_cycle_element++)
        {
#if defined(PRUNE_BY_ELEMENT_TRACE)
            text_printf("Applying cycle element %u: ", *this_cycle_element);
#endif

          if ((*(gdg_diff_set + (*this_cycle_element >> 5)) & set_fixed_bv_mask[*this_cycle_element & 0x1F]) == 0)
          {
#if defined(PRUNE_BY_ELEMENT_TRACE)
            text_printf("recursing\n");
#endif
            set_fixed_bv_unite_element(gdg_break_set, *this_cycle_element);
            gdg_break_cycles_rec(index+1);
            set_fixed_bv_difference_element(gdg_break_set, *this_cycle_element);
          }
#if defined(PRUNE_BY_ELEMENT_TRACE)
          else
            text_printf("recursing\n");
#endif
        }

        gdg_break_set_cardinality--;
        gdg_diff_set -= set_fixed_bv_size;      //Restore gdg_diff_set pointer to this_level's slot
        text_printf("At exit from level %i diff set is {", index);
        set_fixed_bv_print(gdg_diff_set);
        text_printf("}\n");
      }
      else
      {
        gdg_break_set_cardinality++;
        for (unsigned *this_cycle_element = cycle->arr;
             *this_cycle_element != SET_END;
             this_cycle_element++)
        {
          set_fixed_bv_unite_element(gdg_break_set, *this_cycle_element);
          gdg_break_cycles_rec(index+1);
          set_fixed_bv_difference_element(gdg_break_set, *this_cycle_element);
        }
        gdg_break_set_cardinality--;
      }
    }
  }
}

void gdg_analyse_fast(gdg * this_gdg,
                        int break_set_disposition,
                        bool remove_break_sets,
                        int long break_set_cardinality_limit,
                        bool restart_after_cardinality_limit_reached,
                        unsigned long break_set_count)
{
  // Init step 1: sign on
  if (script_gtb_verbose())
  {
    text_printf("Fast analysis of loop structure in GDG with break set cardinality limit of %li after which analysis %s\n", break_set_cardinality_limit,
                restart_after_cardinality_limit_reached ? "is rerun with no cardinality limit" : "terminates");

    text_printf("Break sets are %s\n", break_set_disposition == SCRIPT_BUILT_IN_retain_break_sets ? "retained" :
                                   break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_element ? "pruned on an element by element basis" :
                                   break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_set ? "pruned when a new break set is detected" :
                                   break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_table ? "pruned after all break sets have been found" :
                                   "??? illegal break set disposition");
    text_printf("Pruned break sets are %s\n", remove_break_sets ? "deleted from the table" : "left in the table but marked with zero cardinality");
  }

  // Init step 2: unpack break set disposition into individual flags
  bool prune_break_sets_at_end;

  if (break_set_disposition == SCRIPT_BUILT_IN_retain_break_sets)
  {
    prune_break_sets_by_element = false;
    prune_break_sets_by_set = false;
    prune_break_sets_at_end = false;
  }
  else if (break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_element)
  {
    prune_break_sets_by_element = true;
    prune_break_sets_by_set = false;
    prune_break_sets_at_end = false;
  }
  else if (break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_set)
  {
    prune_break_sets_by_element = false;
    prune_break_sets_by_set = true;
    prune_break_sets_at_end = false;
  }
  else if (break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_table)
  {
    prune_break_sets_by_element = false;
    prune_break_sets_by_set = false;
    prune_break_sets_at_end = true;
  }
  else
    text_message(TEXT_FATAL, "unknown break set disposition detected\n");

  // Init step 3: make top level SCC partitions
  set_ empty_set = SET_NULL;
  this_gdg->strong_number = graph_tarjan_scc(this_gdg->gdg_nodes, &empty_set, 1, NULL);

  set_ all_nodes = SET_NULL;

  int max_scc_number = -1000;

  for (gdg_node* this_node = (gdg_node*) graph_next_node(this_gdg->gdg_nodes);
       this_node != NULL;
       this_node = (gdg_node*) graph_next_node(this_node))
  {
    set_unite_element(&all_nodes, graph_atom_number(this_node));

    if (this_gdg->strong_number[graph_atom_number(this_node)] > max_scc_number)
      max_scc_number = this_gdg->strong_number[graph_atom_number(this_node)];
  }

  if (script_gtb_verbose())
    text_printf("GDG has %i strongly connected component%s\n", max_scc_number, max_scc_number == 1 ? "" : "s");

#if 0
  // Enable this to output SCC structure
  text_printf("Strongly connected component numbers\n");
  for (gdg_node* this_node = (gdg_node*) graph_next_node(this_gdg->gdg_nodes);
       this_node != NULL;
       this_node = (gdg_node*) graph_next_node(this_node))
  {
    text_printf("%s: %i\n", this_node->symbol_table_entry->id, this_gdg->strong_number[graph_atom_number(this_node)]);
  }
#endif

  //Init step 4: initialise set handler
  set_fixed_bv_extent((graph_max_edge_number(this_gdg->gdg_nodes) /32) +1);
  if (script_gtb_verbose())
    text_printf("Fixed bit vector sets require %u 32-bit words each\n", set_fixed_bv_size);

  // Init setp 5: allocate and initialise globals
  hash_table_size = break_set_count*2;
  break_table_size = break_set_count;
  hash_table = (unsigned**) mem_calloc(hash_table_size, sizeof(unsigned*));
  break_table = (unsigned*) mem_calloc(break_table_size * set_fixed_bv_size, sizeof(unsigned));
  break_table_top = break_table + (break_table_size * set_fixed_bv_size);
  cardinality_table = (unsigned*) mem_calloc(break_table_size, sizeof(unsigned));
  gdg_break_set = set_fixed_bv_allocate();
  gdg_break_set_cardinality = 0;
  if (script_gtb_verbose())
    text_printf("Allocated %lu hash table slots (%lu bytes) and %lu break set slots (%lu bytes)\n",
                hash_table_size, hash_table_size * sizeof(unsigned*),
                break_table_size, break_table_size * set_fixed_bv_size * sizeof(unsigned));

  // Loop over SCC: find cycles; check SCC has L and R cycles; output the CIG and then break the cycles
  set_ scc_nodes = SET_NULL, scc_edges = SET_NULL;

  for (int scc_number = -1; scc_number <= max_scc_number; scc_number++)
  {
    gdg_break_set_cardinality_limit = break_set_cardinality_limit;

    // Loop step 1: collect nodes and edges in this SCC
    set_clear(&scc_nodes);
    set_clear(&scc_edges);

    for (gdg_node* this_node = (gdg_node*) graph_next_node(this_gdg->gdg_nodes);
         this_node != NULL;
         this_node = (gdg_node*) graph_next_node(this_node))
      if (this_gdg->strong_number[graph_atom_number(this_node)] == scc_number || (scc_number == -1 && this_gdg->strong_number[graph_atom_number(this_node)] < 0))
      {
        set_unite_element(&scc_nodes, graph_atom_number(this_node));
        for (gdg_edge* this_edge = (gdg_edge*) graph_next_out_edge(this_node); this_edge != NULL; this_edge = (gdg_edge*) graph_next_out_edge(this_edge))
          if (this_gdg->strong_number[graph_atom_number(graph_destination(this_edge))] == scc_number)
            set_unite_element(&scc_edges, graph_atom_number(this_edge));
      }

    if (script_gtb_verbose())
      text_printf("\n*** Processing SCC component %i%s: %u node%s and %u edge%s\n", scc_number,
                                                                                  scc_number == -1 ? " (singleton SCC's)" : scc_number == 0 ? "(unreachable nodes)" : "",
                                                                                  set_cardinality(&scc_nodes), set_cardinality(&scc_nodes) == 1 ? "" :"s",
                                                                                  set_cardinality(&scc_edges), set_cardinality(&scc_edges) == 1 ? "" :"s");

    // Loop step 2: find cycles
    void *gdg_cycle_table = gdg_compute_cycles(this_gdg, &scc_nodes, scc_number);
    void *cycle_inclusion_graph = gdg_construct_cycle_inclusion_graph(gdg_cycle_table);

    //Loop step 3: output cycle inclusion graph for this SCC
    char *cig_file_name = "cig0.vcg";
    FILE *cig_file = fopen(cig_file_name, "w");
    text_redirect(cig_file);
    graph_vcg(cycle_inclusion_graph, NULL, NULL, NULL);
    text_redirect(stdout);
    fclose(cig_file);
    cig_file_name[3]++;

    //Loop step 4: output edge cycle histogram
    if (script_gtb_verbose())
      gdg_edge_cycle_histogram(gdg_cycle_table, scc_number);

    // Loop step 5: check that cycle table contains both L and R cycles
    unsigned cycle_count = 0;
    bool left_context = false;
    bool right_context = false;
    for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
         this_symbol != NULL;
         this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
    {
      cycle_count++;
      if (this_symbol->left_context)
        left_context = true;
      if (this_symbol->right_context)
        right_context = true;
    }

      if (script_gtb_verbose())
        text_printf("Partition %i has %i cycles: left context cycles are %s and right context cycles are %s; %s cycle breaking for this cycle\n",
                scc_number, cycle_count,
                left_context ? "present" : "absent",
                right_context ? "present": "absent",
                left_context && right_context ? "performing" : "skipping");

    if (!(left_context && right_context))
      continue;

    //Loop step 5: clear break table structure
    memset(hash_table, 0, hash_table_size * sizeof(unsigned*));
    memset(break_table, 0, break_table_size * set_fixed_bv_size * sizeof(unsigned));
    break_next_free_location = break_table;
    leaf_count = 0;
    calls = 0;
    break_index = 0;

    int limit_loop_count = 0;
    do
    {
      limit_loop_count++;
      gdg_cardinality_limit_triggered = false;

      fast_cycles = (fast_cycle_data*) mem_calloc(cycle_count + 1, sizeof(fast_cycle_data));
      gdg_diff_sets = (unsigned *) mem_calloc((cycle_count + 1), sizeof(unsigned) * set_fixed_bv_size);

      //Loop step 6: two passes of the cycle breaker, one for L and the rest for R
      for (int pass = 0; pass < 2; pass++)
      {
        int cycle_number = 0;

        // Inner loop 1: load fast_cycle_data from the cycle table
        for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
             this_symbol != NULL;
             this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
        {
          if ((pass == 0 && this_symbol->left_context) || (pass == 1 && this_symbol->right_context))
          {
            fast_cycles[cycle_number].cardinality = set_cardinality(&this_symbol->id);
            fast_cycles[cycle_number].arr = set_array(&this_symbol->id);
            fast_cycles[cycle_number].bv = set_fixed_bv_allocate();
            for (unsigned *element = fast_cycles[cycle_number].arr; *element != SET_END; element++)
              set_fixed_bv_unite_element(fast_cycles[cycle_number].bv, *element);

  #if 0
  //Debug code to check loading of fast sets
           text_printf("Context %s, cycle_number %i, cardinality %i\nOriginal cycle: {", pass == 0 ? "L" : "R", cycle_number, fast_cycles[cycle_number].cardinality);
           set_print_set(&this_symbol->id, 0, 0);
           text_printf(         "}\n    Fast cycle: {");
           set_fixed_bv_print(fast_cycles[cycle_number].bv);
           text_printf("}\n");
  #endif
            cycle_number++;
          }
        }

        // Inner loop 3: break the cycles
        gdg_diff_set = gdg_diff_sets;
        gdg_break_cycles_rec(0);

        // Inner loop 3: free fast cycle memory
        for (int count = 0; count < cycle_count+1; count++)
          if (fast_cycles[count].bv != NULL)
          {
            mem_free(fast_cycles[count].bv);
            mem_free(fast_cycles[count].arr);

            fast_cycles[count].bv = NULL;
            fast_cycles[count].arr = NULL;
          }
      }

      //Loop step 8: do break set pruning by table
      if (prune_break_sets_at_end)
      {
        if (script_gtb_verbose())
          text_printf("Set pruning by table: %u break sets to prune\n", break_index);
        unsigned *outer_cardinality = cardinality_table;
        unsigned *outer_set = break_table;
        for (int outer_count = 0; outer_count < break_index; outer_count++, outer_cardinality++, outer_set += set_fixed_bv_size)
        {
          if (*outer_cardinality == 0)
            continue;

          unsigned *inner_cardinality = outer_cardinality + 1;
          unsigned *inner_set = outer_set + set_fixed_bv_size;
          for (int inner_count = outer_count + 1; inner_count < break_index; inner_count++, inner_cardinality++, inner_set += set_fixed_bv_size)
          {
            if (*inner_cardinality == 0)
              continue;

  //#define PRUNE_BY_TABLE_TRACE 0
  #if defined(PRUNE_TRACE)
            text_printf("Prune by table: outer set is {");
            set_fixed_bv_print(outer_set);
            text_printf("} inner set is {");
            set_fixed_bv_print(inner_set);
            text_printf("}: ");
  #endif

            if (*outer_cardinality > *inner_cardinality)
            {
              if (set_fixed_bv_is_subset(outer_set, inner_set))
              {
  #if defined(PRUNE_TRACE)
                text_printf("outer set suppressed: stepping to next outer set\n");
  #endif
                *outer_cardinality = 0;
                break;
              }
  #if defined(PRUNE_TRACE)
              else
                text_printf("outer set retained\n");
  #endif
            }
            else
            {
              if (set_fixed_bv_is_subset(inner_set, outer_set))
              {
  #if defined(PRUNE_TRACE)
                text_printf("inner set suppressed\n");
  #endif
                *inner_cardinality = 0;
              }
  #if defined(PRUNE_TRACE)
              else
                text_printf("inner set retained\n");
  #endif
            }
          }
        }
      }

      //Loop step 9: print out the break sets
      text_printf("\nBreak sets for partition %i\n", scc_number);
      int retained_count = 0;
      int suppressed_count = 0;
      for (int count = 0; count < break_index; count++)
      {
       if (cardinality_table[count] != 0)
       {
  #if 1
         text_printf("%i: {", count);
         set_fixed_bv_print(break_table + (count * set_fixed_bv_size));
         text_printf("} cardinality %u\n", cardinality_table[count]);
  #endif
         retained_count++;
       }
       else
         suppressed_count++;
      }

      // Loop step 10: print out counters
      if (script_gtb_verbose())
        text_printf("Partition %i: %i leaf calls, %i retained break sets, %i suppressed break sets, total of %i break sets\n", scc_number, leaf_count, retained_count, suppressed_count, retained_count + suppressed_count);

      // Loop step 11: free loop specific memory
      mem_free(gdg_diff_sets);
      mem_free(fast_cycles);

      // Loop step 12: check for cardinality limit triggering and either go round again or abort
      if ((limit_loop_count == 1) && gdg_cardinality_limit_triggered)
        text_printf("Break set cardinality limit of %u triggered: %s\n", gdg_break_set_cardinality_limit, restart_after_cardinality_limit_reached ? "restarting" : "terminating");

      gdg_break_set_cardinality_limit = 0;

    } while (limit_loop_count == 1 && gdg_cardinality_limit_triggered && restart_after_cardinality_limit_reached);
  }

  // Wrap up step 1: free memory
  set_free(&scc_nodes);
  set_free(&scc_edges);
  mem_free(gdg_break_set);
}

