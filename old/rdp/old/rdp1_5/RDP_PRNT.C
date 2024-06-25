/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* rdp_prnt.c - rdp output routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <string.h>
#include <ctype.h>
#include "memalloc.h"
#include "scan.h"
#include "set.h"
#include "symbol.h"
#include "textio.h"
#include "rdp_aux.h"
#include "rdp_gram.h"
#include "rdp_prnt.h"
#include "rdp.h"

extern void * tokens; 
extern char * rdp_sourcefilename;  /* source file name loaded by main line */

char * rdp_token_string = NULL; 
char * rdp_enum_string = NULL; 

static unsigned rdp_indentation = 0;  /* current identation level */

static void rdp_indent(void)
{
  unsigned temp; 
  
  for (temp = 0; temp < rdp_indentation; temp++)
    text_printf("  "); 
}

/* print a token test: if follow is null then print a test that does not do
   error recovery. If card(first) is 1 or 0, print a scan_test only */

static void rdp_print_parser_test(char * first_name, set_ * first, char * follow_name)
{
  text_printf("scan_test"); 
  
  if (set_cardinality(first)> 1)
    text_printf("_set(%s%s%s, &%s_first", 
  rdp_error_production_name == 0 ? "": "\"", 
  rdp_error_production_name == 0 ? "NULL": first_name, 
  rdp_error_production_name == 0 ? "": "\"", 
  first_name
  ); 
  else
  {
    text_printf("(%s%s%s, ", 
    rdp_error_production_name == 0 ? "": "\"", 
    rdp_error_production_name == 0 ? "NULL": first_name, 
    rdp_error_production_name == 0 ? "": "\""); 
    set_print_set(first, rdp_enum_string, 78); 
  }
  
  if (follow_name == NULL)
    text_printf(", NULL)"); 
  else
    text_printf(", &%s_stop)", follow_name); 
}

static void rdp_print_parser_string(char * s)
{
  while (* s != 0)
  {
    if (* s == '\"' || * s == '\\' || * s == '\'')
      text_printf("\\"); 
    text_printf("%c", * s++); 
  }
}

static void rdp_print_parser_args(rdp_arg_list * p)
{
  if (p->next != NULL)
    rdp_print_parser_args(p->next); 
  switch (p->kind)
  {
    case ARG_BOOLEAN: 
    text_printf("  arg_boolean(\'%s\', \"%s\", &%s);\n", p->key, p->desc, p->var); 
    break; 
    case ARG_NUMERIC: 
    text_printf("  arg_numeric(\'%s\', \"%s\", &%s);\n", p->key, p->desc, p->var); 
    break; 
    case ARG_STRING: 
    text_printf("  arg_string (\'%s\', \"%s\", &%s);\n", p->key, p->desc, p->var); 
    break; 
    case ARG_BLANK: 
    text_printf("  arg_message(\"%s\");\n", p->desc); 
    break; 
  }
}

void rdp_make_token_string(void * base)
{
  char * p_char, 
  * p_char_esc, 
  * p_string, 
  * p_string_esc, 
  * p_comment, 
  * p_comment_visible, 
  * p_comment_nest, 
  * p_comment_nest_visible, 
  * p_comment_line, 
  * p_comment_line_visible, 
  * p_ignore; 
  
  rdp_data * p =(rdp_data *) symbol_next_symbol_in_scope(base); 
  
  rdp_token_string = text_insert_string("IGNORE"); 
  text_insert_string("ID"); 
  text_insert_string("INTEGER"); 
  text_insert_string("REAL"); 
  text_insert_string("CHAR"); 
  text_insert_string("CHAR_ESC"); 
  text_insert_string("STRING"); 
  text_insert_string("STRING_ESC"); 
  text_insert_string("COMMENT"); 
  text_insert_string("COMMENT_VISIBLE"); 
  text_insert_string("COMMENT_NEST"); 
  text_insert_string("COMMENT_NEST_VISIBLE"); 
  text_insert_string("COMMENT_LINE"); 
  text_insert_string("COMMENT_LINE_VISIBLE"); 
  text_insert_string("EOF"); 
  text_insert_string("EOLN"); 
  
  while (p != NULL)
  {
    if (p->kind == K_TOKEN || p->kind == K_EXTENDED)
    {
      char * c = p->id; 
      
      p->token_string = text_insert_char('\'');  /* insert open quote */
      while (* c != 0)        /* iterate to end of string */
      {
        if (* c == '\"' || * c == '\\' || * c == '\'')
          text_insert_char('\\'); 
        text_insert_char(* c++); 
      }
      text_insert_string("\'");  /* insert close quote */
    }
    p =(rdp_data *) symbol_next_symbol_in_scope(p); 
  }
  
  p =(rdp_data *) symbol_next_symbol_in_scope(base); 
  
  p_ignore = rdp_enum_string = text_insert_string("SCAN_P_IGNORE"); 
  text_insert_string("SCAN_P_ID"); 
  text_insert_string("SCAN_P_INTEGER"); 
  text_insert_string("SCAN_P_REAL"); 
  p_char = text_insert_string("SCAN_P_CHAR"); 
  p_char_esc = text_insert_string("SCAN_P_CHAR_ESC"); 
  p_string = text_insert_string("SCAN_P_STRING"); 
  p_string_esc = text_insert_string("SCAN_P_STRING_ESC"); 
  p_comment = text_insert_string("SCAN_P_COMMENT"); 
  p_comment_visible = text_insert_string("SCAN_P_COMMENT_VISIBLE"); 
  p_comment_nest = text_insert_string("SCAN_P_COMMENT_NEST"); 
  p_comment_nest_visible = text_insert_string("SCAN_P_COMMENT_NEST_VISIBLE"); 
  p_comment_line = text_insert_string("SCAN_P_COMMENT_LINE"); 
  p_comment_line_visible = text_insert_string("SCAN_P_COMMENT_LINE_VISIBLE"); 
  text_insert_string("SCAN_P_EOF"); 
  text_insert_string("SCAN_P_EOLN"); 
  
  while (p != NULL)
  {
    if (p->kind == K_TOKEN || p->kind == K_EXTENDED)
    {
      p->token_enum = text_insert_characters("RDP_T_"); 
      
      if (text_is_valid_C_id(p->id))
        text_insert_string(p->id); 
      else
      {
        char * c = p->id; 
        
        text_insert_integer(p->token_value); 
        text_insert_characters(" /* "); 
        if (strcmp(c, "/*")== 0) /* special case: put a / * in the comment */
        {
          text_insert_char('/'); 
          text_insert_char(' '); 
          text_insert_char('*'); 
        }
        else if (strcmp(c, "*/")== 0) /* special case: put a * / in the comment */
        {
          text_insert_char('*'); 
          text_insert_char(' '); 
          text_insert_char('/'); 
        }
        else while (* c != 0)
          text_insert_char(* c++); 
        
        text_insert_string(" */"); 
      }
      if (p->kind == K_EXTENDED)
      {
        switch (p->extended_value)
        {
          case SCAN_P_CHAR: 
          p->extended_enum = p_char; 
          break; 
          case SCAN_P_CHAR_ESC: 
          p->extended_enum = p_char_esc; 
          break; 
          case SCAN_P_STRING: 
          p->extended_enum = p_string; 
          break; 
          case SCAN_P_STRING_ESC: 
          p->extended_enum = p_string_esc; 
          break; 
          case SCAN_P_COMMENT: 
          p->extended_enum = p_comment; 
          break; 
          case SCAN_P_COMMENT_VISIBLE: 
          p->extended_enum = p_comment_visible; 
          break; 
          case SCAN_P_COMMENT_NEST: 
          p->extended_enum = p_comment_nest; 
          break; 
          case SCAN_P_COMMENT_NEST_VISIBLE: 
          p->extended_enum = p_comment_nest_visible; 
          break; 
          case SCAN_P_COMMENT_LINE: 
          p->extended_enum = p_comment_line; 
          break; 
          case SCAN_P_COMMENT_LINE_VISIBLE: 
          p->extended_enum = p_comment_line_visible; 
          break; 
        }
      }
      else
        p->extended_enum = p_ignore; 
    }
    p =(rdp_data *) symbol_next_symbol_in_scope(p); 
  }
}

static void rdp_print_parser_param_list_sub(rdp_param_list * param, int last, int definition)
{
  if (param != NULL)
  {
    unsigned count; 
    
    rdp_print_parser_param_list_sub(param->next, 0, definition); 
    text_printf("%s", definition ? param->type: ""); 
    
    if (definition)
      for (count = 0; count < param->stars; count++)
      text_printf("*"); 
    
    text_printf("%s", definition ? " ": ""); 
    switch (param->flavour)
    {
      case PARAM_INTEGER: 
      text_printf("%lu", param->n); 
      break; 
      case PARAM_REAL: 
      text_printf("%lf", param->r); 
      break; 
      case PARAM_STRING: 
      text_printf("\"%s\"", param->id); 
      break; 
      case PARAM_ID: 
      text_printf("%s", param->id); 
      break; 
    }
    text_printf("%s", last ? "": ", "); 
  }
}

static void rdp_print_parser_param_list(char * first, rdp_param_list * params, int definition)
{
  text_printf("("); 
  
  /* processing for tree parameter */
  if (rdp_dir_tree)
  {
    if (definition)
      text_printf("rdp_tree_data* rdp_tree"); 
    else
    {
      if (first == NULL)
        text_printf("rdp_tree"); 
      else
        text_printf("rdp_add_child(\"%s\", rdp_tree)", first); 
    }
    
    if (params != NULL)
      text_printf(", ");      /* put in separator for rest of parameters */
  }
  
  if (params == NULL && definition && !rdp_dir_tree)
    text_printf("void"); 
  else
    rdp_print_parser_param_list_sub(params, 1, definition); 
  
  text_printf(")"); 
}

static void rdp_print_parser_production_name(rdp_data * n)
{
  switch (n->kind)
  {
    case K_CODE: 
    text_printf("[*%s*]", n->id); 
    break; 
    case K_EXTENDED: 
    case K_TOKEN: 
    text_printf("%s", n->token_enum); 
    break; 
    case K_INTEGER: 
    case K_REAL: 
    case K_STRING: 
    text_printf("SCAN_P_%s", n->id); 
    break; 
    default: 
    text_printf("%s", n->id); 
    break; 
  }
}

/* dignostic print routines */
static void rdp_print_sub_sequence(rdp_data * production, int expand); 
static void rdp_print_sub_alternate(rdp_data * production, int expand); 

void rdp_print_sub_item(rdp_data * prod, int expand)
{
  switch (prod->kind)
  {
    case K_INTEGER: 
    case K_STRING: 
    case K_REAL: 
    case K_EXTENDED: 
    text_printf("%s ", prod->id); 
    break; 
    case K_TOKEN: 
    text_printf("\'%s\' ", prod->id); 
    break; 
    case K_CODE: 
    /* Don't print anything */
    break; 
    case K_PRIMARY: 
    text_printf("%s ", prod->id); 
    break; 
    case K_SEQUENCE: 
    rdp_print_sub_sequence(prod, expand); 
    break; 
    case K_LIST: 
    if (expand)
    {
      /* first find special cases */
      if (prod->supplementary_token == NULL) /* All EBNF forms have no delimiter */
      {
        if (prod->lo == 0 && prod->hi == 0)
        {
          text_printf("{ "); 
          rdp_print_sub_alternate(prod, expand); 
          text_printf("} "); 
        }
        else if (prod->lo == 0 && prod->hi == 1)
        {
          text_printf("[ "); 
          rdp_print_sub_alternate(prod, expand); 
          text_printf("] "); 
        }
        else if (prod->lo == 1 && prod->hi == 0)
        {
          text_printf("< "); 
          rdp_print_sub_alternate(prod, expand); 
          text_printf("> "); 
        }
        else if (prod->lo == 1 && prod->hi == 1)
        {
          text_printf("( "); 
          rdp_print_sub_alternate(prod, expand); 
          text_printf(") "); 
        }
      }
      else
      {                       /* Now do general case */
        text_printf("( "); 
        rdp_print_sub_alternate(prod, expand); 
        text_printf(")%lu@%lu", prod->lo, prod->hi); 
        if (prod->supplementary_token != NULL)
          text_printf(" \'%s\'", prod->supplementary_token->id); 
        else
          text_printf(" #"); 
      }
    }
    else
      text_printf("%s ", prod->id); 
    break; 
    default: 
    text_message(TEXT_FATAL, "internal error - unexpected kind found\n"); 
  }
}

static void rdp_print_sub_sequence(rdp_data * production, int expand)
{
  rdp_list * list = production->list; 
  
  while (list != NULL)
  {
    rdp_print_sub_item(list->production, expand); 
    list = list->next; 
  }
}

static void rdp_print_sub_alternate(rdp_data * production, int expand)
{
  rdp_list * list = production->list; 
  
  while (list != NULL)
  {
    rdp_print_sub_item(list->production, expand); 
    
    if ((list = list->next)!= NULL)
      text_printf("| "); 
  }
}

void rdp_print_header(char * headerfilename)
{
  FILE * headerfile; 
  
  rdp_table_list * temp_table = rdp_dir_symbol_table; 
  rdp_data * temp =(rdp_data *) symbol_next_symbol_in_scope(symbol_get_scope(tokens)); 
  unsigned count = 0; 
  int first = 1; 
  char * filenamebase = text_uppercase_string(text_extract_filename(rdp_sourcefilename)); 
  
  if (rdp_verbose)
    text_message(TEXT_INFO, "Dumping header file to \'%s\'\n", headerfilename); 
  
  if (* headerfilename == '-')
    headerfile = stdout; 
  else if ((headerfile = fopen(headerfilename, "w"))== NULL)
    text_message(TEXT_FATAL, "unable to open header output file \'%s\' for writing\n", headerfilename); 
  
  text_redirect(headerfile); 
  
  text_printf(
  "/*******************************************************************************\n"
  "*\n"
  "* Header file generated by RDP on "); 
  
  text_print_time(); 
  
  text_printf(
  " from %s\n"
  "*\n"
  "*******************************************************************************/\n"
  "#ifndef %s_H\n"
  "#define %s_H\n\n"
  "#include \"scan.h\"\n", 
  text_force_filetype(rdp_sourcefilename, "bnf"), 
  filenamebase, filenamebase
  ); 
  
  text_printf("\n/* Maximum number of passes */\n#define RDP_PASSES %u\n", rdp_dir_passes); 
  
  text_printf("\n/* Time and date stamp */\n#define RDP_STAMP \"Generated on ");
  text_print_time();
  text_printf(" and compiled on \" __DATE__ \" at \" __TIME__ \n");
  
  /* print token enumeration */
  text_printf("\n/* Token enumeration */\nenum\n{\nRDP_TT_BOTTOM = SCAN_P_TOP"); 
  while (temp != NULL)
  {
    if (temp->kind == K_TOKEN || temp->kind == K_EXTENDED)
    {
      text_printf(","); 
      if (count++ % 8 == 0)
        text_printf("\n"); 
      rdp_print_parser_production_name(temp); 
      if (first)
      {
        text_printf(" = SCAN_P_TOP"); 
        first = 0; 
      }
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
  text_printf(",\nRDP_TT_TOP\n};\n\n"); 
  
  /* print declaration for tree datatype */
  text_printf("/* Tree data type */\n\n"
  "typedef struct rdp_tree_data_node\n{\n  SCAN_DATA\n  %s\n} rdp_tree_data;\n\n", 
  rdp_dir_tree_fields
  ); 
  
  /* print declarations for symbol tables */
  text_printf("/* Symbol table support */\n"); 
  while (temp_table != NULL)
  {
    text_printf("typedef struct %s_data_node\n{\n%s\n} %s_data;\n", 
    temp_table->name, temp_table->data_fields, temp_table->name); 
    text_printf("extern void * %s;\n", temp_table->name); 
    text_printf("extern %s_data * %s_temp;\n", temp_table->name, temp_table->name); 
    text_printf("#define %s_cast(x) ((%s_data *)x)\n\n", temp_table->name, temp_table->name); 
    temp_table = temp_table->next; 
  }
  
  /* print start production prototype */
  text_printf("/* Parser start production */\n"); 
  text_printf("%s", rdp_start_prod->return_type); 
  for (count = 0; count < rdp_start_prod->return_type_stars; count++)
    text_printf("*"); 
  text_printf(" %s", rdp_start_prod->id); 
  
  rdp_print_parser_param_list(NULL, rdp_start_prod->params, 1); 
  
  text_printf(";\n\n#endif\n\n/* End of %s */\n", 
  text_force_filetype(headerfilename, "h")); 
  
  text_redirect(stdout); 
}

static void rdp_print_parser_alternate(rdp_data * production, rdp_data * primary); 
static void rdp_print_parser_sequence(rdp_data * production, rdp_data * primary); 

static void rdp_print_parser_subproduction(rdp_data * prod, rdp_data * primary, int promote_epsilon, char * default_action)
{
  if (prod->lo == 0)          /* this can be an optional body */
  {
    text_printf("if ("); 
    rdp_print_parser_test(prod->id, & prod->first, NULL); 
    text_printf(")\n"); 
    rdp_indent(); 
  }
  
  text_printf("{ /* Start of %s */\n", prod->id); 
  
  if (prod->ll1_violation)
  {
    rdp_indent(); 
    text_printf("/* WARNING - an LL(1) violation was detected at this point in the grammar */\n"); 
  }
  
  rdp_indentation++; 
  
  /* We don't need to instantiate count if hi is infinity and lo is 0 or 1 */
  if (!((prod->hi == 0 || prod->hi == 1)&&(prod->lo == 1 || prod->lo == 0)))
  {
    rdp_indent(); 
    text_printf("unsigned long rdp_count = 0;\n"); 
  }
  
  rdp_indent(); 
  text_printf("while (1)\n"); 
  
  rdp_indent(); 
  text_printf("{\n"); 
  rdp_indentation++; 
  
  /* Put in test that first element of body matches if iterator low count > 0 and prod isn't nullable */
  if (prod->lo != 0 && !prod->contains_null)
  {
    rdp_indent(); 
    rdp_print_parser_test(prod->id, & prod->first, primary->id); 
    text_printf(";\n"); 
  }
  
  rdp_indent(); 
  text_printf("{\n"); 
  rdp_indentation++; 
  
  rdp_print_parser_alternate(prod, primary); 
  
  rdp_indent(); 
  text_printf("}\n"); 
  rdp_indentation--; 
  
  if (!((prod->hi == 0 || prod->hi == 1)&&(prod->lo == 1 || prod->lo == 0)))
  {
    rdp_indent(); 
    text_printf("rdp_count++;\n"); 
  }
  
  if (prod->hi > 1)           /* Don't bother testing rdp_count of hi is zero or infty */
  {
    rdp_indent(); 
    text_printf("if (rdp_count == %lu) break;\n", prod->hi); 
  }
  
  if (prod->supplementary_token != NULL)
  {
    rdp_indent(); 
    text_printf("if (SCAN_CAST->token != %s) break;\n", prod->supplementary_token->token_enum); 
    
    if (rdp_dir_tree)
    {
      if (prod->delimiter_promote == PROMOTE_DONT)
      {
        /* add a tree node for this scanner item */
        text_printf("if (rdp_tree_update) memcpy(graph_insert_node_child(sizeof(rdp_tree_data), 0, rdp_tree), text_scan_data, sizeof(scan_data));\n"); 
        rdp_indent(); 
      }
      else if (prod->delimiter_promote == PROMOTE_AND_COPY)
      {
        /* copy scanner data to current tree parent */
        text_printf("if (rdp_tree_update) memcpy(rdp_tree, text_scan_data, sizeof(scan_data));\n"); 
        rdp_indent(); 
      }
    }
    
    rdp_indent(); 
    text_printf("scan_();\n");  /* skip list token */
  }
  else if (prod->hi != 1)
  {
    rdp_indent(); 
    text_printf("if (!"); 
    rdp_print_parser_test(prod->id, & prod->first, NULL); 
    text_printf(") break;\n"); 
  }
  
  if (prod->hi == 1)
  {
    rdp_indent(); 
    text_printf("break;   /* hi limit is 1! */\n"); 
  }
  
  rdp_indentation--; 
  rdp_indent(); 
  text_printf("}\n"); 
  
  if (prod->lo > 1)           /* test rdp_count on way out */
  {
    rdp_indent(); 
    text_printf("if (rdp_count < %lu)", prod->lo); 
    text_printf("  text_message(TEXT_ERROR_ECHO, \"iteration count too low\\n\");\n"); 
  }
  
  rdp_indentation--; 
  rdp_indent(); 
  text_printf("} /* end of %s */\n", prod->id); 
  
  if (prod->lo == 0 &&(rdp_dir_tree || default_action != NULL))
  {
    rdp_indent(); 
    text_printf("else\n"); 
    rdp_indent(); 
    text_printf("{\n"); 
    rdp_indentation++; 
    rdp_indent(); 
    text_printf("/* default action processing for %s*/\n", prod->id); 
    if (rdp_dir_tree)
    {
      /* First do tree node handling */
      if (promote_epsilon == PROMOTE_DONT)
      {
        /* add an epsilon tree node */
        rdp_indent(); 
        text_printf("if (rdp_tree_update) {rdp_tree_data *temp = (rdp_tree_data*) graph_insert_node_child(sizeof(rdp_tree_data), 0, rdp_tree); temp->id = \"%s%s\"; temp->token = SCAN_P_ID;}\n", rdp_dir_annotated_epsilon_tree ? "#: ": "", rdp_dir_annotated_epsilon_tree ? prod->id + 4: ""); 
      }
      else if (promote_epsilon == PROMOTE_AND_COPY)
      {
        /* copy epsilon to current tree parent */
        rdp_indent(); 
        text_printf("if (rdp_tree_update) {rdp_tree->id = \"%s%s\"; rdp_tree->token = SCAN_P_ID;}\n", rdp_dir_annotated_epsilon_tree ? "#: ": "", rdp_dir_annotated_epsilon_tree ? prod->id + 4: ""); 
      }
    }
    
    /* Now copy out default action */
    if (!rdp_parser_only && default_action != NULL) /* disabled by -p option */
    {
      while (* default_action != '\0')
      {
        if (* default_action == '\n')
          text_printf(" \\\n"); 
        else
          text_printf("%c", * default_action); 
        default_action++; 
      }
      
      text_printf("\n");      /* terminate semantic actions tidily */
    }
    
    rdp_indentation--; 
    rdp_indent(); 
    text_printf("}\n"); 
  }
}

static void rdp_print_parser_item(rdp_data * prod, rdp_data * primary, char * return_name, rdp_param_list * actuals, int promote_epsilon, int promote, char * default_action)
{
  if (promote == PROMOTE_DEFAULT)
    promote = prod->promote_default; 
  
  if (!(prod->kind == K_CODE && prod->code_successor))
    rdp_indent();             /* Don't indent code sequence-internal or inline items */
  
  switch (prod->kind)
  {
    case K_INTEGER: 
    case K_REAL: 
    case K_STRING: 
    case K_EXTENDED: 
    case K_TOKEN: 
    if (rdp_dir_tree)
    {
      if (promote == PROMOTE_DONT)
      {
        /* add a tree node for this scanner item as child of current parent */
        text_printf("if (rdp_tree_update) memcpy(graph_insert_node_child(sizeof(rdp_tree_data), 0, rdp_tree), text_scan_data, sizeof(scan_data));\n"); 
        rdp_indent(); 
      }
      else if (promote == PROMOTE_AND_COPY)
      {
        /* copy scanner data to current tree parent */
        text_printf("if (rdp_tree_update) memcpy(rdp_tree, text_scan_data, sizeof(scan_data));\n"); 
        rdp_indent(); 
      }
      else if (promote == PROMOTE_ABOVE)
      {
        /* add a tree node for this scanner item as parent of current parent*/
        text_printf("if (rdp_tree_update) memcpy(graph_insert_node_parent(sizeof(rdp_tree_data), 0, rdp_tree), text_scan_data, sizeof(scan_data));\n"); 
        rdp_indent(); 
      }
    }
    text_printf("scan_test(%s%s%s, ", 
    rdp_error_production_name == 0 ? "": "\"", 
    rdp_error_production_name == 0 ? "NULL": primary->id, 
    rdp_error_production_name == 0 ? "": "\""); 
    
    rdp_print_parser_production_name(prod); 
    text_printf(", &%s_stop);\n", primary->id); 
    rdp_indent(); 
    if (return_name != NULL && !rdp_parser_only) /* disable if -p option used */
    {
      text_printf("%s = SCAN_CAST->%s;\n", return_name, 
      prod->kind == K_REAL ? "data.r": 
      prod->kind == K_INTEGER ? "data.i": "id"); 
      rdp_indent(); 
    }
    text_printf("scan_();\n"); 
    break; 
    case K_CODE: 
    if (!rdp_parser_only)     /* disabled by -p option */
    {
      char * temp = prod->id; 
      
      if (prod->code_pass != 0)
        text_printf("if (rdp_pass == %u) { \\\n", prod->code_pass); 
      
      while (* temp != '\0')
      {
        if (* temp == '\n')
          text_printf(" \\\n"); 
        else
          text_printf("%c", * temp); 
        temp++; 
      }
      
      if (prod->code_pass != 0)
        text_printf(" \\\n}"); 
      
      if (prod->kind == K_CODE && prod->code_terminator)
        text_printf("\n");    /* terminate semantic actions tidily */
    }
    break; 
    case K_PRIMARY: 
    if (rdp_dir_tree && promote == PROMOTE_AND_COPY)
      text_printf("rdp_tree->id = \"%s\"; rdp_tree->token = 0;\n", prod->id); 
    if (return_name != NULL && !rdp_parser_only) /* disable if -p option set! */
      text_printf("%s = ", return_name); 
    text_printf("%s", prod->id); 
    if (!(prod->code_only && actuals == NULL))
      rdp_print_parser_param_list(promote == PROMOTE_DONT ? prod->id: (char *) NULL, actuals, 0); 
    text_printf(";\n"); 
    break; 
    case K_SEQUENCE: 
    text_message(TEXT_FATAL, "internal error - unexpected alternate in sequence\n"); 
    break; 
    case K_LIST: 
    rdp_print_parser_subproduction(prod, primary, promote_epsilon, default_action); 
    break; 
    default: 
    text_message(TEXT_FATAL, "internal error - unexpected kind found\n"); 
  }
}

static void rdp_print_parser_sequence(rdp_data * production, rdp_data * primary)
{
  rdp_list * list = production->list; 
  
  while (list != NULL)
  {
    rdp_print_parser_item(list->production, primary, list->return_name, list->actuals, list->promote_epsilon, list->promote, list->default_action); 
    list = list->next; 
  }
}

static void rdp_print_parser_alternate(rdp_data * production, rdp_data * primary)
{
  rdp_list * list = production->list; 
  
  if (list->next == NULL)     /* special case: only one alternate */
    rdp_print_parser_sequence(list->production, primary); 
  else
  {
    while (list != NULL)
    {
      if (list->production->kind != K_SEQUENCE)
        text_message(TEXT_FATAL, "internal error - expecting alternate\n"); 
      
      rdp_indent(); 
      
      text_printf("if ("); 
      rdp_print_parser_test(list->production->id, & list->production->first, NULL); 
      text_printf(")\n"); 
      rdp_indent(); 
      text_printf("{\n"); 
      rdp_indentation++; 
      
      rdp_print_parser_sequence(list->production, primary); 
      
      rdp_indentation--; 
      rdp_indent(); 
      
      text_printf("}\n"); 
      
      if ((list = list->next)!= NULL)
      {
        rdp_indent(); 
        text_printf("else\n"); 
      }
      else
        /* tail test at end of alternates */
      if (!(production->contains_null && production->lo != 0))
      {
        rdp_indent(); 
        text_printf("else\n"); 
        rdp_indentation++; 
        rdp_indent(); 
        rdp_print_parser_test(production->id, & production->first, primary->id); 
        rdp_indentation--; 
        rdp_indent(); 
        text_printf(";\n"); 
      }
    }
  }
}

static void rdp_print_locals(void * base)
{
  rdp_list * list; 
  unsigned temp_int; 
  
  if (!set_includes_element(& rdp_production_set, rdp_cast(base)->kind))
    return; 
  
  list = rdp_cast(base)->list; 
  
  while (list != NULL)
  {
    if (list->production->kind != K_PRIMARY)
      rdp_print_locals(list->production); 
    
    if (list->return_name != NULL)
      if (symbol_lookup_key(locals, &(list->return_name), NULL)== NULL)
    {
      locals_data * local =(locals_data *) symbol_new_symbol(sizeof(locals_data)); 
      
      local->id = list->return_name; 
      symbol_insert_symbol(locals, local); 
      
      text_printf("  %s", list->production->return_type); 
      for (temp_int = 0; temp_int < list->production->return_type_stars; temp_int++)
        text_printf("*"); 
      text_printf(" %s;\n", list->return_name); 
    }
    
    list = list->next; 
  }
}

static void rdp_print_parser_primaries(void * base)
{
  rdp_data * temp =(rdp_data *) symbol_next_symbol_in_scope(base); 
  unsigned temp_int; 
  
  text_printf("\n/* Parser functions */\n"); 
  while (temp != NULL)
  {
    if (temp->kind == K_PRIMARY &&(temp->call_count > 0)&& !temp->code_only)
    {
      unsigned count, 
      is_void =((strcmp(temp->return_type, "void")== 0)&& temp->return_type_stars == 0); 
      
      void * local_scope = symbol_new_scope(locals, temp->id); 
      
      if (temp != rdp_start_prod)
        text_printf("static "); 
      
      text_printf("%s", temp->return_type); 
      
      for (count = 0; count < temp->return_type_stars; count++)
        text_printf("*"); 
      text_printf(" %s", temp->id); 
      
      rdp_print_parser_param_list(NULL, temp->params, 1); 
      
      text_printf("\n{\n"); 
      
      if (temp->ll1_violation)
        text_printf("/* WARNING - an LL(1) violation was detected at this point in the grammar */\n"); 
      
      /* scan all subproductions and add return variables to symbol_ table */
      if (!is_void)
      {
        text_printf("  %s", temp->return_type); 
        for (temp_int = 0; temp_int < temp->return_type_stars; temp_int++)
          text_printf("*"); 
        text_printf(" result;\n"); 
      }
      
      if (!rdp_parser_only)   /* disabled by -p option */
        rdp_print_locals(temp); 
      
      symbol_unlink_scope(local_scope); 
      
      /* In trace mode, add an entry message */
      if (rdp_trace)
        text_printf("  text_message(TEXT_INFO, \"Entered \'%s\'\\n\");\n\n", temp->id); 
      #if 0
      text_printf("  if ("); 
      rdp_print_parser_test(temp->id, & temp->first, temp->contains_null ?(char *) NULL: temp->id); 
      text_printf(")\n"); 
      #endif
      text_printf("  {\n"); 
      rdp_indentation = 2; 
      
      rdp_print_parser_alternate(temp, temp); 
      
      /* add error handling on exit */
      #if 1
      text_printf("    scan_test_set(%s%s%s, &%s_stop, &%s_stop);\n", 
      rdp_error_production_name == 0 ? "": "\"", 
      rdp_error_production_name == 0 ? "NULL": temp->id, 
      rdp_error_production_name == 0 ? "": "\"", 
      temp->id, temp->id); 
      #endif
      text_printf("   }\n"); 
      /* In trace mode, add an exit message */
      if (rdp_trace)
        text_printf("  text_message(TEXT_INFO, \"Exited  \'%s\'\\n\");\n", temp->id); 
      
      text_printf("%s}\n\n", is_void ? "": "  return result;\n"); 
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
}

static void rdp_print_parser_include_line(rdp_string_list * p)
{
  if (p->next != NULL)
    rdp_print_parser_include_line(p->next); 
  text_printf("#include \"%s\"\n", p->str1); 
}

void rdp_print_parser(char * outputfilename, void * base)
{
  rdp_data * temp; 
  rdp_table_list * temp_table; 
  FILE * parserfile; 
  unsigned token_count; 
  char * str; 
  
  if (rdp_verbose)
    text_message(TEXT_INFO, "Dumping parser file to \'%s\'\n", outputfilename); 
  
  if (* outputfilename == '-')
    parserfile = stdout; 
  else if ((parserfile = fopen(outputfilename, "w"))== NULL)
    text_message(TEXT_FATAL, "unable to open parser file \'%s\' for writing\n", outputfilename); 
  
  text_redirect(parserfile); 
  
  /* print main file header */
  text_printf("/*******************************************************************************\n"
  "*\n"
  "* Parser generated by RDP on "); 
  
  text_print_time(); 
  text_printf(" from %s\n"
  "*\n"
  "*******************************************************************************/\n"
  "#include <time.h>\n"
  "#include \"arg.h\"\n"
  "#include \"graph.h\"\n"
  "#include \"memalloc.h\"\n"
  "#include \"scan.h\"\n"
  "#include \"set.h\"\n"
  "#include \"symbol.h\"\n"
  "#include \"textio.h\"\n", 
  text_force_filetype(rdp_sourcefilename, "bnf")); 
  
  if (rdp_dir_include != NULL)
    rdp_print_parser_include_line(rdp_dir_include); 
  
  if (* outputfilename != '-') /* suppress if stdout is destination: the headerfile */
    text_printf("#include \"%s\"\n", text_force_filetype(outputfilename, "h")); 
  
  text_printf("\nchar\n *rdp_sourcefilename = NULL,   /* source file name */\n"
  " *rdp_outputfilename = \"%s\";         /* output file name */\n\n", rdp_dir_output_file); 
  
  text_printf("int\n  rdp_symbol_echo = 0,                 /* symbol echo flag */\n"
  "  rdp_verbose = 0,                     /* verbosity flag */\n"
  "  rdp_pass;                            /* pass number */\n\n"
  "int rdp_error_return = 0;              /* return value for main routine */\n\n"
  "char *rdp_tokens = ", rdp_dir_title); 
  
  str = rdp_token_string; 
  for (token_count = 0; token_count < rdp_token_count; token_count++)
  {
    text_printf("\"%s\\0\" ", str); 
    if (token_count % 8 == 0)
      text_printf("\n"); 
    while (* str++ != 0)
      ; 
  }
  
  text_printf(";\n\n"); 
  
  temp_table = rdp_dir_symbol_table; 
  while (temp_table != NULL)
  {
    text_printf("%s_data * %s_temp = NULL;\n", temp_table->name, temp_table->name); 
    text_printf("void* %s = NULL;\n", temp_table->name); 
    temp_table = temp_table->next; 
  }
  
  /* print tree update flag and routine */
  if (rdp_dir_tree)
    text_printf("\n/* Tree update function for noterminal nodes */\n"
  "int rdp_tree_update = 0;\n\nrdp_tree_data* rdp_add_child(char* id, rdp_tree_data* rdp_tree)\n{\n"
  "  rdp_tree_data* child;\n\n"
  "  if (rdp_tree_update)\n"
  "  {\n"
  "    child  = (rdp_tree_data*) graph_insert_node_child(sizeof(rdp_tree_data), 0, rdp_tree);\n\n"
  "    child->id = id;\n"
  "    return child;\n"
  "  }\n"
  "  else\n"
  "    return NULL;\n"
  "}\n"
  ); 
  
  /* print load keyword function */
  text_printf("\n/* Load keywords */\nstatic void rdp_load_keywords(void)\n{\n"); 
  temp =(rdp_data *) symbol_next_symbol_in_scope(symbol_get_scope(tokens)); 
  while (temp != NULL)
  {
    if (temp->kind == K_TOKEN || temp->kind == K_EXTENDED)
    {
      text_printf("  scan_load_keyword(\""); 
      rdp_print_parser_string(temp->id); 
      text_printf("\", "); 
      
      if (temp->close != NULL)
      {
        text_printf("\""); 
        rdp_print_parser_string(temp->close); 
        text_printf("\", "); 
      }
      else
        text_printf("NULL, "); 
      
      text_printf("%s, ", temp->token_enum); 
      text_printf("%s", temp->extended_enum); 
      text_printf(");\n"); 
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
  text_printf("}\n"); 
  
  /* print set declaration */
  text_printf("\n/* Set declarations */\n\n"); 
  temp =(rdp_data *) symbol_next_symbol_in_scope(base); 
  while (temp != NULL)
  {
    if (set_includes_element(& rdp_production_set, temp->kind)&& !temp->code_only)
    {
      if (temp->first_cardinality > 1)
        text_printf("  set_ %s_first = SET_NULL;\n", temp->id); 
      
      if (temp->kind == K_PRIMARY)
        text_printf("  set_ %s_stop = SET_NULL;\n", temp->id); 
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
  
  text_printf("\n/* Initialise sets */\n\nstatic void rdp_set_initialise(void)\n{\n"); 
  temp =(rdp_data *) symbol_next_symbol_in_scope(base); 
  while (temp != NULL)
  {
    if (set_includes_element(& rdp_production_set, temp->kind)&& !temp->code_only)
    {
      if (temp->first_cardinality > 1)
      {
        text_printf("  set_assign_list(&%s_first, ", temp->id); 
        set_print_set(& temp->first, rdp_enum_string, 78); 
        text_printf(", SET_END);\n"); 
      }
      
      if (temp->kind == K_PRIMARY)
      {
        text_printf("  set_assign_list(&%s_stop, ", temp->id); 
        set_print_set(& temp->follow, rdp_enum_string, 78); 
        text_printf(",SET_END);\n"); 
      }
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
  text_printf("}\n"); 
  
  /* print forward declarations */
  text_printf("\n/* Parser forward declarations and macros */\n"); 
  temp =(rdp_data *) symbol_next_symbol_in_scope(base); 
  while (temp != NULL)
  {
    if (temp->kind == K_PRIMARY &&(temp->call_count > 0))
    {
      unsigned count; 
      
      if (temp->code_only)
      {
        text_printf("#define %s", temp->id); 
        if (temp->params != NULL)
          rdp_print_parser_param_list(NULL, temp->params, 0); 
        text_printf(" "); 
        rdp_print_parser_alternate(temp, temp); 
      }
      else
      {
        if (temp != rdp_start_prod)
          text_printf("static "); 
        
        text_printf("%s", temp->return_type); 
        
        for (count = 0; count < temp->return_type_stars; count++)
          text_printf("*"); 
        text_printf(" %s", temp->id); 
        
        rdp_print_parser_param_list(NULL, temp->params, 1); 
        
        text_printf(";\n"); 
      }
    }
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
  
  /* print parser definitions */
  rdp_print_parser_primaries(base); 
  
  /* print main line routine */
  text_printf("int main(int argc, char *argv[])\n"
  "{\n"
  "  clock_t rdp_finish_time, rdp_start_time = clock();\n"
  "  int\n"
  "    rdp_symbol_statistics = 0,    /* show symbol_ table statistics flag */\n"
  "    rdp_line_echo_all = 0,        /* make a listing on all passes flag */\n"
  "    rdp_filter = 0,               /* filter flag */\n"
  "    rdp_line_echo = 0;            /* make listing flag */\n\n"
  "  unsigned long rdp_textsize = %lul;   /* size of scanner text array */\n\n"
  "  unsigned long rdp_tabwidth = %lul;   /* tab expansion width */\n\n", 
  rdp_dir_text_size, rdp_dir_tab_width); 
  
  text_printf("  char* rdp_vcg_filename = NULL;      /* filename for -V option */\n\n"); 
  
  if (rdp_dir_tree)
    text_printf("  rdp_tree_data* rdp_tree = (rdp_tree_data*) graph_insert_graph(\"RDP derivation tree\");  /* hook for derivation tree */\n\n"
  ); 
  
  /* print help building code */
  text_printf("  arg_message(\"%s\\n\" RDP_STAMP \"\\n\\n\"", rdp_dir_title); 
  text_printf("\"Usage: %.*s [options] source", 
  (int) strcspn(outputfilename, "."), outputfilename); 
  
  if (* rdp_dir_suffix != 0)  /* skip suffix print if no suffix defined */
    text_printf("[.%s]", rdp_dir_suffix); 
  text_printf("\");\n\n"); 
  
  if (rdp_dir_args != NULL)
    rdp_print_parser_args(rdp_dir_args); 
  
  text_printf("\n  rdp_sourcefilename = *arg_process(argc, argv);\n\n"); 
  
  text_printf("  if (rdp_filter)\n"
  "{\n"
  "    rdp_sourcefilename = \"-\";\n"
  "    rdp_outputfilename = \"-\";\n\n"
  "}\n"); 
  
  text_printf("  if (rdp_sourcefilename == NULL)\n"
  "     arg_help(\"No source file specified\");\n\n"); 
  
  text_printf("  rdp_sourcefilename = text_default_filetype(rdp_sourcefilename, \"%s\");\n", 
  rdp_dir_suffix); 
  
  text_printf("  text_init(rdp_textsize, %u, %u, (int) rdp_tabwidth);\n"
  "  scan_init(%u, %u, %u, rdp_symbol_echo, rdp_tokens);\n", 
  rdp_dir_max_errors, rdp_dir_max_warnings, 
  rdp_dir_case_insensitive, rdp_dir_newline_visible, rdp_dir_show_skips); 
  
  /* Initialise any symbol tables */
  temp_table = rdp_dir_symbol_table; 
  while (temp_table != NULL)
  {
    text_printf("  %s = symbol_new_table(\"%s\", %u, %u, %s, %s, %s);\n", 
    temp_table->name, 
    temp_table->name, 
    temp_table->size, 
    temp_table->prime, 
    temp_table->compare, 
    temp_table->hash, 
    temp_table->print); 
    
    temp_table = temp_table->next; 
  }
  
  text_printf("  rdp_set_initialise();\n"
  "  rdp_load_keywords();\n"); 
  
  text_printf("  if (rdp_verbose)\n"
  "     text_printf(\"\\n%s\\n\" RDP_STAMP \"\\n\\n\");\n", rdp_dir_title); 
  
  if (rdp_dir_pre_parse != NULL)
    text_printf("  %s\n", rdp_dir_pre_parse); 
  
  text_printf("  for (rdp_pass = 1; rdp_pass <= RDP_PASSES; rdp_pass++)\n  {\n"); 
  
  if (rdp_dir_tree)
    text_printf("    rdp_tree_update = rdp_pass == RDP_PASSES;\n"); 
  
  text_printf(
  "    text_echo(rdp_line_echo_all || (rdp_line_echo && rdp_pass == RDP_PASSES));\n"
  "    if (text_open(rdp_sourcefilename) == NULL)\n"
  "      arg_help(\"unable to open source file\");\n\n"
  "    text_get_char();\n"
  "    scan_();\n\n"); 
  
  text_printf("    %s", rdp_start_prod->id); 
  
  rdp_print_parser_param_list(rdp_start_prod->id, rdp_start_prod->params, 0); 
  
  text_printf(
  ";            /* call parser at top level */\n"
  "    if (text_total_errors() != 0)\n"
  "      text_message(TEXT_FATAL, \"error%%s detected in source file\\n\", text_total_errors() == 1 ? \"\" : \"s\");   /* crash quietly */ \n"
  "  }\n"
  "  if (rdp_symbol_statistics)\n"
  "    symbol_print_all_table_statistics(11);\n\n"); 
  
  
  if (rdp_dir_tree)
  {
    if (!rdp_dir_epsilon_tree)
      text_printf("  scan_prune_tree((scan_data*) rdp_tree);\n"); 
    
    text_printf("  if (rdp_vcg_filename != NULL)\n"
    "  {\n"
    "    FILE *rdp_vcg_file;\n\n"
    "    if (*rdp_vcg_filename == \'\\0\')   /* No filename supplied */\n"
    "      rdp_vcg_filename = \"rdparser\";\n"
    "    rdp_vcg_file = fopen((rdp_vcg_filename = text_default_filetype(rdp_vcg_filename, \"vcg\")), \"w\");\n\n"
    "    if (rdp_vcg_file == NULL)\n"
    "      text_message(TEXT_FATAL, \"unable to open VCG file \'%%s\' for write\\n\", rdp_vcg_filename);\n\n"
    "    if (rdp_verbose)\n"
    "      text_message(TEXT_INFO, \"Dumping derivation tree to VCG file \'%%s\'\\n\", rdp_vcg_filename);\n\n"
    "    text_redirect(rdp_vcg_file);\n"
    "    graph_vcg(rdp_tree, NULL, scan_vcg_print_node, NULL);\n"
    "    text_redirect(stdout);\n"
    "  }\n\n"
    ); 
  }
  
  if (rdp_dir_post_parse != NULL)
    text_printf("  %s\n", rdp_dir_post_parse); 
  
  text_printf(
  "  text_print_total_errors();\n"
  "  if (rdp_verbose)\n"
  "  {\n"
  "    rdp_finish_time = clock();\n"
  "    text_message(TEXT_INFO, \"%%.3f CPU seconds used\\n\", ((double) (rdp_finish_time-rdp_start_time)) / CLOCKS_PER_SEC);\n"
  "  }\n", 
  (int) strcspn(outputfilename, "."), outputfilename); 
  
  text_printf("  return rdp_error_return;\n}\n"); 
  
  text_printf("\n/* End of %s */\n", 
  text_force_filetype(outputfilename, "c")); 
  
  text_redirect(stdout); 
}

void rdp_dump_extended(void * base)
{
  rdp_data * temp =(rdp_data *) symbol_next_symbol_in_scope(base); 
  
  if (rdp_verbose)
    text_printf("\n Expanded EBNF listing\n\n"); 
  while (temp != NULL)
  {
    rdp_list * list = temp->list; 
    int k = temp->kind; 
    unsigned count; 
    
    if (k != K_CODE && k != K_STRING && k != K_INTEGER && k != K_REAL)
    {
      text_printf(" "); 
      rdp_print_parser_production_name(temp); 
      
      rdp_print_parser_param_list(NULL, temp->params, 1); 
      
      text_printf(":%s", temp->return_type); 
      for (count = 0; count < temp->return_type_stars; count++)
        text_printf("*"); 
      text_printf(" ::= "); 
      
      if (k == K_TOKEN)
        text_printf("\'%s\'", temp->id); 
      else
      {
        if (k == K_LIST)
        {
          if (temp->lo == 0 && temp->hi == 0 && temp->supplementary_token == NULL)
            text_printf("{ "); 
          else if (temp->lo == 0 && temp->hi == 1 && temp->supplementary_token == NULL)
            text_printf("[ "); 
          else if (temp->lo == 1 && temp->hi == 0 && temp->supplementary_token == NULL)
            text_printf("< "); 
          else
            text_printf("( "); 
        }
        while (list != NULL)
        {
          rdp_print_parser_production_name(list->production); 
          rdp_print_parser_param_list(NULL, list->actuals, 0); 
          
          if (list->return_name != NULL)
            text_printf(":%s", list->return_name); 
          
          text_printf(" "); 
          list = list->next; 
          if (k != K_SEQUENCE && list != NULL)
            text_printf("| "); 
        }
        
        if (k == K_LIST)
        {
          if (temp->lo == 0 && temp->hi == 0 && temp->supplementary_token == NULL)
            text_printf("}"); 
          else if (temp->lo == 0 && temp->hi == 1 && temp->supplementary_token == NULL)
            text_printf("]"); 
          else if (temp->lo == 1 && temp->hi == 0 && temp->supplementary_token == NULL)
            text_printf(">"); 
          else if (temp->lo == 1 && temp->hi == 1 && temp->supplementary_token == NULL)
            text_printf(")"); 
          else
          {
            text_printf(")%lu@%lu", temp->lo, temp->hi); 
            if (temp->supplementary_token != NULL)
              text_printf(" \'%s\'", temp->supplementary_token->id); 
            else
              text_printf(" #"); 
          }
        }
      }
      
      text_printf(".\n"); 
      
      text_printf(" First set is {%s", temp->contains_null ? "(NULL) ": ""); 
      set_print_set(& temp->first, rdp_token_string, 78); 
      text_printf("}\n"); 
      
      text_printf(" Stop set is {"); 
      set_print_set(& temp->follow, rdp_token_string, 78); 
      text_printf("}\n"); 
      
      if (temp->call_count == 1)
        text_printf(" Production is called once\n\n"); 
      else
        text_printf(" Production is called %u times\n\n", temp->call_count); 
    }
    
    temp =(rdp_data *) symbol_next_symbol_in_scope(temp); 
  }
}

/* End of rdp_prnt.c */
