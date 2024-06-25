/****************************************************************************
*
* RDP release 1.30 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 28 Dec 1994
*
* symbol.c - a hash coded symbol table
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "params.h"
#include "crash.h"
#include "symbol.h"

static symbol__ **hash_table;   /* table of pointers to hash lists */
static symbol__ *scope_chain,   /* pointers to chain of scope lists */
*root;                          /* pointer to first scope list */

static unsigned symbol__hashsize = 0,  /* number of buckets in symbol table */
 symbol__hashprime;             /* hashing prime: symbolhashsize and symbolhashprime should be coprime */

static integer symbol__scopecount = 0;  /* number of scope regions declared */

symbol__ *symbol__new(void)     /* allocate a new symbol node */
{                               /* allocate space: crash if heap is full */
  symbol__ *temp = (symbol__ *) crash__calloc(sizeof(symbol__), "symbol");

  if (symbol__hashsize == 0)    /* Have we been initialised? */
    crash__("symbol: symbol__init() must be called before creating symbols");
  return (temp);
}

static unsigned symbol__hash_id(const char *s)  /* hash an identifier */
{                               /* hash a string */
  unsigned hashnumber = 0;

  if (s != NULL)                /* Don't try and scan a vacuous string */
    while (*s != 0)             /* run up string */
      hashnumber = *s++ + symbol__hashprime * hashnumber;
  return (hashnumber % symbol__hashsize); /* return an index into hash_table */
}

symbol__ *symbol__lookup_id(const char *s)  /* lookup a symbol by id. Return NULL is not found */
{
  symbol__ *p = hash_table[symbol__hash_id(s)];

  /* look for symbol with same id */
  while (p != NULL && !(strcmp(s, p->id) == 0 && p->scope->data.i > 0))
    p = p->next_hash;

  return p;
}

symbol__ *symbol__lookup_id_scope(const char *s, symbol__ * scope)  /* lookup a symbol by id. Return NULL if not found in scope */
{
  symbol__ *p = hash_table[symbol__hash_id(s)];

  /* look for symbol with same id and equal scope */
  while (p != NULL && !(strcmp(s, p->id) == 0 && p->scope == scope))
    p = p->next_hash;

  return p;
}

symbol__ *symbol__lookup_id_local(const char *s)  /* lookup a symbol by id. Return NULL if not found in current scope */
{
  return symbol__lookup_id_scope(s, scope_chain);
}

void symbol__insert(symbol__ * s) /* insert a symbol at head of hash list */
{
  unsigned hash_index;

  hash_index = symbol__hash_id(s->id);  /* calculate hash bucket */

  s->next_hash = hash_table[hash_index];  /* point s at old hash list head */
  hash_table[hash_index] = s;   /* point hash list at s */

  if (s->next_hash != NULL)     /* if this wasn't the start of a new list ... */
    (s->next_hash)->last_hash = s;  /* ...point old list next back at s */

  /* now insert in scope list */
  s->next_scope = scope_chain->next_scope;
  scope_chain->next_scope = s;

  s->last_scope = scope_chain;  /* special case: s will be first in list */
  if (s->next_scope != NULL)    /* if this wasn't the start of a new list */
    (s->next_scope)->last_scope = s;  /* ... point old list back at s */

  s->scope = scope_chain;       /* set up pointer to scope block */
}

symbol__ *symbol__insert_id(const char *id) /* mak a symbol for id and insert */
{
  symbol__ *s = symbol__new();

  s->id = (char *) id;

  symbol__insert(s);

  return s;
}

void symbol__print_symbol(symbol__ * p) /* dump symbol */
{                               /* type out symbol contents */
  if (p == NULL)
    fprintf(MESSAGES, "(NULL)\n");
  else
    fprintf(MESSAGES, "Node id '%s', scope '%s'\n", p->id, (p->scope)->id);
}

void symbol__print_symboltable(void)  /* dump symbol table */
{                               /* diagnostic dump of symbol table */
  unsigned temp;
  symbol__ *p = root,
  *q;

  fprintf(MESSAGES, "\nSymbol table dump by hash bucket\n");
  for (temp = 0; temp < symbol__hashsize; temp++)
  {
    symbol__ *p = hash_table[temp];

    fprintf(MESSAGES, "*** Bucket %u:\n", temp);
    while (p != NULL)
    {
      symbol__print_symbol(p);
      p = p->next_hash;
    }
  }

  fprintf(MESSAGES, "\nSymbol table dump by scope chain\n");
  while (p != NULL)
  {
    fprintf(MESSAGES, "\nScope id '%s'\n", p->id);
    q = p->next_scope;

    while (q != NULL)
    {
      symbol__print_symbol(q);
      q = q->next_scope;
    }
    p = p->next_hash;
  }

  fprintf(MESSAGES, "End of symbol table dump\n");
}

void symbol__print_statistics(void) /* dump symbol statistics */
{                               /* diagnostic dump of symbol table */
  int temp,
   lists = 0,
   symbols = 0,
   mean = 0;

#define MAX_HISTOGRAM 11
  int histogram[MAX_HISTOGRAM] = {0};

  for (temp = 0; temp < symbol__hashsize; temp++)
  {
    symbol__ *p = hash_table[temp];
    unsigned count = 0;

    while (p != NULL)
    {
      symbols++;
      count++;
      p = p->next_hash;
    }
    histogram[count < MAX_HISTOGRAM - 1 ? count : MAX_HISTOGRAM - 1]++; /* bump histogram bucket */
  }

  fprintf(MESSAGES, "\nSymbol table has %u buckets and contains %i symbols\n", symbol__hashsize, symbols);

  /* Now calculate mean utilisation */

  for (temp = 0; temp < MAX_HISTOGRAM; temp++)
  {
    fprintf(MESSAGES, "%3i bucket%s %i symbol%s\n",
           histogram[temp], histogram[temp] == 1 ? "  has " : "s have",
           temp, temp == 1 ? "" : temp == MAX_HISTOGRAM - 1 ? "s or more" : "s");
    if (histogram[temp] != 0)
      lists++;
    mean += (temp + 1) * histogram[temp];
  }
  fprintf(MESSAGES, "\nMean list length is %f\n", ((float) mean / (float) symbol__hashsize) - 1);
}

void symbol__set_scope(symbol__ * s)
{
  scope_chain = s;
}

symbol__ *symbol__new_scope(const char *id)
{
  symbol__ *p = symbol__new();  /* create new scope element */

  p->id = (char *) id;

  p->data.i = ++symbol__scopecount;

  p->next_hash = root;
  root = p;
  if (p->next_hash != NULL)
    (p->next_hash)->last_hash = p;

  scope_chain = root;
  return p;
}

void symbol__scope_invisible(symbol__ * s)
{
  if (s->data.i > 0)
    s->data.i = -s->data.i;
}

void symbol__scope_visible(symbol__ * s)
{
  if (s->data.i < 0)
    s->data.i = -s->data.i;
}

/* alphabeticise a scope region. Don't change positions in the hash table:
   just move pointers in the scope chain */
void symbol__sort_scope(symbol__ * s)
{
  symbol__ *unsorted,           /* old list */
  *sorted = NULL;               /* temporary build list */

  if ((sorted = s->next_scope) == NULL)
    return;                     /* nothing to do */

  unsorted = sorted->next_scope;/* initialise unsorted to rest of list */
  sorted->last_scope = NULL;    /* make a new list with only one element */
  sorted->next_scope = NULL;

  while (unsorted != NULL)      /* iterate over rest of list */
  {
    /* find insertion point on list */

    while (strcmp(sorted->id, unsorted->id) > 0 && sorted->last_scope != NULL)
      sorted = sorted->last_scope;  /* go backards */

    while (strcmp(sorted->id, unsorted->id) < 0 && sorted->next_scope != NULL)
      sorted = sorted->next_scope;  /* go forwards */

    /* now do the insertion */

    if (strcmp(sorted->id, unsorted->id) > 0)
    {                           /* insert behind */
      symbol__ *insert = unsorted;

      unsorted = unsorted->next_scope;  /* advance to next unsorted symbol */

      insert->next_scope = sorted;
      if (sorted->last_scope != NULL)
        (sorted->last_scope)->next_scope = insert;

      insert->last_scope = sorted->last_scope;
      sorted->last_scope = insert;
    }
    else
    {                           /* insert after */
      symbol__ *insert = unsorted;

      unsorted = unsorted->next_scope;  /* advance to next unsorted symbol */

      insert->next_scope = sorted->next_scope;
      if (sorted->next_scope != NULL)
        (sorted->next_scope)->last_scope = insert;

      sorted->next_scope = insert;
      insert->last_scope = sorted;
    }
  }
  /* now tidy up */
  while (sorted->last_scope != NULL)  /* run back down sorted list */
    sorted = sorted->last_scope;

  s->next_scope = sorted;       /* point scope header at reordered list */
  sorted->last_scope = s;       /* and point list back at scope header */

  /* wow: that was more work than I expected: must investigate non-pointer based recursive data structure representations! */
}


void symbol__init(unsigned symbolhashsize, unsigned symbolhashprime)
{
  unsigned symboltablesize = symbolhashsize * sizeof(symbol__ *),
   count;

  if (symbol__hashsize != 0)
    crash__("symbol: symbol__init() called twice");

  symbol__hashsize = symbolhashsize;
  symbol__hashprime = symbolhashprime;

  hash_table = (symbol__ **) crash__malloc(symboltablesize, "symbol hash table");

  for (count = 0; count < symbol__hashsize; count++)
    hash_table[count] = NULL;

  root = NULL;
  symbol__new_scope("root");
}

/* End of symbol.c */
