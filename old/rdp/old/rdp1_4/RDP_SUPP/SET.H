/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* set.h - dynamically resizable set handling
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef SET__H
#define SET__H

#include <limits.h>
#include <stddef.h>

typedef struct
{
  unsigned length;
  unsigned char *elements;
} set__;

#define SET__NULL {0, NULL}     /* a null set */
#define SET__END UINT_MAX       /* an impossible set value */

unsigned *set__array(const set__ * src);

unsigned set__cardinality(const set__ * src);

void set__assign_element(set__ * dst, const unsigned element);
void set__assign_list(set__ * dst,...);
void set__assign_set(set__ * dst, const set__ * src);

int set__compare(set__ * dst, set__ * src);

void set__difference_element(set__ * dst, const unsigned element);
void set__difference_list(set__ * dst,...);
void set__difference_set(const set__ * dst, const set__ * src);

void set__free(set__ * dst);

void set__grow(set__ * dst, const unsigned length);

int set__includes_element(set__ * dst, const unsigned element);
int set__includes_list(set__ * dst,...);
int set__includes_set(const set__ * dst, const set__ * src);

void set__intersect_element(set__ * dst, const unsigned element);
void set__intersect_list(set__ * dst,...);
void set__intersect_set(set__ * dst, const set__ * src);

void set__invert(set__ * dst, const unsigned universe);

unsigned set__minimum_size(const unsigned minimum_size);

void set__normalise(set__ * dst);

void set__print_element(const unsigned element, const char *element_names);
void set__print_set(const set__ * src, const char *element_names);

void set__unite_element(set__ * dst, const unsigned element);
void set__unite_list(set__ * dst,...);
void set__unite_set(set__ * dst, const set__ * src);

#endif

/* End of set.h */
