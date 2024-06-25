/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 8 April 1996
*
* textio.h - file opening, text buffering and error reporting
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef TEXTIO_H
#define TEXTIO_H

#define TEXT_MESSAGES stdout

#include <stdio.h>
#include "set.h"
#include "symbol.h"

extern char *text_bot;          /* text array for storing id's and strings */
extern char *text_top;
extern int text_char;
extern void *text_scan_data;
extern char *text_current;      /* pointer to current source character */

enum text_message_type          /* text message actions */
{
  TEXT_INFO, TEXT_WARNING, TEXT_ERROR, TEXT_FATAL,
  TEXT_INFO_ECHO, TEXT_WARNING_ECHO, TEXT_ERROR_ECHO, TEXT_FATAL_ECHO
};

char *text_capitalise_string(char *str);
char *text_default_filetype(char *fname, const char *ftype);
void text_dump(void);
void text_echo(const int i);
char *text_force_filetype(char *fname, const char *ftype);
void text_get_char(void);
void text_init(const long max_text, const unsigned max_errors, const unsigned max_warnings, const unsigned tab_width);
char *text_insert_char(const char c);
char *text_insert_characters(const char *str);
char *text_insert_integer(const unsigned n);
char *text_insert_string(const char *str);
char *text_insert_substring(const char *prefix, const char *str, const unsigned n);
unsigned long text_line_number(void);
char *text_lowercase_string(char *str);
int text_match_template(char *str, char wildcard, char digitwildcard);
int text_match_blank(void);
int text_message(const enum text_message_type type, const char *fmt,...);
FILE *text_open(char *s);
int text_print_C_string(char *string);
int text_printf(const char *fmt,...);
void text_print_statistics(void);
void text_print_time(void);
int text_print_total_errors(void);
void text_redirect(FILE * file);
unsigned text_total_errors(void);
unsigned text_total_warnings(void);
char* text_uppercase_string(char *str);

#endif

/* End of textio.h */
