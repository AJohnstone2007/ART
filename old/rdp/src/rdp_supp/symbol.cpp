/*******************************************************************************
*
* RDP release 1.70 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 16 March 2012
*
* symbol.c - a hash coded symbol table
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "textio.h"
#include "memalloc.h"
#include "set.h"
#include "symbol.h"

/* define some shorthand casts */
#define TABLE ((symbol_table *) table)
#define SYMBOL ((symbol_ *) symbol)

typedef struct symbol_node
{
  struct symbol_node
  * next_hash,                /* next symbol in hash list */
  * * last_hash,              /* pointer to next pointer of last_symbol in hash list */
  * next_scope,               /* next symbol in scope list */
  * scope;                    /* pointer to the scope symbol */
  unsigned hash;              /* hash value for quick searching */
}symbol_;

typedef struct symbol_table_node
{
  char * name;                /* an identifying string */
  symbol_ * * table;          /* table of pointers to hash lists */
  symbol_ * current;          /* pointers to chain of scope lists */
  symbol_ * scopes;           /* pointer to first scope list */
  unsigned hash_size;         /* number of buckets in symbol table */
  unsigned hash_prime;        /* hashing prime: hashsize and hashprime should be coprime */
  int(* compare)(void * left, void * right);
  unsigned(* hash)(unsigned prime, void * data);
  void(* print)(const void * symbol);
  struct symbol_table_node * next;  /* pointer to last declared symbol table */
}symbol_table;

static symbol_table * symbol_tables = NULL;

int symbol_compare_double(void * left, void * right)
{
  if (*((double *) left)== *((double *) right))
    return 0;
  else if (*((double *) left)> *((double *) right))
    return 1;
  else
    return - 1; 
}

int symbol_compare_double_reverse(void * left, void * right)
{
  if (*((double *) left)== *((double *) right))
    return 0;
  else if (*((double *) left)> *((double *) right))
    return - 1;
  else
    return 1;
}

int symbol_compare_long(void * left, void * right)
{
  if (*((long *) left)== *((long *) right))
    return 0;
  else if (*((long *) left)> *((long *) right))
    return 1; 
  else
    return - 1;
}

int symbol_compare_long_reverse(void * left, void * right)
{
  if (*((long *) left)== *((long *) right))
    return 0;
  else if (*((long *) left)> *((long *) right))
    return - 1;
  else
    return 1; 
}

int symbol_compare_unsigned_long(void * left, void * right)
{
  if (*((unsigned long *) left)== *((unsigned long *) right))
    return 0;
  else if (*((unsigned long *) left)> *((unsigned long *) right))
    return 1;
  else
    return - 1;
}

int symbol_compare_unsigned_long_reverse(void * left, void * right)
{
  if (*((unsigned long *) left)== *((unsigned long *) right))
    return 0;
  else if (*((unsigned long *) left)> *((unsigned long *) right))
    return - 1;
  else
    return 1;
}

int symbol_compare_string4(void * left, void * right)
{
  struct compare_string4_struct {char *a; char *b; char *c; char *d;};

  struct compare_string4_struct *l = (struct compare_string4_struct *) left;
  struct compare_string4_struct *r = (struct compare_string4_struct *) right;

  int ret_value = strcmp(l->a, r->a);

  if (ret_value == 0) {
    ret_value = strcmp(l->b, r->b);

    if (ret_value == 0) {
      ret_value = strcmp(l->c, r->c);

      if (ret_value == 0) {
        ret_value = strcmp(l->d, r->d);
      }
    }
  }

  return ret_value;
}

int symbol_compare_integer_string_string(void * left, void * right)
{
  struct compare_integer_string_struct {int i; char *s; char *t;};

  int left_int = ((struct compare_integer_string_struct *) left)->i;
  int right_int = ((struct compare_integer_string_struct *) right)->i;
  char* left_str1 = ((struct compare_integer_string_struct *) left)->s;
  char* right_str1 = ((struct compare_integer_string_struct *) right)->s;
  char* left_str2 = ((struct compare_integer_string_struct *) left)->t;
  char* right_str2 = ((struct compare_integer_string_struct *) right)->t;
  int ret_value;

  if (left_int > right_int)
    ret_value = 1;
  else if (left_int < right_int)
    ret_value =  -1;
  else  /* integers are equal */
    if ((ret_value = strcmp(left_str1, right_str1)) == 0)
      ret_value = strcmp(left_str2, right_str2);

  return ret_value;
}

int symbol_compare_integer_string(void * left, void * right)
{
  struct compare_integer_string_struct {int i; char *s; };

  int left_int = ((struct compare_integer_string_struct *) left)->i;
  int right_int = ((struct compare_integer_string_struct *) right)->i;
  char* left_str = ((struct compare_integer_string_struct *) left)->s;
  char* right_str = ((struct compare_integer_string_struct *) right)->s;
  int ret_value;

  if (left_int > right_int)
    ret_value = 1;
  else if (left_int < right_int)
    ret_value =  -1;
  else  /* integers are equal */
    ret_value = strcmp(left_str, right_str);

  return ret_value;
}

int symbol_compare_integer_string_reverse(void * left, void * right)
{
  struct compare_integer_string_struct {int i; char *s; };

  int left_int = ((struct compare_integer_string_struct *) left)->i;
  int right_int = ((struct compare_integer_string_struct *) right)->i;
  if (left_int < right_int)
    return 1;
  else if (left_int > right_int)
    return -1;
  else  /* integers are equal */
  {
    char* left_str = ((struct compare_integer_string_struct *) left)->s;
    char* right_str = ((struct compare_integer_string_struct *) right)->s;

    return strcmp(right_str, left_str);
  }
}

/* integer pair lo:hi */
int symbol_compare_integer_pair(void * left, void * right)
{
  if (*(((int *) left) + 1) > *(((int *) right)+1))
    return 1;
  else if (*(((int *) left) + 1) < *(((int *) right)+1))
    return -1;
  else /* hi integers equal */
  {
    if (*((int *) left) > *((int *) right))
      return 1;
    else if (*((int *) left) < *((int *) right))
      return -1;
    else
      return 0;
  }
}

/* integer pair lo:hi */
int symbol_compare_integer_pair_reverse(void * left, void * right)
{
  if (*(((int *) left) + 1) > *(((int *) right)+1))
    return -1;
  else if (*(((int *) left) + 1) < *(((int *) right)+1))
    return 1;
  else /* hi integers equal */
  {
    if (*((int *) left) > *((int *) right))
      return -1;
    else if (*((int *) left) < *((int *) right))
      return 1;
    else
      return 0;
  }
}

int symbol_compare_integer_set(void * left, void * right)
{
  struct compare_integer_set_struct {int integer; set_ set;};

  struct compare_integer_set_struct *left_struct = (struct compare_integer_set_struct *) left;
  struct compare_integer_set_struct *right_struct = (struct compare_integer_set_struct *) right;
  unsigned char_index;
  unsigned minimum_length = left_struct->set.length < right_struct->set.length ? left_struct->set.length : right_struct->set.length;

  if (left_struct->integer > right_struct->integer)
    return 1;
  else if (left_struct->integer < right_struct->integer)
    return -1;

  for (char_index = 0; char_index < minimum_length; char_index++)
    if (left_struct->set.elements[char_index] > right_struct->set.elements[char_index])
      return 1;
    else if (left_struct->set.elements[char_index] < right_struct->set.elements[char_index])
      return -1;

  if (left_struct->set.length > minimum_length)
  {
    for (; char_index < left_struct->set.length; char_index++)
      if (left_struct->set.elements[char_index] != 0)
        return 1;
  }
  else if (right_struct->set.length > minimum_length)
  {
    for (; char_index < right_struct->set.length; char_index++)
      if (right_struct->set.elements[char_index] != 0)
        return -1;
  }

  return 0;
}

int symbol_compare_integer_pointer_to_set(void * left, void * right)
{
  struct compare_integer_set_struct {int integer; set_ *set;};

  struct compare_integer_set_struct *left_struct = (struct compare_integer_set_struct *) left;
  struct compare_integer_set_struct *right_struct = (struct compare_integer_set_struct *) right;
  unsigned char_index;
  unsigned minimum_length = left_struct->set->length < right_struct->set->length ? left_struct->set->length : right_struct->set->length;

  if (left_struct->integer > right_struct->integer)
    return 1;
  else if (left_struct->integer < right_struct->integer)
    return -1;

  for (char_index = 0; char_index < minimum_length; char_index++)
    if (left_struct->set->elements[char_index] > right_struct->set->elements[char_index])
      return 1;
    else if (left_struct->set->elements[char_index] < right_struct->set->elements[char_index])
      return -1;

  if (left_struct->set->length > minimum_length)
  {
    for (; char_index < left_struct->set->length; char_index++)
      if (left_struct->set->elements[char_index] != 0)
        return 1;
  }
  else if (right_struct->set->length > minimum_length)
  {
    for (; char_index < right_struct->set->length; char_index++)
      if (right_struct->set->elements[char_index] != 0)
        return -1;
  }

  return 0;
}

int symbol_compare_set(void * left, void * right)
{
  return set_compare_set((set_ *) left, (set_ *) right);
}

int symbol_compare_set_reverse(void * left, void * right)
{
  return set_compare_set((set_ *) right, (set_ *) left);
}

int symbol_compare_set_pointer_set_pointer(void * left, void * right)
{
  set_ * left_set_1 = *((set_ **) left);
  set_ * right_set_1 = *((set_ **) right);
  set_ * left_set_2 = *(((set_ **) left)+1);
  set_ * right_set_2 = *(((set_ **) right)+1);

  unsigned char_index;
  unsigned minimum_length;

  /* Check against set 1 */
  minimum_length = left_set_1->length < right_set_1->length ? left_set_1->length : right_set_1->length;

  for (char_index = 0; char_index < minimum_length; char_index++)
    if (left_set_1->elements[char_index] > right_set_1->elements[char_index])
      return 1;
    else if (left_set_1->elements[char_index] < right_set_1->elements[char_index])
      return -1;

  if (left_set_1->length > minimum_length)
  {
    for (; char_index < left_set_1->length; char_index++)
      if (left_set_1->elements[char_index] != 0)
        return 1;
  }
  else if (right_set_1->length > minimum_length)
  {
    for (; char_index < right_set_1->length; char_index++)
      if (right_set_1->elements[char_index] != 0)
        return -1;
  }

  /* Now check against set 2 */
  minimum_length = left_set_2->length < right_set_2->length ? left_set_2->length : right_set_2->length;

  for (char_index = 0; char_index < minimum_length; char_index++)
    if (left_set_2->elements[char_index] > right_set_2->elements[char_index])
      return 1;
    else if (left_set_2->elements[char_index] < right_set_2->elements[char_index])
      return -1;

  if (left_set_2->length > minimum_length)
  {
    for (; char_index < left_set_2->length; char_index++)
      if (left_set_2->elements[char_index] != 0)
        return 1;
  }
  else if (right_set_2->length > minimum_length)
  {
    for (; char_index < right_set_2->length; char_index++)
      if (right_set_2->elements[char_index] != 0)
        return -1;
  }

  return 0;
}

int symbol_compare_set_pointer_set_pointer_reverse(void * left, void * right)
{
  set_ * left_set_1 = *((set_ **) left);
  set_ * right_set_1 = *((set_ **) right);
  set_ * left_set_2 = *(((set_ **) left)+1);
  set_ * right_set_2 = *(((set_ **) right)+1);

  unsigned char_index;
  unsigned minimum_length;

  /* Check against set 1 */
  minimum_length = left_set_1->length < right_set_1->length ? left_set_1->length : right_set_1->length;

  for (char_index = 0; char_index < minimum_length; char_index++)
    if (left_set_1->elements[char_index] > right_set_1->elements[char_index])
      return -1;
    else if (left_set_1->elements[char_index] < right_set_1->elements[char_index])
      return 1;

  if (left_set_1->length > minimum_length)
  {
    for (; char_index < left_set_1->length; char_index++)
      if (left_set_1->elements[char_index] != 0)
        return -1;
  }
  else if (right_set_1->length > minimum_length)
  {
    for (; char_index < right_set_1->length; char_index++)
      if (right_set_1->elements[char_index] != 0)
        return 1;
  }

  /* Now check against set 2 */
  minimum_length = left_set_2->length < right_set_2->length ? left_set_2->length : right_set_2->length;

  for (char_index = 0; char_index < minimum_length; char_index++)
    if (left_set_2->elements[char_index] > right_set_2->elements[char_index])
      return -1;
    else if (left_set_2->elements[char_index] < right_set_2->elements[char_index])
      return 1;

  if (left_set_2->length > minimum_length)
  {
    for (; char_index < left_set_2->length; char_index++)
      if (left_set_2->elements[char_index] != 0)
        return -1;
  }
  else if (right_set_2->length > minimum_length)
  {
    for (; char_index < right_set_2->length; char_index++)
      if (right_set_2->elements[char_index] != 0)
        return 1;
  }

  return 0;
}

int symbol_compare_string(void * left, void * right)
{
  char * left_str = *(char * *) left;
  char * right_str = *(char * *) right;

  return strcmp(left_str, right_str);
}

int symbol_compare_set_array(void * left, void * right)
{
  unsigned * left_str = *(unsigned * *) left;
  unsigned * right_str = *(unsigned * *) right;

  /* Check for nulls */
  if (left_str == NULL && right_str == NULL)
    return 0;

  if (left_str == NULL)
    return -1;

  if (right_str == NULL)
    return 1;

  /* scan strings until end or difference */
  while (*left_str != SET_END && *right_str != SET_END)
  {
    if (*left_str < *right_str)
      return -1;

    if (*left_str > *right_str)
      return 1;
  }

  /* Check for end of string termination */
  if (*left_str == *right_str)
    return 0;

  if (*left_str == SET_END)
    return -1;
  else
    return 1;
}

int symbol_compare_set_array_reverse(void * left, void * right)
{
  unsigned * left_str = *(unsigned * *) left;
  unsigned * right_str = *(unsigned * *) right;

  /* Check for nulls */
  if (left_str == NULL && right_str == NULL)
    return 0;

  if (left_str == NULL)
    return 1;

  if (right_str == NULL)
    return -1;

  /* scan strings until end or difference */
  while (*left_str != SET_END && *right_str != SET_END)
  {
    if (*left_str < *right_str)
      return 1;

    if (*left_str > *right_str)
      return -1;
  }

  /* Check for end of string termination */
  if (*left_str == *right_str)
    return 0;

  if (*left_str == SET_END)
    return 1;
  else
    return -1;
}

int symbol_compare_string_reverse(void * left, void * right)
{
  char * left_str = *(char * *) left;
  char * right_str = *(char * *) right;

  return strcmp(right_str, left_str);
}

void * symbol_find(const void * table, void * key, size_t key_size, size_t symbol_size, void * scope, enum SYMBOL_FIND_OP op)
{
  void * temp;

  switch (op)
  {
    case SYMBOL_ANY:
    if ((temp = symbol_lookup_key(table, key, scope))== NULL)
      return symbol_insert_key(table, key, key_size, symbol_size);
    else
      return temp;

    case SYMBOL_NEW:
    if ((temp = symbol_lookup_key(table, key, scope))!= NULL)
    {
      text_message(TEXT_ERROR_ECHO, "Doubly defined symbol \'");
      symbol_print_symbol(table, temp);
      text_printf("\'\n");
      return temp;
    }
    else
      return symbol_insert_key(table, key, key_size, symbol_size);

    case SYMBOL_OLD:
    if ((temp = symbol_lookup_key(table, key, scope))== NULL)
    {
      temp = symbol_insert_key(table, key, key_size, symbol_size);
      text_message(TEXT_ERROR_ECHO, "Undefined symbol \'");
      symbol_print_symbol(table, temp);
      text_printf("\'\n");
    }
    return temp;

    default:
    text_message(TEXT_FATAL, "internal error: illegal opcode supplied to symbol_find()");
    return NULL;              /* actually this will never be executed, of course */
  }
}

void symbol_free_scope(const void * symbol)  /* release all memory in a scope */
{
  /* Incomplete */
  symbol_ * sym = SYMBOL - 1;

  symbol_free_symbol(sym->scope+1);
}

void symbol_free_symbol(void * symbol)
{
  /* What about unlinking the scope chain? */
  symbol_ * sym = SYMBOL - 1;

  /* first unlink: code copied from symbol_unlink with extra check */
  if (sym->last_hash != NULL)
    *(sym->last_hash)= sym->next_hash;  /* point previous pointer to next symbol */

  if (sym->next_hash != NULL) /* if this isn't the end of the list */
    sym->next_hash->last_hash = sym->last_hash;

  mem_free(sym);              /* free the memory */
}

void symbol_free_hash_chain(symbol_ *sym)
{
  if (sym != NULL)
  {
    symbol_free_hash_chain(sym->next_hash);
    mem_free(sym);
  }
}

void symbol_free_table(void * table) /* release all memory in a table */
{
  unsigned index;

  if (table == NULL)
    return;
    
  /* kill symbol list on each hash bucket */
  for (index = 0; index < TABLE->hash_size; index++)
      symbol_free_hash_chain(TABLE->table[index]);

  /* kill scope chain */
  symbol_free_hash_chain(TABLE->scopes);

  /* kill table structure */
  mem_free(table);
}

void * symbol_get_scope_of_symbol(const void * symbol)
{
  symbol_ * sym = SYMBOL - 1;

  return sym->scope;
}

void * symbol_get_scope(const void * table) /* return current scope */
{
  return TABLE->current + 1;
}

unsigned symbol_hash_mem(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = 0;
  unsigned count = *(unsigned *) data;
  char * string = *(char * *)((unsigned *) data + 1);

  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    hashnumber = * string++ + hash_prime * hashnumber;
  return hashnumber;
}

unsigned symbol_hash_integer_set(unsigned hash_prime, void * data)
{                             /* hash a string */
  struct compare_integer_set_struct {int integer; set_ set;};

  struct compare_integer_set_struct *data_struct = (struct compare_integer_set_struct *) data;

  unsigned hashnumber = data_struct->integer;
  unsigned count = data_struct->set.length;
  unsigned char *string = data_struct->set.elements;

  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    {
      if (*string != 0)
        hashnumber = * string + hash_prime * hashnumber;

      string++;
    }

  return hashnumber;
}

unsigned symbol_hash_integer_pointer_to_set(unsigned hash_prime, void * data)
{                             /* hash a string */
  struct compare_integer_set_struct {int integer; set_ *set;};

  struct compare_integer_set_struct *data_struct = (struct compare_integer_set_struct *) data;

  unsigned hashnumber = data_struct->integer;
  unsigned count = data_struct->set->length;
  unsigned char *string = data_struct->set->elements;

  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    {
      if (*string != 0)
        hashnumber = * string + hash_prime * hashnumber;

      string++;
    }

  return hashnumber;
}

unsigned symbol_hash_set(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = 0;
  set_ * data_set = (set_ *) data;
  unsigned count = data_set->length;
  unsigned char *string = data_set->elements;

  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    {
      if (*string != 0)
        hashnumber = * string + hash_prime * hashnumber;

      string++;
    }

  return hashnumber;
}

unsigned symbol_hash_set_pointer_set_pointer(unsigned hash_prime, void * data)
{
  unsigned hashnumber = 0;
  set_ * data_set_1 = *((set_ **) data);
  set_ * data_set_2 = *((set_ **) data + 1);

  unsigned count;
  unsigned char *string;

  /* hash first set */
  count = data_set_1->length;
  string = data_set_1->elements;

  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    {
      if (*string != 0)
        hashnumber = * string + hash_prime * hashnumber;

      string++;
    }

  /* hash second set */
  count = data_set_2->length;
  string = data_set_2->elements;

  if (string != NULL)         /* Don't try and scan a vacuous string */
    while (count-- > 0)       /* run up string */
    {
      if (*string != 0)
        hashnumber = * string + hash_prime * hashnumber;

      string++;
    }

  return hashnumber;
}

unsigned symbol_hash_string(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = 0;
  char * str = *(char * *) data;

  if (str != NULL)            /* Don't try and scan a vacuous string */
    while (* str != 0)        /* run up string */
    hashnumber = * str++ + hash_prime * hashnumber;
  return hashnumber;
}

unsigned symbol_hash_set_array(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = 0;
  unsigned * str = *(unsigned * *) data;

  if (str != NULL)            /* Don't try and scan a vacuous string */
    while (* str != SET_END)        /* run up string */
    hashnumber = * str++ + hash_prime * hashnumber;
  return hashnumber;
}

unsigned symbol_hash_string4(unsigned hash_prime, void * data)
{                             /* hash a string */
  struct compare_string4_struct {char *a; char *b; char *c; char *d;} *x = (struct compare_string4_struct *) data;

  unsigned hashnumber = 0;

  if (x->a != NULL)            /* Don't try and scan a vacuous string */
  for (char *tmp = x->a; *tmp != 0; tmp++)
    hashnumber = * tmp + hash_prime * hashnumber;

  if (x->b != NULL)            /* Don't try and scan a vacuous string */
  for (char *tmp = x->b; *tmp != 0; tmp++)
    hashnumber = * tmp + hash_prime * hashnumber;

  if (x->c != NULL)            /* Don't try and scan a vacuous string */
  for (char *tmp = x->c; *tmp != 0; tmp++)
    hashnumber = * tmp + hash_prime * hashnumber;

  if (x->d != NULL)            /* Don't try and scan a vacuous string */
  for (char *tmp = x->d; *tmp != 0; tmp++)
    hashnumber = * tmp + hash_prime * hashnumber;

  return hashnumber;
}

unsigned symbol_hash_integer_string(unsigned hash_prime, void * data)
{                             /* hash a string */
  struct compare_integer_string_struct {int i; char *s; };

  unsigned hashnumber = ((struct compare_integer_string_struct *) data)->i;
  char * str = ((struct compare_integer_string_struct *) data)->s;

  if (str != NULL)            /* Don't try and scan a vacuous string */
    while (* str != 0)        /* run up string */
    hashnumber = * str++ + hash_prime * hashnumber;
  return hashnumber;
}

unsigned symbol_hash_integer_string_string(unsigned hash_prime, void * data)
{                             /* hash a string */
  struct compare_integer_string_struct {int i; char *s; char *t;};

  unsigned hashnumber = ((struct compare_integer_string_struct *) data)->i;
  char * str = ((struct compare_integer_string_struct *) data)->s;

  if (str != NULL)            /* Don't try and scan a vacuous string */
    while (* str != 0)        /* run up string */
    hashnumber = * str++ + hash_prime * hashnumber;

  str = ((struct compare_integer_string_struct *) data)->t;

  if (str != NULL)            /* Don't try and scan a vacuous string */
    while (* str != 0)        /* run up string */
    hashnumber = * str++ + hash_prime * hashnumber;

  return hashnumber;
}

unsigned symbol_hash_double(unsigned hash_prime, void * data)
{                             /* hash a string */
  return(unsigned)((long) hash_prime * *((long *) data));
}

unsigned symbol_hash_long(unsigned hash_prime, void * data)
{                             /* hash a string */
  return(unsigned)((long) hash_prime * *((long *) data));
}

unsigned symbol_hash_integer_pair(unsigned hash_prime, void * data)
{                             /* hash a string */
  unsigned hashnumber = 0;

  hashnumber = *((int *) data) + hash_prime * hashnumber;
  hashnumber = *(((int *) data) + 1) + hash_prime * hashnumber;

  return hashnumber;
}

unsigned symbol_hash_unsigned_long(unsigned hash_prime, void * data)
{
  return(unsigned)((unsigned long) hash_prime * *((unsigned long *) data));
}

void * symbol_insert_key(const void * table, void * key, size_t key_size, size_t symbol_size) /* lookup key in table. Return NULL if not found */
{
  void * sym = symbol_new_symbol(symbol_size);

  if (key_size > symbol_size)
    text_message(TEXT_FATAL, "programmer error - key_size > symbol_size in symbol_insert_key\n");

  memcpy(sym, key, key_size);

  return symbol_insert_symbol(table, sym);
}

void * symbol_insert_symbol(const void * table, void * symbol) /* insert a symbol at head of hash list */
{
  unsigned hash_index;
  symbol_ * s = SYMBOL - 1;

  s->hash =(* TABLE->hash)(TABLE->hash_prime, symbol);
  hash_index = s->hash % TABLE->hash_size;

  s->next_hash = TABLE->table[hash_index];  /* point s at old hash list head */
  TABLE->table[hash_index]= s;  /* point hash list at s */

  s->last_hash = & TABLE->table[hash_index];  /* point last hash at index pointer */

  if (s->next_hash != NULL)   /* if this wasn't the start of a new list ... */
    (s->next_hash)->last_hash = &(s->next_hash);  /* ...point old list next back at s */

  /* now insert in scope list */
  s->next_scope = TABLE->current->next_scope;
  TABLE->current->next_scope = s;

  s->scope = TABLE->current + 1;  /* set up pointer to scope block */

  return symbol;
}

void * symbol_lookup_key(const void * table, void * key, void * scope) /* lookup a symbol by id. Return NULL is not found */
{
  unsigned hash =(* TABLE->hash)(TABLE->hash_prime, key);
  symbol_ * p = TABLE->table[hash % TABLE->hash_size];

  /* look for symbol with same hash and a true compare */
  while (!(p == NULL ||
           (((* TABLE->compare)(key, p + 1)== 0) && !(p->scope != scope && scope != NULL))
           )
        )
{
    p = p->next_hash;
}

  if (p == NULL) return NULL;  /* Not found at all */

  return p + 1;
}

void * symbol_new_scope(void * table, char * id)
{
  symbol_scope_data * p =(symbol_scope_data *) symbol_new_symbol(sizeof(symbol_scope_data));  /* create new scope element */
  symbol_ * s =(symbol_ *) p - 1;

  p->id = id;
  s->next_hash = TABLE->scopes;
  TABLE->current = TABLE->scopes = s;
  if (s->next_hash != NULL)
    (s->next_hash)->last_hash = &(s->next_hash);
  return p;
}

void * symbol_new_symbol(size_t size) /* allocate a new symbol node */
{                             /* allocate space: crash if heap is full */
  void * temp1 = mem_calloc(sizeof(symbol_)+ size, 1);
  void * temp2 =(symbol_ *) temp1 + 1;

  return temp2;
}

void * symbol_new_table(char * name,
const unsigned symbol_hashsize,
const unsigned symbol_hashprime,
int(* compare)(void * left, void * right),
unsigned(* hash)(unsigned hash_prime, void * symbol),
void(* print)(const void * symbol)) /* create a new symbol table */
{
  symbol_table * temp =(symbol_table *) mem_malloc(sizeof(symbol_table));
  symbol_scope_data * scope =(symbol_scope_data *) symbol_new_symbol(sizeof(symbol_scope_data));
  unsigned count;

  scope->id = "";
  temp->name = name; 
  temp->hash_size = symbol_hashsize; 
  temp->hash_prime = symbol_hashprime;
  temp->compare = compare; 
  temp->hash = hash; 
  temp->print = print; 
  temp->table =(symbol_ * *) mem_malloc(symbol_hashsize * sizeof(symbol_ *));

  for (count = 0; count < symbol_hashsize; count++)
    temp->table[count]= NULL;
  
  temp->current = temp->scopes =(symbol_ *) scope - 1;

  /* now hook into list of tables */
  temp->next = symbol_tables; 
  symbol_tables = temp; 
  
  return temp;
}

void * symbol_next_symbol(void * table, void * symbol) /* lookup along table from s for next identical ID. Return NULL if not found */
{
  symbol_ * p = SYMBOL - 1;

  p = p->next_hash;
  while (p != NULL &&(* TABLE->compare)(symbol, p + 1)!= 0)
    p = p->next_hash;

  return p == NULL ? p: p + 1;
}

void * symbol_next_symbol_in_scope(void * symbol) /* Return next symbol in scope chain. Return NULL if at end */
{
  symbol_ * s =(symbol_ *)(((symbol_ *) symbol - 1)->next_scope);

  return s == NULL ? NULL: s + 1;
}

void symbol_print_all_table(void) /* diagnostic dump of all symbol tables */
{
  symbol_table * temp = symbol_tables;

  while (temp != NULL)
  {
    symbol_print_table(temp);
    temp = temp->next;
  }
}

void symbol_print_all_table_statistics(const unsigned histogram_size) /* dump all bucket usage histograms */
{
  symbol_table * temp = symbol_tables;

  while (temp != NULL)
  {
    symbol_print_table_statistics(temp, histogram_size);
    temp = temp->next;
  }
}

void symbol_print_scope(const void * table, void * sym)
{
  symbol_scope_data *this_scope = (symbol_scope_data *) sym;
  sym = symbol_next_symbol_in_scope(sym);

  text_printf("*** Start scope '%s'\n", this_scope->id);
  while (sym != NULL)
  {
    (* TABLE->print)(sym);
    text_printf("\n");

    sym = symbol_next_symbol_in_scope(sym);
  }
  text_printf("*** End scope '%s'\n", this_scope->id);
}

void symbol_print_string(const void * symbol)
{
  text_printf("%s", symbol == NULL ? "Null symbol": *((char * *) symbol));
}

void symbol_print_symbol(const void * table, const void * symbol)
{
  (* TABLE->print)(symbol);
}

void symbol_print_table(const void * table) /* dump symbol table */
{                             /* diagnostic dump of symbol table */
  unsigned temp;
  symbol_ * p = TABLE->scopes;

  text_message(TEXT_INFO, "Symbol table \'%s\': dump by hash bucket\n\n", TABLE->name);
  for (temp = 0; temp < TABLE->hash_size; temp++)
  {
    symbol_ * p = TABLE->table[temp];

    if (p != NULL)
    {
      text_printf("*** Bucket %u:\n", temp);
      while (p != NULL)
      {
        (* TABLE->print)(p + 1);
        text_printf("\n");
        p = p->next_hash;
      }
    }
  }
  text_printf("\n");

  text_message(TEXT_INFO, "Symbol table \'%s\' dump by scope chain\n", TABLE->name);
  while (p != NULL)
  {
    symbol_print_scope(table, p + 1);
    p = p->next_hash;
  }
  text_printf("\n");
}

void symbol_print_table_statistics(const void * table, const unsigned histogram_size) /* dump symbol statistics */
{                             /* diagnostic dump of symbol table */
  unsigned temp_unsigned;
  int symbols = 0,
  mean = 0;

  int * histogram =(int *) mem_calloc(histogram_size, sizeof(int));

  for (temp_unsigned = 0; temp_unsigned < TABLE->hash_size; temp_unsigned++)
  {
    symbol_ * p = TABLE->table[temp_unsigned];
    unsigned count = 0;

    while (p != NULL)
    {
      symbols++;
      count++;
      p = p->next_hash;
    }
    histogram[count < histogram_size - 1 ? count: histogram_size - 1]++;  /* bump histogram bucket */
  }

  text_message(TEXT_INFO, "\nSymbol table `%s\' has %u buckets and contains %i symbols\n", TABLE->name, TABLE->hash_size, symbols);

  /* Now calculate mean utilisation */

  for (temp_unsigned = 0; temp_unsigned < histogram_size; temp_unsigned++)
  {
    text_printf("%3i bucket%s %i symbol%s\n",
    histogram[temp_unsigned], histogram[temp_unsigned]== 1 ? "  has ": "s have",
    temp_unsigned, temp_unsigned == 1 ? "": temp_unsigned == histogram_size - 1 ? "s or more": "s");
    mean +=(temp_unsigned + 1)* histogram[temp_unsigned];
  }
  text_message(TEXT_INFO, "\nMean list length is %f\n", ((float) mean /(float) TABLE->hash_size)- 1);

  mem_free(histogram);        /* release memory used for histogram */
}

void symbol_print_integer_pair(const void * symbol)
{
  if (symbol == NULL)
    text_printf("Null symbol");
  else
    text_printf("%i, %i", *((int *) symbol), *(((int *) symbol) + 1));
}

void symbol_print_integer_string(const void * symbol)
{
  struct compare_integer_string_struct {int i; char *s; };

  int symbol_i = ((struct compare_integer_string_struct *) symbol)->i;
  char* symbol_s = ((struct compare_integer_string_struct *) symbol)->s;

  if (symbol == NULL)
    text_printf("Null symbol");
  else
    text_printf("%i, %s", symbol_i, symbol_s);
}

void symbol_print_integer_string_string(const void * symbol)
{
  struct compare_integer_string_struct {int i; char *s; char *t;};

  int symbol_i = ((struct compare_integer_string_struct *) symbol)->i;
  char* symbol_s = ((struct compare_integer_string_struct *) symbol)->s;
  char* symbol_t = ((struct compare_integer_string_struct *) symbol)->t;

  if (symbol == NULL)
    text_printf("Null symbol");
  else
    text_printf("%i, %s, %s", symbol_i, symbol_s, symbol_t);
}

void symbol_print_string4(const void * symbol)
{
  struct compare_string4_struct {char *a; char *b; char *c; char *d;} *x = (struct compare_string4_struct *) symbol;

  text_printf("%s, %s, %s, %s", x->a, x->b, x->c, x->d);
}

void symbol_print_double(const void * symbol)
{
  if (symbol == NULL)
    text_printf("Null symbol");
  else
    text_printf("%lf", *((double *) symbol));
}

void symbol_print_unsigned_long(const void * symbol)
{
  if (symbol == NULL)
    text_printf("Null symbol");
  else
    text_printf("%lu", *((long *) symbol));
}

void symbol_print_integer_set(const void * symbol)
{
  struct compare_integer_set_struct {int integer; set_ set;};

  struct compare_integer_set_struct *symbol_struct = (struct compare_integer_set_struct*) symbol;

  if (symbol == NULL)
    text_printf("Null symbol");
  else
  {
    text_printf("%i, ", symbol_struct->integer);
    set_print_set(&(symbol_struct->set), NULL, 0);
  }
}

void symbol_print_integer_pointer_to_set(const void * symbol)
{
  struct compare_integer_set_struct {int integer; set_ *set;};

  struct compare_integer_set_struct *symbol_struct = (struct compare_integer_set_struct*) symbol;

  if (symbol == NULL)
    text_printf("Null symbol");
  else
  {
    text_printf("%i, ", symbol_struct->integer);
    set_print_set(symbol_struct->set, NULL, 0);
  }
}

void symbol_print_set(const void * symbol)
{
  if (symbol == NULL)
    text_printf("Null symbol");
  else
    set_print_set((set_ *) symbol, NULL, 0);
}

void symbol_print_set_array(const void * symbol)
{
  if (symbol == NULL)
    text_printf("Null symbol");
  else
    set_print_set_array((unsigned *) symbol, NULL, 0);
}

void symbol_print_set_pointer_set_pointer(const void * symbol)
{
  if (symbol == NULL)
    text_printf("Null symbol");
  else
  {
    text_printf("( ");
    set_print_set(*((set_ **) symbol), NULL, 0);
    text_printf(" : ");
    set_print_set(*((set_ **) symbol + 1), NULL, 0);
    text_printf(" )");
  }
}

void symbol_set_scope(void * table, void * symbol)
{
  symbol_ * s = SYMBOL - 1;

  TABLE->current = s;
}

void symbol_set_scope_name(void * table, char* name)
{
  ((symbol_scope_data*) symbol_get_scope(table))->id = name;
}

/* Sort a scope region. Don't change positions in the hash table:
   just move pointers in the scope chain */
void symbol_sort_scope(void * table, void * symbol)
{
  typedef struct sort_list_type
  {
    symbol_ * s; 
    struct sort_list_type * next; 
    struct sort_list_type * * last; 
  }sort_list; 
  
  symbol_ * s = SYMBOL - 1; 
  
  sort_list * sorted =(sort_list *) mem_malloc(sizeof(sort_list)); 
  sort_list * temp_sorted; 
  symbol_ * temp_scope;
  
  if (s->next_scope == NULL)  /* attempt to sort empty list */
    return; 
  
  if (s->next_scope->next_scope == NULL) /* attempt to sort list of one */
    return; 
  
  /* put first symbol in scope on sorted list */
  sorted->s = s->next_scope; 
  sorted->last = & sorted; 
  
  /* put a sentinel on the end */
  sorted->next =(sort_list *) mem_calloc(sizeof(sort_list), 1); 
  sorted->next->last = & sorted->next; 
  
  /* first create sorted list by insertion sort on sort_list */
  temp_scope = s->next_scope->next_scope;  /* point temp_scope at second element */
  while (temp_scope != NULL)  /* iterate over entire scope list */
  {
    sort_list * new_sorted; 
    
    /* find place in sorted list for insertion */
    temp_sorted = sorted; 
    while (temp_sorted->next != NULL)
    {
      if ((* TABLE->compare)(temp_scope + 1, temp_sorted->s + 1)< 0)
        break;
      temp_sorted = temp_sorted->next; 
    }
    
    /* now insert new sorted node before the one pointed to by temp_sorted */
    new_sorted =(sort_list *) mem_malloc(sizeof(sort_list));  /* create node */
    new_sorted->s = temp_scope;  /* point at current scope symbol */
    new_sorted->next = temp_sorted;  /* look forwards... */
    new_sorted->last = temp_sorted->last;  /* and backwards */
    *(temp_sorted->last)= new_sorted;  /* make temp's predecessor look at us */
    temp_sorted->last = & new_sorted->next;  /* and make temp look at us */
    
    temp_scope = temp_scope->next_scope;  /* step to next element in scope list */
  }
  
  /* now rebuild scope pointers in scope range: the list should be the right length already! */
  temp_sorted = sorted; 
  temp_scope = s; 
  while (temp_sorted != NULL) /* iterate over sorted list */
  {
    sort_list * free_sorted = temp_sorted; 
    
    temp_scope->next_scope = temp_sorted->s;  /* point next scope to this sorted symbol */
    temp_sorted = temp_sorted->next;  /* move to next sorted symbol */
    mem_free(free_sorted);    /* free as we go */
    temp_scope = temp_scope->next_scope;  /* move to next sorted scope element */
  }
}

void symbol_unlink_scope(void * symbol)
{
  symbol_ * s = SYMBOL - 1;

  s = s->next_scope;

  while (s != NULL)
  {
    symbol_unlink_symbol(s + 1);
    s = s->next_scope;
  }
}

void symbol_unlink_symbol(void * symbol) /* remove a symbol from the hash chain */
{
  symbol_ * s = SYMBOL - 1;

  *(s->last_hash)= s->next_hash;  /* point previous pointer to next symbol */

  if (s->next_hash != NULL)   /* if this isn't the end of the list */
    s->next_hash->last_hash = s->last_hash;
}

/* End of symbol.c */
