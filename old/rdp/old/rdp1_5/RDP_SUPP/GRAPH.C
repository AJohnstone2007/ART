/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* graph.c - graph creation and manipulation routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "graph.h"
#include "textio.h"
#include "memalloc.h"
/*******************************************************************************
*
* Graphs are represented using doubly linked lists of `atoms'. Each atom
* contains three pointers: `succ1' (successor1), `succ2' (successor2) and
* `pred' (predecessor). These three pointers will either contain NULL or the
* adddress of the base address of another atom.
*
* Whenever an atom is created, a user data block of `size' bytes may be
* included. User data is stored after the pointers, and all user level
* references are to the base of the user data block so that the internal
* pointers are invisible to the user.
*
* Operations on atoms use the base of the internal pointers, other
* operations use the base of the user data.
*
*******************************************************************************/

/* The basic graph pointer block used by edges, nodes and graph headers */
/* Designed to be part of a doubly linked list with data hooks */
typedef struct graph_atom_struct
{
  struct graph_atom_struct
  * succ1,                    /* successor 1: used for next graph, next edge, or start of edge list */
  * succ2,                    /* successor 2: used for next node or start of node list and as edge backpointer */
  * pred;                     /* pointer to previous node */
  long int atom_number;       /* creation number for this atom: nodes have negative numbers, edges and graphs positive */
}graph_atom; 

#define NEXT_GRAPH succ1
#define NEXT_NODE succ2
#define NEXT_EDGE succ1
#define EDGE_TARGET succ2

/* Global variables */
static graph_atom graph_list = {NULL, NULL, NULL, 0};  /* The list of active graph structures */
static long int graph_atom_count = 1;  /* The number of the next atom to be created */

/* Comparison routines for the first data item in the user data block:
   routines for double length reals, long integers, raw memory and strings.
   It's easy to add new tests by following the routines given here. */
int graph_compare_double(void * left, void * right)
{
  if (*((double *) left)== *((double *) right))
    return 0; 
  else if (*((double *) left)> *((double *) right))
    return 1; 
  else
    return - 1; 
}

int graph_compare_long(void * left, void * right)
{
  if (*((long *) left)== *((long *) right))
    return 0; 
  else if (*((long *) left)> *((long *) right))
    return 1; 
  else
    return - 1; 
}

int graph_compare_mem(void * left, void * right, size_t size)
/* compare size characters of two memory blocks */
{
  char * ll =(char *) left; 
  char * rr =(char *) right; 
  
  while (size-- > 0 && * ll == * rr)
  {
    ll++; 
    rr++; 
  }
  if (* ll == * rr)
    return 0; 
  else if (* ll > * rr)
    return 1; 
  else
    return - 1; 
}

int graph_compare_string(void * left, void * right)
{
  char * left_str = *(char * *) left; 
  char * right_str = *(char * *) right; 
  
  return strcmp(left_str, right_str); 
}

void graph_delete_edge(void * edge)
{
  graph_atom * base =(graph_atom *) edge - 1; 
  
  /* unlink this edge */
  
  if (base->NEXT_EDGE != NULL) /* make next node point back to our predecessor */
    base->NEXT_EDGE->pred = base->pred; 
  
  base->pred->NEXT_EDGE = base->NEXT_EDGE;  /* point predecessor node at our successor */
  
  /* and free the edge's memory */
  mem_free(base); 
}

void graph_delete_graph(void * graph)
{
  graph_atom * base =(graph_atom *) graph - 1; 
  
  /* delete nodes */
  while (base->NEXT_NODE != NULL)
    graph_delete_node(base->NEXT_NODE + 1); 
  
  /* now unlink this graph */
  
  if (base->NEXT_GRAPH != NULL) /* make next node point back to our predecessor */
    base->NEXT_GRAPH->pred = base->pred; 
  
  base->pred->NEXT_GRAPH = base->NEXT_GRAPH;  /* point predecessor node at our successor */
  
  /* and free the graph's memory */
  mem_free(base); 
}

void graph_delete_node(void * node)
{
  graph_atom * base =(graph_atom *) node - 1; 
  
  /* delete edges */
  
  while (base->NEXT_EDGE != NULL)
    graph_delete_edge(base->NEXT_EDGE + 1); 
  
  /* now unlink this node */
  
  if (base->NEXT_NODE != NULL) /* make next node point back to our predecessor */
    base->NEXT_NODE->pred = base->pred; 
  
  base->pred->NEXT_NODE = base->NEXT_NODE;  /* point predecessor node at our successor */
  
  /* and free the node's memory */
  mem_free(base); 
}

unsigned long graph_get_atom_number(const void * graph_or_node_or_edge)
{
  long atom_number =((graph_atom *) graph_or_node_or_edge - 1)->atom_number; 
  
  return atom_number < 0 ? - atom_number: atom_number; 
}

void * graph_get_edge_target(const void * edge)
{
  if (edge == NULL)
    return NULL; 
  else
  {
    graph_atom * temp =((graph_atom *) edge - 1)->EDGE_TARGET; 
    
    return temp == NULL ? temp: temp + 1; 
  }
}

void * graph_get_final_edge(const void * node_or_edge)
{
  graph_atom * temp =(graph_atom *) node_or_edge - 1; 
  
  while (temp->NEXT_EDGE != NULL)
    temp = temp->NEXT_EDGE; 
  
  return temp == NULL ? temp: temp + 1; 
}

void * graph_get_final_node(const void * node_or_edge)
{
  graph_atom * temp =(graph_atom *) node_or_edge - 1; 
  
  while (temp->NEXT_NODE != NULL)
    temp = temp->NEXT_NODE; 
  
  return temp == NULL ? temp: temp + 1; 
}

void * graph_get_next_edge(const void * node_or_edge)
{
  graph_atom * temp =((graph_atom *) node_or_edge - 1)->NEXT_EDGE; 
  
  return temp == NULL ? temp: temp + 1; 
}

void * graph_get_next_node(const void * graph_or_node)
{
  graph_atom * temp =((graph_atom *) graph_or_node - 1)->NEXT_NODE; 
  
  return temp == NULL ? temp: temp + 1; 
}

void * graph_get_previous_edge(const void * node_or_edge)
{
  graph_atom * temp =((graph_atom *) node_or_edge - 1)->pred; 
  
  return temp->pred->NEXT_EDGE != temp ? NULL: temp + 1; 
}

void * graph_get_previous_node(const void * node_or_edge)
{
  graph_atom * temp =((graph_atom *) node_or_edge - 1)->pred; 
  
  return temp->pred->NEXT_NODE != temp ? NULL: temp + 1; 
}

void * graph_insert_edge(size_t size, void * target_node, void * node_or_edge)
{
  graph_atom * base =(graph_atom *) node_or_edge - 1; 
  graph_atom * temp =(graph_atom *) mem_calloc(sizeof(graph_atom)+ size, 1); 
  
  temp->atom_number = graph_atom_count++; 
  
  /* Now insert after node_or_edge */
  temp->NEXT_EDGE = base->NEXT_EDGE;  /* look at rest of list */
  base->NEXT_EDGE = temp;     /* point previous at this node */
  
  temp->pred = base;          /* point backlink at base pointer */
  
  if (temp->NEXT_EDGE != NULL) /* if rest of list is non-null... */
    (temp->NEXT_EDGE)->pred = temp;  /* point next node back at us */
  
  temp->EDGE_TARGET =(graph_atom *) target_node - 1; 
  
  return temp + 1; 
}


void * graph_insert_graph(char * id)
{
  graph_atom * base = & graph_list; 
  graph_atom * temp =(graph_atom *) mem_calloc(sizeof(graph_atom)+ sizeof(char *), 1); 
  
  temp->atom_number = graph_atom_count++; 
  temp->NEXT_NODE = NULL; 
  
  /* Now insert at head of graph_list */
  temp->NEXT_GRAPH = base->NEXT_GRAPH;  /* look at rest of list */
  base->NEXT_GRAPH = temp;    /* point previous at this node */
  
  temp->pred = base;          /* point backlink at base pointer */
  
  if (temp->NEXT_GRAPH != NULL) /* if rest of list is non-null... */
    (temp->NEXT_GRAPH)->pred = temp;  /* point next node back at us */
  
  *((char * *)(++temp))= id; 
  
  return temp; 
}

void * graph_insert_node(size_t size, void * node_or_graph)
{
  graph_atom * base =(graph_atom *) node_or_graph - 1; 
  graph_atom * temp =(graph_atom *) mem_calloc(sizeof(graph_atom)+ size, 1); 
  
  temp->atom_number = -(graph_atom_count++); 
  temp->NEXT_EDGE = NULL; 
  
  /* Now insert after node_or_graph */
  temp->NEXT_NODE = base->NEXT_NODE;  /* look at rest of list */
  base->NEXT_NODE = temp;     /* point previous at this node */
  
  temp->pred = base;          /* point backlink at base pointer */
  
  if (temp->NEXT_NODE != NULL) /* if rest of list is non-null... */
    (temp->NEXT_NODE)->pred = temp;  /* point next node back at us */
  
  return temp + 1; 
}

void * graph_insert_node_child(size_t node_size, size_t edge_size, void * parent_node)
/* make a new node and insert in a graqph, and then add an edge from a source
   node to the new node. Return the node

   This version is a bit kludgy to ensure that RDP adds child nodes at the end
   We should build a tree library instead, I think
*/
{
  void * insert_node, 
  * temp = graph_insert_node(node_size, parent_node); 
  
  insert_node = graph_get_final_edge(parent_node); 
  
  graph_insert_edge(edge_size, temp, insert_node); 
  
  return temp; 
}

void * graph_insert_node_parent(size_t node_size, size_t edge_size, void * child_node)
/* This slightly tricky routine is a sort of dual to graph_insert_node_child.
   The idea is to make a new node that will become the parent of the child node.
   The problem is that anythin pointing to child_node at entry must be left
   pointing at the new parent, so the trick is to reuse the existing child_node.

   1. Make a new node
   2. Copy the contents of child_node into it, so that it becomes a clone.
   3. Make the first edge point back at the clone instead of child_node
   4. Clear the contents of child_node and its edge list.
   5. Add a new edge from child_node to the clone.
*/
{
  graph_atom * child_base, * clone_base; 
  void * clone_node = graph_insert_node(node_size, child_node); 
  
  child_base =(graph_atom *) child_node - 1; 
  clone_base =(graph_atom *) clone_node - 1; 
  
  /* Copy child contents to clone */
  memcpy(clone_node, child_node, node_size); 
  
  /* Link the child's edges to the clone  */
  clone_base->NEXT_EDGE = child_base->NEXT_EDGE; 
  if (clone_base->NEXT_EDGE != NULL)
    (clone_base->NEXT_EDGE)->pred = clone_base; 
  
  memset(child_node, 0, node_size);  /* Clear data fields in child node */
  child_base->NEXT_EDGE = NULL;  /* Clear edge list in child node */
  
  graph_insert_edge(edge_size, clone_node, child_node); 
  
  return child_node; 
}

unsigned graph_hash_double(unsigned hash_prime, void * data)
{                             /* hash a string */
  return(unsigned)((long) hash_prime * *((long *) data)); 
}

unsigned graph_hash_long(unsigned hash_prime, void * data)
{                             /* hash a string */
  return(unsigned)((long) hash_prime * *((long *) data)); 
}

unsigned graph_hash_mem(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = 0; 
  unsigned count = *(unsigned *) data; 
  char * string = *(char * *)((unsigned *) data + 1); 
  
  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    hashnumber = * string++ + hash_prime * hashnumber; 
  return hashnumber; 
}

unsigned graph_hash_string(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = 0; 
  char * str = *(char * *) data; 
  
  if (str != NULL)            /* Don't try and scan a vacuous string */
    while (* str != 0)        /* run up string */
    hashnumber = * str++ + hash_prime * hashnumber; 
  return hashnumber; 
}

static void graph_vcg_graph(graph_atom * curr_graph, 
void(* graph_action)(const void * graph), 
void(* node_action)(const void * node), 
void(* edge_action)(const void * edge)
)
{
  graph_atom * curr_node, 
  * curr_edge; 
  
  if (graph_action != NULL)   /* print a user defined label field */
    graph_action(curr_graph + 1); 
  
  curr_node = curr_graph->NEXT_NODE; 
  
  while (curr_node != NULL)
  {
    text_printf("node:{title:\"%li\"", curr_node->atom_number); 
    
    if (node_action != NULL)  /* print a user defined label field */
      node_action(curr_node + 1); 
    else
      text_printf("label: \"Node:%li\"", - curr_node->atom_number); 
    
    text_printf("}\n"); 
    
    curr_edge = curr_node->NEXT_EDGE; 
    
    while (curr_edge != NULL)
    {
      text_printf("edge:{sourcename:\"%li\" targetname:\"%li\"", curr_node->atom_number, curr_edge->EDGE_TARGET->atom_number); 
      
      if (edge_action != NULL) /* print a user defined label field */
        edge_action(curr_edge + 1); 
/*
      else
        text_printf("label: \"Edge:%li\"", curr_edge->atom_number);
*/
      
      text_printf("}\n"); 
      
      curr_edge = curr_edge->NEXT_EDGE; 
    }
    curr_node = curr_node->NEXT_NODE; 
  }
}

void graph_vcg(void * graph, 
void(* graph_action)(const void * graph), 
void(* node_action)(const void * node), 
void(* edge_action)(const void * edge)
)
{
  text_printf("graph:{\norientation:top_to_bottom"
  "\nedge.arrowsize:7"
  "\nedge.thickness:1"
  "\ndisplay_edge_labels:yes"
  "\narrowmode:free"
  "\nnode.borderwidth:1\n"); 
  
  if (graph == NULL)
  {
    /* dump all graphs */
    graph_atom * curr_graph = graph_list.NEXT_GRAPH; 
    
    while (curr_graph != NULL)
    {
      graph_vcg_graph(curr_graph, graph_action, node_action, edge_action); 
      
      curr_graph = curr_graph->NEXT_GRAPH; 
    }
  }
  else
    /* dump specific graph */
  graph_vcg_graph((graph_atom *) graph - 1, graph_action, node_action, edge_action); 
  
  text_printf("\n}\n"); 
}

static unsigned long graph_vcg_atoms_graph(long unsigned graph_offset, 
graph_atom * curr_graph, 
void(* graph_action)(const void * graph), 
void(* node_action)(const void * node), 
void(* edge_action)(const void * edge)
)
{
  #define xscale 120
  #define yscale 50
  #define width "100"
  
  graph_atom * curr_node, 
  * curr_edge; 
  unsigned node_count = 1; 
  unsigned long max_edges = 0; 
  
  text_printf("\nnode:{loc:{x:1 y:%li} title:\"%li\"", graph_offset, curr_graph->atom_number); 
  
  if (graph_action != NULL)   /* print a user defined label field */
    graph_action(curr_graph + 1); 
  else
    text_printf("label: \"Graph:%li\"", curr_graph->atom_number); 
  text_printf("}\n"); 
  
  curr_node = curr_graph->NEXT_NODE; 
  
  if (curr_node != NULL)
    text_printf("edge:{sourcename:\"%li\" targetname:\"%li\"}\n", curr_graph->atom_number, curr_node->atom_number); 
  
  while (curr_node != NULL)
  {
    unsigned edge_count = 1; 
    
    text_printf("\nnode:{loc:{x:%u y:%u} title:\"%li\"", node_count * xscale, graph_offset, curr_node->atom_number); 
    
    if (node_action != NULL)  /* print a user defined label field */
      node_action(curr_node + 1); 
    else
      text_printf("label: \"Node:%li\"", - curr_node->atom_number); 
    text_printf("}\n"); 
    
    if (curr_node->NEXT_NODE != NULL)
      text_printf("edge:{sourcename:\"%li\" targetname:\"%li\"}\n", curr_node->atom_number, curr_node->NEXT_NODE->atom_number); 
    
    curr_edge = curr_node->NEXT_EDGE; 
    
    if (curr_edge != NULL)
      text_printf("edge:{sourcename:\"%li\" targetname:\"%li\"}\n", curr_node->atom_number, curr_edge->atom_number); 
    
    while (curr_edge != NULL)
    {
      text_printf("\nnode:{loc:{x:%u y:%u} title:\"%li\"", node_count * xscale, edge_count * yscale + graph_offset, curr_edge->atom_number); 
      
      if (edge_action != NULL) /* print a user defined label field */
        edge_action(curr_edge + 1); 
      else
        text_printf("label: \"Edge:%li\"", curr_edge->atom_number); 
      text_printf("}\n"); 
      
      if (curr_edge->NEXT_EDGE != NULL)
        text_printf("edge:{sourcename:\"%li\" targetname:\"%li\"}\n", curr_edge->atom_number, curr_edge->NEXT_EDGE->atom_number); 
      
      text_printf("edge:{backarrowstyle:none sourcename:\"%li\" targetname:\"%li\"}\n", curr_edge->atom_number, curr_edge->EDGE_TARGET->atom_number); 
      
      curr_edge = curr_edge->NEXT_EDGE; 
      
      if (edge_count > max_edges)
        max_edges = edge_count; 
      
      edge_count++; 
    }
    curr_node = curr_node->NEXT_NODE; 
    node_count++; 
  }
  return max_edges * yscale; 
}

void graph_vcg_atoms(void * graph, 
void(* graph_action)(const void * graph), 
void(* node_action)(const void * node), 
void(* edge_action)(const void * edge)
)
{
  long unsigned graph_offset = 0; 
  
  text_printf("graph:{\n"
  "\nport_sharing:yes"
  "\nnode.width:" width
  "\nedge.backarrowsize:7"
  "\nedge.backarrowstyle:solid"
  "\nedge.arrowsize:7"
  "\nedge.thickness:1"
  "\nnode.borderwidth:1\n"); 
  
  if (graph == NULL)
  {
    /* dump all graphs */
    graph_atom * curr_graph = graph_list.NEXT_GRAPH; 
    
    while (curr_graph != NULL)
    {
      graph_offset += graph_vcg_atoms_graph(graph_offset, curr_graph, graph_action, node_action, edge_action); 
      
      if (curr_graph->NEXT_GRAPH != NULL) /* if there is another graph, add an edge */
        text_printf("edge:{ sourcename:\"%li\" targetname:\"%li\"}\n", curr_graph->atom_number, curr_graph->NEXT_GRAPH->atom_number); 
      
      graph_offset += yscale; 
      
      curr_graph = curr_graph->NEXT_GRAPH; 
    }
  }
  else
    /* dump specific graph */
  graph_vcg_atoms_graph(0, (graph_atom *) graph - 1, graph_action, node_action, edge_action); 
  
  text_printf("\n}\n"); 
}

/* End of graph.c */
