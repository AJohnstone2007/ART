/*******************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* rdp_prnt.h - rdp output routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef RDP_PRNT_H
#define RDP_PRNT_H

extern char* rdp__token_string;

void rdp__dump_extended(void * base);
void rdp__print_header(char *headerfilename);
void rdp__make_token_string(void * base);
void rdp__print_parser(char *outputfilename, void * base);
void rdp__print_sub_item(struct rdp__data_node * prod, int expand);

#endif

/* End of rdp_prnt.h */
