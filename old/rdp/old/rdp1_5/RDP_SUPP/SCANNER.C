/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* scanner.c - default scanner module
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "memalloc.h"
#include "symbol.h"
#include "textio.h"
#include "scan.h"

extern void * scan_table; 
static last_line_number = 0; 
static last_column = 0; 

unsigned long scan_column_number(void)
{
  return last_column; 
}

unsigned long scan_line_number(void)
{
  return last_line_number; 
}

void scan_(void)
{                             /* get the next lexeme */
  char * start = text_top;    /* remember start of symbol */
  scan_data * s; 
  int nestable; 
  unsigned nestlevel; 
  char * close; 
  int last = ' '; 
  
  do
  {
    memset(text_scan_data, 0, sizeof(scan_data)); 
    
    SCAN_CAST->extended = SCAN_P_IGNORE;  /* Don't do extendeds for non scanner table items */
    while (text_char != EOF && !(scan_newline_visible && text_char == '\n')&& isspace(text_char))
      text_get_char();        /* blank strip */
    
    if (SCAN_CAST->token != 0) /* Non zero means a token was restored at EOF */
      break; 
    
    last_column = text_column_number(); 
    last_line_number = text_line_number(); 
    
    if (isalpha(text_char)|| text_char == '_')
    {                         /* read an identifier into text buffer */
      SCAN_CAST->id = text_top;  /* point to text table */
      while (isalnum(text_char)|| text_char == '_')
      {
        if (scan_case_insensitive && text_char >= 'A' && text_char <= 'Z')
          text_char -= 'A' - 'a'; 
        
        text_insert_char((char) text_char); 
        text_get_char(); 
      }
      text_insert_char('\0');  /* terminate string */
      if ((s =(scan_data *) symbol_lookup_key(scan_table, &(SCAN_CAST->id), NULL))!= NULL)
      {
        memcpy(text_scan_data, s, sizeof(scan_data)); 
        text_top = start;     /* scrub from text buffer */
      }
      else
        SCAN_CAST->token = SCAN_P_ID; 
    }                         /* end of ID collection */
    else if (isdigit(text_char))
    {                         /* read a number of some sort */
      int hex = 0; 
      char * bug; 
      
      SCAN_CAST->id = text_top;  /* remember start position */
      SCAN_CAST->token = SCAN_P_INTEGER;  /* assume integer */
      
      /* Check for hexadecimal introducer */
      if (text_char == '0')
      {
        text_insert_char((char) text_char); 
        text_get_char(); 
        if (text_char == 'x' || text_char == 'X')
        {
          hex = 1; 
          text_insert_char((char) text_char); 
          text_get_char(); 
        }
      }
      
      /* Now collect decimal or hex digits */
      while ((hex ? isxdigit(text_char): isdigit(text_char))|| text_char == '_')
      {
        if (text_char != '_') /* suppress underscores */
          text_insert_char((char) text_char); 
        text_get_char(); 
      }
      
      if (!hex)               /* check for decimal part and exponent */
      {
        /* get decimal */
        if (text_char == '.' && isdigit(*(text_current - 1)) /* lookahead! */)
        {
          SCAN_CAST->token = SCAN_P_REAL; 
          do
          {
            text_insert_char((char) text_char); 
            text_get_char(); 
          }
          while (isdigit(text_char)); 
          }
        
        /* get exponent */
        if (text_char == 'E' || text_char == 'e')
        {
          SCAN_CAST->token = SCAN_P_REAL; 
          text_insert_char((char) text_char); 
          text_get_char(); 
          if (text_char == '+' || text_char == '-' || isdigit(text_char))
            do
          {
            text_insert_char((char) text_char); 
            text_get_char(); 
          }
          while (isdigit(text_char)); 
          }
      }
      
      /* Now absorb any letters that are attached to the number */
      while (isalpha(text_char))
      {
        text_insert_char((char) text_char); 
        text_get_char(); 
      }
      
      text_insert_char('\0');  /* terminate string */
      
      /* There's a bug in the Borland strtoul() which is triggered by leading zeroes: so skip over them */
      
      bug = SCAN_CAST->id; 
      if (!hex)
        while (* bug == '0')
        bug++; 
      
      if (SCAN_CAST->token == SCAN_P_INTEGER)
        SCAN_CAST->data.i = strtoul(bug, NULL, 0); 
      else
        SCAN_CAST->data.r = strtod(SCAN_CAST->id, NULL); 
      
      /* text_top = start; */  /* scrub from text buffer */
    }                         /* end of number collection */
    else
    {                         /* process non-alphanumeric symbol */
      if (text_char == EOF)
      {
        SCAN_CAST->token = SCAN_P_EOF; 
        SCAN_CAST->id = "EOF"; 
        text_top = start;     /* scrub from text buffer */
      }
      else if (text_char == '\n')
      {
        text_top = start;     /* scrub from text buffer */
        SCAN_CAST->token = SCAN_P_EOLN; 
        SCAN_CAST->id = "EOLN"; 
        text_get_char(); 
      }
      else
      {
        char * start = text_top; 
        scan_data * last_sym, 
        * this_sym = NULL; 
        
        while (last_sym = this_sym, text_insert_char((char) text_char), * text_top = '\0', 
          (this_sym =(scan_data *) symbol_lookup_key(scan_table, & start, NULL))!= NULL
        )
        text_get_char();      /* collect longest match */
        
        if (text_top == start + 1) /* single character means mismatch */
        {
          text_message(TEXT_ERROR_ECHO, "Unexpected character 0x%.2X \'%c\' in source file\n", *(text_top - 1), isprint(*(text_top - 1))? *(text_top - 1): ' '); 
          text_top = start;   /* scrub from text buffer */
          SCAN_CAST->token = SCAN_P_IGNORE; 
          text_get_char(); 
        }
        else
          memcpy(text_scan_data, last_sym, sizeof(scan_data));  /* set up SCAN_CAST */
        
        text_top = start;     /* discard token from text buffer */
        
      }
    }
    
    /* Now do extended tokens */
    
    if (SCAN_CAST->extended == SCAN_P_IGNORE)
      continue; 
    
    close = SCAN_CAST->id; 
    nestlevel = 1; 
    nestable = 0; 
    while (* close++ != 0)    /* find string after the ID in the prototype token */
      ; 
    
    switch (SCAN_CAST->extended)
    {
      case SCAN_P_CHAR: 
      text_insert_char((char) text_char);  /* insert character in string table */
      text_insert_char(0);    /* terminate string */
      text_get_char(); 
      SCAN_CAST->id = start; 
/*      SCAN_CAST->token = SCAN_CAST->extended;*/
      break; 
      
      case SCAN_P_CHAR_ESC: 
      if (text_char == * close) /* found escape character */
      {
        long int temp; 
        char * start; 
        
        /* translate all C escapes. Anything else returns escaped character */
        text_get_char();      /* skip escape character */
        switch (text_char)
        {
          case'n': 
          text_insert_char('\n'); 
          text_get_char(); 
          break; 
          case't': 
          text_insert_char('\t'); 
          text_get_char(); 
          break; 
          case'v': 
          text_insert_char('\v'); 
          text_get_char(); 
          break; 
          case'b': 
          text_insert_char('\b'); 
          text_get_char(); 
          break; 
          case'r': 
          text_insert_char('\r'); 
          text_get_char(); 
          break; 
          case'f': 
          text_insert_char('\f'); 
          text_get_char(); 
          break; 
          case'a': 
          text_insert_char('\a'); 
          text_get_char(); 
          break; 
          case'x': 
          case'X':            /* hexadecimal */
          start = text_top; 
          do
          {
            text_get_char(); 
            text_insert_char((char) text_char); 
          }
          while (isxdigit(text_char)); 
            text_top = 0;     /* change last character to a null */
          temp = strtol(start, NULL, 16); 
          text_top = start;   /* scrub from buffer */
          if (temp > 255)
            text_message(TEXT_WARNING_ECHO, "Hex escape sequence overflows eight bits: wrapping\n"); 
          text_insert_char((char)(temp % 255)); 
          break; 
          case'0': 
          case'1': 
          case'2': 
          case'3': 
          case'4': 
          case'5': 
          case'6': 
          case'7':            /* octal */
          start = text_top; 
          do
          {
            text_insert_char((char) text_char); 
            text_get_char(); 
          }
          while (text_char >= '0' && text_char <= '7'); 
            text_top = 0;     /* change last character to a null */
          temp = strtol(start, NULL, 8); 
          text_top = start;   /* scrub from buffer */
          if (temp > 255)
            text_message(TEXT_WARNING_ECHO, "Octal escape sequence overflows eight bits: wrapping\n"); 
          text_insert_char((char)(temp % 255)); 
          break; 
          default:            /* any other quoted character returns itself */
          text_insert_char((char) text_char);  /* insert ordinary character */
          text_get_char(); 
          break; 
        }
      }
      else
      {
        text_insert_char((char) text_char);  /* just insert character in string table */
        text_insert_char(0); 
        text_get_char(); 
      }
      text_insert_char(0); 
      SCAN_CAST->id = start; 
/*      SCAN_CAST->token = SCAN_CAST->extended;*/
      break; 
      
      case SCAN_P_STRING: 
      do
      {
        while (text_char != *(SCAN_CAST->id))
        {
          if (text_char == '\n' || text_char == EOF)
          {
            text_message(TEXT_ERROR_ECHO, "Unterminated string\n"); 
            break; 
          }
          text_insert_char((char) text_char);  /* add character to string */
          text_get_char();    /* get next character */
        }
        text_get_char();      /* get character after close */
      }
      while (text_char == *(SCAN_CAST->id)?
        text_insert_char((char) text_char), text_get_char(), 1
      : 0);                   /* go round again if this is a close quote */
      text_insert_char(0);    /* terminate string */
      SCAN_CAST->id = start;  /* make current id string body */
/*      SCAN_CAST->token = SCAN_CAST->extended;*/
      break; 
      
      case SCAN_P_STRING_ESC: 
      while (text_char != *(SCAN_CAST->id))
      {
        if (text_char == '\n' || text_char == EOF)
        {
          text_message(TEXT_ERROR_ECHO, "Unterminated string\n"); 
          break; 
        }
        else if (text_char == * close) /* found escape character */
        {
          long int temp; 
          char * start; 
          
          /* translate all C escapes. Anything else returns escaped character */
          text_get_char();    /* skip escape character */
          switch (text_char)
          {
            case'n': 
            text_insert_char('\n'); 
            text_get_char(); 
            break; 
            case't': 
            text_insert_char('\t'); 
            text_get_char(); 
            break; 
            case'v': 
            text_insert_char('\v'); 
            text_get_char(); 
            break; 
            case'b': 
            text_insert_char('\b'); 
            text_get_char(); 
            break; 
            case'r': 
            text_insert_char('\r'); 
            text_get_char(); 
            break; 
            case'f': 
            text_insert_char('\f'); 
            text_get_char(); 
            break; 
            case'a': 
            text_insert_char('\a'); 
            text_get_char(); 
            break; 
            case'x': 
            case'X':          /* hexadecimal */
            start = text_top; 
            do
            {
              text_get_char(); 
              text_insert_char((char) text_char); 
            }
            while (isxdigit(text_char)); 
              text_top = 0;   /* change last character to a null */
            temp = strtol(start, NULL, 16); 
            text_top = start;  /* scrub from buffer */
            if (temp > 255)
              text_message(TEXT_WARNING_ECHO, "Hex escape sequence overflows eight bits: wrapping\n"); 
            text_insert_char((char)(temp % 255)); 
            break; 
            case'0': 
            case'1': 
            case'2': 
            case'3': 
            case'4': 
            case'5': 
            case'6': 
            case'7':          /* octal */
            start = text_top; 
            do
            {
              text_insert_char((char) text_char); 
              text_get_char(); 
            }
            while (text_char >= '0' && text_char <= '7'); 
              text_top = 0;   /* change last character to a null */
            temp = strtol(start, NULL, 8); 
            text_top = start;  /* scrub from buffer */
            if (temp > 255)
              text_message(TEXT_WARNING_ECHO, "Octal escape sequence overflows eight bits: wrapping\n"); 
            text_insert_char((char)(temp % 255)); 
            break; 
            default:          /* any other quoted character returns itself */
            text_insert_char((char) text_char);  /* insert ordinary character */
            text_get_char(); 
            break; 
          }
        }
        else
        {                     /* ordinary character */
          text_insert_char((char) text_char);  /* insert ordinary character */
          text_get_char(); 
        }
      }
      text_get_char();        /* skip close character */
      text_insert_char(0);    /* terminate string */
      SCAN_CAST->id = start;  /* make current id string body */
/*      SCAN_CAST->token = SCAN_CAST->extended;*/
      break; 
      
      case SCAN_P_COMMENT_LINE: 
      case SCAN_P_COMMENT_LINE_VISIBLE: 
      while (text_char != '\n' && text_char != EOF)
      {
        text_insert_char((char) text_char); 
        text_get_char(); 
      }
      text_insert_char(0);    /* terminate with a null */
      SCAN_CAST->id = start;  /* make current id comment body */
      if (SCAN_CAST->extended == SCAN_P_COMMENT_LINE)
        SCAN_CAST->token = SCAN_P_IGNORE; 
      break; 
      
      case SCAN_P_COMMENT_NEST: 
      case SCAN_P_COMMENT_NEST_VISIBLE: 
      nestable = 1; 
      case SCAN_P_COMMENT_VISIBLE: 
      case SCAN_P_COMMENT: 
      /* We have to be a bit careful here: remember that the text_get_char()
         routine puts a space in at the start of each line to delay echoing
         of the line in the assembler */
      do
      {
        if (text_char == EOF)
          text_message(TEXT_FATAL_ECHO, "Comment terminated by end of file\n"); 
        
        if (last != '\n') text_insert_char((char) text_char); 
          
        last = text_char; 
        text_get_char(); 
        if ((*(close + 1)== 0 && * close == *(text_top - 1))||  /* single close */
          (*(close + 1)== *(text_top - 1)&& * close == *(text_top - 2)) /* double close */
        )
        nestlevel--; 
        else if ((*(SCAN_CAST->id + 1)== 0 &&
          * SCAN_CAST->id == *(text_top - 1)
        )||                   /* single close */
        (*(SCAN_CAST->id + 1)== *(text_top - 1)&&
        * SCAN_CAST->id == *(text_top - 2)
        )                     /* double close */
        )
        nestlevel += nestable; 
      }
      while (nestlevel > 0); 
        
      if (*(close + 1)!= 0)   /* two character close token */
        text_top--;           /* backup one extra character */
      
      *(text_top - 1)= 0;     /* backup over close and terminate with a null */
      SCAN_CAST->id = start;  /* make current id comment body */
      
      if (SCAN_CAST->extended == SCAN_P_COMMENT || SCAN_CAST->extended == SCAN_P_COMMENT_NEST)
      {
        SCAN_CAST->token = SCAN_P_IGNORE; 
        text_top = start;     /* scrub the comment from text buffer */
      }
      else
        /* SCAN_CAST->token = SCAN_CAST->extended */ ; 
      break; 
      
      default: 
      break;                  /* do nothing */
    }
  }
  while (SCAN_CAST->token == SCAN_P_IGNORE); 
    
  if (scan_symbol_echo)
  {
    text_message(TEXT_INFO, "Scanned "); 
    set_print_element(SCAN_CAST->token, scan_token_names); 
    text_printf(" id \'%s\'\n", SCAN_CAST->id); 
  }
}
