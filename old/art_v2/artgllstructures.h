/*******************************************************************************
*
* ART V1.00 production GLL parsing support (c) Adrian Johnstone 2011
*
*******************************************************************************/
#if !defined ARTGLLSTRUCTURES_H
#define ARTGLLSTRUCTURES_H
#include "artgllbase.h"

void dumpHashTable(struct containerElement **hashTable, int hashBucketCount, const char** artLabelStrings, enum artLabel* tokenString);

// A tagged union which can contain a reference to any basic datastructure element - used for hash tables and the despatchStack
enum artContainerKind{CK_SPPFPackNode, CK_SPPFNode, CK_GSSEdge, CK_GSSNode, CK_popElement, CK_descriptor, CK_testRepeatElement};
struct containerElement{artContainerKind kind; struct containerElement *next;};
//struct anyElement{artContainerKind tag; struct anyElement *next;};
struct listAnyElement{containerElement *payload; listAnyElement *next;};

class container{

public:

container(){ list = iterator = NULL; }; /* Normal constructor which doesn't touch class variables */

/* This constructor actually initialises the class variables associated with the hash table
   There must be exactly one instantiation of a container object that activates this constructor before any
   other methods are called. This is a pretty dirty trick.
*/
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
  return (a /** ART_EXTENT*/ * ART_EXTENT * ART_EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          b               * ART_EXTENT * ART_EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          c                             * ART_EXTENT * inputExtent * inputExtent * inputExtent * inputExtent +
          d                                           * inputExtent * inputExtent * inputExtent * inputExtent +
          (e-tokenString)                                           * inputExtent * inputExtent * inputExtent +
          (f-tokenString)                                                         * inputExtent * inputExtent +
          (g-tokenString)                                                                       * inputExtent +
          (h-tokenString)) & hashMask;
}

unsigned hash(artContainerKind a, artLabel b, artLabel c, artLabel *d, artLabel *e, artLabel *f) {
  return (a * ART_EXTENT * ART_EXTENT * inputExtent * inputExtent * inputExtent  +
          b               * ART_EXTENT * inputExtent * inputExtent * inputExtent  +
          c                             * inputExtent * inputExtent * inputExtent  +
          (d-tokenString)                             * inputExtent * inputExtent  +
          (e-tokenString)                                           * inputExtent  +
          (f-tokenString)) & hashMask;
}

unsigned hash(artContainerKind a, artLabel b, artLabel *c, artLabel *d) {
  return (a * ART_EXTENT * inputExtent * inputExtent +
          b               * inputExtent * inputExtent +
          (c-tokenString)               * inputExtent +
          (d-tokenString)) & hashMask;
}

unsigned hash(artContainerKind a, artLabel b, artLabel *c) {
  return (a * ART_EXTENT * inputExtent +
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
#endif
