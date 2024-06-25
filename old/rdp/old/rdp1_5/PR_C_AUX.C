/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* pr_c_aux.c - pretty printer semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdio.h>
#include "scan.h"
#include "textio.h"
#include "pr_c_aux.h"

static int lexeme_count = 0; 
static int eoln_count = 0; 
static int comment_count = 0; 
static int last_line = 1; 
static FILE * outputfile; 
unsigned long indent_size = 2l; 
unsigned long comment_start = 30l; 


static int space_table[K_TOP][K_TOP]= {
/*                                               K
                               C                 E
                               L                 Y        O  P
                      B        O           F     W        P  R  P
                      L  B     S           I     O        E  E  U
                      O  L  C  E           E     R        N  P  N
                      C  O  H  _           L     D        _  R  C
                      K  C  A  B  C        D  K  _     M  B  O  T
                      _  K  R  R  O  D     _  E  I     O  R  C  U  S
                      C  _  A  A  M  I     D  Y  N     N  A  E  A  T
                      L  O  C  C  M  A  E  E  W  D  I  A  C  S  T  R
                      O  P  T  K  E  D  O  L  O  E  T  D  K  S  I  I
                      S  E  E  E  N  I  L  I  R  N  E  I  E  O  O  N
                      E  N  R  T  T  C  N  M  D  T  M  C  T  R  N  G
                      ---------------------------------------------- */
/* BLOCK_CLOSE    */ {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}, 
/* BLOCK_OPEN     */ {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
/* CHARACTER      */ {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
/* CLOSE_BRACKET  */ {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0}, 
/* COMMENT        */ {1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1}, 
/* DIADIC         */ {1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1}, 
/* EOLN           */ {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
/* FIELD_DELIM    */ {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
/* KEYWORD        */ {0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0}, 
/* KEYWORD_INDENT */ {0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0}, 
/* ITEM           */ {0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1}, 
/* MONADIC        */ {1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1}, 
/* OPEN_BRACKET   */ {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
/* PREPROCSSOR    */ {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, 
/* PUNCTUATION    */ {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 
/* STRING         */ {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}
}; 

void pretty_open(char * sourcefilename, char * outputfilename)
{
  if (strcmp(sourcefilename, outputfilename)== 0)
    text_message(TEXT_FATAL, "temporary output filename is the same as the source filename"); 
  
  if (* outputfilename == '-')
    outputfile = stdout; 
  else if ((outputfile = fopen(outputfilename, "w"))== NULL)
    text_message(TEXT_FATAL, "unable to open output file \'%s\'", outputfilename); 
}

void pretty_close(char * sourcefilename, char * outputfilename)
{
  unsigned long useful_lexeme_count = lexeme_count - comment_count - eoln_count; 
  char * backup_filename = text_force_filetype(sourcefilename, "bak"); 
  
  fclose(outputfile); 
  
  remove(backup_filename); 
  
  if (rename(sourcefilename, backup_filename)!= 0)
    text_message(TEXT_FATAL, "unable to rename \'%s\' to \'%s\'\n", sourcefilename, backup_filename); 
  
  if (rename(outputfilename, sourcefilename)!= 0)
    text_message(TEXT_FATAL, "unable to rename \'%s\' to \'%s\'\n", outputfilename, sourcefilename); 
  
  text_printf("%s,%lu,%lu,%.2lf\n", sourcefilename, 
  last_line, 
  useful_lexeme_count, 
  (double) useful_lexeme_count /(double) last_line); 
}

void pretty_print(char * lexeme, enum kinds kind, unsigned long column, unsigned long line)
{
  static int last_kind = K_EOLN; 
  static unsigned long indentation = 0; 
  static int temporary_indent = 0; 
  static int printed = 0; 
  
  lexeme_count++;             /* bump lexeme counter for statistics */
  last_line = line;           /* remember the highest line number seen */
  
  if (kind == K_BLOCK_CLOSE)
    indentation--; 
  else if (last_kind == K_BLOCK_OPEN)
    indentation++; 
  
  if (last_kind == K_EOLN)    /* do indentation */
  {
    unsigned long indent_count, space_count; 
    
    if (temporary_indent && kind != K_BLOCK_OPEN) /* add an indent of we aren't opening a block */
      indentation++; 
    
    for (indent_count = 0; indent_count < indentation; indent_count++)
      if (!((column == 1)&&(kind == K_COMMENT))) /* Don't indent comments that start in column 1 */
      if (indent_size == 0)
    {
      fprintf(outputfile, "\t");  /* indent using a tab */
      printed += text_get_tab_width(); 
    }
    else
      for (space_count = 0; space_count < indent_size; space_count++)
      printed += fprintf(outputfile, " "); 
    
    if (temporary_indent && kind != K_BLOCK_OPEN) /* reset temporary indent */
      indentation--; 
    
    temporary_indent = 0; 
  }
  
  if (space_table[last_kind][kind]) /* insert space if necessary */
    printed += fprintf(outputfile, " "); 
  
  /* Print the lexeme: some kinds need special actions */
  switch (kind)
  {
    case K_EOLN: 
    fprintf(outputfile, "\n"); 
    eoln_count++; 
    printed = 0; 
    break; 
    
    case K_COMMENT: 
    comment_count++; 
    if (last_kind != K_EOLN)  /* comments that aren't first on a line move to middle */
      do
      printed += fprintf(outputfile, " "); 
    while (printed <(int) comment_start); 
      
    printed += fprintf(outputfile, "/*%s*/", lexeme); 
    break; 
    
    case K_STRING: 
    printed += fprintf(outputfile, "\""); 
    printed += text_print_C_string_file(outputfile, lexeme); 
    printed += fprintf(outputfile, "\""); 
    break; 
    
    case K_CHARACTER: 
    printed += fprintf(outputfile, "\'"); 
    printed += text_print_C_char_file(outputfile, lexeme); 
    printed += fprintf(outputfile, "\'"); 
    break; 
    
    case K_PREPROCESSOR: 
    printed += fprintf(outputfile, "#%s", lexeme); 
    break; 
    
    default: 
    printed += fprintf(outputfile, "%s", lexeme); 
    break; 
  }
  
  if (kind == K_KEYWORD_INDENT) /* Set an indent for next line */
    temporary_indent = 1; 
  
  last_kind = kind; 
}
/* End of pr_c_aux.c */

