/*****************************************************************************
*
* GTB release 2.5 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 25 Sep 2002
*
* gtb_gdg.c - Grammar Dependency Graph functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#include <stdlib.h>
#include <math.h>
#include "graph.h"
#include "hist.h"
#include "gtb.h"
#include "gtb_scr.h"
#include "gtb_gram.h"
#include "gtb_gdg.h"
#include "gtb_gen.h"
#include "gtb_pred.h"
#include "gtb_nfa.h"
#include "gtb_dfa.h"
#include "gtb_sr.h"
#include "gtb_gp.h"
#include "util.h"

//#define GDG_SUPPRESS_SELF_LOOPS
//#define GDG_TRACE 1
//#define GDG_ANALYSE_BU_TRACE 1
//#define COMPRESS_TRACE

static gdg *current_gdg;
static gdg_node **gdg_nodes;

gdg* gdg_construct_gdg(grammar * this_gram)
{
  unsigned l_count = 0;
  unsigned L_count = 0;
  unsigned r_count = 0;
  unsigned R_count = 0;

  current_gdg = (gdg*) mem_calloc(1, sizeof(gdg));

  gdg_nodes = (gdg_node**) mem_calloc(this_gram->first_level_0_slot, sizeof(gdg_node*));
  set_ temp_set = SET_NULL;

  current_gdg->gdg_nodes = graph_insert_graph("Grammar dependency graph");

  current_gdg->grammar = gram_copy_gram(this_gram);

  set_ not_a_terminal_set = SET_NULL;

  set_assign_list(&not_a_terminal_set, GRAM_EPSILON_SYMBOL_NUMBER, GRAM_EOS_SYMBOL_NUMBER, SET_END);


  /* Make a GDG node for each rule and link it into the symbol table */
  /* Iterate over symbols */
  for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_rule_symbol != NULL;
       this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
  {
    gram_node *this_rule = this_rule_symbol->rule_tree;

    if (this_rule != NULL)  /* Skip terminals, etc, which have no associated rule tree */
    {
      set_unite_element(&not_a_terminal_set, this_rule_symbol->symbol_number);

      gdg_node *this_gdg_node = (gdg_node *) graph_insert_node(sizeof(gdg_node), current_gdg->gdg_nodes);

      this_gdg_node->symbol_table_entry = this_rule_symbol;
      gdg_nodes[this_gdg_node->symbol_table_entry->symbol_number] = this_gdg_node;

      if (this_rule_symbol == this_gram->start_rule)
        graph_set_root(current_gdg->gdg_nodes, this_gdg_node);
    }
  }

  /* Now iterate over rules, adding edges */
  /* Iterate over symbols */
  for (gram_symbols_data *this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(this_gram->symbol_table));
       this_rule_symbol != NULL;
       this_rule_symbol = (gram_symbols_data*) symbol_next_symbol_in_scope(this_rule_symbol))
  {
    gram_node *this_rule = this_rule_symbol->rule_tree;
    unsigned instance_count = 0;

    if (this_rule != NULL)
    {
      for (gram_edge *this_production_edge = (gram_edge*) graph_next_out_edge(this_rule);
           this_production_edge != NULL;
           this_production_edge = (gram_edge*) graph_next_out_edge(this_production_edge))
      {
        int seen_left_context = 0;
        int seen_left_epsilon_context = 1;

        for (gram_edge *this_element_edge = (gram_edge *) graph_next_out_edge(graph_destination(this_production_edge));
             this_element_edge != NULL;
             this_element_edge = (gram_edge*) graph_next_out_edge(graph_destination(this_element_edge)))
        {
          if (this_element_edge->symbol_table_entry->name_space == SCRIPT_NS_NONTERMINAL)
          {
            gdg_node *source_node = gdg_nodes[this_rule_symbol->symbol_number];
            gdg_node *destination_node = gdg_nodes[this_element_edge->symbol_table_entry->symbol_number];

#if defined(GDG_SUPPRESS_SELF_LOOPS)
            if (source_node == destination_node)
              continue;
#endif

            gdg_edge *this_gdg_edge;

            // Find a GDG edge from source to destination, or add one
            for (this_gdg_edge = (gdg_edge*) graph_next_out_edge(source_node);
                 graph_destination(this_gdg_edge) != destination_node && this_gdg_edge != NULL;
                 this_gdg_edge = (gdg_edge*) graph_next_out_edge(this_gdg_edge))
              ;

/* If you want the multigraph version, omment out next line !! */
            if (this_gdg_edge == NULL)
              this_gdg_edge = (gdg_edge*) graph_insert_edge(sizeof(gdg_edge), destination_node, source_node);

            if (graph_source(this_gdg_edge) == graph_destination(this_gdg_edge))
              set_unite_element(&(current_gdg->self_loop_edges), graph_atom_number(this_gdg_edge));

            if (seen_left_context)
            {
              this_gdg_edge->left_context |= 1;
              L_count++;
            }

            if (seen_left_epsilon_context)
            {
              this_gdg_edge->left_epsilon_context |= 1;
              l_count++;
            }

            set_assign_set(&temp_set, &this_rule_symbol->immediate_instance_follow[instance_count]);
            gram_unwrap_nonterminal_follow_set(this_gram, &temp_set);
//#define OLD_LR_RULES 1
#if defined(OLD_LR_RULES)
            set_difference_set(&temp_set, &not_a_terminal_set);
#else
            set_difference_element(&temp_set, GRAM_EOS_SYMBOL_NUMBER);
            set_difference_element(&temp_set, GRAM_EPSILON_SYMBOL_NUMBER);
#endif
            if (set_cardinality(&temp_set) > 0)
            {
              this_gdg_edge->right_context |= 1;
              R_count++;
            }

            if (set_includes_element(&this_rule_symbol->immediate_instance_follow[instance_count], GRAM_EPSILON_SYMBOL_NUMBER))
            {
              this_gdg_edge->right_epsilon_context |= 1;
              r_count++;
            }
            instance_count++;
          }

          //Watch out for epsilons!
          if (!set_includes_element(&this_element_edge->symbol_table_entry->first, GRAM_EPSILON_SYMBOL_NUMBER))
            seen_left_epsilon_context =0;

//Two versions here: one disallows A::=#, the second allows it. Liz thinks second is correct
#if defined(OLD_LR_RULES)
          set_assign_set(&temp_set, &this_element_edge->symbol_table_entry->first);
          set_difference_set(&temp_set, &not_a_terminal_set);
          if (set_cardinality(&temp_set) > 0)
            seen_left_context |= 1;
#else
          if (set_cardinality(&this_element_edge->symbol_table_entry->first) > 0)
            seen_left_context |= 1;
#endif

          if (!set_includes_element(&this_element_edge->symbol_table_entry->first, GRAM_EPSILON_SYMBOL_NUMBER))
            seen_left_epsilon_context =0;
        }
      }
    }
  }

  graph_create_node_index(current_gdg->gdg_nodes);
  graph_create_edge_index(current_gdg->gdg_nodes);
  if (script_gtb_verbose())
    text_printf("GDG has %u nodes and %u edges of which %u l, %u L, %u r and %u R\n", graph_max_node_number(current_gdg->gdg_nodes),graph_max_edge_number(current_gdg->gdg_nodes),l_count, L_count, r_count, R_count);
  return current_gdg;
}

static GRAPH_ATOM_NUMBER_T *gdg_levels;

static void vcg_print_gdg_node(const void *node)
{
  gdg_node *this_node = (gdg_node *) node;
  char *colour;

  if (current_gdg->strong_number != NULL)
  {
    if (current_gdg->strong_number[graph_atom_number(this_node)] < 0)
    {
      text_printf("borderwidth:2 label:\"%u: %s\"",
                  graph_atom_number(node),
                  this_node->symbol_table_entry->id);
    }
    else
    {
      if (current_gdg->strong_number[graph_atom_number(this_node)] == 0)
      {
        colour = "color:pink";
      }
      else
      switch (current_gdg->strong_number[graph_atom_number(this_node)] % 6)
      {
        case 1: colour = "color:red"; break;
        case 2: colour = "color:green"; break;
        case 3: colour = "color:blue"; break;
        case 4: colour = "color:cyan"; break;
        case 5: colour = "color:magenta"; break;
        case 0: colour = "color:yellow"; break;
      }
    }
  }
  else
    colour = "";

  text_printf("borderwidth:2%s level:%u label:\"%u: %s\"",
              colour,
              gdg_levels[graph_atom_number(this_node)]*2,
              graph_atom_number(node),
              this_node->symbol_table_entry->id);
}

static void vcg_print_gdg_graph(const void *graph)
{
  text_printf("layoutalgorithm:dfs");
}

static void vcg_print_gdg_edge(const void *edge)
{
  gdg_edge *this_edge = (gdg_edge *) edge;

  if (current_gdg->strong_number != NULL)
  {
    if (current_gdg->strong_number[graph_atom_number(graph_source(edge)) ] ==0 || current_gdg->strong_number[graph_atom_number(graph_destination(edge))] == 0)
      text_printf("class:3");
    else if (current_gdg->strong_number[graph_atom_number(graph_source(edge))] != current_gdg->strong_number[graph_atom_number(graph_destination(edge))])
      text_printf("class:2");
  }

  if (current_gdg->region_number != NULL)
  {
  char *colour = "";
    if (current_gdg->region_number[graph_atom_number(this_edge)] != 0)
    {
      switch (current_gdg->region_number[graph_atom_number(this_edge)] % 6)
      {
        case 1: colour = "color:red"; break;
        case 2: colour = "color:green"; break;
        case 3: colour = "color:blue"; break;
        case 4: colour = "color:yellow"; break;
        case 5: colour = "color:cyan"; break;
        case 0: colour = "color:magenta"; break;
      }
    }

    text_printf("%s", colour);
  }
  else
  if (set_includes_element(&(current_gdg->self_loop_edges), graph_atom_number(edge)))
    text_printf("color: blue");

  if (this_edge->compression_factor != 0)
  {
    text_printf("thickness:4 label:\"%u:%s%s%s%s@%u\"",
                graph_atom_number(this_edge),
                this_edge->left_epsilon_context? "l" : "",
                this_edge->left_context? "L" : "",
                this_edge->right_epsilon_context? "r" : "",
                this_edge->right_context? "R" : "",
                this_edge->compression_factor);
  }
  else
    text_printf("label:\"%u:%s%s%s%s\"",
              graph_atom_number(this_edge),
              this_edge->left_epsilon_context? "l" : "",
              this_edge->left_context? "L" : "",
              this_edge->right_epsilon_context? "r" : "",
              this_edge->right_context? "R" : "");

}

void gdg_write(FILE *output_file, gdg *this_gdg)
{
  current_gdg = this_gdg;
  gdg_levels = graph_level(this_gdg->gdg_nodes, 1);

  text_redirect(output_file);
  text_message(TEXT_WARNING, "write[] on a GDG produces no output: did you mean render[]?\n");
  text_redirect(stdout);

  mem_free(gdg_levels);
}

void gdg_render(FILE *output_file, gdg *this_gdg)
{
  current_gdg = this_gdg;
  gdg_levels = graph_level(this_gdg->gdg_nodes, 1);

  text_redirect(output_file);
  graph_vcg(this_gdg->gdg_nodes, vcg_print_gdg_graph, vcg_print_gdg_node, vcg_print_gdg_edge);
  text_redirect(stdout);

  mem_free(gdg_levels);
}

gdg *gdg_copy(gdg* this_gdg)
{
  gdg *new_gdg = (gdg*) mem_calloc(1, sizeof(gdg));

  gdg_nodes = (gdg_node**) mem_calloc(this_gdg->grammar->first_level_0_slot - 1, sizeof(gdg_node*));

  new_gdg->gdg_nodes = graph_insert_graph("Compressed grammar dependency graph");

  new_gdg->grammar = gram_copy_gram(this_gdg->grammar);

  set_assign_set(&(new_gdg->self_loop_edges), &(this_gdg->self_loop_edges));

  //Now make a new set of nodes from the old GDG's node table
  unsigned gdg_node_count = graph_max_node_number(this_gdg->gdg_nodes);

  for (unsigned count = 0; count <= gdg_node_count; count++)
  {
    if (graph_node_index(this_gdg->gdg_nodes)[count] == NULL)
      continue;

    gdg_node *this_gdg_node = (gdg_node *) graph_insert_node(sizeof(gdg_node), new_gdg->gdg_nodes);

    graph_set_atom_number(this_gdg_node, count);

    this_gdg_node->symbol_table_entry = ((gdg_node*) graph_node_index(this_gdg->gdg_nodes)[count])->symbol_table_entry;
  }

  graph_create_node_index(new_gdg->gdg_nodes);

  unsigned old_root_number = graph_atom_number(graph_root(this_gdg->gdg_nodes));

  graph_set_root(new_gdg->gdg_nodes, graph_node_index(new_gdg->gdg_nodes)[old_root_number]);

  // Now lets do the edges
  for (unsigned count = 0; count <= gdg_node_count; count++)
  {
    void *old_source_node;

    if ((old_source_node = graph_node_index(this_gdg->gdg_nodes)[count]) == NULL)
      continue;

    for (void *this_edge = graph_next_out_edge(old_source_node); this_edge != NULL; this_edge = graph_next_out_edge(this_edge))
    {
      void *old_destination_node = graph_destination(this_edge);

      void *new_source_node = graph_node_index(new_gdg->gdg_nodes)[graph_atom_number(old_source_node)];
      void *new_destination_node = graph_node_index(new_gdg->gdg_nodes)[graph_atom_number(old_destination_node)];

      void *new_edge = graph_insert_edge_after_final(sizeof (gdg_edge), new_destination_node, new_source_node);

      memcpy(new_edge, this_edge, sizeof(gdg_edge));

      graph_set_atom_number(new_edge, graph_atom_number(this_edge));
    }
  }

  graph_create_edge_index(new_gdg->gdg_nodes);

  return new_gdg;
}

gdg* render_gdg;

void gdg_print_tarjan_node_symbol(const void * symbol)
{
  graph_scc_node_table_data *this_symbol = (graph_scc_node_table_data*) symbol;

  if (symbol == NULL)
    text_printf("Null symbol");
  else
  {
    text_printf("{");
    unsigned *elements = set_array(&this_symbol->id);

    for (unsigned *this_element = elements; *this_element != SET_END; this_element++)
      text_printf("%s%s", ((gdg_node*)graph_node_index(render_gdg->gdg_nodes)[*this_element])->symbol_table_entry->id, *(this_element+1) != SET_END ? ", ":"");

    mem_free(elements);
    text_printf("} has edges ");

    for (unsigned *edge_number = this_symbol->edges; *edge_number != SET_END; edge_number++)
      text_printf("%u%s", *edge_number, *(edge_number+1) != SET_END ? "," : "");

    if (this_symbol->has_children)
      text_printf(" (internal)\n");
    else
      text_printf(" (leaf)\n");
  }
}


struct gdg_analysis_block {unsigned *edge_array; struct gdg_analysis_block *next;};
void gdg_analyse_one_graph(gdg* gdg, set_ *tilde_schema);

void gdg_analyse_gdg_chain(gdg_analysis_block *leaves, gdg* this_gdg, set_ *tilde_schema)
{
  if (leaves == NULL)
  {
    text_printf("full tilde schema {");
    set_print_set(tilde_schema, NULL, 0);
    text_printf("}\n");
    gdg_analyse_one_graph(this_gdg, tilde_schema);
    return;
  }

  set_ new_tilde = SET_NULL;

  for (unsigned *suppress_edge_number = leaves->edge_array; *suppress_edge_number != SET_END; suppress_edge_number ++)
  {
    text_printf("partial tilde schema {");
    set_print_set(tilde_schema, NULL, 0);
    text_printf("}\n");

    set_assign_set(&new_tilde, tilde_schema);
    set_unite_element(&new_tilde, *suppress_edge_number);
    gdg_analyse_gdg_chain(leaves->next, this_gdg, &new_tilde);
  }
  set_free(&new_tilde);
}

void gdg_analyse_one_graph(gdg* gdg, set_ *tilde_schema)
{
  static int level = 0;

  level++;

  text_printf("Entering level %u with partial tilde schema {", level);
  set_print_set(tilde_schema, NULL, 0);
  text_printf("}\n");

  void* node_table = symbol_new_table("scc_node_table", 999983, 999979, symbol_compare_set, symbol_hash_set, gdg_print_tarjan_node_symbol);

  set_ suppress_edges = SET_NULL;

  set_assign_set(&suppress_edges, tilde_schema);
  set_unite_set(&suppress_edges, &gdg->self_loop_edges);
  gdg->strong_number = graph_tarjan_scc(gdg->gdg_nodes, &suppress_edges, 0, node_table);
  set_free(&suppress_edges);

#if 1
  for (int count = 0; count< graph_max_node_number(gdg->gdg_nodes); count++)
    text_printf("** %u %i\n", count, gdg->strong_number[count]);
#endif

  //Now find leaves
  for (graph_scc_node_table_data *child_node = (graph_scc_node_table_data *) symbol_next_symbol_in_scope(symbol_get_scope(node_table));
       child_node != NULL;
       child_node = (graph_scc_node_table_data *) symbol_next_symbol_in_scope(child_node))
    for (graph_scc_node_table_data *parent_node = (graph_scc_node_table_data *) symbol_next_symbol_in_scope(symbol_get_scope(node_table));
         parent_node != NULL;
         parent_node = (graph_scc_node_table_data *) symbol_next_symbol_in_scope(parent_node))
      if (child_node != parent_node && set_includes_set(&parent_node->id, &child_node->id))
        parent_node->has_children = 1;

  text_printf("Top level SCC analysis\n");
  symbol_print_table(node_table);

  // And now bring the leaves together into a linked list of edge blocks
  gdg_analysis_block *leaves = NULL;
  for (graph_scc_node_table_data *this_node = (graph_scc_node_table_data *) symbol_next_symbol_in_scope(symbol_get_scope(node_table));
       this_node != NULL;
       this_node = (graph_scc_node_table_data *) symbol_next_symbol_in_scope(this_node))
    if (!this_node->has_children)
    {
      gdg_analysis_block *temp = (gdg_analysis_block*) mem_calloc(1, sizeof(gdg_analysis_block));

      temp->next = leaves;
      leaves = temp;
      temp->edge_array = this_node->edges;
    }

#if 0
  if (leaves!!!!!!
#endif
  // Now we've finished with the node table: change this to a deep-ish copy to get rid of the SCC regions
  symbol_free_table(node_table);

  text_printf("Exiting level %u\n", level);
  level--;
}

gdg *gdg_analyse_gdg(gdg * this_gdg)
{
  gdg *analysed_gdg = gdg_copy(this_gdg);
  render_gdg = analysed_gdg;

  // Top level: self loops
  set_ tilde_schema = SET_NULL;

  for (gdg_node *this_node = (gdg_node*) graph_next_node(analysed_gdg->gdg_nodes); this_node != NULL; this_node = (gdg_node*) graph_next_node(this_node))
    for (gdg_edge *this_edge = (gdg_edge*) graph_next_out_edge(this_node); this_edge != NULL; this_edge = (gdg_edge*) graph_next_out_edge(this_edge))
      if (graph_source(this_edge) == graph_destination(this_edge))
      {
#if 1
        text_printf("Self loop %u:%s%s%s%s\n",
                    graph_atom_number(this_edge),
                    this_edge->left_epsilon_context? "l" : "",
                    this_edge->left_context? "L" : "",
                    this_edge->right_epsilon_context? "r" : "",
                    this_edge->right_context? "R" : "");
#endif
        if (this_edge->left_context)
        {
          text_printf("L self loop at node %s: delete edge %u\n", this_node->symbol_table_entry->id, graph_atom_number(this_edge));

          set_unite_element(&tilde_schema, graph_atom_number(this_edge));
        }
      }

      gdg_analysis_block *leaves = (gdg_analysis_block*) mem_calloc(1, sizeof(gdg_analysis_block));
      set_ edges = SET_NULL;
      set_fill(&edges, graph_max_edge_number(analysed_gdg->gdg_nodes));
      set_difference_element(&edges, 0);
      leaves->edge_array = set_array(&edges);
      set_free(&edges);

      gdg_analyse_gdg_chain(leaves, analysed_gdg, &tilde_schema);

  return analysed_gdg;
}


gdg *gdg_compress_gdg(gdg * this_gdg, unsigned recursion_level)
{
  gdg *compressed_gdg = gdg_copy(this_gdg);

  int changed = true;
  unsigned iterations = 0;

  while (changed)
  {
    changed = false;
    iterations++;

  #if defined(COMPRESS_TRACE)
    text_printf("\n** Starting compression of simple sequences on iteration %u\n", iterations);
  #endif
    int go_on = true;

    while (go_on)
    {
      go_on = false;

      for (void *this_node = graph_next_node(compressed_gdg->gdg_nodes); this_node != NULL; this_node = graph_next_node(this_node))
        if (graph_in_degree(this_node) == 1 && graph_out_degree(this_node) == 1)
        {
  #if defined(COMPRESS_TRACE)
          text_printf("Node %u has in-degree = out-degree = 1\n", graph_atom_number(this_node));
  #endif

          gdg_edge *in_edge = (gdg_edge*) graph_next_in_edge(this_node);
          gdg_edge *out_edge = (gdg_edge*) graph_next_out_edge(this_node);
          void *source_node = graph_source(in_edge);
          void *destination_node = graph_destination(out_edge);

  #if defined(COMPRESS_TRACE)
          text_printf("Node %u has parent node %u via edge %u and child node %u via edge %u\n",
                      graph_atom_number(this_node),
                      graph_atom_number(source_node),
                      graph_atom_number(in_edge),
                      graph_atom_number(destination_node),
                      graph_atom_number(out_edge)
                     );
  #endif

          if (source_node == destination_node && this_node == destination_node)
          {
  #if defined(COMPRESS_TRACE)
            text_printf("Node %u is isolated self-loop: no compression performed\n",
                        graph_atom_number(this_node));
  #endif
            continue;
          }

          gdg_edge *new_edge = (gdg_edge*) graph_insert_edge(sizeof(gdg_edge), destination_node, source_node);

          new_edge->left_context = in_edge->left_context || out_edge->left_context;
          new_edge->left_epsilon_context = in_edge->left_epsilon_context && out_edge->left_epsilon_context;

          new_edge->right_context = in_edge->right_context || out_edge->right_context;
          new_edge->right_epsilon_context = in_edge->right_epsilon_context && out_edge->right_epsilon_context;

          // Set compression factors to something meaningful
          if (in_edge->compression_factor == 0)
            in_edge->compression_factor = 1;

          if (out_edge->compression_factor == 0)
            out_edge->compression_factor = 1;

          new_edge->compression_factor = min(in_edge->compression_factor, out_edge->compression_factor);

          if (graph_source(new_edge) == graph_destination(new_edge))
          {
            set_unite_element(&(compressed_gdg->self_loop_edges), graph_atom_number(new_edge));
  #if defined(COMPRESS_TRACE)
            text_printf("New edge %u is self-loop\n",
                        graph_atom_number(new_edge));
  #endif
          }


  #if defined(COMPRESS_TRACE)
            text_printf("Removed node %u\n",
                        graph_atom_number(this_node));
  #endif

          graph_delete_node(this_node);
          changed = true;

          go_on = true;

          break;
        }

    }

     // And now do multigraph compression
  #if defined(COMPRESS_TRACE)
    text_printf("Starting compression of multi-edges on iteration %u\n", iterations);
  #endif

    for (void *this_node = graph_next_node(compressed_gdg->gdg_nodes); this_node != NULL; this_node = graph_next_node(this_node))
    {
  #if defined(COMPRESS_TRACE)
    text_printf("Checking node %u\n", graph_atom_number(this_node));
  #endif
      for (gdg_edge *edge_a = (gdg_edge*) graph_next_out_edge(this_node); edge_a != NULL; edge_a = (gdg_edge*) graph_next_out_edge(edge_a))
      {
        void *destination = graph_destination(edge_a);

  #if defined(COMPRESS_TRACE)
    text_printf("Checking primary edge %u\n", graph_atom_number(edge_a));
  #endif

        restart:

        for (gdg_edge *edge_b = (gdg_edge*) graph_next_out_edge(edge_a); edge_b != NULL; edge_b = (gdg_edge*) graph_next_out_edge(edge_b))
        {
  #if defined(COMPRESS_TRACE)
    text_printf("Checking secondary edge %u\n", graph_atom_number(edge_b));
  #endif
          if (graph_destination(edge_b) == destination)
          {
            edge_a->left_context = edge_a->left_context || edge_b->left_context;
            edge_a->left_epsilon_context = edge_a->left_epsilon_context || edge_b->left_epsilon_context;

            edge_a->right_context = edge_a->right_context || edge_b->right_context;
            edge_a->right_epsilon_context = edge_a->right_epsilon_context || edge_b->right_epsilon_context;

            if (edge_a->compression_factor == 0)
              edge_a->compression_factor = 1;
            else
              edge_a->compression_factor++;

  #if defined(COMPRESS_TRACE)
    text_printf("Deleting secondary edge %u\n", graph_atom_number(edge_b));
  #endif

            graph_delete_edge(edge_b);
            changed = true;

            goto restart;
          }
        }
      }
    }

    if (recursion_level != 0)
    {
      if (iterations >= recursion_level)
        break;
    }
  }

  //Update the indices
  graph_create_node_index(compressed_gdg->gdg_nodes);
  graph_create_edge_index(compressed_gdg->gdg_nodes);

  return compressed_gdg;
}

/* Recursion analysis of GDG's via the bottom up method */

static set_ gdg_edge_visited = SET_NULL;
static set_ gdg_node_visited = SET_NULL;
static set_ *gdg_valid_nodes;
static gdg_node *gdg_start_node;
static set_ gdg_diff_set = SET_NULL;
static set_ gdg_temp_set = SET_NULL;
static void *gdg_cycle_table;
static void *gdg_break_table;
static bool gdg_cardinality_limit_triggered;
#if defined(GDG_ANALYSE_BU_TRACE)
static unsigned cycles;
#endif
static long unsigned total_cycles;
static gdg *analyse_gdg;

struct break_data
{
  set_ id;
  unsigned cardinality;
};

int gdg_print_id_for_node_atom_number_callback(const unsigned this_node_number)
{
  return text_printf("%s", ((gdg_node*) graph_node_index(analyse_gdg->gdg_nodes)[this_node_number])->symbol_table_entry->id);
}

int gdg_print_id_for_source_of_edge_atom_number_callback(const unsigned this_node_number)
{
  return text_printf("%s", ((gdg_node*) graph_source(graph_edge_index(analyse_gdg->gdg_nodes)[this_node_number]))->symbol_table_entry->id);
}

void gdg_analyse_gdg_bu_rec(gdg_edge* this_edge)
{
  gdg_node* this_node = (gdg_node*) graph_destination(this_edge);

  if (!set_includes_element(gdg_valid_nodes, graph_atom_number(this_node)))
    return;

  unsigned edge_atom_number = graph_atom_number(this_edge);

  if (!set_includes_element(&gdg_edge_visited, edge_atom_number))
  {
    set_unite_element(&gdg_edge_visited, edge_atom_number);

    if (this_node == gdg_start_node)
    {
#if defined(GDG_ANALYSE_BU_TRACE)
      text_printf("** cycle:\n   Edges: ");
      set_print_set(&gdg_edge_visited, NULL, 0);
      text_printf("\n   Nodes: ");
      set_print_set_callback(&gdg_node_visited, gdg_print_id_for_node_atom_number_callback, 0);
      text_printf("\n");
      cycles++;
#endif

      if (symbol_lookup_key(gdg_cycle_table, &gdg_edge_visited, NULL) != NULL)
      {
#if defined(GDG_ANALYSE_BU_TRACE)
        text_printf("Old\n");
#endif
      }
      else
      {
        set_ temp = SET_NULL;
        set_assign_set_normalise(&temp, &gdg_edge_visited);

        symbol_insert_key(gdg_cycle_table, &temp, sizeof (set_), sizeof (gdg_cycle_data));
#if defined(GDG_ANALYSE_BU_TRACE)
        text_printf("New\n");
#endif
      }
    }
    else
    {
      unsigned node_atom_number = graph_atom_number(this_node);

      if (!set_includes_element(&gdg_node_visited, node_atom_number))
      {
        set_unite_element(&gdg_node_visited, node_atom_number);

        for (gdg_edge* this_edge = (gdg_edge*) graph_next_out_edge(this_node); this_edge != NULL; this_edge = (gdg_edge*) graph_next_out_edge(this_edge))
          gdg_analyse_gdg_bu_rec(this_edge);

        set_difference_element(&gdg_node_visited, node_atom_number);
      }
    }

    set_difference_element(&gdg_edge_visited, edge_atom_number);
  }

}

void gdg_dump_cycle_table(void* gdg_cycle_table, int partition_number)
{
  int cycle_count = 0;

  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
  {
    cycle_count++;
    text_printf("%s%s%s%s {",
                this_symbol->left_epsilon_context? "l" : "",
                this_symbol->left_context? "L" : "",
                this_symbol->right_epsilon_context? "r" : "",
                this_symbol->right_context? "R" : "");
    set_print_set(&this_symbol->id, NULL, 0);
    text_printf("} {");
    set_print_set_callback(&this_symbol->id, gdg_print_id_for_source_of_edge_atom_number_callback, 0);
    text_printf("} %i\n", partition_number);
  }
  text_printf("Partition %i has %u cycles\n", partition_number, cycle_count);
}

void* gdg_compute_cycles(gdg* this_gdg, set_ *valid_nodes, int partition_number)
{
  analyse_gdg = this_gdg;
  gdg_valid_nodes = valid_nodes;
  total_cycles = 0;

  if (script_gtb_verbose())
    text_printf("Computing cycles for partition %i\n", partition_number);

  gdg_cycle_table = symbol_new_table("gdg_cycle_table", 999983, 999979, symbol_compare_set, symbol_hash_set, symbol_print_set);

  for (gdg_start_node = (gdg_node*) graph_next_node(this_gdg->gdg_nodes);
       gdg_start_node != NULL;
       gdg_start_node = (gdg_node*) graph_next_node(gdg_start_node))
  {
	#if defined(GDG_ANALYSE_BU_TRACE)
    text_printf("Starting search for nonterminal %s\n", gdg_start_node->symbol_table_entry->id);
    cycles = 0;
#endif
    set_clear(&gdg_edge_visited);
    set_clear(&gdg_node_visited);

    for (gdg_edge* this_edge = (gdg_edge*) graph_next_out_edge(gdg_start_node); this_edge != NULL; this_edge = (gdg_edge*) graph_next_out_edge(this_edge))
      gdg_analyse_gdg_bu_rec(this_edge);

#if defined(GDG_ANALYSE_BU_TRACE)
    total_cycles+= cycles;
    text_printf("Cyclecount %u, total %lu\n", cycles, total_cycles);
#endif
  }

  //Calculate cycle attributes
  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
  {
    unsigned *edge_numbers = set_array(&this_symbol->id);

    for (unsigned *this_edge_number = edge_numbers; *this_edge_number != SET_END; this_edge_number++)
    {
      gdg_edge* this_edge = (gdg_edge*) graph_edge_index(analyse_gdg->gdg_nodes)[*this_edge_number];
      this_symbol->left_epsilon_context = this_symbol->right_epsilon_context = 1;

      if (this_edge->left_context)
        this_symbol->left_context = 1;

      if (this_edge->right_context)
        this_symbol->right_context = 1;

      if (!this_edge->left_epsilon_context)
        this_symbol->left_epsilon_context = 0;

      if (!this_edge->right_epsilon_context)
        this_symbol->right_epsilon_context = 0;
    }
  }

  if (script_gtb_verbose())
    gdg_dump_cycle_table(gdg_cycle_table, partition_number);

  //Compute id_arrays
  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
    this_symbol->id_array = set_array(&this_symbol->id);

  return gdg_cycle_table;
}

static gdg_cycle_data **gdg_cycles;
static set_ gdg_break_set = SET_NULL;
static unsigned gdg_break_set_cardinality;
static long unsigned break_rec_calls;
static long unsigned breaks;
static long unsigned leaves;
static long unsigned search_cuts;
static long unsigned gdg_recursion_count;
static long unsigned suppressed_break_sets;
static long unsigned gdg_break_set_cardinality_limit;
static bool gdg_prune_break_sets_by_element;
static bool gdg_prune_break_sets_by_set;

#if 1 //Restore old code
void gdg_break_cycles_rec(int cycle_number)
{
  break_rec_calls++;
  if (script_gtb_verbose() && break_rec_calls % 100000 == 0)
    text_printf("\r%lu calls; %lu leaf calls, %lu break sets added; %lu break sets removed; %lu search cuts", break_rec_calls, leaves, breaks, suppressed_break_sets, search_cuts);
//  text_printf("%i\n", level);

// Bottom out recursion if we've reached the overall call limit (optional paramater 2 in the call to gdg_analyse_bu[])
  if (gdg_recursion_count != 0 && gdg_recursion_count < break_rec_calls)
    return;

// Bottom out recursion if we've reached the cardinality limit (optional paramater 3 in the call to gdg_analyse_bu[])
  if (gdg_break_set_cardinality_limit != 0 && gdg_break_set_cardinality_limit < set_cardinality(&gdg_break_set))
  {
    gdg_cardinality_limit_triggered = true;
    return;
  }

  if (gdg_cycles[cycle_number] == NULL) //we're at the end of the list of cycles so add to break table
  {
    unsigned gdg_break_set_cardinality = set_cardinality(&gdg_break_set);

    if (symbol_lookup_key(gdg_break_table, &gdg_break_set, NULL) == NULL)
    {
      if (gdg_prune_break_sets_by_set)
      {
        // Revised code to check for super and subsets when we've found a break set
        break_data* induction_break_symbol = (break_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));

        while (induction_break_symbol != NULL)
        {
          break_data *this_break_symbol = induction_break_symbol;

          induction_break_symbol = (break_data*) symbol_next_symbol_in_scope(this_break_symbol);

          if (this_break_symbol->cardinality < gdg_break_set_cardinality)
          {
            if (set_includes_set(&gdg_break_set, &this_break_symbol->id))
              return;  /* Nothing to do: a better set already exists and we don't need to search for supersets */
          }
          else if (this_break_symbol->cardinality > gdg_break_set_cardinality)
          {
            if (set_includes_set(&this_break_symbol->id, &gdg_break_set))
            {
              // Delete this element
              symbol_free_symbol(this_break_symbol);
              suppressed_break_sets++;
            }
          }
        }
      }
      else if (gdg_prune_break_sets_by_element)
      {
        // This is the second part of Liz's idea to remove the non-minimal break sets
        // Horrible hack: iterating over symbols while we change the list is dangerous, so let's just do it again and again...
        int changed = true;

        while (changed)
        {
          changed = false;

          for (set_* this_break_set = (set_*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));
               this_break_set != NULL;
               this_break_set = (set_*) symbol_next_symbol_in_scope(this_break_set))
            if (set_includes_set(this_break_set, &gdg_break_set))
            {
              changed = true;
              symbol_free_symbol(this_break_set);
              suppressed_break_sets++;
              break;
            }
        }
      }

      set_ temp = SET_NULL;
      set_assign_set_normalise(&temp, &gdg_break_set);

      ((break_data*) symbol_insert_key(gdg_break_table, &temp, sizeof (set_), sizeof (break_data)))->cardinality = gdg_break_set_cardinality;
      breaks++;
    }
  }
  else // We're not at the end of the list, so modify break_set and recurse
  {
    if (set_has_common_elements_set(&gdg_break_set, &(gdg_cycles[cycle_number]->id)))  // Nothing to do: we're looking for mininal break sets
      gdg_break_cycles_rec(cycle_number+1);
    else
    {
      // Construct new_break set for this cycle
      unsigned *break_set_elements = set_array(&(gdg_cycles[cycle_number]->id));

      for (unsigned *this_break_set_element = break_set_elements;
           *this_break_set_element != SET_END;
           this_break_set_element++)
      {
        set_unite_element(&gdg_break_set, *this_break_set_element);
//        text_printf("Adding %u\n", *this_break_set_element);

        if (gdg_prune_break_sets_by_element)
        {
  // This is the first part of the code to implement Liz's idea to ensure that only break sets minimal under inclusion are
  // incorporated into the break set table
          int subset_found = false;
          for (set_* this_break_set = (set_*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));
               this_break_set != NULL;
               this_break_set = (set_*) symbol_next_symbol_in_scope(this_break_set))
            if (set_includes_set(&gdg_break_set, this_break_set))
            {
              subset_found = true;
              search_cuts++;
              break;
            }

          if (!subset_found)
            gdg_break_cycles_rec(cycle_number+1);
        }
        else
          gdg_break_cycles_rec(cycle_number+1);

        set_difference_element(&gdg_break_set, *this_break_set_element);
      }

      mem_free(break_set_elements);
    }
  }
}
#else
//New version
void gdg_break_cycles_rec(gdg_cycle_data *cycle)
{
  break_rec_calls++;
//  if ((break_rec_calls & 0xffff) == 0)
//  if ((break_rec_calls %10000000) == 0)
//      text_printf("\r%lu calls; %lu leaf calls, %lu break sets added; %lu break sets removed; %lu search cuts", break_rec_calls, leaves, breaks, suppressed_break_sets, search_cuts);

  if (cycle == NULL) //we're at the end of the list of cycles so add to break table
  {
    leaves++;
    if (symbol_lookup_key(gdg_break_table, &gdg_break_set, NULL) == NULL)
    {
      set_ temp = SET_NULL;
      set_assign_set_normalise(&temp, &gdg_break_set);

      ((break_data*) symbol_insert_key(gdg_break_table, &temp, sizeof (set_), sizeof (break_data)))->cardinality = gdg_break_set_cardinality;

      for (break_data* this_break_symbol = (break_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));
           this_break_symbol != NULL;
           this_break_symbol = (break_data*) symbol_next_symbol_in_scope(this_break_symbol))
      {
        if (this_break_symbol->cardinality != 0)
        {
          if (this_break_symbol->cardinality < gdg_break_set_cardinality)
          {
            if (set_includes_set(&gdg_break_set, &this_break_symbol->id))
              return;  /* Nothing to do: a better set already exists and we don't need to search for supersets */
          }
          else if (this_break_symbol->cardinality > gdg_break_set_cardinality)
          {
            if (set_includes_set(&this_break_symbol->id, &gdg_break_set))
            {
              this_break_symbol->cardinality = 0;
              suppressed_break_sets++;
            }
          }
        }
      }
      breaks++;
    }
  }
  else // We're not at the end of the list, so modify break_set and recurse
  {
    gdg_cycle_data *next_cycle = (gdg_cycle_data*) symbol_next_symbol_in_scope(cycle);

    if (set_has_common_elements_set(&gdg_break_set, &cycle->id))  // Nothing to do: we're looking for mininal break sets
      gdg_break_cycles_rec(next_cycle);
    else
    {
#if 1
      //Start of Adrian's idea to optimise subset searching
      set_clear(&gdg_diff_set);
#if 1
      for (break_data* this_break_symbol = (break_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));
           this_break_symbol != NULL;
           this_break_symbol = (break_data*) symbol_next_symbol_in_scope(this_break_symbol))
      {
        if (this_break_symbol->cardinality != 0)
        {
//          set_assign_set(&gdg_temp_set, &gdg_break_set);
//          set_difference_set(&gdg_temp_set, &this_break_symbol->id);
//          if (set_cardinality(&gdg_temp_set) == 1)
//            set_unite_set(&gdg_diff_set, &gdg_temp_set);
        }
      }
//      set_assign_set(&gdg_temp_set, &cycle->id);
//      set_difference_set(&gdg_temp_set, &gdg_diff_set);

#endif
      unsigned *filtered_cycle_elements = set_array(&cycle->id);

      gdg_break_set_cardinality++;
      for (unsigned *this_cycle_element = filtered_cycle_elements;
           *this_cycle_element != SET_END;
           this_cycle_element++)
      {
        set_unite_element(&gdg_break_set, *this_cycle_element);
        gdg_break_cycles_rec(next_cycle);
        set_difference_element(&gdg_break_set, *this_cycle_element);
      }
      mem_free(filtered_cycle_elements);
      gdg_break_set_cardinality--;


#else
      // Construct new_break set for this cycle
      gdg_break_set_cardinality++;
      for (unsigned *this_cycle_element = cycle->id_array;
           *this_cycle_element != SET_END;
           this_cycle_element++)
      {
        set_unite_element(&gdg_break_set, *this_cycle_element);
        gdg_break_cycles_rec(next_cycle);
        set_difference_element(&gdg_break_set, *this_cycle_element);
      }
      gdg_break_set_cardinality--;
#endif
    }
  }
}
#endif

void gdg_dump_break_table(void* gdg_break_table, int partition_number, int break_limit)
{
  unsigned min_cardinality = UINT_MAX;

  int break_count = 0;

  for (break_data *this_symbol = (break_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));
       this_symbol != NULL;
       this_symbol = (break_data*) symbol_next_symbol_in_scope(this_symbol))
  {
    if (this_symbol->cardinality != 0)
      break_count++;

    if (this_symbol->cardinality < min_cardinality)
      min_cardinality = this_symbol->cardinality;

    if (break_limit != 0)
    {
      if (this_symbol->cardinality != 0)
      {
        text_printf("break: {");
        set_print_set(&this_symbol->id, NULL, 0);
        text_printf("} cardinality %i partition %i\n", this_symbol->cardinality, partition_number);
        if (break_limit > 0)
          break_limit--;
      }
    }
    else
    {
      text_printf("Break set dump terminated on reaching limit\n");
      break;
    }
  }
  text_printf("Partition %i has %u break set%s with minimum cardinality %i\n", partition_number, break_count, break_count == 1 ? "" : "s", min_cardinality);
}


struct cycle_inclusion_data
{
  gdg_cycle_data *cycle;
};

void *gdg_construct_cycle_inclusion_graph(void *cycle_table)
{
  void *ret_graph = graph_insert_graph("cycle inclusion");

  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
  {
    cycle_inclusion_data *this_node = (cycle_inclusion_data *) graph_insert_node(sizeof(cycle_inclusion_data), ret_graph);

    this_node->cycle = this_symbol;
  }

  graph_create_node_index(ret_graph);

  int node_count = graph_max_node_number(ret_graph);

  set_ temp = SET_NULL;

  for (unsigned i = 1; i<=node_count; i++)
    for (unsigned j = i + 1; j<=node_count; j++)
    {
      set_assign_set(&temp, &((cycle_inclusion_data*) graph_node_index(ret_graph)[i])->cycle->id);
      set_intersect_set(&temp, &((cycle_inclusion_data*) graph_node_index(ret_graph)[j])->cycle->id);

      if (set_cardinality(&temp) > 3)
        graph_insert_edge(0, graph_node_index(ret_graph)[i],
                             graph_node_index(ret_graph)[j]);
    }

    set_free(&temp);

  return ret_graph;
}

void gdg_edge_cycle_histogram(void* cycle_table, int scc_number)
{
  histogram_node *this_hist;

  text_printf("Edge usage histogram for partition %i\n", scc_number);

  hist_init(&this_hist);

  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
  {
    unsigned *cycle_edge_numbers = set_array(&this_symbol->id);

    for (unsigned *this_cycle_edge_number = cycle_edge_numbers; *this_cycle_edge_number != SET_END; this_cycle_edge_number++)
      hist_update(this_hist, *this_cycle_edge_number);

    mem_free(cycle_edge_numbers);
  }

  hist_print(this_hist);

  hist_free(&this_hist);
}

#define cardinality_cost 1
#define unbroken_cycle_cost 10

static double cost(void *cycle_table, set_ *break_set)
{
  double this_cost = set_cardinality(break_set) * cardinality_cost;

  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
    if (!set_has_common_elements_set(&this_symbol->id, break_set))
      this_cost += unbroken_cycle_cost;

  return this_cost;
}

void gdg_anneal_break_set(void *cycle_table,
                           int anneal_accept_threshold,
                           int anneal_reject_threshold,
                           double anneal_temperature_start,
                           double anneal_temperature_reduction)
{
  // Collect the edge numbers for the cycles in this table
  set_ cycle_edges = SET_NULL;

  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
    set_unite_set(&cycle_edges, &this_symbol->id);

  unsigned edge_count = set_cardinality(&cycle_edges);

  unsigned *cycle_edge_numbers = set_array(&cycle_edges);

  set_ break_set = SET_NULL;

//  set_fill(&break_set, edge_count);

  unsigned long step = 0;
  int good, bad;
  double temp = anneal_temperature_start;

  double current_cost = cost(cycle_table, &break_set);

  do
  {
    good = bad = 0;
    do
    {
      unsigned current_move_element_index = rand()%edge_count;   /* randomly select an edge to flip */

      unsigned current_move_element = cycle_edge_numbers[current_move_element_index];

      text_printf("Inverting edge %u\nBefore:{", current_move_element);
      set_print_set(&break_set, NULL, 0);
      set_invert_element(&break_set, current_move_element);  // perturb configuration
      text_printf("}\nAfter:{");
      set_print_set(&break_set, NULL, 0);
      text_printf("}\n");

      double new_cost = cost(cycle_table, &break_set);

      double de = current_cost - new_cost;

      if (de < 0 || ((double) rand() / (double) RAND_MAX) < exp(-de / temp))
      {
        current_cost = new_cost;
        good++;                 /* accept */
      }
      else
      {
        set_invert_element(&break_set, current_move_element);  // un-perturb configuration
        bad++;
      }
    }
    while (good < anneal_accept_threshold && bad < anneal_reject_threshold);
    printf("Step %6i Good %3i Bad %3i T %lf Cost %lf\n", step, good, bad, temp, current_cost);

    temp *= anneal_temperature_reduction;

    step++;
  }
  while (good > 0 && temp > 1E-6);
}

void gdg_analyse_bu_action(int scc_number)
{
  //First do L cycles
  unsigned cycle_count = 0;
  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->left_context)
      gdg_cycles[cycle_count++] = this_symbol;

  gdg_cycles[cycle_count] = NULL;

  set_clear(&gdg_break_set);
  gdg_break_cycles_rec(0);

  text_printf("\nAfter computing L-breaks: %lu calls; %lu leaf calls, %lu break sets added; %lu break sets removed; %lu search cuts\n", break_rec_calls, leaves, breaks, suppressed_break_sets, search_cuts);
  gdg_dump_break_table(gdg_break_table, scc_number, 100);

  //Now add in R cycles
  cycle_count = 0;
  for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
       this_symbol != NULL;
       this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
    if (this_symbol->right_context)
      gdg_cycles[cycle_count++] = this_symbol;

  gdg_cycles[cycle_count] = NULL;

  set_clear(&gdg_break_set);
  gdg_break_cycles_rec(0);

  text_printf("\nAfter computing L and R breaks: %lu calls; %lu leaf calls, %lu break sets added; %lu break sets removed; %lu search cuts\n", break_rec_calls, leaves, breaks, suppressed_break_sets, search_cuts);
  gdg_dump_break_table(gdg_break_table, scc_number, 100);
}

void gdg_analyse_gdg_bu(gdg * this_gdg, int break_set_disposition, unsigned long recursion_count, int long break_set_cardinality_limit)
{
  text_printf("Analysing loop structure of GDG with recursion limit %li and maximum break set cardinality %li\n", recursion_count, break_set_cardinality_limit);
  text_printf("Break sets are %s\n", break_set_disposition == SCRIPT_BUILT_IN_retain_break_sets ? "retained" :
                                 break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_element ? "pruned on an element by element basis" :
                                 break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_set ? "pruned when a new break set is detected" :
                                 "??? illegal break set disposition");

  // Make top level SCC partitions
  set_ empty_set = SET_NULL;
  this_gdg->strong_number = graph_tarjan_scc(this_gdg->gdg_nodes, &empty_set, 1, NULL);

  gdg_recursion_count = recursion_count;
  bool gdg_cardinality_limit_restart = break_set_cardinality_limit < 0;
  if (break_set_disposition == SCRIPT_BUILT_IN_retain_break_sets)
  {
    gdg_prune_break_sets_by_element = false;
    gdg_prune_break_sets_by_set = false;
  }
  else if (break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_element)
  {
    gdg_prune_break_sets_by_element = true;
    gdg_prune_break_sets_by_set = false;
  }
  else if (break_set_disposition == SCRIPT_BUILT_IN_prune_break_sets_by_set)
  {
    gdg_prune_break_sets_by_element = false;
    gdg_prune_break_sets_by_set = true;
  }
  else
    text_message(TEXT_FATAL, "unknown break set disposition detected\n");

  suppressed_break_sets = 0;
  break_rec_calls = 0;
  breaks = 0;
  leaves = 0;
  search_cuts = 0;
  gdg_break_set_cardinality = 0;

  int max_scc_number = 0;

  set_ all_nodes = SET_NULL;

  for (gdg_node* this_node = (gdg_node*) graph_next_node(this_gdg->gdg_nodes);
       this_node != NULL;
       this_node = (gdg_node*) graph_next_node(this_node))
  {
    set_unite_element(&all_nodes, graph_atom_number(this_node));

    if (this_gdg->strong_number[graph_atom_number(this_node)] > max_scc_number)
      max_scc_number = this_gdg->strong_number[graph_atom_number(this_node)];
  }

//  symbol_free_table(gdg_analyse_one_scc(this_gdg, &all_nodes, -1));
//  text_printf("GDG has %i strongly connected components\n", max_scc_number);

  set_ scc_nodes = SET_NULL, scc_edges = SET_NULL;

  for (int scc_number = 0; scc_number <= max_scc_number; scc_number++)
  {
    set_clear(&scc_nodes);
    set_clear(&scc_edges);

    for (gdg_node* this_node = (gdg_node*) graph_next_node(this_gdg->gdg_nodes);
         this_node != NULL;
         this_node = (gdg_node*) graph_next_node(this_node))
      if (this_gdg->strong_number[graph_atom_number(this_node)] == scc_number || (scc_number == 0 && this_gdg->strong_number[graph_atom_number(this_node)] < 0))
      {
        set_unite_element(&scc_nodes, graph_atom_number(this_node));
        for (gdg_edge* this_edge = (gdg_edge*) graph_next_out_edge(this_node); this_edge != NULL; this_edge = (gdg_edge*) graph_next_out_edge(this_edge))
          if (this_gdg->strong_number[graph_atom_number(graph_destination(this_edge))] == scc_number)
            set_unite_element(&scc_edges, graph_atom_number(this_edge));
      }

    text_printf("SCC component %i has %u nodes and %u edges\n", scc_number, set_cardinality(&scc_nodes), set_cardinality(&scc_edges));

    void *cycle_table = gdg_compute_cycles(this_gdg, &scc_nodes, scc_number);

    void *cycle_inclusion_graph = gdg_construct_cycle_inclusion_graph(cycle_table);

    char *cig_file_name = "cig0.vcg";
    FILE *cig_file = fopen(cig_file_name, "w");
    text_redirect(cig_file);
    graph_vcg(cycle_inclusion_graph, NULL, NULL, NULL);
    text_redirect(stdout);
    fclose(cig_file);
    cig_file_name[3]++;

    gdg_edge_cycle_histogram(cycle_table, scc_number);


    break_rec_calls = 0;

    gdg_break_table = symbol_new_table("gdg_break_table", 999983, 999979, symbol_compare_set, symbol_hash_set, symbol_print_set);

    gdg_break_set_cardinality_limit = break_set_cardinality_limit < 0 ? -break_set_cardinality_limit : break_set_cardinality_limit;
    set_clear(&gdg_break_set);
    gdg_cardinality_limit_triggered = false;

    //Compute size of index array
    unsigned cycle_count = 0;
    for (gdg_cycle_data *this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_cycle_table));
         this_symbol != NULL;
         this_symbol = (gdg_cycle_data*) symbol_next_symbol_in_scope(this_symbol))
      cycle_count++;

    gdg_cycles = (gdg_cycle_data**) mem_calloc(cycle_count + 1, sizeof(gdg_cycle_data*));

    gdg_analyse_bu_action(scc_number);

    if (gdg_recursion_count != 0 && gdg_recursion_count < break_rec_calls)
      text_printf("Exceeded gdg_analyse_bu recursion limit of %lu calls\n", gdg_recursion_count);

    if (gdg_cardinality_limit_triggered)
    {
      text_printf("There are further break sets of cardinality %lu or above\n", gdg_break_set_cardinality_limit);

      if (gdg_cardinality_limit_restart)
      {
        gdg_break_set_cardinality_limit = 0;
        text_printf("Restarting unbounded gdg_analyse_bu recursion with previous table\n");

        gdg_analyse_bu_action(scc_number);
      }
    }


    //Now perform (possibly redundant) n^2 subset test

    for (break_data *test_symbol = (break_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));
         test_symbol != NULL;
         test_symbol = (break_data*) symbol_next_symbol_in_scope(test_symbol))
      for (break_data *mark_symbol = (break_data*) symbol_next_symbol_in_scope(symbol_get_scope(gdg_break_table));
           mark_symbol != NULL;
           mark_symbol = (break_data*) symbol_next_symbol_in_scope(mark_symbol))
        if ((test_symbol != mark_symbol) && set_includes_set(&mark_symbol->id, &test_symbol->id))
          mark_symbol->cardinality = 0;

  text_printf("\nAfter setting supersets to cardinality zero: %lu calls; %lu leaf calls, %lu break sets added; %lu break sets removed; %lu search cuts\n", break_rec_calls, leaves, breaks, suppressed_break_sets, search_cuts);
  gdg_dump_break_table(gdg_break_table, 0, -1);

//    if (scc_number == 2)
//      gdg_anneal_break_set(cycle_table, 100, 200, 10000.0, 0.99);

    symbol_free_table(gdg_break_table);
  }

  set_free(&scc_nodes);
  set_free(&scc_edges);

#if defined(GDG_ANALYSE_BU_TRACE)
  text_printf("Strongly connected component numbers\n");
  for (gdg_node* this_node = (gdg_node*) graph_next_node(this_gdg->gdg_nodes);
       this_node != NULL;
       this_node = (gdg_node*) graph_next_node(this_node))
  {
    text_printf("%s: %i\n", this_node->symbol_table_entry->id, this_gdg->strong_number[graph_atom_number(this_node)]);
  }
#endif
}

