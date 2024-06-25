/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* arg.h - process command line arguments
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#ifndef ARG_H
#define ARG_H

enum arg_kind_type{ARG_BLANK, ARG_BOOLEAN, ARG_NUMERIC, ARG_STRING}; 

void arg_boolean(const char key, const char * description, int * intvalue); 
void arg_help(const char * msg); 
void arg_message(const char * description); 
void arg_numeric(const char key, const char * description, unsigned long * unsignedvalue); 
const char * * arg_process(int argc, const char * argv[]); 
void arg_string(const char key, const char * description, const char * * str); 

#endif

/* End of arg.h */
