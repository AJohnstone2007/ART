/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_sr.h - shift-reduce parser functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#define GTB_EDGE_VISITS
//#define GTB_ROB_COMPATIBLE

#ifndef GTB_SR_H
#define GTB_SR_H

typedef struct ssg_node_struct
{
  unsigned value;
  unsigned level;
} ssg_node;

typedef struct ssg_edge_struct
{
#if defined GTB_EDGE_VISITS
  unsigned visits;
#endif
  unsigned symbol;
  struct sppf_node_struct *sppf_node;
} ssg_edge;

typedef struct sppf_node_struct{
  unsigned x;
  unsigned j;
#if defined(GTB_ROB_COMPATIBLE)
  unsigned first_reduction_number;        /* This is only used by BRNGLR in ROB mode */
#endif
} sppf_node;

typedef struct derivation_struct{
  struct dfa *dfa;
  void *ssg;
  void* sppf;
  sppf_node** epsilon_sppf_index;
  int accept:1;
  unsigned *a;        /* Input string */ 
  unsigned n;         /* Length of input string */
}  derivation;

derivation* sr_lr_parse(dfa* this_dfa, char *string);
derivation *sr_tomita_1_parse(dfa * this_dfa, char *string, int nullable_accept_states_present, int reduction_queue_length);
derivation *sr_rnglr_recognise(dfa * this_dfa, char *string, int reduction_stack_size);

void sr_ssg_statistics(FILE * output_file, void *ssg_trace);

void sr_print_action(dfa* this_dfa, int state, int this_symbol, int this_parse_action);
void sr_write_derivation(FILE *output_file, derivation *this_derivation);
void sr_render_derivation(FILE *output_file, derivation *this_derivation);
#endif

