/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 8 April 1996
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
#include <time.h>
#include <ctype.h>
#include "scan.h"
#include "memalloc.h"
#include "symbol.h"
#include "set.h"
#include "textio.h"

#define MAX_ECHO 9              /* Maximum number of error markers per line */
char *text_bot = NULL;          /* text array for storing id's and strings */
char *text_top;                 /* top of text character */
static unsigned maxerrors = 25; /* crash if error count exceeds this value */
static unsigned maxwarnings = 100;  /* crash if warning count exceeds this value */
static unsigned totalerrors = 0;/* total number of errors this run */
static unsigned totalwarnings = 0;  /* total number of warnings this run */
static size_t maxtext = 20000;  /* size of text buffer */
static unsigned tabwidth = 8;   /* tab expansion width: 2 is a good value for tgrind */
static FILE *messages = TEXT_MESSAGES;

static unsigned errors = 0;     /* total errors for this file */
static FILE *file = NULL;       /* current file handle */
static char *first_char;        /* first character of current source line */
static char *last_char;         /* last character of current source line */
static unsigned long linenumber = 0;  /* current line in this file */
static char *name = NULL;       /* filename */
int text_char = ' ';            /* current text character */
char *text_current;             /* pointer to current source character */
void *text_scan_data;           /* pointer to the last thing read by the scanner */
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
  unsigned long linenumber;     /* copy of current line in this file */
  int text_char;                /* copy of current text character */
  char *text_current;           /* copy of pointer to current source character */
  scan_data text_scan_data;         /* copy of pointer to the last thing read by the scanner */
  char *symbol_first_char;      /* copy of first character if this symbol */
  unsigned warnings;            /* copy of total warnings for this file */

  struct source_list *previous; /* previous file descriptor */
} *source_descriptor_list = NULL; /* head of file descriptor list */

static int echo = 0;            /* enable line echoing */
static int echo_pos[MAX_ECHO];        /* array of error positions */
static int echo_num = -1;       /* current error number this line */

char *text_capitalise_string(char *str)
{
  char *ret = str;

  int non_alpha_seen = 1;

  while (*str != 0)
  {
    if (non_alpha_seen && isalpha(*str))
      *str = toupper(*str);
    else if (!non_alpha_seen && isalpha(*str))
      *str = tolower(*str);

    non_alpha_seen = !isalpha(*str);

    str++;
  }

  return ret;
}

void text_dump(void)            /* debugging routine to dump text space */
{
  char *p = text_bot - 1;

  while (p <= text_top)
  {
    while (++p <= text_top && *p != 0)
      text_printf("%c", isprint(*p) ? *p : '.');

    text_printf("\n");
  }
}

static void text_echo_line_number(void)
{
  if (linenumber != 0)
    fprintf(messages, "%6lu: ", linenumber);  /* Print present line number */
  else
    fprintf(messages, "******: ");
}

static void text_echo_line(void)
{
  char *temp;

  text_echo_line_number();          /* print line number */

  /* current input line is stored in reverse order at top of text buffer: print backwards from last character of text buffer */
  for (temp = first_char - 1; temp > last_char; temp--)
    fputc(*temp, messages);

  /* now print out the echo number line */
  if (echo_num >= 0)
  {
    int num_count = -1, char_count = 1;

    if (echo_num>=MAX_ECHO)
      echo_num = MAX_ECHO - 1;  /* only the first MAX_ECHO errors have pointers */
    text_echo_line_number();

    while (++num_count <= echo_num)
    {
      while (char_count++ < echo_pos[num_count]-1)
	fputc('-', messages);
      fputc('1'+num_count, messages);
    }
    fputc('\n', messages);
  }

  echo_num = -1;  /* reset echo numbering array pointer */
}

static void text_close(void)
{
  struct source_list *temp = source_descriptor_list;

  if (file == NULL)
    return;

  linenumber = 0;               /* switch off line number on messages */

  if (echo || errors > 0 || warnings > 0)
    text_message(TEXT_INFO, "%u error%s and %u warning%s\n",
		 errors, (errors == 1 ? "" : "s"),
		 warnings, (warnings == 1 ? "" : "s"));

  fclose(file);                 /* close the file */
  file = NULL;

  if (source_descriptor_list != NULL) /* unload next file if there is one */
  {
    source_descriptor_list = source_descriptor_list->previous;

    errors = temp->errors;
    file = temp->file;
    first_char = temp->first_char;
    last_char = temp->last_char;
    linenumber = temp->linenumber;
    name = temp->name;
    text_char = temp->text_char;
    text_current = temp->text_current;
    memcpy(text_scan_data, &(temp->text_scan_data), sizeof(scan_data));
    symbol_first_char = temp->symbol_first_char;
    warnings = temp->warnings;

    free(temp);                 /* give the storage back */

    if (echo)
    {
      text_message(TEXT_INFO, "\n");
      text_echo_line();         /* reecho line in case there are errors */
    }
  }
}

void text_echo(const int i)
{
  echo = i;
}

char *text_default_filetype(char *fname, const char *ftype)
{
  char *fullname;

  if (*ftype == 0)              /* no file type to be added */
    return fname;               /* no change */

  fullname = (char *) mem_malloc(strlen(fname) + strlen(ftype) + 2);

  strcpy(fullname, fname);

  if (strchr(fullname, '.') == NULL)
  {
    strcat(fullname, ".");
    strcat(fullname, ftype);
  }

  return fullname;
}

char *text_force_filetype(char *fname, const char *ftype)
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

  fullname = (char *) mem_malloc(length + strlen(ftype) + 2);

  strncpy(fullname, fname, length);
  fullname[length] = 0;
  strcat(fullname, ".");
  strcat(fullname, ftype);

  return fullname;
}

void text_get_char(void)
{                               /* advance text_current, reading another line if necessary */
  if (text_current <= last_char)
  {
    if (file != NULL)
      if (feof(file))
	text_close();

    if (file == NULL)
    {
      text_char = EOF;
      return;
    }

    while (text_current <= last_char)
    {
      if ((echo || echo_num >= 0) && linenumber > 0)
	text_echo_line();

      linenumber++;             /* increment current line number */
      last_char = text_current = first_char;  /* initialise pointers to empty line */
      do
      {
	*--last_char = text_char = getc(file);

	if (text_char == EOF)
	  *last_char = ' ';     /* kludge */
	else if (text_char == '\t' && tabwidth != 0)  /* expand tabs to next tabstop */
	{
	  *last_char = ' ';   /* make tab a space */
	  while ((text_current - last_char) % tabwidth != 0)
	    *(--last_char) = ' ';
	}
      }
      while (text_char != '\n' && text_char != EOF);
      *--last_char = ' ';
    }
  }
  text_char = *--text_current;
}

void text_init(const long max_text, const unsigned max_errors, const unsigned max_warnings, const unsigned tab_width)
{
  tabwidth = tab_width;
  maxtext = (size_t) max_text;  /* set text buffer size */

  if ((long) maxtext != max_text)
    text_message(TEXT_WARNING, "-T%lu too large for architecture: recast to -T%u\n", max_text, maxtext);
  maxerrors = max_errors;       /* set maximum number of errors per file */
  maxwarnings = max_warnings;   /* set maximum number of warnings per file */

  text_bot = (char *) mem_malloc(maxtext);  /* allocate text buffer */

  text_top = text_bot;          /* top of text character */
  text_current = last_char = first_char = text_bot + maxtext; /* make new buffer region below top of text */
}

char *text_insert_char(const char c)
{
  char *start = text_top;

  if (text_top >= last_char)
  {
#if 0
    /* Dump buffer on overflow */
    text_message(TEXT_INFO, "Ran out of text space - dumping buffer\n");
    text_dump();
#endif
    text_message(TEXT_FATAL, "Ran out of text space\n");
  }
  else
    *text_top++ = c;

  return start;
}

char *text_insert_characters(const char *str)
{
  char *start = text_top;

  while (*str != '\0')
    text_insert_char(*str++);

  return start;
}

char *text_insert_integer(const unsigned n)
{
  char *start = text_top;

  if (n > 9)
    text_insert_integer(n / 10);/* recursively handle multi-digit numbers */
  text_insert_char(n % 10 + '0');

  return start;
}

char *text_insert_string(const char *str)
{
  char *start = text_top;

  do
    text_insert_char(*str);
  while (*str++ != '\0');

  return start;
}

char *text_insert_substring(const char *prefix, const char *str, const unsigned n)  /* put an id_number into text buffer */
{
  char *start = text_top;

  text_insert_characters(prefix);
  text_insert_char('_');
  text_insert_characters(str);
  text_insert_char('_');
  text_insert_integer(n);
  text_insert_char('\0');
  return start;
}

unsigned long text_line_number(void)
{
  return linenumber;
}

char *text_lowercase_string(char *str)
{
  char *ret = str;

  while (*str != 0)
  {
    *str = tolower(*str);
    str++;
  }

  return ret;
}

/* A simple template matching routine: return true if the str parameter
   matches the beginning of the current text line exactly, except that
   any occurence of wildcard matches any character and any occurence
   of digitwildcard matches any digit */
int text_match_template(char *str, char wildcard, char digitwildcard)
{
  char *temp = first_char - 1;

  while (1)
  {
    if (*str == 0)
      return 1;

    if ((temp <= last_char))
      return 0;

    if (!(*str == wildcard || *str == digitwildcard) && (*str != *temp))
      return 0;

    if (*str == digitwildcard && !isdigit(*temp))
      return 0;

    str++;
    temp--;
  }
}

int text_match_blank(void)
{
  char *temp = first_char - 1;

  while (1)
  {
    if (isgraph(*temp))
      return 0;

    if ((temp <= last_char))
      return 1;

    temp--;
  }
}

int text_message(const enum text_message_type type, const char *fmt,...)
{
  int i;
  int crash = 0;
  va_list ap;                   /* argument list walker */

  if (fmt == NULL)              /* No-op if errors are suppressed */
    return 0;

  if (type == TEXT_INFO_ECHO || type == TEXT_WARNING_ECHO ||
      type == TEXT_ERROR_ECHO || type == TEXT_FATAL_ECHO)
  {
    if (++echo_num < MAX_ECHO)
      echo_pos[echo_num] = (int) (first_char - text_current);
  }

  text_echo_line_number();

  fprintf(messages, "%s",
	  type == TEXT_INFO || type == TEXT_INFO_ECHO ? "" :
	  type == TEXT_WARNING || type == TEXT_WARNING_ECHO ? (warnings++, totalwarnings++, "Warning ") :
	  type == TEXT_ERROR || type == TEXT_ERROR_ECHO ? (errors++, totalerrors++, "Error ") :
	  type == TEXT_FATAL || type == TEXT_FATAL_ECHO ? (crash = 1, "Fatal ") : "Unknown ");

  if (type == TEXT_WARNING_ECHO || type == TEXT_ERROR_ECHO)
    fprintf(messages, "%.1i ", echo_num+1);

  if (name != NULL)
    fprintf(messages, "(%s) ", name);

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vfprintf(messages, fmt, ap);  /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  if ((errors > maxerrors) && (maxerrors > 0))
  {
    fprintf(messages, "Fatal (%s): too many errors\n",
	    name == NULL ? "null file" : name);
    crash = 1;
  }

  if ((warnings > maxwarnings) && (maxwarnings > 0))
  {
    fprintf(messages, "Fatal (%s): too many warnings\n",
	    name == NULL ? "null file" : name);
    crash = 1;
  }

  if (crash)
    exit(EXIT_FAILURE);

  return i + 1;                 /* return number of characters printed */
}

FILE *text_open(char *s)
{
  FILE *handle,
  *old = file;

  if (*s == '-')
    handle = stdin;
  else
    handle = fopen(s, "r");     /* try and get the file */

  if (handle != NULL)           /* we found a file */
  {
    if (old != NULL)            /* save current file context */
    {
      struct source_list *temp = (struct source_list *) mem_calloc(1, sizeof(struct source_list));

      /* load descriptor block */
      temp->errors = errors;
      temp->file = file;
      temp->first_char = first_char;
      temp->last_char = last_char;
      temp->linenumber = linenumber;
      temp->name = name;
      temp->text_char = text_char;
      temp->text_current = text_current;
      memcpy(&(temp->text_scan_data), text_scan_data, sizeof(scan_data));
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
      text_message(TEXT_INFO, "\n");

    text_current = last_char = first_char = last_char - 1;  /* make new buffer region below current line */
  }

  return handle;
}

int text_printf(const char *fmt,...)
{
  int i;
  va_list ap;                   /* argument list walker */

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vfprintf(messages, fmt, ap);  /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  return i;                     /* return number of characters printed */
}

int text_print_C_string(char *string)
{
  int i=0;

  while (*string != '\0')
  {
    if (isprint(*string) && *string != '\"' && *string != '\\')
    {
      putc(*string, messages);
      i++;
    }
    else
    {
      putc('\\', messages);
      i+=2;
      switch (*string)
      {
	case '\a': putc('a', messages); break;
	case '\b': putc('b', messages); break;
	case '\f': putc('f', messages); break;
	case '\n': putc('n', messages); break;
	case '\r': putc('r', messages); break;
	case '\t': putc('t', messages); break;
	case '\v': putc('v', messages); break;
	case '\\': putc('\\', messages); break;
	case '\"': putc('"', messages); break;
	default: i += 2; fprintf(messages, "X%.2X", *string); break;
      }
    }
    string++;
  }
  return i;
}

void text_print_statistics(void)
{
  long symbolcount = text_top - text_bot,
   linecount = (text_bot - last_char) + maxtext;

  if (text_bot == NULL)
    text_message(TEXT_INFO, "Text buffer uninitialised\n");
  else
    text_message(TEXT_INFO, "Text buffer size %u bytes with %lu bytes free\n",
                 maxtext, maxtext - symbolcount - linecount);
}

void text_print_time(void)
{
  char line[80];
  time_t timer = time(NULL);

  strftime(line, 80, "%b %d %Y %H:%M:%S", localtime(&timer));
  text_printf("%s", line);
}

int text_print_total_errors(void)
{
  if (totalerrors != 0 || totalwarnings != 0 || echo)
    text_message(TEXT_INFO, "Total %u error%s and %u warning%s\n",
		 totalerrors, (totalerrors == 1 ? "" : "s"),
                 totalwarnings, (totalwarnings == 1 ? "" : "s"));

  return totalerrors != 0;
}

void text_redirect(FILE * file)
{
  if (messages != stdout && messages != stderr)
    fclose(messages);

  messages = file;
}

unsigned text_total_errors(void)
{
  return totalerrors;
}

unsigned text_total_warnings(void)
{
  return totalwarnings;
}

char *text_uppercase_string(char *str)
{
  char *ret = str;

  while (*str != 0)
  {
    *str = toupper(*str);
    str++;
  }

  return ret;
}

/* End of textio.c */
