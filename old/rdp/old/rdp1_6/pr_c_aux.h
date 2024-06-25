/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* pr_c_aux.h - pretty printer semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
enum kinds
{
  K_BLOCK_CLOSE, K_BLOCK_OPEN, K_CHARACTER, K_CLOSE_BRACKET, K_COMMENT, 
  K_DIADIC, K_EOLN, K_FIELD_DELIM, K_KEYWORD, K_KEYWORD_INDENT, K_ITEM, 
  K_MONADIC, K_OPEN_BRACKET, K_PREPROCESSOR, K_PUNCTUATION, K_STRING, K_TOP
}; 

extern unsigned long indent_size; 
extern unsigned long comment_start; 

void pretty_close(char * sourcefilename, char * outputfilename); 
void pretty_open(char * sourcefilename, char * outputfilename); 
void pretty_print(char * lexeme, enum kinds kind, unsigned long column, unsigned long line); 

/* End of pr_c_aux.h */
