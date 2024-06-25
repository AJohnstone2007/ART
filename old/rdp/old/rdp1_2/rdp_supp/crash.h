/****************************************************************************
*
* RDP release 1.20 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 6 Nov 1994
*
* crash.h - fatal error handling and memory allocation
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef CRASH_H
#define CRASH_H

#include <stddef.h>
void crash__(char *fmt,...);
void *crash__malloc(size_t size, char *fmt,...);
void *crash__calloc(size_t size, char *fmt,...);

#endif

/* End of crash.h */
