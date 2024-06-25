/*******************************************************************************
*
* RDP release 1.70 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 16 March 2012
*
* hist.c - histogram creation and manipulation routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <limits.h>
#include <stdlib.h>
#include "hist.h"
#include "memalloc.h"
#include "textio.h"

void hist_init(histogram_node **this_hist)
{
  *this_hist = (histogram_node*) mem_calloc(1, sizeof(histogram_node)); /* create base sentinel */

  (*this_hist)->next = (histogram_node*) mem_calloc(1, sizeof(histogram_node)); /* create end sentinel */
  (*this_hist)->next->bucket = ULONG_MAX;
}

void hist_update(histogram_node *this_hist, unsigned long value)
{
  histogram_node *this_histogram_node = this_hist;
  histogram_node *previous_histogram_node;
  histogram_node *new_histogram_node;

  do
  {
    if (this_histogram_node->bucket == value)
    {
      this_histogram_node->value++;
      return;
    }

    previous_histogram_node = this_histogram_node;
    this_histogram_node = this_histogram_node->next;
  }
  while (this_histogram_node->bucket <= value);

  new_histogram_node = (histogram_node*) mem_calloc(1, sizeof(histogram_node));
  new_histogram_node->bucket = value;
  new_histogram_node->value = 1;
  new_histogram_node->next = this_histogram_node;
  previous_histogram_node->next = new_histogram_node;
}

void hist_print(histogram_node *this_hist)
{
  histogram_node *this_histogram_node = this_hist;

  while (this_histogram_node->next != NULL)
  {
    if (this_histogram_node->value != 0)
      text_printf("%lu: %lu\n", this_histogram_node->bucket, this_histogram_node->value);

    this_histogram_node = this_histogram_node->next;
  }
}

unsigned long hist_bucket_value(histogram_node *this_hist, unsigned long bucket)
{
  histogram_node *this_histogram_node = this_hist;

  while (this_histogram_node->next != NULL && this_histogram_node->bucket != bucket)
    this_histogram_node = this_histogram_node->next;

  return this_histogram_node->bucket == bucket ? this_histogram_node->value : 0;
}

unsigned long hist_count_nonempty_buckets(histogram_node *this_hist)
{
  histogram_node *this_histogram_node = this_hist;
  unsigned long buckets = 0;

  while (this_histogram_node->next != NULL)
  {
    if (this_histogram_node->value != 0)
      buckets++;

    this_histogram_node = this_histogram_node->next;
  }

  return buckets;
}

unsigned long hist_count_all_buckets(histogram_node *this_hist)
{
  histogram_node *this_histogram_node = this_hist;
  unsigned long buckets = 0;

  while (this_histogram_node->next != NULL)
  {
    buckets++;

    this_histogram_node = this_histogram_node->next;
  }

  return buckets;
}

unsigned long hist_sum_buckets(histogram_node *this_hist)
{
  histogram_node *this_histogram_node = this_hist;
  unsigned long sum = 0;

  while (this_histogram_node->next != NULL)
  {
    sum += this_histogram_node->value;

    this_histogram_node = this_histogram_node->next;
  }

  return sum;
}

unsigned long hist_weighted_sum_buckets(histogram_node *this_hist)
{
  histogram_node *this_histogram_node = this_hist;
  unsigned long sum = 0;

  while (this_histogram_node->next != NULL)
  {
    sum += this_histogram_node->bucket * this_histogram_node->value;

    this_histogram_node = this_histogram_node->next;
  }

  return sum;
}

void hist_free(histogram_node **this_hist)
{
  histogram_node *free_node = *this_hist;

  while (free_node != NULL)
  {
    histogram_node *succ_node = free_node->next;

    mem_free(free_node);

    free_node = succ_node;
  }

  *this_hist = NULL;
}

