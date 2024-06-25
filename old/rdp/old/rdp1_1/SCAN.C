/****************************************************************************
*
* RDP release 1.00 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Feb 1994
*
* scan.c - a programmable scanner
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <ctype.h>
#include "crash.h"
#include "symbol.h"
#include "set.h"
#include "scan.h"

#define EOF_CHAR 26             /* internal EOF character is ^z */

static struct source_list
{
 int is_text:
   1;                           /* text buffer flag */
  FILE *file;                   /* current file handle */
  char *text;                   /* text pointer */
  symbol__ scan__sym;           /* copy of the last thing read by the scanner */
  char *next_char;              /* copy of current source character */
  char *first_char;             /* copy of first character of current source line */
  char *last_char;              /* copy of last character of current source line */
  unsigned linenumber;          /* current line in this file */
  unsigned errors;              /* total errors for this file */
  unsigned warnings;            /* total warnings for this file */
  char *name;                   /* filename */
  struct source_list *last;     /* previous file */
} *current_source = NULL;       /* head of file list */

symbol__ *scan__sym;            /* pointer to the last thing read by the scanner */
static char *next_char;         /* current source character */
static char *first_char;        /* first character of current source line */
static char *last_char;         /* last character of current source line */
char *scan__text;               /* top of text character */
static char *text;              /* text array for storing id's and strings */

static unsigned maxerrors;      /* crash if error count exceeds this value */
static unsigned maxwarnings;    /* crash if warning count exceeds this value */
static unsigned totalerrors = 0;/* total number of errors this run */
static unsigned totalwarnings = 0;  /* total number of warnings this run */
static unsigned maxtext;        /* size of text buffer */
static unsigned tabwidth = 2;   /* tab expansion width: 2 is a good value for tgrind */

static struct
{
 int newline_visible:
   1,                           /* ignore line ends flag */
  case_insensitive:
   1,                           /* map capitals to lower case flag */
  symbol_echo:
   1,                           /* echo symbols flag */
  line_suppress_echo:
   1,                           /* disable line echoing even on errors */
  line_echo:
   1,                           /* echo source flag */
  line_echoed:
   1,                           /* line has been echoed flag */
  summarise_errors:
   1,                           /* print summary error statistics at end of each file */
  suppress_errors:
   1,                           /* suppress error messges */
  show_skips:
   1,                           /* print 'skipping to...' messages */
	hex_dollar:
   1;                           /* allow hexadecimal numbers of the form $xx */
} scan__flags =

{
  0
};                              /* scanner control block */

struct extended_keyword_data
{
  int kind;                     /* What kind of extended keyword */
  char *close;
};

/****************************************************************************
*
* Messaging routines
*
****************************************************************************/
void scan__echo_line(FILE * fname)
{
  char *temp;

  if (current_source->is_text)
  {
    temp = first_char;
    printf("        ");         /* Don't print line numbers in text mode */

    for (temp = first_char; *temp != '\n' && *temp != EOF_CHAR; temp++)
      fputc(*temp, fname);
  }
  else
  {
    if (*(first_char - 1) == EOF_CHAR)  /* Don't dump if at end of file */
      return;
    printf("%6i: ", current_source->linenumber);  /* Print present line number */

    /* current input line is stored in reverse oreder at top of text buffer: print backwards from last character of text buffer */
    for (temp = first_char - 1; temp > last_char; temp--)
      fputc(*temp, fname);
  }

  fputc('\n', fname);           /* and terminate with a line feed */
  scan__flags.line_echoed = 1;  /* mark line as echoed */
}

/* generic error output routine */
void scan__echo_line_pointer(FILE * f)
{
  char *temp;

  if (f != NULL)                /* skip echoing for null file */
  {
    if (!scan__flags.line_echoed) /* echo line if it hasn't already been output */
      scan__echo_line(f);
    printf("******* ");         /* print error highlight */
    if (current_source->is_text)
    {
      for (temp = first_char; temp != next_char - 1; temp++)
        putchar('-');           /* print error pointer */
    }
    else
    {
      for (temp = first_char; temp > next_char + 2; temp--)
        putchar('-');           /* print error pointer */
    }
    printf("^\n");
  }

}

int scan__error_echo(const char *fmt,...) /* Print a formated error message */
{
  int i;
  va_list ap;                   /* argument list walker */

  if (scan__flags.suppress_errors || fmt == NULL) /* No-op if errors are suppressed */
    return (0);

  scan__echo_line_pointer(stdout);

  printf("Error %s: ", current_source->name);

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vprintf(fmt, ap);         /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  printf("\n");
  totalerrors++;

  if (current_source->errors++ > maxerrors)
    crash__("scan: Too many errors");

  return (i + 1);               /* return number of characters printd */
}

int scan__warning_echo(const char *fmt,...) /* Print a formated error message */
{
  int i;
  va_list ap;                   /* argument list walker */

  if (scan__flags.suppress_errors || fmt == NULL) /* No-op if errors are suppressed */
    return (0);

  scan__echo_line_pointer(stdout);

  printf("Warning %s: ", current_source->name);

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vprintf(fmt, ap);         /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  printf("\n");
  totalwarnings++;

  if (current_source->warnings++ > maxwarnings)
    crash__("scan: Too many warnings");

  return (i + 1);               /* return number of characters printd */
}

int scan__info_echo(const char *fmt,...)  /* Print a formated informational message */
{
  int i;
  va_list ap;                   /* argument list walker */

  if (scan__flags.suppress_errors || fmt == NULL) /* No-op if errors are suppressed */
    return (0);

  scan__echo_line_pointer(stdout);

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vprintf(fmt, ap);         /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  printf("\n");

  return (i + 1);               /* return number of characters printd */
}

int scan__error(const char *fmt,...)  /* Print a formated error message */
{
  int i;
  va_list ap;                   /* argument list walker */

  if (fmt == NULL)
    return 0;

  printf("Error: ");

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vprintf(fmt, ap);         /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  printf("\n");

  if (totalerrors++ > maxerrors)
    crash__("scan: Too many errors");

  return (i + 1);               /* return number of characters printd */
}

int scan__warning(const char *fmt,...)  /* Print a formated warning message */
{
  int i;
  va_list ap;                   /* argument list walker */

  if (fmt == NULL)
    return 0;

  printf("Warning: ");

  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vprintf(fmt, ap);         /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  printf("\n");

  if (totalwarnings++ > maxwarnings)
    crash__("scan: Too many warnings");

  return (i + 1);               /* return number of characters printd */
}

int scan__info(const char *fmt,...) /* Print a formated informational message */
{
  int i;
  va_list ap;                   /* argument list walker */

  if (fmt == NULL)
    return 0;

  printf(" ");
  va_start(ap, fmt);            /* pass parameters to vprintf */
  i = vprintf(fmt, ap);         /* remember count of characaters printed */
  va_end(ap);                   /* end of var args block */

  printf("\n");
  return (i + 1);
}

static int scan__print_file_errors(int verbose)
{
  if (current_source->errors != 0 || current_source->warnings != 0 || verbose)
    scan__info("%s: %u error%s, %u warning%s", current_source->name,
               current_source->errors, (current_source->errors == 1 ? "" : "s"),
               current_source->warnings, (current_source->warnings == 1 ? "" : "s"));
  return (current_source->errors != 0 || current_source->warnings != 0);
}

int scan__print_total_errors(int verbose)
{
  if (totalerrors != 0 || totalwarnings != 0 || verbose)
    scan__info("Total: %u error%s, %u warning%s",
               totalerrors, (totalerrors == 1 ? "" : "s"),
               totalwarnings, (totalwarnings == 1 ? "" : "s"));

  return (totalerrors != 0 || totalwarnings != 0);
}

void scan__print_statistics(void)
{
  long unsigned symbolcount = scan__text - text,
   linecount = text + maxtext - last_char;

  scan__info("Text size %u bytes of which symbols %lu bytes and free %lu bytes",
             maxtext, symbolcount, maxtext - symbolcount - linecount);
}

unsigned scan__total_errors(void)
{
  return totalerrors;
}

unsigned scan__total_warnings(void)
{
  return totalwarnings;
}

/****************************************************************************
*
* Token testing and skipping routines
*
****************************************************************************/
void scan__skip(set__ stop)     /* scan until a token in stop set appears */
{
  while (!set__inc_element(stop, scan__sym->token))
    scan__();
  if (scan__flags.show_skips)
    scan__info_echo("Skipping to...");
}

int scan__test_set(set__ valid, set__ stop, const char *fmt,...)
{
  if (!set__inc_element(valid, scan__sym->token))
  {
    if (fmt != NULL)
    {
      va_list ap;               /* argument list walker */

      va_start(ap, fmt);
      scan__error_echo(fmt, ap);
      va_end(ap);

      scan__skip(stop);
    }
    return 0;
  }
  else
    return 1;
}

int scan__test(unsigned valid, set__ stop, const char *fmt,...)
{
  if (valid != scan__sym->token)
  {
    if (fmt != NULL)
    {
      va_list ap;               /* argument list walker */

      va_start(ap, fmt);
      scan__error_echo(fmt, ap);
      va_end(ap);

      scan__skip(stop);
    }
    return 0;
  }
  else
    return 1;
}

/****************************************************************************
*
* Internal routine to get a character from the input buffer, or recharge
* from the source file
*
****************************************************************************/
static void scan__get_char(void)
{                               /* advance next_char, reading another line if necessary */
  if (current_source->is_text)
  {
    if (*next_char == '\n')
    {
      first_char = next_char + 1; /* mark start of line */
      if (scan__flags.line_echo)
        scan__echo_line(stdout);
    }
    if (*next_char != EOF_CHAR) /* Don't advance beyond end of file marker */
      next_char++;
    if (*next_char == '\t')     /* Convert tabs to spaces (yuk!) */
      *next_char = ' ';
  }
  else
  {
    while (next_char <= last_char && *next_char != EOF_CHAR)
    {                           /* read another line into the buffer */
      scan__flags.line_echoed = scan__flags.line_suppress_echo;
      current_source->linenumber++;
      last_char = next_char = first_char; /* initialise pointers to empty line */
      do
      {
        int ch = fgetc(current_source->file); /* get a character from file */

        if (ch == EOF)
          ch = EOF_CHAR;        /* convert to eight bits */

        *(--last_char) = (char) ch;

        if (*last_char == '\t') /* expand tabs to next tabstop */
        {
          *last_char = ' ';     /* make tab a space */
          while ((next_char - last_char) % tabwidth != 0)
            *(--last_char) = ' ';
        }
      }
      while (*last_char != '\n' && *last_char != EOF_CHAR);

      if (scan__flags.line_echo)
        scan__echo_line(stdout);
    }
    --next_char;
  }
  if (*next_char >= 'A' && *next_char <= 'Z' && scan__flags.case_insensitive)
    *next_char -= 'A' - 'a';

  if (((*next_char < ' ') || (*next_char > '~')) && *next_char != EOF_CHAR && *next_char != '\n')
  {
    int bad = *next_char;

    *next_char = ' ';           /* kill bad character */
    /* push next_char back so that error arrow appears in right place! */
    --next_char;
    scan__error_echo("Illegal character '0x%.2X' in source file", bad);
    ++next_char;                /* restore pointer */
  }
}

/****************************************************************************
*
* Insert a character into the text buffer
*
****************************************************************************/
void scan__insert_char(char c)
{
  if (scan__text >= last_char)
    crash__("Ran out of scanner text space");
  else
    *scan__text++ = c;
}

/****************************************************************************
*
* Insert the ASCII representation of a number into the text buffer
*
****************************************************************************/
void scan__insert_number(unsigned n)
{
  if (n > 9)
    scan__insert_number(n / 10);/* recursively handle multi-digit numbers */
  scan__insert_char(n % 10 + '0');
}

/****************************************************************************
*
* Insert a string followed by an underscore followed by a number
*
****************************************************************************/
char *scan__insert_subid(const char *s1, unsigned n)  /* put an id_number into text buffer */
{
  char *start = scan__text;

  while (*s1 != '\0')
    scan__insert_char(*s1++);
  scan__insert_char('_');
  scan__insert_char('_');
  scan__insert_number(n);
  scan__insert_char('\0');
  return (start);
}

/****************************************************************************
*
* Scanner symbol diagnostic print
*
****************************************************************************/
static void scan__print_symbol(symbol__ * s)
{
  printf("Scanned ");
  switch (s->token)
  {
  case P_BAD:
    printf("BAD     ");
    break;
  case P_IGNORE:
    printf("IGNORE  ");
    break;
  case P_ID:
    printf("ID      ");
    break;
  case P_NEW_ID:
    printf("NEW_ID  ");
    break;
  case P_NUMBER:
    printf("NUMBER  ");
    break;
  case P_EOF:
    printf("EOF     ");
    break;
  case P_EOLN:
    printf("EOLN    ");
    break;
  default:
    if (s->token < P_TOP)
      printf("??????? ");
    else
      printf("KEYWORD ");
  }
  printf("token=%3u  number=%3i  id '%s'\n", s->token, s->number, s->id);
}

/****************************************************************************
*
* Main scanner routine
*
****************************************************************************/
void scan__(void)
{                               /* get the next lexeme */
  char *scan__text_start = scan__text;  /* remember start of symbol */

  memset(scan__sym, 0, sizeof(symbol__));

  if (current_source == NULL)   /* Has the last file been shut? */
    scan__sym->token = P_EOF;
  else
    do
    {
      while (*next_char != EOF_CHAR &&
             !(scan__flags.newline_visible && *next_char == '\n') &&
             isspace(*next_char)
        )
        scan__get_char();       /* blank strip */
      if (isalpha(*next_char))
      {                         /* read an identifier into text buffer */
        symbol__ *s;

        scan__sym->id = scan__text; /* point to text table */
        while (isalnum(*next_char) || *next_char == '_')
        {
          scan__insert_char(*next_char);
          scan__get_char();
        }
        scan__insert_char('\0');/* terminate string */
        if ((s = symbol__lookup_id(scan__sym->id)) != NULL)
        {
          memcpy(scan__sym, s, sizeof(symbol__));
          scan__text = scan__text_start;  /* scrub from text buffer */
        }
        else
          scan__sym->token = P_NEW_ID;
      }
      else if (isdigit(*next_char))
      {                         /* read a number */
        scan__sym->token = P_NUMBER;
        scan__sym->number = 0;  /* initialise new number value */
        while (isdigit(*next_char))
        {                       /* just accept decimal digits */
          int oldnumber = scan__sym->number;  /* remember last number */

          scan__sym->number = oldnumber * 10 + *next_char - '0';
          scan__get_char();
          if (scan__sym->number < oldnumber)
            scan__error_echo("Number too large: wrapping");
          scan__text = scan__text_start;  /* scrub from text buffer */
        }
      }
			else if (*next_char == '$' && scan__flags.hex_dollar) /* hexadecimal number */
      {
        scan__sym->token = P_NUMBER;
        scan__sym->number = 0;  /* initialise new number value */
        scan__get_char();       /* skip $ sign */
        while (isdigit(*next_char) | isxdigit(*next_char))
        {
          int oldnumber = scan__sym->number;  /* remember last number */

          scan__sym->number = (oldnumber << 4) +
            (isdigit(*next_char) ?
             (*next_char - '0') :
             (toupper(*next_char) - 'A' + 10));
          scan__get_char();
          if (scan__sym->number < oldnumber)
            scan__error_echo("Number too large: wrapping");
          scan__text = scan__text_start;  /* scrub from text buffer */
        }
      }
      else
      {                         /* process non-alphanumeric symbol */
        if (*next_char == EOF_CHAR)
        {
          scan__text = scan__text_start;  /* scrub from text buffer */
          scan__close();        /* shut down this file */
        }
        else if (*next_char == '\n')
        {
          scan__text = scan__text_start;  /* scrub from text buffer */
          scan__sym->token = P_EOLN;
          scan__get_char();
        }
        else
        {
          char *start = scan__text;
          symbol__ *last_sym,
          *this_sym = NULL;

          while (last_sym = this_sym, scan__insert_char(*next_char), *scan__text = '\0',
                 (this_sym = symbol__lookup_id(start)) != NULL
            )
            scan__get_char();

          if (scan__text == start + 1)
          {
            next_char--;        /* push error pointer forward one slot */
            scan__error_echo("Unexpected character in source file");
            next_char++;        /* restore pointer */
            scan__text = scan__text_start;  /* scrub from text buffer */
            scan__sym->token = P_BAD;
            scan__get_char();
          }
          else
            memcpy(scan__sym, last_sym, sizeof(symbol__));

          scan__text = start;   /* discard token from text buffer */

          if (scan__sym->data != NULL)  /* must be extended keyword */
          {
            struct extended_keyword_data *e = (struct extended_keyword_data *) (scan__sym->data);
            int nestable = 0,
             nestlevel = 1;

            /* add a header byte */
            scan__insert_char(128 + (scan__sym->token % 128));

            switch (e->kind)
            {
            case E_STRING:
              do
              {
                while (*next_char != *(scan__sym->id))
                {
                  if (*next_char == '\n' || *next_char == EOF_CHAR)
                  {
                    scan__error_echo("Unterminated string");
                    break;
                  }
                  scan__insert_char(*next_char);  /* add character to string */
                  scan__get_char(); /* get next character */
                }
                scan__get_char(); /* get character after close */
              }
              while (*next_char == *(scan__sym->id) ?
                     scan__insert_char(*next_char), scan__get_char(), 1
                     : 0);      /* go round again if this is a close quote */
              scan__insert_char(0); /* terminate string */
              break;

            case E_STRING_ESC:
              while (*next_char != *(scan__sym->id))
              {
                if (*next_char == '\n' || *next_char == EOF_CHAR)
                {
                  scan__error_echo("Unterminated string");
                  break;
                }
                if (*next_char == *(e->close))
                  scan__get_char(); /* skip escape character */
                scan__insert_char(*next_char);  /* insert escaped character */
                scan__get_char();
              }
              scan__get_char(); /* skip close character */
              scan__insert_char(0); /* terminate string */
              break;

            case E_COMMENT_LINE:
            case E_COMMENT_LINE_VISIBLE:
              do
              {
                scan__insert_char(*next_char);
                scan__get_char();
              }
              while (*next_char != '\n' && *next_char != EOF_CHAR);
              *scan__text = 0;  /* terminate with a null */
              scan__sym->id = start;
              if (e->kind == E_COMMENT_LINE)
                scan__sym->token = P_IGNORE;
              break;

            case E_COMMENT_NEST:
            case E_COMMENT_NEST_VISIBLE:
              nestable = 1;
            case E_COMMENT_VISIBLE:
            case E_COMMENT:
              do
              {
                scan__insert_char(*next_char);
                scan__get_char();
                if ((*(e->close + 1) == 0 &&
                     *e->close == *(scan__text - 1)
                     ) ||       /* single close */
                    (*(e->close + 1) == *(scan__text - 1) &&
                     *e->close == *(scan__text - 2)
                     )          /* double close */
                  )
                  nestlevel--;
                else if ((*(scan__sym->id + 1) == 0 &&
                          *scan__sym->id == *(scan__text - 1)
                          ) ||  /* single close */
                         (*(scan__sym->id + 1) == *(scan__text - 1) &&
                          *scan__sym->id == *(scan__text - 2)
                          )     /* double close */
                  )
                  nestlevel += nestable;
              }
              while (nestlevel > 0);

              if (*e->close + 1 == 0) /* single character close token */
                scan__text -= 1;/* backup one character */
              else
                scan__text -= 2;/* backup two characters */

              if (e->kind == E_COMMENT || e->kind == E_COMMENT_NEST)
                scan__sym->token = P_IGNORE;
              scan__text = scan__text_start;  /* scrub the comment from text buffer */
              break;

            }
            scan__insert_char(0); /* terminate with a null */
            scan__sym->id = start;
          }
        }
      }
    }
    while (scan__sym->token == P_IGNORE);

  if (scan__flags.symbol_echo)
    scan__print_symbol(scan__sym);
}

/****************************************************************************
*
* Install a keyword in the symbol table
*
****************************************************************************/
symbol__ *scan__load_keyword(const char *token, int value)
{
  symbol__ *s = symbol__insert_id(token);

  s->token = value;

  return s;
}

/****************************************************************************
*
* Install extended keyword in the symbol table
*
****************************************************************************/
void scan__load_extended_data(symbol__ * s, int kind, const char *close)
{
  s->data = crash__malloc(sizeof(struct extended_keyword_data), "extended keyword data");

  ((struct extended_keyword_data *) (s->data))->kind = kind;

  ((struct extended_keyword_data *) (s->data))->close = (char *) close;
}

symbol__ *scan__load_extended_keyword(const char *token, int value, int kind, const char *close)
{
  symbol__ *s = scan__load_keyword(token, value);

  scan__load_extended_data(s, kind, close);

  return (s);
}

/****************************************************************************
*
* Scanner mode setup routines
*
****************************************************************************/
void scan__tab_width(unsigned u)
{
  tabwidth = u;
}

void scan__newline_visible(int i)
{
  scan__flags.newline_visible = i;
}

void scan__case_insensitive(int i)
{
  scan__flags.case_insensitive = i;
}

void scan__line_never_echo(int i)
{
  scan__flags.line_suppress_echo = i;
}

void scan__line_echo(int i)
{
  scan__flags.line_echo = i;
}

void scan__symbol_echo(int i)
{
  scan__flags.symbol_echo = i;
}

void scan__summarise_errors(int i)
{
  scan__flags.summarise_errors = i;
}

void scan__suppress_errors(int i)
{
  scan__flags.suppress_errors = i;
}

void scan__show_skips(int i)
{
  scan__flags.show_skips = i;
}

void scan__hex_dollar(int i)
{
	scan__flags.hex_dollar = i;
}

/****************************************************************************
*
* Initialise scan subsystem
*
****************************************************************************/
/* add  filetype if fname doesn't already have one */
char *scan__default_filetype(const char *fname, const char *ftype)
{
  char *fullname = crash__malloc(strlen(fname) + strlen(ftype) + 2, "file name");

  strcpy(fullname, fname);

  if (strchr(fullname, '.') == NULL)
  {
    strcat(fullname, ".");
    strcat(fullname, ftype);
  }

  return fullname;
}

/* strip the filetype from fname and add ftype */
char *scan__add_filetype(const char *fname, const char *ftype)
{
  char *fullname;
  size_t length;

  length = strcspn(fname, "."); /* find the dot */
  fullname = crash__malloc(length + strlen(ftype) + 2, "file name");

  strncpy(fullname, fname, length);
  fullname[length] = 0;
  strcat(fullname, ".");
  strcat(fullname, ftype);

  return fullname;
}

void scan__init(unsigned max_text, unsigned max_errors, unsigned max_warnings)
{
  maxtext = max_text;           /* set text buffer size */
  maxerrors = max_errors;       /* set maximum number of errors per file */
  maxwarnings = max_warnings;   /* set maximum number of warnings per file */

  text = crash__malloc(maxtext, "scanner text buffer"); /* allocate text buffer */

  scan__sym = symbol__new();    /* make a new symbol for next symbol */
  scan__text = text;            /* top of text character */
  next_char = last_char = first_char = text + maxtext;  /* make new buffer region below top of text */
}

FILE *scan__open_file(char *s)
{
  FILE *handle = fopen(s, "r"); /* try and get the file */

  if (handle != NULL)
  {
    struct source_list *temp = crash__calloc(sizeof(struct source_list), "scanner file node");

    if (current_source != NULL) /* save entire context */
    {
      current_source->first_char = first_char;
      current_source->last_char = last_char;
      current_source->next_char = next_char;
      memcpy(&(current_source->scan__sym), scan__sym, sizeof(symbol__));
    }
    temp->last = current_source;
    current_source = temp;

    current_source->name = s;   /* remember the file's name */
    current_source->file = handle;  /* make new file current */

    if (scan__flags.line_echo)
      scan__info("%s", current_source->name);

    next_char = last_char = first_char = last_char - 1; /* make new buffer region below current line */
    scan__get_char();           /* prime input buffer */
    scan__();                   /* prime next symbol */
  }
  return handle;
}

char *scan__load_file(char *s)
{
  FILE *temp = fopen(scan__default_filetype(s, "bnf"), "r");
  char *start = scan__text;
  int ch;

  if (temp != NULL)
  {
    do
    {
      ch = fgetc(temp);         /* get a character from source file */
      if (ch == EOF)
        ch = EOF_CHAR;          /* convert to eight bits */
      scan__insert_char((char) ch); /* shove in text buffer */
    }
    while (ch != EOF_CHAR);     /* we want the final EOF! */
    fclose(temp);
    return start;
  }
  else
    return NULL;
}

char *scan__open_text(char *s, char *t)
{
  struct source_list *temp = crash__calloc(sizeof(struct source_list), "scanner file node");

  if (t == NULL)
    return NULL;                /* propogate error return */

  if (current_source != NULL)   /* save entire context */
  {
    current_source->first_char = first_char;
    current_source->last_char = last_char;
    current_source->next_char = next_char;
    memcpy(&(current_source->scan__sym), scan__sym, sizeof(symbol__));
  }
  temp->last = current_source;
  current_source = temp;

  current_source->name = s;     /* remember the file's name */
  current_source->text = t;     /* make new text buffer current */
  current_source->is_text = 1;  /* mark as text source */

  if (scan__flags.line_echo)
    scan__info("%s", current_source->name);

  first_char = next_char = t;   /* set next char to start of text */
  if (scan__flags.line_echo)
    scan__echo_line(stdout);
  scan__();                     /* prime next symbol */
  return t;
}

void scan__close(void)
{
  struct source_list *temp = current_source;  /* remember current node for freeing */

  if (current_source == NULL)   /* whoops: programming error */
    crash__("Internal scanner error: attempt to close NULL sourcefile");

  if (scan__flags.summarise_errors)
    scan__print_file_errors(1);

  current_source = current_source->last;  /* pop filename stack */
  fclose(temp->file);           /* close the file */
  free(temp);                   /* give the storage back */

  if (current_source == NULL)
    scan__sym->token = P_EOF;
  else
  {                             /* restore old context */
    first_char = current_source->first_char;  /* unload first_char */
    last_char = current_source->last_char;  /* unload last_char */
    next_char = current_source->next_char;  /* unload next char */
    memcpy(scan__sym, &(current_source->scan__sym), sizeof(symbol__));  /* unload symbol */
    if (scan__flags.line_echo)
    {
      scan__info("%s", current_source->name);
      scan__echo_line(stdout);  /* reecho line in case there are errors */
    }
  }
}

/* Macro hooks */
char *scan__position(void)
{
  return next_char;
}

void scan__goto(char *target)
{
  if (current_source->is_text)
  {
    next_char = target;
    scan__();
  }
  else
    crash__("Attempted a scanner goto in file mode");
}

/* End of scan.c */
