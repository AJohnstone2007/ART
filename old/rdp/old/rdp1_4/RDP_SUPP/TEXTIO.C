/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* textio.c - file opening, text buffering and error reporting
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <ctype.h>
#include "memalloc.h"
#include "symbol.h"
#include "set.h"
#include "textio.h"

static char *text__bot = NULL;  /* text array for storing id's and strings */
char *text__top;                /* top of text character */
static unsigned maxerrors = 25; /* crash if error count exceeds this value */
static unsigned maxwarnings = 100;  /* crash if warning count exceeds this value */
static unsigned totalerrors = 0;/* total number of errors this run */
static unsigned totalwarnings = 0;  /* total number of warnings this run */
static size_t maxtext = 20000;/* size of text buffer */
static unsigned tabwidth = 8;   /* tab expansion width: 2 is a good value for tgrind */
static FILE *messages = TEXT__MESSAGES;

static unsigned errors = 0;     /* total errors for this file */
static FILE *file = NULL;       /* current file handle */
static char *first_char;        /* first character of current source line */
static char *last_char;         /* last character of current source line */
static unsigned linenumber = 0; /* current line in this file */
static char *name = NULL;       /* filename */
int text__char = ' ';           /* current text character */
static char *text__current;     /* pointer to current source character */
void * text__scan_data;         /* pointer to the last thing read by the scanner */
static char *symbol_first_char; /* first character if this symbol */
static unsigned warnings = 0;   /* total warnings for this file */

/* data block for linked list of stored file contexts */
static struct source_list
{
  char *name;                   /* copy of filename */
  unsigned errors;              /* copy of total errors for this file */
  FILE *file;                   /* copy of current file handle */
  char *first_char;             /* copy of first character of current source line */
  char *last_char;              /* copy of last character of current source line */
  unsigned linenumber;          /* copy of current line in this file */
  int text__char;               /* copy of current text character */
  char *text__current;          /* copy of pointer to current source character */
  void* text__scan_data;        /* copy of pointer to the last thing read by the scanner */
  char *symbol_first_char;      /* copy of first character if this symbol */
  unsigned warnings;            /* copy of total warnings for this file */

  struct source_list *previous; /* previous file descriptor */
} *source_descriptor_list = NULL;   /* head of file descriptor list */

static int echo;           /* enable line echoing */
static int echoed;         /* this line has already been echoed flag */

static void text__echo_line(void)
{
  char *temp;

  fprintf(messages, "%6i: ", linenumber); /* Print present line number */

  /* current input line is stored in reverse order at top of text buffer: print backwards from last character of text buffer */
  for (temp = first_char - 1; temp > last_char; temp--)
    fputc(*temp, messages);

  fputc('\n', messages);        /* and terminate with a line feed */
  echoed = 1;              /* mark line as echoed */
}

static void text__echo_line_pointer(void)
{
  char *temp;

  if (!echoed)             /* echo line if it hasn't already been output */
    text__echo_line();
  fprintf(messages, "******* ");/* print error highlight */
  for (temp = first_char; temp > text__current + 1; temp--)
    fputc('-', messages);       /* print error pointer */
  fprintf(messages, "^\n");
}

static void text__close(void)
{
  struct source_list *temp = source_descriptor_list;

  if (file == NULL)
    return;

  if (echo || errors > 0 || warnings > 0)
    text__message(TEXT__INFO, "%u error%s and %u warning%s\n",
		  errors, (errors == 1 ? "" : "s"),
		  warnings, (warnings == 1 ? "" : "s"));

  fclose(file);           /* close the file */
  file = NULL;

  if (source_descriptor_list != NULL)	/* unload next file if there is one */
  {
    source_descriptor_list = source_descriptor_list->previous;

    errors = temp->errors;
    file = temp->file;
    first_char = temp->first_char;
    last_char = temp->last_char;
    linenumber = temp->linenumber;
    name = temp->name;
    text__char = temp->text__char;
    text__current = temp->text__current;
    text__scan_data = temp->text__scan_data;
    text__scan_data = temp->text__scan_data;
    symbol_first_char = temp->symbol_first_char;
    warnings = temp->warnings;

    free(temp);           /* give the storage back */

    if (echo)
    {
      text__message(TEXT__INFO, "\n");
      text__echo_line();    /* reecho line in case there are errors */
    }
  }
}

void text__echo(const int i)
{
  echo = i;
}

char *text__default_filetype(char *fname, const char *ftype)
{
  char *fullname;

  if (*ftype == 0)              /* no file type to be added */
    return fname;               /* no change */

  fullname = (char *) mem__malloc(strlen(fname) + strlen(ftype) + 2);

  strcpy(fullname, fname);

  if (strchr(fullname, '.') == NULL)
  {
    strcat(fullname, ".");
    strcat(fullname, ftype);
  }

  return fullname;
}

char *text__force_filetype(char *fname, const char *ftype)
{
  char *fullname;
  size_t length;

  if (*ftype == 0)              /* no file type to be added */
    return fname;               /* no change */

  /* work backwards from end of filename looking for a dot, or a directory separator */

  length = strlen(fname);
  while (fname[length] != '.' && fname[length] != '/' /* Unix */ &&
	 fname[length] != '\\' /* DOS */ && length > 0)
    length--;

  if (fname[length] != '.')     /* no dot found */
    length = strlen(fname);

  fullname = (char *) mem__malloc(length + strlen(ftype) + 2);

  strncpy(fullname, fname, length);
  fullname[length] = 0;
  strcat(fullname, ".");
  strcat(fullname, ftype);

  return fullname;
}

void text__get_char(void)
{                               /* advance text__current, reading another line if necessary */
  if (text__current <= last_char)
  {
    if (file != NULL)
      if (feof(file))
        text__close();

    if (file == NULL)
    {
      text__char = EOF;
      return;
    }

    while (text__current <= last_char)
    {
      echoed = 0;
      linenumber++;                      /* increment current line number */
      last_char = text__current = first_char; /* initialise pointers to empty line */
      do
      {
	*--last_char = text__char = getc(file);

	if (text__char == EOF)
	  *last_char = ' ';	/* kludge */

	if (text__char == '\t')   /* expand tabs to next tabstop */
	{
	  if (tabwidth != 0)	/* just pass tabs if tabstop is zero */
	  {
	    *last_char = ' ';       /* make tab a space */
	    while ((text__current - last_char) % tabwidth != 0)
	      *(--last_char) = ' ';
	  }
	}
      }
      while (text__char != '\n' && text__char != EOF);

      if (echo)
	text__echo_line();
    }
  }
  text__char = *--text__current;
}

void text__init(const size_t max_text, const unsigned max_errors, const unsigned max_warnings, const unsigned tab_width)
{
  tabwidth = tab_width;
  maxtext = (size_t) max_text;           /* set text buffer size */
  maxerrors = max_errors;       /* set maximum number of errors per file */
  maxwarnings = max_warnings;   /* set maximum number of warnings per file */

  text__bot = (char *) mem__malloc(maxtext);  /* allocate text buffer */

  text__top = text__bot;        /* top of text character */
  text__current = last_char = first_char = text__bot + maxtext; /* make new buffer region below top of text */
}

char *text__insert_char(const char c)
{
  char *start = text__top;

  if (text__top >= last_char)
    text__message(TEXT__FATAL, "ran out of text space\n");
  else
    *text__top++ = c;

  return start;
}

char *text__insert_characters(const char *str)
{
  char *start = text__top;

  while (*str != '\0')
    text__insert_char(*str++);

  return start;
}

char *text__insert_integer(const unsigned n)
{
  char *start = text__top;

  if (n > 9)
    text__insert_integer(n / 10); /* recursively handle multi-digit numbers */
  text__insert_char(n % 10 + '0');

  return start;
}

char *text__insert_string(const char *str)
{
  char *start = text__top;

  do
    text__insert_char(*str);
  while (*str++ != '\0');

  return start;
}

char *text__insert_substring(const char *str, const unsigned n) /* put an id_number into text buffer */
{
  char *start = text__top;

  while (*str != '\0')
    text__insert_char(*str++);
  text__insert_char('_');
  text__insert_char('_');
  text__insert_integer(n);
  text__insert_char('\0');
  return start;
}

int text__message(const enum text__message_type type, const char *fmt,...)
{
  int i;
  int crash = 0;
  va_list ap;                   /* argument list walker */

  if (fmt == NULL)              /* No-op if errors are suppressed */
    return 0;

  if ((type == TEXT__INFO_ECHO || type == TEXT__WARNING_ECHO ||
       type == TEXT__ERROR_ECHO || type == TEXT__FATAL_ECHO) &&
      file != NULL
    )
    text__echo_line_pointer();

  fprintf(messages, "%s (%s): ",
	  type == TEXT__INFO || type == TEXT__INFO_ECHO ? "" :
	  type == TEXT__WARNING || type == TEXT__WARNING_ECHO ? (warnings++, totalwarnings++, "Warning") :
	  type == TEXT__ERROR || type == TEXT__ERROR_ECHO ? (errors++, totalerrors++, "Error") :
	  type == TEXT__FATAL || type == TEXT__FATAL_ECHO ? (crash = 1, "Fatal") : "Unknown",
	  name == NULL ? "null file" : name);

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vfprintf(messages, fmt, ap);  /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  if (errors > maxerrors)
  {
    fprintf(messages, "Fatal (%s): too many errors\n",
	    name == NULL ? "null file" : name);
    crash = 1;
  }

  if (warnings > maxwarnings)
  {
    fprintf(messages, "Fatal (%s): too many warnings\n",
	    name == NULL ? "null file" : name);
    crash = 1;
  }

  if (crash)
    exit(EXIT_FAILURE);

  return i + 1;                 /* return number of characters printed */
}

FILE *text__open(char *s)
{
  FILE *handle,
    *old = file;

  if (*s == '-')
    handle = stdin;
  else
    handle = fopen(s, "r");     /* try and get the file */

  if (handle != NULL)           /* we found a file */
  {
    if (old != NULL)           /* save current file context */
    {
      struct source_list *temp = (struct source_list *) mem__calloc(1, sizeof(struct source_list));

      /* load descriptor block */
      temp->errors = errors;
      temp->file = file;
      temp->first_char = first_char;
      temp->last_char = last_char;
      temp->linenumber = linenumber;
      temp->name = name;
      temp->text__char = text__char;
      temp->text__current = text__current;
      temp->text__scan_data = text__scan_data;
      temp->symbol_first_char = symbol_first_char;
      temp->warnings = warnings;

      /* link descriptor block into head of list */
      temp->previous = source_descriptor_list;
      source_descriptor_list = temp;
    }

    /* re-initialise file context */
    errors = 0;
    file = handle;
    linenumber = 0;
    name = s;
    warnings = 0;

    if (echo)
      text__message(TEXT__INFO, "\n");

    text__current = last_char = first_char = last_char - 1; /* make new buffer region below current line */
  }

  return handle;
}

int text__printf(const char *fmt,...)
{
  int i;
  va_list ap;                   /* argument list walker */

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vfprintf(messages, fmt, ap);  /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  return i;                     /* return number of characters printed */
}

void text__print_statistics(void)
{
  long unsigned symbolcount = text__top - text__bot,
   linecount = text__bot + maxtext - last_char;

  if (text__bot == NULL)
    text__message(TEXT__INFO, "text buffer uninitialised\n");
  else
    text__message(TEXT__INFO, "text buffer size %u bytes with %lu bytes free\n",
		  maxtext, maxtext - symbolcount - linecount);
}

int text__print_total_errors(void)
{
  if (totalerrors != 0 || totalwarnings != 0 || echo)
    text__message(TEXT__INFO, "total of %u error%s and %u warning%s\n",
                  totalerrors, (totalerrors == 1 ? "" : "s"),
                  totalwarnings, (totalwarnings == 1 ? "" : "s"));

  return totalerrors != 0;
}

void text__redirect(FILE * file)
{
  if (messages != stdout && messages != stderr)
    fclose(messages);

  messages = file;
}

unsigned text__total_errors(void)
{
  return totalerrors;
}

unsigned text__total_warnings(void)
{
  return totalwarnings;
}

/* End of textio.c */
