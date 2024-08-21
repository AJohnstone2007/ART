/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_brnglr_prs.h - an BRNGLR parser, as per the paper
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef GTB_BRNGLR_PRS_H
#define GTB_BRNGLR_PRS_H

#include "gtb_sr.h"
derivation *sr_brnglr_parse(dfa * this_dfa, char *string, int reduction_stack_size);
#endif

