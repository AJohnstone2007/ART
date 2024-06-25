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

static unsigned long mem_calloced = 0; 
static unsigned long mem_malloced = 0; 
static unsigned long mem_realloced = 0; 

static void * mem_check(void * p, const char * str)
{
  if (p == NULL)
    text_message(TEXT_FATAL, "insufficient memory for %salloc\n", str); 
  return p; 
}

void * mem_calloc(size_t nitems, size_t size)
{
  mem_calloced +=(nitems * size); 
  
  return mem_check(calloc(nitems, size), "c"); 
}

void mem_free(void * block)
{
  if (block == NULL)          /* Is this a pointer actually allocated? */
    text_message(TEXT_FATAL, "attempted to free a null block\n"); 
  
  free(block);                /* free the block */
}

void * mem_malloc(size_t size)
{
  mem_malloced += size; 
  
  return mem_check(malloc(size), "m"); 
}

void mem_print_statistics(void)
{
  text_message(TEXT_INFO, "Heap manager calloc\'ed %lu bytes, malloc\'ed %lu bytes and realloc\'ed %lu bytes\n", 
  mem_calloced, mem_malloced, mem_realloced); 
}

void * mem_realloc(void * block, size_t size)
{
  mem_realloced += size; 
  
  return mem_check(realloc(block, size), "re"); 
}
/* End of memalloc.c */
