/******************************************************************************
*
* ART V1.00 production GLL parsing support - simple hashing (c) Adrian Johnstone 2011
*
*******************************************************************************/
#include "bccgcc.h"
#include "artgllgenerated.h"
#include "artgllstructures.h"
#include "artgll.h"

void GLLParser::checkAcceptance(artLabel startLabel) {
  this->startLabel = startLabel;
  rootSPPFNode = SPPF->notinSPPF(startLabel, tokenString, tokenLast);
  inLanguage = rootSPPFNode != NULL;
}

bool GLLParser::testRepeat(artLabel regexpLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
#if defined(STATISTICS)
  testRepeatCount++;
#endif

#if defined(TRACE)
  cout << "testRepeat(" << artLabelStrings[regexpLabel] << ", " << artLabelStrings[stackTop->label] << ", " << (stackTop->level - tokenString) << "), " << (currentToken - tokenString) <<
          ", (" << artLabelStrings[derivationNode->label] << ", " << (derivationNode->leftExtent - tokenString) << ", " << (derivationNode->rightExtent - tokenString) << ")) - ";
#endif

    if (testRepeatElementsSeen->notinTestRepeatElement(regexpLabel, stackTop, currentToken, derivationNode) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
   testRepeatElementsSeen->addtoTestRepeatElement(regexpLabel, stackTop, currentToken, derivationNode);

    return false;
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif
  return true;
}

void GLLParser::findDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
#if defined(STATISTICS)
  findDescriptorCount++;
#endif
#if defined(TRACE)
  cout << "findDescriptor (" << artLabelStrings[restartLabel] << ", (" << artLabelStrings[stackTop->label] << ", " << (stackTop->level - tokenString) << "), " << (currentToken - tokenString) <<
          ", (" << artLabelStrings[derivationNode->label] << ", " << (derivationNode->leftExtent - tokenString) << ", " << (derivationNode->rightExtent - tokenString) << ")) - ";
#endif

    if (descriptorsSeen->notinDescriptor(restartLabel, stackTop, currentToken, derivationNode) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
    descriptor *newDescriptor = descriptorsSeen->addtoDescriptor(restartLabel, stackTop, currentToken, derivationNode);
    descriptorsToBeProcessed->pushDescriptor(newDescriptor);
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif
}

void GLLParser::pop(GSSNode *stackTop, artLabel *currentToken, SPPFNode *currentDerivationNode) {
#if defined(STATISTICS)
  popCount++;
#endif
#if defined(TRACE)
    cout << "pop ((" << artLabelStrings[stackTop->label] << ", " << (stackTop->level - tokenString) << "), " << (currentToken - tokenString) <<
            ", (" << artLabelStrings[currentDerivationNode->label] << ", " << (currentDerivationNode->leftExtent - tokenString) << ", " << (currentDerivationNode->rightExtent - tokenString) << ")) - ";

#endif

  if (stackTop == rootGSSNode) {
#if defined(TRACE)
    cout << "pop of root node - returning\n";
#endif
    return;
  }

  if (stackTop->poppedNodes.notinPopElement(stackTop, currentDerivationNode) == NULL) {
    stackTop->poppedNodes.addtoAndPushPopElement(stackTop, currentDerivationNode);
#if defined(TRACE)
    cout << "not found\n";
#endif
}
#if defined(TRACE)
  else
    cout << "found\n";
#endif

  for (GSSEdge *outEdge = stackTop->outEdges.iterGSSEdgeInit(); outEdge != NULL; outEdge = stackTop->outEdges.iterGSSEdgeNext()) {
#if defined(TRACE)
    cout << "Popping GSSEdge ((" << artLabelStrings[outEdge->source->label] << ", " << (outEdge->source->level - tokenString) << "), (" << artLabelStrings[outEdge->destination->label] << ", " << (outEdge->destination->level - tokenString) << "), (" <<
         artLabelStrings[outEdge->edgeLabel->label] << ", " << (outEdge->edgeLabel->leftExtent - tokenString) << ", " << (outEdge->edgeLabel->rightExtent - tokenString) << "))\n";
#endif
    SPPFNode *derivationNode = GLLParser::findSPPFNode(stackTop->label, outEdge->edgeLabel, currentDerivationNode);
    GLLParser::findDescriptor(stackTop->label, outEdge->destination, currentToken, derivationNode);
  }
#if defined(TRACE)
    cout << "Exiting pop\n";
#endif
}

GSSNode *GLLParser::findGSSNode(artLabel stackTopLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *currentDerivationNode) {
#if defined(STATISTICS)
  findGSSNodeCount++;
#endif
GSSNode *newGSSNode;

#if defined(TRACE)
  if (stackTop == NULL && currentDerivationNode == NULL)
    cout << "findGSS (" << artLabelStrings[stackTopLabel] << ", (null), " << (currentToken - tokenString) <<
    ", (null)) - ";
  else
    cout << "findGSS (" << artLabelStrings[stackTopLabel] << ", (" << artLabelStrings[stackTop->label] << ", " << (stackTop->level - tokenString) << "), " << (currentToken - tokenString) <<
    ", (" << artLabelStrings[currentDerivationNode->label] << ", " << (currentDerivationNode->leftExtent - tokenString) << ", " << (currentDerivationNode->rightExtent - tokenString) << ")) - ";
#endif

  if ((newGSSNode = GSS->notinGSS(stackTopLabel, currentToken)) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
    newGSSNode = GSS->addtoGSS(stackTopLabel, currentToken);
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif

#if defined(TRACE)
    if (stackTop != NULL && newGSSNode->outEdges.notinGSSEdge(newGSSNode, currentDerivationNode, stackTop) == NULL)
      cout << "findGSSEdge ((" << artLabelStrings[newGSSNode->label] << ", " << (newGSSNode->level - tokenString) << "), (" << artLabelStrings[stackTop->label] << ", " << (stackTop->level - tokenString) << "), (" <<
      artLabelStrings[currentDerivationNode->label] << ", " << (currentDerivationNode->leftExtent - tokenString) << ", " << (currentDerivationNode->rightExtent - tokenString) << ")) - ";
#endif
  if (stackTop != NULL && newGSSNode->outEdges.notinGSSEdge(newGSSNode, currentDerivationNode, stackTop) == NULL) {
    newGSSNode->outEdges.addtoAndPushGSSEdge(newGSSNode, currentDerivationNode, stackTop);
#if defined(TRACE)
    cout << "not found\n";
#endif

/* Now: if newGSSNode was new, then the pop list must be empty so we could drop this off by setting a boolean above, and testing that first */

#if defined (TRACE)
    cout << "Start of contingent pop processing for GSS node (" << artLabelStrings[newGSSNode->label] << ", "<< (newGSSNode->level - tokenString) << ")\n";
#endif

    for (popElement *poppedElement = newGSSNode->poppedNodes.iterPopInit(); poppedElement != NULL; poppedElement = newGSSNode->poppedNodes.iterPopNext()) {
#if 0
#if defined (TRACE)
      cout << "PoppedElement iteration on (" << artLabelStrings[poppedElement->derivationNode->label] << ", " << (poppedElement->derivationNode->leftExtent - tokenString) << ", " << (poppedElement->derivationNode->rightExtent - tokenString) <<")\n";
#endif
#endif
      SPPFNode *derivationNode = GLLParser::findSPPFNode(stackTopLabel, currentDerivationNode, poppedElement->derivationNode);
      GLLParser::findDescriptor(stackTopLabel, stackTop, poppedElement->derivationNode->rightExtent, derivationNode);
    }
#if defined (TRACE)
    cout << "End of contingent pop processing for GSS node (" << artLabelStrings[newGSSNode->label] << ", "<< (newGSSNode->level - tokenString) << ")\n";
#endif
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif
  return newGSSNode;
}

/* getNodeE() */
SPPFNode *GLLParser::findSPPFNode(artLabel *currentToken) {
#if defined(STATISTICS)
  findSPPFNodeECount++;
#endif
  SPPFNode *newSPPFNode;

#if defined(TRACE)
  cout << "findSPPFEpsilon (#, " << (currentToken - tokenString) << ", " << (currentToken - tokenString) << ") - ";
#endif
  if ((newSPPFNode = SPPF->notinSPPF(ART_EPSILON, currentToken, currentToken) ) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
    newSPPFNode = SPPF->addtoSPPF(ART_EPSILON, currentToken, currentToken);
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif

  return newSPPFNode;
}

/* getNodeT() */
SPPFNode *GLLParser::findSPPFNode(artLabel terminalLabel, artLabel *currentToken) {
#if defined(STATISTICS)
  findSPPFNodeTCount++;
#endif

#if defined(SAMPLE)
  if (currentToken > rightmostToken)
    rightmostToken = currentToken;

  if (findSPPFNodeTCount % 1000 == 0)
    cout << " ** findSPPDNodeTCount: " << findSPPFNodeTCount << "  rightmostToken: " << (rightmostToken - tokenString) << "\n";
#endif

  SPPFNode *newSPPFNode;

#if defined(TRACE)
  cout << "findSPPFTerminal (" << artLabelStrings[terminalLabel] << ", " << (currentToken - tokenString) << ", " << (currentToken - tokenString + 1) << ") - ";
#endif
  if ((newSPPFNode = SPPF->notinSPPF(terminalLabel, currentToken, currentToken + 1) ) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
    newSPPFNode = SPPF->addtoSPPF(terminalLabel, currentToken, currentToken + 1);
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif

  return newSPPFNode;
}

/* getNode() */
SPPFNode *GLLParser::findSPPFNode(artLabel justMatchedLabel, SPPFNode *leftChild, SPPFNode *rightChild) {
#if defined(STATISTICS)
  findSPPFNodeCount++;
#endif
#if defined(TRACE)
  cout << "findSPPF (" << artLabelStrings[justMatchedLabel] <<
          ", (" <<  artLabelStrings[leftChild->label] << ", " <<  (leftChild->leftExtent - tokenString) << ", " <<  (leftChild->rightExtent - tokenString) <<
          "), (" <<  artLabelStrings[rightChild->label] << ", " <<  (rightChild->leftExtent - tokenString) << ", " <<  (rightChild->rightExtent - tokenString) << "))\n";
#endif

  if (fiRL[justMatchedLabel]) {
#if defined(TRACE)
    cout << "Returning rightChild\n";
#endif
    return rightChild;
  }

  SPPFNode *newSPPFNode;

// i - setup parameters

  artLabel* leftExtent = (leftChild == dummySPPFNode ? rightChild->leftExtent : leftChild->leftExtent);
  artLabel newSPPFNodeLabel;

  if (eoOPL[justMatchedLabel])
    newSPPFNodeLabel = eoR_pL[justMatchedLabel] ? lhsL[justMatchedLabel] : pL[justMatchedLabel];
  else {
    newSPPFNodeLabel = eoRL[justMatchedLabel] ? lhsL[justMatchedLabel] : aL[justMatchedLabel];
  }

// ii - find the SPPF node
#if defined(TRACE)
			cout << "findSPPF (" << artLabelStrings[newSPPFNodeLabel] << ", " << leftExtent-tokenString << ", " << rightChild->rightExtent - tokenString << ") - ";
#endif

  if ((newSPPFNode = SPPF->notinSPPF(newSPPFNodeLabel, leftExtent, rightChild->rightExtent) ) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
    newSPPFNode = SPPF->addtoSPPF(newSPPFNodeLabel, leftExtent, rightChild->rightExtent);
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif

// iii - add the pack node
#if defined(TRACE)
  cout << "packNode (" << artLabelStrings[justMatchedLabel] << ", " << rightChild->leftExtent-tokenString << ") - ";
#endif

  if (newSPPFNode->packNodes.notinPack(newSPPFNode, justMatchedLabel, rightChild->leftExtent) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
    SPPFPackNode *x = newSPPFNode->packNodes.addtoPack(newSPPFNode, justMatchedLabel, rightChild->leftExtent);
    x->leftChildLabel = leftChild->label;
    x->rightChildLabel = rightChild->label;
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif

  return newSPPFNode;
}

/* getSkipLoopNode() and getLoopBaseNode() */
SPPFNode *GLLParser::findSPPFNode(artLabel parentLabel, artLabel childLabel, artLabel *currentToken) {
#if defined(STATISTICS)
  findSPPFNodeSkipLoopCount++;
#endif
  SPPFNode *newSPPFNode;

#if defined(TRACE)
  cout << "findSPPFNodeSkipLoop (" << artLabelStrings[parentLabel] <<
          ", " << artLabelStrings[childLabel] <<
          ", " << (currentToken - tokenString) << ") - ";
#endif
  if ((newSPPFNode = SPPF->notinSPPF(parentLabel, currentToken, currentToken) ) == NULL) {
#if defined(TRACE)
    cout << "not found\n";
#endif
    newSPPFNode = SPPF->addtoSPPF(parentLabel, currentToken, currentToken);
  }
#if defined(TRACE)
  else
    cout << "found\n";
#endif

  //if there is no packnode labelled (L, i), the make one and add an epsilon node as a child
  if (newSPPFNode->packNodes.notinPack(newSPPFNode, childLabel, currentToken) == NULL) {
#if defined(TRACE)
    cout << "Adding pack node\n";
#endif
    SPPFPackNode *x = newSPPFNode->packNodes.addtoPack(newSPPFNode, childLabel, currentToken);

    x->leftChildLabel = ART_L_DUMMY;
    x->rightChildLabel = ART_EPSILON;

    findSPPFNode(currentToken);  // Add epsilon node
  }

  return newSPPFNode;
}

void GLLParser::disambiguateRec(SPPFNode *SPPFNode) {
//  cout << "disambiguate() visiting node " << artLabelStrings[SPPFNode->label] << ", " << SPPFNode->leftExtent-tokenString << ", " << SPPFNode->rightExtent-tokenString << "\n";
  int outDegree = 0;

  if (SPPFNode == NULL)
    return;

  if (SPPFNode->reachable)
    return;  // Found a loop

  SPPFNode->reachable = true;

  for (SPPFPackNode *tmp = SPPFNode->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = SPPFNode->packNodes.iterSPPFPackNodeNext()) {
//    cout << "disambiguate() visiting pack node " << artLabelStrings[tmp->L] << ", " << tmp->pivot-tokenString << "\n";

    tmp->reachable = true;

    outDegree++;

    if (tmp->leftChildLabel != ART_L_DUMMY)
      disambiguateRec(SPPF->notinSPPF(tmp->leftChildLabel, SPPFNode->leftExtent, tmp->pivot));

    if (tmp->rightChildLabel != ART_L_DUMMY)
      disambiguateRec(SPPF->notinSPPF(tmp->rightChildLabel, tmp->pivot, SPPFNode->rightExtent));
  }

  if (outDegree > 1) {
    SPPFNode->ambiguous = true;
    ambiguityCount++;
  }

}

void GLLParser::disambiguate() {
  ambiguityCount = 0;
  disambiguateRec(rootSPPFNode);
}

ofstream sppfFile;
int sppfRecNumber;

void GLLParser::VCGSPPFRec(SPPFNode *node, int level) {
//  cout << "VCGSPPFRec() visiting node " << artLabelStrings[node->label] << ", " << (node->leftExtent-tokenString) << ", " << (node->rightExtent-tokenString) << "\n";
  if (node == NULL)
    return;

  if (node->reachable)
    return;  // Found a loop

  node->reachable = true;

  // count pack nodes
  int packNodeCount = 0;
  for (SPPFPackNode *tmp = node->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = node->packNodes.iterSPPFPackNodeNext())
    packNodeCount++;

  sppfFile << "node:{title:\"" <<
              artRDTStrings[node->label] << " (" <<
              artLabelStrings[node->label] <<
              "," <<
              (node->leftExtent - tokenString) <<
              "," <<
              (node->rightExtent - tokenString) << ")\"shape:ellipse" <<
              (artRDTStrings[node->label] == NULL ? " bordercolor:magenta " : "" ) <<
              " level:"<< level << " horizontal_order: " << ++sppfRecNumber << (packNodeCount > 1 ? " color:red" : "") << "}\n";


  for (SPPFPackNode *tmp = node->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = node->packNodes.iterSPPFPackNodeNext()) {
//    cout << "VCGSPPFRec() visiting pack node " << (artLabelStrings[tmp->L]) << ", " << (tmp->pivot-tokenString) << "\n";

    sppfFile << "node:{title:\"" <<
    artLabelStrings[tmp->parent->label] <<
    "," <<
    (tmp->parent->leftExtent - tokenString) <<
    "," <<
    (tmp->parent->rightExtent - tokenString) <<
    " " <<
    artLabelStrings[tmp->L] <<
    "," <<
    (tmp->pivot - tokenString) <<
    "\" label:\"" <<
    artLabelStrings[tmp->L] <<
    (foldL[tmp->L] == ART_FOLD_UNDER ? "^" : foldL[tmp->L] == ART_FOLD_OVER ? "^^" : foldL[tmp->L] == ART_FOLD_TEAR ? "^^^" : "") <<
    "," <<
    (tmp->pivot - tokenString) <<
    "\" level: " << (level + 1) << " horizontal_order: " << ++sppfRecNumber << (packNodeCount > 1 ? " color:orange" : "")
    << (artRDTStrings[tmp->parent->label] == NULL ? " bordercolor:magenta" : "" )
    << "}\n" <<
    "edge:{sourcename:\"" <<
    artRDTStrings[node->label] << " (" <<
    artLabelStrings[tmp->parent->label] <<
    "," <<
    (tmp->parent->leftExtent - tokenString) <<
    "," <<
    (tmp->parent->rightExtent - tokenString) <<
    ")\" targetname:\"" <<
    artLabelStrings[tmp->parent->label] <<
    "," <<
    (tmp->parent->leftExtent - tokenString) <<
    "," <<
    (tmp->parent->rightExtent - tokenString) <<
    " " <<
    artLabelStrings[tmp->L] <<
    "," <<
    (tmp->pivot - tokenString) <<
    "\"}\n";

    tmp->reachable = true;

    if (tmp->leftChildLabel != ART_L_DUMMY) {
      sppfFile <<
      "edge:{sourcename:\"" <<
      artLabelStrings[tmp->parent->label] <<
      "," <<
      (tmp->parent->leftExtent - tokenString) <<
      "," <<
      (tmp->parent->rightExtent - tokenString) <<
      " " <<
      artLabelStrings[tmp->L] <<
      "," <<
      (tmp->pivot - tokenString) <<
      "\" targetname:\"" <<
      artRDTStrings[tmp->leftChildLabel] << " (" <<
      artLabelStrings[tmp->leftChildLabel] <<
      "," <<
      (tmp->parent->leftExtent - tokenString) <<
      "," <<
      (tmp->pivot - tokenString) <<
      ")\"}\n";
      VCGSPPFRec(SPPF->notinSPPF(tmp->leftChildLabel, node->leftExtent, tmp->pivot), level+ 2);
    }

    if (tmp->rightChildLabel != ART_L_DUMMY) {
      sppfFile <<
      "edge:{sourcename:\"" <<
      artLabelStrings[tmp->parent->label] <<
      "," <<
      (tmp->parent->leftExtent - tokenString) <<
      "," <<
      (tmp->parent->rightExtent - tokenString) <<
      " " <<
      artLabelStrings[tmp->L] <<
      "," <<
      (tmp->pivot - tokenString) <<
      "\" targetname:\"" <<
      artRDTStrings[tmp->rightChildLabel] << " (" <<
      artLabelStrings[tmp->rightChildLabel] <<
      "," <<
      (tmp->pivot - tokenString) <<
      "," <<
      (tmp->parent->rightExtent - tokenString) <<
      ")\"}\n";
      VCGSPPFRec(SPPF->notinSPPF(tmp->rightChildLabel, tmp->pivot, node->rightExtent), level+ 2);
    }
  }
}

void GLLParser::VCGSPPF(SPPFNode *node, container *SPPF) {

  SPPF->clearReachable();

  sppfFile.open ("gllsppf.vcg");
  sppfFile <<
  "graph:{\n"
  "layoutalgorithm:tree\n"
  "splines:yes\n"

  "edge.arrowsize:7\n"
  "edge.thickness:1\n"
  "display_edge_labels:yes\n"
  "arrowmode:free\n"
  "node.borderwidth:1\n";

  sppfRecNumber = 0;

  VCGSPPFRec(node, 1);

  SPPF->VCGSPPFunreachable();

  sppfFile << "}\n";
  sppfFile.close();

}

RDTNode *GLLParser::rdt(SPPFNode *SPPFNode, RDTNode *parentRDTNode, bool suppressIntermediates) {
  RDTNode *rdtNode;
  SPPFPackNode *candidate = NULL;
  artLabel* pivot = tokenString;

  if (SPPFNode == NULL)
    return NULL;

  if (SPPFNode->reachable)
    return NULL;  // Found a loop

  if (parentRDTNode == NULL) // Special case: root node of RDT
    rdtNode = new RDTNode(SPPFNode->label);
  else
    rdtNode = new RDTNode(SPPFNode->label, parentRDTNode);

  int outDegree = 0;

  SPPFNode->reachable = true;
  for (SPPFPackNode *tmp = SPPFNode->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = SPPFNode->packNodes.iterSPPFPackNodeNext()) {
    outDegree++;
    SPPFNode->ambiguous = outDegree > 1;

    // Do not select pack nodes with a back edge
    if ((tmp->leftChildLabel != ART_L_DUMMY && SPPF->notinSPPF(tmp->leftChildLabel, SPPFNode->leftExtent, tmp->pivot)->reachable) ||
        (tmp->rightChildLabel != ART_L_DUMMY && SPPF->notinSPPF(tmp->rightChildLabel, tmp->pivot, SPPFNode->rightExtent)->reachable))
      continue;

    if (tmp->pivot >= pivot) {
      candidate = tmp;
      pivot = tmp->pivot;
    }
  }

  if (candidate != NULL) {
    candidate->reachable = true;

    if (candidate->leftChildLabel != ART_L_DUMMY)
      rdt(SPPF->notinSPPF(candidate->leftChildLabel, SPPFNode->leftExtent, candidate->pivot), rdtNode, suppressIntermediates);

    if (candidate->rightChildLabel != ART_L_DUMMY)
      rdt(SPPF->notinSPPF(candidate->rightChildLabel, candidate->pivot, SPPFNode->rightExtent), rdtNode, suppressIntermediates);
  }

  return rdtNode;
}


