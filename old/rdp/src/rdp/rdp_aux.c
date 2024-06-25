/****************************************************************************
*
* RDP release 1.70 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 16 March 2012
*
* rdp_aux.c - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <string.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include "graph.h"
#include "memalloc.h"
#include "scan.h"
#include "set.h"
#include "symbol.h"
#include "textio.h"
#include "rdp_aux.h"
#include "rdp_gram.h"
#include "rdp_prnt.h"
#include "rdp.h"

extern void * codes, * rdp, * tokens;

void * rdp_base;              /* symbol table for the parser */
extern char * rdp_sourcefilename;  /* source file name loaded by main line */


int
rdp_comment_only = 0,         /* Flag to track productions that contain only comments */
rdp_rule_count = 0,           /* Number of rules declared * 2 */
rdp_force = 0,                /* force output files flag */
rdp_kleene = 0,               /* flag to output Kleene closure of tokens grammar */
rdp_error_production_name = 0,  /* flag to force writing of production name into error messages */
rdp_expanded = 0,             /* flag to generate expanded bnf listing */
rdp_undeclared_symbols_are_tokens,  /* convert symbols flag */
rdp_parser_only = 0,          /* omit semantic actions flag */
rdp_trace = 0;                /* add trace messages flag */

char
* rdp_primary_id;             /* identifier for parent production */

unsigned rdp_component;       /* sub-production component number */
unsigned rdp_token_count = SCAN_P_TOP;  /* number of tokens + extendeds */

rdp_data * rdp_start_prod;

char
* rdp_dir_title = "rdparser",  /* string from TITLE directive */
* rdp_dir_suffix = "",        /* string from SUFFIX directive */
* rdp_dir_pre_parse = NULL,   /* string from PRE_PARSE directive */
* rdp_dir_post_parse = NULL,  /* string from POST_PARSE directive */
* rdp_dir_global = NULL,      /* string from GLOBAL directive */
* rdp_dir_output_file = NULL,  /* string from OUTPUT_FILE directive */
* rdp_dir_tree_edge_fields = "",   /* fild names for tree edge from TREE directive */
* rdp_dir_tree_node_fields = "";   /* fild names for tree node from TREE directive */

unsigned
rdp_dir_derivation_tree = 0,         /* DERIVATION_TREE flag */
rdp_dir_tree = 0,                    /* TREE flag */
rdp_dir_epsilon_tree = 0,            /* EPSILON_TRTEE tree flag */
rdp_dir_annotated_epsilon_tree = 0,  /* ANNOTATED_EPSILON_TRTEE tree flag */
rdp_dir_case_insensitive = 0,  /* CASE_INSENSITIVE flag */
rdp_dir_show_skips = 0,       /* SHOW_SKIPS flag */
rdp_dir_newline_visible = 0,  /* NEWLINE_VISIBLE flag */
rdp_dir_multiple_source_files = 0, /* MULTIPLE_SOURCE_FILES flag */
rdp_dir_passes = 1,           /* PASSES directive */
rdp_dir_hash_size = 101,      /* HASH_SIZE directive */
rdp_dir_hash_prime = 31,      /* HASH_PRIME dirctive */
rdp_dir_retain_comments,      /* RETAIN_COMMENTS directive */
rdp_dir_max_errors = 25,      /* MAX_ERRORS directive */
rdp_dir_max_warnings = 100;   /* MAX_WARNINGS directive */

unsigned long rdp_dir_text_size = 35000l;  /* TEXT_SIZE directive */
unsigned long rdp_dir_tab_width = 8;  /* TAB_WIDTH directive */

rdp_string_list
* rdp_dir_include = NULL;     /* strings from INCLUDE directives */

rdp_arg_list
* rdp_dir_args = NULL;        /* data from ARG_* directives */

rdp_table_list
* rdp_dir_symbol_table = NULL;  /* data from SYMBOL_TABLE directives */

rdp_index_table_list
* rdp_dir_index_table = NULL;  /* data from SYMBOL_TABLE directives */

set_
rdp_production_set;

void rdp_add_arg(enum arg_kind_type kind, char * key, char * var, char * desc)
{
  rdp_arg_list * temp =(rdp_arg_list *) mem_malloc(sizeof(rdp_arg_list));

  temp->kind = kind;
  temp->key = key;
  temp->var = var;
  temp->desc = desc;
  temp->next = rdp_dir_args;
  rdp_dir_args = temp; 
}

rdp_data * rdp_find(char * id, kind_type kind, symbol_type symbol)
{
  rdp_data * temp;
  void * table; 
  
  /* Figure out which table to use */
  if (kind == K_CODE)
    table = codes;
  else if (kind == K_TOKEN || kind == K_EXTENDED)
    table = tokens; 
  else table = rdp;

  if ((temp =(rdp_data *) symbol_lookup_key(table, & id, NULL))== NULL)
  {
    if (symbol == RDP_OLD && rdp_undeclared_symbols_are_tokens)
    {
      text_message(TEXT_WARNING_ECHO, "Undeclared symbol \'%s\' converted to token\n", id);
      rdp_process_token(id); 
    }
    else
    {
      if (symbol == RDP_OLD)
        text_message(TEXT_ERROR_ECHO, "Undeclared symbol \'%s\'\n", id);
      temp =(rdp_data *) symbol_new_symbol(sizeof(rdp_data)); 
      temp->id = id; 
      symbol_insert_symbol(table, temp); 
      temp->token = SCAN_P_ID; 
      temp->kind = kind; 
      temp->hi = temp->lo = 1;  /* set instance numbers to one */
      temp->first_cardinality = 0;
      set_assign_element(& temp->follow, SCAN_P_EOF);
      temp->follow_cardinality = 1;
      temp->return_type_stars = 0; 
      switch (kind)
      {
        case K_INTEGER: 
        temp->return_type = "long int"; 
        break; 
        case K_REAL:
        temp->return_type = "double";
        break;
        case K_TOKEN:
        case K_STRING:
        temp->return_type = "char";
        temp->return_type_stars = 1;
        break;
        default:
        temp->return_type = "void";
        break;
      }
    }
  }
  else
    if (symbol == RDP_NEW)
    text_message(TEXT_ERROR_ECHO, "Doubly declared symbol \'%s\'\n", id);

  return temp;
}

rdp_data * rdp_find_extended(char * open, char * close, int token)
{
  rdp_data * result;

  rdp_check_token_valid(open);
  rdp_check_token_valid(close);
  result = rdp_find(open, K_EXTENDED, RDP_ANY);
  result->token_value = token;
  result->close = close;
  result->return_type = "char";
  result->return_type_stars = 1;
  return result;
}


void rdp_pre_parse(void)
{
  rdp_dir_output_file = text_force_filetype(rdp_sourcefilename, "out");
  rdp_base = symbol_new_scope(rdp, "parser");
  set_assign_list(& rdp_production_set, K_PRIMARY, K_SEQUENCE, K_LIST, SET_END);

  rdp_add_arg(ARG_BLANK, NULL, NULL, "");
  rdp_add_arg(ARG_BOOLEAN, "f", "rdp_filter", "Filter mode (read from stdin and write to stdout)");
  rdp_add_arg(ARG_BOOLEAN, "l", "rdp_line_echo", "Make a listing");
  rdp_add_arg(ARG_BOOLEAN, "L", "rdp_lexicalise", "Print lexicalised source file");
  rdp_add_arg(ARG_STRING, "o", "rdp_outputfilename", "Write output to filename");
  rdp_add_arg(ARG_BOOLEAN, "s", "rdp_symbol_echo", "Echo each scanner symbol as it is read");
  rdp_add_arg(ARG_BOOLEAN, "S", "rdp_symbol_statistics", "Print summary symbol table statistics"); 
  rdp_add_arg(ARG_NUMERIC, "t", "rdp_tabwidth", "Tab expansion width (default 8)"); 
  rdp_add_arg(ARG_NUMERIC, "T", "rdp_textsize", "Text buffer size in bytes for scanner (default 20000)"); 
  rdp_add_arg(ARG_BOOLEAN, "v", "rdp_verbose", "Set verbose mode"); 
  rdp_add_arg(ARG_STRING, "V", "rdp_vcg_filename", "Write derivation tree to filename in VCG format"); 
  
  rdp_find("ID", K_STRING, RDP_ANY)->token_value = SCAN_P_ID;  /* add predefined primitive productions */
  rdp_find("INTEGER", K_INTEGER, RDP_ANY)->token_value = SCAN_P_INTEGER; 
  rdp_find("REAL", K_REAL, RDP_ANY)->token_value = SCAN_P_REAL; 
  rdp_find("EOLN", K_STRING, RDP_ANY)->token_value = SCAN_P_EOLN;
}

static void rdp_order_tokens(void * base)
{
  rdp_data * temp =(rdp_data *) symbol_next_symbol_in_scope(base);
  
  while (temp != NULL)
  {
    if (temp->kind == K_TOKEN || temp->kind == K_EXTENDED)
    {
      temp->extended_value = temp->token_value; 
      temp->token_value = rdp_token_count++; 
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
  
  /* now set up start sets for tokens, code and primitives */
  temp =(rdp_data *) symbol_next_symbol_in_scope(base); 
  while (temp != NULL)
  {
    if (temp->kind == K_TOKEN || temp->kind == K_INTEGER ||
      temp->kind == K_REAL || temp->kind == K_STRING ||
    temp->kind == K_EXTENDED)
    {
      set_unite_element(& temp->first, temp->token_value); 
      temp->first_cardinality = set_cardinality(& temp->first); 
      set_unite_element(& temp->follow, SCAN_P_EOF);
      temp->follow_cardinality = set_cardinality(& temp->follow); 

      temp->first_done = 1; 
    }
    else if (temp->kind == K_LIST && temp->supplementary_token != NULL)
    {
      set_unite_element(& temp->follow, temp->supplementary_token->token_value); 
      temp->follow_cardinality = set_cardinality(& temp->follow); 
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
}

static void rdp_add_continuations(void * base)
{
  rdp_data * temp =(rdp_data *) symbol_next_symbol_in_scope(base); 
  char * last_token = "";     /* remember most recent token name */
  int tokens_added = 0; 
  
  if (rdp_verbose)
    text_message(TEXT_INFO, "Checking for continuation tokens\n"); 
  
  while (temp != NULL)        /* scan over all productions */
  {
    if (temp->kind == K_TOKEN || temp->kind == K_EXTENDED)
    {
      char * lo = last_token, 
      * hi = temp->id; 
      
      if (!text_is_valid_C_id(hi)) /* ignore identifiers */
      {
        /* rdp_find common prefix */
        while (* lo == * hi && * hi != 0) /* bump while they are identical */
        {
          lo++; 
          hi++; 
        }
        
        hi++;                 /* we can't have two identical tokens, so at worst this will move to a null */
        
        /* now add continuations */
        /* is the first non-identical character the one before the terminating null ? */
        while (*(hi)!= 0)     /* add a continuation */
        {
          /* insert the sub-string */
          char * c = temp->id, 
          * continuation_name = text_top;  /* remember start position */
          
          while (c != hi)
            text_insert_char(* c++);  /* copy identifier */
          text_insert_char(0);  /* add a terminating null */
          
          if (rdp_verbose)
            text_message(TEXT_INFO, "Adding continuation token \'%s\'\n", continuation_name);

          tokens_added = 1;

          rdp_find(continuation_name, K_TOKEN, RDP_ANY);

          hi++;
        }
      }

      last_token = temp->id;
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp);
  }
  if (rdp_verbose && !tokens_added)
    text_message(TEXT_INFO, "No continuation tokens needed\n");

}

void rdp_print_kleene_closure_of_tokens(void *tokenTable) {
  int commentCount = 1;
  tokens_data *tokenSymbol;

  /* First pass to output start rule */
  text_printf("(* Lexer grammar generated by RDP *)\n\n"
              "lexerGrammar ::= { INTEGER | REAL | ID |");

  for (tokenSymbol = (tokens_data*) symbol_next_symbol_in_scope(symbol_get_scope(tokenTable));
       tokenSymbol != NULL;
       tokenSymbol = (tokens_data*) symbol_next_symbol_in_scope(tokenSymbol)) {
    switch(tokenSymbol->extended_value) {
      case SCAN_P_IGNORE:
        text_printf("\n  '");
        text_print_C_char(tokenSymbol->id);
        text_printf("'");
        if (symbol_next_symbol_in_scope(tokenSymbol)!= NULL)
          text_printf(" |");
        break;

      case SCAN_P_CHAR:
        text_printf("\n  CHAR('");
        text_print_C_char(tokenSymbol->id);
        text_printf("')");
        if (symbol_next_symbol_in_scope(tokenSymbol)!= NULL)
          text_printf(" |");
        break;
      case SCAN_P_CHAR_ESC:
        text_printf("\n  CHAR_ESC('");
        text_print_C_char(tokenSymbol->id);
        text_printf("' '");
        text_print_C_char(tokenSymbol->close);
        text_printf("')");
        if (symbol_next_symbol_in_scope(tokenSymbol)!= NULL)
          text_printf(" |");
        break;
      case SCAN_P_STRING:
        text_printf("\n  STRING('");
        text_print_C_char(tokenSymbol->id);
        text_printf("')");
        if (symbol_next_symbol_in_scope(tokenSymbol)!= NULL)
          text_printf(" |");
        break;
      case SCAN_P_STRING_ESC:
        text_printf("\n  STRING_ESC('");
        text_print_C_char(tokenSymbol->id);
        text_printf("' '");
        text_print_C_char(tokenSymbol->close);
        text_printf("')");
        if (symbol_next_symbol_in_scope(tokenSymbol)!= NULL)
          text_printf(" |");
        break;
      case SCAN_P_COMMENT:
      case SCAN_P_COMMENT_VISIBLE:
      case SCAN_P_COMMENT_NEST:
      case SCAN_P_COMMENT_NEST_VISIBLE:
      case SCAN_P_COMMENT_LINE:
      case SCAN_P_COMMENT_LINE_VISIBLE:
        /* Suppress on this pass */
        break;

      default: text_message(TEXT_FATAL, "unexpected extended value found in token '%s'\n", tokenSymbol->id);
    }
  }

  text_printf("\n}.\n\n");

  /* Second pass to output commments */
  for (tokenSymbol = (tokens_data*) symbol_next_symbol_in_scope(symbol_get_scope(tokenTable));
       tokenSymbol != NULL;
       tokenSymbol = (tokens_data*) symbol_next_symbol_in_scope(tokenSymbol)) {
    switch(tokenSymbol->extended_value) {
      case SCAN_P_IGNORE:
      case SCAN_P_CHAR:
      case SCAN_P_CHAR_ESC:
      case SCAN_P_STRING:
      case SCAN_P_STRING_ESC:
        break;

      case SCAN_P_COMMENT:
        text_printf("\ncomment%i ::= COMMENT('", commentCount++);
        text_print_C_char(tokenSymbol->id);
        text_printf("' '");
        text_print_C_char(tokenSymbol->close);
        text_printf("').");
        break;
      case SCAN_P_COMMENT_VISIBLE:
        text_printf("\ncomment%i ::= COMMENT_VISIBLE('", commentCount++);
        text_print_C_char(tokenSymbol->id);
        text_printf("' '");
        text_print_C_char(tokenSymbol->close);
        text_printf("').");
        break;
      case SCAN_P_COMMENT_NEST:
        text_printf("\ncomment%i ::= COMMENT_NEST('", commentCount++);
        text_print_C_char(tokenSymbol->id);
        text_printf("' '");
        text_print_C_char(tokenSymbol->close);
        text_printf("').");
        break;
      case SCAN_P_COMMENT_NEST_VISIBLE:
        text_printf("\ncomment%i ::= COMMENT_NEST_VISIBLE('", commentCount++);
        text_print_C_char(tokenSymbol->id);
        text_printf("' '");
        text_print_C_char(tokenSymbol->close);
        text_printf("').");
        break;
      case SCAN_P_COMMENT_LINE:
        text_printf("\ncomment%i ::= COMMENT_LINE('", commentCount++);
        text_print_C_char(tokenSymbol->id);
        text_printf("').");
        break;
      case SCAN_P_COMMENT_LINE_VISIBLE:
        text_printf("\ncomment%i ::= COMMENT_LINE_VISIBLE('", commentCount++);
        text_print_C_char(tokenSymbol->id);
        text_printf("').");
        break;

      default: text_message(TEXT_FATAL, "unexpected extended value found in token '%s'\n", tokenSymbol->id);
    }
  }
}

void rdp_post_parse(char * outputfilename, int force)
{
  void
  * tokens_base = symbol_get_scope(tokens),
  * rdp_base = symbol_get_scope(rdp);

  locals_data * local =(locals_data *) symbol_new_symbol(sizeof(locals_data));

  local->id = "result";
  symbol_insert_symbol(locals, local);

  symbol_sort_scope(tokens, tokens_base);  /* sort productions into alphabetical order */
  rdp_add_continuations(tokens_base);  /* scan through tokens and add any necessry continuations */
  symbol_sort_scope(tokens, tokens_base);  /* re-sort productions into alphabetical order */
  rdp_order_tokens(tokens_base);  /* apply token numbers to token productions */
  rdp_order_tokens(rdp_base);  /* apply token numbers to token productions */
  rdp_make_token_string(tokens_base);  /* make a string with all token names in it */

  if (rdp_kleene) {
    rdp_print_kleene_closure_of_tokens(tokens);
    exit(0);
  }
  symbol_sort_scope(rdp, rdp_base);  /* sort productions into alphabetical order */
  rdp_bad_grammar(rdp_base);  /* find the non-LL(1)-isms */

  if (rdp_expanded)           /* print out expanded BNF */
    rdp_dump_extended(rdp_base);

  if (text_total_errors()> 0)
  {
    if (force)
      text_message(TEXT_WARNING, "Grammar is not LL(1) but -F set: writing files\n");
    else
      text_message(TEXT_FATAL, "Run aborted without creating output files - rerun with -F to override\n");
  }

  rdp_print_header(text_force_filetype(outputfilename, "h"));  /* dump header file */

  rdp_print_parser(text_force_filetype(outputfilename, "c"), rdp_base);  /* dump main file */

  if (rdp_verbose)            /* see how much text buffer space we used */
    text_print_statistics();
}

void * rdp_process_token(char * name)
{
  rdp_data * result;

  rdp_check_token_valid(name);
  result = rdp_find(name, K_TOKEN, RDP_ANY);
  result->call_count++;       /* increment call count */

  return result;
}

/* End of rdp_aux.c */
