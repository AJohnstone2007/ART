/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* scan.c - support routines for the scanner
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "memalloc.h"
#include "symbol.h"
#include "set.h"
#include "textio.h"
#include "scan.h"

void * scan__table = NULL;
int scan__case_insensitive = 0;
int scan__newline_visible = 0;
int scan__show_skips = 0;
int scan__symbol_echo = 0;
static char *scan__token_names = NULL;

void scan__init(const int case_insensitive, const int newline_visible, const int show_skips, const int symbol_echo, char *token_names)
{
  scan__case_insensitive = case_insensitive;
  scan__show_skips = show_skips;
  scan__newline_visible = newline_visible;
  scan__symbol_echo = symbol_echo;
  scan__token_names = token_names;

  text__scan_data = mem__malloc(sizeof(scan__data));

  scan__table = symbol__new_table("scan table", 101,31, symbol__compare_string, symbol__hash_string, symbol__print_string);

}

void scan__load_keyword(char *id1, const char *id2, const int token)
{
  scan__data * d = (scan__data*) symbol__new_symbol(sizeof(scan__data));

  d->id = text__insert_string(id1);

  if (id2 != NULL)              /* If this has a secondary string */
    text__insert_string(id2);

  d->token = token;

  symbol__insert_symbol(scan__table, d);
}

static void scan__skip(set__ * stop)  /* scan until a token in stop set appears */
{
  while (!set__includes_element(stop, SCAN__CAST->token))
    scan__();

  if (scan__show_skips)
    text__message(TEXT__INFO_ECHO, "Skipping to...\n");
}

int scan__test(const unsigned valid, set__ * stop)
{
  if (valid != SCAN__CAST->token)
  {
    if (stop != NULL)
    {
      text__message(TEXT__ERROR_ECHO, "Scanned ");
      set__print_element(SCAN__CAST->token, scan__token_names);
      text__printf(" whilst expecting ");
      set__print_element(valid, scan__token_names);
      text__printf("\n");
      scan__skip(stop);
    }
    return 0;
  }
  else
    return 1;
}

int scan__test_set(set__ * valid, set__ * stop)
{
  if (!set__includes_element(valid, SCAN__CAST->token))
  {
    if (stop != NULL)
    {
      text__message(TEXT__ERROR_ECHO, "Scanned ");
      set__print_element(SCAN__CAST->token, scan__token_names);
      text__printf(" whilst expecting one of ");
      set__print_set(stop, scan__token_names);
      text__printf("\n");
      scan__skip(stop);
    }
    return 0;
  }
  else
    return 1;
}

/* End of scan.c */
