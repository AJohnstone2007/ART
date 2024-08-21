/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 21 March 2003
*
* gtb_rd.c - recursive descent parser functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <string.h>
#include <ctype.h>
#include "graph.h"
#include "memalloc.h"
#include "textio.h"
#include "gtb.h"
#include "gtb_lex.h"
#include "gtb_gram.h"
#include "gtb_scr.h"
#include <stdio.h>

static void print_symbol(char *prefix, gram_symbols_data *this_symbol, int alternate_number, int instance_number, char *suffix)
{
  char *temp = text_make_C_identifier(this_symbol->id);
  text_printf("%s%s", prefix, temp);

  if (alternate_number != 0)
    text_printf("__%i", alternate_number);

  if (instance_number != 0)
    text_printf("__%i", instance_number);

  text_printf("%s", suffix);
  mem_free(temp);
}

static void indent(unsigned indent_level)
{
  text_printf("\n");
  for (int i = 0; i < indent_level; i++)
    text_printf("  ");
}

static void print_code_production(gram_symbols_data *this_symbol, gram_edge *this_production_edge, unsigned production_count, unsigned indent_level)
{
  unsigned instance_count = 1;

  gram_edge* this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_production_edge));

  if (this_element_edge == NULL)
  {
    indent(indent_level); text_printf("/* epsilon */");
  }
  else
  {
    if (this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_TERMINAL)
    {
      indent(indent_level); text_printf("/* skip leading terminal */ i++;");
    }
    else
    {
      indent(indent_level); text_printf("/* call nonterminal %s */ ", this_element_edge->symbol_table_entry->id);
      print_symbol("c_u = gll_create(GLL_R_", this_symbol, production_count, instance_count, ", c_u, i); ");
      print_symbol("L = GLL_L_", this_element_edge->symbol_table_entry, 0, 0, "; break;");
      indent(indent_level - 1); print_symbol("case GLL_R_", this_symbol, production_count, instance_count, ":");
      instance_count++;
    }

    for (this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge));
         this_element_edge != NULL;
         this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
    {
      if (this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_TERMINAL)
      {
        indent(indent_level); text_printf("/* test for terminal %s */ ", this_element_edge->symbol_table_entry->id); print_symbol("if (I[i] == GLL_T_", this_element_edge->symbol_table_entry, 0, 0, ") i++; else { L = GLL_L_S_0; break; }");
      }
      else
      {
        indent(indent_level); print_symbol("if (set_includes_element(&gll_instance_first_", this_symbol, production_count, instance_count, ", I[i])) {");
        indent(indent_level + 1);
        text_printf("/* call nonterminal %s */ ", this_element_edge->symbol_table_entry->id);
        print_symbol("c_u = gll_create(GLL_R_", this_symbol, production_count, instance_count, ", c_u, i);");
        print_symbol("L = GLL_L_", this_element_edge->symbol_table_entry, 0, 0, "; break; }");
        indent(indent_level); text_printf("else { L = GLL_L_S_0; break; }");
        indent(indent_level - 1); print_symbol("case GLL_R_", this_symbol, production_count, instance_count, ":");
        instance_count++;
      }
    }
  }

  indent(indent_level); text_printf("/* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;\n");
}

void pred_gll_parse(grammar *this_gram, char *filename, char *str)
{
  char *parser_filename = text_force_filetype(filename, "cpp");
  FILE *parser_file = fopen((parser_filename), "w");

  if (parser_file == NULL)
    text_message(TEXT_FATAL, "unable to open GLL parser file '%s' for write\n", parser_filename);

  text_redirect(parser_file);

  text_printf("/******************************************************************************\n");
  text_printf("*\n");
  text_printf("* GLL parser generated ");
  text_print_time();
  text_printf(" by " GTB_VERSION " from %s\n*\n", rdp_sourcefilename);
  text_printf("******************************************************************************/\n\n");

  // Output includes
  text_printf("#include <stdlib.h>\n"
              "#include <string.h>\n"
              "#include \"set.h\"\n"
              "#include \"textio.h\"\n"
              "#include \"gtb_gll.h\"");

  // Output symbol enumeration
  text_printf("\nenum gll_symbol{ ");
  for (int this_symbol_number = 0; this_symbol_number < this_gram->first_level_0_slot; this_symbol_number++)
  {
    gram_symbols_data *this_symbol = gram_find_symbol_by_number(this_gram, this_symbol_number);

    if (this_symbol_number < this_gram->first_terminal)
      print_symbol("GLL_P_", this_symbol, 0, 0, ", ");
    else if (this_symbol_number < this_gram->first_nonterminal)
      print_symbol("GLL_T_", this_symbol, 0, 0, ", ");
    else
      print_symbol("GLL_N_", this_symbol, 0, 0,  ", ");
  }
  text_printf("};\n");

  // Output code label enumeration
  text_printf("\nenum gll_label { \n  GLL_L_ILLEGAL, GLL_L_S_0,\n  ");

  int return_label_count = 0;
  int alternate_label_count = 0;

  /* Pass one: output return labels */
  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
     this_symbol != NULL;
     this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
     )
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      int alternate_count = 1;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), alternate_count++)
      {
        int instance_count = 0;

        for (gram_edge *this_element_edge = ((gram_edge*) graph_next_out_edge(graph_destination(this_production_edge)));
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        {
          gram_symbols_data *this_element = this_element_edge->symbol_table_entry;

          if (this_element->name_space == SCRIPT_NS_NONTERMINAL)
          {
            print_symbol("GLL_R_", this_symbol, alternate_count, ++instance_count, ", ");
            return_label_count++;
          }

        }
      }
    }

  text_printf("\n  ");

  /* Pass two: output alternate labels */
  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
     this_symbol != NULL;
     this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
     )
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      int alternate_count = 0;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        print_symbol("GLL_L_", this_symbol, ++alternate_count, 0, ", ");
        alternate_label_count++;
      }
    }

  text_printf("\n  ");

  /* Pass three: output RHS labels */
  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
     this_symbol != NULL;
     this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
     )
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
      print_symbol("GLL_L_", this_symbol,  0, 0, ", ");

  text_printf(" };\nint i;");


  lex_init(str, this_gram);
  unsigned token_count = 0;
  unsigned token;

  do {
    token = lex_lex(); token_count++;
    if (token == GRAM_ILLEGAL_SYMBOL_NUMBER)
    {
      text_redirect(stdout);
      text_message(TEXT_FATAL, "illegal token found in input string to gll_parse");
    }
  } while (token != GRAM_EOS_SYMBOL_NUMBER);

  lex_init(str, this_gram);

  text_printf("\nconst unsigned m = %i;\ngll_symbol I[m] = { ", token_count);

  do {
    token = lex_lex();
    text_printf("(gll_symbol)%i, ", token);
  } while (token != GRAM_EOS_SYMBOL_NUMBER && token != GRAM_ILLEGAL_SYMBOL_NUMBER);

  text_printf("};\n");

  // Output set declarations
  print_symbol("\nstatic set_ gll_first_", this_gram->start_rule, 0, 0, " = SET_NULL;");

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
     this_symbol != NULL;
     this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
     )
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      int alternate_count = 0;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        print_symbol("\nstatic set_ gll_first_", this_symbol, ++alternate_count, 0, " = SET_NULL;");

        int instance_count = 1;

        for (gram_edge *this_element_edge = ((gram_edge*) graph_next_out_edge(graph_destination(this_production_edge)));
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        {
          gram_symbols_data *this_element = this_element_edge->symbol_table_entry;

          if (this_element->name_space == SCRIPT_NS_NONTERMINAL)
            print_symbol("\nstatic set_ gll_instance_first_", this_symbol, alternate_count, instance_count++, " = SET_NULL;");
        }
      }
    }
  text_printf("\n\n");

  // Output set initialiser
  text_printf("static void gll_set_init() {");

  print_symbol("\n  set_assign_list(&gll_first_", this_gram->start_rule, 0, 0, "");
  if (set_includes_element(&this_gram->start_rule->first, GRAM_EPSILON_SYMBOL_NUMBER))
    text_printf(", GLL_P_DOLLAR");   /* Special case: epsilon in first(start) means follow is $ */

  for (int terminal = this_gram->first_terminal; terminal < this_gram->first_nonterminal; terminal++)
    if (set_includes_element(&this_gram->start_rule->first, terminal))
      print_symbol(", GLL_T_", gram_find_symbol_by_number(this_gram, terminal), 0, 0, "");
    text_printf(", SET_END);");

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
     this_symbol != NULL;
     this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
     )
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      int production_count = 0;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), production_count++)
      {
        set_ working_set = SET_NULL;

        set_assign_set(&working_set, &this_symbol->follow);
        gram_unwrap_nonterminal_follow_set(this_gram, &working_set);

        if (set_includes_element(&this_symbol->selectors[production_count], GRAM_EPSILON_SYMBOL_NUMBER))
          set_unite_set(&working_set, &this_symbol->selectors[production_count]);
        else
          set_assign_set(&working_set, &this_symbol->selectors[production_count]);

        print_symbol("\n  set_assign_list(&gll_first_", this_symbol, production_count + 1, 0, "");

        if (set_includes_element(&working_set, GRAM_EOS_SYMBOL_NUMBER))
          print_symbol(", GLL_P_", gram_find_symbol_by_number(this_gram, GRAM_EOS_SYMBOL_NUMBER), 0, 0, "");

        for (int terminal = this_gram->first_terminal; terminal < this_gram->first_nonterminal; terminal++)
          if (set_includes_element(&working_set, terminal))
            print_symbol(", GLL_T_", gram_find_symbol_by_number(this_gram, terminal), 0, 0, "");

        set_free(&working_set);

        text_printf(", SET_END);");

        int instance_count = 1;

        for (gram_edge *this_element_edge = ((gram_edge*) graph_next_out_edge(graph_destination(this_production_edge)));
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        {
          gram_symbols_data *this_element = this_element_edge->symbol_table_entry;

          set_ working_first_set = SET_NULL;
          set_ working_follow_set = SET_NULL;

          if (this_element->name_space == SCRIPT_NS_NONTERMINAL)
          {
            set_assign_set(&working_follow_set, &this_symbol->immediate_instance_follow[this_element_edge->instance_number]);

            if (set_includes_element(&working_follow_set, GRAM_EPSILON_SYMBOL_NUMBER))
              set_unite_set(&working_follow_set, &this_symbol->follow);

            gram_unwrap_nonterminal_follow_set(this_gram, &working_follow_set);

            set_assign_set(&working_first_set, &this_element->first);

            if (set_includes_element(&working_first_set, GRAM_EPSILON_SYMBOL_NUMBER))
              set_unite_set(&working_first_set, &working_follow_set);

            print_symbol("\n  set_assign_list(&gll_instance_first_", this_symbol, production_count + 1, instance_count++, "");

            if (set_includes_element(&working_first_set, GRAM_EOS_SYMBOL_NUMBER))
              print_symbol(", GLL_P_", gram_find_symbol_by_number(this_gram, GRAM_EOS_SYMBOL_NUMBER), 0, 0, "");

            for (int terminal = this_gram->first_terminal; terminal < this_gram->first_nonterminal; terminal++)
              if (set_includes_element(&working_first_set, terminal))
                print_symbol(", GLL_T_", gram_find_symbol_by_number(this_gram, terminal), 0, 0, "");
            text_printf(", SET_END);");
          }

          set_free(&working_first_set);
          set_free(&working_follow_set);
        }
      }
    }
  text_printf("\n}\n\n");

  // Output recogniser function
  text_printf("static void gll_recognise() {\n"
              "  gss_node* c_u;\n"
              "  i = 0;\n"
              "  enum gll_label L = ");

              print_symbol("GLL_L_", this_gram->start_rule, 0, 0, ";\n");

  print_symbol("\n  if (!set_includes_element(&gll_first_", this_gram->start_rule, 0, 0, ", I[0])) { text_printf(\"Reject\"); exit(1); }\n");

  text_printf("\n  c_u = gll_init(GLL_L_S_0, %i, %i);\n"
              "  while (1)\n"
              "    switch(L){\n"
              "      case GLL_L_S_0:\n"
              "        if (R_cardinality != 0) { gll_remove(&L, &c_u, &i); break; } else gll_print_accept_or_reject();\n", return_label_count, alternate_label_count);

  for (gram_symbols_data *this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
     this_symbol != NULL;
     this_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_symbol)
     )
    if (this_symbol->name_space == SCRIPT_NS_NONTERMINAL)
    {
      print_symbol("\n /* ----------------------------------------------- */\n\n      case GLL_L_", this_symbol,  0, 0, ":");

      //Walk the productions to generate the LHS_x code fragment
      int production_count = 1;

      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), production_count++)
      {
        indent(4);
        if (production_count != 1 && !this_symbol->ll1_nondetermined)
          text_printf("else ");

        print_symbol("if (set_includes_element(&gll_first_", this_symbol, production_count, 0, ", I[i]))");

        if (this_symbol->ll1_nondetermined)
          print_symbol(" gll_add(GLL_L_", this_symbol, production_count, 0, ", c_u, i);");
        else
          print_symbol("{ L = GLL_L_", this_symbol, production_count, 0, "; break; }");
      }

      indent(4);

      if (this_symbol->ll1_nondetermined)
        text_printf("L = GLL_L_S_0; break;");
      else
        text_printf("text_printf(\"Fatal internal error: LL(1) rule reached end of body\"); exit (1);");

      text_printf("\n");

      //Now walk again to generate the RHS_x fragments for non-LL(1) nonterminals only
      production_count = 1;
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_symbol->rule_tree);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge), production_count++)
      {
        print_symbol("\n      case GLL_L_", this_symbol,  production_count, 0, ":");

        print_code_production(this_symbol, this_production_edge, production_count, 4);
      }
    }

  text_printf("\n      default: text_printf(\"Fatal internal error: unknown label\"); exit (1);\n"
              "    };\n");
  text_printf("}\n\n");

  //Output main
  text_printf("int main() {\n"
              "  gll_set_init();\n"
              "  gll_recognise();\n"
              "}\n"
             );

  text_redirect(stdout);
  fclose(parser_file);
}

