/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* set.h - dynamically resizable set handling
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include "textio.h"
#include "memalloc.h"
#include "set.h"

/* lookup table of bit masks for bits 0 to 7 */
/*                                    0  1  2  3   4   5   6    7 */
static const unsigned char mask[8] = {1, 2, 4, 8, 16, 32, 64, 128};

/* minimum allowable set size so as to minimise reallocation traffic */
static unsigned set__minimumsize = 0;

#define SET__GROW_TO                                                         \
  {                                                                          \
    va_list ap;                   /* argument list walker */                 \
    unsigned c,                   /* running argument copy */                \
     max = 0;                     /* largest element */                      \
    /* scan all the elements to find the largest one */                      \
    va_start(ap, dst);            /* read parameter list until end marker */ \
    while ((c = va_arg(ap, unsigned)) != SET__END)                           \
      if (c > max)                /* if this element is larger than max */   \
	max = c;                  /* then remember it */                     \
    va_end(ap);                   /* end of parameter list processing */     \
    set__grow(dst, max / 8 + 1);/* set size to be at least as long max */    \
  }

#define SET__ITERATE_LIST(func)                                              \
  {                                                                          \
    va_list ap;                   /* argument list walker */                 \
    unsigned c;                   /* running argument copy */                \
    va_start(ap, dst);            /* read parameter list until end marker */ \
    while ((c = va_arg(ap, unsigned)) != SET__END)                           \
      func;                                                                  \
    va_end(ap);                   /* end of parameter list processing */     \
  }

#define SET__ITERATE_SET(func, length)                                       \
  {                                                                          \
    unsigned count;                                                          \
    for (count = 0; count < length; count++) /* scan sets */                 \
      func;                                                                  \
  }

/* return an array of (cardinality + 1) unsigned integers, being the element
   numbers of set src terminated by SET__END */
unsigned *set__array(const set__ * src)
{
  unsigned *running,
  *block = (unsigned *) mem__malloc((set__cardinality(src) + 1) * sizeof(unsigned)),
   c,                           /* current set byte counter */
   element = 0;                 /* running element number */

  running = block;              /* point running pointer at start of block */

  for (c = 0; c < src->length; c++) /* scan over all set bytes */
  {
    unsigned walker,            /* walking one */
     byte = src->elements[c];   /* get the current byte */

    for (walker = 1; walker < 256; walker <<= 1)  /* walk a one across eight bits */
    {
      if ((byte & walker) != 0) /* if this element is present in the set */
	*running++ = element;

      element++;
    }
  }
  *running = SET__END;

  return block;
}

/* return the number of elements in this set */
unsigned set__cardinality(const set__ * src)
{
  /* lookup table of number of bits set in a nibble */
  /* 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 */
  static const unsigned char bits[16] = {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4};

  unsigned cardinality = 0,     /* running cardinality counter */
   count;                       /* set element counter */

  for (count = 0; count < src->length; count++) /* scan over whole set */
  {
    cardinality += bits[src->elements[count] & 15]; /* add in cardinality of hi nibble */
    cardinality += bits[src->elements[count] >> 4]; /* add in cardinality of lo nibble */
  }
  return cardinality;           /* return result */
}

/* clear a dst and then set only those bits specified by src */
void set__assign_element(set__ * dst, const unsigned element) /* assign one element to a set */
{
  set__grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  dst->elements[element >> 3] |= mask[element & 7]; /* OR in the corresponding bit */
}

void set__assign_list(set__ * dst,...)  /* assign a list of elements to a set */
{
  SET__GROW_TO;
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  SET__ITERATE_LIST((dst->elements[c >> 3] |= mask[c & 7]));
}

void set__assign_set(set__ * dst, const set__ * src)  /* assign one set to another */
{
  set__grow(dst, src->length);  /* set size to be at least as long as needed */
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  memcpy(dst->elements, src->elements, src->length);  /* memory-memory copy */
}

/* perform a comparison between sets, returning results like strcmp() */
int set__compare(set__ * dst, set__ * src)
{
  set__normalise(dst);
  set__normalise(src);

  if (dst->length != src->length)
    return dst->length > src->length;
  else
    return strncmp((char *) dst->elements, (char *) src->elements, dst->length);
}

/* remove from dst those elements in src */
void set__difference_element(set__ * dst, const unsigned element)
{
  set__grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  dst->elements[element >> 3] &= ~mask[element & 7];  /* mask off corresponding bit */
}

void set__difference_list(set__ * dst,...)
{
  SET__GROW_TO;
  SET__ITERATE_LIST((dst->elements[c >> 3] &= ~mask[c & 7]));
}

void set__difference_set(const set__ * dst, const set__ * src)
{
  unsigned length = dst->length < src->length ? dst->length : src->length;  /* only iterate over shortest set */

  SET__ITERATE_SET((dst->elements[count] &= ~src->elements[count]), length);
}

/* free storage associated with a set's elements and return set to SET__NULL */
void set__free(set__ * dst)
{
  mem__free(dst->elements);
  dst->length = 0;
  dst->elements = NULL;
}

void set__grow(set__ * dst, const unsigned length)  /* test size of set and resize if necessary */
{
  if (dst->length < length)     /* if set is too small then resize */
  {
    /* belt and braces resize */
    unsigned char * temp = (unsigned char *) mem__calloc(length, 1);

    if (dst->elements != NULL)
    {
      memcpy(temp, dst->elements, dst->length);
      mem__free(dst->elements);
    }
    dst->length = length;
    dst->elements = temp;

#if 0
    dst->elements = (unsigned char *) mem__realloc(dst->elements, length);
    memset(dst->elements + dst->length, 0, length - dst->length); /* clear new bytes */
    dst->length = length;       /* and set new length accordingly */
#endif
  }
}

int set__includes_element(set__ * dst, const unsigned element)
{
  set__grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  return (dst->elements[element >> 3] & mask[element & 7]) != 0;
}

int set__includes_list(set__ * dst,...)
{
  int isin = 1;

  SET__GROW_TO;
  SET__ITERATE_LIST((isin = isin && ((dst->elements[c >> 3] & mask[c & 7]) != 0)));
  return (isin);
}

int set__includes_set(const set__ * dst, const set__ * src)
{
  int isin = 1;
  unsigned length = dst->length < src->length ? dst->length : src->length;  /* only iterate over shortest set */

  SET__ITERATE_SET((isin = isin && ((src->elements[count] | dst->elements[count]) == dst->elements[count])), length);

  if (src->length > dst->length)/* there may be more bits left! */
  {
    unsigned count;

    for (count = length; count < src->length; count++)
      isin = isin && (src->elements[count] == 0);
  }
  return (isin);
}

void set__intersect_element(set__ * dst, const unsigned element)
{
  unsigned char result;

  set__grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  result = dst->elements[element >> 3] & mask[element & 7];
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  dst->elements[element >> 3] = result;
}

void set__intersect_list(set__ * dst,...)
{
  set__ src = SET__NULL;

  /* first we must build a set to hold the element list: copy set__assign_list */
  {
      va_list ap;               /* argument list walker */
      unsigned c,               /* running argument copy */
     max = 0;                   /* largest element */

       /* scan all the elements to find the largest one */
      va_start(ap, dst);        /* read parameter list until end marker */
      while ((c = va_arg(ap, unsigned)) != SET__END)
	if (c > max)            /* if this element is larger than max */
	  max = c;              /* then remember it */
      va_end(ap);               /* end of parameter list processing */
      set__grow(&src, max / 8 + 1); /* set size to be at least as long max */
  }
  memset(src.elements, 0, src.length);  /* clear the set to the empty set */
  SET__ITERATE_LIST((src.elements[c >> 3] |= mask[c & 7]));

  set__intersect_set(dst, &src);

  set__free(&src);
}

void set__intersect_set(set__ * dst, const set__ * src)
{
  unsigned length = dst->length < src->length ? dst->length : src->length;  /* only iterate over shortest set */

  SET__ITERATE_SET((dst->elements[count] &= src->elements[count]), length);
  /* Now clear rest of dst */
    if (length < dst->length)
    memset(dst->elements + length, 0, dst->length - length); /* clear new bytes */
}

void set__invert(set__ * dst, const unsigned universe)
{
  /* lookup table of fill values for bits 0 - 7 */
  /* 0  1  2   3   4   5    6    7 */
  static const unsigned char fills[8] = {1, 3, 7, 15, 31, 63, 127, 255};

  unsigned top = universe / 8 + 1;

  set__grow(dst, top);          /* set size to be at least as long as needed */
  SET__ITERATE_SET((dst->elements[count] ^= 0xFF), dst->length);
  dst->elements[top - 1] &= fills[universe % 8];  /* mask off last byte */
  memset(dst->elements + top, 0, dst->length - top);  /* clear extra bytes */
}

unsigned set__minimum_size(const unsigned minimum_size)
{
  if (minimum_size != SET__END) /* minimum = SET__END => query only */
    set__minimumsize = minimum_size;  /* set the minimum size */

  return set__minimumsize;      /* return current value */
}

void set__normalise(set__ * dst)
{
  if (dst->length < set__minimumsize) /* do we need to grow dst? */
    set__grow(dst, set__minimumsize); /* grow to minimum size */
  else
  {
    unsigned char *p = dst->elements + dst->length - 1; /* find last byte */

    if (*p == 0)                /* is there an empty byte at end? */
    {
      while (*(p--) == 0 && dst->length > set__minimumsize) /* run down zero bytes */
        dst->length--;          /* reducing length as we go */

      dst->elements = (unsigned char *) mem__realloc(dst->elements, dst->length); /* and resize */
    }
  }
}

void set__print_element(const unsigned element, const char *element_names)
{
  if (element_names == NULL)  /* just print decimal set element numbers */
    text__printf("%u", element);
  else
  {
    unsigned c;

    /* skip to correct element name */
    for (c = element; c > 0; c--)
      while (*element_names++ != 0)
	;

    text__printf("%s", element_names);  /* print the elemnt name string */
  }
}

void set__print_set(const set__ * src, const char *element_names)
{
  unsigned
   last_printed = 0,
   not_first = 0,
  *elements = set__array(src),
  *base;

  base = elements;
  while (*elements != SET__END)
  {
    if (not_first)
      text__printf(", ");
    else
      not_first = 1;

    if (element_names == NULL)  /* just print decimal set element numbers */
      text__printf("%u", *elements++);
    else
    {
      unsigned c;

      /* skip to correct element name */
      for (c = *elements - last_printed; c > 0; c--)
	while (*element_names++ != 0)
	  ;

      text__printf("%s", element_names);  /* print the elemnt name string */
      last_printed = *elements++; /* remember where we are for next time */
    }
  }
  mem__free(base);          /* release memory block */
}

void set__unite_element(set__ * dst, const unsigned element)
{
  set__grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */

  dst->elements[element >> 3] |= mask[element & 7]; /* OR in the corresponding bit */
}

void set__unite_list(set__ * dst,...)
{
  SET__GROW_TO;
  SET__ITERATE_LIST((dst->elements[c >> 3] |= mask[c & 7]));
}

void set__unite_set(set__ * dst, const set__ * src)
{
  set__grow(dst, src->length);  /* set size to be at least as long as needed */
  SET__ITERATE_SET((dst->elements[count] |= src->elements[count]), src->length);
}

/* End of set.c */
