/*******************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* rdp_prnt.c - rdp output routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <string.h>
#include <ctype.h>
#include <time.h>
#include "memalloc.h"
#include "scan.h"
#include "set.h"
#include "symbol.h"
#include "textio.h"
#include "rdp_aux.h"
#include "rdp_gram.h"
#include "rdp_prnt.h"
#include "rdp.h"

extern void *tokens;
extern char* rdp__sourcefilename; /* source file name loaded by main line */

char* rdp__token_string = NULL;
char* rdp__enum_string = NULL;

static unsigned rdp__indentation = 0;          /* current identation level */

static void rdp__indent(void)
{
  unsigned temp;

  for (temp = 0; temp < rdp__indentation; temp++)
    text__printf("  ");
}

/* print a token test: if follow is null then print a test that does not do
   error recovery. If card(first) is 1 or 0, print a scan__test only */

static void rdp__print_parser_test(char* statement, char *first_name, set__ *first, char* follow_name)
{
  text__printf("%s (scan__test", statement);

  if (set__cardinality(first) > 1)
    text__printf("_set(&%s_first", first_name);
  else
  {
    text__printf("(");
    set__print_set(first, rdp__enum_string);
  }

  if (follow_name == NULL)
    text__printf(", NULL))\n");
  else
    text__printf(", &%s_stop))\n", follow_name);
}

static void rdp__print_parser_string(char *s)
{
  while (*s != 0)
  {
    if (*s == '\"' || *s == '\\' || *s == '\'')
      text__printf("\\");
    text__printf("%c", *s++);
  }
}

void rdp__make_token_string(void * base)
{
  rdp__data *p = (rdp__data*) symbol__next_symbol_in_scope(base);

  rdp__token_string = text__insert_string("IGNORE");
  text__insert_string("ID");
  text__insert_string("INTEGER");
  text__insert_string("REAL");
  text__insert_string("ISTRING");
  text__insert_string("ISTRING_ESC");
  text__insert_string("ICOMMENT");
  text__insert_string("ICOMMENT_VISIBLE");
  text__insert_string("ICOMMENT_NEST");
  text__insert_string("ICOMMENT_NEST_VISIBLE");
  text__insert_string("ICOMMENT_LINE");
  text__insert_string("ICOMMENT_LINE_VISIBLE");
  text__insert_string("KEYWORD");
  text__insert_string("EOF");
  text__insert_string("EOLN");

  while (p != NULL)
  {
    if (p->kind == K__TOKEN)
    {
      char *c = p->id;
      p->token_string = text__insert_char('\''); /* insert open quote */
      while (*c != 0)	/* iterate to end of string */
      {
	if (*c == '\"' || *c == '\\' || *c == '\'')
	  text__insert_char('\\');
	text__insert_char(*c++);
      }
      text__insert_string("\'");  /* insert close quote */
    }
    p = (rdp__data*) symbol__next_symbol_in_scope(p);
  }

  p = (rdp__data*) symbol__next_symbol_in_scope(base);

  rdp__enum_string = text__insert_string("T__IGNORE");
  text__insert_string("T__ID");
  text__insert_string("T__INTEGER");
  text__insert_string("T__REAL");
  text__insert_string("T__ISTRING");
  text__insert_string("T__ISTRING_ESC");
  text__insert_string("T__ICOMMENT");
  text__insert_string("T__ICOMMENT_VISIBLE");
  text__insert_string("T__ICOMMENT_NEST");
  text__insert_string("T__ICOMMENT_NEST_VISIBLE");
  text__insert_string("T__ICOMMENT_LINE");
  text__insert_string("T__ICOMMENT_LINE_VISIBLE");
  text__insert_string("T__KEYWORD");
  text__insert_string("T__EOF");
  text__insert_string("T__EOLN");

  while (p != NULL)
  {
    if (p->kind == K__TOKEN)
    {
      p->token_enum = text__insert_characters("T__");
      if (rdp__is_valid_C_id(p->id))
	text__insert_string(p->id);
      else
      {
	char * c = p->id;

	text__insert_integer(p->token_value);
	text__insert_characters(" /* ");
	while (*c != 0)
	  text__insert_char(*c++);
	text__insert_string(" */");
      }
    }
    else
    if (p->kind == K__EXTENDED) /* set up token_value, but don't insert in string! */
    {
      switch(p->token_value)
      {
       case T__ISTRING:
	 p->token_enum = "T__ISTRING"; break;
       case T__ISTRING_ESC:
	 p->token_enum = "T__ISTRING_ESC"; break;
       case T__ICOMMENT_VISIBLE:
	 p->token_enum = "T__ICOMMENT_VISIBLE"; break;
       case T__ICOMMENT_NEST_VISIBLE:
	 p->token_enum = "T__ICOMMENT_NEST_VISIBLE"; break;
       case T__ICOMMENT_LINE_VISIBLE:
	 p->token_enum = "T__ICOMMENT_LINE_VISIBLE"; break;
       case T__ICOMMENT:
	 p->token_enum = "T__ICOMMENT"; break;
       case T__ICOMMENT_NEST:
	 p->token_enum = "T__ICOMMENT_NEST"; break;
       case T__ICOMMENT_LINE:
	 p->token_enum = "T__ICOMMENT_LINE"; break;
      }

    }
    p = (rdp__data*) symbol__next_symbol_in_scope(p);
  }
}

static void print_time(void)
{
 char line[80];
 time_t timer = time(NULL);

 strftime(line, 80, "%b %d %Y at %H:%M:%S" ,localtime(&timer));
 text__printf("%s", line);
}

static void rdp__print_parser_param_list_sub(rdp__param_list *param, int last, int definition)
{
  if (param != NULL)
  {
    rdp__print_parser_param_list_sub(param->next, 0, definition);
    text__printf("%s%s%s%s", definition ? param->type : "", definition ? " ": "", param->id, last ? "" : ", ");
  }
}

static void rdp__print_parser_param_list(rdp__param_list *params, int definition)
{
  text__printf("(");

  if (params == NULL && definition)
    text__printf("void");
  else
    rdp__print_parser_param_list_sub(params, 1, definition);

  text__printf(")");
}

static void rdp__print_parser_production_name(rdp__data * n)
{
  switch (n->kind)
  {
  case K__CODE:
    text__printf("[*%s*]", n->id);
    break;
  case K__EXTENDED:
  case K__TOKEN:
    text__printf("%s", n->token_enum);
    break;
  case K__INTEGER:
  case K__REAL:
  case K__STRING:
    text__printf("T__%s", n->id);
    break;
  default:
    text__printf("%s", n->id);
    break;
  }
}

/* dignostic print routines */
static void rdp__print_sub_sequence(rdp__data * production, int expand);
static void rdp__print_sub_alternate(rdp__data * production, int expand);

void rdp__print_sub_item(rdp__data * prod, int expand)
{
  switch (prod->kind)
  {
 case K__INTEGER:
 case K__STRING:
 case K__REAL:
  case K__EXTENDED:
    text__printf("%s ", prod->id);
    break;
  case K__TOKEN:
    text__printf("'%s' ", prod->id);
    break;
  case K__CODE:
    /* Don't print anything */
    break;
  case K__PRIMARY:
    text__printf("%s ", prod->id);
    break;
  case K__SEQUENCE:
    rdp__print_sub_sequence(prod, expand);
    break;
  case K__DO_FIRST:
    if (expand)
    {
      text__printf("( ");
      rdp__print_sub_alternate(prod, expand);
      text__printf(") ");
    }
    else
      text__printf("%s ", prod->id);
    break;
  case K__LIST:
    if (expand)
    {
      text__printf("< ");
      rdp__print_sub_alternate(prod, expand);
      text__printf("> ");
    }
    else
      text__printf("%s ", prod->id);
    break;
  case K__CONDITIONAL:
    if (expand)
    {
      text__printf("[ ");
      rdp__print_sub_alternate(prod, expand);
      text__printf("] ");
    }
    else
      text__printf("%s ", prod->id);
    break;
  case K__ITERATION:
    if (expand)
    {
      text__printf("{ ");
      rdp__print_sub_alternate(prod, expand);
      text__printf("} ");
    }
    else
      text__printf("%s ", prod->id);
    break;
  }
}

static void rdp__print_sub_sequence(rdp__data * production, int expand)
{
  rdp__list *list = production->list;

  while (list != NULL)
  {
    rdp__print_sub_item(list->production, expand);
    list = list->next;
  }
}

static void rdp__print_sub_alternate(rdp__data * production, int expand)
{
  rdp__list *list = production->list;

  while (list != NULL)
  {
    rdp__print_sub_item(list->production, expand);

    if ((list = list->next) != NULL)
      text__printf("| ");
  }
}

void rdp__print_header(char *headerfilename)
{
  FILE * headerfile;

  rdp__table_list *temp_table = rdp__dir_symbol_table;
  rdp__data* temp = (rdp__data*) symbol__next_symbol_in_scope(symbol__get_scope(tokens));
  int count = 0, first = 1;

  if (rdp__verbose)
    text__message(TEXT__INFO, "dumping header file to '%s'\n", headerfilename);

  if (*headerfilename == '-')
    headerfile = stdout;
  else if ((headerfile = fopen(headerfilename, "w")) == NULL)
    text__message(TEXT__FATAL, "can't open header output file '%s' for writing\n", headerfilename);

  text__redirect(headerfile);

  text__printf(
	  "/****************************************************************************\n"
	  "*\n"
	  "* Header file generated by RDP on ");

  print_time();

  text__printf(
	  " from %s\n"
	  "*\n"
	  "****************************************************************************/\n"
	  "#include \"scan.h\"\n",
	  text__force_filetype(rdp__sourcefilename, "bnf"));

  /* print token enumeration */
  text__printf("\n/* Token enumeration */\nenum\n{\n");
  while (temp != NULL)
  {
    if (temp->kind == K__TOKEN)
    {
      if (!first)
	text__printf(", ");
      if (count++ % 8 == 0)
        text__printf("\n");
      rdp__print_parser_production_name(temp);
      if (first)
      {
	text__printf(" = T__TOP");
	first = 0;
      }
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
  text__printf(",\nTT__TOP\n};\n\n");

  /* print casting macros for symbol tables */
  text__printf("/* Symbol table support */\n");
  while (temp_table != NULL)
  {
    text__printf("typedef struct %s__data_node\n{\n%s\n} %s__data;\n",
		  temp_table->name, temp_table->data_fields, temp_table->name);
    text__printf("extern void * %s;\n", temp_table->name);
    text__printf("#define %s__cast(x) ((%s__data *)x)\n\n", temp_table->name, temp_table->name);
    temp_table = temp_table->next;
  }

  /* print start production prototype */
  text__printf("/* Parser start production */\n");
  text__printf("%s", rdp__start_prod->return_type);
  for (count = 0; count < rdp__start_prod->return_type_stars; count++)
    text__printf("*");
  text__printf(" %s", rdp__start_prod->id);

  rdp__print_parser_param_list(rdp__start_prod->params, 1);

  text__printf(";\n\n/* End of %s */\n",
	  text__force_filetype(headerfilename, "h"));

  text__redirect(stdout);
}

static void rdp__print_parser_alternate(rdp__data * production, rdp__data * primary);
static void rdp__print_parser_sequence(rdp__data * production, rdp__data * primary);

static void rdp__print_parser_item(rdp__data * prod, rdp__data * primary, char *return_name, rdp__param_list* actuals)
{
  if (!(prod->kind == K__CODE && prod->code_successor))
    rdp__indent(); /* Don't indent code sequence-internal or inline items */

  switch (prod->kind)
  {
  case K__INTEGER:
  case K__REAL:
  case K__STRING:
  case K__EXTENDED:
  case K__TOKEN:
    if (return_name != NULL && !rdp__parser_only) /* disable if -p option used */
    {
      text__printf("%s = SCAN__CAST->%s;\n", return_name,
	      prod->kind == K__REAL ? "data.r" :
	      prod->kind == K__INTEGER ? "data.i" : "id");
      rdp__indent();
    }
    text__printf("scan__test(");
    rdp__print_parser_production_name(prod);
    text__printf(", &%s_stop);\n" , primary->id);
    rdp__indent();
    text__printf("scan__();\n");
    break;
  case K__CODE:
    if (!rdp__parser_only)      /* disabled by -p option */
    {
      char *temp = prod->id;

      if (prod->code_pass != 0)
	text__printf("if (rdp__pass == %u) { \\\n", prod->code_pass);

      while (*temp != '\0')
      {
	if (*temp == '\n') text__printf(" \\\n");
	else text__printf("%c", *temp);
	temp++;
      }

      if (prod->code_pass != 0)
	text__printf(" \\\n}");

      if (prod->kind == K__CODE && prod->code_terminator)
	text__printf("\n"); /* terminate semantic actions tidily */
    }
    break;
  case K__PRIMARY:
    if (return_name != NULL && !rdp__parser_only) /* disable if -p option set! */
      text__printf("%s = ", return_name);
    text__printf("%s", prod->id);
    if (!(prod->code_only && actuals == NULL))
      rdp__print_parser_param_list(actuals, 0);
    text__printf(";\n");
    break;
  case K__SEQUENCE:
    text__message(TEXT__FATAL, "internal error - unexpected alternate in sequence\n");
    break;
  case K__DO_FIRST:
    text__printf("{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(prod, primary);
    rdp__indentation--;
    rdp__indent();
    text__printf("}\n");
    break;
  case K__LIST:
    text__printf("{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(prod, primary);
    rdp__indentation--;
    rdp__indent();
    text__printf("}\n");
    rdp__indent();
    text__printf("while (SCAN__CAST->token == ");
    rdp__print_parser_production_name(prod->supplementary_token);
    text__printf(")\n");
    rdp__indent();
    text__printf("{\n");
    rdp__indentation++;
    rdp__indent();
    text__printf("scan__(); /* skip delimiter */\n");
    rdp__print_parser_alternate(prod, primary);
    rdp__indentation--;
    rdp__indent();
    text__printf("}\n");
    break;
  case K__CONDITIONAL:
    rdp__print_parser_test("if", prod->id, &prod->first, NULL);
    rdp__indent();
    text__printf("{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(prod, primary);
    rdp__indentation--;
    rdp__indent();
    text__printf("}\n");
    break;
  case K__ITERATION:
    rdp__print_parser_test("while", prod->id, &prod->first, NULL);
    rdp__indent();
    text__printf("{\n");
    rdp__indentation++;
    rdp__print_parser_alternate(prod, primary);
    rdp__indentation--;
    rdp__indent();
    text__printf("}\n");
    break;
  }
}

static void rdp__print_parser_sequence(rdp__data * production, rdp__data * primary)
{
  rdp__list *list = production->list;

  while (list != NULL)
  {
    rdp__print_parser_item(list->production, primary, list->return_name, list->actuals);
    list = list->next;
  }
}

static void rdp__print_parser_alternate(rdp__data * production, rdp__data * primary)
{
  rdp__list *list = production->list;

  if (list->next == NULL)       /* special case: only one alternate */
    rdp__print_parser_sequence(list->production, primary);
  else
  {
    while (list != NULL)
    {
      if (list->production->kind != K__SEQUENCE)
	text__message(TEXT__FATAL, "internal error - expecting alternate production\n");

      rdp__indent();
      rdp__print_parser_test("if", list->production->id, &list->production->first, NULL);

      rdp__indent();
      text__printf("{\n");
      rdp__indentation++;

      rdp__print_parser_sequence(list->production, primary);

      rdp__indentation--;
      rdp__indent();

      text__printf("}\n");

      if ((list = list->next) != NULL)
      {
	rdp__indent();
	text__printf("else\n");
      }
    }
  }
}

static void rdp__print_locals(void * base)
{
  rdp__list *list;
  int temp_int;

  if (!set__includes_element(&rdp__production_set, rdp__cast(base)->kind))
    return;

  list = rdp__cast(base)->list;

  while (list != NULL)
  {
    if (list->production->kind != K__PRIMARY)
      rdp__print_locals(list->production);

    if (list->return_name != NULL)
      if (symbol__lookup_key(locals, list->return_name) == NULL)
      {
	locals__data* local = (locals__data*) symbol__new_symbol(sizeof(locals__data));

	local->id = list->return_name;
	symbol__insert_symbol(locals, local);

	text__printf("  %s", list->production->return_type);
	for (temp_int = 0; temp_int<list->production->return_type_stars; temp_int++)
	  text__printf("*");
	text__printf(" %s;\n", list->return_name);
      }

    list = list->next;
  }
}

static void rdp__print_parser_primaries(void * base)
{
  rdp__data* temp = (rdp__data*) symbol__next_symbol_in_scope(base);
  int temp_int;

  text__printf("\n/* Parser functions */\n");
  while (temp != NULL)
  {
    if (temp->kind == K__PRIMARY && (temp->call_count > 0) && !temp->code_only)
    {
      int count,
       is_void = (strcmp(temp->return_type, "void") == 0);

      void *local_scope = symbol__new_scope(locals, temp->id);

      if (temp != rdp__start_prod)
	text__printf("static ");

      text__printf("%s", temp->return_type);

      for (count = 0; count < temp->return_type_stars; count++)
	text__printf("*");
      text__printf(" %s", temp->id);

      rdp__print_parser_param_list(temp->params, 1);

      text__printf("\n{\n");

      /* scan all subproductions and add return variables to symbol__ table */
      if (!is_void)
      {
	text__printf("  %s", temp->return_type);
	for (temp_int = 0; temp_int<temp->return_type_stars; temp_int++)
	  text__printf("*");
	text__printf(" result;\n");
      }

      rdp__print_locals(temp);

      symbol__unlink_scope(local_scope);

      rdp__print_parser_test("  if", temp->id, &temp->first, temp->contains_null ? NULL : temp->id);
      text__printf("  {\n");
      rdp__indentation = 2;

      rdp__print_parser_alternate(temp, temp);
      /* add error handling on exit */
      text__printf("    scan__test_set(&%s_stop, &%s_stop);\n  }\n%s}\n\n", temp->id, temp->id, is_void ? "" : "  return result;\n");
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
}

static void rdp__print_parser_include_line(rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_include_line(p->next);
  text__printf("#include \"%s\"\n", p->str1);
}

static void rdp__print_parser_help_line(rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_help_line(p->next);
  text__printf("                  \"%s%s\\n\"\n", p->str2 == NULL ? "" : "-", p->str1);
}

static void rdp__print_parser_switch_line(rdp__string_list * p)
{
  if (p->next != NULL)
    rdp__print_parser_switch_line(p->next);
  if (p->str2 != NULL)
    text__printf("         case '%c':\n           %s\n           break;\n", *(p->str1), p->str2);
}

void rdp__print_parser(char *outputfilename, void * base)
{
  rdp__data *temp;
  rdp__table_list * temp_table;
  FILE * parserfile;
  int token_count;
  char * str;

  if (rdp__verbose)
    text__message(TEXT__INFO, "dumping parser file to '%s'\n", outputfilename);

  if (*outputfilename == '-')
    parserfile = stdout;
  else if ((parserfile = fopen(outputfilename, "w")) == NULL)
    text__message(TEXT__FATAL, "can't open parser file '%s' for writing\n", outputfilename);

  text__redirect(parserfile);

  /* print main file header */
  text__printf("/****************************************************************************\n"
	       "*\n"
	       "* Parser generated by RDP on ");

  print_time();

  text__printf(" from %s\n"
	       "*\n"
	       "****************************************************************************/\n"
	       "#include <time.h>\n"
	       "#include \"memalloc.h\"\n"
	       "#include \"scan.h\"\n"
	       "#include \"set.h\"\n"
	       "#include \"symbol.h\"\n"
	       "#include \"textio.h\"\n",
	       text__force_filetype(rdp__sourcefilename, "bnf"));

  if (rdp__dir_include != NULL)
    rdp__print_parser_include_line(rdp__dir_include);

  if (*outputfilename != '-')   /* suppress if stdout is destination: the headerfile */
    text__printf("#include \"%s\"\n", text__force_filetype(outputfilename, "h"));

  text__printf("\nchar\n *rdp__sourcefilename = NULL,   /* source file name */\n"
	       " *rdp__outputfilename = \"%s\",         /* output file name */\n", rdp__dir_output_file);
  text__printf("  rdp__symbol_echo = 0,                 /* symbol echo flag */\n"
	       "  rdp__verbose = 0,                     /* verbosity flag */\n"
	       "  rdp__pass;                            /* pass number */\n\n"
	       "int rdp__error_return = 0;              /* return value for main routine */\n\n"
	       "char *rdp__tokens = ", rdp__dir_title);

  str = rdp__token_string;
  for (token_count = 0; token_count < rdp__token_count; token_count++)
  {
    text__printf("\"%s\\0\" ", str);
    if (token_count % 8 == 0)
      text__printf("\n");
    while (*str++ != 0)
      ;
  }

  text__printf(";\n\n");

  temp_table = rdp__dir_symbol_table;
  while (temp_table != NULL)
  {
    text__printf("void* %s = NULL;\n", temp_table->name);
    temp_table = temp_table->next;
  }

  text__printf("\n\nstatic void rdp__help(char *msg)\n"
	       "{\n"
	       "  text__message(TEXT__FATAL, \"%%s\\n\\n\"\n"
	       "          \"%s\\n\"\n"
	       "          \"Generated on ", rdp__dir_title);
  print_time();
  text__printf(" and compiled on \" __DATE__ \" at \" __TIME__ \"\\n\\n\"\n");
  text__printf("          \"Usage: %.*s [options] source",
	       (int) strcspn(outputfilename, "."), outputfilename);

  if (*rdp__dir_suffix != 0)    /* skip suffix print if no suffix defined */
    text__printf("[.%s]", rdp__dir_suffix);

  text__printf("\\n\\n\"\n"
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
    rdp__print_parser_help_line(rdp__dir_help);

  text__printf("          ,msg == NULL ? \"\" : msg);\n"
	       "}\n");

  /* print load keyword function */
  text__printf("\n/* Load keywords */\nstatic void rdp__load_keywords(void)\n{\n");
  temp = (rdp__data*) symbol__next_symbol_in_scope(symbol__get_scope(tokens));
  while (temp != NULL)
  {
    if (temp->kind == K__TOKEN || temp->kind == K__EXTENDED)
    {
      text__printf("  scan__load_keyword(\"");
      rdp__print_parser_string(temp->id);
      text__printf("\", ");

      if (temp->close != NULL)
      {
	text__printf("\"", temp->extended_class);
	rdp__print_parser_string(temp->close);
	text__printf("\", ");
      }
      else
	text__printf("NULL, ");

      text__printf("%s", temp->token_enum);
      text__printf(");\n");
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
  text__printf("}\n");

  /* print set declaration */
  text__printf("\n/* Set declarations */\n\n");
  temp = (rdp__data*) symbol__next_symbol_in_scope(base);
  while (temp != NULL)
  {
    if (set__includes_element(&rdp__production_set, temp->kind) && !temp->code_only )
    {
      if (temp->first_cardinality > 1)
	text__printf("  set__ %s_first = SET__NULL;\n", temp->id);

      if (temp->kind == K__PRIMARY)
        text__printf("  set__ %s_stop = SET__NULL;\n", temp->id);
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }

  text__printf("\n/* Initialise sets */\n\nstatic void rdp__set_initialise(void)\n{\n");
  temp = (rdp__data*) symbol__next_symbol_in_scope(base);
  while (temp != NULL)
  {
    if (set__includes_element(&rdp__production_set, temp->kind) && !temp->code_only)
    {
      if (temp->first_cardinality > 1)
      {
	text__printf("  set__assign_list(&%s_first, ", temp->id);
	set__print_set(&temp->first, rdp__enum_string);
	text__printf(", SET__END);\n");
      }

      if (temp->kind == K__PRIMARY)
      {
	text__printf("  set__assign_list(&%s_stop, ", temp->id);
	set__print_set(&temp->follow, rdp__enum_string);
	text__printf(",SET__END);\n");
      }
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
  text__printf("}\n");

  /* print forward declarations */
  text__printf("\n/* Parser forward declarations and macros */\n");
  temp = (rdp__data*) symbol__next_symbol_in_scope(base);
  while (temp != NULL)
  {
    if (temp->kind == K__PRIMARY && (temp->call_count > 0))
    {
      int count;

      if (temp->code_only)
      {
	text__printf("#define %s", temp->id);
	if (temp->params != NULL)
	  rdp__print_parser_param_list(temp->params, 0);
	text__printf(" ");
	rdp__print_parser_alternate(temp, temp);
      }
      else
      {
	if (temp != rdp__start_prod)
	  text__printf("static ");

	text__printf("%s", temp->return_type);

	for (count = 0; count < temp->return_type_stars; count++)
	  text__printf("*");
	text__printf(" %s", temp->id);

	rdp__print_parser_param_list(temp->params, 1);

	text__printf(";\n");
      }
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }

  /* print parser definitions */
  rdp__print_parser_primaries(base);

  /* print main line routine */
  text__printf("int main(int argc, char *argv[])\n"
	       "{\n"
	       "  clock_t rdp__finish_time, rdp__start_time = clock();\n"
	       "  unsigned\n"
	       "    rdp__tabwidth = %u,            /* tab expansion width */\n"
	       "    rdp__symbol_statistics = 0,    /* show symbol__ table statistics flag */\n"
	       "    rdp__line_echo = 0;            /* make listing flag */\n\n"
	       "  unsigned long rdp__textsize = %lu;   /* size of scanner text array */\n\n"
	       "  while (--argc > 0)\n"
	       "  {\n"
	       "     if ((*++argv)[0] == '-')      /* switch */\n"
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
	       "           rdp__symbol_echo = 1;\n"
	       "           break;\n"
	       "         case 'l':\n"
	       "           rdp__line_echo = 1;\n"
	       "           break;\n"
	       "         case 't':\n"
	       "           sscanf(*argv + 2, \"%%u\", &rdp__tabwidth);\n"
	       "           break;\n"
	       "         case 'T':\n"
	       "           sscanf(*argv + 2, \"%%lu\", &rdp__textsize);\n"
	       "           break;\n"
	       "         case 'v':\n"
	       "           rdp__verbose = 1;\n"
	       "           break;\n",
	       rdp__dir_tab_width, rdp__dir_text_size);

  if (rdp__dir_help != NULL)
    rdp__print_parser_switch_line(rdp__dir_help);

  text__printf("         default:\n"
	       "           text__printf(\"\\nUnrecognised option -%%c\", (*argv)[1]);\n"
	       "           rdp__help(\"\");\n"
	       "       }\n"
	       "     }\n"
	       "   else\n"
	       "     rdp__sourcefilename = *argv;\n"
	       "  }\n");
  text__printf("  if (rdp__sourcefilename == NULL)\n"
	       "     rdp__help(\"No source file specified\");\n");

  text__printf("  text__init((size_t) rdp__textsize, %u, %u, rdp__tabwidth);\n"
	       "  scan__init(%u, %u, %u, rdp__symbol_echo, rdp__tokens);\n",
	       rdp__dir_max_errors, rdp__dir_max_warnings,
	       rdp__dir_case_insensitive, rdp__dir_newline_visible, rdp__dir_show_skips);

  temp_table = rdp__dir_symbol_table;
  while (temp_table != NULL)
  {
    text__printf("  %s = symbol__new_table(\"%s\", %u, %u, %s, %s, %s);\n",
		 temp_table->name,
		 temp_table->name,
		 temp_table->size,
		 temp_table->prime,
		 temp_table->compare,
		 temp_table->hash,
		 temp_table->print);

    temp_table = temp_table->next;
  }

  text__printf("  rdp__set_initialise();\n"
	       "  rdp__load_keywords();\n");

  text__printf("  if (rdp__verbose)\n"
	       "     text__printf(\"\\n%s\\nGenerated on ", rdp__dir_title);
  print_time();
  text__printf(" and compiled on \" __DATE__ \" at \" __TIME__ \"\\n\\n\");\n");

  if (rdp__dir_pre_parse != NULL)
    text__printf("  %s\n", rdp__dir_pre_parse);

  text__printf("  for (rdp__pass = 1; rdp__pass <= %u; rdp__pass++)\n"
	       "  {\n", rdp__dir_passes);

  text__printf(
	  "    if (text__total_errors() != 0)\n"
	  "      text__message(TEXT__FATAL, \"errors detected in source file\\n\");   /* crash quietly */ \n"
	  "    text__echo(rdp__line_echo && rdp__pass == %u);\n"
	  "    if (text__open(text__default_filetype(rdp__sourcefilename, \"%s\")) == NULL)\n"
	  "      rdp__help(\"Can't open source file\");\n\n"
	  "    text__get_char();\n"
	  "    scan__();\n\n",
	  rdp__dir_passes, rdp__dir_suffix);

  text__printf(
	  "    %s", rdp__start_prod->id);

  rdp__print_parser_param_list(rdp__start_prod->params, 0);

  text__printf(
	  ";            /* call parser at top level */\n"
	  "  }\n"
	  "  if (rdp__symbol_statistics)\n"
	  "    symbol__print_all_table_statistics(11);\n");

  if (rdp__dir_post_parse != NULL)
    text__printf("  %s\n", rdp__dir_post_parse);

  text__printf(
	  "  if (rdp__verbose)\n"
	  "  {\n"
	  "    rdp__finish_time = clock();\n"
	  "    text__message(TEXT__INFO, \"%%f CPU seconds used\\n\", ((double) (rdp__finish_time-rdp__start_time)) / CLOCKS_PER_SEC);\n"
	  "  }\n",
	  (int) strcspn(outputfilename, "."), outputfilename);

  text__printf("  return rdp__error_return;\n}\n");

  text__printf("\n/* End of %s */\n",
	  text__force_filetype(outputfilename, "c"));

  text__redirect(stdout);
}

void rdp__dump_extended(void * base)
{
  rdp__data* temp = (rdp__data*) symbol__next_symbol_in_scope(base);

  if (rdp__verbose)
    text__printf("\n Expanded EBNF listing\n\n");
  while (temp != NULL)
  {
    rdp__list *list = temp->list;
    int k = temp->kind,
     count;

    if (k != K__CODE && k != K__STRING && k != K__INTEGER && k != K__REAL)
    {
      text__printf(" ");
      rdp__print_parser_production_name(temp);

      rdp__print_parser_param_list(temp->params, 1);

      text__printf(":%s", temp->return_type);
      for (count = 0; count < temp->return_type_stars; count++)
	text__printf("*");
      text__printf(" ::= ");

      if (k == K__TOKEN)
	text__printf("'");
      text__printf("%s",
	      k == K__TOKEN ? temp->id :
	      k == K__REAL || k == K__INTEGER || k == K__STRING ? "(* Scanner primitive *) " :
	      k == K__DO_FIRST ? "( " :
	      k == K__LIST ? "< " :
	      k == K__CONDITIONAL ? "[ " :
	      k == K__ITERATION ? "{ " : "");

      while (list != NULL)
      {
	rdp__print_parser_production_name(list->production);
	rdp__print_parser_param_list(list->actuals, 0);

	if (list->return_name != NULL)
	  text__printf(":%s", list->return_name);

	text__printf(" ");
	list = list->next;
	if (k != K__SEQUENCE && list != NULL)
	  text__printf("| ");
      }

      text__printf("%s",
	      k == K__TOKEN ? "'" :
	      k == K__DO_FIRST ? ") " :
	      k == K__LIST ? "> " :
	      k == K__CONDITIONAL ? "] " :
	      k == K__ITERATION ? "} " : "");

      if (k == K__LIST)
	text__printf("'%s'", temp->supplementary_token->id);

      text__printf(".\n");

      text__printf(" First set is {%s", temp->contains_null ? "(NULL) " : "");
      set__print_set(&temp->first, rdp__token_string);
      text__printf("}\n");

      text__printf(" Stop set is {");
      set__print_set(&temp->follow, rdp__token_string);
      text__printf("}\n");

      if (temp->call_count == 1)
	text__printf(" Production is called once\n\n");
      else
	text__printf(" Production is called %u times\n\n", temp->call_count);
    }

    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
}

/* End of rdp_prnt.c */
