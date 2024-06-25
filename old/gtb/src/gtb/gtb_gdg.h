/*****************************************************************************
*
* GTB release 2.5 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 25 Sep 2002
*
* gtb_gdg.h - Grammar Dependency Graph functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_GDG_H
#define GTB_GDG_H

#include "gtb_gram.h"

struct gdg_node
{
  gram_symbols_data *symbol_table_entry;
  int loop_level;
};

struct gdg_edge
{
  unsigned compression_factor;
  int left_epsilon_context:1;
  int left_context:1;
  int right_epsilon_context:1;
  int right_context:1;
};

struct gdg
{
  int *strong_number;
  unsigned *region_number;
  set_ self_loop_edges;
  void *gdg_nodes;
  struct grammar* grammar;
};

struct gdg_cycle_data
{
  set_ id;
  unsigned *id_array;
  int left_epsilon_context:1;
  int left_context:1;
  int right_epsilon_context:1;
  int right_context:1;
  int scc_number;
};

gdg *gdg_analyse_gdg(gdg * this_gdg);
void gdg_analyse_gdg_bu(gdg * this_gdg, int break_set_disposition, unsigned long recursion_count, int long break_set_cardinality_limit);
gdg *gdg_compress_gdg(gdg * this_gdg, unsigned recursion_level);
gdg *gdg_construct_gdg(grammar * this_gram);
void gdg_write(FILE *output_file, gdg *this_gdg);
void gdg_render(FILE *output_file, gdg *this_gdg);

void* gdg_compute_cycles(gdg* this_gdg, set_ *valid_nodes, int partition_number);
void *gdg_construct_cycle_inclusion_graph(void *cycle_table);
void gdg_edge_cycle_histogram(void* cycle_table, int scc_number);

#endif

