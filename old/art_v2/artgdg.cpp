/* artgdg.cpp - ART V2 grammar dependency graph routines(c) Adrian Johnstone 2009-2012 */
#include "artaux.h"

static void artComputeDependenciesRec(symbols_data* symbol, rdp_tree_node_data* node) {
  if (node == NULL)
    return;

  if (node->tableEntry != NULL && !node->isLHS)
    set_unite_element(&(symbol->dependency), node->tableEntry->number);

  for (void* edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artComputeDependenciesRec(symbol, (rdp_tree_node_data*) graph_destination(edge));
}

void artWriteGDGMDG() {
  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       sym != NULL;
       sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
    if (sym->kind == ART_NONTERMINAL)
      artComputeDependenciesRec(sym, sym->PPrimeNode);

  FILE *gdgFile = fopen("artgdg.vcg", "w");
  text_redirect(gdgFile);
  text_printf("graph:{\n"
              "orientation:top_to_bottom\n"
              "edge.arrowsize:7\n"
              "edge.thickness:1\n"
              "display_edge_labels:yes\n"
              "arrowmode:free\n"
              "node.borderwidth:1\n");

  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       sym != NULL;
       sym = (symbols_data*) symbol_next_symbol_in_scope(sym)) {
    if (sym->kind != ART_NONTERMINAL)
      continue;

    text_printf("node:{title:\"");
    artWriteSymbol(sym->kind, sym->minorID, sym->majorID, NULL, NULL);
    text_printf("\"}\n");

    unsigned *dependencyNumbers = set_array(&sym->dependency);

    for (unsigned* dependencyNumber = dependencyNumbers; *dependencyNumber != SET_END; dependencyNumber++) {
      symbols_data *dependencySym;

      // Ugly and slow symbol lookup by number - printSymbols has an index creator: better to factor that out
      for (dependencySym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
           dependencySym != NULL;
           dependencySym = (symbols_data*) symbol_next_symbol_in_scope(dependencySym))
        if (*dependencyNumber == dependencySym->number)
          break;
      if (dependencySym->kind == ART_NONTERMINAL) {
        text_printf("edge:{sourcename:\"");
        artWriteSymbol(sym->kind, sym->minorID, sym->majorID, NULL, NULL);
        text_printf("\" targetname:\"");
        artWriteSymbol(dependencySym->kind, dependencySym->minorID, dependencySym->majorID, NULL, NULL);
        text_printf("\"}");
      }
    }

    mem_free(dependencyNumbers);
  }

  text_printf("}\n");
  text_redirect(stdout);
}

