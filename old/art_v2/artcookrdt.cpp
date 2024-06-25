/* artcookrdt.cpp - ART V2 RDT fixup routines (c) Adrian Johnstone 2009-2012 */
#include "artaux.h"

static void artRewriteLeaf(void *RDT, rdp_tree_node_data *node, artKind kind, const char* artMostRecentMajorID) {
  node->kind = kind;

  const char *firstID = NULL;

  for (void *childEdge = graph_next_out_edge(node); childEdge != NULL; childEdge = graph_next_out_edge(childEdge)) {
    rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(childEdge);
    rdp_tree_node_data *grandChildNode = artFirstChildOf(childNode);

    childNode->deleted = true;
    if (grandChildNode != NULL)
      grandChildNode->deleted = true;

    if (childNode->token == 0 && strcmp(childNode->id, "fold") == 0) {
      node->isGiftLabel = true;
      if (grandChildNode->token == RDP_T_9495 /* ^_ */)
        node->fold = ART_FOLD_NONE;
      else if (grandChildNode->token == RDP_T_94 /* ^ */)
        node->fold = ART_FOLD_UNDER;
      else if (grandChildNode->token == RDP_T_9494 /* ^^ */)
        node->fold = ART_FOLD_OVER;
      else if (grandChildNode->token == RDP_T_62 /* > */)
        node->fold = ART_FOLD_TEAR;
      else if (grandChildNode->token == RDP_T_6262 /* >> */)
        node->fold = ART_FOLD_TEAR_PARENT;
      else
        text_message(TEXT_FATAL, "unexpected fold node %i labelled %s\n", graph_atom_number(grandChildNode), grandChildNode->id);
    }
   else if (childNode->token == 0 && strcmp(childNode->id, "gather") == 0) {
      node->isGiftLabel = true;
      node->gatherName = artFind(ART_NONTERMINAL, artMostRecentMajorID, grandChildNode->id, NULL);
      node->gatherName->isGatherTarget = true;
    }
   else if (childNode->token == 0 && strcmp(childNode->id, "delay") == 0) {
      node->isDelayed = true;
    }
    else {
      if (firstID == NULL) {
        firstID = childNode->id;
        node->id = firstID;
      }
      else
        node->rangeUpperCharacterTerminal = artFind(ART_CHARACTER_TERMINAL, "", childNode->id, NULL);
    }
  }

  if (kind == ART_EPSILON)
    node->tableEntry = artFind(node->kind, "", "#", NULL);
  else if (kind == ART_BUILTIN_TERMINAL || kind == ART_CHARACTER_TERMINAL || kind == ART_CASE_SENSITIVE_TERMINAL || kind == ART_CASE_INSENSITIVE_TERMINAL)
    node->tableEntry = artFind(node->kind, "", node->id, NULL);
  else
    node->tableEntry = artFind(node->kind, artMostRecentMajorID, node->id, NULL);
}

static bool artLoadKindAndRewriteLeaves(void *RDT, rdp_tree_node_data *node, const char* artMostRecentMajorID) {
//  text_printf("artLoadKind visiting node %i labelled %s\n", graph_atom_number(node), node->id);

  if (node->token == 0 && strcmp(node->id, "epsilon") == 0) {
    artRewriteLeaf(RDT, node, ART_EPSILON, artMostRecentMajorID);
    return false;
  }
  else if (node->token == 0 && strcmp(node->id, "builtinTerm") == 0) {
    artRewriteLeaf(RDT, node, ART_BUILTIN_TERMINAL, artMostRecentMajorID);
    return false;
  }
  else if (node->token == 0 && strcmp(node->id, "charTerm") == 0) {
    artRewriteLeaf(RDT, node, ART_CHARACTER_TERMINAL, artMostRecentMajorID);
    return false;
  }
  else if (node->token == 0 && strcmp(node->id, "caseSensTerm") == 0) {
    artRewriteLeaf(RDT, node, ART_CASE_SENSITIVE_TERMINAL, artMostRecentMajorID);
    return false;
  }
  else if (node->token == 0 && strcmp(node->id, "caseInsensTerm") == 0) {
    artRewriteLeaf(RDT, node, ART_CASE_INSENSITIVE_TERMINAL, artMostRecentMajorID);
    return false;
  }
  else if (node->token == 0 && strcmp(node->id, "nonTerm") == 0) {
    artRewriteLeaf(RDT, node, ART_NONTERMINAL, artMostRecentMajorID);
    return false;
  }
  else if (node->token == 0 && strcmp(node->id, "cat") == 0)
    node->kind = ART_CAT;
  else if (node->token == 0 && strcmp(node->id, "unary") == 0)
    node->kind = ART_UNARY;
  else if (node->token == 0 && strcmp(node->id, "alt") == 0)
    node->kind = ART_ALT;
  else if (node->token == 0 && strcmp(node->id, "diff") == 0)
    node->kind = ART_DIFF;
  else if (node->token == 0 && strcmp(node->id, "iter") == 0)
    node->kind = ART_ITER;
  else if (node->token == RDP_T_41 /* ) */)
    node->kind = ART_DO_FIRST;
  else if (node->token == RDP_T_63 /* ? */)
    node->kind = ART_OPTIONAL;
  else if (node->token == RDP_T_43 /* + */)
    node->kind = ART_POSITIVE_CLOSURE;
  else if (node->token == RDP_T_42 /* * */)
    node->kind = ART_KLEENE_CLOSURE;
  else if (node->token == 0 && strcmp(node->id, "pos") == 0) {
    node->kind = ART_POS;
    node->tableEntry = artPosSymbol;
    rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(node));
    rdp_tree_node_data *grandChildNode = artFirstChildOf(childNode);

    if (childNode != NULL && childNode->token == 0 && strcmp(childNode->id, "annotation") == 0) {
      childNode->kind = ART_ANNOTATION;
      grandChildNode->kind = ART_ANNOTATION_VALUE; 
      return false;
    }

    if (childNode != NULL && childNode->token == 0 && strcmp(childNode->id, "name") == 0) {
      node->atomName = artFind(ART_POS_NAME, artMostRecentMajorID, grandChildNode->id, NULL);
      node->atomName->defined = true;
      childNode->deleted = true;
      if (grandChildNode != NULL)
        grandChildNode->deleted = true;
    }

    return false;
  }
  else if (node->token == 0 && strcmp(node->id, "insertion") == 0)
    node->kind = ART_INSERTION;
  else if (node->token == 0 && strcmp(node->id, "tear") == 0)
    node->kind = ART_TEAR;
  else
    text_message(TEXT_FATAL, "unexpected tree node number %i with token %i labelled '%s' found during kind labelling\n", graph_atom_number(node), node->token, node->id);

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artLoadKindAndRewriteLeaves(RDT, (rdp_tree_node_data*) graph_destination(edge), artMostRecentMajorID);

  return false; // This sub-phase completes in one iteration; no need to do closure
}

const char *elementLabel(rdp_tree_node_data *node) {
  void* edge = graph_next_out_edge(node);
  if (edge != NULL)
    return ((rdp_tree_node_data *) graph_destination(edge))->id;
  else
    return node->id;
}

symbols_data_node* processLexicalTableEntry(const char* mostRecentMajor, rdp_tree_node_data *node) {
  if (node == NULL)
    return NULL;

  symbols_data *ret;

  rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(node));

  // text_message(TEXT_INFO, "Process lexical table entry at node number %i with token %i labelled '%s'\n", graph_atom_number(node), node->token, node->id);

  if (node->token == 0 && strcmp(node->id, "caseSensTerm") == 0)
    ret = artFind(ART_CASE_SENSITIVE_TERMINAL, "", childNode->id, NULL);
  else if (node->token == 0 && strcmp(node->id, "caseInsensTerm") == 0)
    ret = artFind(ART_CASE_INSENSITIVE_TERMINAL, "", childNode->id, NULL);
  else if (node->token == 0 && strcmp(node->id, "builtinTerm") == 0)
    ret = artFind(ART_CASE_INSENSITIVE_TERMINAL, "", childNode->id, NULL);
  else
    ret = artFind(ART_NONTERMINAL, mostRecentMajor, node->id, NULL);

  ret->isLexical = true;

  return ret;
}

void addSymbolsListNode(symbols_list **base, symbols_data *symbol) {

  symbols_list *tmp = (symbols_list*) mem_calloc(1, sizeof(symbols_list));
  tmp->symbol = symbol;
  tmp->next = *base;

  *base = tmp;
}

void addLexicalPriority(symbols_data *left, symbols_data *right) {
  addSymbolsListNode(&(left->lexicalPriority), right);
}

void addLexicalShorter(symbols_data *left, symbols_data *right) {
  addSymbolsListNode(&(left->lexicalShorter), right);
}

void addLexicalLonger(symbols_data *left, symbols_data *right) {
  addSymbolsListNode(&(left->lexicalLonger), right);
}

void processLexicalNode(const char *mostRecentMajor, rdp_tree_node_data *leftNode, rdp_tree_node_data* opNode, rdp_tree_node_data* rightNode) {
  symbols_data *leftSymbol = processLexicalTableEntry(mostRecentMajor, leftNode);
  symbols_data *rightSymbol = processLexicalTableEntry(mostRecentMajor, rightNode);

  if (opNode != NULL) {
    if (strcmp(opNode->id, ">") == 0)
      addLexicalPriority(leftSymbol, rightSymbol);
    else if (strcmp(opNode->id, "<") == 0)
      addLexicalPriority(rightSymbol, leftSymbol);
    else if (strcmp(opNode->id, ">>") == 0)
      addLexicalLonger(leftSymbol, rightSymbol);
    else if (strcmp(opNode->id, "<<") == 0)
      addLexicalShorter(leftSymbol, rightSymbol);
    else if (strcmp(opNode->id, ">>>") == 0)
      addLexicalLonger(leftSymbol, leftSymbol);
    else if (strcmp(opNode->id, ">>>") == 0)
      addLexicalShorter(leftSymbol, leftSymbol);
    else if (strcmp(opNode->id, "^") == 0) {
      addLexicalLonger(rightSymbol, leftSymbol);
      addLexicalPriority(leftSymbol, rightSymbol);
    }
  }
}

bool isLexSymbol(symbols_data *symbol) {
  return symbol->isLexical || symbol->kind == ART_CASE_SENSITIVE_TERMINAL || symbol->kind == ART_CASE_INSENSITIVE_TERMINAL || symbol->kind == ART_BUILTIN_TERMINAL;
}

void artWriteLexicalDisambiguationRelations() {
  // print lexical relations from symbol table
  text_printf("*** Lexical declarations\n");

  text_printf("\nRelation X > Y (X has higher priority and marks Y for removal)\n");
  for (symbols_data *symbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
     symbol != NULL;
     symbol = (symbols_data*) symbol_next_symbol_in_scope(symbol)) {
    if (!isLexSymbol(symbol)) continue;
    for (symbols_list *tmp = symbol->lexicalPriority; tmp != NULL; tmp = tmp->next) { artWriteSymbol(symbol); text_printf(" > "); artWriteSymbol(tmp->symbol); text_printf("\n"); }
  }

  text_printf("\nRelation X >> Y (X longer than Y marks Y for removal)\n");
  for (symbols_data *symbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
     symbol != NULL;
     symbol = (symbols_data*) symbol_next_symbol_in_scope(symbol)) {
    if (!isLexSymbol(symbol)) continue;
    for (symbols_list *tmp = symbol->lexicalLonger; tmp != NULL; tmp = tmp->next) { artWriteSymbol(symbol); text_printf(" >> "); artWriteSymbol(tmp->symbol); text_printf("\n"); }
  }

  text_printf("\nRelation X << Y (X shorter than Y marks Y for removal)\n");
  for (symbols_data *symbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
     symbol != NULL;
     symbol = (symbols_data*) symbol_next_symbol_in_scope(symbol)) {
    if (!isLexSymbol(symbol)) continue;
    for (symbols_list *tmp = symbol->lexicalShorter; tmp != NULL; tmp = tmp->next) { artWriteSymbol(symbol); text_printf(" << "); artWriteSymbol(tmp->symbol); text_printf("\n"); }
  }

  text_printf("*** End of lexical declarations\n");
}

void processAttributes(rdp_tree_node_data *attributesNode, const char* artMostRecentMajorID, const char* minorID, symbols_list **attributes) {

      // ... attributes
      for (void *edge = graph_next_out_edge(attributesNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data *childNode = (rdp_tree_node_data *) graph_destination(edge);
        rdp_tree_node_data *typeNode = (rdp_tree_node_data *) graph_destination(graph_next_out_edge(childNode));

        symbols_data *attributeSymbol = artFind(ART_ATTRIBUTE, artMostRecentMajorID, minorID, childNode->id);
        if (attributeSymbol->attributeType != NULL && strcmp(typeNode->id, attributeSymbol->attributeType) != 0)
          text_message(TEXT_ERROR, "attribute %s.%s.%s declared multiply with different types\n", attributeSymbol->majorID, attributeSymbol->minorID, attributeSymbol->attributeID);

        attributeSymbol->attributeType = typeNode->id;

        addSymbolsListNode(attributes, attributeSymbol);

//      addAttribute(nonterminalSymbol, artMostRecentMajorID, strdup(artAttributeBlockName(childNode)), typeNode->id);
      }
}

void artCookRDT(void *RDT) {
  const char* artMostRecentMajorID = "";  // This is the default major name (empty) for the case where a grammar with no majors is encountered

  // Walk the level one nodes chcking for majors, productions, deleters, disambiguators and equations
  for (void* edge = graph_next_out_edge(graph_root(RDT)); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data *kindNode = (rdp_tree_node_data*) graph_destination(edge);

    if (kindNode->token == 0 && strcmp(kindNode->id, "major") == 0) {
//      text_message(TEXT_INFO, "major node number %i with token %i labelled '%s' found during toplevel scan\n", graph_atom_number(kindNode), kindNode->token, kindNode->id);
      artMostRecentMajorID = ((rdp_tree_node_data*) graph_destination(graph_next_out_edge(kindNode)))->id;
    }

    else if ((kindNode->token == 0 && strcmp(kindNode->id, "production") == 0) || (kindNode->token == 0 && strcmp(kindNode->id, "deleter") == 0)) {
//      text_message(TEXT_INFO, "production or deleter node number %i with token %i labelled '%s' found during toplevel scan\n", graph_atom_number(kindNode), kindNode->token, kindNode->id);
      rdp_tree_node_data *nonterminalNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(kindNode));
      rdp_tree_node_data *nameNode = NULL, *attributesNode = NULL, *bodyNode = NULL;

      // Insert here: scan from second child for body, name and attribute nodes
      for (void *edge = graph_next_out_edge(graph_next_out_edge(kindNode)); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(edge);

        if (childNode->token == 0 && strcmp(childNode->id, "attributes") == 0) {
          attributesNode = childNode;
          childNode->deleted = true;
        }
        else if (childNode->token == 0 && strcmp(childNode->id, "name") == 0) {
          nameNode = childNode;
          childNode->deleted = true;
        }
        else if (childNode->token == 0 && strcmp(childNode->id, "body") == 0)
          bodyNode = childNode;
      }
      // Start with the body so that the delayed attributes are set
      for (void *edge = graph_next_out_edge(bodyNode); edge != NULL; edge = graph_next_out_edge(edge))
        while (artLoadKindAndRewriteLeaves(RDT, (rdp_tree_node_data*) graph_destination(edge), artMostRecentMajorID))
          ;    // recursively process this production tree one change at a time until there are no changes


      // Now process name and attribute nodes ...
      symbols_data *nonterminalSymbol = artFind(ART_NONTERMINAL, artMostRecentMajorID, nonterminalNode->id, NULL);
#if 0
      // ... name
      if (nameNode != NULL)
        bodyNode->atomName = ((rdp_tree_node_data*) graph_destination(graph_next_out_edge(nameNode)))->id; // This might seem odd, but LHS name is production specific so as to support modularity
#endif

      processAttributes(attributesNode, artMostRecentMajorID, nonterminalSymbol->minorID, &nonterminalSymbol->attributes);

#if 0
      // ... attributes
      for (void *edge = graph_next_out_edge(attributesNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data *childNode = (rdp_tree_node_data *) graph_destination(edge);
        rdp_tree_node_data *typeNode = (rdp_tree_node_data *) graph_destination(graph_next_out_edge(childNode));

        symbols_data *attributeSymbol = artFind(ART_ATTRIBUTE, artMostRecentMajorID, nonterminalSymbol->minorID, childNode->id);
        if (attributeSymbol->attributeType != NULL && strcmp(typeNode->id, attributeSymbol->attributeType) != 0)
          text_message(TEXT_ERROR, "attribute %s.%s.%s declared multiply with different types\n", attributeSymbol->majorID, attributeSymbol->minorID, attributeSymbol->attributeID);

        attributeSymbol->attributeType = typeNode->id;

        addSymbolsListNode(&nonterminalSymbol->attributes, attributeSymbol);

//      addAttribute(nonterminalSymbol, artMostRecentMajorID, strdup(artAttributeBlockName(childNode)), typeNode->id);
      }
#if 0
#endif
      // Now add any delayed attributes
      collectDelayedAttributesRec(nonterminalSymbol, artMostRecentMajorID, bodyNode);
#endif
    }

    else if (kindNode->token == 0 && strcmp(kindNode->id, "lexical") == 0) {
      kindNode->deleted = true;
//      text_message(TEXT_INFO, "lexical node number %i with token %i labelled '%s' found during toplevel scan\n", graph_atom_number(kindNode), kindNode->token, kindNode->id);
      for (void* edge = graph_next_out_edge(kindNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data *opNode = (rdp_tree_node_data*) graph_destination(edge);

//        text_message(TEXT_INFO, "lexical subnode number %i with token %i labelled '%s' found during toplevel scan\n", graph_atom_number(opNode), opNode->token, opNode->id);
        if (opNode->token == 0 && strcmp(opNode->id, "lexicalGroup") == 0) {
          for (void* edge = graph_next_out_edge(opNode); edge != NULL; edge = graph_next_out_edge(edge)) {
            rdp_tree_node_data *elementNode = (rdp_tree_node_data*) graph_destination(edge);
            processLexicalNode(artMostRecentMajorID, elementNode, NULL, NULL);
//            text_message(TEXT_INFO, "lexical(%s)\n", elementNode->id);
          }
        }
        else {
          rdp_tree_node_data *leftGroupNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(opNode));
          rdp_tree_node_data *rightGroupNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(opNode)));

          for (void* leftEdge = graph_next_out_edge(leftGroupNode); leftEdge != NULL; leftEdge = graph_next_out_edge(leftEdge)) {
            rdp_tree_node_data *leftElementNode = (rdp_tree_node_data*) graph_destination(leftEdge);

            if (rightGroupNode == NULL) {
//              text_message(TEXT_INFO, "lexical(%s %s)\n", elementLabel(leftElementNode), opNode->id);
              processLexicalNode(artMostRecentMajorID, leftElementNode, opNode, NULL);
            }
            else
              for (void* rightEdge = graph_next_out_edge(rightGroupNode); rightEdge != NULL; rightEdge = graph_next_out_edge(rightEdge)) {
                rdp_tree_node_data *rightElementNode = (rdp_tree_node_data*) graph_destination(rightEdge);

//                text_message(TEXT_INFO, "lexical(%s %s %s)\n", elementLabel(leftElementNode), opNode->id, elementLabel(rightElementNode));
                processLexicalNode(artMostRecentMajorID, leftElementNode, opNode, rightElementNode);
              }
          }
        }
      }
    }

    else if (kindNode->token == 0 && strcmp(kindNode->id, "prelude") == 0) {
      kindNode->deleted = true;
      preludeText = ((rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_destination(graph_next_out_edge(kindNode)))))->id;
    }

    else if (kindNode->token == 0 && strcmp(kindNode->id, "support") == 0) {
      kindNode->deleted = true;

      for (void *edge = graph_next_out_edge(kindNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data *supportNode = (rdp_tree_node_data*) graph_destination(edge);

        if (strcmp(supportNode->id, "annotation") == 0) {
          supportText = ((rdp_tree_node_data*) graph_destination(graph_next_out_edge(supportNode)))->id;
        } else if (strcmp(supportNode->id, "attributes") == 0) {
          // Make a list of attributes to be added to every nonterminal
          processAttributes(supportNode, "ART", "ART_", &supportAttributes); 
        }
      }
    }

    else
      text_message(TEXT_FATAL, "unexpected tree node number %i with token %i labelled '%s' found during toplevel scan\n", graph_atom_number(kindNode), kindNode->token, kindNode->id);
  }
}

bool artRewriteEBNF(rdp_tree_node_data *node) {
  bool changed = false;

  if (node->deleted)
    return false;

//  printf("artRewriteEBNFRec() visiting node %i labelled %s with token %i\n", graph_atom_number(node), node->id, node->token);

  if (node->kind == ART_KLEENE_CLOSURE || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_OPTIONAL) {
    rdp_tree_node_data *childNode;

    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {  // Find the non deleted child node
      childNode = (rdp_tree_node_data*) graph_destination(edge);
      if (!childNode->deleted)
        break;
    }

    if (childNode->kind == ART_DO_FIRST) {  // EBNF postfix operator above patrenthesis - merge
//      printf("artRewriteEBNFRec() rewriting on () node %i labelled %s with token %i\n", graph_atom_number(childNode), childNode->id, childNode->token);
      for (void *edge = graph_next_out_edge(childNode); edge != NULL; edge = graph_next_out_edge(edge))
        artCloneTreeAsChildOf(node, (rdp_tree_node_data*) graph_destination(edge));

      artDeleteTree(childNode);
      return true;
    }
    else if (!(childNode->kind == ART_CAT || childNode->kind == ART_UNARY || childNode->kind == ART_ALT)) {
//      printf("artRewriteEBNFRec() rewriting on non-cat node %i labelled %s with token %i\n", graph_atom_number(childNode), childNode->id, childNode->token);

      rdp_tree_node_data* newCatNode = artNewTreeNode(node, "cat", ART_CAT, NULL);

      artNewTreeNode(newCatNode, "pos", ART_POS, NULL);
      artCloneTreeAsChildOf(newCatNode, childNode);
      artNewTreeNode(newCatNode, "pos", ART_POS, NULL);

      artDeleteTree(childNode);
      return true;
    }
  }

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    changed |= artRewriteEBNF((rdp_tree_node_data*) graph_destination(edge));

  return changed;
}

bool artRewriteIterators(rdp_tree_node_data *node) {
  if (node->deleted)
    return false;

  if (node->kind == ART_ITER) {
//    text_printf("Found iterator node %i\n", graph_atom_number(node));

    // Change iterator node to a do-first node
    node->token = RDP_T_41   /* ) */ ;
    node->id = ")";
    node->kind = ART_DO_FIRST;

    rdp_tree_node_data *leftChildNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(node));
    rdp_tree_node_data *rightChildNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(node)));

    rdp_tree_node_data* newCatNode = artNewTreeNode(node, "cat", ART_CAT, NULL);

    artNewTreeNode(newCatNode, "pos", ART_POS, NULL);
    artCloneTreeAsChildOf(newCatNode, leftChildNode);
    artNewTreeNode(newCatNode, "pos", ART_POS, NULL);
    rdp_tree_node_data* newKleeneNode = artNewTreeNode(newCatNode, "*", ART_KLEENE_CLOSURE, NULL);
    newKleeneNode->token= RDP_T_42 /* * */;
    artNewTreeNode(newCatNode, "pos", ART_POS, NULL);

    // Now do the sequence under the Kleene star
    newCatNode = newCatNode = artNewTreeNode(newKleeneNode, "cat", ART_CAT, NULL);

    artNewTreeNode(newCatNode, "pos", ART_POS, NULL);
    artCloneTreeAsChildOf(newCatNode, rightChildNode);
    artNewTreeNode(newCatNode, "pos", ART_POS, NULL);
    artCloneTreeAsChildOf(newCatNode, leftChildNode);
    artNewTreeNode(newCatNode, "pos", ART_POS, NULL);

    artDeleteTree(leftChildNode);
    artDeleteTree(rightChildNode);

    return true;
  }

  // Recursively check the rest of the tree
  for (void *outEdge = graph_next_out_edge(node); outEdge != NULL; outEdge = graph_next_out_edge(outEdge))
    if (artRewriteIterators((rdp_tree_node_data*) graph_destination(outEdge)))
      return true;

  return false;
}

static void artTestFoldUnderClosure(rdp_tree_node_data* node) {
  if (node->deleted)
    return;

  for (rdp_tree_edge_data* thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(node);
       thisEdge != NULL;
       thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(thisEdge))
    artTestFoldUnderClosure((rdp_tree_node_data*) graph_destination(thisEdge));

  if (node->fold == ART_FOLD_OVER)
    text_message(TEXT_ERROR, "^^ not allowed within closure at node %i\n", graph_atom_number(node));
}

bool artTestFoldWellFormed(rdp_tree_node_data* node)
{
  if (node->deleted)
    return false;

  for (rdp_tree_edge_data* thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(node);
       thisEdge != NULL;
       thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(thisEdge))
    artTestFoldWellFormed((rdp_tree_node_data*) graph_destination(thisEdge));

  if (node->kind == ART_KLEENE_CLOSURE || node->kind == ART_POSITIVE_CLOSURE)
    artTestFoldUnderClosure(node);

  return false; // This application completes in a single pass - no closure required
}

bool artFixupCatUnary(rdp_tree_node_data *node) {
  if (node->deleted)
    return false;

//  printf("artFixupUnaryRec() visiting node %i labelled %s with token %i\n", graph_atom_number(node), node->id, node->token);

  if (node->kind == ART_CAT || node->kind == ART_UNARY) {
    if (graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(node)))) == NULL) {  // arity 3
      node->id = "unary";
      node->kind = ART_UNARY;
    }
    else {
      node->id = "cat";
      node->kind = ART_CAT;
    }
  }

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artFixupCatUnary((rdp_tree_node_data*) graph_destination(edge));

  return false; // This sub-phase completes in one iteration; no need to do closure
}

bool artRewriteClosure(rdp_tree_node_data *moduleNode, rdp_tree_node_data *node, bool useLeftRecursion) {
  if (node->deleted)
    return false;

  // First check children - we're doing postorder
  for (rdp_tree_edge_data* thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(node);
       thisEdge != NULL;
       thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(thisEdge))
    if (artRewriteClosure(moduleNode, (rdp_tree_node_data*) graph_destination(thisEdge), useLeftRecursion))
      return true;  // Restart

  if (node->kind == ART_KLEENE_CLOSURE || node->kind == ART_POSITIVE_CLOSURE) {
    bool isPositive = node->kind == ART_POSITIVE_CLOSURE;

//    text_printf("Rewriting closure node %i labelled %s using %s recursion\n", graph_atom_number(node), node->id, useLeftRecursion ? "left" : "right");

    rdp_tree_node_data *childNode;  // Find non-deleted child

    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
      childNode = (rdp_tree_node_data*) graph_destination(edge);
      if (!childNode->deleted)
        break;
    }

//    text_printf("Using child node %i labelled %s\n", graph_atom_number(childNode), childNode->id);

static char *newName = (char*) mem_malloc(sizeof("ART_C_xxxxxx"));
static int closureCount = 1;

    sprintf(newName, "ART_C_%i", closureCount++);

    node->kind = ART_NONTERMINAL;
    node->id = strdup(newName);
    node->tableEntry = artFind(node->kind, moduleNode->id, node->id, NULL);
    graph_delete_edge(graph_next_in_edge(childNode));

//    text_printf("Inserting new nonterminal %s\n", node->id);

    rdp_tree_node_data* newLHSNode = artNewTreeNode(moduleNode, node->id, ART_LHS, node->tableEntry);
    node->tableEntry->iftNode = newLHSNode;
    node->tableEntry->defined = true;
    newLHSNode->isLHS = true;

//    text_printf("Inserting new lhs node %i labelled %s\n", graph_atom_number(newLHSNode), newLHSNode->id);

// Put in the base case: an epsilon for * and a copy of the body for +
    if (isPositive)
      artCloneTreeAsChildOf(newLHSNode, childNode, NULL, NULL, NULL, NULL, NULL);
    else {
      rdp_tree_node_data *newUnaryNode = artNewTreeNode(newLHSNode, "unary", ART_UNARY, NULL);
      artNewTreeNode(newUnaryNode, "pos", ART_POS, NULL);
      artNewTreeNode(newUnaryNode, "epsilon", ART_EPSILON, NULL);
      artNewTreeNode(newUnaryNode, "pos", ART_POS, NULL);
    }


// Now link in the body subtree, and modify with (left/right) recursive calls
// Notes from debug: the body can have a unary, cat or alt root
// For unary or cat we should simply link the body to the LHS and prepend (-C) or append (-c) the recursive nonterminal instance
// For alt, we should make a cat node, then attach the child and the recursive call
// Problem is that this may generate EBNF so a -m may be required; don't see this as a particular problem
    if (childNode->kind == ART_CAT || childNode->kind == ART_UNARY) {
      graph_insert_edge(sizeof(rdp_tree_edge_data), childNode, newLHSNode);

      // Force cat even if it was originally a unary
      childNode->id = "cat";
      childNode->kind = ART_CAT;

      if (useLeftRecursion) {
        artNewTreeNodeHead(childNode, node->id, ART_NONTERMINAL, node->tableEntry);
        artNewTreeNodeHead(childNode, "pos", ART_POS, NULL);
      }
      else {
        artNewTreeNode(childNode, node->id, ART_NONTERMINAL, node->tableEntry);
        artNewTreeNode(childNode, "pos", ART_POS, NULL);
      }
    }
    else if (childNode->kind == ART_ALT) {
      rdp_tree_node_data *newCatNode = artNewTreeNode(newLHSNode, "cat", ART_UNARY, NULL);
      artNewTreeNode(newCatNode, "pos", ART_POS, NULL);
      rdp_tree_node_data *newDoFirstNode = artNewTreeNode(newCatNode, ")", ART_DO_FIRST, NULL);
      newDoFirstNode->token = RDP_T_41;
      graph_insert_edge(sizeof(rdp_tree_edge_data), childNode, newDoFirstNode);
      artNewTreeNode(newCatNode, "pos", ART_POS, NULL);

      if (useLeftRecursion) {
        artNewTreeNodeHead(newCatNode, node->id, ART_NONTERMINAL, node->tableEntry);
        artNewTreeNodeHead(newCatNode, "pos", ART_POS, NULL);
      }
      else {
        artNewTreeNode(newCatNode, node->id, ART_NONTERMINAL, node->tableEntry);
        artNewTreeNode(newCatNode, "pos", ART_POS, NULL);
      }
    }
    else
      text_message(TEXT_FATAL, "unexpected closure configuration at node %i labelled %s\n", graph_atom_number(node), node->id);

    return true;
  }

  return false;
}

bool artRewriteBracketsFull(rdp_tree_node_data *moduleNode, rdp_tree_node_data *node) {
  if (node->deleted)
    return false;

  // First check children - we're doing postorder
  for (rdp_tree_edge_data* thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(node);
       thisEdge != NULL;
       thisEdge = (rdp_tree_edge_data*) graph_next_out_edge(thisEdge))
    if (artRewriteBracketsFull(moduleNode, (rdp_tree_node_data*) graph_destination(thisEdge)))
      return true;  // Restart

  if (node->kind == ART_DO_FIRST) {

//    text_printf("Rewriting bracket node %i labelled %s\n", graph_atom_number(node), node->id);

    rdp_tree_node_data *childNode;  // Find non-deleted child

    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
      childNode = (rdp_tree_node_data*) graph_destination(edge);
      if (!childNode->deleted)
        break;
    }

//    text_printf("Using child node %i labelled %s\n", graph_atom_number(childNode), childNode->id);

static char *newName = (char*) mem_malloc(sizeof("ART_B_xxxxxx"));
static int closureCount = 1;

    sprintf(newName, "ART_B_%i", closureCount++);

    node->kind = ART_NONTERMINAL;
    node->id = strdup(newName);
    node->tableEntry = artFind(node->kind, moduleNode->id, node->id, NULL);
    graph_delete_edge(graph_next_in_edge(childNode));

//    text_printf("Inserting new nonterminal %s\n", node->id);

    rdp_tree_node_data* newLHSNode = artNewTreeNode(moduleNode, node->id, ART_LHS, node->tableEntry);
    node->tableEntry->iftNode = newLHSNode;
    node->tableEntry->defined = true;
    newLHSNode->isLHS = true;

//    text_printf("Inserting new lhs node %i labelled %s\n", graph_atom_number(newLHSNode), newLHSNode->id);

// Now link in the body subtree, and modify with (left/right) recursive calls
// Notes from debug: the body can have a unary, cat or alt root
// For unary or cat we should simply link the body to the new LHS node
// For alt, link the grandchildren to the new LHS node
    if (childNode->kind == ART_CAT || childNode->kind == ART_UNARY) {
      graph_insert_edge(sizeof(rdp_tree_edge_data), childNode, newLHSNode);
    }
    else if (childNode->kind == ART_ALT) { // Connect the ALT node's children to the new LHS node
      for (void *childOutEdge = graph_next_out_edge(childNode); childOutEdge != NULL; childOutEdge = graph_next_out_edge(childOutEdge)) {
        rdp_tree_node_data *grandChildNode = (rdp_tree_node_data*) graph_destination(childOutEdge);

        graph_insert_edge(sizeof(rdp_tree_edge_data), grandChildNode, newLHSNode);
      }
      childNode->deleted = true;
    }
    else
      text_message(TEXT_FATAL, "unexpected closure configuration at node %i labelled %s\n", graph_atom_number(node), node->id);

    return true;
  }

  return false;
}
static rdp_tree_node_data* artMostRecentMajorNode = NULL;

bool artRewriteClosuresLeft(rdp_tree_node_data *node) {
  return artRewriteClosure(artMostRecentMajorNode, node, true);
}

bool artRewriteClosuresRight(rdp_tree_node_data *node){
  return artRewriteClosure(artMostRecentMajorNode, node, false);
}

bool artRewriteBrackets(rdp_tree_node_data *node) {
  return artRewriteBracketsFull(artMostRecentMajorNode, node);
}

void artApplyToProductionBodies(void *tree, bool (action) (rdp_tree_node_data* node)) {
  for (void* moduleEdge = graph_next_out_edge(graph_root(tree)); moduleEdge != NULL; moduleEdge = graph_next_out_edge(moduleEdge)) {
    artMostRecentMajorNode = (rdp_tree_node_data*) graph_destination(moduleEdge);
    for (void* nonterminalEdge = graph_next_out_edge(artMostRecentMajorNode); nonterminalEdge != NULL; nonterminalEdge = graph_next_out_edge(nonterminalEdge)) {
      rdp_tree_node_data *nonterminalNode = (rdp_tree_node_data*) graph_destination(nonterminalEdge);
      for (void* productionEdge = graph_next_out_edge(nonterminalNode); productionEdge != NULL; productionEdge = graph_next_out_edge(productionEdge))
        while (action((rdp_tree_node_data*) graph_destination(productionEdge)))
          ;    // recursively process this production tree one change at a time until there are no changes
    }
  }
}


