/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
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

extern const char* text_bot;       /* text array for storing id's and strings */
extern char* text_top;
extern int text_char;
extern void * text_scan_data;
extern char* text_current;   /* pointer to current source character */

enum text_message_type        /* text message actions */
{
  TEXT_INFO, TEXT_WARNING, TEXT_ERROR, TEXT_FATAL, 
  TEXT_INFO_ECHO, TEXT_WARNING_ECHO, TEXT_ERROR_ECHO, TEXT_FATAL_ECHO
}; 

const char* text_capitalise_string(const char* str); 
unsigned long text_column_number(void); 
const char* text_default_filetype(const char* fname, const char* ftype); 
void text_dump(void); 
void text_echo(const int i); 
void text_echo_line(void);
const char* text_extract_filename(const char* fname);
void text_free(void);
const char* text_find_ASCII_element(int c);
const char* text_force_filetype(const char* fname, const char* ftype);
void text_get_char(void);
void text_get_line(const char* buffer, int max_line);
int text_get_echo(void);
unsigned text_get_tab_width(void);
void text_init(const long max_text, const unsigned max_errors, const unsigned max_warnings, const unsigned tab_width);
const char* text_insert_char(const char c);
const char* text_insert_characters(const char* str);
const char* text_insert_integer(const unsigned n);
const char* text_insert_string(const char* str);
const char* text_insert_substring(const char* prefix, const char* str, const unsigned n);
int text_is_valid_C_id(const char* s);
unsigned long text_line_number(void);
unsigned long text_column_number(void);
const char* text_lowercase_string(const char* str);
const char* text_make_C_identifier(const char* str);
int text_match_template(const char* str, char wildcard, char digitwildcard);
int text_match_blank(void);
int text_message(const enum text_message_type type, const char* fmt, ...);
FILE * text_open(const char* s);
int text_print_as_C_string_or_char(FILE * file, const char* string, int is_char_string);
int text_print_C_char(const char* string); 
int text_print_C_char_file(FILE * file, const char* string); 
int text_print_C_string(const char* string); 
int text_print_C_string_file(FILE * file, const char* string); 
int text_printf(const char* fmt, ...); 
void text_print_statistics(void); 
void text_print_time(void); 
int text_print_total_errors(void); 
void text_redirect(FILE * file);
FILE *text_stream(void);
const char* text_time_string(void);
unsigned long text_sequence_number(void);
unsigned text_total_errors(void);
unsigned text_total_warnings(void); 
const char* text_uppercase_string(const char* str); 
const char* text_strdup(const char* str);

#endif 

/* End of textio.h */








