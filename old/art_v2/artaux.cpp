/* artaux.cpp - ART V2.00 auxiliary routines (c) Adrian Johnstone 2009-2012 */
#include <stdlib.h>
#include "artaux.h"
#include "artparserwriter.h"

int artVerbose = 0;
int artVersion = 0;
int artClosureLeft = 0;
int artClosureRight = 0;
int artWarnOnMultiple = 0;
int artMultiplyOut = 0;
int artLeftFactor = 0;
int artBracketToNonterminal = 0;
int artSuppressPopGuard = 0;
int artSuppressProductionGuard = 0;
int artSuppressNonterminalGuard = 0;
int artTestRepeatNo = 0;
int artErrorOnWirth = 0;
int artSuppressSemantics = 0;
unsigned long artTreeLevel = 3;
int artExploitDeterminism = 0;
int artClusteredGSS = 0;
int artDelayPoppingDescriptors = 0;
int artPredictivePops = 0;
int artFIFODescriptors = 0;
int artEOSFollow = 0;
int artMGLL = 0;

int artGrammarIsEBNF;
int artGrammarIsFBNF;

const char* artOutputDirectory = ".";
const char* artDespatchModeString = "";
const char* artSupportModeString = "HashPool";
const char* artTargetLanguageString = "Java";
const char* artGrammarFormatString = "";
const char* artParserName = "ARTGLLParser";
const char* artLexerName = "ARTGLLLexer";
const char* artParserNamespace = NULL;

enum artDespatchKind artDespatchMode;
enum artSupportKind artSupportMode;
enum artTargetLanguageKind artTargetLanguage;
enum artGrammarFormatKind artGrammarFormat;

void * symbols;
void * mergedSets;
struct symbols_data_node *artMainMajorSymbol = NULL;
struct symbols_data_node *artStartSymbol = NULL;
symbols_data *artEpsilonSymbol;
symbols_data *artPosSymbol;
symbols_data *artEndOfStringSymbol;
int artMergedSetCount;
const char* preludeText = NULL;
const char* supportText = NULL;
struct symbols_list_node *supportAttributes;

const char* asciiIdentifiers[] = {
  "_NUL", "_SOH", "_STX", "_ETX", "_EOT", "_ENQ", "_ACK", "_BEL", "_BS", "_HT", "_LF", "_VT", "_FF",
  "_CR", "_SO", "_SI", "_DLE", "_DC1", "_DC2", "_DC3", "_DC4", "_NAK", "_SYN", "_ETB", "_CAN",
  "_EM", "_SUB", "_ESC", "_FS", "_GS", "_RS", "_", "_SPACE", "_SHREIK", "_DBLQUOTE", "_HASH",
  "_DOLLAR", "_PERCENT", "_AMPERSAND", "_QUOTE", "_LPAR", "_RPAR", "_STAR", "_PLUS",
  "_COMMA", "_MINUS", "_PERIOD", "_SLASH", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
  "_COLON", "_SEMICOLON", "_LT", "_EQUAL", "_GT", "_QUERY", "_AT", "A", "B", "C", "D", "E",
  "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
  "X", "Y", "Z", "_LBRACK", "_BACKSLASH", "_RBRACK", "_UPARROW", "_", "_BACKQUOTE", "a", "b", "c",
  "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
  "v", "w", "x", "y", "z", "_LBRACE", "_BAR", "_RBRACE", "_TILDE", "_DEL"};

const char* asciiStrings[] = {
"\\000", "\\001", "\\002", "\\003", "\\004", "\\005", "\\006", "\\007", "\\010", "\\t",   "\\n",   "\\013", "\\014", "\\r",   "\\016", "\\017",
"\\020", "\\021", "\\022", "\\023", "\\024", "\\025", "\\026", "\\027", "\\030", "\\031", "\\032", "\\033", "\\034", "\\035", "\\036", "\\037",
" ", "!", "\\\"", "#",
"$", "%", "&", "'", "(", ")", "*", "+",
",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E",
"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
"X", "Y", "Z", "[", "\\\\", "]", "^", "_", "`", "a", "b", "c",
"d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
"v", "w", "x", "y", "z", "{", "|", "}", "~", "_DEL"};

const char* asciiQuotedStrings[] = {
"\\\\\\\\000", "\\\\\\\\001", "\\\\\\\\002", "\\\\\\\\003", "\\\\\\\\004", "\\\\\\\\005", "\\\\\\\\006", "\\\\\\\\007", "\\\\\\\\010", "\\\\\\\\t",   "\\\\\\\\n",   "\\\\\\\\013", "\\\\\\\\014", "\\\\\\\\r",   "\\\\\\\\016", "\\\\\\\\017",
"\\\\\\\\020", "\\\\\\\\021", "\\\\\\\\022", "\\\\\\\\023", "\\\\\\\\024", "\\\\\\\\025", "\\\\\\\\026", "\\\\\\\\027", "\\\\\\\\030", "\\\\\\\\031", "\\\\\\\\032", "\\\\\\\\033", "\\\\\\\\034", "\\\\\\\\035", "\\\\\\\\036", "\\\\\\\\037",
" ", "!", "\\\\\\\"", "#",
"$", "%", "&", "\\\\\\\\'", "(", ")", "*", "+",
",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E",
"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
"X", "Y", "Z", "[", "\\\\\\\\\\\\\\\\", "]", "^", "_", "\\\\\\\\`", "a", "b", "c",
"d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
"v", "w", "x", "y", "z", "{", "|", "}", "~", "\\\\\\\\0177"};

void artErrorCheck() {
  if (text_total_errors() > 0)
    text_message(TEXT_FATAL, "%i error%s detected\n", text_total_errors(), text_total_errors() == 1 ? "" : "s");
}

/* Start of tree utility functions ********************************************/

bool artIsNonterminalNode(rdp_tree_node_data* node, const char* label) {
  if (node == NULL) return false;

  return node->token == 0 && strcmp(node->id, label) == 0;
}

rdp_tree_node_data *artNewTreeNodeHead(rdp_tree_node_data *parent, const char* id, artKind kind, symbols_data_node * tableEntry) {
  rdp_tree_node_data* newNode = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), parent);
  newNode->id = strdup(id);
  newNode->kind = kind;
  newNode->tableEntry = tableEntry;
  graph_insert_edge(sizeof(rdp_tree_edge_data), newNode, parent);

  return newNode;
}

rdp_tree_node_data *artNewTreeNode(rdp_tree_node_data *parent, const char* id, artKind kind, symbols_data_node * tableEntry) {
  rdp_tree_node_data* newNode = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), parent);
  newNode->id = strdup(id);
  newNode->kind = kind;
  newNode->tableEntry = tableEntry;
  graph_insert_edge_after_final(sizeof(rdp_tree_edge_data), newNode, parent);

  return newNode;
}

symbols_data *artRewriteMajorName(symbols_data *symbol, symbols_data **rewriteSet, const char* rewriteMajorID) {
  if (symbol == NULL || rewriteSet == NULL)
    return symbol;

  if (symbol->kind != ART_NONTERMINAL)  // Only rewrite nonterminals
    return symbol;

  symbols_data *majorSymbol = artFind(ART_MAJOR, symbol->majorID, symbol->majorID, NULL);

  for (; *rewriteSet != NULL; rewriteSet++)
    if (*rewriteSet == majorSymbol)
      return artFind(ART_NONTERMINAL, rewriteMajorID, symbol->minorID, NULL);

  return symbol;  // No change
}

static rdp_tree_node_data *artCloneNode(rdp_tree_node_data *parent, rdp_tree_node_data *src, symbols_data *newTableEntry) {
  rdp_tree_node_data* newNode = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), parent);
  memcpy(newNode, src, sizeof (rdp_tree_node_data));
  newNode->tableEntry = newTableEntry;
  graph_insert_edge_after_final(sizeof(rdp_tree_edge_data), newNode, parent);

  return newNode;
}

static void artCloneTreeAsChildOfRec(rdp_tree_node_data *parent, rdp_tree_node_data *src,
                                     symbols_data *terminalWSSymbol,
                                     symbols_data **rewriteSet,
                                     const char* rewriteMajorID) {
  for (void *edge = graph_next_out_edge(src); edge != NULL; edge = graph_next_out_edge(edge)) {
    rdp_tree_node_data *child = (rdp_tree_node_data*) graph_destination(edge);

    if(!child->deleted) {
      artCloneTreeAsChildOfRec(artCloneNode(parent, child, artRewriteMajorName(child->tableEntry, rewriteSet, rewriteMajorID)), child, terminalWSSymbol, rewriteSet, rewriteMajorID);
      if (terminalWSSymbol != NULL && (child->kind == ART_BUILTIN_TERMINAL || child->kind == ART_CASE_INSENSITIVE_TERMINAL || child->kind == ART_CASE_SENSITIVE_TERMINAL || child->kind == ART_CHARACTER_TERMINAL)) {
        artNewTreeNode(parent, "pos", ART_POS, NULL);
        artNewTreeNode(parent, terminalWSSymbol->minorID, ART_NONTERMINAL, terminalWSSymbol);
      }
    }
  }
}

// Make a rewritten copy of src under parent, returning the root of the new tree
void artCloneTreeAsChildOf(rdp_tree_node_data *parent, rdp_tree_node_data *src,
                           symbols_data *terminalWSSymbol,
                           symbols_data **rewriteSet,
                           const char* rewriteMajorID,
                           symbols_data *leadingWrapperSymbol,
                           symbols_data *trailingWrapperSymbol
                          ) {

  if (src->deleted)
    return;

  // The src node is a special case, and we can't do rewrites in it! (Ugly, but there you go)
  rdp_tree_node_data *srcClone = artCloneNode(parent, src, artRewriteMajorName(src->tableEntry, rewriteSet, rewriteMajorID));

  if (leadingWrapperSymbol != NULL) {
    artNewTreeNode(srcClone, "pos", ART_POS, NULL);
    artNewTreeNode(srcClone, leadingWrapperSymbol->minorID, ART_NONTERMINAL, leadingWrapperSymbol);
  }
  artCloneTreeAsChildOfRec(srcClone, src, terminalWSSymbol, rewriteSet, rewriteMajorID);
  if (trailingWrapperSymbol != NULL) {
    artNewTreeNode(srcClone, trailingWrapperSymbol->minorID, ART_NONTERMINAL, trailingWrapperSymbol);
    artNewTreeNode(srcClone, "pos", ART_POS, NULL);
  }

  artFixupCatUnary(srcClone);
}

void artCloneTreeAsChildOf(rdp_tree_node_data *parent, rdp_tree_node_data *src) {
  artCloneTreeAsChildOf(parent, src, NULL, NULL, NULL, NULL, NULL);
}

void artDeleteTree(rdp_tree_node_data *src)
{
  for (rdp_tree_edge_data *productionEdge = (rdp_tree_edge_data*) graph_next_out_edge(src);
       productionEdge != NULL;
       productionEdge = (rdp_tree_edge_data*) graph_next_out_edge(productionEdge))
    artDeleteTree((rdp_tree_node_data *) graph_destination(productionEdge));

    src->deleted = 1;
}

rdp_tree_node_data *artLeftmostElementRec(rdp_tree_node_data* node) {
  rdp_tree_node_data *ret;

  if (graph_next_out_edge(node) == NULL || node->kind == ART_POS)  // Only consider down to POS nodes!
    ret = node;
  else
    ret = artLeftmostElementRec((rdp_tree_node_data*) graph_destination(graph_next_out_edge(node)));

  return ret;
}

rdp_tree_node_data *artRightmostElementRec(rdp_tree_node_data* node) {
  rdp_tree_node_data *ret;

  if (graph_next_out_edge(node) == NULL || node->kind == ART_POS)  // Only consider down to POS nodes!
    ret = node;
  else
    for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
      ret = artRightmostElementRec((rdp_tree_node_data*) graph_destination(edge));

//  text_printf("artRightmost return node %i\n", graph_atom_number(ret));
  return ret;
}

rdp_tree_node_data *artFirstChildOf(rdp_tree_node_data* node) {
  void *edge = graph_next_out_edge(node);

  if (edge == NULL)
    return NULL;

  return (rdp_tree_node_data*) graph_destination(edge);
}

/* End of tree utility functions **********************************************/

/* Start of symbol table utility functions ************************************/

bool artHasKeywords() {
  int ret_value = false;

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    ret_value |= (thisSymbol->kind == ART_CASE_SENSITIVE_TERMINAL || thisSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL);

  return ret_value;
}

static void artWriteAs(const char* string, const char* *ASCII_table) {
  if (string == NULL)
    text_printf("!!NULL in artWriteAs!!");

  for ( ; *string != '\0'; string++)
    if ((unsigned) *string > 0 && (unsigned) *string < 128)
      text_printf("%s", ASCII_table[*string]);
    else
      text_printf("_%u", (unsigned) *string);
}

static void printLabelSanitised(const char* prefix, rdp_tree_node_data *node, const char* *ASCII_table) {
  if (node->lhsL == NULL || node->lhsL->tableEntry == NULL)
    return;

  text_printf("%s", prefix);

  if (node == NULL) {
    text_printf("NULL_NODE");
    return;
  }

  artWriteAs(node->lhsL->tableEntry->majorID, ASCII_table);
  text_printf("_");
  artWriteAs(node->lhsL->tableEntry->minorID, ASCII_table);
  if (!node->isLHS)
    text_printf("_%i", node->nodeNumber);
}

static const char* ASCIITableForIdentifiers[] = {
"_NUL", "_SOH", "_STX", "_ETX", "_EOT", "_ENQ", "_ACK", "_BEL", "_BS", "_HT", "_LF", "_VT", "_FF",
"_CR", "_SO", "_SI", "_DLE", "_DC1", "_DC2", "_DC3", "_DC4", "_NAK", "_SYN", "_ETB", "_CAN",
"_EM", "_SUB", "_ESC", "_FS", "_GS", "_RS", "_", "_SPACE", "_SHREIK", "_DBLQUOTE", "_HASH",
"_DOLLAR", "_PERCENT", "_AMPERSAND", "_QUOTE", "_LPAR", "_RPAR", "_STAR", "_PLUS",
"_COMMA", "_MINUS", "_PERIOD", "_SLASH", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
"_COLON", "_SEMICOLON", "_LT", "_EQUAL", "_GT", "_QUERY", "_AT", "A", "B", "C", "D", "E",
"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
"X", "Y", "Z", "_LBRACK", "_BACKSLASH", "_RBRACK", "_UPARROW", "_US", "_BACKQUOTE", "a", "b", "c",
"d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
"v", "w", "x", "y", "z", "_LBRACE", "_BAR", "_RBRACE", "_TILDE", "_DEL"};

static const char* ASCIITableForStrings[] = {
"\\000", "\\001", "\\002", "\\003", "\\004", "\\005", "\\006", "\\007", "\\010", "\\t",   "\\n",   "\\013", "\\014", "\\r",   "\\016", "\\017",
"\\020", "\\021", "\\022", "\\023", "\\024", "\\025", "\\026", "\\027", "\\030", "\\031", "\\032", "\\033", "\\034", "\\035", "\\036", "\\037",
" ", "!", "\\\"", "#",
"$", "%", "&", "'", "(", ")", "*", "+",
",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E",
"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
"X", "Y", "Z", "[", "\\\\", "]", "^", "_", "`", "a", "b", "c",
"d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
"v", "w", "x", "y", "z", "{", "|", "}", "~", "_DEL"};

void artWriteAsID(const char* string) {artWriteAs(string, ASCIITableForIdentifiers);}
void artWriteAsString(const char* string) {artWriteAs(string, ASCIITableForStrings);}

void artWriteNodeLabelAsString(const char* prefix, rdp_tree_node_data  *node) { printLabelSanitised(prefix, node, ASCIITableForStrings); }

void artWriteSymbolAsTerminalID(symbols_data *symbol) {
  switch (symbol->kind) {
    case ART_BUILTIN_TERMINAL: text_printf("ART_TB_");  artWriteAsID(symbol->minorID); break;
    case ART_CHARACTER_TERMINAL: text_printf("ART_TC_");  artWriteAsID(symbol->minorID); break;
    case ART_CASE_SENSITIVE_TERMINAL: text_printf("ART_TS_");  artWriteAsID(symbol->minorID); break;
    case ART_CASE_INSENSITIVE_TERMINAL: text_printf("ART_TI_");  artWriteAsID(symbol->minorID); break;
    defaut: text_message(TEXT_FATAL, "internal error: call to artWriteSymbolAsTerminalID on a symbol which is not a terminal\n");
  }
}

void artWriteSymbolAsTerminalString(symbols_data *symbol) {
  switch (symbol->kind) {
    case ART_BUILTIN_TERMINAL: text_printf("ART_TB_");  artWriteAsString(symbol->minorID); break;
    case ART_CHARACTER_TERMINAL: text_printf("ART_TC_");  artWriteAsString(symbol->minorID); break;
    case ART_CASE_SENSITIVE_TERMINAL: text_printf("ART_TS_");  artWriteAsString(symbol->minorID); break;
    case ART_CASE_INSENSITIVE_TERMINAL: text_printf("ART_TI_");  artWriteAsString(symbol->minorID); break;
    defaut: text_message(TEXT_FATAL, "internal error: call to artWriteSymbolAsTerminalString on a symbol which is not a terminal\n");
  }
}

struct symbols_data_node *artLookup(enum artKind kind, const char* majorID, const char* minorID, const char* attributeID)
{
  struct compareIntegerStringStringStruct {int i; const char* s; const char* t; const char* u;} key;

  key.i = kind; key.s = majorID; key.t = minorID; key.u = attributeID;
  return (struct symbols_data_node*) symbol_lookup_key(symbols, &key, NULL);
}

struct symbols_data_node *artLookupByNumber(unsigned number)
{
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    if (thisSymbol->number == number)
      return thisSymbol;

  return NULL;
}

struct symbols_data_node *artFind(enum artKind kind, const char* majorID, const char* minorID, const char* attributeID)
{
//  printf("****artFind(%i, %s.%s)\n", kind, majorID, minorID);
  void *temp;

  if ((temp = artLookup(kind, majorID, minorID, attributeID)) == NULL)
  {
    struct compareIntegerStringStringStringStruct {int i; const char* s; const char* t; const char* u;} key;

    key.i = kind; key.s = majorID; key.t = minorID; key.u = attributeID;
    temp = symbol_insert_key(symbols, &key, sizeof(compareIntegerStringStringStringStruct), sizeof(symbols_data));
  }

  return (struct symbols_data_node*) temp;
}

void artWriteSymbols()
{
  int symbolsCount = 0;

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    symbolsCount++;

  symbols_data **symbolsIndex = new symbols_data*[symbolsCount];

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    symbolsIndex[thisSymbol->number] = thisSymbol;

  text_printf("\nSymbol table\n");
  for (int i = 0; i < symbolsCount; i++)
  {
    text_printf("\n%4i: ", symbolsIndex[i]->number);
    int width = artWriteSymbol(symbolsIndex[i]->kind, symbolsIndex[i]->minorID, symbolsIndex[i]->kind == ART_CHARACTER_TERMINAL ? (const char* ) " " : symbolsIndex[i]->majorID, symbolsIndex[i]->attributeID, symbolsIndex[i]->attributeType);
    for (int tmp = width; tmp < 27; tmp++)
      printf(" ");

    switch (symbolsIndex[i]->kind)
    {
      case ART_END_OF_STRING: text_printf("END_OF_STRING"); break;
      case ART_EPSILON: text_printf("EPSILON"); break;
      case ART_POS: text_printf("POS"); break;
      case ART_BUILTIN_TERMINAL: text_printf("BUILTIN_TERMINAL"); break;
      case ART_CHARACTER_TERMINAL: text_printf("CHARACTER_TERMINAL"); break;
      case ART_CASE_SENSITIVE_TERMINAL: text_printf("CASE_SENSITIVE_TERMINAL"); break;
      case ART_CASE_INSENSITIVE_TERMINAL: text_printf("CASE_INSENSITIVE_TERMINAL");break;
      case ART_NONTERMINAL: text_printf("NONTERMINAL"); break;
      case ART_MAJOR: text_printf("MAJOR"); break;
      case ART_POS_NAME: text_printf("POS_NAME"); break;
      case ART_ATTRIBUTE: text_printf("ATTRIBUTE"); break;

      default: break;
    }

    if (symbolsIndex[i]->kind == ART_POS_NAME) {
      text_printf("%s%s", symbolsIndex[i]->defined ? " defined" : " undefined", symbolsIndex[i]->used ? " used" : " unused");
    }

    if (symbolsIndex[i]->kind == ART_NONTERMINAL) {
      text_printf("%s%s%s%s%s%s", symbolsIndex[i]->isLexical ? " GIFT target" : "", symbolsIndex[i]->isLexical ? " lexical" : "", symbolsIndex[i]->defined ? " defined" : " undefined", symbolsIndex[i]->used ? " reachable" : " unreachable", symbolsIndex[i]->containsDelayedInstances ? " contains delayed instances" : "", symbolsIndex[i]->hasDelayedInstances ? " has delayed instances" : "");
    }

    if (symbolsIndex[i]->kind == ART_MAJOR) {
      text_printf(" %s whitespace import ", symbolsIndex[i]->defined ? "defined" : "undefined  ");
      artWriteImport(symbolsIndex[i]->whiteSpaceImport);
    }

    if (set_cardinality(&symbolsIndex[i]->dependency) >0) {
      text_printf(" dependency {");
      set_print_set(&symbolsIndex[i]->dependency, NULL, 0);
      text_printf(" }");
    }
  }
  text_printf("\n");
}

static void artWriteMergedSets() {
  text_printf("\nMerged sets\n\n");
  for (mergedSets_data *thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(symbol_get_scope(mergedSets));
       thisSet != NULL;
       thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(thisSet))
  {
     text_printf("%i: ", thisSet->number);
     set_print_set(&thisSet->set, NULL, 0);
     text_printf("\n");
  }
}

/* End of symbol table utility functions **************************************/

/* Start of text and VCG mode printing utility functions **********************/

void artRedirectToFile(const char* fileName) {
  char* fullFileName = (char* ) mem_calloc(1, 2 + strlen((char*) fileName) + strlen(artOutputDirectory));

  strcat(fullFileName, artOutputDirectory);
  strcat(fullFileName, "/");
  strcat(fullFileName, fileName);

  FILE *f = fopen(fullFileName, "w");

  if (f == NULL)
    text_message(TEXT_FATAL, "unable to open '%s' for output\n", fullFileName);

  mem_free(fullFileName);
  text_redirect(f);
}

static void artWriteSet(set_ *s) {
  unsigned *elements = set_array(s);

  text_printf("[");
  for (unsigned *el = elements; *el != SET_END; el++) {
    if (el != elements) text_printf(", ");
//    artWriteAsString(artLookupByNumber(*el)->minorID);
    artWriteSymbol(artLookupByNumber(*el)->kind, artLookupByNumber(*el)->minorID, artLookupByNumber(*el)->majorID, artLookupByNumber(*el)->attributeID, artLookupByNumber(*el)->attributeType);
  }
  text_printf("]");

  mem_free(elements);
}

static void artDotWriteGraph(const void * graph) {
text_printf("graph []\n"
            "node [ color=BLACK shape=box style=filled fillcolor=WHITE fontname=\"Courier\" fontcolor=BLACK fontsize=14]\n"
            "edge [ color=BLACK style=filled fillcolor=WHITE fontname=\"Courier\" fontcolor=BLACK fontsize=14]\n");
}

static void artVCGWriteEdge(const void * edge)
{
// This is just a kludge to shut the compiler warning messages up - comment out the next line to get
  if (graph_atom_number(edge) == 0)
    text_printf("label:\"(%i)\"", graph_atom_number(edge));  // uncomment this line to put edge numbers into the VCG output
}

static void artDotWriteEdge(const void * edge)
{
// This is just a kludge to shut the compiler warning messages up - comment out the next line to get
  if (graph_atom_number(edge) == 0)
    text_printf("[label=\"(%i)\"]", graph_atom_number(edge));  // uncomment this line to put edge numbers into the DOT output
}

int artWriteSymbol(artKind kind, const char* pattern, const char* pattern2, const char* pattern3, const char* pattern4)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_print_C_string((char*) pattern);break;
    case ART_BUILTIN_TERMINAL: width+= text_printf("&"); width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string((char*) pattern);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_string((char*) pattern); width+= text_printf("\'"); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_char((char*) pattern); width+= text_printf("\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern2); width+= text_printf("."); width+= text_print_C_string((char*) pattern); break;
    case ART_ATTRIBUTE: width+= text_print_C_string((char*) pattern2); width+= text_printf("."); width+= text_print_C_string((char*) pattern); width+= text_printf("."); width+= text_print_C_string((char*) pattern3); width+= text_printf(":"); width+= text_print_C_string((char*) pattern4); break;
  }
  return width;
}

int artWriteSymbol(symbols_data *symbol) {
  return artWriteSymbol(symbol->kind, symbol->minorID, symbol->majorID, symbol->attributeID, symbol->attributeType);
}

int artWriteSymbolAsString(artKind kind, const char* pattern, const char* pattern2, const char* pattern3, const char* pattern4)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_print_C_string((char*) pattern);break;
    case ART_BUILTIN_TERMINAL: width+= text_printf("&"); width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string((char*) pattern);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_string((char*) pattern); width+= text_printf("\'"); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\\\""); width+= text_print_C_char((char*) pattern); width+= text_printf("\\\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern2); width+= text_printf("."); width+= text_print_C_string((char*) pattern); break;
    case ART_ATTRIBUTE: width+= text_print_C_string((char*) pattern2); width+= text_printf("."); width+= text_print_C_string((char*) pattern); width+= text_printf("."); width+= text_print_C_string((char*) pattern3); width+= text_printf(":"); width+= text_print_C_string((char*) pattern4); break;
  }
  return width;
}

static void artVCGWriteNode(const void * node)
{
  rdp_tree_node_data *thisNode = (rdp_tree_node_data*) node;

  if (thisNode->deleted)
      text_printf("color:red ");

  switch (thisNode->kind) {
    case ART_ILLEGAL:
    case ART_END_OF_STRING:
      text_printf("bordercolor:red ");
      break;

    case ART_MAJOR:
    case ART_LHS:
    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
    case ART_EPSILON:
      text_printf("bordercolor:blue ");
      break;

    case ART_ROOT:
    case ART_UNARY:
    case ART_CAT:
    case ART_ALT:
    case ART_DIFF:
    case ART_ITER:
    case ART_DO_FIRST:
    case ART_OPTIONAL:
    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE:
      text_printf("bordercolor:green ");
      break;

    case ART_ANNOTATION:
    case ART_INSERTION:
      text_printf("bordercolor:orange ");
      break;

    case ART_POS:
    case ART_TEAR:
      text_printf("bordercolor:magenta ");
      break;

};

  text_printf("label:\"");
  text_printf("(%i->%i) ", graph_atom_number(thisNode), thisNode->rightSibling == NULL ? 0 : graph_atom_number(thisNode->rightSibling));  // uncomment this line to put node numbers into the VCG output

  if (thisNode->deleted)
    text_printf("(deleted) ");

  artWriteSymbolAsString(thisNode->kind, thisNode->id, thisNode->rangeUpperCharacterTerminal != NULL ? thisNode->rangeUpperCharacterTerminal->minorID : thisNode->tableEntry != NULL ? thisNode->tableEntry->majorID : (const char* ) "", NULL, NULL);

  if (thisNode->atomName != NULL)
    text_printf(":%s.%s", thisNode->atomName->majorID, thisNode->atomName->minorID);

  if (thisNode->gatherName != NULL)
    text_printf("!%s.%s", thisNode->gatherName->majorID, thisNode->gatherName->minorID);

  switch (thisNode->fold)
  {
    case ART_FOLD_EMPTY: break;
    case ART_FOLD_NONE: text_printf("^_"); break;
    case ART_FOLD_UNDER: text_printf("^"); break;
    case ART_FOLD_OVER: text_printf("^^"); break;
    case ART_FOLD_TEAR: text_printf(">"); break;
    case ART_FOLD_TEAR_PARENT: text_printf(">>"); break;
    default: text_message(TEXT_FATAL, "unknown fold kind found in tree"); break;
  }

  if (thisNode->kind == ART_TEAR)
    text_printf("->%i", graph_atom_number(thisNode->tearLink));

    artWriteNodeLabelAsString("\\nlabel: ", thisNode);

  if (thisNode->isPosParentLabel || thisNode->isCodeLabel || thisNode->isSPPFLabel || thisNode->isReferredLabel || thisNode->isDelayed || thisNode->isGiftLabel || thisNode->isLHS ||
      thisNode->isNullableBracket || thisNode->isEoOP || thisNode->isEoA || thisNode->isPopD || thisNode->isPredictivePop || thisNode->isPostPredictivePop || thisNode->isEoD ||
      thisNode->isEoO || thisNode->isEoP || thisNode->isEoK || thisNode->isEoR || thisNode->isFiR|| thisNode->isFiPC || thisNode->isColon) {
    text_printf("\\nattr: ");

    if (thisNode->isPosParentLabel)
      text_printf("isPosParentLabel ");

    if (thisNode->isPosSelector)
      text_printf("isPosSelector ");

    if (thisNode->isCodeLabel)
      text_printf("isCodeLabel ");

    if (thisNode->isSPPFLabel)
      text_printf("isSPPFLabel ");

    if (thisNode->isReferredLabel)
      text_printf("isReferredLabel ");

    if (thisNode->isTestRepeatLabel)
      text_printf("isTestRepeatLabel ");

    if (thisNode->isGiftLabel)
      text_printf("isGiftLabel ");

    if (thisNode->isDelayed)
      text_printf("isDelayed ");

    if (thisNode->isLHS)
      text_printf("isLHS ");

    if (thisNode->isNullableBracket)
      text_printf("isNullableBracket ");

    if (thisNode->isEoOP)
      text_printf("isEoOP ");
    if (thisNode->isPopD)
      text_printf("isPopD ");
    if (thisNode->isPredictivePop)
      text_printf("isPredictivePop ");
    if (thisNode->isPostPredictivePop)
      text_printf("isPostPredictivePop ");
    if (thisNode->isEoA)
      text_printf("isEoA ");
    if (thisNode->isEoD)
      text_printf("isEoD ");
    if (thisNode->isEoO)
      text_printf("isEoO ");
    if (thisNode->isEoP)
      text_printf("isEoP ");
    if (thisNode->isEoK)
      text_printf("isEoK ");
    if (thisNode->isEoR)
      text_printf("isEoR ");
    if (thisNode->isFiR)
      text_printf("isFir ");
    if (thisNode->isFiPC)
      text_printf("isFiPC ");
    if (thisNode->isColon)
      text_printf("isColon ");
  }

  if (thisNode->lhsL != NULL && thisNode->lhsL != thisNode)
    artWriteNodeLabelAsString("\\nLHS: ", thisNode->lhsL);
  if (thisNode->niL != NULL && thisNode->niL != thisNode)
    artWriteNodeLabelAsString("\\nniL: ", thisNode->niL);
  if (thisNode->aL != NULL && thisNode->aL != thisNode)
    artWriteNodeLabelAsString("\\naL: ", thisNode->aL);
  if (thisNode->nL != NULL && thisNode->nL != thisNode)
    artWriteNodeLabelAsString("\\nnL: ", thisNode->nL);
  if (thisNode->pL != NULL && thisNode->pL != thisNode)
    artWriteNodeLabelAsString("\\npL: ", thisNode->pL);
  if (thisNode->lrL != NULL && thisNode->lrL != thisNode)
    artWriteNodeLabelAsString("\\nlrL: ", thisNode->lrL);
  if (thisNode->erL != NULL && thisNode->erL != thisNode && thisNode->erL != NULL)
    artWriteNodeLabelAsString("\\nerL: ", thisNode->erL);

  if (set_cardinality(&thisNode->first) != 0) {
    text_printf("\\nfirst: ");
    if (artMergedSet(&thisNode->first))
      text_printf("[%i] ", artMergedSetNumber(&thisNode->first));
    artWriteSet(&thisNode->first);
  }

  if (set_cardinality(&thisNode->follow) != 0) {
    text_printf("\\nfollow: ");
    if (artMergedSet(&thisNode->follow))
      text_printf("[%i] ", artMergedSetNumber(&thisNode->follow));
    artWriteSet(&thisNode->follow);
  }

  if (set_cardinality(&thisNode->guard) != 0) {
    text_printf("\\nguard: ");
    if (set_cardinality(&thisNode->guard) != 0)
      text_printf("[%i] ", artMergedSetNumber(&thisNode->guard));
    artWriteSet(&thisNode->guard);
  }

  text_printf("\"");
  text_printf(" horizontal_order:%lu", graph_atom_number(node));
}

static void artDotWriteNode(const void * node)
{
  rdp_tree_node_data *thisNode = (rdp_tree_node_data*) node;

  text_printf("[ ");
  if (thisNode->deleted)
      text_printf("color=red ");

  switch (thisNode->kind) {
    case ART_ILLEGAL:
    case ART_END_OF_STRING:
      text_printf("bordercolor=red ");
      break;

    case ART_MAJOR:
    case ART_LHS:
    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
    case ART_EPSILON:
      text_printf("bordercolor=blue ");
      break;

    case ART_ROOT:
    case ART_UNARY:
    case ART_CAT:
    case ART_ALT:
    case ART_DIFF:
    case ART_ITER:
    case ART_DO_FIRST:
    case ART_OPTIONAL:
    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE:
      text_printf("bordercolor=green ");
      break;

    case ART_ANNOTATION:
    case ART_INSERTION:
      text_printf("bordercolor=orange ");
      break;

    case ART_POS:
    case ART_TEAR:
      text_printf("bordercolor=magenta ");
      break;

};

  text_printf("label=\"");
  text_printf("(%i->%i) ", graph_atom_number(thisNode), thisNode->rightSibling == NULL ? 0 : graph_atom_number(thisNode->rightSibling));  // uncomment this line to put node numbers into the DOT output

  if (thisNode->deleted)
    text_printf("(deleted) ");

  artWriteSymbolAsString(thisNode->kind, thisNode->id, thisNode->rangeUpperCharacterTerminal != NULL ? thisNode->rangeUpperCharacterTerminal->minorID : thisNode->tableEntry != NULL ? thisNode->tableEntry->majorID : (const char* ) "", NULL, NULL);

  if (thisNode->atomName != NULL)
    text_printf(":%s.%s", thisNode->atomName->majorID, thisNode->atomName->minorID);

  if (thisNode->gatherName != NULL)
    text_printf("!%s.%s", thisNode->gatherName->majorID, thisNode->gatherName->minorID);

  switch (thisNode->fold)
  {
    case ART_FOLD_EMPTY: break;
    case ART_FOLD_NONE: text_printf("^_"); break;
    case ART_FOLD_UNDER: text_printf("^"); break;
    case ART_FOLD_OVER: text_printf("^^"); break;
    case ART_FOLD_TEAR: text_printf(">"); break;
    case ART_FOLD_TEAR_PARENT: text_printf(">>"); break;
    default: text_message(TEXT_FATAL, "unknown fold kind found in tree"); break;
  }

  if (thisNode->kind == ART_TEAR)
    text_printf("->%i", graph_atom_number(thisNode->tearLink));

  if (thisNode->instanceNumber != 0)
    text_printf(" [%i]", thisNode->instanceNumber);

  artWriteNodeLabelAsString("\\llabel: ", thisNode);


  if (thisNode->isPosParentLabel || thisNode->isCodeLabel || thisNode->isSPPFLabel || thisNode->isReferredLabel || thisNode->isDelayed || thisNode->isGiftLabel || thisNode->isLHS ||
      thisNode->isNullableBracket || thisNode->isEoOP || thisNode->isEoA || thisNode->isPopD || thisNode->isPredictivePop || thisNode->isPostPredictivePop || thisNode->isEoD || thisNode->isEoO ||
      thisNode->isEoP || thisNode->isEoK || thisNode->isEoR || thisNode->isFiR|| thisNode->isFiPC || thisNode->isColon) {
    text_printf("\\lattr: ");

    if (thisNode->isPosParentLabel)
      text_printf("isPosParentLabel ");

    if (thisNode->isPosSelector)
      text_printf("isPosSelector ");

    if (thisNode->isCodeLabel)
      text_printf("isCodeLabel ");

    if (thisNode->isSPPFLabel)
      text_printf("isSPPFLabel ");

    if (thisNode->isReferredLabel)
      text_printf("isReferredLabel ");

    if (thisNode->isTestRepeatLabel)
      text_printf("isTestRepeatLabel ");

    if (thisNode->isGiftLabel)
      text_printf("isGiftLabel ");

    if (thisNode->isDelayed)
      text_printf("isDelayed ");

    if (thisNode->isLHS)
      text_printf("isLHS ");

    if (thisNode->isNullableBracket)
      text_printf("isNullableBracket ");

    if (thisNode->isEoOP)
      text_printf("isEoOP ");
    if (thisNode->isPopD)
      text_printf("isPopD ");
    if (thisNode->isPredictivePop)
      text_printf("isPredictivePop ");
    if (thisNode->isPostPredictivePop)
      text_printf("isPostPredictivePop ");
    if (thisNode->isEoA)
      text_printf("isEoA ");
    if (thisNode->isEoD)
      text_printf("isEoD ");
    if (thisNode->isEoO)
      text_printf("isEoO ");
    if (thisNode->isEoP)
      text_printf("isEoP ");
    if (thisNode->isEoK)
      text_printf("isEoK ");
    if (thisNode->isEoR)
      text_printf("isEoR ");
    if (thisNode->isFiR)
      text_printf("isFiR ");
    if (thisNode->isFiPC)
      text_printf("isFiPC ");
    if (thisNode->isColon)
      text_printf("isColon ");
  }

  if (thisNode->lhsL != NULL && thisNode->lhsL != thisNode)
    artWriteNodeLabelAsString("\\lLHS: ", thisNode->lhsL);
  if (thisNode->niL != NULL && thisNode->niL != thisNode)
    artWriteNodeLabelAsString("\\lniL: ", thisNode->niL);
  if (thisNode->aL != NULL && thisNode->aL != thisNode)
    artWriteNodeLabelAsString("\\laL: ", thisNode->aL);
  if (thisNode->nL != NULL && thisNode->nL != thisNode)
    artWriteNodeLabelAsString("\\lnL: ", thisNode->nL);
  if (thisNode->pL != NULL && thisNode->pL != thisNode)
    artWriteNodeLabelAsString("\\lpL: ", thisNode->pL);
  if (thisNode->lrL != NULL && thisNode->lrL != thisNode)
    artWriteNodeLabelAsString("\\llrL: ", thisNode->lrL);
  if (thisNode->erL != NULL && thisNode->erL != thisNode && thisNode->erL != NULL)
    artWriteNodeLabelAsString("\\lerL: ", thisNode->erL);

  if (set_cardinality(&thisNode->first) != 0) {
    text_printf("\\lfirst: ");
    if (artMergedSet(&thisNode->first))
      text_printf("[%i] ", artMergedSetNumber(&thisNode->first));
    artWriteSet(&thisNode->first);
  }

  if (set_cardinality(&thisNode->follow) != 0) {
    text_printf("\\lfollow: ");
    if (artMergedSet(&thisNode->follow))
      text_printf("[%i] ", artMergedSetNumber(&thisNode->follow));
    artWriteSet(&thisNode->follow);
  }

  if (set_cardinality(&thisNode->guard) != 0) {
    text_printf("\\lguard: ");
    if (set_cardinality(&thisNode->guard) != 0)
      text_printf("[%i] ", artMergedSetNumber(&thisNode->guard));
    artWriteSet(&thisNode->guard);
  }

  text_printf("\\l\"");
  text_printf(" horizontal_order=%lu", graph_atom_number(node));
  text_printf("] ");
}

void artVCGRDT(const char* filename, void *rdp_tree)
{
  FILE *f = fopen(filename, "w");
  if (f== NULL)
    text_message(TEXT_FATAL, "unable to open %s for write\n", filename);
  text_redirect(f);
  graph_vcg(rdp_tree, NULL, artVCGWriteNode, artVCGWriteEdge);
  fclose(f);
  text_redirect(stdout);
}

void artDotRDT(const char* filename, void *rdp_tree)
{
  FILE *f = fopen(filename, "w");
  if (f== NULL)
    text_message(TEXT_FATAL, "unable to open %s for write\n", filename);
  text_redirect(f);
  graph_dot(rdp_tree, artDotWriteGraph, artDotWriteNode, artDotWriteEdge);
  fclose(f);
  text_redirect(stdout);
}

static rdp_tree_node_data* lastLHSNode = NULL;
void artWriteGrammarRec(rdp_tree_node_data *node, int level) {
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

    case ART_DIFF: text_printf(" / "); break;

    case ART_UNARY:
    case ART_CAT:
      break;

    case ART_LHS: artWriteSymbol(node->kind, node->tableEntry->minorID, node->tableEntry->majorID, NULL, NULL);
      if (node->tableEntry->attributes != NULL) {
        text_printf(" <");
        for (symbols_list *tmp = node->tableEntry->attributes; tmp != NULL; tmp = tmp->next)
          text_printf(" %s:%s", tmp->symbol->attributeID, tmp->symbol->attributeType);
        text_printf(" >");
      }
    text_printf(" ::=\n  "); lastLHSNode = node; break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
//      artWriteSet(&node->guard);
      text_printf(" "); artWriteSymbol(node->kind, node->tableEntry->minorID, node->rangeUpperCharacterTerminal != NULL ? node->rangeUpperCharacterTerminal->minorID : node->tableEntry->majorID, NULL, NULL);
      break;

    case ART_EPSILON:
//      artWriteSet(&node->follow);
      text_printf(" #"); break;

    case ART_DO_FIRST:
    case ART_OPTIONAL:
    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE: text_printf(" ("); break;

    case ART_POS: /* text_printf(" ."); */ break;
    case ART_ANNOTATION: text_printf(" {"); break;
    case ART_ANNOTATION_VALUE: text_printf(" %s", node->id); break;
    case ART_INSERTION: text_printf(" ["); break;
    case ART_TEAR: text_printf(" $"); break;
    default:
    text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
    // Inorder actions
    if (graph_next_out_edge(edge) != NULL)
      switch (node->kind) {
        case ART_DIFF: text_printf(" / "); break;
        case ART_ITER: text_printf(" @ "); break;
        case ART_ALT: text_printf(" | "); break;
        case ART_LHS: text_printf(" |\n  "); break;
        default: break;
      };
  }

  //Postorder actions
  switch (node->kind) {
    case ART_LHS: text_printf(" ;\n\n"); break;
    case ART_DO_FIRST: text_printf(" )"); break;
    case ART_OPTIONAL: text_printf(" )?"); break;
    case ART_POSITIVE_CLOSURE: text_printf(" )+"); break;
    case ART_KLEENE_CLOSURE: text_printf(" )*"); break;

    case ART_ANNOTATION: text_printf(" } "); break;
    case ART_INSERTION: text_printf(" ]"); break;
    case ART_CAT: case ART_UNARY: case ART_EPSILON:
      break;
    default: break;
  };
}

static void artWriteGrammar(bool usePPrime, bool showHistograms) {
  printf("\nGrammar: start symbol ");
  artWriteSymbol(artStartSymbol->kind, artStartSymbol->minorID, artStartSymbol->majorID, NULL, NULL);
  printf("\n\n");
  int nonterminalCount = 0, productionCount = 0, longestProductionLength = 0;
  histogram_node *productionsPerNonterminal;
  hist_init(&productionsPerNonterminal);
  histogram_node *elementsPerProduction;
  hist_init(&elementsPerProduction);

  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol)) {
    rdp_tree_node_data *lhsNode = usePPrime ? thisSymbol->PPrimeNode : thisSymbol->iftNode;

    if (thisSymbol->kind == ART_NONTERMINAL) {
      if (lhsNode == NULL) {
        if (!thisSymbol->used)
          text_printf("(* Unreachable *) ");
        else
          text_printf("(* Reachable *) ");
        artWriteSymbol(thisSymbol->kind, thisSymbol->minorID, thisSymbol->majorID, NULL, NULL); text_printf(" ::= (* undefined *) ;\n\n");
        continue;
      }

      artWriteGrammarRec(lhsNode, 1);

      unsigned long productionsThisNonterminal = 0;
      nonterminalCount++;

      for (void *edge = graph_next_out_edge(lhsNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        productionCount++;
        productionsThisNonterminal++;

        int length = 0;
        for (void *subedge = graph_next_out_edge(graph_destination(edge)); subedge != NULL; subedge = graph_next_out_edge(subedge))
          if (((rdp_tree_node_data*) graph_destination(subedge))->kind != ART_POS)
            length++;

        hist_update(elementsPerProduction, length);
        if (length > longestProductionLength)
          longestProductionLength = length;
      }
      hist_update(productionsPerNonterminal, productionsThisNonterminal);
    }
  }

  printf("\nGrammar has %i active nonterminal%s, %i production%s, longest production has %i element%s\n",
         nonterminalCount, nonterminalCount == 1 ? "" : "s",
         productionCount, productionCount == 1 ? "" : "s",
         longestProductionLength, longestProductionLength == 1 ? "" : "s"
        );

  if (showHistograms) {
    printf("\nProductions per nonterminal histogram\n");
    hist_print(productionsPerNonterminal);

    printf("\nElements per production histogram\n");
    hist_print(elementsPerProduction);
  }
}

void artPrintIFTKind(enum artKind kind) {
  switch (kind) {
  case ART_ILLEGAL: text_printf("ILLEGAL"); break;
  case ART_END_OF_STRING: text_printf("eos"); break;
  case ART_ROOT: text_printf("root"); break;
  case ART_MAJOR: text_printf("major"); break;
  case ART_PRODUCTION: text_printf("production"); break;
  case ART_EPSILON: text_printf("#"); break;
  case ART_BUILTIN_TERMINAL: text_printf("terminal"); break;
  case ART_CHARACTER_TERMINAL: text_printf("terminal"); break;
  case ART_CASE_SENSITIVE_TERMINAL: text_printf("terminal"); break;
  case ART_CASE_INSENSITIVE_TERMINAL: text_printf("terminal"); break;
  case ART_NONTERMINAL: text_printf("nonterminal"); break;
  case ART_LHS: text_printf("lhs"); break;
  case ART_UNARY: text_printf("cat"); break;
  case ART_CAT: text_printf("cat"); break;
  case ART_ALT: text_printf("alt"); break;
  case ART_DIFF: text_printf("/"); break;
  case ART_ITER: text_printf("@"); break;
  case ART_TEAR: text_printf("tear"); break;
  case ART_DO_FIRST: text_printf("("); break;
  case ART_OPTIONAL: text_printf("?"); break;
  case ART_POSITIVE_CLOSURE: text_printf("+"); break;
  case ART_KLEENE_CLOSURE: text_printf("*"); break;
  case ART_POS: text_printf("slot"); break;
  case ART_ANNOTATION: text_printf("action"); break;
  case ART_ANNOTATION_VALUE: text_printf("actionvalue"); break;
  case ART_INSERTION: text_printf("insertion"); break;
  case ART_POS_NAME: text_printf("name"); break;
  case ART_ATTRIBUTE: text_printf("attribute"); break;

  default: text_message(TEXT_FATAL, "unexpected Kind in artPrintIFTKind())"); break;
  }
}

void artPrintFoldKind(enum artFold kind) {
  switch (kind) {
  case ART_FOLD_EMPTY: text_printf("EMPTY"); break;
  case ART_FOLD_NONE: text_printf("NONE"); break;
  case ART_FOLD_UNDER: text_printf("UNDER"); break;
  case ART_FOLD_OVER: text_printf("OVER"); break;
  case ART_FOLD_TEAR: text_printf("TEAR"); break;
  case ART_FOLD_TEAR_PARENT: text_printf("TEAR_PARENT"); break;
  default: text_message(TEXT_FATAL, "unexpected Kind in artPrintFoldKind())"); break;
  }
}

void artDumpIFTRec(rdp_tree_node_data *thisNode, int level) {
  text_printf("(%i\n", level);
  if (level != 0) { // Roots are different in V2 and V3, so just suppress
    text_printf("%i (%i): ", thisNode->nodeNumber, thisNode->instanceNumber);
    artPrintIFTKind(thisNode->kind);
    text_printf(" ");
#if 0
    if (thisNode->isLHS)
      ;
    else
#endif
      artWriteNodeLabelAsString("", thisNode);

    if (thisNode->kind == ART_ANNOTATION_VALUE) text_printf("%s", thisNode->id);
    if (thisNode->kind == ART_LHS || thisNode->kind == ART_NONTERMINAL)
      text_printf(" %s.%s", thisNode->tableEntry->majorID, thisNode->tableEntry->minorID);
    else if (thisNode->kind == ART_CASE_SENSITIVE_TERMINAL || thisNode->kind == ART_CASE_INSENSITIVE_TERMINAL || thisNode->kind == ART_CHARACTER_TERMINAL || thisNode->kind == ART_BUILTIN_TERMINAL) {
      text_printf(" ");
      artWriteSymbol(thisNode->kind, thisNode->tableEntry->minorID, NULL, NULL, NULL);
    }

  switch (thisNode->fold)
  {
    case ART_FOLD_EMPTY: break;
    case ART_FOLD_NONE: text_printf("^_"); break;
    case ART_FOLD_UNDER: text_printf("^"); break;
    case ART_FOLD_OVER: text_printf("^^"); break;
    case ART_FOLD_TEAR: text_printf("^^^"); break;
    case ART_FOLD_TEAR_PARENT: text_printf("^^^P"); break;
    default: text_message(TEXT_FATAL, "unknown fold kind found in tree"); break;
  }


     text_printf("\n");

    if (thisNode->isLHS) text_printf("isLHS ");
    if (thisNode->isDelayed) text_printf("isDelayed ");
    if (thisNode->isSPPFLabel) text_printf("isSPPFLabel ");
    if (thisNode->isCodeLabel) text_printf("isCodeLabel ");
    if (thisNode->isTestRepeatLabel) text_printf("isTestRepeatLabel ");
    if (thisNode->isAltLabel) text_printf("isAltLabel ");
    if (thisNode->isRELabel) text_printf("isRELabel ");
    if (thisNode->isReferredLabel) text_printf("isReferredLabel ");
    if (thisNode->isClosureLabel) text_printf("isClosureLabel ");
    if (thisNode->isGiftLabel) text_printf("isGiftLabel ");
    if (thisNode->isPosSelector) text_printf("isSlotSelector ");
    if (thisNode->isPosParentLabel) text_printf("isSlotParentLabel ");
    if (thisNode->isPopD) text_printf("isPopD ");
    if (thisNode->isPostPredictivePop) text_printf("isPostPredictivePop ");
    if (thisNode->isPredictivePop) text_printf("isPredictivePop ");

    if (thisNode->isNullableBracket) text_printf("isNullableBracket ");
    if (thisNode->isColon) text_printf("isColon ");
    if (thisNode->isEoOP) text_printf("isEoOP ");
    if (thisNode->isEoA) text_printf("isEoA ");
    if (thisNode->isEoR) text_printf("isEoR ");
    if (thisNode->isFiR) text_printf("isFiR ");
    if (thisNode->isRELabel) text_printf("isRELabel ");
    if (thisNode->isFiPC) text_printf("isFiPC ");
    if (thisNode->isEoD) text_printf("isEoD ");
    if (thisNode->isEoO) text_printf("isEoO ");
    if (thisNode->isEoP) text_printf("isEoP ");
    if (thisNode->isEoK) text_printf("isEoK ");
    text_printf("\n");

    if (thisNode->lhsL != NULL && thisNode->lhsL != thisNode) { artWriteNodeLabelAsString("lhsL: ", thisNode->lhsL); text_printf("\n"); }
    if (thisNode->niL != NULL && thisNode->niL != thisNode) { artWriteNodeLabelAsString("niL: ", thisNode->niL);  text_printf("\n"); }
    if (thisNode->aL != NULL && thisNode->aL != thisNode) { artWriteNodeLabelAsString("aL: ", thisNode->aL); text_printf("\n"); }
    if (thisNode->nL != NULL && thisNode->nL != thisNode) { artWriteNodeLabelAsString("nL: ", thisNode->nL); text_printf("\n"); }
    if (thisNode->pL != NULL && thisNode->pL != thisNode) { artWriteNodeLabelAsString("pL: ", thisNode->pL); text_printf("\n"); }
    if (thisNode->lrL != NULL && thisNode->lrL != thisNode) { artWriteNodeLabelAsString("lrL: ", thisNode->lrL); text_printf("\n"); }
    if (thisNode->erL != NULL && thisNode->erL != thisNode) { artWriteNodeLabelAsString("erL: ", thisNode->erL); text_printf("\n"); }

    if (set_cardinality(&thisNode->first) != 0) { text_printf("first: "); artWriteSet(&thisNode->first); text_printf("\n"); }
    if (set_cardinality(&thisNode->follow) != 0) { text_printf("follow: "); artWriteSet(&thisNode->follow); text_printf("\n"); }
    if (set_cardinality(&thisNode->guard) != 0) { text_printf("guard: "); artWriteSet(&thisNode->guard); text_printf("\n"); }
  }
  for (void *edge = graph_next_out_edge(thisNode); edge != NULL; edge = graph_next_out_edge(edge))
    artDumpIFTRec((rdp_tree_node_data*) graph_destination(edge), level + 1);

  text_printf("%i)\n", level);
}
void artDumpSymbols(enum artKind kind) {
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol)) {
    if (thisSymbol->kind != kind) continue;

    text_printf("**Symbol %i\n", thisSymbol->number);
    artPrintIFTKind(thisSymbol->kind);
    text_printf("\n%s\n%s\n%s\n", thisSymbol->majorID, thisSymbol->minorID, thisSymbol->attributeID);
    // print attributes
    if (thisSymbol->hasDelayedInstances)
      text_printf("hasDelayedInstances\n");

    if (thisSymbol->containsDelayedInstances)
      text_printf("containsDelayedInstances\n");

    if (thisSymbol->isGatherTarget)
      text_printf("isGatherTarget\n");

    for (struct symbols_list_node *thisAttribute = thisSymbol->attributes; thisAttribute != NULL; thisAttribute = thisAttribute->next)
      text_printf("** hasAttribute %s:%s\n", thisAttribute->symbol->attributeType, thisAttribute->symbol->attributeID);
  }
}

void artDumpIFT(const char *filename, void *ift) {
  FILE *f = fopen(filename, "w");

  if (f == NULL)
    text_message(TEXT_FATAL, "unable to open '%s' for output\n", filename);

  text_redirect(f);

  // Print symbols
  text_printf("(Symbols start\n");
  artDumpSymbols(ART_END_OF_STRING);
  artDumpSymbols(ART_CHARACTER_TERMINAL);
  artDumpSymbols(ART_CASE_SENSITIVE_TERMINAL);
  artDumpSymbols(ART_CASE_INSENSITIVE_TERMINAL);
  artDumpSymbols(ART_BUILTIN_TERMINAL);
  artDumpSymbols(ART_EPSILON);
  artDumpSymbols(ART_NONTERMINAL);
  text_printf("Symbols end)\n");

  // Suppress for comparisons with V3
#if 0
  // Print merged sets
  text_printf("(Merged sets start\n");
  for (mergedSets_data *thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(symbol_get_scope(mergedSets));
     thisSet != NULL;
     thisSet = (mergedSets_data*) symbol_next_symbol_in_scope(thisSet)) {
    text_printf("%i %i ( ", thisSet->number, thisSet->usedInParser ? 1 : 0);

    unsigned *elements = set_array(&thisSet->set);
    for (unsigned *temp = elements; *temp < SET_END; temp++)
      text_printf("%i ", *temp);

    mem_free(elements);
    text_printf(")\n");
  }

  text_printf("Merged sets end)\n");
  #endif
  for (symbols_data *lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
   lhsSymbol != NULL;
   lhsSymbol = (symbols_data*) symbol_next_symbol_in_scope(lhsSymbol))
    if (lhsSymbol->kind == ART_NONTERMINAL && lhsSymbol->iftNode != NULL)
      artDumpIFTRec(lhsSymbol->iftNode, 1);

  text_redirect(stdout);
}

/* End of text and VCG mode printing utility functions **********************/
static int terminalInstanceNumber;
static int nodeNumber;

static void artSetNodeNumberRec(rdp_tree_node_data* node) {
  node->nodeNumber = nodeNumber++;;

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artSetNodeNumberRec((rdp_tree_node_data*) graph_destination(edge));
}

void artSetInstanceNumberRec(symbols_data *symbol, rdp_tree_node_data* node) {

  if (node->isDelayed) {
    symbol->containsDelayedInstances = true;
    node->tableEntry->hasDelayedInstances = true;
  }

  switch (node->kind) {
    case ART_NONTERMINAL:
      node->instanceNumber = node->tableEntry->nextInstanceNumber++;
      break;
    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
      node->instanceNumber = terminalInstanceNumber++;
    break;
  }
  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge))
    artSetInstanceNumberRec(symbol, (rdp_tree_node_data*) graph_destination(edge));
}

void artPostParse(void* RDT)
{
  // Called with an RDP generated parser rewritten Derivation Tree in paramater RDT - see artparse.bnf for specification

  // Phase 0: parse
  // Phase 1: output RDT, semantic check command line options, check for missing main header, initialise symbol tables
  // Phase 2: set kinds
  // Phase 3: induce a grammar from the specification
  // Phase 4: remove @ operators; rewrite *, + and ?; check for folds under closures
  // Phase 5: (optionally) rewrite positive and Kleene closures to their left or right recursive equivalents
  // Phase 6: (optionally) recursively multiply out all optional and parentheses - leave closures and their subtree alone
  // Phase 7: (optionally) textually left factor using a Trie into the factorised tree
  // Phase 8: connect %X tear operators within insertions to the corresponding ^^^ operator
  // Phase 9: make a clean copy of the tree with no deleted nodes
  // Phase 10: compute attributes into the resolved tree, yielding the Intermediate Form Tree (IFT)
  // Phase 11: output GDG and MDG - these run from the symbol table so no need to specify a tree
  // Phase 12: output parsers
  // Phase 13: output TTG

  // Phase 1: output RDT, semantic check command line options, check for missing main header, initialise symbol tables
  if (artVersion) {
    printf("%i.%i.%i.%s\n", ARTMAJOR, ARTMINOR, ARTBUILD, ARTSTATUS);
    exit(0);
  }

  if (artVerbose) artVCGRDT("artrdtraw.vcg", RDT);
  if (artVerbose) artDotRDT("artrdtraw.dot", RDT);

  // Set dynamic defaults
  if (strcmp(artDespatchModeString, "") == 0) {
    if (strcmp(artTargetLanguageString, "Java") == 0)
      artDespatchModeString = "fragment";
    else
      artDespatchModeString = "static";
  }

  // Semantic checks on command line options
  if (*artOutputDirectory == 0) arg_help("empty output directory specified: do not leave a space after -o option\n");

  if (strcmp(artDespatchModeString, "dynamic") == 0) artDespatchMode = ART_DESPATCH_DYNAMIC;
  else if (strcmp(artDespatchModeString, "static") == 0) artDespatchMode = ART_DESPATCH_STATIC;
  else if (strcmp(artDespatchModeString, "state") == 0) artDespatchMode = ART_DESPATCH_STATE;
  else if (strcmp(artDespatchModeString, "fragment") == 0) artDespatchMode = ART_DESPATCH_FRAGMENT;
  else arg_help("unknown despatch mode\n");

  if (strcmp(artSupportModeString, "BigFastSimple") == 0) artSupportMode = ART_SUPPORT_BFS;
  else if (strcmp(artSupportModeString, "HashPool") == 0) artSupportMode = ART_SUPPORT_HP;
  else if (strcmp(artSupportModeString, "ObjectOriented") == 0) artSupportMode = ART_SUPPORT_OO;
  else arg_help("unknown support model\n");

  if (strcmp(artTargetLanguageString, "C++") == 0) artTargetLanguage = ART_LANGUAGE_CPP;
  else if (strcmp(artTargetLanguageString, "Java") == 0) artTargetLanguage = ART_LANGUAGE_JAVA;
  else arg_help("unknown target language\n");

       if (strcmp(artGrammarFormatString, "antlr") == 0) artGrammarFormat = ART_GRAMMAR_ANTLR;
  else if (strcmp(artGrammarFormatString, "art") == 0) artGrammarFormat = ART_GRAMMAR_ART;
  else if (strcmp(artGrammarFormatString, "bison") == 0) artGrammarFormat = ART_GRAMMAR_BISON;
  else if (strcmp(artGrammarFormatString, "dparser") == 0) artGrammarFormat = ART_GRAMMAR_DPARSER;
  else if (strcmp(artGrammarFormatString, "elkhound") == 0) artGrammarFormat = ART_GRAMMAR_ELKHOUND;
  else if (strcmp(artGrammarFormatString, "gtb") == 0) artGrammarFormat = ART_GRAMMAR_GTB;
  else if (strcmp(artGrammarFormatString, "rascal") == 0) artGrammarFormat = ART_GRAMMAR_RASCAL;
  else if (strcmp(artGrammarFormatString, "rats") == 0) artGrammarFormat = ART_GRAMMAR_RATS;
  else if (strcmp(artGrammarFormatString, "rdp") == 0) artGrammarFormat = ART_GRAMMAR_RDP;
  else if (strcmp(artGrammarFormatString, "sdf") == 0) artGrammarFormat = ART_GRAMMAR_SDF;
  else if (strcmp(artGrammarFormatString, "stratego") == 0) artGrammarFormat = ART_GRAMMAR_STRATEGO;
  else if (strcmp(artGrammarFormatString, "") == 0) artGrammarFormat = ART_GRAMMAR_NONE;
  else arg_help("unknown grammar output format\n");

  if (artClosureLeft && artClosureRight)
    arg_help("cannot rewrite closures using both left and right recursion\n");

  if (artMGLL && artClusteredGSS)
    arg_help("MGLL and RGLL incompatible: MGLL cannot yet run in clustered mode\n");

  // Test for missing initial header - first nonlexical should be major
  rdp_tree_node_data *initialKindNode;
  for (void *edge = graph_next_out_edge(graph_root(RDT)); edge != NULL; edge = graph_next_out_edge(edge)) {
    initialKindNode = (rdp_tree_node_data *) graph_destination(edge);
    if (strcmp(initialKindNode->id, "lexical") !=0  && strcmp(initialKindNode->id, "prelude") !=0&& strcmp(initialKindNode->id, "support") !=0)
      break;
  }

  if (strcmp(initialKindNode->id, "major") != 0) {
    rdp_tree_node_data *initialIDNode = artFirstChildOf(initialKindNode);

//    text_message(TEXT_WARNING, "production for nonterminal '%s' found with no preceding header: creating default major name\n", initialIDNode->id);
    rdp_tree_node_data* majorNode = artNewTreeNodeHead((rdp_tree_node_data*) graph_root(RDT), "major", ART_MAJOR, NULL);

    artNewTreeNode(majorNode, "ART", ART_ILLEGAL, NULL);
    artNewTreeNode(majorNode, "whitespace", ART_ILLEGAL, NULL);
    artNewTreeNode(majorNode, "imports", ART_ILLEGAL, NULL);
    rdp_tree_node_data* startNode = artNewTreeNode(majorNode, "start", ART_ILLEGAL, NULL);
    artNewTreeNode(startNode, initialIDNode->id, ART_ILLEGAL, NULL);

    artNewTreeNode(majorNode, "options", ART_ILLEGAL, NULL);
  }

  symbols = symbol_new_table("symbols", 1013, 1009, symbol_compare_integer_string_string_string, symbol_hash_integer_string_string_string, symbol_print_integer_string_string_string);
  artEpsilonSymbol = artFind(ART_EPSILON, "", "#", NULL);
  artEndOfStringSymbol = artFind(ART_END_OF_STRING, "", "$", NULL);
  mergedSets = symbol_new_table("mergedSets", 1013, 1009, symbol_compare_set, symbol_hash_set, symbol_print_set);
  if (artVerbose) artVCGRDT("art_rdtprecooked.vcg", RDT);
  if (artVerbose) artDotRDT("art_rdtprecooked.dot", RDT);

  // Phase 2: set kinds
  artCookRDT(RDT);
  if (artVerbose) artVCGRDT("art_rdtcooked.vcg", RDT);
  if (artVerbose) artDotRDT("art_rdtcooked.dot", RDT);

  // Phase 3: induce a grammar from the specification
  void *inducedTree = artInduceGrammar(RDT);
  artErrorCheck();
  if (artVerbose) artVCGRDT("art_induced.vcg",inducedTree);
  if (artVerbose) artDotRDT("art_induced.dot",inducedTree);

  // Phase 4: remove @ operators; rewrite *, + and ?; check for folds under closures
  artApplyToProductionBodies(inducedTree, artRewriteIterators);
  artApplyToProductionBodies(inducedTree, artRewriteEBNF);
  artApplyToProductionBodies(inducedTree, artTestFoldWellFormed);
  artApplyToProductionBodies(inducedTree, artFixupCatUnary);
  artErrorCheck();
  if (artVerbose) artVCGRDT("art_normalised.vcg", inducedTree);
  if (artVerbose) artDotRDT("art_normalised.dot", inducedTree);
  if (artVerbose) {text_printf("\n** Initial normalised grammar\n");  artWriteSymbols(); artWriteGrammar(true, false); }

  // Phase 5: (optionally) rewrite positive and Kleene closures to their left or right recursive equivalents
  if(artClosureLeft)
    artApplyToProductionBodies(inducedTree, artRewriteClosuresLeft);

  if(artClosureRight)
    artApplyToProductionBodies(inducedTree, artRewriteClosuresRight);

  artApplyToProductionBodies(inducedTree, artFixupCatUnary);

  if (artVerbose) artVCGRDT("art_declosured.vcg", inducedTree);
  if (artVerbose) artDotRDT("art_declosured.dot", inducedTree);

  // Phase 6: (optionally) recursively multiply out all optional and parentheses - leave closures and their subtrees alone
  if(artMultiplyOut) artMultiplyOutProductions(inducedTree);
  artApplyToProductionBodies(inducedTree, artFixupCatUnary);
  if (artVerbose) artVCGRDT("art_multipliedout.vcg", inducedTree);
  if (artVerbose) artDotRDT("art_multipliedout.dot", inducedTree);

  // Phase 7: (optionally) textually left factor using a Trie into the factorised tree
  if (artLeftFactor) artLeftFactorise(inducedTree);
  artApplyToProductionBodies(inducedTree, artFixupCatUnary);
  if (artVerbose) artVCGRDT("art_leftfactorised.vcg", inducedTree);
  if (artVerbose) artDotRDT("art_leftfactorised.dot", inducedTree);

  // Phase 8: (optionally) replace () brackets with nonterminals
  if (artBracketToNonterminal) artApplyToProductionBodies(inducedTree, artRewriteBrackets);
  if (artVerbose) artVCGRDT("art_brackettononterminal.vcg", inducedTree);
  if (artVerbose) artDotRDT("art_brackettononterminal.dot", inducedTree);

  // Phase 9: connect %X tear operators within insertions to the corresponding ^^^ operator
  artResolveTearsRec((rdp_tree_node_data*) graph_root(inducedTree));
  if (artVerbose) artVCGRDT("art_tearresolved.vcg", inducedTree);
  if (artVerbose) artDotRDT("art_tearresolved.dot", inducedTree);
  artErrorCheck();

  // Phase 10: make a clean copy of the tree with no deleted nodes
  artGrammarConsistency();
  void *ift = (rdp_tree_node_data*) graph_insert_graph("ART IFT");

  rdp_tree_node_data *root = (rdp_tree_node_data*) graph_insert_node(sizeof(rdp_tree_node_data), ift);
  root->kind = ART_ROOT; root->id = "IFT root";
  graph_set_root(ift, root);

  for (void *edge = graph_next_out_edge(graph_root(inducedTree)); edge != NULL; edge = graph_next_out_edge(edge))
    artCloneTreeAsChildOf(root, (rdp_tree_node_data*) graph_destination(edge));

  //Now adjust symbol table to point at the cleaned tree rather than the P' tree
  for (void *moduleEdge = graph_next_out_edge(graph_root(ift)); moduleEdge != NULL; moduleEdge = graph_next_out_edge(moduleEdge)) {
    rdp_tree_node_data *moduleNode = (rdp_tree_node_data*) graph_destination(moduleEdge);

    artFind(ART_MAJOR, moduleNode->id, moduleNode->id, NULL)->iftNode = moduleNode;

    for (void *productionEdge = graph_next_out_edge(moduleNode); productionEdge != NULL; productionEdge = graph_next_out_edge(productionEdge)) {
      rdp_tree_node_data *productionNode = (rdp_tree_node_data*) graph_destination(productionEdge);

      artFind(ART_NONTERMINAL, moduleNode->id, productionNode->id, NULL)->iftNode = productionNode;
    }
  }

  //Now adjust symbol table to suppress nonterminals that point to deleted nodes - these correspond to rewritten rules under rewritten rules
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol)) {
    if (thisSymbol->kind == ART_NONTERMINAL) {
      if (thisSymbol->iftNode == NULL && !thisSymbol->isGatherTarget) {
        text_message(TEXT_ERROR, "nonterminal '%s.%s' undefined\n", thisSymbol->majorID, thisSymbol->minorID);
        continue;
      }
      else {   // Check for repeated rules and issue warnings
        for (void *outerEdge = graph_next_out_edge(thisSymbol->iftNode); outerEdge != NULL; outerEdge = graph_next_out_edge(outerEdge))
          for (void *innerEdge = graph_next_out_edge(outerEdge); innerEdge != NULL; innerEdge = graph_next_out_edge(innerEdge))
            if (artEqualSubtreeRec(false, (rdp_tree_node_data*) graph_destination(innerEdge), (rdp_tree_node_data*) graph_destination(outerEdge)))
              text_message(TEXT_WARNING, "Repeated rule for nonterminal %s.%s\n", thisSymbol->majorID, thisSymbol->minorID);
      }

      if (thisSymbol->iftNode != NULL && thisSymbol->iftNode->deleted)
        text_printf("found deleted nonterminal %s.%s\n", thisSymbol->majorID, thisSymbol->minorID);
    }
  }

  // Update node numbers to suit V3 conventions, allocate instance numbers and hasDelayedInstance flag
  nodeNumber = 3;
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    if (thisSymbol->kind == ART_NONTERMINAL) {
      if (thisSymbol->iftNode != NULL) // watch out for undefined nonterminals which will have an empty tree!
        artSetNodeNumberRec(thisSymbol->iftNode);
      for (void *edge = graph_next_out_edge(thisSymbol->iftNode); edge != NULL; edge = graph_next_out_edge(edge)) {
        terminalInstanceNumber = 1;
        for (symbols_data *innerSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols)); innerSymbol != NULL; innerSymbol = (symbols_data*) symbol_next_symbol_in_scope(innerSymbol))
          innerSymbol->nextInstanceNumber = 1; // By production
        artSetInstanceNumberRec(thisSymbol, (rdp_tree_node_data*) graph_destination(edge));
    }
  }

  if (artVerbose) artVCGRDT("art_cleaned.vcg", ift);
  if (artVerbose) artDotRDT("art_cleaned.dot", ift);
  artErrorCheck();

  // Phase  10B: optionally create lexical grammar, and rewrite phrase level grammar to use pseudo token
  if (artVerbose) artWriteLexicalDisambiguationRelations();

  // Phase 11: compute attributes into the resolved tree, yielding the Intermediate Form Tree (IFT)
  artComputeSiblingLinksAndAritiesRec((rdp_tree_node_data*) graph_root(ift), NULL);
  artComputeSets();
  if (artVerbose) artVCGRDT("art_iftzzz.vcg", ift);
  if (artVerbose) artDotRDT("art_iftzzz.dot", ift);
  artComputeAttributes();
  while (artComputeGuardSetsRec((rdp_tree_node_data*) graph_root(ift), NULL, NULL))
    ;
  artComputeMergedSets(ift);
  if (artVerbose) artWriteMergedSets();
  if (artVerbose) artVCGRDT("art_ift.vcg", ift);
  if (artVerbose) artDotRDT("art_ift.dot", ift);
//  artErrorCheck();

  if (artVerbose) {text_printf("\n** Final rewritten grammar\n");  artWriteSymbols(); artWriteGrammar(false, false); }

// Grammar status
  if (artVerbose) {
    if (artGrammarIsEBNF && !artGrammarIsFBNF)
      text_printf("\nFinal grammar is EBNF\n");
    else if (artGrammarIsEBNF && artGrammarIsFBNF)
      text_printf("\nFinal grammar is FBNF\n");
    else if (!artGrammarIsEBNF)
      text_printf("\nFinal grammar is BNF\n");
  }

  if (artGrammarIsEBNF && artClusteredGSS)
    text_message(TEXT_WARNING, "-U (clustered GSS) not implemented for EBNF grammars\n");

  // Phase 12: output GDG and MDG - these run from the symbol table so no need to specify a tree
  if (artVerbose) artWriteGDGMDG();

  // Phase 13: output grammar and parsers
  artWriteGrammarOutput(artGrammarFormat);
  // Kludge! Java version outputs an attribute evaluator that uses some extra labels. The 'raw' version skips this. Hence do everything twice to ensure that all
  // labels are marked, and that label enumeration in second version is complete
  for (int tmp = 0; tmp < 2; tmp++) {
  new artParserWriter(artParserNamespace, artParserName, artLexerName, "ARTGLLHashPool", "ART_GLLDUMMY", (rdp_tree_node_data*) graph_root(ift), symbols, new artParserCustomisation);
  if (artVerbose) artVCGRDT("art_iftAfterParserDummyOutput.vcg", ift);
  if (artVerbose) artDotRDT("art_iftAfterParserDummyOutput.dot", ift);
  if (artTargetLanguage == ART_LANGUAGE_CPP) {
    new artParserWriter(artParserNamespace, artParserName, artLexerName, "ARTGLLHashPool", "ARTGLLEnum", (rdp_tree_node_data*) graph_root(ift), symbols, new cppEnumCustomisation);
    new artParserWriter(artParserNamespace, artParserName, artLexerName, "ARTGLLHashPool", "ARTGLLParser", (rdp_tree_node_data*) graph_root(ift), symbols, new cppCustomisation);
  }
  if (artTargetLanguage == ART_LANGUAGE_JAVA)
    switch (artSupportMode) {
      case ART_SUPPORT_OO:
        new artParserWriter(artParserNamespace, artParserName, "ARTGLLObjectOriented", artParserName, artLexerName, (rdp_tree_node_data*) graph_root(ift), symbols, new javaCustomisation);
        break;

      case ART_SUPPORT_HP:
        new artParserWriter(artParserNamespace, artParserName, "ARTGLLParserHashPool", artParserName, artLexerName,(rdp_tree_node_data*) graph_root(ift), symbols, new javaCustomisation);
        break;

      case ART_SUPPORT_BFS:
        text_message(TEXT_ERROR, "BigFastSimple mode not available for Java\n");
        break;
    }
  }

  artErrorCheck();

  if (artVerbose) artVCGRDT("art_iftAfterParserOutput.vcg", ift);
  if (artVerbose) artDotRDT("art_iftAfterParserOutput.dot", ift);

  // Phase 14: output IFT in dump format
  artDumpIFT("art_IFTdump.txt", ift);
}

