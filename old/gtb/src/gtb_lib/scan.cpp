/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* scan.c - support routines for the scanner
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <limits.h>
#include "set.h"
#include "graph.h"
#include "memalloc.h"
#include "symbol.h"
#include "textio.h"
#include "scan.h"

/* #define show_vcg_atom_numbers */

void * scan_table = NULL;
int scan_case_insensitive = 0;
int scan_newline_visible = 0;
int scan_show_skips = 0;
int scan_symbol_echo = 0;
int scan_adsp_integers = 0;
int scan_lexicalise_flag = 0;

char * scan_token_names = NULL;

void scan_allow_adsp_integers(int allow)
{
  scan_adsp_integers = allow;
}

void scan_lexicalise(void)
{
  scan_lexicalise_flag = 1;
}

void scan_init(const int case_insensitive, const int newline_visible, const int show_skips, const int symbol_echo, char * token_names)
{
  scan_case_insensitive = case_insensitive;
  scan_show_skips = show_skips;
  scan_newline_visible = newline_visible;
  scan_symbol_echo = symbol_echo;
  scan_token_names = token_names;

  scan_comment_list = (scan_comment_block*) mem_calloc(1, sizeof(scan_comment_block));
  scan_comment_list_end = scan_comment_list;
  text_scan_data = mem_malloc(sizeof(scan_data));

  scan_table = symbol_new_table("scan table", 101, 31, symbol_compare_string, symbol_hash_string, symbol_print_string);

  scan_insert_comment_block("",0,0); 
}

void scan_load_keyword(char * id1, const char * id2, const int token, const int extended)
{
  scan_data * d =(scan_data *) symbol_new_symbol(sizeof(scan_data));

  d->id = text_insert_string(id1);


  if (id2 != NULL)            /* If this has a secondary string */
    text_insert_string(id2);

  d->token = token;
  d->extended = extended;

  symbol_insert_symbol(scan_table, d);
}

static void scan_skip(set_ * stop) /* scan until a token in stop set appears */
{
  while (!set_includes_element(stop, SCAN_CAST->token))
    scan_();                  /* Don't add tokens to tree when skipping! */
  
  if (scan_show_skips)
    text_message(TEXT_ERROR_ECHO, "Skipping to...\n"); 
}

int scan_test(const char * production, const int valid, set_ * stop)
{
  if (valid != SCAN_CAST->token)
  {
    if (stop != NULL)
    {
      if (production != NULL)
        text_message(TEXT_ERROR_ECHO, "In rule \'%s\', scanned ", production); 
      else
        text_message(TEXT_ERROR_ECHO, "Scanned ");
      set_print_element(SCAN_CAST->token, scan_token_names); 
      text_printf(" whilst expecting "); 

      set_print_element(valid, scan_token_names); 
      text_printf("\n");
      scan_skip(stop); 
    }
    return 0; 
  }
  else
    return 1; 
}

int scan_test_set(const char * production, set_ * valid, set_ * stop)
{
  if (!set_includes_element(valid, SCAN_CAST->token))
  {
    if (stop != NULL)
    {
      if (production != NULL)
        text_message(TEXT_ERROR_ECHO, "In rule \'%s\', scanned ", production); 
      else
        text_message(TEXT_ERROR_ECHO, "Scanned ");
      set_print_element(SCAN_CAST->token, scan_token_names); 
      text_printf(" whilst expecting %s", set_cardinality(valid)== 1 ? "": "one of ");
      set_print_set(valid, scan_token_names, 60); 
      text_printf("\n"); 
      scan_skip(stop); 
    }
    return 0;
  }
  else
    return 1;
}

int scan_token_number(char *keyword)
{
  scan_data *this_scanner_symbol;

  if ((this_scanner_symbol = (scan_data*) symbol_lookup_key(scan_table, &keyword, NULL)) == NULL)
    return SCAN_P_IGNORE;
  else
    return this_scanner_symbol->token;
}

void scan_vcg_print_edge(const void * edge)
{
  static unsigned long edge_count = 0;
  /* Set user inserted edges to red */
  if (*((int*) edge) == 0)
    text_printf("color:red");

#if defined(show_vcg_atom_numbers)
  text_printf("label:\"%lu\" ", graph_atom_number(edge));
#endif
  text_printf(" horizontal_order:%lu", 1000 - ++edge_count);
}

void scan_vcg_print_node(const void * node)
{
  text_printf("label:\"");

#if defined(show_vcg_atom_numbers)
  text_printf("%lu: ", graph_atom_number(node));
#endif

  switch (((scan_data *) node)->token)
  {
    case SCAN_P_IGNORE:
    case SCAN_P_ID: text_print_C_string(((scan_data *) node)->id); break;
    case SCAN_P_INTEGER:
    text_printf("INTEGER: %lu", ((scan_data *) node)->data.i); break;
    case SCAN_P_REAL: text_printf("REAL: %lf", ((scan_data *) node)->data.r); break;

    case SCAN_P_STRING:
    case SCAN_P_STRING_ESC:
    case SCAN_P_COMMENT:
    case SCAN_P_COMMENT_VISIBLE:
    case SCAN_P_COMMENT_NEST:
    case SCAN_P_COMMENT_NEST_VISIBLE:
    case SCAN_P_COMMENT_LINE:
    case SCAN_P_COMMENT_LINE_VISIBLE: text_printf("\\\""); text_print_C_string(((scan_data *) node)->id); text_printf("\\\""); break;

    case SCAN_P_EOF: text_printf("EOF"); break;
    case SCAN_P_EOLN: text_printf("EOLN"); break;
    default: text_printf("\'");
    text_print_C_string(((scan_data *) node)->id);
    text_printf("\'");
    break;
  }

  text_printf("\" horizontal_order:%lu", graph_atom_number(node));

  if (((scan_data *) node)->token == SCAN_P_IGNORE) /* make nonterminals round */
    text_printf(" shape:ellipse ");
}

/* End of scan.c */
