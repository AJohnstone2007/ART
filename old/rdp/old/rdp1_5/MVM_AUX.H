/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* mvm_aux.h - MVM assembler semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/

extern int emit_code; 
extern int execute_sim; 

extern unsigned long * location; 
extern unsigned long code_location; 
extern unsigned long data_location; 
extern unsigned long transfer; 

extern void * last_label; 
extern void * dummy_label; 

void * current_label(void); 

void emit_transfer(void); 
void emit_eoln(void); 
void emit_fill(void); 
void emit_loc(void); 
void emit_op(int op, unsigned long oper1, unsigned long oper2, unsigned long oper3, int mode1, int mode2, int opers); 
void emit1(unsigned long val); 
void emit2(unsigned long val); 

void init(char * outputfilename); 
int quit(char * outputfilename); 
