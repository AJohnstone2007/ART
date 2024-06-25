/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* mvm_def.h - Mini Virtual Machine opcode definitions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
enum opcodes{OP_HALT, OP_ADD, OP_SUB, OP_MUL, OP_DIV, OP_EXP, 
  OP_EQ, OP_NE, OP_GT, OP_GE, OP_LT, OP_LE, 
  OP_CPY, 
  OP_BNE, OP_BEQ, OP_BRA,
  OP_PRTS, OP_PRTI,
  OP_CALL, OP_RET,
  OP_PUSH, OP_POP,
  OP_LDSP, OP_STSP};

enum addressModes {MODE_CONSTANT, MODE_VARIABLE, MODE_INDEXED, MODE_SP_INDEXED, MODE_UNUSED};

