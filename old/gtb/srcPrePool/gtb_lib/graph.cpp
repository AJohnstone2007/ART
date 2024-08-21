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
#include "set.h"
#include "graph.h"
#include "textio.h"
#include "memalloc.h"
#include "util.h"
//#define GTB

#if defined(GTB)
#include "gtb_gdg.h"
#endif

/* #define DEBUG */
//#define STRONG_TRACE

typedef struct graph_graph_struct
{
  void **node_index;
  void **edge_index;
  void * root;
  struct graph_graph_struct * next_graph;
  struct graph_node_struct * next_node;
  struct graph_graph_struct * previous_graph;
  GRAPH_ATOM_NUMBER_T atom_number;
}graph_graph;

typedef struct graph_node_struct
{
  void *unused1;
  void *unused2;
  struct graph_edge_struct * next_in_edge;
  struct graph_edge_struct * next_out_edge;
  struct graph_node_struct * next_node;     /* or previous_in edge */
  struct graph_node_struct * previous_node; /* or previous out_edge */
  GRAPH_ATOM_NUMBER_T atom_number;
}graph_node;

typedef struct graph_edge_struct
{
  struct graph_node_struct * destination;
  struct graph_node_struct * source;
  struct graph_edge_struct * next_in_edge;
  struct graph_edge_struct * next_out_edge;
  struct graph_edge_struct * previous_in_edge;
  struct graph_edge_struct * previous_out_edge;
  GRAPH_ATOM_NUMBER_T atom_number;
}graph_edge;

/* Global variables */
static graph_graph graph_list = {NULL, NULL, NULL, 0};  /* The list of active graph structures */
static GRAPH_ATOM_NUMBER_T graph_next_graph_count = 1;  /* The number of the next graph to be created */
static GRAPH_ATOM_NUMBER_T graph_next_node_count = 1;  /* The number of the next node to be created */
static GRAPH_ATOM_NUMBER_T graph_next_edge_count = 1;  /* The number of the next edge to be created */

void graph_set_next_node_count(unsigned count)
{
  graph_next_node_count = count;
}

void graph_vcg_diagnostic_edge(const void *edge)
{
  text_printf("label: \"" GRAPH_ATOM_NUMBER_F "(" GRAPH_ATOM_NUMBER_F "->" GRAPH_ATOM_NUMBER_F ")\"",
              graph_atom_number(edge),
              graph_atom_number(graph_source(edge)),
              graph_atom_number(graph_destination(edge)));
}

void graph_vcg_diagnostic_node(const void *node)
{
  text_printf("label: \"" GRAPH_ATOM_NUMBER_F "\"",
              graph_atom_number(node));
}

void graph_vcg_diagnostic(void *graph)
{
  graph_node * curr_node;
  graph_edge * curr_edge;
  graph_graph *curr_graph = (graph_graph *) graph - 1;

  curr_node = curr_graph->next_node;

  text_printf("graph:{\norientation:top_to_bottom"
  "\nedge.arrowsize:7"
  "\nedge.thickness:1"
  "\ndisplay_edge_labels:yes"
  "\narrowmode:free"
  "\nnode.borderwidth:1\n");

  while (curr_node != NULL)
  {
    text_printf("node:{title:\"" GRAPH_ATOM_NUMBER_F "\"", curr_node->atom_number);

    graph_vcg_diagnostic_node(curr_node + 1);

    text_printf("}\n");

    curr_edge = curr_node->next_out_edge;

    while (curr_edge != NULL)
    {
      text_printf("edge:{color:blue sourcename:\"" GRAPH_ATOM_NUMBER_F "\" targetname:\"" GRAPH_ATOM_NUMBER_F "\"", curr_node->atom_number, curr_edge->destination->atom_number);

      graph_vcg_diagnostic_edge(curr_edge + 1);

      text_printf("}\n");

      curr_edge = curr_edge->next_out_edge;
    }

    curr_edge = curr_node->next_in_edge;

    while (curr_edge != NULL)
    {
      text_printf("edge:{color:red sourcename:\"" GRAPH_ATOM_NUMBER_F "\" targetname:\"" GRAPH_ATOM_NUMBER_F "\"", curr_edge->source->atom_number, curr_node->atom_number);

      graph_vcg_diagnostic_edge(curr_edge + 1);

      text_printf("}\n");

      curr_edge = curr_edge->next_in_edge;
    }

    curr_node = curr_node->next_node;
  }

  text_printf("\n}\n");
}

static void graph_vcg_graph(graph_graph * curr_graph,
void(* graph_action)(const void * graph),
void(* node_action)(const void * node),
void(* edge_action)(const void * edge)
)
{
  graph_node * curr_node;
  graph_edge * curr_edge;

  if (graph_action != NULL)   /* print a user defined label field */
    graph_action(curr_graph + 1);

  curr_node = curr_graph->next_node;

  while (curr_node != NULL)
  {
    text_printf("node:{title:\"" GRAPH_ATOM_NUMBER_F "\"", curr_node->atom_number);

    if (node_action != NULL)  /* print a user defined label field */
      node_action(curr_node + 1);
    else
      text_printf("label: \"Node:" GRAPH_ATOM_NUMBER_F "\"", curr_node->atom_number);

    text_printf("}\n");

    curr_edge = curr_node->next_out_edge;

    while (curr_edge != NULL)
    {
      text_printf("edge:{sourcename:\"" GRAPH_ATOM_NUMBER_F "\" targetname:\"" GRAPH_ATOM_NUMBER_F "\"", curr_node->atom_number, curr_edge->destination->atom_number);

      if (edge_action != NULL) /* print a user defined label field */
        edge_action(curr_edge + 1);
#ifdef DEBUG
      else
        graph_vcg_diagnostic_edge(curr_edge + 1);
#endif
      text_printf("}\n");

      curr_edge = curr_edge->next_out_edge;
    }
    curr_node = curr_node->next_node;
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
    graph_graph * curr_graph = graph_list.next_graph;

    while (curr_graph != NULL)
    {
      graph_vcg_graph(curr_graph, graph_action, node_action, edge_action);

      curr_graph = curr_graph->next_graph;
    }
  }
  else
    /* dump specific graph */
  graph_vcg_graph((graph_graph *) graph - 1, graph_action, node_action, edge_action);

  text_printf("\n}\n");
}

void * graph_insert_graph(char * id)
{
  graph_graph * base = & graph_list;
  graph_graph * temp =(graph_graph *) mem_calloc(sizeof(graph_graph)+ sizeof(char *), 1);

  temp->atom_number = graph_next_graph_count++;
  temp->next_node = NULL;
  graph_next_node_count = graph_next_edge_count = 1;

  /* Now insert at destination of graph_list */
  temp->next_graph = base->next_graph;  /* look at rest of list */
  base->next_graph = temp;    /* point previous at this node */

  temp->previous_graph = base;          /* point backlink at base pointer */

  if (temp->next_graph != NULL) /* if rest of list is non-null... */
    (temp->next_graph)->previous_graph = temp;  /* point next node back at us */

  *((char * *)(++temp))= id;

  return temp;
}

void * graph_insert_node(size_t size, void * node_or_graph)
{
  graph_node * base =(graph_node *) node_or_graph - 1;
  graph_node * temp =(graph_node *) mem_calloc(sizeof(graph_node)+ size, 1);

  temp->atom_number = graph_next_node_count++;
  temp->next_out_edge = NULL;

  /* Now insert after node_or_graph */
  temp->next_node = base->next_node;  /* look at rest of list */
  base->next_node = temp;     /* point previous at this node */

  temp->previous_node = base;          /* point backlink at base pointer */

  if (temp->next_node != NULL) /* if rest of list is non-null... */
    (temp->next_node)->previous_node = temp;  /* point next node back at us */

  return temp + 1;
}

void * graph_insert_edge(size_t size, void * destination_node, void * source_node)
{
  graph_node * source_node_base =(graph_node *) source_node - 1;
  graph_node * destination_node_base =(graph_node *) destination_node - 1;
  graph_edge * temp =(graph_edge *) mem_calloc(sizeof(graph_edge)+ size, 1);

  temp->atom_number = graph_next_edge_count++;

  /* source and out-edge processing */
  temp->next_out_edge = source_node_base->next_out_edge;  /* look at rest of list */
  source_node_base->next_out_edge = temp;     /* point previous at this node */

  temp->previous_out_edge = (graph_edge *) source_node - 1;  /* point backlink at source_base pointer */

  if (temp->next_out_edge != NULL) /* if rest of list is non-null... */
    (temp->next_out_edge)->previous_out_edge = temp;  /* point next node back at us */

  temp->source = source_node_base;

  /* destination and in-edge processing */
  temp->next_in_edge = destination_node_base->next_in_edge;  /* look at rest of list */
  destination_node_base->next_in_edge = temp;     /* point previous at this node */

  temp->previous_in_edge = (graph_edge *) destination_node - 1;  /* point backlink at destination_base pointer */

  if (temp->next_in_edge != NULL) /* if rest of list is non-null... */
    (temp->next_in_edge)->previous_in_edge = temp;  /* point next node back at us */

  temp->destination = destination_node_base;

  return temp + 1;
}

void * graph_insert_edge_after_final(size_t size, void * destination_node, void * source_node)
{
  graph_node * source_node_base =(graph_node *) source_node - 1;
  graph_node * destination_node_base =(graph_node *) destination_node - 1;
  graph_edge *temp_edge;
  graph_edge * temp =(graph_edge *) mem_calloc(sizeof(graph_edge)+ size, 1);

  temp->atom_number = graph_next_edge_count++;

  /* source and out-edge processing */
  for (temp_edge = (graph_edge*) source_node - 1;
       temp_edge->next_out_edge != NULL;
       temp_edge = temp_edge->next_out_edge)
    ;

  // Now, temp_edge->next_out_edge is NULL, so just tack us on at the end
  temp_edge->next_out_edge = temp;     /* point previous at this node */
  temp->previous_out_edge = temp_edge;  /* point backlink at source_base pointer */

  temp->source = source_node_base;

  /* destination and in-edge processing */
  for (temp_edge = (graph_edge*) destination_node - 1;
       temp_edge->next_in_edge != NULL;
       temp_edge = temp_edge->next_in_edge)
    ;

  // Now, temp_edge->next_in_edge is NULL, so just tack us on at the end
  temp_edge->next_in_edge = temp;     /* point previous at this node */
  temp->previous_in_edge = temp_edge;  /* point backlink at destination_base pointer */

  temp->destination = destination_node_base;

  return temp + 1;
}

void * graph_insert_edge_after_out_edge(size_t size, void * destination_node, void * predecessor_edge)
{
  graph_node * source_node_base = (graph_node *) graph_source(predecessor_edge) - 1;
  graph_node * destination_node_base = (graph_node *) destination_node - 1;
  graph_edge * predecessor_edge_base = (graph_edge *) predecessor_edge - 1;

  graph_edge * temp =(graph_edge *) mem_calloc(sizeof(graph_edge)+ size, 1);
  temp->atom_number = graph_next_edge_count++;

  /* source and out-edge processing - does inline insertion */
  temp->next_out_edge = predecessor_edge_base->next_out_edge;  /* look at rest of list */
  predecessor_edge_base->next_out_edge = temp;     /* point previous at this node */

  temp->previous_out_edge = predecessor_edge_base;

  if (temp->next_out_edge != NULL) /* if rest of list is non-null... */
    (temp->next_out_edge)->previous_out_edge = temp;  /* point next node back at us */

  temp->source = source_node_base;

  /* destination and in-edge processing */
  temp->next_in_edge = destination_node_base->next_in_edge;  /* look at rest of list */
  destination_node_base->next_in_edge = temp;     /* point previous at this node */

  temp->previous_in_edge = (graph_edge *) destination_node - 1;  /* point backlink at destination_base pointer */

  if (temp->next_in_edge != NULL) /* if rest of list is non-null... */
    (temp->next_in_edge)->previous_in_edge = temp;  /* point next node back at us */

  temp->destination = destination_node_base;

  return temp + 1;
}

void * graph_insert_node_child(size_t node_size, size_t edge_size, void * parent_node)
/* make a new node and insert in a graph, and then add an edge from a source
   node to the new node. Return the new edge.
*/
{
  void * temp = graph_insert_node(node_size, parent_node);

  graph_insert_edge(edge_size, temp, parent_node);

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
  graph_node * child_base, * clone_base;
  graph_edge * this_edge;
  void * clone_node = graph_insert_node(node_size, child_node);

  child_base =(graph_node *) child_node - 1;
  clone_base =(graph_node *) clone_node - 1;

  /* Copy child contents to clone */
  memcpy(clone_node, child_node, node_size);

  /* Link the child's out edges to the clone  */
  clone_base->next_out_edge = child_base->next_out_edge;
  if (clone_base->next_out_edge != NULL)
  {
    (clone_base->next_out_edge)->previous_out_edge = (graph_edge*) clone_base;

    for (this_edge = clone_base->next_out_edge; this_edge != NULL; this_edge = this_edge->next_out_edge)
      this_edge->source = clone_base;
  }


  memset(child_node, 0, node_size);  /* Clear data fields in child node */
  child_base->next_out_edge = NULL;  /* Clear edge list in child node */

  graph_insert_edge(edge_size, clone_node, child_node);

  return child_node;
}

void * graph_clear_graph(void * graph)
{
  graph_graph * base =(graph_graph *) graph - 1;

  /* delete indices */
  if (base->node_index)
    mem_free(base->node_index);
  if (base->edge_index)
    mem_free(base->edge_index);

  /* delete nodes */
  while (base->next_node != NULL)
    graph_delete_node(base->next_node + 1);

  return graph;
}

void * graph_delete_graph(void * graph)
{
  graph_graph * base =(graph_graph *) graph - 1, *return_value;

  /* delete indices */
  if (base->node_index)
    mem_free(base->node_index);
  if (base->edge_index)
    mem_free(base->edge_index);

  /* delete nodes */
  while (base->next_node != NULL)
    graph_delete_node(base->next_node + 1);

  /* now unlink this graph */

  if (base->next_graph != NULL) /* make next node point back to our in */
    base->next_graph->previous_graph = base->previous_graph;

  return_value = base->previous_graph + 1;

  base->previous_graph->next_graph = base->next_graph;  /* point in node at our out */

  /* and free the graph's memory */
  mem_free(base);

  return return_value;
}

void * graph_delete_node(void * node)
{
  graph_node * base =(graph_node *) node - 1, *return_value;

  /* delete out edges */
  while (base->next_out_edge != NULL)
    graph_delete_edge(base->next_out_edge + 1);

  /* delete in edges */
  while (base->next_in_edge != NULL)
    graph_delete_edge(base->next_in_edge + 1);

  /* now unlink this node */
  if (base->next_node != NULL) /* make next node point back to our in */
    base->next_node->previous_node = base->previous_node;

  return_value = base->previous_node + 1;

  base->previous_node->next_node = base->next_node;  /* point in node at our out */

  /* and free the node's memory */
  mem_free(base);

  return return_value;
}

#if 0
void * graph_delete_only_node(void * node)
{
  graph_node * base =(graph_node *) node - 1, *return_value;

  /* now unlink this node */
  if (base->next_node != NULL) /* make next node point back to our in */
    base->next_node->previous_node = base->previous_node;

  return_value = base->previous_node + 1;

  base->previous_node->next_node = base->next_node;  /* point in node at our out */

  /* and free the node's memory */
  mem_free(base);

  return return_value;
}
#endif

void * graph_delete_edge(void * edge)
{
  graph_edge * base =(graph_edge *) edge - 1, *return_value;

  /* unlink this edge from the out list */
  if (base->next_out_edge != NULL) /* make next node point back to our in */
    base->next_out_edge->previous_out_edge = base->previous_out_edge;

  return_value = base->previous_out_edge + 1;

  base->previous_out_edge->next_out_edge = base->next_out_edge;  /* point in node at our out */

  /* unlink this edge from the in list */
  if (base->next_in_edge != NULL) /* make next node point back to our in */
    base->next_in_edge->previous_in_edge = base->previous_in_edge;

  base->previous_in_edge->next_in_edge = base->next_in_edge;  /* point in node at our out */

  /* and free the edge's memory */
  mem_free(base);

  return return_value;
}

void graph_delete_set_of_edges(void* graph, set_ *edges_to_delete)
{
  unsigned *edges_to_delete_numbers = set_array(edges_to_delete);

  graph_create_edge_index(graph);

  for (unsigned *count = edges_to_delete_numbers; *count < SET_END; count++)
    graph_delete_edge(graph_edge_index(graph)[*count]);

  mem_free(edges_to_delete_numbers);

  graph_create_edge_index(graph);
}

void graph_retain_set_of_nodes(void* graph, set_ *nodes_to_retain)
{
  void *this_node = graph_next_node(graph);

  while (this_node != NULL)
  {
    void *del_node = this_node;

    this_node = graph_next_node(this_node);

    if (!set_includes_element(nodes_to_retain, graph_atom_number(del_node)))
      graph_delete_node(del_node);
  }
}

void * graph_initial_node(const void * node_or_edge)
{
  graph_node * temp =(graph_node *) node_or_edge - 1;

  while (temp->previous_node->next_node != temp)
    temp = temp->previous_node;

  return temp + 1;
}

void * graph_final_node(const void * node_or_edge)
{
  graph_node * temp =(graph_node *) node_or_edge - 1;

  if (temp->next_node == NULL)
    return NULL;

  while (temp->next_node != NULL)
    temp = temp->next_node;

  return temp + 1;
}

void * graph_next_node(const void * graph_or_node)
{
  graph_node * temp =((graph_node *) graph_or_node - 1)->next_node;

  return temp == NULL ? temp: temp + 1;
}

void * graph_previous_node(const void * node)
{
  graph_node * temp =((graph_node *) node - 1)->previous_node;

  return temp->previous_node->next_node != temp ? NULL: temp + 1;
}

void * graph_initial_out_edge(const void * node_or_edge)
{
  graph_edge * temp =(graph_edge *) node_or_edge - 1;

  while (temp->previous_out_edge->next_out_edge != temp)
    temp = temp->previous_out_edge;

  return temp + 1;
}


void * graph_final_out_edge(const void * node_or_edge)
{
  graph_edge * temp =(graph_edge *) node_or_edge - 1;

  if (temp->next_out_edge == NULL)
    return NULL;

  while (temp->next_out_edge != NULL)
    temp = temp->next_out_edge;

  return temp + 1;
}

void * graph_next_out_edge(const void * node_or_edge)
{
  graph_edge * temp =((graph_edge *) node_or_edge - 1)->next_out_edge;

  return temp == NULL ? temp: temp + 1;
}

void * graph_previous_out_edge(const void * node_or_edge)
{
  graph_edge * temp =((graph_edge *) node_or_edge - 1)->previous_out_edge;

  return temp->previous_out_edge->next_out_edge != temp ? NULL: temp + 1;
}

void * graph_initial_in_edge(const void * node_or_edge)
{
  graph_edge * temp =(graph_edge *) node_or_edge - 1;

  while (temp->previous_in_edge->next_in_edge != temp)
    temp = temp->previous_in_edge;

  return temp + 1;
}


void * graph_final_in_edge(const void * node_or_edge)
{
  graph_edge * temp =(graph_edge *) node_or_edge - 1;

  if (temp->next_in_edge == NULL)
    return NULL;

  while (temp->next_in_edge != NULL)
    temp = temp->next_in_edge;

  return temp + 1;
}

void * graph_next_in_edge(const void * node_or_edge)
{

  graph_edge * temp =((graph_edge *) node_or_edge - 1)->next_in_edge;

  return temp == NULL ? temp: temp + 1;
}

void * graph_previous_in_edge(const void * node_or_edge)
{
  graph_edge * temp =((graph_edge *) node_or_edge - 1)->previous_in_edge;

  return temp->previous_in_edge->next_in_edge != temp ? NULL: temp + 1;
}

void graph_set_root(const void * graph, void * root)
{
  ((graph_graph *) graph - 1)->root = root;
}

void * graph_root(const void * graph)
{
  return ((graph_graph *) graph - 1)->root;
}

void * graph_destination(const void * edge)
{
  if (edge == NULL)
    return NULL;
  else
  {
    graph_node * temp =((graph_edge *) edge - 1)->destination;

    return temp == NULL ? temp: temp + 1;
  }
}

void * graph_source(const void * edge)
{
  if (edge == NULL)
    return NULL;
  else
  {
    graph_node * temp =((graph_edge *) edge - 1)->source;

    return temp == NULL ? temp: temp + 1;
  }
}

GRAPH_ATOM_NUMBER_T graph_atom_number(const void * graph_or_node_or_edge)
{
  if (graph_or_node_or_edge == NULL)
    return 0;
  else
    return ((graph_graph *) graph_or_node_or_edge - 1)->atom_number;
}

void graph_epsilon_prune_rdp_tree(void *parent_node, size_t rdp_edge_size)
{
  struct deletable_node_list_struct{ void* node; struct deletable_node_list_struct *next;} *base = NULL, *temp;

  void *this_parent_out_edge;

  if (parent_node == NULL)
    return;

  for (this_parent_out_edge = graph_next_out_edge(parent_node);
       this_parent_out_edge != NULL;
       this_parent_out_edge = graph_next_out_edge(this_parent_out_edge))
  {
    void *child_node = graph_destination(this_parent_out_edge);

    graph_epsilon_prune_rdp_tree(child_node, rdp_edge_size);
    if (*((char**)child_node) == 0)
    {
      void *this_child_out_edge, *final_child_out_edge = NULL;

      /* Move child's out edges up */
      graph_node *parent_base = (graph_node*)parent_node - 1;

      /* Run through child edges changing their source to parent */
      for (this_child_out_edge = graph_next_out_edge(child_node);
           this_child_out_edge != NULL;
           this_child_out_edge = graph_next_out_edge(this_child_out_edge))
      {
        graph_edge *edge_base = (graph_edge*)this_child_out_edge - 1;
        edge_base->source = parent_base;
        final_child_out_edge = this_child_out_edge;
      }

      /* Move complete run of edges up to before this_parent_out_edge */
      if (final_child_out_edge != NULL)   /* skip if there are no children */
      {
        void *initial_child_out_edge = graph_next_out_edge(child_node);
        graph_edge *initial_child_out_edge_base = (graph_edge*)initial_child_out_edge - 1;
        graph_edge *final_child_out_edge_base = (graph_edge*)final_child_out_edge - 1;
        graph_edge *parent_out_edge_base = (graph_edge*)this_parent_out_edge - 1;
        graph_node *child_node_base = (graph_node*)child_node - 1;

        parent_out_edge_base->previous_out_edge->next_out_edge = initial_child_out_edge_base;
        initial_child_out_edge_base->previous_out_edge = parent_out_edge_base->previous_out_edge;

        final_child_out_edge_base->next_out_edge = parent_out_edge_base;
        parent_out_edge_base->previous_out_edge = final_child_out_edge_base;

        /* Set the child's out list to empty */
        child_node_base->next_out_edge = NULL;
      }

      /* Add node to list of deletables */
      temp = (struct deletable_node_list_struct*) mem_malloc(sizeof(struct deletable_node_list_struct));
      temp->node = child_node;
      temp->next = base;
      base = temp;
    }
  }

  /* Now do the actual deletions */
  while (base!=NULL)
  {
    graph_delete_node(base->node);
    temp = base;
    base = base->next;
    mem_free(temp);
  }
}

static void graph_edge_consistency(void *edge)
{
  graph_edge *edge_base = (graph_edge*) edge - 1;

  if (edge_base->previous_in_edge->next_in_edge != edge_base)
    text_message(TEXT_ERROR, "Edge %lu has inconsistent previous in-edge pointer\n", graph_atom_number(edge));

  if (edge_base->previous_out_edge->next_out_edge != edge_base)
    text_message(TEXT_ERROR, "Edge %lu has inconsistent previous out-edge pointer\n", graph_atom_number(edge));

  if (edge_base->next_in_edge != NULL)
    if (edge_base->next_in_edge->previous_in_edge != edge_base)
      text_message(TEXT_ERROR, "Edge %lu has inconsistent next in-edge pointer\n", graph_atom_number(edge));

  if (edge_base->next_out_edge != NULL)
    if (edge_base->next_out_edge->previous_out_edge != edge_base)
      text_message(TEXT_ERROR, "Edge %lu has inconsistent next out-edge pointer\n", graph_atom_number(edge));
}

void graph_print_consistency(void *graph)
{
  void *this_node;
  set_ nodes = SET_NULL, edges = SET_NULL;

  text_printf("Consistency report for graph: %li\nNodes:\n", graph_atom_number(graph));

  for (this_node = graph_next_node(graph); this_node != NULL; this_node = graph_next_node(this_node))
  {
    void *this_edge;

    set_unite_element(&nodes, graph_atom_number(this_node));

    text_printf("\nNode: %lu\n", graph_atom_number(this_node));
    text_printf("  in-edges:");
    for (this_edge = graph_next_in_edge(this_node); this_edge != NULL; this_edge = graph_next_in_edge(this_edge))
    {
      text_printf(" %lu", graph_atom_number(this_edge));
      set_unite_element(&edges, graph_atom_number(this_edge));
    }

    text_printf("\n  out-edges:");
    for (this_edge = graph_next_out_edge(this_node); this_edge != NULL; this_edge = graph_next_out_edge(this_edge))
    {
      text_printf(" %lu", graph_atom_number(this_edge));
      set_unite_element(&edges, graph_atom_number(this_edge));
    }
  }

  set_print_set(&nodes, NULL, 75);
  text_printf("\nEdges:\n");
  set_print_set(&edges, NULL, 75);
  text_printf("\n");

  /* Now check internal consistency of edges, and that both ends of an edge are in our node set */
  for (this_node = graph_next_node(graph); this_node != NULL; this_node = graph_next_node(this_node))
  {
    void *this_edge;

    for (this_edge = graph_next_in_edge(this_node); this_edge != NULL; this_edge = graph_next_in_edge(this_edge))
    {
      graph_edge_consistency(this_edge);
      if (!set_includes_element(&nodes, graph_atom_number(graph_source(this_edge))))
      {
        text_printf("\n");
        text_message(TEXT_ERROR, "Node %li in-edge %li has source node %li which is not in node set\n",
                     graph_atom_number(this_node),
                     graph_atom_number(this_edge),
                     graph_atom_number(graph_source(this_edge)));
      }
    }
  }
}

GRAPH_ATOM_NUMBER_T graph_max_node_number(void *graph)
{
  void *this_node;
  unsigned max_node_number = 0;
  unsigned this_node_number;

  GRAPH_ITERATE_NODE(graph, this_node, void)
    if ((this_node_number = graph_atom_number(this_node)) > max_node_number)
      max_node_number = this_node_number;

  return max_node_number;
}

GRAPH_ATOM_NUMBER_T graph_max_edge_number(void *graph)
{
  void *this_node;
  void *this_edge;
  unsigned max_edge_number = 0;
  unsigned this_edge_number;

  GRAPH_ITERATE_NODE(graph, this_node, void)
    GRAPH_ITERATE_OUT_EDGE(this_node, this_edge, void)
      if ((this_edge_number = graph_atom_number(this_edge)) > max_edge_number)
        max_edge_number = this_edge_number;

  return max_edge_number;
}

void graph_create_node_index(void *graph)
{
  graph_graph * base =(graph_graph *) graph - 1;
  void *this_node;

  if (base->node_index != NULL)
    mem_free(base->node_index);
  base->node_index = (void**) mem_calloc(graph_max_node_number(graph) + 2, sizeof (void*));

  GRAPH_ITERATE_NODE(graph, this_node, void)
    base->node_index[graph_atom_number(this_node)] = this_node;
}

void graph_create_edge_index(void *graph)
{
  graph_graph * base =(graph_graph *) graph - 1;
  void *this_node;
  void *this_edge;

  if (base->edge_index != NULL)
    mem_free(base->edge_index);
  base->edge_index = (void**) mem_calloc(graph_max_edge_number(graph) + 2, sizeof (void*));

  GRAPH_ITERATE_NODE(graph, this_node, void)
    GRAPH_ITERATE_OUT_EDGE(this_node, this_edge, void)
      base->edge_index[graph_atom_number(this_edge)] = this_edge;
}

void **graph_node_index(void *graph)
{
  void** node_index = (void**) (((graph_graph*) graph - 1)->node_index);

  if (node_index == NULL)
    text_message(TEXT_FATAL, "attempt to use uninitialised graph node index\n");

  return node_index;
}

void **graph_edge_index(void *graph)
{
  void ** edge_index = (void**) (((graph_graph*) graph - 1)->edge_index);

  if (edge_index == NULL)
    text_message(TEXT_FATAL, "attempt to use uninitialised graph edge index\n");

  return edge_index;
}

static void graph_level_rec(GRAPH_ATOM_NUMBER_T *levels, GRAPH_ATOM_NUMBER_T this_level, void *this_node, set_*visited, int deep)
{
  GRAPH_ATOM_NUMBER_T this_atom_number = graph_atom_number(this_node);
  void *this_edge;

#if 0
  int dummy;
  for (dummy = 0; dummy<this_level; dummy++)
    text_printf(" ");

  text_printf("Atom " GRAPH_ATOM_NUMBER_F ", level " GRAPH_ATOM_NUMBER_F "... ", this_atom_number, this_level);

  text_printf("visited: {");
  set_print_set(visited, NULL, 0);
  text_printf("} ");
#endif
  if (!set_includes_element(visited, this_atom_number))
  {
    if (this_level > levels[this_atom_number])
    {
      levels[this_atom_number] = this_level;
#if 0
      text_printf("visiting\n");
#endif
      set_unite_element(visited, this_atom_number);

      GRAPH_ITERATE_OUT_EDGE(this_node, this_edge, void)
        graph_level_rec(levels, this_level + 1, graph_destination(this_edge), visited, deep);

      if (deep)
        set_difference_element(visited, this_atom_number);
    }
  }
#if 0
  else
    text_printf("already visited\n");
#endif
}

GRAPH_ATOM_NUMBER_T *graph_level(void *graph, int deep)
{
  unsigned max_node_number = graph_max_node_number(graph);
  GRAPH_ATOM_NUMBER_T *levels;
  void *root = graph_root(graph);
  set_ visited = SET_NULL;

  if (root == NULL)
    text_message(TEXT_FATAL, "call to graph_level() with non-rooted graph\n");

  levels = (GRAPH_ATOM_NUMBER_T *) mem_calloc(max_node_number + 2000, sizeof (GRAPH_ATOM_NUMBER_T));
  set_unite_element(&visited, max_node_number);  /* Force visited set to full length to mininise realloc activity */
  set_clear(&visited);

  graph_level_rec(levels, 1, root, &visited, deep);

  set_free(&visited);

  return levels;
}

void graph_set_atom_number(void *atom, GRAPH_ATOM_NUMBER_T number)
{
  if (atom != NULL)
    ((graph_graph *) atom - 1)->atom_number = number;
}

static void *scc_node_table;
static unsigned recursion_limit;
static unsigned scc_count;
static unsigned *return_prenum;
static unsigned *return_postnum;
static unsigned *prenum;
static unsigned *postnum;
static unsigned *lowlink;
static unsigned *stack;
static unsigned *onstack;

static unsigned *sp;
static unsigned current_prenum;
static unsigned current_postnum;
static int current_strong;
static int current_single_strong;

static unsigned graph_max_node_num;
static unsigned graph_max_edge_num;

/* Tarjan's strongly connected component algorithm */
static void graph_create_pre_and_post_numbers_rec(void *node, set_ *valid_edges, int *strong)
{
  unsigned atom_number = graph_atom_number(node);

#if defined STRONG_TRACE
  text_printf("Strong component: entered node %u\n", atom_number);
#endif

  prenum[atom_number] = current_prenum++;
  lowlink[atom_number] = prenum[atom_number];
  *++sp = atom_number;
  onstack[atom_number] = 1;

  for (void *this_out_edge = graph_next_out_edge(node); this_out_edge != NULL; this_out_edge = graph_next_out_edge(this_out_edge))
  {
    unsigned edge_number = graph_atom_number(this_out_edge);
    if (set_includes_element(valid_edges, edge_number))  // Don't recurse through invisible edges
    {
      void *successor_node = graph_destination(this_out_edge);
      unsigned successor_atom_number = graph_atom_number(successor_node);

      if (prenum[successor_atom_number] == 0)
      {
        graph_create_pre_and_post_numbers_rec(successor_node, valid_edges, strong);
        lowlink[atom_number] = min(lowlink[atom_number], lowlink[successor_atom_number]);
      }
      else
      {
        if (onstack[successor_atom_number])
          lowlink[atom_number] = min(lowlink[atom_number], prenum[successor_atom_number]);
      }
    }
  }

  if (lowlink[atom_number] == prenum[atom_number])
  {
    unsigned temp;

#if defined STRONG_TRACE
    text_printf("New strong component\n");
#endif

    if (*sp == atom_number)
    {
      strong[*sp] = current_single_strong--;
      onstack[*sp] = 0;
#if defined STRONG_TRACE
      text_printf("Soliton %u\n", *sp);
#endif
      sp--;
    }
    else
    {
      do
      {
        temp = *sp--;
        onstack[temp] = 0;
        strong[temp] = current_strong;
#if defined STRONG_TRACE
        text_printf("%u\n", temp);
#endif
      }
      while (temp != atom_number);

      current_strong++;
    }
  }
  postnum[atom_number] = current_postnum++;

#if defined STRONG_TRACE
  text_printf("Strong component: exited node %u\n", atom_number);
#endif
}

int * graph_tarjan_scc_rec(void *graph, void *root, set_ *valid_edges, unsigned suppressed_edge_number, unsigned level)
{
#if defined(GTB)
#if defined STRONG_TRACE
  text_printf("Strong component: call to recursive wrapper routine with suppressed edge %u and edge component {", suppressed_edge_number);

  set_print_set(valid_edges, NULL, 0);
  text_printf("}\n");
#endif

  bool at_top_level = set_includes_element(valid_edges, 0);

  set_difference_element(valid_edges, suppressed_edge_number);

  sp =stack;
  memset(stack, 0, (graph_max_node_num + 2) * sizeof (unsigned));
  memset(onstack, 0, (graph_max_node_num + 2) * sizeof (unsigned));
  memset(lowlink, 0, (graph_max_node_num + 2) * sizeof (unsigned));
  prenum = return_prenum;
  memset(return_prenum, 0, (graph_max_node_num + 2) * sizeof (unsigned));
  postnum = return_postnum;
  memset(return_postnum, 0, (graph_max_node_num + 2) * sizeof (unsigned));

  current_prenum = 1;
  current_postnum = 1;
  current_single_strong = -1;
  current_strong = 1;

  int *strong = (int*) mem_calloc(graph_max_node_num + 2, sizeof (int*));
  graph_create_pre_and_post_numbers_rec(root, valid_edges, strong);
  int scc_count = current_strong;

  // Make a set that is big enough to hold the whole graph's edges
  set_ scc_edges = SET_NULL;
  set_unite_element(&scc_edges, graph_max_edge_num);  // Set size of edges set accordingly

  // Make a set that is big enough to hold the whole graph's nodes
  set_ scc_nodes = SET_NULL;
  set_unite_element(&scc_nodes, graph_max_node_num);  // Set size of edges set accordingly

  // We have a set of SCC's in strong. Iterate over each one, building the edge and node sets
  for (int this_scc = 1; this_scc < scc_count; this_scc++)
  {
#if defined STRONG_TRACE
      text_printf("Gathering SCC number %i", this_scc);
#endif
    set_clear(&scc_nodes);
    set_clear(&scc_edges);

    int seen_left_context = 0;

    // Make a set with this_scc's nodes in it
    for (unsigned this_node_number = 0; this_node_number <= graph_max_node_num; this_node_number++)
      if (strong[this_node_number] == this_scc)
      {
        set_unite_element(&scc_nodes, this_node_number);
        for (void *this_edge = graph_next_out_edge(graph_node_index(graph)[this_node_number]);
             this_edge != NULL;
             this_edge = graph_next_out_edge(this_edge))
          if (strong[graph_atom_number(graph_destination(this_edge))] == this_scc)
          {
            set_unite_element(&scc_edges, graph_atom_number(this_edge));

            if (((gdg_edge*) this_edge)->left_context)
              seen_left_context = 1;
          }
      }

// take out the suppressed edges!
   set_intersect_set(&scc_edges, valid_edges);
   set_difference_element(&scc_edges, suppressed_edge_number);

#if defined STRONG_TRACE
      text_printf("Gathered SCC with nodes {");
      set_print_set(&scc_nodes, NULL, 0);
      text_printf("} and edges {");
      set_print_set(&scc_edges, NULL, 0);
      text_printf("}\n");
#endif

#if 1
    // Look to see if we have already processed this scc
    if (!seen_left_context)
    {
#if defined STRONG_TRACE
      text_printf("SCC has no L edges: continuing to next SCC\n");
#endif
      continue;  // to next scc
    }
#endif

    if (scc_node_table != NULL)
    {
      if (symbol_lookup_key(scc_node_table, &scc_nodes, NULL) != NULL)
      {
  #if defined STRONG_TRACE
        text_printf("SCC is already in scc_nodes table: continuing to next SCC\n");
  #endif
        continue;  // to next scc
      }

      set_ new_scc_nodes = SET_NULL;
      set_assign_set_normalise(&new_scc_nodes, &scc_nodes);

      graph_scc_node_table_data *this_scc_node_symbol = (graph_scc_node_table_data*) symbol_insert_key(scc_node_table, &new_scc_nodes, sizeof (set_), sizeof (graph_scc_node_table_data));
      this_scc_node_symbol->edges = set_array(&scc_edges);

  #if defined STRONG_TRACE
        text_printf("Added SCC to scc_nodes table: will recurse\n");

        symbol_print_table(scc_node_table);
  #endif

      if (++scc_count % 10000 == 0)
        text_printf("\r%u", scc_count);

      /* Now we need to iterate over all the edges in this_scc, removing
         one edge at a time and recursing */
      for (unsigned *this_scc_edge_number = this_scc_node_symbol->edges; *this_scc_edge_number != SET_END; this_scc_edge_number++)
      {
  #if defined STRONG_TRACE
      text_printf("Strong component: recursing after suppressing edge %u\n", *this_scc_edge_number);
  #endif
        if (*this_scc_edge_number > suppressed_edge_number)  //Triangularisation!
          if (recursion_limit == 0 || level < recursion_limit)
            graph_tarjan_scc_rec(graph, graph_destination(graph_edge_index(graph)[*this_scc_edge_number]), &scc_edges, *this_scc_edge_number, level++);
      }
  #if defined STRONG_TRACE
      text_printf("Finished recursing over edges: continuing to next SCC\n");
  #endif
    }

  }
  set_free(&scc_edges);
  set_free(&scc_nodes);

  if (!at_top_level)  //dirty trick: top level set has a zero element in it: return strong in that case...
    mem_free(strong);

  return strong;
#endif
}

int* graph_tarjan_scc(void *graph, set_ *ignore_edges, unsigned recursion_level, void *node_table)
{
#if defined STRONG_TRACE
  text_printf("Strong component: call to top level routine\n");
#endif

  scc_node_table = node_table;
  scc_count = 0;

  graph_max_node_num = graph_max_node_number(graph);
  graph_max_edge_num = graph_max_edge_number(graph);

  recursion_limit = recursion_level;
  stack = (unsigned*) mem_calloc(graph_max_node_num + 2, sizeof (unsigned));
  onstack = (unsigned*) mem_calloc(graph_max_node_num + 2, sizeof (unsigned));
  lowlink = (unsigned*) mem_calloc(graph_max_node_num + 2, sizeof (unsigned));
  return_prenum = (unsigned*) mem_calloc(graph_max_node_num + 2, sizeof (unsigned));
  return_postnum = (unsigned*) mem_calloc(graph_max_node_num + 2, sizeof (unsigned));

  set_ valid_edges = SET_NULL;
  set_fill(&valid_edges, graph_max_edge_number(graph));

  if (ignore_edges != NULL)
    set_difference_set(&valid_edges, ignore_edges);

  //Now do the recursive walk
  int *return_strong = graph_tarjan_scc_rec(graph, graph_root(graph), &valid_edges, 0, 1);

  set_free(&valid_edges);
  mem_free(stack);
  mem_free(onstack);
  mem_free(lowlink);
  mem_free(return_prenum);
  mem_free(return_postnum);

#if defined STRONG_TRACE
  symbol_print_table(scc_node_table);
#endif

  return return_strong;
}


int graph_in_degree(const void *node)
{
  int in_degree = 0;

  for (void *this_edge = graph_next_in_edge(node); this_edge != NULL; this_edge = graph_next_in_edge(this_edge))
    in_degree++;

  return in_degree;
}

int graph_out_degree(const void *node)
{
  int out_degree = 0;

  for (void *this_edge = graph_next_out_edge(node); this_edge != NULL; this_edge = graph_next_out_edge(this_edge))
    out_degree++;

  return out_degree;
}

void graph_reachable_nodes_rec(void *node, set_ *visited)
{
  unsigned atom = graph_atom_number(node);

  if (set_includes_element(visited, atom))
    return;
  else
  {
    set_unite_element(visited, atom);

    for (void* this_out_edge = graph_next_out_edge(node); this_out_edge != NULL; this_out_edge = graph_next_out_edge(this_out_edge))
      graph_reachable_nodes_rec(graph_destination(this_out_edge), visited);
  }
}

set_ *graph_reachable_nodes(void *this_graph, void *reachable_root_node)
{
  set_* visited = (set_*) mem_calloc(1, sizeof(set_));

  set_unite_element(visited, graph_max_node_number(this_graph));
  set_clear(visited);

  graph_reachable_nodes_rec(reachable_root_node, visited);

  return visited;
}

/* End of graph.c */
