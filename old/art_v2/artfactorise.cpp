/* artfactorise.cpp - ART V2 factorisation routines(c) Adrian Johnstone 2009-2012 */
#include "artaux.h"
//#define FACTORISE_TRACE

struct factorListElement {rdp_tree_node_data *productionNode; struct factorListElement *sibling; struct factorListElement *children;};
                                      
static bool artEqualNode(bool deleterMatch, rdp_tree_node_data *left, rdp_tree_node_data *right) {

  bool isEqual = true;

  // Check identifiers
  isEqual &= (strcmp(left->id, right->id) == 0);

  // Check kind
  isEqual &= left->kind == right->kind;

  // Check symbol entries except for deleters
  if (!deleterMatch) {
    if (left->tableEntry == NULL && right->tableEntry == NULL) // match
      ;
    else if (left->tableEntry == NULL || right->tableEntry == NULL) // mismatch - one has a tableEntry and the other doesn't
      isEqual = false;
    else {
      isEqual &= strcmp(left->tableEntry->minorID, right->tableEntry->minorID) == 0;
      isEqual &= strcmp(left->tableEntry->majorID, right->tableEntry->majorID) == 0;
    }
  }

  return isEqual;
}

bool artEqualSubtreeRec(bool deleterMatch, rdp_tree_node_data* left, rdp_tree_node_data* right) {
  // Special case: deleted subtree always match any other deleted subtree
#if defined FACTORISE_TRACE
  printf("testing for equality subtrees rooted at %i labelled %s and %i labelled %s\n", graph_atom_number(left), left->id, graph_atom_number(right), right->id);
#endif
  if (left->deleted && right->deleted)
    return true;

  bool isEqual = true;

  void *leftEdge, *rightEdge;

  for (leftEdge = graph_next_out_edge(left), rightEdge = graph_next_out_edge(right);
       leftEdge != NULL && rightEdge != NULL;
       leftEdge = graph_next_out_edge(leftEdge), rightEdge = graph_next_out_edge(rightEdge))
    isEqual &= artEqualSubtreeRec(deleterMatch, (rdp_tree_node_data*) graph_destination(leftEdge), (rdp_tree_node_data*) graph_destination(rightEdge));

  if (leftEdge != NULL || rightEdge != NULL)
    isEqual = false;  // We didnt get to the end of one of the sequences

  if (left->deleted || right->deleted)
    isEqual = false;

  isEqual &= artEqualNode(deleterMatch, left, right);

#if defined FACTORISE_TRACE
  printf("Subtrees rooted at %i labelled %s and %i labelled %s are %sequal\n", graph_atom_number(left), left->id, graph_atom_number(right), right->id, isEqual ? "" : "un");
#endif
  return isEqual;
}

static factorListElement *artFactorListElementInsert (factorListElement *parentNode, rdp_tree_node_data *node) {
#if defined FACTORISE_TRACE
  printf("Inserting node %i %s into Trie under node pointing at %i %s\n", graph_atom_number(node), node->id, graph_atom_number(parentNode->productionNode), parentNode->productionNode->id);
#endif

  for (factorListElement *child = parentNode->children; child != NULL; child = child->sibling)
    if (artEqualSubtreeRec(false, child->productionNode, node))
      return child;

  factorListElement *newChild = new factorListElement;

  newChild->productionNode = node;
  newChild->children = NULL;
  newChild->sibling = parentNode->children;
  parentNode->children = newChild;

  return newChild;
}

static void artWriteFactorTree(factorListElement *node) {
  if (node == NULL)
    return;

  printf("%i:%s ", graph_atom_number(node->productionNode), node->productionNode->id);

  if (node->children != NULL && node->children->sibling != NULL)
    printf ("( ");
  artWriteFactorTree(node->children);
  if (node->children != NULL && node->children->sibling != NULL)
    printf(") ");

  if (node->sibling != NULL)
    printf("| ");

  artWriteFactorTree(node->sibling);
}

static void artBuildFactorProduction(rdp_tree_node_data* productionNode, bool isFirst, factorListElement *node) {
  if (node == NULL)
    return;

  rdp_tree_node_data *newTreeNode = productionNode;

  if (isFirst) {
    if  (*(node->productionNode->id) == 0 && node->productionNode->kind ==0) {//End of String means an empty sub-production - so #
// WAS:      newTreeNode = artNewTreeNode(newTreeNode, "epsilon", ART_EPSILON, NULL);
      newTreeNode = artNewTreeNode(newTreeNode, "unary", ART_UNARY, NULL);
      artNewTreeNode(newTreeNode, "pos", ART_POS, NULL);
      artNewTreeNode(newTreeNode, "epsilon", ART_EPSILON, NULL);
      artNewTreeNode(newTreeNode, "pos", ART_POS, NULL);

    }
    else {
      newTreeNode = artNewTreeNode(newTreeNode, "cat", ART_CAT, NULL);
      artNewTreeNode(newTreeNode, "pos", ART_POS, NULL);
    }
  }

  if  (!(*(node->productionNode->id) == 0 && node->productionNode->kind ==0)) //Suppress end of string markers?
    artCloneTreeAsChildOf(newTreeNode, node->productionNode);

  bool newIsFirst = node->children != NULL && node->children->sibling != NULL;

  if (newIsFirst) {  // Start of alternate
    rdp_tree_node_data *tmpTreeNode = artNewTreeNode(newTreeNode, ")", ART_DO_FIRST, NULL);
    tmpTreeNode = artNewTreeNode(tmpTreeNode, "alt", ART_ALT, NULL);

    artNewTreeNode(newTreeNode, "pos", ART_POS, NULL);
    newTreeNode = tmpTreeNode;
  }

  artBuildFactorProduction(newTreeNode, newIsFirst, node->children);

  artBuildFactorProduction(productionNode, isFirst, node->sibling);
}

void artLeftFactorise(void* tree) {
  rdp_tree_node_data *emptyTreeNode =(rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), tree);
  emptyTreeNode->id = "";
  emptyTreeNode->deleted = true;
  emptyTreeNode->kind = ART_ILLEGAL;

  for (void* moduleEdge = graph_next_out_edge(graph_root(tree)); moduleEdge != NULL; moduleEdge = graph_next_out_edge(moduleEdge)) {
    rdp_tree_node_data *moduleNode = (rdp_tree_node_data*) graph_destination(moduleEdge);
    void* nonterminalEdge = graph_next_out_edge(moduleNode);

    while (nonterminalEdge != NULL) {
      rdp_tree_node_data *LHSNode = (rdp_tree_node_data*) graph_destination(nonterminalEdge);

#if defined FACTORISE_TRACE
      printf("Testing left factorisation for node %i nonterminal %s\n", graph_atom_number(LHSNode), LHSNode->id);
#endif
      factorListElement *base = new factorListElement, *parent;

      base->productionNode = LHSNode; base->sibling = base->children = NULL;

      for (void* edge = graph_next_out_edge(LHSNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data *catNode = (rdp_tree_node_data*) graph_destination(edge);

        if (catNode->deleted)
          continue; // skip deleted productions

        parent = base;

        for (void* edge = graph_next_out_edge(catNode); edge != NULL; edge = graph_next_out_edge(edge)) {
          rdp_tree_node_data *seqNode = (rdp_tree_node_data*) graph_destination(edge);

          parent = artFactorListElementInsert(parent, seqNode);

#if defined FACTORISE_TRACE
          printf("Parent updated to node pointing at %i %s\n", graph_atom_number(parent->productionNode), parent->productionNode->id);
#endif
        }
        artFactorListElementInsert(parent, emptyTreeNode);
      }

#if defined FACTORISE_TRACE
      printf("Factor tree: ");
      artWriteFactorTree(base); printf("\n");
#endif
      rdp_tree_node_data *insertNode = artNewTreeNodeHead(moduleNode, LHSNode->id, LHSNode->kind, LHSNode->tableEntry);

      insertNode->isLHS = true;
      
      for (factorListElement *production = base->children; production != NULL; production = production->sibling)
        artBuildFactorProduction(insertNode, true, production->children);
      artDeleteTree(LHSNode);

      if (artVerbose) artVCGRDT("art_debug.vcg", tree);
      nonterminalEdge = graph_next_out_edge(nonterminalEdge);
    }
  }
}

