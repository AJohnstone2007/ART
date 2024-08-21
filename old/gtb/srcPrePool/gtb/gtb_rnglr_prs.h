/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_rnglr_prs.h - an RNGLR parser, as per the TOPLAS paper
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef GTB_RNGLR_PRS_H
#define GTB_RNGLR_PRS_H

#include "gtb_sr.h"
derivation *sr_rnglr_parse(dfa * this_dfa, char *string, int reduction_stack_size);
#endif

