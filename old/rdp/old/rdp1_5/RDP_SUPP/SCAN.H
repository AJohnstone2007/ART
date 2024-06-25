/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* scan.h - a programmable scanner
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef SCAN_H
#define SCAN_H

#include <stdio.h>
#include <string.h>
#include "set.h"
#include "symbol.h"

#define SCAN_DATA   \
char * id; \
int token; \
int extended; \
union{ \
  long unsigned u; \
  long int i; \
  double r; \
  void * p; \
}data; 

#define SCAN_CAST ((scan_data*) text_scan_data)

typedef long int integer; 

extern char * scan_token_names; 
extern void * scan_table; 

typedef struct
{
  SCAN_DATA
}scan_data; 

extern int scan_case_insensitive;  /* this will dissapear at V2.00 */
extern int scan_show_skips; 
extern int scan_newline_visible;  /* this will dissapear at V2.00 */
extern int scan_symbol_echo; 
extern long unsigned scan_derivation_step; 

/* Warning: if you change this list, the routine rdp_make_token_string in
   rdp_prnt.c must also be updated to match in two places! */
enum scan_primitive_type
{
  SCAN_P_IGNORE, SCAN_P_ID, SCAN_P_INTEGER, SCAN_P_REAL, 
  SCAN_P_CHAR, SCAN_P_CHAR_ESC, 
  SCAN_P_STRING, SCAN_P_STRING_ESC, 
  SCAN_P_COMMENT, SCAN_P_COMMENT_VISIBLE, 
  SCAN_P_COMMENT_NEST, SCAN_P_COMMENT_NEST_VISIBLE, 
  SCAN_P_COMMENT_LINE, SCAN_P_COMMENT_LINE_VISIBLE, 
  SCAN_P_EOF, SCAN_P_EOLN, SCAN_P_TOP
}; 

void scan_(void); 
unsigned long scan_column_number(void); 
void scan_init(const int case_insensitive, const int newline_visible, const int show_skips, const int symbol_echo, char * token_names); 
unsigned long scan_line_number(void); 
void scan_load_keyword(char * id1, const char * id2, const int token, const int extended); 
void scan_prune_tree(scan_data * rdp_tree); 
int scan_test(const char * production, const int valid, set_ * stop); 
int scan_test_set(const char * production, set_ * valid, set_ * stop); 
void scan_vcg_print_node(const void * node); 
#endif

/* End of scan.h */
