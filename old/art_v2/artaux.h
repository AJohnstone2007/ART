/* artAux.h - ART V2 support routine header (c) Adrian Johnstone 2009-2012 */
#ifndef ARTAUX_H
#define ARTAUX_H

enum artDespatchKind {ART_DESPATCH_DYNAMIC, ART_DESPATCH_STATIC, ART_DESPATCH_STATE, ART_DESPATCH_FRAGMENT /*, ART_DESPATCH_FRAGMENT_DECLARATION*/};
enum artSupportKind {ART_SUPPORT_BFS, ART_SUPPORT_HP, ART_SUPPORT_OO};
enum artTargetLanguageKind {ART_LANGUAGE_DEFAULT, ART_LANGUAGE_CPP, ART_LANGUAGE_JAVA};

enum artFold {ART_FOLD_EMPTY, ART_FOLD_NONE, ART_FOLD_UNDER, ART_FOLD_OVER, ART_FOLD_TEAR, ART_FOLD_TEAR_PARENT};

enum artKind {ART_ILLEGAL, ART_END_OF_STRING,
              ART_ROOT, ART_MAJOR, ART_PRODUCTION,

              ART_EPSILON, ART_BUILTIN_TERMINAL, ART_CHARACTER_TERMINAL, ART_CASE_SENSITIVE_TERMINAL, ART_CASE_INSENSITIVE_TERMINAL, ART_NONTERMINAL,

              ART_LHS, ART_UNARY, ART_CAT, ART_ALT, ART_DIFF, ART_ITER, ART_TEAR,
              ART_DO_FIRST, ART_OPTIONAL, ART_POSITIVE_CLOSURE, ART_KLEENE_CLOSURE,
              ART_POS, ART_ANNOTATION, ART_ANNOTATION_VALUE, ART_INSERTION, ART_POS_NAME, ART_ATTRIBUTE, ART_INSTANCE};

enum artLUT { ART_LUT_set, ART_LUT_artLabelStrings, ART_LUT_artAnnotations, ART_LUT_artPreSlots, ART_LUT_artPostSlots,
              ART_LUT_artInstanceOfs, ART_LUT_artSlotInstanceOfs, ART_LUT_artKindOfs, ART_LUT_artUserNameOfs, ART_LUT_artTerminalRequiresWhiteSpace, ART_LUT_artTerminalCaseInsensitive,
              ART_LUT_lhsL, ART_LUT_foldL, ART_LUT_pL, ART_LUT_aL, ART_LUT_fiRL, ART_LUT_eoOPL, ART_LUT_eoRL, ART_LUT_eoR_pL, ART_LUT_popD};

enum artGrammarFormatKind {ART_GRAMMAR_NONE, ART_GRAMMAR_ART, ART_GRAMMAR_ANTLR, ART_GRAMMAR_BISON, ART_GRAMMAR_DPARSER, ART_GRAMMAR_ELKHOUND, ART_GRAMMAR_GTB, ART_GRAMMAR_RASCAL, ART_GRAMMAR_RATS, ART_GRAMMAR_RDP, ART_GRAMMAR_SDF, ART_GRAMMAR_STRATEGO};

#include "artparse.h"

struct posAttributes {
   rdp_tree_node_data* treeNode;
};

/* Symbol table support */

typedef struct symbols_data_node {
  enum artKind kind;                         // The flavour of this symbol
  const char* majorID;                       // The major part of a name: "" empty for terminals which live in their own namespaces
  const char* minorID;                       // The pattern for a terminal, the minor name of a nonterminal, or the name of a major
  const char* attributeID;                   // The name of an attribute (ART_ATTRIBUTE ONLY)
  const char* attributeType;                 // The name of an attribute's type (ART_ATTRIBUTE ONLY)
  const char* lhsAttributeName;              // Th estring attr from X:attr ::= ... 
  rdp_tree_node_data* PNode;                 // A pointer to this symbol's LHS node (NONTERMINALS ONLY) in P or P major root (MAJORS ONLY)
  rdp_tree_node_data* PPrimeNode;            // A pointer to this symbol's LHS node (NONTERMINALS ONLY) in P' or P' major root (MAJORS ONLY)
  rdp_tree_node_data* DNode;                 // A pointer to this symbol's LHS node (NONTERMINALS ONY ) in D or D major root (MAJORS ONLY)
  rdp_tree_node_data* iftNode;               // A pointer to this symbol's LHS node (NONTERMINALS ONY ) in IFT or IFT major root (MAJORS ONLY)
  unsigned number;                           // Unique symbol number
  unsigned nextInstanceNumber;               // A counter used when allocating instance numbers
  set_ dependency;                           // A set of symbol numbers; used for GDG (nonterminals) and MDG (majors)
  struct iPrimeNode *whiteSpaceImport;       // a pointer to the whitespace import for this major (MAJORS ONLY)
  rdp_tree_node_data *builtinWhitespaceNode; // a pointer to the whitespace
  struct symbols_data_node *startSymbol;     // a pointer to the start symbol for this major (MAJORS ONLY)
  bool used;                                 // used/reachability flag
  bool hasDelayedInstances;                  // true if there are any right hand side instances for this nonterminal which have the delayed < annotation
  bool containsDelayedInstances;             // true if there are any right hand side instances for this nonterminal which have the delayed < annotation
  bool isGatherTarget;                       // appears as target in ! gather operatort
  bool defined;                              // has appeared as an M() (MAJORS) or as an M ::= (NONTERMINALS)
  bool deleted;                              // a nonterminal whose LHS subtree has been deleted
  bool isLexical;                            // a nonterminal which appears in a lexical declaration
  struct symbols_list_node *lexicalPriority;
  struct symbols_list_node *lexicalShorter;
  struct symbols_list_node *lexicalLonger;
  struct symbols_list_node *attributes;
  } symbols_data;

typedef struct symbols_list_node {
  symbols_data_node *symbol;
  struct symbols_list_node *next;
} symbols_list;

extern void * symbols;

typedef struct mergedSets_data_node
{
  set_ set;
  int number;
  int usedInParser: 1;
  int inverted: 1;

} mergedSets_data;

extern const char* asciiIdentifiers[];
extern const char* asciiStrings[];
extern const char* asciiQuotedStrings[];
extern struct symbols_list_node *supportAttributes;
extern void * mergedSets;
extern int artMergedSetCount;
extern int artFirstFreeSymbolNumber;
extern struct symbols_data_node *artMainMajorSymbol;
extern struct symbols_data_node *artStartSymbol;
extern struct symbols_data_node *artEpsilonSymbol;
extern struct symbols_data_node *artPosSymbol;
extern struct symbols_data_node *artEndOfStringSymbol;
extern int artVersion;
extern int artVerbose;
extern int artClosureRight;
extern int artClosureLeft;
extern int artWarnOnMultiple;
extern int artMultiplyOut;
extern int artLeftFactor;
extern int artBracketToNonterminal;
extern int artSuppressPopGuard;
extern int artSuppressProductionGuard;
extern int artSuppressNonterminalGuard;
extern int artTestRepeatNo;
extern int artErrorOnWirth;
extern int artSuppressSemantics;
extern unsigned long artTreeLevel;
extern int artExploitDeterminism;
extern int artClusteredGSS;
extern int artDelayPoppingDescriptors;
extern int artPredictivePops;
extern int artFIFODescriptors;
extern int artEOSFollow;
extern int artMGLL;
extern const char* artOutputLanguage;
extern const char* artOutputDirectory;
extern const char* artParserName;
extern const char* artLexerName;
extern const char* artParserNamespace;

extern const char* artDespatchModeString;
extern const char* artSupportModeString;
extern const char* artTargetLanguageString;
extern const char* artGrammarFormatString;
extern const char* preludeText;
extern const char* supportText;

extern enum artDespatchKind artDespatchMode;
extern enum artSupportKind artSupportMode;
extern enum artTargetLanguageKind artTargetLanguage;
extern enum artGrammarFormatKind artGrammarFormat;

extern int artGrammarIsEBNF;
extern int artGrammarIsFBNF;

extern bool **lexicalPriorityRelation;
extern bool **lexicalShorterRelation;
extern bool **lexicalLonggerRelation;

void artErrorCheck();

bool artHasKeywords();

bool artIsNonterminalNode(rdp_tree_node_data* node, const char* label);

rdp_tree_node_data *artNewTreeNodeHead(rdp_tree_node_data *parent, const char* id, artKind kind, symbols_data_node * tableEntry);
rdp_tree_node_data *artNewTreeNode(rdp_tree_node_data *parent, const char* id, artKind kind, symbols_data_node * tableEntry);
void artCloneTreeAsChildOf(rdp_tree_node_data *parent, rdp_tree_node_data *src);
void artCloneTreeAsChildOf(rdp_tree_node_data *parent, rdp_tree_node_data *src,
                           symbols_data *terminalWSSymbol,
                           symbols_data **rewriteSet,
                           const char* rewriteMajorID,
                           symbols_data *leadingWrapperSymbol,
                           symbols_data *trailingWrapperSymbol
                          );
void artDeleteTree(rdp_tree_node_data *src);

rdp_tree_node_data *artLeftmostElementRec(rdp_tree_node_data* node);
rdp_tree_node_data *artRightmostElementRec(rdp_tree_node_data* node);
rdp_tree_node_data *artFirstChildOf(rdp_tree_node_data* node);

struct symbols_data_node *artLookup(enum artKind kind, const char* majorId, const char* id, const char* attributeID);
struct symbols_data_node *artFind(enum artKind kind, const char* majorID, const char* minorID, const char* attributeID);
void artLoadSymbolsRec(rdp_tree_node_data* node, const char* majorID);
//int artMergedSetNumber(set_ *s);
void artWriteSymbols();

void artRedirectToFile(const char* fileName);
int artWriteSymbol(symbols_data *symbol);
int artWriteSymbol(artKind kind, const char* pattern, const char* majorID, const char* attributeID, const char* attributeType);
void artWriteAsID(const char* string);
void artWriteAsString(const char* string);
void artWriteNodeLabelAsID(const char* prefix, rdp_tree_node_data  *node);
void artWriteNodeLabelAsString(const char* prefix, rdp_tree_node_data  *node);
void artWriteSymbolAsTerminalID(symbols_data *symbol);
void artWriteSymbolAsTerminalString(symbols_data *symbol);
void artVCGRDT(const char* filename, void *rdp_tree);
void artWriteGrammarRec(rdp_tree_node_data *node, int level);

void artPreParse();
void artPostParse(void* RDT);

/* artrdtcook.cpp *************************************************************/
void artCookRDT(void *RDT);
bool artRewriteEBNF(rdp_tree_node_data *node);
bool artRewriteIterators(rdp_tree_node_data *node);
bool artTestFoldWellFormed(rdp_tree_node_data* node);
bool artFixupCatUnary(rdp_tree_node_data *node);
void artApplyToProductionBodies(void *RDT, bool (action) (rdp_tree_node_data* node));
bool artRewriteClosuresLeft(rdp_tree_node_data *node);
bool artRewriteClosuresRight(rdp_tree_node_data *node);
bool artRewriteBrackets(rdp_tree_node_data *node);
void artWriteLexicalDisambiguationRelations();

/* artmultiplyout.cpp *********************************************************/
void artMultiplyOutProductions(void* RDT);

/* artfactorise.cpp ***********************************************************/
bool artEqualSubtreeRec(bool deleterMatch, rdp_tree_node_data* left, rdp_tree_node_data* right);
void artLeftFactorise(void* tree);

/* artinduce.cpp **************************************************************/

void artWriteImport(iPrimeNode *node);
void *artInduceGrammar(void *RDT);
void artGrammarConsistency(void);

/* artift.cpp *****************************************************************/
void artResolveTearsRec(rdp_tree_node_data* rdpTree);
void artComputeSiblingLinksAndAritiesRec(rdp_tree_node_data *node, rdp_tree_node_data *rightSiblingNode);
void artComputeSets();
void artComputeAttributes();
bool artMergedSet(set_ *s);
int artMergedSetNumber(set_ *s);
bool artComputeGuardSetsRec(rdp_tree_node_data* node, rdp_tree_node_data* productionNode, rdp_tree_node_data* bracketNode);
void artComputeMergedSets(void *tree);

/* artgdg.cpp */
void artWriteGDGMDG();

/* artwritegrammar.cpp ******************************************************/
void artWriteGrammarOutput(enum artGrammarFormatKind format);
#endif

