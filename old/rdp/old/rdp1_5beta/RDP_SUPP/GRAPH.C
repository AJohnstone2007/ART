/*****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 8 April 1996
*
* graph.c - graph creation and manipulation routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "graph.h"
#include "textio.h"
#include "memalloc.h"
/*****************************************************************************
*
* Graphs are represented using doubly linked lists of `atoms'. Each atom
* contains three pointers in the following order: `succ1' (successor1),
* `succ2' (successor2) and `pred' (predecessor). These three pointers will
* either contain NULL or the adddress of the `succ1' pointer in another atom.
*
* It is most important that this ordering is maintained: the unlink
* operation requires that the `pred' pointer for a node contains the address
* of the `succ1' pointer in the previous node.
*
* Whenever an atom is created, a user data block of `size' bytes may be
* included. User data is stored after the pointers, and all user level
* references are to the base of the user data block so that the internal
* pointers are invisible to the user.
*
* Operations on atoms use the base of the internal pointers, other
* operations use the base of the user data.
*
*****************************************************************************/

/* The basic graph pointer block used by edges, nodes and graph headers */
/* Designed to be part of a doubly linked list with data hooks */
typedef struct graph_atom_struct
{
  struct graph_atom_struct
  *succ1,                   /* successor 1: used for next graph, next edge, or start of edge list */
  *succ2,                  /* successor 2: used for next node or start of node list and as edge backpointer*/
  **pred;                  /* pointer to successor pointer in previous node */
  long int atom_number;	   /* creation number for this atom: negatives used during walks */
} graph_atom;

#define NEXT_GRAPH succ1
#define NEXT_NODE succ2
#define NEXT_EDGE succ1
#define EDGE_TARGET succ2

/* Global variables */
static graph_atom* graph_list = NULL; /* The list of active graph structures */
static long int graph_atom_count = 1; /* The number of the next atom to be created */

/* Comparison routines for the first data item in the user data block:
   routines for double length reals, long integers, raw memory and strings.
   It's easy to add new tests by following the routines given here. */
int graph_compare_double(void *left, void *right)
{
  if (*((double *) left) == *((double *) right))
    return 0;
  else if (*((double *) left) > *((double *) right))
    return 1;
  else
    return -1;
}

int graph_compare_long(void *left, void *right)
{
  if (*((long *) left) == *((long *) right))
    return 0;
  else if (*((long *) left) > *((long *) right))
    return 1;
  else
    return -1;
}

int graph_compare_mem(void *left, void *right, size_t size)
/* compare size characters of two memory blocks */
{
  char *ll = (char*)left;
  char *rr = (char*)right;

  while (size-- >0 && *ll == *rr)
  {
    ll++;
    rr++;
  }
  if (*ll == *rr)
    return 0;
  else if (*ll > *rr)
    return 1;
  else
    return -1;
}

int graph_compare_string(void *left, void *right)
{
  char *left_str = *(char **) left;
  char *right_str = *(char **) right;

  return strcmp(left_str, right_str);
}

void graph_delete_edge(void *edge)
{
  graph_atom *edge_base;
  graph_atom *temp;

  edge_base = (graph_atom*) edge - 1;
  temp = (graph_atom*) (edge_base->pred);            /* point temp at node before this one */

  /* unlink this edge */

  if (temp != NULL)
    temp->NEXT_EDGE = edge_base->NEXT_EDGE; /* point previous node at successor node */

  if (edge_base->NEXT_EDGE != NULL)
    edge_base->NEXT_EDGE->pred = (graph_atom**) temp; /* point successor node back at previous node */

  /* and free the edge's memory */
  mem_free(edge_base);
}

void graph_delete_graph(void *graph)
{
  graph_atom *graph_base;
  graph_atom *temp;

  graph_base = (graph_atom*) graph - 1;
  temp = (graph_atom*) (graph_base->pred);            /* point temp at node before this one */

  /* delete nodes */
  while (graph_base->NEXT_NODE != NULL)
    graph_delete_node(graph_base->NEXT_NODE + 1);

  /* now unlink this graph */

  if (temp != NULL)
    temp->NEXT_GRAPH = graph_base->NEXT_GRAPH; /* point previous node at successor node */

  if (graph_base->NEXT_GRAPH != NULL)
    graph_base->NEXT_GRAPH->pred = (graph_atom**) temp; /* point successor node back at previous node */

  /* and free the graph header's memory */
  mem_free(graph_base);
}

void graph_delete_node(void *node)
{
  graph_atom *node_base;
  graph_atom *temp;

  node_base = (graph_atom*) node - 1;
  temp = (graph_atom*) (node_base->pred);            /* point temp at node before this one */

  /* delete edges */

  while (node_base->NEXT_EDGE != NULL)
    graph_delete_edge(node_base->NEXT_EDGE + 1);

  /* now unlink this node */
  if (temp != NULL)
    temp->NEXT_NODE = node_base->NEXT_NODE; /* point previous node at successor node */

  if (node_base->NEXT_NODE != NULL)
    node_base->NEXT_NODE->pred = (graph_atom**) temp; /* point successor node back at previous node */

  /* and free the node's memory */
  mem_free(node_base);
}

unsigned long graph_get_atom_number(const void *graph_or_node_or_edge)
{
  return ((graph_atom*) graph_or_node_or_edge - 1)->atom_number;
}

void *graph_get_edge_target(const void* edge)
{
  graph_atom *temp = ((graph_atom*) edge - 1)->EDGE_TARGET;

  return  temp == NULL ? temp : temp + 1;
}

void *graph_get_final_edge(const void* node_or_edge)
{
  graph_atom *temp = (graph_atom*) node_or_edge - 1;

  while (temp->NEXT_EDGE != NULL)
    temp  = temp->NEXT_EDGE;

  return  temp == NULL ? temp : temp + 1;
}

void *graph_get_final_node(const void* node_or_edge)
{
  graph_atom *temp = (graph_atom*) node_or_edge - 1;

  while (temp->NEXT_NODE != NULL)
    temp  = temp->NEXT_NODE;

  return  temp == NULL ? temp : temp + 1;
}

void *graph_get_next_edge(const void* node_or_edge)
{
  graph_atom *temp = ((graph_atom*) node_or_edge - 1)->NEXT_EDGE;

  return  temp == NULL ? temp : temp + 1;
}

void *graph_get_next_node(const void* graph_or_node)
{
  graph_atom *temp = ((graph_atom*) graph_or_node - 1)->NEXT_NODE;

  return  temp == NULL ? temp : temp + 1;
}

void *graph_get_previous_edge(const void* node_or_edge)
{
  graph_atom *temp = (graph_atom*) (((graph_atom*) node_or_edge - 1)->pred);

  return  temp == NULL ? temp : temp + 1;
}

void *graph_get_previous_node(const void* node_or_edge)
{
  graph_atom *temp = (graph_atom*) (((graph_atom*) node_or_edge - 1)->pred);

  return  temp == NULL ? temp : temp + 1;
}

void *graph_insert_edge(size_t size, void * target_node, void* node_or_edge)
{
  graph_atom **previous = &((graph_atom*) node_or_edge - 1)->NEXT_EDGE;
  graph_atom *temp = (graph_atom*) mem_calloc(sizeof(graph_atom) + size, 1);

  temp->atom_number = graph_atom_count++;

  /* Now insert after node_or_edge */
  temp->NEXT_EDGE = *previous;		/* look at rest of list */
  *previous = temp; 	        	/* point previous at this node */

  temp->pred = previous;		/* point backlink at base pointer */

  if (temp->NEXT_EDGE != NULL)		/* if rest of list is non-null... */
    (temp->NEXT_EDGE)->pred = &(temp->NEXT_EDGE);	/* point next node back at us */

  temp->EDGE_TARGET = (graph_atom *) target_node - 1 ;

  return temp+1;
}


void *graph_insert_graph(char *id)
{
  graph_atom **previous = &graph_list;
  graph_atom *temp = (graph_atom*) mem_calloc(sizeof(graph_atom) + sizeof(char*), 1);

  temp->atom_number = graph_atom_count++;
  temp->NEXT_NODE = NULL;


  /* Now insert at head of graph_list */
  temp->NEXT_GRAPH = *previous;		/* look at rest of list */
  *previous = temp; 	        	/* point previous at this node */

  temp->pred = previous;		/* point backlink at base pointer */

  if (temp->NEXT_GRAPH != NULL)		/* if rest of list is non-null... */
    (temp->NEXT_GRAPH)->pred = &(temp->NEXT_GRAPH);	/* point next node back at us */

  *((char**) (++temp)) = id;

  return temp;
}

void *graph_insert_node(size_t size, void* node_or_graph)
{
  graph_atom **previous = &((graph_atom*) node_or_graph - 1)->NEXT_NODE;
  graph_atom *temp = (graph_atom*) mem_calloc(sizeof(graph_atom) + size, 1);

  temp->atom_number = graph_atom_count++;
  temp->NEXT_EDGE = NULL;


  /* Now insert after node_or_graph */
  temp->NEXT_NODE = *previous;		/* look at rest of list */
  *previous = temp; 	        	/* point previous at this node */

  temp->pred = previous;		/* point backlink at base pointer */

  if (temp->NEXT_NODE != NULL)		/* if rest of list is non-null... */
    (temp->NEXT_NODE)->pred = &(temp->NEXT_NODE);	/* point next node back at us */

  return temp+1;
}

void *graph_insert_node_child(size_t node_size, size_t edge_size, void* parent_node)
/* make a new node and insert in a graqph, and then add an edge from a source
   node to the new node. Return the node

   This version is a bit kludgy to ensure that RDP adds child nodes at the end
   We should build a tree library instead, I think
*/
{
  void *insert_node, *temp = graph_insert_node(node_size, parent_node);

  insert_node = graph_get_final_edge(parent_node);

  graph_insert_edge(edge_size, temp, insert_node);

  return temp;
}

void *graph_insert_node_parent(size_t node_size, size_t edge_size, void* child_node)
/* make a new node and insert in a graqph, and then add an edge from a source
   node to the new node. Return the node

   This version is a bit kludgy to ensure that RDP adds child nodes at the end
   We should build a tree library instead, I think
*/
{
  void *insert_node, *temp = graph_insert_node(node_size, child_node);

  insert_node = graph_get_final_edge(child_node);

  graph_insert_edge(edge_size, insert_node, temp);

  return temp;
}

unsigned graph_hash_double(unsigned hash_prime, void *data)
{                               /* hash a string */
  return (unsigned) ((long) hash_prime * *((long *) data));
}

unsigned graph_hash_long(unsigned hash_prime, void *data)
{                               /* hash a string */
  return (unsigned) ((long) hash_prime * *((long *) data));
}

unsigned graph_hash_mem(unsigned hash_prime, void *data)
{                               /* hash a string */
  unsigned hashnumber = 0;
  unsigned count = *(unsigned *) data;
  char *string = *(char **) ((unsigned *) data + 1);

  if (string != NULL)           /* Don't try and scan a vacuous string */
    while (count-- > 0)         /* run up string */
      hashnumber = *string++ + hash_prime * hashnumber;
  return hashnumber;
}

unsigned graph_hash_string(unsigned hash_prime, void *data)
{                               /* hash a string */
  unsigned hashnumber = 0;
  char *str = *(char **) data;

  if (str != NULL)              /* Don't try and scan a vacuous string */
    while (*str != 0)           /* run up string */
      hashnumber = *str++ + hash_prime * hashnumber;
  return hashnumber;
}

static void graph_vcg_graph(graph_atom* curr_graph,
			    void (*node_action) (const void *node),
			    void (*edge_action) (const void *edge)
			   )

{
  graph_atom *curr_node, *curr_edge;

  curr_node = curr_graph->NEXT_NODE;

  while(curr_node != NULL)
  {
    text_printf("node:{title:\"%li\"", curr_node->atom_number);

    if (node_action != NULL)   /* print a user defined label field */
      node_action(curr_node+1);

    text_printf("}\n");

    curr_edge = curr_node->NEXT_EDGE;

    while(curr_edge != NULL)
    {
      text_printf("edge:{sourcename:\"%li\" targetname:\"%li\"", curr_node->atom_number, curr_edge->EDGE_TARGET->atom_number);

      if (edge_action != NULL)   /* print a user defined label field */
	edge_action(curr_edge+1);

      text_printf("}\n");

      curr_edge = curr_edge->NEXT_EDGE;
    }
    curr_node = curr_node->NEXT_NODE;
  }
}

void graph_vcg(void *graph,
	       void (*node_action) (const void *node),
	       void (*edge_action) (const void *edge)
	      )
{
  graph_atom* curr_graph = (graph_atom*) graph - 1;

  text_printf("graph:{\norientation:top_to_bottom"
		     "\nedge.arrowsize:7"
		     "\nedge.thickness:1"
		     "\ndisplay_edge_labels:yes"
		     "\narrowmode:free"
		     "\nnode.borderwidth:1\n");

  if (graph == NULL)
  {
    /* dump all graphs */
    curr_graph = graph_list;

    while(curr_graph != NULL)
    {
      graph_vcg_graph(curr_graph, node_action, edge_action);

      curr_graph = curr_graph->NEXT_GRAPH;
    }
  }
  else
    /* dump specific graph */
    graph_vcg_graph(curr_graph, node_action, edge_action);

  text_printf("\n}\n");
}

static void graph_vcg_atoms_graph(graph_atom* curr_graph,
			   void (*node_action) (const void *node),
			   void (*edge_action) (const void *edge)
			  )
{
  graph_atom *curr_node, *curr_edge;
  unsigned node_count = 2;
  unsigned edge_count = 2;

  curr_node = curr_graph->NEXT_NODE;

  while(curr_node != NULL)
  {
    text_printf("\nnode:{horizontal_order:%u title:\"%li\"", node_count, curr_node->atom_number);

    if (node_action != NULL)   /* print a user defined label field */
      node_action(curr_node+1);

    text_printf("}\n");

    if (curr_node->NEXT_NODE != NULL)
      text_printf("nearedge:{linestyle:continuous  sourcename:\"%li\" targetname:\"%li\"}\n", curr_node->atom_number, curr_node->NEXT_NODE->atom_number);

    curr_edge = curr_node->NEXT_EDGE;

    if(curr_edge != NULL)
      text_printf("backedge:{linestyle:continuous sourcename:\"%li\" targetname:\"%li\"}\n", curr_node->atom_number, curr_edge->atom_number);

    while(curr_edge != NULL)
    {
      text_printf("\nnode:{title:\"%li\"", curr_edge->atom_number);

      if (edge_action != NULL)   /* print a user defined label field */
	edge_action(curr_edge+1);

      text_printf("}\n");

      if (curr_edge->NEXT_EDGE != NULL)
	text_printf("backedge:{linestyle:continuous sourcename:\"%li\" targetname:\"%li\"}\n", curr_edge->atom_number, curr_edge->NEXT_EDGE->atom_number);

      text_printf("bentnearedge:{backarrowstyle:none sourcename:\"%li\" targetname:\"%li\"}\n", curr_edge->atom_number, curr_edge->EDGE_TARGET->atom_number);

      text_printf("edge:{linestyle:dotted color: red backarrowstyle:none sourcename:\"%li\" targetname:\"%li\"}\n", curr_node->atom_number, curr_edge->EDGE_TARGET->atom_number);

      curr_edge = curr_edge->NEXT_EDGE;
      edge_count++;
    }
    curr_node = curr_node->NEXT_NODE;
    node_count++;
  }

}

void graph_vcg_atoms(void *graph,
		     void (*node_action) (const void *node),
		     void (*edge_action) (const void *edge)
		    )
{
  graph_atom* curr_graph = (graph_atom*) graph - 1;

  text_printf("graph:{orientation:left_to_right"
		     "\nedge.backarrowsize:7"
		     "\nedge.backarrowstyle:solid"
		     "\nedge.arrowsize:7"
		     "\nedge.thickness:1"
		     "\nedge.linestyle:dotted"
		     "\nnode.borderwidth:1\n");

  if (graph == NULL)
  {
    /* dump all graphs */
    curr_graph = graph_list;

    while(curr_graph != NULL)
    {
      graph_vcg_atoms_graph(curr_graph, node_action, edge_action);

      if (curr_graph->NEXT_GRAPH != NULL)  /* if there is another graph, add an edge */
	text_printf("edge:{ sourcename:\"%li\" targetname:\"%li\"}\n", curr_graph->atom_number, curr_graph->NEXT_GRAPH->atom_number);

      curr_graph = curr_graph->NEXT_GRAPH;
    }
  }
  else
    /* dump specific graph */
    graph_vcg_atoms_graph(curr_graph, node_action, edge_action);

  text_printf("\n}\n");
}

/* End of graph.c */
