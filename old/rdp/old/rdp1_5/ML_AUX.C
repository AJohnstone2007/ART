/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* ml_aux.c - miniloop one pass compiler semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include "textio.h"
#include "memalloc.h"
#include "ml_aux.h"

FILE * outfile; 

static long unsigned temp_count = 0; 

int emitf(const char * fmt, ...)
{
  int i; 
  va_list ap;                 /* argument list walker */
  
  va_start(ap, fmt);          /* pass parameters to vprintf */
  i = vfprintf(outfile, fmt, ap);  /* remember count of characaters printed */
  va_end(ap);                 /* end of var args block */
  
  return i;                   /* return number of characters printed */
}

void emit_open(char * sourcefilename, char * outfilename)
{
  if ((outfile = fopen(outfilename, "w"))== NULL)
    text_message(TEXT_FATAL, "unable to open output file \'%s\' for writing\n", outfilename); 
  emitf("; %s - generated from \'%s\'\n\n", outfilename, sourcefilename); 
  emitf(" DATA 0x8000\n__MPP_DATA:\n CODE 0x1000\n__MPP_CODE:\n"); 
}

void emit_close(void)
{
  emitf("\n HALT\n\n DATA\n__temp: BLOCKW %lu  ;declare array of temporaries\n\n"
  " END __MPP_CODE\n", temp_count); 
  fclose(outfile); 
}

void emit(char * asm_op, char * alg_op, char * dst, char * src1, char * src2)
{
  emitf(" %s  %s, %s", asm_op, dst, src1); 
  if (src2 != NULL)
    emitf(", %s", src2); 
  
  /* Now output algebraic style */
  emitf(" \t;%s := %s %s", dst, src1, alg_op); 
  if (src2 != NULL)
    emitf(" %s", src2); 
  emitf("\n"); 
}

void emit_print(char kind, char * src)
{
  if (kind == 'S')
  {
    unsigned long label = new_label(); 
    
    emitf("\n DATA\n__STR_%lu: STRING \"", label); 
    text_print_C_string_file(outfile, src); 
    emitf("\"\n\n CODE\n PRTS __STR_%lu\n", label); 
  }
  else
  {
    emitf(" PRTI "); 
    text_print_C_string_file(outfile, src); 
    emitf("\t;print integer\n"); 
  }
}

char * new_temporary(void)
{
  char * ret =(char *) mem_malloc(30); 
  
  sprintf(ret, "__temp + %lu", temp_count++); 
  
  return ret; 
}

unsigned long new_label(void)
{
  static long unsigned label = 0; 
  
  return label++; 
}

/* End of ml_aux.c */
