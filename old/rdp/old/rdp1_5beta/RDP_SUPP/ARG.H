/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 8 April 1996
*
* arg.h - process command line arguments
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef ARG_H
#define ARG_H

enum arg_kind_type{ARG_BLANK, ARG_BOOLEAN, ARG_NUMERIC, ARG_STRING};

void arg_boolean(char key, char* description, int *intvalue);
void arg_help(char *msg);
void arg_message(char* description);
void arg_numeric(char key, char* description, unsigned long *unsignedvalue);
char * arg_process(int argc, char *argv[]);
void arg_string(char key, char* description, char **str);

#endif

/* End of arg.h */
