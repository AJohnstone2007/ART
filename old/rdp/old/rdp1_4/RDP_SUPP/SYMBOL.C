/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* symbol.c - a hash coded symbol table
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "textio.h"
#include "memalloc.h"
#include "symbol.h"

/* define some shorthand casts */
#define TABLE ((symbol__table *) table)
#define SYMBOL ((symbol__ *) symbol)

static char *symbol__root_string = "Root";

typedef struct scope_data_node
{
  char *id;
} symbol__scope_data;

typedef struct symbol__node
{
  struct symbol__node
  *next_hash,                   /* next symbol in hash list */
  **last_hash,                  /* pointer to next pointer of last_symbol in hash list */
  *next_scope,                  /* next symbol in scope list */
  *scope;                       /* pointer to the scope symbol */
  unsigned hash;                /* hash value for quick searching */
} symbol__;

typedef struct symbol__table_node
{
  char *name;                   /* an identifying string */
  symbol__ **table;             /* table of pointers to hash lists */
  symbol__ *current;            /* pointers to chain of scope lists */
  symbol__ *scopes;             /* pointer to first scope list */
  unsigned hash_size;           /* number of buckets in symbol table */
  unsigned hash_prime;          /* hashing prime: hashsize and hashprime should be coprime */
  int (*compare) (void *left, void *right);
  unsigned (*hash) (unsigned prime, void *data);
  void (*print) (const void *symbol);
  struct symbol__table_node *next;    /* pointer to last declared symbol table */
} symbol__table;

static symbol__table * symbol__tables = NULL;

int symbol__compare_string(void *left, void *right)
{
  char *left_str = *(char **) left;
  char *right_str = *(char **) right;

  return strcmp(left_str, right_str);
}

void symbol__free_symbol(void *symbol)
{
  symbol__ *sym = SYMBOL - 1;

  /* first unlink: code copied from symbol__unlink with extra check */
  if (sym->last_hash != NULL)
    *(sym->last_hash) = sym->next_hash; /* point previous pointer to next symbol */

  if (sym->next_hash != NULL)   /* if this isn't the end of the list */
    sym->next_hash->last_hash = sym->last_hash;

  mem__free(sym);               /* free the memory */
}

void *symbol__get_scope(const void *table) /* return current scope */
{
  return TABLE->current + 1;
}

unsigned symbol__hash_mem(unsigned hash_prime, void *data)
{                               /* hash a string */
  unsigned hashnumber = 0;
  unsigned count = *(unsigned *) data;
  char *string = *(char **) ((unsigned *) data + 1);

  if (string != NULL)           /* Don't try and scan a vacuous string */
    while (count-- > 0)         /* run up string */
      hashnumber = *string++ + hash_prime * hashnumber;
  return hashnumber;
}

unsigned symbol__hash_string(unsigned hash_prime, void *data)
{                               /* hash a string */
  unsigned hashnumber = 0;
  char *str = *(char **) data;

  if (str != NULL)              /* Don't try and scan a vacuous string */
    while (*str != 0)           /* run up string */
      hashnumber = *str++ + hash_prime * hashnumber;
  return hashnumber;
}

void *symbol__insert_string_key(const void *table, char *str, size_t size) /* lookup key in table. Return NULL if not found */
{
  char** d = (char **) symbol__new_symbol(size);

  *d = str;

  return symbol__insert_symbol(table, d);
}

void *symbol__insert_symbol(const void *table, void *symbol) /* insert a symbol at head of hash list */
{
  unsigned hash_index;
  symbol__ *s = SYMBOL - 1;

  s->hash = (*TABLE->hash)(TABLE->hash_prime, (char **) symbol);
  hash_index = s->hash % TABLE->hash_size;

  s->next_hash = TABLE->table[hash_index];  /* point s at old hash list head */
  TABLE->table[hash_index] = s; /* point hash list at s */

  s->last_hash = &TABLE->table[hash_index]; /* point last hash at index pointer */

  if (s->next_hash != NULL)     /* if this wasn't the start of a new list ... */
    (s->next_hash)->last_hash = &(s->next_hash);  /* ...point old list next back at s */

  /* now insert in scope list */
  s->next_scope = TABLE->current->next_scope;
  TABLE->current->next_scope = s;

  s->scope = TABLE->current;  /* set up pointer to scope block */

  return symbol;
}

void *symbol__lookup_key(const void *table, void *key)  /* lookup a symbol by id. Return NULL is not found */
{
  unsigned hash = (*TABLE->hash)(TABLE->hash_prime, &key);
  symbol__ *p = TABLE->table[hash % TABLE->hash_size];

  /* look for symbol with same hash and a true compare */
  while (p != NULL && /* hash!=p->hash && */ (*TABLE->compare)(&key, p + 1) != 0)
    p = p->next_hash;

  return p == NULL ? p : p + 1;
}

void *symbol__new_scope(void *table, char *id)
{
  symbol__scope_data *p = (symbol__scope_data *) symbol__new_symbol(sizeof(symbol__scope_data));  /* create new scope element */
  symbol__ *s = (symbol__ *) p - 1;

  p->id = id;
  s->next_hash = TABLE->scopes;
  TABLE->current = TABLE->scopes = s;
  if (s->next_hash != NULL)
    (s->next_hash)->last_hash = &(s->next_hash);
  return p;
}

void *symbol__new_symbol(size_t size) /* allocate a new symbol node */
{                               /* allocate space: crash if heap is full */
  void *temp1 = mem__calloc(sizeof(symbol__) + size, 1);
  void *temp2 = (symbol__ *) temp1 + 1;

  return temp2;
}

void *symbol__new_table(char *name,
			 const unsigned symbol__hashsize,
                         const unsigned symbol__hashprime,
			 int (*compare) (void *left, void *right),
                         unsigned (*hash) (unsigned hash_prime, void *symbol),
			 void (*print) (const void *symbol))  /* create a new symbol table */
{
  symbol__table *temp = (symbol__table *) mem__malloc(sizeof(symbol__table));
  symbol__scope_data *scope = (symbol__scope_data *) symbol__new_symbol(sizeof(symbol__scope_data));
  unsigned count;

  scope->id = symbol__root_string;
  temp->name = name;
  temp->hash_size = symbol__hashsize;
  temp->hash_prime = symbol__hashprime;
  temp->compare = compare;
  temp->hash = hash;
  temp->print = print;
  temp->table = (symbol__ **) mem__malloc(symbol__hashsize * sizeof(symbol__ *));

  for (count = 0; count < symbol__hashsize; count++)
    temp->table[count] = NULL;

  temp->current = temp->scopes = (symbol__ *) scope - 1;

  /* now hook into list of tables */
  temp->next = symbol__tables;
  symbol__tables = temp;

  return temp;
}

void *symbol__next_symbol(void *table, void *symbol)  /* lookup along table from s for next identical ID. Return NULL if not found */
{
  symbol__ *p = SYMBOL - 1;

  p = p->next_hash;
  while (p != NULL && (*TABLE->compare)(symbol, p + 1) != 0)
    p = p->next_hash;

  return p == NULL ? p : p + 1;
}

void *symbol__next_symbol_in_scope(void *symbol) /* Return next symbol in scope chain. Return NULL if at end */
{
  symbol__ * s = (symbol__*) (((symbol__ *) symbol - 1) -> next_scope);

  return s == NULL ? NULL : s + 1;
}

void symbol__print_all_table(void)  /* diagnostic dump of all symbol tables */
{
  symbol__table * temp = symbol__tables;

  while (temp != NULL)
  {
    symbol__print_table(temp);
    temp = temp->next;
  }
}

void symbol__print_all_table_statistics(const int histogram_size) /* dump all bucket usage histograms */
{
  symbol__table * temp = symbol__tables;

  while (temp != NULL)
  {
    symbol__print_table_statistics(temp, histogram_size);
    temp = temp->next;
  }
}

void symbol__print_scope(const void * table, void *sym)
{
  text__printf("\nScope id '%s'\n", *(char **) sym);
  sym = symbol__next_symbol_in_scope(sym);

  while (sym != NULL)
  {
    (*TABLE->print)(sym);
    sym = symbol__next_symbol_in_scope(sym);
  }
}

void symbol__print_string(const void *symbol)
{
  text__printf("%s\n", symbol == NULL ? "Null symbol" : *((char **) symbol));
}

void symbol__print_symbol(const void *table, const void *symbol)
{
  (*TABLE->print)(symbol);
}

void symbol__print_table(const void *table) /* dump symbol table */
{                               /* diagnostic dump of symbol table */
  unsigned temp;
  symbol__ *p = TABLE->scopes;

  text__message(TEXT__INFO, "Symbol table '%s': dump by hash bucket\n\n", TABLE->name);
  for (temp = 0; temp < TABLE->hash_size; temp++)
  {
    symbol__ *p = TABLE->table[temp];

    text__printf("*** Bucket %u:\n", temp);
    while (p != NULL)
    {
      (*TABLE->print)(p + 1);
      p = p->next_hash;
    }
  }
  text__printf("\n");

  text__message(TEXT__INFO, "Symbol table '%s' dump by scope chain\n", TABLE->name);
  while (p != NULL)
  {
    symbol__print_scope(table, p + 1);
    p = p->next_hash;
  }
  text__printf("\n");
}

void symbol__print_table_statistics(const void *table, const int histogram_size)  /* dump symbol statistics */
{                               /* diagnostic dump of symbol table */
  int temp,
   lists = 0,
   symbols = 0,
   mean = 0;

  int *histogram = (int *) mem__calloc(histogram_size, sizeof(int));

  for (temp = 0; temp < TABLE->hash_size; temp++)
  {
    symbol__ *p = TABLE->table[temp];
    unsigned count = 0;

    while (p != NULL)
    {
      symbols++;
      count++;
      p = p->next_hash;
    }
    histogram[count < histogram_size - 1 ? count : histogram_size - 1]++; /* bump histogram bucket */
  }

  text__message(TEXT__INFO, "\nSymbol table `%s' has %u buckets and contains %i symbols\n", TABLE->name, TABLE->hash_size, symbols);

  /* Now calculate mean utilisation */

  for (temp = 0; temp < histogram_size; temp++)
  {
    text__printf("%3i bucket%s %i symbol%s\n",
                 histogram[temp], histogram[temp] == 1 ? "  has " : "s have",
		 temp, temp == 1 ? "" : temp == histogram_size - 1 ? "s or more" : "s");
    if (histogram[temp] != 0)
      lists++;
    mean += (temp + 1) * histogram[temp];
  }
  text__message(TEXT__INFO, "\nMean list length is %f\n", ((float) mean / (float) TABLE->hash_size) - 1);

  mem__free(histogram);         /* release memory used for histogram */
}

void symbol__set_scope(void *table, void *symbol)
{
  symbol__ *s = SYMBOL - 1;

  TABLE->current = s;
}

/* Sort a scope region. Don't change positions in the hash table:
   just move pointers in the scope chain */
void symbol__sort_scope(void *table, void *symbol)
{
  typedef struct sort_list_type
  {
    symbol__ *s;
    struct sort_list_type *next;
    struct sort_list_type **last;
  } sort_list;

  symbol__ *s = SYMBOL - 1;

  sort_list *sorted = (sort_list *) mem__malloc(sizeof(sort_list));
  sort_list *temp_sorted;
  symbol__ *temp_scope;

  if (s->next_scope == NULL)    /* attempt to sort empty list */
    return;

  if (s->next_scope->next_scope == NULL)  /* attempt to sort list of one */
    return;

  /* put first symbol in scope on sorted list */
  sorted->s = s->next_scope;
  sorted->last = &sorted;

  /* put a sentinel on the end */
  sorted->next = (sort_list *) mem__calloc(sizeof(sort_list), 1);
  sorted->next->last = &sorted->next;

  /* first create sorted list by insertion sort on sort_list */
  temp_scope = s->next_scope->next_scope; /* point temp_scope at second element */
  while (temp_scope != NULL)    /* iterate over entire scope list */
  {
    sort_list *new_sorted;

    /* find place in sorted list for insertion */
    temp_sorted = sorted;
    while (temp_sorted->next != NULL)
    {
      if ((*TABLE->compare) (temp_scope + 1, temp_sorted->s + 1) < 0)
        break;
      temp_sorted = temp_sorted->next;
    }

    /* now insert new sorted node before the one pointed to by temp_sorted */
    new_sorted = (sort_list *) mem__malloc(sizeof(sort_list));  /* create node */
    new_sorted->s = temp_scope; /* point at current scope symbol */
    new_sorted->next = temp_sorted; /* look forwards... */
    new_sorted->last = temp_sorted->last; /* and backwards */
    *(temp_sorted->last) = new_sorted;  /* make temp's predecessor look at us */
    temp_sorted->last = &new_sorted->next;  /* and make temp look at us */

    temp_scope = temp_scope->next_scope;  /* step to next element in scope list */
  }

  /* now rebuild scope pointers in scope range: the list should be the right length already! */
  temp_sorted = sorted;
  temp_scope = s;
  while (temp_sorted != NULL)   /* iterate over sorted list */
  {
    sort_list * free_sorted = temp_sorted;

    temp_scope->next_scope = temp_sorted->s;  /* point next scope to this sorted symbol */
    temp_sorted = temp_sorted->next;  /* move to next sorted symbol */
    mem__free(free_sorted);               /* free as we go */
    temp_scope = temp_scope->next_scope;  /* move to next sorted scope element */
  }
}

void symbol__unlink_scope(void *symbol)
{
  symbol__ *s = SYMBOL - 1;

  s = s->next_scope;

  while (s != NULL)
  {
    symbol__unlink_symbol(s + 1);
    s = s->next_scope;
  }
}

void symbol__unlink_symbol(void *symbol)  /* remove a symbol from the hash chain */
{
  symbol__ *s = SYMBOL - 1;

  *(s->last_hash) = s->next_hash; /* point previous pointer to next symbol */

  if (s->next_hash != NULL)     /* if this isn't the end of the list */
    s->next_hash->last_hash = s->last_hash;
}

/* End of symbol.c */
