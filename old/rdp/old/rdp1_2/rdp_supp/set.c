/****************************************************************************
*
* RDP release 1.20 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 6 Nov 1994
*
* set.c - Pascal style set handling
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <stdlib.h>
#include "params.h"
#include "crash.h"
#include "set.h"

unsigned set__size = 0,
 set__length = 0;

/* find bit n */
static unsigned char mask[] = {1, 2, 4, 8, 16, 32, 64, 128};

/* bits per nibble lookup      0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15 */
static unsigned char bits[] = {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4};

/****************************************************************************
*
* Initialise set library: must be called once only before any other set__*
*                         routines.
*
****************************************************************************/
void set__init(unsigned size)
{
  if (set__size != 0)
    crash__("set: set__init() called twice");

  set__size = size;
  set__length = ((size / 8) + (size % 8 == 0 ? 0 : 1)); /* number of bytes needed to hold size elements */
}

/****************************************************************************
*
* Allocate space for a new set and perform sanity checks
*
****************************************************************************/
set__ set__make(void)
{
  if (set__length == 0)
    crash__("set: set__init() must be called before set__make()");

  return (set__) crash__calloc(set__length, "set");
}

/****************************************************************************
*
* Allocate space for a new set and stuff some new elements in
*
****************************************************************************/
set__ set__construct(unsigned e,...)
{
  va_list ap;
  unsigned c = e;
  set__ s = set__make();

  va_start(ap, e);              /* read parameter list until oversized element */
  do
  {
    s[c >> 3] |= mask[c & 7];   /* OR in coresponding bit */
  }
  while ((c = va_arg(ap, unsigned)) < (unsigned) set__size);
  va_end(ap);                   /* end of parameter list processing */

  return (s);
}

/****************************************************************************
*
* Invert the contents of a set
*
****************************************************************************/
void set__invert(set__ s)
{
  unsigned count;

  for (count = 0; count < set__length; count++) /* scan set */
    s[count] = ~s[count];       /* invert bytes */
}

/****************************************************************************
*
* Clear the contents of a set
*
****************************************************************************/
void set__clear(set__ s)
{
  memset(s, 0, set__length);    /* clear the set to the empty set */
}

/****************************************************************************
*
* Count number of elements in a set
*
****************************************************************************/
unsigned set__cardinality(set__ s)
{
  unsigned cardinality = 0,
   count;

  for (count = 0; count < set__length; count++)
  {
    cardinality += bits[s[count] & 15];
    cardinality += bits[s[count] >> 4];
  }
  return cardinality;
}

/****************************************************************************
*
* Intersect the contents of the second set into the first
*
****************************************************************************/
void set__intersect_set(set__ dst, set__ src)
{
  unsigned count;

  for (count = 0; count < set__length; count++) /* scan sets */
    dst[count] &= src[count];   /* intersect source bits */
}

/****************************************************************************
*
* Copy the contents of the second set into the first.
*
****************************************************************************/
void set__assign_set(set__ dst, set__ src)
{
  memcpy(dst, src, set__length);/* straight memory-memory copy */
}

void set__assign_element(set__ s, unsigned element)
{
  memset(s, 0, set__length);    /* clear the set to the empty set */

  s[element >> 3] |= mask[element & 7]; /* OR in the corresponding bit */
}

void set__assign_list(set__ s,...)
{
  va_list ap;
  unsigned c;

  memset(s, 0, set__length);    /* clear the set to the empty set */

  va_start(ap, s);              /* read parameter list until oversized element */
  while ((c = va_arg(ap, unsigned)) < (unsigned) set__size)
    s[c >> 3] |= mask[c & 7];   /* OR in the corresponding bit */
  va_end(ap);                   /* end of parameter list processing */
}

/****************************************************************************
*
* Unite the contents of the second set into the first
*
****************************************************************************/
void set__unite_set(set__ dst, set__ src)
{
  unsigned count;

  for (count = 0; count < set__length; count++) /* scan sets */
    dst[count] |= src[count];   /* OR in source bits */
}

void set__unite_element(set__ s, unsigned element)
{
  s[element >> 3] |= mask[element & 7]; /* OR in the corresponding bit */
}

void set__unite_list(set__ s,...)
{
  va_list ap;
  unsigned c;

  va_start(ap, s);              /* read parameter list until oversized element */
  while ((c = va_arg(ap, unsigned)) < (unsigned) set__size)
    s[c >> 3] |= mask[c & 7];   /* OR in coresponding bit */
  va_end(ap);                   /* end of parameter list processing */
}

/****************************************************************************
*
* Remove the contents of the second set from the first
*
****************************************************************************/
void set__remove_set(set__ dst, set__ src)
{
  unsigned count;

  for (count = 0; count < set__length; count++) /* scan sets */
    dst[count] &= ~src[count];  /* mask off source bits */
}

void set__remove_element(set__ s, unsigned element)
{
  s[element >> 3] &= ~mask[element & 7];  /* mask off corresponding bit */
}

void set__remove_list(set__ s,...)
{
  va_list ap;
  unsigned c;

  va_start(ap, s);              /* read parameter list until oversized element */
  while ((c = va_arg(ap, unsigned)) < (unsigned) set__size)
    s[c >> 3] &= ~mask[c & 7];  /* mask off corresponding bit */
  va_end(ap);                   /* end of parameter list processing */
}

/****************************************************************************
*
* Test if the second set is a subset of the first
*
****************************************************************************/
int set__inc_set(set__ dst, set__ src)
{
  int isin = 1;
  unsigned count;

  for (count = 0; count < set__length; count++) /* scan sets */
    if ((src[count] | dst[count]) != dst[count])
      isin = 0;                 /* test if src has bits set  where dst has zeroes */

  return (isin);
}

int set__inc_element(set__ s, unsigned element)
{
  return (s[element >> 3] & mask[element & 7]) != 0;
}

int set__inc_list(set__ s,...)
{
  va_list ap;
  unsigned c;
  int isin = 1;

  va_start(ap, s);              /* read parameter list until oversized element */
  while ((c = va_arg(ap, unsigned)) < (unsigned) set__size)
    isin = isin && ((s[c >> 3] & mask[c & 7]) != 0);
  va_end(ap);                   /* end of parameter list processing */
  return (isin);
}

/****************************************************************************
*
* Diagnostic dump of set contents
*
****************************************************************************/
void set__print(char *msg, set__ s)
{
  unsigned c;

  fprintf(MESSAGES, "%s\n", msg);
  for (c = 0; c < (unsigned) set__length; c++)  /* scan over set */
    fprintf(MESSAGES, "%02X ", s[c]);      /* print as list of hex bytes */
  fprintf(MESSAGES, "\n");
  for (c = 0; c < (unsigned) set__size; c++)  /* scan over all set elements */
    if (set__inc_element(s, c)) /* print as list of decimal elements */
      fprintf(MESSAGES, "%u\n", c);
}

/* End of set.c */
