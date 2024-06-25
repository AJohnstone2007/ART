/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* graph.h - graph creation and manipulation routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef GRAPH_H
#define GRAPH_H

#include <stddef.h>
#include <stdio.h>

#define GRAPH_ATOM_NUMBER_T unsigned
#define GRAPH_ATOM_NUMBER_F "%u"

#define GRAPH_ITERATE_NODE(this_graph, this_node, type) for (this_node = (type *) graph_next_node(this_graph); this_node != NULL; this_node = (type *) graph_next_node(this_node))

#define GRAPH_ITERATE_IN_EDGE(this_node, this_edge, type) for (this_edge = (type *) graph_next_in_edge(this_node); this_edge != NULL; this_edge = (type *) graph_next_in_edge(this_edge))

#define GRAPH_ITERATE_OUT_EDGE(this_node, this_edge, type) for (this_edge = (type *) graph_next_out_edge(this_node); this_edge != NULL; this_edge = (type *)graph_next_out_edge(this_edge))

void * graph_insert_graph(char * id);
void * graph_insert_node(size_t size, void * node_or_graph);
void * graph_insert_edge(size_t size, void * destination_node, void * source_node);
void * graph_insert_edge_after_final(size_t size, void * destination_node, void * source_node);

void * graph_insert_node_child(size_t node_size, size_t edge_size, void * parent_node);
void * graph_insert_node_parent(size_t node_size, size_t edge_size, void * child_node);

void * graph_delete_graph(void * graph);
void * graph_delete_node(void * node);
void * graph_delete_only_node(void * node);
void * graph_delete_edge(void * edge);

void * graph_initial_node(const void * graph_or_node);
void * graph_final_node(const void * graph_or_node);
void * graph_next_node(const void * graph_or_node);
void * graph_previous_node(const void * graph_or_node);

void * graph_initial_out_edge(const void * node_or_edge);
void * graph_final_out_edge(const void * node_or_edge);
void * graph_next_out_edge(const void * node_or_edge);
void * graph_previous_out_edge(const void * node_or_edge);

void * graph_initial_in_edge(const void * node_or_edge);
void * graph_final_in_edge(const void * node_or_edge);
void * graph_next_in_edge(const void * node_or_edge);
void * graph_previous_in_edge(const void * node_or_edge);

void graph_set_root(const void * graph, void * root);
void * graph_root(const void * graph);

void * graph_destination(const void * edge);
void * graph_source(const void * edge);

void graph_create_node_index(void *graph);
void graph_create_edge_index(void *graph);
GRAPH_ATOM_NUMBER_T graph_max_node_number(void * graph);

GRAPH_ATOM_NUMBER_T *graph_level(void *graph, int deep);

void **graph_node_index(void *graph);
unsigned graph_node_index_size(void *graph);
void **graph_edge_index(void *graph);
unsigned *graph_edge_index_size(void *graph);

GRAPH_ATOM_NUMBER_T  graph_atom_number(const void * graph_or_node_or_edge);
void graph_set_atom_number(void *atom, GRAPH_ATOM_NUMBER_T number);

void graph_epsilon_prune_rdp_tree(void *parent, size_t edge_size);

void graph_print_consistency(void *graph);

void graph_vcg(void * graph,
               void(* graph_action)(const void * graph),
               void(* node_action)(const void * node),
               void(* edge_action)(const void * edge)
              );

void graph_vcg_diagnostic_edge(const void *edge);


#endif

/* End of graph.h */
