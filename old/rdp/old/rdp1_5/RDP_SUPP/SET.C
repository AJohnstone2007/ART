/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* set.h - dynamically resizable set handling
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include "textio.h"
#include "memalloc.h"
#include "set.h"

/* lookup table of bit masks for bits 0 to 7 */
/*                                    0  1  2  3   4   5   6    7 */
static const unsigned char mask[8]= {1, 2, 4, 8, 16, 32, 64, 128}; 

/* minimum allowable set size so as to minimise reallocation traffic */
static unsigned set_minimumsize = 0; 

#define SET_GROW_TO                                                         \
{ \
  va_list ap;                 /* argument list walker */ \
  unsigned c,                 /* running argument copy */ \
  max = 0;                    /* largest element */ \
  /* scan all the elements to find the largest one */ \
  va_start(ap, dst);          /* read parameter list until end marker */ \
  while ((c = va_arg(ap, unsigned))!= SET_END)\
    if (c > max)              /* if this element is larger than max */ \
    max = c;                  /* then remember it */ \
  va_end(ap);                 /* end of parameter list processing */ \
  set_grow(dst, max / 8 + 1);  /* set size to be at least as long max */ \
}

#define SET_ITERATE_LIST(func)                                              \
{ \
  va_list ap;                 /* argument list walker */ \
  unsigned c;                 /* running argument copy */ \
  va_start(ap, dst);          /* read parameter list until end marker */ \
  while ((c = va_arg(ap, unsigned))!= SET_END)\
    func; \
  va_end(ap);                 /* end of parameter list processing */ \
}

#define SET_ITERATE_SET(func, length)                                       \
{ \
  unsigned count; \
  for (count = 0; count < length; count++) /* scan sets */ \
    func; \
}

/* return an array of (cardinality + 1) unsigned integers, being the element
   numbers of set src terminated by SET_END */
unsigned * set_array(const set_ * src)
{
  unsigned * running, 
  * block =(unsigned *) mem_malloc((set_cardinality(src)+ 1)* sizeof(unsigned)), 
  c,                          /* current set byte counter */
  element = 0;                /* running element number */
  
  running = block;            /* point running pointer at end of block */
  
  for (c = 0; c < src->length; c++) /* scan over all set bytes */
  {
    unsigned walker,          /* walking one */
    byte = src->elements[c];  /* get the current byte */
    
    for (walker = 1; walker < 256; walker <<= 1) /* walk a one across eight bits */
    {
      if ((byte & walker)!= 0) /* if this element is present in the set */
        * running++ = element; 
      
      element++; 
    }
  }
  
  * running = SET_END; 
  
  return block; 
}

/* return the number of elements in this set */
unsigned set_cardinality(const set_ * src)
{
  /* lookup table of number of bits set in a nibble */
  /* 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 */
  static const unsigned char bits[16]= {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4}; 
  
  unsigned cardinality = 0,   /* running cardinality counter */
  count;                      /* set element counter */
  
  for (count = 0; count < src->length; count++) /* scan over whole set */
  {
    cardinality += bits[src->elements[count]& 15];  /* add in cardinality of hi nibble */
    cardinality += bits[src->elements[count]>> 4];  /* add in cardinality of lo nibble */
  }
  return cardinality;         /* return result */
}

/* clear a dst and then set only those bits specified by src */
void set_assign_element(set_ * dst, const unsigned element) /* assign one element to a set */
{
  set_grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  dst->elements[element >> 3]|= mask[element & 7];  /* OR in the corresponding bit */
}

void set_assign_list(set_ * dst, ...) /* assign a list of elements to a set */
{
  SET_GROW_TO; 
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  SET_ITERATE_LIST((dst->elements[c >> 3]|= mask[c & 7])); 
}

void set_assign_set(set_ * dst, const set_ * src) /* assign one set to another */
{
  set_grow(dst, src->length);  /* set size to be at least as long as needed */
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  memcpy(dst->elements, src->elements, src->length);  /* memory-memory copy */
}

/* perform a comparison between sets, returning results like strcmp() */
int set_compare(set_ * dst, set_ * src)
{
  set_normalise(dst); 
  set_normalise(src); 
  
  if (dst->length != src->length)
    return dst->length > src->length; 
  else
    return strncmp((char *) dst->elements, (char *) src->elements, dst->length); 
}

/* remove from dst those elements in src */
void set_difference_element(set_ * dst, const unsigned element)
{
  set_grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  dst->elements[element >> 3]&=(unsigned char)(~mask[element & 7]);  /* mask off corresponding bit */
}

void set_difference_list(set_ * dst, ...)
{
  SET_GROW_TO; 
  SET_ITERATE_LIST((dst->elements[c >> 3]&=(unsigned char)(~mask[c & 7]))); 
}

void set_difference_set(const set_ * dst, const set_ * src)
{
  unsigned length = dst->length < src->length ? dst->length: src->length;  /* only iterate over shortest set */
  
  SET_ITERATE_SET((dst->elements[count]&=(unsigned char)(~src->elements[count])), length); 
}

/* free storage associated with a set's elements and return set to SET_NULL */
void set_free(set_ * dst)
{
  if (dst->elements != NULL)
    mem_free(dst->elements); 
  dst->length = 0; 
  dst->elements = NULL; 
}

void set_grow(set_ * dst, const unsigned length) /* test size of set and resize if necessary */
{
  if (dst->length < length)   /* if set is too small then resize */
  {
    /* belt and braces resize */
    unsigned char * temp =(unsigned char *) mem_calloc(length, 1); 
    
    if (dst->elements != NULL)
    {
      memcpy(temp, dst->elements, dst->length); 
      mem_free(dst->elements); 
    }
    dst->length = length; 
    dst->elements = temp; 
    
    #if 0
    dst->elements =(unsigned char *) mem_realloc(dst->elements, length); 
    memset(dst->elements + dst->length, 0, length - dst->length);  /* clear new bytes */
    dst->length = length;     /* and set new length accordingly */
    #endif
  }
}

int set_includes_element(set_ * dst, const unsigned element)
{
  set_grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  return(dst->elements[element >> 3]& mask[element & 7])!= 0; 
}

int set_includes_list(set_ * dst, ...)
{
  int isin = 1; 
  
  SET_GROW_TO; 
  SET_ITERATE_LIST((isin = isin &&((dst->elements[c >> 3]& mask[c & 7])!= 0))); 
  return(isin); 
}

int set_includes_set(const set_ * dst, const set_ * src)
{
  int isin = 1; 
  unsigned length = dst->length < src->length ? dst->length: src->length;  /* only iterate over shortest set */
  
  SET_ITERATE_SET((isin = isin &&((src->elements[count]| dst->elements[count])== dst->elements[count])), length); 
  
  if (src->length > dst->length) /* there may be more bits left! */
  {
    unsigned count; 
    
    for (count = length; count < src->length; count++)
      isin = isin &&(src->elements[count]== 0); 
  }
  return(isin); 
}

void set_intersect_element(set_ * dst, const unsigned element)
{
  unsigned char result; 
  
  set_grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  result = dst->elements[element >> 3]& mask[element & 7]; 
  memset(dst->elements, 0, dst->length);  /* clear the set to the empty set */
  dst->elements[element >> 3]= result; 
}

void set_intersect_list(set_ * dst, ...)
{
  set_ src = SET_NULL; 
  
  /* first we must build a set to hold the element list: copy set_assign_list */
  {
    va_list ap;               /* argument list walker */
    unsigned c,               /* running argument copy */
    max = 0;                  /* largest element */
    
    /* scan all the elements to find the largest one */
    va_start(ap, dst);        /* read parameter list until end marker */
    while ((c = va_arg(ap, unsigned))!= SET_END)
      if (c > max)            /* if this element is larger than max */
      max = c;                /* then remember it */
    va_end(ap);               /* end of parameter list processing */
    set_grow(& src, max / 8 + 1);  /* set size to be at least as long max */
  }
  memset(src.elements, 0, src.length);  /* clear the set to the empty set */
  SET_ITERATE_LIST((src.elements[c >> 3]|= mask[c & 7])); 
  
  set_intersect_set(dst, & src); 
  
  set_free(& src); 
}

void set_intersect_set(set_ * dst, const set_ * src)
{
  unsigned length = dst->length < src->length ? dst->length: src->length;  /* only iterate over shortest set */
  
  SET_ITERATE_SET((dst->elements[count]&= src->elements[count]), length); 
  /* Now clear rest of dst */
  if (length < dst->length)
    memset(dst->elements + length, 0, dst->length - length);  /* clear new bytes */
}

void set_invert(set_ * dst, const unsigned universe)
{
  /* lookup table of fill values for bits 0 - 7 */
  /* 0  1  2   3   4   5    6    7 */
  static const unsigned char fills[8]= {1, 3, 7, 15, 31, 63, 127, 255}; 
  
  unsigned top = universe / 8 + 1; 
  
  set_grow(dst, top);         /* set size to be at least as long as needed */
  SET_ITERATE_SET((dst->elements[count]^= 0xFF), dst->length); 
  dst->elements[top - 1]&= fills[universe % 8];  /* mask off last byte */
  memset(dst->elements + top, 0, dst->length - top);  /* clear extra bytes */
}

unsigned set_minimum_size(const unsigned minimum_size)
{
  if (minimum_size != SET_END) /* minimum = SET_END => query only */
    set_minimumsize = minimum_size;  /* set the minimum size */
  
  return set_minimumsize;     /* return current value */
}

void set_normalise(set_ * dst)
{
  if (dst->length < set_minimumsize) /* do we need to grow dst? */
    set_grow(dst, set_minimumsize);  /* grow to minimum size */
  else
  {
    unsigned char * p = dst->elements + dst->length - 1;  /* find last byte */
    
    if (* p == 0)             /* is there an empty byte at end? */
    {
      while (*(p--)== 0 && dst->length > set_minimumsize) /* run down zero bytes */
        dst->length--;        /* reducing length as we go */
      
      dst->elements =(unsigned char *) mem_realloc(dst->elements, dst->length);  /* and resize */
    }
  }
}

void set_print_element(const unsigned element, const char * element_names)
{
  if (element_names == NULL)  /* just print decimal set element numbers */
    text_printf("%u", element); 
  else
  {
    unsigned c; 
    
    /* skip to correct element name */
    for (c = element; c > 0; c--)
    {
/*      text_printf("\n%s", element_names); */  /* print the element name string */
      
      while (* element_names++ != 0)
        ; 
    }
    
    text_printf("%s", element_names);  /* print the element name string */
  }
}

void set_print_set(const set_ * src, const char * element_names, unsigned line_length)
{
  unsigned
  column = 0, 
  last_printed = 0, 
  not_first = 0, 
  * elements = set_array(src), 
  * base; 
  
  base = elements; 
  while (* elements != SET_END)
  {
    if (not_first)
      column += text_printf(", "); 
    else
      not_first = 1; 
    
    if (line_length != 0 && column >= line_length) /* we are past margin */
    {
      text_printf("\n"); 
      column = 0; 
    }
    
    if (element_names == NULL) /* just print decimal set element numbers */
      column += text_printf("%u", * elements++); 
    else
    {
      unsigned c; 
      
      /* skip to correct element name */
      for (c = * elements - last_printed; c > 0; c--)
        while (* element_names++ != 0)
        ; 
      
      column += text_printf("%s", element_names);  /* print the element name string */
      last_printed = * elements++;  /* remember where we are for next time */
    }
  }
  mem_free(base);             /* release memory block */
}

void set_unite_element(set_ * dst, const unsigned element)
{
  set_grow(dst, element / 8 + 1);  /* set size to be at least as long as needed */
  
  dst->elements[element >> 3]|= mask[element & 7];  /* OR in the corresponding bit */
}

void set_unite_list(set_ * dst, ...)
{
  SET_GROW_TO; 
  SET_ITERATE_LIST((dst->elements[c >> 3]|= mask[c & 7])); 
}

void set_unite_set(set_ * dst, const set_ * src)
{
  set_grow(dst, src->length);  /* set size to be at least as long as needed */
  SET_ITERATE_SET((dst->elements[count]|= src->elements[count]), src->length); 
}

/* End of set.c */
