/*******************************************************************************
*
* GTB release 2.6 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 4 December 2007
*
* gtb_gll.h - auxilliary functions for gll parsers
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#define VCG

typedef struct P_node{unsigned k; struct P_node *P_nodes;} p_node;
typedef struct gss_edge {struct gss_node *target; struct gss_edge* edges;} gss_edge;
typedef struct gss_node {
  enum gll_label L;
  unsigned i;
  gss_edge *edges;
  P_node *P_nodes;
#if defined(VCG)
  unsigned vcg_node_number;
#endif
} gss_node;

extern unsigned R_cardinality;  /* defined in gtb_gll.cpp */
extern const unsigned m;        /* defined in generated parser or by lexer */

gss_node* gll_init(enum gll_label S_0, unsigned return_label_count, unsigned alternate_label_count);

void gll_print_accept_or_reject();

void gll_add(enum gll_label L, gss_node *u, int j);

void gll_remove(enum gll_label *L, gss_node **u, int *j);

void gll_pop(gss_node *u, int j);

gss_node *gll_create(enum gll_label L, gss_node *u, int j);
