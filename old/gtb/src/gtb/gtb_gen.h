/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 21 Sep 2000
*
* gtb_gen.h - generator functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef GTB_GEN_H
#define GTB_GEN_H
struct gen_string_symbol
{
  unsigned *id;
};

int gen_string_nonterminal_count(grammar * this_gram, unsigned *string);
void *gen_generate(grammar * this_gram, gram_symbols_data *start_symbol, unsigned long limit, unsigned order, int show_all_sentential_forms, int quiet);
#endif

