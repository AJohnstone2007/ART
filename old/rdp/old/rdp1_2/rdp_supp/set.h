/****************************************************************************
*
* RDP release 1.20 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 6 Nov 1994  
*
* set.h - Pascal style set handling
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef SET_H
#define SET_H

typedef unsigned char *set__;

extern unsigned set__size;

#define SET_END set__size

void set__init(unsigned size);
set__ set__make(void);
set__ set__construct(unsigned e,...);
void set__invert(set__ s);
void set__clear(set__ s);
unsigned set__cardinality(set__ s);
void set__intersect_set(set__ dst, set__ src);
void set__assign_set(set__ dst, set__ src);
void set__assign_element(set__ dst, unsigned element);
void set__assign_list(set__ s,...);
void set__unite_set(set__ dst, set__ src);
void set__unite_element(set__ dst, unsigned element);
void set__unite_list(set__ s,...);
void set__remove_set(set__ dst, set__ src);
void set__remove_element(set__ dst, unsigned element);
void set__remove_list(set__ s,...);
int set__inc_set(set__ dst, set__ src);
int set__inc_element(set__ s, unsigned element);
int set__inc_list(set__ s,...);
void set__print(char *msg, set__ s);

#endif

/* End of set.h */
