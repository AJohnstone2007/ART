/****************************************************************************
*
* RDP release 1.10 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Mar 1994
*
* rdp.c - hand written rdp front end
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <ctype.h>
#include <string.h>
#include "crash.h"
#include "symbol.h"
#include "set.h"
#include "scan.h"
#include "rdp.h"
#include "rdp_aux.h"

 /* extern unsigned _stklen = 8000; *//* Increase stack size for Borland C */

char *rdp__sourcefilename = NULL, /* source file name */
*rdp__outputfilename = "rdparser",  /* output file name */
 rdp__verbose = 0;              /* verbose mode flag */

symbol__
* rdp__base;                    /* pointer to production chain scope */

set__
unit_first,
dir_first,
dir_stop,
prod_first,
alt_first,
seq_first,
item_first,
item_ret_first,
item_inl_first,
unit_stop,
prod_stop,
alt_stop,
seq_stop,
item_inl_stop,
item_ret_stop,
id_set;

void rdp__help(char *msg)
{
  crash__("%s\n\nRecursive descent parser generator V1.00 " __DATE__ " " __TIME__ "\n(c) Adrian Johnstone 1994\n\n"
          "Usage: rdp [options] source[.bnf] \n\n"
          "Options:\n\n"
          "-e  Write out expanded bnf along with first and follow sets\n"
          "-l  Make a listing\n"
          "-ofilename Write output to filename (default rdparser.c)\n"
          "-s  Echo each scanner symbol as it is read\n"
          "-S  Print summary symbol table statistics\n"
          "-tn Tab expansion width (default 2)\n"
          "-Tn Text buffer size in bytes for scanner (default 8192)\n"
          "-F  Force creation of output files\n"
          "-v  Set verbose mode\n"
          "\nYou can contact the author (Adrian Johnstone) at:\n\n"
          "Computer Science Department, Royal Holloway, University of London,\n"
          "Egham, Surrey, TW20 0EX UK. Email: adrian@dcs.rhbnc.ac.uk\n",
          msg == NULL ? "" : msg);
}

void rdp__load_keywords(void)   /* install keywords in symbol table */
{
  scan__load_keyword("OPTION", T_OPTION);
  scan__load_keyword("TITLE", T_TITLE);
  scan__load_keyword("SUFFIX", T_SUFFIX);
  scan__load_keyword("CASE_INSENSITIVE", T_CASE_INSENSITIVE);
  scan__load_keyword("SHOW_SKIPS", T_SHOW_SKIPS);
  scan__load_keyword("SET_SIZE", T_SET_SIZE);
  scan__load_keyword("PASSES", T_PASSES);
  scan__load_keyword("HASH_SIZE", T_HASH_SIZE);
  scan__load_keyword("HASH_PRIME", T_HASH_PRIME);
  scan__load_keyword("TEXT_SIZE", T_TEXT_SIZE);
  scan__load_keyword("MAX_ERRORS", T_MAX_ERRORS);
  scan__load_keyword("MAX_WARNINGS", T_MAX_WARNINGS);
  scan__load_keyword("TAB_WIDTH", T_TAB_WIDTH);
  scan__load_keyword("TEXT_MODE", T_TEXT_MODE);
  scan__load_keyword("PRE_PROCESS", T_PRE_PROCESS);
  scan__load_keyword("POST_PROCESS", T_POST_PROCESS);
  scan__load_keyword("USES", T_USES);
  scan__load_keyword("HEX_DOLLAR", T_HEX_DOLLAR);
  scan__load_keyword("OUTPUT_FILE", T_OUTPUT_FILE);
  scan__load_keyword("INCLUDE", T_INCLUDE);
  scan__load_keyword("STRING", T_STRING);
  scan__load_keyword("STRING_ESC", T_STRING_ESC);
  scan__load_keyword("COMMENT", T_COMMENT);
  scan__load_keyword("COMMENT_LINE", T_COMMENT_LINE);
  scan__load_keyword("COMMENT_LINE_VISIBLE", T_COMMENT_LINE_VISIBLE);
  scan__load_keyword("COMMENT_NEST", T_COMMENT_NEST);
  scan__load_keyword("COMMENT_VISIBLE", T_COMMENT_VISIBLE);
  scan__load_keyword("COMMENT_NEST_VISIBLE", T_COMMENT_NEST_VISIBLE);
  scan__load_keyword("{", T_LBRACE);
  scan__load_keyword("}", T_RBRACE);
  scan__load_keyword("(", T_LPAR);
  scan__load_keyword(")", T_RPAR);
  scan__load_keyword("[", T_LBRACK);
  scan__load_keyword("]", T_RBRACK);
  scan__load_keyword("<", T_LANGLE);
  scan__load_keyword(">", T_RANGLE);
  scan__load_keyword(".", T_PERIOD);
  scan__load_keyword("|", T_ALT);
  scan__load_keyword("::=", T_IS);
  scan__load_keyword(":", T_COLON);
  scan__load_keyword(",", T_COMMA);
  scan__load_keyword("*", T_STAR);
  scan__load_keyword("::", T_DCOLON);
  scan__load_extended_keyword("'", T_SQUOTE, E_STRING_ESC, "\\");
  scan__load_extended_keyword("\"", T_DQUOTE, E_STRING_ESC, "\\");
  scan__load_extended_keyword("(*", T_OCOMMENT, E_COMMENT_NEST, "*)");
}

void rdp__load_sets(void)       /* initisliase first and stop sets for each production */
{

  unit_first = set__construct(P_ID, P_NEW_ID, T_OPTION, T_TITLE, T_SUFFIX,
                              T_CASE_INSENSITIVE, T_SHOW_SKIPS,
                              T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
                              T_HASH_SIZE, T_PASSES, T_HASH_PRIME, T_TEXT_MODE,
                              T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                              T_MAX_WARNINGS, T_USES, T_HEX_DOLLAR, T_OUTPUT_FILE,
                              T_INCLUDE,
                              SET_END);
  prod_first = set__construct(P_ID, P_NEW_ID, SET_END);
  dir_first = set__construct(T_OPTION, T_TITLE, T_SUFFIX,
                             T_CASE_INSENSITIVE, T_SHOW_SKIPS,
                             T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
                             T_HASH_SIZE, T_PASSES, T_HASH_PRIME, T_TEXT_MODE,
                             T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                             T_MAX_WARNINGS, T_USES, T_HEX_DOLLAR, T_OUTPUT_FILE,
                             T_INCLUDE,
                             SET_END);
  alt_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
                             T_STRING, T_STRING_ESC, T_COMMENT_VISIBLE,
                             T_COMMENT_LINE_VISIBLE, T_COMMENT_LINE,
                             T_COMMENT_NEST_VISIBLE, T_COMMENT,
                             T_COMMENT_NEST, T_LBRACE, T_LBRACK, T_LPAR,
                             T_LANGLE,
                             SET_END);

  seq_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
                             T_STRING, T_STRING_ESC, T_COMMENT_VISIBLE, T_COMMENT_LINE_VISIBLE, T_COMMENT_LINE, T_COMMENT_NEST_VISIBLE, T_COMMENT, T_COMMENT_NEST,
                             T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
                             SET_END);

  item_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
                              T_STRING, T_STRING_ESC, T_COMMENT_VISIBLE, T_COMMENT_LINE_VISIBLE, T_COMMENT_LINE, T_COMMENT_NEST_VISIBLE, T_COMMENT, T_COMMENT_NEST,
                              T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
                              SET_END);
  item_inl_first = set__construct(T_DQUOTE,
                                  T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
                                  SET_END);
  item_ret_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE,
                                  T_STRING, T_STRING_ESC, T_COMMENT_VISIBLE, T_COMMENT_LINE_VISIBLE, T_COMMENT_LINE, T_COMMENT_NEST_VISIBLE, T_COMMENT, T_COMMENT_NEST,
                                  SET_END);

  unit_stop = set__construct(P_EOF, SET_END);
  prod_stop = set__construct(P_ID, P_NEW_ID, T_OPTION, T_TITLE, T_SUFFIX,
                             T_CASE_INSENSITIVE, T_SHOW_SKIPS,
                             T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
                             T_HASH_SIZE, T_PASSES, T_HASH_PRIME, T_TEXT_MODE,
                             T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                             T_MAX_WARNINGS, T_USES, T_HEX_DOLLAR, T_OUTPUT_FILE,
                             T_INCLUDE,
                             P_EOF, SET_END);
  dir_stop = set__construct(P_ID, P_NEW_ID, T_OPTION, T_TITLE, T_SUFFIX,
                            T_CASE_INSENSITIVE, T_SHOW_SKIPS,
                            T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
                            T_HASH_SIZE, T_PASSES, T_HASH_PRIME, T_TEXT_MODE,
                            T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                            T_MAX_WARNINGS, T_USES, T_HEX_DOLLAR, T_OUTPUT_FILE,
                            T_INCLUDE,
                            P_EOF, SET_END);
  alt_stop = set__construct(T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
                            T_PERIOD, P_EOF, SET_END);
  seq_stop = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
                            T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
                            T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
                            T_ALT, T_PERIOD, P_EOF, SET_END);
  item_inl_stop = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
                                 T_STRING, T_STRING_ESC, T_COMMENT_VISIBLE, T_COMMENT_LINE_VISIBLE, T_COMMENT_LINE, T_COMMENT_NEST_VISIBLE, T_COMMENT,
                                 T_COMMENT_NEST,
                                 T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
                                 T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
                                 T_ALT, T_PERIOD, T_COLON, P_EOF, SET_END);
  item_ret_stop = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
                                 T_STRING, T_STRING_ESC, T_COMMENT_VISIBLE, T_COMMENT_LINE_VISIBLE, T_COMMENT_LINE, T_COMMENT_NEST_VISIBLE, T_COMMENT,
                                 T_COMMENT_NEST,
                                 T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
                                 T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
                                 T_ALT, T_PERIOD, T_COLON, P_EOF, SET_END);
  id_set = set__construct(P_ID, P_NEW_ID, SET_END);
}

/************** EBNF parser ***************************************/
rdp__list *alt(void);           /* forward definition */

symbol__ *item_ret(void)        /* parse a returnable item and return a symbol */
{
  symbol__ *ret = NULL;

  if (scan__test_set(item_ret_first, item_ret_stop, "Expecting a returnable item"))
  {
    switch (scan__sym->token)
    {
    case P_ID:                  /* Primary */
    case P_NEW_ID:
      scan__sym->token = P_ID;  /* Make NEW_ID's ID's to save text space */
      ret = rdp__find(scan__sym->id, K_PRIMARY, 0);
      rdp__check_eoln(scan__sym->id);
      scan__();
      break;

    case T_SQUOTE:              /* Token */
      rdp__check_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      scan__();
      break;

    case T_STRING:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting string delimiter token");
      rdp__check_string_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      DATA(ret, extended_class) = E_STRING;
      DATA(ret, close) = scan__sym->id;
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;

    case T_STRING_ESC:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting string delimiter token");
      rdp__check_string_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting string escape token");
      rdp__check_string_token_valid(scan__sym->id);
      DATA(ret, extended_class) = E_STRING_ESC;
      DATA(ret, close) = scan__sym->id;
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;

    case T_COMMENT_LINE_VISIBLE:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
      rdp__check_comment_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      DATA(ret, extended_class) = E_COMMENT_LINE_VISIBLE;
      DATA(ret, close) = scan__text;
      scan__insert_char('\n');
      scan__insert_char(0);
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;

    case T_COMMENT_LINE:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
      rdp__check_comment_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      DATA(ret, extended_class) = E_COMMENT_LINE;
      DATA(ret, close) = scan__text;
      scan__insert_char('\n');
      scan__insert_char(0);
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;

    case T_COMMENT:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
      rdp__check_comment_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment closing token");
      rdp__check_comment_token_valid(scan__sym->id);
      DATA(ret, extended_class) = E_COMMENT;
      DATA(ret, close) = scan__sym->id;
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;

    case T_COMMENT_NEST:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
      rdp__check_comment_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment closing token");
      rdp__check_comment_token_valid(scan__sym->id);
      DATA(ret, extended_class) = E_COMMENT_NEST;
      DATA(ret, close) = scan__sym->id;
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;

    case T_COMMENT_VISIBLE:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
      rdp__check_comment_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment closing token");
      rdp__check_comment_token_valid(scan__sym->id);
      DATA(ret, extended_class) = E_COMMENT_VISIBLE;
      DATA(ret, close) = scan__sym->id;
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;

    case T_COMMENT_NEST_VISIBLE:
      scan__();
      scan__test(T_LPAR, prod_stop, "Expecting (");
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
      rdp__check_comment_token_valid(scan__sym->id);
      ret = rdp__find(scan__sym->id, K_TOKEN, 0);
      scan__();
      scan__test(T_SQUOTE, prod_stop, "Expecting comment closing token");
      rdp__check_comment_token_valid(scan__sym->id);
      DATA(ret, extended_class) = E_COMMENT_NEST_VISIBLE;
      DATA(ret, close) = scan__sym->id;
      DATA(ret, return_type) = "char";
      DATA(ret, return_type_stars) = 1;
      scan__();
      scan__test(T_RPAR, prod_stop, "Expecting )");
      scan__();
      break;
    }
    scan__test_set(item_ret_stop, item_ret_stop, "Expecting an item follow");
  }
  return ret;
}

symbol__ *item_inl(void)        /* parse an inline item and return a symbol */
{
  symbol__ *ret = NULL;
  rdp__list *body;

  if (scan__test_set(item_inl_first, item_inl_stop, "Expecting an inline item"))
  {
    switch (scan__sym->token)
    {
    case T_DQUOTE:              /* Code */
      ret = rdp__find(scan__sym->id, K_CODE, 0);
      DATA(ret, contains_null) = 1;
      scan__();
      break;

    case T_LPAR:                /* Do-first */
      scan__();                 /* skip left parenthesis */
      body = alt();
      scan__test(T_RPAR, item_inl_stop, "Expecting ) at end of do-first");
      scan__();
      ret = rdp__find(scan__insert_subid(rdp__primary_id, rdp__component++), K_DO_FIRST, 1);
      DATA(ret, list) = body;   /* parse contents */
      break;

    case T_LBRACE:              /* Iteration */
      scan__();                 /* skip left brace */
      body = alt();
      scan__test(T_RBRACE, item_inl_stop, "Expecting } at end of iteration");
      scan__();
      ret = rdp__find(scan__insert_subid(rdp__primary_id, rdp__component++), K_ITERATION, 1);
      DATA(ret, list) = body;   /* parse contents */
      DATA(ret, contains_null) = 1;
      break;

    case T_LBRACK:              /* Conditional */
      scan__();                 /* skip left bracket */
      body = alt();
      scan__test(T_RBRACK, item_inl_stop, "Expecting ] at end of conditional");
      scan__();
      ret = rdp__find(scan__insert_subid(rdp__primary_id, rdp__component++), K_CONDITIONAL, 1);
      DATA(ret, list) = body;   /* parse contents */
      DATA(ret, contains_null) = 1;
      break;

    case T_LANGLE:              /* List */
      scan__();                 /* skip left brace */
      body = alt();
      scan__test(T_RANGLE, item_inl_stop, "Expecting > at end of list");
      scan__();
      ret = rdp__find(scan__insert_subid(rdp__primary_id, rdp__component++), K_LIST, 1);
      DATA(ret, list) = body;   /* parse contents */
      scan__test(T_SQUOTE, item_inl_stop, "A list must be followed by a token");
      if (*(scan__sym->id + 1) == 0)
        scan__error_echo("Empty tokens are not allowed in lists");
      DATA(ret, supplementary_token) = rdp__find(scan__sym->id, K_TOKEN, 0);
      scan__();
      break;
    }
    scan__test_set(item_inl_stop, item_inl_stop, "Expecting an item follow");
  }
  return ret;
}


rdp__list *seq(void)            /* parse a sequence and return a list */
{
  struct rdp__list_node *base = crash__calloc(sizeof(struct rdp__list_node), "sequence list node"), /* allocate dummy first element */
  *end = base;

  if (scan__test_set(seq_first, seq_stop, "Expecting a sequence"))
  {
    do
    {
      /* link in at end of sequence list */
      end->next = crash__calloc(sizeof(struct rdp__list_node), "sequence list node");
      end = end->next;

      if (set__inc_element(item_ret_first, scan__sym->token))
      {
        end->production = item_ret();
        if (scan__sym->token == T_COLON)  /* we have a return name */
        {
          scan__();             /* skip trailing colon */
          scan__test_set(id_set, prod_stop, "Expecting an identifier");
          end->return_name = scan__sym->id;
          scan__();
        }
      }
      else if (set__inc_element(item_inl_first, scan__sym->token))
        end->production = item_inl();
    }
    while (set__inc_element(item_first, scan__sym->token));

    scan__test_set(seq_stop, seq_stop, "Expecting a sequence follow");
  }
  return base->next;            /* drop sentinel */
}

rdp__list *alt(void)            /* parse and return a list of alternates */
{
  struct rdp__list_node *base,
  *end;

  /* make a dummy list node as a sentinel */
  base = crash__calloc(sizeof(struct rdp__list_node), "alt list node");
  end = base;

  if (scan__test_set(alt_first, alt_stop, "Expecting an alternate"))
  {
    do
    {
      /* allocate a new list node */
      rdp__list *body = seq();

      end->next = crash__calloc(sizeof(struct rdp__list_node), "alt list node");
      end = end->next;

      end->production = rdp__find(scan__insert_subid(rdp__primary_id, rdp__component++), K_SEQUENCE, 1);
      DATA(end->production, list) = body;
    }
    while (scan__sym->token == T_ALT ? scan__(), 1 : 0);

    scan__test_set(alt_stop, alt_stop, "Expecting an alternate follow");
  }

  return base->next;
}

void prod(void)                 /* parse a production */
{
  if (scan__test_set(prod_first, prod_stop, "Expecting a production"))
  {
    {
      char *this_id = scan__sym->id,
      *return_type_name = "void";
      unsigned return_type_stars = 0;
      symbol__ *body;

      rdp__check_prod_name_valid(this_id);  /* look for __ */


      rdp__primary_id = scan__sym->id;
      rdp__component = 0;


      scan__();
      if (scan__sym->token == T_COLON)
      {
        scan__();               /* skip trailing colon */
        scan__test_set(id_set, prod_stop, "Expecting an identifier");
        return_type_name = scan__sym->id;
        scan__();
        /* now collect star operators */
        while (scan__sym->token == T_STAR)
        {
          return_type_stars++;
          scan__();
        }
      }

      scan__test(T_IS, prod_stop, "A production definition must start with an ::= symbol");
      scan__();
      body = rdp__find(rdp__primary_id, K_PRIMARY, 0);
      DATA(body, list) = alt(); /* parse contents */
      DATA(body, been_defined) = 1;
      DATA(body, return_type) = return_type_name;
      DATA(body, return_type_stars) = return_type_stars;

      if (rdp__start_prod == NULL)
        rdp__start_prod = body;

      scan__test(T_PERIOD, prod_stop, "Expecting '.' at end of production");
      scan__();
    }
    scan__test_set(prod_stop, prod_stop, "Expecting a production follow");
  }
}

void dir(void)
{
  switch (scan__sym->token)
  {
    struct rdp__string_list *temp;
    char *filename;

  case T_OPTION:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    temp = crash__malloc(sizeof(struct rdp__string_list), "OPTION node allocation");
    temp->str1 = scan__sym->id + 1;
    temp->next = rdp__dir_help;
    rdp__dir_help = temp;
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    temp->str2 = scan__sym->id + 1;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_USES:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    temp = crash__malloc(sizeof(struct rdp__string_list), "INCLUDE node allocation");
    temp->str1 = scan__sym->id + 1;
    temp->next = rdp__dir_include;
    rdp__dir_include = temp;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_INCLUDE:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    filename = scan__sym->id + 1;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    if (scan__open_file(filename) == NULL)
      scan__error_echo("Include file '%s' not found", filename);
    break;

  case T_PRE_PROCESS:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    rdp__dir_pre_process = scan__sym->id + 1;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_POST_PROCESS:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    rdp__dir_post_process = scan__sym->id + 1;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_TITLE:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    rdp__dir_title = scan__sym->id + 1;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_SUFFIX:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    rdp__dir_suffix = scan__sym->id + 1;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_OUTPUT_FILE:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(T_DQUOTE, prod_stop, "Expecting code item");
    rdp__dir_output_file = scan__sym->id + 1;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_SET_SIZE:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_set_size = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_HASH_SIZE:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_hash_size = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_PASSES:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_passes = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_HASH_PRIME:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_hash_prime = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_TEXT_SIZE:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_text_size = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_TAB_WIDTH:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_tab_width = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_MAX_ERRORS:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_max_errors = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_MAX_WARNINGS:
    scan__();
    scan__test(T_LPAR, dir_stop, "Expecting " "(");
    scan__();
    scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
    rdp__dir_max_warnings = scan__sym->number;
    scan__();
    scan__test(T_RPAR, dir_stop, "Expecting " ")");
    scan__();
    break;

  case T_HEX_DOLLAR:
    scan__();
    rdp__dir_hex_dollar = 1;
    break;

  case T_TEXT_MODE:
    scan__();
    rdp__dir_text_mode = 1;
    break;

  case T_CASE_INSENSITIVE:
    scan__();
    rdp__dir_case_insensitive = 1;
    break;

  case T_SHOW_SKIPS:
    scan__();
    rdp__dir_show_skips = 1;
    break;
  }
}

void unit(void)                 /* parse a file */
{
  scan__test_set(unit_first, prod_stop, "Expecting product or directive");

  while (set__inc_element(unit_first, scan__sym->token))
  {
    if (set__inc_element(prod_first, scan__sym->token))
      prod();
    else if (set__inc_element(dir_first, scan__sym->token))
      dir();

  }

  scan__test(P_EOF, prod_stop, "Expecting end of file");
}

int main(int argc, char *argv[])
{
  unsigned tabwidth = 2,        /* tab expansion width */
   textsize = 20000,            /* size of scanner text array */
   symbol__statistics = 0;      /* show symbol table statistics flag */

  while (--argc > 0)
  {
    if ((*++argv)[0] == '-')    /* switch */
    {
      switch ((*argv)[1])
      {
      case 'e':
        rdp__expanded = 1;
        break;
      case 'o':
        if (*(*argv + 2) != 0)
          rdp__outputfilename = *argv + 2;  /* leave as rdparser if null filename */
        break;
      case 's':
        scan__symbol_echo(1);
        break;
      case 'S':
        symbol__statistics = 1;
        break;
      case 'l':
        scan__line_echo(1);
        break;
      case 't':
        {
          sscanf(*argv + 2, "%u", &tabwidth);
          scan__line_echo(tabwidth);
        }
        break;
      case 'T':
        {
          sscanf(*argv + 2, "%u", &textsize);
        }
        break;
      case 'v':
        rdp__verbose = 1;
        break;
      case 'F':
        rdp__force = 1;
        break;
      default:
        {
          printf("\nUnrecognised option -%c\n", (*argv)[1]);
          rdp__help("");
        }
      }
    }
    else
      rdp__sourcefilename = *argv;
  }

  if (rdp__sourcefilename == NULL)
    rdp__help("No source file specified");

  set__init(RDP_SET_SIZE);
  rdp__load_sets();

  symbol__init(101, 31);
  rdp__load_keywords();
  rdp__base = symbol__new_scope("parser");  /* create bnf scope */
  scan__show_skips(1);
  scan__summarise_errors(rdp__verbose);
  scan__init(textsize, 25, 100);

  if (rdp__verbose)
    printf("\nRecursive descent parser generator V1.00 (c) Adrian Johnstone 1994\n\n");

#define FILE_MODE
#ifdef FILE_MODE
  if (scan__open_file(scan__default_filetype(rdp__sourcefilename, "bnf")) == NULL)
    rdp__help("Can't open source file");
#else
  if (scan__open_text("Text mode",
                      scan__load_file(scan__default_filetype(rdp__sourcefilename, "bnf"))
                      ) == NULL)
    rdp__help("Can't open source file");
#endif


  rdp__pre_process();

  unit();                       /* call parser start production */

  if (symbol__statistics)
    symbol__print_statistics();

  return rdp__post_process(rdp__outputfilename, rdp__force, rdp__base);
}

/* End of rdp.c */
