/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Jan 1995
*
* symtest.c - test all sym.c routines. See symtest.ok for correct output
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include "stdio.h"
#include "symbol.h"

typedef struct data_struct {char *id; int token;} data;

int main(void)
{
  void *table1 = symbol__new_table("Test table 1", 7,3, symbol__compare_string, symbol__hash_string, symbol__print_string);

  data *tmp = (data*) symbol__new_symbol(sizeof(data)),
   *scope1, *chain;

  tmp->id = "manually built";

  symbol__insert_symbol(table1, tmp);

  symbol__print_symbol(table1, tmp);

  symbol__print_table(table1);

  symbol__insert_string_key(table1, "manually built", sizeof(data));

  symbol__print_table(table1);

  symbol__insert_string_key(table1, "adrian", sizeof(data));
  symbol__insert_string_key(table1, "was", sizeof(data));
  symbol__insert_string_key(table1, "here", sizeof(data));
  symbol__insert_string_key(table1, "and", sizeof(data));
  symbol__insert_string_key(table1, "here", sizeof(data));



  scope1 = (data*) symbol__new_scope(table1, "nested scope");
  symbol__insert_string_key(table1, "adrian", sizeof(data));
  symbol__insert_string_key(table1, "was", sizeof(data));
  symbol__insert_string_key(table1, "here", sizeof(data));
  symbol__insert_string_key(table1, "and", sizeof(data));
  symbol__insert_string_key(table1, "here", sizeof(data));

  chain = (data*) symbol__lookup_key(table1, "here");
  symbol__print_symbol(table1, chain);

  while ((chain  = (data*) symbol__next_symbol(table1, chain)) != NULL)
  {
    printf("Found another: ");
    symbol__print_symbol(table1, chain);
  }

  symbol__print_table_statistics(table1, 11);

  symbol__print_table(table1);

  symbol__sort_scope(table1, scope1);

  symbol__print_table(table1);

  symbol__set_scope(table1, scope1);

  symbol__unlink_symbol(tmp);

  symbol__print_table(table1);

  symbol__unlink_scope(scope1);
  symbol__print_table(table1);

  return 0;
}


