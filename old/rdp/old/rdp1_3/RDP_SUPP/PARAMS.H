/****************************************************************************
*
* RDP release 1.30 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 28 Dec 1994
*
* params.h - parameterised definitions for RDP
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef PARAMS_H
#define PARAMS_H

/* Where to send all echoed output from RDP and its support
   routines. Usually set to stderr */

#define MESSAGES stdout

/* A list of C reserved words that should not be used as RDP identifiers */

#define RDP__RESERVED_WORDS                                      \
/* ANSI C reserved words */                                      \
"auto", "break", "case", "char", "const", "continue", "default", \
"do", "double", "else", "enum", "extern", "float", "for",        \
"goto", "if", "int", "long", "register", "return", "short",      \
"signed", "sizeof", "static", "struct", "switch", "typedef",     \
"union", "unsigned", "void", "volatile", "while",                \
/* C++ reserved words */                                         \
/* ANSI C library names (a selection) */                         \
"printf"

/* Parameterisation for standard types integer, real,
   unsigned_integer and character. We also define printf() format
   strings for each type. */

typedef long int integer;
#define INTEGER_FORMAT "%li"

typedef double real;
#define REAL_FORMAT "%f"

typedef long unsigned unsigned_integer;
#define UNSIGNED_INTEGER_FORMAT "%lu"

typedef char character;
#define CHAR_FORMAT "%c"

typedef char* string;
#define STRING_FORMAT "%s"

#endif

/* End of params.h */
