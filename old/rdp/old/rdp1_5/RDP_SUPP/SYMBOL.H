/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* symbol.h - hash coded symbol table management
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef SYMBOL_H
#define SYMBOL_H

#include <stddef.h>

enum SYMBOL_FIND_OP{SYMBOL_NEW, SYMBOL_OLD, SYMBOL_ANY}; 

int symbol_compare_double(void * left, void * right); 
int symbol_compare_double_reverse(void * left, void * right); 
int symbol_compare_long(void * left, void * right); 
int symbol_compare_long_reverse(void * left, void * right); 
int symbol_compare_string(void * left, void * right); 
int symbol_compare_string_reverse(void * left, void * right); 

void * symbol_find(const void * table, void * key, size_t key_size, size_t symbol_size, void * scope, enum SYMBOL_FIND_OP op); 

void symbol_free_scope(const void * scope);  /* release all memory in a scope */
void symbol_free_symbol(void * symbol);  /* release memory allocated to symbol */
void symbol_free_table(void * table);  /* release all memory in a table */

void * symbol_get_scope(const void * table);  /* return current scope */

unsigned symbol_hash_double(unsigned hash_prime, void * data); 
unsigned symbol_hash_long(unsigned hash_prime, void * data); 
unsigned symbol_hash_mem(unsigned hash_prime, void * data); 
unsigned symbol_hash_string(unsigned hash_prime, void * data);  /* hash a null terminated string */

void * symbol_insert_key(const void * table, void * key, size_t key_size, size_t symbol_size);  /* lookup key in table. Return NULL if not found */
void * symbol_insert_symbol(const void * table, void * symbol);  /* insert a symbol at head of hash list */

void * symbol_lookup_key(const void * table, void * key, void * scope);  /* lookup key in table. Return NULL if not found */

void * symbol_new_scope(void * table, char * str);  /* make a new scope symbol */
void * symbol_new_symbol(size_t size);  /* make a new symbol */
void * symbol_new_table(char * name, 
const unsigned symbol_hashsize, 
const unsigned symbol_hashprime, 
int(* compare)(void * left_symbol, void * right_symbol), 
unsigned(* hash)(unsigned hash_prime, void * data), 
void(* print)(const void * symbol));  /* create a new symbol table */

void * symbol_next_symbol(void * table, void * symbol);  /* lookup along table from s for next identical key. Return NULL if not found */
void * symbol_next_symbol_in_scope(void * symbol);  /* Return next symbol in scope chain. Return NULL if at end */

void symbol_print_all_table(void);  /* diagnostic dump of all symbol tables */
void symbol_print_all_table_statistics(const unsigned histogram_size);  /* dump all bucket usage histograms */

void symbol_print_double(const void * symbol);  /* print a pointer to a long */
void symbol_print_long(const void * symbol);  /* print a pointer to a long */
void symbol_print_string(const void * symbol);  /* print a pointer to a string */

void symbol_print_scope(const void * table, void * scope);  /* diagnostic print of a complete scope */
void symbol_print_symbol(const void * table, const void * symbol);  /* print a single symbol */
void symbol_print_table(const void * table);  /* diagnostic dump of symbol table */
void symbol_print_table_statistics(const void * table, const unsigned histogram_size);  /* dump bucket usage histogram */

void symbol_set_scope(void * table, void * scope);  /* set new current scope */

void symbol_sort_table(void * table);  /* sort all scopes in a table into alphabetical order */
void symbol_sort_scope(void * table, void * scope);  /* sort scope into alphabetical order */

void symbol_unlink_scope(void * data);  /* remove an entire scope */
void symbol_unlink_symbol(void * data);  /* remove a symbol */
void symbol_unlink_table(void * table);  /* remove an entire table */

#endif

/* End of symbol.h */
