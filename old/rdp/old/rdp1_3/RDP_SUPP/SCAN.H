/****************************************************************************
*
* RDP release 1.30 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 28 Dec 1994
*
* scan.h - a programmable scanner
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef SCAN_H
#define SCAN_H

#include <stdio.h>
#include "params.h"
#include "set.h"
#include "symbol.h"

extern symbol__ *scan__sym;
extern char *scan__text;
extern unsigned scan__errorcount;
extern unsigned scan__linenumber;
enum scan__extended_class_type
{
  E_SIMPLE, /* i.e. not extended! */ E_STRING, E_STRING_ESC,
  E_COMMENT, E_COMMENT_NEST, E_COMMENT_LINE,
  E_COMMENT_VISIBLE, E_COMMENT_NEST_VISIBLE, E_COMMENT_LINE_VISIBLE
};

enum scan__primitive_type
{
  P_BAD, P_IGNORE, P_ID, P_NEW_ID, P_INTEGER, P_REAL, P_KEYWORD, P_EOF, P_EOLN, P_ALT_ID, P_TOP
};

void scan__(void);
char *scan__add_filetype(char *fname, const char *ftype);
void scan__case_insensitive(int i);
void scan__close(void);
char *scan__default_filetype(char *fname, const char *ftype);
void scan__echo_line(FILE * fname);
int scan__error(const char *fmt,...);
int scan__error_echo(const char *fmt,...);
void scan__goto(char *target);
int scan__info(const char *fmt,...);
int scan__info_echo(const char *fmt,...);
void scan__init(unsigned max_text, unsigned max_errors, unsigned max_warnings);
void scan__insert_char(char c);
void scan__insert_integer(unsigned n);
char *scan__insert_subid(const char *s1, unsigned n);
char *scan__load_file(char *s);
void scan__load_extended_data(symbol__ * s, int kind, const char *close);
symbol__ *scan__load_extended_keyword(const char *token, int value, int kind, const char *close);
symbol__ *scan__load_keyword(const char *token, int value);
void scan__line_echo(int i);
void scan__line_never_echo(int i);
void scan__newline_visible(int i);
FILE *scan__open_file(char *s);
char *scan__open_text(char *s, char *t);
char *scan__position(void);
void scan__print_statistics(void);
int scan__print_total_errors(int verbose);
void scan__show_skips(int i);
void scan__summarise_errors(int i);
void scan__suppress_errors(int i);
void scan__symbol_echo(int i);
void scan__tab_width(unsigned u);
int scan__test(unsigned valid, set__ stop, const char *fmt,...);
int scan__test_set(set__ valid, set__ stop, const char *fmt,...);
unsigned scan__total_errors(void);
unsigned scan__total_warnings(void);
int scan__warning(const char *fmt,...);
int scan__warning_echo(const char *fmt,...);

#endif

/* End of scan.h */
