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

int graph_compare_double(void * left, void * right); 
int graph_compare_long(void * left, void * right); 
int graph_compare_mem(void * left, void * right, size_t size); 
int graph_compare_string(void * left, void * right); 

void graph_delete_edge(void * edge); 
void graph_delete_graph(void * graph); 
void graph_delete_node(void * node); 

unsigned long graph_get_atom_number(const void * graph_or_node_or_edge); 
void * graph_get_edge_target(const void * edge); 
void * graph_get_final_edge(const void * node_or_edge); 
void * graph_get_final_node(const void * node_or_edge); 
void * graph_get_next_edge(const void * node_or_edge); 
void * graph_get_next_node(const void * graph_or_node); 
void * graph_get_previous_edge(const void * node_or_edge); 
void * graph_get_previous_node(const void * node_or_edge); 

unsigned graph_hash_double(unsigned hash_prime, void * data); 
unsigned graph_hash_long(unsigned hash_prime, void * data); 
unsigned graph_hash_mem(unsigned hash_prime, void * data); 
unsigned graph_hash_string(unsigned hash_prime, void * data); 

void * graph_insert_node_child(size_t node_size, size_t edge_size, void * parent_node); 
void * graph_insert_node_parent(size_t node_size, size_t edge_size, void * child_node); 
void * graph_insert_edge(size_t size, void * target_node, void * node_or_edge); 
void * graph_insert_graph(char * id); 
void * graph_insert_node(size_t size, void * node_or_graph); 

void graph_vcg(void * graph, 
void(* graph_action)(const void * graph), 
void(* node_action)(const void * node), 
void(* edge_action)(const void * edge)
); 
void graph_vcg_atoms(void * graph, 
void(* graph_action)(const void * graph), 
void(* node_action)(const void * node), 
void(* edge_action)(const void * edge)
); 


#endif

/* End of graph.h */
