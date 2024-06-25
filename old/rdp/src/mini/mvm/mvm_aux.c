/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* mvm_aux.c - Mini Virtual Machine assembler semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include "scan.h"
#include "memalloc.h"
#include "textio.h"
#include "mvmasm.h"
#include "mvm_def.h"
#include "mvm_aux.h"

int emit_code = 0;
int execute_sim = 0;

operand unusedOperand = {MODE_UNUSED, 0, 0};

static FILE * objfile = NULL;

unsigned long * location; 
unsigned long data_location; 
unsigned long code_location; 
unsigned long transfer = 0; 

void * last_label = NULL;     /* pointer to most recently seen label: NULLed at start of each line */
void * dummy_label = NULL;    /* dummy symbol returned by current label on error */

static int emitted = 0;       /* Count of bytes emitted this line */

static int emitf(char * fmt, ...) /* conditional print to object file */
{
  int i; 
  va_list ap;                 /* argument list walker */
  
  va_start(ap, fmt); 
  
  if (emit_code)              /* no-op if not emitting... */
  {
    if (emitted < 16 && text_get_echo())
      i = vprintf(fmt, ap); 
    vfprintf(objfile, fmt, ap);  /* ... otherwise pass to fprintf() */
  }

  va_end(ap); 
  
  return(i);                  /* for completeness, although not used here */
}

void emit_eoln(void)
{
  if (emit_code)
    fprintf(objfile, "\n"); 
}

void emit_transfer(void)
{
  if (emit_code)
    emitted += emitf("*%.4lX", transfer); 
}

void emit_loc(void)
{
  emitted = 0; 
  emitf("%.4lX ", * location); 
}

void emit_fill(void)
{
  if (text_get_echo())
  {
    while (emitted++ < 16) printf(" "); 
      printf(" "); 
  }
}

void emitOperand(operand oper)
{
  switch (oper.mode) {
    case MODE_SP_INDEXED:
    case MODE_VARIABLE:
    case MODE_CONSTANT: emit2(oper.val1); break;
    case MODE_INDEXED: emit2(oper.val1); emit2(oper.val2); break;
    case MODE_UNUSED: return;
    default: text_message(TEXT_FATAL, "illegal operand mode\n");
  }
}

void emitInstruction(enum opcodes op, operand oper1, operand oper2, operand oper3)
{
  emit1((unsigned long) op);  /* output opcode */
  emit1((unsigned long)(((oper3.mode & 3) << 4) | ((oper2.mode & 3) << 2) | (oper1.mode & 3)));  /* output addressing modes */
  emitOperand(oper1);
  emitOperand(oper2);
  emitOperand(oper3);
}

void emit1(unsigned long val)
{
  emitted += emitf("%.2lX", val); 
  (* location)++; 
}

void emit2(unsigned long val)
{
  emitted += emitf("%.4lX", val); 
  (* location)+= 2; 
}

void * current_label(void)    /* check that there is a valid label on this line */
{
  if (last_label == NULL)
  {
    text_message(TEXT_ERROR_ECHO, "Missing label on directive\n"); 
    return & dummy_label; 
  }
  else
    return last_label; 
}

void init(char * outputfilename)
{
  if (* outputfilename == '-')
    objfile = stdout; 
  else if ((objfile = fopen(outputfilename, "w"))== NULL)
    text_message(TEXT_FATAL, "Unable to open object file"); 
}

int quit(char * outputfilename)
{
  fclose(objfile); 
  
  text_message(TEXT_INFO, "Transfer address %.8lX\n", transfer); 
  
  if (execute_sim && * outputfilename != '-')
  {
    #define COMMAND "mvmsim -t -v "
    char * command =(char *) mem_calloc(1, strlen(outputfilename)+ strlen(COMMAND)+ 1); 
    
    command = strcat(command, COMMAND);
    command = strcat(command, outputfilename); 
    
    text_message(TEXT_INFO, "Calling simulator: %s \n", command); 
    
    if (system(command)!= 0)
      text_message(TEXT_FATAL, "Not enough memory or simulator not found\n"); 
  }
  
  return 0; 
}

