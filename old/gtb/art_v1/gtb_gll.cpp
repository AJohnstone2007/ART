/*******************************************************************************
*
* GTB release 2.6 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 4 December 2007
*
* gtb_gll.cpp - auxilliary functions for gll parsers
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include "gtb_gll.h"
#include "memalloc.h"
#include "textio.h"

typedef struct R_node{enum gll_label L; gss_node *u; struct R_node *R_nodes;} R_node;
typedef struct U_node{gss_node *u; unsigned valid;} U_node;

static gss_node *u_0;
static U_node *U;
static R_node **R;
unsigned R_cardinality = 0;

#if defined(VCG)
static FILE *vcg_file;
static unsigned vcg_node_count = 0;
static unsigned vcg_edge_count = 0;
#endif

static gss_node* new_gss_node(enum gll_label L, unsigned i)
{
  gss_node *temp = (gss_node*) mem_calloc(1, sizeof(gss_node));
  temp->L = L;
  temp->i = i;
#if defined(VCG)
    temp->vcg_node_number = ++vcg_node_count;
  fprintf(vcg_file, "node:{title:\"%i\" level:%i label:\"%i, %i\"}\n", temp->vcg_node_number, i, L ,i);
#endif
  return temp;
}

static void new_gss_edge(gss_node*source, gss_node *target)
{
  gss_edge* temp= (gss_edge*) mem_calloc(1, sizeof(gss_edge));

  temp->target = target;
  temp->edges = source->edges;
  source->edges = temp;
#if defined(VCG)
  ++vcg_edge_count;
  fprintf(vcg_file, "edge:{sourcename:\"%i\" targetname:\"%i\"}\n", source->vcg_node_number, target->vcg_node_number);
#endif
}

gss_node* gll_init(enum gll_label S_0, unsigned return_label_count, unsigned alternate_label_count)
{
#if defined(VCG)
  if ((vcg_file = fopen("gll.vcg", "w")) == NULL)
    text_message(TEXT_FATAL, "unable to open 'gll.vcg' for write\n");
  fprintf(vcg_file, "graph:{orientation:top_to_bottom edge.arrowsize:7 edge.thickness:1 arrowmode:free\n");
#endif

  u_0 = new_gss_node(S_0, 0);
  R = (R_node**) mem_calloc(m, sizeof(R_node*));
  U = (U_node*) mem_calloc(return_label_count * (return_label_count + alternate_label_count), sizeof(U_node));
  for (int temp = 0; temp < return_label_count * (return_label_count + alternate_label_count); temp++)
    U[temp].valid = UINT_MAX;
  return u_0;
}

void gll_print_accept_or_reject()
{
  U_node *U_i_L = &U[u_0->L];

  if (U_i_L->valid == m && U_i_L->u == u_0)
    text_printf("Accept\n");
  else
    text_printf("Reject\n");

#if defined(VCG)
  fprintf(vcg_file, "}\n");
#endif
}

void gll_add(enum gll_label L, gss_node *u, int j)
{
  U_node *U_i_L = &U[L * u->L];

  if (U_i_L->valid != j)
  {
    U_i_L->u = u;
    U_i_L->valid = j;
  }

  R_node *temp = (R_node*) mem_calloc(1, sizeof(R_node));
  temp->L = L;
  temp->u = u;
  temp->R_nodes = R[j];
  R[j] = temp;
  R_cardinality++;
}

void gll_remove(enum gll_label *L, gss_node **u, int *j)
{
  /* This loop looks dangerous because we don't check level against m, but we
     have already tested the cardinality of R before calling this */

  /* To do: improve performance here by keeping track of the lowest R that has cadinality > 0 */
  for (int level = 0; R[level] != NULL; level++)
  {
    R_cardinality--;
    R_node *temp = R[level];
    R[level] = R[level]->R_nodes;  /* unlink */
    *L  = temp->L;
    *u = temp->u;
    *j = level;
    mem_free(temp);
  }
}

void gll_pop(gss_node *u, int j)
{
  /* Note to Liz: change function in paper to check u against u_0 not c_u */
  if (u != u_0)
  {
    P_node * temp = (P_node*) mem_calloc(1, sizeof (P_node));

    temp->k = j;
    temp->P_nodes = u->P_nodes;
    u->P_nodes = temp;

    for (gss_edge* this_edge = u->edges; this_edge != NULL; this_edge = this_edge->edges)
      gll_add(u->L, this_edge->target, j);
  }
}

gss_node *gll_create(enum gll_label L, gss_node *u, int j)
{
  U_node *U_i_L = &U[L * u->L];
  gss_node *v;

  if (U_i_L->valid != j)
  {
    v = U_i_L->u = new_gss_node(L,j);
    U_i_L->valid = j;
  }
  else
  {
    v = U_i_L->u;

    for (gss_edge *temp = v->edges; temp != NULL; temp = temp->edges)
      if (temp->target == u)
        return v;
  }

  new_gss_edge(v, u);
  return v;
}
