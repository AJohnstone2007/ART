/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* rdp_prnt.h - rdp output routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef RDP_PRNT_H
#define RDP_PRNT_H

extern char * rdp_token_string; 

void rdp_dump_extended(void * base); 
void rdp_print_header(char * headerfilename);
void rdp_make_token_string(void * base);
void rdp_print_parser(char * outputfilename, void * base); 
void rdp_print_sub_item(struct rdp_data_node * prod, int expand); 

#endif

/* End of rdp_prnt.h */
