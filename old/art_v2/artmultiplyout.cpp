/* artmultiplyout.cpp - ART V2 multiplying out routines (c) Adrian Johnstone 2009-2012 */
#include "artaux.h"

static void artMultiplyNewAlternate(rdp_tree_node_data *grandParentNode, rdp_tree_node_data *parentNode, rdp_tree_node_data *bracketNode, rdp_tree_node_data *cloneRoot) {

static bool suppressSuffixInitialPosNode;

// 1. Check for degenerate alternate
//    Check to see if we are making an epsilon, and that there is no prefix or suffix
//    Making an epsilon is indicated by cloneRoot == NULL
//    No suffix or prefix if parentNode has onlyy three children

  if (cloneRoot == NULL && graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(parentNode)))) == NULL ) {
/* WAS:    rdp_tree_node_data *newEpsilonNode = artNewTreeNode(grandParentNode, "epsilon", ART_EPSILON, NULL);
    artNewTreeNode(newEpsilonNode, "pos", ART_POS, NULL);
*/
      rdp_tree_node_data *newUnaryNode = artNewTreeNode(grandParentNode, "unary", ART_UNARY, NULL);
      artNewTreeNode(newUnaryNode, "pos", ART_POS, NULL);
      artNewTreeNode(newUnaryNode, "epsilon", ART_EPSILON, NULL);
      artNewTreeNode(newUnaryNode, "pos", ART_POS, NULL);

  }
  else {
    rdp_tree_node_data *newCatNode = artNewTreeNode(grandParentNode, "cat", ART_CAT, NULL);

    void *edge;
    for (edge = graph_next_out_edge(parentNode); graph_destination(edge) != bracketNode; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data *prefixNode = (rdp_tree_node_data*) graph_destination(edge);
//      text_printf("Cloning prefix node (%s) %i\n", prefixNode->id, graph_atom_number(prefixNode));
      artCloneTreeAsChildOf(newCatNode, prefixNode);
    }

    if (cloneRoot != NULL)  // Now step over the elements of the body, skipping the leading and trainling pos nodes
      for (void *cloneEdge = graph_next_out_edge(graph_next_out_edge(cloneRoot)); graph_next_out_edge(cloneEdge) != NULL; cloneEdge = graph_next_out_edge(cloneEdge)) {
        rdp_tree_node_data* cloneNode = (rdp_tree_node_data*) graph_destination(cloneEdge);

//        text_printf("Cloning body node (%s) %i\n", cloneNode->id, graph_atom_number(cloneNode));
        artCloneTreeAsChildOf(newCatNode, cloneNode);
        suppressSuffixInitialPosNode = false;
      }
    else
      suppressSuffixInitialPosNode = true;

    for (edge = suppressSuffixInitialPosNode ? graph_next_out_edge(graph_next_out_edge(edge)) : graph_next_out_edge(edge);
         graph_destination(edge) != NULL;
         edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data *suffixNode = (rdp_tree_node_data*) graph_destination(edge);
//      text_printf("Cloning sufffix node (%s) %i\n", suffixNode->id, graph_atom_number(suffixNode));
      artCloneTreeAsChildOf(newCatNode, suffixNode);
    }
  }
}

static int artMultiplyOutBracketsRec(rdp_tree_node_data* thisNode, rdp_tree_node_data *parentNode, rdp_tree_node_data *grandParentNode)  {
#if 0
  text_printf("artMultiplyBracketsRec() visiting node (%s) %i\n", thisNode->id, graph_atom_number(thisNode));
#endif

  if (thisNode->deleted)  // Ignore deleted subtrees
    return false;

  if ((thisNode->kind == ART_KLEENE_CLOSURE && thisNode->token == RDP_T_42 /* * */) ||
      (thisNode->kind == ART_POSITIVE_CLOSURE &&thisNode->token == RDP_T_43 /* + */)
     ) return false;  // Ignore the contents of closures

  rdp_tree_node_data *bodyParentNode;

  if ((thisNode->kind == ART_DO_FIRST && thisNode->token == RDP_T_41   /* ) */) ||
      (thisNode->kind == ART_OPTIONAL && thisNode->token == RDP_T_63    /* ? */)
     ) {
    rdp_tree_node_data *bracketNode = thisNode;

    rdp_tree_node_data* firstNonDeletedChild;
    for(void *edge = graph_next_out_edge(bracketNode); edge  != NULL; edge = graph_next_out_edge(edge)) {
      firstNonDeletedChild = (rdp_tree_node_data*) graph_destination(edge);
      if (!firstNonDeletedChild->deleted)
        break;
    }

    if (firstNonDeletedChild->kind == ART_ALT)
      bodyParentNode = firstNonDeletedChild;
    else
      bodyParentNode = bracketNode;

#if 0
    text_printf("Found bracket node (%s) %i with parent (%s) %i, grandparent (%s) %i and body parent node (%s) %i\n",
      bracketNode->id, graph_atom_number(thisNode),
      parentNode->id, graph_atom_number(parentNode),
      grandParentNode->id, graph_atom_number(grandParentNode),
      bodyParentNode->id, graph_atom_number(bodyParentNode)
      );
#endif

    if (bracketNode->kind == ART_OPTIONAL &&thisNode->token == RDP_T_63 /* ? */) {
//      text_printf("Making new alternate for empty body alternate for ()?\n");
      artMultiplyNewAlternate(grandParentNode, parentNode, bracketNode, NULL);
    }

    for (void *edge = graph_next_out_edge(bodyParentNode); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data *bodyNode = (rdp_tree_node_data*) graph_destination(edge);

      if (bodyNode->deleted)
        continue;

//      text_printf("Making new alternate for body node (%s) %i\n", bodyNode->id, graph_atom_number(bodyNode));

      if (bodyNode->kind == ART_EPSILON) {
//        text_printf("Making new alternate for epsilon subtree (%s) %i\n", bodyNode->id, graph_atom_number(bodyNode));
        artMultiplyNewAlternate(grandParentNode, parentNode, bracketNode, NULL);
      }
      else {
//        text_printf("Making new alternate for body subtree (%s) %i\n", bodyNode->id, graph_atom_number(bodyNode));
        artMultiplyNewAlternate(grandParentNode, parentNode, bracketNode, bodyNode);
      }
    }

    artDeleteTree(parentNode);

    return false; /* true; */
  }

  // Not having done a rewrite, look at our children returning as soon as a rewrite occurs
  for (void *outEdge = graph_next_out_edge(thisNode); outEdge != NULL; outEdge = graph_next_out_edge(outEdge))
    if (artMultiplyOutBracketsRec((rdp_tree_node_data*) graph_destination(outEdge), thisNode, parentNode))
      return true;

  // Not having done a rewrite or found a rewrite amongst our children; return false since nothing has changed
  return false;
}

void artMultiplyOutProductions(void* tree) {
  rdp_tree_node_data *rootNode = (rdp_tree_node_data*) graph_root(tree);

  for (void* moduleEdge = graph_next_out_edge(graph_root(tree)); moduleEdge != NULL; moduleEdge = graph_next_out_edge(moduleEdge)) {
    rdp_tree_node_data *moduleNode = (rdp_tree_node_data*) graph_destination(moduleEdge);

        while (artMultiplyOutBracketsRec(moduleNode, rootNode, NULL))
          ;
      }
}

