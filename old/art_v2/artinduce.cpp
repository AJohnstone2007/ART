/* artinduce.cpp - ART V2 grammar induction routines(c) Adrian Johnstone 2009-2012 */
#include <stdlib.h>
#include "artaux.h"
//#define INDUCE_TRACE

int artFirstFreeSymbolNumber = 0;
static int artFirstMajor;
static void artNumberSymbolsPass(enum artKind kind)
{
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    if (thisSymbol->kind == kind)
      thisSymbol->number = artFirstFreeSymbolNumber++;
}

static void artNumberSymbols(void)
{
  artFirstFreeSymbolNumber = 0;

  symbol_sort_scope(symbols, symbol_get_scope(symbols));

  artNumberSymbolsPass(ART_END_OF_STRING);
  artNumberSymbolsPass(ART_BUILTIN_TERMINAL);
  artNumberSymbolsPass(ART_CHARACTER_TERMINAL);
  artNumberSymbolsPass(ART_CASE_SENSITIVE_TERMINAL);
  artNumberSymbolsPass(ART_CASE_INSENSITIVE_TERMINAL);
  artNumberSymbolsPass(ART_EPSILON);
  artNumberSymbolsPass(ART_NONTERMINAL);
  artFirstMajor = artFirstFreeSymbolNumber;
  artNumberSymbolsPass(ART_MAJOR);
  artNumberSymbolsPass(ART_POS_NAME);
  artNumberSymbolsPass(ART_ATTRIBUTE);
}

static bool artRewriteSetEqual(symbols_data **left, symbols_data **right) {
  for( ; *left != NULL && *right != NULL; left++, right++)
    if (*left != *right)
      return false;

  if (*left != *right)
    return false;

  return true;
}

struct iPrimeNode {const char* lhsMajorID; const char* lhsMinorID; const char* rhsMajorID; const char* rhsMinorID; symbols_data **rewriteSet; bool contingentFlag; struct iPrimeNode *next;} *iPrime;

static struct iPrimeNode* uniteImport(const char* lhsMajorID, const char* lhsMinorID, const char* rhsMajorID, const char* rhsMinorID, symbols_data **rewriteSet, bool contingentFlag) {
#if defined INDUCE_TRACE
  printf("uniteImport(%s.%s, %s.%s, (", lhsMajorID, lhsMinorID, rhsMajorID, rhsMinorID);
  for (symbols_data **tmp = rewriteSet; *tmp != NULL; tmp++)
    printf("%s", (*tmp)->majorID);
  printf(" ), %s) generates ", contingentFlag ? "CTrue" : "CFalse");
#endif

  iPrimeNode *tmp;
  for (tmp = iPrime; tmp->next != NULL; tmp = tmp->next)
   if (strcmp(lhsMajorID, tmp->lhsMajorID) == 0 &&
       strcmp(lhsMinorID, tmp->lhsMinorID) == 0 &&
       strcmp(rhsMajorID, tmp->rhsMajorID) == 0 &&
       strcmp(rhsMinorID, tmp->rhsMinorID) == 0 &&
       artRewriteSetEqual(rewriteSet, tmp->rewriteSet) &&
       contingentFlag == tmp->contingentFlag
      ) {
#if defined INDUCE_TRACE
     artWriteImport(tmp);
     printf(" (OLD)\n");
#endif
     return NULL;
  }

  tmp = new iPrimeNode;
  tmp->lhsMajorID = lhsMajorID;
  tmp->lhsMinorID = lhsMinorID;
  tmp->rhsMajorID = rhsMajorID;
  tmp->rhsMinorID = rhsMinorID;
  tmp->rewriteSet = rewriteSet;
  tmp->contingentFlag = contingentFlag;
  tmp->next = iPrime;
  iPrime = tmp;

#if defined INDUCE_TRACE
  artWriteImport(tmp);
  printf(" (NEW)\n");
#endif

  return tmp;
}

static int artCompPointer(const void *left, const void* right) {
  if (*((void**) left) > *((void**) right) )
    return 1;
  if (*((void**) left) < *((void**) right) )
    return -1;
  return 0;
}

static void artInduceProcessConcreteImport(const char* majorID, rdp_tree_node_data *importNode, bool isWhiteSpace) {
  symbols_data *majorSymbol = artFind(ART_MAJOR, majorID, majorID, NULL);

  if (importNode == NULL) // say, for empty whitespace declaration
    return;

  const char* lhsMajorID = "";
  const char* lhsMinorID = "";
  const char* rhsMajorID = "";
  const char* rhsMinorID = "";

  rdp_tree_node_data *importOpNode;
  rdp_tree_node_data *rewriteSetNode = NULL;
  bool isStarImport = false;          // default is <-
  bool isDoubleArrowImport = false;   // default is <-

  // Collect import parameters, rearranging as required due to parsing constraints
  for (void *edge = graph_next_out_edge(importNode); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(edge);

    if (childNode->token == 0 && strcmp(childNode->id, "importLHS") == 0) {
      rdp_tree_node_data *firstFieldNode = artFirstChildOf(childNode);

      if (graph_next_out_edge(firstFieldNode) == NULL) { // only one field?
        rhsMajorID = firstFieldNode->id;
        rhsMinorID = "";
      }
      else {
        rhsMajorID = firstFieldNode->id;
        rhsMinorID = artFirstChildOf(firstFieldNode)->id;
      }
    }
    else if (childNode->token == 0 && strcmp(childNode->id, "importOp") == 0) {
      importOpNode = artFirstChildOf(childNode);

      switch (importOpNode->token) {
        case RDP_T_6045 /* <- */: break;
        case RDP_T_604542 /* <-* */: isStarImport = true; break;
        case RDP_T_6061 /* <= */:    isDoubleArrowImport = true; break;
        case RDP_T_606142 /* <=* */: isStarImport = true; isDoubleArrowImport = true; break;
        default: text_message(TEXT_FATAL, "illegal import operator token %i '%s'\\n", importOpNode->token, importOpNode->id); break;
      }
    }
    else if (childNode->token == 0 && strcmp(childNode->id, "rewriteSet") == 0) {
      rewriteSetNode = childNode;
    }
    else if (childNode->token == 0 && strcmp(childNode->id, "importRHS") == 0) {
      lhsMajorID = rhsMajorID;
      lhsMinorID = rhsMinorID;

      rdp_tree_node_data *firstFieldNode = artFirstChildOf(childNode);

      if (graph_next_out_edge(firstFieldNode) == NULL) { // only one field?
        rhsMajorID = firstFieldNode->id;
        rhsMinorID = "";
      }
      else {
        rhsMajorID = firstFieldNode->id;
        rhsMinorID = artFirstChildOf(firstFieldNode)->id;
      }
    }
    else
      text_message(TEXT_FATAL, "unexpected node %i labelled %s found during processing of concrete import\n", graph_atom_number(childNode), childNode->id);
  }

  // Some error checking
  if (*lhsMinorID != 0) {
    text_message(TEXT_ERROR, "illegal import %s (%s.%s <- %s.%s): \n", majorID, lhsMajorID, lhsMinorID, rhsMajorID, rhsMinorID);
    return;
  }

  lhsMinorID = lhsMajorID;
  lhsMajorID = (char*) majorID;

  if (*lhsMinorID != 0 && *rhsMinorID == 0)
    text_message(TEXT_WARNING, "import (%s.%s <-%s %s.%s) may attach productions from multiple nonterminals\n", lhsMajorID, lhsMinorID, isStarImport ? "*" : "", rhsMajorID, lhsMinorID);

  // Count rewrites and check for repeats
  int rewriteSetCardinality = 0;
  if (rewriteSetNode != NULL) {
    for (void *edge1 = graph_next_out_edge(rewriteSetNode); edge1 != NULL; edge1 = graph_next_out_edge(edge1)) {
      const char* majorID1 = ((rdp_tree_node_data*) graph_destination(edge1))->id;
      symbols_data *symbol1 = artFind(ART_MAJOR, majorID1, majorID1, NULL);
      rewriteSetCardinality++;

      for (void *edge2 = graph_next_out_edge(edge1); edge2 != NULL; edge2 = graph_next_out_edge(edge2)) {
        const char* majorID2 = ((rdp_tree_node_data*) graph_destination(edge2))->id;
        symbols_data *symbol2 = artFind(ART_MAJOR, majorID2, majorID2, NULL);

        if (symbol1 == symbol2)
          text_message(TEXT_FATAL, "rewrite set has repeated element '%s' in import %s.%s %s %s.%s\n", symbol1->minorID, majorID, lhsMinorID, importOpNode->id, rhsMajorID, rhsMinorID);
      }
    }
  }

  // Now load rewrite symbol pointers into the array rewriteSet
  symbols_data **rewriteSet = (symbols_data**) mem_calloc(rewriteSetCardinality + 2, sizeof(symbols_data*));  // Plus two to allow for final NULL plus extra element for * operators
  rewriteSetCardinality = 0;

  if (isDoubleArrowImport)
    rewriteSet[rewriteSetCardinality++] = artFind(ART_MAJOR, rhsMajorID, rhsMajorID, NULL);  // Add in the RHS module for => and =>* imports

  if (rewriteSetNode != NULL) {
    for (void *edge = graph_next_out_edge(rewriteSetNode); edge != NULL; edge = graph_next_out_edge(edge)) {
      const char* majorID = ((rdp_tree_node_data*) graph_destination(edge))->id;
      rewriteSet[rewriteSetCardinality++] = artFind(ART_MAJOR, majorID, majorID, NULL);
    }
  }
  rewriteSet[rewriteSetCardinality] = 0;  // not strictly necessary since we calloced - here as defensive programming in case we move to new()

  // Normalise the ordering of the elements so that set comparison is linear time
  qsort(rewriteSet, rewriteSetCardinality, sizeof(symbols_data*), artCompPointer);

  iPrimeNode *iPrimeEntry = uniteImport(lhsMajorID, lhsMinorID, rhsMajorID, rhsMinorID, rewriteSet, isStarImport);

  if (isWhiteSpace)
    majorSymbol->whiteSpaceImport = iPrimeEntry;
}

static void artInduceProcessOption(const char* majorID, rdp_tree_node_data *actionNode) {
/* This needs redoing now that annotations are strings! This is just a bunch of kludges */

  for (void *edge = graph_next_out_edge(actionNode); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data* flagNode = (rdp_tree_node_data*) graph_destination(edge);

    text_printf("Major %s has option %s\n", majorID, flagNode->id);

    if (strcmp(flagNode->id, " closureLeft ") == 0)
      artClosureLeft = true;
    else if (strcmp(flagNode->id, " closureRight ") == 0)
      artClosureRight = true;
    else if (strcmp(flagNode->id, " leftFactor ") == 0)
      artLeftFactor = true;
    else if (strcmp(flagNode->id, " multiplyOut ") == 0)
      artMultiplyOut = true;
    else if (strcmp(flagNode->id, " multiplyOut leftFactor ") == 0) {
      artMultiplyOut = true; artLeftFactor = true;
    }
    else
      text_message(TEXT_FATAL, "unknown option '%s' for major %s\n", flagNode->id, majorID);
  }
}

/* Start of production and deleter processing *********************************/
enum artProductionKind {ART_PK_P, ART_PK_PPRIME, ART_PK_D};

static bool uniteProduction(artProductionKind mode, void *PGraph, void* PPrimeGraph, void* DGraph, const char* lhsMajorID, const char* lhsMinorID, rdp_tree_node_data* rhs) {

  symbols_data *majorSymbol = artFind(ART_MAJOR, lhsMajorID, lhsMajorID, NULL);
  if (majorSymbol->PNode == NULL) {
#if defined INDUCE_TRACE
    printf("New major symbol '%s'\n", lhsMajorID);
#endif
    majorSymbol->PNode = artNewTreeNode((rdp_tree_node_data*) graph_root(PGraph), lhsMajorID, ART_MAJOR, majorSymbol);
    majorSymbol->PPrimeNode = artNewTreeNode((rdp_tree_node_data*) graph_root(PPrimeGraph), lhsMajorID, ART_MAJOR, majorSymbol);
    majorSymbol->DNode = artNewTreeNode((rdp_tree_node_data*) graph_root(DGraph), lhsMajorID, ART_MAJOR, majorSymbol);
  }

  symbols_data *nonterminalSymbol = artFind(ART_NONTERMINAL, lhsMajorID, lhsMinorID, NULL);
  nonterminalSymbol->defined = true;
  if (nonterminalSymbol->PNode == NULL) {
#if defined INDUCE_TRACE
    printf("New nonterminal symbol '%s.%s'\n", lhsMajorID, lhsMinorID);
#endif
    nonterminalSymbol->PNode = artNewTreeNode(majorSymbol->PNode, lhsMinorID, ART_LHS, nonterminalSymbol);
    nonterminalSymbol->PNode->isLHS = true;

    nonterminalSymbol->PPrimeNode = artNewTreeNode(majorSymbol->PPrimeNode, lhsMinorID, ART_LHS, nonterminalSymbol);
    nonterminalSymbol->PPrimeNode->isLHS = true;

    nonterminalSymbol->DNode = artNewTreeNode(majorSymbol->DNode, lhsMinorID, ART_LHS, nonterminalSymbol);
    nonterminalSymbol->DNode->isLHS = true;
  }

  const char* productionKindString = "???";
  rdp_tree_node_data *activeTreeRoot = NULL;

  switch (mode) {
    case ART_PK_P: productionKindString = "P"; activeTreeRoot = nonterminalSymbol->PNode; break;
    case ART_PK_PPRIME: productionKindString = "P'"; activeTreeRoot = nonterminalSymbol->PPrimeNode; break;
    case ART_PK_D: productionKindString = "D"; activeTreeRoot = nonterminalSymbol->DNode; break;
  }

#if defined INDUCE_TRACE
  printf("uniteProduction(%s) %s.%s ::=", productionKindString, lhsMajorID, lhsMinorID);
  artWriteGrammarRec(rhs, 1);
  printf(" ;");
#endif

  for (void *edge = graph_next_out_edge(activeTreeRoot); edge != NULL; edge = graph_next_out_edge(edge)) {
    if (artEqualSubtreeRec(false, (rdp_tree_node_data *) graph_destination(edge), rhs)) {
#if defined INDUCE_TRACE
      printf(" (OLD)\n");
#endif
      return false;  // Found so no change
    }
  }

  artCloneTreeAsChildOf(activeTreeRoot, rhs);

#if defined INDUCE_TRACE
  printf(" (NEW)\n");
#endif
  return true;  // Changed
}

void artWriteImport(iPrimeNode *node) {
  if (node == NULL)
    text_printf("undefined")
    ;
  else {
    text_printf("(%s.%s <-%s", node->lhsMajorID, node->lhsMinorID, node->contingentFlag ? "*" : "");
    if (*(node->rewriteSet) != 0) {
      printf(" (");
      for (symbols_data **s = node->rewriteSet; *s != NULL; s++)
        text_printf(" %s", (*s)->minorID);
      printf(" )");
    }
    text_printf(" %s.%s)", node->rhsMajorID, node->rhsMinorID);
  }
}

static void artWriteImportSet(struct iPrimeNode *base) {
  for (struct iPrimeNode *tmp = base; tmp->next != NULL; tmp = tmp->next){
    text_printf("\n");
    artWriteImport(tmp);
  }
}

static void artInduceInitialise(void *RDT, void* PGraph, void* PPrimeGraph, void* DGraph, void* ZGraph) {
  bool first = true;
  symbols_data *majorSymbol = NULL;

  iPrime = new iPrimeNode; iPrime->next = NULL;

  for (void *edge = graph_next_out_edge(graph_root(RDT)); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data *kindNode = (rdp_tree_node_data*) graph_destination(edge);

    rdp_tree_node_data *idNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(kindNode));

    if (strcmp(kindNode->id, "major") == 0) {
      rdp_tree_node_data *whiteSpaceNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(kindNode)));
      rdp_tree_node_data *importsNode =    (rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(kindNode))));
      rdp_tree_node_data *startNode =      (rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(kindNode)))));
      rdp_tree_node_data *optionsNode =    (rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(graph_next_out_edge(kindNode))))));

      // Create new symbol
      majorSymbol = artFind(ART_MAJOR, idNode->id, idNode->id, NULL);
      if (majorSymbol->defined)
        text_message(TEXT_ERROR, "%s() is multiply defined\n", idNode->id);
      majorSymbol->defined = true;

      /* Whitespace processing */
      rdp_tree_node_data* firstWhitespaceChild = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(whiteSpaceNode));
      if (firstWhitespaceChild != NULL && firstWhitespaceChild->kind == 0 &&
            (strcmp(firstWhitespaceChild->id, "builtinTerm") == 0 || strcmp(firstWhitespaceChild->id, "charTerm") == 0 || strcmp(firstWhitespaceChild->id, "epsilon") == 0)
         )
        majorSymbol->builtinWhitespaceNode = whiteSpaceNode;
      else
        artInduceProcessConcreteImport(idNode->id, firstWhitespaceChild, true);

      /* Import processing */
      for (void *edge = graph_next_out_edge(importsNode); edge != NULL; edge = graph_next_out_edge(edge))
        artInduceProcessConcreteImport(idNode->id, (rdp_tree_node_data*) graph_destination(edge), false);

      /* Start node processing for this major */
      if (graph_next_out_edge(startNode) != NULL) {
        majorSymbol->startSymbol = artFind(ART_NONTERMINAL, idNode->id, ((rdp_tree_node_data*) graph_destination(graph_next_out_edge(startNode)))->id, NULL);
#if defined INDUCE_TRACE
        printf("Found start symbol %s.%s\n", majorSymbol->startSymbol->majorID, majorSymbol->startSymbol->minorID);
#endif
      }

      /* Options processing */
      artInduceProcessOption(idNode->id, (rdp_tree_node_data*) graph_destination(graph_next_out_edge(optionsNode)));

      if (first) {
        artMainMajorSymbol = majorSymbol;
        artStartSymbol = majorSymbol->startSymbol;
        first = false;
      }
    }
    else if (strcmp(kindNode->id, "production") == 0 || strcmp(kindNode->id, "deleter") == 0) {
      rdp_tree_node_data *lhsNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(kindNode));

      if (artWarnOnMultiple && artFind(ART_NONTERMINAL, majorSymbol->majorID, lhsNode->id, NULL)->PNode != NULL)
        text_message(TEXT_WARNING, "nonterminal %s has multiple LHS instances in module %s\n", lhsNode->id, majorSymbol->majorID);


      rdp_tree_node_data *rhsNode = NULL;

      // Scan for body node
      for (void *edge = graph_next_out_edge(kindNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data* childNode = (rdp_tree_node_data*) graph_destination(edge);

        if (childNode->token == 0 && strcmp(childNode->id, "body") == 0)
          rhsNode = childNode;
      }

      if (rhsNode == NULL)
        text_message(TEXT_FATAL, " no RHS body node found for nonterminal");

      for (void *edge = graph_next_out_edge(rhsNode); edge != NULL; edge = graph_next_out_edge(edge))
        uniteProduction(strcmp(kindNode->id, "deleter") == 0 ? ART_PK_D : ART_PK_P, PGraph, PPrimeGraph, DGraph, majorSymbol->minorID, lhsNode->id, (rdp_tree_node_data*) graph_destination(edge));
    }

    else if (strcmp(kindNode->id, "disambiguator") == 0 || strcmp(kindNode->id, "lexical") == 0 || strcmp(kindNode->id, "prelude") == 0 || strcmp(kindNode->id, "support") == 0) {
      /* skip disambiguator, lexical, prelude and support declarations*/
    }
    else text_message(TEXT_FATAL, "at RDT level 2, found an unexpected node %i labelled '%s'\n", graph_atom_number(kindNode), kindNode->id);
  }

#if defined INDUCE_TRACE
  text_printf("After initialisation, import set is:");
  artWriteImportSet(iPrime);
  text_printf("\n");
#endif

  // We now have D, P and I' in its imitial state, i.e. after completion of step 1 processing

  // Step 2: M.X ::= \rho in P => M.X ::= T(\rho, W_M) in P'
  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
    if (sym->kind == ART_NONTERMINAL) {
      symbols_data *majorSymbol = artFind(ART_MAJOR, sym->majorID, sym->majorID, NULL);
      symbols_data *whiteSpaceSymbol = NULL;

      if (majorSymbol->whiteSpaceImport != NULL)
        whiteSpaceSymbol = artFind(ART_NONTERMINAL, majorSymbol->whiteSpaceImport->lhsMajorID, majorSymbol->whiteSpaceImport->lhsMinorID, NULL);

      if (sym == whiteSpaceSymbol)  // Don't add terminal whitespace to the whitespace nonterminals productions!
        whiteSpaceSymbol = NULL;

      for (void *edge = graph_next_out_edge(sym->PNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        rdp_tree_node_data* zRoot = artNewTreeNode((rdp_tree_node_data*) graph_root(ZGraph), "newZRoot", ART_NONTERMINAL, NULL);
        artCloneTreeAsChildOf(zRoot, (rdp_tree_node_data*) graph_destination(edge), whiteSpaceSymbol, NULL, NULL, NULL, NULL);
        uniteProduction(ART_PK_PPRIME, PGraph, PPrimeGraph, DGraph, sym->majorID, sym->minorID, artFirstChildOf(zRoot));
      }
    }
}

static bool artWhiteSpaceEquivalent(iPrimeNode *leftImport, iPrimeNode *rightImport) {
  bool retValue;

#if defined INDUCE_TRACE
  printf("artWhiteSpaceEquivalent("); artWriteImport(leftImport); printf("), ("); artWriteImport(rightImport); printf(")");
#endif

  if (leftImport == NULL && rightImport == NULL)
    retValue = true;
  else if (leftImport == NULL || rightImport == NULL)
    retValue = false;
  else {
    symbols_data *leftExport = artFind(ART_NONTERMINAL, leftImport->rhsMajorID, leftImport->rhsMinorID, NULL);
    symbols_data *rightExport = artFind(ART_NONTERMINAL, rightImport->rhsMajorID, rightImport->rhsMinorID, NULL);

    retValue = leftExport == rightExport;
  }

#if defined INDUCE_TRACE
  printf(" returns %s\n", retValue ? "true" : "false");
#endif

  return retValue;
}

static bool artContingentImportRec(rdp_tree_node_data *node, const char* lhsMajorID, const char* lhsMinorID, symbols_data **rewriteSet) {
#if defined INDUCE_TRACE
  printf("artContigentImportRec() on node %i kind %i labelled %s with lhsMajorID %s, lhsMinorID %s\n", graph_atom_number(node), node->kind, node->id, lhsMajorID, lhsMinorID);
#endif
  int changed = false;

  // Check children first
  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    changed |= artContingentImportRec((rdp_tree_node_data*) graph_destination(edge), lhsMajorID, lhsMinorID, rewriteSet);

  if (node->kind == ART_NONTERMINAL) {
#if defined INDUCE_TRACE
  printf("Located RHS nonterminal\n");
#endif
    symbols_data *majorSymbol = artFind(ART_MAJOR, node->tableEntry->majorID, node->tableEntry->majorID, NULL);

    bool found = false;
    for (symbols_data **tmp = rewriteSet; *tmp != NULL; tmp++) {
#if defined INDUCE_TRACE
  printf("Testing majorSymbol %s.%s against %s.%s\n", majorSymbol->majorID, majorSymbol->minorID, (*tmp)->majorID, (*tmp)->minorID);
#endif
      found |= (*tmp == majorSymbol);
    }

    if (found) {
#if defined INDUCE_TRACE
  printf("found\n");
#endif
      changed |= (uniteImport(lhsMajorID, node->tableEntry->minorID, node->tableEntry->majorID, node->tableEntry->minorID, rewriteSet, true) != NULL);
    }
    else {
#if defined INDUCE_TRACE
  printf("not found\n");
#endif
    }
  }

  return changed;
}

static void artInduceClosure(void* PGraph, void* PPrimeGraph, void *DGraph, void *ZGraph) {
  bool changed = true;

  while (changed) {
    changed = false;

    for (iPrimeNode *import = iPrime; import->next != NULL; import = import->next) {
#if defined INDUCE_TRACE
      printf("Closure processing on import "); artWriteImport(import); printf("\n");
#endif

      // Steps 1 and 2 done during initialisation

      // Step 3: (M. <- L.X, R, C) in I' => (M.X <- L.X, R, C) in I'
      if (*(import->lhsMajorID) != 0 && *(import->lhsMinorID) == 0 && *(import->rhsMajorID) != 0 && *(import->rhsMinorID) != 0)
        changed |= (uniteImport(import->lhsMajorID, import->rhsMinorID, import->rhsMajorID, import->rhsMinorID, import->rewriteSet, import->contingentFlag) != NULL);

      // Step 4: (M. <- L., R, C) in I', L.Y ::= rho in P' => (M.Y <- L.Y, R, C) in I'
      // NB we are constructing the import expressions during induction, rather than simply creating $(M.Z\gets L.Z, R, C), Z\in N$ as specified in the definition. As soon as we find any $L.Y\rightarrow\rho$ we can stop searching $P$.},
      if (*(import->lhsMajorID) != 0 && *(import->lhsMinorID) == 0 && *(import->rhsMajorID) != 0 && *(import->rhsMinorID) == 0) {
        // Walk all nonterminals in major L and add a fully qualidied import
        for (void *edge = graph_next_out_edge(artFind(ART_MAJOR, import->rhsMajorID, import->rhsMajorID, NULL)->PPrimeNode); edge != NULL; edge = graph_next_out_edge(edge))
          changed |= (uniteImport(import->lhsMajorID, ((rdp_tree_node_data*) graph_destination(edge))->id, import->rhsMajorID, ((rdp_tree_node_data*) graph_destination(edge))->id, import->rewriteSet, import->contingentFlag) != NULL);
      }

      // Step 5: (M.Y <- L.X, R, false) in I', L.X ::= \rho in P', M.Y :/= S(\rho) not in D =>
      //         M.Y ::= I(\rho, R, M, W(M.Y), W(L.X)) in P'
      // Only do this for full qualified imports!
      if (*(import->lhsMajorID) != 0 && *(import->lhsMinorID) != 0 && *(import->rhsMajorID) != 0 && *(import->rhsMinorID) != 0) {
#if defined INDUCE_TRACE
        printf("Unqualified closure processing on import "); artWriteImport(import); printf("\n");
#endif
        // Iterate over L.X's productions
        symbols_data *LX = artFind(ART_NONTERMINAL, import->rhsMajorID, import->rhsMinorID, NULL);
        symbols_data *MY = artFind(ART_NONTERMINAL, import->lhsMajorID, import->lhsMinorID, NULL);

        for (void *edge = graph_next_out_edge(LX->PPrimeNode); edge != NULL; edge = graph_next_out_edge(edge)) {
          rdp_tree_node_data* zRoot = artNewTreeNode((rdp_tree_node_data*) graph_root(ZGraph), "newZRoot", ART_NONTERMINAL, NULL);

          // Wrapper whitespace processing
          symbols_data *lhsMajorSymbol = artFind(ART_MAJOR, import->lhsMajorID, import->lhsMajorID, NULL);
          symbols_data *rhsMajorSymbol = artFind(ART_MAJOR, import->rhsMajorID, import->rhsMajorID, NULL);
          iPrimeNode *lhsWhiteSpaceImport = lhsMajorSymbol->whiteSpaceImport;
          iPrimeNode *rhsWhiteSpaceImport = rhsMajorSymbol->whiteSpaceImport;
          symbols_data *leadingWhiteSpaceSymbol = NULL;
          symbols_data *trailingWhiteSpaceSymbol = NULL;

          // Add check to see if the target nonterminal is our whitespace nontermial!

          if (!artWhiteSpaceEquivalent(lhsWhiteSpaceImport, rhsWhiteSpaceImport)) {
            if (lhsWhiteSpaceImport != NULL)
              trailingWhiteSpaceSymbol = artFind(ART_NONTERMINAL, lhsWhiteSpaceImport->lhsMajorID, lhsWhiteSpaceImport->lhsMinorID, NULL);
            if (rhsWhiteSpaceImport != NULL)
              leadingWhiteSpaceSymbol = artFind(ART_NONTERMINAL, rhsWhiteSpaceImport->lhsMajorID, rhsWhiteSpaceImport->lhsMinorID, NULL);
          }

          if (MY == trailingWhiteSpaceSymbol) // We are importing to the whitespace symbol, so don't use wrapper whitespace
            trailingWhiteSpaceSymbol = leadingWhiteSpaceSymbol = NULL;

          if (leadingWhiteSpaceSymbol != NULL)
            for (symbols_data **tmp = import->rewriteSet; *tmp != 0; tmp++)
              if (*tmp == rhsMajorSymbol)  // We need to change the major name of the rewrite symbol
                leadingWhiteSpaceSymbol = artFind(ART_NONTERMINAL, import->lhsMajorID, rhsWhiteSpaceImport->lhsMinorID, NULL);

          rdp_tree_node_data *originalProductionTree = (rdp_tree_node_data*) graph_destination(edge);

          artCloneTreeAsChildOf(zRoot, originalProductionTree, NULL, import->rewriteSet, import->lhsMajorID, leadingWhiteSpaceSymbol, trailingWhiteSpaceSymbol);

          // If no matching deleter, fold back into P'

          rdp_tree_node_data* productionTree = artFirstChildOf(zRoot);
          bool matchingDeleter = false;

          for (void *edge = graph_next_out_edge(MY->DNode); edge != NULL; edge = graph_next_out_edge(edge))
            matchingDeleter |= artEqualSubtreeRec(true, productionTree, (rdp_tree_node_data*) graph_destination(edge));

          if (!matchingDeleter)
            changed |= uniteProduction(ART_PK_PPRIME, PGraph, PPrimeGraph, DGraph, import->lhsMajorID, import->lhsMinorID, productionTree);

          if (import->contingentFlag) {
#if defined INDUCE_TRACE
  printf("Performing contingent import traversal\n");
#endif
            // Flow through to...
            // Step 6: (M.Y <- L.X, R, true ) in I', L.X ::= \rho in P', M.Y :/= S(\rho) not in D =>
            //         M.Y ::= I(\rho, R, M, W(M.Y), W(L.X)) in P'
            //         \rho uses Q.Z, Q in R => (M.Y <- Q.Z, R, true) in I'

            // Walk the production tree, adding contingent imports
            changed |= artContingentImportRec(originalProductionTree, import->lhsMajorID, import->lhsMinorID, import->rewriteSet);
          }
        }
      }
    }
  }
}

void artReachabilityAnalysisRec(rdp_tree_node_data* node) {
  if (node == NULL)  // undefined nonterminal
    return;

#if defined INDUCE_TRACE
  printf("artReachabilityAnalysisRec() visiting node %i labelled %s\n", graph_atom_number(node), node->id);
#endif

  if (node->kind == ART_NONTERMINAL) {
    if (node->tableEntry->used)
      return;

    node->tableEntry->used = true;
    artReachabilityAnalysisRec(node->tableEntry->PPrimeNode);
  }

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artReachabilityAnalysisRec((rdp_tree_node_data*) graph_destination(edge));
}

void artReachabilityAnalysis(){
  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
    if (sym->kind == ART_NONTERMINAL)
      sym->used = false;

  artStartSymbol->used = true;
  artReachabilityAnalysisRec(artStartSymbol->PPrimeNode);
}

void artGrammarConsistency() {
  artNumberSymbols();

  // Check for undefined symbols
  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       sym != NULL;
       sym = (symbols_data*) symbol_next_symbol_in_scope(sym)) {
#if 0
    if (sym->kind == ART_NONTERMINAL && sym->PPrimeNode == NULL && sym->iftNode == NULL)
      text_message(TEXT_ERROR, "undefined nonterminal %s.%s\n", sym->majorID, sym->minorID);
#endif
/*      if (sym->iftNode->deleted)
        text_message(TEXT_ERROR, "deleted nonterminal %s.%s\n", sym->majorID, sym->minorID);
*/
  }

  if (artStartSymbol == NULL)
    text_message(TEXT_ERROR, "%s() has no start symbol\n", artMainMajorSymbol->majorID);

  // Check for empty majors
  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       sym != NULL;
       sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
    if (sym->kind == ART_MAJOR && sym->PPrimeNode == NULL)
     /* text_message(TEXT_ERROR, "major %s() has no productions\n", sym->majorID)*/;

  // For pre-lexing mode, check that we don't have intersecting patterns
  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       sym != NULL;
       sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
    for (symbols_data *tsym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
         tsym != NULL;
         tsym = (symbols_data*) symbol_next_symbol_in_scope(tsym))
      if ((sym != tsym) &&
          (sym->kind == ART_CHARACTER_TERMINAL || sym->kind == ART_CASE_SENSITIVE_TERMINAL || sym->kind == ART_CASE_INSENSITIVE_TERMINAL) &&
          (tsym->kind == ART_CHARACTER_TERMINAL || tsym->kind == ART_CASE_SENSITIVE_TERMINAL || tsym->kind == ART_CASE_INSENSITIVE_TERMINAL) &&
          strcmp(sym->minorID, tsym->minorID) == 0
      ) {
        text_message(TEXT_WARNING, "identical patterns for token ");
        artWriteSymbol(sym->kind, sym->minorID, sym->majorID, NULL, NULL);
        text_printf("\n");
      }


  artReachabilityAnalysis();
}

void artInduceWrapUp(){
  // If there is a whitespace convention for the main module, make an augmented start symbol of the form _S
  if (artMainMajorSymbol->whiteSpaceImport != NULL) {
    char* newStartSymbolID = (char*) mem_calloc(1, strlen(artStartSymbol->minorID) + 2);

    sprintf(newStartSymbolID, "_%s", artStartSymbol->minorID);
    symbols_data *newStartSymbol = artFind(ART_NONTERMINAL, artMainMajorSymbol->majorID, newStartSymbolID, NULL);

    if (newStartSymbol->PPrimeNode != NULL)
      text_message(TEXT_FATAL, "augmented start symbol %s already present in grammar\n", newStartSymbolID);

    newStartSymbol->PPrimeNode = artNewTreeNode(artMainMajorSymbol->PPrimeNode, newStartSymbolID, ART_LHS, newStartSymbol);
    newStartSymbol->PPrimeNode->isLHS = true;

    rdp_tree_node_data *newUnaryNode = artNewTreeNode(newStartSymbol->PPrimeNode, "unary", ART_UNARY, NULL);

    symbols_data *whiteSpaceSymbol = artFind(ART_NONTERMINAL, artMainMajorSymbol->whiteSpaceImport->lhsMajorID, artMainMajorSymbol->whiteSpaceImport->lhsMinorID, NULL);
    artNewTreeNode(newUnaryNode, "pos", ART_POS, NULL);
    artNewTreeNode(newUnaryNode, whiteSpaceSymbol->minorID, ART_NONTERMINAL, whiteSpaceSymbol);
    artNewTreeNode(newUnaryNode, "pos", ART_POS, NULL);
    artNewTreeNode(newUnaryNode, artStartSymbol->minorID, ART_NONTERMINAL, artStartSymbol);
    artNewTreeNode(newUnaryNode, "pos", ART_POS, NULL);

    artMainMajorSymbol->startSymbol = artStartSymbol = newStartSymbol;
  }

  // Add epsilon rules for all whitespace symbols
  for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       sym != NULL;
       sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
    if (sym->kind == ART_MAJOR && sym->whiteSpaceImport != NULL) {
      rdp_tree_node_data *lhsNode = artFind(ART_NONTERMINAL, sym->minorID, sym->whiteSpaceImport->lhsMinorID, NULL)->PPrimeNode;

      if (lhsNode != NULL) { // Suppress on dummy whitespace imports
        rdp_tree_node_data *newEpsilonNode = artNewTreeNode(lhsNode, "epsilon", ART_EPSILON, NULL);

        artNewTreeNode(newEpsilonNode, "pos", ART_POS, NULL);
      }
    }

  artGrammarConsistency();
}

void *artInduceGrammar(void *RDT){
  void *PGraph = (rdp_tree_node_data*) graph_insert_graph("ART P");
  void *PPrimeGraph = (rdp_tree_node_data*) graph_insert_graph("ART P'");
  void *DGraph = (rdp_tree_node_data*) graph_insert_graph("ART D");
  void *ZGraph = (rdp_tree_node_data*) graph_insert_graph("ART Z");

  rdp_tree_node_data *newRoot = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), PGraph);
  newRoot->kind = ART_ROOT; newRoot->id = "P Root";
  graph_set_root(PGraph, newRoot);

  newRoot = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), PPrimeGraph);
  newRoot->kind = ART_ROOT; newRoot->id = "P' Root";
  graph_set_root(PPrimeGraph, newRoot);

  newRoot = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), DGraph);
  newRoot->kind = ART_ROOT; newRoot->id = "D Root";
  graph_set_root(DGraph, newRoot);

  newRoot = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), ZGraph);
  newRoot->kind = ART_ROOT; newRoot->id = "Z Root";
  graph_set_root(ZGraph, newRoot);

  artInduceInitialise(RDT, PGraph, PPrimeGraph, DGraph, ZGraph);
  artErrorCheck();

  artInduceClosure(PGraph, PPrimeGraph, DGraph, ZGraph);

  artInduceWrapUp();

  if (artVerbose) artVCGRDT("art_p.vcg", PGraph);
  if (artVerbose) artVCGRDT("art_d.vcg", DGraph);
  if (artVerbose) artVCGRDT("art_z.vcg", ZGraph);

  return PPrimeGraph;
}

