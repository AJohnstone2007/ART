/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 28 September 2000
*
* gtb_gp.h - generalised parser functions (not GLR: see gtb_sr.cpp for those)
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef GTB_GP_H
#define GTB_GP_H

struct cyk_table
{
  bool accept;
};

derivation* gp_earley_recognise(grammar *this_grammar, char *string, int lookahead);
derivation* gp_earley_parse(grammar *this_grammar, char *string, int lookahead);
cyk_table* gp_cyk_recognise(grammar *this_grammar, char *string);
#endif

