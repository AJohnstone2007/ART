/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* memalloc.h - robust memory allocation with helpful messages on error
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef MEMALLOC_H
#define MEMALLOC_H

#include <stddef.h>

void *mem__calloc(size_t nitems, size_t size);
void mem__free(void *block);
void *mem__malloc(size_t size);
void *mem__realloc(void *block, size_t size);

#endif

/* End of memalloc.h */
