/*****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* symbol.h - hash coded symbol table management
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************/
#ifndef SYMBOL_H
#define SYMBOL_H

#include <stddef.h>

int symbol__compare_string(void *left, void *right);

void symbol__free_scope(const void *scope); /* release all memory in a scope */
void symbol__free_symbol(void *symbol); /* release memory allocated to symbol */
void symbol__free_table(void *table); /* release all memory in a table */

void *symbol__get_scope(const void *table); /* return current scope */

unsigned symbol__hash_mem(unsigned hash_prime, void *data);
unsigned symbol__hash_string(unsigned hash_prime, void *data);  /* hash a null terminated string */

void *symbol__insert_string_key(const void *table, char *str, size_t size); /* lookup key in table. Return NULL if not found */
void *symbol__insert_symbol(const void *table, void *symbol);  /* insert a symbol at head of hash list */

void *symbol__lookup_key(const void *table, void *key); /* lookup key in table. Return NULL if not found */

void *symbol__new_scope(void *table, char *str);  /* make a new scope symbol */
void *symbol__new_symbol(size_t size);  /* make a new symbol */
void *symbol__new_table(char *name,
			 const unsigned symbol__hashsize,
			 const unsigned symbol__hashprime,
			 int (*compare) (void *left_symbol, void *right_symbol),
			 unsigned (*hash) (unsigned hash_prime, void *data),
			 void (*print) (const void *symbol)); /* create a new symbol table */

void *symbol__next_symbol(void *table, void *symbol); /* lookup along table from s for next identical key. Return NULL if not found */
void *symbol__next_symbol_in_scope(void *symbol); /* Return next symbol in scope chain. Return NULL if at end */

void symbol__print_all_table(void);  /* diagnostic dump of all symbol tables */
void symbol__print_all_table_statistics(const int histogram_size); /* dump all bucket usage histograms */

void symbol__print_string(const void *symbol);  /* print a pointer to a string */

void symbol__print_scope(const void *table, void *scope);  /* diagnostic print of a complete scope */
void symbol__print_symbol(const void *table, const void *symbol);  /* print a single symbol */
void symbol__print_table(const void *table);  /* diagnostic dump of symbol table */
void symbol__print_table_statistics(const void *table, const int histogram_size); /* dump bucket usage histogram */

void symbol__set_scope(void *table, void *scope); /* set new current scope */

void symbol__sort_table(void *table); /* sort all scopes in a table into alphabetical order */
void symbol__sort_scope(void *table, void *scope); /* sort scope into alphabetical order */

void symbol__unlink_scope(void *data);  /* remove an entire scope */
void symbol__unlink_symbol(void *data); /* remove a symbol */
void symbol__unlink_table(void *table); /* remove an entire table */

#endif

/* End of symbol.h */
