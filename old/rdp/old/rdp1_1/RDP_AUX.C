/****************************************************************************
*
* RDP release 1.10 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 13 Mar 1994
*
* rdp_aux.c - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include "crash.h"
#include "symbol.h"
#include "set.h"
#include "scan.h"
#include "rdp_aux.h"

static unsigned rdp__column = 0,/* current printing column */
 rdp__indentation = 0,          /* current identation level */
 rdp__line_break = 60,          /* column to break lines on */
 rdp__set_width;                /* how many elements in set */

char
 rdp__force = 0,                /* force output files flag */
 rdp__expanded = 0,             /* flag to generate expanded bnf listing */
*rdp__primary_id;               /* identifier for parent production */

symbol__
* rdp__start_prod = NULL;       /* symbol for first production in BNF file */

unsigned rdp__component;        /* sub-production component number */

char
*rdp__dir_title = "rdparser",   /* string from TITLE directive */
*rdp__dir_suffix = "",          /* string from SUFFIX directive */
*rdp__dir_pre_process = NULL,   /* string from PRE_PROCESS directive */
*rdp__dir_post_process = NULL,  /* string from POST_PROCESS directive */
*rdp__dir_output_file = "a.out";/* string from OUTPUT_FILE directive */

unsigned rdp__dir_set_size = 0, /* number from SET_SIZE directive */
 rdp__dir_hex_dollar = 0,       /* HEX DOLLAR flag */
 rdp__dir_text_mode = 0,        /* TEXT_MODE flag */
 rdp__dir_case_insensitive = 0, /* CASE_INSENSITIVE flag */
 rdp__dir_show_skips = 0,       /* SHOW_SKIPS flag */
 rdp__dir_newline_visible = 0,  /* NEWLINE_VISIBLE flag */
 rdp__dir_passes = 1,           /* PASSES directive */
 rdp__dir_hash_size = 101,      /* HASH_SIZE directive */
 rdp__dir_hash_prime = 31,      /* HASH_PRIME dirctive */
 rdp__dir_tab_width = 8,        /* TAB_WIDTH directive */
 rdp__dir_text_size = 8192,     /* TEXT_SIZE directive */
 rdp__dir_max_errors = 25,      /* MAX_ERRORS directive */
 rdp__dir_max_warnings = 100;   /* MAX_WARNINGS directive */

struct rdp__string_list
*rdp__dir_help,                 /* strings from HELP directives */
*rdp__dir_include;              /* strings from INCLUDE directives */

set__
rdp__production_set,
rdp__empty_production_set;

/* A hideously inefficient set printing routine to aid debugging: endless linear searches */
static void rdp__print_parser_token_set(FILE * f, set__ tokens, symbol__ * base)
{
  unsigned count;               /* present set element */
  int first = 1;

  for (count = 0; count < RDP_SET_SIZE; count++)
    if (set__inc_element(tokens, count))
    {
      symbol__ *p = base->next_scope;

      while (p != NULL && DATA(p, token_value) != count)
        p = p->next_scope;

      if (!first)
        fprintf(f, ", ");
      else
        first = 0;
      if (DATA(p, kind) == K_TOKEN)
        fprintf(f, "'%s'", p->id + 1);
      else
        fprintf(f, "%s", p->id);
    }
}

/* dignostic print routines */
static void rdp__print_sub_sequence(FILE * outputfile, symbol__ * production, int expand);
static void rdp__print_sub_alternate(FILE * outputfile, symbol__ * production, int expand);

static void rdp__print_sub_item(FILE * outputfile, symbol__ * prod, int expand)
{
  switch (DATA(prod, kind))
  {
 case K_NUMBER_PRIMITIVE:
 case K_STRING_PRIMITIVE:
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
  struct rdp__list_node *list = DATA(production, list);

  while (list != NULL)
  {
    rdp__print_sub_item(outputfile, list->production, expand);
    list = list->next;
  }
}

static void rdp__print_sub_alternate(FILE * outputfile, symbol__ * production, int expand)
{
  struct rdp__list_node *list = DATA(production, list);

  while (list != NULL)
  {
    rdp__print_sub_item(outputfile, list->production, expand);

    if ((list = list->next) != NULL)
      fprintf(outputfile, "| ");
  }
}

symbol__ *rdp__find(char *id, unsigned kind, int must_be_new)
{
  symbol__ *temp;

  if ((temp = symbol__lookup_id(id)) == NULL)
  {
    temp = symbol__insert_id(id);
    temp->token = P_ID;
    temp->data = crash__calloc(sizeof(struct rdp__data_node), "symbol data");
    DATA(temp, kind) = kind;
    DATA(temp, first) = set__make();
    DATA(temp, first_cardinality) = 0;
    DATA(temp, follow) = set__construct(P_EOF, SET_END);
    DATA(temp, follow_cardinality) = 1;
    DATA(temp, return_type_stars) = 0;
    switch (kind)
    {
    case K_NUMBER_PRIMITIVE:
    case K_TOKEN:
      DATA(temp, return_type) = "int";
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
  else
    /* check to see if this production has already got a list */
  if (must_be_new && DATA(temp, been_defined))
    scan__error_echo("Doubly defined production '%s'", id);

  return temp;
}

void rdp__check_eoln(char *id)
{
  if (strcmp(id, "EOLN") == 0)
    rdp__dir_newline_visible = 1; /* Grammar contains an explicit EOLN */
}

void rdp__check_token_valid(char *id)
{
  if (*id + 1 == 0)
    scan__error_echo("Empty tokens are not allowed: use [..] instead");
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
    scan__error_echo("String tokens must be exactly one character long");
}

void rdp__check_comment_token_valid(char *id)
{
  rdp__check_token_valid(id);   /* make sure it's not empty or contains spaces */
  if (!(*(id + 2) == 0 || *(id + 3) == 0))
    scan__error_echo("String close tokens must be less than two characters long");
}

void rdp__check_prod_name_valid(char *id)
{
  /* Test for two consecutive _ in token */
  char *c = id;
  int bad = 0;

  while (*c != 0)
    bad |= (*c++ == '_' && *c == '_');
  if (bad)
    scan__error_echo("Production names must not contain two consecutive underscores");
}

static int rdp__follow_changed; /* repeat until done flag for follow sets */

/****************************************************************************
*
* Grammar checking routines
*
****************************************************************************/
static void rdp__first(symbol__ * prod)
{
  if (DATA(prod, in_use))       /* something has gone wrong */
  {
    scan__error("LL(1) violation in '%s': production is left recursive", prod->id); /* and return */
    return;
  }

  if (!DATA(prod, first_done))  /* something to do */
  {
    struct rdp__list_node *list = DATA(prod, list); /* set up alternates pointer */

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
  struct rdp__list_node *check = DATA(prod, list);  /* pointer to sequence list */

  while (check != NULL)         /* scan entire sequence and add to follow sets */
  {
    struct rdp__list_node *following = check; /* temporary to look at followers */
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
  struct rdp__list_node *check = DATA(prod, list);  /* pointer to alternate list */

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

static int rdp__bad_grammar(symbol__ * base)
{
  symbol__ *temp;
  unsigned follow_pass = 0,
   bad = 0;
  set__ work = set__make();

  /* Count productions and produce statistics */
  if (rdp__verbose)
  {
    unsigned primaries = 0,
     tokens = 0,
     internals = 0,
     codes = 0;

    temp = base->next_scope;
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

    if (rdp__dir_set_size == 0)
      rdp__dir_set_size = tokens; /* initialise token set size */

    printf(" %u EBNF productions, %u tokens, %u code items and %u internal productions\n",
           primaries, tokens, codes, internals);
  }

  /* Check for empties */
  if (rdp__verbose)
    scan__info("Checking for empty productions and alternates");

  temp = base->next_scope;
  while (temp != NULL)
  {
    struct rdp__list_node *list = DATA(temp, list);
    int k = DATA(temp, kind),
     bad_alternate = 1;

    if (list == NULL && k == K_PRIMARY)
    {
      scan__warning("Production '%s' is empty", temp->id);
      bad = 1;
    }

    if (k == K_SEQUENCE)        /* check for empty alternates */
      while (list != NULL)
      {
        if (DATA(list->production, kind) != K_CODE)
          bad_alternate = 0;
        list = list->next;
      }
    else
      bad_alternate = 0;

    if (bad_alternate)
    {
      scan__error("LL(1) violation in '%s': production has empty alternate or alternate containing only code", temp->id);
      bad = 1;
    }
    temp = temp->next_scope;
  }

  /* rdp__find first symbol__ sets */
  if (rdp__verbose)
    scan__info("Generating first sets");

  temp = base->next_scope;
  while (temp != NULL)
  {
    rdp__first(temp);
    temp = temp->next_scope;
  }

  /* add first() to follow() for iterations */
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_ITERATION)
    {
      set__unite_set(DATA(temp, follow), DATA(temp, first));
      DATA(temp, follow_cardinality) = set__cardinality(DATA(temp, follow));
    }
    temp = temp->next_scope;
  }

  /* rdp__find follow symbol__ sets */
  if (rdp__verbose)
    scan__info("Generating follow sets");

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
  /* check that for each production, all alternates have unique start tokens */
  if (rdp__verbose)
    scan__info("Checking for disjoint first sets");

  temp = base->next_scope;
  while (temp != NULL)
  {
    if (set__inc_element(rdp__production_set, DATA(temp, kind)) && DATA(temp, kind) != K_SEQUENCE)
    {
      struct rdp__list_node *left = DATA(temp, list);

      while (left != NULL)
      {
        struct rdp__list_node *right = left->next;

        while (right != NULL)
        {
          set__assign_set(work, DATA(left->production, first));
          set__intersect_set(work, DATA(right->production, first));

          if (set__cardinality(work) != 0)
          {
            char *tempstr = strstr(temp->id, "__"); /* rdp__find __ in name */
            unsigned length;

            if (tempstr != NULL)
              length = tempstr - temp->id;  /* length of prefix */
            else
              length = strlen(temp->id);

            scan__error("LL(1) violation in '%.*s':", length, temp->id);
            printf(" Alternate ");
            rdp__print_sub_item(stdout, left->production, 1);
            printf("\n and alternate ");
            rdp__print_sub_item(stdout, right->production, 1);
            printf(" have the following start terminals in common\n ");
            rdp__print_parser_token_set(stdout, work, base);
            printf("\n");
            bad = 1;
          }
          right = right->next;
        }
        left = left->next;
      }
    }
    temp = temp->next_scope;
  }

  /* check that first(a) - follow (a) is empty for nullable a */
  if (rdp__verbose)
    scan__info("Checking nullable productions");

  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, contains_null))
    {
      set__assign_set(work, DATA(temp, first));
      set__intersect_set(work, DATA(temp, follow));
      if (set__cardinality(work) != 0 && DATA(temp, kind) != K_ITERATION)
      {
        char *tempstr = strstr(temp->id, "__"); /* rdp__find __ in name */
        unsigned length;

        if (tempstr != NULL)
          length = tempstr - temp->id;  /* length of prefix */
        else
          length = strlen(temp->id);

        scan__error("LL(1) violation in '%.*s':", length, temp->id);
        if (DATA(temp, kind) != K_PRIMARY)
        {
          printf(" sub-production ");
          rdp__print_sub_item(stdout, temp, 1);
          printf("\n ");
        }
        else
          printf(" production ");
        printf("contains null but first and follow sets both include ");

        rdp__print_parser_token_set(stdout, work, base);
        printf(".\n");
        bad = 1;
      }
    }
    temp = temp->next_scope;
  }
  return bad;
}

/* Output line breaking support */
void rdp__indent(FILE * f)
{
  unsigned temp;

  for (temp = 0; temp < rdp__indentation; temp++)
    fprintf(f, "  ");

  rdp__column = rdp__indentation * 2;
}

void rdp__check_break(FILE * f)
{
  if (rdp__column >= rdp__line_break)
  {
    rdp__column = 0;
    fprintf(f, "\n");
  }
}

/* Parser output routines */
int rdp__is_valid_C_id(char *s)
{
  int temp;

  temp = (isalpha(*s) || *s == '_');
  while (*++s != '\0')
    temp = temp && (isalnum(*s) || *s == '_');
  return (temp);
}

static void rdp__print_parser_production_name(FILE * f, symbol__ * n, char *open_comm, char *close_comm)
{
  switch (DATA(n, kind))
  {
 case K_CODE:
    rdp__column += fprintf(f, "\"%s\"", n->id + 1);
    break;
  case K_TOKEN:
    if (rdp__is_valid_C_id(n->id + 1))
      rdp__column += fprintf(f, "T_%s", n->id + 1);
    else
      rdp__column += fprintf(f, "T_%u %s %s %s", DATA(n, token_value), open_comm, n->id + 1, close_comm);
    break;

  case K_NUMBER_PRIMITIVE:
  case K_STRING_PRIMITIVE:
    rdp__column += fprintf(f, "P_%s", n->id);
    break;
  default:
    rdp__column += fprintf(f, "%s", n->id);
    break;
  }

}

static void rdp__print_parser_string(FILE * f, char *s)
{
  fprintf(f, "\"");
  while (*s != 0)
  {
    if (*s == '\"' || *s == '\\' || *s == '\'')
      fprintf(f, "\\");
    fprintf(f, "%c", *s++);
  }
  fprintf(f, "\"");

}

static void rdp__print_header(char *headerfilename, symbol__ * base)
{
  FILE *headerfile;
  symbol__ *temp;
  int count;

  if (rdp__verbose)
    scan__info("Dumping C parser header file to '%s'", headerfilename);

  if ((headerfile = fopen(headerfilename, "w")) == NULL)
    crash__("Can't open header output file");

  fprintf(headerfile,
          "/****************************************************************************\n"
          "*\n"
          "* Parser header file generated by RDP on " __DATE__ " at " __TIME__ "\n"
          "*\n"
          "****************************************************************************/\n"
          "#include \"scan.h\"\n");

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
  fprintf(headerfile, " %s(void);\n", rdp__start_prod->id);

  fclose(headerfile);
}


static void rdp__print_parser_alternate(FILE * outputfile, symbol__ * production, symbol__ * primary);

static void rdp__print_parser_item(FILE * outputfile, symbol__ * prod, symbol__ * primary, char *return_name)
{
  rdp__indent(outputfile);
  switch (DATA(prod, kind))
  {
  case K_NUMBER_PRIMITIVE:
  case K_STRING_PRIMITIVE:
  case K_TOKEN:
    fprintf(outputfile, "scan__test(");
    if (return_name != NULL)
      fprintf(outputfile, "(%s = %s", return_name,
              DATA(prod, extended_class) != E_SIMPLE ||
              DATA(prod, kind) == K_STRING_PRIMITIVE ? "scan__sym->id," :
              DATA(prod, kind) == K_NUMBER_PRIMITIVE ? "scan__sym->number," : "");

    rdp__print_parser_production_name(outputfile, prod, "/*", "*/");
    if (return_name != NULL)
      fprintf(outputfile, ")");
    fprintf(outputfile, ", %s_stop, \"Expecting \"", primary->id);
    rdp__print_parser_string(outputfile, DATA(prod, kind) == K_TOKEN ? prod->id + 1 : prod->id);
    fprintf(outputfile, ");\n");
    rdp__indent(outputfile);
    fprintf(outputfile, "scan__();\n");
    break;
  case K_CODE:
    fprintf(outputfile, "%s\n", prod->id + 1);
    break;
  case K_PRIMARY:
    if (return_name != NULL)
      fprintf(outputfile, "%s = ", return_name);
    fprintf(outputfile, "%s();\n", prod->id);
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
  struct rdp__list_node *list = DATA(production, list);

  while (list != NULL)
  {
    rdp__print_parser_item(outputfile, list->production, primary, list->return_name);
    list = list->next;
  }
}

static void rdp__print_parser_alternate(FILE * outputfile, symbol__ * production, symbol__ * primary)
{
  struct rdp__list_node *list = DATA(production, list);

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
  struct rdp__list_node *list = DATA(base, list);

  while (list != NULL)
  {
    if (DATA(list->production, kind) != K_PRIMARY)
      rdp__print_collect_declarations(list->production, scope);
    if (list->return_name != NULL)
      /* insert symbol and point at parent data block */
      if (symbol__lookup_id_scope(list->return_name, scope) == NULL)
        symbol__insert_id(list->return_name)->data = list->production->data;
    list = list->next;
  }
}

static void rdp__print_parser_primaries(FILE * outputfile, symbol__ * base)
{
  symbol__ *temp = base->next_scope;

  fprintf(outputfile, "\n/* Parser functions */\n");
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_PRIMARY)
    {
      int count,
       is_void = (strcmp(DATA(temp, return_type), "void") == 0);

      symbol__ *locals = symbol__new_scope(temp->id);

      fprintf(outputfile, "%s", DATA(temp, return_type));

      for (count = 0; count < DATA(temp, return_type_stars); count++)
        fprintf(outputfile, "*");
      fprintf(outputfile, " %s(void)\n{\n", temp->id);

      /* scan all subproductions and add return variables to symbol__ table */
      if (!is_void)
        symbol__insert_id("result")->data = temp->data; /* loop back on ourselves */

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
        fprintf(outputfile, "  if (scan__test_set(%s_first, %s_stop, \"Expecting %s\"))\n"
                "  {\n", temp->id, temp->id, temp->id);

      rdp__indentation = 2;

      rdp__print_parser_alternate(outputfile, temp, temp);
      /* add error handling on exit */
      fprintf(outputfile, "    scan__test_set(%s_stop, %s_stop, \"Expecting %s follow symbol\");\n"
              "  }\n%s}\n\n",
              temp->id, temp->id, temp->id, is_void ? "" : "  return result;\n");
    }
    temp = temp->next_scope;
  }
}

static void rdp__print_parser_include_line(FILE * outputfile, struct rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_include_line(outputfile, p->next);
  fprintf(outputfile, "#include \"%s\"\n", p->str1);
}

static void rdp__print_parser_help_line(FILE * outputfile, struct rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_help_line(outputfile, p->next);
  fprintf(outputfile, "                  \"%s%s\\n\"\n", isspace(*(p->str1)) ? "" : "-", p->str1);
}

static void rdp__print_parser_switch_line(FILE * outputfile, struct rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_switch_line(outputfile, p->next);
  if (!isspace(*(p->str1)))
    fprintf(outputfile, "         case '%c':\n           %s\n           break;\n", *(p->str1), p->str2);
}

static void rdp__print_parser(char *outputfilename, symbol__ * base)
{
  FILE *outputfile;
  symbol__ *temp;

  if (rdp__verbose)
    scan__info("Dumping C parser main file to '%s'", outputfilename);

  if ((outputfile = fopen(outputfilename, "w")) == NULL)
    crash__("Can't open parser output file");

  /* print main file header */
  fprintf(outputfile,
          "/****************************************************************************\n"
          "*\n"
          "* Parser generated by RDP on " __DATE__ " at " __TIME__ "\n"
          "*\n"
          "****************************************************************************/\n"
          "#include <stdio.h>\n"
          "#include \"crash.h\"\n"
          "#include \"symbol.h\"\n"
          "#include \"set.h\"\n"
          "#include \"scan.h\"\n"
          "#include \"%s\"\n", scan__add_filetype(outputfilename, "h"));

  if (rdp__dir_include != NULL)
    rdp__print_parser_include_line(outputfile, rdp__dir_include);

  fprintf(outputfile,
          "\nchar\n *rdp__sourcefilename = NULL,   /* source file name */\n"
          " *rdp__outputfilename = \"%s\",		  /* output file name */\n", rdp__dir_output_file);
  fprintf(outputfile,
          "  rdp__verbose = 0,                   /* verbosity flag */\n"
          "  rdp__pass;											/* pass number */\n\n"
          "symbol__\n"
          " *rdp__base;													/* pointer to application scope */\n\n"
          "static void rdp__help(char *msg)\n"
          "{\n"
          "	crash__(\"%%s\\n\\n %s \" __DATE__ \" \" __TIME__ \"\\n\\n\"\n", rdp__dir_title);

  fprintf(outputfile,
          "                 \"Usage: %.*s [options] sourcefile \\n\\n\"\n"
          "                 \"Options:\\n\\n\"\n"
          "		              \"-l  Make a listing\\n\"\n"
          "		              \"-ofilename Write output to filename\\n\"\n"
          "		              \"-s  Echo each scanner symbol as it is read\\n\"\n"
          "		              \"-S  Print summary symbol table statistics\\n\"\n"
          "		              \"-tn Tab expansion width (default %u)\\n\"\n"
          "		              \"-Tn Text buffer size in bytes for scanner (default %u)\\n\"\n"
          "		              \"-v  Set rdp__verbose mode\\n\"\n",
          (int) strcspn(outputfilename, "."), outputfilename, rdp__dir_tab_width, rdp__dir_text_size);

  if (rdp__dir_help != NULL)
    rdp__print_parser_help_line(outputfile, rdp__dir_help);

  fprintf(outputfile,
          "	     	  ,msg == NULL ? \"\" : msg);\n"
          "}\n");

  /* print load keyword function */
  rdp__column = 0;
  fprintf(outputfile, "\n/* Load keywords */\nstatic void load_keywords(void)\n{\n");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_TOKEN)
    {
      fprintf(outputfile, "  scan__load_%skeyword(",
              DATA(temp, extended_class) == E_SIMPLE ? "" : "extended_");
      rdp__print_parser_string(outputfile, temp->id + 1);
      fprintf(outputfile, ", ");
      rdp__print_parser_production_name(outputfile, temp, "/*", "*/");
      if (DATA(temp, extended_class) != E_SIMPLE)
      {
        fprintf(outputfile, ", %i, ", DATA(temp, extended_class));
        rdp__print_parser_string(outputfile, DATA(temp, close) + 1);
      }
      fprintf(outputfile, ");\n");
    }
    temp = temp->next_scope;
  }
  fprintf(outputfile, "}\n");

  /* print set declaration */
  fprintf(outputfile, "\n/* Set declarations */\n\nunsigned char");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (set__inc_element(rdp__production_set, DATA(temp, kind)))
    {
      int count;

      fprintf(outputfile, "\n  %s_first[%u] = {", temp->id, rdp__set_width);
      for (count = 0; count < rdp__set_width; count++)
        fprintf(outputfile, "%c%u", count == 0 ? ' ' : ',', DATA(temp, first)[count]);
      fprintf(outputfile, " }, ");

      if (DATA(temp, kind) == K_PRIMARY)
      {
        fprintf(outputfile, "\n  %s_stop[%u] = {", temp->id, rdp__set_width);
        for (count = 0; count < rdp__set_width; count++)
          fprintf(outputfile, "%c%u", count == 0 ? ' ' : ',', DATA(temp, follow)[count]);
        fprintf(outputfile, " }, ");
      }
    }
    temp = temp->next_scope;
  }
  fprintf(outputfile, "\nerror_sync;\n");

  /* print forward declarations */
  fprintf(outputfile, "\n/* Parser forward declarations */\n");
  temp = base->next_scope;
  while (temp != NULL)
  {
    if (DATA(temp, kind) == K_PRIMARY)
    {
      int count;

      if (temp != rdp__start_prod)
        fprintf(outputfile, "static ");
      fprintf(outputfile, "%s", DATA(temp, return_type));
      for (count = 0; count < DATA(temp, return_type_stars); count++)
        fprintf(outputfile, "*");
      fprintf(outputfile, " %s(void);\n", temp->id);
    }
    temp = temp->next_scope;
  }

  /* print parser definitions */
  rdp__print_parser_primaries(outputfile, base);

  /* print main line routine */
  fprintf(outputfile,
          "int main(int argc, char *argv[])\n"
          "{\n"
          "  unsigned\n"
          "    rdp__tabwidth = 4,			      /* tab expansion width */\n"
          "    rdp__textsize = 8192,			    /* size of scanner text array */\n"
          "    rdp__symbol_statistics = 0,	/* show symbol__ table statistics flag */\n\n"
          "    rdp__line_echo = 0;	/* make listing flag */\n\n"
          "  while (--argc > 0)\n"
          "  {\n"
          "	   if ((*++argv)[0] == '-') /* switch */\n"
          "	   {\n"
          "		   switch ((*argv)[1])\n"
          "		   {\n"
          "		     case 'o':\n"
          "			     if (*(*argv + 2) != 0)\n"
          "			     rdp__outputfilename = *argv + 2;\n"
          "			     break;\n"
          "	 	     case 'S':\n"
          "		 	     rdp__symbol_statistics = 1;\n"
          "		 	     break;\n"
          "		     case 's':\n"
          "			     scan__symbol_echo(1);\n"
          "			     break;\n"
          "		     case 'l':\n"
          "			     rdp__line_echo = 1;\n"
          "			     break;\n"
          "		     case 't':\n"
          "			     sscanf(*argv + 2, \"%%u\", &rdp__tabwidth);\n"
					"			     scan__tab_width(rdp__tabwidth);\n"
          "			     break;\n"
          "	       case 'T':\n"
          "			     sscanf(*argv + 2, \"%%u\", &rdp__textsize);\n"
          "		       break;\n"
          "        case 'v':\n"
          "			     rdp__verbose = 1;\n"
          "			     break;\n");

  if (rdp__dir_help != NULL)
    rdp__print_parser_switch_line(outputfile, rdp__dir_help);

  fprintf(outputfile,
          "		     default:\n"
          "			     printf(\"\\nUnrecognised option -%%c\", (*argv)[1]);\n"
          "			     rdp__help(\"\");\n"
          "		   }\n"
          "	   }\n"
          "	 else\n"
          "		 rdp__sourcefilename = *argv;\n"
          "  }\n");
  fprintf(outputfile,
          "  if (rdp__sourcefilename == NULL)\n"
          "	   rdp__help(\"No source file specified\");\n");

  fprintf(outputfile,
          "  set__init(%u);\n"
          "  symbol__init(%u, %u);\n", rdp__dir_set_size, rdp__dir_hash_size, rdp__dir_hash_prime);

  fprintf(outputfile,
          "  load_keywords();\n"
					"  rdp__base = symbol__new_scope(\"parser\");	/* create application scope */\n"
					"  scan__hex_dollar(%u);\n"
					"  scan__tab_width(%u);\n"
					"  scan__newline_visible(%u);\n"
					"  scan__case_insensitive(%u);\n"
					"  scan__show_skips(%u);\n"
					"  scan__summarise_errors(rdp__verbose);\n"
					"  scan__init(%u,%u,%u);\n",
					rdp__dir_hex_dollar, rdp__dir_tab_width, rdp__dir_newline_visible,
					rdp__dir_case_insensitive, rdp__dir_show_skips, rdp__dir_text_size,
					rdp__dir_max_errors, rdp__dir_max_warnings);

	fprintf(outputfile,
					"  if (rdp__verbose)\n"
					"	   printf(\"\\n%s \" __DATE__ \" \" __TIME__ \"\\n\");\n", rdp__dir_title);

	if (rdp__dir_pre_process != NULL)
		fprintf(outputfile, "  %s\n", rdp__dir_pre_process);

	fprintf(outputfile, "  for (rdp__pass = 1; rdp__pass <= %u; rdp__pass++)\n"
					"  {\n", rdp__dir_passes);

	if (rdp__dir_text_mode)
		fprintf(outputfile,
						"  if (scan__total_errors() != 0)\n"
						"    crash__(\"errors detected in source file\");   /* crash quietly */ \n"
						"  if (scan__open_text(\"Text mode\",\n"
						"                      scan__load_file(scan__default_filetype(rdp__sourcefilename, \"%s\"))\n"
						"                     ) == NULL)\n"
						"    rdp__help(\"Can't open source file\");\n"
						"    if (rdp__pass == %u)\n"
						"      scan__line_echo(rdp__line_echo);\n\n",
						rdp__dir_suffix, rdp__dir_passes);
	else
		fprintf(outputfile,
						"    if (scan__total_errors() != 0)\n"
						"      crash__(\"errors detected in source file\");   /* crash quietly */ \n"
						"    if (scan__open_file(scan__default_filetype(rdp__sourcefilename, \"%s\")) == NULL)\n"
						"      rdp__help(\"Can't open source file\");\n"
						"    if (rdp__pass == %u)\n"
						"      scan__line_echo(rdp__line_echo);\n\n",
						rdp__dir_suffix, rdp__dir_passes);

	fprintf(outputfile,
          "    %s();						/* call parser at top level */\n"
          "  }\n"
          "  if (rdp__symbol_statistics)\n"
          "    symbol__print_statistics();\n", rdp__start_prod->id);

  fprintf(outputfile, "  return %s\n}\n", rdp__dir_post_process == NULL ? "0;" : rdp__dir_post_process);

  fclose(outputfile);
}

void rdp__pre_process(void)
{
  rdp__production_set = set__construct(K_PRIMARY, K_SEQUENCE, K_DO_FIRST, K_LIST, K_CONDITIONAL, K_ITERATION, SET_END);
  rdp__empty_production_set = set__construct(K_LIST, K_CONDITIONAL, K_ITERATION, SET_END);

  DATA(rdp__find("ID", K_STRING_PRIMITIVE, 0), token_value) = P_ID; /* add predefined primitive productions */
  DATA(rdp__find("NUMBER", K_NUMBER_PRIMITIVE, 0), token_value) = P_NUMBER;
  DATA(rdp__find("NEW_ID", K_STRING_PRIMITIVE, 0), token_value) = P_NEW_ID;
  DATA(rdp__find("EOLN", K_NUMBER_PRIMITIVE, 0), token_value) = P_EOLN;
  DATA(rdp__find("EOF", K_NUMBER_PRIMITIVE, 0), token_value) = P_EOF;
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
    if (DATA(temp, kind) == K_TOKEN || DATA(temp, kind) == K_NUMBER_PRIMITIVE ||
        DATA(temp, kind) == K_STRING_PRIMITIVE)
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

  if (rdp__verbose)
    scan__info("Checking for continuation tokens");

  while (temp != NULL)          /* scan over all productions */
  {
    if (DATA(temp, kind) == K_TOKEN)
    {
      char *lo = last_token,
      *hi = temp->id;

      if (*lo != 0 && !rdp__is_valid_C_id(hi + 1))
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

          rdp__find(continuation_name, K_TOKEN, 0);

          hi++;
        }
      }

      last_token = temp->id;
    }
    temp = temp->next_scope;
  }
}

int rdp__post_process(char *outputfilename, char force, symbol__ * base)
{
  if (scan__total_errors() != 0)
    crash__("EBNF file contains syntax errors");
  if (rdp__verbose)
    scan__info("");             /* skip a line */
  symbol__sort_scope(base);     /* sort productions into alphabetical order */
  rdp__add_continuations(base); /* scan through tokens and add any necessry continuations */
  symbol__sort_scope(base);     /* re-sort productions into alphabetical order */
  rdp__order_tokens(base);      /* apply token numbers to token productions */
  rdp__bad_grammar(base);       /* rdp__find the non-LL(1)-isms */

  if (rdp__verbose)             /* see how much text buffer space we used */
    scan__print_statistics();

  if (rdp__expanded)            /* print out expanded BNF */
  {
    symbol__ *temp = base->next_scope;

    if (rdp__verbose)
      printf(" Expanded EBNF listing");
    while (temp != NULL)
    {
      struct rdp__list_node *list = DATA(temp, list);
      int k = DATA(temp, kind),
       count;

      if (k != K_CODE && k != K_STRING_PRIMITIVE && k != K_NUMBER_PRIMITIVE)
      {
        printf(" ");
        rdp__print_parser_production_name(stdout, temp, "(*", "*)");
        printf(":%s", DATA(temp, return_type));
        for (count = 0; count < DATA(temp, return_type_stars); count++)
          printf("*");
        printf(" ::= ");

        if (k == K_TOKEN)
          printf("'");
        printf("%s",
               k == K_TOKEN ? temp->id + 1 :
               k == K_NUMBER_PRIMITIVE || k == K_STRING_PRIMITIVE ? "(* Scanner primitive *) " :
               k == K_DO_FIRST ? "( " :
               k == K_LIST ? "< " :
               k == K_CONDITIONAL ? "[ " :
               k == K_ITERATION ? "{ " : "");

        while (list != NULL)
        {
          rdp__print_parser_production_name(stdout, list->production, "(*", "*)");
          if (list->return_name != NULL)
            printf(":%s", list->return_name);
          printf(" ");
          list = list->next;
          if (k != K_SEQUENCE && list != NULL)
            printf("| ");
        }

        printf("%s",
               k == K_TOKEN ? "'" :
               k == K_DO_FIRST ? ") " :
               k == K_LIST ? "> " :
               k == K_CONDITIONAL ? "] " :
               k == K_ITERATION ? "} " : "");

        if (k == K_LIST)
          printf("'%s'", DATA(temp, supplementary_token)->id + 1);

        printf(".\n");

        printf(" First set is {%s", DATA(temp, contains_null) ? "(NULL) " : "");
        rdp__print_parser_token_set(stdout, DATA(temp, first), base);
        printf("}\n");

        printf(" Stop set is {");
        rdp__print_parser_token_set(stdout, DATA(temp, follow), base);
        printf("}\n\n");
      }

      temp = temp->next_scope;
    }
  }
  if (scan__print_total_errors(rdp__verbose))
  {
    if (force)
      scan__warning("Grammar is not LL(1) but -F switch set: writing output files");
    else
      crash__("Run aborted without creating output files: rerun with -F to override");
  }

  rdp__print_header(scan__add_filetype(outputfilename, "h"), base); /* dump header file */
  rdp__print_parser(scan__add_filetype(outputfilename, "c"), base); /* dump main file */

  return 0;
}

/* End of rdp_aux.c */
