/*****************************************************"**************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_sppf.h - sppf related functions used by gtb_rnglr and gtb_brnglr
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef GTB_SPPF_H
#define GTB_SPPF_H
#include "gtb_sr.h"

struct epsilon_sppf_table_data {
unsigned *id;
struct sppf_node_struct *sppf_node;
};

extern unsigned sppf_epsilon_node_count;
extern unsigned sppf_epsilon_edge_count;

void sppf_set_render_derivation(derivation *derivation);
void sppf_vcg_print_edge(const void *edge);
void sppf_vcg_print_node(const void *node);
void sppf_statistics(derivation* this_derivation, char *message);
void sppf_vcg_epsilon_sppf_print_index(const void *graph);
void sppf_create_epsilon_sppf(derivation* this_derivation);
#endif

