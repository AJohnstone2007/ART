/****************************************************************************
*
* RDP release 1.20 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 6 Nov 1994
*
* symbol.h - a hash coded symbol table
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef SYMBOL_H
#define SYMBOL_H

#include "params.h"

typedef struct symbol__node
{
  char *id;                     /* identifier string */
  int token;                    /* scanner result type */
  union
  {
    character c;
    integer i;
    unsigned_integer u;
    real r;
    void *p;                    /* pointer to anything larger */
  } data;
  struct symbol__node
  *next_hash,                   /* next symbol in hash list */
  *last_hash,                   /* last_symbol in hash list */
  *next_scope,                  /* next symbol in scope list */
  *last_scope,                  /* last symbol in scope list */
  *scope;                       /* scope symbol */
} symbol__;

void symbol__init(unsigned symbol__hashsize, unsigned symbol__hashprime);
void symbol__insert(symbol__ * s);  /* insert s in table */
symbol__ *symbol__insert_id(const char *id);  /* make a symbol for id and insert */
symbol__ *symbol__lookup_id(const char *s); /* lookup s in table. Return NULL if not found */
symbol__ *symbol__lookup_id_local(const char *s); /* lookup a symbol by id. Return NULL if not found in current scope */
symbol__ *symbol__lookup_id_scope(const char *s, symbol__ * scope); /* lookup a symbol by id. Return NULL if not found in scope */
symbol__ *symbol__new(void);    /* make a new symbol */
symbol__ *symbol__new_scope(const char *s); /* make a new scope symbol */
void symbol__scope_invisible(symbol__ * s); /* hide a scope */
void symbol__print_statistics(void);  /* dump bucket usage histogram */
void symbol__print_symbol(symbol__ * p);  /* diagnostic print of a single symbol */
void symbol__print_symboltable(void); /* dump symbol table */
void symbol__scope_visible(symbol__ * s); /* reveal a scope */
void symbol__set_scope(symbol__ * s); /* use s scope base */
void symbol__sort_scope(symbol__ * s);  /* sort scope into alphabetical order */

#endif

/* End of symbol.h */
