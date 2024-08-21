/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* memalloc.c - robust memory allocation with helpful messages on error
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdlib.h>
#include "textio.h"
#include "memalloc.h"

#define memalloc_instrument 1

#if (memalloc_instrument)
static unsigned long mem_free_calls = 0;
static unsigned long mem_calloc_calls = 0;
static unsigned long mem_calloced = 0;
static unsigned long mem_malloc_calls = 0;
static unsigned long mem_malloced = 0;
static unsigned long mem_realloc_calls = 0;
static unsigned long mem_realloced = 0;
#endif

static void * mem_check(void * p, const char * str)
{
  if (p == NULL)
    text_message(TEXT_FATAL, "insufficient memory for %salloc\n", str);
  return p;
}

void * mem_calloc(size_t nitems, size_t size)
{
  #if (memalloc_instrument)
  mem_calloc_calls++;
  mem_calloced +=(nitems * size);
  #endif

  return mem_check(calloc(nitems, size), "c");
}

void * mem_malloc(size_t size)
{
  #if (memalloc_instrument)
  mem_malloc_calls++;
  mem_malloced += size;
  #endif

  return mem_check(malloc(size), "m");
}

void * mem_realloc(void * block, size_t size)
{
  #if (memalloc_instrument)
  mem_realloc_calls++;
  mem_realloced += size;
  #endif

  return mem_check(realloc(block, size), "re");
}

void mem_free(void * block)
{
  if (block == NULL)          /* Is this a pointer actually allocated? */
    text_message(TEXT_FATAL, "attempted to free a null block\n");

#if (memalloc_instrument)
  mem_free_calls++;
//  text_printf("Freeing block at %p\n", block);
#endif

  free(block);                /* free the block */
}

void mem_print_statistics(void)
{
#if (memalloc_instrument)
  text_message(TEXT_INFO, "Heap manager made %lu calloc calls, %lu malloc calls, %lu realloc calls and %lu free calls\n",
  mem_calloc_calls, mem_malloc_calls, mem_realloc_calls, mem_free_calls);
  text_message(TEXT_INFO, "Heap manager calloc\'ed %lu bytes, malloc\'ed %lu bytes and realloc\'ed %lu bytes\n",
  mem_calloced, mem_malloced, mem_realloced);
#endif
}

/* End of memalloc.c */
