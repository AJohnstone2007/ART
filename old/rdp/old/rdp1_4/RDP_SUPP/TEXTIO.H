/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* textio.h - file opening, text buffering and error reporting
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef TEXTIO_H
#define TEXTIO_H

#define TEXT__MESSAGES stderr

#include <stdio.h>
#include "set.h"
#include "symbol.h"

extern char *text__top;
extern int text__char;
extern void * text__scan_data;

enum text__message_type         /* text message actions */
{  TEXT__INFO, TEXT__WARNING, TEXT__ERROR, TEXT__FATAL,
   TEXT__INFO_ECHO, TEXT__WARNING_ECHO, TEXT__ERROR_ECHO, TEXT__FATAL_ECHO
};

char *text__default_filetype(char *fname, const char *ftype);
void text__echo(const int i);
char *text__force_filetype(char *fname, const char *ftype);
void text__get_char(void);
void text__init(const size_t max_text, const unsigned max_errors, const unsigned max_warnings, const unsigned tab_width);
char *text__insert_char(const char c);
char *text__insert_characters(const char *str);
char *text__insert_integer(const unsigned n);
char *text__insert_string(const char *str);
char *text__insert_substring(const char *str, const unsigned n);
int text__message(const enum text__message_type type, const char *fmt, ...);
FILE *text__open(char *s);
int text__printf(const char *fmt, ...);
void text__print_statistics(void);
int text__print_total_errors(void);
void text__redirect(FILE* file);
unsigned text__total_errors(void);
unsigned text__total_warnings(void);

#endif

/* End of textio.h */
