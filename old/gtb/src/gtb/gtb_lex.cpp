/*****************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 27 April 2004
*
* gtb_lex.cpp - lexical analysis functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#include "gtb_gram.h"
#include "gtb_scr.h"
#include <ctype.h>
#include "gtb_lex.h"

//#define LEXER_TRACE

static char *ip;
static int lexer_count;
static void* lex_symbol_table;
static char* lex_whitespace;
static int lex_whitespace_symbol_number = 0;

void lex_init(char *string, grammar *this_grammar)
{
  lexer_count = 1;
  lex_symbol_table = this_grammar->symbol_table;
  ip = string;

  lex_whitespace = script_symbol_find("lex_whitespace")->value->v_STRING;

  if (*lex_whitespace != 0)
    lex_whitespace_symbol_number = gram_find_symbol_by_id(this_grammar, lex_whitespace, SCRIPT_NS_TERMINAL)->symbol_number;

}

int lex_lex(void)
{
  gram_symbols_data *this_symbol,
  *longest_symbol;
  int longest_length = 0;

  if (!isgraph(*ip) && *ip != 0)
  {
    while (!isgraph(*ip) && *ip != 0)
      ip++;
    if (lex_whitespace_symbol_number != 0)
    {
      if (*ip == 0)
        return GRAM_EOS_SYMBOL_NUMBER;
      else
       return lex_whitespace_symbol_number;
    }
  }


  if (*ip == 0)
  {
    if (script_gtb_verbose())
    {
#if defined(LEXER_TRACE)
    text_printf("Lex: EOS\n");
#endif
    }
    return GRAM_EOS_SYMBOL_NUMBER;
  }

  for (this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(symbol_get_scope(lex_symbol_table));
       this_symbol != NULL;
       this_symbol = (gram_symbols_data *) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->name_space == SCRIPT_NS_TERMINAL)
    {
      int this_length = strlen(this_symbol->id);

      if (strncmp(this_symbol->id, ip, this_length) == 0 && this_length > longest_length)
      {
        longest_symbol = this_symbol;
        longest_length = this_length;
      }
    }

  if (longest_length > 0)
  {
    if (script_gtb_verbose())
    {
#if defined(LEXER_TRACE)
      text_printf("Lex: %i \'%s\'\n", longest_symbol->symbol_number, longest_symbol->id);
#endif
    }
    ip += longest_length;
    return longest_symbol->symbol_number;
  }
  else
  {
    if (script_gtb_verbose())
    {
#if defined(LEXER_TRACE)
      text_printf("Lex: no match\n");
#endif
    }
    return GRAM_ILLEGAL_SYMBOL_NUMBER;
  }
}

