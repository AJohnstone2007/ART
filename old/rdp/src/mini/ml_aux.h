/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* ml_aux.h - Miniloop one pass compiler semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
void emit_open(char * sourcefilename, char * outfilename); 
void emit(char * asm_op, char * alg_op, char * dst, char * src1, char * src2); 
void emit_print(char kind, char * src); 
void emit_close(void); 
int emitf(const char * fmt, ...); 

char * new_temporary(void); 
unsigned long new_label(void); 

/* End of ml_aux.h */
