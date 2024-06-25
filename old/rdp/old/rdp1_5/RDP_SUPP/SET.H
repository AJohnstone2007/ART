/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* set.h - dynamically resizable set handling
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef SET_H
#define SET_H

#include <limits.h>
#include <stddef.h>

typedef struct
{
  unsigned length; 
  unsigned char * elements; 
}set_; 

#define SET_NULL {0, NULL}     /* a null set */
#define SET_END UINT_MAX       /* an impossible set value */

unsigned * set_array(const set_ * src); 

unsigned set_cardinality(const set_ * src); 

void set_assign_element(set_ * dst, const unsigned element); 
void set_assign_list(set_ * dst, ...); 
void set_assign_set(set_ * dst, const set_ * src); 

int set_compare(set_ * dst, set_ * src); 

void set_difference_element(set_ * dst, const unsigned element); 
void set_difference_list(set_ * dst, ...); 
void set_difference_set(const set_ * dst, const set_ * src); 

void set_free(set_ * dst); 

void set_grow(set_ * dst, const unsigned length); 

int set_includes_element(set_ * dst, const unsigned element); 
int set_includes_list(set_ * dst, ...); 
int set_includes_set(const set_ * dst, const set_ * src); 

void set_intersect_element(set_ * dst, const unsigned element); 
void set_intersect_list(set_ * dst, ...); 
void set_intersect_set(set_ * dst, const set_ * src); 

void set_invert(set_ * dst, const unsigned universe); 

unsigned set_minimum_size(const unsigned minimum_size); 

void set_normalise(set_ * dst); 

void set_print_element(const unsigned element, const char * element_names); 
void set_print_set(const set_ * src, const char * element_names, unsigned line_length); 

void set_unite_element(set_ * dst, const unsigned element); 
void set_unite_list(set_ * dst, ...); 
void set_unite_set(set_ * dst, const set_ * src); 

#endif

/* End of set.h */
