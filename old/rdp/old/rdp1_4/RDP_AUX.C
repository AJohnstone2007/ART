/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Jan 1995
*
* rdp_aux.c - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <stdio.h>
#include "memalloc.h"
#include "scan.h"
#include "set.h"
#include "symbol.h"
#include "textio.h"
#include "rdp_aux.h"
#include "rdp_gram.h"
#include "rdp_prnt.h"
#include "rdp.h"

extern void *codes, *rdp, *tokens;

void *rdp__base;                 /* symbol table for the parser */
extern char* rdp__sourcefilename; /* source file name loaded by main line */

char
 rdp__force = 0,                /* force output files flag */
 rdp__error_production_name = 0,/* flag to force writing of production name into error messages */
 rdp__expanded = 0,             /* flag to generate expanded bnf listing */
 rdp__parser_only = 0,          /* omit semantic actions flag */
*rdp__primary_id;               /* identifier for parent production */

unsigned rdp__component;        /* sub-production component number */
unsigned rdp__token_count = T__TOP; /* number of tokens + extendeds */

rdp__data * rdp__start_prod;

char
*rdp__dir_title = "rdparser",   /* string from TITLE directive */
*rdp__dir_suffix = "",          /* string from SUFFIX directive */
*rdp__dir_pre_parse = NULL,     /* string from PRE_PARSE directive */
*rdp__dir_post_parse = NULL,    /* string from POST_PARSE directive */
*rdp__dir_output_file = "a.out";/* string from OUTPUT_FILE directive */

unsigned
 rdp__dir_interpreter = 0,        /* TEXT_MODE flag */
 rdp__dir_case_insensitive = 0, /* CASE_INSENSITIVE flag */
 rdp__dir_show_skips = 0,       /* SHOW_SKIPS flag */
 rdp__dir_newline_visible = 0,  /* NEWLINE_VISIBLE flag */
 rdp__dir_passes = 1,           /* PASSES directive */
 rdp__dir_hash_size = 101,      /* HASH_SIZE directive */
 rdp__dir_hash_prime = 31,      /* HASH_PRIME dirctive */
 rdp__dir_tab_width = 8,        /* TAB_WIDTH directive */
 rdp__dir_max_errors = 25,      /* MAX_ERRORS directive */
 rdp__dir_max_warnings = 100;   /* MAX_WARNINGS directive */

unsigned long rdp__dir_text_size = 20000;    /* TEXT_SIZE directive */

rdp__string_list
*rdp__dir_help = NULL,          /* strings from HELP directives */
*rdp__dir_include = NULL;       /* strings from INCLUDE directives */

rdp__table_list
*rdp__dir_symbol_table = NULL;    /* data from SYMBOL_TABLE directives */

set__
rdp__production_set,
rdp__empty_production_set;

rdp__data *rdp__find(char *id, kind_type kind, symbol_type symbol)
{
  rdp__data* temp;
  void * table;

  /* Figure out which table to use */
  if (kind == K__CODE)
    table = codes;
  if (kind == K__TOKEN || kind == K__EXTENDED)
    table = tokens;
  else table = rdp;

  if ((temp = (rdp__data*) symbol__lookup_key(table, id)) == NULL)
  {
    if (symbol == RDP__OLD)
      text__message(TEXT__ERROR_ECHO, "undeclared symbol '%s'\n", id);
    temp = (rdp__data*) symbol__new_symbol(sizeof(rdp__data));
    temp->id = id;
    symbol__insert_symbol(table, temp);
    temp->token = T__ID;
    temp->kind = kind;
    temp->first_cardinality = 0;
    set__assign_element(&temp->follow, T__EOF);
    temp->follow_cardinality = 1;
    temp->return_type_stars = 0;
    switch (kind)
    {
    case K__INTEGER:
      temp->return_type = "long int";
      break;
    case K__TOKEN:
      temp->return_type = "int";
      break;
    case K__REAL:
      temp->return_type = "double";
      break;
    case K__STRING:
      temp->return_type = "char";
      temp->return_type_stars = 1;
      break;
    default:
      temp->return_type = "void";
      break;
    }
  }
  else
    if (symbol == RDP__NEW)
      text__message(TEXT__ERROR_ECHO, "doubly declared symbol '%s'\n", id);

  return temp;
}

rdp__data *rdp__find_extended(char *open, char *close, int token)
{
  rdp__data *result;

  rdp__check_token_valid(open);
  rdp__check_token_valid(close);
  result = rdp__find(open, K__EXTENDED, RDP__ANY);
  result->token_value = token;
  result->close = close;
  result->return_type = "char";
  result->return_type_stars = 1;
  return result;
}


void rdp__pre_parse(void)
{
  rdp__base = symbol__new_scope(rdp, "parser");
  set__assign_list(&rdp__production_set, K__PRIMARY, K__SEQUENCE, K__DO_FIRST, K__LIST, K__CONDITIONAL, K__ITERATION, SET__END);
  set__assign_list(&rdp__empty_production_set, K__LIST, K__CONDITIONAL, K__ITERATION, SET__END);

  rdp__find("ID", K__STRING, RDP__ANY)->token_value = T__ID; /* add predefined primitive productions */
  rdp__find("INTEGER", K__INTEGER, RDP__ANY)->token_value = T__INTEGER;
  rdp__find("REAL", K__REAL, RDP__ANY)->token_value = T__REAL;
  rdp__find("EOLN", K__INTEGER, RDP__ANY)->token_value = T__EOLN;
}

static void rdp__order_tokens(void * base)
{
  rdp__data *temp = (rdp__data*) symbol__next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (temp->kind == K__TOKEN)
    {
      temp->token_value = rdp__token_count++;
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }

  /* now set up start sets for tokens, code and primitives */
  temp = (rdp__data*) symbol__next_symbol_in_scope(base);
  while (temp != NULL)
  {
    if (temp->kind == K__TOKEN || temp->kind == K__INTEGER ||
	temp->kind == K__REAL || temp->kind == K__STRING ||
	temp->kind == K__EXTENDED)
    {
      set__unite_element(&temp->first, temp->token_value);
      temp->first_cardinality = set__cardinality(&temp->first);
      set__unite_element(&temp->follow, T__EOF);
      temp->follow_cardinality = set__cardinality(&temp->follow);

      temp->first_done = 1;
    }
    else if (temp->kind == K__LIST)
    {
      set__unite_element(&temp->follow, temp->supplementary_token->token_value);
      temp->follow_cardinality = set__cardinality(&temp->follow);
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
}

static void rdp__add_continuations(void * base)
{
  rdp__data *temp = (rdp__data*) symbol__next_symbol_in_scope(base);
  char *last_token = "";        /* remember most recent token name */
  int tokens_added = 0;

  if (rdp__verbose)
    text__message(TEXT__INFO, "checking for continuation tokens\n");

  while (temp != NULL)          /* scan over all productions */
  {
    if (temp->kind == K__TOKEN)
    {
      char *lo = last_token,
      *hi = temp->id;

      if (!rdp__is_valid_C_id(hi))  /* ignore identifiers */
      {
	/* rdp__find common prefix */
	while (*lo == *hi && *hi != 0)  /* bump while they are identical */
	{
	  lo++;
	  hi++;
	}

	hi++;                   /* we can't have two identical tokens, so at worst this will move to a null */

	/* now add continuations */
	/* is the first non-identical character the one before the terminating null ? */
	while (*(hi) != 0)      /* add a continuation */
	{
	  /* insert the sub-string */
	  char *c = temp->id,
	  *continuation_name = text__top;  /* remember start position */

	  while (c != hi)
	    text__insert_char(*c++);  /* copy identifier */
	  text__insert_char(0); /* add a terminating null */

	  if (rdp__verbose)
	    text__message(TEXT__INFO, "adding continuation token '%s'\n", continuation_name);

	  tokens_added = 1;

	  rdp__find(continuation_name, K__TOKEN, RDP__ANY);

	  hi++;
	}
      }

      last_token = temp->id;
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
  if (rdp__verbose && !tokens_added)
    text__message(TEXT__INFO, "no continuation tokens needed\n");

}

void rdp__post_parse(char *outputfilename, char force)
{
  void
   *tokens_base = symbol__get_scope(tokens),
   *rdp_base = symbol__get_scope(rdp);

  locals__data* local = (locals__data*) symbol__new_symbol(sizeof(locals__data));

  local->id = "result";
  symbol__insert_symbol(locals, local);

  symbol__sort_scope(tokens, tokens_base);     /* sort productions into alphabetical order */
  rdp__add_continuations(tokens_base); /* scan through tokens and add any necessry continuations */
  symbol__sort_scope(tokens, tokens_base);     /* re-sort productions into alphabetical order */
  rdp__order_tokens(tokens_base);      /* apply token numbers to token productions */
  rdp__order_tokens(rdp_base);      /* apply token numbers to token productions */
  rdp__make_token_string(tokens_base); /* make a string with all token names in it */

  symbol__sort_scope(rdp, rdp_base);     /* sort productions into alphabetical order */
  rdp__bad_grammar(rdp_base);       /* find the non-LL(1)-isms */

  if (rdp__expanded)            /* print out expanded BNF */
    rdp__dump_extended(rdp__base);

  if (text__total_errors()>0)
  {
    if (force)
      text__message(TEXT__WARNING, "grammar is not LL(1) but -F switch set: writing files\n");
    else
      text__message(TEXT__FATAL, "run aborted without creating output files - rerun with -F to override\n");
  }

  rdp__print_header(text__force_filetype(outputfilename, "h")); /* dump header file */

  rdp__print_parser(text__force_filetype(outputfilename, "c"), rdp_base); /* dump main file */

  if (rdp__verbose)             /* see how much text buffer space we used */
    text__print_statistics();

  text__print_total_errors();
}

/* End of rdp_aux.c */
