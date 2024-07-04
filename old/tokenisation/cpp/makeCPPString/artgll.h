/*******************************************************************************
*
* ART V1.00 production GLL parsing support (c) Adrian Johnstone 2011
*
*******************************************************************************/
#if !defined(ARTGLL_H)
#define ARTGLL_H
// Hash based GLL parsing

// User definable compilation options - usually set on compiler command line so commented out here
// #define TRACE              // Enable voluminous trace information from running parse
#define STATISTICS          // Count calls to support routines
// #define SUPPRESS_RUNTIME   // Suppress CPU seconds display for regression testing
// Don't change anything below this line


//Basic datastructure elements
class RDTNode{
  public:
    static ofstream F;
    static int nodeNumber;

    artLabel label;
    enum artFold fold;
    RDTNode *sibling;
    RDTNode *child;
    RDTNode(artLabel label) {
      this->label = label; child = sibling = NULL;
    };

    RDTNode(artLabel label, RDTNode *parent) {
      this->label = label; child = NULL;
      RDTNode*rightmostChild = parent->child;
      if (rightmostChild == NULL)
        parent->child = this;
      else {
        while (rightmostChild->sibling != NULL)
          rightmostChild = rightmostChild->sibling;
        rightmostChild->sibling = this;
      }
      sibling = NULL;

    };

    void print() {
      cout << label << "(";
      if (child != NULL)
        child->print();
      cout << ")";
      if (sibling != NULL)
        sibling->print();
    };

    void VCGRec(int parentNodeNumber, const char **artLabelStrings);

    void VCG(const char *filename, const char** artLabelStrings) {
      F.open(filename);
      F <<
      "graph:{\n"
      "layoutalgorithm:tree\n"
      "orientation:top_to_bottom\n"
      "edge.arrowsize:7\n"
      "edge.thickness:1\n"
      "display_edge_labels:yes\n"
      "arrowmode:free\n"
      "node.borderwidth:1\n";
      nodeNumber = 1;
      this->VCGRec(0, artLabelStrings);
      F << "}";
      F.close();
    };

void writeRDTRec(RDTNode *node, const char** strings) {
  if (node == NULL)
    return;

  if (node->child == NULL)
    F << strings[node->label] << " ";
  else {
    F << "( ";
    writeRDTRec(node->child, strings);
    F << ") ";
  }

  if (node->sibling != NULL) // We're a leaf
    writeRDTRec(node->sibling, strings);
}

void writeRDT(const char *filename, RDTNode *node, const char** strings) {
  F.open(filename);
  writeRDTRec(node, strings);
  F.close();
}

};

class SPPFNode{public: artContainerKind kind; struct containerElement *next; artLabel label; artLabel *leftExtent; artLabel *rightExtent; int reachable:1; int ambiguous:1; container packNodes; SPPFNode() {packNodes.list = packNodes.iterator = NULL;};};
class SPPFPackNode{public: artContainerKind kind; struct containerElement *next; SPPFNode *parent; artLabel L; artLabel *pivot; artLabel leftChildLabel; artLabel rightChildLabel; int reachable:1;};
class GSSEdge{public: artContainerKind kind; struct containerElement *next; struct GSSNode *source; SPPFNode *edgeLabel; GSSNode *destination;};
class popElement{public: artContainerKind kind; struct containerElement *next; GSSNode *stackTop; SPPFNode *derivationNode;};
class GSSNode{public: artContainerKind kind; struct containerElement *next; artLabel label; artLabel *level; container poppedNodes; container outEdges; GSSNode() { poppedNodes.list = poppedNodes.iterator = outEdges.list = outEdges.iterator = NULL;};};
class descriptor{public: artContainerKind kind; struct containerElement *next; artLabel restartLabel; GSSNode *stackTop; artLabel *currentToken; SPPFNode *derivationNode;};
class testRepeatElement{public: artContainerKind kind; struct containerElement *next; artLabel regexpLabel; GSSNode *stackTop; artLabel *currentToken; SPPFNode *derivationNode;};

class GLLParser{
public:

  artLabel startLabel;                 // Start label
  bool inLanguage;                     // Recogniser flag
  clock_t startTime;
  clock_t stopTime;
#if defined(NANOSECOND)
  timespec startTime_CLOCK_MONOTONIC;
  timespec stopTime_CLOCK_MONOTONIC;
  timespec startTime_CLOCK_PROCESS_CPUTIME_ID;
  timespec stopTime_CLOCK_PROCESS_CPUTIME_ID;
#endif

  const char *grammarName;                   // Grammar name (name of source file, usually) - used for diagnostic reporting
  const char *buildOptions;                  // ART build options - used for diagnostic reporting
  const char* startSymbolName;               // Grammar start symbol as string - used for diagnostic reporting
  const char* tokenStringName;               // input buffer name  - used for diagnostic reporting
  artLabel* tokenString;               // input buffer
  int inputLength;
  const char **tokenStart;                   // array of pointers for each token back into its first character in the input buffer
  const char **tokenEnd;                     // array of pointers for each token back into its successor character in the input buffer
  artLabel* tokenLast;                 // pointer to last token in input buffer
  artLabel *currentToken;              // pointer into tokenString

#if defined(SAMPLE)
  artLabel *rightmostToken;            // the rightmost terminal matched so far
#endif
  artLabel currentRestartLabel;        // label from descriptor

  const char **artLabelStrings;              // lookup table of string for each label
  const char **artRDTStrings;                // lookup table of string for each RDT node
  bool *artTerminalRequiresWhiteSpace; // lookup table for longest match lexer
  bool *artTerminalCaseInsensitive;    // lookup table for longest match lexer
  int ambiguityCount;


  // Precomputed static finite functions - see cribsheet for details
  enum artFold *foldL;
  artLabel *lhsL;
  artLabel *pL;
  artLabel *aL;
  bool *fiRL;
  bool *eoOPL;
  bool *eoRL;
  bool *eoR_pL;

  SPPFNode *dummySPPFNode;             // dummy SPPF node for packing
  SPPFNode *currentSPPFNode;           // current SPPF node
  SPPFNode *temporarySPPFNode;         // temporary SPPF node used in template for optional
  SPPFNode *currentSPPFRightChildNode; // placeholder for use with terminals
  descriptor *currentDescriptor;       // current descriptor
  GSSNode *currentStackTop;            // current GSS node
  GSSNode *rootGSSNode;                // root of the GSS
  SPPFNode *rootSPPFNode;              // root of the SPF
  container *descriptorsToBeProcessed; // stack of descriptors awaiting processing
  container *descriptorsSeen;          // stack of descriptors awaiting processing
  container *GSS;
  container *SPPF;
  container *testRepeatElementsSeen;   // collection of testRepeatElements

#if defined(STATISTICS)
  int testRepeatCount;
  int findDescriptorCount;
  int popCount;
  int findGSSNodeCount;
  int findSPPFNodeECount;
  int findSPPFNodeTCount;
  int findSPPFNodeCount;
  int findSPPFNodeSkipLoopCount;
#endif

SPPFNode *findSPPFNode(artLabel *currentToken);                                                   // for epsilon
SPPFNode *findSPPFNode(artLabel terminalLabel, artLabel *currentToken);                           // for terminal
SPPFNode *findSPPFNode(artLabel justMatchedLabel, SPPFNode *leftChild, SPPFNode *rightChild);     // for nonterminal
SPPFNode *findSPPFNode(artLabel parentLabel, artLabel childLabel, artLabel *currentToken);        // for closures with non-null body

GSSNode *findGSSNode(artLabel stackTopLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *currentSPPFNode);
void pop(GSSNode *stackTop, artLabel *currentToken, SPPFNode *currentSPPFNode);
void findDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);
bool testRepeat(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);

RDTNode *rdt(SPPFNode *SPPFNode, RDTNode *parentRDTNode, bool suppressIntermediates);
void VCGSPPFRec(SPPFNode *node, int level);
void VCGSPPF(SPPFNode *SPPFNode, container *SPPF);

void disambiguate();
void disambiguateRec(SPPFNode *SPPFNode);

void lexBuiltInInstances(const char *start, artLabel* longestToken, int *longestLength);
const char *lexBuiltinWhitespace(const char *start);

  const char *matchSensitive(const char *lexeme, const char *cc) {
    const char *end = cc;

    while (*lexeme != 0 && *lexeme == *end) {
      lexeme++;
      end++;
    }

    if (*lexeme == 0)
      return end;
    else
      return cc;
  }

  // Watch out: this ony works for ASCII!
  const char lowerCase(const char c) { return (c >= (const char) 'A' && c <= (const char) 'Z') ? (const char) (c + 32) : c; }

  const char *matchInsensitive(const char *lexeme, const char *cc) {
    const char *end = cc;

    while (*lexeme != 0 && lowerCase(*lexeme) == lowerCase(*end)) {
      lexeme++;
      end++;
    }

    if (*lexeme == 0)
      return end;
    else
      return cc;
  }

  // initialise tokenString and tokenStart
  bool lexLongestMatch(const char *parseString) {
    tokenString = new artLabel[strlen(parseString) + 1];  // Can't be more than one token per character, though this is overkill, of course
    tokenStart = new const char*[strlen(parseString) + 1];
    tokenEnd = new const char*[strlen(parseString) + 1];

    const char *pp = parseString;
    currentToken = tokenString;
  #if defined(SAMPLE)
    rightmostToken = tokenString;
  #endif
    artLabel longestToken;

    // to do: Kuldge-y handling of initial whitespace: (i) see if the start of string matches anything; if not then call whitespace
    pp = lexBuiltinWhitespace(pp); // Just do it for everything at this stage

    while (1) {
      // Are we at the end of the string?
      if (*pp == 0) {
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        inputLength = currentToken - tokenString;
        return true;
      }


      // Now scan all token patterns for the longest match
      int longestLength = 0;
      const char *end;

      for (artLabel temp = (artLabel) 1; temp != ART__EPSILON; temp = (artLabel) ((int) temp + 1)) {
        if (artTerminalCaseInsensitive[temp])
          end = matchInsensitive(artLabelStrings[temp], pp);
        else
          end = matchSensitive(artLabelStrings[temp], pp);

        int tokenLength = end - pp;

        if (tokenLength > longestLength) {
          longestToken = temp;
          longestLength = tokenLength;
        }
      }

      // Do builtins last so that 'a' a wins over ID = a;but watch out for `a. More analysis required
      lexBuiltInInstances(pp, &longestToken, &longestLength);

      if(longestLength == 0) {
        cout << "Error: at input position " << (pp - parseString) << " lexer found unexpected input character " << (int) *pp << " (" << *pp << ")\n";
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        return false;
      }

//      printf("Lexer debug: matched %i %s length %i\n", longestToken, artLabelStrings[longestToken], longestLength);
      tokenStart[currentToken-tokenString] = pp;
      pp += longestLength;
      tokenEnd[currentToken-tokenString] = pp;
      *currentToken++ = longestToken;

      if (artTerminalRequiresWhiteSpace[longestToken]) {
        pp = lexBuiltinWhitespace(pp);
//      printf("Lexer debug: whitespace skipped to offset [%i]\n", pp - parseString);
      }
    }
#if 0
#if defined(TRACE)
  cout << "Token string:\n";
  for (artLabel *tmp = tokenString; *tmp != ART__EOS; tmp++) {
    cout << "[" << (tmp-tokenString) << "] " << *tmp << ": " << artLabelStrings[*tmp] << "  '";
    for (const char *tc = tokenStart[tmp-tokenString]; tc < tokenEnd[tmp-tokenString]; tc++)
      cout << *tc;
    cout << "'\n";
  }
  cout << "End of token string\n";
#endif
#endif

  }

  const char *artBuiltIn_OCAML_LABEL_NAME(const char *cc) {    // This is like ID, but allows ` and ' characters internally
    if (isalpha(*cc) || *cc == '_')
      while (isalnum(*cc) || *cc == '_' || *cc == '`' || *cc == '\'')
        cc++;

    return cc;
  }

  // These functions should be replaced by boolean lookup tables for efficiency
  bool artIsOCAML_OPERATOR_CHAR(const char *cc) {
    return
        *cc == '!' || *cc == '$' || *cc == '%' || *cc == '&' || *cc == '*' ||
        *cc == '+' || *cc == '-' || *cc == '.' || *cc == '/' || *cc == ':' ||
        *cc == '<' || *cc == '=' || *cc == '>' || *cc == '?' || *cc == '@' ||
        *cc == '^' || *cc == '|' || *cc == '~' ;
  }

  bool artIsOCAML_PREFIX_SYMBOL_INITIAL_CHAR(const char *cc) {
    return
        *cc == '!' || *cc == '?' || *cc == '~' ;
  }

  bool artIsOCAML_INFIX_SYMBOL_INITIAL_CHAR(const char *cc) {
    return
        *cc == '=' || *cc == '<' || *cc == '>' || *cc == '@' || *cc == '^' ||
        *cc == '|' || *cc == '&' || *cc == '+' || *cc == '-' || *cc == '*' ||
        *cc == '/' || *cc == '$' || *cc == '%' ;
  }

  const char *artBuiltIn_OCAML_INFIX_SYMBOL(const char *cc) {
    if (!artIsOCAML_INFIX_SYMBOL_INITIAL_CHAR(cc))
      return cc;

    cc++;

    while (artIsOCAML_OPERATOR_CHAR(cc))
      cc++;

    return cc;
  }

  const char *artBuiltIn_OCAML_PREFIX_SYMBOL(const char *cc) {
    if (!artIsOCAML_PREFIX_SYMBOL_INITIAL_CHAR(cc))
      return cc;

    cc++;

    while (artIsOCAML_OPERATOR_CHAR(cc))
      cc++;

    return cc;
  }

  const char *artBuiltIn_ID(const char *cc) {
    if (isalpha(*cc) || *cc == '_')
      while (isalnum(*cc) || *cc == '_')
        cc++;

    return cc;
  }

    const char *artBuiltIn_INTEGER(const char *cc) {
    /* Check for hexadecimal introducer */
    bool hex = (*cc == '0' && (*(cc + 1) == 'x' || *(cc + 1) == 'X'));

    if (hex) cc += 2;  // Skip over hex introducer

    /* Now collect decimal or hex digits */
    while ((hex ? isxdigit(*cc) : isdigit(*cc)))
      cc++;

    return cc;
  }

  const char *artBuiltIn_REAL(const char *cc) {
    if (!isdigit(*cc))  // Reals must contain at least one leading digit
      return cc;

    while (isdigit(*cc))
      cc++;

    if (*cc != '.')
      return cc;

    cc++; // skip .

    while (isdigit(*cc))
      cc++;

    if (*cc == 'e' || *cc == 'E') {
      cc++;

      while (isdigit(*cc))
       cc++;
    }

    return cc;
  }

  const char *artBuiltIn_CHAR_SQ(const char *cc) {
    const char* start = cc;

    if (*cc != '\'')
      return start;

    cc++;

    if (*cc == '\\')
      cc++;

    cc++;

   if (*cc != '\'')
      return start;

    cc++;  // skip past final delimiter

    return cc;
  }

  const char *artBuiltIn_STRING_SQ(const char *cc) {
    if (*cc != '\'')
      return cc;

    do {
      if (*cc == '\\')
        cc++;

      cc++;
    }
    while (*cc != '\'');

    cc++;  // skip past final delimiter

    return cc;
  }

const char *artBuiltIn_STRING_DQ(const char *cc) {
   if (*cc != '"')
     return cc;
   do {
     if (*cc == '\\')
       cc++;
     cc++;
   }
   while (*cc != '"');
   cc++;  // skip past final delimiter
   return cc;
 }

  const char *artBuiltIn_STRING_BQ(const char *cc) {
    if (*cc != '`')
      return cc;

    do {
      if (*cc == '\\')
        cc++;

      cc++;
    }
    while (*cc != '`');

    cc++;  // skip past final delimiter

    return cc;
  }

  const char *artBuiltIn_STRING_BB(const char *cc) {
    if (!( (*cc == '[') && (*(cc+1) == '[') ) )
      return cc;

    do {
      if (*cc == '\\')
        cc++;
      cc++;
    }
    while (!( (*cc == ']') && (*(cc+1) == ']') ) );

    cc++;
    cc++;  // skip past final delimiter

    return cc;
  }

  const char *artBuiltIn_CHAR_BQ(const char *cc) {
    if (*cc != '`')
      return cc;

    if (*cc != '\\') {
      if (*(cc + 1) == '0' && (*(cc + 2) == 'x' || *(cc + 2) == 'X'))
        cc += 5;
    }
    else
      cc +=2;

    return cc;
  }

  const char *artBuiltIn_WHITESPACE(const char *cc) {
    while(*cc != 0 && (isspace(*cc)))
      cc++;

    return cc;
  }

  const char *artBuiltIn_COMMENT_NEST_ART(const char *cc) {
    int nestingLevel = 1;

    if (*cc == '(' && *(cc+1) == '*') {
      cc += 2;

      while (nestingLevel > 0) {

        if (*cc == 0)
          break;

        if (*cc == '(' && *(cc+1) == '*') {
          cc += 2;
          nestingLevel++;
        }
        else if (*cc == '*' && *(cc+1) == ')') {
          cc += 2;
          nestingLevel--;
        }
        else
          cc++;
      }
    }

    return cc;
  }

  const char *artBuiltIn_COMMENT_BLOCK_C(const char *cc) {
    if (*cc == '/' && *(cc+1) == '*') {
      cc += 2;

      while (1) {
        if (*cc == 0)
          break;

        if (*cc == '*' && *(cc+1) == '/') {
          cc += 2;
          break;
        }

        cc++;
      }
    }

    return cc;
  }

  const char *artBuiltIn_COMMENT_LINE_C(const char *cc) {
    if (*cc == '/' && *(cc+1) == '/')
      while (*cc != 0 && *cc != '\n')
        cc++;

      if (*cc == '\n')
        cc++;

    return cc;
  }

  void parse(const char *const characterString);

  void checkAcceptance(artLabel startLabel);
  void createInitialStructures() {
    descriptorsToBeProcessed = new container(23, tokenString, tokenLast - tokenString); // stack of descriptors awaiting processing
    descriptorsSeen = new container; // stack of descriptors awaiting processing
    GSS = new container;
    SPPF = new container;

    dummySPPFNode = new SPPFNode;
    dummySPPFNode->kind = CK_SPPFNode;
    dummySPPFNode->label = ART_L__DUMMY;
    dummySPPFNode->leftExtent = dummySPPFNode->rightExtent = tokenString;
  }

  void unloadDescriptor() {
    currentDescriptor = descriptorsToBeProcessed->popDescriptor();
    currentRestartLabel = currentDescriptor->restartLabel;
    currentToken = currentDescriptor->currentToken;
    currentStackTop = currentDescriptor->stackTop;
    currentSPPFNode = currentDescriptor->derivationNode;

#if defined(TRACE)
     cout << "\n******\n\nProcessing descriptor (" << artLabelStrings[currentDescriptor->restartLabel] << ", (" <<
     artLabelStrings[currentDescriptor->stackTop->label] << ", " << (currentDescriptor->stackTop->level - tokenString) << "), " <<
     (currentDescriptor->currentToken - tokenString) << ", (" <<  artLabelStrings[currentSPPFNode->label] << ", " <<
     (currentSPPFNode->leftExtent - tokenString) << ", " <<  (currentSPPFNode->rightExtent - tokenString) <<

     "))\n";
#endif
  }

	void startClock(){
#if defined(NANOSECOND)
    if (clock_gettime(CLOCK_MONOTONIC, &startTime_CLOCK_MONOTONIC) != 0)
      printf("!!! Error in timer function\n");
    if (clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &startTime_CLOCK_PROCESS_CPUTIME_ID) != 0)
      printf("!!! Error in timer function\n");
#endif
    startTime = clock();
  }

	void stopClock() {
#if defined(NANOSECOND)
    if (clock_gettime(CLOCK_MONOTONIC, &stopTime_CLOCK_MONOTONIC) != 0)
      printf("!!! Error in timer function\n");
    if (clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &stopTime_CLOCK_PROCESS_CPUTIME_ID) != 0)
      printf("!!! Error in timer function\n");
#endif
    stopTime = clock();
  }

  void printStatistics() {
    if (SPPF->printStatistics(grammarName, buildOptions, startSymbolName, tokenStringName, tokenString, ambiguityCount, tokenLast, inLanguage, 0, artLabelStrings, foldL) < 1000)
     VCGSPPF(rootSPPFNode, SPPF);

     #if defined(STATISTICS)
    cout << ", testRepeatCount, " << testRepeatCount
         << ", findDescriptorCount, " << findDescriptorCount
         << ", popCount, " << popCount
         << ", findGSSNodeCount, " << findGSSNodeCount
         << ", findSPPFNodeECount, " << findSPPFNodeECount
         << ", findSPPFNodeTCount, " << findSPPFNodeTCount
         << ", findSPPFNodeCount, " << findSPPFNodeCount
         << ", findSPPFNodeSkipLoopCount, " << findSPPFNodeSkipLoopCount;
#endif
    cout << "\n";

  }

  void setTokenStringName(const char *name) { tokenStringName = strdup(name); }

  GLLParser();
};

class artHistogram {
class histogramNode {
public:
  unsigned long bucket;
  unsigned long value;
  histogramNode *next;
};

histogramNode *base;

public:
artHistogram()
{
  base = new histogramNode;
  base->bucket = base->value = 0;

  base->next = new histogramNode;
  base->next->bucket = ULONG_MAX;
  base->next->value = 0;
  base->next->next = NULL;
}

void update(unsigned long value)
{
  histogramNode *currentHistogramNode = base;
  histogramNode *previousHistogramNode;
  histogramNode *newHistogramNode;

  do
  {
    if (currentHistogramNode->bucket == value)
    {
      currentHistogramNode->value++;
      return;
    }

    previousHistogramNode = currentHistogramNode;
    currentHistogramNode = currentHistogramNode->next;
  }
  while (currentHistogramNode->bucket <= value);

  newHistogramNode = new histogramNode;
  newHistogramNode->bucket = value;
  newHistogramNode->value = 1;
  newHistogramNode->next = currentHistogramNode;
  previousHistogramNode->next = newHistogramNode;
}

void print()
{
  unsigned long sum = sumBuckets();

  histogramNode *currentHistogramNode = base;

  while (currentHistogramNode->next != NULL)
  {
    if (currentHistogramNode->value != 0)
      cout << currentHistogramNode->bucket << ": " << currentHistogramNode->value << "(" << (100 * currentHistogramNode->value) / sum << "%)\n";

    currentHistogramNode = currentHistogramNode->next;
  }
}

unsigned long bucketValue(unsigned long bucket)
{
  histogramNode *currentHistogramNode = base;

  while (currentHistogramNode->next != NULL && currentHistogramNode->bucket != bucket)
    currentHistogramNode = currentHistogramNode->next;

  return currentHistogramNode->bucket == bucket ? currentHistogramNode->value : 0;
}

unsigned long countNonemptyBuckets()
{
  histogramNode *currentHistogramNode = base;
  unsigned long buckets = 0;

  while (currentHistogramNode->next != NULL)
  {
    if (currentHistogramNode->value != 0)
      buckets++;

    currentHistogramNode = currentHistogramNode->next;
  }

  return buckets;
}

unsigned long countAllBuckets()
{
  histogramNode *currentHistogramNode = base;
  unsigned long buckets = 0;

  while (currentHistogramNode->next != NULL)
  {
    buckets++;

    currentHistogramNode = currentHistogramNode->next;
  }

  return buckets;
}

unsigned long sumBuckets()
{
  histogramNode *currentHistogramNode = base;
  unsigned long sum = 0;

  while (currentHistogramNode->next != NULL)
  {
    sum += currentHistogramNode->value;

    currentHistogramNode = currentHistogramNode->next;
  }

  return sum;
}

unsigned long weightedSumBuckets()
{
  histogramNode *currentHistogramNode = base;
  unsigned long sum = 0;

  while (currentHistogramNode->next != NULL)
  {
    sum += currentHistogramNode->bucket * currentHistogramNode->value;

    currentHistogramNode = currentHistogramNode->next;
  }

  return sum;
}

#if 0
~histogram()
{
  histogramNode *nodeToDelete = *base;

  while (nodeToDelete != NULL)
  {
    histogramNode *successorNode = nodeToDelete->next;

    delete nodeToDelete;

    nodeToDelete = successorNode;
  }
}
#endif
};

#endif


