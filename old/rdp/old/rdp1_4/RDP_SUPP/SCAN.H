/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* scan.h - a programmable scanner
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef SCAN_H
#define SCAN_H

#include <stdio.h>
#include "set.h"
#include "symbol.h"

#define SCAN__DATA   \
  char *id;          \
  int token;         \
  union {            \
  long int i;        \
  long unsigned u;   \
  double r;          \
  void *p;           \
  } data;

#define SCAN__CAST ((scan__data*) text__scan_data)

typedef long unsigned integer;

extern void* scan__table;

typedef struct
{
  SCAN__DATA
} scan__data;

extern int scan__case_insensitive;  /* this will dissapear at V2.00 */
extern int scan__show_skips;
extern int scan__newline_visible; /* this will dissapear at V2.00 */
extern int scan__symbol_echo;

enum scan__primitive_type
{
  T__IGNORE, T__ID, T__INTEGER, T__REAL,
  T__ISTRING, T__ISTRING_ESC,
  T__ICOMMENT, T__ICOMMENT_VISIBLE,
  T__ICOMMENT_NEST, T__ICOMMENT_NEST_VISIBLE,
  T__ICOMMENT_LINE, T__ICOMMENT_LINE_VISIBLE,
  T__KEYWORD, T__EOF, T__EOLN, T__TOP
};

void scan__(void);
void scan__init(const int case_insensitive, const int newline_visible, const int show_skips, const int symbol_echo, char *token_names);
void scan__load_keyword(char *id1, const char *id2, const int token);
int scan__test(const unsigned valid, set__ * stop);
int scan__test_set(set__ * valid, set__ * stop);

#endif

/* End of scan.h */
