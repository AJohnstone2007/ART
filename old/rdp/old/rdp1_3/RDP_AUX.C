/****************************************************************************
*
* RDP release 1.30 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 28 Dec 1994
*
* rdp_aux.c - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include "params.h"
#include "crash.h"
#include "symbol.h"
#include "set.h"
#include "scan.h"
#include "rdp_aux.h"

extern char* rdp__sourcefilename; /* source file name loaded by main line */
static unsigned rdp__column = 0,/* current printing column */
 rdp__indentation = 0,          /* current identation level */
 rdp__line_break = 60,          /* column to break lines on */
 rdp__set_width;                /* how many elements in set */

char
 rdp__force = 0,                /* force output files flag */
 rdp__error_production_name = 0,/* flag to force writing of production name into error messages */
 rdp__expanded = 0,             /* flag to generate expanded bnf listing */
 rdp__parser_only = 0,          /* omit semantic actions flag */
*rdp__primary_id;               /* identifier for parent production */

symbol__
* rdp__start_prod = NULL;       /* symbol for first production in BNF file */

unsigned rdp__component;        /* sub-production component number */

char
*rdp__dir_title = "rdparser",   /* string from TITLE directive */
*rdp__dir_suffix = "",          /* string from SUFFIX directive */
*rdp__dir_pre_parse = NULL,     /* string from PRE_PARSE directive */
*rdp__dir_post_parse = NULL,    /* string from POST_PARSE directive */
*rdp__dir_output_file = "a.out";/* string from OUTPUT_FILE directive */

unsigned rdp__dir_set_size = 0, /* INTEGER from SET_SIZE directive */
 rdp__dir_interpreter = 0,        /* TEXT_MODE flag */
 rdp__dir_case_insensitive = 0, /* CASE_INSENSITIVE flag */
 rdp__dir_show_skips = 0,       /* SHOW_SKIPS flag */
 rdp__dir_newline_visible = 0,  /* NEWLINE_VISIBLE flag */
 rdp__dir_passes = 1,           /* PASSES directive */
 rdp__dir_hash_size = 101,      /* HASH_SIZE directive */
 rdp__dir_hash_prime = 31,      /* HASH_PRIME dirctive */
 rdp__dir_tab_width = 8,        /* TAB_WIDTH directive */
 rdp__dir_text_size = 20000,    /* TEXT_SIZE directive */
 rdp__dir_max_errors = 25,      /* MAX_ERRORS directive */
 rdp__dir_max_warnings = 100;   /* MAX_WARNINGS directive */

rdp__string_list
* rdp__dir_help,                /* strings from HELP directives */
*rdp__dir_include;              /* strings from INCLUDE directives */

set__
rdp__production_set,
rdp__empty_production_set;

static int rdp__is_valid_C_id(char *s)
{
  int temp;

  temp = (isalpha(*s) || *s == '_');
  while (*++s != '\0')
    temp = temp && (isalnum(*s) || *s == '_');
  return (temp);
}

static void rdp__print_parser_string(FILE * f, char *s)
{
  while (*s != 0)
  {
    if (*s == '\"' || *s == '\\' || *s == '\'')
      fprintf(f, "\\");
    fprintf(f, "%c", *s++);
  }
}

static void print_time(FILE *f)
{
 char line[80];
 time_t timer = time(NULL);

 strftime(line, 80, "%b %d %Y at %H:%M:%S" ,localtime(&timer));

 fprintf(f, "%s", line);
}

static void  rdp__print_parser_param_list_sub(FILE *f, rdp__param_list *param, int last, int definition)
{
  if (param != NULL)
  {
    rdp__print_parser_param_list_sub(f, param->next, 0, definition);
    fprintf(f, "%s%s%s%s", definition ? param->type : "", definition ? " ": "", param->id, last ? "" : ", ");
  }

}

static void rdp__print_parser_param_list(FILE *f, rdp__param_list *params, int definition)
{
  fprintf(f, "(");

  if (params == NULL && definition)
    fprintf(f, "void");
  else
    rdp__print_parser_param_list_sub(f, params, 1, definition);

  fprintf(f, ")");
}

/* A hideously inefficient set printing routine to aid debugging: endless linear searches */
static void rdp__print_parser_token_set(FILE * f, set__ tokens, symbol__ * base)
{
  int count;               /* present set element */
  int first = 1;

  for (count = set__size-1; count >=0; count--)
    if (set__inc_element(tokens, count))
    {
      symbol__ *p = base->next_scope;

      while (p != NULL && DATA(p, token_value) != count)
	p = p->next_scope;

      if (!first)
	fprintf(f, ", ");
      else
	first = 0;

      if ( p == NULL) /* must have been EOF */
	fprintf(f, "EOF");
      else
      if (DATA(p, kind) == K_TOKEN)
      {
	fprintf(f, "'");
	rdp__print_parser_string(f, p->id + 1);
	fprintf(f, "'");
      }
      else
	fprintf(f, p->id);
    }
}

static void rdp__print_parser_production_name(FILE * f, symbol__ * n, char *open_comm, char *close_comm)
{
  switch (DATA(n, kind))
  {
  case K_CODE:
    rdp__column += fprintf(f, "[*%s*]", n->id + 1);
    break;
  case K_TOKEN:
    if (rdp__is_valid_C_id(n->id + 1))
      rdp__column += fprintf(f, "T_%s", n->id + 1);
    else
      rdp__column += fprintf(f, "T_%u %s %s %s", DATA(n, token_value), open_comm, n->id + 1, close_comm);
    break;

  case K_INTEGER_PRIMITIVE:
  case K_REAL:
  case K_STRING_PRIMITIVE:
    rdp__column += fprintf(f, "P_%s", n->id);
    break;
  default:
    rdp__column += fprintf(f, "%s", n->id);
    break;
  }

}

/* A hideously inefficient token set printing routine to aid debugging: endless linear searches */
static void rdp__print_parser_token_set_enum(FILE * f, set__ tokens, symbol__ * base)
{
  int count;               /* present set element */
  int first = 1;

  for (count = set__size-1; count >=0; count--)
    if (set__inc_element(tokens, count))
    {
      symbol__ *p = base->next_scope;

      while (p != NULL && DATA(p, token_value) != count)
	p = p->next_scope;

      if (!first)
	fprintf(f, ", ");
      else
	first = 0;

      if ( p == NULL) /* must have been EOF */
	fprintf(f, "P_EOF");
      else
	rdp__print_parser_production_name(f, p, "/*", "*/");
    }
}

/* dignostic print routines */
static void rdp__print_sub_sequence(FILE * outputfile, symbol__ * production, int expand);
static void rdp__print_sub_alternate(FILE * outputfile, symbol__ * production, int expand);

static void rdp__print_sub_item(FILE * outputfile, symbol__ * prod, int expand)
{
  switch (DATA(prod, kind))
  {
 case K_INTEGER_PRIMITIVE:
 case K_STRING_PRIMITIVE:
 case K_REAL:
    fprintf(outputfile, "%s ", prod->id);
    break;
  case K_TOKEN:
    fprintf(outputfile, "'%s' ", prod->id + 1);
    break;
  case K_CODE:
    /* Don't print anything */
    break;
  case K_PRIMARY:
    fprintf(outputfile, "%s ", prod->id);
    break;
  case K_SEQUENCE:
    rdp__print_sub_sequence(outputfile, prod, expand);
    break;
  case K_DO_FIRST:
    if (expand)
    {
      fprintf(outputfile, "( ");
      rdp__print_sub_alternate(outputfile, prod, expand);
      fprintf(outputfile, ") ");
    }
    else
      fprintf(outputfile, "%s ", prod->id);
    break;
  case K_LIST:
    if (expand)
    {
      fprintf(outputfile, "< ");
      rdp__print_sub_alternate(outputfile, prod, expand);
      fprintf(outputfile, "> ");
    }
    else
      fprintf(outputfile, "%s ", prod->id);
    break;
  case K_CONDITIONAL:
    if (expand)
    {
      fprintf(outputfile, "[ ");
      rdp__print_sub_alternate(outputfile, prod, expand);
      fprintf(outputfile, "] ");
    }
    else
      fprintf(outputfile, "%s ", prod->id);
    break;
  case K_ITERATION:
    if (expand)
    {
      fprintf(outputfile, "{ ");
      rdp__print_sub_alternate(outputfile, prod, expand);
      fprintf(outputfile, "} ");
    }
    else
      fprintf(outputfile, "%s ", prod->id);
    break;
  }
}

static void rdp__print_sub_sequence(FILE * outputfile, symbol__ * production, int expand)
{
  rdp__list *list = DATA(production, list);

  while (list != NULL)
  {
    rdp__print_sub_item(outputfile, list->production, expand);
    list = list->next;
  }
}

static void rdp__print_sub_alternate(FILE * outputfile, symbol__ * production, int expand)
{
  rdp__list *list = DATA(production, list);

  while (list != NULL)
  {
    rdp__print_sub_item(outputfile, list->production, expand);

    if ((list = list->next) != NULL)
      fprintf(outputfile, "| ");
  }
}

symbol__ *rdp__find(char *id, kind_type kind)
{
  symbol__ *temp;

  if ((temp = symbol__lookup_id(id)) == NULL)
  {
    temp = symbol__insert_id(id);
    temp->token = P_ID;
    temp->data.p = crash__calloc(sizeof(rdp__data), "symbol data");
    DATA(temp, kind) = kind;
    DATA(temp, first) = set__make();
    DATA(temp, first_cardinality) = 0;
    DATA(temp, follow) = set__construct(P_EOF, SET_END);
    DATA(temp, follow_cardinality) = 1;
    DATA(temp, return_type_stars) = 0;
    switch (kind)
    {
    case K_INTEGER_PRIMITIVE:
      DATA(temp, return_type) = "long int";
      break;
    case K_TOKEN:
      DATA(temp, return_type) = "int";
      break;
    case K_REAL:
      DATA(temp, return_type) = "double";
      break;
    case K_STRING_PRIMITIVE:
      DATA(temp, return_type) = "char";
      DATA(temp, return_type_stars) = 1;
      break;
    default:
      DATA(temp, return_type) = "void";
      break;
    }
  }

  return temp;
}

symbol__ *rdp__find_extended(char *open, char *close, enum scan__extended_class_type extended_class)
{
  symbol__ *result;

  rdp__check_token_valid(open);
  result = rdp__find(open, K_TOKEN);
  DATA(result, extended_class) = extended_class;
  DATA(result, close) = close;
  return result;
}


void rdp__check_eoln(char *id)
{
  if (strcmp(id, "EOLN") == 0)
    rdp__dir_newline_visible = 1; /* Grammar contains an explicit EOLN */
}

void rdp__check_token_valid(char *id)
{
  if (*(id + 1) == 0)
    scan__error_echo("Empty tokens are not allowed: use [ ... ] instead");
  /* Test for embedded spaces in token */
  {
    char *c = id + 1;
    int bad = 0;

    while (*c != 0)
      bad |= isspace(*c++);
    if (bad)
      scan__error_echo("Tokens must not contain white space");
  }
}

void rdp__check_string_token_valid(char *id)
{
  rdp__check_token_valid(id);   /* make sure it's not empty or contains spaces */
  if (*(id + 2) != 0)
    scan__error_echo("String delimiter tokens must be exactly one character long");
}

void rdp__check_comment_token_valid(char *id)
{
  rdp__check_token_valid(id);   /* make sure it's not empty or contains spaces */
  if (!(*(id + 2) == 0 || *(id + 3) == 0))
    scan__error_echo("Comment delimiter tokens must be less than two characters long");
}

void rdp__check_prod_name_valid(char *id)
{
  /* Test for two consecutive _ in token */
  char *c = id;
  int bad = 0;

  while (*c != 0)
    bad |= (*c++ == '_' && *c == '_');
  if (bad)
    scan__error_echo("Identifier '%s' contains two consecutive underscores", id);
}

static int rdp__follow_changed; /* repeat until done flag for follow sets */

/****************************************************************************
*
* Grammar checking routines
*
****************************************************************************/
static void rdp__count_productions(symbol__ * base)
{
  unsigned primaries = 0,
   tokens = 0,
   internals = 0,
   codes = 0;

  symbol__ *temp = base->next_scope;

  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_PRIMARY)
      primaries++;
    else if (DATA(temp, kind) == K_TOKEN)
      tokens++;
    else if (DATA(temp, kind) == K_CODE)
      codes++;
    else
      internals++;

    temp = temp->next_scope;
  }

  if (rdp__dir_set_size < (tokens+P_TOP))
    rdp__dir_set_size = tokens+P_TOP; /* initialise token set size */

  if (rdp__verbose)
    fprintf(MESSAGES, " %u EBNF productions, %u tokens, %u semantic actions and %u internal productions\n",
	    primaries, tokens, codes, internals);
}

static void rdp__first(symbol__ * prod)
{
  if (DATA(prod, in_use))       /* something has gone wrong */
  {
    scan__error("LL(1) violation: production '%s' is left recursive", prod->id); /* and return */
    return;
  }

  if (!DATA(prod, first_done))  /* something to do */
  {
    rdp__list *list = DATA(prod, list); /* set up alternates pointer */

    DATA(prod, in_use) = 1;     /* mark this production as being processed */

    if (DATA(prod, kind) == K_SEQUENCE) /* sequences are treated differently */
    {
      DATA(prod, contains_null) = 1;  /* set up list flag */
      while (list != NULL && DATA(prod, contains_null)) /* scan until non-empty alternate is found */
      {
	if (!DATA(list->production, first_done))  /* do first */
          rdp__first(list->production);

        set__unite_set(DATA(prod, first), DATA(list->production, first)); /* add alternate first set to production first set */
        DATA(prod, contains_null) = DATA(list->production, contains_null);  /* set contains_null flag */

        list = list->next;
      }
    }
    else
      while (list != NULL)      /* scan all alternates */
      {
	if (!DATA(list->production, first_done))  /* do first */
	  rdp__first(list->production);

        set__unite_set(DATA(prod, first), DATA(list->production, first)); /* add alternate first set to production first set */
        DATA(prod, contains_null) |= DATA(list->production, contains_null); /* OR in contains_null flag */
	list = list->next;
      }
    DATA(prod, in_use) = 0;     /* production is no longer in use */
    DATA(prod, first_done) = 1; /* first set is now complete */
    /* and set cardinality */
    DATA(prod, first_cardinality) = set__cardinality(DATA(prod, first));
  }
}

/* scan a sequence production for follow set changes */
static void rdp__follow_sequence(symbol__ * prod)
{
  rdp__list *check = DATA(prod, list);  /* pointer to sequence list */

  while (check != NULL)         /* scan entire sequence and add to follow sets */
  {
    rdp__list *following = check; /* temporary to look at followers */
    unsigned old_cardinality = DATA(check->production, follow_cardinality);

    do                          /* scan up list adding first sets of trailing productions */
    {
      following = following->next;
      if (following == NULL)    /* We hit end of sequence */
	set__unite_set(DATA(check->production, follow), DATA(prod, follow));
      else
	set__unite_set(DATA(check->production, follow), DATA(following->production, first));
    }
    while (following != NULL && DATA(following->production, contains_null));

    /* Update cardinality changed flag */
    rdp__follow_changed |= ((DATA(check->production, follow_cardinality) =
			     set__cardinality(DATA(check->production, follow))
                             ) != old_cardinality);

    check = check->next;        /* step to next item in sequence */
  }
}

/* scan an alternate for follow set changes */
static void rdp__follow_alternate(symbol__ * prod)
{
  rdp__list *check = DATA(prod, list);  /* pointer to alternate list */

  while (check != NULL)
  {
    unsigned old_cardinality = DATA(check->production, follow_cardinality);

    set__unite_set(DATA(check->production, follow), DATA(prod, follow));

    rdp__follow_changed |= ((DATA(check->production, follow_cardinality) =
                             set__cardinality(DATA(check->production, follow))
			     ) != old_cardinality);
    check = check->next;
  }
}

static void rdp__find_follow(symbol__ * base)
{
  symbol__ *temp;
  unsigned follow_pass = 0;

  do
  {
    follow_pass++;
    rdp__follow_changed = 0;
    temp = base->next_scope;
    while (temp != NULL)
    {
      if (DATA(temp, kind) == K_SEQUENCE) /* only need to scan sequences */
        rdp__follow_sequence(temp);
      else
        rdp__follow_alternate(temp);
      temp = temp->next_scope;
    }
  }
  while (rdp__follow_changed);

  if (rdp__verbose)
    scan__info("Follow sets stabilised after %u pass%s", follow_pass, follow_pass == 1 ? "" : "es");

}

static int rdp__check_identifier(char *id)
{
  if (symbol__lookup_id(id))
  {
    scan__error("Identifier '%s' is a C++ reserved word or library identifier", id);
    return 1;
  }
  else
    return 0;
}

static int rdp__check_reserved_words(void)
{
  int bad = 0, temp = 0;
  char *rdp__reserved_words[] = {RDP__RESERVED_WORDS, NULL};

  while (rdp__reserved_words[temp] != NULL)
    bad |= rdp__check_identifier(rdp__reserved_words[temp++]);

  return bad;
}

/* check for empty alternates. mark up code sequences while we're at it */
static int rdp__check_empties(symbol__ * base)
{
  int bad = 0;
  symbol__ *temp = base->next_scope;

  while (temp != NULL)
  {
    rdp__list *list = DATA(temp, list);
    int k = DATA(temp, kind),
     bad_alternate = 1;

    if (k == K_PRIMARY && DATA(temp, call_count == 0 ))
      scan__warning("Production '%s' is never referenced: no C code will be generated", temp->id);

    if (list == NULL && k == K_PRIMARY)
    {
      scan__error("Production '%s' is empty", temp->id);
      bad = 1;
    }

    if (k == K_SEQUENCE)        /* check for empty alternates and mark up code */
    {
      while (list != NULL)
      {
        if (DATA(list->production, kind) == K_CODE) /* check code position */
          if (list->next == NULL)                   /* last in list? */
            DATA(list->production, code_terminator) = 1;
          else
	    if (DATA(list->next->production, kind) == K_CODE) /* is the next one code? */
	      DATA(list->next->production, code_successor) = 1; /* next one is code successor */
	    else
	      DATA(list->production, code_terminator) =1;  /* this one is code terminator */

	if (DATA(list->production, kind) != K_CODE)
          bad_alternate = 0;
        list = list->next;
      }
    }
    else
      bad_alternate = 0;

    if (bad_alternate)
    {
      if (DATA(temp, list) == NULL)
	scan__error("LL(1) violation: subproduction '%s' is empty", temp->id);
      else
      {
/*        scan__error("LL(1) violation: subproduction '%s' contains only code", temp->id);*/
	DATA(temp, code_only) = 1;
      }
      bad = 1;
    }
    temp = temp->next_scope;
  }

  /* Now go over again updating primaries to mark code only productions */
  temp = base->next_scope;

  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_PRIMARY && DATA(temp, list) != NULL)
      if (DATA(temp,list)->next == NULL && DATA(DATA(temp,list)->production, code_only))
        DATA(temp, code_only) = 1;
    temp = temp->next_scope;
  }
  return bad;
}

static void rdp__find_first(symbol__ * base)
{
  symbol__ *temp = base->next_scope;

  while (temp != NULL)
  {
    rdp__first(temp);
    temp = temp->next_scope;
  }
}

static int rdp__check_disjoint(symbol__ * base)
{
  int bad = 0;
  set__ work = set__make();

  symbol__ *temp = base->next_scope;

  while (temp != NULL)
  {
    if (set__inc_element(rdp__production_set, DATA(temp, kind)) && DATA(temp, kind) != K_SEQUENCE)
    {
      rdp__list *left = DATA(temp, list);

      while (left != NULL)
      {
	rdp__list *right = left->next;

        while (right != NULL)
        {
	  set__assign_set(work, DATA(left->production, first));
	  set__intersect_set(work, DATA(right->production, first));

	  if (set__cardinality(work) != 0)
          {
	    char *tempstr = strstr(temp->id, "__"); /* find __ in name */
            size_t length;

	    if (tempstr != NULL)
              length = (size_t) (tempstr - temp->id); /* length of prefix */
	    else
              length = strlen(temp->id);

	    scan__error("LL(1) violation: production '%.*s'", length, temp->id);
            fprintf(MESSAGES, " alternates \" ");
	    rdp__print_sub_item(MESSAGES, left->production, 1);
	    fprintf(MESSAGES, "\"\n and        \" ");
	    rdp__print_sub_item(MESSAGES, right->production, 1);
	    fprintf(MESSAGES, "\"\n share these start tokens:\n ");
            rdp__print_parser_token_set(MESSAGES, work, base);
	    fprintf(MESSAGES, "\n");
            bad = 1;
          }
          right = right->next;
	}
	left = left->next;
      }
    }
    temp = temp->next_scope;
  }
  return bad;
}

static int rdp__check_nullable(symbol__ * base)
{
  int bad = 0;
  symbol__ *temp = base->next_scope;
  set__ work = set__make();

  while (temp != NULL)
  {
    if (DATA(temp, contains_null) && (DATA(temp, kind) != K_CODE) )
    {
      set__assign_set(work, DATA(temp, first));
      set__intersect_set(work, DATA(temp, follow));

      if (set__cardinality(work) != 0)
      {
	char *tempstr = strstr(temp->id, "__"); /* find __ in name */
	size_t length;

	if (tempstr != NULL)
	  length = (size_t) (tempstr - temp->id); /* length of prefix */
	else
	  length = strlen(temp->id);

	scan__error("LL(1) violation: production '%.*s' subproduction", length, temp->id);
	rdp__print_sub_item(MESSAGES, temp, 1);
	fprintf(MESSAGES, "\ncontains null but first and follow sets both include: ");

	rdp__print_parser_token_set(MESSAGES, work, base);
        fprintf(MESSAGES, "\n");
	bad = 1;
      }
    }
    temp = temp->next_scope;
  }
  return bad;
}

static void rdp__update_follow_sets(symbol__ * base)
{
  symbol__ *temp = base->next_scope;

  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_ITERATION)
    {
      set__unite_set(DATA(temp, follow), DATA(temp, first));
      DATA(temp, follow_cardinality) = set__cardinality(DATA(temp, follow));
    }
    temp = temp->next_scope;
  }
}

static int rdp__bad_grammar(symbol__ * base)
{
  int bad = 0;

  /* Count productions and produce statistics */
  rdp__count_productions(base);

  /* find first sets */
  if (rdp__verbose)
    scan__info("Generating first sets");
  rdp__find_first(base);

  /* find follow sets */
  if (rdp__verbose)
    scan__info("Generating follow sets");
  rdp__find_follow(base);

  /* check for C reserved words */
  if (rdp__verbose)
    scan__info("Checking for clashes with reserved words");
  bad |= rdp__check_reserved_words();

  /* Check for empties */
  if (rdp__verbose)
    scan__info("Checking for empty and unused productions and alternates");
  bad |= rdp__check_empties(base);

  /* check that for each production, all alternates have unique start tokens */
  if (rdp__verbose)
    scan__info("Checking for disjoint first sets");
  bad |= rdp__check_disjoint(base);

  /* check that first(a) - follow (a) is empty for nullable a */
  if (rdp__verbose)
    scan__info("Checking nullable productions");
  bad |= rdp__check_nullable(base);

  /* add first() to follow() for iterations so that error handling doesn't just eat entire file! */
  if (rdp__verbose)
    scan__info("Updating follow sets");
  rdp__update_follow_sets(base);
  /* re-close follow sets */
  rdp__find_follow(base);

  return bad;
}

/* Output line breaking support */
static void rdp__indent(FILE * f)
{
  unsigned temp;

  for (temp = 0; temp < rdp__indentation; temp++)
    fprintf(f, "  ");

  rdp__column = rdp__indentation * 2;
}

static void rdp__check_break(FILE * f)
{
  if (rdp__column >= rdp__line_break)
  {
    rdp__column = 0;
    fprintf(f, "\n");
  }
}

static void rdp__print_header(char *headerfilename, symbol__ * base)
{
  FILE *headerfile;
  symbol__ *temp;
  int count;

  if (rdp__verbose)
    scan__info("Dumping parser header file to '%s'", headerfilename);

  if (*headerfilename == '-')
    headerfile = stdout;
  else if ((headerfile = fopen(headerfilename, "w")) == NULL)
    crash__("Can't open header output file");

  fprintf(headerfile,
	  "/****************************************************************************\n"
	  "*\n"
	  "* Header file generated by RDP on ");

  print_time(headerfile);

  fprintf(headerfile,
	  " from %s\n"
	  "*\n"
	  "****************************************************************************/\n"
          "#include \"scan.h\"\n",
	  scan__add_filetype(rdp__sourcefilename, "bnf"));

  /* print token enumeration */
  rdp__column = 0;
  fprintf(headerfile, "\n/* Token enumeration */\nenum\n{\n");
  rdp__column += fprintf(headerfile, "TT_DUMMY = P_TOP");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_TOKEN)
    {
      rdp__column += fprintf(headerfile, ", ");
      rdp__check_break(headerfile);
      rdp__print_parser_production_name(headerfile, temp, "/*", "*/");
    }
    temp = temp->next_scope;
  }
  fprintf(headerfile, ",\nTT_TOP\n};\n\n");


  /* print start production prototype */
  fprintf(headerfile, "/* Parser start production */\n");
  fprintf(headerfile, "%s", DATA(rdp__start_prod, return_type));
  for (count = 0; count < DATA(rdp__start_prod, return_type_stars); count++)
    fprintf(headerfile, "*");
  fprintf(headerfile, " %s", rdp__start_prod->id);

  rdp__print_parser_param_list(headerfile, DATA(rdp__start_prod, params), 1);

  fprintf(headerfile, ";\n\n/* End of %s */\n",
	  scan__add_filetype(headerfilename, "h"));

  if (headerfile != stdout)
    fclose(headerfile);
}

static void rdp__print_parser_alternate(FILE * outputfile, symbol__ * production, symbol__ * primary);
static void rdp__print_parser_sequence(FILE * outputfile, symbol__ * production, symbol__ * primary);

static void rdp__print_parser_item(FILE * outputfile, symbol__ * prod, symbol__ * primary, char *return_name, rdp__param_list* actuals)
{
  if (!(DATA(prod, kind) == K_CODE && DATA(prod, code_successor)))
    rdp__indent(outputfile); /* Don't indent code sequence-internal or inline items */

  switch (DATA(prod, kind))
  {
  case K_INTEGER_PRIMITIVE:
  case K_REAL:
  case K_STRING_PRIMITIVE:
  case K_TOKEN:
    fprintf(outputfile, "scan__test(");
    if (return_name != NULL && !rdp__parser_only) /* disable if -p option used */
      fprintf(outputfile, "(%s = %s", return_name,
	      DATA(prod, extended_class) != E_SIMPLE ||
	      DATA(prod, kind) == K_STRING_PRIMITIVE ? "scan__sym->id," :
	      DATA(prod, kind) == K_REAL ? "scan__sym->data.r," :
	      DATA(prod, kind) == K_INTEGER_PRIMITIVE ? "scan__sym->data.i," : "");

    rdp__print_parser_production_name(outputfile, prod, "/*", "*/");
    if (return_name != NULL && !rdp__parser_only) /* disable if -p option used */
      fprintf(outputfile, ")");
    fprintf(outputfile, ", %s_stop, \"Expecting '", primary->id);
    rdp__print_parser_string(outputfile, DATA(prod, kind) == K_TOKEN ? prod->id + 1 : prod->id);
    fprintf(outputfile, "'\");\n");
    rdp__indent(outputfile);
    fprintf(outputfile, "scan__();\n");
    break;
  case K_CODE:
    if (!rdp__parser_only)      /* disabled by -p option */
    {
      char *temp = prod->id + 1;

      if (DATA(prod, code_pass) != 0)
	fprintf(outputfile, "if (rdp__pass == %u) { \\\n", DATA(prod, code_pass));

      while (*temp != '\0')
      {
	if (*temp == '\n') fprintf(outputfile, " \\\n");
	else fprintf(outputfile, "%c", *temp);
	temp++;
      }

      if (DATA(prod, code_pass) != 0)
	fprintf(outputfile, " \\\n}");

      if (DATA(prod, kind) == K_CODE && DATA(prod, code_terminator))
	fprintf(outputfile, "\n"); /* terminate semantic actions tidily */
    }
    break;
  case K_PRIMARY:
    if (return_name != NULL && !rdp__parser_only) /* disable if -p option set! */
      fprintf(outputfile, "%s = ", return_name);
    fprintf(outputfile, "%s", prod->id);
    if (!(DATA(prod, code_only) && actuals == NULL))
      rdp__print_parser_param_list(outputfile, actuals, 0);
    fprintf(outputfile,";\n");
    break;
  case K_SEQUENCE:
    crash__("Internal error: Unexpected alternate in sequence");
    break;
  case K_DO_FIRST:
    fprintf(outputfile, "{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(outputfile, prod, primary);
    rdp__indentation--;
    rdp__indent(outputfile);
    fprintf(outputfile, "}\n");
    break;
  case K_LIST:
    fprintf(outputfile, "{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(outputfile, prod, primary);
    rdp__indentation--;
    rdp__indent(outputfile);
    fprintf(outputfile, "}\n");
    rdp__indent(outputfile);
    fprintf(outputfile, "while (scan__sym->token == ");
    rdp__print_parser_production_name(outputfile, DATA(prod, supplementary_token), "/*", "*/");
    fprintf(outputfile, ")\n");
    rdp__indent(outputfile);
    fprintf(outputfile, "{\n");
    rdp__indentation++;
    rdp__indent(outputfile);
    fprintf(outputfile, "scan__(); /* skip delimiter */\n");
    rdp__print_parser_alternate(outputfile, prod, primary);
    rdp__indentation--;
    rdp__indent(outputfile);
    fprintf(outputfile, "}\n");
    break;
  case K_CONDITIONAL:
    fprintf(outputfile, "if (set__inc_element(%s_first, scan__sym->token))\n", prod->id);
    rdp__indent(outputfile);
    fprintf(outputfile, "{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(outputfile, prod, primary);
    rdp__indentation--;
    rdp__indent(outputfile);
    fprintf(outputfile, "}\n");
    break;
  case K_ITERATION:
    fprintf(outputfile, "while (set__inc_element(%s_first, scan__sym->token))\n", prod->id);
    rdp__indent(outputfile);
    fprintf(outputfile, "{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(outputfile, prod, primary);
    rdp__indentation--;
    rdp__indent(outputfile);
    fprintf(outputfile, "}\n");
    break;
  }
}

static void rdp__print_parser_sequence(FILE * outputfile, symbol__ * production, symbol__ * primary)
{
  rdp__list *list = DATA(production, list);

  while (list != NULL)
  {
    rdp__print_parser_item(outputfile, list->production, primary, list->return_name, list->actuals);
    list = list->next;
  }
}

static void rdp__print_parser_alternate(FILE * outputfile, symbol__ * production, symbol__ * primary)
{
  rdp__list *list = DATA(production, list);

  if (list->next == NULL)       /* special case: only one alternate */
    rdp__print_parser_sequence(outputfile, list->production, primary);
  else
  {
    while (list != NULL)
    {
      if (DATA(list->production, kind) != K_SEQUENCE)
	crash__("Internal error: expecting alternate production");

      rdp__indent(outputfile);
      fprintf(outputfile, "if (set__inc_element(%s_first, scan__sym->token))\n", list->production->id);

      rdp__indent(outputfile);
      fprintf(outputfile, "{\n");
      rdp__indentation++;

      rdp__print_parser_sequence(outputfile, list->production, primary);

      rdp__indentation--;
      rdp__indent(outputfile);

      fprintf(outputfile, "}\n");

      if ((list = list->next) != NULL)
      {
        rdp__indent(outputfile);
        fprintf(outputfile, "else\n");
      }
    }
  }
}

static void rdp__print_collect_declarations(symbol__ * base, symbol__ * scope)
{
  rdp__list *list = DATA(base, list);

  while (list != NULL)
  {
    if (DATA(list->production, kind) != K_PRIMARY)
      rdp__print_collect_declarations(list->production, scope);
    if (list->return_name != NULL)
      /* insert symbol and point at parent data block */
      if (symbol__lookup_id_scope(list->return_name, scope) == NULL)
	symbol__insert_id(list->return_name)->data.p = list->production->data.p;
    list = list->next;
  }
}

static void rdp__print_parser_primaries(FILE * outputfile, symbol__ * base)
{
  symbol__ *temp = base->next_scope;

  fprintf(outputfile, "\n/* Parser functions */\n");
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_PRIMARY && (DATA(temp, call_count) > 0) && !DATA(temp, code_only))
    {
      int count,
       is_void = (strcmp(DATA(temp, return_type), "void") == 0);

      symbol__ *locals = symbol__new_scope(temp->id);

      if (temp != rdp__start_prod)
	fprintf(outputfile,"static ");

      fprintf(outputfile, "%s", DATA(temp, return_type));

      for (count = 0; count < DATA(temp, return_type_stars); count++)
	fprintf(outputfile, "*");
      fprintf(outputfile, " %s", temp->id);

      rdp__print_parser_param_list(outputfile, DATA(temp, params), 1);

      fprintf(outputfile, "\n{\n");

      /* scan all subproductions and add return variables to symbol__ table */
      if (!is_void)
	symbol__insert_id("result")->data.p = temp->data.p; /* loop back on ourselves */

      rdp__print_collect_declarations(temp, locals);

      symbol__scope_invisible(locals);
      if (locals->next_scope != NULL)
	do
	{
          locals = locals->next_scope;
          fprintf(outputfile, "  %s", DATA(locals, return_type));
          for (count = 0; count < DATA(locals, return_type_stars); count++)
	    fprintf(outputfile, "*");
	  fprintf(outputfile, " %s;\n", locals->id);
        }
        while (locals->next_scope != NULL);


      /* Test to see if production contains null, and exit accordingly */
      if (DATA(temp, contains_null))
	fprintf(outputfile, "  if (scan__test_set(%s_first, %s_stop, NULL))  /* contains null, so no error */\n"
                "  {\n", temp->id, temp->id);
      else
      {
	if (rdp__error_production_name)
	  fprintf(outputfile, "  if (scan__test_set(%s_first, %s_stop, \"In production '%s', expecting ", temp->id, temp->id, temp->id);
	else
	  fprintf(outputfile, "  if (scan__test_set(%s_first, %s_stop, \"Expecting ", temp->id, temp->id);
	if (DATA(temp, follow_cardinality) > 1)
	  fprintf(outputfile, "one of ");
	rdp__print_parser_token_set(outputfile, DATA(temp, first), base);
	fprintf(outputfile, "\"))\n  {\n");
      }
      rdp__indentation = 2;

      rdp__print_parser_alternate(outputfile, temp, temp);
      /* add error handling on exit */
      if (rdp__error_production_name)
	fprintf(outputfile, "    scan__test_set(%s_stop, %s_stop, \"In production '%s', expecting ", temp->id, temp->id, temp->id);
      else
	fprintf(outputfile, "    scan__test_set(%s_stop, %s_stop, \"Expecting ", temp->id, temp->id);
      if (DATA(temp, follow_cardinality) > 1)
	fprintf(outputfile, "one of ");
      rdp__print_parser_token_set(outputfile, DATA(temp, follow),base);
      fprintf(outputfile, "\");\n  }\n%s}\n\n",
              is_void ? "" : "  return result;\n");
    }
    temp = temp->next_scope;
  }
}

static void rdp__print_parser_include_line(FILE * outputfile, rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_include_line(outputfile, p->next);
  fprintf(outputfile, "#include \"%s\"\n", p->str1);
}

static void rdp__print_parser_help_line(FILE * outputfile, rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_help_line(outputfile, p->next);
  fprintf(outputfile, "                  \"%s%s\\n\"\n", p->str2 == NULL ? "" : "-", p->str1);
}

static void rdp__print_parser_switch_line(FILE * outputfile, rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_switch_line(outputfile, p->next);
  if (p->str2 != NULL)
    fprintf(outputfile, "         case '%c':\n           %s\n           break;\n", *(p->str1), p->str2);
}

static void rdp__print_parser(char *outputfilename, symbol__ * base)
{
  FILE *outputfile;
  symbol__ *temp;

  if (rdp__verbose)
    scan__info("Dumping parser main file to '%s'", outputfilename);

  if (*outputfilename == '-')
    outputfile = stdout;
  else if ((outputfile = fopen(outputfilename, "w")) == NULL)
    crash__("Can't open parser output file");

  /* print main file header */
  fprintf(outputfile,
	  "/****************************************************************************\n"
	  "*\n"
	  "* Parser generated by RDP on ");

  print_time(outputfile);

  fprintf(outputfile,
	  " from %s\n"
	  "*\n"
	  "****************************************************************************/\n"
	  "#include <time.h>\n"
	  "#include <stdio.h>\n"
	  "#include \"params.h\"\n"
	  "#include \"crash.h\"\n"
	  "#include \"symbol.h\"\n"
	  "#include \"set.h\"\n"
	  "#include \"scan.h\"\n",
	  scan__add_filetype(rdp__sourcefilename, "bnf"));

  if (*outputfilename != '-')   /* suppress if stdout is destination: the headerfile */
    fprintf(outputfile,         /* automatically be prepended to the parser file */
	    "#include \"%s\"\n", scan__add_filetype(outputfilename, "h"));

  if (rdp__dir_include != NULL)
    rdp__print_parser_include_line(outputfile, rdp__dir_include);

  fprintf(outputfile,
	  "\nchar\n *rdp__sourcefilename = NULL,   /* source file name */\n"
	  " *rdp__outputfilename = \"%s\",      /* output file name */\n", rdp__dir_output_file);
  fprintf(outputfile,
	  "  rdp__verbose = 0,                   /* verbosity flag */\n"
	  "  rdp__pass;                      /* pass number */\n\n"
	  "int rdp__error_return = 0;        /* return value for main routine */\n\n"
	  "symbol__\n"
	  " *rdp__base;                          /* pointer to application scope */\n\n"
	  "static void rdp__help(char *msg)\n"
	  "{\n"
	  "  crash__(\"%%s\\n\\n\"\n"
	  "          \"%s\\n\"\n"
	  "          \"Generated on ", rdp__dir_title);

  print_time(outputfile);

  fprintf(outputfile,
	  " and compiled on \" __DATE__ \" at \" __TIME__ \"\\n\\n\"\n");

  fprintf(outputfile,
	  "          \"Usage: %.*s [options] source",
	  (int) strcspn(outputfilename, "."), outputfilename);

  if (*rdp__dir_suffix != 0)    /* skip suffix print if no suffix defined */
    fprintf(outputfile,
	    "[.%s]", rdp__dir_suffix);

  fprintf(outputfile,
	  "\\n\\n\"\n"
	  "          \"Options:\\n\\n\"\n"
	  "          \"-f  Filter mode (read from stdin and write to stdout)\\n\"\n"
	  "          \"-l  Make a listing\\n\"\n"
	  "          \"-ofilename Write output to filename\\n\"\n"
	  "          \"-s  Echo each scanner symbol as it is read\\n\"\n"
	  "          \"-S  Print summary symbol table statistics\\n\"\n"
	  "          \"-tn Tab expansion width (default %u)\\n\"\n"
          "          \"-Tn Text buffer size in bytes for scanner (default %u)\\n\"\n"
          "          \"-v  Set verbose mode\\n\"\n",
	  rdp__dir_tab_width, rdp__dir_text_size);

  if (rdp__dir_help != NULL)
    rdp__print_parser_help_line(outputfile, rdp__dir_help);

  fprintf(outputfile,
          "          ,msg == NULL ? \"\" : msg);\n"
	  "}\n");

  /* print load keyword function */
  rdp__column = 0;
  fprintf(outputfile, "\n/* Load keywords */\nstatic void rdp__load_keywords(void)\n{\n");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_TOKEN)
    {
      fprintf(outputfile, "  scan__load_%skeyword(\"",
	      DATA(temp, extended_class) == E_SIMPLE ? "" : "extended_");
      rdp__print_parser_string(outputfile, temp->id + 1);
      fprintf(outputfile, "\", ");
      rdp__print_parser_production_name(outputfile, temp, "/*", "*/");
      if (DATA(temp, extended_class) != E_SIMPLE)
      {
        if (DATA(temp, close) == NULL)
          fprintf(outputfile, ", %i, NULL", DATA(temp, extended_class));
        else
	{
	  fprintf(outputfile, ", %i, \"", DATA(temp, extended_class));
          rdp__print_parser_string(outputfile, DATA(temp, close) + 1);
          fprintf(outputfile, "\"");
        }
      }
      fprintf(outputfile, ");\n");
    }
    temp = temp->next_scope;
  }
  fprintf(outputfile, "}\n");

  /* print set declaration */
  fprintf(outputfile, "\n/* Set declarations */\n\n");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (set__inc_element(rdp__production_set, DATA(temp, kind)) && !DATA(temp, code_only))
    {
      fprintf(outputfile, "  set__ %s_first;", temp->id);

      if (DATA(temp, kind) == K_PRIMARY)
	fprintf(outputfile, "  set__ %s_stop;", temp->id);

      fprintf(outputfile, "\n");
    }
    temp = temp->next_scope;
  }

  fprintf(outputfile, "\n/* Initialise sets */\n\nstatic void rdp__set_initialise(void)\n{\n");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (set__inc_element(rdp__production_set, DATA(temp, kind)) && !DATA(temp, code_only))
    {
      fprintf(outputfile, "  %s_first = set__construct(", temp->id);
      rdp__print_parser_token_set_enum(outputfile, DATA(temp, first), base);
      fprintf(outputfile, ", SET_END);\n");

      if (DATA(temp, kind) == K_PRIMARY)
      {
	fprintf(outputfile, "  %s_stop = set__construct(", temp->id);
	rdp__print_parser_token_set_enum(outputfile, DATA(temp, follow), base);
	fprintf(outputfile, ",SET_END);\n");
      }
    }
    temp = temp->next_scope;
  }
  fprintf(outputfile, "}\n");

  /* print forward declarations */
  fprintf(outputfile, "\n/* Parser forward declarations and macros */\n");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_PRIMARY && (DATA(temp, call_count) > 0))
    {
      int count;

      if (DATA(temp, code_only))
      {
	fprintf(outputfile, "#define %s", temp->id);
	if (DATA(temp, params) != NULL)
	  rdp__print_parser_param_list(outputfile, DATA(temp, params), 0);
	fprintf(outputfile," ");
	rdp__print_parser_alternate(outputfile, temp, temp);
      }
      else
      {
	if (temp != rdp__start_prod)
	  fprintf(outputfile, "static ");

	fprintf(outputfile, "%s", DATA(temp, return_type));

	for (count = 0; count < DATA(temp, return_type_stars); count++)
	  fprintf(outputfile, "*");
	fprintf(outputfile, " %s", temp->id);

	rdp__print_parser_param_list(outputfile, DATA(temp, params), 1);

	fprintf(outputfile, ";\n");
      }
    }
    temp = temp->next_scope;
  }

  /* print parser definitions */
  rdp__print_parser_primaries(outputfile, base);

  /* print main line routine */
  fprintf(outputfile,
	  "int main(int argc, char *argv[])\n"
	  "{\n"
	  "  clock_t rdp__finish_time, rdp__start_time = clock();\n"
	  "  unsigned\n"
	  "    rdp__tabwidth = %u,            /* tab expansion width */\n"
	  "    rdp__textsize = %u,          /* size of scanner text array */\n"
          "    rdp__symbol_statistics = 0,  /* show symbol__ table statistics flag */\n\n"
          "    rdp__line_echo = 0;  /* make listing flag */\n\n"
          "  while (--argc > 0)\n"
          "  {\n"
          "     if ((*++argv)[0] == '-') /* switch */\n"
          "     {\n"
	  "       switch ((*argv)[1])\n"
          "       {\n"
          "         case 'f': /* filter mode */\n"
	  "           rdp__sourcefilename = \"-\";\n"
          "           rdp__outputfilename = \"-\";\n"
          "           break;\n"
          "         case 'o':\n"
	  "           if (*(*argv + 2) != 0)\n"
	  "           rdp__outputfilename = *argv + 2;\n"
	  "           break;\n"
          "         case 'S':\n"
          "           rdp__symbol_statistics = 1;\n"
          "           break;\n"
	  "         case 's':\n"
          "           scan__symbol_echo(1);\n"
          "           break;\n"
	  "         case 'l':\n"
          "           rdp__line_echo = 1;\n"
          "           break;\n"
          "         case 't':\n"
	  "           sscanf(*argv + 2, \"%%u\", &rdp__tabwidth);\n"
          "           scan__tab_width(rdp__tabwidth);\n"
	  "           break;\n"
          "         case 'T':\n"
	  "           sscanf(*argv + 2, \"%%u\", &rdp__textsize);\n"
          "           break;\n"
          "         case 'v':\n"
          "           rdp__verbose = 1;\n"
          "           break;\n",
          rdp__dir_tab_width, rdp__dir_text_size);

  if (rdp__dir_help != NULL)
    rdp__print_parser_switch_line(outputfile, rdp__dir_help);

  fprintf(outputfile,
          "         default:\n"
	  "           fprintf(MESSAGES, \"\\nUnrecognised option -%%c\", (*argv)[1]);\n"
	  "           rdp__help(\"\");\n"
          "       }\n"
          "     }\n"
          "   else\n"
	  "     rdp__sourcefilename = *argv;\n"
	  "  }\n");
  fprintf(outputfile,
          "  if (rdp__sourcefilename == NULL)\n"
	  "     rdp__help(\"No source file specified\");\n");

  fprintf(outputfile,
          "  set__init(%u);\n"
	  "  symbol__init(%u, %u);\n", rdp__dir_set_size, rdp__dir_hash_size, rdp__dir_hash_prime);

  fprintf(outputfile,
	  "  rdp__set_initialise();\n"
	  "  rdp__load_keywords();\n"
	  "  rdp__base = symbol__new_scope(\"parser\");  /* create application scope */\n"
	  "  scan__tab_width(rdp__tabwidth);\n"
	  "  scan__newline_visible(%u);\n"
	  "  scan__case_insensitive(%u);\n"
	  "  scan__show_skips(%u);\n"
	  "  scan__summarise_errors(rdp__verbose);\n"
	  "  scan__init(rdp__textsize,%u,%u);\n",
	  rdp__dir_newline_visible,
	  rdp__dir_case_insensitive, rdp__dir_show_skips,
	  rdp__dir_max_errors, rdp__dir_max_warnings);

  fprintf(outputfile,
	  "  if (rdp__verbose)\n"
	  "     fprintf(MESSAGES, \"\\n%s\\nGenerated on ", rdp__dir_title);

  print_time(outputfile);

  fprintf(outputfile,
	  " and compiled on \" __DATE__ \" at \" __TIME__ \"\\n\\n\");\n");

  if (rdp__dir_pre_parse != NULL)
    fprintf(outputfile, "  %s\n", rdp__dir_pre_parse);

  fprintf(outputfile, "  for (rdp__pass = 1; rdp__pass <= %u; rdp__pass++)\n"
	  "  {\n", rdp__dir_passes);

  if (rdp__dir_interpreter)
    fprintf(outputfile,
	    "  if (scan__total_errors() != 0)\n"
	    "    crash__(\"errors detected in source file\");   /* crash quietly */ \n"
	    "  if (rdp__pass == %u)\n"
	    "      scan__line_echo(rdp__line_echo);\n"
	    "  if (scan__open_text(\"Text mode\",\n"
	    "                      scan__load_file(scan__default_filetype(rdp__sourcefilename, \"%s\"))\n"
	    "                     ) == NULL)\n"
	    "    rdp__help(\"Can't open source file\");\n\n",
	    rdp__dir_passes, rdp__dir_suffix);
  else
    fprintf(outputfile,
	    "    if (scan__total_errors() != 0)\n"
	    "      crash__(\"errors detected in source file\");   /* crash quietly */ \n"
	    "    if (rdp__pass == %u)\n"
	    "      scan__line_echo(rdp__line_echo);\n"
	    "    if (scan__open_file(scan__default_filetype(rdp__sourcefilename, \"%s\")) == NULL)\n"
	    "      rdp__help(\"Can't open source file\");\n\n",
	    rdp__dir_passes, rdp__dir_suffix);

  fprintf(outputfile,
	  "    %s", rdp__start_prod->id);

  rdp__print_parser_param_list(outputfile, DATA(rdp__start_prod, params), 0);

  fprintf(outputfile,
	  ";            /* call parser at top level */\n"
	  "  }\n"
	  "  if (rdp__symbol_statistics)\n"
	  "    symbol__print_statistics();\n");

  if (rdp__dir_post_parse != NULL)
    fprintf(outputfile, "  %s\n", rdp__dir_post_parse);

  fprintf(outputfile,
	  "  if (rdp__verbose)\n"
          "  {\n"
          "    rdp__finish_time = clock();\n"
	  "    printf(\"\\n %.*s used %%f CPU seconds\\n\", ((double) (rdp__finish_time-rdp__start_time)) / CLOCKS_PER_SEC);\n"
          "  }\n",
	  (int) strcspn(outputfilename, "."), outputfilename);

  fprintf(outputfile, "  return rdp__error_return;\n}\n");

  fprintf(outputfile, "\n/* End of %s */\n",
	  scan__add_filetype(outputfilename, "c"));

  if (outputfile != stdout)
    fclose(outputfile);
}

void rdp__pre_parse(void)
{
  rdp__production_set = set__construct(K_PRIMARY, K_SEQUENCE, K_DO_FIRST, K_LIST, K_CONDITIONAL, K_ITERATION, SET_END);
  rdp__empty_production_set = set__construct(K_LIST, K_CONDITIONAL, K_ITERATION, SET_END);

  DATA(rdp__find("ID", K_STRING_PRIMITIVE), token_value) = P_ID; /* add predefined primitive productions */
  DATA(rdp__find("INTEGER", K_INTEGER_PRIMITIVE), token_value) = P_INTEGER;
  DATA(rdp__find("REAL", K_REAL), token_value) = P_REAL;
  DATA(rdp__find("NEW_ID", K_STRING_PRIMITIVE), token_value) = P_NEW_ID;
  DATA(rdp__find("ALT_ID", K_STRING_PRIMITIVE), token_value) = P_ALT_ID;
  DATA(rdp__find("EOLN", K_INTEGER_PRIMITIVE), token_value) = P_EOLN;
}

static void rdp__order_tokens(symbol__ * base)
{
  unsigned
   token_count = P_TOP + 1;
  symbol__ *temp = base->next_scope;

  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_TOKEN)
    {
      DATA(temp, token_value) = token_count++;
      if (token_count >= SET_END)
        crash__("Too many tokens: recompile RDP with a larger set size");
    }
    temp = temp->next_scope;
  }

  /* now set up start sets for tokens, code and primitives */
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_TOKEN || DATA(temp, kind) == K_INTEGER_PRIMITIVE ||
	DATA(temp, kind) == K_REAL || DATA(temp, kind) == K_STRING_PRIMITIVE)
    {
      set__unite_element(DATA(temp, first), DATA(temp, token_value));
      DATA(temp, first_cardinality) = set__cardinality(DATA(temp, first));
      set__unite_element(DATA(temp, follow), P_EOF);
      DATA(temp, follow_cardinality) = set__cardinality(DATA(temp, follow));

      DATA(temp, first_done) = 1;
    }
    else if (DATA(temp, kind) == K_LIST)
    {
      set__unite_element(DATA(temp, follow), DATA(DATA(temp, supplementary_token), token_value));
      DATA(temp, follow_cardinality) = set__cardinality(DATA(temp, follow));
    }
    temp = temp->next_scope;
  }

  rdp__set_width = (token_count >> 3) + 1;
}

static void rdp__add_continuations(symbol__ * base)
{
  symbol__ *temp = base->next_scope;
  char *last_token = "";        /* remember most recent token name */
  int tokens_added = 0;

  if (rdp__verbose)
    scan__info("Checking for continuation tokens");

  while (temp != NULL)          /* scan over all productions */
  {
    if (DATA(temp, kind) == K_TOKEN)
    {
      char *lo = last_token,
      *hi = temp->id + 1;       /* skip header byte */

      if (!rdp__is_valid_C_id(hi + 1))  /* ignore identifiers */
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
	  *continuation_name = scan__text;  /* remember start position */

          while (c != hi)
            scan__insert_char(*c++);  /* copy identifier */
          scan__insert_char(0); /* add a terminating null */

          if (rdp__verbose)
            scan__info("Adding continuation token '%s'", continuation_name + 1);

	  tokens_added = 1;

	  rdp__find(continuation_name, K_TOKEN);

	  hi++;
	}
      }

      last_token = temp->id + 1;
    }
    temp = temp->next_scope;
  }
  if (rdp__verbose && !tokens_added)
    scan__info("No continuation tokens needed");

}

static void rdp__dump_extended(symbol__ * base)
{
  symbol__ *temp = base->next_scope;

  if (rdp__verbose)
    fprintf(MESSAGES, "\n Expanded EBNF listing\n\n");
  while (temp != NULL)
  {
    rdp__list *list = DATA(temp, list);
    int k = DATA(temp, kind),
     count;

    if (k != K_CODE && k != K_STRING_PRIMITIVE && k != K_INTEGER_PRIMITIVE && k != K_REAL)
    {
      fprintf(MESSAGES, " ");
      rdp__print_parser_production_name(MESSAGES, temp, "(*", "*)");

      rdp__print_parser_param_list(MESSAGES, DATA(temp, params), 1);

      fprintf(MESSAGES, ":%s", DATA(temp, return_type));
      for (count = 0; count < DATA(temp, return_type_stars); count++)
	fprintf(MESSAGES, "*");
      fprintf(MESSAGES, " ::= ");

      if (k == K_TOKEN)
	fprintf(MESSAGES, "'");
      fprintf(MESSAGES, "%s",
	      k == K_TOKEN ? temp->id + 1 :
	      k == K_REAL || k == K_INTEGER_PRIMITIVE || k == K_STRING_PRIMITIVE ? "(* Scanner primitive *) " :
	      k == K_DO_FIRST ? "( " :
	      k == K_LIST ? "< " :
	      k == K_CONDITIONAL ? "[ " :
	      k == K_ITERATION ? "{ " : "");

      while (list != NULL)
      {
	rdp__print_parser_production_name(MESSAGES, list->production, "(*", "*)");
	rdp__print_parser_param_list(MESSAGES, list->actuals, 0);

	if (list->return_name != NULL)
	  fprintf(MESSAGES, ":%s", list->return_name);

	fprintf(MESSAGES, " ");
	list = list->next;
	if (k != K_SEQUENCE && list != NULL)
	  fprintf(MESSAGES, "| ");
      }

      fprintf(MESSAGES, "%s",
	      k == K_TOKEN ? "'" :
	      k == K_DO_FIRST ? ") " :
	      k == K_LIST ? "> " :
	      k == K_CONDITIONAL ? "] " :
	      k == K_ITERATION ? "} " : "");

      if (k == K_LIST)
	fprintf(MESSAGES, "'%s'", DATA(temp, supplementary_token)->id + 1);

      fprintf(MESSAGES, ".\n");

      fprintf(MESSAGES, " First set is {%s", DATA(temp, contains_null) ? "(NULL) " : "");
      rdp__print_parser_token_set(MESSAGES, DATA(temp, first), base);
      fprintf(MESSAGES, "}\n");

      fprintf(MESSAGES, " Stop set is {");
      rdp__print_parser_token_set(MESSAGES, DATA(temp, follow), base);
      fprintf(MESSAGES, "}\n");

      fprintf(MESSAGES, " Production is called %u time%c\n\n", DATA(temp, call_count), DATA(temp, call_count)==1?' ':'s');
    }

    temp = temp->next_scope;
  }
}

void rdp__post_parse(char *outputfilename, char force, symbol__ * base)
{
  if (scan__total_errors() != 0)
    crash__("EBNF file contains syntax errors");
  if (rdp__verbose)
    scan__info("");             /* skip a line */
  symbol__sort_scope(base);     /* sort productions into alphabetical order */
  rdp__add_continuations(base); /* scan through tokens and add any necessry continuations */
  symbol__sort_scope(base);     /* re-sort productions into alphabetical order */
  rdp__order_tokens(base);      /* apply token numbers to token productions */
  rdp__bad_grammar(base);       /* find the non-LL(1)-isms */

  if (rdp__expanded)            /* print out expanded BNF */
    rdp__dump_extended(base);

  if (rdp__verbose)             /* see how much text buffer space we used */
    scan__print_statistics();

  if (scan__print_total_errors(rdp__verbose))
  {
    if (force)
      scan__warning("Grammar is not LL(1) but -F switch set: writing output files");
    else
      crash__("Run aborted without creating output files: rerun with -F to override");
  }

  rdp__print_header(scan__add_filetype(outputfilename, "h"), base); /* dump header file */
  rdp__print_parser(scan__add_filetype(outputfilename, "c"), base); /* dump main file */
}

/* End of rdp_aux.c */
