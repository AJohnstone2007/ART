/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 27 April 2004
*
* gtb_lex.h - lexical analysis functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_LEX_H
#define GTB_LEX_H

#include "gtb_gram.h"

#define LEX(this_symbol) { this_symbol = lex_lex(); \
if (this_symbol == GRAM_ILLEGAL_SYMBOL_NUMBER)\
{ \
  text_printf("Illegal lexical element detected\n"); \
  return 0; \
} }

void lex_init(char *string, grammar *this_grammar);
int lex_lex(void);

#endif



