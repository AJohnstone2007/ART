/* artWriteParser.cpp - ART V2.00 parser output (c) Adrian Johnstone 2009-2013 */
#include "artaux.h"
#include "artversion.h"
#include "stdarg.h"
#define catBufferSize 10000  /* 'big enough' */
#include "artcustomisation.h"
#define TEMPLATE2016 true

class artParserWriter {

  private:
  int lhsTemplateCount;
  int epsilonTemplateCount;
  int terminalTemplateCount;
  int nonterminalTemplateCount;
  int concatenationTemplateCount;
  int alternateTemplateCount;
  int doFirstTemplateCount;
  int optionalNonNullableTemplateCount;
  int optionalNullableTemplateCount;
  int positiveNonNullableTemplateCount;
  int positiveNullableTemplateCount;
  int kleeneNonNullableTemplateCount;
  int kleeneNullableTemplateCount;
  rdp_tree_node_data *iftRoot;
  void * symbols;
  artParserCustomisation *cust;

/* A rather rough and ready string catenation handler - not needed in Java port - will crash if |string| > catBufferSize*/
  char* catBuffer;
  char* catAltBuffer;
  char* catIn;

  const char* catAlt() {

    char* cb = catBuffer, *cba = catAltBuffer;

    for (; *cb != 0; cb++, cba++)
      *cba = *cb;

    *cba = 0;

    catRewrite("");

    return catAltBuffer;
  }

  const char* catWrite(const char * fmt, ...)
  {
    va_list ap;

    va_start(ap, fmt);
    int i = vsprintf(catIn, fmt, ap);
    va_end(ap);

    catIn += i;

    return catBuffer;
  }

  const char* catRewrite(const char * fmt, ...)
  {
    catIn = catBuffer;
    va_list ap;

    va_start(ap, fmt);
    int i = vsprintf(catIn, fmt, ap);
    va_end(ap);

    catIn += i;

    return catBuffer;
  }

  const char* catWriteAs(const char* string, const char* *ASCII_table) {
    if (string == NULL)
      catWrite("!!NULL in artWriteAs!!");

    for ( ; *string != '\0'; string++)
      if ((unsigned) *string > 0 && (unsigned) *string < 128)
        catWrite("%s", ASCII_table[*string]);
      else
        catWrite("_%u", (unsigned) *string);

    return catBuffer;
  }

  const char* catRewriteAs(const char* string, const char* *ASCII_table) {
    catIn = catBuffer;
    if (string == NULL)
      catWrite("!!NULL in artWriteAs!!");

    for ( ; *string != '\0'; string++)
      if ((unsigned) *string > 0 && (unsigned) *string < 128)
        catWrite("%s", ASCII_table[*string]);
      else
        catWrite("_%u", (unsigned) *string);

    return catBuffer;
  }

  const char* catSymbolAsEnumElement(symbols_data *symbol, const char * prefix) {
    if (prefix != NULL) {
      catRewrite(prefix);
      catWrite(".");
    }
    else
      catRewrite("");

    switch (symbol->kind) {
      case ART_BUILTIN_TERMINAL: catWrite("ARTTB_");  catWriteAs(symbol->minorID, asciiIdentifiers); break;
      case ART_CHARACTER_TERMINAL: catWrite("ARTTC_"); catWriteAs(symbol->minorID, asciiIdentifiers); break;
      case ART_CASE_SENSITIVE_TERMINAL: catWrite("ARTTS_");  catWriteAs(symbol->minorID, asciiIdentifiers); break;
      case ART_CASE_INSENSITIVE_TERMINAL: catWrite("ARTTI_");  catWriteAs(symbol->minorID, asciiIdentifiers); break;
      case ART_POS_NAME: catWrite("ARTN_");  catWriteAs(symbol->minorID, asciiIdentifiers); break;
      case ART_NONTERMINAL: catWrite("ARTL_");  catWriteAs(symbol->majorID, asciiIdentifiers); catWrite("_"); catWriteAs(symbol->minorID, asciiIdentifiers); break;
      case ART_EPSILON: catWrite("ARTX_EPSILON"); break;
      default: text_message(TEXT_FATAL, "internal error: call to catSymbolAsTerminalString on a symbol which is not epsilon,  a terminal or a slot label\n");
    }
    return catBuffer;
  }

  const char* catSymbolAsNontermminalName(symbols_data *symbol) {
    switch (symbol->kind) {
      case ART_NONTERMINAL: catRewrite("");  catWriteAs(symbol->majorID, asciiIdentifiers); catWrite("_"); catWriteAs(symbol->minorID, asciiIdentifiers); break;
      default: text_message(TEXT_FATAL, "internal error: call to catSymbolAsParserFragmentName on a symbol which is not a nonterminal\n");
    }
    return catBuffer;
  }

  const char* catSymbolAsParserFragmentName(symbols_data *symbol) {
    switch (symbol->kind) {
      case ART_NONTERMINAL: catRewrite("ARTPF_");  catWriteAs(symbol->majorID, asciiIdentifiers); catWrite("_"); catWriteAs(symbol->minorID, asciiIdentifiers); break;
      default: text_message(TEXT_FATAL, "internal error: call to catSymbolAsParserFragmentName on a symbol which is not a nonterminal\n");
    }
    return catBuffer;
  }

  const char* catSymbolAsRDEvaluatorName(symbols_data *symbol) {
    switch (symbol->kind) {
      case ART_NONTERMINAL: catRewrite("ARTRD_");  catWriteAs(symbol->minorID, asciiIdentifiers); break;
      default: text_message(TEXT_FATAL, "internal error: call to catSymbolAsRDEvaluatorName on a symbol which is not a nonterminal\n");
    }
    return catBuffer;
}

  const char* catNodeAsEnumerationElement(rdp_tree_node_data *node, const char *prefix) {
    catRewrite("ART");
    if (prefix != NULL)
      catWrite(prefix);
    catWrite("_");
    catWriteAs(node->lhsL->tableEntry->majorID, asciiIdentifiers);
    catWrite("_");
    catWriteAs(node->lhsL->tableEntry->minorID, asciiIdentifiers);
    if (!node->isLHS)
      catWrite("_%i", node->nodeNumber);

    return catBuffer;
  }

  const char* catNodeAsEnumerationElementString(rdp_tree_node_data *node) {
    catRewriteAs(node->lhsL->tableEntry->majorID, asciiIdentifiers);
    catWrite("_");
    catWriteAs(node->lhsL->tableEntry->minorID, asciiIdentifiers);
    catWrite("_%i", node->nodeNumber);

    return catBuffer;
  }

const char* catWriteSymbol(artKind kind, const char* pattern, const char* pattern2)
{
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: catWriteAs(pattern, asciiQuotedStrings); break;
    case ART_EPSILON: catWriteAs(pattern, asciiQuotedStrings); break;
    case ART_BUILTIN_TERMINAL: catWrite("&"); catWriteAs(pattern, asciiQuotedStrings); break;
    case ART_CHARACTER_TERMINAL:
      catWrite("`"); catWriteAs(pattern, asciiQuotedStrings);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: catWrite("\'"); catWriteAs(pattern, asciiQuotedStrings);catWrite("\'"); break;
    case ART_CASE_INSENSITIVE_TERMINAL: catWrite("\\\""); catWriteAs(pattern, asciiQuotedStrings); catWrite("\\\""); break;
    case ART_LHS:
    case ART_NONTERMINAL:
      catWriteAs(pattern2, asciiQuotedStrings);
      catWrite(".");
      catWriteAs(pattern, asciiQuotedStrings); break;
  }
//  catWrite(" ");
  return catBuffer;
}

void catNodeAsGrammarSlotRec(rdp_tree_node_data *node, rdp_tree_node_data *slotNode, const char* slotDesignator) {
  if (node->deleted)
    return;

  //Preorder actions
  switch (node->kind) {
    case ART_ILLEGAL:
    case ART_END_OF_STRING:
    case ART_MAJOR:
    case ART_ROOT:
    case ART_ALT:

    case ART_ITER:
      break;

    case ART_DIFF: catWrite("/ "); break;

    case ART_UNARY:
    case ART_CAT:
      break;

    case ART_LHS: catWriteSymbol(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); catWrite(" ::= "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
      catWriteSymbol(node->kind, node->tableEntry->minorID, node->rangeUpperCharacterTerminal != NULL ? node->rangeUpperCharacterTerminal->minorID : node->tableEntry->majorID);
      catWrite(" ");
      break;

    case ART_EPSILON:
      catWrite("# "); break;

    case ART_DO_FIRST: catWrite("( "); break;

    case ART_OPTIONAL: catWrite("[ "); break;

    case ART_POSITIVE_CLOSURE: catWrite("< "); break;

    case ART_KLEENE_CLOSURE: catWrite("{ "); break;

    case ART_POS: if (node == slotNode) catWrite (slotDesignator); /*catWrite("\\f02\\fB\fi183\\fn\\f31 ");*/ break;
    case ART_ANNOTATION: /* catWrite("{ "); */ break;
    case ART_ANNOTATION_VALUE: /* text_printf(" %s", node->id); */break;
    case ART_INSERTION: catWrite("[ "); break;
    case ART_TEAR: catWrite("$ "); break;
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    catNodeAsGrammarSlotRec((rdp_tree_node_data*) graph_destination(edge), slotNode, slotDesignator);
    // Inorder actions
    if (graph_next_out_edge(edge) != NULL)
      switch (node->kind) {
        case ART_DIFF: catWrite("/ "); break;
        case ART_ITER: catWrite("@ "); break;
        case ART_ALT: catWrite("| "); break;
        case ART_LHS: catWrite("| "); break;
        default: break;
      };
  }

  //Postorder actions
  switch (node->kind) {
    case ART_LHS: break;
    case ART_DO_FIRST: catWrite(") "); break;
    case ART_OPTIONAL: catWrite("] "); break;
    case ART_POSITIVE_CLOSURE: catWrite("> "); break;
    case ART_KLEENE_CLOSURE: catWrite("} "); break;

    case ART_ANNOTATION: /* catWrite("} "); */ break;
    case ART_INSERTION: catWrite("] "); break;
    case ART_CAT: case ART_UNARY: case ART_EPSILON:
      break;
    default: break;
  };
}

bool treeContainsSlotRec(rdp_tree_node_data *node, rdp_tree_node_data *slotNode) {
  bool retValue = false;

  if (node == slotNode)
    return true;

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    retValue |= treeContainsSlotRec((rdp_tree_node_data*) graph_destination(edge), slotNode);

  return retValue;
}

const char* catNodeAsGrammarSlot(rdp_tree_node_data *node, const char* slotDesignator) {
  rdp_tree_node_data* lhs = node->lhsL;

  catRewrite("");
  catWriteSymbol(lhs->kind, lhs->tableEntry->minorID, lhs->tableEntry->majorID); catWrite(" ::= ");

  for (void *edge = graph_next_out_edge(lhs); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(edge);

    if (treeContainsSlotRec(childNode, node)) {
      catNodeAsGrammarSlotRec(childNode, node, slotDesignator);
      break;
    }
  }

  return catBuffer;
}

const char* inputSelect() {
  if (artMGLL)
    return "artLexer.artCurrentInputPairReference";
  else
    return "artLexer.artCurrentInputIndex";
}

void parserBlockOpenSelect(const char* label, const char* comment) {
  if (comment != NULL) {cust->indent(); cust->comment(comment); }
  switch (artDespatchMode){
    case ART_DESPATCH_DYNAMIC:
    case ART_DESPATCH_STATIC: cust->label(label); break;

    case ART_DESPATCH_FRAGMENT:
    case ART_DESPATCH_STATE: cust->caseBranchOpen(label, true); break;

    default: text_message(TEXT_FATAL, "unknown despatch mode\n");
  }
}

void jumpSelect(const char* id) {
  switch (artDespatchMode){
    case ART_DESPATCH_DYNAMIC: cust->jumpDynamic(id); break;
    case ART_DESPATCH_STATIC: cust->jump(id); break;
    case ART_DESPATCH_FRAGMENT: cust->jumpFragment(id); break;
    case ART_DESPATCH_STATE: cust->jumpState(id); break;
    default: text_message(TEXT_FATAL, "unknown despatch mode\n");
  }
}

void popSel(const char *nonterminalString) {
  if (artClusteredGSS)
    cust->functionCall("artPopClustered", nonterminalString, "artCurrentGSSNode", inputSelect(), "artCurrentSPPFNode");
  else if (artMGLL)
    cust->functionCall("artPopMGLL", "artCurrentGSSNode", inputSelect(), "artCurrentSPPFNode");
  else
    cust->functionCall("artPop", "artCurrentGSSNode", inputSelect(), "artCurrentSPPFNode");
}

const char* rootGSSNodeSelect() {
//Kludge this away if using original forumulation
  if (artClusteredGSS)
    return "0";
  else
    return "artRootGSSNode";
}

const char* findGSSSelect() {
  if (artClusteredGSS)
    return "artFindGSSClustered";
  else if (artMGLL)
    return "artFindGSSMGLL";
  else
    return "artFindGSS";
}

const char* findGSSInitialSelect() {
  if (artClusteredGSS)
    return "artFindGSSClusteredInitial";
  else
    return "artFindGSS";   // No need for special action in nonclustered implementation
}

const char* findDescriptorSelect() {
  if (artClusteredGSS)
    return "artFindDescriptorClustered";
  else
    return "artFindDescriptor";
}

const char* emptyNodeSelect() { if (artSupportMode == ART_SUPPORT_OO) return cust->nullName(); else return "0"; }

  void updateIsReferredLabelRec(rdp_tree_node_data * node) {
//  printf("updateIsReferredLabel() at node %i labelled %s\n", graph_atom_number(node), node->id);
    if (node->deleted)
      return;

    if (node->isSPPFLabel || node->isCodeLabel || node->isReferredLabel || node->isTestRepeatLabel) {
  //    node->lhsL->isReferredLabel = true;
      if (node->pL != node)
        node->pL->isReferredLabel= true;

      if (node->aL != node)
      node->aL->isReferredLabel = true;
    }
    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
      updateIsReferredLabelRec((rdp_tree_node_data*) graph_destination(edge));
  }

  void printStaticSwitchElementRec(rdp_tree_node_data * node) {
  //  printf("printStaticSwitchElementRec() at node %i labelled %s\n", graph_atom_number(node), node->id);
    if (node->deleted)
      return;

    if (node->isCodeLabel) {
      cust->caseBranchOpen(catNodeAsEnumerationElement(node, "L"), true);
      jumpSelect(catNodeAsEnumerationElement(node, "L"));
    }

    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
      printStaticSwitchElementRec((rdp_tree_node_data*) graph_destination(edge));
  }

  void printStaticSwitch() {
    cust->label("ARTX_DESPATCH_SWITCH");
    cust->caseOpen("artCurrentRestartLabel");
    printStaticSwitchElementRec(iftRoot);
    cust->caseBranchOpen("ARTX_DESPATCH", true);
    jumpSelect("ARTX_DESPATCH");
    cust->caseDefault();
    cust->exception("unexpectedLabel");
    cust->caseClose("artCurrentRestartLabel", true);
  }

  void printCodeAndSPPFLabelRec(rdp_tree_node_data * node, bool codeOnly) {
//    printf("printCodeAndSPPFLabel() at node %i labelled %s\n", graph_atom_number(node), node->id);

    if (node->deleted)
      return;

    if (codeOnly) {
      if (node->isCodeLabel)
        cust->enumerationElement(catNodeAsEnumerationElement(node, "L"));
    }
    else {
      if (node->isCodeLabel || node->isSPPFLabel || node->isReferredLabel || node->isTestRepeatLabel || node->isPosParentLabel || node->isGiftLabel)
      if (!node->isCodeLabel) // we already output the code labels
        cust->enumerationElement(catNodeAsEnumerationElement(node, "L"));

      if (node->isFiPC)
        cust->enumerationElement(catNodeAsEnumerationElement(node, "P"));

      if (node->isColon)
        cust->enumerationElement(catNodeAsEnumerationElement(node, "N"));

      if (node->isClosureLabel)
        cust->enumerationElement(catNodeAsEnumerationElement(node, "C"));

      if (node->isAltLabel)
        cust->enumerationElement(catNodeAsEnumerationElement(node, "A"));

      if (node->isRELabel)
        cust->enumerationElement(catNodeAsEnumerationElement(node, "T"));
    }

    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
      printCodeAndSPPFLabelRec((rdp_tree_node_data*) graph_destination(edge), codeOnly);
  }


void writeCodeRec(rdp_tree_node_data *iftNode, rdp_tree_node_data *lastLHSNode)
{
  void *edge;
#if  0
    printf("Outputting code for node %i labelled %s\n", graph_atom_number(iftNode), iftNode->id);
    if (iftNode->tableEntry != NULL) {
      printf("  tableEntry points to symbol labelled %s.%s\n", iftNode->tableEntry->majorID, iftNode->tableEntry->minorID);
      if (iftNode->tableEntry->iftNode != NULL)
        printf("    tableEntry iftNode %i labelled %s\n", graph_atom_number(iftNode->tableEntry->iftNode), iftNode->tableEntry->iftNode->id);
    }
#endif
  switch (iftNode->kind) {
  /* The root of the tree is labelled ART_MODULE and has as children the LHS nonterminals: we just recurse through them */
  case ART_MAJOR:
    for (edge = graph_next_out_edge(iftNode); edge != NULL; edge = graph_next_out_edge(edge))
      writeCodeRec((rdp_tree_node_data*) graph_destination(edge), NULL);
    break;

/* (a) Productions of nonterminal Y: code(Y ::= a_1 | ... | a_p) is

if testSelect(I[c_I], Y, a_1) findDescriptor_add(L_a_1, c_u, c_I, $)
...
if testSelect(I[c_I], Y, a_p) findDescriptor_add(L_a_p, c_u, c_I, $)
goto despatch

L_a_1: code(a_1, Y, #); if(I[c_I] \in follow(Y)) pop(c_u, c_I, c_N); goto despatch
...
L_a_p: code(a_p, Y, #); if(I[c_I] \in follow(Y)) pop(c_u, c_I, c_N); goto despatch
*/
  case ART_LHS:
    if (artDespatchMode == ART_DESPATCH_FRAGMENT) {
      cust->functionVoidOpen(catSymbolAsParserFragmentName(iftNode->tableEntry));
      cust->caseOpen("artCurrentRestartLabel"); cust->indentUp();
    }

    lhsTemplateCount++;
    iftNode->isCodeLabel = true;
    catNodeAsEnumerationElement(iftNode, "L"); catAlt();
    parserBlockOpenSelect(catAltBuffer, catRewrite(" Nonterminal %s.%s production descriptor loads", iftNode->tableEntry->majorID, iftNode->tableEntry->minorID));

    /* Output descriptor loads */
    for (edge = graph_next_out_edge(iftNode); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(edge);
      rdp_tree_node_data *grandChildNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(childNode));

      if (!artSuppressProductionGuard) {
        cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&grandChildNode->guard)), cust->inputAccessToken());
        cust->newLine();
        cust->indentUp();
      }
      cust->functionCall(findDescriptorSelect(), catNodeAsEnumerationElement(childNode->lrL, "L"), "artCurrentGSSNode", inputSelect(), "artDummySPPFNode");
      if (!artSuppressProductionGuard)
       cust->indentDown();
      childNode->lrL->isSPPFLabel = true;
    }
    cust->indent(); jumpSelect("ARTX_DESPATCH");

    /* Output RHS bodies */
    for (edge = graph_next_out_edge(iftNode); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(edge);

      childNode->lrL->isCodeLabel = true;
      catNodeAsEnumerationElement(childNode->lrL, "L"); catAlt();
      parserBlockOpenSelect(catAltBuffer, catRewrite(" Nonterminal %s.%s: match production", childNode->lhsL->tableEntry->majorID, childNode->lhsL->tableEntry->minorID));

      writeCodeRec(childNode, iftNode); // Pass LHS node in

      if (!(artPredictivePops && iftNode->isPostPredictivePop)) {
        if (!artSuppressPopGuard) {
          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->follow)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");
        }

        popSel(catNodeAsEnumerationElement(iftNode, "L"));
        cust->indent(); jumpSelect("ARTX_DESPATCH /* Top level pop */");
      }
    }
    if (artDespatchMode == ART_DESPATCH_FRAGMENT) {
      cust->caseClose("artCurrentRestartLabel", true);
      cust->functionClose(catSymbolAsParserFragmentName(iftNode->tableEntry)); cust->newLine();
    }
  break;

/*
(b) Base regular expressions: code(r, X, \beta)

(i) r is epsilon #: code(r, X, \beta) is
c_R := findSPPFE(c_I)  c_N := findSPPF(E_r, c_N, c_R)
*/
  case ART_EPSILON:
    epsilonTemplateCount++;
    cust->indent(); cust->comment(" Epsilon template start ");
    cust->functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon", "artLexer.artCurrentInputIndex");
    iftNode->erL->isSPPFLabel = true;
    cust->functionAssignCall("artCurrentSPPFNode", "artFindSPPF", catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");
    cust->indent(); cust->comment(" Epsilon template end ");
    break;

/*
(ii) r is a terminal a: code(r, X, \beta) is
c_R := findSPPFT(a, c_I)  c_I = c_I + 1
c_N := findSPPF(E_r, c_N, c_R)
*/
  case ART_BUILTIN_TERMINAL:
  case ART_CHARACTER_TERMINAL:
  case ART_CASE_SENSITIVE_TERMINAL:
  case ART_CASE_INSENSITIVE_TERMINAL:
  // Terminal template: here we do different things for classical GLL, MGLL with simple templates (may generate useless descriptors) and MGLL with continuation check templates

    terminalTemplateCount++;
    cust->indent(); cust->comment(" Terminal template start ");

    if (artMGLL) { // MGLL variannt
      cust->forSuccessorPair(); cust->newLine();
      cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->erL->guard)), cust->inputSuccessorReferenceToken()); cust->blockOpen(); cust->indentUp(); cust->newLine();

      iftNode->isSPPFLabel = true;
      cust->functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFTerminal", catSymbolAsEnumElement(iftNode->tableEntry, NULL), "artLexer.artCurrentInputIndex", cust->inputSuccessorReferenceLeftExtent());
      iftNode->erL->isSPPFLabel = true;
      cust->functionAssignCall("artTemporarySPPFNode", "artFindSPPF", catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");

      iftNode->erL->isCodeLabel = true;
      cust->functionCall(findDescriptorSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", cust->inputSuccessorReference(), "artTemporarySPPFNode");

      cust->indentDown(); cust->indent(); cust->blockClose(); cust->newLine();

      cust->indent(); jumpSelect("ARTX_DESPATCH");

      cust->newLine();
      cust->indentDown();

      parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);
    } else {
      iftNode->isSPPFLabel = true;
      cust->assign("artLexer.artCurrentInputPairReference", cust->inputAccessFirstSuccessorReference());
      cust->functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFTerminal", catSymbolAsEnumElement(iftNode->tableEntry, NULL), "artLexer.artCurrentInputIndex", cust->inputAccessLeftExtent());
      cust->assign("artLexer.artCurrentInputIndex", cust->inputAccessLeftExtent());

      iftNode->erL->isSPPFLabel = true;
      cust->functionAssignCall("artCurrentSPPFNode", "artFindSPPF", catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");

    }

    cust->indent(); cust->comment(" Terminal template end ");
    break;

 /*
(iii) r in a nonterminal Y: code(r, X, \beta) is
c_U = findGSS_create(E_r, c_N, ) goto J_Y  E_r:
*/
  case ART_NONTERMINAL:
    nonterminalTemplateCount++;
    cust->indent(); cust->comment(" Nonterminal template start ");

    iftNode->erL->isCodeLabel = true;
    cust->functionAssignCall("artCurrentGSSNode", findGSSSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
    iftNode->tableEntry->iftNode->isCodeLabel = true;
    cust->indent(); jumpSelect(catNodeAsEnumerationElement(iftNode->tableEntry->iftNode, "L"));

    iftNode->erL->isCodeLabel = true;

    parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);

    cust->indent(); cust->comment(" Nonterminal template end ");
    break;

/*
(iv) r is a bracketed expression (v): code(r, X, \beta) is
code(v, X, \beta)
*/
  case ART_DO_FIRST:
    cust->indent(); cust->comment(" Do-first template start ");
    doFirstTemplateCount++;
    writeCodeRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(iftNode)), lastLHSNode);
    cust->indent(); cust->comment(" Do-first template end ");
    break;

/*
(v) r is a bracketed expression [v] and v *=> #: code (r, X, \beta) is
OLD TEMPLATE:

if testSelect(I[c_I], X, \beta) {
  c_r := findSPPFE(c_I)
  c_t := findSPPF(E_r, c_N, c_R)  // Note that c_t isn't need; the result is voided
}
if not testSelect(I[c_I], X, (v)\beta) goto despatch
code(v, X, \beta)

NEW TEMPLATE:

if (testSelect(I[c_I ], X; \beta)) {
  c_r := findSPPFE(c_I)
  c_t := findSPPF(E_r, c_N, c_R)
  findDescriptor(E_r, c_u, c_I, c_t)
  testRepeat(T_r, c_U, c_I, c_t)
}
if not testSelect(I[c_I], X, (v)\beta) goto despatch
code(v, X, \beta)
if (testRepeat(T_r, c_U, c_I, c_N)) goto despatch
E_r :

2016 TEMPLATE
if testSelect(I[c_I], E_r) {
  c_t :=getBaseNode(E_s, E_r, c_I, c_N)
  findDescriptor_add(E_r, c_u, c_I, c_t);
  testRepeat(T_r, c_u, c_I, c_T)
}
code(s);
if (testRepeat(T_r, c_u, c_I, c_N) goto despatch;
E_r:

*/
  case ART_OPTIONAL:
    {
      rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(iftNode));

      if (set_includes_element(&childNode->first, artEpsilonSymbol->number)) {
        optionalNullableTemplateCount++;

        if (TEMPLATE2016) {
/****************************************/
// Note - iftNode corresponds to r in the paper; childNode is s

          cust->indent(); cust->comment(" Optional, nullable body template start (2016 version)");
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          cust->blockOpen();
          catNodeAsEnumerationElement(iftNode->erL, "L"); catAlt();

// 30/8/16 change from getBaseNode to getNode
#if 0
          cust->functionAssignCall("artTemporarySPPFNode", "artFindSPPFBaseNode", catNodeAsEnumerationElement(childNode->erL, "L"), catAltBuffer, "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
#else
          cust->functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon", "artLexer.artCurrentInputIndex");
          iftNode->erL->isSPPFLabel = true;
          cust->functionAssignCall("artTemporarySPPFNode", "artFindSPPF", catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");
#endif

          cust->functionCall(findDescriptorSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artTemporarySPPFNode");
          iftNode->isTestRepeatLabel = true;
          cust->functionCall("artTestRepeat", catNodeAsEnumerationElement(iftNode, "T"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artTemporarySPPFNode");
          iftNode->isRELabel = true;
          cust->blockClose();

          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");

          writeCodeRec(childNode, lastLHSNode);

          if (!artTestRepeatNo) {
            iftNode->isTestRepeatLabel = true;
            cust->ifTestRepeat(catNodeAsEnumerationElement(iftNode, "T"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
            iftNode->isCodeLabel = true;
            jumpSelect("ARTX_DESPATCH");
          }
          iftNode->erL->isCodeLabel = true;

          parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);

          cust->indent(); cust->comment(" Optional, nullable body template end (2016 version)");
/**********************************/
        } else
        {
          cust->indent(); cust->comment(" Optional, nullable body template start ");
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          cust->blockOpen();
          cust->functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon", "artLexer.artCurrentInputIndex");
          iftNode->erL->isSPPFLabel = true;
          cust->functionAssignCall("artTemporarySPPFNode", "artFindSPPF", catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");
          cust->functionCall(findDescriptorSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artTemporarySPPFNode");
          if (!artTestRepeatNo) {
            iftNode->isTestRepeatLabel = true;
            cust->ifTestRepeat(catNodeAsEnumerationElement(iftNode, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artTemporarySPPFNode");
            jumpSelect("ARTX_DESPATCH");
          }
          cust->blockClose();

          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");

          writeCodeRec(childNode, lastLHSNode);

          if (!artTestRepeatNo) {
            iftNode->isTestRepeatLabel = true;
            cust->ifTestRepeat(catNodeAsEnumerationElement(iftNode, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
            jumpSelect("ARTX_DESPATCH");
          }
          iftNode->erL->isCodeLabel = true;

          parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);

          cust->indent(); cust->comment(" Optional, nullable body template end ");
        }
      }
/*
(vi) r is a bracketed expression [v] and not v *=> #: code (r, X, \beta) is
if testSelect(I[c_I], X, \beta) {
  c_r := findSPPFE(c_I)
  c_t := findSPPF(E_r, c_N, c_r)
  findDescriptor_add(E_r, c_u, c_I, c_t)
}
if not testSelect(I[c_I], X, v\beta) goto despatch
code(v, X, \beta)
E_r:

(or in 2016 terms)

(vi) r is a bracketed expression [s] and not s *=> #: code (r) is
if testSelect(I[c_I], E_r) {
  c_r := findSPPFNodeE(c_I)
  c_t := findSPPFNode(E_r, c_N, c_r)
  findDescriptor_add(E_r, c_u, c_I, c_t)
}
if not testSelect(I[c_I], L_s) goto despatch
code(s)
E_r:

*/
       else {
          optionalNonNullableTemplateCount++;

          cust->indent(); cust->comment(" Optional, non-nullable body template start ");
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          cust->blockOpen();
          cust->functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon","artLexer.artCurrentInputIndex");
          iftNode->erL->isSPPFLabel = true;
          cust->functionAssignCall("artTemporarySPPFNode", "artFindSPPF", catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");
          cust->functionCall(findDescriptorSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artTemporarySPPFNode");
          cust->blockClose();
          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");
          writeCodeRec(childNode, lastLHSNode);
          iftNode->erL->isCodeLabel = true;

        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);
          cust->indent(); cust->comment(" Optional, non-nullable body template end ");
       }
     }
     break;

/*
(vii) r is a bracketed expression <v> and not v *=> #: code (r, X, \beta) is
c_u := findGSS_create(E_r, c_u, c_I, c_N)
c_N := dummyNode
C_r: if not testSelect(I[c_I], X, v\beta) goto despatch
code(v, X, [v]\beta)
if testSelect(I[c_I], X, [\beta]) pop(c_U, c_I, c_N)
goto C_r
E_r:

or in 2016 terms
(vii) r is a bracketed expression <s> and not s *=> #: code (r) is
c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
c_N := dummyNode
C_r: if not testSelect(I[c_I], L_s) goto despatch
code(s)
if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
goto C_r
E_r:
*/
  case ART_POSITIVE_CLOSURE:
    {
      rdp_tree_node_data *childNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(iftNode));
      if (!set_includes_element(&childNode->first, artEpsilonSymbol->number)) {
          positiveNonNullableTemplateCount++;

          cust->indent(); cust->comment(" Positive closure, non-nullable body template start ");

          iftNode->erL->isCodeLabel = true;
          cust->functionAssignCall("artCurrentGSSNode", findGSSSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
          cust->assign("artCurrentSPPFNode", "artDummySPPFNode");
        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode, "C"), NULL); iftNode->isClosureLabel = true;
        if (!set_includes_element(&childNode->first, artEpsilonSymbol->number)) {
          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");
          writeCodeRec(childNode, lastLHSNode);
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          popSel("unimplementedEBNFClusteredDescriptor");
          cust->indent(); jumpSelect(catNodeAsEnumerationElement(iftNode, "C"));
          iftNode->erL->isCodeLabel = true;
        }

        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);

        cust->indent(); cust->comment(" Positive closure, non-nullable body template end ");
      }
/*
(viii) r is a bracketed expression <s> and s *=> #: code (r) is
c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
c_N := dummyNode
C_r: if testRepeat(T_r, c_u, c_I, c_N) goto despatch
if not testSelect(I[c_I], L_s) goto despatch
code(s)
if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
goto C_r
E_r:

*/
      else {
          positiveNullableTemplateCount++;

          cust->indent(); cust->comment(" Positive closure, nullable body template start (2016 version)");

//        c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
          iftNode->erL->isCodeLabel = true;
          cust->functionAssignCall("artCurrentGSSNode", findGSSSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");

//        c_N := dummyNode
          cust->assign("artCurrentSPPFNode", "artDummySPPFNode");

//      C_r:
        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode, "C"), NULL); iftNode->isClosureLabel = true;

//        if testRepeat(T_r, c_u, c_I, c_N) goto despatch
          iftNode->isTestRepeatLabel = true;
          cust->ifTestRepeat(catNodeAsEnumerationElement(iftNode, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
          jumpSelect("ARTX_DESPATCH");


//        if not testSelect(I[c_I], L_s) goto despatch
          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");

//      code(s)
        writeCodeRec(childNode, lastLHSNode);

// if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
//  goto C_r
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          popSel("unimplementedEBNFClusteredDescriptor");
          cust->indent(); jumpSelect(catNodeAsEnumerationElement(iftNode, "C"));
          iftNode->erL->isCodeLabel = true;

// E_r:
          parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);

          cust->indent(); cust->comment(" Positive closure, nullable body template end (2016 version)");
      }
    }
    break;

/*
(ix) r is a bracketed expression {v} and not v *=> #: code (r, X, \beta) is
c_u := findGSS_create(E_r, c_u, c_I, c_N)
if testSelect(I[c_I], X, \beta) { c_N := findSPPFE(c_I)  pop(c_u, c_I, c_N) }
c_N := dummyNode
C_r: if not testSelect(I[c_I], X, v\beta) goto despatch
code(v, X, [v]\beta)
if testSelect(I[c_I], X, \beta) pop(c_U, c_I, c_N)
goto C_r
E_r:

or, in post 2016-speak

(ix) r is a bracketed expression {s} and not s *=> #: code (r) is
c_u := findGSSNode_create(E_r, c_u, c_I, c_N)

if testSelect(I[c_I], E_r) { c_N := findSPPFNodeE(c_I)  pop(c_u, c_I, c_N) }
c_N := dummyNode
C_r: if not testSelect(I[c_I], L_s) goto despatch
code(s)
if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
goto C_r
E_r:

*/
  case ART_KLEENE_CLOSURE:
    {
      rdp_tree_node_data* childNode = (rdp_tree_node_data*) graph_destination(graph_next_out_edge(iftNode));
      if (!set_includes_element(&childNode->first, artEpsilonSymbol->number)) {
          kleeneNonNullableTemplateCount++;

          cust->indent(); cust->comment(" Kleene closure, non-nullable body template start ");

          iftNode->erL->isCodeLabel = true;
          cust->functionAssignCall("artCurrentGSSNode", findGSSSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          cust->blockOpen();
          cust->functionAssignCall("artCurrentSPPFNode", "artFindSPPFEpsilon", "artLexer.artCurrentInputIndex");
          popSel("unimplementedEBNFClusteredDescriptor");
          cust->blockClose();
          cust->assign("artCurrentSPPFNode", "artDummySPPFNode");
        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode, "C"), "L"); iftNode->isClosureLabel = true;
          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");
        writeCodeRec(childNode, lastLHSNode);
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          popSel("unimplementedEBNFClusteredDescriptor");
          cust->indent(); jumpSelect(catNodeAsEnumerationElement(iftNode, "C"));
          iftNode->erL->isCodeLabel = true;
        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);

          cust->indent(); cust->comment(" Kleene closure, non-nullable body template end ");

      }
/*
(x) r is a bracketed expression {v} and v *=> #: code (r, X, \beta) is
c_u := findGSS_create(E_r, c_u, c_I, c_N)
if testSelect(I[c_I], X, \beta) c_N := getLoopSkipNode(L_r, c_I)
c_N := getLoopBaseNode(E_v, c_I)
C_r: if testRepeat(T_r, c_u, c_I, c_N) goto despatch
if not testSelect(I[c_I], X, v\beta) goto despatch
code(v, X, v\beta)
if testSelect(I[c_I], X, \beta) pop(c_U, c_I, c_N)
goto C_r
E_r:

post 2016
(x) r is a bracketed expression {s} and s *=> #: code (r) is
c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
c_N := getBaseNode(E_s, E_r, c_I)
C_r: if testRepeat(T_r, c_u, c_I, c_N) goto despatch
if not testSelect(I[c_I], L_s) goto despatch
code(s)
if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
goto C_r
E_r:
*/
      else {
          kleeneNullableTemplateCount++;

          cust->indent(); cust->comment(" Kleene closure, nullable body template start (2016 version)");
//        c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
          iftNode->erL->isCodeLabel = true;
          cust->functionAssignCall("artCurrentGSSNode", findGSSSelect(), catNodeAsEnumerationElement(iftNode->erL, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");

//        c_N := getBaseNode(E_s, E_r, c_I, dummyNode)
          catNodeAsEnumerationElement(iftNode->erL, "L"); catAlt();
          cust->functionAssignCall("artCurrentSPPFNode", "artFindSPPFBaseNode", catNodeAsEnumerationElement(childNode->erL, "L"), catAltBuffer, "artLexer.artCurrentInputIndex");

//      C_r:
        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode, "C"), NULL); iftNode->isClosureLabel = true;

//      if testRepeat(T_r, c_u, c_I, c_N) goto despatch
          iftNode->isRELabel = true;
          cust->ifTestRepeat(catNodeAsEnumerationElement(iftNode, "T"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
          jumpSelect("ARTX_DESPATCH");

//       if not testSelect(I[c_I], L_s) goto despatch
          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");

//      code(s)
        writeCodeRec(childNode, lastLHSNode);

//      if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
          cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->rightSibling->guard)), cust->inputAccessToken());
          popSel("unimplementedEBNFClusteredDescriptor");

//       goto C_r
          cust->indent(); jumpSelect(catNodeAsEnumerationElement(iftNode, "C"));
          iftNode->erL->isCodeLabel = true;

//      E_r:
        parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode->erL, "L"), NULL);

          cust->indent(); cust->comment(" Kleene closure, nullable body template end (2016 version)");

      }
    }
    break;

/*
(xi) r is a concatenated expression r_1...r_d: code(r, X, \beta) is
code(r_1, X, \beta)
if not testSelect(I[c_I], X, r2...r_d\beta) goto despatch
code(r_2, X, r3..r_d\beta)
...
if not testSelect(I[c_I], X, r_d\beta) goto despatch
code(r_d, X, \beta)
*/
  case ART_CAT:
  case ART_UNARY: {
    concatenationTemplateCount++;

    cust->indent(); cust->comment(" Cat/unary template start ");

    // rdp_tree_node_data *previousChildNode = NULL;
    rdp_tree_node_data *childNode = NULL;

    for (edge = graph_next_out_edge(iftNode); edge != NULL; edge = graph_next_out_edge(edge)) {
  //    previousChildNode = childNode;
      childNode = (rdp_tree_node_data*) graph_destination(edge);

      if (artPredictivePops && childNode->isPredictivePop) {
        if (!artSuppressPopGuard) {
          cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&childNode->guard)), cust->inputAccessToken());
          jumpSelect("ARTX_DESPATCH");
        }

        popSel(catNodeAsEnumerationElement(lastLHSNode, "L"));
        cust->indent(); jumpSelect("ARTX_DESPATCH /* Predictive pop */");
      }
      else
        writeCodeRec(childNode, lastLHSNode);
    }
    cust->indent(); cust->comment(" Cat/unary template end ");
    }
    break;

/*
(xii) r is an alternated expression r_1 | ... | r_d: code (r, X, \beta) is
if testSelect(I[c_I], X, r_1\beta) findDescriptor_add(L_r_1, c_u, c_I, c_N)
...
if testSelect(I[c_I], X, r_d\beta) findDescriptor_add(L_r_d, c_u, c_I, c_N)
goto despatch

L_r_1: code (r_1, X, \beta) goto A_r
...
L_r_d-1: code (r_d-1, X, \beta) goto A_r
L_r_d: code (r_d, X, \beta)
A_r: if testRepeat(T_r, c_u, c_I, c_N) goto despatch
*/
  case ART_ALT:
      alternateTemplateCount++;

    cust->indent(); cust->comment(" Alternate template start ");

    for (edge = graph_next_out_edge(iftNode); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data* childNode = (rdp_tree_node_data*) graph_destination(edge);
      rdp_tree_node_data* firstPosNode = artLeftmostElementRec(childNode);

        cust->ifInSet(catRewrite("ARTSet%i", artMergedSetNumber(&firstPosNode->guard)), cust->inputAccessToken());
        cust->functionCall(findDescriptorSelect(), catNodeAsEnumerationElement(firstPosNode, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
        firstPosNode->isCodeLabel = firstPosNode->isSPPFLabel = true;  /* Error? Shouldn't this be isSPPFLabel */
    }

      cust->indent(); jumpSelect("ARTX_DESPATCH");

    for (edge = graph_next_out_edge(iftNode); edge != NULL; edge = graph_next_out_edge(edge)) {
      rdp_tree_node_data* childNode = (rdp_tree_node_data*) graph_destination(edge);
      rdp_tree_node_data* firstPosNode = artLeftmostElementRec(childNode);

        firstPosNode->isCodeLabel = true;
      parserBlockOpenSelect(catNodeAsEnumerationElement(firstPosNode, "L"), NULL);
      writeCodeRec(childNode, lastLHSNode);
        if (!(artPredictivePops && iftNode->isPostPredictivePop)) {
          cust->indent();
          jumpSelect(catNodeAsEnumerationElement(iftNode, "A")); iftNode->isAltLabel = true;
        }
    }

    if (!(artPredictivePops && iftNode->isPostPredictivePop)) { // suppress testRepeat block if predictive pops in play
        iftNode->isTestRepeatLabel = true;
      parserBlockOpenSelect(catNodeAsEnumerationElement(iftNode, "A"), NULL);
        if (!artTestRepeatNo) {
          cust->ifTestRepeat(catNodeAsEnumerationElement(iftNode, "L"), "artCurrentGSSNode", "artLexer.artCurrentInputIndex", "artCurrentSPPFNode");
          jumpSelect("ARTX_DESPATCH");
        }
    }

    cust->indent(); cust->comment(" Alternate template end ");
    break;

  case ART_POS:
    if (iftNode->isPosSelector) {
      cust->ifNotInSet(catRewrite("ARTSet%i", artMergedSetNumber(&iftNode->guard)), cust->inputAccessToken());
      jumpSelect("ARTX_DESPATCH");
    }
    break;

  default: text_redirect(stdout); text_message(TEXT_FATAL, "unknown tree node kind %i labelled '%s' found during parser output\n", iftNode->kind, iftNode->id);
  }
}

  public:
  void printTemplateCounts() {
    if (artVerbose)
      text_printf("\nGenerated template instance counts\n\n"
                  "lhs, %i, "
                  "epsilon, %i, "
                  "terminal, %i, "
                  "nonterminal, %i, "
                  "concatenation, %i, "
                  "alternate, %i, "
                  "doFirst, %i, "
                  "optionalNonNullable, %i, "
                  "optionalNullable, %i, "
                  "positiveNonNullable, %i, "
                  "positiveNullable, %i, "
                  "kleeneNonNullable, %i, "
                  "kleeneNullable, %i\n\n",
                  lhsTemplateCount,
                  epsilonTemplateCount,
                  terminalTemplateCount,
                  nonterminalTemplateCount,
                  concatenationTemplateCount,
                  alternateTemplateCount,
                  doFirstTemplateCount,
                  optionalNonNullableTemplateCount,
                  optionalNullableTemplateCount,
                  positiveNonNullableTemplateCount,
                  positiveNullableTemplateCount,
                  kleeneNonNullableTemplateCount,
                  kleeneNullableTemplateCount
      );
    }

  void writeSetInitialiserDeclarations() {
    for (mergedSets_data *thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(symbol_get_scope(mergedSets));
       thisSet != NULL;
       thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(thisSet))
      if (thisSet->usedInParser)
        cust->declareBooleanArray(catRewrite("ARTSet%i", thisSet->number));
  }

  symbols_data *writeARTLabelEnumeration() {
    cust->newLine();
    cust->enumerationOpen("artLabel");
    cust->enumerationElement("ARTX_EOS");

    // Scan and print builtins
    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_BUILTIN_TERMINAL)
        cust->enumerationElement(catSymbolAsEnumElement(lhsSymbol, NULL));

    // Scan and print terminals
    bool first = false;
    symbols_data *firstTerminalSymbol = NULL;

    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_CHARACTER_TERMINAL ||
          lhsSymbol->kind == ART_CASE_SENSITIVE_TERMINAL ||
          lhsSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL) {
        cust->enumerationElement(catSymbolAsEnumElement(lhsSymbol, NULL));
        if (firstTerminalSymbol == NULL)
          firstTerminalSymbol = lhsSymbol;
      }

    // Scan for and print EPSILON
    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_EPSILON)
        cust->enumerationElement("ARTX_EPSILON");


    // Scan for and print nonterminals that have no trees - these have been created by gather operators
    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_NONTERMINAL && lhsSymbol->iftNode == NULL)
        cust->enumerationElement(catSymbolAsEnumElement(lhsSymbol, NULL));

    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_NONTERMINAL && lhsSymbol->iftNode != NULL)
        printCodeAndSPPFLabelRec(lhsSymbol->iftNode, true); // Traverse the tree and print out the code labels

    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_NONTERMINAL && lhsSymbol->iftNode != NULL)
        printCodeAndSPPFLabelRec(lhsSymbol->iftNode, false); // Traverse the tree and print out the rest of the labels

    cust->enumerationElement("ARTX_DESPATCH");
    cust->enumerationElement("ARTX_DUMMY");

    cust->enumerationElement("ARTX_LABEL_EXTENT");
    cust->enumerationClose("artLabel");

    return firstTerminalSymbol;
  }

  void writeARTNameEnumeration() {
    // Scan and print names
    cust->newLine();
    cust->enumerationOpen("artName");
    cust->enumerationElement("ARTNAME_NONE");
    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_POS_NAME)
        cust->enumerationElement(catSymbolAsEnumElement(lhsSymbol, NULL));
    cust->enumerationElement("ARTNAME_EXTENT");
    cust->enumerationClose("artName");
  }

  void writeLexicaliseBuiltinInstances(bool separate, const char* prefix) {
    cust->functionVoidOpen("artLexicaliseBuiltinInstances");
    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
      lhsSymbol != NULL;
      lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
      if (lhsSymbol->kind == ART_BUILTIN_TERMINAL) {
        cust->lexerBuiltInInstance(lhsSymbol->minorID, catSymbolAsEnumElement(lhsSymbol, separate ? prefix : NULL)); /// Need to add artParse. here!
      }
    cust->functionClose("artLexicaliseBuiltinInstances"); cust->newLine();
  }

  void writeLexicalisePreparseWhitespaceInstances(bool separate) {
    cust->functionVoidOpen("artLexicalisePreparseWhitespaceInstances");
    if (artMainMajorSymbol->builtinWhitespaceNode == NULL) {
      cust->lexerWhitespaceBuiltinInstance("WHITESPACE");
    }
    else {
      for (void *edge = graph_next_out_edge(artMainMajorSymbol->builtinWhitespaceNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        if (strcmp( ((rdp_tree_node_data*) graph_destination(edge))->id, "builtinTerm") == 0)
          cust->lexerWhitespaceBuiltinInstance(((rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_destination(edge))))->id);
        else if (strcmp( ((rdp_tree_node_data*) graph_destination(edge))->id, "epsilon") == 0)
          cust->comment(" epsilon import ");
        else if (strcmp( ((rdp_tree_node_data*) graph_destination(edge))->id, "charTerm") == 0)
          cust->lexerWhitespaceCharInstance(((rdp_tree_node_data*) graph_destination(graph_next_out_edge(graph_destination(edge))))->id);
        else
          text_message(TEXT_FATAL, "unexpected whitespace tree node '%s' found during preparse lex mode whitespace synthesis\n");
      }
    }
    cust->functionClose("artLexicalisePreparseWhitespaceInstances"); cust->newLine();
  }

  void writeCodeBodies() {
    for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym)) {
//      if (sym->kind != ART_MAJOR)
      if (sym->kind != ART_NONTERMINAL)
        continue;
      writeCodeRec(sym->iftNode, NULL);
    }
  }

  void writeParseGenerated() {
  if (artDespatchMode == ART_DESPATCH_FRAGMENT) {
    // Write fragment forward declarations
    for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
      if (sym->kind == ART_NONTERMINAL)
        cust->functionVoidForward(catSymbolAsParserFragmentName(sym));

    // Now write our the fragments
    writeCodeBodies();
  }

  cust->functionVoidOpenThrows("artParseGenerated", cust->stringName(), "artCharacterString", cust->integerName(), "artStartLabel");

  cust->assignAppendNull("String artCharacterStringInput", "artCharacterString");
  cust->assign("artStartSymbolLabel", "artStartLabel");
  cust->assign("artIsInLanguage", "false");
  cust->functionCall("artLexer.artLexicaliseUsingLongestMatch", "artCharacterStringInput");
  cust->functionAssignCall("artDummySPPFNode", "artFindSPPFInitial", "ARTL_DUMMY", "0", "0");
  cust->assign("artCurrentSPPFNode", "artDummySPPFNode");
  cust->functionAssignCall("artRootGSSNode", findGSSInitialSelect(), "ARTL_EOS", emptyNodeSelect(), "0", emptyNodeSelect());
  cust->assign("artCurrentGSSNode", rootGSSNodeSelect());
  cust->functionCall("artStartClock");
  if (artMGLL)
    cust->functionCall("artLoadDescriptorInitialMGLL");

  /* The line below is _probably_ only needed for recogniser versions: in parsers we test the SPPF for termination */
//  cust->indent();
//  cust->functionCall(findDescriptorSelect(), "ARTX_DESPATCH", "artRootGSSNode", "artLexer.artCurrentInputIndex", "artDummySPPFNode");
  if (artDespatchMode == ART_DESPATCH_STATE || artDespatchMode == ART_DESPATCH_FRAGMENT || artDespatchMode == ART_DESPATCH_STATIC) {
    if (artMGLL)
      cust->assign("artCurrentRestartLabel", "ARTX_DESPATCH");
    else
      cust->assign("artCurrentRestartLabel", "artStartSymbolLabel");
  }
  cust->assign("artLexer.artCurrentInputIndex", "0");
  cust->assign("artLexer.artCurrentInputPairReference", "0");

  artStartSymbol->iftNode->isCodeLabel = true;
  if (artDespatchMode == ART_DESPATCH_STATIC) {
    cust->indent(); jumpSelect("ARTX_DESPATCH_SWITCH");
    cust->indentUp();
  }

  if (artDespatchMode == ART_DESPATCH_DYNAMIC) {
    cust->indent(); jumpSelect(catNodeAsEnumerationElement(artStartSymbol->iftNode, "L"));
    cust->indentUp();
  }

  if (artDespatchMode == ART_DESPATCH_STATE) {
    cust->whileTrue("artSelectState");
    cust->caseOpen(artDespatchMode == ART_DESPATCH_STATE ? "artCurrentRestartLabel" : "artlhsL[artCurrentRestartLabel]"); cust->indentUp();
  }

  if (artDespatchMode == ART_DESPATCH_FRAGMENT) {
    cust->whileTrue(NULL);
    cust->caseOpen(artDespatchMode == ART_DESPATCH_STATE ? "artCurrentRestartLabel" : "artlhsL[artCurrentRestartLabel]");
    cust->indentUp();
    for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
      if (sym->kind == ART_NONTERMINAL && sym->iftNode != NULL) {
        parserBlockOpenSelect(catNodeAsEnumerationElement(sym->iftNode, "L"), NULL);
        cust->functionCall(catSymbolAsParserFragmentName(sym));
        cust->brk();
      }
  }
  else
    writeCodeBodies();

  parserBlockOpenSelect("ARTX_DESPATCH", NULL);
  cust->ifFunction("artNoDescriptors");
  cust->blockOpen();
  cust->newLine(); cust->indentUp();
  cust->functionCall("artCheckAcceptance");
  cust->functionCall("artStopClock");
  cust->ret();
  cust->indentDown();
  cust->indent();
  cust->blockClose();
  if (artMGLL)
    cust->functionCall("artUnloadDescriptorMGLL");
  else
    cust->functionCall("artUnloadDescriptor");

  if (artDespatchMode == ART_DESPATCH_STATIC)
    printStaticSwitch();

  if (artDespatchMode == ART_DESPATCH_STATE || artDespatchMode == ART_DESPATCH_FRAGMENT) {
    cust->caseClose("artCurrentRestartLabel", true);
  }

  cust->indentDown();
  cust->functionClose("artParse"); cust->newLine();
  }

  void writeSetInitialisation() {
  for (mergedSets_data *thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(symbol_get_scope(mergedSets));
     thisSet != NULL;
     thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(thisSet))
  {
   if (!thisSet->usedInParser)
      continue;

    catRewrite("artSet%iinitialise", thisSet->number);
    cust->functionVoidOpen(catBuffer);

    catRewrite("ARTSet%i", thisSet->number);
    cust->allocateBooleanArray(catBuffer, "artSetExtent");
    cust->functionCall("artInitialiseBooleanArray", catBuffer, "0", "artSetExtent", "false");


    catAlt();
    unsigned *elements = set_array(&thisSet->set);
    for (unsigned *temp = elements; *temp < SET_END; temp++)
      cust->assignBooleanArrayElement(catAltBuffer, catRewrite("%i", *temp), "true");

    mem_free(elements);

    catRewrite("artSet%iinitialise", thisSet->number);
    cust->functionClose(catBuffer);
    cust->newLine();
  }

  cust->functionVoidOpen("artSetInitialise");
  for (mergedSets_data *thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(symbol_get_scope(mergedSets));
     thisSet != NULL;
     thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(thisSet))
  {
    if (!thisSet->usedInParser)
      continue;
    catRewrite("artSet%iinitialise", thisSet->number);
    cust->functionCall(catBuffer);
  }
  cust->functionClose("artSetInitialise");
  cust->newLine();
  }

void printLookupTablesNewRec(rdp_tree_node_data * node) {
//  printf("printLookupTablesNewRec() at node %i labelled %s\n", graph_atom_number(node), node->id);
  if (node->deleted)
    return;

  if (node->isSPPFLabel || node->isCodeLabel || node->isReferredLabel || node->isTestRepeatLabel) {
    catNodeAsEnumerationElement(node, "L"); catAlt();
    catRewrite("");
    if (node->isLHS)
      cust->assignStringArrayElement("artLabelInternalStrings", catAltBuffer, catWriteSymbol(node->kind, node->tableEntry->minorID, node->tableEntry->majorID));
    else
      cust->assignStringArrayElement("artLabelInternalStrings", catAltBuffer, catNodeAsGrammarSlot(node, "."));

    catRewrite("");
    if (node->isLHS) {
//      cust->assignStringArrayElement("artLabelStrings", catAltBuffer, catRewriteA(node->id, asciiStrings));
        cust->assignStringArrayElement("artLabelStrings", catAltBuffer, catWriteSymbol(node->kind, node->tableEntry->minorID, node->tableEntry->majorID));
    }
    else
      cust->assignStringArrayElement("artLabelStrings", catAltBuffer, "");

    if (node->isFiPC) {
      catNodeAsEnumerationElement(node, "P"); catAlt();
      cust->assignStringArrayElement("artLabelInternalStrings", catAltBuffer, catNodeAsGrammarSlot(node, ";"));
    }

    if (node->isColon) {
      catNodeAsEnumerationElement(node, "N"); catAlt();
      cust->assignStringArrayElement("artLabelInternalStrings", catAltBuffer, catNodeAsGrammarSlot(node, ":"));
    }
  }

  if (node->lhsL != node)
    if (node->isCodeLabel || node->isSPPFLabel || node->isReferredLabel || node->isTestRepeatLabel || node->isPosParentLabel ||
      node->isClosureLabel || node->isAltLabel) {
      if (node->isClosureLabel) {
        catNodeAsEnumerationElement(node, "C");
        catAlt();
        cust->assignEnumerationArrayElement("artlhsL", catAltBuffer, catNodeAsEnumerationElement(node->lhsL, "L"));
      }
      else if (node->isAltLabel) {
        catNodeAsEnumerationElement(node, "A");
        catAlt();
        cust->assignEnumerationArrayElement("artlhsL", catAltBuffer, catNodeAsEnumerationElement(node->lhsL, "L"));
      }
      else {
        catNodeAsEnumerationElement(node, "L");
        catAlt();
        cust->assignEnumerationArrayElement("artlhsL", catAltBuffer, catNodeAsEnumerationElement(node->lhsL, "L"));
      }
    }

  if (node->kind == ART_POS && node->rightSibling != NULL) {
      catNodeAsEnumerationElement(node->rightSibling, "L");
      catAlt();
      catNodeAsEnumerationElement(node, "L");
  }

  if (node->isSPPFLabel || node->isCodeLabel || node->isReferredLabel || node->isTestRepeatLabel) {
   if (node->kind == ART_POS && node->atomName != NULL) {
     catSymbolAsEnumElement(node->atomName, NULL);
     catAlt();
     cust->assignIntegerArrayElement("artUserNameOfs", catNodeAsEnumerationElement(node, "L"), catAltBuffer);
   }

   if (node->kind == ART_POS && node->niL != node) {
     catNodeAsEnumerationElement(node->niL->tableEntry->iftNode, "L");
     catAlt();
     cust->assignIntegerArrayElement("artSlotInstanceOfs", catNodeAsEnumerationElement(node, "L"), catAltBuffer);
   }

   if (node->kind == ART_POS)
          cust->assignIntegerArrayElement("artKindOfs", catNodeAsEnumerationElement(node, "L"), "ARTK_INTERMEDIATE");

   if (node->kind == ART_LHS)
        cust->assignIntegerArrayElement("artKindOfs", catNodeAsEnumerationElement(node, "L"), "ARTK_NONTERMINAL");

    if (node->pL != node) {
      catNodeAsEnumerationElement(node->pL, "L");
      catAlt();
      cust->assignEnumerationArrayElement("artpL", catNodeAsEnumerationElement(node, "L"), catAltBuffer);
    }

    if (node->aL != node) {
      catNodeAsEnumerationElement(node->aL, "L");
      catAlt();
      cust->assignEnumerationArrayElement("artaL", catNodeAsEnumerationElement(node, "L"), node->aL != NULL ? catAltBuffer : "ARTX_LABEL_EXTENT");
    }

    if (node->isColon) {
      catNodeAsEnumerationElement(node, "N");
      catAlt();
      cust->assignEnumerationArrayElement("artcolonL", catNodeAsEnumerationElement(node, "L"), catAltBuffer);
    }

    if (node->isEoOP)
      cust->assignEnumerationArrayElement("arteoOPL", catNodeAsEnumerationElement(node, "L"), "true");

    if (node->isFiR)
      cust->assignEnumerationArrayElement("artfiRL", catNodeAsEnumerationElement(node, "L"), "true");

    if (node->isFiPC)
      cust->assignEnumerationArrayElement("artfiPCL", catNodeAsEnumerationElement(node, "L"), "true");

    if (node->isEoR)
      cust->assignEnumerationArrayElement("arteoRL", catNodeAsEnumerationElement(node, "L"), "true");

    if (node->pL->isEoR)
      cust->assignEnumerationArrayElement("arteoR_pL", catNodeAsEnumerationElement(node, "L"), "true");

    if (node->isPopD)
      cust->assignEnumerationArrayElement("artPopD", catNodeAsEnumerationElement(node, "L"), "true");
  }

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    printLookupTablesNewRec((rdp_tree_node_data*) graph_destination(edge));
}

  void writeTableInitialiseNew() {
    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol)) {
      if (lhsSymbol->kind != ART_NONTERMINAL || lhsSymbol->iftNode == NULL)
        continue;
      const char *name = strdup(catSymbolAsNontermminalName(lhsSymbol));
      cust->functionVoidOpen(catRewrite("artTableInitialiser_%s", name));
      printLookupTablesNewRec(lhsSymbol->iftNode);
      cust->functionClose(catRewrite("artTableInitialiser_%s", name));
      cust->newLine();
    }

    // Do the allocations and special cases
    cust->functionVoidOpen("artTableInitialise");
    cust->allocateStringArray("artLabelInternalStrings", "ARTX_LABEL_EXTENT + 1");
    cust->allocateStringArray("artLabelStrings", "ARTX_LABEL_EXTENT + 1");
    cust->assignStringArrayElement("artLabelInternalStrings", "ARTL_EOS", "ART$");
    cust->assignStringArrayElement("artLabelStrings", "ARTL_EOS", " EOS $");
    cust->assignStringArrayElement("artLabelInternalStrings", "ARTX_DESPATCH", "ARTX_DESPATCH");
    cust->assignStringArrayElement("artLabelStrings", "ARTX_DESPATCH", " DESPATCH");
    cust->assignStringArrayElement("artLabelInternalStrings", "ARTL_DUMMY", "ARTL_DUMMY");
    cust->assignStringArrayElement("artLabelStrings", "ARTL_DUMMY", " DUMMY");
    cust->assignStringArrayElement("artLabelInternalStrings", "ARTX_LABEL_EXTENT", "!!ILLEGAL!!");
    cust->assignStringArrayElement("artLabelStrings", "ARTX_LABEL_EXTENT", " ILLEGAL");
    cust->assignStringArrayElement("artLabelStrings", "ARTL_EPSILON", "#");
    cust->assignStringArrayElement("artLabelInternalStrings", "ARTL_EPSILON", "#");
    cust->newLine();
    cust->allocateStringArray("artAnnotations", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseStringArray", "artAnnotations", "0", "ARTX_LABEL_EXTENT", cust->nullName());
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artPreSlots", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artPreSlots", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artPostSlots", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artPostSlots", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artInstanceOfs", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artInstanceOfs", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artSlotInstanceOfs", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artSlotInstanceOfs", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateIntegerArray("artKindOfs", "ARTX_LABEL_EXTENT + 1");
    cust->assignIntegerArrayElement("artKindOfs", "ARTL_EOS", "ARTK_EOS");
    cust->assignIntegerArrayElement("artKindOfs", "ARTL_EPSILON", "ARTK_EPSILON");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artUserNameOfs", "ARTX_LABEL_EXTENT + 1");
    cust->newLine();
    cust->allocateBooleanArray("artTerminalRequiresWhiteSpace", "ARTL_EPSILON");
    cust->functionCall("artInitialiseBooleanArray", "artTerminalRequiresWhiteSpace", "0", "ARTL_EPSILON", "false");
    cust->newLine();
    cust->allocateBooleanArray("artTerminalCaseInsensitive", "ARTL_EPSILON");
    cust->functionCall("artInitialiseBooleanArray", "artTerminalCaseInsensitive", "0", "ARTL_EPSILON", "false");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artGathers", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artGathers", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateEnumerationArray("artFold", "artFolds", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artFolds", "0", "ARTX_LABEL_EXTENT", "0");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artlhsL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artlhsL", "0", "ARTX_LABEL_EXTENT");
    cust->assignIntegerArrayElement("artlhsL", "ARTX_DESPATCH", "ARTX_DESPATCH");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artpL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artpL", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artaL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artaL", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateEnumerationArray("artLabel", "artcolonL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseIntegerArray", "artcolonL", "0", "ARTX_LABEL_EXTENT");
    cust->newLine();
    cust->allocateBooleanArray("arteoOPL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseBooleanArray", "arteoOPL", "0", "ARTX_LABEL_EXTENT", "false");
    cust->newLine();
    cust->allocateBooleanArray("artfiRL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseBooleanArray", "artfiRL", "0", "ARTX_LABEL_EXTENT", "false");
    cust->newLine();
    cust->allocateBooleanArray("artfiPCL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseBooleanArray", "artfiPCL", "0", "ARTX_LABEL_EXTENT", "false");
    cust->newLine();
    cust->allocateBooleanArray("arteoRL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseBooleanArray", "arteoRL", "0", "ARTX_LABEL_EXTENT", "false");
    cust->newLine();
    cust->allocateBooleanArray("arteoR_pL", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseBooleanArray", "arteoR_pL", "0", "ARTX_LABEL_EXTENT", "false");
    cust->newLine();
    cust->allocateBooleanArray("artPopD", "ARTX_LABEL_EXTENT");
    cust->functionCall("artInitialiseBooleanArray", "artPopD", "0", "ARTX_LABEL_EXTENT", "false");
    cust->newLine();
//    cust->allocateBooleanArray("artisLexical", "ARTX_LABEL_EXTENT");
//    cust->functionCall("artInitialiseBooleanArray", "artisLexical", "0", "ARTX_LABEL_EXTENT", "false");
//    cust->newLine();

    // Call the individual nonterminal's initialisers and initislaise strings for terminals
    for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       lhsSymbol != NULL;
       lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol)) {

      if (lhsSymbol->isLexical) {
        catSymbolAsEnumElement(lhsSymbol, NULL); catAlt();
        cust->assignBooleanArrayElement("artisLexical", catSymbolAsEnumElement(lhsSymbol, NULL), "true");
      }

      if (lhsSymbol->kind == ART_BUILTIN_TERMINAL ||
          lhsSymbol->kind == ART_CHARACTER_TERMINAL ||
          lhsSymbol->kind == ART_CASE_SENSITIVE_TERMINAL ||
          lhsSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL) {
        catSymbolAsEnumElement(lhsSymbol, NULL); catAlt();
        cust->assignStringArrayElement("artLabelStrings", catAltBuffer, catRewriteAs(lhsSymbol->minorID, asciiStrings));
        catRewrite("");
            cust->assignStringArrayElement("artLabelInternalStrings", catAltBuffer, catWriteSymbol(lhsSymbol->kind, lhsSymbol->minorID, ""));
      }

    if (lhsSymbol->kind == ART_BUILTIN_TERMINAL || lhsSymbol->kind == ART_CHARACTER_TERMINAL ||
    lhsSymbol->kind == ART_CASE_SENSITIVE_TERMINAL || lhsSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL)
      catSymbolAsEnumElement(lhsSymbol, NULL);

    if (lhsSymbol->kind == ART_BUILTIN_TERMINAL)
      cust->assignIntegerArrayElement("artKindOfs", catBuffer, "ARTK_BUILTIN_TERMINAL");

    if (lhsSymbol->kind == ART_CHARACTER_TERMINAL)
      cust->assignIntegerArrayElement("artKindOfs", catBuffer, "ARTK_CHARACTER_TERMINAL");

    if (lhsSymbol->kind == ART_CASE_SENSITIVE_TERMINAL)
      cust->assignIntegerArrayElement("artKindOfs", catBuffer, "ARTK_CASE_SENSITIVE_TERMINAL");

    if (lhsSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL)
      cust->assignIntegerArrayElement("artKindOfs", catBuffer, "ARTK_CASE_INSENSITIVE_TERMINAL");

    if (lhsSymbol->kind == ART_BUILTIN_TERMINAL ||
      lhsSymbol->kind == ART_CASE_SENSITIVE_TERMINAL ||
      lhsSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL)
      cust->assignBooleanArrayElement("artTerminalRequiresWhiteSpace", catSymbolAsEnumElement(lhsSymbol, NULL), "true");

    if (lhsSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL)
      cust->assignBooleanArrayElement("artTerminalCaseInsensitive", catSymbolAsEnumElement(lhsSymbol, NULL), "true");

    if (lhsSymbol->kind == ART_NONTERMINAL && lhsSymbol->iftNode != NULL) {
      const char *name = strdup(catSymbolAsNontermminalName(lhsSymbol));

        cust->functionCall(catRewrite("artTableInitialiser_%s", name));
      }
    }

    cust->functionClose("artTableInitialise"); cust->newLine();
  }

  void writeConstructor(const char *id, symbols_data_node *firstTerminalSymbol) {
    cust->constructorOpenRef(id, "ARTText",  "artTextHandler");

    cust->assign("artText", "artTextHandler");
    // First go through the statically visible functions and make sure their values are appropriately labelled
    updateIsReferredLabelRec(iftRoot);

    if (firstTerminalSymbol == NULL)
      cust->assign("artFirstTerminalLabel", "ARTX_EPSILON");
    else
      cust->assign("artFirstTerminalLabel", catSymbolAsEnumElement(firstTerminalSymbol, NULL));
    cust->assign("artFirstUnusedLabel", "ARTX_LABEL_EXTENT + 1");
    cust->assign("artSetExtent", "ARTX_EPSILON + 1");
    cust->assign("ARTL_EOS", "ARTX_EOS");
    cust->assign("ARTL_EPSILON", "ARTX_EPSILON");
    cust->assign("ARTL_DUMMY", "ARTX_DUMMY");

    cust->assign("artDefaultStartSymbolLabel", catNodeAsEnumerationElement(artStartSymbol->iftNode, "L"));

    catRewrite("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
      artClosureRight?" ClosureRight":"",
      artClosureLeft?" ClosureLeft":"",
      artMultiplyOut?" MultiplyOut":"",
      artLeftFactor?" LeftFactor":"",
      artBracketToNonterminal?" BracketToNonterminal":"",
      artClusteredGSS?" ClusteredGSS":"",
      artDelayPoppingDescriptors?" DelayPoppingDescriptors":"",
      artPredictivePops?" PredictivePops":"",
      artFIFODescriptors?" FIFODescriptors":"",
      " ",artTargetLanguageString,
      " ",artDespatchModeString,
      " ",artSupportModeString,
      artSuppressSemantics?" SuppressSemantics":"",
      artTreeLevel == 0 ? " TreeLevel 0 (Suppress)":
      artTreeLevel == 1 ? " TreeLevel 1 (Derivation)":
      artTreeLevel == 2 ? " TreeLevel 2 (Derivation Annotated)":
      artTreeLevel == 3 ? " TreeLevel 3 (RDT)": "Unknown tree level",
      artEOSFollow ? " EOSFollow":"",
      artMGLL ? " MGLL":"",
      artExploitDeterminism?" ExploitDeterminism":"",
      artSuppressPopGuard?" SuppressPopGuard":"",
      artSuppressProductionGuard?" SuppressProductionGuard":"",
      artSuppressNonterminalGuard?" SuppressNonterminalGuard":"",
      artTestRepeatNo?" TestRepeatNo":"",
      artWarnOnMultiple?" WarnOnMultiple":"",
      artErrorOnWirth?" ErrorOnWirth":"",
      artVerbose?" Verbose":"");

    cust->assignString("artBuildOptions", catBuffer);
    cust->assign("artFIFODescriptors", artFIFODescriptors ? "true" : "false");

    // Call initialisers
    cust->functionCall("artSetInitialise");
    cust->functionCall("artTableInitialise");
    cust->constructorClose("artConstructorCore"); cust->newLine();

    cust->constructorOpen(id);
    cust->functionCall("this", "new ARTText(new ARTDefaultTextHandler())");
    cust->constructorClose(id);
    cust->newLine();
  }

/* Attribute evaluator code below here ****************************************/

void writeRDEvaluatorInstanceName(rdp_tree_node_data* node) {
  if (node->atomName != NULL)
    text_printf("%s", node->atomName);
  else
    text_printf("%s%i", node->tableEntry->minorID, node->instanceNumber);
}

/* Print the code to handle RDT's at the four different levels */
void writeRDEvaluatorLabelArguments(rdp_tree_node_data* iftNode, bool isLeftChild, const char* attributes) {
  text_printf("new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackNode%sChild(artPackNode)), artSPPFNodeRightExtent(artSPPFPackNode%sChild(artPackNode)), artSPPFNodeLabel(artSPPFPackNode%sChild(artPackNode)), %s)",
               isLeftChild ? "Left" : "Right", isLeftChild ? "Left" : "Right", isLeftChild ? "Left" : "Right", attributes);
}

bool writeRDEvaluatorTreeNode(rdp_tree_node_data *iftNode, bool isNonterminal, bool isLeftChild, bool hasAttributes) { // return false iff a node has been single hatted away
//  printf("writeRDEvaluatorTreeNode(node %i, %s, %s, %s)\n", iftNode->nodeNumber, isNonterminal ? "isNonterminal" : "", isLeftChild ? "isLeftChild" : "", hasAttributes ? "hasAttributes" : "");
  switch (artTreeLevel) {
    case 0:  /* no tree to be generated */
      return true;
    case 1:  /* basic derivation tree */
      cust->indent();
      if (isNonterminal)
        text_printf("artNewParent = ");
      text_printf("artParent.addChild(artNextFreeNodeNumber++, ");
      writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? catRewrite("%s%i", iftNode->id, iftNode->instanceNumber) : "null");
        text_printf(");\n");
      break;
    case 2: { /* Basic derivation tree but showing GIFT annotations in the labels */
      cust->indent();
      if (isNonterminal)
        text_printf("artNewParent = ");
      text_printf("artParent.addChild(artNextFreeNodeNumber++, ");
      writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? catRewrite("%s%i", iftNode->id, iftNode->instanceNumber) : "null");
      text_printf(");\n");
      }
      break;
    case 3: {
      cust->indent();
      const char* annotation = NULL;
      switch (iftNode->fold) {
        case ART_FOLD_EMPTY:
        case ART_FOLD_NONE:
        if (isNonterminal)
          text_printf("artNewWriteable = true; artNewParent = ");
        text_printf("artParent.addChild(artNextFreeNodeNumber++, ");
        writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? catRewrite("%s%i", iftNode->id, iftNode->instanceNumber) : "null");
        text_printf(");\n");
        break;

        case ART_FOLD_UNDER:
        if (isNonterminal)
          text_printf("artNewWriteable = false; artNewParent = artParent;\n");
        return false;

        case ART_FOLD_OVER:
        if (isNonterminal)
          text_printf("artNewWriteable = artWriteable; artNewParent = artParent;");
        cust->indent(); text_printf("if (artWriteable) artParent.setPayload(");
        writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? catRewrite("%s%i", iftNode->id, iftNode->instanceNumber) : "null");
        text_printf(");\n");
        break;

        case ART_FOLD_TEAR_PARENT:
        case ART_FOLD_TEAR:
        text_message(TEXT_FATAL, "tear not yet implemented"); break;
        default: text_message(TEXT_FATAL, "unknown fold value found whilst writing tree builder\n"); break;
      }
      }
      break;
    default:
      text_message(TEXT_FATAL, "unknown tree level\n");
  }

  return true;
}

void writeRDEvaluatorLocalDefines(symbols_data *sym, bool useNulls) {
  // Reset counters
  terminalInstanceNumber = 0;
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    thisSymbol->nextInstanceNumber = 0;

  writeRDEvaluatorCollectDeclarationsRec(sym->iftNode);

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    for (int tmp = 1; tmp <= thisSymbol->nextInstanceNumber; tmp++) {
      if (useNulls)
        text_printf(", null");
      else
        text_printf(", ARTAT_%s_%s %s%i", thisSymbol->majorID, thisSymbol->minorID, thisSymbol->minorID, tmp);
    }
}

void writeRDEvaluatorLocalUses(symbols_data *sym) {
  // Reset counters
  terminalInstanceNumber = 0;
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    thisSymbol->nextInstanceNumber = 0;

  writeRDEvaluatorCollectDeclarationsRec(sym->iftNode);

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    for (int tmp = 1; tmp <= thisSymbol->nextInstanceNumber; tmp++) {
      text_printf(", %s%i", thisSymbol->minorID, tmp);
    }
}

void writeRDEvaluatorLocalNull(symbols_data *sym) {
  // Reset counters
  terminalInstanceNumber = 0;
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    thisSymbol->nextInstanceNumber = 0;

  writeRDEvaluatorCollectDeclarationsRec(sym->iftNode);

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    for (int tmp = 1; tmp <= thisSymbol->nextInstanceNumber; tmp++) {
      text_printf(", %s", cust->nullName());
    }
}

void writeRDEvaluatorAttributeUse(symbols_data *sym, int instanceNumber) {
  if (sym->attributes != NULL || sym->containsDelayedInstances) {
    text_printf(", %s", sym->minorID);
    if (instanceNumber != 0)
      text_printf("%i", instanceNumber);
  }
}

void writeRDEvaluatorLocalAllocates(symbols_data *sym) {
  // Reset counters
  terminalInstanceNumber = 0;
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    thisSymbol->nextInstanceNumber = 0;

  writeRDEvaluatorCollectDeclarationsRec(sym->iftNode);

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    for (int tmp = 1; tmp <= thisSymbol->nextInstanceNumber; tmp++) {
      cust->indent(); text_printf("%s%i = new ARTAT_%s_%s();\n", thisSymbol->minorID, tmp, thisSymbol->majorID, thisSymbol->minorID);
    }
}

void writeRDEvaluatorCallInternal(symbols_data *sym, char* element) {
    cust->indent(); text_printf("%s(%s, artParent, artWriteable", catSymbolAsRDEvaluatorName(sym), element);
    writeRDEvaluatorAttributeUse(sym, 0);
    writeRDEvaluatorLocalUses(sym);
    text_printf(");\n");
}

void writeRDEvaluatorCallTopLevel(symbols_data *sym, char* element, int instanceNumber) {
    cust->indent(); text_printf("%s(%s, artNewParent, artNewWriteable", catSymbolAsRDEvaluatorName(sym), element);
    writeRDEvaluatorAttributeUse(sym, instanceNumber);
    writeRDEvaluatorLocalNull(sym);
    text_printf(");\n");
}

void writeRDEvaluatorProcessSemantics(rdp_tree_node_data *node) {
  cust->indent();
//  text_printf("System.out.println(\"");
//  text_printf("%s", catNodeAsGrammarSlot(node));
//  text_printf("\");\n");

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data *child = (rdp_tree_node_data*) graph_destination(edge);
 //   if (child->kind == ART_ANNOTATION)
      if (!artSuppressSemantics)
        text_printf("%s\n", ((rdp_tree_node_data*) graph_destination(graph_next_out_edge(child)))->id);
  }
}

void writeRDEvaluatorProcessLeaf(rdp_tree_node_data *node, bool isLeftChild, rdp_tree_node_data *lhsNode) {
  switch (node->kind) {
  case ART_EPSILON:
  writeRDEvaluatorTreeNode(node, false, isLeftChild, false);
//    cust->indent(); text_printf("artText.println(\"#, %s\");\n", isLeftChild ? "Left" : "Right");
    break;

  case ART_BUILTIN_TERMINAL:
  case ART_CHARACTER_TERMINAL:
  case ART_CASE_SENSITIVE_TERMINAL:
  case ART_CASE_INSENSITIVE_TERMINAL:
  writeRDEvaluatorTreeNode(node, false, isLeftChild, false);
//    cust->indent(); text_printf("artText.println(\"%s, %s\");\n", catSymbolAsEnumElement(node->tableEntry, NULL),  isLeftChild ? "Left" : "Right");
    break;

  case ART_NONTERMINAL:
//    cust->indent(); text_printf("artText.println(\"Entering %s, %s\");\n", catSymbolAsEnumElement(node->tableEntry, NULL), isLeftChild ? "Left" : "Right");
    for (symbols_list_node *tmp = node->tableEntry->attributes; tmp != NULL; tmp = tmp->next) {
      if (strcmp(tmp->symbol->attributeID, "leftExtent") == 0) {
        cust->indent(); writeRDEvaluatorInstanceName(node); text_printf(".leftExtent = artSPPFNodeLeftExtent(%s);\n", isLeftChild ? "artSPPFPackNodeLeftChild(artPackNode)" : "artSPPFPackNodeRightChild(artPackNode)");
      }
      if (strcmp(tmp->symbol->attributeID, "rightExtent") == 0) {
        cust->indent(); writeRDEvaluatorInstanceName(node); text_printf(".rightExtent = artSPPFNodeRightExtent(%s);\n", isLeftChild ? "artSPPFPackNodeLeftChild(artPackNode)" : "artSPPFPackNodeRightChild(artPackNode)");
      }
    }

    writeRDEvaluatorTreeNode(node, true, isLeftChild, node->tableEntry->attributes != NULL);
    if (node->isDelayed) {
      cust->indent(); text_printf("%s.", lhsNode->tableEntry->minorID);
      writeRDEvaluatorInstanceName(node);
      text_printf(" = new ARTGLLRDTHandle(%s);\n", isLeftChild ? "artSPPFPackNodeLeftChild(artPackNode)" : "artSPPFPackNodeRightChild(artPackNode)");
    }
    else
      writeRDEvaluatorCallTopLevel(node->tableEntry, isLeftChild ? (char*) "artSPPFPackNodeLeftChild(artPackNode)" : (char*) "artSPPFPackNodeRightChild(artPackNode)", node->instanceNumber);
//    cust->indent(); text_printf("artText.println(\"Leaving %s, %s\");\n", catSymbolAsEnumElement(node->tableEntry, null), isLeftChild ? "Left" : "Right");
    break;
  }
}

void writeRDEvaluatorRec(rdp_tree_node_data *iftNode, symbols_data *currentNonterminal)
{
  if (strcmp(cust->targetLanguageName(), "Java") != 0)
    return;

  void *edge;
#if 0
    printf("Outputting RDEvaluator for node %i labelled %s\n", graph_atom_number(iftNode), iftNode->id);
    if (iftNode->tableEntry != NULL) {
      printf("  tableEntry points to symbol labelled %s.%s\n", iftNode->tableEntry->majorID, iftNode->tableEntry->minorID);
      if (iftNode->tableEntry->iftNode != NULL)
        printf("    tableEntry iftNode %i labelled %s\n", graph_atom_number(iftNode->tableEntry->iftNode), iftNode->tableEntry->iftNode->id);
    }
#endif

  switch (iftNode->kind) {
  /* The root of the tree is labelled ART_MODULE and has as children the LHS nonterminals: we just recurse through them */
  case ART_MAJOR:
    text_message(TEXT_FATAL, "unexpected major node during RDEvaluator traversal\n");
    break;

  case ART_LHS:
    // Already handled at outer level
    for (edge = graph_next_out_edge(iftNode); edge != NULL; edge = graph_next_out_edge(edge))
      writeRDEvaluatorRec((rdp_tree_node_data*) graph_destination(edge), iftNode->tableEntry);
  break;

  case ART_EPSILON:
    cust->comment("UNEXPECTED nonterminal node");
    break;

  case ART_BUILTIN_TERMINAL:
  case ART_CHARACTER_TERMINAL:
  case ART_CASE_SENSITIVE_TERMINAL:
  case ART_CASE_INSENSITIVE_TERMINAL:
    cust->comment("UNEXPECTED nonterminal node");
    break;

  case ART_NONTERMINAL:
    cust->comment("UNEXPECTED nonterminal node");
    break;

  case ART_DO_FIRST:
    writeRDEvaluatorRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(iftNode)), currentNonterminal);
    break;

  case ART_OPTIONAL:
    cust->comment("optional node");
    break;

  case ART_POSITIVE_CLOSURE:
    cust->comment("positiveClosure node");
    break;

  case ART_KLEENE_CLOSURE:
    cust->comment("kleeneClosure node");
    break;

  case ART_CAT:
  case ART_UNARY: {
  // Rules:
  // 1. Add a call to the semantics for X ::= . \alpha immediately before processing slot X ::= \alpha .
  // 2. There will be no case for X ::= . \alpha
  // 3. For top level cat's only, the slot X ::= y . \alpha (y \in N \cup T) is FiR
  // 4. There will be no case for FiR slots (X ::= y . \alpha (y \in N \cup T))
  // 5. For slots of the form X ::= y z . \alpha (x,y \en N \cup T) this writer function is called recursively on y and z
  // For slots that are not case 1, 4 or 5, the semantics evaluator calls itself recursively on the left child before processing the right child

    rdp_tree_node_data *fiRNode = NULL, *fiRPreviousChildNode = NULL, *previousChildNode = NULL, *childNode = NULL;
    for (edge = graph_next_out_edge(graph_next_out_edge(iftNode)); edge != NULL; edge = graph_next_out_edge(edge)) { // Rule 2: ignore initial pos node
      previousChildNode = childNode;  // Remember left sibling of this child
      childNode = (rdp_tree_node_data*) graph_destination(edge); // Remember this child

      switch (childNode->kind) {
      case ART_DO_FIRST:
      case ART_OPTIONAL:
      case ART_POSITIVE_CLOSURE:
      case ART_KLEENE_CLOSURE:
        writeRDEvaluatorRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(childNode)), currentNonterminal);
        break;

      case ART_POS:
        if (childNode->isFiR) {
          fiRNode = childNode;
          fiRPreviousChildNode = previousChildNode;
        }
        else {
          cust->indent(); cust->comment(catNodeAsGrammarSlot(childNode, " . "));
          cust->caseBranchOpen(catNodeAsEnumerationElement(childNode, "L"), false); // Rule 2 and 3: there will be a case branch for all others
          childNode->isCodeLabel = true;  // This is the extra codeLabel that is missing from the enumeration

          if (graph_next_out_edge(edge) == NULL) { // Rule 1: initialise attribute variables and call the initial semantics before we process this slot
            writeRDEvaluatorLocalAllocates(currentNonterminal);
#if 0
            for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
              for (int tmp = 1; tmp <= thisSymbol->nextInstanceNumber; tmp++) {
                cust->indent(); text_printf("%s%i = new ARTAT_%s_%s();\n", thisSymbol->minorID, tmp, thisSymbol->majorID, thisSymbol->minorID);
              }
#endif
            writeRDEvaluatorProcessSemantics((rdp_tree_node_data*) graph_destination(graph_next_out_edge(iftNode)));
          }

          if (fiRNode != NULL) { // Rule 5: process y and output the semantics for the FiR node
            writeRDEvaluatorProcessLeaf(fiRPreviousChildNode, true, iftNode->lhsL);
            writeRDEvaluatorProcessSemantics(fiRNode);
            fiRNode = NULL; // switch off fIr processing for this cat
          }
          else // Not rule 5: descend left child so that we are processing RHS in postorder
            writeRDEvaluatorCallInternal(currentNonterminal, (char*) "artSPPFPackNodeLeftChild(artPackNode)");

          writeRDEvaluatorProcessLeaf(previousChildNode, false, iftNode->lhsL);
          writeRDEvaluatorProcessSemantics(childNode);

          cust->brk();
          cust->indentDown();
        }
        break;
      }
    }
    break;
  }

  case ART_ALT:
    cust->comment("alt node");
    break;

  case ART_POS:
    text_message(TEXT_FATAL, "unexpected POS node whilst outputting RD evaluator\n");
    break;

  default: text_redirect(stdout); text_message(TEXT_FATAL, "unknown tree node kind %i labelled '%s' found during parser output\n", iftNode->kind, iftNode->id);
  }
}

  int terminalInstanceNumber;

  void writeRDEvaluatorCollectDeclarationsRec(rdp_tree_node_data* node) {
  switch (node->kind) {
    case ART_NONTERMINAL:
      if ((node->tableEntry->attributes != NULL || node->tableEntry->containsDelayedInstances || node->isDelayed) && node->instanceNumber > node->tableEntry->nextInstanceNumber)
        node->tableEntry->nextInstanceNumber = node->instanceNumber;
      break;
    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
      if (node->instanceNumber > terminalInstanceNumber)
         terminalInstanceNumber = node->instanceNumber;
    break;
  }

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    writeRDEvaluatorCollectDeclarationsRec((rdp_tree_node_data*) graph_destination(edge));

  }

  void writeRDEvaluatorBodies(symbols_data *sym) {

     text_printf("    for (int artPackNode = artSPPFNodePackNodeList(artElement); artPackNode != 0; artPackNode = artSPPFPackNodePackNodeList(artPackNode)) {\n"
                  "      if (artSPPFPackNodeSelected(artPackNode)) {\n"
                  "        switch (artSPPFPackNodeLabel(artPackNode)) {\n");
    cust->indentUp();
    cust->indentUp();

    writeRDEvaluatorRec(sym->iftNode, sym);

    if (strcmp(cust->targetLanguageName(), "Java") == 0)
      text_printf("        default:\n"
                  "          artText.println(ARTTextLevel.FATAL,\"Unhandled pack node \" + artPackNode + \" with label \" + artSPPFPackNodeLabel(artPackNode) + \" - \" + artLabelInternalStrings[artSPPFPackNodeLabel(artPackNode)] + \" in evaluator for %s\");\n"
                  "          break;\n"
                  "        }\n"
                  "      }\n"
                  "    }\n",
                  catSymbolAsRDEvaluatorName(sym)
                 );
    cust->indentDown();
    cust->indentDown();
    cust->functionClose(catSymbolAsRDEvaluatorName(sym));
    cust->newLine();
  }

  void writeRDEvaluator() {
    // Write attribute block classes
    for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
      if (sym->kind == ART_NONTERMINAL && (sym->containsDelayedInstances || sym->hasDelayedInstances || sym->attributes != NULL)) {
//        printf("Writing attribute block class for %s\n", sym->minorID);
        cust->indent(); text_printf("public static class ARTAT_%s_%s extends ARTGLLAttributeBlock {\n", sym->majorID, sym->minorID);
        cust->indentUp();
        for (symbols_list *tmp = supportAttributes; tmp != NULL; tmp = tmp->next) {
          cust->indent(); text_printf("protected %s %s;\n", tmp->symbol->attributeType, tmp->symbol->attributeID);
        }
        for (symbols_list *tmp = sym->attributes; tmp != NULL; tmp = tmp->next) {
          cust->indent(); text_printf("protected %s %s;\n", tmp->symbol->attributeType, tmp->symbol->attributeID);
        }

    for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
      thisSymbol->nextInstanceNumber = 0;

    writeRDEvaluatorCollectDeclarationsRec(sym->iftNode);

    for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); thisSymbol != NULL; thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
      if (thisSymbol->kind == ART_NONTERMINAL)
        for (int tmp = 1; tmp <= thisSymbol->nextInstanceNumber; tmp++) {
          cust->indent(); text_printf("ARTGLLRDTHandle %s%i;\n", thisSymbol->minorID, tmp);
        }

        cust->indent(); text_printf("public String toString() {\n");
        cust->indent(); text_printf("  String ret = \"\";\n");
        for (symbols_list *tmp = sym->attributes; tmp != NULL; tmp = tmp->next) {
          cust->indent(); text_printf("ret += \" %s=\" + %s;\n", tmp->symbol->attributeID, tmp->symbol->attributeID);
        }

        cust->indent(); text_printf("  return ret + \" \";\n}\n");

      cust->indentDown();
      cust->indent(); text_printf("}\n\n");
    }

    // Write fragment forward declarations - nothing to do for Java

    // Now write out the fragments
    for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
      if (sym->kind == ART_NONTERMINAL) {
        if (sym->attributes == NULL && !sym->containsDelayedInstances) {
          cust->indent(); text_printf("public void %s(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable", catSymbolAsRDEvaluatorName(sym));
        }
        else {
          cust->indent(); text_printf("public void %s(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_%s_%s %s", catSymbolAsRDEvaluatorName(sym), sym->majorID, sym->minorID, sym->minorID);
        }
        writeRDEvaluatorLocalDefines(sym, false);
        text_printf(") {\n");
        cust->indent(); text_printf("ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;\n");
        writeRDEvaluatorBodies(sym);
    }

    // Write out the indirect evaluator despatch function
    cust->indent(); text_printf("public void artEvaluate(ARTGLLRDTHandle artElement, Object artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable) {\n"); cust->indentUp();
    cust->indent(); text_printf("switch (artSPPFNodeLabel(artElement.element)) {\n"); cust->indentUp();

    for (symbols_data *sym = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); sym != NULL; sym = (symbols_data*) symbol_next_symbol_in_scope(sym))
      if (sym->kind == ART_NONTERMINAL) {
        if (sym->attributes == NULL && !sym->containsDelayedInstances) {
          cust->indent(); text_printf("case %s: ", catSymbolAsEnumElement(sym, NULL));
          text_printf("%s(artElement.element, artParent, artWriteable", catSymbolAsRDEvaluatorName(sym));
        }
        else {
          cust->indent(); text_printf("case %s: ", catSymbolAsEnumElement(sym, NULL));
          text_printf(" %s(artElement.element, artParent, artWriteable, (ARTAT_%s_%s) artAttributes", catSymbolAsRDEvaluatorName(sym), sym->majorID, sym->minorID);
        }
        writeRDEvaluatorLocalDefines(sym, true);
        text_printf("); break;\n");
      }

    cust->indentDown(); cust->indent(); text_printf("}\n");
    cust->indentDown(); cust->indent(); text_printf("}\n\n");

    // Finally, write out the evaluator harness
    if (artGrammarIsEBNF) {
      cust->indent(); text_printf("public ARTGLLRDT artEvaluator() {\n"); cust->indentUp();
      cust->indent(); text_printf("artText.println(\"artEvaluator not supported for EBNF grammars\");\n");

      cust->indent(); text_printf("return null;\n");
      cust->functionClose("artEvaluator");
      return;
    }

    if (artStartSymbol->containsDelayedInstances || artStartSymbol->hasDelayedInstances || artStartSymbol->attributes != NULL) {
      cust->indent(); text_printf("public ARTGLLRDT artEvaluator() {\n"); cust->indentUp();
      cust->indent(); text_printf("ARTAT_%s_%s %s1 = new ARTAT_%s_%s();\n", artStartSymbol->majorID, artStartSymbol->minorID, artStartSymbol->minorID, artStartSymbol->majorID, artStartSymbol->minorID);
      cust->indent(); text_printf("return"); cust->functionCall("artEvaluator", catRewrite("%s1", artStartSymbol->minorID));
      cust->functionClose("artEvaluator");

      cust->indent(); text_printf("\npublic ARTGLLRDT artEvaluator(ARTAT_%s_%s %s1) {\n", artStartSymbol->majorID, artStartSymbol->minorID, artStartSymbol->minorID); cust->indentUp();
      cust->indent(); text_printf("artRDT = new ARTGLLRDT(\"RDT\", artKindOfs, artLabelStrings, artAnnotations, artLexer.artCharacterStringInput);\n");
      cust->indent(); text_printf("ARTGLLRDTVertex artNewParent = new ARTGLLRDTVertex(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artRootSPPFNode),artSPPFNodeRightExtent(artRootSPPFNode),artSPPFNodeLabel(artRootSPPFNode), %s1));\n", artStartSymbol->minorID);
      cust->indent(); text_printf("artRDT.setRoot(artNewParent);\n");
      cust->indent(); text_printf("boolean artNewWriteable = true;\n");
      cust->indent(); text_printf("artEvaluate(new ARTGLLRDTHandle(artRootSPPFNode), %s%i, artNewParent, artNewWriteable);\n", artStartSymbol->minorID, 1);
      cust->indent(); text_printf("return artRDT;\n");
      cust->functionClose("artEvaluator");
  } else {
      cust->indent(); text_printf("public ARTGLLRDT artEvaluator() {\n"); cust->indentUp();
      cust->indent(); text_printf("artRDT = new ARTGLLRDT(\"RDT\", artKindOfs, artLabelStrings, artAnnotations, artLexer.artCharacterStringInput);\n");
      cust->indent(); text_printf("ARTGLLRDTVertex artNewParent = new ARTGLLRDTVertex(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artRootSPPFNode),artSPPFNodeRightExtent(artRootSPPFNode),artSPPFNodeLabel(artRootSPPFNode), null));\n");
      cust->indent(); text_printf("artRDT.setRoot(artNewParent);\n");
      cust->indent(); text_printf("boolean artNewWriteable = true;\n");
      cust->indent(); text_printf("artEvaluate(new ARTGLLRDTHandle(artRootSPPFNode), null, artNewParent, artNewWriteable);\n" );
      cust->indent(); text_printf("return artRDT;\n");
      cust->functionClose("artEvaluator");
    }

  }

 artParserWriter(const char* moduleId, const char* id, const char* super, const char* filename, const char* lexerName, rdp_tree_node_data* iftRoot, void* symbols, artParserCustomisation *c) {

    this->iftRoot = iftRoot;
    this->symbols = symbols;
    cust = c;
    lhsTemplateCount = 0;
    epsilonTemplateCount = 0;
    terminalTemplateCount = 0;
    nonterminalTemplateCount = 0;
    concatenationTemplateCount = 0;
    alternateTemplateCount = 0;
    doFirstTemplateCount = 0;
    optionalNonNullableTemplateCount = 0;
    optionalNullableTemplateCount = 0;
    positiveNonNullableTemplateCount = 0;
    positiveNullableTemplateCount = 0;
    kleeneNonNullableTemplateCount = 0;
    kleeneNullableTemplateCount = 0;
    catBuffer = new char[catBufferSize];
    catAltBuffer = new char[catBufferSize];

    cust->indentSet(0);
    cust->fileOpen(filename, moduleId);
    if (strcmp(cust->targetLanguageName(), "Java") == 0 && preludeText != NULL)
      text_printf("%s\n", preludeText);
    if (super == NULL) cust->classOpen(id); else cust->classOpen(id, super, strcmp(cust->targetLanguageName(), "Java") ? "ARTGLLAPI" : NULL);
    cust->indentUp();
    writeSetInitialiserDeclarations();
    symbols_data_node *firstTerminalSymbol = writeARTLabelEnumeration();
    writeARTNameEnumeration();
//    writeLexicaliseBuiltinInstances(false, NULL);
//    writeLexicalisePreparseWhitespaceInstances(false);
    writeParseGenerated();
    writeSetInitialisation();
    writeTableInitialiseNew();
    writeConstructor(id, firstTerminalSymbol);
    if (strcmp(cust->targetLanguageName(), "Java") == 0) {
      cust->indent();  text_printf("private ARTGLLRDT artRDT;\n");
      cust->indent(); text_printf("private int artNextFreeNodeNumber = 1;\n");
      if (supportText != NULL)
        text_printf("%s\n\n", supportText);
      writeRDEvaluator();
    }
    cust->indentDown();
    cust->classClose(id);
    cust->fileClose(filename, moduleId);

    cust->indentSet(0);
    cust->fileOpen(lexerName, moduleId);
    cust->classOpen(lexerName, "ARTGLLLexerBase", NULL);

    cust->constructorOpen(lexerName, "ARTGLLParserBase", "artGLLParser");
    cust->functionCall("super", "artGLLParser");
    cust->constructorClose(lexerName);

    writeLexicaliseBuiltinInstances(true, filename);
    writeLexicalisePreparseWhitespaceInstances(true);
    cust->indentDown();
    cust->classClose(id);
    cust->fileClose(lexerName, moduleId);
  }
};

