/*******************************************************************************
*
* RDP release 1.70 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 16 March 2012
*
* memalloc.h - robust memory allocation with helpful messages on error
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef MEMALLOC_H
#define MEMALLOC_H

#include <stddef.h>

void * mem_calloc(size_t nitems, size_t size); 
void mem_free(void * block); 
void * mem_malloc(size_t size); 
void mem_print_statistics(void); 
void * mem_realloc(void * block, size_t size); 

#endif

/* End of memalloc.h */
