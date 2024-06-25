/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* mvm_aux.h - MVM assembler semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/

typedef struct operandStruct {enum addressModes mode; int val1; int val2;} operand;

extern operand unusedOperand;

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
void emitInstruction(enum opcodes op, operand op1, operand op2, operand op3);
void emit1(unsigned long val);
void emit2(unsigned long val); 

void init(char * outputfilename); 
int quit(char * outputfilename); 
