/*****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* memalloc.c - robust memory allocation with helpful messages on error
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#include <stdlib.h>
#include "textio.h"
#include "memalloc.h"

static void *mem__check(void *p, const char *str)
{
  if (p == NULL)
    text__message(TEXT__FATAL, "insufficient memory for %salloc", str);
  return p;
}

void *mem__calloc(size_t nitems, size_t size)
{
  return mem__check(calloc(nitems, size), "c");
}

void mem__free(void *block)
{
  if (block == NULL)            /* Is this a pointer actually allocated? */
    text__message(TEXT__FATAL, "attempted to free a null block");

  free(block);                  /* free the block */
}

void *mem__malloc(size_t size)
{
  return mem__check(malloc(size), "m");
}

void *mem__realloc(void *block, size_t size)
{
  return mem__check(realloc(block, size), "re");
}

/* End of memalloc.c */
