/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* scanner.c - default scanner module
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "memalloc.h"
#include "symbol.h"
#include "textio.h"
#include "scan.h"

extern void * scan__table;

void scan__(void)
{                               /* get the next lexeme */
  char *start = text__top;      /* remember start of symbol */
  scan__data* s;

  memset(text__scan_data, 0, sizeof(scan__data));

  do
  {
    while (text__char != EOF &&
           !(scan__newline_visible && text__char == '\n') &&
           isspace(text__char)
      )
      text__get_char();         /* blank strip */
    if (isalpha(text__char) || text__char == '_')
    {                           /* read an identifier into text buffer */
      SCAN__CAST->id = text__top; /* point to text table */
      while (isalnum(text__char) || text__char == '_')
      {
	if (scan__case_insensitive && text__char >= 'A' && text__char <= 'Z')
	  text__char -= 'A' - 'a';

	text__insert_char(text__char);
	text__get_char();
      }
      text__insert_char('\0');  /* terminate string */
      if ((s = (scan__data*) symbol__lookup_key(scan__table, SCAN__CAST->id)) != NULL)
      {
	memcpy(text__scan_data, s, sizeof(scan__data));
	text__top = start;      /* scrub from text buffer */
      }
      else
	SCAN__CAST->token = T__ID;
    }                           /* end of ID collection */
    else if (isdigit(text__char))
    {                           /* read a number of some sort */
      int hex = 0;

      SCAN__CAST->id = text__top; /* remember start position */
      SCAN__CAST->token = T__INTEGER; /* assume integer */

      /* Check for hexadecimal introducer */
      if (text__char == '0')
      {
        text__insert_char(text__char);
        text__get_char();
        if (text__char == 'x' || text__char == 'X')
        {
          hex = 1;
          text__insert_char(text__char);
          text__get_char();
        }
      }

      /* Now collect decimal or dex digits */
      while (hex ? isxdigit(text__char) : isdigit(text__char))
      {
        text__insert_char(text__char);
        text__get_char();
      }

      if (!hex)                 /* check for decimal part and exponent */
      {
        /* get decimal */
        if (text__char == '.' /* && *(text__char - 1) != '.' *//* Pascal sneakiness */ )
        {
	  SCAN__CAST->token = T__REAL;
          do
          {
            text__insert_char(text__char);
            text__get_char();
	  }
          while (isdigit(text__char));
        }

        /* get exponent */
        if (text__char == 'E' || text__char == 'e')
        {
	  SCAN__CAST->token = T__REAL;
          text__insert_char(text__char);
          text__get_char();
          if (text__char == '+' || text__char == '-' || isdigit(text__char))
            do
            {
              text__insert_char(text__char);
              text__get_char();
            }
            while (isdigit(text__char));
        }
      }
      text__insert_char('\0');  /* terminate string */
      if (SCAN__CAST->token == T__INTEGER)
	SCAN__CAST->data.u = strtoul(SCAN__CAST->id, NULL, 0);
      else
	SCAN__CAST->data.r = strtod(SCAN__CAST->id, NULL);
    }                           /* end of number collection */
    else
    {                           /* process non-alphanumeric symbol */
      if (text__char == EOF)
      {
	SCAN__CAST->token = T__EOF;
        text__top = start;      /* scrub from text buffer */
      }
      else if (text__char == '\n')
      {
        text__top = start;      /* scrub from text buffer */
	SCAN__CAST->token = T__EOLN;
        text__get_char();
      }
      else
      {
        char *start = text__top;
	scan__data *last_sym,
        *this_sym = NULL;

        while (last_sym = this_sym, text__insert_char(text__char), *text__top = '\0',
	       (this_sym = (scan__data*) symbol__lookup_key(scan__table, start)) != NULL
          )
          text__get_char();     /* collect longest match */

        if (text__top == start + 1) /* single character means mismatch */
        {
          text__message(TEXT__ERROR_ECHO, "Unexpected character 0x%.2X '%c' in source file\n", *(text__top - 1), isprint(*(text__top - 1)) ? *(text__top - 1) : ' ');
          text__top = start;    /* scrub from text buffer */
	  SCAN__CAST->token = T__IGNORE;
          text__get_char();
        }
        else
	  memcpy(text__scan_data, last_sym, sizeof(scan__data)); /* set up SCAN__CAST */

        text__top = start;      /* discard token from text buffer */

      }
    }

    /* Now process extended tokens */
    if (SCAN__CAST->id != NULL) 
    {
      int nestable = 0;
      unsigned nestlevel = 1;
      char *close = SCAN__CAST->id;

      while (*close++ != 0)     /* find string after the ID in the prototype token */
        ;

      switch (SCAN__CAST->token)
      {
      case T__ISTRING:
        do
        {
	  while (text__char != *(SCAN__CAST->id))
          {
            if (text__char == '\n' || text__char == EOF)
            {
              text__message(TEXT__ERROR_ECHO, "Unterminated string\n");
              break;
            }
            text__insert_char(text__char);  /* add character to string */
            text__get_char();   /* get next character */
          }
          text__get_char();     /* get character after close */
        }
	while (text__char == *(SCAN__CAST->id) ?
               text__insert_char(text__char), text__get_char(), 1
               : 0);            /* go round again if this is a close quote */
        text__insert_char(0);   /* terminate string */
	SCAN__CAST->id = start;   /* make current id string body */
	break;

      case T__ISTRING_ESC:
	while (text__char != *(SCAN__CAST->id))
        {
          if (text__char == '\n' || text__char == EOF)
          {
            text__message(TEXT__ERROR_ECHO, "Unterminated string\n");
            break;
          }
          else if (text__char == *close)  /* found escape character */
          {
            long int temp;
            char *start;

            /* translate all C escapes. Anything else returns escaped character */
            text__get_char();   /* skip escape character */
            switch (text__char)
            {
            case 'n':
              text__insert_char('\n');
              text__get_char();
              break;
            case 't':
              text__insert_char('\t');
              text__get_char();
              break;
            case 'v':
              text__insert_char('\v');
              text__get_char();
              break;
            case 'b':
              text__insert_char('\b');
	      text__get_char();
              break;
            case 'r':
              text__insert_char('\r');
              text__get_char();
              break;
            case 'f':
              text__insert_char('\f');
              text__get_char();
              break;
            case 'a':
              text__insert_char('\a');
              text__get_char();
              break;
            case 'x':
            case 'X':           /* hexadecimal */
              start = text__top;
              do
              {
                text__get_char();
                text__insert_char(text__char);
              }
              while (isxdigit(text__char));
              text__top = 0;    /* change last character to a null */
              temp = strtol(start, NULL, 16);
              text__top = start;/* scrub from buffer */
              if (temp > 255)
                text__message(TEXT__WARNING_ECHO, "Hex escape sequence overflows eight bits: wrapping\n");
              text__insert_char((char) (temp % 255));
              break;
            case '0':
            case '1':
            case '2':
	    case '3':
            case '4':
            case '5':
            case '6':
            case '7':           /* octal */
              start = text__top;
              do
              {
                text__insert_char(text__char);
                text__get_char();
              }
              while (text__char >= '0' && text__char <= '7');
              text__top = 0;    /* change last character to a null */
              temp = strtol(start, NULL, 8);
              text__top = start;/* scrub from buffer */
              if (temp > 255)
                text__message(TEXT__WARNING_ECHO, "Octal escape sequence overflows eight bits: wrapping\n");
              text__insert_char((char) (temp % 255));
              break;
            default:            /* any other quoted character returns itself */
              text__insert_char(text__char);  /* insert ordinary character */
              text__get_char();
              break;
            }
          }
          else
          {                     /* ordinary character */
            text__insert_char(text__char);  /* insert ordinary character */
            text__get_char();
          }
        }
        text__get_char();       /* skip close character */
        text__insert_char(0);   /* terminate string */
	SCAN__CAST->id = start;   /* make current id string body */
        break;

      case T__ICOMMENT_LINE:
      case T__ICOMMENT_LINE_VISIBLE:
        do
        {
          text__insert_char(text__char);
          text__get_char();
        }
        while (text__char != '\n' && text__char != EOF);
        *text__top = 0;         /* terminate with a null */
	SCAN__CAST->id = start;   /* make current id comment body */
	if (SCAN__CAST->token == T__ICOMMENT_LINE)
	  SCAN__CAST->token = T__IGNORE;
        break;

      case T__ICOMMENT_NEST:
      case T__ICOMMENT_NEST_VISIBLE:
        nestable = 1;
      case T__ICOMMENT_VISIBLE:
      case T__ICOMMENT:
        do
        {
          text__insert_char(text__char);
          text__get_char();
          if ((*(close + 1) == 0 &&
               *close == *(text__top - 1)
               ) ||             /* single close */
              (*(close + 1) == *(text__top - 1) &&
               *close == *(text__top - 2)
               )                /* double close */
            )
	    nestlevel--;
	  else if ((*(SCAN__CAST->id + 1) == 0 &&
		    *SCAN__CAST->id == *(text__top - 1)
                    ) ||        /* single close */
		   (*(SCAN__CAST->id + 1) == *(text__top - 1) &&
		    *SCAN__CAST->id == *(text__top - 2)
                    )           /* double close */
            )
            nestlevel += nestable;
        }
        while (nestlevel > 0);

        if (*(close + 1) != 0)  /* two character close token */
          text__top--;          /* backup one extra character */

	*(text__top-1) = 0;       /* backup over close and terminate with a null */
	SCAN__CAST->id = start;   /* make current id comment body */

	if (SCAN__CAST->token == T__ICOMMENT || SCAN__CAST->token == T__ICOMMENT_NEST)
        {
	  SCAN__CAST->token = T__IGNORE;
          text__top = start;    /* scrub the comment from text buffer */
        }
        break;

      default:
        break;                  /* do nothing */
      }
    }
  }
  while (SCAN__CAST->token == T__IGNORE);

  if (scan__symbol_echo)
    text__message(TEXT__INFO, "Scanned %3u  id '%s'\n", SCAN__CAST->token, SCAN__CAST->id);
}
