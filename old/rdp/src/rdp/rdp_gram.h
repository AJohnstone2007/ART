/*******************************************************************************
*
* RDP release 1.70 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 16 March 2012
*
* rdp_gram.h - rdp grammar checking routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef RDP_GRAM_H
#define RDP_GRAM_H

int rdp_bad_grammar(void * base); 
void rdp_check_eoln(char * id); 
void rdp_check_prod_name_valid(char * id); 
void rdp_check_comment_token_valid(char * id); 
void rdp_check_string_token_valid(char * id); 
void rdp_check_token_valid(char * id); 

#endif

/* End of rdp_gram.h */
