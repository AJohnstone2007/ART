/*******************************************************************************
*
* RDP release 1.60 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* hist.c - histogram creation and manipulation routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef HIST_H
#define HIST_H

typedef struct histogram_node_struct
{
  unsigned long bucket;
  unsigned long value;
  struct histogram_node_struct *next;
} histogram_node;

void hist_init(histogram_node **this_hist);
void hist_update(histogram_node *this_hist, unsigned long value);
void hist_print(histogram_node *this_hist);
unsigned long hist_bucket_value(histogram_node *this_hist, unsigned long bucket);
unsigned long hist_count_nonempty_buckets(histogram_node *this_hist);
unsigned long hist_count_all_buckets(histogram_node *this_hist);
unsigned long hist_sum_buckets(histogram_node *this_hist);
unsigned long hist_weighted_sum_buckets(histogram_node *this_hist);
void hist_free(histogram_node **this_hist);
#endif
