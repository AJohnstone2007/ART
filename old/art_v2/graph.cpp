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


/* #define DEBUG */

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

void graph_vcg_diagnostic_edge(const void *edge)
{
  text_printf("label: \"Edge:" GRAPH_ATOM_NUMBER_F " from:" GRAPH_ATOM_NUMBER_F " to:" GRAPH_ATOM_NUMBER_F "\"",
              graph_atom_number(edge),
              graph_atom_number(graph_source(edge)),
              graph_atom_number(graph_destination(edge)));
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

  text_printf("}\n");
}

static void graph_dot_graph(graph_graph * curr_graph,
void(* graph_action)(const void * graph),
void(* node_action)(const void * node),
void(* edge_action)(const void * edge)
)
{
  text_printf("digraph \"Graph from C++ graph library\" {\n" );

  graph_node * curr_node;
  graph_edge * curr_edge;

  if (graph_action != NULL)   /* print a user defined label field */
    graph_action(curr_graph + 1);

  curr_node = curr_graph->next_node;

  while (curr_node != NULL)
  {
    text_printf("\"" GRAPH_ATOM_NUMBER_F "\"", curr_node->atom_number);

    if (node_action != NULL)  /* print a user defined label field */
      node_action(curr_node + 1);
    else
      text_printf("[label=\"Node:" GRAPH_ATOM_NUMBER_F "\"]", curr_node->atom_number);

    text_printf("\n");

    curr_edge = curr_node->next_out_edge;

    while (curr_edge != NULL)
    {
      text_printf("\"" GRAPH_ATOM_NUMBER_F "\"->\"" GRAPH_ATOM_NUMBER_F "\"", curr_node->atom_number, curr_edge->destination->atom_number);

      if (edge_action != NULL) {/* print a user defined label field */
        edge_action(curr_edge + 1);
      }

      text_printf("\n");
      curr_edge = curr_edge->next_out_edge;
    }
    curr_node = curr_node->next_node;
  }

  text_printf("}\n");
}

void graph_dot(void * graph,
               void(* graph_action)(const void * graph),
               void(* node_action)(const void * node),
               void(* edge_action)(const void * edge)
              )
{
  if (graph == NULL)
  {
    /* dump all graphs */
    graph_graph * curr_graph = graph_list.next_graph;

    while (curr_graph != NULL)
    {
      graph_dot_graph(curr_graph, graph_action, node_action, edge_action);

      curr_graph = curr_graph->next_graph;
    }
  }
  else
    /* dump specific graph */
  graph_dot_graph((graph_graph *) graph - 1, graph_action, node_action, edge_action);
}

void * graph_insert_graph(const char * id)
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

  *((char * *)(++temp))= (char*) id;

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

  temp->next_out_edge = temp_edge->next_out_edge;  /* look at rest of list */
  temp_edge->next_out_edge = temp;     /* point previous at this node */

  temp->previous_out_edge = temp_edge;  /* point backlink at source_base pointer */

  if (temp->next_out_edge != NULL) /* if rest of list is non-null... */
    (temp->next_out_edge)->previous_out_edge = temp;  /* point next node back at us */

  temp->source = source_node_base;

  /* destination and in-edge processing */
  for (temp_edge = (graph_edge*) destination_node - 1;
       temp_edge->next_out_edge != NULL;
       temp_edge = temp_edge->next_out_edge)
    ;

  temp->next_in_edge = temp_edge->next_in_edge;  /* look at rest of list */
  destination_node_base->next_in_edge = temp;     /* point previous at this node */

  temp->previous_in_edge = temp_edge;  /* point backlink at destination_base pointer */

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
  if (node_or_edge == NULL)
    return NULL;
  else {
    graph_edge * temp =((graph_edge *) node_or_edge - 1)->next_out_edge;

    return temp == NULL ? temp: temp + 1;
  }
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
    text_message(TEXT_ERROR, "Edge %li has inconsistent previous in-edge pointer\n");

  if (edge_base->previous_out_edge->next_out_edge != edge_base)
    text_message(TEXT_ERROR, "Edge %li has inconsistent previous out-edge pointer\n");

  if (edge_base->next_in_edge != NULL)
    if (edge_base->next_in_edge->previous_in_edge != edge_base)
      text_message(TEXT_ERROR, "Edge %li has inconsistent next in-edge pointer\n");

  if (edge_base->next_out_edge != NULL)
    if (edge_base->next_out_edge->previous_out_edge != edge_base)
      text_message(TEXT_ERROR, "Edge %li has inconsistent next out-edge pointer\n");
}

void graph_print_consistency(void *graph)
{
  void *this_node;
  set_ nodes = SET_NULL, edges = SET_NULL, temp = SET_NULL;

  for (this_node = graph_next_node(graph); this_node != NULL; this_node = graph_next_node(this_node))
  {
    void *this_edge;

    set_unite_element(&nodes, graph_atom_number(this_node));

    for (this_edge = graph_next_in_edge(this_node); this_edge != NULL; this_edge = graph_next_in_edge(this_edge))
      set_unite_element(&edges, graph_atom_number(this_edge));

    for (this_edge = graph_next_out_edge(this_node); this_edge != NULL; this_edge = graph_next_out_edge(this_edge))
      set_unite_element(&edges, graph_atom_number(this_edge));
  }

  set_assign_set(&temp, &nodes);
  set_intersect_set(&temp, &edges);

  text_printf("Consistency report for graph: %li\nNodes:\n", graph_atom_number(graph));
  set_print_set(&nodes, NULL, 75);
  text_printf("\nEdges:\n");
  set_print_set(&edges, NULL, 75);
  text_printf("\nIntersection of nodes and edges:\n");
  if (set_is_empty(&temp))
    text_printf("(Empty)");
  else
    set_print_set(&temp, NULL, 75);
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

void **graph_node_index(void *graph)
{
  void** node_index = (void**) (((graph_graph*) graph - 1)->node_index);

  if (node_index == NULL)
    text_message(TEXT_FATAL, "attempt to use uninitialised graph node index\n");

  return node_index;
}

unsigned graph_node_index_size(void *graph)
{
  unsigned node_index_size = 0;
  void **this_node = graph_node_index(graph);

  while (*this_node++ != NULL)
    node_index_size++;

  return node_index_size;
}

void **graph_edge_index(void *graph)
{
  void ** edge_index = (void**) (((graph_graph*) graph - 1)->edge_index);

  if (edge_index == NULL)
    text_message(TEXT_FATAL, "attempt to use uninitialised graph edge index\n");

  return edge_index;
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

  levels = (GRAPH_ATOM_NUMBER_T *) mem_calloc(max_node_number + 1, sizeof (GRAPH_ATOM_NUMBER_T));
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
/* End of graph.c */
