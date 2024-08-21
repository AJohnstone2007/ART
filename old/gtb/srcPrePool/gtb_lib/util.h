/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 30 August 2004
*
* util.h - utility routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef UTIL_H
#define UTIL_H
#include "util.h"

#define min(a,b) (a<b?a:b)

#define max(a,b) (a>b?a:b)

int utl_iterative_bsearch_unsigned(unsigned *buffer, int low_index, int high_index, const unsigned target);

#endif
