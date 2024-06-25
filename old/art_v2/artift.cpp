/* artift.cpp - ART V2 IFTattribute computation routines (c) Adrian Johnstone 2009-2012 */
#include "artaux.h"

static rdp_tree_node_data* artLocateTearSource(const char* tearId, rdp_tree_node_data* stopNode, rdp_tree_node_data* seqNode)
{
  rdp_tree_node_data *tearNode = NULL;

  for (void* thisEdge = graph_next_out_edge(seqNode); thisEdge != NULL; thisEdge = graph_next_out_edge(thisEdge))
  {
    rdp_tree_node_data *thisNode = (rdp_tree_node_data*) graph_destination(thisEdge);

    if (thisNode->atomName != NULL && strcmp(thisNode->atomName->minorID, tearId) == 0)
      tearNode = thisNode;

    if (thisNode == stopNode)
      break;
  }

  return tearNode;
}

void artResolveTearsRec(rdp_tree_node_data* rdpTree) {
  if (rdpTree->kind == ART_TEAR)
  {
    rdp_tree_node_data *tearNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(rdpTree));
    rdp_tree_node_data *parentNode = (rdp_tree_node_data*) graph_source(graph_next_in_edge(rdpTree));
    rdp_tree_node_data *seqNode = (rdp_tree_node_data*) graph_source(graph_next_in_edge(parentNode));

    if (rdpTree->kind == ART_INSERTION)
      text_message(TEXT_ERROR, "$ found outside of insertion\n");

    rdpTree->tearLink = artLocateTearSource(tearNode->id, parentNode, seqNode);
    if (rdpTree->tearLink == NULL)
      text_message(TEXT_ERROR, "unable to resolve tear insertion $%s\n", tearNode->id);
    else
      rdpTree->tableEntry = rdpTree->tearLink->tableEntry;
  }
  else
    for (void *thisEdge = graph_next_out_edge(rdpTree); thisEdge != NULL; thisEdge = graph_next_out_edge(thisEdge))
      artResolveTearsRec((rdp_tree_node_data*) graph_destination(thisEdge));
}

void artComputeSiblingLinksAndAritiesRec(rdp_tree_node_data *node, rdp_tree_node_data *rightSiblingNode) {
  node->rightSibling = rightSiblingNode;

//  printf("ComputeSibling visiting node number %i with sibling node %i\n", graph_atom_number(node), graph_atom_number(rightSiblingNode));
//  if (node->kind != ART_POS)  // Do not give node numbers below pos nodes - only grammar nodes shoul dhave labels...
  for (void *thisEdge = graph_next_out_edge(node); thisEdge != NULL; thisEdge = graph_next_out_edge(thisEdge)) {
    rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(thisEdge);
    void *siblingEdge = graph_next_out_edge(thisEdge);
    rdp_tree_node_data *childRightSiblingNode = (siblingEdge == NULL ? NULL : (rdp_tree_node_data*) graph_destination(siblingEdge));

    artComputeSiblingLinksAndAritiesRec(childNode, childRightSiblingNode);
  }
}

static int uniteSetWithElement(set_ * dst, int element)
{
  if (set_includes_element(dst, element))
    return 0;

  set_unite_element(dst, element);
  return 1;
}

static int uniteSetWithSet(set_ * dst, set_ * src)
{
  if (set_includes_set(dst, src))
    return 0;

  set_unite_set(dst, src);
  return 1;
}

static bool artComputeSetsRec(rdp_tree_node_data *node, int level, rdp_tree_node_data* iftNode, rdp_tree_node_data* bracketNode) {
  bool changed = false;

  if (node == NULL) {
//    text_printf("artComputesetsRec() visiting NULL node at level %i\n", level);
    return changed;
  }

//  text_printf("artComputesetsRec() visiting node %i (%s) at level %i\n", graph_atom_number(node), node->id, level);

  rdp_tree_node_data* newBracketNode = bracketNode;

  // Process our siblings
  changed |= artComputeSetsRec(node->rightSibling, level, iftNode, bracketNode);

  // Pass in closure root
  if (node->kind == ART_DO_FIRST || node->kind == ART_OPTIONAL || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_KLEENE_CLOSURE)
    newBracketNode = node;

//  printf("%i [%i] (\n", graph_atom_number(node), graph_atom_number(bracketNode));

  // Process our children
  if (node->kind != ART_POS) // do not recurse into actions!
    changed |= artComputeSetsRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)), level + 1, iftNode, newBracketNode);

//  printf(")%i\n", graph_atom_number(node));

  // case 0: brackets
  if (node->kind == ART_DO_FIRST || node->kind == ART_OPTIONAL || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_KLEENE_CLOSURE) {
    changed |= uniteSetWithSet(&node->first, &((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)))->first);
    if (node->kind == ART_OPTIONAL || node->kind == ART_KLEENE_CLOSURE)
      changed |= uniteSetWithElement(&node->first, artEpsilonSymbol->number);
    return changed;
  }

  // Case 1: node is either a | or a LHS (at level 2) so form the first set by unioning over the children's initial pos nodes - look out for epsilon!
  if (node->kind == ART_ALT || level == 2) {
    for (void* edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data* childNode = (rdp_tree_node_data*) graph_destination(edge);

      changed |= uniteSetWithSet(&node->first, &childNode->first);
    }
    return changed;
  }

  // Case 2: we are a cat or a unary, so scan across building our first set until we meet a non-nullable child
  if (node->kind == ART_CAT || node->kind == ART_UNARY) {
    // Do the nonterminal nodes first

    rdp_tree_node_data *childNode;
    for (childNode = ((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)))->rightSibling;
         childNode !=NULL;
         childNode = childNode->rightSibling->rightSibling) {
      set_ tmp = SET_NULL;
      set_assign_set(&tmp, &childNode->first);
//      printf("Node %i %s with childNode %i %s and parent %i %s\n", graph_atom_number(node), node->id, graph_atom_number(childNode), childNode->id, graph_atom_number(graph_source(graph_next_in_edge(node))), ((rdp_tree_node_data*) graph_source(graph_next_in_edge(node)))->id);

      if (childNode->rightSibling->rightSibling != NULL)
        set_difference_element(&tmp, artEpsilonSymbol->number);

      changed |= uniteSetWithSet(&node->first, &tmp);

      if (!set_includes_element(&childNode->first, artEpsilonSymbol->number))
        break;
    }

    return changed;
  }

  // Case 3: epsilon
  if (node->kind == ART_EPSILON)
    return changed |= uniteSetWithElement(&node->first, artEpsilonSymbol->number);

  // Case 4: terminal
  if (node->kind == ART_BUILTIN_TERMINAL || node->kind == ART_CHARACTER_TERMINAL || node->kind == ART_CASE_SENSITIVE_TERMINAL || node->kind == ART_CASE_INSENSITIVE_TERMINAL)
    return changed | uniteSetWithElement(&node->first, node->tableEntry->number);

  // Case 5: nonterminal instance
  if (node->kind == ART_NONTERMINAL) {
    changed |= uniteSetWithSet(&node->first, &node->tableEntry->iftNode->first);

    set_ tmp = SET_NULL;
    set_assign_set(&tmp, &node->rightSibling->first);
    set_difference_element(&tmp, artEpsilonSymbol->number);
    changed |= uniteSetWithSet(&node->follow, &tmp);

    if (set_includes_element(&node->rightSibling->first, artEpsilonSymbol->number)) // are we at the end of a rule?
      changed |= uniteSetWithSet(&node->follow, &iftNode->follow);

    changed |= uniteSetWithSet(&node->tableEntry->iftNode->follow, &node->follow);

    return changed;
  }

  // Case 6: pos instance; here we are trying to compute some FIRST(\alpha . \beta); \beta may be \epsilon in which case we
  //         either pick up the context from first( ( \alpha\beta) ) . \gamma) where () stands for any EBNF bracket;
  //         or we  pick up the context from follow(lhs(\alpha.\beta))
  if (node->kind == ART_POS) {
    if (node->rightSibling == NULL) { // \beta is \epsilon
      if (bracketNode == NULL)
        return changed |= uniteSetWithElement(&node->first, artEpsilonSymbol->number);
      else {
        changed |= uniteSetWithSet(&node->first, &bracketNode->rightSibling->first);   // fold in follow for this bracket
        if (bracketNode->kind == ART_KLEENE_CLOSURE || bracketNode->kind == ART_POSITIVE_CLOSURE)
          changed |= uniteSetWithSet(&node->first, &bracketNode->first);   // fold in first set for this bracket on closure only
        return changed;
      }
    }
    else {  // \beta is not epsilon so there will be an X (a terminal or a nonterminal) following, then another pos slot
      set_ tmp = SET_NULL;
      set_assign_set(&tmp, &node->rightSibling->first);
      set_difference_element(&tmp, artEpsilonSymbol->number);  // take out the epsilon

      changed |= uniteSetWithSet(&node->first, &tmp); // unit in first(right sibling) \ #

      if (set_includes_element(&node->rightSibling->first, artEpsilonSymbol->number))
        changed |= uniteSetWithSet(&node->first, &node->rightSibling->rightSibling->first);   // bring over first (alpha  X . beta)

      return changed;
    }
  }

  // Unbuilt cases so far
  if (level == 2 || node->kind == ART_ITER || node->kind == ART_DIFF || node->kind == ART_TEAR || node->kind == ART_INSERTION)
    return changed;

  // Case 7: internal programming error
  text_message(TEXT_FATAL, "unexpected tree node label '%s' with kind %i at level %i found during set computation at RDT node number %i\n", node->id, node->kind, level, graph_atom_number(node));
  return changed; // unreachable because of fatal error message, but the control flow analyser can't tell so we put this in to quieten the compiler warning message
}

void artComputeSets() {
  bool changed = true;

  if (artEOSFollow) {
    for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
         thisSymbol != NULL;
         thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
      if (thisSymbol->kind == ART_NONTERMINAL)
        uniteSetWithElement(&thisSymbol->iftNode->follow, artEndOfStringSymbol->number);
  } else
    uniteSetWithElement(&artStartSymbol->iftNode->follow, artEndOfStringSymbol->number);

  while (changed) {
    changed = false;

    for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
         thisSymbol != NULL;
         thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
      if (thisSymbol->kind == ART_NONTERMINAL && thisSymbol->iftNode != NULL) {
//        text_printf("artComputeSets() on symbol %s.%s with iftNode %i (%s)\n", thisSymbol->majorID, thisSymbol->minorID, graph_atom_number(thisSymbol->iftNode), thisSymbol->iftNode->id);
        rdp_tree_node_data *rightSibling = thisSymbol->iftNode->rightSibling;
        thisSymbol->iftNode->rightSibling = NULL;  // unlink lhs nodes

        changed |= artComputeSetsRec(thisSymbol->iftNode, 2, thisSymbol->iftNode, NULL);

        thisSymbol->iftNode->rightSibling = rightSibling;  // re-link lhs nodes
      }
  }
}

bool artComputeGuardSetsRec(rdp_tree_node_data* node, rdp_tree_node_data* iftNode, rdp_tree_node_data* bracketNode) {
  bool changed = false;
//  text_printf("ComputeGuard sets visiting node %i\n", graph_atom_number(node));
  rdp_tree_node_data* newLhsNode = iftNode;
  rdp_tree_node_data* newBracketNode = bracketNode;

  if (node->kind == ART_LHS)
    newLhsNode = node;

  if (node->kind == ART_DO_FIRST || node->kind == ART_OPTIONAL || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_KLEENE_CLOSURE)
    newBracketNode = node;

  if (node->kind != ART_POS)  // Do not recurse through pos nodes - only annotations and insertions below...
  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(edge);
    changed |= artComputeGuardSetsRec(childNode, newLhsNode, newBracketNode);
  }

  // Note - taken out the guard sets for ()?, ()+ and ()* as they are no longer needed - delete at next version */
  if (node->kind == ART_POS /* || node->kind == ART_OPTIONAL || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_KLEENE_CLOSURE */) {
    set_ tmp = SET_NULL;
    set_assign_set(&tmp, &node->first);
    if (set_includes_element(&tmp, artEpsilonSymbol->number))
      if (newBracketNode == NULL)
        set_unite_set(&tmp, &iftNode->follow);
      else {
        set_unite_set(&tmp, &newBracketNode->rightSibling->guard);
        if (newBracketNode->kind == ART_KLEENE_CLOSURE || newBracketNode->kind == ART_POSITIVE_CLOSURE)
          set_unite_set(&tmp, &newBracketNode->guard);  // For loops, we need the first of the body as well
      }

    set_difference_element(&tmp, artEpsilonSymbol->number);
    changed |= uniteSetWithSet(&node->guard, &tmp);
  }

  // Now compute the guard set for the child of a ()? ()+ or ()* - no need for () as the template doesn't use the test
  // Code is superficially similar to the above but we keep it separate here for pedagogic reasons
  if (node->kind == ART_OPTIONAL || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_KLEENE_CLOSURE) {
    rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(node));
    set_ tmp = SET_NULL;
    set_assign_set(&tmp, &childNode->first);
    if (set_includes_element(&tmp, artEpsilonSymbol->number))
      set_unite_set(&tmp, &node->rightSibling->guard);

    set_difference_element(&tmp, artEpsilonSymbol->number);
    changed |= uniteSetWithSet(&childNode->guard, &tmp);
  }

  return changed;
}

static bool artUpdateLabel(rdp_tree_node_data **target, rdp_tree_node_data *contents) {
  if (*target == contents)
    return false;

//    printf("Changed label to %i\n", graph_atom_number(contents));
  *target = contents;
  return true;
}

static bool artUpdateBool(bool *target, bool contents) {
  if (*target == contents)
    return false;

//    printf("Changed Bool\n");
  *target = contents;
  return true;
}

static rdp_tree_node_data* pL(rdp_tree_node_data *node) {
// We only want the slots immediately before a | (see definition in paper), that is EoA (end of alternate) unless we are at the end of the last sequence in a bracket
  if (node->isEoA && !(node->isEoD || node->isEoO || node->isEoP || node->isEoK))
    if (node->aL == node)
      return node;
    else
      return pL(node->aL);
  if (node->isEoOP)
    return pL(node->nL);
  return node;
}

static bool artComputePlAttributeRec(rdp_tree_node_data *node) {
  if (node == NULL)
    return false;

  bool changed = false;

  if (node->kind != ART_POS)  // Do not recurse through POS nodes
  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    changed |= artComputePlAttributeRec((rdp_tree_node_data*) graph_destination(edge));

  changed |= artUpdateLabel(&node->pL, pL(node));

  return changed;
}

static rdp_tree_node_data* pendingnL;

static bool artComputeAttributesRec(rdp_tree_node_data *node, rdp_tree_node_data *parent, rdp_tree_node_data* LHSNode, rdp_tree_node_data *productionNode, int level) {
//  printf("Entering node %i\n", graph_atom_number(node));
  if (node == NULL)
    return false;

  bool changed = false;

  if (node->isLHS)
    LHSNode = node;

  if ( level == 3)
    productionNode = node;

  if (node->kind != ART_POS) // Do not recurse through POS nodes
    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
      changed |= artComputeAttributesRec((rdp_tree_node_data*) graph_destination(edge), node, LHSNode, productionNode, level + 1);
  else
    node->isPosParentLabel = graph_next_out_edge(node) != NULL;

  // Initialisations
  if (node->lhsL == NULL)
    changed |= artUpdateLabel(&node->lhsL, LHSNode == NULL ? node : LHSNode);
  if (node->niL == NULL)
    changed |= artUpdateLabel(&node->niL, node);
  if(node->nL == NULL)
    changed |= artUpdateLabel(&node->nL, node);  // Some elision here - the paper doesn't define nL for the last node in a nonterminal's productions: let's make it L
  if(node->aL == NULL)
    changed |= artUpdateLabel(&node->aL, node);
  if(node->pL == NULL)
    changed |= artUpdateLabel(&node->pL, node);
  if(node->lrL == NULL)
    changed |= artUpdateLabel(&node->lrL, node);
  if(node->erL == NULL)
    changed |= artUpdateLabel(&node->erL, node);

  // Boolean attribute calculations
  // EoR
  changed |= artUpdateBool(&node->isEoR, (node->kind == ART_POS && level == 4 && node->rightSibling == NULL));

  // EoOP
  if (node->kind == ART_OPTIONAL || node->kind == ART_DO_FIRST)
    changed |= artUpdateBool(&artRightmostElementRec(node)->isEoOP, true);

  if (node->aL->isEoOP)
    changed |= artUpdateBool(&node->isEoOP, true);

  // EoD
  if (node->kind == ART_DO_FIRST)
    changed |= artUpdateBool(&artRightmostElementRec(node)->isEoD, true);

  // EoO
  if (node->kind == ART_OPTIONAL)
    changed |= artUpdateBool(&artRightmostElementRec(node)->isEoO, true);

  // EoP
  if (node->kind == ART_POSITIVE_CLOSURE)
    changed |= artUpdateBool(&artRightmostElementRec(node)->isEoP, true);

  // EoK
  if (node->kind == ART_KLEENE_CLOSURE)
    changed |= artUpdateBool(&artRightmostElementRec(node)->isEoK, true);

  // isColon
  if ((node->kind == ART_KLEENE_CLOSURE || node->kind == ART_OPTIONAL) && set_includes_element(&((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)))->first, artEpsilonSymbol->number))
    changed |= artUpdateBool(&node->rightSibling->isColon, true);

  // fiR now processed in final pass - see below

  //niL
  if (node->kind == ART_NONTERMINAL)
    changed |= artUpdateLabel(&node->rightSibling->niL, node);

  //nL
  if (node->kind == ART_POS && pendingnL != NULL) {
    if (pendingnL->isEoR)
      changed |= artUpdateLabel(&pendingnL->nL, pendingnL);
    else
      changed |= artUpdateLabel(&pendingnL->nL, node);
    pendingnL = NULL;
  }

  if (node->kind == ART_POS)
    pendingnL = node;

  //aL
  if (node->kind == ART_ALT) {
    rdp_tree_node_data *E_r_n = artRightmostElementRec(node);
    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data *endNode = artRightmostElementRec((rdp_tree_node_data*) graph_destination(edge));

      changed |= artUpdateLabel(&endNode->aL, E_r_n);
//      if (endNode != E_r_n) // In this update we have extended isEoA to be true for the end of all alternate including the one without a | following
        changed |= artUpdateBool(&endNode->isEoA, true);
    }
  }

  // isEof

  // isPopD
  if (node->kind == ART_POS && node->rightSibling == NULL)
    changed |= artUpdateBool(&node->isPopD, true);

  if (node->rightSibling != NULL && node->rightSibling->isPopD && (node->kind == ART_POS || node->kind == ART_EPSILON || node->kind == ART_BUILTIN_TERMINAL || node->kind == ART_CHARACTER_TERMINAL || node->kind == ART_CASE_SENSITIVE_TERMINAL || node->kind == ART_CASE_INSENSITIVE_TERMINAL))
    changed |= artUpdateBool(&node->isPopD, true);

  // isPosSelector;
  if (node->kind == ART_POS)
    if (graph_destination(graph_next_out_edge(parent)) != node && node->rightSibling != NULL)
      changed |= artUpdateBool(&node->isPosSelector, true);

  //lrL
  if (node->kind == ART_LHS || (node->kind == ART_ALT && level == 2))
    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data* childNode = (rdp_tree_node_data*) graph_destination(edge);

      changed |= artUpdateLabel(&childNode->lrL, artLeftmostElementRec(childNode));
    }

  else if (node->kind == ART_POS)
    if (node->rightSibling != NULL)
      changed |= artUpdateLabel(&node->rightSibling->lrL, node);

  else if (node->kind == ART_UNARY || node->kind == ART_CAT || node->kind == ART_ALT)
    changed |= artUpdateLabel(&node->lrL, artLeftmostElementRec(node));


  //erL
  if (node->kind == ART_EPSILON || node->kind == ART_NONTERMINAL || node->kind == ART_BUILTIN_TERMINAL || node->kind == ART_CHARACTER_TERMINAL || node->kind == ART_CASE_SENSITIVE_TERMINAL || node->kind == ART_CASE_INSENSITIVE_TERMINAL ||
      node->kind == ART_DO_FIRST || node->kind == ART_OPTIONAL || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_KLEENE_CLOSURE)
    changed |= artUpdateLabel(&node->erL, node->rightSibling);

  if (node->kind == ART_UNARY || node->kind == ART_CAT || node->kind == ART_ALT)
    changed |= artUpdateLabel(&node->erL, artRightmostElementRec(node));

//  printf("Leaving node %i\n", graph_atom_number(node));

  return changed;
}

static void artComputeFiRRec(rdp_tree_node_data *node) {
//  printf("artComputeFiRRec() visiting node %i labelled %s\n", graph_atom_number(node), node->id);

  switch (node->kind) {
    case ART_ROOT:
    case ART_MAJOR:
      { for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))  //New LHS means all children are candidates
        artComputeFiRRec((rdp_tree_node_data*) graph_destination(edge));
      }
     break;
    case ART_LHS:
      { for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))  //New LHS means all children are candidates
        artComputeFiRRec((rdp_tree_node_data*) graph_destination(edge));
      }
      break;

    case ART_DO_FIRST:
      artComputeFiRRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)));
      break;


    case ART_OPTIONAL:
      // Do not descend into optionals
      // Commented out 16/8/16 artComputeFiRRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)));
      break;

    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE: // Do not descend into closures
      break;

    case ART_ALT:
      { for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
        artComputeFiRRec((rdp_tree_node_data*) graph_destination(edge));
      }
      break;

    case ART_UNARY:
    case ART_CAT:  // Call your second child
      artComputeFiRRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(node))));
      break;


    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_BUILTIN_TERMINAL:
    case ART_NONTERMINAL:
    case ART_EPSILON:
    {
#if 1
/* 2016 twin version*/
      // Mods to account for Liz's refinement of fiR
      bool twins = false;

      if (node->rightSibling->rightSibling != NULL) {
        rdp_tree_node_data* successorGrammarNode = node->rightSibling->rightSibling;
        if (successorGrammarNode->kind == ART_NONTERMINAL && successorGrammarNode->tableEntry == node->tableEntry)
          twins = true;
      }
      // End of refinement

      if (!node->rightSibling->isEoR && !node->rightSibling->isEoOP && !(twins && set_includes_element(&node->first, artEpsilonSymbol->number))) {
        node->rightSibling->isFiR = true;
      }
#else
      if (!node->rightSibling->pL->isEoR && !(set_includes_element(&node->first, artEpsilonSymbol->number))) {
        node->rightSibling->isFiR = true;
      }
#endif
}
      break;

    case ART_POS:
    case ART_TEAR:
    case ART_ANNOTATION:
      break;

    default: text_printf("Unexpected node %i labelled '%s' encountered during artComputeFiRRec()\n", graph_atom_number(node), node->id);
      break;
  }
//  printf("artComputeFiRRec() leaving node %i labelled %s\n", graph_atom_number(node), node->id);
}


static void artComputeFiPCLocalRec(rdp_tree_node_data *node) {
//  printf("artComputeFiPCLocalRec() visiting node %i labelled %s\n", graph_atom_number(node), node->id);

  switch (node->kind) {

    case ART_OPTIONAL:
      node->rightSibling->isFiPC = true;
      artComputeFiPCLocalRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)));
      break;
      
    case ART_DO_FIRST:
      artComputeFiPCLocalRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)));
      break;


      break;

    case ART_ALT: // descend into all your children
      { for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
        artComputeFiPCLocalRec((rdp_tree_node_data*) graph_destination(edge));
      }
      break;

    case ART_UNARY:
    case ART_CAT:  // Call your second child
      artComputeFiPCLocalRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(node))));
      break;


    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE:

    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_BUILTIN_TERMINAL:
    case ART_NONTERMINAL:
    case ART_EPSILON:
      node->rightSibling->isFiPC = true;
      break;

    default: text_printf("Unexpected node %i labelled '%s' encountered during artComputeFiPCLocalRec()\n", graph_atom_number(node), node->id);
      break;
  }
//  printf("artComputeFiPCLocalRec() leaving node %i labelled %s\n", graph_atom_number(node), node->id);
}

static void artComputeFiPCRec(rdp_tree_node_data *node) {
//  printf("artComputeFiPCRec() visiting node %i labelled %s\n", graph_atom_number(node), node->id);

  if (node->kind == ART_POSITIVE_CLOSURE) {
    // printf("Detected positive closure\n");
    artComputeFiPCLocalRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)));
  }

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artComputeFiPCRec((rdp_tree_node_data*) graph_destination(edge));
}

/*
 This function computes the isPredictivePop and isPostPredictivePop attributes

 The basic idea is to propogate down from the node until a child isPredictivePop is detected,
 then set the attributes in the child and its right sibling.

*/
static void artComputePredictivePopRec(rdp_tree_node_data *node) {
//  printf("artComputePredictivePopRec() visiting node %i\n", graph_atom_number(node));

  node->isPostPredictivePop = true;
  if (node->rightSibling != NULL)
    node->rightSibling->isPostPredictivePop = true;

  if (node->kind == ART_LHS || node->kind == ART_MAJOR || node->kind == ART_ALT || node->kind == ART_DO_FIRST)  // Propogate to all children
    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
      artComputePredictivePopRec((rdp_tree_node_data*) graph_destination(edge));
  else if (node->kind == ART_CAT || node->kind == ART_UNARY) { // Popogate to final child only as long as it is a terminal, nonterminal or do-first bracket
    rdp_tree_node_data* lastNonPosChildNode = NULL;

    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data* childNode = (rdp_tree_node_data*) graph_destination(edge);

      if (childNode->kind != ART_POS)
        lastNonPosChildNode = childNode;
    }

//    printf("Under CAT/UNARY node %i, last non-pos node is %i\n", graph_atom_number(node), graph_atom_number(lastNonPosChildNode));

    if (lastNonPosChildNode->kind == ART_DO_FIRST)
      artComputePredictivePopRec(lastNonPosChildNode);
    else
      lastNonPosChildNode->rightSibling->isPredictivePop = true;
  }
}

void artComputeIsEFBNFRec(rdp_tree_node_data *node) {
  if (node->kind == ART_KLEENE_CLOSURE || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_OPTIONAL || (node->kind == ART_DO_FIRST && !(node->isPredictivePop || node->isPostPredictivePop)))
    artGrammarIsFBNF = false;

  if (node->kind == ART_KLEENE_CLOSURE || node->kind == ART_POSITIVE_CLOSURE || node->kind == ART_OPTIONAL || node->kind == ART_DO_FIRST)
    artGrammarIsEBNF = true;

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artComputeIsEFBNFRec((rdp_tree_node_data*) graph_destination(edge));
}

int attributeClosurePasses;
void artComputeAttributes() {

  bool changed = true;
  attributeClosurePasses = 0;

  while (changed) {
    changed = false;
    attributeClosurePasses++;

    for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym)) {
      if (sym->kind != ART_MAJOR)
        continue;

      pendingnL = NULL;
      changed |= artComputeAttributesRec(sym->iftNode, NULL, NULL, NULL, 1);
      changed |= artComputePlAttributeRec(sym->iftNode);
    }
  }

//  printf("%i attribute closure passes\n", attributeClosurePasses);

  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym)) {
    if (sym->kind != ART_MAJOR)
      continue;
    artComputeFiRRec(sym->iftNode);
    artComputeFiPCRec(sym->iftNode);
    artComputePredictivePopRec(sym->iftNode);
  }

  artGrammarIsFBNF = true;
  artGrammarIsEBNF = false;

  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym)) {
    if (sym->kind != ART_MAJOR)
      continue;
    artComputeIsEFBNFRec(sym->iftNode);
  }
}

bool artMergedSet(set_ *s) {
  mergedSets_data* retValue = (mergedSets_data*) symbol_lookup_key(mergedSets, s, NULL);

  return retValue != NULL;
}

int artMergedSetNumber(set_ *s) {
//  if (s->elements == NULL)
//    return 0;

  mergedSets_data* retValue = (mergedSets_data*) symbol_find(mergedSets, s, sizeof(set_), sizeof(mergedSets_data), NULL, SYMBOL_ANY);

  if (retValue->number == 0) {
    text_message(TEXT_ERROR, "unexpected attempt to find new set on mergedSets table: ");
    set_print_set(s, NULL, 0);
    text_printf("\n");
  }

  return retValue->number;
}

// Updated December 2013 to include all sets so as to support tree dump, and to set usedInParser true as appropriate
void artComputeMergedSets(void *tree) {
  for (symbols_data *thisLhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisLhsSymbol != NULL;
       thisLhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisLhsSymbol))
    if (thisLhsSymbol->kind == ART_NONTERMINAL && thisLhsSymbol->iftNode != NULL) { // add follow sets for nonterminals
      ((mergedSets_data*) symbol_find(mergedSets, (&thisLhsSymbol->iftNode->follow), sizeof(set_), sizeof(mergedSets_data), NULL, SYMBOL_ANY))->usedInParser = 1;  // Put nonterminal follow sets in as well

      if (set_cardinality(&thisLhsSymbol->iftNode->first) == 0)
        text_message(TEXT_ERROR, "Nonterminal '%s' has empty first set\n", thisLhsSymbol->minorID);

      symbol_find(mergedSets, (&thisLhsSymbol->iftNode->first), sizeof(set_), sizeof(mergedSets_data), NULL, SYMBOL_ANY);
    }

  for (rdp_tree_node_data *node = (rdp_tree_node_data*) graph_next_node(tree); node != NULL; node = (rdp_tree_node_data*) graph_next_node(node)) {
    if (set_cardinality(&node->guard) != 0)
      ((mergedSets_data*) symbol_find(mergedSets, &node->guard, sizeof(set_), sizeof(mergedSets_data), NULL, SYMBOL_ANY))->usedInParser = 1;

    symbol_find(mergedSets, (&node->first), sizeof(set_), sizeof(mergedSets_data), NULL, SYMBOL_ANY);
    symbol_find(mergedSets, (&node->follow), sizeof(set_), sizeof(mergedSets_data), NULL, SYMBOL_ANY);
    symbol_find(mergedSets, (&node->guard), sizeof(set_), sizeof(mergedSets_data), NULL, SYMBOL_ANY);
  }

  artMergedSetCount = 1;  // Keep zero as illegal for defensive programming purposes

  for (mergedSets_data *thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(symbol_get_scope(mergedSets));
       thisSet != NULL;
       thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(thisSet))
    thisSet->number = artMergedSetCount++;
}

