/*******************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* rdp_gram.h - rdp grammar checking routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef RDP_GRAM_H
#define RDP_GRAM_H

int rdp__bad_grammar(void * base);
int rdp__is_valid_C_id(char *s);

#endif

/* End of rdp_gram.h */
