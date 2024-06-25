/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* arg.c - command line argument processing routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdlib.h>
#include "arg.h"
#include "memalloc.h"
#include "textio.h"

static struct arg_data
{
  enum arg_kind_type kind; 
  char key; 
  char * description; 
  int * intvalue; 
  unsigned long * unsignedvalue; 
  char * * str; 
  struct arg_data * next; 
} * base; 

static void add_node(enum arg_kind_type kind, 
char key, 
char * description, 
int * intvalue, 
unsigned long * unsignedvalue, 
char * * str)
{
  struct arg_data * temp; 
  
  /* Create and load argument block */
  temp =(struct arg_data *) mem_calloc(1, sizeof(struct arg_data)); 
  temp->kind = kind; 
  temp->key = key; 
  temp->description = description; 
  temp->intvalue = intvalue; 
  temp->unsignedvalue = unsignedvalue; 
  temp->str = str; 
  temp->next = base; 
  base = temp; 
  
}

void arg_message(char * description)
{
  add_node(ARG_BLANK, '\0', description, 0, 0, NULL); 
}

void arg_boolean(char key, char * description, int * intvalue)
{
  add_node(ARG_BOOLEAN, key, description, intvalue, 0, NULL); 
}

void arg_numeric(char key, char * description, unsigned long * unsignedvalue)
{
  add_node(ARG_NUMERIC, key, description, 0, unsignedvalue, NULL); 
}

void arg_string(char key, char * description, char * * str)
{
  add_node(ARG_STRING, key, description, 0, 0, str); 
}

/* return an array of filename strings */
char * * arg_process(int argc, char * argv[])
{
  char * * ret =(char * *) mem_calloc(argc, sizeof(char *)); 
  /* There can't be more than argc file descriptors! */
  int file_count = 0; 
  
  while (--argc > 0)
  {
    if ((* ++argv)[0]== '-')  /* switch */
    {
      struct arg_data * temp = base; 
      
      while (temp->next != NULL && temp->key !=(* argv)[1])
        temp = temp->next; 
      
      if (temp->key !=(* argv)[1])
        arg_help("unknown command line argument"); 
      
      switch (temp->kind)
      {
        case ARG_BOOLEAN: 
        *(temp->intvalue)^= 1; 
        break; 
        case ARG_NUMERIC: 
        sscanf(* argv + 2, "%lu", temp->unsignedvalue); 
        break; 
        case ARG_STRING: 
        *(temp->str)= * argv + 2; ; 
        break; 
        default: 
        break; 
      }
    }
    else
      ret[file_count++]= * argv; 
  }
  return ret; 
}

static void arg_print(struct arg_data * p)
{
  if (p == NULL)
    return; 
  
  arg_print(p->next); 
  if (p->kind != ARG_BLANK)
    printf("-%c %s ", p->key, p->kind == ARG_NUMERIC ? "<n>": p->kind == ARG_STRING ? "<s>": "   "); 
  printf("%s\n", p->description); 
}

void arg_help(char * msg)
{
  printf("\n\nFatal - %s\n\n", msg == NULL ? "": msg); 
  arg_print(base); 
  exit(EXIT_FAILURE); 
}

