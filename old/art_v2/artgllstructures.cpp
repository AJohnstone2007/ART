/*******************************************************************************
*
* ART V1.00 production GLL parsing support - datastructure support (c) Adrian Johnstone 2011
*
*******************************************************************************/
#include "bccgcc.h"
#include "artgllgenerated.h"
#include "artgllstructures.h"
#include "artgll.h"

// hash functions

unsigned container::hashMask;
unsigned container::hashBucketCount;
artLabel* container::tokenString;
unsigned container::inputExtent;

containerElement **container::hashTable;

void container::sane() {
}

int container::printStatistics(const char *grammarName, const char *buildOptions, const char *startSymbolName, const char *tokenStringName, artLabel* tokenString, int ambiguityCount, artLabel* tokenLast, bool inLanguage, double runTime, const char** artLabelStrings, enum artFold *foldL) {
  tokenStringName = ""; /*debug */
  int gssNodeCount = 0;
  int gssEdgeCount = 0;
  int maxUi = 0;
  int sppfNonPnodeCount = 0;
  int sppfPnodeCount = 0;
  int sppfEdgeCount = 0;
  int descriptorCount = 0;
  int testRepeatElementCount = 0;
  int popElementCount = 0;
  int maxExtent = 0;

  artHistogram *hist = new artHistogram();
  int *uCardinality = new int[tokenLast - tokenString + 1];

  for (int tmp = 0; tmp < tokenLast-tokenString + 1; tmp++)
    uCardinality[tmp] = 0;

  // Scan hash table, gathering statistics
  for (int temp = 0; temp < container::hashBucketCount; temp++) {
    unsigned long bucketCardinality = 0;

//    printf("Scanning hash table bucket %i\n", temp);
    for (containerElement *element = container::hashTable[temp]; element != NULL; element = element->next) {
      bucketCardinality++;
      SPPFPackNode *tmp;
      switch (element->kind) {
        case CK_SPPFPackNode: sppfPnodeCount++; break;
        case CK_SPPFNode:
          sppfNonPnodeCount++;
          if (maxExtent < ((SPPFNode *) element)->rightExtent - tokenString)
            maxExtent = ((SPPFNode *) element)->rightExtent - tokenString;

          // Count pack node edges
          for (tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeNext()) {
            sppfEdgeCount++;
            if (tmp->leftChildLabel != ART_L_DUMMY)
              sppfEdgeCount++;
            if (tmp->rightChildLabel != ART_L_DUMMY)
              sppfEdgeCount++;
          }
          break;
        case CK_GSSEdge: gssEdgeCount++; break;
        case CK_GSSNode: gssNodeCount++; break;
        case CK_popElement: popElementCount++; break;
        case CK_descriptor: descriptorCount++; uCardinality[((descriptor*) element)->currentToken - tokenString]++; break;
        case CK_testRepeatElement: testRepeatElementCount++; break;
        default: cout << "Internal error: unknown tag type " << element->kind << " found in hash table\n";
      }
    }
//    hist->update(bucketCardinality);
  }

  for (int tmp = 0; tmp < (tokenLast-tokenString + 1); tmp++) {
//    cout << "walking uCardinality at element " << tmp << " with maxUi currently " << maxUi << " and uCardinality " << uCardinality[tmp] << "\n";
    if (uCardinality[tmp] > maxUi)
      maxUi = uCardinality[tmp];
  }

#if defined(TRACE)
  for (int temp = 0; temp < container::hashBucketCount; temp++)
    for (containerElement *element = container::hashTable[temp]; element != NULL; element = element->next)
      switch (element->kind) {
        case CK_SPPFNode:
          int cardinality = 0;
          SPPFPackNode* tmp;
          for (tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeNext())
            cardinality++;

          if (cardinality > 1) {
            cout << "Ambiguity under SPPP node labelled ("
                 << artLabelStrings[((SPPFNode*) element)->label] << ","
                 << (((SPPFNode*) element)->leftExtent - tokenString) << ","
                 << (((SPPFNode*) element)->rightExtent - tokenString) << "): ";

            ((SPPFNode*) element)->ambiguous = true;

            for (tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeNext()) {
              cout << "(" << artLabelStrings[tmp->L] <<
              "," <<
              (tmp->pivot - tokenString) <<
              ") ";
            }
            cout << "\n";
          }
          break;
      }
#endif

#if defined(TRACE)
  cout << "\nHash list cardinality histogram\n\n";
  hist->print();

  cout << "\nHash table dump\n\n";

  for (int temp = 0; temp < container::hashBucketCount; temp++) {
    for (containerElement *element = container::hashTable[temp]; element != NULL; element = element->next) {
      SPPFPackNode *pn = (SPPFPackNode*) element;
      SPPFNode *sn = (SPPFNode*) element;
      GSSNode *gn = (GSSNode*) element;
      GSSEdge *ge = (GSSEdge*) element;
      descriptor *d = (descriptor*) element;
      testRepeatElement *tr = (testRepeatElement*) element;
      popElement *p = (popElement*) element;

      switch (element->kind) {

        case CK_SPPFPackNode: cout << "Pack node (" << artLabelStrings[pn->L] << ", " << (pn->pivot - tokenString) << ", " << artLabelStrings[pn->leftChildLabel] << ", " << artLabelStrings[pn->rightChildLabel] << ")\n"; break;
        case CK_SPPFNode: cout << "SPPF node (" << artLabelStrings[sn->label] << ", " << (sn->leftExtent - tokenString) << ", "  << (sn->rightExtent - tokenString) << ")\n"; break;

        case CK_GSSEdge: cout << "GSS edge from (" << artLabelStrings[ge->source->label] << ", " << (ge->source->level - tokenString) << ") to (" << artLabelStrings[ge->destination->label] << ", " << (ge->destination->level - tokenString) << ")";
                         if (ge->edgeLabel->kind != CK_SPPFNode)
                           cout << "illegal label tag " << ge->edgeLabel->kind << "\n";
                         else
                            cout << " (" << artLabelStrings[ge->edgeLabel->label] << ", " << (ge->edgeLabel->leftExtent - tokenString) << ", " << (ge->edgeLabel->rightExtent - tokenString) << ")\n";
                         break;

        case CK_GSSNode: cout << "GSS node (" << artLabelStrings[gn->label] << ", " << (gn->level - tokenString) << ")\n"; break;
        case CK_descriptor: cout << "Descriptor (" << artLabelStrings[d->restartLabel] <<
                                    ", (" << artLabelStrings[d->stackTop->label] << ", " << (d->stackTop->level - tokenString) <<
                                    "), " << artLabelStrings[*(d->currentToken)] <<
                                    ", (" << artLabelStrings[d->derivationNode->label] << ", " << (d->derivationNode->leftExtent - tokenString) << ", "  << (d->derivationNode->rightExtent - tokenString) << "))\n";
                                    break;
        case CK_testRepeatElement: cout << "TestRepeat element (" << artLabelStrings[tr->regexpLabel] <<
                                    ", (" << artLabelStrings[tr->stackTop->label] << ", " << (tr->stackTop->level - tokenString) <<
                                    "), " << artLabelStrings[*(tr->currentToken)] <<
                                    ", (" << artLabelStrings[tr->derivationNode->label] << ", " << (tr->derivationNode->leftExtent - tokenString) << ", "  << (tr->derivationNode->rightExtent - tokenString) << "))\n";
                                    break;
        case CK_popElement: cout << "Pop element ( (" << artLabelStrings[p->stackTop ->label] << ", " << (p->stackTop->level - tokenString) << ") (" <<
                                     artLabelStrings[p->derivationNode->label] << ", " << (p->derivationNode->leftExtent - tokenString) << ", "  << (p->derivationNode->rightExtent - tokenString) << "))\n";"\n"; break;

        default: cout << "Unexpected hash element found with tag " << element->kind << "\n";
      }
    }
  }
#endif

  if (sppfNonPnodeCount < 1000) {// write out GSS
    cout << "Writing VCG rendering gllgss.vcg\n";

    ofstream gssFile;
    gssFile.open ("gllgss.vcg");
    gssFile <<
    "graph:{\n"
    "orientation:left_to_right\n"
    "edge.arrowsize:7\n"
    "edge.thickness:1\n"
    "display_edge_labels:yes\n"
    "arrowmode:free\n"
    "node.borderwidth:1\n"
    "node:{title:\"" <<
    "ART_L_DUMMY" <<
    ", 0\"level:0 horizontal_order:1}\n";

    for (int temp = 0; temp < container::hashBucketCount; temp++) {
      for (containerElement *element = container::hashTable[temp]; element != NULL; element = element->next) {
        GSSNode *gn = (GSSNode*) element;
        GSSEdge *ge = (GSSEdge*) element;

        switch (element->kind) {
          case CK_GSSEdge: gssFile << "edge:{sourcename:\"" <<
                                      artLabelStrings[ge->source->label] <<
                                      ", " <<
                                      (ge->source->level - tokenString) <<
                                      "\" targetname:\"" <<
                                      artLabelStrings[ge->destination->label] <<
                                      ", " <<
                                      (ge->destination->level - tokenString) <<
                                      "\" label:\"" <<
                                      artLabelStrings[ge->edgeLabel->label] <<
                                      ", " <<
                                      (ge->edgeLabel->leftExtent - tokenString) <<
                                      ", " <<
                                      (ge->edgeLabel->rightExtent - tokenString) <<
                                      "\"}\n";
          break;
          case CK_GSSNode: gssFile << "node:{title:\"" <<
                                      artLabelStrings[gn->label] <<
                                      ", " <<
                                      (gn->level - tokenString) <<
                                      "\"level:" <<
                                      (gn->level - tokenString) <<
                                      " horizontal_order: " <<
                                      (int) (gn->label) <<
                                      "}\n";
          break;
        }
      }
    }
    gssFile << "}\n";
    gssFile.close();
  }

  if (!inLanguage) {
    for (int temp = 0; temp < (tokenLast - tokenString); temp++)
      cout << temp << ": " << tokenString[temp] << "   " << artLabelStrings[tokenString[temp]] << "\n";
    cout << "Reject: longest putative derivation consumed " << maxExtent << " token" << (maxExtent == 1 ? "\n" :"s\n");
  }

  double tokensPerSecond = runTime > 0.001 ? ((double) (tokenLast - tokenString)) / runTime : 0;

  cout << (inLanguage ? "accept" : "reject") << ", grammar, " << grammarName << ", build options, " << buildOptions << ", start symbol, " <<
          startSymbolName << ", input, " << tokenStringName << ", tokens, " << (tokenLast - tokenString) << ", CPU sec, " <<
#if defined(SUPPRESS_RUNTIME)
"XXXX"
#else
runTime
#endif

#if defined(STATISTICS)
      << ", tokens/second, " << tokensPerSecond <<
          ", ambiguous nodes, " << ambiguityCount <<
          ", GSS nodes, " << gssNodeCount << ", GSS edges, " << gssEdgeCount << ", Sum |Ui|, "<< descriptorCount << ", max |Ui|, " << maxUi <<
          ", SPPF nonPnodes, " << sppfNonPnodeCount << ", SPPF Pnodes, " << sppfPnodeCount << ", SPPF edges, " << sppfEdgeCount  <<
          ", Pop elements, " << popElementCount <<
          ", testRepeat elements, " << testRepeatElementCount
#endif
  ;

  return sppfNonPnodeCount;
}

bool container::listEmpty() {
  return list == NULL;
}

void container::pushDescriptor(struct descriptor *newDescriptor) {
  listAnyElement *tmp = new listAnyElement;
  tmp->payload = (containerElement*) newDescriptor;
  tmp->next = list;
  list = tmp;
}

void container::pushSPPFPackNode(struct SPPFPackNode * newPackNode) {
  listAnyElement *tmp = new listAnyElement;
  tmp->payload = (containerElement*) newPackNode;
  tmp->next = list;
  list = tmp;
}

descriptor* container::popDescriptor() {
  listAnyElement *tmp = list;
  list = list->next;
  return (descriptor*) tmp->payload;
}

descriptor* container::notinDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
  int bucket = hash(CK_descriptor, restartLabel, stackTop->label, derivationNode->label, stackTop->level, currentToken, derivationNode->leftExtent, derivationNode->rightExtent);

  for (descriptor *tmp = (descriptor*) container::hashTable[bucket];
       tmp != NULL;
       tmp = (descriptor*) tmp->next)
    if (tmp->kind == CK_descriptor && tmp->restartLabel == restartLabel && tmp->stackTop == stackTop && tmp->currentToken == currentToken && tmp->derivationNode == derivationNode)
      return tmp;

  return NULL;
}

descriptor* container::addtoDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
  int bucket = hash(CK_descriptor, restartLabel, stackTop->label, derivationNode->label, stackTop->level, currentToken, derivationNode->leftExtent, derivationNode->rightExtent);

  descriptor *tmp = new descriptor;

  tmp->kind = CK_descriptor;
  tmp->next = container::hashTable[bucket];
  container::hashTable[bucket] = (containerElement*) tmp;

  tmp->restartLabel = restartLabel;
  tmp->stackTop = stackTop;
  tmp->currentToken = currentToken;
  tmp->derivationNode = derivationNode;

  return tmp;
}

testRepeatElement* container::notinTestRepeatElement(artLabel regexpLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
  int bucket = hash(CK_testRepeatElement, regexpLabel, stackTop->label, derivationNode->label, stackTop->level, currentToken, derivationNode->leftExtent, derivationNode->rightExtent);

  for (testRepeatElement *tmp = (testRepeatElement*) container::hashTable[bucket];
       tmp != NULL;
       tmp = (testRepeatElement*) tmp->next)
    if (tmp->kind == CK_testRepeatElement && tmp->regexpLabel == regexpLabel && tmp->stackTop == stackTop && tmp->currentToken == currentToken && tmp->derivationNode == derivationNode)
      return tmp;

  return NULL;
}

testRepeatElement* container::addtoTestRepeatElement(artLabel regexpLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
  int bucket = hash(CK_testRepeatElement, regexpLabel, stackTop->label, derivationNode->label, stackTop->level, currentToken, derivationNode->leftExtent, derivationNode->rightExtent);

  testRepeatElement *tmp = new testRepeatElement;

  tmp->kind = CK_testRepeatElement;
  tmp->next = container::hashTable[bucket];
  container::hashTable[bucket] = (containerElement*) tmp;

  tmp->regexpLabel = regexpLabel;
  tmp->stackTop = stackTop;
  tmp->currentToken = currentToken;
  tmp->derivationNode = derivationNode;

  return tmp;
}

popElement* container::notinPopElement(GSSNode* stackTop, SPPFNode *derivationNode) {
  int bucket = hash(CK_popElement, stackTop->label, derivationNode->label, stackTop->level, derivationNode->leftExtent, derivationNode->rightExtent);

  for (popElement *tmp = (popElement*) container::hashTable[bucket];
       tmp != NULL;
       tmp = (popElement*) tmp->next)
    if (tmp->kind == CK_popElement &&
        tmp->stackTop->label == stackTop->label && tmp->stackTop->level == stackTop->level &&
        tmp->derivationNode->label == derivationNode->label && tmp->derivationNode->leftExtent == derivationNode->leftExtent && tmp->derivationNode->rightExtent == derivationNode->rightExtent)
      return tmp;

  return NULL;
}

void container::addtoAndPushPopElement(GSSNode* stackTop, SPPFNode *derivationNode) {

  int bucket = hash(CK_popElement, stackTop->label, derivationNode->label, stackTop->level, derivationNode->leftExtent, derivationNode->rightExtent);

  popElement *tmp = new popElement;

  tmp->kind = CK_popElement;
  tmp->next = container::hashTable[bucket];
  container::hashTable[bucket] = (containerElement*) tmp;

  tmp->stackTop = stackTop;
  tmp->derivationNode = derivationNode;

  listAnyElement *lTmp = new listAnyElement;
  lTmp->payload = (containerElement*) tmp;
  lTmp->next = list;
  list = lTmp;
}

GSSNode* container::notinGSS(artLabel stackTopLabel, artLabel* currentToken) {
  int bucket = hash(CK_GSSNode, stackTopLabel, currentToken);

  for (GSSNode *tmp = (GSSNode*) container::hashTable[bucket];
       tmp != NULL;
       tmp = (GSSNode*) tmp->next)
    if (tmp->kind == CK_GSSNode && tmp->label == stackTopLabel && tmp->level == currentToken)
      return tmp;

  return NULL;
}

GSSNode* container::addtoGSS(artLabel stackTopLabel, artLabel* currentToken) {
  int bucket = hash(CK_GSSNode, stackTopLabel, currentToken);

  GSSNode *tmp = new GSSNode;

  tmp->kind = CK_GSSNode;
  tmp->next = container::hashTable[bucket];
  container::hashTable[bucket] = (containerElement*) tmp;

  tmp->label = stackTopLabel;
  tmp->level = currentToken;

  return tmp;
}

SPPFNode* container::notinSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent) {
  int bucket = hash(CK_SPPFNode, label, leftExtent, rightExtent);

  for (SPPFNode *tmp = (SPPFNode*) container::hashTable[bucket];
       tmp != NULL;
       tmp = (SPPFNode*) tmp->next)
    if (tmp->kind == CK_SPPFNode && tmp->label == label && tmp->leftExtent == leftExtent && tmp->rightExtent == rightExtent) {
      return tmp;
    }

  return NULL;
}

SPPFNode* container::addtoSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent) {
  int bucket = hash(CK_SPPFNode, label, leftExtent, rightExtent);

  SPPFNode *tmp = new SPPFNode;

  tmp->kind = CK_SPPFNode;
  tmp->next = container::hashTable[bucket];
  container::hashTable[bucket] = (containerElement*) tmp;

  tmp->label = label;
  tmp->leftExtent = leftExtent;
  tmp->rightExtent = rightExtent;

  return tmp;
}

SPPFPackNode* container::notinPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken) {
  int bucket = hash(CK_SPPFPackNode, parent->label, stackTopLabel, parent->leftExtent, parent->rightExtent, currentToken);

  for (SPPFPackNode *tmp = (SPPFPackNode*) container::hashTable[bucket];
       tmp != NULL;
       tmp = (SPPFPackNode*) tmp->next)
    if (tmp->kind == CK_SPPFPackNode && tmp->parent == parent && tmp->L == stackTopLabel && tmp->pivot == currentToken)
      return tmp;

  return NULL;
}

SPPFPackNode* container::addtoPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken) {
  int bucket = hash(CK_SPPFPackNode, parent->label, stackTopLabel, parent->leftExtent, parent->rightExtent, currentToken);

  SPPFPackNode *tmp = new SPPFPackNode;

  tmp->kind = CK_SPPFPackNode;
  tmp->next = container::hashTable[bucket];
  container::hashTable[bucket] = (containerElement*) tmp;

  tmp->parent = parent;
  tmp->L = stackTopLabel;
  tmp->pivot = currentToken;

  /* Now insert into parents list of elements */
  parent->packNodes.pushSPPFPackNode(tmp);

  return tmp;
}

GSSEdge* container::notinGSSEdge(GSSNode *newGSSNode, SPPFNode *currentSPPFNode, GSSNode *stackTop) {
  int bucket = hash(CK_GSSEdge, newGSSNode->label, currentSPPFNode->label, stackTop->label, newGSSNode->level, currentSPPFNode->leftExtent, currentSPPFNode->rightExtent, stackTop->level);

  for (GSSEdge *tmp = (GSSEdge*) container::hashTable[bucket];
       tmp != NULL;
       tmp = (GSSEdge*) tmp->next)
    if (tmp->kind == CK_GSSEdge && tmp->source == newGSSNode && tmp->edgeLabel == currentSPPFNode && tmp->destination == stackTop)
      return tmp;

  return NULL;
}

GSSEdge* container::addtoAndPushGSSEdge(GSSNode *newGSSNode, SPPFNode *currentSPPFNode, GSSNode *stackTop) {
  int bucket = hash(CK_GSSEdge, newGSSNode->label, currentSPPFNode->label, stackTop->label, newGSSNode->level, currentSPPFNode->leftExtent, currentSPPFNode->rightExtent, stackTop->level);

  GSSEdge *tmp = new GSSEdge;

  tmp->kind = CK_GSSEdge;
  tmp->next = container::hashTable[bucket];
  container::hashTable[bucket] = (containerElement*) tmp;

  tmp->source = newGSSNode;
  tmp->edgeLabel = currentSPPFNode;
  tmp->destination = stackTop;

  listAnyElement *lTmp = new listAnyElement;
  lTmp->payload = (containerElement*) tmp;
  lTmp->next = list;
  list = lTmp;

  return tmp;
}

GSSEdge* container::iterGSSEdgeInit() {
  iterator = list;

  return iterator == NULL ? NULL : (GSSEdge*) iterator->payload;
}

GSSEdge* container::iterGSSEdgeNext() {
  if (iterator != NULL)
    iterator = iterator->next;

  return iterator == NULL ? NULL : (GSSEdge*) iterator->payload;
}

SPPFPackNode* container::iterSPPFPackNodeInit() {
  iterator = list;

  return iterator == NULL ? NULL : (SPPFPackNode*) iterator->payload;
}

SPPFPackNode* container::iterSPPFPackNodeNext() {
  if (iterator != NULL)
    iterator = iterator->next;

  return iterator == NULL ? NULL : (SPPFPackNode*) iterator->payload;
}

popElement* container::iterPopInit() {
  iterator = list;

#if 0
#if defined(TRACE)
  if (iterator == NULL)
    cout << "popElement iterator returns NULL\n";
  else {
    cout << "popElement iterator returns: (" <<
    ((popElement*) iterator->payload)->derivationNode->label << ", " << ((popElement*) iterator->payload)->derivationNode->leftExtent << ", "  << ((popElement*) iterator->payload)->derivationNode->rightExtent << ")\n";
  }
#endif
#endif

  return iterator == NULL ? NULL : (popElement*) iterator->payload;
}

popElement* container::iterPopNext() {
  if (iterator != NULL)
    iterator = iterator->next;

  return iterator == NULL ? NULL : (popElement*) iterator->payload;
}

ofstream RDTNode::F;
int RDTNode::nodeNumber;

void fprintStringSafe(ofstream *F, const char *str) {
  if (str == NULL)
    *F << "(null)";
  else
    while (*str != 0) {
      if (*str == '"')
        *F << "\\";
      *F << *str++;
    }
}

void RDTNode::VCGRec(int parentNodeNumber, const char **artLabelStrings) {
  int thisNodeNumber = nodeNumber;

  if (parentNodeNumber != 0)
    F <<
    "edge:{sourcename:\"" << parentNodeNumber << "\" targetname:\"" << nodeNumber << "\"}\n";

  F << "node:{title:\"" << nodeNumber << "\" label:\"";
  fprintStringSafe(&F, artLabelStrings[label]);
  F << (this->fold == ART_FOLD_UNDER ? "^" : this->fold == ART_FOLD_OVER ? "^^" : this->fold == ART_FOLD_TEAR ? "^^^" : "") <<
  "\"horizontal_order:" << nodeNumber << "}\n";

  if (sibling != NULL) {
    nodeNumber++;
    sibling->VCGRec(parentNodeNumber, artLabelStrings);
  }

  if (child != NULL) {
    nodeNumber++;
    child->VCGRec(thisNodeNumber, artLabelStrings);
  }
}

void container::clearReachable() {
  for (int temp = 0; temp < container::hashBucketCount; temp++) {

//    printf("ClearReachable() scanning hash table bucket %i\n", temp);
    for (containerElement *element = container::hashTable[temp]; element != NULL; element = element->next)
      switch (element->kind) {
        case CK_SPPFPackNode: ((SPPFPackNode*)element)->reachable = false; break;
        case CK_SPPFNode: ((SPPFNode*)element)->reachable = false; break;
        case CK_GSSEdge: break;
        case CK_GSSNode: break;
        case CK_popElement: break;
        case CK_descriptor: break;
        case CK_testRepeatElement: break;
        default: cout << "Internal error: unknown tag type " << element->kind << " found in hash table during clearReachable()\n";
      }
    }
}

void container::VCGSPPFunreachable() {
  for (int temp = 0; temp < container::hashBucketCount; temp++) {
    for (containerElement *element = container::hashTable[temp]; element != NULL; element = element->next)
      if (element->kind == CK_SPPFPackNode) {
        SPPFNode *node = (SPPFNode*) element;
        if (!node->reachable) {
        }
      }
    }
}

