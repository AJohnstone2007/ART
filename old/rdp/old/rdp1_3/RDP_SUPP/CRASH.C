/****************************************************************************
*
* RDP release 1.30 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 28 Dec 1994
*
* crash.c - fatal error handling and memory allocation
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include "params.h"
#include "crash.h"

void crash__(char *fmt,...)
{
  va_list ap;                   /* argument list walker */

  fprintf(MESSAGES, "\nFatal: ");
  va_start(ap, fmt);
  vfprintf(MESSAGES, fmt, ap);
  va_end(ap);
  fprintf(MESSAGES, "\n");

  exit(EXIT_FAILURE);
}

void *crash__malloc(size_t size, char *fmt,...)
{
  void *p = malloc(size);

  if (p == NULL)
  {
    va_list ap;                 /* argument list walker */

    fprintf(MESSAGES, "\nRan out of memory during allocation of ");
    va_start(ap, fmt);
    vfprintf(MESSAGES, fmt, ap);
    va_end(ap);
    fprintf(MESSAGES, "\n");

    exit(EXIT_FAILURE);
  }

  return p;
}

void *crash__calloc(size_t size, char *fmt,...)
{
  void *p = calloc(1, size);

  if (p == NULL)
  {
    va_list ap;                 /* argument list walker */

    fprintf(MESSAGES, "\nRan out of memory during allocation of ");
    va_start(ap, fmt);
    vfprintf(MESSAGES, fmt, ap);
    va_end(ap);
    fprintf(MESSAGES, "\n");

    exit(EXIT_FAILURE);
  }

  return p;
}

/* End of crash.c */
