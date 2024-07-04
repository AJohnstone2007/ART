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
  if (node->sibling != NULL)
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
  artLabel startLabel;
  bool inLanguage;
  clock_t startTime;
  clock_t stopTime;
  const char *grammarName;
  const char *buildOptions;
  const char* startSymbolName;
  const char* tokenStringName;
  artLabel* tokenString;
  int inputLength;
  const char **tokenStart;
  const char **tokenEnd;
  artLabel* tokenLast;
  artLabel *currentToken;
  artLabel currentRestartLabel;
  const char **artLabelStrings;
  const char **artRDTStrings;
  bool *artTerminalRequiresWhiteSpace;
  bool *artTerminalCaseInsensitive;
  int ambiguityCount;
  enum artFold *foldL;
  artLabel *lhsL;
  artLabel *pL;
  artLabel *aL;
  bool *fiRL;
  bool *eoOPL;
  bool *eoRL;
  bool *eoR_pL;
  SPPFNode *dummySPPFNode;
  SPPFNode *currentSPPFNode;
  SPPFNode *temporarySPPFNode;
  SPPFNode *currentSPPFRightChildNode;
  descriptor *currentDescriptor;
  GSSNode *currentStackTop;
  GSSNode *rootGSSNode;
  SPPFNode *rootSPPFNode;
  container *descriptorsToBeProcessed;
  container *descriptorsSeen;
  container *GSS;
  container *SPPF;
  container *testRepeatElementsSeen;
  int testRepeatCount;
  int findDescriptorCount;
  int popCount;
  int findGSSNodeCount;
  int findSPPFNodeECount;
  int findSPPFNodeTCount;
  int findSPPFNodeCount;
  int findSPPFNodeSkipLoopCount;
SPPFNode *findSPPFNode(artLabel *currentToken);
SPPFNode *findSPPFNode(artLabel terminalLabel, artLabel *currentToken);
SPPFNode *findSPPFNode(artLabel justMatchedLabel, SPPFNode *leftChild, SPPFNode *rightChild);
SPPFNode *findSPPFNode(artLabel parentLabel, artLabel childLabel, artLabel *currentToken);
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
  bool lexLongestMatch(const char *parseString) {
    tokenString = new artLabel[strlen(parseString) + 1];
    tokenStart = new const char*[strlen(parseString) + 1];
    tokenEnd = new const char*[strlen(parseString) + 1];
    const char *pp = parseString;
    currentToken = tokenString;
    artLabel longestToken;
    pp = lexBuiltinWhitespace(pp);
    while (1) {
      if (*pp == 0) {
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        inputLength = currentToken - tokenString;
        return true;
      }
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
      lexBuiltInInstances(pp, &longestToken, &longestLength);
      if(longestLength == 0) {
        cout << "Error: at input position " << (pp - parseString) << " lexer found unexpected input character " << (int) *pp << " (" << *pp << ")\n";
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        return false;
      }
      tokenStart[currentToken-tokenString] = pp;
      pp += longestLength;
      tokenEnd[currentToken-tokenString] = pp;
      *currentToken++ = longestToken;
      if (artTerminalRequiresWhiteSpace[longestToken]) {
        pp = lexBuiltinWhitespace(pp);
      }
    }
  }
  const char *artBuiltIn_OCAML_LABEL_NAME(const char *cc) {
    if (isalpha(*cc) || *cc == '_')
      while (isalnum(*cc) || *cc == '_' || *cc == '`' || *cc == '\'')
        cc++;
    return cc;
  }
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
    bool hex = (*cc == '0' && (*(cc + 1) == 'x' || *(cc + 1) == 'X'));
    if (hex) cc += 2;
    while ((hex ? isxdigit(*cc) : isdigit(*cc)))
      cc++;
    return cc;
  }
  const char *artBuiltIn_REAL(const char *cc) {
    if (!isdigit(*cc))
      return cc;
    while (isdigit(*cc))
      cc++;
    if (*cc != '.')
      return cc;
    cc++;
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
    cc++;
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
    cc++;
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
   cc++;
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
    cc++;
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
    cc++;
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
    descriptorsToBeProcessed = new container(23, tokenString, tokenLast - tokenString);
    descriptorsSeen = new container;
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
  }
 void startClock(){
    startTime = clock();
  }
 void stopClock() {
    stopTime = clock();
  }
  void printStatistics() {
    if (SPPF->printStatistics(grammarName, buildOptions, startSymbolName, tokenStringName, tokenString, ambiguityCount, tokenLast, inLanguage, 0, artLabelStrings, foldL) < 1000)
     VCGSPPF(rootSPPFNode, SPPF);
    cout << ", testRepeatCount, " << testRepeatCount
         << ", findDescriptorCount, " << findDescriptorCount
         << ", popCount, " << popCount
         << ", findGSSNodeCount, " << findGSSNodeCount
         << ", findSPPFNodeECount, " << findSPPFNodeECount
         << ", findSPPFNodeTCount, " << findSPPFNodeTCount
         << ", findSPPFNodeCount, " << findSPPFNodeCount
         << ", findSPPFNodeSkipLoopCount, " << findSPPFNodeSkipLoopCount;
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
};
enum artFold {ART_FOLD_EMPTY, ART_FOLD_UNDER, ART_FOLD_OVER, ART_FOLD_TEAR, ART_FOLD_UNDER_RECURSION, ART_FOLD_OVER_RECURSION};
enum artFold {ART_FOLD_EMPTY, ART_FOLD_UNDER, ART_FOLD_OVER, ART_FOLD_TEAR, ART_FOLD_UNDER_RECURSION, ART_FOLD_OVER_RECURSION};
void dumpHashTable(struct containerElement **hashTable, int hashBucketCount, const char** artLabelStrings, enum artLabel* tokenString);
enum artContainerKind{CK_SPPFPackNode, CK_SPPFNode, CK_GSSEdge, CK_GSSNode, CK_popElement, CK_descriptor, CK_testRepeatElement};
struct containerElement{artContainerKind kind; struct containerElement *next;};
struct listAnyElement{containerElement *payload; listAnyElement *next;};
class container{
public:
container(){ list = iterator = NULL; };
static artLabel* tokenString;
static unsigned inputExtent;
static unsigned hashMask;
static unsigned hashBucketCount;
static struct containerElement **hashTable;
struct listAnyElement *list;
struct listAnyElement *iterator;
container(unsigned hashBucketCountVal, artLabel* tokenStringVal, int inputExtentVal) {
  tokenString = tokenStringVal;
  inputExtent = inputExtentVal;
  hashBucketCount = 1;
  for (int temp = 0; temp < hashBucketCountVal; temp++)
    hashBucketCount *=2;
  hashTable = new containerElement*[hashBucketCount];
  hashMask = hashBucketCount - 1;
  memset(hashTable, 0, hashBucketCount * sizeof(containerElement*));
  list = iterator = NULL;
}
unsigned hash(artContainerKind a, artLabel b, artLabel c, artLabel d, artLabel *e, artLabel *f, artLabel*g, artLabel *h) {
  return (a * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          b * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          c * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          d * inputExtent * inputExtent * inputExtent * inputExtent +
          (e-tokenString) * inputExtent * inputExtent * inputExtent +
          (f-tokenString) * inputExtent * inputExtent +
          (g-tokenString) * inputExtent +
          (h-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel c, artLabel *d, artLabel *e, artLabel *f) {
  return (a * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent +
          b * ART__EXTENT * inputExtent * inputExtent * inputExtent +
          c * inputExtent * inputExtent * inputExtent +
          (d-tokenString) * inputExtent * inputExtent +
          (e-tokenString) * inputExtent +
          (f-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel *c, artLabel *d) {
  return (a * ART__EXTENT * inputExtent * inputExtent +
          b * inputExtent * inputExtent +
          (c-tokenString) * inputExtent +
          (d-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel *c) {
  return (a * ART__EXTENT * inputExtent +
          b * inputExtent +
          (c-tokenString)) & hashMask;
}
bool listEmpty();
void pushDescriptor(struct descriptor* newDescriptor);
struct descriptor* popDescriptor();
struct descriptor* notinDescriptor(artLabel restartLabel, struct GSSNode *stackTop, artLabel *currentToken, struct SPPFNode *derivationNode);
struct descriptor* addtoDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);
struct testRepeatElement* notinTestRepeatElement(artLabel regexpLabel, struct GSSNode *stackTop, artLabel *currentToken, struct SPPFNode *derivationNode);
struct testRepeatElement* addtoTestRepeatElement(artLabel regexptLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);
struct popElement* notinPopElement(GSSNode* stackTop, SPPFNode *derivationNode);
void addtoAndPushPopElement(GSSNode *stackTop, SPPFNode *derivationNode);
struct GSSNode* notinGSS(artLabel stackTopLabel, artLabel* currentToken);
struct GSSNode* addtoGSS(artLabel stackTopLabel, artLabel* currentToken);
struct SPPFNode* notinSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent);
struct SPPFNode* addtoSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent);
struct SPPFPackNode* notinPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken);
struct SPPFPackNode* addtoPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken);
void pushSPPFPackNode(struct SPPFPackNode * newPackNode);
struct GSSEdge* notinGSSEdge(GSSNode *newGSSNode, SPPFNode *derivationNode, GSSNode *stackTop);
struct GSSEdge* addtoAndPushGSSEdge(GSSNode *newGSSNode, SPPFNode *derivationNode, GSSNode *stackTop);
struct GSSEdge* iterGSSEdgeInit();
struct GSSEdge* iterGSSEdgeNext();
struct popElement* iterPopInit();
struct popElement* iterPopNext();
struct SPPFPackNode* iterSPPFPackNodeInit();
struct SPPFPackNode* iterSPPFPackNodeNext();
void clearReachable();
void VCGSPPFunreachable();
int printStatistics(const char *grammarName, const char *buildOptions, const char *startSymbolName, const char *tokenStringName, artLabel* tokenString, int ambiguityCount, artLabel* tokenLast, bool inLanguage, double runTime, const char** artLabelStrings, enum artFold *foldL);
void sane();
};
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
  if (node->sibling != NULL)
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
  artLabel startLabel;
  bool inLanguage;
  clock_t startTime;
  clock_t stopTime;
  const char *grammarName;
  const char *buildOptions;
  const char* startSymbolName;
  const char* tokenStringName;
  artLabel* tokenString;
  int inputLength;
  const char **tokenStart;
  const char **tokenEnd;
  artLabel* tokenLast;
  artLabel *currentToken;
  artLabel currentRestartLabel;
  const char **artLabelStrings;
  const char **artRDTStrings;
  bool *artTerminalRequiresWhiteSpace;
  bool *artTerminalCaseInsensitive;
  int ambiguityCount;
  enum artFold *foldL;
  artLabel *lhsL;
  artLabel *pL;
  artLabel *aL;
  bool *fiRL;
  bool *eoOPL;
  bool *eoRL;
  bool *eoR_pL;
  SPPFNode *dummySPPFNode;
  SPPFNode *currentSPPFNode;
  SPPFNode *temporarySPPFNode;
  SPPFNode *currentSPPFRightChildNode;
  descriptor *currentDescriptor;
  GSSNode *currentStackTop;
  GSSNode *rootGSSNode;
  SPPFNode *rootSPPFNode;
  container *descriptorsToBeProcessed;
  container *descriptorsSeen;
  container *GSS;
  container *SPPF;
  container *testRepeatElementsSeen;
  int testRepeatCount;
  int findDescriptorCount;
  int popCount;
  int findGSSNodeCount;
  int findSPPFNodeECount;
  int findSPPFNodeTCount;
  int findSPPFNodeCount;
  int findSPPFNodeSkipLoopCount;
SPPFNode *findSPPFNode(artLabel *currentToken);
SPPFNode *findSPPFNode(artLabel terminalLabel, artLabel *currentToken);
SPPFNode *findSPPFNode(artLabel justMatchedLabel, SPPFNode *leftChild, SPPFNode *rightChild);
SPPFNode *findSPPFNode(artLabel parentLabel, artLabel childLabel, artLabel *currentToken);
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
  bool lexLongestMatch(const char *parseString) {
    tokenString = new artLabel[strlen(parseString) + 1];
    tokenStart = new const char*[strlen(parseString) + 1];
    tokenEnd = new const char*[strlen(parseString) + 1];
    const char *pp = parseString;
    currentToken = tokenString;
    artLabel longestToken;
    pp = lexBuiltinWhitespace(pp);
    while (1) {
      if (*pp == 0) {
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        inputLength = currentToken - tokenString;
        return true;
      }
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
      lexBuiltInInstances(pp, &longestToken, &longestLength);
      if(longestLength == 0) {
        cout << "Error: at input position " << (pp - parseString) << " lexer found unexpected input character " << (int) *pp << " (" << *pp << ")\n";
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        return false;
      }
      tokenStart[currentToken-tokenString] = pp;
      pp += longestLength;
      tokenEnd[currentToken-tokenString] = pp;
      *currentToken++ = longestToken;
      if (artTerminalRequiresWhiteSpace[longestToken]) {
        pp = lexBuiltinWhitespace(pp);
      }
    }
  }
  const char *artBuiltIn_OCAML_LABEL_NAME(const char *cc) {
    if (isalpha(*cc) || *cc == '_')
      while (isalnum(*cc) || *cc == '_' || *cc == '`' || *cc == '\'')
        cc++;
    return cc;
  }
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
    bool hex = (*cc == '0' && (*(cc + 1) == 'x' || *(cc + 1) == 'X'));
    if (hex) cc += 2;
    while ((hex ? isxdigit(*cc) : isdigit(*cc)))
      cc++;
    return cc;
  }
  const char *artBuiltIn_REAL(const char *cc) {
    if (!isdigit(*cc))
      return cc;
    while (isdigit(*cc))
      cc++;
    if (*cc != '.')
      return cc;
    cc++;
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
    cc++;
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
    cc++;
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
   cc++;
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
    cc++;
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
    cc++;
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
    descriptorsToBeProcessed = new container(23, tokenString, tokenLast - tokenString);
    descriptorsSeen = new container;
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
  }
 void startClock(){
    startTime = clock();
  }
 void stopClock() {
    stopTime = clock();
  }
  void printStatistics() {
    if (SPPF->printStatistics(grammarName, buildOptions, startSymbolName, tokenStringName, tokenString, ambiguityCount, tokenLast, inLanguage, 0, artLabelStrings, foldL) < 1000)
     VCGSPPF(rootSPPFNode, SPPF);
    cout << ", testRepeatCount, " << testRepeatCount
         << ", findDescriptorCount, " << findDescriptorCount
         << ", popCount, " << popCount
         << ", findGSSNodeCount, " << findGSSNodeCount
         << ", findSPPFNodeECount, " << findSPPFNodeECount
         << ", findSPPFNodeTCount, " << findSPPFNodeTCount
         << ", findSPPFNodeCount, " << findSPPFNodeCount
         << ", findSPPFNodeSkipLoopCount, " << findSPPFNodeSkipLoopCount;
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
};
unsigned container::hashMask;
unsigned container::hashBucketCount;
artLabel* container::tokenString;
unsigned container::inputExtent;
containerElement **container::hashTable;
void container::sane() {
}
int container::printStatistics(const char *grammarName, const char *buildOptions, const char *startSymbolName, const char *tokenStringName, artLabel* tokenString, int ambiguityCount, artLabel* tokenLast, bool inLanguage, double runTime, const char** artLabelStrings, enum artFold *foldL) {
  tokenStringName = "";
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
  for (int temp = 0; temp < container::hashBucketCount; temp++) {
    unsigned long bucketCardinality = 0;
    for (containerElement *element = container::hashTable[temp]; element != NULL; element = element->next) {
      bucketCardinality++;
      SPPFPackNode *tmp;
      switch (element->kind) {
        case CK_SPPFPackNode: sppfPnodeCount++; break;
        case CK_SPPFNode:
          sppfNonPnodeCount++;
          if (maxExtent < ((SPPFNode *) element)->rightExtent - tokenString)
            maxExtent = ((SPPFNode *) element)->rightExtent - tokenString;
          for (tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = ((SPPFNode *) element)->packNodes.iterSPPFPackNodeNext()) {
            sppfEdgeCount++;
            if (tmp->leftChildLabel != ART_L__DUMMY)
              sppfEdgeCount++;
            if (tmp->rightChildLabel != ART_L__DUMMY)
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
  }
  for (int tmp = 0; tmp < (tokenLast-tokenString + 1); tmp++) {
    if (uCardinality[tmp] > maxUi)
      maxUi = uCardinality[tmp];
  }
  if (sppfNonPnodeCount < 1000) {
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
    "ART_L__DUMMY" <<
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
runTime
      << ", tokens/second, " << tokensPerSecond <<
          ", ambiguous nodes, " << ambiguityCount <<
          ", GSS nodes, " << gssNodeCount << ", GSS edges, " << gssEdgeCount << ", Sum |Ui|, "<< descriptorCount << ", max |Ui|, " << maxUi <<
          ", SPPF nonPnodes, " << sppfNonPnodeCount << ", SPPF Pnodes, " << sppfPnodeCount << ", SPPF edges, " << sppfEdgeCount <<
          ", Pop elements, " << popElementCount <<
          ", testRepeat elements, " << testRepeatElementCount
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
enum artFold {ART_FOLD_EMPTY, ART_FOLD_UNDER, ART_FOLD_OVER, ART_FOLD_TEAR, ART_FOLD_UNDER_RECURSION, ART_FOLD_OVER_RECURSION};
void dumpHashTable(struct containerElement **hashTable, int hashBucketCount, const char** artLabelStrings, enum artLabel* tokenString);
enum artContainerKind{CK_SPPFPackNode, CK_SPPFNode, CK_GSSEdge, CK_GSSNode, CK_popElement, CK_descriptor, CK_testRepeatElement};
struct containerElement{artContainerKind kind; struct containerElement *next;};
struct listAnyElement{containerElement *payload; listAnyElement *next;};
class container{
public:
container(){ list = iterator = NULL; };
static artLabel* tokenString;
static unsigned inputExtent;
static unsigned hashMask;
static unsigned hashBucketCount;
static struct containerElement **hashTable;
struct listAnyElement *list;
struct listAnyElement *iterator;
container(unsigned hashBucketCountVal, artLabel* tokenStringVal, int inputExtentVal) {
  tokenString = tokenStringVal;
  inputExtent = inputExtentVal;
  hashBucketCount = 1;
  for (int temp = 0; temp < hashBucketCountVal; temp++)
    hashBucketCount *=2;
  hashTable = new containerElement*[hashBucketCount];
  hashMask = hashBucketCount - 1;
  memset(hashTable, 0, hashBucketCount * sizeof(containerElement*));
  list = iterator = NULL;
}
unsigned hash(artContainerKind a, artLabel b, artLabel c, artLabel d, artLabel *e, artLabel *f, artLabel*g, artLabel *h) {
  return (a * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          b * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          c * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          d * inputExtent * inputExtent * inputExtent * inputExtent +
          (e-tokenString) * inputExtent * inputExtent * inputExtent +
          (f-tokenString) * inputExtent * inputExtent +
          (g-tokenString) * inputExtent +
          (h-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel c, artLabel *d, artLabel *e, artLabel *f) {
  return (a * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent +
          b * ART__EXTENT * inputExtent * inputExtent * inputExtent +
          c * inputExtent * inputExtent * inputExtent +
          (d-tokenString) * inputExtent * inputExtent +
          (e-tokenString) * inputExtent +
          (f-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel *c, artLabel *d) {
  return (a * ART__EXTENT * inputExtent * inputExtent +
          b * inputExtent * inputExtent +
          (c-tokenString) * inputExtent +
          (d-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel *c) {
  return (a * ART__EXTENT * inputExtent +
          b * inputExtent +
          (c-tokenString)) & hashMask;
}
bool listEmpty();
void pushDescriptor(struct descriptor* newDescriptor);
struct descriptor* popDescriptor();
struct descriptor* notinDescriptor(artLabel restartLabel, struct GSSNode *stackTop, artLabel *currentToken, struct SPPFNode *derivationNode);
struct descriptor* addtoDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);
struct testRepeatElement* notinTestRepeatElement(artLabel regexpLabel, struct GSSNode *stackTop, artLabel *currentToken, struct SPPFNode *derivationNode);
struct testRepeatElement* addtoTestRepeatElement(artLabel regexptLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);
struct popElement* notinPopElement(GSSNode* stackTop, SPPFNode *derivationNode);
void addtoAndPushPopElement(GSSNode *stackTop, SPPFNode *derivationNode);
struct GSSNode* notinGSS(artLabel stackTopLabel, artLabel* currentToken);
struct GSSNode* addtoGSS(artLabel stackTopLabel, artLabel* currentToken);
struct SPPFNode* notinSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent);
struct SPPFNode* addtoSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent);
struct SPPFPackNode* notinPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken);
struct SPPFPackNode* addtoPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken);
void pushSPPFPackNode(struct SPPFPackNode * newPackNode);
struct GSSEdge* notinGSSEdge(GSSNode *newGSSNode, SPPFNode *derivationNode, GSSNode *stackTop);
struct GSSEdge* addtoAndPushGSSEdge(GSSNode *newGSSNode, SPPFNode *derivationNode, GSSNode *stackTop);
struct GSSEdge* iterGSSEdgeInit();
struct GSSEdge* iterGSSEdgeNext();
struct popElement* iterPopInit();
struct popElement* iterPopNext();
struct SPPFPackNode* iterSPPFPackNodeInit();
struct SPPFPackNode* iterSPPFPackNodeNext();
void clearReachable();
void VCGSPPFunreachable();
int printStatistics(const char *grammarName, const char *buildOptions, const char *startSymbolName, const char *tokenStringName, artLabel* tokenString, int ambiguityCount, artLabel* tokenLast, bool inLanguage, double runTime, const char** artLabelStrings, enum artFold *foldL);
void sane();
};
enum artFold {ART_FOLD_EMPTY, ART_FOLD_UNDER, ART_FOLD_OVER, ART_FOLD_TEAR, ART_FOLD_UNDER_RECURSION, ART_FOLD_OVER_RECURSION};
void dumpHashTable(struct containerElement **hashTable, int hashBucketCount, const char** artLabelStrings, enum artLabel* tokenString);
enum artContainerKind{CK_SPPFPackNode, CK_SPPFNode, CK_GSSEdge, CK_GSSNode, CK_popElement, CK_descriptor, CK_testRepeatElement};
struct containerElement{artContainerKind kind; struct containerElement *next;};
struct listAnyElement{containerElement *payload; listAnyElement *next;};
class container{
public:
container(){ list = iterator = NULL; };
static artLabel* tokenString;
static unsigned inputExtent;
static unsigned hashMask;
static unsigned hashBucketCount;
static struct containerElement **hashTable;
struct listAnyElement *list;
struct listAnyElement *iterator;
container(unsigned hashBucketCountVal, artLabel* tokenStringVal, int inputExtentVal) {
  tokenString = tokenStringVal;
  inputExtent = inputExtentVal;
  hashBucketCount = 1;
  for (int temp = 0; temp < hashBucketCountVal; temp++)
    hashBucketCount *=2;
  hashTable = new containerElement*[hashBucketCount];
  hashMask = hashBucketCount - 1;
  memset(hashTable, 0, hashBucketCount * sizeof(containerElement*));
  list = iterator = NULL;
}
unsigned hash(artContainerKind a, artLabel b, artLabel c, artLabel d, artLabel *e, artLabel *f, artLabel*g, artLabel *h) {
  return (a * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          b * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          c * ART__EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          d * inputExtent * inputExtent * inputExtent * inputExtent +
          (e-tokenString) * inputExtent * inputExtent * inputExtent +
          (f-tokenString) * inputExtent * inputExtent +
          (g-tokenString) * inputExtent +
          (h-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel c, artLabel *d, artLabel *e, artLabel *f) {
  return (a * ART__EXTENT * ART__EXTENT * inputExtent * inputExtent * inputExtent +
          b * ART__EXTENT * inputExtent * inputExtent * inputExtent +
          c * inputExtent * inputExtent * inputExtent +
          (d-tokenString) * inputExtent * inputExtent +
          (e-tokenString) * inputExtent +
          (f-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel *c, artLabel *d) {
  return (a * ART__EXTENT * inputExtent * inputExtent +
          b * inputExtent * inputExtent +
          (c-tokenString) * inputExtent +
          (d-tokenString)) & hashMask;
}
unsigned hash(artContainerKind a, artLabel b, artLabel *c) {
  return (a * ART__EXTENT * inputExtent +
          b * inputExtent +
          (c-tokenString)) & hashMask;
}
bool listEmpty();
void pushDescriptor(struct descriptor* newDescriptor);
struct descriptor* popDescriptor();
struct descriptor* notinDescriptor(artLabel restartLabel, struct GSSNode *stackTop, artLabel *currentToken, struct SPPFNode *derivationNode);
struct descriptor* addtoDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);
struct testRepeatElement* notinTestRepeatElement(artLabel regexpLabel, struct GSSNode *stackTop, artLabel *currentToken, struct SPPFNode *derivationNode);
struct testRepeatElement* addtoTestRepeatElement(artLabel regexptLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode);
struct popElement* notinPopElement(GSSNode* stackTop, SPPFNode *derivationNode);
void addtoAndPushPopElement(GSSNode *stackTop, SPPFNode *derivationNode);
struct GSSNode* notinGSS(artLabel stackTopLabel, artLabel* currentToken);
struct GSSNode* addtoGSS(artLabel stackTopLabel, artLabel* currentToken);
struct SPPFNode* notinSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent);
struct SPPFNode* addtoSPPF(artLabel label, artLabel *leftExtent, artLabel *rightExtent);
struct SPPFPackNode* notinPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken);
struct SPPFPackNode* addtoPack(SPPFNode* parent, artLabel stackTopLabel, artLabel* currentToken);
void pushSPPFPackNode(struct SPPFPackNode * newPackNode);
struct GSSEdge* notinGSSEdge(GSSNode *newGSSNode, SPPFNode *derivationNode, GSSNode *stackTop);
struct GSSEdge* addtoAndPushGSSEdge(GSSNode *newGSSNode, SPPFNode *derivationNode, GSSNode *stackTop);
struct GSSEdge* iterGSSEdgeInit();
struct GSSEdge* iterGSSEdgeNext();
struct popElement* iterPopInit();
struct popElement* iterPopNext();
struct SPPFPackNode* iterSPPFPackNodeInit();
struct SPPFPackNode* iterSPPFPackNodeNext();
void clearReachable();
void VCGSPPFunreachable();
int printStatistics(const char *grammarName, const char *buildOptions, const char *startSymbolName, const char *tokenStringName, artLabel* tokenString, int ambiguityCount, artLabel* tokenLast, bool inLanguage, double runTime, const char** artLabelStrings, enum artFold *foldL);
void sane();
};
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
  if (node->sibling != NULL)
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
  artLabel startLabel;
  bool inLanguage;
  clock_t startTime;
  clock_t stopTime;
  const char *grammarName;
  const char *buildOptions;
  const char* startSymbolName;
  const char* tokenStringName;
  artLabel* tokenString;
  int inputLength;
  const char **tokenStart;
  const char **tokenEnd;
  artLabel* tokenLast;
  artLabel *currentToken;
  artLabel currentRestartLabel;
  const char **artLabelStrings;
  const char **artRDTStrings;
  bool *artTerminalRequiresWhiteSpace;
  bool *artTerminalCaseInsensitive;
  int ambiguityCount;
  enum artFold *foldL;
  artLabel *lhsL;
  artLabel *pL;
  artLabel *aL;
  bool *fiRL;
  bool *eoOPL;
  bool *eoRL;
  bool *eoR_pL;
  SPPFNode *dummySPPFNode;
  SPPFNode *currentSPPFNode;
  SPPFNode *temporarySPPFNode;
  SPPFNode *currentSPPFRightChildNode;
  descriptor *currentDescriptor;
  GSSNode *currentStackTop;
  GSSNode *rootGSSNode;
  SPPFNode *rootSPPFNode;
  container *descriptorsToBeProcessed;
  container *descriptorsSeen;
  container *GSS;
  container *SPPF;
  container *testRepeatElementsSeen;
  int testRepeatCount;
  int findDescriptorCount;
  int popCount;
  int findGSSNodeCount;
  int findSPPFNodeECount;
  int findSPPFNodeTCount;
  int findSPPFNodeCount;
  int findSPPFNodeSkipLoopCount;
SPPFNode *findSPPFNode(artLabel *currentToken);
SPPFNode *findSPPFNode(artLabel terminalLabel, artLabel *currentToken);
SPPFNode *findSPPFNode(artLabel justMatchedLabel, SPPFNode *leftChild, SPPFNode *rightChild);
SPPFNode *findSPPFNode(artLabel parentLabel, artLabel childLabel, artLabel *currentToken);
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
  bool lexLongestMatch(const char *parseString) {
    tokenString = new artLabel[strlen(parseString) + 1];
    tokenStart = new const char*[strlen(parseString) + 1];
    tokenEnd = new const char*[strlen(parseString) + 1];
    const char *pp = parseString;
    currentToken = tokenString;
    artLabel longestToken;
    pp = lexBuiltinWhitespace(pp);
    while (1) {
      if (*pp == 0) {
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        inputLength = currentToken - tokenString;
        return true;
      }
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
      lexBuiltInInstances(pp, &longestToken, &longestLength);
      if(longestLength == 0) {
        cout << "Error: at input position " << (pp - parseString) << " lexer found unexpected input character " << (int) *pp << " (" << *pp << ")\n";
        *currentToken = ART__EOS;
        tokenLast = currentToken;
        return false;
      }
      tokenStart[currentToken-tokenString] = pp;
      pp += longestLength;
      tokenEnd[currentToken-tokenString] = pp;
      *currentToken++ = longestToken;
      if (artTerminalRequiresWhiteSpace[longestToken]) {
        pp = lexBuiltinWhitespace(pp);
      }
    }
  }
  const char *artBuiltIn_OCAML_LABEL_NAME(const char *cc) {
    if (isalpha(*cc) || *cc == '_')
      while (isalnum(*cc) || *cc == '_' || *cc == '`' || *cc == '\'')
        cc++;
    return cc;
  }
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
    bool hex = (*cc == '0' && (*(cc + 1) == 'x' || *(cc + 1) == 'X'));
    if (hex) cc += 2;
    while ((hex ? isxdigit(*cc) : isdigit(*cc)))
      cc++;
    return cc;
  }
  const char *artBuiltIn_REAL(const char *cc) {
    if (!isdigit(*cc))
      return cc;
    while (isdigit(*cc))
      cc++;
    if (*cc != '.')
      return cc;
    cc++;
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
    cc++;
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
    cc++;
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
   cc++;
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
    cc++;
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
    cc++;
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
    descriptorsToBeProcessed = new container(23, tokenString, tokenLast - tokenString);
    descriptorsSeen = new container;
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
  }
 void startClock(){
    startTime = clock();
  }
 void stopClock() {
    stopTime = clock();
  }
  void printStatistics() {
    if (SPPF->printStatistics(grammarName, buildOptions, startSymbolName, tokenStringName, tokenString, ambiguityCount, tokenLast, inLanguage, 0, artLabelStrings, foldL) < 1000)
     VCGSPPF(rootSPPFNode, SPPF);
    cout << ", testRepeatCount, " << testRepeatCount
         << ", findDescriptorCount, " << findDescriptorCount
         << ", popCount, " << popCount
         << ", findGSSNodeCount, " << findGSSNodeCount
         << ", findSPPFNodeECount, " << findSPPFNodeECount
         << ", findSPPFNodeTCount, " << findSPPFNodeTCount
         << ", findSPPFNodeCount, " << findSPPFNodeCount
         << ", findSPPFNodeSkipLoopCount, " << findSPPFNodeSkipLoopCount;
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
};
void GLLParser::checkAcceptance(artLabel startLabel) {
  this->startLabel = startLabel;
  rootSPPFNode = SPPF->notinSPPF(startLabel, tokenString, tokenLast);
  inLanguage = rootSPPFNode != NULL;
}
bool GLLParser::testRepeat(artLabel regexpLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
  testRepeatCount++;
    if (testRepeatElementsSeen->notinTestRepeatElement(regexpLabel, stackTop, currentToken, derivationNode) == NULL) {
   testRepeatElementsSeen->addtoTestRepeatElement(regexpLabel, stackTop, currentToken, derivationNode);
    return false;
  }
  return true;
}
void GLLParser::findDescriptor(artLabel restartLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *derivationNode) {
  findDescriptorCount++;
    if (descriptorsSeen->notinDescriptor(restartLabel, stackTop, currentToken, derivationNode) == NULL) {
    descriptor *newDescriptor = descriptorsSeen->addtoDescriptor(restartLabel, stackTop, currentToken, derivationNode);
    descriptorsToBeProcessed->pushDescriptor(newDescriptor);
  }
}
void GLLParser::pop(GSSNode *stackTop, artLabel *currentToken, SPPFNode *currentDerivationNode) {
  popCount++;
  if (stackTop == rootGSSNode) {
    return;
  }
  if (stackTop->poppedNodes.notinPopElement(stackTop, currentDerivationNode) == NULL) {
    stackTop->poppedNodes.addtoAndPushPopElement(stackTop, currentDerivationNode);
}
  for (GSSEdge *outEdge = stackTop->outEdges.iterGSSEdgeInit(); outEdge != NULL; outEdge = stackTop->outEdges.iterGSSEdgeNext()) {
    SPPFNode *derivationNode = GLLParser::findSPPFNode(stackTop->label, outEdge->edgeLabel, currentDerivationNode);
    GLLParser::findDescriptor(stackTop->label, outEdge->destination, currentToken, derivationNode);
  }
}
GSSNode *GLLParser::findGSSNode(artLabel stackTopLabel, GSSNode *stackTop, artLabel *currentToken, SPPFNode *currentDerivationNode) {
  findGSSNodeCount++;
GSSNode *newGSSNode;
  if ((newGSSNode = GSS->notinGSS(stackTopLabel, currentToken)) == NULL) {
    newGSSNode = GSS->addtoGSS(stackTopLabel, currentToken);
  }
  if (stackTop != NULL && newGSSNode->outEdges.notinGSSEdge(newGSSNode, currentDerivationNode, stackTop) == NULL) {
    newGSSNode->outEdges.addtoAndPushGSSEdge(newGSSNode, currentDerivationNode, stackTop);
    for (popElement *poppedElement = newGSSNode->poppedNodes.iterPopInit(); poppedElement != NULL; poppedElement = newGSSNode->poppedNodes.iterPopNext()) {
      SPPFNode *derivationNode = GLLParser::findSPPFNode(stackTopLabel, currentDerivationNode, poppedElement->derivationNode);
      GLLParser::findDescriptor(stackTopLabel, stackTop, poppedElement->derivationNode->rightExtent, derivationNode);
    }
  }
  return newGSSNode;
}
SPPFNode *GLLParser::findSPPFNode(artLabel *currentToken) {
  findSPPFNodeECount++;
  SPPFNode *newSPPFNode;
  if ((newSPPFNode = SPPF->notinSPPF(ART__EPSILON, currentToken, currentToken) ) == NULL) {
    newSPPFNode = SPPF->addtoSPPF(ART__EPSILON, currentToken, currentToken);
  }
  return newSPPFNode;
}
SPPFNode *GLLParser::findSPPFNode(artLabel terminalLabel, artLabel *currentToken) {
  findSPPFNodeTCount++;
  SPPFNode *newSPPFNode;
  if ((newSPPFNode = SPPF->notinSPPF(terminalLabel, currentToken, currentToken + 1) ) == NULL) {
    newSPPFNode = SPPF->addtoSPPF(terminalLabel, currentToken, currentToken + 1);
  }
  return newSPPFNode;
}
SPPFNode *GLLParser::findSPPFNode(artLabel justMatchedLabel, SPPFNode *leftChild, SPPFNode *rightChild) {
  findSPPFNodeCount++;
  if (fiRL[justMatchedLabel]) {
    return rightChild;
  }
  SPPFNode *newSPPFNode;
  artLabel* leftExtent = (leftChild == dummySPPFNode ? rightChild->leftExtent : leftChild->leftExtent);
  artLabel newSPPFNodeLabel;
  if (eoOPL[justMatchedLabel])
    newSPPFNodeLabel = eoR_pL[justMatchedLabel] ? lhsL[justMatchedLabel] : pL[justMatchedLabel];
  else {
    newSPPFNodeLabel = eoRL[justMatchedLabel] ? lhsL[justMatchedLabel] : aL[justMatchedLabel];
  }
  if ((newSPPFNode = SPPF->notinSPPF(newSPPFNodeLabel, leftExtent, rightChild->rightExtent) ) == NULL) {
    newSPPFNode = SPPF->addtoSPPF(newSPPFNodeLabel, leftExtent, rightChild->rightExtent);
  }
  if (newSPPFNode->packNodes.notinPack(newSPPFNode, justMatchedLabel, rightChild->leftExtent) == NULL) {
    SPPFPackNode *x = newSPPFNode->packNodes.addtoPack(newSPPFNode, justMatchedLabel, rightChild->leftExtent);
    x->leftChildLabel = leftChild->label;
    x->rightChildLabel = rightChild->label;
  }
  return newSPPFNode;
}
SPPFNode *GLLParser::findSPPFNode(artLabel parentLabel, artLabel childLabel, artLabel *currentToken) {
  findSPPFNodeSkipLoopCount++;
  SPPFNode *newSPPFNode;
  if ((newSPPFNode = SPPF->notinSPPF(parentLabel, currentToken, currentToken) ) == NULL) {
    newSPPFNode = SPPF->addtoSPPF(parentLabel, currentToken, currentToken);
  }
  if (newSPPFNode->packNodes.notinPack(newSPPFNode, childLabel, currentToken) == NULL) {
    SPPFPackNode *x = newSPPFNode->packNodes.addtoPack(newSPPFNode, childLabel, currentToken);
    x->leftChildLabel = ART_L__DUMMY;
    x->rightChildLabel = ART__EPSILON;
    findSPPFNode(currentToken);
  }
  return newSPPFNode;
}
void GLLParser::disambiguateRec(SPPFNode *SPPFNode) {
  int outDegree = 0;
  if (SPPFNode == NULL)
    return;
  if (SPPFNode->reachable)
    return;
  SPPFNode->reachable = true;
  for (SPPFPackNode *tmp = SPPFNode->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = SPPFNode->packNodes.iterSPPFPackNodeNext()) {
    tmp->reachable = true;
    outDegree++;
    if (tmp->leftChildLabel != ART_L__DUMMY)
      disambiguateRec(SPPF->notinSPPF(tmp->leftChildLabel, SPPFNode->leftExtent, tmp->pivot));
    if (tmp->rightChildLabel != ART_L__DUMMY)
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
  if (node == NULL)
    return;
  if (node->reachable)
    return;
  node->reachable = true;
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
    if (tmp->leftChildLabel != ART_L__DUMMY) {
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
    if (tmp->rightChildLabel != ART_L__DUMMY) {
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
    return NULL;
  if (parentRDTNode == NULL)
    rdtNode = new RDTNode(SPPFNode->label);
  else
    rdtNode = new RDTNode(SPPFNode->label, parentRDTNode);
  int outDegree = 0;
  SPPFNode->reachable = true;
  for (SPPFPackNode *tmp = SPPFNode->packNodes.iterSPPFPackNodeInit(); tmp != NULL; tmp = SPPFNode->packNodes.iterSPPFPackNodeNext()) {
    outDegree++;
    SPPFNode->ambiguous = outDegree > 1;
    if ((tmp->leftChildLabel != ART_L__DUMMY && SPPF->notinSPPF(tmp->leftChildLabel, SPPFNode->leftExtent, tmp->pivot)->reachable) ||
        (tmp->rightChildLabel != ART_L__DUMMY && SPPF->notinSPPF(tmp->rightChildLabel, tmp->pivot, SPPFNode->rightExtent)->reachable))
      continue;
    if (tmp->pivot >= pivot) {
      candidate = tmp;
      pivot = tmp->pivot;
    }
  }
  if (candidate != NULL) {
    candidate->reachable = true;
    if (candidate->leftChildLabel != ART_L__DUMMY)
      rdt(SPPF->notinSPPF(candidate->leftChildLabel, SPPFNode->leftExtent, candidate->pivot), rdtNode, suppressIntermediates);
    if (candidate->rightChildLabel != ART_L__DUMMY)
      rdt(SPPF->notinSPPF(candidate->rightChildLabel, candidate->pivot, SPPFNode->rightExtent), rdtNode, suppressIntermediates);
  }
  return rdtNode;
}
