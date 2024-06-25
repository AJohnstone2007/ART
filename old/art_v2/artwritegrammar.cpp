/* artwritegrammar.cpp - ART V2 print grammar in various formats (c) Adrian Johnstone 2009-2013 */
#include "artaux.h"

/* We presently support the following ten output formats

ART
ANTLR
BISON
DPARSER
ELKHOUND
GTB
RASCAL
RATS
SDF
STRATEGO

Each P of these has a functions of the form

    artWriteSymbolP              -- output ART grammar elements in target specific format
    artWriteGrammarOutputPRec    -- recursively traverse the ART IFT outputting rules in target specific format

In addition, there is a function artWriteGrammarOutput(enum artGrammarFormatKind format)
which opens the output file and calls the approriate traverser

*/

/* Utility functions and global variables *************************************/
static symbols_data *currentLHSSymbol;

static char* artMapUnderscoreToHyphen(char *str) {
  char *ret = strdup(str);

  for (char* tmp = ret; *tmp != 0; tmp++)
    if (*tmp == '_')
      *tmp = '-';

  return ret;
}

/* ANTLR **********************************************************************/
static int artWriteSymbolANTLR(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_print_C_string((char*) pattern); break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string((char*) pattern);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_string((char*) pattern); width+= text_printf("\'"); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_char((char*) pattern); width+= text_printf("\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_printf("n_"); width+= text_print_C_string((char*) pattern2); width+= text_printf("_"); width+= text_print_C_string((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputANTLRRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: artWriteSymbolANTLR(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" :\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
      text_printf(" "); artWriteSymbolANTLR(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
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
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputANTLRRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
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

/* ART ************************************************************************/
static int artWriteSymbolART(artKind kind, const char* pattern, const char* pattern2)
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
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_char((char*) pattern); width+= text_printf("\'"); break;
    case ART_LHS:
    // Modified May2018 to suppress module names
    //    case ART_NONTERMINAL: text_print_C_string((char*) pattern2); width+= text_printf("_"); width+= text_print_C_string((char*) pattern); break;
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputARTRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: artWriteSymbolART(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" ::=\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
//      artWriteSet(&node->guard);
      text_printf(" "); artWriteSymbolART(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
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
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputARTRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
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
    case ART_LHS: text_printf(" \n\n"); break;
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

/* BISON **********************************************************************/
static int artWriteSymbolBISON(artKind kind, const char* pattern, const char* pattern2, bool printAsIdentifier)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_printf(" /* empty */ "); break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("'");
      width+= text_print_C_char((char*) pattern);
      width+= text_printf("'");
    break;
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
      if (printAsIdentifier)
        artWriteAsID((char*) pattern);
      else {
        width+= text_printf("\"");
        artWriteAsString((char*) pattern);
        width+= text_printf("\"");
      }
      break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern2); width+= text_printf("_"); artWriteAsID((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputBISONRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: artWriteSymbolBISON(node->kind, node->tableEntry->minorID, node->tableEntry->majorID, false); text_printf(" ::=\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
      text_printf(" "); artWriteSymbolBISON(node->kind, node->tableEntry->minorID, node->tableEntry->majorID, true);
      break;

    case ART_EPSILON:
      text_printf(" /* empty */"); break;

    case ART_DO_FIRST:
    case ART_OPTIONAL:
    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE: text_printf(" ("); break;

    case ART_POS: /* text_printf(" ."); */ break;
    case ART_ANNOTATION: text_printf(" {"); break;
    case ART_ANNOTATION_VALUE: text_printf(" %s", node->id); break;
    case ART_INSERTION: text_printf(" ["); break;
    case ART_TEAR: text_printf(" $"); break;
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputBISONRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
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


/* DPARSER ********************************************************************/
static int artWriteSymbolDPARSER(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_printf(" /* empty */"); break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string((char*) pattern);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_string((char*) pattern); width+= text_printf("\""); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_char((char*) pattern); width+= text_printf("\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern2); width+= text_printf("_"); width+= text_print_C_string((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputDPARSERRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: artWriteSymbolDPARSER(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" :\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
      text_printf(" "); artWriteSymbolDPARSER(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
      text_printf(" /* empty */"); break;

    case ART_DO_FIRST:
    case ART_OPTIONAL:
    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE: text_printf(" ("); break;

    case ART_POS: /* text_printf(" ."); */ break;
    case ART_ANNOTATION: text_printf(" {"); break;
    case ART_ANNOTATION_VALUE: text_printf(" %s", node->id); break;
    case ART_INSERTION: text_printf(" ["); break;
    case ART_TEAR: text_printf(" $"); break;
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputDPARSERRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
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

/* ELKHOUND *******************************************************************/
static int artWriteSymbolELKHOUND(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_print_C_string((char*) pattern); break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string((char*) pattern);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_string((char*) pattern); width+= text_printf("\'"); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_char((char*) pattern); width+= text_printf("\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern2); width+= text_printf("_"); width+= text_print_C_string((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputELKHOUNDRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: artWriteSymbolELKHOUND(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" ::=\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
      text_printf(" "); artWriteSymbolELKHOUND(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
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
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputELKHOUNDRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
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
    case ART_LHS: text_printf(" .\n\n"); break;
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

/* GTB ************************************************************************/
static int artWriteSymbolGTB(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_print_C_string((char*) pattern); break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string((char*) pattern);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_string((char*) pattern); width+= text_printf("\'"); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_char((char*) pattern); width+= text_printf("\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern2); width+= text_printf("_"); width+= text_print_C_string((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputGTBRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: artWriteSymbolGTB(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" ::=\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
      text_printf(" "); artWriteSymbolGTB(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
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
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputGTBRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
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
    case ART_LHS: text_printf(" .\n\n"); break;
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

/* Rascal *********************************************************************/
static char* artEscapeAngleBrackets(char *str) {
  int angleCount = 0;

  for (char *tmp = str; *tmp != 0; tmp++)
    if (*tmp == '<' || *tmp == '>')
      angleCount++;

  char *ret = (char*) mem_calloc(1, strlen(str) + 1 + angleCount);

  char *retTmp = ret;
  for (char *tmp = str; *tmp != 0; tmp++) {
    if (*tmp == '<' || *tmp == '>')
      *retTmp++ = '\\';

    *retTmp++ = *tmp;
  }
  return ret;
}

static int artWriteSymbolRASCAL(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string(artEscapeAngleBrackets((char*) pattern)); break;
    case ART_EPSILON: break;
    case ART_BUILTIN_TERMINAL: width+= text_printf("&"); width+= text_print_C_string(artEscapeAngleBrackets((char*) pattern)); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string(artEscapeAngleBrackets((char*) pattern));
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_string(artEscapeAngleBrackets((char*) pattern)); width+= text_printf("\""); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_char(artEscapeAngleBrackets((char*) pattern)); width+= text_printf("\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width += text_printf("N"); width+= text_print_C_string(artEscapeAngleBrackets((char*) pattern2)); width+= text_printf("_"); width+= text_print_C_string(artEscapeAngleBrackets((char*) pattern)); break;
  }
  return width;
}

static bool artChildIsUnary(rdp_tree_node_data *node) {
  rdp_tree_node_data *tmp = artFirstChildOf(node);

  return (/*tmp->kind ==0 &&*/ strcmp(tmp->id, "unary") == 0);
}

static void artWriteGrammarOutputRASCALRec(rdp_tree_node_data *node, int level, bool parentIsBracket) {
  bool newParentIsBracket = false;

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
      break;
    case ART_CAT:
      if (!parentIsBracket)
        text_printf(" (");
      break;

    case ART_LHS:
      if (node->tableEntry == artStartSymbol)
        text_printf("start ");

      text_printf("syntax "); artWriteSymbolRASCAL(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" =\n  ");
      newParentIsBracket = true;
      break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
//      artWriteSet(&node->guard);
      text_printf(" ");
      artWriteSymbolRASCAL(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
//      artWriteSet(&node->follow);

    case ART_DO_FIRST:
    case ART_OPTIONAL:
    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE: newParentIsBracket = true; if (!artChildIsUnary(node)) text_printf(" ("); break;

    case ART_POS: /* text_printf(" ."); */ break;
    case ART_ANNOTATION: text_printf(" {"); break;
    case ART_ANNOTATION_VALUE: text_printf(" %s", node->id); break;
    case ART_INSERTION: text_printf(" ["); break;
    case ART_TEAR: text_printf(" $"); break;
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputRASCALRec((rdp_tree_node_data*) graph_destination(edge), level + 1, newParentIsBracket);
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
    case ART_OPTIONAL: if (artChildIsUnary(node)) text_printf("? "); else text_printf(" )?"); break;
    case ART_POSITIVE_CLOSURE: if (artChildIsUnary(node)) text_printf("+ "); else text_printf(" )+"); break;
    case ART_KLEENE_CLOSURE: if (artChildIsUnary(node)) text_printf("* "); else text_printf(" )*"); break;

    case ART_ANNOTATION: text_printf(" } "); break;
    case ART_INSERTION: text_printf(" ]"); break;
    case ART_CAT: if (!parentIsBracket) text_printf(" )");

    case ART_UNARY: case ART_EPSILON:
      break;
    default: break;
  };
}

/* RATS ***********************************************************************/
static int artWriteSymbolRATS(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_print_C_string((char*) pattern); break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("`");
      width+= text_print_C_string((char*) pattern);
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_string((char*) pattern); width+= text_printf("\'"); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_char((char*) pattern); width+= text_printf("\""); break;
    case ART_LHS:
    case ART_NONTERMINAL: width+= text_print_C_string((char*) pattern2); width+= text_printf("_"); width+= text_print_C_string((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputRATSRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: text_printf("String "); artWriteSymbolRATS(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" =\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
      text_printf(" "); artWriteSymbolRATS(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
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
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputRATSRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
    // Inorder actions
    if (graph_next_out_edge(edge) != NULL)
      switch (node->kind) {
        case ART_DIFF: text_printf(" / "); break;
        case ART_ITER: text_printf(" @ "); break;
        case ART_ALT: text_printf(" / "); break;
        case ART_LHS: text_printf(" /\n  "); break;
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

/* RDP ************************************************************************/
static int artWriteSymbolRDP(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: width += text_print_C_string((char*) pattern);break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string((char*) pattern); break;
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_char((char*) pattern); width+= text_printf("\'"); break;
    case ART_LHS:
    case ART_NONTERMINAL: text_print_C_string((char*) pattern2); width+= text_printf("_"); width+= text_print_C_string((char*) pattern); break;
  }
  return width;
}

static void artWriteGrammarOutputRDPRec(rdp_tree_node_data *node, int level) {
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

    case ART_LHS: artWriteSymbolRDP(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); text_printf(" ::=\n  "); break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
//      artWriteSet(&node->guard);
      text_printf(" "); artWriteSymbolRDP(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
//      artWriteSet(&node->follow);
      text_printf(" #"); break;

    case ART_DO_FIRST:  text_printf(" ("); break;
    case ART_OPTIONAL:  text_printf(" ["); break;
    case ART_POSITIVE_CLOSURE:  text_printf(" <"); break;
    case ART_KLEENE_CLOSURE: text_printf(" {"); break;

    case ART_POS: /* text_printf(" ."); */ break;
    case ART_ANNOTATION: text_printf(" [*"); break;
    case ART_ANNOTATION_VALUE: text_printf(" %s", node->id); break;
    case ART_INSERTION: text_printf(" [["); break;
    case ART_TEAR: text_printf(" $$"); break;
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputRDPRec((rdp_tree_node_data*) graph_destination(edge), level + 1);
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
    case ART_LHS: text_printf(" .\n\n"); break;
    case ART_DO_FIRST: text_printf(" )"); break;
    case ART_OPTIONAL: text_printf(" ]"); break;
    case ART_POSITIVE_CLOSURE: text_printf(" >"); break;
    case ART_KLEENE_CLOSURE: text_printf(" }"); break;

    case ART_ANNOTATION: text_printf(" *] "); break;
    case ART_INSERTION: text_printf(" ]]"); break;
    case ART_CAT: case ART_UNARY: case ART_EPSILON:
      break;
    default: break;
  };
}

/* SDF and Stratego ***********************************************************/
static int artWriteSymbolSDF(artKind kind, const char* pattern, const char* pattern2)
{
  int width = 0;
  switch (kind)
  {
    default:
    case ART_END_OF_STRING: width += text_print_C_string((char*) pattern); break;
    case ART_EPSILON: break;
    case ART_BUILTIN_TERMINAL: width+= text_print_C_string(artMapUnderscoreToHyphen((char*) pattern)); break;
    case ART_CHARACTER_TERMINAL:
      width+= text_printf("\"");
      width+= text_print_C_string((char*) pattern);
      width+= text_printf("\"");
    break;
    case ART_CASE_SENSITIVE_TERMINAL: width+= text_printf("\""); width+= text_print_C_string((char*) pattern); width+= text_printf("\""); break;
    case ART_CASE_INSENSITIVE_TERMINAL: width+= text_printf("\'"); width+= text_print_C_char((char*) pattern); width+= text_printf("\'"); break;
    case ART_LHS:
    case ART_NONTERMINAL:
      width += text_printf("N");
      width+= text_print_C_string(artMapUnderscoreToHyphen((char*) pattern2));
      width+= text_printf("-");
      width+= text_print_C_string(artMapUnderscoreToHyphen((char*) pattern)); break;
  }
  return width;
}

static rdp_tree_node_data *lastLHSNode;

static void artWriteGrammarOutputSDFRec(rdp_tree_node_data *node, int level, bool isStratego) {
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

    case ART_LHS: text_printf("  "); lastLHSNode = node; break;

    case ART_BUILTIN_TERMINAL:
    case ART_CHARACTER_TERMINAL:
    case ART_CASE_SENSITIVE_TERMINAL:
    case ART_CASE_INSENSITIVE_TERMINAL:
    case ART_NONTERMINAL:
//      artWriteSet(&node->guard);
      text_printf(" "); artWriteSymbolSDF(node->kind, node->tableEntry->minorID, node->tableEntry->majorID);
      break;

    case ART_EPSILON:
//      artWriteSet(&node->follow);
      break;

    case ART_DO_FIRST:
    case ART_OPTIONAL:
    case ART_POSITIVE_CLOSURE:
    case ART_KLEENE_CLOSURE: text_printf(" (("); break;

    case ART_POS: /* text_printf(" ."); */ break;
    case ART_ANNOTATION: text_printf(" {"); break;
    case ART_ANNOTATION_VALUE: text_printf(" %s", node->id); break;
    case ART_INSERTION: text_printf(" ["); break;
    case ART_TEAR: text_printf(" $"); break;
    default: text_message(TEXT_FATAL, "unknown node kind %i encountered whilst printing grammar\n", node->kind);
  };

  for (void *edge = graph_next_out_edge(node); edge != NULL; edge = graph_next_out_edge(edge)) {
    artWriteGrammarOutputSDFRec((rdp_tree_node_data*) graph_destination(edge), level + 1, isStratego);
    // Inorder actions
    if (graph_next_out_edge(edge) != NULL)
      switch (node->kind) {
        case ART_DIFF: text_printf(" / "); break;
        case ART_ITER: text_printf(" @ "); break;
        case ART_ALT: text_printf(" )|( "); break;
        case ART_LHS: text_printf(" -> "); artWriteSymbolSDF(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); if (isStratego) text_printf(" {}"); text_printf("\n  "); break;
        default: break;
      };
  }

  //Postorder actions
  switch (node->kind) {
    case ART_LHS: text_printf(" -> "); artWriteSymbolSDF(node->kind, node->tableEntry->minorID, node->tableEntry->majorID); if (isStratego) text_printf(" {}"); text_printf("\n\n"); break;
    case ART_DO_FIRST: text_printf(" ))"); break;
    case ART_OPTIONAL: text_printf(" ))?"); break;
    case ART_POSITIVE_CLOSURE: text_printf(" ))+"); break;
    case ART_KLEENE_CLOSURE: text_printf(" ))*"); break;

    case ART_ANNOTATION: text_printf(" } "); break;
    case ART_INSERTION: text_printf(" ]"); break;
    case ART_CAT: case ART_UNARY: case ART_EPSILON:
      break;
    default: break;
  };
}

void artWriteGrammarOutput(enum artGrammarFormatKind format) {

  // Preludes
  switch (format) {
    case ART_GRAMMAR_NONE:
      return;
    case ART_GRAMMAR_ANTLR:
      artRedirectToFile("Artout.g4");
      text_printf("/* Grammar generated by ART from '%s' on %s */\n\n", rdp_sourcefilename, text_time_string());
      text_printf("grammar Artout;\n\n");
      break;
    case ART_GRAMMAR_ART:
      artRedirectToFile("Artout.art");
      text_printf("(* ART3 grammar generated by ART from '%s' on %s *)\n\n", rdp_sourcefilename, text_time_string());
      text_printf("start ");
      artWriteSymbolART(artStartSymbol->kind, artStartSymbol->minorID, artStartSymbol->majorID);
      text_printf("\n\n");
      break;
    case ART_GRAMMAR_BISON:
      artRedirectToFile("Artout.y");
      text_printf("/* Grammar generated by ART from '%s' on %s */\n\n", rdp_sourcefilename, text_time_string());
      text_printf("%%{\n"
                  "#include <stdio.h>\n"
                  "#include <string.h>\n\n"
                  "void yyerror(const char *str) { fprintf(stderr,\"error: %%s\\n\",str); }\n"
                  "int yywrap() { return 1; }\n"
                  "int main() { yyparse(); }\n"
                  "%%}\n\n"
                  "%%start ");
        artWriteSymbolBISON(artStartSymbol->kind, artStartSymbol->minorID, artStartSymbol->majorID, true);
        text_printf("\n\n");
      break;
    case ART_GRAMMAR_DPARSER:
      artRedirectToFile("Artout.g");
      text_printf("/* Grammar generated by ART from '%s' on %s */\n\n", rdp_sourcefilename, text_time_string());
      break;
    case ART_GRAMMAR_ELKHOUND:
      artRedirectToFile("Artout.gr");
      text_printf("/* Grammar generated by ART from '%s' on %s */\n\n", rdp_sourcefilename, text_time_string());
      break;
    case ART_GRAMMAR_GTB:
      artRedirectToFile("Artout.gtb");
      text_printf("(* Grammar generated by ART from '%s' on %s *)\n\n", rdp_sourcefilename, text_time_string());
      break;
    case ART_GRAMMAR_RASCAL:
      artRedirectToFile("Artout.rsc");
      text_printf("/* Grammar generated by ART from '%s' on %s */\n\n", rdp_sourcefilename, text_time_string());
      text_printf("module Artout\n\nimport IO;\n\nimport ParseTree;\n\nlayout LAYOUT = [\\t-\\n\\r\\ ]*;\n\n");
      break;
    case ART_GRAMMAR_RATS:
      artRedirectToFile("Artout.rat");
      text_printf("/* Grammar generated by ART from '%s' on %s */\n\n", rdp_sourcefilename, text_time_string());
      text_printf("module Artout");
      text_printf(";\n\noption main(Artout_");
      artWriteSymbolART(artStartSymbol->kind, artStartSymbol->minorID, artStartSymbol->majorID);
      text_printf(");\n\npublic String Artout_");
      artWriteSymbolART(artStartSymbol->kind, artStartSymbol->minorID, artStartSymbol->majorID);
      text_printf(" = WS ");
      artWriteSymbolART(artStartSymbol->kind, artStartSymbol->minorID, artStartSymbol->majorID);
      text_printf(" WS EOF;\n\n");
      break;
    case ART_GRAMMAR_RDP:
      artRedirectToFile("Artout.rdp");
      text_printf("(* Grammar generated by ART from '%s' on %s *)\n\n", rdp_sourcefilename, text_time_string());
      break;
    case ART_GRAMMAR_SDF:
      artRedirectToFile("Artout.sdf");
      text_printf("% Grammar generated by ART from '%s' on %s %\n\n", rdp_sourcefilename, text_time_string());
      text_printf("module Artout\n\nimports basic/Whitespace\n\nexports\nsorts\n");
      break;
    case ART_GRAMMAR_STRATEGO:
      artRedirectToFile("Artout.sdf");
      text_printf("% Grammar generated by ART from '%s' on %s %\n\n", rdp_sourcefilename, text_time_string());
      text_printf("module Artout\n\nimports basic/Whitespace\n\nexports\nsorts\n");
      break;
    default: text_message(TEXT_ERROR, "unexpected output grammar format\n");
  }

  // Declare symbols
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol))
    switch (format) {
      case ART_GRAMMAR_ANTLR:
        break;
      case ART_GRAMMAR_ART:
        break;
      case ART_GRAMMAR_BISON:
        if (thisSymbol->kind == ART_CASE_SENSITIVE_TERMINAL || thisSymbol->kind == ART_CASE_INSENSITIVE_TERMINAL) {
          text_printf("%%token ");
          artWriteSymbolBISON(thisSymbol->kind, thisSymbol->minorID, thisSymbol->majorID, true);
          text_printf(" ");
          artWriteSymbolBISON(thisSymbol->kind, thisSymbol->minorID, thisSymbol->majorID, false);
          text_printf("\n");
        }
        break;
      case ART_GRAMMAR_DPARSER:
        break;
      case ART_GRAMMAR_ELKHOUND:
        break;
      case ART_GRAMMAR_GTB:
        break;
      case ART_GRAMMAR_RASCAL:
        break;
      case ART_GRAMMAR_RATS:
        break;
      case ART_GRAMMAR_RDP:
        break;
      case ART_GRAMMAR_SDF: case ART_GRAMMAR_STRATEGO:
        if (thisSymbol->kind == ART_NONTERMINAL) {
          text_printf("  ");
          artWriteSymbolSDF(thisSymbol->kind, thisSymbol->minorID, thisSymbol->majorID);
          text_printf("\n");
        }
        break;
    }

  // Midludes
  switch (format) {
    case ART_GRAMMAR_NONE:
      return;
    case ART_GRAMMAR_ANTLR:
      break;
    case ART_GRAMMAR_ART:
      break;
    case ART_GRAMMAR_BISON:
      text_printf("\n\n%%%%\n\n");
      break;
    case ART_GRAMMAR_DPARSER:
      break;
    case ART_GRAMMAR_ELKHOUND:
      break;
    case ART_GRAMMAR_GTB:
      break;
    case ART_GRAMMAR_RASCAL:
      break;
    case ART_GRAMMAR_RATS:
      break;
      case ART_GRAMMAR_RDP:
        break;
    case ART_GRAMMAR_SDF: case ART_GRAMMAR_STRATEGO:
      text_printf("\nexports\ncontext-free startsymbols\n  ");
      artWriteSymbolSDF(artStartSymbol->kind, artStartSymbol->minorID, artStartSymbol->majorID);
      text_printf("\n\nexports\ncontext-free syntax\n\n");
      break;
    default: text_message(TEXT_ERROR, "unexpected output grammar format\n");
  }

  // Print rules
  for (symbols_data *thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(symbol_get_scope(symbols));
       thisSymbol != NULL;
       thisSymbol = (symbols_data*) symbol_next_symbol_in_scope(thisSymbol)) {
    if (thisSymbol->kind != ART_NONTERMINAL) continue;

    currentLHSSymbol = thisSymbol;

    switch (format) {
      case ART_GRAMMAR_ANTLR:
        artWriteGrammarOutputANTLRRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_ART:
        artWriteGrammarOutputARTRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_BISON:
        artWriteGrammarOutputBISONRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_DPARSER:
        artWriteGrammarOutputDPARSERRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_ELKHOUND:
        artWriteGrammarOutputELKHOUNDRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_GTB:
        artWriteGrammarOutputGTBRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_RASCAL:
        artWriteGrammarOutputRASCALRec(thisSymbol->iftNode, 1, false);
        break;
      case ART_GRAMMAR_RATS:
        artWriteGrammarOutputRATSRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_RDP:
        artWriteGrammarOutputRDPRec(thisSymbol->iftNode, 1);
        break;
      case ART_GRAMMAR_SDF:
        artWriteGrammarOutputSDFRec(thisSymbol->iftNode, 1, false);
        break;
      case ART_GRAMMAR_STRATEGO:
        artWriteGrammarOutputSDFRec(thisSymbol->iftNode, 1, true);
        break;
    }
  }

  // Postludes
  switch (format) {
    case ART_GRAMMAR_ANTLR:
      break;
    case ART_GRAMMAR_ART:
       break;
    case ART_GRAMMAR_BISON:
      text_printf("%%%%\n");
      break;
    case ART_GRAMMAR_DPARSER:
      break;
    case ART_GRAMMAR_ELKHOUND:
      break;
    case ART_GRAMMAR_GTB:
       break;
    case ART_GRAMMAR_RASCAL:
       break;
    case ART_GRAMMAR_RATS:
      text_printf("\nString ID = [_A-Za-z][_0-9A-Za-z]* ;\n\n"
                  "void EOF = !_ ;\n\n"
                  "void WS = [ \\t\\n]* ;\n\n");
      break;
    case ART_GRAMMAR_RDP:
       break;
    case ART_GRAMMAR_SDF:
       break;
    case ART_GRAMMAR_STRATEGO:
       break;
  }

  text_redirect(stdout);
}

